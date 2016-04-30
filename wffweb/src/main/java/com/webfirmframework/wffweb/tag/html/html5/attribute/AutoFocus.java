/*
 * Copyright 2014-2016 Web Firm Framework
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
package com.webfirmframework.wffweb.tag.html.html5.attribute;

import com.webfirmframework.wffweb.tag.html.attribute.AttributeNameConstants;
import com.webfirmframework.wffweb.tag.html.attribute.core.AbstractAttribute;
import com.webfirmframework.wffweb.tag.html.identifier.InputAttributable;

/**
 *
 * <code>autofocus</code> attribute for the element. <br/>
 *
 * This Boolean attribute lets you specify that a form control should have input
 * focus when the page loads, unless the user overrides it, for example by
 * typing in a different control. Only one form element in a document can have
 * the autofocus attribute, which is a Boolean. It cannot be applied if the type
 * attribute is set to hidden (that is, you cannot automatically set focus to a
 * hidden control). Note that the focusing of the control may occur before the
 * firing of the DOMContentLoaded event.
 *
 * @author WFF
 * @since 1.0.0
 */
public class AutoFocus extends AbstractAttribute implements InputAttributable {

    private static final long serialVersionUID = 1_0_0L;

    {
        super.setAttributeName(AttributeNameConstants.AUTOFOCUS);
        init();
    }

    public AutoFocus() {
        setAttributeValue(null);
    }

    public AutoFocus(final String value) {
        setAttributeValue(value);
    }

    public void setValue(final String value) {
        setAttributeValue(value);
    }

    public String getValue() {
        return getAttributeValue();
    }

    /**
     * invokes only once per object
     *
     * @since 1.0.0
     */
    protected void init() {
        // to override and use this method
    }

}
