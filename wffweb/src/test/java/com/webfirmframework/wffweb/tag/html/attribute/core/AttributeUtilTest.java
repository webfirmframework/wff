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
package com.webfirmframework.wffweb.tag.html.attribute.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.webfirmframework.wffweb.tag.html.attribute.ReadOnly;
import com.webfirmframework.wffweb.tag.html.attribute.Value;
import com.webfirmframework.wffweb.tag.html.attribute.global.Style;
import com.webfirmframework.wffweb.tag.html.attributewff.CustomAttribute;
import com.webfirmframework.wffweb.util.WffBinaryMessageUtil;

public class AttributeUtilTest {

    @Test
    public void testGetAttributeHtmlStringBooleanAbstractAttributeArray() {
        Style style = new Style();
        style.addCssProperty("background", "green");
        style.addCssProperty("align", "center");
        CustomAttribute ca = new CustomAttribute("custom-attr",
                "testvalue1, testvalue2");
        
        String attributeHtmlString = AttributeUtil.getAttributeHtmlString(true, style, ca);
        assertEquals(" style=\"background:green;align:center;\" custom-attr=\"testvalue1, testvalue2\"", attributeHtmlString);
        
        attributeHtmlString = AttributeUtil.getAttributeHtmlString(false, style, ca);
        assertEquals(" style=\"background:green;align:center;\" custom-attr=\"testvalue1, testvalue2\"", attributeHtmlString);
     
    }

    @Test
    public void testGetAttributeHtmlStringBooleanCharsetAbstractAttributeArray() {
        Style style = new Style();
        style.addCssProperty("background", "green");
        style.addCssProperty("align", "center");
        CustomAttribute ca = new CustomAttribute("custom-attr",
                "testvalue1, testvalue2");
        
        String attributeHtmlString = AttributeUtil.getAttributeHtmlString(true, style, ca);
        assertEquals(" style=\"background:green;align:center;\" custom-attr=\"testvalue1, testvalue2\"", attributeHtmlString);
        
        attributeHtmlString = AttributeUtil.getAttributeHtmlString(false, style, ca);
        assertEquals(" style=\"background:green;align:center;\" custom-attr=\"testvalue1, testvalue2\"", attributeHtmlString);
        
    }

