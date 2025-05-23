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
package com.webfirmframework.wffweb.tag.repository;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.webfirmframework.wffweb.InvalidTagException;
import com.webfirmframework.wffweb.NullValueException;
import com.webfirmframework.wffweb.WffSecurityException;
import com.webfirmframework.wffweb.server.page.BrowserPage;
import com.webfirmframework.wffweb.tag.html.AbstractHtml;
import com.webfirmframework.wffweb.tag.html.Body;
import com.webfirmframework.wffweb.tag.html.H1;
import com.webfirmframework.wffweb.tag.html.H2;
import com.webfirmframework.wffweb.tag.html.H3;
import com.webfirmframework.wffweb.tag.html.Html;
import com.webfirmframework.wffweb.tag.html.TagNameConstants;
import com.webfirmframework.wffweb.tag.html.TitleTag;
import com.webfirmframework.wffweb.tag.html.attribute.AttributeNameConstants;
import com.webfirmframework.wffweb.tag.html.attribute.Name;
import com.webfirmframework.wffweb.tag.html.attribute.core.AbstractAttribute;
import com.webfirmframework.wffweb.tag.html.attribute.global.Id;
import com.webfirmframework.wffweb.tag.html.formsandinputs.Form;
import com.webfirmframework.wffweb.tag.html.formsandinputs.Input;
import com.webfirmframework.wffweb.tag.html.formsandinputs.TextArea;
import com.webfirmframework.wffweb.tag.html.metainfo.Head;
import com.webfirmframework.wffweb.tag.html.stylesandsemantics.Div;
import com.webfirmframework.wffweb.tag.html.stylesandsemantics.Span;
import com.webfirmframework.wffweb.tag.html.tables.TBody;
import com.webfirmframework.wffweb.tag.html.tables.Table;
import com.webfirmframework.wffweb.tag.html.tables.Td;
import com.webfirmframework.wffweb.tag.html.tables.Tr;
import com.webfirmframework.wffweb.tag.htmlwff.NoTag;
import com.webfirmframework.wffweb.tag.htmlwff.TagContent;
import com.webfirmframework.wffweb.wffbm.data.BMValueType;
import com.webfirmframework.wffweb.wffbm.data.WffBMArray;
import com.webfirmframework.wffweb.wffbm.data.WffBMObject;

@SuppressWarnings({ "serial" })
public class TagRepositoryTest {  
    
    @Test
    public void testFindAllTags() {
        
        
        final Html html = new Html(null) {{
            new Body(this) {{
                new Div(this, new Id("one")) {{
                    new Span(this, new Id("two")) {{
                        new H1(this, new Id("three"));
                        new H2(this, new Id("three"));
                        new NoTag(this, "something");
                    }};

                    new H3(this, new Name("name1"));
                }};  
            }};
            
        }};
        
        {
            final Collection<AbstractHtml> set = TagRepository.findAllTags(false, html);       
            
            assertEquals(8, set.size());
            
            assertTrue(set instanceof HashSet);
        }
        {
            final Collection<AbstractHtml> set =TagRepository.findAllTags(true, html);       
            
            assertEquals(8, set.size());
        }
    }
    
    @Test
    public void testFindAllAttributes() throws Exception {
        final Html html = new Html(null, new Id("yesId")) {{
            new Body(this) {{
                new Div(this, new Id("one"), new Name("name1")) {{
                    new Span(this, new Id("two")) {{
                        new H1(this, new Id("three"), new Name("name1"));
                        new H2(this, new Id("four"));
                        new NoTag(this, "something");
                    }};
                    
                    new Span(this, new Id("spanthree"));

                    new H3(this, new Name("name1"));
                }};  
            }};
            
        }};
        
        final Collection<AbstractAttribute> allAttributes = TagRepository.findAllAttributes(false, html);
        assertEquals(9, allAttributes.size());
    }
    
    @Test
    public void testStaticFindTagsByTagNameBooleanStringAbstractHtmls() throws Exception {
        final Html html = new Html(null) {{
            new Body(this) {{
                new Div(this, new Id("one"), new Name("name1")) {{
                    new Span(this, new Id("two")) {{
                        new H1(this, new Id("three"), new Name("name1"));
                        new H2(this, new Id("four"));
                        new NoTag(this, "something");
                    }};
                    
                    new Span(this, new Id("spanthree"));

                    new H3(this, new Name("name1"));
                }};  
            }};
            
        }};
        
        {
            final Collection<AbstractHtml> tags = TagRepository.findTagsByTagName(false, TagNameConstants.SPAN, html);
            
            for (AbstractHtml tag : tags) {
                assertTrue((tag instanceof Span));
                
            }
            assertEquals(2, tags.size());
        }
        {
            final Collection<AbstractHtml> tags = TagRepository.findTagsByTagName(false, TagNameConstants.HTML, html);
            
            for (AbstractHtml tag : tags) {
                assertTrue((tag instanceof Html));
                
            }
            assertEquals(1, tags.size());
        }
    }
    
