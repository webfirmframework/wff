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
package com.webfirmframework.wffweb.json;

import org.junit.Assert;
import org.junit.Test;

public class JsonListNodeTest {

    @Test
    public void testConvertTo() {
        class CustomJsonMap extends JsonLinkedMap {
        }
        class CustomJsonList extends JsonList {
        }

        JsonList jsonList1 = new JsonList();
        jsonList1.add("one");
        jsonList1.add("two");
        jsonList1.add(3);
        jsonList1.add(true);
        jsonList1.add(null);

        JsonLinkedMap jsonMap = new  JsonLinkedMap();
        jsonMap.put("number", new JsonValue("14", JsonValueType.NUMBER));
        jsonMap.put("string", new JsonValue("string value", JsonValueType.STRING));
        jsonMap.put("bool", new JsonValue("true", JsonValueType.BOOLEAN));
        jsonMap.put("fornull", new JsonValue());
        jsonList1.add(jsonMap);

        JsonMap subMap = new JsonMap();
        subMap.put("subMapNumber", 1);
        subMap.put("subMapString", "string");
        subMap.put("subMapBoolean", true);
        jsonMap.put("subMap", subMap);

        JsonList jsonList2 = new JsonList();
        jsonList2.add("one");
        jsonList2.add("two");
        jsonList2.add(3);
        jsonList2.add(true);
        jsonList2.add(null);
        subMap.put("jsonList2", jsonList2);

        final CustomJsonList convertedObject1 = jsonList1.convertTo(
                CustomJsonMap::new, CustomJsonMap::put,
                CustomJsonList::new, CustomJsonList::add, true);

        Assert.assertEquals("""
                ["one","two",3,true,null,{"number":14,"string":"string value","bool":true,"fornull":null,"subMap":{"subMapString":"string","subMapBoolean":true,"subMapNumber":1,"jsonList2":["one","two",3,true,null]}}]
                """.trim(), convertedObject1.toJsonString());

        Assert.assertEquals(CustomJsonMap.class, convertedObject1.getValueAsJsonMapNode(5).getClass());

        Assert.assertEquals(CustomJsonMap.class, convertedObject1.getValueAsJsonMapNode(5).getValueAsJsonMapNode("subMap").getClass());
        Assert.assertEquals(CustomJsonList.class, convertedObject1.getValueAsJsonMapNode(5).getValueAsJsonMapNode("subMap").getValueAsJsonListNode("jsonList2").getClass());


        Assert.assertEquals("""
                {"number":14,"string":"string value","bool":true,"fornull":null,"subMap":{"subMapString":"string","subMapBoolean":true,"subMapNumber":1,"jsonList2":["one","two",3,true,null]}}""", convertedObject1.getValueAsJsonMapNode(5).toJsonString());


        Assert.assertEquals("""
                {"subMapString":"string","subMapBoolean":true,"subMapNumber":1,"jsonList2":["one","two",3,true,null]}""", convertedObject1.getValueAsJsonMapNode(5).getValueAsJsonMapNode("subMap").toJsonString());
        Assert.assertEquals("""
                ["one","two",3,true,null]""", convertedObject1.getValueAsJsonMapNode(5).getValueAsJsonMapNode("subMap").getValueAsJsonListNode("jsonList2").toJsonString());
    }

}
