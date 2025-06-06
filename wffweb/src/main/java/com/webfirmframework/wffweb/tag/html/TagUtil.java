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
package com.webfirmframework.wffweb.tag.html;

import java.lang.ref.Reference;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.locks.Lock;

import com.webfirmframework.wffweb.WffSecurityException;
import com.webfirmframework.wffweb.common.URIEvent;
import com.webfirmframework.wffweb.internal.ObjectId;
import com.webfirmframework.wffweb.internal.constants.IndexedClassType;
import com.webfirmframework.wffweb.internal.security.object.SecurityObject;
import com.webfirmframework.wffweb.tag.html.attribute.core.AbstractAttribute;
import com.webfirmframework.wffweb.tag.html.attribute.core.AttributeUtil;
import com.webfirmframework.wffweb.tag.html.model.AbstractHtml5SharedObject;

/**
 * @author WFF
 * @since 3.0.6
 *
 */
public final class TagUtil {

    /**
     * @since 3.0.15
     * @since 12.0.1 changed to record type
     */
    private record TagContractRecord(AbstractHtml tag, AbstractHtml5SharedObject sharedObject) {

        /**
         * @return objectId
         * @since 3.0.15 returns long value type
         * @since 3.0.19 returns ObjectId value type
         */
        private ObjectId objectId() {
            return sharedObject.objectId();
        }

        private boolean isValid(final AbstractHtml5SharedObject latestSharedObject) {
            if (sharedObject.equals(tag.getSharedObjectLockless()) || (latestSharedObject == null)) {
                return true;
            }
            return tag.getSharedObjectLockless().objectId().compareTo(latestSharedObject.objectId()) >= 0;
        }

