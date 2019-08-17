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
import java.util.LinkedHashSet;
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

    private volatile Map<AbstractHtml, Set<ContentChangeListener>> contentChangeListeners;

    private volatile Map<AbstractHtml, Set<DetachListener>> detachListeners;

    private volatile String content;

    private volatile boolean contentTypeHtml;

    private volatile boolean allowParallel = true;

    private volatile boolean updateClient = true;

    public static final class Content {

        private final String content;
        private final boolean contentTypeHtml;

        private Content(final String content, final boolean contentTypeHtml) {
            super();
            this.content = content;
            this.contentTypeHtml = contentTypeHtml;
        }

        public String getContent() {
            return content;
        }

        public boolean isContentTypeHtml() {
            return contentTypeHtml;
        }

    }

    public static final class ChangeEvent {

        private final AbstractHtml sourceTag;
        private final Content contentBefore;
        private final Content contentAfter;

        private ChangeEvent(final AbstractHtml sourceTag,
                final Content contentBefore, final Content contentAfter) {
            super();
            this.sourceTag = sourceTag;
            this.contentBefore = contentBefore;
            this.contentAfter = contentAfter;
        }

        public AbstractHtml getSourceTag() {
            return sourceTag;
        }

        public Content getContentBefore() {
            return contentBefore;
        }

        public Content getContentAfter() {
            return contentAfter;
        }

    }

    @FunctionalInterface
    public static interface ContentChangeListener {
        public abstract void contentChanged(ChangeEvent changeEvent);
    }

    public static final class DetachEvent {

        private final AbstractHtml sourceTag;
        private final Content content;

        private DetachEvent(final AbstractHtml sourceTag,
                final Content content) {
            super();
            this.content = content;
            this.sourceTag = sourceTag;
        }

        public AbstractHtml getSourceTag() {
            return sourceTag;
        }

        public Content getContent() {
            return content;
        }
    }

    @FunctionalInterface
    public static interface DetachListener {
        public abstract void detached(DetachEvent detachEvent);
    }

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
     * plain text content with allowParallel true.
     *
     * @param content
     *                    the content its content type will be considered as
     *                    plain text, i.e. contentTypeHtml will be false.
     * @since 3.0.6
     */
    public SharedTagContent(final String content) {
        this.content = content;
        contentTypeHtml = false;
        allowParallel = true;
    }

    /**
     * @param allowParallel
     *                          allows parallel operation if this
     *                          SharedTagContent object has to update content of
     *                          tags from multiple BrowserPage instances.
     * @param content
     *                          the content its content type will be considered
     *                          as plain text, i.e. contentTypeHtml will be
     *                          false.
     * @since 3.0.6
     */
    public SharedTagContent(final boolean allowParallel, final String content) {
        this.allowParallel = allowParallel;
        this.content = content;
        contentTypeHtml = false;
    }

    /**
     * The default value of allowParallel is true.
     *
     * @param content
     * @param contentTypeHtml
     * @since 3.0.6
     */
    public SharedTagContent(final String content,
            final boolean contentTypeHtml) {
        this.content = content;
        this.contentTypeHtml = contentTypeHtml;
        allowParallel = true;
    }

    /**
     * @param allowParallel
     *                            allows parallel operation if this
     *                            SharedTagContent object has to update content
     *                            of tags from multiple BrowserPage instances.
     * @param content
     * @param contentTypeHtml
     * @since 3.0.6
     */
    public SharedTagContent(final boolean allowParallel, final String content,
            final boolean contentTypeHtml) {
        this.allowParallel = allowParallel;
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
     * @return true if parallel operation allowed. Refer
     *         {@link SharedTagContent#setAllowParallel(boolean)} for more
     *         details
     * @since 3.0.6
     */
    public boolean isAllowParallel() {
        final long stamp = lock.readLock();
        try {
            return allowParallel;
        } finally {
            lock.unlockRead(stamp);
        }
    }

    /**
     * @param allowParallel
     *                          allows parallel operation if this
     *                          SharedTagContent object has to update content of
     *                          tags from multiple BrowserPage instances.
     *                          Parallel operation will be applied only if
     *                          appropriate.
     * @since 3.0.6
     */
    public void setAllowParallel(final boolean allowParallel) {
        if (this.allowParallel != allowParallel) {
            final long stamp = lock.writeLock();
            try {
                this.allowParallel = allowParallel;
            } finally {
                lock.unlockWrite(stamp);
            }
        }
    }

    /**
     * its default value is true if not explicitly set
     *
     * @return true if updating client browser page is turned off
     * @since 3.0.6
     */
    public boolean isUpdateClient() {
        final long stamp = lock.readLock();
        try {
            return updateClient;
        } finally {
            lock.unlockRead(stamp);
        }
    }

    /**
     * @param updateClient
     *                         true to turn on updating client browser page. By
     *                         default it is true.
     * @since 3.0.6
     */
    public void setUpdateClient(final boolean updateClient) {
        if (this.updateClient != updateClient) {
            final long stamp = lock.writeLock();
            try {
                this.updateClient = updateClient;
            } finally {
                lock.unlockWrite(stamp);
            }
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
        setContent(updateClient, null, allowParallel, content, contentTypeHtml);
    }

    /**
     * @param allowParallel
     *                          allows parallel operation if this
     *                          SharedTagContent object has to update content of
     *                          tags from multiple BrowserPage instances.
     * @param content
     *                          content to be reflected in all consuming tags.
     * @since 3.0.6
     */
    public void setContent(final boolean allowParallel, final String content) {
        setContent(updateClient, null, allowParallel, content, contentTypeHtml);
    }

    /**
     * @param content
     *                            content to be reflected in all consuming tags
     * @param contentTypeHtml
     *                            true if the content type is HTML or false if
     *                            plain text
     * @since 3.0.6
     */
    public void setContent(final String content,
            final boolean contentTypeHtml) {
        setContent(updateClient, null, allowParallel, content, contentTypeHtml);
    }

    /**
     * @param allowParallel
     *                            allows parallel operation if this
     *                            SharedTagContent object has to update content
     *                            of tags from multiple BrowserPage instances.
     * @param content
     *                            content to be reflected in all consuming tags
     * @param contentTypeHtml
     *                            true if the content type is HTML or false if
     *                            plain text
     * @since 3.0.6
     */
    public void setContent(final boolean allowParallel, final String content,
            final boolean contentTypeHtml) {
        setContent(updateClient, null, allowParallel, content, contentTypeHtml);
    }

    /**
     * @param exclusionTags
     *                          these tags will be excluded for client update
     * @param content
     * @since 3.0.6
     */
    public void setContent(final Set<AbstractHtml> exclusionTags,
            final String content) {
        setContent(updateClient, exclusionTags, allowParallel, content,
                contentTypeHtml);
    }

    /**
     * @param exclusionTags
     *                            these tags will be excluded for client update
     * @param content
     * @param contentTypeHtml
     * @since 3.0.6
     */
    public void setContent(final Set<AbstractHtml> exclusionTags,
            final String content, final boolean contentTypeHtml) {
        setContent(updateClient, exclusionTags, allowParallel, content,
                contentTypeHtml);
    }

    /**
     * @param updateClient
     * @param exclusionTags
     *                            these tags will be excluded for client update
     * @param content
     * @param contentTypeHtml
     * @since 3.0.6
     */
    private void setContent(final boolean updateClient,
            final Set<AbstractHtml> exclusionTags, final boolean allowParallel,
            final String content, final boolean contentTypeHtml) {

        final List<AbstractHtml5SharedObject> sharedObjects = new ArrayList<>(
                4);
        final long stamp = lock.writeLock();
        try {

            final Content contentBefore = new Content(this.content,
                    this.contentTypeHtml);
            final Content contentAfter = new Content(content, contentTypeHtml);

            this.allowParallel = allowParallel;
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
                    dataList = new ArrayList<>(4);
                    tagsGroupedBySharedObject.put(parentTag.getSharedObject(),
                            dataList);
                }

                final NoTag noTag = new NoTag(null, content, contentTypeHtml);
                dataList.add(new ParentNoTagData(prevNoTag, parentTag, noTag));

            }

            insertedTags.clear();

            final List<AbstractHtml> modifiedParents = new ArrayList<>(4);

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
                        final AbstractHtml previousNoTag = parentNoTagData
                                .getPreviousNoTag();

                        if (parentNoTagData.getParent().getSharedObject()
                                .equals(sharedObject)
                                && parentNoTagData.getParent()
                                        .getChildrenSizeLockless() == 1
                                && !previousNoTag.isParentNullifiedOnce()) {

                            insertedTags.add(parentNoTagData.getNoTag());
                            modifiedParents.add(parentNoTagData.getParent());

                            boolean updateClientTagSpecific = updateClient;
                            if (updateClient && exclusionTags != null
                                    && exclusionTags.contains(
                                            parentNoTagData.getParent())) {
                                updateClientTagSpecific = false;
                            }

                            final InnerHtmlListenerData listenerData = parentNoTagData
                                    .getParent()
                                    .addInnerHtmlsAndGetEventsLockless(
                                            updateClientTagSpecific,
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

            if (contentChangeListeners != null) {
                for (final AbstractHtml modifiedParent : modifiedParents) {
                    final Set<ContentChangeListener> listeners = contentChangeListeners
                            .get(modifiedParent);
                    if (listeners != null) {
                        for (final ContentChangeListener listener : listeners) {
                            final ChangeEvent changeEvent = new ChangeEvent(
                                    modifiedParent, contentBefore,
                                    contentAfter);
                            listener.contentChanged(changeEvent);
                        }
                    }
                }

            }
        } finally {
            lock.unlockWrite(stamp);
        }
        pushQueue(allowParallel, sharedObjects);
    }

    /**
     * @param allowParallel
     * @param sharedObjects
     *
     * @since 3.0.6
     */
    private void pushQueue(final boolean allowParallel,
            final List<AbstractHtml5SharedObject> sharedObjects) {

        if (allowParallel && sharedObjects.size() > 1) {
            sharedObjects.parallelStream().forEach((sharedObject) -> {
                final PushQueue pushQueue = sharedObject
                        .getPushQueue(ACCESS_OBJECT);
                if (pushQueue != null) {
                    pushQueue.push();
                }
            });
        } else {
            for (final AbstractHtml5SharedObject sharedObject : sharedObjects) {
                final PushQueue pushQueue = sharedObject
                        .getPushQueue(ACCESS_OBJECT);
                if (pushQueue != null) {
                    pushQueue.push();
                }
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
     * @return the NoTag inserted
     * @since 3.0.6
     */
    AbstractHtml addInnerHtml(final boolean updateClient,
            final AbstractHtml applicableTag) {

        final long stamp = lock.writeLock();
        final AbstractHtml5SharedObject sharedObject = applicableTag
                .getSharedObject();

        boolean listenerInvoked = false;
        NoTag noTagInserted = null;
        try {

            final NoTag noTag = new NoTag(null, content, contentTypeHtml);
            final InnerHtmlListenerData listenerData = applicableTag
                    .addInnerHtmlsAndGetEventsLockless(updateClient, noTag);

            noTagInserted = noTag;
            insertedTags.add(noTag);

            if (listenerData != null) {
                // TODO declare new innerHtmlsAdded for multiple parents after
                // verifying feasibility of considering rich notag content
                listenerData.getListener().innerHtmlsAdded(applicableTag,
                        listenerData.getEvents());
                listenerInvoked = true;
            }

        } finally {
            lock.unlockWrite(stamp);
        }
        if (listenerInvoked) {
            pushQueue(sharedObject);
        }

        return noTagInserted;
    }

    /**
     * Detaches without removing contents from consuming tags.
     *
     * @param exclusionTags
     *                          excluded tags from detachment of this
     *                          SharedTagConent object
     *
     * @since 3.0.6
     */
    public void detach(final Set<AbstractHtml> exclusionTags) {
        detach(false, exclusionTags, null);
    }

    /**
     * @param removeContent
     *                          true to remove content from the attached tags or
     *                          false not to remove but will detach
     *
     *
     * @since 3.0.6
     */
    public void detach(final boolean removeContent) {
        detach(removeContent, null, null);
    }

    /**
     * @param removeContent
     *                          true to remove content from the attached tags or
     *                          false not to remove but will detach
     * @param exclusionTags
     *                          excluded tags from detachment of this
     *                          SharedTagConent object
     *
     * @since 3.0.6
     */
    public void detach(final boolean removeContent,
            final Set<AbstractHtml> exclusionTags) {
        detach(removeContent, exclusionTags, null);
    }

    /**
     * @param removeContent
     *                                      true to remove content from the
     *                                      attached tags or false not to remove
     *                                      but will detach
     * @param exclusionTags
     *                                      excluded tags from detachment of
     *                                      this SharedTagConent object
     * @param exclusionClientUpdateTags
     *                                      these tags will be excluded for
     *                                      client update
     * @since 3.0.6
     */
    public void detach(final boolean removeContent,
            final Set<AbstractHtml> exclusionTags,
            final Set<AbstractHtml> exclusionClientUpdateTags) {

        final List<AbstractHtml5SharedObject> sharedObjects = new ArrayList<>(
                4);
        final long stamp = lock.writeLock();
        try {

            final Content contentBefore = new Content(content, contentTypeHtml);

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
                    dataList = new ArrayList<>(4);
                    tagsGroupedBySharedObject.put(parentTag.getSharedObject(),
                            dataList);
                }

                // final NoTag noTag = new NoTag(null, content,
                // contentTypeHtml);

                dataList.add(new ParentNoTagData(prevNoTag, parentTag, null));
            }

            insertedTags.clear();

            final List<AbstractHtml> modifiedParents = new ArrayList<>(4);

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
                        final AbstractHtml previousNoTag = parentNoTagData
                                .getPreviousNoTag();

                        if (parentNoTagData.getParent().getSharedObject()
                                .equals(sharedObject)
                                && parentNoTagData.getParent()
                                        .getChildrenSizeLockless() == 1
                                && !previousNoTag.isParentNullifiedOnce()) {

                            if (exclusionTags != null && exclusionTags
                                    .contains(parentNoTagData.getParent())) {
                                insertedTags.add(
                                        parentNoTagData.getPreviousNoTag());
                            } else {
                                modifiedParents
                                        .add(parentNoTagData.getParent());

                                boolean updateClientTagSpecific = updateClient;
                                if (updateClient
                                        && exclusionClientUpdateTags != null
                                        && exclusionClientUpdateTags.contains(
                                                parentNoTagData.getParent())) {
                                    updateClientTagSpecific = false;
                                }

                                if (removeContent) {

                                    final ChildTagRemoveListenerData listenerData = parentNoTagData
                                            .getParent()
                                            .removeAllChildrenAndGetEventsLockless(
                                                    updateClientTagSpecific);

                                    if (listenerData != null) {
                                        // TODO declare new innerHtmlsAdded for
                                        // multiple
                                        // parents after verifying feasibility
                                        // of
                                        // considering rich notag content
                                        listenerData.getListener()
                                                .allChildrenRemoved(listenerData
                                                        .getEvent());

                                        // push is require only if listener
                                        // invoked
                                        sharedObjects.add(sharedObject);

                                        // TODO do final verification of this
                                        // code
                                    }
                                } else {
                                    previousNoTag.setSharedTagContent(null);
                                }
                            }

                        }

                    }
                } finally {
                    parentLock.unlock();
                }
            }

            if (detachListeners != null) {
                for (final AbstractHtml modifiedParent : modifiedParents) {
                    final Set<DetachListener> listeners = detachListeners
                            .get(modifiedParent);
                    if (listeners != null) {
                        for (final DetachListener listener : listeners) {
                            final DetachEvent detachEvent = new DetachEvent(
                                    modifiedParent, contentBefore);
                            listener.detached(detachEvent);
                        }
                    }
                }

            }
        } finally {
            lock.unlockWrite(stamp);
        }
        pushQueue(allowParallel, sharedObjects);

    }

    /**
     * @param tag
     *                                  the tag on which the content change to
     *                                  be listened
     * @param contentChangeListener
     *                                  to be added
     * @since 3.0.6
     */
    public void addContentChangeListener(final AbstractHtml tag,
            final ContentChangeListener contentChangeListener) {
        final long stamp = lock.writeLock();

        try {
            final Set<ContentChangeListener> listeners;
            if (contentChangeListeners == null) {
                listeners = new LinkedHashSet<>(4);
                contentChangeListeners = new WeakHashMap<>(4, 0.75F);
                contentChangeListeners.put(tag, listeners);
            } else {
                listeners = contentChangeListeners.computeIfAbsent(tag,
                        k -> new LinkedHashSet<>(4));
            }

            listeners.add(contentChangeListener);
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    /**
     * @param tag
     * @param detachListener
     * @since 3.0.6
     */
    public void addDetachListener(final AbstractHtml tag,
            final DetachListener detachListener) {
        final long stamp = lock.writeLock();

        try {
            final Set<DetachListener> listeners;
            if (detachListeners == null) {
                listeners = new LinkedHashSet<>(4);
                detachListeners = new WeakHashMap<>(4, 0.75F);
                detachListeners.put(tag, listeners);
            } else {
                listeners = detachListeners.computeIfAbsent(tag,
                        k -> new LinkedHashSet<>(4));
            }

            listeners.add(detachListener);
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    /**
     * NB: this method will traverse through all consumer tags of this
     * SharedTagContent instance.
     *
     * @param contentChangeListener
     *                                  to be removed from all linked tags
     * @since 3.0.6
     */
    public void removeContentChangeListener(
            final ContentChangeListener contentChangeListener) {
        final long stamp = lock.writeLock();

        try {
            if (contentChangeListeners != null) {

                for (final Set<ContentChangeListener> listeners : contentChangeListeners
                        .values()) {
                    if (listeners != null) {
                        listeners.remove(contentChangeListener);
                    }
                }
            }

        } finally {
            lock.unlockWrite(stamp);
        }
    }

    /**
     * @param tag
     *                                  the tag from which the listener to be
     *                                  removed
     * @param contentChangeListener
     *                                  to be removed
     * @since 3.0.6
     */
    public void removeContentChangeListener(final AbstractHtml tag,
            final ContentChangeListener contentChangeListener) {
        final long stamp = lock.writeLock();

        try {
            if (contentChangeListeners != null) {
                final Set<ContentChangeListener> listeners = contentChangeListeners
                        .get(tag);
                if (listeners != null) {
                    listeners.remove(contentChangeListener);
                }
            }

        } finally {
            lock.unlockWrite(stamp);
        }
    }

    /**
     * NB: this method will traverse through all consumer tags of this
     * SharedTagContent instance.
     *
     * @param detachListener
     *                           to be removed from all linked tags
     * @since 3.0.6
     */
    public void removeDetachListener(final DetachListener detachListener) {
        final long stamp = lock.writeLock();

        try {
            if (detachListeners != null) {

                for (final Set<DetachListener> listeners : detachListeners
                        .values()) {
                    if (listeners != null) {
                        listeners.remove(detachListener);
                    }
                }
            }

        } finally {
            lock.unlockWrite(stamp);
        }
    }

    /**
     * @param tag
     * @param detachListener
     * @since 3.0.6
     */
    public void removeDetachListener(final AbstractHtml tag,
            final DetachListener detachListener) {
        final long stamp = lock.writeLock();

        try {
            if (detachListeners != null) {
                final Set<DetachListener> listeners = detachListeners.get(tag);
                if (listeners != null) {
                    listeners.remove(detachListener);
                }
            }

        } finally {
            lock.unlockWrite(stamp);
        }
    }

    /**
     * @param tag
     *                                   the tag from which the listener to be
     *                                   removed
     * @param contentChangeListeners
     *                                   to be removed
     * @since 3.0.6
     */
    public void removeContentChangeListeners(final AbstractHtml tag,
            final ContentChangeListener... contentChangeListeners) {

        final long stamp = lock.writeLock();

        try {
            if (this.contentChangeListeners != null) {

                final Set<ContentChangeListener> listeners = this.contentChangeListeners
                        .get(tag);

                if (listeners != null) {
                    for (final ContentChangeListener each : contentChangeListeners) {
                        listeners.remove(each);
                    }
                }
            }

        } finally {
            lock.unlockWrite(stamp);
        }
    }

    /**
     * @param tag
     * @param detachListeners
     * @since 3.0.6
     */
    public void removeDetachListeners(final AbstractHtml tag,
            final DetachListener... detachListeners) {

        final long stamp = lock.writeLock();

        try {
            if (this.detachListeners != null) {

                final Set<DetachListener> listeners = this.detachListeners
                        .get(tag);

                if (listeners != null) {
                    for (final DetachListener each : detachListeners) {
                        listeners.remove(each);
                    }
                }
            }

        } finally {
            lock.unlockWrite(stamp);
        }
    }

    /**
     * @param tag
     *                the tag from which all listeners to be removed
     * @since 3.0.6
     */
    public void removeAllContentChangeListeners(final AbstractHtml tag) {
        final long stamp = lock.writeLock();

        try {
            if (contentChangeListeners != null) {
                contentChangeListeners.remove(tag);
            }

        } finally {
            lock.unlockWrite(stamp);
        }
    }

    /**
     * all listeners will be removed from all tags
     *
     * @since 3.0.6
     */
    public void removeAllContentChangeListeners() {
        final long stamp = lock.writeLock();

        try {
            if (contentChangeListeners != null) {
                contentChangeListeners.clear();
            }

        } finally {
            lock.unlockWrite(stamp);
        }
    }

    /**
     * @param tag
     *                the tag from which all listeners to be removed
     * @since 3.0.6
     */
    public void removeAllDetachListeners(final AbstractHtml tag) {
        final long stamp = lock.writeLock();

        try {
            if (detachListeners != null) {
                detachListeners.remove(tag);
            }

        } finally {
            lock.unlockWrite(stamp);
        }
    }

    /**
     * all listeners will be removed from all tags
     *
     * @since 3.0.6
     */
    public void removeAllDetachListeners() {
        final long stamp = lock.writeLock();

        try {
            if (detachListeners != null) {
                detachListeners.clear();
            }

        } finally {
            lock.unlockWrite(stamp);
        }
    }

}
