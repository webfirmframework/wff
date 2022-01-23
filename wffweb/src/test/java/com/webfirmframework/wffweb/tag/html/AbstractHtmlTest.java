/*
 * Copyright 2014-2021 Web Firm Framework
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
import java.lang.ref.Reference;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.webfirmframework.wffweb.InvalidTagException;
import com.webfirmframework.wffweb.NoParentException;
import com.webfirmframework.wffweb.css.Color;
import com.webfirmframework.wffweb.server.page.BrowserPage;
import com.webfirmframework.wffweb.server.page.BrowserPageTest;
import com.webfirmframework.wffweb.tag.html.attribute.AttributeNameConstants;
import com.webfirmframework.wffweb.tag.html.attribute.MaxLength;
import com.webfirmframework.wffweb.tag.html.attribute.Name;
import com.webfirmframework.wffweb.tag.html.attribute.Src;
import com.webfirmframework.wffweb.tag.html.attribute.Type;
import com.webfirmframework.wffweb.tag.html.attribute.Value;
import com.webfirmframework.wffweb.tag.html.attribute.global.ClassAttribute;
import com.webfirmframework.wffweb.tag.html.attribute.global.Id;
import com.webfirmframework.wffweb.tag.html.attribute.global.Style;
import com.webfirmframework.wffweb.tag.html.attributewff.CustomAttribute;
import com.webfirmframework.wffweb.tag.html.core.TagRegistry;
import com.webfirmframework.wffweb.tag.html.formatting.B;
import com.webfirmframework.wffweb.tag.html.formsandinputs.Input;
import com.webfirmframework.wffweb.tag.html.html5.attribute.Controls;
import com.webfirmframework.wffweb.tag.html.html5.attribute.global.Translate;
import com.webfirmframework.wffweb.tag.html.links.A;
import com.webfirmframework.wffweb.tag.html.links.Link;
import com.webfirmframework.wffweb.tag.html.metainfo.Head;
import com.webfirmframework.wffweb.tag.html.stylesandsemantics.Div;
import com.webfirmframework.wffweb.tag.html.stylesandsemantics.Span;
import com.webfirmframework.wffweb.tag.htmlwff.CustomTag;
import com.webfirmframework.wffweb.tag.htmlwff.NoTag;
import com.webfirmframework.wffweb.tag.htmlwff.TagContent;
import com.webfirmframework.wffweb.tag.repository.TagRepository;
import com.webfirmframework.wffweb.util.WffBinaryMessageUtil;

@SuppressWarnings("serial")
public class AbstractHtmlTest {

    // for future development
    // @Test
    @SuppressWarnings("deprecation")
    public void testToOutputStreamBooleanOutputStream() {

        Html html = new Html(null, new Id("id1")) {
            {
                for (int i = 0; i < 100; i++) {
                    new Div(this, new Id("id2 " + i));
                }
            }
        };

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            html.toOutputStream(true, baos);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        byte[] wffMessageBytes = baos.toByteArray();
        System.out.println("wffMessageBytes.length " + wffMessageBytes.length);
        System.out.println("html.toHtmlString().length() " + html.toHtmlString().length());
        // List<NameValue> parse =
        // WffBinaryMessageUtil.VERSION_1.parse(wffMessageBytes);

        System.out.println(html.toHtmlString());

        // fail("Not yet implemented");
    }

    @Test
    public void testAddRemoveAttributesAttributes() {

        final Name name = new Name("somename");

        Html html = new Html(null) {
            {
                Div div1 = new Div(this);
                div1.addAttributes(name);
                Div div2 = new Div(this);
                div2.addAttributes(name);

                Assert.assertTrue(Arrays.asList(name.getOwnerTags()).contains(div1));
                Assert.assertTrue(Arrays.asList(name.getOwnerTags()).contains(div2));
            }
        };

        html.addAttributes(name);
        Assert.assertTrue(Arrays.asList(name.getOwnerTags()).contains(html));
        assertEquals("<html name=\"somename\"><div name=\"somename\"></div><div name=\"somename\"></div></html>",
                html.toHtmlString());

        html.removeAttributes(name);
        Assert.assertFalse(Arrays.asList(name.getOwnerTags()).contains(html));
        assertEquals("<html><div name=\"somename\"></div><div name=\"somename\"></div></html>", html.toHtmlString());

        html.addAttributes(name);
        Assert.assertTrue(Arrays.asList(name.getOwnerTags()).contains(html));
        assertEquals("<html name=\"somename\"><div name=\"somename\"></div><div name=\"somename\"></div></html>",
                html.toHtmlString());

        name.setValue("another");
        assertEquals("<html name=\"another\"><div name=\"another\"></div><div name=\"another\"></div></html>",
                html.toHtmlString());

        name.setValue("somename");
        html.removeAttributes("name");
        Assert.assertFalse(Arrays.asList(name.getOwnerTags()).contains(html));
        assertEquals("<html><div name=\"somename\"></div><div name=\"somename\"></div></html>", html.toHtmlString());

        html.addAttributes(name);
        Assert.assertTrue(Arrays.asList(name.getOwnerTags()).contains(html));
        assertEquals("<html name=\"somename\"><div name=\"somename\"></div><div name=\"somename\"></div></html>",
                html.toHtmlString());

        html.removeAttributes(new Name("somename"));
        Assert.assertTrue(Arrays.asList(name.getOwnerTags()).contains(html));
        assertEquals("<html name=\"somename\"><div name=\"somename\"></div><div name=\"somename\"></div></html>",
                html.toHtmlString());

        Name name2 = new Name("another");
        html.addAttributes(name2);
        Assert.assertFalse(Arrays.asList(name.getOwnerTags()).contains(html));
        Assert.assertTrue(Arrays.asList(name2.getOwnerTags()).contains(html));
        assertEquals("<html name=\"another\"><div name=\"somename\"></div><div name=\"somename\"></div></html>",
                html.toHtmlString());

    }

    @Test
    public void testHierarchy() throws Exception {
        Div div = new Div(null, new Id("one")) {
            {
                // for (int i = 0; i < 100000; i++) {
                // new Div(this);
                // }
                new NoTag(this, "some cont") {
                    {
                        new H2(this) {
                            {
                                new NoTag(this, "h1 contetn");
                            }
                        };
                    }
                };
                new NoTag(this, "before span");
                new Span(this, new Id("two")) {
                    {
                        new NoTag(this, "span child content");
                    }
                };
                new NoTag(this, "after span");
                new P(this, new Id("three"));
            }
        };
        String htmlString = div.toHtmlString();
        assertEquals(
                "<div id=\"one\"><h2>h1 contetn</h2>some contbefore span<span id=\"two\">span child content</span>after span<p id=\"three\"></p></div>",
                htmlString);
    }

    @Test
    public void testPerformanceAsInnerClassFormat() {

        try {
            long before = System.currentTimeMillis();

            Div div = new Div(null) {
                {
                    for (int i = 0; i < 100000; i++) {
                        new Div(this);
                    }

                }
            };

            String htmlString = div.toHtmlString();

            long after = System.currentTimeMillis();

            System.out.println("testPerformanceAsInnerClassFormat " + htmlString.length()
                    + " tag bytes generation took " + (after - before) + " ms");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPerformanceAsNonInnerClassFormat() {

        try {
            long before = System.currentTimeMillis();

            Div div = new Div(null);

            for (int i = 0; i < 100000; i++) {
                new Div(div);
            }

            String htmlString = div.toHtmlString();

            long after = System.currentTimeMillis();

            System.out.println("testPerformanceAsNonInnerClassFormat " + htmlString.length()
                    + " tag bytes generation took " + (after - before) + " ms");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPerformanceByArrayDeque() {

        try {
            long before = System.currentTimeMillis();

            Div div = new Div(null) {
                {
                    for (int i = 0; i < 100000; i++) {
                        new Div(this);
                    }

                }
            };

            byte[] wffBytes = div.toWffBMBytes();

            // AbstractHtml.getTagFromWffBMBytes(wffBytes);

            long after = System.currentTimeMillis();

            String htmlString = div.toHtmlString();

            System.out.println("htmlString.length() : " + htmlString.length() + " wffBytes.length " + wffBytes.length);
            System.out.println("htmlString.length() - wffBytes.length : " + (htmlString.length() - wffBytes.length));

            System.out.println("testPerformanceByArrayDeque " + wffBytes.length + " tag bytes generation took "
                    + (after - before) + " ms");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testToWffBMBytesAndGetTagFromWffBMBytes() {

        Div div = new Div(null, new CustomAttribute("data-wff-id", "S1")) {
            {
                new Span(this, new CustomAttribute("data-wff-id", "S2")) {
                    {
                        new H1(this, new CustomAttribute("data-wff-id", "S3"));
                        new H2(this, new CustomAttribute("data-wff-id", "S4"));
                        new NoTag(this, "sample text") {
                            {
                                new H4(this, new CustomAttribute("data-wff-id", "S6"));
                            }
                        };
                    }
                };

                new H3(this, new CustomAttribute("data-wff-id", "S5"));
            }
        };

        try {

            byte[] wffBMBytes = div.toWffBMBytes("UTF-8");

            AbstractHtml abstractHtml = AbstractHtml.getTagFromWffBMBytes(wffBMBytes);

            assertEquals(div.toHtmlString(), abstractHtml.toHtmlString());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @SuppressWarnings("unused")
    @Test(expected = InvalidTagException.class)
    public void testToWffBMBytesNoTagWithParentInvalidTagException() {
        Div superDiv = new Div(null);
        byte[] wffBMBytes = new NoTag(superDiv, "").toWffBMBytes("UTF-8");
    }

    @SuppressWarnings("unused")
    @Test(expected = InvalidTagException.class)
    public void testToWffBMBytesNoTagWithoutParentInvalidTagException() {
        byte[] wffBMBytes = new NoTag(null, "").toWffBMBytes("UTF-8");
    }

    @Test
    public void testToOutputStreamReturnedLength() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        Div div = new Div(null, new Style("background:green")) {
            {
                for (int i = 0; i < 100000; i++) {
                    new Div(this);
                }

            }
        };

        int totalWritten = div.toOutputStream(baos);
        byte[] divBytes = baos.toByteArray();
        assertEquals(totalWritten, divBytes.length);
    }

    @Test(expected = NoParentException.class)
    public void testInsertBeforeWithNoParentException() {
        Div div = new Div(null);
        div.insertBefore(new Div(null, new Id("innerDivId")));

    }

    @Test
    public void testInsertBefore() {
        Div parentDiv = new Div(null, new Id("parentDivId"));
        Div childDiv = new Div(parentDiv, new Id("child1"));
        childDiv.insertBefore(new Div(null, new Id("inserted1BeforeChild1")),
                new Div(null, new Id("inserted2BeforeChild1")));
        assertEquals(
                "<div id=\"parentDivId\"><div id=\"inserted1BeforeChild1\"></div><div id=\"inserted2BeforeChild1\"></div><div id=\"child1\"></div></div>",
                parentDiv.toHtmlString());
        System.out.println(parentDiv.toHtmlString());

    }

    @Test(expected = NoParentException.class)
    public void testReplaceWithWithNoParentException() {
        Div div = new Div(null);
        div.replaceWith(new Div(null, new Id("innerDivId")));

    }

    @Test
    public void testReplaceWithUnderBrowserPage() {
        BrowserPage browserPage = new BrowserPage() {

            @Override
            public String webSocketUrl() {
                return "ws://localhost/indexws";
            }

            @Override
            public AbstractHtml render() {

                Html rootTag = new Html(null).give(html -> {
                    new Head(html).give(head -> {
                        new Link(head, new Id("appbasicCssLink"), new Src("https://localhost/appbasic.css"));
                    });
                    new Body(html).give(body -> {

                        new Div(body, new Id("parentDivId"));

                    });
                });
                return rootTag;
            }
        };

        browserPage.toHtmlString();

        final AbstractHtml appbasicCssLink = browserPage.getTagRepository().findTagById("appbasicCssLink");
        assertNotNull(appbasicCssLink);
        assertEquals("link", appbasicCssLink.getTagName());
        assertTrue(TagUtil.isTagged(appbasicCssLink));

        final Link appadditionalCssLink = new Link(null, new Id("appadditionalCssLink"),
                new Src("https://localhost/appadditional.css"));

        appbasicCssLink.replaceWith(appadditionalCssLink);

        final AbstractHtml appadditionalCssLinkFound = browserPage.getTagRepository()
                .findTagById("appadditionalCssLink");
        assertNotNull(appadditionalCssLinkFound);
        assertEquals(appadditionalCssLink, appadditionalCssLinkFound);
    }

    @Test
    public void testReplaceWith() {
        {
            Div parentDiv = new Div(null, new Id("parentDivId"));
            Div childDiv = new Div(parentDiv, new Id("child1"));
            childDiv.replaceWith(new Div(null, new Id("inserted1BeforeChild1")),
                    new Div(null, new Id("inserted2BeforeChild1")));
            assertEquals(
                    "<div id=\"parentDivId\"><div id=\"inserted1BeforeChild1\"></div><div id=\"inserted2BeforeChild1\"></div></div>",
                    parentDiv.toHtmlString());

            final AbstractHtml replacementTag = TagRepository.findTagById("inserted1BeforeChild1", parentDiv);
            assertNotNull(replacementTag);
            Link lnk = new Link(parentDiv, new Id("cssLink"));
            assertNotEquals(
                    "<div id=\"parentDivId\"><div id=\"inserted1BeforeChild1\"></div><div id=\"inserted2BeforeChild1\"></div></div>",
                    parentDiv.toHtmlString());
            final AbstractHtml linkTag = TagRepository.findTagById("cssLink", parentDiv);
            assertNotNull(linkTag);
            assertEquals("link", linkTag.getTagName());
            assertTrue(TagUtil.isTagged(lnk));

        }
        {
            Div parentDiv = new Div(null, new Id("parentDivId"));
            Div childDiv1 = new Div(parentDiv, new Id("child1"));
            new Div(parentDiv, new Id("child2"));

            assertEquals("<div id=\"parentDivId\"><div id=\"child1\"></div><div id=\"child2\"></div></div>",
                    parentDiv.toHtmlString());

            childDiv1.replaceWith(new Div(null, new Id("inserted1BeforeChild1")));
            assertEquals(
                    "<div id=\"parentDivId\"><div id=\"inserted1BeforeChild1\"></div><div id=\"child2\"></div></div>",
                    parentDiv.toHtmlString());

            final AbstractHtml replacementTag = TagRepository.findTagById("inserted1BeforeChild1", parentDiv);
            assertNotNull(replacementTag);
        }

    }

    @SuppressWarnings("unused")
    @Test
    public void testReplaceWith2() {

        {
            Div parentDiv = new Div(null, new Id("parentDivId"));
            Div childDiv1 = new Div(parentDiv, new Id("child1"));
            Div childDiv2 = new Div(parentDiv, new Id("child2"));
            Div childDiv3 = new Div(parentDiv, new Id("child3"));

            assertEquals(
                    "<div id=\"parentDivId\"><div id=\"child1\"></div><div id=\"child2\"></div><div id=\"child3\"></div></div>",
                    parentDiv.toHtmlString());

            childDiv3.replaceWith(childDiv2);

            assertEquals("<div id=\"parentDivId\"><div id=\"child1\"></div><div id=\"child2\"></div></div>",
                    parentDiv.toHtmlString());
        }

    }

    @Test
    public void testGive() {
        Div rootDiv = new Div(null, new Id("rootDivId")).<Div>give(parent -> {
            new Div(parent, new Id("parentDivId")).give(nestedTag1 -> {
                new Div(nestedTag1, new Id("child1"));
                new Div(nestedTag1, new Id("child2"));
                new Div(nestedTag1, new Id("child3"));
            });
        });
        assertEquals(
                "<div id=\"rootDivId\"><div id=\"parentDivId\"><div id=\"child1\"></div><div id=\"child2\"></div><div id=\"child3\"></div></div></div>",
                rootDiv.toHtmlString());

    }

    @Test
    public void testGiveBiFun() {

        AbstractHtml div = null;

        div = new Div(null, new Id("rootDivId")).give(TagContent::text, "content1");
        assertEquals("<div id=\"rootDivId\">content1</div>", div.toHtmlString());
        for (AbstractHtml each : div.getChildren()) {
            NoTag noTag = (NoTag) each;
            assertFalse(noTag.isChildContentTypeHtml());
        }

        div = new Div(null, new Id("rootDivId")).give(TagContent::html, "content1");
        assertEquals("<div id=\"rootDivId\">content1</div>", div.toHtmlString());
        for (AbstractHtml each : div.getChildren()) {
            NoTag noTag = (NoTag) each;
            assertTrue(noTag.isChildContentTypeHtml());
        }

        Div div1 = new Div(null, new Id("rootDivId")).give(TagContent::text, "content1");
        for (AbstractHtml each : div1.getChildren()) {
            NoTag noTag = (NoTag) each;
            assertFalse(noTag.isChildContentTypeHtml());
        }

        Div div2 = new Div(null, new Id("rootDivId")).give(TagContent::html, "content1");
        for (AbstractHtml each : div2.getChildren()) {
            NoTag noTag = (NoTag) each;
            assertTrue(noTag.isChildContentTypeHtml());
        }

    }

    @Test(expected = NoParentException.class)
    public void testInsertAfterWithNoParentException() {
        Div div = new Div(null);
        div.insertAfter(new Div(null, new Id("innerDivId")));

    }

    @Test
    public void testInsertAfter() {
        Div parentDiv = new Div(null, new Id("parentDivId"));
        Div childDiv = new Div(parentDiv, new Id("child1"));
        childDiv.insertAfter(new Div(null, new Id("inserted1BeforeChild1")),
                new Div(null, new Id("inserted2BeforeChild1")));
        assertEquals(
                "<div id=\"parentDivId\"><div id=\"child1\"></div><div id=\"inserted1BeforeChild1\"></div><div id=\"inserted2BeforeChild1\"></div></div>",
                parentDiv.toHtmlString());

    }

    @Test
    @SuppressWarnings("unused")
    public void testInsertAfter2() {
        Div parentDiv = new Div(null, new Id("parentDivId"));
        Div childDiv = new Div(parentDiv, new Id("child1"));
        Div childDiv2 = new Div(parentDiv, new Id("child2"));
        childDiv.insertAfter(new Div(null, new Id("inserted1BeforeChild1")),
                new Div(null, new Id("inserted2BeforeChild1")));
        assertEquals(
                "<div id=\"parentDivId\"><div id=\"child1\"></div><div id=\"inserted1BeforeChild1\"></div><div id=\"inserted2BeforeChild1\"></div><div id=\"child2\"></div></div>",
                parentDiv.toHtmlString());

    }

    @Test
    public void testGetRootTag() {

        Div div1 = new Div(null);
        Div div2 = new Div(div1);
        Div div3 = new Div(div2);
        Div div4 = new Div(div3);
        Div div5 = new Div(null);
        Div div6 = new Div(null);
        Div div7 = new Div(null);
        Div div8 = new Div(null);
        Div div9 = new Div(null);
        Div div10 = new Div(null);

        div5.appendChild(div6);

        div4.appendChild(div5);

        div7.addInnerHtmls(div8, div9, div10);

        div6.addInnerHtml(div7);

        assertEquals(div1, div4.getRootTag());

        assertEquals(div1, div3.getRootTag());

        assertEquals(div1, div2.getRootTag());

        assertEquals(div1, div1.getRootTag());

        assertEquals(div1, div5.getRootTag());

        assertEquals(div1, div6.getRootTag());

        assertEquals(div1, div7.getRootTag());

        assertEquals(div1, div8.getRootTag());

        assertEquals(div1, div9.getRootTag());

        assertEquals(div1, div10.getRootTag());

        div6.removeAllChildren();

        assertEquals(div7, div7.getRootTag());

        div6.getParent().removeChild(div6);

        assertEquals(div6, div6.getRootTag());

    }

    @Test
    public void testSetGetSharedData() {

        Html html = new Html(null) {
            {
                new Head(this) {
                    {
                        new TitleTag(this) {
                            {
                                new NoTag(this, "some title");
                            }
                        };
                    }
                };
                new Body(this, new Id("one")) {
                    {
                        new Div(this);
                    }
                };
            }
        };

        Div div = TagRepository.findOneTagAssignableToTag(Div.class, html);
        Head head = TagRepository.findOneTagAssignableToTag(Head.class, html);

        assertEquals(div.getSharedObject(), head.getSharedObject());

        assertTrue(div.getSharedObject() == head.getSharedObject());

        assertTrue(div.getSharedObject().equals(head.getSharedObject()));

        Object sharedData = "some object";

        div.setSharedData(sharedData);

        assertEquals(sharedData, head.getSharedData());

        assertEquals(div.getSharedData(), head.getSharedData());

        assertTrue(sharedData == head.getSharedData());

        assertTrue(div.getSharedData() == head.getSharedData());

        assertTrue(div.getSharedData().equals(head.getSharedData()));

    }

    @Test
    public void testToString1() throws Exception {
        Html html = new Html(null) {
            {
                new Head(this) {
                    {
                        new TitleTag(this) {
                            {
                                new NoTag(this, "some title");
                            }
                        };
                    }
                };
                new Body(this, new Id("one")) {
                    {

                        new Span(this);
                        new Br(this);
                        new Br(this);
                        new Span(this);

                        new Div(this);
                        new Div(this) {
                            {
                                new Div(this) {
                                    {
                                        new Div(this);
                                    }
                                };
                            }
                        };
                        new Div(this);
                    }
                };
            }
        };

        TitleTag titleTag = TagRepository.findOneTagAssignableToTag(TitleTag.class, html);
        assertEquals("<title>some title</title>", titleTag.toHtmlString());
        titleTag.addInnerHtml(new NoTag(null, "Title changed"));

        assertEquals("<title>Title changed</title>", titleTag.toHtmlString());
        titleTag.appendChild(new Div(null));
        assertEquals("<title>Title changed<div></div></title>", titleTag.toHtmlString());
        titleTag.addInnerHtml(new Div(null));
        assertEquals("<title><div></div></title>", titleTag.toHtmlString());
    }

    @Test
    public void testToString2() throws Exception {
        Div div = new Div(null, new Id("one")) {
            {
                new Span(this, new Id("five")) {
                    {
                        new Div(this, new Id("three"));
                    }
                };
            }
        };

        assertEquals("<div id=\"one\"><span id=\"five\"><div id=\"three\"></div></span></div>", div.toHtmlString());
        Span span = TagRepository.findOneTagAssignableToTag(Span.class, div);
        span.addInnerHtml(new NoTag(null, "Title changed"));
        assertEquals("<div id=\"one\"><span id=\"five\">Title changed</span></div>", div.toHtmlString());
        Id id = (Id) span.getAttributeByName(AttributeNameConstants.ID);
        id.setValue(5);
        assertEquals("<div id=\"one\"><span id=\"5\">Title changed</span></div>", div.toHtmlString());
        span.addAttributes(new ClassAttribute("cls-five"));
        assertEquals("<div id=\"one\"><span id=\"5\" class=\"cls-five\">Title changed</span></div>",
                div.toHtmlString());
        span.removeAttributes(AttributeNameConstants.CLASS);
        assertEquals("<div id=\"one\"><span id=\"5\">Title changed</span></div>", div.toHtmlString());

        {
            Span span2 = new Span(null);
            Style style = new Style("color:green");
            span2.addAttributes(style);
            span.appendChild(span2);

            assertEquals(
                    "<div id=\"one\"><span id=\"5\">Title changed<span style=\"color:green;\"></span></span></div>",
                    div.toHtmlString());

            style.addCssProperty("color", "blue");
            assertEquals("<div id=\"one\"><span id=\"5\">Title changed<span style=\"color:blue;\"></span></span></div>",
                    div.toHtmlString());
            Color color = new Color("yellow");
            style.addCssProperties(color);
            assertEquals(
                    "<div id=\"one\"><span id=\"5\">Title changed<span style=\"color:yellow;\"></span></span></div>",
                    div.toHtmlString());
            color.setCssValue("orange");
            assertEquals(
                    "<div id=\"one\"><span id=\"5\">Title changed<span style=\"color:orange;\"></span></span></div>",
                    div.toHtmlString());
        }
        {
            Span span2 = new Span(null);
            ClassAttribute cls = new ClassAttribute("cls-one");
            span2.addAttributes(cls);
            span.appendChild(span2);
            assertEquals(
                    "<div id=\"one\"><span id=\"5\">Title changed<span style=\"color:orange;\"></span><span class=\"cls-one\"></span></span></div>",
                    div.toHtmlString());
            cls.addClassNames("cls-two");
            assertEquals(
                    "<div id=\"one\"><span id=\"5\">Title changed<span style=\"color:orange;\"></span><span class=\"cls-one cls-two\"></span></span></div>",
                    div.toHtmlString());
            cls.addClassNames("abcd-cls");
            assertEquals(
                    "<div id=\"one\"><span id=\"5\">Title changed<span style=\"color:orange;\"></span><span class=\"cls-one cls-two abcd-cls\"></span></span></div>",
                    div.toHtmlString());
        }
        {
            Span span2 = new Span(null);
            Name name = new Name("webfirmframework");
            span2.addAttributes(name);
            span.appendChild(span2);
            assertEquals(
                    "<div id=\"one\"><span id=\"5\">Title changed<span style=\"color:orange;\"></span><span class=\"cls-one cls-two abcd-cls\"></span><span name=\"webfirmframework\"></span></span></div>",
                    div.toHtmlString());
            name.setValue("wffweb");
            assertEquals(
                    "<div id=\"one\"><span id=\"5\">Title changed<span style=\"color:orange;\"></span><span class=\"cls-one cls-two abcd-cls\"></span><span name=\"wffweb\"></span></span></div>",
                    div.toHtmlString());
            span2.removeAttributes(name);
            assertEquals(
                    "<div id=\"one\"><span id=\"5\">Title changed<span style=\"color:orange;\"></span><span class=\"cls-one cls-two abcd-cls\"></span><span></span></span></div>",
                    div.toHtmlString());
        }

    }

    @Test
    public void testToOutputStream1() throws Exception {
        Html html = new Html(null) {
            {
                new Head(this) {
                    {
                        new TitleTag(this) {
                            {
                                new NoTag(this, "some title");
                            }
                        };
                    }
                };
                new Body(this, new Id("one")) {
                    {

                        new Span(this);
                        new Br(this);
                        new Br(this);
                        new Span(this);

                        new Div(this);
                        new Div(this) {
                            {
                                new Div(this) {
                                    {
                                        new Div(this);
                                    }
                                };
                            }
                        };
                        new Div(this);
                    }
                };
            }
        };

        TitleTag titleTag = TagRepository.findOneTagAssignableToTag(TitleTag.class, html);
        {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            titleTag.toOutputStream(outputStream);
            assertEquals("<title>some title</title>", outputStream.toString());
        }
        {
            titleTag.addInnerHtml(new NoTag(null, "Title changed"));
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            titleTag.toOutputStream(outputStream);
            assertEquals("<title>Title changed</title>", outputStream.toString());
        }
        {
            titleTag.appendChild(new Div(null));
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            titleTag.toOutputStream(outputStream);
            assertEquals("<title>Title changed<div></div></title>", outputStream.toString());
        }

        {
            titleTag.addInnerHtml(new Div(null));
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            titleTag.toOutputStream(outputStream);
            assertEquals("<title><div></div></title>", outputStream.toString());
        }
    }

    @Test
    public void testToOutputStream2() throws Exception {
        Div div = new Div(null, new Id("one")) {
            {
                new Span(this, new Id("five")) {
                    {
                        new Div(this, new Id("three"));
                    }
                };
            }
        };
        {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            div.toOutputStream(outputStream);
            assertEquals("<div id=\"one\"><span id=\"five\"><div id=\"three\"></div></span></div>",
                    outputStream.toString());
        }
        Span span = TagRepository.findOneTagAssignableToTag(Span.class, div);
        {
            span.addInnerHtml(new NoTag(null, "Title changed"));
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            div.toOutputStream(outputStream);
            assertEquals("<div id=\"one\"><span id=\"five\">Title changed</span></div>", outputStream.toString());
        }
        {
            Id id = (Id) span.getAttributeByName(AttributeNameConstants.ID);
            id.setValue(5);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            div.toOutputStream(outputStream);
            assertEquals("<div id=\"one\"><span id=\"5\">Title changed</span></div>", outputStream.toString());
        }
        {
            span.addAttributes(new ClassAttribute("cls-five"));
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            div.toOutputStream(outputStream);
            assertEquals("<div id=\"one\"><span id=\"5\" class=\"cls-five\">Title changed</span></div>",
                    outputStream.toString());
        }
        {
            span.removeAttributes(AttributeNameConstants.CLASS);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            div.toOutputStream(outputStream);
            assertEquals("<div id=\"one\"><span id=\"5\">Title changed</span></div>", outputStream.toString());
        }

    }

    @Test
    public void testToBigHtmlString() throws Exception {

        CustomTag tag = new CustomTag("tag1", null) {
            {
                new CustomTag("tag2", this) {
                    {
                        new CustomTag("tag3", this) {
                            {

                            }
                        };
                    }
                };
                new CustomTag("tag4", this) {
                    {
                        new CustomTag("tag5", this) {
                            {
                                new CustomTag("tag6", this) {
                                    {

                                        new CustomTag("tag8", this);

                                        new CustomTag("tag7", this) {
                                            {
                                                new CustomTag("tag9", this) {
                                                    {

                                                    }
                                                };
                                                new CustomTag("tag10", this) {
                                                    {

                                                    }
                                                };
                                            }
                                        };

                                    }
                                };
                            }
                        };
                    }
                };

                new CustomTag("middle", this);
                new CustomTag("middle2", this).addInnerHtml(new NoTag(null, "This is inner content"));
                new CustomTag("middle3", this) {
                    {
                        new NoTag(this, "line1");
                        new NoTag(this, "line2");
                        new NoTag(this, "line3");
                        new NoTag(this, "line4");
                        new NoTag(this, "line5");
                    }
                };

                new CustomTag("tag44", this) {
                    {
                        new CustomTag("tag55", this) {
                            {
                                new CustomTag("tag66", this) {
                                    {

                                        new CustomTag("tag88", this);

                                        new CustomTag("tag77", this) {
                                            {
                                                new CustomTag("tag99", this) {
                                                    {

                                                    }
                                                };
                                                new CustomTag("tag100", this) {
                                                    {

                                                    }
                                                };
                                            }
                                        };

                                    }
                                };
                            }
                        };
                    }
                };
            }
        };

        assertEquals(
                "<tag1><tag2><tag3></tag3></tag2><tag4><tag5><tag6><tag8></tag8><tag7><tag9></tag9><tag10></tag10></tag7></tag6></tag5></tag4><middle></middle><middle2>This is inner content</middle2><middle3>line1line2line3line4line5</middle3><tag44><tag55><tag66><tag88></tag88><tag77><tag99></tag99><tag100></tag100></tag77></tag66></tag55></tag44></tag1>",
                tag.toBigHtmlString(true));

        CustomTag customTag = (CustomTag) TagRepository.findOneTagByTagName("tag4", tag);

        assertEquals("<tag4><tag5><tag6><tag8></tag8><tag7><tag9></tag9><tag10></tag10></tag7></tag6></tag5></tag4>",
                customTag.toBigHtmlString(true));

        CustomTag middle3 = (CustomTag) TagRepository.findOneTagByTagName("middle3", tag);
        assertEquals("<middle3>line1line2line3line4line5</middle3>", middle3.toBigHtmlString(true));

    }

    @Test
    public void testToBigOutputStream() throws Exception {

        CustomTag tag = new CustomTag("tag1", null) {
            {
                new CustomTag("tag2", this) {
                    {
                        new CustomTag("tag3", this) {
                            {

                            }
                        };
                    }
                };
                new CustomTag("tag4", this) {
                    {
                        new CustomTag("tag5", this) {
                            {
                                new CustomTag("tag6", this) {
                                    {

                                        new CustomTag("tag8", this);

                                        new CustomTag("tag7", this) {
                                            {
                                                new CustomTag("tag9", this) {
                                                    {

                                                    }
                                                };
                                                new CustomTag("tag10", this) {
                                                    {

                                                    }
                                                };
                                            }
                                        };

                                    }
                                };
                            }
                        };
                    }
                };

                new CustomTag("middle", this);
                new CustomTag("middle2", this).addInnerHtml(new NoTag(null, "This is inner content"));
                new CustomTag("middle3", this) {
                    {
                        new NoTag(this, "line1");
                        new NoTag(this, "line2");
                        new NoTag(this, "line3");
                        new NoTag(this, "line4");
                        new NoTag(this, "line5");
                    }
                };

                new CustomTag("tag44", this) {
                    {
                        new CustomTag("tag55", this) {
                            {
                                new CustomTag("tag66", this) {
                                    {

                                        new CustomTag("tag88", this);

                                        new CustomTag("tag77", this) {
                                            {
                                                new CustomTag("tag99", this) {
                                                    {

                                                    }
                                                };
                                                new CustomTag("tag100", this) {
                                                    {

                                                    }
                                                };
                                            }
                                        };

                                    }
                                };
                            }
                        };
                    }
                };
            }
        };

        {

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            tag.toBigOutputStream(os, true);
            assertEquals(
                    "<tag1><tag2><tag3></tag3></tag2><tag4><tag5><tag6><tag8></tag8><tag7><tag9></tag9><tag10></tag10></tag7></tag6></tag5></tag4><middle></middle><middle2>This is inner content</middle2><middle3>line1line2line3line4line5</middle3><tag44><tag55><tag66><tag88></tag88><tag77><tag99></tag99><tag100></tag100></tag77></tag66></tag55></tag44></tag1>",
                    os.toString());
        }

        CustomTag customTag = (CustomTag) TagRepository.findOneTagByTagName("tag4", tag);
        {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            customTag.toBigOutputStream(os, true);
            assertEquals(
                    "<tag4><tag5><tag6><tag8></tag8><tag7><tag9></tag9><tag10></tag10></tag7></tag6></tag5></tag4>",
                    os.toString());
        }

        CustomTag middle3 = (CustomTag) TagRepository.findOneTagByTagName("middle3", tag);
        {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            middle3.toBigOutputStream(os, true);
            assertEquals("<middle3>line1line2line3line4line5</middle3>", os.toString());
        }

    }

    @SuppressWarnings("unused")
    @Test
    public void testToBigHtmlStringNoStackoverflowError() throws Exception {
        Html html = new Html(null) {
            {
                new Head(this) {
                    {
                        new TitleTag(this) {
                            {
                                new NoTag(this, "some title");
                            }
                        };
                    }
                };
                new Body(this, new Id("one")) {
                    {
                        new NoTag(this, "something here");
                        CustomTag previous = new CustomTag("ctag", this);
                        // increase 100 to higher value to get
                        // StackoverflowError for toHtmlString but not
                        // toBigHtmlString
                        for (int i = 0; i < 100; i++) {
                            previous = new CustomTag("ctag" + i, previous);
                            new Span(this);
                        }
                        new Span(previous).addAttributes(new Name("wffweb"));

                    }
                };
            }
        };
        {
            long before = System.currentTimeMillis();
            String htmlString = html.toHtmlString(true);
            // System.out.println(htmlString);
            long after = System.currentTimeMillis();
            System.out.println("total time taken toHtmlString: " + (after - before));
        }
        {
            long before = System.currentTimeMillis();
            String bigHtmlString = html.toBigHtmlString(true);
            // System.out.println(bigHtmlString);
            long after = System.currentTimeMillis();
            System.out.println("total time taken for toBigHtmlString: " + (after - before));
        }

    }

    @Test
    public void testToBigOutputStreamNoStackoverflowError() throws Exception {
        Html html = new Html(null) {
            {
                new Head(this) {
                    {
                        new TitleTag(this) {
                            {
                                new NoTag(this, "some title");
                            }
                        };
                    }
                };
                new Body(this, new Id("one")) {
                    {
                        new NoTag(this, "something here");
                        CustomTag previous = new CustomTag("ctag", this);
                        // increase 100 to higher value to get
                        // StackoverflowError for toHtmlString but not
                        // toBigHtmlString
                        for (int i = 0; i < 100; i++) {
                            previous = new CustomTag("ctag" + i, previous);
                            new Span(this);
                        }
                        new Span(previous).addAttributes(new Name("wffweb"));

                    }
                };
            }
        };

        {
            long before = System.currentTimeMillis();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            html.toOutputStream(os, true);
            // System.out.println(os.toString());
            long after = System.currentTimeMillis();
            System.out.println("total time taken toOutputStream: " + (after - before));
        }

        {
            long before = System.currentTimeMillis();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            html.toBigOutputStream(os, true);
            // System.out.println(os.toString());
            long after = System.currentTimeMillis();
            System.out.println("total time taken for toBigOutputStream: " + (after - before));
        }

    }

    @Test(expected = InvalidTagException.class)
    public void testResetHierarchyInvalidTagException() {
        final Html html = new Html(null) {
            {
                new Div(this, new Id("one")) {
                    {
                        new Span(this, new Id("two")) {
                            {
                                new H1(this, new Id("three"));
                                new H2(this, new Id("three"));
                                new NoTag(this, "something");
                            }
                        };

                        new H3(this, new Name("name1"));
                    }
                };
            }
        };
        BrowserPage browserPage = new BrowserPage() {

            @Override
            public String webSocketUrl() {
                return "wss://webfirmframework/websocket";
            }

            @Override
            public AbstractHtml render() {
                return html;
            }
        };
        browserPage.toHtmlString();

        Div div = browserPage.getTagRepository().findOneTagAssignableToTag(Div.class);

        div.resetHierarchy();
    }

    @Test
    public void testResetHierarchy() {
        try {
            final Html html = new Html(null) {
                {
                    new Div(this, new Id("one")) {
                        {
                            new Span(this, new Id("two")) {
                                {
                                    new H1(this, new Id("three"));
                                    new H2(this, new Id("three"));
                                    new NoTag(this, "something");
                                }
                            };

                            new H3(this, new Name("name1"));
                        }
                    };
                }
            };
            BrowserPage browserPage = new BrowserPage() {

                @Override
                public String webSocketUrl() {
                    return "wss://webfirmframework/websocket";
                }

                @Override
                public AbstractHtml render() {
                    return html;
                }
            };
            browserPage.toHtmlString();

            Div div = browserPage.getTagRepository().findOneTagAssignableToTag(Div.class);
            div.getParent().removeChild(div);
            div.resetHierarchy();

            Assert.assertEquals(
                    "<div id=\"one\"><span id=\"two\"><h1 id=\"three\"></h1><h2 id=\"three\"></h2>something</span><h3 name=\"name1\"></h3></div>",
                    div.toHtmlString());
        } catch (Exception e) {
            Assert.fail(e.toString());
        }
    }

    @Test
    public void testDiv() throws Exception {
        @SuppressWarnings("unused")
        final Div div = new Div(null, new Id("one"));
    }

    @Test(expected = ClassCastException.class)
    public void testGetFirstChildClassCastException() {
        final Div div = new Div(null, new Id("one"));
        final H1 insertedFirstTag = new H1(null);
        div.appendChild(insertedFirstTag);

        // this should throw ClassCastException
        @SuppressWarnings("unused")
        final NoTag firstChildH1 = (NoTag) div.getFirstChild();
    }

    @Test
    public void testGetFirstChild() {
        {

            // with multiple children

            final Div div = new Div(null, new Id("one"));

            final H1 insertedFirstTag = new H1(null);

            div.appendChild(insertedFirstTag);
            div.appendChild(new H2(null));
            div.appendChild(new H3(null));
            div.appendChild(new H4(null));
            div.appendChild(new H5(null));
            div.appendChild(new H6(null));
            div.appendChild(new A(null));
            div.appendChild(new B(null));

            {
                final AbstractHtml firstChildH1 = div.getFirstChild();
                Assert.assertNotNull(firstChildH1);
            }

            final H1 firstChildH1 = (H1) div.getFirstChild();
            Assert.assertEquals(insertedFirstTag, firstChildH1);
        }
        {
            // with empty children
            final Div div = new Div(null, new Id("two"));
            Assert.assertNull(div.getFirstChild());
        }

        {
            // with only one child
            final Div div = new Div(null, new Id("one"));
            final H1 insertedFirstTag = new H1(null);
            div.appendChild(insertedFirstTag);
            Assert.assertEquals(insertedFirstTag, div.getFirstChild());
        }
        {
            final Div div = new Div(null, new Id("one"));
            final H1 insertedFirstTag = new H1(null);

            div.appendChild(insertedFirstTag);

            new Thread(new Runnable() {

                @Override
                public void run() {
                    for (int i = 0; i < 150; i++) {
                        try {
                            insertedFirstTag.insertBefore(new A(null));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                }
            });
        }

    }

    @Test
    public void testThreadSafetyOfGetFirstChild1() {

        // uncomment synchronized (children) in getFirstChild to reproduce the
        // bug

        final Div div = new Div(null, new Id("one"));
        final H1 insertedFirstTag = new H1(null);

        div.appendChild(insertedFirstTag);

        Thread threadOne = new Thread(new Runnable() {

            @Override
            public void run() {
//                System.out.println("threadOne started");
                for (int i = 0; i < 150; i++) {
                    try {
                        insertedFirstTag.insertBefore(new CustomTag("tag" + i, null));
//                        System.out.println("inserted a tag before");
                    } catch (Exception e) {
                        e.printStackTrace();
                        Assert.fail("threadOne this method is not thread safe");
                    }
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Assert.fail("threadOne this method may not be thread safe");
                    }
                }

            }
        });

        Thread threadTwo = new Thread(new Runnable() {

            @Override
            public void run() {
//                System.out.println("threadTwo started");
                for (int i = 0; i < 150; i++) {
                    try {
                        final AbstractHtml firstChild = div.getFirstChild();
                        Assert.assertNotNull(firstChild);
//                        System.out.println(
//                                "firstChild: " + firstChild.toHtmlString());
                    } catch (Exception e) {
                        e.printStackTrace();
                        Assert.fail("threadTwo this method is not thread safe");
                    }
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Assert.fail("threadTwo this method may not be thread safe");
                    }
                }

            }
        });

        threadTwo.start();
        threadOne.start();

        while (threadOne.isAlive() || threadTwo.isAlive())
            ;

    }

    @Test
    public void testThreadSafetyOfGetFirstChild2() {

        // uncomment synchronized (children) in getFirstChild to reproduce the
        // bug

        final Div div = new Div(null, new Id("one"));
        final H1 insertedFirstTag = new H1(null);

        div.appendChild(insertedFirstTag);

        Thread threadOne = new Thread(new Runnable() {

            @Override
            public void run() {
                AbstractHtml currentFirstTag = insertedFirstTag;
//                System.out.println("threadOne started");
                for (int i = 0; i < 150; i++) {
                    try {
                        final CustomTag customTag = new CustomTag("tag" + i, null);
                        currentFirstTag.insertBefore(customTag);
                        currentFirstTag = customTag;
//                        System.out.println("inserted a tag before");
                    } catch (Exception e) {
                        e.printStackTrace();
                        Assert.fail("threadOne this method is not thread safe");
                    }
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Assert.fail("threadOne this method may not be thread safe");
                    }
                }

            }
        });

        Thread threadTwo = new Thread(new Runnable() {

            @Override
            public void run() {
//                System.out.println("threadTwo started");
                for (int i = 0; i < 150; i++) {
                    try {
                        final AbstractHtml firstChild = div.getFirstChild();
                        // got null for firstChild when the getFirstChild
                        // internal implementation is not synchronized
                        Assert.assertNotNull(firstChild);
//                        System.out.println(
//                                "firstChild: " + firstChild.toHtmlString());
                    } catch (Exception e) {
                        e.printStackTrace();
                        Assert.fail("threadTwo this method is not thread safe");
                    }
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Assert.fail("threadTwo this method may not be thread safe");
                    }
                }

            }
        });

        threadTwo.start();
        threadOne.start();

        while (threadOne.isAlive() || threadTwo.isAlive())
            ;

    }

    @Test
    public void testThreadSafetyOfGetLastChild1() {

        // uncomment synchronized (children) in getFirstChild to reproduce the
        // bug

        final Div div = new Div(null, new Id("one"));
        final H1 insertedLastTag = new H1(null);

        div.appendChild(insertedLastTag);

        Thread threadOne = new Thread(new Runnable() {

            @Override
            public void run() {
//                System.out.println("threadOne started");
                for (int i = 0; i < 150; i++) {
                    try {
                        insertedLastTag.insertAfter(new CustomTag("tag" + i, null));
//                        System.out.println("inserted a tag before");
                    } catch (Exception e) {
                        e.printStackTrace();
                        Assert.fail("threadOne this method is not thread safe");
                    }
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Assert.fail("threadOne this method may not be thread safe");
                    }
                }

            }
        });

        Thread threadTwo = new Thread(new Runnable() {

            @Override
            public void run() {
//                System.out.println("threadTwo started");
                for (int i = 0; i < 150; i++) {
                    try {
                        final AbstractHtml lastChild = div.getLastChild();
                        Assert.assertNotNull(lastChild);
//                        System.out.println(
//                                "lastChild: " + lastChild.toHtmlString());
                    } catch (Exception e) {
                        e.printStackTrace();
                        Assert.fail("threadTwo this method is not thread safe");
                    }
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Assert.fail("threadTwo this method may not be thread safe");
                    }
                }

            }
        });

        threadTwo.start();
        threadOne.start();

        while (threadOne.isAlive() || threadTwo.isAlive())
            ;

    }

    @Test
    public void testThreadSafetyOfGetLastChild2() {

        // uncomment synchronized (children) in getFirstChild to reproduce the
        // bug

        final Div div = new Div(null, new Id("one"));
        final H1 insertedLastTag = new H1(null);

        div.appendChild(insertedLastTag);

        Thread threadOne = new Thread(new Runnable() {

            @Override
            public void run() {
                AbstractHtml currentLastTag = insertedLastTag;
//                System.out.println("threadOne started");
                for (int i = 0; i < 150; i++) {
                    try {
                        final CustomTag customTag = new CustomTag("tag" + i, null);
                        currentLastTag.insertBefore(customTag);
                        currentLastTag = customTag;
//                        System.out.println("inserted a tag before");
                    } catch (Exception e) {
                        e.printStackTrace();
                        Assert.fail("threadOne this method is not thread safe");
                    }
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Assert.fail("threadOne this method may not be thread safe");
                    }
                }

            }
        });

        Thread threadTwo = new Thread(new Runnable() {

            @Override
            public void run() {
//                System.out.println("threadTwo started");
                for (int i = 0; i < 150; i++) {
                    try {
                        final AbstractHtml lastChild = div.getLastChild();
                        // got null for lastChild when the getLastChild
                        // internal implementation is not synchronized
                        Assert.assertNotNull(lastChild);
//                        System.out.println(
//                                "lastChild: " + lastChild.toHtmlString());
                    } catch (Exception e) {
                        e.printStackTrace();
                        Assert.fail("threadTwo this method is not thread safe");
                    }
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Assert.fail("threadTwo this method may not be thread safe");
                    }
                }

            }
        });

        threadTwo.start();
        threadOne.start();

        while (threadOne.isAlive() || threadTwo.isAlive())
            ;

    }

    public static void main(String[] args) {
        new AbstractHtmlTest().testThreadSafetyOfGetFirstChild1();
        new AbstractHtmlTest().testThreadSafetyOfGetFirstChild2();
        new AbstractHtmlTest().testThreadSafetyOfGetLastChild1();
        new AbstractHtmlTest().testThreadSafetyOfGetLastChild2();
    }

    @Test
    public void testGetLastChild() {
        {

            // with multiple children

            final Div div = new Div(null, new Id("one"));

            final H1 insertedLastTag = new H1(null);

            div.appendChild(new H2(null));
            div.appendChild(new H3(null));
            div.appendChild(new H4(null));
            div.appendChild(new H5(null));
            div.appendChild(new H6(null));
            div.appendChild(new A(null));
            div.appendChild(new B(null));
            div.appendChild(insertedLastTag);

            {
                final AbstractHtml lastChildH1 = div.getLastChild();
                Assert.assertNotNull(lastChildH1);
            }

            final H1 lastChildH1 = (H1) div.getLastChild();
            Assert.assertEquals(insertedLastTag, lastChildH1);
        }
        {
            // with empty children
            final Div div = new Div(null, new Id("two"));
            Assert.assertNull(div.getLastChild());
        }

        {
            // with only one child
            final Div div = new Div(null, new Id("one"));
            final H1 insertedLastTag = new H1(null);
            div.appendChild(insertedLastTag);
            Assert.assertEquals(insertedLastTag, div.getLastChild());
        }

    }

    @Test(expected = ClassCastException.class)
    public void testGetLastChildClassCastException() {
        final Div div = new Div(null, new Id("one"));
        final H1 insertedFirstTag = new H1(null);
        div.appendChild(insertedFirstTag);

        // this should throw ClassCastException
        @SuppressWarnings("unused")
        final NoTag firstChildH1 = (NoTag) div.getLastChild();
    }

    @Test
    public void testChildAtAndGetChildrenSize() {
        // with multiple children

        final Div div = new Div(null, new Id("one"));

        final H1 insertedFirstTag = new H1(null);

        div.appendChild(insertedFirstTag);

        final H2 secondChild = new H2(null);
        div.appendChild(secondChild);
        div.appendChild(new H3(null));
        div.appendChild(new H4(null));
        div.appendChild(new H5(null));
        div.appendChild(new H6(null));
        div.appendChild(new A(null));
        final B lastChild = new B(null);
        div.appendChild(lastChild);

        {
            final AbstractHtml childAt = div.getChildAt(0);
            Assert.assertNotNull(childAt);
        }
        {
            final H1 childAt = (H1) div.getChildAt(0);
            Assert.assertNotNull(childAt);
        }
        {
            final AbstractHtml childAt = div.getChildAt(0);
            Assert.assertNotNull(childAt);
        }

        Assert.assertEquals(insertedFirstTag, div.getChildAt(0));

        Assert.assertEquals(secondChild, div.getChildAt(1));

        Assert.assertEquals(lastChild, div.getChildAt(div.getChildrenSize() - 1));
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testChildAtWithExp() {

        final Div div = new Div(null, new Id("one"));

        final H1 insertedFirstTag = new H1(null);

        div.appendChild(insertedFirstTag);
        final H2 secondChild = new H2(null);
        div.appendChild(secondChild);

        Assert.assertEquals(insertedFirstTag, div.getChildAt(0));
        Assert.assertEquals(secondChild, div.getChildAt(1));
        div.getChildAt(2);
    }

    @Test(expected = ClassCastException.class)
    public void testChildAtTypeCastedWithClassCastException() {

        final Div div = new Div(null, new Id("one"));

        final H1 insertedFirstTag = new H1(null);

        div.appendChild(insertedFirstTag);

        // this should throw ClassCastException
        @SuppressWarnings("unused")
        final Span childAt = (Span) div.getChildAt(0);

    }

    @Test
    public void test1PrependChildren() {

        final Div div = new Div(null, new Id("one")) {
            {
                new Div(this, new Id("child1"));
            }
        };

        Span span = new Span(null);

        P p = new P(null);

        Br br = new Br(null);

        div.prependChildren(span, p, br);

        assertEquals("<div id=\"one\"><span></span><p></p><br/><div id=\"child1\"></div></div>", div.toHtmlString());

    }

    @Test
    public void test2PrependChildren() {

        final Div div = new Div(null, new Id("one"));

        Span span = new Span(null);

        P p = new P(null);

        Br br = new Br(null);

        div.prependChildren(span, p, br);

        assertEquals("<div id=\"one\"><span></span><p></p><br/></div>", div.toHtmlString());
    }

    @Test
    public void testToWffBMBytes() throws Exception {
        final Html html = new Html(null) {
            {
                new Div(this, new Id("one")) {
                    {
                        new Span(this, new Id("two")) {
                            {
                                new H1(this, new Id("three"));
                                new H2(this, new Id("three"));
                                new NoTag(this, "something");
                            }
                        };

                        new H3(this, new Name("name1"));
                    }
                };
            }
        };

        final String htmlString = html.toHtmlString();
        final byte[] wffBMBytes = html.toWffBMBytes();

        assertTrue(wffBMBytes.length < htmlString.length());
        if (wffBMBytes.length < htmlString.length()) {
            System.out.println("wffBMBytes.length is less than htmlString.length()");
        }
        System.out.println("wffBMBytes.length: " + wffBMBytes.length + "\nhtmlString.length(): " + htmlString.length());

        final AbstractHtml tagFromWffBMBytes = AbstractHtml.getTagFromWffBMBytes(wffBMBytes);

        assertEquals(htmlString, tagFromWffBMBytes.toHtmlString());
    }

    @Test
    public void testToWffBMBytesCharset() throws Exception {
        final Html html = new Html(null) {
            {
                new Div(this, new Id("one")) {
                    {
                        new Span(this, new Id("two"), new Style("color:green"), new ClassAttribute("cls1 cls2"),
                                new Controls(), new CustomAttribute("custom-attr1", "value"),
                                new CustomAttribute("custom-attr2", ""), new CustomAttribute("custom-attr3", null)) {
                            {
                                new H1(this, new Id("three"), new Translate(), new MaxLength(), new Controls("true"));
                                new H2(this, new Id("three"), new Translate(false), new Controls("controls"));
                                new H3(this, new Id("three"), new Translate("yes"));
                                new NoTag(this, "something");
                            }
                        };

                        new H3(this, new Name("name1"));
                    }
                };
            }
        };

        final byte[] wffBMBytes = html.toWffBMBytes(StandardCharsets.UTF_8);
        final AbstractHtml tagFromWffBMBytes = AbstractHtml.getTagFromWffBMBytes(wffBMBytes, StandardCharsets.UTF_8);

        assertEquals(html.toHtmlString(), tagFromWffBMBytes.toHtmlString());
    }

    @Test
    public void testToWffBMBytesCharsetTypeString() throws Exception {
        final Html html = new Html(null) {
            {
                new Div(this, new Id("one")) {
                    {
                        new Span(this, new Id("two")) {
                            {
                                new H1(this, new Id("three"));
                                new H2(this, new Id("three"));
                                new NoTag(this, "something");
                            }
                        };

                        new H3(this, new Name("name1"));
                    }
                };
            }
        };

        final byte[] wffBMBytes = html.toWffBMBytes("UTF-8");
        final AbstractHtml tagFromWffBMBytes = AbstractHtml.getTagFromWffBMBytes(wffBMBytes, "UTF-8");

        assertEquals(html.toHtmlString(), tagFromWffBMBytes.toHtmlString());

        final Collection<AbstractHtml> allTags = TagRepository.findAllTags(tagFromWffBMBytes);
        for (AbstractHtml abstractHtml : allTags) {
            abstractHtml.removeAttributes("id");
        }
        assertNotEquals(html.toHtmlString(), tagFromWffBMBytes.toHtmlString());
        assertEquals("<html><div><span><h1></h1><h2></h2>something</span><h3 name=\"name1\"></h3></div></html>",
                tagFromWffBMBytes.toHtmlString());
    }

    @Test
    public void testToWffBMBytesCharsetSince302() {
        {
            final Html html = new Html(null) {
                {
                    new Div(this, new Id("one")) {
                        {
                            new Span(this, new Id("two")) {
                                {
                                    new H1(this, new Id("three"));
                                    new H2(this, new Id("three"));
                                    new NoTag(this, "something");
                                    new NoTag(this, "<h1>heading</h1>", true);
                                }
                            };

                            new H3(this, new Name("name1"));
                        }
                    };
                }
            };

            final byte[] wffBMBytes = html.toWffBMBytes(StandardCharsets.UTF_8);
            final AbstractHtml tagFromWffBMBytes = AbstractHtml.getTagFromWffBMBytes(wffBMBytes,
                    StandardCharsets.UTF_8);

            assertEquals(html.toHtmlString(), tagFromWffBMBytes.toHtmlString());

            final NoTag noTagWithContentTypeHtmlFalse = (NoTag) tagFromWffBMBytes.getFirstChild().getFirstChild()
                    .getChildAt(2);
            final NoTag noTagWithContentTypeHtmlTrue = (NoTag) tagFromWffBMBytes.getFirstChild().getFirstChild()
                    .getChildAt(3);
            org.junit.Assert.assertFalse(noTagWithContentTypeHtmlFalse.isChildContentTypeHtml());
            org.junit.Assert.assertTrue(noTagWithContentTypeHtmlTrue.isChildContentTypeHtml());

        }
        {
            Div div = new Div(null) {
                {
                    new NoTag(this, "<h1>heading</h1>", true);
                }
            };
            final NoTag firstChild = (NoTag) div.getFirstChild();
            org.junit.Assert.assertTrue(firstChild.noTagContentTypeHtml);
            org.junit.Assert.assertTrue(firstChild.isChildContentTypeHtml());
            final byte[] wffBMBytes = div.toWffBMBytes(StandardCharsets.UTF_8);
            final AbstractHtml tagFromWffBMBytes = AbstractHtml.getTagFromWffBMBytes(wffBMBytes,
                    StandardCharsets.UTF_8);

            assertEquals(div.toHtmlString(), tagFromWffBMBytes.toHtmlString());
        }
        {
            Div div = new Div(null) {
                {
                    new P(this) {
                        {
                            new NoTag(this, "<h1>heading</h1>", false);
                            new NoTag(this, "<h1>heading</h1>", true);
                            new NoTag(this, "<h1>heading</h1>");
                        }
                    };
                }
            };
            {
                final P firstChild = (P) div.getFirstChild();

                final NoTag noTag0 = (NoTag) firstChild.getChildAt(0);
                final NoTag noTag1 = (NoTag) firstChild.getChildAt(1);
                final NoTag noTag2 = (NoTag) firstChild.getChildAt(2);
                org.junit.Assert.assertFalse(noTag0.noTagContentTypeHtml);
                org.junit.Assert.assertTrue(noTag1.noTagContentTypeHtml);
                org.junit.Assert.assertFalse(noTag2.noTagContentTypeHtml);

                org.junit.Assert.assertFalse(noTag0.isChildContentTypeHtml());
                org.junit.Assert.assertTrue(noTag1.isChildContentTypeHtml());
                org.junit.Assert.assertFalse(noTag2.isChildContentTypeHtml());
            }

            final byte[] wffBMBytes = div.toWffBMBytes(StandardCharsets.UTF_8);
            final AbstractHtml tagFromWffBMBytes = AbstractHtml.getTagFromWffBMBytes(wffBMBytes,
                    StandardCharsets.UTF_8);

            assertEquals(div.toHtmlString(), tagFromWffBMBytes.toHtmlString());

            {
                // cannot cast to P as it creates a custom tag internally
                // TODO the method getTagFromWffBMBytes must be improved later
                final AbstractHtml firstChild = tagFromWffBMBytes.getFirstChild();

                assertEquals(firstChild.getTagName(), TagNameConstants.P);

                final NoTag noTag0 = (NoTag) firstChild.getChildAt(0);
                final NoTag noTag1 = (NoTag) firstChild.getChildAt(1);
                final NoTag noTag2 = (NoTag) firstChild.getChildAt(2);
                org.junit.Assert.assertFalse(noTag0.noTagContentTypeHtml);
                org.junit.Assert.assertTrue(noTag1.noTagContentTypeHtml);
                org.junit.Assert.assertFalse(noTag2.noTagContentTypeHtml);

                org.junit.Assert.assertFalse(noTag0.isChildContentTypeHtml());
                org.junit.Assert.assertTrue(noTag1.isChildContentTypeHtml());
                org.junit.Assert.assertFalse(noTag2.isChildContentTypeHtml());
            }
        }
    }

    @Test
    public void testGetTagFromWffBMBytesCharset() throws Exception {
        final Html html = new Html(null) {
            {
                new Div(this, new Id("one")) {
                    {
                        new Span(this, new Id("two"), new Style("color:green"), new ClassAttribute("cls1 cls2"),
                                new Controls(), new ClassAttribute("cls1 cls2"), new Controls(),
                                new CustomAttribute("custom-attr1", "value"), new CustomAttribute("custom-attr2", ""),
                                new CustomAttribute("custom-attr3", null)) {
                            {
                                new H1(this, new Id("three"), new Translate(), new MaxLength(), new Controls("true"));
                                new H2(this, new Id("three"), new Translate(false), new Controls("controls"));
                                new H3(this, new Id("three"), new Translate("yes"));
                                new NoTag(this, "something");
                            }
                        };

                        new H3(this, new Name("name1"));
                    }
                };
            }
        };

        final byte[] wffBMBytes = html.toWffBMBytes(StandardCharsets.UTF_8);
        final AbstractHtml tagFromWffBMBytes = AbstractHtml.getExactTagFromWffBMBytes(wffBMBytes,
                StandardCharsets.UTF_8);

        assertEquals(html.toHtmlString(), tagFromWffBMBytes.toHtmlString());
    }

    @Test
    public void testToCompressedWffBMBytesCharset() throws Exception {
        final Html html = new Html(null) {
            {
                new Div(this, new Id("one")) {
                    {
                        new Span(this, new Id("two"), new Style("color:green"), new ClassAttribute("cls1 cls2"),
                                new Controls(), new ClassAttribute("cls1 cls2"), new Controls(),
                                new CustomAttribute("custom-attr1", "value"), new CustomAttribute("custom-attr2", ""),
                                new CustomAttribute("custom-attr3", null)) {
                            {
                                new H1(this, new Id("three"), new Translate(), new MaxLength(), new Controls("true"));
                                new H2(this, new Id("three"), new Translate(false), new Controls("controls"));
                                new H3(this, new Id("three"), new Translate("yes"));
                                new NoTag(this, "something");
                            }
                        };

                        new H3(this, new Name("name1"));
                    }
                };
            }
        };

        final byte[] wffBMBytes = html.toWffBMBytes(StandardCharsets.UTF_8);
        final byte[] compressedWffBMBytes = html.toCompressedWffBMBytesV2(StandardCharsets.UTF_8);

        // without tagIndex impl: wffBMBytes.length: 311 compressedWffBMBytes.length:
        // 247
        // with tagIndex impl compressedWffBMBytes.length: 242
//        System.out.println("wffBMBytes.length: " + wffBMBytes.length + "\ncompressedWffBMBytes.length: " + compressedWffBMBytes.length);
        assertTrue(wffBMBytes.length > compressedWffBMBytes.length);
    }

    @Test
    public void testTagNameIndex() throws Exception {
        final Map<String, Class<?>> tagClassNameByTagName = TagRegistry.getTagClassByTagName();
        for (Entry<String, Class<?>> entry : tagClassNameByTagName.entrySet()) {
            final AbstractHtml tag = TagRegistry.getNewTagInstance(entry.getKey());

            assertNotNull(tag);
            assertEquals(entry.getValue(), tag.getClass());
            // just for testing
            int tagNameIndex = WffBinaryMessageUtil.getIntFromOptimizedBytes(tag.getTagNameIndexBytes());
            assertEquals(tagNameIndex, (int) TagRegistry.getIndexByTagName(tag.getTagName()));

        }
    }

    @Test
    public void testCompareSizeOfToWffBMBytesAndToCompressedWffBMBytes() throws Exception {
        Html html = new Html(null, new Id("id1")) {
            {
                for (int i = 0; i < 50; i++) {
                    new Div(this, new Id("id2 " + i)) {
                        {
                            new Input(this, new Type(Type.TEXT), new Name("uname"), new Value("uname"));
                        }
                    };
                }
            }
        };

        final int htmlStringLength = html.toHtmlString().length();
        long before = System.nanoTime();
        final int tagWffBMBytesLength = html.toWffBMBytes(StandardCharsets.UTF_8).length;
        long after = System.nanoTime();

        long toWffBMBytesProcessingTime = after - before;

        before = System.nanoTime();
        final int tagCompressedWffBMBytesLength = html.toCompressedWffBMBytesV2(StandardCharsets.UTF_8).length;
        after = System.nanoTime();

        long toCompressedWffBMBytesProcessingTime = after - before;

        assertTrue(tagCompressedWffBMBytesLength < tagWffBMBytesLength);
        System.out.println("htmlStringLength: " + htmlStringLength + "\ntagWffBMBytesLength: " + tagWffBMBytesLength
                + "\ntagCompressedWffBMBytesLength: " + tagCompressedWffBMBytesLength
                + "\ntagCompressedWffBMBytesLength saved " + (tagWffBMBytesLength - tagCompressedWffBMBytesLength)
                + " bytes" + "\ntoWffBMBytesProcessingTime: " + toWffBMBytesProcessingTime
                + "\ntoCompressedWffBMBytesProcessingTime: " + toCompressedWffBMBytesProcessingTime);

//        assertTrue(toCompressedWffBMBytesProcessingTime < toWffBMBytesProcessingTime);

    }

    @Test
    public void testDataWffIdLifeCycle() {
        BrowserPage browserPage = new BrowserPage() {

            @Override
            public String webSocketUrl() {
                return "ws://localhost/indexws";
            }

            @Override
            public AbstractHtml render() {

                Html rootTag = new Html(null).give(html -> {
                    new Head(html).give(head -> {
                        new Link(head, new Id("appbasicCssLink"), new Src("https://localhost/appbasic.css"));
                    });
                    new Body(html).give(body -> {

                        Div dv = new Div(body, new Id("mainDivId1"));

                        assertEquals("<div id=\"mainDivId1\"></div>", dv.toHtmlString());

                        dv = new Div(body, new Id("mainDivId2")).give(dv2 -> {
                            new Span(dv2, new Id("mainDivId2")).give(dv3 -> {
                                new NoTag(dv3, "Sample text");
                            });
                        });

                        assertEquals("<div id=\"mainDivId2\"><span id=\"mainDivId2\">Sample text</span></div>",
                                dv.toHtmlString());

                    });
                });
                return rootTag;
            }
        };

        browserPage.toHtmlString();

        final TagRepository tagRepository = browserPage.getTagRepository();

        final Body body = tagRepository.findBodyTag();

        // no need to remove DataWffId when tag is removed
        // because we are removing it if it is again adding in to another tag

        final AbstractHtml mainDivId2 = tagRepository.findTagById("mainDivId2");
        assertEquals(
                "<div data-wff-id=\"S4\" id=\"mainDivId2\"><span data-wff-id=\"S5\" id=\"mainDivId2\">Sample text</span></div>",
                mainDivId2.toHtmlString());

        final AbstractHtml mainDivId1 = tagRepository.findTagById("mainDivId1");
        assertEquals("<div data-wff-id=\"S3\" id=\"mainDivId1\"></div>", mainDivId1.toHtmlString());
        mainDivId1.getParent().removeChild(mainDivId1);
        assertEquals("<div data-wff-id=\"S3\" id=\"mainDivId1\"></div>", mainDivId1.toHtmlString());

        assertEquals(
                "<div data-wff-id=\"S4\" id=\"mainDivId2\"><span data-wff-id=\"S5\" id=\"mainDivId2\">Sample text</span></div>",
                mainDivId2.toHtmlString());

        body.prependChildren(mainDivId1);
        assertEquals("<div data-wff-id=\"S8\" id=\"mainDivId1\"></div>", mainDivId1.toHtmlString());
        mainDivId1.getParent().removeChild(mainDivId1);
        assertEquals("<div data-wff-id=\"S8\" id=\"mainDivId1\"></div>", mainDivId1.toHtmlString());

        assertEquals(
                "<div data-wff-id=\"S4\" id=\"mainDivId2\"><span data-wff-id=\"S5\" id=\"mainDivId2\">Sample text</span></div>",
                mainDivId2.toHtmlString());

        body.prependChildren(mainDivId1);
        assertEquals("<div data-wff-id=\"S9\" id=\"mainDivId1\"></div>", mainDivId1.toHtmlString());
        mainDivId1.getParent().removeChildren(mainDivId1);
        assertEquals("<div data-wff-id=\"S9\" id=\"mainDivId1\"></div>", mainDivId1.toHtmlString());

        assertEquals(
                "<div data-wff-id=\"S4\" id=\"mainDivId2\"><span data-wff-id=\"S5\" id=\"mainDivId2\">Sample text</span></div>",
                mainDivId2.toHtmlString());

        body.prependChildren(mainDivId1);
        assertEquals("<div data-wff-id=\"S10\" id=\"mainDivId1\"></div>", mainDivId1.toHtmlString());
        mainDivId1.getParent().removeChildren(mainDivId1);
        assertEquals("<div data-wff-id=\"S10\" id=\"mainDivId1\"></div>", mainDivId1.toHtmlString());

        assertEquals(
                "<div data-wff-id=\"S4\" id=\"mainDivId2\"><span data-wff-id=\"S5\" id=\"mainDivId2\">Sample text</span></div>",
                mainDivId2.toHtmlString());

        mainDivId2.getParent().removeChild(mainDivId2);
        body.appendChildren(mainDivId2);
        assertEquals(
                "<div data-wff-id=\"S11\" id=\"mainDivId2\"><span data-wff-id=\"S12\" id=\"mainDivId2\">Sample text</span></div>",
                mainDivId2.toHtmlString());
        assertEquals("<div data-wff-id=\"S10\" id=\"mainDivId1\"></div>", mainDivId1.toHtmlString());
        mainDivId2.getParent().removeChildren(mainDivId2);
        assertEquals(
                "<div data-wff-id=\"S11\" id=\"mainDivId2\"><span data-wff-id=\"S12\" id=\"mainDivId2\">Sample text</span></div>",
                mainDivId2.toHtmlString());
        assertEquals("<div data-wff-id=\"S10\" id=\"mainDivId1\"></div>", mainDivId1.toHtmlString());

        body.appendChildren(mainDivId2);
        assertNotEquals(
                "<div data-wff-id=\"S11\" id=\"mainDivId2\"><span data-wff-id=\"S12\" id=\"mainDivId2\">Sample text</span></div>",
                mainDivId2.toHtmlString());
        assertEquals(
                "<div data-wff-id=\"S13\" id=\"mainDivId2\"><span data-wff-id=\"S14\" id=\"mainDivId2\">Sample text</span></div>",
                mainDivId2.toHtmlString());

        assertEquals("<div data-wff-id=\"S10\" id=\"mainDivId1\"></div>", mainDivId1.toHtmlString());

        mainDivId2.getParent().removeChild(mainDivId2);
        Div outerdv = new Div(null, new Id("mainDivId2Outer"));
        outerdv.addInnerHtml(mainDivId2);
        assertNotEquals(
                "<div data-wff-id=\"S13\" id=\"mainDivId2\"><span data-wff-id=\"S14\" id=\"mainDivId2\">Sample text</span></div>",
                mainDivId2.toHtmlString());
        assertEquals("<div id=\"mainDivId2\"><span id=\"mainDivId2\">Sample text</span></div>",
                mainDivId2.toHtmlString());

        body.prependChildren(mainDivId1);

        mainDivId1.insertBefore(outerdv);
        assertNotEquals("<div id=\"mainDivId2\"><span id=\"mainDivId2\">Sample text</span></div>",
                mainDivId2.toHtmlString());

        assertEquals(
                "<div data-wff-id=\"S17\" id=\"mainDivId2\"><span data-wff-id=\"S18\" id=\"mainDivId2\">Sample text</span></div>",
                mainDivId2.toHtmlString());

        outerdv.getParent().removeChild(outerdv);

        outerdv = new Div(null, new Id("mainDivId2Outer"));
        final Div mainDivId2OuterFirstChild = new Div(outerdv, new Id("mainDivId2OuterFirstChild"));
        mainDivId2OuterFirstChild.insertBefore(mainDivId2);

        assertEquals(
                "<div data-wff-id=\"S17\" id=\"mainDivId2\"><span data-wff-id=\"S18\" id=\"mainDivId2\">Sample text</span></div>",
                mainDivId2.toHtmlString());

        mainDivId1.insertAfter(outerdv);
        assertNotEquals(
                "<div data-wff-id=\"S17\" id=\"mainDivId2\"><span data-wff-id=\"S18\" id=\"mainDivId2\">Sample text</span></div>",
                mainDivId2.toHtmlString());
        assertNotEquals(
                "<div data-wff-id=\"S20\" id=\"mainDivId2\"><span data-wff-id=\"S18\" id=\"mainDivId2\">Sample text</span></div>",
                mainDivId2.toHtmlString());
        assertEquals(
                "<div data-wff-id=\"S20\" id=\"mainDivId2\"><span data-wff-id=\"S22\" id=\"mainDivId2\">Sample text</span></div>",
                mainDivId2.toHtmlString());

        body.appendChild(mainDivId2);

        assertEquals(
                "<div data-wff-id=\"S20\" id=\"mainDivId2\"><span data-wff-id=\"S22\" id=\"mainDivId2\">Sample text</span></div>",
                mainDivId2.toHtmlString());

        mainDivId2.getParent().removeChild(mainDivId2);
        assertEquals(
                "<div data-wff-id=\"S20\" id=\"mainDivId2\"><span data-wff-id=\"S22\" id=\"mainDivId2\">Sample text</span></div>",
                mainDivId2.toHtmlString());
        body.appendChild(mainDivId2);
        assertNotEquals(
                "<div data-wff-id=\"S20\" id=\"mainDivId2\"><span data-wff-id=\"S22\" id=\"mainDivId2\">Sample text</span></div>",
                mainDivId2.toHtmlString());
        assertEquals(
                "<div data-wff-id=\"S23\" id=\"mainDivId2\"><span data-wff-id=\"S24\" id=\"mainDivId2\">Sample text</span></div>",
                mainDivId2.toHtmlString());

        assertNotNull(mainDivId2.getParent());
        body.addInnerHtml(mainDivId2);
        assertEquals(
                "<div data-wff-id=\"S25\" id=\"mainDivId2\"><span data-wff-id=\"S26\" id=\"mainDivId2\">Sample text</span></div>",
                mainDivId2.toHtmlString());

        body.removeAllChildren();

        Div div1 = new Div(null, new Id("div1"));
        Div div2 = new Div(null, new Id("div2"));
        body.appendChildren(div1, div2);

        div1.addInnerHtml(mainDivId2);
        assertEquals(
                "<div data-wff-id=\"S29\" id=\"mainDivId2\"><span data-wff-id=\"S30\" id=\"mainDivId2\">Sample text</span></div>",
                mainDivId2.toHtmlString());
        assertEquals(
                "<div data-wff-id=\"S29\" id=\"mainDivId2\"><span data-wff-id=\"S30\" id=\"mainDivId2\">Sample text</span></div>",
                mainDivId2.toHtmlString());

        assertNotNull(mainDivId2.getParent());

        div2.addInnerHtml(mainDivId2);

//        System.out.println(body.toHtmlString());

        mainDivId2.addInnerHtml(new NoTag(null, ""));
        mainDivId2.appendChild(new NoTag(null, ""));

        SharedTagContent<String> stc = new SharedTagContent<String>("");
        mainDivId2.subscribeTo(stc);

//        assertEquals("<div data-wff-id=\"S15\" id=\"mainDivId1\"></div>", mainDivId1.toHtmlString());

//        System.out.println(body.toHtmlString());
    }

    @Test
    public void testWhenURI1() throws Exception {

        Set<AbstractHtml> expectedTagsForURIChange = new HashSet<>();

        Html html = new Html(null);

        BrowserPage browserPage = new BrowserPage() {

            @Override
            public String webSocketUrl() {
                // TODO Auto-generated method stub
                return "wss://wffweb";
            }

            @Override
            public AbstractHtml render() {
                super.setURI("/someuri");

                return html;
            }

        };
        browserPage.toHtmlString();

        StringBuilder controlFlow = new StringBuilder();

        AbstractHtml div1 = new Div(null).whenURI((uri) -> true, () -> {

            controlFlow.append("div1.whenURI\n");

            AbstractHtml span = new Span(null).whenURI((uri) -> true, () -> {

                controlFlow.append("span.whenURI\n");

                AbstractHtml pParent = new CustomTag("p-parent", null).give(custom -> {
                    controlFlow.append("pParent.give\n");
                    AbstractHtml p = new P(custom).whenURI((uri) -> true, () -> {

                        controlFlow.append("p.whenURI\n");

                        controlFlow.append("NoTag plain text\n");

                        return new AbstractHtml[] { new NoTag(null, "plain text") };
                    }, null);
                    expectedTagsForURIChange.add(p);
                });

                return new AbstractHtml[] { pParent };
            }, null);

            expectedTagsForURIChange.add(span);

            return new AbstractHtml[] { span };
        }, null);

        expectedTagsForURIChange.add(div1);

        html.appendChild(div1);

        assertEquals("""
                div1.whenURI
                span.whenURI
                pParent.give
                p.whenURI
                NoTag plain text
                """, controlFlow.toString());
        assertEquals(
                "<div data-wff-id=\"S2\"><span data-wff-id=\"S3\"><p-parent data-wff-id=\"S4\"><p data-wff-id=\"S5\">plain text</p></p-parent></span></div>",
                div1.toBigHtmlString());

        Set<AbstractHtml> tagsForURIChange = new HashSet<>();

        for (Reference<AbstractHtml> each : BrowserPageTest.getTagsForURIChangeForTest(browserPage)) {
            tagsForURIChange.add(each.get());
        }

        List<AbstractHtml> expectedTagsForURIChangeSorted = 
                expectedTagsForURIChange.stream().sorted(Comparator.comparingInt(Object::hashCode)).collect(Collectors.toList());
        
        List<AbstractHtml> tagsForURIChangeSorted = 
                tagsForURIChange.stream().sorted(Comparator.comparingInt(Object::hashCode)).collect(Collectors.toList());
        
        assertArrayEquals(expectedTagsForURIChangeSorted.toArray(), tagsForURIChangeSorted.toArray());

    }

    @Test
    public void testWhenURI2() throws Exception {

        Set<AbstractHtml> expectedTagsForURIChange = new HashSet<>();

        Html html = new Html(null);

        BrowserPage browserPage = new BrowserPage() {

            @Override
            public String webSocketUrl() {
                // TODO Auto-generated method stub
                return "wss://wffweb";
            }

            @Override
            public AbstractHtml render() {
                super.setURI("/someuri");

                return html;
            }

        };
        browserPage.toHtmlString();

        StringBuilder controlFlow = new StringBuilder();

        AbstractHtml div1 = new Div(null).whenURI((uri) -> true, () -> {

            controlFlow.append("div1.whenURI\n");

            AbstractHtml span = new Span(null).whenURI((uri) -> true, () -> {

                controlFlow.append("span.whenURI\n");

                AbstractHtml pParent = new CustomTag("p-parent", null) {
                    {
                        controlFlow.append("pParent.give\n");
                        AbstractHtml p = new P(this).whenURI((uri) -> true, () -> {

                            controlFlow.append("p.whenURI\n");

                            controlFlow.append("NoTag plain text\n");

                            return new AbstractHtml[] { new NoTag(null, "plain text") };
                        }, null);
                        expectedTagsForURIChange.add(p);
                    }
                };

                return new AbstractHtml[] { pParent };
            }, null);

            expectedTagsForURIChange.add(span);

            return new AbstractHtml[] { span };
        }, null);

        html.appendChild(div1);
        expectedTagsForURIChange.add(div1);

        assertEquals("""
                div1.whenURI
                span.whenURI
                pParent.give
                p.whenURI
                NoTag plain text
                """, controlFlow.toString());
        assertEquals(
                "<div data-wff-id=\"S2\"><span data-wff-id=\"S3\"><p-parent data-wff-id=\"S4\"><p data-wff-id=\"S5\">plain text</p></p-parent></span></div>",
                div1.toBigHtmlString());

        Set<AbstractHtml> tagsForURIChange = new HashSet<>();

        for (Reference<AbstractHtml> each : BrowserPageTest.getTagsForURIChangeForTest(browserPage)) {
            tagsForURIChange.add(each.get());
        }

        List<AbstractHtml> expectedTagsForURIChangeSorted = 
                expectedTagsForURIChange.stream().sorted(Comparator.comparingInt(Object::hashCode)).collect(Collectors.toList());
        
        List<AbstractHtml> tagsForURIChangeSorted = 
                tagsForURIChange.stream().sorted(Comparator.comparingInt(Object::hashCode)).collect(Collectors.toList());
        
        assertArrayEquals(expectedTagsForURIChangeSorted.toArray(), tagsForURIChangeSorted.toArray());

    }

    @Test
    public void testWhenURI3() throws Exception {
        
        Set<AbstractHtml> expectedTagsForURIChange = new HashSet<>();

        Html html = new Html(null);

        BrowserPage browserPage = new BrowserPage() {

            @Override
            public String webSocketUrl() {
                // TODO Auto-generated method stub
                return "wss://wffweb";
            }

            @Override
            public AbstractHtml render() {
                super.setURI("/someuri");

                return html;
            }

        };
        browserPage.toHtmlString();

        StringBuilder controlFlow = new StringBuilder();

        AbstractHtml div1 = new Div(null).whenURI((uri) -> true, () -> {

            controlFlow.append("div1.whenURI\n");

            AbstractHtml span = new Span(null).whenURI((uri) -> true, () -> {

                controlFlow.append("span.whenURI\n");

                AbstractHtml pParent = new CustomTag("p-parent", null) {
                    {
                        controlFlow.append("pParent.give\n");
                        AbstractHtml p = new P(this).whenURI((uri) -> true, () -> {

                            controlFlow.append("p.whenURI\n");

                            controlFlow.append("NoTag plain text\n");

                            return new AbstractHtml[] { new NoTag(null, "plain text") };
                        }, null);
                        expectedTagsForURIChange.add(p);
                    }
                };

                return new AbstractHtml[] { pParent };
            }, null);
            expectedTagsForURIChange.add(span);

            return new AbstractHtml[] { span };
        }, null);
        
        expectedTagsForURIChange.add(div1);

        html.appendChild(div1);

        AbstractHtml div2 = new Div(null);
        html.appendChild(div2);
        div2.whenURI(uri -> true, () -> {

            controlFlow.append("2 div1.whenURI\n");

            AbstractHtml span = new Span(null).whenURI((uri) -> true, () -> {

                controlFlow.append("2 span.whenURI\n");

                AbstractHtml pParent = new CustomTag("p-parent", null) {
                    {
                        controlFlow.append("2 pParent.give\n");
                        AbstractHtml p = new P(this).whenURI((uri) -> true, () -> {

                            controlFlow.append("2 p.whenURI\n");

                            controlFlow.append("2 NoTag plain text\n");

                            return new AbstractHtml[] { new NoTag(null, "plain text") };
                        }, null);
                        expectedTagsForURIChange.add(p);
                    }
                };

                return new AbstractHtml[] { pParent };
            }, null);
            
            expectedTagsForURIChange.add(span);

            return new AbstractHtml[] { span };
        }, null);
        
        expectedTagsForURIChange.add(div2);

        assertEquals("""
                div1.whenURI
                span.whenURI
                pParent.give
                p.whenURI
                NoTag plain text
                2 div1.whenURI
                2 span.whenURI
                2 pParent.give
                2 p.whenURI
                2 NoTag plain text
                """, controlFlow.toString());
        assertEquals(
                "<div data-wff-id=\"S6\"><span data-wff-id=\"S7\"><p-parent data-wff-id=\"S8\"><p data-wff-id=\"S9\">plain text</p></p-parent></span></div>",
                div2.toBigHtmlString());

        AbstractHtml div3 = new Div(html);
        div3.whenURI(uri -> true, () -> {

            controlFlow.append("3 div1.whenURI\n");

            AbstractHtml span = new Span(null).whenURI((uri) -> true, () -> {

                controlFlow.append("3 span.whenURI\n");

                AbstractHtml pParent = new CustomTag("p-parent", null) {
                    {
                        controlFlow.append("3 pParent.give\n");
                        AbstractHtml p = new P(this).whenURI((uri) -> true, () -> {

                            controlFlow.append("3 p.whenURI\n");

                            controlFlow.append("3 NoTag plain text\n");

                            return new AbstractHtml[] { new NoTag(null, "plain text") };
                        }, null);
                        
                        expectedTagsForURIChange.add(p);
                    }
                };

                return new AbstractHtml[] { pParent };
            }, null);
            expectedTagsForURIChange.add(span);

            return new AbstractHtml[] { span };
        }, null);
        
        expectedTagsForURIChange.add(div3);

        assertEquals("""
                div1.whenURI
                span.whenURI
                pParent.give
                p.whenURI
                NoTag plain text
                2 div1.whenURI
                2 span.whenURI
                2 pParent.give
                2 p.whenURI
                2 NoTag plain text
                3 div1.whenURI
                3 span.whenURI
                3 pParent.give
                3 p.whenURI
                3 NoTag plain text
                """, controlFlow.toString());
        assertEquals(
                "<div data-wff-id=\"S10\"><span data-wff-id=\"S11\"><p-parent data-wff-id=\"S12\"><p data-wff-id=\"S13\">plain text</p></p-parent></span></div>",
                div3.toBigHtmlString());
        
        Set<AbstractHtml> tagsForURIChange = new HashSet<>();

        for (Reference<AbstractHtml> each : BrowserPageTest.getTagsForURIChangeForTest(browserPage)) {
            tagsForURIChange.add(each.get());
        }

        assertEquals(expectedTagsForURIChange.size(), tagsForURIChange.size());
        
        
        List<AbstractHtml> expectedTagsForURIChangeSorted = 
                expectedTagsForURIChange.stream().sorted(Comparator.comparingInt(Object::hashCode)).collect(Collectors.toList());
        
        List<AbstractHtml> tagsForURIChangeSorted = 
                tagsForURIChange.stream().sorted(Comparator.comparingInt(Object::hashCode)).collect(Collectors.toList());
        
        assertArrayEquals(expectedTagsForURIChangeSorted.toArray(), tagsForURIChangeSorted.toArray());

    }

}
