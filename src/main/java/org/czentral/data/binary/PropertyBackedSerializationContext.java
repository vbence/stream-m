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

package org.czentral.data.binary;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;

/**
 *
 * @author Varga Bence (vbence@czentral.org)
 */
public class PropertyBackedSerializationContext implements SerializationContext {
    
    protected AnnotatedElement ae;
    
    protected BitBuffer buffer;
    
    protected Class type;
    
    protected Serializer generalSerializer;
    

    public PropertyBackedSerializationContext(AnnotatedElement ae, BitBuffer buffer, Class type, Serializer generalSerializer) {
        this.ae = ae;
        this.buffer = buffer;
        this.type = type;
        this.generalSerializer = generalSerializer;
    }
    
    
    @Override
    public Annotation getAnnotation(Class annotationType) {
        return ae.getAnnotation(annotationType);
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
