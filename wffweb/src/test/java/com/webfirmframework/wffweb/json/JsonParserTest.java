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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class JsonParserTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    private void testToOutputStreamAndToBigOutputStream(JsonBaseNode jsonBaseNode) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            jsonBaseNode.toOutputStream(outputStream, StandardCharsets.UTF_8, true);
            String outputStreamString = outputStream.toString(StandardCharsets.UTF_8);
            Assert.assertEquals(jsonBaseNode.toJsonString(), outputStreamString);
        } catch (IOException e) {
            Assert.fail("IOException white testing toOutputStream");
        }
    }

    @Test
    public void testJsonParserBuilder()  {
        JsonParser jsonParser = JsonParser.newBuilder()
                .jsonArrayType(JsonArrayType.UNMODIFIABLE_LIST)
                .jsonObjectType(JsonObjectType.JSON_CONCURRENT_MAP)
                .jsonNumberValueTypeForArray(JsonNumberValueType.BIG_DECIMAL)
                .jsonNumberValueTypeForObject(JsonNumberValueType.DOUBLE)

                .jsonStringValueTypeForArray(JsonStringValueType.JSON_VALUE)
                .jsonStringValueTypeForObject(JsonStringValueType.STRING)

                .jsonBooleanValueTypeForArray(JsonBooleanValueType.JSON_VALUE)
                .jsonBooleanValueTypeForObject(JsonBooleanValueType.BOOLEAN)

                .jsonNullValueTypeForArray(JsonNullValueType.NULL)
                .jsonNullValueTypeForObject(JsonNullValueType.JSON_VALUE)
                .build();

        Assert.assertEquals(JsonArrayType.UNMODIFIABLE_LIST, jsonParser.jsonArrayType());
        Assert.assertEquals(JsonObjectType.JSON_CONCURRENT_MAP, jsonParser.jsonObjectType());

        Assert.assertEquals(JsonObjectType.JSON_CONCURRENT_MAP, jsonParser.jsonObjectType());
        Assert.assertEquals(JsonNumberValueType.BIG_DECIMAL, jsonParser.jsonNumberValueTypeForArray());

        Assert.assertEquals(JsonNumberValueType.BIG_DECIMAL, jsonParser.jsonNumberValueTypeForArray());
        Assert.assertEquals(JsonNumberValueType.DOUBLE, jsonParser.jsonNumberValueTypeForObject());

        Assert.assertEquals(JsonStringValueType.JSON_VALUE, jsonParser.jsonStringValueTypeForArray());
        Assert.assertEquals(JsonStringValueType.STRING, jsonParser.jsonStringValueTypeForObject());

        Assert.assertEquals(JsonBooleanValueType.JSON_VALUE, jsonParser.jsonBooleanValueTypeForArray());
        Assert.assertEquals(JsonBooleanValueType.BOOLEAN, jsonParser.jsonBooleanValueTypeForObject());

        Assert.assertEquals(JsonNullValueType.NULL, jsonParser.jsonNullValueTypeForArray());
        Assert.assertEquals(JsonNullValueType.JSON_VALUE, jsonParser.jsonNullValueTypeForObject());
    }

    @Test(expected = IllegalJsonFormatException.class)
    public void testIllegalFormatException1()  {
        JsonParser jsonParser = new JsonParser();
        jsonParser.parseJson("{}{}");
    }

    @Test(expected = IllegalJsonFormatException.class)
    public void testIllegalFormatException2()  {
        JsonParser jsonParser = new JsonParser();
        jsonParser.parseJson("[][]");
    }

    @Test(expected = IllegalJsonFormatException.class)
    public void testIllegalFormatException3()  {
        JsonParser jsonParser = new JsonParser();
        jsonParser.parseJson("{}[]");
    }

    @Test(expected = IllegalJsonFormatException.class)
    public void testIllegalFormatException4()  {
        JsonParser jsonParser = new JsonParser();
        jsonParser.parseJson("[]{}");
    }

    @Test(expected = IllegalJsonFormatException.class)
    public void testIllegalFormatException5()  {
        JsonParser jsonParser = new JsonParser();
        jsonParser.parseJson("{{}");
    }

    @Test(expected = IllegalJsonFormatException.class)
    public void testIllegalFormatException6()  {
        JsonParser jsonParser = new JsonParser();
        jsonParser.parseJson("[[]");
    }

    @Test(expected = IllegalJsonFormatException.class)
    public void testIllegalFormatException7()  {
        JsonParser jsonParser = new JsonParser();
        jsonParser.parseJson("{}}");
    }

    @Test(expected = IllegalJsonFormatException.class)
    public void testIllegalFormatException8()  {
        JsonParser jsonParser = new JsonParser();
        jsonParser.parseJson("[]]");
    }

    @Test(expected = IllegalJsonFormatException.class)
    public void testIllegalFormatException9()  {
        JsonParser jsonParser = new JsonParser();
        jsonParser.parseJson("""
                {
                "somekey" : \\: "someval":
                }""");
    }

    @Test(expected = IllegalJsonFormatException.class)
    public void testIllegalFormatException10()  {
        JsonParser jsonParser = new JsonParser();
        jsonParser.parseJson("""
                {
                "somekey" : "v1" : "v2"
                }""");
    }

    @Test(expected = IllegalJsonFormatException.class)
    public void testIllegalFormatException11()  {
        JsonParser jsonParser = new JsonParser();
        jsonParser.parseJson("""
                {
                "somekey" : "v1" \\: "v2"
                }""");
    }

    @Test(expected = NumberFormatException.class)
    public void testIllegalFormatException12()  {
        JsonParser jsonParser = new JsonParser();
        jsonParser.parseJson("""
                [ "one", \\, "two"]
                """);
    }

    @Test(expected = IllegalJsonFormatException.class)
    public void testIllegalFormatException121()  {
        JsonParser jsonParser = new JsonParser();
        jsonParser.parseJson("""
                [ "one", , "two"]
                """);
    }

    @Test(expected = IllegalJsonFormatException.class)
    public void testIllegalFormatException13()  {
        JsonParser jsonParser = new JsonParser();
        jsonParser.parseJson("""
                {
                "somekey" : "v1" \\, "k2" : "val2"
                }""");
    }

    @Test(expected = IllegalJsonFormatException.class)
    public void testIllegalFormatException14()  {
        JsonParser jsonParser = new JsonParser();
        jsonParser.parseJson("""
                {
                \\"somekey" : "v1", "k2" : "val2"
                }""");
    }

    @Test(expected = IllegalJsonFormatException.class)
    public void testIllegalFormatException15()  {
        JsonParser jsonParser = new JsonParser();
        jsonParser.parseJson("""
                {
                "duplicateKey" : "v1",
                "duplicateKey" : "v2"
                }""");
    }

    @Test(expected = IllegalJsonFormatException.class)
    public void testIllegalFormatException16()  {
        JsonParser jsonParser = new JsonParser();
        jsonParser.parseJson("""
                {
                {} : "val"
                }""");
    }

    @Test(expected = IllegalJsonFormatException.class)
    public void testIllegalFormatExceptionIllegalEscapeSequence()  {
        JsonParser jsonParser = JsonParser.newBuilder().validateEscapeSequence(true).build();
        jsonParser.parseJson("""
                {
                "key" : "val \\ ue"
                }""");
    }

    @Test(expected = IllegalJsonFormatException.class)
    public void testIllegalFormatExceptionIllegalEscapeSequence2()  {
        JsonParser jsonParser = JsonParser.newBuilder().validateEscapeSequence(true).build();
        jsonParser.parseJson("""
                {
                "key" : "val \\uZZZZ ue"
                }""");
    }


    @Test
    public void testParseJson() {
        JsonParser jsonParser = JsonParser.newBuilder().validateEscapeSequence(true).build();
        Object parsedValue = jsonParser.parseJson("true");
        Assert.assertNotNull(parsedValue);
        Assert.assertTrue((Boolean) parsedValue);

        parsedValue = jsonParser.parseJson("false");
        Assert.assertNotNull(parsedValue);
        Assert.assertFalse((Boolean) parsedValue);

        parsedValue = jsonParser.parseJson("14");
        Assert.assertNotNull(parsedValue);
        Assert.assertTrue(parsedValue instanceof BigDecimal);
        Assert.assertEquals(14D, ((BigDecimal) parsedValue).doubleValue(), 0);

        parsedValue = jsonParser.parseJson("14.01");
        Assert.assertNotNull(parsedValue);
        Assert.assertTrue(parsedValue instanceof BigDecimal);
        Assert.assertEquals(14.01D, ((BigDecimal) parsedValue).doubleValue(), 0);

        parsedValue = jsonParser.parseJson("{}");
        Assert.assertNotNull(parsedValue);
        Assert.assertTrue(parsedValue instanceof Map<?,?>);

        parsedValue = jsonParser.parseJson("[]");
        Assert.assertNotNull(parsedValue);
        Assert.assertTrue(parsedValue instanceof List<?>);
    }

    @Test
    public void testParseEscapeSequence() throws IOException {
        String jsonForNothing = """
                {
                  "metadata": {
                    "version": "1.0.0",
                    "timestamp": "2025-02-21T12:34:56Z",
                    "source": "test_data",
                    "valid": true
                  },
                  "data": {
                    "id": 9876543210,
                    "name": "Complex JSON Test",
                    "description": "This JSON contains multiple cases to validate parsers.",
                    "isActive": false,
                    "nullField": null,
                    "emptyObject": {},
                    "emptyArray": [],
                    "specialCharacters": "\\" \\\\ / \\b \\f \\n \\r \\t",
                    "unicodeText": "𝒜𝒷𝒸𝒹 → ©®✓😊",
                    "escapedUnicode": "\uD83D\uDE80 \u2022 \u00A9 \u2122",
                    "largeNumbers": {
                      "positive": 1.7976931348623157e+308,
                      "negative": -1.7976931348623157e+308,
                      "tiny": 5e-324,
                      "integer": 9223372036854775807
                    },
                    "arrayTests": {
                      "emptyArray": [],
                      "simpleArray": [1, 2, 3, 4, 5],
                      "mixedArray": [true, false, null, "text", 42, {"key": "value"}],
                      "nestedArray": [[[[[["deep_value"]]]]]],
                      "longArray": [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20]
                    },
                    "deeplyNestedObject": {
                      "level1": {
                        "level2": {
                          "level3": {
                            "level4": {
                              "level5": {
                                "value": "Deep Value",
                                "more": {
                                  "nested": {
                                    "objects": {
                                      "finalLevel": true
                                    }
                                  }
                                }
                              }
                            }
                          }
                        }
                      }
                    },
                    "circularReference": {
                      "self": { "reference": "This simulates circular reference indirectly" }
                    },
                    "complexObjectsArray": [
                      {
                        "id": 1,
                        "name": "Item 1",
                        "details": { "subDetail": "Value 1", "active": true }
                      },
                      {
                        "id": 2,
                        "name": "Item 2",
                        "details": { "subDetail": "Value 2", "active": false }
                      },
                      {
                        "id": 3,
                        "name": "Item 3",
                        "details": { "subDetail": "Value 3", "active": null }
                      }
                    ]
                  },
                  "booleanEdgeCases": {
                    "allTrue": [true, true, true, true],
                    "allFalse": [false, false, false, false],
                    "mixed": [true, false, null, true]
                  },
                  "numericEdgeCases": {
                    "zero": 0,
                    "negativeZero": -0,
                    "maxInt": 9007199254740991,
                    "minInt": -9007199254740991,
                    "floatingPoint": 3.141592653589793,
                    "scientificNotation": 6.022e23,
                    "smallNumber": 1e-10
                  }
                }
                """;
        JsonParser jsonParser = JsonParser.newBuilder().validateEscapeSequence(true).build();
        String json = Files.readString(Path.of(Objects.requireNonNull(JsonListTest.class.getResource("complex_large_json_sample1.json")).getPath())).strip();
        jsonParser.parseJson(json);
    }


    @Test
    public void testJsonUnmodifiableMap() {

        JsonParser jsonParser = new JsonParser();
        Map<String, Object> map = jsonParser.parseJsonObject("""
                {
                "key" : "value",
                "key1" : "value1"
                }
                """);

        Assert.assertNotNull(map);
        String value = map.get("key1").toString();
        Assert.assertNotNull(value);
        Assert.assertEquals("value1", value);
    }

    @Test
    public void testUnicodeSpaceChar()  {
        JsonParser jsonParser = new JsonParser(JsonObjectType.JSON_MAP, JsonArrayType.JSON_LIST, false, null, null, null, null, null, null, null, null, false);
        // \u2003 is a whitespace in Unicode
        String json = """
                {
                "key1" : "v1"\u2003,
                "key2" : "v2"
                }""";
        final Map<String, Object> parsed = jsonParser.parseJsonObject(json);
        Assert.assertEquals("{\"key1\":\"v1\",\"key2\":\"v2\"}", parsed.toString());
    }

    @Test
    public void testLegalCharsInsideStringValue()  {
        JsonParser jsonParser = new JsonParser(JsonObjectType.JSON_MAP, JsonArrayType.JSON_LIST, false, null, null, null, null, null, null, null, null, false);
        // \u2003 is a whitespace in Unicode
        String json = """
                {
                   "k1" : "v1",
                   "k2" : "v2{}[]",
                   "k3" : "v2{\\"k1\\" : \\"v1\\"}[]"
                 }""";
        final Map<String, Object> parsed = jsonParser.parseJsonObject(json);
        Assert.assertEquals(parsed.toString(), JsonMap.parse(parsed.toString()).toString());

    }

    @Test
    public void testLegalCharsInsideStringValue2()  {
        JsonParser jsonParser = new JsonParser(JsonObjectType.JSON_MAP, JsonArrayType.JSON_LIST, false, null, null, null, null, null, null, null, null, false);
        // \u2003 is a whitespace in Unicode
        String json = """
                {"k":"v2{\\"k1\\":\\"v1\\"}"}
                """;
        final Map<String, Object> parsed = jsonParser.parseJsonObject(json);

        Assert.assertEquals("{\"k\":\"v2{\\\"k1\\\":\\\"v1\\\"}\"}", parsed.toString());
        Assert.assertEquals(parsed.toString(), JsonMap.parse(parsed.toString()).toString());

    }

    @Test
    public void testLegalCharsInsideStringValue3()  {
        JsonParser jsonParser = new JsonParser(JsonObjectType.JSON_MAP, JsonArrayType.JSON_LIST, false, null, null, null, null, null, null, null, null, false);
        // \u2003 is a whitespace in Unicode
        String json = """
                {"k3":"v2{\\"k1\\":\\"v1\\"}[]"}
                """.strip();
        final Map<String, Object> parsed = jsonParser.parseJsonObject(json);

        Assert.assertEquals(json, parsed.toString());
        Assert.assertEquals(parsed.toString(), JsonMap.parse(parsed.toString()).toString());
    }

    @Test
    public void testNewBuilder() {
        JsonParser jsonParser = JsonParser.newBuilder()
                .jsonObjectType(JsonObjectType.JSON_MAP)
                .jsonArrayType(JsonArrayType.JSON_LIST)
                .jsonNumberArrayUniformValueType(false)
                .jsonNumberValueTypeForObject(JsonNumberValueType.BIG_DECIMAL)
                .jsonNumberValueTypeForArray(JsonNumberValueType.BIG_DECIMAL)
                .jsonStringValueTypeForObject(JsonStringValueType.STRING)
                .jsonStringValueTypeForArray(JsonStringValueType.STRING)
                .jsonBooleanValueTypeForObject(JsonBooleanValueType.BOOLEAN)
                .jsonBooleanValueTypeForArray(JsonBooleanValueType.BOOLEAN)
                .jsonNullValueTypeForObject(JsonNullValueType.NULL)
                .jsonNullValueTypeForArray(JsonNullValueType.NULL)
                .validateEscapeSequence(true).build();

        JsonMap jsonObject = jsonParser.parseJsonObject("""
                {
                "key1" : "val1",
                "key2" : "val2"
                }""") instanceof JsonMap jsonMap ? jsonMap : null;

        Assert.assertEquals("{\"key1\":\"val1\",\"key2\":\"val2\"}", jsonObject.toJsonString());
        testToOutputStreamAndToBigOutputStream(jsonObject);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testJsonParserWithImmutability() {
        String json = """
                {
                "key1" : "v1",
                "key2" : "v2"
                }""";

        JsonParser jsonParser = JsonParser.newBuilder().jsonObjectType(JsonObjectType.UNMODIFIABLE_MAP).jsonArrayType(JsonArrayType.UNMODIFIABLE_LIST).build();
        Map<String, Object> stringObjectMap = jsonParser.parseJsonObject(json);
        stringObjectMap.put("newKey", "newValue");
        Assert.assertTrue(stringObjectMap instanceof JsonMapNode);
    }

    @Test
    public void testJsonParserWithDefaultProperties() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonParser jsonParser = new JsonParser();

        String json = """
                { 
                "intArray" : [5, 4, 3, 2],
                "longArray" : [3000000005, 3000000004, 3000000003, 3000000002],
                "mixedIntLongNumberArray" : [null, 14, 300000.0004, 3000000003, 3000000002, null],
                "k4imojiVal" : "Hello 🌍筀🍻👻🕻🥻!",
                "k2" : "v2{\\"k\\":\\"v\\"}[]",
                "web-app": {
                  "servlet": [  \s
                    {
                      "servlet-name": "cofaxCDS",
                      "servlet-class": "org.cofax.cds.CDSServlet",
                      "init-param": {
                        "configGlossary:installationAt": "Philadelphia, PA",
                        "configGlossary:adminEmail": "ksm@pobox.com",
                        "configGlossary:poweredBy": "Cofax",
                        "configGlossary:poweredByIcon": "/images/cofax.gif",
                        "configGlossary:staticPath": "/content/static",
                        "templateProcessorClass": "org.cofax.WysiwygTemplate",
                        "templateLoaderClass": "org.cofax.FilesTemplateLoader",
                        "templatePath": "templates",
                        "templateOverridePath": "",
                        "defaultListTemplate": "listTemplate.htm",
                        "defaultFileTemplate": "articleTemplate.htm",
                        "useJSP": false,
                        "jspListTemplate": "listTemplate.jsp",
                        "jspFileTemplate": "articleTemplate.jsp",
                        "cachePackageTagsTrack": 200,
                        "cachePackageTagsStore": 200,
                        "cachePackageTagsRefresh": 60,
                        "cacheTemplatesTrack": 100,
                        "cacheTemplatesStore": 50,
                        "cacheTemplatesRefresh": 15,
                        "cachePagesTrack": 200,
                        "cachePagesStore": 100,
                        "cachePagesRefresh": 10,
                        "cachePagesDirtyRead": 10,
                        "searchEngineListTemplate": "forSearchEnginesList.htm",
                        "searchEngineFileTemplate": "forSearchEngines.htm",
                        "searchEngineRobotsDb": "WEB-INF/robots.db",
                        "useDataStore": true,
                        "dataStoreClass": "org.cofax.SqlDataStore",
                        "redirectionClass": "org.cofax.SqlRedirection",
                        "dataStoreName": "cofax",
                        "dataStoreDriver": "com.microsoft.jdbc.sqlserver.SQLServerDriver",
                        "dataStoreUrl": "jdbc:microsoft:sqlserver://LOCALHOST:1433;DatabaseName=goon",
                        "dataStoreUser": "sa",
                        "dataStorePassword": "dataStoreTestQuery",
                        "dataStoreTestQuery": "SET NOCOUNT ON;select test='test';",
                        "dataStoreLogFile": "/usr/local/tomcat/logs/datastore.log",
                        "dataStoreInitConns": 10,
                        "dataStoreMaxConns": 100,
                        "dataStoreConnUsageLimit": 100,
                        "dataStoreLogLevel": "debug",
                        "maxUrlLength": 500}},
                    {
                      "servlet-name": "cofaxEmail",
                      "servlet-class": "org.cofax.cds.EmailServlet",
                      "init-param": {
                      "mailHost": "mail1",
                      "mailHostOverride": "mail2"}},
                    {
                      "servlet-name": "cofaxAdmin",
                      "servlet-class": "org.cofax.cds.AdminServlet"},
                \s
                    {
                      "servlet-name": "fileServlet",
                      "servlet-class": "org.cofax.cds.FileServlet"},
                    {
                      "servlet-name": "cofaxTools",
                      "servlet-class": "org.cofax.cms.CofaxToolsServlet",
                      "init-param": {
                        "templatePath": "toolstemplates/",
                        "log": 1,
                        "logLocation": "/usr/local/tomcat/logs/CofaxTools.log",
                        "logMaxSize": "",
                        "dataLog": 1,
                        "dataLogLocation": "/usr/local/tomcat/logs/dataLog.log",
                        "dataLogMaxSize": "",
                        "removePageCache": "/content/admin/remove?cache=pages&id=",
                        "removeTemplateCache": "/content/admin/remove?cache=templates&id=",
                        "fileTransferFolder": "/usr/local/tomcat/webapps/content/fileTransferFolder",
                        "lookInContext": 1,
                        "adminGroupID": 4,
                        "betaServer": true}}],
                  "servlet-mapping": {
                    "cofaxCDS": "/",
                    "cofaxEmail": "/cofaxutil/aemail/*",
                    "cofaxAdmin": "/admin/*",
                    "fileServlet": "/static/*",
                    "cofaxTools": "/tools/*"},
                \s
                  "taglib": {
                    "taglib-uri": "cofax.tld",
                    "taglib-location": "/WEB-INF/tlds/cofax.tld"
                    },
                    "menu": {
                    "header": "SVG Viewer",
                    "items": [
                        {"id": "Open"},
                        {"id": "OpenNew", "label": "Open New"},
                        null,
                        {"id": "ZoomIn", "label": "Zoom In"},
                        {"id": "ZoomOut", "label": "Zoom Out"},
                        {"id": "OriginalView", "label": "Original View"},
                        null,
                        {"id": "Quality"},
                        {"id": "Pause"},
                        {"id": "Mute"},
                        null,
                        {"id": "Find", "label": "Find..."},
                        {"id": "FindAgain", "label": "Find Again"},
                        {"id": "Copy"},
                        {"id": "CopyAgain", "label": "Copy Again"},
                        {"id": "CopySVG", "label": "Copy SVG"},
                        {"id": "ViewSVG", "label": "View SVG"},
                        {"id": "ViewSource", "label": "View Source"},
                        {"id": "SaveAs", "label": "Save As"},
                        null,
                        {"id": "Help"},
                        {"id": "About", "label": "About Adobe CVG Viewer..."}
                    ],
                    "otherItems": ["one:two pair", " some str ", "yes", null],
                    "ky4null" : null
                }
                  }
                }""".strip();

        final Map<String, Object> stringObjectMap = jsonParser.parseJsonObject(json);
        Assert.assertEquals(objectMapper.readTree(json), objectMapper.readTree(stringObjectMap.toString()));

    }

    @Test
    public void testJsonParserWithJsonValueType() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonParser jsonParser = new JsonParser(JsonObjectType.JSON_MAP,
                JsonArrayType.JSON_LIST, false, JsonNumberValueType.JSON_VALUE, JsonNumberValueType.JSON_VALUE,
                JsonStringValueType.JSON_VALUE, null, JsonBooleanValueType.JSON_VALUE, null, JsonNullValueType.JSON_VALUE, null, false
        );

        String json = """
                { 
                "intArray" : [5, 4, 3, 2],
                "longArray" : [3000000005, 3000000004, 3000000003, 3000000002],
                "mixedIntLongNumberArray" : [null, 14, 300000.0004, 3000000003, 3000000002, null],
                "k4imojiVal" : "Hello 🌍筀🍻👻🕻🥻!",
                "k2" : "v2{\\"k\\":\\"v\\"}[]",
                "web-app": {
                  "servlet": [  \s
                    {
                      "servlet-name": "cofaxCDS",
                      "servlet-class": "org.cofax.cds.CDSServlet",
                      "init-param": {
                        "configGlossary:installationAt": "Philadelphia, PA",
                        "configGlossary:adminEmail": "ksm@pobox.com",
                        "configGlossary:poweredBy": "Cofax",
                        "configGlossary:poweredByIcon": "/images/cofax.gif",
                        "configGlossary:staticPath": "/content/static",
                        "templateProcessorClass": "org.cofax.WysiwygTemplate",
                        "templateLoaderClass": "org.cofax.FilesTemplateLoader",
                        "templatePath": "templates",
                        "templateOverridePath": "",
                        "defaultListTemplate": "listTemplate.htm",
                        "defaultFileTemplate": "articleTemplate.htm",
                        "useJSP": false,
                        "jspListTemplate": "listTemplate.jsp",
                        "jspFileTemplate": "articleTemplate.jsp",
                        "cachePackageTagsTrack": 200,
                        "cachePackageTagsStore": 200,
                        "cachePackageTagsRefresh": 60,
                        "cacheTemplatesTrack": 100,
                        "cacheTemplatesStore": 50,
                        "cacheTemplatesRefresh": 15,
                        "cachePagesTrack": 200,
                        "cachePagesStore": 100,
                        "cachePagesRefresh": 10,
                        "cachePagesDirtyRead": 10,
                        "searchEngineListTemplate": "forSearchEnginesList.htm",
                        "searchEngineFileTemplate": "forSearchEngines.htm",
                        "searchEngineRobotsDb": "WEB-INF/robots.db",
                        "useDataStore": true,
                        "dataStoreClass": "org.cofax.SqlDataStore",
                        "redirectionClass": "org.cofax.SqlRedirection",
                        "dataStoreName": "cofax",
                        "dataStoreDriver": "com.microsoft.jdbc.sqlserver.SQLServerDriver",
                        "dataStoreUrl": "jdbc:microsoft:sqlserver://LOCALHOST:1433;DatabaseName=goon",
                        "dataStoreUser": "sa",
                        "dataStorePassword": "dataStoreTestQuery",
                        "dataStoreTestQuery": "SET NOCOUNT ON;select test='test';",
                        "dataStoreLogFile": "/usr/local/tomcat/logs/datastore.log",
                        "dataStoreInitConns": 10,
                        "dataStoreMaxConns": 100,
                        "dataStoreConnUsageLimit": 100,
                        "dataStoreLogLevel": "debug",
                        "maxUrlLength": 500}},
                    {
                      "servlet-name": "cofaxEmail",
                      "servlet-class": "org.cofax.cds.EmailServlet",
                      "init-param": {
                      "mailHost": "mail1",
                      "mailHostOverride": "mail2"}},
                    {
                      "servlet-name": "cofaxAdmin",
                      "servlet-class": "org.cofax.cds.AdminServlet"},
                \s
                    {
                      "servlet-name": "fileServlet",
                      "servlet-class": "org.cofax.cds.FileServlet"},
                    {
                      "servlet-name": "cofaxTools",
                      "servlet-class": "org.cofax.cms.CofaxToolsServlet",
                      "init-param": {
                        "templatePath": "toolstemplates/",
                        "log": 1,
                        "logLocation": "/usr/local/tomcat/logs/CofaxTools.log",
                        "logMaxSize": "",
                        "dataLog": 1,
                        "dataLogLocation": "/usr/local/tomcat/logs/dataLog.log",
                        "dataLogMaxSize": "",
                        "removePageCache": "/content/admin/remove?cache=pages&id=",
                        "removeTemplateCache": "/content/admin/remove?cache=templates&id=",
                        "fileTransferFolder": "/usr/local/tomcat/webapps/content/fileTransferFolder",
                        "lookInContext": 1,
                        "adminGroupID": 4,
                        "betaServer": true}}],
                  "servlet-mapping": {
                    "cofaxCDS": "/",
                    "cofaxEmail": "/cofaxutil/aemail/*",
                    "cofaxAdmin": "/admin/*",
                    "fileServlet": "/static/*",
                    "cofaxTools": "/tools/*"},
                \s
                  "taglib": {
                    "taglib-uri": "cofax.tld",
                    "taglib-location": "/WEB-INF/tlds/cofax.tld"
                    },
                    "menu": {
                    "header": "SVG Viewer",
                    "items": [
                        {"id": "Open"},
                        {"id": "OpenNew", "label": "Open New"},
                        null,
                        {"id": "ZoomIn", "label": "Zoom In"},
                        {"id": "ZoomOut", "label": "Zoom Out"},
                        {"id": "OriginalView", "label": "Original View"},
                        null,
                        {"id": "Quality"},
                        {"id": "Pause"},
                        {"id": "Mute"},
                        null,
                        {"id": "Find", "label": "Find..."},
                        {"id": "FindAgain", "label": "Find Again"},
                        {"id": "Copy"},
                        {"id": "CopyAgain", "label": "Copy Again"},
                        {"id": "CopySVG", "label": "Copy SVG"},
                        {"id": "ViewSVG", "label": "View SVG"},
                        {"id": "ViewSource", "label": "View Source"},
                        {"id": "SaveAs", "label": "Save As"},
                        null,
                        {"id": "Help"},
                        {"id": "About", "label": "About Adobe CVG Viewer..."}
                    ],
                    "otherItems": ["one:two pair", " some str ", "yes", null],
                    "ky4null" : null
                }
                  }
                }""".strip();

        final Map<String, Object> stringObjectMap = jsonParser.parseJsonObject(json);
        Assert.assertEquals(objectMapper.readTree(json), objectMapper.readTree(stringObjectMap.toString()));

        JsonListNode listNode = stringObjectMap.get("mixedIntLongNumberArray") instanceof JsonListNode jln ? jln : null;
        Assert.assertNotNull(listNode);

        JsonNode expected = objectMapper.readTree("[null, 14, 300000.0004, 3000000003, 3000000002, null]");
        Assert.assertEquals(expected, objectMapper.readTree(listNode.toJsonString()));
        testToOutputStreamAndToBigOutputStream(listNode);
    }

    @Test
    public void testParsing() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonParser jsonParser = JsonParser.newBuilder().jsonNumberArrayUniformValueType(true).build();

        String json = """
                { 
                "intArray" : [5, 4, 3, 2],
                "longArray" : [3000000005, 3000000004, 3000000003, 3000000002],
                "mixedIntLongNumberArray" : [null, 14, 300000.0004, 3000000003, 3000000002, null],
                "k4imojiVal" : "Hello 🌍筀🍻👻🕻🥻!",
                "k2" : "v2{}[]",
                "k4nullable": {"nullAry":[null,null,null,{"k4nl":null}],"k4ObjContainingNull":{"k4nul":null},"k4null":null},
                "web-app": {
                  "servlet": [  \s
                    {
                      "servlet-name": "cofaxCDS",
                      "servlet-class": "org.cofax.cds.CDSServlet",
                      "init-param": {
                        "configGlossary:installationAt": "Philadelphia, PA",
                        "configGlossary:adminEmail": "ksm@pobox.com",
                        "configGlossary:poweredBy": "Cofax",
                        "configGlossary:poweredByIcon": "/images/cofax.gif",
                        "configGlossary:staticPath": "/content/static",
                        "templateProcessorClass": "org.cofax.WysiwygTemplate",
                        "templateLoaderClass": "org.cofax.FilesTemplateLoader",
                        "templatePath": "templates",
                        "templateOverridePath": "",
                        "defaultListTemplate": "listTemplate.htm",
                        "defaultFileTemplate": "articleTemplate.htm",
                        "useJSP": false,
                        "jspListTemplate": "listTemplate.jsp",
                        "jspFileTemplate": "articleTemplate.jsp",
                        "cachePackageTagsTrack": 200,
                        "cachePackageTagsStore": 200,
                        "cachePackageTagsRefresh": 60,
                        "cacheTemplatesTrack": 100,
                        "cacheTemplatesStore": 50,
                        "cacheTemplatesRefresh": 15,
                        "cachePagesTrack": 200,
                        "cachePagesStore": 100,
                        "cachePagesRefresh": 10,
                        "cachePagesDirtyRead": 10,
                        "searchEngineListTemplate": "forSearchEnginesList.htm",
                        "searchEngineFileTemplate": "forSearchEngines.htm",
                        "searchEngineRobotsDb": "WEB-INF/robots.db",
                        "useDataStore": true,
                        "dataStoreClass": "org.cofax.SqlDataStore",
                        "redirectionClass": "org.cofax.SqlRedirection",
                        "dataStoreName": "cofax",
                        "dataStoreDriver": "com.microsoft.jdbc.sqlserver.SQLServerDriver",
                        "dataStoreUrl": "jdbc:microsoft:sqlserver://LOCALHOST:1433;DatabaseName=goon",
                        "dataStoreUser": "sa",
                        "dataStorePassword": "dataStoreTestQuery",
                        "dataStoreTestQuery": "SET NOCOUNT ON;select test='test';",
                        "dataStoreLogFile": "/usr/local/tomcat/logs/datastore.log",
                        "dataStoreInitConns": 10,
                        "dataStoreMaxConns": 100,
                        "dataStoreConnUsageLimit": 100,
                        "dataStoreLogLevel": "debug",
                        "maxUrlLength": 500}},
                    {
                      "servlet-name": "cofaxEmail",
                      "servlet-class": "org.cofax.cds.EmailServlet",
                      "init-param": {
                      "mailHost": "mail1",
                      "mailHostOverride": "mail2"}},
                    {
                      "servlet-name": "cofaxAdmin",
                      "servlet-class": "org.cofax.cds.AdminServlet"},
                \s
                    {
                      "servlet-name": "fileServlet",
                      "servlet-class": "org.cofax.cds.FileServlet"},
                    {
                      "servlet-name": "cofaxTools",
                      "servlet-class": "org.cofax.cms.CofaxToolsServlet",
                      "init-param": {
                        "templatePath": "toolstemplates/",
                        "log": 1,
                        "logLocation": "/usr/local/tomcat/logs/CofaxTools.log",
                        "logMaxSize": "",
                        "dataLog": 1,
                        "dataLogLocation": "/usr/local/tomcat/logs/dataLog.log",
                        "dataLogMaxSize": "",
                        "removePageCache": "/content/admin/remove?cache=pages&id=",
                        "removeTemplateCache": "/content/admin/remove?cache=templates&id=",
                        "fileTransferFolder": "/usr/local/tomcat/webapps/content/fileTransferFolder",
                        "lookInContext": 1,
                        "adminGroupID": 4,
                        "betaServer": true}}],
                  "servlet-mapping": {
                    "cofaxCDS": "/",
                    "cofaxEmail": "/cofaxutil/aemail/*",
                    "cofaxAdmin": "/admin/*",
                    "fileServlet": "/static/*",
                    "cofaxTools": "/tools/*"},
                \s
                  "taglib": {
                    "taglib-uri": "cofax.tld",
                    "taglib-location": "/WEB-INF/tlds/cofax.tld"
                    },
                    "menu": {
                    "header": "SVG Viewer",
                    "items": [
                        {"id": "Open"},
                        {"id": "OpenNew", "label": "Open New"},
                        null,
                        {"id": "ZoomIn", "label": "Zoom In"},
                        {"id": "ZoomOut", "label": "Zoom Out"},
                        {"id": "OriginalView", "label": "Original View"},
                        null,
                        {"id": "Quality"},
                        {"id": "Pause"},
                        {"id": "Mute"},
                        null,
                        {"id": "Find", "label": "Find..."},
                        {"id": "FindAgain", "label": "Find Again"},
                        {"id": "Copy"},
                        {"id": "CopyAgain", "label": "Copy Again"},
                        {"id": "CopySVG", "label": "Copy SVG"},
                        {"id": "ViewSVG", "label": "View SVG"},
                        {"id": "ViewSource", "label": "View Source"},
                        {"id": "SaveAs", "label": "Save As"},
                        null,
                        {"id": "Help"},
                        {"id": "About", "label": "About Adobe CVG Viewer..."}
                    ],
                    "otherItems": ["one:two pair", " some str ", "yes", null],
                    "ky4null" : null
                }
                  }
                }
                """;

        Map<String, Object> parsed = jsonParser.parseJsonObject(json);

        Assert.assertEquals(objectMapper.readTree(json), objectMapper.readTree(parsed.toString()));

        JsonNode jsonObject = objectMapper.readTree(json);

        ObjectNode jsonObjectFromMap = objectMapper.createObjectNode();
        for (Map.Entry<String, Object> entry : parsed.entrySet()) {
            jsonObjectFromMap.put(entry.getKey(), entry.getValue().toString());
        }

        String jsonFromObjectMapper = objectMapper.writeValueAsString(parsed);
        Assert.assertEquals(jsonObject, objectMapper.readTree(jsonFromObjectMapper));
        Assert.assertEquals("{\"nullAry\":[null,null,null,{\"k4nl\":null}],\"k4ObjContainingNull\":{\"k4nul\":null},\"k4null\":null}", parsed.get("k4nullable").toString());

        if (parsed.get("intArray") instanceof List<?> intArray) {
            for (Object o : intArray) {
                Assert.assertEquals(Integer.class, o.getClass());
            }
        } else {
            Assert.fail("intArray not found");
        }
        if (parsed.get("longArray") instanceof List<?> longArray) {
            for (Object o : longArray) {
                Assert.assertEquals(Long.class, o.getClass());
            }
        } else {
            Assert.fail("longArray not found");
        }
        if (parsed.get("mixedIntLongNumberArray") instanceof List<?> mixedIntLongNumberArray) {
            Assert.assertEquals(6, mixedIntLongNumberArray.size());
//            Assert.assertEquals(BigDecimal.class, mixedIntLongNumberArray.get(1).getClass());
//            for (Object o : mixedIntLongNumberArray) {
//                if (o != null) {
//                    Assert.assertEquals(BigDecimal.class, o.getClass());
//                }
//            }
        } else {
            Assert.fail("mixedIntLongNumberArray not found");
        }

        Assert.assertEquals(objectMapper.readTree(json), objectMapper.readTree(objectMapper.writeValueAsString(parsed)));

        json = """
                {"menu": {
                    "header": "SVG Viewer",
                    "items": [
                        {"id": "Open"},
                        {"id": "OpenNew", "label": "Open New"},
                        null,
                        {"id": "ZoomIn", "label": "Zoom In"},
                        {"id": "ZoomOut", "label": "Zoom Out"},
                        {"id": "OriginalView", "label": "Original View"},
                        null,
                        {"id": "Quality"},
                        {"id": "Pause"},
                        {"id": "Mute"},
                        null,
                        {"id": "Find", "label": "Find..."},
                        {"id": "FindAgain", "label": "Find Again"},
                        {"id": "Copy"},
                        {"id": "CopyAgain", "label": "Copy Again"},
                        {"id": "CopySVG", "label": "Copy SVG"},
                        {"id": "ViewSVG", "label": "View SVG"},
                        {"id": "ViewSource", "label": "View Source"},
                        {"id": "SaveAs", "label": "Save As"},
                        null,
                        {"id": "Help"},
                        {"id": "About", "label": "About Adobe CVG Viewer..."}
                    ],
                    "otherItems": ["one:two pair", " some str ", "yes", null],
                    "ky4null" : null
                }}
                """;
        parsed = jsonParser.parseJsonObject(json);

        Assert.assertEquals(objectMapper.readTree(json), objectMapper.readTree(objectMapper.writeValueAsString(parsed)));
    }

    @Test
    public void testJsonNumberArrayUniformValueType() {

        JsonParser jsonParser = JsonParser.newBuilder().jsonNumberArrayUniformValueType(true).build();

        String json = """
                {
                "intArray" : [5, 4, 3, 2],
                "longArray" : [14, 3000000005, 3000000004, 3000000003, 3000000002],
                "mixedIntLongNumberArray" : [null, 14, 300000.0004, 3000000003, 3000000002, null]
                }
                """;
        Map<String, Object> jsonMap = jsonParser.parseJsonObject(json);
        List<?> intArray = (List<?>) jsonMap.get("intArray");
        for (Object each : intArray) {
            Assert.assertTrue(each instanceof Integer);
        }

        List<?> longArray = (List<?>) jsonMap.get("longArray");
        for (Object each : longArray) {
            Assert.assertTrue(each instanceof Long);
        }

        List<?> mixedIntLongNumberArray = (List<?>) jsonMap.get("mixedIntLongNumberArray");
        for (Object each : mixedIntLongNumberArray) {
            if (each != null) {
                Assert.assertTrue(each instanceof BigDecimal);
            }
        }
        Assert.assertNull(mixedIntLongNumberArray.get(0));
        Assert.assertNull(mixedIntLongNumberArray.get(mixedIntLongNumberArray.size() - 1));
    }

    @Test
    public void testParseJsonString() throws JsonProcessingException {
        String jsonStringValue1 = "\" Some bullet \u2022 is here.\ud83D\uDe80 \"";
        String expectedJsonStringValue1 = jsonStringValue1.substring(1, jsonStringValue1.length() - 1);
        Assert.assertEquals(expectedJsonStringValue1, objectMapper.readValue(jsonStringValue1, String.class));

        JsonParser jsonParser = JsonParser.newBuilder().validateEscapeSequence(true).build();
        Object value = jsonParser.parseJson(jsonStringValue1);
        Assert.assertTrue(value instanceof String);

        Assert.assertEquals(expectedJsonStringValue1, value);
        Assert.assertEquals(expectedJsonStringValue1, jsonParser.parseJsonEncodedStringValue(jsonStringValue1));
    }

    @Test
    public void testJsonParser() throws JsonProcessingException {
        ObjectNode jsonObject = objectMapper.createObjectNode();

        jsonObject.put("key1", Long.MAX_VALUE);
        jsonObject.put("key2", "value2");
        jsonObject.put("key3", true);
        jsonObject.put("key4", 14.01D);

        ObjectNode jsonObject1 = objectMapper.createObjectNode();
        jsonObject1.put("key1", 1);
        jsonObject1.put("key2", "value2");
        jsonObject1.put("key3", true);
        jsonObject1.put("jsonObject", jsonObject);

        ObjectNode jsonObject2 = objectMapper.createObjectNode();
        jsonObject2.put("key1", 1);
        jsonObject2.put("key2", "value2");
        jsonObject2.put("key3", true);
        jsonObject2.put("jsonObject1", jsonObject1);

        ArrayNode jsonArray = objectMapper.createArrayNode();
        jsonArray.add("one");
        jsonArray.add("two");
        jsonArray.add("three");

        jsonObject2.put("jsonArray", jsonArray);


        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        ArrayNode arrayNode1 = objectMapper.createArrayNode();
        arrayNode1.add("one");
        arrayNode1.add("two");
        arrayNode1.add("three");

        ObjectNode objectNode1 = objectMapper.createObjectNode();
        objectNode1.put("key1", 1);
        objectNode1.put("key2", "value2");
        objectNode1.put("key3", true);
        objectNode1.putPOJO("arrayNode1", arrayNode1);

        ObjectNode objectNode2 = objectMapper.createObjectNode();
        objectNode2.put("key1", 1);
        objectNode2.put("key2", "value2");
        objectNode2.put("key3", true);
        objectNode2.putPOJO("objectNode1", objectNode1);

        ArrayNode arrayNode2 = objectMapper.createArrayNode();
        arrayNode2.add("1one");
        arrayNode2.add("2two");
        arrayNode2.add("3three");

        ArrayNode arrayNode3 = objectMapper.createArrayNode();
        arrayNode3.add(1.401);
        arrayNode3.add(1.4);
        arrayNode3.add(14.01);

        ArrayNode arrayNode4 = objectMapper.createArrayNode();
        arrayNode4.add(true);
        arrayNode4.add(false);
        arrayNode4.add(true);

        ArrayNode arrayNode5 = objectMapper.createArrayNode();
        arrayNode5.add(1);
        arrayNode5.add(4);
        arrayNode5.add(0);
        arrayNode5.add(1);

        ObjectNode objectNode3 = objectMapper.createObjectNode();
        objectNode3.put("key1", 1);
        objectNode3.put("key2", "value2");
        objectNode3.put("key3", true);
        objectNode3.putPOJO("objectNode2", objectNode2);
        objectNode3.putPOJO("arrayNode2", arrayNode2);
        objectNode3.putPOJO("arrayNode3", arrayNode3);
        objectNode3.putPOJO("arrayNode4", arrayNode4);
        objectNode3.putPOJO("arrayNode5", arrayNode5);

        String json = objectMapper.writeValueAsString(new JsonParser().parseJson(objectNode3.toString()));

        Assert.assertEquals(objectMapper.readTree(objectNode3.toString()), objectMapper.readTree(json));

        String somevalue = new JsonValue("somevalue", JsonValueType.STRING).asString();
        Assert.assertEquals("somevalue", somevalue);
    }

    @Test
    public void testJsonMapFactory() {
        class CustomJsonMap extends JsonMap {
        }
        final JsonParser jsonParser = JsonParser.newBuilder().jsonObjectType(JsonObjectType.CUSTOM_JSON_MAP).jsonMapFactory(CustomJsonMap::new).build();
        final Object parsedJsonObject = jsonParser.parseJson("""
                {
                "key1": "value1",
                "key2": "value2"
                }
                """);
        final CustomJsonMap customJsonMap = parsedJsonObject instanceof CustomJsonMap map ? map : null;
        Assert.assertNotNull(customJsonMap);
        Assert.assertEquals("value1", customJsonMap.getValueAsString("key1"));
        Assert.assertEquals("value2", customJsonMap.getValueAsString("key2"));
    }

    @Test
    public void testJsonListFactory() {
        class CustomJsonList extends JsonList {
        }
        final JsonParser jsonParser = JsonParser.newBuilder().jsonArrayType(JsonArrayType.CUSTOM_JSON_LIST).jsonListFactory(CustomJsonList::new).build();
        final Object parsedJsonObject = jsonParser.parseJson("""
                ["value1", "value2"]                
                """);
        final CustomJsonList customJsonList = parsedJsonObject instanceof CustomJsonList list ? list : null;
        Assert.assertNotNull(customJsonList);
        Assert.assertEquals("value1", customJsonList.getValueAsString(0));
        Assert.assertEquals("value2", customJsonList.getValueAsString(1));
    }
}
