/*
 * Copyright 2014-2023 Web Firm Framework
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
package com.webfirmframework.wffweb.tag.html.attribute.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.junit.Test;

import com.webfirmframework.wffweb.InvalidValueException;
import com.webfirmframework.wffweb.tag.html.attribute.AttributeNameConstants;
import com.webfirmframework.wffweb.tag.html.attribute.InternalAttrNameConstants;
import com.webfirmframework.wffweb.tag.html.attribute.event.EventAttribute;
import com.webfirmframework.wffweb.tag.html.attribute.global.Style;
import com.webfirmframework.wffweb.util.WffBinaryMessageUtil;


public class AttributeRegistryTest {
    
    @Test
    public void testLoadAllAttributeClasses() throws Exception {
        try {
            //loadAllTagClasses must be after test
            //not possible to write a separate test case for test() method
            //as the internal map will be nullified in loadAllTagClasses
            AttributeRegistry.test();      
            AttributeRegistry.test1();       
        } catch (InvalidValueException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        AttributeRegistry.loadAllAttributeClasses();
    }
    
    @Test
    public void testEventAttr() throws Exception {
        final Map<String, Class<?>> attributeClassByAttrName = AttributeRegistry.getAttributeClassByAttrName();
        for (final PreIndexedAttributeName each : PreIndexedAttributeName
                .allEventAttributes()) {
            if (each.eventAttr()) {
                final Class<?> cls = attributeClassByAttrName
                        .get(each.attrName());

                if (cls == null
                        || !EventAttribute.class.isAssignableFrom(cls)) {
                    org.junit.Assert.fail(each.attrName() + " is not an event attribute");
                }
            }
        }
    }

    @Test
    public void testIfAllAttributeNamesAreReffered() throws Exception {
        final Field[] fields = AttributeNameConstants.class.getFields();
        for (final Field field : fields) {
            // value from constant name field
            final String constantName = field.get(null).toString();
            final String simpleClassName = AttributeRegistry
                    .getAttributeClassNameByAttributeName().get(constantName);
            System.out.println(simpleClassName);
            if (simpleClassName == null) {
                fail(constantName
                        + " attribute name is not included in all tag names map");

            } else if (!simpleClassName.replace("Attribute", "")
                    .equalsIgnoreCase(constantName.replace("-", ""))) {
                fail(constantName
                        + " attribute has an incorrect class mapping");
            }
        }
    }
    

    @Test
    public void testGetNewTagInstance() {
        final Map<String, Class<?>> attrClassNameByAttrName = AttributeRegistry
                .getAttributeClassByAttrName();
        {
            for (Entry<String, Class<?>> entry : attrClassNameByAttrName
                    .entrySet()) {
                AbstractAttribute attr = null;
                try {
                    attr = AttributeRegistry
                            .getNewAttributeInstance(entry.getKey());
                } catch (InvalidValueException e) {
                    continue;
                }

                assertNotNull(attr);
                assertEquals(entry.getValue(), attr.getClass());
               
                //just for testing
                int attrNameIndex = WffBinaryMessageUtil.getIntFromOptimizedBytes(attr.getAttrNameIndexBytes());
                assertEquals(attrNameIndex, (int) AttributeRegistry.getIndexByAttributeName(attr.getAttributeName()));

            }
        }

        {
            for (Entry<String, Class<?>> entry : attrClassNameByAttrName
                    .entrySet()) {
                
                AbstractAttribute attr = null;
                
                if (entry.getValue().equals(Style.class)) {
                    attr = AttributeRegistry
                            .getNewAttributeInstance(entry.getKey(), "color:green;");
                    assertNotNull(attr);
                    assertEquals(entry.getValue(), attr.getClass());
                    continue;
                }
                
                try {
                    attr = AttributeRegistry
                            .getNewAttributeInstance(entry.getKey(), "1");
                } catch (InvalidValueException e) {
                    try {
                        attr = AttributeRegistry
                                .getNewAttributeInstance(entry.getKey(), "true");
                    } catch (InvalidValueException e2) {
                        try {
                            attr = AttributeRegistry
                                    .getNewAttributeInstance(entry.getKey(), "yes");
                        } catch (InvalidValueException e3) {
                            fail("yes is invalid value for " + entry.getValue());
                        }
                    }
                }

                assertNotNull(attr);
                assertEquals(entry.getValue(), attr.getClass());

            }
        }

    }
    
    @Test
    public void testAttrConstantsWithPreIndexedNames() throws Exception {
        for (final Field field : InternalAttrNameConstants.class.getFields()) {
            try {
                final String fieldName = field.getName();
                
                assertNotNull(PreIndexedAttributeName.valueOf(fieldName));
            } catch (final Exception e) {
                e.printStackTrace();
                fail("invalid PreIndexedAttributeName constant");
            }
        }
        for (final Field field : AttributeNameConstants.class.getFields()) {
            try {
                final String attrName = field.get(null).toString();
                if (attrName.endsWith("-")) {
                    continue;
                }
                
                final String fieldName = field.getName();                
                assertNotNull(PreIndexedAttributeName.valueOf(fieldName));
            } catch (final Exception e) {
                e.printStackTrace();
                fail("invalid PreIndexedAttributeName constant");
            }
        }
    }
    
    @Test
    public void testGetIndexByAttributeName() throws Exception {
        
        
        
        final List<String> attributeNames = AttributeRegistry.getAttributeNames();
        for (String attrName : attributeNames) {
            
//            final String constantName = attrName.replace("-", "_").toUpperCase();
//            System.out.println(constantName + "(AttributeNameConstants."
//                    + constantName + "),\n");
            
            final int indexByAttributeName = AttributeRegistry.getIndexByAttributeName(attrName);
            
            final String attrNameByIndex = attributeNames.get(indexByAttributeName);
            assertEquals(attrName, attrNameByIndex);
        }
    }
    
    @Test
    public void testPreIndexedAttributeNameOrder() {

        //it is always used so its length must be kept 1
        assertEquals(1, PreIndexedAttributeName.DATA_WFF_ID.indexBytes().length);
        List<String> names = new ArrayList<>();
        for (PreIndexedAttributeName each : PreIndexedAttributeName.values()) {
            names.add(each.attrName());
        }

        List<String> multiSortedNames = new ArrayList<>(names);
        multiSortedNames
                .sort(Comparator.comparingInt(String::length).thenComparing(String::compareTo));
        assertEquals(multiSortedNames, names);
    }
    
    @Test
    public void testSortedBooleanAttrNames() {

        final List<String> booleanAttributeNames = AttributeRegistry.getBooleanAttributeNames();
        Set<String> set = new HashSet<>(booleanAttributeNames);

        List<String> names = new ArrayList<>();
        for (PreIndexedAttributeName each : PreIndexedAttributeName.values()) {
            if (set.contains(each.attrName())) {
                names.add(each.attrName());
            }
        }
        assertEquals(names, booleanAttributeNames);
    }


}
