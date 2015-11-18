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
/**
 *
 */
package com.webfirmframework.wffweb.tag.html.attribute.core;

import java.nio.charset.Charset;

/**
 *
 * @author WFF
 * @since 1.0.0
 */
public class AttributeUtil {

    private static AttributeUtil attributeUtil;

    static {
        attributeUtil = new AttributeUtil();
    }

    /**
     *
     */
    private AttributeUtil() {
    }

    public static AttributeUtil getThis() {
        return attributeUtil;
    }

    /**
     * @param rebuild
     * @param attributes
     * @return the attributes string starting with a space.
     * @author WFF
     * @since 1.0.0
     */
    public String getAttributeHtmlString(final boolean rebuild,
            final AbstractAttribute... attributes) {
        final StringBuilder attributeSB = new StringBuilder();
        if (attributes != null) {
            for (final AbstractAttribute attribute : attributes) {
                attributeSB.append(" ");
                /*
                 * if (attribute instanceof Width) { Width width = (Width)
                 * attribute; attributeSB.append("width=\"");
                 * attributeSB.append(width); attributeSB.append("\""); } else
                 * if (attribute instanceof Width) {
                 *
                 * }
                 */
                attributeSB.append(attribute.toHtmlString(rebuild));
            }
        }
        return attributeSB.toString();
    }

    /**
     * @param rebuild
     * @param attributes
     * @param charset
     *            the charset
     * @return the attributes string starting with a space.
     * @author WFF
     * @since 1.0.0
     */
    public String getAttributeHtmlString(final boolean rebuild,
            final Charset charset, final AbstractAttribute... attributes) {
        final StringBuilder attributeSB = new StringBuilder();
        if (attributes != null) {
            for (final AbstractAttribute attribute : attributes) {
                attributeSB.append(" ");
                attributeSB.append(attribute.toHtmlString(rebuild, charset));
            }
        }
        return attributeSB.toString();
    }
}
