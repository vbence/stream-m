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

package org.czentral.data.binary;

import org.czentral.data.binary.annotation.BitField;

/**
 *
 * @author Varga Bence (vbence@czentral.org)
 */
public class BitUtility {
    
    public static int getBitLength(BitField field) {
        /* if (field == null) {
            System.out.println("null");
        } */
        int bitLength = field.bitLength();
        int byteLength = field.byteLength();
        
        if (bitLength >= 0) {
            if (byteLength >= 0) {
                throw new RuntimeException("Parameters bitLength and byteLength are mutually exclusive.");
            }
            
            return bitLength;
            
        } else if (byteLength >= 0) {

            return byteLength << 3;
            
        } else {
            
            return -1;
        }
        
    }

    public static int getByteLength(BitField field) {
        int bitLength = getBitLength(field);
        if ((bitLength & 0x07) > 0) {
            throw new RuntimeException("Field length is not a multiple of bytes (8 bits).");
        }
        return bitLength >> 3;
    }
    
}
