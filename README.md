README
=======

*Stream-m* is created to be an *open source* solution for streaming live video right into the web browser using the HTML5 video tag and Google's **WebM** or the prolific **H.264** video format.

The current version is a *working prototype*, which showcases the main ideas. The main design goal is **low resource usage**. It has a web interface with a real-time bandwidth monitor (with the resolution of 1/10 of a second) for spotting network congestion.

Independent streams (channels) are supported.


## FORMATS

Currently both **WebM** and **H.264** are supported, although through slightly different workflows.

### WebM

Google's **WebM** was the first format supported by the server. Publishing of streams can be done through the HTTP interface (see below for details).

Putting the stream URL in a `<video>` tag should work in all WebM-capable platforms.

### <a name="format-h264"></a> H.264 

**H.264 with AAC** is the second addition. Current video publishing solutions (like the [Open Broadcasting Software](https://obsproject.com/)) were created to be compatible with Adobe's *Flash Media Server*. To take advantage of these tools it was necessary for Stream-m to *"speak flash"*, therefore a minimal **RTMP** server implementation was written to handle incoming streams.

Current RTMP support is limited to receiving streams from the publishing software (like *OBS*). Consuming (playing) streams through RTMP is not supported at the moment.

Playing H.264 live streams has been tested and working in Google Chrome so far (Android version included). Directly putting the stream's URL into a `<video>` tag won't work with this format. It will on the other hand, by using the [MediaSource API](http://www.w3.org/TR/media-source/) (currently used by YouTube's HTML5 video player). 

A demo player is included, which will play the stream with the name `first` (the default name in the sample configuration file). The demo can be accessed (by default) on the following URL:

    http://localhost:8080/player-demo/player.html


## RUNNING THE SERVER

    java -jar stream-m.jar <configfile>

Before running the server you should edit the sample config file (change password and choose a stream name). So you will end up with something like:

    java -jar stream-m.jar server.properties


## HTTP INTERFACE

Streams are identified by a unique name. The program refers to this as StreamID or stream name. Note that the `<` and `>` characters are used just to indicate substitution, they must not be included in the resulting URL.

> **Note:** Many parts of this section are only relevant for streams using the WebM format. For peculiarities of H.264 streams please see the [specific section](#format-h264) above.

The default port for the built-in HTTP server is **8080**.

The name and password of each stream is defined in the config file. A stream must be sent with POST or PUT method to the following URL to start a broadcast:

    /publish/<streamname>?password=<streampass>

A stream can be accessed (watched) on the following URL. You may want to insert this URL into a HTML5 `<video>` tag:

    /consume/<streamname>

To support playback with the *MediaSource API* (requesting fragments through *XMLHttpRequest*) additional URL parameters can influence playback. Currently supported GET parameters:

name             | default | effect
-----------------|---------|--------------------------
sendHeader       |  true   | Sets whether to send header.
singleFragment   |  false  | If enabled only a single fragment is sent.
fragmentSequence |  *n/a*  | Output will only start when a fragment with the given sequence number is available.


A snapshot (the first key frame of the last completed fragment) can be downloaded in WebP format on the URL:

    /snapshot/<streamname>


Real-time information can be acquired through an AJAX based console (giving the name and password for the chosen stream on the UI):

    /console/client.html


## RTMP INTERFACE

Currently the server only contains one RTMP application. Listening on the name `publish` it will take a stream, assemble frames then pass them to the usual HTTP infrastructure.

The default port for the built-in RTMP server is **8081**.

The endpoint of the publishing software needs to have the following format:

    /publish/<streamname>?<streampass>


## FRAGMENTS

Live streams consist of fragments (self-contained units of frames which are not referencing any frame outside the fragment). A fragment always starts with a key-frame (intra-frame) therefore it is important that the encoder put in key frames regularly as this determines the size of the fragments.

The ideal fragment size is around 200 kBytes (or 1600 kbits). The key frame interval can be calculated with this formula:

    1600k / <bitrate> * <framerate>

*e.g.* if you are publishing a 500 kbit stream with 16 fps, then:
`1600 / 500 * 16 = 51.2`
(or `1600000 / 500000 * 16 = 51.2`) so every 52nd video frame should be a key frame.

The server splits fragments when it seems necessary. A soft minimum for frame size is currently 100k (no new fragment is started if a new key frame arrives within 100 kBytes from a previous key frame).

The hard maximum for a fragments is 2048 kBytes. This is twice the size needed to hold 2 seconds of a 4096 kbit/sec HD stream.


## PUBLISHING WEBM

### On Windows Systems

In theory several open source tools can be used for this. Practically you will need two of them if you want to stream from your webcam in windows.

**VLC** can access your recording hardware and encode WebM, but it only saves it to a file. (None of VLC-s other output modules currently support it). Neither can VLC send a stream out with POST method.

**FFmpeg** is great on multiplexing and output, but the windows version can not access *DirectShow*, which your sound card uses to give access to the microphone data.

So we are going to usee them both together: VLC will access the audio, compress it to a temporary format (mp3). FFmpeg will connect to VLC to get the audio and VFW for the video, re-encodes audio to Ogg-Vorbis and video to VP8, multiplex them into a WebM container and POSTs it to the given URL.

*VLC* will listen to the local port: 8089 for *FFmpeg* to connect, so firewalls should be aware.

> **Assumptions:**
> 
> - server name: `example.com`
> - stream name: `first`
> - stream password: `secret`

    vlc -I dummy dshow:// --sout \
    "#transcode{vcodec=none,acodec=mp3,ab=128,channels=2,samplerate=44100} :http{mux=ts,dst=127.0.0.1:8089/}" \
    --dshow-vdev=none --no-sout-rtp-sap --no-sout-standard-sap --sout-keep

<!-- -->

    ffmpeg -f vfwcap -r 16 -i 0 -i http://localhost:8089/ -g 52 \
    -acodec libvorbis -ab 64k -vcodec libvpx -vb 448k \
    -f matroska http://example.com:8080/publish/first?password=secret

### On Linux Systems

*FFmpeg* can be used with the following command line (see assumptions above):

    ffmpeg -f video4linux2 -s 320x240 -r 16 -i /dev/video0 -f oss -i /dev/dsp \
    -g 52 -acodec libvorbis -ab 64k -vcodec libvpx -vb 448k \
    -f webm http://example.com:8080/publish/first?password=secret


## PUBLISHING H.264

### On Windows Systems

For the peculiarities of FFmpeg on Windows please see the section above (about WebM). I recommend using OBS or similar product over FFmpeg.

### On Linux Systems

The following command will stream media from your the system's webcam and microphone. Note that the *High* profile and *level 4.0* is used. Also tested and working is *Medium* profile with *level 3.1*.

    ffmpeg -f video4linux2 -s 320x240 -r 16 -i /dev/video0 -f oss -i /dev/dsp \
    -g 52 -strict experimental -acodec aac -ab 56k -vcodec libx264 -vb 452k \
    -profile:v high -level 40 -r 16 \
    -f flv "rtmp://example.com:8081/publish/first?secret"


## TESTING THE INSTALLATION

You can test the installation with the downloadable sample video, *univac.webm*. The file is encoded with an average of 512Kbps. *FFmpeg* can send the stream in real-time (real bit rate) to the server with the following command:

    ffmpeg -re -i univac.webm -vcodec copy -acodec copy \
    -f webm http://localhost:8080/publish/first?password=secret

You can watch it by positioning your (WebM-capable) browser to the following address:

    http://localhost:8080/consume/first
