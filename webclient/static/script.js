var msDiff = 0;
var graphTime = 0;
var lastRefresh = -1;

var drawRoundRect = function (context, x, y, width, height, radius) {
	//context.fillRect(x, y, width, height);
	
	x += 0.5;
	y += 0.5;
	
	context.beginPath();
	context.moveTo(x, y);
	//context.lineTo(x + width - radius, y);
	context.arcTo(x + width, y, x + width, y + radius, radius);
	context.arcTo(x + width, y + height, x + width - radius, y + height, radius);
	context.arcTo(x, y + height, x, y + height - radius, radius);
	context.arcTo(x, y, x + radius, y, radius);
	context.closePath();
	context.fill();
};

var RealtimeGraph = function (id) {
	if (typeof(id) == "undefined")
		return;
	var div = document.createElement("div");
	this.div = div;
	div.setAttribute("id", id);
	div.style.position = "absolute";
	var canvas = document.createElement("canvas");
	canvas.setAttribute("id", id + "-graph");
	div.appendChild(canvas);
	this.canvas = canvas;
	var context = canvas.getContext("2d");
	this.context = context;
	RealtimeGraph.instances.push(this);
};
RealtimeGraph.prototype = new Object();

RealtimeGraph.msDiff = 0;
RealtimeGraph.graphTime = 0;
RealtimeGraph.lastRefresh = -1;
RealtimeGraph.instances = new Array();
RealtimeGraph.buffer = document.createElement("canvas");
RealtimeGraph.bufferContext = RealtimeGraph.buffer.getContext("2d");

RealtimeGraph.prototype.getElement = function() {
	return this.div;
};

RealtimeGraph.prototype.getContext = function() {
	return this.context;
};

RealtimeGraph.prototype._moveGraph = function(pixSize) {
	//var imageData = this.context.getImageData(pixSize, 0, this.canvas.width, this.canvas.height);
	//this.context.putImageData(imageData, 0, 0);
	//this.context.putImageData(this.context.getImageData(pixSize, 0, this.canvas.width, this.canvas.height), 0, 0);
	RealtimeGraph.bufferContext.clearRect(0, 0, RealtimeGraph.buffer.width, RealtimeGraph.buffer.height);
	RealtimeGraph.bufferContext.drawImage(this.canvas, 0, 0);
	this.context.clearRect(0, 0, this.canvas.width, this.canvas.height);
	this.context.drawImage(RealtimeGraph.buffer, -pixSize, 0);
};

RealtimeGraph.getTimeX = function (time) {
	return Math.floor(RealtimeGraph.graphTime / 100) - Math.floor(time / 100);
};

RealtimeGraph.actualize = function() {
	RealtimeGraph.moveToTime(+ new Date() + RealtimeGraph.msDiff);
};

RealtimeGraph.moveToTime = function(time) {
	
	time = Math.floor(time / 100) * 100;
	
	if (time > RealtimeGraph.graphTime) {
		var pixSize = Math.floor((time - RealtimeGraph.graphTime) / 100);
		RealtimeGraph.graphTime = time;
		RealtimeGraph.msDiff = time - new Date();
		RealtimeGraph._moveGraphs(pixSize);
	}
};

RealtimeGraph._moveGraphs = function(pixSize) {
	var i;
	for (i=0; i<RealtimeGraph.instances.length; i++)
		RealtimeGraph.instances[i]._moveGraph(pixSize);
};



var LevelGraph = function (id, width, height, style) {
	RealtimeGraph.call(this, id);
	if (typeof(id) == "undefined")
		return;

	this.width = width;
	this.height = height;
	
	if (width > RealtimeGraph.buffer.width)
		RealtimeGraph.buffer.width = width;
	if (height > RealtimeGraph.buffer.height)
		RealtimeGraph.buffer.height = height;
	
	this.canvas.width = width;
	this.canvas.height = height;
	this.div.style.width = (width - 40) + "px";
	this.div.style.overflow = "hidden";
	
	/*var graphDiv = this.div;
	this.div.onmousemove = function (event) {
		alert(RealtimeGraph.graphTime - (width - event.offsetX) * 100);
		window.status = "move";
	}*/
	
	this.style = style;
	
	this.buffer = new Array(width);
	this.bufferOffset = 0;
	this.bufferTime = 0;
	
	this.valSum = 0;
	this.valCount = 0;
	
	this.scaleFactor = 1;
};
LevelGraph.prototype = new RealtimeGraph();

