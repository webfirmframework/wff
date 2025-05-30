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
package com.webfirmframework.wffweb.server.page;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serial;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.webfirmframework.wffweb.InvalidUsageException;
import com.webfirmframework.wffweb.InvalidValueException;
import com.webfirmframework.wffweb.MethodNotImplementedException;
import com.webfirmframework.wffweb.NotRenderedException;
import com.webfirmframework.wffweb.NullValueException;
import com.webfirmframework.wffweb.PushFailedException;
import com.webfirmframework.wffweb.WffRuntimeException;
import com.webfirmframework.wffweb.common.URIEvent;
import com.webfirmframework.wffweb.common.URIEventInitiator;
import com.webfirmframework.wffweb.common.URIEventMask;
import com.webfirmframework.wffweb.internal.security.object.BrowserPageSecurity;
import com.webfirmframework.wffweb.internal.security.object.SecurityObject;
import com.webfirmframework.wffweb.internal.server.page.js.WffJsFile;
import com.webfirmframework.wffweb.server.page.action.BrowserPageAction;
import com.webfirmframework.wffweb.settings.WffConfiguration;
import com.webfirmframework.wffweb.tag.html.AbstractHtml;
import com.webfirmframework.wffweb.tag.html.Html;
import com.webfirmframework.wffweb.tag.html.TagNameConstants;
import com.webfirmframework.wffweb.tag.html.TagUtil;
import com.webfirmframework.wffweb.tag.html.attribute.Defer;
import com.webfirmframework.wffweb.tag.html.attribute.Nonce;
import com.webfirmframework.wffweb.tag.html.attribute.Src;
import com.webfirmframework.wffweb.tag.html.attribute.Type;
import com.webfirmframework.wffweb.tag.html.attribute.core.AbstractAttribute;
import com.webfirmframework.wffweb.tag.html.attribute.core.AttributeRegistry;
import com.webfirmframework.wffweb.tag.html.attribute.event.EventAttribute;
import com.webfirmframework.wffweb.tag.html.attribute.event.ServerMethod;
import com.webfirmframework.wffweb.tag.html.html5.attribute.global.DataWffId;
import com.webfirmframework.wffweb.tag.html.programming.Script;
import com.webfirmframework.wffweb.tag.htmlwff.NoTag;
import com.webfirmframework.wffweb.tag.repository.TagRepository;
import com.webfirmframework.wffweb.util.HashUtil;
import com.webfirmframework.wffweb.util.StringUtil;
import com.webfirmframework.wffweb.util.WffBinaryMessageUtil;
import com.webfirmframework.wffweb.util.data.NameValue;
import com.webfirmframework.wffweb.wffbm.data.BMValueType;
import com.webfirmframework.wffweb.wffbm.data.WffBMArray;
import com.webfirmframework.wffweb.wffbm.data.WffBMByteArray;
import com.webfirmframework.wffweb.wffbm.data.WffBMObject;

/**
 * @author WFF
 * @since 2.0.0
 */
public abstract class BrowserPage implements Serializable {

    // if this class' is refactored then SecurityClassConstants should be
    // updated.

    @Serial
    private static final long serialVersionUID = 1_0_1L;

    private static final Logger LOGGER = Logger.getLogger(BrowserPage.class.getName());

    public static final String WFF_INSTANCE_ID = "wffInstanceId";

    private static final boolean PRODUCTION_MODE = true;

    private final String instanceId = UUID.randomUUID().toString();

    private final String externalDrivePath = useExternalDrivePathPvt();

    private volatile Map<String, AbstractHtml> tagByWffId;

    private volatile AbstractHtml rootTag;

    private volatile boolean wsWarningDisabled;

    private final Map<String, WebSocketPushListener> sessionIdWsListeners = new ConcurrentHashMap<>();

    private final Deque<WebSocketPushListener> wsListeners = new ConcurrentLinkedDeque<>();

    private volatile WebSocketPushListener wsListener;

    private volatile DataWffId wffScriptTagId;

    /**
     * it's true by default since 3.0.1
     */
    private volatile boolean enableDeferOnWffScript = true;

    private volatile Nonce nonceForWffScriptTag;

    private boolean autoremoveWffScript = true;

    private volatile boolean renderInvoked;

    // ConcurrentLinkedQueue give better performance than ConcurrentLinkedDeque
    // on benchmark
    // NB: not all methods of Queue is implemented, ensure before using it.
    private final Deque<ClientTasksWrapper> wffBMBytesQueue = buildClientTasksWrapperDeque("out_main");

    // ConcurrentLinkedQueue give better performance than ConcurrentLinkedDeque
    // on benchmark
    // NB: not all methods of Queue is implemented, ensure before using it.
    private final Queue<ClientTasksWrapper> wffBMBytesHoldPushQueue = buildClientTasksWrapperQueue("out_hp");

    private volatile Path tempDirPath = null;

    // there will be only one thread waiting for the lock so fairness must be
    // false and fairness may decrease the lock time
    // only one thread is allowed to gain lock so permit should be 1
    private final transient Semaphore taskFromClientQLock = new Semaphore(1, false);

    // ConcurrentLinkedQueue give better performance than ConcurrentLinkedDeque
    // on benchmark
    // NB: not all methods of Queue is implemented, ensure before using it.
    private final Queue<byte[]> taskFromClientQ = buildByteArrayQ("in");

    private static final SecurityObject ACCESS_OBJECT = new BrowserPageSecurity(new Security());

    // by default the push queue should be enabled
    private volatile boolean pushQueueEnabled = true;

    // by default the pushQueueOnNewWebSocketListener should be enabled
    private volatile boolean pushQueueOnNewWebSocketListener = true;

    private final AtomicInteger holdPush = new AtomicInteger(0);

    private final Map<String, ServerMethodWrapper> serverMethods = new ConcurrentHashMap<>();

    private boolean removeFromBrowserContextOnTabClose = true;

    private boolean removePrevFromBrowserContextOnTabInit = true;

    int wsHeartbeatInterval = -1;

    private int wsReconnectInterval = -1;

    private static final int INITIAL_WS_DEFAULT_HEARTBEAT_INTERVAL = 25_000;

    private static volatile int wsDefaultHeartbeatInterval = INITIAL_WS_DEFAULT_HEARTBEAT_INTERVAL;

    private static volatile int wsDefaultReconnectInterval = 2_000;

    private final LongAdder pushQueueSize = new LongAdder();

    // there will be only one thread waiting for the lock so fairness must be
    // false and fairness may decrease the lock time
    // only one thread is allowed to gain lock so permit should be 1
    private final transient Semaphore pushWffBMBytesQueueLock = new Semaphore(1, false);

    // there will be only one thread waiting for the lock so fairness must be
    // false and fairness may decrease the lock time
    // only one thread is allowed to gain lock so permit should be 1
    private final transient Semaphore unholdPushLock = new Semaphore(1, false);

    // fair may be false
    // lock to replace synchronized block. (scenario: if a virtual thread is
    // executing a synchronized block it cannot be detached from the OS thread until
    // it exits the synchronized block, ReentrantLock will solve this issue)
    // to execute only one UI update at a time as setURI method can also be called
    // from server side
    // To read up to date value from main memory and to flush modification to main
    // memory synchronized/ReentrantLock
    // will do it as per java memory model,
    // synchronized block cannot be used as it will prevent virtual thread from
    // being detached from its OS thread
    private final transient Lock commonLock = new ReentrantLock(false);

    private final AtomicReference<Thread> waitingThreadRef = new AtomicReference<>();

    private volatile TagRepository tagRepository;

    private volatile Executor executor;

    volatile boolean onInitialClientPingInvoked;

    volatile long lastClientAccessedTime = System.currentTimeMillis();

    private final Set<Reference<AbstractHtml>> tagsForURIChange = ConcurrentHashMap.newKeySet(1);

    private volatile URIEvent uriEvent;

    private volatile Reference<BrowserPageSessionImpl> sessionRef;

    private static final byte[] PLACEHOLDER_BYTE_ARRAY_FOR_PAYLOAD_ID = new byte[4];

    private final AtomicInteger serverSidePayloadIdGenerator = new AtomicInteger();

    private final AtomicInteger clientSidePayloadIdGenerator = new AtomicInteger();

    private static final long DEFAULT_IO_BUFFER_TIMEOUT = TimeUnit.MILLISECONDS
            .toNanos(INITIAL_WS_DEFAULT_HEARTBEAT_INTERVAL);

    // should be before settings field initialization
    // 1024 *1024 = 1048576 i.e. 1MB, a heavy html page size might be 1 MB in size
    // so considered this value.
    // gave inputBufferTimeout and outputBufferTimeout = 25_000 as the
    // wsDefaultHeartbeatInterval is 25_000ms
    private final Settings defaultSettings = new Settings(1048576, DEFAULT_IO_BUFFER_TIMEOUT, 1048576,
            DEFAULT_IO_BUFFER_TIMEOUT, new OnPayloadLoss("location.reload();", () -> BrowserPage.this
                    .performBrowserPageAction(BrowserPageAction.RELOAD_FROM_CACHE.getActionByteBuffer())));

    final Settings settings = useSettingsPvt();

    private final OnPayloadLoss onPayloadLoss = settings.onPayloadLoss;

    private final Semaphore inputBufferLimitLock = settings.inputBufferLimit > 0
            ? new Semaphore(settings.inputBufferLimit)
            : null;

    private final Semaphore outputBufferLimitLock = settings.outputBufferLimit > 0
            ? new Semaphore(settings.outputBufferLimit)
            : null;

    private volatile boolean losslessCommunicationCheckFailed;

    // NB: this non-static initialization makes BrowserPage and PayloadProcessor
    // never to get GCd. It leads to memory leak. It seems to be a bug.
    // private final ThreadLocal<PayloadProcessor> PALYLOAD_PROCESSOR_TL =
    // ThreadLocal
    // .withInitial(() -> new PayloadProcessor(this, true));

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

    /**
     * To specify (by removeFromContext method) when to remove {@code BrowserPage}
     * instance from {@code BrowserPageContext}.
     *
     *
     * @since 2.1.4
     */
    public enum On {

        /**
         * to remove the current {@code BrowserPage} instance from
         * {@code BrowserPageContext} when the tab/window is closed.
         */
        TAB_CLOSE,

        /**
         * To remove the previous {@code BrowserPage} instance opened in the same tab
         * when new {@code BrowserPage} is requested by the tab.
         */
        INIT_REMOVE_PREVIOUS
    }

    /**
     * @since 12.0.0-beta.8
     */
    @FunctionalInterface
    protected interface ServerSideAction {
        void perform();
    }

    /**
     * @param javaScript       the JavaScript code to invoke at client side when
     *                         there is a lossy communication detected at client
     *                         side.
     * @param serverSideAction the action to perform at server side when there is a
     *                         lossy communication detected at server side.
     * @since 12.0.0-beta.8
     */
    protected record OnPayloadLoss(String javaScript, ServerSideAction serverSideAction) {
        // should be public

        public OnPayloadLoss {
        }
    }

    /**
     * The {@code Settings} object returned by {@link #defaultSettings()} method
     * contains the default values for all of these parameters. The
     * {@code inputBufferLimit} and {@code outputBufferLimit} works as expected only
     * when {@code onPayloadLoss} param is passed otherwise the buffer may grow
     * beyond the given limit especially when corresponding buffer timeout is less
     * than the equalent nanoseconds of BrowserPage session maxIdleTimeout (the
     * {@code maxIdleTimeout} passed from
     * {@link BrowserPageContext#enableAutoClean}, eg: long inputBufferTimeout,
     * outputBufferTimeout = TimeUnit.MILLISECONDS.toNanos(maxIdleTimeout));.
     *
     * @param inputBufferLimit    the limit for input buffer, i.e. the number of
     *                            bytes allowed to store, a value &lt;= 0 represents
     *                            no limit i.e. unlimited size. This is the buffer
     *                            used to store the data from the client events. The
     *                            threads which store the data to the buffer will be
     *                            blocked until enough space available in the
     *                            buffer.
     * @param inputBufferTimeout  the timeout nanoseconds for waiting threads of
     *                            input buffer. The optimal value may be a value
     *                            less than the heartbeat time of websocket in
     *                            nanoseconds ( set by
     *                            {@link #setWebSocketHeartbeatInterval(int)} or
     *                            {@link #setWebSocketDefultHeartbeatInterval(int)}).
     *                            However, this value strictly depends on your
     *                            application environment &amp; project
     *                            requirements. The maximum recommended value is
     *                            usually equal to the timeout of session in
     *                            nanoseconds, which is nanoseconds of
     *                            {@code maxIdleTimeout} passed in
     *                            {@link BrowserPageContext#enableAutoClean}, eg:
     *                            long inputBufferTimeout =
     *                            TimeUnit.MILLISECONDS.toNanos(maxIdleTimeout).
     * @param outputBufferLimit   the limit for output buffer, i.e. the number of
     *                            bytes allowed to store, a value &lt;= 0 represents
     *                            no limit i.e. unlimited size. This is the buffer
     *                            used to store the data from the server events. The
     *                            threads which store the data to the buffer will be
     *                            blocked until enough space available in the
     *                            buffer.
     * @param outputBufferTimeout the timeout nanoseconds for waiting threads of
     *                            output buffer. The optimal value may be a value
     *                            less than the heartbeat time of websocket in
     *                            nanoseconds ( set by
     *                            {@link #setWebSocketHeartbeatInterval(int)} or
     *                            {@link #setWebSocketDefultHeartbeatInterval(int)}).
     *                            However, this value strictly depends on your
     *                            application environment &amp; project
     *                            requirements. The maximum recommended value is
     *                            usually equal to the timeout of session in
     *                            nanoseconds, which is nanoseconds of
     *                            {@code maxIdleTimeout} passed in
     *                            {@link BrowserPageContext#enableAutoClean}, eg:
     *                            long outputBufferTimeout =
     *                            TimeUnit.MILLISECONDS.toNanos(maxIdleTimeout).
     * @param onPayloadLoss       pass an object of {@code OnPayloadLoss} to enable
     *                            or null to disable lossless browser page
     *                            communication.
     * @since 12.0.0-beta.8
     */
    protected record Settings(int inputBufferLimit, long inputBufferTimeout, int outputBufferLimit,
            long outputBufferTimeout, OnPayloadLoss onPayloadLoss) {
        // should be public
        public Settings {
        }
    }

    public abstract String webSocketUrl();

    /**
     * @return the Queue
     * @since 3.0.18
     */
    private Queue<byte[]> buildByteArrayQ(final String subDirName) {

        if (externalDrivePath != null) {
            try {
                return new ExternalDriveByteArrayQueue(externalDrivePath, instanceId, subDirName);
            } catch (final IOException e) {
                LOGGER.severe(
                        "The given path by useExternalDrivePath is invalid or it doesn't have read/write permission.");
            }
        }

        return new ConcurrentLinkedQueue<>();
    }

