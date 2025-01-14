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
package com.webfirmframework.wffweb.css;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.webfirmframework.wffweb.InvalidValueException;
import com.webfirmframework.wffweb.css.core.CssProperty;
import com.webfirmframework.wffweb.css.file.CssBlock;

/**
 * @author WFF
 * @since 1.0.0
 * @version 1.0.0
 *
 */
public class BackgroundImageTest {

    /**
     * Test method for {@link com.webfirmframework.wffweb.css.BackgroundImage#BackgroundImage()}.
     */
    @Test
    public void testBackgroundImage() {
        
        BackgroundImage backgroundImage = new BackgroundImage();
        assertEquals(BackgroundImage.NONE, backgroundImage.getCssValue());
        backgroundImage.setCssValue("url(/images/HelloDesign.jpg)");
        assertEquals("url(/images/HelloDesign.jpg)", backgroundImage.getCssValue());
        
    }

    /**
     * Test method for {@link com.webfirmframework.wffweb.css.BackgroundImage#BackgroundImage(java.lang.String)}.
     */
    @Test
    public void testBackgroundImageString() {
        BackgroundImage backgroundImage = new BackgroundImage("url(/images/HelloDesign.jpg), url(/images/HelloDesign2.jpg)");
        assertEquals("url(\"/images/HelloDesign.jpg\"), url(\"/images/HelloDesign2.jpg\")", backgroundImage.getCssValue());
        backgroundImage.setCssValue("url(\"/images/HelloDesign3.jpg\"), url(\"/images/HelloDesign4.jpg\")");
        assertEquals("url(\"/images/HelloDesign3.jpg\"), url(\"/images/HelloDesign4.jpg\")", backgroundImage.getCssValue());
    }

    /**
     * Test method for {@link com.webfirmframework.wffweb.css.BackgroundImage#BackgroundImage(com.webfirmframework.wffweb.css.BackgroundImage)}.
     */
    @Test
    public void testBackgroundImageBackgroundImage() {
        BackgroundImage backgroundImage = new BackgroundImage(new BackgroundImage("url(/images/HelloDesign.jpg), url(/images/HelloDesign2.jpg)"));
        assertEquals("url(\"/images/HelloDesign.jpg\"), url(\"/images/HelloDesign2.jpg\")", backgroundImage.getCssValue());
        backgroundImage.setCssValue("url(\"/images/HelloDesign3.jpg\"), url(\"/images/HelloDesign4.jpg\")");
        assertEquals("url(\"/images/HelloDesign3.jpg\"), url(\"/images/HelloDesign4.jpg\")", backgroundImage.getCssValue());
    }

    /**
     * Test method for {@link com.webfirmframework.wffweb.css.BackgroundImage#BackgroundImage(java.lang.String[])}.
     */
    @Test
    public void testBackgroundImageStringArray() {
        BackgroundImage backgroundImage = new BackgroundImage("/images/HelloDesign.jpg", "/images/HelloDesign2.jpg");
        assertEquals("url(\"/images/HelloDesign.jpg\"), url(\"/images/HelloDesign2.jpg\")", backgroundImage.getCssValue());
    }

    /**
     * Test method for {@link com.webfirmframework.wffweb.css.BackgroundImage#BackgroundImage(com.webfirmframework.wffweb.css.UrlCss3Value[])}.
     */
    @Test
    public void testBackgroundImageUrlCss3ValueArray() {
        final UrlCss3Value urlCss3Value2 = new UrlCss3Value("url(\"/images/HelloDesign2.jpg\")");
        final UrlCss3Value urlCss3Value1 = new UrlCss3Value("url(\"/images/HelloDesign.jpg\")");
        BackgroundImage backgroundImage = new BackgroundImage(urlCss3Value1, urlCss3Value2);
        assertEquals("url(\"/images/HelloDesign.jpg\"), url(\"/images/HelloDesign2.jpg\")", backgroundImage.getCssValue());
    }
    
