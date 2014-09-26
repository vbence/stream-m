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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author bence
 */
public class ApplicationContext implements ChunkProcessor {
    
    protected boolean alive = true;
    
    protected Map<Integer, AssemblyBuffer> assemblyBuffers = new HashMap<>();
    
    protected OutputStream outputStream;
    
    protected ResourceLimit limit;
    
    protected int fcpublishTxID = 0;
    
    protected ApplicationLibrary library;
    
    protected ApplicationInstance applicationInstance;

    public ApplicationContext(OutputStream outputStream, ResourceLimit limit, ApplicationLibrary factory) {
        this.outputStream = outputStream;
        this.limit = limit;
        this.library = factory;
    }


    /**
     * Get the value of outputStream
     *
     * @return the value of outputStream
     */
    public OutputStream getOutputStream() {
        return outputStream;
    }

    /**
     * Set the value of outputStream
     *
     * @param outputStream new value of outputStream
     */
    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    /**
     * Get the value of limit
     *
     * @return the value of limit
     */
    public ResourceLimit getLimit() {
        return limit;
    }

    /**
     * Set the value of limit
     *
     * @param limit new value of limit
     */
    public void setLimit(ResourceLimit limit) {
        this.limit = limit;
    }

    
    @Override
    public void processChunk(MessageInfo mi, byte[] readBuffer, int payloadOffset, int payloadLength) {
        
        boolean fragmented = (mi.length > payloadLength);
        if (fragmented) {
            AssemblyBuffer assemblyBuffer = assemblyBuffers.get(mi.chunkStreamID);
            
            boolean newFragment = (mi.offset == 0);
            if (newFragment) {
                
                if (assemblyBuffer != null) {
                    throw new RuntimeException("Trying so start new fragment whith an other one in progress.");
                }
                
                if (mi.length > limit.assemblyBufferSize) {
                    throw new RuntimeException("Resource limit reached: message too large (" + mi.length + " > " + limit.assemblyBufferSize + ").");
                }
                
                if (assemblyBuffers.size() >= limit.assemblyBufferCount) {
                    throw new RuntimeException("Resource limit reached: too many messages to assemble.");
                }
                
                assemblyBuffer = new AssemblyBuffer(mi.length);
                assemblyBuffers.put(mi.chunkStreamID, assemblyBuffer);
            }
            
            System.arraycopy(readBuffer, payloadOffset, assemblyBuffer.array, mi.offset, payloadLength);

            boolean assembled = (mi.offset + payloadLength >= mi.length);
            if (assembled) {
                processMessage(mi, assemblyBuffer.array, 0, mi.length);
                assemblyBuffers.put(mi.chunkStreamID, null);
            }
            
        } else {
            byte[] resultBuffer = new byte[payloadLength];
            System.arraycopy(readBuffer, payloadOffset, resultBuffer, 0, payloadLength);
            processMessage(mi, resultBuffer, 0, resultBuffer.length);
        }
        //System.err.print(HexDump.prettyPrintHex(buffer, payloadOffset, Math.min(16, payloadLength)));
    }
    
    protected void processMessage(MessageInfo mi, byte[] buffer, int payloadOffset, int payloadLength) {
        //System.err.print(HexDump.prettyPrintHex(buffer, payloadOffset, payloadLength));
        
        int type = mi.type;
        AMFDecoder ac = new AMFDecoder(buffer, payloadOffset, payloadLength);
        
        if (type == 0x14) {
            RTMPCommand command = readCommand(ac);
            
            if (applicationInstance == null) {
                
                if (command.getName().equals("connect")) {

                    String appName = (String)command.getCommandObject().get("app");
                    try {
                        Application app = library.getApplication(appName);
                        applicationInstance = app.getInstance();
                    } catch (ApplicationNotFoundException e) {
                        AMFPacket response = new AMFPacket();
                        response.writeString("_error");
                        response.writeMixed(null);
                        response.writeMixed(null);
                        writeCommand(mi.chunkStreamID, response);
                        return;
                    }
                    applicationInstance.onConnect(this);

                    applicationInstance.invokeCommand(mi, command);

                } else {

                    AMFPacket response = new AMFPacket();
                    response.writeString("_error");
                    response.writeMixed(null);
                    response.writeMixed(null);
                    writeCommand(mi.chunkStreamID, response);
                }
            } else {
                applicationInstance.invokeCommand(mi, command);
            }
            
        } else if (type == 0x12) {
            RTMPCommand command = readData(ac);
            applicationInstance.onData(mi, command);
        } else if (type == 0x08 || type == 0x09) {
            applicationInstance.mediaChunk(mi, buffer, payloadOffset, payloadLength);
        }
        
    }
    
    protected RTMPCommand readCommand(AMFDecoder decoder) {
        String commandName = decoder.readString();
        double txid = decoder.readNumber();
        Map<String, Object> commandObject = (Map<String, Object>)decoder.readMixed();

        List<Object> arguments = new LinkedList<Object>();
        while (!decoder.eof()) {
            Object argument = decoder.readMixed();
            arguments.add(argument);
        }
        
        return new RTMPCommand(commandName, txid, commandObject, arguments);
    }
    
    protected RTMPCommand readData(AMFDecoder decoder) {
        List<Object> arguments = new LinkedList<Object>();
        while (!decoder.eof()) {
            Object argument = decoder.readMixed();
            arguments.add(argument);
        }
        
        return new RTMPCommand("", 0d, null, arguments);
    }
    
    public void writeCommand(int streamID, AMFPacket message) {
        byte[] header = new byte[12];
        header[0] = (byte)((streamID) & 0x3f);

        // length
        int responseLength = message.getLength();
        header[4] = (byte) ((responseLength >> 16) & 0xff);
        header[5] = (byte) ((responseLength >> 8) & 0xff);
        header[6] = (byte) ((responseLength) & 0xff);

        // message type
        header[7] = 0x14;

        // writing
        try {
            outputStream.write(header);
            outputStream.write(message.getBuffer(), 0, responseLength);
            outputStream.flush();
            //System.out.write(header);
            //System.out.write(response.getBuffer(), 0, responseLength);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private static class AssemblyBuffer {
        public static byte[] array;

        public AssemblyBuffer(int size) {
            array = new byte[size];
        }
    }
    
    
    @Override
    public boolean alive() {
        return alive;
    }
    
    public void terminate() {
        alive = false;
    }
    
}
