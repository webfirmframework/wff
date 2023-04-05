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
package com.webfirmframework.wffweb.tag.html;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.Assert;
import org.junit.Test;

import com.webfirmframework.wffweb.tag.html.attribute.global.Id;
import com.webfirmframework.wffweb.tag.html.stylesandsemantics.Div;
import com.webfirmframework.wffweb.tag.html.stylesandsemantics.Span;
import com.webfirmframework.wffweb.tag.htmlwff.TagContent;

@SuppressWarnings("serial")
public class HtmlTest {

    @Test
    public void testHtmlToStringSetPrependDocType() throws IOException {

        Html html = new Html(null) {
            {
                new Div(this, new Id("id")).give(TagContent::text, "ä, ö, and ü are Umlauts. 😀");
            }
        };

        final String expected = "<html><div id=\"id\">ä, ö, and ü are Umlauts. 😀</div></html>";
        Assert.assertEquals(expected, html.toHtmlString());

        Assert.assertEquals(expected, html.toHtmlString(true));
        Assert.assertEquals(expected, html.toHtmlString(false));

        Assert.assertEquals(expected, html.toHtmlString());

        Assert.assertEquals(expected, html.toHtmlString());

        Assert.assertEquals(expected, html.toHtmlString(true));
        Assert.assertEquals(expected, html.toHtmlString(false));

        Assert.assertEquals(expected,
                html.toHtmlString(true));
        Assert.assertEquals(expected,
                html.toHtmlString(false));

        html.setPrependDocType(true);

        final String expected2 = "<!DOCTYPE html>\n<html><div id=\"id\">ä, ö, and ü are Umlauts. 😀</div></html>";
        Assert.assertEquals(expected2, html.toHtmlString(false));

        Assert.assertEquals(expected2, html.toHtmlString());

        Assert.assertEquals(expected2, html.toHtmlString(true));

        Assert.assertEquals(expected2,
                html.toHtmlString());

        Assert.assertEquals(expected2,
                html.toHtmlString());

        Assert.assertEquals(expected2,
                html.toHtmlString(true));
        Assert.assertEquals(expected2,
                html.toHtmlString(false));

        Assert.assertEquals(expected2,
                html.toHtmlString(true));
        Assert.assertEquals(expected2,
                html.toHtmlString(false));

        html.setPrependDocType(false);

        Assert.assertEquals(expected, html.toHtmlString(false));
        Assert.assertEquals(expected, html.toHtmlString());
        Assert.assertEquals(expected, html.toHtmlString(true));

        Assert.assertEquals(expected, html.toHtmlString());

        Assert.assertEquals(expected, html.toHtmlString());

        Assert.assertEquals(expected, html.toHtmlString(true));
        Assert.assertEquals(expected, html.toHtmlString(false));

        Assert.assertEquals(expected,
                html.toHtmlString(true));
        Assert.assertEquals(expected,
                html.toHtmlString(false));

    }

