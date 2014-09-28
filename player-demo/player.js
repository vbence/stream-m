(function (global) {
    "use strict";
    
    global.org = global.org || {};
    global.org.czentral = global.org.czentral || {};
    global.org.czentral.streamm = global.org.czentral.streamm || {};
    
    var streamm = global.org.czentral.streamm;
    
    streamm.Player = function(div, options) {
        var document;
        var video;
        var mediaSource;
        var sourceBuffer;
        
        var xhr;
        
        // init
        (function () {
            document = div.ownerDocument;
            video = document.createElement("video");
            video.setAttribute("controls", "controls");
            div.appendChild(video);
            
            video.addEventListener("play", onVideoPlay, false);
            video.addEventListener("pause", onVideoPause, false);
            
            initMediaSource();
        })();
        
        function onVideoPlay(e) {
            sourceBuffer = mediaSource.addSourceBuffer(options["contentType"]);
            sourceBuffer.addEventListener("updateend", function () {
                
                // seek to the start time of the buffer
                if (video.currentTime === 0 && video.readyState > 0) {
                    video.currentTime = sourceBuffer.buffered.start(0);
                }
            }, false);

            xhr = makeRequest(options["url"] + "?singleFragment=1", xhrReady);
        }
        
        function onVideoPause(e) {
            xhr.abort();
            initMediaSource();
        };
        
        function initMediaSource() {
            mediaSource = global.MediaSource
                    ? new global.MediaSource()
                    : new global.WebkitMediaSource();
            if (video.src) {
                global.URL.revokeObjectURL(video.src);
            }
            video.src = global.URL.createObjectURL(mediaSource);
        }

        function makeRequest(url, handler) {
            var xhr = new XMLHttpRequest();
            xhr.responseType = "arraybuffer";
            xhr.onload = handler;
            xhr.open("GET", url, true);
            xhr.send();
            return xhr;
        }

        function xhrReady(e) {
            var xhr = e.target;
            if (xhr.status != 200) {
                console.log("HTTP error: " + xhr.status);
                return;
            }

            var response = xhr.response;
            
            // get find moof box
            var offset = 0;
            do {
                var boxName = String.fromCharCode.apply(null, new Uint8Array(response.slice(offset + 4, (offset + 4) + 4)));
                var boxLength = new DataView(response, offset, 4).getUint32(0);
                //console.log(boxName, boxLength);

                if (boxName == "moof") {
                    break;
                }

                offset += boxLength;
            } while (offset < response.byteLength);

            // read sequence number out of moof box
            var sequenceNumber = 0;
            if (offset < response.byteLength) {
                sequenceNumber = new DataView(response, offset + 20, 4).getUint32(0);
            }
            
            // process result
            var sourceBuffer;
            if (mediaSource.sourceBuffers.length > 0) {
                sourceBuffer = mediaSource.sourceBuffers[0];
                sourceBuffer.appendBuffer(response);
            } else {
                console.log("ERROR: no source.");
                return;
            }
            
            // request next fragment
            xhr = makeRequest(options["url"] + "?singleFragment=1&sendHeader=0&fragmentSequence=" + (sequenceNumber + 1), xhrReady);
        }

    }
    
})(window);
