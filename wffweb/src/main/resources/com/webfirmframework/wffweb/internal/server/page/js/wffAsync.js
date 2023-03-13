/*
 * these methods are for wffweb developers
 */
window.wffAsync = new function() {

	var encoder = wffGlobal.encoder;

	this.callbackFunctions = {};
	var uuid = 0;
	this.generateUUID = function() {
		return (++uuid).toString();
	};

	this.serverMethod = function(methodName, jsObject) {

		console.log('methodName', methodName);

		this.invoke = function(callback) {

			console.log('callback', callback);

			var callbackFunId;

			if (typeof callback === "function") {

				callbackFunId = wffAsync.generateUUID();

				wffAsync.callbackFunctions[callbackFunId] = callback;

				console.log('callback', callback);

			} else if (typeof callback === "undefined") {
				// NOP
			} else {
				throw "invoke function takes function argument";
			}

			var taskNameValue = wffTaskUtil.getTaskNameValue(
					wffGlobal.taskValues.TASK,
					wffGlobal.taskValues.INVOKE_CUSTOM_SERVER_METHOD);

			var methodNameBytes = encoder.encode(methodName);

			var values = [];

			if (typeof jsObject !== "undefined") {

				if (typeof jsObject === "object") {
					var argumentBMObject = new WffBMObject(jsObject);
					var argBytes = argumentBMObject.getBMBytes();
					values.push(argBytes);
				} else {
					throw "argument value should be an object";
				}
			}

			var nameValue = {
				'name' : methodNameBytes,
				'values' : values
			};

			var nameValues = [ taskNameValue, nameValue ];

			if (typeof callbackFunId !== "undefined") {
				var nameValueCallbackFun = {
					'name' : encoder.encode(callbackFunId),
					'values' : []
				};
				nameValues.push(nameValueCallbackFun);
			}

			var wffBM = wffBMUtil.getWffBinaryMessageBytes(nameValues);

			wffWS.send(wffBM);

		};

		return this;
	};


	var setServerURIWithCallbackPvt = function(uri, callback, initiator, rplc) {

		var taskNameValue = wffTaskUtil.getTaskNameValue(
			wffGlobal.taskValues.TASK,
			wffGlobal.taskValues.CLIENT_PATHNAME_CHANGED);
		var nameValue = {
			'name': encoder.encode(uri),
			'values': []
		};
		var rplcByt = rplc ? 1 : 0;
		//NB: should be checked as undefined as its value could be zero
		if (typeof initiator !== "undefined" && initiator >= 0 && initiator < wffGlobal.uriEventInitiator.size) {
			nameValue.values.push([initiator, rplcByt]);
		} else {
			nameValue.values.push([wffGlobal.uriEventInitiator.CLIENT_CODE, rplcByt]);
		}

		var nameValues = [taskNameValue, nameValue];

		if (callback) {
			var callbackFunId = wffAsync.generateUUID();
			wffAsync.callbackFunctions[callbackFunId] = callback;

			var nameValueCallbackFun = {
				'name': encoder.encode(callbackFunId),
				'values': []
			};

			nameValues.push(nameValueCallbackFun);
		}

		var wffBM = wffBMUtil.getWffBinaryMessageBytes(nameValues);
		wffWS.send(wffBM);
	};

	this.setServerURIWithCallback = function(uri, callback, initiator) {setServerURIWithCallbackPvt(uri, callback, initiator, false);};

	this.setServerURI = function(uri) { setServerURIWithCallbackPvt(uri, undefined, wffGlobal.uriEventInitiator.CLIENT_CODE, false); };

	var throwInvalidSetURIArgException = function() {
		throw "Invalid argument found in setURI function call. " +
		"Eg: wffAsync.setURI('/sampleuri', function(e){console.log('uri changed');}, function(e){console.log('uri changed, after server change');}, false);, " +
		"wffAsync.setURI('/sampleuri', function(e){console.log('uri changed');}, function(e){console.log('uri changed, after server change');});, " +
		"wffAsync.setURI('/sampleuri', function(e){console.log('uri changed');});, " +
		"wffAsync.setURI('/sampleuri'); or " +
		"wffAsync.setURI('/sampleuri', null, null, false);";
	};
	
	//currentURI
	var cURI = function() {
		var l = window.location;
		var h = l.href.endsWith('#') ? '#' : l.hash;
		return l.pathname + l.search + h;
	};

	this.setURI = function(uri, onSetURI, afterSetURI, replace) {

		if (typeof onSetURI !== "undefined" && onSetURI !== null && typeof onSetURI !== "function") {
			throwInvalidSetURIArgException();
		}
		if (typeof afterSetURI !== "undefined" && afterSetURI !== null && typeof afterSetURI !== "function") {
			throwInvalidSetURIArgException();
		}
		if (typeof replace !== "undefined" && replace !== null && typeof replace !== "boolean") {
			throwInvalidSetURIArgException();
		}

		var uriBefore = cURI();
		
		var l = window.location;
        var nvkSer = false;
		if (uri === uriBefore || uri === l.href) {
			if (!(l.hash.startsWith('#') || l.href.endsWith('#'))) {
				nvkSer = true;
			}
		} else {
			nvkSer = true;
			if (replace) {
				window.history.replaceState({ by: 'setURI' }, document.title, uri);
			} else {
				window.history.pushState({ by: 'setURI' }, document.title, uri);
			}
		}
		
		wffGlobal.getAndUpdateLocation();

		var uriAfter = cURI();
		
		if (nvkSer) {
			var wffEvent = { uriBefore: uriBefore, uriAfter: uriAfter, origin: "client", initiator: 'clientCode', replace: replace ? true : false };

			var callbackWrapper = afterSetURI;

			if (typeof wffGlobalListeners !== "undefined") {
				if (wffGlobalListeners.onSetURI) {
					try {
						wffGlobalListeners.onSetURI(wffEvent);
					} catch (e) {
						wffLog("wffGlobalListeners.onSetURI threw exception when wffAsync.setURI is called", e);
					}
				}
				//NB: should be copied before using inside callbackWrapper
				var afterSetURIGlobal = wffGlobalListeners.afterSetURI;

				if (afterSetURIGlobal) {
					callbackWrapper = function() {
						if (afterSetURI) {
							try {
								afterSetURI(wffEvent);
							} catch (e) {
								wffLog("The third argument threw exception when wffAsync.setURI is called", e);
							}
						}

						try {
							afterSetURIGlobal(wffEvent);
						} catch (e) {
							wffLog("wffGlobalListeners.afterSetURI threw exception when wffAsync.setURI is called", e);
						}
					};
				}

			}

			try {
				if (onSetURI) {
					onSetURI(wffEvent);
				}
			} catch (e) {
				wffLog("The second argument threw exception when wffAsync.setURI is called", e);
			}
			//NB: should be uriAfter to be consistent with uri pattern
			setServerURIWithCallbackPvt(uriAfter, callbackWrapper, wffGlobal.uriEventInitiator.CLIENT_CODE, replace);
		}
	};

};

// sample usage
//
// wffAsync.serverMethod('methodName', {'key1':'hi'}).invoke(function(obj) {
// console.log('yes success2', obj);
// });
//
