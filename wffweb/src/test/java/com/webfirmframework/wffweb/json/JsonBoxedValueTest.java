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

import com.webfirmframework.wffweb.InvalidValueException;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

public class JsonBoxedValueTest {

    @Test
    public void testCreate() {
        JsonBoxedValue jsonBoxedValue = JsonBoxedValue.create(new JsonMap());

        Assert.assertNotNull(jsonBoxedValue);
        Assert.assertNotNull(jsonBoxedValue.jsonMapNode());
        Assert.assertNull(jsonBoxedValue.value());
        Assert.assertNull(jsonBoxedValue.jsonListNode());

        jsonBoxedValue = JsonBoxedValue.create(new JsonList());

        Assert.assertNotNull(jsonBoxedValue);
        Assert.assertNotNull(jsonBoxedValue.jsonListNode());
        Assert.assertNull(jsonBoxedValue.value());
        Assert.assertNull(jsonBoxedValue.jsonMapNode());

        jsonBoxedValue = JsonBoxedValue.create(new BigDecimal("1401"));

        Assert.assertNotNull(jsonBoxedValue);
        Assert.assertNotNull(jsonBoxedValue.value());
        Assert.assertNull(jsonBoxedValue.jsonListNode());
        Assert.assertNull(jsonBoxedValue.jsonMapNode());
    }

    @Test
    public void testJsonBoxedValueByGetValueFromMap() {
        JsonMap jsonMap = new JsonMap();
        jsonMap.put("key1", "value1");
        jsonMap.put("key2", new BigDecimal("14.01"));

        JsonMap jsonMap1 = new JsonMap();
        jsonMap1.put("key11", "value1");
        jsonMap1.put("key12", new BigDecimal("14.01"));
        jsonMap.put("key3", jsonMap1);

        JsonList jsonList1 = new JsonList();
        jsonList1.add("value1");
        jsonList1.add(new BigDecimal("14.01"));
        jsonMap.put("key4", jsonList1);

        {
            JsonBoxedValue jsonBoxedValue = jsonMap.getValue("key1");
            Assert.assertNotNull(jsonBoxedValue);
            Assert.assertEquals("value1", jsonBoxedValue.asString());

            jsonBoxedValue = jsonMap.getValue("key2");
            Assert.assertEquals(new BigDecimal("14.01"), jsonBoxedValue.asBigDecimal());

            jsonBoxedValue = jsonMap.getValue("key3");
            Assert.assertEquals("value1", jsonBoxedValue.getValue("key11").asString());
            Assert.assertEquals(new BigDecimal("14.01"), jsonBoxedValue.getValue("key12").asBigDecimal());

            jsonBoxedValue = jsonMap.getValue("key4");
            Assert.assertEquals("value1", jsonBoxedValue.getValue(0).asString());
            Assert.assertEquals(new BigDecimal("14.01"), jsonBoxedValue.getValue(1).asBigDecimal());
        }
        jsonMap = JsonMap.parse(jsonMap.toJsonString());
        {
            JsonBoxedValue jsonBoxedValue = jsonMap.getValue("key1");
            Assert.assertNotNull(jsonBoxedValue);
            Assert.assertEquals("value1", jsonBoxedValue.asString());

            jsonBoxedValue = jsonMap.getValue("key2");
            Assert.assertEquals(new BigDecimal("14.01"), jsonBoxedValue.asBigDecimal());

            jsonBoxedValue = jsonMap.getValue("key3");
            Assert.assertEquals("value1", jsonBoxedValue.getValue("key11").asString());
            Assert.assertEquals(new BigDecimal("14.01"), jsonBoxedValue.getValue("key12").asBigDecimal());

            jsonBoxedValue = jsonMap.getValue("key4");
            Assert.assertEquals("value1", jsonBoxedValue.getValue(0).asString());
            Assert.assertEquals(new BigDecimal("14.01"), jsonBoxedValue.getValue(1).asBigDecimal());
        }
    }

