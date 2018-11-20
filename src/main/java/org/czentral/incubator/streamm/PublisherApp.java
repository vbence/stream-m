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

package org.czentral.incubator.streamm;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Properties;
import org.czentral.data.binary.ByteArraySlice;
import org.czentral.data.binary.Serializer;
import org.czentral.data.binary.SerializerFactoryImpl;
import org.czentral.data.binary.serializer.ByteArraySliceSerializer;
import org.czentral.data.binary.serializer.ByteBufferSerializer;
import org.czentral.data.binary.serializer.DescriptorInteger;
import org.czentral.data.binary.serializer.FixedInteger;
import org.czentral.data.binary.serializer.FixedString;
import org.czentral.data.binary.serializer.GeneralSerializer;
import org.czentral.minirtmp.Application;
import org.czentral.minirtmp.ApplicationInstance;

/**
 *
 * @author Varga Bence
 */
public class PublisherApp implements Application {
    
    protected Properties props;

    protected Map<String, ControlledStream> streams;
    
    protected Serializer serializer; 

    
    public PublisherApp(Properties props, Map<String, ControlledStream> streams) {
        this.props = props;
        this.streams = streams;
        
        SerializerFactoryImpl sf = new SerializerFactoryImpl();
        sf.addSerializer(int.class, new FixedInteger());
        sf.addSerializer(Integer.class, new FixedInteger());
        sf.addSerializer(Number.class, new FixedInteger());
        sf.addSerializer(String.class, new FixedString());
        sf.addSerializer(DescriptorInteger.class, new DescriptorInteger());
        sf.addSerializer(ByteBuffer.class, new ByteBufferSerializer());
        sf.addSerializer(ByteArraySlice.class, new ByteArraySliceSerializer());
        serializer = new GeneralSerializer(sf);
    }

    @Override
    public ApplicationInstance getInstance() {
        return new PublisherAppInstance(props, streams, serializer);
    }
    
}
