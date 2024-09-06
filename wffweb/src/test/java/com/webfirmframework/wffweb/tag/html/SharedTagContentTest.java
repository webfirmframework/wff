package com.webfirmframework.wffweb.tag.html;

import static org.junit.Assert.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;

import com.webfirmframework.wffweb.server.page.BrowserPage;
import com.webfirmframework.wffweb.tag.html.SharedTagContent.ChangeEvent;
import com.webfirmframework.wffweb.tag.html.SharedTagContent.Content;
import com.webfirmframework.wffweb.tag.html.SharedTagContent.ContentChangeListener;
import com.webfirmframework.wffweb.tag.html.SharedTagContent.ContentFormatter;
import com.webfirmframework.wffweb.tag.html.SharedTagContent.DetachEvent;
import com.webfirmframework.wffweb.tag.html.SharedTagContent.UpdateClientNature;
import com.webfirmframework.wffweb.tag.html.stylesandsemantics.Div;
import com.webfirmframework.wffweb.tag.html.stylesandsemantics.Span;
import com.webfirmframework.wffweb.tag.htmlwff.NoTag;

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
public class SharedTagContentTest {

    @Test
    public void testSharedTagContentString() {
//        fail("Not yet implemented");
        SharedTagContent<String> stc = new SharedTagContent<>("Test content");
        Div div = new Div(null);
        Span span = new Span(div);
        span.addInnerHtml(stc);
        
        assertEquals("<div><span>Test content</span></div>", div.toHtmlString());
        assertEquals(UpdateClientNature.ALLOW_ASYNC_PARALLEL, stc.getUpdateClientNature());
    }

    @Test
    public void testSharedTagContentUpdateClientNatureString() {
        {
            SharedTagContent<String> stc = new SharedTagContent<>(UpdateClientNature.ALLOW_ASYNC_PARALLEL, "Test content");
            Div div = new Div(null);
            Span span = new Span(div);
            span.addInnerHtml(stc);
            
            assertEquals("<div><span>Test content</span></div>", div.toHtmlString());
            assertEquals(UpdateClientNature.ALLOW_ASYNC_PARALLEL, stc.getUpdateClientNature());
        }
        {
            SharedTagContent<String> stc = new SharedTagContent<>(UpdateClientNature.ALLOW_PARALLEL, "Test content");
            Div div = new Div(null);
            Span span = new Span(div);
            span.addInnerHtml(stc);
            
            assertEquals("<div><span>Test content</span></div>", div.toHtmlString());
            assertEquals(UpdateClientNature.ALLOW_PARALLEL, stc.getUpdateClientNature());
        }
        {
            SharedTagContent<String> stc = new SharedTagContent<>(UpdateClientNature.SEQUENTIAL, "Test content");
            Div div = new Div(null);
            Span span = new Span(div);
            span.addInnerHtml(stc);
            
            assertEquals("<div><span>Test content</span></div>", div.toHtmlString());
            assertEquals(UpdateClientNature.SEQUENTIAL, stc.getUpdateClientNature());
        }
    }

    @Test
    public void testSharedTagContentStringBoolean() {
        {
            SharedTagContent<String> stc = new SharedTagContent<>("Test content", true);
            Div div = new Div(null);
            Span span = new Span(div);
            span.addInnerHtml(stc);
            
            assertEquals("<div><span>Test content</span></div>", div.toHtmlString());
            assertTrue(stc.isContentTypeHtml());
            assertEquals(UpdateClientNature.ALLOW_ASYNC_PARALLEL, stc.getUpdateClientNature());
        }
        {
            SharedTagContent<String> stc = new SharedTagContent<>("Test content", false);
            Div div = new Div(null);
            Span span = new Span(div);
            span.addInnerHtml(stc);
            
            assertEquals("<div><span>Test content</span></div>", div.toHtmlString());
            assertFalse(stc.isContentTypeHtml());
            assertEquals(UpdateClientNature.ALLOW_ASYNC_PARALLEL, stc.getUpdateClientNature());
        }
    }

    @Test
    public void testSharedTagContentUpdateClientNatureStringBoolean() {
        {
            SharedTagContent<String> stc = new SharedTagContent<>(UpdateClientNature.ALLOW_ASYNC_PARALLEL, "Test content", true);
            Div div = new Div(null);
            Span span = new Span(div);
            span.addInnerHtml(stc);
            
            assertEquals("<div><span>Test content</span></div>", div.toHtmlString());
            assertTrue(stc.isContentTypeHtml());
            assertEquals(UpdateClientNature.ALLOW_ASYNC_PARALLEL, stc.getUpdateClientNature());
        }
        {
            SharedTagContent<String> stc = new SharedTagContent<>(UpdateClientNature.ALLOW_ASYNC_PARALLEL, "Test content", false);
            Div div = new Div(null);
            Span span = new Span(div);
            span.addInnerHtml(stc);
            
            assertEquals("<div><span>Test content</span></div>", div.toHtmlString());
            assertFalse(stc.isContentTypeHtml());
            assertEquals(UpdateClientNature.ALLOW_ASYNC_PARALLEL, stc.getUpdateClientNature());
        }
        {
            SharedTagContent<String> stc = new SharedTagContent<>(UpdateClientNature.ALLOW_PARALLEL, "Test content", true);
            Div div = new Div(null);
            Span span = new Span(div);
            span.addInnerHtml(stc);
            
            assertEquals("<div><span>Test content</span></div>", div.toHtmlString());
            assertTrue(stc.isContentTypeHtml());
            assertEquals(UpdateClientNature.ALLOW_PARALLEL, stc.getUpdateClientNature());
        }
        {
            SharedTagContent<String> stc = new SharedTagContent<>(UpdateClientNature.ALLOW_PARALLEL, "Test content", false);
            Div div = new Div(null);
            Span span = new Span(div);
            span.addInnerHtml(stc);
            
            assertEquals("<div><span>Test content</span></div>", div.toHtmlString());
            assertFalse(stc.isContentTypeHtml());
            assertEquals(UpdateClientNature.ALLOW_PARALLEL, stc.getUpdateClientNature());
        }
        {
            SharedTagContent<String> stc = new SharedTagContent<>(UpdateClientNature.SEQUENTIAL, "Test content", true);
            Div div = new Div(null);
            Span span = new Span(div);
            span.addInnerHtml(stc);
            
            assertEquals("<div><span>Test content</span></div>", div.toHtmlString());
            assertTrue(stc.isContentTypeHtml());
            assertEquals(UpdateClientNature.SEQUENTIAL, stc.getUpdateClientNature());
        }
        {
            SharedTagContent<String> stc = new SharedTagContent<>(UpdateClientNature.SEQUENTIAL, "Test content", false);
            Div div = new Div(null);
            Span span = new Span(div);
            span.addInnerHtml(stc);
            
            assertEquals("<div><span>Test content</span></div>", div.toHtmlString());
            assertFalse(stc.isContentTypeHtml());
            assertEquals(UpdateClientNature.SEQUENTIAL, stc.getUpdateClientNature());
        }
    }

    @Test
    public void testGetContent() {
        {
            SharedTagContent<String> stc = new SharedTagContent<>(UpdateClientNature.ALLOW_ASYNC_PARALLEL, "Test content", true);
            Div div = new Div(null);
            Span span = new Span(div);
            span.addInnerHtml(stc);
            
            assertEquals("Test content", stc.getContent());
            assertEquals("<div><span>Test content</span></div>", div.toHtmlString());
            assertTrue(stc.isContentTypeHtml());
            assertEquals(UpdateClientNature.ALLOW_ASYNC_PARALLEL, stc.getUpdateClientNature());
        }
        {
            SharedTagContent<String> stc = new SharedTagContent<>(UpdateClientNature.ALLOW_ASYNC_PARALLEL, "Test content", true);
            Div div = new Div(null);
            Span span1 = new Span(div);
            span1.addInnerHtml(stc);
            Span span2 = new Span(div);
            span2.addInnerHtml(stc);
            
            
            assertEquals("Test content", stc.getContent());
            assertEquals("<div><span>Test content</span><span>Test content</span></div>", div.toHtmlString());
            stc.setContent("Content Changed");
            assertEquals("Content Changed", stc.getContent());
            assertEquals("<div><span>Content Changed</span><span>Content Changed</span></div>", div.toHtmlString());
            assertTrue(stc.isContentTypeHtml());
            assertEquals(UpdateClientNature.ALLOW_ASYNC_PARALLEL, stc.getUpdateClientNature());
        }
    }

    @Test
    public void testIsContentTypeHtml() {
        {
            SharedTagContent<String> stc = new SharedTagContent<>(UpdateClientNature.ALLOW_ASYNC_PARALLEL, "Test content", true);
            Div div = new Div(null);
            Span span1 = new Span(div);
            span1.addInnerHtml(stc);
            Span span2 = new Span(div);
            span2.addInnerHtml(stc);
            
            
            assertTrue(stc.isContentTypeHtml());          
            assertEquals("Test content", stc.getContent());
            assertEquals("<div><span>Test content</span><span>Test content</span></div>", div.toHtmlString());
            stc.setContent("Content Changed", false);
            assertFalse(stc.isContentTypeHtml());
            assertEquals("Content Changed", stc.getContent());
            assertEquals("<div><span>Content Changed</span><span>Content Changed</span></div>", div.toHtmlString());
            assertEquals(UpdateClientNature.ALLOW_ASYNC_PARALLEL, stc.getUpdateClientNature());
        }
    }

