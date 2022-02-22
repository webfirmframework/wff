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

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * Note: only for internal use.
 *
 * @since 12.0.0-beta.4
 */
final class LocalStorageImpl implements LocalStorage {

    private final AtomicInteger getItemIdGenerator = new AtomicInteger(0);

    private final AtomicInteger setItemIdGenerator = new AtomicInteger(0);

    private final AtomicInteger removeItemIdGenerator = new AtomicInteger(0);

    private final AtomicInteger clearItemsIdGenerator = new AtomicInteger(0);

    // key: id by setItemIdGenerator
    private final Map<Integer, LSConsumerEventRecord> setItemConsumers = new ConcurrentHashMap<>(2);

    // key: id by getItemIdGenerator
    private final Map<Integer, LSConsumerEventRecord> getItemConsumers = new ConcurrentHashMap<>(2);

    // key: id by removeItemIdGenerator
    private final Map<Integer, LSConsumerEventRecord> removeItemConsumers = new ConcurrentHashMap<>(2);

    // key: id by clearItemsIdGenerator
    private final Map<Integer, LSConsumerEventRecord> clearItemsConsumers = new ConcurrentHashMap<>(2);

    private final Map<String, BrowserPage> browserPages;

    LocalStorageImpl(final Map<String, BrowserPage> browserPages) {
        this.browserPages = browserPages;
    }

    @Override
    public void setItem(final String key, final String value) {
        this.setItem(key, value, null);
    }

    @Override
    public void setItem(final String key, final String value, final Consumer<LocalStorage.Event> successConsumer) {

        if (value == null) {
            removeAndGetItem(key, successConsumer);
            return;
        }
        final String idString;
        final long operationTimeMillis = System.currentTimeMillis();
        if (successConsumer != null) {
            final int id = setItemIdGenerator.incrementAndGet();
            idString = String.valueOf(id);
            final LSConsumerEventRecord record = new LSConsumerEventRecord(successConsumer,
                    new LocalStorage.Event(key, operationTimeMillis, new Item(value, operationTimeMillis)));
            setItemConsumers.put(id, record);
        } else {
            idString = null;
        }

        for (final BrowserPage browserPage : browserPages.values()) {
            browserPage.setLocalStorageItem(idString, key, value, operationTimeMillis, setItemConsumers);
        }
    }

    @Override
    public void getItem(final String key, final Consumer<Event> consumer) {
        Objects.requireNonNull(consumer);
        final int id = getItemIdGenerator.incrementAndGet();
        final long operationTimeMillis = System.currentTimeMillis();
        final String idString = String.valueOf(id);
        final LSConsumerEventRecord record = new LSConsumerEventRecord(consumer,
                new LocalStorage.Event(key, operationTimeMillis));
        getItemConsumers.put(id, record);
        for (final BrowserPage browserPage : browserPages.values()) {
            browserPage.getLocalStorageItem(idString, key, getItemConsumers);
        }
    }

    @Override
    public void removeItem(final String key) {
        this.removeItem(key, null);
    }

    @Override
    public void removeItem(final String key, final Consumer<Event> consumer) {

        final String idString;
        final long operationTimeMillis = System.currentTimeMillis();

        if (consumer != null) {
            final int id = removeItemIdGenerator.incrementAndGet();

            idString = String.valueOf(id);
            final LSConsumerEventRecord record = new LSConsumerEventRecord(consumer,
                    new LocalStorage.Event(key, operationTimeMillis));
            removeItemConsumers.put(id, record);
        } else {
            idString = null;
        }

        for (final BrowserPage browserPage : browserPages.values()) {
            browserPage.removeLocalStorageItem(idString, key, operationTimeMillis, removeItemConsumers);
        }
    }

    @Override
    public void removeAndGetItem(final String key, final Consumer<Event> consumer) {
        Objects.requireNonNull(consumer);
        final int id = removeItemIdGenerator.incrementAndGet();
        final long operationTimeMillis = System.currentTimeMillis();
        final String idString = String.valueOf(id);
        final LSConsumerEventRecord record = new LSConsumerEventRecord(consumer,
                new LocalStorage.Event(key, operationTimeMillis));
        removeItemConsumers.put(id, record);
        for (final BrowserPage browserPage : browserPages.values()) {
            browserPage.removeAndGetLocalStorageItem(idString, key, operationTimeMillis, removeItemConsumers);
        }
    }

    @Override
    public void clear() {
        this.clear(null);
    }

    @Override
    public void clear(final Consumer<Event> consumer) {

        final String idString;
        final long operationTimeMillis = System.currentTimeMillis();
        if (consumer != null) {
            final int id = clearItemsIdGenerator.incrementAndGet();
            idString = String.valueOf(id);
            final LSConsumerEventRecord record = new LSConsumerEventRecord(consumer,
                    new LocalStorage.Event(operationTimeMillis));
            clearItemsConsumers.put(id, record);
        } else {
            idString = null;
        }
        for (final BrowserPage browserPage : browserPages.values()) {
            browserPage.clearLocalStorageItems(idString, operationTimeMillis, clearItemsConsumers);
        }
    }
}