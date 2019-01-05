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
