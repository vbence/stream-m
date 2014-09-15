/*
 * This file is part of the "stream-m" software. An HTML5 compatible live
 * streaming and broadcasting server.
 * Copyright (C) 2014 Varga Bence
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
