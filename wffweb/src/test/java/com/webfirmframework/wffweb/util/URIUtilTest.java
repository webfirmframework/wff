package com.webfirmframework.wffweb.util;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

import com.webfirmframework.wffweb.InvalidValueException;

public class URIUtilTest {

    @Test
    public void testParseValues() {
        Map<String, String> variableNameValues = URIUtil.parseValues("/some/uri/pathparam/{itemId}/yes", "/some/uri/pathparam/123/yes");
        assertEquals(1, variableNameValues.size());
        assertEquals("123", variableNameValues.get("itemId"));
        
        variableNameValues = URIUtil.parseValues("/some/uri/pathparam/{itemId}/{userId}", "/some/uri/pathparam/123/1");
        assertEquals(2, variableNameValues.size());
        assertEquals("123", variableNameValues.get("itemId"));
        assertEquals("1", variableNameValues.get("userId"));
        
        
        variableNameValues = URIUtil.parseValues("/some/uri/pathparam/{itemId}/gap/{userId}", "/some/uri/pathparam/123/gap/1");
        assertEquals(2, variableNameValues.size());
        assertEquals("123", variableNameValues.get("itemId"));
        assertEquals("1", variableNameValues.get("userId"));
        
        variableNameValues = URIUtil.parseValues("/some/uri/pathparam/{itemId}/gap/{userId}/", "/some/uri/pathparam/123/gap/1/");
        assertEquals(2, variableNameValues.size());
        assertEquals("123", variableNameValues.get("itemId"));
        assertEquals("1", variableNameValues.get("userId"));
    }
    
    @Test(expected = InvalidValueException.class)
    public void testParseValuesException1() {
        URIUtil.parseValues("/some/uri/pathparam/{itemId}/yes/", "/some/uri/pathparam/123/yes");
    }
    
    @Test(expected = InvalidValueException.class)
    public void testParseValuesException2() {
        URIUtil.parseValues("/some/uri/pathparam/{itemId}/yes", "/some/uri/pathparam/123/yes/");
    }
    
    @Test(expected = InvalidValueException.class)
    public void testParseValuesException3() {
        URIUtil.parseValues("/some/uri/pathparam/{itemId}/{itemId}", "/some/uri/pathparam/123/456");
    }
    
    @Test(expected = InvalidValueException.class)
    public void testParseValuesException4() {
        URIUtil.parseValues("/another/uri/pathparam/{itemId}/yes", "/some/uri/pathparam/123/yes");
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void testParseValuesException5() {
        URIUtil.parseValues("/some/uri/pathparam/{itemId}/yes", "/some/uri/pathparam/123/yes").put("some", "someValue");
    }

}
