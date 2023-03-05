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

import java.util.Map;

/**
 * There will be one {@code BrowserPageSession} object per httpSessionId
 *
 * @since 12.0.0-beta.4
 */
public sealed interface BrowserPageSession permits BrowserPageSessionImpl {

    /**
     * @return the http session id
     */
    String id();

    /**
     * The saved object will be prevented from being garbage collected so manually
     * remove it after use. The return map is thread-safe and atomic operations can
     * be performed with it.
     *
     * @return the map to save user properties
     * @since 12.0.0-beta.4
     */
    Map<String, Object> userProperties();

    LocalStorage localStorage();

    /**
     * Note: This will not prevent the saved object from being garbage collected.
     * This is a thread-safe method.
     *
     * @param key      the key
     * @param property the value object
     * @return the previously associated object.
     * @since 12.0.0-beta.4
     */
    Object setWeakProperty(final String key, final Object property);

    /**
     * This is a thread-safe method.
     *
     * @param key the key to get the saved property
     * @return the property if exists otherwise null. It will also return null if
     *         the saved object is garbage collected.
     * @since 12.0.0-beta.4
     */
    Object getWeakProperty(final String key);

    /**
     * This is a thread-safe method.
     *
     * @param key
     * @return the previously associated object or null no mapping found. It will
     *         also return null if the saved object is garbage collected.
     * @since 12.0.0-beta.4
     */
    Object removeWeakProperty(final String key);

}