    @Test
    public void testGetUpdateClientNature() {
        SharedTagContent<String> stc = new SharedTagContent<>(UpdateClientNature.SEQUENTIAL, "Test content", true);
        Div div = new Div(null);
        Span span = new Span(div);
        span.addInnerHtml(stc);
        
        
        assertEquals(UpdateClientNature.SEQUENTIAL, stc.getUpdateClientNature());
        stc.setUpdateClientNature(UpdateClientNature.ALLOW_ASYNC_PARALLEL);
        assertEquals(UpdateClientNature.ALLOW_ASYNC_PARALLEL, stc.getUpdateClientNature());
        
        stc.setUpdateClientNature(UpdateClientNature.ALLOW_PARALLEL);
        assertEquals(UpdateClientNature.ALLOW_PARALLEL, stc.getUpdateClientNature());
        
        stc.setUpdateClientNature(UpdateClientNature.SEQUENTIAL);
        assertEquals(UpdateClientNature.SEQUENTIAL, stc.getUpdateClientNature());
    }

    @Test
    public void testSetUpdateClientNature() {
        SharedTagContent<String> stc = new SharedTagContent<>(UpdateClientNature.SEQUENTIAL, "Test content", true);
        Div div = new Div(null);
        Span span = new Span(div);
        span.addInnerHtml(stc);
        
        
        assertEquals(UpdateClientNature.SEQUENTIAL, stc.getUpdateClientNature());
        stc.setUpdateClientNature(UpdateClientNature.ALLOW_ASYNC_PARALLEL);
        assertEquals(UpdateClientNature.ALLOW_ASYNC_PARALLEL, stc.getUpdateClientNature());
        
        stc.setUpdateClientNature(UpdateClientNature.ALLOW_PARALLEL);
        assertEquals(UpdateClientNature.ALLOW_PARALLEL, stc.getUpdateClientNature());
        
        stc.setUpdateClientNature(UpdateClientNature.SEQUENTIAL);
        assertEquals(UpdateClientNature.SEQUENTIAL, stc.getUpdateClientNature());
    }

    @Test
    public void testIsUpdateClient() {
        SharedTagContent<String> stc = new SharedTagContent<>(UpdateClientNature.SEQUENTIAL, "Test content", true);
        Div div = new Div(null);
        Span span = new Span(div);
        span.addInnerHtml(stc);
        
        assertTrue(stc.isUpdateClient());
        
        stc.setUpdateClient(false);
        assertFalse(stc.isUpdateClient());
        
        stc.setUpdateClient(true);
        assertTrue(stc.isUpdateClient());
    }

    @Test
    public void testSetUpdateClient() {
        SharedTagContent<String> stc = new SharedTagContent<>("Test content", true);
        Div div = new Div(null);
        Span span = new Span(div);
        span.addInnerHtml(stc);
        
        assertTrue(stc.isUpdateClient());
        
        stc.setUpdateClient(false);
        assertFalse(stc.isUpdateClient());
        
        stc.setUpdateClient(true);
        assertTrue(stc.isUpdateClient());
    }

    @Test
    public void testContains() {
        SharedTagContent<String> stc = new SharedTagContent<>("Test content", true);
        Div div = new Div(null);
        Span span = new Span(div);
        span.addInnerHtml(stc);
        
        assertTrue(stc.contains(span.getFirstChild()));
        assertFalse(stc.contains(div.getFirstChild()));
    }

    @Test
    public void testSetContentString() {
        
        {
            SharedTagContent<String> stc = new SharedTagContent<>(UpdateClientNature.ALLOW_ASYNC_PARALLEL, "Test content", true);
            Div div = new Div(null);
            Span span1 = new Span(div);
            span1.addInnerHtml(stc);
            Span span2 = new Span(div);
            span2.addInnerHtml(stc);
            
            
            assertEquals("Test content", stc.getContent());
            assertEquals("<div><span>Test content</span><span>Test content</span></div>", div.toHtmlString());
            stc.setContent("Content Changed");
            assertEquals("Content Changed", stc.getContent());
            assertEquals("<div><span>Content Changed</span><span>Content Changed</span></div>", div.toHtmlString());
            assertTrue(stc.isContentTypeHtml());
            assertEquals(UpdateClientNature.ALLOW_ASYNC_PARALLEL, stc.getUpdateClientNature());
        }
    }

    @Test
    public void testSetContentUpdateClientNatureString() {
        
        {
            SharedTagContent<String> stc = new SharedTagContent<>(UpdateClientNature.SEQUENTIAL, "Test content", true);
            Div div = new Div(null);
            Span span1 = new Span(div);
            span1.addInnerHtml(stc);
            Span span2 = new Span(div);
            span2.addInnerHtml(stc);
            
            assertEquals(UpdateClientNature.SEQUENTIAL, stc.getUpdateClientNature());
            
            assertEquals("Test content", stc.getContent());
            assertEquals("<div><span>Test content</span><span>Test content</span></div>", div.toHtmlString());
            
            stc.setContent(UpdateClientNature.ALLOW_PARALLEL, "Content Changed");
            assertEquals(UpdateClientNature.ALLOW_PARALLEL, stc.getUpdateClientNature());
            
            assertEquals("Content Changed", stc.getContent());
            assertEquals("<div><span>Content Changed</span><span>Content Changed</span></div>", div.toHtmlString());
            assertTrue(stc.isContentTypeHtml());
            
        }
    }

    @Test
    public void testSetContentStringBoolean() {
        {
            SharedTagContent<String> stc = new SharedTagContent<>(UpdateClientNature.SEQUENTIAL, "Test content", true);
            Div div = new Div(null);
            Span span1 = new Span(div);
            span1.addInnerHtml(stc);
            Span span2 = new Span(div);
            span2.addInnerHtml(stc);
            
            assertTrue(stc.isContentTypeHtml());
            
            assertEquals("Test content", stc.getContent());
            assertEquals("<div><span>Test content</span><span>Test content</span></div>", div.toHtmlString());
            
            stc.setContent("Content Changed", false);
            assertFalse(stc.isContentTypeHtml());
            
            assertEquals("Content Changed", stc.getContent());
            assertEquals("<div><span>Content Changed</span><span>Content Changed</span></div>", div.toHtmlString());
            
            stc.setContent("Content Changed", true);
            assertTrue(stc.isContentTypeHtml());
            
        }
    }

    @Test
    public void testSetContentUpdateClientNatureStringBoolean() {
        
        {
            SharedTagContent<String> stc = new SharedTagContent<>(UpdateClientNature.SEQUENTIAL, "Test content", true);
            Div div = new Div(null);
            Span span1 = new Span(div);
            span1.addInnerHtml(stc);
            Span span2 = new Span(div);
            span2.addInnerHtml(stc);
            
            assertTrue(stc.isContentTypeHtml());
            
            assertEquals("Test content", stc.getContent());
            assertEquals("<div><span>Test content</span><span>Test content</span></div>", div.toHtmlString());
            
            stc.setContent(UpdateClientNature.ALLOW_PARALLEL, "Content Changed", false);
            assertFalse(stc.isContentTypeHtml());
            assertEquals(UpdateClientNature.ALLOW_PARALLEL, stc.getUpdateClientNature());
            
            assertEquals("Content Changed", stc.getContent());
            assertEquals("<div><span>Content Changed</span><span>Content Changed</span></div>", div.toHtmlString());
            
            stc.setContent(UpdateClientNature.ALLOW_ASYNC_PARALLEL, "Content Changed", true);
            assertTrue(stc.isContentTypeHtml());
            assertEquals(UpdateClientNature.ALLOW_ASYNC_PARALLEL, stc.getUpdateClientNature());
            
        }
    }

    @Test
    public void testSetContentSetOfAbstractHtmlString() {
        
        {
            SharedTagContent<String> stc = new SharedTagContent<>(UpdateClientNature.SEQUENTIAL, "Test content", true);
            Div div = new Div(null);
            Span spanChild1 = new Span(div);
            spanChild1.addInnerHtml(stc);
            P pChild2 = new P(div);
            pChild2.addInnerHtml(stc);
            
            assertTrue(stc.isContentTypeHtml());
            
            assertEquals("Test content", stc.getContent());
            assertEquals("<div><span>Test content</span><p>Test content</p></div>", div.toHtmlString());
            
            //div.toHtmlString will contain all tags updated even after this call because it is ignoring only for update client push
            stc.setContent(new HashSet<>(Arrays.asList(spanChild1)), "Content Changed");
            
            assertTrue(stc.isContentTypeHtml());
            assertEquals(UpdateClientNature.SEQUENTIAL, stc.getUpdateClientNature());
            
            assertEquals("Content Changed", stc.getContent());
            
           
            assertEquals("<div><span>Content Changed</span><p>Content Changed</p></div>", div.toHtmlString());
            
            stc.setContent(UpdateClientNature.ALLOW_ASYNC_PARALLEL, "Content Changed", true);
            assertTrue(stc.isContentTypeHtml());
            assertEquals(UpdateClientNature.ALLOW_ASYNC_PARALLEL, stc.getUpdateClientNature());
            
        }
        
    }

    @Test
    public void testSetContentSetOfAbstractHtmlStringBoolean() {

        {
            SharedTagContent<String> stc = new SharedTagContent<>(UpdateClientNature.SEQUENTIAL, "Test content", true);
            Div div = new Div(null);
            Span spanChild1 = new Span(div);
            spanChild1.addInnerHtml(stc);
            P pChild2 = new P(div);
            pChild2.addInnerHtml(stc);
            
            assertTrue(stc.isContentTypeHtml());
            
            assertEquals("Test content", stc.getContent());
            assertEquals("<div><span>Test content</span><p>Test content</p></div>", div.toHtmlString());
            
            //div.toHtmlString will contain all tags updated even after this call because it is ignoring only for update client push
            stc.setContent(new HashSet<>(Arrays.asList(spanChild1)), "Content Changed", false);
            
            assertFalse(stc.isContentTypeHtml());
            assertEquals(UpdateClientNature.SEQUENTIAL, stc.getUpdateClientNature());
            
            assertEquals("Content Changed", stc.getContent());
            
           
            assertEquals("<div><span>Content Changed</span><p>Content Changed</p></div>", div.toHtmlString());
            
            stc.setContent(new HashSet<>(Arrays.asList(spanChild1)), "Content Changed", true);
            assertTrue(stc.isContentTypeHtml());
            assertEquals(UpdateClientNature.SEQUENTIAL, stc.getUpdateClientNature());
            
        }
    }

