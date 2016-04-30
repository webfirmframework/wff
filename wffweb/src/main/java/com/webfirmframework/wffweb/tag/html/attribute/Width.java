package com.webfirmframework.wffweb.tag.html.attribute;

import com.webfirmframework.wffweb.css.CssLengthUnit;
import com.webfirmframework.wffweb.css.core.LengthUnit;
import com.webfirmframework.wffweb.tag.html.attribute.core.AbstractAttribute;
import com.webfirmframework.wffweb.tag.html.identifier.InputAttributable;

/**
 * This is a width attribute.
 *
 * @author WFF
 * @since 1.0.0
 *
 */
public class Width extends AbstractAttribute implements InputAttributable {

    private static final long serialVersionUID = 1_0_0L;

    private String htmlString;
    private float value;
    private LengthUnit cssLengthUnit;

    {
        super.setAttributeName(AttributeNameConstants.WIDTH);
        init();
    }

    /**
     * the value will be set as <code>100%</code>
     *
     * @since 1.0.0
     */
    public Width() {
        value = 100;
        cssLengthUnit = CssLengthUnit.PER;
        htmlString = (int) value + "" + CssLengthUnit.PER.getUnit();
        setAttributeValue(htmlString);
    }

    /**
     * @param percent
     *            the percentage value to set.
     * @since 1.0.0
     */
    public Width(final float percent) {
        value = percent;
        cssLengthUnit = CssLengthUnit.PER;
        htmlString = percent + "" + CssLengthUnit.PER.getUnit();
        setAttributeValue(htmlString);
    }

    /**
     * @param value
     *            the value to set
     * @param cssLengthUnit
     *            the unit for the value.
     * @since 1.0.0
     */
    public Width(final int value, final CssLengthUnit cssLengthUnit) {
        this.value = value;
        this.cssLengthUnit = cssLengthUnit;
        htmlString = value + "" + cssLengthUnit.getUnit();
        setAttributeValue(htmlString);
    }

    /**
     * @param value
     *            the value to set
     * @param lengthUnit
     *            The length unit. It may be an enum class which implements
     *            {@code LengthUnit} interface. the unit for the value.
     * @since 1.0.0
     */
    public Width(final int value, final LengthUnit lengthUnit) {
        this.value = value;
        cssLengthUnit = lengthUnit;
        htmlString = value + "" + lengthUnit.getUnit();
        setAttributeValue(htmlString);
    }

    protected void init() {
        // to override and use this method
    }

    /**
     * @param percent
     *            the percent to set
     * @since 1.0.0
     */
    public void setPercent(final float percent) {
        value = percent;
        cssLengthUnit = CssLengthUnit.PER;
        setAttributeValue(percent + "" + cssLengthUnit.getUnit());
    }

    /**
     * @param value
     *            the value to set
     * @param cssLengthUnit
     *            the unit for the value. return the current object will be
     *            returned.
     * @since 1.0.0
     */
    public Width setValue(final int value, final CssLengthUnit cssLengthUnit) {
        this.value = value;
        this.cssLengthUnit = cssLengthUnit;
        htmlString = value + "" + cssLengthUnit.getUnit();
        setAttributeValue(htmlString);
        return this;
    }

    /**
     * @param value
     *            the value to set
     * @param lengthUnit
     *            the unit for the value. It may be an enum class which
     *            implements {@code LengthUnit} interface. return the current
     *            object will be returned.
     * @since 1.0.0
     */
    public Width setValue(final int value, final LengthUnit lengthUnit) {
        this.value = value;
        cssLengthUnit = lengthUnit;
        htmlString = value + "" + lengthUnit.getUnit();
        setAttributeValue(htmlString);
        return this;
    }

    /**
     * @param value
     *            the value to set
     * @param cssLengthUnit
     *            the unit for the value. return the current object will be
     *            returned.
     * @since 1.0.0
     */
    public Width setValue(final float value,
            final CssLengthUnit cssLengthUnit) {
        this.value = value;
        this.cssLengthUnit = cssLengthUnit;
        htmlString = value + "" + cssLengthUnit.getUnit();
        setAttributeValue(htmlString);
        return this;
    }

    /**
     * @param value
     *            the value to set
     * @param lengthUnit
     *            the unit for the value. return the current object will be
     *            returned.
     * @since 1.0.0
     */
    public Width setValue(final float value, final LengthUnit lengthUnit) {
        this.value = value;
        cssLengthUnit = lengthUnit;
        htmlString = value + "" + lengthUnit.getUnit();
        setAttributeValue(htmlString);
        return this;
    }

    /**
     * @return the value in float.
     * @since 1.0.0
     */
    public float getValue() {
        return value;
    }

    /**
     * @return the unit for the value.
     * @since 1.0.0
     */
    public LengthUnit getUnit() {
        return cssLengthUnit;
    }

}
