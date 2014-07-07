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

import java.io.*;
import org.czentral.util.stream.Buffer;
import org.czentral.util.stream.Feeder;

class StreamInput {
    
    private final Stream stream;
    private final InputStream input;
    
    private final int BUFFER_SIZE = 65536;
        
    public StreamInput(Stream stream, InputStream input) {
        this.stream = stream;
        this.input = input;
    }
    
    public void run() {
        
        // notification about starting the input process
        stream.postEvent(new ServerEvent(this, stream, ServerEvent.INPUT_START));
        
        MeasuredInputStream mis = new MeasuredInputStream(input, stream);
        
        Buffer buffer = new Buffer(BUFFER_SIZE);
        Feeder feeder = new Feeder(buffer, mis);
        
        HeaderDetectionState hds = new HeaderDetectionState(this, stream);
        feeder.feedTo(hds);

        StreamingState ss = new StreamingState(this, stream, hds.getVideoTrackNumber());
        feeder.feedTo(ss);
        
        // notification about ending the input process
        stream.postEvent(new ServerEvent(this, stream, ServerEvent.INPUT_STOP));
    }
    
}