    /**
     * @return the Deque
     * @since 3.0.18
     */
    private Deque<ClientTasksWrapper> buildClientTasksWrapperDeque(final String subDir) {
        if (externalDrivePath != null) {
            try {
                return new ExternalDriveClientTasksWrapperDeque(externalDrivePath, instanceId, subDir);
            } catch (final IOException e) {
                LOGGER.severe(
                        "The given path by useExternalDrivePath is invalid or it doesn't have read/write permission.");
            }
        }

        return new ConcurrentLinkedDeque<>();
    }

    /**
     * @return the Queue
     * @since 3.0.18
     */
    private Queue<ClientTasksWrapper> buildClientTasksWrapperQueue(final String subDir) {
        if (externalDrivePath != null) {
            try {
                return new ExternalDriveClientTasksWrapperQueue(externalDrivePath, instanceId, subDir);
            } catch (final IOException e) {
                LOGGER.severe(
                        "The given path by useExternalDrivePath is invalid or it doesn't have read/write permission.");
            }
        }

        return new ConcurrentLinkedQueue<>();
    }

    /**
     * Override and use this method to set path to write temporary files to save
     * heap space.
     *
     * @return the path to store temporary files to save heap space.
     * @since 3.0.18
     */
    private String useExternalDrivePathPvt() {
        try {
            return useExternalDrivePath();
        } catch (final Exception e) {
            if (LOGGER.isLoggable(Level.WARNING)) {
                LOGGER.log(Level.WARNING, "Unable get externalDrivePath from useExternalDrivePath method.", e);
            }
        }
        return null;
    }

    /**
     * Override and use this method to set path to write temporary files to save
     * heap space.
     *
     * @return the path to store temporary files to save heap space.
     * @since 3.0.18
     */
    protected String useExternalDrivePath() {
        return null;
    }

    /**
     * @param wsListener
     *
     * @since 2.0.0
     */
    public final void setWebSocketPushListener(final WebSocketPushListener wsListener) {
        this.wsListener = wsListener;
        if (rootTag != null) {
            rootTag.getSharedObject().setActiveWSListener(wsListener != null, ACCESS_OBJECT);
        }
        if (pushQueueOnNewWebSocketListener) {
            pushWffBMBytesQueue();
        }
    }

    /**
     * adds the WebSocket listener for the given WebSocket session
     *
     * @param sessionId  the unique id of WebSocket session
     * @param wsListener
     *
     * @since 2.1.0
     */
    public final void addWebSocketPushListener(final String sessionId, final WebSocketPushListener wsListener) {

        sessionIdWsListeners.put(sessionId, wsListener);

        // should be in the first of this queue as it could provide the latest
        // reliable ws connection
        wsListeners.push(wsListener);

        this.wsListener = wsListener;
        if (rootTag != null) {
            rootTag.getSharedObject().setActiveWSListener(wsListener != null, ACCESS_OBJECT);
        }

        if (pushQueueOnNewWebSocketListener) {
            pushWffBMBytesQueue();
        }

    }

    /**
     * removes the WebSocket listener added for this WebSocket session
     *
     * @param sessionId the unique id of WebSocket session
     *
     * @since 2.1.0
     */
    public final void removeWebSocketPushListener(final String sessionId) {

        final WebSocketPushListener removedListener = sessionIdWsListeners.remove(sessionId);
        // remove all
        while (wsListeners.remove(removedListener)) {
        }

        wsListener = wsListeners.peek();
        if (rootTag != null) {
            rootTag.getSharedObject().setActiveWSListener(wsListener != null, ACCESS_OBJECT);
        }
    }

    /**
     * removes all WebSocket listeners added
     *
     *
     * @since 3.0.16
     */
    final void clearWSListeners() {
        // remove all
        wsListeners.clear();

        wsListener = wsListeners.peek();
        if (rootTag != null) {
            rootTag.getSharedObject().setActiveWSListener(wsListener != null, ACCESS_OBJECT);
        }
    }

    public final WebSocketPushListener getWsListener() {
        return wsListener;
    }

    final void push(final NameValue... nameValues) {
        final ByteBuffer payload = buildPayload(WffBinaryMessageUtil.VERSION_1.getWffBinaryMessageBytes(nameValues));
        push(new ClientTasksWrapper(payload));
    }

    /**
     * @param multiTasks
     * @return the wrapper class holding the byteBuffers of multiple tasks
     */
    final ClientTasksWrapper pushAndGetWrapper(final Queue<Collection<NameValue>> multiTasks) {

        final ByteBuffer[] tasks = new ByteBuffer[multiTasks.size()];
        int index = 0;

        Collection<NameValue> taskNameValues;
        while ((taskNameValues = multiTasks.poll()) != null) {
            final NameValue[] nameValues = taskNameValues.toArray(new NameValue[taskNameValues.size()]);

            final ByteBuffer payload = buildPayload(
                    WffBinaryMessageUtil.VERSION_1.getWffBinaryMessageBytes(nameValues));
            tasks[index] = payload;
            index++;
        }

        final ClientTasksWrapper clientTasks = new ClientTasksWrapper(tasks);
        push(clientTasks);
        return clientTasks;
    }

    private void pushLockless(final ClientTasksWrapper clientTasks) {
        if (holdPush.get() > 0) {
            // add method internally calls offer method in ConcurrentLinkedQueue
            wffBMBytesHoldPushQueue.offer(clientTasks);
        } else {
            if (!wffBMBytesHoldPushQueue.isEmpty()) {
                copyCachedBMBytesToMainQ();
            }
            // add method internally calls offer which internally
            // calls offerLast method in ConcurrentLinkedQueue

            if (wffBMBytesQueue.offerLast(clientTasks)) {
                pushQueueSize.increment();
            }
        }
    }

    private void push(final ClientTasksWrapper clientTasks) {
        if (outputBufferLimitLock != null) {
            if (!losslessCommunicationCheckFailed) {
                try {
                    // onPayloadLoss check should be second
                    if (outputBufferLimitLock.tryAcquire(clientTasks.getCurrentSize(), settings.outputBufferTimeout,
                            TimeUnit.NANOSECONDS) || onPayloadLoss == null) {
                        pushLockless(clientTasks);
                    } else {
                        losslessCommunicationCheckFailed = true;
                        if (LOGGER.isLoggable(Level.SEVERE)) {
                            LOGGER.severe(
                                    """
                                            Buffer timeout reached while preparing server event for client so further changes will not be pushed to client.
                                             Increase Settings.outputBufferLimit or Settings.outputBufferTimeout to solve this issue.
                                             NB: Settings.outputBufferTimeout should be <= maxIdleTimeout by BrowserPageContent.enableAutoClean method.""");
                        }
                        if (onPayloadLoss.javaScript != null && !onPayloadLoss.javaScript.isBlank()) {
                            // it already contains placeholder for payloadId
                            final ByteBuffer clientAction = BrowserPageAction
                                    .getActionByteBufferForExecuteJS(onPayloadLoss.javaScript);

                            if (wffBMBytesQueue instanceof final ExternalDriveClientTasksWrapperDeque deque) {
                                deque.clearLast();
                                // deque.offerFirst is not allowed as the ClientTasksWrapper will not have
                                // queueEntryId for
                                // ExternalDriveClientTasksWrapperDeque
                                deque.offerLast(new ClientTasksWrapper(clientAction));
                            } else {
                                // do not use clear() as it may not clear from last to first in
                                // ConcurrentLinkedDeque (no guarantee)
                                while (wffBMBytesQueue.pollLast() != null) {
                                    // do nothing here, just to remove all items
                                }
                                wffBMBytesQueue.offerFirst(new ClientTasksWrapper(clientAction));
                            }
                        }
                    }
                } catch (final InterruptedException e) {
                    if (LOGGER.isLoggable(Level.SEVERE)) {
                        LOGGER.log(Level.SEVERE, "Thread interrupted while preparing server event for client.", e);
                    }
                }
            }
        } else {
            pushLockless(clientTasks);
        }
    }

    private void pushWffBMBytesQueue() {

        if (wsListener != null) {

            if (!wffBMBytesHoldPushQueue.isEmpty()) {
                copyCachedBMBytesToMainQ();
            }

            // hasQueuedThreads internally uses transient volatile Node
            // so it must be fine for production use but
            // TODO verify it in deep if it is good for production
            if (!pushWffBMBytesQueueLock.hasQueuedThreads() && !wffBMBytesQueue.isEmpty()) {

                final Thread taskThread = Thread.currentThread();
                waitingThreadRef.getAndSet(taskThread);
                pushWffBMBytesQueueLock.acquireUninterruptibly();

                try {

                    // wsPushInProgress must be implemented here and it is very
                    // important because multiple threads should not push
                    // simultaneously
                    // from same wffBMBytesQueue which will cause incorrect
                    // order of
                    // push

                    ClientTasksWrapper clientTask = wffBMBytesQueue.poll();
                    if (clientTask != null) {
                        AtomicReferenceArray<ByteBuffer> tasks;

                        do {
                            pushQueueSize.decrement();
                            int totalBytesPushed = 0;
                            try {
                                tasks = clientTask.tasks();
                                if (tasks != null) {
                                    final int length = tasks.length();
                                    for (int i = 0; i < length; i++) {
                                        final ByteBuffer task = tasks.get(i);
                                        if (task != null) {
                                            wsListener.push(buildPayloadForClient(task));
                                            final int capacity = task.capacity();
                                            totalBytesPushed += capacity;
                                            clientTask.nullifyTask(capacity, tasks, i);
                                        }
                                    }
                                    clientTask.nullifyTasks();
                                } else {
                                    totalBytesPushed = clientTask.getCurrentSize();
                                }
                            } catch (final PushFailedException e) {
                                if (pushQueueEnabled && wffBMBytesQueue.offerFirst(clientTask)) {
                                    rollbackServerSidePayloadId();
                                    pushQueueSize.increment();
                                }

                                break;
                            } catch (final IllegalStateException | NullPointerException e) {
                                if (wffBMBytesQueue.offerFirst(clientTask)) {
                                    rollbackServerSidePayloadId();
                                    pushQueueSize.increment();
                                }
                                break;
                            } finally {
                                if (outputBufferLimitLock != null && totalBytesPushed > 0) {
                                    outputBufferLimitLock.release(totalBytesPushed);
                                }
                            }

                            if (pushWffBMBytesQueueLock.hasQueuedThreads()) {
                                final Thread waitingThread = waitingThreadRef.get();
                                if (waitingThread != null && !waitingThread.equals(taskThread)
                                        && waitingThread.getPriority() >= taskThread.getPriority()) {
                                    break;
                                }
                            }

                            clientTask = wffBMBytesQueue.poll();

                        } while (clientTask != null);

                    }

                } finally {
                    // should be before unlock
                    waitingThreadRef.compareAndSet(taskThread, null);
                    pushWffBMBytesQueueLock.release();
                }

            }
        } else {
            if (LOGGER.isLoggable(Level.WARNING) && !wsWarningDisabled) {
                LOGGER.warning(
                        "There is no WebSocket listener set, set it with BrowserPage#setWebSocketPushListener method.");
            }
        }
    }

    final DataWffId getNewDataWffId() {
        return rootTag.getSharedObject().getNewDataWffId(ACCESS_OBJECT);
    }

    /**
     * @return the time of websocket message received from client, websocket opened,
     *         or the object created time.
     * @since 3.0.16
     */
    final long getLastClientAccessedTime() {
        return lastClientAccessedTime;
    }

    /**
     * @param timeMillis
     * @since 3.0.16
     */
    final void setLastClientAccessedTime(final long timeMillis) {
        lastClientAccessedTime = timeMillis;
    }

    /**
     * It invokes on the first websocket ping, it invokes only once in the whole
     * lifetime of this {@code BrowserPage} object. All other client events will be
     * received only after invoking this method. It doesn't have any relation to the
     * websocket heartbeat interval or reconnect interval time so changing such
     * configurations will not have any effect on it. Note: It will not invoke
     * whenever the websocket client reconnects to the server but only once in the
     * whole lifetime. <br>
     * <br>
     * Override this method to build the UI instead of
     * {@code BrowserPage#afterRender(AbstractHtml)} if you want to build the UI
     * only if the client is able to communication to the server. The
     * {@code BrowserPage#afterRender(AbstractHtml)} is invoked before this method
     * (and just after the {@code BrowserPage#render()} method) regardless of the
     * client is able to communicate to the server or not.
     *
     * @param rootTag the root of the {@code BrowserPage}
     * @since 3.0.18
     */
    protected void onInitialClientPing(final AbstractHtml rootTag) {

    }

    /**
     * @param message the bytes the received in onmessage
     *
     * @since 2.1.0
     */
    public final void webSocketMessaged(final byte[] message) {
        if (!checkLosslessCommunication(message)) {
            return;
        }
        webSocketMessagedWithoutLosslessCheck(message);
    }

    /**
     * @param message the bytes the received in onmessage
     *
     * @since 2.1.0
     */
    final void webSocketMessagedWithoutLosslessCheck(final byte[] message) {

        // should be after checkLosslessCommunication
        lastClientAccessedTime = System.currentTimeMillis();
        // minimum number of an empty bm message length is 4
        // below that length is not a valid bm message so check
        // message.length < 4
        // later if there is such requirement
        if (message.length < 4) {
            return;
        }

        // executeWffBMTask(message);

        if (inputBufferLimitLock != null) {
            try {
                // onPayloadLoss check should be second
                if (inputBufferLimitLock.tryAcquire(message.length, settings.inputBufferTimeout, TimeUnit.NANOSECONDS)
                        || onPayloadLoss == null) {
                    taskFromClientQ.offer(message);
                } else {
                    losslessCommunicationCheckFailed = true;
                    if (LOGGER.isLoggable(Level.SEVERE)) {
                        LOGGER.severe(
                                """
                                        Buffer timeout reached while processing event from client so further client events will not be received at server side.
                                         Increase Settings.inputBufferLimit or Settings.inputBufferTimeout to solve this issue.
                                         NB: Settings.inputBufferTimeout should be <= maxIdleTimeout by BrowserPageContent.enableAutoClean method.""");
                    }
                    if (onPayloadLoss.serverSideAction != null) {
                        onPayloadLoss.serverSideAction.perform();
                    }
                }
            } catch (final InterruptedException e) {
                if (LOGGER.isLoggable(Level.SEVERE)) {
                    LOGGER.log(Level.SEVERE, "Thread interrupted while preparing server event for client.", e);
                }
            }
        } else {
            taskFromClientQ.offer(message);
        }

        if (!taskFromClientQ.isEmpty()) {
            final Executor executor = this.executor;
            final Executor activeExecutor = executor != null ? executor : WffConfiguration.getVirtualThreadExecutor();
            if (activeExecutor != null) {
                activeExecutor.execute(this::executeTasksFromClientFromQ);
            } else if (externalDrivePath != null) {
                CompletableFuture.runAsync(this::executeTasksFromClientFromQ);
            } else {
                executeTasksFromClientFromQ();
            }
        }

    }

