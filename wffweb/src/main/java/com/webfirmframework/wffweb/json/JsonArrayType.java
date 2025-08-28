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
public enum JsonArrayType {
    /**
     * represents JsonList which is extended by ArrayList.
     */
    JSON_LIST,
    /**
     * represents JsonLinkedList which is extended by LinkedList.
     */
    JSON_LINKED_LIST,

    /**
     * represents immutable List in Java
     */
    UNMODIFIABLE_LIST,

    /**
     * represents the user defined Map supplied by jsonListFactory of the
     * JsonParser.
     *
     * @since 12.0.6
     */
    CUSTOM_JSON_LIST;
}
