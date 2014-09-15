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