    final boolean checkLosslessCommunication(final byte[] message) {
        if (losslessCommunicationCheckFailed) {
            return false;
        }
        // if lossless communication is enabled
        if (onPayloadLoss != null && message.length > PLACEHOLDER_BYTE_ARRAY_FOR_PAYLOAD_ID.length) {
            final int payloadId = WffBinaryMessageUtil
                    .getIntFromBytes(new byte[] { message[0], message[1], message[2], message[3] });
            if (payloadId == getClientSidePayloadId()) {
                losslessCommunicationCheckFailed = false;
                return true;
            }
            losslessCommunicationCheckFailed = true;
            onPayloadLoss.serverSideAction.perform();
            return false;
        }
        losslessCommunicationCheckFailed = false;
        return true;
    }

    /**
     * @since 3.0.15
     */
    private void executeTasksFromClientFromQ() {

        // hasQueuedThreads internally uses transient volatile Node
        // so it must be fine for production use but
        // TODO verify it in deep if it is good for production
        if (!taskFromClientQLock.hasQueuedThreads() && !taskFromClientQ.isEmpty()) {

            try {

                taskFromClientQLock.acquireUninterruptibly();

                // wsPushInProgress must be implemented here and it is very
                // important because multiple threads should not process
                // simultaneously
                // from same taskFromClientQueue which will cause incorrect
                // order of
                // execution

                byte[] taskFromClient = taskFromClientQ.poll();
                if (taskFromClient != null) {

                    do {

                        try {
                            executeWffBMTask(taskFromClient);
                        } catch (final WffRuntimeException e) {
                            if (!PRODUCTION_MODE) {
                                e.printStackTrace();
                            }
                            if (LOGGER.isLoggable(Level.SEVERE)) {
                                LOGGER.log(Level.SEVERE, e.getMessage(), e);
                            }
                        } catch (final Exception e) {
                            if (!PRODUCTION_MODE) {
                                e.printStackTrace();
                            }
                            if (LOGGER.isLoggable(Level.SEVERE)) {
                                LOGGER.log(Level.SEVERE, "Could not process this data received from client.", e);
                            }
                        } finally {
                            if (inputBufferLimitLock != null) {
                                inputBufferLimitLock.release(taskFromClient.length);
                            }
                        }

                        if (taskFromClientQLock.hasQueuedThreads()) {
                            break;
                        }

                        taskFromClient = taskFromClientQ.poll();

                    } while (taskFromClient != null);

                }

            } finally {
                taskFromClientQLock.release();
            }
        }

    }

    /**
     * Invokes just before {@link BrowserPage#render()} method. This is an empty
     * method in BrowserPage. Override and use. This method invokes only once per
     * object in all of its lifetime.
     *
     * @since 3.0.1
     */
    protected void beforeRender() {
        // NOP override and use
    }

    /**
     * Override and use this method to render html content to the client browser
     * page. This method invokes only once per object in all of its lifetime.
     *
     * @return the object of {@link Html} class which needs to be displayed in the
     *         client browser page.
     *
     */
    public abstract AbstractHtml render();

    /**
     * Invokes after {@link BrowserPage#render()} method. This is an empty method in
     * BrowserPage. Override and use. This method invokes only once per object in
     * all of its lifetime.
     *
     * @param rootTag the rootTag returned by {@link BrowserPage#render()} method.
     * @since 3.0.1
     */
    protected void afterRender(final AbstractHtml rootTag) {
        // NOP override and use
    }

    /**
     * Invokes before any of the {@code toHtmlString} or {@code toOutputStream}
     * methods invoked. This is an empty method in BrowserPage. Override and use it.
     * This method will invoke every time before any of the following methods is
     * called, {@code toHtmlString}, {@code toOutputStream} etc..
     *
     * @param rootTag the rootTag returned by {@link BrowserPage#render()} method.
     * @since 3.0.1
     */
    protected void beforeToHtml(final AbstractHtml rootTag) {
        // NOP override and use
    }

    /**
     * Invokes after any of the {@code toHtmlString} or {@code toOutputStream}
     * methods invoked. This is an empty method in BrowserPage. Override and use it.
     * This method will invoke every time after any of the following methods is
     * called, {@code toHtmlString}, {@code toOutputStream} etc..
     *
     * @param rootTag the rootTag returned by {@link BrowserPage#render()} method.
     * @since 3.0.1
     */
    protected void afterToHtml(final AbstractHtml rootTag) {
        // NOP override and use
    }

    /**
     * @param nameValues
     */
    private void invokeAsychMethod(final List<NameValue> nameValues) {
        // @formatter:off
        // invoke method task format :-
        // { "name": task_byte, "values" : [invoke_method_byte_from_Task_enum]}, {
        // "name": data-wff-id, "values" : [ event_attribute_name ]}
        // { "name": 2, "values" : [[0]]}, { "name":"C55", "values" : ["onclick"]}
        // @formatter:on

        final NameValue wffTagIdAndAttrName = nameValues.get(1);
        final byte[] intBytes = new byte[wffTagIdAndAttrName.getName().length - 1];
        System.arraycopy(wffTagIdAndAttrName.getName(), 1, intBytes, 0, intBytes.length);

        final String wffTagId = new String(wffTagIdAndAttrName.getName(), 0, 1, StandardCharsets.UTF_8)
                + WffBinaryMessageUtil.getIntFromOptimizedBytes(intBytes);

        final byte[][] values = wffTagIdAndAttrName.getValues();

        final byte[] attrIndexOrName = values[0];
        final String eventAttrName;
        if (attrIndexOrName[0] == 0) {
            // 0 represents index as optimized bytes of int

            final int lengthOfOptimizedBytes = attrIndexOrName.length - 1;

            final byte[] indexBytes = new byte[lengthOfOptimizedBytes];
            System.arraycopy(attrIndexOrName, 1, indexBytes, 0, lengthOfOptimizedBytes);

            eventAttrName = AttributeRegistry
                    .getAttrNameByEventAttrIndex(WffBinaryMessageUtil.getIntFromOptimizedBytes(indexBytes));
        } else {
            // the first byte of a string will never start with 0 byte value
            // because a 0 byte value is a null character in charset
            eventAttrName = new String(attrIndexOrName, StandardCharsets.UTF_8);
        }

        final WffBMObject wffBMObject;
        if (values.length > 1) {
            final byte[] wffBMObjectBytes = values[1];
            wffBMObject = new WffBMObject(wffBMObjectBytes, true);
        } else {
            wffBMObject = null;
        }

        // NB: see javadoc on its declaration
        commonLock.lock();

        try {

            final AbstractHtml methodTag = tagByWffId.get(wffTagId);
            if (methodTag != null) {

                final AbstractAttribute attributeByName = methodTag.getAttributeByName(eventAttrName);

                if (attributeByName != null) {

                    if (attributeByName instanceof EventAttribute) {

                        final EventAttribute eventAttr = (EventAttribute) attributeByName;

                        final ServerMethod serverMethod = eventAttr.getServerMethod();

                        final ServerMethod.Event event = new ServerMethod.Event(wffBMObject, methodTag, attributeByName,
                                null, eventAttr.getServerSideData(), uriEvent != null ? uriEvent.uriAfter() : null);

                        final WffBMObject returnedObject;

                        try {
                            returnedObject = serverMethod.invoke(event);
                        } catch (final Exception e) {
                            throw new WffRuntimeException(e.getMessage(), e);
                        }

                        final String jsPostFunctionBody = eventAttr.getJsPostFunctionBody();

                        if (jsPostFunctionBody != null) {

                            final NameValue invokePostFunTask = Task.INVOKE_POST_FUNCTION.getTaskNameValue();
                            final NameValue nameValue = new NameValue();
                            // name as function body string and value at
                            // zeroth index as
                            // wffBMObject bytes

                            // handling JsUtil.toDynamicJs at server side is much better otherwise if the
                            // script is huge the client browser page might get frozen.
                            nameValue.setName(StringUtil.strip(jsPostFunctionBody).getBytes(StandardCharsets.UTF_8));

                            if (returnedObject != null) {
                                nameValue.setValues(new byte[][] { returnedObject.buildBytes(true) });
                            }

                            push(invokePostFunTask, nameValue);
                            if (holdPush.get() == 0) {
                                pushWffBMBytesQueue();
                            }
                        }

                    } else {
                        LOGGER.severe(attributeByName + " is NOT instanceof EventAttribute");
                    }

                } else {
                    LOGGER.severe("no event attribute found for " + attributeByName);
                }

            } else {
                if (!PRODUCTION_MODE) {
                    LOGGER.severe("No tag found for wffTagId " + wffTagId);
                }
            }

        } finally {
            commonLock.unlock();
        }
    }

    /**
     * @param nameValues
     * @throws UnsupportedEncodingException throwing this exception will be removed
     *                                      in future version because its internal
     *                                      implementation will never make this
     *                                      exception due to the code changes since
     *                                      3.0.1.
     */
    private void removeBrowserPageFromContext(final List<NameValue> nameValues) throws UnsupportedEncodingException {
        // @formatter:off
        // invoke custom server method task format :-
        // { "name": task_byte, "values" : [remove_browser_page_byte_from_Task_enum]},
        // { "name": wff-instance-id-bytes, "values" : []}
        // @formatter:on

        final NameValue instanceIdNameValue = nameValues.get(1);

        final String instanceIdToRemove = new String(instanceIdNameValue.getName(), StandardCharsets.UTF_8);

        BrowserPageContext.INSTANCE.removeBrowserPage(getInstanceId(), instanceIdToRemove);
    }

    /**
     * @param nameValues
     */
    private void invokeCustomServerMethod(final List<NameValue> nameValues) {
        // @formatter:off
        // invoke custom server method task format :-
        // { "name": task_byte, "values" :
        // [invoke_custom_server_method_byte_from_Task_enum]},
        // { "name": server method name bytes, "values" : [ wffBMObject bytes ]}
        // { "name": callback function id bytes, "values" : [ ]}
        // @formatter:on

        final NameValue methodNameAndArg = nameValues.get(1);
        final String methodName = new String(methodNameAndArg.getName(), StandardCharsets.UTF_8);

        // NB: see javadoc on its declaration
        commonLock.lock();
        try {

            final ServerMethodWrapper serverMethod = serverMethods.get(methodName);

            if (serverMethod != null) {

                final byte[][] values = methodNameAndArg.getValues();

                final WffBMObject wffBMObject = values.length > 0 ? new WffBMObject(values[0], true) : null;

                final WffBMObject returnedObject;
                try {

                    returnedObject = serverMethod.serverMethod().invoke(new ServerMethod.Event(wffBMObject, null, null,
                            methodName, serverMethod.serverSideData(), uriEvent != null ? uriEvent.uriAfter() : null));

                } catch (final Exception e) {
                    throw new WffRuntimeException(e.getMessage(), e);
                }

                String callbackFunId = null;

                if (nameValues.size() > 2) {
                    final NameValue callbackFunNameValue = nameValues.get(2);
                    callbackFunId = new String(callbackFunNameValue.getName(), StandardCharsets.UTF_8);
                }

                if (callbackFunId != null) {
                    final NameValue invokeCallbackFuncTask = Task.INVOKE_CALLBACK_FUNCTION.getTaskNameValue();

                    final NameValue nameValue = new NameValue();
                    nameValue.setName(callbackFunId.getBytes(StandardCharsets.UTF_8));

                    if (returnedObject != null) {
                        nameValue.setValues(new byte[][] { returnedObject.buildBytes(true) });
                    }

                    push(invokeCallbackFuncTask, nameValue);
                    if (holdPush.get() == 0) {
                        pushWffBMBytesQueue();
                    }

                }

            } else {
                LOGGER.warning(methodName + " doesn't exist, please add it as browserPage.addServerMethod(\""
                        + methodName + "\", serverMethod)");
            }
        } finally {
            commonLock.unlock();
        }

    }

