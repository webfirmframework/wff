var wffClientCRUDUtil = new function() {

	var encoder = wffGlobal.encoder;
	var decoder = wffGlobal.decoder;
	
	var uriChangeQ = [];

	var getStringFromBytes = function(utf8Bytes) {
		return decoder.decode(new Uint8Array(utf8Bytes));
	};

	var invokeTask = function(nameValues) {
		
		var taskNameValue = nameValues[0];
		// var taskName = getStringFromBytes(taskNameValue.name);
		var taskValue = taskNameValue.values[0][0];
		console.log('taskNameValue.name', taskNameValue.name);
		
		if (taskValue == wffGlobal.taskValues.ATTRIBUTE_UPDATED) {
			console.log('taskValue == "ATTRIBUTE_UPDATED"');

			for (var i = 1; i < nameValues.length; i++) {

				console.log('i', i);
				console.log('nameValues[i].values', nameValues[i].values);

				var attrNameValue = wffTagUtil.getAttrNameValueFromCompressedBytes(nameValues[i].name);
				var attrName = attrNameValue[0];
				var attrValue = attrNameValue[1];

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

//					if (indexOfSeparator != -1) {
//						//value attribute doesn't work with setAttribute method
//						//should be called before setAttribute method
//						applicableTag[attrName] = attrValue;
//						applicableTag.setAttribute(attrName, attrValue);
//					} else {
//						//value attribute doesn't work with setAttribute method
//						//should be called before setAttribute method
//						applicableTag[attrName] = "";
//						applicableTag.setAttribute(attrName, "");
//					}
					
					applicableTag[attrName] = attrValue;
					applicableTag.setAttribute(attrName, attrValue);
				}

			}

		} 
		
//		else if (taskValue == wffGlobal.taskValues.REMOVED_ATTRIBUTE) {
//			console.log('taskValue == "REMOVED_ATTRIBUTE"');
//
//			for (var i = 1; i < nameValues.length; i++) {
//
//				console.log('i', i);
//				console.log('nameValues[i].values', nameValues[i].values);
//
//				var attrName = getStringFromBytes(nameValues[i].name);
//
//				console.log('attrName', attrName);
//
//				// var tagId = wffBMUtil
//				// .getIntFromOptimizedBytes(nameValues[i].name);
//				var wffIds = nameValues[i].values;
//
//				// var tagName = getStringFromBytes(wffIds[0]);
//
//				for (var j = 0; j < wffIds.length; j++) {
//					console.log('j', j);
//
//					var wffId = wffTagUtil.getWffIdFromWffIdBytes(wffIds[j]);
//
//					var applicableTag = wffTagUtil.getTagByWffId(wffId);
//
//					applicableTag.removeAttribute(attrName);
//
//				}
//
//			}
//
//		} 
		
		else if (taskValue == wffGlobal.taskValues.APPENDED_CHILD_TAG
				|| taskValue == wffGlobal.taskValues.APPENDED_CHILDREN_TAGS) {

			for (var i = 1; i < nameValues.length; i++) {
				var wffId = wffTagUtil
						.getWffIdFromWffIdBytes(nameValues[i].name);
				var values = nameValues[i].values;
				var tagName = wffTagUtil.getTagNameFromCompressedBytes(values[0]);

				var parent = wffTagUtil.getTagByTagNameAndWffId(tagName, wffId);

				for (var j = 1; j < values.length; j++) {
					// var htmlString = getStringFromBytes(values[j]);

					// var div = document.createElement('div');
					// div.innerHTML = htmlString;
					// var htmlNodes = div.firstChild;

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
				var tagName = wffTagUtil.getTagNameFromCompressedBytes(values[0]);

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
				var tagName = wffTagUtil.getTagNameFromCompressedBytes(values[0]);

				var parentTag = wffTagUtil.getTagByTagNameAndWffId(tagName,
						wffId);
				while (parentTag.firstChild) {
					parentTag.removeChild(parentTag.firstChild);
				}
			}

		} else if (taskValue == wffGlobal.taskValues.MOVED_CHILDREN_TAGS) {

			console.log('wffGlobal.taskValues.MOVED_CHILDREN_TAGS nameValues.length '
							+ nameValues.length);

			for (var i = 1; i < nameValues.length; i++) {
				
				var currentParentWffId = wffTagUtil
						.getWffIdFromWffIdBytes(nameValues[i].name);
				var values = nameValues[i].values;				
				
				var currentParentTagName = wffTagUtil.getTagNameFromCompressedBytes(values[0]);

				var currentParentTag = wffTagUtil.getTagByTagNameAndWffId(
						currentParentTagName, currentParentWffId);
				
				var childTag = null;
				//if NoTag then length will be zero
				if (values[2].length == 0) {
					childTag = wffTagUtil.createTagFromWffBMBytes(values[3]);
				} else {
					var childTagName = wffTagUtil.getTagNameFromCompressedBytes(values[2]);
					var childWffId = wffTagUtil.getWffIdFromWffIdBytes(values[1]);
					
					childTag = wffTagUtil.getTagByTagNameAndWffId(childTagName,
							childWffId);
					if (typeof childTag !== 'undefined') {
						console.log('childTag !== undefined', childTag);
						var previousParent = childTag.parentNode;
						console.log('childTag.parentNode', previousParent);
						if (typeof previousParent !== 'undefined') {						
							previousParent.removeChild(childTag);
						}
					} else {
						console.log('childTag === undefined', childTag);
						childTag = wffTagUtil.createTagFromWffBMBytes(values[3]);
					}
				}				
				currentParentTag.appendChild(childTag);
			}

		} else if (taskValue == wffGlobal.taskValues.ADDED_ATTRIBUTES) {
			console.log('taskValue == "ADDED_ATTRIBUTES"');

			for (var i = 1; i < nameValues.length; i++) {

				var nameValue = nameValues[i];
				if (nameValue.name[0] == wffGlobal.taskValues.MANY_TO_ONE) {
					var tagName = wffTagUtil.getTagNameFromCompressedBytes(nameValue.values[0]);
					var wffId = wffTagUtil
							.getWffIdFromWffIdBytes(nameValue.values[1]);

					var applicableTag = wffTagUtil.getTagByTagNameAndWffId(
							tagName, wffId);

					for (var j = 2; j < nameValue.values.length; j++) {

						var attrNameValue = wffTagUtil.getAttrNameValueFromCompressedBytes(nameValue.values[j]);
						
						var attrName = attrNameValue[0];
						var attrValue = attrNameValue[1];
						//value attribute doesn't work with setAttribute method
						//should be called before setAttribute method
						applicableTag[attrName] = attrValue;
						applicableTag.setAttribute(attrName, attrValue);
					}
				}

			}

		} else if (taskValue == wffGlobal.taskValues.REMOVED_ATTRIBUTES) {
			console.log('taskValue == "REMOVED_ATTRIBUTES"');

			for (var i = 1; i < nameValues.length; i++) {

				var nameValue = nameValues[i];
				if (nameValue.name[0] == wffGlobal.taskValues.MANY_TO_ONE) {
					var tagName = wffTagUtil.getTagNameFromCompressedBytes(nameValue.values[0]);
					var wffId = wffTagUtil
							.getWffIdFromWffIdBytes(nameValue.values[1]);

					var applicableTag = wffTagUtil.getTagByTagNameAndWffId(
							tagName, wffId);

					for (var j = 2; j < nameValue.values.length; j++) {

						var attrName = wffTagUtil.getAttrNameFromCompressedBytes(nameValue.values[j]);						
						
						applicableTag.removeAttribute(attrName);
						
						if (wffGlobal.NDXD_BLN_ATRBS.indexOf(attrName) != -1) {
							var prop = applicableTag[attrName];
							//=== is very important here
							if (prop && prop === true) {
								applicableTag[attrName] = false;	
							}
						}
					}
				}

			}

		} else if (taskValue == wffGlobal.taskValues.ADDED_INNER_HTML) {

			console.log('wffGlobal.taskValues.ADDED_INNER_HTML');
			
			var tagName = wffTagUtil.getTagNameFromCompressedBytes(nameValues[1].name);
			
			var wffId = wffTagUtil
					.getWffIdFromWffIdBytes(nameValues[1].values[0]);
			
			var parentTag = wffTagUtil.getTagByTagNameAndWffId(tagName,
					wffId);
			
			// it should be case sensitive node.innerHTML
			// parentTag.innerHTML = innerHtml;
			// inner html will not work with table

			while (parentTag.firstChild) {
				parentTag.removeChild(parentTag.firstChild);
			}

			for (var i = 2; i < nameValues.length; i++) {
				
				var values = nameValues[i].values;

				var htmlNodes = wffTagUtil.createTagFromWffBMBytes(nameValues[i].name);
				
				//if length is 1 then there is an existing tag with this id
				if (values.length == 1 && values[0].length == 1) {
					console.log('values.length == 3');
					var existingTag = wffTagUtil.getTagByTagNameAndWffId(
							htmlNodes.nodeName, htmlNodes
									.getAttribute("data-wff-id"));
					if (existingTag) {
						var parentOfExistingTag = existingTag.parentNode;
						parentOfExistingTag.removeChild(existingTag);
					}
					
				}
				
				parentTag.appendChild(htmlNodes);
			}

		} else if (taskValue == wffGlobal.taskValues.INSERTED_BEFORE_TAG) {
			
			console.log('wffGlobal.taskValues.INSERTED_BEFORE_TAG');
			
			var tagName = wffTagUtil.getTagNameFromCompressedBytes(nameValues[1].name);
			var wffId = wffTagUtil.getWffIdFromWffIdBytes(nameValues[1].values[0]);
			var parentTag = wffTagUtil.getTagByTagNameAndWffId(tagName, wffId);
			
			//beforeTag means replacingTag 
			var beforeTagName = wffTagUtil.getTagNameFromCompressedBytes(nameValues[2].name);
			var beforeTag;
			
			//# means NoTag
			if (beforeTagName === '#') {
				var chldNdxOptmzdIntByts = nameValues[2].values[0];
				beforeTag = wffTagUtil.getChildByNthIndexBytes(parentTag, chldNdxOptmzdIntByts);				
			} else {
				var beforeTagWffId = wffTagUtil.getWffIdFromWffIdBytes(nameValues[2].values[0]);					
				beforeTag = wffTagUtil.getTagByTagNameAndWffId(beforeTagName,
						beforeTagWffId);
			}

			for (var i = 3; i < nameValues.length; i++) {
				
				var nm = nameValues[i].name;
				var values = nameValues[i].values;			
				
				var htmlNodes = wffTagUtil.createTagFromWffBMBytes(values[0]);
				
				//if length is 1 then there is an existing tag with this id
				if (nm.length == 1) {
					console.log('nm.length == 1');
					var existingTag = wffTagUtil.getTagByTagNameAndWffId(
							htmlNodes.nodeName, htmlNodes
									.getAttribute("data-wff-id"));
					var parentOfExistingTag = existingTag.parentNode;
					parentOfExistingTag.removeChild(existingTag);
					
				}
				parentTag.insertBefore(htmlNodes, beforeTag);
			}
			
			
		} else if (taskValue == wffGlobal.taskValues.INSERTED_AFTER_TAG) {
			
			console.log('wffGlobal.taskValues.INSERTED_AFTER_TAG');
			
			var tagName = wffTagUtil.getTagNameFromCompressedBytes(nameValues[1].name);
			var wffId = wffTagUtil.getWffIdFromWffIdBytes(nameValues[1].values[0]);
			var parentTag = wffTagUtil.getTagByTagNameAndWffId(tagName, wffId);
			
			//beforeTag means replacingTag 
			var beforeTagName = wffTagUtil.getTagNameFromCompressedBytes(nameValues[2].name);
			var beforeTag;
			
			//# means NoTag
			if (beforeTagName === '#') {
				var chldNdxOptmzdIntByts = nameValues[2].values[0];
				beforeTag = wffTagUtil.getChildByNthIndexBytes(parentTag, chldNdxOptmzdIntByts);				
			} else {
				var beforeTagWffId = wffTagUtil.getWffIdFromWffIdBytes(nameValues[2].values[0]);					
				beforeTag = wffTagUtil.getTagByTagNameAndWffId(beforeTagName,
						beforeTagWffId);
			}

			var firstNd;
			for (var i = 3; i < nameValues.length; i++) {
				
				var nm = nameValues[i].name;
				var values = nameValues[i].values;			
				
				var htmlNodes = wffTagUtil.createTagFromWffBMBytes(values[0]);
				
				//if length is 1 then there is an existing tag with this id
				if (nm.length == 1) {
					console.log('nm.length == 1');
					var existingTag = wffTagUtil.getTagByTagNameAndWffId(
							htmlNodes.nodeName, htmlNodes
									.getAttribute("data-wff-id"));
					var parentOfExistingTag = existingTag.parentNode;
					parentOfExistingTag.removeChild(existingTag);
					
				}
				
				parentTag.insertBefore(htmlNodes, beforeTag);
				if (!firstNd) {
					firstNd = htmlNodes;
				}
			}
			if (firstNd) {
				parentTag.removeChild(beforeTag);
				parentTag.insertBefore(beforeTag, firstNd);					
			}
		} else if (taskValue == wffGlobal.taskValues.REPLACED_WITH_TAGS) {

			console.log('wffGlobal.taskValues.REPLACED_WITH_TAGS');
			
			var tagName = wffTagUtil.getTagNameFromCompressedBytes(nameValues[1].name);
			var wffId = wffTagUtil.getWffIdFromWffIdBytes(nameValues[1].values[0]);
			var parentTag = wffTagUtil.getTagByTagNameAndWffId(tagName, wffId);
			
			//beforeTag means replacingTag 
			var beforeTagName = wffTagUtil.getTagNameFromCompressedBytes(nameValues[2].name);
			var beforeTag;
			
			//# means NoTag
			if (beforeTagName === '#') {
				var chldNdxOptmzdIntByts = nameValues[2].values[0];
				beforeTag = wffTagUtil.getChildByNthIndexBytes(parentTag, chldNdxOptmzdIntByts);				
			} else {
				var beforeTagWffId = wffTagUtil.getWffIdFromWffIdBytes(nameValues[2].values[0]);					
				beforeTag = wffTagUtil.getTagByTagNameAndWffId(beforeTagName,
						beforeTagWffId);
			}

			for (var i = 3; i < nameValues.length; i++) {
				
				var nm = nameValues[i].name;
				var values = nameValues[i].values;			
				
				var htmlNodes = wffTagUtil.createTagFromWffBMBytes(values[0]);
				
				//if length is 1 then there is an existing tag with this id
				if (nm.length == 1) {
					console.log('nm.length == 1');
					var existingTag = wffTagUtil.getTagByTagNameAndWffId(
							htmlNodes.nodeName, htmlNodes
									.getAttribute("data-wff-id"));
					var parentOfExistingTag = existingTag.parentNode;
					parentOfExistingTag.removeChild(existingTag);
					
				}
				parentTag.insertBefore(htmlNodes, beforeTag);
			}
			parentTag.removeChild(beforeTag); 

		} else if (taskValue == wffGlobal.taskValues.RELOAD_BROWSER) {
			location.reload(true);
		} else if (taskValue == wffGlobal.taskValues.RELOAD_BROWSER_FROM_CACHE) {
			location.reload();
		} else if (taskValue == wffGlobal.taskValues.EXEC_JS) {
			var js = getStringFromBytes(taskNameValue.values[1]);
			//only on other pages
			var op = taskNameValue.values[2][0];
			if (op == 1) {
				localStorage.setItem('WFF_EXEC_JS', JSON.stringify({ js: js, instanceId: wffGlobal.INSTANCE_ID }));
				localStorage.removeItem('WFF_EXEC_JS');
			} else {
				if (window.execScript) {
					window.execScript(js);
				} else {
					eval(js);
				}
			}
		} else if (taskValue == wffGlobal.taskValues.SET_URI) {
			var jsObj = new JsObjectFromBMBytes(taskNameValue.values[1], true);
			jsObj.uriAfter = jsObj.ua;
			jsObj.uriBefore = jsObj.ub;
			jsObj.origin = jsObj.o;
			delete jsObj.ua;
			delete jsObj.ub;
			delete jsObj.o;
			if (jsObj.uriAfter && jsObj.uriAfter !== jsObj.uriBefore) {
				if (jsObj.origin === 'S') {
					jsObj.origin = 'server';
					jsObj.initiator = 'serverCode';
				} 
				var vnt = {};
				for (k in jsObj) {
					vnt[k] = jsObj[k];
				}
				history.pushState({}, document.title, jsObj.uriAfter);
				uriChangeQ.push(vnt);
				if (typeof wffGlobalListeners !== "undefined" && wffGlobalListeners.onSetURI && jsObj.origin === 'server') {
					try {
						wffGlobalListeners.onSetURI(vnt);
					} catch (e) {
						wffLog("wffGlobalListeners.onSetURI threw exception when browserPage.setURI is called", e);
					}
				}
			}
		} else if (taskValue == wffGlobal.taskValues.AFTER_SET_URI) {
			if (typeof wffGlobalListeners !== "undefined" && wffGlobalListeners.afterSetURI) {
				for (var i = 0; i < uriChangeQ.length; i++) {
					var vnt = uriChangeQ[i];					
					try {
						wffGlobalListeners.afterSetURI(vnt);
					} catch (e) {
						wffLog("wffGlobalListeners.afterSetURI threw exception when the setURI method in the server is called.", e);
					}
				}
			}
			uriChangeQ = [];
		} else if (taskValue == wffGlobal.taskValues.SET_LS_ITEM) {
			var jsObj = new JsObjectFromBMBytes(taskNameValue.values[1], true);
			// k for key, v for value, wt for write time, cb callback
			if (typeof localStorage !== "undefined" && jsObj.k && jsObj.wt && jsObj.id) {
				var wt = parseInt(jsObj.wt);
				var prev = localStorage.getItem(jsObj.k + '_wff_data');
				var lstWT = 0;
				var lstId = 0;
				if (prev) {
					try {
						var itemObj = JSON.parse(prev);
						lstWT = parseInt(itemObj.wt);
						lstId = itemObj.id;
					} catch (e) {
						wffLog(e);
					}
				}
				if (wt >= lstWT) {
					var id = wffBMUtil.getIntFromOptimizedBytes(jsObj.id);
					if (wt > lstWT || (wt == lstWT && id > lstId)) {
						var itemVal = JSON.stringify({ v: jsObj.v, wt: jsObj.wt, id: id });
						localStorage.setItem(jsObj.k + '_wff_data', itemVal);
					}
				}

				if (jsObj.cb) {
					var taskNameValue = wffTaskUtil.getTaskNameValue(
						wffGlobal.taskValues.TASK,
						taskValue);
					var nameValue = {
						'name': jsObj.id,
						'values': []
					};
					var nameValues = [taskNameValue, nameValue];
					var wffBM = wffBMUtil.getWffBinaryMessageBytes(nameValues);
					wffWS.send(wffBM);
				}

			}
		} else if (taskValue == wffGlobal.taskValues.GET_LS_ITEM) {
			var jsObj = new JsObjectFromBMBytes(taskNameValue.values[1], true);
			// k for key, v for value, wt for write time
			if (typeof localStorage !== "undefined" && jsObj.id && jsObj.k) {
				//string
				var itemJSON = localStorage.getItem(jsObj.k + '_wff_data');
				var itemObj;
				if (itemJSON) {
					try {
						itemObj = JSON.parse(itemJSON);
					} catch (e) {
						wffLog(e);
					}
				}
				if (!itemObj || !itemObj.wt) {
					itemObj = {};
				}
				var taskNameValue = wffTaskUtil.getTaskNameValue(
					wffGlobal.taskValues.TASK,
					taskValue);
				var nameValue = {
					'name': jsObj.id,
					'values': []
				};
				if (itemObj.wt) {
					nameValue.values = [encoder.encode(itemObj.v), encoder.encode(itemObj.wt)];
				}
				var nameValues = [taskNameValue, nameValue];
				var wffBM = wffBMUtil.getWffBinaryMessageBytes(nameValues);
				wffWS.send(wffBM);
			}
		} else if (taskValue == wffGlobal.taskValues.REMOVE_LS_ITEM || taskValue == wffGlobal.taskValues.REMOVE_AND_GET_LS_ITEM) {
			var jsObj = new JsObjectFromBMBytes(taskNameValue.values[1], true);
			// k for key, v for value, wt for write time, cb for callback
			if (typeof localStorage !== "undefined" && jsObj.k && jsObj.wt && jsObj.id) {
				//string
				var itemJSON = localStorage.getItem(jsObj.k + '_wff_data');
				var itemObj;
				if (itemJSON) {
					try {
						itemObj = JSON.parse(itemJSON);
					} catch (e) {
						wffLog(e);
					}
				}
				if (!itemObj || !itemObj.wt || !itemObj.id) {
					itemObj = {};
				} else {
					var jsObjWT = parseInt(jsObj.wt);
					var itemObjWT = parseInt(itemObj.wt);
					if (jsObjWT >= itemObjWT) {
						var id = wffBMUtil.getIntFromOptimizedBytes(jsObj.id);
						if (jsObjWT > itemObjWT || (jsObjWT == itemObjWT && id > itemObj.id)) {
							localStorage.removeItem(jsObj.k + '_wff_data');
						}
					}
				}
				if (jsObj.cb) {
					var taskNameValue = wffTaskUtil.getTaskNameValue(
						wffGlobal.taskValues.TASK,
						taskValue);
					var nameValue = {
						'name': jsObj.id,
						'values': []
					};
					if (taskValue == wffGlobal.taskValues.REMOVE_AND_GET_LS_ITEM && itemObj.wt) {
						nameValue.values = [encoder.encode(itemObj.v), encoder.encode(itemObj.wt)];
					}
					var nameValues = [taskNameValue, nameValue];
					var wffBM = wffBMUtil.getWffBinaryMessageBytes(nameValues);
					wffWS.send(wffBM);
				}

			}
		} else if (taskValue == wffGlobal.taskValues.CLEAR_LS) {
			var jsObj = new JsObjectFromBMBytes(taskNameValue.values[1], true);
			// wt for write time, D for data, T for token, tp for type, cb for callback
			if (typeof localStorage !== "undefined" && jsObj.wt && jsObj.tp && jsObj.id) {
				for (var k in localStorage) {
					if ((k.endsWith('_wff_data') && (jsObj.tp === 'D' || jsObj.tp === 'DT'))
						|| (k.endsWith('_wff_token') && (jsObj.tp === 'T' || jsObj.tp === 'DT'))) {
						try {
							var itemObj = JSON.parse(localStorage.getItem(k));
							if (itemObj && itemObj.wt && itemObj.id) {
								var jsObjWT = parseInt(jsObj.wt);
								var itemObjWT = parseInt(itemObj.wt);
								if (jsObjWT >= itemObjWT) {
									var id = wffBMUtil.getIntFromOptimizedBytes(jsObj.id);
									if (jsObjWT > itemObjWT || (jsObjWT == itemObjWT && id > itemObj.id)) {
										if (k.endsWith('_wff_token')) {
											//this is to handle case in storage event, id is required for deleting from multiple nodes,  
											itemObj.removed = true;
											itemObj.id = id;
											itemObj.wt = jsObj.wt;
											itemObj.nid = wffGlobal.NODE_ID;
											localStorage.setItem(k, JSON.stringify(itemObj));
										}
										localStorage.removeItem(k);
									}
								}
							}
						} catch (e) {
							wffLog(e);
						}
					}
				}
				if (jsObj.cb) {
					var taskNameValue = wffTaskUtil.getTaskNameValue(
						wffGlobal.taskValues.TASK,
						taskValue);
					var nameValue = {
						'name': jsObj.id,
						'values': []
					};
					var nameValues = [taskNameValue, nameValue];
					var wffBM = wffBMUtil.getWffBinaryMessageBytes(nameValues);
					wffWS.send(wffBM);
				}
			}
		} else if (taskValue == wffGlobal.taskValues.SET_LS_TOKEN) {
			var jsObj = new JsObjectFromBMBytes(taskNameValue.values[1], true);
			// k for key, v for value, wt for write time, id for write id
			if (typeof localStorage !== "undefined" && jsObj.k && jsObj.wt && jsObj.id) {
				var tknNm = jsObj.k + '_wff_token';
				var wt = parseInt(jsObj.wt);
				var prev = localStorage.getItem(tknNm);
				var lstWT = 0;
				var lstId = 0;
				if (prev) {
					try {
						var itemObj = JSON.parse(prev);
						lstWT = parseInt(itemObj.wt);
						lstId = itemObj.id;
					} catch (e) {
						wffLog(e);
					}
				}
				if (wt >= lstWT) {
					var id = wffBMUtil.getIntFromOptimizedBytes(jsObj.id);
					if (wt > lstWT || (wt == lstWT && id > lstId)) {
						var itemVal = JSON.stringify({ v: jsObj.v, wt: jsObj.wt, nid: wffGlobal.NODE_ID, id: id });
						localStorage.setItem(tknNm, itemVal);
					}
				}
			}
		} else if (taskValue == wffGlobal.taskValues.REMOVE_LS_TOKEN) {
			var jsObj = new JsObjectFromBMBytes(taskNameValue.values[1], true);
			// k for key, v for value, wt for write time
			if (typeof localStorage !== "undefined" && jsObj.k && jsObj.wt && jsObj.id) {
				var tknNm = jsObj.k + '_wff_token';
				//string
				var itemJSON = localStorage.getItem(tknNm);
				var itemObj;
				if (itemJSON) {
					try {
						itemObj = JSON.parse(itemJSON);
					} catch (e) {
						wffLog(e);
					}
				}
				if (itemObj && itemObj.wt && itemObj.id) {
					var jsObjWT = parseInt(jsObj.wt);
					var itemObjWT = parseInt(itemObj.wt);
					if (jsObjWT >= itemObjWT) {
						var id = wffBMUtil.getIntFromOptimizedBytes(jsObj.id);
						if (jsObjWT >= itemObjWT || (jsObjWT == itemObjWT && id > itemObj.id)) {
							//this is to handle case in storage event, id is required for deleting from multiple nodes,  
							itemObj.removed = true;
							itemObj.id = id;
							itemObj.wt = jsObj.wt;
							itemObj.nid = wffGlobal.NODE_ID;
							localStorage.setItem(tknNm, JSON.stringify(itemObj));
							localStorage.removeItem(tknNm);
						}

					}

				}
			}
		} else if (taskValue == wffGlobal.taskValues.COPY_INNER_TEXT_TO_VALUE) {

			console.log('wffGlobal.taskValues.COPY_INNER_TEXT_TO_VALUE');

			var tagName = wffTagUtil.getTagNameFromCompressedBytes(nameValues[1].name);

			var wffId = wffTagUtil
				.getWffIdFromWffIdBytes(nameValues[1].values[0]);

			var parentTag = wffTagUtil.getTagByTagNameAndWffId(tagName,
				wffId);

			var d = document.createElement('div');
			d.innerHTML = parentTag.outerHTML;
			parentTag.value = d.childNodes[0].innerText;
		} else if (taskValue == wffGlobal.taskValues.SET_BM_OBJ_ON_TAG
				|| taskValue == wffGlobal.taskValues.SET_BM_ARR_ON_TAG) {
			
			var tagName = wffTagUtil.getTagNameFromCompressedBytes(nameValues[1].name);
			
			var wffId = wffTagUtil
							.getWffIdFromWffIdBytes(nameValues[1].values[0]);
			
			var tag = wffTagUtil.getTagByTagNameAndWffId(tagName, wffId);
			
			var ky = getStringFromBytes(nameValues[1].values[1]);
			
			var bmObjOrArrBytes = nameValues[1].values[2];
			
			var jsObjOrArr;
			
			if (taskValue == wffGlobal.taskValues.SET_BM_OBJ_ON_TAG) {
				jsObjOrArr = new JsObjectFromBMBytes(bmObjOrArrBytes, true);
			} else {
				jsObjOrArr = new JsArrayFromBMBytes(bmObjOrArrBytes, true);
			}
			
			var wffObjects = tag['wffObjects'];
			
			if(typeof wffObjects === 'undefined') {
				wffObjects = {};
				tag['wffObjects'] = wffObjects;
			}
			
			wffObjects[ky] = jsObjOrArr;
			
		} else if (taskValue == wffGlobal.taskValues.DEL_BM_OBJ_OR_ARR_FROM_TAG) {
			var tagName = wffTagUtil.getTagNameFromCompressedBytes(nameValues[1].name);
			var wffId = wffTagUtil
					.getWffIdFromWffIdBytes(nameValues[1].values[0]);
			
			var tag = wffTagUtil.getTagByTagNameAndWffId(tagName, wffId);
			
			var ky = getStringFromBytes(nameValues[1].values[1]);
			
			var wffObjects = tag['wffObjects'];
			
			if(typeof wffObjects !== 'undefined') {
				delete wffObjects[ky];
			}
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
	
	this.invokeTasks = function(wffBMBytes) {
		
		var nameValues = wffBMUtil.parseWffBinaryMessageBytes(wffBMBytes);
		var taskNameValue = nameValues[0];
		
		
		if (taskNameValue.name[0] == wffGlobal.taskValues.TASK) {
			
			console.log('TASK');
			
			invokeTask(nameValues);
			
		} else if (taskNameValue.name[0] == wffGlobal.taskValues.TASK_OF_TASKS) {
			
			console.log('TASK_OF_TASKS');
			
			var tasksBM = taskNameValue.values;
			
			for (var i = 0; i < tasksBM.length; i++) {
				var taskNameValues = wffBMUtil.parseWffBinaryMessageBytes(tasksBM[i]);
				invokeTask(taskNameValues);
			}
			
		} else {
			return false;
		}
		return true;
	};

	this.getAttributeUpdates = function(wffBMBytes) {
		var nameValue = wffBMUtil.parseWffBinaryMessageBytes(wffBMBytes)[1];
	};

};
wffGlobal.frz(wffClientCRUDUtil, false);