LevelGraph.prototype.setPoint = function(time, value) {
	var x = RealtimeGraph.getTimeX(time);
	if (x < 0) {
		x = RealtimeGraph.getTimeX(time);
	}
	
	// canvas lines
	x += 0.5;
	this.context.strokeStyle = this.style;
	this.context.beginPath();
	this.context.moveTo(this.canvas.width - x, this.canvas.height - 0.5);
	this.context.lineTo(this.canvas.width - x, this.canvas.height - 0.5 - (value * this.scaleFactor));
	this.context.stroke();
	
	var timeDiff = Math.floor(time / 100) - Math.floor(this.bufferTime / 100);
	timeDiff = Math.min(timeDiff, this.width);
	var i;
	for (i=1; i<=timeDiff; i++) {
		var lastVal = this.buffer[(this.bufferOffset + i) % this.buffer.length];
		if (lastVal > 0) {
			this.valSum -= lastVal;
			this.valCount--;
		}					
		this.buffer[(this.bufferOffset + i) % this.buffer.length] = 0;
	}
	
	this.bufferOffset = (this.bufferOffset + timeDiff) % this.buffer.length;
	this.bufferTime = time;
	this.buffer[this.bufferOffset] = value;
	
	if (value != 0) {
		this.valSum += value;
		this.valCount++;
	}
};

LevelGraph.prototype.rescale = function() {
	var max = -1;

	var i;
	for (i=0; i<this.width; i++) {
		var value = this.buffer[i];
		if (value > max)
			max = value;
	}
	
	var newFactor = this.canvas.height / max * (4/5);
	
	if (newFactor > this.scaleFactor * 5 / 4 || newFactor < this.scaleFactor * 3 / 4) {
		this.scaleFactor = newFactor;
		this.redraw();
	}
	
};

LevelGraph.prototype.redraw = function() {
	this.context.clearRect(0, 0, this.canvas.width, this.canvas.height);
	this.context.strokeStyle = this.style;
	
	var x = RealtimeGraph.getTimeX(this.bufferTime - 100 * this.width);
	
	//console.log("x: " + x + ", w: " + this.width);
	
	var i;
	for (i=1; i<=this.width; i++) {
		var value = this.buffer[(this.bufferOffset + i) % this.buffer.length] * this.scaleFactor;
		if (value != 0) {
			this.context.beginPath();
			this.context.moveTo(this.width - 1 - x + i + 0.5, this.canvas.height - 0.5);
			this.context.lineTo(this.width - 1 - x + i + 0.5, this.canvas.height - 0.5 - value);
			this.context.stroke();
		}
	}
	
};

var ScaleLevelMap = function (id, width, height, style) {
	LevelGraph.call(this, id, width, height, style);
	if (typeof(id) == "undefined")
		return;

	var scaleCanvas = document.createElement("canvas");
	scaleCanvas.setAttribute("id", id + "-scale");
	scaleCanvas.width = width;
	scaleCanvas.height = height;
	scaleCanvas.style.position = "absolute";
	this.scaleCanvas = scaleCanvas;
	this.div.insertBefore(scaleCanvas, this.canvas);
	
};
ScaleLevelMap.prototype = new LevelGraph();

