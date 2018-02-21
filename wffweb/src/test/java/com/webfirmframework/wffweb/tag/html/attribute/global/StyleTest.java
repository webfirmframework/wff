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
 * @author WFF
 */
package com.webfirmframework.wffweb.tag.html.attribute.global;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Test;

import com.webfirmframework.wffweb.css.CssNameConstants;
import com.webfirmframework.wffweb.css.Cursor;
import com.webfirmframework.wffweb.css.HeightCss;
import com.webfirmframework.wffweb.css.WidthCss;
import com.webfirmframework.wffweb.css.core.CssProperty;
import com.webfirmframework.wffweb.css.css3.AlignContent;
import com.webfirmframework.wffweb.css.css3.BackfaceVisibility;
import com.webfirmframework.wffweb.csswff.CustomCssProperty;
import com.webfirmframework.wffweb.tag.html.stylesandsemantics.Div;

/**
 * 
 * @author WFF
 * @since 1.0.0
 */
public class StyleTest {

//    @Test
    public void testStyle() {
         fail("Not yet implemented");
    }

//    @Test
    public void testStyleString() {
         fail("Not yet implemented");
    }

//    @Test
    public void testStyleMapOfStringString() {
         fail("Not yet implemented");
    }

//    @Test
    public void testStyleCssPropertyArray() {
         fail("Not yet implemented");
    }

//    @Test
    public void testStyleBooleanCssPropertyArray() {
         fail("Not yet implemented");
    }

//    @Test
    public void testAddStylesString() {
         fail("Not yet implemented");
    }

//    @Test
    public void testAddStylesMapOfStringString() {
         fail("Not yet implemented");
    }

    @Test
    public void testAddStyle() {
        try {
            {
                final Style style = new Style();
                style.addCssProperty(CssNameConstants.ALIGN_CONTENT, AlignContent.CENTER.getCssValue());
                assertEquals(false, style.isImportant(AlignContent.FLEX_END));
                
                assertNotNull(style
                        .getCssProperty(CssNameConstants.ALIGN_CONTENT));
                
                assertNull(style.getCssProperty(CssNameConstants.ALIGN_ITEMS));
                
                String customCssName = "custom-css-name";
                String customCssValue = "custom-css-value";
                style.addCssProperty(customCssName, customCssValue);
                
                CssProperty cssProperty = style
                        .getCssProperty("custom-css-name");
                
                assertNotNull(cssProperty);
                
                assertEquals(customCssName, cssProperty.getCssName());
                assertEquals(customCssValue, cssProperty.getCssValue());
                
                assertEquals(true, cssProperty instanceof CustomCssProperty);
                
                assertEquals(true, style.toString().contains(cssProperty.getCssValue()));
                
                ((CustomCssProperty) cssProperty).setCssValue("modified-custom-css-value");
                
                assertEquals(true, style.toString().contains(cssProperty.getCssValue()));
                
                try {
                    style.addCssProperty("custom-style-name:", "custom-style-value");
                    fail();
                } catch (Exception e) {
                }
                try {
                    style.addCssProperty("custom-style-name", ":custom-style-value");
                    fail();
                } catch (Exception e) {
                }
                try {
                    style.addCssProperty("custom-style-name", "custom-style-value;");
                    fail();
                } catch (Exception e) {
                }
                
                WidthCss widthCss = new WidthCss(75F);
                Style anotherStyle = new Style();
                anotherStyle.addCssProperties(widthCss);
                style.addCssProperties(widthCss);// will give WARNING: clonned cssProperty 100%(hashcode: 842714223) as it is already used in another tag
                
                boolean addedStyleAlignItems = style.addCssProperty(CssNameConstants.BACKFACE_VISIBILITY, BackfaceVisibility.VISIBLE.getCssValue());
                assertTrue(addedStyleAlignItems);
                CssProperty cssPropertyBackfaceVisibility = style.getCssProperty(CssNameConstants.BACKFACE_VISIBILITY);
                assertNotNull(cssPropertyBackfaceVisibility);
            }
            
            {
                Style style = new Style();
                Cursor cursor = new Cursor();
                style.addCssProperty(cursor);
                assertTrue(style.toString().equals("style=\"cursor:default;\""));
                cursor.setCursorUrls("auto", "Test.gif", "TestImage.png");
                System.out.println(style.toString());
                assertTrue(style.toString().equals("style=\"cursor:url(\"Test.gif\"), url(\"TestImage.png\"), auto;\""));
            }
            
            System.out.println("testAddStyle success");
        } catch (Exception e) {
            e.printStackTrace();
            fail("testAddStyle failed");
        }
        
    }

