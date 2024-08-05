/*
 * Copyright 2014-2024 Web Firm Framework
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

import com.webfirmframework.wffweb.tag.html.attribute.core.AbstractAttribute;
import com.webfirmframework.wffweb.tag.html.attribute.core.PreIndexedAttributeName;
import com.webfirmframework.wffweb.tag.html.identifier.GlobalAttributable;

/**
 * {@code <element dir="ltr|rtl|auto"> }
 *
 * <pre>
 *
 * The dir attribute specifies the text direction of the element's content.
 * </pre>
 *
 * @author WFF
 *
 */
public class Dir extends AbstractAttribute implements GlobalAttributable {

    private static final long serialVersionUID = 1_0_0L;

    public static final String AUTO = "auto";
    public static final String LTR = "ltr";
    public static final String RTL = "rtl";

    private static final PreIndexedAttributeName PRE_INDEXED_ATTR_NAME = PreIndexedAttributeName.DIR;

    {
        super.setPreIndexedAttribute(PRE_INDEXED_ATTR_NAME);
        init();
    }

    /**
     * the default value <code>ltr</code> will be set.
     *
     * @author WFF
     * @since 1.0.0
     */
    public Dir() {
        setAttributeValue(LTR);
    }

    public Dir(final String value) {
        setAttributeValue(value);
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
     * @return the value
     * @author WFF
     * @since 1.0.0
     */
    public String getValue() {
        return getAttributeValue();
    }

    /**
     * @param value the value to set
     * @author WFF
     * @since 1.0.0
     */
    public void setValue(final String value) {
        setAttributeValue(value);
    }

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
        super.assignAttributeValue(value);
    }

}
