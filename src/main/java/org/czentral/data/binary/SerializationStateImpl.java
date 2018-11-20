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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Varga Bence (vbence@czentral.org)
 */
public class SerializationStateImpl implements SerializationState {
    
    private List<String> propertyQueue = new LinkedList<>();
    
    private List<PropertyLayout> bufferLayout = new LinkedList<>();
    
    private BitBuffer buffer;
    
    private List<Object> parameters = new LinkedList<>();
    
    private Set<String> requeued = new HashSet<>();


    public void setBuffer(BitBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public List<String> getPropertyQueue() {
        return propertyQueue;
    }

    public void setPropertyQueue(List<String> propertyQueue) {
        this.propertyQueue = propertyQueue;
    }

    @Override
    public List<PropertyLayout> getBufferLayout() {
        return bufferLayout;
    }

    public void setBufferLayout(List<PropertyLayout> bufferLayout) {
        this.bufferLayout = bufferLayout;
    }
    
    @Override
    public BitBuffer getBuffer() {
        return buffer;
    }

    @Override
    public void requeue(String name) {
        requeued.add(name);
    }
    
    public Set<String> getRequeued() {
        return requeued;
    }

    
    @Override
    public <T> List<T> getParameters(Class<T> c) {
        List<T> result = new LinkedList<>();
        for (Object param : parameters) {
            if (c.isAssignableFrom(param.getClass())) {
                result.add((T)param);
            }
        }
        return result;
    }

    @Override
    public <T> void addParameter(T parameter) {
        parameters.add(parameter);
    }

    
    public static class PropertyLayoutImpl implements SerializationState.PropertyLayout {
        
        private final String name;
        
        private final long bitPosition;

        private final long bitLength;

        public PropertyLayoutImpl(String name, long bitPosition, long bitLength) {
            this.name = name;
            this.bitPosition = bitPosition;
            this.bitLength = bitLength;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public long getBitPosition() {
            return bitPosition;
        }

        @Override
        public long getBitLength() {
            return bitLength;
        }
        
    }
}
