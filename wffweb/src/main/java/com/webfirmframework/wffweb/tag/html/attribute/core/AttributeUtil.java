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
/**
 *
 */
package com.webfirmframework.wffweb.tag.html.attribute.core;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;

import com.webfirmframework.wffweb.WffSecurityException;
import com.webfirmframework.wffweb.internal.constants.IndexedClassType;
import com.webfirmframework.wffweb.internal.security.object.SecurityObject;
import com.webfirmframework.wffweb.tag.html.AbstractHtml;
import com.webfirmframework.wffweb.tag.html.attribute.InternalAttrNameConstants;
import com.webfirmframework.wffweb.tag.html.attributewff.CustomAttribute;
import com.webfirmframework.wffweb.tag.html.html5.attribute.global.DataWffId;
import com.webfirmframework.wffweb.util.WffBinaryMessageUtil;

/**
 *
 * @author WFF
 * @since 1.0.0
 */
public final class AttributeUtil {

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
    public static String getAttributeHtmlString(final boolean rebuild, final AbstractAttribute... attributes) {
        if (attributes != null) {

            // prefix + total delimiters
            // 1 + (attributes.length - 1)
            // StringUtil.join for reference
            int capacity = attributes.length;

            final String[] htmlStrings = new String[attributes.length];
            for (int i = 0; i < attributes.length; i++) {
                final String htmlString = attributes[i].toHtmlString(rebuild);
                htmlStrings[i] = htmlString;
                capacity += htmlString.length();
            }

            final StringBuilder attributeSB = new StringBuilder(capacity);
            for (final String htmlString : htmlStrings) {
                attributeSB.append(' ').append(htmlString);
            }

            return attributeSB.toString();
        }
        return "";
    }

    /**
     * @param rebuild
     * @param attributes
     * @param charset    the charset
     * @return the attributes bytes array compressed by index
     * @author WFF
     * @throws IOException
     * @since 1.1.3
     */
    public static byte[][] getAttributeHtmlBytesCompressedByIndex(final boolean rebuild, final Charset charset,
            final AbstractAttribute... attributes) throws IOException {

        if (attributes != null) {
            final byte[][] attributesArray = new byte[attributes.length][0];

            for (int i = 0; i < attributesArray.length; i++) {
                final AbstractAttribute attribute = attributes[i];
                attributesArray[i] = attribute.toCompressedBytesByIndex(rebuild, charset);
            }
            return attributesArray;

        }
        return new byte[0][0];
    }

    /**
     * @param rebuild
     * @param attributes
     * @param charset    the charset
     * @return the attributes bytes array compressed by index
     * @author WFF
     * @throws IOException
     * @since 12.0.3
     */
    public static byte[][] getAttributeHtmlBytesCompressedByIndexV2(final boolean rebuild, final Charset charset,
            final AbstractAttribute... attributes) throws IOException {
        if (attributes != null) {
            final byte[][] attributesArray = new byte[attributes.length][0];

            for (int i = 0; i < attributesArray.length; i++) {
                final AbstractAttribute attribute = attributes[i];
                attributesArray[i] = attribute.toCompressedBytesByIndexV2(rebuild, charset);
            }
            return attributesArray;

        }
        return new byte[0][0];
    }

    /**
     * @param attributes
     * @param charset    the charset
     * @return the wff attributes strings bytes array.
     * @author WFF
     * @throws UnsupportedEncodingException throwing this exception will be removed
     *                                      in future version because its internal
     *                                      implementation will never make this
     *                                      exception due to the code changes since
     *                                      3.0.1.
     * @since 2.0.0
     */
    public static byte[][] getWffAttributeBytes(final String charset, final AbstractAttribute... attributes)
            throws UnsupportedEncodingException {
        return getWffAttributeBytes(Charset.forName(charset), attributes);
    }