ScaleLevelMap.prototype.redraw = function() {
	LevelGraph.prototype.redraw.call(this);
	
	var context = this.scaleCanvas.getContext("2d");
	
	context.clearRect(0, 0, this.scaleCanvas.width, this.scaleCanvas.height);
	
	
	// maximum visible value
	var base = this.height / this.scaleFactor;
	
	console.log(this.height + ", " + this.scaleFactor + " = " + base);

	if (base <=0)
		return;
	
	var exp = 1;
	
	var divi = base >= 10 ? 10 : (1 / 10);
	while (base >= 10 || base < 1) {
		base /= divi;
		exp *= divi;
	}
	
	var step;
	if (base < 2.5) {
		step = 0.5 * exp;
	} else if (base < 5) {
		step = exp;
	} else {
		step = 2 * exp;
	}
	
	var fontSize = 11;
	context.font = fontSize + "px monospace";
	
	var y = 0;
	var lineValue = 0;
	while ((y = Math.round(lineValue * this.scaleFactor)) < this.height) {
		context.strokeStyle = "rgba(0,0,0,0.25)";
		context.beginPath();
		context.moveTo(0, this.height - 1 + 0.5 - y);
		context.lineTo(this.width - 1 + 0.5, this.height - 1 + 0.5 - y);
		context.stroke();
		
		// if there is at least a 10 pixel space over the line
		if (lineValue != 0 && y < this.height - 10) {
			var txt = Math.round(lineValue / exp * 100) / 100 * exp;
			metrics = context.measureText(txt);

			context.fillStyle = "rgba(0,0,0,0.45)";
			context.clearRect(0, this.height - 1 - y - (Math.round(fontSize / 2) + 1), metrics.width + 6, fontSize + 2);
			drawRoundRect(context, 1, this.height - 1 - y - (Math.round(fontSize / 2) + 1), metrics.width + 2, fontSize + 2, 3);

			//context.strokeStyle = "rgb(255,255,255)";
			context.fillStyle = "rgb(255,255,255)";
			context.fillText(txt, 2 + 0.5, this.height - 1 + 0.5 - y + (Math.round(fontSize / 2) - 4));
		}
		
		lineValue += step / 2;
		
		y = Math.round(lineValue * this.scaleFactor);
		context.strokeStyle = "rgba(0,0,0,0.1)";
		context.beginPath();
		context.moveTo(0, this.height - 1 + 0.5 - y);
		context.lineTo(this.width - 1 + 0.5, this.height - 1 + 0.5 - y);
		context.stroke();

		lineValue += step / 2;
	}
};

var StativeLevelGraph = function (id, width, height, style) {
	ScaleLevelMap.call(this, id, width, height, style);
	
	if (typeof(id) == "undefined")
		return;
	
	this.lastTime = 0;
	this.paintTime = 0;
};
StativeLevelGraph.prototype = new ScaleLevelMap();

StativeLevelGraph.prototype._rollValue = function(startTime, endTime, value) {
	var timeDiff = Math.floor(endTime / 100) - Math.floor(startTime / 100);
	timeDiff = Math.min(timeDiff, this.width);
	
	var endDiff = Math.floor(endTime / 100) - Math.floor(this.bufferTime / 100);
	if (endDiff <= -this.width)
		return;
	if (endDiff < 0)
		timeDiff = Math.min(timeDiff, this.width + endDiff);
	
	var i;
	for (i=endDiff - timeDiff; i<=endDiff; i++) {
		var lastVal = this.buffer[(this.buffer.length * 2 + this.bufferOffset + i) % this.buffer.length];
		if (lastVal > 0) {
			this.valSum -= lastVal;
			this.valCount--;
		}
		this.buffer[(this.buffer.length * 2 + this.bufferOffset + i) % this.buffer.length] = value;
		this.valSum += value;
		this.valCount++;
	}
	
	if (endTime > this.bufferTime) {
		this.bufferTime = endTime;
		this.bufferOffset = (this.bufferOffset + endDiff) % this.buffer.length;
	}
};

StativeLevelGraph.prototype._drawBar = function(timeStart, timeEnd, value) {
	//return;
	var paintStart = RealtimeGraph.getTimeX(timeStart);
	var paintEnd = RealtimeGraph.getTimeX(timeEnd);
	
	this.context.fillRect(this.canvas.width - paintStart, this.canvas.height - 0.5 - this.lastValue * this.scaleFactor, this.canvas.width - 1 - paintEnd, this.canvas.height);	
	
	this.paintTime = timeEnd;
};

