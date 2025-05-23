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
 */
package com.webfirmframework.wffweb.tag.html;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

import com.webfirmframework.wffweb.internal.ObjectId;
import com.webfirmframework.wffweb.tag.html.attribute.core.AbstractAttribute;
import com.webfirmframework.wffweb.tag.html.model.AbstractHtml5SharedObject;
import com.webfirmframework.wffweb.wffbm.data.WffBMData;

/**
 * @author WFF
 * @since 2.1.8
 */
public abstract class AbstractHtmlRepository {

    /**
     * @since 3.0.15
     * @since 12.0.1 it is record type
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

    /**
     * @param tag
     * @param attributeName
     * @return the tag or null
     * @since 3.0.15
     */
    protected static AbstractAttribute getAttributeByNameLockless(final AbstractHtml tag, final String attributeName) {
        return tag.getAttributeByNameLockless(attributeName);
    }

    /**
     * @param tag
     * @return collection of attributes
     * @since 3.0.15
     *
     */
    protected static Collection<AbstractAttribute> getAttributesLockless(final AbstractHtml tag) {
        return tag.getAttributesLockless();
    }

    /**
     * @param tag the tag to check if it contains attributes
     * @return true if it contains attributes otherwise false
     * @since 12.0.2
     */
    protected static boolean containsAttributesLockless(final AbstractHtml tag) {
        return tag.containsAttributesLockless();
    }

    /**
     * Old implementation of getReadLocks method. It is kept for future reference.
     *
     * @param fromTags
     * @return the list of read lock
     * @since 3.0.1
     */
    @SuppressWarnings("unused")
    private static Collection<Lock> getReadLocksOldImpl(final AbstractHtml... fromTags) {
        // passed 2 instead of 1 because the load factor is 0.75f
        final Collection<Lock> locks = fromTags.length > 1 ? new HashSet<>(fromTags.length) : new ArrayDeque<>(2);
        for (final AbstractHtml tag : fromTags) {
            final Lock readLock = tag.getReadLock();
            if (readLock != null) {
                locks.add(readLock);
            }
        }
        return locks;
    }

    /**
     * Note: use lockAndGetReadLocks which safer than this method when it comes to
     * thread-safety.
     *
     * @param fromTags
     * @return the list of read lock
     * @since 3.0.1
     */
    protected static Collection<Lock> getReadLocks(final AbstractHtml... fromTags) {
        return extractReadLocks(fromTags);
    }

    /**
     * @param fromTags
     * @return the list of read lock
     * @since 3.0.15
     */
    protected final static Collection<Lock> lockAndGetReadLocks(final AbstractHtml... fromTags) {
        return lockAndGetLocks(false, fromTags);
    }

    /**
     * @param fromTags
     * @return the list of write lock
     * @since 3.0.15
     */
    protected final static Collection<Lock> lockAndGetWriteLocks(final AbstractHtml... fromTags) {
        return lockAndGetLocks(true, fromTags);
    }

    private static Collection<Lock> lockAndGetLocks(final boolean writeLock, final AbstractHtml... fromTags) {

        if (fromTags == null || fromTags.length == 0) {
            return List.of();
        }

        List<TagContractRecord> tagContractRecords;
        boolean tagModified = false;
        List<Lock> locks = null;
        do {

            if (tagModified) {
                for (final Lock lock : locks) {
                    lock.unlock();
                }
                Thread.onSpinWait();
            }

            tagContractRecords = new ArrayList<>(fromTags.length);

            for (final AbstractHtml tag : fromTags) {
                final AbstractHtml5SharedObject sharedObject = tag.getSharedObject();
                tagContractRecords.add(new TagContractRecord(tag, sharedObject));
            }

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

                final Lock lock = writeLock ? tagContractRecord.tag.lockAndGetWriteLock(latestSharedObject)
                        : tagContractRecord.tag.lockAndGetReadLock(latestSharedObject);
                if (lock == null) {
                    tagModified = true;
                    break;
                }
                locks.add(lock);
                latestSharedObject = tagContractRecord.tag.getSharedObject();
            }

            // NB: must reverse it before returning because its unlocking must be in the
            // reverse order
            if (locks.size() > 1) {
                Collections.reverse(locks);
            }

        } while (tagModified);

