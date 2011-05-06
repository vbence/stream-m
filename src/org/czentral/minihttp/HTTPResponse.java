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