StativeLevelGraph.prototype.setPoint = function(time, value) {
	
	if (this.lastTime > 0) {
		this._rollValue(this.lastTime, RealtimeGraph.graphTime - 100, this.lastValue);
		this.context.fillStyle = this.style;
		if (this.paintTime > 0)
			this._drawBar(this.paintTime, RealtimeGraph.graphTime - 100, this.lastValue);
	
	}
	
	// saving last value
	this.lastTime = time;
	this.lastValue = value;

	ScaleLevelMap.prototype.setPoint.call(this, time, value);

};

StativeLevelGraph.prototype.redraw = function() {
	ScaleLevelMap.prototype.redraw.call(this);
	
	this.context.fillStyle = this.style;
	if (this.paintTime > 0)
		this._drawBar(this.paintTime, RealtimeGraph.graphTime - 100, this.lastValue);
	
	//var pixSize = RealtimeGraph.getTimeX(this.lastTime);
	//this.context.fillRect(this.canvas.width - pixSize, this.canvas.height - 0.5 - this.lastValue * this.scaleFactor, this.canvas.width - 1, this.canvas.height);	
};

StativeLevelGraph.prototype._moveGraph = function(pixSize) {
	ScaleLevelMap.prototype._moveGraph.call(this, pixSize);
	
	var paintSize = RealtimeGraph.getTimeX(this.paintTime);
	if (this.lastTime != 0 && paintSize >= 39) {
		this.context.fillStyle = this.style;
		this.context.fillRect(this.canvas.width - paintSize, this.canvas.height - 0.5 - this.lastValue * this.scaleFactor, this.canvas.width - 1, this.canvas.height);	
		this.paintTime = RealtimeGraph.graphTime;
	}
};


var ToggleGraph = function (id, width, height, style) {
	LevelGraph.call(this, id, width, height, style);
	this.paint = false;
	this.lastTime = 0;
};
ToggleGraph.prototype = new LevelGraph();

ToggleGraph.prototype._moveGraph = function(pixSize) {
	//var imageData = this.context.getImageData(pixSize, 0, this.canvas.width, this.canvas.height);
	//this.context.putImageData(imageData, 0, 0);
	
	RealtimeGraph.bufferContext.clearRect(0, 0, RealtimeGraph.buffer.width, RealtimeGraph.buffer.height);
	RealtimeGraph.bufferContext.drawImage(this.canvas, 0, 0);
	this.context.clearRect(0, 0, this.canvas.width, this.canvas.height);
	this.context.drawImage(RealtimeGraph.buffer, -pixSize, 0);

	if (RealtimeGraph.getTimeX(this.lastTime) >= 39)
		this._refreshBars();
};

ToggleGraph.prototype._refreshBars = function() {
	var pixSize = RealtimeGraph.getTimeX(this.lastTime);
	//console.log(pixSize);
	if (this.paint) {
		this.context.fillStyle = this.style;
		this.context.fillRect(this.canvas.width - pixSize, 0, this.canvas.width - 1, this.canvas.height);
	} else {
		this.context.clearRect(this.canvas.width - pixSize, 0, this.canvas.width - 1, this.canvas.height);
	}
	this.lastTime = RealtimeGraph.graphTime;
};

ToggleGraph.prototype.setPoint = function(time, value) {
	var x = RealtimeGraph.getTimeX(time);
	if (x < 0) {
		RealtimeGraph.moveToTime(time);
		x = RealtimeGraph.getTimeX(time);
	}
	this._refreshBars();
	
	this.paint ^= true;
	if (this.paint) {
		this.context.fillStyle = this.style;
		this.context.fillRect(this.canvas.width - x, 0, this.canvas.width, this.canvas.height);
	} else {
		this.context.clearRect(this.canvas.width - x, 0, this.canvas.width, this.canvas.height);
	}
	this.lastTime = RealtimeGraph.graphTime;
};

var showScreen = function(name, className) {
	var elem = document.getElementById("screen-" + name);
	if (elem.style.display == "block")
		return;
	var divs = document.getElementsByTagName("div");
	for (i=0; i<divs.length; i++)
		if (divs[i].className == className)
			divs[i].style.display = "none";
	elem.style.display = "block";
};