    /**
     * executes the task in the given wff binary message. <br>
     * For WFF authors :- Make sure that the passing {@code message} is not empty
     * while consuming this method, just as made conditional checking in
     * {@code BrowserPage#webSocketMessaged(byte[])} method.
     *
     * @throws UnsupportedEncodingException throwing this exception will be removed
     *                                      in future version because its internal
     *                                      implementation will never make this
     *                                      exception due to the code changes since
     *                                      3.0.1.
     *
     * @since 2.0.0
     */
    private void executeWffBMTask(final byte[] message) throws UnsupportedEncodingException {

        // message is a BM Message which never starts with 0, if it starts with 0 it
        // means it is prepended by id placeholder
        final int offset;
        final int length;
        if (onPayloadLoss != null) {
            offset = PLACEHOLDER_BYTE_ARRAY_FOR_PAYLOAD_ID.length;
            length = message.length - offset;
        } else {
            offset = 0;
            length = message.length;
        }

        final List<NameValue> nameValues = WffBinaryMessageUtil.VERSION_1.parse(message, offset, length);

        final NameValue task = nameValues.get(0);
        final byte taskValue = task.getValues()[0][0];

        if (Task.TASK.getValueByte() == task.getName()[0]) {

            // IM stands for Invoke Method
            if (taskValue == Task.INVOKE_ASYNC_METHOD.getValueByte()) {

                invokeAsychMethod(nameValues);

            } else if (taskValue == Task.INVOKE_CUSTOM_SERVER_METHOD.getValueByte()) {

                invokeCustomServerMethod(nameValues);

            } else if (taskValue == Task.REMOVE_BROWSER_PAGE.getValueByte()) {

                removeBrowserPageFromContext(nameValues);

            } else if (taskValue == Task.INITIAL_WS_OPEN.getValueByte()) {
                if (!onInitialClientPingInvoked) {
                    if (nameValues.size() > 1) {
                        // NB: see javadoc on its declaration
                        commonLock.lock();
                        try {
                            // NB: should be in reverse order as the token should be set first then only the
                            // setURI is to be called.
                            for (int i = nameValues.size() - 1; i > 0; i--) {
                                final NameValue nm = nameValues.get(i);
                                if (nm.getName()[0] == Task.SET_LS_TOKEN.getValueByte()) {
                                    final WffBMArray bmArray = new WffBMArray(nm.getValues()[0]);
                                    for (final Object each : bmArray) {
                                        if (each instanceof final WffBMObject bmObj) {
                                            final String key = (String) bmObj.getValue("k");
                                            final String value = (String) bmObj.getValue("v");
                                            final String wt = (String) bmObj.getValue("wt");
                                            final int id = ((Double) bmObj.getValue("id")).intValue();
                                            final TokenWrapper tokenWrapper = getTokenWrapper(key, true);
                                            if (tokenWrapper != null) {
                                                final long stamp = tokenWrapper.lock.writeLock();
                                                try {
                                                    final long writeTime = Long.parseLong(wt);
                                                    tokenWrapper.setTokenAndWriteTime(value, null, writeTime, id);
                                                } finally {
                                                    tokenWrapper.lock.unlockWrite(stamp);
                                                }
                                            }
                                        }
                                    }
                                } else if (nm.getName()[0] == Task.SET_URI.getValueByte()) {
                                    final URIEventInitiator eventInitiator = URIEventInitiator
                                            .get(nm.getValues()[0][0]);
                                    final String urlPath = new String(nm.getValues()[1], StandardCharsets.UTF_8);
                                    setURI(false, urlPath, eventInitiator, false);
                                }
                            }
                            onInitialClientPingInvoked = true;
                            onInitialClientPing(rootTag);
                        } catch (final Exception e) {
                            if (LOGGER.isLoggable(Level.SEVERE)) {
                                LOGGER.log(Level.SEVERE, "Exception while executing onInitialWSOpen", e);
                            }
                        } finally {
                            commonLock.unlock();
                        }
                    }
                }
            } else if (taskValue == Task.CLIENT_PATHNAME_CHANGED.getValueByte()) {

                if (nameValues.size() > 1) {

                    // NB: see javadoc on its declaration
                    commonLock.lock();
                    try {
                        final NameValue pathnameNV = nameValues.get(1);
                        final String urlPath = new String(pathnameNV.getName(), StandardCharsets.UTF_8);
                        final URIEventInitiator eventInitiator = URIEventInitiator.get(pathnameNV.getValues()[0][0]);
                        final boolean replace = pathnameNV.getValues()[0][1] == 1;

                        setURI(false, urlPath, eventInitiator, replace);

                        String callbackFunId = null;

                        if (nameValues.size() > 2) {
                            final NameValue callbackFunNameValue = nameValues.get(2);
                            callbackFunId = new String(callbackFunNameValue.getName(), StandardCharsets.UTF_8);
                        }

                        if (callbackFunId != null) {
                            final NameValue invokeCallbackFuncTask = Task.INVOKE_CALLBACK_FUNCTION.getTaskNameValue();

                            final NameValue nameValue = new NameValue();
                            nameValue.setName(callbackFunId.getBytes(StandardCharsets.UTF_8));

                            push(invokeCallbackFuncTask, nameValue);
                            if (holdPush.get() == 0) {
                                pushWffBMBytesQueue();
                            }

                        }

                    } catch (final Exception e) {
                        if (LOGGER.isLoggable(Level.SEVERE)) {
                            LOGGER.log(Level.SEVERE, "Exception while executing setURI", e);
                        }
                    } finally {
                        commonLock.unlock();
                    }
                }

            } else if (taskValue == Task.SET_LS_ITEM.getValueByte()) {
                try {
                    final LocalStorageImpl localStorage = getLocalStorage();
                    if (nameValues.size() > 1 && localStorage != null) {
                        final NameValue nameValue = nameValues.get(1);
                        final LSConsumerEventRecord lsEventRecord = localStorage.setItemConsumers
                                .remove(WffBinaryMessageUtil.getIntFromOptimizedBytes(nameValue.getName()));
                        if (lsEventRecord != null) {
                            lsEventRecord.consumer().accept(lsEventRecord.event());
                        }
                    }
                } catch (final Exception e) {
                    if (LOGGER.isLoggable(Level.SEVERE)) {
                        LOGGER.log(Level.SEVERE, "Exception while executing localStorage.setItem", e);
                    }
                }

            } else if (taskValue == Task.GET_LS_ITEM.getValueByte()) {
                try {
                    final LocalStorageImpl localStorage = getLocalStorage();
                    if (nameValues.size() > 1 && localStorage != null) {
                        final NameValue nameValue = nameValues.get(1);
                        final LSConsumerEventRecord lsEventRecord = localStorage.getItemConsumers
                                .remove(WffBinaryMessageUtil.getIntFromOptimizedBytes(nameValue.getName()));
                        if (lsEventRecord != null) {
                            final LocalStorage.Event event;
                            final byte[][] values = nameValue.getValues();
                            if (values.length > 1) {
                                final String value = new String(values[0], StandardCharsets.UTF_8);
                                final long updatedTimeMillis = Long
                                        .parseLong(new String(values[1], StandardCharsets.UTF_8));
                                event = new LocalStorage.Event(lsEventRecord.event().key(),
                                        lsEventRecord.event().operationTimeMillis(),
                                        new ItemData(value, updatedTimeMillis));

                            } else {
                                event = lsEventRecord.event();
                            }
                            lsEventRecord.consumer().accept(event);
                        }
                    }
                } catch (final Exception e) {
                    if (LOGGER.isLoggable(Level.SEVERE)) {
                        LOGGER.log(Level.SEVERE, "Exception while executing localStorage.getItem", e);
                    }
                }

            } else if (taskValue == Task.REMOVE_LS_ITEM.getValueByte()
                    || taskValue == Task.REMOVE_AND_GET_LS_ITEM.getValueByte()) {
                try {
                    final LocalStorageImpl localStorage = getLocalStorage();
                    if (nameValues.size() > 1 && localStorage != null) {
                        final NameValue nameValue = nameValues.get(1);
                        final LSConsumerEventRecord lsEventRecord = localStorage.removeItemConsumers
                                .remove(WffBinaryMessageUtil.getIntFromOptimizedBytes(nameValue.getName()));
                        if (lsEventRecord != null) {
                            LocalStorage.Event event = null;
                            if (taskValue == Task.REMOVE_AND_GET_LS_ITEM.getValueByte()) {
                                final byte[][] values = nameValue.getValues();
                                if (values.length > 1) {
                                    final String value = new String(values[0], StandardCharsets.UTF_8);
                                    final long updatedTimeMillis = Long
                                            .parseLong(new String(values[1], StandardCharsets.UTF_8));
                                    event = new LocalStorage.Event(lsEventRecord.event().key(),
                                            lsEventRecord.event().operationTimeMillis(),
                                            new ItemData(value, updatedTimeMillis));
                                }
                            } else {
                                event = lsEventRecord.event();
                            }
                            lsEventRecord.consumer().accept(event);
                        }
                    }
                } catch (final Exception e) {
                    if (LOGGER.isLoggable(Level.SEVERE)) {
                        final String methodName = taskValue == Task.REMOVE_LS_ITEM.getValueByte() ? "removeItem"
                                : "getAndRemoveItem";
                        LOGGER.log(Level.SEVERE, "Exception while executing localStorage." + methodName, e);
                    }
                }
            } else if (taskValue == Task.CLEAR_LS.getValueByte()) {
                try {
                    final LocalStorageImpl localStorage = getLocalStorage();
                    if (nameValues.size() > 1 && localStorage != null) {
                        final NameValue nameValue = nameValues.get(1);
                        final LSConsumerEventRecord lsEventRecord = localStorage.clearItemsConsumers
                                .remove(WffBinaryMessageUtil.getIntFromOptimizedBytes(nameValue.getName()));
                        if (lsEventRecord != null) {
                            lsEventRecord.consumer().accept(lsEventRecord.event());
                        }
                    }
                } catch (final Exception e) {
                    if (LOGGER.isLoggable(Level.SEVERE)) {
                        LOGGER.log(Level.SEVERE, "Exception while executing localStorage.clear", e);
                    }
                }
            } else if (taskValue == Task.SET_LS_TOKEN.getValueByte()) {
                try {
                    // name: id
                    // values: key, value, write time
                    if (nameValues.size() > 1) {
                        final NameValue nameValue = nameValues.get(1);
                        final byte[][] values = nameValue.getValues();
                        if (values.length > 2) {
                            final String key = new String(values[0], StandardCharsets.UTF_8);
                            // create should be true as the initial value in a new node could be null
                            final TokenWrapper tokenWrapper = getTokenWrapper(key, true);
                            if (tokenWrapper != null) {
                                final long stamp = tokenWrapper.lock.writeLock();
                                try {
                                    tokenWrapper.setTokenAndWriteTime(null, values[1],
                                            Long.parseLong(new String(values[2], StandardCharsets.UTF_8)),
                                            WffBinaryMessageUtil.getIntFromOptimizedBytes(nameValue.getName()));
                                } finally {
                                    tokenWrapper.lock.unlockWrite(stamp);
                                }
                            }
                        }

                    }
                } catch (final Exception e) {
                    if (LOGGER.isLoggable(Level.SEVERE)) {
                        LOGGER.log(Level.SEVERE, "Exception while updating token at server", e);
                    }
                }
            } else if (taskValue == Task.REMOVE_LS_TOKEN.getValueByte()) {
                try {
                    // name: id
                    // values: key, write time
                    if (nameValues.size() > 1) {
                        final NameValue nameValue = nameValues.get(1);
                        final byte[][] values = nameValue.getValues();
                        if (values.length > 1) {
                            final String key = new String(values[0], StandardCharsets.UTF_8);
                            final LocalStorageImpl localStorage = getLocalStorage();
                            final TokenWrapper tokenWrapper = localStorage != null
                                    ? localStorage.tokenWrapperByKey.get(key)
                                    : null;
                            if (tokenWrapper != null) {
                                final long stamp = tokenWrapper.lock.writeLock();
                                try {
                                    final int updatedId = WffBinaryMessageUtil
                                            .getIntFromOptimizedBytes(nameValue.getName());
                                    tokenWrapper.setTokenAndWriteTime(null, null,
                                            Long.parseLong(new String(values[1], StandardCharsets.UTF_8)), updatedId,
                                            key, localStorage.tokenWrapperByKey);

                                } finally {
                                    tokenWrapper.lock.unlockWrite(stamp);
                                }
                            }
                        }
                    }
                } catch (final Exception e) {
                    if (LOGGER.isLoggable(Level.SEVERE)) {
                        LOGGER.log(Level.SEVERE, "Exception while updating token at server", e);
                    }
                }
            }

        }
    }

    private TokenWrapper getTokenWrapper(final String key, final boolean create) {
        final LocalStorageImpl localStorage = getLocalStorage();
        return localStorage != null
                ? (create ? localStorage.tokenWrapperByKey.computeIfAbsent(key, TokenWrapper::new)
                        : localStorage.tokenWrapperByKey.get(key))
                : null;
    }

    private LocalStorageImpl getLocalStorage() {
        final Reference<BrowserPageSessionImpl> sessionRef = this.sessionRef;
        final BrowserPageSessionImpl sessionTmp = sessionRef != null ? sessionRef.get() : null;
        if (sessionTmp == null) {
            final BrowserPageSessionImpl session = BrowserPageContext.INSTANCE.getSessionImplByInstanceId(instanceId);
            if (session != null) {
                setSession(session);
                return session.localStorage;
            }
        }
        return sessionTmp != null ? sessionTmp.localStorage : null;
    }

    void setSession(final BrowserPageSessionImpl session) {
        sessionRef = new WeakReference<>(session);
    }

    private void invokeAfterSetURIAtClient() {
        final NameValue taskNameValue = Task.AFTER_SET_URI.getTaskNameValue();
        push(taskNameValue);
        if (holdPush.get() == 0) {
            pushWffBMBytesQueue();
        }
    }

    private void invokeSetURIAtClient(final String uriBefore, final String uriAfter, final boolean replace) {

        final WffBMObject event = new WffBMObject();
        // ua for uriAfter, ub for uriBefore, o for origin, r for replace
        event.put("ub", uriBefore != null ? BMValueType.STRING : BMValueType.NULL, uriBefore);
        event.put("ua", uriAfter != null ? BMValueType.STRING : BMValueType.NULL, uriAfter);
        // S for server
        event.put("o", BMValueType.STRING, "S");
        event.put("r", BMValueType.BOOLEAN, replace);
        final NameValue taskNameValue = Task.SET_URI.getTaskNameValue(event.buildBytes(true));
        push(taskNameValue);
        if (holdPush.get() == 0) {
            pushWffBMBytesQueue();
        }
    }

    private static void putId(final WffBMObject event, final int id) {
        final WffBMByteArray idBytes = new WffBMByteArray();
        try {
            idBytes.write(WffBinaryMessageUtil.getOptimizedBytesFromInt(id));
            event.put("id", BMValueType.BM_BYTE_ARRAY, idBytes);
        } catch (final IOException e) {
            event.put("id", BMValueType.NULL, null);
        }
    }

    private void invokeSetLocalStorageTokenAtClient(final int id, final String key, final String value,
            final long writeTime) {
        final WffBMObject event = new WffBMObject();
        putId(event, id);
        // k for key
        event.put("k", BMValueType.STRING, key);
        // v for value
        event.put("v", BMValueType.STRING, value);
        // wt for write time
        event.put("wt", BMValueType.STRING, String.valueOf(writeTime));
        final NameValue taskNameValue = Task.SET_LS_TOKEN.getTaskNameValue(event.buildBytes(true));
        push(taskNameValue);
        if (holdPush.get() == 0) {
            pushWffBMBytesQueue();
        }
    }

    private void invokeSetLocalStorageItemAtClient(final int id, final String key, final String value,
            final long writeTime, final boolean callback) {
        final WffBMObject event = new WffBMObject();
        putId(event, id);
        // k for key
        event.put("k", BMValueType.STRING, key);
        // v for value
        event.put("v", BMValueType.STRING, value);
        // wt for write time
        event.put("wt", BMValueType.STRING, String.valueOf(writeTime));
        if (callback) {
            event.put("cb", BMValueType.BOOLEAN, true);
        }

        final NameValue taskNameValue = Task.SET_LS_ITEM.getTaskNameValue(event.buildBytes(true));
        push(taskNameValue);
        if (holdPush.get() == 0) {
            pushWffBMBytesQueue();
        }
    }

    private void invokeGetLocalStorageItemAtClient(final int id, final String key) {
        final WffBMObject event = new WffBMObject();
        putId(event, id);
        // k for key
        event.put("k", BMValueType.STRING, key);
        final NameValue taskNameValue = Task.GET_LS_ITEM.getTaskNameValue(event.buildBytes(true));
        push(taskNameValue);
        if (holdPush.get() == 0) {
            pushWffBMBytesQueue();
        }
    }

    private void invokeRemoveLocalStorageItemAtClient(final int id, final String key, final long writeTime,
            final boolean callback) {
        final WffBMObject event = new WffBMObject();
        putId(event, id);
        // k for key
        event.put("k", BMValueType.STRING, key);
        // wt for write time
        event.put("wt", BMValueType.STRING, String.valueOf(writeTime));
        if (callback) {
            event.put("cb", BMValueType.BOOLEAN, true);
        }
        final NameValue taskNameValue = Task.REMOVE_LS_ITEM.getTaskNameValue(event.buildBytes(true));
        push(taskNameValue);
        if (holdPush.get() == 0) {
            pushWffBMBytesQueue();
        }
    }