    @Test
    public void testAddInnerHtml() {
        {
            SharedTagContent<String> stc = new SharedTagContent<>(UpdateClientNature.SEQUENTIAL, "Test content", true);
            Div div = new Div(null);
            Span spanChild1 = new Span(div);
            spanChild1.addInnerHtml(stc);
            P pChild2 = new P(div);
            pChild2.addInnerHtml(stc);
            
            assertTrue(stc.isContentTypeHtml());
            
            assertEquals("Test content", stc.getContent());
            assertEquals("<div><span>Test content</span><p>Test content</p></div>", div.toHtmlString());
            
            Div div2 = new Div(null);
            div2.addInnerHtml(stc);
            assertEquals("<div>Test content</div>", div2.toHtmlString());
            
            //div.toHtmlString will contain all tags updated even after this call because it is ignoring only for update client push
            stc.setContent(new HashSet<>(Arrays.asList(spanChild1)), "Content Changed", false);
            
            assertFalse(stc.isContentTypeHtml());
            assertEquals(UpdateClientNature.SEQUENTIAL, stc.getUpdateClientNature());
            
            assertEquals("Content Changed", stc.getContent());
            
           
            assertEquals("<div><span>Content Changed</span><p>Content Changed</p></div>", div.toHtmlString());
            assertEquals("<div>Content Changed</div>", div2.toHtmlString());
            
            stc.setContent(new HashSet<>(Arrays.asList(spanChild1)), "Content Changed", true);
            assertTrue(stc.isContentTypeHtml());
            assertEquals(UpdateClientNature.SEQUENTIAL, stc.getUpdateClientNature());
            
        }
    }

    @Test
    public void testDetachSetOfAbstractHtml() {
        
        {
            SharedTagContent<String> stc = new SharedTagContent<>(UpdateClientNature.SEQUENTIAL, "Test content", true);
            Div div = new Div(null);
            Span spanChild1 = new Span(div);
            spanChild1.addInnerHtml(stc);
            P pChild2 = new P(div);
            pChild2.addInnerHtml(stc);
            
            assertTrue(stc.isContentTypeHtml());
            
            assertEquals("Test content", stc.getContent());
            assertEquals("<div><span>Test content</span><p>Test content</p></div>", div.toHtmlString());
            
            Div div2 = new Div(null);
            div2.addInnerHtml(stc);
            assertEquals("<div>Test content</div>", div2.toHtmlString());
            
            stc.detach(new HashSet<>(Arrays.asList(div2)));
            
            stc.setContent("Content Changed", false);
            
            assertFalse(stc.isContentTypeHtml());
            assertEquals(UpdateClientNature.SEQUENTIAL, stc.getUpdateClientNature());
            
            assertEquals("Content Changed", stc.getContent());
            
           
            assertEquals("<div><span>Test content</span><p>Test content</p></div>", div.toHtmlString());
            assertEquals("<div>Content Changed</div>", div2.toHtmlString());
            
            stc.setContent(new HashSet<>(Arrays.asList(spanChild1)), "Content Changed", true);
            assertTrue(stc.isContentTypeHtml());
            assertEquals(UpdateClientNature.SEQUENTIAL, stc.getUpdateClientNature());
            
        }

    }

    @Test
    public void testDetachBoolean() {

        {
            SharedTagContent<String> stc = new SharedTagContent<>(UpdateClientNature.SEQUENTIAL, "Test content", true);
            Div div = new Div(null);
            Span spanChild1 = new Span(div);
            spanChild1.addInnerHtml(stc);
            P pChild2 = new P(div);
            pChild2.addInnerHtml(stc);
            
            assertTrue(stc.isContentTypeHtml());
            
            assertEquals("Test content", stc.getContent());
            assertEquals("<div><span>Test content</span><p>Test content</p></div>", div.toHtmlString());
            
            Div div2 = new Div(null);
            div2.addInnerHtml(stc);
            assertEquals("<div>Test content</div>", div2.toHtmlString());
            
            stc.detach(false);
            
            stc.setContent("Content Changed", false);
            
            assertFalse(stc.isContentTypeHtml());
            assertEquals(UpdateClientNature.SEQUENTIAL, stc.getUpdateClientNature());
            
            assertEquals("Content Changed", stc.getContent());
            
           
            assertEquals("<div><span>Test content</span><p>Test content</p></div>", div.toHtmlString());
            assertEquals("<div>Test content</div>", div2.toHtmlString());
            
            stc.setContent(new HashSet<>(Arrays.asList(spanChild1)), "Content Changed", true);
            assertTrue(stc.isContentTypeHtml());
            assertEquals(UpdateClientNature.SEQUENTIAL, stc.getUpdateClientNature());
            
        }
        
        {
            SharedTagContent<String> stc = new SharedTagContent<>(UpdateClientNature.SEQUENTIAL, "Test content", true);
            Div div = new Div(null);
            Span spanChild1 = new Span(div);
            spanChild1.addInnerHtml(stc);
            P pChild2 = new P(div);
            pChild2.addInnerHtml(stc);
            
            assertTrue(stc.isContentTypeHtml());
            
            assertEquals("Test content", stc.getContent());
            assertEquals("<div><span>Test content</span><p>Test content</p></div>", div.toHtmlString());
            
            Div div2 = new Div(null);
            div2.addInnerHtml(stc);
            assertEquals("<div>Test content</div>", div2.toHtmlString());
            
            stc.detach(true);
            
            stc.setContent("Content Changed", false);
            
            assertFalse(stc.isContentTypeHtml());
            assertEquals(UpdateClientNature.SEQUENTIAL, stc.getUpdateClientNature());
            
            assertEquals("Content Changed", stc.getContent());
            
           
            assertEquals("<div><span></span><p></p></div>", div.toHtmlString());
            assertEquals("<div></div>", div2.toHtmlString());
            
            stc.setContent(new HashSet<>(Arrays.asList(spanChild1)), "Content Changed", true);
            assertTrue(stc.isContentTypeHtml());
            assertEquals(UpdateClientNature.SEQUENTIAL, stc.getUpdateClientNature());
            
        }
        
    }

    @Test
    public void testDetachBooleanSetOfAbstractHtml() {

        {
            SharedTagContent<String> stc = new SharedTagContent<>(UpdateClientNature.SEQUENTIAL, "Test content", true);
            Div div = new Div(null);
            Span spanChild1 = new Span(div);
            spanChild1.addInnerHtml(stc);
            P pChild2 = new P(div);
            pChild2.addInnerHtml(stc);
            
            assertTrue(stc.isContentTypeHtml());
            
            assertEquals("Test content", stc.getContent());
            assertEquals("<div><span>Test content</span><p>Test content</p></div>", div.toHtmlString());
            
            Div div2 = new Div(null);
            div2.addInnerHtml(stc);
            assertEquals("<div>Test content</div>", div2.toHtmlString());
            
            stc.detach(false, new HashSet<>(Arrays.asList(div2)));
            
            stc.setContent("Content Changed", false);
            
            assertFalse(stc.isContentTypeHtml());
            assertEquals(UpdateClientNature.SEQUENTIAL, stc.getUpdateClientNature());
            
            assertEquals("Content Changed", stc.getContent());
            
           
            assertEquals("<div><span>Test content</span><p>Test content</p></div>", div.toHtmlString());
            assertEquals("<div>Content Changed</div>", div2.toHtmlString());
            
            stc.setContent(new HashSet<>(Arrays.asList(spanChild1)), "Content Changed", true);
            assertTrue(stc.isContentTypeHtml());
            assertEquals(UpdateClientNature.SEQUENTIAL, stc.getUpdateClientNature());
            
        }
        {
            SharedTagContent<String> stc = new SharedTagContent<>(UpdateClientNature.SEQUENTIAL, "Test content", true);
            Div div = new Div(null);
            Span spanChild1 = new Span(div);
            spanChild1.addInnerHtml(stc);
            P pChild2 = new P(div);
            pChild2.addInnerHtml(stc);
            
            assertTrue(stc.isContentTypeHtml());
            
            assertEquals("Test content", stc.getContent());
            assertEquals("<div><span>Test content</span><p>Test content</p></div>", div.toHtmlString());
            
            Div div2 = new Div(null);
            div2.addInnerHtml(stc);
            assertEquals("<div>Test content</div>", div2.toHtmlString());
            
            stc.detach(true, new HashSet<>(Arrays.asList(div2)));
            
            stc.setContent("Content Changed", false);
            
            assertFalse(stc.isContentTypeHtml());
            assertEquals(UpdateClientNature.SEQUENTIAL, stc.getUpdateClientNature());
            
            assertEquals("Content Changed", stc.getContent());
            
           
            assertEquals("<div><span></span><p></p></div>", div.toHtmlString());
            assertEquals("<div>Content Changed</div>", div2.toHtmlString());
            
            stc.setContent(new HashSet<>(Arrays.asList(spanChild1)), "Content Changed", true);
            assertTrue(stc.isContentTypeHtml());
            assertEquals(UpdateClientNature.SEQUENTIAL, stc.getUpdateClientNature());
            
        }
    }