    @Test
    public void testHtmlToBigOutputStreamSetPrependDocType() throws IOException {

        Html html = new Html(null) {
            {
                new Div(this, new Id("id")).give(TagContent::text, "ä, ö, and ü are Umlauts. 😀");
            }
        };

        String expected = "<html><div id=\"id\">ä, ö, and ü are Umlauts. 😀</div></html>";
        {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            int written = html.toBigOutputStream(os);
            Assert.assertEquals(expected.getBytes().length, written);
            Assert.assertEquals(expected, os.toString());
        }

        {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            int written = html.toBigOutputStream(os, true);
            Assert.assertEquals(expected.getBytes().length, written);
            Assert.assertEquals(expected, os.toString());
        }
        {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            int written = html.toBigOutputStream(os, true);
            Assert.assertEquals(expected.getBytes().length, written);
            Assert.assertEquals(expected, os.toString());
        }

        {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            int written = html.toBigOutputStream(os, StandardCharsets.UTF_8);
            Assert.assertEquals(expected.getBytes(StandardCharsets.UTF_8).length, written);
            Assert.assertEquals(expected, os.toString("UTF-8"));
        }

        {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            int written = html.toBigOutputStream(os, StandardCharsets.UTF_8.name());
            Assert.assertEquals(expected.getBytes(StandardCharsets.UTF_8.name()).length, written);
            Assert.assertEquals(expected, os.toString("UTF-8"));
        }

        {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            int written = html.toBigOutputStream(os, true, StandardCharsets.UTF_8);
            Assert.assertEquals(expected.getBytes(StandardCharsets.UTF_8).length, written);
            Assert.assertEquals(expected, os.toString("UTF-8"));
        }

        {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            int written = html.toBigOutputStream(os, false, StandardCharsets.UTF_8);
            Assert.assertEquals(expected.getBytes(StandardCharsets.UTF_8).length, written);
            Assert.assertEquals(expected, os.toString("UTF-8"));
        }

        {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            int written = html.toBigOutputStream(os, true, StandardCharsets.UTF_8.name());
            Assert.assertEquals(expected.getBytes(StandardCharsets.UTF_8.name()).length, written);
            Assert.assertEquals(expected, os.toString("UTF-8"));
        }
        {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            int written = html.toBigOutputStream(os, false, StandardCharsets.UTF_8.name());
            Assert.assertEquals(expected.getBytes(StandardCharsets.UTF_8.name()).length, written);
            Assert.assertEquals(expected, os.toString("UTF-8"));
        }

        html.setPrependDocType(true);

        String expected2 = "<!DOCTYPE html>\n<html><div id=\"id\">ä, ö, and ü are Umlauts. 😀</div></html>";
        {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            int written = html.toBigOutputStream(os, false);
            Assert.assertEquals(expected2.getBytes().length, written);
            Assert.assertEquals(expected2, os.toString());

        }

        {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            int written = html.toBigOutputStream(os);
            Assert.assertEquals(expected2.getBytes().length, written);
            Assert.assertEquals(expected2, os.toString());
        }

        {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            int written = html.toBigOutputStream(os, true);
            Assert.assertEquals(expected2.getBytes().length, written);
            Assert.assertEquals(expected2, os.toString());
        }

        {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            int written = html.toBigOutputStream(os, StandardCharsets.UTF_8);
            Assert.assertEquals(expected2.getBytes(StandardCharsets.UTF_8).length, written);
            Assert.assertEquals(expected2, os.toString("UTF-8"));
        }

        {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            int written = html.toBigOutputStream(os, StandardCharsets.UTF_8.name());
            Assert.assertEquals(expected2.getBytes(StandardCharsets.UTF_8).length, written);
            Assert.assertEquals(expected2, os.toString("UTF-8"));
        }

        {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            int written = html.toBigOutputStream(os, true, StandardCharsets.UTF_8);
            Assert.assertEquals(expected2.getBytes(StandardCharsets.UTF_8).length, written);
            Assert.assertEquals(expected2, os.toString("UTF-8"));
        }

        {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            int written = html.toBigOutputStream(os, false, StandardCharsets.UTF_8);
            Assert.assertEquals(expected2.getBytes(StandardCharsets.UTF_8).length, written);
            Assert.assertEquals(expected2, os.toString("UTF-8"));
        }

        {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            int written = html.toBigOutputStream(os, true, StandardCharsets.UTF_8.name());
            Assert.assertEquals(expected2.getBytes(StandardCharsets.UTF_8.name()).length, written);
            Assert.assertEquals(expected2, os.toString("UTF-8"));
        }

        {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            int written = html.toBigOutputStream(os, false, StandardCharsets.UTF_8.name());
            Assert.assertEquals(expected2.getBytes(StandardCharsets.UTF_8).length, written);
            Assert.assertEquals(expected2, os.toString("UTF-8"));
        }

        html.setPrependDocType(false);

        {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            int written = html.toBigOutputStream(os, false);
            Assert.assertEquals(expected.getBytes().length, written);
            Assert.assertEquals(expected, os.toString());
        }
        {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            int written = html.toBigOutputStream(os);
            Assert.assertEquals(expected.getBytes().length, written);
            Assert.assertEquals(expected, os.toString());
        }
        {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            int written = html.toBigOutputStream(os, true);
            Assert.assertEquals(expected.getBytes().length, written);
            Assert.assertEquals(expected, os.toString());
        }
        {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            int written = html.toBigOutputStream(os, StandardCharsets.UTF_8);
            Assert.assertEquals(expected.getBytes(StandardCharsets.UTF_8).length, written);
            Assert.assertEquals(expected, os.toString("UTF-8"));
        }
        {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            int written = html.toBigOutputStream(os, StandardCharsets.UTF_8.name());
            Assert.assertEquals(expected.getBytes(StandardCharsets.UTF_8).length, written);
            Assert.assertEquals(expected, os.toString("UTF-8"));
        }
        {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            int written = html.toBigOutputStream(os, true, StandardCharsets.UTF_8);
            Assert.assertEquals(expected.getBytes(StandardCharsets.UTF_8).length, written);
            Assert.assertEquals(expected, os.toString("UTF-8"));
        }
        {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            int written = html.toBigOutputStream(os, false, StandardCharsets.UTF_8);
            Assert.assertEquals(expected.getBytes(StandardCharsets.UTF_8).length, written);
            Assert.assertEquals(expected, os.toString("UTF-8"));
        }
        {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            int written = html.toBigOutputStream(os, true, StandardCharsets.UTF_8.name());
            Assert.assertEquals(expected.getBytes(StandardCharsets.UTF_8.name()).length, written);
            Assert.assertEquals(expected, os.toString("UTF-8"));
        }
        {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            int written = html.toBigOutputStream(os, false, StandardCharsets.UTF_8.name());
            Assert.assertEquals(expected.getBytes(StandardCharsets.UTF_8.name()).length, written);
            Assert.assertEquals(expected, os.toString("UTF-8"));
        }

    }

