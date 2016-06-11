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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;

import org.junit.After;
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

        final long afterTimeMillis = System.currentTimeMillis();
        System.out.println("beforeTimeMillis " + beforeTimeMillis);
        System.out.println("afterTimeMillis " + afterTimeMillis);
        System.out.println(afterTimeMillis - beforeTimeMillis);

    }

    @Test
    public void testCloneTag() throws Exception {
        final Width width1 = new Width(1);
        final Html html1 = new Html(1, null, width1) {
            /**
             *
             */
            private static final long serialVersionUID = 1L;
            Html html2 = new Html(2, this, new Width(2)) {

                /**
                 *
                 */
                private static final long serialVersionUID = 1L;

                Html html3 = new Html(3, this, new Width(3)) {

                    /**
                     *
                     */
                    private static final long serialVersionUID = 1L;
                };

                Html html4 = new Html(4, this, new Width(4)) {

                    /**
                     *
                     */
                    private static final long serialVersionUID = 1L;
                };

            };

        };
        final Html clonedHtml1 = html1.clone();
       assertNotEquals(clonedHtml1, html1);

    }

    @Test
    public void testToStringPrint() throws Exception {
        final Width width1 = new Width(1);
        final Html html1 = new Html(1, null, width1, new Id("html1Id")) {
            /**
             *
             */
            private static final long serialVersionUID = 1L;
            Html html2 = new Html(2, this, new Width(2)) {

                /**
                 *
                 */
                private static final long serialVersionUID = 1L;

                Html html3 = new Html(3, this, new Width(3)) {

                    /**
                     *
                     */
                    private static final long serialVersionUID = 1L;
                };

                Html html4 = new Html(4, this, new Width(4)) {

                    /**
                     *
                     */
                    private static final long serialVersionUID = 1L;
                };

            };

        };
        final String expected = "<!DOCTYPE html>\n<html1 width=\"1.0%\" id=\"html1Id\"><html2 width=\"2.0%\"><html3 width=\"3.0%\"></html3><html4 width=\"4.0%\"></html4></html2></html1>";
        // System.out.println(html1);
        // System.out.println();
        // System.out.println(expected);
        html1.setPrependDocType(true);
       assertEquals(expected, html1.toString());

        {
            width1.setValue(56, CssLengthUnit.PER);
            final String expected2 = "<!DOCTYPE html>\n<html1 width=\"56%\" id=\"html1Id\"><html2 width=\"2.0%\"><html3 width=\"3.0%\"></html3><html4 width=\"4.0%\"></html4></html2></html1>";
           assertEquals(expected2, html1.toString());
        }

        {
            width1.setValue(55, CssLengthUnit.PX);
            final String expected2 = "<!DOCTYPE html>\n<html1 width=\"55px\" id=\"html1Id\"><html2 width=\"2.0%\"><html3 width=\"3.0%\"></html3><html4 width=\"4.0%\"></html4></html2></html1>";
           assertEquals(expected2, html1.toString());
        }

    }

    @Test
    public void testMultipleAttributesToHtmlString() throws Exception {
        final Html html = new Html(null, new Hidden(), new Id("htmlId"),
                new Style("color:red;width:15px"));
        final String expectedString = "<html hidden id=\"htmlId\" style=\"color:red;width:15px;\"></html>";
       assertEquals(expectedString, html.toString());

        if (html.toHtmlString().contains("\0")) {
            System.out.println("yes contains");
        }
    }

    @Test
    public void testSingleHtmlTag() throws Exception {
        final Html html = new Html(null);
        final String expectedString = "<html></html>";
       assertEquals(expectedString, html.toString());
    }

    @SuppressWarnings("serial")
    private class SampleAbstractHtmlView extends AbstractHtmlView {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        @Override
        public void develop(final OutputBuffer out) {
            final Html html = new Html(null);
            out.append(html);
        }

    }

    @Test
    public void testSingleHtmlTag2() throws Exception {
        final SampleAbstractHtmlView view = new SampleAbstractHtmlView();
        final String expectedString = "<html></html>";
       assertEquals(expectedString, view.toString());
       assertEquals(expectedString, view.toString());
    }

    @Test(expected = AssertionError.class)
    public void testSingleHtmlTag3() throws Exception {
        final SampleAbstractHtmlView view = new SampleAbstractHtmlView();
        view.setPreserveOutputBufferContent(true);
        final String expectedString = "<html></html>";
       assertEquals(expectedString, view.toString());
       assertEquals(expectedString, view.toString());
    }

    @Test
    public void testCustomTag1() throws Exception {
        final CustomTag customTag = new CustomTag("newtag",
                TagType.OPENING_CLOSING, null) {
            /**
             *
             */
            private static final long serialVersionUID = 1L;
            CustomTag c = new CustomTag("anothertag", TagType.OPENING_CLOSING,
                    this);
        };
        final String expectedString = "<newtag><anothertag></anothertag></newtag>";
       assertEquals(expectedString, customTag.toHtmlString());
    }

    @Test
    public void testCustomTag2() throws Exception {
        final CustomTag customTag = new CustomTag("newtag",
                TagType.OPENING_CLOSING, null) {
            /**
             *
             */
            private static final long serialVersionUID = 1L;
            CustomTag c = new CustomTag("anothertag", TagType.SELF_CLOSING,
                    this);
        };
        final String expectedString = "<newtag><anothertag/></newtag>";
       assertEquals(expectedString, customTag.toHtmlString());
    }

    @Test
    public void testCustomTag3() throws Exception {
        final CustomTag customTag = new CustomTag("newtag",
                TagType.OPENING_CLOSING, null) {
            /**
             *
             */
            private static final long serialVersionUID = 1L;
            CustomTag c = new CustomTag("anothertag", TagType.NON_CLOSING,
                    this);
        };
        final String expectedString = "<newtag><anothertag></newtag>";
       assertEquals(expectedString, customTag.toHtmlString());
    }

    @Test
    public void testCustomTag4() throws Exception {
        final CustomTag customTag = new CustomTag("newtag", TagType.NON_CLOSING,
                null) {
            /**
             *
             */
            private static final long serialVersionUID = 1L;
            CustomTag c = new CustomTag("anothertag", TagType.OPENING_CLOSING,
                    this);
        };
        final String expectedString = "<newtag><anothertag></anothertag>";
       assertEquals(expectedString, customTag.toHtmlString());
    }

    @Test
    public void testCustomTag5() throws Exception {
        final CustomTag customTag = new CustomTag("newtag", TagType.NON_CLOSING,
                null) {
            /**
             *
             */
            private static final long serialVersionUID = 1L;
            CustomTag c = new CustomTag("anothertag", TagType.SELF_CLOSING,
                    this);
        };
        final String expectedString = "<newtag><anothertag/>";
       assertEquals(expectedString, customTag.toHtmlString());
    }

    @Test
    public void testCustomTag6() throws Exception {
        final CustomTag customTag = new CustomTag("newtag", TagType.NON_CLOSING,
                null) {
            /**
             *
             */
            private static final long serialVersionUID = 1L;
            CustomTag c = new CustomTag("anothertag", TagType.NON_CLOSING,
                    this);
        };
        final String expectedString = "<newtag><anothertag>";
       assertEquals(expectedString, customTag.toHtmlString());
    }

    @Test
    public void testCustomTag7() throws Exception {
        final CustomTag customTag = new CustomTag("newtag",
                TagType.SELF_CLOSING, null) {
            /**
             *
             */
            private static final long serialVersionUID = 1L;
            CustomTag c = new CustomTag("anothertag", TagType.OPENING_CLOSING,
                    this);
        };
        final String expectedString = "<newtag/><anothertag></anothertag>";
       assertEquals(expectedString, customTag.toHtmlString());
    }

    @Test
    public void testCustomTag8() throws Exception {
        final CustomTag customTag = new CustomTag("newtag",
                TagType.SELF_CLOSING, null) {
            /**
             *
             */
            private static final long serialVersionUID = 1L;
            CustomTag c = new CustomTag("anothertag", TagType.SELF_CLOSING,
                    this);
        };
        final String expectedString = "<newtag/><anothertag/>";
       assertEquals(expectedString, customTag.toHtmlString());
    }

    @Test
    public void testCustomTag9() throws Exception {
        final CustomTag customTag = new CustomTag("newtag",
                TagType.SELF_CLOSING, null) {
            /**
             *
             */
            private static final long serialVersionUID = 1L;
            CustomTag c = new CustomTag("anothertag", TagType.NON_CLOSING,
                    this);
        };
        final String expectedString = "<newtag/><anothertag>";
       assertEquals(expectedString, customTag.toHtmlString());
    }

    @Test
    public void testMetaInfoTags() throws Exception {
        final Div div = new Div(null, new Id("1")) {
            /**
             *
             */
            private static final long serialVersionUID = 1L;
            Br br = new Br(this);
            Link link = new Link(this);
            Meta meta = new Meta(this);
            Div div = new Div(this, new Id("2"));
            Head head = new Head(null);
            Base base = new Base(null);

        };
        final String expectedString = "<div id=\"1\"><br/><link/><meta/><div id=\"2\"></div></div>";
       assertEquals(expectedString, div.toHtmlString());
    }

    @Test
    public void testToHtmlString() throws Exception {
        final Html html = new Html(null, new Hidden(), new Id("htmlId"),
                new Style("color:red;width:15px"));
        html.setPrependDocType(true);
        final String expectedString = "<!DOCTYPE html>\n<html hidden id=\"htmlId\" style=\"color:red;width:15px;\"></html>";
       assertEquals(expectedString, html.toHtmlString());

    }

    @Test
    public void testToHtmlStringWithCharset1() throws Exception {
        final Html html = new Html(null, new Hidden(), new Id("htmlId"),
                new Style("color:red;width:15px"));
        html.setPrependDocType(true);
        final String expectedString = "<!DOCTYPE html>\n<html hidden id=\"htmlId\" style=\"color:red;width:15px;\"></html>";
       assertEquals(expectedString,
                html.toHtmlString(StandardCharsets.UTF_8));
    }

    @Test
    public void testToHtmlStringWithCharset2() throws Exception {
        final Html html = new Html(null, new Hidden(), new Id("htmlId"),
                new Style("color:red;width:15px"));
        html.setPrependDocType(true);
        final String expectedString = "<!DOCTYPE html>\n<html hidden id=\"htmlId\" style=\"color:red;width:15px;\"></html>";
       assertEquals(expectedString,
                html.toHtmlString(StandardCharsets.UTF_8.name()));
    }

    @Test
    public void testToHtmlStringWithCharset3() throws Exception {
        final Html html = new Html(null, new Hidden(), new Id("htmlId"),
                new Style("color:red;width:15px"));
        html.setPrependDocType(true);
        final String expectedString = "<!DOCTYPE html>\n<html hidden id=\"htmlId\" style=\"color:red;width:15px;\"></html>";
       assertEquals(expectedString,
                html.toHtmlString(true, StandardCharsets.UTF_8));
    }

    @Test
    public void testToHtmlStringWithCharset4() throws Exception {
        final Html html = new Html(null, new Hidden(), new Id("htmlId"),
                new Style("color:red;width:15px"));
        html.setPrependDocType(true);
        final String expectedString = "<!DOCTYPE html>\n<html hidden id=\"htmlId\" style=\"color:red;width:15px;\"></html>";
       assertEquals(expectedString,
                html.toHtmlString(true, StandardCharsets.UTF_8.name()));
    }

}
