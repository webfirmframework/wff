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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.StampedLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.webfirmframework.wffweb.server.page.ClientTasksWrapper;
import com.webfirmframework.wffweb.tag.html.listener.PushQueue;
import com.webfirmframework.wffweb.tag.html.model.AbstractHtml5SharedObject;
import com.webfirmframework.wffweb.tag.htmlwff.NoTag;

/**
 * This class is highly thread-safe so you can even declare a static object to
 * use under multiple threads. Changing the content of this object will be
 * reflected in all consuming tags if shared property of this object is true.
 * The shared property can be set as by passing constructor argument or by
 * setter method. Its default value is true if not explicitly specified.<br>
 * <br>
 * Usage Eg:-<br>
 *
 * <pre>
 * SharedTagContent&lt;String&gt; stc = new SharedTagContent&lt;&gt;("Initial Content");
 *
 * Div div = new Div(null);
 *
 * Span span1 = new Span(div);
 * span1.subscribeTo(stc);
 *
 * Span span2 = new Span(div);
 * span2.addInnerHtml(stc);
 *
 * System.out.println(div.toHtmlString());
 * stc.setContent("Content Changed");
 * System.out.println(div.toHtmlString());
 * </pre>
 *
 * prints
 *
 * <pre>
 * &lt;div&gt;&lt;span&gt;Initial Content&lt;/span&gt;&lt;span&gt;Initial Content&lt;/span&gt;&lt;/div&gt;
 *
 * &lt;div&gt;&lt;span&gt;Content Changed&lt;/span&gt;&lt;span&gt;Content Changed&lt;/span&gt;&lt;/div&gt;
 * </pre>
 *
 * @param <T>
 *            class type of content in this SharedTagContent object
 * @author WFF
 * @since 3.0.6
 *
 */
public class SharedTagContent<T> {

    private static final Logger LOGGER = Logger
            .getLogger(SharedTagContent.class.getName());

    private static final Security ACCESS_OBJECT;

    private final ContentFormatter<T> DEFAULT_CONTENT_FORMATTER = content -> new Content<>(
            String.valueOf(content.content), content.contentTypeHtml);

    // NB Using ReentrantReadWriteLock causes
    // java.lang.IllegalMonitorStateException in production app
    private final StampedLock lock = new StampedLock();

    private volatile long ordinal = 0L;

    private final Map<NoTag, InsertedTagData<T>> insertedTags = new WeakHashMap<>(
            4, 0.75F);

    private volatile Map<AbstractHtml, Set<ContentChangeListener<T>>> contentChangeListeners;

    private volatile Map<AbstractHtml, Set<DetachListener<T>>> detachListeners;

    private volatile T content;

    private volatile boolean contentTypeHtml;

    private volatile boolean shared = true;

    private volatile UpdateClientNature updateClientNature = UpdateClientNature.ALLOW_ASYNC_PARALLEL;

    private volatile boolean updateClient = true;

    /**
     * Represents the behavior of push operation of BrowserPage to client.
     * {@code ALLOW_ASYNC_PARALLEL} is the default in the
     * {@code SharedTagContent} object unless it is explicitly specified. In
     * future after the arrival of Java Virtual thread there will be two more
     * types named {@code ALLOW_VIRTUAL_PARALLEL} and
     * {@code ALLOW_VIRTUAL_ASYNC_PARALLEL} and
     * {@code ALLOW_VIRTUAL_ASYNC_PARALLEL} may be the default.
     *
     */
    public static enum UpdateClientNature {

        /**
         * Allows parallel operation in the background for pushing changes to
         * client browser page.
         */
        ALLOW_ASYNC_PARALLEL,

        /**
         * Allows parallel operation but will wait for the push to finish to
         * exit the setContent method.
         */
        ALLOW_PARALLEL,

        /**
         * Does each browser page push operation one by one.
         */
        SEQUENTIAL;

        private UpdateClientNature() {
        }
    }

    public static final class Content<T> {

        private final T content;

        private final boolean contentTypeHtml;

        /**
         * The content will be treated as plain text.
         *
         * @param content
         *                    the plain text content.
         * @since 3.0.15
         */
        public Content(final T content) {
            super();
            this.content = content;
            contentTypeHtml = false;
        }

        public Content(final T content, final boolean contentTypeHtml) {
            super();
            this.content = content;
            this.contentTypeHtml = contentTypeHtml;
        }

        public T getContent() {
            return content;
        }

        public boolean isContentTypeHtml() {
            return contentTypeHtml;
        }

    }

    @FunctionalInterface
    public static interface ContentFormatter<T> {
        public abstract Content<String> format(final Content<T> content);
    }

    public static final class ChangeEvent<T> {

        private final AbstractHtml sourceTag;
        private final ContentChangeListener<T> sourceListener;
        private final Content<T> contentBefore;
        private final Content<T> contentAfter;
        private final Content<String> contentApplied;
        private final ContentFormatter<T> formatter;

        private ChangeEvent(final AbstractHtml sourceTag,
                final ContentChangeListener<T> sourceListener,
                final Content<T> contentBefore, final Content<T> contentAfter,
                final Content<String> contentApplied,
                final ContentFormatter<T> formatter) {
            super();
            this.sourceTag = sourceTag;
            this.sourceListener = sourceListener;
            this.contentBefore = contentBefore;
            this.contentAfter = contentAfter;
            this.contentApplied = contentApplied;
            this.formatter = formatter;
        }

