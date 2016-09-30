/**
 * 
 */

var wffClientCRUDUtil = new function() {

	var encoder = wffGlobal.encoder;
	var decoder = wffGlobal.decoder;

	getStringFromBytes = function(utf8Bytes) {
		return decoder.decode(new Uint8Array(utf8Bytes));
	};

	this.applyUpdates = function(wffBMBytes) {
		console.log('applying updates', document.getElementsByTagName('*'));

		var nameValues = wffBMUtil.parseWffBinaryMessageBytes(wffBMBytes);

		var taskNameValue = nameValues[0];
		// var taskName = getStringFromBytes(taskNameValue.name);
		var taskValue = taskNameValue.values[0][0];
		console.log('taskNameValue.name', taskNameValue.name);

		if (taskNameValue.name[0] != wffGlobal.taskValues.TASK) {
			return;
		}
		if (taskValue == wffGlobal.taskValues.ATTRIBUTE_UPDATED) {
			console.log('taskValue == "ATTRIBUTE_UPDATED"');

			for (var i = 1; i < nameValues.length; i++) {

				console.log('i', i);
				console.log('nameValues[i].values', nameValues[i].values);

				var attrNameValue = getStringFromBytes(nameValues[i].name);
				console.log('attrNameValue', attrNameValue);
				var indexOfSeparator = attrNameValue.indexOf('=');

				var attrNameValueArry = wffTagUtil
						.splitAttrNameValue(attrNameValue);
				var attrName = attrNameValueArry[0];
				var attrValue = attrNameValueArry[1];

				// if (indexOfSeparator != -1) {
				// attrName = attrNameValue.substring(0, indexOfSeparator);
				// attrValue = attrNameValue.substring(indexOfSeparator + 1,
				// attrNameValue.length);
				// } else {
				// attrName = attrNameValue;
				// attrValue = '';
				// }

				console.log('attrName', attrName, 'attrValue', attrValue);

				// var tagId = wffBMUtil
				// .getIntFromOptimizedBytes(nameValues[i].name);
				var wffIds = nameValues[i].values;

				// var tagName = getStringFromBytes(wffIds[0]);

				for (var j = 0; j < wffIds.length; j++) {
					console.log('j', j);

					var wffId = wffTagUtil.getWffIdFromWffIdBytes(wffIds[j]);

					var applicableTag = wffTagUtil.getTagByWffId(wffId);

					if (indexOfSeparator != -1) {
						applicableTag.setAttribute(attrName, attrValue);
					} else {
						applicableTag.setAttribute(attrName, "");
					}
				}

			}

		} else if (taskValue == wffGlobal.taskValues.REMOVED_ATTRIBUTE) {
			console.log('taskValue == "REMOVED_ATTRIBUTE"');

			for (var i = 1; i < nameValues.length; i++) {

				console.log('i', i);
				console.log('nameValues[i].values', nameValues[i].values);

				var attrName = getStringFromBytes(nameValues[i].name);

				console.log('attrName', attrName);

				// var tagId = wffBMUtil
				// .getIntFromOptimizedBytes(nameValues[i].name);
				var wffIds = nameValues[i].values;

				// var tagName = getStringFromBytes(wffIds[0]);

				for (var j = 0; j < wffIds.length; j++) {
					console.log('j', j);

					var wffId = wffTagUtil.getWffIdFromWffIdBytes(wffIds[j]);

					var applicableTag = wffTagUtil.getTagByWffId(wffId);

					applicableTag.removeAttribute(attrName);

				}

			}

		} else if (taskValue == wffGlobal.taskValues.APPENDED_CHILD_TAG
				|| taskValue == wffGlobal.taskValues.APPENDED_CHILDREN_TAGS) {

			// TODO comment this later
			if (taskValue == wffGlobal.taskValues.APPENDED_CHILD_TAG) {
				console.log('APPENDED_CHILD_TAG');
			} else if (taskValue == wffGlobal.taskValues.APPENDED_CHILDREN_TAGS) {
				console.log('APPENDED_CHILDREN_TAGS');
			}
			// end

			for (var i = 1; i < nameValues.length; i++) {
				var wffId = wffTagUtil
						.getWffIdFromWffIdBytes(nameValues[i].name);
				var values = nameValues[i].values;
				var tagName = getStringFromBytes(values[0]);

				var parent = wffTagUtil.getTagByTagNameAndWffId(tagName, wffId);

				for (var j = 1; j < values.length; j++) {
					// var htmlString = getStringFromBytes(values[j]);

					// var div = document.createElement('div');
					// div.innerHTML = htmlString;
					// var htmlNodes = div.firstChild;

					if (parent == undefined) {
						console.log('parent == undefined', 'tagName', tagName,
								'wffId', wffId);
					}

					var htmlNodes = wffTagUtil
							.createTagFromWffBMBytes(values[j]);

					parent.appendChild(htmlNodes);
				}
			}
		} else if (taskValue == wffGlobal.taskValues.REMOVED_TAGS) {

			console.log('wffGlobal.taskValues.REMOVED_TAGS nameValues.length '
					+ nameValues.length);

			for (var i = 1; i < nameValues.length; i++) {
				var wffId = wffTagUtil
						.getWffIdFromWffIdBytes(nameValues[i].name);
				var values = nameValues[i].values;
				var tagName = getStringFromBytes(values[0]);

				var tagToRemove = wffTagUtil.getTagByTagNameAndWffId(tagName,
						wffId);

				console.log('tagToRemove', tagToRemove, 'tagName', tagName,
						'wffId', wffId, 'count i', i);
				var parent = tagToRemove.parentNode;
				parent.removeChild(tagToRemove);

			}

		} else if (taskValue == wffGlobal.taskValues.REMOVED_ALL_CHILDREN_TAGS) {

			console.log('wffGlobal.taskValues.REMOVED_ALL_CHILDREN_TAGS');

			for (var i = 1; i < nameValues.length; i++) {
				var wffId = wffTagUtil
						.getWffIdFromWffIdBytes(nameValues[i].name);
				var values = nameValues[i].values;
				var tagName = getStringFromBytes(values[0]);

				var parentTag = wffTagUtil.getTagByTagNameAndWffId(tagName,
						wffId);
				while (parentTag.firstChild) {
					parentTag.removeChild(parentTag.firstChild);
				}
			}

		} else if (taskValue == wffGlobal.taskValues.MOVED_CHILDREN_TAGS) {

			console
					.log('wffGlobal.taskValues.MOVED_CHILDREN_TAGS nameValues.length '
							+ nameValues.length);

			for (var i = 1; i < nameValues.length; i++) {
				var currentParentWffId = wffTagUtil
						.getWffIdFromWffIdBytes(nameValues[i].name);
				var values = nameValues[i].values;
				var currentParentTagName = getStringFromBytes(values[0]);

				var currentParentTag = wffTagUtil.getTagByTagNameAndWffId(
						currentParentTagName, currentParentWffId);

				var childWffId = wffTagUtil.getWffIdFromWffIdBytes(values[1]);
				var childTagName = getStringFromBytes(values[2]);
				var childTag = wffTagUtil.getTagByTagNameAndWffId(childTagName,
						childWffId);

				if (childTag !== undefined) {
					var previousParent = childTag.parentNode;
					console.log('childTag !== undefined', childTag);
					previousParent.removeChild(childTag);
				} else {
					console.log('childTag === undefined', childTag);
					childTag = wffTagUtil.createTagFromWffBMBytes(values[3]);
				}
				currentParentTag.appendChild(childTag);

			}

		}

		if (taskValue == wffGlobal.taskValues.ADDED_ATTRIBUTES) {
			console.log('taskValue == "ADDED_ATTRIBUTES"');

			for (var i = 1; i < nameValues.length; i++) {

				var nameValue = nameValues[i];
				if (nameValue.name[0] == wffGlobal.taskValues.MANY_TO_ONE) {
					var tagName = getStringFromBytes(nameValue.values[0]);
					var wffId = wffTagUtil
							.getWffIdFromWffIdBytes(nameValue.values[1]);

					var applicableTag = wffTagUtil.getTagByTagNameAndWffId(
							tagName, wffId);

					for (var j = 2; j < nameValue.values.length; j++) {

						var attrNameValue = getStringFromBytes(nameValue.values[j]);

						var attrNameValueArry = wffTagUtil
								.splitAttrNameValue(attrNameValue);
						var attrName = attrNameValueArry[0];
						var attrValue = attrNameValueArry[1];
						applicableTag.setAttribute(attrName, attrValue);
					}
				}

			}

		} else if (taskValue == wffGlobal.taskValues.REMOVED_ATTRIBUTES) {
			console.log('taskValue == "REMOVED_ATTRIBUTES"');

			for (var i = 1; i < nameValues.length; i++) {

				var nameValue = nameValues[i];
				if (nameValue.name[0] == wffGlobal.taskValues.MANY_TO_ONE) {
					var tagName = getStringFromBytes(nameValue.values[0]);
					var wffId = wffTagUtil
							.getWffIdFromWffIdBytes(nameValue.values[1]);

					var applicableTag = wffTagUtil.getTagByTagNameAndWffId(
							tagName, wffId);

					for (var j = 2; j < nameValue.values.length; j++) {

						var attrName = getStringFromBytes(nameValue.values[j]);

						applicableTag.removeAttribute(attrName);
					}
				}

			}

		} else if (taskValue == wffGlobal.taskValues.ADDED_INNER_HTML) {

			console.log('wffGlobal.taskValues.ADDED_INNER_HTML');

			for (var i = 1; i < nameValues.length; i++) {
				var wffId = wffTagUtil
						.getWffIdFromWffIdBytes(nameValues[i].name);
				var values = nameValues[i].values;
				var tagName = getStringFromBytes(values[0]);
				// var innerHtml = getStringFromBytes(values[1]);

				// console.log('innerHtml', innerHtml);

				var parentTag = wffTagUtil.getTagByTagNameAndWffId(tagName,
						wffId);
				// it should be case sensitive node.innerHTML
				// parentTag.innerHTML = innerHtml;

				while (parentTag.firstChild) {
					parentTag.removeChild(parentTag.firstChild);
				}

				var htmlNodes = wffTagUtil.createTagFromWffBMBytes(values[1]);
				parentTag.appendChild(htmlNodes);
			}

		} else if (taskValue == wffGlobal.taskValues.RELOAD_BROWSER) {
			location.reload(true);
		} else if (taskValue == wffGlobal.taskValues.RELOAD_BROWSER_FROM_CACHE) {
			location.reload();
		} else {
			return false;
		}
		
		return true;

		// else if (taskValue == 'DA') {
		//
		// for (var i = 1; i < nameValues.length; i++) {
		//
		// var tagId = wffBMUtil
		// .getIntFromOptimizedBytes(nameValues[i].name);
		// var attrNames = nameValues[i].values;
		//
		// var tagName = getStringFromBytes(attrNames[0]);
		//
		// for (var j = 1; j < attrNames.length; j++) {
		//
		// var attrName = getStringFromBytes(attrNames[j]);
		//
		// var applicableTag = wffTagUtil.getTagByTagNameAndWffId(
		// tagName, tagId);
		//
		// applicableTag.removeAttribute(attrName);
		// console.log('attr removed');
		// }
		//
		// }
		// }

	};

	this.getAttributeUpdates = function(wffBMBytes) {
		var nameValue = wffBMUtil.parseWffBinaryMessageBytes(wffBMBytes)[1];
	};

};