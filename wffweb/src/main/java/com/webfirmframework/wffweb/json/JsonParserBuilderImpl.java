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
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @since 12.0.4
 */
final class JsonParserBuilderImpl implements JsonParser.Builder {

    private JsonObjectType jsonObjectType;
    private JsonArrayType jsonArrayType;
    private boolean jsonNumberArrayUniformValueType;
    private JsonNumberValueType jsonNumberValueTypeForObject;
    private JsonNumberValueType jsonNumberValueTypeForArray;
    private JsonStringValueType jsonStringValueTypeForObject;
    private JsonStringValueType jsonStringValueTypeForArray;
    private JsonBooleanValueType jsonBooleanValueTypeForObject;
    private JsonBooleanValueType jsonBooleanValueTypeForArray;
    private JsonNullValueType jsonNullValueTypeForObject;
    private JsonNullValueType jsonNullValueTypeForArray;
    private boolean validateEscapeSequence = true;
    private Supplier<Map<String, Object>> jsonMapFactory;
    private Supplier<List<Object>> jsonListFactory;
    private Function<Integer, Map<String, Object>> jsonMapFactorySizeAware;
    private Function<Integer, List<Object>> jsonListFactorySizeAware;

    @Override
    public JsonParserBuilderImpl jsonObjectType(final JsonObjectType jsonObjectType) {
        this.jsonObjectType = jsonObjectType;
        return this;
    }

    @Override
    public JsonParserBuilderImpl jsonArrayType(final JsonArrayType jsonArrayType) {
        this.jsonArrayType = jsonArrayType;
        return this;
    }

    @Override
    public JsonParserBuilderImpl jsonNumberArrayUniformValueType(final boolean uniform) {
        jsonNumberArrayUniformValueType = uniform;
        return this;
    }

    @Override
    public JsonParserBuilderImpl jsonNumberValueTypeForObject(final JsonNumberValueType jsonNumberValueType) {
        jsonNumberValueTypeForObject = jsonNumberValueType;
        return this;
    }

    @Override
    public JsonParserBuilderImpl jsonNumberValueTypeForArray(final JsonNumberValueType jsonNumberValueType) {
        jsonNumberValueTypeForArray = jsonNumberValueType;
        return this;
    }

    @Override
    public JsonParserBuilderImpl jsonStringValueTypeForObject(final JsonStringValueType jsonStringValueType) {
        jsonStringValueTypeForObject = jsonStringValueType;
        return this;
    }

    @Override
    public JsonParserBuilderImpl jsonStringValueTypeForArray(final JsonStringValueType jsonStringValueType) {
        jsonStringValueTypeForArray = jsonStringValueType;
        return this;
    }

    @Override
    public JsonParserBuilderImpl jsonBooleanValueTypeForObject(final JsonBooleanValueType jsonBooleanValueType) {
        jsonBooleanValueTypeForObject = jsonBooleanValueType;
        return this;
    }

    @Override
    public JsonParserBuilderImpl jsonBooleanValueTypeForArray(final JsonBooleanValueType jsonBooleanValueType) {
        jsonBooleanValueTypeForArray = jsonBooleanValueType;
        return this;
    }

    @Override
    public JsonParserBuilderImpl jsonNullValueTypeForObject(final JsonNullValueType jsonNullValueType) {
        jsonNullValueTypeForObject = jsonNullValueType;
        return this;
    }

    @Override
    public JsonParserBuilderImpl jsonNullValueTypeForArray(final JsonNullValueType jsonNullValueType) {
        jsonNullValueTypeForArray = jsonNullValueType;
        return this;
    }

    @Override
    public JsonParser.Builder validateEscapeSequence(final boolean validateEscapeSequence) {
        this.validateEscapeSequence = validateEscapeSequence;
        return this;
    }

    @Override
    public JsonParser.Builder jsonMapFactory(final Supplier<Map<String, Object>> jsonMapFactory) {
        this.jsonMapFactory = jsonMapFactory;
        return this;
    }

