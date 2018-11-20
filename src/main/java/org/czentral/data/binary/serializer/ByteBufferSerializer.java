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
