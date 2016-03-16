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
package com.webfirmframework.wffweb.common.test;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.webfirmframework.wffweb.css.CssLengthUnit;
import com.webfirmframework.wffweb.io.OutputBuffer;
import com.webfirmframework.wffweb.tag.html.AbstractHtml.TagType;
import com.webfirmframework.wffweb.tag.html.Br;
import com.webfirmframework.wffweb.tag.html.Html;
import com.webfirmframework.wffweb.tag.html.attribute.Width;
import com.webfirmframework.wffweb.tag.html.attribute.global.Id;
import com.webfirmframework.wffweb.tag.html.attribute.global.Style;
import com.webfirmframework.wffweb.tag.html.html5.attribute.global.Hidden;
import com.webfirmframework.wffweb.tag.html.links.Link;
import com.webfirmframework.wffweb.tag.html.metainfo.Base;
import com.webfirmframework.wffweb.tag.html.metainfo.Head;
import com.webfirmframework.wffweb.tag.html.metainfo.Meta;
import com.webfirmframework.wffweb.tag.html.stylesandsemantics.Div;
import com.webfirmframework.wffweb.tag.htmlwff.CustomTag;
import com.webfirmframework.wffweb.view.AbstractHtmlView;

/**
 * 
 * @author WFF
 * @since 1.0.0
 */
public class TagPrintTest implements Serializable {

    private static final long serialVersionUID = 1_0_0L;
    
    private long beforeTimeMillis;

    private long afterTimeMillis;

    /**
     * 
     * @author WFF
     * @since 1.0.0
     */
    @Before
    public void before() {
        beforeTimeMillis = System.currentTimeMillis();
    }

    /**
     * 
     * @author WFF
     * @since 1.0.0
     */
    @After
    public void after() {
        afterTimeMillis = System.currentTimeMillis();
        System.out.println("beforeTimeMillis " + beforeTimeMillis);
        System.out.println("afterTimeMillis " + afterTimeMillis);
        System.out.println(afterTimeMillis - beforeTimeMillis);
        
    }

    @Test
    public void testCloneTag() throws Exception {
        final Width width1 = new Width(1);
        final Html html1 = new Html(1, null, width1) {
            Html html2 = new Html(2, this, new Width(2)) {

                Html html3 = new Html(3, this, new Width(3)) {
                };

                Html html4 = new Html(4, this, new Width(4)) {
                };

            };

        };
        Html clonedHtml1 = html1.clone();
        Assert.assertNotEquals(clonedHtml1, html1);

    }

    @Test
    public void testToStringPrint() throws Exception {
        final Width width1 = new Width(1);
        final Html html1 = new Html(1, null, width1, new Id("html1Id")) {
            Html html2 = new Html(2, this, new Width(2)) {

                Html html3 = new Html(3, this, new Width(3)) {
                };

                Html html4 = new Html(4, this, new Width(4)) {
                };

            };

        };
        String expected = "<!DOCTYPE html>\n<html1 width=\"1.0%\" id=\"html1Id\"><html2 width=\"2.0%\"><html3 width=\"3.0%\"></html3><html4 width=\"4.0%\"></html4></html2></html1>";
//         System.out.println(html1);
        // System.out.println();
        // System.out.println(expected);
        html1.setPrependDocType(true);
        Assert.assertEquals(expected, html1.toString());

        {
            width1.setValue(56, CssLengthUnit.PER);
            String expected2 = "<!DOCTYPE html>\n<html1 width=\"56%\" id=\"html1Id\"><html2 width=\"2.0%\"><html3 width=\"3.0%\"></html3><html4 width=\"4.0%\"></html4></html2></html1>";
            Assert.assertEquals(expected2, html1.toString());
        }

        {
            width1.setValue(55, CssLengthUnit.PX);
            String expected2 = "<!DOCTYPE html>\n<html1 width=\"55px\" id=\"html1Id\"><html2 width=\"2.0%\"><html3 width=\"3.0%\"></html3><html4 width=\"4.0%\"></html4></html2></html1>";
            Assert.assertEquals(expected2, html1.toString());
        }

    }
    
