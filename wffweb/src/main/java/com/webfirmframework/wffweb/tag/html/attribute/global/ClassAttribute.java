/*
 * Copyright 2014-2018 Web Firm Framework
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
package com.webfirmframework.wffweb.tag.html.attribute.global;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.webfirmframework.wffweb.tag.html.attribute.AttributeNameConstants;
import com.webfirmframework.wffweb.tag.html.attribute.core.AbstractAttribute;
import com.webfirmframework.wffweb.tag.html.identifier.GlobalAttributable;
import com.webfirmframework.wffweb.util.StringBuilderUtil;
import com.webfirmframework.wffweb.util.StringUtil;

/**
 * @author WFF
 *
 */
public class ClassAttribute extends AbstractAttribute
        implements GlobalAttributable {

    /**
     *
     */
    private static final long serialVersionUID = 1_0_0L;

    {
        super.setAttributeName(AttributeNameConstants.CLASS);
        init();
    }

    public ClassAttribute() {
    }

    /**
     * one or more class name separated by space or as an array of class names.
     *
     * @param classNames
     */
    public ClassAttribute(final String... classNames) {
        splitAndAdd(classNames);
    }

    private void splitAndAdd(final String... attrValues) {
        if (attrValues != null) {

            final List<String> allValues = new ArrayList<String>(
                    attrValues.length);
            for (final String className : attrValues) {
                String trimmmedValue = null;
                if (className != null
                        && !(trimmmedValue = className.trim()).isEmpty()) {
                    final String[] values = StringUtil
                            .splitBySpace(trimmmedValue);
                    allValues.addAll(Arrays.asList(values));
                }
            }

            addAllToAttributeValueSet(allValues);
        }
    }

    /**
     * invokes only once per object
     *
     * @author WFF
     * @since 1.0.0
     */
    protected void init() {
        // to override and use this method
    }

    /**
     * adds the given class names.
     *
     * @param classNames
     *            one or more class names separated by space or as an array of
     *            class names.
     * @since 1.0.0
     * @author WFF
     */
    public void addClassNames(final String... classNames) {
        splitAndAdd(classNames);
    }

    /**
     * removed all current class names and adds the given class names.
     *
     * @param classNames
     *            one or more class names separated by space or an array of
     *            class names.
     * @since 1.0.0
     * @author WFF
     */
    public void addNewClassNames(final String... classNames) {

        if (classNames != null) {
            final List<String> allValues = new ArrayList<String>(
                    classNames.length);
            for (final String className : classNames) {
                String trimmmedValue = null;
                if (className != null
                        && !(trimmmedValue = className.trim()).isEmpty()) {
                    final String[] values = StringUtil
                            .splitBySpace(trimmmedValue);
                    allValues.addAll(Arrays.asList(values));
                }
            }
            removeAllFromAttributeValueSet();
            addAllToAttributeValueSet(allValues);
        }
    }

    /**
     * adds the given class names in the class attribute
     *
     * @param classNames
     * @since 1.0.0
     * @author WFF
     */
    public void addAllClassNames(final Collection<String> classNames) {
        addAllToAttributeValueSet(classNames);
    }

    /**
     * removes all class names from the class attribute
     *
     * @param classNames
     *            the class names to remove
     * @since 1.0.0
     * @author WFF
     */
    public void removeAllClassNames(final Collection<String> classNames) {
        removeAllFromAttributeValueSet(classNames);
    }

    /**
     * removes the given class name
     *
     * @param className
     *            the class name to remove
     * @since 1.0.0
     * @author WFF
     */
    public void removeClassName(final String className) {
        removeFromAttributeValueSet(className);
    }

    /**
     * @return the value string of class names
     * @since 2.1.9
     * @author WFF
     */
    @Override
    public String getAttributeValue() {

        final StringBuilder builder = new StringBuilder();
        for (final String className : getAttributeValueSet()) {
            builder.append(className).append(' ');
        }

        return StringBuilderUtil.getTrimmedString(builder);
    }

    /**
     * NB:- every time it returns a <code>new LinkedHashSet</code> object and
     * changes to this set object will not have any affect on this
     * <code>ClassAttribute</code> object.
     *
     * @return the set of class names it contained.
     * @since 2.1.9
     * @author WFF
     */
    public Set<String> getClassNames() {
        return new LinkedHashSet<String>(getAttributeValueSet());
    }

    /**
     * sets the value for this attribute
     *
     * @param updateClient
     *            true to update client browser page if it is available. The
     *            default value is true but it will be ignored if there is no
     *            client browser page.
     * @param value
     *            the value for the attribute.
     * @since 2.1.15
     * @author WFF
     */
    public void setValue(final boolean updateClient, final String value) {
        if (value != null) {
            final String[] inputValues = StringUtil.splitBySpace(value);
            final List<String> allValues = new ArrayList<String>(
                    inputValues.length);
            for (final String each : inputValues) {
                String trimmmedValue = null;
                if (each != null && !(trimmmedValue = each.trim()).isEmpty()) {
                    final String[] values = StringUtil
                            .splitBySpace(trimmmedValue);
                    allValues.addAll(Arrays.asList(values));
                }
            }
            removeAllFromAttributeValueSet();
            addAllToAttributeValueSet(updateClient, allValues);
        }
    }

    /**
     * sets the value for this attribute
     *
     *
     * @param value
     *            the value for the attribute.
     * @since 2.1.15
     * @author WFF
     */
    public void setValue(final String value) {
        if (value != null) {
            final String[] inputValues = StringUtil.splitBySpace(value);
            final List<String> allValues = new ArrayList<String>(
                    inputValues.length);
            for (final String each : inputValues) {
                String trimmmedValue = null;
                if (each != null && !(trimmmedValue = each.trim()).isEmpty()) {
                    final String[] values = StringUtil
                            .splitBySpace(trimmmedValue);
                    allValues.addAll(Arrays.asList(values));
                }
            }
            removeAllFromAttributeValueSet();
            addAllToAttributeValueSet(allValues);
        }
    }

}
