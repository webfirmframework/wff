wffGlobalConst('wffTaskUtil',
new function () {
	var encoder = wffGlobal.encoder;
	this.getTaskNameValue = function(nameByte, valueByte) {
//		var tUtf8Bytes = encoder.encode(name);
//		var cUtf8Bytes = encoder.encode(value);
		var taskNameValue = {
			'name' : [nameByte],
			'values' : [ [valueByte] ]
		};
		return taskNameValue;
	};
});
//never ever change name
wffGlobalConst('wffSM',
new function () {

	var encoder = wffGlobal.encoder;
	
	var getAttrBytesForServer = function(attrNmOrNdx) {
		
		var attrBytes = [];
		
		if (typeof attrNmOrNdx === 'string') {
			var ndx = wffGlobal.NDXD_VNT_ATRBS.indexOf(attrNmOrNdx);
			if (ndx != -1) {
				var ndxByts = wffBMUtil.getOptimizedBytesFromInt(ndx);
				//0 represents rest of the bytes are optimized int bytes 
				attrBytes.push(0);
	            for (var i = 0; i < ndxByts.length; i++) {
	            	attrBytes.push(ndxByts[i]);
				}
			} else {
				//0 (byte value) is a null char in charset so no need to start with 0 (byte value)
				// an attr name will never start with 0 (byte value)
				attrBytes = encoder.encode(attrNmOrNdx);
			}
		} else {
			var ndxByts = wffBMUtil.getOptimizedBytesFromInt(attrNmOrNdx);
			//0 represents rest of the bytes are optimized int bytes
			attrBytes.push(0);
			for (var i = 0; i < ndxByts.length; i++) {
	            attrBytes.push(ndxByts[i]);
			}
		}
		return attrBytes;
	};
	
	//PD for preventDefault
	var invokeAsyncPD = function(event, tag, attrNmOrNdx, prvntDflt) {
		
		if(prvntDflt) {
			event.preventDefault();
		}	
		
		var taskNameValue = wffTaskUtil.getTaskNameValue(wffGlobal.taskValues.TASK, wffGlobal.taskValues.INVOKE_ASYNC_METHOD);

		var attrBytes = getAttrBytesForServer(attrNmOrNdx);
		
		var nameValue = {'name':wffTagUtil.getWffIdBytesFromTag(tag), 'values':[attrBytes]};
		var nameValues = [taskNameValue, nameValue];
		var wffBM = wffBMUtil.getWffBinaryMessageBytes(nameValues);
		wffWS.send(wffBM);
	};	
	//never ever rename, iapd
	this.b = function(event, tag, attrNmOrNdx) {
		invokeAsyncPD(event, tag, attrNmOrNdx, true);
	};
	
	var invokeAsync = function(event, tag, attrNmOrNdx) {
		invokeAsyncPD(event, tag, attrNmOrNdx, false);
	};
	//never ever rename, ia
	this.a = invokeAsync;
	
	var invokeAsyncWithPreFunPD = function(event, tag, attrNmOrNdx, preFun, prvntDflt) {
		if(prvntDflt) {
			event.preventDefault();
		}
		
		var invoked = false;
		var actionPerform = function() {
			invoked = true;
			var taskNameValue = wffTaskUtil.getTaskNameValue(wffGlobal.taskValues.TASK, wffGlobal.taskValues.INVOKE_ASYNC_METHOD);
			var attrBytes = getAttrBytesForServer(attrNmOrNdx);
			
			var nameValue = {'name':wffTagUtil.getWffIdBytesFromTag(tag), 'values':[attrBytes]};
			var nameValues = [taskNameValue, nameValue];
			var wffBM = wffBMUtil.getWffBinaryMessageBytes(nameValues);
			wffWS.send(wffBM);
		};
		
		//a wrapper object is important to avoid script injection
		var action = new function() {
			this.perform = function() {
				actionPerform();
			};
		};
		
		if (preFun(event, tag, action)) {
			if(!invoked) {
				actionPerform();
			}			
		}
		
	};
	//never ever rename, iawpfpd
	this.f = function(event, tag, attrNmOrNdx, preFun) {
		invokeAsyncWithPreFunPD(event, tag, attrNmOrNdx, preFun, true);
	};
	
	var invokeAsyncWithPreFun = function(event, tag, attrNmOrNdx, preFun) {
		invokeAsyncWithPreFunPD(event, tag, attrNmOrNdx, preFun, false);
	};
	
	//never ever rename, iawpf
	this.e = invokeAsyncWithPreFun;
	
	var invokeAsyncWithPreFilterFunPD = function(event, tag, attrNmOrNdx, preFun, filterFun, prvntDflt) {
		if(prvntDflt) {
			event.preventDefault();
		}
		
		var invoked = false;
		var actionPerform = function() {
			invoked = true;
			var taskNameValue = wffTaskUtil.getTaskNameValue(wffGlobal.taskValues.TASK, wffGlobal.taskValues.INVOKE_ASYNC_METHOD);

			var attrBytes = getAttrBytesForServer(attrNmOrNdx);

			var jsObject = filterFun(event, tag);
			var argumentBMObject = new WffBMObject(jsObject);
			var argBytes = argumentBMObject.getBMBytes();
					
			var nameValue = {'name':wffTagUtil.getWffIdBytesFromTag(tag), 'values':[attrBytes, argBytes]};
			var nameValues = [taskNameValue, nameValue];
			var wffBM = wffBMUtil.getWffBinaryMessageBytes(nameValues);

			wffWS.send(wffBM);
		};		
			
		//a wrapper object is important to avoid script injection
		var action = new function() {
			this.perform = function() {				
				actionPerform();
			};			
		};
		
		if (preFun(event, tag, action)) {
			if (!invoked) {
				actionPerform();	
			}			
		}
		
	};	
	
	//never ever rename, iawpffpd
	this.h = function(event, tag, attrNmOrNdx, preFun, filterFun) {
		invokeAsyncWithPreFilterFunPD(event, tag, attrNmOrNdx, preFun, filterFun, true);
	};
	
	var invokeAsyncWithPreFilterFun = function(event, tag, attrNmOrNdx, preFun, filterFun) {
		invokeAsyncWithPreFilterFunPD(event, tag, attrNmOrNdx, preFun, filterFun, false);
	};
	//never ever rename, iawpff
	this.g = invokeAsyncWithPreFilterFun;
	
	var invokeAsyncWithFilterFunPD = function(event, tag, attrNmOrNdx, filterFun, prvntDflt) {
		if(prvntDflt) {
			event.preventDefault();
		}
		
		var taskNameValue = wffTaskUtil.getTaskNameValue(wffGlobal.taskValues.TASK, wffGlobal.taskValues.INVOKE_ASYNC_METHOD);

		var attrBytes = getAttrBytesForServer(attrNmOrNdx);
		
		var jsObject = filterFun(event, tag);
		var argumentBMObject = new WffBMObject(jsObject);
		var argBytes = argumentBMObject.getBMBytes();
		
		var nameValue = {'name':wffTagUtil.getWffIdBytesFromTag(tag), 'values':[attrBytes, argBytes]};
		var nameValues = [taskNameValue, nameValue];
		var wffBM = wffBMUtil.getWffBinaryMessageBytes(nameValues);
		
		wffWS.send(wffBM);
	};
	//never ever rename, iawffpd
	this.d = function(event, tag, attrNmOrNdx, filterFun) {
		invokeAsyncWithFilterFunPD(event, tag, attrNmOrNdx, filterFun, true);
	};
	
	var invokeAsyncWithFilterFun = function(event, tag, attrNmOrNdx, filterFun) {
		invokeAsyncWithFilterFunPD(event, tag, attrNmOrNdx, filterFun, false);
	};
	
	//never ever rename, iawff
	this.c = invokeAsyncWithFilterFun;
	
});
wffGlobalConst('wffServerMethods',window.wffSM);
