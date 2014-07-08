package org.czentral.incubator.streamm;

/*
 * This file is part of the "stream-m" software. An HTML5 compatible live
 * streaming server.
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

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

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
    
    private final int DEFAULT_PACKET_SIZE = 64 * 1024;

    protected OutputStream base;
    protected Stream stream;
    
    protected int chunkSize = DEFAULT_PACKET_SIZE;
    
    /**
     * Wraps the given output stream. Uses default packet size.
     * 
     * @param base Existing output stream to use.
     * @param stream Stream object, works as origination of events.
     */
    public MeasuredOutputStream(OutputStream base, Stream stream) {
        this.base = base;
        this.stream = stream;
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
            stream.postEvent(new TransferEvent(this, stream, TransferEvent.STREAM_OUTPUT, fragLength, new Date().getTime() - transferStart));

            // next packet (chunk) start
            offset += fragLength;
            length -= fragLength;
        }
    }
    
}
