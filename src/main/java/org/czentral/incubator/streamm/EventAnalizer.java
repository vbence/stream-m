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

import org.czentral.util.stream.TransferEvent;
import java.io.OutputStream;
import java.util.Date;
import org.czentral.event.Event;
import org.czentral.event.EventListener;
import org.czentral.event.EventDispatcher;

public class EventAnalizer implements EventListener {
    
    private Stream stream;
    private EventDispatcher dispatcher;
    private OutputStream output;
    
    private boolean runs;
    
    private TransferAnalizer[] transfers = new TransferAnalizer[10];
    
    public EventAnalizer(Stream stream, EventDispatcher dispatcher, OutputStream os) {
        this.stream = stream;
        this.dispatcher = dispatcher;
        this.output = os;
    }
    
    public void handleEvent(Event event) {
        if (event instanceof TransferEvent) {
            
            TransferEvent tEvent = (TransferEvent)event;
            int type = tEvent.getType();
            
            if (type >= transfers.length)
                return;
            
            TransferAnalizer analizer = transfers[type];
            if (analizer == null) {
                analizer = new TransferAnalizer(type);
                transfers[type] = analizer;
            }
            analizer.processTransfer(tEvent);
        } else {
            try {
                output.write(event.toString().getBytes());
                output.flush();
            } catch (Exception e) {
                // socket writing exception (probably client dropped connection)
                runs = false;
            }
        }
    }
    
    public void run() {
        syncTime();
        
        dispatcher.addListener(this);
        runs = true;
        
        while (runs && stream.isRunning()) {
            try {
                Thread.sleep(2000);
            } catch (Exception e) {
            }
        }
        
        dispatcher.removeListener(this);
    }
    
    private void syncTime() {
        StringBuffer sb = new StringBuffer(30);
        sb.append("{cls:3,time:");
        sb.append(new Date().getTime());
        sb.append("},");
            
        try {
            output.write(sb.toString().getBytes());
            output.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    class TransferAnalizer {
        
        private int id;
        
        private int[] transfer = new int[40];
        private int transferOffset = 0;
        private long transferTime = 0;
        
        public TransferAnalizer(int id) {
            this.id = id;
        }
    
        public void processTransfer(TransferEvent event) {
            // event's time in 1/10 sec
            long eventTime = event.getDate().getTime() / 100;
            
            // setting transferTime on the first use
            if (transferTime == 0)
                transferTime = eventTime;
            
            // length of data needs to be fushed (buffer space after
            // transferTime which needs to be reused).
            int flushLength = Math.min((int)(eventTime - transferTime), transfer.length);
            
            // making necesary space in the buffer (flushing current content).
            for (int i=1; i<=flushLength; i++) {
                int idx = (transferOffset + i) % transfer.length;
                sendData((transferTime - 40 + i) * 100, transfer[idx]);
                transfer[idx] = 0;
            }
            
            // setting buffer offset to the end of the freed block
            transferOffset = (transferOffset + flushLength) % transfer.length;
            
            // setting buffer time to event's time
            transferTime = eventTime;
            
            //int graphTime = (int)(event.getDuration() / 100) + 1;
            int eventDuration = Math.max((int)(event.getDuration() / 100), 1);
            int graphicDuration = Math.min(eventDuration, transfer.length);
            int startOffset = (transferOffset - (graphicDuration - 1) + transfer.length) % transfer.length;
            int avgValue = event.getBytes() / graphicDuration;
            for (int i=0; i<graphicDuration; i++) {
                transfer[(startOffset + i + transfer.length) % transfer.length] += avgValue;
            }
        }
        
        private void sendData(long time, int value) {
            StringBuilder sb = new StringBuilder(30);
            
            sb.append("{cls:2,type:");
            sb.append(id);
            sb.append(",time:");
            sb.append(time);
            sb.append(",bytes:");
            sb.append(value);
            sb.append("},");
            
            //System.out.println(sb);
            try {
                output.write(sb.toString().getBytes());
                output.flush();
            } catch (Exception e) {
                // socket writing exception (probably client dropped connection)
                runs = false;
            }
        }
    }
}
