package com.webfirmframework.wffweb.util;

import static org.junit.Assert.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.junit.Test;

import com.webfirmframework.wffweb.InvalidValueException;

public class URIUtilTest {


    @Test
    public void testParseValuesWithURIPartVariable() {
        Map<String, String> variableNameValues = null;

        variableNameValues = URIUtil.parseValues("/some/uri/pathparam/{itemId}/yes", "/some/uri/pathparam/123/yes");
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

        variableNameValues = URIUtil.parseValues("/some/uri/pathparam/{symbols}/gap",
                "/some/uri/pathparam/" + URLEncoder.encode("{}[]!@#$%^&*", StandardCharsets.UTF_8) + "/gap");
        assertEquals(1, variableNameValues.size());
        assertEquals("{}[]!@#$%^&*", variableNameValues.get("symbols"));

        //new
        variableNameValues = URIUtil.parseValues("/some/uri/pathparam/[pathUri]/gap/{userId}/", "/some/uri/pathparam/path1/path2/path3/gap/1/");
        assertEquals(2, variableNameValues.size());
        assertEquals("path1/path2/path3", variableNameValues.get("pathUri"));
        assertEquals("1", variableNameValues.get("userId"));

        variableNameValues = URIUtil.parseValues("/some/uri/pathparam/{userId}/gap/[pathUri]/", "/some/uri/pathparam/1/gap/path1/path2/path3/");
        assertEquals(2, variableNameValues.size());
        assertEquals("path1/path2/path3", variableNameValues.get("pathUri"));
        assertEquals("1", variableNameValues.get("userId"));

        variableNameValues = URIUtil.parseValues("/some/uri/pathparam/{userId}/gap/[pathUri]/{itemId}",
                "/some/uri/pathparam/1/gap/path1/path2/path3/2");
        assertEquals(3, variableNameValues.size());
        assertEquals("path1/path2/path3", variableNameValues.get("pathUri"));
        assertEquals("1", variableNameValues.get("userId"));
        assertEquals("2", variableNameValues.get("itemId"));

        variableNameValues = URIUtil.parseValues(
                "/some/uri/pathparam/{userId}/[pathUri]/{itemId}",
                "/some/uri/pathparam/1/path1/path2/path3/2");
        assertEquals(3, variableNameValues.size());
        assertEquals("path1/path2/path3", variableNameValues.get("pathUri"));
        assertEquals("1", variableNameValues.get("userId"));
        assertEquals("2", variableNameValues.get("itemId"));

        variableNameValues = URIUtil.parseValues(
                "/some/uri/pathparam/[pathUri]/{itemId}",
                "/some/uri/pathparam/1/path1/path2/path3/2");
        assertEquals(2, variableNameValues.size());
        assertEquals("1/path1/path2/path3", variableNameValues.get("pathUri"));
        assertEquals("2", variableNameValues.get("itemId"));

        variableNameValues = URIUtil.parseValues(
                "/some/uri/pathparam/[pathUri]",
                "/some/uri/pathparam/1/path1/path2/path3/2");
        assertEquals(1, variableNameValues.size());
        assertEquals("1/path1/path2/path3/2", variableNameValues.get("pathUri"));

        variableNameValues = URIUtil.parseValues(
                "/some/uri/pathparam/[pathUri]",
                "/some/uri/pathparam/1/path1/path2/path3/2/");
        assertEquals(1, variableNameValues.size());
        assertEquals("1/path1/path2/path3/2/", variableNameValues.get("pathUri"));

        variableNameValues = URIUtil.parseValues(
                "/some/uri/pathparam/{itemId}/[pathUri]",
                "/some/uri/pathparam/2/path1/path2/path3/2");
        assertEquals(2, variableNameValues.size());
        assertEquals("2", variableNameValues.get("itemId"));
        assertEquals("path1/path2/path3/2", variableNameValues.get("pathUri"));

        variableNameValues = URIUtil.parseValues(
                "/some/uri/pathparam/{itemId}/[pathUri]/",
                "/some/uri/pathparam/2/path1/path2/path3/2/");
        assertEquals(2, variableNameValues.size());
        assertEquals("2", variableNameValues.get("itemId"));
        assertEquals("path1/path2/path3/2", variableNameValues.get("pathUri"));

        variableNameValues = URIUtil.parseValues(
                "/some/uri/pathparam/{userId}/[pathUri]/{itemId}/",
                "/some/uri/pathparam/1/path1/path2/path3/2/");
        assertEquals(3, variableNameValues.size());
        assertEquals("path1/path2/path3", variableNameValues.get("pathUri"));
        assertEquals("1", variableNameValues.get("userId"));
        assertEquals("2", variableNameValues.get("itemId"));

        variableNameValues = URIUtil.parseValues(
                "/some/uri/pathparam/{userId}/[pathUri1]/{pathUri2}/",
                "/some/uri/pathparam/1/path1/path2/path3/2/");
        assertEquals(3, variableNameValues.size());
        assertEquals("1", variableNameValues.get("userId"));
        assertEquals("path1/path2/path3", variableNameValues.get("pathUri1"));
        assertEquals("2", variableNameValues.get("pathUri2"));

        variableNameValues = URIUtil.parseValues(
                "/some/uri/pathparam/{userId}/[pathUri1]/{pathUri2}",
                "/some/uri/pathparam/1/path1/path2/path3/2");
        assertEquals(3, variableNameValues.size());
        assertEquals("1", variableNameValues.get("userId"));
        assertEquals("path1/path2/path3", variableNameValues.get("pathUri1"));
        assertEquals("2", variableNameValues.get("pathUri2"));

        variableNameValues = URIUtil.parseValues(
                "/some/uri/pathparam/{userId}/[pathUri1]/{pathUri2}/uri1",
                "/some/uri/pathparam/1/path1/path2/path3/2/uri1");
        assertEquals(3, variableNameValues.size());
        assertEquals("1", variableNameValues.get("userId"));
        assertEquals("path1/path2/path3", variableNameValues.get("pathUri1"));
        assertEquals("2", variableNameValues.get("pathUri2"));
    }

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
        
