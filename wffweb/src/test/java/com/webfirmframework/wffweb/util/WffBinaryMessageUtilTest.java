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
package com.webfirmframework.wffweb.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;
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
import org.junit.Assert;
import org.junit.Assume;

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

        byte[] modifiedMessage = ByteBuffer.allocate(message.length + 8).put(new byte[4]).put(message).put(new byte[4]).rewind().array();
        List<NameValue> actualNameValues = WffBinaryMessageUtil.VERSION_1.parse(modifiedMessage, 4, modifiedMessage.length - 8);
        
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
        nameValues.add(new NameValue("".getBytes(), new byte[0][0]));
        nameValues.add(new NameValue("name1".getBytes(), new byte[][]{"value1".getBytes()}));
        nameValues.add(new NameValue("name2".getBytes(), new byte[][]{"value2".getBytes()}));

        byte[] message = WffBinaryMessageUtil.VERSION_1.getWffBinaryMessageBytes(nameValues);
        
        List<NameValue> actualNameValues = WffBinaryMessageUtil.VERSION_1.parse(message);
        
        for (int i = 0; i < nameValues.size(); i++) {
            
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
        
        if (totalMillisTakenForWffBinaryMessage > 125) {
           fail(testName.getMethodName() + " took "
                    + totalMillisTakenForWffBinaryMessage + " ms, maximum 125mx is allowed");
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

            Assume.assumeTrue(
                    "The best performance comes when totalMillisTakenForWffBinaryMessage < totalMillisTakenForJacksonFasterxml but it didn't satisfy. It is not a severe issue so it may be ignored.",
                    totalMillisTakenForWffBinaryMessage < totalMillisTakenForJacksonFasterxml);
            
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
     
        
        byte[] expectedMessage = { 1, 1,
                //key length                    value length
                4, 'k', 'e', 'y', '1', 7, 6, 'v', 'a', 'l', 'u', 'e', '1', 
                4, 'k', 'e', 'y', '2', 7, 6, 'v', 'a', 'l', 'u', 'e', '2',
                4, 'k', 'e', 'y', '3', 15, 6, 'v', 'a', 'l', 'u', 'e', '3', 7, 'v', 'a', 'l', 'u', 'e', '4', '1' 
                };
        
       assertArrayEquals(expectedMessage, actualMessage);
        
    }
    
    @Test
    public void testGetWffBinaryMessageBytesEncodingDecodeing() {
        
        List<NameValue> nameValues = new LinkedList<NameValue>();
        nameValues.add(new NameValue("key1".getBytes(), new byte[][]{"value1".getBytes()}));
        nameValues.add(new NameValue("key2".getBytes(), new byte[][]{"value2".getBytes()}));
        nameValues.add(new NameValue("key3".getBytes(), new byte[][]{"value3".getBytes(), "value41".getBytes()}));

        byte[] actualMessage = WffBinaryMessageUtil.VERSION_1.getWffBinaryMessageBytes(nameValues);
     
        
        byte[] expectedMessage = { 1, 1,
                //key length                    value length
                4, 'k', 'e', 'y', '1', 7, 6, 'v', 'a', 'l', 'u', 'e', '1', 
                4, 'k', 'e', 'y', '2', 7, 6, 'v', 'a', 'l', 'u', 'e', '2',
                4, 'k', 'e', 'y', '3', 15, 6, 'v', 'a', 'l', 'u', 'e', '3', 7, 'v', 'a', 'l', 'u', 'e', '4', '1' 
                };
        
       assertArrayEquals(expectedMessage, actualMessage);
       
       List<NameValue> decodedNameValues = WffBinaryMessageUtil.VERSION_1.parse(actualMessage);
       
       for (int i = 0; i < decodedNameValues.size(); i++) {
           
           NameValue decodedNameValue = decodedNameValues.get(i);
           NameValue nameValue = nameValues.get(i);
           
           assertArrayEquals(nameValue.getName(), decodedNameValue.getName());
           assertArrayEquals(nameValue.getValues(), decodedNameValue.getValues());
           
           assertEquals(nameValue.getValues().length, decodedNameValue.getValues().length);
           
           for (int j = 0; j < decodedNameValue.getValues().length; j++) {
               byte[] value = nameValue.getValues()[j];
               byte[] decodedValue = decodedNameValue.getValues()[j];
               assertArrayEquals(value, decodedValue);
           }
           
       }
       
       actualMessage = WffBinaryMessageUtil.VERSION_1.getWffBinaryMessageBytes(nameValues);
       
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
     
        
        byte[] expectedMessage = { 1, 1,
                //key length                    value length
                4, 'k', 'e', 'y', '1', 7, 6, 'v', 'a', 'l', 'u', 'e', '1', 
                4, 'k', 'e', 'y', '3', 15, 6, 'v', 'a', 'l', 'u', 'e', '3', 7, 'v', 'a', 'l', 'u', 'e', '4', '1', 
                4, 'k', 'e', 'y', '4', 0,
                4, 'k', 'e', 'y', '2', 7, 6, 'v', 'a', 'l', 'u', 'e', '2',
                4, 'k', 'e', 'y', '5', 0,
                };
        
       assertArrayEquals(expectedMessage, actualMessage);
       assertEquals(expectedMessage.length, actualMessage.length);
        
    }
    
    @Test
    public void testGetOptimizedBytesFromIntAndGetIntFromOptimizedBytes() {
        for (int i = 0; i < 512; i++) {
            byte[] optimizedBytesFromInt = WffBinaryMessageUtil.getOptimizedBytesFromInt(i);
            int intFromOptimizedBytes = WffBinaryMessageUtil.getIntFromOptimizedBytes(optimizedBytesFromInt);
            assertEquals(i, intFromOptimizedBytes);
        }
        //init i with zero when required
        for (int i = Integer.MAX_VALUE - 512; i < Integer.MAX_VALUE; i++) {
            byte[] optimizedBytesFromInt = WffBinaryMessageUtil.getOptimizedBytesFromInt(i);
            byte[] optimizedBytesFromLong = WffBinaryMessageUtil.getOptimizedBytesFromLong(i);
            assertArrayEquals(optimizedBytesFromInt, optimizedBytesFromLong);
        }
        
        byte[] optimizedBytesFromInt = WffBinaryMessageUtil.getOptimizedBytesFromInt(Integer.MAX_VALUE);
        byte[] optimizedBytesFromLong = WffBinaryMessageUtil.getOptimizedBytesFromLong(Integer.MAX_VALUE);
        assertArrayEquals(optimizedBytesFromInt, optimizedBytesFromLong);
    }
    
    //for future development
//    @Test
    @SuppressWarnings("unused")
    private void testGet56bitOptimizedBytesFromLongAndGetLongFrom56bitOptimizedBytes() {
        
        //2^64 = 9223372036854775807 
        //2^56 = 72057594037927936
        
        //(2^55) - 1 = 36028797018963967
        //(2^53) - 1 = 9007199254740991
        
        System.out.println((long) Math.pow(2, 55) - 1);
        System.out.println((long) Math.pow(2, 55) - 1);
        final long jsNumber_MAX_SAFE_INTEGER = 9007199254740991L;
        
        long int54Signed = (long) Math.pow(2, 53) - 1;
        
        final long int56Signed = (long) Math.pow(2, 55) - 1;
        
        //even if we do (long) Math.pow(2, 64) it can hold max of Long.MAX_VALUE
        
        final BigInteger int64Signed = new BigInteger("2").pow(63).subtract(new BigInteger("1")); 
        
        
        Assert.assertEquals(jsNumber_MAX_SAFE_INTEGER, int54Signed);
        Assert.assertEquals(new BigInteger(String.valueOf(Long.MAX_VALUE)), int64Signed);
        
        //Number.MAX_SAFE_INTEGER is the maximum safe value of integer
        //i.e. (2^53)-1
        
        
        
        {
            byte[] optimizedBytesFromLong = WffBinaryMessageUtil.getOptimizedBytesFromLong( jsNumber_MAX_SAFE_INTEGER);
            long longFromOptimizedBytes = WffBinaryMessageUtil.getLongFrom56bitOptimizedBytes(optimizedBytesFromLong);
            assertEquals(jsNumber_MAX_SAFE_INTEGER, longFromOptimizedBytes);
        }
        
        {
            byte[] optimizedBytesFromLong = WffBinaryMessageUtil.getOptimizedBytesFromInt56bit(int56Signed);
            assertEquals(7, optimizedBytesFromLong.length);
            long longFromOptimizedBytes = WffBinaryMessageUtil.getLongFrom56bitOptimizedBytes(optimizedBytesFromLong);
            assertEquals(int56Signed, longFromOptimizedBytes);
        }
        
        {
            byte[] optimizedBytesFromLong = WffBinaryMessageUtil.getOptimizedBytesFromInt56bit(int54Signed);
            assertEquals(7, optimizedBytesFromLong.length);
            long longFromOptimizedBytes = WffBinaryMessageUtil.getLongFrom56bitOptimizedBytes(optimizedBytesFromLong);
            assertEquals(int54Signed, longFromOptimizedBytes);
        }
        
        {
            byte[] optimizedBytesFromLong = WffBinaryMessageUtil.getOptimizedBytesFromInt56bit( jsNumber_MAX_SAFE_INTEGER);
            long longFromOptimizedBytes = WffBinaryMessageUtil.getLongFrom56bitOptimizedBytes(optimizedBytesFromLong);
            System.out.println(Arrays.toString(optimizedBytesFromLong));
            assertEquals(7, optimizedBytesFromLong.length);
            assertEquals(jsNumber_MAX_SAFE_INTEGER, longFromOptimizedBytes);
        }
        
//        for (long i = Integer.MAX_VALUE; i < Integer.MAX_VALUE + 512; i++) {
//            byte[] optimizedBytesFromLong = WffBinaryMessageUtil.getOptimizedBytesFromLong( i);
//            long longFromOptimizedBytes = WffBinaryMessageUtil.getLongFrom56bitOptimizedBytes(optimizedBytesFromLong);
//            assertEquals(i, longFromOptimizedBytes);
//        }
//        for (long i = Long.MAX_VALUE; i > Long.MAX_VALUE - Integer.MAX_VALUE; i--) {
//            byte[] optimizedBytesFromLong = WffBinaryMessageUtil.getOptimizedBytesFromLong( i);
//            long longFromOptimizedBytes = WffBinaryMessageUtil.getLongFromOptimizedBytes(optimizedBytesFromLong);
//            assertEquals(i, longFromOptimizedBytes);
//        }
//        System.out.println("Long.MAX_VALUE to Integer.MAX_VALUE success");
    }
    
    @Test
    public void testGetOptimizedBytesFromLongAndGetLongFromOptimizedBytes() {
        {
            byte[] optimizedBytesFromLong = WffBinaryMessageUtil.getOptimizedBytesFromLong( Long.MAX_VALUE);
            long longFromOptimizedBytes = WffBinaryMessageUtil.getLongFromOptimizedBytes(optimizedBytesFromLong);
            assertEquals(Long.MAX_VALUE, longFromOptimizedBytes);
        }
        for (long i = Integer.MAX_VALUE; i < Integer.MAX_VALUE + 512; i++) {
            byte[] optimizedBytesFromLong = WffBinaryMessageUtil.getOptimizedBytesFromLong( i);
            long longFromOptimizedBytes = WffBinaryMessageUtil.getLongFromOptimizedBytes(optimizedBytesFromLong);
            assertEquals(i, longFromOptimizedBytes);
        }
//        for (long i = Long.MAX_VALUE; i > Long.MAX_VALUE - Integer.MAX_VALUE; i--) {
//            byte[] optimizedBytesFromLong = WffBinaryMessageUtil.getOptimizedBytesFromLong( i);
//            long longFromOptimizedBytes = WffBinaryMessageUtil.getLongFromOptimizedBytes(optimizedBytesFromLong);
//            assertEquals(i, longFromOptimizedBytes);
//        }
//        System.out.println("Long.MAX_VALUE to Integer.MAX_VALUE success");
    }
    
    @Test
    public void testGetLengthOfOptimizedBytesFromInt() {
        assertEquals(WffBinaryMessageUtil.getOptimizedBytesFromInt(255).length, WffBinaryMessageUtil.getLengthOfOptimizedBytesFromInt(255));
        assertEquals(WffBinaryMessageUtil.getOptimizedBytesFromInt(256).length, WffBinaryMessageUtil.getLengthOfOptimizedBytesFromInt(256));
        assertEquals(WffBinaryMessageUtil.getOptimizedBytesFromInt(65535).length, WffBinaryMessageUtil.getLengthOfOptimizedBytesFromInt(65535));
        assertEquals(WffBinaryMessageUtil.getOptimizedBytesFromInt(65536).length, WffBinaryMessageUtil.getLengthOfOptimizedBytesFromInt(65536));
        assertEquals(WffBinaryMessageUtil.getOptimizedBytesFromInt(16777215).length, WffBinaryMessageUtil.getLengthOfOptimizedBytesFromInt(16777215));
        assertEquals(WffBinaryMessageUtil.getOptimizedBytesFromInt(16777216).length, WffBinaryMessageUtil.getLengthOfOptimizedBytesFromInt(16777216));
        assertEquals(WffBinaryMessageUtil.getOptimizedBytesFromInt(2147483646).length, WffBinaryMessageUtil.getLengthOfOptimizedBytesFromInt(2147483646));
        assertEquals(WffBinaryMessageUtil.getOptimizedBytesFromInt(2147483647).length, WffBinaryMessageUtil.getLengthOfOptimizedBytesFromInt(2147483647));
        
        long before = System.currentTimeMillis();
        
        for (int i = 0; i <= 35566 * 2; i++) {
            @SuppressWarnings("unused")
            int len = WffBinaryMessageUtil.getOptimizedBytesFromInt(i).length;
        }
        
        long after = System.currentTimeMillis();
        
        long difference1 = after - before;
        
        System.out.println("time taken for getOptimizedBytesFromInt "+difference1);
        
        before = System.currentTimeMillis();
        for (int i = 0; i <= 35566 * 2; i++) {
          @SuppressWarnings("unused")
          int len = WffBinaryMessageUtil.getLengthOfOptimizedBytesFromInt(i);
        }
        after = System.currentTimeMillis();
        long difference2 = after - before;
        
        System.out.println("time taken for getLengthOfOptimizedBytesFromInt "+difference2);
        
        
        //proves getLengthOfOptimizedBytesFromInt gives better performance 
//        assertTrue(difference1 > difference2);
    }
    
    //@formatter:on

}
