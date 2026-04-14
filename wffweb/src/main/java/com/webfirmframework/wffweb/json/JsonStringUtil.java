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

import com.webfirmframework.wffweb.util.StringUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * Only for internal use.
 *
 * @since 12.0.4
 */
final class JsonStringUtil {

    // private static final int SLASH_T_CODE_POINT = "\t".codePointAt(0);
    private static final int SLASH_T_CODE_POINT = 9;

    //    private static final int SLASH_B_CODE_POINT = "\b".codePointAt(0);//8
    private static final int SLASH_B_CODE_POINT = 8;//8

    //    private static final int SLASH_F_CODE_POINT = "\f".codePointAt(0);//12
    private static final int SLASH_F_CODE_POINT = 12;//12

    //    private static final int SLASH_SLASH_CODE_POINT = "\\".codePointAt(0);//92
    private static final int SLASH_SLASH_CODE_POINT = 92;

    //    private static final int FORWARD_SLASH_CODE_POINT = "/".codePointAt(0);
    private static final int FORWARD_SLASH_CODE_POINT = 47;

    //    private static final int[] SLASH_R_SLASH_N_CHARS = "\\r\\n".codePoints().toArray();
    private static final int[] SLASH_R_SLASH_N_CHARS = {92, 114, 92, 110};

    // Note: it should be sorted in ascending order, call
    // Arrays.sort(JSON_ESCAPABLE_DELIMS_SORTED_ASC); to dynamically sort
    // this is only for json string
//    [34, 47, 92, 98, 102, 110, 114, 116, 117]
//    private static final int[] JSON_STRING_ESCAPABLE_DELIMS_SORTED_ASC = {
//            JsonCodePointUtil.DOUBLE_QUOTES_CODE_POINT, // 34
//            "/".codePointAt(0), //47
//            JsonCodePointUtil.ESCAPE_CODE_POINT,  //92
//            "b".codePointAt(0),  //98
//            "f".codePointAt(0),  //102
//            "n".codePointAt(0),  //110
//            "r".codePointAt(0),  //114
//            "t".codePointAt(0),  //116
//            JsonCodePointUtil.U_CODE_POINT,  //117
//    };

    static boolean isJsonStringEscapableDelim(final int codePoint) {
        // for better performance, case is the integers in
        // JSON_STRING_ESCAPABLE_DELIMS_SORTED_ASC
        return switch (codePoint) {
            case 34, 47, 92, 98, 102, 110, 114, 116, 117 -> true;
            default -> false;
        };
    }

    private static int getCodePointForEscapableDelim(final int codePoint) {
        // for better performance, case is the integers in
        // JSON_STRING_ESCAPABLE_DELIMS_SORTED_ASC
        //Note: \" i.e. \34 = JsonCodePointUtil.DOUBLE_QUOTES_CODE_POINT
        return switch (codePoint) {
            case 34 -> JsonCodePointUtil.DOUBLE_QUOTES_CODE_POINT;
            case 47 -> FORWARD_SLASH_CODE_POINT;
            case 92 -> SLASH_SLASH_CODE_POINT;
            case 98 -> SLASH_B_CODE_POINT;
            case 102 -> SLASH_F_CODE_POINT;
            case 110 -> JsonCodePointUtil.SLASH_N_CODE_POINT;
            case 114 -> JsonCodePointUtil.SLASH_R_CODE_POINT;
            case 116 -> SLASH_T_CODE_POINT;
            default -> codePoint;
        };
    }

    static void writeJsonKeyValue(final OutputStream outputStream, final Charset charset, final boolean flushOnWrite,
                                  final Map.Entry<String, Object> entry) throws IOException {
        final byte[] charBytes = ":\"".getBytes(charset);
        final byte colon = charBytes[0];
        final byte doubleQuotes = charBytes[1];
        final Object value = entry.getValue();
        final String key = placeEscapeCharInJsonStringValueIfRequired(entry.getKey(), false);
        if (value instanceof final JsonValue jsonValue) {
            outputStream.write(doubleQuotes);
            outputStream.write(key.getBytes(charset));
            outputStream.write(doubleQuotes);
            outputStream.write(colon);
            outputStream.write(jsonValue.toJsonString().getBytes(charset));
            if (flushOnWrite) {
                outputStream.flush();
            }
        } else if (value instanceof final String s) {
            outputStream.write(doubleQuotes);
            outputStream.write(key.getBytes(charset));
            outputStream.write(doubleQuotes);
            outputStream.write(colon);
            outputStream.write(placeEscapeCharInJsonStringValueIfRequired(s, true).getBytes(charset));
            if (flushOnWrite) {
                outputStream.flush();
            }
        } else if (value instanceof final JsonListNode jln) {
            outputStream.write(doubleQuotes);
            outputStream.write(key.getBytes(charset));
            outputStream.write(doubleQuotes);
            outputStream.write(colon);
            jln.toOutputStream(outputStream, charset, flushOnWrite);
        } else if (value instanceof final JsonMapNode jmn) {
            outputStream.write(doubleQuotes);
            outputStream.write(key.getBytes(charset));
            outputStream.write(doubleQuotes);
            outputStream.write(colon);
            jmn.toOutputStream(outputStream, charset, flushOnWrite);
        } else {
            outputStream.write(doubleQuotes);
            outputStream.write(key.getBytes(charset));
            outputStream.write(doubleQuotes);
            outputStream.write(colon);
            outputStream.write(String.valueOf(value).getBytes(charset));
            if (flushOnWrite) {
                outputStream.flush();
            }
        }
    }

