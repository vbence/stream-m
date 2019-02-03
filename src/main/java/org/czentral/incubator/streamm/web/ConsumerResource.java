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

import java.util.Map;
import java.util.Properties;
import org.czentral.incubator.streamm.ControlledStream;
import org.czentral.util.stream.MeasuredOutputStream;
import org.czentral.incubator.streamm.StreamClient;
import org.czentral.minihttp.HTTPException;
import org.czentral.minihttp.HTTPRequest;
import org.czentral.minihttp.HTTPResource;
import org.czentral.minihttp.HTTPResponse;

/**
 *
 * @author Varga Bence
 */
public class ConsumerResource implements HTTPResource {
    
    private final String STR_CONTENT_TYPE = "Content-type";
    private final String STR_X_SEQUENCE = "X-Sequence";
    private final String STR_METHOD_HEAD = "HEAD";
    
    protected Properties props;

    protected Map<String, ControlledStream> streams;

    public ConsumerResource(Properties props, Map<String, ControlledStream> streams) {
        this.props = props;
        this.streams = streams;
    }
    
    public void serve(HTTPRequest request, HTTPResponse response) throws HTTPException {
        
        // the part of the path after the resource's path
        int resLength = request.getResourcePath().length();
        String requestPath = request.getPathName();
        if (requestPath.length() - resLength <= 1) {
            throw new HTTPException(400, "No Stream ID Specified");
        }
        
        // Stream ID
        String streamID = requestPath.substring(resLength + 1);
        
        // is a stream with that Stream ID defined?
        if (props.getProperty("streams." + streamID) == null) {
            throw new HTTPException(404, "Stream Not Registered");
        }
        
        // getting stream
        ControlledStream stream = streams.get(streamID);
        if (stream == null || !stream.isRunning()) {
            throw new HTTPException(503, "Stream Not Running");
        }

        String crossOriginProp = props.getProperty("http.crossorgin", null);
        if (crossOriginProp != null) {
            response.setParameter("Access-Control-Allow-Origin", crossOriginProp);
            response.setParameter("Access-Control-Expose-Headers", STR_X_SEQUENCE);
        }

        // setting rsponse content-type
        response.setParameter(STR_CONTENT_TYPE, stream.getMimeType());
        
        // end of HEAD response
        if (request.getMethod().equals(STR_METHOD_HEAD)) {
            response.getOutputStream();
            return;
        }
        
        // request GET parameters
        String sendHeaderParam = getParameterWithDefault(request, "sendHeader", "true");
        boolean sendHeader = !("0".equals(sendHeaderParam) || "false".equals(sendHeaderParam));
        
        String fragmentSequenceParam = getParameterWithDefault(request, "fragmentSequence", "-1");
        int fragmentSequence = Integer.parseInt(fragmentSequenceParam, 10);
                
        String singleFragmentParam = getParameterWithDefault(request, "singleFragment", "false");
        boolean singleFragment = !("0".equals(singleFragmentParam) || "false".equals(singleFragmentParam));
        
        
        // setting rsponse sequence number (if needed)
        if (singleFragment) {
            String sequenceHeader;
            
            if (fragmentSequence > stream.getFragmentAge()) {
                sequenceHeader = Integer.toString(fragmentSequence);
            } else {
                sequenceHeader = Integer.toString(stream.getFragmentAge());
            }
            response.setParameter(STR_X_SEQUENCE, sequenceHeader);
        }

        // check if there are free slots
        if (!stream.subscribe(false)) {
            throw new HTTPException(503, "Resource Busy");
        }

        // log transfer events (bandwidth usage)
        final int PACKET_SIZE = 24 * 1024;
        MeasuredOutputStream mos = new MeasuredOutputStream(response.getOutputStream(), stream, PACKET_SIZE);
        
        // create a client
        StreamClient client = new StreamClient(stream, mos);

        // whether to send header
        client.setSendHeader(sendHeader);
        
        // start output with the requested fragment
        if (fragmentSequence > 0) {
            client.setRequestedFragmentSequence(fragmentSequence);
        }
        
        // send only a single fragment
        client.setSendSingleFragment(singleFragment);
        
        // serving the request (from this thread)
        client.run();

        // freeing the slot
        stream.unsubscribe();
    }
    
    private static String getParameterWithDefault(HTTPRequest request, String key, String defaultValue) {
        String result = request.getParameter(key);
        return result == null ? defaultValue : result;
    }
    
}
