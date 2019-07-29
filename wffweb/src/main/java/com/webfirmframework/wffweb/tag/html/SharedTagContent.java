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

import java.io.Serializable;
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

import com.webfirmframework.wffweb.tag.html.listener.PushQueue;
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

    private static final Security ACCESS_OBJECT;

    private final StampedLock lock = new StampedLock();

    private final Set<NoTag> insertedTags = Collections
            .newSetFromMap(new WeakHashMap<NoTag, Boolean>(4, 0.75F));

    private volatile String content;

    private volatile boolean contentTypeHtml;

    // for security purpose, the class name should not be modified
    private static final class Security implements Serializable {

        private static final long serialVersionUID = 1L;

        private Security() {
        }
    }

    static {
        ACCESS_OBJECT = new Security();
    }

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
        return content;
    }

    /**
     * @return true if content type is HTML
     * @since 3.0.6
     */
    public boolean isContentTypeHtml() {
        return contentTypeHtml;
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
                // noTagAsBase.isParentNullifiedOnce() == true
                // means the parent of this tag has already been changed
                // at least once
                if (prevNoTagAsBase.isParentNullifiedOnce()) {
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

            final List<AbstractHtml5SharedObject> sharedObjects = new ArrayList<>();

            for (final Entry<AbstractHtml5SharedObject, List<ParentNoTagData>> entry : tagsGroupedBySharedObject
                    .entrySet()) {

                final AbstractHtml5SharedObject sharedObject = entry.getKey();

                final List<ParentNoTagData> parentNoTagDatas = entry.getValue();

                // pushing using first parent object makes bug (got bug when
                // singleton SharedTagContent object is used under multiple
                // BrowserPage instances)
                // may be because the sharedObject in the parent can be changed
                // before lock

                final Lock parentLock = sharedObject.getLock(ACCESS_OBJECT)
                        .writeLock();
                parentLock.lock();

                try {

                    for (final ParentNoTagData parentNoTagData : parentNoTagDatas) {

                        // parentNoTagData.getParent().getSharedObject().equals(sharedObject)
                        // is important here as it could be change just before
                        // locking

                        // if parentNoTagData.parent == 0 means the previous
                        // NoTag was already removed
                        // if parentNoTagData.parent > 1 means the previous
                        // NoTag was replaced by other children or the previous
                        // NoTag exists but aslo appended/prepended another
                        // child/children to it.

                        // noTagAsBase.isParentNullifiedOnce() == true
                        // means the parent of this tag has already been changed
                        // at least once
                        final AbstractHtml noTagAsBase = parentNoTagData
                                .getNoTag();

                        if (parentNoTagData.getParent().getSharedObject()
                                .equals(sharedObject)
                                && parentNoTagData.getParent()
                                        .getChildrenSizeLockless() == 1
                                && !noTagAsBase.isParentNullifiedOnce()) {

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

                                // push is require only if listener invoked
                                sharedObjects.add(sharedObject);

                                // TODO do final verification of this code
                            }
                        }

                    }
                } finally {
                    parentLock.unlock();
                }
            }

            pushQueue(sharedObjects);

        } finally {
            lock.unlockWrite(stamp);
        }

    }

    /**
     * @since 3.0.6
     */
    private void pushQueue(
            final List<AbstractHtml5SharedObject> sharedObjects) {
        for (final AbstractHtml5SharedObject sharedObject : sharedObjects) {
            final PushQueue pushQueue = sharedObject
                    .getPushQueue(ACCESS_OBJECT);
            if (pushQueue != null) {
                pushQueue.push();
            }
        }
    }

    /**
     * @since 3.0.6
     */
    private void pushQueue(final AbstractHtml5SharedObject sharedObject) {
        final PushQueue pushQueue = sharedObject.getPushQueue(ACCESS_OBJECT);
        if (pushQueue != null) {
            pushQueue.push();
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
        final AbstractHtml5SharedObject sharedObject = applicableTag
                .getSharedObject();

        boolean listenerInvoked = false;

        try {

            final NoTag noTag = new NoTag(null, content, contentTypeHtml);
            final InnerHtmlListenerData listenerData = applicableTag
                    .addInnerHtmlsAndGetEventsLockless(updateClient, noTag);

            if (listenerData != null) {
                // TODO declare new innerHtmlsAdded for multiple parents after
                // verifying feasibility of considering rich notag content
                listenerData.getListener().innerHtmlsAdded(applicableTag,
                        listenerData.getEvents());
                listenerInvoked = true;
            }

            insertedTags.add(noTag);

        } finally {
            lock.unlockWrite(stamp);
        }
        if (listenerInvoked) {
            pushQueue(sharedObject);
        }

    }

}
