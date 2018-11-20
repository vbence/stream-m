/*
 * This file is part of the "stream-m" software. An HTML5 compatible live
 * streaming and broadcasting server.
 * Copyright (C) 2014 Varga Bence (vbence@czentral.org)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