    @Override
    public JsonParser.Builder jsonListFactory(final Supplier<List<Object>> jsonListFactory) {
        this.jsonListFactory = jsonListFactory;
        return this;
    }

    @Override
    public JsonParser.Builder jsonMapFactorySizeAware(final Function<Integer, Map<String, Object>> jsonMapFactorySizeAware) {
        this.jsonMapFactorySizeAware = jsonMapFactorySizeAware;
        return this;
    }

    @Override
    public JsonParser.Builder jsonListFactorySizeAware(final Function<Integer, List<Object>> jsonListFactorySizeAware) {
        this.jsonListFactorySizeAware = jsonListFactorySizeAware;
        return this;
    }

    @Override
    public JsonParser build() {
        if (JsonObjectType.CUSTOM_JSON_MAP.equals(jsonObjectType) && jsonMapFactory == null) {
            if (jsonMapFactorySizeAware != null) {
                throw new IllegalArgumentException("jsonObjectType should be CUSTOM_JSON_MAP_SIZE_AWARE if jsonMapFactorySizeAware is provided!");
            }
            throw new IllegalArgumentException("jsonMapFactory is required if jsonObjectType is CUSTOM_JSON_MAP!");
        }
        if (JsonArrayType.CUSTOM_JSON_LIST.equals(jsonArrayType) && jsonListFactory == null) {
            if (jsonListFactorySizeAware != null) {
                throw new IllegalArgumentException("jsonObjectType should be CUSTOM_JSON_LIST_SIZE_AWARE if jsonListFactorySizeAware is provided!");
            }
            throw new IllegalArgumentException("jsonListFactory is required if jsonArrayType is CUSTOM_JSON_LIST!");
        }
        if (JsonObjectType.CUSTOM_JSON_MAP_SIZE_AWARE.equals(jsonObjectType) && jsonMapFactorySizeAware == null) {
            if (jsonMapFactory != null) {
                throw new IllegalArgumentException("jsonObjectType should be CUSTOM_JSON_MAP if jsonMapFactory is provided!");
            }
            throw new IllegalArgumentException("jsonMapFactorySizeAware is required if jsonObjectType is CUSTOM_JSON_MAP_SIZE_AWARE!");
        }
        if (JsonArrayType.CUSTOM_JSON_LIST_SIZE_AWARE.equals(jsonArrayType) && jsonListFactorySizeAware == null) {
            if (jsonListFactory != null) {
                throw new IllegalArgumentException("jsonObjectType should be CUSTOM_JSON_LIST if jsonListFactory is provided!");
            }
            throw new IllegalArgumentException("jsonListFactorySizeAware is required if jsonArrayType is CUSTOM_JSON_LIST_SIZE_AWARE!");
        }

        if (JsonObjectType.JSON_CONCURRENT_MAP.equals(jsonObjectType) || JsonObjectType.JSON_CONCURRENT_SKIP_LIST_MAP.equals(jsonObjectType)) {
            if (jsonNullValueTypeForObject == null) {
                jsonNullValueTypeForObject =  JsonNullValueType.JSON_VALUE;
            } else if (JsonNullValueType.NULL.equals(jsonNullValueTypeForObject)) {
                throw new IllegalArgumentException("jsonNullValueTypeForObject should be JSON_VALUE if jsonObjectType is %s!".formatted(jsonObjectType.name()));
            }
        }

        return new JsonParser(jsonObjectType, jsonArrayType, jsonNumberArrayUniformValueType,
                jsonNumberValueTypeForObject, jsonNumberValueTypeForArray, jsonStringValueTypeForObject,
                jsonStringValueTypeForArray, jsonBooleanValueTypeForObject, jsonBooleanValueTypeForArray,
                jsonNullValueTypeForObject, jsonNullValueTypeForArray, validateEscapeSequence, jsonMapFactory,
                jsonListFactory, jsonMapFactorySizeAware, jsonListFactorySizeAware);
    }
}
