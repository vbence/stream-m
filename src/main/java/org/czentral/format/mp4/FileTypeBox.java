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
@BitTable(propOrder = {"majorBrand", "minorVersion", "compatibleBrands"})
public class FileTypeBox extends Box {
    
    protected String majorBrand;

    protected int minorVersion;
    
    protected String[] compatibleBrands;

    public FileTypeBox(String majorBrand, int minorVersion, String[] compatibleBrands) {
        super("ftyp");
        this.majorBrand = majorBrand;
        this.minorVersion = minorVersion;
        this.compatibleBrands = compatibleBrands;
    }
    
    @BitField(bitLength = 32)
    public String getMajorBrand() {
        return majorBrand;
    }

    public void setMajorBrand(String majorBrand) {
        this.majorBrand = majorBrand;
    }

    @BitField(bitLength = 32)
    public int getMinorVersion() {
        return minorVersion;
    }

    public void setMinorVersion(int minorVersion) {
        this.minorVersion = minorVersion;
    }

    @BitField(bitLength = 32)
    public String[] getCompatibleBrands() {
        return compatibleBrands;
    }

    public void setCompatibleBrands(String[] compatibleBrands) {
        this.compatibleBrands = compatibleBrands;
    }
    
    
    
    
    
    
}
