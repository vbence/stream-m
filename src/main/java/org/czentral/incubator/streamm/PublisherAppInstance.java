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

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.czentral.data.binary.AnnotationMapSerializationContext;
import org.czentral.data.binary.BitBuffer;
import org.czentral.data.binary.Serializer;
import org.czentral.format.flv.VideoTag;
import org.czentral.format.mp4.DecoderConfigDescriptor;
import org.czentral.minirtmp.AMFPacket;
import org.czentral.minirtmp.ApplicationContext;
import org.czentral.minirtmp.MessageInfo;
import org.czentral.minirtmp.ApplicationInstance;
import org.czentral.minirtmp.RTMPCommand;
import org.czentral.util.stream.TimeInstant;

/**
 *
 * @author Varga Bence
 */
class PublisherAppInstance implements ApplicationInstance {
    
    private static final String HANDLER_NAME_SET_DATA_FRAME = "@setDataFrame";
    
    private static final int VIDEO_TIMESCALE_MULTIPLIER = 1000;
    
    private static final int MINIMAL_FRAGMENT_SIZE = 100 * 1024;
    
    private static final char STREAM_NAME_SEPARATOR = '?';
    
    private static final int FRAME_TYPE_KEYFRAME = 0x01;
    private static final int FRAME_TYPE_INTER_FRAME = 0x02;
    
    private static long MAX_DRIFT_MS = 200;
    
    private static int NAL_TYPE_SPS = 7;
    private static int NAL_TYPE_PPS = 8;
    private static int NAL_TYPE_SPS_EXTENSION = 13;
    private static int NAL_TYPE_SLICE_NONIDR = 1;
    private static int NAL_TYPE_SLICE_IDR = 5;
    
    protected ApplicationContext context;

    protected Properties props;

    protected Map<String, ControlledStream> streams;
    
    protected Serializer serializer;

    
    protected String streamID;
    
    protected double fcpublishTxID;
    
    protected Map<String, Object> metaData;


    protected String codecDescription = "";
    
    protected boolean streamStarted = false;

    protected Mp4Header header = new Mp4Header();
    
    protected Map<Integer, TrackInfo> streamToTrack = new HashMap<>();
    
    
    protected Mp4FragmentBuilder builder;
    
    protected int fragmentSequence = 1;
    
    
    public PublisherAppInstance(Properties props, Map<String, ControlledStream> streams, Serializer serializer) {
        this.props = props;
        this.streams = streams;
        this.serializer = serializer;
    }
    

