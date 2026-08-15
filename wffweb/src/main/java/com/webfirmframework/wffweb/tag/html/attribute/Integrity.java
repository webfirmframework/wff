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

import com.webfirmframework.wffweb.tag.html.attribute.core.AbstractValueSetAttribute;
import com.webfirmframework.wffweb.tag.html.attribute.core.PreIndexedAttributeName;
import com.webfirmframework.wffweb.tag.html.identifier.LinkAttributable;
import com.webfirmframework.wffweb.tag.html.identifier.ScriptAttributable;

import java.io.Serial;
import java.util.Collection;
import java.util.Set;

/**
 *
 * @author WFF
 * @since 12.0.14
 *
 */
public class Integrity extends AbstractValueSetAttribute implements ScriptAttributable, LinkAttributable {

    @Serial
    private static final long serialVersionUID = 1_0_0L;


    private static final PreIndexedAttributeName PRE_INDEXED_ATTR_NAME = PreIndexedAttributeName.INTEGRITY;

    {

        // This class may to be re-implemented just like ClassAttribute because
        // this class is also taking multiple values separated by space just as
        // in ClassAttribute so many features can be reused from ClassAttribute.
        super.setPreIndexedAttribute(PRE_INDEXED_ATTR_NAME);
        init();
    }

    /**
     *
     * @param value the value for the attribute. If there are multiple values it can
     *              be separated by space.
     * @since 12.0.14
     * @author WFF
     */
    public Integrity(final String value) {
        super.addAllToAttributeValueSet(value);
    }

    /**
     *
     * @param values the values for the attribute. If there are multiple values it
     *               can be separated by space.
     * @since 12.0.14
     * @author WFF
     */
    public Integrity(final String... values) {
        super.addAllToAttributeValueSet(values);
    }

    /**
     * sets the value for this attribute
     *
     * @param value the value for the attribute.
     * @since 12.0.14
     * @author WFF
     */
    public void setValue(final String value) {
        super.setAttributeValue(value);
    }

    /**
     * gets the value of this attribute
     *
     * @return the value of the attribute
     * @since 12.0.14
     * @author WFF
     */
    public String getValue() {
        return super.getAttributeValue();
    }

    /**
     * @return a new copy of set of values
     * @since 12.0.14
     * @author WFF
     */
    public Set<String> getValueSet() {
        return super.getCopyOfAttributeValueSet();
    }

    /**
     * removes the value
     *
     * @param value
     * @since 12.0.14
     * @author WFF
     */
    public void removeValue(final String value) {
        super.removeFromAttributeValueSet(value);
    }

    /**
     * removes the values
     *
     * @param values
     * @since 12.0.14
     * @author WFF
     */
    public void removeValues(final Collection<String> values) {
        super.removeAllFromAttributeValueSet(values);
    }

    /**
     * removes the all values
     *
     * @since 12.0.14
     */
    public void removeAllValues() {
        super.removeAllFromAttributeValueSet();
    }

    /**
     * Removes all values
     *
     * @param force true to forcefully remove all values and also to update client
     *              even if it is already empty
     * @since 12.0.14
     */
    public void removeAllValues(final boolean force) {
        super.removeAllFromAttributeValueSet(force);
    }

    /**
     * adds the values to the last
     *
     * @param values
     * @since 12.0.14
     * @author WFF
     */
    public void addValues(final Collection<String> values) {
        super.addAllToAttributeValueSet(values);
    }

    /**
     * adds the value to the last
     *
     * @param value
     * @since 12.0.14
     * @author WFF
     */
    public void addValue(final String value) {
        super.addToAttributeValueSet(value);
    }

    /**
     * invokes only once per object
     *
     * @author WFF
     * @since 1.0.0
     */
    protected void init() {
        // to override and use this method
    }

    public void setValue(final boolean updateClient, final String value) {
        super.setAttributeValue(updateClient, value);
    }

}