    private void invokeRemoveLocalStorageTokenAtClient(final int id, final String key, final long writeTime) {
        final WffBMObject event = new WffBMObject();
        putId(event, id);
        // wt for write time
        event.put("wt", BMValueType.STRING, String.valueOf(writeTime));
        // k for key
        event.put("k", BMValueType.STRING, key);
        final NameValue taskNameValue = Task.REMOVE_LS_TOKEN.getTaskNameValue(event.buildBytes(true));
        push(taskNameValue);
        if (holdPush.get() == 0) {
            pushWffBMBytesQueue();
        }
    }

    private void invokeRemoveAndGetLocalStorageItemAtClient(final int id, final String key, final long writeTime) {
        final WffBMObject event = new WffBMObject();
        putId(event, id);
        // k for key
        event.put("k", BMValueType.STRING, key);
        // wt for write time
        event.put("wt", BMValueType.STRING, String.valueOf(writeTime));
        event.put("cb", BMValueType.BOOLEAN, true);
        final NameValue taskNameValue = Task.REMOVE_AND_GET_LS_ITEM.getTaskNameValue(event.buildBytes(true));
        push(taskNameValue);
        if (holdPush.get() == 0) {
            pushWffBMBytesQueue();
        }
    }

    /**
     * @param id
     * @param type      D for data, T for token, DT for both data and token.
     * @param writeTime
     * @param callback
     */
    private void invokeClearLocalStorageItemsAtClient(final int id, final String type, final long writeTime,
            final boolean callback) {
        final WffBMObject event = new WffBMObject();
        putId(event, id);
        // wt for write time
        event.put("wt", BMValueType.STRING, String.valueOf(writeTime));
        // tp for type
        event.put("tp", BMValueType.STRING, type);
        if (callback) {
            event.put("cb", BMValueType.BOOLEAN, true);
        }
        final NameValue taskNameValue = Task.CLEAR_LS.getTaskNameValue(event.buildBytes(true));
        push(taskNameValue);
        if (holdPush.get() == 0) {
            pushWffBMBytesQueue();
        }
    }

    private void addAttrValueChangeListener(final AbstractHtml abstractHtml) {
        abstractHtml.getSharedObject().setValueChangeListener(new AttributeValueChangeListenerImpl(this, tagByWffId),
                ACCESS_OBJECT);
    }

    private void addDataWffIdAttribute(final AbstractHtml abstractHtml) {

        final Deque<Set<AbstractHtml>> childrenStack = new ArrayDeque<>();
        final Set<AbstractHtml> initialSet = Set.of(abstractHtml);
        childrenStack.push(initialSet);

        Set<AbstractHtml> children;

        while ((children = childrenStack.poll()) != null) {

            for (final AbstractHtml child : children) {

                if (TagUtil.isTagged(child)) {
                    if (child.getDataWffId() == null) {
                        child.setDataWffId(getNewDataWffId());
                    }
                    tagByWffId.put(child.getDataWffId().getValue(), child);
                }

                final Set<AbstractHtml> subChildren = child.getChildren(ACCESS_OBJECT);

                if (subChildren != null && !subChildren.isEmpty()) {
                    childrenStack.push(subChildren);
                }

            }

        }

    }

    private void embedWffScriptIfRequired(final AbstractHtml abstractHtml, final String wsUrlWithInstanceId) {

        if (wffScriptTagId != null && tagByWffId.containsKey(wffScriptTagId.getValue())) {
            // no need to add script tag if it exists in the ui
            return;
        }

        final Deque<Set<AbstractHtml>> childrenStack = new ArrayDeque<>();
        final Set<AbstractHtml> initialSet = Set.of(abstractHtml);
        childrenStack.push(initialSet);

        boolean headAndbodyTagMissing = true;

        Set<AbstractHtml> children;

        outerLoop: while ((children = childrenStack.poll()) != null) {

            for (final AbstractHtml child : children) {

                if ((enableDeferOnWffScript && TagNameConstants.HEAD.equals(child.getTagName()))
                        || (!enableDeferOnWffScript && TagNameConstants.BODY.equals(child.getTagName()))) {

                    headAndbodyTagMissing = false;

                    wffScriptTagId = getNewDataWffId();

                    final Script script = new Script(null, new Type(Type.TEXT_JAVASCRIPT));

                    script.setDataWffId(wffScriptTagId);

                    final boolean losslessCommunication = onPayloadLoss != null;
                    final String onPayloadLossJS = losslessCommunication ? onPayloadLoss.javaScript : "";

                    final String wffJs = WffJsFile.getAllOptimizedContent(wsUrlWithInstanceId, getInstanceId(),
                            removePrevFromBrowserContextOnTabInit, removeFromBrowserContextOnTabClose,
                            (wsHeartbeatInterval > 0 ? wsHeartbeatInterval : wsDefaultHeartbeatInterval),
                            (wsReconnectInterval > 0 ? wsReconnectInterval : wsDefaultReconnectInterval),
                            autoremoveWffScript, losslessCommunication, onPayloadLossJS);

                    if (enableDeferOnWffScript) {
                        // byes are in UTF-8 so charset=utf-8 is explicitly
                        // specified
                        // Defer must be first argument
                        script.addAttributes(new Defer(null), new Src("data:text/javascript;charset=utf-8;base64,"
                                + HashUtil.base64FromUtf8Bytes(wffJs.getBytes(StandardCharsets.UTF_8))));
                    } else {
                        new NoTag(script, wffJs);
                    }

                    if (nonceForWffScriptTag != null) {
                        script.addAttributes(nonceForWffScriptTag);
                    }

                    // to avoid invoking listener
                    child.addChild(ACCESS_OBJECT, script, false);

                    // ConcurrentHashMap cannot contain null as value
                    tagByWffId.put(wffScriptTagId.getValue(), script);

                    break outerLoop;
                }

                final Set<AbstractHtml> subChildren = child.getChildren(ACCESS_OBJECT);

                if (subChildren != null && !subChildren.isEmpty()) {
                    childrenStack.push(subChildren);
                }
            }

        }

        if (headAndbodyTagMissing) {
            wffScriptTagId = getNewDataWffId();

            final Script script = new Script(null, new Type(Type.TEXT_JAVASCRIPT));

            script.setDataWffId(wffScriptTagId);

            final boolean losslessCommunication = onPayloadLoss != null;
            final String onPayloadLossJS = losslessCommunication ? onPayloadLoss.javaScript : "";
            final String wffJs = WffJsFile.getAllOptimizedContent(wsUrlWithInstanceId, getInstanceId(),
                    removePrevFromBrowserContextOnTabInit, removeFromBrowserContextOnTabClose,
                    (wsHeartbeatInterval > 0 ? wsHeartbeatInterval : wsDefaultHeartbeatInterval),
                    (wsReconnectInterval > 0 ? wsReconnectInterval : wsDefaultReconnectInterval), autoremoveWffScript,
                    losslessCommunication, onPayloadLossJS);

            if (enableDeferOnWffScript) {
                // byes are in UTF-8 so charset=utf-8 is explicitly specified
                // Defer must be first argument
                script.addAttributes(new Defer(null), new Src("data:text/javascript;charset=utf-8;base64,"
                        + HashUtil.base64FromUtf8Bytes(wffJs.getBytes(StandardCharsets.UTF_8))));
            } else {
                new NoTag(script, wffJs);
            }

            if (nonceForWffScriptTag != null) {
                script.addAttributes(nonceForWffScriptTag);
            }

            // to avoid invoking listener
            abstractHtml.addChild(ACCESS_OBJECT, script, false);

            // ConcurrentHashMap cannot contain null as value
            tagByWffId.put(wffScriptTagId.getValue(), script);

        }

    }

    private void addChildTagAppendListener(final AbstractHtml abstractHtml) {

        abstractHtml.getSharedObject().setChildTagAppendListener(
                new ChildTagAppendListenerImpl(this, ACCESS_OBJECT, tagByWffId), ACCESS_OBJECT);
    }

    private void addChildTagRemoveListener(final AbstractHtml abstractHtml) {

        abstractHtml.getSharedObject().setChildTagRemoveListener(
                new ChildTagRemoveListenerImpl(this, ACCESS_OBJECT, tagByWffId), ACCESS_OBJECT);
    }

    private void addAttributeAddListener(final AbstractHtml abstractHtml) {
        abstractHtml.getSharedObject().setAttributeAddListener(new AttributeAddListenerImpl(this, ACCESS_OBJECT),
                ACCESS_OBJECT);
    }

    private void addAttributeRemoveListener(final AbstractHtml abstractHtml) {
        abstractHtml.getSharedObject().setAttributeRemoveListener(
                new AttributeRemoveListenerImpl(this, ACCESS_OBJECT, tagByWffId), ACCESS_OBJECT);
    }

    private void addInnerHtmlAddListener(final AbstractHtml abstractHtml) {
        abstractHtml.getSharedObject()
                .setInnerHtmlAddListener(new InnerHtmlAddListenerImpl(this, ACCESS_OBJECT, tagByWffId), ACCESS_OBJECT);
    }

    private void addInsertTagsBeforeListener(final AbstractHtml abstractHtml) {
        abstractHtml.getSharedObject().setInsertTagsBeforeListener(
                new InsertTagsBeforeListenerImpl(this, ACCESS_OBJECT, tagByWffId), ACCESS_OBJECT);
    }

    private void addInsertAfterListener(final AbstractHtml abstractHtml) {
        abstractHtml.getSharedObject()
                .setInsertAfterListener(new InsertAfterListenerImpl(this, ACCESS_OBJECT, tagByWffId), ACCESS_OBJECT);
    }

    private void addReplaceListener(final AbstractHtml abstractHtml) {
        abstractHtml.getSharedObject().setReplaceListener(new ReplaceListenerImpl(this, ACCESS_OBJECT, tagByWffId),
                ACCESS_OBJECT);
    }

    private void addWffBMDataUpdateListener(final AbstractHtml abstractHtml) {
        abstractHtml.getSharedObject().setWffBMDataUpdateListener(new WffBMDataUpdateListenerImpl(this, ACCESS_OBJECT),
                ACCESS_OBJECT);
    }

    private void addWffBMDataDeleteListener(final AbstractHtml abstractHtml) {
        abstractHtml.getSharedObject().setWffBMDataDeleteListener(new WffBMDataDeleteListenerImpl(this, ACCESS_OBJECT),
                ACCESS_OBJECT);
    }

    private void addPushQueue(final AbstractHtml rootTag) {
        rootTag.getSharedObject().setPushQueue(() -> {
            if (holdPush.get() == 0) {
                pushWffBMBytesQueue();
            }
        }, ACCESS_OBJECT);
    }

    /**
     * NB: this method should not be called under {@link BrowserPage#render()}
     * method because this method internally calls {@link BrowserPage#render()}
     * method.
     *
     * @return {@code String} equalent to the html string of the tag including the
     *         child tags.
     *
     */
    public String toHtmlString() {
        initAbstractHtml();
        wsWarningDisabled = true;
        beforeToHtml(rootTag);
        wsWarningDisabled = false;
        final String htmlString = rootTag.toHtmlString(true);
        wsWarningDisabled = true;
        afterToHtml(rootTag);
        wsWarningDisabled = false;
        return htmlString;
    }

    /**
     * NB: this method should not be called under {@link BrowserPage#render()}
     * method because this method internally calls {@link BrowserPage#render()}
     * method. rebuilds the html string of the tag including the child tags/values
     * if parameter is true, otherwise returns the html string prebuilt and kept in
     * the cache.
     *
     * @param rebuild true to rebuild &amp; false to return previously built string.
     * @return {@code String} equalent to the html string of the tag including the
     *         child tags.
     *
     * @since 2.1.4
     */
    public String toHtmlString(final boolean rebuild) {
        initAbstractHtml();
        wsWarningDisabled = true;
        beforeToHtml(rootTag);
        wsWarningDisabled = false;
        final String htmlString = rootTag.toHtmlString(rebuild);
        wsWarningDisabled = true;
        afterToHtml(rootTag);
        wsWarningDisabled = false;
        return htmlString;
    }

    /**
     * NB: this method should not be called under {@link BrowserPage#render()}
     * method because this method internally calls {@link BrowserPage#render()}
     * method.
     *
     * @param os the object of {@code OutputStream} to write to.
     * @return the total number of bytes written
     * @throws IOException
     * @since 2.1.4 void toOutputStream
     * @since 2.1.8 int toOutputStream
     */
    public int toOutputStream(final OutputStream os) throws IOException {
        initAbstractHtml();
        wsWarningDisabled = true;
        beforeToHtml(rootTag);
        wsWarningDisabled = false;
        final int totalWritten = rootTag.toOutputStream(os, true);
        wsWarningDisabled = true;
        afterToHtml(rootTag);
        wsWarningDisabled = false;
        return totalWritten;
    }

    /**
     * NB: this method should not be called under {@link BrowserPage#render()}
     * method because this method internally calls {@link BrowserPage#render()}
     * method.
     *
     * @param os      the object of {@code OutputStream} to write to.
     * @param rebuild true to rebuild &amp; false to write previously built bytes.
     * @return the total number of bytes written
     * @throws IOException
     * @since 2.1.4 void toOutputStream
     * @since 2.1.8 int toOutputStream
     */
    public int toOutputStream(final OutputStream os, final boolean rebuild) throws IOException {
        initAbstractHtml();
        wsWarningDisabled = true;
        beforeToHtml(rootTag);
        wsWarningDisabled = false;
        final int totalWritten = rootTag.toOutputStream(os, rebuild);
        wsWarningDisabled = true;
        afterToHtml(rootTag);
        wsWarningDisabled = false;
        return totalWritten;
    }

    /**
     * NB: this method should not be called under {@link BrowserPage#render()}
     * method because this method internally calls {@link BrowserPage#render()}
     * method.
     *
     * @param os           the object of {@code OutputStream} to write to.
     * @param rebuild      true to rebuild &amp; false to write previously built
     *                     bytes.
     * @param flushOnWrite true to flush on each write to OutputStream
     * @return the total number of bytes written
     * @throws IOException
     * @since 3.0.2
     */
    public int toOutputStream(final OutputStream os, final boolean rebuild, final boolean flushOnWrite)
            throws IOException {
        initAbstractHtml();
        wsWarningDisabled = true;
        beforeToHtml(rootTag);
        wsWarningDisabled = false;
        final int totalWritten = rootTag.toOutputStream(os, rebuild, flushOnWrite);
        wsWarningDisabled = true;
        afterToHtml(rootTag);
        wsWarningDisabled = false;
        return totalWritten;
    }

    /**
     * NB: this method should not be called under {@link BrowserPage#render()}
     * method because this method internally calls {@link BrowserPage#render()}
     * method.
     *
     * @param os      the object of {@code OutputStream} to write to.
     * @param charset the charset
     * @return the total number of bytes written
     * @throws IOException
     * @since 2.1.4 void toOutputStream
     * @since 2.1.8 int toOutputStream
     */
    public int toOutputStream(final OutputStream os, final String charset) throws IOException {
        return toOutputStream(os, Charset.forName(charset));
    }

