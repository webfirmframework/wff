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
    public JsonParser build() {
        return new JsonParser(jsonObjectType, jsonArrayType, jsonNumberArrayUniformValueType,
                jsonNumberValueTypeForObject, jsonNumberValueTypeForArray, jsonStringValueTypeForObject,
                jsonStringValueTypeForArray, jsonBooleanValueTypeForObject, jsonBooleanValueTypeForArray,
                jsonNullValueTypeForObject, jsonNullValueTypeForArray, validateEscapeSequence, jsonMapFactory,
                jsonListFactory);
    }
}
