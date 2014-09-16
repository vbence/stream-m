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
import static org.junit.Assert.*;

/**
 *
 * @author Varga Bence (vbence@czentral.org)
 */
public class ByteBufferSerializerTest {
    
    SerializationContext context = null;
    BitfieldImplementation bfi = null;
    
    public ByteBufferSerializerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        final int BUFFER_LENGTH = 6;
        BitBuffer bb = new BitBuffer(new byte[BUFFER_LENGTH], 0, BUFFER_LENGTH * 8);
        AnnotationMapSerializationContext sc = new AnnotationMapSerializationContext(bb, null, null);
        bfi = new BitfieldImplementation();
        sc.putAnnotation(BitField.class, bfi);
        context = sc;
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of serialize method, of class ByteBufferSerializer.
     */
    @Test
    public void testSerialize() {
        System.out.println("serialize");
        byte[] array = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        Object o = ByteBuffer.wrap(array, 1, 5);
        ByteBufferSerializer instance = new ByteBufferSerializer();
        instance.serialize(o, context);
        
        byte[] expected = {1, 2, 3, 4, 5, 0};
        assert(Arrays.equals(context.getBuffer().getBuffer(), expected));
    }
    
}
