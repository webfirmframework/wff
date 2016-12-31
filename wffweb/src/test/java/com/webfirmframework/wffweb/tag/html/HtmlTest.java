/*
 * Copyright 2014-2017 Web Firm Framework
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.Assert;
import org.junit.Test;

import com.webfirmframework.wffweb.tag.html.attribute.global.Id;
import com.webfirmframework.wffweb.tag.html.stylesandsemantics.Div;

@SuppressWarnings("serial")
public class HtmlTest {

    @Test
    public void testHtmlToStringSetPrependDocType() throws IOException {

        Html html = new Html(null) {
            {
                new Div(this, new Id("id"));
            }
        };

        Assert.assertEquals("<html><div id=\"id\"></div></html>",
                html.toHtmlString());

        Assert.assertEquals("<html><div id=\"id\"></div></html>",
                html.toHtmlString(true));
        Assert.assertEquals("<html><div id=\"id\"></div></html>",
                html.toHtmlString(false));

        Assert.assertEquals("<html><div id=\"id\"></div></html>",
                html.toHtmlString(StandardCharsets.UTF_8));

        Assert.assertEquals("<html><div id=\"id\"></div></html>",
                html.toHtmlString(StandardCharsets.UTF_8.name()));

        Assert.assertEquals("<html><div id=\"id\"></div></html>",
                html.toHtmlString(true, StandardCharsets.UTF_8));
        Assert.assertEquals("<html><div id=\"id\"></div></html>",
                html.toHtmlString(false, StandardCharsets.UTF_8));

        Assert.assertEquals("<html><div id=\"id\"></div></html>",
                html.toHtmlString(true, StandardCharsets.UTF_8.name()));
        Assert.assertEquals("<html><div id=\"id\"></div></html>",
                html.toHtmlString(false, StandardCharsets.UTF_8.name()));

        html.setPrependDocType(true);

        Assert.assertEquals(
                "<!DOCTYPE html>\n<html><div id=\"id\"></div></html>",
                html.toHtmlString(false));

        Assert.assertEquals(
                "<!DOCTYPE html>\n<html><div id=\"id\"></div></html>",
                html.toHtmlString());

        Assert.assertEquals(
                "<!DOCTYPE html>\n<html><div id=\"id\"></div></html>",
                html.toHtmlString(true));

        Assert.assertEquals(
                "<!DOCTYPE html>\n<html><div id=\"id\"></div></html>",
                html.toHtmlString(StandardCharsets.UTF_8));

        Assert.assertEquals(
                "<!DOCTYPE html>\n<html><div id=\"id\"></div></html>",
                html.toHtmlString(StandardCharsets.UTF_8.name()));

        Assert.assertEquals(
                "<!DOCTYPE html>\n<html><div id=\"id\"></div></html>",
                html.toHtmlString(true, StandardCharsets.UTF_8));
        Assert.assertEquals(
                "<!DOCTYPE html>\n<html><div id=\"id\"></div></html>",
                html.toHtmlString(false, StandardCharsets.UTF_8));

        Assert.assertEquals(
                "<!DOCTYPE html>\n<html><div id=\"id\"></div></html>",
                html.toHtmlString(true, StandardCharsets.UTF_8.name()));
        Assert.assertEquals(
                "<!DOCTYPE html>\n<html><div id=\"id\"></div></html>",
                html.toHtmlString(false, StandardCharsets.UTF_8.name()));

        html.setPrependDocType(false);

        Assert.assertEquals("<html><div id=\"id\"></div></html>",
                html.toHtmlString(false));
        Assert.assertEquals("<html><div id=\"id\"></div></html>",
                html.toHtmlString());
        Assert.assertEquals("<html><div id=\"id\"></div></html>",
                html.toHtmlString(true));

        Assert.assertEquals("<html><div id=\"id\"></div></html>",
                html.toHtmlString(StandardCharsets.UTF_8));

        Assert.assertEquals("<html><div id=\"id\"></div></html>",
                html.toHtmlString(StandardCharsets.UTF_8.name()));

        Assert.assertEquals("<html><div id=\"id\"></div></html>",
                html.toHtmlString(true, StandardCharsets.UTF_8));
        Assert.assertEquals("<html><div id=\"id\"></div></html>",
                html.toHtmlString(false, StandardCharsets.UTF_8));

        Assert.assertEquals("<html><div id=\"id\"></div></html>",
                html.toHtmlString(true, StandardCharsets.UTF_8.name()));
        Assert.assertEquals("<html><div id=\"id\"></div></html>",
                html.toHtmlString(false, StandardCharsets.UTF_8.name()));

    }

    @Test
    public void testHtmlToOutputStreamSetPrependDocType() throws IOException {

        Html html = new Html(null) {
            {
                new Div(this, new Id("id"));
            }
        };

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        baos.reset();
        html.toOutputStream(baos);
        Assert.assertEquals("<html><div id=\"id\"></div></html>",
                new String(baos.toByteArray()));

        baos.reset();
        html.toOutputStream(baos, true);
        Assert.assertEquals("<html><div id=\"id\"></div></html>",
                new String(baos.toByteArray()));

        baos.reset();
        html.toOutputStream(baos, false);
        Assert.assertEquals("<html><div id=\"id\"></div></html>",
                new String(baos.toByteArray()));

        baos.reset();
        html.toOutputStream(baos, StandardCharsets.UTF_8);
        Assert.assertEquals("<html><div id=\"id\"></div></html>",
                new String(baos.toByteArray()));

        baos.reset();
        html.toOutputStream(baos, StandardCharsets.UTF_8.name());
        Assert.assertEquals("<html><div id=\"id\"></div></html>",
                new String(baos.toByteArray()));

        baos.reset();
        html.toOutputStream(baos, true, StandardCharsets.UTF_8);
        Assert.assertEquals("<html><div id=\"id\"></div></html>",
                new String(baos.toByteArray()));

        baos.reset();
        html.toOutputStream(baos, false, StandardCharsets.UTF_8);
        Assert.assertEquals("<html><div id=\"id\"></div></html>",
                new String(baos.toByteArray()));

        baos.reset();
        html.toOutputStream(baos, true, StandardCharsets.UTF_8.name());
        Assert.assertEquals("<html><div id=\"id\"></div></html>",
                new String(baos.toByteArray()));

        baos.reset();
        html.toOutputStream(baos, false, StandardCharsets.UTF_8.name());
        Assert.assertEquals("<html><div id=\"id\"></div></html>",
                new String(baos.toByteArray()));

        html.setPrependDocType(true);

        baos.reset();
        html.toOutputStream(baos);
        Assert.assertEquals(
                "<!DOCTYPE html>\n<html><div id=\"id\"></div></html>",
                new String(baos.toByteArray()));

        baos.reset();
        html.toOutputStream(baos, true);
        Assert.assertEquals(
                "<!DOCTYPE html>\n<html><div id=\"id\"></div></html>",
                new String(baos.toByteArray()));

        baos.reset();
        html.toOutputStream(baos, false);
        Assert.assertEquals(
                "<!DOCTYPE html>\n<html><div id=\"id\"></div></html>",
                new String(baos.toByteArray()));

        baos.reset();
        html.toOutputStream(baos, StandardCharsets.UTF_8);
        Assert.assertEquals(
                "<!DOCTYPE html>\n<html><div id=\"id\"></div></html>",
                new String(baos.toByteArray()));

        baos.reset();
        html.toOutputStream(baos, StandardCharsets.UTF_8.name());
        Assert.assertEquals(
                "<!DOCTYPE html>\n<html><div id=\"id\"></div></html>",
                new String(baos.toByteArray()));

        baos.reset();
        html.toOutputStream(baos, true, StandardCharsets.UTF_8);
        Assert.assertEquals(
                "<!DOCTYPE html>\n<html><div id=\"id\"></div></html>",
                new String(baos.toByteArray()));

        baos.reset();
        html.toOutputStream(baos, false, StandardCharsets.UTF_8);
        Assert.assertEquals(
                "<!DOCTYPE html>\n<html><div id=\"id\"></div></html>",
                new String(baos.toByteArray()));

        baos.reset();
        html.toOutputStream(baos, true, StandardCharsets.UTF_8.name());
        Assert.assertEquals(
                "<!DOCTYPE html>\n<html><div id=\"id\"></div></html>",
                new String(baos.toByteArray()));

        baos.reset();
        html.toOutputStream(baos, false, StandardCharsets.UTF_8.name());
        Assert.assertEquals(
                "<!DOCTYPE html>\n<html><div id=\"id\"></div></html>",
                new String(baos.toByteArray()));

        html.setPrependDocType(false);

        baos.reset();
        html.toOutputStream(baos);
        Assert.assertEquals("<html><div id=\"id\"></div></html>",
                new String(baos.toByteArray()));

        baos.reset();
        html.toOutputStream(baos, true);
        Assert.assertEquals("<html><div id=\"id\"></div></html>",
                new String(baos.toByteArray()));

        baos.reset();
        html.toOutputStream(baos, false);
        Assert.assertEquals("<html><div id=\"id\"></div></html>",
                new String(baos.toByteArray()));

        baos.reset();
        html.toOutputStream(baos, StandardCharsets.UTF_8);
        Assert.assertEquals("<html><div id=\"id\"></div></html>",
                new String(baos.toByteArray()));

        baos.reset();
        html.toOutputStream(baos, StandardCharsets.UTF_8.name());
        Assert.assertEquals("<html><div id=\"id\"></div></html>",
                new String(baos.toByteArray()));

        baos.reset();
        html.toOutputStream(baos, true, StandardCharsets.UTF_8);
        Assert.assertEquals("<html><div id=\"id\"></div></html>",
                new String(baos.toByteArray()));

        baos.reset();
        html.toOutputStream(baos, false, StandardCharsets.UTF_8);
        Assert.assertEquals("<html><div id=\"id\"></div></html>",
                new String(baos.toByteArray()));

        baos.reset();
        html.toOutputStream(baos, true, StandardCharsets.UTF_8.name());
        Assert.assertEquals("<html><div id=\"id\"></div></html>",
                new String(baos.toByteArray()));

        baos.reset();
        html.toOutputStream(baos, false, StandardCharsets.UTF_8.name());
        Assert.assertEquals("<html><div id=\"id\"></div></html>",
                new String(baos.toByteArray()));

    }

}
