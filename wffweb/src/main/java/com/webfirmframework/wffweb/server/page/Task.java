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
package com.webfirmframework.wffweb.server.page;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.webfirmframework.wffweb.util.data.NameValue;

/**
 * Only for internal purpose.
 *
 * @author WFF
 * @since 2.0.0
 */
public enum Task {

    INITIAL_WS_OPEN,

    INVOKE_ASYNC_METHOD,

    ATTRIBUTE_UPDATED,

    TASK,

    APPENDED_CHILD_TAG,

    REMOVED_TAGS,

    APPENDED_CHILDREN_TAGS,

    REMOVED_ALL_CHILDREN_TAGS,

    MOVED_CHILDREN_TAGS,

    INSERTED_BEFORE_TAG,

    INSERTED_AFTER_TAG,

    REPLACED_WITH_TAGS,

    REMOVED_ATTRIBUTES,

    ADDED_ATTRIBUTES,

    MANY_TO_ONE,

    ONE_TO_MANY,

    MANY_TO_MANY,

    ONE_TO_ONE,

    ADDED_INNER_HTML,

    INVOKE_POST_FUNCTION,

    EXEC_JS,

    RELOAD_BROWSER,

    RELOAD_BROWSER_FROM_CACHE,

    INVOKE_CALLBACK_FUNCTION,

    INVOKE_CUSTOM_SERVER_METHOD,

    TASK_OF_TASKS,

    COPY_INNER_TEXT_TO_VALUE,

    /**
     * to remove BrowserPage instance from BrowserPageContext
     */
    REMOVE_BROWSER_PAGE,

    /**
     * to set WffBMObject on tag
     */
    SET_BM_OBJ_ON_TAG,

    /**
     * to set WffBMArray on tag
     */
    SET_BM_ARR_ON_TAG,

    /**
     * to delete WffBMObject or WffBMArray from tag
     */
    DEL_BM_OBJ_OR_ARR_FROM_TAG,

    CLIENT_PATHNAME_CHANGED,

    /**
     * To execute afterSetURI method in the client if it is available
     */
    AFTER_SET_URI,

    /**
     * To execute history.pushState in the client if it is available
     */
    SET_URI,

    /**
     * To set localStorage item. LS stands for localStorage
     */
    SET_LS_ITEM,

    /**
     * To set localStorage token item. only one token can be set i.e. multiple token
     * entries are not allowed. It should always replace the same. It should always
     * use the same key. LS stands for localStorage
     */
    SET_LS_TOKEN,

    /**
     * To get localStorage item. LS stands for localStorage
     */
    GET_LS_ITEM,

    /**
     * To remove localStorage item. LS stands for localStorage
     */
    REMOVE_LS_ITEM,

    /**
     * To remove localStorage token item. only one token can be set so there will be
     * only one entry for token and the key will always be the same. LS stands for
     * localStorage
     */
    REMOVE_LS_TOKEN,

    /**
     * To remove localStorage item. LS stands for localStorage
     */
    REMOVE_AND_GET_LS_ITEM,

    /**
     * To clear localStorage. LS stands for localStorage
     */
    CLEAR_LS;

    private final String shortName;

    private final byte valueByte;

    private final String jsNameValue;

    private static String jsObjectString;

    // WffJsFile.PRODUCTION_MODE, there is bug while enabling this
    private static final boolean SHORT_NAME_ENABLED = false;

    private Task() {
        valueByte = (byte) ordinal();
        shortName = "T".concat(String.valueOf(ordinal()));
        jsNameValue = SHORT_NAME_ENABLED ? shortName.concat(":").concat(String.valueOf(ordinal()))
                : name().concat(":").concat(String.valueOf(ordinal()));
    }

    /**
     * @return the valueByte
     */
    public byte getValueByte() {
        return valueByte;
    }

    /**
     * @return nameValue with name as TASK byte value and values as the the current
     *         task/object byte value.
     * @since 2.0.0
     * @author WFF
     */
    public NameValue getTaskNameValue() {
        final NameValue task = new NameValue();
        task.setName(Task.TASK.getValueByte());
        task.setValues(new byte[][] { new byte[] { getValueByte() } });
        return task;
    }

    /**
     * @param additionalValues the additional values to be added in
     *                         nameValue.values, nameValue.values[0] will be
     *                         taskValueByte and nameValue.values[1],
     *                         nameValue.values[2].. will be the the given
     *                         additionalValues
     * @return nameValue with name as TASK byte value and values as the the current
     *         task/object byte value.
     * @since 12.0.0-beta.1
     * @author WFF
     */
    public NameValue getTaskNameValue(final byte[]... additionalValues) {
        final NameValue task = new NameValue();
        task.setName(Task.TASK.getValueByte());

        final byte[][] values = new byte[additionalValues.length + 1][];
        values[0] = new byte[] { getValueByte() };

        for (int i = 1; i < values.length; i++) {
            values[i] = additionalValues[i - 1];
        }

        task.setValues(values);
        return task;
    }

    /**
     * @return nameValue with name as TASK_OF_TASKS byte value and values as null
     * @since 2.1.3
     * @author WFF
     */
    public static NameValue getTaskOfTasksNameValue() {
        final NameValue task = new NameValue();
        task.setName(Task.TASK_OF_TASKS.getValueByte());
        return task;
    }

    /**
     * @return the javascript object string, Eg:- in the optimized form
     *         <code>{ T0 : 0,
     *         T1 : 1}</code> of <code>{ INVOKE_ASYNC_METHOD : 0,
     *         ATTRIBUTE_UPDATED : 1}</code>
     * @since 1.1.5
     * @since 2.1.8 optimized with shortName
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
        final Task[] allTasks = Task.values();
        int totalLength = allTasks.length + 1;
        for (final Task task : allTasks) {
            totalLength += task.jsNameValue.length();
        }

        final StringBuilder builder = new StringBuilder(totalLength);
        builder.append('{');
        for (final Task task : allTasks) {
            builder.append(task.jsNameValue).append(',');
        }

        builder.replace(builder.length() - 1, builder.length(), "}");

        jsObjectString = builder.toString();
        return jsObjectString;
    }

    /**
     * @return unique short name for the task
     * @since 2.1.8
     * @author WFF
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * @return the sorted set of tasks in the descending order of the length of task
     *         name.
     * @since 2.1.10
     * @since 3.0.15 bug fix
     * @author WFF
     */
    public static Set<Task> getSortedTasks() {

        // old impl
        // final Set<Task> sortedTaskNames = new TreeSet<>((o1, o2) -> {
        // // to sort the tasks in descending order of the length
        // // of the
        // // names
        // if (o1.name().length() > o2.name().length()) {
        // return -1;
        // }
        // if (o1.name().length() < o2.name().length()) {
        // return 1;
        // }
        // return -1;
        // });

        final Task[] values = Task.values();
        final List<Task> tasks = new ArrayList<>(values.length);
        for (final Task task : values) {
            tasks.add(task);
        }
        final Comparator<Task> asc = (o1, o2) -> Integer.compare(o1.name().length(), o2.name().length());

        tasks.sort(asc.thenComparing(Task::name));

        Collections.reverse(tasks);

        return new LinkedHashSet<>(tasks);
    }

}
