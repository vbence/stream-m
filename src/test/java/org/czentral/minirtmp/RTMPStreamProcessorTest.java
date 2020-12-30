/*
 * Copyright 2020 Bence Varga
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

package org.czentral.minirtmp;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class RTMPStreamProcessorTest {

    @Test
    public void process() throws Exception {
        ResourceLimit limit = new ResourceLimit();
        limit.assemblyBufferCount = 100;
        limit.assemblyBufferSize = 200 * 1024;
        limit.chunkStreamCount = 100;

        String pathname = "rtmp-in.pcap";
        byte[] buffer = loadFile(pathname);
        int start = 0xc5f;
        int end = 0x16c7 + 0x28;

        for (int i=start; i<end; i++) {
            TestChunkProcessor cp = new TestChunkProcessor();
            RTMPStreamProcessor o = new RTMPStreamProcessor(limit, cp);
            try {
                int round1 = o.process(buffer, start, i - start);
                System.out.print("i: " + String.format("%x", i) + ", c: " + cp.counter);
                o.process(buffer, start + round1, end - (start + round1));
                System.out.println(", c2: " + cp.counter);
            } catch (Exception e) {
                System.err.println("i: " + String.format("%x", i));
                throw e;
            }
        }
        System.out.println(buffer.length);
    }

    private byte[] loadFile(String pathname) throws IOException {
        //File file = new File(pathname);
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(pathname).getFile());
        FileInputStream fis = new FileInputStream(file);
        byte[] buffer = new byte[(int)file.length()];
        int offset = 0;
        int read = 0;
        while (offset < buffer.length) {
            read = fis.read(buffer, offset, buffer.length - offset);
            offset += read;
        }
        return buffer;
    }

    class TestChunkProcessor implements ChunkProcessor {

        int counter = 0;
        @Override
        public boolean alive() {
            return true;
        }

        @Override
        public void processChunk(MessageInfo mi, byte[] buffer, int payloadOffset, int payloadLength) {
            counter++;
        }
    }
}