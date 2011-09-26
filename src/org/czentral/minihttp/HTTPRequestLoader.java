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
import org.czentral.util.stream.*;

/**
 * Loads and parses parameters of a HTTP request from an InputStream.
 *
 * The maximum size of a request or header line is the size ofthe buffer.
 */
class HTTPRequestLoader implements HTTPRequest {
	
	// state contants
	private static final int STATE_NOTHING_LOADED = 0;
	private static final int STATE_REQUEST_LINE_LOADED = 1;
	private static final int STATE_REQUEST_HEADERS_LOADED = 2;
	private static final int STATE_GET_VARS_LOADED = 3;
	private static final int STATE_FULLY_LOADED = 1000000;
	
	private static final String TRANSFER_ENCODING = "Transfer-encoding";
	private static final String TRANSFER_ENCODING_CHUNKED = "chunked";
	
	// loading state
	private int state = STATE_NOTHING_LOADED;
	
	// feeder
	private StreamBuffer buffer;
	private InputStream input;
	private InputStream bodyStream;
	private Socket socket;
	private StreamProcessorFeeder feeder;
	
	// parsed values
	private String requestMethod;
	private String requestURI;
	private String requestPath;
	private String queryString;
	private String requestVersion;
	
	// additional fields
	private InetAddress remoteAddress;
	private String resourcePath;
	
	// maped keys and (possibly multiple) values
	private Map<String, String[]> requestHeaders;
	private Map<String, String[]> getParams;
	
	/**
	 * Instantiates the object to load request headers from an
	 * <code>InputStream</code> thru a <code>StreamBuffer</code>. Surplus (bytes
	 * red but not part of the header) remains available after the loading
	 * operation is done.
	 *
	 * The maximum size of a request or header line is the size ofthe buffer.
	 */
	public HTTPRequestLoader(StreamBuffer buffer, InputStream input) {
		this.buffer = buffer;
		this.input = input;
		feeder = new StreamProcessorFeeder(buffer, input);
	}
	
	/**
	 * Loads the full header. Callng this method forces the loader to process
	 * all the request headers, allowing the access the request body (e.g. POST
	 * content).
	 */
	public void loadFully() {
		loadGetVars();
		state = STATE_FULLY_LOADED;
	}
	
	/**
	 * Gets request method (e.g. GET, POST, HEAD).
	 * @return The query string.
	 */
	public String getMethod() {
		loadRequestLine();
		return requestMethod;
	}
	
	/**
	 * Gets request URI (request path with query string).
	 * @return The query string.
	 */
	public String getRequestURI() {
		loadRequestLine();
		return requestURI;
	}
	
	/**
	 * Gets request path: the part of the URI without the query string (and the trailing '?').
	 * @return The query string.
	 */
	public String getPathName() {
		loadRequestLine();
		return requestPath;
	}
	
	/**
	 * Gets query string: the part ofthe URI after the '?' sign.
	 * @return The query string.
	 */
	public String getQueryString() {
		loadRequestLine();
		return requestMethod;
	}
	
	/**
	 * Gets treuest version. (e.g. <code>HTTP/1.1</code>)
	 * @param key Header key.
	 * @return Array containing the values for all header lines with this key.
	 */
	public String getVersion() {
		loadRequestLine();
		return requestMethod;
	}
	
	/**
	 * Returns header fields with this identifier.
	 * @param key Header key.
	 * @return Array containing the values for all header lines with this key.
	 */
	public String[] getHeaders(String key) {
		loadRequestHeaders();
		return requestHeaders.get(key.toUpperCase());
	}
	
	/**
	 * Returns the <strong>lastest<strong> header field with this identifier.
	 * @param key Header key.
	 * @return The latest request header line with the given key.
	 */
	public String getHeader(String key) {
		loadRequestHeaders();
		String[] values = requestHeaders.get(key.toUpperCase());
		if (values == null)
			return null;
		return values[values.length - 1];
	}
	
	/**
	 * Returns all the get parameters with this identifier (in the order of appearance).
	 * @param key Key in the query string.
	 * @return Array containing the values for all get variables with this key.
	 */
	public String[] getParameters(String key) {
		loadGetVars();
		return getParams.get(key);
	}
	
	/**
	 * Returns the latest get parameter with this identifier.
	 * @param key Key in the query string.
	 * @return The latest get variable with the given key.
	 */
	public String getParameter(String key) {
		loadGetVars();
		String[] values = getParams.get(key);
		if (values == null)
			return null;
		return values[values.length - 1];
	}
	
	public InetAddress getRemoteAddress() {
		return remoteAddress;
	}
	
	public void setRemoteAddress(InetAddress addr) {
		remoteAddress = addr;
	}
	
	public Socket getSocket() {
		return socket;
	}
	
	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	
	public InputStream getInputStream() {
		if (bodyStream == null) {
			loadFully();
			
			String transferEncoding = getHeader(TRANSFER_ENCODING);
			if (transferEncoding != null && transferEncoding.equals(TRANSFER_ENCODING_CHUNKED)) {
				bodyStream = new ChunkedInputStream(buffer.getData(), buffer.getOffset(), buffer.getLength(), input);
			} else {
				bodyStream = new PrependedInputStream(buffer.getData(), buffer.getOffset(), buffer.getLength(), input);
			}
		}
		return bodyStream;
	}
	
