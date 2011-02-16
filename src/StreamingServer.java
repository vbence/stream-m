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

import java.io.*;
import java.net.*;
import java.util.*;
import threadedevent.EventDispatcher;
import org.czentral.minihttp.*;

class StreamingServer {
	
	// configuration settings
	private static Map<String, String> settings = new HashMap<String, String>(20);
		
	// streams by name
	private static Map<String, ControlledStream> streams = new HashMap<String, ControlledStream>();
	
	public static void main(String args[]) {
		
		// chacking args
		if (args.length < 1) {
			System.err.println("Usage: java StreamingServer <configfile>");
			System.exit(1);
		}
		
		// loading config
		try {
			String filename = args[0];
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.length() == 0 || line.startsWith("#") || line.startsWith(";"))
					continue;
				
				int pos = line.indexOf("=");
				if (pos == -1)
					throw new RuntimeException("Config lines must vahe a <key> = <value> syntax.");
				
				settings.put(line.substring(0, pos).trim(), line.substring(pos + 1).trim());
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		// instantiating and running the server
		new StreamingServer().run();
	}
	
	private static void loadConfig(String filename) {
	}
	
	public void run() {
		String serverPort = settings.get("server.port");
		System.out.println("Stating server on port: " + serverPort);
		MiniHTTP server = new MiniHTTP(Integer.parseInt(serverPort));
		
		PublisherResource publish = new PublisherResource();
		server.registerResource("/publish", publish);

		ConsumerResource consume = new ConsumerResource();
		server.registerResource("/consume", consume);

		InfoResource info = new InfoResource();
		server.registerResource("/info", info);
		
		/*
		FileResource script = new FileResource("script.js");
		server.registerResource("/script.js", script);
		*/
		
		try {
			HTTPZipResource console = new HTTPZipResource("console.zip");
			server.registerResource("/console", console);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		server.start();
	}

	class PublisherResource implements HTTPResource {
		
		public void serve(HTTPRequest request, HTTPResponse response) throws HTTPException {
			
			// the part of the request path after the resource's path
			int resLength = request.getResourcePath().length();
			String requestPath = request.getPathName();
			if (requestPath.length() - resLength <= 1)
				throw new HTTPException(404, "No Stream ID Specified");
			
			// stream ID
			String streamID = requestPath.substring(resLength + 1);
			
			// is a stream with that name defined?
			if (settings.get("streams." + streamID) == null)
				throw new HTTPException(403, "Stream Not Registered");
			
			// check password
			String requestPassword = request.getParameter("password");
			if (requestPassword == null)
				throw new HTTPException(403, "Authentication failed: No password");
			if (!requestPassword.equals(settings.get("streams." + streamID + ".password")))
				throw new HTTPException(403, "Authentication failed: Wrong password");
			
			// stopping stream if already running
			ControlledStream stream = streams.get(streamID);
			if (stream != null) {
				stream.stop();
			}
			
			// creating new Sream instance
			stream = new ControlledStream(Integer.parseInt(settings.get("streams." + streamID + ".limit")));
			
			// put stream to in the collection
			streams.put(streamID, stream);
			
			// setting socket parameters
			Socket sock = request.getSocket();
			try {
				sock.setSoTimeout(100);
				sock.setReceiveBufferSize(256 * 1024);
			} catch (Exception e) {
				throw new RuntimeException("Cannot set socket parameters", e);
			}
			
			// creating input reader
			StreamInput streamInput = new StreamInput(stream, request.getInputStream());
			
			/*
			 * Start publishing
			 */

			// this thread is working (will be blocked) while the stream is being published
			streamInput.run();
			
			/*
			 * End of publishing
			 */
			
			// stopping stream (clients get notified)
			stream.stop();
			
		}
	}
	
	class ConsumerResource implements HTTPResource {
		
		private final String STR_CONTENT_TYPE = "Content-type";
		
		public void serve(HTTPRequest request, HTTPResponse response) throws HTTPException {
			// the part of the path after the resource's path
			int resLength = request.getResourcePath().length();
			String requestPath = request.getPathName();
			if (requestPath.length() - resLength <= 1)
				throw new HTTPException(400, "No Stream ID Specified");
			
			// Stream ID
			String streamID = requestPath.substring(resLength + 1);
			
			// is a stream with that Stream ID defined?
			if (settings.get("streams." + streamID) == null)
				throw new HTTPException(404, "Stream Not Registered");
			
			// getting stream
			ControlledStream stream = streams.get(streamID);
			if (stream == null || !stream.running())
				throw new HTTPException(503, "Stream Not Running");
			
			// check if there are free slots
			if (!stream.subscribe(false))
				throw new HTTPException(503, "Resource Busy");

			// setting rsponse content-type
			response.setParameter(STR_CONTENT_TYPE, "video/webm");

			// create a client
			StreamClient client = new StreamClient(stream, response.getOutputStream());
			
			// serving the request (from this thread)
			client.run();
			
			// freeing the slot
			stream.unsubscribe();
				
		}
	}

	class InfoResource implements HTTPResource {
		
		private final String STR_CONTENT_TYPE = "Content-type";
		
		public void serve(HTTPRequest request, HTTPResponse response) throws HTTPException {
			
			System.out.println(0);

			// the part of the path after the resource's path
			int resLength = request.getResourcePath().length();
			String requestPath = request.getPathName();
			if (requestPath.length() - resLength <= 1)
				throw new HTTPException(400, "No Stream ID Specified");
			
			// stream ID
			String streamID = requestPath.substring(resLength + 1);
			
			// is a stream with that name defined?
			if (settings.get("streams." + streamID) == null)
				throw new HTTPException(404, "Stream Not Registered");
			
			// check password
			String requestPassword = request.getParameter("password");
			if (requestPassword == null)
				throw new HTTPException(403, "Authentication failed: No password");
			if (!requestPassword.equals(settings.get("streams." + streamID + ".password")))
				throw new HTTPException(403, "Authentication failed: Wrong password");

			// getting stream
			ControlledStream stream = streams.get(streamID);
			if (stream == null || !stream.running())
				throw new HTTPException(503, "Stream Not Running");
			
			// setting rsponse content-type
			response.setParameter(STR_CONTENT_TYPE, "text/plain");

			System.out.println(1);

			// get evet dispatcher (if no current one then it is created)
			EventDispatcher dispatcher = stream.getEventDispatcher();
			
			System.out.println(2);
			
			// creating analizer
			EventAnalizer analizer = new EventAnalizer(stream, dispatcher, response.getOutputStream());
			
			// serving the request (from this thread)
			analizer.run();
			
		}
	}
	
	class FileResource implements HTTPResource {
		
		private String fileName;
		
		public FileResource(String fileName) {
			this.fileName = fileName;
		}
		
		public void serve(HTTPRequest request, HTTPResponse response) throws HTTPException {
			try {
				File headFile = new File(fileName);
				FileInputStream fis = new FileInputStream(headFile);
				byte[] data = new byte[(int)headFile.length()];
				fis.read(data, 0, data.length);
				fis.close();
				
				response.getOutputStream().write(data);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			
		}
	}
	
}

