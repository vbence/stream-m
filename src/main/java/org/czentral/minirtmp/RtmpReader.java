/*
 * Copyright 2019 Bence Varga
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

import org.czentral.incubator.streamm.HexDump;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RtmpReader {
    Map<Integer, RtmpPacket> lastPackets = new HashMap<>();

    private long chunkSize;

    private byte[] lastBytes = new byte[16 * 1024];
    private ByteBuffer lastBuffer = ByteBuffer.wrap(lastBytes);
    private RtmpPacket lastParsedPacket = null;

    public RtmpReader(long chunkSize) {
        this.chunkSize = chunkSize;
    }

    public Optional<RtmpPacket> read(ByteBuffer buffer) throws RtmpException {
        int originalOffset = buffer.position();
        Optional<RtmpPacket> op = RtmpPacket.fromBuffer(buffer);
        if (!op.isPresent()) {
            return op;
        }

        RtmpPacket p = op.get();
        RtmpPacket lastPacket = lastPackets.getOrDefault(p.sid, null);

        boolean newMessage = lastPacket == null ||
                lastPacket.messageOffset == lastPacket.messageSize;

        if (lastPacket != null) {
            p.absoluteTimestamp = lastPacket.absoluteTimestamp;

            if (p.chunkType >= 3) {
                p.packetTimestamp = lastPacket.packetTimestamp;
            }

            if (p.chunkType >= 2) {
                p.messageSize = lastPacket.messageSize;
                p.messageType = lastPacket.messageType;
            }

            if (p.chunkType >= 1) {
                p.messageStreamId = lastPacket.messageStreamId;
            }

        } else {
            if (p.chunkType > 0) {
                //System.err.println("Last good packet --------------------------");
                //System.err.println(lastParsedPacket);
                //System.err.print(HexDump.prettyPrintHex(lastBytes, 0, lastBuffer.limit()));
                byte[] b = new byte[1024];
                ByteBuffer temp = buffer.duplicate();
                temp.position(originalOffset);
                int length = Math.min(b.length, temp.remaining());
                temp.get(b, 0, length);
                System.err.print(HexDump.prettyPrintHex(b, 0, length));
                throw new RtmpException(String.format("Missing previous chunk (chunkId: %d, type: %d)", p.sid, p.chunkType));
            }
        }

        long timestamp;
        if (p.hasExtendedTimestamp()) {
            timestamp = (buffer.get() & 0xff) << 24 | (buffer.get() & 0xff) << 16
                    | (buffer.get() & 0xff) << 8 | (buffer.get() & 0xff);
        } else {
            timestamp = p.packetTimestamp;
        }

        if (p.chunkType == 0) {
            p.absoluteTimestamp = timestamp;
        } else {
            if (newMessage) {
                p.absoluteTimestamp += timestamp;
            }
        }

        if (!newMessage) {
            p.messageOffset = lastPacket.messageOffset;
        }
        p.headerLength = buffer.position() - originalOffset;

        lastPackets.put(p.sid, p);

        ByteBuffer temp = buffer.duplicate();
        temp.position(originalOffset);
        lastBuffer.limit(Math.min(temp.limit()-temp.position(), lastBytes.length));
        temp.get(lastBytes, 0, lastBuffer.limit());
        lastParsedPacket = p;

        return Optional.of(p);
    }

    public void setChunkSize(long chunkSize) {
        this.chunkSize = chunkSize;
    }

    public long getChunkSize() {
        return chunkSize;
    }
}
