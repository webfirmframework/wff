package com.webfirmframework.wffweb.tag.html.attributewff;

import static org.junit.Assert.*;

import org.junit.Test;

public class ImmutableCustomAttributeTest {

    @Test
    public void testImmutableCustomAttributeString() {
        ImmutableCustomAttribute attribute = new ImmutableCustomAttribute("attr-name");
        assertEquals("attr-name", attribute.toHtmlString());
    }

    @Test
    public void testImmutableCustomAttributeStringString() {
        ImmutableCustomAttribute attribute = new ImmutableCustomAttribute("attr-name", "attr-value");
        assertEquals("attr-name=\"attr-value\"", attribute.toHtmlString());
    }

    @Test
    public void testGetValue() {
        ImmutableCustomAttribute attribute = new ImmutableCustomAttribute("attr-name", "attr-value");
        assertEquals("attr-value", attribute.getValue());
        assertEquals("attr-value", attribute.getAttributeValue());
        assertEquals("attr-name", attribute.getAttributeName());        
    }

}
