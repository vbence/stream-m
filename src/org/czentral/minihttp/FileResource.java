/*
 * This file is part of the "stream-m" software. An HTML5 compatible live
 * streaming server.
 * Copyright (C) 2014 Varga Bence
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.czentral.minihttp;

import java.io.File;
import java.io.FileInputStream;
import org.czentral.minihttp.HTTPException;
import org.czentral.minihttp.HTTPRequest;
import org.czentral.minihttp.HTTPResource;
import org.czentral.minihttp.HTTPResponse;

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
