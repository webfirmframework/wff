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
package com.webfirmframework.wffweb.tag.html.attribute;

import com.webfirmframework.wffweb.tag.html.attribute.core.AbstractAttribute;
import com.webfirmframework.wffweb.tag.html.identifier.InputAttributable;

/**
 *
 * <code>maxlength</code> attribute for the element.<br/>
 * <i>maxlength</i> attribute for <i>input</i> element :- <br/>
 * If the value of the type attribute is text, email, search, password, tel, or
 * url, this attribute specifies the maximum number of characters (in Unicode
 * code points) that the user can enter; for other control types, it is ignored.
 * It can exceed the value of the size attribute. If it is not specified, the
 * user can enter an unlimited number of characters. Specifying a negative
 * number results in the default behavior; that is, the user can enter an
 * unlimited number of characters. The constraint is evaluated only when the
 * value of the attribute has been changed.<br/>
 * <br/>
 *
 * @author WFF
 * @since 1.0.0
 */
public class MaxLength extends AbstractAttribute implements InputAttributable {

    private static final long serialVersionUID = 1_0_0L;

    private int value;

    {
        super.setAttributeName(AttributeNameConstants.MAXLENGTH);
        init();
    }

    /**
     *
     * @param value
     *            the value for the attribute
     * @since 1.0.0
     */
    public MaxLength(final int value) {
        this.value = value;
        setAttributeValue(String.valueOf(value));
    }

    /**
     * sets the value for this attribute
     *
     * @param value
     *            the value for the attribute.
     * @since 1.0.0
     */
    protected void setValue(final int value) {
        this.value = value;
        super.setAttributeValue(String.valueOf(value));
    }

    /**
     * gets the value of this attribute
     *
     * @return the value of the attribute
     * @since 1.0.0
     */
    public int getValue() {
        return value;
    }

    /**
     * invokes only once per object
     *
     * @since 1.0.0
     */
    protected void init() {
    }

}
