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

import java.net.*;
import java.io.*;
import java.util.Date;
import threadedevent.EventQueue;
import threadedevent.GeneralEventProducer;

class StreamInput {
	
	private Stream stream;
	private InputStream input;
	private boolean runs;
	
	private byte[] header;
	
	private StreamInputState currentState;
		
	public StreamInput(Stream stream, InputStream input) {
		this.stream = stream;
		this.input = input;
	}
	
	public void run() {
		runs = true;
		
		// notification about starting the input process
		stream.postEvent(new ServerEvent(this, stream, ServerEvent.INPUT_START));
		
		changeState(new HeaderDetectionState(this, stream));
		
		byte[] buffer = new byte[65535];
		int offset = 0;
		int length = 0;
		while (runs && stream.running()) {
				
			try {
				
				// starting time of the transfer
				long transferStart = new Date().getTime();
				
				// reading data
				int numBytes = input.read(buffer, offset, buffer.length - offset);
				
				// notification about the transfer
				stream.postEvent(new TransferEvent(this, stream, TransferEvent.STREAM_INPUT, numBytes, new Date().getTime() - transferStart));

				if (numBytes == -1)
					runs = false;
				length += numBytes;
				
				int newOffset = currentState.processData(buffer, 0, length);
				if (newOffset < offset + length) {
					length = length - newOffset;
					System.arraycopy(buffer, newOffset, buffer, 0, length);
					offset = length;
				} else {
					length = 0;
					offset = 0;
				}
				
			} catch (SocketTimeoutException e) {
				continue;
			} catch (Exception e) {
				e.printStackTrace();
				runs = false;
			}
		}
		
		try {
			input.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		// notification about ending the input process
		stream.postEvent(new ServerEvent(this, stream, ServerEvent.INPUT_STOP));
	}
	
	public void changeState(StreamInputState newState) {
		currentState = newState;
	}
	
}