    /**
     * Test method for {@link com.webfirmframework.wffweb.css.BackgroundImage#BackgroundImage(com.webfirmframework.wffweb.css.UrlCss3Value[])}.
     */
    @Test(expected = InvalidValueException.class)
    public void testBackgroundImageUrlCss3ValueArrayInvalidValueAssignment1() {
        final UrlCss3Value urlCss3Value2 = new UrlCss3Value("url(\"/images/HelloDesign2.jpg\")");
        final UrlCss3Value urlCss3Value1 = new UrlCss3Value("url(\"/images/HelloDesign.jpg\")");
        BackgroundImage backgroundImage = new BackgroundImage(urlCss3Value1, urlCss3Value2);
        assertEquals("url(\"/images/HelloDesign.jpg\"), url(\"/images/HelloDesign2.jpg\")", backgroundImage.getCssValue());
        try {
            urlCss3Value1.setX(50);
        } catch (InvalidValueException e) {
            assertEquals(-1, urlCss3Value1.getX());
            assertEquals("url(\"/images/HelloDesign.jpg\")", urlCss3Value1.getValue());
            throw e;
        }
    }
    /**
     * Test method for {@link com.webfirmframework.wffweb.css.BackgroundImage#BackgroundImage(com.webfirmframework.wffweb.css.UrlCss3Value[])}.
     */
    @Test(expected = InvalidValueException.class)
    public void testBackgroundImageUrlCss3ValueArrayInvalidValueAssignment2() {
        final UrlCss3Value urlCss3Value2 = new UrlCss3Value("url(\"/images/HelloDesign2.jpg\")");
        final UrlCss3Value urlCss3Value1 = new UrlCss3Value("url(\"/images/HelloDesign.jpg\")");
        BackgroundImage backgroundImage = new BackgroundImage(urlCss3Value1, urlCss3Value2);
        assertEquals("url(\"/images/HelloDesign.jpg\"), url(\"/images/HelloDesign2.jpg\")", backgroundImage.getCssValue());
        try {
            urlCss3Value1.setY(50);
        } catch (InvalidValueException e) {
            assertEquals(-1, urlCss3Value1.getY());
            assertEquals("url(\"/images/HelloDesign.jpg\")", urlCss3Value1.getValue());
            throw e;
        }
    }

    /**
     * Test method for {@link com.webfirmframework.wffweb.css.BackgroundImage#getCssName()}.
     */
    @Test
    public void testGetCssName() {
        assertEquals(CssNameConstants.BACKGROUND_IMAGE, new BackgroundImage().getCssName());
    }

    /**
     * Test method for {@link com.webfirmframework.wffweb.css.BackgroundImage#getCssValue()}.
     */
    @Test
    public void testGetCssValue() {
        BackgroundImage backgroundImage = new BackgroundImage();
        backgroundImage.setCssValue("url(/images/HelloDesign.jpg), url(/images/HelloDesign2.jpg)");
        assertEquals("url(\"/images/HelloDesign.jpg\"), url(\"/images/HelloDesign2.jpg\")", backgroundImage.getCssValue());
        backgroundImage.setCssValue("url(\"/images/HelloDesign3.jpg\"), url(\"/images/HelloDesign4.jpg\")");
        assertEquals("url(\"/images/HelloDesign3.jpg\"), url(\"/images/HelloDesign4.jpg\")", backgroundImage.getCssValue());    
        backgroundImage.setAsInherit();
        assertNull(backgroundImage.getUrlCss3Values());
        assertEquals(BackgroundImage.INHERIT, backgroundImage.getCssValue());
    }

    /**
     * Test method for {@link com.webfirmframework.wffweb.css.BackgroundImage#toString()}.
     */
    @Test
    public void testToString() {
        BackgroundImage backgroundImage = new BackgroundImage("/images/HelloDesign.jpg", "/images/HelloDesign2.jpg");
        assertEquals(backgroundImage.getCssName()+": url(\"/images/HelloDesign.jpg\"), url(\"/images/HelloDesign2.jpg\")", backgroundImage.toString());
    }

