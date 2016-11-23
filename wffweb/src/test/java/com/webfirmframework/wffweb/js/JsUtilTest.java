/*
 * Copyright 2014-2016 Web Firm Framework
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

import static org.junit.Assert.assertEquals;

import java.util.LinkedHashMap;
import java.util.Map;
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
                "{username:document.getElementById(\"uId\").value,email:document.getElementById(\"b2593ccc-2ab9-4cf8-818d-1f317a27a691\").value,password:document.getElementById(\"555\").value}",
                JsUtil.getJsObjectForFieldsValue(jsKeyFieldIds));
    }

}
