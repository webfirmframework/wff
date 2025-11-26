wffGlobalConst('wffWS',
new function() {

	var encoder = wffGlobal.encoder;
	var decoder = wffGlobal.decoder;
	var getTime = function() {
	    return Date.now ? Date.now() : new Date().getTime();
    };
	
	// (null == undefined) is true

    // last timeout id
	var prevTmt = null;
	//last reconnect interval obj
	var prevIntvl = null;
	var webSocket = null;
	var inDataQ = [];
	var sendQData = null;
	var lastWSPingTime = getTime();
	//websocket re-openable by hearbeat internal

	this.ping = function() {
        if (wffGlobal.WS_HRTBT > 0 && wffGlobal.WS_HRTBT_TMT > 0) {
            if (prevTmt !== null) {
                clearTimeout(prevTmt);
            }
            prevTmt = setTimeout(function(){
            lastWSPingTime = getTime();
            prevTmt = null;
            try{wffWS.send([]);}catch(e){wffWS.closeSocket();}
            }, wffGlobal.WS_HRTBT);
        }
	};

	this.openSocket = function(wsUrl) {

		// Ensures only one connection is open at a time
		if (webSocket !== null
				&& webSocket.readyState !== WebSocket.CLOSED 
				&& webSocket.readyState !== WebSocket.CLOSING) {
			console.log("WebSocket is already opened.");
			return;
		}

		if (webSocket) {
			var fn = function(event){};
			webSocket.onopen = fn;
			webSocket.onmessage = fn;
			webSocket.onclose = fn;
			webSocket.onerror = fn;
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
				    if (binary.length == 0) {
				        lastWSPingTime = getTime();
                        wffWS.ping();
				    }

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
				lastWSPingTime = getTime();
                wffWS.ping();
			}catch(e){
				wffLog(e);
			}
		};

		webSocket.onmessage = function(event) {
			try {
				var binary = new Int8Array(event.data);
				console.log(binary);
				
				if (binary.length < 4) {
				    if (binary.length == 0) {
				        lastWSPingTime = getTime();
				        wffWS.ping();
				    }
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
				lastWSPingTime = getTime();
				if (prevTmt === null) {
					wffWS.ping();
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
				} else if (prevIntvl !== null && webSocket
				&& (webSocket.readyState === WebSocket.CONNECTING || webSocket.readyState === WebSocket.OPEN)) {
				    clearInterval(prevIntvl);
				    prevIntvl = null;
				}
			}, wffGlobal.WS_RECON);
		};
		webSocket.onerror = function(event) {
			try{webSocket.close();}catch(e){wffLog(e);}
		};
	};

	/**
	 * Sends the bytes to the server
	 */
	this.send = function(bytes) {
		if (bytes.length > 0) {
		    var bin = bytes;
		    if (wffGlobal.LOSSLESS_COMM && bin.length > 4) {
		        // this id bytes should always be length 4
		        bin = wffBMUtil.getBytesFromInt(wffGlobal.getUniqueClientSidePayloadId());
		        for (var i = 0; i < bytes.length; i++) {
                    bin.push(bytes[i]);
		        }
		    }
			inDataQ.push(bin);
			if (sendQData !== null) {
				sendQData();
			}
		} else if (webSocket !== null && webSocket.readyState === WebSocket.OPEN) {
			webSocket.send(new Int8Array(bytes).buffer);
		}
	};

	this.closeSocket = function() {
		try {
			if (webSocket !== null 
				&& webSocket.readyState === WebSocket.OPEN) {
				webSocket.close();
			}
		} catch(e){wffLog(e);}
	};

	this.getState = function() {
		if (webSocket !== null) {
			return webSocket.readyState;
		}
		return -1;
	};

	this.checkCon = function() {
        if (webSocket !== null && wffWS
        && wffGlobal.WS_HRTBT > 0 && wffGlobal.WS_HRTBT_TMT > 0
        && (getTime() - lastWSPingTime) >= (wffGlobal.WS_HRTBT + wffGlobal.WS_HRTBT_TMT)) {
            if (webSocket.readyState === WebSocket.CONNECTING) {
                lastWSPingTime = getTime();
            } else {
                if(prevIntvl !== null) {
                    clearInterval(prevIntvl);
                    prevIntvl = null;
                }
                var oldWS = webSocket;
                var fn = function(event){};
                oldWS.onopen = fn;
                oldWS.onmessage = fn;
                oldWS.onclose = fn;
                oldWS.onerror = fn;
                webSocket = null;

                //should be after nullifying webSocket
                if (wffGlobal.LOSSLESS_COMM) {
                    var taskNameValue = wffTaskUtil.getTaskNameValue(wffGlobal.taskValues.TASK, wffGlobal.taskValues.CLIENT_SIDE_PING_ON_NEW_WS_OPEN);
                    var nameValues = [taskNameValue];
                    var wffBM = wffBMUtil.getWffBinaryMessageBytes(nameValues);
                    wffWS.send(wffBM);
                }

                lastWSPingTime = getTime();
                wffWS.openSocket(wffGlobal.WS_URL);
                try {
                    if (oldWS !== null && oldWS.readyState === WebSocket.OPEN) {
                        oldWS.close();
                    }
                } catch(e){wffLog(e);}
            }
        }
    };
});
