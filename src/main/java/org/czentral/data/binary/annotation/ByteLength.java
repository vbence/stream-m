/*
 * This file is part of the "stream-m" software. An HTML5 compatible live
 * streaming and broadcasting server.
 * Copyright (C) 2014 Varga Bence
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

package org.czentral.data.binary.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import org.czentral.data.binary.InstanceLifecycleListener;
import org.czentral.data.binary.PropertyHandler;
import org.czentral.data.binary.PropertyLifecycleListener;
import org.czentral.data.binary.SerializationState;

/**
 *
 * @author Varga Bence (vbence@czentral.org)
 */
@Retention(value = RetentionPolicy.RUNTIME)
@ProcessingAnnotation(listeners = {ByteLength.Listener.class})
public @interface ByteLength {
    
    public enum Mode {
        unspecified,
        full,
        following
    }
    
    public Mode value() default Mode.unspecified;
    
    
    public static class Listener implements InstanceLifecycleListener, PropertyLifecycleListener {

        @Override
        public void preSerializeInstance(Object o, SerializationState state) {
            return;
        }

        @Override
        public void postSerializeInstance(Object o, SerializationState state) {
            List<Parameter> params = state.getParameters(Parameter.class);
            for (Parameter param : params) {

                long firstOffset;
                Mode mode = ((ByteLength)param.prop.getAnnotation(ByteLength.class)).value();
                if (mode == Mode.full) {
                    firstOffset = state.getBufferLayout().get(0).getBitPosition();
                } else if (mode == Mode.following) {
                    firstOffset = param.layout.getBitPosition() + param.layout.getBitLength();
                } else {
                    throw new RuntimeException("ByteLength mode not specified.");
                }
                
                SerializationState.PropertyLayout lastPropertyLayout = state.getBufferLayout().get(state.getBufferLayout().size() - 1);
                long bitLength = lastPropertyLayout.getBitPosition() + lastPropertyLayout.getBitLength() - firstOffset;
                if ((bitLength & 7) != 0) {
                    throw new RuntimeException("Serialized length is not byte-safe.");
                }
                int byteLength = (int)(bitLength >> 3);
                
                param.prop.setValue(o, byteLength);
                state.requeue(param.prop.getName());
            }
        }

        @Override
        public void preUnserializeInstance(Object o, SerializationState state) {
            return;
        }

        @Override
        public void postUnserializeInstance(Object o, SerializationState state) {
            return;
        }

        @Override
        public boolean preSerializeProperty(Object o, PropertyHandler property, SerializationState state) {
            return true;
        }

        @Override
        public void postSerializeProperty(Object o, PropertyHandler property, SerializationState state) {
            if (property.getAnnotation(ByteLength.class) != null) {
                state.addParameter(new Parameter(property, state.getBufferLayout().get(state.getBufferLayout().size() - 1)));
            }
        }
        
        protected static class Parameter {
            public PropertyHandler prop;
            
            public SerializationState.PropertyLayout layout;

            public Parameter(PropertyHandler prop, SerializationState.PropertyLayout layout) {
                this.prop = prop;
                this.layout = layout;
            }
            
        }
        
    }
    
}
