/**
 * 
 */

var wffTagUtil = new function() {

	var encoder = wffGlobal.encoder;
	var decoder = wffGlobal.decoder;

	var getStringFromBytes = function(utf8Bytes) {
		return decoder.decode(new Uint8Array(utf8Bytes));
	};
	
	
	this.createTagFromBytes = function (bytes) {
		var htmlString = getStringFromBytes(bytes);
		var div = document.createElement('div');
		div.innerHTML = htmlString;
		return div.firstChild;
	};

	this.getTagByTagNameAndWffId = function(tagName, wffId) {
		console.log('getTagByTagNameAndWffId tagName', tagName, 'wffId', wffId);
		var elements = document.querySelectorAll(tagName + '[data-wff-id="'
				+ wffId + '"]');
		if (elements.length > 1) {
			alert('multiple tags with same wff id');
		}
		return elements[0];
	};

	this.getTagByWffId = function(wffId) {
		console.log('getTagByTagNameAndWffId ', 'wffId', wffId);
		var elements = document.querySelectorAll('[data-wff-id="' + wffId
				+ '"]');
		if (elements.length > 1) {
			alert('multiple tags with same wff id');
		}
		return elements[0];
	};

	/*
	 * this method will return string wff id from wff id bytes
	 */
	this.getWffIdFromWffIdBytes = function(wffIdBytes) {

		var sOrC = decoder.decode(new Uint8Array([ wffIdBytes[0] ]));
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
	
	this.splitAttrNameValue = function(attrNameValue) {
		//attrNameValue will contain string like name=attr-name
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
		return [attrName, attrValue];
	};

	// getWffIdBytesFromTag = this.getWffIdBytesFromTag;
	// var div = document.createElement('div');
	// div.setAttribute('data-wff-id', 'S255');
	// console.log('div', div);
	// var v = getWffIdBytesFromTag(div);
	// console.log('v' ,v);

};