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
package org.czentral.minihttp;

import java.util.*;

/**
 * This class builds a multimap where more than one value can be associated to
 * a key. 
 */
class MultimapBuilder {
    
    private Map<String, List<String>> buffer = new HashMap<>(10);
    
    /**
     * Adds a key-value pair to the multimap.
     * @param key The key.
     * @param value A value associated to the given key.
     */
    public void add(String key, String value) {
        List<String> v = buffer.get(key);
        if (v == null) {
            v = new LinkedList<String>();
            buffer.put(key, v);
        }
        v.add(value);
    }
    
    /**
     * Creates a new (multi)map where values are stored in arrays of strings
     * instead of vectors of strings. The advantage is a smaller memory
     * footprint and faster access, but the conversion process adds its own
     * overhead.
     * Use this if you are likely to access these values many times and they
     * will be needed long enough for the GC to actually free up the memory used
     * by the working map.
     */
    public Map<String, String[]> getCompactMap() {
        Map<String, String[]> result = new HashMap<String, String[]>(buffer.size());
        for (Map.Entry<String, List<String>> entry : buffer.entrySet()) {
                    List<String> v = entry.getValue();
                    String[] values = new String[v.size()];
                    for (int i=0; i<v.size(); i++)
                            values[i] = v.get(i);
                    result.put(entry.getKey(), values);
        }
        return result;
    }
}