    @Test
    public void testFindTagsByAttributeStringStringAbstractHtmls() throws Exception {
        
        final Html html = new Html(null) {{
            new Body(this) {{
                new Div(this, new Id("one"), new Name("name1")) {{
                    new Span(this, new Id("two")) {{
                        new H1(this, new Id("three"), new Name("name1"));
                        new H2(this, new Id("four"));
                        new NoTag(this, "something");
                    }};

                    new H3(this, new Name("name1"));
                }};  
            }};
            
        }};
        
        final Collection<AbstractHtml> tags = TagRepository.findTagsByAttribute(AttributeNameConstants.NAME, "name1", html);
        
        for (AbstractHtml tag : tags) {
            assertTrue((tag instanceof Div) || (tag instanceof H1) || (tag instanceof H3));
            
        }
        
    }
    
    @Test
    public void testFindBodyTag() throws Exception {
        
        final Html html = new Html(null) {{
            new Body(this) {{
                new Div(this, new Id("one")) {{
                    new Span(this, new Id("two")) {{
                        new H1(this, new Id("three"));
                        new H2(this, new Id("three"));
                        new NoTag(this, "something");
                    }};

                    new H3(this, new Name("name1"));
                }};  
            }};
            
        }};
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
        
        {
            final Body tag = browserPage.getTagRepository().findBodyTag();
            assertNotNull(tag);
            assertTrue(tag instanceof Body);
        }
        {
            final Body tag = browserPage.getTagRepository().findBodyTag(false);
            assertNotNull(tag);
            assertTrue(tag instanceof Body);
        }
        {
            final Body tag = browserPage.getTagRepository().findBodyTag(true);
            assertNotNull(tag);
            assertTrue(tag instanceof Body);
        }
    }
    
    @Test
    public void testFindTitleTag() throws Exception {
        
        final Html html = new Html(null) {{
            new TitleTag(this) {{
              new NoTag(this);  
            }};
            new Body(this) {{
                new Div(this, new Id("one")) {{
                    new Span(this, new Id("two")) {{
                        new H1(this, new Id("three"));
                        new H2(this, new Id("three"));
                        new NoTag(this, "something");
                    }};
                    
                    new H3(this, new Name("name1"));
                }};  
            }};
            
        }};
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
        
        {
            final TitleTag tag = browserPage.getTagRepository().findTitleTag();
            assertNotNull(tag);
            assertTrue(tag instanceof TitleTag);
        }
        {
            final TitleTag tag = browserPage.getTagRepository().findTitleTag(false);
            assertNotNull(tag);
            assertTrue(tag instanceof TitleTag);
        }
        {
            final TitleTag tag = browserPage.getTagRepository().findTitleTag(true);
            assertNotNull(tag);
            assertTrue(tag instanceof TitleTag);
        }
    }
    
    @Test
    public void testFindHeadTag() throws Exception {
        
        final Html html = new Html(null) {{
            new Head(this);
            new Body(this) {{
                new Div(this, new Id("one")) {{
                    new Span(this, new Id("two")) {{
                        new H1(this, new Id("three"));
                        new H2(this, new Id("three"));
                        new NoTag(this, "something");
                    }};
                    
                    new H3(this, new Name("name1"));
                }};  
            }};
            
        }};
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
        
        {
            final Head tag = browserPage.getTagRepository().findHeadTag();
            assertNotNull(tag);
            assertTrue(tag instanceof Head);
        }
        {
            final Head tag = browserPage.getTagRepository().findHeadTag(false);
            assertNotNull(tag);
            assertTrue(tag instanceof Head);
        }
        {
            final Head tag = browserPage.getTagRepository().findHeadTag(true);
            assertNotNull(tag);
            assertTrue(tag instanceof Head);
        }
    }
    
    @Test
    public void testFindOneTagByAttribute() throws Exception {
        final Html html = new Html(null) {{
            new Div(this, new Id("one")) {{
                new Span(this, new Id("two")) {{
                    new H1(this, new Id("three"));
                    new H2(this, new Id("three"));
                    new NoTag(this, "something");
                }};

                new H3(this, new Name("name1"));
            }};  
        }};
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
        
        {
            final AbstractHtml tag = browserPage.getTagRepository().findOneTagByAttribute("name", "name1");
            assertNotNull(tag);
            assertTrue(tag instanceof H3);
        }
        
        {
            final AbstractHtml tag = browserPage.getTagRepository().findOneTagByAttribute("name", "name1222");
            assertNull(tag);
        }
        {
            final AbstractHtml tag = TagRepository.findOneTagByAttribute("name", "name1", html);
            assertNotNull(tag);
            assertTrue(tag instanceof H3);
        }
        
        {
            final AbstractHtml tag = TagRepository.findOneTagByAttribute(false, "name", "name1222", html);
            assertNull(tag);
        }
        {
            final AbstractHtml tag = TagRepository.findOneTagByAttribute(true, "name", "name1222", html);
            assertNull(tag);
        }
        {
            final AbstractHtml tag = browserPage.getTagRepository().findOneTagByAttributeName("name");
            assertNotNull(tag);
            assertTrue(tag instanceof H3);
        }
        {
            final AbstractHtml tag = browserPage.getTagRepository().findOneTagByAttributeName("style");
            assertNull(tag);
        }
        {
            final AbstractHtml tag = TagRepository.findOneTagByAttributeName(false, "name", html);
            assertNotNull(tag);
            assertTrue(tag instanceof H3);
        }
        {
            final AbstractHtml tag = TagRepository.findOneTagByAttributeName(false, "style", html);
            assertNull(tag);
        }
        {
            final AbstractHtml tag = TagRepository.findOneTagByAttributeName(true, "name", html);
            assertNotNull(tag);
            assertTrue(tag instanceof H3);
        }
        {
            final AbstractHtml tag = TagRepository.findOneTagByAttributeName(true, "style", html);
            assertNull(tag);
        }
    }

