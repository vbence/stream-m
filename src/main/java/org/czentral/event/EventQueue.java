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

/**
 * Event queue for asynchronous processing.
 */
public class EventQueue {
    
    private final int BUFFER_SIZE = 1024;
    
    private final Event[] buffer = new Event[BUFFER_SIZE];
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
     * Un-shifts the event first in the queue. This method <b>blocks</b> until an event
     * is available.
     * 
     * @return The event which is in the queue for the longest time (not necessarily the oldest event).
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
