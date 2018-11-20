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
@BitTable(propOrder = {"trackID"
        , "defaultSampleDescriptionIndex"
        , "defaultSampleDuration"
        , "defaultSampleSize"
        , "defaultSampleFlags"})
public class TrackExtendsBox extends FullBox {

    protected int trackID;
    
    protected int defaultSampleDescriptionIndex;
    
    protected int defaultSampleDuration;
    
    protected int defaultSampleSize;
    
    protected SampleFlags defaultSampleFlags;

    public TrackExtendsBox() {
        super("trex", 0, 0);
        defaultSampleFlags = new SampleFlags();
    }
    
    @BitField(bitLength = 32)
    public int getTrackID() {
        return trackID;
    }

    public void setTrackID(int trackID) {
        this.trackID = trackID;
    }

    @BitField(bitLength = 32)
    public int getDefaultSampleDescriptionIndex() {
        return defaultSampleDescriptionIndex;
    }

    public void setDefaultSampleDescriptionIndex(int defaultSampleDescriptionIndex) {
        this.defaultSampleDescriptionIndex = defaultSampleDescriptionIndex;
    }

    @BitField(bitLength = 32)
    public int getDefaultSampleDuration() {
        return defaultSampleDuration;
    }

    public void setDefaultSampleDuration(int defaultSampleDuration) {
        this.defaultSampleDuration = defaultSampleDuration;
    }

    @BitField(bitLength = 32)
    public int getDefaultSampleSize() {
        return defaultSampleSize;
    }

    public void setDefaultSampleSize(int defaultSampleSize) {
        this.defaultSampleSize = defaultSampleSize;
    }

    @BitField(bitLength = 32)
    public SampleFlags getDefaultSampleFlags() {
        return defaultSampleFlags;
    }

    public void setDefaultSampleFlags(SampleFlags defaultSampleFlags) {
        this.defaultSampleFlags = defaultSampleFlags;
    }

}
