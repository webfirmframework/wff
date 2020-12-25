package com.webfirmframework.wffweb.css;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

/*
 * Copyright 2014-2021 Web Firm Framework
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
public class CssColorNameTest {

    @Test
    public void testGetHex() {
       final String hex = CssColorName.ALICE_BLUE.getHex();
       
       assertNotNull(hex);
       
       assertEquals("#F0F8FF", hex);
       
       final int r = CssColorName.WHITE.getR();
       
       assertEquals(255, r);
       
       assertEquals(255, CssColorName.WHITE.getG());
       
       assertEquals(255, CssColorName.WHITE.getB());
    }
    
    @Test
    public void testHashcode() throws Exception {
        //"FB" and "Ea" have same hashcode so this test is relevant 
        final CssColorName[] values = CssColorName.values();
        Set<Integer> hashcodes = new HashSet<>();
        for (CssColorName cssColorName : values) {
            if (hashcodes.contains(cssColorName.getColorName().hashCode())) {
                fail("multiple css color names have same hashcode");
            }
            hashcodes.add(cssColorName.getColorName().hashCode());
        }
        
    }
    
    @Test
    public void testExtractOpacity() throws Exception {
        assertTrue(CssColorName.extractOpacity("#FFF") == 1F);
        assertTrue(CssColorName.extractOpacity("#FFF") == 1F);
        assertTrue(CssColorName.extractOpacity("#FFFF") == 1F);
        assertTrue(CssColorName.extractOpacity("#F5F5F5") == 1F);
        assertTrue(CssColorName.extractOpacity("#00ff0088") == 0.53F);
        assertTrue(CssColorName.extractOpacity("#0f08") == 0.53F);
    }

}