    @Test
    public void testDetachBooleanSetOfAbstractHtmlSetOfAbstractHtml() {
        {
            SharedTagContent<String> stc = new SharedTagContent<>(UpdateClientNature.SEQUENTIAL, "Test content", true);
            Div div = new Div(null);
            Span spanChild1 = new Span(div);
            spanChild1.addInnerHtml(stc);
            P pChild2 = new P(div);
            pChild2.addInnerHtml(stc);
            
            assertTrue(stc.isContentTypeHtml());
            
            assertEquals("Test content", stc.getContent());
            assertEquals("<div><span>Test content</span><p>Test content</p></div>", div.toHtmlString());
            
            Div div2 = new Div(null);
            div2.addInnerHtml(stc);
            assertEquals("<div>Test content</div>", div2.toHtmlString());
            
            //third argument has no effect here as there is no BrowserPage instance and websocket connection
            stc.detach(false, new HashSet<>(Arrays.asList(div2)), new HashSet<>(Arrays.asList(div)));
            
            stc.setContent("Content Changed", false);
            
            assertFalse(stc.isContentTypeHtml());
            assertEquals(UpdateClientNature.SEQUENTIAL, stc.getUpdateClientNature());
            
            assertEquals("Content Changed", stc.getContent());
            
           
            assertEquals("<div><span>Test content</span><p>Test content</p></div>", div.toHtmlString());
            assertEquals("<div>Content Changed</div>", div2.toHtmlString());
            
            stc.setContent(new HashSet<>(Arrays.asList(spanChild1)), "Content Changed", true);
            assertTrue(stc.isContentTypeHtml());
            assertEquals(UpdateClientNature.SEQUENTIAL, stc.getUpdateClientNature());
            
        }
        {
            SharedTagContent<String> stc = new SharedTagContent<>(UpdateClientNature.SEQUENTIAL, "Test content", true);
            Div div = new Div(null);
            Span spanChild1 = new Span(div);
            spanChild1.addInnerHtml(stc);
            P pChild2 = new P(div);
            pChild2.addInnerHtml(stc);
            
            assertTrue(stc.isContentTypeHtml());
            
            assertEquals("Test content", stc.getContent());
            assertEquals("<div><span>Test content</span><p>Test content</p></div>", div.toHtmlString());
            
            Div div2 = new Div(null);
            div2.addInnerHtml(stc);
            assertEquals("<div>Test content</div>", div2.toHtmlString());
            
            //third argument has no effect here as there is no BrowserPage instance and websocket connection
            stc.detach(true, new HashSet<>(Arrays.asList(div2)), new HashSet<>(Arrays.asList(div)));
            
            stc.setContent("Content Changed", false);
            
            assertFalse(stc.isContentTypeHtml());
            assertEquals(UpdateClientNature.SEQUENTIAL, stc.getUpdateClientNature());
            
            assertEquals("Content Changed", stc.getContent());
            
           
            assertEquals("<div><span></span><p></p></div>", div.toHtmlString());
            assertEquals("<div>Content Changed</div>", div2.toHtmlString());
            
            stc.setContent(new HashSet<>(Arrays.asList(spanChild1)), "Content Changed", true);
            assertTrue(stc.isContentTypeHtml());
            assertEquals(UpdateClientNature.SEQUENTIAL, stc.getUpdateClientNature());
            
        }
    }

    @Test
    public void testAddContentChangeListener() {

        {
            SharedTagContent<String> stc = new SharedTagContent<>(UpdateClientNature.ALLOW_PARALLEL, "Test content", true);
            Div div = new Div(null);
            Span span1 = new Span(div);
            span1.addInnerHtml(stc);
            Span span2 = new Span(div);
            span2.addInnerHtml(stc);
            
            AtomicBoolean listenerInvoked = new AtomicBoolean();
            stc.addContentChangeListener(span2, new SharedTagContent.ContentChangeListener<String>() {
                
                @Override
                public Runnable contentChanged(ChangeEvent<String> changeEvent) {
                    listenerInvoked.set(true);
                    assertEquals("Test content", changeEvent.contentBefore().content());
                    assertEquals("Content Changed", changeEvent.contentAfter().content());
                    CompletableFuture.runAsync(() -> {assertEquals("Content Changed", stc.getContent());});
                    return () -> { assertEquals("Content Changed", stc.getContent());};
                }
            });
            
            
            assertEquals("Test content", stc.getContent());
            assertEquals("<div><span>Test content</span><span>Test content</span></div>", div.toHtmlString());
            stc.setContent("Content Changed");
            assertEquals("Content Changed", stc.getContent());
            assertEquals("<div><span>Content Changed</span><span>Content Changed</span></div>", div.toHtmlString());
            assertTrue(stc.isContentTypeHtml());
            assertEquals(UpdateClientNature.ALLOW_PARALLEL, stc.getUpdateClientNature());
            
            assertTrue(listenerInvoked.get());
        }
    }

    @Test
    public void testAddDetachListener() {
        
        
        {
            SharedTagContent<String> stc = new SharedTagContent<>(UpdateClientNature.SEQUENTIAL, "Test content", true);
            Div div = new Div(null);
            Span spanChild1 = new Span(div);
            spanChild1.addInnerHtml(stc);
            P pChild2 = new P(div);
            pChild2.addInnerHtml(stc);
            
            AtomicBoolean listenerInvoked = new AtomicBoolean();
            stc.addDetachListener(pChild2, new SharedTagContent.DetachListener<String>() {
                
                @Override
                public Runnable detached(DetachEvent<String> detachEvent) {
                    listenerInvoked.set(true);
                    assertEquals("Test content", detachEvent.content().content());
                    
                    return () -> { assertEquals("Test content", stc.getContent());};
                }
            });
            
            
            assertTrue(stc.isContentTypeHtml());
            
            assertEquals("Test content", stc.getContent());
            assertEquals("<div><span>Test content</span><p>Test content</p></div>", div.toHtmlString());
            
            Div div2 = new Div(null);
            div2.addInnerHtml(stc);
            assertEquals("<div>Test content</div>", div2.toHtmlString());
            
            stc.detach(false);
            
            stc.setContent("Content Changed", false);
            
            assertFalse(stc.isContentTypeHtml());
            assertEquals(UpdateClientNature.SEQUENTIAL, stc.getUpdateClientNature());
            
            assertEquals("Content Changed", stc.getContent());
            
           
            assertEquals("<div><span>Test content</span><p>Test content</p></div>", div.toHtmlString());
            assertEquals("<div>Test content</div>", div2.toHtmlString());
            
            stc.setContent(new HashSet<>(Arrays.asList(spanChild1)), "Content Changed", true);
            assertTrue(stc.isContentTypeHtml());
            assertEquals(UpdateClientNature.SEQUENTIAL, stc.getUpdateClientNature());
            
        }
        
        {
            SharedTagContent<String> stc = new SharedTagContent<>(UpdateClientNature.SEQUENTIAL, "Test content", true);
            Div div = new Div(null);
            Span spanChild1 = new Span(div);
            spanChild1.addInnerHtml(stc);
            P pChild2 = new P(div);
            pChild2.addInnerHtml(stc);
            
            AtomicBoolean listenerInvoked = new AtomicBoolean();
            stc.addDetachListener(pChild2, new SharedTagContent.DetachListener<String>() {
                
                @Override
                public Runnable detached(DetachEvent<String> detachEvent) {
                    listenerInvoked.set(true);
                    assertEquals("Test content", detachEvent.content().content());
                    
                    return () -> { assertEquals("Test content", stc.getContent());};
                }
            });
            
            assertTrue(stc.isContentTypeHtml());
            
            assertEquals("Test content", stc.getContent());
            assertEquals("<div><span>Test content</span><p>Test content</p></div>", div.toHtmlString());
            
            Div div2 = new Div(null);
            div2.addInnerHtml(stc);
            assertEquals("<div>Test content</div>", div2.toHtmlString());
            
            stc.detach(true);
            
            stc.setContent("Content Changed", false);
            
            assertFalse(stc.isContentTypeHtml());
            assertEquals(UpdateClientNature.SEQUENTIAL, stc.getUpdateClientNature());
            
            assertEquals("Content Changed", stc.getContent());
            
           
            assertEquals("<div><span></span><p></p></div>", div.toHtmlString());
            assertEquals("<div></div>", div2.toHtmlString());
            
            stc.setContent(new HashSet<>(Arrays.asList(spanChild1)), "Content Changed", true);
            assertTrue(stc.isContentTypeHtml());
            assertEquals(UpdateClientNature.SEQUENTIAL, stc.getUpdateClientNature());
            
        }
    }

    @Test
    public void testRemoveContentChangeListenerContentChangeListener() {

        SharedTagContent<String> stc = new SharedTagContent<>(UpdateClientNature.ALLOW_PARALLEL, "Test content", true);
        Div div = new Div(null);
        Span span1 = new Span(div);
        span1.addInnerHtml(stc);
        Span span2 = new Span(div);
        span2.addInnerHtml(stc);
        
        AtomicBoolean listenerInvoked = new AtomicBoolean();
        final SharedTagContent.ContentChangeListener<String> contentChangeListener = new SharedTagContent.ContentChangeListener<String>() {
            
            @Override
            public Runnable contentChanged(ChangeEvent<String> changeEvent) {
                listenerInvoked.set(true);
                return null;
            }
        };
        stc.addContentChangeListener(span2, contentChangeListener);
        
        stc.removeContentChangeListener(contentChangeListener);
        stc.setContent("Changed Content");
        
        assertFalse(listenerInvoked.get());
    }

    @Test
    public void testRemoveContentChangeListenerAbstractHtmlContentChangeListener() {
        SharedTagContent<String> stc = new SharedTagContent<>(UpdateClientNature.ALLOW_PARALLEL, "Test content", true);
        Div div = new Div(null);
        Span span1 = new Span(div);
        span1.addInnerHtml(stc);
        Span span2 = new Span(div);
        span2.addInnerHtml(stc);
        
        AtomicBoolean listenerInvoked = new AtomicBoolean();
        final SharedTagContent.ContentChangeListener<String> contentChangeListener = new SharedTagContent.ContentChangeListener<String>() {
            
            @Override
            public Runnable contentChanged(ChangeEvent<String> changeEvent) {
                listenerInvoked.set(true);
                return null;
            }
        };
        stc.addContentChangeListener(span2, contentChangeListener);
        
        stc.removeContentChangeListener(span2, contentChangeListener);
        stc.setContent("Changed Content");
        
        assertFalse(listenerInvoked.get());
    }

