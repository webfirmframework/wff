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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;

import org.junit.Test;

public class NumberUtilTest {

    @Test
    public void testIsStrictInt() {

        final String intValue1 = String.valueOf(Integer.MAX_VALUE);
        final String intValue2 = String.valueOf(Integer.MIN_VALUE);

        assertTrue(NumberUtil.isStrictInt(intValue1));
        assertTrue(NumberUtil.isStrictInt(intValue2));
        assertTrue(NumberUtil.isStrictInt(String.valueOf(Integer.MAX_VALUE - 1)));
        assertTrue(NumberUtil.isStrictInt(String.valueOf(Integer.MIN_VALUE + 1)));
        assertTrue(NumberUtil.isStrictInt("-1401"));
        assertTrue(NumberUtil.isStrictInt("1401"));
        assertTrue(NumberUtil.isStrictInt("0"));
        assertTrue(NumberUtil.isStrictInt("1"));

        assertFalse(NumberUtil.isStrictInt(null));
        assertFalse(NumberUtil.isStrictInt(""));
        assertFalse(NumberUtil.isStrictInt("-"));
        assertFalse(NumberUtil.isStrictInt("--"));
        assertFalse(NumberUtil.isStrictInt("+"));
        assertFalse(NumberUtil.isStrictInt("++"));
        assertFalse(NumberUtil.isStrictInt("-+"));
        assertFalse(NumberUtil.isStrictInt("00"));
        assertFalse(NumberUtil.isStrictInt("000"));
        assertFalse(NumberUtil.isStrictInt("14-01"));
        assertFalse(NumberUtil.isStrictInt("1401-"));
        assertFalse(NumberUtil.isStrictInt("+1401"));
        assertFalse(NumberUtil.isStrictInt("0123456789"));
        assertFalse(NumberUtil.isStrictInt("-01401"));
        assertFalse(NumberUtil.isStrictInt(intValue1.concat("0")));
        assertFalse(NumberUtil.isStrictInt(intValue2.concat("0")));
        assertFalse(NumberUtil.isStrictInt(intValue2.concat("3147483647")));
        assertFalse(NumberUtil.isStrictInt(intValue2.concat("2247483647")));
        assertFalse(NumberUtil.isStrictInt(intValue2.concat("2157483647")));
        assertFalse(NumberUtil.isStrictInt(intValue2.concat("2148483647")));
        assertFalse(NumberUtil.isStrictInt(intValue2.concat("2147583647")));
        assertFalse(NumberUtil.isStrictInt(intValue2.concat("2147493647")));
        assertFalse(NumberUtil.isStrictInt(intValue2.concat("2147484647")));
        assertFalse(NumberUtil.isStrictInt(intValue2.concat("2147483747")));
        assertFalse(NumberUtil.isStrictInt(intValue2.concat("2147483657")));
        assertFalse(NumberUtil.isStrictInt(intValue2.concat("2147483648")));

        assertFalse(NumberUtil.isStrictInt(intValue2.concat("-3147483648")));
        assertFalse(NumberUtil.isStrictInt(intValue2.concat("-2247483648")));
        assertFalse(NumberUtil.isStrictInt(intValue2.concat("-2157483648")));
        assertFalse(NumberUtil.isStrictInt(intValue2.concat("-2148483648")));
        assertFalse(NumberUtil.isStrictInt(intValue2.concat("-2147583648")));
        assertFalse(NumberUtil.isStrictInt(intValue2.concat("-2147493648")));
        assertFalse(NumberUtil.isStrictInt(intValue2.concat("-2147484648")));
        assertFalse(NumberUtil.isStrictInt(intValue2.concat("-2147483748")));
        assertFalse(NumberUtil.isStrictInt(intValue2.concat("-2147483658")));
        assertFalse(NumberUtil.isStrictInt(intValue2.concat("-2147483649")));

        assertFalse(NumberUtil.isStrictInt(String.valueOf(Integer.MIN_VALUE - 1L)));
        assertFalse(NumberUtil.isStrictInt(String.valueOf(Integer.MAX_VALUE + 1L)));
        assertFalse(NumberUtil.isStrictInt(String.valueOf(Long.MIN_VALUE)));
        assertFalse(NumberUtil.isStrictInt(String.valueOf(Long.MAX_VALUE)));
    }

