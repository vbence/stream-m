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
package org.czentral.data.binary.serializer;

import org.czentral.data.binary.ByteArraySlice;
import org.czentral.data.binary.SerializationContext;
import org.czentral.data.binary.Serializer;

/**
 *
 * @author Varga Bence (vbence@czentral.org)
 */
public class ByteArraySliceSerializer implements Serializer {

    @Override
    public void serialize(Object o, SerializationContext context) {
        ByteArraySlice slice = (ByteArraySlice)o;
        
        if ((context.getBuffer().getBitPosition() & 0x07) > 0) {
            throw new RuntimeException("Current buffer position is not byte-aligned.");
        }

        int bufferOffset = (int)((context.getBuffer().getBitOffset() + context.getBuffer().getBitPosition()) >> 3);
        
        System.arraycopy(slice.getArray(), slice.getOffset(), context.getBuffer().getBuffer(), bufferOffset, slice.getLength());
        context.getBuffer().moveBitPosition(slice.getLength() << 3);
    }

    @Override
    public Object unserialize(SerializationContext context) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
}
