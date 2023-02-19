/*
 * Copyright 2014-2023 Web Firm Framework
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

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class ClientTasksWrapper {

    private volatile AtomicReferenceArray<ByteBuffer> tasks;

    private final Long queueEntryId;

    private volatile int currentSize;

    ClientTasksWrapper(final ByteBuffer... tasks) {
        super();
        this.tasks = new AtomicReferenceArray<>(tasks);
        queueEntryId = null;
        int totalLength = 0;
        for (final ByteBuffer task : tasks) {
            totalLength += task.capacity();
        }
        currentSize = totalLength;
    }

    ClientTasksWrapper(final long queueEntryId, final ByteBuffer... tasks) {
        super();
        this.tasks = new AtomicReferenceArray<>(tasks);
        this.queueEntryId = queueEntryId;
        int totalLength = 0;
        for (final ByteBuffer task : tasks) {
            totalLength += task.capacity();
        }
        currentSize = totalLength;
    }

    AtomicReferenceArray<ByteBuffer> tasks() {
        return tasks;
    }

    public void nullifyTasks() {
        tasks = null;
    }

    Long queueEntryId() {
        return queueEntryId;
    }

    /**
     * @param taskLength the length of the nullifying task at the given index
     * @param tasks      the tasks to nullify at given index
     * @param index      the index where to nullify
     * @since 12.0.0-beta.8
     */
    void nullifyTask(final int taskLength, final AtomicReferenceArray<ByteBuffer> tasks, final int index) {
        // tasks should be passed from argument as the instance tasks may be nullified
        // by other thread at any time
        tasks.set(index, null);
        // should be locally initialized first
        final int sizeLocal = currentSize;
        currentSize = sizeLocal - taskLength;
    }

    /**
     * @return the total size of all tasks.
     * @since 12.0.0-beta.8
     */
    int getCurrentSize() {
        return currentSize;
    }
}
