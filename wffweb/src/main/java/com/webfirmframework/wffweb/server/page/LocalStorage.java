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

import java.util.function.Consumer;

/**
 * @since 12.0.0-beta.4
 *
 */
public sealed interface LocalStorage permits LocalStorageImpl {

    record Item(String value, long updatedTimeMillis) {
    }

    record Event(String key, long operationTimeMillis, Item item) {
        public Event(final String key, final long operationTimeMillis) {
            this(key, operationTimeMillis, null);
        }

        public Event(final long operationTimeMillis) {
            this(null, operationTimeMillis, null);
        }
    }

    void setItem(String key, String value, Consumer<Event> successConsumer);

    void getItem(String key, Consumer<Event> consumer);

    void removeItem(String key, Consumer<Event> consumer);

    void removeAndGetItem(String key, Consumer<Event> consumer);

    void clear(Consumer<Event> consumer);
}
