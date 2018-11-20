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
