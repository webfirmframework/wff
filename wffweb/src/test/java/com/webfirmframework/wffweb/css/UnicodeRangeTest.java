package com.webfirmframework.wffweb.css;

import static org.junit.Assert.*;

import org.junit.Test;

public class UnicodeRangeTest {

    @Test
    public void testUnicodeRange() {
        UnicodeRange unicodeRange = new UnicodeRange();
        assertEquals(CssNameConstants.UNICODE_RANGE, unicodeRange.getCssName());
        assertEquals(UnicodeRange.DEFAULT_VALUE, unicodeRange.getCssValue());
        assertEquals("unicode-range:" + UnicodeRange.DEFAULT_VALUE, unicodeRange.toCssString());
    }

    @Test
    public void testUnicodeRangeString() {
        UnicodeRange unicodeRange = new UnicodeRange("U+0025-00FF");
        assertEquals("U+0025-00FF", unicodeRange.getCssValue());
    }

}
