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


import java.io.*;
import java.util.*;


public class HTTPResponse {
    
    protected OutputStream outputStream;
    protected Map<String,String> parameters = new HashMap<String,String>(10);
    protected int responseCode = 200;
    protected String responseMessage = "OK";
    
    protected boolean open = true;
    
    
    public HTTPResponse(OutputStream outputStream) {
        this.outputStream = outputStream;
        parameters.put("Server", "MiniHTTP");
    }
    
    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }
    
    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }
    
    public void setParameter(String key, String value) { 
        parameters.put(key, value);
    }
    
    public OutputStream getOutputStream() {
        flushHeaders();
        return outputStream;
    }
    
    protected synchronized void flushHeaders() {
        
        // if already flushed
        if (!this.open)
            return;

        PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream));
        
        // response code
        writer.println("HTTP/1.1 " + responseCode + " " + responseMessage);
        
        // parameters
        Iterator<Map.Entry<String,String>> iterator = parameters.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String,String> entry = iterator.next();
            writer.println(entry.getKey() + ": " + entry.getValue());
        }
        
        // end of header
        writer.println();
        
        writer.flush();
        
        // note the closed state
        this.open = false;
    }
    
    public void reportException(HTTPException e) {
        setResponseCode(e.getCode());
        setResponseMessage(e.getMessage());
        setParameter("Content-type", "text/html");
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(getOutputStream()));
        writer.println("<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML 2.0//EN\">");
        writer.println("<http><head><title>" + e.getMessage() + "</title></head><body><h1>" + e.getMessage() + "</h1></body></http>");
        writer.close();
    }

}
