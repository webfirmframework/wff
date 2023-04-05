package com.webfirmframework.wffweb.util;

import static org.junit.Assert.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
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

    @Test
    public void testParseValuesWithQueryParams() {
        Map<String, String> variableNameValues = URIUtil.parseValues("/some/uri/pathparam/{itemId}/yes", "/some/uri/pathparam/123/yes?name=wffweb");
        assertEquals(1, variableNameValues.size());
        assertEquals("123", variableNameValues.get("itemId"));

        variableNameValues = URIUtil.parseValues("/some/uri/pathparam/{itemId}/{userId}", "/some/uri/pathparam/123/1?name=wffweb");
        assertEquals(2, variableNameValues.size());
        assertEquals("123", variableNameValues.get("itemId"));
        assertEquals("1", variableNameValues.get("userId"));


        variableNameValues = URIUtil.parseValues("/some/uri/pathparam/{itemId}/gap/{userId}", "/some/uri/pathparam/123/gap/1?name=wffweb");
        assertEquals(2, variableNameValues.size());
        assertEquals("123", variableNameValues.get("itemId"));
        assertEquals("1", variableNameValues.get("userId"));

        variableNameValues = URIUtil.parseValues("/some/uri/pathparam/{itemId}/gap/{userId}/", "/some/uri/pathparam/123/gap/1/?name=wffweb");
        assertEquals(2, variableNameValues.size());
        assertEquals("123", variableNameValues.get("itemId"));
        assertEquals("1", variableNameValues.get("userId"));

        variableNameValues = URIUtil.parseValues("/some/uri/pathparam/{symbols}/gap?name=wffweb",
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
    public void testPatternMatchesBaseWithQueryParams() {
        assertTrue(URIUtil.patternMatchesBase("/some/uri/user/{userId}", "/some/uri/user/123/item/456?name=wffweb"));
        assertTrue(URIUtil.patternMatchesBase("/some/uri/user/{userId}/item/{itemId}", "/some/uri/user/123/item/456?name=wffweb"));
        assertTrue(URIUtil.patternMatchesBase("some/uri/user/{userId}/item/{itemId}", "some/uri/user/123/item/456?name=wffweb"));
        assertTrue(URIUtil.patternMatchesBase("/some/uri/user", "/some/uri/user/123/item/456?name=wffweb"));

        assertTrue(URIUtil.patternMatchesBase("someuri", "someuri?name=wffweb"));
        assertTrue(URIUtil.patternMatchesBase("someuri", "someuri/some/uri/user/123/item/456?name=wffweb"));

        assertFalse(URIUtil.patternMatchesBase("someuri", "some1uri/some/uri/user/123/item/456?name=wffweb"));
        assertFalse(URIUtil.patternMatchesBase("/some/uri/user/{userId}/item/{itemId}", "/some/uri/user/123?name=wffweb"));
        assertFalse(URIUtil.patternMatchesBase("/some/uri/user/{userId}/item/{itemId}", "/some/uri/user/123/item2/456?name=wffweb"));
        assertFalse(URIUtil.patternMatchesBase("some/uri/user/{userId}/item/{itemId}", "/some/uri/user/123/item/456?name=wffweb"));
        assertFalse(URIUtil.patternMatchesBase("/some/uri/user/{userId}/item/{itemId}", "some/uri/user/123/item/456?name=wffweb"));

        assertTrue(URIUtil.patternMatchesBase("/some/uri/pathparam/{userId}/[pathUri]/{itemId}", "/some/uri/pathparam/1/path1/path2/path3/2?name=wffweb"));
        assertTrue(URIUtil.patternMatchesBase("/some/uri/pathparam/{userId}/[pathUri]", "/some/uri/pathparam/1/path1/path2/path3/2?name=wffweb"));

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
    
    @Test
    public void testPatternMatchesWithQueryParams() {

        assertTrue(URIUtil.patternMatches("/some/uri/user/{userId}", "/some/uri/user/123?name=wffweb"));
        assertTrue(URIUtil.patternMatches("/some/uri/user/{userId}/item/{itemId}", "/some/uri/user/123/item/456?name=wffweb"));
        assertTrue(URIUtil.patternMatches("some/uri/user/{userId}/item/{itemId}", "some/uri/user/123/item/456?name=wffweb"));
        assertTrue(URIUtil.patternMatches("/some/uri/user", "/some/uri/user?name=wffweb"));
        assertTrue(URIUtil.patternMatches("someuri", "someuri?name=wffweb"));

        assertFalse(URIUtil.patternMatches("/some/uri/user/{userId}/item/{itemId}", "/some/uri/user/123?name=wffweb"));
        assertFalse(URIUtil.patternMatches("/some/uri/user/{userId}/item/{itemId}", "/some/uri/user/123/item2/456?name=wffweb"));
        assertFalse(URIUtil.patternMatches("some/uri/user/{userId}/item/{itemId}", "/some/uri/user/123/item/456?name=wffweb"));
        assertFalse(URIUtil.patternMatches("/some/uri/user/{userId}/item/{itemId}", "some/uri/user/123/item/456?name=wffweb"));

        assertTrue(URIUtil.patternMatches("/some/uri/pathparam/[pathUri]/gap/{userId}/", "/some/uri/pathparam/path1/path2/path3/gap/1/?name=wffweb"));
        assertTrue(URIUtil.patternMatches("/some/uri/pathparam/{userId}/gap/[pathUri]/{itemId}", "/some/uri/pathparam/1/gap/path1/path2/path3/2?name=wffweb"));
        assertTrue(URIUtil.patternMatches("/some/uri/pathparam/{userId}/[pathUri]/{itemId}", "/some/uri/pathparam/1/path1/path2/path3/2?name=wffweb"));
        assertTrue(URIUtil.patternMatches("/some/uri/pathparam/[pathUri]/{itemId}", "/some/uri/pathparam/1/path1/path2/path3/2?name=wffweb"));
        assertTrue(URIUtil.patternMatches("/some/uri/pathparam/[pathUri]", "/some/uri/pathparam/1/path1/path2/path3/2?name=wffweb"));
        assertTrue(URIUtil.patternMatches("/some/uri/pathparam/{itemId}/[pathUri]", "/some/uri/pathparam/2/path1/path2/path3/2?name=wffweb"));
        assertTrue(URIUtil.patternMatches("/some/uri/pathparam/{itemId}/[pathUri]/", "/some/uri/pathparam/2/path1/path2/path3/2/?name=wffweb"));
        assertTrue(URIUtil.patternMatches("/some/uri/pathparam/{userId}/[pathUri]/{itemId}/", "/some/uri/pathparam/1/path1/path2/path3/2/?name=wffweb"));


        assertFalse(URIUtil.patternMatches("someuri", "someuri/?name=wffweb"));
        assertFalse(URIUtil.patternMatches("someuri", "/someuri?name=wffweb"));
        assertFalse(URIUtil.patternMatches("someuri", "/someuri/?name=wffweb"));
        assertFalse(URIUtil.patternMatches("/some/uri/pathparam/[pathUri]/gap/{userId}/", "/some/uri/pathparam/path1/path2/path3/gap/1?name=wffweb"));
        assertFalse(URIUtil.patternMatches("/some/uri/pathparam/[pathUri]/gap/{userId}", "/some/uri/pathparam/path1/path2/path3/gap/1/?name=wffweb"));
        assertTrue(URIUtil.patternMatches("/some/uri/pathparam/{userId}/gap/[pathUri]/{itemId}", "/some/uri/pathparam/1/gap/path1/path2/path3/2/?name=wffweb"));


        assertFalse(URIUtil.patternMatches("/some/uri/pathparam/{userId}/somethingelse/[pathUri]/{itemId}", "/some/uri/pathparam/1/path1/path2/path3/?name=wffweb"));
        assertFalse(URIUtil.patternMatches("some/uri/pathparam/[pathUri]/{itemId}", "/some/uri/pathparam/1/path1/path2/path3/2?name=wffweb"));
        assertFalse(URIUtil.patternMatches("/1some/uri/pathparam/[pathUri]", "/some/uri/pathparam/1/path1/path2/path3/2?name=wffweb"));
        assertFalse(URIUtil.patternMatches("/some/uri/pathparam/{itemId}/[pathUri]", "/1some/uri/pathparam/2/path1/path2/path3/2?name=wffweb"));
        assertFalse(URIUtil.patternMatches("/some/uri/pathparam/{itemId}/[pathUri]/another", "/some/uri/pathparam/2/path1/path2/path3/2/?name=wffweb"));
        assertFalse(URIUtil.patternMatches("/some/uri/pathparam/{userId}/[pathUri]/{itemId}/", "/some/uri/pathparam/1/path1/path2/path3/2?name=wffweb"));

    }

    @Test
    public void testParse() {
        ParsedURI parsedURI = URIUtil.parse("/some/uri/pathparam/{itemId}/yes", "/some/uri/pathparam/123/yes?name=wffweb&name2&name3=&name3");
        assertNotNull(parsedURI);
        assertEquals(1, parsedURI.pathParameters().size());
        assertEquals(3, parsedURI.queryParameters().size());
        assertEquals(List.of("wffweb"), parsedURI.queryParameters().get("name"));
        assertEquals(List.of(""), parsedURI.queryParameters().get("name2"));
        assertEquals(List.of("", ""), parsedURI.queryParameters().get("name3"));
        assertEquals("", parsedURI.hash());
        
        parsedURI = URIUtil.parse("/some/uri/pathparam/{itemId}/yes", "/some/uri/pathparam/123/yes?name=wffweb&name2&name3=&name3&");
        assertNotNull(parsedURI);
        assertEquals(1, parsedURI.pathParameters().size());
        assertEquals(3, parsedURI.queryParameters().size());
        assertEquals(List.of("wffweb"), parsedURI.queryParameters().get("name"));
        assertEquals(List.of(""), parsedURI.queryParameters().get("name2"));
        assertEquals(List.of("", ""), parsedURI.queryParameters().get("name3"));
        assertEquals("", parsedURI.hash());


        parsedURI = URIUtil.parse("/some/uri/pathparam/{itemId}/yes", "/some/uri/pathparam/123/yes?");
        assertNotNull(parsedURI);
        assertEquals(1, parsedURI.pathParameters().size());
        assertEquals(0, parsedURI.queryParameters().size());
        assertEquals("", parsedURI.hash());

        parsedURI = URIUtil.parse("/some/uri/pathparam/{itemId}/yes", "/some/uri/pathparam/123/yes?name=wffweb&name2&name3=&name3#hashpart");
        assertNotNull(parsedURI);
        assertEquals(1, parsedURI.pathParameters().size());
        assertEquals(3, parsedURI.queryParameters().size());
        assertEquals(List.of("wffweb"), parsedURI.queryParameters().get("name"));
        assertEquals(List.of(""), parsedURI.queryParameters().get("name2"));
        assertEquals(List.of("", ""), parsedURI.queryParameters().get("name3"));
        assertEquals("hashpart", parsedURI.hash());
        
        parsedURI = URIUtil.parse("/some/uri/pathparam/{itemId}/yes", "/some/uri/pathparam/123/yes#hashpart2");
        assertEquals("/some/uri/pathparam/123/yes", parsedURI.pathname());
        
        parsedURI = URIUtil.parse("/some/uri/pathparam/{itemId}/yes", "/some/uri/pathparam/123/yes?name=wffweb&name2&name3=&name3#hashpart1#hashpart2");
        assertEquals("/some/uri/pathparam/123/yes", parsedURI.pathname());
        //in Chrome the location.hash contains this value if multiple # symbol is found
        assertEquals("hashpart1#hashpart2", parsedURI.hash());
        
        parsedURI = URIUtil.parse("/some/uri/pathparam/{itemId}/yes", "/some/uri/pathparam/123/yes#hashpart");
        assertEquals("hashpart", parsedURI.hash());
    }
    
    @Test
    public void testToURIInfo() {
        URIUtil.URIInfo uriInfo = null;

        uriInfo = URIUtil.toURIInfo("/some/uri/pathparam/{itemId}/yes");
        assertEquals("/some/uri/pathparam/{itemId}/yes", uriInfo.pathname());
        assertEquals("", uriInfo.queryString());
        assertEquals("", uriInfo.hash());

        uriInfo = URIUtil.toURIInfo("/some/uri/pathparam/123/yes?name=wffweb&name2&name3=&name3");
        assertEquals("/some/uri/pathparam/123/yes", uriInfo.pathname());
        assertEquals("name=wffweb&name2&name3=&name3", uriInfo.queryString());
        assertEquals("", uriInfo.hash());

        uriInfo = URIUtil.toURIInfo("/some/uri/pathparam/123/yes#hashpart2");
        assertEquals("/some/uri/pathparam/123/yes", uriInfo.pathname());
        assertEquals("hashpart2", uriInfo.hash());
        assertEquals("", uriInfo.queryString());

        uriInfo = URIUtil.toURIInfo("/some/uri/pathparam/123/yes#");
        assertEquals("/some/uri/pathparam/123/yes", uriInfo.pathname());
        assertEquals("", uriInfo.hash());
        assertEquals("", uriInfo.queryString());

        uriInfo = URIUtil.toURIInfo("/some/uri/pathparam/123/yes?name=wffweb#");
        assertEquals("/some/uri/pathparam/123/yes", uriInfo.pathname());
        assertEquals("", uriInfo.hash());
        assertEquals("name=wffweb", uriInfo.queryString());

        uriInfo = URIUtil.toURIInfo("/some/uri/pathparam/123/yes?name=wffweb");
        assertEquals("/some/uri/pathparam/123/yes", uriInfo.pathname());
        assertEquals("", uriInfo.hash());
        assertEquals("name=wffweb", uriInfo.queryString());

        uriInfo = URIUtil.toURIInfo("/some/uri/pathparam/123/yes#?name=wffweb");
        assertEquals("/some/uri/pathparam/123/yes", uriInfo.pathname());
        assertEquals("?name=wffweb", uriInfo.hash());
        assertEquals("", uriInfo.queryString());

        uriInfo = URIUtil.toURIInfo("/some/uri/pathparam/123/yes?name=wffweb");

        assertEquals("/some/uri/pathparam/123/yes", uriInfo.pathname());
        assertEquals("", uriInfo.hash());
        assertEquals("name=wffweb", uriInfo.queryString());

        uriInfo = URIUtil.toURIInfo("/some/uri/pathparam/123/yes?name=wffweb#hashpart2");

        assertEquals("/some/uri/pathparam/123/yes", uriInfo.pathname());
        assertEquals("hashpart2", uriInfo.hash());
        assertEquals("name=wffweb", uriInfo.queryString());

        uriInfo = URIUtil.toURIInfo("/some/uri/pathparam/123/yes#hashpart2?name=wffweb");

        assertEquals("/some/uri/pathparam/123/yes", uriInfo.pathname());
        assertEquals("hashpart2?name=wffweb", uriInfo.hash());
        assertEquals("", uriInfo.queryString());
    }
}
