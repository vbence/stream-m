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
@BitTable(propOrder = {"reserved1"
        , "sampleDependsOn"
        , "sampleIsDependedOn"
        , "sampleHasRedundancy"
        , "samplePaddingValue"
        , "sampleIsDifferenceSample"
        , "sampleDegradationPriority"})
public class SampleFlags {
    
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
