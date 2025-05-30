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
import java.util.logging.Level;
import java.util.logging.Logger;

import com.webfirmframework.wffweb.InvalidValueException;
import com.webfirmframework.wffweb.NullValueException;
import com.webfirmframework.wffweb.clone.CloneUtil;
import com.webfirmframework.wffweb.css.CssNameConstants;
import com.webfirmframework.wffweb.css.UrlCss3Value;
import com.webfirmframework.wffweb.css.core.AbstractCssProperty;
import com.webfirmframework.wffweb.data.Bean;
import com.webfirmframework.wffweb.informer.StateChangeInformer;
import com.webfirmframework.wffweb.util.CssValueUtil;
import com.webfirmframework.wffweb.util.StringBuilderUtil;
import com.webfirmframework.wffweb.util.StringUtil;

/**
 * <pre>
 * icon: {@code auto|<i>URL<i>|initial|inherit;}
 *
 * The icon property provides the author the ability to style an element with an iconic equivalent.
 *
 * Note: An element's icon is not used unless the "content" property is set to the value "icon"!
 * Default value:  auto
 * Inherited:      no
 * Animatable:     no
 * Version:        CSS3
 * JavaScript syntax:      object.style.icon="url(image.png)"
 * </pre>
 *
 * @author WFF
 * @since 1.0.0
 */
public class Icon extends AbstractCssProperty<Icon> implements StateChangeInformer<Bean> {

    @Serial
    private static final long serialVersionUID = 1_0_0L;

    public static final String INITIAL = "initial";
    public static final String INHERIT = "inherit";
    public static final String NONE = "none";

    private static final List<String> PREDEFINED_CONSTANTS = Arrays.asList(INITIAL, INHERIT, NONE);

    private static final Logger LOGGER = Logger.getLogger(Icon.class.getName());

    private String cssValue;

    // currently this variable is unused but it might be required for future
    // update.
    @SuppressWarnings("unused")
    private String[] imageUrls;

    private UrlCss3Value[] urlCss3Values;

    /**
     * the default value is <code>none</code>
     *
     * @author WFF
     * @since 1.0.0
     */
    public Icon() {
        setCssValue(NONE);
    }

    /**
     * @param cssValue the cssValue to set. <br>
     *                 eg:- {@code url(images/BackgroundDesign.png)}
     * @author WFF
     */
    public Icon(final String cssValue) {
        setCssValue(cssValue);
    }

    /**
     * @param icon the {@code Icon} object from which the cssName and cssValue to
     *             set.
     * @author WFF
     */
    public Icon(final Icon icon) {
        if (icon == null) {
            throw new NullValueException("customCss can not be null");
        }
        if (icon.getCssName() == null) {
            throw new NullValueException("customCss.getCssName() can not be null");
        }
        setCssValue(icon.getCssValue());
    }

    /**
     *
     * sample code :- {@code new Icon("Test.gif", "TestImage.png")} creates
     * <code>background-image: url("Test.gif"), url("TestImage.png");</code> . For
     * css3 syntax method please use {@code new Icon(UrlCss3Value... urlCss3Values)}
     * or {@code setImageUrls(UrlCss3Value... urlCss3Values)} method.
     *
     * @param imageUrls an array of icon urls, eg:-
     *                  {@code icon.setImageUrls("Test.gif", "TestImage.png")} and
     *                  the generated css will be
     *                  <code>background-image: url("Test.gif"), url("TestImage.png");</code>
     * @since 1.0.0
     * @author WFF
     */
    public Icon(final String... imageUrls) {
        setImageUrls(imageUrls);
    }

