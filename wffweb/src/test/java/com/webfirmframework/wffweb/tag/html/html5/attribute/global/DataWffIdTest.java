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
package com.webfirmframework.wffweb.tag.html.html5.attribute.global;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import com.webfirmframework.wffweb.tag.html.attribute.Value;
import com.webfirmframework.wffweb.tag.html.attribute.core.AttributeRegistry;

public class DataWffIdTest {

    @Test
    public void testGetAttrNameIndex() {
//        fail("Not yet implemented");        
        
//        {
//            Value value = new Value("S152");
//            assertEquals((int) AttributeRegistry.getIndexByAttributeName(value.getAttributeName()), value.getAttrNameIndex());
//            assertNotEquals(-1, value.getAttrNameIndex());
//        }
        
        {
            DataWffId dataWffId = new DataWffId("S152");            
            assertEquals((int) AttributeRegistry.getIndexByAttributeName(DataWffId.ATTRIBUTE_NAME), dataWffId.getAttrNameIndex());
            assertNotEquals(-1, dataWffId.getAttrNameIndex());
        }
    }

}
