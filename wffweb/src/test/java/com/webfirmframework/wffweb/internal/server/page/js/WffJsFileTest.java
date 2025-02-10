/*
 * Copyright since 2014 Web Firm Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.webfirmframework.wffweb.internal.server.page.js;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class WffJsFileTest {

    private static String expectedContent = """
            var wffLog = console.log;if (!Array.from) {
            Array.from = (function() {
            var toStr = Object.prototype.toString;var isCallable = function(fn) {
            return typeof fn === 'function'
            || toStr.call(fn) === '[object Function]';};var toInteger = function(value) {
            var number = Number(value);if (isNaN(number)) {
            return 0;}
            if (number === 0 || !isFinite(number)) {
            return number;}
            return (number > 0 ? 1 : -1) * Math.floor(Math.abs(number));};var maxSafeInteger = Math.pow(2, 53) - 1;var toLength = function(value) {
            var len = toInteger(value);return Math.min(Math.max(len, 0), maxSafeInteger);};return function from(arrayLike) {
            var C = this;var items = Object(arrayLike);if (arrayLike == null) {
            throw new TypeError(
            "Array.from requires an array-like object - not null or undefined");}
            var mapFn = arguments.length > 1 ? arguments[1] : void undefined;var T;if (typeof mapFn !== 'undefined') {
            if (!isCallable(mapFn)) {
            throw new TypeError(
            'Array.from: when provided, the second argument must be a function');}
            if (arguments.length > 2) {
            T = arguments[2];}
            }
            var len = toLength(items.length);var A = isCallable(C) ? Object(new C(len)) : new Array(len);var k = 0;var kValue;while (k < len) {
            kValue = items[k];if (mapFn) {
            A[k] = typeof T === 'undefined' ? mapFn(kValue, k) : mapFn
            .call(T, kValue, k);} else {
            A[k] = kValue;}
            k += 1;}
            A.length = len;return A;};}());}window.wffGlobal = new function() {
            var wffId = -1;var serverPIdGen = 0;var clientPIdGen = 0;var MAX_INT_VALUE = 2147483647;var onPLI = false;var cLoc = undefined;this.getUniqueWffIntId = function() {
            return ++wffId;};this.getUniqueServerSidePayloadId = function() {
            var id = ++serverPIdGen;if (id == 0) {
            id = ++serverPIdGen;} else if (id > MAX_INT_VALUE) {
            id = id * -1;}
            return id;};this.getUniqueClientSidePayloadId = function() {
            var id = ++clientPIdGen;if (id == 0) {
            id = ++serverPIdGen;} else if (id > MAX_INT_VALUE) {
            id = id * -1;}
            return id;};this.getAndUpdateLocation = function() {
            var prevCLoc = cLoc;var l = window.location;var h = l.href.endsWith('#') ? '#' : l.hash;cLoc = {pathname: l.pathname, search: l.search, hash: h};return prevCLoc;};this.CPRSD_DATA = true;this.NDXD_TGS = ["#","$","@","a","b","i","p","q","s","u","br","dd","dl","dt","em","h1","h2","h3","h4","h5","h6","hr","li","ol","rp","rt","td","th","tr","ul","bdi","bdo","col","del","dfn","div","img","ins","kbd","map","nav","pre","qfn","sub","sup","svg","var","wbr","abbr","area","base","body","cite","code","data","form","head","html","line","link","main","mark","math","menu","meta","path","rect","ruby","samp","span","text","time","aside","audio","embed","input","label","meter","param","small","style","table","tbody","tfoot","thead","title","track","video","button","canvas","circle","dialog","figure","footer","header","hgroup","iframe","keygen","legend","object","option","output","script","select","source","strong","address","article","caption","details","ellipse","picture","polygon","section","summary","basefont","colgroup","datalist","fieldset","menuitem","noscript","optgroup","polyline","progress","template","textarea","blockquote","figcaption"];this.NDXD_ATRBS = ["id","alt","dir","for","low","max","min","rel","rev","src","cols","face","form","high","href","lang","list","loop","name","open","role","rows","size","step","type","wrap","align","async","class","color","defer","ismap","media","muted","nonce","oncut","scope","shape","sizes","style","title","value","width","accept","action","border","coords","height","hidden","method","nohref","onblur","oncopy","ondrag","ondrop","onload","onplay","onshow","poster","sorted","srcset","target","usemap","charset","checked","colspan","content","default","dirname","enctype","headers","onabort","onclick","onended","onerror","onfocus","oninput","onkeyup","onpaste","onpause","onreset","onwheel","optimum","pattern","preload","rowspan","sandbox","autoplay","controls","datetime","disabled","download","dropzone","hreflang","multiple","onchange","ononline","onresize","onscroll","onsearch","onseeked","onselect","onsubmit","ontoggle","onunload","readonly","required","reversed","selected","tabindex","accesskey","autofocus","draggable","maxlength","minlength","oncanplay","ondragend","onemptied","onfocusin","oninvalid","onkeydown","onmouseup","onoffline","onplaying","onseeking","onstalled","onstorage","onsuspend","onwaiting","translate","formaction","formmethod","formtarget","http-equiv","ondblclick","ondragover","onfocusout","onkeypress","onmouseout","onpagehide","onpageshow","onpopstate","onprogress","ontouchend","spellcheck","cellpadding","cellspacing","contextmenu","data-wff-id","formenctype","ondragenter","ondragleave","ondragstart","onloadstart","onmousedown","onmousemove","onmouseover","ontouchmove","placeholder","animationend","autocomplete","onafterprint","onhashchange","onloadeddata","onmouseenter","onmouseleave","onratechange","ontimeupdate","ontouchstart","onbeforeprint","oncontextmenu","ontouchcancel","transitionend","accept-charset","animationstart","formnovalidate","onbeforeunload","onvolumechange","contenteditable","oncanplaythrough","ondurationchange","onloadedmetadata","animationiteration"];this.NDXD_VNT_ATRBS = ["oncut","onblur","oncopy","ondrag","ondrop","onload","onplay","onshow","onabort","onclick","onended","onerror","onfocus","oninput","onkeyup","onpaste","onpause","onreset","onwheel","onchange","ononline","onresize","onscroll","onsearch","onseeked","onselect","onsubmit","ontoggle","onunload","oncanplay","ondragend","onemptied","onfocusin","oninvalid","onkeydown","onmouseup","onoffline","onplaying","onseeking","onstalled","onstorage","onsuspend","onwaiting","ondblclick","ondragover","onfocusout","onkeypress","onmouseout","onpagehide","onpageshow","onpopstate","onprogress","ontouchend","ondragenter","ondragleave","ondragstart","onloadstart","onmousedown","onmousemove","onmouseover","ontouchmove","onafterprint","onhashchange","onloadeddata","onmouseenter","onmouseleave","onratechange","ontimeupdate","ontouchstart","onbeforeprint","oncontextmenu","ontouchcancel","onbeforeunload","onvolumechange","oncanplaythrough","ondurationchange","onloadedmetadata"];this.NDXD_BLN_ATRBS = ["open","async","defer","ismap","hidden","checked","default","controls","disabled","multiple","readonly","reversed","selected"];this.WFF_ID_PFXS = ["S","C"];this.taskValues = {INITIAL_WS_OPEN:0,INVOKE_ASYNC_METHOD:1,ATTRIBUTE_UPDATED:2,TASK:3,APPENDED_CHILD_TAG:4,REMOVED_TAGS:5,APPENDED_CHILDREN_TAGS:6,REMOVED_ALL_CHILDREN_TAGS:7,MOVED_CHILDREN_TAGS:8,INSERTED_BEFORE_TAG:9,INSERTED_AFTER_TAG:10,REPLACED_WITH_TAGS:11,REMOVED_ATTRIBUTES:12,ADDED_ATTRIBUTES:13,MANY_TO_ONE:14,ONE_TO_MANY:15,MANY_TO_MANY:16,ONE_TO_ONE:17,ADDED_INNER_HTML:18,INVOKE_POST_FUNCTION:19,EXEC_JS:20,RELOAD_BROWSER:21,RELOAD_BROWSER_FROM_CACHE:22,INVOKE_CALLBACK_FUNCTION:23,INVOKE_CUSTOM_SERVER_METHOD:24,TASK_OF_TASKS:25,COPY_INNER_TEXT_TO_VALUE:26,REMOVE_BROWSER_PAGE:27,SET_BM_OBJ_ON_TAG:28,SET_BM_ARR_ON_TAG:29,DEL_BM_OBJ_OR_ARR_FROM_TAG:30,CLIENT_PATHNAME_CHANGED:31,AFTER_SET_URI:32,SET_URI:33,SET_LS_ITEM:34,SET_LS_TOKEN:35,GET_LS_ITEM:36,REMOVE_LS_ITEM:37,REMOVE_LS_TOKEN:38,REMOVE_AND_GET_LS_ITEM:39,CLEAR_LS:40};this.uriEventInitiator = {SERVER_CODE:0,CLIENT_CODE:1,BROWSER:2,size:3};this.WS_URL = "ws://webfirmframework.com";this.INSTANCE_ID = "instance-id-1234585-451";this.NODE_ID = "5b9e5768-3596-4efa-849f-eec3fc317980";this.REMOVE_PREV_BP_ON_INITTAB = true;this.REMOVE_PREV_BP_ON_TABCLOSE = true;this.WS_RECON = 2000;this.WS_HRTBT = 1000;this.LOSSLESS_COMM = true;this.onPayloadLoss = function() {if (!this.onPLI) { this.onPLI = true; location.reload();}};if ((typeof TextEncoder) === "undefined") {
            this.encoder = new function TextEncoder(charset) {
            if (charset === "utf-8") {
            this.encode = function(text) {
            var bytes = [];for (var i = 0; i < text.length; i++) {
            var charCode = text.charCodeAt(i);if (charCode < 0x80) {
            bytes.push(charCode);} else if (charCode < 0x800) {
            bytes.push(0xc0 | (charCode >> 6),
            0x80 | (charCode & 0x3f));} else if (charCode < 0xd800 || charCode >= 0xe000) {
            bytes.push(0xe0 | (charCode >> 12),
            0x80 | ((charCode >> 6) & 0x3f),
            0x80 | (charCode & 0x3f));} else {
            i++;charCode = 0x10000 + (((charCode & 0x3ff) << 10) | (text
            .charCodeAt(i) & 0x3ff));bytes.push(0xf0 | (charCode >> 18),
            0x80 | ((charCode >> 12) & 0x3f),
            0x80 | ((charCode >> 6) & 0x3f),
            0x80 | (charCode & 0x3f));}
            }
            return bytes;};}
            }("utf-8");} else {
            this.encoder = new TextEncoder("utf-8");}
            if ((typeof TextDecoder) === "undefined") {
            this.decoder = new function TextDecoder(charset) {
            if (charset === "utf-8") {
            this.decode = function(bytes) {
            var text = '', i;for (i = 0; i < bytes.length; i++) {
            var value = bytes[i];if (value < 0x80) {
            text += String.fromCharCode(value);} else if (value > 0xBF && value < 0xE0) {
            text += String.fromCharCode((value & 0x1F) << 6
            | bytes[i + 1] & 0x3F);i += 1;} else if (value > 0xDF && value < 0xF0) {
            text += String.fromCharCode((value & 0x0F) << 12
            | (bytes[i + 1] & 0x3F) << 6 | bytes[i + 2]
            & 0x3F);i += 2;} else {
            var charCode = ((value & 0x07) << 18
            | (bytes[i + 1] & 0x3F) << 12
            | (bytes[i + 2] & 0x3F) << 6 | bytes[i + 3] & 0x3F) - 0x010000;text += String.fromCharCode(
            charCode >> 10 | 0xD800,
            charCode & 0x03FF | 0xDC00);i += 3;}
            }
            return text;};}
            }("utf-8");} else {
            this.decoder = new TextDecoder("utf-8");}
            };var wffBMUtil = new function(){

            this.f15 = function(v77){
            var v34 = 0;var v22 = 0;for (var i = 0; i < v77.length; i++){
            var name = v77[i].name;var values = v77[i].values;if(name.length > v34){
            v34 = name.length;}
            var v18 = 0;for (var j = 0; j < values.length; j++){
            v18 += values[j].length;}
            var v2 = f3(v18);var v4 = values.length
            * v2;var v3 = v18
            + v4;if(v3 > v22){
            v22 = v3;}
            }
            var v14 = f3(v34);var v12 = f3(v22);var v11 = [];v11.push(v14);v11.push(v12);for (var i = 0; i < v77.length; i++){
            var name = v77[i].name;var v44 = f30(name.length,
            v14);f35(v11, v44);f35(v11, name);var values = v77[i].values;if(values.length == 0){
            f35(v11, f30(0,
            v12));}else{
            var v73 = 0;for (var l = 0; l < values.length; l++){
            v73 += values[l].length;}
            v73 += (v12 * values.length);var v37 = f30(v73,
            v12);f35(v11, v37);for (var m = 0; m < values.length; m++){
            var value = values[m];v37 = f30(value.length,
            v12);f35(v11, v37);f35(v11, value);}
            }
            }
            return v11;};this.f11 = function(message){
            var v77 = [];var v10 = message[0];var v8 = message[1];for (var v58 = 2; v58 < message.length; v58++){
            var v85 = {};var v39 = subarray(message,
            v58, v10);v58 = v58 + v10;var v49 = f16(v39);var v86 = subarray(message, v58,
            v49);v58 = v58 + v86.length;v85.name = v86;var v35 = subarray(message,
            v58, v8);v58 = v58 + v8;v49 = f16(v35);var v78 = subarray(message, v58,
            v49);v58 = v58 + v78.length - 1;var v9 = f8(
            v8, v78);v85.values = v9;v77.push(v85);}
            return v77;};
            var f8 = function(v8, v78){
            var values = [];for (var i = 0; i < v78.length; i++){
            var v35 = subarray(v78, i,
            v8);var v67 = f16(v35);var value = subarray(v78, i
            + v8, v67);values.push(value);i = i + v8 + v67 - 1;}
            return values;};
            var f35 = function(v53, v41){
            for (var a = 0; a < v41.length; a++){
            v53.push(v41[a]);}
            };
            var f5 = function(v53, v79,
            position, length){
            var uptoIndex = position + length;for (var a = position; a < uptoIndex; a++){
            v53.push(v79[a]);}
            };this.f5 = f5;var subarray = function(srcAry, pos, len){
            var upto = pos + len;if(srcAry.slice){
            return srcAry.slice(pos, upto);}
            var sub = [];for (var i = pos; i < upto; i++){
            sub.push(srcAry[i]);}
            return sub;};this.subarray = subarray;
            var f40 = function(bytes){
            return bytes[0] << 24 | (bytes[1] & 0xFF) << 16
            | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);};
            var f41 = function(value){
            var bytes = [ (value >> 24), (value >> 16), (value >> 8), value ];return bytes;};this.f41 = f41;
            var f16 = function(bytes){
            if(bytes.length == 4){
            return bytes[0] << 24 | (bytes[1] & 0xFF) << 16
            | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);}else if(bytes.length == 3){
            return (bytes[0] & 0xFF) << 16 | (bytes[1] & 0xFF) << 8
            | (bytes[2] & 0xFF);}else if(bytes.length == 2){
            return (bytes[0] & 0xFF) << 8 | (bytes[1] & 0xFF);}else if(bytes.length == 1){
            return (bytes[0] & 0xFF);}
            return bytes[0] << 24 | (bytes[1] & 0xFF) << 16
            | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);};this.f16 = f16;
            var f17 = function(value){
            var v66 = (value >> 24);var v81 = (value >> 16);var v68 = (value >> 8);var v72 = value;if(v66 == 0){
            if(v81 == 0){
            if(v68 == 0){
            return [ v72 ];}
            return [ v68, v72 ];}
            return [ v81, v68, v72 ];}
            return [ v66, v81, v68, v72 ];};this.f17 = f17;
            var f3 = function(value){
            var v66 = (value >> 24);var v81 = (value >> 16);var v68 = (value >> 8);var v72 = value;if(v66 == 0){
            if(v81 == 0){
            if(v68 == 0){
            return 1;}
            return 2;}
            return 3;}
            return 4;};
            var f30 = function(value, v54){
            var v66 = (value >> 24);var v81 = (value >> 16);var v68 = (value >> 8);var v72 = value;if(v54 == 1){
            return [ v72 ];}else if(v54 == 2){
            return [ v68, v72 ];}else if(v54 == 3){
            return [ v81, v68, v72 ];}
            return [ v66, v81, v68, v72 ];};
            var f34 = function(v63){
            var arrayBuff = new ArrayBuffer(8);var float64 = new Float64Array(arrayBuff);float64[0] = v63;var uin = new Int8Array(arrayBuff);return Array.from(uin).reverse();};this.f34 = f34;
            var f9 = function(bytes){
            var buffer = new ArrayBuffer(8);var int8Array = new Int8Array(buffer);for (var i = 0; i < bytes.length; i++){
            int8Array[i] = bytes[bytes.length - 1 - i];}
            return new Float64Array(buffer)[0];};this.f9 = f9;};
            var wffTagUtil = new function(){
            var encoder = wffGlobal.encoder;var decoder = wffGlobal.decoder;var f32 = function(utf8Bytes){
            return decoder.decode(new Uint8Array(utf8Bytes));};var subarray = wffBMUtil.subarray;var f6 = function(utf8Bytes){
            if(utf8Bytes.length == 1){
            var v97 = wffBMUtil.f16(utf8Bytes);return wffGlobal.NDXD_TGS[v97];}
            var v7 = utf8Bytes[0];if(v7 > 0){
            var v26 = subarray(utf8Bytes, 1, v7);var v97 = wffBMUtil.f16(v26);return wffGlobal.NDXD_TGS[v97];}else{
            var v61 = utf8Bytes.length - 1;var v60 = subarray(utf8Bytes, 1, v61);return f32(v60);}
            };this.f6 = f6;var f4 = function(utf8Bytes){
            if(utf8Bytes.length == 1){
            var v83 = wffBMUtil.f16(utf8Bytes);return wffGlobal.NDXD_ATRBS[v83];}
            var v5 = utf8Bytes[0];if(v5 > 0){
            var v19 = subarray(utf8Bytes, 1, v5);var v83 = wffBMUtil.f16(v19);return wffGlobal.NDXD_ATRBS[v83];}else{
            var v61 = utf8Bytes.length - 1;var v93Bytes = subarray(utf8Bytes, 1, v61);return f32(v93Bytes);}
            };this.f4 = f4;var f1 = function(utf8Bytes){
            var v5 = utf8Bytes[0];if(v5 > 0 || (v5 < 0 && v5 > -5)){
            var ng = v5 < 0;var v5 = ng ? Math.abs(v5) : v5;var v19 = subarray(utf8Bytes, 1, v5);var v83 = wffBMUtil.f16(v19);var v84 = utf8Bytes.length - (v5 + 1);var v69 = subarray(utf8Bytes, v5 + 1, v84);var v88 = ng ? wffBMUtil.f16(v69).toString() : f32(v69);return [wffGlobal.NDXD_ATRBS[v83], v88];}else if(v5 == 0){
            var v61 = utf8Bytes.length - 1;var v70 = subarray(utf8Bytes, 1, v61);return f33(f32(v70));}else{
            v5 = Math.abs(v5);var ndx = v5 - 5;if(ndx >= wffGlobal.WFF_ID_PFXS.length){
            wffLog("Error: prefix not indexed for", ndx);return null;}
            var pfx = wffGlobal.WFF_ID_PFXS[ndx];return ["data-wff-id", pfx + wffBMUtil.f16(subarray(utf8Bytes, 1, utf8Bytes.length)).toString()];}
            };this.f1 = f1;var f28 = function(tag, htmlString){
            var tmpDv = document.createElement('div');tmpDv.innerHTML = htmlString;for (var i = 0; i < tmpDv.childNodes.length; i++){
            var cn = tmpDv.childNodes[i];if(cn.nodeName === '#text'){
            tag.appendChild(document.createTextNode(cn.nodeValue));}else{
            tag.appendChild(cn.cloneNode(true));}
            }
            };this.f18 = function(tagName, v101){
            var elements = document.querySelectorAll(tagName + '[data-wff-id="'
            + v101 + '"]');if(elements.length > 1){
            wffLog('multiple tags with same wff id', 'tagName', 'v101', v101);}
            return elements[0];};this.f42 = function(v101){
            var elements = document.querySelectorAll('[data-wff-id="' + v101
            + '"]');if(elements.length > 1){
            wffLog('multiple tags with same wff id', 'v101', v101);}
            return elements[0];};
            this.f25 = function(v76){
            var v103 = f32([ v76[0] ]);var intBytes = [];for (var i = 1; i < v76.length; i++){
            intBytes.push(v76[i]);}
            var v102 = wffBMUtil.f16(intBytes);return v103 + v102;};this.f27 = function(tag){
            var v88 = tag.getAttribute("data-wff-id");if(v88 == null){
            v88 = "C" + wffGlobal.getUniqueWffIntId();tag.setAttribute("data-wff-id", v88);}
            var v103 = v88.substring(0, 1);var v52 = encoder.encode(v103);var v76 = [ v52[0] ];var v102 = v88.substring(1, v88.length);var v75 = wffBMUtil.f17(parseInt(v102));v76 = v76.concat(v75);return v76;};var f33 = function(v57){
            var v93;var v88;var v31 = v57.indexOf('=');if(v31 != -1){
            v93 = v57.substring(0, v31);v88 = v57.substring(v31 + 1,
            v57.length);}else{
            v93 = v57;v88 = '';}
            return [ v93, v88 ];};this.f33 = f33;this.f20 = function(bytes){
            var v77 = wffBMUtil.f11(bytes);var v16 = v77[0];var v29 = v16.values;var v98 = [];var parent;var v55 = f32(v29[0]);if(v55 === '#'){
            var text = f32(v29[1]);parent = document.createDocumentFragment();parent.appendChild(document.createTextNode(text));}else if(v55 === '@'){
            var text = f32(v29[1]);parent = document.createDocumentFragment();f28(parent, text);}else{
            parent = document.createElement(v55);for (var i = 1; i < v29.length; i++){
            var v57 = f33(f32(v29[i]));parent[v57[0]] = v57[1];parent.setAttribute(v57[0], v57[1]);}
            }
            v98.push(parent);for (var i = 1; i < v77.length; i++){
            var name = v77[i].name;var values = v77[i].values;var tagName = f32(values[0]);var child;if(tagName === '#'){
            var text = f32(values[1]);child = document.createDocumentFragment();child.appendChild(document.createTextNode(text));}else if(tagName === '@'){
            var text = f32(values[1]);child = document.createDocumentFragment();f28(child, text);}else{
            child = document.createElement(tagName);for (var j = 1; j < values.length; j++){
            var v57 = f33(f32(values[j]));child[v57[0]] = v57[1];child.setAttribute(v57[0], v57[1]);}
            }
            v98.push(child);var v62 = wffBMUtil.f16(name);v98[v62].appendChild(child);}
            return parent;};this.f2 = function(bytes){
            var v77 = wffBMUtil.f11(bytes);var v16 = v77[0];var v29 = v16.values;var v98 = [];var parent;var v55 = f6(v29[0]);if(v55 === '#' || v55 === '$'){
            var txt = v55 === '$' ? wffBMUtil.f16(v29[1]).toString()
            : v55 === '#' ? f32(v29[1]) : '';parent = document.createDocumentFragment();parent.appendChild(document.createTextNode(txt));}else if(v55 === '@'){
            var txt = f32(v29[1]);parent = document.createDocumentFragment();f28(parent, txt);}else{
            parent = document.createElement(v55);for (var i = 1; i < v29.length; i++){
            var v57 = f1(v29[i]);parent[v57[0]] = v57[1];parent.setAttribute(v57[0], v57[1]);}
            }
            v98.push(parent);for (var i = 1; i < v77.length; i++){
            var name = v77[i].name;var values = v77[i].values;var tagName = f6(values[0]);var child;if(tagName === '#' || tagName === '$'){
            var txt = tagName === '$' ? wffBMUtil.f16(values[1]).toString()
            : tagName === '#' ? f32(values[1]) : '';child = document.createDocumentFragment();child.appendChild(document.createTextNode(txt));}else if(tagName === '@'){
            var text = f32(values[1]);child = document.createDocumentFragment();f28(child, text);}else{
            child = document.createElement(tagName);for (var j = 1; j < values.length; j++){
            var v57 = f1(values[j]);child[v57[0]] = v57[1];child.setAttribute(v57[0], v57[1]);}
            }
            v98.push(child);var v62 = wffBMUtil.f16(name);v98[v62].appendChild(child);}
            return parent;};if(wffGlobal.CPRSD_DATA){
            this.f20 = this.f2;}
            this.f23 = function(p, v20){
            var i = wffBMUtil.f16(v20);return p.childNodes[i];};};
            var wffBMCRUIDUtil = new function(){
            var encoder = wffGlobal.encoder;var decoder = wffGlobal.decoder;var encodedBytesForHash = encoder.encode('#');this.f19 = function(tag){
            var v77 = [];var tUtf8Bytes = encoder.encode("T");var cUtf8Bytes = encoder.encode("DT");var v47 = {
            'name' : tUtf8Bytes,
            'values' : [ cUtf8Bytes ]
            };v77.push(v47);var v92 = wffTagUtil.f27(tag);var v85 = {
            'name' : v92,
            'values' : []
            };v77.push(v85);return wffBMUtil.f15(v77);};this.f14 = function(v93, v88, v65){
            var v77 = [];var tUtf8Bytes = encoder.encode("T");var cUtf8Bytes = encoder.encode("UA");var v47 = {
            'name' : tUtf8Bytes,
            'values' : [ cUtf8Bytes ]
            };v77.push(v47);var v24 = encoder.encode(v93 + "="
            + v88);var v85 = {
            'name' : wffBMUtil.f17(v65),
            'values' : [ v24 ]
            };v77.push(v85);return wffBMUtil.f15(v77);};var f45 = function(v77, tag, v62){
            var v6 = v62 + 1;var nodeName = tag.nodeName;if(nodeName != '#text'){
            var values = [];var v56 = encoder.encode(nodeName);values.push(v56);for (var i = 0; i < tag.attributes.length; i++){
            var attribute = tag.attributes[i];var v13;if(attribute.value != null){
            v13 = encoder
            .encode(attribute.name + '=' + attribute.value);}else{
            v13 = encoder
            .encode(attribute.name);}
            values.push(v13);}
            var v85 = {
            'name' : wffBMUtil.f17(v62),
            'values' : values
            };v77.push(v85);}else{
            var v45 = encoder.encode(tag.nodeValue);var values = [ encodedBytesForHash, v45 ];var v85 = {
            'name' : wffBMUtil.f17(v62),
            'values' : values
            };v77.push(v85);}
            v62++;for (var i = 0; i < tag.childNodes.length; i++){
            f45(v77, tag.childNodes[i], v62);}
            };this.f21 = function(tag, v42){
            var v77 = [];var tUtf8Bytes = encoder.encode("T");var cUtf8Bytes = encoder.encode("C");var v47 = {
            'name' : tUtf8Bytes,
            'values' : [ cUtf8Bytes ]
            };v77.push(v47);var v62 = 0;f45(v77, tag, v62);v77[1].name = wffBMUtil.f17(v42);return wffBMUtil.f15(v77);};};
            var wffClientCRUDUtil = new function(){
            var encoder = wffGlobal.encoder;var decoder = wffGlobal.decoder;var uriChangeQ = [];var f32 = function(utf8Bytes){
            return decoder.decode(new Uint8Array(utf8Bytes));};var f46 = function(v77){
            var v47 = v77[0];var taskValue = v47.values[0][0];if(taskValue == wffGlobal.taskValues.ATTRIBUTE_UPDATED){
            for (var i = 1; i < v77.length; i++){
            var v57 = wffTagUtil.f1(v77[i].name);var v93 = v57[0];var v88 = v57[1];var v100 = v77[i].values;for (var j = 0; j < v100.length; j++){
            var v101 = wffTagUtil.f25(v100[j]);var v50 = wffTagUtil.f42(v101);v50[v93] = v88;v50.setAttribute(v93, v88);}
            }
            }
            else if(taskValue == wffGlobal.taskValues.APPENDED_CHILD_TAG
            || taskValue == wffGlobal.taskValues.APPENDED_CHILDREN_TAGS){
            for (var i = 1; i < v77.length; i++){
            var v101 = wffTagUtil
            .f25(v77[i].name);var values = v77[i].values;var tagName = wffTagUtil.f6(values[0]);var parent = wffTagUtil.f18(tagName, v101);for (var j = 1; j < values.length; j++){
            var v90 = wffTagUtil
            .f20(values[j]);parent.appendChild(v90);}
            }
            }else if(taskValue == wffGlobal.taskValues.REMOVED_TAGS){
            for (var i = 1; i < v77.length; i++){
            var v101 = wffTagUtil
            .f25(v77[i].name);var values = v77[i].values;var tagName = wffTagUtil.f6(values[0]);var v64 = wffTagUtil.f18(tagName,
            v101);var parent = v64.parentNode;parent.removeChild(v64);}
            }else if(taskValue == wffGlobal.taskValues.REMOVED_ALL_CHILDREN_TAGS){
            for (var i = 1; i < v77.length; i++){
            var v101 = wffTagUtil
            .f25(v77[i].name);var values = v77[i].values;var tagName = wffTagUtil.f6(values[0]);var parentTag = wffTagUtil.f18(tagName,
            v101);while (parentTag.firstChild){
            parentTag.removeChild(parentTag.firstChild);}
            }
            }else if(taskValue == wffGlobal.taskValues.MOVED_CHILDREN_TAGS){
            for (var i = 1; i < v77.length; i++){
            var v25 = wffTagUtil
            .f25(v77[i].name);var values = v77[i].values;var v17 = wffTagUtil.f6(values[0]);var v33 = wffTagUtil.f18(
            v17, v25);var v94 = null;if(values[2].length == 0){
            v94 = wffTagUtil.f20(values[3]);}else{
            var v59 = wffTagUtil.f6(values[2]);var v74 = wffTagUtil.f25(values[1]);v94 = wffTagUtil.f18(v59,
            v74);if(typeof v94 !== 'undefined'){
            var previousParent = v94.parentNode;if(typeof previousParent !== 'undefined'){
            previousParent.removeChild(v94);}
            }else{
            v94 = wffTagUtil.f20(values[3]);}
            }
            v33.appendChild(v94);}
            }else if(taskValue == wffGlobal.taskValues.ADDED_ATTRIBUTES){
            for (var i = 1; i < v77.length; i++){
            var v85 = v77[i];if(v85.name[0] == wffGlobal.taskValues.MANY_TO_ONE){
            var tagName = wffTagUtil.f6(v85.values[0]);var v101 = wffTagUtil
            .f25(v85.values[1]);var v50 = wffTagUtil.f18(
            tagName, v101);for (var j = 2; j < v85.values.length; j++){
            var v57 = wffTagUtil.f1(v85.values[j]);var v93 = v57[0];var v88 = v57[1];v50[v93] = v88;v50.setAttribute(v93, v88);}
            }
            }
            }else if(taskValue == wffGlobal.taskValues.REMOVED_ATTRIBUTES){
            for (var i = 1; i < v77.length; i++){
            var v85 = v77[i];if(v85.name[0] == wffGlobal.taskValues.MANY_TO_ONE){
            var tagName = wffTagUtil.f6(v85.values[0]);var v101 = wffTagUtil
            .f25(v85.values[1]);var v50 = wffTagUtil.f18(
            tagName, v101);for (var j = 2; j < v85.values.length; j++){
            var v93 = wffTagUtil.f4(v85.values[j]);v50.removeAttribute(v93);if(wffGlobal.NDXD_BLN_ATRBS.indexOf(v93) != -1){
            var prop = v50[v93];if(prop && prop === true){
            v50[v93] = false;}
            }
            }
            }
            }
            }else if(taskValue == wffGlobal.taskValues.ADDED_INNER_HTML){
            var tagName = wffTagUtil.f6(v77[1].name);var v101 = wffTagUtil
            .f25(v77[1].values[0]);var parentTag = wffTagUtil.f18(tagName,
            v101);while (parentTag.firstChild){
            parentTag.removeChild(parentTag.firstChild);}
            for (var i = 2; i < v77.length; i++){
            var values = v77[i].values;var v90 = wffTagUtil.f20(v77[i].name);if(values.length == 1 && values[0].length == 1){
            var existingTag = wffTagUtil.f18(
            v90.nodeName, v90
            .getAttribute("data-wff-id"));if(existingTag){
            var v21 = existingTag.parentNode;v21.removeChild(existingTag);}
            }
            parentTag.appendChild(v90);}
            }else if(taskValue == wffGlobal.taskValues.INSERTED_BEFORE_TAG){
            var tagName = wffTagUtil.f6(v77[1].name);var v101 = wffTagUtil.f25(v77[1].values[0]);var parentTag = wffTagUtil.f18(tagName, v101);var v51 = wffTagUtil.f6(v77[2].name);var v87;if(v51 === '#'){
            var v20 = v77[2].values[0];v87 = wffTagUtil.f23(parentTag, v20);}else{
            var v43 = wffTagUtil.f25(v77[2].values[0]);v87 = wffTagUtil.f18(v51,
            v43);}
            for (var i = 3; i < v77.length; i++){
            var nm = v77[i].name;var values = v77[i].values;var v90 = wffTagUtil.f20(values[0]);if(nm.length == 1){
            var existingTag = wffTagUtil.f18(
            v90.nodeName, v90
            .getAttribute("data-wff-id"));var v21 = existingTag.parentNode;if(v21){v21.removeChild(existingTag);}
            }
            parentTag.insertBefore(v90, v87);}
            }else if(taskValue == wffGlobal.taskValues.INSERTED_AFTER_TAG){
            var tagName = wffTagUtil.f6(v77[1].name);var v101 = wffTagUtil.f25(v77[1].values[0]);var parentTag = wffTagUtil.f18(tagName, v101);var v51 = wffTagUtil.f6(v77[2].name);var v87;if(v51 === '#'){
            var v20 = v77[2].values[0];v87 = wffTagUtil.f23(parentTag, v20);}else{
            var v43 = wffTagUtil.f25(v77[2].values[0]);v87 = wffTagUtil.f18(v51,
            v43);}
            var firstNd;for (var i = 3; i < v77.length; i++){
            var nm = v77[i].name;var values = v77[i].values;var v90 = wffTagUtil.f20(values[0]);if(nm.length == 1){
            var existingTag = wffTagUtil.f18(
            v90.nodeName, v90
            .getAttribute("data-wff-id"));var v21 = existingTag.parentNode;if(v21){v21.removeChild(existingTag);}
            }
            parentTag.insertBefore(v90, v87);if(!firstNd){
            firstNd = v90;}
            }
            if(firstNd){
            parentTag.removeChild(v87);parentTag.insertBefore(v87, firstNd);}
            }else if(taskValue == wffGlobal.taskValues.REPLACED_WITH_TAGS){
            var tagName = wffTagUtil.f6(v77[1].name);var v101 = wffTagUtil.f25(v77[1].values[0]);var parentTag = wffTagUtil.f18(tagName, v101);var v51 = wffTagUtil.f6(v77[2].name);var v87;if(v51 === '#'){
            var v20 = v77[2].values[0];v87 = wffTagUtil.f23(parentTag, v20);}else{
            var v43 = wffTagUtil.f25(v77[2].values[0]);v87 = wffTagUtil.f18(v51,
            v43);}
            for (var i = 3; i < v77.length; i++){
            var nm = v77[i].name;var values = v77[i].values;var v90 = wffTagUtil.f20(values[0]);if(nm.length == 1){
            var existingTag = wffTagUtil.f18(
            v90.nodeName, v90
            .getAttribute("data-wff-id"));var v21 = existingTag.parentNode;v21.removeChild(existingTag);}
            parentTag.insertBefore(v90, v87);}
            parentTag.removeChild(v87);}else if(taskValue == wffGlobal.taskValues.RELOAD_BROWSER){
            location.reload(true);}else if(taskValue == wffGlobal.taskValues.RELOAD_BROWSER_FROM_CACHE){
            location.reload();}else if(taskValue == wffGlobal.taskValues.EXEC_JS){
            var js = f32(v47.values[1]);var op = v47.values[2][0];if(op == 1){
            localStorage.setItem('WFF_EXEC_JS', JSON.stringify({ js: js, instanceId: wffGlobal.INSTANCE_ID }));localStorage.removeItem('WFF_EXEC_JS');}else{
            if(window.execScript){
            window.execScript(js);}else{
            eval(js);}
            }
            }else if(taskValue == wffGlobal.taskValues.SET_URI){
            var jsObj = new JsObjectFromBMBytes(v47.values[1], true);jsObj.uriAfter = jsObj.ua;jsObj.uriBefore = jsObj.ub;jsObj.origin = jsObj.o;jsObj.replace = jsObj.r;delete jsObj.ua;delete jsObj.ub;delete jsObj.o;delete jsObj.r;if(jsObj.uriAfter && jsObj.uriAfter !== jsObj.uriBefore){
            if(jsObj.origin === 'S'){
            jsObj.origin = 'server';jsObj.initiator = 'serverCode';}
            var vnt = {};for (k in jsObj){
            vnt[k] = jsObj[k];}
            if(jsObj.replace){
            history.replaceState({}, document.title, jsObj.uriAfter);}else{
            history.pushState({}, document.title, jsObj.uriAfter);}
            wffGlobal.getAndUpdateLocation();uriChangeQ.push(vnt);if(typeof wffGlobalListeners !== "undefined" && wffGlobalListeners.onSetURI && jsObj.origin === 'server'){
            try {
            wffGlobalListeners.onSetURI(vnt);} catch (e){
            wffLog("wffGlobalListeners.onSetURI threw exception when browserPage.setURI is called", e);}
            }
            }
            }else if(taskValue == wffGlobal.taskValues.AFTER_SET_URI){
            if(typeof wffGlobalListeners !== "undefined" && wffGlobalListeners.afterSetURI){
            for (var i = 0; i < uriChangeQ.length; i++){
            var vnt = uriChangeQ[i];try {
            wffGlobalListeners.afterSetURI(vnt);} catch (e){
            wffLog("wffGlobalListeners.afterSetURI threw exception when the setURI method in the server is called.", e);}
            }
            }
            uriChangeQ = [];}else if(taskValue == wffGlobal.taskValues.SET_LS_ITEM){
            var jsObj = new JsObjectFromBMBytes(v47.values[1], true);if(typeof localStorage !== "undefined" && jsObj.k && jsObj.wt && jsObj.id){
            var wt = parseInt(jsObj.wt);var prev = localStorage.getItem(jsObj.k + '_wff_data');var lstWT = 0;var lstId = 0;if(prev){
            try {
            var itemObj = JSON.parse(prev);lstWT = parseInt(itemObj.wt);lstId = itemObj.id;} catch (e){
            wffLog(e);}
            }
            if(wt >= lstWT){
            var id = wffBMUtil.f16(jsObj.id);if(wt > lstWT || (wt == lstWT && id > lstId)){
            var itemVal = JSON.stringify({ v: jsObj.v, wt: jsObj.wt, id: id });localStorage.setItem(jsObj.k + '_wff_data', itemVal);}
            }
            if(jsObj.cb){
            var v47 = wffTaskUtil.f36(
            wffGlobal.taskValues.TASK,
            taskValue);var v85 = {
            'name': jsObj.id,
            'values': []
            };var v77 = [v47, v85];var wffBM = wffBMUtil.f15(v77);wffWS.send(wffBM);}
            }
            }else if(taskValue == wffGlobal.taskValues.GET_LS_ITEM){
            var jsObj = new JsObjectFromBMBytes(v47.values[1], true);if(typeof localStorage !== "undefined" && jsObj.id && jsObj.k){
            var itemJSON = localStorage.getItem(jsObj.k + '_wff_data');var itemObj;if(itemJSON){
            try {
            itemObj = JSON.parse(itemJSON);} catch (e){
            wffLog(e);}
            }
            if(!itemObj || !itemObj.wt){
            itemObj = {};}
            var v47 = wffTaskUtil.f36(
            wffGlobal.taskValues.TASK,
            taskValue);var v85 = {
            'name': jsObj.id,
            'values': []
            };if(itemObj.wt){
            v85.values = [encoder.encode(itemObj.v), encoder.encode(itemObj.wt)];}
            var v77 = [v47, v85];var wffBM = wffBMUtil.f15(v77);wffWS.send(wffBM);}
            }else if(taskValue == wffGlobal.taskValues.REMOVE_LS_ITEM || taskValue == wffGlobal.taskValues.REMOVE_AND_GET_LS_ITEM){
            var jsObj = new JsObjectFromBMBytes(v47.values[1], true);if(typeof localStorage !== "undefined" && jsObj.k && jsObj.wt && jsObj.id){
            var itemJSON = localStorage.getItem(jsObj.k + '_wff_data');var itemObj;if(itemJSON){
            try {
            itemObj = JSON.parse(itemJSON);} catch (e){
            wffLog(e);}
            }
            if(!itemObj || !itemObj.wt || !itemObj.id){
            itemObj = {};}else{
            var jsObjWT = parseInt(jsObj.wt);var itemObjWT = parseInt(itemObj.wt);if(jsObjWT >= itemObjWT){
            var id = wffBMUtil.f16(jsObj.id);if(jsObjWT > itemObjWT || (jsObjWT == itemObjWT && id > itemObj.id)){
            localStorage.removeItem(jsObj.k + '_wff_data');}
            }
            }
            if(jsObj.cb){
            var v47 = wffTaskUtil.f36(
            wffGlobal.taskValues.TASK,
            taskValue);var v85 = {
            'name': jsObj.id,
            'values': []
            };if(taskValue == wffGlobal.taskValues.REMOVE_AND_GET_LS_ITEM && itemObj.wt){
            v85.values = [encoder.encode(itemObj.v), encoder.encode(itemObj.wt)];}
            var v77 = [v47, v85];var wffBM = wffBMUtil.f15(v77);wffWS.send(wffBM);}
            }
            }else if(taskValue == wffGlobal.taskValues.CLEAR_LS){
            var jsObj = new JsObjectFromBMBytes(v47.values[1], true);if(typeof localStorage !== "undefined" && jsObj.wt && jsObj.tp && jsObj.id){
            for (var k in localStorage){
            if((k.endsWith('_wff_data') && (jsObj.tp === 'D' || jsObj.tp === 'DT'))
            || (k.endsWith('_wff_token') && (jsObj.tp === 'T' || jsObj.tp === 'DT'))){
            try {
            var itemObj = JSON.parse(localStorage.getItem(k));if(itemObj && itemObj.wt && itemObj.id){
            var jsObjWT = parseInt(jsObj.wt);var itemObjWT = parseInt(itemObj.wt);if(jsObjWT >= itemObjWT){
            var id = wffBMUtil.f16(jsObj.id);if(jsObjWT > itemObjWT || (jsObjWT == itemObjWT && id > itemObj.id)){
            if(k.endsWith('_wff_token')){
            itemObj.removed = true;itemObj.id = id;itemObj.wt = jsObj.wt;itemObj.nid = wffGlobal.NODE_ID;localStorage.setItem(k, JSON.stringify(itemObj));}
            localStorage.removeItem(k);}
            }
            }
            } catch (e){
            wffLog(e);}
            }
            }
            if(jsObj.cb){
            var v47 = wffTaskUtil.f36(
            wffGlobal.taskValues.TASK,
            taskValue);var v85 = {
            'name': jsObj.id,
            'values': []
            };var v77 = [v47, v85];var wffBM = wffBMUtil.f15(v77);wffWS.send(wffBM);}
            }
            }else if(taskValue == wffGlobal.taskValues.SET_LS_TOKEN){
            var jsObj = new JsObjectFromBMBytes(v47.values[1], true);if(typeof localStorage !== "undefined" && jsObj.k && jsObj.wt && jsObj.id){
            var tknNm = jsObj.k + '_wff_token';var wt = parseInt(jsObj.wt);var prev = localStorage.getItem(tknNm);var lstWT = 0;var lstId = 0;if(prev){
            try {
            var itemObj = JSON.parse(prev);lstWT = parseInt(itemObj.wt);lstId = itemObj.id;} catch (e){
            wffLog(e);}
            }
            if(wt >= lstWT){
            var id = wffBMUtil.f16(jsObj.id);if(wt > lstWT || (wt == lstWT && id > lstId)){
            var itemVal = JSON.stringify({ v: jsObj.v, wt: jsObj.wt, nid: wffGlobal.NODE_ID, id: id });localStorage.setItem(tknNm, itemVal);}
            }
            }
            }else if(taskValue == wffGlobal.taskValues.REMOVE_LS_TOKEN){
            var jsObj = new JsObjectFromBMBytes(v47.values[1], true);if(typeof localStorage !== "undefined" && jsObj.k && jsObj.wt && jsObj.id){
            var tknNm = jsObj.k + '_wff_token';var itemJSON = localStorage.getItem(tknNm);var itemObj;if(itemJSON){
            try {
            itemObj = JSON.parse(itemJSON);} catch (e){
            wffLog(e);}
            }
            if(itemObj && itemObj.wt && itemObj.id){
            var jsObjWT = parseInt(jsObj.wt);var itemObjWT = parseInt(itemObj.wt);if(jsObjWT >= itemObjWT){
            var id = wffBMUtil.f16(jsObj.id);if(jsObjWT >= itemObjWT || (jsObjWT == itemObjWT && id > itemObj.id)){
            itemObj.removed = true;itemObj.id = id;itemObj.wt = jsObj.wt;itemObj.nid = wffGlobal.NODE_ID;localStorage.setItem(tknNm, JSON.stringify(itemObj));localStorage.removeItem(tknNm);}
            }
            }
            }
            }else if(taskValue == wffGlobal.taskValues.COPY_INNER_TEXT_TO_VALUE){
            var tagName = wffTagUtil.f6(v77[1].name);var v101 = wffTagUtil
            .f25(v77[1].values[0]);var parentTag = wffTagUtil.f18(tagName,
            v101);var d = document.createElement('div');d.innerHTML = parentTag.outerHTML;parentTag.value = d.childNodes[0].innerText;}else if(taskValue == wffGlobal.taskValues.SET_BM_OBJ_ON_TAG
            || taskValue == wffGlobal.taskValues.SET_BM_ARR_ON_TAG){
            var tagName = wffTagUtil.f6(v77[1].name);var v101 = wffTagUtil
            .f25(v77[1].values[0]);var tag = wffTagUtil.f18(tagName, v101);var ky = f32(v77[1].values[1]);var v38 = v77[1].values[2];var jsObjOrArr;if(taskValue == wffGlobal.taskValues.SET_BM_OBJ_ON_TAG){
            jsObjOrArr = new JsObjectFromBMBytes(v38, true);}else{
            jsObjOrArr = new JsArrayFromBMBytes(v38, true);}
            var wffObjects = tag['wffObjects'];if(typeof wffObjects === 'undefined'){
            wffObjects = {};tag['wffObjects'] = wffObjects;}
            wffObjects[ky] = jsObjOrArr;}else if(taskValue == wffGlobal.taskValues.DEL_BM_OBJ_OR_ARR_FROM_TAG){
            var tagName = wffTagUtil.f6(v77[1].name);var v101 = wffTagUtil
            .f25(v77[1].values[0]);var tag = wffTagUtil.f18(tagName, v101);var ky = f32(v77[1].values[1]);var wffObjects = tag['wffObjects'];if(typeof wffObjects !== 'undefined'){
            delete wffObjects[ky];}
            }
            return true;};this.f44 = function(v80){
            var v77 = wffBMUtil.f11(v80);var v47 = v77[0];if(v47.name[0] == wffGlobal.taskValues.TASK){
            f46(v77);}else if(v47.name[0] == wffGlobal.taskValues.TASK_OF_TASKS){
            var tasksBM = v47.values;for (var i = 0; i < tasksBM.length; i++){
            var v46 = wffBMUtil.f11(tasksBM[i]);f46(v46);}
            }else{
            return false;}
            return true;};this.f31 = function(v80){
            var v85 = wffBMUtil.f11(v80)[1];};};
            var wffTaskUtil = new function (){
            var encoder = wffGlobal.encoder;this.f36 = function(v95, valueByte){
            var v47 = {
            'name' : [v95],
            'values' : [ [valueByte] ]
            };return v47;};};var wffServerMethods = new function (){
            var encoder = wffGlobal.encoder;var f26 = function(v71){
            var v89 = [];if(typeof v71 === 'string'){
            var ndx = wffGlobal.NDXD_VNT_ATRBS.indexOf(v71);if(ndx != -1){
            var ndxByts = wffBMUtil.f17(ndx);v89.push(0);for (var i = 0; i < ndxByts.length; i++){
            v89.push(ndxByts[i]);}
            }else{
            v89 = encoder.encode(v71);}
            }else{
            var ndxByts = wffBMUtil.f17(v71);v89.push(0);for (var i = 0; i < ndxByts.length; i++){
            v89.push(ndxByts[i]);}
            }
            return v89;};var f43 = function(event, tag, v71, prvntDflt){
            if(prvntDflt){
            event.preventDefault();}
            var v47 = wffTaskUtil.f36(wffGlobal.taskValues.TASK, wffGlobal.taskValues.INVOKE_ASYNC_METHOD);var v89 = f26(v71);var v85 = {'name':wffTagUtil.f27(tag), 'values':[v89]};var v77 = [v47, v85];var wffBM = wffBMUtil.f15(v77);wffWS.send(wffBM);};this.b = function(event, tag, v71){
            f43(event, tag, v71, true);};var invokeAsync = function(event, tag, v71){
            f43(event, tag, v71, false);};this.a = invokeAsync;var f24 = function(event, tag, v71, preFun, prvntDflt){
            if(prvntDflt){
            event.preventDefault();}
            var invoked = false;var actionPerform = function(){
            invoked = true;var v47 = wffTaskUtil.f36(wffGlobal.taskValues.TASK, wffGlobal.taskValues.INVOKE_ASYNC_METHOD);var v89 = f26(v71);var v85 = {'name':wffTagUtil.f27(tag), 'values':[v89]};var v77 = [v47, v85];var wffBM = wffBMUtil.f15(v77);wffWS.send(wffBM);};var action = new function(){
            this.perform = function(){
            actionPerform();};};if(preFun(event, tag, action)){
            if(!invoked){
            actionPerform();}
            }
            };this.f = function(event, tag, v71, preFun){
            f24(event, tag, v71, preFun, true);};var invokeAsyncWithPreFun = function(event, tag, v71, preFun){
            f24(event, tag, v71, preFun, false);};this.e = invokeAsyncWithPreFun;var f7 = function(event, tag, v71, preFun, filterFun, prvntDflt){
            if(prvntDflt){
            event.preventDefault();}
            var invoked = false;var actionPerform = function(){
            invoked = true;var v47 = wffTaskUtil.f36(wffGlobal.taskValues.TASK, wffGlobal.taskValues.INVOKE_ASYNC_METHOD);var v89 = f26(v71);var jsObject = filterFun(event, tag);var v32 = new WffBMObject(jsObject);var v96 = v32.getBMBytes();var v85 = {'name':wffTagUtil.f27(tag), 'values':[v89, v96]};var v77 = [v47, v85];var wffBM = wffBMUtil.f15(v77);wffWS.send(wffBM);};var action = new function(){
            this.perform = function(){
            actionPerform();};};if(preFun(event, tag, action)){
            if(!invoked){
            actionPerform();}
            }
            };this.h = function(event, tag, v71, preFun, filterFun){
            f7(event, tag, v71, preFun, filterFun, true);};var invokeAsyncWithPreFilterFun = function(event, tag, v71, preFun, filterFun){
            f7(event, tag, v71, preFun, filterFun, false);};this.g = invokeAsyncWithPreFilterFun;var f12 = function(event, tag, v71, filterFun, prvntDflt){
            if(prvntDflt){
            event.preventDefault();}
            var v47 = wffTaskUtil.f36(wffGlobal.taskValues.TASK, wffGlobal.taskValues.INVOKE_ASYNC_METHOD);var v89 = f26(v71);var jsObject = filterFun(event, tag);var v32 = new WffBMObject(jsObject);var v96 = v32.getBMBytes();var v85 = {'name':wffTagUtil.f27(tag), 'values':[v89, v96]};var v77 = [v47, v85];var wffBM = wffBMUtil.f15(v77);wffWS.send(wffBM);};this.d = function(event, tag, v71, filterFun){
            f12(event, tag, v71, filterFun, true);};var invokeAsyncWithFilterFun = function(event, tag, v71, filterFun){
            f12(event, tag, v71, filterFun, false);};this.c = invokeAsyncWithFilterFun;};var wffSM = wffServerMethods;
            var wffClientMethods = new function(){
            var f32 = function(utf8Bytes){
            return wffGlobal.decoder.decode(new Uint8Array(utf8Bytes));};this.exePostFun = function(v80){
            var v77 = wffBMUtil.f11(v80);var v47 = v77[0];var taskValue = v47.values[0][0];if(v47.name[0] != wffGlobal.taskValues.TASK){
            return false;}
            if(taskValue == wffGlobal.taskValues.INVOKE_POST_FUNCTION){
            var funString = f32(v77[1].name);var values = v77[1].values;var jsObject = null;if(values.length > 0){
            var value = values[0];if(value.length > 0){
            var bmObjBytes = v77[1].values[0];jsObject = new JsObjectFromBMBytes(bmObjBytes, true);}
            }
            var func;if(window.execScript){
            func = window.execScript("(function(jsObject){" + funString
            + "})");}else{
            func = eval("(function(jsObject){" + funString + "})");}
            func(jsObject);}else if(taskValue == wffGlobal.taskValues.INVOKE_CALLBACK_FUNCTION){
            var funKey = f32(v77[1].name);var cbFun = wffAsync.v27[funKey];var values = v77[1].values;var jsObject = null;if(values.length > 0){
            var value = values[0];if(value.length > 0){
            var bmObjBytes = v77[1].values[0];jsObject = new JsObjectFromBMBytes(bmObjBytes, true);}
            }
            cbFun(jsObject);delete wffAsync.v27[funKey];}else{
            return false;}
            return true;};};
            var f37 = function(valueType){
            if(valueType === '[object String]'){
            return 0;}else if(valueType === '[object Number]'){
            return 1;}else if(valueType === '[object Undefined]'){
            return 2;}else if(valueType === '[object Null]'){
            return 3;}else if(valueType === '[object Boolean]'){
            return 4;}else if(valueType === '[object Object]'){
            return 5;}else if(valueType === '[object Array]'){
            return 6;}else if(valueType === '[object RegExp]'){
            return 7;}else if(valueType === '[object Function]'){
            return 8;}else if(valueType === '[object Int8Array]'
            || valueType == "[object Uint8Array]"){
            return 9;}
            return -1;};var WffBMArray = function(jsArray, outer){
            var encoder = wffGlobal.encoder;var decoder = wffGlobal.decoder;this.jsArray = jsArray;this.outer = outer;var getWffBMArray = function(jsArray, outer){
            var v77 = [];if(typeof outer === 'undefined' || outer){
            var typeNameValue = {
            name : [ 1 ],
            values : []
            };v77.push(typeNameValue);}
            if(jsArray.length > 0){
            var arrayValType;for (var i = 0; i < jsArray.length; i++){
            arrayValType = f37(Object.prototype.toString
            .call(jsArray[i]));if(arrayValType != 2 && arrayValType != 3){
            break;}
            }
            var v85 = {
            name : [ arrayValType ]
            };v77.push(v85);var values = [];v85.values = values;if(arrayValType == 0){
            for (var i = 0; i < jsArray.length; i++){
            if(arrayValType != 2 && arrayValType != 3){
            values.push(encoder.encode(jsArray[i]));}else{
            values.push([]);}
            }
            }else if(arrayValType == 1){
            for (var i = 0; i < jsArray.length; i++){
            if(arrayValType != 2 && arrayValType != 3){
            values.push(wffBMUtil.f34(jsArray[i]));}else{
            values.push([]);}
            }
            }  else if(arrayValType == 2
            || arrayValType == 3){
            for (var i = 0; i < jsArray.length; i++){
            values.push([]);}
            }else if(arrayValType == 4){
            for (var i = 0; i < jsArray.length; i++){
            if(arrayValType != 2 && arrayValType != 3){
            if(jsArray[i]){
            values.push(true);}else{
            values.push(false);}
            }else{
            values.push([]);}
            }
            }else if(arrayValType == 5){
            for (var i = 0; i < jsArray.length; i++){
            values
            .push(new WffBMObject(jsArray[i], false)
            .getBMBytes());}
            }else if(arrayValType == 6){
            for (var i = 0; i < jsArray.length; i++){
            values.push(new WffBMArray(jsArray[i], false).getBMBytes());}
            }else if(arrayValType == 7 || arrayValType == 8){
            for (var i = 0; i < jsArray.length; i++){
            if(arrayValType != 2 && arrayValType != 3){
            values.push(encoder.encode(jsArray[i].toString()));}else{
            values.push([]);}
            }
            }else if(arrayValType == 9){
            for (var i = 0; i < jsArray.length; i++){
            if(typeof jsArray[i] === 'undefined' || jsArray[i] == null){
            values.push([]);}else{
            values.push(new WffBMByteArray(jsArray[i], false)
            .getBMBytes());}
            }
            }else{
            values.push([]);}
            }
            return wffBMUtil.f15(v77);};this.getBMBytes = function getBMBytes(){
            return getWffBMArray(this.jsArray, this.outer);};return this;};var WffBMByteArray = function(int8Array, outer){
            var encoder = wffGlobal.encoder;var decoder = wffGlobal.decoder;this.jsArray = int8Array;this.outer = outer;var getWffBMByteArray = function(int8Array, outer){
            var v77 = [];if(typeof outer === 'undefined' || outer){
            var typeNameValue = {
            name : [ 1 ],
            values : []
            };v77.push(typeNameValue);}
            var arrayValType = 10;var v85 = {
            name : [ arrayValType ]
            };v77.push(v85);v85.values = [ int8Array ];return wffBMUtil.f15(v77);};this.getBMBytes = function getBMBytes(){
            return getWffBMByteArray(this.jsArray, this.outer);};return this;};var WffBMObject = function(jsObject, outer){
            var encoder = wffGlobal.encoder;var decoder = wffGlobal.decoder;this.jsObject = jsObject;this.outer = outer;var getWffBMObject = function(jsObj, outer){
            var jsObjType = Object.prototype.toString.call(jsObj);var v77 = [];if(typeof outer === 'undefined' || outer){
            var typeNameValue = {
            name : [ 0 ],
            values : []
            };v77.push(typeNameValue);}
            for ( var k in jsObj){
            var value = jsObj[k];var valType = f37(Object.prototype.toString
            .call(value));var v85 = {};v85.name = encoder.encode(k);var values = [ [ valType ] ];v85.values = values;var typeByte = [ valType ];if(valType == 0){
            values.push(encoder.encode(value));}else if(valType == 1){
            values.push(wffBMUtil.f34(value));}else if(valType == 2){
            values.push([]);}else if(valType == 3){
            values.push([]);}else if(valType == 4){
            if(value){
            values.push([ 1 ]);}else{
            values.push([ 0 ]);}
            }else if(valType == 5){
            values.push(new WffBMObject(value, false).getBMBytes());}else if(valType == 6){
            if(value.length == 0){
            values.push([]);}else{
            values.push(new WffBMArray(value, false).getBMBytes());}
            }else if(valType == 7 || valType == 8){
            values.push(encoder.encode(value.toString()));}else if(valType == 9){
            values.push(new WffBMByteArray(value, false).getBMBytes());}else{
            values.push([]);}
            v77.push(v85);}
            return wffBMUtil.f15(v77);};this.getBMBytes = function getBMBytes(){
            return getWffBMObject(this.jsObject, this.outer);};};var JsObjectFromBMBytes = function(v80, outer){
            var encoder = wffGlobal.encoder;var decoder = wffGlobal.decoder;var f32 = function(utf8Bytes){
            return wffGlobal.decoder.decode(new Uint8Array(utf8Bytes));};var v77 = wffBMUtil.f11(v80);var i = 0;if(typeof outer === 'undefined' || outer){
            i = 1;}
            for (; i < v77.length; i++){
            var v85 = v77[i];var name = f32(v85.name);var values = v85.values;if(values[0] == 0){
            this[name] = f32(values[1]);}else if(values[0] == 1){
            this[name] = wffBMUtil.f9(values[1]);}else if(values[0] == 2){
            this[name] = undefined;}else if(values[0] == 3){
            this[name] = null;}else if(values[0] == 4){
            this[name] = values[1] == 1 ? true : false;}else if(values[0] == 5){
            this[name] = new JsObjectFromBMBytes(values[1], false);}else if(values[0] == 6){
            this[name] = new JsArrayFromBMBytes(values[1], false);}else if(values[0] == 7){
            this[name] = new RegExp(f32(values[1]));}else if(values[0] == 8){
            if(window.execScript){
            this[name] = window.execScript("("
            + f32(values[1]) + ")");}else{
            this[name] = eval("(" + f32(values[1]) + ")");}
            }else if(values[0] == 9){
            this[name] = new JsArrayFromBMBytes(values[1], false);}else if(values[0] == 10){
            this[name] = new Int8Array(values[1]);}
            }
            return this;};var JsArrayFromBMBytes = function(v80, outer){
            var encoder = wffGlobal.encoder;var decoder = wffGlobal.decoder;var f32 = function(utf8Bytes){
            return decoder.decode(new Uint8Array(utf8Bytes));};var v77 = wffBMUtil.f11(v80);var i = 0;if(typeof outer === 'undefined' || outer){
            i = 1;}
            var v85 = v77[i];var dataType = v85.name[0];var values = v85.values;var jsArray = [];if(dataType == 0){
            for (var j = 0; j < values.length; j++){
            jsArray.push(f32(values[j]));}
            }else if(dataType == 1){
            for (var j = 0; j < values.length; j++){
            jsArray.push(wffBMUtil.f9(values[j]));}
            }else if(dataType == 2){
            for (var j = 0; j < values.length; j++){
            jsArray.push(undefined);}
            }else if(dataType == 3){
            for (var j = 0; j < values.length; j++){
            jsArray.push(null);}
            }else if(dataType == 4){
            for (var j = 0; j < values.length; j++){
            jsArray.push(values[j] == 1 ? true : false);}
            }else if(dataType == 5){
            for (var j = 0; j < values.length; j++){
            jsArray.push(new JsObjectFromBMBytes(values[j], false));}
            }else if(dataType == 6){
            for (var j = 0; j < values.length; j++){
            jsArray.push(new JsArrayFromBMBytes(values[j], false));}
            }else if(dataType == 7){
            for (var j = 0; j < values.length; j++){
            jsArray.push(new RegExp(f32(values[j])));}
            }else if(dataType == 8){
            for (var j = 0; j < values.length; j++){
            var fun = f32(values[j]);var ary;if(window.execScript){
            ary = [ window.execScript("(" + fun + ")") ];}else{
            ary = [ eval("(" + fun + ")") ];}
            jsArray.push(ary[0]);}
            }else if(dataType == 9){
            if(values.length == 1){
            jsArray = new JsArrayFromBMBytes(values[0], false);}else{
            for (var j = 0; j < values.length; j++){
            jsArray.push(new JsArrayFromBMBytes(values[j], false));}
            }
            }  else if(dataType == 10){
            if(values.length == 1){
            jsArray = new Int8Array(values[0]);}else{
            for (var j = 0; j < values.length; j++){
            jsArray.push(new Int8Array(values[j]));}
            }
            }
            return jsArray;};
            window.wffAsync = new function(){
            var encoder = wffGlobal.encoder;this.v27 = {};var uuid = 0;this.generateUUID = function(){
            return (++uuid).toString();};this.serverMethod = function(v82, jsObject){
            this.invoke = function(callback){
            var v48;if(typeof callback === "function"){
            v48 = wffAsync.generateUUID();wffAsync.v27[v48] = callback;}else if(typeof callback === "undefined"){
            }else{
            throw "invoke function takes function argument";}
            var v47 = wffTaskUtil.f36(
            wffGlobal.taskValues.TASK,
            wffGlobal.taskValues.INVOKE_CUSTOM_SERVER_METHOD);var v36 = encoder.encode(v82);var values = [];if(typeof jsObject !== "undefined"){
            if(typeof jsObject === "object"){
            var v32 = new WffBMObject(jsObject);var v96 = v32.getBMBytes();values.push(v96);}else{
            throw "argument value should be an object";}
            }
            var v85 = {
            'name' : v36,
            'values' : values
            };var v77 = [ v47, v85 ];if(typeof v48 !== "undefined"){
            var v15 = {
            'name' : encoder.encode(v48),
            'values' : []
            };v77.push(v15);}
            var wffBM = wffBMUtil.f15(v77);wffWS.send(wffBM);};return this;};var f10 = function(uri, callback, initiator, rplc){
            var v47 = wffTaskUtil.f36(
            wffGlobal.taskValues.TASK,
            wffGlobal.taskValues.CLIENT_PATHNAME_CHANGED);var v85 = {
            'name': encoder.encode(uri),
            'values': []
            };var v99 = rplc ? 1 : 0;if(typeof initiator !== "undefined" && initiator >= 0 && initiator < wffGlobal.uriEventInitiator.size){
            v85.values.push([initiator, v99]);}else{
            v85.values.push([wffGlobal.uriEventInitiator.CLIENT_CODE, v99]);}
            var v77 = [v47, v85];if(callback){
            var v48 = wffAsync.generateUUID();wffAsync.v27[v48] = callback;var v15 = {
            'name': encoder.encode(v48),
            'values': []
            };v77.push(v15);}
            var wffBM = wffBMUtil.f15(v77);wffWS.send(wffBM);};this.setServerURIWithCallback = function(uri, callback, initiator){f10(uri, callback, initiator, false);};this.setServerURI = function(uri){ f10(uri, undefined, wffGlobal.uriEventInitiator.CLIENT_CODE, false); };var throwInvalidSetURIArgException = function(){
            throw "Invalid argument found in setURI function call. " +
            "Eg: wffAsync.setURI('/sampleuri', function(e){}, function(e){}, false);, " +
            "wffAsync.setURI('/sampleuri', function(e){}, function(e){});, " +
            "wffAsync.setURI('/sampleuri', function(e){});, " +
            "wffAsync.setURI('/sampleuri'); or " +
            "wffAsync.setURI('/sampleuri', null, null, false);";};var cURI = function(){
            var l = window.location;var h = l.href.endsWith('#') ? '#' : l.hash;return l.pathname + l.search + h;};var cURINoHsh = function(){
            var l = window.location;return l.pathname + l.search;};this.setURI = function(uri, onSetURI, afterSetURI, replace){
            if(typeof onSetURI !== "undefined" && onSetURI !== null && typeof onSetURI !== "function"){
            throwInvalidSetURIArgException();}
            if(typeof afterSetURI !== "undefined" && afterSetURI !== null && typeof afterSetURI !== "function"){
            throwInvalidSetURIArgException();}
            if(typeof replace !== "undefined" && replace !== null && typeof replace !== "boolean"){
            throwInvalidSetURIArgException();}
            var uriBefore = cURI();var uriBeforeNoHsh = cURINoHsh();var sameURI = uri === uriBefore || uri === window.location.href;if(!sameURI){
            if(replace){
            window.history.replaceState({ by: 'setURI' }, document.title, uri);}else{
            window.history.pushState({ by: 'setURI' }, document.title, uri);}
            }
            wffGlobal.getAndUpdateLocation();var uriAfter = cURI();var uriAfterNoHsh = cURINoHsh();if(uriBeforeNoHsh !== uriAfterNoHsh){
            var wffEvent = { uriBefore: uriBefore, uriAfter: uriAfter, origin: "client", initiator: 'clientCode', replace: replace ? true : false };var callbackWrapper = afterSetURI;if(typeof wffGlobalListeners !== "undefined"){
            if(wffGlobalListeners.onSetURI){
            try {
            wffGlobalListeners.onSetURI(wffEvent);} catch (e){
            wffLog("wffGlobalListeners.onSetURI threw exception when wffAsync.setURI is called", e);}
            }
            var afterSetURIGlobal = wffGlobalListeners.afterSetURI;if(afterSetURIGlobal){
            callbackWrapper = function(){
            if(afterSetURI){
            try {
            afterSetURI(wffEvent);} catch (e){
            wffLog("The third argument threw exception when wffAsync.setURI is called", e);}
            }
            try {
            afterSetURIGlobal(wffEvent);} catch (e){
            wffLog("wffGlobalListeners.afterSetURI threw exception when wffAsync.setURI is called", e);}
            };}
            }
            try {
            if(onSetURI){
            onSetURI(wffEvent);}
            } catch (e){
            wffLog("The second argument threw exception when wffAsync.setURI is called", e);}
            f10(uriAfter, callbackWrapper, wffGlobal.uriEventInitiator.CLIENT_CODE, replace);}
            };};
            var wffBMClientEvents = new function(){
            window.v1 = false;var encoder = wffGlobal.encoder;var decoder = wffGlobal.decoder;var f29 = function(wffInstanceId){
            var v77 = [];var v23 = encoder.encode(wffInstanceId);var tnv = wffTaskUtil.f36(
            wffGlobal.taskValues.TASK,
            wffGlobal.taskValues.REMOVE_BROWSER_PAGE);v77.push(tnv);var v85 = {
            'name' : v23,
            'values' : []
            };v77.push(v85);var wffBM = wffBMUtil.f15(v77);wffWS.send(wffBM);};this.f29 = f29;this.f22 = function(){
            if(!wffGlobal.REMOVE_PREV_BP_ON_INITTAB ||
            window.v1){
            return;}
            window.v1 = true;var wffInstanceId = sessionStorage.getItem('WFF_INSTANCE_ID');if(typeof wffInstanceId !== "undefined"
            && wffInstanceId !== wffGlobal.INSTANCE_ID){
            f29(wffInstanceId);}
            sessionStorage.setItem('WFF_INSTANCE_ID', wffGlobal.INSTANCE_ID);};this.f39 = function(){
            var v77 = [];var tnv = wffTaskUtil.f36(
            wffGlobal.taskValues.TASK,
            wffGlobal.taskValues.INITIAL_WS_OPEN);v77.push(tnv);var l = window.location;if(l){
            var h = l.href.endsWith('#') ? '#' : l.hash;var pthNV = {
            'name': [wffGlobal.taskValues.SET_URI],
            'values': [[wffGlobal.uriEventInitiator.BROWSER], encoder.encode(l.pathname + l.search + h)]
            };v77.push(pthNV);}
            if(typeof localStorage !== "undefined"){
            var itms = [];for (var k in localStorage){
            var ndx = k.lastIndexOf('_wff_token');if(ndx > -1 && k.endsWith('_wff_token')){
            try {
            var itemObj = JSON.parse(localStorage.getItem(k));itms.push({ k: k.substring(0, ndx), v: itemObj.v, id: itemObj.id, wt: itemObj.wt });} catch (e){
            wffLog('corrupted token data found in localStorage', e);}
            }
            }
            if(itms.length > 0){
            var bmBts = new WffBMArray(itms).getBMBytes();var nv = {
            'name': [wffGlobal.taskValues.SET_LS_TOKEN],
            'values': [bmBts]
            };v77.push(nv);}
            }
            var wffBM = wffBMUtil.f15(v77);wffWS.send(wffBM);};};
            var wffWS = new function(){
            var encoder = wffGlobal.encoder;var decoder = wffGlobal.decoder;var prevIntvl = null;var webSocket = null;var inDataQ = [];var sendQData = null;var v40 = new Date().getTime();this.openSocket = function(wsUrl){
            if(webSocket !== null
            && webSocket.readyState !== WebSocket.CLOSED
            && webSocket.readyState !== WebSocket.CLOSING){
            return;}
            webSocket = new WebSocket(wsUrl);sendQData = function(){
            if(webSocket !== null && webSocket.readyState === WebSocket.OPEN){
            var inData = [];var ndx = 0;var xp = false;var dataSent = false;for (ndx = 0; ndx < inDataQ.length; ndx++){
            var each = inDataQ[ndx];if(!xp){
            try {
            webSocket.send(new Int8Array(each).buffer);dataSent = true;} catch(e){
            xp = true;inData.push(each);}
            }else{
            inData.push(each);}
            }
            inDataQ = inData;if(dataSent){
            v40 = new Date().getTime();}
            }
            };webSocket.binaryType = 'arraybuffer';webSocket.onopen = function(event){
            try {
            if(prevIntvl !== null){
            clearInterval(prevIntvl);prevIntvl = null;}
            wffBMClientEvents.f22();if(sendQData !== null){
            sendQData();}
            if(typeof event.data === 'undefined'){
            return;}
            var binary = new Int8Array(event.data);if(binary.length < 4){
            return;}
            if((wffGlobal.LOSSLESS_COMM || binary[0] == 0) && binary.length > 4){
            var v91 = wffBMUtil.f16([binary[0], binary[1], binary[2], binary[3]]);if(v91 != 0 && v91 != wffGlobal.getUniqueServerSidePayloadId()){
            wffGlobal.onPayloadLoss();}else{
            var bin = [];for (var i = 4; i < binary.length; i++){
            bin.push(binary[i]);}
            wffClientCRUDUtil.f44(bin);}
            }else{
            wffClientCRUDUtil.f44(binary);}
            }catch(e){
            wffLog(e);}
            };webSocket.onmessage = function(event){
            try {
            var binary = new Int8Array(event.data);if(binary.length < 4){
            return;}
            if(wffGlobal.WS_HRTBT > 0 && (new Date().getTime() - v40) >= wffGlobal.WS_HRTBT){
            try{
            webSocket.send(new Int8Array([]).buffer);v40 = new Date().getTime();} catch(e){
            }
            }
            if((wffGlobal.LOSSLESS_COMM || binary[0] == 0) && binary.length > 4){
            var v91 = wffBMUtil.f16([binary[0], binary[1], binary[2], binary[3]]);if(v91 != 0 && v91 != wffGlobal.getUniqueServerSidePayloadId()){
            wffGlobal.onPayloadLoss();return;}else{
            var bin = [];for (var i = 4; i < binary.length; i++){
            bin.push(binary[i]);}
            binary = bin;}
            }
            var executed = wffClientMethods.exePostFun(binary);if(!executed){
            wffClientCRUDUtil.f44(binary);}
            }catch(e){
            wffLog(e);}
            };webSocket.onclose = function(event){
            if(prevIntvl !== null){
            clearInterval(prevIntvl);prevIntvl = null;}
            prevIntvl = setInterval(function(){
            if(webSocket === null || webSocket.readyState === WebSocket.CLOSED){
            wffWS.openSocket(wffGlobal.WS_URL);}
            }, wffGlobal.WS_RECON);};webSocket.onerror = function(event){
            try{webSocket.close();}catch(e){wffLog("ws.close error");}
            };};
            this.send = function(bytes){
            if(bytes.length > 0){
            var bin = bytes;if(wffGlobal.LOSSLESS_COMM && bin.length > 4){
            bin = wffBMUtil.f41(wffGlobal.getUniqueClientSidePayloadId());for (var i = 0; i < bytes.length; i++){
            bin.push(bytes[i]);}
            }
            inDataQ.push(bin);if(sendQData !== null){
            sendQData();}
            }else{
            webSocket.send(new Int8Array(bytes).buffer);v40 = new Date().getTime();}
            };this.closeSocket = function(){
            try {
            if(webSocket !== null
            && webSocket.readyState !== WebSocket.CONNECTING
            && webSocket.readyState !== WebSocket.CLOSED){
            webSocket.close();}
            } catch(e){}
            };this.getState = function(){
            if(webSocket !== null){
            return webSocket.readyState;}
            return -1;};};
            document.addEventListener("DOMContentLoaded",
            function(event){
            wffGlobal.getAndUpdateLocation();var encoder = wffGlobal.encoder;if(typeof window.f39Invoked === 'undefined'){
            window.f39Invoked = true;wffBMClientEvents.f39();}
            wffWS.openSocket(wffGlobal.WS_URL);window.v30 = false;var f38 = function(){
            if(!window.v30){
            wffBMClientEvents.f29(sessionStorage.getItem('WFF_INSTANCE_ID'));}
            window.v30 = true;};var f13 = function (eventName){
            var el = window;eventName = 'on' + eventName;var isSupported = (eventName in el);if(!isSupported && typeof InstallTrigger !== 'undefined'){
            el.setAttribute(eventName, 'return;');isSupported = typeof el[eventName] == 'function';}
            el = null;return isSupported;};if(f13('beforeunload')){
            window.addEventListener("beforeunload", f38, false);}
            if(f13('unload')){
            window.addEventListener("unload", f38, false);}
            if(f13('popstate')){
            window.addEventListener('popstate', function(event){
            var prevLoc = wffGlobal.getAndUpdateLocation();var uriBefore;var uriBeforeNoHash;if(prevLoc){
            uriBefore = prevLoc.pathname + prevLoc.search + prevLoc.hash;uriBeforeNoHash = prevLoc.pathname + prevLoc.search;}
            var l = window.location;var h = l.href.endsWith('#') ? '#' : l.hash;var uriAfter = l.pathname + l.search + h;var uriAfterNoHash = l.pathname + l.search;if(prevLoc && uriBeforeNoHash !== uriAfterNoHash){
            if(typeof wffGlobalListeners !== "undefined"){
            var wffEvent = { uriAfter: uriAfter, origin: "client", initiator: 'browser' };if(uriBefore){
            wffEvent.uriBefore = uriBefore;}
            if(wffGlobalListeners.onSetURI){
            try {
            wffGlobalListeners.onSetURI(wffEvent);} catch (e){
            wffLog("wffGlobalListeners.onSetURI threw exception on browser navigation", e);}
            }
            var afterSetURIGlobal = wffGlobalListeners.afterSetURI;var callbackWrapper = undefined;if(afterSetURIGlobal){
            callbackWrapper = function(){
            try {
            afterSetURIGlobal(wffEvent);} catch (e){
            wffLog("wffGlobalListeners.afterSetURI threw exception on browser navigation", e);}
            };}
            wffAsync.setServerURIWithCallback(uriAfter, callbackWrapper, wffGlobal.uriEventInitiator.BROWSER);}else{
            wffAsync.setServerURIWithCallback(uriAfter, undefined, wffGlobal.uriEventInitiator.BROWSER);}
            }
            });}
            if(f13('storage') && typeof localStorage !== "undefined"){
            window.addEventListener('storage', function(event){
            if(event && event.key && event.newValue){
            var ndx = event.key.lastIndexOf('_wff_token')
            if(ndx > -1 && event.key.endsWith('_wff_token')){
            var itemObj;try {
            itemObj = JSON.parse(event.newValue);} catch (e){
            wffLog(e);}
            if(itemObj && itemObj.id && itemObj.wt && itemObj.nid !== wffGlobal.NODE_ID){
            var ky = encoder.encode(event.key.substring(0, ndx));var wtBts = encoder.encode(itemObj.wt);var id = wffBMUtil.f17(itemObj.id);var v77;if(itemObj.removed){
            var v47 = wffTaskUtil.f36(
            wffGlobal.taskValues.TASK,
            wffGlobal.taskValues.REMOVE_LS_TOKEN);var v85 = {
            'name': id,
            'values': [ky, wtBts]
            };v77 = [v47, v85];}else{
            var v47 = wffTaskUtil.f36(
            wffGlobal.taskValues.TASK,
            wffGlobal.taskValues.SET_LS_TOKEN);var v85 = {
            'name': id,
            'values': [ky, encoder.encode(itemObj.v), wtBts]
            };v77 = [v47, v85];}
            var wffBM = wffBMUtil.f15(v77);wffWS.send(wffBM);}
            }else if(event.key === 'WFF_EXEC_JS'){
            var json = JSON.parse(event.newValue);if(json.js && json.instanceId && json.instanceId !== wffGlobal.INSTANCE_ID){
            if(window.execScript){
            window.execScript(json.js);}else{
            eval(json.js);}
            }
            }
            }
            });}
            MutationObserver = window.MutationObserver
            || window.WebKitMutationObserver;var attrObserver = new MutationObserver(function(mutations,
            observer){
            var v57s = {};for (var i = 0; i < mutations.length; i++){
            var mutation = mutations[i];v57s[mutation.attributeName] = mutation.target
            .getAttribute(mutation.attributeName);}
            });attrObserver.observe(document, {
            subtree : true,
            attributes : true
            });var tagObserver = new MutationObserver(
            function(mutations, observer){
            for (var i = 0; i < mutations.length; i++){
            var mutation = mutations[i];var addedNodes = mutation.addedNodes;for (var j = 0; j < addedNodes.length; j++){
            var addedNode = addedNodes[j];}
            }
            });tagObserver.observe(document, {
            childList : true,
            subtree : true
            });});setInterval(function(){try{wffWS.send([]);}catch(e){wffWS.closeSocket();}},1000);""";

    @Test
    public void testGetAllOptimizedContent() {
        final int indexOfNodeId = expectedContent.indexOf("this.NODE_ID = \"");
        final int endOfNodeId = expectedContent.indexOf(";", indexOfNodeId);
        final String expectedContentDynamicValueAdjusted = expectedContent.replace(
                expectedContent.substring(indexOfNodeId, endOfNodeId + 1), "this.NODE_ID = \"xxxxxxxxxxxxxxx\";");
        final boolean losslessCommunication = true;
        final String onLossyCommunicationJS = losslessCommunication ? "location.reload()" : "";
        final String allContent = WffJsFile.getAllOptimizedContent("ws://webfirmframework.com",
                "instance-id-1234585-451", true, true, 1000, 2000, false, losslessCommunication,
                onLossyCommunicationJS);
        // just for modifying expectedContent after dev changes
//        assertEquals(expectedContent, allContent);
        assertEquals(expectedContentDynamicValueAdjusted, allContent
                .replace(allContent.substring(indexOfNodeId, endOfNodeId + 1), "this.NODE_ID = \"xxxxxxxxxxxxxxx\";"));
    }

}
