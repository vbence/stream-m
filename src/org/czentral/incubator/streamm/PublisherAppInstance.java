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

package org.czentral.incubator.streamm;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import org.czentral.data.binary.AnnotationMapSerializationContext;
import org.czentral.data.binary.BitBuffer;
import org.czentral.data.binary.SerializerFactoryImpl;
import org.czentral.data.binary.serializer.DescriptorInteger;
import org.czentral.data.binary.serializer.FixedInteger;
import org.czentral.data.binary.serializer.FixedString;
import org.czentral.data.binary.serializer.GeneralSerializer;
import org.czentral.minihttp.HTTPException;
import org.czentral.minirtmp.AMFPacket;
import org.czentral.minirtmp.ApplicationContext;
import org.czentral.minirtmp.MessageInfo;
import org.czentral.minirtmp.ApplicationInstance;
import org.czentral.minirtmp.RTMPCommand;

/**
 *
 * @author Varga Bence
 */
class PublisherAppInstance implements ApplicationInstance {
    
    private static final String HANDLER_NAME_SET_DATA_FRAME = "@setDataFrame";
    
    protected ApplicationContext context;

    protected Properties props;

    protected Map<String, ControlledStream> streams;
    
    protected String streamID;
    
    protected double fcpublishTxID;
    
    protected Map<String, Object> metaData;
    
    protected Mp4Header header = new Mp4Header();
    
    protected Map<Integer, Integer> streamToTrack = new HashMap<>();
    
    protected boolean streamStarted = false;

    
    public PublisherAppInstance(Properties props, Map<String, ControlledStream> streams) {
        this.props = props;
        this.streams = streams;
    }
    

