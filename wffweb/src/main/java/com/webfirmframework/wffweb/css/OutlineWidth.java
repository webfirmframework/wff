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
 * @author WFF
 */
package com.webfirmframework.wffweb.css;

import java.io.Serial;
import java.util.Arrays;
import java.util.List;

import com.webfirmframework.wffweb.InvalidValueException;
import com.webfirmframework.wffweb.NullValueException;
import com.webfirmframework.wffweb.css.core.AbstractCssProperty;
import com.webfirmframework.wffweb.util.CssLengthUtil;
import com.webfirmframework.wffweb.util.StringUtil;
import com.webfirmframework.wffweb.util.TagStringUtil;

/**
 * <pre>
 * outline-width: medium|thin|thick|length|initial|inherit;
 *
 * An outline is a line that is drawn around elements (outside the borders) to make the element "stand out".
 *
 * The outline-width specifies the width of an outline.
 *
 * Note: Always declare the outline-style property before the outline-width property. An element must have an outline before you change the width of it.
 * Default value:  medium
 * Inherited:      no
 * Animatable:     yes, see individual properties
 * Version:        CSS2
 * JavaScript syntax:      object.style.outlineWidth="2px"
 * </pre>
 *
 *
 * @author WFF
 * @version 1.0.0
 * @since 1.0.0
 */
public class OutlineWidth extends AbstractCssProperty<OutlineWidth> {

    @Serial
    private static final long serialVersionUID = 1_0_0L;

    public static final String MEDIUM = "medium";
    public static final String THIN = "thin";
    public static final String THICK = "thick";
    public static final String INITIAL = "initial";
    public static final String INHERIT = "inherit";

    private static final List<String> PREDEFINED_CONSTANTS = Arrays.asList(INITIAL, INHERIT, MEDIUM, THIN, THICK);

    private String cssValue;
    private Float value;
    private CssLengthUnit cssLengthUnit;

    /**
     * The {@code initial} will be set as the value
     */
    public OutlineWidth() {
        setCssValue(INITIAL);
    }

    /**
     * @param cssValue the css value to set.
     */
    public OutlineWidth(final String cssValue) {
        setCssValue(cssValue);
    }

    /**
     * @param outlineWidth the {@code OutlineWidth} object from which the cssValue
     *                     to set.And, {@code null} will throw
     *                     {@code NullValueException}
     */
    public OutlineWidth(final OutlineWidth outlineWidth) {
        if (outlineWidth == null) {
            throw new NullValueException("outlineWidth can not be null");
        }
        setCssValue(outlineWidth.getCssValue());
    }

    /**
     * @param percent the percentage value to set. The cssLengthUnit will
     *                automatically set to %.
     * @since 1.0.0
     * @author WFF
     */
    public OutlineWidth(final float percent) {
        cssLengthUnit = CssLengthUnit.PER;
        value = percent;
        cssValue = String.valueOf(percent) + cssLengthUnit;
    }

    /**
     * @param value
     * @param cssLengthUnit
     */
    public OutlineWidth(final float value, final CssLengthUnit cssLengthUnit) {
        this.value = value;
        this.cssLengthUnit = cssLengthUnit;
        cssValue = String.valueOf(value) + cssLengthUnit;
    }

    /**
     * @param value
     * @param cssLengthUnit
     * @return the current object
     * @since 1.0.0
     * @author WFF
     */
    public OutlineWidth setValue(final float value, final CssLengthUnit cssLengthUnit) {
        this.value = value;
        this.cssLengthUnit = cssLengthUnit;
        cssValue = String.valueOf(value) + cssLengthUnit;
        if (getStateChangeInformer() != null) {
            getStateChangeInformer().stateChanged(this);
        }
        return this;
    }

