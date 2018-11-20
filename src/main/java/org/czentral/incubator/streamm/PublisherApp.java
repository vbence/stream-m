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