    @Test
    public void testFindTagById() {
        final Form form = new Form(null) {
            {
                new Input(this, new Name("name1"));
                new Div(this, new Name("excluded")){{new Input(this, new Name("name2"));}};
                new Input(this, new Name("name3"));
                new Div(this){{new Input(this, new Name("name4"));}};
                new Div(this){{new Input(this, new Id("id4"));}};
                new Div(this){{new Input(this, new Name("name4"));}};
                new TextArea(this, new Name("included"));
            }
        };
        
        final Input input44 = new Input(null, new Id("id44"));
        
        final Form form2 = new Form(null) {
            {
                new Input(this, new Name("name1"));
                new Div(this, new Name("excluded")){{new Input(this, new Name("name2"));}};
                new Input(this, new Name("name3"));
                new Div(this){{new Input(this, new Name("name4"));}};
                new Div(this){{appendChild(input44);}};
                new Div(this){{new Input(this, new Name("name4"));}};
                new TextArea(this, new Name("included"));
            }
        };
        
        AbstractHtml found = TagRepository.findTagById("id4", form);
        
        Assert.assertNotNull(found);
        Assert.assertEquals("id4", ((Id) found.getAttributeByName("id")).getValue());
        
        AbstractHtml found2 = TagRepository.findTagById("id44", form2);
        Assert.assertNotNull(found2);
        Assert.assertEquals(input44, found2);
        Assert.assertEquals("id44", ((Id) found2.getAttributeByName("id")).getValue());
        
        
    }
    
    @Test
    public void testFindTagsByTagName() {
        final Set<Div> divs = new HashSet<Div>();
        
        final Form form = new Form(null) {
            {
                new Input(this, new Name("name1"));
                Div d1 = new Div(this, new Name("excluded")){{
                    new Input(this, new Name("name2"));
                    }};
                    divs.add(d1);
                new Input(this, new Name("name3"));
                Div d2 = new Div(this){{new Input(this, new Name("name4"));}};
                divs.add(d2);
                Div d3 = new Div(this){{new Input(this, new Id("id4"));}};
                divs.add(d3);
                Div d4 = new Div(this){{new Input(this, new Name("name4"));}};
                divs.add(d4);
                new TextArea(this, new Name("included"));
            }
        };
        
        
        Assert.assertEquals(form, TagRepository.findOneTagByTagName(TagNameConstants.FORM, form));
        Assert.assertEquals("div", TagRepository.findOneTagByTagName(TagNameConstants.DIV, form).getTagName());
        
        final Collection<AbstractHtml> tagsByTagName = TagRepository.findTagsByTagName(TagNameConstants.DIV, form);
        Assert.assertTrue(tagsByTagName.size() == divs.size());
        
        for (Div div : divs) {
            Assert.assertTrue(tagsByTagName.contains(div));
        }
        
        
        final AbstractHtml[] tags = new AbstractHtml[1];
        
        Html html = new Html(null) {{
            new Head(this);
            new Body(this) {{
                tags[0] = this;
                    new Div(this) {{
                            new NoTag(this, "\nsamplediv ");
                            new Div(this);
                    }};
            }};
        }};
        
        Assert.assertEquals(tags[0], TagRepository.findOneTagByTagName(TagNameConstants.BODY, html));
        Assert.assertEquals("div", TagRepository.findOneTagByTagName(TagNameConstants.DIV, html).getTagName());
        
    }
    
    @Test
    public void testFindAttributesByTagName() {
        
        
        final Set<AbstractAttribute> attributes = new HashSet<AbstractAttribute>();
        
        final Id idTwo = new Id("two");
        final Name name = new Name("name");
        
        attributes.add(idTwo);
        attributes.add(name);
        
        
        Html html = new Html(null) {{
            new Head(this);
            new Body(this, new Id("one")) {{
                new Div(this, idTwo, name) {{
                    new NoTag(this, "\nsamplediv ");
                    new Div(this);
                }};
            }};
        }};
        
        final Collection<AbstractAttribute> attributesByTagName = TagRepository.findAttributesByTagName(TagNameConstants.DIV, html);
        Assert.assertTrue(attributesByTagName.size() == attributes.size());
        
        for (AbstractAttribute attr : attributes) {
            Assert.assertTrue(attributesByTagName.contains(attr));
        }
        
    }
    
    @Test (expected = WffSecurityException.class)
    public void testWffSecurityException() {
        new TagRepository(null, null, null, new AbstractHtml[0]);
    }
    
