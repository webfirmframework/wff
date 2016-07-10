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
package com.webfirmframework.wffweb.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webfirmframework.wffweb.util.data.NameValue;

import de.undercouch.bson4jackson.BsonFactory;

public class WffBinaryMessageUtilTest {

    private static final int MAX_NAME_VALUE_PAIRS = 1000;

    @Rule
    public TestName testName = new TestName();

    private long beforeMillis;

    @Before
    public void beforeTest() throws Exception {
        beforeMillis = System.currentTimeMillis();
    }

    @After
    public void afterTest() throws Exception {
        long afterMillis = System.currentTimeMillis();
        final long totalMillisTaken = afterMillis - beforeMillis;

        if (!"testPerformanceOfWffBinaryMessageToNameValuesAndWiseVersa"
                .equals(testName.getMethodName())) {
            System.out.println(testName.getMethodName() + " took "
                    + totalMillisTaken + " ms");
        }

    }

    //@formatter:off
    @Test
    public void testParse1() {
        
        List<NameValue> nameValues = new LinkedList<NameValue>();
        nameValues.add(new NameValue("name1".getBytes(), new byte[][]{"value1".getBytes()}));
        nameValues.add(new NameValue("name2".getBytes(), new byte[][]{"value2".getBytes()}));
        nameValues.add(new NameValue("name3".getBytes(), new byte[][]{"value3".getBytes(), "value41".getBytes()}));

        byte[] message = WffBinaryMessageUtil.VERSION_1.getWffBinaryMessageBytes(nameValues);
        
        List<NameValue> actualNameValues = WffBinaryMessageUtil.VERSION_1.parse(message);
        
        for (int i = 0; i < actualNameValues.size(); i++) {
            
            final NameValue expectedNameValue = nameValues.get(i);
            
            NameValue actualNameValue = actualNameValues.get(i);
            
            assertArrayEquals(expectedNameValue.getName(), actualNameValue.getName());
            
            final byte[][] values = expectedNameValue.getValues();
            
            for (int j = 0; j < values.length; j++) {
                assertArrayEquals(expectedNameValue.getValues()[j], actualNameValue.getValues()[j]);
            }
            
        }
    }
    
    @Test
    public void testParse2() {
        
        List<NameValue> nameValues = new LinkedList<NameValue>();
        nameValues.add(new NameValue("name1".getBytes(), new byte[][]{"value1".getBytes()}));
        nameValues.add(new NameValue("name2".getBytes(), new byte[][]{"value2".getBytes()}));

        byte[] message = WffBinaryMessageUtil.VERSION_1.getWffBinaryMessageBytes(nameValues);
        
        List<NameValue> actualNameValues = WffBinaryMessageUtil.VERSION_1.parse(message);
        
        for (int i = 0; i < actualNameValues.size(); i++) {
            
            final NameValue expectedNameValue = nameValues.get(i);
            
            NameValue actualNameValue = actualNameValues.get(i);
            
            assertArrayEquals(expectedNameValue.getName(), actualNameValue.getName());
            
            final byte[][] values = expectedNameValue.getValues();
            
            for (int j = 0; j < values.length; j++) {
                assertArrayEquals(expectedNameValue.getValues()[j], actualNameValue.getValues()[j]);
            }
            
        }
    }
    
    @Test
    public void testParse3() {
        
        List<NameValue> nameValues = new LinkedList<NameValue>();
        nameValues.add(new NameValue("name1".getBytes(), new byte[][]{"value1".getBytes()}));
        nameValues.add(new NameValue("name3".getBytes(), new byte[][]{"value3".getBytes(), "value41".getBytes()}));

        byte[] message = WffBinaryMessageUtil.VERSION_1.getWffBinaryMessageBytes(nameValues);
        
        List<NameValue> actualNameValues = WffBinaryMessageUtil.VERSION_1.parse(message);
        
        for (int i = 0; i < actualNameValues.size(); i++) {
            
            final NameValue expectedNameValue = nameValues.get(i);
            
            NameValue actualNameValue = actualNameValues.get(i);
            
            assertArrayEquals(expectedNameValue.getName(), actualNameValue.getName());
            
            final byte[][] values = expectedNameValue.getValues();
            
            for (int j = 0; j < values.length; j++) {
                assertArrayEquals(expectedNameValue.getValues()[j], actualNameValue.getValues()[j]);
            }
            
        }
    }
    
