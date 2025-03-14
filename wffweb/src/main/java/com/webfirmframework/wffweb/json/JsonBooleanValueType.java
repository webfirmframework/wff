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

import java.util.Arrays;

/**
 * @since 12.0.4
 */
public enum JsonBooleanValueType {

    BOOLEAN {
        @Override
        Boolean parse(final int[] codePoints, final int[] startEndIndices) {
            return JsonBooleanValueType.parseBooleanOtherwiseNull(codePoints, startEndIndices);
        }
    },

    JSON_VALUE {
        @Override
        JsonValue parse(final int[] codePoints, final int[] startEndIndices) {
            return new JsonValue(JsonCodePointUtil.cut(codePoints, startEndIndices), JsonValueType.BOOLEAN);
        }
    };

    private static final int[] TRUE_CODE_POINTS = "true".codePoints().toArray();

    private static final int[] FALSE_CODE_POINTS = "false".codePoints().toArray();

    JsonBooleanValueType() {
    }

    Object parse(final int[] codePoints, final int[] startEndIndices) {
        throw new AssertionError();
    }

    private static boolean isBooleanValue(final int[] jsonCodePoints) {
        return Arrays.equals(TRUE_CODE_POINTS, jsonCodePoints) || Arrays.equals(FALSE_CODE_POINTS, jsonCodePoints);
    }

    static boolean isBooleanValue(final int[] jsonCodePoints, final int[] startEndIndices) {
        if (startEndIndices == null) {
            return isBooleanValue(jsonCodePoints);
        }
        return Arrays.equals(JsonBooleanValueType.TRUE_CODE_POINTS, 0, JsonBooleanValueType.TRUE_CODE_POINTS.length,
                jsonCodePoints, startEndIndices[0], (startEndIndices[1] + 1))
                || Arrays.equals(JsonBooleanValueType.FALSE_CODE_POINTS, 0,
                        JsonBooleanValueType.FALSE_CODE_POINTS.length, jsonCodePoints, startEndIndices[0],
                        (startEndIndices[1] + 1));
    }

    static Boolean parseBooleanOtherwiseNull(final int[] jsonCodePoints, final int[] startEndIndices) {
        if (startEndIndices != null) {
            if (Arrays.equals(JsonBooleanValueType.TRUE_CODE_POINTS, 0, JsonBooleanValueType.TRUE_CODE_POINTS.length,
                    jsonCodePoints, startEndIndices[0], (startEndIndices[1] + 1))) {
                return Boolean.TRUE;
            } else if (Arrays.equals(JsonBooleanValueType.FALSE_CODE_POINTS, 0,
                    JsonBooleanValueType.FALSE_CODE_POINTS.length, jsonCodePoints, startEndIndices[0],
                    (startEndIndices[1] + 1))) {
                return Boolean.FALSE;
            }
            return null;
        }
        if (Arrays.equals(TRUE_CODE_POINTS, jsonCodePoints)) {
            return Boolean.TRUE;
        } else if (Arrays.equals(FALSE_CODE_POINTS, jsonCodePoints)) {
            return Boolean.FALSE;
        }
        return null;
    }
}