    /**
     * Test method for {@link com.webfirmframework.wffweb.css.BackgroundImage#setCssValue(java.lang.String)}.
     */
    @Test
    public void testSetCssValueString() {
        BackgroundImage backgroundImage = new BackgroundImage();
        backgroundImage.setCssValue("url(/images/HelloDesign.jpg), url(/images/HelloDesign2.jpg)");
        assertEquals("url(\"/images/HelloDesign.jpg\"), url(\"/images/HelloDesign2.jpg\")", backgroundImage.getCssValue());
        backgroundImage.setCssValue("url(\"/images/HelloDesign3.jpg\"), url(\"/images/HelloDesign4.jpg\")");
        assertEquals("url(\"/images/HelloDesign3.jpg\"), url(\"/images/HelloDesign4.jpg\")", backgroundImage.getCssValue());    
    }

    /**
     * Test method for {@link com.webfirmframework.wffweb.css.BackgroundImage#setImageUrls(java.lang.String[])}.
     */
    @Test
    public void testSetImageUrlsStringArray() {
        BackgroundImage backgroundImage = new BackgroundImage();
        backgroundImage.setImageUrls("/images/HelloDesign.jpg", "/images/HelloDesign2.jpg");
        assertEquals("url(\"/images/HelloDesign.jpg\"), url(\"/images/HelloDesign2.jpg\")", backgroundImage.getCssValue());    
    }

    /**
     * Test method for {@link com.webfirmframework.wffweb.css.BackgroundImage#setImageUrls(com.webfirmframework.wffweb.css.UrlCss3Value[])}.
     */
    @Test
    public void testSetImageUrlsUrlCss3ValueArray() {
        final UrlCss3Value urlCss3Value2 = new UrlCss3Value("url(\"/images/HelloDesign2.jpg\")");
        final UrlCss3Value urlCss3Value1 = new UrlCss3Value("url(\"/images/HelloDesign.jpg\")");
        BackgroundImage backgroundImage = new BackgroundImage(urlCss3Value1, urlCss3Value2);
        assertEquals("url(\"/images/HelloDesign.jpg\"), url(\"/images/HelloDesign2.jpg\")", backgroundImage.getCssValue());
    }
    
    /**
     * Test method for {@link com.webfirmframework.wffweb.css.BackgroundImage#setImageUrls(com.webfirmframework.wffweb.css.UrlCss3Value[])}.
     */
    @Test(expected = InvalidValueException.class)
    public void testSetImageUrlsUrlCss3ValueArrayInvalidValueAssignment1() {
        final UrlCss3Value urlCss3Value2 = new UrlCss3Value("url(\"/images/HelloDesign2.jpg\")");
        final UrlCss3Value urlCss3Value1 = new UrlCss3Value("url(\"/images/HelloDesign.jpg\")");
        BackgroundImage backgroundImage = new BackgroundImage(urlCss3Value1, urlCss3Value2);
        assertEquals("url(\"/images/HelloDesign.jpg\"), url(\"/images/HelloDesign2.jpg\")", backgroundImage.getCssValue());
        try {
            urlCss3Value1.setX(50);
        } catch (InvalidValueException e) {
            assertEquals(-1, urlCss3Value1.getX());
            assertEquals("url(\"/images/HelloDesign.jpg\")", urlCss3Value1.getValue());
            throw e;
        }
    }
    
    /**
     * Test method for {@link com.webfirmframework.wffweb.css.BackgroundImage#setImageUrls(com.webfirmframework.wffweb.css.UrlCss3Value[])}.
     */
    @Test(expected = InvalidValueException.class)
    public void testSetImageUrlsUrlCss3ValueArrayInvalidValueAssignment2() {
        final UrlCss3Value urlCss3Value2 = new UrlCss3Value("url(\"/images/HelloDesign2.jpg\")");
        final UrlCss3Value urlCss3Value1 = new UrlCss3Value("url(\"/images/HelloDesign.jpg\")");
        BackgroundImage backgroundImage = new BackgroundImage(urlCss3Value1, urlCss3Value2);
        assertEquals("url(\"/images/HelloDesign.jpg\"), url(\"/images/HelloDesign2.jpg\")", backgroundImage.getCssValue());
        try {
            urlCss3Value1.setX(50);
        } catch (InvalidValueException e) {
            assertEquals(-1, urlCss3Value1.getX());
            assertEquals("url(\"/images/HelloDesign.jpg\")", urlCss3Value1.getValue());
            throw e;
        }
    }

