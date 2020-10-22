/*
 * Copyright 2014-2020 Web Firm Framework
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

import com.webfirmframework.wffweb.js.JsUtil;
import com.webfirmframework.wffweb.server.page.Task;
import com.webfirmframework.wffweb.util.WffBinaryMessageUtil;
import com.webfirmframework.wffweb.util.data.NameValue;

public enum BrowserPageAction {

    RELOAD(Task.RELOAD_BROWSER, null), RELOAD_FROM_CACHE(Task.RELOAD_BROWSER_FROM_CACHE, null);

    private byte[] actionBytes;

    private BrowserPageAction(final Task task, final String js) {
        init(task, js);
    }

    private void init(final Task task, final String js) {
        if (js != null) {

            final NameValue nameValue = new NameValue();
            nameValue.setName(js.getBytes(StandardCharsets.UTF_8));
            nameValue.setValues(new byte[][] {});

            actionBytes = WffBinaryMessageUtil.VERSION_1
                    .getWffBinaryMessageBytes(Task.RELOAD_BROWSER.getTaskNameValue(), nameValue);
        } else {
            actionBytes = WffBinaryMessageUtil.VERSION_1
                    .getWffBinaryMessageBytes(Task.RELOAD_BROWSER.getTaskNameValue());
        }
    }

    /**
     * @return the bytes for the browser action
     * @since 2.0.3
     * @author WFF
     */
    public byte[] getActionBytes() {
        return actionBytes;
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
        return ByteBuffer.wrap(getActionBytesForExecuteJS(js));
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

        // this method will never throw UnsupportedEncodingException
        // but not changing the method signature to keep consistency of this
        // method

        final NameValue taskNameValue = Task.EXEC_JS.getTaskNameValue();
        final byte[][] taskValue = taskNameValue.getValues();
        final byte[][] values = new byte[taskValue.length + 1][0];

        System.arraycopy(taskValue, 0, values, 0, taskValue.length);

        // previously it was passing "UTF-8" so it was throwing
        // UnsupportedEncodingException
        // but not it is not throwing this exception
        // however, do not change method signature to keep consistency of the
        // method accross multiple versions.
        // handling JsUtil.toDynamicJs at server side is much better otherwise if the
        // script is huge the client browser page might get frozen.
        values[taskValue.length] = JsUtil.toDynamicJs(js).getBytes(StandardCharsets.UTF_8);

        taskNameValue.setValues(values);

        return WffBinaryMessageUtil.VERSION_1.getWffBinaryMessageBytes(taskNameValue);
    }

}