    @Test (expected = InvalidTagException.class)
    public void testUpsertWffBMObjectInvalidTagException() {
        BrowserPage browserPage = new BrowserPage() {
            
            @Override
            public String webSocketUrl() {
                return "wss://socketurl";
            }
            
            @Override
            public AbstractHtml render() {
                Html html = new Html(null) {{
                    new Head(this) {{
                        new TitleTag(this){{
                            new NoTag(this, "some title");
                        }};
                    }};
                    new Body(this, new Id("one")) {{
                        new Div(this);
                    }};
                }}; 
                return html;
            }
        };
        browserPage.toHtmlString();
        TagRepository tagRepository = browserPage.getTagRepository();
        tagRepository.upsert(new NoTag(null, "test"), "somekey", new WffBMObject());
    }
    
    @Test (expected = InvalidTagException.class)
    public void testUpsertWffBMArrayInvalidTagException() {
        BrowserPage browserPage = new BrowserPage() {
            
            @Override
            public String webSocketUrl() {
                return "wss://socketurl";
            }
            
            @Override
            public AbstractHtml render() {
                Html html = new Html(null) {{
                    new Head(this) {{
                        new TitleTag(this){{
                            new NoTag(this, "some title");
                        }};
                    }};
                    new Body(this, new Id("one")) {{
                        new Div(this);
                    }};
                }}; 
                return html;
            }
        };
        browserPage.toHtmlString();
        TagRepository tagRepository = browserPage.getTagRepository();
        tagRepository.upsert(new NoTag(null, "test"), "somekey", new WffBMArray(BMValueType.STRING));
    }
    
    @Test (expected = InvalidTagException.class)
    public void testDeleteWffBMObjectOrArrayInvalidTagException() {
        BrowserPage browserPage = new BrowserPage() {
            
            @Override
            public String webSocketUrl() {
                return "wss://socketurl";
            }
            
            @Override
            public AbstractHtml render() {
                Html html = new Html(null) {{
                    new Head(this) {{
                        new TitleTag(this){{
                            new NoTag(this, "some title");
                        }};
                    }};
                    new Body(this, new Id("one")) {{
                        new Div(this);
                    }};
                }}; 
                return html;
            }
        };
        browserPage.toHtmlString();
        TagRepository tagRepository = browserPage.getTagRepository();
        tagRepository.delete(new NoTag(null, "test"), "somekey");
    }
    
    @Test (expected = InvalidTagException.class)
    public void testUpsertWffBMObjectInvalidTagException1() {
        BrowserPage browserPage = new BrowserPage() {
            
            @Override
            public String webSocketUrl() {
                return "wss://socketurl";
            }
            
            @Override
            public AbstractHtml render() {
                Html html = new Html(null) {{
                    new Head(this) {{
                        new TitleTag(this){{
                            new NoTag(this, "some title");
                        }};
                    }};
                    new Body(this, new Id("one")) {{
                        new Div(this);
                    }};
                }}; 
                return html;
            }
        };
        browserPage.toHtmlString();
        TagRepository tagRepository = browserPage.getTagRepository();
        tagRepository.upsert(new NoTag(null, "test"), "somekey", new WffBMObject());
    }
    
    @Test (expected = InvalidTagException.class)
    public void testUpsertWffBMArrayInvalidTagException1() {
        BrowserPage browserPage = new BrowserPage() {
            
            @Override
            public String webSocketUrl() {
                return "wss://socketurl";
            }
            
            @Override
            public AbstractHtml render() {
                Html html = new Html(null) {{
                    new Head(this) {{
                        new TitleTag(this){{
                            new NoTag(this, "some title");
                        }};
                    }};
                    new Body(this, new Id("one")) {{
                        new Div(this);
                    }};
                }}; 
                return html;
            }
        };
        browserPage.toHtmlString();
        TagRepository tagRepository = browserPage.getTagRepository();
        tagRepository.upsert(new NoTag(null, "test"), "somekey", new WffBMArray(BMValueType.STRING));
    }
    
    @Test (expected = InvalidTagException.class)
    public void testDeleteWffBMObjectOrArrayInvalidTagException1() {
        BrowserPage browserPage = new BrowserPage() {
            
            @Override
            public String webSocketUrl() {
                return "wss://socketurl";
            }
            
            @Override
            public AbstractHtml render() {
                Html html = new Html(null) {{
                    new Head(this) {{
                        new TitleTag(this){{
                            new NoTag(this, "some title");
                        }};
                    }};
                    new Body(this, new Id("one")) {{
                        new Div(this);
                    }};
                }}; 
                return html;
            }
        };
        browserPage.toHtmlString();
        TagRepository tagRepository = browserPage.getTagRepository();
        tagRepository.delete(new NoTag(null, "test"), "somekey");
    }
    
