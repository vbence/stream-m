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

public class ServerEvent extends GeneralEvent {
	
	public static final int INPUT_START = 1;
	public static final int INPUT_STOP = 2;
	public static final int INPUT_FRAGMENT_START = 3;
	public static final int INPUT_FRAGMENT_END = 4;
	public static final int INPUT_FIRST_FRAGMENT = 5;
	
	public static final int CLIET_START = 1001;
	public static final int CLIET_STOP = 1002;
	public static final int CLIET_FRAGMENT_START = 1003;
	public static final int CLIET_FRAGMENT_END = 1004;
	public static final int CLIET_FRAGMENT_SKIP = 1005;
	
	private Date startDate;
	
	private Object sourceStream;
	
	public ServerEvent(Object source, Object sourceStream, int type) {
		this(source, sourceStream, type, null);
	}
	
	public ServerEvent(Object source, Object sourceStream, int type, Date startDate) {
		super(source, type);
		this.sourceStream = sourceStream;
		this.startDate = startDate;
	}
	
	public Object getSourceStreram() {
		return sourceStream;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer(40);
		sb.append("{cls:1, type:");
		sb.append(getType());
		if (startDate != null) {
			sb.append(",start:");
			sb.append(startDate.getTime());
		}
		sb.append(",time:");
		sb.append(getDate().getTime());
		sb.append("},");
		return sb.toString();
	}
}
