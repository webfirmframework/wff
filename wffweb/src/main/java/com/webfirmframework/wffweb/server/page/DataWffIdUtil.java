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
 */
package com.webfirmframework.wffweb.server.page;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Deque;

import com.webfirmframework.wffweb.tag.html.AbstractHtml;
import com.webfirmframework.wffweb.tag.html.TagNameConstants;
import com.webfirmframework.wffweb.tag.html.TagUtil;
import com.webfirmframework.wffweb.tag.html.core.PreIndexedTagName;
import com.webfirmframework.wffweb.tag.html.html5.attribute.global.DataWffId;
import com.webfirmframework.wffweb.tag.htmlwff.NoTag;
import com.webfirmframework.wffweb.util.WffBinaryMessageUtil;

final class DataWffIdUtil {

    private static final PreIndexedTagName[] PRE_INDEXED_TAG_NAMES = PreIndexedTagName.values();

    private DataWffIdUtil() {
        throw new AssertionError();
    }

    /**
     * @param dataWffId
     * @return bytes of data wff id
     *
     */
    static byte[] getDataWffIdBytes(final String dataWffId) {

        // the first byte represents C for Client and S for Server and the
        // remaining string is an integer value
        final byte sOrC = dataWffId.getBytes(StandardCharsets.UTF_8)[0];

        final byte[] intBytes = WffBinaryMessageUtil.getOptimizedBytesFromInt(Integer.parseInt(dataWffId.substring(1)));

        final byte[] dataWffIdBytes = new byte[1 + intBytes.length];
        dataWffIdBytes[0] = sOrC;
        System.arraycopy(intBytes, 0, dataWffIdBytes, 1, intBytes.length);

        return dataWffIdBytes;
    }

    // for future development to handle long id
    // static byte[] getDataWffIdBytes(final String dataWffId) {
    //
    // //TODO getWffIdFromWffIdBytes and may be getWffIdBytesFromTag
    // //to be modified in js
    //
    // final byte[] valueUtf8Bytes = dataWffId
    // .getBytes(StandardCharsets.UTF_8);
    // // the first byte represents C for Client and S for Server and the
    // // remaining string is an integer value
    // final byte sOrC = valueUtf8Bytes[0];
    //
    // final long id = Long.parseLong(dataWffId.substring(1));
    //
    // if (id < Integer.MAX_VALUE) {
    // final byte[] intBytes = WffBinaryMessageUtil
    // .getOptimizedBytesFromLong(id);
    // final byte[] dataWffIdBytes = new byte[2 + intBytes.length];
    // // 0 means contains optimized int bytes
    // dataWffIdBytes[0] = 0;
    // dataWffIdBytes[1] = sOrC;
    // System.arraycopy(intBytes, 0, dataWffIdBytes, 2, intBytes.length);
    //
    // return dataWffIdBytes;
    // }
    //
    // final byte[] dataWffIdBytes = new byte[1 + valueUtf8Bytes.length];
    // // 1 means the rest of the bytes are utf-8 bytes
    // dataWffIdBytes[0] = 1;
    //
    // System.arraycopy(valueUtf8Bytes, 0, dataWffIdBytes, 1,
    // valueUtf8Bytes.length);
    // return dataWffIdBytes;
    // }

    /**
     * @param accessObject TODO
     * @param abstractHtml
     * @return array containing tagName bytes and dataWffIdBytes of the given
     *         argument or its parent.
     * @throws UnsupportedEncodingException throwing this exception will be removed
     *                                      in future version because its internal
     *                                      implementation will never make this
     *                                      exception due to the code changes since
     *                                      3.0.1.
     * @since 2.0.0
     * @author WFF
     * @deprecated this method will be removed in future
     */
    @Deprecated
    static byte[][] getTagNameAndWffId(final AbstractHtml abstractHtml) throws UnsupportedEncodingException {

        final Deque<AbstractHtml> parentStack = new ArrayDeque<>();
        parentStack.push(abstractHtml);

        AbstractHtml parent;
        while ((parent = parentStack.poll()) != null) {

            if (TagUtil.isTagged(parent)) {
                final DataWffId dataWffId = parent.getDataWffId();

                final byte[] dataWffIdBytes = DataWffIdUtil.getDataWffIdBytes(dataWffId.getValue());

                return new byte[][] { parent.getTagName().getBytes(StandardCharsets.UTF_8), dataWffIdBytes };
            }

            final AbstractHtml parentOfParent = parent.getParent();
            if (parentOfParent != null) {

                parentStack.push(parentOfParent);
            }
        }

        return null;
    }

