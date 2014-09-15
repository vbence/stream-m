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

import org.czentral.data.binary.annotation.BitField;
import org.czentral.data.binary.annotation.BitTable;

/**
 *
 * @author Varga Bence
 */
@BitTable(propOrder = {"creationTime"
        , "modificationTime"
        , "trackID"
        , "reserved1"
        , "duration"
        , "reserved2"
        , "layer"
        , "alternateGroup"
        , "volume"
        , "reserved3"
        , "matrix"
        , "width"
        , "height"})
public class TrackHeaderBox extends FullBox {
    
    public static int FLAG_TRACK_ENABLED = 0x01;
    public static int FLAG_TRACK_IN_MOVIE = 0x02;
    public static int FLAG_TRACK_IN_PREVIEW = 0x04;
    
    long creationTime;
    long modificationTime;
    int trackID;
    
    @BitField(bitLength = 32)
    public final int reserved1 = 0;
    
    long duration;
    
    @BitField(bitLength = 32)
    private final int[] reserved2 = {0, 0};

    int layer = 0;
    int alternateGroup = 0;
    int volume = 0;
    
    @BitField(bitLength = 16)
    private final int reserved3 = 0;
    
    int[] matrix = {0x10000, 0, 0, 0, 0x10000, 0, 0, 0, 0x40000000};
    
    int width;
    int height;

    public TrackHeaderBox(int version) {
        super("tkhd", version, 0);
        if (version != 0) {
            throw new IllegalArgumentException("Only version 0 is supported.");
        }
    }
    
    public TrackHeaderBox() {
        this(0);
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
    public int getTrackID() {
        return trackID;
    }

    public void setTrackID(int trackID) {
        this.trackID = trackID;
    }

    @BitField(bitLength = 32)
    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    @BitField(bitLength = 16)
    public int getLayer() {
        return layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    @BitField(bitLength = 16)
    public int getAlternateGroup() {
        return alternateGroup;
    }

    public void setAlternateGroup(int alternateGroup) {
        this.alternateGroup = alternateGroup;
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
    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    @BitField(bitLength = 32)
    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
    
}
