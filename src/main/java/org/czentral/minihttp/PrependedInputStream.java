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

package org.czentral.minihttp;

import java.io.*;

/**
 * Pre-read data is returned if available, then continues reading form the
 * InputStream.
 */
public class PrependedInputStream extends InputStream {
    
    private InputStream input;
    private byte[] buffer;
    private int bufferOffset;
    
    /**
     * Constructs an object with a buffer of previously read data and an
     * InputStream to read the rest of the data from.
     */
    public PrependedInputStream(byte[] data, int offset, int length, InputStream is) {
        buffer = new byte[length];
        System.arraycopy(data, offset, buffer, 0, length);
        bufferOffset = 0;
        this.input = is;
    }
    
    public int read() throws IOException {
        byte[] shortBuffer = new byte[1];
        read(shortBuffer, 0, 1);
        return shortBuffer[0];
    }
    
    public int read(byte[] data) throws IOException {
        return read(data, 0, data.length);
    }
    
    public int read(byte[] data, int offset, int length) throws IOException {
        
        int numBytes = -1;
        
        if (bufferOffset < buffer.length) {
            
            // number of bytes to read
            numBytes = Math.min(buffer.length - bufferOffset, length);
            
            // copy the data
            System.arraycopy(buffer, bufferOffset, data, offset, numBytes);
            
            // increment buffer offset
            bufferOffset += numBytes;
            
        } else {
            
            // read from the real input stream
            numBytes = input.read(data, offset, length);
            
        }
        
        return numBytes;

    }
    
}
