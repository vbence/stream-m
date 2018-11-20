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

package org.czentral.util.stream;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.Date;
import org.czentral.event.EventSource;

/**
 *
 * @author Varga Bence
 */
public class MeasuredInputStream extends InputStream {
    
    protected final InputStream base;
    protected final EventSource source;
    
    public MeasuredInputStream(InputStream base, EventSource source) {
        this.base = base;
        this.source = source;
    }

    @Override
    public int read() throws IOException {
        byte[] buffer = new byte[1];
        int bytes = read(buffer, 0, 1);
        return buffer[0];
    }
    
    @Override
    public int read(byte[] buffer, int offset, int maxLength) throws IOException {

        // starting time of the transfer
        long transferStart = new Date().getTime();
        
        int numBytes = 0;
        while (numBytes == 0) {

            try {
                // reading data
                numBytes = base.read(buffer, offset, maxLength);
            } catch (SocketTimeoutException e) {
            }
            
        }

        // notification about the transfer
        source.postEvent(new TransferEvent(source, TransferEvent.STREAM_INPUT, numBytes, new Date().getTime() - transferStart));
        
        return numBytes;
    }
    
}
