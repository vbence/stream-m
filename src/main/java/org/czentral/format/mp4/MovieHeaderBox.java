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
@BitTable(propOrder = {"creationTime"
        , "modificationTime"
        , "timescale"
        , "duration"
        , "rate"
        , "volume"
        , "reserved1"
        , "reserved2"
        , "matrix"
        , "preDefined"
        , "nextTrackId"
        , "content"})
public class MovieHeaderBox extends FullBox {
    
    long creationTime;
    long modificationTime;
    long timescale;
    long duration;
    
    int rate = 0x10000;
    int volume = 0x100;
    
    @BitField(bitLength = 16)
    public final int reserved1 = 0;
    
    @BitField(bitLength = 32)
    public final int[] reserved2 = {0, 0};
    
    int[] matrix = {0x10000, 0, 0, 0, 0x10000, 0, 0, 0, 0x40000000};
    
    @BitField(bitLength = 32)
    public final int[] preDefined = {0, 0, 0, 0, 0, 0};
    
    int nextTrackId;
    
    List<Box> content = new LinkedList<>();

    public MovieHeaderBox(int version, long creationTime, long modificationTime, long timescale, long duration, int nextTrackId) {
        this(version);
        this.creationTime = creationTime;
        this.modificationTime = modificationTime;
        this.timescale = timescale;
        this.duration = duration;
        this.nextTrackId = nextTrackId;
    }

    public MovieHeaderBox(int version) {
        super("mvhd", version, 0);
        if (version != 0) {
            throw new IllegalArgumentException("Only version 0 is supported.");
        }
    }
    
    public MovieHeaderBox() {
        super("mvhd", 0, 0);
    }

    @BitField(bitLength = 32)
    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    @BitField(bitLength = 32)
    public long getModificationTime() {
        return modificationTime;
    }

    public void setModificationTime(long modificationTime) {
        this.modificationTime = modificationTime;
    }

    @BitField(bitLength = 32)
    public long getTimescale() {
        return timescale;
    }

    public void setTimescale(long timescale) {
        this.timescale = timescale;
    }

    @BitField(bitLength = 32)
    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    @BitField(bitLength = 32)
    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    @BitField(bitLength = 16)
    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    @BitField(bitLength = 32)
    public int[] getMatrix() {
        return matrix;
    }

    public void setMatrix(int[] matrix) {
        this.matrix = matrix;
    }

    @BitField(bitLength = 32)
    public int getNextTrackId() {
        return nextTrackId;
    }

    public void setNextTrackId(int nextTrackId) {
        this.nextTrackId = nextTrackId;
    }
    
    @BitField
    public List<Box> getContent() {
        return content;
    }

    public void setContent(List<Box> content) {
        this.content = content;
    }
    
}
