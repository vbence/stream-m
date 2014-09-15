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

import java.util.LinkedList;
import java.util.List;
import org.czentral.format.mp4.*;

/**
 *
 * @author Varga Bence (vbence@czentral.org)
 */
public class Mp4Header {
    
    private FileTypeBox fileType;

    private MovieBox movie;

    private MovieHeaderBox movieHeader;
    
    private MovieExtendsBox movieExtends;
    
    private List<TrackBox> tracks = new LinkedList<>();
    
        
    public Mp4Header() {
        
        String[] compatibleBrands = {"isom", "iso2", "avc1", "mp41"};
        fileType = new FileTypeBox("isom", 512, compatibleBrands);

        movie = new MovieBox();

        movieHeader = new MovieHeaderBox();
        movieHeader.setTimescale(1000);
        movieHeader.setNextTrackId(-1);
        movie.getContent().add(movieHeader);
        
        InitialObjectDescriptorBox iods = new InitialObjectDescriptorBox();
        movie.getContent().add(iods);
        InitialObjectDescriptor iod = new InitialObjectDescriptor();
        iods.setDescriptor(iod);
        
        movieExtends = new MovieExtendsBox();
        movie.getContent().add(movieExtends);
        
    }
    
    public int addAudioTrack(int sampleRate, int sampleSize, int maxBitrate, byte[] decoderSpecificBytes) {
        
        TrackBox track = new TrackBox();
        movie.getContent().add(track);
        
        tracks.add(track);
        int trackID = tracks.size();
        
        TrackHeaderBox trackHeader = new TrackHeaderBox();
        track.getContent().add(trackHeader);
        trackHeader.setFlags(TrackHeaderBox.FLAG_TRACK_ENABLED | TrackHeaderBox.FLAG_TRACK_IN_MOVIE);
        trackHeader.setTrackID(trackID);
        trackHeader.setDuration(0xffffffff);
        trackHeader.setVolume(0x100);
        
        MediaBox media = new MediaBox();
        track.getContent().add(media);
        
        MediaHeaderBox mediaHeader = new MediaHeaderBox();
        media.getContent().add(mediaHeader);
        mediaHeader.setTimescale(sampleRate);
        mediaHeader.setDuration(0xffffffff);
        
        HandlerBox handler = new HandlerBox();
        media.getContent().add(handler);
        handler.setHandlerType(HandlerBox.HandlerType.AUDIO.toString());
        
        MediaInformationBox mediaInformation = new MediaInformationBox();
        media.getContent().add(mediaInformation);
        
        SoundMediaHeaderBox smhd = new SoundMediaHeaderBox();
        mediaInformation.getContent().add(smhd);
        
        DataInformationBox dinf = new DataInformationBox();
        mediaInformation.getContent().add(dinf);
        
        DataReferenceBox dref = new DataReferenceBox();
        dinf.getContent().add(dref);
        
        DataEntryUrlBox dataEntryUrl = new DataEntryUrlBox();
        List<DataEntryUrlBox> entries = new LinkedList<>();
        entries.add(dataEntryUrl);
        dref.setEntries(entries);
        
        SampleTableBox sampleTable = new SampleTableBox();
        mediaInformation.getContent().add(sampleTable);
        
        SampleDescriptionBox stsd = new SampleDescriptionBox();
        sampleTable.getContent().add(stsd);
        
        AudioSampleEntry audioSampleEntry = new AudioSampleEntry("mp4a");
        stsd.getContent().add(audioSampleEntry);
        audioSampleEntry.setSampleRate(sampleRate);
        audioSampleEntry.setSampleSize(sampleSize);
        audioSampleEntry.setChannelCount(2);
        
        
        ESDescriptorBox esds = new ESDescriptorBox();
        audioSampleEntry.getContent().add(esds);
        
        ESDescriptor esDescriptor = new ESDescriptor();
        esds.setEsDescriptor(esDescriptor);
        esDescriptor.setEsID(trackID); // maybe???
        
        DecoderConfigDescriptor decoderConfig = new DecoderConfigDescriptor();
        esDescriptor.setDecoderConfigDescriptor(decoderConfig);
        decoderConfig.setObjectTypeIndication(DecoderConfigDescriptor.PROFILE_AAC_MAIN);
        decoderConfig.setStreamType(DecoderConfigDescriptor.STREAM_TYPE_AUDIO);
        decoderConfig.setMaxBitrate(maxBitrate);
        
        DecoderSpecificInfo dsi = new DecoderSpecificInfo();
        decoderConfig.getDecoderSpecificInfo().add(dsi);
        dsi.setBytes(decoderSpecificBytes);
        
        SLConfigDescriptor slc = new SLConfigDescriptor();
        esDescriptor.setSlConfigDescriptor(slc);
        slc.setPredefined(0x02);
        
        
        TimeToSampleBox stts = new TimeToSampleBox();
        sampleTable.getContent().add(stts);
        
        SampleToChunkBox stsc = new SampleToChunkBox();
        sampleTable.getContent().add(stsc);
        
        SampleSizeBox stsz = new SampleSizeBox();
        sampleTable.getContent().add(stsz);
        
        ChunkOffsetBox stco = new ChunkOffsetBox();
        sampleTable.getContent().add(stco);
        
        
        TrackExtendsBox trex = new TrackExtendsBox();
        movieExtends.getContent().add(trex);
        trex.setTrackID(trackID);
        
        
        return trackID;
    }