	public String getResourcePath() {
		return resourcePath;
	}
	
	public void setResourcePath(String addr) {
		resourcePath = addr;
	}
	
	private void loadRequestLine() {
		if (state >= STATE_REQUEST_LINE_LOADED)
			return;

		feeder.feedTo(new RequestLineLoader());
		state = STATE_REQUEST_LINE_LOADED;
	}
	
	private void loadRequestHeaders() {
		if (state >= STATE_REQUEST_HEADERS_LOADED)
			return;

		loadRequestLine();
		
		RequestHeaderLoader loader = new RequestHeaderLoader();
		feeder.feedTo(loader);
		requestHeaders = loader.getCompactMap();
		
		state = STATE_REQUEST_HEADERS_LOADED;
	}
	
	private void loadGetVars() {
		if (state >= STATE_GET_VARS_LOADED)
			return;
		
		loadRequestHeaders();
		MultimapBuilder builder = new MultimapBuilder();

		int sPos = 0;
		int pos;

		while ((pos = queryString.indexOf('=', sPos)) != -1) {
			
			String key = queryString.substring(sPos, pos);
			
			sPos = queryString.indexOf('&', pos + 1);
			if (sPos == -1)
				sPos = queryString.length();
			
			String value = queryString.substring(pos + 1, sPos);
			
			sPos++;
			
			builder.add(key, value);
		}
		
		getParams = builder.getCompactMap();
		state = STATE_GET_VARS_LOADED;
	}
	
	/**
	 * Returns the offset of the first occurence of a given byte inside the selected region of an array.
	 * 
	 * @param array The array containing the data to search in.
	 * @param offset The offset of the first data bayte to compare.
	 * @param length The number of bytes to compare.
	 * @param value The value to search for.
	 * @return Index of the first occurrence of the given value or -1 if no match.
	 */
	private int arrayIndexOf(byte[] array, int offset, int length, byte value) {
		int endOffset = offset + length;
		for (;offset < endOffset; offset++)
			if (array[offset] == value)
				return offset;
		return -1;
	}
	
	
	class RequestHeaderLoader extends StreamProcessor {
		
		private MultimapBuilder requestHeaders = new MultimapBuilder();

		boolean finished = false;
		
		public boolean finished() {
			return finished;
		}
		
		public int process(byte[] buffer, int offset, int length) {
			int startOffset = offset;
			
			int pos = offset;
			do {
				pos = arrayIndexOf(buffer, pos, length - (pos - offset), (byte)'\r');
				int lineEnd = pos;
				
				if (pos == offset) {
					
					// empty line (end of header)
					finished = true;
				
				} else {
					
					// no EOL found or no lookahead possible (not enough bytes buffred)
					if (pos == -1 || pos > offset + length - 3)
						break;
					
					// CR not followed by LF or next line starts with LWS
					if (buffer[pos + 1] != '\n' || buffer[pos + 2] == ' ' || buffer[pos + 2] == '\t') {
						pos++;
						continue;
					}
					
					// find ':' character
					if ((pos = arrayIndexOf(buffer, offset, lineEnd - offset, (byte)':')) != -1) {
						// fond, extracting key-value pair
						String key = new String(buffer, offset, pos - offset);
						pos++;
						String value = new String(buffer, pos, lineEnd - pos);
						
						// trimmed and transformed to upper case (header fields are case-insensitive)
						requestHeaders.add(key.trim().toUpperCase(), value.trim());
					}
					
				}
				
				// adjust buffer boundaries and search position
				length -= lineEnd - offset + 2;
				offset = lineEnd + 2;
				pos = offset;
				
			} while (!finished);
			
			// return number of bytes truly processed
			return offset - startOffset;
		}
		
		public Map<String, String[]> getCompactMap() {
			return requestHeaders.getCompactMap();
		}
	}
	
	
	class RequestLineLoader extends StreamProcessor {
		
		boolean finished = false;
		
		public boolean finished() {
			return finished;
		}
		
		public int process(byte[] buffer, int offset, int length) {
			
			int startOffset = offset;
			int pos;
			
			// finding request method
			pos = arrayIndexOf(buffer, offset, length, (byte)' ');
			if (pos == -1)
				return 0;
			requestMethod = new String(buffer, offset, pos - offset);
			
			length -= pos - offset + 1;
			offset = (pos + 1);
			
			// finding request URI
			pos = arrayIndexOf(buffer, offset, length, (byte)' ');
			if (pos == -1)
				return 0;
			requestURI = new String(buffer, offset, pos - offset);
			
			// find query string (inside request URI)
			int qsPos = requestURI.indexOf('?');
			if (qsPos != -1) {
				requestPath = requestURI.substring(0, qsPos);
				queryString = requestURI.substring(qsPos + 1);
			} else {
				requestPath = requestURI;
				queryString = "";
			}
			
			length -= pos - offset + 1;
			offset = (pos + 1);
			
			// finding request version
			pos = arrayIndexOf(buffer, offset, length, (byte)'\r');
			if (pos == -1 || pos >= offset + length - 1 || buffer[pos + 1] != '\n')
				return startOffset;
			requestVersion = new String(buffer, offset, pos - offset);
			
			// processing of the header is finished
			finished = true;
			
			// number of bytes which can be discarded from the buffer
			return pos + 2 - startOffset;
		}
	}
}
