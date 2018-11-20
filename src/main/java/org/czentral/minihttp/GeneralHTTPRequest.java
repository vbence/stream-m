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
import java.net.*;
import java.util.*;
import java.util.regex.*;


public class GeneralHTTPRequest implements HTTPRequest {
    
    protected String method;
    protected String version;
    protected String URI;
    protected String queryString;
    protected String pathName;
    protected Map<String,String> parameters = new HashMap<String,String>(20);
    protected Map<String,String> headerFields = new HashMap<String,String>(20);
    protected String resourcePath;
    protected InetAddress remoteAddress;
    
    
    protected GeneralHTTPRequest() {
    }
    
    
    public String getMethod() {
        return method;
    }
    
    public String getVersion() {
        return version;
    }
    
    public String getQueryString() {
        return queryString;
    }
    
    public String getPathName() {
        return pathName;
    }
    
    public String getRequestURI() {
        return URI;
    }
    
    public String[] getHeaders(String key) {
        throw new Error("Unsupported");
    }
    
    public String getHeader(String key) {
        return headerFields.get(key);
    }
    
    public String[] getParameters(String key) {
        throw new Error("Unsupported");
    }
    
    public String getParameter(String key) {
        return parameters.get(key);
    }
    
    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }
        
    public String getResourcePath() {
        return resourcePath;
    }
    
    public void setRemoteAddress(InetAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
    }
        
    public InetAddress getRemoteAddress() {
        return remoteAddress;
    }
    
    public InputStream getInputStream() {
        throw new Error("Unsupported");
    }
    
    public Socket getSocket() {
        throw new Error("Unsupported");
    }
    
    public static HTTPRequest load(InputStream is) throws HTTPException {
        GeneralHTTPRequest request = new GeneralHTTPRequest();

        try {
            LineNumberReader reader = new LineNumberReader(new InputStreamReader(is));
            
            String requestLine = reader.readLine();
            Pattern requestPattern = Pattern.compile("([^ ]+) ([^ ]+) ([^ ]+)");
            Matcher matcher = requestPattern.matcher(requestLine);
            matcher.find();
            
            if (matcher.groupCount() != 3)
                throw new HTTPException(400, "Bad Request");

            String method = matcher.group(1);
            String url = matcher.group(2);
            String version = matcher.group(3);
            
            // store request version
            request.version = version;
            
            /*
            if (!method.equals(METHOD_GET))
                throw new HTTPException(405, "Method Not Allowed");
            */
            request.URI = url;
                
            // store request method
            request.method = method;
            
            if (!url.startsWith("/"))
                throw new HTTPException(400, "Bad Request");
            
            Pattern paramPattern = Pattern.compile("([^:]+):[ ]?.+");
            String line;
            while ((line = reader.readLine()).length() > 0) {
                Matcher m = paramPattern.matcher(line);
                m.find();
                
                if (m.groupCount() == 2)
                    request.headerFields.put(m.group(1), m.group(2));
            }
            
            int queryPos = url.indexOf("?");
            if (queryPos != -1) {
                request.queryString = url.substring(queryPos + 1);
                request.pathName = url.substring(0, queryPos);
            } else {
                request.pathName = url;
            }
            
            if (request.queryString != null) {
                Pattern qsPattern = Pattern.compile("([^=]+)=([^&]+)[&]?");
                Matcher qsMatcher = qsPattern.matcher(request.queryString);
                while (qsMatcher.find()) {
                    request.parameters.put(qsMatcher.group(1), qsMatcher.group(2));
                }
            }
                        
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        return request;
    }

}