    /**
     * NB: this method should not be called under {@link BrowserPage#render()}
     * method because this method internally calls {@link BrowserPage#render()}
     * method.
     *
     * @param os      the object of {@code OutputStream} to write to.
     * @param charset the charset
     * @return the total number of bytes written
     * @throws IOException
     * @since 2.1.4 void toOutputStream
     * @since 12.0.0-beta.4
     */
    public int toOutputStream(final OutputStream os, final Charset charset) throws IOException {
        initAbstractHtml();
        wsWarningDisabled = true;
        beforeToHtml(rootTag);
        wsWarningDisabled = false;
        final int totalWritten = rootTag.toOutputStream(os, true, charset);
        wsWarningDisabled = true;
        afterToHtml(rootTag);
        wsWarningDisabled = false;
        return totalWritten;
    }

    /**
     * NB: this method should not be called under {@link BrowserPage#render()}
     * method because this method internally calls {@link BrowserPage#render()}
     * method.
     *
     * @param os           the object of {@code OutputStream} to write to.
     * @param charset      the charset
     * @param flushOnWrite true to flush on each write to OutputStream
     * @return the total number of bytes written
     * @throws IOException
     * @since 3.0.2
     */
    public int toOutputStream(final OutputStream os, final Charset charset, final boolean flushOnWrite)
            throws IOException {
        initAbstractHtml();
        wsWarningDisabled = true;
        beforeToHtml(rootTag);
        wsWarningDisabled = false;
        final int totalWritten = rootTag.toOutputStream(os, true, charset, flushOnWrite);
        wsWarningDisabled = true;
        afterToHtml(rootTag);
        wsWarningDisabled = false;
        return totalWritten;
    }

    /**
     * NB: this method should not be called under {@link BrowserPage#render()}
     * method because this method internally calls {@link BrowserPage#render()}
     * method.
     *
     * @param os      the object of {@code OutputStream} to write to.
     * @param rebuild true to rebuild &amp; false to write previously built bytes.
     * @param charset the charset
     * @return the total number of bytes written
     * @throws IOException
     * @since 2.1.4 void toOutputStream
     * @since 2.1.8 int toOutputStream
     */
    public int toOutputStream(final OutputStream os, final boolean rebuild, final String charset) throws IOException {
        initAbstractHtml();
        wsWarningDisabled = true;
        beforeToHtml(rootTag);
        wsWarningDisabled = false;
        final int totalWritten = rootTag.toOutputStream(os, rebuild, charset);
        wsWarningDisabled = true;
        afterToHtml(rootTag);
        wsWarningDisabled = false;
        return totalWritten;
    }

    /**
     * NB: this method should not be called under {@link BrowserPage#render()}
     * method because this method internally calls {@link BrowserPage#render()}
     * method.
     *
     * @param os           the object of {@code OutputStream} to write to.
     * @param rebuild      true to rebuild &amp; false to write previously built
     *                     bytes.
     * @param charset      the charset
     * @param flushOnWrite true to flush on each write to OutputStream
     * @return the total number of bytes written
     * @throws IOException
     * @since 3.0.2
     */
    public int toOutputStream(final OutputStream os, final boolean rebuild, final Charset charset,
            final boolean flushOnWrite) throws IOException {
        initAbstractHtml();
        wsWarningDisabled = true;
        beforeToHtml(rootTag);
        wsWarningDisabled = false;
        final int totalWritten = rootTag.toOutputStream(os, rebuild, charset, flushOnWrite);
        wsWarningDisabled = true;
        afterToHtml(rootTag);
        wsWarningDisabled = false;
        return totalWritten;
    }

    /**
     * Use this method to produce HTML from very heavy and complicated tag
     * hierarchy. For normal and simple HTML hierarchy use {@code toHtmlString}
     * method which is faster than this method. The advantage of
     * {@code toBigHtmlString} over {@code toHtmlString} is it will never throw
     * {@code StackOverflowError}. <br>
     * NB:- this method has not been undergone all testing process.
     *
     * @param rebuild true to rebuild the tag hierarchy or false to return from
     *                cache if available.
     * @return the HTML string similar to toHtmlString method.
     * @since 3.0.15
     */
    public String toBigHtmlString(final boolean rebuild) {
        initAbstractHtml();
        wsWarningDisabled = true;
        beforeToHtml(rootTag);
        wsWarningDisabled = false;
        final String htmlString = rootTag.toBigHtmlString(rebuild);
        wsWarningDisabled = true;
        afterToHtml(rootTag);
        wsWarningDisabled = false;
        return htmlString;
    }

    /**
     * Use this method to produce HTML from very heavy and complicated tag
     * hierarchy. For normal and simple HTML hierarchy use {@code toHtmlString}
     * method which is faster than this method. The advantage of
     * {@code toBigHtmlString} over {@code toHtmlString} is it will never throw
     * {@code StackOverflowError}. <br>
     * NB:- this method has not been undergone all testing process.
     *
     * @return the HTML string similar to toHtmlString method.
     * @since 3.0.15
     */
    public String toBigHtmlString() {
        return toBigHtmlString(true);
    }

    /**
     * Use this method to produce HTML from very heavy and complicated tag
     * hierarchy. For normal and simple HTML hierarchy use {@code toOutputStream}
     * method which is faster than this method. The advantage of
     * {@code toBigOutputStream} over {@code toOutputStream} is it will never throw
     * {@code StackOverflowError} and the memory consumed at the time of writing
     * could be available for GC (depends on JVM GC rules). <br>
     * NB:- this method has not been undergone all testing process.
     *
     * @param os           the object of {@code OutputStream} to write to.
     * @param rebuild      true to rebuild &amp; false to write previously built
     *                     bytes.
     * @param charset      the charset
     * @param flushOnWrite true to flush on each write to OutputStream
     * @return the total number of bytes written
     * @throws IOException
     * @since 3.0.15
     */
    public int toBigOutputStream(final OutputStream os, final boolean rebuild, final Charset charset,
            final boolean flushOnWrite) throws IOException {
        initAbstractHtml();
        wsWarningDisabled = true;
        beforeToHtml(rootTag);
        wsWarningDisabled = false;
        final int totalWritten = rootTag.toBigOutputStream(os, rebuild, charset, flushOnWrite);
        wsWarningDisabled = true;
        afterToHtml(rootTag);
        wsWarningDisabled = false;
        return totalWritten;
    }

    /**
     * Use this method to produce HTML from very heavy and complicated tag
     * hierarchy. For normal and simple HTML hierarchy use {@code toOutputStream}
     * method which is faster than this method. The advantage of
     * {@code toBigOutputStream} over {@code toOutputStream} is it will never throw
     * {@code StackOverflowError} and the memory consumed at the time of writing
     * could be available for GC (depends on JVM GC rules). <br>
     * NB:- this method has not been undergone all testing process.
     *
     * @param os      the object of {@code OutputStream} to write to.
     * @param rebuild true to rebuild &amp; false to write previously built bytes.
     * @param charset the charset
     * @return the total number of bytes written
     * @throws IOException
     * @since 3.0.15
     */
    public int toBigOutputStream(final OutputStream os, final boolean rebuild, final Charset charset)
            throws IOException {
        return toBigOutputStream(os, rebuild, charset, false);
    }

    /**
     * Use this method to produce HTML from very heavy and complicated tag
     * hierarchy. For normal and simple HTML hierarchy use {@code toOutputStream}
     * method which is faster than this method. The advantage of
     * {@code toBigOutputStream} over {@code toOutputStream} is it will never throw
     * {@code StackOverflowError} and the memory consumed at the time of writing
     * could be available for GC (depends on JVM GC rules). <br>
     * NB:- this method has not been undergone all testing process.
     *
     * @param os      the object of {@code OutputStream} to write to.
     * @param charset the charset
     * @return the total number of bytes written
     * @throws IOException
     * @since 3.0.15
     */
    public int toBigOutputStream(final OutputStream os, final Charset charset) throws IOException {
        return toBigOutputStream(os, true, charset, false);
    }

    /**
     * Use this method to produce HTML from very heavy and complicated tag
     * hierarchy. For normal and simple HTML hierarchy use {@code toOutputStream}
     * method which is faster than this method. The advantage of
     * {@code toBigOutputStream} over {@code toOutputStream} is it will never throw
     * {@code StackOverflowError} and the memory consumed at the time of writing
     * could be available for GC (depends on JVM GC rules). <br>
     * NB:- this method has not been undergone all testing process.
     *
     * @param os           the object of {@code OutputStream} to write to.
     * @param charset      the charset
     * @param flushOnWrite true to flush on each write to OutputStream
     * @return the total number of bytes written
     * @throws IOException
     * @since 3.0.15
     */
    public int toBigOutputStream(final OutputStream os, final Charset charset, final boolean flushOnWrite)
            throws IOException {
        return toBigOutputStream(os, true, charset, flushOnWrite);
    }

    private void initAbstractHtml() {

        if (rootTag == null) {

            // NB: see javadoc on its declaration
            commonLock.lock();
            try {
                if (rootTag == null) {
                    if (renderInvoked) {
                        throw new InvalidUsageException(
                                "This method cannot be called here because this method is called by render or its child method.");
                    }
                    renderInvoked = true;
                    beforeRender();
                    rootTag = render();
                    if (rootTag == null) {
                        renderInvoked = false;
                        throw new NullValueException(
                                "render must return an instance of AbstractHtml, eg:- new Html(null);");
                    }

                    tagByWffId = rootTag.getSharedObject().initTagByWffId(ACCESS_OBJECT);
                    addInnerHtmlsForURLChange(rootTag);

                    addDataWffIdAttribute(rootTag);
                    // attribute value change listener
                    // should be added only after adding data-wff-id attribute
                    addAttrValueChangeListener(rootTag);
                    addChildTagAppendListener(rootTag);
                    addChildTagRemoveListener(rootTag);
                    addAttributeAddListener(rootTag);
                    addAttributeRemoveListener(rootTag);
                    addInnerHtmlAddListener(rootTag);
                    addInsertAfterListener(rootTag);
                    addInsertTagsBeforeListener(rootTag);
                    addReplaceListener(rootTag);
                    addWffBMDataUpdateListener(rootTag);
                    addWffBMDataDeleteListener(rootTag);
                    addPushQueue(rootTag);

                    if (rootTag.getSharedObject().isWhenURIUsed()) {
                        TagUtil.applyURIChangeAndAddDataWffIdAttribute(rootTag, rootTag.getSharedObject(), tagByWffId,
                                ACCESS_OBJECT);
                    }

                    wsWarningDisabled = true;
                    afterRender(rootTag);
                    wsWarningDisabled = false;
                } else {

                    unholdPushLock.acquireUninterruptibly();
                    try {
                        wffBMBytesQueue.clear();
                        pushQueueSize.reset();
                    } finally {
                        unholdPushLock.release();
                    }

                }
            } finally {
                commonLock.unlock();
            }

        } else {

            unholdPushLock.acquireUninterruptibly();
            try {
                wffBMBytesQueue.clear();
                pushQueueSize.reset();
            } finally {
                unholdPushLock.release();
            }
        }

        final String webSocketUrl = webSocketUrl();
        if (webSocketUrl == null) {
            throw new NullValueException("webSocketUrl must return valid WebSocket url");
        }

        final String wsUrlWithInstanceId = webSocketUrl.indexOf('?') == -1
                ? webSocketUrl + "?wffInstanceId=" + getInstanceId()
                : webSocketUrl + "&wffInstanceId=" + getInstanceId();

        embedWffScriptIfRequired(rootTag, wsUrlWithInstanceId);
    }

    /**
     * @return a unique id for this instance
     *
     * @since 2.0.0
     */
    public final String getInstanceId() {
        return instanceId;
    }

    /**
     * By default, it is set as true.
     *
     * @return the pushQueueEnabled
     * @since 2.0.2
     */
    public final boolean isPushQueueEnabled() {
        return pushQueueEnabled;
    }

    /**
     * If the server could not push any server updates it will be put in the queue
     * and when it tries to push in the next time it will first push updates from
     * this queue. By default, it is set as true.
     *
     * @param enabledPushQueue the enabledPushQueue to set
     * @since 2.0.2
     */
    public final void setPushQueueEnabled(final boolean enabledPushQueue) {
        pushQueueEnabled = enabledPushQueue;
    }

    /**
     * @param methodName
     * @param serverMethod
     *
     * @since 2.1.0
     */
    public final void addServerMethod(final String methodName, final ServerMethod serverMethod) {
        addServerMethod(methodName, serverMethod, null);
    }

    /**
     * @param methodName the method to invoke
     * @param recordData the recordData to be found in the event argument or null
     * @param uri        the uri or null
     * @return the object returned by
     *         {@link ServerMethod#invoke(ServerMethod.Event)} method.
     * @since 12.0.0-beta.6
     */
    public final WffBMObject invokeServerMethod(final String methodName, final Record recordData, final String uri) {
        final ServerMethodWrapper serverMethodWrapper = serverMethods.get(methodName);
        if (serverMethodWrapper != null) {
            final ServerMethod serverMethod = serverMethodWrapper.serverMethod();
            if (serverMethod != null) {
                return serverMethod.invoke(
                        new ServerMethod.Event(methodName, serverMethodWrapper.serverSideData(), uri, recordData));
            }
        }
        throw new MethodNotImplementedException(methodName + " is not added by browserPage.addServerMethod method");
    }

    /**
     * @param methodName
     * @param serverMethod
     * @param serverSideData this object will be available in the event of
     *                       serverMethod.invoke
     *
     * @since 3.0.2
     */
    public final void addServerMethod(final String methodName, final ServerMethod serverMethod,
            final Object serverSideData) {
        serverMethods.put(methodName, new ServerMethodWrapper(serverMethod, serverSideData));
    }

    /**
     * removes the method from
     *
     * @param methodName
     *
     * @since 2.1.0
     */
    public final void removeServerMethod(final String methodName) {
        serverMethods.remove(methodName);
    }

    /**
     * performs action provided by {@code BrowserPageAction}.
     *
     * @param actionByteBuffer The ByteBuffer object taken from
     *                         {@code BrowserPageAction} .Eg:-
     *                         {@code BrowserPageAction.RELOAD.getActionByteBuffer();}
     *
     * @since 2.1.0
     */
    public final void performBrowserPageAction(final ByteBuffer actionByteBuffer) {
        // actionByteBuffer is already prepended by payloadId placeholder
        push(new ClientTasksWrapper(actionByteBuffer));
        if (holdPush.get() == 0) {
            pushWffBMBytesQueue();
        }
    }

    /**
     * @return the pushQueueOnNewWebSocketListener true if it's enabled otherwise
     *         false. By default it's set as true.
     * @since 2.1.1
     */
    public final boolean isPushQueueOnNewWebSocketListener() {
        return pushQueueOnNewWebSocketListener;
    }

