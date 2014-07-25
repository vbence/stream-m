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
import java.util.Map;
import java.util.Properties;
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
    
    protected ApplicationContext context;

    protected Properties props;

    protected Map<String, ControlledStream> streams;
    
    protected double fcpublishTxID;
    
    protected byte[] videoMeta;
    
    protected byte[] audioMeta;

    
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
        System.err.println("called: " + command.getName());
        
        if (command.getName().equals("connect")) {
            cmdConnect(mi, command);
        
        } else if (command.getName().equals("releaseStream")) {
            sendSuccess(mi, command);
        
        } else if (command.getName().equals("createStream")) {
            cmdCreateStream(mi, command);
        
        } else if (command.getName().equals("FCPublish")) {
            cmdFCPublish(mi, command);
        
        } else if (command.getName().equals("publish")) {
            cmdPublish(mi, command);
        
        } else {
            System.err.println("Unknown command: " + command.getName());
            
            AMFPacket response = new AMFPacket();
            response.writeString("_error");
            response.writeNumber(command.getTxid());
            response.writeMixed(null);
            response.writeMixed(null);
            context.writeCommand(mi.streamID, response);
        }
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
        fcpublishTxID = command.getTxid();
        sendSuccess(mi, command);
    }
    
    protected void cmdPublish(MessageInfo mi, RTMPCommand command) {
        AMFPacket response = new AMFPacket();
        response.writeString("onStatus");
        response.writeNumber(fcpublishTxID);

        response.writeMixed(null);

        Map<String, Object> arg2 = new HashMap<String, Object>();
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
        System.err.println(command.getArguments());
    }

    @Override
    public void mediaChunk(MessageInfo mi, byte[] readBuffer, int payloadOffset, int payloadLength) {
        if (videoMeta == null && mi.type == 0x09) {
            videoMeta = new byte[payloadLength];
            System.arraycopy(readBuffer, payloadOffset, videoMeta, 0, payloadLength);
        }
        if (audioMeta == null && mi.type == 0x08) {
            audioMeta = new byte[payloadLength];
            System.arraycopy(readBuffer, payloadOffset, audioMeta, 0, payloadLength);
        }
    }
    
}
