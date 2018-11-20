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

package org.czentral.incubator.streamm.web;

import java.net.Socket;
import java.util.Map;
import java.util.Properties;
import org.czentral.incubator.streamm.ControlledStream;
import org.czentral.util.stream.MeasuredInputStream;
import org.czentral.incubator.streamm.StreamInput;
import org.czentral.minihttp.HTTPException;
import org.czentral.minihttp.HTTPRequest;
import org.czentral.minihttp.HTTPResource;
import org.czentral.minihttp.HTTPResponse;

/**
 *
 * @author Varga Bence
 */
public class PublisherResource implements HTTPResource {
    
    private final String MIME_TYPE_WEBM = "video/webm; codecs=\"vp8,vorbis\"";
    
    protected Properties props;

    protected Map<String, ControlledStream> streams;

    public PublisherResource(Properties props, Map<String, ControlledStream> streams) {
        this.props = props;
        this.streams = streams;
    }

    public void serve(HTTPRequest request, HTTPResponse response) throws HTTPException {
        // the part of the request path after the resource's path
        int resLength = request.getResourcePath().length();
        String requestPath = request.getPathName();
        if (requestPath.length() - resLength <= 1) {
            throw new HTTPException(404, "No Stream ID Specified");
        }
        // stream ID
        String streamID = requestPath.substring(resLength + 1);
        // is a stream with that name enabled?
        String streamProperty = props.getProperty("streams." + streamID);
        if (!"true".equals(streamProperty) && !"1".equals(streamProperty)) {
            throw new HTTPException(403, "Stream Not Registered");
        }
        // check password
        String requestPassword = request.getParameter("password");
        if (requestPassword == null) {
            throw new HTTPException(403, "Authentication failed: No password");
        }
        if (!requestPassword.equals(props.getProperty("streams." + streamID + ".password"))) {
            throw new HTTPException(403, "Authentication failed: Wrong password");
        }
        // stopping stream if already isRunning
        ControlledStream stream = streams.get(streamID);
        if (stream != null) {
            stream.stop();
        }
        // creating new Sream instance
        stream = new ControlledStream(Integer.parseInt(props.getProperty("streams." + streamID + ".limit")));
        stream.setMimeType(MIME_TYPE_WEBM);
        // put stream to in the collection
        streams.put(streamID, stream);
        // setting socket parameters
        Socket sock = request.getSocket();
        try {
            sock.setSoTimeout(100);
            sock.setReceiveBufferSize(256 * 1024);
        } catch (Exception e) {
            throw new RuntimeException("Cannot set socket parameters", e);
        }
        // generate transfer events to mesaure bandwidth usage
        MeasuredInputStream mis = new MeasuredInputStream(request.getInputStream(), stream);
        // creating input reader
        StreamInput streamInput = new StreamInput(stream, mis);
        /*
         * Start publishing
         */
        // this thread is working (will be blocked) while the stream is being published
        streamInput.run();
        /*
         * End of publishing
         */
        // stopping stream (clients get notified)
        stream.stop();
    }
    
}
