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

import com.webfirmframework.wffweb.tag.html.attribute.core.AbstractAttribute;
import com.webfirmframework.wffweb.tag.html.attribute.core.PreIndexedAttributeName;
import com.webfirmframework.wffweb.tag.html.identifier.ButtonAttributable;
import com.webfirmframework.wffweb.tag.html.identifier.InputAttributable;

/**
 *
 * @author WFF
 * @since 12.0.14
 *
 */
public class PopoverTargetAction extends AbstractAttribute implements InputAttributable, ButtonAttributable {

    /**
     *
     */
    private static final long serialVersionUID = 1_0_0L;

    public static final String HIDE = "hide";

    public static final String SHOW = "show";

    public static final String TOGGLE = "toggle";

    private static final PreIndexedAttributeName PRE_INDEXED_ATTR_NAME = PreIndexedAttributeName.POPOVERTARGETACTION;

    {
        super.setPreIndexedAttribute(PRE_INDEXED_ATTR_NAME);
        init();
    }

    public PopoverTargetAction() {
        super.setAttributeValue(null);
    }

    /**
     * @param value eg: hide
     */
    public PopoverTargetAction(final String value) {
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
     * @param value eg: hide
     * @since 12.0.14
     */
    public void setValue(final String value) {
        super.setAttributeValue(value);
    }

    /**
     * @return the value, eg: hide
     * @since 12.0.14
     */
    public String getValue() {
        return super.getAttributeValue();
    }

}
