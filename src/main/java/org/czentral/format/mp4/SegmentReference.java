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