        @Override
        public String toString() {
            return sharedObject.objectId().id() + ":" + tag.internalId();
        }
    }

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
            @SuppressWarnings("exports") final SecurityObject accessObject, final AbstractHtml tag,
            final Charset charset) {

        if (accessObject == null || !(IndexedClassType.BROWSER_PAGE.equals(accessObject.forClassType()))) {
            throw new WffSecurityException("Not allowed to consume this method. This method is for internal use.");
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
            final byte[] wffTagNameBytes = new byte[rowNodeNameBytes.length + 1];
            // if zero there is no optimized int bytes for index
            // because there is no tagNameIndex. second byte
            // onwards the bytes of tag name
            wffTagNameBytes[0] = 0;
            System.arraycopy(rowNodeNameBytes, 0, wffTagNameBytes, 1, rowNodeNameBytes.length);

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
            System.arraycopy(tagNameIndexBytes, 0, wffTagNameBytes, 1, tagNameIndexBytes.length);
        }

        return wffTagNameBytes;
    }

    /**
     * only for internal use
     *
     * @param currentTag
     * @param accessObject
     * @param foreignTags
     * @return the collection of locks
     * @since 3.0.15
     */
    static List<Lock> lockAndGetWriteLocks(final AbstractHtml currentTag, final SecurityObject accessObject,
            final AbstractHtml... foreignTags) {

        List<Lock> locks = null;

        // tag state before lock
        List<TagContractRecord> tagContractRecords;
        boolean tagModified = false;

        do {

            if (tagModified) {
                for (final Lock lock : locks) {
                    lock.unlock();
                }
                Thread.onSpinWait();
            }

            tagContractRecords = new ArrayList<>(foreignTags.length + 1);

            for (final AbstractHtml eachTag : foreignTags) {
                tagContractRecords.add(new TagContractRecord(eachTag, eachTag.getSharedObjectLockless()));
            }

            tagContractRecords.add(new TagContractRecord(currentTag, currentTag.getSharedObjectLockless()));

            // lock should be called on the order of objectId otherwise there will be
            // deadlock
            tagContractRecords.sort(Comparator.comparing(TagContractRecord::objectId));

            locks = new ArrayList<>(tagContractRecords.size());

            tagModified = false;

            AbstractHtml5SharedObject latestSharedObject = null;
            for (final TagContractRecord tagContractRecord : tagContractRecords) {

                if (!tagContractRecord.isValid(latestSharedObject)) {
                    tagModified = true;
                    break;
                }
                if (tagContractRecord.tag.getSharedObjectLockless().equals(latestSharedObject)) {
                    continue;
                }

                final Lock lock = tagContractRecord.tag.lockAndGetWriteLock(latestSharedObject);
                if (lock == null) {
                    tagModified = true;
                    break;
                }
                locks.add(lock);

                latestSharedObject = tagContractRecord.tag.getSharedObjectLockless();
            }

            if (locks.size() > 1) {
                Collections.reverse(locks);
            }

        } while (tagModified);

        return locks;
    }

    /**
     * @param accessObject
     * @param tags
     * @return the locks
     * @since 3.0.15
     */
    static List<Lock> lockAndGetNestedAttributeWriteLocks(final SecurityObject accessObject,
            final AbstractHtml... tags) {
        final Set<AbstractAttribute> allNestedAttributes = new HashSet<>();
        AbstractHtml.loopThroughAllNestedChildren(child -> {

            allNestedAttributes.addAll(child.getAttributesLockless());

            return true;
        }, true, tags);

        return AttributeUtil.lockAndGetWriteLocks(accessObject, allNestedAttributes);
    }

    /**
     * NB: only for internal use
     *
     * @param task         task to run atomically
     * @param writeLock    true to execute under write lock or false to execute
     *                     under read lock
     * @param accessObject
     * @since 12.0.0-beta.1
     */
    public static void runAtomically(final AbstractHtml tag, final Runnable task, final boolean writeLock,
            @SuppressWarnings("exports") final SecurityObject accessObject) {
        if (accessObject == null || !(IndexedClassType.BROWSER_PAGE.equals(accessObject.forClassType()))) {
            throw new WffSecurityException("Not allowed to consume this method. This method is for internal use.");
        }
        final Lock lock = writeLock ? tag.lockAndGetWriteLock() : tag.lockAndGetReadLock();
        try {
            task.run();
        } finally {
            lock.unlock();
        }
    }

    /**
     * @param tags the list of tags to order by its hierarchy order
     * @since 12.0.0-beta.1
     */
    public static void sortByHierarchyOrder(final List<Reference<AbstractHtml>> tags) {
        tags.sort(Comparator.comparingLong(o -> Objects.requireNonNull(o.get()).hierarchyOrder));
    }

    /**
     * NB: only for internal use
     *
     * @param tag
     * @param uriEvent
     * @param expectedSO
     * @param accessObject
     * @since 12.0.0-beta.5
     * @return true if sharedObjects are equals
     */
    @SuppressWarnings("exports")
    public static boolean changeInnerHtmlsForURIChange(final AbstractHtml tag, final URIEvent uriEvent,
            final AbstractHtml5SharedObject expectedSO, final SecurityObject accessObject) {

        if (accessObject == null || !(IndexedClassType.BROWSER_PAGE.equals(accessObject.forClassType()))) {
            throw new WffSecurityException("Not allowed to consume this method. This method is for internal use.");
        }

        return tag.changeInnerHtmlsForURIChange(uriEvent, expectedSO);
    }

    /**
     * NB: only for internal use
     *
     * @param tag          the tag object
     * @param sharedObject
     * @param tagByWffId
     * @param accessObject
     * @since 12.0.0-beta.7
     */
    public static void applyURIChangeAndAddDataWffIdAttribute(final AbstractHtml tag,
            final AbstractHtml5SharedObject sharedObject, final Map<String, AbstractHtml> tagByWffId,
            @SuppressWarnings("exports") final SecurityObject accessObject) {
        if (accessObject == null || !(IndexedClassType.BROWSER_PAGE.equals(accessObject.forClassType()))) {
            throw new WffSecurityException("Not allowed to consume this method. This method is for internal use.");
        }
        final Lock lock = tag.lockAndGetWriteLock();
        try {
            // tagByWffId is required only if calling from browserPage.initAbstractHtml
            // method
            tag.applyURIChange(sharedObject, tagByWffId, true);
        } finally {
            lock.unlock();
        }
    }

}