    @Test
    public void testIsStrictLong() {

        final String intValue1 = String.valueOf(Long.MAX_VALUE);
        final String intValue2 = String.valueOf(Long.MIN_VALUE);

        assertTrue(NumberUtil.isStrictLong(intValue1));
        assertTrue(NumberUtil.isStrictLong(intValue2));
        assertTrue(NumberUtil.isStrictLong(String.valueOf(Long.MAX_VALUE - 1)));
        assertTrue(NumberUtil.isStrictLong(String.valueOf(Long.MIN_VALUE + 1)));
        assertTrue(NumberUtil.isStrictLong("-1401"));
        assertTrue(NumberUtil.isStrictLong("1401"));
        assertTrue(NumberUtil.isStrictLong("0"));
        assertTrue(NumberUtil.isStrictLong("1"));

        assertFalse(NumberUtil.isStrictLong(null));
        assertFalse(NumberUtil.isStrictLong(""));
        assertFalse(NumberUtil.isStrictLong("-"));
        assertFalse(NumberUtil.isStrictLong("--"));
        assertFalse(NumberUtil.isStrictLong("+"));
        assertFalse(NumberUtil.isStrictLong("++"));
        assertFalse(NumberUtil.isStrictLong("-+"));
        assertFalse(NumberUtil.isStrictLong("00"));
        assertFalse(NumberUtil.isStrictLong("000"));
        assertFalse(NumberUtil.isStrictLong("14-01"));
        assertFalse(NumberUtil.isStrictLong("1401-"));
        assertFalse(NumberUtil.isStrictLong("+1401"));
        assertFalse(NumberUtil.isStrictLong("0123456789"));
        assertFalse(NumberUtil.isStrictLong("-01401"));
        assertFalse(NumberUtil.isStrictLong(intValue1.concat("0")));
        assertFalse(NumberUtil.isStrictLong(intValue2.concat("0")));
        assertFalse(NumberUtil.isStrictLong(intValue2.concat("3147483647")));
        assertFalse(NumberUtil.isStrictLong(intValue2.concat("2247483647")));
        assertFalse(NumberUtil.isStrictLong(intValue2.concat("2157483647")));
        assertFalse(NumberUtil.isStrictLong(intValue2.concat("2148483647")));
        assertFalse(NumberUtil.isStrictLong(intValue2.concat("2147583647")));
        assertFalse(NumberUtil.isStrictLong(intValue2.concat("2147493647")));
        assertFalse(NumberUtil.isStrictLong(intValue2.concat("2147484647")));
        assertFalse(NumberUtil.isStrictLong(intValue2.concat("2147483747")));
        assertFalse(NumberUtil.isStrictLong(intValue2.concat("2147483657")));
        assertFalse(NumberUtil.isStrictLong(intValue2.concat("2147483648")));

        assertFalse(NumberUtil.isStrictLong(intValue2.concat("-3147483648")));
        assertFalse(NumberUtil.isStrictLong(intValue2.concat("-2247483648")));
        assertFalse(NumberUtil.isStrictLong(intValue2.concat("-2157483648")));
        assertFalse(NumberUtil.isStrictLong(intValue2.concat("-2148483648")));
        assertFalse(NumberUtil.isStrictLong(intValue2.concat("-2147583648")));
        assertFalse(NumberUtil.isStrictLong(intValue2.concat("-2147493648")));
        assertFalse(NumberUtil.isStrictLong(intValue2.concat("-2147484648")));
        assertFalse(NumberUtil.isStrictLong(intValue2.concat("-2147483748")));
        assertFalse(NumberUtil.isStrictLong(intValue2.concat("-2147483658")));
        assertFalse(NumberUtil.isStrictLong(intValue2.concat("-2147483649")));

        assertFalse(NumberUtil
                .isStrictLong(new BigInteger(String.valueOf(Long.MIN_VALUE)).subtract(BigInteger.ONE).toString()));
        assertFalse(
                NumberUtil.isStrictLong(new BigInteger(String.valueOf(Long.MAX_VALUE)).add(BigInteger.ONE).toString()));
    }

}
