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
package com.webfirmframework.wffweb.css.css3;

import java.io.Serial;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.webfirmframework.wffweb.InvalidValueException;
import com.webfirmframework.wffweb.NullValueException;
import com.webfirmframework.wffweb.css.CssLengthUnit;
import com.webfirmframework.wffweb.css.CssNameConstants;
import com.webfirmframework.wffweb.css.core.AbstractCssProperty;
import com.webfirmframework.wffweb.util.CssLengthUtil;
import com.webfirmframework.wffweb.util.StringUtil;
import com.webfirmframework.wffweb.util.TagStringUtil;

/**
 * <pre>
 * background-size: auto|<i>length</i>|cover|contain|initial|inherit;
 *
 * The background-size property specifies the size of the background images.
 * Default value:  auto
 * Inherited:      no
 * Animatable:     yes
 * Version:        CSS3
 * JavaScript syntax:      object.style.backgroundSize="60px 120px"
 * </pre>
 *
 * @author WFF
 * @since 1.0.0
 */
public class BackgroundSize extends AbstractCssProperty<BackgroundSize> {

    @Serial
    private static final long serialVersionUID = 1_0_0L;

    public static final String INITIAL = "initial";
    public static final String INHERIT = "inherit";
    public static final String AUTO = "auto";
    public static final String COVER = "cover";
    public static final String CONTAIN = "contain";

    private static final List<String> PREDEFINED_CONSTANTS = Arrays.asList(INITIAL, INHERIT, AUTO, COVER, CONTAIN);

    private String cssValue;
    private Float width;
    private CssLengthUnit widthLengthUnit;

    private Float height;
    private CssLengthUnit heightLengthUnit;

    /**
     * The {@code 0px} will be set as the value
     */
    public BackgroundSize() {
        setCssValue("0px");
    }

    /**
     * @param cssValue the css value to set.
     */
    public BackgroundSize(final String cssValue) {
        setCssValue(cssValue);
    }

    /**
     * @param backgroundSize the {@code BackgroundSize} object from which the
     *                       cssValue to set.And, {@code null} will throw
     *                       {@code NullValueException}
     */
    public BackgroundSize(final BackgroundSize backgroundSize) {
        if (backgroundSize == null) {
            throw new NullValueException("backgroundSize can not be null");
        }
        setCssValue(backgroundSize.getCssValue());
    }

    /**
     * @param widthHeight   the value to set.
     * @param cssLengthUnit the value unit to set.
     * @since 1.0.0
     * @author WFF
     */
    public BackgroundSize(final float widthHeight, final CssLengthUnit cssLengthUnit) {

        width = height = Float.valueOf(widthHeight);
        widthLengthUnit = heightLengthUnit = cssLengthUnit;

        cssValue = String.valueOf(widthHeight) + cssLengthUnit;
    }

    /**
     * @param width
     * @param widthLengthUnit
     * @param height
     * @param heightLengthUnit
     * @author WFF
     * @since 1.0.0
     */
    public BackgroundSize(final float width, final CssLengthUnit widthLengthUnit, final float height,
            final CssLengthUnit heightLengthUnit) {

        this.width = Float.valueOf(width);
        this.height = Float.valueOf(height);

        this.widthLengthUnit = widthLengthUnit;
        this.heightLengthUnit = heightLengthUnit;

        if (width == height && Objects.equals(widthLengthUnit, heightLengthUnit)) {
            cssValue = String.valueOf(width) + widthLengthUnit;
        } else {
            cssValue = new StringBuilder().append(width).append(widthLengthUnit).append(' ').append(height)
                    .append(heightLengthUnit).toString();

        }
    }

    /**
     * @param width
     * @param widthLengthUnit
     * @param height
     * @param heightLengthUnit
     * @return
     * @author WFF
     * @since 1.0.0
     */
    public BackgroundSize setValue(final float width, final CssLengthUnit widthLengthUnit, final float height,
            final CssLengthUnit heightLengthUnit) {

        this.width = Float.valueOf(width);
        this.height = Float.valueOf(height);

        this.widthLengthUnit = widthLengthUnit;
        this.heightLengthUnit = heightLengthUnit;

        if (width == height && Objects.equals(widthLengthUnit, heightLengthUnit)) {
            cssValue = String.valueOf(width) + widthLengthUnit;
        } else {
            cssValue = new StringBuilder().append(width).append(widthLengthUnit).append(' ').append(height)
                    .append(heightLengthUnit).toString();

        }

        if (getStateChangeInformer() != null) {
            getStateChangeInformer().stateChanged(this);
        }
        return this;
    }

