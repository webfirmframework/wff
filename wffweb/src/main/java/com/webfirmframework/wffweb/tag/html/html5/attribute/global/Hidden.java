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
 * @author WFF
 */
package com.webfirmframework.wffweb.tag.html.html5.attribute.global;

import com.webfirmframework.wffweb.InvalidValueException;
import com.webfirmframework.wffweb.tag.html.attribute.AttributeNameConstants;
import com.webfirmframework.wffweb.tag.html.attribute.core.AbstractAttribute;
import com.webfirmframework.wffweb.tag.html.attribute.core.PreIndexedAttributeName;
import com.webfirmframework.wffweb.tag.html.identifier.BooleanAttribute;
import com.webfirmframework.wffweb.tag.html.identifier.GlobalAttributable;

/**
 * {@code <element hidden> }
 *
 * <pre>
 * The hidden attribute is a boolean attribute.
 *
 * When present, it specifies that an element is not yet, or is no longer, relevant.
 *
 * Browsers should not display elements that have the hidden attribute specified.
 *
 * The hidden attribute can also be used to keep a user from seeing an element until some other condition has been met (like selecting a checkbox, etc.). Then, a JavaScript could remove the hidden attribute, and make the element visible.
 * </pre>
 *
 * @author WFF
 *
 */
public class Hidden extends AbstractAttribute implements GlobalAttributable, BooleanAttribute {

    private static final long serialVersionUID = 1_0_1L;

    private static final PreIndexedAttributeName PRE_INDEXED_ATTR_NAME;

    static {
        PRE_INDEXED_ATTR_NAME = (PreIndexedAttributeName.HIDDEN);

    }

    {

        super.setPreIndexedAttribute(PRE_INDEXED_ATTR_NAME);
        init();
    }

    /**
     * sets the default value as <code>hidden</code> (since 2.1.5). If value is not
     * required then use <code>new Hidden(null)</code>.
     */
    public Hidden() {
        setAttributeValue(AttributeNameConstants.HIDDEN);
    }

    /**
     *
     *
     * @param value the value should be hidden, true, empty string or null
     * @author WFF
     * @since 1.1.4
     */
    public Hidden(final String value) {
        if (AttributeNameConstants.HIDDEN.equals(value) || value == null || value.isBlank()) {
            setAttributeValue(value);
        } else if ("true".equals(value) || "false".equals(value)) {
            setAttributeValue(Boolean.parseBoolean(value) ? AttributeNameConstants.HIDDEN : null);
        } else {
            throw new InvalidValueException("the value should be hidden, true, empty string or null");
        }
    }

    public Hidden(final boolean hidden) {
        setAttributeValue(hidden ? AttributeNameConstants.HIDDEN : "");
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

}
