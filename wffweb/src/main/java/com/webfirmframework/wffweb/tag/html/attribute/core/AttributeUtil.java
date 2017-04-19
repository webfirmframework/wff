/*
 * Copyright 2014-2017 Web Firm Framework
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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 *
 * @author WFF
 * @since 1.0.0
 */
public class AttributeUtil {

    /**
     *
     */
    private AttributeUtil() {
        throw new AssertionError();
    }

    /**
     * @param rebuild
     * @param attributes
     * @return the attributes string starting with a space.
     * @author WFF
     * @since 1.0.0
     */
    public static final String getAttributeHtmlString(final boolean rebuild,
            final AbstractAttribute... attributes) {
        if (attributes != null) {
            final StringBuilder attributeSB = new StringBuilder(
                    attributes.length * 16);
            for (final AbstractAttribute attribute : attributes) {
                attributeSB.append(' ');
                attributeSB.append(attribute.toHtmlString(rebuild));
            }
            return attributeSB.toString();
        }
        return "";
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
    public static final String getAttributeHtmlString(final boolean rebuild,
            final Charset charset, final AbstractAttribute... attributes) {
        if (attributes != null) {
            final StringBuilder attributeSB = new StringBuilder(
                    attributes.length * 16);
            for (final AbstractAttribute attribute : attributes) {
                attributeSB.append(' ');
                attributeSB.append(attribute.toHtmlString(rebuild, charset));
            }
            return attributeSB.toString();
        }
        return "";
    }

    /**
     * @param rebuild
     * @param attributes
     * @param charset
     *            the charset
     * @return the attributes bytes array compressed by index
     * @author WFF
     * @throws IOException
     * @since 1.1.3
     */
    public static final byte[][] getAttributeHtmlBytesCompressedByIndex(
            final boolean rebuild, final Charset charset,
            final AbstractAttribute... attributes) throws IOException {

        if (attributes != null) {
            final byte[][] attributesArray = new byte[attributes.length][0];

            for (int i = 0; i < attributesArray.length; i++) {
                final AbstractAttribute attribute = attributes[i];
                attributesArray[i] = attribute.toCompressedBytesByIndex(rebuild,
                        charset);
            }
            return attributesArray;

        }
        return new byte[0][0];
    }

    /**
     * @param attributes
     * @param charset
     *            the charset
     * @return the wff attributes strings bytes array.
     * @author WFF
     * @throws UnsupportedEncodingException
     * @since 2.0.0
     */
    public static final byte[][] getWffAttributeBytes(final String charset,
            final AbstractAttribute... attributes)
            throws UnsupportedEncodingException {

        if (attributes != null) {
            final byte[][] attributesArray = new byte[attributes.length][0];

            for (int i = 0; i < attributesArray.length; i++) {
                final AbstractAttribute attribute = attributes[i];
                attributesArray[i] = attribute.toWffString().getBytes(charset);
            }
            return attributesArray;

        }
        return new byte[0][0];
    }

}
