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
 * @author WFF
 */
package com.webfirmframework.wffweb.util;

import java.nio.charset.StandardCharsets;

import org.junit.Assert;
import org.junit.Test;

import com.webfirmframework.wffweb.tag.html.AbstractHtml;
import com.webfirmframework.wffweb.tag.html.Body;
import com.webfirmframework.wffweb.tag.html.Html;
import com.webfirmframework.wffweb.tag.html.attribute.Src;
import com.webfirmframework.wffweb.tag.html.attribute.Value;
import com.webfirmframework.wffweb.tag.html.attribute.global.ClassAttribute;
import com.webfirmframework.wffweb.tag.html.attribute.global.Id;
import com.webfirmframework.wffweb.tag.html.formatting.Em;
import com.webfirmframework.wffweb.tag.html.html5.images.FigCaption;
import com.webfirmframework.wffweb.tag.html.links.Link;
import com.webfirmframework.wffweb.tag.html.metainfo.Head;
import com.webfirmframework.wffweb.tag.html.stylesandsemantics.Div;
import com.webfirmframework.wffweb.tag.html.stylesandsemantics.Span;
import com.webfirmframework.wffweb.tag.htmlwff.CustomTag;
import com.webfirmframework.wffweb.tag.htmlwff.NoTag;
import com.webfirmframework.wffweb.tag.htmlwff.TagContent;
import com.webfirmframework.wffweb.tag.repository.TagRepository;

public class TagCompressedWffBMBytesParserTest {

    @Test
    public void testParseWithExactTagFalse() {
        final Html rootTag = new Html(null).give(html -> {
            new Head(html).give(head -> {
                new Link(head, new Id("appbasicCssLink"), new Src("https://localhost/appbasic.css"));
            });
            new Body(html).give(body -> {
                new Div(body, new Id("parentDivId"), new Value(String.valueOf(Integer.MAX_VALUE)));
                new Span(body).give(TagContent::html, "<p>notes</p>");
                new Em(body).give(TagContent::text, "some text");
            });
        });
        rootTag.toHtmlString();
        final Link appbasicCssLink = new Link(null, new Id("appbasicCssLink"),
                new Src("https://localhost/appbasic.css"));
        final Div divWithoutAttributes = new Div(null);
        final Div div = new Div(null, new Id("someId"), new Value("1401"));

        AbstractHtml parsedTag = null;

        parsedTag = TagCompressedWffBMBytesParser.VERSION_3.parse(div.toCompressedWffBMBytesV3(StandardCharsets.UTF_8),
                true, StandardCharsets.UTF_8);
        Assert.assertEquals(div.toHtmlString(), parsedTag.toHtmlString());

        parsedTag = TagCompressedWffBMBytesParser.VERSION_3.parse(
                divWithoutAttributes.toCompressedWffBMBytesV3(StandardCharsets.UTF_8), false, StandardCharsets.UTF_8);
        Assert.assertEquals(divWithoutAttributes.toHtmlString(), parsedTag.toHtmlString());

        parsedTag = TagCompressedWffBMBytesParser.VERSION_3
                .parse(appbasicCssLink.toCompressedWffBMBytesV3(StandardCharsets.UTF_8), false, StandardCharsets.UTF_8);
        Assert.assertEquals("<link src=\"https://localhost/appbasic.css\" id=\"appbasicCssLink\"></link>",
                parsedTag.toHtmlString());

        parsedTag = TagCompressedWffBMBytesParser.VERSION_3
                .parse(rootTag.toCompressedWffBMBytesV3(StandardCharsets.UTF_8), false, StandardCharsets.UTF_8);
        Assert.assertEquals(
                "<html><head><link src=\"https://localhost/appbasic.css\" id=\"appbasicCssLink\"></link></head><body><div id=\"parentDivId\" value=\"2147483647\"></div><span><p>notes</p></span><em>some text</em></body></html>",
                parsedTag.toHtmlString());

        final Span span = TagRepository.findOneTagAssignableToTag(Span.class, parsedTag);
        // exactTag is false so all are instance of CustomTag.
        Assert.assertNull(span);

        final Em em = TagRepository.findOneTagAssignableToTag(Em.class, parsedTag);
        // exactTag is false so all are instance of CustomTag.
        Assert.assertNull(em);
    }

