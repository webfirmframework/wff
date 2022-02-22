/*
 * Copyright 2014-2021 Web Firm Framework
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

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.Test;

public class TaskTest {

    @Test
    public void testGetSortedTasks() {
        final Set<Task> sortedTasks = Task.getSortedTasks();
        assertTrue(Task.values().length <= Byte.MAX_VALUE);
        assertEquals(Task.values().length, sortedTasks.size());
        assertEquals("[INVOKE_CUSTOM_SERVER_METHOD, DEL_BM_OBJ_OR_ARR_FROM_TAG, REMOVED_ALL_CHILDREN_TAGS, RELOAD_BROWSER_FROM_CACHE, INVOKE_CALLBACK_FUNCTION, COPY_INNER_TEXT_TO_VALUE, CLIENT_PATHNAME_CHANGED, REMOVE_AND_GET_LS_ITEM, APPENDED_CHILDREN_TAGS, INVOKE_POST_FUNCTION, REMOVE_BROWSER_PAGE, MOVED_CHILDREN_TAGS, INVOKE_ASYNC_METHOD, INSERTED_BEFORE_TAG, REPLACED_WITH_TAGS, REMOVED_ATTRIBUTES, INSERTED_AFTER_TAG, APPENDED_CHILD_TAG, SET_BM_OBJ_ON_TAG, SET_BM_ARR_ON_TAG, ATTRIBUTE_UPDATED, ADDED_INNER_HTML, ADDED_ATTRIBUTES, INITIAL_WS_OPEN, REMOVE_LS_ITEM, RELOAD_BROWSER, TASK_OF_TASKS, AFTER_SET_URI, REMOVED_TAGS, MANY_TO_MANY, SET_LS_ITEM, ONE_TO_MANY, MANY_TO_ONE, GET_LS_ITEM, ONE_TO_ONE, CLEAR_LS, SET_URI, EXEC_JS, TASK]", sortedTasks.toString());
    }

}
