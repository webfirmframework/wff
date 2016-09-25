var wffClientMethods = new function() {

	getStringFromBytes = function(utf8Bytes) {
		return wffGlobal.decoder.decode(new Uint8Array(utf8Bytes));
	};

	this.exePostFun = function(wffBMBytes) {

		var nameValues = wffBMUtil.parseWffBinaryMessageBytes(wffBMBytes);

		var taskNameValue = nameValues[0];
		// var taskName = getStringFromBytes(taskNameValue.name);
		var taskValue = taskNameValue.values[0][0];

		if (taskNameValue.name[0] != wffGlobal.taskValues.TASK) {
			return false;
		}

		if (taskValue == wffGlobal.taskValues.INVOKE_POST_FUNCTION) {

			console.log('taskValues.INVOKE_POST_FUNCTION');

			var funString = getStringFromBytes(nameValues[1].name);

			var values = nameValues[1].values;
			var jsObject = null;

			if (values.length > 0) {
				var value = values[0];
				if (value.length > 0) {
					var bmObjBytes = nameValues[1].values[0];
					jsObject = new JsObjectFromBMBytes(bmObjBytes, true);
				}
			}

			var func;
			if (window.execScript) {
				func = window.execScript("(function(jsObject){" + funString
						+ "})");
			} else {
				func = eval("(function(jsObject){" + funString + "})");
			}
			func(jsObject);

			return true;
		}

		return false;
	};
};