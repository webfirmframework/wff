package com.webfirmframework.wffweb.server.page;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.Test;

public class TaskTest {

    @Test
    public void testGetSortedTasks() {
        final Set<Task> sortedTasks = Task.getSortedTasks();
        assertEquals(Task.values().length, sortedTasks.size());
        assertEquals("[INVOKE_CUSTOM_SERVER_METHOD, DEL_BM_OBJ_OR_ARR_FROM_TAG, REMOVED_ALL_CHILDREN_TAGS, RELOAD_BROWSER_FROM_CACHE, INVOKE_CALLBACK_FUNCTION, COPY_INNER_TEXT_TO_VALUE, APPENDED_CHILDREN_TAGS, INVOKE_POST_FUNCTION, REMOVE_BROWSER_PAGE, MOVED_CHILDREN_TAGS, INVOKE_ASYNC_METHOD, INSERTED_BEFORE_TAG, REPLACED_WITH_TAGS, REMOVED_ATTRIBUTES, INSERTED_AFTER_TAG, APPENDED_CHILD_TAG, SET_BM_OBJ_ON_TAG, SET_BM_ARR_ON_TAG, ATTRIBUTE_UPDATED, ADDED_INNER_HTML, ADDED_ATTRIBUTES, RELOAD_BROWSER, TASK_OF_TASKS, REMOVED_TAGS, MANY_TO_MANY, ONE_TO_MANY, MANY_TO_ONE, ONE_TO_ONE, EXEC_JS, TASK]", sortedTasks.toString());
    }

}
