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

import java.util.Arrays;
import org.czentral.data.binary.AnnotationMapSerializationContext;
import org.czentral.data.binary.BitBuffer;
import org.czentral.data.binary.SerializationContext;
import org.czentral.data.binary.annotation.BitField;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Varga Bence
 */
public class FixedIntegerTest {
    
    SerializationContext context = null;
    BitfieldImplementation bfi = null;
    
    public FixedIntegerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        final int BUFFER_LENGTH = 5;
        BitBuffer bb = new BitBuffer(new byte[BUFFER_LENGTH], 0, BUFFER_LENGTH * 8);
        AnnotationMapSerializationContext sc = new AnnotationMapSerializationContext(bb, null, null);
        bfi = new BitfieldImplementation();
        sc.putAnnotation(BitField.class, bfi);
        context = sc;
    }
    
    @After
    public void tearDown() {
        context = null;
    }

    /**
     * Test of serialize method, of class FixedInteger.
     */
    @Test
    public void testFullByte() {
        System.out.println("fullByte");
        Object o = 0b11111111;
        FixedInteger instance = new FixedInteger();
        bfi.setBitLength(8);
        
        instance.serialize(o, context);

        byte[] expected = {(byte)0b11111111, 0, 0, 0, 0};
        assert(Arrays.equals(context.getBuffer().getBuffer(), expected));
    }
    
    /**
     * Test of serialize method, of class FixedInteger.
     */
    @Test
    public void testBrokenByte() {
        System.out.println("brokenByte");
        Object o = 0b11111111;
        FixedInteger instance = new FixedInteger();
        bfi.setBitLength(8);
        context.getBuffer().moveBitPosition(1);

        instance.serialize(o, context);

        byte[] expected = {(byte)0b01111111, (byte)0b10000000, 0, 0, 0};
        assert(Arrays.equals(context.getBuffer().getBuffer(), expected));
    }
    /**
     * Test of serialize method, of class FixedInteger.
     */
    @Test
    public void testBrokenWord() {
        System.out.println("brokenWord");
        Object o = 0b1111111111111111;
        FixedInteger instance = new FixedInteger();
        bfi.setBitLength(16);
        context.getBuffer().moveBitPosition(1);

        instance.serialize(o, context);

        byte[] expected = {(byte)0b01111111, (byte)0b11111111, (byte)0b10000000, 0, 0};
        assert(Arrays.equals(context.getBuffer().getBuffer(), expected));
    }
    
    class BitfieldImplementation implements BitField {
        
        int bitLength = -1;

        int byteLength = -1;
        
        @Override
        public int byteLength() {
            return byteLength;
        }

        public void setByteLength(int byteLength) {
            this.byteLength = byteLength;
        }

        @Override
        public int bitLength() {
            return bitLength;
        }

        public void setBitLength(int bitLength) {
            this.bitLength = bitLength;
        }

        @Override
        public Class classOverride() {
            return Void.class;
        }

        @Override
        public Class<? extends java.lang.annotation.Annotation> annotationType() {
            return BitField.class;
        }
        
    }
    
}