        variableNameValues = URIUtil.parseValues("/some/uri/pathparam/{symbols}/gap",
                "/some/uri/pathparam/" + URLEncoder.encode("{}[]!@#$%^&*", StandardCharsets.UTF_8) + "/gap");
        assertEquals(1, variableNameValues.size());
        assertEquals("{}[]!@#$%^&*", variableNameValues.get("symbols"));
        
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

    @Test(expected = InvalidValueException.class)
    public void testParseValuesException6() {
        URIUtil.parseValues("/some/uri/pathparam/[pathUri]/gap/{userId}/", "/some/uri/pathparam/path1/path2/path3/gap/1");
    }

    @Test(expected = InvalidValueException.class)
    public void testParseValuesException7() {
        URIUtil.parseValues("/some/uri/pathparam/[pathUri]/gap/{userId}", "/some/uri/pathparam/path1/path2/path3/gap/1/");
    }

    @Test(expected = InvalidValueException.class)
    public void testParseValuesException8() {
        URIUtil.parseValues("some/uri/pathparam/[pathUri]/gap/{userId}", "/some/uri/pathparam/path1/path2/path3/gap/1");
    }

    @Test(expected = InvalidValueException.class)
    public void testParseValuesException9() {
        URIUtil.parseValues("/some/uri/pathparam/[pathUri]/gap/{userId}", "some/uri/pathparam/path1/path2/path3/gap/1");
    }

    @Test(expected = InvalidValueException.class)
    public void testParseValuesException10() {
        URIUtil.parseValues("/some/uri/pathparam/{userId}/[pathUri]/{itemId}/", "/some/uri/pathparam/1/path1/path2/path3/2");
    }

    @Test(expected = InvalidValueException.class)
    public void testParseValuesException11() {
        URIUtil.parseValues("/some/uri/pathparam/{userId}/[userId]/{itemId}/", "/some/uri/pathparam/1/path1/path2/path3/2");
    }