    @Test
    public void testAddStylesBooleanCssPropertyArray() {
        final Style style = new Style();

        try {
            style.addCssProperties(true, AlignContent.CENTER);
            assertEquals(true, style.isImportant(AlignContent.CENTER));

            assertEquals(false, style.isImportant(AlignContent.FLEX_END));

            style.addCssProperties(false, AlignContent.CENTER);
            assertEquals(false, style.isImportant(AlignContent.CENTER));

            assertNotNull(style
                    .getCssProperty(CssNameConstants.ALIGN_CONTENT));

            assertNull(style.getCssProperty(CssNameConstants.ALIGN_ITEMS));
            
            WidthCss widthCss = new WidthCss(50F);
            style.addCssProperties(true, widthCss);
            
            assertEquals(true, style.isImportant(CssNameConstants.WIDTH));
            assertEquals(false, style.isImportant(CssNameConstants.HEIGHT));
            
            assertNotNull(style
                    .getCssProperty(CssNameConstants.WIDTH));

            assertNull(style.getCssProperty(CssNameConstants.HEIGHT));
            System.out
                    .println("testAddStylesBooleanCssPropertyArray is success");
        } catch (Exception e) {
            e.printStackTrace();
            fail("testAddStylesBooleanCssPropertyArray failed");
        }

    }

    @Test
    public void testAddStylesCssPropertyArray() {
        final Style style = new Style();
        try {

            style.addCssProperties(AlignContent.CENTER);
            
            style.addCssProperty("dfd", "ddddfd");
            
            style.getCssProperty("dfd");
            
            assertNotNull(style
                    .getCssProperty(CssNameConstants.ALIGN_CONTENT));

            assertNull(style.getCssProperty(CssNameConstants.ALIGN_ITEMS));
            
            WidthCss widthCss = new WidthCss(50F);
            style.addCssProperties(widthCss);
            
            assertNotNull(style
                    .getCssProperty(CssNameConstants.WIDTH));

            assertNull(style.getCssProperty(CssNameConstants.HEIGHT));

            System.out
                    .println("testAddStylesBooleanCssPropertyArray is success");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Not yet implemented");
        }

    }
    
    @Test
    public void testAddSameCssPropertyButDifferentObject() throws Exception {
        final Style style = new Style();
        WidthCss widthCss = new WidthCss(50F);
        style.addCssProperties(widthCss);
        CssProperty cssProperty = style.getCssProperty(CssNameConstants.WIDTH);
        assertTrue(((WidthCss)cssProperty).isAlreadyInUse());
        
        WidthCss widthCss2 = new WidthCss(50F);
        style.addCssProperties(widthCss2);
        
        CssProperty cssProperty2 = style.getCssProperty(CssNameConstants.WIDTH);
        assertTrue(((WidthCss)cssProperty2).isAlreadyInUse());
        assertFalse(((WidthCss)cssProperty).isAlreadyInUse());
    }
    
