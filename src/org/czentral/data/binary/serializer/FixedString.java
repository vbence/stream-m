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

import java.io.UnsupportedEncodingException;
import org.czentral.data.binary.SerializationContext;
import org.czentral.data.binary.Serializer;
import org.czentral.data.binary.annotation.BitField;
import org.czentral.data.binary.BitUtility;

/**
 *
 * @author Varga Bence (vbence@czentral.org)
 */
public class FixedString implements Serializer {
    
    public static final String defaultCharset = "UTF-8";

    @Override
    public void serialize(Object o, SerializationContext sc) {
        BitField fieldParams = (BitField)sc.getAnnotation(BitField.class);
        int length = BitUtility.getByteLength(fieldParams);
        if (length < 0) {
            throw new RuntimeException("Field length must be specified.");
        }
        
        String value = (String)o;
        byte[] bytes = null;
        try {
            bytes = value.getBytes(defaultCharset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        
        if (bytes.length != length) {
            throw new RuntimeException("Field length mismatch (got " + bytes.length + " bytes, expected " + length + ").");
        }
        
        if ((sc.getBuffer().getBitPosition() & 0x07) > 0) {
            throw new RuntimeException("Current buffer position is not byte-aligned.");
        }
        
        int bufferOffset = (int)((sc.getBuffer().getBitOffset() + sc.getBuffer().getBitPosition()) >> 3);
        
        System.arraycopy(bytes, 0, sc.getBuffer().getBuffer(), bufferOffset, length);
        sc.getBuffer().moveBitPosition(length << 3);
    }

    @Override
    public Object unserialize(SerializationContext sc) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
