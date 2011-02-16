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

/*
 * Author: User
 * Created: 2010. október 26. 13:29:14
 * Modified: 2010. október 26. 13:29:14
 */

import java.util.Vector;
import threadedevent.EventQueue;
import threadedevent.EventListener;

public class EventDispatcher implements Runnable {
	
	private boolean runs;
	
	private EventQueue eventQueue;
	
	private Vector<EventListener> listeners = new Vector<EventListener>();
	
	public EventDispatcher (EventQueue eventQueue) {
		this.eventQueue = eventQueue;
	}
	
	public void addListener(EventListener listener) {
		listeners.add(listener);
		//System.out.println("added: " + listener);
	}
	
	public void removeListener(EventListener listener) {
		listeners.remove(listener);
		//System.out.println("removed: " + listener);
	}
	
	public int countListeners() {
		return listeners.size();
	}
	
	public void start() {
		runs = true;
		new Thread(this).start();
	}
	
	public void run() {
		while (runs) {
			Event event = eventQueue.poll();
			
			for (int i=0; i<listeners.size(); i++) {
				//System.out.println("E: " + i);
				try {
					listeners.get(i).handleEvent(event);
				} catch (Exception e) {
					e.printStackTrace();
					listeners.remove(i);
					System.out.println("Exception wile processing event. Affected listener removed.");
				}
			}
		}
	}
	
	public boolean runs() {
		return runs;
	}
	
	public void stop() {
		runs = true;
	}
	
}