    @Test
    public void testConstructorLock() throws Exception {
        Html html = new Html(null) {
            {
                new Div(this, new Id("id"));
            }
        };
    }

    @Test
    public void testBigHtmlToStringSetPrependDocType() {

        {
            Html html = new Html(null);
            Assert.assertEquals("<html></html>", html.toBigHtmlString(true));
            html.setPrependDocType(true);
            Assert.assertEquals("<!DOCTYPE html>\n<html></html>", html.toBigHtmlString(true));
        }
        {
            Div div = new Div(null);
            Assert.assertEquals("<div></div>", div.toBigHtmlString(true));
            div.appendChild(new Span(null));
            Assert.assertEquals("<div><span></span></div>", div.toBigHtmlString(true));
        }

        Html html = new Html(null) {
            {
                new Div(this, new Id("id")).give(TagContent::text, "ä, ö, and ü are Umlauts. 😀");
            }
        };

        final String expected = "<html><div id=\"id\">ä, ö, and ü are Umlauts. 😀</div></html>";
        Assert.assertEquals(expected, html.toBigHtmlString());

        Assert.assertEquals(expected, html.toBigHtmlString(true));
        Assert.assertEquals(expected, html.toBigHtmlString(false));

        Assert.assertEquals(expected, html.toBigHtmlString(true));

        Assert.assertEquals(expected, html.toBigHtmlString());

        Assert.assertEquals(expected, html.toBigHtmlString(true));
        Assert.assertEquals(expected, html.toBigHtmlString(false));

        Assert.assertEquals(expected, html.toBigHtmlString(true));
        Assert.assertEquals(expected, html.toBigHtmlString(false));

        html.setPrependDocType(true);

        final String expected2 = "<!DOCTYPE html>\n<html><div id=\"id\">ä, ö, and ü are Umlauts. 😀</div></html>";
        Assert.assertEquals(expected2, html.toBigHtmlString(false));

        Assert.assertEquals(expected2, html.toBigHtmlString());

        Assert.assertEquals(expected2, html.toBigHtmlString(true));

        Assert.assertEquals(expected2, html.toBigHtmlString());

        Assert.assertEquals(expected2, html.toBigHtmlString());

        Assert.assertEquals(expected2, html.toBigHtmlString(true));
        Assert.assertEquals(expected2, html.toBigHtmlString(false));

        Assert.assertEquals(expected2, html.toBigHtmlString(true));
        Assert.assertEquals(expected2, html.toBigHtmlString(false));

        html.setPrependDocType(false);

        Assert.assertEquals(expected, html.toBigHtmlString(false));
        Assert.assertEquals(expected, html.toBigHtmlString());
        Assert.assertEquals(expected, html.toBigHtmlString(true));

        Assert.assertEquals(expected, html.toBigHtmlString());

        Assert.assertEquals(expected, html.toBigHtmlString());

        Assert.assertEquals(expected, html.toBigHtmlString(true));
        Assert.assertEquals(expected, html.toBigHtmlString(false));

        Assert.assertEquals(expected, html.toBigHtmlString(true));
        Assert.assertEquals(expected, html.toBigHtmlString(false));

    }