    @Test
    public void testFindOneTagAssignableToTag() {
        Html html = new Html(null) {{
            new Head(this) {{
                new TitleTag(this){{
                    new NoTag(this, "some title");
                }};
            }};
            new Body(this, new Id("one")) {{
                new Div(this);
            }};
        }}; 
        html.appendChild(new Div(null));
        
        AbstractHtml abstractHtml = TagRepository.findOneTagAssignableToTag(AbstractHtml.class, html);
        Assert.assertNotNull(abstractHtml);
        Assert.assertNotNull(abstractHtml.toHtmlString());
        
        Head head = TagRepository.findOneTagAssignableToTag(Head.class, html);
        Assert.assertNotNull(head);
        Assert.assertNotNull(head.toHtmlString());
        
        TitleTag titleTag = TagRepository.findOneTagAssignableToTag(TitleTag.class, html);
        Assert.assertNotNull(titleTag);
        Assert.assertNotNull(titleTag.toHtmlString());
        
        Assert.assertEquals("<title>some title</title>", titleTag.toHtmlString());
        
        Body body = TagRepository.findOneTagAssignableToTag(Body.class, html);
        Assert.assertNotNull(body);
        Assert.assertNotNull(body.toHtmlString());
        
        Div div = TagRepository.findOneTagAssignableToTag(Div.class, html);
        Assert.assertNotNull(div);
        Assert.assertNotNull(div.toHtmlString());
        
        Span span = TagRepository.findOneTagAssignableToTag(Span.class, html);
        Assert.assertNull(span);
        
    }
    
    @Test (expected = InvalidTagException.class)
    public void testFindOneTagAssignableToTagThrowsInvalidTagException() {
        Html html = new Html(null) {{
            new Head(this) {{
                new TitleTag(this){{
                    new NoTag(this, "some title");
                }};
            }};
            new Body(this, new Id("one")) {{
                new Div(this);
            }};
        }}; 
        html.appendChild(new Div(null));
        
        TagRepository.findOneTagAssignableToTag(NoTag.class, html);
    }
    
    @Test (expected = InvalidTagException.class)
    public void testFindOneTagAssignableToTagThrowsInvalidTagException2() {
        Html html = new Html(null) {{
            new Head(this) {{
                new TitleTag(this){{
                    new NoTag(this, "some title");
                }};
            }};
            new Body(this, new Id("one")) {{
                new Div(this);
            }};
        }}; 
        html.appendChild(new Div(null));
        
        TagRepository.findOneTagAssignableToTag(NoTag.class, html);
    }
    
    @Test
    public void testFindTagsAssignableToTag() {
        Html html = new Html(null) {{
            new Head(this) {{
                new TitleTag(this){{
                    new NoTag(this, "some title");
                }};
            }};
            new Body(this, new Id("one")) {{
                new Div(this);
                new Div(this) {{
                    new Div(this) {{
                        new Div(this);
                    }};
                }};
                new Div(this);
            }};
        }}; 
        
        Collection<AbstractHtml> abstractHtmls = TagRepository.findTagsAssignableToTag(AbstractHtml.class, html);
        Assert.assertNotNull(abstractHtmls);
        Assert.assertEquals(9, abstractHtmls.size());
        for (AbstractHtml abstractHtml : abstractHtmls) {
            Assert.assertNotNull(abstractHtml.toHtmlString());
        }
        
        Collection<Head> heads = TagRepository.findTagsAssignableToTag(Head.class, html);
        Assert.assertNotNull(heads);
        Assert.assertEquals(1, heads.size());
        for (Head each : heads) {
            Assert.assertNotNull(each.toHtmlString());
        }
        
        Collection<Div> divs = TagRepository.findTagsAssignableToTag(Div.class, html);
        Assert.assertNotNull(divs);
        Assert.assertEquals(5, divs.size());
        for (Div each : divs) {
            Assert.assertNotNull(each.toHtmlString());
        }
        
    }
    
    @Test (expected = InvalidTagException.class)
    public void testFindTagsAssignableToTagThrowsInvalidTagException() {
        Html html = new Html(null) {{
            new Head(this) {{
                new TitleTag(this){{
                    new NoTag(this, "some title");
                }};
            }};
            new Body(this, new Id("one")) {{
                new Div(this);
            }};
        }};
        
        TagRepository.findTagsAssignableToTag(NoTag.class, html);
    }
    
    @Test (expected = InvalidTagException.class)
    public void testFindTagsAssignableToTagThrowsInvalidTagException2() {
        Html html = new Html(null) {{
            new Head(this) {{
                new TitleTag(this){{
                    new NoTag(this, "some title");
                }};
            }};
            new Body(this, new Id("one")) {{
                new Div(this);
            }};
        }};
        
        TagRepository.findTagsAssignableToTag(NoTag.class, html);
    }
    

