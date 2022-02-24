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
 * The operations are strongly consistent in the same node but eventually
 * consistent in multiple nodes as there is a network latency. Eg: The following
 * code will print <em>value1</em> as they are executed in the same node.
 *
 * <pre>
 * localStorage.setItem("key1", "value1");
 * localStorage.getItem("key1", System.out::println);
 * </pre>
 * <p>
 * Eg: Suppose the setItem and getItem methods are executed on different nodes
 * as follows it may not print <em>value1</em> as the data is eventually
 * consistent across multiple nodes. <br>
 * <br>
 * first, executed code on node1
 *
 * <pre>
 * localStorage.setItem("key1", "value1");
 * </pre>
 * <p>
 * second, executed code on node2
 *
 * <pre>
 * localStorage.getItem("key1", System.out::println);
 * </pre>
 * <p>
 * It may print <em>value1</em> but not guaranteed. It will be eventually
 * available. {@code setItem} doesn't cache any data at server side. In the
 * above code, the consumer objects are executed asynchronously. <br>
 * <br>
 * To save token use {@code setToken} method. It also caches the data in all
 * nodes at server side so can get the value as follows
 *
 * <pre>
 * localStorage.setToken("token1", "tokenvalue");
 * String token = localStorage.getToken("token1");
 * </pre>
 *
 * @since 12.0.0-beta.4
 */
public sealed interface LocalStorage permits LocalStorageImpl {

    /**
     * @since 12.0.0-beta.4
     */
    sealed interface Item permits ItemData,TokenData {

        String value();

        long updatedTimeMillis();
    }

    /**
     * @since 12.0.0-beta.4
     */
    record Event(String key, long operationTimeMillis, Item item) {

        public Event(final String key, final long operationTimeMillis) {
            this(key, operationTimeMillis, null);
        }

        public Event(final long operationTimeMillis) {
            this(null, operationTimeMillis, null);
        }
    }

    /**
     * This is an asynchronous method. The consumer will be invoked asynchronously.
     *
     * @param key             the key for the value.
     * @param value           the value to set. If there is an existing value it
     *                        will be overwritten with this new value. If null is
     *                        passed the existing item will be removed.
     * @param successConsumer to invoke the consumer if writing is successful.
     * @since 12.0.0-beta.4
     */
    void setItem(String key, String value, Consumer<Event> successConsumer);

    /**
     * This is an asynchronous method.
     *
     * @param key   the key for the value.
     * @param value the value to set. If there is an existing value it will be
     *              overwritten with this new value. If null is passed the existing
     *              item will be removed.
     * @since 12.0.0-beta.4
     */
    void setItem(String key, String value);

    /**
     * This is an asynchronous method. The consumer will be invoked asynchronously.
     *
     * @param key      the key to get the value.
     * @param consumer the consumer to get the value.
     * @since 12.0.0-beta.4
     */
    void getItem(String key, Consumer<Event> consumer);

    /**
     * This is an asynchronous method. The consumer will be invoked asynchronously.
     *
     * @param key      the key to remove the value.
     * @param consumer the consumer to invoked after successful removal.
     * @since 12.0.0-beta.4
     */
    void removeItem(String key, Consumer<Event> consumer);

    /**
     * This is an asynchronous method.
     *
     * @param key the key to remove the value.
     * @since 12.0.0-beta.4
     */
    void removeItem(String key);

    /**
     * This is an asynchronous method.
     *
     * @param key      the key to remove the value
     * @param consumer the consumer to invoke after the successful removal, the
     *                 removed item details will be available in the consumer.
     * @since 12.0.0-beta.4
     */
    void removeAndGetItem(String key, Consumer<Event> consumer);

    /**
     * This is an asynchronous method. It clears only the wffweb related items from
     * localStorage.
     *
     * @param consumer the consumer to invoke after the successful clearing.
     * @since 12.0.0-beta.4
     */
    void clear(Consumer<Event> consumer);

    /**
     * This is an asynchronous method. It clears only the wffweb related items and
     * tokens from localStorage.
     *
     * @since 12.0.0-beta.4
     */
    void clear();

    /**
     * This is an asynchronous method. It clears only the wffweb related items from
     * localStorage.
     *
     * @since 12.0.0-beta.4
     */
    void clearItems();

    /**
     * It clears only the wffweb related tokens from localStorage.
     *
     * @since 12.0.0-beta.4
     */
    void clearTokens();

    /**
     * This is an asynchronous method.
     *
     * @param consumer invokes after successful clear.
     * @since 12.0.0-beta.4
     */
    void clearItems(Consumer<Event> consumer);

    /**
     * This is an asynchronous method.
     *
     * @param consumer invokes after successful clear.
     * @since 12.0.0-beta.4
     */
    void clearTokens(Consumer<Event> consumer);

    /**
     * @param key   key for the token
     * @param value value for the token
     * @since 12.0.0-beta.4
     */
    void setToken(String key, String value);

    /**
     * @param key the key to get the token
     * @return the Item object containing token value.
     * @since 12.0.0-beta.4
     */
    Item getToken(String key);

    /**
     * Removes the token for the key.
     *
     * @param key the key to remove the token.
     * @since 12.0.0-beta.4
     */
    void removeToken(String key);
}
