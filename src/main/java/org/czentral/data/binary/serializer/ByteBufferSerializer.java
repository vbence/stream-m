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

import java.nio.ByteBuffer;
import org.czentral.data.binary.SerializationContext;
import org.czentral.data.binary.Serializer;

/**
 * Serializes a ByteBuffer's content. The buffer's backing array will be copied
 * to the target starting with position (inclusive) to limit (exclusive).
 * 
 * @author Varga Bence (vbence@czentral.org)
 */
public class ByteBufferSerializer implements Serializer {

    @Override
    public void serialize(Object o, SerializationContext sc) {
        
        ByteBuffer bb = (ByteBuffer)o;
        
        if ((sc.getBuffer().getBitPosition() & 0x07) > 0) {
            throw new RuntimeException("Current buffer position is not byte-aligned.");
        }

        int bufferOffset = (int)((sc.getBuffer().getBitOffset() + sc.getBuffer().getBitPosition()) >> 3);
        
        int elemCount = bb.limit() - bb.position();
        System.arraycopy(bb.array(), bb.position(), sc.getBuffer().getBuffer(), bufferOffset, elemCount);
        sc.getBuffer().moveBitPosition(elemCount << 3);
    }

    @Override
    public Object unserialize(SerializationContext context) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
