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
 */
package com.webfirmframework.wffweb.tag.html.html5.attribute.global;

import java.io.Serial;

import com.webfirmframework.wffweb.WffSecurityException;
import com.webfirmframework.wffweb.internal.constants.IndexedClassType;
import com.webfirmframework.wffweb.internal.security.object.SecurityObject;
import com.webfirmframework.wffweb.tag.html.attribute.AttributeNameConstants;
import com.webfirmframework.wffweb.tag.html.attribute.core.PreIndexedAttributeName;

public class DataWffId extends DataAttribute {

    @Serial
    private static final long serialVersionUID = 1_0_0L;

    // NB: if modifying its value it should also be updated in
    // InternalAttrNameConstants.DATA_WFF_ID
    private static final String ATTRIBUTE_NAME_EXTENSION = "wff-id";

    // must be kept final to provide atomic consistency across multiple threads
    public static final String ATTRIBUTE_NAME = AttributeNameConstants.DATA.concat(ATTRIBUTE_NAME_EXTENSION);

    // must be kept final to provide atomic consistency across multiple threads
    private final String attributeValue;

    /**
     * -1 for S and -2 for C
     */
    private final byte attributeValuePrefix;

    private final byte[] attributeValueBytes;

    private static final PreIndexedAttributeName PRE_INDEXED_ATTR_NAME = PreIndexedAttributeName.DATA_WFF_ID;

    /**
     * @param value the value for the attribute
     * @since 2.0.0
     * @author WFF
     */
    public DataWffId(final String value) {
        super(PRE_INDEXED_ATTR_NAME, value);
        attributeValuePrefix = 0;
        attributeValueBytes = null;
        attributeValue = value;
    }

    /**
     * Note: Only for internal use.
     *
     * @param value                the value for the attribute
     * @param attributeValuePrefix the value for the attribute.
     * @param attributeValueBytes  the bytes of integer to represent id.
     * @param accessObject         the access object to call this method.
     * @since 12.0.3
     * @author WFF
     */
    public DataWffId(final String value, final byte attributeValuePrefix, final byte[] attributeValueBytes,
            @SuppressWarnings("exports") final SecurityObject accessObject) {
        super(PRE_INDEXED_ATTR_NAME, value);
        this.attributeValuePrefix = attributeValuePrefix;
        this.attributeValueBytes = attributeValueBytes;
        attributeValue = value;
        if (accessObject == null
                || !IndexedClassType.ABSTRACT_HTML5_SHARED_OBJECT.equals(accessObject.forClassType())) {
            throw new WffSecurityException("Not allowed to call this constructor. This class is for internal use.");
        }
    }

    @Override
    public void setValue(final String value) {
        throw new WffSecurityException("Not allowed to change its value");
    }

    /*
     * (non-Javadoc)
     *
     * @see com.webfirmframework.wffweb.tag.html.attribute.core.AbstractAttribute#
     * getAttributeName()
     */
    @Override
    public String getAttributeName() {
        // must override and return constant (final variable)
        // to provide atomic across multiple threads
        return ATTRIBUTE_NAME;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.webfirmframework.wffweb.tag.html.attribute.core.AbstractAttribute#
     * getAttributeValue()
     */
    @Override
    public String getAttributeValue() {
        // must override and return constant (final variable)
        // to provide atomic across multiple threads
        return attributeValue;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.webfirmframework.wffweb.tag.html.html5.attribute.global.DataAttribute
     * #getValue()
     */
    @Override
    public String getValue() {
        // must override and return constant (final variable)
        // to provide atomic across multiple threads
        return attributeValue;
    }

    @Override
    protected void setAttributeValue(final String attributeValue) {
        throw new WffSecurityException("Not allowed to change value for data-wff-id");
    }

    @Override
    protected void setAttributeName(final String attributeName) {
        throw new WffSecurityException("Not allowed to change value for data-wff-id");
    }

    @Override
    protected void setAttributeValue(final boolean updateClient, final String attributeValue) {
        throw new WffSecurityException("Not allowed to change value for data-wff-id");
    }

    // for testing
    static int getAttrNameIndex() {
        return PRE_INDEXED_ATTR_NAME.index();
    }

    /**
     * Note: Only for internal use.
     *
     * @return the byte represented for value prefix.
     * @since 12.0.3
     */
    public final byte attributeValuePrefix() {
        return attributeValuePrefix;
    }

    /**
     * Only for internal use.
     *
     * @param accessObject the access object to call this method.
     * @return the value bytes
     */
    public final byte[] attributeValueBytes(@SuppressWarnings("exports") final SecurityObject accessObject) {
        if (accessObject == null || !IndexedClassType.ABSTRACT_ATTRIBUTE.equals(accessObject.forClassType())) {
            throw new WffSecurityException("Not allowed to consume this method. This method is for internal use.");
        }
        return attributeValueBytes;
    }
}
