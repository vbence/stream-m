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

import java.util.Vector;
import threadedevent.EventDispatcher;
import threadedevent.EventListener;
import threadedevent.EventQueue;
    
public class ControlledStream extends Stream {
    
    private int maxClients;
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