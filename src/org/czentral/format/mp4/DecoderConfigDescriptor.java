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

import java.util.LinkedList;
import java.util.List;
import org.czentral.data.binary.annotation.BitField;
import org.czentral.data.binary.annotation.BitTable;

/**
 *
 * @author Varga Bence (vbence@czentral.org)
 */
@BitTable(propOrder = {"objectTypeIndication"
        , "streamType"
        , "upStream"
        , "reserved"
        , "bufferSizeDB"
        , "maxBitrate"
        , "avgBitrate"
        , "decoderSpecificInfo"})
public class DecoderConfigDescriptor extends BaseDescriptor {
    
    public static final int PROFILE_AAC_MAIN = 0x40;

    public static final int STREAM_TYPE_AUDIO = 0x05;

    private int objectTypeIndication;
    private int streamType;
    private int upStream;

    @BitField(bitLength = 1)
    private final int reserved = 1;
    
    private int bufferSizeDB;
    private int maxBitrate;
    private int avgBitrate;

    private List<DecoderSpecificInfo> decoderSpecificInfo = new LinkedList<>();
    

    public DecoderConfigDescriptor() {
        super((byte)0x04);
    }

    
    @BitField(bitLength = 8)
    public int getObjectTypeIndication() {
        return objectTypeIndication;
    }

    public void setObjectTypeIndication(int objectTypeIndication) {
        this.objectTypeIndication = objectTypeIndication;
    }

    @BitField(bitLength = 6)
    public int getStreamType() {
        return streamType;
    }

    public void setStreamType(int streamType) {
        this.streamType = streamType;
    }

    @BitField(bitLength = 1)
    public int getUpStream() {
        return upStream;
    }

    public void setUpStream(int upStream) {
        this.upStream = upStream;
    }

    @BitField(bitLength = 24)
    public int getBufferSizeDB() {
        return bufferSizeDB;
    }

    public void setBufferSizeDB(int bufferSizeDB) {
        this.bufferSizeDB = bufferSizeDB;
    }

    @BitField(bitLength = 32)
    public int getMaxBitrate() {
        return maxBitrate;
    }

    public void setMaxBitrate(int maxBitrate) {
        this.maxBitrate = maxBitrate;
    }

    @BitField(bitLength = 32)
    public int getAvgBitrate() {
        return avgBitrate;
    }

    public void setAvgBitrate(int avgBitrate) {
        this.avgBitrate = avgBitrate;
    }

    @BitField
    public List<DecoderSpecificInfo> getDecoderSpecificInfo() {
        return decoderSpecificInfo;
    }

    public void setDecoderSpecificInfo(List<DecoderSpecificInfo> decoderSpecificInfo) {
        this.decoderSpecificInfo = decoderSpecificInfo;
    }
    
}
