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

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * It should contain only publically accessible features. BrowserPageContext
 * specific code should be written inside BrowserPageSessionWrapper.
 *
 * @since 12.0.0-beta.4
 */
final class BrowserPageSessionImpl implements BrowserPageSession {

    private final String id;

    private final Map<String, Object> userProperties;

    private final Map<String, WeakReference<Object>> weakProperties;

    final LocalStorageImpl localStorage;

    BrowserPageSessionImpl(final String id, final Map<String, BrowserPage> browserPages) {
        this.id = id;
        userProperties = new ConcurrentHashMap<>(2);
        weakProperties = new ConcurrentHashMap<>(2);
        localStorage = new LocalStorageImpl(browserPages);
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public Map<String, Object> userProperties() {
        return userProperties;
    }

    @Override
    public LocalStorage localStorage() {
        return localStorage;
    }

    @Override
    public Object setWeakProperty(final String key, final Object property) {
        final WeakReference<Object> ref = weakProperties.put(key, new WeakReference<>(property));
        return ref != null ? ref.get() : null;
    }

    @Override
    public Object getWeakProperty(final String key) {
        // to remove entry if property is GCed
        final WeakReference<Object> ref = weakProperties.computeIfPresent(key, (k, v) -> v.get() == null ? null : v);
        return ref != null ? ref.get() : null;
    }

    @Override
    public Object removeWeakProperty(final String key) {
        final WeakReference<Object> ref = weakProperties.remove(key);
        return ref != null ? ref.get() : null;
    }
}