    /**
     * Test method for {@link com.webfirmframework.wffweb.css.BackgroundImage#getUrlCss3Values()}.
     */
    @Test
    public void testGetUrlCss3Values() {
        {
            BackgroundImage cursor = new BackgroundImage();
            String cssValue = "url(\"Test.png\") , url(sample)";
            cursor.setCssValue(cssValue);

            assertEquals(
                    CssNameConstants.BACKGROUND_IMAGE+": url(\"Test.png\"), url(\"sample\")",
                    cursor.toString());
            List<UrlCss3Value> urlCss3ValuesFirst = cursor.getUrlCss3Values();
            assertEquals(2, urlCss3ValuesFirst.size());
            assertEquals("Test.png", urlCss3ValuesFirst.get(0).getUrl());
            assertEquals("sample", urlCss3ValuesFirst.get(1).getUrl());
            assertEquals(-1, urlCss3ValuesFirst.get(1).getX());
            assertEquals(-1, urlCss3ValuesFirst.get(1).getY());

        }
        {
            BackgroundImage cursor = new BackgroundImage();
            UrlCss3Value urlCss3Value1 = new UrlCss3Value("url(\"Test.png\")");
            UrlCss3Value urlCss3Value2 = new UrlCss3Value("url(sample)");
            cursor.setImageUrls(urlCss3Value1, urlCss3Value2);

            assertEquals(CssNameConstants.BACKGROUND_IMAGE +
                    ": url(\"Test.png\"), url(\"sample\")",
                    cursor.toString());
            List<UrlCss3Value> urlCss3ValuesFirst = cursor.getUrlCss3Values();
            assertEquals(2, urlCss3ValuesFirst.size());
            assertEquals("Test.png", urlCss3ValuesFirst.get(0).getUrl());
            assertEquals("sample", urlCss3ValuesFirst.get(1).getUrl());
            assertEquals(-1, urlCss3ValuesFirst.get(1).getX());
            assertEquals(-1, urlCss3ValuesFirst.get(1).getY());

        }    
    }

    /**
     * Test method for {@link com.webfirmframework.wffweb.css.BackgroundImage#setAsInitial()}.
     */
    @Test
    public void testSetAsInitial() {
        BackgroundImage backgroundImage = new BackgroundImage();
        backgroundImage.setImageUrls("/images/HelloDesign.jpg", "/images/HelloDesign2.jpg");
        assertEquals("url(\"/images/HelloDesign.jpg\"), url(\"/images/HelloDesign2.jpg\")", backgroundImage.getCssValue());
        assertNotNull(backgroundImage.getUrlCss3Values());
        assertEquals(2, backgroundImage.getUrlCss3Values().size());
        backgroundImage.setAsInitial();
        assertNull(backgroundImage.getUrlCss3Values());
        assertEquals(BackgroundImage.INITIAL, backgroundImage.getCssValue());
    }

    /**
     * Test method for {@link com.webfirmframework.wffweb.css.BackgroundImage#setAsInherit()}.
     */
    @Test
    public void testSetAsInherit() {
        BackgroundImage backgroundImage = new BackgroundImage();
        backgroundImage.setImageUrls("/images/HelloDesign.jpg", "/images/HelloDesign2.jpg");
        assertEquals("url(\"/images/HelloDesign.jpg\"), url(\"/images/HelloDesign2.jpg\")", backgroundImage.getCssValue());
        assertNotNull(backgroundImage.getUrlCss3Values());
        assertEquals(2, backgroundImage.getUrlCss3Values().size());
        backgroundImage.setAsInherit();
        assertNull(backgroundImage.getUrlCss3Values());
        assertEquals(BackgroundImage.INHERIT, backgroundImage.getCssValue());
    }

