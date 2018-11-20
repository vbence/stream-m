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

import java.lang.reflect.Array;
import java.util.Collection;
import org.czentral.data.binary.NoSerializerFoundException;
import org.czentral.data.binary.SerializationContext;
import org.czentral.data.binary.Serializer;
import org.czentral.data.binary.SerializerFactory;
import org.czentral.data.binary.annotation.BitField;
import org.czentral.data.binary.annotation.BitTable;

/**
 *
 * @author Varga Bence (vbence@czentral.org)
 */
public class GeneralSerializer implements Serializer {
    
    protected SerializerFactory factory;
    
    protected Serializer acsInstance = new AnnotatedClass();

    public GeneralSerializer(SerializerFactory factory) {
        this.factory = factory;
    }

    @Override
    public void serialize(Object o, SerializationContext sc) {
        if (o.getClass().isArray()) {
            for (int i=0; i<Array.getLength(o); i++) {
                serializeSingle(Array.get(o, i), sc);
            }

        } else if (o instanceof Collection) {
            for (Object currentObject : (Collection)o) {
                serializeSingle(currentObject, sc);
            }
        
        } else {
            
            serializeSingle(o, sc);
        }
    }

    public void serializeSingle(Object o, SerializationContext sc) {
        Class type = o.getClass();
        BitField fieldAnnotation = (BitField)(sc.getAnnotation(BitField.class));
        if (fieldAnnotation != null && fieldAnnotation.classOverride() != Void.class) {
            type = fieldAnnotation.classOverride();
        }

        boolean itemHandled = serializeWithType(o, type, sc);
        if (!itemHandled) {
            throw new RuntimeException("Unhandled type [" + type + "].");
        }
    }
    
    protected boolean serializeWithType(Object o, Class type, SerializationContext sc) {
        
        Serializer serializer = null;
        if (type.getAnnotation(BitTable.class) != null) {
            
            serializer = acsInstance;
        } else {
        
            try {
                serializer = factory.getSerializer(type);
            } catch (NoSerializerFoundException e) {
                // throw new RuntimeException("No suitable serializer found for type [" + type.getName() + "].", e);
            }
            
        }
        
        if (serializer != null) {
            serializer.serialize(o, sc);
            return true;
        } else {
            
            // try with superclass
            boolean handled = false;
            Class supertype = type.getSuperclass();
            if (supertype != Object.class) {
                handled = serializeWithType(o, supertype, sc);
            }
            return handled;
        }
        
    }
    
    @Override
    public Object unserialize(SerializationContext context) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
