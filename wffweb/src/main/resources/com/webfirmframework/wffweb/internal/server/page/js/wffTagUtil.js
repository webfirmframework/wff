
var wffTagUtil = new function() {

	var encoder = wffGlobal.encoder;
	var decoder = wffGlobal.decoder;

	var getStringFromBytes = function(utf8Bytes) {
		return decoder.decode(new Uint8Array(utf8Bytes));
	};
	
	var subarray = wffBMUtil.subarray;
	
	var getTagNameFromCompressedBytes = function(utf8Bytes) {
		
//		if length is 1 then it contain only optimized byte of index
		if (utf8Bytes.length == 1) {
			var tgNamNdx = wffBMUtil.getIntFromOptimizedBytes(utf8Bytes);
			//it includes # and @ represented for NoTag
			return wffGlobal.NDXD_TGS[tgNamNdx];
		}
		
		var lengOfOptmzdBytsOfTgNam = utf8Bytes[0];
		
		if (lengOfOptmzdBytsOfTgNam > 0) {
			
			var tgNamNdxOptmzdByts = subarray(utf8Bytes, 1, lengOfOptmzdBytsOfTgNam);
			
			var tgNamNdx = wffBMUtil.getIntFromOptimizedBytes(tgNamNdxOptmzdByts);
			return wffGlobal.NDXD_TGS[tgNamNdx];
		} else {
			
			var reqBytsLngth = utf8Bytes.length - 1;
			var tagNameBytes = subarray(utf8Bytes, 1, reqBytsLngth);
			
			return getStringFromBytes(tagNameBytes);
		}	
		
	};
	
	this.getTagNameFromCompressedBytes = getTagNameFromCompressedBytes;
	
	var getAttrNameFromCompressedBytes = function(utf8Bytes) {
		
//		if length is 1 then it contain only optimized byte of index
		if (utf8Bytes.length == 1) {
			var attrNamNdx = wffBMUtil.getIntFromOptimizedBytes(utf8Bytes);
			//it includes # and @ represented for NoTag
			return wffGlobal.NDXD_ATRBS[attrNamNdx];
		}
		
		var lengOfOptmzdBytsOfAttrNam = utf8Bytes[0];
		
		if (lengOfOptmzdBytsOfAttrNam > 0) {
			
			var attrNamNdxOptmzdByts = subarray(utf8Bytes, 1, lengOfOptmzdBytsOfAttrNam);
			
			var attrNamNdx = wffBMUtil.getIntFromOptimizedBytes(attrNamNdxOptmzdByts);
			return wffGlobal.NDXD_ATRBS[attrNamNdx];
		} else {
			
			var reqBytsLngth = utf8Bytes.length - 1;
			var attrNameBytes = subarray(utf8Bytes, 1, reqBytsLngth);
			
			return getStringFromBytes(attrNameBytes);
		}	
		
	};
	
	this.getAttrNameFromCompressedBytes = getAttrNameFromCompressedBytes;
	
	
	var getAttrNameValueFromCompressedBytes = function(utf8Bytes) {
		var lengOfOptmzdBytsOfAttrNam = utf8Bytes[0];
		
		if(lengOfOptmzdBytsOfAttrNam > 0 || (lengOfOptmzdBytsOfAttrNam < 0 && lengOfOptmzdBytsOfAttrNam > -5)) {
		    var ng = lengOfOptmzdBytsOfAttrNam < 0;
		    var lengOfOptmzdBytsOfAttrNam = ng ? Math.abs(lengOfOptmzdBytsOfAttrNam) : lengOfOptmzdBytsOfAttrNam;
			var attrNamNdxOptmzdByts = subarray(utf8Bytes, 1, lengOfOptmzdBytsOfAttrNam);
			
			var attrNamNdx = wffBMUtil.getIntFromOptimizedBytes(attrNamNdxOptmzdByts);
			
			var attrValLen = utf8Bytes.length - (lengOfOptmzdBytsOfAttrNam + 1);
			
			var attrValByts = subarray(utf8Bytes, lengOfOptmzdBytsOfAttrNam + 1, attrValLen);

			var attrValue = ng ? wffBMUtil.getIntFromOptimizedBytes(attrValByts).toString() : getStringFromBytes(attrValByts);

			// [attributeName, attributeValue]
			return [wffGlobal.NDXD_ATRBS[attrNamNdx], attrValue];
		} else if (lengOfOptmzdBytsOfAttrNam == 0) {
			var reqBytsLngth = utf8Bytes.length - 1;
			var attrNamByts = subarray(utf8Bytes, 1, reqBytsLngth);
			return splitAttrNameValue(getStringFromBytes(attrNamByts));
		} else {
		   lengOfOptmzdBytsOfAttrNam = Math.abs(lengOfOptmzdBytsOfAttrNam);
		   // -5 or -6 it is data-wff-id
		   // -5 for prefix S, -6 for prefix C, getting it dynamically
		   var ndx = lengOfOptmzdBytsOfAttrNam - 5;
		   if (ndx >= wffGlobal.WFF_ID_PFXS.length) {
		       wffLog("Error: prefix not indexed for", ndx);
               return null;
		   }
		   // pfx stands for prefix
		   var pfx = wffGlobal.WFF_ID_PFXS[ndx];
           // [attributeName, attributeValue]
           return ["data-wff-id", pfx + wffBMUtil.getIntFromOptimizedBytes(subarray(utf8Bytes, 1, utf8Bytes.length)).toString()];
		}
	};
	
	this.getAttrNameValueFromCompressedBytes = getAttrNameValueFromCompressedBytes;
	
	var appendHtmlAsChildren = function(tag, htmlString) {
		var tmpDv = document.createElement('div');
		tmpDv.innerHTML = htmlString;

		for (var i = 0; i < tmpDv.childNodes.length; i++) {
			var cn = tmpDv.childNodes[i];
			
			if (cn.nodeName === '#text') {
				tag.appendChild(document.createTextNode(cn.nodeValue));
			} else {
				// must clone otherwise will not be appended
				tag.appendChild(cn.cloneNode(true));
			}
		}
	};

	//Not required for now
//	this.createTagFromBytes = function(bytes) {
//		var htmlString = getStringFromBytes(bytes);
//		var div = document.createElement('div');
//		div.innerHTML = htmlString;
//		return div.firstChild;
//	};

	this.getTagByTagNameAndWffId = function(tn, wffId) {
	    // tn stands for tagName
		console.log('getTagByTagNameAndWffId tagName', tn, 'wffId', wffId);
		var elements = document.querySelectorAll(tn + '[data-wff-id="'
				+ wffId + '"]');
		if (elements.length > 1) {
			console.log('getTagByTagNameAndWffId multiple tags with same wff id');
			wffLog('multiple tags with same wff id', tn, 'wffId', wffId);
		}
		return elements[0];
	};

	this.getTagByWffId = function(wffId) {
		console.log('getTagByTagNameAndWffId ', 'wffId', wffId);
		var elements = document.querySelectorAll('[data-wff-id="' + wffId
				+ '"]');
		if (elements.length > 1) {
			wffLog('multiple tags with same wff id', 'wffId', wffId);
		}
		return elements[0];
	};

	/*
	 * this method will return string wff id from wff id bytes
	 */
	this.getWffIdFromWffIdBytes = function(wffIdBytes) {

		var sOrC = getStringFromBytes([ wffIdBytes[0] ]);
		var intBytes = [];
		for (var i = 1; i < wffIdBytes.length; i++) {
			intBytes.push(wffIdBytes[i]);
		}
		var intId = wffBMUtil.getIntFromOptimizedBytes(intBytes);
		return sOrC + intId;

	};

	this.getWffIdBytesFromTag = function(tag) {
		// the first char will be either C or S where C stands for Client and S
		// stands for Server
		console.log('tag', tag);
		var attrValue = tag.getAttribute("data-wff-id");
		console.log('attrValue', attrValue);

		if (attrValue == null) {
			attrValue = "C" + wffGlobal.getUniqueWffIntId();
			tag.setAttribute("data-wff-id", attrValue);
		}

		var sOrC = attrValue.substring(0, 1);

		var sOrCUtf8Bytes = encoder.encode(sOrC);

		var wffIdBytes = [ sOrCUtf8Bytes[0] ];

		console.log('sOrCUtf8Bytes', sOrCUtf8Bytes[0]);

		var intId = attrValue.substring(1, attrValue.length);

		var intIdBytes = wffBMUtil.getOptimizedBytesFromInt(parseInt(intId));

		wffIdBytes = wffIdBytes.concat(intIdBytes);

		console.log('wffIdBytes', wffIdBytes);
		return wffIdBytes;
	};

	var splitAttrNameValue = function(attrNameValue) {
		// attrNameValue will contain string like name=attr-name
		var attrName;
		var attrValue;
		var indexOfSeparator = attrNameValue.indexOf('=');
		if (indexOfSeparator != -1) {
			attrName = attrNameValue.substring(0, indexOfSeparator);
			attrValue = attrNameValue.substring(indexOfSeparator + 1,
					attrNameValue.length);
		} else {
			attrName = attrNameValue;
			attrValue = '';
		}
		return [ attrName, attrValue ];
	};

	this.splitAttrNameValue = splitAttrNameValue;

	// getWffIdBytesFromTag = this.getWffIdBytesFromTag;
	// var div = document.createElement('div');
	// div.setAttribute('data-wff-id', 'S255');
	// console.log('div', div);
	// var v = getWffIdBytesFromTag(div);
	// console.log('v' ,v);

	this.createTagFromWffBMBytes = function(bytes) {
		var nameValues = wffBMUtil.parseWffBinaryMessageBytes(bytes);

		var superParentNameValue = nameValues[0];
		var superParentValues = superParentNameValue.values;

		var allTags = [];

		var parent;
		var parentTagName = getStringFromBytes(superParentValues[0]);
		if (parentTagName === '#') {
			var text = getStringFromBytes(superParentValues[1]);
			parent = document.createDocumentFragment();
			parent.appendChild(document.createTextNode(text));
		} else if (parentTagName === '@') {
			// @ short for html content
			var text = getStringFromBytes(superParentValues[1]);
			parent = document.createDocumentFragment();			
			appendHtmlAsChildren(parent, text);
			
		} else {

			parent = document.createElement(parentTagName);

			for (var i = 1; i < superParentValues.length; i++) {
				var attrNameValue = splitAttrNameValue(getStringFromBytes(superParentValues[i]));
				//value attribute doesn't work with setAttribute method
				//should be called before setAttribute method
				parent[attrNameValue[0]] = attrNameValue[1];
				parent.setAttribute(attrNameValue[0], attrNameValue[1]);
			}
		}

		allTags.push(parent);

		for (var i = 1; i < nameValues.length; i++) {
			var name = nameValues[i].name;
			var values = nameValues[i].values;

            // tn stands for tagName
			var tn = getStringFromBytes(values[0]);

			var child;

			if (tn === '#') {
				var text = getStringFromBytes(values[1]);
				child = document.createDocumentFragment();
				child.appendChild(document.createTextNode(text));
			} else if (tn === '@') {
				// @ short for html content
				var text = getStringFromBytes(values[1]);
				child = document.createDocumentFragment();				
				appendHtmlAsChildren(child, text);
			} else {
				child = document.createElement(tn);

				for (var j = 1; j < values.length; j++) {
					var attrNameValue = splitAttrNameValue(getStringFromBytes(values[j]));
					//value attribute doesn't work with setAttribute method
					//should be called before setAttribute method
					child[attrNameValue[0]] = attrNameValue[1];
					child.setAttribute(attrNameValue[0], attrNameValue[1]);
				}
			}

			allTags.push(child);

			var parentIndex = wffBMUtil.getIntFromOptimizedBytes(name);
			allTags[parentIndex].appendChild(child);
		}

		return parent;
	};
	
	this.createTagFromCompressedWffBMBytes = function(bytes) {
		var nameValues = wffBMUtil.parseWffBinaryMessageBytes(bytes);

		var superParentNameValue = nameValues[0];
		var superParentValues = superParentNameValue.values;

		var allTags = [];

		var parent;
		var parentTagName = getTagNameFromCompressedBytes(superParentValues[0]);
		
		if (parentTagName === '#' || parentTagName === '$' || parentTagName === '%') {
			var txt = (parentTagName === '$' || parentTagName === '%') ? wffBMUtil.getIntFromOptimizedBytes(superParentValues[1]).toString()
			: parentTagName === '#' ? getStringFromBytes(superParentValues[1]) : '';
			parent = document.createDocumentFragment();
			parent.appendChild(document.createTextNode(txt));
		} else if (parentTagName === '@') {
			// @ short for html content
			var txt = getStringFromBytes(superParentValues[1]);
			parent = document.createDocumentFragment();			
			appendHtmlAsChildren(parent, txt);
		} else {
			parent = document.createElement(parentTagName);
			for (var i = 1; i < superParentValues.length; i++) {
				var attrNameValue = getAttrNameValueFromCompressedBytes(superParentValues[i]);
				//value attribute doesn't work with setAttribute method
				//should be called before setAttribute method
				parent[attrNameValue[0]] = attrNameValue[1];
				parent.setAttribute(attrNameValue[0], attrNameValue[1]);
			}
		}

		allTags.push(parent);

		for (var i = 1; i < nameValues.length; i++) {
			var name = nameValues[i].name;
			var values = nameValues[i].values;

            // tn stands for tagName
			var tn = getTagNameFromCompressedBytes(values[0]);

			var child;

			if (tn === '#' || tn === '$' || tn === '%') {
			    var txt = (tn === '$' || tn === '%') ? wffBMUtil.getIntFromOptimizedBytes(values[1]).toString()
            			: tn === '#' ? getStringFromBytes(values[1]) : '';
				child = document.createDocumentFragment();
				child.appendChild(document.createTextNode(txt));
			} else if (tn === '@') {
				// @ short for html content
				var text = getStringFromBytes(values[1]);
				child = document.createDocumentFragment();				
				appendHtmlAsChildren(child, text);
			} else {
				child = document.createElement(tn);

				for (var j = 1; j < values.length; j++) {
					var attrNameValue = getAttrNameValueFromCompressedBytes(values[j]);
					//value attribute doesn't work with setAttribute method
					//should be called before setAttribute method
					child[attrNameValue[0]] = attrNameValue[1];
					child.setAttribute(attrNameValue[0], attrNameValue[1]);
				}
			}

			allTags.push(child);

			var parentIndex = wffBMUtil.getIntFromOptimizedBytes(name);
			allTags[parentIndex].appendChild(child);
		}

		return parent;
	};
	
	if(wffGlobal.CPRSD_DATA) {
		this.createTagFromWffBMBytes = this.createTagFromCompressedWffBMBytes;
	}
	
	//p for parent
	this.getChildByNthIndexBytes = function(p, chldNdxOptmzdIntByts) {
		//childIndex
		var i = wffBMUtil.getIntFromOptimizedBytes(chldNdxOptmzdIntByts);
		return p.childNodes[i];
	};
	
};

