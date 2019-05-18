/*
 * Copyright 2014-2018 Web Firm Framework
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

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.junit.Test;

import com.webfirmframework.wffweb.InvalidValueException;
import com.webfirmframework.wffweb.tag.html.AbstractHtml;
import com.webfirmframework.wffweb.tag.html.attribute.AttributeNameConstants;
import com.webfirmframework.wffweb.tag.html.attribute.InternalAttrNameConstants;
import com.webfirmframework.wffweb.tag.html.attribute.global.Style;
import com.webfirmframework.wffweb.tag.html.attributewff.CustomAttribute;
import com.webfirmframework.wffweb.tag.html.core.TagRegistry;
import com.webfirmframework.wffweb.tag.html.html5.attribute.global.DataAttribute;
import com.webfirmframework.wffweb.tag.htmlwff.CustomTag;

public class AttributeRegistryTest {

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
                assertEquals(attr.getAttrNameIndex(), (int) AttributeRegistry.getIndexByAttributeName(attr.getAttributeName()));

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
            }
        }
    }
    
    @Test
    public void testGetIndexByAttributeName() throws Exception {
        
        
        
        final List<String> attributeNames = AttributeRegistry.getAttributeNames();
        for (String attrName : attributeNames) {
            
            final String constantName = attrName.replace("-", "_").toUpperCase();
            System.out.println(constantName + "(AttributeNameConstants."
                    + constantName + "),\n");
            
            final int indexByAttributeName = AttributeRegistry.getIndexByAttributeName(attrName);
            
            final String attrNameByIndex = attributeNames.get(indexByAttributeName);
            assertEquals(attrName, attrNameByIndex);
        }
    }

}