    /**
     * @param urlCss3Values an array of {@code UrlCss3Value} objects.
     * @author WFF
     */
    public Icon(final UrlCss3Value... urlCss3Values) {
        setImageUrls(urlCss3Values);
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
        return CssNameConstants.ICON;
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
     * @param cssValue {@code null} is considered as an invalid value.
     * @since 1.0.0
     * @author WFF
     */
    @Override
    public Icon setCssValue(final String cssValue) {

        CssValueUtil.throwExceptionForInvalidValue(cssValue);

        if (PREDEFINED_CONSTANTS.contains(StringUtil.strip(cssValue))) {
            imageUrls = null;
            if (urlCss3Values != null) {
                for (final UrlCss3Value urlCss3Value : urlCss3Values) {
                    urlCss3Value.setAlreadyInUse(false);
                }
            }
            urlCss3Values = null;
            this.cssValue = cssValue;
            return this;
        }

        final String[] cssValueParts = StringUtil.splitByComma(cssValue);
        if (cssValueParts.length > 1) {
            final StringBuilder cssValueBuilder = new StringBuilder();
            if (urlCss3Values == null) {
                urlCss3Values = new UrlCss3Value[cssValueParts.length];
                for (int i = 0; i < cssValueParts.length; i++) {
                    final UrlCss3Value urlCss3Value = new UrlCss3Value(cssValueParts[i]);
                    urlCss3Value.setAlreadyInUse(true);
                    urlCss3Value.setStateChangeInformer(this);
                    urlCss3Values[i] = urlCss3Value;

                    cssValueBuilder.append(urlCss3Value.getValue());
                    if (i < cssValueParts.length - 1) {
                        cssValueBuilder.append(", ");
                    } else {
                        cssValueBuilder.append(' ');
                    }
                }
            } else {

                UrlCss3Value[] urlCss3ValuesTemp = null;
                if (cssValueParts.length == urlCss3Values.length) {
                    urlCss3ValuesTemp = urlCss3Values;
                } else {
                    urlCss3ValuesTemp = new UrlCss3Value[cssValueParts.length];
                }
                for (int i = 0; i < cssValueParts.length; i++) {
                    final UrlCss3Value urlCss3Value;
                    if (i < urlCss3Values.length) {
                        urlCss3Value = urlCss3Values[i];
                        urlCss3Value.setUrlCssValue(cssValueParts[i]);
                    } else {
                        urlCss3Value = new UrlCss3Value(cssValueParts[i]);
                    }

                    urlCss3Value.setAlreadyInUse(true);
                    urlCss3Value.setStateChangeInformer(this);
                    urlCss3ValuesTemp[i] = urlCss3Value;
                    cssValueBuilder.append(urlCss3Value.getValue());
                    if (i < cssValueParts.length - 1) {
                        cssValueBuilder.append(", ");
                    } else {
                        cssValueBuilder.append(' ');
                    }

                }

                for (int i = cssValueParts.length; i < urlCss3Values.length; i++) {
                    urlCss3Values[i].setAlreadyInUse(false);
                }

                for (final UrlCss3Value urlCss3Value : urlCss3ValuesTemp) {
                    urlCss3Value.setStateChangeInformer(this);
                    urlCss3Value.setAlreadyInUse(true);
                }
                urlCss3Values = urlCss3ValuesTemp;
            }
            this.cssValue = StringBuilderUtil.getTrimmedString(cssValueBuilder);
        } else {
            this.cssValue = cssValue;
        }
        if (getStateChangeInformer() != null) {
            getStateChangeInformer().stateChanged(this);
        }
        return this;
    }

    /**
     * sample code :- {@code icon.setImageUrls("Test.gif", "TestImage.png")} creates
     * <code>background-image: url("Test.gif"), url("TestImage.png");</code>. For
     * css3 syntax method please use
     * {@code setImageUrls(UrlCss3Value... urlCss3Values)} method.
     *
     * @param imageUrls an array of icon urls, eg:-
     *                  {@code icon.setImageUrls("Test.gif", "TestImage.png")} and
     *                  the generated css will be
     *                  <code>background-image: url("Test.gif"), url("TestImage.png");</code>
     * @since 1.0.0
     * @author WFF
     */
    public void setImageUrls(final String... imageUrls) {
        if (imageUrls == null) {
            throw new NullValueException("imageUrls is null");
        }
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < imageUrls.length; i++) {
            sb.append("url(\"").append(imageUrls[i]).append("\")");
            if (i < imageUrls.length - 1) {
                sb.append(", ");
            } else {
                sb.append(' ');
            }
        }
        setCssValue(StringBuilderUtil.getTrimmedString(sb));
        this.imageUrls = imageUrls;
    }