var backGraph;
var inputGraph;
var outputGraph;
var viewerGraph;

function boot() {
	
	var element;
	var container;
	
	// last 40 pixels of all graphs are hidden with css (buffer area)

	container = document.getElementById("bandwidth");
	
	//backGraph = new ToggleGraph("backGraph", 628, 320, "rgb(230,230,230)");
	backGraph = new ToggleGraph("backGraph", 628, 320, "rgba(0,0,0,0.1)");
	element = backGraph.getElement();
	container.appendChild(element);
	
	inputGraph = new ScaleLevelMap("inputGraph", 628, 160, "rgb(250,180,0)");
	element = inputGraph.getElement();
	container.appendChild(element);
	
	outputGraph = new ScaleLevelMap("outputGraph", 628, 160, "rgb(0,180,250)");
	element = outputGraph.getElement();
	element.style.marginTop = "160px";
	container.appendChild(element);
	
	
	var container = document.getElementById("viewer");

	//viewerGraph = new ScaleLevelMap("viewerGraph", 628, 160, "rgb(80,120,250)");
	viewerGraph = new StativeLevelGraph("viewerGraph", 628, 160, "rgb(80,120,250)");
	element = viewerGraph.getElement();
	container.appendChild(element);
	
	
	setInterval(function() {
		inputGraph.rescale();
		outputGraph.rescale();
		viewerGraph.rescale();
	}, 2000);
	
	timeRefresh();
	
	showScreen("login", "screen");
}

function calculateGraph(arr) {
	var i;
	maxTime = -1;
	for (i=0; i<arr.length; i++) {
		maxTime = Math.max(maxTime, arr[i].time);
	}
	
	//RealtimeGraph.moveToTime(maxTime);
	
	/*
	var msTime = +new Date();
	
	maxTime = Math.floor(maxTime / 100) * 100;
	
	if (maxTime > graphTime) {
		var pixSize = Math.floor((maxTime - graphTime) / 100);
		moveGraph(pixSize);
		graphTime = maxTime;
		msDiff = maxTime - msTime;
	}
	*/
}

function processPacket(arr) {
	var i;
	calculateGraph(arr);
	for (i=0; i<arr.length; i++)
		processEvent(arr[i]);
}

function sendRequest() {
	
	// cancel pending requests
	if (pendingRequest != null) {
		clearTimeout(pendingRequest);
		pendingRequest = null;
	}
	
	// request
	var req = new XMLHttpRequest();
	var length = 0;
	var uri = "/info/" + document.getElementById("streamID").value + "?password=" + document.getElementById("password").value;
	req.onreadystatechange = function(event) {
		if (req.readyState == 3) {
			// INTERACTIVE
			
			if (req.status != 200)
				return;
			
			// showing "realtime" screen
			showScreen("realtime", "screen");

			var dataPack = req.responseText.substring(length);
			
			if (dataPack.length > 0) {
				//try {
					var arr = eval("[" + dataPack.substring(0, dataPack.length-1) + "]");
					processPacket(arr);
				//} catch (e) {
				//	console.log(dataPack);
				//}
			}
			
			length = req.responseText.length;
		
		} else if (req.readyState == 4) {
			// COMPLETED
			
			if (req.status == 200 || req.status == 503) {
				log(req.statusText + " (Retrying in every 5 seconds.)");
				showScreen("realtime", "screen");
				length = 0;
				pendingRequest = setTimeout(function() {
					req.open("GET", uri, true);
					req.send();
				}, 5000);
			} else if (req.status == 0) {
				log("No response from the server.");
			} else {
				log(req.statusText);
			}
			
		}
	};
	
	req.open("GET", uri, true);
	req.send();
	
}
pendingRequest = null;

boot();

