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
    public static final int FLAG_SAMPLE_DURATION_PRESENT = 0x100;
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
