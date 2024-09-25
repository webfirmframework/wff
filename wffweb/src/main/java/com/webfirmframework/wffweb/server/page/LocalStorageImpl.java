/*
 * Copyright 2014-2024 Web Firm Framework
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

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import com.webfirmframework.wffweb.BrowserPageNotFoundException;
import com.webfirmframework.wffweb.util.ObjectUtil;

/**
 * Note: only for internal use.
 *
 * @since 12.0.0-beta.4
 */
final class LocalStorageImpl implements LocalStorage {

    private final AtomicInteger itemIdGenerator = new AtomicInteger(0);

    private final AtomicInteger tokenIdGenerator = new AtomicInteger(0);

    // key: id by setItemIdGenerator
    final Map<Integer, LSConsumerEventRecord> setItemConsumers = new ConcurrentHashMap<>(2);

    // key: id by itemIdGenerator
    final Map<Integer, LSConsumerEventRecord> getItemConsumers = new ConcurrentHashMap<>(2);

    // key: id by removeItemIdGenerator
    final Map<Integer, LSConsumerEventRecord> removeItemConsumers = new ConcurrentHashMap<>(2);

    // key: id by clearItemsIdGenerator
    final Map<Integer, LSConsumerEventRecord> clearItemsConsumers = new ConcurrentHashMap<>(2);

    private final Map<String, BrowserPage> browserPages;

