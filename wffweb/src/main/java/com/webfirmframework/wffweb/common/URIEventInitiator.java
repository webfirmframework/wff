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
package com.webfirmframework.wffweb.common;

/**
 * @since 12.0.0-beta.4
 *
 */
public enum URIEventInitiator {

    SERVER_CODE,

    CLIENT_CODE,

    BROWSER;

    private final String jsNameValue;

    private static String jsObjectString;

    private static final URIEventInitiator[] ALL = URIEventInitiator.values();

    private URIEventInitiator() {
        jsNameValue = name().concat(":").concat(String.valueOf(ordinal()));
    }

    /**
     * @return the JavaScript object string, Eg:- <code>{ SERVER_CODE: 0,
     *         CLIENT_CODE: 1, BROWSER: 2, size: 3}</code>
     * @since 12.0.0-beta.4
     * @author WFF
     */
    public static String getJsObjectString() {

        if (jsObjectString != null) {
            return jsObjectString;
        }

        // StringBuilder builder = new StringBuilder();
        // URIEventInitiator.values().length - 1 for , (comma)
        // 2 for opening + closing curly brace
        // (URIEventInitiator.values().length - 1) + 2;
        // when reduced it becomes URIEventInitiator.values().length + 1
        int totalLength = ALL.length + 1;
        for (final URIEventInitiator initiator : ALL) {
            totalLength += initiator.jsNameValue.length();
        }

        final StringBuilder builder = new StringBuilder(totalLength);
        builder.append('{');
        for (final URIEventInitiator initiator : ALL) {
            builder.append(initiator.jsNameValue).append(',');
        }

        builder.append("size:");
        builder.append(ALL.length);
        builder.append('}');

        jsObjectString = builder.toString();
        return jsObjectString;
    }

    public static URIEventInitiator get(final byte index) {
        return ALL[index];
    }
}
