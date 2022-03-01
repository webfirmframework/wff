package com.webfirmframework.wffweb.common;

import static org.junit.Assert.*;

import org.junit.Test;

public class EventInitiatorTest {

    @Test
    public void testGetJsObjectString() {
        assertEquals("{SERVER_CODE:0,CLIENT_CODE:1,BROWSER:2,size:3}", URIEventInitiator.getJsObjectString());
    }

}
