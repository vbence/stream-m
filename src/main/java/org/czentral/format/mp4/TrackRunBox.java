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

package org.czentral.format.mp4;

import org.czentral.data.binary.ByteArraySlice;
import org.czentral.data.binary.annotation.BitField;
import org.czentral.data.binary.annotation.BitTable;

/**
 *
 * @author Varga Bence
 */
@BitTable(propOrder = {"sampleCount"
        , "dataOffset"
        , "firstSampleFlags"
        , "data"})
public class TrackRunBox extends FullBox {
    
    public static final int FLAG_FIRST_SAMPLE_FLAGS_PRESENT = 0x004;
    public static final int FLAG_DATA_OFFSET_PRESENT = 0x001;
    public static final int FLAG_SAMPLE_SIZE_PRESENT = 0x200;
    public static final int FLAG_SAMPLE_COMPOSITION_TIME_OFFSET_PRESENT = 0x800;
    
    protected long sampleCount;
    
    protected int dataOffset;
    
    protected int firstSampleFlags;
    
    protected ByteArraySlice data;
    
    
    public TrackRunBox() {
        super("trun", 0, FLAG_DATA_OFFSET_PRESENT | FLAG_FIRST_SAMPLE_FLAGS_PRESENT);
    }
    
    @BitField(bitLength = 32)
    public long getSampleCount() {
        return sampleCount;
    }

    public void setSampleCount(long sampleCount) {
        this.sampleCount = sampleCount;
    }

    @BitField(bitLength = 32)
    public int getDataOffset() {
        return dataOffset;
    }

    public void setDataOffset(int dataOffset) {
        this.dataOffset = dataOffset;
    }

    @BitField(bitLength = 32)
    public int getFirstSampleFlags() {
        return firstSampleFlags;
    }

    public void setFirstSampleFlags(int firstSampleFlags) {
        this.firstSampleFlags = firstSampleFlags;
    }
    
    @BitField
    public ByteArraySlice getData() {
        return data;
    }

    public void setData(ByteArraySlice data) {
        this.data = data;
    }

}
