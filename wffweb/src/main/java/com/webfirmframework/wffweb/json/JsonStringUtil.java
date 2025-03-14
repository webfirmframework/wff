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

import java.util.Map;

/**
 * Only for internal use.
 *
 * @since 12.0.4
 */
final class JsonStringUtil {

    // Note: it should be sorted in ascending order, call
    // Arrays.sort(JSON_ESCAPABLE_DELIMS_SORTED_ASC); to dynamically sort
    // this is only for json string
//    [34, 47, 92, 98, 102, 110, 114, 116, 117]
//    private static final int[] JSON_STRING_ESCAPABLE_DELIMS_SORTED_ASC = {
//            JsonCodePointUtil.DOUBLE_QUOTES_CODE_POINT,
//            "/".codePointAt(0),
//            JsonCodePointUtil.ESCAPE_CODE_POINT,
//            "b".codePointAt(0),
//            "f".codePointAt(0),
//            "n".codePointAt(0),
//            "r".codePointAt(0),
//            "t".codePointAt(0),
//            JsonCodePointUtil.U_CODE_POINT,
//    };

    static boolean isJsonStringEscapableDelim(final int codePoint) {
        // for better performance, case is the integers in
        // JSON_STRING_ESCAPABLE_DELIMS_SORTED_ASC
        return switch (codePoint) {
        case 34, 47, 92, 98, 102, 110, 114, 116, 117 -> true;
        default -> false;
        };
    }

    static String buildJsonValue(final Object value) {
        if (value instanceof final JsonValue jsonValue) {
            final String s = jsonValue.asString();
            if (s == null) {
                return "null";
            }
            if (JsonValueType.STRING.equals(jsonValue.getValueType())) {
                return "\"".concat(placeEscapeCharInJsonStringValueIfRequired(s)).concat("\"");
            }
            return s;
        }
        if (value instanceof final String s) {
            return "\"".concat(placeEscapeCharInJsonStringValueIfRequired(s)).concat("\"");
        }
        if (value instanceof final JsonListNode jln) {
            return jln.toJsonString();
        }
        if (value instanceof final JsonMapNode jmn) {
            return jmn.toJsonString();
        }
        return String.valueOf(value);
    }

    static String buildJsonKeyValue(final Map.Entry<String, Object> entry) {
        return buildJsonKeyValue(entry, false);
    }

    static String buildBigJsonKeyValue(final Map.Entry<String, Object> entry) {
        return buildJsonKeyValue(entry, true);
    }

    private static String buildJsonKeyValue(final Map.Entry<String, Object> entry, final boolean parallel) {
        final Object value = entry.getValue();
        final String key = entry.getKey();
        if (value instanceof final JsonValue jsonValue) {
            if (JsonValueType.STRING.equals(jsonValue.getValueType())) {
                return "\"%s\":\"%s\"".formatted(key, placeEscapeCharInJsonStringValueIfRequired(jsonValue.asString()));
            }
            return "\"%s\":%s".formatted(key, jsonValue.asString());
        }
        if (value instanceof final String s) {
            return "\"%s\":\"%s\"".formatted(key, placeEscapeCharInJsonStringValueIfRequired(s));
        }
        if (value instanceof final JsonListNode jln) {
            return "\"%s\":%s".formatted(key, jln.toJsonString());
        }
        if (value instanceof final JsonMapNode jmn) {
            return "\"%s\":%s".formatted(key, parallel ? jmn.toBigJsonString() : jmn.toJsonString());
        }
        return "\"%s\":%s".formatted(key, value);
    }

    private static String placeEscapeCharInJsonStringValueIfRequired(final String s) {
        if (s == null || s.isBlank()) {
            return s;
        }
        final int[] codePoints = s.codePoints().toArray();
        int replaceableDelimsCount = 0;
        for (final int codePoint : codePoints) {
            if (codePoint == JsonCodePointUtil.ESCAPE_CODE_POINT) {
                replaceableDelimsCount++;
            }
        }
        if (replaceableDelimsCount == 0) {
            return s;
        }
        final StringBuilder sb = new StringBuilder(codePoints.length + replaceableDelimsCount);
        for (int i = 0; i < codePoints.length; i++) {
            int codePoint = codePoints[i];
            switch (codePoint) {
            case JsonCodePointUtil.ESCAPE_CODE_POINT -> {
                sb.appendCodePoint(codePoint);
                i++;
                if (i < codePoints.length) {
                    codePoint = codePoints[i];
                    if (codePoint == JsonCodePointUtil.U_CODE_POINT) {
                        if (JsonCodePointUtil.isValidUnicodeEscapeSequence(codePoints, (i - 1), i + 4)) {
                            sb.appendCodePoint(codePoint);
                            sb.appendCodePoint(codePoints[i + 1]);
                            sb.appendCodePoint(codePoints[i + 2]);
                            sb.appendCodePoint(codePoints[i + 3]);
                            sb.appendCodePoint(codePoints[i + 4]);
                            i += 4;
                        } else {
                            sb.appendCodePoint(JsonCodePointUtil.ESCAPE_CODE_POINT);
                            sb.appendCodePoint(codePoint);
                        }
                    } else if (JsonStringUtil.isJsonStringEscapableDelim(codePoint)) {
                        sb.appendCodePoint(codePoint);
                    } else {
                        sb.appendCodePoint(JsonCodePointUtil.ESCAPE_CODE_POINT);
                        sb.appendCodePoint(codePoint);
                    }
                } else {
                    sb.appendCodePoint(JsonCodePointUtil.ESCAPE_CODE_POINT);
                }
            }
            case JsonCodePointUtil.DOUBLE_QUOTES_CODE_POINT -> {
                sb.appendCodePoint(JsonCodePointUtil.ESCAPE_CODE_POINT);
                sb.appendCodePoint(codePoint);
            }
            default -> sb.appendCodePoint(codePoint);
            }
        }

        return sb.toString();
    }

}
