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
