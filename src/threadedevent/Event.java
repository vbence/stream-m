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
package threadedevent;


import java.util.Date;

/**
 * Interface for general-purpose events.
 */
public interface Event {
	
	/**
	 * Gets the originating object of this event.
	 *
	 * @return Originating object.
	 */
	public Object getSource();
	
	/**
	 * Gets the type of the event. Implementing classes sould define the possible
	 * types. Event handlers should check for the class of the event object
	 * (e.g. with instanceOf operator), then check for the type.
	 *
	 * @return Event type (specified by implementing classes).
	 */
	public int getType();
	
	/**
	 * Gets the time when this event occurred.
	 *
	 * @return Date object representing the time of the event.
	 */
	public Date getDate();
	
}
