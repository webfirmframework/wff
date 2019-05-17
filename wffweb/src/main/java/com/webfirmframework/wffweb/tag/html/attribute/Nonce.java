/*
 * Copyright 2014-2019 Web Firm Framework
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
import com.webfirmframework.wffweb.tag.html.attribute.core.IndexedAttributeName;
import com.webfirmframework.wffweb.tag.html.identifier.ScriptAttributable;

/**
 *
 * nonce attribute for Script tag
 *
 * @since 3.0.1
 * @author WFF
 *
 */
public class Nonce extends AbstractAttribute implements ScriptAttributable {

    private static final long serialVersionUID = 1_0_0L;

    private static volatile int ATTR_NAME_INDEX = -1;

    static {
        final Integer index = IndexedAttributeName.INSTANCE
                .getIndexByAttributeName(AttributeNameConstants.NONCE);
        ATTR_NAME_INDEX = index != null ? index : -1;
    }

    {
        if (ATTR_NAME_INDEX == -1) {
            final Integer index = IndexedAttributeName.INSTANCE
                    .getIndexByAttributeName(AttributeNameConstants.NONCE);
            ATTR_NAME_INDEX = index != null ? index : -1;
        }
        super.setAttributeNameIndex(ATTR_NAME_INDEX);
        super.setAttributeName(AttributeNameConstants.NONCE);
        init();
    }

    /**
     *
     * @param value
     *                  the value for the attribute
     * @since 3.0.1
     * @author WFF
     */
    public Nonce(final String value) {
        setAttributeValue(value);
    }

    /**
     * sets the value for this attribute
     *
     * @param value
     *                  the value for the attribute.
     * @since 3.0.1
     * @author WFF
     */
    public void setValue(final String value) {
        super.setAttributeValue(value);
    }

    /**
     * sets the value for this attribute
     *
     * @param updateClient
     *                         true to update client browser page if it is
     *                         available. The default value is true but it will
     *                         be ignored if there is no client browser page.
     * @param value
     *                         the value for the attribute.
     * @since 3.0.1
     * @author WFF
     */
    public void setValue(final boolean updateClient, final String value) {
        super.setAttributeValue(updateClient, value);
    }

    /**
     * gets the value of this attribute
     *
     * @return the value of the attribute
     * @since 3.0.1
     * @author WFF
     */
    public String getValue() {
        return super.getAttributeValue();
    }

    /**
     * invokes only once per object
     *
     * @author WFF
     * @since 3.0.1
     */
    protected void init() {
        // to override and use this method
    }

}
