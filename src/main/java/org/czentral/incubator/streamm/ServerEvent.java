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

public class ServerEvent extends EventImpl {
    
    public static final int INPUT_START = 1;
    public static final int INPUT_STOP = 2;
    public static final int INPUT_FRAGMENT_START = 3;
    public static final int INPUT_FRAGMENT_END = 4;
    public static final int INPUT_FIRST_FRAGMENT = 5;
    
    public static final int CLIET_START = 1001;
    public static final int CLIET_STOP = 1002;
    public static final int CLIET_FRAGMENT_START = 1003;
    public static final int CLIET_FRAGMENT_END = 1004;
    public static final int CLIET_FRAGMENT_SKIP = 1005;
    
    private Date startDate;
    
    private Object sourceStream;
    
    public ServerEvent(Object source, Object sourceStream, int type) {
        this(source, sourceStream, type, null);
    }
    
    public ServerEvent(Object source, Object sourceStream, int type, Date startDate) {
        super(source, type);
        this.sourceStream = sourceStream;
        this.startDate = startDate;
    }
    
    public Object getSourceStreram() {
        return sourceStream;
    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer(40);
        sb.append("{cls:1, type:");
        sb.append(getType());
        if (startDate != null) {
            sb.append(",start:");
            sb.append(startDate.getTime());
        }
        sb.append(",time:");
        sb.append(getDate().getTime());
        sb.append("},");
        return sb.toString();
    }
}
