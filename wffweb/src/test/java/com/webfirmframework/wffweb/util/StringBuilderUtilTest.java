package com.webfirmframework.wffweb.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class StringBuilderUtilTest {

    @Test
    public void testGetTrimmedString1() {
        StringBuilder builder = new StringBuilder();
        builder.append("         ");
        builder.append(StringUtil.join(' ', "hi", "how", "are", "you"));
        builder.append("         ");        
        
        assertEquals("hi how are you", StringBuilderUtil.getTrimmedString(builder));
    }
    
    @Test
    public void testGetTrimmedString2() {
        StringBuilder builder = new StringBuilder();
        builder.append("         ");
        builder.append("  \t");
        builder.append(" \n");
        builder.append("         ");
        
        assertEquals("", StringBuilderUtil.getTrimmedString(builder));
    }
    
    @Test
    public void testReplaceFirst() {
        StringBuilder builder = new StringBuilder("This is some long sentance. This is done only once.");
        assertEquals("Replaced by that is some long sentance. This is done only once.", StringBuilderUtil.replaceFirst(builder, "This", "Replaced by that").toString());
        
        builder = new StringBuilder("Ones is 1");        
        assertEquals("Ones are 1", StringBuilderUtil.replaceFirst(builder, "is", "are").toString());
        builder = new StringBuilder("One are 1");        
        assertEquals("One is 1", StringBuilderUtil.replaceFirst(builder, "are", "is").toString());
    }
    
    @Test
    public void testStrip() {
        StringBuilder builder = new StringBuilder("  This is some long sentance.   ");
        assertEquals("This is some long sentance.", StringBuilderUtil.strip(builder).toString());
        
        builder = new StringBuilder("\n This is some long sentance. \n ");
        assertEquals("This is some long sentance.", StringBuilderUtil.strip(builder).toString());
        
        builder = new StringBuilder("\t This is some long sentance. \t ");
        assertEquals("This is some long sentance.", StringBuilderUtil.strip(builder).toString());
        
        builder = new StringBuilder(" This is some long sentance. ");
        assertEquals("This is some long sentance.", StringBuilderUtil.strip(builder).toString());
        
        builder = new StringBuilder("    This is some long sentance.");
        assertEquals("This is some long sentance.", StringBuilderUtil.strip(builder).toString());
        
        builder = new StringBuilder("This is some long sentance.    ");
        assertEquals("This is some long sentance.", StringBuilderUtil.strip(builder).toString());
        
        builder = new StringBuilder("This is some long sentance.");
        assertEquals("This is some long sentance.", StringBuilderUtil.strip(builder).toString());
        
        String unicodeChar = "😀";
        
        builder = new StringBuilder("    😀 is an imoji.    ");
        assertEquals("😀 is an imoji.", StringBuilderUtil.strip(builder).toString());
        
        builder = new StringBuilder("    This sentence contains 😀 as an imoji.    ");
        assertEquals("This sentence contains 😀 as an imoji.", StringBuilderUtil.strip(builder).toString());
    }


}
