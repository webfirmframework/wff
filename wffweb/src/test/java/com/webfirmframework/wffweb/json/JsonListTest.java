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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webfirmframework.wffweb.InvalidValueException;
import com.webfirmframework.wffweb.wffbm.data.WffBMArray;
import com.webfirmframework.wffweb.wffbm.data.WffBMNumberArray;
import com.webfirmframework.wffweb.wffbm.data.WffBMObject;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;

public class JsonListTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testJsonListParse() {
        String json = """
                ["value1","value2"]
                """;
        JsonList parsed = JsonList.parse(json);
        Assert.assertNotNull(parsed);

        Assert.assertEquals(json.strip(), parsed.toJsonString());

    }

    @Test
    public void testJsonListParseEmptyJsonArray() throws JsonProcessingException {
        String json = """
                [     ]
                """;
        JsonList jsonList = JsonList.parse(json);
        Assert.assertNotNull(jsonList);
        Assert.assertEquals(objectMapper.readTree(json), objectMapper.readTree(jsonList.toJsonString()));
    }

    @Test
    public void testJsonLinkedListParse() {
        JsonLinkedList parsed = JsonLinkedList.parse("""
                ["value1", "value2"]
                """);
        Assert.assertNotNull(parsed);
    }

    @Test
    public void testJsonListWithMixedValueTypes() throws IOException {
        String json = """
                [null, 14, 300000.0004, 3000000003, 3000000002, null, true, false, " some string "]
                """;
        JsonList parsed = JsonList.parse(json);
        Assert.assertNotNull(parsed);
        Assert.assertNull(parsed.get(0));
        Assert.assertNull(parsed.get(5));
        Assert.assertEquals(" some string ", parsed.get(parsed.size() - 1));
        Assert.assertEquals(json.strip().replace(", ", ","), parsed.toJsonString());
    }

    @Test
    public void testJsonListWithNumberOrNullValuesOnly() throws IOException {
        String json = """
                [null, 14, 300000.0004, 3000000003, 3000000002, null]
                """;
        JsonList parsed = JsonList.parse(json);
        Assert.assertNotNull(parsed);
        Assert.assertNull(parsed.get(0));
        Assert.assertNull(parsed.get(parsed.size() - 1));

        Assert.assertEquals(json.strip().replace(" ", ""), parsed.toJsonString());

    }

    @Test
    public void testJsonListGetValueAs() {
        JsonList jsonList = JsonList.parse("""
                ["value1", "value2"]
                """);
        Assert.assertNotNull(jsonList);
        String valueAt0 = jsonList.getValueAsString(0);
        Assert.assertNotNull(valueAt0);
        Assert.assertEquals("value1", valueAt0);
    }

    @Test
    public void testJsonListToWffBMArrayStringArray() {
        JsonList jsonList = JsonList.parse("""
                ["value1", "value2"]
                """);
        Assert.assertNotNull(jsonList);
        WffBMArray wffBMArray = jsonList.toWffBMArray();
        Assert.assertNotNull(wffBMArray);
        String valueAt0 = wffBMArray.getValueAsString(0);
        Assert.assertNotNull(valueAt0);
        Assert.assertEquals("value1", valueAt0);

        String valueAt1 = wffBMArray.getValueAsString(1);
        Assert.assertNotNull(valueAt1);
        Assert.assertEquals("value2", valueAt1);
    }

    @Test
    public void testJsonListToWffBMArrayStringArray1() {
        JsonList jsonList = new JsonList();
        jsonList.add("value1");
        jsonList.add("value2");

        WffBMArray wffBMArray = jsonList.toWffBMArray();
        Assert.assertNotNull(wffBMArray);
        String valueAt0 = wffBMArray.getValueAsString(0);
        Assert.assertNotNull(valueAt0);
        Assert.assertEquals("value1", valueAt0);

        String valueAt1 = wffBMArray.getValueAsString(1);
        Assert.assertNotNull(valueAt1);
        Assert.assertEquals("value2", valueAt1);
    }

    @Test
    public void testJsonListToWffBMArrayIntegerArray() {
        JsonList jsonList = JsonList.parse("""
                [14, 1, 19]
                """);
        Assert.assertNotNull(jsonList);
        WffBMArray wffBMArray = jsonList.toWffBMArray();
        Assert.assertNotNull(wffBMArray);
        Assert.assertTrue(wffBMArray instanceof WffBMNumberArray<?>);
        Assert.assertEquals(14, wffBMArray.getValueAsInteger(0).intValue());
        Assert.assertEquals(1, wffBMArray.getValueAsInteger(1).intValue());
        Assert.assertEquals(19, wffBMArray.getValueAsInteger(2).intValue());
    }

    @Test
    public void testJsonListToWffBMArrayIntegerArray1() {
        JsonList jsonList = new JsonList();
        jsonList.add(14);
        jsonList.add(1);
        jsonList.add(19);

        WffBMArray wffBMArray = jsonList.toWffBMArray();
        Assert.assertNotNull(wffBMArray);
        Assert.assertTrue(wffBMArray instanceof WffBMNumberArray<?>);
        Assert.assertEquals(14, wffBMArray.getValueAsInteger(0).intValue());
        Assert.assertEquals(1, wffBMArray.getValueAsInteger(1).intValue());
        Assert.assertEquals(19, wffBMArray.getValueAsInteger(2).intValue());
    }


    @Test
    public void testJsonListToWffBMArrayLongArray() {
        JsonList jsonList = JsonList.parse("""
                [3000000005, 3000000004, 3000000003]
                """);
        Assert.assertNotNull(jsonList);
        WffBMArray wffBMArray = jsonList.toWffBMArray();
        Assert.assertNotNull(wffBMArray);
        Assert.assertTrue(wffBMArray instanceof WffBMNumberArray<?>);
        Assert.assertEquals(3000000005L, wffBMArray.getValueAsLong(0).longValue());
        Assert.assertEquals(3000000004L, wffBMArray.getValueAsLong(1).longValue());
        Assert.assertEquals(3000000003L, wffBMArray.getValueAsLong(2).longValue());
    }

    @Test
    public void testJsonListToWffBMArrayLongArray1() {
        JsonList jsonList = new JsonList();
        jsonList.add(3000000005L);
        jsonList.add(3000000004L);
        jsonList.add(3000000003L);

        WffBMArray wffBMArray = jsonList.toWffBMArray();
        Assert.assertNotNull(wffBMArray);
        Assert.assertTrue(wffBMArray instanceof WffBMNumberArray<?>);
        Assert.assertEquals(3000000005L, wffBMArray.getValueAsLong(0).longValue());
        Assert.assertEquals(3000000004L, wffBMArray.getValueAsLong(1).longValue());
        Assert.assertEquals(3000000003L, wffBMArray.getValueAsLong(2).longValue());
    }

    @Test
    public void testJsonListToWffBMArrayDoubleArray() {
        JsonList jsonList = JsonList.parse("""
                [14.01, 1.19, 11.9]
                """);
        Assert.assertNotNull(jsonList);
        WffBMArray wffBMArray = jsonList.toWffBMArray();
        Assert.assertNotNull(wffBMArray);
        Assert.assertTrue(wffBMArray instanceof WffBMNumberArray<?>);
        Assert.assertEquals(14.01D, wffBMArray.getValueAsDouble(0).doubleValue(), 0);
        Assert.assertEquals(1.19D, wffBMArray.getValueAsDouble(1).doubleValue(), 0);
        Assert.assertEquals(11.9D, wffBMArray.getValueAsDouble(2).doubleValue(), 0);
    }

    @Test
    public void testJsonListToWffBMArrayDoubleArray1() {
        JsonList jsonList = new JsonList();
        jsonList.add(14.01D);
        jsonList.add(1.19D);
        jsonList.add(11.9D);

        WffBMArray wffBMArray = jsonList.toWffBMArray();
        Assert.assertNotNull(wffBMArray);
        Assert.assertTrue(wffBMArray instanceof WffBMNumberArray<?>);
        Assert.assertEquals(14.01D, wffBMArray.getValueAsDouble(0).doubleValue(), 0);
        Assert.assertEquals(1.19D, wffBMArray.getValueAsDouble(1).doubleValue(), 0);
        Assert.assertEquals(11.9D, wffBMArray.getValueAsDouble(2).doubleValue(), 0);
    }


    @Test
    public void testJsonListToWffBMArrayBigDecimalArray() {
        JsonList jsonList = JsonList.parse("""
                [14.01, 1.19, 11.9]
                """);
        Assert.assertNotNull(jsonList);
        WffBMArray wffBMArray = jsonList.toWffBMArray();
        Assert.assertNotNull(wffBMArray);
        Assert.assertTrue(wffBMArray instanceof WffBMNumberArray<?>);
        Assert.assertEquals(14.01D, wffBMArray.getValueAsBigDecimal(0).doubleValue(), 0);
        Assert.assertEquals(1.19D, wffBMArray.getValueAsBigDecimal(1).doubleValue(), 0);
        Assert.assertEquals(11.9D, wffBMArray.getValueAsBigDecimal(2).doubleValue(), 0);
    }

    @Test
    public void testJsonListToWffBMArrayBigDecimalArray1() {
        JsonList jsonList = new JsonList();
        jsonList.add(new BigDecimal("14.01"));
        jsonList.add(new BigDecimal("1.19"));
        jsonList.add(new BigDecimal("11.9"));

        WffBMArray wffBMArray = jsonList.toWffBMArray();
        Assert.assertNotNull(wffBMArray);
        Assert.assertTrue(wffBMArray instanceof WffBMNumberArray<?>);
        Assert.assertEquals(14.01D, wffBMArray.getValueAsBigDecimal(0).doubleValue(), 0);
        Assert.assertEquals(1.19D, wffBMArray.getValueAsBigDecimal(1).doubleValue(), 0);
        Assert.assertEquals(11.9D, wffBMArray.getValueAsBigDecimal(2).doubleValue(), 0);
    }

    @Test(expected = InvalidValueException.class)
    public void testJsonListToWffBMArrayInvalidValueException() {
        JsonList jsonList = new JsonList();
        jsonList.add(new BigDecimal("14.01"));
        jsonList.add(new BigDecimal("1.19"));
        jsonList.add(null);
        jsonList.toWffBMArray();
    }

    @Test(expected = InvalidValueException.class)
    public void testJsonListToWffBMArrayInvalidValueException2() {
        JsonList jsonList = new JsonList();
        jsonList.add(new BigDecimal("14.01"));
        jsonList.add(true);
        jsonList.toWffBMArray();
    }

    @Test(expected = InvalidValueException.class)
    public void testJsonListToWffBMArrayInvalidValueException3() {
        JsonList jsonList = new JsonList();
        jsonList.add(new BigDecimal("14.01"));
        jsonList.add("true");
        jsonList.toWffBMArray();
    }

    @Test(expected = InvalidValueException.class)
    public void testJsonListToWffBMArrayInvalidValueException4() {
        JsonList jsonList = new JsonList();
        jsonList.add(14.01D);
        jsonList.add(true);
        jsonList.toWffBMArray();
    }

    @Test(expected = InvalidValueException.class)
    public void testJsonListToWffBMArrayInvalidValueException5() {
        JsonList jsonList = new JsonList();
        jsonList.add(14.01D);
        jsonList.add("true");
        jsonList.toWffBMArray();
    }

    @Test
    public void testJsonListToWffBMArrayEmptyArray() {
        JsonList jsonList = new JsonList();
        WffBMArray wffBMArray = jsonList.toWffBMArray();
        Assert.assertNotNull(wffBMArray);
        Assert.assertTrue(wffBMArray.isEmpty());
    }

    @Test
    public void testJsonMapToWffBMArrayWithWffBMObjectValue() {
        JsonList jsonListOuterMost = new JsonList();
        JsonMap jsonMap = new JsonMap();
        jsonMap.put("k1","value1");
        jsonMap.put("k2","value2");
        jsonMap.put("k3", null);
        jsonMap.put("k4",true);
        jsonMap.put("k5",false);
        jsonMap.put("k6", new BigDecimal("14.01"));
        jsonMap.put("k7", 14.01);

        jsonListOuterMost.add(jsonMap);

        WffBMArray wffBMArrayOuter = jsonListOuterMost.toWffBMArray();
        WffBMObject wffBMObject = wffBMArrayOuter.getValueAsWffBMObject(0);

        Assert.assertNotNull(wffBMObject);
        String valueAt0 = wffBMObject.getValueAsString("k1");
        Assert.assertNotNull(valueAt0);
        Assert.assertEquals("value1", valueAt0);

        String valueAt1 = wffBMObject.getValueAsString("k2");
        Assert.assertNotNull(valueAt1);
        Assert.assertEquals("value2", valueAt1);

        Assert.assertEquals(true, wffBMObject.getValueAsBoolean("k4"));
        Assert.assertEquals(false, wffBMObject.getValueAsBoolean("k5"));
        Assert.assertEquals(new BigDecimal("14.01").doubleValue(), wffBMObject.getValueAsBigDecimal("k6").doubleValue(), 0);
        Assert.assertEquals(new BigDecimal("14.01").doubleValue(), wffBMObject.getValueAsDouble("k7").doubleValue(), 0);
    }

    @Test
    public void testJsonMapToWffBMArrayWithWffBMArrayValue() {
        JsonList jsonListOuterMost = new JsonList();
        JsonList jsonList = new JsonList();
        jsonList.add(14.01D);
        jsonList.add(1.19D);
        jsonList.add(11.9D);

        jsonListOuterMost.add(jsonList);

        WffBMArray wffBMArrayOuter = jsonListOuterMost.toWffBMArray();
        WffBMArray wffBMArray = wffBMArrayOuter.getValueAsWffBMArray(0);
        Assert.assertNotNull(wffBMArray);
        Assert.assertTrue(wffBMArray instanceof WffBMNumberArray<?>);
        Assert.assertEquals(14.01D, wffBMArray.getValueAsDouble(0).doubleValue(), 0);
        Assert.assertEquals(1.19D, wffBMArray.getValueAsDouble(1).doubleValue(), 0);
        Assert.assertEquals(11.9D, wffBMArray.getValueAsDouble(2).doubleValue(), 0);

    }

}
