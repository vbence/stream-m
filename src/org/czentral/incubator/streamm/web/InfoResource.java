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
import org.czentral.event.EventDispatcher;
import org.czentral.incubator.streamm.ControlledStream;
import org.czentral.incubator.streamm.EventAnalizer;
import org.czentral.minihttp.HTTPException;
import org.czentral.minihttp.HTTPRequest;
import org.czentral.minihttp.HTTPResource;
import org.czentral.minihttp.HTTPResponse;

/**
 *
 * @author Varga Bence
 */
public class InfoResource implements HTTPResource {
    
    private final String STR_CONTENT_TYPE = "Content-type";
    
    protected Map<String, String> settings;

    protected Map<String, ControlledStream> streams;

    public InfoResource(Map<String, String> settings, Map<String, ControlledStream> streams) {
        this.settings = settings;
        this.streams = streams;
    }

    public void serve(HTTPRequest request, HTTPResponse response) throws HTTPException {
        // the part of the path after the resource's path
        int resLength = request.getResourcePath().length();
        String requestPath = request.getPathName();
        if (requestPath.length() - resLength <= 1) {
            throw new HTTPException(400, "No Stream ID Specified");
        }
        // stream ID
        String streamID = requestPath.substring(resLength + 1);
        // is a stream with that name defined?
        if (settings.get("streams." + streamID) == null) {
            throw new HTTPException(404, "Stream Not Registered");
        }
        // check password
        String requestPassword = request.getParameter("password");
        if (requestPassword == null) {
            throw new HTTPException(403, "Authentication failed: No password");
        }
        if (!requestPassword.equals(settings.get("streams." + streamID + ".password"))) {
            throw new HTTPException(403, "Authentication failed: Wrong password");
        }
        // getting stream
        ControlledStream stream = (ControlledStream) streams.get(streamID);
        if (stream == null || !stream.isRunning()) {
            throw new HTTPException(503, "Stream Not Running");
        }
        // setting rsponse content-type
        response.setParameter(STR_CONTENT_TYPE, "text/plain");
        // get evet dispatcher (if no current one then it is created)
        EventDispatcher dispatcher = stream.getEventDispatcher();
        // creating analizer
        EventAnalizer analizer = new EventAnalizer(stream, dispatcher, response.getOutputStream());
        // serving the request (from this thread)
        analizer.run();
    }
    
}
