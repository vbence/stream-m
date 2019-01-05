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
@BitTable(propOrder = {"esID"
        , "streamDependenceFlag"
        , "urlFlag"
        , "ocrStreamFlag"
        , "streamPriority"
        , "decoderConfigDescriptor"
        , "slConfigDescriptor"})
public class ESDescriptor extends BaseDescriptor {

    private int esID;
    
    @BitField(bitLength = 1)
    private final int streamDependenceFlag = 0;
    
    @BitField(bitLength = 1)
    private final int urlFlag = 0;
    
    @BitField(bitLength = 1)
    private final int ocrStreamFlag = 0;
    
    private int streamPriority;
    
    private DecoderConfigDescriptor decoderConfigDescriptor;
    
    private SLConfigDescriptor slConfigDescriptor;

    public ESDescriptor() {
        super((byte)0x03);
    }

    @BitField(bitLength = 16)
    public int getEsID() {
        return esID;
    }

    public void setEsID(int esID) {
        this.esID = esID;
    }

    @BitField(bitLength = 5)
    public int getStreamPriority() {
        return streamPriority;
    }

    public void setStreamPriority(int streamPriority) {
        this.streamPriority = streamPriority;
    }

    @BitField
    public DecoderConfigDescriptor getDecoderConfigDescriptor() {
        return decoderConfigDescriptor;
    }

    public void setDecoderConfigDescriptor(DecoderConfigDescriptor decoderConfigDescriptor) {
        this.decoderConfigDescriptor = decoderConfigDescriptor;
    }

    @BitField
    public SLConfigDescriptor getSlConfigDescriptor() {
        return slConfigDescriptor;
    }

    public void setSlConfigDescriptor(SLConfigDescriptor slConfigDescriptor) {
        this.slConfigDescriptor = slConfigDescriptor;
    }
    
}
