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

package org.czentral.incubator.streamm;

class EBMLElement {
    
    private long id;
    private long size;
    private byte[] buffer;
    private int offset;
    private int dataOffset;
    
    
    protected EBMLElement(byte[] buffer, int offset, int length) {
        
        if (length < 2) {
            throw new RuntimeException("Partial header (buffered sample too small).");
        }
        
        this.buffer = buffer;
        this.offset = offset;

        int limit = offset + length;

        long sizeFlag;
        long num;

        sizeFlag = 0x80;
        num = 0;
        while (((num |= buffer[offset++] & 0xff) & sizeFlag) == 0 && sizeFlag != 0) {
            if (offset >= limit) {
                throw new RuntimeException("Partial header (buffered sample too small).");
            }
            num <<= 8;
            sizeFlag <<= 7;
        }
        
        id = num;
        
        sizeFlag = 0x80;
        num = 0;
        while (((num |= buffer[offset++] & 0xff) & sizeFlag) == 0 && sizeFlag != 0) {
            if (offset >= limit) {
                throw new RuntimeException("Partial header (buffered sample too small).");
            }
            num <<= 8;
            sizeFlag <<= 7;
        }
        
        size = num ^ sizeFlag;
        
        dataOffset = offset;
    }
    
    public static long loadUnsigned(byte[] buffer, int offset, int length) {
        long num = 0;
        while (length > 0) {
            length--;
            num <<=8;
            num |= buffer[offset++] & 0xff;
        }
        
        return num;
    }
    
    public static long loadEBMLUnsigned(byte[] buffer, int offset, int length) {
        
        long sizeFlag;
        long num;

        sizeFlag = 0x80;
        num = 0;
        while (((num |= buffer[offset++] & 0xff) & sizeFlag) == 0 && sizeFlag != 0) {
            num <<= 8;
            sizeFlag <<= 7;
        }
        
        return num ^ sizeFlag;
    }
    
    public static long loadEBMLSigned(byte[] buffer, int offset, int length) {
        
        long sizeFlag;
        long num;
        long negBits = -1 << 7;

        sizeFlag = 0x80;
        num = 0;
        while (((num |= buffer[offset++] & 0xff) & sizeFlag) == 0 && sizeFlag != 0) {
            num <<= 8;
            sizeFlag <<= 7;
            negBits <<= 7;
        }
        
        if ((num & sizeFlag >> 1) != 0)
            num |= negBits;
        
        return num;
    }
    
    public long getId() {
        return id;
    }

    public long getDataSize() {
        return size;
    }
    
    public int getElementSize() {
        if (size == 0x1ffffffffffffffL)
            return -1;

        if (size >= 0x100000000l)
            throw new RuntimeException("Element too long to get array offset.");

        return (int)(dataOffset - offset + size);
    }
    
    public byte[] getBuffer() {
        return buffer;
    }
    
    public int getElementOffset() {
        return offset;
    }
    
    public int getDataOffset() {
        return dataOffset;
    }
    
    public int getEndOffset() {
        if (size == 0x1ffffffffffffffL)
            return -1;

        if ((dataOffset + size) >= 0x100000000l)
            throw new RuntimeException("Element too long to get array offset.");
        
        return (int)(dataOffset + size);
    }
    
    public String toString() {
        return "EBMLElement ID:0x" + Long.toHexString(id) + " size: " + size;
    }
}
