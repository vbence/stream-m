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

import org.czentral.util.stream.Buffer;

public class MatroskaFragment implements MovieFragment {
    
    private final int INITIAL_CLUSTER_LENGTH = 9;
    private final int TIMECODE_LAST_OFFSET = 18;
    private final int CLUSTER_LENGTH_LAST_OFFSET = 8;
    private final byte[] clusterHead = {0x1F, 0x43, (byte)0xB6, 0x75, 0x08, 00, 00, 00, 00, 
            (byte)0xe7, (byte)0x88, 00, 00, 00, 00, 00, 00, 00, 00};

    private byte[] data = new byte[LIMIT_FRAME_MAXIMUM];
    private int dataLength = 0;
    private int clusterOffset = -1;
    
    private Buffer keyBuffer = null;
    
    public MatroskaFragment() {
    }
    
    public void openCluster(long timeCode) {

        if (clusterOffset != -1)
            closeCluster();
        
        System.arraycopy(clusterHead, 0, data, dataLength, clusterHead.length);
        clusterOffset = dataLength;
        dataLength += clusterHead.length;
        
        // saving timeCode
        int offset = clusterOffset + TIMECODE_LAST_OFFSET;
        long num;
        num = timeCode;
        while (num > 0) {
            data[offset--] = (byte)num;
            num >>= 8;
        }
    }
    
    public void closeCluster() {
        
        if (clusterOffset == -1)
            throw new RuntimeException("No open cluster.");
        
        // cluster length (including initial TimeCode element)
        int clusterLength = dataLength - clusterOffset - INITIAL_CLUSTER_LENGTH;
        
        // saving cluster length to the EBML element's header
        int offset = clusterOffset + CLUSTER_LENGTH_LAST_OFFSET;
        int num;
        num = clusterLength;
        while (num > 0) {
            data[offset--] = (byte)num;
            num >>= 8;
        }
        
        clusterOffset = -1;
    }
    
    public boolean hasCluster() {
        return clusterOffset != -1;
    }
    
    public void appendKeyBlock(byte[] buffer, int offset, int length, int keyframeOffset) {
        if (keyframeOffset > 0) {
            keyBuffer = new Buffer(data, dataLength + (keyframeOffset - offset), length - (keyframeOffset - offset));
        }
        appendBlock(buffer, offset, length);
    }
    
    public void appendBlock(byte[] buffer, int offset, int length) {
        if (data.length < dataLength + length)
            throw new RuntimeException("Buffer full");
        
        System.arraycopy(buffer, offset, data, dataLength, length);
        dataLength += length;
    }
    
    @Override
    public Buffer[] getBuffers() {
        Buffer[] result = { new Buffer(data, 0, dataLength) };
        return result;
    }
    
    @Override
    public int length() {
        return dataLength;
    }

    public Buffer getKeyBuffer() {
        return keyBuffer;
    }
    
}