    @Test
    public void testHtmlToOutputStreamSetPrependDocType() throws IOException {

        Html html = new Html(null) {
            {
                new Div(this, new Id("id")).give(TagContent::text, "ä, ö, and ü are Umlauts. 😀");
            }
        };

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        baos.reset();
        html.toOutputStream(baos);
        final String expected = "<html><div id=\"id\">ä, ö, and ü are Umlauts. 😀</div></html>";
        Assert.assertEquals(expected, new String(baos.toByteArray()));

        baos.reset();
        html.toOutputStream(baos, true);
        Assert.assertEquals(expected, new String(baos.toByteArray()));

        baos.reset();
        html.toOutputStream(baos, false);
        Assert.assertEquals(expected, new String(baos.toByteArray()));

        baos.reset();
        html.toOutputStream(baos, StandardCharsets.UTF_8);
        Assert.assertEquals(expected, new String(baos.toByteArray()));

        baos.reset();
        html.toOutputStream(baos, StandardCharsets.UTF_8.name());
        Assert.assertEquals(expected, new String(baos.toByteArray()));

        baos.reset();
        html.toOutputStream(baos, true, StandardCharsets.UTF_8);
        Assert.assertEquals(expected, new String(baos.toByteArray()));

        baos.reset();
        html.toOutputStream(baos, false, StandardCharsets.UTF_8);
        Assert.assertEquals(expected, new String(baos.toByteArray()));

        baos.reset();
        html.toOutputStream(baos, true, StandardCharsets.UTF_8.name());
        Assert.assertEquals(expected, new String(baos.toByteArray()));

        baos.reset();
        html.toOutputStream(baos, false, StandardCharsets.UTF_8.name());
        Assert.assertEquals(expected, new String(baos.toByteArray()));

        html.setPrependDocType(true);

        baos.reset();
        html.toOutputStream(baos);
        final String expected2 = "<!DOCTYPE html>\n<html><div id=\"id\">ä, ö, and ü are Umlauts. 😀</div></html>";
        Assert.assertEquals(expected2, new String(baos.toByteArray()));

        baos.reset();
        html.toOutputStream(baos, true);
        Assert.assertEquals(expected2, new String(baos.toByteArray()));

        baos.reset();
        html.toOutputStream(baos, false);
        Assert.assertEquals(expected2, new String(baos.toByteArray()));

        baos.reset();
        html.toOutputStream(baos, StandardCharsets.UTF_8);
        Assert.assertEquals(expected2, new String(baos.toByteArray()));

        baos.reset();
        html.toOutputStream(baos, StandardCharsets.UTF_8.name());
        Assert.assertEquals(expected2, new String(baos.toByteArray()));

        baos.reset();
        html.toOutputStream(baos, true, StandardCharsets.UTF_8);
        Assert.assertEquals(expected2, new String(baos.toByteArray()));

        baos.reset();
        html.toOutputStream(baos, false, StandardCharsets.UTF_8);
        Assert.assertEquals(expected2, new String(baos.toByteArray()));

        baos.reset();
        html.toOutputStream(baos, true, StandardCharsets.UTF_8.name());
        Assert.assertEquals(expected2, new String(baos.toByteArray()));

        baos.reset();
        html.toOutputStream(baos, false, StandardCharsets.UTF_8.name());
        Assert.assertEquals(expected2, new String(baos.toByteArray()));

        html.setPrependDocType(false);

        baos.reset();
        html.toOutputStream(baos);
        Assert.assertEquals(expected, new String(baos.toByteArray()));

        baos.reset();
        html.toOutputStream(baos, true);
        Assert.assertEquals(expected, new String(baos.toByteArray()));

        baos.reset();
        html.toOutputStream(baos, false);
        Assert.assertEquals(expected, new String(baos.toByteArray()));

        baos.reset();
        html.toOutputStream(baos, StandardCharsets.UTF_8);
        Assert.assertEquals(expected, new String(baos.toByteArray()));

        baos.reset();
        html.toOutputStream(baos, StandardCharsets.UTF_8.name());
        Assert.assertEquals(expected, new String(baos.toByteArray()));

        baos.reset();
        html.toOutputStream(baos, true, StandardCharsets.UTF_8);
        Assert.assertEquals(expected, new String(baos.toByteArray()));

        baos.reset();
        html.toOutputStream(baos, false, StandardCharsets.UTF_8);
        Assert.assertEquals(expected, new String(baos.toByteArray()));

        baos.reset();
        html.toOutputStream(baos, true, StandardCharsets.UTF_8.name());
        Assert.assertEquals(expected, new String(baos.toByteArray()));

        baos.reset();
        html.toOutputStream(baos, false, StandardCharsets.UTF_8.name());
        Assert.assertEquals(expected, new String(baos.toByteArray()));

    }

}