    @Test
    public void testMultipleAttributesToHtmlString() throws Exception {
        Html html = new Html(null, new Hidden(), new Id("htmlId"), new Style(
                "color:red;width:15px"));
        String expectedString = "<html hidden id=\"htmlId\" style=\"color: red; width: 15px;\"></html>";
        Assert.assertEquals(expectedString, html.toString());

        if (html.toHtmlString().contains("\0")) {
            System.out.println("yes contains");
        }
    }

    @Test
    public void testSingleHtmlTag() throws Exception {
        Html html = new Html(null);
        String expectedString = "<html></html>";
        Assert.assertEquals(expectedString, html.toString());
    }
    
    @SuppressWarnings("serial")
    private class SampleAbstractHtmlView extends AbstractHtmlView {

        @Override
        public void develop(OutputBuffer out) {
            Html html = new Html(null);
            out.append(html);
        }
        
    }
    
    @Test
    public void testSingleHtmlTag2() throws Exception {
        SampleAbstractHtmlView view = new SampleAbstractHtmlView();
        String expectedString = "<html></html>";
        Assert.assertEquals(expectedString, view.toString());
        Assert.assertEquals(expectedString, view.toString());
    }
    
    @Test(expected = AssertionError.class)
    public void testSingleHtmlTag3() throws Exception {
        SampleAbstractHtmlView view = new SampleAbstractHtmlView();
        view.setPreserveOutputBufferContent(true);
        String expectedString = "<html></html>";
        Assert.assertEquals(expectedString, view.toString());
        Assert.assertEquals(expectedString, view.toString());
    }
    
    @Test
    public void testCustomTag1() throws Exception {
        CustomTag customTag = new CustomTag("newtag", TagType.OPENING_CLOSING, null) {
            CustomTag c = new CustomTag("anothertag", TagType.OPENING_CLOSING,
                    this);
        };
        String expectedString = "<newtag><anothertag></anothertag></newtag>";
        Assert.assertEquals(expectedString, customTag.toHtmlString());
    }
    
    
    @Test
    public void testCustomTag2() throws Exception {
        CustomTag customTag = new CustomTag("newtag", TagType.OPENING_CLOSING, null) {
            CustomTag c = new CustomTag("anothertag", TagType.SELF_CLOSING,
                    this);
        };
        String expectedString = "<newtag><anothertag/></newtag>";
        Assert.assertEquals(expectedString, customTag.toHtmlString());
    }

    @Test
    public void testCustomTag3() throws Exception {
        CustomTag customTag = new CustomTag("newtag", TagType.OPENING_CLOSING, null) {
            CustomTag c = new CustomTag("anothertag", TagType.NON_CLOSING,
                    this);
        };
        String expectedString = "<newtag><anothertag></newtag>";
        Assert.assertEquals(expectedString, customTag.toHtmlString());
    }
    
    
    @Test
    public void testCustomTag4() throws Exception {
        CustomTag customTag = new CustomTag("newtag", TagType.NON_CLOSING, null) {
            CustomTag c = new CustomTag("anothertag", TagType.OPENING_CLOSING,
                    this);
        };
        String expectedString = "<newtag><anothertag></anothertag>";
        Assert.assertEquals(expectedString, customTag.toHtmlString());
    }
    
    
    @Test
    public void testCustomTag5() throws Exception {
        CustomTag customTag = new CustomTag("newtag", TagType.NON_CLOSING, null) {
            CustomTag c = new CustomTag("anothertag", TagType.SELF_CLOSING,
                    this);
        };
        String expectedString = "<newtag><anothertag/>";
        Assert.assertEquals(expectedString, customTag.toHtmlString());
    }

    @Test
    public void testCustomTag6() throws Exception {
        CustomTag customTag = new CustomTag("newtag", TagType.NON_CLOSING, null) {
            CustomTag c = new CustomTag("anothertag", TagType.NON_CLOSING,
                    this);
        };
        String expectedString = "<newtag><anothertag>";
        Assert.assertEquals(expectedString, customTag.toHtmlString());
    }
    
