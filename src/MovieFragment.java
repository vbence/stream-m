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

class MovieFragment {
	
	private final int INITIAL_CLUSTER_LENGTH = 10;
	private final int TIMECODE_LAST_OFFSET = 18;
	private final int CLUSTER_LENGTH_LAST_OFFSET = 8;
	private final byte[] clusterHead = {0x1F, 0x43, (byte)0xB6, 0x75, 0x08, 00, 00, 00, 00, 
			(byte)0xe7, (byte)0x88, 00, 00, 00, 00, 00, 00, 00, 00};

	private byte[] data = new byte[1024 * 1024];
	private int dataLength = 0;
	private int clusterOffset = -1;
	
	private int keyframeOffset = -1;
	private int keyframeLength = -1;
	
	public MovieFragment() {
	}
	
	public void openCluster(long timeCode) {

		if (clusterOffset != -1)
			closeCluster();
		
		System.arraycopy(clusterHead, 0, data, dataLength, clusterHead.length);
		clusterOffset = dataLength;
		dataLength += clusterHead.length;
		
		// saving timeCode
		int offset = clusterOffset + TIMECODE_LAST_OFFSET;
		long num;
		num = timeCode;
		while (num > 0) {
			data[offset--] = (byte)num;
			num >>= 8;
		}
	}
	
	public void closeCluster() {
		
		if (clusterOffset == -1)
			throw new RuntimeException("No open cluster.");
		
		// cluster length (including initial TimeCode element)
		int clusterLength = dataLength - clusterOffset - INITIAL_CLUSTER_LENGTH;
		
		// saving cluster length to the EBML element's header
		int offset = clusterOffset + CLUSTER_LENGTH_LAST_OFFSET;
		int num;
		num = clusterLength;
		while (num > 0) {
			data[offset--] = (byte)num;
			num >>= 8;
		}
		
		clusterOffset = -1;
	}
	
	public void appendKeyBlock(byte[] buffer, int offset, int length, int keyframeOffset) {
		if (keyframeOffset > 0) {
			this.keyframeOffset = dataLength + (keyframeOffset - offset);
			this.keyframeLength = length - (keyframeOffset - offset);
		}
		appendBlock(buffer, offset, length);
	}
	
	public void appendBlock(byte[] buffer, int offset, int length) {
		if (data.length < dataLength + length)
			throw new RuntimeException("Buffer full");
		
		System.arraycopy(buffer, offset, data, dataLength, length);
		dataLength += length;
	}
	
	public byte[] getData() {
		return data;
	}
	
	public int length() {
		return dataLength;
	}
	
	public int getKeyframeOffset() {
		return keyframeOffset;
	}
	
	public int getKeyframeLength() {
		return keyframeLength;
	}
}
