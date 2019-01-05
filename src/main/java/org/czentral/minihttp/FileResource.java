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

import java.io.File;
import java.io.FileInputStream;

/**
 *
 * @author Varga Bence
 */
public class FileResource implements HTTPResource {
    private String fileName;

    public FileResource(String fileName) {
        this.fileName = fileName;
    }

    public void serve(HTTPRequest request, HTTPResponse response) throws HTTPException {
        try {
            File headFile = new File(fileName);
            FileInputStream fis = new FileInputStream(headFile);
            byte[] data = new byte[(int) headFile.length()];
            fis.read(data, 0, data.length);
            fis.close();
            response.getOutputStream().write(data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
}
