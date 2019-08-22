package com.webfirmframework.wffweb.tag.html;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;

import com.webfirmframework.wffweb.tag.html.SharedTagContent.UpdateClientNature;
import com.webfirmframework.wffweb.tag.html.stylesandsemantics.Div;
import com.webfirmframework.wffweb.tag.html.stylesandsemantics.Span;

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
//        fail("Not yet implemented");
    }

    @Test
    public void testDetachBooleanSetOfAbstractHtmlSetOfAbstractHtml() {
//        fail("Not yet implemented");
    }

    @Test
    public void testAddContentChangeListener() {
//        fail("Not yet implemented");
    }

    @Test
    public void testAddDetachListener() {
//        fail("Not yet implemented");
    }

    @Test
    public void testRemoveContentChangeListenerContentChangeListener() {
//        fail("Not yet implemented");
    }

    @Test
    public void testRemoveContentChangeListenerAbstractHtmlContentChangeListener() {
//        fail("Not yet implemented");
    }

    @Test
    public void testRemoveDetachListenerDetachListener() {
//        fail("Not yet implemented");
    }

    @Test
    public void testRemoveDetachListenerAbstractHtmlDetachListener() {
//        fail("Not yet implemented");
    }

    @Test
    public void testRemoveContentChangeListeners() {
//        fail("Not yet implemented");
    }

    @Test
    public void testRemoveDetachListeners() {
//        fail("Not yet implemented");
    }

    @Test
    public void testRemoveAllContentChangeListenersAbstractHtml() {
//        fail("Not yet implemented");
    }

    @Test
    public void testRemoveAllContentChangeListeners() {
//        fail("Not yet implemented");
    }

    @Test
    public void testRemoveAllDetachListenersAbstractHtml() {
//        fail("Not yet implemented");
    }

    @Test
    public void testRemoveAllDetachListeners() {
//        fail("Not yet implemented");
    }

}
