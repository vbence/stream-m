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

