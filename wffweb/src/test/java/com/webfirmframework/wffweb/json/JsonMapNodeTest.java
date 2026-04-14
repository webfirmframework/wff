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

import java.math.BigDecimal;

public class JsonMapNodeTest {

    @Test
    public void testConvertTo() {
        class CustomJsonMap extends JsonLinkedMap {
        }
        class CustomList extends JsonList {

        }
        JsonLinkedMap jsonMap = new  JsonLinkedMap();
        jsonMap.put("number", new JsonValue("14", JsonValueType.NUMBER));
        jsonMap.put("string", new JsonValue("string value", JsonValueType.STRING));
        jsonMap.put("bool", new JsonValue("true", JsonValueType.BOOLEAN));
        jsonMap.put("fornull", new JsonValue());
        JsonList jsonList1 = new JsonList();
        jsonList1.add("one");
        jsonList1.add("two");
        jsonList1.add(3);
        jsonList1.add(true);
        jsonList1.add(null);
        jsonMap.put("jsonList1", jsonList1);

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


        CustomJsonMap convertedObject1 = jsonMap.convertTo(
                CustomJsonMap::new, CustomJsonMap::put,
                CustomList::new, CustomList::add, true);

        Assert.assertEquals(CustomList.class, convertedObject1.getValueAsJsonListNode("jsonList1").getClass());
        Assert.assertEquals(CustomList.class, convertedObject1.getValueAsJsonMapNode("subMap").getValueAsJsonListNode("jsonList2").getClass());

        Assert.assertEquals("{\"number\":14,\"string\":\"string value\",\"bool\":true,\"fornull\":null,\"jsonList1\":[\"one\",\"two\",3,true,null],\"subMap\":{\"subMapString\":\"string\",\"subMapBoolean\":true,\"subMapNumber\":1,\"jsonList2\":[\"one\",\"two\",3,true,null]}}", convertedObject1.toJsonString());
        Assert.assertEquals(CustomList.class, convertedObject1.getValueAsJsonListNode("jsonList1").getClass());

        Assert.assertTrue(convertedObject1.get("string") instanceof String);
        Assert.assertEquals("string value", convertedObject1.getValueAsString("string"));

        Assert.assertTrue(convertedObject1.get("number") instanceof Integer);
        Assert.assertEquals(new BigDecimal("14"), convertedObject1.getValueAsBigDecimal("number"));


        jsonMap.put("number", new JsonValue("2147483648", JsonValueType.NUMBER));
        convertedObject1 = jsonMap.convertTo(
                CustomJsonMap::new, CustomJsonMap::put,
                CustomList::new, CustomList::add, true);
        Assert.assertTrue(convertedObject1.get("number") instanceof Long);
        Assert.assertEquals(2147483648L, convertedObject1.getValueAsLong("number").longValue());
        Assert.assertEquals(new BigDecimal("2147483648"), convertedObject1.getValueAsBigDecimal("number"));

        jsonMap.put("number", new JsonValue("2147483648.1401", JsonValueType.NUMBER));
        convertedObject1 = jsonMap.convertTo(
                CustomJsonMap::new, CustomJsonMap::put,
                CustomList::new, CustomList::add, true);
        Assert.assertTrue(convertedObject1.get("number") instanceof BigDecimal);
        Assert.assertEquals(new BigDecimal("2147483648.1401"), convertedObject1.getValueAsBigDecimal("number"));
        Assert.assertEquals(new BigDecimal("2147483648.1401"), convertedObject1.getValueAsJsonValue("number").asBigDecimal());

        jsonMap.put("number", new JsonValue("14", JsonValueType.NUMBER));
        convertedObject1 = jsonMap.convertTo(
                CustomJsonMap::new, CustomJsonMap::put,
                CustomList::new, CustomList::add, true);

        Assert.assertTrue(convertedObject1.get("bool") instanceof Boolean);
        Assert.assertTrue(convertedObject1.getValueAsBoolean("bool"));

        Assert.assertNull(convertedObject1.get("fornull"));


        final CustomJsonMap convertedObject2 = jsonMap.convertTo(
                CustomJsonMap::new, CustomJsonMap::put,
                CustomList::new, CustomList::add, false);

        Assert.assertTrue(convertedObject2.get("string") instanceof JsonValue);
        Assert.assertEquals("string value", convertedObject2.getValueAsString("string"));
        Assert.assertEquals("string value", convertedObject2.getValueAsJsonValue("string").asString());

        Assert.assertTrue(convertedObject2.get("number") instanceof JsonValue);
        Assert.assertEquals(new BigDecimal("14"), convertedObject2.getValueAsBigDecimal("number"));
        Assert.assertEquals(new BigDecimal("14"), convertedObject2.getValueAsJsonValue("number").asBigDecimal());

        Assert.assertTrue(convertedObject2.get("bool") instanceof JsonValue);
        Assert.assertTrue(convertedObject2.getValueAsBoolean("bool"));
        Assert.assertTrue(convertedObject2.getValueAsJsonValue("bool").asBoolean());

        Assert.assertTrue(convertedObject2.get("fornull") instanceof JsonValue);
        Assert.assertNull(convertedObject1.getValueAsJsonValue("fornull").asString());
        Assert.assertNotNull(convertedObject2.get("fornull"));
    }

}
