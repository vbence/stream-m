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
	 * Gets query string: the part ofthe URI after the '?' sign.
	 * @return The query string.
	 */
	public String getQueryString();
	
	/**
	 * Gets treuest version. (e.g. <code>HTTP/1.1</code>)
	 * @param key Header key.
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
	 * Returns the lastest header field with this identifier.
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

