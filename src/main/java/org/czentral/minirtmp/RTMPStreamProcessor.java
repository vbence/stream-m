/*
 * Copyright 2018 Bence Varga
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
 * OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.czentral.minirtmp;

import java.util.HashMap;
import java.util.Map;
import org.czentral.incubator.streamm.HexDump;
import org.czentral.util.stream.Processor;

class RTMPStreamProcessor implements Processor {
    
    protected boolean finished = false;
    
    protected Map<Integer, MessageInfo> lastMessages = new HashMap<Integer, MessageInfo>();
    
    protected int chunkSize = 128;
    
    protected ResourceLimit limit;
    
    protected ChunkProcessor chunkProcessor;

    public RTMPStreamProcessor(ResourceLimit limit, ChunkProcessor chunkProcessor) {
        this.limit = limit;
        this.chunkProcessor = chunkProcessor;
    }

    protected void setChunkSize(int newChunkSize) {
        chunkSize = newChunkSize;
    }

    /**
     * Get the value of limit
     *
     * @return the value of limit
     */
    public ResourceLimit getLimit() {
        return limit;
    }

    /**
     * Set the value of limit
     *
     * @param limit new value of limit
     */
    public void setLimit(ResourceLimit limit) {
        this.limit = limit;
    }

    @Override
    public boolean finished() {
        return finished;
    }

    @Override
    public int process(byte[] buffer, int offset, int length) {
        int bytesProcessed = 0;
        do {
            int packetBytes = processPacket(buffer, offset + bytesProcessed, length - bytesProcessed);
            bytesProcessed += packetBytes;
            
            if (packetBytes == 0 || finished) {
                return bytesProcessed;
            }
        } while (true);
    }
    
    public int processPacket(byte[] buffer, int offset, int length) {
        if (length < 1) {
            return 0;
        }
        
        int bufferOffset = offset;
        
        int code = (buffer[offset] & 0xff) >> 6;
        
        int sid = buffer[offset++] & 0x3f;
        int sidLength = 1;
        if (sid == 0) {
            sid = 64 + (buffer[offset++] & 0xff);
            sidLength++;
        } else if (sid == 1) {
            sid = 64 + (buffer[offset++] & 0xff) + ((buffer[offset++] & 0xff) << 8);
            sidLength += 2;
        }
        
        int headLength = 0;
        if (code == 0) {
            headLength = 11;
        } else if (code == 1) {
            headLength = 7;
        } else if (code == 2) {
            headLength = 3;
        } else if (code == 3) {
            headLength = 0;
        }
        
        if (length < sidLength + headLength) {
            return 0;
        }
        
        int payloadLength = 0;
        int type;
        MessageInfo lastMessage = lastMessages.get(sid);

        if (headLength >= 7) {
            
            int fullLength = (buffer[offset + 3] & 0xff) << 16 | (buffer[offset + 4] & 0xff) << 8 | (buffer[offset + 5] & 0xff);
            type = (buffer[offset + 6] & 0xff);
            
            if (lastMessage != null && lastMessage.offset > 0 && lastMessage.offset < lastMessage.length) {
                lastMessage = null;
                // throw new RuntimeException("Unsupported feature encountered: LONG_HEAD_INSIDE_FRAGENT.");
            }

            int msid = 0;
            long lastTimestamp = 0;
            if (headLength >= 11) {
                msid = (buffer[offset + 7] & 0xff) << 24 | (buffer[offset + 8] & 0xff) << 16 | (buffer[offset + 9] & 0xff) << 8 | (buffer[offset + 10] & 0xff);
            } else {
                if (lastMessage != null) {
                    msid = lastMessage.messageStreamID;
                    lastTimestamp = lastMessage.calculatedTimestamp;
                }
            }

            lastMessage = new MessageInfo(sid, type, fullLength);
            
            lastMessage.messageStreamID = msid;
            
            int timeStamp = (buffer[offset + 0] & 0xff) << 16 | (buffer[offset + 1] & 0xff) << 8 | (buffer[offset + 2] & 0xff);
            if (headLength >= 11) {
                lastMessage.absoluteTimestamp = timeStamp;
                lastMessage.calculatedTimestamp = timeStamp;
            } else {
                lastMessage.relativeTimestamp = timeStamp;
                lastMessage.calculatedTimestamp = lastTimestamp + timeStamp;
            }
            
            lastMessages.put(sid, lastMessage);
            if (lastMessages.size() > limit.chunkStreamCount) {
                throw new RuntimeException("Unsupported feature encountered: TOO_MANY_STREAMS. Current limit is " + limit.chunkStreamCount + "");
            }
            
            if (fullLength > chunkSize) {
                                
                int readLength = Math.min(fullLength, chunkSize);

                payloadLength = readLength;
            } else {
                payloadLength = fullLength;
            }
            
        } else {
            
            if (lastMessage == null) {
                throw new RuntimeException("Unsupported feature encountered: SHORT_HEAD_WITHOUT_HISTORY.");
            }
            
            if (lastMessage.offset < lastMessage.length) {

                int messageLeft = lastMessage.length - lastMessage.offset;
                payloadLength = Math.min(messageLeft, chunkSize);
                type = lastMessage.type;
                
            } else {

                lastMessage.offset = 0;
                payloadLength = lastMessage.length;
                type = lastMessage.type;
                if (payloadLength > chunkSize) {
                    int readLength = Math.min(payloadLength, chunkSize);
                    
                    payloadLength = readLength;
                }
            }
        }
        
        if (length < sidLength + headLength + payloadLength) {
            return 0;
        }
        
        //System.err.println("code: " + code + ", sid: " + sid + ", type: " + type + ", length: " + payloadLength);
        //System.err.println(HexDump.prettyPrintHex(buffer, bufferOffset, sidLength + headLength + payloadLength));
        //System.err.println(HexDump.prettyPrintHex(buffer, bufferOffset, Math.min(16, sidLength + headLength + payloadLength)));
        
        // change chunk size command processed
        int readOffset = offset + headLength;
        if (type == 0x01) {
            int proposedChunkSize = (buffer[readOffset++] & 0xff) << 24 | (buffer[readOffset++] & 0xff) << 16 | (buffer[readOffset++] & 0xff) << 8 | (buffer[readOffset++] & 0xff);
            int newChunkSize = Math.min(proposedChunkSize, 0xFFFFFF);
            setChunkSize(newChunkSize);
        }
        
        chunkProcessor.processChunk(lastMessage, buffer, readOffset, payloadLength);
        
        finished = !chunkProcessor.alive();
        
        lastMessage.offset += payloadLength;
        
        return sidLength + headLength + payloadLength;
    }

    
}
