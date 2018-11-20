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

import java.util.zip.*;
import java.io.*;

public class ZipResource implements HTTPResource {
    
    protected ZipFile zip;
    
    public ZipResource(String fileName) throws ZipException, IOException {
        zip = new ZipFile(fileName);
    }
    
    @Override
    public void serve(HTTPRequest request, HTTPResponse response) throws HTTPException {
        final int BUFFER_LENGTH = 16 * 1024;
        
        // only GET is allowed
        if (!request.getMethod().equals(HTTPRequest.METHOD_GET))
            throw new HTTPException(400, "Bad Request");
        
        if (request.getPathName().length() <= request.getResourcePath().length() + 1) {
            throw new HTTPException(404, "Not Found");
        }
        
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
