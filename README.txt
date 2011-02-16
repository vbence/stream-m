README
=======

"stream.m" is a streaming media server. It currently works much like shoutcast
but instead of MP3 it relays a live WebM stream.


FRAGMENT SIZES
===============

The live stream consists of fragments (self-contained units of frames without
referencing any frame outside the fragment). A fragment starts with a keyframe
therefrore it is important that the encoder put in keyframes regularly as
this determines the size of the framents.

The ideal fragment size is around 200 kBytes (or 1600 kbits). The keyframe
interval can be calculated with this forula:
1600k / <bitrate> * <framerate>

e.g. if you are publishing an 500 kbit stream with 16 fps, then:
1600 / 500 * 16 = 51.2
(or 1600000 / 500000 * 16 = 51.2)
so every 52nd video frame should be a keyframe.

The server splits fragments when it seems necessary. A soft minimum for frame
size is currently 100k (no new fragment is started if a new keyframe arrives
within 100 kBytes from a previous keyframe). The hard maximum is 1024 kBytes
wich currently seems enough.


TRANSFER
=========

All operations are done over HTTP. The stream is POSTed with the password as a
GET attribute in the URL (so firewalls and throttling ISP's should not be a
problem).

The port number can be set in the configuration file. Default port is: 8080


HTTP ACCES
===========

Streams are identified by a name. The program refers to this as StreamID or
streamname. Note that the < and > characters are used just to signal that
substitution is expected, they must not be included in the real URL.


The name and password of each stream is defined in the config file. A stream
must be POSTed to this URL to start broadcasting:

/publish/<streamname>?password=<streampass>


A stream can be accessed (watched) on the following URL. You may want to insert
this URL into a HTML5 <video> tag:

/consume/<streamname>


Realtime information can be acquired thru an AJAX based console for a certain
stream on the following URL (giving the name and password for the chosen
stream on the UI):

/console/client.html


RUNNING THE SERVER
===================

java StreamingServer <configfile>

The release version has the classes in a .jar archive. Befire running the
server you should edit the sample config file (change password and choose a
stream name). So you will end up with something like:

java -cp lib/stream-m.jar StreamingServer server.conf


PUBLISHING ON WINDOWS
======================

In theory several open source tools can be used for this. Practically you will
need two of them if you want to stream from your webcam in windows.

VLC can access your recording hardware and encode WebM, but it only saves it to
a file. (None of VLC-s other output modules currently support it). Neither can
VLC send a stream out with POST method.

FFmpeg is great on muxing and output, but the windows version can not access
DirectShow, which your soundcard uses to give access to the microphone data.

So we are going to usee them both together: VLC will access the audio, compress
it to a temporary format (mp3). FFmpeg will connect to VLC to get the audio
and VFW for the video, re-encodes audio to ogg-vorbis and video to VP8, mux
them into a WebM container and POSTs it to the given URL.


VLC will listen to the local port: 8081 for FFmpeg to connect, so paranoid
firewalls should be aware. Assumptions:
server name: example.com
stream name: first
stream password: secret

vlc -I dummy dshow:// --sout "#transcode{vcodec=none,acodec=mp3,ab=128,channels=2,samplerate=44100}:http{mux=ts,dst=127.0.0.1:8081/}" --dshow-vdev=none --no-sout-rtp-sap --no-sout-standard-sap --sout-keep
ffmpeg -f vfwcap -r 16 -i 0 -i http://localhost:8081/ -g 52 -acodec libvorbis -ab 64k -vcodec libvpx -vb 448k -f matroska http://example.com:8080/publish/first?password=secret


PUBLISHING ON LINUX
====================

...is not tested yet. But you will probably be allright with something like:

ffmpeg -f video4linux2 -s 320x240 -r 16 -i /dev/video0 -f oss -i /dev/dsp -g 52 -acodec libvorbis -ab 64k -vcodec libvpx -vb 448k -f matroska http://example.com:8080/publish/first?password=secret

