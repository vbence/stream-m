/*
 * This file is part of the "stream-m" software. An HTML5 compatible live
 * streaming and broadcasting server.
 * Copyright (C) 2016 bence
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
package org.czentral.format.flv;

import java.nio.ByteBuffer;

/**
 *
 * @author bence
 */
public class VideoTag {
    
    private FrameType frameType;
    private Codec codec;
    private AvcType avcType;
    private int compositiontimeOffset = 0;
        
    public enum FrameType {
        KEYFRAME(1),
        INTER(2),
        DISPOSABLE_INTER(3),
        GENERATED_KEYFRAME(4),
        VIDEO_COMMAND(5);
        
        private final int value;
        
        private FrameType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
    
    private static final FrameType[] FRAME_TYPE_DICT;
    
    public enum Codec {
        JPEG(1),
        SORENSON_H263(2),
        SCREEN_VIDEO(3),
        ON2_VP6(4),
        ON2_VP6_ALPHA(5),
        SCREEN_VIDEO_V2(6),
        AVC(7);
        
        private final int value;
        
        private Codec(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
    
    private static final Codec[] CODEC_DICT;
    
    public enum AvcType {
        SEQUENCE_HEADER(0),
        AVC_NALU(1),
        EOS(2);
        
        private final int value;
        
        private AvcType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
    
    private static final AvcType[] AVC_TYPE_DICT;
    
    static {
        FRAME_TYPE_DICT = new FrameType[16];
        for (FrameType o: FrameType.values()) {
            FRAME_TYPE_DICT[o.getValue()] = o;
        }
        
        CODEC_DICT = new Codec[16];
        for (Codec o: Codec.values()) {
            CODEC_DICT[o.getValue()] = o;
        }
        
        AVC_TYPE_DICT = new AvcType[16];
        for (AvcType o: AvcType.values()) {
            AVC_TYPE_DICT[o.getValue()] = o;
        }
    }

    public FrameType getFrameType() {
        return frameType;
    }

    public Codec getCodec() {
        return codec;
    }

    public AvcType getAvcType() {
        return avcType;
    }

    public int getCompositiontimeOffset() {
        return compositiontimeOffset;
    }
    
    public int getLength() {
        return codec == Codec.AVC ? 5 : 1;
    }
    
    public static VideoTag parse(ByteBuffer bb) {
        VideoTag result = new VideoTag();
        
        int typeAndCodec = bb.get();
        
        int frameTypeId = (typeAndCodec & 0x0f) >> 4;
        result.frameType = FRAME_TYPE_DICT[frameTypeId];
        
        int codecId = typeAndCodec & 0x0f;
        result.codec = CODEC_DICT[codecId];
        
        if (result.codec == Codec.AVC) {
            
            int avcTypeId = bb.get();
            result.avcType = AVC_TYPE_DICT[avcTypeId];
            
            int cpoHigh = bb.get();
            int cpoLow = bb.getShort();
            result.compositiontimeOffset = (cpoHigh &0xff) << 16 | cpoLow;
        }
        
        return result;
    }
    
}
