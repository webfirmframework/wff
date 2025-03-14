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
package com.webfirmframework.wffweb.tag.html.attribute;

import java.io.Serial;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import com.webfirmframework.wffweb.tag.html.attribute.core.AbstractValueSetAttribute;
import com.webfirmframework.wffweb.tag.html.attribute.core.PreIndexedAttributeName;
import com.webfirmframework.wffweb.tag.html.identifier.TdAttributable;
import com.webfirmframework.wffweb.tag.html.identifier.ThAttributable;

/**
 * @author WFF
 *
 */
public class Headers extends AbstractValueSetAttribute implements ThAttributable, TdAttributable {

    @Serial
    private static final long serialVersionUID = 1_0_0L;

    private static final PreIndexedAttributeName PRE_INDEXED_ATTR_NAME = PreIndexedAttributeName.HEADERS;

    {
        super.setPreIndexedAttribute(PRE_INDEXED_ATTR_NAME);
        init();
    }

    public Headers() {
    }

    /**
     * one or more header ids separated by space.
     *
     * @param headerIds
     * @since 3.0.2
     */
    public Headers(final String headerIds) {
        if (headerIds != null) {
            super.addAllToAttributeValueSet(headerIds);
        }
    }

    /**
     * one or more header ids separated by space or as an array of header ids.
     *
     * @param headerIds
     */
    public Headers(final String... headerIds) {
        if (headerIds != null) {
            super.addAllToAttributeValueSet(headerIds);
        }
    }

    /**
     * invokes only once per object
     *
     * @author WFF
     * @since 1.1.3
     */
    protected void init() {
        // to override and use this method
    }

    /**
     * adds the given header ids.
     *
     * @param headerIds one or more header ids separated by space or as an array of
     *                  header ids.
     * @since 1.1.3
     * @author WFF
     */
    public void addHeaderIds(final String... headerIds) {
        if (headerIds != null) {
            super.addAllToAttributeValueSet(headerIds);
        }
    }

    /**
     * removed all current header ids and adds the given header ids.
     *
     * @param headerIds one or more header ids separated by space or an array of
     *                  header ids.
     * @since 1.1.3
     * @author WFF
     */
    public void addNewHeaderIds(final String... headerIds) {
        if (headerIds != null) {
            super.replaceAllInAttributeValueSet(headerIds);
        }
    }

    /**
     * adds the given header ids in the class attribute
     *
     * @param headerIds
     * @since 1.1.3
     * @author WFF
     */
    public void addAllHeaderIds(final Collection<String> headerIds) {
        super.addAllToAttributeValueSet(headerIds);
    }

    /**
     * removes all header ids from the class attribute
     *
     * @param headerIds the header ids to remove
     * @since 1.1.3
     * @author WFF
     */
    public void removeAllHeaderIds(final Collection<String> headerIds) {
        super.removeAllFromAttributeValueSet(headerIds);
    }

    /**
     * removes the given header id
     *
     * @param headerId the header id to remove
     * @since 3.0.1
     * @author WFF
     */
    public void removeHeaderId(final String headerId) {
        super.removeFromAttributeValueSet(headerId);
    }

    /**
     * removes the given header ids
     *
     * @param headerIds the header ids to remove
     * @since 3.0.1
     * @author WFF
     */
    public void removeHeaderIds(final Collection<String> headerIds) {
        super.removeAllFromAttributeValueSet(headerIds);
    }

    /**
     * removes the given header ids
     *
     * @param headerIds the header ids to remove
     * @since 3.0.1
     * @author WFF
     */
    public void removeHeaderIds(final String... headerIds) {
        super.removeAllFromAttributeValueSet(Arrays.asList(headerIds));
    }

    /**
     * Removes all header ids
     *
     * @since 3.0.1
     */
    public void removeAllHeaderIds() {
        super.removeAllFromAttributeValueSet();
    }

    /**
     * Removes all header ids
     *
     * @param force true to forcefully remove all values and also to update client
     *              even if it is already empty
     * @since 12.0.0-beta.12
     */
    public void removeAllHeaderIds(final boolean force) {
        super.removeAllFromAttributeValueSet(force);
    }

    /**
     * sets the value for this attribute
     *
     * @param value the value for the attribute.
     * @since 12.0.0-beta.12
     */
    public void setValue(final String value) {
        super.setAttributeValue(value);
    }

    /**
     * sets the value for this attribute
     *
     * @param updateClient   true to update client browser page if it is available.
     *                       The default value is true but it will be ignored if
     *                       there is no client browser page.
     * @param attributeValue the value for the attribute.
     * @since 12.0.0-beta.12
     * @author WFF
     */
    public void setValue(final boolean updateClient, final String attributeValue) {
        super.setAttributeValue(updateClient, attributeValue);
    }

    /**
     * @param value the value to set again even if the existing value is same at
     *              server side, the assigned value will be reflected in the UI.
     *              Sometimes we may modify the value only at client side (not
     *              server side), {@code setValue} will change only if the passed
     *              value is different from existing value at server side.
     * @since 12.0.0-beta.12
     */
    public void assignValue(final String value) {
        assignAttributeValue(value);
    }

    /**
     * @return the header ids
     * @since 12.0.0-beta.12
     */
    public Set<String> getHeaderIds() {
        return super.getCopyOfAttributeValueSet();
    }

}
