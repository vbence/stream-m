package org.czentral.incubator.streamm;

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

import org.czentral.incubator.streamm.web.SnapshotResource;
import org.czentral.incubator.streamm.web.PublisherResource;
import org.czentral.incubator.streamm.web.ConsumerResource;
import org.czentral.incubator.streamm.web.InfoResource;
import java.io.*;
import java.util.*;
import org.czentral.minihttp.*;

public class Bootstrap {
    
    // configuration settings
    private static Map<String, String> settings = new HashMap<String, String>(20);
        
    // streams by name
    private static Map<String, ControlledStream> streams = new HashMap<String, ControlledStream>();
    
    
    public static void main(String args[]) {
        
        // chacking args
        if (args.length < 1) {
            System.err.println("Usage: java StreamingServer <configfile>");
            System.exit(1);
        }
        
        // loading config
        try {
            String filename = args[0];
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.length() == 0 || line.startsWith("#") || line.startsWith(";"))
                    continue;
                
                int pos = line.indexOf("=");
                if (pos == -1)
                    throw new RuntimeException("Config lines must vahe a <key> = <value> syntax.");
                
                settings.put(line.substring(0, pos).trim(), line.substring(pos + 1).trim());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        // instantiating and running the server
        new Bootstrap().run();
    }
    
    private static void loadConfig(String filename) {
    }
    
    public void run() {
        String serverPort = settings.get("server.port");
        System.out.println("Stating server on port: " + serverPort);
        MiniHTTP server = new MiniHTTP(Integer.parseInt(serverPort));
        
        PublisherResource publish = new PublisherResource(settings, streams);
        server.registerResource("/publish", publish);

        ConsumerResource consume = new ConsumerResource(settings, streams);
        server.registerResource("/consume", consume);

        SnapshotResource snapshot = new SnapshotResource(settings, streams);
        server.registerResource("/snapshot", snapshot);

        InfoResource info = new InfoResource(settings, streams);
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