    @Test
    public void testFindFirstParentTagAssignableToTag() throws Exception {
        Html html = new Html(null).give(t -> {
            new Head(t).give(head -> {
                new TitleTag(head).give(TagContent::text, "some title");
            });
            new Body(t, new Id("one")).give(body -> {
                
                new Div(body).give(topDv -> {
                    new Div(topDv).give(dv -> {
                        new Table(dv).give(table -> {                        
                           new TBody(table).give(tBody -> {
                               new Tr(tBody).give(tr -> {
                                   Td td1 = new Td(tr).give(TagContent::text, "col1"); 
                                   Td td2 = new Td(tr).give(TagContent::text, "col2"); 
                                   
                                   TBody tBdy = TagRepository.findFirstParentTagAssignableToTag(TBody.class, td1);
                                   assertNotNull(tBdy);
                                   assertEquals(tBody, tBdy);
                                   
                                   tBdy = TagRepository.findFirstParentTagAssignableToTag(TBody.class, td2);
                                   assertNotNull(tBdy);
                                   assertEquals(tBody, tBdy);
                                   
                                   //firstChild is NoTag("col2")
                                   final AbstractHtml firstChild = td2.getFirstChild();
                                   assertEquals(NoTag.class, firstChild.getClass());
                                   tBdy = TagRepository.findFirstParentTagAssignableToTag(TBody.class, firstChild);
                                   assertNotNull(tBdy);
                                   assertEquals(tBody, tBdy);
                                   
                                   Div firstParentDv = TagRepository.findFirstParentTagAssignableToTag(Div.class, td1);
                                   assertNotNull(firstParentDv);
                                   assertEquals(dv, firstParentDv);
                                   
                                   firstParentDv = TagRepository.findFirstParentTagAssignableToTag(Div.class, dv);
                                   assertNotNull(firstParentDv);
                                   assertEquals(topDv, firstParentDv);
                                   
                                   Head head = TagRepository.findFirstParentTagAssignableToTag(Head.class, td1);
                                   assertNull(head);
                               });
                           }); 
                        });
                    });
                });
                
            });
        });
        Html parentOfHtml = TagRepository.findFirstParentTagAssignableToTag(Html.class, html);
        assertNull(parentOfHtml);
    }
    
    @Test(expected = NullValueException.class)
    public void testFindFirstParentTagAssignableToTagExp1() {
        TagRepository.findFirstParentTagAssignableToTag(Html.class, null);
    }
    
    @Test(expected = NullValueException.class)
    public void testFindFirstParentTagAssignableToTagExp2() {
        TagRepository.findFirstParentTagAssignableToTag(null, new Div(null));
    }
    
    @Test(expected = InvalidTagException.class)
    public void testFindFirstParentTagAssignableToTagExp3() {
        TagRepository.findFirstParentTagAssignableToTag(NoTag.class, null);
    }
    
    @Test
    public void testParallel() throws Exception {
        Html html = new Html(null).give(t -> {
            new Head(t).give(head -> {
                new TitleTag(head).give(TagContent::text, "some title");
            });
            new Body(t, new Id("one")).give(body -> {

                new Div(body).give(topDv -> {
                    new Div(topDv).give(dv -> {
                        for (int i = 0; i < 100; i++) {
                            new Div(dv);
                        }
                        new Div(dv, new Id("someDivId"));
                    });
                });

            });
        });
        
        AbstractHtml tag = TagRepository.findTagById(true, "someDivId", html);
        assertNotNull(tag);
        assertEquals("<div id=\"someDivId\"></div>", tag.toHtmlString());
        
        //TODO test other methods with parallel true
    }

    @Test
    public void testFindOneAttributeByAttributeNameAndValue() throws Exception {
        final AbstractAttribute name1Attr = new Name("name1");
        final Html html = new Html(null) {{
            new Div(this, new Id("one")) {{
                new Span(this, new Id("two")) {{
                    new H1(this, new Id("three"));
                    new H2(this, new Id("three"));
                    new NoTag(this, "something");
                }};

                new H3(this, name1Attr);
            }};
        }};
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

        {
            final AbstractAttribute attribute = browserPage.getTagRepository().findOneAttributeByAttributeNameAndValue("name", "name1");
            assertNotNull(attribute);
            assertEquals(name1Attr, attribute);
        }

        {
            final AbstractAttribute attribute = browserPage.getTagRepository().findOneAttributeByAttributeNameAndValue("name", "name1222");
            assertNull(attribute);
        }
        {
            final AbstractAttribute attribute = TagRepository.findOneAttributeByAttributeNameAndValue("name", "name1", html);
            assertNotNull(attribute);
            assertEquals(name1Attr, attribute);
        }

        {
            final AbstractAttribute attribute = TagRepository.findOneAttributeByAttributeNameAndValue(false, "name", "name1222", html);
            assertNull(attribute);
        }
        {
            final AbstractAttribute attribute = TagRepository.findOneAttributeByAttributeNameAndValue(true, "name", "name1222", html);
            assertNull(attribute);
        }
        {
            final AbstractAttribute attribute = browserPage.getTagRepository().findOneAttributeByAttributeName("name");
            assertNotNull(attribute);
            assertEquals(name1Attr, attribute);
        }
        {
            final AbstractAttribute attribute = browserPage.getTagRepository().findOneAttributeByAttributeName("style");
            assertNull(attribute);
        }
        {
            final AbstractAttribute attribute = TagRepository.findOneAttributeByAttributeName(false, "name", html);
            assertNotNull(attribute);
            assertEquals(name1Attr, attribute);
        }
        {
            final AbstractAttribute attribute = TagRepository.findOneAttributeByAttributeName(false, "style", html);
            assertNull(attribute);
        }
        {
            final AbstractAttribute attribute = TagRepository.findOneAttributeByAttributeName(true, "name", html);
            assertNotNull(attribute);
            assertEquals(name1Attr, attribute);
        }
        {
            final AbstractAttribute attribute = TagRepository.findOneAttributeByAttributeName(true, "style", html);
            assertNull(attribute);
        }
    }

