/*
 * Copyright 2014-2021 Web Firm Framework
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
            var wffId = -1;this.getUniqueWffIntId = function() {
            return ++wffId;};this.CPRSD_DATA = true;this.NDXD_TGS = ["#","@","a","b","i","p","q","s","u","br","dd","dl","dt","em","h1","h2","h3","h4","h5","h6","hr","li","ol","rp","rt","td","th","tr","ul","bdi","bdo","col","del","dfn","div","img","ins","kbd","map","nav","pre","qfn","sub","sup","svg","var","wbr","abbr","area","base","body","cite","code","data","form","head","html","line","link","main","mark","math","menu","meta","path","rect","ruby","samp","span","text","time","aside","audio","embed","input","label","meter","param","small","style","table","tbody","tfoot","thead","title","track","video","button","canvas","circle","dialog","figure","footer","header","hgroup","iframe","keygen","legend","object","option","output","script","select","source","strong","address","article","caption","details","ellipse","picture","polygon","section","summary","basefont","colgroup","datalist","fieldset","menuitem","noscript","optgroup","polyline","progress","template","textarea","blockquote","figcaption"];this.NDXD_ATRBS = ["id","alt","dir","for","low","max","min","rel","rev","src","cols","face","form","high","href","lang","list","loop","name","open","role","rows","size","step","type","wrap","align","async","class","color","defer","ismap","media","muted","nonce","oncut","scope","shape","sizes","style","title","value","width","accept","action","border","coords","height","hidden","method","nohref","onblur","oncopy","ondrag","ondrop","onload","onplay","onshow","poster","sorted","srcset","target","usemap","charset","checked","colspan","content","default","dirname","enctype","headers","onabort","onclick","onended","onerror","onfocus","oninput","onkeyup","onpaste","onpause","onreset","onwheel","optimum","pattern","preload","rowspan","sandbox","autoplay","controls","datetime","disabled","download","dropzone","hreflang","multiple","onchange","ononline","onresize","onscroll","onsearch","onseeked","onselect","onsubmit","ontoggle","onunload","readonly","required","reversed","selected","tabindex","accesskey","autofocus","draggable","maxlength","minlength","oncanplay","ondragend","onemptied","onfocusin","oninvalid","onkeydown","onmouseup","onoffline","onplaying","onseeking","onstalled","onstorage","onsuspend","onwaiting","translate","formaction","formmethod","formtarget","http-equiv","ondblclick","ondragover","onfocusout","onkeypress","onmouseout","onpagehide","onpageshow","onpopstate","onprogress","ontouchend","spellcheck","cellpadding","cellspacing","contextmenu","data-wff-id","formenctype","ondragenter","ondragleave","ondragstart","onloadstart","onmousedown","onmousemove","onmouseover","ontouchmove","placeholder","animationend","autocomplete","onafterprint","onhashchange","onloadeddata","onmouseenter","onmouseleave","onratechange","ontimeupdate","ontouchstart","onbeforeprint","oncontextmenu","ontouchcancel","transitionend","accept-charset","animationstart","formnovalidate","onbeforeunload","onvolumechange","contenteditable","oncanplaythrough","ondurationchange","onloadedmetadata","animationiteration"];this.NDXD_VNT_ATRBS = ["oncut","onblur","oncopy","ondrag","ondrop","onload","onplay","onshow","onabort","onclick","onended","onerror","onfocus","oninput","onkeyup","onpaste","onpause","onreset","onwheel","onchange","ononline","onresize","onscroll","onsearch","onseeked","onselect","onsubmit","ontoggle","onunload","oncanplay","ondragend","onemptied","onfocusin","oninvalid","onkeydown","onmouseup","onoffline","onplaying","onseeking","onstalled","onstorage","onsuspend","onwaiting","ondblclick","ondragover","onfocusout","onkeypress","onmouseout","onpagehide","onpageshow","onpopstate","onprogress","ontouchend","ondragenter","ondragleave","ondragstart","onloadstart","onmousedown","onmousemove","onmouseover","ontouchmove","onafterprint","onhashchange","onloadeddata","onmouseenter","onmouseleave","onratechange","ontimeupdate","ontouchstart","onbeforeprint","oncontextmenu","ontouchcancel","onbeforeunload","onvolumechange","oncanplaythrough","ondurationchange","onloadedmetadata"];this.NDXD_BLN_ATRBS = ["open","async","defer","ismap","hidden","checked","default","controls","disabled","multiple","readonly","reversed","selected"];this.taskValues = {INITIAL_WS_OPEN:0,INVOKE_ASYNC_METHOD:1,ATTRIBUTE_UPDATED:2,TASK:3,APPENDED_CHILD_TAG:4,REMOVED_TAGS:5,APPENDED_CHILDREN_TAGS:6,REMOVED_ALL_CHILDREN_TAGS:7,MOVED_CHILDREN_TAGS:8,INSERTED_BEFORE_TAG:9,INSERTED_AFTER_TAG:10,REPLACED_WITH_TAGS:11,REMOVED_ATTRIBUTES:12,ADDED_ATTRIBUTES:13,MANY_TO_ONE:14,ONE_TO_MANY:15,MANY_TO_MANY:16,ONE_TO_ONE:17,ADDED_INNER_HTML:18,INVOKE_POST_FUNCTION:19,EXEC_JS:20,RELOAD_BROWSER:21,RELOAD_BROWSER_FROM_CACHE:22,INVOKE_CALLBACK_FUNCTION:23,INVOKE_CUSTOM_SERVER_METHOD:24,TASK_OF_TASKS:25,COPY_INNER_TEXT_TO_VALUE:26,REMOVE_BROWSER_PAGE:27,SET_BM_OBJ_ON_TAG:28,SET_BM_ARR_ON_TAG:29,DEL_BM_OBJ_OR_ARR_FROM_TAG:30,CLIENT_PATHNAME_CHANGED:31,AFTER_SET_URI:32,SET_URI:33,SET_LS_ITEM:34,SET_LS_TOKEN:35,GET_LS_ITEM:36,REMOVE_LS_ITEM:37,REMOVE_LS_TOKEN:38,REMOVE_AND_GET_LS_ITEM:39,CLEAR_LS:40};this.uriEventInitiator = {SERVER_CODE:0,CLIENT_CODE:1,BROWSER:2,size:3};this.WS_URL = "ws://webfirmframework.com";this.INSTANCE_ID = "instance-id-1234585-451";this.NODE_ID = "ec19a8a5-8d25-4ac1-83f6-19b0845556d2";this.REMOVE_PREV_BP_ON_INITTAB = true;this.REMOVE_PREV_BP_ON_TABCLOSE = true;this.WS_RECON = 2000;if ((typeof TextEncoder) === "undefined") {
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
            var frz = ((typeof Object) !== "undefined" && Object.freeze) ? function(obj, d) {
            if (d) {
            for (var k in obj) {
            var v = obj[k];if (v && typeof v === "object") {
            frz(v, d);}
            }
            }
            return Object.freeze(obj);} : function(o, d) { };this.frz = frz;};wffGlobal.frz(wffGlobal, true);var wffBMUtil = new function(){

            this.f14 = function(v76){
            var v34 = 0;var v22 = 0;for (var i = 0; i < v76.length; i++){
            var name = v76[i].name;var values = v76[i].values;if(name.length > v34){
            v34 = name.length;}
            var v18 = 0;for (var j = 0; j < values.length; j++){
            v18 += values[j].length;}
            var v2 = f3(v18);var v4 = values.length
            * v2;var v3 = v18
            + v4;if(v3 > v22){
            v22 = v3;}
            }
            var v14 = f3(v34);var v12 = f3(v22);var v11 = [];v11.push(v14);v11.push(v12);for (var i = 0; i < v76.length; i++){
            var name = v76[i].name;var v43 = f29(name.length,
            v14);f34(v11, v43);f34(v11, name);var values = v76[i].values;if(values.length == 0){
            f34(v11, f29(0,
            v12));}else{
            var v72 = 0;for (var l = 0; l < values.length; l++){
            v72 += values[l].length;}
            v72 += (v12 * values.length);var v37 = f29(v72,
            v12);f34(v11, v37);for (var m = 0; m < values.length; m++){
            var value = values[m];v37 = f29(value.length,
            v12);f34(v11, v37);f34(v11, value);}
            }
            }
            return v11;};this.f10 = function(message){
            var v76 = [];var v10 = message[0];var v8 = message[1];for (var v57 = 2; v57 < message.length; v57++){
            var v84 = {};var v39 = subarray(message,
            v57, v10);v57 = v57 + v10;var v48 = f15(v39);var v85 = subarray(message, v57,
            v48);v57 = v57 + v85.length;v84.name = v85;var v35 = subarray(message,
            v57, v8);v57 = v57 + v8;v48 = f15(v35);var v77 = subarray(message, v57,
            v48);v57 = v57 + v77.length - 1;var v9 = f7(
            v8, v77);v84.values = v9;v76.push(v84);}
            return v76;};
            var f7 = function(v8, v77){
            var values = [];for (var i = 0; i < v77.length; i++){
            var v35 = subarray(v77, i,
            v8);var v66 = f15(v35);var value = subarray(v77, i
            + v8, v66);values.push(value);i = i + v8 + v66 - 1;}
            return values;};
            var f34 = function(v52, v40){
            for (var a = 0; a < v40.length; a++){
            v52.push(v40[a]);}
            };
            var f4 = function(v52, v78,
            position, length){
            var uptoIndex = position + length;for (var a = position; a < uptoIndex; a++){
            v52.push(v78[a]);}
            };this.f4 = f4;var subarray = function(srcAry, pos, len){
            var upto = pos + len;if(srcAry.slice){
            return srcAry.slice(pos, upto);}
            var sub = [];for (var i = pos; i < upto; i++){
            sub.push(srcAry[i]);}
            return sub;};this.subarray = subarray;
            var f39 = function(bytes){
            return bytes[0] << 24 | (bytes[1] & 0xFF) << 16
            | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);};
            var f40 = function(value){
            var bytes = [ (value >> 24), (value >> 16), (value >> 8), value ];return bytes;};
            var f15 = function(bytes){
            if(bytes.length == 4){
            return bytes[0] << 24 | (bytes[1] & 0xFF) << 16
            | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);}else if(bytes.length == 3){
            return (bytes[0] & 0xFF) << 16 | (bytes[1] & 0xFF) << 8
            | (bytes[2] & 0xFF);}else if(bytes.length == 2){
            return (bytes[0] & 0xFF) << 8 | (bytes[1] & 0xFF);}else if(bytes.length == 1){
            return (bytes[0] & 0xFF);}
            return bytes[0] << 24 | (bytes[1] & 0xFF) << 16
            | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);};this.f15 = f15;
            var f16 = function(value){
            var v65 = (value >> 24);var v80 = (value >> 16);var v67 = (value >> 8);var v71 = value;if(v65 == 0){
            if(v80 == 0){
            if(v67 == 0){
            return [ v71 ];}
            return [ v67, v71 ];}
            return [ v80, v67, v71 ];}
            return [ v65, v80, v67, v71 ];};this.f16 = f16;
            var f3 = function(value){
            var v65 = (value >> 24);var v80 = (value >> 16);var v67 = (value >> 8);var v71 = value;if(v65 == 0){
            if(v80 == 0){
            if(v67 == 0){
            return 1;}
            return 2;}
            return 3;}
            return 4;};
            var f29 = function(value, v53){
            var v65 = (value >> 24);var v80 = (value >> 16);var v67 = (value >> 8);var v71 = value;if(v53 == 1){
            return [ v71 ];}else if(v53 == 2){
            return [ v67, v71 ];}else if(v53 == 3){
            return [ v80, v67, v71 ];}
            return [ v65, v80, v67, v71 ];};
            var f33 = function(v62){
            var arrayBuff = new ArrayBuffer(8);var float64 = new Float64Array(arrayBuff);float64[0] = v62;var uin = new Int8Array(arrayBuff);return Array.from(uin).reverse();};this.f33 = f33;
            var f8 = function(bytes){
            var buffer = new ArrayBuffer(8);var int8Array = new Int8Array(buffer);for (var i = 0; i < bytes.length; i++){
            int8Array[i] = bytes[bytes.length - 1 - i];}
            return new Float64Array(buffer)[0];};this.f8 = f8;};wffGlobal.frz(wffBMUtil, false);
            var wffTagUtil = new function(){
            var encoder = wffGlobal.encoder;var decoder = wffGlobal.decoder;var f31 = function(utf8Bytes){
            return decoder.decode(new Uint8Array(utf8Bytes));};var subarray = wffBMUtil.subarray;var f5 = function(utf8Bytes){
            if(utf8Bytes.length == 1){
            var v95 = wffBMUtil.f15(utf8Bytes);return wffGlobal.NDXD_TGS[v95];}
            var v7 = utf8Bytes[0];if(v7 > 0){
            var v26 = subarray(utf8Bytes, 1, v7);var v95 = wffBMUtil.f15(v26);return wffGlobal.NDXD_TGS[v95];}else{
            var v60 = utf8Bytes.length - 1;var v59 = subarray(utf8Bytes, 1, v60);return f31(v59);}
            };this.f5 = f5;var getAttrNameFromCompressedBytes = function(utf8Bytes){
            if(utf8Bytes.length == 1){
            var v82 = wffBMUtil.f15(utf8Bytes);return wffGlobal.NDXD_ATRBS[v82];}
            var v5 = utf8Bytes[0];if(v5 > 0){
            var v19 = subarray(utf8Bytes, 1, v5);var v82 = wffBMUtil.f15(v19);return wffGlobal.NDXD_ATRBS[v82];}else{
            var v60 = utf8Bytes.length - 1;var v91Bytes = subarray(utf8Bytes, 1, v60);return f31(v91Bytes);}
            };this.getAttrNameFromCompressedBytes = getAttrNameFromCompressedBytes;var f1 = function(utf8Bytes){
            var v5 = utf8Bytes[0];if(v5 > 0){
            var v19 = subarray(utf8Bytes, 1, v5);var v82 = wffBMUtil.f15(v19);var v83 = utf8Bytes.length - (v5 + 1);var v68 = subarray(utf8Bytes, v5 + 1, v83);var attrNamVal = [wffGlobal.NDXD_ATRBS[v82], f31(v68)];return attrNamVal;}else{
            var v60 = utf8Bytes.length - 1;var v69 = subarray(utf8Bytes, 1, v60);return f32(f31(v69));}
            };this.f1 = f1;var f27 = function(tag, htmlString){
            var tmpDv = document.createElement('div');tmpDv.innerHTML = htmlString;for (var i = 0; i < tmpDv.childNodes.length; i++){
            var cn = tmpDv.childNodes[i];if(cn.nodeName === '#text'){
            tag.appendChild(document.createTextNode(cn.nodeValue));}else{
            tag.appendChild(cn.cloneNode(true));}
            }
            };this.f17 = function(tagName, v99){
            var elements = document.querySelectorAll(tagName + '[data-wff-id="'
            + v99 + '"]');if(elements.length > 1){
            wffLog('multiple tags with same wff id', 'tagName', 'v99', v99);}
            return elements[0];};this.f41 = function(v99){
            var elements = document.querySelectorAll('[data-wff-id="' + v99
            + '"]');if(elements.length > 1){
            wffLog('multiple tags with same wff id', 'v99', v99);}
            return elements[0];};
            this.f24 = function(v75){
            var v101 = f31([ v75[0] ]);var intBytes = [];for (var i = 1; i < v75.length; i++){
            intBytes.push(v75[i]);}
            var v100 = wffBMUtil.f15(intBytes);return v101 + v100;};this.f26 = function(tag){
            var v87 = tag.getAttribute("data-wff-id");if(v87 == null){
            v87 = "C" + wffGlobal.getUniqueWffIntId();tag.setAttribute("data-wff-id", v87);}
            var v101 = v87.substring(0, 1);var v51 = encoder.encode(v101);var v75 = [ v51[0] ];var v100 = v87.substring(1, v87.length);var v74 = wffBMUtil.f16(parseInt(v100));v75 = v75.concat(v74);return v75;};var f32 = function(v56){
            var v91;var v87;var v31 = v56.indexOf('=');if(v31 != -1){
            v91 = v56.substring(0, v31);v87 = v56.substring(v31 + 1,
            v56.length);}else{
            v91 = v56;v87 = '';}
            return [ v91, v87 ];};this.f32 = f32;this.f19 = function(bytes){
            var v76 = wffBMUtil.f10(bytes);var v16 = v76[0];var v29 = v16.values;var v96 = [];var parent;var v54 = f31(v29[0]);if(v54 === '#'){
            var text = f31(v29[1]);parent = document.createDocumentFragment();parent.appendChild(document.createTextNode(text));}else if(v54 === '@'){
            var text = f31(v29[1]);parent = document.createDocumentFragment();f27(parent, text);}else{
            parent = document.createElement(v54);for (var i = 1; i < v29.length; i++){
            var v56 = f32(f31(v29[i]));parent[v56[0]] = v56[1];parent.setAttribute(v56[0], v56[1]);}
            }
            v96.push(parent);for (var i = 1; i < v76.length; i++){
            var name = v76[i].name;var values = v76[i].values;var tagName = f31(values[0]);var child;if(tagName === '#'){
            var text = f31(values[1]);child = document.createDocumentFragment();child.appendChild(document.createTextNode(text));}else if(tagName === '@'){
            var text = f31(values[1]);child = document.createDocumentFragment();f27(child, text);}else{
            child = document.createElement(tagName);for (var j = 1; j < values.length; j++){
            var v56 = f32(f31(values[j]));child[v56[0]] = v56[1];child.setAttribute(v56[0], v56[1]);}
            }
            v96.push(child);var v61 = wffBMUtil.f15(name);v96[v61].appendChild(child);}
            return parent;};this.f2 = function(bytes){
            var v76 = wffBMUtil.f10(bytes);var v16 = v76[0];var v29 = v16.values;var v96 = [];var parent;var v54 = f5(v29[0]);if(v54 === '#'){
            var text = f31(v29[1]);parent = document.createDocumentFragment();parent.appendChild(document.createTextNode(text));}else if(v54 === '@'){
            var text = f31(v29[1]);parent = document.createDocumentFragment();f27(parent, text);}else{
            parent = document.createElement(v54);for (var i = 1; i < v29.length; i++){
            var v56 = f1(v29[i]);parent[v56[0]] = v56[1];parent.setAttribute(v56[0], v56[1]);}
            }
            v96.push(parent);for (var i = 1; i < v76.length; i++){
            var name = v76[i].name;var values = v76[i].values;var tagName = f5(values[0]);var child;if(tagName === '#'){
            var text = f31(values[1]);child = document.createDocumentFragment();child.appendChild(document.createTextNode(text));}else if(tagName === '@'){
            var text = f31(values[1]);child = document.createDocumentFragment();f27(child, text);}else{
            child = document.createElement(tagName);for (var j = 1; j < values.length; j++){
            var v56 = f1(values[j]);child[v56[0]] = v56[1];child.setAttribute(v56[0], v56[1]);}
            }
            v96.push(child);var v61 = wffBMUtil.f15(name);v96[v61].appendChild(child);}
            return parent;};if(wffGlobal.CPRSD_DATA){
            this.f19 = this.f2;}
            this.f22 = function(p, v20){
            var i = wffBMUtil.f15(v20);return p.childNodes[i];};};wffGlobal.frz(wffTagUtil, false);
            var wffBMCRUIDUtil = new function(){
            var encoder = wffGlobal.encoder;var decoder = wffGlobal.decoder;var encodedBytesForHash = encoder.encode('#');this.f18 = function(tag){
            var v76 = [];var tUtf8Bytes = encoder.encode("T");var cUtf8Bytes = encoder.encode("DT");var v46 = {
            'name' : tUtf8Bytes,
            'values' : [ cUtf8Bytes ]
            };v76.push(v46);var v90 = wffTagUtil.f26(tag);var v84 = {
            'name' : v90,
            'values' : []
            };v76.push(v84);return wffBMUtil.f14(v76);};this.f13 = function(v91, v87, v64){
            var v76 = [];var tUtf8Bytes = encoder.encode("T");var cUtf8Bytes = encoder.encode("UA");var v46 = {
            'name' : tUtf8Bytes,
            'values' : [ cUtf8Bytes ]
            };v76.push(v46);var v24 = encoder.encode(v91 + "="
            + v87);var v84 = {
            'name' : wffBMUtil.f16(v64),
            'values' : [ v24 ]
            };v76.push(v84);return wffBMUtil.f14(v76);};var f44 = function(v76, tag, v61){
            var v6 = v61 + 1;var nodeName = tag.nodeName;if(nodeName != '#text'){
            var values = [];var v55 = encoder.encode(nodeName);values.push(v55);for (var i = 0; i < tag.attributes.length; i++){
            var attribute = tag.attributes[i];var v13;if(attribute.value != null){
            v13 = encoder
            .encode(attribute.name + '=' + attribute.value);}else{
            v13 = encoder
            .encode(attribute.name);}
            values.push(v13);}
            var v84 = {
            'name' : wffBMUtil.f16(v61),
            'values' : values
            };v76.push(v84);}else{
            var v44 = encoder.encode(tag.nodeValue);var values = [ encodedBytesForHash, v44 ];var v84 = {
            'name' : wffBMUtil.f16(v61),
            'values' : values
            };v76.push(v84);}
            v61++;for (var i = 0; i < tag.childNodes.length; i++){
            f44(v76, tag.childNodes[i], v61);}
            };this.f20 = function(tag, v41){
            var v76 = [];var tUtf8Bytes = encoder.encode("T");var cUtf8Bytes = encoder.encode("C");var v46 = {
            'name' : tUtf8Bytes,
            'values' : [ cUtf8Bytes ]
            };v76.push(v46);var v61 = 0;f44(v76, tag, v61);v76[1].name = wffBMUtil.f16(v41);return wffBMUtil.f14(v76);};};wffGlobal.frz(wffBMCRUIDUtil, false);
            var wffClientCRUDUtil = new function(){
            var encoder = wffGlobal.encoder;var decoder = wffGlobal.decoder;var uriChangeQ = [];var f31 = function(utf8Bytes){
            return decoder.decode(new Uint8Array(utf8Bytes));};var f45 = function(v76){
            var v46 = v76[0];var taskValue = v46.values[0][0];if(taskValue == wffGlobal.taskValues.ATTRIBUTE_UPDATED){
            for (var i = 1; i < v76.length; i++){
            var v56 = wffTagUtil.f1(v76[i].name);var v91 = v56[0];var v87 = v56[1];var v98 = v76[i].values;for (var j = 0; j < v98.length; j++){
            var v99 = wffTagUtil.f24(v98[j]);var v49 = wffTagUtil.f41(v99);v49[v91] = v87;v49.setAttribute(v91, v87);}
            }
            }
            else if(taskValue == wffGlobal.taskValues.APPENDED_CHILD_TAG
            || taskValue == wffGlobal.taskValues.APPENDED_CHILDREN_TAGS){
            for (var i = 1; i < v76.length; i++){
            var v99 = wffTagUtil
            .f24(v76[i].name);var values = v76[i].values;var tagName = wffTagUtil.f5(values[0]);var parent = wffTagUtil.f17(tagName, v99);for (var j = 1; j < values.length; j++){
            var v89 = wffTagUtil
            .f19(values[j]);parent.appendChild(v89);}
            }
            }else if(taskValue == wffGlobal.taskValues.REMOVED_TAGS){
            for (var i = 1; i < v76.length; i++){
            var v99 = wffTagUtil
            .f24(v76[i].name);var values = v76[i].values;var tagName = wffTagUtil.f5(values[0]);var v63 = wffTagUtil.f17(tagName,
            v99);var parent = v63.parentNode;parent.removeChild(v63);}
            }else if(taskValue == wffGlobal.taskValues.REMOVED_ALL_CHILDREN_TAGS){
            for (var i = 1; i < v76.length; i++){
            var v99 = wffTagUtil
            .f24(v76[i].name);var values = v76[i].values;var tagName = wffTagUtil.f5(values[0]);var parentTag = wffTagUtil.f17(tagName,
            v99);while (parentTag.firstChild){
            parentTag.removeChild(parentTag.firstChild);}
            }
            }else if(taskValue == wffGlobal.taskValues.MOVED_CHILDREN_TAGS){
            for (var i = 1; i < v76.length; i++){
            var v25 = wffTagUtil
            .f24(v76[i].name);var values = v76[i].values;var v17 = wffTagUtil.f5(values[0]);var v33 = wffTagUtil.f17(
            v17, v25);var v92 = null;if(values[2].length == 0){
            v92 = wffTagUtil.f19(values[3]);}else{
            var v58 = wffTagUtil.f5(values[2]);var v73 = wffTagUtil.f24(values[1]);v92 = wffTagUtil.f17(v58,
            v73);if(typeof v92 !== 'undefined'){
            var previousParent = v92.parentNode;if(typeof previousParent !== 'undefined'){
            previousParent.removeChild(v92);}
            }else{
            v92 = wffTagUtil.f19(values[3]);}
            }
            v33.appendChild(v92);}
            }else if(taskValue == wffGlobal.taskValues.ADDED_ATTRIBUTES){
            for (var i = 1; i < v76.length; i++){
            var v84 = v76[i];if(v84.name[0] == wffGlobal.taskValues.MANY_TO_ONE){
            var tagName = wffTagUtil.f5(v84.values[0]);var v99 = wffTagUtil
            .f24(v84.values[1]);var v49 = wffTagUtil.f17(
            tagName, v99);for (var j = 2; j < v84.values.length; j++){
            var v56 = wffTagUtil.f1(v84.values[j]);var v91 = v56[0];var v87 = v56[1];v49[v91] = v87;v49.setAttribute(v91, v87);}
            }
            }
            }else if(taskValue == wffGlobal.taskValues.REMOVED_ATTRIBUTES){
            for (var i = 1; i < v76.length; i++){
            var v84 = v76[i];if(v84.name[0] == wffGlobal.taskValues.MANY_TO_ONE){
            var tagName = wffTagUtil.f5(v84.values[0]);var v99 = wffTagUtil
            .f24(v84.values[1]);var v49 = wffTagUtil.f17(
            tagName, v99);for (var j = 2; j < v84.values.length; j++){
            var v91 = wffTagUtil.getAttrNameFromCompressedBytes(v84.values[j]);v49.removeAttribute(v91);if(wffGlobal.NDXD_BLN_ATRBS.indexOf(v91) != -1){
            var prop = v49[v91];if(prop && prop === true){
            v49[v91] = false;}
            }
            }
            }
            }
            }else if(taskValue == wffGlobal.taskValues.ADDED_INNER_HTML){
            var tagName = wffTagUtil.f5(v76[1].name);var v99 = wffTagUtil
            .f24(v76[1].values[0]);var parentTag = wffTagUtil.f17(tagName,
            v99);while (parentTag.firstChild){
            parentTag.removeChild(parentTag.firstChild);}
            for (var i = 2; i < v76.length; i++){
            var values = v76[i].values;var v89 = wffTagUtil.f19(v76[i].name);if(values.length == 1 && values[0].length == 1){
            var existingTag = wffTagUtil.f17(
            v89.nodeName, v89
            .getAttribute("data-wff-id"));if(existingTag){
            var v21 = existingTag.parentNode;v21.removeChild(existingTag);}
            }
            parentTag.appendChild(v89);}
            }else if(taskValue == wffGlobal.taskValues.INSERTED_BEFORE_TAG){
            var tagName = wffTagUtil.f5(v76[1].name);var v99 = wffTagUtil.f24(v76[1].values[0]);var parentTag = wffTagUtil.f17(tagName, v99);var v50 = wffTagUtil.f5(v76[2].name);var v86;if(v50 === '#'){
            var v20 = v76[2].values[0];v86 = wffTagUtil.f22(parentTag, v20);}else{
            var v42 = wffTagUtil.f24(v76[2].values[0]);v86 = wffTagUtil.f17(v50,
            v42);}
            for (var i = 3; i < v76.length; i++){
            var nm = v76[i].name;var values = v76[i].values;var v89 = wffTagUtil.f19(values[0]);if(nm.length == 1){
            var existingTag = wffTagUtil.f17(
            v89.nodeName, v89
            .getAttribute("data-wff-id"));var v21 = existingTag.parentNode;v21.removeChild(existingTag);}
            parentTag.insertBefore(v89, v86);}
            }else if(taskValue == wffGlobal.taskValues.INSERTED_AFTER_TAG){
            var tagName = wffTagUtil.f5(v76[1].name);var v99 = wffTagUtil.f24(v76[1].values[0]);var parentTag = wffTagUtil.f17(tagName, v99);var v50 = wffTagUtil.f5(v76[2].name);var v86;if(v50 === '#'){
            var v20 = v76[2].values[0];v86 = wffTagUtil.f22(parentTag, v20);}else{
            var v42 = wffTagUtil.f24(v76[2].values[0]);v86 = wffTagUtil.f17(v50,
            v42);}
            var firstNd;for (var i = 3; i < v76.length; i++){
            var nm = v76[i].name;var values = v76[i].values;var v89 = wffTagUtil.f19(values[0]);if(nm.length == 1){
            var existingTag = wffTagUtil.f17(
            v89.nodeName, v89
            .getAttribute("data-wff-id"));var v21 = existingTag.parentNode;v21.removeChild(existingTag);}
            parentTag.insertBefore(v89, v86);if(!firstNd){
            firstNd = v89;}
            }
            if(firstNd){
            parentTag.removeChild(v86);parentTag.insertBefore(v86, firstNd);}
            }else if(taskValue == wffGlobal.taskValues.REPLACED_WITH_TAGS){
            var tagName = wffTagUtil.f5(v76[1].name);var v99 = wffTagUtil.f24(v76[1].values[0]);var parentTag = wffTagUtil.f17(tagName, v99);var v50 = wffTagUtil.f5(v76[2].name);var v86;if(v50 === '#'){
            var v20 = v76[2].values[0];v86 = wffTagUtil.f22(parentTag, v20);}else{
            var v42 = wffTagUtil.f24(v76[2].values[0]);v86 = wffTagUtil.f17(v50,
            v42);}
            for (var i = 3; i < v76.length; i++){
            var nm = v76[i].name;var values = v76[i].values;var v89 = wffTagUtil.f19(values[0]);if(nm.length == 1){
            var existingTag = wffTagUtil.f17(
            v89.nodeName, v89
            .getAttribute("data-wff-id"));var v21 = existingTag.parentNode;v21.removeChild(existingTag);}
            parentTag.insertBefore(v89, v86);}
            parentTag.removeChild(v86);}else if(taskValue == wffGlobal.taskValues.RELOAD_BROWSER){
            location.reload(true);}else if(taskValue == wffGlobal.taskValues.RELOAD_BROWSER_FROM_CACHE){
            location.reload();}else if(taskValue == wffGlobal.taskValues.EXEC_JS){
            var js = f31(v46.values[1]);var op = v46.values[2][0];if(op == 1){
            localStorage.setItem('WFF_EXEC_JS', JSON.stringify({ js: js, instanceId: wffGlobal.INSTANCE_ID }));localStorage.removeItem('WFF_EXEC_JS');}else{
            if(window.execScript){
            window.execScript(js);}else{
            eval(js);}
            }
            }else if(taskValue == wffGlobal.taskValues.SET_URI){
            var jsObj = new JsObjectFromBMBytes(v46.values[1], true);jsObj.uriAfter = jsObj.ua;jsObj.uriBefore = jsObj.ub;jsObj.origin = jsObj.o;jsObj.replace = jsObj.r;delete jsObj.ua;delete jsObj.ub;delete jsObj.o;delete jsObj.r;if(jsObj.uriAfter && jsObj.uriAfter !== jsObj.uriBefore){
            if(jsObj.origin === 'S'){
            jsObj.origin = 'server';jsObj.initiator = 'serverCode';}
            var vnt = {};for (k in jsObj){
            vnt[k] = jsObj[k];}
            if(jsObj.replace){
            history.replaceState({}, document.title, jsObj.uriAfter);}else{
            history.pushState({}, document.title, jsObj.uriAfter);}
            uriChangeQ.push(vnt);if(typeof wffGlobalListeners !== "undefined" && wffGlobalListeners.onSetURI && jsObj.origin === 'server'){
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
            var jsObj = new JsObjectFromBMBytes(v46.values[1], true);if(typeof localStorage !== "undefined" && jsObj.k && jsObj.wt && jsObj.id){
            var wt = parseInt(jsObj.wt);var prev = localStorage.getItem(jsObj.k + '_wff_data');var lstWT = 0;var lstId = 0;if(prev){
            try {
            var itemObj = JSON.parse(prev);lstWT = parseInt(itemObj.wt);lstId = itemObj.id;} catch (e){
            wffLog(e);}
            }
            if(wt >= lstWT){
            var id = wffBMUtil.f15(jsObj.id);if(wt > lstWT || (wt == lstWT && id > lstId)){
            var itemVal = JSON.stringify({ v: jsObj.v, wt: jsObj.wt, id: id });localStorage.setItem(jsObj.k + '_wff_data', itemVal);}
            }
            if(jsObj.cb){
            var v46 = wffTaskUtil.f35(
            wffGlobal.taskValues.TASK,
            taskValue);var v84 = {
            'name': jsObj.id,
            'values': []
            };var v76 = [v46, v84];var wffBM = wffBMUtil.f14(v76);wffWS.send(wffBM);}
            }
            }else if(taskValue == wffGlobal.taskValues.GET_LS_ITEM){
            var jsObj = new JsObjectFromBMBytes(v46.values[1], true);if(typeof localStorage !== "undefined" && jsObj.id && jsObj.k){
            var itemJSON = localStorage.getItem(jsObj.k + '_wff_data');var itemObj;if(itemJSON){
            try {
            itemObj = JSON.parse(itemJSON);} catch (e){
            wffLog(e);}
            }
            if(!itemObj || !itemObj.wt){
            itemObj = {};}
            var v46 = wffTaskUtil.f35(
            wffGlobal.taskValues.TASK,
            taskValue);var v84 = {
            'name': jsObj.id,
            'values': []
            };if(itemObj.wt){
            v84.values = [encoder.encode(itemObj.v), encoder.encode(itemObj.wt)];}
            var v76 = [v46, v84];var wffBM = wffBMUtil.f14(v76);wffWS.send(wffBM);}
            }else if(taskValue == wffGlobal.taskValues.REMOVE_LS_ITEM || taskValue == wffGlobal.taskValues.REMOVE_AND_GET_LS_ITEM){
            var jsObj = new JsObjectFromBMBytes(v46.values[1], true);if(typeof localStorage !== "undefined" && jsObj.k && jsObj.wt && jsObj.id){
            var itemJSON = localStorage.getItem(jsObj.k + '_wff_data');var itemObj;if(itemJSON){
            try {
            itemObj = JSON.parse(itemJSON);} catch (e){
            wffLog(e);}
            }
            if(!itemObj || !itemObj.wt || !itemObj.id){
            itemObj = {};}else{
            var jsObjWT = parseInt(jsObj.wt);var itemObjWT = parseInt(itemObj.wt);if(jsObjWT >= itemObjWT){
            var id = wffBMUtil.f15(jsObj.id);if(jsObjWT > itemObjWT || (jsObjWT == itemObjWT && id > itemObj.id)){
            localStorage.removeItem(jsObj.k + '_wff_data');}
            }
            }
            if(jsObj.cb){
            var v46 = wffTaskUtil.f35(
            wffGlobal.taskValues.TASK,
            taskValue);var v84 = {
            'name': jsObj.id,
            'values': []
            };if(taskValue == wffGlobal.taskValues.REMOVE_AND_GET_LS_ITEM && itemObj.wt){
            v84.values = [encoder.encode(itemObj.v), encoder.encode(itemObj.wt)];}
            var v76 = [v46, v84];var wffBM = wffBMUtil.f14(v76);wffWS.send(wffBM);}
            }
            }else if(taskValue == wffGlobal.taskValues.CLEAR_LS){
            var jsObj = new JsObjectFromBMBytes(v46.values[1], true);if(typeof localStorage !== "undefined" && jsObj.wt && jsObj.tp && jsObj.id){
            for (var k in localStorage){
            if((k.endsWith('_wff_data') && (jsObj.tp === 'D' || jsObj.tp === 'DT'))
            || (k.endsWith('_wff_token') && (jsObj.tp === 'T' || jsObj.tp === 'DT'))){
            try {
            var itemObj = JSON.parse(localStorage.getItem(k));if(itemObj && itemObj.wt && itemObj.id){
            var jsObjWT = parseInt(jsObj.wt);var itemObjWT = parseInt(itemObj.wt);if(jsObjWT >= itemObjWT){
            var id = wffBMUtil.f15(jsObj.id);if(jsObjWT > itemObjWT || (jsObjWT == itemObjWT && id > itemObj.id)){
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
            var v46 = wffTaskUtil.f35(
            wffGlobal.taskValues.TASK,
            taskValue);var v84 = {
            'name': jsObj.id,
            'values': []
            };var v76 = [v46, v84];var wffBM = wffBMUtil.f14(v76);wffWS.send(wffBM);}
            }
            }else if(taskValue == wffGlobal.taskValues.SET_LS_TOKEN){
            var jsObj = new JsObjectFromBMBytes(v46.values[1], true);if(typeof localStorage !== "undefined" && jsObj.k && jsObj.wt && jsObj.id){
            var tknNm = jsObj.k + '_wff_token';var wt = parseInt(jsObj.wt);var prev = localStorage.getItem(tknNm);var lstWT = 0;var lstId = 0;if(prev){
            try {
            var itemObj = JSON.parse(prev);lstWT = parseInt(itemObj.wt);lstId = itemObj.id;} catch (e){
            wffLog(e);}
            }
            if(wt >= lstWT){
            var id = wffBMUtil.f15(jsObj.id);if(wt > lstWT || (wt == lstWT && id > lstId)){
            var itemVal = JSON.stringify({ v: jsObj.v, wt: jsObj.wt, nid: wffGlobal.NODE_ID, id: id });localStorage.setItem(tknNm, itemVal);}
            }
            }
            }else if(taskValue == wffGlobal.taskValues.REMOVE_LS_TOKEN){
            var jsObj = new JsObjectFromBMBytes(v46.values[1], true);if(typeof localStorage !== "undefined" && jsObj.k && jsObj.wt && jsObj.id){
            var tknNm = jsObj.k + '_wff_token';var itemJSON = localStorage.getItem(tknNm);var itemObj;if(itemJSON){
            try {
            itemObj = JSON.parse(itemJSON);} catch (e){
            wffLog(e);}
            }
            if(itemObj && itemObj.wt && itemObj.id){
            var jsObjWT = parseInt(jsObj.wt);var itemObjWT = parseInt(itemObj.wt);if(jsObjWT >= itemObjWT){
            var id = wffBMUtil.f15(jsObj.id);if(jsObjWT >= itemObjWT || (jsObjWT == itemObjWT && id > itemObj.id)){
            itemObj.removed = true;itemObj.id = id;itemObj.wt = jsObj.wt;itemObj.nid = wffGlobal.NODE_ID;localStorage.setItem(tknNm, JSON.stringify(itemObj));localStorage.removeItem(tknNm);}
            }
            }
            }
            }else if(taskValue == wffGlobal.taskValues.COPY_INNER_TEXT_TO_VALUE){
            var tagName = wffTagUtil.f5(v76[1].name);var v99 = wffTagUtil
            .f24(v76[1].values[0]);var parentTag = wffTagUtil.f17(tagName,
            v99);var d = document.createElement('div');d.innerHTML = parentTag.outerHTML;parentTag.value = d.childNodes[0].innerText;}else if(taskValue == wffGlobal.taskValues.SET_BM_OBJ_ON_TAG
            || taskValue == wffGlobal.taskValues.SET_BM_ARR_ON_TAG){
            var tagName = wffTagUtil.f5(v76[1].name);var v99 = wffTagUtil
            .f24(v76[1].values[0]);var tag = wffTagUtil.f17(tagName, v99);var ky = f31(v76[1].values[1]);var v38 = v76[1].values[2];var jsObjOrArr;if(taskValue == wffGlobal.taskValues.SET_BM_OBJ_ON_TAG){
            jsObjOrArr = new JsObjectFromBMBytes(v38, true);}else{
            jsObjOrArr = new JsArrayFromBMBytes(v38, true);}
            var wffObjects = tag['wffObjects'];if(typeof wffObjects === 'undefined'){
            wffObjects = {};tag['wffObjects'] = wffObjects;}
            wffObjects[ky] = jsObjOrArr;}else if(taskValue == wffGlobal.taskValues.DEL_BM_OBJ_OR_ARR_FROM_TAG){
            var tagName = wffTagUtil.f5(v76[1].name);var v99 = wffTagUtil
            .f24(v76[1].values[0]);var tag = wffTagUtil.f17(tagName, v99);var ky = f31(v76[1].values[1]);var wffObjects = tag['wffObjects'];if(typeof wffObjects !== 'undefined'){
            delete wffObjects[ky];}
            }
            return true;};this.f43 = function(v79){
            var v76 = wffBMUtil.f10(v79);var v46 = v76[0];if(v46.name[0] == wffGlobal.taskValues.TASK){
            f45(v76);}else if(v46.name[0] == wffGlobal.taskValues.TASK_OF_TASKS){
            var tasksBM = v46.values;for (var i = 0; i < tasksBM.length; i++){
            var v45 = wffBMUtil.f10(tasksBM[i]);f45(v45);}
            }else{
            return false;}
            return true;};this.f30 = function(v79){
            var v84 = wffBMUtil.f10(v79)[1];};};wffGlobal.frz(wffClientCRUDUtil, false);
            var wffTaskUtil = new function (){
            var encoder = wffGlobal.encoder;this.f35 = function(v93, valueByte){
            var v46 = {
            'name' : [v93],
            'values' : [ [valueByte] ]
            };return v46;};};var wffServerMethods = new function (){
            var encoder = wffGlobal.encoder;var f25 = function(v70){
            var v88 = [];if(typeof v70 === 'string'){
            var ndx = wffGlobal.NDXD_VNT_ATRBS.indexOf(v70);if(ndx != -1){
            var ndxByts = wffBMUtil.f16(ndx);v88.push(0);for (var i = 0; i < ndxByts.length; i++){
            v88.push(ndxByts[i]);}
            }else{
            v88 = encoder.encode(v70);}
            }else{
            var ndxByts = wffBMUtil.f16(v70);v88.push(0);for (var i = 0; i < ndxByts.length; i++){
            v88.push(ndxByts[i]);}
            }
            return v88;};var f42 = function(event, tag, v70, prvntDflt){
            if(prvntDflt){
            event.preventDefault();}
            var v46 = wffTaskUtil.f35(wffGlobal.taskValues.TASK, wffGlobal.taskValues.INVOKE_ASYNC_METHOD);var v88 = f25(v70);var v84 = {'name':wffTagUtil.f26(tag), 'values':[v88]};var v76 = [v46, v84];var wffBM = wffBMUtil.f14(v76);wffWS.send(wffBM);};this.b = function(event, tag, v70){
            f42(event, tag, v70, true);};var invokeAsync = function(event, tag, v70){
            f42(event, tag, v70, false);};this.a = invokeAsync;var f23 = function(event, tag, v70, preFun, prvntDflt){
            if(prvntDflt){
            event.preventDefault();}
            var invoked = false;var actionPerform = function(){
            invoked = true;var v46 = wffTaskUtil.f35(wffGlobal.taskValues.TASK, wffGlobal.taskValues.INVOKE_ASYNC_METHOD);var v88 = f25(v70);var v84 = {'name':wffTagUtil.f26(tag), 'values':[v88]};var v76 = [v46, v84];var wffBM = wffBMUtil.f14(v76);wffWS.send(wffBM);};var action = new function(){
            this.perform = function(){
            actionPerform();};};if(preFun(event, tag, action)){
            if(!invoked){
            actionPerform();}
            }
            };this.f = function(event, tag, v70, preFun){
            f23(event, tag, v70, preFun, true);};var invokeAsyncWithPreFun = function(event, tag, v70, preFun){
            f23(event, tag, v70, preFun, false);};this.e = invokeAsyncWithPreFun;var f6 = function(event, tag, v70, preFun, filterFun, prvntDflt){
            if(prvntDflt){
            event.preventDefault();}
            var invoked = false;var actionPerform = function(){
            invoked = true;var v46 = wffTaskUtil.f35(wffGlobal.taskValues.TASK, wffGlobal.taskValues.INVOKE_ASYNC_METHOD);var v88 = f25(v70);var jsObject = filterFun(event, tag);var v32 = new WffBMObject(jsObject);var v94 = v32.getBMBytes();var v84 = {'name':wffTagUtil.f26(tag), 'values':[v88, v94]};var v76 = [v46, v84];var wffBM = wffBMUtil.f14(v76);wffWS.send(wffBM);};var action = new function(){
            this.perform = function(){
            actionPerform();};};if(preFun(event, tag, action)){
            if(!invoked){
            actionPerform();}
            }
            };this.h = function(event, tag, v70, preFun, filterFun){
            f6(event, tag, v70, preFun, filterFun, true);};var invokeAsyncWithPreFilterFun = function(event, tag, v70, preFun, filterFun){
            f6(event, tag, v70, preFun, filterFun, false);};this.g = invokeAsyncWithPreFilterFun;var f11 = function(event, tag, v70, filterFun, prvntDflt){
            if(prvntDflt){
            event.preventDefault();}
            var v46 = wffTaskUtil.f35(wffGlobal.taskValues.TASK, wffGlobal.taskValues.INVOKE_ASYNC_METHOD);var v88 = f25(v70);var jsObject = filterFun(event, tag);var v32 = new WffBMObject(jsObject);var v94 = v32.getBMBytes();var v84 = {'name':wffTagUtil.f26(tag), 'values':[v88, v94]};var v76 = [v46, v84];var wffBM = wffBMUtil.f14(v76);wffWS.send(wffBM);};this.d = function(event, tag, v70, filterFun){
            f11(event, tag, v70, filterFun, true);};var invokeAsyncWithFilterFun = function(event, tag, v70, filterFun){
            f11(event, tag, v70, filterFun, false);};this.c = invokeAsyncWithFilterFun;};var wffSM = wffServerMethods;wffGlobal.frz(wffTaskUtil, false);wffGlobal.frz(wffSM, false);
            var wffClientMethods = new function(){
            var f31 = function(utf8Bytes){
            return wffGlobal.decoder.decode(new Uint8Array(utf8Bytes));};this.exePostFun = function(v79){
            var v76 = wffBMUtil.f10(v79);var v46 = v76[0];var taskValue = v46.values[0][0];if(v46.name[0] != wffGlobal.taskValues.TASK){
            return false;}
            if(taskValue == wffGlobal.taskValues.INVOKE_POST_FUNCTION){
            var funString = f31(v76[1].name);var values = v76[1].values;var jsObject = null;if(values.length > 0){
            var value = values[0];if(value.length > 0){
            var bmObjBytes = v76[1].values[0];jsObject = new JsObjectFromBMBytes(bmObjBytes, true);}
            }
            var func;if(window.execScript){
            func = window.execScript("(function(jsObject){" + funString
            + "})");}else{
            func = eval("(function(jsObject){" + funString + "})");}
            func(jsObject);}else if(taskValue == wffGlobal.taskValues.INVOKE_CALLBACK_FUNCTION){
            var funKey = f31(v76[1].name);var cbFun = wffAsync.v27[funKey];var values = v76[1].values;var jsObject = null;if(values.length > 0){
            var value = values[0];if(value.length > 0){
            var bmObjBytes = v76[1].values[0];jsObject = new JsObjectFromBMBytes(bmObjBytes, true);}
            }
            cbFun(jsObject);delete wffAsync.v27[funKey];}else{
            return false;}
            return true;};};wffGlobal.frz(wffClientMethods, false);
            var f36 = function(valueType){
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
            var v76 = [];if(typeof outer === 'undefined' || outer){
            var typeNameValue = {
            name : [ 1 ],
            values : []
            };v76.push(typeNameValue);}
            if(jsArray.length > 0){
            var arrayValType;for (var i = 0; i < jsArray.length; i++){
            arrayValType = f36(Object.prototype.toString
            .call(jsArray[i]));if(arrayValType != 2 && arrayValType != 3){
            break;}
            }
            var v84 = {
            name : [ arrayValType ]
            };v76.push(v84);var values = [];v84.values = values;if(arrayValType == 0){
            for (var i = 0; i < jsArray.length; i++){
            if(arrayValType != 2 && arrayValType != 3){
            values.push(encoder.encode(jsArray[i]));}else{
            values.push([]);}
            }
            }else if(arrayValType == 1){
            for (var i = 0; i < jsArray.length; i++){
            if(arrayValType != 2 && arrayValType != 3){
            values.push(wffBMUtil.f33(jsArray[i]));}else{
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
            return wffBMUtil.f14(v76);};this.getBMBytes = function getBMBytes(){
            return getWffBMArray(this.jsArray, this.outer);};return this;};var WffBMByteArray = function(int8Array, outer){
            var encoder = wffGlobal.encoder;var decoder = wffGlobal.decoder;this.jsArray = int8Array;this.outer = outer;var getWffBMByteArray = function(int8Array, outer){
            var v76 = [];if(typeof outer === 'undefined' || outer){
            var typeNameValue = {
            name : [ 1 ],
            values : []
            };v76.push(typeNameValue);}
            var arrayValType = 10;var v84 = {
            name : [ arrayValType ]
            };v76.push(v84);v84.values = [ int8Array ];return wffBMUtil.f14(v76);};this.getBMBytes = function getBMBytes(){
            return getWffBMByteArray(this.jsArray, this.outer);};return this;};var WffBMObject = function(jsObject, outer){
            var encoder = wffGlobal.encoder;var decoder = wffGlobal.decoder;this.jsObject = jsObject;this.outer = outer;var getWffBMObject = function(jsObj, outer){
            var jsObjType = Object.prototype.toString.call(jsObj);var v76 = [];if(typeof outer === 'undefined' || outer){
            var typeNameValue = {
            name : [ 0 ],
            values : []
            };v76.push(typeNameValue);}
            for ( var k in jsObj){
            var value = jsObj[k];var valType = f36(Object.prototype.toString
            .call(value));var v84 = {};v84.name = encoder.encode(k);var values = [ [ valType ] ];v84.values = values;var typeByte = [ valType ];if(valType == 0){
            values.push(encoder.encode(value));}else if(valType == 1){
            values.push(wffBMUtil.f33(value));}else if(valType == 2){
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
            v76.push(v84);}
            return wffBMUtil.f14(v76);};this.getBMBytes = function getBMBytes(){
            return getWffBMObject(this.jsObject, this.outer);};};var JsObjectFromBMBytes = function(v79, outer){
            var encoder = wffGlobal.encoder;var decoder = wffGlobal.decoder;var f31 = function(utf8Bytes){
            return wffGlobal.decoder.decode(new Uint8Array(utf8Bytes));};var v76 = wffBMUtil.f10(v79);var i = 0;if(typeof outer === 'undefined' || outer){
            i = 1;}
            for (; i < v76.length; i++){
            var v84 = v76[i];var name = f31(v84.name);var values = v84.values;if(values[0] == 0){
            this[name] = f31(values[1]);}else if(values[0] == 1){
            this[name] = wffBMUtil.f8(values[1]);}else if(values[0] == 2){
            this[name] = undefined;}else if(values[0] == 3){
            this[name] = null;}else if(values[0] == 4){
            this[name] = values[1] == 1 ? true : false;}else if(values[0] == 5){
            this[name] = new JsObjectFromBMBytes(values[1], false);}else if(values[0] == 6){
            this[name] = new JsArrayFromBMBytes(values[1], false);}else if(values[0] == 7){
            this[name] = new RegExp(f31(values[1]));}else if(values[0] == 8){
            if(window.execScript){
            this[name] = window.execScript("("
            + f31(values[1]) + ")");}else{
            this[name] = eval("(" + f31(values[1]) + ")");}
            }else if(values[0] == 9){
            this[name] = new JsArrayFromBMBytes(values[1], false);}else if(values[0] == 10){
            this[name] = new Int8Array(values[1]);}
            }
            return this;};var JsArrayFromBMBytes = function(v79, outer){
            var encoder = wffGlobal.encoder;var decoder = wffGlobal.decoder;var f31 = function(utf8Bytes){
            return decoder.decode(new Uint8Array(utf8Bytes));};var v76 = wffBMUtil.f10(v79);var i = 0;if(typeof outer === 'undefined' || outer){
            i = 1;}
            var v84 = v76[i];var dataType = v84.name[0];var values = v84.values;var jsArray = [];if(dataType == 0){
            for (var j = 0; j < values.length; j++){
            jsArray.push(f31(values[j]));}
            }else if(dataType == 1){
            for (var j = 0; j < values.length; j++){
            jsArray.push(wffBMUtil.f8(values[j]));}
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
            jsArray.push(new RegExp(f31(values[j])));}
            }else if(dataType == 8){
            for (var j = 0; j < values.length; j++){
            var fun = f31(values[j]);var ary;if(window.execScript){
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
            return (++uuid).toString();};this.serverMethod = function(v81, jsObject){
            this.invoke = function(callback){
            var v47;if(typeof callback === "function"){
            v47 = wffAsync.generateUUID();wffAsync.v27[v47] = callback;}else if(typeof callback === "undefined"){
            }else{
            throw "invoke function takes function argument";}
            var v46 = wffTaskUtil.f35(
            wffGlobal.taskValues.TASK,
            wffGlobal.taskValues.INVOKE_CUSTOM_SERVER_METHOD);var v36 = encoder.encode(v81);var values = [];if(typeof jsObject !== "undefined"){
            if(typeof jsObject === "object"){
            var v32 = new WffBMObject(jsObject);var v94 = v32.getBMBytes();values.push(v94);}else{
            throw "argument value should be an object";}
            }
            var v84 = {
            'name' : v36,
            'values' : values
            };var v76 = [ v46, v84 ];if(typeof v47 !== "undefined"){
            var v15 = {
            'name' : encoder.encode(v47),
            'values' : []
            };v76.push(v15);}
            var wffBM = wffBMUtil.f14(v76);wffWS.send(wffBM);};return this;};var f9 = function(uri, callback, initiator, rplc){
            var v46 = wffTaskUtil.f35(
            wffGlobal.taskValues.TASK,
            wffGlobal.taskValues.CLIENT_PATHNAME_CHANGED);var v84 = {
            'name': encoder.encode(uri),
            'values': []
            };var v97 = rplc ? 1 : 0;if(typeof initiator !== "undefined" && initiator >= 0 && initiator < wffGlobal.uriEventInitiator.size){
            v84.values.push([initiator, v97]);}else{
            v84.values.push([wffGlobal.uriEventInitiator.CLIENT_CODE, v97]);}
            var v76 = [v46, v84];if(callback){
            var v47 = wffAsync.generateUUID();wffAsync.v27[v47] = callback;var v15 = {
            'name': encoder.encode(v47),
            'values': []
            };v76.push(v15);}
            var wffBM = wffBMUtil.f14(v76);wffWS.send(wffBM);};this.setServerURIWithCallback = function(uri, callback, initiator){f9(uri, callback, initiator, false);};this.setServerURI = function(uri){ f9(uri, undefined, wffGlobal.uriEventInitiator.CLIENT_CODE, false); };var throwInvalidSetURIArgException = function(){
            throw "Invalid argument found in setURI function call. " +
            "Eg: wffAsync.setURI('/sampleuri', function(e){}, function(e){}, false);, " +
            "wffAsync.setURI('/sampleuri', function(e){}, function(e){});, " +
            "wffAsync.setURI('/sampleuri', function(e){});, " +
            "wffAsync.setURI('/sampleuri'); or " +
            "wffAsync.setURI('/sampleuri', null, null, false);";};this.setURI = function(uri, onSetURI, afterSetURI, replace){
            if(typeof onSetURI !== "undefined" && onSetURI !== null && typeof onSetURI !== "function"){
            throwInvalidSetURIArgException();}
            if(typeof afterSetURI !== "undefined" && afterSetURI !== null && typeof afterSetURI !== "function"){
            throwInvalidSetURIArgException();}
            if(typeof replace !== "undefined" && replace !== null && typeof replace !== "boolean"){
            throwInvalidSetURIArgException();}
            var uriBefore = window.location.pathname;if(replace){
            history.replaceState({}, document.title, uri);}else{
            history.pushState({}, document.title, uri);}
            var uriAfter = window.location.pathname;if(uriBefore !== uriAfter){
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
            f9(uriAfter, callbackWrapper, wffGlobal.uriEventInitiator.CLIENT_CODE, replace);}
            };};wffGlobal.frz(window.wffAsync, false);
            var wffBMClientEvents = new function(){
            window.v1 = false;var encoder = wffGlobal.encoder;var decoder = wffGlobal.decoder;var f28 = function(wffInstanceId){
            var v76 = [];var v23 = encoder.encode(wffInstanceId);var tnv = wffTaskUtil.f35(
            wffGlobal.taskValues.TASK,
            wffGlobal.taskValues.REMOVE_BROWSER_PAGE);v76.push(tnv);var v84 = {
            'name' : v23,
            'values' : []
            };v76.push(v84);var wffBM = wffBMUtil.f14(v76);wffWS.send(wffBM);};this.f28 = f28;this.f21 = function(){
            if(!wffGlobal.REMOVE_PREV_BP_ON_INITTAB ||
            window.v1){
            return;}
            window.v1 = true;var wffInstanceId = sessionStorage.getItem('WFF_INSTANCE_ID');if(typeof wffInstanceId !== "undefined"
            && wffInstanceId !== wffGlobal.INSTANCE_ID){
            f28(wffInstanceId);}
            sessionStorage.setItem('WFF_INSTANCE_ID', wffGlobal.INSTANCE_ID);};this.f38 = function(){
            var v76 = [];var tnv = wffTaskUtil.f35(
            wffGlobal.taskValues.TASK,
            wffGlobal.taskValues.INITIAL_WS_OPEN);v76.push(tnv);if(window.location && window.location.pathname){
            var pthNV = {
            'name': [wffGlobal.taskValues.SET_URI],
            'values': [[wffGlobal.uriEventInitiator.BROWSER], encoder.encode(window.location.pathname)]
            };v76.push(pthNV);}
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
            };v76.push(nv);}
            }
            var wffBM = wffBMUtil.f14(v76);wffWS.send(wffBM);};};wffGlobal.frz(wffBMClientEvents, false);
            var wffWS = new function(){
            var encoder = wffGlobal.encoder;var decoder = wffGlobal.decoder;var prevIntvl = null;var webSocket = null;var inDataQ = [];var sendQData = null;this.openSocket = function(wsUrl){
            if(webSocket !== null
            && webSocket.readyState !== WebSocket.CLOSED
            && webSocket.readyState !== WebSocket.CLOSING){
            return;}
            webSocket = new WebSocket(wsUrl);sendQData = function(){
            if(webSocket !== null && webSocket.readyState === WebSocket.OPEN){
            var inData = [];var ndx = 0;var xp = false;for (ndx = 0; ndx < inDataQ.length; ndx++){
            var each = inDataQ[ndx];if(!xp){
            try {
            webSocket.send(new Int8Array(each).buffer);} catch(e){
            xp = true;inData.push(each);}
            }else{
            inData.push(each);}
            }
            inDataQ = inData;}
            };webSocket.binaryType = 'arraybuffer';webSocket.onopen = function(event){
            try {
            if(prevIntvl !== null){
            clearInterval(prevIntvl);prevIntvl = null;}
            wffBMClientEvents.f21();if(sendQData !== null){
            sendQData();}
            if(typeof event.data === 'undefined'){
            return;}
            var binary = new Int8Array(event.data);if(binary.length < 4){
            return;}
            wffClientCRUDUtil.f43(binary);}catch(e){
            wffLog(e);}
            };webSocket.onmessage = function(event){
            try {
            var binary = new Int8Array(event.data);if(binary.length < 4){
            return;}
            var executed = wffClientMethods.exePostFun(binary);if(!executed){
            wffClientCRUDUtil.f43(binary);}
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
            inDataQ.push(bytes);if(sendQData !== null){
            sendQData();}
            }else{
            webSocket.send(new Int8Array(bytes).buffer);}
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
            return -1;};};wffGlobal.frz(wffWS, false);
            document.addEventListener("DOMContentLoaded",
            function(event){
            var encoder = wffGlobal.encoder;if(typeof window.f38Invoked === 'undefined'){
            window.f38Invoked = true;wffBMClientEvents.f38();}
            wffWS.openSocket(wffGlobal.WS_URL);window.v30 = false;var f37 = function(){
            if(!window.v30){
            wffBMClientEvents.f28(sessionStorage.getItem('WFF_INSTANCE_ID'));}
            window.v30 = true;};var f12 = function (eventName){
            var el = window;eventName = 'on' + eventName;var isSupported = (eventName in el);if(!isSupported && typeof InstallTrigger !== 'undefined'){
            el.setAttribute(eventName, 'return;');isSupported = typeof el[eventName] == 'function';}
            el = null;return isSupported;};if(f12('beforeunload')){
            window.addEventListener("beforeunload", f37, false);}
            if(f12('unload')){
            window.addEventListener("unload", f37, false);}
            if(f12('popstate')){
            window.addEventListener('popstate', function(event){
            var uriAfter = window.location.pathname;if(typeof wffGlobalListeners !== "undefined"){
            var wffEvent = { uriAfter: uriAfter, origin: "client", initiator: 'browser' };if(wffGlobalListeners.onSetURI){
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
            });}
            if(f12('storage') && typeof localStorage !== "undefined"){
            window.addEventListener('storage', function(event){
            if(event && event.key && event.newValue){
            var ndx = event.key.lastIndexOf('_wff_token')
            if(ndx > -1 && event.key.endsWith('_wff_token')){
            var itemObj;try {
            itemObj = JSON.parse(event.newValue);} catch (e){
            wffLog(e);}
            if(itemObj && itemObj.id && itemObj.wt && itemObj.nid !== wffGlobal.NODE_ID){
            var ky = encoder.encode(event.key.substring(0, ndx));var wtBts = encoder.encode(itemObj.wt);var id = wffBMUtil.f16(itemObj.id);var v76;if(itemObj.removed){
            var v46 = wffTaskUtil.f35(
            wffGlobal.taskValues.TASK,
            wffGlobal.taskValues.REMOVE_LS_TOKEN);var v84 = {
            'name': id,
            'values': [ky, wtBts]
            };v76 = [v46, v84];}else{
            var v46 = wffTaskUtil.f35(
            wffGlobal.taskValues.TASK,
            wffGlobal.taskValues.SET_LS_TOKEN);var v84 = {
            'name': id,
            'values': [ky, encoder.encode(itemObj.v), wtBts]
            };v76 = [v46, v84];}
            var wffBM = wffBMUtil.f14(v76);wffWS.send(wffBM);}
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
            var v56s = {};for (var i = 0; i < mutations.length; i++){
            var mutation = mutations[i];v56s[mutation.attributeName] = mutation.target
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
        final String allContent = WffJsFile.getAllOptimizedContent("ws://webfirmframework.com",
                "instance-id-1234585-451", true, true, 1000, 2000, false);
        // just for modifying expectedContent after dev changes
//        assertEquals(expectedContent, allContent);
        assertEquals(expectedContentDynamicValueAdjusted, allContent
                .replace(allContent.substring(indexOfNodeId, endOfNodeId + 1), "this.NODE_ID = \"xxxxxxxxxxxxxxx\";"));
    }

}
