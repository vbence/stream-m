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
            BufferedInputStream is;
            OutputStream os;
            try {
                is = new BufferedInputStream(sock.getInputStream());
                os = sock.getOutputStream();
            } catch (IOException e) {
                throw new RuntimeException("Error opening streams");
            }
            
            ResourceLimit limit = new ResourceLimit();
            limit.chunkStreamCount = 8;
            limit.assemblyBufferCount = 2;
            limit.assemblyBufferSize = 4096;
            
            Feeder feeder = new Feeder(new Buffer(262144), is);
            
            HandshakeProcessor handshake = new HandshakeProcessor(os);
            try {
                feeder.feedTo(handshake);
            } catch (RestartStreamerException e) {
                e.printStackTrace();
            }

            String clientId = sock.getRemoteSocketAddress().toString();
            try {
                sock.setReceiveBufferSize(1024*1024);
            } catch (SocketException e) {
                e.printStackTrace();
            }
            ApplicationContext context = new ApplicationContext(os, limit, factory, clientId);
            try {
                feeder.feedTo(new RTMPStreamProcessor(limit, context));
            } catch (RestartStreamerException e) {
                e.printStackTrace();
                System.out.println("restart");
                System.out.println(e.getMessage());
                feeder = new Feeder(new Buffer(262144), is);
                try {
                    feeder.feedTo(new RTMPStreamProcessor(limit, context));
                } catch (RestartStreamerException restartStreamerException) {
                    restartStreamerException.printStackTrace();
                }
            }

            try {
                is.close();
                os.close();
            } catch (IOException e) {
                throw new RuntimeException("Error closing streams.");
            }
        }
    }
}
