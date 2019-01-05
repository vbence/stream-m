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

package org.czentral.data.binary.serializer;

import java.lang.annotation.Annotation;
import org.czentral.data.binary.BitBuffer;
import org.czentral.data.binary.SerializationContext;
import org.czentral.data.binary.Serializer;

/**
 *
 * @author Varga Bence (vbence@czentral.org)
 */
class PropertyContext implements SerializationContext {
    
    protected AbstractPropertyAccessor propertyAccess;
    
    protected BitBuffer buffer;
    
    protected Serializer generalSerializer;

    public PropertyContext(AbstractPropertyAccessor propertyAccess, BitBuffer buffer, Serializer generalSerializer) {
        this.propertyAccess = propertyAccess;
        this.buffer = buffer;
        this.generalSerializer = generalSerializer;
    }

    @Override
    public Annotation getAnnotation(Class type) {
        return propertyAccess.annotatedElement.getAnnotation(type);
    }

    @Override
    public BitBuffer getBuffer() {
        return buffer;
    }

    @Override
    public Class getType() {
        return propertyAccess.type;
    }

    @Override
    public Serializer getRootSerializer() {
        return generalSerializer;
    }
    
}
