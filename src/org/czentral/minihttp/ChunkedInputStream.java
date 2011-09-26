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
 * On-the-fly decoding of chunked transfer-encoded data from an InputStream.
 */
public class ChunkedInputStream extends InputStream {
	
	private final int BUFFER_SIZE = 32 * 1024;
	private final int CHUNK_HEAD_MAX_SIZE = 10;
	
	private InputStream input;
	private byte[] buffer;
	private int bufferOffset;
	private int bufferLength;
	
	private int chunkLength;
	
	/**
	 * Constructs an object with a buffer of previously read data and an
	 * InputStream to read the rest of the data from.
	 */
	public ChunkedInputStream(byte[] data, int offset, int length, InputStream is) {
		buffer = new byte[BUFFER_SIZE];
		System.arraycopy(data, offset, buffer, 0, length);
		bufferLength = length;
		chunkLength = 0;
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
		
		// new chunk start
		if (chunkLength == 0) {
			
			// reading to the buffer until a header can be parsed
			int headLength = refreshChunkSize(buffer, bufferOffset, bufferLength);
			while (headLength == -1) {
				// chunk header not fully loaded
				// ToDo: make space in the buffer if needed
				int numBytes = input.read(buffer, bufferOffset + bufferLength, CHUNK_HEAD_MAX_SIZE);
				if (numBytes == -1)
					return -1;
				bufferLength += numBytes;
				
				headLength = refreshChunkSize(buffer, bufferOffset, bufferLength);
			}
			bufferOffset += headLength;
			bufferLength -= headLength;
			if (bufferLength == 0)
				bufferOffset = 0;
			
		}

		if (bufferLength > 0) {
			
			// continuing a chunk
			int segLength = Math.min(Math.min(chunkLength, bufferLength), length);
			System.arraycopy(buffer, bufferOffset, data, offset, segLength);
			bufferOffset += segLength;
			bufferLength -= segLength;
			if (bufferLength == 0)
				bufferOffset = 0;
			chunkLength -= segLength;
			return segLength;
			
		} else {
			
			// no data buffered
			int segLength = Math.min(chunkLength, length);
			int numBytes = input.read(data, offset, segLength);
			chunkLength -= numBytes;
			return numBytes;
		}
	}
	
	private int refreshChunkSize(byte[] data, int offset, int length) {
		int endOffset = offset + length;
		
		for (int i=offset; i<endOffset - 1; i++) {
			if (data[i] == '\r' && data[i + 1] == '\n') {
				if (i == offset)
					return 2;
				int size = Integer.parseInt(new String(data, offset, i - offset), 16);
				chunkLength = size;
				return i - offset + 2;
			}
		}
		return -1;
	}	
	
}