function processEvent(event) {
	
	//console.log(event);
	
	if (event.cls == 2) {
		
		// bandwidth event
		
		if (event.type == 1) {
			
			// input
			
			inputGraph.setPoint(event.time, event.bytes / 1024);
			/*
			var x = getTimeX(event.time);
			var canvas = document.getElementById("canvas-input");
			var context = canvas.getContext("2d");
			context.strokeStyle = "rgb(255,180,0)";
			context.beginPath();
			context.moveTo(canvas.width - x, canvas.height - 1);
			context.lineTo(canvas.width - x, canvas.height - 1 - (event.bytes / 100));
			context.stroke();
			*/
			
		} else if (event.type == 2) {
			
			// output
			outputGraph.setPoint(event.time, event.bytes / 1024);
			
			/*
			var x = getTimeX(event.time);
			var canvas = document.getElementById("canvas-output");
			var context = canvas.getContext("2d");
			context.strokeStyle = "rgb(255,0,180)";
			context.beginPath();
			context.moveTo(canvas.width - x, canvas.height - 1);
			context.lineTo(canvas.width - x, canvas.height - 1 - (event.bytes / 100));
			context.stroke();
			*/
		}
		
	} else if (event.cls == 1) {
		
		// server event
		
		
		if (event.type == 3) {
			
			// INPUT_FRAGMENT_START
			backGraph.setPoint(event.time, 1000);
			/*
			var x = getTimeX(event.time);
			var canvas = document.getElementById("canvas-input");
			var context = canvas.getContext("2d");
			context.strokeStyle = "rgb(120,180,255)";
			context.beginPath();
			context.moveTo(canvas.width - x, canvas.height - 1);
			context.lineTo(canvas.width - x, 0);
			context.stroke();
			*/
			//viewerGraph.setPoint(event.time, 0.120);
		}
		
	} else if (event.cls == 4) {
		// server status event
		
		//alert("t: " + event.time + ", cc: " + event.clientCount);
		viewerGraph.setPoint(event.time, event.clientCount);
	}
}

logTimeout = null;
logHideTimeout = null;

function log(text) {
	var now = new Date();
	var log = document.getElementById("log");
	log.innerHTML = '<b>' + now.getHours() + ':' + now.getMinutes() + ':' + now.getSeconds() + '</b> ' + text;
	log.style.opacity = "0.96";
	log.style.display = "block";
	
	if (typeof(logTimeout) != "undefined" && logTimeout != null)
		clearTimeout(logTimeout);
	logTimeout = setTimeout(function() {
		log.style.opacity = "0.66";
		logTimeout = null;
	}, 8000);
	
	if (typeof(logHideTimeout) != "undefined" && logHideTimeout != null)
		clearTimeout(logHideTimeout);
	logHideTimeout = setTimeout(function() {
		log.style.display = "none";
		logHideTimeout = null;
	}, 10000);
}


function getTimeX(time) {
	return Math.round((graphTime - time) / 100) + 0.5;
}

function moveGraph(x) {
	moveCanvas(document.getElementById("canvas-input"), x);
	moveCanvas(document.getElementById("canvas-output"), x);
}

function moveCanvas(canvas, x) {
	var context = canvas.getContext("2d");
	var imageData = context.getImageData(x, 0, canvas.width, canvas.height);
	context.putImageData(imageData, 0, 0);
}

function timeRefresh() {
	RealtimeGraph.actualize();
	setTimeout(timeRefresh, 2000);
}


function btnClick() {
	/*
	var txt = "";
	var i;
	for (i=1; i<=inputGraph.buffer.length; i++)
		txt += inputGraph.buffer[(inputGraph.buffer.length + i) % inputGraph.buffer.length] + ", ";
	console.log(txt);
	*/
	
	/*
	var base = 0.0034;
	var exp = 1;
	
	var mul = base > 10 ? 10 : 1 / 10;
	while (base >= 10 || base < 1) {
		base /= mul;
		exp *= mul;
	}
	
	var step;
	if (base < 2.5) {
		step = 0.5 * exp;
	} else if (base < 5) {
		step = exp;
	} else {
		step = 2 * exp;
	}
	
	console.log(base + ", " + exp + " -> " + step);
	*/
	
	inputGraph.setPoint(+new Date(), 32);
	outputGraph.setPoint(+new Date(), 0.120);
	
}
