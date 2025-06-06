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

import java.io.Serial;
import java.io.Serializable;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.webfirmframework.wffweb.internal.InternalId;
import com.webfirmframework.wffweb.internal.security.object.SecurityObject;
import com.webfirmframework.wffweb.internal.security.object.SharedTagContentSecurity;
import com.webfirmframework.wffweb.internal.tag.html.listener.PushQueue;
import com.webfirmframework.wffweb.server.page.ClientTasksWrapper;
import com.webfirmframework.wffweb.settings.WffConfiguration;
import com.webfirmframework.wffweb.tag.html.model.AbstractHtml5SharedObject;
import com.webfirmframework.wffweb.tag.htmlwff.NoTag;

/**
 * This class is highly thread-safe, so you can even declare a static object to
 * use under multiple threads. Changing the content of this object will be
 * reflected in all consuming tags if shared property of this object is true.
 * The shared property can be set as by passing constructor argument or by
 * setter method. Its default value is true if not explicitly specified.<br>
 * <br>
 * Usage Eg:-<br>
 *
 * <pre><code>
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
 * </code></pre>
 *
 * prints
 *
 * <pre><code>
 * &lt;div&gt;&lt;span&gt;Initial Content&lt;/span&gt;&lt;span&gt;Initial Content&lt;/span&gt;&lt;/div&gt;
 *
 * &lt;div&gt;&lt;span&gt;Content Changed&lt;/span&gt;&lt;span&gt;Content Changed&lt;/span&gt;&lt;/div&gt;
 * </code></pre>
 *
 * @param <T> class type of content in this SharedTagContent object
 * @author WFF
 * @since 3.0.6
 *
 */
public class SharedTagContent<T> {

    private static final Logger LOGGER = Logger.getLogger(SharedTagContent.class.getName());

    private static final SecurityObject ACCESS_OBJECT;

    private final ContentFormatter<T> DEFAULT_CONTENT_FORMATTER = content -> new Content<>(
            String.valueOf(content.content), content.contentTypeHtml);

    // NB Using ReentrantReadWriteLock causes
    // java.lang.IllegalMonitorStateException in production app
    private final transient StampedLock lock = new StampedLock();

    private final AtomicLong ordinal = new AtomicLong(0L);

    private final Map<NoTag, InsertedTagData<T>> insertedTags = new WeakHashMap<>(4, 0.75F);

    private final Queue<QueuedMethodCall<T>> methodCallQ = new ConcurrentLinkedQueue<>();

    private final Semaphore methodCallQLock = new Semaphore(1);

    volatile Map<InternalId, Set<ContentChangeListener<T>>> contentChangeListeners;

    volatile Map<InternalId, Set<DetachListener<T>>> detachListeners;

    private volatile Content<T> contentWithType = new Content<>(null, false);

    private volatile boolean shared = true;

    private volatile UpdateClientNature updateClientNature = UpdateClientNature.ALLOW_ASYNC_PARALLEL;

    private volatile boolean updateClient = true;

    private volatile boolean asyncUpdate;

    private volatile Executor executor;

    final Set<ApplicableTagGCTask<T>> applicableTagGCTasksCache = ConcurrentHashMap.newKeySet();

//    final Set<InsertedTagDataGCTask<T>> insertedTagDataGCTasksCache = Collections
//            .newSetFromMap(new ConcurrentHashMap<>());

    private final ReferenceQueue<? super AbstractHtml> tagGCTasksRQ = new ReferenceQueue<>();

//    private final ReferenceQueue<? super InsertedTagData<T>> insertedTagDataGCTasksRQ = new ReferenceQueue<>();

    /**
     * Represents the behavior of push operation of BrowserPage to client.
     * {@code ALLOW_ASYNC_PARALLEL} is the default in the {@code SharedTagContent}
     * object unless it is explicitly specified. In future after the arrival of Java
     * Virtual thread there will be two more types named
     * {@code ALLOW_VIRTUAL_PARALLEL} and {@code ALLOW_VIRTUAL_ASYNC_PARALLEL} and
     * {@code ALLOW_VIRTUAL_ASYNC_PARALLEL} may be the default.
     *
     */
    public static enum UpdateClientNature {

        /**
         * Allows parallel operation in the background for pushing changes to client
         * browser page.
         */
        ALLOW_ASYNC_PARALLEL,

        /**
         * Allows parallel operation but will wait for the push to finish to exit the
         * setContent method.
         */
        ALLOW_PARALLEL,

        /**
         * Does each browser page push operation one by one.
         */
        SEQUENTIAL;

        private UpdateClientNature() {
        }
    }

    /**
     * This is a serializable record class for handling content in
     * {@code SharedTagContent} object.
     *
     * @param <T>
     * @param content         the content to be embedded in the consumer tags.
     * @param contentTypeHtml true to treat the content as HTML when embedding in
     *                        the consumer tags otherwise false. Default value is
     *                        false.
     */
    public static final record Content<T> (T content, boolean contentTypeHtml) implements Serializable {

        @Serial
        private static final long serialVersionUID = 2L;

        /**
         * Note: {@code contentTypeHtml} will be set as {@code false}
         *
         * @param content the content to be embedded in the consumer tags as plain text.
         * @since 12.0.0-beta.3
         */
        public Content(final T content) {
            this(content, false);
        }

        /**
         * @return the content
         * @since 3.0.15
         */
        public T content() {
            return content;
        }

        /**
         * Denotes whether the content type to be treated as HTML when embedding in the
         * consumer tags.
         *
         * @return true or false
         * @since 3.0.15
         */
        public boolean contentTypeHtml() {
            return contentTypeHtml;
        }

    }

    private static interface QueuedMethodCall<T> {
    }

    private static record SetContentCall<T> (boolean updateClient, UpdateClientNature updateClientNature,
            Set<AbstractHtml> exclusionTags, T content, boolean contentTypeHtml, boolean shared)
            implements QueuedMethodCall<T> {
    }

    private static record DetachCall<T> (boolean removeContent, Set<AbstractHtml> exclusionTags,
            Set<AbstractHtml> exclusionClientUpdateTags) implements QueuedMethodCall<T> {
    }

    private static record AddContentChangeListenerCall<T> (AbstractHtml tag,
            ContentChangeListener<T> contentChangeListener) implements QueuedMethodCall<T> {

    }

    private static record AddDetachListenerCall<T> (AbstractHtml tag, DetachListener<T> detachListener)
            implements QueuedMethodCall<T> {

    }

    private static record RemoveContentChangeListenerCall<T> (ContentChangeListener<T> contentChangeListener)
            implements QueuedMethodCall<T> {

    }

    private static record RemoveContentChangeListenerCall2<T> (AbstractHtml tag,
            ContentChangeListener<T> contentChangeListener) implements QueuedMethodCall<T> {

    }

    private static record RemoveDetachListenerCall<T> (DetachListener<T> detachListener)
            implements QueuedMethodCall<T> {

    }

    private static record RemoveDetachListenerCall2<T> (AbstractHtml tag, DetachListener<T> detachListener)
            implements QueuedMethodCall<T> {

    }

    private static record RemoveContentChangeListenersCall<T> (AbstractHtml tag,
            Collection<ContentChangeListener<T>> contentChangeListeners) implements QueuedMethodCall<T> {

    }

    private static record RemoveDetachListenersCall<T> (AbstractHtml tag, Collection<DetachListener<T>> detachListeners)
            implements QueuedMethodCall<T> {

    }

    private static record RemoveAllContentChangeListenersCall<T> (AbstractHtml tag) implements QueuedMethodCall<T> {

    }

    private static record RemoveAllContentChangeListenersNoArgCall<T> () implements QueuedMethodCall<T> {

    }

    private static record RemoveAllDetachListenersCall<T> (AbstractHtml tag) implements QueuedMethodCall<T> {

    }

    private static record RemoveAllDetachListenersNoArgCall<T> () implements QueuedMethodCall<T> {

    }

    private static record SetUpdateClientNatureCall<T> (UpdateClientNature updateClientNature)
            implements QueuedMethodCall<T> {

    }

    private static record SetUpdateClientCall<T> (boolean updateClient) implements QueuedMethodCall<T> {

    }

    private static record SetSharedCall<T> (boolean shared) implements QueuedMethodCall<T> {

    }

    private static record SetExecutorCall<T> (Executor executor) implements QueuedMethodCall<T> {

    }

    private static record RemoveNoTagCall<T> (AbstractHtml insertedTag, AbstractHtml parentTag)
            implements QueuedMethodCall<T> {

    }

    private static record AddNoTagCall<T> (NoTag noTag, InsertedTagData<T> insertedTagData)
            implements QueuedMethodCall<T> {

    }

    private static record RemoveListenersCall<T> (InternalId tagId) implements QueuedMethodCall<T> {

    }

    private static record ReadStateCall<T> (StateConsumer<T> stateConsumer) implements QueuedMethodCall<T> {

    }

    @FunctionalInterface
    public static interface ContentFormatter<T> {
        public abstract Content<String> format(final Content<T> content);
    }

