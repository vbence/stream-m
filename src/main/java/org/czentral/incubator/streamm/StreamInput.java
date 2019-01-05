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

import java.io.*;
import org.czentral.util.stream.Buffer;
import org.czentral.util.stream.Feeder;

public class StreamInput {
    
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
        
        Buffer buffer = new Buffer(BUFFER_SIZE);
        Feeder feeder = new Feeder(buffer, input);
        
        HeaderDetectionState hds = new HeaderDetectionState(this, stream);
        feeder.feedTo(hds);

        StreamingState ss = new StreamingState(this, stream, hds.getVideoTrackNumber());
        feeder.feedTo(ss);
        
        // notification about ending the input process
        stream.postEvent(new ServerEvent(this, stream, ServerEvent.INPUT_STOP));
    }
    
}
