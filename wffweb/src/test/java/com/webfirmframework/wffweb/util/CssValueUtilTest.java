/*
 * Copyright 2014-2015 Web Firm Framework
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

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.webfirmframework.wffweb.InvalidValueException;
import com.webfirmframework.wffweb.util.CssValueUtil;

/**
 * @author WFF
 * @since 1.0.0
 * @version 1.0.0
 *
 */
public class CssValueUtilTest {

    /**
     * Test method for {@link com.webfirmframework.wffweb.util.CssValueUtil#split(java.lang.String)}.
     */
    @Test
    public void testSplit1() {
        final List<String> cssValueParts = CssValueUtil.split("hsla(7, 8, 9, 1) red rgb(3, 5, 6) rgb(7, 8, 9) rgba(7, 8, 9, 1) middle hsl(10, 11, 12) green   blue");
        
        Assert.assertEquals("hsla(7, 8, 9, 1)", cssValueParts.get(0));
        Assert.assertEquals("red", cssValueParts.get(1));
        Assert.assertEquals("rgb(3, 5, 6)", cssValueParts.get(2));
        Assert.assertEquals("rgb(7, 8, 9)", cssValueParts.get(3));
        Assert.assertEquals("rgba(7, 8, 9, 1)", cssValueParts.get(4));
        Assert.assertEquals("middle", cssValueParts.get(5));
        Assert.assertEquals("hsl(10, 11, 12)", cssValueParts.get(6));
        Assert.assertEquals("green", cssValueParts.get(7));
        Assert.assertEquals("blue", cssValueParts.get(8));
    }
    
    /**
     * Test method for {@link com.webfirmframework.wffweb.util.CssValueUtil#split(java.lang.String)}.
     */
    @Test
    public void testSplit2() {
        final List<String> cssValueParts = CssValueUtil.split("white red rgb(3, 5, 6) rgb(7, 8, 9) rgba(7, 8, 9, 1) middle hsl(10, 11, 12) green   blue");
        
        Assert.assertEquals("white", cssValueParts.get(0));
        Assert.assertEquals("red", cssValueParts.get(1));
        Assert.assertEquals("rgb(3, 5, 6)", cssValueParts.get(2));
        Assert.assertEquals("rgb(7, 8, 9)", cssValueParts.get(3));
        Assert.assertEquals("rgba(7, 8, 9, 1)", cssValueParts.get(4));
        Assert.assertEquals("middle", cssValueParts.get(5));
        Assert.assertEquals("hsl(10, 11, 12)", cssValueParts.get(6));
        Assert.assertEquals("green", cssValueParts.get(7));
        Assert.assertEquals("blue", cssValueParts.get(8));
    }
    
    /**
     * Test method for {@link com.webfirmframework.wffweb.util.CssValueUtil#split(java.lang.String)}.
     */
    @Test
    public void testSplit3() {
        final List<String> cssValueParts = CssValueUtil.split("white  red rgb(3, 5, 6) rgb(7, 8, 9) rgba(7, 8, 9, 1) middle  blue green   hsl(10, 11, 12)");
        
        Assert.assertEquals("white", cssValueParts.get(0));
        Assert.assertEquals("red", cssValueParts.get(1));
        Assert.assertEquals("rgb(3, 5, 6)", cssValueParts.get(2));
        Assert.assertEquals("rgb(7, 8, 9)", cssValueParts.get(3));
        Assert.assertEquals("rgba(7, 8, 9, 1)", cssValueParts.get(4));
        Assert.assertEquals("middle", cssValueParts.get(5));
        Assert.assertEquals("blue", cssValueParts.get(6));
        Assert.assertEquals("green", cssValueParts.get(7));
        Assert.assertEquals("hsl(10, 11, 12)", cssValueParts.get(8));
    }
    
    /**
     * Test method for {@link com.webfirmframework.wffweb.util.CssValueUtil#split(java.lang.String)}.
     */
    @Test
    public void testSplit4() {
        final List<String> cssValueParts = CssValueUtil.split("55px solid green");
        
        Assert.assertEquals("55px", cssValueParts.get(0));
        Assert.assertEquals("solid", cssValueParts.get(1));
        Assert.assertEquals("green", cssValueParts.get(2));
    }
    
    /**
     * Test method for {@link com.webfirmframework.wffweb.util.CssValueUtil#split(java.lang.String)}.
     */
    @Test
    public void testSplit5() {
        final List<String> cssValueParts = CssValueUtil.split("55px  solid   green");
        
        Assert.assertEquals("55px", cssValueParts.get(0));
        Assert.assertEquals("solid", cssValueParts.get(1));
        Assert.assertEquals("green", cssValueParts.get(2));
    }
    
    /**
     * Test method for {@link com.webfirmframework.wffweb.util.CssValueUtil#split(java.lang.String)}.
     */
    @Test(expected = InvalidValueException.class)
    public void testSplitForInvalidValue1() {
        CssValueUtil.split("hsla(7, 8, 9, 1)rgb(3, 5, 6)");
    }
    
    /**
     * Test method for {@link com.webfirmframework.wffweb.util.CssValueUtil#split(java.lang.String)}.
     */
    @Test(expected = InvalidValueException.class)
    public void testSplitForInvalidValue2() {
        CssValueUtil.split("hsla(7, 8, 9, 1)red");
    }
    
    /**
     * Test method for {@link com.webfirmframework.wffweb.util.CssValueUtil#split(java.lang.String)}.
     */
    @Test(expected = InvalidValueException.class)
    public void testSplitForInvalidValue3() {
        CssValueUtil.split("hsla(7, 8, 9, 1) redrgb(3, 5, 6) ");
    }
    
    /**
     * Test method for {@link com.webfirmframework.wffweb.util.CssValueUtil#split(java.lang.String)}.
     */
    @Test(expected = InvalidValueException.class)
    public void testSplitForInvalidValue4() {
        CssValueUtil.split("hsla(7, 8, 9, 1)red rgb(3, 5, 6) ");
    }
    
    /**
     * Test method for {@link com.webfirmframework.wffweb.util.CssValueUtil#split(java.lang.String)}.
     */
    @Test(expected = InvalidValueException.class)
    public void testSplitForInvalidValue5() {
        CssValueUtil.split("redhsla(7, 8, 9, 1)");
    }

}
