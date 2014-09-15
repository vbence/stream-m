/*
 * This file is part of the "stream-m" software. An HTML5 compatible live
 * streaming and broadcasting server.
 * Copyright (C) 2014 Varga Bence
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
