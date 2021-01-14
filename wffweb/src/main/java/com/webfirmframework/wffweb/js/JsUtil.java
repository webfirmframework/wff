/*
 * Copyright 2014-2021 Web Firm Framework
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
package com.webfirmframework.wffweb.js;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.webfirmframework.wffweb.InvalidValueException;
import com.webfirmframework.wffweb.util.StringUtil;

/**
 * Utility methods to generate JavaScript code.
 *
 * @author WFF
 * @since 2.1.1
 */
public final class JsUtil {

	private static final int SPACE_CODE_POINT;

	private static final int SLASH_R_CODE_POINT;

	private static final int SLASH_N_CODE_POINT;

	private static final int SLASH_T_CODE_POINT;

	static {
		final int[] codePoints = " \r\n\t".codePoints().toArray();
		SPACE_CODE_POINT = codePoints[0];
		SLASH_R_CODE_POINT = codePoints[1];
		SLASH_N_CODE_POINT = codePoints[2];
		SLASH_T_CODE_POINT = codePoints[3];
	}

	private JsUtil() {
		throw new AssertionError();
	}

	/**
	 * @param jsKeyAndElementId The map containing key values. The key in the map
	 *                          will be used as the key in the generated js object.
	 *                          The value in the map should be the id of the field.
	 * @return the JavaScript object for the fields value. Sample :
	 *         <code>{username:document.getElementById('uId').value}</code>
	 * @since 2.1.1
	 * @author WFF
	 */
	public static String getJsObjectForFieldsValue(final Map<String, Object> jsKeyAndElementId) {

		final StringBuilder builder = new StringBuilder(38);

		builder.append('{');

		for (final Entry<String, Object> entry : jsKeyAndElementId.entrySet()) {

			builder.append(entry.getKey()).append(":document.getElementById('").append(entry.getValue().toString())
			        .append("').value,");

		}

		builder.replace(builder.length() - 1, builder.length(), "}");

		return builder.toString();
	}

	/**
	 * @param jsKeyAndElementId   The map containing key values. The key in the map
	 *                            will be used as the key in the generated js
	 *                            object. The value in the map should be the id of
	 *                            the field.
	 * @param alternativeFunction alternative function name for
	 *                            document.getElementById
	 *
	 * @return the JavaScript object for the fields value. Sample :
	 *         <code>{username:gebi('uId').value}</code> if the alternativeFunction
	 *         is gebi
	 * @since 3.0.1
	 * @author WFF
	 */
	public static String getJsObjectForFieldsValue(final Map<String, Object> jsKeyAndElementId,
	        final String alternativeFunction) {

		if (StringUtil.isBlank(alternativeFunction)) {
			throw new InvalidValueException("alternativeFunction cannot be blank");
		}

		final StringBuilder builder = new StringBuilder(38);

		builder.append('{');

		for (final Entry<String, Object> entry : jsKeyAndElementId.entrySet()) {

			builder.append(entry.getKey()).append(':').append(alternativeFunction).append("('")
			        .append(entry.getValue().toString()).append("').value,");

		}

		builder.replace(builder.length() - 1, builder.length(), "}");

		return builder.toString();
	}

	/**
	 * @param ids The set containing element ids. The id will be used as the key in
	 *            the generated js object. The value in the set should be the id of
	 *            the field. The id in the set should be a valid JavaScript object
	 *            key.
	 * @return the JavaScript object for the fields value. Sample :
	 *         <code>{uId:document.getElementById('uId').value}</code>
	 * @since 2.1.1
	 * @author WFF
	 */
	public static String getJsObjectForFieldsValue(final Set<Object> ids) {

		final StringBuilder builder = new StringBuilder(75);

		builder.append('{');

		for (final Object id : ids) {

			builder.append(id.toString()).append(":document.getElementById('").append(id.toString())
			        .append("').value,");

		}

		builder.replace(builder.length() - 1, builder.length(), "}");

		return builder.toString();
	}

