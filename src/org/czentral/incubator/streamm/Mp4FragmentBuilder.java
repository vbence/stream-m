/*
 * This file is part of the "stream-m" software. An HTML5 compatible live
 * streaming and broadcasting server.
 * Copyright (C) 2014 Varga Bence (vbence@czentral.org)
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

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.czentral.data.binary.AnnotationMapSerializationContext;
import org.czentral.data.binary.BitBuffer;
import org.czentral.data.binary.ByteArraySlice;
import org.czentral.data.binary.Serializer;
import org.czentral.format.mp4.MovieDataBox;
import org.czentral.format.mp4.MovieFragmentBox;
import org.czentral.format.mp4.MovieFragmentHeaderBox;
import org.czentral.format.mp4.SegmentIndexBox;
import org.czentral.format.mp4.SegmentReference;
import org.czentral.format.mp4.SegmentTypeBox;
import org.czentral.format.mp4.TrackFragmentBaseMediaDecodeTimeBox;
import org.czentral.format.mp4.TrackFragmentBox;
import org.czentral.format.mp4.TrackFragmentHeaderBox;
import org.czentral.format.mp4.TrackRunBox;
import org.czentral.util.stream.Buffer;

/**
 *
 * @author Varga Bence (vbence@czentral.org)
 */
public class Mp4FragmentBuilder {
    
    private final int MDAT_HEADER_SIZE = 8;
    
    private final long initialTime;
    
    private final int sequenceNumber;
    
    private final SegmentTypeBox styp;
    
    private final SegmentIndexBox sidx;
    
    //private final MovieFragmentBox moof;
    
    //private final MovieDataBox mdat;
    
    private final Map<Integer, TrackInfo> tracks = new TreeMap<>();
    
    private int dataLength = 0;
    
    public Mp4FragmentBuilder(int sequenceNumber, long initialTime) {
        
        this.sequenceNumber = sequenceNumber;
        this.initialTime = initialTime;
        
        styp = new SegmentTypeBox("msdh", 0, new String[] {"msdh", "msix"});
        sidx = new SegmentIndexBox();
        sidx.setReferenceID(1);
        sidx.setTimescale(1000);
        sidx.setReferenceCount(1);
        sidx.setEarliestPresentationTime(initialTime);

        SegmentReference reference = new SegmentReference();
        sidx.getReferences().add(reference);
        reference.setStartsWithSAP(1);
        reference.setSapType(1);
        reference.setSubsegmentDuration(25000); // test value
        
        /*
        moof = new MovieFragmentBox();
        
        MovieFragmentHeaderBox mfhd = new MovieFragmentHeaderBox();
        moof.getContent().add(mfhd);
        mfhd.setSequenceNumber(sequenceNumber);
        */
        
        
        //mdat = new MovieDataBox();
    }
        
    public void addFrame(int trackID, int timeOffset, long trackTime, byte[] buffer, int offset, int length) {
        TrackInfo info = getTrackInfo(trackID, trackTime);
        
        info.framingData.putInt(length);
        info.framingData.putInt(timeOffset);
        //info.lastTime = time;
        info.samples.add(new ByteArraySlice(buffer, offset, length));
        info.dataLength += length;
        
        //mdat.getContent().add(new ByteArraySlice(buffer, offset, length));
        dataLength += length;
    }
    
    protected TrackInfo getTrackInfo(int trackID, long trackTime) {
        TrackInfo info = tracks.get(trackID);
        if (info == null) {
            info = addTrack(trackID, trackTime);
            tracks.put(trackID, info);
        }
        return info;
    }
    
    protected TrackInfo addTrack(int trackID, long trackTime) {
        TrackFragmentBox traf = new TrackFragmentBox();
        //moof.getContent().add(traf);
        
        TrackFragmentHeaderBox tfhd = new TrackFragmentHeaderBox();
        traf.getContent().add(tfhd);
        tfhd.setFlags(TrackFragmentHeaderBox.FLAG_DEFAULT_BASE_IS_MOOF);
        tfhd.setTrackID(trackID);
        
        TrackFragmentBaseMediaDecodeTimeBox tfdt = new TrackFragmentBaseMediaDecodeTimeBox();
        traf.getContent().add(tfdt);
        tfdt.setBaseMediaDecodeTime(trackTime);
        
        TrackRunBox trun = new TrackRunBox();
        traf.getContent().add(trun);
        trun.setFlags( trun.getFlags()
                | TrackRunBox.FLAG_SAMPLE_SIZE_PRESENT
                | TrackRunBox.FLAG_SAMPLE_COMPOSITION_TIME_OFFSET_PRESENT );
        
        TrackInfo info = new TrackInfo();
        info.traf = traf;
        info.trun = trun;
        info.tfhd = tfhd;
        info.framingData = ByteBuffer.allocate(64 * 1024);
        
        return info;
    }

    public int getDataLength() {
        return dataLength;
    }
    
