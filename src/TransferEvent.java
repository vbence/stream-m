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

public class TransferEvent extends ServerEvent {
	
	public static final int STREAM_INPUT = 1;
	public static final int STREAM_OUTPUT = 2;
	
	private int bytes;
	private long duration;
	
	public TransferEvent(Object source, Object sourceStream, int type, int bytes, long duration) {
		super(source, sourceStream, type);
		this.bytes = bytes;
		this.duration = duration;
	}
	
	public int getBytes() {
		return bytes;
	}
	
	public long getDuration() {
		return duration;
	}
}
