/*
 * Copyright 2014-2020 Web Firm Framework
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
import com.webfirmframework.wffweb.tag.html.identifier.TableAttributable;

/**
 * <pre>
 * &lt;table cellspacing=&quot;pixels&quot;&gt;
 * The cellspacing attribute specifies the space, in pixels, between cells.
 *
 * NB:- The cellspacing attribute of &lt;table&gt; is not supported in HTML5. Use CSS instead.
 * </pre>
 *
 * @author WFF
 * @since 1.1.5
 */
public class CellSpacing extends AbstractAttribute implements TableAttributable {

    private static final long serialVersionUID = 1_0_0L;

    private int value;

    private static final PreIndexedAttributeName PRE_INDEXED_ATTR_NAME;

    static {
        PRE_INDEXED_ATTR_NAME = (PreIndexedAttributeName.CELLSPACING);

    }

    {

        super.setPreIndexedAttribute(PRE_INDEXED_ATTR_NAME);
        init();
    }

    @SuppressWarnings("unused")
    private CellSpacing() {
    }

    /**
     * @param value the the number of pixels
     * @since 1.1.5
     * @author WFF
     */
    public CellSpacing(final String value) {
        this.value = Integer.parseInt(value);
        setAttributeValue(value);
    }

    /**
     * @param value the the number of pixels
     * @since 1.1.5
     * @author WFF
     */
    public CellSpacing(final int value) {
        setAttributeValue(String.valueOf(value));
        this.value = value;
    }

    /**
     * invokes only once per object
     *
     * @author WFF
     * @since 1.1.5
     */
    protected void init() {
        // NOP
        // to override and use this method
    }

    /**
     * @return the the number of pixels
     * @author WFF
     * @since 1.1.5
     */
    public int getValue() {
        return value;
    }

    /**
     * @param value the the number of pixels
     * @author WFF
     * @since 1.1.5
     */
    public void setValue(final int value) {
        setAttributeValue(String.valueOf(value));
        this.value = value;
    }

}
