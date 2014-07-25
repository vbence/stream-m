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

package org.czentral.minirtmp;

import java.io.OutputStream;
import org.czentral.incubator.streamm.HexDump;
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
