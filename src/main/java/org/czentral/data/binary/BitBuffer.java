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

package org.czentral.data.binary;

/**
 *
 * @author Varga Bence (vbence@czentral.org)
 */
public class BitBuffer {
    
    protected byte[] buffer;
    protected long bitPosition;
    protected long bitOffset;
    protected long bitLength;

    public BitBuffer(byte[] buffer, long bitOffset, long bitLength) {
        this.buffer = buffer;
        this.bitOffset = bitOffset;
        this.bitLength = bitLength;
        this.bitPosition = 0;
    }
    
    public static BitBuffer createFromBytes(byte[] buffer, int offset, int length) {
        return new BitBuffer(buffer, offset << 8, length << 8);
    }
    
    public static BitBuffer createFromBits(byte[] buffer, long bitOffset, long bitLength) {
        return new BitBuffer(buffer, bitOffset, bitLength);
    }

    public long getBitPosition() {
        return bitPosition;
    }

    public long getBitOffset() {
        return bitOffset;
    }

    public long getBitLength() {
        return bitLength;
    }

    public byte[] getBuffer() {
        return buffer;
    }
    
    public void moveBitPosition(long bits) {
        bitPosition += bits;
    }    
    
}
