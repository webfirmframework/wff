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
package com.webfirmframework.wffweb.tag.html.attribute.global;

import com.webfirmframework.wffweb.tag.html.attribute.core.AbstractAttribute;
import com.webfirmframework.wffweb.tag.html.attribute.core.PreIndexedAttributeName;
import com.webfirmframework.wffweb.tag.html.identifier.GlobalAttributable;

/**
 *
 * @author WFF
 * @since 12.0.14
 *
 */
public class Popover extends AbstractAttribute implements GlobalAttributable {

    /**
     *
     */
    private static final long serialVersionUID = 1_0_0L;

    public static final String AUTO = "auto";

    public static final String HINT = "hint";

    public static final String MANUAL = "manual";

    private static final PreIndexedAttributeName PRE_INDEXED_ATTR_NAME = PreIndexedAttributeName.POPOVER;

    {
        super.setPreIndexedAttribute(PRE_INDEXED_ATTR_NAME);
        init();
    }

    public Popover() {
        super.setAttributeValue(null);
    }

    /**
     * @param value eg: hint
     */
    public Popover(final String value) {
        super.setAttributeValue(value);
    }

    /**
     * invokes only once per object
     *
     * @since 12.0.14
     */
    protected void init() {
        // to override and use this method
    }

    /**
     * @param value eg: hint
     * @since 12.0.14
     */
    public void setValue(final String value) {
        super.setAttributeValue(value);
    }

    /**
     * @return the value, eg: hint
     * @since 12.0.14
     */
    public String getValue() {
        return super.getAttributeValue();
    }

}
