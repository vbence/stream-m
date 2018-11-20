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
import org.omg.PortableServer.REQUEST_PROCESSING_POLICY_ID;

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
