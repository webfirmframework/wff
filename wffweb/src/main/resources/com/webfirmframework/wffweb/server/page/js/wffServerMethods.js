var wffTaskUtil = new function () {
	
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
};

var wffServerMethods = new function () {

	var encoder = wffGlobal.encoder;
	//PD for preventDefault
	var invokeAsyncPD = function(event, tag, attrNmOrNdx, prvntDflt) {
		
		if(prvntDflt) {
			event.preventDefault();
		}
		
		var attr = typeof attrNmOrNdx === 'string' ? attrNmOrNdx : wffGlobal.NDXD_ATRBS[attrNmOrNdx];
		
		var taskNameValue = wffTaskUtil.getTaskNameValue(wffGlobal.taskValues.TASK, wffGlobal.taskValues.INVOKE_ASYNC_METHOD);

		
		var attrBytes = encoder.encode(attr);
		
		var nameValue = {'name':wffTagUtil.getWffIdBytesFromTag(tag), 'values':[attrBytes]};
		var nameValues = [taskNameValue, nameValue];
		var wffBM = wffBMUtil.getWffBinaryMessageBytes(nameValues);
		wffWS.send(wffBM);
	};	
	//never ever rename iapd
	this.iapd = function(event, tag, attrNmOrNdx) {
		invokeAsyncPD(event, tag, attrNmOrNdx, true);
	};
	
	var invokeAsync = function(event, tag, attrNmOrNdx) {
		invokeAsyncPD(event, tag, attrNmOrNdx, false);
	};
	//never ever rename ia
	this.ia = invokeAsync;
	
	var invokeAsyncWithPreFunPD = function(event, tag, attrNmOrNdx, preFun, prvntDflt) {
		if(prvntDflt) {
			event.preventDefault();
		}
		
		if (preFun(event, tag)) {
			var taskNameValue = wffTaskUtil.getTaskNameValue(wffGlobal.taskValues.TASK, wffGlobal.taskValues.INVOKE_ASYNC_METHOD);

			var attr = typeof attrNmOrNdx === 'string' ? attrNmOrNdx : wffGlobal.NDXD_ATRBS[attrNmOrNdx];
			
			var attrBytes = encoder.encode(attr);
			
			var nameValue = {'name':wffTagUtil.getWffIdBytesFromTag(tag), 'values':[attrBytes]};
			var nameValues = [taskNameValue, nameValue];
			var wffBM = wffBMUtil.getWffBinaryMessageBytes(nameValues);
			wffWS.send(wffBM);
		}
		
	};
	//never ever rename iawpfpd
	this.iawpfpd = function(event, tag, attrNmOrNdx, preFun) {
		invokeAsyncWithPreFunPD(event, tag, attrNmOrNdx, preFun, true);
	};
	
	var invokeAsyncWithPreFun = function(event, tag, attrNmOrNdx, preFun) {
		invokeAsyncWithPreFunPD(event, tag, attrNmOrNdx, preFun, false);
	};
	
	//never ever rename iawpf
	this.iawpf = invokeAsyncWithPreFun;
	
	var invokeAsyncWithPreFilterFunPD = function(event, tag, attrNmOrNdx, preFun, filterFun, prvntDflt) {
		if(prvntDflt) {
			event.preventDefault();
		}
		
		if (preFun(event, tag)) {
			var taskNameValue = wffTaskUtil.getTaskNameValue(wffGlobal.taskValues.TASK, wffGlobal.taskValues.INVOKE_ASYNC_METHOD);

			var attr = typeof attrNmOrNdx === 'string' ? attrNmOrNdx : wffGlobal.NDXD_ATRBS[attrNmOrNdx];
			
			var attrBytes = encoder.encode(attr);
			
			var jsObject = filterFun(event, tag);
			var argumentBMObject = new WffBMObject(jsObject);
			var argBytes = argumentBMObject.getBMBytes();
			
			var nameValue = {'name':wffTagUtil.getWffIdBytesFromTag(tag), 'values':[attrBytes, argBytes]};
			var nameValues = [taskNameValue, nameValue];
			var wffBM = wffBMUtil.getWffBinaryMessageBytes(nameValues);
			
			
			
			wffWS.send(wffBM);
		}
		
	};	
	
	//never ever rename iawpffpd
	this.iawpffpd = function(event, tag, attrNmOrNdx, preFun, filterFun) {
		invokeAsyncWithPreFilterFunPD(event, tag, attrNmOrNdx, preFun, filterFun, true);
	};
	
	var invokeAsyncWithPreFilterFun = function(event, tag, attrNmOrNdx, preFun, filterFun) {
		invokeAsyncWithPreFilterFunPD(event, tag, attrNmOrNdx, preFun, filterFun, false);
	};
	//never ever rename iawpff
	this.iawpff = invokeAsyncWithPreFilterFun;
	
	var invokeAsyncWithFilterFunPD = function(event, tag, attrNmOrNdx, filterFun, prvntDflt) {
		if(prvntDflt) {
			event.preventDefault();
		}
		
		var taskNameValue = wffTaskUtil.getTaskNameValue(wffGlobal.taskValues.TASK, wffGlobal.taskValues.INVOKE_ASYNC_METHOD);

		var attr = typeof attrNmOrNdx === 'string' ? attrNmOrNdx : wffGlobal.NDXD_ATRBS[attrNmOrNdx];
		
		var attrBytes = encoder.encode(attr);
		
		var jsObject = filterFun(event, tag);
		var argumentBMObject = new WffBMObject(jsObject);
		var argBytes = argumentBMObject.getBMBytes();
		
		var nameValue = {'name':wffTagUtil.getWffIdBytesFromTag(tag), 'values':[attrBytes, argBytes]};
		var nameValues = [taskNameValue, nameValue];
		var wffBM = wffBMUtil.getWffBinaryMessageBytes(nameValues);
		
		
		
		wffWS.send(wffBM);
		
	};
	//never ever rename iawffpd
	this.iawffpd = function(event, tag, attrNmOrNdx, filterFun) {
		invokeAsyncWithFilterFunPD(event, tag, attrNmOrNdx, filterFun, true);
	};
	
	var invokeAsyncWithFilterFun = function(event, tag, attrNmOrNdx, filterFun) {
		invokeAsyncWithFilterFunPD(event, tag, attrNmOrNdx, filterFun, false);
	};
	
	//never ever rename iawff
	this.iawff = invokeAsyncWithFilterFun;
	
	//TODO
//	this.invokeAsyncWithPreFilterPostFun = function(tag, attr, preFun, filterFun, postFun) {
//		console.log('invokeAsyncWithPreFilterFun tag', tag);
//		
//		if (preFun(tag)) {
//			var taskNameValue = wffTaskUtil.getTaskNameValue(wffGlobal.taskValues.TASK, wffGlobal.taskValues.INVOKE_ASYNC_METHOD);
//
//			
//			var attrBytes = encoder.encode(attr);
//			
//			var argumentBMObject = filterFun(tag);
//			
//			var argBytes = argumentBMObject.getBMBytes();
//			
//			var nameValue = {'name':wffTagUtil.getWffIdBytesFromTag(tag), 'values':[attrBytes, argBytes]};
//			var nameValues = [taskNameValue, nameValue];
//			var wffBM = wffBMUtil.getWffBinaryMessageBytes(nameValues);
//			
//			
//			
//			wffWS.send(wffBM);
//		}
//		
//	};

	
};
//never ever rename ia
var wffSM = wffServerMethods;

