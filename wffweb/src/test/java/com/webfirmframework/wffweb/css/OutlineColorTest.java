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

import com.webfirmframework.wffweb.css.CssNameConstants;
import com.webfirmframework.wffweb.css.HslCssValue;
import com.webfirmframework.wffweb.css.HslaCssValue;
import com.webfirmframework.wffweb.css.OutlineColor;
import com.webfirmframework.wffweb.css.RgbCssValue;
import com.webfirmframework.wffweb.css.RgbaCssValue;

import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * @author WFF
 * @since 1.0.0
 */
public class OutlineColorTest {

    @Test
    public void testColumnRuleColor() {
        OutlineColor outlineColor = new OutlineColor();
        assertEquals(OutlineColor.INVERT, outlineColor.getCssValue());
    }

    @Test
    public void testOutlineColorString() {
        OutlineColor outlineColor = new OutlineColor("#0000ff");
        assertEquals("#0000ff", outlineColor.getCssValue());   
    }

    @Test
    public void testOutlineColorOutlineColor() {
        OutlineColor outlineColor = new OutlineColor("#0000ff");
        OutlineColor outlineColor1 = new OutlineColor(outlineColor);
        assertEquals("#0000ff", outlineColor1.getCssValue());
    }

    @Test
    public void testSetValue() {
        OutlineColor outlineColor = new OutlineColor();
        outlineColor.setValue("#0000ff");
        assertEquals("#0000ff", outlineColor.getCssValue());   
    }

    @Test
    public void testGetCssName() {
        OutlineColor outlineColor = new OutlineColor();
        assertEquals(CssNameConstants.OUTLINE_COLOR, outlineColor.getCssName());
    }

    @Test
    public void testGetCssValue() {
        OutlineColor outlineColor = new OutlineColor("#0000ff");
        assertEquals("#0000ff", outlineColor.getCssValue());   
    }

    @Test
    public void testToString() {
        OutlineColor outlineColor = new OutlineColor("#0000ff");
        assertEquals(CssNameConstants.OUTLINE_COLOR + ": #0000ff",
                outlineColor.toString());
    }

    @Test
    public void testGetValue() {
        OutlineColor outlineColor = new OutlineColor("#0000ff");
        assertEquals("#0000ff", outlineColor.getValue());   
    }

    @Test
    public void testSetCssValueString() {
        OutlineColor outlineColor = new OutlineColor();
        outlineColor.setCssValue("#0000ff");
        assertEquals("#0000ff", outlineColor.getCssValue());   
    }

    @Test
    public void testSetAsInitial() {
        OutlineColor outlineColor = new OutlineColor();
        outlineColor.setAsInitial();
        assertEquals(OutlineColor.INITIAL, outlineColor.getCssValue());   
    }

    @Test
    public void testSetAsInherit() {
        OutlineColor outlineColor = new OutlineColor();
        outlineColor.setAsInherit();
        assertEquals(OutlineColor.INHERIT, outlineColor.getCssValue());   
    }
    
    @Test
    public void testSetAsInvert() {
        OutlineColor outlineColor = new OutlineColor();
        outlineColor.setAsInvert();
        assertEquals(OutlineColor.INVERT, outlineColor.getCssValue());   
    }
    
    @Test
    public void testSetRgbCssValue() {
        try {
            OutlineColor outlineColor = new OutlineColor();
            RgbCssValue rgbCssValue = new RgbCssValue("rgb(15, 25, 255)");
            outlineColor.setRgbCssValue(rgbCssValue);
            outlineColor.setRgbCssValue(rgbCssValue);
            assertEquals(CssNameConstants.OUTLINE_COLOR + ": rgb(15, 25, 255)", outlineColor.toString());
            assertEquals("rgb(15, 25, 255)", outlineColor.getCssValue());
            
            OutlineColor outlineColor2 = new OutlineColor();
            outlineColor2.setRgbCssValue(rgbCssValue);
            RgbCssValue rgbCssValueClone = outlineColor2.getRgbCssValue();
            Assert.assertNotEquals(outlineColor.getRgbCssValue(), rgbCssValueClone);
            
            RgbCssValue rgbCssValue2 = new RgbCssValue("rgb(55, 5, 255)");
            outlineColor2.setRgbCssValue(rgbCssValue2);
            Assert.assertNotEquals(rgbCssValueClone, outlineColor2.getRgbCssValue());
            Assert.assertEquals(rgbCssValue2, outlineColor2.getRgbCssValue());
            
            outlineColor2.setAsInvert();
            assertEquals(OutlineColor.INVERT, outlineColor2.getCssValue());
            Assert.assertNull(outlineColor2.getRgbCssValue());
            Assert.assertFalse(rgbCssValue2.isAlreadyInUse());
            
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
        {
            OutlineColor color = new OutlineColor();
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
    }

}