    @Test
    public void testGetAttributeHtmlCompressedByIndexArray() throws IOException {
        {
            
            Style style = new Style();
            style.addCssProperty("background", "green");
            style.addCssProperty("align", "center");
            CustomAttribute ca = new CustomAttribute("custom-attr",
                    "testvalue1, testvalue2");
            
            CustomAttribute valuelessAttr = new CustomAttribute("custom-attr2", null);

            {
                byte[][] attributeHtmlCompressedByIndexArray = AttributeUtil.getAttributeHtmlBytesCompressedByIndex(
                        true, StandardCharsets.UTF_8, style, ca);

                assertEquals(2, attributeHtmlCompressedByIndexArray.length);
                
                byte[] compressedBytesByIndex = attributeHtmlCompressedByIndexArray[0];
                
                int lengthOfOptimizedBytesOfAttrNameIndex = compressedBytesByIndex[0];
                assertTrue(lengthOfOptimizedBytesOfAttrNameIndex > 0);
                assertEquals(1, lengthOfOptimizedBytesOfAttrNameIndex);
                
                byte[] tagNameIndexBytes =   new byte[lengthOfOptimizedBytesOfAttrNameIndex];
                
                System.arraycopy(compressedBytesByIndex, 1, tagNameIndexBytes, 0, lengthOfOptimizedBytesOfAttrNameIndex);
                
                assertEquals(AttributeRegistry.getAttributeNames().indexOf("style"), WffBinaryMessageUtil.getIntFromOptimizedBytes(tagNameIndexBytes));
                
                assertEquals("background:green;align:center;", new String(compressedBytesByIndex,
                        compressedBytesByIndex[0] + 1,
                        compressedBytesByIndex.length
                                - (compressedBytesByIndex[0] + 1)));
                
                compressedBytesByIndex = attributeHtmlCompressedByIndexArray[1];
                assertEquals(0, compressedBytesByIndex[0]);
                assertEquals("custom-attr=testvalue1, testvalue2", new String(compressedBytesByIndex,
                        compressedBytesByIndex[0] + 1,
                        compressedBytesByIndex.length
                                - (compressedBytesByIndex[0] + 1)));

            }
            
            {
                byte[][] attributeHtmlCompressedByIndexArray = AttributeUtil.getAttributeHtmlBytesCompressedByIndex(
                        false, StandardCharsets.UTF_8, style, ca);

                assertEquals(2, attributeHtmlCompressedByIndexArray.length);
                
                byte[] compressedBytesByIndex = attributeHtmlCompressedByIndexArray[0];
                assertEquals(1, compressedBytesByIndex[0]);
                assertEquals("background:green;align:center;", new String(compressedBytesByIndex,
                        compressedBytesByIndex[0] + 1,
                        compressedBytesByIndex.length
                                - (compressedBytesByIndex[0] + 1)));
                
                compressedBytesByIndex = attributeHtmlCompressedByIndexArray[1];
                assertEquals(0, compressedBytesByIndex[0]);
                assertEquals("custom-attr=testvalue1, testvalue2", new String(compressedBytesByIndex,
                        compressedBytesByIndex[0] + 1,
                        compressedBytesByIndex.length
                                - (compressedBytesByIndex[0] + 1)));

            }
            
            {
                byte[][] attributeHtmlCompressedByIndexArray = AttributeUtil.getAttributeHtmlBytesCompressedByIndex(
                        false, StandardCharsets.UTF_8, style, ca, valuelessAttr);
                
                {   
                 // just to verify caching causes no problem
                    AttributeUtil.parseExactAttributeHtmlBytesCompressedByIndex(attributeHtmlCompressedByIndexArray, StandardCharsets.UTF_8); 
                }

                assertEquals(3, attributeHtmlCompressedByIndexArray.length);
                
                byte[] compressedBytesByIndex = attributeHtmlCompressedByIndexArray[0];
                assertEquals(1, compressedBytesByIndex[0]);
                assertEquals("background:green;align:center;", new String(compressedBytesByIndex,
                        compressedBytesByIndex[0] + 1,
                        compressedBytesByIndex.length
                                - (compressedBytesByIndex[0] + 1)));
                
                compressedBytesByIndex = attributeHtmlCompressedByIndexArray[1];
                assertEquals(0, compressedBytesByIndex[0]);
                assertEquals("custom-attr=testvalue1, testvalue2", new String(compressedBytesByIndex,
                        compressedBytesByIndex[0] + 1,
                        compressedBytesByIndex.length
                                - (compressedBytesByIndex[0] + 1)));
                
                compressedBytesByIndex = attributeHtmlCompressedByIndexArray[2];
                assertEquals(0, compressedBytesByIndex[0]);
                assertEquals("custom-attr2", new String(compressedBytesByIndex,
                        compressedBytesByIndex[0] + 1,
                        compressedBytesByIndex.length
                                - (compressedBytesByIndex[0] + 1)));

            }
            
            {
                byte[][] attributeHtmlCompressedByIndexArray = AttributeUtil.getAttributeHtmlBytesCompressedByIndex(
                        false, StandardCharsets.UTF_8, style, ca, valuelessAttr, new ReadOnly(), new Value(""));
                
                final AbstractAttribute[] attributes = AttributeUtil.parseExactAttributeHtmlBytesCompressedByIndex(attributeHtmlCompressedByIndexArray, StandardCharsets.UTF_8);
                AbstractAttribute attr1 = attributes[0];
                assertEquals("style", attr1.getAttributeName());
                assertEquals("background:green;align:center;", attr1.getAttributeValue());
                
                AbstractAttribute attr2 = attributes[1];
                assertEquals("custom-attr", attr2.getAttributeName());
                assertEquals("testvalue1, testvalue2", attr2.getAttributeValue());
                
                AbstractAttribute attr3 = attributes[2];
                assertEquals("custom-attr2", attr3.getAttributeName());
                assertNull(attr3.getAttributeValue());
                
                AbstractAttribute attr4 = attributes[3];
                assertEquals("readonly", attr4.getAttributeName());
                assertNull(attr3.getAttributeValue());
                
                AbstractAttribute attr5 = attributes[4];
                assertEquals("value", attr5.getAttributeName());
                assertNotNull(attr5.getAttributeValue());
                assertEquals("", attr5.getAttributeValue());
            }
            
        }
//        fail("Not yet implemented");
    }
    
    
//    public static void main(String[] args) throws Exception {
//
//        {
//            Style style = new Style();
//            style.addCssProperty("background", "green");
//            style.addCssProperty("align", "center");
//            CustomAttribute ca = new CustomAttribute("custom-attr",
//                    "testvalue1, testvalue2");
//
//            byte[][] attributeHtmlCompressedByIndexArray = getAttributeHtmlBytesCompressedByIndex(
//                    true, StandardCharsets.UTF_8, style, ca);
//
//            for (byte[] compressedBytesByIndex : attributeHtmlCompressedByIndexArray) {
//
//                System.out.println(
//                        "attribute index for style : " + AttributeRegistrar
//                                .getAttributeNames().indexOf("style"));
//                if (compressedBytesByIndex[0] == 0) {
//                    System.out.println("not indexed");
//                    System.out.println(new String(compressedBytesByIndex,
//                            compressedBytesByIndex[0] + 1,
//                            compressedBytesByIndex.length
//                                    - (compressedBytesByIndex[0] + 1)));
//
//                } else {
//                    System.out.println(new String(compressedBytesByIndex,
//                            compressedBytesByIndex[0] + 1,
//                            compressedBytesByIndex.length
//                                    - (compressedBytesByIndex[0] + 1)));
//                }
//            }
//
//        }
//
//        System.out.println("-------------------------");
//
//        // ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        //
//        // baos.write(257);
//        //
//        // System.out.println((byte) ((char) '='));
//        // System.out.println("---------");
//        // for (byte b : baos.toByteArray()) {
//        // System.out.println(b);
//        // }
//        // System.out.println("----y-----");
//        //
//        //// System.out.println(WffBinaryMessageUtil.getIntFromBytes(baos.toByteArray()));
//        //
//        // for (byte b : WffBinaryMessageUtil.getBytesFromInt(2555)) {
//        // System.out.println(b);
//        // }
//        // System.out.println("----------");
//        // System.out.println(WffBinaryMessageUtil.getIntFromOptimizedBytes(new
//        // byte[]{61}));
//        // System.out.println(new
//        // String(WffBinaryMessageUtil.getBytesFromInt(2555)));
//        //
//        //
//        // Style style = new Style();
//        // style.addCssProperty("background", "green");
//        // style.addCssProperty("align", "center");
//        //
//        // Style style2 = new Style();
//        // style2.addCssProperty("background2", "green");
//        // style2.addCssProperty("align2", "center");
//        //
//        //
//        // System.out.println(style.toHtmlString());
//        //
//        // byte[][] attributeHtmlCompressedByIndexArray =
//        // getAttributeHtmlCompressedByIndexArray(false, StandardCharsets.UTF_8,
//        // style, style2);
//        //
//        // System.out.println(new
//        // String(attributeHtmlCompressedByIndexArray[1]));
//
//    }
    
    

}