    final Map<String, TokenWrapper> tokenWrapperByKey = new ConcurrentHashMap<>(2);

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
            if (successConsumer != null) {
                getAndRemoveItem(key, successConsumer);
            } else {
                removeItem(key);
            }
            return;
        }
        ObjectUtil.requireNonNull(key, "key cannot be null");
        final Collection<BrowserPage> bps = browserPages.values();
        if (bps.isEmpty()) {
            throw new BrowserPageNotFoundException("There is no active browser page in the session to set item");
        }
        final int id = itemIdGenerator.incrementAndGet();
        final long operationTimeMillis = System.currentTimeMillis();
        final boolean callback = successConsumer != null;
        if (callback) {
            final LSConsumerEventRecord eventRecord = new LSConsumerEventRecord(successConsumer,
                    new LocalStorage.Event(key, operationTimeMillis, new ItemData(value, operationTimeMillis)));
            setItemConsumers.put(id, eventRecord);
        }

        for (final BrowserPage browserPage : bps) {
            browserPage.setLocalStorageItem(id, key, value, operationTimeMillis, callback);
        }
    }

    @Override
    public void getItem(final String key, final Consumer<Event> consumer) {
        ObjectUtil.requireNonNull(key, "key cannot be null");
        ObjectUtil.requireNonNull(consumer, "consumer cannot be null");
        final Collection<BrowserPage> bps = browserPages.values();
        if (bps.isEmpty()) {
            throw new BrowserPageNotFoundException("There is no active browser page in the session to get item");
        }
        final int id = itemIdGenerator.incrementAndGet();
        final long operationTimeMillis = System.currentTimeMillis();
        final LSConsumerEventRecord eventRecord = new LSConsumerEventRecord(consumer,
                new Event(key, operationTimeMillis));
        getItemConsumers.put(id, eventRecord);
        for (final BrowserPage browserPage : bps) {
            browserPage.getLocalStorageItem(id, key);
        }
    }

    @Override
    public void removeItem(final String key) {
        this.removeItem(key, null);
    }

    @Override
    public void removeItem(final String key, final Consumer<Event> consumer) {
        ObjectUtil.requireNonNull(key, "key cannot be null");

        final Collection<BrowserPage> bps = browserPages.values();
        if (bps.isEmpty()) {
            throw new BrowserPageNotFoundException("There is no active browser page in the session to remove item");
        }

        final int id = itemIdGenerator.incrementAndGet();
        final long operationTimeMillis = System.currentTimeMillis();

        final boolean callback = consumer != null;
        if (callback) {
            final LSConsumerEventRecord eventRecord = new LSConsumerEventRecord(consumer,
                    new LocalStorage.Event(key, operationTimeMillis));
            removeItemConsumers.put(id, eventRecord);
        }

        for (final BrowserPage browserPage : bps) {
            browserPage.removeLocalStorageItem(id, key, operationTimeMillis, callback);
        }
    }

    @Override
    public void getAndRemoveItem(final String key, final Consumer<Event> consumer) {
        ObjectUtil.requireNonNull(key, "key cannot be null");
        ObjectUtil.requireNonNull(consumer, "consumer cannot be null");
        final Collection<BrowserPage> bps = browserPages.values();
        if (bps.isEmpty()) {
            throw new BrowserPageNotFoundException(
                    "There is no active browser page in the session to remove and get item");
        }
        final int id = itemIdGenerator.incrementAndGet();
        final long operationTimeMillis = System.currentTimeMillis();
        final LSConsumerEventRecord eventRecord = new LSConsumerEventRecord(consumer,
                new LocalStorage.Event(key, operationTimeMillis));
        removeItemConsumers.put(id, eventRecord);
        for (final BrowserPage browserPage : bps) {
            browserPage.removeAndGetLocalStorageItem(id, key, operationTimeMillis);
        }
    }

    @Override
    public void clear() {
        this.clear(null);
    }

    @Override
    public void clearItems() {
        this.clearItems(null);
    }

    @Override
    public void clearTokens() {
        clearTokens(null);
    }

    @Override
    public void clear(final Consumer<Event> consumer) {
        final Collection<BrowserPage> bps = browserPages.values();
        if (bps.isEmpty()) {
            throw new BrowserPageNotFoundException(
                    "There is no active browser page in the session to clear items and tokens");
        }
        final int id = itemIdGenerator.incrementAndGet();
        final long operationTimeMillis = System.currentTimeMillis();
        final boolean callback = consumer != null;
        if (callback) {
            final LSConsumerEventRecord eventRecord = new LSConsumerEventRecord(consumer,
                    new LocalStorage.Event(operationTimeMillis));
            clearItemsConsumers.put(id, eventRecord);
        }

        removeAllTokens(operationTimeMillis);

        for (final BrowserPage browserPage : bps) {
            browserPage.clearLocalStorage(id, operationTimeMillis, callback);
        }
    }

    private void removeAllTokens(final long operationTimeMillis) {

        final Queue<TokenWrapper> toRemoveTokens = new ArrayDeque<>();
        for (final Map.Entry<String, TokenWrapper> entry : tokenWrapperByKey.entrySet()) {
            toRemoveTokens.add(entry.getValue());
        }

        final int id = toRemoveTokens.peek() != null ? tokenIdGenerator.incrementAndGet() : 0;

        TokenWrapper tokenWrapper = null;
        while ((tokenWrapper = toRemoveTokens.poll()) != null) {
            final long stamp = tokenWrapper.lock.writeLock();
            try {
                tokenWrapper.setTokenAndWriteTime(null, null, operationTimeMillis, id, tokenWrapper.key,
                        tokenWrapperByKey);
            } finally {
                tokenWrapper.lock.unlockWrite(stamp);
            }

        }
    }

    @Override
    public void clearItems(final Consumer<Event> consumer) {
        final Collection<BrowserPage> bps = browserPages.values();
        if (bps.isEmpty()) {
            throw new BrowserPageNotFoundException("There is no active browser page in the session to clear items");
        }
        final int id = itemIdGenerator.incrementAndGet();
        final long operationTimeMillis = System.currentTimeMillis();
        final boolean callback = consumer != null;
        if (callback) {
            final LSConsumerEventRecord eventRecord = new LSConsumerEventRecord(consumer,
                    new LocalStorage.Event(operationTimeMillis));
            clearItemsConsumers.put(id, eventRecord);
        }
        for (final BrowserPage browserPage : bps) {
            browserPage.clearLocalStorageItems(id, operationTimeMillis, callback);
        }
    }

    @Override
    public void clearTokens(final Consumer<Event> consumer) {
        final Collection<BrowserPage> bps = browserPages.values();
        if (bps.isEmpty()) {
            throw new BrowserPageNotFoundException("There is no active browser page in the session to clear tokens");
        }
        final int id = itemIdGenerator.incrementAndGet();
        final long operationTimeMillis = System.currentTimeMillis();
        final boolean callback = consumer != null;
        if (consumer != null) {
            final LSConsumerEventRecord eventRecord = new LSConsumerEventRecord(consumer,
                    new LocalStorage.Event(operationTimeMillis));
            clearItemsConsumers.put(id, eventRecord);
        }
        removeAllTokens(operationTimeMillis);
        for (final BrowserPage browserPage : bps) {
            browserPage.clearLocalStorageTokens(id, operationTimeMillis, callback);
        }
    }

    @Override
    public Item setToken(final String key, final String value) {
        if (key == null) {
            return null;
        }

        if (value != null) {
            final TokenWrapper tokenWrapper = tokenWrapperByKey.computeIfAbsent(key, TokenWrapper::new);
            final long stamp = tokenWrapper.lock.writeLock();
            try {
                final TokenData previousItem = new TokenData(tokenWrapper.getValue(),
                        tokenWrapper.getUpdatedTimeMillis());
                final String localToken = tokenWrapper.getValue();
                if (!value.equals(localToken)) {
                    final Collection<BrowserPage> bps = browserPages.values();
                    if (bps.isEmpty()) {
                        throw new BrowserPageNotFoundException(
                                "There is no active browser page in the session to remove the token");
                    }
                    final int id = tokenIdGenerator.incrementAndGet();
                    final long operationTimeMillis = System.currentTimeMillis();
                    final boolean updated = tokenWrapper.setTokenAndWriteTime(value, null, operationTimeMillis, id);
                    if (updated) {
                        for (final BrowserPage browserPage : bps) {
                            browserPage.setLocalStorageToken(id, key, value, operationTimeMillis);
                        }
                    }
                }
                return previousItem;
            } finally {
                tokenWrapper.lock.unlockWrite(stamp);
            }
        } else {
            final TokenWrapper tokenWrapper = tokenWrapperByKey.get(key);
            if (tokenWrapper != null) {
                final long stamp = tokenWrapper.lock.writeLock();
                try {
                    final Collection<BrowserPage> bps = browserPages.values();
                    if (bps.isEmpty()) {
                        throw new BrowserPageNotFoundException(
                                "There is no active browser page in the session to remove the token");
                    }
                    final TokenData previousItem = new TokenData(tokenWrapper.getValue(),
                            tokenWrapper.getUpdatedTimeMillis());
                    if (tokenWrapperByKey.remove(key) != null) {
                        final int id = tokenIdGenerator.incrementAndGet();
                        final long operationTimeMillis = System.currentTimeMillis();
                        final boolean updated = tokenWrapper.setTokenAndWriteTime(null, null, operationTimeMillis, id,
                                key, tokenWrapperByKey);
                        if (updated) {
                            for (final BrowserPage browserPage : bps) {
                                browserPage.removeLocalStorageToken(id, key, operationTimeMillis);
                            }
                        }
                    }
                    return previousItem;
                } finally {
                    tokenWrapper.lock.unlockWrite(stamp);
                }
            }
        }

        return null;
    }

    @Override
    public Item getToken(final String key) {
        final TokenWrapper tokenWrapper = tokenWrapperByKey.get(key);
        if (tokenWrapper != null) {
            final long stamp = tokenWrapper.lock.readLock();
            try {
                return new TokenData(tokenWrapper.getValue(), tokenWrapper.getUpdatedTimeMillis());
            } finally {
                tokenWrapper.lock.unlockRead(stamp);
            }
        }
        return null;
    }

    @Override
    public Item removeToken(final String key) {
        return setToken(key, null);
    }
}