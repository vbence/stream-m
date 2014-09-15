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