    @Test
    public void testRemoveDetachListenerDetachListener() {

        SharedTagContent<String> stc = new SharedTagContent<>(UpdateClientNature.ALLOW_PARALLEL, "Test content", true);
        Div div = new Div(null);
        Span span1 = new Span(div);
        span1.addInnerHtml(stc);
        Span span2 = new Span(div);
        span2.addInnerHtml(stc);
        
        AtomicBoolean listenerInvoked = new AtomicBoolean();
        final SharedTagContent.DetachListener<String> detachListener = new SharedTagContent.DetachListener<String>() {
            
            @Override
            public Runnable detached(DetachEvent<String> detachEvent) {
                listenerInvoked.set(true);
                return null;
            }
        };
        stc.addDetachListener(span2, detachListener);
        
        stc.removeDetachListener(detachListener);
        stc.detach(true);
        
        assertFalse(listenerInvoked.get());
    }

    @Test
    public void testRemoveDetachListenerAbstractHtmlDetachListener() {

        SharedTagContent<String> stc = new SharedTagContent<>(UpdateClientNature.ALLOW_PARALLEL, "Test content", true);
        Div div = new Div(null);
        Span span1 = new Span(div);
        span1.addInnerHtml(stc);
        Span span2 = new Span(div);
        span2.addInnerHtml(stc);
        
        AtomicBoolean listenerInvoked = new AtomicBoolean();
        final SharedTagContent.DetachListener<String> detachListener = new SharedTagContent.DetachListener<String>() {
            
            @Override
            public Runnable detached(DetachEvent<String> detachEvent) {
                listenerInvoked.set(true);
                return null;
            }
        };
        stc.addDetachListener(span2, detachListener);
        
        stc.removeDetachListener(span2, detachListener);
        stc.detach(true);
        
        assertFalse(listenerInvoked.get());
    }

    @Test
    public void testRemoveContentChangeListeners() {
        SharedTagContent<String> stc = new SharedTagContent<>(UpdateClientNature.ALLOW_PARALLEL, "Test content", true);
        Div div = new Div(null);
        Span span1 = new Span(div);
        span1.addInnerHtml(stc);
        Span span2 = new Span(div);
        span2.addInnerHtml(stc);
        
        AtomicBoolean listenerInvoked = new AtomicBoolean();
        final SharedTagContent.ContentChangeListener<String> contentChangeListener = new SharedTagContent.ContentChangeListener<String>() {
            
            @Override
            public Runnable contentChanged(ChangeEvent<String> changeEvent) {
                listenerInvoked.set(true);
                return null;
            }
        };
        stc.addContentChangeListener(span2, contentChangeListener);
        
        stc.removeContentChangeListeners(span2, Arrays.asList(contentChangeListener));
        stc.setContent("Changed Content");
        
        assertFalse(listenerInvoked.get());
    }

    @Test
    public void testRemoveDetachListeners() {
        SharedTagContent<String> stc = new SharedTagContent<>(UpdateClientNature.ALLOW_PARALLEL, "Test content", true);
        Div div = new Div(null);
        Span span1 = new Span(div);
        span1.addInnerHtml(stc);
        Span span2 = new Span(div);
        span2.addInnerHtml(stc);
        
        AtomicBoolean listenerInvoked = new AtomicBoolean();
        final SharedTagContent.DetachListener<String> detachListener = new SharedTagContent.DetachListener<String>() {
            
            @Override
            public Runnable detached(DetachEvent<String> detachEvent) {
                listenerInvoked.set(true);
                return null;
            }
        };
        stc.addDetachListener(span2, detachListener);
        
        stc.removeDetachListeners(span2, Arrays.asList(detachListener));
        stc.detach(true);
        
        assertFalse(listenerInvoked.get());
    }

    @Test
    public void testRemoveAllContentChangeListenersAbstractHtml() {

        SharedTagContent<String> stc = new SharedTagContent<>(UpdateClientNature.ALLOW_PARALLEL, "Test content", true);
        Div div = new Div(null);
        Span span1 = new Span(div);
        span1.addInnerHtml(stc);
        Span span2 = new Span(div);
        span2.addInnerHtml(stc);
        
        AtomicBoolean listenerInvoked = new AtomicBoolean();
        final SharedTagContent.ContentChangeListener<String> contentChangeListener = new SharedTagContent.ContentChangeListener<String>() {
            
            @Override
            public Runnable contentChanged(ChangeEvent<String> changeEvent) {
                listenerInvoked.set(true);
                return null;
            }
        };
        stc.addContentChangeListener(span2, contentChangeListener);
        
        stc.removeAllContentChangeListeners(span2);
        stc.setContent("Changed Content");
        
        assertFalse(listenerInvoked.get());
    }

    @Test
    public void testRemoveAllContentChangeListeners() {
        SharedTagContent<String> stc = new SharedTagContent<>(UpdateClientNature.ALLOW_PARALLEL, "Test content", true);
        Div div = new Div(null);
        Span span1 = new Span(div);
        span1.addInnerHtml(stc);
        Span span2 = new Span(div);
        span2.addInnerHtml(stc);
        
        AtomicBoolean listenerInvoked = new AtomicBoolean();
        final SharedTagContent.ContentChangeListener<String> contentChangeListener = new SharedTagContent.ContentChangeListener<String>() {
            
            @Override
            public Runnable contentChanged(ChangeEvent<String> changeEvent) {
                listenerInvoked.set(true);
                return null;
            }
        };
        stc.addContentChangeListener(span2, contentChangeListener);
        
        stc.removeAllContentChangeListeners();
        stc.setContent("Changed Content");
        
        assertFalse(listenerInvoked.get());
    }

    @Test
    public void testRemoveAllDetachListenersAbstractHtml() {
        SharedTagContent<String> stc = new SharedTagContent<>(UpdateClientNature.ALLOW_PARALLEL, "Test content", true);
        Div div = new Div(null);
        Span span1 = new Span(div);
        span1.addInnerHtml(stc);
        Span span2 = new Span(div);
        span2.addInnerHtml(stc);
        
        AtomicBoolean listenerInvoked = new AtomicBoolean();
        final SharedTagContent.DetachListener<String> detachListener = new SharedTagContent.DetachListener<String>() {
            
            @Override
            public Runnable detached(DetachEvent<String> detachEvent) {
                listenerInvoked.set(true);
                return null;
            }
        };
        stc.addDetachListener(span2, detachListener);
        
        stc.removeAllDetachListeners(span2);
        stc.detach(true);
        
        assertFalse(listenerInvoked.get());
    }

    @Test
    public void testRemoveAllDetachListeners() {

        SharedTagContent<String> stc = new SharedTagContent<>(UpdateClientNature.ALLOW_PARALLEL, "Test content", true);
        Div div = new Div(null);
        Span span1 = new Span(div);
        span1.addInnerHtml(stc);
        Span span2 = new Span(div);
        span2.addInnerHtml(stc);
        
        AtomicBoolean listenerInvoked = new AtomicBoolean();
        final SharedTagContent.DetachListener<String> detachListener = new SharedTagContent.DetachListener<String>() {
            
            @Override
            public Runnable detached(DetachEvent<String> detachEvent) {
                listenerInvoked.set(true);
                return null;
            }
        };
        stc.addDetachListener(span2, detachListener);
        
        stc.removeAllDetachListeners(span2);
        stc.detach(true);
        
        assertFalse(listenerInvoked.get());
    }
    
    @Test
    public void testAbstractHtmlGetSharedTagContent() throws Exception {
        SharedTagContent<String> stc = new SharedTagContent<>(UpdateClientNature.SEQUENTIAL, "Test Content", true);
        Div div = new Div(null);
        Span spanChild1 = new Span(div);
        spanChild1.addInnerHtml(stc);
        P pChild2 = new P(div);
        pChild2.addInnerHtml(stc);
        
        assertEquals(stc, spanChild1.getSharedTagContent());
        assertEquals(stc, pChild2.getSharedTagContent());
        
        spanChild1.removeAllChildren();
        pChild2.removeAllChildren();
        assertNull(spanChild1.getSharedTagContent());
        assertNull(pChild2.getSharedTagContent());
        
        spanChild1.addInnerHtml(stc);
        pChild2.addInnerHtml(stc);
        assertEquals(stc, spanChild1.getSharedTagContent());
        assertEquals(stc, pChild2.getSharedTagContent());
        
        spanChild1.addInnerHtml(new NoTag(null, "Changed Content"));
        pChild2.addInnerHtml(new NoTag(null, "Changed Content"));
        assertNull(spanChild1.getSharedTagContent());
        assertNull(pChild2.getSharedTagContent());
        
        spanChild1.addInnerHtml(stc);
        pChild2.addInnerHtml(stc);
        assertEquals(stc, spanChild1.getSharedTagContent());
        assertEquals(stc, pChild2.getSharedTagContent());
        
        spanChild1.addInnerHtml(spanChild1.getFirstChild());
        pChild2.addInnerHtml(pChild2.getFirstChild());
        assertNull(spanChild1.getSharedTagContent());
        assertNull(pChild2.getSharedTagContent());
        
        spanChild1.addInnerHtml(stc);
        pChild2.addInnerHtml(stc);
        assertEquals(stc, spanChild1.getSharedTagContent());
        assertEquals(stc, pChild2.getSharedTagContent());
        
        spanChild1.appendChild(new NoTag(null, "Changed Content"));
        pChild2.appendChild(new NoTag(null, "Changed Content"));
        assertNull(spanChild1.getSharedTagContent());
        assertNull(pChild2.getSharedTagContent());
        
        spanChild1.addInnerHtml(stc, new SharedTagContent.ContentFormatter<String>() {
            
            @Override
            public SharedTagContent.Content<String> format(SharedTagContent.Content<String> content) {
                assertEquals("Test Content", content.content());
                return new SharedTagContent.Content<>("Formatted content", content.contentTypeHtml());
            }
        });
        pChild2.addInnerHtml(stc, new SharedTagContent.ContentFormatter<String>() {
            
            @Override
            public SharedTagContent.Content<String> format(SharedTagContent.Content<String> content) {
                assertEquals("Test Content", content.content());
                return new SharedTagContent.Content<>("Formatted content", content.contentTypeHtml());
            }
        });
        assertEquals(stc, spanChild1.getSharedTagContent());
        assertEquals(stc, pChild2.getSharedTagContent());
    }
    
