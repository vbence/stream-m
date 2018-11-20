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
        , "timescale"
        , "duration"
        , "pad"
        , "language"
        , "preDefined"})
public class MediaHeaderBox extends FullBox {
    
    long creationTime;
    long modificationTime;
    long timescale;
    long duration;
    
    @BitField(bitLength = 1)
    private final int pad = 0;
    
    private int[] language = {'u' - 0x60, 'n' - 0x60, 'd' - 0x60};
    
    @BitField(bitLength = 16)
    public final int preDefined = 0;

    public MediaHeaderBox(int version) {
        super("mdhd", version, 0);
        if (version != 0) {
            throw new IllegalArgumentException("Only version 0 is supported.");
        }
    }
    
    public MediaHeaderBox() {
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

    @BitField(bitLength = 5)
    public int[] getLanguage() {
        return language;
    }

    public void setLanguage(int[] language) {
        this.language = language;
    }
    
}
