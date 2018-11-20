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
    
public interface HTTPRequest {
    
    public static final String METHOD_GET = "GET";
    public static final String METHOD_POST = "POST";
    
    /**
     * Gets request method (e.g. GET, POST, HEAD).
     * @return The query string.
     */
    public String getMethod();
    
    /**
     * Gets request URI (request path with query string).
     * @return The query string.
     */
    public String getRequestURI();
    
    /**
     * Gets request path: the part of the URI without the query string (and the trailing '?').
     * @return The query string.
     */
    public String getPathName();
    
    /**
     * Gets query string: the part of the URI after the '?' sign.
     * @return The query string.
     */
    public String getQueryString();
    
    /**
     * Gets the request version. (e.g. <code>HTTP/1.1</code>)
     * @return Array containing the values for all header lines with this key.
     */
    public String getVersion();
    
    /**
     * Returns header fields with this identifier.
     * @param key Header key.
     * @return Array containing the values for all header lines with this key.
     */
    public String[] getHeaders(String key);
    
    /**
     * Returns the last header field with this identifier.
     * @param key Header key.
     * @return The latest request header line with the given key.
     */
    public String getHeader(String key);
    
    /**
     * Returns all the get parameters with this identifier (in the order of appearance).
     * @param key Key in the query string.
     * @return Array containing the values for all get variables with this key.
     */
    public String[] getParameters(String key);
    
    /**
     * Returns the latest get parameter with this identifier.
     * @param key Key in the query string.
     * @return The latest get variable with the given key.
     */
    public String getParameter(String key);
    
    /**
     * Returns the path bound to the resource, handling this request.
     * @return The latest get variable with the given key.
     */
    public String getResourcePath();
    
    /**
     * Returns the address of the remote host.
     * @return The address of the remote host.
     */
    public InetAddress getRemoteAddress();
    
    /**
     * Gets input stream to retrieve the request body.
     * @return InputStream with the request body.
     */
    public InputStream getInputStream();
    
    /**
     * Gets the socket on which the client is connected.
     * @return Socket on which the user is connected of <code>null</code> if not an HTTP connection.
     */
    public Socket getSocket();
    
}

