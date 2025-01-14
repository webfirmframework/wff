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
        
        assertEquals("hi how are you", StringBuilderUtil.getTrimmedString(new StringBuilder("hi how are you")));
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
        
        builder = new StringBuilder("    😀 is an imoji.    ");
        assertEquals("😀 is an imoji.", StringBuilderUtil.strip(builder).toString());
        
        builder = new StringBuilder("    This sentence contains 😀 as an imoji.    ");
        assertEquals("This sentence contains 😀 as an imoji.", StringBuilderUtil.strip(builder).toString());
        
        builder = new StringBuilder("");
        assertEquals("", StringBuilderUtil.strip(builder).toString());
        
        builder = new StringBuilder("    ");
        assertEquals("", StringBuilderUtil.strip(builder).toString());
        
        assertEquals("hi how are you", StringBuilderUtil.strip(new StringBuilder("hi how are you")).toString());
        
        assertEquals("hi how are you😀", StringBuilderUtil.strip(new StringBuilder("hi how are you😀")).toString());

    }


}
