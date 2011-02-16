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


public class GeneralEventProducer {
	
	private EventQueue eventQueue;
	
	public EventQueue getEventQueue() {
		return eventQueue;
	}
	
	public void setEventQueue(EventQueue queue) {
		if (this.eventQueue != null)
			throw new RuntimeException("An event queue already bound to this object.");
		this.eventQueue = queue;
	}
	
	public void removeEventQueue() {
		eventQueue = null;
	}
	
	public boolean postEvent(Event event) {
		if (eventQueue == null)
			return false;
		
		eventQueue.post(event);
		return true;
	}
}
