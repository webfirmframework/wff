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

import java.io.Serial;

import com.webfirmframework.wffweb.tag.html.attribute.core.AbstractAttribute;
import com.webfirmframework.wffweb.tag.html.attribute.core.PreIndexedAttributeName;
import com.webfirmframework.wffweb.tag.html.identifier.TdAttributable;
import com.webfirmframework.wffweb.tag.html.identifier.ThAttributable;

/**
 * <pre>
 * &lt;td rowspan=&quot;number&quot;&gt;
 * The rowspan attribute specifies the number of rows a cell should span.
 *
 * </pre>
 *
 * @author WFF
 *
 */
public class RowSpan extends AbstractAttribute implements ThAttributable, TdAttributable {

    @Serial
    private static final long serialVersionUID = 1_0_0L;

    private int value;

    private static final PreIndexedAttributeName PRE_INDEXED_ATTR_NAME = PreIndexedAttributeName.ROWSPAN;

    {
        super.setPreIndexedAttribute(PRE_INDEXED_ATTR_NAME);
        init();
    }

    @SuppressWarnings("unused")
    private RowSpan() {
    }

    /**
     * @param value the the number of rows to span
     * @since 1.1.3
     * @author WFF
     */
    public RowSpan(final String value) {
        this.value = Integer.parseInt(value);
        setAttributeValue(value);
    }

    /**
     * @param value the the number of rows to span
     * @since 1.1.3
     * @author WFF
     */
    public RowSpan(final int value) {
        setAttributeValue(String.valueOf(value));
        this.value = value;
    }

    /**
     * invokes only once per object
     *
     * @author WFF
     * @since 1.1.3
     */
    protected void init() {
        // NOP
        // to override and use this method
    }

    /**
     * @return the the number of rows spanned
     * @author WFF
     * @since 1.1.3
     */
    public int getValue() {
        return value;
    }

    /**
     * @param value the the number of rows to span
     * @author WFF
     * @since 1.1.3
     */
    public void setValue(final int value) {
        setAttributeValue(String.valueOf(value));
        this.value = value;
    }

}