        public AbstractHtml getSourceTag() {
            return sourceTag;
        }

        public ContentChangeListener<T> getSourceListener() {
            return sourceListener;
        }

        public Content<T> getContentBefore() {
            return contentBefore;
        }

        public Content<T> getContentAfter() {
            return contentAfter;
        }

        public Content<String> getContentApplied() {
            return contentApplied;
        }

        public ContentFormatter<T> getFormatter() {
            return formatter;
        }

    }

    /**
     * @author WFF
     * @since 3.0.6
     *
     */
    @FunctionalInterface
    public static interface ContentChangeListener<T> {
        /**
         * NB: Do not call any methods of this SharedTagContent inside
         * contentChanged method, instead write it inside the returning Runnable
         * object (Runnable.run).
         *
         * @param changeEvent
         * @return Write code to run after contentChanged invoked. It doesn't
         *         guarantee the order of execution as the order of
         *         contentChanged method execution. This is just like a post
         *         function for this method. If any methods of this
         *         SharedTagContent object to be called it must be written
         *         inside this returning Runnable object (Runnable.run).
         */
        public abstract Runnable contentChanged(
                final ChangeEvent<T> changeEvent);
    }

    public static final class DetachEvent<T> {

        private final AbstractHtml sourceTag;
        private final DetachListener<T> sourceListener;
        private final Content<T> content;

        private DetachEvent(final AbstractHtml sourceTag,
                final DetachListener<T> sourceListener,
                final Content<T> content) {
            super();
            this.sourceListener = sourceListener;
            this.sourceTag = sourceTag;
            this.content = content;
        }

        public AbstractHtml getSourceTag() {
            return sourceTag;
        }

        public DetachListener<T> getSourceListener() {
            return sourceListener;
        }

        public Content<T> getContent() {
            return content;
        }
    }

