var wffBMClientEvents = new function() {

	window.wffRemovePrevBPInstanceInvoked = false;
	
	
	var encoder = wffGlobal.encoder;
	var decoder = wffGlobal.decoder;
	
	var wffRemoveBPInstance = function(wffInstanceId) {
		var nameValues = [];

		var wffInstanceIdBytes = encoder.encode(wffInstanceId);

		//taskNameValue
		var tnv = wffTaskUtil.getTaskNameValue(
				wffGlobal.taskValues.TASK,
				wffGlobal.taskValues.REMOVE_BROWSER_PAGE);

		nameValues.push(tnv);

		var nameValue = {
			'name' : wffInstanceIdBytes,
			'values' : []
		};

		nameValues.push(nameValue);

		var wffBM = wffBMUtil.getWffBinaryMessageBytes(nameValues);

		wffWS.send(wffBM);
	};
	
	this.wffRemoveBPInstance = wffRemoveBPInstance;

	this.wffRemovePrevBPInstance = function() {
		
		console.log('wffRemovePrevBPInstance ');
		console.log('wffGlobal.REMOVE_PREV_BP_ON_INITTAB ' + wffGlobal.REMOVE_PREV_BP_ON_INITTAB);
		console.log('window.wffRemovePrevBPInstanceInvoked ' + window.wffRemovePrevBPInstanceInvoked);
		
		if (!wffGlobal.REMOVE_PREV_BP_ON_INITTAB || 
				window.wffRemovePrevBPInstanceInvoked) {
			return;
		}
		
		window.wffRemovePrevBPInstanceInvoked = true;
		console.log('sessionStorage WFF_INSTANCE_ID ' + sessionStorage.getItem('WFF_INSTANCE_ID'));
		

		var wffInstanceId = sessionStorage.getItem('WFF_INSTANCE_ID');
		
		if (typeof wffInstanceId !== "undefined"
				&& wffInstanceId !== wffGlobal.INSTANCE_ID) {
			
			console.log('wffInstanceId != INSTANCE_ID ');

			wffRemoveBPInstance(wffInstanceId);

		}
		
		sessionStorage.setItem('WFF_INSTANCE_ID', wffGlobal.INSTANCE_ID);
	};

	this.wffInitialWSOpen = function() {
		var nameValues = [];

		//taskNameValue
		var tnv = wffTaskUtil.getTaskNameValue(
			wffGlobal.taskValues.TASK,
			wffGlobal.taskValues.INITIAL_WS_OPEN);

		nameValues.push(tnv);

		var l = window.location;

		if (l) {
			var h = l.href.endsWith('#') ? '#' : l.hash;
			var pthNV = {
				'name': [wffGlobal.taskValues.SET_URI],
				'values': [[wffGlobal.uriEventInitiator.BROWSER], encoder.encode(l.pathname + l.search + h)]
			};
			nameValues.push(pthNV);
		}

		if (typeof localStorage !== "undefined") {
			var itms = [];
			for (var k in localStorage) {
				var ndx = k.lastIndexOf('_wff_token');
				if (ndx > -1 && k.endsWith('_wff_token')) {
					try {
						var itemObj = JSON.parse(localStorage.getItem(k));
						itms.push({ k: k.substring(0, ndx), v: itemObj.v, id: itemObj.id, wt: itemObj.wt });
					} catch (e) {
						wffLog('corrupted token data found in localStorage', e);
					}
				}
			}
			if (itms.length > 0) {
				var bmBts = new WffBMArray(itms).getBMBytes();
				var nv = {
					'name': [wffGlobal.taskValues.SET_LS_TOKEN],
					'values': [bmBts]
				};
				nameValues.push(nv);
			}

		}

		var wffBM = wffBMUtil.getWffBinaryMessageBytes(nameValues);

		wffWS.send(wffBM);
	};

};
