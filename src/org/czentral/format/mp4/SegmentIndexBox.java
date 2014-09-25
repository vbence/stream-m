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
@BitTable(propOrder = {"referenceID"
        , "timescale"
        , "earliestPresentationTime"
        , "firstOffset"
        , "reserved"
        , "referenceCount"
        , "references"})
public class SegmentIndexBox extends FullBox {
    
    private long referenceID;
    
    private long timescale;
    
    private long earliestPresentationTime;
    
    private long firstOffset;

    @BitField(bitLength = 16)
    private final int reserved = 0;
    
    private int referenceCount;
    
    private List<SegmentReference> references = new LinkedList<SegmentReference>();


    public SegmentIndexBox() {
        super("sidx", 0, 0);
    }

    
    @BitField(bitLength = 32)
    public long getReferenceID() {
        return referenceID;
    }

    public void setReferenceID(long referenceID) {
        this.referenceID = referenceID;
    }

    @BitField(bitLength = 32)
    public long getTimescale() {
        return timescale;
    }

    public void setTimescale(long timescale) {
        this.timescale = timescale;
    }

    @BitField(bitLength = 32)
    public long getEarliestPresentationTime() {
        return earliestPresentationTime;
    }

    public void setEarliestPresentationTime(long earliestPresentationTime) {
        this.earliestPresentationTime = earliestPresentationTime;
    }

    @BitField(bitLength = 32)
    public long getFirstOffset() {
        return firstOffset;
    }

    public void setFirstOffset(long firstOffset) {
        this.firstOffset = firstOffset;
    }

    @BitField(bitLength = 16)
    public int getReferenceCount() {
        return referenceCount;
    }

    public void setReferenceCount(int referenceCount) {
        this.referenceCount = referenceCount;
    }

    @BitField
    public List<SegmentReference> getReferences() {
        return references;
    }

    public void setReferences(List<SegmentReference> references) {
        this.references = references;
    }
    
}