    @Test
    public void testContentFormatter() throws Exception {
        
        SharedTagContent<String> stc = new SharedTagContent<>(UpdateClientNature.SEQUENTIAL, "Test Content", true);
        Div div = new Div(null);
        Span spanChild1 = new Span(div);
        spanChild1.addInnerHtml(stc);
        P pChild2 = new P(div);
        pChild2.addInnerHtml(stc);
        
        spanChild1.addInnerHtml(stc, new SharedTagContent.ContentFormatter<String>() {
            
            @Override
            public SharedTagContent.Content<String> format(SharedTagContent.Content<String> content) {
                assertEquals("Test Content", content.content());
                return new SharedTagContent.Content<>("Formatted1 Content", content.contentTypeHtml());
            }
        });
        
        pChild2.addInnerHtml(stc, new SharedTagContent.ContentFormatter<String>() {
            
            @Override
            public SharedTagContent.Content<String> format(SharedTagContent.Content<String> content) {
                assertEquals("Test Content", content.content());
                return new SharedTagContent.Content<>("Formatted2 Content", content.contentTypeHtml());
            }
        });
        assertEquals(stc, spanChild1.getSharedTagContent());
        assertEquals(stc, pChild2.getSharedTagContent());
        
        assertEquals("<span>Formatted1 Content</span>", spanChild1.toHtmlString());
        assertEquals("<p>Formatted2 Content</p>", pChild2.toHtmlString());        
        assertEquals("<div><span>Formatted1 Content</span><p>Formatted2 Content</p></div>", div.toHtmlString());
        
        assertTrue(((NoTag)spanChild1.getFirstChild()).isChildContentTypeHtml());
        assertTrue(((NoTag)pChild2.getFirstChild()).isChildContentTypeHtml());
        
        spanChild1.addInnerHtml(stc, new SharedTagContent.ContentFormatter<String>() {
            
            @Override
            public SharedTagContent.Content<String> format(SharedTagContent.Content<String> content) {
                
                if ("return null".equals(content.content())) {
                    return null;
                } else {
                    assertEquals("Test Content", content.content());
                }
                return new SharedTagContent.Content<>("Formatted1 Content", false);
            }
        });
        
        pChild2.addInnerHtml(stc, new SharedTagContent.ContentFormatter<String>() {
            
            @Override
            public SharedTagContent.Content<String> format(SharedTagContent.Content<String> content) {
                
                if ("return null".equals(content.content())) {
                    return null;
                } else {
                    assertEquals("Test Content", content.content());    
                }
                return new SharedTagContent.Content<>("Formatted2 Content", false);
            }
        });
        
        assertEquals("Formatted1 Content", ((NoTag)spanChild1.getFirstChild()).getChildContent());
        assertEquals("Formatted2 Content", ((NoTag)pChild2.getFirstChild()).getChildContent());
        
        assertFalse(((NoTag)spanChild1.getFirstChild()).isChildContentTypeHtml());
        assertFalse(((NoTag)pChild2.getFirstChild()).isChildContentTypeHtml());
        
        stc.setContent("return null", true);
        
        assertEquals("Formatted1 Content", ((NoTag)spanChild1.getFirstChild()).getChildContent());
        assertEquals("Formatted2 Content", ((NoTag)pChild2.getFirstChild()).getChildContent());
        
        assertFalse(((NoTag)spanChild1.getFirstChild()).isChildContentTypeHtml());
        assertFalse(((NoTag)pChild2.getFirstChild()).isChildContentTypeHtml());
        
        spanChild1.addInnerHtml(stc, new SharedTagContent.ContentFormatter<String>() {
            
            @Override
            public SharedTagContent.Content<String> format(SharedTagContent.Content<String> content) {
                assertEquals("return null", content.content());
                return null;
            }
        });
        
        pChild2.addInnerHtml(stc, new SharedTagContent.ContentFormatter<String>() {
            
            @Override
            public SharedTagContent.Content<String> format(SharedTagContent.Content<String> content) {
                assertEquals("return null", content.content());
                return null;
            }
        });
        
        assertEquals("", ((NoTag)spanChild1.getFirstChild()).getChildContent());
        assertEquals("", ((NoTag)pChild2.getFirstChild()).getChildContent());
        
        assertFalse(((NoTag)spanChild1.getFirstChild()).isChildContentTypeHtml());
        assertFalse(((NoTag)pChild2.getFirstChild()).isChildContentTypeHtml());
        
    }
    
    @Test
    public void testSharedProperty() throws Exception {
        SharedTagContent<String> stc = new SharedTagContent<>(false, "Test Content", false);
        
        Div div = new Div(null);
        Span span1 = new Span(div);
        span1.addInnerHtml(stc);
        Span span2 = new Span(div);
        span2.addInnerHtml(stc);
        
        
        assertEquals("Test Content", stc.getContent());
        assertEquals("<div><span>Test Content</span><span>Test Content</span></div>", div.toHtmlString());
        assertFalse(stc.isShared());
        stc.setContent("Content Changed");
        assertEquals("Content Changed", stc.getContent());
        assertEquals("<div><span>Test Content</span><span>Test Content</span></div>", div.toHtmlString());
        assertFalse(stc.isContentTypeHtml());
        
        stc.setShared(true);
        assertTrue(stc.isShared());
        stc.setContent("Content Changed2");
        assertEquals("Content Changed2", stc.getContent());
        assertEquals("<div><span>Content Changed2</span><span>Content Changed2</span></div>", div.toHtmlString());
        assertFalse(stc.isContentTypeHtml());
    }
    
    @Test
    public void testAbstractHtmlRemoveSharedTagContent() throws Exception {
        SharedTagContent<String> stc = new SharedTagContent<>(true, "Test Content", false);
        
        Div div = new Div(null);
        Span span1 = new Span(div);
        span1.addInnerHtml(stc);
        Span span2 = new Span(div);
        span2.addInnerHtml(stc);
        
        
        assertEquals("Test Content", stc.getContent());
        assertEquals("<div><span>Test Content</span><span>Test Content</span></div>", div.toHtmlString());
        assertTrue(span2.removeSharedTagContent(false));
        stc.setContent("Content Changed");
        assertEquals("Content Changed", stc.getContent());
        assertEquals("<div><span>Content Changed</span><span>Test Content</span></div>", div.toHtmlString());


        span2.addInnerHtml(stc);
        
        stc.setContent("Content Changed2");
        assertEquals("Content Changed2", stc.getContent());
        assertEquals("<div><span>Content Changed2</span><span>Content Changed2</span></div>", div.toHtmlString());
        
        assertTrue(span2.removeSharedTagContent(true));
        stc.setContent("Content Changed3");
        assertEquals("Content Changed3", stc.getContent());
        assertEquals("<div><span>Content Changed3</span><span></span></div>", div.toHtmlString());

        assertFalse(span2.removeSharedTagContent(false));
        assertFalse(span2.removeSharedTagContent(true));
    }
    
    @Test
    public void testNoTagGC() {
        Span span = new Span(null);
        SharedTagContent<String> stc = new SharedTagContent<String>("initd");
        span.subscribeTo(stc);
        
        final AbstractHtml firstChild = span.getFirstChild();
        
        assertNotNull(firstChild);
        assertEquals(span, firstChild.getParent());
        
        stc.setContent("content changed");
        assertNull(firstChild.getParent());
        
        assertNotEquals(firstChild, span.getFirstChild());
        assertFalse(stc.contains(firstChild));
        
    }
    
//for dev test purpose
//    @Test testAbstractHtmlSubscribeTo
//    public static void main(String args[]) {
//        BrowserPage browserPage = new BrowserPage() {
//            
//            private BrowserPage current = this;
//            
//            @Override
//            public String webSocketUrl() {
//                // TODO Auto-generated method stub
//                return "wss://webfirmframework.com/wsforwffweb";
//            }
//            
//            @Override
//            public AbstractHtml render() {
//                Html html = new Html(null) {
//                    {
//                        new Head(this);
//                        new Body(this) {{
//                            final Div div = new Div(this, new Id("mainDivId"));
//                            
//                            SharedTagContent<String> stc = new SharedTagContent<String>("initial");
//                            div.subscribeTo(stc);
//                            final Thread thread = new Thread(new Runnable() {
//                                
//                                @Override
//                                public void run() {
//                                    try {
//                                        Thread.sleep(5000);
//                                    } catch (InterruptedException e) {
//                                        // TODO Auto-generated catch block
//                                        e.printStackTrace();
//                                    }
//                                    for (int i = 0; i < 10; i++) {
//                                        stc.setContent("changed value time " + i);
//                                        System.out.println("BrowserPage " + current.getPushQueueSize());
//                                    }
//                                    
//                                }
//                            });
//                            thread.start();
//                        }};
//                        
//                    }
//                };
//                return html;
//            }
//        };
//        
//        final String htmlString = browserPage.toHtmlString();
//        
//        
//        final AbstractHtml div = browserPage.getTagRepository().findTagById("mainDivId");
//        assertNotNull(div);
//    }
    
