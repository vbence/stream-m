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

package org.czentral.minirtmp;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author bence
 */
class AMFDecoder {
    protected byte[] buffer;
    protected int startOffset;
    protected int offset;
    protected int length;

    public AMFDecoder(byte[] buffer, int offset, int length) {
        this.buffer = buffer;
        this.startOffset = offset;
        this.offset = offset;
        this.length = length;
    }

    public boolean eof() {
        return offset >= startOffset + length;
    }

    public String readString() {
        if (buffer[offset] != 0x02) {
            throw new IllegalArgumentException("String type ID (0x02) expected.");
        }
        int length = (buffer[offset + 1] & 0xff) << 8 | (buffer[offset + 2] & 0xff);
        String result = null;
        try {
            result = new String(buffer, offset + 3, length, "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        offset += 1 + 2 + length;
        return result;
    }

    public boolean readBoolean() {
        if (buffer[offset] != 0x01) {
            throw new IllegalArgumentException("Boolean type ID (0x01) expected.");
        }
        offset++;
        return buffer[offset++] == 0x01;
    }

    public double readNumber() {
        if (buffer[offset] != 0x00) {
            throw new IllegalArgumentException("Number type ID (0x00) expected.");
        }
        offset++;
        ByteBuffer bb = ByteBuffer.wrap(buffer, offset, 8);
        double result = bb.getDouble();
        offset += 8;
        return result;
    }

    public Map<String, Object> readObject() {
        if (buffer[offset] != 0x03) {
            throw new IllegalArgumentException("Object type ID (0x03) expected.");
        }
        Map<String, Object> result = new HashMap<String, Object>();
        offset++;
        do {
            int length = (buffer[offset] & 0xff) << 8 | (buffer[offset + 1] & 0xff);
            String key = null;
            try {
                key = new String(buffer, offset + 2, length, "UTF-8");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            offset += 2 + length;
            //System.err.println("k: " + key + ", o: " + offset);
            if (length == 0) {
                if (buffer[offset] != 0x09) {
                    throw new IllegalArgumentException("Object-end type ID (0x09) expected.");
                }
                offset++;
                break;
            }
            Object value = readMixed();
            result.put(key, value);
        } while (true);
        return result;
    }
    
    public Map<String, Object> readEArray() {
        if (buffer[offset] != 0x08) {
            throw new IllegalArgumentException("ECMA array type ID (0x08) expected.");
        }
        Map<String, Object> result = new HashMap<String, Object>();
        offset++;
        int size = (buffer[offset++] & 0xff) << 24 | (buffer[offset++] & 0xff) << 16 | (buffer[offset++] & 0xff) << 8 | (buffer[offset++] & 0xff);
        do {
            int length = (buffer[offset] & 0xff) << 8 | (buffer[offset + 1] & 0xff);
            String key = null;
            try {
                key = new String(buffer, offset + 2, length, "UTF-8");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            offset += 2 + length;
            if (length == 0) {
                if (buffer[offset] != 0x09) {
                    throw new IllegalArgumentException("Object-end type ID (0x09) expected.");
                }
                offset++;
                break;
            }
            Object value = readMixed();
            result.put(key, value);
        } while (true);
        return result;
    }

    public Object readMixed() {
        int type = buffer[offset] & 0xff;
        if (type == 0x00) {
            return readNumber();
        } else if (type == 0x01) {
            return readBoolean();
        } else if (type == 0x02) {
            return readString();
        } else if (type == 0x03) {
            return readObject();
        } else if (type == 0x08) {
            return readEArray();
        } else if (type == 0x05) {
            offset++;
            return null;
        } else {
            throw new UnsupportedOperationException("Unknown type ID: " + type);
        }
    }
    
}