    @Test
    public void testParse4() {
        
        List<NameValue> nameValues = new LinkedList<NameValue>();
        nameValues.add(new NameValue("name1".getBytes(), new byte[][]{"value1".getBytes()}));
        nameValues.add(new NameValue("name3".getBytes(), new byte[][]{"value3".getBytes(), "value41".getBytes()}));
        nameValues.add(new NameValue("name2".getBytes(), new byte[][]{"value2".getBytes()}));

        byte[] message = WffBinaryMessageUtil.VERSION_1.getWffBinaryMessageBytes(nameValues);
        
        List<NameValue> actualNameValues = WffBinaryMessageUtil.VERSION_1.parse(message);
        
        for (int i = 0; i < actualNameValues.size(); i++) {
            
            final NameValue expectedNameValue = nameValues.get(i);
            
            NameValue actualNameValue = actualNameValues.get(i);
            
            assertArrayEquals(expectedNameValue.getName(), actualNameValue.getName());
            
            final byte[][] values = expectedNameValue.getValues();
            
            for (int j = 0; j < values.length; j++) {
                assertArrayEquals(expectedNameValue.getValues()[j], actualNameValue.getValues()[j]);
            }
            
        }
    }
    
    @Test
    public void testParse5() {
        
        List<NameValue> nameValues = new LinkedList<NameValue>();
        nameValues.add(new NameValue("name3".getBytes(), new byte[][]{"value3".getBytes(), "value41".getBytes()}));
        nameValues.add(new NameValue("name1".getBytes(), new byte[][]{"value1".getBytes()}));
        nameValues.add(new NameValue("name2".getBytes(), new byte[][]{"value2".getBytes()}));

        byte[] message = WffBinaryMessageUtil.VERSION_1.getWffBinaryMessageBytes(nameValues);
        
        List<NameValue> actualNameValues = WffBinaryMessageUtil.VERSION_1.parse(message);
        
        for (int i = 0; i < actualNameValues.size(); i++) {
            
            final NameValue expectedNameValue = nameValues.get(i);
            
            NameValue actualNameValue = actualNameValues.get(i);
            
            assertArrayEquals(expectedNameValue.getName(), actualNameValue.getName());
            
            final byte[][] values = expectedNameValue.getValues();
            
            for (int j = 0; j < values.length; j++) {
                assertArrayEquals(expectedNameValue.getValues()[j], actualNameValue.getValues()[j]);
            }
            
        }
    }
   
