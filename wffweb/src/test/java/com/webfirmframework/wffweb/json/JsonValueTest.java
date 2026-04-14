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

public class JsonValueTest {

    @Test
    public void toJsonString() {
        Assert.assertEquals("\"somevalue\"", new JsonValue("somevalue", JsonValueType.STRING).toJsonString());
        Assert.assertEquals("\"somevalue \\n \"", new JsonValue("somevalue \\n ", JsonValueType.ENCODED_STRING).toJsonString());
        Assert.assertEquals("\"\"", new JsonValue("", JsonValueType.STRING).toJsonString());
        Assert.assertEquals("\"\"", new JsonValue("", JsonValueType.ENCODED_STRING).toJsonString());
        Assert.assertEquals("true", new JsonValue("true", JsonValueType.BOOLEAN).toJsonString());
        Assert.assertEquals("false", new JsonValue("false", JsonValueType.BOOLEAN).toJsonString());
        Assert.assertEquals("14.01", new JsonValue("14.01", JsonValueType.NUMBER).toJsonString());
        Assert.assertEquals("1401", new JsonValue("1401", JsonValueType.NUMBER).toJsonString());
        Assert.assertEquals("null", new JsonValue("null", JsonValueType.NULL).toJsonString());
        Assert.assertEquals("null", new JsonValue((int[]) null, JsonValueType.NULL).toJsonString());
        Assert.assertEquals("    \uD83D\uDE80 • © ™    ", new JsonValue("    \uD83D\uDE80 \u2022 \u00A9 \u2122    ", JsonValueType.STRING).asString());

        Assert.assertEquals(" \uD83D\uDE80 • © ™ \nnew line ", new JsonValue(" \uD83D\uDE80 \u2022 \u00A9 \u2122 \nnew line ", JsonValueType.STRING).asString(true));
        Assert.assertEquals(" \uD83D\uDE80 • © ™ \nnew line \n", new JsonValue(" \uD83D\uDE80 \u2022 \u00A9 \u2122 \nnew line \n", JsonValueType.STRING).asString(true));
        Assert.assertEquals(" \uD83D\uDE80 • © ™ \nnew line ", new JsonValue(" \uD83D\uDE80 \u2022 \u00A9 \u2122 \\nnew line ", JsonValueType.ENCODED_STRING).asString(true));
        Assert.assertEquals("\uD83D\uDE80 • © ™ \nnew line \n", new JsonValue("\uD83D\uDE80 \u2022 \u00A9 \u2122 \\nnew line \\n", JsonValueType.ENCODED_STRING).asString(true));
        Assert.assertEquals("\uD83D\uDE80 • © ™ \nnew line", new JsonValue("\uD83D\uDE80 \u2022 \u00A9 \u2122 \nnew line", JsonValueType.STRING).asString(true));
        Assert.assertEquals("\n\uD83D\uDE80 • © ™ \nnew line\n", new JsonValue("\\n\uD83D\uDE80 \u2022 \u00A9 \u2122 \\nnew line\\n", JsonValueType.ENCODED_STRING).asString(true));
        Assert.assertEquals("\n\uD83D\uDE80 • © ™ \nnew line\n", new JsonValue("\\n\uD83D\uDE80 \\u2022 \\u00A9 \\u2122 \\nnew line\\n", JsonValueType.ENCODED_STRING).asString(true, true));
    }

}