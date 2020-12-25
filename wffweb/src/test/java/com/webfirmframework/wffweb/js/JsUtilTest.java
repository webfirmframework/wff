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
package com.webfirmframework.wffweb.js;

import static org.junit.Assert.*;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.Test;

public class JsUtilTest {

    @Test
    public void testGetJsObjectForFieldsValue() {
        final Map<String, Object> jsKeyFieldIds = new LinkedHashMap<String, Object>();
        jsKeyFieldIds.put("username", "uId");
        jsKeyFieldIds.put("email",
                UUID.fromString("b2593ccc-2ab9-4cf8-818d-1f317a27a691"));
        jsKeyFieldIds.put("password", 555);

        assertEquals(
                "{username:document.getElementById('uId').value,email:document.getElementById('b2593ccc-2ab9-4cf8-818d-1f317a27a691').value,password:document.getElementById('555').value}",
                JsUtil.getJsObjectForFieldsValue(jsKeyFieldIds));
    }

    @Test
    public void testGetJsObjectForFieldsValueWithAlternativeFunction() {
        final Map<String, Object> jsKeyFieldIds = new LinkedHashMap<String, Object>();
        jsKeyFieldIds.put("username", "uId");
        jsKeyFieldIds.put("email",
                UUID.fromString("b2593ccc-2ab9-4cf8-818d-1f317a27a691"));
        jsKeyFieldIds.put("password", 555);

        String alternativeFunction = "gebi";

        assertEquals(
                "{username:gebi('uId').value,email:gebi('b2593ccc-2ab9-4cf8-818d-1f317a27a691').value,password:gebi('555').value}",
                JsUtil.getJsObjectForFieldsValue(jsKeyFieldIds,
                        alternativeFunction));
    }

    @Test
    public void testGetJsObjectForFieldsValueByElementIds() {
        final Set<Object> jsKeyFieldIds = new LinkedHashSet<Object>();
        jsKeyFieldIds.add("uId");
        jsKeyFieldIds
                .add(UUID.fromString("b2593ccc-2ab9-4cf8-818d-1f317a27a691"));
        jsKeyFieldIds.add(555);

        assertEquals(
                "{uId:document.getElementById('uId').value,b2593ccc-2ab9-4cf8-818d-1f317a27a691:document.getElementById('b2593ccc-2ab9-4cf8-818d-1f317a27a691').value,555:document.getElementById('555').value}",
                JsUtil.getJsObjectForFieldsValue(jsKeyFieldIds));
    }

    @Test
    public void testGetJsObjectForFieldsValueByElementIdsWithAltFun() {
        final Set<Object> jsKeyFieldIds = new LinkedHashSet<Object>();
        jsKeyFieldIds.add("uId");
        jsKeyFieldIds
                .add(UUID.fromString("b2593ccc-2ab9-4cf8-818d-1f317a27a691"));
        jsKeyFieldIds.add(555);

        assertEquals(
                "{uId:gebi('uId').value,b2593ccc-2ab9-4cf8-818d-1f317a27a691:gebi('b2593ccc-2ab9-4cf8-818d-1f317a27a691').value,555:gebi('555').value}",
                JsUtil.getJsObjectForFieldsValue(jsKeyFieldIds, "gebi"));
    }

    @Test
    public void testGetJsObjectForFieldsValueByElementIdsStrings() {
        assertEquals(
                "{uId:document.getElementById('uId').value,b2593ccc-2ab9-4cf8-818d-1f317a27a691:document.getElementById('b2593ccc-2ab9-4cf8-818d-1f317a27a691').value,555:document.getElementById('555').value}",
                JsUtil.getJsObjectForFieldsValue("uId",
                        "b2593ccc-2ab9-4cf8-818d-1f317a27a691", "555"));
    }

    @Test
    public void testGetJsObjectForFieldsValueByElementIdsStringsWithAltFun() {
        assertEquals(
                "{uId:gebi('uId').value,b2593ccc-2ab9-4cf8-818d-1f317a27a691:gebi('b2593ccc-2ab9-4cf8-818d-1f317a27a691').value,555:gebi('555').value}",
                JsUtil.getJsObjectForFieldsValueWithAltFun("gebi", "uId",
                        "b2593ccc-2ab9-4cf8-818d-1f317a27a691", "555"));
    }

    @Test
    public void testGetJsObjectForFieldsValueSetObjectSetObject()
            throws Exception {
        final Set<Object> jsInputKeyFieldIds = new LinkedHashSet<Object>();
        jsInputKeyFieldIds.add("usernameInputId");

        final Set<Object> jsCheckboxKeyFieldIds = new LinkedHashSet<Object>();
        jsCheckboxKeyFieldIds.add("dateExpiredCheckboxId");

        assertEquals(
                "{usernameInputId:document.getElementById('usernameInputId').value,dateExpiredCheckboxId:document.getElementById('dateExpiredCheckboxId').checked}",
                JsUtil.getJsObjectForFieldsValue(jsInputKeyFieldIds,
                        jsCheckboxKeyFieldIds));
    }

    @Test
    public void testGetJsObjectForFieldsValueSetObjectSetObjectWithAltFun()
            throws Exception {
        final Set<Object> jsInputKeyFieldIds = new LinkedHashSet<Object>();
        jsInputKeyFieldIds.add("usernameInputId");

        final Set<Object> jsCheckboxKeyFieldIds = new LinkedHashSet<Object>();
        jsCheckboxKeyFieldIds.add("dateExpiredCheckboxId");

        assertEquals(
                "{usernameInputId:gebi('usernameInputId').value,dateExpiredCheckboxId:gebi('dateExpiredCheckboxId').checked}",
                JsUtil.getJsObjectForFieldsValue(jsInputKeyFieldIds,
                        jsCheckboxKeyFieldIds, "gebi"));
    }
    
    @Test
    public void testToDynamicJs() throws Exception {
        assertEquals(2, "\r\n".length());
        assertEquals(2, "\r\n".toCharArray().length);
        assertEquals(2, "\r\n".codePoints().count());
        
        assertEquals("some js content \\n after new line \\n again after new line", 
                JsUtil.toDynamicJs("  some js content \r\n after new line \n again after new line   "));
        
        assertEquals("some js content \\n  \\n after new line \\n again after new line", 
                JsUtil.toDynamicJs("  some js content \r  \r\n after new line \n again after new line   "));
        
        assertEquals("some js content \\n  \\n after new line \\n again after new line", 
                JsUtil.toDynamicJs("  some js content \r  \r\n after new line \n again after new line   "));
        assertEquals("", 
                JsUtil.toDynamicJs(""));
        
        assertEquals("", 
                JsUtil.toDynamicJs("   \t \r\n \n"));
    }
}
