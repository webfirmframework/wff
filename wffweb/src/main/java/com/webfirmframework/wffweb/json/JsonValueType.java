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
public enum JsonValueType {

    /**
     * To represent a JSON decoded string value.
     */
    STRING,

    /**
     * To represent a JSON number value.
     */
    NUMBER,

    /**
     * To represent a JSON boolean value.
     */
    BOOLEAN,

    /**
     * To represent a JSON null value.
     */
    NULL,

    /**
     * To represent a JSON string value which is already encoded to JSON compatible string value.
     * If the parser creates a JsonValue object it will be this type and object will contain raw text value i.e. without decoding new line, escape chars, Unicode char sequences etc... to Java chars.
     */
    ENCODED_STRING
}
