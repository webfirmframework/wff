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
package com.webfirmframework.wffweb.tag.html;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

import com.webfirmframework.wffweb.tag.html.attribute.core.AbstractAttribute;
import com.webfirmframework.wffweb.tag.html.model.AbstractHtml5SharedObject;
import com.webfirmframework.wffweb.wffbm.data.WffBMData;

/**
 * @author WFF
 * @since 2.1.8
 */
public abstract class AbstractHtmlRepository {

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
     * @param fromTags
     * @return the list of read lock
     * @since 3.0.1
     */
    protected static Collection<Lock> getReadLocks(final AbstractHtml... fromTags) {

        if (fromTags == null || fromTags.length == 0) {
            return Collections.emptyList();
        }
        if (fromTags.length == 1) {
            return Arrays.asList(fromTags[0].getReadLock());
        }

        final Map<AbstractHtml5SharedObject, AbstractHtml> sharedObjects = new HashMap<>(fromTags.length);

        for (final AbstractHtml tag : fromTags) {
            sharedObjects.put(tag.getSharedObject(), tag);
        }

        final List<Entry<AbstractHtml5SharedObject, AbstractHtml>> sortedSharedObjects = new ArrayList<>(
                sharedObjects.entrySet());

        sortedSharedObjects.sort(Comparator.comparingLong(o -> o.getKey().objectId()));

        final List<Lock> readLocks = new ArrayList<>(sortedSharedObjects.size());

        for (final Entry<AbstractHtml5SharedObject, AbstractHtml> entry : sortedSharedObjects) {
            readLocks.add(entry.getValue().getReadLock());
        }

        return readLocks;
    }

    /**
     * @param fromTags
     * @return the list of read lock
     * @since 3.0.15
     */
    protected final static Collection<Lock> lockAndGetReadLocks(final AbstractHtml... fromTags) {

        if (fromTags == null || fromTags.length == 0) {
            return Collections.emptyList();
        }

        if (fromTags.length == 1) {
            final Lock readLock = fromTags[0].getReadLock();
            readLock.lock();
            return Arrays.asList(readLock);
        }

        final Map<AbstractHtml5SharedObject, AbstractHtml> sharedObjects = new HashMap<>(fromTags.length);

        for (final AbstractHtml tag : fromTags) {
            sharedObjects.put(tag.getSharedObject(), tag);
        }

        final List<Entry<AbstractHtml5SharedObject, AbstractHtml>> sortedSharedObjects = new ArrayList<>(
                sharedObjects.entrySet());

        sortedSharedObjects.sort(Comparator.comparingLong(o -> o.getKey().objectId()));

        final List<Lock> readLocks = new ArrayList<>(sortedSharedObjects.size());

        for (final Entry<AbstractHtml5SharedObject, AbstractHtml> entry : sortedSharedObjects) {
            final Lock readLock = entry.getValue().getReadLock();
            readLock.lock();
            readLocks.add(readLock);
        }

        Collections.reverse(readLocks);

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
