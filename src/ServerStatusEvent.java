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

import java.util.Date;
import threadedevent.GeneralEvent;

public class ServerStatusEvent extends GeneralEvent {

	public static final int DEFAULT_TYPE = 1;
	
	private int clientCount = -1;
	private int clientLimit = -1;
	
	public ServerStatusEvent(Object source) {
		super(source, DEFAULT_TYPE);
	}
	
	public void setClientCount(int newClientCount) {
		clientCount = newClientCount;
	}
	
	public void setClientLimit(int newClientLimit) {
		clientLimit = newClientLimit;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer(40);
		sb.append("{cls:4, clientCount:");
		sb.append(clientCount);
		sb.append(", clientLimit:");
		sb.append(clientLimit);
		sb.append(",time:");
		sb.append(getDate().getTime());
		sb.append("},");
		return sb.toString();
	}
}