    @Test(expected = InvalidValueException.class)
    public void testParseValuesException12() {
        URIUtil.parseValues("/some/uri/pathparam/{userId}/[pathUri]/{userId}/", "/some/uri/pathparam/1/path1/path2/path3/2");
    }

    @Test(expected = InvalidValueException.class)
    public void testParseValuesException13() {
        URIUtil.parseValues("/some/uri/pathparam/{userId}/[pathUri]/[pathUri]/", "/some/uri/pathparam/1/path1/path2/path3/2");
    }
    
    @Test(expected = InvalidValueException.class)
    public void testParseValuesException14() {
        URIUtil.parseValues("/some/uri/pathparam/[pathUri]/gap/{userId}", "/some/uri/pathparam/path1/path2/path3/gap1/1");
    }
    
    @Test(expected = InvalidValueException.class)
    public void testParseValuesException15() {
        URIUtil.parseValues("/some/uri/pathparam/[pathUri]/gap", "/some/uri/pathparam/path1/path2/path3");
    }
    
    @Test(expected = InvalidValueException.class)
    public void testParseValuesException16() {
        URIUtil.parseValues("/some/uri/pathparam/[pathUri]/gap", "/some/uri/pathparam/path1/path2/path3/");
    }
    
    @Test(expected = InvalidValueException.class)
    public void testParseValuesException17() {
        URIUtil.parseValues("/some/uri/pathparam/[pathUri]/gap/", "/some/uri/pathparam/path1/path2/path3");
    }

    @Test
    public void testPatternMatchesBase() {
        assertTrue(URIUtil.patternMatchesBase("/some/uri/user/{userId}", "/some/uri/user/123/item/456"));
        assertTrue(URIUtil.patternMatchesBase("/some/uri/user/{userId}/item/{itemId}", "/some/uri/user/123/item/456"));
        assertTrue(URIUtil.patternMatchesBase("some/uri/user/{userId}/item/{itemId}", "some/uri/user/123/item/456"));
        assertTrue(URIUtil.patternMatchesBase("/some/uri/user", "/some/uri/user/123/item/456"));

        assertTrue(URIUtil.patternMatchesBase("someuri", "someuri"));
        assertTrue(URIUtil.patternMatchesBase("someuri", "someuri/some/uri/user/123/item/456"));

        assertFalse(URIUtil.patternMatchesBase("someuri", "some1uri/some/uri/user/123/item/456"));
        assertFalse(URIUtil.patternMatchesBase("/some/uri/user/{userId}/item/{itemId}", "/some/uri/user/123"));
        assertFalse(URIUtil.patternMatchesBase("/some/uri/user/{userId}/item/{itemId}", "/some/uri/user/123/item2/456"));
        assertFalse(URIUtil.patternMatchesBase("some/uri/user/{userId}/item/{itemId}", "/some/uri/user/123/item/456"));
        assertFalse(URIUtil.patternMatchesBase("/some/uri/user/{userId}/item/{itemId}", "some/uri/user/123/item/456"));

        assertTrue(URIUtil.patternMatchesBase("/some/uri/pathparam/{userId}/[pathUri]/{itemId}", "/some/uri/pathparam/1/path1/path2/path3/2"));
        assertTrue(URIUtil.patternMatchesBase("/some/uri/pathparam/{userId}/[pathUri]", "/some/uri/pathparam/1/path1/path2/path3/2"));
        
    }
    