    @Override
    public void onConnect(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public void invokeCommand(MessageInfo mi, RTMPCommand command) {
        // System.err.println("cmd: " + command.getName() + " " + command.getArguments());
        
        if (command.getName().equals("connect")) {
            cmdConnect(mi, command);
        
        } else if (command.getName().equals("releaseStream")) {
            sendSuccess(mi, command);
        
        } else if (command.getName().equals("FCUnpublish")) {
            //sendSuccess(mi, command);
            context.terminate();
            
        } else if (command.getName().equals("createStream")) {
            cmdCreateStream(mi, command);
        
        } else if (command.getName().equals("FCPublish")) {
            cmdFCPublish(mi, command);
        
        } else if (command.getName().equals("publish")) {
            cmdPublish(mi, command);
        
        } else if (command.getName().equals("deleteStream")) {
            cmdDeleteStream(mi, command);
        
        } else {
            System.err.println("Unknown command: " + command.getName());
            sendError(mi, command, null, null);
        }
    }
    
    protected void terminateWithError(MessageInfo mi, RTMPCommand command, Object object, Object information) {
        sendError(mi, command, object, information);
        context.terminate();
    }
    
    protected void sendError(MessageInfo mi, RTMPCommand command, Object object, Object information) {
        AMFPacket response = new AMFPacket();
        response.writeString("_error");
        response.writeNumber(command.getTxid());
        response.writeMixed(object);
        response.writeMixed(information);
        context.writeCommand(mi.chunkStreamID, mi.messageStreamID, response);
    }
    
    protected void sendSuccess(MessageInfo mi, RTMPCommand command) {
        AMFPacket response = new AMFPacket();
        response.writeString("_result");
        response.writeNumber(command.getTxid());
        response.writeMixed(null);
        response.writeMixed(null);
        context.writeCommand(mi.chunkStreamID, mi.messageStreamID, response);
    }
    
    protected void cmdDeleteStream(MessageInfo mi, RTMPCommand command) {
        AMFPacket response = new AMFPacket();
        response.writeString("_result");
        response.writeNumber(command.getTxid());
        response.writeMixed(null);
        response.writeMixed(null);
        context.writeCommand(mi.chunkStreamID, mi.messageStreamID, response);
        context.terminate();
    }
    
    protected void cmdCreateStream(MessageInfo mi, RTMPCommand command) {
        AMFPacket response = new AMFPacket();
        response.writeString("_result");
        response.writeNumber(command.getTxid());
        response.writeMixed(null);
        response.writeMixed(1d);
        context.writeCommand(mi.chunkStreamID, mi.messageStreamID, response);
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

        context.writeCommand(mi.chunkStreamID, mi.messageStreamID, response);
    }

    protected void cmdFCPublish(MessageInfo mi, RTMPCommand command) {
        fcpublishTxID = command.getTxid();
        sendSuccess(mi, command);
    }
    
    protected void cmdPublish(MessageInfo mi, RTMPCommand command) {
        
        Object arg1 = command.getArguments().get(0);
        if (!(arg1 instanceof String)) {
            sendSuccess(mi, command);
            return;
        }
        
        String streamName = (String)command.getArguments().get(0);
        int separatorPos = streamName.indexOf(STREAM_NAME_SEPARATOR);
        if (separatorPos == -1) {
            Map<String, Object> info = new HashMap<>();
            info.put("code", "NetStream.Publish.BadName");  // with lack of better choice
            info.put("level", "error");
            info.put("description", "Stream password required.");
            terminateWithError(mi, command, null, info);
            return;
        }
        String clientStreamID = streamName.substring(0, separatorPos);
        String clientSecret = streamName.substring(separatorPos + 1);

        String streamProperty = props.getProperty("streams." + clientStreamID);
        if (!"true".equals(streamProperty) && !"1".equals(streamProperty)) {
            Map<String, Object> info = new HashMap<>();
            info.put("code", "NetStream.Publish.BadName");  // with lack of better choice
            info.put("level", "error");
            info.put("description", "Stream does not exist.");
            terminateWithError(mi, command, null, info);
            return;
        }
        
        String streamSectret = props.getProperty("streams." + clientStreamID + ".password");
        if (streamSectret == null) {
            Map<String, Object> info = new HashMap<>();
            info.put("code", "NetStream.Publish.BadName");  // with lack of better choice
            info.put("level", "error");
            info.put("description", "Stream does not exist.");
            terminateWithError(mi, command, null, info);
            return;
        }
        if (!clientSecret.equals(streamSectret)) {
            Map<String, Object> info = new HashMap<>();
            info.put("code", "NetStream.Publish.BadName");  // with lack of better choice
            info.put("level", "error");
            info.put("description", "Authentication error.");
            terminateWithError(mi, command, null, info);
            return;
        }
        
        // raise limit after successful authentication
        context.getLimit().assemblyBufferSize = 50 * 1024 * 1024;
        context.getLimit().assemblyBufferCount = 128;
        context.getLimit().chunkStreamCount = 256;
        
        streamID = clientStreamID;
        
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

        context.writeCommand(mi.chunkStreamID, mi.messageStreamID, response);
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

        int trackId = mi.messageStreamID <<1 | (mi.type == 0x09 ? 1 : 0);
        
        TrackInfo trackInfo = streamToTrack.get(trackId);
        
        if (trackInfo == null) {
            
            //System.out.println("csid: " + mi.chunkStreamID);
            
            if (streamStarted) {
                throw new RuntimeException("Unexpected media (frame in an unanounced stream).");
            }
            
            if (mi.type == 0x09) {
                //HexDump.displayHex(readBuffer, payloadOffset, Math.min(payloadLength, 32));
                VideoTag tag = VideoTag.parse(ByteBuffer.wrap(readBuffer, payloadOffset, payloadLength));
                
                if (tag.getCodec() != VideoTag.Codec.AVC) {
                    throw new RuntimeException("Only AVC codec suported.");
                }
                
                if (tag.getAvcType() != VideoTag.AvcType.SEQUENCE_HEADER) {
                    throw new RuntimeException("First frame expected to contain Sequence Header.");
                }
                
                final int SKIP_BYTES = 5;
                byte[] decoderSpecificBytes = new byte[payloadLength - SKIP_BYTES];
                if (decoderSpecificBytes.length == 0) {
                    return;
                }
                System.arraycopy(readBuffer, payloadOffset + SKIP_BYTES, decoderSpecificBytes, 0, payloadLength - SKIP_BYTES);
                
                //HexDump.displayHex(decoderSpecificBytes, 0, Math.min(decoderSpecificBytes.length, 32));
                int nalHead = decoderSpecificBytes[1];
                int nalRefIdc = (nalHead & 0b01100000) >> 5;
                int nalType = (nalHead & 0b011111);
                
                if (nalType != NAL_TYPE_PPS
                        && nalType != NAL_TYPE_SPS
                        && nalType != NAL_TYPE_SPS_EXTENSION) {
                    System.err.println("SPS/PPS expected, got type " + nalType + "");
                }
                
                int codecConfig = (decoderSpecificBytes[1] & 0xff) << 16
                        | (decoderSpecificBytes[2] & 0xff) << 8
                        | (decoderSpecificBytes[3] & 0xff);
                codecDescription += (codecDescription.length() > 0 ? "," : "")
                        + "avc1." + Integer.toHexString(0x1000000 | codecConfig).substring(1);
                
                int timescale = metaData.get("framerate") != null
                        ? ((Double)metaData.get("framerate")).intValue() * VIDEO_TIMESCALE_MULTIPLIER
                        : 30 * VIDEO_TIMESCALE_MULTIPLIER;
                int newTrackID = header.addVideoTrack(((Double)metaData.get("width")).intValue()
                        , ((Double)metaData.get("height")).intValue()
                        , timescale
                        , decoderSpecificBytes);
                
                trackInfo = new TrackInfo(newTrackID, TrackInfo.Type.VIDEO, timescale);
                streamToTrack.put(trackId, trackInfo);
                
            } else if (mi.type == 0x08) {
                final int SKIP_BYTES = 2;
                byte[] decoderSpecificBytes = new byte[payloadLength - SKIP_BYTES];
                System.arraycopy(readBuffer, payloadOffset + SKIP_BYTES, decoderSpecificBytes, 0, payloadLength - SKIP_BYTES);
                
                // AOT is in decimal
                int audioObjectType = (decoderSpecificBytes[0] & 0xff) >> 3;
                if (audioObjectType == 31) {
                    audioObjectType = ( (decoderSpecificBytes[0] & 0x07) << 3
                            | (decoderSpecificBytes[1] & 0xe0) >> 5 )
                            + 31;
                }
                codecDescription += (codecDescription.length() > 0 ? "," : "")
                        + "mp4a." + Integer.toHexString(0x100 | DecoderConfigDescriptor.PROFILE_AAC_MAIN).substring(1)
                        + "." + audioObjectType;

                int timescale = ((Double)metaData.get("audiosamplerate")).intValue();
                int audioSampleSize = metaData.containsKey("audiosamplesize") ? ((Double)metaData.get("audiosamplesize")).intValue() : 16;
                int newTrackID = header.addAudioTrack(((Double)metaData.get("audiosamplerate")).intValue()
                        , audioSampleSize
                        , ((Double)metaData.get("audiodatarate")).intValue()
                        , decoderSpecificBytes);
                
                trackInfo = new TrackInfo(newTrackID, TrackInfo.Type.AUDIO, timescale);
                streamToTrack.put(trackId, trackInfo);
            }
            
            return;
        }
        
        // need to flush old fragment
        int frameType = (readBuffer[payloadOffset] & 0xf0) >> 4;
        if (builder != null
                && builder.getDataLength() >= MINIMAL_FRAGMENT_SIZE
                && trackInfo.type == TrackInfo.Type.VIDEO
                && frameType == FRAME_TYPE_KEYFRAME) {
            
            // start stream if not already started
            if (!streamStarted) {
                startStream();
            }

            // build and save frame
            streams.get(streamID).pushFragment(builder.build(serializer));
            
            // remove current builder
            builder = null;
        }
        
        // create builder if none exists
        if (builder == null) {
            builder = new Mp4FragmentBuilder(fragmentSequence++, mi.calculatedTimestamp);
            //System.out.println("---");
        }

        if (mi.type == 0x09) {
            
            /* if (builder.getDataLength() > 0)
                return; */
            
            VideoTag tag = VideoTag.parse(ByteBuffer.wrap(readBuffer, payloadOffset, payloadLength));
            
            //HexDump.displayHex(readBuffer, payloadOffset, Math.min(payloadLength, 32));
            int nalHead = readBuffer[payloadOffset + 1];
            int nalRefIdc = (nalHead & 0b01100000) >> 5;
            int nalType = (nalHead & 0b011111);
            
            if (tag.getFrameType() == VideoTag.FrameType.KEYFRAME) {
                if (nalType != NAL_TYPE_SLICE_IDR) {
                    throw new RuntimeException("Expected IDR slice. Got " + nalType + "");
                }
            }
            
            if (tag.getFrameType() == VideoTag.FrameType.INTER) {
                if (nalType != NAL_TYPE_SLICE_NONIDR) {
                    throw new RuntimeException("Expected non-IDR slice. got " + nalType + "");
                }
            }

            //System.out.println("tt:" + trackInfo.timeMs + " ft:" + mi.calculatedTimestamp);
            
            /*
            if (Math.abs(trackInfo.timeMs - mi.calculatedTimestamp) > MAX_DRIFT_MS) {
                throw new RuntimeException("RTMP timestamps diverged from estimation.");
            }
            */
            
            final int SKIP_BYTES = 5;
            int offset = payloadOffset + 2;
            int compositionTime = (readBuffer[offset++] & 0xff) << 16 | (readBuffer[offset++] & 0xff) << 8 | (readBuffer[offset++] & 0xff);
            /* if (payloadLength < 5) {
                System.out.println("payload < 5 bytes");
            } else {
                int nalSize = (readBuffer[offset++] & 0xff) << 24 | (readBuffer[offset++] & 0xff) << 16 | (readBuffer[offset++] & 0xff) << 8 | (readBuffer[offset++] & 0xff);
                if (payloadLength - SKIP_BYTES < nalSize + 4) {
                    System.out.println("NAL size mismatch! ns: " + nalSize + ", pls: " + (payloadLength - SKIP_BYTES));
                }
            } */
            
            builder.addFrame(trackInfo.trackID, (int)(compositionTime * trackInfo.timing.getTicksPerSecond() / 1000 / VIDEO_TIMESCALE_MULTIPLIER + 0.5) * VIDEO_TIMESCALE_MULTIPLIER, new TimeInstant(trackInfo.timing.getTicksPerSecond(), mi.calculatedTimestamp * trackInfo.timing.getTicksPerSecond() / 1000), readBuffer, payloadOffset + SKIP_BYTES, payloadLength - SKIP_BYTES);
            //System.out.printf("v (%d) %d %d: %d%n", mi.length, trackInfo.timing.getMillis(), mi.calculatedTimestamp, trackInfo.timing.getMillis() - mi.calculatedTimestamp);

            trackInfo.timing = trackInfo.timing.transposed(VIDEO_TIMESCALE_MULTIPLIER);

        } else if (mi.type == 0x08) {
            
            final int SKIP_BYTES = 2;
            builder.addFrame(trackInfo.trackID, 0, new TimeInstant(trackInfo.timing.getTicksPerSecond(), mi.calculatedTimestamp * trackInfo.timing.getTicksPerSecond() / 1000), readBuffer, payloadOffset + SKIP_BYTES, payloadLength - SKIP_BYTES);

            //System.out.printf("a (%d) %d %d: %d%n", mi.length, trackInfo.timing.getMillis(), mi.calculatedTimestamp, trackInfo.timing.getMillis() - mi.calculatedTimestamp);
            /*
            if (Math.abs(trackInfo.timeMs - mi.calculatedTimestamp) > MAX_DRIFT_MS) {
                throw new RuntimeException("RTMP timestamps diverged from estimation.");
            }
            */

            trackInfo.timing = trackInfo.timing.transposed(1024);
        }
        
        //HexDump.displayHex(readBuffer, payloadOffset, Math.min(payloadLength, 32));
        //System.out.println("at: " + mi.absoluteTimestamp + ", rt: " + mi.relativeTimestamp);
    }
    
    private void startStream() {
        streamStarted = true;
        
        ControlledStream stream = streams.get(streamID);
        if (stream != null) {
            stream.stop();
        }
        
        stream = new ControlledStream(Integer.parseInt(props.getProperty("streams." + streamID + ".limit")));
        stream.setMimeType("video/mp4;codecs=\"" + codecDescription + "\"");
        streams.put(streamID, stream);
        
        // rendering mp4 header
        
        final int BUFFER_LENGTH = 32 * 1024;
        BitBuffer bb = new BitBuffer(new byte[BUFFER_LENGTH], 0, BUFFER_LENGTH * 8);
        AnnotationMapSerializationContext ctx = new AnnotationMapSerializationContext(bb, null, serializer);
        
        serializer.serialize(header.getFileType(), ctx);
        serializer.serialize(header.getMovie(), ctx);
        
        if ((bb.getBitPosition() & 0x07) != 0) {
            throw new RuntimeException("Rendered header is not byte-aligned.");
        }
        int headerLength = (int)(bb.getBitPosition() >> 3);
        byte[] headerBytes = new byte[headerLength];
        System.arraycopy(bb.getBuffer(), 0, headerBytes, 0, headerLength);
        
        stream.setHeader(headerBytes);
        
    }
    
    private static class TrackInfo {
        
        public enum Type {
            UNKNOWN,
            AUDIO,
            VIDEO
        }

        public int trackID = -1;
        
        public Type type = Type.UNKNOWN;

        public TimeInstant timing;

        public TrackInfo(int trackID, Type type) {
            this.trackID = trackID;
            this.type = type;
        }

        public TrackInfo(int trackID, Type type, int timescale) {
            this(trackID, type);
            this.timing = new TimeInstant(timescale, 0);
        }

    }
    
}
