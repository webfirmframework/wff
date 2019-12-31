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
package com.webfirmframework.wffweb.tag.html;

import java.nio.charset.Charset;

import com.webfirmframework.wffweb.WffSecurityException;
import com.webfirmframework.wffweb.security.object.SecurityClassConstants;

/**
 * @author WFF
 * @since 3.0.6
 *
 */
public final class TagUtil {

    private TagUtil() {
        throw new AssertionError();
    }

    /**
     * @param tag
     * @return true if it is tagless
     * @since 3.0.7
     */
    public static boolean isTagless(final AbstractHtml tag) {
        final String tagName = tag.getTagName();
        if (tagName == null || tagName.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * @param tag
     * @return true if it is Tagged
     * @since 3.0.7
     */
    public static boolean isTagged(final AbstractHtml tag) {
        return !isTagless(tag);
    }

    /**
     * NB: Only for internal use
     *
     * @param accessObject
     * @param tag
     * @param charset
     * @return bytes
     * @since 3.0.6
     */
    public static byte[] getTagNameBytesCompressedByIndex(
            final Object accessObject, final AbstractHtml tag,
            final Charset charset) {

        if (accessObject == null || !(SecurityClassConstants.BROWSER_PAGE
                .equals(accessObject.getClass().getName()))) {
            throw new WffSecurityException(
                    "Not allowed to consume this method. This method is for internal use.");
        }

        // NB: if this method is modified then
        // DataWffIdUtil.parseTagNameBytesCompressedByIndex,
        // DataWffIdUtil.isTagNameTextArea should also be
        // modified

        // just be initialized as local
        final byte[] tagNameIndexBytes = tag.getTagNameIndexBytes();

        final String tagName = tag.getTagName();

        if (tagNameIndexBytes == null) {
            final byte[] rowNodeNameBytes = tagName.getBytes(charset);
            final byte[] wffTagNameBytes = new byte[rowNodeNameBytes.length
                    + 1];
            // if zero there is no optimized int bytes for index
            // because there is no tagNameIndex. second byte
            // onwards the bytes of tag name
            wffTagNameBytes[0] = 0;
            System.arraycopy(rowNodeNameBytes, 0, wffTagNameBytes, 1,
                    rowNodeNameBytes.length);

            return wffTagNameBytes;

            // logging is not required here
            // it is not an unusual case
            // if (LOGGER.isLoggable(Level.WARNING)) {
            // LOGGER.warning(nodeName
            // + " is not indexed, please register it with
            // TagRegistry");
            // }

        }

        final byte[] wffTagNameBytes;
        if (tagNameIndexBytes.length == 1) {
            wffTagNameBytes = tagNameIndexBytes;
        } else {
            wffTagNameBytes = new byte[tagNameIndexBytes.length + 1];
            wffTagNameBytes[0] = (byte) tagNameIndexBytes.length;
            System.arraycopy(tagNameIndexBytes, 0, wffTagNameBytes, 1,
                    tagNameIndexBytes.length);
        }

        return wffTagNameBytes;
    }

}
