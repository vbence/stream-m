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
package org.czentral.minihttp;

/*
 * Author: User
 * Created: Thursday, April 29, 2010 12:56:12 PM
 * Modified: Thursday, April 29, 2010 12:56:12 PM
 */

import java.io.*;
import java.net.*;
import java.util.*;
import org.czentral.util.stream.*;

public class MiniHTTP extends Thread {
    
    protected int port;
    
    protected Map<String,HTTPResource> resources = new HashMap<String,HTTPResource>();
    
    public MiniHTTP(int port) {
        super();
        this.port = port;
    }
    
    public void run() {
        
        ServerSocket sock = null;
        try {
            sock = new ServerSocket(port);
        } catch (IOException e) {
            throw new RuntimeException("MiniHTTP can not listen on given port: " + port);
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
    
    public void registerResource(String path, HTTPResource resource) {
        if (path.length() > 1 && path.endsWith("/"))
            throw new RuntimeException("Resource identifiers can not end with / character.");
        
        resources.put(path, resource);
    }
    
    public void unregisterResource(String path) {
        resources.remove(path);
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
                is = new BufferedInputStream(sock.getInputStream());
                os = new BufferedOutputStream(sock.getOutputStream());
            } catch (IOException e) {
                throw new RuntimeException("Error opening streams");
            }

            HTTPResponse response = new HTTPResponse(os);
            HTTPRequestLoader request = null;
            
            //try {
                request = new HTTPRequestLoader(new Buffer(32 * 1024), is);
            //} catch (HTTPException e) {
            //    response.reportException(e);
            //    return;
            //}
            
            request.setRemoteAddress(sock.getInetAddress());
            request.setSocket(sock);
            
            HTTPResource resource = null;
            
            String pathName = request.getPathName();
            while ((resource = resources.get(pathName)) == null) {
                int pos = pathName.lastIndexOf("/");
                if (pos == -1)
                    break;
                pathName = pathName.substring(0, pos);
            }
            request.setResourcePath(pathName);
            
            if (resource == null) {
                response.reportException(new HTTPException(404, "Not Found"));
                return;
            }
            
            // load all the request headers before processing
            request.loadFully();
            
            try {
                resource.serve(request, response);
            } catch (HTTPException e) {
                response.reportException(e);
            } catch (Exception e) {
                e.printStackTrace();
                response.reportException(new HTTPException(500, "Internal Server Error"));
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
