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
import com.webfirmframework.wffweb.tag.html.html5.identifier.AudioAttributable;
import com.webfirmframework.wffweb.tag.html.identifier.InputAttributable;

/**
 * <code>required</code> attribute for the element.<br/>
 *
 * <i>required</i> attribute for <i>input</i> element :- <br/>
 * This attribute specifies that the user must fill in a value before submitting
 * a form. It cannot be used when the type attribute is hidden, image, or a
 * button type (submit, reset, or button). The :optional and :required CSS
 * pseudo-classes will be applied to the field as appropriate.
 *
 * @author WFF
 * @since 1.0.0
 *
 */
public class Required extends AbstractAttribute
        implements AudioAttributable, InputAttributable {

    private static final long serialVersionUID = 1_0_0L;

    {
        super.setAttributeName(AttributeNameConstants.REQUIRED);
        init();
    }

    public Required() {
        setAttributeValue(null);
    }

    public Required(final String value) {
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
    }
}