	/**
	 *
	 * @param ids                 The set containing element ids. The id will be
	 *                            used as the key in the generated js object. The
	 *                            value in the set should be the id of the field.
	 *                            The id in the set should be a valid JavaScript
	 *                            object key.
	 *
	 * @param alternativeFunction alternative function name for
	 *                            document.getElementById
	 *
	 * @return the JavaScript object for the fields value. Sample :
	 *         <code>{uId:gebi('uId').value} if alternativeFunction is gebi </code>
	 * @since 3.0.1
	 * @author WFF
	 */
	public static String getJsObjectForFieldsValue(final Set<Object> ids, final String alternativeFunction) {

		if (StringUtil.isBlank(alternativeFunction)) {
			throw new InvalidValueException("alternativeFunction cannot be blank");
		}

		final StringBuilder builder = new StringBuilder(75);

		builder.append('{');

		for (final Object id : ids) {

			builder.append(id.toString()).append(':').append(alternativeFunction).append("('").append(id.toString())
			        .append("').value,");

		}

		builder.replace(builder.length() - 1, builder.length(), "}");

		return builder.toString();
	}

	/**
	 * @param ids The string array containing element ids. The id will be used as
	 *            the key in the generated js object. The value in the array should
	 *            be the id of the field. The id in the array should be a valid
	 *            JavaScript object key.
	 * @return the JavaScript object for the fields value. Sample :
	 *         <code>{uId:document.getElementById('uId'.value)}</code>
	 * @since 2.1.3
	 * @author WFF
	 */
	public static String getJsObjectForFieldsValue(final String... ids) {

		final StringBuilder builder = new StringBuilder(38);

		builder.append('{');

		for (final Object id : ids) {

			builder.append(id.toString()).append(":document.getElementById('").append(id.toString())
			        .append("').value,");

		}

		builder.replace(builder.length() - 1, builder.length(), "}");

		return builder.toString();
	}

	/**
	 * @param ids                 The string array containing element ids. The id
	 *                            will be used as the key in the generated js
	 *                            object. The value in the array should be the id of
	 *                            the field. The id in the array should be a valid
	 *                            JavaScript object key.
	 * @param alternativeFunction alternative function name for
	 *                            document.getElementById
	 * @return the JavaScript object for the fields value. Sample :
	 *         <code>{uId:document.getElementById('uId'.value)}</code>
	 * @since 3.0.1
	 * @author WFF
	 */
	public static String getJsObjectForFieldsValueWithAltFun(final String alternativeFunction, final String... ids) {

		if (StringUtil.isBlank(alternativeFunction)) {
			throw new InvalidValueException("alternativeFunction cannot be blank");
		}

		final StringBuilder builder = new StringBuilder(38);

		builder.append('{');

		for (final Object id : ids) {

			builder.append(id.toString()).append(':').append(alternativeFunction).append("('").append(id.toString())
			        .append("').value,");

		}

		builder.replace(builder.length() - 1, builder.length(), "}");

		return builder.toString();
	}

	/**
	 * @param inputIds    input field ids such as input type text etc..
	 * @param checkboxIds checkbox field ids such as input type checkbox
	 * @return the JavaScript object for the fields value. Sample :
	 *         <code>{usernameInputId:document.getElementById('usernameInputId').value,dateExpiredCheckboxId:document.getElementById('dateExpiredCheckboxId').checked}</code>
	 * @since 3.0.1
	 */
	public static String getJsObjectForFieldsValue(final Set<Object> inputIds, final Set<Object> checkboxIds) {

		final StringBuilder builder = new StringBuilder(75);

		builder.append('{');

		for (final Object id : inputIds) {

			builder.append(id.toString()).append(":document.getElementById('").append(id.toString())
			        .append("').value,");

		}

		for (final Object id : checkboxIds) {

			builder.append(id.toString()).append(":document.getElementById('").append(id.toString())
			        .append("').checked,");

		}

		builder.replace(builder.length() - 1, builder.length(), "}");

		return builder.toString();
	}

	/**
	 *
	 * @param inputIds            input field ids such as input type text etc..
	 * @param checkboxIds         checkbox field ids such as input type checkbox
	 * @param alternativeFunction alternative function name for
	 *                            document.getElementById
	 * @return the JavaScript object for the fields value. Sample :
	 *         <code>{usernameInputId:document.getElementById('usernameInputId').value,dateExpiredCheckboxId:document.getElementById('dateExpiredCheckboxId').checked}</code>
	 * @since 3.0.1
	 */
	public static String getJsObjectForFieldsValue(final Set<Object> inputIds, final Set<Object> checkboxIds,
	        final String alternativeFunction) {

		if (StringUtil.isBlank(alternativeFunction)) {
			throw new InvalidValueException("alternativeFunction cannot be blank");
		}

		final StringBuilder builder = new StringBuilder(75);

		builder.append('{');

		for (final Object id : inputIds) {

			builder.append(id.toString()).append(':').append(alternativeFunction).append("('").append(id.toString())
			        .append("').value,");

		}

		for (final Object id : checkboxIds) {

			builder.append(id.toString()).append(':').append(alternativeFunction).append("('").append(id.toString())
			        .append("').checked,");

		}

		builder.replace(builder.length() - 1, builder.length(), "}");

		return builder.toString();
	}

