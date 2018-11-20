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

import org.czentral.data.binary.BitBuffer;
import org.czentral.data.binary.SerializationContext;
import org.czentral.data.binary.Serializer;
import org.czentral.data.binary.annotation.BitField;
import org.czentral.data.binary.BitUtility;

/**
 *
 * @author Varga Bence (vbence@czentral.org)
 */
public class FixedInteger implements Serializer {

    @Override
    public void serialize(Object o, SerializationContext context) {
        BitField fieldParams = (BitField)context.getAnnotation(BitField.class);
        int length = BitUtility.getBitLength(fieldParams);
        if (length < 0) {
            throw new RuntimeException("Field length must be specified.");
        }
        
        long value;
        if (o instanceof Integer) {
            value = (Integer)o;
        } else if (o instanceof Byte) {
            value = (Byte)o;
        } else if (o instanceof Long) {
            value = (Long)o;
        } else {
            throw new RuntimeException("Value has unknown type [" + o.getClass().getName() + "].");
        }
        
        BitBuffer bitBuffer = context.getBuffer();
        byte[] buffer = bitBuffer.getBuffer();
        long offsetPosition = bitBuffer.getBitOffset() + bitBuffer.getBitPosition();
        
        int valueLength = length;
        while (valueLength > 0) {
            int bytePosition = (int)(offsetPosition >> 3);
            int bitPtr = valueLength;
            int freeBits = 8 - (int)(offsetPosition & 0x07);
            int rot = bitPtr - freeBits;
            byte mask = -1;
            if (valueLength < 8) {
                mask <<= 8 - valueLength;
            }
            mask = (byte)((mask & 0xff) >>> (offsetPosition & 0x07));
            
            byte data = (rot > 0) ? (byte)(value >> rot) : (byte)(value << -rot);
            data &= mask;
            buffer[bytePosition] &= ~mask;
            buffer[bytePosition] |= data;
            
            valueLength -= freeBits;
            offsetPosition += freeBits;
        }
        bitBuffer.moveBitPosition(length);
        
    }

    @Override
    public Object unserialize(SerializationContext context) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
