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
@BitTable(propOrder = {"reserved1"
        , "sampleDependsOn"
        , "sampleIsDependedOn"
        , "sampleHasRedundancy"
        , "samplePaddingValue"
        , "sampleIsDifferenceSample"
        , "sampleDegradationPriority"})
public class SampleFlags {
    
    public static final int DEPENDS_ON_UNKNOWN = 0;
    public static final int DEPENDS_ON_OTHERS = 1;
    public static final int DEPENDS_ON_NO_OTHERS = 2;
    
    @BitField(bitLength = 6)
    public final int reserved1 = 0;
    
    private int sampleDependsOn;
    
    private int sampleIsDependedOn;
    
    private int sampleHasRedundancy;
    
    private int samplePaddingValue;
    
    private int sampleIsDifferenceSample;
    
    private int sampleDegradationPriority;
    

    @BitField(bitLength = 2)
    public int getSampleDependsOn() {
        return sampleDependsOn;
    }

    public void setSampleDependsOn(int sampleDependsOn) {
        this.sampleDependsOn = sampleDependsOn;
    }

    @BitField(bitLength = 2)
    public int getSampleIsDependedOn() {
        return sampleIsDependedOn;
    }

    public void setSampleIsDependedOn(int sampleIsDependedOn) {
        this.sampleIsDependedOn = sampleIsDependedOn;
    }

    @BitField(bitLength = 2)
    public int getSampleHasRedundancy() {
        return sampleHasRedundancy;
    }

    public void setSampleHasRedundancy(int sampleHasRedundancy) {
        this.sampleHasRedundancy = sampleHasRedundancy;
    }

    @BitField(bitLength = 3)
    public int getSamplePaddingValue() {
        return samplePaddingValue;
    }

    public void setSamplePaddingValue(int samplePaddingValue) {
        this.samplePaddingValue = samplePaddingValue;
    }

    @BitField(bitLength = 1)
    public int getSampleIsDifferenceSample() {
        return sampleIsDifferenceSample;
    }

    public void setSampleIsDifferenceSample(int sampleIsDifferenceSample) {
        this.sampleIsDifferenceSample = sampleIsDifferenceSample;
    }

    @BitField(bitLength = 16)
    public int getSampleDegradationPriority() {
        return sampleDegradationPriority;
    }

    public void setSampleDegradationPriority(int sampleDegradationPriority) {
        this.sampleDegradationPriority = sampleDegradationPriority;
    }

}
