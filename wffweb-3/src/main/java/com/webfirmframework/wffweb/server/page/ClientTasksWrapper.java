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
package com.webfirmframework.wffweb.server.page;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class ClientTasksWrapper {

    private volatile AtomicReferenceArray<ByteBuffer> tasks;

    private final Long queueEntryId;

    ClientTasksWrapper(final ByteBuffer... tasks) {
        super();
        this.tasks = new AtomicReferenceArray<>(tasks);
        queueEntryId = null;
    }

    ClientTasksWrapper(final long queueEntryId, final ByteBuffer... tasks) {
        super();
        this.tasks = new AtomicReferenceArray<>(tasks);
        this.queueEntryId = queueEntryId;
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

}
