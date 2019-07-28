/*
 * Copyright 2014-2019 Web Firm Framework
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.StampedLock;

import com.webfirmframework.wffweb.tag.html.model.AbstractHtml5SharedObject;
import com.webfirmframework.wffweb.tag.htmlwff.NoTag;

/**
 * Changing the content of this object will be reflected in all consuming tags.
 *
 * @author WFF
 * @since 3.0.6
 *
 */
public class SharedTagContent {

    private final StampedLock lock = new StampedLock();

    private final Set<NoTag> insertedTags = Collections
            .newSetFromMap(new WeakHashMap<NoTag, Boolean>(4, 0.75F));

    private volatile String content;

    private volatile boolean contentTypeHtml;

    /**
     * @param content
     *                    the content its content type will be considered as
     *                    plain text, i.e. contentTypeHtml will be false.
     * @since 3.0.6
     */
    public SharedTagContent(final String content) {
        this.content = content;
        contentTypeHtml = false;
    }

    /**
     * @param content
     * @param contentTypeHtml
     * @since 3.0.6
     */
    public SharedTagContent(final String content,
            final boolean contentTypeHtml) {
        this.content = content;
        this.contentTypeHtml = contentTypeHtml;
    }

    /**
     * @return the content
     * @since 3.0.6
     */
    public String getContent() {
        final long stamp = lock.readLock();
        try {
            return content;
        } finally {
            lock.unlockRead(stamp);
        }
    }

    /**
     * @return true if content type is HTML
     * @since 3.0.6
     */
    public boolean isContentTypeHtml() {
        final long stamp = lock.readLock();
        try {
            return contentTypeHtml;
        } finally {
            lock.unlockRead(stamp);
        }
    }

    /**
     * @param noTag
     * @return true if the object just exists in the set but it doesn't mean the
     *         NoTag is not changed from parent or parent is modified.
     * @since 3.0.6
     */
    boolean contains(final AbstractHtml noTag) {
        final long stamp = lock.readLock();
        try {
            return insertedTags.contains(noTag);
        } finally {
            lock.unlockRead(stamp);
        }
    }

    /**
     * @param content
     *                    content to be reflected in all consuming tags.
     * @since 3.0.6
     */
    public void setContent(final String content) {
        setContent(true, content, contentTypeHtml);
    }

    /**
     * @param content
     *                            content to be reflected in all consuming tags
     * @param contentTypeHtml
     *                            true if the content type is HTML or false if
     *                            plain text
     */
    public void setContent(final String content,
            final boolean contentTypeHtml) {
        setContent(true, content, contentTypeHtml);
    }

    /**
     * @param updateClient
     * @param content
     * @param contentTypeHtml
     * @since 3.0.6
     */
    private void setContent(final boolean updateClient, final String content,
            final boolean contentTypeHtml) {

        final long stamp = lock.writeLock();
        try {

            this.content = content;
            this.contentTypeHtml = contentTypeHtml;

            final Map<AbstractHtml5SharedObject, List<ParentNoTagData>> tagsGroupedBySharedObject = new HashMap<>();

            for (final NoTag prevNoTag : insertedTags) {
                final AbstractHtml parentTag = prevNoTag.getParent();

                if (parentTag == null) {
                    continue;
                }

                final AbstractHtml prevNoTagAsBase = prevNoTag;
                // prevNoTagAsBase.getParentNullifiedCount() > 0
                // means the parent of this tag has already been changed
                // at least once
                if (prevNoTagAsBase.getParentNullifiedCount() > 0) {
                    continue;
                }

                List<ParentNoTagData> dataList = tagsGroupedBySharedObject
                        .get(parentTag.getSharedObject());

                if (dataList == null) {
                    dataList = new ArrayList<>();
                    tagsGroupedBySharedObject.put(parentTag.getSharedObject(),
                            dataList);
                }

                final NoTag noTag = new NoTag(null, content, contentTypeHtml);
                dataList.add(new ParentNoTagData(parentTag, noTag));

            }

            insertedTags.clear();

            for (final Entry<AbstractHtml5SharedObject, List<ParentNoTagData>> entry : tagsGroupedBySharedObject
                    .entrySet()) {

                final List<ParentNoTagData> parentNoTagDatas = entry.getValue();

                final AbstractHtml firstParent = parentNoTagDatas.get(0)
                        .getParent();
                final Lock parentLock = firstParent.getWriteLock();
                parentLock.lock();

                try {

                    for (final ParentNoTagData parentNoTagData : parentNoTagDatas) {

                        // if parentNoTagData.parent == 0 means the previous
                        // NoTag was already removed
                        // if parentNoTagData.parent > 1 means the previous
                        // NoTag was replaced by other children or the previous
                        // NoTag exists but aslo appended/prepended another
                        // child/children to it.

                        // parentNoTagData.parent.getParentNullifiedCount() > 0
                        // means the parent of this tag has already been changed
                        // at least once
                        final AbstractHtml noTagAsBase = parentNoTagData
                                .getNoTag();
                        if (parentNoTagData.getParent()
                                .getChildrenSizeLockless() == 1
                                && noTagAsBase.getParentNullifiedCount() == 0) {

                            insertedTags.add(parentNoTagData.getNoTag());

                            final InnerHtmlListenerData listenerData = parentNoTagData
                                    .getParent()
                                    .addInnerHtmlsAndGetEventsLockless(
                                            updateClient,
                                            parentNoTagData.getNoTag());

                            if (listenerData != null) {
                                // TODO declare new innerHtmlsAdded for multiple
                                // parents after verifying feasibility of
                                // considering rich notag content
                                listenerData.getListener().innerHtmlsAdded(
                                        parentNoTagData.getParent(),
                                        listenerData.getEvents());

                                // TODO do final verification of this code
                            }
                        }

                    }
                } finally {
                    parentLock.unlock();
                }

                firstParent.pushQueue();

            }

        } finally {
            lock.unlockWrite(stamp);
        }

    }

    /**
     * @param updateClient
     * @param applicableTag
     * @since 3.0.6
     */
    void addInnerHtml(final boolean updateClient,
            final AbstractHtml applicableTag) {

        final long stamp = lock.writeLock();

        try {

            final NoTag noTag = new NoTag(null, content, contentTypeHtml);
            final InnerHtmlListenerData listenerData = applicableTag
                    .addInnerHtmlsAndGetEventsLockless(updateClient, noTag);

            if (listenerData != null) {
                // TODO declare new innerHtmlsAdded for multiple parents after
                // verifying feasibility of considering rich notag content
                listenerData.getListener().innerHtmlsAdded(applicableTag,
                        listenerData.getEvents());
            }

            insertedTags.add(noTag);

        } finally {
            lock.unlockWrite(stamp);
        }
        applicableTag.pushQueue();

    }

}
