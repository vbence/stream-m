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

import org.czentral.data.binary.BitBuffer;
import org.czentral.data.binary.SerializationContext;
import org.czentral.data.binary.Serializer;
import org.czentral.data.binary.annotation.BitField;
import org.czentral.data.binary.BitUtility;

/**
 *
 * @author Varga Bence (vbence@czentral.org)
 */
public class DescriptorInteger implements Serializer {

    @Override
    public void serialize(Object o, SerializationContext context) {
        BitField fieldParams = (BitField)context.getAnnotation(BitField.class);
        int length = BitUtility.getBitLength(fieldParams);
        if (length > -1 && (length & 3) > 0) {
            throw new RuntimeException("Fixed length (footprint) must be a multiple of 8.");
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
        
        int bytesNeeded = (length == -1)
                ? (int)Math.ceil(Math.log(value) / Math.log(2) / 7)
                : length >> 3;
        
        int bytePosition = (int)(offsetPosition >> 3);
        for (int i=bytesNeeded - 1; i>=0; i--) {
            buffer[bytePosition++] = (byte)((i == 0 ? 0 : 0x80) | ((value >> i * 7) & 0x7f));
        }
        
        bitBuffer.moveBitPosition(bytesNeeded << 3);
    }

    @Override
    public Object unserialize(SerializationContext context) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
