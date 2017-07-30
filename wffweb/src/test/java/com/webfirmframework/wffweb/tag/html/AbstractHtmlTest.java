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

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import com.webfirmframework.wffweb.InvalidTagException;
import com.webfirmframework.wffweb.NoParentException;
import com.webfirmframework.wffweb.tag.html.attribute.Name;
import com.webfirmframework.wffweb.tag.html.attribute.global.Id;
import com.webfirmframework.wffweb.tag.html.attribute.global.Style;
import com.webfirmframework.wffweb.tag.html.attributewff.CustomAttribute;
import com.webfirmframework.wffweb.tag.html.stylesandsemantics.Div;
import com.webfirmframework.wffweb.tag.html.stylesandsemantics.Span;
import com.webfirmframework.wffweb.tag.htmlwff.NoTag;

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
        System.out.println(
                "html.toHtmlString().length() " + html.toHtmlString().length());
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

                Assert.assertTrue(
                        Arrays.asList(name.getOwnerTags()).contains(div1));
                Assert.assertTrue(
                        Arrays.asList(name.getOwnerTags()).contains(div2));
            }
        };

        html.addAttributes(name);
        Assert.assertTrue(Arrays.asList(name.getOwnerTags()).contains(html));
        assertEquals(
                "<html name=\"somename\"><div name=\"somename\"></div><div name=\"somename\"></div></html>",
                html.toHtmlString());

        html.removeAttributes(name);
        Assert.assertFalse(Arrays.asList(name.getOwnerTags()).contains(html));
        assertEquals(
                "<html><div name=\"somename\"></div><div name=\"somename\"></div></html>",
                html.toHtmlString());

        html.addAttributes(name);
        Assert.assertTrue(Arrays.asList(name.getOwnerTags()).contains(html));
        assertEquals(
                "<html name=\"somename\"><div name=\"somename\"></div><div name=\"somename\"></div></html>",
                html.toHtmlString());

        name.setValue("another");
        assertEquals(
                "<html name=\"another\"><div name=\"another\"></div><div name=\"another\"></div></html>",
                html.toHtmlString());

        name.setValue("somename");
        html.removeAttributes("name");
        Assert.assertFalse(Arrays.asList(name.getOwnerTags()).contains(html));
        assertEquals(
                "<html><div name=\"somename\"></div><div name=\"somename\"></div></html>",
                html.toHtmlString());

        html.addAttributes(name);
        Assert.assertTrue(Arrays.asList(name.getOwnerTags()).contains(html));
        assertEquals(
                "<html name=\"somename\"><div name=\"somename\"></div><div name=\"somename\"></div></html>",
                html.toHtmlString());

        html.removeAttributes(new Name("somename"));
        Assert.assertTrue(Arrays.asList(name.getOwnerTags()).contains(html));
        assertEquals(
                "<html name=\"somename\"><div name=\"somename\"></div><div name=\"somename\"></div></html>",
                html.toHtmlString());

        Name name2 = new Name("another");
        html.addAttributes(name2);
        Assert.assertFalse(Arrays.asList(name.getOwnerTags()).contains(html));
        Assert.assertTrue(Arrays.asList(name2.getOwnerTags()).contains(html));
        assertEquals(
                "<html name=\"another\"><div name=\"somename\"></div><div name=\"somename\"></div></html>",
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

            System.out.println("testPerformanceAsInnerClassFormat "
                    + htmlString.length() + " tag bytes generation took "
                    + (after - before) + " ms");
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

            System.out.println("testPerformanceAsNonInnerClassFormat "
                    + htmlString.length() + " tag bytes generation took "
                    + (after - before) + " ms");
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

            System.out.println("htmlString.length() : " + htmlString.length()
                    + " wffBytes.length " + wffBytes.length);
            System.out.println("htmlString.length() - wffBytes.length : "
                    + (htmlString.length() - wffBytes.length));

            System.out.println("testPerformanceByArrayDeque " + wffBytes.length
                    + " tag bytes generation took " + (after - before) + " ms");
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
                                new H4(this, new CustomAttribute("data-wff-id",
                                        "S6"));
                            }
                        };
                    }
                };

                new H3(this, new CustomAttribute("data-wff-id", "S5"));
            }
        };

        try {

            byte[] wffBMBytes = div.toWffBMBytes("UTF-8");

            AbstractHtml abstractHtml = AbstractHtml
                    .getTagFromWffBMBytes(wffBMBytes);

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
        System.out.println(parentDiv.toHtmlString());

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
        System.out.println(parentDiv.toHtmlString());
        
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

    }

}
