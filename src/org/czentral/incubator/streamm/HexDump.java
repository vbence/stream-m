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
            int segmentLength = Math.min(LINE_LENGTH, startOffset + maxLength - offset);
            for (int i = 0; i < segmentLength; i++) {
                sb.append(digits[(buffer[offset + i] >> 4) & 0x0f]);
                sb.append(digits[(buffer[offset + i]) & 0x0f]);
                sb.append(' ');
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