        return locks;
    }

    private static List<Lock> extractReadLocks(final AbstractHtml... fromTags) {

        if (fromTags == null || fromTags.length == 0) {
            return List.of();
        }

        if (fromTags.length == 1) {
            final AbstractHtml tag = fromTags[0];
            final AbstractHtml5SharedObject sharedObject = tag.getSharedObject();

            final List<Lock> locks = new ArrayList<>(1);
            locks.add(AbstractHtml.getReadLock(sharedObject));
            return locks;
        }

        final Set<AbstractHtml5SharedObject> sharedObjectsSet = new HashSet<>(fromTags.length);

        for (final AbstractHtml tag : fromTags) {
            final AbstractHtml5SharedObject sharedObject = tag.getSharedObject();
            sharedObjectsSet.add(sharedObject);
        }

        final List<AbstractHtml5SharedObject> sortedSharedObjects = new ArrayList<>(sharedObjectsSet);

        sortedSharedObjects.sort(Comparator.comparing(AbstractHtml5SharedObject::objectId));

        final List<Lock> readLocks = new ArrayList<>(sortedSharedObjects.size());

        for (final AbstractHtml5SharedObject sharedObject : sortedSharedObjects) {
            readLocks.add(AbstractHtml.getReadLock(sharedObject));
        }
        return readLocks;
    }

    /**
     * @param parallel true to use parallel stream.
     * @param parents  the tags from which the nested children to be taken.
     * @return stream of all nested children including the given parent.
     * @since 3.0.0
     * @author WFF
     */
    protected static Stream<AbstractHtml> getAllNestedChildrenIncludingParent(final boolean parallel,
            final AbstractHtml... parents) {

        final Builder<AbstractHtml> builder = Stream.builder();

        AbstractHtml.loopThroughAllNestedChildren(child -> {

            builder.add(child);

            return true;
        }, true, parents);

        // this way makes StackOverflowException
        // return AbstractHtml.getAllNestedChildrenIncludingParent(parent);

        // if (parallel) {
        // return AbstractHtml.getAllNestedChildrenIncludingParent(parent)
        // .parallel();
        // }

        if (parallel) {
            return builder.build().parallel();
        }
        return builder.build();
    }

    /**
     * Loops through all nested children tags (excluding the given tag) of the given
     * tag. The looping is in a random order to gain maximum performance and minimum
     * memory footprint.
     *
     * @param nestedChild    the object of NestedChild from which the
     *                       eachChild(AbstractHtml) to be invoked.
     * @param includeParents true to include the given parent tags in the loop
     * @param parents        the tags from which to loop through.
     *
     * @since 2.1.8
     * @author WFF
     */
    protected static void loopThroughAllNestedChildren(final NestedChild nestedChild, final boolean includeParents,
            final AbstractHtml... parents) {
        AbstractHtml.loopThroughAllNestedChildren(nestedChild, includeParents, parents);
    }

    /**
     * @param abstractHtml
     * @param key
     * @param wffBMData
     * @return
     * @since 2.1.8
     * @author WFF
     */
    protected static WffBMData addWffData(final AbstractHtml abstractHtml, final String key,
            final WffBMData wffBMData) {
        return abstractHtml.addWffData(key, wffBMData);
    }

    /**
     * @param abstractHtml
     * @param key
     * @return
     * @since 2.1.8
     * @author WFF
     */
    protected static WffBMData removeWffData(final AbstractHtml abstractHtml, final String key) {
        return abstractHtml.removeWffData(key);
    }

    /**
     * @param abstractHtml
     * @param key
     * @return
     * @since 3.0.1
     * @author WFF
     */
    protected static WffBMData getWffData(final AbstractHtml abstractHtml, final String key) {
        return abstractHtml.getWffData(key);
    }

}
