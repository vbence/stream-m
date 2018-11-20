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

package org.czentral.incubator.streamm;

import org.czentral.event.EventDispatcher;
import org.czentral.event.EventQueue;
    
public class ControlledStream extends Stream {
    
    private final int maxClients;
    private int numClients;
    
    private EventDispatcher dispatcher;
    
    public ControlledStream(int maxClients) {
        this.maxClients = maxClients;
    }
    
    public boolean subscribe(boolean force) {
        if (force || numClients < maxClients) {
            numClients++;
            refresStatus();
            return true;
        }
        return false;
    }
    
    public void unsubscribe() {
        numClients--;
        refresStatus();
    }
    
    protected void refresStatus() {
        ServerStatusEvent event = new ServerStatusEvent(this);
        event.setClientCount(numClients);
        postEvent(event);
    }
    
    public EventDispatcher getEventDispatcher() {
        if (dispatcher == null)
            bindDispatcher();
        return dispatcher;
    }
    
    private synchronized void bindDispatcher() {
        if (dispatcher != null)
            throw new RuntimeException("Listener already bound.");
        
        EventQueue queue = new EventQueue();
        dispatcher = new EventDispatcher(queue);
        setEventQueue(queue);
        
        // Starting the event dispatcher on a separate thread (it will be
        // shared thru multiple web clients).
        dispatcher.start();
    }
}