    /*
    public MovieFragment build(Serializer s) {
        
        finalizeTracks();
        
        final int BUFFER_LENGTH = 2048 * 1024;
        BitBuffer bb = new BitBuffer(new byte[BUFFER_LENGTH], 0, BUFFER_LENGTH * 8);
        AnnotationMapSerializationContext ctx = new AnnotationMapSerializationContext(bb, null, s);
        
        //s.serialize(styp, ctx);
        
        //long sidxPosition = bb.getBitPosition();
        //s.serialize(sidx, ctx);
        
        long moofPosition = bb.getBitPosition();
        s.serialize(moof, ctx);
        int moofSize = (int)((bb.getBitPosition() - moofPosition) >> 3);
        
        ByteBuffer dataOffsetBuffer = ByteBuffer.wrap(bb.getBuffer(), (int)(moofPosition >> 3) + 64 + 16, 4);
        dataOffsetBuffer.putInt(moofSize + MDAT_HEADER_SIZE);

        s.serialize(mdat, ctx);
        
        //ByteBuffer referenceSizeBuffer = ByteBuffer.wrap(bb.getBuffer(), (int)(sidxPosition >> 3) + 32, 4);
        //referenceSizeBuffer.putInt((int)((bb.getBitPosition() - moofPosition) >> 3));

        //validateData(bb.getBuffer(), moofSize);
        
        Buffer[] fragmentBuffers = { new Buffer(bb.getBuffer(), 0, (int)(bb.getBitPosition() >> 3))};
        Mp4Fragment fragment = new Mp4Fragment(fragmentBuffers);
        
        return fragment;
    } */
    
    public MovieFragment build(Serializer s) {
        
        final int BUFFER_LENGTH = 2048 * 1024;
        BitBuffer bb = new BitBuffer(new byte[BUFFER_LENGTH], 0, BUFFER_LENGTH * 8);
        AnnotationMapSerializationContext ctx = new AnnotationMapSerializationContext(bb, null, s);
        
        s.serialize(styp, ctx);
        
        long sidxPosition = bb.getBitPosition();
        s.serialize(sidx, ctx);
        
        MovieFragmentBox moof = new MovieFragmentBox();

        MovieFragmentHeaderBox mfhd = new MovieFragmentHeaderBox();
        moof.getContent().add(mfhd);
        mfhd.setSequenceNumber(sequenceNumber);

        MovieDataBox mdat = new MovieDataBox();
        
        for (TrackInfo info : tracks.values()) {
            
            info.trun.setData(new ByteArraySlice(info.framingData.array(), 0, info.framingData.position()));
            info.trun.setSampleCount(info.samples.size());
            
            moof.getContent().add(info.traf);
            mdat.getContent().addAll(info.samples);
            
        }
        
        long moofPosition = bb.getBitPosition();
        s.serialize(moof, ctx);
        int moofSize = (int)((bb.getBitPosition() - moofPosition) >> 3);
        
        s.serialize(mdat, ctx);
        
        ByteBuffer referenceSizeBuffer = ByteBuffer.wrap(bb.getBuffer(), (int)(sidxPosition >> 3) + 32, 4);
        referenceSizeBuffer.putInt((int)((bb.getBitPosition() - moofPosition) >> 3));

        // update trun.dataOffset fields
        int offset = (int)(moofPosition >> 3) + 24;
        int sampleDataPointer = 0;
        for (TrackInfo info : tracks.values()) {
            int doOffset = offset + 40 + 16;
            ByteBuffer dataOffsetBuffer = ByteBuffer.wrap(bb.getBuffer(), doOffset, 4);
            dataOffsetBuffer.putInt(moofSize + MDAT_HEADER_SIZE + sampleDataPointer);
            
            offset += 40 + 24 + info.samples.size() * 8;
            sampleDataPointer += info.dataLength;
        }
        
        Buffer[] fragmentBuffers = { new Buffer(bb.getBuffer(), 0, (int)(bb.getBitPosition() >> 3))};
        Mp4Fragment fragment = new Mp4Fragment(fragmentBuffers);
        
        return fragment;
    }

    /* private void finalizeTracks() {

        // finalize trun boxes
        // add sample data to mdat (per track)
        // set first track to be relative to 'moof' box
        
        boolean firstTrack = true;
        for (TrackInfo info : tracks.values()) {
            if (firstTrack) {
                firstTrack = false;
                info.tfhd.setFlags(info.tfhd.getFlags()
                        | TrackFragmentHeaderBox.FLAG_DEFAULT_BASE_IS_MOOF);
            }

            info.trun.setData(new ByteArraySlice(info.framingData.array(), 0, info.framingData.position()));
            info.trun.setSampleCount(info.samples.size());
            mdat.getContent().addAll(info.samples);
        }
        
    } */
    
    private void reportData(byte[] buffer, int position) {
        
        int offset = position;
        
        // length
        int boxLength = (buffer[offset++] & 0xff) << 24
                | (buffer[offset++] & 0xff) << 16
                | (buffer[offset++] & 0xff) << 8
                | (buffer[offset++] & 0xff);
        
        System.out.println("bl: " + boxLength);
        
        // 'mdat'
        offset += 4;
        
        // end of box
        int limit = position + boxLength;
        
        while (offset < limit) {
            int nalSize = (buffer[offset++] & 0xff) << 24
                    | (buffer[offset++] & 0xff) << 16
                    | (buffer[offset++] & 0xff) << 8
                    | (buffer[offset++] & 0xff);
            
            HexDump.displayHex(buffer, offset, Math.min(nalSize, 16));
            
            offset += nalSize;
        }
        
    }
    
    protected static class TrackInfo {
        
        public int lastTime = 0;
        
        public TrackFragmentBox traf;
        
        public TrackRunBox trun;
        
        public TrackFragmentHeaderBox tfhd;
        
        public ByteBuffer framingData;
        
        public List<ByteArraySlice> samples = new LinkedList<>();
        
        public int dataLength = 0;
        
    }
    
    private static class Mp4Fragment implements MovieFragment {
        
        private Buffer[] buffers;

        public Mp4Fragment(Buffer[] buffers) {
            this.buffers = buffers;
        }

        @Override
        public Buffer[] getBuffers() {
            return buffers;
        }

        @Override
        public int length() {
            return buffers[0].getLength();
        }
        
    }
    
}