    /**
     * @param attributes
     * @param charset    the charset
     * @return the wff attributes strings bytes array.
     * @author WFF
     * @since 3.0.1
     */
    public static byte[][] getWffAttributeBytes(final Charset charset, final AbstractAttribute... attributes) {

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

    /**
     * @param attributesBytes
     * @return the array of attributes built from bytes
     * @since 3.0.3
     */
    public static AbstractAttribute[] parseExactAttributeHtmlBytesCompressedByIndex(final byte[][] attributesBytes,
            final Charset charset) {

        final AbstractAttribute[] attributes = new AbstractAttribute[attributesBytes.length];

        int index = 0;
        for (final byte[] compressedBytesByIndex : attributesBytes) {

            final int lengthOfOptimizedBytesOfAttrNameIndex = compressedBytesByIndex[0];

            if (lengthOfOptimizedBytesOfAttrNameIndex > 0) {
                final byte[] tagNameIndexBytes = new byte[lengthOfOptimizedBytesOfAttrNameIndex];
                System.arraycopy(compressedBytesByIndex, 1, tagNameIndexBytes, 0,
                        lengthOfOptimizedBytesOfAttrNameIndex);

                final int attrNameIndex = WffBinaryMessageUtil.getIntFromOptimizedBytes(tagNameIndexBytes);

                final String attrValue = new String(compressedBytesByIndex, compressedBytesByIndex[0] + 1,
                        compressedBytesByIndex.length - (compressedBytesByIndex[0] + 1), charset);

                attributes[index] = AttributeRegistry.getNewAttributeInstanceOrNullIfFailed(attrNameIndex, attrValue);

            } else {

                final String attrNameValue = new String(compressedBytesByIndex, compressedBytesByIndex[0] + 1,
                        compressedBytesByIndex.length - (compressedBytesByIndex[0] + 1), charset);

                final int indexOfEqualChar = attrNameValue.indexOf('=');

                final String attrName;
                final String attrValue;

                if (indexOfEqualChar == -1) {
                    attrName = attrNameValue;
                    attrValue = null;
                } else {
                    attrName = attrNameValue.substring(0, indexOfEqualChar);
                    attrValue = attrNameValue.substring(indexOfEqualChar + 1, attrNameValue.length());
                }

                final AbstractAttribute newAttributeInstance = AttributeRegistry
                        .getNewAttributeInstanceOrNullIfFailed(attrName, attrValue);

                attributes[index] = newAttributeInstance != null ? newAttributeInstance
                        : new CustomAttribute(attrName, attrValue);

            }

            index++;
        }
        return attributes;
    }

    /**
     * @param attributesBytes
     * @return the array of attributes built from bytes
     * @since 3.0.3
     */
    public static AbstractAttribute[] parseExactAttributeHtmlBytesCompressedByIndexV2(final byte[][] attributesBytes,
            final Charset charset) {

        final AbstractAttribute[] attributes = new AbstractAttribute[attributesBytes.length];

        int index = 0;
        for (final byte[] compressedBytesByIndex : attributesBytes) {

            int lengthOfOptimizedBytesOfAttrNameIndex = compressedBytesByIndex[0];

            if (lengthOfOptimizedBytesOfAttrNameIndex > 0
                    || (lengthOfOptimizedBytesOfAttrNameIndex < 0 && lengthOfOptimizedBytesOfAttrNameIndex > -5)) {
                final boolean negative = lengthOfOptimizedBytesOfAttrNameIndex < 0;
                lengthOfOptimizedBytesOfAttrNameIndex = Math.abs(lengthOfOptimizedBytesOfAttrNameIndex);
                final byte[] tagNameIndexBytes = new byte[lengthOfOptimizedBytesOfAttrNameIndex];
                System.arraycopy(compressedBytesByIndex, 1, tagNameIndexBytes, 0,
                        lengthOfOptimizedBytesOfAttrNameIndex);

                final int attrNameIndex = WffBinaryMessageUtil.getIntFromOptimizedBytes(tagNameIndexBytes);

                final byte[] attrValueBytes = new byte[compressedBytesByIndex.length
                        - (lengthOfOptimizedBytesOfAttrNameIndex + 1)];
                System.arraycopy(compressedBytesByIndex, lengthOfOptimizedBytesOfAttrNameIndex + 1, attrValueBytes, 0,
                        attrValueBytes.length);

                final String attrValue = negative
                        ? String.valueOf(WffBinaryMessageUtil.getIntFromOptimizedBytes(attrValueBytes))
                        : new String(attrValueBytes, charset);

                attributes[index] = AttributeRegistry.getNewAttributeInstanceOrNullIfFailed(attrNameIndex, attrValue);

            } else if (lengthOfOptimizedBytesOfAttrNameIndex == 0) {

                final String attrNameValue = new String(compressedBytesByIndex, compressedBytesByIndex[0] + 1,
                        compressedBytesByIndex.length - (compressedBytesByIndex[0] + 1), charset);

                final int indexOfEqualChar = attrNameValue.indexOf('=');

                final String attrName;
                final String attrValue;

                if (indexOfEqualChar == -1) {
                    attrName = attrNameValue;
                    attrValue = null;
                } else {
                    attrName = attrNameValue.substring(0, indexOfEqualChar);
                    attrValue = attrNameValue.substring(indexOfEqualChar + 1, attrNameValue.length());
                }

                final AbstractAttribute newAttributeInstance = AttributeRegistry
                        .getNewAttributeInstanceOrNullIfFailed(attrName, attrValue);

                attributes[index] = newAttributeInstance != null ? newAttributeInstance
                        : new CustomAttribute(attrName, attrValue);

            } else {
                lengthOfOptimizedBytesOfAttrNameIndex = Math.abs(lengthOfOptimizedBytesOfAttrNameIndex);
                final int indexOfPrefix = lengthOfOptimizedBytesOfAttrNameIndex - 5;
                if (indexOfPrefix >= DataWffId.VALUE_PREFIXES.length) {
                    throw new IllegalArgumentException("Error: prefix not indexed for " + indexOfPrefix);
                }
                final String prefix = DataWffId.VALUE_PREFIXES[indexOfPrefix];
                // -5 or -6 it is data-wff-id
                // -5 for prefix S, -6 for prefix C

                final byte[] attrValueIntBytes = new byte[compressedBytesByIndex.length - 1];
                System.arraycopy(compressedBytesByIndex, 1, attrValueIntBytes, 0, compressedBytesByIndex.length - 1);

                final String attrValue = prefix + WffBinaryMessageUtil.getIntFromOptimizedBytes(attrValueIntBytes);

                final AbstractAttribute newAttributeInstance = AttributeRegistry
                        .getNewAttributeInstanceOrNullIfFailed(InternalAttrNameConstants.DATA_WFF_ID, attrValue);

                attributes[index] = newAttributeInstance != null ? newAttributeInstance
                        : new CustomAttribute(InternalAttrNameConstants.DATA_WFF_ID, attrValue);
            }

            index++;
        }
        return attributes;
    }

    /**
     * NB: for internal purpose only
     *
     * @param accessObject
     * @param attrName
     * @param attrNameIndexBytes
     * @param charset
     * @return
     * @since 3.0.6
     */
    private static byte[] getAttrNameBytesCompressedByIndex(final SecurityObject accessObject, final String attrName,
            final byte[] attrNameIndexBytes, final Charset charset) {

        if (accessObject == null || !(IndexedClassType.BROWSER_PAGE.equals(accessObject.forClassType()))) {
            throw new WffSecurityException("Not allowed to consume this method. This method is for internal use.");
        }

        if (attrNameIndexBytes == null) {
            final byte[] rowNodeNameBytes = attrName.getBytes(charset);
            final byte[] wffAttrNameBytes = new byte[rowNodeNameBytes.length + 1];
            // if zero there is no optimized int bytes for index
            // because there is no tagNameIndex. second byte
            // onwards the bytes of tag name
            wffAttrNameBytes[0] = 0;
            System.arraycopy(rowNodeNameBytes, 0, wffAttrNameBytes, 1, rowNodeNameBytes.length);

            return wffAttrNameBytes;

            // logging is not required here
            // it is not an unusual case
            // if (LOGGER.isLoggable(Level.WARNING)) {
            // LOGGER.warning(nodeName
            // + " is not indexed, please register it with
            // TagRegistry");
            // }

        }

        final byte[] wffAttrNameBytes;
        if (attrNameIndexBytes.length == 1) {
            wffAttrNameBytes = attrNameIndexBytes;
        } else {
            wffAttrNameBytes = new byte[attrNameIndexBytes.length + 1];
            wffAttrNameBytes[0] = (byte) attrNameIndexBytes.length;
            System.arraycopy(attrNameIndexBytes, 0, wffAttrNameBytes, 1, attrNameIndexBytes.length);
        }

        return wffAttrNameBytes;
    }

    /**
     * NB: for internal purpose only
     *
     * @param accessObject
     * @param attrName
     * @param charset
     * @return
     * @since 3.0.6
     */
    public static byte[] getAttrNameBytesCompressedByIndex(
            @SuppressWarnings("exports") final SecurityObject accessObject, final String attrName,
            final Charset charset) {

        final PreIndexedAttributeName attr = PreIndexedAttributeName.forAttrName(attrName);

        final byte[] attrNameIndexBytes;
        if (attr != null) {
            attrNameIndexBytes = attr.internalIndexBytes();
        } else {
            attrNameIndexBytes = null;
        }

        return getAttrNameBytesCompressedByIndex(accessObject, attrName, attrNameIndexBytes, charset);
    }

    /**
     * NB: for internal purpose only
     *
     * @param accessObject
     * @param attr
     * @param charset
     * @return
     * @since 3.0.6
     */
    public static byte[] getAttrNameBytesCompressedByIndex(
            @SuppressWarnings("exports") final SecurityObject accessObject, final AbstractAttribute attr,
            final Charset charset) {
        return getAttrNameBytesCompressedByIndex(accessObject, attr.getAttributeName(), attr.getAttrNameIndexBytes(),
                charset);
    }

    /**
     * @param accessObject
     * @param attributes
     * @return
     * @since 3.0.15
     */
    public static List<Lock> lockAndGetWriteLocks(@SuppressWarnings("exports") final SecurityObject accessObject,
            final AbstractAttribute... attributes) {

        if (accessObject == null || !(IndexedClassType.ABSTRACT_HTML.equals(accessObject.forClassType()))) {
            throw new WffSecurityException("Not allowed to consume this method. This method is for internal use.");
        }

        if (attributes == null || attributes.length == 0) {
            return null;
        }

        final Set<AbstractAttribute> attributesSet = new HashSet<>(attributes.length);
        for (final AbstractAttribute attr : attributes) {
            attributesSet.add(attr);
        }

        final List<AbstractAttribute> attributesList = new ArrayList<AbstractAttribute>(attributesSet);

        // lock should be called on the order of objectId otherwise there will be
        // deadlock
        attributesList.sort(Comparator.comparing(AbstractAttribute::objectId));

        final List<Lock> attrLocks = new ArrayList<Lock>(attributesList.size());

        for (final AbstractAttribute attr : attributesList) {
            final Lock writeLock = attr.getObjectWriteLock();
            writeLock.lock();
            attrLocks.add(writeLock);
        }

        if (attrLocks.size() > 1) {
            Collections.reverse(attrLocks);
        }

        return attrLocks;
    }

    /**
     * @param accessObject
     * @param attributes
     * @return
     * @since 3.0.15
     */
    public static List<Lock> lockAndGetWriteLocks(@SuppressWarnings("exports") final SecurityObject accessObject,
            final Collection<AbstractAttribute> attributes) {

        if (accessObject == null || !(IndexedClassType.ABSTRACT_HTML.equals(accessObject.forClassType()))) {
            throw new WffSecurityException("Not allowed to consume this method. This method is for internal use.");
        }

        final List<AbstractAttribute> attributesList = new ArrayList<AbstractAttribute>(new HashSet<>(attributes));

        // lock should be called on the order of objectId otherwise there will be
        // deadlock
        attributesList.sort(Comparator.comparing(AbstractAttribute::objectId));

        final List<Lock> attrLocks = new ArrayList<Lock>(attributesList.size());

        for (final AbstractAttribute attr : attributesList) {
            final Lock writeLock = attr.getObjectWriteLock();
            writeLock.lock();
            attrLocks.add(writeLock);
        }

        if (attrLocks.size() > 1) {
            Collections.reverse(attrLocks);
        }

        return attrLocks;
    }

    /**
     * @param accessObject
     * @param attribute
     * @return the lock object
     * @since 3.0.15
     */
    public static Lock lockAndGetWriteLock(@SuppressWarnings("exports") final SecurityObject accessObject,
            final AbstractAttribute attribute) {

        if (accessObject == null || !(IndexedClassType.ABSTRACT_HTML.equals(accessObject.forClassType()))) {
            throw new WffSecurityException("Not allowed to consume this method. This method is for internal use.");
        }

        final Lock writeLock = attribute.getObjectWriteLock();
        writeLock.lock();

        return writeLock;
    }

    /**
     * for internal use only.
     *
     * @param accessObject
     * @param attribute
     * @param ownerTag
     * @return
     * @since 3.0.15
     */
    public static boolean unassignOwnerTag(@SuppressWarnings("exports") final SecurityObject accessObject,
            final AbstractAttribute attribute, final AbstractHtml ownerTag) {
        if (accessObject == null || !(IndexedClassType.ABSTRACT_HTML.equals(accessObject.forClassType()))) {
            throw new WffSecurityException("Not allowed to consume this method. This method is for internal use.");
        }
        return attribute.unsetOwnerTagLockless(ownerTag);
    }

    /**
     * for internal use only.
     *
     * @param accessObject
     * @param attribute
     * @param ownerTag
     * @since 3.0.15
     */
    public static void assignOwnerTag(@SuppressWarnings("exports") final SecurityObject accessObject,
            final AbstractAttribute attribute, final AbstractHtml ownerTag) {
        if (accessObject == null || !(IndexedClassType.ABSTRACT_HTML.equals(accessObject.forClassType()))) {
            throw new WffSecurityException("Not allowed to consume this method. This method is for internal use.");
        }
        attribute.setOwnerTagLockless(ownerTag);
    }

}
