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
