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
package com.webfirmframework.wffweb.tag.html.attribute.core;

import java.io.Serial;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;

import com.webfirmframework.wffweb.util.StringUtil;

/**
 * @author WFF
 * @since 2.1.15
 */
public abstract class AbstractValueSetAttribute extends AbstractAttribute {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * sets the value for this attribute
     *
     * @param updateClient true to update client browser page if it is available.
     *                     The default value is true but it will be ignored if there
     *                     is no client browser page.
     * @param value        the value for the attribute.
     * @since 2.1.15
     * @author WFF
     */
    @Override
    protected void setAttributeValue(final boolean updateClient, final String value) {
        if (value != null) {
            final Collection<String> allValues = extractValues(value);
            if (allValues.isEmpty()) {
                super.removeAllFromAttributeValueSet();
            } else {
                super.replaceAllInAttributeValueSet(updateClient, allValues);
            }
        }
    }

    /*
     * splits by space
     */
    private static Collection<String> extractValues(final String value) {
        final String[] inputValues = StringUtil.splitBySpace(value);
        final Collection<String> allValues = new ArrayDeque<>();
        for (final String each : inputValues) {
            // As per Collections.addAll's doc it is faster than
            // allValues.addAll(Arrays.asList(values));
            final String striped = StringUtil.strip(each);
            if (!striped.isBlank()) {
                Collections.addAll(allValues, striped);
            }
        }
        return allValues;
    }

    /**
     * sets the value for this attribute
     *
     *
     * @param value the value for the attribute.
     * @since 2.1.15
     * @author WFF
     */
    @Override
    protected void setAttributeValue(final String value) {
        if (value != null) {
            replaceAllInAttributeValueSet(value);
        }
    }

    /**
     * @param value the value to set again even if the existing value is same at
     *              server side, the assigned value will be reflected in the UI.
     *              Sometimes we may modify the value only at client side (not
     *              server side), {@code setValue} will change only if the passed
     *              value is different from existing value at server side.
     * @since 12.0.0-beta.12
     */
    @Override
    protected void assignAttributeValue(final String value) {
        if (value != null) {
            replaceAllInAttributeValueSet(true, true, value);
        }
    }

    /**
     * @return the copy of attributeValueSet in thread-safe way
     * @since 12.0.0-beta.12
     */
    protected Set<String> getCopyOfAttributeValueSet() {
        final Collection<Lock> readLocks = lockAndGetReadLocksWithAttrLock();
        try {
            final Set<String> attributeValueSet = super.getAttributeValueSetNoInit();
            if (attributeValueSet != null) {
                return new LinkedHashSet<>(attributeValueSet);
            }
        } finally {
            for (final Lock lock : readLocks) {
                lock.unlock();
            }
        }
        return Set.of();
    }

    /**
     * Checks if the value is a part of this attribute value.
     *
     * @param value the value to be checked
     * @return true if the given value is contained in the value string of this
     *         attribute.
     * @since 2.1.15
     * @author WFF
     */
    public boolean contains(final String value) {
        final Collection<Lock> readLocks = lockAndGetReadLocksWithAttrLock();
        try {
            final Set<String> attributeValueSet = super.getAttributeValueSetNoInit();
            if (attributeValueSet != null) {
                return attributeValueSet.contains(value);
            }
        } finally {
            for (final Lock lock : readLocks) {
                lock.unlock();
            }
        }
        return false;
    }

    /**
     * Checks if the values are part of this attribute value.
     *
     * @param values the values to be checked
     * @return true if the given values are contained in the value string of this
     *         attribute.
     * @since 2.1.15
     * @author WFF
     */
    public boolean containsAll(final Collection<String> values) {
        final Collection<Lock> readLocks = lockAndGetReadLocksWithAttrLock();
        try {
            final Set<String> attributeValueSet = super.getAttributeValueSetNoInit();
            if (attributeValueSet != null) {
                return attributeValueSet.containsAll(values);
            }
        } finally {
            for (final Lock lock : readLocks) {
                lock.unlock();
            }
        }
        return false;
    }

    /**
     * @return true if empty otherwise false
     * @since 12.0.0-beta.12
     */
    public boolean isEmpty() {
        final Collection<Lock> readLocks = lockAndGetReadLocksWithAttrLock();
        try {
            final Set<String> attributeValueSet = super.getAttributeValueSetNoInit();
            if (attributeValueSet != null) {
                return attributeValueSet.isEmpty();
            }
        } finally {
            for (final Lock lock : readLocks) {
                lock.unlock();
            }
        }
        return false;
    }

    /**
     * @return the size
     * @since 12.0.0
     */
    public int size() {
        final Collection<Lock> readLocks = lockAndGetReadLocksWithAttrLock();
        try {
            final Set<String> attributeValueSet = super.getAttributeValueSetNoInit();
            if (attributeValueSet != null) {
                return attributeValueSet.size();
            }
        } finally {
            for (final Lock lock : readLocks) {
                lock.unlock();
            }
        }
        return 0;
    }

    @Override
    public String getAttributeValue() {
        final Collection<Lock> readLocks = lockAndGetReadLocksWithAttrLock();
        try {
            final Set<String> attributeValueSet = super.getAttributeValueSetNoInit();
            if (attributeValueSet != null) {
                return StringUtil.join(' ', attributeValueSet);
            }

        } finally {
            for (final Lock lock : readLocks) {
                lock.unlock();
            }
        }
        return "";
    }

    /**
     * @param attrValues
     * @since 2.1.15
     * @author WFF
     */
    protected void addAllToAttributeValueSet(final String... attrValues) {
        if (attrValues != null) {

            final Collection<String> allValues = new ArrayDeque<>();
            for (final String attrValue : attrValues) {
                String trimmmedValue = null;
                if (attrValue != null && !(trimmmedValue = StringUtil.strip(attrValue)).isEmpty()) {
                    // As per Collections.addAll's doc it is faster than
                    // allValues.addAll(Arrays.asList(values));
                    Collections.addAll(allValues, StringUtil.splitBySpace(trimmmedValue));
                }
            }

            super.addAllToAttributeValueSet(allValues);
        }
    }

    /**
     * Removes all values from the attributeValueSet and adds the given attribute
     * values.
     *
     * @param attrValues
     * @since 3.0.1
     */
    protected void replaceAllInAttributeValueSet(final String... attrValues) {
        replaceAllInAttributeValueSet(true, attrValues);
    }

    /**
     * Removes all values from the attributeValueSet and adds the given attribute
     * values.
     *
     * @param updateClient
     * @param attrValues
     * @since 3.0.1
     */
    protected void replaceAllInAttributeValueSet(final boolean updateClient, final String... attrValues) {
        replaceAllInAttributeValueSet(false, updateClient, attrValues);
    }

    /**
     * Removes all values from the attributeValueSet and adds the given attribute
     * values.
     *
     * @param force
     * @param updateClient
     * @param attrValues
     * @since 12.0.0-beta.12
     */
    private void replaceAllInAttributeValueSet(final boolean force, final boolean updateClient,
            final String... attrValues) {
        if (attrValues != null) {
            final Collection<String> allValues = new ArrayDeque<>();
            for (final String attrValue : attrValues) {
                if (attrValue != null && !attrValue.isBlank()) {
                    final Collection<String> values = extractValues(attrValue);
                    if (!values.isEmpty()) {
                        allValues.addAll(values);
                    }
                }
            }
            super.replaceAllInAttributeValueSet(force, updateClient, allValues);
        }
    }

}
