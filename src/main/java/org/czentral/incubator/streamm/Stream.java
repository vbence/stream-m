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

import org.czentral.event.EventSourceImpl;

public class Stream extends EventSourceImpl {
    
    private MovieFragment fragment;
    private int fragmentAge;
    
    private byte[] header;
    
    private String mimeType = "application/octet-stream";
    
    private boolean runs = true;
    
    public synchronized boolean isRunning() {
        return runs;
    }
    
    public synchronized void stop() {
        runs = false;
    }
    
    public synchronized int getFragmentAge() {
        return fragmentAge;
    }
    
    public synchronized MovieFragment getFragment() {
        return fragment;
    }
    
    public synchronized byte[] getHeader() {
        return header;
    }
    
    public synchronized void setHeader(byte[] newHeader) {
        header = newHeader;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
    
    public synchronized void pushFragment(MovieFragment newFragment) {
        if (fragmentAge == 0)
            postEvent(new ServerEvent(this, this, ServerEvent.INPUT_FIRST_FRAGMENT));
        fragment = newFragment;
        fragmentAge++;
    }
}
