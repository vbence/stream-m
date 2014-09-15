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