    /**
     * @author WFF
     * @since 3.0.6
     *
     */
    @FunctionalInterface
    public static interface DetachListener<T> {
        /**
         * NB: Do not call any methods of this SharedTagContent inside detached,
         * instead write it inside the returning Runnable object (Runnable.run).
         *
         * @param detachEvent
         * @return Write code to run after detached invoked. It doen't guarantee
         *         the order of execution as the order of detached method
         *         execution. This is just like a post function for this method.
         *         If any methods of this SharedTagContent object to be called
         *         it must be written inside this returning Runnable object
         *         (Runnable.run).
         */
        public abstract Runnable detached(final DetachEvent<T> detachEvent);
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
     * @param updateClientNature
     *
     *                               If this SharedTagContent object has to
     *                               update content of tags from multiple
     *                               BrowserPage instances,
     *                               UpdateClientNature.ALLOW_ASYNC_PARALLEL
     *                               will allow parallel operation in the
     *                               background for pushing changes to client
     *                               browser page and
     *                               UpdateClientNature.ALLOW_PARALLEL will
     *                               allow parallel operation but will wait for
     *                               the push to finish to exit the setContent
     *                               method. UpdateClientNature.SEQUENTIAL will
     *                               sequentially do each browser page push
     *                               operation.
     * @param shared
     *                               true to share its content across all
     *                               consuming tags when
     *                               {@link SharedTagContent#setContent} is
     *                               called.
     * @param content
     *                               the content to embed in the consumer tags.
     * @param contentTypeHtml
     *                               true to treat the given content as HTML
     *                               otherwise false.
     * @since 3.0.15
     */
    public SharedTagContent(final UpdateClientNature updateClientNature,
            final boolean shared, final T content,
            final boolean contentTypeHtml) {
        if (updateClientNature != null) {
            this.updateClientNature = updateClientNature;
        }
        this.shared = shared;
        this.content = content;
        this.contentTypeHtml = contentTypeHtml;
    }

    /**
     * plain text content with updateClientNature as
     * UpdateClientNature.ALLOW_ASYNC_PARALLEL.
     *
     * @param content
     *                    the content its content type will be considered as
     *                    plain text, i.e. contentTypeHtml will be false.
     * @since 3.0.6
     */
    public SharedTagContent(final T content) {
        this(null, true, content, false);
    }

    /**
     * @param updateClientNature
     *
     *                               If this SharedTagContent object has to
     *                               update content of tags from multiple
     *                               BrowserPage instances,
     *                               UpdateClientNature.ALLOW_ASYNC_PARALLEL
     *                               will allow parallel operation in the
     *                               background for pushing changes to client
     *                               browser page and
     *                               UpdateClientNature.ALLOW_PARALLEL will
     *                               allow parallel operation but will wait for
     *                               the push to finish to exit the setContent
     *                               method. UpdateClientNature.SEQUENTIAL will
     *                               sequentially do each browser page push
     *                               operation.
     * @param content
     *                               the content its content type will be
     *                               considered as plain text, i.e.
     *                               contentTypeHtml will be false.
     * @since 3.0.6
     */
    public SharedTagContent(final UpdateClientNature updateClientNature,
            final T content) {
        this(updateClientNature, true, content, false);
    }

    /**
     * The default value of updateClientNature is
     * UpdateClientNature.ALLOW_ASYNC_PARALLEL.
     *
     * @param content
     *                            the content to embed in the consumer tags.
     * @param contentTypeHtml
     *                            true to treat the given content as HTML
     *                            otherwise false.
     * @since 3.0.6
     */
    public SharedTagContent(final T content, final boolean contentTypeHtml) {
        this(null, true, content, contentTypeHtml);
    }

    /**
     * @param updateClientNature
     *
     *                               If this SharedTagContent object has to
     *                               update content of tags from multiple
     *                               BrowserPage instances,
     *                               UpdateClientNature.ALLOW_ASYNC_PARALLEL
     *                               will allow parallel operation in the
     *                               background for pushing changes to client
     *                               browser page and
     *                               UpdateClientNature.ALLOW_PARALLEL will
     *                               allow parallel operation but will wait for
     *                               the push to finish to exit the setContent
     *                               method. UpdateClientNature.SEQUENTIAL will
     *                               sequentially do each browser page push
     *                               operation.
     * @param content
     *                               the content to embed in the consumer tags.
     * @param contentTypeHtml
     *                               true to treat the given content as HTML
     *                               otherwise false.
     * @since 3.0.6
     */
    public SharedTagContent(final UpdateClientNature updateClientNature,
            final T content, final boolean contentTypeHtml) {
        this(updateClientNature, true, content, contentTypeHtml);
    }

    /**
     * @param updateClientNature
     *
     *                               If this SharedTagContent object has to
     *                               update content of tags from multiple
     *                               BrowserPage instances,
     *                               UpdateClientNature.ALLOW_ASYNC_PARALLEL
     *                               will allow parallel operation in the
     *                               background for pushing changes to client
     *                               browser page and
     *                               UpdateClientNature.ALLOW_PARALLEL will
     *                               allow parallel operation but will wait for
     *                               the push to finish to exit the setContent
     *                               method. UpdateClientNature.SEQUENTIAL will
     *                               sequentially do each browser page push
     *                               operation.
     * @param shared
     *                               true to share its content across all
     *                               consuming tags when
     *                               {@link SharedTagContent#setContent} is
     *                               called.
     * @param content
     *                               the content which will be treated as plain
     *                               text in the consumer tags.
     *
     * @since 3.0.15
     */
    public SharedTagContent(final UpdateClientNature updateClientNature,
            final boolean shared, final T content) {
        this(updateClientNature, shared, content, false);
    }

    /**
     * @param shared
     *                            true to share its content across all consuming
     *                            tags when {@link SharedTagContent#setContent}
     *                            is called.
     * @param content
     *                            the content to embed in the consumer tags.
     * @param contentTypeHtml
     *                            true to treat the given content as HTML
     *                            otherwise false.
     * @since 3.0.6
     */
    public SharedTagContent(final boolean shared, final T content,
            final boolean contentTypeHtml) {
        this(null, shared, content, contentTypeHtml);
    }

    /**
     * @param shared
     *                    true to share its content across all consuming tags
     *                    when {@link SharedTagContent#setContent} is called.
     * @param content
     *                    the content which will be treated as plain text in the
     *                    consumer tags.
     * @since 3.0.6
     */
    public SharedTagContent(final boolean shared, final T content) {
        this(null, shared, content, false);
    }

    /**
     * @return the content
     * @since 3.0.6
     */
    public T getContent() {
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
     * @return true if its content is shared across all consuming tags.
     * @since 3.0.6
     */
    public boolean isShared() {
        return shared;
    }

    /**
     *
     * Refer {@link #setUpdateClientNature(UpdateClientNature)} for more
     * details.
     *
     * @return UpdateClientNature which specifies the nature.
     * @since 3.0.6
     */
    public UpdateClientNature getUpdateClientNature() {
        return updateClientNature;
    }

    /**
     * @param updateClientNature
     *
     *                               If this SharedTagContent object has to
     *                               update content of tags from multiple
     *                               BrowserPage instances,
     *                               UpdateClientNature.ALLOW_ASYNC_PARALLEL
     *                               will allow parallel operation in the
     *                               background for pushing changes to client
     *                               browser page and
     *                               UpdateClientNature.ALLOW_PARALLEL will
     *                               allow parallel operation but will wait for
     *                               the push to finish to exit the setContent
     *                               method. UpdateClientNature.SEQUENTIAL will
     *                               sequentially do each browser page push
     *                               operation.
     * @since 3.0.6
     */
    public void setUpdateClientNature(
            final UpdateClientNature updateClientNature) {

        if (updateClientNature != null
                && !updateClientNature.equals(this.updateClientNature)) {
            final long stamp = lock.writeLock();
            try {
                this.updateClientNature = updateClientNature;
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
        return updateClient;
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
     * @param shared
     *                   true to make content of this SharedTagContent object
     *                   across all consuming tags while
     *                   {@link SharedTagContent#setContent(Object)} is called.
     * @since 3.0.6
     */
    public void setShared(final boolean shared) {
        if (this.shared = shared) {
            final long stamp = lock.writeLock();
            try {
                this.shared = shared;
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
            return insertedTags.containsKey(noTag);
        } finally {
            lock.unlockRead(stamp);
        }
    }

    /**
     * @param content
     *                    content to be reflected in all consuming tags.
     * @since 3.0.6
     */
    public void setContent(final T content) {
        setContent(updateClient, updateClientNature, null, content,
                contentTypeHtml);
    }

    /**
     * @param updateClientNature
     *
     *                               If this SharedTagContent object has to
     *                               update content of tags from multiple
     *                               BrowserPage instances,
     *                               UpdateClientNature.ALLOW_ASYNC_PARALLEL
     *                               will allow parallel operation in the
     *                               background for pushing changes to client
     *                               browser page and
     *                               UpdateClientNature.ALLOW_PARALLEL will
     *                               allow parallel operation but will wait for
     *                               the push to finish to exit the setContent
     *                               method. UpdateClientNature.SEQUENTIAL will
     *                               sequentially do each browser page push
     *                               operation.
     * @param content
     *                               content to be reflected in all consuming
     *                               tags.
     * @since 3.0.6
     */
    public void setContent(final UpdateClientNature updateClientNature,
            final T content) {
        setContent(updateClient,
                (updateClientNature != null ? updateClientNature
                        : this.updateClientNature),
                null, content, contentTypeHtml);
    }

    /**
     * @param content
     *                            content to be reflected in all consuming tags
     * @param contentTypeHtml
     *                            true if the content type is HTML or false if
     *                            plain text
     * @since 3.0.6
     */
    public void setContent(final T content, final boolean contentTypeHtml) {
        setContent(updateClient, updateClientNature, null, content,
                contentTypeHtml);
    }

    /**
     * @param updateClientNature
     *
     *                               If this SharedTagContent object has to
     *                               update content of tags from multiple
     *                               BrowserPage instances,
     *                               UpdateClientNature.ALLOW_ASYNC_PARALLEL
     *                               will allow parallel operation in the
     *                               background for pushing changes to client
     *                               browser page and
     *                               UpdateClientNature.ALLOW_PARALLEL will
     *                               allow parallel operation but will wait for
     *                               the push to finish to exit the setContent
     *                               method. UpdateClientNature.SEQUENTIAL will
     *                               sequentially do each browser page push
     *                               operation.
     * @param content
     *                               content to be reflected in all consuming
     *                               tags
     * @param contentTypeHtml
     *                               true if the content type is HTML or false
     *                               if plain text
     * @since 3.0.6
     */
    public void setContent(final UpdateClientNature updateClientNature,
            final T content, final boolean contentTypeHtml) {
        setContent(updateClient,
                (updateClientNature != null ? updateClientNature
                        : this.updateClientNature),
                null, content, contentTypeHtml);
    }

    /**
     * @param exclusionTags
     *                          tags to be excluded only from client update. It
     *                          means that the content of all consumer tags will
     *                          be kept updated at server side so their content
     *                          content formatter and change listeners will be
     *                          invoked.
     * @param content
     * @since 3.0.6
     */
    public void setContent(final Set<AbstractHtml> exclusionTags,
            final T content) {
        setContent(updateClient, updateClientNature, exclusionTags, content,
                contentTypeHtml);
    }

    /**
     * @param exclusionTags
     *                            tags to be excluded only from client update.
     *                            It means that the content of all consumer tags
     *                            will be kept updated at server side so their
     *                            content content formatter and change listeners
     *                            will be invoked.
     * @param content
     * @param contentTypeHtml
     * @since 3.0.6
     */
    public void setContent(final Set<AbstractHtml> exclusionTags,
            final T content, final boolean contentTypeHtml) {
        setContent(updateClient, updateClientNature, exclusionTags, content,
                contentTypeHtml);
    }

    /**
     * @param updateClient
     * @param updateClientNature
     *
     *                               If this SharedTagContent object has to
     *                               update content of tags from multiple
     *                               BrowserPage instances,
     *                               UpdateClientNature.ALLOW_ASYNC_PARALLEL
     *                               will allow parallel operation in the
     *                               background for pushing changes to client
     *                               browser page and
     *                               UpdateClientNature.ALLOW_PARALLEL will
     *                               allow parallel operation but will wait for
     *                               the push to finish to exit the setContent
     *                               method. UpdateClientNature.SEQUENTIAL will
     *                               sequentially do each browser page push
     *                               operation.
     * @param exclusionTags
     *                               tags to be excluded only from client
     *                               update. It means that the content of all
     *                               consumer tags will be kept updated at
     *                               server side so their content content
     *                               formatter and change listeners will be
     *                               invoked.
     * @param content
     * @param contentTypeHtml
     */
    private void setContent(final boolean updateClient,
            final UpdateClientNature updateClientNature,
            final Set<AbstractHtml> exclusionTags, final T content,
            final boolean contentTypeHtml) {
        setContent(updateClient, updateClientNature, exclusionTags, content,
                contentTypeHtml, shared);
    }

    /**
     * @param updateClient
     * @param updateClientNature
     *
     *                               If this SharedTagContent object has to
     *                               update content of tags from multiple
     *                               BrowserPage instances,
     *                               UpdateClientNature.ALLOW_ASYNC_PARALLEL
     *                               will allow parallel operation in the
     *                               background for pushing changes to client
     *                               browser page and
     *                               UpdateClientNature.ALLOW_PARALLEL will
     *                               allow parallel operation but will wait for
     *                               the push to finish to exit the setContent
     *                               method. UpdateClientNature.SEQUENTIAL will
     *                               sequentially do each browser page push
     *                               operation.
     * @param exclusionTags
     *                               tags to be excluded only from client
     *                               update. It means that the content of all
     *                               consumer tags will be kept updated at
     *                               server side so their content content
     *                               formatter and change listeners will be
     *                               invoked.
     * @param content
     * @param contentTypeHtml
     * @param shared
     */
    private void setContent(final boolean updateClient,
            final UpdateClientNature updateClientNature,
            final Set<AbstractHtml> exclusionTags, final T content,
            final boolean contentTypeHtml, final boolean shared) {

        if (shared) {

            final List<AbstractHtml5SharedObject> sharedObjects = new ArrayList<>(
                    4);
            List<Runnable> runnables = null;
            final long stamp = lock.writeLock();
            try {

                final Content<T> contentBefore = new Content<>(this.content,
                        this.contentTypeHtml);
                final Content<T> contentAfter = new Content<>(content,
                        contentTypeHtml);

                this.updateClientNature = updateClientNature;
                this.content = content;
                this.contentTypeHtml = contentTypeHtml;
                this.shared = shared;

                final List<Map.Entry<NoTag, InsertedTagData<T>>> insertedTagsEntries = insertedTags
                        .entrySet().stream()
                        .sorted(Map.Entry.comparingByValue())
                        .collect(Collectors.toList());

                insertedTags.clear();

                final Map<AbstractHtml5SharedObject, List<ParentNoTagData<T>>> tagsGroupedBySharedObject = new LinkedHashMap<>();

                for (final Entry<NoTag, InsertedTagData<T>> entry : insertedTagsEntries) {

                    final NoTag prevNoTag = entry.getKey();
                    final InsertedTagData<T> insertedTagData = entry.getValue();
                    final ContentFormatter<T> formatter = insertedTagData
                            .formatter();

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

                    List<ParentNoTagData<T>> dataList = tagsGroupedBySharedObject
                            .get(parentTag.getSharedObject());

                    if (dataList == null) {
                        dataList = new ArrayList<>(4);
                        tagsGroupedBySharedObject
                                .put(parentTag.getSharedObject(), dataList);
                    }
                    NoTag noTag;
                    Content<String> contentApplied;
                    try {
                        contentApplied = formatter.format(contentAfter);
                        if (contentApplied != null) {
                            noTag = new NoTag(null, contentApplied.getContent(),
                                    contentApplied.isContentTypeHtml());

                        } else {
                            noTag = prevNoTag;
                            contentApplied = null;
                        }
                    } catch (final RuntimeException e) {
                        contentApplied = new Content<>("", false);
                        noTag = new NoTag(null, contentApplied.getContent(),
                                contentApplied.isContentTypeHtml());
                        LOGGER.log(Level.SEVERE,
                                "Exception while ContentFormatter.format", e);
                    }

                    final AbstractHtml noTagAsBase = noTag;
                    noTagAsBase.setSharedTagContent(this);
                    dataList.add(new ParentNoTagData<>(prevNoTag, parentTag,
                            noTag, insertedTagData, contentApplied));

                }

                final List<ModifiedParentData<T>> modifiedParents = new ArrayList<>(
                        4);

                for (final Entry<AbstractHtml5SharedObject, List<ParentNoTagData<T>>> entry : tagsGroupedBySharedObject
                        .entrySet()) {

                    final AbstractHtml5SharedObject sharedObject = entry
                            .getKey();

                    final List<ParentNoTagData<T>> parentNoTagDatas = entry
                            .getValue();

                    // pushing using first parent object makes bug (got bug when
                    // singleton SharedTagContent object is used under multiple
                    // BrowserPage instances)
                    // may be because the sharedObject in the parent can be
                    // changed
                    // before lock

                    final Lock parentLock = sharedObject.getLock(ACCESS_OBJECT)
                            .writeLock();
                    parentLock.lock();

                    try {

                        for (final ParentNoTagData<T> parentNoTagData : parentNoTagDatas) {

                            // parentNoTagData.getParent().getSharedObject().equals(sharedObject)
                            // is important here as it could be change just
                            // before
                            // locking

                            // if parentNoTagData.parent == 0 means the previous
                            // NoTag was already removed
                            // if parentNoTagData.parent > 1 means the previous
                            // NoTag was replaced by other children or the
                            // previous
                            // NoTag exists but aslo appended/prepended another
                            // child/children to it.

                            // noTagAsBase.isParentNullifiedOnce() == true
                            // means the parent of this tag has already been
                            // changed
                            // at least once
                            final AbstractHtml previousNoTag = parentNoTagData
                                    .previousNoTag();

                            // to get safety of lock it is executed before
                            // addInnerHtmlsAndGetEventsLockless
                            // However lock safety is irrelevant here as the
                            // SharedTagContent will not reuse the same NoTag
                            previousNoTag.setSharedTagContent(null);

                            if (parentNoTagData.parent().getSharedObject()
                                    .equals(sharedObject)
                                    && parentNoTagData.parent()
                                            .getChildrenSizeLockless() == 1
                                    && !previousNoTag.isParentNullifiedOnce()) {

                                if (parentNoTagData.contentApplied() != null) {

                                    insertedTags.put(parentNoTagData.getNoTag(),
                                            parentNoTagData.insertedTagData());

                                    modifiedParents
                                            .add(new ModifiedParentData<>(
                                                    parentNoTagData.parent(),
                                                    parentNoTagData
                                                            .contentApplied(),
                                                    parentNoTagData
                                                            .insertedTagData()
                                                            .formatter()));

                                    boolean updateClientTagSpecific = updateClient;
                                    if (updateClient && exclusionTags != null
                                            && exclusionTags.contains(
                                                    parentNoTagData.parent())) {
                                        updateClientTagSpecific = false;
                                    }

                                    final InnerHtmlListenerData listenerData = parentNoTagData
                                            .parent()
                                            .addInnerHtmlsAndGetEventsLockless(
                                                    updateClientTagSpecific,
                                                    parentNoTagData.getNoTag());

                                    if (listenerData != null) {

                                        // subscribed and offline
                                        if (parentNoTagData.insertedTagData()
                                                .subscribed()
                                                && !sharedObject
                                                        .isActiveWSListener()) {
                                            final ClientTasksWrapper lastClientTask = parentNoTagData
                                                    .insertedTagData()
                                                    .lastClientTask();
                                            if (lastClientTask != null) {
                                                lastClientTask.nullifyTasks();
                                            }
                                        }

                                        // TODO declare new innerHtmlsAdded for
                                        // multiple
                                        // parents after verifying feasibility
                                        // of
                                        // considering rich notag content
                                        final ClientTasksWrapper clientTask = listenerData
                                                .listener().innerHtmlsAdded(
                                                        parentNoTagData
                                                                .parent(),
                                                        listenerData.events());

                                        parentNoTagData.insertedTagData()
                                                .lastClientTask(clientTask);

                                        // push is require only if listener
                                        // invoked
                                        sharedObjects.add(sharedObject);

                                        // TODO do final verification of this
                                        // code
                                    }

                                } else {
                                    insertedTags.put(parentNoTagData.getNoTag(),
                                            parentNoTagData.insertedTagData());

                                    modifiedParents
                                            .add(new ModifiedParentData<>(
                                                    parentNoTagData.parent(),
                                                    parentNoTagData
                                                            .contentApplied(),
                                                    parentNoTagData
                                                            .insertedTagData()
                                                            .formatter()));
                                }
                            }

                        }
                    } finally {
                        parentLock.unlock();
                    }
                }

                if (contentChangeListeners != null) {
                    for (final ModifiedParentData<T> modifiedParentData : modifiedParents) {
                        final AbstractHtml modifiedParent = modifiedParentData
                                .parent();
                        final Set<ContentChangeListener<T>> listeners = contentChangeListeners
                                .get(modifiedParent);
                        if (listeners != null) {
                            runnables = new ArrayList<>(listeners.size());
                            for (final ContentChangeListener<T> listener : listeners) {
                                final ChangeEvent<T> changeEvent = new ChangeEvent<>(
                                        modifiedParent, listener, contentBefore,
                                        contentAfter,
                                        modifiedParentData.contentApplied(),
                                        modifiedParentData.formatter());
                                try {
                                    final Runnable runnable = listener
                                            .contentChanged(changeEvent);
                                    if (runnable != null) {
                                        runnables.add(runnable);
                                    }
                                } catch (final RuntimeException e) {
                                    LOGGER.log(Level.SEVERE,
                                            "Exception while ContentChangeListener.contentChanged",
                                            e);
                                }
                            }
                        }
                    }

                }
            } finally {
                lock.unlockWrite(stamp);
            }

            pushQueue(updateClientNature, sharedObjects);

            if (runnables != null) {
                for (final Runnable runnable : runnables) {
                    try {
                        runnable.run();
                    } catch (final RuntimeException e) {
                        LOGGER.log(Level.SEVERE,
                                "Exception while Runnable.run returned by ContentChangeListener.contentChanged",
                                e);
                    }
                }
            }

        } else {
            final long stamp = lock.writeLock();
            try {
                this.updateClientNature = updateClientNature;
                this.content = content;
                this.contentTypeHtml = contentTypeHtml;
                this.shared = shared;
            } finally {
                lock.unlockWrite(stamp);
            }
        }
    }

    /**
     * @param updateClientNature
     * @param sharedObjects
     *
     * @since 3.0.6
     */
    private void pushQueue(final UpdateClientNature updateClientNature,
            final List<AbstractHtml5SharedObject> sharedObjects) {

        final List<PushQueue> pushQueues = new ArrayList<>(
                sharedObjects.size());
        for (final AbstractHtml5SharedObject sharedObject : sharedObjects) {
            final PushQueue pushQueue = sharedObject
                    .getPushQueue(ACCESS_OBJECT);
            if (pushQueue != null) {
                pushQueues.add(pushQueue);
            }
        }

        if (pushQueues.size() > 1) {
            if (UpdateClientNature.ALLOW_ASYNC_PARALLEL
                    .equals(updateClientNature)) {
                for (final PushQueue pushQueue : pushQueues) {
                    CompletableFuture.runAsync(() -> pushQueue.push());
                }
            } else if (UpdateClientNature.ALLOW_PARALLEL
                    .equals(updateClientNature)) {
                pushQueues.parallelStream().forEach(PushQueue::push);
            } else {
                // UpdateClientNature.SEQUENTIAL.equals(updateClientNature)
                for (final PushQueue pushQueue : pushQueues) {
                    pushQueue.push();
                }
            }
        } else {
            for (final PushQueue pushQueue : pushQueues) {
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
     * @param formatter
     * @param subscribe
     *                          if true then updateClient will be true only if
     *                          activeWSListener is true otherwise updateClient
     *                          will be false at the time of content update.
     * @return the NoTag inserted
     * @since 3.0.6
     */
    AbstractHtml addInnerHtml(final boolean updateClient,
            final AbstractHtml applicableTag,
            final ContentFormatter<T> formatter, final boolean subscribe) {

        final ContentFormatter<T> cFormatter = formatter != null ? formatter
                : DEFAULT_CONTENT_FORMATTER;

        final long stamp = lock.writeLock();
        final AbstractHtml5SharedObject sharedObject = applicableTag
                .getSharedObject();

        boolean listenerInvoked = false;
        NoTag noTagInserted = null;
        try {

            final Content<T> contentLocal = new Content<>(content,
                    contentTypeHtml);

            NoTag noTag;
            try {
                final Content<String> formattedContent = cFormatter
                        .format(contentLocal);
                if (formattedContent != null) {
                    noTag = new NoTag(null, formattedContent.getContent(),
                            formattedContent.isContentTypeHtml());
                } else {
                    noTag = new NoTag(null, "", false);
                }

            } catch (final RuntimeException e) {
                noTag = new NoTag(null, "", false);
                LOGGER.log(Level.SEVERE,
                        "Exception while ContentFormatter.format", e);
            }

            final InnerHtmlListenerData listenerData = applicableTag
                    .addInnerHtmlsAndGetEventsLockless(updateClient, noTag);

            noTagInserted = noTag;

            final InsertedTagData<T> insertedTagData = new InsertedTagData<>(
                    ordinal, cFormatter, subscribe);

            // AtomicLong is not required as it is under lock
            ordinal++;
            insertedTags.put(noTag, insertedTagData);

            if (listenerData != null) {
                // TODO declare new innerHtmlsAdded for multiple parents after
                // verifying feasibility of considering rich notag content
                final ClientTasksWrapper clientTask = listenerData.listener()
                        .innerHtmlsAdded(applicableTag, listenerData.events());
                insertedTagData.lastClientTask(clientTask);
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
     * NB: Only for internal use
     *
     * @param insertedTag
     *                        instance of NoTag
     * @param parentTag
     *                        parent tag of NoTag
     * @return true if removed otherwise false
     * @since 3.0.6
     */
    boolean remove(final AbstractHtml insertedTag,
            final AbstractHtml parentTag) {
        final long stamp = lock.writeLock();
        try {

            final boolean removed = insertedTags.remove(insertedTag) != null;
            if (removed) {
                if (detachListeners != null) {
                    detachListeners.remove(parentTag);
                }
                if (contentChangeListeners != null) {
                    contentChangeListeners.remove(parentTag);
                }
            }
            return removed;
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    /**
     * @param noTag
     * @return true if the parent of this NoTag was added by
     *         AbstractHtml.subscribedTo method but it doesn't mean the NoTag is
     *         not changed from parent or parent is modified.
     * @since 3.0.6
     */
    boolean isSubscribed(final AbstractHtml noTag) {
        final long stamp = lock.readLock();
        try {
            final InsertedTagData<T> insertedTagData = insertedTags.get(noTag);
            return insertedTagData != null && insertedTagData.subscribed();
        } finally {
            lock.unlockRead(stamp);
        }
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
        List<Runnable> runnables = null;

        final long stamp = lock.writeLock();
        try {

            final Content<T> contentBefore = new Content<>(content,
                    contentTypeHtml);

            final Map<AbstractHtml5SharedObject, List<ParentNoTagData<T>>> tagsGroupedBySharedObject = new HashMap<>();

            for (final Entry<NoTag, InsertedTagData<T>> entry : insertedTags
                    .entrySet()) {
                if (entry == null) {
                    continue;
                }
                final NoTag prevNoTag = entry.getKey();
                if (prevNoTag == null) {
                    continue;
                }
                final InsertedTagData<T> insertedTagData = entry.getValue();
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

                List<ParentNoTagData<T>> dataList = tagsGroupedBySharedObject
                        .get(parentTag.getSharedObject());

                if (dataList == null) {
                    dataList = new ArrayList<>(4);
                    tagsGroupedBySharedObject.put(parentTag.getSharedObject(),
                            dataList);
                }

                // final NoTag noTag = new NoTag(null, content,
                // contentTypeHtml);
                // not inserting NoTag so need not pass

                dataList.add(new ParentNoTagData<>(prevNoTag, parentTag,
                        insertedTagData));
            }

            insertedTags.clear();

            final List<AbstractHtml> modifiedParents = new ArrayList<>(4);

            for (final Entry<AbstractHtml5SharedObject, List<ParentNoTagData<T>>> entry : tagsGroupedBySharedObject
                    .entrySet()) {

                final AbstractHtml5SharedObject sharedObject = entry.getKey();

                final List<ParentNoTagData<T>> parentNoTagDatas = entry
                        .getValue();

                // pushing using first parent object makes bug (got bug when
                // singleton SharedTagContent object is used under multiple
                // BrowserPage instances)
                // may be because the sharedObject in the parent can be changed
                // before lock

                final Lock parentLock = sharedObject.getLock(ACCESS_OBJECT)
                        .writeLock();
                parentLock.lock();

                try {

                    for (final ParentNoTagData<T> parentNoTagData : parentNoTagDatas) {

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
                                .previousNoTag();

                        if (parentNoTagData.parent().getSharedObject()
                                .equals(sharedObject)
                                && parentNoTagData.parent()
                                        .getChildrenSizeLockless() == 1
                                && !previousNoTag.isParentNullifiedOnce()) {

                            if (exclusionTags != null && exclusionTags
                                    .contains(parentNoTagData.parent())) {
                                insertedTags.put(
                                        parentNoTagData.previousNoTag(),
                                        parentNoTagData.insertedTagData());
                            } else {
                                modifiedParents.add(parentNoTagData.parent());

                                boolean updateClientTagSpecific = updateClient;
                                if (updateClient
                                        && exclusionClientUpdateTags != null
                                        && exclusionClientUpdateTags.contains(
                                                parentNoTagData.parent())) {
                                    updateClientTagSpecific = false;
                                }

                                // to get safety of lock it is executed before
                                // removeAllChildrenAndGetEventsLockless
                                // However lock safety is irrelevant here as the
                                // SharedTagContent will not reuse the same
                                // NoTag
                                previousNoTag.setSharedTagContent(null);

                                if (removeContent) {
                                    final ChildTagRemoveListenerData listenerData = parentNoTagData
                                            .parent()
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
                    final Set<DetachListener<T>> listeners = detachListeners
                            .get(modifiedParent);
                    if (listeners != null) {
                        runnables = new ArrayList<>(listeners.size());
                        for (final DetachListener<T> listener : listeners) {
                            final DetachEvent<T> detachEvent = new DetachEvent<>(
                                    modifiedParent, listener, contentBefore);
                            try {
                                final Runnable runnable = listener
                                        .detached(detachEvent);
                                if (runnable != null) {
                                    runnables.add(runnable);
                                }
                            } catch (final RuntimeException e) {
                                LOGGER.log(Level.SEVERE,
                                        "Exception while DetachListener.detached",
                                        e);
                            }
                        }

                    }
                }

            }
        } finally {
            lock.unlockWrite(stamp);
        }

        pushQueue(updateClientNature, sharedObjects);

        if (runnables != null) {
            for (final Runnable runnable : runnables) {
                try {
                    runnable.run();
                } catch (final RuntimeException e) {
                    LOGGER.log(Level.SEVERE,
                            "Exception while Runnable.run returned by DetachListener.detached",
                            e);
                }
            }
        }

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
            final ContentChangeListener<T> contentChangeListener) {
        final long stamp = lock.writeLock();

        try {
            final Set<ContentChangeListener<T>> listeners;
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
            final DetachListener<T> detachListener) {
        final long stamp = lock.writeLock();

        try {
            final Set<DetachListener<T>> listeners;
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
            final ContentChangeListener<T> contentChangeListener) {
        final long stamp = lock.writeLock();

        try {
            if (contentChangeListeners != null) {

                for (final Set<ContentChangeListener<T>> listeners : contentChangeListeners
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
            final ContentChangeListener<T> contentChangeListener) {
        final long stamp = lock.writeLock();

        try {
            if (contentChangeListeners != null) {
                final Set<ContentChangeListener<T>> listeners = contentChangeListeners
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
    public void removeDetachListener(final DetachListener<T> detachListener) {
        final long stamp = lock.writeLock();

        try {
            if (detachListeners != null) {

                for (final Set<DetachListener<T>> listeners : detachListeners
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
            final DetachListener<T> detachListener) {
        final long stamp = lock.writeLock();

        try {
            if (detachListeners != null) {
                final Set<DetachListener<T>> listeners = detachListeners
                        .get(tag);
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
            final Collection<ContentChangeListener<T>> contentChangeListeners) {

        final long stamp = lock.writeLock();

        try {
            if (this.contentChangeListeners != null) {

                final Set<ContentChangeListener<T>> listeners = this.contentChangeListeners
                        .get(tag);

                if (listeners != null) {
                    for (final ContentChangeListener<T> each : contentChangeListeners) {
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
            final Collection<DetachListener<T>> detachListeners) {

        final long stamp = lock.writeLock();

        try {
            if (this.detachListeners != null) {

                final Set<DetachListener<T>> listeners = this.detachListeners
                        .get(tag);

                if (listeners != null) {
                    for (final DetachListener<T> each : detachListeners) {
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

    /**
     * @param tag
     *                the tag whose ContentFormatter to be got.
     * @return the ContentFormatter object set for the given tag.
     * @since 3.0.11
     */
    public ContentFormatter<T> getContentFormatter(final AbstractHtml tag) {
        final AbstractHtml firstChild = tag.getFirstChild();
        if (firstChild != null) {
            final long stamp = lock.readLock();
            try {
                final InsertedTagData<T> insertedTagData = insertedTags
                        .get(firstChild);
                if (insertedTagData != null) {
                    return insertedTagData.formatter();
                }
            } finally {
                lock.unlockRead(stamp);
            }
        }
        return null;
    }

    /**
     * @param tag
     *                tag from which the listeners to be got.
     * @return the ContentChangeListeners for the given tag.
     * @since 3.0.11
     */
    public Set<ContentChangeListener<T>> getContentChangeListeners(
            final AbstractHtml tag) {
        final long stamp = lock.readLock();
        try {
            final Set<ContentChangeListener<T>> listeners = contentChangeListeners
                    .get(tag);
            if (listeners != null) {
                final Set<ContentChangeListener<T>> unmodifiableSet = Collections
                        .unmodifiableSet(listeners);
                return unmodifiableSet;
            }

        } finally {
            lock.unlockRead(stamp);
        }
        return null;
    }

    /**
     * @param tag
     *                tag from which the listeners to be got.
     * @return the DetachListeners for the given tag.
     * @since 3.0.11
     */
    public Set<DetachListener<T>> getDetachListeners(final AbstractHtml tag) {
        final long stamp = lock.readLock();
        try {
            final Set<DetachListener<T>> listeners = detachListeners.get(tag);
            if (listeners != null) {
                final Set<DetachListener<T>> unmodifiableSet = Collections
                        .unmodifiableSet(listeners);
                return unmodifiableSet;
            }
        } finally {
            lock.unlockRead(stamp);
        }
        return null;
    }

}
