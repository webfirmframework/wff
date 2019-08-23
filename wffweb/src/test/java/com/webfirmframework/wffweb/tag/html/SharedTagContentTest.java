package com.webfirmframework.wffweb.tag.html;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;

import com.webfirmframework.wffweb.tag.html.SharedTagContent.ChangeEvent;
import com.webfirmframework.wffweb.tag.html.SharedTagContent.DetachEvent;
import com.webfirmframework.wffweb.tag.html.SharedTagContent.UpdateClientNature;
import com.webfirmframework.wffweb.tag.html.stylesandsemantics.Div;
import com.webfirmframework.wffweb.tag.html.stylesandsemantics.Span;
import com.webfirmframework.wffweb.tag.htmlwff.NoTag;

public class SharedTagContentTest {

    @Test
    public void testSharedTagContentString() {
//        fail("Not yet implemented");
        SharedTagContent stc = new SharedTagContent("Test content");
        Div div = new Div(null);
        Span span = new Span(div);
        span.addInnerHtml(stc);
        
        assertEquals("<div><span>Test content</span></div>", div.toHtmlString());
        assertEquals(UpdateClientNature.ALLOW_ASYNC_PARALLEL, stc.getUpdateClientNature());
    }

    @Test
    public void testSharedTagContentUpdateClientNatureString() {
        {
            SharedTagContent stc = new SharedTagContent(UpdateClientNature.ALLOW_ASYNC_PARALLEL, "Test content");
            Div div = new Div(null);
            Span span = new Span(div);
            span.addInnerHtml(stc);
            
            assertEquals("<div><span>Test content</span></div>", div.toHtmlString());
            assertEquals(UpdateClientNature.ALLOW_ASYNC_PARALLEL, stc.getUpdateClientNature());
        }
        {
            SharedTagContent stc = new SharedTagContent(UpdateClientNature.ALLOW_PARALLEL, "Test content");
            Div div = new Div(null);
            Span span = new Span(div);
            span.addInnerHtml(stc);
            
            assertEquals("<div><span>Test content</span></div>", div.toHtmlString());
            assertEquals(UpdateClientNature.ALLOW_PARALLEL, stc.getUpdateClientNature());
        }
        {
            SharedTagContent stc = new SharedTagContent(UpdateClientNature.SEQUENTIAL, "Test content");
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
            SharedTagContent stc = new SharedTagContent("Test content", true);
            Div div = new Div(null);
            Span span = new Span(div);
            span.addInnerHtml(stc);
            
            assertEquals("<div><span>Test content</span></div>", div.toHtmlString());
            assertTrue(stc.isContentTypeHtml());
            assertEquals(UpdateClientNature.ALLOW_ASYNC_PARALLEL, stc.getUpdateClientNature());
        }
        {
            SharedTagContent stc = new SharedTagContent("Test content", false);
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
            SharedTagContent stc = new SharedTagContent(UpdateClientNature.ALLOW_ASYNC_PARALLEL, "Test content", true);
            Div div = new Div(null);
            Span span = new Span(div);
            span.addInnerHtml(stc);
            
            assertEquals("<div><span>Test content</span></div>", div.toHtmlString());
            assertTrue(stc.isContentTypeHtml());
            assertEquals(UpdateClientNature.ALLOW_ASYNC_PARALLEL, stc.getUpdateClientNature());
        }
        {
            SharedTagContent stc = new SharedTagContent(UpdateClientNature.ALLOW_ASYNC_PARALLEL, "Test content", false);
            Div div = new Div(null);
            Span span = new Span(div);
            span.addInnerHtml(stc);
            
            assertEquals("<div><span>Test content</span></div>", div.toHtmlString());
            assertFalse(stc.isContentTypeHtml());
            assertEquals(UpdateClientNature.ALLOW_ASYNC_PARALLEL, stc.getUpdateClientNature());
        }
        {
            SharedTagContent stc = new SharedTagContent(UpdateClientNature.ALLOW_PARALLEL, "Test content", true);
            Div div = new Div(null);
            Span span = new Span(div);
            span.addInnerHtml(stc);
            
            assertEquals("<div><span>Test content</span></div>", div.toHtmlString());
            assertTrue(stc.isContentTypeHtml());
            assertEquals(UpdateClientNature.ALLOW_PARALLEL, stc.getUpdateClientNature());
        }
        {
            SharedTagContent stc = new SharedTagContent(UpdateClientNature.ALLOW_PARALLEL, "Test content", false);
            Div div = new Div(null);
            Span span = new Span(div);
            span.addInnerHtml(stc);
            
            assertEquals("<div><span>Test content</span></div>", div.toHtmlString());
            assertFalse(stc.isContentTypeHtml());
            assertEquals(UpdateClientNature.ALLOW_PARALLEL, stc.getUpdateClientNature());
        }
        {
            SharedTagContent stc = new SharedTagContent(UpdateClientNature.SEQUENTIAL, "Test content", true);
            Div div = new Div(null);
            Span span = new Span(div);
            span.addInnerHtml(stc);
            
            assertEquals("<div><span>Test content</span></div>", div.toHtmlString());
            assertTrue(stc.isContentTypeHtml());
            assertEquals(UpdateClientNature.SEQUENTIAL, stc.getUpdateClientNature());
        }
        {
            SharedTagContent stc = new SharedTagContent(UpdateClientNature.SEQUENTIAL, "Test content", false);
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
            SharedTagContent stc = new SharedTagContent(UpdateClientNature.ALLOW_ASYNC_PARALLEL, "Test content", true);
            Div div = new Div(null);
            Span span = new Span(div);
            span.addInnerHtml(stc);
            
            assertEquals("Test content", stc.getContent());
            assertEquals("<div><span>Test content</span></div>", div.toHtmlString());
            assertTrue(stc.isContentTypeHtml());
            assertEquals(UpdateClientNature.ALLOW_ASYNC_PARALLEL, stc.getUpdateClientNature());
        }
        {
            SharedTagContent stc = new SharedTagContent(UpdateClientNature.ALLOW_ASYNC_PARALLEL, "Test content", true);
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
            SharedTagContent stc = new SharedTagContent(UpdateClientNature.ALLOW_ASYNC_PARALLEL, "Test content", true);
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
        SharedTagContent stc = new SharedTagContent(UpdateClientNature.SEQUENTIAL, "Test content", true);
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
        SharedTagContent stc = new SharedTagContent(UpdateClientNature.SEQUENTIAL, "Test content", true);
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
        SharedTagContent stc = new SharedTagContent(UpdateClientNature.SEQUENTIAL, "Test content", true);
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
        SharedTagContent stc = new SharedTagContent("Test content", true);
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
        SharedTagContent stc = new SharedTagContent("Test content", true);
        Div div = new Div(null);
        Span span = new Span(div);
        span.addInnerHtml(stc);
        
        assertTrue(stc.contains(span.getFirstChild()));
        assertFalse(stc.contains(div.getFirstChild()));
    }

    @Test
    public void testSetContentString() {
        
        {
            SharedTagContent stc = new SharedTagContent(UpdateClientNature.ALLOW_ASYNC_PARALLEL, "Test content", true);
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
            SharedTagContent stc = new SharedTagContent(UpdateClientNature.SEQUENTIAL, "Test content", true);
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
            SharedTagContent stc = new SharedTagContent(UpdateClientNature.SEQUENTIAL, "Test content", true);
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
            SharedTagContent stc = new SharedTagContent(UpdateClientNature.SEQUENTIAL, "Test content", true);
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
            SharedTagContent stc = new SharedTagContent(UpdateClientNature.SEQUENTIAL, "Test content", true);
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
            SharedTagContent stc = new SharedTagContent(UpdateClientNature.SEQUENTIAL, "Test content", true);
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
            SharedTagContent stc = new SharedTagContent(UpdateClientNature.SEQUENTIAL, "Test content", true);
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
            SharedTagContent stc = new SharedTagContent(UpdateClientNature.SEQUENTIAL, "Test content", true);
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
            SharedTagContent stc = new SharedTagContent(UpdateClientNature.SEQUENTIAL, "Test content", true);
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
            SharedTagContent stc = new SharedTagContent(UpdateClientNature.SEQUENTIAL, "Test content", true);
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
            SharedTagContent stc = new SharedTagContent(UpdateClientNature.SEQUENTIAL, "Test content", true);
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
            SharedTagContent stc = new SharedTagContent(UpdateClientNature.SEQUENTIAL, "Test content", true);
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
            SharedTagContent stc = new SharedTagContent(UpdateClientNature.SEQUENTIAL, "Test content", true);
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
            SharedTagContent stc = new SharedTagContent(UpdateClientNature.SEQUENTIAL, "Test content", true);
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
            SharedTagContent stc = new SharedTagContent(UpdateClientNature.ALLOW_PARALLEL, "Test content", true);
            Div div = new Div(null);
            Span span1 = new Span(div);
            span1.addInnerHtml(stc);
            Span span2 = new Span(div);
            span2.addInnerHtml(stc);
            
            AtomicBoolean listenerInvoked = new AtomicBoolean();
            stc.addContentChangeListener(span2, new SharedTagContent.ContentChangeListener() {
                
                @Override
                public Runnable contentChanged(ChangeEvent changeEvent) {
                    listenerInvoked.set(true);
                    assertEquals("Test content", changeEvent.getContentBefore().getContent());
                    assertEquals("Content Changed", changeEvent.getContentAfter().getContent());
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
            SharedTagContent stc = new SharedTagContent(UpdateClientNature.SEQUENTIAL, "Test content", true);
            Div div = new Div(null);
            Span spanChild1 = new Span(div);
            spanChild1.addInnerHtml(stc);
            P pChild2 = new P(div);
            pChild2.addInnerHtml(stc);
            
            AtomicBoolean listenerInvoked = new AtomicBoolean();
            stc.addDetachListener(pChild2, new SharedTagContent.DetachListener() {
                
                @Override
                public Runnable detached(DetachEvent detachEvent) {
                    listenerInvoked.set(true);
                    assertEquals("Test content", detachEvent.getContent().getContent());
                    
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
            SharedTagContent stc = new SharedTagContent(UpdateClientNature.SEQUENTIAL, "Test content", true);
            Div div = new Div(null);
            Span spanChild1 = new Span(div);
            spanChild1.addInnerHtml(stc);
            P pChild2 = new P(div);
            pChild2.addInnerHtml(stc);
            
            AtomicBoolean listenerInvoked = new AtomicBoolean();
            stc.addDetachListener(pChild2, new SharedTagContent.DetachListener() {
                
                @Override
                public Runnable detached(DetachEvent detachEvent) {
                    listenerInvoked.set(true);
                    assertEquals("Test content", detachEvent.getContent().getContent());
                    
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

        SharedTagContent stc = new SharedTagContent(UpdateClientNature.ALLOW_PARALLEL, "Test content", true);
        Div div = new Div(null);
        Span span1 = new Span(div);
        span1.addInnerHtml(stc);
        Span span2 = new Span(div);
        span2.addInnerHtml(stc);
        
        AtomicBoolean listenerInvoked = new AtomicBoolean();
        final SharedTagContent.ContentChangeListener contentChangeListener = new SharedTagContent.ContentChangeListener() {
            
            @Override
            public Runnable contentChanged(ChangeEvent changeEvent) {
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
        SharedTagContent stc = new SharedTagContent(UpdateClientNature.ALLOW_PARALLEL, "Test content", true);
        Div div = new Div(null);
        Span span1 = new Span(div);
        span1.addInnerHtml(stc);
        Span span2 = new Span(div);
        span2.addInnerHtml(stc);
        
        AtomicBoolean listenerInvoked = new AtomicBoolean();
        final SharedTagContent.ContentChangeListener contentChangeListener = new SharedTagContent.ContentChangeListener() {
            
            @Override
            public Runnable contentChanged(ChangeEvent changeEvent) {
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

        SharedTagContent stc = new SharedTagContent(UpdateClientNature.ALLOW_PARALLEL, "Test content", true);
        Div div = new Div(null);
        Span span1 = new Span(div);
        span1.addInnerHtml(stc);
        Span span2 = new Span(div);
        span2.addInnerHtml(stc);
        
        AtomicBoolean listenerInvoked = new AtomicBoolean();
        final SharedTagContent.DetachListener detachListener = new SharedTagContent.DetachListener() {
            
            @Override
            public Runnable detached(DetachEvent detachEvent) {
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

        SharedTagContent stc = new SharedTagContent(UpdateClientNature.ALLOW_PARALLEL, "Test content", true);
        Div div = new Div(null);
        Span span1 = new Span(div);
        span1.addInnerHtml(stc);
        Span span2 = new Span(div);
        span2.addInnerHtml(stc);
        
        AtomicBoolean listenerInvoked = new AtomicBoolean();
        final SharedTagContent.DetachListener detachListener = new SharedTagContent.DetachListener() {
            
            @Override
            public Runnable detached(DetachEvent detachEvent) {
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
        SharedTagContent stc = new SharedTagContent(UpdateClientNature.ALLOW_PARALLEL, "Test content", true);
        Div div = new Div(null);
        Span span1 = new Span(div);
        span1.addInnerHtml(stc);
        Span span2 = new Span(div);
        span2.addInnerHtml(stc);
        
        AtomicBoolean listenerInvoked = new AtomicBoolean();
        final SharedTagContent.ContentChangeListener contentChangeListener = new SharedTagContent.ContentChangeListener() {
            
            @Override
            public Runnable contentChanged(ChangeEvent changeEvent) {
                listenerInvoked.set(true);
                return null;
            }
        };
        stc.addContentChangeListener(span2, contentChangeListener);
        
        stc.removeContentChangeListeners(span2, contentChangeListener);
        stc.setContent("Changed Content");
        
        assertFalse(listenerInvoked.get());
    }

    @Test
    public void testRemoveDetachListeners() {
        SharedTagContent stc = new SharedTagContent(UpdateClientNature.ALLOW_PARALLEL, "Test content", true);
        Div div = new Div(null);
        Span span1 = new Span(div);
        span1.addInnerHtml(stc);
        Span span2 = new Span(div);
        span2.addInnerHtml(stc);
        
        AtomicBoolean listenerInvoked = new AtomicBoolean();
        final SharedTagContent.DetachListener detachListener = new SharedTagContent.DetachListener() {
            
            @Override
            public Runnable detached(DetachEvent detachEvent) {
                listenerInvoked.set(true);
                return null;
            }
        };
        stc.addDetachListener(span2, detachListener);
        
        stc.removeDetachListeners(span2, detachListener);
        stc.detach(true);
        
        assertFalse(listenerInvoked.get());
    }

    @Test
    public void testRemoveAllContentChangeListenersAbstractHtml() {

        SharedTagContent stc = new SharedTagContent(UpdateClientNature.ALLOW_PARALLEL, "Test content", true);
        Div div = new Div(null);
        Span span1 = new Span(div);
        span1.addInnerHtml(stc);
        Span span2 = new Span(div);
        span2.addInnerHtml(stc);
        
        AtomicBoolean listenerInvoked = new AtomicBoolean();
        final SharedTagContent.ContentChangeListener contentChangeListener = new SharedTagContent.ContentChangeListener() {
            
            @Override
            public Runnable contentChanged(ChangeEvent changeEvent) {
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
        SharedTagContent stc = new SharedTagContent(UpdateClientNature.ALLOW_PARALLEL, "Test content", true);
        Div div = new Div(null);
        Span span1 = new Span(div);
        span1.addInnerHtml(stc);
        Span span2 = new Span(div);
        span2.addInnerHtml(stc);
        
        AtomicBoolean listenerInvoked = new AtomicBoolean();
        final SharedTagContent.ContentChangeListener contentChangeListener = new SharedTagContent.ContentChangeListener() {
            
            @Override
            public Runnable contentChanged(ChangeEvent changeEvent) {
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
        SharedTagContent stc = new SharedTagContent(UpdateClientNature.ALLOW_PARALLEL, "Test content", true);
        Div div = new Div(null);
        Span span1 = new Span(div);
        span1.addInnerHtml(stc);
        Span span2 = new Span(div);
        span2.addInnerHtml(stc);
        
        AtomicBoolean listenerInvoked = new AtomicBoolean();
        final SharedTagContent.DetachListener detachListener = new SharedTagContent.DetachListener() {
            
            @Override
            public Runnable detached(DetachEvent detachEvent) {
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

        SharedTagContent stc = new SharedTagContent(UpdateClientNature.ALLOW_PARALLEL, "Test content", true);
        Div div = new Div(null);
        Span span1 = new Span(div);
        span1.addInnerHtml(stc);
        Span span2 = new Span(div);
        span2.addInnerHtml(stc);
        
        AtomicBoolean listenerInvoked = new AtomicBoolean();
        final SharedTagContent.DetachListener detachListener = new SharedTagContent.DetachListener() {
            
            @Override
            public Runnable detached(DetachEvent detachEvent) {
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
        SharedTagContent stc = new SharedTagContent(UpdateClientNature.SEQUENTIAL, "Test Content", true);
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
        
        spanChild1.addInnerHtml(stc, new SharedTagContent.ContentFormatter() {
            
            @Override
            public SharedTagContent.Content format(SharedTagContent.Content content) {
                assertEquals("Test Content", content.getContent());
                return new SharedTagContent.Content("Formatted content", content.isContentTypeHtml());
            }
        });
        pChild2.addInnerHtml(stc, new SharedTagContent.ContentFormatter() {
            
            @Override
            public SharedTagContent.Content format(SharedTagContent.Content content) {
                assertEquals("Test Content", content.getContent());
                return new SharedTagContent.Content("Formatted content", content.isContentTypeHtml());
            }
        });
        assertEquals(stc, spanChild1.getSharedTagContent());
        assertEquals(stc, pChild2.getSharedTagContent());
    }
    
    @Test
    public void testContentFormatter() throws Exception {
        
        SharedTagContent stc = new SharedTagContent(UpdateClientNature.SEQUENTIAL, "Test Content", true);
        Div div = new Div(null);
        Span spanChild1 = new Span(div);
        spanChild1.addInnerHtml(stc);
        P pChild2 = new P(div);
        pChild2.addInnerHtml(stc);
        
        spanChild1.addInnerHtml(stc, new SharedTagContent.ContentFormatter() {
            
            @Override
            public SharedTagContent.Content format(SharedTagContent.Content content) {
                assertEquals("Test Content", content.getContent());
                return new SharedTagContent.Content("Formatted1 Content", content.isContentTypeHtml());
            }
        });
        
        pChild2.addInnerHtml(stc, new SharedTagContent.ContentFormatter() {
            
            @Override
            public SharedTagContent.Content format(SharedTagContent.Content content) {
                assertEquals("Test Content", content.getContent());
                return new SharedTagContent.Content("Formatted2 Content", content.isContentTypeHtml());
            }
        });
        assertEquals(stc, spanChild1.getSharedTagContent());
        assertEquals(stc, pChild2.getSharedTagContent());
        
        assertEquals("<span>Formatted1 Content</span>", spanChild1.toHtmlString());
        assertEquals("<p>Formatted2 Content</p>", pChild2.toHtmlString());        
        assertEquals("<div><span>Formatted1 Content</span><p>Formatted2 Content</p></div>", div.toHtmlString());
        
    }

}
