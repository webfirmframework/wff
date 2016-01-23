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
package com.webfirmframework.wffweb.tag.html.html5.attribute.global;

import com.webfirmframework.wffweb.tag.html.attribute.AttributeNameConstants;
import com.webfirmframework.wffweb.tag.html.attribute.core.AbstractAttribute;
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
public class Hidden extends AbstractAttribute implements GlobalAttributable {

    private static final long serialVersionUID = 1_0_0L;

    private Boolean hidden;

    {
        super.setAttributeName(AttributeNameConstants.HIDDEN);
        init();
    }

    public Hidden() {
        setAttributeValue(null);
    }

    public Hidden(final Boolean hidden) {
        if (hidden == null) {
            setAttributeValue(null);
        } else {
            setAttributeValue(String.valueOf(hidden));
        }
        this.hidden = hidden;
    }

    /**
     * invokes only once per object
     *
     * @author WFF
     * @since 1.0.0
     */
    protected void init() {
    }

    /**
     * @return the hidden
     * @author WFF
     * @since 1.0.0
     */
    public boolean isHidden() {
        return hidden == null || hidden.booleanValue() ? true : false;
    }

    /**
     * @param hidden
     *            the hidden to set. {@code null} will remove the value.
     * @author WFF
     * @since 1.0.0
     */
    public void setHidden(final Boolean hidden) {
        if (hidden == null) {
            setAttributeValue(null);
        } else {
            setAttributeValue(String.valueOf(hidden));
        }
        this.hidden = hidden;
    }

}
