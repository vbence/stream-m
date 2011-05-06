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
				is = sock.getInputStream();
				os = sock.getOutputStream();
			} catch (IOException e) {
				throw new RuntimeException("Error opening streams");
			}

			HTTPResponse response = new HTTPResponse(os);
			HTTPRequestLoader request = null;
			
			//try {
				request = new HTTPRequestLoader(new StreamBuffer(32 * 1024), is);
			//} catch (HTTPException e) {
			//	response.reportException(e);
			//	return;
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
