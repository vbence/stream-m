/*
	This file is part of "stream.m" software, a video broadcasting tool
	compatible with Google's WebM format.
	Copyright (C) 2011 Varga Bence

	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.czentral.minihttp;

import java.util.*;

/**
 * This class builds a multimap where more than one value can be associated to
 * a key. 
 */
class MultimapBuilder {
	
	private Map<String, Vector<String>> buffer = new HashMap<String, Vector<String>>(10);
	
	/**
	 * Adds a key-value pair to the multimap.
	 * @param key The key.
	 * @param value A value associated to the given key.
	 */
	public void add(String key, String value) {
		Vector<String> v = buffer.get(key);
		if (v == null) {
			v = new Vector<String>(10);
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
	 * will be needed long enogh for the GC to actually free up the memory used
	 * by the working map.
	 */
	public Map<String, String[]> getCompactMap() {
		Map<String, String[]> result = new HashMap<String, String[]>(buffer.size());
		Iterator<Map.Entry<String, Vector<String>>> iterator = buffer.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, Vector<String>> entry = iterator.next();
			Vector<String> v = entry.getValue();
			String[] values = new String[v.size()];
			for (int i=0; i<v.size(); i++)
				values[i] = v.get(i);
			result.put(entry.getKey(), values);
		}
		return result;
	}
}