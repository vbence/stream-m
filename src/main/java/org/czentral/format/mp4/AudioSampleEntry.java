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
