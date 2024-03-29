/**
 * this is wff binary message version 1 implementation
 */

var wffBMCRUIDUtil = new function() {

	var encoder = wffGlobal.encoder;
	var decoder = wffGlobal.decoder;
	
	var encodedBytesForHash = encoder.encode('#');	

	// this.applyAttributeUpadates = function (wffBMBytes) {
	// var nameValues = wffBMUtil.parseWffBinaryMessageBytes(wffBMBytes);
	// };

	this.getTagDeletedWffBMBytes = function(tag) {
		var nameValues = [];

		var tUtf8Bytes = encoder.encode("T");
		var cUtf8Bytes = encoder.encode("DT");
		var taskNameValue = {
			'name' : tUtf8Bytes,
			'values' : [ cUtf8Bytes ]
		};
		nameValues.push(taskNameValue);

		var wffTagId = wffTagUtil.getWffIdBytesFromTag(tag);
		
		var nameValue = {
			'name' : wffTagId,
			'values' : []
		};
		nameValues.push(nameValue);
		return wffBMUtil.getWffBinaryMessageBytes(nameValues);
	};

	this.getAttrUpdatedWffBMBytes = function(attrName, attrValue, tagDocIndex) {
		var nameValues = [];

		var tUtf8Bytes = encoder.encode("T");
		var cUtf8Bytes = encoder.encode("UA");

		var taskNameValue = {
			'name' : tUtf8Bytes,
			'values' : [ cUtf8Bytes ]
		};

		nameValues.push(taskNameValue);

		var attrNameValueBytes = encoder.encode(attrName + "="
				+ attrValue);

		var nameValue = {
			'name' : wffBMUtil.getOptimizedBytesFromInt(tagDocIndex),
			'values' : [ attrNameValueBytes ]
		};

		nameValues.push(nameValue);

		return wffBMUtil.getWffBinaryMessageBytes(nameValues);
	};
	
	var recurChild = function(nameValues, tag, parentIndex) {

		var indexInWffBinaryMessage = parentIndex + 1;

		var nodeName = tag.nodeName;
		
		//in javascript element.nodeName will be #text if it contains Text Node		
		if (nodeName != '#text') {

			var values = [];

			var nodeNameBytes = encoder.encode(nodeName);
			
			values.push(nodeNameBytes);

			for (var i = 0; i < tag.attributes.length; i++) {
				var attribute = tag.attributes[i];

				var attrNameAndValueBytes;

				if (attribute.value != null) {
					attrNameAndValueBytes = encoder
							.encode(attribute.name + '=' + attribute.value);
				} else {
					attrNameAndValueBytes = encoder
							.encode(attribute.name);
				}

				values.push(attrNameAndValueBytes);

			}

			var nameValue = {
				'name' : wffBMUtil.getOptimizedBytesFromInt(parentIndex),
				'values' : values
			};

			nameValues.push(nameValue);

		} else {
			// we are using short form for #text as #						
			var nodeValueBytes = encoder.encode(tag.nodeValue);
			var values = [ encodedBytesForHash, nodeValueBytes ];
			var nameValue = {
				'name' : wffBMUtil.getOptimizedBytesFromInt(parentIndex),
				'values' : values
			};

			nameValues.push(nameValue);

		}

		parentIndex++;

		for (var i = 0; i < tag.childNodes.length; i++) {
			recurChild(nameValues, tag.childNodes[i], parentIndex);

		}

	};

	this.getTagCreatedWffBMBytes = function(tag, parentDocIndex) {
		var nameValues = [];

		var tUtf8Bytes = encoder.encode("T");
		var cUtf8Bytes = encoder.encode("C");
		var taskNameValue = {
			'name' : tUtf8Bytes,
			'values' : [ cUtf8Bytes ]
		};
		nameValues.push(taskNameValue);

		var parentIndex = 0;
		recurChild(nameValues, tag, parentIndex);
		// the parent index in the document.
		nameValues[1].name = wffBMUtil.getOptimizedBytesFromInt(parentDocIndex);
		return wffBMUtil.getWffBinaryMessageBytes(nameValues);
	};

	

};