    @Test
    public void testJsonBoxedValueByGetValueFromList() {
        JsonList jsonList = new JsonList();
        jsonList.add("value1");
        jsonList.add(new BigDecimal("14.01"));

        JsonMap jsonMap1 = new JsonMap();
        jsonMap1.put("key11", "value1");
        jsonMap1.put("key12", new BigDecimal("14.01"));
        jsonList.add(jsonMap1);

        JsonList jsonList1 = new JsonList();
        jsonList1.add("value1");
        jsonList1.add(new BigDecimal("14.01"));
        jsonList.add(jsonList1);

        {
            JsonBoxedValue jsonBoxedValue = jsonList.getValue(0);
            Assert.assertNotNull(jsonBoxedValue);
            Assert.assertEquals("value1", jsonBoxedValue.asString());

            jsonBoxedValue = jsonList.getValue(1);
            Assert.assertEquals(new BigDecimal("14.01"), jsonBoxedValue.asBigDecimal());

            jsonBoxedValue = jsonList.getValue(2);
            Assert.assertEquals("value1", jsonBoxedValue.getValue("key11").asString());
            Assert.assertEquals(new BigDecimal("14.01"), jsonBoxedValue.getValue("key12").asBigDecimal());

            jsonBoxedValue = jsonList.getValue(3);
            Assert.assertEquals("value1", jsonBoxedValue.getValue(0).asString());
            Assert.assertEquals(new BigDecimal("14.01"), jsonBoxedValue.getValue(1).asBigDecimal());
        }
        jsonList = JsonList.parse(jsonList.toJsonString());
        {
            JsonBoxedValue jsonBoxedValue = jsonList.getValue(0);
            Assert.assertNotNull(jsonBoxedValue);
            Assert.assertEquals("value1", jsonBoxedValue.asString());

            jsonBoxedValue = jsonList.getValue(1);
            Assert.assertEquals(new BigDecimal("14.01"), jsonBoxedValue.asBigDecimal());

            jsonBoxedValue = jsonList.getValue(2);
            Assert.assertEquals("value1", jsonBoxedValue.getValue("key11").asString());
            Assert.assertEquals(new BigDecimal("14.01"), jsonBoxedValue.getValue("key12").asBigDecimal());

            jsonBoxedValue = jsonList.getValue(3);
            Assert.assertEquals("value1", jsonBoxedValue.getValue(0).asString());
            Assert.assertEquals(new BigDecimal("14.01"), jsonBoxedValue.getValue(1).asBigDecimal());
        }
    }

