/*
 * This file is part of the "stream-m" software. An HTML5 compatible live
 * streaming and broadcasting server.
 * Copyright (C) 2014 Varga Bence (vbence@czentral.org)
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
 * @author Varga Bence (vbence@czentral.org)
 */
@BitTable(propOrder = {"referenceType"
        , "referencedSize"
        , "subsegmentDuration"
        , "startsWithSAP"
        , "sapType"
        , "sapDeltaTime"})
public class SegmentReference {
    
    private int referenceType;
    
    private int referencedSize;
    private int subsegmentDuration;

    private int startsWithSAP;
    
    private int sapType;

    private int sapDeltaTime;
    

    public SegmentReference() {
    }

    
    @BitField(bitLength = 1)
    public int getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(int referenceType) {
        this.referenceType = referenceType;
    }

    @BitField(bitLength = 31)
    public int getReferencedSize() {
        return referencedSize;
    }

    public void setReferencedSize(int referencedSize) {
        this.referencedSize = referencedSize;
    }

    @BitField(bitLength = 32)
    public int getSubsegmentDuration() {
        return subsegmentDuration;
    }

    public void setSubsegmentDuration(int subsegmentDuration) {
        this.subsegmentDuration = subsegmentDuration;
    }

    @BitField(bitLength = 1)
    public int getStartsWithSAP() {
        return startsWithSAP;
    }

    public void setStartsWithSAP(int startsWithSAP) {
        this.startsWithSAP = startsWithSAP;
    }

    @BitField(bitLength = 3)
    public int getSapType() {
        return sapType;
    }

    public void setSapType(int sapType) {
        this.sapType = sapType;
    }

    @BitField(bitLength = 28)
    public int getSapDeltaTime() {
        return sapDeltaTime;
    }

    public void setSapDeltaTime(int sapDeltaTime) {
        this.sapDeltaTime = sapDeltaTime;
    }
    
}
