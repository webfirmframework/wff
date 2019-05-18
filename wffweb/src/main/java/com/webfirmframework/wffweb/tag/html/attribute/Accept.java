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
import com.webfirmframework.wffweb.tag.html.attribute.core.PreIndexedAttributeName;
import com.webfirmframework.wffweb.tag.html.identifier.InputAttributable;

/**
 *
 * <code>accept</code> attribute for the element.
 *
 * <pre>
 *
 * If the value of the type attribute is file, this attribute indicates the types of files that the server accepts; otherwise it is ignored. The value must be a comma-separated list of unique content type specifiers:
 * A file extension starting with the STOP character (U+002E). (E.g.: ".jpg,.png,.doc")
 * A valid MIME type with no extensions
 * audio/* representing sound files <i>(HTML5)</i>
 * video/* representing video files <i>(HTML5)</i>
 * image/* representing image files <i>(HTML5)</i>
 *
 * </pre>
 *
 * @author WFF
 * @since 1.0.0
 */
public class Accept extends AbstractAttribute implements InputAttributable {

    private static final long serialVersionUID = 1_0_0L;

    private static final int ATTR_NAME_INDEX;

    static {
        ATTR_NAME_INDEX = PreIndexedAttributeName
                .getIndex(PreIndexedAttributeName.ACCEPT);

    }

    {

        super.setAttributeNameAndIndex(AttributeNameConstants.ACCEPT,
                ATTR_NAME_INDEX);
        init();
    }

    /**
     *
     * @param value
     *                  the value for the attribute
     * @since 1.0.0
     * @author WFF
     */
    public Accept(final String value) {
        setAttributeValue(value);
    }

    /**
     * sets the value for this attribute
     *
     * @param value
     *                  the value for the attribute.
     * @since 1.0.0
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
     * @since 2.1.15
     * @author WFF
     */
    public void setValue(final boolean updateClient, final String value) {
        super.setAttributeValue(updateClient, value);
    }

    /**
     * gets the value of this attribute
     *
     * @return the value of the attribute
     * @since 1.0.0
     * @author WFF
     */
    public String getValue() {
        return super.getAttributeValue();
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

}
