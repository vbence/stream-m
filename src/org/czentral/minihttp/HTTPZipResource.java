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
 * Created: Thursday, April 29, 2010 5:13:55 PM
 * Modified: Thursday, April 29, 2010 5:13:55 PM
 */

import java.util.zip.*;
import java.io.*;

public class HTTPZipResource implements HTTPResource {
	
	protected ZipFile zip;
	
	public HTTPZipResource(String fileName) throws ZipException, IOException {
		zip = new ZipFile(fileName);
	}
		
	public void serve(HTTPRequest request, HTTPResponse response) throws HTTPException {
		final int BUFFER_LENGTH = 16 * 1024;
		
		// only GET is allowed
		if (!request.getMethod().equals(HTTPRequest.METHOD_GET))
			throw new HTTPException(400, "Bad Request");
			
		
		String entryName = request.getPathName().substring(request.getResourcePath().length() + 1);
		ZipEntry entry = zip.getEntry(entryName);
		
		if (entry == null)
			throw new HTTPException(404, "Not Found");
		
		try {
			InputStream is = zip.getInputStream(entry);
			byte[] buffer = new byte[BUFFER_LENGTH];
			
			response.setParameter("X-Powered-By", "HTTPZipResource");
			
			response.setParameter("Content-length", Long.toString(entry.getSize()));
			OutputStream os = response.getOutputStream();
			int red;
			while ((red = is.read(buffer)) != -1) {
				os.write(buffer, 0, red);
			}
			
			is.close();
			os.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
