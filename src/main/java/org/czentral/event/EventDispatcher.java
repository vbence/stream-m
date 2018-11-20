/*
 * Copyright 2018 Bence Varga
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
 * OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
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
