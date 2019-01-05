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
 * @author Varga Bence
 */
@BitTable(propOrder = {"objectDescriptorID"
        , "urlFlag"
        , "includeInlineProfileLevelFlag"
        , "reserved"
        , "odProfileLevelIndication"
        , "sceneProfileLevelIndication"
        , "audioProfileLevelIndication"
        , "visualProfileLevelIndication"
        , "graphicsProfileLevelIndication"})
public class InitialObjectDescriptor extends BaseDescriptor {
    
    protected int objectDescriptorID;
    
    @BitField(bitLength = 1)
    protected final int urlFlag = 0;

    @BitField(bitLength = 1)
    protected final int includeInlineProfileLevelFlag = 0;
    
    @BitField(bitLength = 4)
    protected final int reserved = 0b1111;
    
    protected byte odProfileLevelIndication = (byte)0xff;
    
    protected byte sceneProfileLevelIndication = (byte)0xff;

    protected byte audioProfileLevelIndication = (byte)0xff;

    protected byte visualProfileLevelIndication = (byte)0xff;
    
    protected byte graphicsProfileLevelIndication = (byte)0xff;
    
    
    public InitialObjectDescriptor() {
        super((byte)0x10);
    }

    
    @BitField(bitLength = 10)
    public int getObjectDescriptorID() {
        return objectDescriptorID;
    }

    public void setObjectDescriptorID(int objectDescriptorID) {
        this.objectDescriptorID = objectDescriptorID;
    }

    @BitField(bitLength = 8)
    public byte getOdProfileLevelIndication() {
        return odProfileLevelIndication;
    }

    public void setOdProfileLevelIndication(byte odProfileLevelIndication) {
        this.odProfileLevelIndication = odProfileLevelIndication;
    }

    @BitField(bitLength = 8)
    public byte getSceneProfileLevelIndication() {
        return sceneProfileLevelIndication;
    }

    public void setSceneProfileLevelIndication(byte sceneProfileLevelIndication) {
        this.sceneProfileLevelIndication = sceneProfileLevelIndication;
    }

    @BitField(bitLength = 8)
    public byte getAudioProfileLevelIndication() {
        return audioProfileLevelIndication;
    }

    public void setAudioProfileLevelIndication(byte audioProfileLevelIndication) {
        this.audioProfileLevelIndication = audioProfileLevelIndication;
    }

    @BitField(bitLength = 8)
    public byte getVisualProfileLevelIndication() {
        return visualProfileLevelIndication;
    }

    public void setVisualProfileLevelIndication(byte visualProfileLevelIndication) {
        this.visualProfileLevelIndication = visualProfileLevelIndication;
    }

    @BitField(bitLength = 8)
    public byte getGraphicsProfileLevelIndication() {
        return graphicsProfileLevelIndication;
    }

    public void setGraphicsProfileLevelIndication(byte graphicsProfileLevelIndication) {
        this.graphicsProfileLevelIndication = graphicsProfileLevelIndication;
    }
    
}
