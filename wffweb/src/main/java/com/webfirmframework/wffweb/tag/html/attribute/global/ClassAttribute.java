/*
 * Copyright 2014-2023 Web Firm Framework
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

import java.util.Collection;
import java.util.Set;

import com.webfirmframework.wffweb.tag.html.attribute.core.AbstractValueSetAttribute;
import com.webfirmframework.wffweb.tag.html.attribute.core.PreIndexedAttributeName;
import com.webfirmframework.wffweb.tag.html.identifier.GlobalAttributable;

/**
 * @author WFF
 *
 */
public class ClassAttribute extends AbstractValueSetAttribute implements GlobalAttributable {

    /**
     *
     */
    private static final long serialVersionUID = 1_0_0L;

    private static final PreIndexedAttributeName PRE_INDEXED_ATTR_NAME = PreIndexedAttributeName.CLASS;

    {
        super.setPreIndexedAttribute(PRE_INDEXED_ATTR_NAME);
        init();
    }

    public ClassAttribute() {
    }

    /**
     * one or more class names separated by space.
     *
     * @param classNames
     * @since 3.0.2
     */
    public ClassAttribute(final String classNames) {
        super.addAllToAttributeValueSet(classNames);
    }

    /**
     * one or more class name separated by space or as an array of class names.
     *
     * @param classNames
     */
    public ClassAttribute(final String... classNames) {
        super.addAllToAttributeValueSet(classNames);
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
     * @param classNames one or more class names separated by space or as an array
     *                   of class names.
     * @since 1.0.0
     * @author WFF
     */
    public void addClassNames(final String... classNames) {
        super.addAllToAttributeValueSet(classNames);
    }

    /**
     * removed all current class names and adds the given class names.
     *
     * @param classNames one or more class names separated by space or an array of
     *                   class names.
     * @since 1.0.0
     * @author WFF
     */
    public void addNewClassNames(final String... classNames) {

        if (classNames != null) {
            super.replaceAllInAttributeValueSet(classNames);
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
        super.addAllToAttributeValueSet(classNames);
    }

    /**
     * removes all class names from the class attribute
     *
     * @param classNames the class names to remove
     * @since 1.0.0
     * @author WFF
     */
    public void removeAllClassNames(final Collection<String> classNames) {
        super.removeAllFromAttributeValueSet(classNames);
    }

    /**
     * removes all class names from the class attribute
     *
     * @since 12.0.0-beta.12
     */
    public void removeAllClassNames() {
        super.removeAllFromAttributeValueSet();
    }

    /**
     * removes all class names from the class attribute
     *
     * @param force true to forcefully remove all values and also to update client
     *              even if it is already empty
     * @since 12.0.0-beta.12
     */
    public void removeAllClassNames(final boolean force) {
        super.removeAllFromAttributeValueSet(force);
    }

    /**
     * removes the given class name
     *
     * @param className the class name to remove
     * @since 1.0.0
     * @author WFF
     */
    public void removeClassName(final String className) {
        super.removeFromAttributeValueSet(className);
    }

    /**
     * @return the value string of class names
     * @since 2.1.9
     * @author WFF
     */
    @Override
    public String getAttributeValue() {
        return super.getAttributeValue();
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
        return super.getCopyOfAttributeValueSet();
    }

    /**
     * sets the value for this attribute
     *
     *
     * @param value the value for the attribute.
     * @since 2.1.15
     * @author WFF
     */
    public void setValue(final String value) {
        super.setAttributeValue(value);
    }

    public void setValue(final boolean updateClient, final String value) {
        super.setAttributeValue(updateClient, value);
    }

    /**
     * @param value the value to set again even if the existing value is same at
     *              server side, the assigned value will be reflected in the UI.
     *              Sometimes we may modify the value only at client side (not
     *              server side), {@code setValue} will change only if the passed
     *              value is different from existing value at server side.
     * @since 12.0.0-beta.7
     */
    public void assignValue(final String value) {
        assignAttributeValue(value);
    }

}