    @Test
    public void testParseWithExactTagTrue() {
        final Html rootTag = new Html(null).give(html -> {
            new Head(html).give(head -> {
                new Link(head, new Id("appbasicCssLink"), new Src("https://localhost/appbasic.css"));
            });
            new Body(html).give(body -> {
                new Div(body, new Id("parentDivId"), new Value(String.valueOf(Integer.MAX_VALUE)));
                new Span(body).give(TagContent::html, "<p>notes</p>");
                new Em(body).give(TagContent::text, "some text");
                new CustomTag("a", AbstractHtml.TagType.OPENING_CLOSING, body).give(TagContent::text,
                        "some sample text");
            });
        });
        rootTag.toHtmlString();
        final Link appbasicCssLink = new Link(null, new Id("appbasicCssLink"),
                new Src("https://localhost/appbasic.css"));
        final Div divWithoutAttributes = new Div(null);
        final Div div = new Div(null, new Id("someId"), new Value("1401"));
        final FigCaption figCaption = new FigCaption(null, new ClassAttribute("cls1"), new Value("1401"))
                .give(TagContent::text, "140119");

        AbstractHtml parsedTag = TagCompressedWffBMBytesParser.VERSION_3
                .parse(div.toCompressedWffBMBytesV3(StandardCharsets.UTF_8), true, StandardCharsets.UTF_8);
        Assert.assertEquals(div.toHtmlString(), parsedTag.toHtmlString());

        final byte[] figCaptionCompressedWffBMBytesV3 = figCaption.toCompressedWffBMBytesV3(StandardCharsets.UTF_8);
        parsedTag = TagCompressedWffBMBytesParser.VERSION_3.parse(figCaptionCompressedWffBMBytesV3, true,
                StandardCharsets.UTF_8);
        Assert.assertEquals(figCaption.toHtmlString(), parsedTag.toHtmlString());
        Assert.assertEquals(57, figCaption.toHtmlString().length());
        Assert.assertEquals(28, figCaptionCompressedWffBMBytesV3.length);
        Assert.assertTrue(figCaptionCompressedWffBMBytesV3.length < figCaption.toHtmlString().length());

        parsedTag = TagCompressedWffBMBytesParser.VERSION_3.parse(
                divWithoutAttributes.toCompressedWffBMBytesV3(StandardCharsets.UTF_8), true, StandardCharsets.UTF_8);
        Assert.assertEquals(divWithoutAttributes.toHtmlString(), parsedTag.toHtmlString());

        parsedTag = TagCompressedWffBMBytesParser.VERSION_3
                .parse(appbasicCssLink.toCompressedWffBMBytesV3(StandardCharsets.UTF_8), true, StandardCharsets.UTF_8);
        Assert.assertEquals(appbasicCssLink.toHtmlString(), parsedTag.toHtmlString());

        parsedTag = TagCompressedWffBMBytesParser.VERSION_3
                .parse(rootTag.toCompressedWffBMBytesV3(StandardCharsets.UTF_8), true, StandardCharsets.UTF_8);
        Assert.assertEquals(rootTag.toHtmlString(), parsedTag.toHtmlString());

        final Span span = TagRepository.findOneTagAssignableToTag(Span.class, parsedTag);
        Assert.assertNotNull(span.getFirstChild());
        Assert.assertTrue(span.getFirstChild() instanceof NoTag);
        if (span.getFirstChild() instanceof final NoTag noTag) {
            Assert.assertTrue(noTag.isChildContentTypeHtml());
        }

        final Em em = TagRepository.findOneTagAssignableToTag(Em.class, parsedTag);
        Assert.assertNotNull(em.getFirstChild());
        Assert.assertTrue(em.getFirstChild() instanceof NoTag);
        if (em.getFirstChild() instanceof final NoTag noTag) {
            Assert.assertFalse(noTag.isChildContentTypeHtml());
        }
    }
}