    /**
     * By default it's set as true. If it's enabled then the wffbmBytesQueue will be
     * pushed when new webSocket listener is added/set.
     *
     * @param pushQueueOnNewWebSocketListener the pushQueueOnNewWebSocketListener to
     *                                        set. Pass true to enable this option
     *                                        and false to disable this option.
     * @since 2.1.1
     */
    public final void setPushQueueOnNewWebSocketListener(final boolean pushQueueOnNewWebSocketListener) {
        this.pushQueueOnNewWebSocketListener = pushQueueOnNewWebSocketListener;
    }

    /**
     * @return the holdPush true if the push is on hold
     * @since 2.1.3
     */
    public final boolean isHoldPush() {
        return holdPush.get() > 0;
    }

    /**
     * holds push if not already on hold until unholdPush is called Usage :-
     *
     * <pre><code>
     *     browserPage.holdPush();
     * try {
     *     for (AbstractHtml tag : tags) {
     *         tag.removeAttributes("style");
     *     }
     *     // other tag manipulations
     * } finally {
     *     browserPage.unholdPush();
     * }
     * </code></pre>
     *
     *
     * @since 2.1.3
     */
    public final void holdPush() {
        holdPush.incrementAndGet();
    }

    /**
     * unholds push if not already unheld. Usage :-
     *
     * <pre><code>
     *     browserPage.holdPush();
     * try {
     *     for (AbstractHtml tag : tags) {
     *         tag.removeAttributes("style");
     *     }
     *     // other tag manipulations
     * } finally {
     *     browserPage.unholdPush();
     * }
     * </code></pre>
     *
     *
     * @since 2.1.3
     */
    public final void unholdPush() {
        if (holdPush.get() > 0) {
            final int count = holdPush.decrementAndGet();
            if (count < 0) {
                holdPush.incrementAndGet();
            } else {
                // count should be second checking
                if (copyCachedBMBytesToMainQ() || count == 0) {
                    pushWffBMBytesQueue();
                }
            }
        }
    }

    private boolean copyCachedBMBytesToMainQ() {

        boolean copied = false;

        if (!unholdPushLock.hasQueuedThreads()) {
            unholdPushLock.acquireUninterruptibly();

            try {

                ClientTasksWrapper clientTask = wffBMBytesHoldPushQueue.poll();

                if (clientTask != null) {

                    final NameValue invokeMultipleTasks = Task.getTaskOfTasksNameValue();

                    final Deque<ByteBuffer> wffBMs = new ArrayDeque<>(wffBMBytesHoldPushQueue.size() + 1);

                    AtomicReferenceArray<ByteBuffer> byteBuffers;
                    do {

                        byteBuffers = clientTask.tasks();
                        if (byteBuffers != null) {
                            final int length = byteBuffers.length();
                            for (int i = 0; i < length; i++) {
                                final ByteBuffer wffBM = byteBuffers.get(i);
                                if (wffBM != null) {
                                    wffBMs.add(wffBM);
                                }
                                byteBuffers.set(i, null);
                            }
                            clientTask.nullifyTasks();
                        }

                        clientTask = wffBMBytesHoldPushQueue.poll();
                    } while (clientTask != null);

                    final byte[][] values = new byte[wffBMs.size()][0];

                    int index = 0;
                    for (final ByteBuffer eachWffBM : wffBMs) {
                        values[index] = removePayloadIdPlaceholder(eachWffBM);
                        index++;
                    }

                    invokeMultipleTasks.setValues(values);

                    final ByteBuffer payload = buildPayload(
                            WffBinaryMessageUtil.VERSION_1.getWffBinaryMessageBytes(invokeMultipleTasks));

                    // no need to call outputBufferLimitLock.tryAcquire as its caller methods do
                    // this locking
                    wffBMBytesQueue.add(new ClientTasksWrapper(payload));

                    pushQueueSize.increment();

                    copied = true;
                }

            } finally {
                unholdPushLock.release();
            }

        }

        return copied;
    }

    /**
     * Gets the size of internal push queue. This size might not be accurate in
     * multi-threading environment.
     * <p>
     * Use case :- Suppose there is a thread in the server which makes real time ui
     * changes. But if the end user lost connection and the webSocket is not closed
     * connection, in such case the developer can decide whether to make any more ui
     * updates from server when the pushQueueSize exceeds a particular limit.
     *
     * @return the size of internal push queue.
     *
     * @since 2.1.4
     */
    public final int getPushQueueSize() {
        // wffBMBytesQueue.size() is not reliable as
        // it's ConcurrentLinkedQueue.
        // As per the javadoc ConcurrentLinkedQueue.size is NOT a constant-time
        // operation. So to avoid performance degrade of using
        // wffBMBytesQueue.size a separate LongAdder is kept to maintain the
        // queue size.
        return pushQueueSize.intValue();
    }

    /**
     * By default On.TAB_CLOSE and On.INIT_REMOVE_PREVIOUS are enabled.
     *
     * @param enable
     * @param ons    the instance of On to represent on which browser event the
     *               browser page needs to be removed.
     *
     * @since 2.1.4
     */
    public final void removeFromContext(final boolean enable, final On... ons) {
        for (final On on : ons) {
            if (On.TAB_CLOSE.equals(on)) {
                removeFromBrowserContextOnTabClose = enable;
            } else if (On.INIT_REMOVE_PREVIOUS.equals(on)) {
                removePrevFromBrowserContextOnTabInit = enable;
            }
        }
    }

    /**
     * @return the externalDrivePath
     * @since 3.0.18
     */
    String getExternalDrivePath() {
        return externalDrivePath;
    }

    /**
     * @since 12.0.0-beta.4
     * @param removed
     */
    void informRemovedFromContext(final boolean removed) {
        // NB: should not write compute intensive code when removed is false
        wsWarningDisabled = removed;
        if (removed) {
            removedFromContext();
        }
    }

    /**
     * Invokes when this browser page instance is removed from browser page context.
     * Override and use this method to stop long-running tasks / threads.
     *
     *
     * @since 2.1.4
     */
    protected void removedFromContext() {
        // NOP
        // To override and use this method
    }

    /**
     * To check if the given tag exists in the UI. <br>
     * NB:- This method is valid only if {@code browserPage#toHtmlString} or
     * {@code browserPage#toOutputStream} is called at least once in the lifetime.
     *
     * @param tag the tag object to be checked.
     * @return true if the given tag contains in the BrowserPage i.e. UI. false if
     *         the given tag was removed or was not already added in the UI.
     * @throws NullValueException   throws this exception if the given tag is null.
     * @throws NotRenderedException if the {@code BrowserPage} object is not
     *                              rendered. i.e. if
     *                              {@code browserPage#toHtmlString} or
     *                              {@code browserPage#toOutputStream} was NOT
     *                              called at least once in the lifetime.
     *
     * @since 2.1.7
     */
    public final boolean contains(final AbstractHtml tag) throws NullValueException, NotRenderedException {

        if (tag == null) {
            throw new NullValueException("tag object in browserPage.contains(AbstractHtml tag) method cannot be null");
        }

        if (rootTag == null) {
            throw new NotRenderedException(
                    "Could not check its existence. Make sure that you have called browserPage#toHtmlString method at least once in the lifetime.");
        }

        // this is the better way of checking, the rest of the code is old
        return rootTag.getSharedObject().equals(tag.getSharedObject());
    }

    /**
     * Sets the heartbeat ping interval of webSocket client in milliseconds. Give -1
     * to disable it. By default, it's set with -1. It affects only for the
     * corresponding {@code BrowserPage} instance from which it is called. <br>
     * NB:- This method has effect only if it is called before
     * {@code BrowserPage#render()} method return. This method can be called inside
     * {@code BrowserPage#render()} method to override the default global heartbeat
     * interval set by {@code BrowserPage#setWebSocketDefultHeartbeatInterval(int)}
     * method.
     *
     * @param milliseconds the heartbeat ping interval of webSocket client in
     *                     milliseconds. Give -1 to disable it.
     *
     * @since 2.1.8
     */
    protected final void setWebSocketHeartbeatInterval(final int milliseconds) {
        wsHeartbeatInterval = milliseconds;
    }

    /**
     * @return the interval value set by
     *         {@code BrowserPage#setWebSocketHeartbeatInterval(int)} method.
     *
     * @since 2.1.8
     */
    public final int getWebSocketHeartbeatInterval() {
        return wsHeartbeatInterval;
    }

    /**
     * Sets the default heartbeat ping interval of webSocket client in milliseconds.
     * Give -1 to disable it. It affects globally. By default, it's set with -1 till
     * wffweb-2.1.8 and Since wffweb-2.1.9 it's 25000ms i.e. 25 seconds.<br>
     * NB:- This method has effect only if it is called before
     * {@code BrowserPage#render()} invocation.
     *
     * @param milliseconds the heartbeat ping interval of webSocket client in
     *                     milliseconds. Give -1 to disable it
     *
     * @since 2.1.8
     * @since 2.1.9 the default value is 25000ms i.e. 25 seconds.
     */
    public static final void setWebSocketDefultHeartbeatInterval(final int milliseconds) {
        wsDefaultHeartbeatInterval = milliseconds;
    }

    /**
     * @return the interval value set by {@code setWebSocketDefultHeartbeatInterval}
     *         method.
     *
     * @since 2.1.8
     */
    public static final int getWebSocketDefultHeartbeatInterval() {
        return wsDefaultHeartbeatInterval;
    }

    /**
     * Sets the default reconnect interval of webSocket client in milliseconds. It
     * affects globally. By default, it's set with 2000 ms.<br>
     * NB:- This method has effect only if it is called before
     * {@code BrowserPage#render()} invocation.
     *
     * @param milliseconds the reconnect interval of webSocket client in
     *                     milliseconds. It must be greater than 0.
     *
     * @since 2.1.8
     */
    public static final void setWebSocketDefultReconnectInterval(final int milliseconds) {
        if (milliseconds < 1) {
            throw new InvalidValueException("The value must be greater than 0");
        }
        wsDefaultReconnectInterval = milliseconds;
    }

    /**
     * @return the interval value set by {@code setWebSocketDefultReconnectInterval}
     *         method.
     *
     * @since 2.1.8
     */
    public static final int getWebSocketDefultReconnectInterval() {
        return wsDefaultReconnectInterval;
    }

    /**
     * Sets the reconnect interval of webSocket client in milliseconds. Give -1 to
     * disable it. By default, it's set with -1. It affects only for the
     * corresponding {@code BrowserPage} instance from which it is called. <br>
     * NB:- This method has effect only if it is called before
     * {@code BrowserPage#render()} method return. This method can be called inside
     * {@code BrowserPage#render()} method to override the default global WebSocket
     * reconnect interval set by
     * {@code BrowserPage#setWebSocketDefultReconnectInterval(int)} method.
     *
     * @param milliseconds the reconnect interval of webSocket client in
     *                     milliseconds. Give -1 to disable it.
     *
     * @since 2.1.8
     */
    protected final void setWebSocketReconnectInterval(final int milliseconds) {
        wsReconnectInterval = milliseconds;
    }

    /**
     * @return the interval value set by
     *         {@code BrowserPage#setWebSocketReconnectInterval(int)} method.
     *
     * @since 2.1.8
     */
    public final int getWebSocketReconnectInterval() {
        return wsReconnectInterval;
    }

    /**
     * Gets the TagRepository to do different tag operations. This tag repository is
     * specific to this BrowserPage instance.
     *
     * @return the TagRepository object to do different tag operations. Or null if
     *         any one of the BrowserPage#toString or BrowserPage#toOutputStream
     *         methods is not called.
     *
     * @since 2.1.8
     */
    public final TagRepository getTagRepository() {

        if (tagRepository == null && rootTag != null) {
            // NB: see javadoc on its declaration
            commonLock.lock();
            try {
                if (tagRepository == null) {
                    tagRepository = new TagRepository(ACCESS_OBJECT, this, tagByWffId, rootTag);
                }
            } finally {
                commonLock.unlock();
            }
        }

        return tagRepository;
    }

    /**
     * Sets nonce attribute value for wff script.
     *
     * @param value pass value to set nonce value or pass null to remove nonce
     *              attribute
     * @since 3.0.1
     */
    protected final void setNonceForWffScript(final String value) {

        if (autoremoveWffScript) {
            throw new InvalidUsageException(
                    "Cannot remove while autoremoveWffScript is set as true. Please do setAutoremoveWffScript(false)");
        }

        if (value != null) {
            if (nonceForWffScriptTag == null) {
                nonceForWffScriptTag = new Nonce(value);
                if (wffScriptTagId != null) {
                    final AbstractHtml[] ownerTags = wffScriptTagId.getOwnerTags();
                    if (ownerTags.length > 0) {
                        final AbstractHtml wffScript = ownerTags[0];
                        wffScript.addAttributes(nonceForWffScriptTag);
                    }
                }
            } else {
                nonceForWffScriptTag.setValue(value);
            }
        } else {
            if (wffScriptTagId != null && nonceForWffScriptTag != null) {
                final AbstractHtml[] ownerTags = wffScriptTagId.getOwnerTags();
                if (ownerTags.length > 0) {
                    final AbstractHtml wffScript = ownerTags[0];
                    wffScript.removeAttributes(nonceForWffScriptTag);
                }
            }
            nonceForWffScriptTag = null;
        }

    }

    /**
     * @param algo eg: HashUtil.SHA_256
     * @return the base64 encoded string of the hash generated with the given algo
     *         for the wff script (text/javascript) content.
     * @since 3.0.1
     */
    private String getWffScriptHashInBase64(final String algo) {

        initAbstractHtml();

        if (wffScriptTagId != null) {
            final AbstractHtml[] ownerTags = wffScriptTagId.getOwnerTags();
            if (ownerTags.length > 0) {
                final AbstractHtml wffScript = ownerTags[0];
                final NoTag firstChild = (NoTag) wffScript.getFirstChild();
                final String childContent = firstChild.getChildContent();
                try {
                    return HashUtil.hashInBase64(childContent, algo);
                } catch (final NoSuchAlgorithmException e) {
                    if (LOGGER.isLoggable(Level.SEVERE)) {
                        LOGGER.log(Level.SEVERE, "Make sure that the jdk supports " + algo, e);
                    }
                }
            }
        }

        return null;
    }

    /**
     * Generates and gets the SHA hash of internal wff script as base64 encoded
     * string. NB: this method should not be called under
     * {@link BrowserPage#render()} method because this method internally calls
     * {@link BrowserPage#render()} method.
     *
     * @return the wff script (text/javascript) content converted to SHA-256 hash
     *         encoded in base64 string.
     * @since 3.0.1
     */
    public final String getWffScriptSHA256InBase64() {
        return getWffScriptHashInBase64(HashUtil.SHA_256);
    }

