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

package org.czentral.minirtmp;

import java.io.OutputStream;

import org.czentral.util.stream.Processor;

/**
 *
 * @author Varga Bence
 */
public class HandshakeProcessor implements Processor {
    
    protected OutputStream os;
    
    protected int bytesNeeded = 1537;
    
    protected Stage stage = Stage.ClientBlock;

    enum Stage {
        ClientBlock,
        ServerBlock,
        Finished;
    }
    
    public HandshakeProcessor(OutputStream os) {
        this.os = os;
    }

    @Override
    public boolean finished() {
        return stage == Stage.Finished;
    }

    @Override
    public int process(byte[] buffer, int offset, int length) {
        if (length < bytesNeeded) {
            return 0;
        }

        //System.err.println(HexDump.prettyPrintHex(buffer, offset, length));
        
        if (stage == Stage.ClientBlock) {
            int processed = bytesNeeded;
            
            byte[] response = new byte[1537];
            response[0] = 0x03;
            try {
                os.write(response);
                os.write(buffer, offset + 1, bytesNeeded - 1);
                os.flush();
            } catch (Exception e) {
                throw new RuntimeException("Unable to write handshake response.", e);
            }

            bytesNeeded = 1536;
            stage = Stage.ServerBlock;
            return processed;
            
        } else if (stage == Stage.ServerBlock) {
            int processed = bytesNeeded;
            
            try {
                byte[] setChunkSizeCommand = {02, 00, 00, 00, 00, 00, 04, 01, 00, 00, 00, 00, 00, (byte)0xff, (byte)0xff, (byte)0xff};
                os.write(setChunkSizeCommand);
                os.flush();
            } catch (Exception e) {
                throw new RuntimeException("Unable to send \"SetChunkSize\" command.", e);
            }

            bytesNeeded = 0;
            stage = Stage.Finished;
            return processed;
        }
        
        return 0;
    }
    
}
