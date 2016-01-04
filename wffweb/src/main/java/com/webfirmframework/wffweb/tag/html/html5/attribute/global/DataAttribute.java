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

import com.webfirmframework.wffweb.NullValueException;
import com.webfirmframework.wffweb.tag.html.attribute.AttributeNameConstants;
import com.webfirmframework.wffweb.tag.html.attribute.core.AbstractAttribute;
import com.webfirmframework.wffweb.tag.html.identifier.GlobalAttribute;

/**
 * @author WFF
 *
 */
public class DataAttribute extends AbstractAttribute implements GlobalAttribute {

    private static final long serialVersionUID = 1083424538256446122L;

    {
        init();
    }

    public DataAttribute(final String attributeNameExension) {
        if (attributeNameExension == null) {
            throw new NullValueException(
                    "attributeNameExension can not be null");
        }
        super.setAttributeName(AttributeNameConstants.DATA
                .concat(attributeNameExension));
        setAttributeValue(null);
    }

    public DataAttribute(final String attributeNameExension, final String value) {
        if (attributeNameExension == null) {
            throw new NullValueException(
                    "attributeNameExension can not be null");
        }
        super.setAttributeName(AttributeNameConstants.DATA
                .concat(attributeNameExension));
        setAttributeValue(value);
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
