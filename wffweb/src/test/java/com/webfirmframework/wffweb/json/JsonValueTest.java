package com.webfirmframework.wffweb.json;

import org.junit.Assert;
import org.junit.Test;

public class JsonValueTest {

    @Test
    public void toJsonString() {
        Assert.assertEquals("\"somevalue\"", new JsonValue("somevalue", JsonValueType.STRING).toJsonString());
        Assert.assertEquals("true", new JsonValue("true", JsonValueType.BOOLEAN).toJsonString());
        Assert.assertEquals("false", new JsonValue("false", JsonValueType.BOOLEAN).toJsonString());
        Assert.assertEquals("14.01", new JsonValue("14.01", JsonValueType.NUMBER).toJsonString());
        Assert.assertEquals("1401", new JsonValue("1401", JsonValueType.NUMBER).toJsonString());
        Assert.assertEquals("null", new JsonValue("null", JsonValueType.NULL).toJsonString());
    }

}