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
public class FixedStringTest {
    
    SerializationContext context = null;
    BitfieldImplementation bfi = null;
    
    public FixedStringTest() {
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
     * Test of serialize method, of class FixedString.
     */
    @Test
    public void testBasic() {
        System.out.println("basic");
        Object o = "test";
        FixedString instance = new FixedString();
        bfi.setByteLength(4);
        
        instance.serialize(o, context);

        byte[] expected = {'t', 'e', 's', 't', 0};
        assert(Arrays.equals(context.getBuffer().getBuffer(), expected));
    }
    
    /**
     * Test of serialize method, of class FixedString.
     */
    @Test(expected = RuntimeException.class)
    public void testOverflow() {
        System.out.println("overflow");
        Object o = "test";
        FixedString instance = new FixedString();
        bfi.setByteLength(3);
        
        instance.serialize(o, context);
    }
        
    /**
     * Test of serialize method, of class FixedString.
     */
    @Test(expected = RuntimeException.class)
    public void testBrokenPosition() {
        System.out.println("overflow");
        Object o = "test";
        FixedString instance = new FixedString();
        bfi.setByteLength(4);
        context.getBuffer().moveBitPosition(1);
        
        instance.serialize(o, context);
    }
    
}
