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
package org.czentral.incubator.streamm;

import org.czentral.incubator.streamm.web.SnapshotResource;
import org.czentral.incubator.streamm.web.PublisherResource;
import org.czentral.incubator.streamm.web.ConsumerResource;
import org.czentral.incubator.streamm.web.InfoResource;
import java.io.*;
import java.util.*;
import org.czentral.minihttp.*;

public class Bootstrap {
    
    // configuration settings
    private Properties props;
    
    // streams by name
    private Map<String, ControlledStream> streams = new HashMap<String, ControlledStream>();

    public Bootstrap(Properties props) {
        this.props = props;
    }
    
    public static void main(String args[]) {
        
        // chacking args
        if (args.length < 1) {
            System.err.println("Usage: java StreamingServer <configfile>");
            System.exit(1);
        }
        
        // loading config
        Properties props = null;
        try {
            String filename = args[0];
            InputStream in = new FileInputStream(filename);
            props = new Properties();
            props.load(in);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        // instantiating and running the server
        new Bootstrap(props).run();
    }
    
    public void run() {
        String serverPort = props.getProperty("server.port");
        System.out.println("Stating server on port: " + serverPort);
        MiniHTTP server = new MiniHTTP(Integer.parseInt(serverPort));
        
        PublisherResource publish = new PublisherResource(props, streams);
        server.registerResource("/publish", publish);

        ConsumerResource consume = new ConsumerResource(props, streams);
        server.registerResource("/consume", consume);

        SnapshotResource snapshot = new SnapshotResource(props, streams);
        server.registerResource("/snapshot", snapshot);

        InfoResource info = new InfoResource(props, streams);
        server.registerResource("/info", info);
        
        /*
        FileResource script = new FileResource("script.js");
        server.registerResource("/script.js", script);
        */
        
        try {
            HTTPZipResource console = new HTTPZipResource("console.zip");
            server.registerResource("/console", console);
        } catch (Exception e) {
            // throw new RuntimeException(e);
        }
        
        server.start();
    }

}
