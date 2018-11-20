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
 * On-the-fly decoding of chunked transfer-encoded data from an InputStream.
 */
public class ChunkedInputStream extends InputStream {
    
    private final int BUFFER_SIZE = 32 * 1024;
    private final int CHUNK_HEAD_MAX_SIZE = 10;
    
    private InputStream input;
    private byte[] buffer;
    private int bufferOffset;
    private int bufferLength;
    
    private int chunkLength;
    
    /**
     * Constructs an object with a buffer of previously read data and an
     * InputStream to read the rest of the data from.
     */
    public ChunkedInputStream(byte[] data, int offset, int length, InputStream is) {
        buffer = new byte[BUFFER_SIZE];
        System.arraycopy(data, offset, buffer, 0, length);
        bufferLength = length;
        chunkLength = 0;
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
        
        // new chunk start
        if (chunkLength == 0) {
            
            // reading to the buffer until a header can be parsed
            int headLength = refreshChunkSize(buffer, bufferOffset, bufferLength);
            while (headLength == -1) {
                // chunk header not fully loaded
                // ToDo: make space in the buffer if needed
                int numBytes = input.read(buffer, bufferOffset + bufferLength, CHUNK_HEAD_MAX_SIZE);
                if (numBytes == -1)
                    return -1;
                bufferLength += numBytes;
                
                headLength = refreshChunkSize(buffer, bufferOffset, bufferLength);
            }
            bufferOffset += headLength;
            bufferLength -= headLength;
            if (bufferLength == 0)
                bufferOffset = 0;
            
        }

        if (bufferLength > 0) {
            
            // continuing a chunk
            int segLength = Math.min(Math.min(chunkLength, bufferLength), length);
            System.arraycopy(buffer, bufferOffset, data, offset, segLength);
            bufferOffset += segLength;
            bufferLength -= segLength;
            if (bufferLength == 0)
                bufferOffset = 0;
            chunkLength -= segLength;
            return segLength;
            
        } else {
            
            // no data buffered
            int segLength = Math.min(chunkLength, length);
            int numBytes = input.read(data, offset, segLength);
            chunkLength -= numBytes;
            return numBytes;
        }
    }
    
    private int refreshChunkSize(byte[] data, int offset, int length) {
        int endOffset = offset + length;
        
        for (int i=offset; i<endOffset - 1; i++) {
            if (data[i] == '\r' && data[i + 1] == '\n') {
                if (i == offset)
                    return 2;
                int size = Integer.parseInt(new String(data, offset, i - offset), 16);
                chunkLength = size;
                return i - offset + 2;
            }
        }
        return -1;
    }    
    
}