    /**
     * @param percent the percent to set
     * @since 1.0.0
     * @author WFF
     */
    public void setPercent(final float percent) {
        value = percent;
        cssLengthUnit = CssLengthUnit.PER;
        cssValue = String.valueOf(percent) + cssLengthUnit;
        if (getStateChangeInformer() != null) {
            getStateChangeInformer().stateChanged(this);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.webfirmframework.wffweb.css.CssProperty#getCssName()
     *
     * @since 1.0.0
     *
     * @author WFF
     */
    @Override
    public String getCssName() {
        return CssNameConstants.OUTLINE_WIDTH;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.webfirmframework.wffweb.css.CssProperty#getCssValue()
     *
     * @since 1.0.0
     *
     * @author WFF
     */
    @Override
    public String getCssValue() {
        return cssValue;
    }

    @Override
    public String toString() {
        return getCssName() + ": " + getCssValue();
    }

    /**
     * gets the outline-width in float value. {@code OutlineWidth#getUnit()} should
     * be used to get the cssLengthUnit for this value.
     *
     * @return the value in float or null if the cssValue is <code>initial</code> or
     *         <code>inherit</code>.
     * @since 1.0.0
     * @author WFF
     */
    public Float getValue() {
        return value;
    }

    /**
     * @return the cssLengthUnit {@code PX}/{@code PER}, or {@code null} if the
     *         value is any inbuilt value like {@code inherit}.
     * @since 1.0.0
     * @author WFF
     */
    public CssLengthUnit getUnit() {
        return cssLengthUnit;
    }

    /**
     * @param cssValue the value should be in the format of <code>55px</code> or
     *                 <code>95%</code>. {@code null} is considered as an invalid
     *                 value and it will throw {@code NullValueException}.
     * @since 1.0.0
     * @author WFF
     */
    @Override
    public OutlineWidth setCssValue(final String cssValue) {
        try {
            if (cssValue == null) {
                throw new NullValueException(
                        "null is an invalid value. The value format should be as for example 75px or 85%. Or, initial/inherit.");
            } else {
                final String trimmedCssValue = StringUtil.strip(cssValue);
                boolean invalidValue = true;
                for (final CssLengthUnit cssLengthUnit : CssLengthUnit.values()) {
                    final String unit = cssLengthUnit.getUnit();
                    if (trimmedCssValue.endsWith(unit)) {
                        final String valueOnly = cssValue.replaceFirst(unit, "");
                        try {
                            value = Float.parseFloat(valueOnly);
                        } catch (final NumberFormatException e) {
                            break;
                        }
                        this.cssLengthUnit = cssLengthUnit;
                        this.cssValue = cssValue;
                        invalidValue = false;
                        break;
                    }
                }
                if (trimmedCssValue.equalsIgnoreCase(INITIAL) || trimmedCssValue.equalsIgnoreCase(INHERIT)
                        || trimmedCssValue.equalsIgnoreCase(MEDIUM) || trimmedCssValue.equalsIgnoreCase(THIN)
                        || trimmedCssValue.equalsIgnoreCase(THICK)) {
                    this.cssValue = trimmedCssValue.toLowerCase();
                    cssLengthUnit = null;
                    value = null;
                    invalidValue = false;
                }
                if (invalidValue) {
                    throw new InvalidValueException(cssValue
                            + " is an invalid value. The value format should be as for example 75px, 85%, initial, inherit etc..");
                }
            }
            if (getStateChangeInformer() != null) {
                getStateChangeInformer().stateChanged(this);
            }
        } catch (final NumberFormatException e) {
            throw new InvalidValueException(cssValue
                    + " is an invalid value. The value format should be as for example 75px or 85%. Or, initial/inherit.",
                    e);
        }
        return this;
    }

    /**
     * sets as {@code initial}
     *
     * @since 1.0.0
     * @author WFF
     */
    public void setAsInitial() {
        setCssValue(INITIAL);
    }

    /**
     * sets as {@code inherit}
     *
     * @since 1.0.0
     * @author WFF
     */
    public void setAsInherit() {
        setCssValue(INHERIT);
    }

    /**
     * sets as {@code medium}.
     *
     * @since 1.0.0
     * @author WFF
     */
    public void setAsMedium() {
        setCssValue(MEDIUM);
    }

    /**
     * sets as {@code thin}.
     *
     * @since 1.0.0
     * @author WFF
     */
    public void setAsThin() {
        setCssValue(THIN);
    }

    /**
     * sets as {@code thick}.
     *
     * @since 1.0.0
     * @author WFF
     */
    public void setAsThick() {
        setCssValue(THICK);
    }

    /**
     * validates if the given cssValue is valid for this class.
     *
     * @param cssValue the value to check.
     * @return true if valid and false if invalid.
     * @author WFF
     * @since 1.0.0
     */
    public static boolean isValid(final String cssValue) {
        final String trimmedCssValue = TagStringUtil.toLowerCase(StringUtil.strip(cssValue));

        if (StringUtil.containsSpace(trimmedCssValue)) {
            return false;
        }

        if (CssLengthUtil.getLengthValueAsPremitiveAndUnit(trimmedCssValue).length == 2) {
            return true;
        }

        return PREDEFINED_CONSTANTS.contains(trimmedCssValue);
    }

}
