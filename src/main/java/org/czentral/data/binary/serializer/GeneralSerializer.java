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