    /**
     * Generates and gets the SHA hash of internal wff script as base64 encoded
     * string. NB: this method should not be called under
     * {@link BrowserPage#render()} method because this method internally calls
     * {@link BrowserPage#render()} method.
     *
     * @return the wff script (text/javascript) content converted to SHA-384 hash
     *         encoded in base64 string.
     * @since 3.0.1
     */
    public final String getWffScriptSHA384InBase64() {
        return getWffScriptHashInBase64(HashUtil.SHA_384);
    }

    /**
     * Generates and gets the SHA hash of internal wff script as base64 encoded
     * string. NB: this method should not be called under
     * {@link BrowserPage#render()} method because this method internally calls
     * {@link BrowserPage#render()} method.
     *
     * @return the wff script (text/javascript) content converted to SHA-512 hash
     *         encoded in base64 string.
     * @since 3.0.1
     */
    public final String getWffScriptSHA512InBase64() {
        return getWffScriptHashInBase64(HashUtil.SHA_512);
    }

    /**
     * It must be called from render method to take effect. By default it's set as
     * true. So if Content-Security-Policy is implemented then script-src must allow
     * data:.
     *
     * @param enable
     * @since 3.0.1
     */
    protected final void setEnableDeferOnWffScript(final boolean enable) {
        enableDeferOnWffScript = enable;
    }

    /**
     * by default it's true.
     *
     * @return true if enabled otherwise false
     * @since 3.0.1
     */
    protected final boolean isEnableDeferOnWffScript() {
        return enableDeferOnWffScript;
    }

    /**
     * By default it is true.
     *
     * @return the current state
     * @since 3.0.1
     */
    protected final boolean isAutoremoveWffScript() {
        return autoremoveWffScript;
    }

    /**
     * Automatically removes the wff script tag after loading it from the ui. By
     * default it is true.
     *
     * @param autoremoveWffScript
     * @since 3.0.1
     */
    protected final void setAutoremoveWffScript(final boolean autoremoveWffScript) {
        this.autoremoveWffScript = autoremoveWffScript;
    }

    /**
     * Creates and returns new instance of {@code PayloadProcessor} for this browser
     * page. This PayloadProcessor can process incoming partial bytes from
     * WebSocket. To manually create new PayloadProcessor use <em>new
     * PayloadProcessor(browserPage)</em>. Use this PayloadProcessor instance only
     * under one thread at a time for its complete payload parts.
     *
     * @return new instance of PayloadProcessor each method call.
     * @since 3.0.11
     */
    public final PayloadProcessor newPayloadProcessor() {
        return new PayloadProcessor(this, true);
    }

    /**
     * gets the executor set by {@link BrowserPage#setExecutor}.
     *
     * @return return the executor object
     * @since 3.0.15
     */
    protected final Executor getExecutor() {
        return executor;
    }

    /**
     * Sets the executor to run {@link ServerMethod#invoke}, <br>
     *
     * <br>
     * NB: You may need only one copy of executor object for all browserPage
     * instances in the project. Eg: <br>
     *
     * <pre><code>
     *
     * public static final Executor EXECUTOR = Executors.newCachedThreadPool();
     * browserPage.setExecutor(EXECUTOR);
     * </code></pre>
     * <p>
     * When Java releases Virtual Thread we may be able to use as follows
     *
     * <pre><code>
     * public static final Executor EXECUTOR = Executors.newVirtualThreadExecutor();
     * browserPage.setExecutor(EXECUTOR);
     * </code></pre>
     *
     * @param executor
     * @since 3.0.15
     */
    protected final void setExecutor(final Executor executor) {
        this.executor = executor;
    }

    private void addInnerHtmlsForURLChange(final AbstractHtml rootTag) {
        rootTag.getSharedObject().setURIChangeTagSupplier(tag -> {
            if (tag != null) {
                tagsForURIChange.add(new TagWeakReference(tag));
            }
            return uriEvent;
        }, ACCESS_OBJECT);
    }

    /**
     *
     * @param updateClientURI
     * @param uriEvent
     * @since 12.0.0-beta.1
     * @since 12.0.0-beta.5 replace param added
     */
    private void changeInnerHtmlsOnTagsForURIChange(final boolean updateClientURI, final URIEvent uriEvent) {

        final boolean setURIAndAfterSetURI[] = { false, false };
        try {
            TagUtil.runAtomically(rootTag, () -> {
                this.uriEvent = uriEvent;

                if (updateClientURI) {
                    invokeSetURIAtClient(uriEvent.uriBefore(), uriEvent.uriAfter(), uriEvent.replace());
                    setURIAndAfterSetURI[0] = true;
                }

                final int size = tagsForURIChange.size();
                final List<AbstractHtml> tempCacheToPreventGC = new ArrayList<>(size);
                final List<Reference<AbstractHtml>> initialList = new ArrayList<>(size);
                for (final Reference<AbstractHtml> each : tagsForURIChange) {
                    final AbstractHtml tag = each.get();
                    if (tag != null) {
                        initialList.add(each);
                        tempCacheToPreventGC.add(tag);
                    }
                }

                TagUtil.sortByHierarchyOrder(initialList);

                // NB: should not directly iterate from tagsForUrlChange
                for (final Reference<AbstractHtml> tagRef : initialList) {
                    final AbstractHtml tag = tagRef.get();
                    if (tag != null) {
                        final boolean sharedObjectsEqual = TagUtil.changeInnerHtmlsForURIChange(tag, uriEvent,
                                rootTag.getSharedObject(), ACCESS_OBJECT);
                        if (!sharedObjectsEqual) {
                            tagsForURIChange.remove(tagRef);
                        }

                    }
                }

                if (updateClientURI) {
                    invokeAfterSetURIAtClient();
                    setURIAndAfterSetURI[1] = true;
                }

            }, true, ACCESS_OBJECT);

        } finally {
            if (updateClientURI) {
                if (!setURIAndAfterSetURI[0]) {
                    invokeSetURIAtClient(uriEvent.uriBefore(), uriEvent.uriAfter(), uriEvent.replace());
                }
                if (!setURIAndAfterSetURI[1]) {
                    invokeAfterSetURIAtClient();
                }
            }
            tagsForURIChange.removeIf(each -> each.get() == null);
        }
    }

    /**
     * @param updateClientURI
     * @param uri
     * @param initiator
     * @param replace
     * @since 12.0.0-beta.1
     * @since 12.0.0-beta.5 added replace param
     */
    private void setURI(final boolean updateClientURI, final String uri, final URIEventInitiator initiator,
            final boolean replace) {
        final URIEvent uriEvent = this.uriEvent;
        final String lastURI = uriEvent != null ? uriEvent.uriAfter() : null;
        if (lastURI == null || !lastURI.equals(uri)) {
            if (uri != null) {
                URIEvent event = new URIEvent(lastURI, uri, initiator, replace);
                final URIEventMask uriEventMask = beforeURIChange(event);
                event = uriEventMask != null && !Objects.equals(uriEventMask.uriBefore(), lastURI)
                        ? new URIEvent(uriEventMask.uriBefore(), uri, initiator, replace)
                        : event;
                if (rootTag != null) {
                    changeInnerHtmlsOnTagsForURIChange(updateClientURI, event);
                } else {
                    this.uriEvent = event;
                }
                afterURIChange(event);
            }
        }
    }

    /**
     * @param uri
     * @since 12.0.0-beta.1
     */
    public final void setURI(final String uri) {
        // NB: see javadoc on its declaration
        commonLock.lock();
        try {
            setURI(true, uri, URIEventInitiator.SERVER_CODE, false);
        } finally {
            commonLock.unlock();
        }
    }

    /**
     * @param uri
     * @param replace true to replace the given uri in the browser history
     * @since 12.0.0-beta.5
     */
    public final void setURI(final String uri, final boolean replace) {
        // NB: see javadoc on its declaration
        commonLock.lock();
        try {
            setURI(true, uri, URIEventInitiator.SERVER_CODE, replace);
        } finally {
            commonLock.unlock();
        }
    }

    /**
     * Override and use
     *
     * @param uriEvent
     * @since 12.0.0-beta.5
     */
    protected URIEventMask beforeURIChange(final URIEvent uriEvent) {
        return null;
    }

    /**
     * Override and use
     *
     * @param uriEvent
     * @since 12.0.0-beta.5
     */
    protected void afterURIChange(final URIEvent uriEvent) {
    }

    /**
     * @return the current uri. If the path is not passed to browserPage at the time
     *         of initial request then this method will return the uri only after
     *         initial client ping. However, inside
     *         {@link BrowserPage#onInitialClientPing(AbstractHtml)} this method
     *         will return current uri.
     * @since 12.0.0-beta.1
     */
    public final String getURI() {
        return uriEvent != null ? uriEvent.uriAfter() : null;
    }

    /**
     * NB: only for testing
     *
     * @return
     */
    final Set<Reference<AbstractHtml>> getTagsForURIChangeForTest() {
        return tagsForURIChange;
    }

    /**
     * @return the internal logger object
     * @since 12.0.0-beta.4
     */
    protected static Logger getBrowserPageLogger() {
        return LOGGER;
    }

    void setLocalStorageItem(final int id, final String key, final String value, final long writeTime,
            final boolean callback) {
        invokeSetLocalStorageItemAtClient(id, key, value, writeTime, callback);
    }

    void getLocalStorageItem(final int id, final String key) {
        invokeGetLocalStorageItemAtClient(id, key);
    }

    void removeLocalStorageItem(final int id, final String key, final long operationTimeMillis,
            final boolean callback) {
        invokeRemoveLocalStorageItemAtClient(id, key, operationTimeMillis, callback);
    }

    void removeAndGetLocalStorageItem(final int id, final String key, final long operationTimeMillis) {
        invokeRemoveAndGetLocalStorageItemAtClient(id, key, operationTimeMillis);
    }

    void clearLocalStorage(final int id, final long operationTimeMillis, final boolean callback) {
        invokeClearLocalStorageItemsAtClient(id, "DT", operationTimeMillis, callback);
    }

    void clearLocalStorageItems(final int id, final long operationTimeMillis, final boolean callback) {
        invokeClearLocalStorageItemsAtClient(id, "D", operationTimeMillis, callback);
    }

    void clearLocalStorageTokens(final int id, final long operationTimeMillis, final boolean callback) {
        invokeClearLocalStorageItemsAtClient(id, "T", operationTimeMillis, callback);
    }

    void setLocalStorageToken(final int id, final String key, final String value, final long operationTimeMillis) {
        invokeSetLocalStorageTokenAtClient(id, key, value, operationTimeMillis);
    }

    void removeLocalStorageToken(final int id, final String key, final long operationTimeMillis) {
        invokeRemoveLocalStorageTokenAtClient(id, key, operationTimeMillis);
    }

    /**
     * It will provide you a unique temporary directory path for this BrowserPage
     * instance. This temp directory and all of its sub-directories/files will be
     * deleted after this BrowserPage instance is garbage collected. You should
     * override {@link #useExternalDrivePath()} and return a valid path in it. The
     * returned path should have read &amp; write permission.
     *
     * @return the temporary directory if useExternalDrivePath is set
     * @since 12.0.0-beta.6
     */
    public Path getTempDirectory() {
        if (tempDirPath == null && externalDrivePath != null) {
            final Path dirPath = Paths.get(externalDrivePath, instanceId, "temp");
            if (Files.notExists(dirPath)) {
                try {
                    Files.createDirectories(dirPath);
                    tempDirPath = dirPath;
                    return dirPath;
                } catch (final IOException e) {
                    LOGGER.severe(
                            "The given path by useExternalDrivePath is invalid or it doesn't have read/write permission.");
                }
            }
        }

        return tempDirPath;
    }

    protected final Settings defaultSettings() {
        return defaultSettings;
    }

    private Settings useSettingsPvt() {
        try {
            final Settings settings = useSettings();
            if (settings != null) {
                return settings;
            }
        } catch (final InvalidValueException e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        } catch (final RuntimeException e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE,
                        "Exception while invoking the overridden useSettings method so default Settings will be applied",
                        e);
            }
        }
        return defaultSettings;
    }

    /**
     * @return the settings for this browserPage instance
     * @since 12.0.0-beta.8
     */
    protected Settings useSettings() {
        return defaultSettings;
    }

    /**
     * @return the Settings applied to this browserPage instance
     * @since 12.0.0-beta.8
     */
    protected final Settings getSettings() {
        return settings;
    }

    private ByteBuffer buildPayload(final byte[] bmMsg) {
        if (onPayloadLoss != null) {
            // 4 for id bytes
            final ByteBuffer byteBuffer = ByteBuffer
                    .allocate(bmMsg.length + PLACEHOLDER_BYTE_ARRAY_FOR_PAYLOAD_ID.length);
            // bmMsg never starts with 0, if it starts with 0 it means it is already
            // prepended by id placeholder
            if (bmMsg[0] != 0) {
                // placeholder for id bytes
                byteBuffer.put(PLACEHOLDER_BYTE_ARRAY_FOR_PAYLOAD_ID);
            }
            byteBuffer.put(bmMsg);
            byteBuffer.rewind();
            return byteBuffer;
        }

        return ByteBuffer.wrap(bmMsg);
    }

    private byte[] removePayloadIdPlaceholder(final ByteBuffer bmMsg) {
        final byte[] array = bmMsg.array();
        if (onPayloadLoss != null) {
            final byte[] bmMsgWithoutId = new byte[array.length - PLACEHOLDER_BYTE_ARRAY_FOR_PAYLOAD_ID.length];
            System.arraycopy(array, PLACEHOLDER_BYTE_ARRAY_FOR_PAYLOAD_ID.length, bmMsgWithoutId, 0,
                    bmMsgWithoutId.length);
            bmMsg.position(PLACEHOLDER_BYTE_ARRAY_FOR_PAYLOAD_ID.length);
            return bmMsgWithoutId;
        }
        return array;
    }

    private ByteBuffer buildPayloadForClient(final ByteBuffer bmMsgWithIdPlaceholder) {
        if (onPayloadLoss != null) {
            // id bytes should always be length 4
            bmMsgWithIdPlaceholder.put(WffBinaryMessageUtil.getBytesFromInt(getServerSidePayloadId()));
            bmMsgWithIdPlaceholder.rewind();
        }

        return bmMsgWithIdPlaceholder;
    }

    private int getServerSidePayloadId() {
        int id = serverSidePayloadIdGenerator.incrementAndGet();
        if (id == 0) {
            id = serverSidePayloadIdGenerator.incrementAndGet();
        }
        return id;
    }

    private void rollbackServerSidePayloadId() {
        final int id = serverSidePayloadIdGenerator.decrementAndGet();
        if (id == 0) {
            serverSidePayloadIdGenerator.decrementAndGet();
        }
    }

    private int getClientSidePayloadId() {
        int id = clientSidePayloadIdGenerator.incrementAndGet();
        if (id == 0) {
            id = clientSidePayloadIdGenerator.incrementAndGet();
        }
        return id;
    }

}
