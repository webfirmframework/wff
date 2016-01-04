/*
 * Copyright 2014-2016 Web Firm Framework
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
package com.webfirmframework.wffweb.util;

import com.webfirmframework.wffweb.css.CssLengthUnit;

/**
 * a utility class for css length value manipulations.
 *
 * @author WFF
 * @since 1.0.0
 * @version 1.0.0
 *
 */
public class CssLengthUtil {

    /**
     *
     * @author WFF
     * @since 1.0.0
     */
    private CssLengthUtil() {
        throw new AssertionError();
    }

    /**
     * gets the length value and unit as an array. For a cssValue
     * <code>555px</code>, the returned array may be used as
     *
     * <pre>
     * Object[] lengthValueAsPremitiveAndUnit = CssLengthUtil
     *         .getLengthValueAsPremitiveAndUnit(&quot;555px&quot;);
     * float value = (float) lengthValueAsPremitiveAndUnit[0];
     *
     * // the object will be equal to CssLengthUnit.PX
     * CssLengthUnit unit = (CssLengthUnit) lengthValueAsPremitiveAndUnit[1];
     *
     * Auto-boxing is done when the primitive value is stored in an object array
     * therefore there is no much advantage with this method.
     * [This method is left for future modification.]
     *
     * </pre>
     *
     * @param cssValue
     *            the value from which the length value and unit required to be
     *            parsed, Eg:- <code>555px</code>.
     * @return an array containing length and unit. The length will be in the
     *         zeroth index as {@code float} (primitive type) type and its unit
     *         in the first index as an object of {@code CssLengthUnit}. If the
     *         given cssValue doesn't contain unit but contains value then an
     *         array containing only length value (i.e. array having length one)
     *         will be returned. For any invalid value it will return an empty
     *         array (having length zero), specifically it will never return
     *         null.
     *
     * @author WFF
     * @since 1.0.0
     */
    public static Object[] getLengthValueAsPremitiveAndUnit(
            final String cssValue) {
        for (final CssLengthUnit cssLengthUnit : CssLengthUnit.values()) {
            final String unit = cssLengthUnit.getUnit();
            if (cssValue.endsWith(unit)) {
                final String valueOnly = cssValue.replaceFirst(unit, "");
                try {
                    return new Object[] { Float.parseFloat(valueOnly),
                            cssLengthUnit };
                } catch (final NumberFormatException e) {
                    return new Object[0];
                }
            }
        }

        try {
            return new Object[] { Float.parseFloat(cssValue) };
        } catch (final NumberFormatException e) {
            return new Object[0];
        }
    }

    /**
     * gets the length value and unit as an array. For a cssValue
     * <code>555px</code>, the returned array may be used as
     *
     * <pre>
     * Object[] lengthValueAndUnit = CssLengthUtil.getLengthValueAndUnit(&quot;555px&quot;);
     * Float value = (Float) lengthValueAndUnit[0];
     *
     * // the object will be equal to CssLengthUnit.PX
     * CssLengthUnit unit = (CssLengthUnit) lengthValueAndUnit[1];
     * </pre>
     *
     * @param cssValue
     *            the value from which the length value and unit required to be
     *            parsed, Eg:- <code>555px</code>.
     * @return an array containing length and unit. The length will be in the
     *         zeroth index as {@code Float} (wrapper type) type and its unit in
     *         the first index as an object of {@code CssLengthUnit}. If the
     *         given cssValue doesn't contain unit but contains value then an
     *         array containing only length value (i.e. array having length one)
     *         will be returned. For any invalid value it will return an empty
     *         array (having length zero), specifically it will never return
     *         null.
     *
     * @author WFF
     * @since 1.0.0
     */
    public static Object[] getLengthValueAndUnit(final String cssValue) {
        for (final CssLengthUnit cssLengthUnit : CssLengthUnit.values()) {
            final String unit = cssLengthUnit.getUnit();
            if (cssValue.endsWith(unit)) {
                final String valueOnly = cssValue.replaceFirst(unit, "");
                try {
                    return new Object[] { Float.valueOf(valueOnly),
                            cssLengthUnit };
                } catch (final NumberFormatException e) {
                    return new Object[0];
                }
            }
        }

        try {
            return new Object[] { Float.valueOf(cssValue) };
        } catch (final NumberFormatException e) {
            return new Object[0];
        }
    }

}