    /**
     * Test method for {@link com.webfirmframework.wffweb.css.BackgroundImage#stateChanged(com.webfirmframework.wffweb.data.Bean)}.
     */
//    @Test
//    public void testStateChanged() {
//        fail("Not yet implemented");
//    }
    
    
    private static final String URL_CSS_VALUE_STRING = "url(\"data:image/svg+xml;charset=utf8,<svg width='35px' height='35px' xmlns='http://www.w3.org/2000/svg' viewBox='0 0 100 100' preserveAspectRatio='xMidYMid' class='uil-hourglass'><rect x='0' y='0' width='100' height='100' fill='none' class='bk'></rect><g><path fill='none' stroke='#007282' stroke-width='5' stroke-miterlimit='10' d='M58.4,51.7c-0.9-0.9-1.4-2-1.4-2.3s0.5-0.4,1.4-1.4 C70.8,43.8,79.8,30.5,80,15.5H70H30H20c0.2,15,9.2,28.1,21.6,32.3c0.9,0.9,1.4,1.2,1.4,1.5s-0.5,1.6-1.4,2.5 C29.2,56.1,20.2,69.5,20,85.5h10h40h10C79.8,69.5,70.8,55.9,58.4,51.7z' class='glass'></path><clipPath id='uil-hourglass-clip1'><rect x='15' y='20' width='70' height='25' class='clip'><animate attributeName='height' from='25' to='0' dur='1s' repeatCount='indefinite' vlaues='25;0;0' keyTimes='0;0.5;1'></animate><animate attributeName='y' from='20' to='45' dur='1s' repeatCount='indefinite' vlaues='20;45;45' keyTimes='0;0.5;1'></animate></rect></clipPath><clipPath id='uil-hourglass-clip2'><rect x='15' y='55' width='70' height='25' class='clip'><animate attributeName='height' from='0' to='25' dur='1s' repeatCount='indefinite' vlaues='0;25;25' keyTimes='0;0.5;1'></animate><animate attributeName='y' from='80' to='55' dur='1s' repeatCount='indefinite' vlaues='80;55;55' keyTimes='0;0.5;1'></animate></rect></clipPath><path d='M29,23c3.1,11.4,11.3,19.5,21,19.5S67.9,34.4,71,23H29z' clip-path='url(#uil-hourglass-clip1)' fill='#ffab00' class='sand'></path><path d='M71.6,78c-3-11.6-11.5-20-21.5-20s-18.5,8.4-21.5,20H71.6z' clip-path='url(#uil-hourglass-clip2)' fill='#ffab00' class='sand'></path><animateTransform attributeName='transform' type='rotate' from='0 50 50' to='180 50 50' repeatCount='indefinite' dur='1s' values='0 50 50;0 50 50;180 50 50' keyTimes='0;0.7;1'></animateTransform></g></svg>\")";
    
    @SuppressWarnings("serial")
    private static final CssBlock processingSvg = new CssBlock(".processing") {

        @Override
        protected void load(final Set<CssProperty> cssProperties) {
              UrlCss3Value urlCss3Value = new UrlCss3Value(URL_CSS_VALUE_STRING);
              BackgroundImage backgroundImage = new BackgroundImage(urlCss3Value);
            cssProperties.add(backgroundImage);
            cssProperties.add(BackgroundRepeat.NO_REPEAT);
        }
        
    };
    
    @Test
    public void testDataStringInBackgroundImage() throws Exception {      
        {
            UrlCss3Value urlCss3Value = new UrlCss3Value(URL_CSS_VALUE_STRING);
            
            assertEquals(URL_CSS_VALUE_STRING, urlCss3Value.getValue());
            assertEquals(URL_CSS_VALUE_STRING.substring(5, URL_CSS_VALUE_STRING.length() - 2), urlCss3Value.getUrl());
            
            System.out.println(urlCss3Value.toString());
            
            
            
            
            BackgroundImage backgroundImage = new BackgroundImage(urlCss3Value);
            
            System.out.println(backgroundImage.toCssString());
            
            assertEquals(".processing{background-image:" + URL_CSS_VALUE_STRING + ";background-repeat:no-repeat;}", processingSvg.toCssString());
            
            System.out.println(processingSvg.toCssString());
        }
    }


}
