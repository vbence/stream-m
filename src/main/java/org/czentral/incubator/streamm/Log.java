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

class Log {
    
    public static void displaySegment(byte[] buffer, int offset, int maxLength) {
        final int DISPLAY_MAX_LENGTH = 48;
        
        String s = "(" + maxLength + ")";
        for (int i=0; i<Math.min(DISPLAY_MAX_LENGTH, maxLength); i++) {
            String digit = "0" + Integer.toHexString(buffer[offset + i] & 0xff);
            s += " " + digit.substring(digit.length() - 2);
        }
        s += "  ";
        for (int i=0; i<Math.min(DISPLAY_MAX_LENGTH, maxLength); i++) {
            int num = buffer[offset + i] & 0xff;
            s += num > 13 ? (char)num : '.';
        }
        if (maxLength > DISPLAY_MAX_LENGTH)
            s += "  >>";
        
        System.out.println(s);
    }
    
    public static void reportChunk(byte[] buffer, int offset, int length) {
        
        int endOffset = offset + length;
        
        // first NAL Unit
        for (int i=offset; i<endOffset - 4; i++) {
            if (buffer[i] == 0 && buffer[i+1] == 1) {
                offset = i + 2;
                break;
            }
        }
        
        while (offset <= endOffset - 4) {
            int startOffset = offset;
            
            for (int i=offset; i<endOffset - 4; i++) {
                if (buffer[i + 3] == 1 && buffer[i] == 0 && buffer[i + 1] == 0 && buffer[i + 2] == 0) {
                    offset = i;
                    break;
                }
            }
            
            if (offset == startOffset) {
                Log.displaySegment(buffer, startOffset, endOffset - startOffset);
                offset = endOffset;
            } else {
                Log.displaySegment(buffer, startOffset, offset - startOffset);
            }

            offset += 4;
            
        }
    }

    public static void displayTime(long time) {
        float sec = time / 90000f;
        String s;
        s = "" + (sec % 60);
        sec /= 60;
        s = ((long)sec % 60) + ":" + s;
        sec /= 60;
        s = (long)sec + ":" + s;
        System.out.println(s);
    }
}
