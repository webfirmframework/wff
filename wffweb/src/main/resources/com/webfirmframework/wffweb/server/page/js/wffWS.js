/**
 * 
 */

var wffWS = new function() {

	var encoder = wffGlobal.encoder;
	var decoder = wffGlobal.decoder;

	var webSocket;

	this.openSocket = function(wsUtl) {

		// Ensures only one connection is open at a time
		if (webSocket !== undefined
				&& webSocket.readyState !== WebSocket.CLOSED 
				&& webSocket.readyState !== WebSocket.CLOSING) {
			console.log("WebSocket is already opened.");
			return;
		}

		// Create a new instance of websocket
		webSocket = new WebSocket(wsUtl);

		// this is required to send binary data
		webSocket.binaryType = 'arraybuffer';

		webSocket.onopen = function(event) {
			console.log("onopen", event);

			if (event.data === undefined) {
				console.log("event.data === undefined");
				return;
			}

			var binary = new Int8Array(event.data);
			wffClientCRUDUtil.invokeTasks(binary);

		};

		webSocket.onmessage = function(event) {
			console.log("onmessage", event);

			// console.log("onmessage");
			// console.dir(event);

			var binary = new Int8Array(event.data);
			console.log(binary);

			// for (var i = 0; i < binary.length; i++) {
			// console.log(i, binary[i]);
			// }

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
		};

		webSocket.onclose = function(event) {
			console.log("onclose", event);
			setTimeout(function() {
				if (!webSocket || webSocket.readyState == 3) {
					console.log("2 seconds loop");
					wffWS.openSocket(wffGlobal.WS_URL);
				}
			}, 2000);
		};
	};

	/**
	 * Sends the bytes to the server
	 */
	this.send = function(bytes) {
		webSocket.send(new Int8Array(bytes));
	};

	this.closeSocket = function() {
		webSocket.close();
	};

};
