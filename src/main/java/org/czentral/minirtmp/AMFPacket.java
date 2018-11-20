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

package org.czentral.minirtmp;

import java.nio.ByteBuffer;
import java.util.Map;

/**
 *
 * @author bence
 */
public class AMFPacket {
    protected final int BUFFER_SIZE = 65536;
    protected byte[] buffer = new byte[BUFFER_SIZE];
    protected int offset = 0;

    public byte[] getBuffer() {
        return buffer;
    }

    public int getLength() {
        return offset;
    }

    public void writeString(String s) {
        byte[] bytes = null;
        try {
            bytes = s.getBytes("UTF-8");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (bytes.length > 0xffff) {
            throw new IllegalArgumentException("String too big for the supported type (0x02).");
        }
        buffer[offset++] = 0x02;
        buffer[offset++] = (byte) ((bytes.length >> 8) & 0xff);
        buffer[offset++] = (byte) (bytes.length & 0xff);
        System.arraycopy(bytes, 0, buffer, offset, bytes.length);
        offset += bytes.length;
    }

    public void writeNumber(double d) {
        buffer[offset++] = 0x00;
        ByteBuffer.wrap(buffer, offset, 8).putDouble(d);
        offset += 8;
    }

    public void writeNull() {
        buffer[offset++] = 0x05;
    }

    public void writeBoolean(boolean b) {
        buffer[offset++] = 0x01;
        buffer[offset++] = b ? (byte) 0x01 : (byte) 0x00;
    }

    public void writeArray(Object[] a) {
        buffer[offset++] = 0x08;
        if (a.length > 0) {
            throw new UnsupportedOperationException("Currently only empty arrays supported.");
        }
        for (int i = 0; i < 4; i++) {
            buffer[offset++] = 0x00;
        }
    }

    public void writeObject(Map<String, Object> map) {
        buffer[offset++] = 0x03;
        for (String key : map.keySet()) {
            //System.err.println("k: " + key);
            // writing key
            byte[] bytes = key.getBytes();
            if (bytes.length > 0xffff) {
                throw new IllegalArgumentException("Key too long.");
            }
            buffer[offset++] = (byte) ((bytes.length >> 8) & 0xff);
            buffer[offset++] = (byte) (bytes.length & 0xff);
            System.arraycopy(bytes, 0, buffer, offset, bytes.length);
            offset += bytes.length;
            // writing value
            writeMixed(map.get(key));
        }
        buffer[offset++] = 0x00;
        buffer[offset++] = 0x00;
        buffer[offset++] = 0x09;
    }

    @SuppressWarnings(value = "unchecked")
    public void writeMixed(Object o) {
        if (o instanceof String) {
            writeString((String) o);
        } else if (o instanceof Double) {
            writeNumber((Double) o);
        } else if (o instanceof Boolean) {
            writeBoolean((Boolean) o);
        } else if (o instanceof Map) {
            writeObject((Map) o);
        } else if (o instanceof Object[]) {
            writeArray((Object[]) o);
        } else if (o == null) {
            writeNull();
        } else {
            throw new UnsupportedOperationException("Unsupported object type.");
        }
    }
    
}
