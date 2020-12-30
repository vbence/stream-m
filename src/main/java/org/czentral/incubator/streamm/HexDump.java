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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author bence
 */
public class HexDump {

    public static void displayHex(byte[] buffer, int startOffset, int maxLength) {
        System.out.println(prettyPrintHex(buffer, startOffset, maxLength));
    }

    public static String prettyPrintHex(byte[] buffer, int startOffset, int maxLength) {
        final int LINE_LENGTH = 16;
        char[] digits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        StringBuilder sb = new StringBuilder(4096);
        int offset = startOffset;
        while (offset < startOffset + maxLength) {
            sb.append(String.format("%08X:  ", offset));
            int segmentLength = Math.min(LINE_LENGTH, startOffset + maxLength - offset);
            for (int i = 0; i < segmentLength; i++) {
                if (i > 0) {
                    sb.append(i % 4 == 0 ? '-' : ' ');
                }
                sb.append(digits[(buffer[offset + i] >> 4) & 0x0f]);
                sb.append(digits[(buffer[offset + i]) & 0x0f]);
                //String digit = new String("0" + Integer.toHexString(buffer[offset + i] & 0xff));
                //s += " " + digit.substring(digit.length() - 2);
            }
            for (int i = 0; i < LINE_LENGTH - segmentLength + 1; i++) {
                sb.append("   ");
            }
            for (int i = 0; i < segmentLength; i++) {
                int num = buffer[offset + i] & 0xff;
                sb.append(num > 13 ? (char) num : '.');
            }
            sb.append('\n');
            offset += segmentLength;
        }
        return sb.toString();
    }
    
}
