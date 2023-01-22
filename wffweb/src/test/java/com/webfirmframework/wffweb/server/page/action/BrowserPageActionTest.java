package com.webfirmframework.wffweb.server.page.action;

import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.Arrays;

import static org.junit.Assert.*;

public class BrowserPageActionTest {

    @Test
    public void getActionByteBuffer() {
        ByteBuffer actionByteBuffer = BrowserPageAction.RELOAD.getActionByteBuffer();
        assertArrayEquals(new byte[] {0, 0, 0, 0, 1, 1, 1, 3, 2, 1, 21}, actionByteBuffer.array());
    }
}