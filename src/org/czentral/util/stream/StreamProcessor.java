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

package org.czentral.util.stream;


/**
 * A <code>StreamProcessor</code> processes data from a buffer which holds a section of
 * a coninuous stream.
 * 
 * The fashion in which the implementing class handles a case where the given offset is not at a start of
 * a data structure (in some terminologies this is an out-of-sync stream) depends on the implementation. As
 * some binary protocols can recover form this situation (e.g. MPEG Transport Stream) and others can not.
 */
public abstract class StreamProcessor {
	
	/**
	 * Process binary data from this buffer.
	 *
	 * @param buffer The buffer of data.
	 * @param offset The offset of the first byte which needs to be processed within the buffer.
	 * @param buffer The number of data bytes need to be processed.
	 * @return Number of bytes succesfully processed.
	 */
	public abstract int process(byte[] buffer, int offset, int length);
	
	/**
	 * Process binary data from this buffer.
	 *
	 * @param buffer The buffer of data.
	 * @return Number of bytes succesfully processed.
	 */
	public int process(StreamBuffer buffer) {
		return process(buffer.getData(), buffer.getOffset(), buffer.getLength());
	}

	/**
	 * Checks if this processor has finished its job.
	 * 
	 * @return <code>true</code> if this processor extracted all the data.
	 */
	public abstract boolean finished();
}
