/*
 * Copyright 2014-2015 Web Firm Framework
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
import com.webfirmframework.wffweb.tag.html.identifier.GlobalAttribute;

/**
 * @author WFF
 *
 */
public class Id extends AbstractAttribute implements GlobalAttribute {

    /**
     *
     */
    private static final long serialVersionUID = -3589199170250333206L;

    {
        super.setAttributeName(AttributeNameConstants.ID);
        init();
    }

    /**
     * sets with empty value as id=""
     *
     * @since 1.0.0
     * @author WFF
     */
    public Id() {
        setAttributeValue("");
    }

    /**
     * value for the id attribute.
     *
     * @param id
     * @since 1.0.0
     * @author WFF
     */
    public Id(final String value) {
        setAttributeValue(value);
    }

    /**
     * value for the id attribute.
     *
     * @param id
     * @since 1.0.0
     * @author WFF
     */
    public Id(final int value) {
        setAttributeValue(String.valueOf(value));
    }

    /**
     * value for the id attribute.
     *
     * @param id
     * @since 1.0.0
     * @author WFF
     */
    public Id(final float value) {
        setAttributeValue(String.valueOf(value));
    }

    /**
     * value for the id attribute.
     *
     * @param id
     * @since 1.0.0
     * @author WFF
     */
    public Id(final long value) {
        setAttributeValue(String.valueOf(value));
    }

    /**
     * value for the id attribute.
     *
     * @param id
     * @since 1.0.0
     * @author WFF
     */
    public Id(final double value) {
        setAttributeValue(String.valueOf(value));
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
     * value for the id attribute.
     *
     * @param id
     * @since 1.0.0
     * @author WFF
     */
    public void setValue(final String id) {
        if (id != null) {
            setAttributeValue(id);
        }
    }

    /**
     * value for the id attribute.
     *
     * @param id
     * @since 1.0.0
     * @author WFF
     */
    public void setValue(final int id) {
        setAttributeValue(String.valueOf(id));
    }

    /**
     * value for the id attribute.
     *
     * @param id
     * @since 1.0.0
     * @author WFF
     */
    public void setValue(final long id) {
        setAttributeValue(String.valueOf(id));
    }

    /**
     * value for the id attribute.
     *
     * @param id
     * @since 1.0.0
     * @author WFF
     */
    public void setValue(final float id) {
        setAttributeValue(String.valueOf(id));
    }

    /**
     * value for the id attribute.
     *
     * @param id
     * @since 1.0.0
     * @author WFF
     */
    public void setValue(final double id) {
        setAttributeValue(String.valueOf(id));
    }

    public String getValue() {
        return getAttributeValue();
    }
}
