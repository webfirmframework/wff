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
package com.webfirmframework.wffweb.json;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class JsonCodePointUtilTest {

    @Test
    public void testSplitByAnyRangeBoundFullString() {

        List<String> expectedValues = new ArrayList<>();
        List<String> actualValues = new ArrayList<>();
        int[] codePoints = null;
        int[][] parts = null;

        codePoints = "one:two".codePoints().toArray();
        parts = JsonCodePointUtil.splitByAnyRangeBound(codePoints, 0, codePoints.length - 1, ":".codePointAt(0));
        expectedValues.add("one");
        expectedValues.add("two");

        for (int[] each : parts) {
            actualValues.add(new String(each, 0, each.length));
        }
        Assert.assertArrayEquals(expectedValues.toArray(), actualValues.toArray());


        expectedValues.clear();
        actualValues.clear();
        codePoints = ":two".codePoints().toArray();
        parts = JsonCodePointUtil.splitByAnyRangeBound(codePoints, 0, codePoints.length - 1, ":".codePointAt(0));
        expectedValues.add("");
        expectedValues.add("two");

        for (int[] each : parts) {
            actualValues.add(new String(each, 0, each.length));
        }
        Assert.assertArrayEquals(expectedValues.toArray(), actualValues.toArray());


        expectedValues.clear();
        actualValues.clear();
        codePoints = "one:".codePoints().toArray();
        parts = JsonCodePointUtil.splitByAnyRangeBound(codePoints, 0, codePoints.length - 1, ":".codePointAt(0));
        expectedValues.add("one");
        expectedValues.add("");

        for (int[] each : parts) {
            actualValues.add(new String(each, 0, each.length));
        }
        Assert.assertArrayEquals(expectedValues.toArray(), actualValues.toArray());


        expectedValues.clear();
        actualValues.clear();
        codePoints = ":".codePoints().toArray();
        parts = JsonCodePointUtil.splitByAnyRangeBound(codePoints, 0, codePoints.length - 1, ":".codePointAt(0));
        expectedValues.add("");
        expectedValues.add("");

        for (int[] each : parts) {
            actualValues.add(new String(each, 0, each.length));
        }
        Assert.assertArrayEquals(expectedValues.toArray(), actualValues.toArray());


        expectedValues.clear();
        actualValues.clear();
        codePoints = "one;two;three".codePoints().toArray();
        parts = JsonCodePointUtil.splitByAnyRangeBound(codePoints, 0, codePoints.length - 1, ":".codePointAt(0));
        expectedValues.add("one;two;three");

        for (int[] each : parts) {
            actualValues.add(new String(each, 0, each.length));
        }
        Assert.assertArrayEquals(expectedValues.toArray(), actualValues.toArray());
    }

    @Test
    public void testSplitByAnyRangeBoundSubstring() {

        List<String> expectedValues = new ArrayList<>();
        List<String> actualValues = new ArrayList<>();
        int[] codePoints = null;
        int[][] parts = null;

        codePoints = "1one:two".codePoints().toArray();
        parts = JsonCodePointUtil.splitByAnyRangeBound(codePoints, 1, codePoints.length - 1, ":".codePointAt(0));
        expectedValues.add("one");
        expectedValues.add("two");

        for (int[] each : parts) {
            actualValues.add(new String(each, 0, each.length));
        }
        Assert.assertArrayEquals(expectedValues.toArray(), actualValues.toArray());


        expectedValues.clear();
        actualValues.clear();
        codePoints = "1one:two:three".codePoints().toArray();
        parts = JsonCodePointUtil.splitByAnyRangeBound(codePoints, 1, codePoints.length - 1, ":".codePointAt(0));
        expectedValues.add("one");
        expectedValues.add("two");
        expectedValues.add("three");

        for (int[] each : parts) {
            actualValues.add(new String(each, 0, each.length));
        }
        Assert.assertArrayEquals(expectedValues.toArray(), actualValues.toArray());


        expectedValues.clear();
        actualValues.clear();
        codePoints = "one:two1".codePoints().toArray();
        parts = JsonCodePointUtil.splitByAnyRangeBound(codePoints, 0, codePoints.length - 2, ":".codePointAt(0));
        expectedValues.add("one");
        expectedValues.add("two");

        for (int[] each : parts) {
            actualValues.add(new String(each, 0, each.length));
        }
        Assert.assertArrayEquals(expectedValues.toArray(), actualValues.toArray());


        expectedValues.clear();
        actualValues.clear();
        codePoints = "one:two:three1".codePoints().toArray();
        parts = JsonCodePointUtil.splitByAnyRangeBound(codePoints, 0, codePoints.length - 2, ":".codePointAt(0));
        expectedValues.add("one");
        expectedValues.add("two");
        expectedValues.add("three");

        for (int[] each : parts) {
            actualValues.add(new String(each, 0, each.length));
        }
        Assert.assertArrayEquals(expectedValues.toArray(), actualValues.toArray());



        expectedValues.clear();
        actualValues.clear();
        codePoints = "1one:two1".codePoints().toArray();
        parts = JsonCodePointUtil.splitByAnyRangeBound(codePoints, 1, codePoints.length - 2, ":".codePointAt(0));
        expectedValues.add("one");
        expectedValues.add("two");

        for (int[] each : parts) {
            actualValues.add(new String(each, 0, each.length));
        }
        Assert.assertArrayEquals(expectedValues.toArray(), actualValues.toArray());


        expectedValues.clear();
        actualValues.clear();
        codePoints = "1one:two:three1".codePoints().toArray();
        parts = JsonCodePointUtil.splitByAnyRangeBound(codePoints, 1, codePoints.length - 2, ":".codePointAt(0));
        expectedValues.add("one");
        expectedValues.add("two");
        expectedValues.add("three");

        for (int[] each : parts) {
            actualValues.add(new String(each, 0, each.length));
        }
        Assert.assertArrayEquals(expectedValues.toArray(), actualValues.toArray());


        expectedValues.clear();
        actualValues.clear();
        codePoints = "123one:two123".codePoints().toArray();
        parts = JsonCodePointUtil.splitByAnyRangeBound(codePoints, 3, codePoints.length - 4, ":".codePointAt(0));
        expectedValues.add("one");
        expectedValues.add("two");

        for (int[] each : parts) {
            actualValues.add(new String(each, 0, each.length));
        }
        Assert.assertArrayEquals(expectedValues.toArray(), actualValues.toArray());


        expectedValues.clear();
        actualValues.clear();
        codePoints = "123one:two:three123".codePoints().toArray();
        parts = JsonCodePointUtil.splitByAnyRangeBound(codePoints, 3, codePoints.length - 4, ":".codePointAt(0));
        expectedValues.add("one");
        expectedValues.add("two");
        expectedValues.add("three");

        for (int[] each : parts) {
            actualValues.add(new String(each, 0, each.length));
        }
        Assert.assertArrayEquals(expectedValues.toArray(), actualValues.toArray());


        expectedValues.clear();
        actualValues.clear();
        codePoints = ":two".codePoints().toArray();
        parts = JsonCodePointUtil.splitByAnyRangeBound(codePoints, 0, codePoints.length - 1, ":".codePointAt(0));
        expectedValues.add("");
        expectedValues.add("two");

        for (int[] each : parts) {
            actualValues.add(new String(each, 0, each.length));
        }
        Assert.assertArrayEquals(expectedValues.toArray(), actualValues.toArray());


        expectedValues.clear();
        actualValues.clear();
        codePoints = ":two:three".codePoints().toArray();
        parts = JsonCodePointUtil.splitByAnyRangeBound(codePoints, 0, codePoints.length - 1, ":".codePointAt(0));
        expectedValues.add("");
        expectedValues.add("two");
        expectedValues.add("three");

        for (int[] each : parts) {
            actualValues.add(new String(each, 0, each.length));
        }
        Assert.assertArrayEquals(expectedValues.toArray(), actualValues.toArray());


        expectedValues.clear();
        actualValues.clear();
        codePoints = "one:".codePoints().toArray();
        parts = JsonCodePointUtil.splitByAnyRangeBound(codePoints, 0, codePoints.length - 1, ":".codePointAt(0));
        expectedValues.add("one");
        expectedValues.add("");

        for (int[] each : parts) {
            actualValues.add(new String(each, 0, each.length));
        }
        Assert.assertArrayEquals(expectedValues.toArray(), actualValues.toArray());


        expectedValues.clear();
        actualValues.clear();
        codePoints = "one:two:".codePoints().toArray();
        parts = JsonCodePointUtil.splitByAnyRangeBound(codePoints, 0, codePoints.length - 1, ":".codePointAt(0));
        expectedValues.add("one");
        expectedValues.add("two");
        expectedValues.add("");

        for (int[] each : parts) {
            actualValues.add(new String(each, 0, each.length));
        }
        Assert.assertArrayEquals(expectedValues.toArray(), actualValues.toArray());


        expectedValues.clear();
        actualValues.clear();
        codePoints = "one:two:three:".codePoints().toArray();
        parts = JsonCodePointUtil.splitByAnyRangeBound(codePoints, 0, codePoints.length - 1, ":".codePointAt(0));
        expectedValues.add("one");
        expectedValues.add("two");
        expectedValues.add("three");
        expectedValues.add("");

        for (int[] each : parts) {
            actualValues.add(new String(each, 0, each.length));
        }
        Assert.assertArrayEquals(expectedValues.toArray(), actualValues.toArray());

        expectedValues.clear();
        actualValues.clear();
        codePoints = ":".codePoints().toArray();
        parts = JsonCodePointUtil.splitByAnyRangeBound(codePoints, 0, codePoints.length - 1, ":".codePointAt(0));
        expectedValues.add("");
        expectedValues.add("");

        for (int[] each : parts) {
            actualValues.add(new String(each, 0, each.length));
        }
        Assert.assertArrayEquals(expectedValues.toArray(), actualValues.toArray());


        expectedValues.clear();
        actualValues.clear();
        codePoints = "one;two;three".codePoints().toArray();
        parts = JsonCodePointUtil.splitByAnyRangeBound(codePoints, 3, codePoints.length - 4, ":".codePointAt(0));
        expectedValues.add(";two;th");

        for (int[] each : parts) {
            actualValues.add(new String(each, 0, each.length));
        }
        Assert.assertArrayEquals(expectedValues.toArray(), actualValues.toArray());


    }

    @Test
    public void testSplitByAnyRangeBoundSubstring2() {
        int[] startEndIndices = {9, 337};
//        int[] codePoints = ;
    }

    @Test
    public void testFindFirstAndLastNonWhitespaceIndices() {
        int[] indices = JsonCodePointUtil.findFirstAndLastNonWhitespaceIndices("    ".codePoints().toArray());
        Assert.assertNull(indices);

        indices = JsonCodePointUtil.findFirstAndLastNonWhitespaceIndices("  1 1  ".codePoints().toArray());
        Assert.assertNotNull(indices);
        Assert.assertArrayEquals(new int[] {2, 4}, indices);

        indices = JsonCodePointUtil.findFirstAndLastNonWhitespaceIndices("1 1".codePoints().toArray());
        Assert.assertNotNull(indices);
        Assert.assertArrayEquals(new int[] {0, 2}, indices);

        indices = JsonCodePointUtil.findFirstAndLastNonWhitespaceIndices(" 1 1".codePoints().toArray());
        Assert.assertNotNull(indices);
        Assert.assertArrayEquals(new int[] {1, 3}, indices);

        indices = JsonCodePointUtil.findFirstAndLastNonWhitespaceIndices("1 1 ".codePoints().toArray());
        Assert.assertNotNull(indices);
        Assert.assertArrayEquals(new int[] {0, 2}, indices);


    }

    @Test
    public void testToString() {
        Assert.assertEquals("", JsonCodePointUtil.toString("".codePoints().toArray(), null));
    }

    @Test
    public void testIsValidUnicodeEscapeSequence() {
        final String[] validUnicodeValues = {
                "\\ud83D",
                "\\uDe80",
                "\\u0123",
                "\\u4567",
                "\\u89AB",
                "\\uCDEF",
                "\\uabcd",
                "\\uef01",
        };
        for (String unicodeValue : validUnicodeValues) {
            final int[] codePoints = unicodeValue.codePoints().toArray();
            Assert.assertTrue(JsonCodePointUtil.isValidUnicodeEscapeSequence(codePoints, 0, 5));
        }

        final String[] invalidUnicodeValues = {
                "\\ud83G",
                "\\uDe8g",
                "\\u012H",
                "\\u456h",
                "\\u89AI",
                "\\uCDEi",
                "\\uabcJ",
                "\\uef0j",
                "\\uef0K",
                "\\uef0k",
                "\\uef0L",
                "\\uef0l",
                "\\uef0M",
                "\\uef0m",
                "\\uef0N",
                "\\uef0n",
                "\\uef0O",
                "\\uef0o",
                "\\uef0P",
                "\\uef0p",
                "\\uef0Q",
                "\\uef0q",
                "\\uef0R",
                "\\uef0r",
                "\\uef0S",
                "\\uef0s",
                "\\uef0T",
                "\\uef0t",
                "\\uef0U",
                "\\uef0u",
                "\\uef0V",
                "\\uef0v",
                "\\uef0W",
                "\\uef0w",
                "\\uef0X",
                "\\uef0Y",
                "\\uef0y",
                "\\uef0Z",
                "\\uef0z",
        };
        for (String invalidUnicodeValue : invalidUnicodeValues) {
            final int[] codePoints = invalidUnicodeValue.codePoints().toArray();
            Assert.assertFalse(JsonCodePointUtil.isValidUnicodeEscapeSequence(codePoints, 0, 5));
        }
    }

    @Test
    public void testIsStrictLong1() {

        final String intValue1 = String.valueOf(Long.MAX_VALUE);
        final String intValue2 = String.valueOf(Long.MIN_VALUE);

        assertTrue(JsonCodePointUtil.isStrictLong(intValue1.codePoints().toArray(), null));
        assertTrue(JsonCodePointUtil.isStrictLong(intValue2.codePoints().toArray(), null));
        assertTrue(JsonCodePointUtil.isStrictLong(String.valueOf(Long.MAX_VALUE - 1).codePoints().toArray(), null));
        assertTrue(JsonCodePointUtil.isStrictLong(String.valueOf(Long.MIN_VALUE + 1).codePoints().toArray(), null));
        assertTrue(JsonCodePointUtil.isStrictLong("-1401".codePoints().toArray(), null));
        assertTrue(JsonCodePointUtil.isStrictLong("1401".codePoints().toArray(), null));
        assertTrue(JsonCodePointUtil.isStrictLong("0".codePoints().toArray(), null));
        assertTrue(JsonCodePointUtil.isStrictLong("1".codePoints().toArray(), null));

        assertFalse(JsonCodePointUtil.isStrictLong(null, null));
        assertFalse(JsonCodePointUtil.isStrictLong("".codePoints().toArray(), null));
        assertFalse(JsonCodePointUtil.isStrictLong("-".codePoints().toArray(), null));
        assertFalse(JsonCodePointUtil.isStrictLong("--".codePoints().toArray(), null));
        assertFalse(JsonCodePointUtil.isStrictLong("+".codePoints().toArray(), null));
        assertFalse(JsonCodePointUtil.isStrictLong("++".codePoints().toArray(), null));
        assertFalse(JsonCodePointUtil.isStrictLong("-+".codePoints().toArray(), null));
        assertFalse(JsonCodePointUtil.isStrictLong("00".codePoints().toArray(), null));
        assertFalse(JsonCodePointUtil.isStrictLong("000".codePoints().toArray(), null));
        assertFalse(JsonCodePointUtil.isStrictLong("14-01".codePoints().toArray(), null));
        assertFalse(JsonCodePointUtil.isStrictLong("1401-".codePoints().toArray(), null));
        assertFalse(JsonCodePointUtil.isStrictLong("+1401".codePoints().toArray(), null));
        assertFalse(JsonCodePointUtil.isStrictLong("0123456789".codePoints().toArray(), null));
        assertFalse(JsonCodePointUtil.isStrictLong("-01401".codePoints().toArray(), null));
        assertFalse(JsonCodePointUtil.isStrictLong(intValue1.concat("0").codePoints().toArray(), null));
        assertFalse(JsonCodePointUtil.isStrictLong(intValue2.concat("0").codePoints().toArray(), null));
        assertFalse(JsonCodePointUtil.isStrictLong(intValue2.concat("3147483647").codePoints().toArray(), null));
        assertFalse(JsonCodePointUtil.isStrictLong(intValue2.concat("2247483647").codePoints().toArray(), null));
        assertFalse(JsonCodePointUtil.isStrictLong(intValue2.concat("2157483647").codePoints().toArray(), null));
        assertFalse(JsonCodePointUtil.isStrictLong(intValue2.concat("2148483647").codePoints().toArray(), null));
        assertFalse(JsonCodePointUtil.isStrictLong(intValue2.concat("2147583647").codePoints().toArray(), null));
        assertFalse(JsonCodePointUtil.isStrictLong(intValue2.concat("2147493647").codePoints().toArray(), null));
        assertFalse(JsonCodePointUtil.isStrictLong(intValue2.concat("2147484647").codePoints().toArray(), null));
        assertFalse(JsonCodePointUtil.isStrictLong(intValue2.concat("2147483747").codePoints().toArray(), null));
        assertFalse(JsonCodePointUtil.isStrictLong(intValue2.concat("2147483657").codePoints().toArray(), null));
        assertFalse(JsonCodePointUtil.isStrictLong(intValue2.concat("2147483648").codePoints().toArray(), null));

        assertFalse(JsonCodePointUtil.isStrictLong(intValue2.concat("-3147483648").codePoints().toArray(), null));
        assertFalse(JsonCodePointUtil.isStrictLong(intValue2.concat("-2247483648").codePoints().toArray(), null));
        assertFalse(JsonCodePointUtil.isStrictLong(intValue2.concat("-2157483648").codePoints().toArray(), null));
        assertFalse(JsonCodePointUtil.isStrictLong(intValue2.concat("-2148483648").codePoints().toArray(), null));
        assertFalse(JsonCodePointUtil.isStrictLong(intValue2.concat("-2147583648").codePoints().toArray(), null));
        assertFalse(JsonCodePointUtil.isStrictLong(intValue2.concat("-2147493648").codePoints().toArray(), null));
        assertFalse(JsonCodePointUtil.isStrictLong(intValue2.concat("-2147484648").codePoints().toArray(), null));
        assertFalse(JsonCodePointUtil.isStrictLong(intValue2.concat("-2147483748").codePoints().toArray(), null));
        assertFalse(JsonCodePointUtil.isStrictLong(intValue2.concat("-2147483658").codePoints().toArray(), null));
        assertFalse(JsonCodePointUtil.isStrictLong(intValue2.concat("-2147483649").codePoints().toArray(), null));

        assertFalse(JsonCodePointUtil
                .isStrictLong(new BigInteger(String.valueOf(Long.MIN_VALUE)).subtract(BigInteger.ONE).toString().codePoints().toArray(), null));
        assertFalse(
                JsonCodePointUtil.isStrictLong(new BigInteger(String.valueOf(Long.MAX_VALUE)).add(BigInteger.ONE).toString().codePoints().toArray(), null));
    }

    @Test
    public void testIsStrictLong2() {

        final String intValue1 = String.valueOf(Long.MAX_VALUE);
        final String intValue2 = String.valueOf(Long.MIN_VALUE);
        String wrapper = " %s ";

        assertTrue(JsonCodePointUtil.isStrictLong(wrapper.formatted(intValue1).codePoints().toArray(), new int[] { 1, intValue1.length() }));
        assertTrue(JsonCodePointUtil.isStrictLong(wrapper.formatted(intValue2).codePoints().toArray(), new int[] { 1, intValue2.length() }));
        assertTrue(JsonCodePointUtil.isStrictLong(wrapper.formatted(String.valueOf(Long.MAX_VALUE - 1)).codePoints().toArray(), new int[] { 1, String.valueOf(Long.MAX_VALUE - 1).length() }));
        assertTrue(JsonCodePointUtil.isStrictLong(wrapper.formatted(String.valueOf(Long.MIN_VALUE + 1)).codePoints().toArray(), new int[] { 1, String.valueOf(Long.MIN_VALUE + 1).length() }));
        assertTrue(JsonCodePointUtil.isStrictLong(wrapper.formatted("-1401").codePoints().toArray(), new int[] { 1, "-1401".length() }));
        assertTrue(JsonCodePointUtil.isStrictLong(wrapper.formatted("1401").codePoints().toArray(), new int[] { 1, "1401".length() }));
        assertTrue(JsonCodePointUtil.isStrictLong(wrapper.formatted("0").codePoints().toArray(), new int[] { 1, "0".length() }));
        assertTrue(JsonCodePointUtil.isStrictLong(wrapper.formatted("1").codePoints().toArray(), new int[] { 1, "1".length() }));

        assertFalse(JsonCodePointUtil.isStrictLong(null, null));
        assertFalse(JsonCodePointUtil.isStrictLong("".codePoints().toArray(), null));
        assertFalse(JsonCodePointUtil.isStrictLong(wrapper.formatted("-").codePoints().toArray(), new int[] { 1, "-".length() }));
        assertFalse(JsonCodePointUtil.isStrictLong(wrapper.formatted("--").codePoints().toArray(), new int[] { 1, "--".length() }));
        assertFalse(JsonCodePointUtil.isStrictLong(wrapper.formatted("+").codePoints().toArray(), new int[] { 1, "+".length() }));
        assertFalse(JsonCodePointUtil.isStrictLong(wrapper.formatted("++").codePoints().toArray(), new int[] { 1, "++".length() }));
        assertFalse(JsonCodePointUtil.isStrictLong(wrapper.formatted("-+").codePoints().toArray(), new int[] { 1, "-+".length() }));
        assertFalse(JsonCodePointUtil.isStrictLong(wrapper.formatted("00").codePoints().toArray(), new int[] { 1, "00".length() }));
        assertFalse(JsonCodePointUtil.isStrictLong(wrapper.formatted("000").codePoints().toArray(), new int[] { 1, "000".length() }));
        assertFalse(JsonCodePointUtil.isStrictLong(wrapper.formatted("14-01").codePoints().toArray(), new int[] { 1, "14-01".length() }));
        assertFalse(JsonCodePointUtil.isStrictLong(wrapper.formatted("1401-").codePoints().toArray(), new int[] { 1, "1401-".length() }));
        assertFalse(JsonCodePointUtil.isStrictLong(wrapper.formatted("+1401").codePoints().toArray(), new int[] { 1, "+1401".length() }));
        assertFalse(JsonCodePointUtil.isStrictLong(wrapper.formatted("0123456789").codePoints().toArray(), new int[] { 1, "0123456789".length() }));
        assertFalse(JsonCodePointUtil.isStrictLong(wrapper.formatted("-01401").codePoints().toArray(), new int[] { 1, "-01401".length() }));
        assertFalse(JsonCodePointUtil.isStrictLong(wrapper.formatted(intValue1.concat("0")).codePoints().toArray(), new int[] { 1, intValue1.concat("0").length() }));
        assertFalse(JsonCodePointUtil.isStrictLong(wrapper.formatted(intValue2.concat("0")).codePoints().toArray(), new int[] { 1, intValue2.concat("0").length() }));
        assertFalse(JsonCodePointUtil.isStrictLong(wrapper.formatted(intValue2.concat("3147483647")).codePoints().toArray(), new int[] { 1, intValue2.concat("3147483647").length() }));
        assertFalse(JsonCodePointUtil.isStrictLong(wrapper.formatted(intValue2.concat("2247483647")).codePoints().toArray(), new int[] { 1, intValue2.concat("2247483647").length() }));
        assertFalse(JsonCodePointUtil.isStrictLong(wrapper.formatted(intValue2.concat("2157483647")).codePoints().toArray(), new int[] { 1, intValue2.concat("2157483647").length() }));
        assertFalse(JsonCodePointUtil.isStrictLong(wrapper.formatted(intValue2.concat("2148483647")).codePoints().toArray(), new int[] { 1, intValue2.concat("2148483647").length() }));
        assertFalse(JsonCodePointUtil.isStrictLong(wrapper.formatted(intValue2.concat("2147583647")).codePoints().toArray(), new int[] { 1, intValue2.concat("2147583647").length() }));
        assertFalse(JsonCodePointUtil.isStrictLong(wrapper.formatted(intValue2.concat("2147493647")).codePoints().toArray(), new int[] { 1, intValue2.concat("2147493647").length() }));
        assertFalse(JsonCodePointUtil.isStrictLong(wrapper.formatted(intValue2.concat("2147484647")).codePoints().toArray(), new int[] { 1, intValue2.concat("2147484647").length() }));
        assertFalse(JsonCodePointUtil.isStrictLong(wrapper.formatted(intValue2.concat("2147483747")).codePoints().toArray(), new int[] { 1, intValue2.concat("2147483747").length() }));
        assertFalse(JsonCodePointUtil.isStrictLong(wrapper.formatted(intValue2.concat("2147483657")).codePoints().toArray(), new int[] { 1, intValue2.concat("2147483657").length() }));
        assertFalse(JsonCodePointUtil.isStrictLong(wrapper.formatted(intValue2.concat("2147483648")).codePoints().toArray(), new int[] { 1, intValue2.concat("2147483648").length() }));

        assertFalse(JsonCodePointUtil.isStrictLong(wrapper.formatted(intValue2.concat("-3147483648")).codePoints().toArray(), new int[] { 1, intValue2.concat("-3147483648").length() }));
        assertFalse(JsonCodePointUtil.isStrictLong(wrapper.formatted(intValue2.concat("-2247483648")).codePoints().toArray(), new int[] { 1, intValue2.concat("-2247483648").length() }));
        assertFalse(JsonCodePointUtil.isStrictLong(wrapper.formatted(intValue2.concat("-2157483648")).codePoints().toArray(), new int[] { 1, intValue2.concat("-2157483648").length() }));
        assertFalse(JsonCodePointUtil.isStrictLong(wrapper.formatted(intValue2.concat("-2148483648")).codePoints().toArray(), new int[] { 1, intValue2.concat("-2148483648").length() }));
        assertFalse(JsonCodePointUtil.isStrictLong(wrapper.formatted(intValue2.concat("-2147583648")).codePoints().toArray(), new int[] { 1, intValue2.concat("-2147583648").length() }));
        assertFalse(JsonCodePointUtil.isStrictLong(wrapper.formatted(intValue2.concat("-2147493648")).codePoints().toArray(), new int[] { 1, intValue2.concat("-2147493648").length() }));
        assertFalse(JsonCodePointUtil.isStrictLong(wrapper.formatted(intValue2.concat("-2147484648")).codePoints().toArray(), new int[] { 1, intValue2.concat("-2147484648").length() }));
        assertFalse(JsonCodePointUtil.isStrictLong(wrapper.formatted(intValue2.concat("-2147483748")).codePoints().toArray(), new int[] { 1, intValue2.concat("-2147483748").length() }));
        assertFalse(JsonCodePointUtil.isStrictLong(wrapper.formatted(intValue2.concat("-2147483658")).codePoints().toArray(), new int[] { 1, intValue2.concat("-2147483658").length() }));
        assertFalse(JsonCodePointUtil.isStrictLong(wrapper.formatted(intValue2.concat("-2147483649")).codePoints().toArray(), new int[] { 1, intValue2.concat("-2147483649").length() }));

        assertFalse(JsonCodePointUtil
                .isStrictLong(wrapper.formatted(new BigInteger(String.valueOf(Long.MIN_VALUE)).subtract(BigInteger.ONE).toString()).codePoints().toArray(),
                        new int[] { 1, new BigInteger(String.valueOf(Long.MIN_VALUE)).subtract(BigInteger.ONE).toString().length() }));
        assertFalse(
                JsonCodePointUtil.isStrictLong(wrapper.formatted(new BigInteger(String.valueOf(Long.MAX_VALUE)).add(BigInteger.ONE).toString()).codePoints().toArray(),
                        new int[] { 1, new BigInteger(String.valueOf(Long.MAX_VALUE)).add(BigInteger.ONE).toString().length() }));
    }

}
