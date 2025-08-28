/*
 * Copyright since 2014 Web Firm Framework
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
package com.webfirmframework.wffweb.json;

/**
 * @since 12.0.4
 */
public enum JsonObjectType {
    /**
     * represents JsonMap which is extended by HashMap.
     *
     * @since 12.0.4
     */
    JSON_MAP,
    /**
     * represents JsonConcurrentMap which is extended by ConcurrentHashMap.
     *
     * @since 12.0.4
     */
    JSON_CONCURRENT_MAP,

    /**
     * represents JsonLinkedMap which is extended by LinkedHashMap.
     *
     * @since 12.0.4
     */
    JSON_LINKED_MAP,

    /**
     * represents UnmodifiableMap in Java.
     *
     * @since 12.0.4
     */
    UNMODIFIABLE_MAP,

    /**
     * represents the user defined Map supplied in jsonMapFactory of the JsonParser.
     *
     * @since 12.0.6
     */
    CUSTOM_JSON_MAP;
}
