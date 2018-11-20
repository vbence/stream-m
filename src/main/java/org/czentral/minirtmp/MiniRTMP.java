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
package org.czentral.minirtmp;

import java.io.*;
import java.net.*;
import org.czentral.util.stream.*;

public class MiniRTMP implements Runnable {
    
    protected int port;
    
    protected InetAddress address;
    
    protected ApplicationLibrary factory;
    
    public MiniRTMP(int port, InetAddress address, ApplicationLibrary factory) {
        this.port = port;
        this.address = address;
        this.factory = factory;
    }

    public MiniRTMP(int port, ApplicationLibrary factory) {
        this(port, null, factory);
    }
    
    public void start() {
        new Thread(this).start();
    }
    
    public void run() {
        
        ServerSocket sock = null;
        try {
            sock = new ServerSocket(port, 50, address);
        } catch (IOException e) {
            throw new RuntimeException("MiniRTMP can not listen on given port: " + port);
        }
        
        try {
            while (true) {
                Socket clientSock = sock.accept();
                new Worker(clientSock).start();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error accepting connection on port: " + port);
        }
    }
    
    class Worker extends Thread {
        
        private Socket sock;
        
        public Worker(Socket sock) {
            this.sock = sock;
        }
        
        public void run() {
            InputStream is;
            OutputStream os;
            try {
                is = sock.getInputStream();
                os = sock.getOutputStream();
            } catch (IOException e) {
                throw new RuntimeException("Error opening streams");
            }
            
            ResourceLimit limit = new ResourceLimit();
            limit.chunkStreamCount = 8;
            limit.assemblyBufferCount = 2;
            limit.assemblyBufferSize = 4096;
            
            Feeder feeder = new Feeder(new Buffer(65536), is);
            
            HandshakeProcessor handshake = new HandshakeProcessor(os);
            feeder.feedTo(handshake);
            
            String clientId = sock.getRemoteSocketAddress().toString();
            ApplicationContext context = new ApplicationContext(os, limit, factory, clientId);
            feeder.feedTo(new RTMPStreamProcessor(limit, context));
            
            try {
                is.close();
                os.close();
            } catch (IOException e) {
                throw new RuntimeException("Error closing streams.");
            }
        }
    }
}
