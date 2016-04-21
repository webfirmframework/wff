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

import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import com.webfirmframework.wffweb.util.data.NameValue;

public class WffBinaryMessageUtilTest {

    @Rule
    public TestName testName = new TestName();

    private long beforeMillis;
    private long afterMillis;

    @Before
    public void beforeTest() throws Exception {
        beforeMillis = System.currentTimeMillis();
    }

    @After
    public void afterTest() throws Exception {
        afterMillis = System.currentTimeMillis();
        final long totalMillisTaken = afterMillis - beforeMillis;
        if (totalMillisTaken > 100) {
            Assert.fail(testName.getMethodName() + " took " + totalMillisTaken
                    + "ms");
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
        List<NameValue> nameValues = new LinkedList<NameValue>();
        nameValues.add(new NameValue("name3".getBytes(), new byte[][]{"value3".getBytes(), "value41".getBytes()}));
        nameValues.add(new NameValue("name1".getBytes(), new byte[][]{"value1".getBytes()}));
        nameValues.add(new NameValue("name2".getBytes(), new byte[][]{"value2".getBytes()}));
        
        for (int i = 0; i < 1000; i++) {
            nameValues.add(new NameValue("name4".getBytes(), new byte[][]{"value41".getBytes(), "value42".getBytes()}));
        }

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
        
        Assert.assertArrayEquals(expectedMessage, actualMessage);
        
    }
    
    @Test
    public void testGetWffBinaryMessageBytes2() {
        
        List<NameValue> nameValues = new LinkedList<NameValue>();
        nameValues.add(new NameValue("key1".getBytes(), new byte[][]{"value1".getBytes()}));
        nameValues.add(new NameValue("key3".getBytes(), new byte[][]{"value3".getBytes(), "value41".getBytes()}));
        nameValues.add(new NameValue("key2".getBytes(), new byte[][]{"value2".getBytes()}));

        byte[] actualMessage = WffBinaryMessageUtil.VERSION_1.getWffBinaryMessageBytes(nameValues);
     
        
        byte[] expectedMessage = { 4, 4,
                //key length                    value length
                0, 0, 0, 4, 'k', 'e', 'y', '1', 0, 0, 0, 6, 'v', 'a', 'l', 'u', 'e', '1', 
                0, 0, 0, 4, 'k', 'e', 'y', '3', 0, 0, 0, 21, 0, 0, 0, 6, 'v', 'a', 'l', 'u', 'e', '3', 0, 0, 0, 7, 'v', 'a', 'l', 'u', 'e', '4', '1', 'A', 
                0, 0, 0, 4, 'k', 'e', 'y', '2', 0, 0, 0, 6, 'v', 'a', 'l', 'u', 'e', '2'
                };
        
        Assert.assertArrayEquals(expectedMessage, actualMessage);
        
    }
    
    //@formatter:on

}
