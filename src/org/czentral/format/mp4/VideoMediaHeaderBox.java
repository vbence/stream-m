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
@BitTable(propOrder = {"graphicsMode"
        , "opcolor"})
public class VideoMediaHeaderBox extends FullBox {
    
    private int graphicsMode;

    private int[] opcolor = {0, 0, 0};

    
    public VideoMediaHeaderBox() {
        super("vmhd", 0, 1);
    }
    
    @BitField(bitLength = 16)
    public int getGraphicsMode() {
        return graphicsMode;
    }

    public void setGraphicsMode(int graphicsMode) {
        this.graphicsMode = graphicsMode;
    }

    @BitField(bitLength = 16)
    public int[] getOpcolor() {
        return opcolor;
    }

    public void setOpcolor(int[] opcolor) {
        this.opcolor = opcolor;
    }
    
}