    /**
     * @param urlCss3Values urlCss3Values an array of {@code UrlCss3Value} objects.
     * @since 1.0.0
     * @author WFF
     */
    public void setImageUrls(final UrlCss3Value... urlCss3Values) {
        if (urlCss3Values == null) {
            throw new NullValueException("urlCss3Values is null");
        }
        if (this.urlCss3Values != null) {
            for (final UrlCss3Value urlCss3Value : this.urlCss3Values) {
                urlCss3Value.setAlreadyInUse(false);
            }
        }

        final StringBuilder cssValueBuilder = new StringBuilder();

        final int urlCss3ValuesLength = urlCss3Values.length;
        for (int i = 0; i < urlCss3ValuesLength; i++) {
            UrlCss3Value urlCss3Value = urlCss3Values[i];
            if (urlCss3Value.isAlreadyInUse()) {
                try {
                    final UrlCss3Value clonedUrlCss3Value = CloneUtil
                            .<UrlCss3Value>deepCloneOnlyIfDoesNotContain(urlCss3Value, this.urlCss3Values);
                    if (!Objects.equals(clonedUrlCss3Value, urlCss3Value)) {
                        urlCss3Value = clonedUrlCss3Value;
                        if (LOGGER.isLoggable(Level.WARNING)) {
                            LOGGER.warning("cloned urlCss3Value " + urlCss3Value + "(hashcode: "
                                    + urlCss3Value.hashCode() + ") as it is already used by another object");
                        }
                    }
                } catch (final CloneNotSupportedException e) {
                    if (LOGGER.isLoggable(Level.SEVERE)) {
                        LOGGER.log(Level.SEVERE, e.getMessage(), e);
                    }
                }
            }
            cssValueBuilder.append(urlCss3Value.getValue());
            if (i < urlCss3ValuesLength - 1) {
                cssValueBuilder.append(", ");
            } else {
                cssValueBuilder.append(' ');
            }
            urlCss3Value.setAlreadyInUse(true);
            urlCss3Value.setStateChangeInformer(this);
        }

        if (this.urlCss3Values != null) {
            for (final UrlCss3Value urlCss3Value : this.urlCss3Values) {
                urlCss3Value.setAlreadyInUse(false);
            }
        }

        for (final UrlCss3Value urlCss3Value : urlCss3Values) {
            urlCss3Value.setAlreadyInUse(true);
            urlCss3Value.setStateChangeInformer(this);
        }

        this.urlCss3Values = urlCss3Values;
        cssValue = StringBuilderUtil.getTrimmedString(cssValueBuilder);
    }

    /**
     * @return a new object of unmodifiable {@code List<UrlCss3Value>} whenever this
     *         method is called. Or null.
     *
     * @author WFF
     * @since 1.0.0
     */
    public List<UrlCss3Value> getUrlCss3Values() {
        // for security improvements for urlCss3Values without
        // compromising performance.
        // TODO the drawback of this the returning of new object each time even
        // if the urlCss3Values array has not been modified, as a minor
        // performance
        // improvement, return a new object only if the urlCss3Values is
        // modified.
        if (urlCss3Values == null) {
            return null;
        }
        return List.of(urlCss3Values);
    }

    /**
     *
     * @author WFF
     * @since 1.0.0
     */
    public void setAsInitial() {
        setCssValue(INITIAL);
    }

    /**
     *
     * @author WFF
     * @since 1.0.0
     */
    public void setAsInherit() {
        setCssValue(INHERIT);
    }

    @Override
    public void stateChanged(final Bean stateChangedObject) {
        if (urlCss3Values != null && stateChangedObject instanceof UrlCss3Value) {
            final UrlCss3Value urlCss3Value = (UrlCss3Value) stateChangedObject;
            if (urlCss3Value.getX() > -1 || urlCss3Value.getY() > -1) {
                throw new InvalidValueException("urlCss3Value cannot be set with x or y values.");
            }
            setImageUrls(urlCss3Values);
            if (getStateChangeInformer() != null) {
                getStateChangeInformer().stateChanged(this);
            }
        }
    }
}