    @Test
    public void testOrderOfContentChangeOnConsumerTags() throws Exception {
        
        int maxTags = 500;
        List<Integer> expectedContentChangeOrder = new ArrayList<>();
        for (int i = 0; i < maxTags * 2; i++) {
            expectedContentChangeOrder.add(i);
        }
        
        List<Integer> contentChangeOrder = new ArrayList<>();
        
        AtomicInteger idGenerator = new AtomicInteger(0);
        
        Div div = new Div(null);
        
        
        SharedTagContent<String> stc = new SharedTagContent<String>("initial content");
        
        for (int i = 0; i < maxTags; i++) {
            Span span1 = new Span(div);
            span1.subscribeTo(stc, new ContentFormatter<String>() {

                @Override
                public Content<String> format(Content<String> content) {
                    contentChangeOrder.add(idGenerator.getAndIncrement());
                    return content;
                }
            });
        }
        
        stc.setContent("content changed");      
        
        assertArrayEquals(expectedContentChangeOrder.toArray(), contentChangeOrder.toArray());
    }
    
    @Test
    public void testListenersLifeCycle1() throws Exception {
        
        Div div = new Div(null);
        
        SharedTagContent<String> stc = new SharedTagContent<String>("initial content");
        
        ContentChangeListener<String> contentChangeListener = new ContentChangeListener<String>() {
            
            @Override
            public Runnable contentChanged(ChangeEvent<String> changeEvent) {
                return null;
            }
        };
        
        stc.addContentChangeListener(div, contentChangeListener);
        
        div.subscribeTo(stc);
        
        assertNotNull(stc.getContentChangeListeners(div));
        assertEquals(1, stc.getContentChangeListeners(div).size());
        
        div.removeAllChildren();
        
        assertNull(stc.getContentChangeListeners(div));
        
        //scenario 1
        stc.addContentChangeListener(div, contentChangeListener);
        
        div.subscribeTo(stc);
        
        assertNotNull(stc.getContentChangeListeners(div));
        assertEquals(1, stc.getContentChangeListeners(div).size());
      
        //scenario 2
        div.addInnerHtml(new NoTag(null, "NoTag content"));
        
        assertNull(stc.getContentChangeListeners(div));
        
        //scenario 3
        stc.addContentChangeListener(div, contentChangeListener);
        
        div.subscribeTo(stc);
        
        stc.setContent("some stc content");
       
        
        //scenario 4
        div.removeAllChildren();
        
        assertNull(stc.getContentChangeListeners(div));
        
        //scenario 5
        
        stc.addContentChangeListener(div, contentChangeListener);
        
        ContentFormatter<String> formatter = new ContentFormatter<String>() {

            @Override
            public Content<String> format(Content<String> content) {
                return null;
            }
        };
        div.subscribeTo(stc, formatter);
        
        stc.setContent("some stc content");
        
        assertNotNull(div.getFirstChild().getCachedStcFormatter());
        
        assertEquals(formatter, div.getFirstChild().getCachedStcFormatter());
        
        //scenario 6
        assertNotNull(stc.getContentChangeListeners(div));
        AbstractHtml firstChild = div.getFirstChild();
        div.addInnerHtml(new NoTag(null, ""));
        assertNull(firstChild.getCachedStcFormatter());
        assertNull(stc.getContentChangeListeners(div));
        formatter = new ContentFormatter<String>() {

            @Override
            public Content<String> format(Content<String> content) {
                return new Content<String>("another content", false);
            }
        };
        div.subscribeTo(stc, formatter);
        
        stc.setContent("some stc content2");
        
        assertNotNull(div.getFirstChild().getCachedStcFormatter());
        assertEquals(formatter, div.getFirstChild().getCachedStcFormatter());
        
        //scenario 7        
        firstChild = div.getFirstChild();
        div.appendChildren(new NoTag(null, ""));
        assertNull(firstChild.getCachedStcFormatter());
       
        div.subscribeTo(stc, formatter);
        
        stc.setContent("some stc content2");
        
        assertNotNull(div.getFirstChild().getCachedStcFormatter());
        assertEquals(formatter, div.getFirstChild().getCachedStcFormatter());
        
        
    }
    
    @Test
    public void testListenersLifeCycle2() throws Exception {        
        
        Div div = new Div(null);
        
        SharedTagContent<String> stc = new SharedTagContent<String>("initial content");
        
        ContentChangeListener<String> contentChangeListener = new ContentChangeListener<String>() {
            
            @Override
            public Runnable contentChanged(ChangeEvent<String> changeEvent) {
                return null;
            }
        };
        
        stc.addContentChangeListener(div, contentChangeListener);
        
        div.subscribeTo(stc);
        
        assertNotNull(stc.getContentChangeListeners(div));
        assertEquals(1, stc.getContentChangeListeners(div).size());
        
        div.appendChildren(new NoTag(null, ""));
        
        assertNull(stc.getContentChangeListeners(div));
        
        //scenario 1
        stc.addContentChangeListener(div, contentChangeListener);
        
        assertNotNull(stc.getContentChangeListeners(div));
        
        div.subscribeTo(stc);
        
        assertNotNull(stc.getContentChangeListeners(div));
        assertEquals(1, stc.getContentChangeListeners(div).size());
      
        //scenario 2
        div.appendChild(new NoTag(null, ""));
        
        assertNull(stc.getContentChangeListeners(div));
        
        //scenario 3
        stc.addContentChangeListener(div, contentChangeListener);
        
        div.subscribeTo(stc);
        
        stc.setContent("some stc content");
       
        
        //scenario 4
        div.appendChild(new NoTag(null, ""));
        
        assertNull(stc.getContentChangeListeners(div));
        
        //scenario 5
        
        stc.addContentChangeListener(div, contentChangeListener);
        
        ContentFormatter<String> formatter = new ContentFormatter<String>() {

            @Override
            public Content<String> format(Content<String> content) {
                return null;
            }
        };
        div.subscribeTo(stc, formatter);
        
        stc.setContent("some stc content");
        
        assertNotNull(div.getFirstChild().getCachedStcFormatter());
        assertEquals(formatter, div.getFirstChild().getCachedStcFormatter());
        
        //scenario 6
        assertNotNull(stc.getContentChangeListeners(div));
        div.addInnerHtml(new NoTag(null, ""));
        assertNull(stc.getContentChangeListeners(div));
        
        formatter = new ContentFormatter<String>() {

            @Override
            public Content<String> format(Content<String> content) {
                return new Content<String>("another content", false);
            }
        };
        div.subscribeTo(stc, formatter);
        
        stc.setContent("some stc content2");
        
        assertNotNull(div.getFirstChild().getCachedStcFormatter());
        assertEquals(formatter, div.getFirstChild().getCachedStcFormatter());
        
    }
    
    @Test
    public void testListenersLifeCycle3() throws Exception {
        
        Div div = new Div(null);
        
        SharedTagContent<String> stc = new SharedTagContent<String>("initial content");
        
        ContentChangeListener<String> contentChangeListener = new ContentChangeListener<String>() {
            
            @Override
            public Runnable contentChanged(ChangeEvent<String> changeEvent) {
                return null;
            }
        };
        
        stc.addContentChangeListener(div, contentChangeListener);
        
        div.subscribeTo(stc);
        
        assertNotNull(stc.getContentChangeListeners(div));
        assertEquals(1, stc.getContentChangeListeners(div).size());
        
        div.removeSharedTagContent(false);
        
        assertNull(stc.getContentChangeListeners(div));
        
        //scenario 1
        stc.addContentChangeListener(div, contentChangeListener);
        
        assertNotNull(stc.getContentChangeListeners(div));
        
        div.subscribeTo(stc);
        
        assertNotNull(stc.getContentChangeListeners(div));
        assertEquals(1, stc.getContentChangeListeners(div).size());
      
        //scenario 2
        div.removeSharedTagContent(true);
        
        assertNull(stc.getContentChangeListeners(div));
        
        //scenario 3
        stc.addContentChangeListener(div, contentChangeListener);
        
        div.subscribeTo(stc);
        
        stc.setContent("some stc content");
       
        
        //scenario 4
        div.removeChildren(Arrays.asList(div.getFirstChild()));
        
        assertNull(stc.getContentChangeListeners(div));
        
        //scenario 5
        
        stc.addContentChangeListener(div, contentChangeListener);
        
        ContentFormatter<String> formatter = new ContentFormatter<String>() {

            @Override
            public Content<String> format(Content<String> content) {
                return null;
            }
        };
        div.subscribeTo(stc, formatter);
        
        stc.setContent("some stc content");
        
        assertNotNull(div.getFirstChild().getCachedStcFormatter());
        assertEquals(formatter, div.getFirstChild().getCachedStcFormatter());
        
        //scenario 6
        assertNotNull(stc.getContentChangeListeners(div));
        div.removeSharedTagContent(true);
        assertNull(stc.getContentChangeListeners(div));
        
        formatter = new ContentFormatter<String>() {

            @Override
            public Content<String> format(Content<String> content) {
                return new Content<String>("another content", false);
            }
        };
        div.subscribeTo(stc, formatter);
        
        stc.setContent("some stc content2");
        
        assertNotNull(div.getFirstChild().getCachedStcFormatter());
        assertEquals(formatter, div.getFirstChild().getCachedStcFormatter());
        
    }
    
    
    @Test
    public void testListenersLifeCycle4() throws Exception {
        
        Div div = new Div(null);
        
        SharedTagContent<String> stc = new SharedTagContent<String>("initial content");
        
        ContentChangeListener<String> contentChangeListener = new ContentChangeListener<String>() {
            
            @Override
            public Runnable contentChanged(ChangeEvent<String> changeEvent) {
                return null;
            }
        };
        
        stc.addContentChangeListener(div, contentChangeListener);
        
        div.subscribeTo(stc);
        
        assertNotNull(stc.getContentChangeListeners(div));
        assertEquals(1, stc.getContentChangeListeners(div).size());
        
        div.prependChildren(new NoTag(null, ""));
        
        assertNull(stc.getContentChangeListeners(div));
        
        //scenario 1
        stc.addContentChangeListener(div, contentChangeListener);
        
        assertNotNull(stc.getContentChangeListeners(div));
        
        div.subscribeTo(stc);
        
        assertNotNull(stc.getContentChangeListeners(div));
        assertEquals(1, stc.getContentChangeListeners(div).size());
      
        //scenario 2
        div.appendChild(new NoTag(null, ""));
        
        assertNull(stc.getContentChangeListeners(div));
        
        //scenario 3
        stc.addContentChangeListener(div, contentChangeListener);
        
        div.subscribeTo(stc);
        
        stc.setContent("some stc content");
       
        
        //scenario 4
        div.removeChild(div.getFirstChild());
        
        assertNull(stc.getContentChangeListeners(div));
        
        //scenario 5
        
        stc.addContentChangeListener(div, contentChangeListener);
        
        ContentFormatter<String> formatter = new ContentFormatter<String>() {

            @Override
            public Content<String> format(Content<String> content) {
                return null;
            }
        };
        div.subscribeTo(stc, formatter);
        
        stc.setContent("some stc content");
        
        assertNotNull(div.getFirstChild().getCachedStcFormatter());
        assertEquals(formatter, div.getFirstChild().getCachedStcFormatter());
        
        //scenario 6
        assertNotNull(stc.getContentChangeListeners(div));
        div.removeChild(div.getFirstChild());
        assertNull(stc.getContentChangeListeners(div));
        
        formatter = new ContentFormatter<String>() {

            @Override
            public Content<String> format(Content<String> content) {
                return new Content<String>("another content", false);
            }
        };
        div.subscribeTo(stc, formatter);
        
        stc.setContent("some stc content2");
        
        assertNotNull(div.getFirstChild().getCachedStcFormatter());
        assertEquals(formatter, div.getFirstChild().getCachedStcFormatter());
        
        //scenario 7
        AbstractHtml firstChild = div.getFirstChild();
        div.removeChild(new NoTag(null, ""));
        assertEquals(firstChild, div.getFirstChild());
        assertNotNull(div.getFirstChild().getCachedStcFormatter());
        assertEquals(formatter, div.getFirstChild().getCachedStcFormatter());
        
    }
    