    public int addVideoTrack(int width, int height, int timeScale, byte[] decoderSpecificBytes) {
        
        TrackBox track = new TrackBox();
        movie.getContent().add(track);
        
        tracks.add(track);
        int trackID = tracks.size();
        
        TrackHeaderBox trackHeader = new TrackHeaderBox();
        track.getContent().add(trackHeader);
        trackHeader.setFlags(TrackHeaderBox.FLAG_TRACK_ENABLED | TrackHeaderBox.FLAG_TRACK_IN_MOVIE);
        trackHeader.setTrackID(trackID);
        trackHeader.setDuration(0xffffffff);
        trackHeader.setWidth(width);
        trackHeader.setHeight(height);
        
        MediaBox media = new MediaBox();
        track.getContent().add(media);
        
        MediaHeaderBox mediaHeader = new MediaHeaderBox();
        media.getContent().add(mediaHeader);
        mediaHeader.setTimescale(timeScale);
        mediaHeader.setDuration(0xffffffff);
        
        HandlerBox handler = new HandlerBox();
        media.getContent().add(handler);
        handler.setHandlerType(HandlerBox.HandlerType.VIDEO.toString());
        
        MediaInformationBox mediaInformation = new MediaInformationBox();
        media.getContent().add(mediaInformation);
        
        VideoMediaHeaderBox vmhd = new VideoMediaHeaderBox();
        mediaInformation.getContent().add(vmhd);
        
        DataInformationBox dinf = new DataInformationBox();
        mediaInformation.getContent().add(dinf);
        
        DataReferenceBox dref = new DataReferenceBox();
        dinf.getContent().add(dref);
        
        DataEntryUrlBox dataEntryUrl = new DataEntryUrlBox();
        List<DataEntryUrlBox> entries = new LinkedList<>();
        entries.add(dataEntryUrl);
        dref.setEntries(entries);
        
        SampleTableBox sampleTable = new SampleTableBox();
        mediaInformation.getContent().add(sampleTable);
        
        SampleDescriptionBox stsd = new SampleDescriptionBox();
        sampleTable.getContent().add(stsd);
        
        VisualSampleEntry visualSampleEntry = new VisualSampleEntry("avc1");
        stsd.getContent().add(visualSampleEntry);
        visualSampleEntry.setWidth(width);
        visualSampleEntry.setHeight(height);

        GeneralBoxImpl avcc = new GeneralBoxImpl("avcC");
        visualSampleEntry.getContent().add(avcc);
        avcc.setBytes(decoderSpecificBytes);
        
        
        TimeToSampleBox stts = new TimeToSampleBox();
        sampleTable.getContent().add(stts);
        
        SampleToChunkBox stsc = new SampleToChunkBox();
        sampleTable.getContent().add(stsc);
        
        SampleSizeBox stsz = new SampleSizeBox();
        sampleTable.getContent().add(stsz);
        
        ChunkOffsetBox stco = new ChunkOffsetBox();
        sampleTable.getContent().add(stco);
        
        
        TrackExtendsBox trex = new TrackExtendsBox();
        movieExtends.getContent().add(trex);
        trex.setTrackID(trackID);
        
        
        return trackID;
    }

    public FileTypeBox getFileType() {
        return fileType;
    }

    public MovieBox getMovie() {
        return movie;
    }
    
}
