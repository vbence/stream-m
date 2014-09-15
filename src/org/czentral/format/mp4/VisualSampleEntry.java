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

import java.util.LinkedList;
import java.util.List;
import org.czentral.data.binary.annotation.BitField;
import org.czentral.data.binary.annotation.BitTable;

/**
 *
 * @author Varga Bence
 */
@BitTable(propOrder = {"preDefined1"
    , "reserved1"
    , "preDefined2"
    , "width"
    , "height"
    , "horizontalResolution"
    , "verticalResolution"
    , "reserved2"
    , "frameCount"
    , "compressorName"
    , "depth"
    , "preDefined3"
    , "content"})
public class VisualSampleEntry extends SampleEntry {
    
    @BitField(bitLength = 16)
    private final int preDefined1 = 0;

    @BitField(bitLength = 16)
    private final int reserved1 = 0;

    @BitField(bitLength = 32)
    private final int[] preDefined2 = {0, 0, 0};
    
    private int width;
    
    private int height;
    
    private long horizontalResolution = 0x00480000;
    
    private long verticalResolution = 0x00480000;
    
    @BitField(bitLength = 32)
    private final int reserved2 = 0;
    
    private int frameCount = 1;
    
    private String compressorName = "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000";
    
    private int depth = 0x0018;

    @BitField(bitLength = 16)
    private final int preDefined3 = -1;
    
    protected List<Box> content = new LinkedList<>();
    
    
    public VisualSampleEntry(String codingname) {
        super(codingname);
    }

    @BitField(bitLength = 16)
    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    @BitField(bitLength = 16)
    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @BitField(bitLength = 32)
    public long getHorizontalResolution() {
        return horizontalResolution;
    }

    public void setHorizontalResolution(long horizontalResolution) {
        this.horizontalResolution = horizontalResolution;
    }

    @BitField(bitLength = 32)
    public long getVerticalResolution() {
        return verticalResolution;
    }

    public void setVerticalResolution(long verticalResolution) {
        this.verticalResolution = verticalResolution;
    }

    @BitField(bitLength = 16)
    public int getFrameCount() {
        return frameCount;
    }

    public void setFrameCount(int frameCount) {
        this.frameCount = frameCount;
    }

    // Note: 32 BYTES (not bits)
    @BitField(byteLength = 32)
    public String getCompressorName() {
        return compressorName;
    }

    public void setCompressorName(String compressorName) {
        this.compressorName = compressorName;
    }

    @BitField(bitLength = 16)
    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }
    
    @BitField
    public List<Box> getContent() {
        return content;
    }

    public void setContent(List<Box> content) {
        this.content = content;
    }
    
}
