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

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Varga Bence (vbence@czentral.org)
 */
public class AnnotationMapSerializationContext implements SerializationContext {
    
    protected Map<Class,Annotation> map = new HashMap<>();
    
    protected BitBuffer buffer;
    
    protected Class type;
    
    protected Serializer generalSerializer;
    

    public AnnotationMapSerializationContext(BitBuffer buffer, Class type, Serializer generalSerializer) {
        this.buffer = buffer;
        this.type = type;
        this.generalSerializer = generalSerializer;
    }
    
    
    @Override
    public Annotation getAnnotation(Class annotationType) {
        return map.get(annotationType);
    }
    
    public void putAnnotation(Class cls, Annotation annotation) {
        map.put(cls, annotation);
    }
    
    @Override
    public BitBuffer getBuffer() {
        return buffer;
    }

    @Override
    public Class getType() {
        return type;
    }

    @Override
    public Serializer getRootSerializer() {
        return generalSerializer;
    }

}
