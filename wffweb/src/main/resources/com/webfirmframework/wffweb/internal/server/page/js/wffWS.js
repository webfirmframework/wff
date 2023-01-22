
var wffWS = new function() {

	var encoder = wffGlobal.encoder;
	var decoder = wffGlobal.decoder;
	
	// (null == undefined) is true
	
	//last reconnect interval obj
	var prevIntvl = null;
	var webSocket = null;
	var inDataQ = [];
	var sendQData = null;

	this.openSocket = function(wsUrl) {

		// Ensures only one connection is open at a time
		if (webSocket !== null
				&& webSocket.readyState !== WebSocket.CLOSED 
				&& webSocket.readyState !== WebSocket.CLOSING) {
			console.log("WebSocket is already opened.");
			return;
		}

		// Create a new instance of websocket
		webSocket = new WebSocket(wsUrl);
		
		sendQData = function() {
			if (webSocket !== null && webSocket.readyState === WebSocket.OPEN) {
				var inData = [];
				var ndx = 0;
				var xp = false;
				for (ndx = 0; ndx < inDataQ.length; ndx++) { 
					var each = inDataQ[ndx]; 
					if (!xp) {
						try {
							webSocket.send(new Int8Array(each).buffer);
						} catch(e) {
							xp = true;
							inData.push(each);
						}
					} else {
						inData.push(each);
					}					
				}
				inDataQ = inData;
			}			
		};
		

		// this is required to send binary data
		webSocket.binaryType = 'arraybuffer';

		webSocket.onopen = function(event) {
			try {
				
				if(prevIntvl !== null) {
					clearInterval(prevIntvl);
					prevIntvl = null;
				}
				
				wffBMClientEvents.wffRemovePrevBPInstance();
				
				if (sendQData !== null) {
					sendQData();	
				}				

				if (typeof event.data === 'undefined') {
					return;
				}

				var binary = new Int8Array(event.data);
				
				if (binary.length < 4) {
					//invalid wff bm message so not to process
					return;
				}
				if ((wffGlobal.LOSSLESS_COMM || binary[0] == 0) && binary.length > 4) {
				    var payloadId = wffBMUtil.getIntFromOptimizedBytes([binary[0], binary[1], binary[2], binary[3]]);
				    // payloadId = 0 means it has no id but has placeholder for id
				    if (payloadId != 0 && payloadId != wffGlobal.getUniqueServerSidePayloadId()) {
				        wffGlobal.onPayloadLoss();
				    } else {
				        // TODO optimize later
				        var bin = [];
    				    for (var i = 4; i < binary.length; i++) {
    				        bin.push(binary[i]);
    				    }
                        wffClientCRUDUtil.invokeTasks(bin);
				    }
				} else {
				    wffClientCRUDUtil.invokeTasks(binary);
				}
			}catch(e){
				wffLog(e);
			}
		};

		webSocket.onmessage = function(event) {
			try {
				var binary = new Int8Array(event.data);
				console.log(binary);
				
				if (binary.length < 4) {
					//invalid wff bm message so not to process
					return;
				}

				// for (var i = 0; i < binary.length; i++) {
				// console.log(i, binary[i]);
				// }

				if ((wffGlobal.LOSSLESS_COMM || binary[0] == 0) && binary.length > 4) {
                	var payloadId = wffBMUtil.getIntFromOptimizedBytes([binary[0], binary[1], binary[2], binary[3]]);
                	// payloadId = 0 means it has no id but has placeholder for id
                	if (payloadId != 0 && payloadId != wffGlobal.getUniqueServerSidePayloadId()) {
                	    wffGlobal.onPayloadLoss();
                	    return;
                	} else {
                        // TODO optimize later
                        var bin = [];
                        for (var i = 4; i < binary.length; i++) {
                            bin.push(binary[i]);
                        }
                        binary = bin;
                	}
                }

				var executed = wffClientMethods.exePostFun(binary);
				if (!executed) {
					wffClientCRUDUtil.invokeTasks(binary);
				}

				// var wffMessage = wffBMUtil.parseWffBinaryMessageBytes(binary);
				// console.log('wffMessage', wffMessage);
				//
				// for (var i = 0; i < wffMessage.length; i++) {
				// var decodedString = decoder.decode(new Uint8Array(
				// wffMessage[i].name));
				// console.log('decodedString', decodedString,
				// 'wffMessage[i].values.length', wffMessage[i].values.length);
				// console.log('values');
				// for (var j = 0; j < wffMessage[i].values.length; j++) {
				// console.log('value', decoder.decode(new Uint8Array(
				// wffMessage[i].values[j])));
				// }
				// }

				// var string = new TextDecoder("utf-8").decode(binary);
				// console.log(string, string);

				// var binary = new Uint8Array(event.data.‌​length);
				// for (var i = 0; i < event.data.length; i++) {
				// console.log(i, event.data[i]);
				// // binary[i] = event.data[i];
				// }
				// console.dir(binary);
				// sendBinary(binary.bu‌​ffer);

				// console.log('going to writeResponse');
				// writeResponse(event.data);

				// var uint8array = new TextEncoder("utf-8").encode("¢");
				// var string = new TextDecoder("utf-8").decode(uint8array);
			}catch(e){
				wffLog(e);
			}
		};
		
		webSocket.onclose = function(event) {
			if(prevIntvl !== null) {
				clearInterval(prevIntvl);
				prevIntvl = null;
			}
			prevIntvl = setInterval(function() {
				if (webSocket === null || webSocket.readyState === WebSocket.CLOSED) {					
					wffWS.openSocket(wffGlobal.WS_URL);
				}
			}, wffGlobal.WS_RECON);
		};
		webSocket.onerror = function(event) {
			try{webSocket.close();}catch(e){wffLog("ws.close error");}
		};
	};

	/**
	 * Sends the bytes to the server
	 */
	this.send = function(bytes) {
		if (bytes.length > 0) {
		    var bin = bytes;
		    if (wffGlobal.LOSSLESS_COMM && bin.length > 4) {
		        bin = wffBMUtil.getBytesFromInt(wffGlobal.getUniqueClientSidePayloadId());
		        for (var i = 0; i < bytes.length; i++) {
                    bin.push(bytes[i]);
		        }
		    }
			inDataQ.push(bin);
			if (sendQData !== null) {
				sendQData();
			}
		} else {
			webSocket.send(new Int8Array(bytes).buffer);	
		}
	};

	this.closeSocket = function() {
		try {
			if (webSocket !== null 
				&& webSocket.readyState !== WebSocket.CONNECTING
				&& webSocket.readyState !== WebSocket.CLOSED) {
				webSocket.close();
			}
		} catch(e){}
	};

	this.getState = function() {
		if (webSocket !== null) {
			return webSocket.readyState;
		}
		return -1;
	};
};

