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

/**
 * Event queue for asynchronious processing.
 */
public class EventQueue {
	
	private final int BUFFER_SIZE = 1024;
	
	private Event[] buffer = new Event[BUFFER_SIZE];
	private int bufferOffset = 0;
	private int bufferLength = 0;
	
	/**
	 * Tries to enqueue this event to be processed later by an other thread.
	 * 
	 * @return <code>True</code> if the event has been successfully queued.
	 */
	public synchronized boolean post(Event event) {
		
		// queue full, the event canot be queued.
		if (bufferLength >= BUFFER_SIZE)
			return false;
		
		// effective offset in the circular buffer
		int offset = (bufferOffset + bufferLength) % BUFFER_SIZE;
		
		// save the event and increment length
		buffer[offset] = event;
		bufferLength++;
		
		// wakes up any threads waiting for events to become available
		notify();
		
		return true;
	}
	
	/**
	 * Unshifts the event first in the queue. This method <b>blocks</b> until an event
	 * is available.
	 * 
	 * @return The event which is in the queue for the longest time (not neccessarily the oldest event).
	 */
	public Event poll() {
		
		Event event;
		
		while ((event = unshift()) == null) {
			
			// block until events are available
			try {
				synchronized (this) {
					wait();
				}
			} catch (InterruptedException e) {
				throw new RuntimeException("Event polling thread interrupted.", e);
			}
		}

		return event;
		
	}
	
	/**
	 * Unshifts the event first in the queue.
	 * 
	 * @return The event which is in the queue for the longest time or <code>null</code> if the queue is empty.
	 */
	private synchronized Event unshift() {
		
		if (bufferLength == 0)
			return null;
		
		Event event = buffer[bufferOffset];
		
		// new offset
		bufferOffset = (bufferOffset + 1) % BUFFER_SIZE;
		
		// decrement length
		bufferLength--;
		
		return event;
	}
}
