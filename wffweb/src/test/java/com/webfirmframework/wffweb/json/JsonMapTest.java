package com.webfirmframework.wffweb.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.webfirmframework.wffweb.InvalidValueException;
import com.webfirmframework.wffweb.wffbm.data.WffBMArray;
import com.webfirmframework.wffweb.wffbm.data.WffBMNumberArray;
import com.webfirmframework.wffweb.wffbm.data.WffBMObject;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;

public class JsonMapTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testJsonMapParse() throws JsonProcessingException {
        String json = """
                {
                "key" : "value",
                "key1" : "value1"
                }
                """;
        JsonMapNode jsonMap = JsonMap.parse(json);
        Assert.assertNotNull(jsonMap);

        Assert.assertEquals(objectMapper.readTree(json), objectMapper.readTree(jsonMap.toString()));
    }

    @Test
    public void testJsonMapParse2() throws JsonProcessingException {
        String json = """
                {
                   "string": "Hello, World!\\nNew Line \u2022 Bullet",
                   "number": 12345678901234567890,
                   "boolean_true": true,
                   "boolean_false": false,
                   "null_value": null,
                   "empty_object": {},
                   "empty_array": [],
                   "nested_object": {
                     "level1": {
                       "level2": {
                         "level3": {
                           "key": "value"
                         }
                       }
                     }
                   },
                   "nested_array": [[[[["deep"]]]]],
                   "mixed_array": [123, "text", true, null, {"key": "value"}, [1, 2, 3]],
                   "special_chars": " \\" \\\\ / \\b \\f \\n \\r \\t \\/ ",
                   "unicode": "𝒜𝒷𝒸𝒹",
                   "large_number": 1.7976931348623157e+308,
                   "negative_number": -0.000123,
                   "escaped_unicode": "\uD83D\uDE80 \\uWXYZ"
                 }
                """;
        JsonMapNode jsonMap = JsonParser.newBuilder().jsonObjectType(JsonObjectType.JSON_MAP).validateEscapeSequence(false).build().parseJsonObject(json) instanceof JsonMapNode mapNode ? mapNode : null;
        Assert.assertNotNull(jsonMap);
        Assert.assertEquals("Hello, World!\\nNew Line • Bullet", jsonMap.getValueAsString("string"));
        Assert.assertEquals(" \\\" \\\\ / \\b \\f \\n \\r \\t \\/ ", jsonMap.getValueAsString("special_chars"));
        String value = jsonMap.getValueAsString("escaped_unicode");
        Assert.assertEquals("\uD83D\uDE80 \\uWXYZ", value);
        System.out.println("Arrays.toString(value.codePoints().toArray()) = " + Arrays.toString(value.codePoints().toArray()));
        System.out.println("jsonMap.toJsonString = " + jsonMap.toJsonString());

         Assert.assertEquals("{\"string\":\"Hello, World!\\nNew Line • Bullet\",\"escaped_unicode\":\"\uD83D\uDE80 \\\\uWXYZ\",\"null_value\":null,\"empty_object\":{},\"negative_number\":-0.000123,\"nested_object\":{\"level1\":{\"level2\":{\"level3\":{\"key\":\"value\"}}}},\"special_chars\":\" \\\" \\\\ / \\b \\f \\n \\r \\t \\/ \",\"nested_array\":[[[[[\"deep\"]]]]],\"number\":12345678901234567890,\"empty_array\":[],\"boolean_false\":false,\"mixed_array\":[123,\"text\",true,null,{\"key\":\"value\"},[1,2,3]],\"unicode\":\"\uD835\uDC9C\uD835\uDCB7\uD835\uDCB8\uD835\uDCB9\",\"large_number\":1.7976931348623157E+308,\"boolean_true\":true}", jsonMap.toJsonString());
//         Assert.assertEquals(objectMapper.readTree(json), objectMapper.readTree(jsonMap.toString()));
//         Assert.assertEquals(objectMapper.readTree(json).toString(), objectMapper.readTree(jsonMap.toString()).toString());
    }

    @Test
    public void testJsonMapParseEmptyJsonObject() throws JsonProcessingException {
        String json = """
                {
                
                }
                """;
        JsonMapNode jsonMap = JsonMap.parse(json);
        Assert.assertNotNull(jsonMap);

        Assert.assertEquals(objectMapper.readTree(json), objectMapper.readTree(jsonMap.toJsonString()));
    }

    @Test
    public void testJsonMapParseForEmptyStringValue() {
        JsonMap parsed = JsonMap.parse("""
                {
                "key" : "value",
                "key1" : "value1",
                "key3" : {"k" : ""}
                }
                """);
        Assert.assertNotNull(parsed);
    }

    @Test
    public void testJsonMapParseForEmptyStringValue2() {
        String json = """
                {
                  "intArray" : [5, 4, 3, 2],
                  "longArray" : [3000000005, 3000000004, 3000000003, 3000000002],
                  "mixedIntLongNumberArray" : [null, 14, 300000.0004, 3000000003, 3000000002, null],
                  "k4imojiVal" : "Hello 🌍筀🍻👻🕻🥻!",
                  "k2" : "v2{\\"k\\":\\"v\\"}[]",
                  "web-app": {
                    "servlet": [
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
        JsonMap parsed = JsonMap.parse(json);
        Assert.assertNotNull(parsed);
    }

    @Test
    public void testJsonLinkedMapParse() {
        JsonLinkedMap parsed = JsonLinkedMap.parse("""
                {
                "key" : "value",
                "key1" : "value1"
                }
                """);
        Assert.assertNotNull(parsed);
    }

    @Test
    public void testJsonConcurrentMapParse() {
        JsonConcurrentMap parse = JsonConcurrentMap.parse("""
                {
                "key" : "value",
                "key1" : "value1"
                }
                """);
        Assert.assertNotNull(parse);
    }

    @Test
    public void testJsonMapToJsonString() {
        JsonMap jsonMap = new JsonMap();
        // {\"k\":\"v\"} with \\ \"
        String value = "{\\\"k\\\":\\\"v\\\"} with \\\\ \\\" ";
        String key = "key4String";
        jsonMap.put(key, value);
        String expected = """
                {"%s":"%s"}
                """.formatted(key, value).strip();
        Assert.assertEquals(expected, jsonMap.toJsonString());
        jsonMap.put("anotherkey", "val\\");
        Assert.assertEquals("""
                {"anotherkey":"val\\\\","key4String":"{\\"k\\":\\"v\\"} with \\\\ \\" "}
                """.strip(), jsonMap.toJsonString());
    }

    @Test
    public void testJsonMapToJsonString2() {
        JsonMap jsonMap = new JsonMap();
        // {\"k\":\"v\"} with \\ \"
        String value = "\\uWXYZ";
        String key = "key4String";
        jsonMap.put(key, value);
        String expected = """
                {"%s":"%s"}
                """.formatted(key, "\\\\uWXYZ").strip();
        Assert.assertEquals(expected, jsonMap.toString());
    }

    @Test
    public void testJsonMapGetValueAs() {
        JsonMap jsonMap = JsonMap.parse("""
                {
                "key" : "value",
                "key1" : "value1",
                "key3" : 14.01,
                "key4" : 14
                }
                """);
        Assert.assertNotNull(jsonMap);
        String value = jsonMap.getValueAsString("key1");
        Assert.assertNotNull(value);
        Assert.assertEquals("value1", value);
        Assert.assertEquals(14.01D, jsonMap.getValueAsDouble("key3"), 0);
        Assert.assertEquals(new BigDecimal("14.01").doubleValue(), jsonMap.getValueAsBigDecimal("key3").doubleValue(), 0);
        Assert.assertEquals(new BigInteger("14").longValue(), jsonMap.getValueAsBigInteger("key4").longValue(), 0);
        Assert.assertEquals(14, jsonMap.getValueAsInteger("key4").intValue());
        Assert.assertEquals(14L, jsonMap.getValueAsLong("key4").longValue());
    }


    @Test
    public void testJsonMapToWffBMObject() {
        JsonMap jsonMap = JsonMap.parse("""
                { "k1" : "value1", "k2" : "value2", "k3" : null, "k4" : true, "k5" : false, "k6" : 14.01 }
                """);
        Assert.assertNotNull(jsonMap);
        WffBMObject wffBMObject = jsonMap.toWffBMObject();
        Assert.assertNotNull(wffBMObject);
        String valueAt0 = wffBMObject.getValueAsString("k1");
        Assert.assertNotNull(valueAt0);
        Assert.assertEquals("value1", valueAt0);

        String valueAt1 = wffBMObject.getValueAsString("k2");
        Assert.assertNotNull(valueAt1);
        Assert.assertEquals("value2", valueAt1);

        String valueAt2 = wffBMObject.getValueAsString("k3");
        Assert.assertNull(valueAt2);

        Assert.assertEquals(true, wffBMObject.getValueAsBoolean("k4"));
        Assert.assertEquals(false, wffBMObject.getValueAsBoolean("k5"));
        Assert.assertEquals(new BigDecimal("14.01").doubleValue(), wffBMObject.getValueAsBigDecimal("k6").doubleValue(), 0);
    }

    @Test
    public void testJsonMapToWffBMObject1() {
        JsonMap jsonMap = new JsonMap();
        jsonMap.put("k1","value1");
        jsonMap.put("k2","value2");
        jsonMap.put("k3", null);
        jsonMap.put("k4",true);
        jsonMap.put("k5",false);
        jsonMap.put("k6", new BigDecimal("14.01"));
        jsonMap.put("k7", 14.01);

        WffBMObject wffBMObject = jsonMap.toWffBMObject();
        Assert.assertNotNull(wffBMObject);
        String valueAt0 = wffBMObject.getValueAsString("k1");
        Assert.assertNotNull(valueAt0);
        Assert.assertEquals("value1", valueAt0);

        String valueAt1 = wffBMObject.getValueAsString("k2");
        Assert.assertNotNull(valueAt1);
        Assert.assertEquals("value2", valueAt1);

        Assert.assertEquals(true, wffBMObject.getValueAsBoolean("k4"));
        Assert.assertEquals(false, wffBMObject.getValueAsBoolean("k5"));
        Assert.assertEquals(new BigDecimal("14.01").doubleValue(), wffBMObject.getValueAsBigDecimal("k6").doubleValue(), 0);
        Assert.assertEquals(new BigDecimal("14.01").doubleValue(), wffBMObject.getValueAsDouble("k7").doubleValue(), 0);
    }

    @Test
    public void testJsonMapToWffBMObjectIntegerValue() {
        JsonMap jsonMap = JsonMap.parse("""
                { "k1" : 14, "k2" : 1, "k3" : 19 }
                """);
        Assert.assertNotNull(jsonMap);
        WffBMObject wffBMObject = jsonMap.toWffBMObject();
        Assert.assertNotNull(wffBMObject);
        
        Assert.assertEquals(14, wffBMObject.getValueAsInteger("k1").intValue());
        Assert.assertEquals(1, wffBMObject.getValueAsInteger("k2").intValue());
        Assert.assertEquals(19, wffBMObject.getValueAsInteger("k3").intValue());
    }

    @Test
    public void testJsonMapToWffBMObjectIntegerValue1() {
        JsonMap jsonMap = new JsonMap();
        jsonMap.put("k1", 14);
        jsonMap.put("k2", 1);
        jsonMap.put("k3", 19);

        WffBMObject wffBMObject = jsonMap.toWffBMObject();
        Assert.assertNotNull(wffBMObject);
        Assert.assertEquals(14, wffBMObject.getValueAsInteger("k1").intValue());
        Assert.assertEquals(1, wffBMObject.getValueAsInteger("k2").intValue());
        Assert.assertEquals(19, wffBMObject.getValueAsInteger("k3").intValue());
    }


    @Test
    public void testJsonMapToWffBMObjectLongValue() {
        JsonMap jsonMap = JsonMap.parse("""
                { "k1" : 3000000005, "k2" : 3000000004, "k3" : 3000000003 }
                """);
        Assert.assertNotNull(jsonMap);
        WffBMObject wffBMObject = jsonMap.toWffBMObject();
        Assert.assertNotNull(wffBMObject);
        
        Assert.assertEquals(3000000005L, wffBMObject.getValueAsLong("k1").longValue());
        Assert.assertEquals(3000000004L, wffBMObject.getValueAsLong("k2").longValue());
        Assert.assertEquals(3000000003L, wffBMObject.getValueAsLong("k3").longValue());
    }

    @Test
    public void testJsonMapToWffBMObjectLongValue1() {
        JsonMap jsonMap = new JsonMap();
        jsonMap.put("k1", 3000000005L);
        jsonMap.put("k2", 3000000004L);
        jsonMap.put("k3", 3000000003L);

        WffBMObject wffBMObject = jsonMap.toWffBMObject();
        Assert.assertNotNull(wffBMObject);

        Assert.assertEquals(3000000005L, wffBMObject.getValueAsLong("k1").longValue());
        Assert.assertEquals(3000000004L, wffBMObject.getValueAsLong("k2").longValue());
        Assert.assertEquals(3000000003L, wffBMObject.getValueAsLong("k3").longValue());
    }

    @Test
    public void testJsonMapToWffBMObjectDoubleValue() {
        JsonMap jsonMap = JsonMap.parse("""
                { "k1" : 14.01, "k2" : 1.19, "k3" : 11.9 }
                """);
        Assert.assertNotNull(jsonMap);
        WffBMObject wffBMObject = jsonMap.toWffBMObject();
        Assert.assertNotNull(wffBMObject);
        
        Assert.assertEquals(14.01D, wffBMObject.getValueAsDouble("k1").doubleValue(), 0);
        Assert.assertEquals(1.19D, wffBMObject.getValueAsDouble("k2").doubleValue(), 0);
        Assert.assertEquals(11.9D, wffBMObject.getValueAsDouble("k3").doubleValue(), 0);
    }

    @Test
    public void testJsonMapToWffBMObjectDoubleValue1() {
        JsonMap jsonMap = new JsonMap();
        jsonMap.put("k1", 14.01D);
        jsonMap.put("k2", 1.19D);
        jsonMap.put("k3", 11.9D);

        WffBMObject wffBMObject = jsonMap.toWffBMObject();
        Assert.assertNotNull(wffBMObject);

        Assert.assertEquals(14.01D, wffBMObject.getValueAsDouble("k1").doubleValue(), 0);
        Assert.assertEquals(1.19D, wffBMObject.getValueAsDouble("k2").doubleValue(), 0);
        Assert.assertEquals(11.9D, wffBMObject.getValueAsDouble("k3").doubleValue(), 0);
    }


    @Test
    public void testJsonMapToWffBMObjectBigDecimalValue() {
        JsonMap jsonMap = JsonMap.parse("""
                { "k1" : 14.01, "k2" : 1.19, "k3" : 11.9 }
                """);
        Assert.assertNotNull(jsonMap);
        WffBMObject wffBMObject = jsonMap.toWffBMObject();
        Assert.assertNotNull(wffBMObject);
        
        Assert.assertEquals(14.01D, wffBMObject.getValueAsBigDecimal("k1").doubleValue(), 0);
        Assert.assertEquals(1.19D, wffBMObject.getValueAsBigDecimal("k2").doubleValue(), 0);
        Assert.assertEquals(11.9D, wffBMObject.getValueAsBigDecimal("k3").doubleValue(), 0);
    }

    @Test
    public void testJsonMapToWffBMObjectBigDecimalValue1() {
        JsonMap jsonMap = new JsonMap();
        jsonMap.put("k1", new BigDecimal("14.01"));
        jsonMap.put("k2", new BigDecimal("1.19"));
        jsonMap.put("k3", new BigDecimal("11.9"));

        WffBMObject wffBMObject = jsonMap.toWffBMObject();
        Assert.assertNotNull(wffBMObject);

        Assert.assertEquals(14.01D, wffBMObject.getValueAsBigDecimal("k1").doubleValue(), 0);
        Assert.assertEquals(1.19D, wffBMObject.getValueAsBigDecimal("k2").doubleValue(), 0);
        Assert.assertEquals(11.9D, wffBMObject.getValueAsBigDecimal("k3").doubleValue(), 0);
    }

    @Test(expected = InvalidValueException.class)
    public void testJsonMapToWffBMObjectInvalidValueException() {
        JsonMap jsonMap = new JsonMap();
        jsonMap.put("somekey", new Object());
        jsonMap.toWffBMObject();
    }

    @Test
    public void testJsonMapToWffBMObjectEmpty() {
        JsonMap jsonMap = new JsonMap();
        WffBMObject wffBMObject = jsonMap.toWffBMObject();
        Assert.assertNotNull(wffBMObject);
        Assert.assertTrue(wffBMObject.isEmpty());
    }

    @Test
    public void testJsonMapToWffBMObjectWithWffBMObjectValue() {
        JsonMap jsonMapOuterMost = new JsonMap();
        JsonMap jsonMap = new JsonMap();
        jsonMap.put("k1","value1");
        jsonMap.put("k2","value2");
        jsonMap.put("k3", null);
        jsonMap.put("k4",true);
        jsonMap.put("k5",false);
        jsonMap.put("k6", new BigDecimal("14.01"));
        jsonMap.put("k7", 14.01);

        jsonMapOuterMost.put("map", jsonMap);

        WffBMObject wffBMObjectOuter = jsonMapOuterMost.toWffBMObject();
        WffBMObject wffBMObject = wffBMObjectOuter.getValueAsWffBMObject("map");

        Assert.assertNotNull(wffBMObject);
        String valueAt0 = wffBMObject.getValueAsString("k1");
        Assert.assertNotNull(valueAt0);
        Assert.assertEquals("value1", valueAt0);

        String valueAt1 = wffBMObject.getValueAsString("k2");
        Assert.assertNotNull(valueAt1);
        Assert.assertEquals("value2", valueAt1);

        Assert.assertEquals(true, wffBMObject.getValueAsBoolean("k4"));
        Assert.assertEquals(false, wffBMObject.getValueAsBoolean("k5"));
        Assert.assertEquals(new BigDecimal("14.01").doubleValue(), wffBMObject.getValueAsBigDecimal("k6").doubleValue(), 0);
        Assert.assertEquals(new BigDecimal("14.01").doubleValue(), wffBMObject.getValueAsDouble("k7").doubleValue(), 0);
    }

    @Test
    public void testJsonMapToWffBMObjectWithWffBMArrayValue() {
        JsonMap jsonMapOuterMost = new JsonMap();
        JsonList jsonList = new JsonList();
        jsonList.add(14.01D);
        jsonList.add(1.19D);
        jsonList.add(11.9D);

        jsonMapOuterMost.put("list", jsonList);

        WffBMObject wffBMObjectOuter = jsonMapOuterMost.toWffBMObject();
        WffBMArray wffBMArray = wffBMObjectOuter.getValueAsWffBMArray("list");
        Assert.assertNotNull(wffBMArray);
        Assert.assertTrue(wffBMArray instanceof WffBMNumberArray<?>);
        Assert.assertEquals(14.01D, wffBMArray.getValueAsDouble(0).doubleValue(), 0);
        Assert.assertEquals(1.19D, wffBMArray.getValueAsDouble(1).doubleValue(), 0);
        Assert.assertEquals(11.9D, wffBMArray.getValueAsDouble(2).doubleValue(), 0);
    }

    @Test
    public void testJsonMapToJsonStringSpecialCase() {
        ObjectNode jsonObject = objectMapper.createObjectNode();
        String value = "\\\\";
        jsonObject.put("k", value);
        String expectedJson = """
                {"k":"%s"}
                """.formatted(value).strip();

        //fails, it makes {"k":"\\\\"}
//        Assert.assertEquals(expectedJson, jsonObject.toString());

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("k", value);
        //ObjectMapper also fails, it makes {"k":"\\\\"}
//        Assert.assertEquals(expectedJson, objectNode.toString());

        //the same with wffweb JsonMap handles it properly
        JsonMap jsonMap = new JsonMap();
        jsonMap.put("k", value);
        Assert.assertEquals(expectedJson, jsonMap.toJsonString());
        JsonMap jsonMap1 = new JsonMap();
        jsonMap1.put("k2", "val2");
        jsonMap.put("map", jsonMap1);
        Assert.assertEquals("{\"k\":\"\\\\\",\"map\":{\"k2\":\"val2\"}}", jsonMap.toJsonString());
    }

    @Test
    public void testJsonParsing() throws JsonProcessingException {
        String json = """
                {
                  "string" : "Hello, World!\\nNew Line • Bullet",
                  "escaped_unicode" : "🚀 \\\\WXYZ",
                  "null_value" : null,
                  "empty_object" : { },
                  "negative_number" : -1.23E-4,
                  "nested_object" : {
                    "level1" : {
                      "level2" : {
                        "level3" : {
                          "key" : "value"
                        }
                      }
                    }
                  },
                  "special_chars" : " \\" \\\\ / \\b \\f \\n \\r \\t / ",
                  "nested_array" : [ [ [ [ [ "deep" ] ] ] ] ],
                  "number" : 12345678901234567890,
                  "empty_array" : [ ],
                  "boolean_false" : false,
                  "mixed_array" : [ 123, "text", true, null, {
                    "key" : "value"
                  }, [ 1, 2, 3 ] ],
                  "unicode" : "𝒜𝒷𝒸𝒹",
                  "large_number" : 1.7976931348623157E308,
                  "boolean_true" : true
                }
                """;

        JsonMap jsonMap = JsonMap.parse(json);

        Assert.assertEquals(objectMapper.readTree(json), objectMapper.readTree(jsonMap.toJsonString()));

    }

    @Test
    public void testPerformance() {

    }

    @Test
    public void testToJsonStringAndToBigJsonStringPerformance() {
        JsonMap jsonMap = new JsonMap();
        for (int i = 0; i < 1000; i++) {
            jsonMap.put("key" + i, "value"+ i);
            JsonList jsonList = new JsonList();
            for (int j = 0; j < 1000; j++) {
                jsonList.add(j);
            }
            jsonMap.put("jsonList"+i, jsonList);
        }

        String jsonString = jsonMap.toJsonString();
        String bigJsonString = jsonMap.toBigJsonString();

        long before, after, diff;

        before = System.currentTimeMillis();
        jsonString = jsonMap.toJsonString();
        after = System.currentTimeMillis();
        diff = after - before;
        long jsonStringDiff = diff;
        System.out.println("   toJsonString diff = " + diff);

        before = System.currentTimeMillis();
        bigJsonString = jsonMap.toBigJsonString();
        after = System.currentTimeMillis();
        diff = after - before;
        long jsonBigStringDiff = diff;
        System.out.println("toBigJsonString diff = " + diff);

        System.out.println("(jsonStringDiff - jsonBigStringDiff) = " + (jsonStringDiff - jsonBigStringDiff));
    }

}