    @Test
    public void testParsingExample() {
        final String restJSONResponse = """
                {
                "Success": {
                "users": [
                {"username": "user1", "firstName": "First Name1", "lastName": "Last Name1", "age": 14, "single": true},
                {"username": "user2", "firstName": "First Name2", "lastName": "Last Name2", "age": 24, "single": false},
                {"username": "user3", "firstName": "First Name3", "lastName": "Last Name3", "age": 34, "single": false}
                ]
                },
                "Error": null
                }
                """;
        JsonMap parsedJSONResponse = JsonMap.parse(restJSONResponse);
        Assert.assertNotNull(parsedJSONResponse);
        JsonBoxedValue success = parsedJSONResponse.getValue("Success");
        Assert.assertNotNull(success);
        JsonBoxedValue users = success.getValue("users");
        Assert.assertNotNull(users);
        JsonBoxedValue userAt0 = users.getValue(0);
        Assert.assertNotNull(userAt0);

        JsonBoxedValue usernameAt0 = userAt0.getValue("username");
        String usernameAt0AsString = usernameAt0.asString();
        Assert.assertEquals("user1", usernameAt0AsString);

        JsonBoxedValue firstNameAt0 = userAt0.getValue("firstName");
        String firstNameAt0AsString = firstNameAt0.asString();
        Assert.assertEquals("First Name1", firstNameAt0AsString);

        JsonBoxedValue lastNameAt0 = userAt0.getValue("lastName");
        String lastNameAt0AsString = lastNameAt0.asString();
        Assert.assertEquals("Last Name1", lastNameAt0AsString);

        JsonBoxedValue ageAt0 = userAt0.getValue("age");
        Integer ageAt0AsInteger = ageAt0.asInteger();
        Assert.assertNotNull(ageAt0AsInteger);
        Assert.assertEquals(14, ageAt0AsInteger.intValue());

        JsonBoxedValue singleAt0 = userAt0.getValue("single");
        Boolean singleAt0AsBoolean = singleAt0.asBoolean();
        Assert.assertNotNull(singleAt0AsBoolean);
        Assert.assertTrue(singleAt0AsBoolean);

        JsonBoxedValue userAt1 = users.getValue(1);
        Assert.assertNotNull(userAt1);

        JsonBoxedValue usernameAt1 = userAt1.getValue("username");
        String usernameAt1AsString = usernameAt1.asString();
        Assert.assertEquals("user2", usernameAt1AsString);

        JsonBoxedValue firstNameAt1 = userAt1.getValue("firstName");
        String firstNameAt1AsString = firstNameAt1.asString();
        Assert.assertEquals("First Name2", firstNameAt1AsString);

        JsonBoxedValue lastNameAt1 = userAt1.getValue("lastName");
        String lastNameAt1AsString = lastNameAt1.asString();
        Assert.assertEquals("Last Name2", lastNameAt1AsString);

        JsonBoxedValue ageAt1 = userAt1.getValue("age");
        Integer ageAt1AsInteger = ageAt1.asInteger();
        Assert.assertNotNull(ageAt1AsInteger);
        Assert.assertEquals(24, ageAt1AsInteger.intValue());

        JsonBoxedValue singleAt1 = userAt1.getValue("single");
        Boolean singleAt1AsBoolean = singleAt1.asBoolean();
        Assert.assertNotNull(singleAt1AsBoolean);
        Assert.assertFalse(singleAt1AsBoolean);

        JsonBoxedValue userAt2 = users.getValue(2);
        Assert.assertNotNull(userAt2);

        JsonBoxedValue usernameAt2 = userAt1.getValue("username");
        String usernameAt2AsString = usernameAt2.asString();
        Assert.assertEquals("user2", usernameAt2AsString);

        JsonBoxedValue firstNameAt2 = userAt2.getValue("firstName");
        String firstNameAt2AsString = firstNameAt2.asString();
        Assert.assertEquals("First Name3", firstNameAt2AsString);

        JsonBoxedValue lastNameAt2 = userAt2.getValue("lastName");
        String lastNameAt2AsString = lastNameAt2.asString();
        Assert.assertEquals("Last Name3", lastNameAt2AsString);

        JsonBoxedValue ageAt2 = userAt2.getValue("age");
        Integer ageAt2AsInteger = ageAt2.asInteger();
        Assert.assertNotNull(ageAt2AsInteger);
        Assert.assertEquals(34, ageAt2AsInteger.intValue());

        JsonBoxedValue singleAt2 = userAt2.getValue("single");
        Boolean singleAt2AsBoolean = singleAt2.asBoolean();
        Assert.assertNotNull(singleAt2AsBoolean);
        Assert.assertFalse(singleAt2AsBoolean);

        //To get first name of user1 in a single line of code
        final String firstNameOfUser1 = parsedJSONResponse.getValue("Success").getValue("users").getValue(0).getValue("firstName").asString();
        Assert.assertNotNull(ageAt2AsInteger);
        Assert.assertEquals("First Name1", firstNameOfUser1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testIndexOutOfBoundsException() {
        JsonList jsonList = new JsonList(3);
        jsonList.add("one");
        jsonList.add("two");
        jsonList.add(3);
        jsonList =  JsonList.parse(jsonList.toJsonString());
        jsonList.getValue(3);
    }

    @Test(expected = InvalidValueException.class)
    public void testInvalidValueException0() {
        JsonBoxedValue.create(new JsonMap()).asShort();
    }

    @Test(expected = InvalidValueException.class)
    public void testInvalidValueException1() {
        JsonBoxedValue.create(new JsonMap()).asInteger();
    }

    @Test(expected = InvalidValueException.class)
    public void testInvalidValueException2() {
        JsonBoxedValue.create(new JsonMap()).asLong();
    }

    @Test(expected = InvalidValueException.class)
    public void testInvalidValueException3() {
        JsonBoxedValue.create(new JsonMap()).asDouble();
    }

    @Test(expected = InvalidValueException.class)
    public void testInvalidValueException4() {
        JsonBoxedValue.create(new JsonMap()).asBigDecimal();
    }

    @Test(expected = InvalidValueException.class)
    public void testInvalidValueException5() {
        JsonBoxedValue.create(new JsonMap()).asBigInteger();
    }

    @Test(expected = InvalidValueException.class)
    public void testInvalidValueException6() {
        JsonBoxedValue.create(new JsonMap()).asBoolean();
    }

    //------

    @Test(expected = InvalidValueException.class)
    public void testInvalidValueException10() {
        JsonBoxedValue.create(new JsonList()).asShort();
    }

    @Test(expected = InvalidValueException.class)
    public void testInvalidValueException11() {
        JsonBoxedValue.create(new JsonList()).asInteger();
    }

    @Test(expected = InvalidValueException.class)
    public void testInvalidValueException12() {
        JsonBoxedValue.create(new JsonList()).asLong();
    }

    @Test(expected = InvalidValueException.class)
    public void testInvalidValueException13() {
        JsonBoxedValue.create(new JsonList()).asDouble();
    }

    @Test(expected = InvalidValueException.class)
    public void testInvalidValueException14() {
        JsonBoxedValue.create(new JsonList()).asBigDecimal();
    }

    @Test(expected = InvalidValueException.class)
    public void testInvalidValueException15() {
        JsonBoxedValue.create(new JsonList()).asBigInteger();
    }

    @Test(expected = InvalidValueException.class)
    public void testInvalidValueException16() {
        JsonBoxedValue.create(new JsonList()).asBoolean();
    }

    //--

    @Test(expected = InvalidValueException.class)
    public void testInvalidValueException20() {
        try {
            JsonBoxedValue.create(new JsonMap()).asShort();
        } catch (InvalidValueException e) {
            if (!"The value is JsonMapNode which cannot be converted to Short.".equals(e.getMessage())) {
                Assert.fail("unexpected exception message: " + e.getMessage());
            }
            throw e;
        }

    }

    @Test(expected = InvalidValueException.class)
    public void testInvalidValueException21() {
        try {
            JsonBoxedValue.create(new JsonList()).asShort();
        } catch (InvalidValueException e) {
            if (!"The value is JsonListNode which cannot be converted to Short.".equals(e.getMessage())) {
                Assert.fail("unexpected exception message: " + e.getMessage());
            }
            throw e;
        }
    }

    @Test(expected = InvalidValueException.class)
    public void testInvalidValueException22() {
        try {
            JsonBoxedValue.create(new JsonMap()).asInteger();
        } catch (InvalidValueException e) {
            if (!"The value is JsonMapNode which cannot be converted to Integer.".equals(e.getMessage())) {
                Assert.fail("unexpected exception message: " + e.getMessage());
            }
            throw e;
        }

    }

    @Test(expected = InvalidValueException.class)
    public void testInvalidValueException23() {
        try {
            JsonBoxedValue.create(new JsonList()).asInteger();
        } catch (InvalidValueException e) {
            if (!"The value is JsonListNode which cannot be converted to Integer.".equals(e.getMessage())) {
                Assert.fail("unexpected exception message: " + e.getMessage());
            }
            throw e;
        }
    }

    @Test(expected = InvalidValueException.class)
    public void testInvalidValueException24() {
        try {
            JsonBoxedValue.create(new JsonMap()).asLong();
        } catch (InvalidValueException e) {
            if (!"The value is JsonMapNode which cannot be converted to Long.".equals(e.getMessage())) {
                Assert.fail("unexpected exception message: " + e.getMessage());
            }
            throw e;
        }

    }

    @Test(expected = InvalidValueException.class)
    public void testInvalidValueException25() {
        try {
            JsonBoxedValue.create(new JsonList()).asLong();
        } catch (InvalidValueException e) {
            if (!"The value is JsonListNode which cannot be converted to Long.".equals(e.getMessage())) {
                Assert.fail("unexpected exception message: " + e.getMessage());
            }
            throw e;
        }
    }

    @Test(expected = InvalidValueException.class)
    public void testInvalidValueException26() {
        try {
            JsonBoxedValue.create(new JsonMap()).asDouble();
        } catch (InvalidValueException e) {
            if (!"The value is JsonMapNode which cannot be converted to Double.".equals(e.getMessage())) {
                Assert.fail("unexpected exception message: " + e.getMessage());
            }
            throw e;
        }

    }

    @Test(expected = InvalidValueException.class)
    public void testInvalidValueException27() {
        try {
            JsonBoxedValue.create(new JsonList()).asDouble();
        } catch (InvalidValueException e) {
            if (!"The value is JsonListNode which cannot be converted to Double.".equals(e.getMessage())) {
                Assert.fail("unexpected exception message: " + e.getMessage());
            }
            throw e;
        }
    }

    @Test(expected = InvalidValueException.class)
    public void testInvalidValueException28() {
        try {
            JsonBoxedValue.create(new JsonMap()).asBigDecimal();
        } catch (InvalidValueException e) {
            if (!"The value is JsonMapNode which cannot be converted to BigDecimal.".equals(e.getMessage())) {
                Assert.fail("unexpected exception message: " + e.getMessage());
            }
            throw e;
        }

    }

    @Test(expected = InvalidValueException.class)
    public void testInvalidValueException29() {
        try {
            JsonBoxedValue.create(new JsonList()).asBigDecimal();
        } catch (InvalidValueException e) {
            if (!"The value is JsonListNode which cannot be converted to BigDecimal.".equals(e.getMessage())) {
                Assert.fail("unexpected exception message: " + e.getMessage());
            }
            throw e;
        }
    }

    @Test(expected = InvalidValueException.class)
    public void testInvalidValueException210() {
        try {
            JsonBoxedValue.create(new JsonMap()).asBigInteger();
        } catch (InvalidValueException e) {
            if (!"The value is JsonMapNode which cannot be converted to BigInteger.".equals(e.getMessage())) {
                Assert.fail("unexpected exception message: " + e.getMessage());
            }
            throw e;
        }

    }

    @Test(expected = InvalidValueException.class)
    public void testInvalidValueException211() {
        try {
            JsonBoxedValue.create(new JsonList()).asBigInteger();
        } catch (InvalidValueException e) {
            if (!"The value is JsonListNode which cannot be converted to BigInteger.".equals(e.getMessage())) {
                Assert.fail("unexpected exception message: " + e.getMessage());
            }
            throw e;
        }
    }

    @Test(expected = InvalidValueException.class)
    public void testInvalidValueException212() {
        try {
            JsonBoxedValue.create(new JsonMap()).asBoolean();
        } catch (InvalidValueException e) {
            if (!"The value is JsonMapNode which cannot be converted to Boolean.".equals(e.getMessage())) {
                Assert.fail("unexpected exception message: " + e.getMessage());
            }
            throw e;
        }

    }

    @Test(expected = InvalidValueException.class)
    public void testInvalidValueException213() {
        try {
            JsonBoxedValue.create(new JsonList()).asBoolean();
        } catch (InvalidValueException e) {
            if (!"The value is JsonListNode which cannot be converted to Boolean.".equals(e.getMessage())) {
                Assert.fail("unexpected exception message: " + e.getMessage());
            }
            throw e;
        }
    }

}