    @Test
    public void testFindAttributesByAttributeNameAndValue() throws Exception {
        final AbstractAttribute unusedAttr = new Name("name1");
        final AbstractAttribute name1Attr1 = new Name("name1");
        final AbstractAttribute name1Attr2 = new Name("name1");
        final Html html = new Html(null) {{
            new Div(this, new Id("one")) {{
                new Span(this, new Id("two")) {{
                    new H1(this, new Id("three"));
                    new H2(this, new Id("three"));
                    new NoTag(this, "something");
                }};

                new H3(this, name1Attr1);
                new H3(this, name1Attr2);
            }};
        }};
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

        Set<AbstractAttribute> expectedAttributes = Set.of(name1Attr1, name1Attr2);
        Set<AbstractAttribute> notExpectedAttributes = Set.of(unusedAttr);
        {
            final Collection<AbstractAttribute> attributes = browserPage.getTagRepository().findAttributesByAttributeNameAndValue("name", "name1");
            assertNotNull(attributes);
            assertEquals(expectedAttributes, attributes);
            assertNotEquals(notExpectedAttributes, attributes);
        }

        {
            final Collection<AbstractAttribute> attributes = browserPage.getTagRepository().findAttributesByAttributeNameAndValue("name", "name1222");
            assertNotNull(attributes);
            assertTrue(attributes.isEmpty());
        }
        {
            final Collection<AbstractAttribute> attributes = TagRepository.findAttributesByAttributeNameAndValue("name", "name1", html);
            assertNotNull(attributes);
            assertEquals(expectedAttributes, attributes);
            assertNotEquals(notExpectedAttributes, attributes);
        }

        {
            final Collection<AbstractAttribute> attributes = TagRepository.findAttributesByAttributeNameAndValue(false, "name", "name1222", html);
            assertNotNull(attributes);
            assertTrue(attributes.isEmpty());
        }
        {
            final Collection<AbstractAttribute> attributes = TagRepository.findAttributesByAttributeNameAndValue(true, "name", "name1222", html);
            assertNotNull(attributes);
            assertTrue(attributes.isEmpty());
        }
        {
            final Collection<AbstractAttribute> attributes = browserPage.getTagRepository().findAttributesByAttributeName("name");
            assertNotNull(attributes);
            assertEquals(expectedAttributes, attributes);
            assertNotEquals(notExpectedAttributes, attributes);
        }
        {
            final Collection<AbstractAttribute> attributes = browserPage.getTagRepository().findAttributesByAttributeName("style");
            assertNotNull(attributes);
            assertTrue(attributes.isEmpty());
        }
        {
            final Collection<AbstractAttribute> attributes = TagRepository.findAttributesByAttributeName(false, "name", html);
            assertNotNull(attributes);
            assertEquals(expectedAttributes, attributes);
            assertNotEquals(notExpectedAttributes, attributes);
        }
        {
            final Collection<AbstractAttribute> attributes = TagRepository.findAttributesByAttributeName(false, "style", html);
            assertNotNull(attributes);
            assertTrue(attributes.isEmpty());
        }
        {
            final Collection<AbstractAttribute> attributes = TagRepository.findAttributesByAttributeName("style", html);
            assertNotNull(attributes);
            assertTrue(attributes.isEmpty());
        }
        {
            final Collection<AbstractAttribute> attributes = TagRepository.findAttributesByAttributeName(true, "name", html);
            assertNotNull(attributes);
            assertEquals(expectedAttributes, attributes);
            assertNotEquals(notExpectedAttributes, attributes);
        }
        {
            final Collection<AbstractAttribute> attributes = TagRepository.findAttributesByAttributeName(false, "style", html);
            assertNotNull(attributes);
            assertTrue(attributes.isEmpty());
        }
        {
            final Collection<AbstractAttribute> attributes = TagRepository.findAttributesByAttributeName("style", html);
            assertNotNull(attributes);
            assertTrue(attributes.isEmpty());
        }
        {
            final Collection<AbstractAttribute> attributes = TagRepository.findAttributesByAttributeName(true, "style", html);
            assertNotNull(attributes);
            assertTrue(attributes.isEmpty());
        }
    }

