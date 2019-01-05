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

import java.util.LinkedList;
import java.util.List;
import org.czentral.format.mp4.*;

/**
 *
 * @author Varga Bence (vbence@czentral.org)
 */
public class Mp4Header {
    
    private final int DEFAULT_TIMESCALE = 1000;
    
    private FileTypeBox fileType;

    private MovieBox movie;

    private MovieHeaderBox movieHeader;
    
    private MovieExtendsBox movieExtends;
    
    private List<TrackBox> tracks = new LinkedList<>();
    
        
    public Mp4Header() {
        
        //fileType = new FileTypeBox("isom", 512, new String[] {"isom", "iso2", "avc1", "mp41", "dash"});
        
        fileType = new FileTypeBox("iso5", 1, new String[] {"avc1", "iso5", "dash"} );

        movie = new MovieBox();

        movieHeader = new MovieHeaderBox();
        movieHeader.setTimescale(DEFAULT_TIMESCALE);
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
        trex.setDefaultSampleDescriptionIndex(1);
        trex.setDefaultSampleDuration(1024);
        trex.getDefaultSampleFlags().setSampleDependsOn(SampleFlags.DEPENDS_ON_NO_OTHERS);
        
        return trackID;
    }

    public int addVideoTrack(int width, int height, int timescale, byte[] decoderSpecificBytes) {
        
        TrackBox track = new TrackBox();
        movie.getContent().add(track);
        
        tracks.add(track);
        int trackID = tracks.size();
        
        TrackHeaderBox trackHeader = new TrackHeaderBox();
        track.getContent().add(trackHeader);
        trackHeader.setFlags(TrackHeaderBox.FLAG_TRACK_ENABLED | TrackHeaderBox.FLAG_TRACK_IN_MOVIE);
        trackHeader.setTrackID(trackID);
        trackHeader.setDuration(0xffffffff);
        trackHeader.setWidth(width << 16);  // 16.16 fixed point format
        trackHeader.setHeight(height << 16);    // 16.16 fixed point format
        
        MediaBox media = new MediaBox();
        track.getContent().add(media);
        
        MediaHeaderBox mediaHeader = new MediaHeaderBox();
        media.getContent().add(mediaHeader);
        mediaHeader.setTimescale(timescale);
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
        trex.setDefaultSampleDescriptionIndex(1);
        trex.setDefaultSampleDuration(1001);
        trex.getDefaultSampleFlags().setSampleIsDifferenceSample(1);
        
        
        return trackID;
    }

    public FileTypeBox getFileType() {
        return fileType;
    }

    public MovieBox getMovie() {
        return movie;
    }
    
}
