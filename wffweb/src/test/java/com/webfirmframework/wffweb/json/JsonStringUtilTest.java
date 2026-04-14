package com.webfirmframework.wffweb.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class JsonStringUtilTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testPlaceEscapeCharInJsonStringValueIfRequired() throws JsonProcessingException {

        //finished testing
        String wronglyParsedInObjectMapper = "\\/";

        String jsonValue = """
                 \\" \\\\ / \\b \\f \\n \\r \\t \\r\\n
                 """.trim();
        String json = """
                {
                "special_chars": "%s"
                }
                """.formatted(jsonValue);

        JsonNode jsonNode = objectMapper.readTree(json);

        Assert.assertEquals(wronglyParsedInObjectMapper, JsonStringUtil.placeEscapeCharInJsonStringValueIfRequired(wronglyParsedInObjectMapper));
        Assert.assertEquals("""
                \\" \\\\\\\\ / \\b \\f \\n \\r \\t \\r\\n""", JsonStringUtil.placeEscapeCharInJsonStringValueIfRequired(jsonValue));

        Assert.assertEquals(jsonNode.toString(), JsonMap.parse(json).toJsonString());

        String jsonFormat2 = """
                {"k2" : "v2{\\"k\\":\\"v\\"}[]"}
                """;

        JsonMap jsonMap = JsonMap.parse(jsonFormat2);

        Assert.assertEquals("""
                {"k2":"v2{\\"k\\":\\"v\\"}[]"}""".trim(), jsonMap.toJsonString());

        Assert.assertEquals("\\".repeat(2), JsonStringUtil.placeEscapeCharInJsonStringValueIfRequired("\\".repeat(1)));
        Assert.assertEquals("\\".repeat(4), JsonStringUtil.placeEscapeCharInJsonStringValueIfRequired("\\".repeat(2)));
        Assert.assertEquals("\\".repeat(6), JsonStringUtil.placeEscapeCharInJsonStringValueIfRequired("\\".repeat(3)));
        Assert.assertEquals("\\".repeat(8), JsonStringUtil.placeEscapeCharInJsonStringValueIfRequired("\\".repeat(4)));

        Assert.assertEquals("\u2122%s\u2122".formatted("\\".repeat(2)), JsonStringUtil.placeEscapeCharInJsonStringValueIfRequired("\u2122%s\u2122".formatted("\\".repeat(1))));
        Assert.assertEquals("\u2122%s\u2122".formatted("\\".repeat(4)), JsonStringUtil.placeEscapeCharInJsonStringValueIfRequired("\u2122%s\u2122".formatted("\\".repeat(2))));
        Assert.assertEquals("\u2122%s\u2122".formatted("\\".repeat(6)), JsonStringUtil.placeEscapeCharInJsonStringValueIfRequired("\u2122%s\u2122".formatted("\\".repeat(3))));
        Assert.assertEquals("\u2122%s\u2122".formatted("\\".repeat(8)), JsonStringUtil.placeEscapeCharInJsonStringValueIfRequired("\u2122%s\u2122".formatted("\\".repeat(4))));

        Assert.assertEquals("\\u2122%s\\u2122".formatted("\\".repeat(2)), JsonStringUtil.placeEscapeCharInJsonStringValueIfRequired("\\u2122%s\\u2122".formatted("\\".repeat(1))));
        Assert.assertEquals("\\u2122%s\\u2122".formatted("\\".repeat(4)), JsonStringUtil.placeEscapeCharInJsonStringValueIfRequired("\\u2122%s\\u2122".formatted("\\".repeat(2))));
        Assert.assertEquals("\\u2122%s\\u2122".formatted("\\".repeat(6)), JsonStringUtil.placeEscapeCharInJsonStringValueIfRequired("\\u2122%s\\u2122".formatted("\\".repeat(3))));
        Assert.assertEquals("\\u2122%s\\u2122".formatted("\\".repeat(8)), JsonStringUtil.placeEscapeCharInJsonStringValueIfRequired("\\u2122%s\\u2122".formatted("\\".repeat(4))));

        Assert.assertEquals("\\b%s\\b".formatted("\\".repeat(2)), JsonStringUtil.placeEscapeCharInJsonStringValueIfRequired("\\b%s\\b".formatted("\\".repeat(1))));
        Assert.assertEquals("\\b%s\\b".formatted("\\".repeat(4)), JsonStringUtil.placeEscapeCharInJsonStringValueIfRequired("\\b%s\\b".formatted("\\".repeat(2))));
        Assert.assertEquals("\\b%s\\b".formatted("\\".repeat(6)), JsonStringUtil.placeEscapeCharInJsonStringValueIfRequired("\\b%s\\b".formatted("\\".repeat(3))));
        Assert.assertEquals("\\b%s\\b".formatted("\\".repeat(8)), JsonStringUtil.placeEscapeCharInJsonStringValueIfRequired("\\b%s\\b".formatted("\\".repeat(4))));

        Assert.assertEquals("start%send".formatted("\\".repeat(2)), JsonStringUtil.placeEscapeCharInJsonStringValueIfRequired("start%send".formatted("\\".repeat(1))));
        Assert.assertEquals("start%send".formatted("\\".repeat(4)), JsonStringUtil.placeEscapeCharInJsonStringValueIfRequired("start%send".formatted("\\".repeat(2))));
        Assert.assertEquals("start%send".formatted("\\".repeat(6)), JsonStringUtil.placeEscapeCharInJsonStringValueIfRequired("start%send".formatted("\\".repeat(3))));
        Assert.assertEquals("start%send".formatted("\\".repeat(8)), JsonStringUtil.placeEscapeCharInJsonStringValueIfRequired("start%send".formatted("\\".repeat(4))));

        Assert.assertEquals("%su%s%su".formatted("\\".repeat(2), "\\".repeat(2), "\\".repeat(2)), JsonStringUtil.placeEscapeCharInJsonStringValueIfRequired("%su%s%su".formatted("\\".repeat(1), "\\".repeat(1), "\\".repeat(1))));
        Assert.assertEquals("%su%s%su".formatted("\\".repeat(4), "\\".repeat(4), "\\".repeat(4)), JsonStringUtil.placeEscapeCharInJsonStringValueIfRequired("%su%s%su".formatted("\\".repeat(2), "\\".repeat(2), "\\".repeat(2))));

    }

    @Test
    public void testReplaceEscapeCharSequenceWithJavaChars() throws IOException {

        String escapeCharSequence = "    \\uD83D\\uDE80 \\u2022 \\u00A9 \\u2122 \n    ";
        int[] codePoints = escapeCharSequence.codePoints().toArray();
        Assert.assertEquals(escapeCharSequence, JsonStringUtil.replaceEscapeCharSequenceWithJavaChars(codePoints,  0, codePoints.length, false));
        Assert.assertEquals("    \uD83D\uDE80 \u2022 \u00A9 \u2122 \n    ", JsonStringUtil.replaceEscapeCharSequenceWithJavaChars(codePoints,  0, codePoints.length, true));

        String jsonValue = """
                 \\" \\\\ / \\b \\f \\n \\r \\t \\/ \\r\\n
                 """.trim();
//        jsonValue = "\\u2122 \uD83D\uDE80";
//        jsonValue = "\\r\\n";
        String json = """
                {
                "special_chars": "%s",
                "key": "val"
                }
                """.formatted(jsonValue);

        JsonNode jsonNode = objectMapper.readTree(json);
        String specialChars = jsonNode.get("special_chars").asText();
        codePoints = jsonValue.codePoints().toArray();
        String actual = JsonStringUtil.replaceEscapeCharSequenceWithJavaChars(codePoints, 0, codePoints.length);
        Assert.assertEquals(specialChars, actual);

        testReplaceEscapeCharSequenceWithJavaCharsForInput("\\\\", "\\");
        testReplaceEscapeCharSequenceWithJavaCharsForInput("\\", "\\");
        testReplaceEscapeCharSequenceWithJavaCharsForInput("\\\\u2122", "\\u2122");
        testReplaceEscapeCharSequenceWithJavaCharsForInput("\\\\\\u2122", "\\\\u2122");
        testReplaceEscapeCharSequenceWithJavaCharsForInput("\\\\\\\\u2122", "\\\\u2122");
    }

    private void testReplaceEscapeCharSequenceWithJavaCharsForInput(String input, String expectedResult) {
        int[] testCodePoints1 = input.codePoints().toArray();
        String decoded = JsonStringUtil.replaceEscapeCharSequenceWithJavaChars(testCodePoints1, 0, testCodePoints1.length, false);
        String msg = "\nInput: %s\nExpected: %s\nActual: %s".formatted(input, expectedResult, decoded);
        if (!expectedResult.equals(decoded)) {
            Assert.fail(msg);
        } else {
            System.out.println(msg);
        }
    }

    @Test
    public void testToUnicodeCharsDecodedString() {
        final String input1 = "\\u2122";
        final String input2 = "\\\\u2122";
        final int[] codePoints1 = input1.codePoints().toArray();
        final int[] codePoints2 = input2.codePoints().toArray();
        Assert.assertEquals("\u2122", JsonStringUtil.toUnicodeCharsDecodedString(codePoints1, 0, codePoints1.length));
        Assert.assertEquals("™", JsonStringUtil.toUnicodeCharsDecodedString(codePoints1, 0, codePoints1.length));
        Assert.assertEquals(input2, JsonStringUtil.toUnicodeCharsDecodedString(codePoints2, 0, codePoints2.length));

        final String input3 = "start\\u2122end";
        final String input4 = "start\\\\u2122end";
        final int[] codePoints3 = input3.codePoints().toArray();
        final int[] codePoints4 = input4.codePoints().toArray();
        Assert.assertEquals("start\u2122end", JsonStringUtil.toUnicodeCharsDecodedString(codePoints3, 0, codePoints3.length));
        Assert.assertEquals("start™end", JsonStringUtil.toUnicodeCharsDecodedString(codePoints3, 0, codePoints3.length));
        Assert.assertEquals(input4, JsonStringUtil.toUnicodeCharsDecodedString(codePoints4, 0, codePoints4.length));

        final String input5 = "start\\u2122";
        final String input6 = "start\\\\u2122";
        final int[] codePoints5 = input5.codePoints().toArray();
        final int[] codePoints6 = input6.codePoints().toArray();
        Assert.assertEquals("start\u2122", JsonStringUtil.toUnicodeCharsDecodedString(codePoints5, 0, codePoints5.length));
        Assert.assertEquals("start™", JsonStringUtil.toUnicodeCharsDecodedString(codePoints5, 0, codePoints5.length));
        Assert.assertEquals(input6, JsonStringUtil.toUnicodeCharsDecodedString(codePoints6, 0, codePoints6.length));

        final String input7 = "\\u2122end";
        final String input8 = "\\\\u2122end";
        final int[] codePoints7 = input7.codePoints().toArray();
        final int[] codePoints8 = input8.codePoints().toArray();
        Assert.assertEquals("\u2122end", JsonStringUtil.toUnicodeCharsDecodedString(codePoints7, 0, codePoints7.length));
        Assert.assertEquals("™end", JsonStringUtil.toUnicodeCharsDecodedString(codePoints7, 0, codePoints7.length));
        Assert.assertEquals(input8, JsonStringUtil.toUnicodeCharsDecodedString(codePoints8, 0, codePoints8.length));

        final String input9 = "\n\\u2122\n";
        final String input10 = "\n\\\\u2122\n";
        final int[] codePoints9 = input9.codePoints().toArray();
        final int[] codePoints10 = input10.codePoints().toArray();
        Assert.assertEquals("\n\u2122\n", JsonStringUtil.toUnicodeCharsDecodedString(codePoints9, 0, codePoints9.length));
        Assert.assertEquals("\n™\n", JsonStringUtil.toUnicodeCharsDecodedString(codePoints9, 0, codePoints9.length));
        Assert.assertEquals(input10, JsonStringUtil.toUnicodeCharsDecodedString(codePoints10, 0, codePoints10.length));

    }

}