    static void writeJsonValue(final OutputStream outputStream, final Charset charset, final boolean flushOnWrite,
                               final Object value) throws IOException {
        if (value instanceof final JsonValue jsonValue) {
            outputStream.write(jsonValue.toJsonString().getBytes(charset));
            if (flushOnWrite) {
                outputStream.flush();
            }
        } else if (value instanceof final String s) {
            outputStream
                    .write(placeEscapeCharInJsonStringValueIfRequired(s, true).getBytes(charset));
            if (flushOnWrite) {
                outputStream.flush();
            }
        } else if (value instanceof final JsonListNode jln) {
            jln.toOutputStream(outputStream, charset, flushOnWrite);
        } else if (value instanceof final JsonMapNode jmn) {
            jmn.toOutputStream(outputStream, charset, flushOnWrite);
        } else {
            outputStream.write(String.valueOf(value).getBytes(charset));
            if (flushOnWrite) {
                outputStream.flush();
            }
        }
    }

    static String buildJsonValue(final Object value) {
        return buildJsonValue(value, false);
    }

    static String buildBigJsonValue(final Object value) {
        return buildJsonValue(value, true);
    }

    private static String buildJsonValue(final Object value, final boolean parallel) {
        if (value instanceof final JsonValue jsonValue) {
            return jsonValue.toJsonString();
        }
        if (value instanceof final String s) {
            return placeEscapeCharInJsonStringValueIfRequired(s, true);
        }
        if (value instanceof final JsonListNode jln) {
            return parallel ? jln.toBigJsonString() : jln.toJsonString();
        }
        if (value instanceof final JsonMapNode jmn) {
            return parallel ? jmn.toBigJsonString() : jmn.toJsonString();
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
        final String key = placeEscapeCharInJsonStringValueIfRequired(entry.getKey(), false);
        if (value instanceof final JsonValue jsonValue) {
            return buildKeyColonValue(key, jsonValue.toJsonString());
        }
        if (value instanceof final String s) {
            return buildKeyColonValue(key, placeEscapeCharInJsonStringValueIfRequired(s, true));
        }
        if (value instanceof final JsonListNode jln) {
            return buildKeyColonValue(key, jln.toJsonString());
        }
        if (value instanceof final JsonMapNode jmn) {
            return buildKeyColonValue(key, parallel ? jmn.toBigJsonString() : jmn.toJsonString());
        }
        return buildKeyColonValue(key, String.valueOf(value));
    }

    private static String buildKeyColonValue(final String key, final String jsonValue) {
        return '"' + key + '"' + ':' + jsonValue;
    }

    static String placeEscapeCharInJsonStringValueIfRequired(final String s) {
        if (s == null || s.isBlank()) {
            return s;
        }
        return placeEscapeCharInJsonStringValueIfRequired(s, StringUtil.toCodePoints(s), false);
    }

    static String placeEscapeCharInJsonStringValueIfRequired(final String s, final boolean surroundByDoubleQuotes) {
        if (s == null) {
            return s;
        }
        if (s.isBlank()) {
            return surroundByDoubleQuotes ? "\"".concat(s).concat("\"") : s;
        }
        return placeEscapeCharInJsonStringValueIfRequired(s, StringUtil.toCodePoints(s), surroundByDoubleQuotes);
    }

    static String placeEscapeCharInJsonStringValueIfRequired(final int[] codePoints, final boolean surroundByDoubleQuotes) {
        return placeEscapeCharInJsonStringValueIfRequired(null, codePoints, surroundByDoubleQuotes);
    }

    private static String placeEscapeCharInJsonStringValueIfRequired(final String s, final int[] codePoints, final boolean surroundByDoubleQuotes) {
        if (codePoints == null) {
            return null;
        }
        if (codePoints.length == 0) {
            return surroundByDoubleQuotes ? "\"\"" : "";
        }
        final int replaceableDelimsCount = countReplaceableDelims(codePoints);
        if (replaceableDelimsCount == 0) {
            if (s != null) {
                return surroundByDoubleQuotes ? "\"".concat(s).concat("\"") : s;
            }
            return surroundByDoubleQuotes ? "\"".concat(new String(codePoints, 0, codePoints.length)).concat("\"") : new String(codePoints, 0, codePoints.length);
        }
        final int sbLength = surroundByDoubleQuotes ? codePoints.length + replaceableDelimsCount + 2 : codePoints.length + replaceableDelimsCount;
        final StringBuilder sb = new StringBuilder(sbLength);
        if (surroundByDoubleQuotes) {
            sb.append("\"");
        }
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
                            if (codePoint == JsonCodePointUtil.ESCAPE_CODE_POINT) {
                                final int nextI = i + 1;
                                final int nextCodePoint = nextI < codePoints.length ? codePoints[nextI] : -1;
                                if (JsonCodePointUtil.isValidUnicodeEscapeSequence(codePoints, i, i + 5)) {
                                    sb.appendCodePoint(JsonCodePointUtil.ESCAPE_CODE_POINT);
                                } else if (nextCodePoint != JsonCodePointUtil.U_CODE_POINT
                                        && nextCodePoint != JsonCodePointUtil.ESCAPE_CODE_POINT
                                        && JsonStringUtil.isJsonStringEscapableDelim(nextCodePoint)) {
                                    sb.appendCodePoint(JsonCodePointUtil.ESCAPE_CODE_POINT);
                                } else {
                                    sb.appendCodePoint(JsonCodePointUtil.ESCAPE_CODE_POINT);
                                    sb.appendCodePoint(JsonCodePointUtil.ESCAPE_CODE_POINT);
                                }
                            }
                        } else {
                            sb.appendCodePoint(JsonCodePointUtil.ESCAPE_CODE_POINT);
                            sb.appendCodePoint(codePoint);
                        }
                    } else {
                        sb.appendCodePoint(JsonCodePointUtil.ESCAPE_CODE_POINT);
                    }
                }
                case JsonCodePointUtil.SLASH_R_CODE_POINT -> sb.append("\\r");
                case JsonCodePointUtil.SLASH_N_CODE_POINT -> sb.append("\\n");
                case JsonStringUtil.SLASH_B_CODE_POINT -> sb.append("\\b");
                case JsonStringUtil.SLASH_F_CODE_POINT -> sb.append("\\f");
                case JsonStringUtil.SLASH_T_CODE_POINT -> sb.append("\\t");
                case JsonCodePointUtil.DOUBLE_QUOTES_CODE_POINT -> {
                    sb.appendCodePoint(JsonCodePointUtil.ESCAPE_CODE_POINT);
                    sb.appendCodePoint(codePoint);
                }
                default -> sb.appendCodePoint(codePoint);
            }
        }
        if (surroundByDoubleQuotes) {
            sb.append("\"");
        }
        return sb.toString();
    }

    private static int countReplaceableDelims(final int[] codePoints) {
        int replaceableDelimsCount = 0;
        for (final int codePoint : codePoints) {
            switch (codePoint) {
                case JsonCodePointUtil.ESCAPE_CODE_POINT,
                     JsonCodePointUtil.DOUBLE_QUOTES_CODE_POINT,
                     JsonCodePointUtil.SLASH_R_CODE_POINT,
                     JsonCodePointUtil.SLASH_N_CODE_POINT,
                     SLASH_B_CODE_POINT,
                     SLASH_F_CODE_POINT,
                     SLASH_T_CODE_POINT -> replaceableDelimsCount++;
            }
        }
        return replaceableDelimsCount;
    }

    static String replaceEscapeCharSequenceWithJavaChars(final int[] codePoints, final int startIndex, final int length) {
        return replaceEscapeCharSequenceWithJavaChars(codePoints, startIndex, length, false);
    }

    static String replaceEscapeCharSequenceWithJavaChars(final int[] codePoints, final int startIndex, final int length, final boolean parseUnicodeCharSequence) {
        final int endIndex = startIndex + length - 1;
        boolean matchFound = false;
        matchFinderLoop:
        for (int i = startIndex; i <= endIndex; i++) {
            switch (codePoints[i]) {
                case JsonCodePointUtil.ESCAPE_CODE_POINT, JsonCodePointUtil.DOUBLE_QUOTES_CODE_POINT -> {
                    matchFound = true;
                    break matchFinderLoop;
                }
            }
        }
        if (!matchFound) {
            return new String(codePoints, startIndex, length);
        }
        final StringBuilder sb = new StringBuilder(length);

        for (int i = startIndex; i <= (endIndex); i++) {
            int codePoint = codePoints[i];
            switch (codePoint) {
                case JsonCodePointUtil.ESCAPE_CODE_POINT -> {
                    final int prevCodePoint = codePoint;
                    i++;
                    if (i <= endIndex) {
                        codePoint = codePoints[i];
                        if (codePoint == JsonCodePointUtil.U_CODE_POINT && JsonCodePointUtil.isValidUnicodeEscapeSequence(codePoints, (i - 1), i + 4)) {
                            if (parseUnicodeCharSequence) {
                                final StringBuilder unicodeValuePart = new StringBuilder(4);
                                unicodeValuePart.appendCodePoint(codePoints[i + 1]);
                                unicodeValuePart.appendCodePoint(codePoints[i + 2]);
                                unicodeValuePart.appendCodePoint(codePoints[i + 3]);
                                unicodeValuePart.appendCodePoint(codePoints[i + 4]);
                                sb.appendCodePoint(Integer.parseInt(unicodeValuePart.toString(), 16));
                            } else {
                                sb.appendCodePoint(prevCodePoint);
                                sb.appendCodePoint(codePoint);
                                sb.appendCodePoint(codePoints[i + 1]);
                                sb.appendCodePoint(codePoints[i + 2]);
                                sb.appendCodePoint(codePoints[i + 3]);
                                sb.appendCodePoint(codePoints[i + 4]);
                            }
                            i += 4;
                        } else if (JsonStringUtil.startsWithSlashRSlashN(codePoints, (i - 1))) {
                            sb.append("\r\n");
                            i += 2;
                        } else if (JsonStringUtil.isJsonStringEscapableDelim(codePoint)) {
                            sb.appendCodePoint(getCodePointForEscapableDelim(codePoint));
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

    static String toUnicodeCharsDecodedString(final int[] codePoints, final int startIndex, final int length) {
        final int endIndex = startIndex + length - 1;
        boolean matchFound = false;
        for (int i = startIndex; i <= endIndex; i++) {
            if (codePoints[i] == JsonCodePointUtil.ESCAPE_CODE_POINT) {
                matchFound = true;
                break;
            }
        }
        if (!matchFound) {
            return new String(codePoints, startIndex, length);
        }
        final StringBuilder sb = new StringBuilder(length);

        for (int i = startIndex; i <= (endIndex); i++) {
            int codePoint = codePoints[i];
            if (codePoint == JsonCodePointUtil.ESCAPE_CODE_POINT) {
                final int prevCodePoint = codePoint;
                i++;
                if (i <= endIndex) {
                    codePoint = codePoints[i];
                    if (codePoint == JsonCodePointUtil.U_CODE_POINT && JsonCodePointUtil.isValidUnicodeEscapeSequence(codePoints, (i - 1), i + 4)) {
                        final StringBuilder unicodeValuePart = new StringBuilder(4);
                        unicodeValuePart.appendCodePoint(codePoints[i + 1]);
                        unicodeValuePart.appendCodePoint(codePoints[i + 2]);
                        unicodeValuePart.appendCodePoint(codePoints[i + 3]);
                        unicodeValuePart.appendCodePoint(codePoints[i + 4]);
                        sb.appendCodePoint(Integer.parseInt(unicodeValuePart.toString(), 16));
                        i += 4;
                    } else {
                        sb.appendCodePoint(prevCodePoint);
                        sb.appendCodePoint(codePoint);
                    }
                } else {
                    sb.appendCodePoint(JsonCodePointUtil.ESCAPE_CODE_POINT);
                }
            } else {
                sb.appendCodePoint(codePoint);
            }
        }
        return sb.toString();
    }

    private static boolean startsWithSlashRSlashN(final int[] codePoints, final int startIndex) {
        if ((codePoints.length - startIndex) >= SLASH_R_SLASH_N_CHARS.length) {
            for (int i = 0; i < SLASH_R_SLASH_N_CHARS.length; i++) {
                if (codePoints[startIndex + i] != SLASH_R_SLASH_N_CHARS[i]) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

}
