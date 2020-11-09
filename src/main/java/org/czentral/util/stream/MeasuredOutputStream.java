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
import java.io.OutputStream;
import java.util.Date;
import org.czentral.event.EventSource;
import org.czentral.incubator.streamm.Stream;

/**
 * Decorates an OutputStream object, generating TransferEvents on writes. Data
 * is being written in chunks to achieve a better time-resolution with the
 * events. Smaller chunk sizes lead to better resolution on cost of resources.
 * 
 * To avoid unnecessary resource usage the last <i>chunkSize / 2</i> bytes are
 * written together with the last chunk.
 * 
 * @author Varga Bence
 */
public class MeasuredOutputStream extends OutputStream {
    
    private final int DEFAULT_PACKET_SIZE = 1024 * 1024;

    protected OutputStream base;
    protected EventSource source;
    
    protected int chunkSize = DEFAULT_PACKET_SIZE;
    
    /**
     * Wraps the given output stream. Uses default packet size.
     * 
     * @param base Existing output stream to use.
     * @param source Object to work as event source.
     */
    public MeasuredOutputStream(OutputStream base, EventSource source) {
        this.base = base;
        this.source = source;
    }
    
    /**
     * Wraps the given output stream and sets a custom packet size.
     * 
     * @param base Existing output stream to use.
     * @param stream Stream object, works as origination of events.
     * @param packetSize Size of the chunks of data written at one time.
     */
    public MeasuredOutputStream(OutputStream base, Stream stream, int packetSize) {
        this(base, stream);
        this.chunkSize = packetSize;
    }
    
    @Override
    public void write(int i) throws IOException {
        byte[] buffer = { (byte)i };
        write(buffer, 0, 1);
    }

    @Override
    public void write(byte[] buffer) throws IOException {
        write(buffer, 0, buffer.length);
    }
    
    @Override
    public void write(byte[] buffer, int offset, int length) throws IOException {
        
        while (length > 0) {

            // current packet size
            int fragLength = length;
            if (fragLength >= 1.5 * chunkSize)
                fragLength = chunkSize;

            // starting time of the transfer
            long transferStart = new Date().getTime();

            // writing data packet
            base.write(buffer, offset, fragLength);

            // notification about the transfer
            source.postEvent(new TransferEvent(source, TransferEvent.STREAM_OUTPUT, fragLength, new Date().getTime() - transferStart));

            // next packet (chunk) start
            offset += fragLength;
            length -= fragLength;
        }
    }
    
}
