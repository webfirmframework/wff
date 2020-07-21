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
	var invokeAsyncPD = function(event, tag, attr, prvntDflt) {
		
		if(prvntDflt) {
			event.preventDefault();
		}
		
		var taskNameValue = wffTaskUtil.getTaskNameValue(wffGlobal.taskValues.TASK, wffGlobal.taskValues.INVOKE_ASYNC_METHOD);

		
		var attrBytes = encoder.encode(attr);
		
		var nameValue = {'name':wffTagUtil.getWffIdBytesFromTag(tag), 'values':[attrBytes]};
		var nameValues = [taskNameValue, nameValue];
		var wffBM = wffBMUtil.getWffBinaryMessageBytes(nameValues);
		wffWS.send(wffBM);
	};	
	//never ever rename iapd
	this.iapd = invokeAsyncPD;
	
	var invokeAsync = function(event, tag, attr) {
		invokeAsyncPD(event, tag, attr, false);
	};
	//never ever rename ia
	this.ia = invokeAsync;
	
	var invokeAsyncWithPreFunPD = function(event, tag, attr, preFun, prvntDflt) {
		if(prvntDflt) {
			event.preventDefault();
		}
		
		if (preFun(event, tag)) {
			var taskNameValue = wffTaskUtil.getTaskNameValue(wffGlobal.taskValues.TASK, wffGlobal.taskValues.INVOKE_ASYNC_METHOD);

			
			var attrBytes = encoder.encode(attr);
			
			var nameValue = {'name':wffTagUtil.getWffIdBytesFromTag(tag), 'values':[attrBytes]};
			var nameValues = [taskNameValue, nameValue];
			var wffBM = wffBMUtil.getWffBinaryMessageBytes(nameValues);
			wffWS.send(wffBM);
		}
		
	};
	//never ever rename iawpfpd
	this.iawpfpd = invokeAsyncWithPreFunPD;
	
	var invokeAsyncWithPreFun = function(event, tag, attr, preFun) {
		invokeAsyncWithPreFunPD(event, tag, attr, preFun, false);
	};
	
	//never ever rename iawpf
	this.iawpf = invokeAsyncWithPreFun;
	
	var invokeAsyncWithPreFilterFunPD = function(event, tag, attr, preFun, filterFun, prvntDflt) {
		if(prvntDflt) {
			event.preventDefault();
		}
		
		if (preFun(event, tag)) {
			var taskNameValue = wffTaskUtil.getTaskNameValue(wffGlobal.taskValues.TASK, wffGlobal.taskValues.INVOKE_ASYNC_METHOD);

			
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
	this.iawpffpd = invokeAsyncWithPreFilterFunPD;
	
	var invokeAsyncWithPreFilterFun = function(event, tag, attr, preFun, filterFun) {
		invokeAsyncWithPreFilterFunPD(event, tag, attr, preFun, filterFun, false);
	};
	//never ever rename iawpff
	this.iawpff = invokeAsyncWithPreFilterFun;
	
	var invokeAsyncWithFilterFunPD = function(event, tag, attr, filterFun, prvntDflt) {
		if(prvntDflt) {
			event.preventDefault();
		}
		
		var taskNameValue = wffTaskUtil.getTaskNameValue(wffGlobal.taskValues.TASK, wffGlobal.taskValues.INVOKE_ASYNC_METHOD);

		
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
	this.iawffpd = invokeAsyncWithFilterFunPD;
	
	var invokeAsyncWithFilterFun = function(event, tag, attr, filterFun) {
		invokeAsyncWithFilterFunPD(event, tag, attr, filterFun, false);
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