    @Test
    public void testFindOneAttributeByFilter() throws Exception {
        final Html html = new Html(null) {{
            new Div(this, new Id("one")) {{
                new Span(this, new Id("two")) {{
                    new H1(this, new Id("three"));
                    new H2(this, new Id("three"));
                    new NoTag(this, "something");
                }};

                new H3(this, new Name("name1"));
            }};
        }};
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
        {
            final AbstractAttribute attribute = TagRepository.findOneAttributeByFilter(false, attributeNameValue -> "name".equals(attributeNameValue.name()) && "name1222".equals(attributeNameValue.value()), html);
            assertNull(attribute);
        }
        {
            final AbstractAttribute attribute = TagRepository.findOneAttributeByFilter(true, attributeNameValue -> "name".equals(attributeNameValue.name()) && "name1222".equals(attributeNameValue.value()), html);
            assertNull(attribute);
        }
        {
            final AbstractAttribute attribute = TagRepository.findOneAttributeByFilter(false, attributeNameValue -> "id".equals(attributeNameValue.name()) && "three".equals(attributeNameValue.value()), html);
            assertNotNull(attribute);
            assertEquals("id", attribute.getAttributeName());
            assertEquals("three", attribute.getAttributeValue());
        }
        {
            final AbstractAttribute attribute = TagRepository.findOneAttributeByFilter(true, attributeNameValue -> "id".equals(attributeNameValue.name()) && "three".equals(attributeNameValue.value()), html);
            assertNotNull(attribute);
            assertEquals("id", attribute.getAttributeName());
            assertEquals("three", attribute.getAttributeValue());
        }
        {
            final AbstractAttribute attribute = browserPage.getTagRepository().findOneAttributeByFilter(false, attributeNameValue -> "name".equals(attributeNameValue.name()) && "name1222".equals(attributeNameValue.value()));
            assertNull(attribute);
        }
        {
            final AbstractAttribute attribute = browserPage.getTagRepository().findOneAttributeByFilter(true, attributeNameValue -> "name".equals(attributeNameValue.name()) && "name1222".equals(attributeNameValue.value()));
            assertNull(attribute);
        }
        {
            final AbstractAttribute attribute = browserPage.getTagRepository().findOneAttributeByFilter(false, attributeNameValue -> "id".equals(attributeNameValue.name()) && "three".equals(attributeNameValue.value()));
            assertNotNull(attribute);
            assertEquals("id", attribute.getAttributeName());
            assertEquals("three", attribute.getAttributeValue());
        }
        {
            final AbstractAttribute attribute = browserPage.getTagRepository().findOneAttributeByFilter(true, attributeNameValue -> "id".equals(attributeNameValue.name()) && "three".equals(attributeNameValue.value()));
            assertNotNull(attribute);
            assertEquals("id", attribute.getAttributeName());
            assertEquals("three", attribute.getAttributeValue());
        }
    }

    @Test
    public void testFindAttributesByFilter() throws Exception {
        final Html html = new Html(null) {{
            new Div(this, new Id("one")) {{
                new Span(this, new Id("two")) {{
                    new H1(this, new Id("three"));
                    new H2(this, new Id("three"));
                    new NoTag(this, "something");
                }};

                new H3(this, new Name("name1"));
            }};
        }};
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
        {
            final Collection<AbstractAttribute> attributes = TagRepository.findAttributesByFilter(false, attributeNameValue -> "name".equals(attributeNameValue.name()) && "name1222".equals(attributeNameValue.value()), html);
            assertNotNull(attributes);
            assertEquals(0, attributes.size());
        }
        {
            final Collection<AbstractAttribute> attributes = TagRepository.findAttributesByFilter(true, attributeNameValue -> "name".equals(attributeNameValue.name()) && "name1222".equals(attributeNameValue.value()), html);
            assertNotNull(attributes);
            assertEquals(0, attributes.size());
        }
        {
            final Collection<AbstractAttribute> attributes = TagRepository.findAttributesByFilter(false, attributeNameValue -> "id".equals(attributeNameValue.name()) && "three".equals(attributeNameValue.value()), html);
            assertNotNull(attributes);
            for (AbstractAttribute attribute : attributes) {
                assertEquals("id", attribute.getAttributeName());
                assertEquals("three", attribute.getAttributeValue());
            }
        }
        {
            final Collection<AbstractAttribute> attributes = TagRepository.findAttributesByFilter(true, attributeNameValue -> "id".equals(attributeNameValue.name()) && "three".equals(attributeNameValue.value()), html);
            assertNotNull(attributes);
            for (AbstractAttribute attribute : attributes) {
                assertEquals("id", attribute.getAttributeName());
                assertEquals("three", attribute.getAttributeValue());
            }
        }
        {
            final Collection<AbstractAttribute> attributes = browserPage.getTagRepository().findAttributesByFilter(false, attributeNameValue -> "name".equals(attributeNameValue.name()) && "name1222".equals(attributeNameValue.value()));
            assertNotNull(attributes);
            assertEquals(0, attributes.size());
        }
        {
            final Collection<AbstractAttribute> attributes = browserPage.getTagRepository().findAttributesByFilter(true, attributeNameValue -> "name".equals(attributeNameValue.name()) && "name1222".equals(attributeNameValue.value()));
            assertNotNull(attributes);
            assertEquals(0, attributes.size());
        }
        {
            final Collection<AbstractAttribute> attributes = browserPage.getTagRepository().findAttributesByFilter(false, attributeNameValue -> "id".equals(attributeNameValue.name()) && "three".equals(attributeNameValue.value()));
            assertNotNull(attributes);
            for (AbstractAttribute attribute : attributes) {
                assertEquals("id", attribute.getAttributeName());
                assertEquals("three", attribute.getAttributeValue());
            }
        }
        {
            final Collection<AbstractAttribute> attributes = browserPage.getTagRepository().findAttributesByFilter(true, attributeNameValue -> "id".equals(attributeNameValue.name()) && "three".equals(attributeNameValue.value()));
            assertNotNull(attributes);
            for (AbstractAttribute attribute : attributes) {
                assertEquals("id", attribute.getAttributeName());
                assertEquals("three", attribute.getAttributeValue());
            }
        }
    }

}
