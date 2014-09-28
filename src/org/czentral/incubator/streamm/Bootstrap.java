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
import org.czentral.minihttp.FileResource;
import org.czentral.minihttp.*;
import org.czentral.minirtmp.ApplicationLibrary;
import org.czentral.minirtmp.ApplicationLibraryImpl;
import org.czentral.minirtmp.MiniRTMP;
import org.czentral.minirtmp.ApplicationInstance;

public class Bootstrap {
    
    private final String PREFIX_STATIC = "static.";
    
    private final String PREFIX_ZIP = "zip.";
    
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
            System.err.println("Usage: Bootstrap <configfile>");
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
        
        // HTTP
        {
            String httpPortProp = props.getProperty("http.port");
            if (httpPortProp == null) {
                throw new RuntimeException("Config option not found: http.port.");
            }
            int httpPort = Integer.parseInt(httpPortProp);
            MiniHTTP server = new MiniHTTP(httpPort);

            PublisherResource publish = new PublisherResource(props, streams);
            server.registerResource("/publish", publish);

            ConsumerResource consume = new ConsumerResource(props, streams);
            server.registerResource("/consume", consume);

            SnapshotResource snapshot = new SnapshotResource(props, streams);
            server.registerResource("/snapshot", snapshot);

            InfoResource info = new InfoResource(props, streams);
            server.registerResource("/info", info);
            
            // mapped static files
            for (FileShareInfo shareInfo : getSharedResources(PREFIX_STATIC, props)) {
                FileResource resource = new FileResource(shareInfo.fileName);
                server.registerResource(shareInfo.url, resource);
            }

            // mapped zip files
            for (FileShareInfo shareInfo : getSharedResources(PREFIX_ZIP, props)) {
                try {
                    ZipResource resource = new ZipResource(shareInfo.fileName);
                    server.registerResource(shareInfo.url, resource);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            
            server.start();
        }
        
        // RTMP
        {
            ApplicationLibraryImpl library = new ApplicationLibraryImpl();
            library.registerApplication("publish", new PublisherApp(props, streams));

            String rtmpPortProp = props.getProperty("rtmp.port");
            if (rtmpPortProp == null) {
                throw new RuntimeException("Config option not found: http.port.");
            }
            int rtmpPort = Integer.parseInt(rtmpPortProp);
            MiniRTMP server = new MiniRTMP(rtmpPort, library);
            
            server.start();
        }
    
    }
    
    private static Collection<FileShareInfo> getSharedResources(String prefix, Properties props) {
        Collection<FileShareInfo> result = new LinkedList<>();
        
        for (Map.Entry prop : props.entrySet()) {
            String key = (String)prop.getKey();
            if (key.startsWith(prefix)) {
                
                String resourceID = key.substring(prefix.length());
                if (resourceID.contains(".")) {
                    continue;
                }

                if (prop.getValue().equals("true") || prop.getValue().equals("1")) {

                    String fineNameKey = prefix + resourceID + ".file";
                    String fileName = props.getProperty(fineNameKey);
                    if (fileName == null) {
                        throw new RuntimeException("Expected key not defined: [" + fineNameKey + "].");
                    }

                    String urlKey = prefix + resourceID + ".url";
                    String url = props.getProperty(urlKey);
                    if (url == null) {
                        throw new RuntimeException("Expected key not defined: [" + urlKey + "].");
                    }

                    File f = new File(fileName);
                    if (!f.exists()) {
                        throw new RuntimeException("Unable to open file: [" + fileName + "].");
                    }
                    
                    result.add(new FileShareInfo(fileName, url));
                }
            }
        }

        return result;
    }
    
    private static class FileShareInfo {
        
        private final String fileName;
        private final String url;

        public FileShareInfo(String fileName, String url) {
            this.fileName = fileName;
            this.url = url;
        }

        public String getFileName() {
            return fileName;
        }

        public String getUrl() {
            return url;
        }
        
    }
            
            

}