    @Override
    public void onConnect(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public void invokeCommand(MessageInfo mi, RTMPCommand command) {
        System.err.println("cmd: " + command.getName() + " " + command.getArguments());
        
        if (command.getName().equals("connect")) {
            cmdConnect(mi, command);
        
        } else if (command.getName().equals("releaseStream")) {
            sendSuccess(mi, command);
        
        } else if (command.getName().equals("FCUnpublish")) {
            sendSuccess(mi, command);
            
        } else if (command.getName().equals("createStream")) {
            cmdCreateStream(mi, command);
        
        } else if (command.getName().equals("FCPublish")) {
            cmdFCPublish(mi, command);
        
        } else if (command.getName().equals("publish")) {
            cmdPublish(mi, command);
        
        } else {
            System.err.println("Unknown command: " + command.getName());
            sendError(mi, command, null, null);
        }
    }
    
    protected void sendError(MessageInfo mi, RTMPCommand command, Object object, Object information) {
        AMFPacket response = new AMFPacket();
        response.writeString("_error");
        response.writeNumber(command.getTxid());
        response.writeMixed(null);
        response.writeMixed(null);
        context.writeCommand(mi.streamID, response);
    }
    
    protected void sendSuccess(MessageInfo mi, RTMPCommand command) {
        AMFPacket response = new AMFPacket();
        response.writeString("_result");
        response.writeNumber(command.getTxid());
        response.writeMixed(null);
        response.writeMixed(null);
        context.writeCommand(mi.streamID, response);
    }
    
    protected void cmdCreateStream(MessageInfo mi, RTMPCommand command) {
        AMFPacket response = new AMFPacket();
        response.writeString("_result");
        response.writeNumber(command.getTxid());
        response.writeMixed(null);
        response.writeMixed(1d);
        context.writeCommand(mi.streamID, response);
    }
    
    protected void cmdConnect(MessageInfo mi, RTMPCommand command) {
        
        AMFPacket response = new AMFPacket();
        response.writeString("_result");
        response.writeNumber(command.getTxid());

        Map<String, Object> arg1 = new HashMap<>();
        arg1.put("fmsVer", "FMS/3,5,5,2004");
        arg1.put("capabilities", 31d);
        arg1.put("mode", 1d);
        response.writeObject(arg1);

        Map<String, Object> arg2 = new HashMap<>();
        arg2.put("level", "status");
        arg2.put("code", "NetConnection.Connect.Success");
        arg2.put("description", "Connection succeeded");
        arg2.put("data", new Object[0]);
        arg2.put("clientId", 1d);
        arg2.put("objectEncoding", 0d);
        response.writeObject(arg2);

        context.writeCommand(mi.streamID, response);
    }

    protected void cmdFCPublish(MessageInfo mi, RTMPCommand command) {
        
        String streamName = (String)command.getArguments().get(0);
        int separatorPos = streamName.indexOf(';');
        if (separatorPos == -1) {
            throw new RuntimeException("No separator found in stream name.");
        }
        String clientStreamID = streamName.substring(0, separatorPos);
        String clientSecret = streamName.substring(separatorPos + 1);

        String streamSectret = props.getProperty("streams." + clientStreamID + ".password");
        if (streamSectret == null) {
            Map<String, Object> info = new HashMap<>();
            info.put("code", "NetStream.Publish.BadName");  // with lack of better choice
            info.put("level", "error");
            info.put("description", "Stream does not exist.");
            sendError(mi, command, null, info);
            throw new RuntimeException("Stream does not exist.");
        }
        if (!clientSecret.equals(streamSectret)) {
            Map<String, Object> info = new HashMap<>();
            info.put("code", "NetStream.Publish.BadName");  // with lack of better choice
            info.put("level", "error");
            info.put("description", "Authentication error.");
            sendError(mi, command, null, info);
            throw new RuntimeException("Authentication error.");
        }
        
        streamID = clientStreamID;
        fcpublishTxID = command.getTxid();
        sendSuccess(mi, command);
    }
    
    protected void cmdPublish(MessageInfo mi, RTMPCommand command) {
        AMFPacket response = new AMFPacket();
        response.writeString("onStatus");
        response.writeNumber(fcpublishTxID);

        response.writeMixed(null);

        Map<String, Object> arg2 = new HashMap<>();
        arg2.put("level", "status");
        arg2.put("code", "NetStream.Publish.Start");
        arg2.put("description", "");
        arg2.put("details", command.getArguments().get(0));
        arg2.put("clientId", 1d);
        arg2.put("objectEncoding", 0d);
        response.writeObject(arg2);

        context.writeCommand(mi.streamID, response);
    }
    
    @Override
    public void onData(MessageInfo mi, RTMPCommand command) {
        String handlerName = (String)command.getArguments().get(0);
        if (HANDLER_NAME_SET_DATA_FRAME.equals(handlerName)) {
            metaData = (Map)command.getArguments().get(2);
            System.err.println("metadata: " + command.getArguments());
        } else {
            System.err.println("Unknown data: " + command.getArguments());
        }
    }

    @Override
    public void mediaChunk(MessageInfo mi, byte[] readBuffer, int payloadOffset, int payloadLength) {
        Integer trackID = streamToTrack.get(mi.streamID);
        
        if (trackID == null) {
            
            if (mi.type == 0x09) {
                final int SKIP_BYTES = 5;
                byte[] decoderSpecificBytes = new byte[payloadLength - SKIP_BYTES];
                System.arraycopy(readBuffer, payloadOffset + SKIP_BYTES, decoderSpecificBytes, 0, payloadLength - SKIP_BYTES);
                int timescale = metaData.get("framerate") != null
                        ? ((Double)metaData.get("framerate")).intValue() / 1000
                        : 30;
                int newTrackID = header.addVideoTrack(((Double)metaData.get("width")).intValue()
                        , ((Double)metaData.get("height")).intValue()
                        , timescale
                        , decoderSpecificBytes);
                streamToTrack.put(mi.streamID, newTrackID);
                
            } else if (mi.type == 0x08) {
                final int SKIP_BYTES = 2;
                byte[] decoderSpecificBytes = new byte[payloadLength - SKIP_BYTES];
                System.arraycopy(readBuffer, payloadOffset + SKIP_BYTES, decoderSpecificBytes, 0, payloadLength - SKIP_BYTES);
                int newTrackID = header.addAudioTrack(((Double)metaData.get("audiosamplerate")).intValue()
                        , ((Double)metaData.get("audiosamplesize")).intValue()
                        , ((Double)metaData.get("audiodatarate")).intValue()
                        , decoderSpecificBytes);
                streamToTrack.put(mi.streamID, newTrackID);
            }
            
        } else {
            
            if (!streamStarted) {
                startStream();
            }
            
        }
        //HexDump.displayHex(readBuffer, payloadOffset, Math.min(payloadLength, 32));
    }
    
    private void startStream() {
        streamStarted = true;
        
        ControlledStream stream = streams.get(streamID);
        if (stream != null) {
            stream.stop();
        }
        
        stream = new ControlledStream(Integer.parseInt(props.getProperty("streams." + streamID + ".limit")));
        stream.setMimeType("video/mp4");
        streams.put(streamID, stream);
        
        // rendering mp4 header
        
        final int BUFFER_LENGTH = 32 * 1024;
        BitBuffer bb = new BitBuffer(new byte[BUFFER_LENGTH], 0, BUFFER_LENGTH * 8);
        SerializerFactoryImpl sf = new SerializerFactoryImpl();
        sf.addSerializer(int.class, new FixedInteger());
        sf.addSerializer(Integer.class, new FixedInteger());
        sf.addSerializer(Number.class, new FixedInteger());
        sf.addSerializer(String.class, new FixedString());
        sf.addSerializer(DescriptorInteger.class, new DescriptorInteger());
        GeneralSerializer s = new GeneralSerializer(sf);
        AnnotationMapSerializationContext ctx = new AnnotationMapSerializationContext(bb, null, s);
        
        s.serialize(header.getFileType(), ctx);
        s.serialize(header.getMovie(), ctx);
        
        if ((bb.getBitPosition() & 0x07) != 0) {
            throw new RuntimeException("Rendered header is not byte-aligned.");
        }
        int headerLength = (int)(bb.getBitPosition() >> 3);
        byte[] headerBytes = new byte[headerLength];
        System.arraycopy(bb.getBuffer(), 0, headerBytes, 0, headerLength);
        
        stream.setHeader(headerBytes);
        
    }
    
}