    @Test
    public void testPerformanceOfWffBinaryMessageToNameValuesAndWiseVersa() {
        
        List<NameValue> nameValues = getProducedNameValues();

        long beforeMillis = System.currentTimeMillis();
        
        byte[] message = WffBinaryMessageUtil.VERSION_1.getWffBinaryMessageBytes(nameValues);
        
        List<NameValue> actualNameValues = WffBinaryMessageUtil.VERSION_1.parse(message);
        
        long afterMillis = System.currentTimeMillis();
        
        final long totalMillisTakenForWffBinaryMessage = afterMillis - beforeMillis;
        
        if (totalMillisTakenForWffBinaryMessage > 100) {
           fail(testName.getMethodName() + " took "
                    + totalMillisTakenForWffBinaryMessage + " ms, maximum 100mx is allowed");
        } else {
            System.out.println(testName.getMethodName() + " took just "
                    + totalMillisTakenForWffBinaryMessage + " ms for building and parsing "
                    + MAX_NAME_VALUE_PAIRS + " name-value pairs.");
        }
        
        for (int i = 0; i < actualNameValues.size(); i++) {
            
            final NameValue expectedNameValue = nameValues.get(i);
            
            NameValue actualNameValue = actualNameValues.get(i);
            
            assertArrayEquals(expectedNameValue.getName(), actualNameValue.getName());
            
            final byte[][] values = expectedNameValue.getValues();
            
            for (int j = 0; j < values.length; j++) {
                assertArrayEquals(expectedNameValue.getValues()[j], actualNameValue.getValues()[j]);
            }
            
        }
        
        try {
            
            Map<String, List<String>> jsonObject = getProducedJsonObject();
            
            ObjectMapper objectMapper = new ObjectMapper();
            
            beforeMillis = System.currentTimeMillis();
            
            byte[] jsonValueAsBytes = objectMapper.writeValueAsBytes(jsonObject);
            
            @SuppressWarnings({ "unused", "rawtypes" })
            Map parsed = objectMapper.readValue(jsonValueAsBytes, Map.class);
            
            afterMillis = System.currentTimeMillis();
            
            final long totalMillisTakenForJacksonFasterxml = afterMillis - beforeMillis;
            
            System.out.println("totalMillisTaken for jackson faster xml buiding and parsing " + totalMillisTakenForJacksonFasterxml);
            
            assertTrue(totalMillisTakenForWffBinaryMessage < totalMillisTakenForJacksonFasterxml);
            
            if (totalMillisTakenForWffBinaryMessage < totalMillisTakenForJacksonFasterxml) {
                int timesFaster = (int) ((double) totalMillisTakenForJacksonFasterxml / (double) totalMillisTakenForWffBinaryMessage);
                System.out.println("wff binary message building and parsing is " + timesFaster+ " times faster than jackson fasterxml parsing and bulding of json.");
                timesFaster = Math.round(timesFaster);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<NameValue> getProducedNameValues() {
        List<NameValue> nameValues = new LinkedList<NameValue>();
        
        for (int i = 0; i < MAX_NAME_VALUE_PAIRS; i++) {
            
            byte[][] values = {
                    new byte[] {'v', 'a', 'l', 'u', 'e', '1'}, 
                    new byte[] {'v', 'a', 'l', 'u', 'e', '2'}, 
                    new byte[] {'v', 'a', 'l', 'u', 'e', '3'}, 
                    new byte[] {'v', 'a', 'l', 'u', 'e', '4'}, 
                    new byte[] {'v', 'a', 'l', 'u', 'e', '5'}, 
                    new byte[] {'v', 'a', 'l', 'u', 'e', '6'}, 
                    new byte[] {'v', 'a', 'l', 'u', 'e', '7'}, 
                    new byte[] {'v', 'a', 'l', 'u', 'e', '8'}, 
                    new byte[] {'v', 'a', 'l', 'u', 'e', '9'}, 
                    new byte[] {'v', 'a', 'l', 'u', 'e', '1', '0'} 
                    };
            
            nameValues.add(new NameValue(("nameeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee" + i).getBytes(), values));
        }
        return nameValues;
    }
    
    @Test
    public void testWffBinaryMessageBytesLengthIsLowerThanBson() throws Exception {
        
        List<NameValue> nameValues = getProducedNameValues();

        byte[] message = WffBinaryMessageUtil.VERSION_1.getWffBinaryMessageBytes(nameValues);
        
        Map<String, List<String>> jsonObject = getProducedJsonObject();
        
        ObjectMapper bsonMapper = new ObjectMapper(
                new BsonFactory());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bsonMapper.writeValue(baos, jsonObject);
        byte[] bsonBytes = baos.toByteArray();
        assertTrue(message.length < bsonBytes.length);
        
        if (message.length < bsonBytes.length) {
            System.out.println("the length of wff binary message is lower than bson bytes, the ratio wff binary message:bson = " + (message.length + ":" + bsonBytes.length)+ ", gain is " + (bsonBytes.length - message.length) + " bytes");
        }
        
    }

    private Map<String, List<String>> getProducedJsonObject() {
        Map<String, List<String>> jsonObject = new HashMap<String, List<String>>();
        
        for (int i = 0; i < MAX_NAME_VALUE_PAIRS; i++) {
            List<String> values = new LinkedList<String>();
            values.add("value1");
            values.add("value2");
            values.add("value3");
            values.add("value4");
            values.add("value5");
            values.add("value6");
            values.add("value7");
            values.add("value8");
            values.add("value9");
            values.add("value10");
            jsonObject.put("nameeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee" + i , values);   
        }
        return jsonObject;
    }
    

    @Test
    public void testGetWffBinaryMessageBytes1() {
        
        List<NameValue> nameValues = new LinkedList<NameValue>();
        nameValues.add(new NameValue("key1".getBytes(), new byte[][]{"value1".getBytes()}));
        nameValues.add(new NameValue("key2".getBytes(), new byte[][]{"value2".getBytes()}));
        nameValues.add(new NameValue("key3".getBytes(), new byte[][]{"value3".getBytes(), "value41".getBytes()}));

        byte[] actualMessage = WffBinaryMessageUtil.VERSION_1.getWffBinaryMessageBytes(nameValues);
     
        
        byte[] expectedMessage = { 4, 4,
                //key length                    value length
                0, 0, 0, 4, 'k', 'e', 'y', '1', 0, 0, 0, 6, 'v', 'a', 'l', 'u', 'e', '1', 
                0, 0, 0, 4, 'k', 'e', 'y', '2', 0, 0, 0, 6, 'v', 'a', 'l', 'u', 'e', '2',
                0, 0, 0, 4, 'k', 'e', 'y', '3', 0, 0, 0, 21, 0, 0, 0, 6, 'v', 'a', 'l', 'u', 'e', '3', 0, 0, 0, 7, 'v', 'a', 'l', 'u', 'e', '4', '1', 'A' 
                };
        
       assertArrayEquals(expectedMessage, actualMessage);
        
    }
    
    @Test
    public void testGetWffBinaryMessageBytes2() {
        
        List<NameValue> nameValues = new LinkedList<NameValue>();
        nameValues.add(new NameValue("key1".getBytes(), new byte[][]{"value1".getBytes()}));
        nameValues.add(new NameValue("key3".getBytes(), new byte[][]{"value3".getBytes(), "value41".getBytes()}));
        nameValues.add(new NameValue("key4".getBytes(), new byte[0][0]));
        nameValues.add(new NameValue("key2".getBytes(), new byte[][]{"value2".getBytes()}));
        nameValues.add(new NameValue("key5".getBytes(), new byte[0][0]));

        byte[] actualMessage = WffBinaryMessageUtil.VERSION_1.getWffBinaryMessageBytes(nameValues);
     
        
        byte[] expectedMessage = { 4, 4,
                //key length                    value length
                0, 0, 0, 4, 'k', 'e', 'y', '1', 0, 0, 0, 6, 'v', 'a', 'l', 'u', 'e', '1', 
                0, 0, 0, 4, 'k', 'e', 'y', '3', 0, 0, 0, 21, 0, 0, 0, 6, 'v', 'a', 'l', 'u', 'e', '3', 0, 0, 0, 7, 'v', 'a', 'l', 'u', 'e', '4', '1', 'A', 
                0, 0, 0, 4, 'k', 'e', 'y', '4', 0, 0, 0, 0,
                0, 0, 0, 4, 'k', 'e', 'y', '2', 0, 0, 0, 6, 'v', 'a', 'l', 'u', 'e', '2',
                0, 0, 0, 4, 'k', 'e', 'y', '5', 0, 0, 0, 0
                };
        
       assertArrayEquals(expectedMessage, actualMessage);
       assertEquals(expectedMessage.length, actualMessage.length);
        
    }
    
    @Test
    public void testGetOptimizedBytesFromIntAndGetIntFromOptimizedBytes() {
        for (long i = 0; i <= Integer.MAX_VALUE; i++) {
            byte[] optimizedBytesFromInt = WffBinaryMessageUtil.getOptimizedBytesFromInt((int) i);
            int intFromOptimizedBytes = WffBinaryMessageUtil.getIntFromOptimizedBytes(optimizedBytesFromInt);
            assertEquals(i, intFromOptimizedBytes);
        }
    }
    
    //@formatter:on

}
