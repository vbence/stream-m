/*
    This file is part of "stream.m" software, a video broadcasting tool
    compatible with Google's WebM format.
    Copyright (C) 2011 Varga Bence

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.Date;

/**
 *
 * @author Varga Bence
 */
class MeasuredInputStream extends InputStream {
    
    protected final InputStream base;
    protected final Stream stream;
    
    public MeasuredInputStream(InputStream base, Stream stream) {
        this.base = base;
        this.stream = stream;
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
        stream.postEvent(new TransferEvent(this, stream, TransferEvent.STREAM_INPUT, numBytes, new Date().getTime() - transferStart));
        
        return numBytes;
    }
    
}
