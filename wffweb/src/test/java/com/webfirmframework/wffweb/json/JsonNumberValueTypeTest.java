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

import java.math.BigDecimal;

public class JsonNumberValueTypeTest {

    @Test
    public void testParseAUTO_INTEGER_LONG_BIG_DECIMAL() {

        Object parsedValue = JsonNumberValueType.AUTO_INTEGER_LONG_BIG_DECIMAL.parse("6E25".codePoints().toArray(), null);
        Assert.assertTrue(parsedValue instanceof BigDecimal);

        parsedValue = JsonNumberValueType.AUTO_INTEGER_LONG_BIG_DECIMAL.parse("14.01".codePoints().toArray(), null);
        Assert.assertTrue(parsedValue instanceof BigDecimal);

        parsedValue = JsonNumberValueType.AUTO_INTEGER_LONG_BIG_DECIMAL.parse(String.valueOf(Long.MAX_VALUE).concat(".14").codePoints().toArray(), null);
        Assert.assertTrue(parsedValue instanceof BigDecimal);

        parsedValue = JsonNumberValueType.AUTO_INTEGER_LONG_BIG_DECIMAL.parse("6E2".codePoints().toArray(), null);
        Assert.assertTrue(parsedValue instanceof Integer);


        parsedValue = JsonNumberValueType.AUTO_INTEGER_LONG_BIG_DECIMAL.parse("6E12".codePoints().toArray(), null);
        Assert.assertTrue(parsedValue instanceof Long);
    }

    @Test
    public void testParseAUTO_INTEGER_LONG_DOUBLE() {

        Object parsedValue = JsonNumberValueType.AUTO_INTEGER_LONG_DOUBLE.parse("6E25".codePoints().toArray(), null);
        Assert.assertTrue(parsedValue instanceof Double);

        parsedValue = JsonNumberValueType.AUTO_INTEGER_LONG_DOUBLE.parse("14.01".codePoints().toArray(), null);
        Assert.assertTrue(parsedValue instanceof Double);

        parsedValue = JsonNumberValueType.AUTO_INTEGER_LONG_DOUBLE.parse(String.valueOf(Integer.MAX_VALUE).concat(".14").codePoints().toArray(), null);
        Assert.assertTrue(parsedValue instanceof Double);

        parsedValue = JsonNumberValueType.AUTO_INTEGER_LONG_DOUBLE.parse("6E2".codePoints().toArray(), null);
        Assert.assertTrue(parsedValue instanceof Integer);

        parsedValue = JsonNumberValueType.AUTO_INTEGER_LONG_DOUBLE.parse("6E12".codePoints().toArray(), null);
        Assert.assertTrue(parsedValue instanceof Long);
    }
}
