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
        
        var xhr;
        
        // init
        (function () {
            document = div.ownerDocument;
            video = document.createElement("video");
            video.setAttribute("controls", "controls");
            div.appendChild(video);
            
            video.addEventListener("play", onVideoPlay, false);
            video.addEventListener("pause", onVideoPause, false);
            
            if (options["debug"]) {
                attachVideoDebug(video);
            }
            
            initMediaSource();
        })();
        
        function onVideoPlay(e) {
            xhr = makeRequest(options["url"] + "?singleFragment=1", xhrReady);
        }
        
        function onVideoPause(e) {
            xhr.removeEventListener("load", xhrReady, false);
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
            xhr.addEventListener("load", handler, false);
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
            
            var sequenceHeader = xhr.getResponseHeader("X-Sequence");
            if (sequenceHeader === null) {
                throw "Response header X-Sequence not found.";
            }
            var sequenceNumber = parseInt(sequenceHeader, 10);
            
            // process result
            var sourceBuffer;
            if (mediaSource.sourceBuffers.length > 0) {
                sourceBuffer = mediaSource.sourceBuffers[0];
            } else {
                sourceBuffer = mediaSource.addSourceBuffer(xhr.getResponseHeader("Content-type"));
                if (options["debug"]) {
                    attachBufferDebug(sourceBuffer);
                }
                sourceBuffer.addEventListener("updateend", function () {
                    // seek to the start time of the buffer
                    if (video.currentTime === 0 && video.readyState > 0) {
                        video.currentTime = sourceBuffer.buffered.start(0);
                    }
                }, false);
            }
            sourceBuffer.appendBuffer(response);
            
            // request next fragment
            xhr = makeRequest(options["url"] + "?singleFragment=1&sendHeader=0&fragmentSequence=" + (sequenceNumber + 1), xhrReady);
        }
        
        function attachVideoDebug(video) {
            var events = ["loadstart", "progress", "suspend", "abort", "error", "emptied", "stalled", "loadedmetadata", "loadeddata", "canplay", "canplaythrough", "playing", "waiting", "seeking", "seeked", "ended", "durationchange", "timeupdate", "play", "pause", "ratechange", "resize", "volumechange"];
            for (var i=0; i<events.length; i++) {
                video.addEventListener(events[i], logVideoEvent);
            }
        }
        
        function attachBufferDebug(sourceBuffer) {
            var events = ["updatestart", "update", "updateend", "error", "abort" ];
            for (var i=0; i<events.length; i++) {
                sourceBuffer.addEventListener(events[i], logVideoEvent);
            }
        }
        
        function logVideoEvent(e) {
            var debugTable = document.getElementById("videoDebug");
            if (!debugTable) {
                debugTable = document.createElement("table");
                debugTable.id = "videoDebug";
                debugTable.innerHTML = '<tr><td>time</td><td>target</td><td>name</td><td>ct</td><td>ns</td><td>rs</td><td>dur</td><td>error</td><td>bc</td><td>last buffer</td></tr>';
                document.body.appendChild(debugTable);
            }
            var tr = document.createElement("tr");
            if (debugTable.children.length > 1) {
                debugTable.insertBefore(tr, debugTable.children[1]);
            } else {
                debugTable.appendChild(tr);
            }
            var date = new Date();
            tr.innerHTML = '<td>' + (date.getHours() + ":" + date.getMinutes() + ":<b>" + date.getSeconds() + "." + date.getMilliseconds()) + '</b></td><td>' + Object.getPrototypeOf(e.target).constructor.name + '</td><th>' + e.type + '</th><td>' + video.currentTime + '</td><td>' + video.networkState + '</td><td>' + video.readyState + '</td><td>' + video.duration + '</td><td>' + (video.error ? video.error.code : '-') + '</td><td>' + video.buffered.length + '</td><td>' + (video.buffered.length ? (video.buffered.start(video.buffered.length - 1) + " - " + video.buffered.end(video.buffered.length - 1)) : 0) + '</td>';
        }

    }
    
})(window);