    @Test
    public void testCustomTag7() throws Exception {
        CustomTag customTag = new CustomTag("newtag", TagType.SELF_CLOSING, null) {
            CustomTag c = new CustomTag("anothertag", TagType.OPENING_CLOSING,
                    this);
        };
        String expectedString = "<newtag/><anothertag></anothertag>";
        Assert.assertEquals(expectedString, customTag.toHtmlString());
    }
    
    
    @Test
    public void testCustomTag8() throws Exception {
        CustomTag customTag = new CustomTag("newtag", TagType.SELF_CLOSING, null) {
            CustomTag c = new CustomTag("anothertag", TagType.SELF_CLOSING,
                    this);
        };
        String expectedString = "<newtag/><anothertag/>";
        Assert.assertEquals(expectedString, customTag.toHtmlString());
    }

    @Test
    public void testCustomTag9() throws Exception {
        CustomTag customTag = new CustomTag("newtag", TagType.SELF_CLOSING, null) {
            CustomTag c = new CustomTag("anothertag", TagType.NON_CLOSING,
                    this);
        };
        String expectedString = "<newtag/><anothertag>";
        Assert.assertEquals(expectedString, customTag.toHtmlString());
    }
    
    @Test
    public void testMetaInfoTags() throws Exception {
        Div div = new Div(null, new Id("1")) {
            Br br = new Br(this);
            Link link = new Link(this);
            Meta meta = new Meta(this);
            Div div = new Div(this, new Id("2"));
            Head head = new Head(null);
            Base base = new Base(null);

        };
        String expectedString = "<div id=\"1\"><br/><link/><meta/><div id=\"2\"></div></div>";
        Assert.assertEquals(expectedString, div.toHtmlString());
    }
    
    @Test
    public void testToHtmlString() throws Exception {
        Html html = new Html(null, new Hidden(), new Id("htmlId"), new Style(
                "color:red;width:15px"));
        html.setPrependDocType(true);
        String expectedString = "<!DOCTYPE html>\n<html hidden id=\"htmlId\" style=\"color: red; width: 15px;\"></html>";
        Assert.assertEquals(expectedString, html.toHtmlString());

    }
    
    @Test
    public void testToHtmlStringWithCharset1() throws Exception {
        Html html = new Html(null, new Hidden(), new Id("htmlId"), new Style(
                "color:red;width:15px"));
        html.setPrependDocType(true);
        String expectedString = "<!DOCTYPE html>\n<html hidden id=\"htmlId\" style=\"color: red; width: 15px;\"></html>";
        Assert.assertEquals(expectedString, html.toHtmlString(StandardCharsets.UTF_8));
    }
    
    @Test
    public void testToHtmlStringWithCharset2() throws Exception {
        Html html = new Html(null, new Hidden(), new Id("htmlId"), new Style(
                "color:red;width:15px"));
        html.setPrependDocType(true);
        String expectedString = "<!DOCTYPE html>\n<html hidden id=\"htmlId\" style=\"color: red; width: 15px;\"></html>";
        Assert.assertEquals(expectedString, html.toHtmlString(StandardCharsets.UTF_8.name()));
    }
    
    @Test
    public void testToHtmlStringWithCharset3() throws Exception {
        Html html = new Html(null, new Hidden(), new Id("htmlId"), new Style(
                "color:red;width:15px"));
        html.setPrependDocType(true);
        String expectedString = "<!DOCTYPE html>\n<html hidden id=\"htmlId\" style=\"color: red; width: 15px;\"></html>";
        Assert.assertEquals(expectedString, html.toHtmlString(true, StandardCharsets.UTF_8));
    }
    
    @Test
    public void testToHtmlStringWithCharset4() throws Exception {
        Html html = new Html(null, new Hidden(), new Id("htmlId"), new Style(
                "color:red;width:15px"));
        html.setPrependDocType(true);
        String expectedString = "<!DOCTYPE html>\n<html hidden id=\"htmlId\" style=\"color: red; width: 15px;\"></html>";
        Assert.assertEquals(expectedString, html.toHtmlString(true, StandardCharsets.UTF_8.name()));
    }

}
