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

import java.util.LinkedList;
import java.util.List;
import org.czentral.data.binary.annotation.BitField;
import org.czentral.data.binary.annotation.BitTable;

/**
 *
 * @author Varga Bence
 */
@BitTable(propOrder = {"reserved1"
    , "channelCount"
    , "sampleSize"
    , "preDefined1"
    , "reserved2"
    , "sampleRate"
    , "content"})
public class AudioSampleEntry extends SampleEntry {
    
    @BitField(bitLength = 32)
    private final int[] reserved1 = {0, 0};
    
    private int channelCount = 2;

    private int sampleSize = 16;
    
    @BitField(bitLength = 16)
    private final int preDefined1 = 0;
    
    @BitField(bitLength = 16)
    private final int reserved2 = 0;

    // not official default
    private long sampleRate = 44100 << 16;
    
    protected List<Box> content = new LinkedList<>();
    
    
    public AudioSampleEntry(String codingname) {
        super(codingname);
    }

    @BitField(bitLength = 16)
    public int getChannelCount() {
        return channelCount;
    }

    public void setChannelCount(int channelCount) {
        this.channelCount = channelCount;
    }

    @BitField(bitLength = 16)
    public int getSampleSize() {
        return sampleSize;
    }

    public void setSampleSize(int sampleSize) {
        this.sampleSize = sampleSize;
    }

    @BitField(bitLength = 32)
    public long getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(long sampleRate) {
        this.sampleRate = sampleRate;
    }
    
    @BitField
    public List<Box> getContent() {
        return content;
    }

    public void setContent(List<Box> content) {
        this.content = content;
    }
    
}