    @Test
    public void testListenersLifeCycle5() throws Exception {
        
        P p = new P(null);
        Div div = new Div(p);
        
        SharedTagContent<String> stc = new SharedTagContent<String>("initial content");
        
        ContentChangeListener<String> contentChangeListener = new ContentChangeListener<String>() {
            
            @Override
            public Runnable contentChanged(ChangeEvent<String> changeEvent) {
                return null;
            }
        };
        
        stc.addContentChangeListener(div, contentChangeListener);
        
        div.subscribeTo(stc);
        
        assertNotNull(stc.getContentChangeListeners(div));
        assertEquals(1, stc.getContentChangeListeners(div).size());
        
        div.replaceWith(new NoTag(null, ""));
        
        assertNotNull(stc.getContentChangeListeners(div));
        assertEquals(1, stc.getContentChangeListeners(div).size());
        
        
        ContentFormatter<String> formatter = new ContentFormatter<String>() {

            @Override
            public Content<String> format(Content<String> content) {
                return null;
            }
        };
        div.subscribeTo(stc, formatter);
        
        stc.setContent("some stc content");
        
        assertNotNull(div.getFirstChild().getCachedStcFormatter());
        assertEquals(formatter, div.getFirstChild().getCachedStcFormatter());        
        
        formatter = new ContentFormatter<String>() {

            @Override
            public Content<String> format(Content<String> content) {
                return null;
            }
        };
        div.subscribeTo(stc, formatter);
        
        stc.setContent("some stc content");
        
        assertNotNull(div.getFirstChild().getCachedStcFormatter());
        assertEquals(formatter, div.getFirstChild().getCachedStcFormatter());
        
    }
    
    @Test
    public void testListenersLifeCycle6() throws Exception {
        
        P p = new P(null);
        Div div = new Div(p);
        
        SharedTagContent<String> stc = new SharedTagContent<String>("initial content");
        
        ContentChangeListener<String> contentChangeListener = new ContentChangeListener<String>() {
            
            @Override
            public Runnable contentChanged(ChangeEvent<String> changeEvent) {
                return null;
            }
        };
        
        stc.addContentChangeListener(div, contentChangeListener);
        
        div.subscribeTo(stc);
        
        assertNotNull(stc.getContentChangeListeners(div));
        assertEquals(1, stc.getContentChangeListeners(div).size());
        
        div.insertBefore(new NoTag(null, "before"));
        
        assertNotNull(stc.getContentChangeListeners(div));
        assertEquals(1, stc.getContentChangeListeners(div).size());
        
        
        ContentFormatter<String> formatter = new ContentFormatter<String>() {

            @Override
            public Content<String> format(Content<String> content) {
                return null;
            }
        };
        div.subscribeTo(stc, formatter);
        
        stc.setContent("some stc content");
        
        assertNotNull(div.getFirstChild().getCachedStcFormatter());
        assertEquals(formatter, div.getFirstChild().getCachedStcFormatter());        
        
        formatter = new ContentFormatter<String>() {

            @Override
            public Content<String> format(Content<String> content) {
                return null;
            }
        };
        div.subscribeTo(stc, formatter);
        
        stc.setContent("some stc content");
        
        assertNotNull(div.getFirstChild().getCachedStcFormatter());
        assertEquals(formatter, div.getFirstChild().getCachedStcFormatter());
        
    }
    
    @Test
    public void testListenersLifeCycle7() throws Exception {
        
        P p = new P(null);
        Div div = new Div(p);
        
        SharedTagContent<String> stc = new SharedTagContent<String>("initial content");
        
        ContentChangeListener<String> contentChangeListener = new ContentChangeListener<String>() {
            
            @Override
            public Runnable contentChanged(ChangeEvent<String> changeEvent) {
                return null;
            }
        };
        
        stc.addContentChangeListener(div, contentChangeListener);
        
        div.subscribeTo(stc);
        
        assertNotNull(stc.getContentChangeListeners(div));
        assertEquals(1, stc.getContentChangeListeners(div).size());
        
        div.insertAfter(new NoTag(null, "before"));
        
        assertNotNull(stc.getContentChangeListeners(div));
        assertEquals(1, stc.getContentChangeListeners(div).size());
        
        
        ContentFormatter<String> formatter = new ContentFormatter<String>() {

            @Override
            public Content<String> format(Content<String> content) {
                return null;
            }
        };
        div.subscribeTo(stc, formatter);
        
        stc.setContent("some stc content");
        
        assertNotNull(div.getFirstChild().getCachedStcFormatter());
        assertEquals(formatter, div.getFirstChild().getCachedStcFormatter());

        AbstractHtml firstChildBefore = div.getFirstChild();
        formatter = new ContentFormatter<String>() {

            @Override
            public Content<String> format(Content<String> content) {
                return null;
            }
        };
        div.subscribeTo(stc, formatter);

        assertNotEquals(firstChildBefore, div.getFirstChild());

        firstChildBefore = div.getFirstChild();

        stc.setContent("some stc content");
        
        assertNotNull(div.getFirstChild().getCachedStcFormatter());
        assertEquals(formatter, div.getFirstChild().getCachedStcFormatter());
        assertEquals(firstChildBefore, div.getFirstChild());
        
    }

    @Test
    public void testRemoveSharedTagContentOnContentChange() {
        AbstractHtml span = new Span(null);
        SharedTagContent<String> stc = new SharedTagContent<String>("initial content");
        span.subscribeTo(stc, content -> null);

        stc.addContentChangeListener(span, changeEvent -> {
            return () -> span.removeSharedTagContent(false);
        });
        assertEquals(stc, span.getSharedTagContent());
        stc.setContent("again changed");
        assertNull(span.getSharedTagContent());
        assertNull(stc.getContentChangeListeners(span));

    }

    @Test
    public void testSetContentWithWhenURI() {
        // dead-lock testing
        StringBuffer controlFlow = new StringBuffer();
        SharedTagContent<String> stc = new SharedTagContent<>("initial").setAsyncUpdate(true);

        BrowserPage browserPage1 = new BrowserPage() {

            @Override
            public String webSocketUrl() {
                return "wss://wffweb";
            }

            @Override
            public AbstractHtml render() {
                super.setURI("/someuri");
                return new Html(null).<Html>give(html1 -> {
                    new Body(html1).give(body -> {

                        new Div(body).subscribeTo(stc);
                        new Div(body).subscribeTo(stc);
                        new Div(body).subscribeTo(stc);

                        new Div(body).whenURI((uri) -> true, (event) -> {
                            controlFlow.append("invoked div whenURI %s success ".formatted(event.uriEvent().uriAfter()));
                            stc.setContent("uri is " + event.uriEvent().uriAfter());
                        });
                    });
                });
            }

        };
        browserPage1.toHtmlString();

        assertEquals("invoked div whenURI /someuri success ", controlFlow.toString());

        BrowserPage browserPage2 = new BrowserPage() {

            @Override
            public String webSocketUrl() {
                return "wss://wffweb";
            }

            @Override
            public AbstractHtml render() {
                super.setURI("/someuri");
                return new Html(null).<Html>give(html1 -> {
                    new Body(html1).give(body -> {

                        new Div(body).subscribeTo(stc);
                        new Div(body).subscribeTo(stc);
                        new Div(body).subscribeTo(stc);

                        new Div(body).whenURI((uri) -> true, (event) -> {
                            controlFlow.append("invoked div whenURI %s success ".formatted(event.uriEvent().uriAfter()));
                            stc.setContent("uri is " + event.uriEvent().uriAfter());
                        });
                    });
                });
            }

        };
        browserPage2.toHtmlString();

        assertEquals("invoked div whenURI /someuri success invoked div whenURI /someuri success ", controlFlow.toString());

        browserPage1.setURI("another");

        assertEquals("invoked div whenURI /someuri success invoked div whenURI /someuri success invoked div whenURI another success ", controlFlow.toString());

        browserPage2.setURI("another");

        assertEquals("invoked div whenURI /someuri success invoked div whenURI /someuri success invoked div whenURI another success invoked div whenURI another success ", controlFlow.toString());

    }
            

}