    /**
     * @param widthHeight
     * @param cssLengthUnit
     * @return the current object
     * @since 1.0.0
     * @author WFF
     */
    public BackgroundSize setValue(final float widthHeight, final CssLengthUnit cssLengthUnit) {

        width = height = Float.valueOf(widthHeight);
        widthLengthUnit = heightLengthUnit = cssLengthUnit;

        cssValue = String.valueOf(widthHeight) + cssLengthUnit;

        if (getStateChangeInformer() != null) {
            getStateChangeInformer().stateChanged(this);
        }
        return this;
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
        return CssNameConstants.BACKGROUND_SIZE;
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
     * gets the horizontal value in float value.
     * {@code BackgroundSize#getWidthUnit()} should be used to get the cssLengthUnit
     * for this value.
     *
     * @return the horizontal value in float or {@code null} if the value is any
     *         inbuilt value like {@code inherit}.
     * @since 1.0.0
     * @author WFF
     */
    public Float getWidth() {
        return width;
    }

    /**
     * gets the vertical value in float value.
     * {@code BackgroundSize#getHeightUnit()} should be used to get the
     * cssLengthUnit for this value.
     *
     * @return the vertical value in float or {@code null} if the value is any
     *         inbuilt value like {@code inherit}.
     * @since 1.0.0
     * @author WFF
     */
    public Float getHeight() {
        return height;
    }

    /**
     * @return the cssLengthUnit {@code PX}/{@code PER}, or {@code null} if the
     *         value is any inbuilt value like {@code inherit}.
     * @since 1.0.0
     * @author WFF
     */
    public CssLengthUnit getWidthUnit() {
        return widthLengthUnit;
    }

    /**
     * @return the cssLengthUnit {@code PX}/{@code PER}, or {@code null} if the
     *         value is any inbuilt value like {@code inherit}.
     * @since 1.0.0
     * @author WFF
     */
    public CssLengthUnit getHeightUnit() {
        return heightLengthUnit;
    }

    /**
     * @param cssValue the value should be in the format of <code>55px</code> or
     *                 <code>95%</code>. {@code null} is considered as an invalid
     *                 value and it will throw {@code NullValueException}.
     * @since 1.0.0
     * @author WFF
     */
    @Override
    public BackgroundSize setCssValue(final String cssValue) {
        try {
            if (cssValue == null) {
                throw new NullValueException(
                        "null is an invalid value. The value format should be as for example 75px or 85%. Or, initial/inherit.");
            } else {
                final String trimmedCssValue = TagStringUtil.toLowerCase(StringUtil.strip(cssValue));
                boolean invalidValue = true;

                final String[] lengthValues = StringUtil.splitBySpace(trimmedCssValue);

                if (lengthValues.length == 1) {
                    if (trimmedCssValue.equalsIgnoreCase(INITIAL) || trimmedCssValue.equalsIgnoreCase(INHERIT)) {
                        this.cssValue = trimmedCssValue;
                        width = null;
                        height = null;
                        widthLengthUnit = null;
                        heightLengthUnit = null;
                        invalidValue = false;
                    } else {
                        final Object[] lengthValueAndUnitHorizontalVertical = CssLengthUtil
                                .getLengthValueAndUnit(lengthValues[0]);

                        if (lengthValueAndUnitHorizontalVertical.length == 2) {
                            width = height = (Float) lengthValueAndUnitHorizontalVertical[0];
                            widthLengthUnit = heightLengthUnit = (CssLengthUnit) lengthValueAndUnitHorizontalVertical[1];
                            this.cssValue = trimmedCssValue;
                            invalidValue = false;
                        }
                    }
                } else if (lengthValues.length == 2) {
                    final Object[] lengthValueAndUnitHorizontal = CssLengthUtil.getLengthValueAndUnit(lengthValues[0]);

                    final Object[] lengthValueAndUnitVertical = CssLengthUtil.getLengthValueAndUnit(lengthValues[1]);

                    if (lengthValueAndUnitHorizontal.length == 2 && lengthValueAndUnitVertical.length == 2) {
                        width = (Float) lengthValueAndUnitHorizontal[0];
                        widthLengthUnit = (CssLengthUnit) lengthValueAndUnitHorizontal[1];
                        height = (Float) lengthValueAndUnitVertical[0];
                        heightLengthUnit = (CssLengthUnit) lengthValueAndUnitVertical[1];

                        // nano optimized way to check if both width
                        // and height are equal.
                        if (Float.floatToIntBits(width.floatValue()) == Float.floatToIntBits(width.floatValue())
                                && Objects.equals(widthLengthUnit, heightLengthUnit)) {
                            this.cssValue = String.valueOf(width) + heightLengthUnit;
                        } else {
                            this.cssValue = trimmedCssValue;
                        }
                        invalidValue = false;
                    }
                } else {
                    throw new InvalidValueException("The given cssValue should not contain more that 2 length values.");
                }

                if (invalidValue) {
                    throw new InvalidValueException(cssValue
                            + " is an invalid value. The value format should be as for example 75px, 85%, 125px 10px, initial, inherit etc..");
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
     * validates if the given cssValue is valid for this class.
     *
     * @param cssValue the value to check.
     * @return true if valid and false if invalid.
     * @author WFF
     * @since 1.0.0
     */
    public static boolean isValid(final String cssValue) {
        final String trimmedCssValue = TagStringUtil.toLowerCase(StringUtil.strip(cssValue));

        final String[] cssValueParts = StringUtil.splitBySpace(trimmedCssValue);
        if (cssValueParts.length > 2) {
            return false;
        }
        if (PREDEFINED_CONSTANTS.contains(trimmedCssValue)) {
            return true;
        }

        boolean valid = false;
        for (final String each : cssValueParts) {
            final Object[] lengthValueAndUnit = CssLengthUtil.getLengthValueAsPremitiveAndUnit(each);
            if (lengthValueAndUnit.length != 2) {
                return false;
            }
            valid = true;
        }
        return valid;
    }
}
