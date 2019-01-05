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
@BitTable(propOrder = {"majorBrand", "minorVersion", "compatibleBrands"})
public class SegmentTypeBox extends Box {
    
    protected String majorBrand;

    protected int minorVersion;
    
    protected String[] compatibleBrands;

    public SegmentTypeBox(String majorBrand, int minorVersion, String[] compatibleBrands) {
        super("styp");
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
