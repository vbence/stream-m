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
