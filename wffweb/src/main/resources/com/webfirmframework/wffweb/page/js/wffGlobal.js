/*
 * this file must be loaded first.
 * this file should not be formatted
 */
var window.wffGlobal = new function() {
	console.log('test');
	var wffId = -1;
	
	this.getUniqueWffIntId = function() {
		return ++wffId;
	};
	
	this.encoder = new TextEncoder("utf-8");
	this.decoder = new TextDecoder("utf-8");
	
	this.taskValues = ${TASK_VALUES};
	this.WS_URL = "${WS_URL}";

};
