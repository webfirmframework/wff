/*
 * Copyright 2014-2022 Web Firm Framework
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
package com.webfirmframework.wffweb.server.page.action;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import com.webfirmframework.wffweb.server.page.Task;
import com.webfirmframework.wffweb.util.StringUtil;
import com.webfirmframework.wffweb.util.WffBinaryMessageUtil;
import com.webfirmframework.wffweb.util.data.NameValue;

public enum BrowserPageAction {

    RELOAD(Task.RELOAD_BROWSER, null), RELOAD_FROM_CACHE(Task.RELOAD_BROWSER_FROM_CACHE, null);

    private static final byte[] PLACEHOLDER_BYTE_ARRAY_FOR_PAYLOAD_ID = new byte[4];

    private final byte[] actionBytes;

    private BrowserPageAction(final Task task, final String js) {
        actionBytes = init(task, js);
    }

    private byte[] init(final Task task, final String js) {
        // this should be locally declared because
        // BrowserPageAction.PLACEHOLDER_BYTE_ARRAY_FOR_PAYLOAD_ID gives null in this
        // scope
        final byte[] placeholderByteArrayForPayloadId = new byte[4];
        if (js != null) {
            final NameValue nameValue = new NameValue();
            nameValue.setName(js.getBytes(StandardCharsets.UTF_8));
            nameValue.setValues(new byte[][] {});

            final byte[] bmMsg = WffBinaryMessageUtil.VERSION_1
                    .getWffBinaryMessageBytes(Task.RELOAD_BROWSER.getTaskNameValue(), nameValue);

            final ByteBuffer byteBuffer = ByteBuffer.allocate(bmMsg.length + placeholderByteArrayForPayloadId.length);
            byteBuffer.put(placeholderByteArrayForPayloadId).put(bmMsg).rewind();
            return byteBuffer.array();
        } else {
            final byte[] bmMsg = WffBinaryMessageUtil.VERSION_1
                    .getWffBinaryMessageBytes(Task.RELOAD_BROWSER.getTaskNameValue());
            final ByteBuffer byteBuffer = ByteBuffer.allocate(bmMsg.length + placeholderByteArrayForPayloadId.length);
            byteBuffer.put(placeholderByteArrayForPayloadId).put(bmMsg).rewind();
            return byteBuffer.array();
        }
    }

    /**
     * @return the bytes for the browser action
     * @since 2.0.3
     * @author WFF
     */
    public byte[] getActionBytes() {
        return Arrays.copyOf(actionBytes, actionBytes.length);
    }

    /**
     * @return the action {@code ByteBuffer} for the browser action
     * @since 2.0.3
     * @author WFF
     */
    public ByteBuffer getActionByteBuffer() {
        return ByteBuffer.wrap(actionBytes);
    }

    /**
     * Gets the action {@code ByteBuffer} for executing the given JavaScript
     *
     * @param js JavaScript to execute in the browser
     * @return the action {@code ByteBuffer} for executing the given JavaScript in
     *         the browser.
     *
     * @since 2.1.0 initial implementation.
     * @since 3.0.15 throwing UnsupportedEncodingException is removed as announced
     *        in 3.0.1 release.
     * @author WFF
     */
    public static ByteBuffer getActionByteBufferForExecuteJS(final String js) {
        final byte[] actionBytesForExecuteJS = getActionBytesForExecuteJS(js, false);
        final ByteBuffer byteBuffer = ByteBuffer
                .allocate(actionBytesForExecuteJS.length + PLACEHOLDER_BYTE_ARRAY_FOR_PAYLOAD_ID.length);
        byteBuffer.put(PLACEHOLDER_BYTE_ARRAY_FOR_PAYLOAD_ID).put(actionBytesForExecuteJS).rewind();
        return byteBuffer;
    }

    /**
     * Gets the action {@code ByteBuffer} for executing the given JavaScript.
     *
     * @param js                      JavaScript to execute in the browser
     * @param onlyInOtherBrowserPages true to execute the js only on the other pages
     *                                not on the page on which the action is
     *                                performed (i.e.
     *                                {@code browserPage.performBrowserPageAction}
     *                                is called). Other pages include all other
     *                                pages opened in other tabs even if they are
     *                                loaded from different nodes. false to execute
     *                                the js only on the action performed browser
     *                                page not on other pages.
     * @return the action {@code ByteBuffer} for executing the given JavaScript in
     *         the browser.
     * @since 12.0.0-beta.4
     */
    public static ByteBuffer getActionByteBufferForExecuteJS(final String js, final boolean onlyInOtherBrowserPages) {
        final byte[] actionBytesForExecuteJS = getActionBytesForExecuteJS(js, onlyInOtherBrowserPages);
        final ByteBuffer byteBuffer = ByteBuffer
                .allocate(actionBytesForExecuteJS.length + PLACEHOLDER_BYTE_ARRAY_FOR_PAYLOAD_ID.length);
        byteBuffer.put(PLACEHOLDER_BYTE_ARRAY_FOR_PAYLOAD_ID).put(actionBytesForExecuteJS).rewind();
        return byteBuffer;
    }

    /**
     * Gets the action bytes for executing the given JavaScript in the browser.
     *
     * @param js JavaScript to execute in the browser
     * @return the action bytes for executing the given JavaScript in the browser.
     *
     * @since 2.1.0 initial implementation.
     * @since 3.0.15 throwing UnsupportedEncodingException is removed as announced
     *        in 3.0.1 release.
     * @author WFF
     */
    public static byte[] getActionBytesForExecuteJS(final String js) {
        final byte[] actionBytesForExecuteJS = getActionBytesForExecuteJS(js, false);
        final ByteBuffer byteBuffer = ByteBuffer
                .allocate(actionBytesForExecuteJS.length + PLACEHOLDER_BYTE_ARRAY_FOR_PAYLOAD_ID.length);
        byteBuffer.put(PLACEHOLDER_BYTE_ARRAY_FOR_PAYLOAD_ID).put(actionBytesForExecuteJS).rewind();
        return byteBuffer.array();
    }

    /**
     * Gets the action bytes for executing the given JavaScript in the browser.
     *
     * @param js                      JavaScript to execute in the browser
     * @param onlyInOtherBrowserPages true to execute the js only on the other pages
     *                                not on the page on which the action is
     *                                performed (i.e.
     *                                {@code browserPage.performBrowserPageAction}
     *                                is called). Other pages include all other
     *                                pages opened in other tabs even if they are
     *                                loaded from different nodes. false to execute
     *                                the js only on the action performed browser
     *                                page not on other pages.
     * @return the action bytes for executing the given JavaScript in the browser.
     * @since 12.0.0-beta.4
     */
    private static byte[] getActionBytesForExecuteJS(final String js, final boolean onlyInOtherBrowserPages) {

        // this method will never throw UnsupportedEncodingException
        // but not changing the method signature to keep consistency of this
        // method

        final NameValue taskNameValue = Task.EXEC_JS.getTaskNameValue();
        final byte[][] taskValue = taskNameValue.getValues();
        final byte[][] values = new byte[taskValue.length + 2][0];

        System.arraycopy(taskValue, 0, values, 0, taskValue.length);

        // previously it was passing "UTF-8" so it was throwing
        // UnsupportedEncodingException
        // but not it is not throwing this exception
        // however, do not change method signature to keep consistency of the
        // method accross multiple versions.
        // handling JsUtil.toDynamicJs at server side is much better otherwise if the
        // script is huge the client browser page might get frozen.
        values[taskValue.length] = StringUtil.strip(js).getBytes(StandardCharsets.UTF_8);

        values[taskValue.length + 1] = onlyInOtherBrowserPages ? new byte[] { 1 } : new byte[] { 0 };

        taskNameValue.setValues(values);

        return WffBinaryMessageUtil.VERSION_1.getWffBinaryMessageBytes(taskNameValue);
    }

}