    @Test
    public void testGetCssProperties() throws Exception {
        final Style style = new Style();
        WidthCss widthCss = new WidthCss(50F);
        style.addCssProperties(widthCss);
        
        Collection<CssProperty> cssProperties = style.getCssProperties();
        assertEquals(widthCss, style.getCssProperty(CssNameConstants.WIDTH));
        assertEquals(1, cssProperties.size());
        
        HeightCss heightCss = new HeightCss(50F);
        cssProperties.add(heightCss);
        
        assertEquals(heightCss, style.getCssProperty(CssNameConstants.HEIGHT));
        
        HeightCss heightCss2 = new HeightCss(50F);
        {
            cssProperties.add(heightCss2);
            
            assertEquals(heightCss2, style.getCssProperty(CssNameConstants.HEIGHT));
            style.removeCssProperty(CssNameConstants.HEIGHT);
            
            assertFalse(cssProperties.contains(heightCss2));
        }
        {
            cssProperties.add(heightCss2);
            
            assertEquals(heightCss2, style.getCssProperty(CssNameConstants.HEIGHT));
            cssProperties.remove(heightCss2);
            assertNull(style.getCssProperty(CssNameConstants.HEIGHT));
        }
        {
            style.addCssProperty(heightCss2);
            
            assertEquals(heightCss2, style.getCssProperty(CssNameConstants.HEIGHT));
            cssProperties.remove(heightCss2);
            
            assertNull(style.getCssProperty(CssNameConstants.HEIGHT));
            
            cssProperties.add(heightCss2);
            
            style.removeCssProperty(heightCss2);
            assertNull(style.getCssProperty(CssNameConstants.HEIGHT));
            assertFalse(cssProperties.contains(heightCss2));
        }
    }

//    @Test
    public void testRemoveStyleString() {
         fail("Not yet implemented");
    }

//    @Test
    public void testRemoveStyleStringString() {
         fail("Not yet implemented");
    }

//    @Test
    public void testRemoveStyleCssProperty() {
         fail("Not yet implemented");
    }

//    @Test
    public void testMarkAsImportant() {
         fail("Not yet implemented");
    }

//    @Test
    public void testMarkAsUnimportant() {
         fail("Not yet implemented");
    }

//    @Test
    public void testInit() {
         fail("Not yet implemented");
    }

//    @Test
    public void testIsImportantCssProperty() {
         fail("Not yet implemented");
    }

//    @Test
    public void testIsImportantString() {
         fail("Not yet implemented");
    }

//    @Test
    public void testGetCssProperty() {
         fail("Not yet implemented");
    }

//    @Test
    public void testStateChanged() {
         fail("Not yet implemented");
    }

//    @Test
    public void testTest() {

        final Style style = new Style();

        style.addCssProperties(true, AlignContent.CENTER);

        System.out.println("style.isImportant(AlignContent.CENTER) "
                + style.isImportant(AlignContent.CENTER));

        System.out.println("style.isImportant(AlignContent.FLEX_END) "
                + style.isImportant(AlignContent.FLEX_END));

        System.out.println("style.isImportant(ALIGN_CONTENT ) "
                + style.isImportant(CssNameConstants.ALIGN_CONTENT));

        System.out.println(style.toHtmlString());

        CssProperty cssProperty = style
                .getCssProperty(CssNameConstants.ALIGN_CONTENT);

        if (cssProperty != null
                && CssNameConstants.ALIGN_CONTENT.equals(cssProperty.getCssName())) {
            AlignContent alignContent = (AlignContent) cssProperty;
            System.out.println("alignContent.getCssValue() "+alignContent.getCssValue());
            System.out.println("cssProperty.getValue()"
                    + cssProperty.getCssValue());
        }

        style.removeCssProperty(AlignContent.CENTER);

        System.out.println(style.toHtmlString());

        WidthCss widthCss = new WidthCss(142.444F);
        style.addCssProperty("width", "100px");
        style.addCssProperties(widthCss);
        CssProperty widthCssProperty = style
                .getCssProperty(CssNameConstants.WIDTH);
        widthCssProperty = style.getCssProperty(CssNameConstants.WIDTH);
        System.out.println("widthCssProperty " + widthCssProperty);

        try {
            WidthCss clonedWidthCss = widthCss.clone();
            System.out.println("clonedWidthCss " + clonedWidthCss);
            if (!Objects.equals(clonedWidthCss, widthCss)) {
                System.out.println("SUCCESS : clonedWidthCss != widthCss");
            }
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

    }
    
    
    @Test
    public void testAddSupportForNewCustomCssClass() {
        Style style = new Style();
        {
            boolean addedStyleCustomAlignSelf = style.addCssProperty(CustomCssNameConstants.CUSTOM_ALIGN_SELF, CustomAlignSelf.BASELINE.toString());
            assertTrue(addedStyleCustomAlignSelf);
            CssProperty cssPropertyCustomAlignSelf = style.getCssProperty(CustomCssNameConstants.CUSTOM_ALIGN_SELF);
            assertNotNull(cssPropertyCustomAlignSelf);
            
            assertFalse(cssPropertyCustomAlignSelf instanceof CustomAlignSelf);
            
            Style.addSupportForNewCustomCssClass(CustomCssNameConstants.CUSTOM_ALIGN_SELF, CustomAlignSelf.class);
            
            cssPropertyCustomAlignSelf = style.getCssProperty(CustomCssNameConstants.CUSTOM_ALIGN_SELF);
            
            assertNotNull(cssPropertyCustomAlignSelf);
            assertTrue(cssPropertyCustomAlignSelf instanceof CustomAlignSelf);
        }
        System.out.println("testAddSupportForNewCustomCssClass succes");
    }
    
    @Test
    public void testRemoveSupportOfCssClass() {
        Style style = new Style();
        Style.addSupportForNewCustomCssClass(CustomCssNameConstants.CUSTOM_ALIGN_SELF, CustomAlignSelf.class);
        boolean addedStyleCustomAlignSelf = style.addCssProperty(CustomCssNameConstants.CUSTOM_ALIGN_SELF, CustomAlignSelf.BASELINE.toString());
        assertTrue(addedStyleCustomAlignSelf);
        
        CssProperty cssPropertyCustomAlignSelf = style.getCssProperty(CustomCssNameConstants.CUSTOM_ALIGN_SELF);
        assertNotNull(cssPropertyCustomAlignSelf);
        assertTrue(cssPropertyCustomAlignSelf instanceof CustomAlignSelf);
        
        Style.removeSupportOfCssClass(CustomAlignSelf.class);
        
        cssPropertyCustomAlignSelf = style.getCssProperty(CustomCssNameConstants.CUSTOM_ALIGN_SELF);
        assertNotNull(cssPropertyCustomAlignSelf);
        assertFalse(cssPropertyCustomAlignSelf instanceof CustomAlignSelf);
        System.out.println("testRemoveSupportOfCssClass success");
    }
    
    @Test
    public void testRemoveAllProperties() {
        Style style = new Style("background:green");

        assertEquals(1, style.getCssProperties().size());

        style.removeCssProperty("background");

        assertEquals(0, style.getCssProperties().size());

        style.addCssProperty("background", "green");

        assertEquals(1, style.getCssProperties().size());

        style.getCssProperties().clear();

        assertEquals(0, style.getCssProperties().size());

    }
    
    @Test
    public void testGetAttributeValue() {
        Assert.assertEquals("color:green;background:yellow;", new Style("color:green;background:yellow").getAttributeValue());
    }

}
