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

import org.junit.Test;

import com.webfirmframework.wffweb.tag.html.attribute.AttributeNameConstants;

public class AttributeRegistryTest {

    @Test
    public void testIfAllAttributeNamesAreReffered() throws Exception {
        final Field[] fields = AttributeNameConstants.class.getFields();
        for (final Field field : fields) {
            if (AttributeRegistry.getAttributeClassNameByAttributeName()
                    .get(field.get(null)) == null) {
                fail(field.get(null)
                        + " attribute name is not included in all tag names map");

            }
        }
    }
    
    @Test
    public void testLoadAllAttributeClasses() throws Exception {
        AttributeRegistry.loadAllAttributeClasses();
    }

}
