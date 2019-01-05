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

package org.czentral.data.binary;

import java.util.List;

/**
 *
 * @author Varga Bence (vbence@czentral.org)
 */
public interface SerializationState {
    
    public List<String> getPropertyQueue();
    
    public List<SerializationState.PropertyLayout> getBufferLayout();
    
    public BitBuffer getBuffer();
    
    public void requeue(String name);

    
    public <T> List<T> getParameters(Class<T> c);
    
    public <T> void addParameter(T parameter);
    
    
    public interface PropertyLayout {
        
        public String getName();
        
        public long getBitPosition();
        
        public long getBitLength();
        
    }
    
}
