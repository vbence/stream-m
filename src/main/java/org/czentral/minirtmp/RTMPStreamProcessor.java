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

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.czentral.util.stream.Processor;

class RTMPStreamProcessor implements Processor {
    
    protected boolean finished = false;
    
    protected Map<Integer, MessageInfo> lastMessages = new HashMap<Integer, MessageInfo>();
    
    private final int DEFAULT_CHUNK_SIZE = 128;
    
    protected ResourceLimit limit;
    
    protected ChunkProcessor chunkProcessor;

    protected RtmpReader reader = new RtmpReader(DEFAULT_CHUNK_SIZE);

    protected RtmpPacket lastPacket = null;

    public RTMPStreamProcessor(ResourceLimit limit, ChunkProcessor chunkProcessor) {
        this.limit = limit;
        this.chunkProcessor = chunkProcessor;
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

        int processed = 0;

        ByteBuffer bb = ByteBuffer.wrap(buffer, offset, length);
        if (lastPacket == null || lastPacket.chunkOffset >= reader.getChunkSize() || lastPacket.messageOffset >= lastPacket.messageSize) {
            try {
                Optional<RtmpPacket> op = reader.read(bb);
                if (!op.isPresent()) {
                    return 0;
                }
                lastPacket = op.get();
                processed += lastPacket.headerLength;
                //System.out.println(op.get());
            } catch (RtmpException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
                //return 0;
            }
        }
        //if (bb.remaining() < lastPacket.payloadLegth) {
        //    return 0;
        //}

        //System.err.println("code: " + code + ", sid: " + sid + ", type: " + type + ", length: " + payloadLength);
        //System.err.println(HexDump.prettyPrintHex(buffer, bufferOffset, sidLength + headLength + payloadLength));
        //System.err.println(HexDump.prettyPrintHex(buffer, bufferOffset, Math.min(16, sidLength + headLength + payloadLength)));
        
        // change chunk size command processed
        if (lastPacket.messageType == 0x01) {
            if (bb.remaining() < 4) {
                return 0;
            }
            ByteBuffer bbc = bb.duplicate();
            int proposedChunkSize = (bbc.get() & 0xff) << 24 | (bbc.get() & 0xff) << 16
                    | (bbc.get() & 0xff) << 8 | (bbc.get() & 0xff);
            int newChunkSize = Math.min(proposedChunkSize, 0xFFFFFF);
            reader.setChunkSize(newChunkSize);
        }

        //chunkProcessor.processChunk(lastMessage, buffer, readOffset, payloadLength);
        //MessageInfo messageInfo = buildInfo(p);
        int chunkRemaining = (int)Math.min(lastPacket.messageSize-lastPacket.messageOffset, reader.getChunkSize() - lastPacket.chunkOffset);
        int byteCount = Math.min(chunkRemaining, bb.remaining());
        if (byteCount > 0) {
            chunkProcessor.processChunk(buildInfo(lastPacket), bb.array(), bb.position(), byteCount);

            finished = !chunkProcessor.alive();

            lastPacket = lastPacket.transposed(byteCount);

            processed += byteCount;
        }

        //System.out.printf("len: %d, newLen: %d%n", sidLength + headLength + payloadLength, p.messageSize + p.headerLength);
        //return sidLength + headLength + payloadLength;
        //System.out.printf("len: %d, newLen: %d (%d + %d)%n", sidLength + headLength + payloadLength, p.headerLength + p.payloadLegth, p.headerLength, p.payloadLegth);
        return processed;
    }

    private MessageInfo buildInfo(RtmpPacket p) {
        MessageInfo mi = new MessageInfo(p.sid, p.messageType, (int)p.messageSize);
        mi.offset = (int)p.messageOffset;
        mi.length = (int)p.messageSize;
        mi.calculatedTimestamp = p.absoluteTimestamp;
        return mi;
    }

}
