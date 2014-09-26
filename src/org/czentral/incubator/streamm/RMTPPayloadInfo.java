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
