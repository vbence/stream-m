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

import java.util.Date;

public abstract class EventImpl implements Event {
    
    private Object source;
    
    private int type;
    
    private Date date;
    
    protected EventImpl (Object source) {
        this(source, 0, new Date());
    }
    
    protected EventImpl (Object source, int type) {
        this(source, type, new Date());
    }
    
    protected EventImpl (Object source, int type, Date date) {
        this.source = source;
        this.type = type;
        this.date = date;
    }
    
    public int getType() {
        return type;
    }
    
    public Object getSource() {
        return source;
    }
    
    public Date getDate() {
        return date;
    }
}