    @Test
    public void testPatternMatches() {
        assertTrue(URIUtil.patternMatches("/some/uri/user/{userId}", "/some/uri/user/123"));
        assertTrue(URIUtil.patternMatches("/some/uri/user/{userId}/item/{itemId}", "/some/uri/user/123/item/456"));
        assertTrue(URIUtil.patternMatches("some/uri/user/{userId}/item/{itemId}", "some/uri/user/123/item/456"));
        assertTrue(URIUtil.patternMatches("/some/uri/user", "/some/uri/user"));
        assertTrue(URIUtil.patternMatches("someuri", "someuri"));

        assertFalse(URIUtil.patternMatches("/some/uri/user/{userId}/item/{itemId}", "/some/uri/user/123"));
        assertFalse(URIUtil.patternMatches("/some/uri/user/{userId}/item/{itemId}", "/some/uri/user/123/item2/456"));
        assertFalse(URIUtil.patternMatches("some/uri/user/{userId}/item/{itemId}", "/some/uri/user/123/item/456"));
        assertFalse(URIUtil.patternMatches("/some/uri/user/{userId}/item/{itemId}", "some/uri/user/123/item/456"));

        assertTrue(URIUtil.patternMatches("/some/uri/pathparam/[pathUri]/gap/{userId}/", "/some/uri/pathparam/path1/path2/path3/gap/1/"));
        assertTrue(URIUtil.patternMatches("/some/uri/pathparam/{userId}/gap/[pathUri]/{itemId}", "/some/uri/pathparam/1/gap/path1/path2/path3/2"));
        assertTrue(URIUtil.patternMatches("/some/uri/pathparam/{userId}/[pathUri]/{itemId}", "/some/uri/pathparam/1/path1/path2/path3/2"));
        assertTrue(URIUtil.patternMatches("/some/uri/pathparam/[pathUri]/{itemId}", "/some/uri/pathparam/1/path1/path2/path3/2"));
        assertTrue(URIUtil.patternMatches("/some/uri/pathparam/[pathUri]", "/some/uri/pathparam/1/path1/path2/path3/2"));
        assertTrue(URIUtil.patternMatches("/some/uri/pathparam/{itemId}/[pathUri]", "/some/uri/pathparam/2/path1/path2/path3/2"));
        assertTrue(URIUtil.patternMatches("/some/uri/pathparam/{itemId}/[pathUri]/", "/some/uri/pathparam/2/path1/path2/path3/2/"));
        assertTrue(URIUtil.patternMatches("/some/uri/pathparam/{userId}/[pathUri]/{itemId}/", "/some/uri/pathparam/1/path1/path2/path3/2/"));


        assertFalse(URIUtil.patternMatches("someuri", "someuri/"));
        assertFalse(URIUtil.patternMatches("someuri", "/someuri"));
        assertFalse(URIUtil.patternMatches("someuri", "/someuri/"));
        assertFalse(URIUtil.patternMatches("/some/uri/pathparam/[pathUri]/gap/{userId}/", "/some/uri/pathparam/path1/path2/path3/gap/1"));
        assertFalse(URIUtil.patternMatches("/some/uri/pathparam/[pathUri]/gap/{userId}", "/some/uri/pathparam/path1/path2/path3/gap/1/"));
        assertTrue(URIUtil.patternMatches("/some/uri/pathparam/{userId}/gap/[pathUri]/{itemId}", "/some/uri/pathparam/1/gap/path1/path2/path3/2/"));


        assertFalse(URIUtil.patternMatches("/some/uri/pathparam/{userId}/somethingelse/[pathUri]/{itemId}", "/some/uri/pathparam/1/path1/path2/path3/"));
        assertFalse(URIUtil.patternMatches("some/uri/pathparam/[pathUri]/{itemId}", "/some/uri/pathparam/1/path1/path2/path3/2"));
        assertFalse(URIUtil.patternMatches("/1some/uri/pathparam/[pathUri]", "/some/uri/pathparam/1/path1/path2/path3/2"));
        assertFalse(URIUtil.patternMatches("/some/uri/pathparam/{itemId}/[pathUri]", "/1some/uri/pathparam/2/path1/path2/path3/2"));
        assertFalse(URIUtil.patternMatches("/some/uri/pathparam/{itemId}/[pathUri]/another", "/some/uri/pathparam/2/path1/path2/path3/2/"));
        assertFalse(URIUtil.patternMatches("/some/uri/pathparam/{userId}/[pathUri]/{itemId}/", "/some/uri/pathparam/1/path1/path2/path3/2"));


    }
}
