/*
 * Copyright 2014-2016 Web Firm Framework
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
package com.webfirmframework.wffweb.server.page;

import com.webfirmframework.wffweb.util.data.NameValue;

/**
 * @author WFF
 * @since 2.0.0
 */
public enum Task {

    INVOKE_ASYNC_METHOD,

    ATTRIBUTE_UPDATED,

    TASK,

    APPENDED_CHILD_TAG,

    REMOVED_TAGS,

    APPENDED_CHILDREN_TAGS,

    REMOVED_ALL_CHILDREN_TAGS,

    MOVED_CHILDREN_TAGS,

    REMOVED_ATTRIBUTES,

    ADDED_ATTRIBUTES,

    MANY_TO_ONE,

    ONE_TO_MANY,

    MANY_TO_MANY,

    ONE_TO_ONE,

    ADDED_INNER_HTML,

    INVOKE_POST_FUNCTION,

    EXECURE_JS,

    RELOAD_BROWSER,

    RELOAD_BROWSER_FROM_CACHE;

    private byte valueByte;

    private String jsNameValue;

    private static String jsObjectString;

    private Task() {
        valueByte = (byte) ordinal();
        jsNameValue = name() + ":" + ordinal();
    }

    /**
     * @return the valueByte
     */
    public byte getValueByte() {
        return valueByte;
    }

    public NameValue getTaskNameValue() {
        final NameValue task = new NameValue();
        task.setName(Task.TASK.getValueByte());
        task.setValues(new byte[][] { new byte[] { getValueByte() } });
        return task;
    }

    /**
     * @return the javascript object string, Eg:-
     *         <code>{ INVOKE_ASYNC_METHOD : 0,
     *         ATTRIBUTE_UPDATED : 1}</code>
     * @since 1.1.5
     * @author WFF
     */
    public static String getJsObjectString() {

        if (jsObjectString != null) {
            return jsObjectString;
        }

        // StringBuilder builder = new StringBuilder();
        // Task.values().length - 1 for , (comma)
        // 2 for opening + closing curly brace
        // (Task.values().length - 1) + 2;
        // when reduced it becomes Task.values().length + 1
        int totalLength = Task.values().length + 1;
        for (final Task task : Task.values()) {
            totalLength += task.jsNameValue.length();
        }

        final StringBuilder builder = new StringBuilder(totalLength);
        builder.append('{');
        for (final Task task : Task.values()) {
            builder.append(task.jsNameValue);
            builder.append(',');
        }

        builder.replace(builder.length() - 1, builder.length(), "}");

        jsObjectString = builder.toString();
        return jsObjectString;
    }

}