    /**
     * @param childIndex
     * @return { PreIndexedTagName.HASH.indexBytes(), childIndexBytes }
     * @since 3.0.7
     */
    static byte[][] getIndexedTagNameAndChildIndexForNoTag(final Object accessObject, final NoTag noTag) {

        final int childIndex = noTag.getParent().getIndexByChild(accessObject, noTag);

        final byte[] intBytes = WffBinaryMessageUtil.getOptimizedBytesFromInt(childIndex);

        // # represents NoTag, as tag name
        return new byte[][] { PreIndexedTagName.HASH.indexBytes(), intBytes };
    }

    /**
     * @param accessObject TODO
     * @param tag
     * @return array containing tagName bytes and dataWffIdBytes of the given
     *         argument or its parent.
     * @since 3.0.6 contains TagNameBytesCompressedByIndex
     * @author WFF
     */
    static byte[][] getIndexedTagNameAndWffId(final Object accessObject, final AbstractHtml tag) {

        final Deque<AbstractHtml> parentStack = new ArrayDeque<>();
        parentStack.push(tag);

        AbstractHtml parent;
        while ((parent = parentStack.poll()) != null) {

            if (TagUtil.isTagged(parent)) {
                final DataWffId dataWffId = parent.getDataWffId();

                final byte[] dataWffIdBytes = DataWffIdUtil.getDataWffIdBytes(dataWffId.getValue());

                final byte[] wffTagNameBytes = TagUtil.getTagNameBytesCompressedByIndex(accessObject, parent,
                        StandardCharsets.UTF_8);

                return new byte[][] { wffTagNameBytes, dataWffIdBytes };
            }

            final AbstractHtml parentOfParent = parent.getParent();
            if (parentOfParent != null) {

                parentStack.push(parentOfParent);
            }
        }

        return null;
    }

    /**
     * @return
     * @throws UnsupportedEncodingException throwing this exception will be removed
     *                                      in future version because its internal
     *                                      implementation will never make this
     *                                      exception due to the code changes since
     *                                      3.0.1.
     * @since 2.1.13
     * @author WFF
     */
    static byte[][] getTagNameAndWffIdForNoTag() throws UnsupportedEncodingException {
        // there is no DataWffId attribute for NoTag
        return new byte[2][0];
    }

    /**
     * @param bytes   the compressed by index bytes of tag name
     * @param charset
     * @return the tag name or null if no tag name
     * @since 3.0.8
     */
    static String parseTagNameBytesCompressedByIndex(final byte[] bytes, final Charset charset) {

        // this method should be moved to appropriate class

        if (bytes == null || bytes.length == 0) {
            return null;
        }
        if (bytes.length == 1) {
            final int tagNameIndex = WffBinaryMessageUtil.getIntFromOptimizedBytes(bytes);
            return PRE_INDEXED_TAG_NAMES[tagNameIndex].tagName();
        }

        final byte lengOfOptmzdBytsOfTgNam = bytes[0];

        if (lengOfOptmzdBytsOfTgNam > 0) {
            final byte[] copy = new byte[lengOfOptmzdBytsOfTgNam];
            System.arraycopy(bytes, 1, copy, 0, copy.length);
            final int tagNameIndex = WffBinaryMessageUtil.getIntFromOptimizedBytes(copy);
            return PRE_INDEXED_TAG_NAMES[tagNameIndex].tagName();
        }

        final byte[] tagNameBytes = new byte[bytes.length - 1];
        System.arraycopy(bytes, 1, tagNameBytes, 0, tagNameBytes.length);
        return new String(tagNameBytes, charset);
    }

    /**
     * @param bytes   tag name compressed by index bytes
     * @param charset
     * @return true if the tag name is textarea otherwise false
     * @since 3.0.8
     */
    static boolean isTagNameTextArea(final byte[] bytes, final Charset charset) {

        // this method should be moved to appropriate class

        if (bytes == null || bytes.length == 0) {
            return false;
        }
        if (bytes.length == 1) {
            final int tagNameIndex = WffBinaryMessageUtil.getIntFromOptimizedBytes(bytes);

            if (tagNameIndex == PreIndexedTagName.TEXTAREA.index()) {
                return true;
            }

            return false;
        }

        final byte lengOfOptmzdBytsOfTgNam = bytes[0];

        if (lengOfOptmzdBytsOfTgNam > 0) {
            final byte[] copy = new byte[lengOfOptmzdBytsOfTgNam];
            System.arraycopy(bytes, 1, copy, 0, copy.length);
            final int tagNameIndex = WffBinaryMessageUtil.getIntFromOptimizedBytes(copy);

            if (tagNameIndex == PreIndexedTagName.TEXTAREA.index()) {
                return true;
            }

            return false;
        }

        final byte[] tagNameBytes = new byte[bytes.length - 1];
        System.arraycopy(bytes, 1, tagNameBytes, 0, tagNameBytes.length);
        return TagNameConstants.TEXTAREA.equals(new String(tagNameBytes, charset));
    }
}
