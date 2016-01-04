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
* @author WFF
*/
package com.webfirmframework.wffweb.css;

import static org.junit.Assert.assertEquals;

import org.junit.Assert.*;

import com.webfirmframework.wffweb.css.BackgroundColor;
import com.webfirmframework.wffweb.css.CssNameConstants;
import com.webfirmframework.wffweb.css.HslCssValue;
import com.webfirmframework.wffweb.css.HslaCssValue;
import com.webfirmframework.wffweb.css.RgbCssValue;
import com.webfirmframework.wffweb.css.RgbaCssValue;

import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * @author WFF
 * @since 1.0.0
 */
public class BackgroundColorTest {

    @Test
    public void testColumnRuleColor() {
        BackgroundColor backgroundColor = new BackgroundColor();
        assertEquals(BackgroundColor.TRANSPARENT, backgroundColor.getCssValue());
    }

    @Test
    public void testBackgroundColorString() {
        BackgroundColor backgroundColor = new BackgroundColor("#0000ff");
        assertEquals("#0000ff", backgroundColor.getCssValue());   
    }

    @Test
    public void testBackgroundColorBackgroundColor() {
        BackgroundColor backgroundColor = new BackgroundColor("#0000ff");
        BackgroundColor backgroundColor1 = new BackgroundColor(backgroundColor);
        assertEquals("#0000ff", backgroundColor1.getCssValue());
    }

    @Test
    public void testSetValue() {
        BackgroundColor backgroundColor = new BackgroundColor();
        backgroundColor.setValue("#0000ff");
        assertEquals("#0000ff", backgroundColor.getCssValue());   
    }

    @Test
    public void testGetCssName() {
        BackgroundColor backgroundColor = new BackgroundColor();
        assertEquals(CssNameConstants.BACKGROUND_COLOR, backgroundColor.getCssName());
    }

    @Test
    public void testGetCssValue() {
        BackgroundColor backgroundColor = new BackgroundColor("#0000ff");
        assertEquals("#0000ff", backgroundColor.getCssValue());   
    }

    @Test
    public void testToString() {
        BackgroundColor backgroundColor = new BackgroundColor("#0000ff");
        assertEquals(CssNameConstants.BACKGROUND_COLOR + ": #0000ff",
                backgroundColor.toString());
    }

    @Test
    public void testGetValue() {
        BackgroundColor backgroundColor = new BackgroundColor("#0000ff");
        assertEquals("#0000ff", backgroundColor.getValue());   
    }

    @Test
    public void testSetCssValueString() {
        BackgroundColor backgroundColor = new BackgroundColor();
        backgroundColor.setCssValue("#0000ff");
        assertEquals("#0000ff", backgroundColor.getCssValue());   
    }

    @Test
    public void testSetAsInitial() {
        BackgroundColor backgroundColor = new BackgroundColor();
        backgroundColor.setAsInitial();
        assertEquals(BackgroundColor.INITIAL, backgroundColor.getCssValue());   
    }

    @Test
    public void testSetAsInherit() {
        BackgroundColor backgroundColor = new BackgroundColor();
        backgroundColor.setAsInherit();
        assertEquals(BackgroundColor.INHERIT, backgroundColor.getCssValue());   
    }
    
    @Test
    public void testSetAsTransparent() {
        BackgroundColor backgroundColor = new BackgroundColor();
        backgroundColor.setAsTransparent();
        assertEquals(BackgroundColor.TRANSPARENT, backgroundColor.getCssValue());   
    }
    
    @Test
    public void testSetRgbCssValue() {
        try {
            
            {
                BackgroundColor backgroundColor = new BackgroundColor();
                RgbCssValue rgbCssValue = new RgbCssValue("rgb(15, 25, 255)");
                backgroundColor.setRgbCssValue(rgbCssValue);
                backgroundColor.setRgbCssValue(rgbCssValue);
                assertEquals(CssNameConstants.BACKGROUND_COLOR + ": rgb(15, 25, 255)", backgroundColor.toString());
                assertEquals("rgb(15, 25, 255)", backgroundColor.getCssValue());
                
                BackgroundColor backgroundColor2 = new BackgroundColor();
                backgroundColor2.setRgbCssValue(rgbCssValue);
                RgbCssValue rgbCssValueClone = backgroundColor2.getRgbCssValue();
                Assert.assertNotEquals(backgroundColor.getRgbCssValue(), rgbCssValueClone);
                
                RgbCssValue rgbCssValue2 = new RgbCssValue("rgb(55, 5, 255)");
                backgroundColor2.setRgbCssValue(rgbCssValue2);
                Assert.assertNotEquals(rgbCssValueClone, backgroundColor2.getRgbCssValue());
                Assert.assertEquals(rgbCssValue2, backgroundColor2.getRgbCssValue());
                
                backgroundColor2.setAsTransparent();
                assertEquals(BackgroundColor.TRANSPARENT, backgroundColor2.getCssValue());
                Assert.assertNull(backgroundColor2.getRgbCssValue());
                Assert.assertFalse(rgbCssValue2.isAlreadyInUse());
            }
            {
                BackgroundColor color = new BackgroundColor();
                RgbCssValue rgbCssValue = new RgbCssValue("rgb(15, 25, 255)");
                Assert.assertFalse(rgbCssValue.isAlreadyInUse());
                color.setRgbCssValue(rgbCssValue);
                Assert.assertTrue(rgbCssValue.isAlreadyInUse());
                
                RgbaCssValue rgbaCssValue = new RgbaCssValue("rgba(15, 25, 100, 1)");
                Assert.assertFalse(rgbaCssValue.isAlreadyInUse());
                color.setRgbaCssValue(rgbaCssValue);
                Assert.assertTrue(rgbaCssValue.isAlreadyInUse());
                
                Assert.assertFalse(rgbCssValue.isAlreadyInUse());
                Assert.assertNull(color.getRgbCssValue());
                color.setAsInherit();
                Assert.assertNull(color.getRgbaCssValue());
                Assert.assertNull(color.getRgbCssValue());
                Assert.assertNull(color.getHslCssValue());
                Assert.assertNull(color.getHslaCssValue());
                
                HslCssValue hslCssValue = new HslCssValue("hsl(15, 25%, 100%)");
                Assert.assertFalse(hslCssValue.isAlreadyInUse());
                color.setHslCssValue(hslCssValue);
                Assert.assertTrue(hslCssValue.isAlreadyInUse());
                
                Assert.assertNull(color.getRgbaCssValue());
                Assert.assertNull(color.getRgbCssValue());
                Assert.assertNotNull(color.getHslCssValue());
                Assert.assertNull(color.getHslaCssValue());
                
                HslaCssValue hslaCssValue = new HslaCssValue("hsla(15, 25%, 100%, 1)");
                Assert.assertFalse(hslaCssValue.isAlreadyInUse());
                color.setHslaCssValue(hslaCssValue);
                Assert.assertTrue(hslaCssValue.isAlreadyInUse());
                
                Assert.assertNull(color.getRgbaCssValue());
                Assert.assertNull(color.getRgbCssValue());
                Assert.assertNull(color.getHslCssValue());
                Assert.assertNotNull(color.getHslaCssValue());
                
                color.setAsInitial();
                Assert.assertNull(color.getRgbaCssValue());
                Assert.assertNull(color.getRgbCssValue());
                Assert.assertNull(color.getHslCssValue());
                Assert.assertNull(color.getHslaCssValue());
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

}
