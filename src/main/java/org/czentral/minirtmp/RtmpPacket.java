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

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.Optional;

public class RtmpPacket {

    int chunkType;
    int sid;
    long packetTimestamp;
    long messageSize;
    int messageType;
    long messageStreamId;

    long absoluteTimestamp;
    long messageOffset;
    long headerLength;

    protected RtmpPacket() {
    }

    public static Optional<RtmpPacket> fromBuffer(ByteBuffer buffer) {
        try {
            RtmpPacket p = new RtmpPacket();

            int b1 = buffer.get() & 0xff;
            p.chunkType = b1 >> 6;

            int sid1 = b1 & 0x3f;
            if (sid1 == 0) {
                p.sid = 64 + buffer.get() & 0xff;
            } else if (sid1 == 1) {
                p.sid = 64 + ((buffer.get() & 0xff) | ((buffer.get() & 0xff) << 8));
            } else {
                p.sid = sid1;
            }

            if (p.chunkType <= 2) {
                p.packetTimestamp = (buffer.get() & 0xff) << 16 | (buffer.get() & 0xff) << 8 | (buffer.get() & 0xff);
                if (p.chunkType <= 1) {
                    p.messageSize = (buffer.get() & 0xff) << 16 | (buffer.get() & 0xff) << 8 | (buffer.get() & 0xff);
                    p.messageType = buffer.get() & 0xff;
                    if (p.chunkType <= 0) {
                        p.messageStreamId = (buffer.get() & 0xff) | (buffer.get() & 0xff) << 8 | (buffer.get() & 0xff) << 16
                                | (buffer.get() & 0xff) << 24;
                    }
                }
            }

            return Optional.of(p);
        } catch (BufferUnderflowException e) {
            return Optional.empty();
        }
    }

    public boolean hasExtendedTimestamp() {
        return packetTimestamp == 0xffffff;
    }

    @Override
    public String toString() {
        return "RtmpPacket{" +
                "chunkType=" + chunkType +
                ", sid=" + sid +
                ", packetTimestamp=" + packetTimestamp +
                ", messageSize=" + messageSize +
                ", messageType=" + messageType +
                ", messageStreamId=" + messageStreamId +
                ", absoluteTimestamp=" + absoluteTimestamp +
                ", messageOffset=" + messageOffset +
                ", headerLength=" + headerLength +
                '}';
    }
}
