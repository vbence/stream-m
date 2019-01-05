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

import java.util.Date;
import org.czentral.event.EventImpl;

public class ServerStatusEvent extends EventImpl {

    public static final int DEFAULT_TYPE = 1;
    
    private int clientCount = -1;
    private int clientLimit = -1;
    
    public ServerStatusEvent(Object source) {
        super(source, DEFAULT_TYPE);
    }
    
    public void setClientCount(int newClientCount) {
        clientCount = newClientCount;
    }
    
    public void setClientLimit(int newClientLimit) {
        clientLimit = newClientLimit;
    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer(40);
        sb.append("{cls:4, clientCount:");
        sb.append(clientCount);
        sb.append(", clientLimit:");
        sb.append(clientLimit);
        sb.append(",time:");
        sb.append(getDate().getTime());
        sb.append("},");
        return sb.toString();
    }
}
