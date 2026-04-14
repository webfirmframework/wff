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

import java.util.List;
import java.util.Map;

/**
 * Only for internal use.
 *
 * @since 12.0.4
 */
final class JsonSection {

    private final int id;

    private final JsonSectionType jsonSectionType;

    private final int startIndex;

    private Map<String, Object> jsonObject;

    private List<Object> jsonArray;

    //not used anywhere
    private String jsonStringPart;

    private int[] jsonStringPartCodePoints;

    private Object jsonObjectArrayOrStringPart;

    private Object jsonObjectOrArray;

    private int endIndex = -1;

    JsonSection(final int id, final JsonSectionType jsonSectionType, final int startIndex) {
        this.id = id;
        this.startIndex = startIndex;
        this.jsonSectionType = jsonSectionType;
    }

    int startIndex() {
        return startIndex;
    }

    int getEndIndex() {
        return endIndex;
    }

    void setEndIndex(final int endIndex) {
        this.endIndex = endIndex;
    }

    int id() {
        return id;
    }

    JsonSectionType jsonSectionType() {
        return jsonSectionType;
    }

    Map<String, Object> getJsonObject() {
        return jsonObject;
    }

    void setJsonObject(final Map<String, Object> jsonObject) {
        this.jsonObject = jsonObject;
        jsonObjectArrayOrStringPart = jsonObject;
        jsonObjectOrArray = jsonObject;
    }

    List<Object> getJsonArray() {
        return jsonArray;
    }

    void setJsonArray(final List<Object> jsonArray) {
        this.jsonArray = jsonArray;
        jsonObjectArrayOrStringPart = jsonArray;
        jsonObjectOrArray = jsonArray;
    }

    @Deprecated(since = "12.0.11", forRemoval = true)
    String getJsonStringPart() {
        return jsonStringPart;
    }

    @Deprecated(since = "12.0.11", forRemoval = true)
    void setJsonStringPart(final String jsonStringPart) {
        this.jsonStringPart = jsonStringPart;
        jsonObjectArrayOrStringPart = jsonStringPart;
    }

    @Deprecated(since = "12.0.11", forRemoval = true)
    Object getJsonObjectArrayOrStringPart() {
        return jsonObjectArrayOrStringPart;
    }

    void setJsonStringPartCodePoints(final int[] jsonStringPartCodePoints) {
        this.jsonStringPartCodePoints = jsonStringPartCodePoints;
    }

    int[] getJsonStringPartCodePoints() {
        return jsonStringPartCodePoints;
    }

    Object getJsonObjectOrArray() {
        return jsonObjectOrArray;
    }
}