    public static final record ChangeEvent<T> (AbstractHtml sourceTag, ContentChangeListener<T> sourceListener,
            Content<T> contentBefore, Content<T> contentAfter, Content<String> contentApplied,
            ContentFormatter<T> formatter) {

        /**
         * @return the source tag
         * @since 3.0.15
         */
        public AbstractHtml sourceTag() {
            return sourceTag;
        }

        /**
         * @return the sourceListener
         * @since 3.0.15
         */
        public ContentChangeListener<T> sourceListener() {
            return sourceListener;
        }

        /**
         * @return the content before change event
         * @since 3.0.15
         */
        public Content<T> contentBefore() {
            return contentBefore;
        }

        /**
         * @return the content after the change event
         * @since 3.0.15
         */
        public Content<T> contentAfter() {
            return contentAfter;
        }

        /**
         * @return the applied content on tag
         * @since 3.0.15
         */
        public Content<String> contentApplied() {
            return contentApplied;
        }

        /**
         * @return the formatter
         * @since 3.0.15
         */
        public ContentFormatter<T> formatter() {
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
         * NB: Do not call any methods of this SharedTagContent inside contentChanged
         * method, instead write it inside the returning Runnable object (Runnable.run).
         *
         * @param changeEvent
         * @return Write code to run after contentChanged invoked. It doesn't guarantee
         *         the order of execution as the order of contentChanged method
         *         execution. This is just like a post function for this method. If any
         *         methods of this SharedTagContent object to be called it must be
         *         written inside this returning Runnable object (Runnable.run).
         */
        public abstract Runnable contentChanged(final ChangeEvent<T> changeEvent);
    }

    public static final record DetachEvent<T> (AbstractHtml sourceTag, DetachListener<T> sourceListener,
            Content<T> content) {
    }

    public static final record StateEvent<T> (Content<T> content, boolean shared, UpdateClientNature updateClientNature,
            boolean updateClient, boolean asyncUpdate) {
    }

    /**
     * @author WFF
     * @since 3.0.6
     *
     */
    @FunctionalInterface
    public static interface DetachListener<T> {
        /**
         * NB: Do not call any methods of this SharedTagContent inside detached, instead
         * write it inside the returning Runnable object (Runnable.run).
         *
         * @param detachEvent
         * @return Write code to run after detached invoked. It doesn't guarantee the
         *         order of execution as the order of detached method execution. This is
         *         just like a post function for this method. If any methods of this
         *         SharedTagContent object to be called it must be written inside this
         *         returning Runnable object (Runnable.run).
         */
        public abstract Runnable detached(final DetachEvent<T> detachEvent);
    }

    /**
     * @param <T> the content data type
     * @since 12.0.2
     */
    @FunctionalInterface
    public static interface StateConsumer<T> {

        public abstract void accept(final StateEvent<T> stateEvent);

    }

    /**
     * Note: Only for internal use.
     *
     */
    // for security purpose, the class name should not be modified
    private static final class Security {
        private Security() {
            if (ACCESS_OBJECT != null) {
                throw new AssertionError("Not allowed to call this constructor");
            }
        }
    }

    static {
        ACCESS_OBJECT = new SharedTagContentSecurity(new Security());
    }

    /**
     * @param executor           the executor object for async push or null if no
     *                           preference. This executor object will be used only
     *                           if the {@code updateClientNature} is
     *                           {@link UpdateClientNature#ALLOW_ASYNC_PARALLEL} or
     *                           {@link UpdateClientNature#ALLOW_PARALLEL}.
     *
     *                           <br>
     *                           NB: You may need only one copy of executor object
     *                           for all sharedTagContent instances in the project.
     *                           So it could be declared as a static final object.
     *                           Eg: <br>
     *
     * <pre><code>
     * public static final Executor EXECUTOR = Executors.newCachedThreadPool();
     * sharedTagContent.setExecutor(EXECUTOR);
     * </code></pre>
     * When Java releases Virtual Thread we may be able to use as follows
     * <pre><code>
     * public static final Executor EXECUTOR = Executors.newVirtualThreadExecutor();
     * sharedTagContent.setExecutor(EXECUTOR);
     * </code></pre>
     *
     * @param updateClientNature
     *
     *                           If this SharedTagContent object has to update
     *                           content of tags from multiple BrowserPage
     *                           instances, UpdateClientNature.ALLOW_ASYNC_PARALLEL
     *                           will allow parallel operation in the background for
     *                           pushing changes to client browser page and
     *                           UpdateClientNature.ALLOW_PARALLEL will allow
     *                           parallel operation but will wait for the push to
     *                           finish to exit the setContent method.
     *                           UpdateClientNature.SEQUENTIAL will sequentially do
     *                           each browser page push operation.
     * @param shared             true to share its content across all consuming tags
     *                           when {@link SharedTagContent#setContent} is called.
     * @param content            the content to embed in the consumer tags.
     * @param contentTypeHtml    true to treat the given content as HTML otherwise
     *                           false.
     * @since 3.0.15
     */
    public SharedTagContent(final Executor executor, final UpdateClientNature updateClientNature, final boolean shared,
            final T content, final boolean contentTypeHtml) {
        if (updateClientNature != null) {
            this.updateClientNature = updateClientNature;
        }
        this.shared = shared;
        this.executor = executor;
        this.contentWithType = new Content<>(content, contentTypeHtml);
    }

    /**
     * or null if no preference.
     *
     * @param updateClientNature
     *
     *                           If this SharedTagContent object has to update
     *                           content of tags from multiple BrowserPage
     *                           instances, UpdateClientNature.ALLOW_ASYNC_PARALLEL
     *                           will allow parallel operation in the background for
     *                           pushing changes to client browser page and
     *                           UpdateClientNature.ALLOW_PARALLEL will allow
     *                           parallel operation but will wait for the push to
     *                           finish to exit the setContent method.
     *                           UpdateClientNature.SEQUENTIAL will sequentially do
     *                           each browser page push operation.
     * @param shared             true to share its content across all consuming tags
     *                           when {@link SharedTagContent#setContent} is called.
     * @param content            the content to embed in the consumer tags.
     * @param contentTypeHtml    true to treat the given content as HTML otherwise
     *                           false.
     * @since 3.0.15
     */
    public SharedTagContent(final UpdateClientNature updateClientNature, final boolean shared, final T content,
            final boolean contentTypeHtml) {
        this(null, updateClientNature, shared, content, contentTypeHtml);
    }

    /**
     * plain text content with updateClientNature as
     * UpdateClientNature.ALLOW_ASYNC_PARALLEL.
     *
     * @param executor the executor object for async push or null if no preference.
     *                 This executor object will be used only if the
     *                 {@code updateClientNature} is
     *                 {@link UpdateClientNature#ALLOW_ASYNC_PARALLEL} or
     *                 {@link UpdateClientNature#ALLOW_PARALLEL}.
     *
     *                 <br>
     *                 NB: You may need only one copy of executor object for all
     *                 sharedTagContent instances in the project. So it could be
     *                 declared as a static final object. Eg: <br>
     *
     * <pre><code>
     * public static final Executor EXECUTOR = Executors.newCachedThreadPool();
     * sharedTagContent.setExecutor(EXECUTOR);
     * </code></pre>
     * When Java releases Virtual Thread we may be able to use as follows
     * <pre><code>
     * public static final Executor EXECUTOR = Executors.newVirtualThreadExecutor();
     * sharedTagContent.setExecutor(EXECUTOR);
     * </code></pre>
     *
     * @param content  the content its content type will be considered as plain
     *                 text, i.e. contentTypeHtml will be false.
     * @since 3.0.15
     */
    public SharedTagContent(final Executor executor, final T content) {
        this(executor, null, true, content, false);
    }

    /**
     * plain text content with updateClientNature as
     * UpdateClientNature.ALLOW_ASYNC_PARALLEL.
     *
     * @param content the content its content type will be considered as plain text,
     *                i.e. contentTypeHtml will be false.
     * @since 3.0.6
     */
    public SharedTagContent(final T content) {
        this(null, null, true, content, false);
    }

    /**
     * @param updateClientNature
     *
     *                           If this SharedTagContent object has to update
     *                           content of tags from multiple BrowserPage
     *                           instances, UpdateClientNature.ALLOW_ASYNC_PARALLEL
     *                           will allow parallel operation in the background for
     *                           pushing changes to client browser page and
     *                           UpdateClientNature.ALLOW_PARALLEL will allow
     *                           parallel operation but will wait for the push to
     *                           finish to exit the setContent method.
     *                           UpdateClientNature.SEQUENTIAL will sequentially do
     *                           each browser page push operation.
     * @param content            the content its content type will be considered as
     *                           plain text, i.e. contentTypeHtml will be false.
     * @since 3.0.6
     */
    public SharedTagContent(final UpdateClientNature updateClientNature, final T content) {
        this(null, updateClientNature, true, content, false);
    }

    /**
     * @param executor           the executor object for async push or null if no
     *                           preference. This executor object will be used only
     *                           if the {@code updateClientNature} is
     *                           {@link UpdateClientNature#ALLOW_ASYNC_PARALLEL} or
     *                           {@link UpdateClientNature#ALLOW_PARALLEL}.
     *
     *                           <br>
     *                           NB: You may need only one copy of executor object
     *                           for all sharedTagContent instances in the project.
     *                           So it could be declared as a static final object.
     *                           Eg: <br>
     *
     * <pre><code>
     * public static final Executor EXECUTOR = Executors.newCachedThreadPool();
     * sharedTagContent.setExecutor(EXECUTOR);
     * </code></pre>
     * When Java releases Virtual Thread we may be able to use as follows
     * <pre><code>
     * public static final Executor EXECUTOR = Executors.newVirtualThreadExecutor();
     * sharedTagContent.setExecutor(EXECUTOR);
     * </code></pre>
     *
     * @param updateClientNature
     *
     *                           If this SharedTagContent object has to update
     *                           content of tags from multiple BrowserPage
     *                           instances, UpdateClientNature.ALLOW_ASYNC_PARALLEL
     *                           will allow parallel operation in the background for
     *                           pushing changes to client browser page and
     *                           UpdateClientNature.ALLOW_PARALLEL will allow
     *                           parallel operation but will wait for the push to
     *                           finish to exit the setContent method.
     *                           UpdateClientNature.SEQUENTIAL will sequentially do
     *                           each browser page push operation.
     * @param content            the content its content type will be considered as
     *                           plain text, i.e. contentTypeHtml will be false.
     * @since 3.0.15
     */
    public SharedTagContent(final Executor executor, final UpdateClientNature updateClientNature, final T content) {
        this(executor, updateClientNature, true, content, false);
    }

    /**
     * The default value of updateClientNature is
     * UpdateClientNature.ALLOW_ASYNC_PARALLEL.
     *
     * @param content         the content to embed in the consumer tags.
     * @param contentTypeHtml true to treat the given content as HTML otherwise
     *                        false.
     * @since 3.0.6
     */
    public SharedTagContent(final T content, final boolean contentTypeHtml) {
        this(null, null, true, content, contentTypeHtml);
    }

    /**
     * The default value of updateClientNature is
     * UpdateClientNature.ALLOW_ASYNC_PARALLEL.
     *
     * @param executor        the executor object for async push or null if no
     *                        preference. This executor object will be used only if
     *                        the {@code updateClientNature} is
     *                        {@link UpdateClientNature#ALLOW_ASYNC_PARALLEL} or
     *                        {@link UpdateClientNature#ALLOW_PARALLEL}.
     *
     *                        <br>
     *                        NB: You may need only one copy of executor object for
     *                        all sharedTagContent instances in the project. So it
     *                        could be declared as a static final object. Eg: <br>
     * <pre><code>
     * public static final Executor EXECUTOR = Executors.newCachedThreadPool();
     * sharedTagContent.setExecutor(EXECUTOR);
     * </code></pre>
     * When Java releases Virtual Thread we may be able to use as follows
     * <pre><code>
     * public static final Executor EXECUTOR = Executors.newVirtualThreadExecutor();
     * sharedTagContent.setExecutor(EXECUTOR);
     * </code></pre>
     *
     * @param content         the content to embed in the consumer tags.
     * @param contentTypeHtml true to treat the given content as HTML otherwise
     *                        false.
     * @since 3.0.15
     */
    public SharedTagContent(final Executor executor, final T content, final boolean contentTypeHtml) {
        this(executor, null, true, content, contentTypeHtml);
    }

    /**
     * @param updateClientNature
     *
     *                           If this SharedTagContent object has to update
     *                           content of tags from multiple BrowserPage
     *                           instances, UpdateClientNature.ALLOW_ASYNC_PARALLEL
     *                           will allow parallel operation in the background for
     *                           pushing changes to client browser page and
     *                           UpdateClientNature.ALLOW_PARALLEL will allow
     *                           parallel operation but will wait for the push to
     *                           finish to exit the setContent method.
     *                           UpdateClientNature.SEQUENTIAL will sequentially do
     *                           each browser page push operation.
     * @param content            the content to embed in the consumer tags.
     * @param contentTypeHtml    true to treat the given content as HTML otherwise
     *                           false.
     * @since 3.0.6
     */
    public SharedTagContent(final UpdateClientNature updateClientNature, final T content,
            final boolean contentTypeHtml) {
        this(null, updateClientNature, true, content, contentTypeHtml);
    }

    /**
     * @param executor           the executor object for async push or null if no
     *                           preference. This executor object will be used only
     *                           if the {@code updateClientNature} is
     *                           {@link UpdateClientNature#ALLOW_ASYNC_PARALLEL} or
     *                           {@link UpdateClientNature#ALLOW_PARALLEL}.
     *
     *                           <br>
     *                           NB: You may need only one copy of executor object
     *                           for all sharedTagContent instances in the project.
     *                           So it could be declared as a static final object.
     *                           Eg: <br>
     *
     * <pre><code>
     * public static final Executor EXECUTOR = Executors.newCachedThreadPool();
     * sharedTagContent.setExecutor(EXECUTOR);
     * </code></pre>
     * When Java releases Virtual Thread we may be able to use as follows
     * <pre><code>
     * public static final Executor EXECUTOR = Executors.newVirtualThreadExecutor();
     * sharedTagContent.setExecutor(EXECUTOR);
     * </code></pre>
     *
     * @param updateClientNature
     *
     *                           If this SharedTagContent object has to update
     *                           content of tags from multiple BrowserPage
     *                           instances, UpdateClientNature.ALLOW_ASYNC_PARALLEL
     *                           will allow parallel operation in the background for
     *                           pushing changes to client browser page and
     *                           UpdateClientNature.ALLOW_PARALLEL will allow
     *                           parallel operation but will wait for the push to
     *                           finish to exit the setContent method.
     *                           UpdateClientNature.SEQUENTIAL will sequentially do
     *                           each browser page push operation.
     * @param content            the content to embed in the consumer tags.
     * @param contentTypeHtml    true to treat the given content as HTML otherwise
     *                           false.
     * @since 3.0.15
     */
    public SharedTagContent(final Executor executor, final UpdateClientNature updateClientNature, final T content,
            final boolean contentTypeHtml) {
        this(executor, updateClientNature, true, content, contentTypeHtml);
    }

    /**
     * @param updateClientNature
     *
     *                           If this SharedTagContent object has to update
     *                           content of tags from multiple BrowserPage
     *                           instances, UpdateClientNature.ALLOW_ASYNC_PARALLEL
     *                           will allow parallel operation in the background for
     *                           pushing changes to client browser page and
     *                           UpdateClientNature.ALLOW_PARALLEL will allow
     *                           parallel operation but will wait for the push to
     *                           finish to exit the setContent method.
     *                           UpdateClientNature.SEQUENTIAL will sequentially do
     *                           each browser page push operation.
     * @param shared             true to share its content across all consuming tags
     *                           when {@link SharedTagContent#setContent} is called.
     * @param content            the content which will be treated as plain text in
     *                           the consumer tags.
     *
     * @since 3.0.15
     */
    public SharedTagContent(final UpdateClientNature updateClientNature, final boolean shared, final T content) {
        this(null, updateClientNature, shared, content, false);
    }

    /**
     * @param executor           the executor object for async push or null if no
     *                           preference. This executor object will be used only
     *                           if the {@code updateClientNature} is
     *                           {@link UpdateClientNature#ALLOW_ASYNC_PARALLEL} or
     *                           {@link UpdateClientNature#ALLOW_PARALLEL}.
     *
     *                           <br>
     *                           NB: You may need only one copy of executor object
     *                           for all sharedTagContent instances in the project.
     *                           So it could be declared as a static final object.
     *                           Eg: <br>
     *
     * <pre><code>
     * public static final Executor EXECUTOR = Executors.newCachedThreadPool();
     * sharedTagContent.setExecutor(EXECUTOR);
     * </code></pre>
     * When Java releases Virtual Thread we may be able to use as follows
     * <pre><code>
     * public static final Executor EXECUTOR = Executors.newVirtualThreadExecutor();
     * sharedTagContent.setExecutor(EXECUTOR);
     * </code></pre>
     *
     * @param updateClientNature
     *
     *                           If this SharedTagContent object has to update
     *                           content of tags from multiple BrowserPage
     *                           instances, UpdateClientNature.ALLOW_ASYNC_PARALLEL
     *                           will allow parallel operation in the background for
     *                           pushing changes to client browser page and
     *                           UpdateClientNature.ALLOW_PARALLEL will allow
     *                           parallel operation but will wait for the push to
     *                           finish to exit the setContent method.
     *                           UpdateClientNature.SEQUENTIAL will sequentially do
     *                           each browser page push operation.
     * @param shared             true to share its content across all consuming tags
     *                           when {@link SharedTagContent#setContent} is called.
     * @param content            the content which will be treated as plain text in
     *                           the consumer tags.
     *
     * @since 3.0.15
     */
    public SharedTagContent(final Executor executor, final UpdateClientNature updateClientNature, final boolean shared,
            final T content) {
        this(executor, updateClientNature, shared, content, false);
    }

    /**
     * @param shared          true to share its content across all consuming tags
     *                        when {@link SharedTagContent#setContent} is called.
     * @param content         the content to embed in the consumer tags.
     * @param contentTypeHtml true to treat the given content as HTML otherwise
     *                        false.
     * @since 3.0.6
     */
    public SharedTagContent(final boolean shared, final T content, final boolean contentTypeHtml) {
        this(null, null, shared, content, contentTypeHtml);
    }

    /**
     * @param executor        the executor object for async push or null if no
     *                        preference. This executor object will be used only if
     *                        the {@code updateClientNature} is
     *                        {@link UpdateClientNature#ALLOW_ASYNC_PARALLEL} or
     *                        {@link UpdateClientNature#ALLOW_PARALLEL}.
     *
     *                        <br>
     *                        NB: You may need only one copy of executor object for
     *                        all sharedTagContent instances in the project. So it
     *                        could be declared as a static final object. Eg: <br>
     *
     * <pre><code>
     * public static final Executor EXECUTOR = Executors.newCachedThreadPool();
     * sharedTagContent.setExecutor(EXECUTOR);
     * </code></pre>
     * When Java releases Virtual Thread we may be able to use as follows
     * <pre><code>
     * public static final Executor EXECUTOR = Executors.newVirtualThreadExecutor();
     * sharedTagContent.setExecutor(EXECUTOR);
     * </code></pre>
     *
     * @param shared          true to share its content across all consuming tags
     *                        when {@link SharedTagContent#setContent} is called.
     * @param content         the content to embed in the consumer tags.
     * @param contentTypeHtml true to treat the given content as HTML otherwise
     *                        false.
     * @since 3.0.15
     */
    public SharedTagContent(final Executor executor, final boolean shared, final T content,
            final boolean contentTypeHtml) {
        this(executor, null, shared, content, contentTypeHtml);
    }

    /**
     * @param shared  true to share its content across all consuming tags when
     *                {@link SharedTagContent#setContent} is called.
     * @param content the content which will be treated as plain text in the
     *                consumer tags.
     * @since 3.0.6
     */
    public SharedTagContent(final boolean shared, final T content) {
        this(null, null, shared, content, false);
    }

    /**
     * @param executor the executor object for async push or null if no preference.
     *                 This executor object will be used only if the
     *                 {@code updateClientNature} is
     *                 {@link UpdateClientNature#ALLOW_ASYNC_PARALLEL} or
     *                 {@link UpdateClientNature#ALLOW_PARALLEL}.
     *
     *                 <br>
     *                 NB: You may need only one copy of executor object for all
     *                 sharedTagContent instances in the project. So it could be
     *                 declared as a static final object. Eg: <br>
     *
     * <pre><code>
     * public static final Executor EXECUTOR = Executors.newCachedThreadPool();
     * sharedTagContent.setExecutor(EXECUTOR);
     * </code></pre>
     * When Java releases Virtual Thread we may be able to use as follows
     * <pre><code>
     * public static final Executor EXECUTOR = Executors.newVirtualThreadExecutor();
     * sharedTagContent.setExecutor(EXECUTOR);
     * </code></pre>
     *
     * @param shared   true to share its content across all consuming tags when
     *                 {@link SharedTagContent#setContent} is called.
     * @param content  the content which will be treated as plain text in the
     *                 consumer tags.
     * @since 3.0.15
     */
    public SharedTagContent(final Executor executor, final boolean shared, final T content) {
        this(executor, null, shared, content, false);
    }

    /**
     * @return the content
     * @since 3.0.6
     */
    public T getContent() {
        return contentWithType.content;
    }

    /**
     * @return true if content type is HTML
     * @since 3.0.6
     */
    public boolean isContentTypeHtml() {
        return contentWithType.contentTypeHtml;
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
     * Refer {@link #setUpdateClientNature(UpdateClientNature)} for more details.
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
     *                           If this SharedTagContent object has to update
     *                           content of tags from multiple BrowserPage
     *                           instances, UpdateClientNature.ALLOW_ASYNC_PARALLEL
     *                           will allow parallel operation in the background for
     *                           pushing changes to client browser page and
     *                           UpdateClientNature.ALLOW_PARALLEL will allow
     *                           parallel operation but will wait for the push to
     *                           finish to exit the setContent method.
     *                           UpdateClientNature.SEQUENTIAL will sequentially do
     *                           each browser page push operation.
     * @since 3.0.6
     */
    public void setUpdateClientNature(final UpdateClientNature updateClientNature) {
        methodCallQ.add(new SetUpdateClientNatureCall<>(updateClientNature));
        executeMethodCallsFromQ();
    }

    /**
     * @param updateClientNature
     *
     *                           If this SharedTagContent object has to update
     *                           content of tags from multiple BrowserPage
     *                           instances, UpdateClientNature.ALLOW_ASYNC_PARALLEL
     *                           will allow parallel operation in the background for
     *                           pushing changes to client browser page and
     *                           UpdateClientNature.ALLOW_PARALLEL will allow
     *                           parallel operation but will wait for the push to
     *                           finish to exit the setContent method.
     *                           UpdateClientNature.SEQUENTIAL will sequentially do
     *                           each browser page push operation.
     * @since 3.0.6
     */
    private void setUpdateClientNatureForQ(final UpdateClientNature updateClientNature) {

        if (updateClientNature != null && !updateClientNature.equals(this.updateClientNature)) {
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
     * @return the Executor object.
     * @since 3.0.15
     */
    public Executor getExecutor() {
        return executor;
    }

    /**
     * @param updateClient true to turn on updating client browser page. By default
     *                     it is true.
     * @since 3.0.6
     */
    public void setUpdateClient(final boolean updateClient) {
        methodCallQ.add(new SetUpdateClientCall<>(updateClient));
        executeMethodCallsFromQ();
    }

    /**
     * @param updateClient true to turn on updating client browser page. By default
     *                     it is true.
     * @since 3.0.6
     */
    private void setUpdateClientForQ(final boolean updateClient) {
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
     * @param shared true to make content of this SharedTagContent object across all
     *               consuming tags while
     *               {@link SharedTagContent#setContent(Object)} is called.
     * @since 3.0.6
     */
    public void setShared(final boolean shared) {
        methodCallQ.add(new SetSharedCall<>(shared));
        executeMethodCallsFromQ();
    }

    /**
     * @param shared true to make content of this SharedTagContent object across all
     *               consuming tags while
     *               {@link SharedTagContent#setContent(Object)} is called.
     * @since 3.0.6
     */
    private void setSharedForQ(final boolean shared) {
        if (this.shared != shared) {
            final long stamp = lock.writeLock();
            try {
                this.shared = shared;
            } finally {
                lock.unlockWrite(stamp);
            }
        }
    }

    /**
     * @param executor the executor object for async push or null if no preference.
     *                 This executor object will be used only if the
     *                 {@code updateClientNature} is
     *                 {@link UpdateClientNature#ALLOW_ASYNC_PARALLEL} or
     *                 {@link UpdateClientNature#ALLOW_PARALLEL}.
     *
     *                 <br>
     *                 NB: You may need only one copy of executor object for all
     *                 sharedTagContent instances in the project. So it could be
     *                 declared as a static final object. Eg: <br>
     * <pre><code>
     * public static final Executor EXECUTOR = Executors.newCachedThreadPool();
     * sharedTagContent.setExecutor(EXECUTOR);
     * </code></pre>
     * When Java releases Virtual Thread we may be able to use as follows
     * <pre><code>
     * public static final Executor EXECUTOR = Executors.newVirtualThreadExecutor();
     * sharedTagContent.setExecutor(EXECUTOR);
     * </code></pre>
     *
     * @since 3.0.15
     */
    public void setExecutor(final Executor executor) {
        methodCallQ.add(new SetExecutorCall<>(executor));
        executeMethodCallsFromQ();
    }

    /**
     * @param executor the executor object for async push or null if no preference.
     *                 This executor object will be used only if the
     *                 {@code updateClientNature} is
     *                 {@link UpdateClientNature#ALLOW_ASYNC_PARALLEL} or
     *                 {@link UpdateClientNature#ALLOW_PARALLEL}.
     *
     *                 <br>
     *                 NB: You may need only one copy of executor object for all
     *                 sharedTagContent instances in the project. So it could be
     *                 declared as a static final object. Eg: <br>
     * <pre><code>
     * public static final Executor EXECUTOR = Executors.newCachedThreadPool();
     * sharedTagContent.setExecutor(EXECUTOR);
     * </code></pre>
     * When Java releases Virtual Thread we may be able to use as follows
     * <pre><code>
     * public static final Executor EXECUTOR = Executors.newVirtualThreadExecutor();
     * sharedTagContent.setExecutor(EXECUTOR);
     * </code></pre>
     *
     * @since 3.0.15
     */
    private void setExecutorForQ(final Executor executor) {
        if (this.executor != executor) {
            final long stamp = lock.writeLock();
            try {
                this.executor = executor;
            } finally {
                lock.unlockWrite(stamp);
            }
        }

    }

    /**
     * @param noTag
     * @return true if the object just exists in the set, but it doesn't mean the
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
     * @param content content to be reflected in all consuming tags.
     * @since 3.0.6
     */
    public void setContent(final T content) {
        setContent(updateClient, updateClientNature, null, content, contentWithType.contentTypeHtml);
    }

    /**
     * @param updateClientNature
     *
     *                           If this SharedTagContent object has to update
     *                           content of tags from multiple BrowserPage
     *                           instances, UpdateClientNature.ALLOW_ASYNC_PARALLEL
     *                           will allow parallel operation in the background for
     *                           pushing changes to client browser page and
     *                           UpdateClientNature.ALLOW_PARALLEL will allow
     *                           parallel operation but will wait for the push to
     *                           finish to exit the setContent method.
     *                           UpdateClientNature.SEQUENTIAL will sequentially do
     *                           each browser page push operation.
     * @param content            content to be reflected in all consuming tags.
     * @since 3.0.6
     */
    public void setContent(final UpdateClientNature updateClientNature, final T content) {
        setContent(updateClient, (updateClientNature != null ? updateClientNature : this.updateClientNature), null,
                content, contentWithType.contentTypeHtml);
    }

    /**
     * @param content         content to be reflected in all consuming tags
     * @param contentTypeHtml true if the content type is HTML or false if plain
     *                        text
     * @since 3.0.6
     */
    public void setContent(final T content, final boolean contentTypeHtml) {
        setContent(updateClient, updateClientNature, null, content, contentTypeHtml);
    }

    /**
     * @param updateClientNature
     *
     *                           If this SharedTagContent object has to update
     *                           content of tags from multiple BrowserPage
     *                           instances, UpdateClientNature.ALLOW_ASYNC_PARALLEL
     *                           will allow parallel operation in the background for
     *                           pushing changes to client browser page and
     *                           UpdateClientNature.ALLOW_PARALLEL will allow
     *                           parallel operation but will wait for the push to
     *                           finish to exit the setContent method.
     *                           UpdateClientNature.SEQUENTIAL will sequentially do
     *                           each browser page push operation.
     * @param content            content to be reflected in all consuming tags
     * @param contentTypeHtml    true if the content type is HTML or false if plain
     *                           text
     * @since 3.0.6
     */
    public void setContent(final UpdateClientNature updateClientNature, final T content,
            final boolean contentTypeHtml) {
        setContent(updateClient, (updateClientNature != null ? updateClientNature : this.updateClientNature), null,
                content, contentTypeHtml);
    }

    /**
     * @param exclusionTags tags to be excluded only from client update. It means
     *                      that the content of all consumer tags will be kept
     *                      updated at server side so their content formatter and
     *                      change listeners will be invoked.
     * @param content
     * @since 3.0.6
     */
    public void setContent(final Set<AbstractHtml> exclusionTags, final T content) {
        setContent(updateClient, updateClientNature, exclusionTags, content, contentWithType.contentTypeHtml);
    }

    /**
     * @param exclusionTags   tags to be excluded only from client update. It means
     *                        that the content of all consumer tags will be kept
     *                        updated at server side so their content formatter and
     *                        change listeners will be invoked.
     * @param content
     * @param contentTypeHtml
     * @since 3.0.6
     */
    public void setContent(final Set<AbstractHtml> exclusionTags, final T content, final boolean contentTypeHtml) {
        setContent(updateClient, updateClientNature, exclusionTags, content, contentTypeHtml);
    }

    /**
     * @param updateClient
     * @param updateClientNature
     *
     *                           If this SharedTagContent object has to update
     *                           content of tags from multiple BrowserPage
     *                           instances, UpdateClientNature.ALLOW_ASYNC_PARALLEL
     *                           will allow parallel operation in the background for
     *                           pushing changes to client browser page and
     *                           UpdateClientNature.ALLOW_PARALLEL will allow
     *                           parallel operation but will wait for the push to
     *                           finish to exit the setContent method.
     *                           UpdateClientNature.SEQUENTIAL will sequentially do
     *                           each browser page push operation.
     * @param exclusionTags      tags to be excluded only from client update. It
     *                           means that the content of all consumer tags will be
     *                           kept updated at server side so their content
     *                           formatter and change listeners will be invoked.
     * @param content
     * @param contentTypeHtml
     */
    private void setContent(final boolean updateClient, final UpdateClientNature updateClientNature,
            final Set<AbstractHtml> exclusionTags, final T content, final boolean contentTypeHtml) {
        setContent(updateClient, updateClientNature, exclusionTags, content, contentTypeHtml, shared);
    }

    /**
     * @param updateClient       true or false
     * @param updateClientNature
     *
     *                           If this SharedTagContent object has to update
     *                           content of tags from multiple BrowserPage
     *                           instances, UpdateClientNature.ALLOW_ASYNC_PARALLEL
     *                           will allow parallel operation in the background for
     *                           pushing changes to client browser page and
     *                           UpdateClientNature.ALLOW_PARALLEL will allow
     *                           parallel operation but will wait for the push to
     *                           finish to exit the setContent method.
     *                           UpdateClientNature.SEQUENTIAL will sequentially do
     *                           each browser page push operation.
     * @param exclusionTags      tags to be excluded only from client update. It
     *                           means that the content of all consumer tags will be
     *                           kept updated at server side so their content
     *                           formatter and change listeners will be invoked.
     * @param content
     * @param contentTypeHtml
     * @param shared
     */
    private void setContent(final boolean updateClient, final UpdateClientNature updateClientNature,
            final Set<AbstractHtml> exclusionTags, final T content, final boolean contentTypeHtml,
            final boolean shared) {
        putContent(new SetContentCall<>(updateClient, updateClientNature, exclusionTags, content, contentTypeHtml,
                shared));
    }

    private void putContent(final SetContentCall<T> setContentCall) {
        methodCallQ.add(setContentCall);
        executeMethodCallsFromQ();
    }

    private void executeMethodCallsFromQ() {
        if (methodCallQLock.tryAcquire()) {
            final Executor executor = this.executor;
            try {
                processMethodCallQ(executor, asyncUpdate, false);
            } finally {
                methodCallQLock.release();
            }
        } else {
            if (!methodCallQLock.hasQueuedThreads()) {
                final Executor activeExecutor = executor != null ? executor
                        : WffConfiguration.getVirtualThreadExecutor();
                final Runnable runnable = () -> {
                    methodCallQLock.acquireUninterruptibly();
                    try {
                        processMethodCallQ(activeExecutor, asyncUpdate, true);
                    } finally {
                        methodCallQLock.release();
                    }
                };
                if (activeExecutor != null) {
                    activeExecutor.execute(runnable);
                } else {
                    CompletableFuture.runAsync(runnable);
                }
            }
        }
    }

    private void processMethodCallQ(final Executor executor, final boolean parallelUpdateOnTags,
            final boolean calledByThread) {
        QueuedMethodCall<T> eachItem;
        while ((eachItem = methodCallQ.poll()) != null) {
            if (eachItem instanceof final SetContentCall<T> each) {
                setContentForQ(each.updateClient, each.updateClientNature, each.exclusionTags, each.content,
                        each.contentTypeHtml, each.shared, executor, parallelUpdateOnTags, calledByThread);
            } else if (eachItem instanceof final DetachCall<T> each) {
                detachForQ(each.removeContent, each.exclusionTags, each.exclusionClientUpdateTags, parallelUpdateOnTags,
                        calledByThread);
            } else if (eachItem instanceof final AddContentChangeListenerCall<T> each) {
                addContentChangeListenerForQ(each.tag, each.contentChangeListener);
            } else if (eachItem instanceof final AddDetachListenerCall<T> each) {
                addDetachListenerForQ(each.tag, each.detachListener);
            } else if (eachItem instanceof final RemoveContentChangeListenerCall<T> each) {
                removeContentChangeListenerForQ(each.contentChangeListener);
            } else if (eachItem instanceof final RemoveContentChangeListenerCall2<T> each) {
                removeContentChangeListenerForQ(each.tag, each.contentChangeListener);
            } else if (eachItem instanceof final RemoveDetachListenerCall<T> each) {
                removeDetachListenerForQ(each.detachListener);
            } else if (eachItem instanceof final RemoveDetachListenerCall2<T> each) {
                removeDetachListenerForQ(each.tag, each.detachListener);
            } else if (eachItem instanceof final RemoveContentChangeListenersCall<T> each) {
                removeContentChangeListenersForQ(each.tag, each.contentChangeListeners);
            } else if (eachItem instanceof final RemoveDetachListenersCall<T> each) {
                removeDetachListenersForQ(each.tag, each.detachListeners);
            } else if (eachItem instanceof final RemoveAllContentChangeListenersCall<T> each) {
                removeAllContentChangeListenersForQ(each.tag);
            } else if (eachItem instanceof RemoveAllContentChangeListenersNoArgCall<T>) {
                removeAllContentChangeListenersForQ();
            } else if (eachItem instanceof final RemoveAllDetachListenersCall<T> each) {
                removeAllDetachListenersForQ(each.tag);
            } else if (eachItem instanceof RemoveAllDetachListenersNoArgCall<T>) {
                removeAllDetachListenersForQ();
            } else if (eachItem instanceof final SetUpdateClientNatureCall<T> each) {
                setUpdateClientNatureForQ(each.updateClientNature);
            } else if (eachItem instanceof final SetUpdateClientCall<T> each) {
                setUpdateClientForQ(each.updateClient);
            } else if (eachItem instanceof final SetSharedCall<T> each) {
                setSharedForQ(each.shared);
            } else if (eachItem instanceof final SetExecutorCall<T> each) {
                setExecutorForQ(each.executor);
            } else if (eachItem instanceof final AddNoTagCall<T> each) {
                final long stamp = lock.writeLock();
                try {
                    insertedTags.put(each.noTag, each.insertedTagData);
                } finally {
                    lock.unlockWrite(stamp);
                }
            } else if (eachItem instanceof final RemoveNoTagCall<T> each) {
                removeForQ(each.insertedTag, each.parentTag);
            } else if (eachItem instanceof final RemoveListenersCall<T> each) {
                removeListenersForQ(each.tagId);
            } else if (eachItem instanceof final SharedTagContent.ReadStateCall<T> each) {
                readStateForQ(each.stateConsumer);
            }
        }
        cleanup();
    }

    /**
     * @param updateClient         true or false
     * @param updateClientNature   If this SharedTagContent object has to update
     *                             content of tags from multiple BrowserPage
     *                             instances,
     *                             UpdateClientNature.ALLOW_ASYNC_PARALLEL will
     *                             allow parallel operation in the background for
     *                             pushing changes to client browser page and
     *                             UpdateClientNature.ALLOW_PARALLEL will allow
     *                             parallel operation but will wait for the push to
     *                             finish to exit the setContent method.
     *                             UpdateClientNature.SEQUENTIAL will sequentially
     *                             do each browser page push operation.
     * @param exclusionTags        tags to be excluded only from client update. It
     *                             means that the content of all consumer tags will
     *                             be kept updated at server side so their content
     *                             formatter and change listeners will be invoked.
     * @param content
     * @param contentTypeHtml
     * @param shared
     * @param executor
     * @param parallelUpdateOnTags
     * @param calledByThread
     */
    private void setContentForQ(final boolean updateClient, final UpdateClientNature updateClientNature,
            final Set<AbstractHtml> exclusionTags, final T content, final boolean contentTypeHtml, final boolean shared,
            final Executor executor, final boolean parallelUpdateOnTags, final boolean calledByThread) {

        if (shared) {

            final Queue<AbstractHtml5SharedObject> sharedObjects = new ConcurrentLinkedQueue<>();
            Deque<Runnable> runnables = null;
            final long stamp = lock.writeLock();
            try {

                final Content<T> contentBefore = this.contentWithType;
                final Content<T> contentAfter = new Content<>(content, contentTypeHtml);

                this.updateClientNature = updateClientNature;
                this.contentWithType = new Content<>(content, contentTypeHtml);
                this.shared = shared;
                // this.executor = executor;

                final List<Entry<NoTag, InsertedTagData<T>>> insertedTagsEntries = insertedTags.entrySet().stream()
                        .sorted(Entry.comparingByValue()).toList();

                insertedTags.clear();

                // LinkedHashMap is irrelevant after the implementation of sharedObject.objectId
                // grouping
                Map<AbstractHtml5SharedObject, List<ParentNoTagData<T>>> tagsGroupedBySO = new HashMap<>(
                        insertedTagsEntries.size());

                for (final Entry<NoTag, InsertedTagData<T>> entry : insertedTagsEntries) {

                    final NoTag prevNoTag = entry.getKey();
                    if (prevNoTag == null) {
                        continue;
                    }
                    final InsertedTagData<T> insertedTagData = entry.getValue();

                    final AbstractHtml parentTag = prevNoTag.getParent();
                    final AbstractHtml prevNoTagAsBase = prevNoTag;

                    // the condition isParentNullifiedOnce true means the parent
                    // of this tag has already been changed at least once
                    if (parentTag == null || prevNoTagAsBase.isParentNullifiedOnce()
                            || prevNoTagAsBase.sharedTagContent != SharedTagContent.this) {
                        prevNoTagAsBase.setCacheSTCFormatter(null);
                        prevNoTagAsBase.setSharedTagContent(null);
                        prevNoTagAsBase.setSharedTagContentSubscribed(null);
                        continue;
                    }

                    final List<ParentNoTagData<T>> dataList = tagsGroupedBySO
                            .computeIfAbsent(parentTag.getSharedObject(), k -> new ArrayList<>(4));

                    final ContentFormatter<T> formatter = insertedTagData.formatter();

                    NoTag noTag;
                    Content<String> contentApplied;
                    try {
                        contentApplied = formatter.format(contentAfter);
                        if (contentApplied != null) {
                            prevNoTagAsBase.setCacheSTCFormatter(null);
                            noTag = new NoTag(null, contentApplied.content, contentApplied.contentTypeHtml);
                        } else {
                            noTag = prevNoTag;
                        }
                    } catch (final Exception e) {
                        contentApplied = new Content<>("", false);
                        prevNoTagAsBase.setCacheSTCFormatter(null);
                        noTag = new NoTag(null, contentApplied.content, contentApplied.contentTypeHtml);
                        LOGGER.log(Level.SEVERE, "Exception while ContentFormatter.format", e);
                    }

                    final AbstractHtml noTagAsBase = noTag;
                    noTagAsBase.setCacheSTCFormatter(formatter);
                    noTagAsBase.setSharedTagContent(this);
                    noTagAsBase.setSharedTagContentSubscribed(insertedTagData.subscribed());
                    dataList.add(new ParentNoTagData<>(prevNoTag, parentTag, noTag, insertedTagData, contentApplied));

                }
                final List<Entry<AbstractHtml5SharedObject, List<ParentNoTagData<T>>>> tagsGroupedBySOEntries = new ArrayList<>(
                        tagsGroupedBySO.entrySet());

                tagsGroupedBySO = null;

                tagsGroupedBySOEntries.sort(Comparator.comparing(o -> o.getKey().objectId()));

                final Queue<ModifiedParentData<T>> modifiedParents = new ConcurrentLinkedQueue<>();
                final Queue<Map.Entry<NoTag, InsertedTagData<T>>> forInsertedTags = new ConcurrentLinkedQueue<>();

                final int countDownLatchSize = tagsGroupedBySOEntries.size();

                final CountDownLatch countDownLatch;
                final Executor virtualThreadExecutor;

                if (parallelUpdateOnTags && countDownLatchSize > 1) {
                    virtualThreadExecutor = WffConfiguration.getVirtualThreadExecutor();
                    countDownLatch = new CountDownLatch(countDownLatchSize);
                } else {
                    virtualThreadExecutor = null;
                    countDownLatch = null;
                }

                for (final Entry<AbstractHtml5SharedObject, List<ParentNoTagData<T>>> entry : tagsGroupedBySOEntries) {

                    final Runnable runnable = () -> {
                        try {
                            // NB: never use sharedObject from key
//                    final AbstractHtml5SharedObject sharedObject = entry.getKey();

                            // pushing using first parent object makes bug (got bug when
                            // singleton SharedTagContent object is used under multiple
                            // BrowserPage instances)
                            // may be because the sharedObject in the parent can be
                            // changed
                            // before lock

                            for (final ParentNoTagData<T> parentNoTagData : entry.getValue()) {
                                // individual locking is better here, if multi-locking is used then it should be
                                // atomic which might be slow
                                // if multi-locking is not atomic it leads to deadlock as this runnable is async
                                final Lock writeLock = parentNoTagData.parent().lockAndGetWriteLock();
                                try {

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
                                    final AbstractHtml previousNoTag = parentNoTagData.previousNoTag();
                                    final AbstractHtml5SharedObject sharedObject = previousNoTag.getSharedObject();

                                    if (parentNoTagData.parent().getSharedObject().equals(sharedObject)
                                            && parentNoTagData.parent().getChildrenSizeLockless() == 1
                                            && !previousNoTag.isParentNullifiedOnce()) {

                                        if (parentNoTagData.contentApplied() != null) {

                                            modifiedParents.add(new ModifiedParentData<>(parentNoTagData.parent(),
                                                    parentNoTagData.contentApplied(),
                                                    parentNoTagData.insertedTagData().formatter()));

                                            boolean updateClientTagSpecific = updateClient;
                                            if (updateClient && exclusionTags != null
                                                    && exclusionTags.contains(parentNoTagData.parent())) {
                                                updateClientTagSpecific = false;
                                            }

                                            // to get safety of lock it is executed before
                                            // addInnerHtmlsAndGetEventsLockless
                                            // However lock safety is irrelevant here as the
                                            // SharedTagContent will not reuse the same NoTag
                                            previousNoTag.setSharedTagContent(null);
                                            previousNoTag.setSharedTagContentSubscribed(null);
                                            previousNoTag.setCacheSTCFormatter(null);

                                            // insertedTags.put should be after this stmt
                                            final InnerHtmlListenerData listenerData = parentNoTagData.parent()
                                                    .addInnerHtmlsAndGetEventsLockless(updateClientTagSpecific,
                                                            parentNoTagData.getNoTag());

                                            // insertedTags.put(parentNoTagData.getNoTag(),
                                            // parentNoTagData.insertedTagData());
                                            forInsertedTags.add(Map.entry(parentNoTagData.getNoTag(),
                                                    parentNoTagData.insertedTagData()));

                                            if (listenerData != null) {

                                                // subscribed and offline
                                                if (parentNoTagData.insertedTagData().subscribed()
                                                        && !sharedObject.isActiveWSListener()) {
                                                    final ClientTasksWrapper lastClientTask = parentNoTagData
                                                            .insertedTagData().lastClientTask();
                                                    if (lastClientTask != null) {
                                                        lastClientTask.nullifyTasks();
                                                    }
                                                }

                                                // TODO declare new innerHtmlsAdded for
                                                // multiple
                                                // parents after verifying feasibility
                                                // of
                                                // considering rich notag content
                                                final ClientTasksWrapper clientTask = listenerData.listener()
                                                        .innerHtmlsAdded(parentNoTagData.parent(),
                                                                listenerData.events());

                                                parentNoTagData.insertedTagData().lastClientTask(clientTask);

                                                // push is require only if listener
                                                // invoked
                                                sharedObjects.add(sharedObject);

                                                // TODO do final verification of this
                                                // code
                                            }

                                        } else {
                                            // insertedTags.put(parentNoTagData.getNoTag(),
                                            // parentNoTagData.insertedTagData());
                                            forInsertedTags.add(Map.entry(parentNoTagData.getNoTag(),
                                                    parentNoTagData.insertedTagData()));

                                            modifiedParents.add(new ModifiedParentData<>(parentNoTagData.parent(),
                                                    parentNoTagData.contentApplied(),
                                                    parentNoTagData.insertedTagData().formatter()));
                                        }
                                    } else {
                                        previousNoTag.setSharedTagContent(null);
                                        previousNoTag.setSharedTagContentSubscribed(null);
                                        previousNoTag.setCacheSTCFormatter(null);
                                        final AbstractHtml noTagAsBase = parentNoTagData.getNoTag();
                                        noTagAsBase.setCacheSTCFormatter(null);
                                        noTagAsBase.setSharedTagContent(null);
                                        noTagAsBase.setSharedTagContentSubscribed(null);
                                    }
                                } finally {
                                    writeLock.unlock();
                                }

                            }

                        } finally {
                            if (countDownLatch != null) {
                                countDownLatch.countDown();
                            }
                        }
                    };

                    if (virtualThreadExecutor != null && (calledByThread
                            || !entry.getKey().getLock(ACCESS_OBJECT).isWriteLockedByCurrentThread())) {
                        virtualThreadExecutor.execute(runnable);
                    } else {
                        runnable.run();
                    }
                }

                if (countDownLatch != null) {
                    try {
                        countDownLatch.await();
                    } catch (final InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

                for (final Entry<NoTag, InsertedTagData<T>> insertedTag : forInsertedTags) {
                    insertedTags.put(insertedTag.getKey(), insertedTag.getValue());
                }

                if (contentChangeListeners != null && countDownLatchSize > 0) {

                    final Deque<Map.Entry<ContentChangeListener<T>, ChangeEvent<T>>> listenerToEvent = new ArrayDeque<>(
                            modifiedParents.size());

                    for (final ModifiedParentData<T> modifiedParentData : modifiedParents) {
                        final AbstractHtml modifiedParent = modifiedParentData.parent();
                        final Set<ContentChangeListener<T>> listeners = contentChangeListeners
                                .get(modifiedParent.internalId());
                        if (listeners != null) {
                            for (final ContentChangeListener<T> listener : listeners) {
                                final ChangeEvent<T> changeEvent = new ChangeEvent<>(modifiedParent, listener,
                                        contentBefore, contentAfter, modifiedParentData.contentApplied(),
                                        modifiedParentData.formatter());
                                listenerToEvent.add(Map.entry(listener, changeEvent));
                            }
                        }
                    }

                    if (!listenerToEvent.isEmpty()) {
                        runnables = new ArrayDeque<>(listenerToEvent.size());
                        final ReentrantLock forMonitorEnterExit = new ReentrantLock();
                        forMonitorEnterExit.lock();
                        try {
                            for (final Entry<ContentChangeListener<T>, ChangeEvent<T>> each : listenerToEvent) {
                                try {
                                    final Runnable runnable = each.getKey().contentChanged(each.getValue());
                                    if (runnable != null) {
                                        runnables.add(runnable);
                                    }
                                } catch (final Exception e) {
                                    LOGGER.log(Level.SEVERE, "Exception while ContentChangeListener.contentChanged", e);
                                }
                            }
                        } finally {
                            forMonitorEnterExit.unlock();
                        }
                    }
                }
            } finally {
                lock.unlockWrite(stamp);
            }

            pushQueue(executor, updateClientNature, sharedObjects);

            if (runnables != null) {
                for (final Runnable runnable : runnables) {
                    try {
                        runnable.run();
                    } catch (final Exception e) {
                        LOGGER.log(Level.SEVERE,
                                "Exception while Runnable.run returned by ContentChangeListener.contentChanged", e);
                    }
                }
            }

        } else {
            final long stamp = lock.writeLock();
            try {
                this.updateClientNature = updateClientNature;
                this.contentWithType = new Content<>(content, contentTypeHtml);
                this.shared = shared;
            } finally {
                lock.unlockWrite(stamp);
            }
        }
    }

    /**
     * @param executor           the executor object for async push or null if no
     *                           preference. This executor object will be used only
     *                           if the {@code updateClientNature} is
     *                           {@link UpdateClientNature#ALLOW_ASYNC_PARALLEL} or
     *                           {@link UpdateClientNature#ALLOW_PARALLEL}.
     *
     *                           <br>
     *                           NB: You may need only one copy of executor object
     *                           for all sharedTagContent instances in the project.
     *                           So it could be declared as a static final object.
     *                           Eg: <br>
     * <pre><code>
     * public static final Executor EXECUTOR = Executors.newCachedThreadPool();
     * sharedTagContent.setExecutor(EXECUTOR);
     * </code></pre>
     * <p>
     * When Java releases Virtual Thread we may be able to use as follows
     * <pre><code>
     * public static final Executor EXECUTOR = Executors.newVirtualThreadExecutor();
     * sharedTagContent.setExecutor(EXECUTOR);
     * </code></pre>
     *
     * @param updateClientNature
     * @param sharedObjects
     * @since 3.0.6
     * @since 3.0.15 introduced executor
     */
    private void pushQueue(final Executor executor, final UpdateClientNature updateClientNature,
            final Queue<AbstractHtml5SharedObject> sharedObjects) {

        final List<PushQueue> pushQueues = new ArrayList<>(sharedObjects.size());
        AbstractHtml5SharedObject sharedObject;
        while ((sharedObject = sharedObjects.poll()) != null) {
            final PushQueue pushQueue = sharedObject.getPushQueue(ACCESS_OBJECT);
            if (pushQueue != null) {
                pushQueues.add(pushQueue);
            }
        }

        if (pushQueues.size() > 1) {
            final Executor activeExecutor = executor != null ? executor : WffConfiguration.getVirtualThreadExecutor();
            if (UpdateClientNature.ALLOW_ASYNC_PARALLEL.equals(updateClientNature)) {
                if (activeExecutor != null) {
                    for (final PushQueue pushQueue : pushQueues) {
                        activeExecutor.execute(pushQueue::push);
                    }
                } else {
                    for (final PushQueue pushQueue : pushQueues) {
                        CompletableFuture.runAsync(pushQueue::push);
                    }
                }

            } else if (UpdateClientNature.ALLOW_PARALLEL.equals(updateClientNature)) {
                if (activeExecutor != null) {
                    final List<CompletableFuture<Boolean>> cfList = new ArrayList<>(pushQueues.size());
                    for (final PushQueue pushQueue : pushQueues) {
                        final CompletableFuture<Boolean> cf = CompletableFuture.supplyAsync(() -> {
                            pushQueue.push();
                            return true;
                        }, activeExecutor);
                        cfList.add(cf);
                    }

                    for (final CompletableFuture<Boolean> each : cfList) {
                        try {
                            each.get();
                        } catch (InterruptedException | ExecutionException ignore) {
                            // NOP
                        }
                    }

                } else {
                    pushQueues.parallelStream().forEach(PushQueue::push);
                }

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
     * @since 3.0.18
     */
    private void cleanup() {
        final Executor executor = this.executor;
        if (executor != null) {
            executor.execute(this::clearGCTasksRQ);
        } else {
            clearGCTasksRQ();
        }
    }

    /**
     * @since 3.0.18
     */
    private void clearGCTasksRQ() {
        Reference<?> reference;
        while ((reference = tagGCTasksRQ.poll()) != null) {
            reference.clear();
            if (reference instanceof final Runnable task) {
                task.run();
            }
        }
//        while ((reference = insertedTagDataGCTasksRQ.poll()) != null) {
//            reference.clear();
//            final Runnable task = (Runnable) reference;
//            task.run();
//        }
    }

    /**
     * @param updateClient
     * @param applicableTag
     * @param formatter
     * @param subscribe     if true then updateClient will be true only if
     *                      activeWSListener is true otherwise updateClient will be
     *                      false at the time of content update.
     * @return the inserted NoTag
     * @since 3.0.6
     */
    AbstractHtml addInnerHtml(final boolean updateClient, final AbstractHtml applicableTag,
            final ContentFormatter<T> formatter, final boolean subscribe) {
        // NB: never add this method to updateMethodCallQ it leads to deadlock bug
        // (tested and verified), it is already thread-safe as the relevant tag is
        // locked before calling this method.

        final ContentFormatter<T> cFormatter = formatter != null ? formatter : DEFAULT_CONTENT_FORMATTER;

        final AbstractHtml5SharedObject sharedObject = applicableTag.getSharedObject();

        boolean listenerInvoked = false;
        NoTag noTagInserted = null;

        final Content<T> contentLocal = this.contentWithType;

        NoTag noTag;
        try {
            final Content<String> formattedContent = cFormatter.format(contentLocal);
            if (formattedContent != null) {
                noTag = new NoTag(null, formattedContent.content, formattedContent.contentTypeHtml);
            } else {
                noTag = new NoTag(null, "", false);
            }

        } catch (final Exception e) {
            noTag = new NoTag(null, "", false);
            LOGGER.log(Level.SEVERE, "Exception while ContentFormatter.format", e);
        }

        // insertedTags.put should be after this stmt
        final InnerHtmlListenerData listenerData = applicableTag.addInnerHtmlsAndGetEventsLockless(updateClient, noTag);

        noTagInserted = noTag;

        final InsertedTagData<T> insertedTagData = new InsertedTagData<>(ordinal.getAndIncrement(), cFormatter,
                subscribe);
        // for GC task, InsertedTagData contains WeakReference of cFormatter to prevent
        // it from GC it is kept in noTag
        final AbstractHtml noTagAsBase = noTag;
        noTagAsBase.setCacheSTCFormatter(cFormatter);
        noTagAsBase.setSharedTagContent(this);
        noTagAsBase.setSharedTagContentSubscribed(subscribe);

        // Note: if we are asynchronously remove listeners it will remove a valid
        // applicableTag entry
        // eg: stc.addListener for applicableTag then stc.subscribeTo(stc) then
        // applicableTag.removeAllChildren() then
        // after some time stc.addListener for applicableTag then stc.subscribeTo(stc),
        // so if the previously created insertedTagData
        // is enqueued after that then this valid entry for applicableTag will be
        // removed from the map

//            final InsertedTagDataGCTask<T> insertedTagDataGCTask = new InsertedTagDataGCTask<>(insertedTagData,
//                    insertedTagDataGCTasksRQ, this, applicableTag.internalId());
//            insertedTagDataGCTasksCache.add(insertedTagDataGCTask);

        methodCallQ.add(new AddNoTagCall<>(noTag, insertedTagData));

        if (listenerData != null) {
            // TODO declare new innerHtmlsAdded for multiple parents after
            // verifying feasibility of considering rich notag content
            final ClientTasksWrapper clientTask = listenerData.listener().innerHtmlsAdded(applicableTag,
                    listenerData.events());
            insertedTagData.lastClientTask(clientTask);
            listenerInvoked = true;
        }

        if (listenerInvoked) {
            pushQueue(sharedObject);
        }

        executeMethodCallsFromQ();

        return noTagInserted;
    }

    /**
     * NB: Only for internal use
     *
     * @param insertedTag instance of NoTag
     * @param parentTag   parent tag of NoTag
     * @return true if removed otherwise false
     * @since 3.0.6
     */
    boolean remove(final AbstractHtml insertedTag, final AbstractHtml parentTag) {

        methodCallQ.add(new RemoveNoTagCall<>(insertedTag, parentTag));
        executeMethodCallsFromQ();

        return true;
    }

    /**
     * NB: Only for internal use
     *
     * @param insertedTag instance of NoTag
     * @param parentTag   parent tag of NoTag
     * @return true if removed otherwise false
     * @since 12.0.0-beta.12
     */
    private boolean removeForQ(final AbstractHtml insertedTag, final AbstractHtml parentTag) {
        // NB: never add this method to updateMethodCallQ it leads to deadlock bug
        // (tested and verified with addInnerHtml), it is already thread-safe as the
        // relevant tag is locked before calling this method.

        final long stamp = lock.writeLock();
        try {
            insertedTag.setCacheSTCFormatter(null);
            insertedTag.setSharedTagContent(null);
            insertedTag.setSharedTagContentSubscribed(null);

            final boolean removed = insertedTags.remove(insertedTag) != null;
            if (detachListeners != null) {
                detachListeners.remove(parentTag.internalId());
            }
            if (contentChangeListeners != null) {
                contentChangeListeners.remove(parentTag.internalId());
            }
            return removed;
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    /**
     * NB: Only for internal use
     *
     * @param tagId
     */
    void removeListeners(final InternalId tagId) {
        methodCallQ.add(new RemoveListenersCall<>(tagId));
        executeMethodCallsFromQ();
    }

    /**
     * NB: Only for internal use
     *
     * @param tagId
     * @since 12.0.0-beta.12
     */
    private void removeListenersForQ(final InternalId tagId) {
        final Map<InternalId, Set<DetachListener<T>>> detachListeners = this.detachListeners;
        if (detachListeners != null) {
            detachListeners.remove(tagId);
        }
        final Map<InternalId, Set<ContentChangeListener<T>>> contentChangeListeners = this.contentChangeListeners;
        if (contentChangeListeners != null) {
            contentChangeListeners.remove(tagId);
        }
    }

    /**
     * @param stateConsumer
     * @since 12.0.2
     */
    private void readStateForQ(final StateConsumer<T> stateConsumer) {
        stateConsumer.accept(new StateEvent<>(contentWithType, shared, updateClientNature, updateClient, asyncUpdate));
    }

    /**
     * @param noTag
     * @return true if the parent of this NoTag was added by
     *         AbstractHtml.subscribedTo method but it doesn't mean the NoTag is not
     *         changed from parent or parent is modified.
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
     * @param exclusionTags excluded tags from detachment of this SharedTagConent
     *                      object
     *
     * @since 3.0.6
     */
    public void detach(final Set<AbstractHtml> exclusionTags) {
        detach(false, exclusionTags, null);
    }

    /**
     * @param removeContent true to remove content from the attached tags or false
     *                      not to remove but will detach
     *
     *
     * @since 3.0.6
     */
    public void detach(final boolean removeContent) {
        detach(removeContent, null, null);
    }

    /**
     * @param removeContent true to remove content from the attached tags or false
     *                      not to remove but will detach
     * @param exclusionTags excluded tags from detachment of this SharedTagConent
     *                      object
     *
     * @since 3.0.6
     */
    public void detach(final boolean removeContent, final Set<AbstractHtml> exclusionTags) {
        detach(removeContent, exclusionTags, null);
    }

    /**
     * @param removeContent             true to remove content from the attached
     *                                  tags or false not to remove but will detach
     * @param exclusionTags             excluded tags from detachment of this
     *                                  SharedTagConent object
     * @param exclusionClientUpdateTags these tags will be excluded for client
     *                                  update
     * @since 3.0.6
     */
    public void detach(final boolean removeContent, final Set<AbstractHtml> exclusionTags,
            final Set<AbstractHtml> exclusionClientUpdateTags) {
        methodCallQ.add(new DetachCall<>(removeContent, exclusionTags, exclusionClientUpdateTags));
        executeMethodCallsFromQ();
    }

    /**
     * @param removeContent             true to remove content from the attached
     *                                  tags or false not to remove but will detach
     * @param exclusionTags             excluded tags from detachment of this
     *                                  SharedTagConent object
     * @param exclusionClientUpdateTags these tags will be excluded for client
     *                                  update
     * @param parallelUpdateOnTags
     * @param calledByThread
     * @since 3.0.6
     */
    private void detachForQ(final boolean removeContent, final Set<AbstractHtml> exclusionTags,
            final Set<AbstractHtml> exclusionClientUpdateTags, final boolean parallelUpdateOnTags,
            final boolean calledByThread) {

        final Queue<AbstractHtml5SharedObject> sharedObjects = new ConcurrentLinkedQueue<>();
        Deque<Runnable> runnables = null;

        final long stamp = lock.writeLock();
        try {

            final Content<T> contentBefore = this.contentWithType;

            Map<AbstractHtml5SharedObject, List<ParentNoTagData<T>>> tagsGroupedBySO = new HashMap<>();

            for (final Entry<NoTag, InsertedTagData<T>> entry : insertedTags.entrySet()) {
                if (entry == null) {
                    continue;
                }
                final NoTag prevNoTag = entry.getKey();
                if (prevNoTag == null) {
                    continue;
                }
                final InsertedTagData<T> insertedTagData = entry.getValue();
                final AbstractHtml parentTag = prevNoTag.getParent();
                final AbstractHtml prevNoTagAsBase = prevNoTag;

                // the condition isParentNullifiedOnce true means the parent of
                // this tag has already been changed at least once
                if (parentTag == null || prevNoTagAsBase.isParentNullifiedOnce()
                        || prevNoTagAsBase.sharedTagContent != SharedTagContent.this) {
                    prevNoTagAsBase.setCacheSTCFormatter(null);
                    prevNoTagAsBase.setSharedTagContent(null);
                    prevNoTagAsBase.setSharedTagContentSubscribed(null);
                    continue;
                }

                final List<ParentNoTagData<T>> dataList = tagsGroupedBySO.computeIfAbsent(parentTag.getSharedObject(),
                        k -> new ArrayList<>(4));

                // final NoTag noTag = new NoTag(null, content,
                // contentTypeHtml);
                // not inserting NoTag so need not pass

                dataList.add(new ParentNoTagData<>(prevNoTag, parentTag, insertedTagData));
            }

            insertedTags.clear();

            final List<Entry<AbstractHtml5SharedObject, List<ParentNoTagData<T>>>> tagsGroupedBySOEntries = new ArrayList<>(
                    tagsGroupedBySO.entrySet());

            tagsGroupedBySO = null;

            tagsGroupedBySOEntries.sort(Comparator.comparing(o -> o.getKey().objectId()));

            final Queue<AbstractHtml> modifiedParents = new ConcurrentLinkedQueue<>();
            final Queue<Map.Entry<NoTag, InsertedTagData<T>>> forInsertedTags = new ConcurrentLinkedQueue<>();

            final int countDownLatchSize = tagsGroupedBySOEntries.size();

            final CountDownLatch countDownLatch;
            final Executor virtualThreadExecutor;

            if (parallelUpdateOnTags && countDownLatchSize > 1) {
                virtualThreadExecutor = WffConfiguration.getVirtualThreadExecutor();
                countDownLatch = new CountDownLatch(countDownLatchSize);
            } else {
                virtualThreadExecutor = null;
                countDownLatch = null;
            }

            for (final Entry<AbstractHtml5SharedObject, List<ParentNoTagData<T>>> entry : tagsGroupedBySOEntries) {

                final Runnable runnable = () -> {
                    try {
                        // pushing using first parent object makes bug (got bug when
                        // singleton SharedTagContent object is used under multiple
                        // BrowserPage instances)
                        // may be because the sharedObject in the parent can be changed
                        // before lock

                        for (final ParentNoTagData<T> parentNoTagData : entry.getValue()) {
                            // individual locking is better here, if multi-locking is used then it should be
                            // atomic which might be slow
                            // if multi-locking is not atomic it leads to deadlock as this runnable is async
                            final Lock writeLock = parentNoTagData.parent().lockAndGetWriteLock();
                            try {

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
                                final AbstractHtml previousNoTag = parentNoTagData.previousNoTag();
                                final AbstractHtml5SharedObject sharedObject = previousNoTag.getSharedObject();

                                if (parentNoTagData.parent().getSharedObject().equals(sharedObject)
                                        && parentNoTagData.parent().getChildrenSizeLockless() == 1
                                        && !previousNoTag.isParentNullifiedOnce()) {

                                    if (exclusionTags != null && exclusionTags.contains(parentNoTagData.parent())) {
                                        // insertedTags.put(parentNoTagData.previousNoTag(),
                                        // parentNoTagData.insertedTagData());
                                        forInsertedTags.add(Map.entry(parentNoTagData.previousNoTag(),
                                                parentNoTagData.insertedTagData()));
                                    } else {
                                        modifiedParents.add(parentNoTagData.parent());

                                        boolean updateClientTagSpecific = updateClient;
                                        if (updateClient && exclusionClientUpdateTags != null
                                                && exclusionClientUpdateTags.contains(parentNoTagData.parent())) {
                                            updateClientTagSpecific = false;
                                        }

                                        // to get safety of lock it is executed before
                                        // removeAllChildrenAndGetEventsLockless
                                        // However lock safety is irrelevant here as the
                                        // SharedTagContent will not reuse the same
                                        // NoTag
                                        previousNoTag.setSharedTagContent(null);
                                        previousNoTag.setSharedTagContentSubscribed(null);
                                        previousNoTag.setCacheSTCFormatter(null);

                                        if (removeContent) {
                                            final ChildTagRemoveListenerData listenerData = parentNoTagData.parent()
                                                    .removeAllChildrenAndGetEventsLockless(updateClientTagSpecific);

                                            if (listenerData != null) {
                                                // TODO declare new innerHtmlsAdded for
                                                // multiple
                                                // parents after verifying feasibility
                                                // of
                                                // considering rich notag content
                                                listenerData.getListener().allChildrenRemoved(listenerData.getEvent());

                                                // push is require only if listener
                                                // invoked
                                                sharedObjects.add(sharedObject);

                                                // TODO do final verification of this
                                                // code
                                            }
                                        }

                                    }

                                } else {
                                    previousNoTag.setCacheSTCFormatter(null);
                                    previousNoTag.setSharedTagContent(null);
                                    previousNoTag.setSharedTagContentSubscribed(null);

                                    final AbstractHtml noTagAsBase = parentNoTagData.getNoTag();
                                    noTagAsBase.setCacheSTCFormatter(null);
                                    noTagAsBase.setSharedTagContent(null);
                                    noTagAsBase.setSharedTagContentSubscribed(null);
                                }
                            } finally {
                                writeLock.unlock();
                            }

                        }

                    } finally {
                        if (countDownLatch != null) {
                            countDownLatch.countDown();
                        }
                    }

                };

                if (virtualThreadExecutor != null
                        && (calledByThread || !entry.getKey().getLock(ACCESS_OBJECT).isWriteLockedByCurrentThread())) {
                    virtualThreadExecutor.execute(runnable);
                } else {
                    runnable.run();
                }

            }

            if (countDownLatch != null) {
                try {
                    countDownLatch.await();
                } catch (final InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            for (final Entry<NoTag, InsertedTagData<T>> insertedTag : forInsertedTags) {
                insertedTags.put(insertedTag.getKey(), insertedTag.getValue());
            }

            if (detachListeners != null && countDownLatchSize > 0) {

                final Deque<Map.Entry<DetachListener<T>, DetachEvent<T>>> listenerToEvent = new ArrayDeque<>(
                        modifiedParents.size());
                for (final AbstractHtml modifiedParent : modifiedParents) {
                    final Set<DetachListener<T>> listeners = detachListeners.remove(modifiedParent.internalId());
                    if (listeners != null) {
                        for (final DetachListener<T> listener : listeners) {
                            final DetachEvent<T> detachEvent = new DetachEvent<>(modifiedParent, listener,
                                    contentBefore);
                            listenerToEvent.add(Map.entry(listener, detachEvent));
                        }
                    }
                }

                if (!listenerToEvent.isEmpty()) {
                    runnables = new ArrayDeque<>(listenerToEvent.size());
                    final ReentrantLock forMonitorEnterExit = new ReentrantLock();
                    forMonitorEnterExit.lock();
                    try {
                        for (final Entry<DetachListener<T>, DetachEvent<T>> each : listenerToEvent) {
                            try {
                                final Runnable runnable = each.getKey().detached(each.getValue());
                                if (runnable != null) {
                                    runnables.add(runnable);
                                }
                            } catch (final Exception e) {
                                LOGGER.log(Level.SEVERE, "Exception while DetachListener.detached", e);
                            }
                        }
                    } finally {
                        forMonitorEnterExit.unlock();
                    }
                }
            }
            if (contentChangeListeners != null) {
                for (final AbstractHtml modifiedParent : modifiedParents) {
                    contentChangeListeners.remove(modifiedParent.internalId());
                }
            }

        } finally {
            lock.unlockWrite(stamp);
        }

        pushQueue(executor, updateClientNature, sharedObjects);

        if (runnables != null) {
            for (final Runnable runnable : runnables) {
                try {
                    runnable.run();
                } catch (final Exception e) {
                    LOGGER.log(Level.SEVERE, "Exception while Runnable.run returned by DetachListener.detached", e);
                }
            }
        }

    }

    /**
     * @param tag                   the tag on which the content change to be
     *                              listened
     * @param contentChangeListener to be added
     * @since 3.0.6
     * @since 3.0.18 the contentChangeListener object will be removed from this
     *        SharedTagContent when the tag is detached from this SharedTagContent.
     */
    public void addContentChangeListener(final AbstractHtml tag, final ContentChangeListener<T> contentChangeListener) {
        methodCallQ.add(new AddContentChangeListenerCall<>(tag, contentChangeListener));
        executeMethodCallsFromQ();
    }

    /**
     * @param tag                   the tag on which the content change to be
     *                              listened
     * @param contentChangeListener to be added
     * @since 3.0.6
     * @since 3.0.18 the contentChangeListener object will be removed from this
     *        SharedTagContent when the tag is detached from this SharedTagContent.
     */
    private void addContentChangeListenerForQ(final AbstractHtml tag,
            final ContentChangeListener<T> contentChangeListener) {
        final long stamp = lock.writeLock();

        try {
            final Set<ContentChangeListener<T>> listeners;
            if (contentChangeListeners == null) {
                listeners = new LinkedHashSet<>(4);
                contentChangeListeners = new ConcurrentHashMap<>(4, 0.75F);
                contentChangeListeners.put(tag.internalId(), listeners);
                applicableTagGCTasksCache.add(new ApplicableTagGCTask<>(tag, tagGCTasksRQ, this));
            } else {
                listeners = contentChangeListeners.computeIfAbsent(tag.internalId(), k -> {
                    applicableTagGCTasksCache.add(new ApplicableTagGCTask<>(tag, tagGCTasksRQ, this));
                    return new LinkedHashSet<>(4);
                });
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
     * @since 3.0.18 the detachListener object will be removed from this
     *        SharedTagContent when the tag is detached from this SharedTagContent.
     */
    public void addDetachListener(final AbstractHtml tag, final DetachListener<T> detachListener) {
        methodCallQ.add(new AddDetachListenerCall<>(tag, detachListener));
        executeMethodCallsFromQ();
    }

    /**
     * @param tag
     * @param detachListener
     * @since 3.0.6
     * @since 3.0.18 the detachListener object will be removed from this
     *        SharedTagContent when the tag is detached from this SharedTagContent.
     */
    public void addDetachListenerForQ(final AbstractHtml tag, final DetachListener<T> detachListener) {
        final long stamp = lock.writeLock();

        try {
            final Set<DetachListener<T>> listeners;
            if (detachListeners == null) {
                listeners = new LinkedHashSet<>(4);
                detachListeners = new ConcurrentHashMap<>(4, 0.75F);
                detachListeners.put(tag.internalId(), listeners);
                applicableTagGCTasksCache.add(new ApplicableTagGCTask<>(tag, tagGCTasksRQ, this));
            } else {
                listeners = detachListeners.computeIfAbsent(tag.internalId(), k -> {
                    applicableTagGCTasksCache.add(new ApplicableTagGCTask<>(tag, tagGCTasksRQ, this));
                    return new LinkedHashSet<>(4);
                });
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
     * @param contentChangeListener to be removed from all linked tags
     * @since 3.0.6
     */
    public void removeContentChangeListener(final ContentChangeListener<T> contentChangeListener) {
        methodCallQ.add(new RemoveContentChangeListenerCall<>(contentChangeListener));
        executeMethodCallsFromQ();
    }

    /**
     * NB: this method will traverse through all consumer tags of this
     * SharedTagContent instance.
     *
     * @param contentChangeListener to be removed from all linked tags
     * @since 3.0.6
     */
    private void removeContentChangeListenerForQ(final ContentChangeListener<T> contentChangeListener) {
        final long stamp = lock.writeLock();

        try {
            if (contentChangeListeners != null) {

                for (final Set<ContentChangeListener<T>> listeners : contentChangeListeners.values()) {
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
     * @param tag                   the tag from which the listener to be removed
     * @param contentChangeListener to be removed
     * @since 3.0.6
     */
    public void removeContentChangeListener(final AbstractHtml tag,
            final ContentChangeListener<T> contentChangeListener) {
        methodCallQ.add(new RemoveContentChangeListenerCall2<>(tag, contentChangeListener));
        executeMethodCallsFromQ();
    }

    /**
     * @param tag                   the tag from which the listener to be removed
     * @param contentChangeListener to be removed
     * @since 3.0.6
     */
    private void removeContentChangeListenerForQ(final AbstractHtml tag,
            final ContentChangeListener<T> contentChangeListener) {
        final long stamp = lock.writeLock();

        try {
            if (contentChangeListeners != null) {
                final Set<ContentChangeListener<T>> listeners = contentChangeListeners.get(tag.internalId());
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
     * @param detachListener to be removed from all linked tags
     * @since 3.0.6
     */
    public void removeDetachListener(final DetachListener<T> detachListener) {
        methodCallQ.add(new RemoveDetachListenerCall<>(detachListener));
        executeMethodCallsFromQ();
    }

    /**
     * NB: this method will traverse through all consumer tags of this
     * SharedTagContent instance.
     *
     * @param detachListener to be removed from all linked tags
     * @since 3.0.6
     */
    private void removeDetachListenerForQ(final DetachListener<T> detachListener) {
        final long stamp = lock.writeLock();

        try {
            if (detachListeners != null) {

                for (final Set<DetachListener<T>> listeners : detachListeners.values()) {
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
    public void removeDetachListener(final AbstractHtml tag, final DetachListener<T> detachListener) {
        methodCallQ.add(new RemoveDetachListenerCall2<>(tag, detachListener));
        executeMethodCallsFromQ();
    }

    /**
     * @param tag
     * @param detachListener
     * @since 3.0.6
     */
    private void removeDetachListenerForQ(final AbstractHtml tag, final DetachListener<T> detachListener) {
        final long stamp = lock.writeLock();

        try {
            if (detachListeners != null) {
                final Set<DetachListener<T>> listeners = detachListeners.get(tag.internalId());
                if (listeners != null) {
                    listeners.remove(detachListener);
                }
            }

        } finally {
            lock.unlockWrite(stamp);
        }
    }

    /**
     * @param tag                    the tag from which the listener to be removed
     * @param contentChangeListeners to be removed
     * @since 3.0.6
     */
    public void removeContentChangeListeners(final AbstractHtml tag,
            final Collection<ContentChangeListener<T>> contentChangeListeners) {
        methodCallQ.add(new RemoveContentChangeListenersCall<>(tag, contentChangeListeners));
        executeMethodCallsFromQ();
    }

    /**
     * @param tag                    the tag from which the listener to be removed
     * @param contentChangeListeners to be removed
     * @since 3.0.6
     */
    private void removeContentChangeListenersForQ(final AbstractHtml tag,
            final Collection<ContentChangeListener<T>> contentChangeListeners) {

        final long stamp = lock.writeLock();

        try {
            if (this.contentChangeListeners != null) {

                final Set<ContentChangeListener<T>> listeners = this.contentChangeListeners.get(tag.internalId());

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
    public void removeDetachListeners(final AbstractHtml tag, final Collection<DetachListener<T>> detachListeners) {
        methodCallQ.add(new RemoveDetachListenersCall<>(tag, detachListeners));
        executeMethodCallsFromQ();
    }

    /**
     * @param tag
     * @param detachListeners
     * @since 3.0.6
     */
    private void removeDetachListenersForQ(final AbstractHtml tag,
            final Collection<DetachListener<T>> detachListeners) {

        final long stamp = lock.writeLock();

        try {
            if (this.detachListeners != null) {

                final Set<DetachListener<T>> listeners = this.detachListeners.get(tag.internalId());

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
     * @param tag the tag from which all listeners to be removed
     * @since 3.0.6
     */
    public void removeAllContentChangeListeners(final AbstractHtml tag) {
        methodCallQ.add(new RemoveAllContentChangeListenersCall<>(tag));
        executeMethodCallsFromQ();
    }

    /**
     * @param tag the tag from which all listeners to be removed
     * @since 3.0.6
     */
    private void removeAllContentChangeListenersForQ(final AbstractHtml tag) {
        final long stamp = lock.writeLock();

        try {
            if (contentChangeListeners != null) {
                contentChangeListeners.remove(tag.internalId());
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
        methodCallQ.add(new RemoveAllContentChangeListenersNoArgCall<>());
        executeMethodCallsFromQ();
    }

    /**
     * all listeners will be removed from all tags
     *
     * @since 3.0.6
     */
    private void removeAllContentChangeListenersForQ() {
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
     * @param tag the tag from which all listeners to be removed
     * @since 3.0.6
     */
    public void removeAllDetachListeners(final AbstractHtml tag) {
        methodCallQ.add(new RemoveAllDetachListenersCall<>(tag));
        executeMethodCallsFromQ();
    }

    /**
     * @param tag the tag from which all listeners to be removed
     * @since 3.0.6
     */
    private void removeAllDetachListenersForQ(final AbstractHtml tag) {
        final long stamp = lock.writeLock();

        try {
            if (detachListeners != null) {
                detachListeners.remove(tag.internalId());
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
        methodCallQ.add(new RemoveAllDetachListenersNoArgCall<>());
        executeMethodCallsFromQ();
    }

    /**
     * all listeners will be removed from all tags
     *
     * @since 3.0.6
     */
    private void removeAllDetachListenersForQ() {
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
     * Note: this method a may lead to deadlock if it is called under whenURI event
     * thread while another thread is updating this SharedTagContent object so use
     * {@link #getContentFormatter(AbstractHtml, Consumer)} method which is
     * asynchronous.
     *
     * @param tag the tag whose ContentFormatter to be got.
     * @return the ContentFormatter object set for the given tag.
     * @since 3.0.11
     */
    public ContentFormatter<T> getContentFormatter(final AbstractHtml tag) {
        final AbstractHtml firstChild = tag.getFirstChild();
        if (firstChild != null) {
            final long stamp = lock.readLock();
            try {
                final InsertedTagData<T> insertedTagData = insertedTags.get(firstChild);
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
     * This method is safe to use under whenURI event thread.
     *
     * @param tag      the tag whose ContentFormatter to be got.
     * @param consumer the consumer to catch the ContentFormatter object set for the
     *                 given tag.
     * @since 12.0.0-beta.10
     */
    public void getContentFormatter(final AbstractHtml tag, final Consumer<ContentFormatter<T>> consumer) {
        final AbstractHtml firstChild = tag.getFirstChild();
        if (firstChild != null && consumer != null) {
            final Executor activeExecutor = executor != null ? executor : WffConfiguration.getVirtualThreadExecutor();
            final Runnable runnable = () -> consumer.accept(getContentFormatter(tag));
            if (activeExecutor != null) {
                activeExecutor.execute(runnable);
            } else {
                CompletableFuture.runAsync(runnable);
            }
        }
    }

    /**
     * Note: this method a may lead to deadlock if it is called under whenURI event
     * thread while another thread is updating this SharedTagContent object so use
     * {@link #getContentChangeListeners(AbstractHtml, Consumer)} method which is
     * asynchronous.
     *
     * @param tag tag from which the listeners to be got.
     * @return the ContentChangeListeners for the given tag.
     * @since 3.0.11
     */
    public Set<ContentChangeListener<T>> getContentChangeListeners(final AbstractHtml tag) {
        final long stamp = lock.readLock();
        try {
            final Map<InternalId, Set<ContentChangeListener<T>>> contentChangeListeners = this.contentChangeListeners;
            if (contentChangeListeners != null) {
                final Set<ContentChangeListener<T>> listeners = contentChangeListeners.get(tag.internalId());
                if (listeners != null) {
                    return Set.copyOf(listeners);
                }
            }
        } finally {
            lock.unlockRead(stamp);
        }
        return null;
    }

    /**
     * This method is safe to use under whenURI event thread.
     *
     * @param tag      tag from which the listeners to be got.
     * @param consumer the consumer to catch the ContentChangeListeners for the
     *                 given tag.
     * @since 12.0.0-beta.10
     */
    public void getContentChangeListeners(final AbstractHtml tag,
            final Consumer<Set<ContentChangeListener<T>>> consumer) {
        if (tag != null && consumer != null) {
            final Executor activeExecutor = executor != null ? executor : WffConfiguration.getVirtualThreadExecutor();
            final Runnable runnable = () -> consumer.accept(getContentChangeListeners(tag));
            if (activeExecutor != null) {
                activeExecutor.execute(runnable);
            } else {
                CompletableFuture.runAsync(runnable);
            }
        }
    }

    /**
     * Note: this method a may lead to deadlock if it is called under whenURI event
     * thread while another thread is updating this SharedTagContent object so use
     * {@link #getDetachListeners(AbstractHtml, Consumer)} method which is
     * asynchronous.
     *
     * @param tag tag from which the listeners to be got.
     * @return the DetachListeners for the given tag.
     * @since 3.0.11
     */
    public Set<DetachListener<T>> getDetachListeners(final AbstractHtml tag) {
        final long stamp = lock.readLock();
        try {
            final Map<InternalId, Set<DetachListener<T>>> detachListeners = this.detachListeners;
            if (detachListeners != null) {
                final Set<DetachListener<T>> listeners = detachListeners.get(tag.internalId());
                if (listeners != null) {
                    return Set.copyOf(listeners);
                }
            }
        } finally {
            lock.unlockRead(stamp);
        }
        return null;
    }

    /**
     * This method is safe to use under whenURI event thread.
     *
     * @param tag      tag from which the listeners to be got.
     * @param consumer the consumer to catch the DetachListeners for the given tag.
     * @since 12.0.0-beta.10
     */
    public void getDetachListeners(final AbstractHtml tag, final Consumer<Set<DetachListener<T>>> consumer) {
        if (tag != null && consumer != null) {
            final Executor activeExecutor = executor != null ? executor : WffConfiguration.getVirtualThreadExecutor();
            final Runnable runnable = () -> consumer.accept(getDetachListeners(tag));
            if (activeExecutor != null) {
                activeExecutor.execute(runnable);
            } else {
                CompletableFuture.runAsync(runnable);
            }
        }
    }

    /**
     * Clears the unwanted objects from the temporary cache only if required. The
     * unwanted objects are automatically cleared from the temp cache but this
     * method is for manually clearing it.
     *
     * @since 3.0.18
     */
    public void clearTempCache() {
        clearGCTasksRQ();
    }

    /**
     * To set internal operations to be asynchronous which may increase performance
     * when there are millions of consumer tags. If it is enabled all update methods
     * will be fully asynchronous. However, it will strictly keep the order of
     * update, in FIFO order. Note: Java Virtual Thread should be enabled (only if
     * the Java version is &lt; 21) to support this feature. Even if this method is
     * set as false internal operations may still be done in asynchronous only when
     * appropriate to improve performance.
     *
     * @since 12.0.0-beta.12
     * @param asyncUpdate true to enable and false to disable. Its default value is
     *                    false.
     */
    public SharedTagContent<T> setAsyncUpdate(final boolean asyncUpdate) {
        this.asyncUpdate = asyncUpdate;
        return this;
    }

    /**
     * @return true or false
     * @since 12.0.0-beta.12
     */
    public boolean isAsyncUpdate() {
        return asyncUpdate;
    }

    /**
     * @return the current content with its type
     * @since 12.0.0-beta.12
     */
    public Content<T> getContentWithType() {
        return contentWithType;
    }

    /**
     * @return true if it contains pending task
     * @since 12.0.0-beta.12
     */
    public boolean hasPendingTask() {
        return !methodCallQ.isEmpty();
    }

    /**
     * @return the number of pending tasks in the queue
     * @since 12.0.0-beta.12
     */
    public int pendingTasksSize() {
        return methodCallQ.size();
    }

    /**
     * Reads the state i.e. content and other properties of this SharedTagContent
     * object. It ensures that no one is updating the content/properties of this
     * SharedTagContent object at the time of reading. The difference between
     * getContent and readState is, in a multi-threaded environment, if setContent
     * is called just before this method it will make sure that the state is read
     * only after setting the content (It will keep an orderly execution of method
     * calls in FIFO).
     *
     * @param stateConsumer the StateConsumer to read the state.
     * @since 12.0.2
     */
    public void readState(final StateConsumer<T> stateConsumer) {
        methodCallQ.add(new ReadStateCall<>(stateConsumer));
        executeMethodCallsFromQ();
    }

}