	/**
	 * Removes the trailing and leading whitespaces
	 *
	 * @param codePoints
	 * @return the striped codePoints
	 * @since 3.0.1 initial implementation.
	 * @since 3.0.15 it is unicode aware.
	 */
	private static int[] strip(final int[] codePoints) {

		if (codePoints.length == 0) {
			return new int[0];
		}

		int first;
		int last;

		for (first = 0; first < codePoints.length; first++) {
			final int codePoint = codePoints[first];
			if (codePoint != SPACE_CODE_POINT && codePoint != SLASH_T_CODE_POINT && codePoint != SLASH_N_CODE_POINT
			        && !Character.isWhitespace(codePoint)) {
				break;
			}
		}

		for (last = codePoints.length; last > first; last--) {
			final int codePoint = codePoints[last - 1];
			if (codePoint != SPACE_CODE_POINT && codePoint != SLASH_T_CODE_POINT && codePoint != SLASH_N_CODE_POINT
			        && !Character.isWhitespace(codePoint)) {
				break;
			}
		}

		final int lastRemovedCount = codePoints.length - last;

		final int codePointsCount = codePoints.length - (first + lastRemovedCount);

		final int[] codePointsStripped = new int[codePointsCount];
		System.arraycopy(codePoints, first, codePointsStripped, 0, codePointsCount);
		return codePointsStripped;
	}

	/**
	 * This method is mainly for internal use.
	 *
	 * @param s
	 * @return the returned string will be striped and all lines will be replace by
	 *         {@code \n} .
	 * @since 3.0.15
	 */
	public static String toDynamicJs(final String s) {

		final int[] codePoints = strip(s.codePoints().toArray());

		final StringBuilder builder = new StringBuilder(codePoints.length);

		int prevC = 0;

		for (int i = 0; i < codePoints.length; i++) {
			final int c = codePoints[i];
			if (i == 0) {
				if (c == SLASH_N_CODE_POINT) {
					builder.append("\\n");
				} else if (c != SLASH_R_CODE_POINT) {
					builder.appendCodePoint(c);
				}
			} else {
				if (prevC == SLASH_R_CODE_POINT && c == SLASH_N_CODE_POINT) {
					builder.append("\\n");
				} else if (prevC == SLASH_R_CODE_POINT && c != SLASH_N_CODE_POINT) {
					builder.append("\\n");
					builder.appendCodePoint(c);
				} else if (c == SLASH_N_CODE_POINT) {
					builder.append("\\n");
				} else if (c != SLASH_R_CODE_POINT) {
					builder.appendCodePoint(c);
				}
			}

			prevC = c;
		}

		return builder.toString();
	}

	/**
	 * It replaces all new lines by {@code \n} so that it can be used as a dynamic
	 * JavaScript string in event attributes js function body. It is unicode aware.
	 *
	 * @param s
	 * @return the string having all lines replaced by {@code \n} .
	 * @since 3.0.17
	 */
	public static String toDynamicJsString(final String s) {

		final int[] codePoints = s.codePoints().toArray();

		final StringBuilder builder = new StringBuilder(codePoints.length);

		int prevC = 0;

		for (int i = 0; i < codePoints.length; i++) {
			final int c = codePoints[i];
			if (i == 0) {
				if (c == SLASH_N_CODE_POINT) {
					builder.append("\\n");
				} else if (c != SLASH_R_CODE_POINT) {
					builder.appendCodePoint(c);
				}
			} else {
				if (prevC == SLASH_R_CODE_POINT && c == SLASH_N_CODE_POINT) {
					builder.append("\\n");
				} else if (prevC == SLASH_R_CODE_POINT && c != SLASH_N_CODE_POINT) {
					builder.append("\\n");
					builder.appendCodePoint(c);
				} else if (c == SLASH_N_CODE_POINT) {
					builder.append("\\n");
				} else if (c != SLASH_R_CODE_POINT) {
					builder.appendCodePoint(c);
				}
			}

			prevC = c;
		}

		return builder.toString();
	}

}
