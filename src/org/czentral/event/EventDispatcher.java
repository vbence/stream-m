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

package org.czentral.event;

import java.util.ArrayList;
import java.util.Collection;


public class EventDispatcher implements Runnable {
    
    private boolean runs;
    
    private final EventQueue eventQueue;
    
    private final Collection<EventListener> listeners = new ArrayList<>(12);
    
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
            
            for (EventListener l : listeners) {
                try {
                    l.handleEvent(event);
                } catch (Exception e) {
                    e.printStackTrace();
                    listeners.remove(l);
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
