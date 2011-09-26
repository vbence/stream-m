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

/**
 * Pre-read data is returned if available, then continues reading form the
 * InputStream.
 */
public class PrependedInputStream extends InputStream {
	
	private InputStream input;
	private byte[] buffer;
	private int bufferOffset;
	
	/**
	 * Constructs an object with a buffer of previously read data and an
	 * InputStream to read the rest of the data from.
	 */
	public PrependedInputStream(byte[] data, int offset, int length, InputStream is) {
		buffer = new byte[length];
		System.arraycopy(data, offset, buffer, 0, length);
		bufferOffset = 0;
		this.input = is;
	}
	
	public int read() throws IOException {
		byte[] shortBuffer = new byte[1];
		read(shortBuffer, 0, 1);
		return shortBuffer[0];
	}
	
	public int read(byte[] data) throws IOException {
		return read(data, 0, data.length);
	}
	
	public int read(byte[] data, int offset, int length) throws IOException {
		
		int numBytes = -1;
		
		if (bufferOffset < buffer.length) {
			
			// number of bytes to read
			numBytes = Math.min(buffer.length - bufferOffset, length);
			
			// copy the data
			System.arraycopy(buffer, bufferOffset, data, offset, numBytes);
			
			// increment buffer offset
			bufferOffset += numBytes;
			
		} else {
			
			// read from the real input stream
			numBytes = input.read(data, offset, length);
			
		}
		
		return numBytes;

	}
	
}
