package com.webfirmframework.wffweb.server.page.js;

import static org.junit.Assert.*;

import org.junit.Test;

public class WffJsFileTest {
    
    private static String expectedContent = "var wffLog = console.log;if (!Array.from) {\n" + 
            "Array.from = (function() {\n" + 
            "var toStr = Object.prototype.toString;var isCallable = function(fn) {\n" + 
            "return typeof fn === 'function'\n" + 
            "|| toStr.call(fn) === '[object Function]';};var toInteger = function(value) {\n" + 
            "var number = Number(value);if (isNaN(number)) {\n" + 
            "return 0;}\n" + 
            "if (number === 0 || !isFinite(number)) {\n" + 
            "return number;}\n" + 
            "return (number > 0 ? 1 : -1) * Math.floor(Math.abs(number));};var maxSafeInteger = Math.pow(2, 53) - 1;var toLength = function(value) {\n" + 
            "var len = toInteger(value);return Math.min(Math.max(len, 0), maxSafeInteger);};return function from(arrayLike) {\n" + 
            "var C = this;var items = Object(arrayLike);if (arrayLike == null) {\n" + 
            "throw new TypeError(\n" + 
            "\"Array.from requires an array-like object - not null or undefined\");}\n" + 
            "var mapFn = arguments.length > 1 ? arguments[1] : void undefined;var T;if (typeof mapFn !== 'undefined') {\n" + 
            "if (!isCallable(mapFn)) {\n" + 
            "throw new TypeError(\n" + 
            "'Array.from: when provided, the second argument must be a function');}\n" + 
            "if (arguments.length > 2) {\n" + 
            "T = arguments[2];}\n" + 
            "}\n" + 
            "var len = toLength(items.length);var A = isCallable(C) ? Object(new C(len)) : new Array(len);var k = 0;var kValue;while (k < len) {\n" + 
            "kValue = items[k];if (mapFn) {\n" + 
            "A[k] = typeof T === 'undefined' ? mapFn(kValue, k) : mapFn\n" + 
            ".call(T, kValue, k);} else {\n" + 
            "A[k] = kValue;}\n" + 
            "k += 1;}\n" + 
            "A.length = len;return A;};}());}window.wffGlobal = new function() {\n" + 
            "var wffId = -1;this.getUniqueWffIntId = function() {\n" + 
            "return ++wffId;};this.CPRSD_DATA = true;this.NDXD_TGS = [\"#\",\"@\",\"a\",\"b\",\"i\",\"p\",\"q\",\"s\",\"u\",\"br\",\"dd\",\"dl\",\"dt\",\"em\",\"h1\",\"h2\",\"h3\",\"h4\",\"h5\",\"h6\",\"hr\",\"li\",\"ol\",\"rp\",\"rt\",\"td\",\"th\",\"tr\",\"ul\",\"bdi\",\"bdo\",\"col\",\"del\",\"dfn\",\"div\",\"img\",\"ins\",\"kbd\",\"map\",\"nav\",\"pre\",\"qfn\",\"sub\",\"sup\",\"svg\",\"var\",\"wbr\",\"abbr\",\"area\",\"base\",\"body\",\"cite\",\"code\",\"data\",\"form\",\"head\",\"html\",\"line\",\"link\",\"main\",\"mark\",\"math\",\"menu\",\"meta\",\"path\",\"rect\",\"ruby\",\"samp\",\"span\",\"text\",\"time\",\"aside\",\"audio\",\"embed\",\"input\",\"label\",\"meter\",\"param\",\"small\",\"style\",\"table\",\"tbody\",\"tfoot\",\"thead\",\"title\",\"track\",\"video\",\"button\",\"canvas\",\"circle\",\"dialog\",\"figure\",\"footer\",\"header\",\"hgroup\",\"iframe\",\"keygen\",\"legend\",\"object\",\"option\",\"output\",\"script\",\"select\",\"source\",\"strong\",\"address\",\"article\",\"caption\",\"details\",\"ellipse\",\"picture\",\"polygon\",\"section\",\"summary\",\"basefont\",\"colgroup\",\"datalist\",\"fieldset\",\"menuitem\",\"noscript\",\"optgroup\",\"polyline\",\"progress\",\"template\",\"textarea\",\"blockquote\",\"figcaption\"];this.NDXD_ATRBS = [\"id\",\"alt\",\"dir\",\"for\",\"low\",\"max\",\"min\",\"rel\",\"rev\",\"src\",\"cols\",\"face\",\"form\",\"high\",\"href\",\"lang\",\"list\",\"loop\",\"name\",\"open\",\"role\",\"rows\",\"size\",\"step\",\"type\",\"wrap\",\"align\",\"async\",\"class\",\"color\",\"defer\",\"ismap\",\"media\",\"muted\",\"nonce\",\"oncut\",\"scope\",\"shape\",\"sizes\",\"style\",\"title\",\"value\",\"width\",\"accept\",\"action\",\"border\",\"coords\",\"height\",\"hidden\",\"method\",\"nohref\",\"onblur\",\"oncopy\",\"ondrag\",\"ondrop\",\"onload\",\"onplay\",\"onshow\",\"poster\",\"sorted\",\"srcset\",\"target\",\"usemap\",\"charset\",\"checked\",\"colspan\",\"content\",\"default\",\"dirname\",\"enctype\",\"headers\",\"onabort\",\"onclick\",\"onended\",\"onerror\",\"onfocus\",\"oninput\",\"onkeyup\",\"onpaste\",\"onpause\",\"onreset\",\"onwheel\",\"optimum\",\"pattern\",\"preload\",\"rowspan\",\"sandbox\",\"autoplay\",\"controls\",\"datetime\",\"disabled\",\"download\",\"dropzone\",\"hreflang\",\"multiple\",\"onchange\",\"ononline\",\"onresize\",\"onscroll\",\"onsearch\",\"onseeked\",\"onselect\",\"onsubmit\",\"ontoggle\",\"onunload\",\"readonly\",\"required\",\"reversed\",\"selected\",\"tabindex\",\"accesskey\",\"autofocus\",\"draggable\",\"maxlength\",\"minlength\",\"oncanplay\",\"ondragend\",\"onemptied\",\"onfocusin\",\"oninvalid\",\"onkeydown\",\"onmouseup\",\"onoffline\",\"onplaying\",\"onseeking\",\"onstalled\",\"onstorage\",\"onsuspend\",\"onwaiting\",\"translate\",\"formaction\",\"formmethod\",\"formtarget\",\"http-equiv\",\"ondblclick\",\"ondragover\",\"onfocusout\",\"onkeypress\",\"onmouseout\",\"onpagehide\",\"onpageshow\",\"onpopstate\",\"onprogress\",\"ontouchend\",\"spellcheck\",\"cellpadding\",\"cellspacing\",\"contextmenu\",\"data-wff-id\",\"formenctype\",\"ondragenter\",\"ondragleave\",\"ondragstart\",\"onloadstart\",\"onmousedown\",\"onmousemove\",\"onmouseover\",\"ontouchmove\",\"placeholder\",\"animationend\",\"autocomplete\",\"onafterprint\",\"onhashchange\",\"onloadeddata\",\"onmouseenter\",\"onmouseleave\",\"onratechange\",\"ontimeupdate\",\"ontouchstart\",\"onbeforeprint\",\"oncontextmenu\",\"ontouchcancel\",\"transitionend\",\"accept-charset\",\"animationstart\",\"formnovalidate\",\"onbeforeunload\",\"onvolumechange\",\"contenteditable\",\"oncanplaythrough\",\"ondurationchange\",\"onloadedmetadata\",\"animationiteration\"];this.NDXD_VNT_ATRBS = [\"oncut\",\"onblur\",\"oncopy\",\"ondrag\",\"ondrop\",\"onload\",\"onplay\",\"onshow\",\"onabort\",\"onclick\",\"onended\",\"onerror\",\"onfocus\",\"oninput\",\"onkeyup\",\"onpaste\",\"onpause\",\"onreset\",\"onwheel\",\"onchange\",\"ononline\",\"onresize\",\"onscroll\",\"onsearch\",\"onseeked\",\"onselect\",\"onsubmit\",\"ontoggle\",\"onunload\",\"oncanplay\",\"ondragend\",\"onemptied\",\"onfocusin\",\"oninvalid\",\"onkeydown\",\"onmouseup\",\"onoffline\",\"onplaying\",\"onseeking\",\"onstalled\",\"onstorage\",\"onsuspend\",\"onwaiting\",\"ondblclick\",\"ondragover\",\"onfocusout\",\"onkeypress\",\"onmouseout\",\"onpagehide\",\"onpageshow\",\"onpopstate\",\"onprogress\",\"ontouchend\",\"ondragenter\",\"ondragleave\",\"ondragstart\",\"onloadstart\",\"onmousedown\",\"onmousemove\",\"onmouseover\",\"ontouchmove\",\"onafterprint\",\"onhashchange\",\"onloadeddata\",\"onmouseenter\",\"onmouseleave\",\"onratechange\",\"ontimeupdate\",\"ontouchstart\",\"onbeforeprint\",\"oncontextmenu\",\"ontouchcancel\",\"onbeforeunload\",\"onvolumechange\",\"oncanplaythrough\",\"ondurationchange\",\"onloadedmetadata\"];this.NDXD_BLN_ATRBS = [\"open\",\"async\",\"defer\",\"ismap\",\"hidden\",\"checked\",\"default\",\"controls\",\"disabled\",\"multiple\",\"readonly\",\"reversed\",\"selected\"];this.taskValues = {INVOKE_ASYNC_METHOD:0,ATTRIBUTE_UPDATED:1,TASK:2,APPENDED_CHILD_TAG:3,REMOVED_TAGS:4,APPENDED_CHILDREN_TAGS:5,REMOVED_ALL_CHILDREN_TAGS:6,MOVED_CHILDREN_TAGS:7,INSERTED_BEFORE_TAG:8,INSERTED_AFTER_TAG:9,REPLACED_WITH_TAGS:10,REMOVED_ATTRIBUTES:11,ADDED_ATTRIBUTES:12,MANY_TO_ONE:13,ONE_TO_MANY:14,MANY_TO_MANY:15,ONE_TO_ONE:16,ADDED_INNER_HTML:17,INVOKE_POST_FUNCTION:18,EXEC_JS:19,RELOAD_BROWSER:20,RELOAD_BROWSER_FROM_CACHE:21,INVOKE_CALLBACK_FUNCTION:22,INVOKE_CUSTOM_SERVER_METHOD:23,TASK_OF_TASKS:24,COPY_INNER_TEXT_TO_VALUE:25,REMOVE_BROWSER_PAGE:26,SET_BM_OBJ_ON_TAG:27,SET_BM_ARR_ON_TAG:28,DEL_BM_OBJ_OR_ARR_FROM_TAG:29};this.WS_URL = \"ws://webfirmframework.com\";this.INSTANCE_ID = \"instance-id-1234585-451\";this.REMOVE_PREV_BP_ON_INITTAB = true;this.REMOVE_PREV_BP_ON_TABCLOSE = true;this.WS_RECON = 2000;if ((typeof TextEncoder) === \"undefined\") {\n" + 
            "this.encoder = new function TextEncoder(charset) {\n" + 
            "if (charset === \"utf-8\") {\n" + 
            "this.encode = function(text) {\n" + 
            "var bytes = [];for (var i = 0; i < text.length; i++) {\n" + 
            "var charCode = text.charCodeAt(i);if (charCode < 0x80) {\n" + 
            "bytes.push(charCode);} else if (charCode < 0x800) {\n" + 
            "bytes.push(0xc0 | (charCode >> 6),\n" + 
            "0x80 | (charCode & 0x3f));} else if (charCode < 0xd800 || charCode >= 0xe000) {\n" + 
            "bytes.push(0xe0 | (charCode >> 12),\n" + 
            "0x80 | ((charCode >> 6) & 0x3f),\n" + 
            "0x80 | (charCode & 0x3f));} else {\n" + 
            "i++;charCode = 0x10000 + (((charCode & 0x3ff) << 10) | (text\n" + 
            ".charCodeAt(i) & 0x3ff));bytes.push(0xf0 | (charCode >> 18),\n" + 
            "0x80 | ((charCode >> 12) & 0x3f),\n" + 
            "0x80 | ((charCode >> 6) & 0x3f),\n" + 
            "0x80 | (charCode & 0x3f));}\n" + 
            "}\n" + 
            "return bytes;};}\n" + 
            "}(\"utf-8\");} else {\n" + 
            "this.encoder = new TextEncoder(\"utf-8\");}\n" + 
            "if ((typeof TextDecoder) === \"undefined\") {\n" + 
            "this.decoder = new function TextDecoder(charset) {\n" + 
            "if (charset === \"utf-8\") {\n" + 
            "this.decode = function(bytes) {\n" + 
            "var text = '', i;for (i = 0; i < bytes.length; i++) {\n" + 
            "var value = bytes[i];if (value < 0x80) {\n" + 
            "text += String.fromCharCode(value);} else if (value > 0xBF && value < 0xE0) {\n" + 
            "text += String.fromCharCode((value & 0x1F) << 6\n" + 
            "| bytes[i + 1] & 0x3F);i += 1;} else if (value > 0xDF && value < 0xF0) {\n" + 
            "text += String.fromCharCode((value & 0x0F) << 12\n" + 
            "| (bytes[i + 1] & 0x3F) << 6 | bytes[i + 2]\n" + 
            "& 0x3F);i += 2;} else {\n" + 
            "var charCode = ((value & 0x07) << 18\n" + 
            "| (bytes[i + 1] & 0x3F) << 12\n" + 
            "| (bytes[i + 2] & 0x3F) << 6 | bytes[i + 3] & 0x3F) - 0x010000;text += String.fromCharCode(\n" + 
            "charCode >> 10 | 0xD800,\n" + 
            "charCode & 0x03FF | 0xDC00);i += 3;}\n" + 
            "}\n" + 
            "return text;};}\n" + 
            "}(\"utf-8\");} else {\n" + 
            "this.decoder = new TextDecoder(\"utf-8\");}\n" + 
            "};var wffBMUtil = new function(){\n" + 
            "\n" + 
            "this.f13 = function(v76){\n" + 
            "var v34 = 0;var v22 = 0;for (var i = 0; i < v76.length; i++){\n" + 
            "var name = v76[i].name;var values = v76[i].values;if(name.length > v34){\n" + 
            "v34 = name.length;}\n" + 
            "var v18 = 0;for (var j = 0; j < values.length; j++){\n" + 
            "v18 += values[j].length;}\n" + 
            "var v2 = f3(v18);var v4 = values.length\n" + 
            "* v2;var v3 = v18\n" + 
            "+ v4;if(v3 > v22){\n" + 
            "v22 = v3;}\n" + 
            "}\n" + 
            "var v14 = f3(v34);var v12 = f3(v22);var v11 = [];v11.push(v14);v11.push(v12);for (var i = 0; i < v76.length; i++){\n" + 
            "var name = v76[i].name;var v43 = f28(name.length,\n" + 
            "v14);f33(v11, v43);f33(v11, name);var values = v76[i].values;if(values.length == 0){\n" + 
            "f33(v11, f28(0,\n" + 
            "v12));}else{\n" + 
            "var v72 = 0;for (var l = 0; l < values.length; l++){\n" + 
            "v72 += values[l].length;}\n" + 
            "v72 += (v12 * values.length);var v37 = f28(v72,\n" + 
            "v12);f33(v11, v37);for (var m = 0; m < values.length; m++){\n" + 
            "var value = values[m];v37 = f28(value.length,\n" + 
            "v12);f33(v11, v37);f33(v11, value);}\n" + 
            "}\n" + 
            "}\n" + 
            "return v11;};this.f9 = function(message){\n" + 
            "var v76 = [];var v10 = message[0];var v8 = message[1];for (var v57 = 2; v57 < message.length; v57++){\n" + 
            "var v84 = {};var v39 = subarray(message,\n" + 
            "v57, v10);v57 = v57 + v10;var v48 = f14(v39);var v85 = subarray(message, v57,\n" + 
            "v48);v57 = v57 + v85.length;v84.name = v85;var v35 = subarray(message,\n" + 
            "v57, v8);v57 = v57 + v8;v48 = f14(v35);var v77 = subarray(message, v57,\n" + 
            "v48);v57 = v57 + v77.length - 1;var v9 = f7(\n" + 
            "v8, v77);v84.values = v9;v76.push(v84);}\n" + 
            "return v76;};\n" + 
            "var f7 = function(v8, v77){\n" + 
            "var values = [];for (var i = 0; i < v77.length; i++){\n" + 
            "var v35 = subarray(v77, i,\n" + 
            "v8);var v66 = f14(v35);var value = subarray(v77, i\n" + 
            "+ v8, v66);values.push(value);i = i + v8 + v66 - 1;}\n" + 
            "return values;};\n" + 
            "var f33 = function(v52, v40){\n" + 
            "for (var a = 0; a < v40.length; a++){\n" + 
            "v52.push(v40[a]);}\n" + 
            "};\n" + 
            "var f4 = function(v52, v78,\n" + 
            "position, length){\n" + 
            "var uptoIndex = position + length;for (var a = position; a < uptoIndex; a++){\n" + 
            "v52.push(v78[a]);}\n" + 
            "};this.f4 = f4;var subarray = function(srcAry, pos, len){\n" + 
            "var upto = pos + len;if(srcAry.slice){\n" + 
            "return srcAry.slice(pos, upto);}\n" + 
            "var sub = [];for (var i = pos; i < upto; i++){\n" + 
            "sub.push(srcAry[i]);}\n" + 
            "return sub;};this.subarray = subarray;\n" + 
            "var f37 = function(bytes){\n" + 
            "return bytes[0] << 24 | (bytes[1] & 0xFF) << 16\n" + 
            "| (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);};\n" + 
            "var f38 = function(value){\n" + 
            "var bytes = [ (value >> 24), (value >> 16), (value >> 8), value ];return bytes;};\n" + 
            "var f14 = function(bytes){\n" + 
            "if(bytes.length == 4){\n" + 
            "return bytes[0] << 24 | (bytes[1] & 0xFF) << 16\n" + 
            "| (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);}else if(bytes.length == 3){\n" + 
            "return (bytes[0] & 0xFF) << 16 | (bytes[1] & 0xFF) << 8\n" + 
            "| (bytes[2] & 0xFF);}else if(bytes.length == 2){\n" + 
            "return (bytes[0] & 0xFF) << 8 | (bytes[1] & 0xFF);}else if(bytes.length == 1){\n" + 
            "return (bytes[0] & 0xFF);}\n" + 
            "return bytes[0] << 24 | (bytes[1] & 0xFF) << 16\n" + 
            "| (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);};this.f14 = f14;\n" + 
            "var f15 = function(value){\n" + 
            "var v65 = (value >> 24);var v80 = (value >> 16);var v67 = (value >> 8);var v71 = value;if(v65 == 0){\n" + 
            "if(v80 == 0){\n" + 
            "if(v67 == 0){\n" + 
            "return [ v71 ];}\n" + 
            "return [ v67, v71 ];}\n" + 
            "return [ v80, v67, v71 ];}\n" + 
            "return [ v65, v80, v67, v71 ];};this.f15 = f15;\n" + 
            "var f3 = function(value){\n" + 
            "var v65 = (value >> 24);var v80 = (value >> 16);var v67 = (value >> 8);var v71 = value;if(v65 == 0){\n" + 
            "if(v80 == 0){\n" + 
            "if(v67 == 0){\n" + 
            "return 1;}\n" + 
            "return 2;}\n" + 
            "return 3;}\n" + 
            "return 4;};\n" + 
            "var f28 = function(value, v53){\n" + 
            "var v65 = (value >> 24);var v80 = (value >> 16);var v67 = (value >> 8);var v71 = value;if(v53 == 1){\n" + 
            "return [ v71 ];}else if(v53 == 2){\n" + 
            "return [ v67, v71 ];}else if(v53 == 3){\n" + 
            "return [ v80, v67, v71 ];}\n" + 
            "return [ v65, v80, v67, v71 ];};\n" + 
            "var f32 = function(v62){\n" + 
            "var arrayBuff = new ArrayBuffer(8);var float64 = new Float64Array(arrayBuff);float64[0] = v62;var uin = new Int8Array(arrayBuff);return Array.from(uin).reverse();};this.f32 = f32;\n" + 
            "var f8 = function(bytes){\n" + 
            "var buffer = new ArrayBuffer(8);var int8Array = new Int8Array(buffer);for (var i = 0; i < bytes.length; i++){\n" + 
            "int8Array[i] = bytes[bytes.length - 1 - i];}\n" + 
            "return new Float64Array(buffer)[0];};this.f8 = f8;};\n" + 
            "var wffTagUtil = new function(){\n" + 
            "var encoder = wffGlobal.encoder;var decoder = wffGlobal.decoder;var f30 = function(utf8Bytes){\n" + 
            "return decoder.decode(new Uint8Array(utf8Bytes));};var subarray = wffBMUtil.subarray;var f5 = function(utf8Bytes){\n" + 
            "if(utf8Bytes.length == 1){\n" + 
            "var v95 = wffBMUtil.f14(utf8Bytes);return wffGlobal.NDXD_TGS[v95];}\n" + 
            "var v7 = utf8Bytes[0];if(v7 > 0){\n" + 
            "var v26 = subarray(utf8Bytes, 1, v7);var v95 = wffBMUtil.f14(v26);return wffGlobal.NDXD_TGS[v95];}else{\n" + 
            "var v60 = utf8Bytes.length - 1;var v59 = subarray(utf8Bytes, 1, v60);return f30(v59);}\n" + 
            "};this.f5 = f5;var getAttrNameFromCompressedBytes = function(utf8Bytes){\n" + 
            "if(utf8Bytes.length == 1){\n" + 
            "var v82 = wffBMUtil.f14(utf8Bytes);return wffGlobal.NDXD_ATRBS[v82];}\n" + 
            "var v5 = utf8Bytes[0];if(v5 > 0){\n" + 
            "var v19 = subarray(utf8Bytes, 1, v5);var v82 = wffBMUtil.f14(v19);return wffGlobal.NDXD_ATRBS[v82];}else{\n" + 
            "var v60 = utf8Bytes.length - 1;var v91Bytes = subarray(utf8Bytes, 1, v60);return f30(v91Bytes);}\n" + 
            "};this.getAttrNameFromCompressedBytes = getAttrNameFromCompressedBytes;var f1 = function(utf8Bytes){\n" + 
            "var v5 = utf8Bytes[0];if(v5 > 0){\n" + 
            "var v19 = subarray(utf8Bytes, 1, v5);var v82 = wffBMUtil.f14(v19);var v83 = utf8Bytes.length - (v5 + 1);var v68 = subarray(utf8Bytes, v5 + 1, v83);var attrNamVal = [wffGlobal.NDXD_ATRBS[v82], f30(v68)];return attrNamVal;}else{\n" + 
            "var v60 = utf8Bytes.length - 1;var v69 = subarray(utf8Bytes, 1, v60);return f31(f30(v69));}\n" + 
            "};this.f1 = f1;var f26 = function(tag, htmlString){\n" + 
            "var tmpDv = document.createElement('div');tmpDv.innerHTML = htmlString;for (var i = 0; i < tmpDv.childNodes.length; i++){\n" + 
            "var cn = tmpDv.childNodes[i];if(cn.nodeName === '#text'){\n" + 
            "tag.appendChild(document.createTextNode(cn.nodeValue));}else{\n" + 
            "tag.appendChild(cn.cloneNode(true));}\n" + 
            "}\n" + 
            "};this.f16 = function(tagName, v98){\n" + 
            "var elements = document.querySelectorAll(tagName + '[data-wff-id=\"'\n" + 
            "+ v98 + '\"]');if(elements.length > 1){\n" + 
            "wffLog('multiple tags with same wff id', 'tagName', 'v98', v98);}\n" + 
            "return elements[0];};this.f39 = function(v98){\n" + 
            "var elements = document.querySelectorAll('[data-wff-id=\"' + v98\n" + 
            "+ '\"]');if(elements.length > 1){\n" + 
            "wffLog('multiple tags with same wff id', 'v98', v98);}\n" + 
            "return elements[0];};\n" + 
            "this.f23 = function(v75){\n" + 
            "var v100 = f30([ v75[0] ]);var intBytes = [];for (var i = 1; i < v75.length; i++){\n" + 
            "intBytes.push(v75[i]);}\n" + 
            "var v99 = wffBMUtil.f14(intBytes);return v100 + v99;};this.f25 = function(tag){\n" + 
            "var v87 = tag.getAttribute(\"data-wff-id\");if(v87 == null){\n" + 
            "v87 = \"C\" + wffGlobal.getUniqueWffIntId();tag.setAttribute(\"data-wff-id\", v87);}\n" + 
            "var v100 = v87.substring(0, 1);var v51 = encoder.encode(v100);var v75 = [ v51[0] ];var v99 = v87.substring(1, v87.length);var v74 = wffBMUtil.f15(parseInt(v99));v75 = v75.concat(v74);return v75;};var f31 = function(v56){\n" + 
            "var v91;var v87;var v31 = v56.indexOf('=');if(v31 != -1){\n" + 
            "v91 = v56.substring(0, v31);v87 = v56.substring(v31 + 1,\n" + 
            "v56.length);}else{\n" + 
            "v91 = v56;v87 = '';}\n" + 
            "return [ v91, v87 ];};this.f31 = f31;this.f18 = function(bytes){\n" + 
            "var v76 = wffBMUtil.f9(bytes);var v16 = v76[0];var v29 = v16.values;var v96 = [];var parent;var v54 = f30(v29[0]);if(v54 === '#'){\n" + 
            "var text = f30(v29[1]);parent = document.createDocumentFragment();parent.appendChild(document.createTextNode(text));}else if(v54 === '@'){\n" + 
            "var text = f30(v29[1]);parent = document.createDocumentFragment();f26(parent, text);}else{\n" + 
            "parent = document.createElement(v54);for (var i = 1; i < v29.length; i++){\n" + 
            "var v56 = f31(f30(v29[i]));parent[v56[0]] = v56[1];parent.setAttribute(v56[0], v56[1]);}\n" + 
            "}\n" + 
            "v96.push(parent);for (var i = 1; i < v76.length; i++){\n" + 
            "var name = v76[i].name;var values = v76[i].values;var tagName = f30(values[0]);var child;if(tagName === '#'){\n" + 
            "var text = f30(values[1]);child = document.createDocumentFragment();child.appendChild(document.createTextNode(text));}else if(tagName === '@'){\n" + 
            "var text = f30(values[1]);child = document.createDocumentFragment();f26(child, text);}else{\n" + 
            "child = document.createElement(tagName);for (var j = 1; j < values.length; j++){\n" + 
            "var v56 = f31(f30(values[j]));child[v56[0]] = v56[1];child.setAttribute(v56[0], v56[1]);}\n" + 
            "}\n" + 
            "v96.push(child);var v61 = wffBMUtil.f14(name);v96[v61].appendChild(child);}\n" + 
            "return parent;};this.f2 = function(bytes){\n" + 
            "var v76 = wffBMUtil.f9(bytes);var v16 = v76[0];var v29 = v16.values;var v96 = [];var parent;var v54 = f5(v29[0]);if(v54 === '#'){\n" + 
            "var text = f30(v29[1]);parent = document.createDocumentFragment();parent.appendChild(document.createTextNode(text));}else if(v54 === '@'){\n" + 
            "var text = f30(v29[1]);parent = document.createDocumentFragment();f26(parent, text);}else{\n" + 
            "parent = document.createElement(v54);for (var i = 1; i < v29.length; i++){\n" + 
            "var v56 = f1(v29[i]);parent[v56[0]] = v56[1];parent.setAttribute(v56[0], v56[1]);}\n" + 
            "}\n" + 
            "v96.push(parent);for (var i = 1; i < v76.length; i++){\n" + 
            "var name = v76[i].name;var values = v76[i].values;var tagName = f5(values[0]);var child;if(tagName === '#'){\n" + 
            "var text = f30(values[1]);child = document.createDocumentFragment();child.appendChild(document.createTextNode(text));}else if(tagName === '@'){\n" + 
            "var text = f30(values[1]);child = document.createDocumentFragment();f26(child, text);}else{\n" + 
            "child = document.createElement(tagName);for (var j = 1; j < values.length; j++){\n" + 
            "var v56 = f1(values[j]);child[v56[0]] = v56[1];child.setAttribute(v56[0], v56[1]);}\n" + 
            "}\n" + 
            "v96.push(child);var v61 = wffBMUtil.f14(name);v96[v61].appendChild(child);}\n" + 
            "return parent;};if(wffGlobal.CPRSD_DATA){\n" + 
            "this.f18 = this.f2;}\n" + 
            "this.f21 = function(p, v20){\n" + 
            "var i = wffBMUtil.f14(v20);return p.childNodes[i];};};\n" + 
            "var wffBMCRUIDUtil = new function(){\n" + 
            "var encoder = wffGlobal.encoder;var decoder = wffGlobal.decoder;var encodedBytesForHash = encoder.encode('#');this.f17 = function(tag){\n" + 
            "var v76 = [];var tUtf8Bytes = encoder.encode(\"T\");var cUtf8Bytes = encoder.encode(\"DT\");var v46 = {\n" + 
            "'name' : tUtf8Bytes,\n" + 
            "'values' : [ cUtf8Bytes ]\n" + 
            "};v76.push(v46);var v90 = wffTagUtil.f25(tag);var v84 = {\n" + 
            "'name' : v90,\n" + 
            "'values' : []\n" + 
            "};v76.push(v84);return wffBMUtil.f13(v76);};this.f12 = function(v91, v87, v64){\n" + 
            "var v76 = [];var tUtf8Bytes = encoder.encode(\"T\");var cUtf8Bytes = encoder.encode(\"UA\");var v46 = {\n" + 
            "'name' : tUtf8Bytes,\n" + 
            "'values' : [ cUtf8Bytes ]\n" + 
            "};v76.push(v46);var v24 = encoder.encode(v91 + \"=\"\n" + 
            "+ v87);var v84 = {\n" + 
            "'name' : wffBMUtil.f15(v64),\n" + 
            "'values' : [ v24 ]\n" + 
            "};v76.push(v84);return wffBMUtil.f13(v76);};var f42 = function(v76, tag, v61){\n" + 
            "var v6 = v61 + 1;var nodeName = tag.nodeName;if(nodeName != '#text'){\n" + 
            "var values = [];var v55 = encoder.encode(nodeName);values.push(v55);for (var i = 0; i < tag.attributes.length; i++){\n" + 
            "var attribute = tag.attributes[i];var v13;if(attribute.value != null){\n" + 
            "v13 = encoder\n" + 
            ".encode(attribute.name + '=' + attribute.value);}else{\n" + 
            "v13 = encoder\n" + 
            ".encode(attribute.name);}\n" + 
            "values.push(v13);}\n" + 
            "var v84 = {\n" + 
            "'name' : f15(v61),\n" + 
            "'values' : values\n" + 
            "};v76.push(v84);}else{\n" + 
            "var v44 = encoder.encode(tag.nodeValue);var values = [ encodedBytesForHash, v44 ];var v84 = {\n" + 
            "'name' : f15(v61),\n" + 
            "'values' : values\n" + 
            "};v76.push(v84);}\n" + 
            "v61++;for (var i = 0; i < tag.childNodes.length; i++){\n" + 
            "f42(v76, tag.childNodes[i], v61);}\n" + 
            "};this.f19 = function(tag, v41){\n" + 
            "var v76 = [];var tUtf8Bytes = encoder.encode(\"T\");var cUtf8Bytes = encoder.encode(\"C\");var v46 = {\n" + 
            "'name' : tUtf8Bytes,\n" + 
            "'values' : [ cUtf8Bytes ]\n" + 
            "};v76.push(v46);var v61 = 0;f42(v76, tag, v61);v76[1].name = wffBMUtil.f15(v41);return wffBMUtil.f13(v76);};};\n" + 
            "var wffClientCRUDUtil = new function(){\n" + 
            "var encoder = wffGlobal.encoder;var decoder = wffGlobal.decoder;var f30 = function(utf8Bytes){\n" + 
            "return decoder.decode(new Uint8Array(utf8Bytes));};var f43 = function(v76){\n" + 
            "var v46 = v76[0];var taskValue = v46.values[0][0];if(taskValue == wffGlobal.taskValues.ATTRIBUTE_UPDATED){\n" + 
            "for (var i = 1; i < v76.length; i++){\n" + 
            "var v56 = wffTagUtil.f1(v76[i].name);var v91 = v56[0];var v87 = v56[1];var v97 = v76[i].values;for (var j = 0; j < v97.length; j++){\n" + 
            "var v98 = wffTagUtil.f23(v97[j]);var v49 = wffTagUtil.f39(v98);v49[v91] = v87;v49.setAttribute(v91, v87);}\n" + 
            "}\n" + 
            "}\n" + 
            "else if(taskValue == wffGlobal.taskValues.APPENDED_CHILD_TAG\n" + 
            "|| taskValue == wffGlobal.taskValues.APPENDED_CHILDREN_TAGS){\n" + 
            "for (var i = 1; i < v76.length; i++){\n" + 
            "var v98 = wffTagUtil\n" + 
            ".f23(v76[i].name);var values = v76[i].values;var tagName = wffTagUtil.f5(values[0]);var parent = wffTagUtil.f16(tagName, v98);for (var j = 1; j < values.length; j++){\n" + 
            "var v89 = wffTagUtil\n" + 
            ".f18(values[j]);parent.appendChild(v89);}\n" + 
            "}\n" + 
            "}else if(taskValue == wffGlobal.taskValues.REMOVED_TAGS){\n" + 
            "for (var i = 1; i < v76.length; i++){\n" + 
            "var v98 = wffTagUtil\n" + 
            ".f23(v76[i].name);var values = v76[i].values;var tagName = wffTagUtil.f5(values[0]);var v63 = wffTagUtil.f16(tagName,\n" + 
            "v98);var parent = v63.parentNode;parent.removeChild(v63);}\n" + 
            "}else if(taskValue == wffGlobal.taskValues.REMOVED_ALL_CHILDREN_TAGS){\n" + 
            "for (var i = 1; i < v76.length; i++){\n" + 
            "var v98 = wffTagUtil\n" + 
            ".f23(v76[i].name);var values = v76[i].values;var tagName = wffTagUtil.f5(values[0]);var parentTag = wffTagUtil.f16(tagName,\n" + 
            "v98);while (parentTag.firstChild){\n" + 
            "parentTag.removeChild(parentTag.firstChild);}\n" + 
            "}\n" + 
            "}else if(taskValue == wffGlobal.taskValues.MOVED_CHILDREN_TAGS){\n" + 
            "for (var i = 1; i < v76.length; i++){\n" + 
            "var v25 = wffTagUtil\n" + 
            ".f23(v76[i].name);var values = v76[i].values;var v17 = wffTagUtil.f5(values[0]);var v33 = wffTagUtil.f16(\n" + 
            "v17, v25);var v92 = null;if(values[2].length == 0){\n" + 
            "v92 = wffTagUtil.f18(values[3]);}else{\n" + 
            "var v58 = wffTagUtil.f5(values[2]);var v73 = wffTagUtil.f23(values[1]);v92 = wffTagUtil.f16(v58,\n" + 
            "v73);if(typeof v92 !== 'undefined'){\n" + 
            "var previousParent = v92.parentNode;if(typeof previousParent !== 'undefined'){\n" + 
            "previousParent.removeChild(v92);}\n" + 
            "}else{\n" + 
            "v92 = wffTagUtil.f18(values[3]);}\n" + 
            "}\n" + 
            "v33.appendChild(v92);}\n" + 
            "}else if(taskValue == wffGlobal.taskValues.ADDED_ATTRIBUTES){\n" + 
            "for (var i = 1; i < v76.length; i++){\n" + 
            "var v84 = v76[i];if(v84.name[0] == wffGlobal.taskValues.MANY_TO_ONE){\n" + 
            "var tagName = wffTagUtil.f5(v84.values[0]);var v98 = wffTagUtil\n" + 
            ".f23(v84.values[1]);var v49 = wffTagUtil.f16(\n" + 
            "tagName, v98);for (var j = 2; j < v84.values.length; j++){\n" + 
            "var v56 = wffTagUtil.f1(v84.values[j]);var v91 = v56[0];var v87 = v56[1];v49[v91] = v87;v49.setAttribute(v91, v87);}\n" + 
            "}\n" + 
            "}\n" + 
            "}else if(taskValue == wffGlobal.taskValues.REMOVED_ATTRIBUTES){\n" + 
            "for (var i = 1; i < v76.length; i++){\n" + 
            "var v84 = v76[i];if(v84.name[0] == wffGlobal.taskValues.MANY_TO_ONE){\n" + 
            "var tagName = wffTagUtil.f5(v84.values[0]);var v98 = wffTagUtil\n" + 
            ".f23(v84.values[1]);var v49 = wffTagUtil.f16(\n" + 
            "tagName, v98);for (var j = 2; j < v84.values.length; j++){\n" + 
            "var v91 = wffTagUtil.getAttrNameFromCompressedBytes(v84.values[j]);v49.removeAttribute(v91);if(wffGlobal.NDXD_BLN_ATRBS.indexOf(v91) != -1){\n" + 
            "var prop = v49[v91];if(prop && prop === true){\n" + 
            "v49[v91] = false;}\n" + 
            "}\n" + 
            "}\n" + 
            "}\n" + 
            "}\n" + 
            "}else if(taskValue == wffGlobal.taskValues.ADDED_INNER_HTML){\n" + 
            "var tagName = wffTagUtil.f5(v76[1].name);var v98 = wffTagUtil\n" + 
            ".f23(v76[1].values[0]);var parentTag = wffTagUtil.f16(tagName,\n" + 
            "v98);while (parentTag.firstChild){\n" + 
            "parentTag.removeChild(parentTag.firstChild);}\n" + 
            "for (var i = 2; i < v76.length; i++){\n" + 
            "var values = v76[i].values;var v89 = wffTagUtil.f18(v76[i].name);if(values.length == 1 && values[0].length == 1){\n" + 
            "var existingTag = wffTagUtil.f16(\n" + 
            "v89.nodeName, v89\n" + 
            ".getAttribute(\"data-wff-id\"));if(existingTag){\n" + 
            "var v21 = existingTag.parentNode;v21.removeChild(existingTag);}\n" + 
            "}\n" + 
            "parentTag.appendChild(v89);}\n" + 
            "}else if(taskValue == wffGlobal.taskValues.INSERTED_BEFORE_TAG){\n" + 
            "var tagName = wffTagUtil.f5(v76[1].name);var v98 = wffTagUtil.f23(v76[1].values[0]);var parentTag = wffTagUtil.f16(tagName, v98);var v50 = wffTagUtil.f5(v76[2].name);var v86;if(v50 === '#'){\n" + 
            "var v20 = v76[2].values[0];v86 = wffTagUtil.f21(parentTag, v20);}else{\n" + 
            "var v42 = wffTagUtil.f23(v76[2].values[0]);v86 = wffTagUtil.f16(v50,\n" + 
            "v42);}\n" + 
            "for (var i = 3; i < v76.length; i++){\n" + 
            "var nm = v76[i].name;var values = v76[i].values;var v89 = wffTagUtil.f18(values[0]);if(nm.length == 1){\n" + 
            "var existingTag = wffTagUtil.f16(\n" + 
            "v89.nodeName, v89\n" + 
            ".getAttribute(\"data-wff-id\"));var v21 = existingTag.parentNode;v21.removeChild(existingTag);}\n" + 
            "parentTag.insertBefore(v89, v86);}\n" + 
            "}else if(taskValue == wffGlobal.taskValues.INSERTED_AFTER_TAG){\n" + 
            "var tagName = wffTagUtil.f5(v76[1].name);var v98 = wffTagUtil.f23(v76[1].values[0]);var parentTag = wffTagUtil.f16(tagName, v98);var v50 = wffTagUtil.f5(v76[2].name);var v86;if(v50 === '#'){\n" + 
            "var v20 = v76[2].values[0];v86 = wffTagUtil.f21(parentTag, v20);}else{\n" + 
            "var v42 = wffTagUtil.f23(v76[2].values[0]);v86 = wffTagUtil.f16(v50,\n" + 
            "v42);}\n" + 
            "var firstNd;for (var i = 3; i < v76.length; i++){\n" + 
            "var nm = v76[i].name;var values = v76[i].values;var v89 = wffTagUtil.f18(values[0]);if(nm.length == 1){\n" + 
            "var existingTag = wffTagUtil.f16(\n" + 
            "v89.nodeName, v89\n" + 
            ".getAttribute(\"data-wff-id\"));var v21 = existingTag.parentNode;v21.removeChild(existingTag);}\n" + 
            "parentTag.insertBefore(v89, v86);if(!firstNd){\n" + 
            "firstNd = v89;}\n" + 
            "}\n" + 
            "if(firstNd){\n" + 
            "parentTag.removeChild(v86);parentTag.insertBefore(v86, firstNd);}\n" + 
            "}else if(taskValue == wffGlobal.taskValues.REPLACED_WITH_TAGS){\n" + 
            "var tagName = wffTagUtil.f5(v76[1].name);var v98 = wffTagUtil.f23(v76[1].values[0]);var parentTag = wffTagUtil.f16(tagName, v98);var v50 = wffTagUtil.f5(v76[2].name);var v86;if(v50 === '#'){\n" + 
            "var v20 = v76[2].values[0];v86 = wffTagUtil.f21(parentTag, v20);}else{\n" + 
            "var v42 = wffTagUtil.f23(v76[2].values[0]);v86 = wffTagUtil.f16(v50,\n" + 
            "v42);}\n" + 
            "for (var i = 3; i < v76.length; i++){\n" + 
            "var nm = v76[i].name;var values = v76[i].values;var v89 = wffTagUtil.f18(values[0]);if(nm.length == 1){\n" + 
            "var existingTag = wffTagUtil.f16(\n" + 
            "v89.nodeName, v89\n" + 
            ".getAttribute(\"data-wff-id\"));var v21 = existingTag.parentNode;v21.removeChild(existingTag);}\n" + 
            "parentTag.insertBefore(v89, v86);}\n" + 
            "parentTag.removeChild(v86);}else if(taskValue == wffGlobal.taskValues.RELOAD_BROWSER){\n" + 
            "location.reload(true);}else if(taskValue == wffGlobal.taskValues.RELOAD_BROWSER_FROM_CACHE){\n" + 
            "location.reload();}else if(taskValue == wffGlobal.taskValues.EXEC_JS){\n" + 
            "var js = f30(v46.values[1]);if(window.execScript){\n" + 
            "window.execScript(js);}else{\n" + 
            "eval(js);}\n" + 
            "}else if(taskValue == wffGlobal.taskValues.COPY_INNER_TEXT_TO_VALUE){\n" + 
            "var tagName = wffTagUtil.f5(v76[1].name);var v98 = wffTagUtil\n" + 
            ".f23(v76[1].values[0]);var parentTag = wffTagUtil.f16(tagName,\n" + 
            "v98);var d = document.createElement('div');d.innerHTML = parentTag.outerHTML;parentTag.value = d.childNodes[0].innerText;}else if(taskValue == wffGlobal.taskValues.SET_BM_OBJ_ON_TAG\n" + 
            "|| taskValue == wffGlobal.taskValues.SET_BM_ARR_ON_TAG){\n" + 
            "var tagName = wffTagUtil.f5(v76[1].name);var v98 = wffTagUtil\n" + 
            ".f23(v76[1].values[0]);var tag = wffTagUtil.f16(tagName, v98);var ky = f30(v76[1].values[1]);var v38 = v76[1].values[2];var jsObjOrArr;if(taskValue == wffGlobal.taskValues.SET_BM_OBJ_ON_TAG){\n" + 
            "jsObjOrArr = new JsObjectFromBMBytes(v38, true);}else{\n" + 
            "jsObjOrArr = new JsArrayFromBMBytes(v38, true);}\n" + 
            "var wffObjects = tag['wffObjects'];if(typeof wffObjects === 'undefined'){\n" + 
            "wffObjects = {};tag['wffObjects'] = wffObjects;}\n" + 
            "wffObjects[ky] = jsObjOrArr;}else if(taskValue == wffGlobal.taskValues.DEL_BM_OBJ_OR_ARR_FROM_TAG){\n" + 
            "var tagName = wffTagUtil.f5(v76[1].name);var v98 = wffTagUtil\n" + 
            ".f23(v76[1].values[0]);var tag = wffTagUtil.f16(tagName, v98);var ky = f30(v76[1].values[1]);var wffObjects = tag['wffObjects'];if(typeof wffObjects !== 'undefined'){\n" + 
            "delete wffObjects[ky];}\n" + 
            "}\n" + 
            "return true;};this.f41 = function(v79){\n" + 
            "var v76 = wffBMUtil.f9(v79);var v46 = v76[0];if(v46.name[0] == wffGlobal.taskValues.TASK){\n" + 
            "f43(v76);}else if(v46.name[0] == wffGlobal.taskValues.TASK_OF_TASKS){\n" + 
            "var tasksBM = v46.values;for (var i = 0; i < tasksBM.length; i++){\n" + 
            "var v45 = wffBMUtil.f9(tasksBM[i]);f43(v45);}\n" + 
            "}else{\n" + 
            "return false;}\n" + 
            "return true;};this.f29 = function(v79){\n" + 
            "var v84 = wffBMUtil.f9(v79)[1];};};\n" + 
            "var wffTaskUtil = new function (){\n" + 
            "var encoder = wffGlobal.encoder;this.f34 = function(v93, valueByte){\n" + 
            "var v46 = {\n" + 
            "'name' : [v93],\n" + 
            "'values' : [ [valueByte] ]\n" + 
            "};return v46;};};var wffServerMethods = new function (){\n" + 
            "var encoder = wffGlobal.encoder;var f24 = function(v70){\n" + 
            "var v88 = [];if(typeof v70 === 'string'){\n" + 
            "var ndx = wffGlobal.NDXD_VNT_ATRBS.indexOf(v70);if(ndx != -1){\n" + 
            "var ndxByts = wffBMUtil.f15(ndx);v88.push(0);for (var i = 0; i < ndxByts.length; i++){\n" + 
            "v88.push(ndxByts[i]);}\n" + 
            "}else{\n" + 
            "v88 = encoder.encode(v70);}\n" + 
            "}else{\n" + 
            "var ndxByts = wffBMUtil.f15(v70);v88.push(0);for (var i = 0; i < ndxByts.length; i++){\n" + 
            "v88.push(ndxByts[i]);}\n" + 
            "}\n" + 
            "return v88;};var f40 = function(event, tag, v70, prvntDflt){\n" + 
            "if(prvntDflt){\n" + 
            "event.preventDefault();}\n" + 
            "var v46 = wffTaskUtil.f34(wffGlobal.taskValues.TASK, wffGlobal.taskValues.INVOKE_ASYNC_METHOD);var v88 = f24(v70);var v84 = {'name':wffTagUtil.f25(tag), 'values':[v88]};var v76 = [v46, v84];var wffBM = wffBMUtil.f13(v76);wffWS.send(wffBM);};this.b = function(event, tag, v70){\n" + 
            "f40(event, tag, v70, true);};var invokeAsync = function(event, tag, v70){\n" + 
            "f40(event, tag, v70, false);};this.a = invokeAsync;var f22 = function(event, tag, v70, preFun, prvntDflt){\n" + 
            "if(prvntDflt){\n" + 
            "event.preventDefault();}\n" + 
            "var invoked = false;var actionPerform = function(){\n" + 
            "invoked = true;var v46 = wffTaskUtil.f34(wffGlobal.taskValues.TASK, wffGlobal.taskValues.INVOKE_ASYNC_METHOD);var v88 = f24(v70);var v84 = {'name':wffTagUtil.f25(tag), 'values':[v88]};var v76 = [v46, v84];var wffBM = wffBMUtil.f13(v76);wffWS.send(wffBM);};var action = new function(){\n" + 
            "this.perform = function(){\n" + 
            "actionPerform();};};if(preFun(event, tag, action)){\n" + 
            "if(!invoked){\n" + 
            "actionPerform();}\n" + 
            "}\n" + 
            "};this.f = function(event, tag, v70, preFun){\n" + 
            "f22(event, tag, v70, preFun, true);};var invokeAsyncWithPreFun = function(event, tag, v70, preFun){\n" + 
            "f22(event, tag, v70, preFun, false);};this.e = invokeAsyncWithPreFun;var f6 = function(event, tag, v70, preFun, filterFun, prvntDflt){\n" + 
            "if(prvntDflt){\n" + 
            "event.preventDefault();}\n" + 
            "var invoked = false;var actionPerform = function(){\n" + 
            "invoked = true;var v46 = wffTaskUtil.f34(wffGlobal.taskValues.TASK, wffGlobal.taskValues.INVOKE_ASYNC_METHOD);var v88 = f24(v70);var jsObject = filterFun(event, tag);var v32 = new WffBMObject(jsObject);var v94 = v32.getBMBytes();var v84 = {'name':wffTagUtil.f25(tag), 'values':[v88, v94]};var v76 = [v46, v84];var wffBM = wffBMUtil.f13(v76);wffWS.send(wffBM);};var action = new function(){\n" + 
            "this.perform = function(){\n" + 
            "actionPerform();};};if(preFun(event, tag, action)){\n" + 
            "if(!invoked){\n" + 
            "actionPerform();}\n" + 
            "}\n" + 
            "};this.h = function(event, tag, v70, preFun, filterFun){\n" + 
            "f6(event, tag, v70, preFun, filterFun, true);};var invokeAsyncWithPreFilterFun = function(event, tag, v70, preFun, filterFun){\n" + 
            "f6(event, tag, v70, preFun, filterFun, false);};this.g = invokeAsyncWithPreFilterFun;var f10 = function(event, tag, v70, filterFun, prvntDflt){\n" + 
            "if(prvntDflt){\n" + 
            "event.preventDefault();}\n" + 
            "var v46 = wffTaskUtil.f34(wffGlobal.taskValues.TASK, wffGlobal.taskValues.INVOKE_ASYNC_METHOD);var v88 = f24(v70);var jsObject = filterFun(event, tag);var v32 = new WffBMObject(jsObject);var v94 = v32.getBMBytes();var v84 = {'name':wffTagUtil.f25(tag), 'values':[v88, v94]};var v76 = [v46, v84];var wffBM = wffBMUtil.f13(v76);wffWS.send(wffBM);};this.d = function(event, tag, v70, filterFun){\n" + 
            "f10(event, tag, v70, filterFun, true);};var invokeAsyncWithFilterFun = function(event, tag, v70, filterFun){\n" + 
            "f10(event, tag, v70, filterFun, false);};this.c = invokeAsyncWithFilterFun;};var wffSM = wffServerMethods;\n" + 
            "var wffClientMethods = new function(){\n" + 
            "var f30 = function(utf8Bytes){\n" + 
            "return wffGlobal.decoder.decode(new Uint8Array(utf8Bytes));};this.exePostFun = function(v79){\n" + 
            "var v76 = wffBMUtil.f9(v79);var v46 = v76[0];var taskValue = v46.values[0][0];if(v46.name[0] != wffGlobal.taskValues.TASK){\n" + 
            "return false;}\n" + 
            "if(taskValue == wffGlobal.taskValues.INVOKE_POST_FUNCTION){\n" + 
            "var funString = f30(v76[1].name);var values = v76[1].values;var jsObject = null;if(values.length > 0){\n" + 
            "var value = values[0];if(value.length > 0){\n" + 
            "var bmObjBytes = v76[1].values[0];jsObject = new JsObjectFromBMBytes(bmObjBytes, true);}\n" + 
            "}\n" + 
            "var func;if(window.execScript){\n" + 
            "func = window.execScript(\"(function(jsObject){\" + funString\n" + 
            "+ \"})\");}else{\n" + 
            "func = eval(\"(function(jsObject){\" + funString + \"})\");}\n" + 
            "func(jsObject);}else if(taskValue == wffGlobal.taskValues.INVOKE_CALLBACK_FUNCTION){\n" + 
            "var funKey = f30(v76[1].name);var cbFun = wffAsync.v27[funKey];var values = v76[1].values;var jsObject = null;if(values.length > 0){\n" + 
            "var value = values[0];if(value.length > 0){\n" + 
            "var bmObjBytes = v76[1].values[0];jsObject = new JsObjectFromBMBytes(bmObjBytes, true);}\n" + 
            "}\n" + 
            "cbFun(jsObject);delete wffAsync.v27[funKey];}else{\n" + 
            "return false;}\n" + 
            "return true;};};\n" + 
            "var f35 = function(valueType){\n" + 
            "if(valueType === '[object String]'){\n" + 
            "return 0;}else if(valueType === '[object Number]'){\n" + 
            "return 1;}else if(valueType === '[object Undefined]'){\n" + 
            "return 2;}else if(valueType === '[object Null]'){\n" + 
            "return 3;}else if(valueType === '[object Boolean]'){\n" + 
            "return 4;}else if(valueType === '[object Object]'){\n" + 
            "return 5;}else if(valueType === '[object Array]'){\n" + 
            "return 6;}else if(valueType === '[object RegExp]'){\n" + 
            "return 7;}else if(valueType === '[object Function]'){\n" + 
            "return 8;}else if(valueType === '[object Int8Array]'\n" + 
            "|| valueType == \"[object Uint8Array]\"){\n" + 
            "return 9;}\n" + 
            "return -1;};var WffBMArray = function(jsArray, outer){\n" + 
            "var encoder = wffGlobal.encoder;var decoder = wffGlobal.decoder;this.jsArray = jsArray;this.outer = outer;var getWffBMArray = function(jsArray, outer){\n" + 
            "var v76 = [];if(typeof outer === 'undefined' || outer){\n" + 
            "var typeNameValue = {\n" + 
            "name : [ 1 ],\n" + 
            "values : []\n" + 
            "};v76.push(typeNameValue);}\n" + 
            "if(jsArray.length > 0){\n" + 
            "var arrayValType;for (var i = 0; i < jsArray.length; i++){\n" + 
            "arrayValType = f35(Object.prototype.toString\n" + 
            ".call(jsArray[i]));if(arrayValType != 2 && arrayValType != 3){\n" + 
            "break;}\n" + 
            "}\n" + 
            "var v84 = {\n" + 
            "name : [ arrayValType ]\n" + 
            "};v76.push(v84);var values = [];v84.values = values;if(arrayValType == 0){\n" + 
            "for (var i = 0; i < jsArray.length; i++){\n" + 
            "if(arrayValType != 2 && arrayValType != 3){\n" + 
            "values.push(encoder.encode(jsArray[i]));}else{\n" + 
            "values.push([]);}\n" + 
            "}\n" + 
            "}else if(arrayValType == 1){\n" + 
            "for (var i = 0; i < jsArray.length; i++){\n" + 
            "if(arrayValType != 2 && arrayValType != 3){\n" + 
            "values.push(wffBMUtil.f32(jsArray[i]));}else{\n" + 
            "values.push([]);}\n" + 
            "}\n" + 
            "}  else if(arrayValType == 2\n" + 
            "|| arrayValType == 3){\n" + 
            "for (var i = 0; i < jsArray.length; i++){\n" + 
            "values.push([]);}\n" + 
            "}else if(arrayValType == 4){\n" + 
            "for (var i = 0; i < jsArray.length; i++){\n" + 
            "if(arrayValType != 2 && arrayValType != 3){\n" + 
            "if(jsArray[i]){\n" + 
            "values.push(true);}else{\n" + 
            "values.push(false);}\n" + 
            "}else{\n" + 
            "values.push([]);}\n" + 
            "}\n" + 
            "}else if(arrayValType == 5){\n" + 
            "for (var i = 0; i < jsArray.length; i++){\n" + 
            "values\n" + 
            ".push(new WffBMObject(jsArray[i], false)\n" + 
            ".getBMBytes());}\n" + 
            "}else if(arrayValType == 6){\n" + 
            "for (var i = 0; i < jsArray.length; i++){\n" + 
            "values.push(new WffBMArray(jsArray[i], false).getBMBytes());}\n" + 
            "}else if(arrayValType == 7 || arrayValType == 8){\n" + 
            "for (var i = 0; i < jsArray.length; i++){\n" + 
            "if(arrayValType != 2 && arrayValType != 3){\n" + 
            "values.push(encoder.encode(jsArray[i].toString()));}else{\n" + 
            "values.push([]);}\n" + 
            "}\n" + 
            "}else if(arrayValType == 9){\n" + 
            "for (var i = 0; i < jsArray.length; i++){\n" + 
            "if(typeof jsArray[i] === 'undefined' || jsArray[i] == null){\n" + 
            "values.push([]);}else{\n" + 
            "values.push(new WffBMByteArray(jsArray[i], false)\n" + 
            ".getBMBytes());}\n" + 
            "}\n" + 
            "}else{\n" + 
            "values.push([]);}\n" + 
            "}\n" + 
            "return wffBMUtil.f13(v76);};this.getBMBytes = function getBMBytes(){\n" + 
            "return getWffBMArray(this.jsArray, this.outer);};return this;};var WffBMByteArray = function(int8Array, outer){\n" + 
            "var encoder = wffGlobal.encoder;var decoder = wffGlobal.decoder;this.jsArray = int8Array;this.outer = outer;var getWffBMByteArray = function(int8Array, outer){\n" + 
            "var v76 = [];if(typeof outer === 'undefined' || outer){\n" + 
            "var typeNameValue = {\n" + 
            "name : [ 1 ],\n" + 
            "values : []\n" + 
            "};v76.push(typeNameValue);}\n" + 
            "var arrayValType = 10;var v84 = {\n" + 
            "name : [ arrayValType ]\n" + 
            "};v76.push(v84);v84.values = [ int8Array ];return wffBMUtil.f13(v76);};this.getBMBytes = function getBMBytes(){\n" + 
            "return getWffBMByteArray(this.jsArray, this.outer);};return this;};var WffBMObject = function(jsObject, outer){\n" + 
            "var encoder = wffGlobal.encoder;var decoder = wffGlobal.decoder;this.jsObject = jsObject;this.outer = outer;var getWffBMObject = function(jsObj, outer){\n" + 
            "var jsObjType = Object.prototype.toString.call(jsObj);var v76 = [];if(typeof outer === 'undefined' || outer){\n" + 
            "var typeNameValue = {\n" + 
            "name : [ 0 ],\n" + 
            "values : []\n" + 
            "};v76.push(typeNameValue);}\n" + 
            "for ( var k in jsObj){\n" + 
            "var value = jsObj[k];var valType = f35(Object.prototype.toString\n" + 
            ".call(value));var v84 = {};v84.name = encoder.encode(k);var values = [ [ valType ] ];v84.values = values;var typeByte = [ valType ];if(valType == 0){\n" + 
            "values.push(encoder.encode(value));}else if(valType == 1){\n" + 
            "values.push(wffBMUtil.f32(value));}else if(valType == 2){\n" + 
            "values.push([]);}else if(valType == 3){\n" + 
            "values.push([]);}else if(valType == 4){\n" + 
            "if(value){\n" + 
            "values.push([ 1 ]);}else{\n" + 
            "values.push([ 0 ]);}\n" + 
            "}else if(valType == 5){\n" + 
            "values.push(new WffBMObject(value, false).getBMBytes());}else if(valType == 6){\n" + 
            "if(value.length == 0){\n" + 
            "values.push([]);}else{\n" + 
            "values.push(new WffBMArray(value, false).getBMBytes());}\n" + 
            "}else if(valType == 7 || valType == 8){\n" + 
            "values.push(encoder.encode(value.toString()));}else if(valType == 9){\n" + 
            "values.push(new WffBMByteArray(value, false).getBMBytes());}else{\n" + 
            "values.push([]);}\n" + 
            "v76.push(v84);}\n" + 
            "return wffBMUtil.f13(v76);};this.getBMBytes = function getBMBytes(){\n" + 
            "return getWffBMObject(this.jsObject, this.outer);};};var JsObjectFromBMBytes = function(v79, outer){\n" + 
            "var encoder = wffGlobal.encoder;var decoder = wffGlobal.decoder;var f30 = function(utf8Bytes){\n" + 
            "return wffGlobal.decoder.decode(new Uint8Array(utf8Bytes));};var v76 = wffBMUtil.f9(v79);var i = 0;if(typeof outer === 'undefined' || outer){\n" + 
            "i = 1;}\n" + 
            "for (; i < v76.length; i++){\n" + 
            "var v84 = v76[i];var name = f30(v84.name);var values = v84.values;if(values[0] == 0){\n" + 
            "this[name] = f30(values[1]);}else if(values[0] == 1){\n" + 
            "this[name] = wffBMUtil.f8(values[1]);}else if(values[0] == 2){\n" + 
            "this[name] = undefined;}else if(values[0] == 3){\n" + 
            "this[name] = null;}else if(values[0] == 4){\n" + 
            "this[name] = values[1] == 1 ? true : false;}else if(values[0] == 5){\n" + 
            "this[name] = new JsObjectFromBMBytes(values[1], false);}else if(values[0] == 6){\n" + 
            "this[name] = new JsArrayFromBMBytes(values[1], false);}else if(values[0] == 7){\n" + 
            "this[name] = new RegExp(f30(values[1]));}else if(values[0] == 8){\n" + 
            "if(window.execScript){\n" + 
            "this[name] = window.execScript(\"(\"\n" + 
            "+ f30(values[1]) + \")\");}else{\n" + 
            "this[name] = eval(\"(\" + f30(values[1]) + \")\");}\n" + 
            "}else if(values[0] == 9){\n" + 
            "this[name] = new JsArrayFromBMBytes(values[1], false);}else if(values[0] == 10){\n" + 
            "this[name] = new Int8Array(values[1]);}\n" + 
            "}\n" + 
            "return this;};var JsArrayFromBMBytes = function(v79, outer){\n" + 
            "var encoder = wffGlobal.encoder;var decoder = wffGlobal.decoder;var f30 = function(utf8Bytes){\n" + 
            "return decoder.decode(new Uint8Array(utf8Bytes));};var v76 = wffBMUtil.f9(v79);var i = 0;if(typeof outer === 'undefined' || outer){\n" + 
            "i = 1;}\n" + 
            "var v84 = v76[i];var dataType = v84.name[0];var values = v84.values;var jsArray = [];if(dataType == 0){\n" + 
            "for (var j = 0; j < values.length; j++){\n" + 
            "jsArray.push(f30(values[j]));}\n" + 
            "}else if(dataType == 1){\n" + 
            "for (var j = 0; j < values.length; j++){\n" + 
            "jsArray.push(wffBMUtil.f8(values[j]));}\n" + 
            "}else if(dataType == 2){\n" + 
            "for (var j = 0; j < values.length; j++){\n" + 
            "jsArray.push(undefined);}\n" + 
            "}else if(dataType == 3){\n" + 
            "for (var j = 0; j < values.length; j++){\n" + 
            "jsArray.push(null);}\n" + 
            "}else if(dataType == 4){\n" + 
            "for (var j = 0; j < values.length; j++){\n" + 
            "jsArray.push(values[j] == 1 ? true : false);}\n" + 
            "}else if(dataType == 5){\n" + 
            "for (var j = 0; j < values.length; j++){\n" + 
            "jsArray.push(new JsObjectFromBMBytes(values[j], false));}\n" + 
            "}else if(dataType == 6){\n" + 
            "for (var j = 0; j < values.length; j++){\n" + 
            "jsArray.push(new JsArrayFromBMBytes(values[j], false));}\n" + 
            "}else if(dataType == 7){\n" + 
            "for (var j = 0; j < values.length; j++){\n" + 
            "jsArray.push(new RegExp(f30(values[j])));}\n" + 
            "}else if(dataType == 8){\n" + 
            "for (var j = 0; j < values.length; j++){\n" + 
            "var fun = f30(values[j]);var ary;if(window.execScript){\n" + 
            "ary = [ window.execScript(\"(\" + fun + \")\") ];}else{\n" + 
            "ary = [ eval(\"(\" + fun + \")\") ];}\n" + 
            "jsArray.push(ary[0]);}\n" + 
            "}else if(dataType == 9){\n" + 
            "if(values.length == 1){\n" + 
            "jsArray = new JsArrayFromBMBytes(values[0], false);}else{\n" + 
            "for (var j = 0; j < values.length; j++){\n" + 
            "jsArray.push(new JsArrayFromBMBytes(values[j], false));}\n" + 
            "}\n" + 
            "}  else if(dataType == 10){\n" + 
            "if(values.length == 1){\n" + 
            "jsArray = new Int8Array(values[0]);}else{\n" + 
            "for (var j = 0; j < values.length; j++){\n" + 
            "jsArray.push(new Int8Array(values[j]));}\n" + 
            "}\n" + 
            "}\n" + 
            "return jsArray;};\n" + 
            "window.wffAsync = new function(){\n" + 
            "var encoder = wffGlobal.encoder;this.v27 = {};var uuid = 0;this.generateUUID = function(){\n" + 
            "return (++uuid).toString();};this.serverMethod = function(v81, jsObject){\n" + 
            "this.invoke = function(callback){\n" + 
            "var v47;if(typeof callback === \"function\"){\n" + 
            "v47 = wffAsync.generateUUID();wffAsync.v27[v47] = callback;}else if(typeof callback === \"undefined\"){\n" + 
            "}else{\n" + 
            "throw \"invoke function takes function argument\";}\n" + 
            "var v46 = wffTaskUtil.f34(\n" + 
            "wffGlobal.taskValues.TASK,\n" + 
            "wffGlobal.taskValues.INVOKE_CUSTOM_SERVER_METHOD);var v36 = encoder.encode(v81);var values = [];if(typeof jsObject !== \"undefined\"){\n" + 
            "if(typeof jsObject === \"object\"){\n" + 
            "var v32 = new WffBMObject(jsObject);var v94 = v32.getBMBytes();values.push(v94);}else{\n" + 
            "throw \"argument value should be an object\";}\n" + 
            "}\n" + 
            "var v84 = {\n" + 
            "'name' : v36,\n" + 
            "'values' : values\n" + 
            "};var v76 = [ v46, v84 ];if(typeof v47 !== \"undefined\"){\n" + 
            "var v15 = {\n" + 
            "'name' : encoder.encode(v47),\n" + 
            "'values' : []\n" + 
            "};v76.push(v15);}\n" + 
            "var wffBM = wffBMUtil.f13(v76);wffWS.send(wffBM);};return this;};};\n" + 
            "var wffBMClientEvents = new function(){\n" + 
            "window.v1 = false;var encoder = wffGlobal.encoder;var decoder = wffGlobal.decoder;var f27 = function(wffInstanceId){\n" + 
            "var v76 = [];var v23 = encoder.encode(wffInstanceId);var tnv = wffTaskUtil.f34(\n" + 
            "wffGlobal.taskValues.TASK,\n" + 
            "wffGlobal.taskValues.REMOVE_BROWSER_PAGE);v76.push(tnv);var v84 = {\n" + 
            "'name' : v23,\n" + 
            "'values' : []\n" + 
            "};v76.push(v84);var wffBM = wffBMUtil.f13(v76);wffWS.send(wffBM);};this.f27 = f27;this.f20 = function(){\n" + 
            "if(!wffGlobal.REMOVE_PREV_BP_ON_INITTAB ||\n" + 
            "window.v1){\n" + 
            "return;}\n" + 
            "window.v1 = true;var wffInstanceId = sessionStorage.getItem('WFF_INSTANCE_ID');if(typeof wffInstanceId !== \"undefined\"\n" + 
            "&& wffInstanceId !== wffGlobal.INSTANCE_ID){\n" + 
            "f27(wffInstanceId);}\n" + 
            "sessionStorage.setItem('WFF_INSTANCE_ID', wffGlobal.INSTANCE_ID);};};\n" + 
            "var wffWS = new function(){\n" + 
            "var encoder = wffGlobal.encoder;var decoder = wffGlobal.decoder;var prevIntvl = null;var webSocket = null;var inDataQ = [];var sendQData = null;this.openSocket = function(wsUrl){\n" + 
            "if(webSocket !== null\n" + 
            "&& webSocket.readyState !== WebSocket.CLOSED\n" + 
            "&& webSocket.readyState !== WebSocket.CLOSING){\n" + 
            "return;}\n" + 
            "webSocket = new WebSocket(wsUrl);sendQData = function(){\n" + 
            "if(webSocket !== null && webSocket.readyState === WebSocket.OPEN){\n" + 
            "var inData = [];var ndx = 0;var xp = false;for (ndx = 0; ndx < inDataQ.length; ndx++){\n" + 
            "var each = inDataQ[ndx];if(!xp){\n" + 
            "try {\n" + 
            "webSocket.send(new Int8Array(each).buffer);} catch(e){\n" + 
            "xp = true;inData.push(each);}\n" + 
            "}else{\n" + 
            "inData.push(each);}\n" + 
            "}\n" + 
            "inDataQ = inData;}\n" + 
            "};webSocket.binaryType = 'arraybuffer';webSocket.onopen = function(event){\n" + 
            "try {\n" + 
            "if(prevIntvl !== null){\n" + 
            "clearInterval(prevIntvl);prevIntvl = null;}\n" + 
            "wffBMClientEvents.f20();if(sendQData !== null){\n" + 
            "sendQData();}\n" + 
            "if(typeof event.data === 'undefined'){\n" + 
            "return;}\n" + 
            "var binary = new Int8Array(event.data);if(binary.length < 4){\n" + 
            "return;}\n" + 
            "wffClientCRUDUtil.f41(binary);}catch(e){\n" + 
            "wffLog(e);}\n" + 
            "};webSocket.onmessage = function(event){\n" + 
            "try {\n" + 
            "var binary = new Int8Array(event.data);if(binary.length < 4){\n" + 
            "return;}\n" + 
            "var executed = wffClientMethods.exePostFun(binary);if(!executed){\n" + 
            "wffClientCRUDUtil.f41(binary);}\n" + 
            "}catch(e){\n" + 
            "wffLog(e);}\n" + 
            "};webSocket.onclose = function(event){\n" + 
            "if(prevIntvl !== null){\n" + 
            "clearInterval(prevIntvl);prevIntvl = null;}\n" + 
            "prevIntvl = setInterval(function(){\n" + 
            "if(webSocket === null || webSocket.readyState === WebSocket.CLOSED){\n" + 
            "wffWS.openSocket(wffGlobal.WS_URL);}\n" + 
            "}, wffGlobal.WS_RECON);};webSocket.onerror = function(event){\n" + 
            "try{webSocket.close();}catch(e){wffLog(\"ws.close error\");}\n" + 
            "};};\n" + 
            "this.send = function(bytes){\n" + 
            "if(bytes.length > 0){\n" + 
            "inDataQ.push(bytes);if(sendQData !== null){\n" + 
            "sendQData();}\n" + 
            "}else{\n" + 
            "webSocket.send(new Int8Array(bytes).buffer);}\n" + 
            "};this.closeSocket = function(){\n" + 
            "try {\n" + 
            "if(webSocket !== null\n" + 
            "&& webSocket.readyState !== WebSocket.CONNECTING\n" + 
            "&& webSocket.readyState !== WebSocket.CLOSED){\n" + 
            "webSocket.close();}\n" + 
            "} catch(e){}\n" + 
            "};this.getState = function(){\n" + 
            "if(webSocket !== null){\n" + 
            "return webSocket.readyState;}\n" + 
            "return -1;};};\n" + 
            "document.addEventListener(\"DOMContentLoaded\",\n" + 
            "function(event){\n" + 
            "wffWS.openSocket(wffGlobal.WS_URL);window.v30 = false;var f36 = function(){\n" + 
            "if(!window.v30){\n" + 
            "wffBMClientEvents.f27(sessionStorage.getItem('WFF_INSTANCE_ID'));}\n" + 
            "window.v30 = true;};var f11 = function (eventName){\n" + 
            "var el = window;eventName = 'on' + eventName;var isSupported = (eventName in el);if(!isSupported && typeof InstallTrigger !== 'undefined'){\n" + 
            "el.setAttribute(eventName, 'return;');isSupported = typeof el[eventName] == 'function';}\n" + 
            "el = null;return isSupported;};if(f11('beforeunload')){\n" + 
            "window.addEventListener(\"beforeunload\", f36, false);}\n" + 
            "if(f11('unload')){\n" + 
            "window.addEventListener(\"unload\", f36, false);}\n" + 
            "MutationObserver = window.MutationObserver\n" + 
            "|| window.WebKitMutationObserver;var attrObserver = new MutationObserver(function(mutations,\n" + 
            "observer){\n" + 
            "var v56s = {};for (var i = 0; i < mutations.length; i++){\n" + 
            "var mutation = mutations[i];v56s[mutation.attributeName] = mutation.target\n" + 
            ".getAttribute(mutation.attributeName);}\n" + 
            "});attrObserver.observe(document, {\n" + 
            "subtree : true,\n" + 
            "attributes : true\n" + 
            "});var tagObserver = new MutationObserver(\n" + 
            "function(mutations, observer){\n" + 
            "for (var i = 0; i < mutations.length; i++){\n" + 
            "var mutation = mutations[i];var addedNodes = mutation.addedNodes;for (var j = 0; j < addedNodes.length; j++){\n" + 
            "var addedNode = addedNodes[j];}\n" + 
            "}\n" + 
            "});tagObserver.observe(document, {\n" + 
            "childList : true,\n" + 
            "subtree : true\n" + 
            "});});setInterval(function(){try{wffWS.send([]);}catch(e){wffWS.closeSocket();}},1000);";

    @Test
    public void testGetAllOptimizedContent() {
        String allContent = WffJsFile.getAllOptimizedContent("ws://webfirmframework.com", "instance-id-1234585-451", true, true, 1000, 2000, false);
        assertEquals(expectedContent, allContent);
    }

}
