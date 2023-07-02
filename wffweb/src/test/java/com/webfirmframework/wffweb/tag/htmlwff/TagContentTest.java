package com.webfirmframework.wffweb.tag.htmlwff;

import static org.junit.Assert.*;

import org.junit.Test;

import com.webfirmframework.wffweb.tag.html.stylesandsemantics.Div;

public class TagContentTest {

    @Test
    public void testTextIfRequired() {
        
        Div div = new Div(null);
        
        assertEquals("<div></div>", div.toHtmlString());
        
        div.give(TagContent::textIfRequired, "content1");
        assertEquals("<div>content1</div>", div.toHtmlString());
        assertTrue(div.getFirstChild() instanceof NoTag noTag && !noTag.isChildContentTypeHtml());
        
        div.give(TagContent::textIfRequired, "content1");
        assertEquals("<div>content1</div>", div.toHtmlString());
        assertTrue(div.getFirstChild() instanceof NoTag noTag && !noTag.isChildContentTypeHtml());
        
        div.give(TagContent::textIfRequired, "content2");
        assertEquals("<div>content2</div>", div.toHtmlString());
        assertTrue(div.getFirstChild() instanceof NoTag noTag && !noTag.isChildContentTypeHtml());
        
        div.give(TagContent::textIfRequired, (String) null);
        assertEquals("<div>null</div>", div.toHtmlString());
        assertTrue(div.getFirstChild() instanceof NoTag noTag && !noTag.isChildContentTypeHtml());
        
        div.give(TagContent::textIfRequired, "content1");
        assertEquals("<div>content1</div>", div.toHtmlString());
        assertTrue(div.getFirstChild() instanceof NoTag noTag && !noTag.isChildContentTypeHtml());
        
        div.give(TagContent::htmlIfRequired, "content1");
        assertEquals("<div>content1</div>", div.toHtmlString());
        assertTrue(div.getFirstChild() instanceof NoTag noTag && noTag.isChildContentTypeHtml());
        
    }

    @Test
    public void testHtmlIfRequired() {

        Div div = new Div(null);
        
        assertEquals("<div></div>", div.toHtmlString());
        
        div.give(TagContent::htmlIfRequired, "content1");
        assertEquals("<div>content1</div>", div.toHtmlString());
        assertTrue(div.getFirstChild() instanceof NoTag noTag && noTag.isChildContentTypeHtml());
        
        div.give(TagContent::htmlIfRequired, "content1");
        assertEquals("<div>content1</div>", div.toHtmlString());
        assertTrue(div.getFirstChild() instanceof NoTag noTag && noTag.isChildContentTypeHtml());
        
        div.give(TagContent::htmlIfRequired, "content2");
        assertEquals("<div>content2</div>", div.toHtmlString());
        assertTrue(div.getFirstChild() instanceof NoTag noTag && noTag.isChildContentTypeHtml());
        
        div.give(TagContent::htmlIfRequired, (String) null);
        assertEquals("<div>null</div>", div.toHtmlString());
        assertTrue(div.getFirstChild() instanceof NoTag noTag && noTag.isChildContentTypeHtml());
        
        div.give(TagContent::htmlIfRequired, "content1");
        assertEquals("<div>content1</div>", div.toHtmlString());
        assertTrue(div.getFirstChild() instanceof NoTag noTag && noTag.isChildContentTypeHtml());
        
        div.give(TagContent::textIfRequired, "content1");
        assertEquals("<div>content1</div>", div.toHtmlString());
        assertTrue(div.getFirstChild() instanceof NoTag noTag && !noTag.isChildContentTypeHtml());
        
    }

}
