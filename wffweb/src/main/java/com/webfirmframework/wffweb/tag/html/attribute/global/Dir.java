/*
 * Copyright 2014-2017 Web Firm Framework
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

import com.webfirmframework.wffweb.tag.html.attribute.AttributeNameConstants;
import com.webfirmframework.wffweb.tag.html.attribute.core.AbstractAttribute;
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

    {
        super.setAttributeName(AttributeNameConstants.DIR);
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
     * @param value
     *            the value to set
     * @author WFF
     * @since 1.0.0
     */
    public void setValue(final String value) {
        setAttributeValue(value);
    }

}
