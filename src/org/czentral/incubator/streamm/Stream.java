package org.czentral.incubator.streamm;

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
