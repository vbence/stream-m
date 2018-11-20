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
package org.czentral.incubator.streamm;

/**
 *
 * @author Varga Bence (vbence@czentral.org)
 */
public class RMTPPayloadInfo {

    public static int FRAME_TYPE_KEY = 0x01;
    public static int FRAME_TYPE_INTER = 0x02;
    public static int FRAME_TYPE_DISPOSABLE = 0x03;
    public static int FRAME_TYPE_GENERATED_KEY = 0x04;
    public static int FRAME_TYPE_INFO_OR_COMMAND = 0x05;
    
    public static int PACKET_TYPE_AVC_SEQUENCE = 0x00;
    public static int PACKET_TYPE_AVC_NALU = 0x01;
    public static int PACKET_TYPE_AVC_END = 0x02;
    
    int frameType;
    
    int codecID;
    
    int type;
    
    int avcPacketType = -1;
    
    int compositionTime = 0;
    
    
    protected RMTPPayloadInfo() {
    }
    
    public void parsePacket(byte[] array, int offset, int length) {
        frameType = array[offset] >> 4 & 0x0f;
        codecID = array[offset] & 0x0f;
        offset++;
        
        if (codecID == 7) {
            avcPacketType = array[offset++] & 0xff;
        }
        
        if (codecID == 7) {
            compositionTime = array[offset++] << 16 | array[offset++] << 8 | array[offset++];
        }
    }
    
    
    
}
