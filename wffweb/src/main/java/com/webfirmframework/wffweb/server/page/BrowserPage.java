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
package com.webfirmframework.wffweb.server.page;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.atomic.LongAdder;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.webfirmframework.wffweb.InvalidUsageException;
import com.webfirmframework.wffweb.InvalidValueException;
import com.webfirmframework.wffweb.NotRenderedException;
import com.webfirmframework.wffweb.NullValueException;
import com.webfirmframework.wffweb.PushFailedException;
import com.webfirmframework.wffweb.WffRuntimeException;
import com.webfirmframework.wffweb.js.JsUtil;
import com.webfirmframework.wffweb.server.page.js.WffJsFile;
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
import com.webfirmframework.wffweb.tag.html.attribute.event.ServerAsyncMethod;
import com.webfirmframework.wffweb.tag.html.attribute.listener.AttributeValueChangeListener;
import com.webfirmframework.wffweb.tag.html.html5.attribute.global.DataWffId;
import com.webfirmframework.wffweb.tag.html.programming.Script;
import com.webfirmframework.wffweb.tag.htmlwff.NoTag;
import com.webfirmframework.wffweb.tag.repository.TagRepository;
import com.webfirmframework.wffweb.util.HashUtil;
import com.webfirmframework.wffweb.util.WffBinaryMessageUtil;
import com.webfirmframework.wffweb.util.data.NameValue;
import com.webfirmframework.wffweb.wffbm.data.WffBMObject;

/**
 * @author WFF
 * @since 2.0.0
 */
public abstract class BrowserPage implements Serializable {

    // if this class' is refactored then SecurityClassConstants should be
    // updated.

    private static final long serialVersionUID = 1_0_1L;

    private static final Logger LOGGER = Logger.getLogger(BrowserPage.class.getName());

    public static final String WFF_INSTANCE_ID = "wffInstanceId";

    private static final boolean PRODUCTION_MODE = true;

    private final String instanceId = UUID.randomUUID().toString();

    private AttributeValueChangeListener valueChangeListener;

    private Map<String, AbstractHtml> tagByWffId;

    private volatile AbstractHtml rootTag;

    private volatile boolean wsWarningDisabled;

    private final Map<String, WebSocketPushListener> sessionIdWsListeners = new ConcurrentHashMap<>();

    private final Deque<WebSocketPushListener> wsListeners = new ConcurrentLinkedDeque<>();

    private volatile WebSocketPushListener wsListener;

    private DataWffId wffScriptTagId;

    /**
     * it's true by default since 3.0.1
     */
    private boolean enableDeferOnWffScript = true;

    private Nonce nonceForWffScriptTag;

    private boolean autoremoveWffScript = true;

    private volatile boolean renderInvoked;

    // ConcurrentLinkedQueue give better performance than ConcurrentLinkedDeque
    // on benchmark
    private final Deque<ClientTasksWrapper> wffBMBytesQueue = new ConcurrentLinkedDeque<>();

    // ConcurrentLinkedQueue give better performance than ConcurrentLinkedDeque
    // on benchmark
    private final Queue<ClientTasksWrapper> wffBMBytesHoldPushQueue = new ConcurrentLinkedQueue<>();

    // there will be only one thread waiting for the lock so fairness must be
    // false and fairness may decrease the lock time
    // only one thread is allowed to gain lock so permit should be 1
    private final transient Semaphore taskFromClientQLock = new Semaphore(1, false);

    // ConcurrentLinkedQueue give better performance than ConcurrentLinkedDeque
    // on benchmark
    private final Queue<byte[]> taskFromClientQ = new ConcurrentLinkedQueue<>();

    private static final Security ACCESS_OBJECT = new Security();

    // by default the push queue should be enabled
    private boolean pushQueueEnabled = true;

    // by default the pushQueueOnNewWebSocketListener should be enabled
    private boolean pushQueueOnNewWebSocketListener = true;

    private final AtomicInteger holdPush = new AtomicInteger(0);

    private final Map<String, ServerMethod> serverMethods = new ConcurrentHashMap<>();

    private boolean removeFromBrowserContextOnTabClose = true;

    private boolean removePrevFromBrowserContextOnTabInit = true;

    private int wsHeartbeatInterval = -1;

    private int wsReconnectInterval = -1;

    private static int wsDefaultHeartbeatInterval = 25_000;

    private static int wsDefaultReconnectInterval = 2_000;

    private final LongAdder pushQueueSize = new LongAdder();

    // there will be only one thread waiting for the lock so fairness must be
    // false and fairness may decrease the lock time
    // only one thread is allowed to gain lock so permit should be 1
    private final transient Semaphore pushWffBMBytesQueueLock = new Semaphore(1, false);

    // there will be only one thread waiting for the lock so fairness must be
    // false and fairness may decrease the lock time
    // only one thread is allowed to gain lock so permit should be 1
    private final transient Semaphore unholdPushLock = new Semaphore(1, false);

    private final AtomicReference<Thread> waitingThreadRef = new AtomicReference<>();

    private volatile TagRepository tagRepository;

    private Executor executor;

    // to make it GC friendly, it is made as static
    private static final ThreadLocal<PayloadProcessor> PALYLOAD_PROCESSOR_TL = new ThreadLocal<>();

    // NB: this non-static initialization makes BrowserPage and PayloadProcessor
    // never to get GCd. It leads to memory leak. It seems to be a bug.
    // private final ThreadLocal<PayloadProcessor> PALYLOAD_PROCESSOR_TL =
    // ThreadLocal
    // .withInitial(() -> new PayloadProcessor(this, true));

    // for security purpose, the class name should not be modified
    private static final class Security implements Serializable {

        private static final long serialVersionUID = 1L;

        private Security() {
        }
    }

    /**
     * To specify (by removeFromContext method) when to remove {@code BrowserPage}
     * instance from {@code BrowserPageContext}.
     *
     * @author WFF
     * @since 2.1.4
     */
    public static enum On {

        /**
         * to remove the current {@code BrowserPage} instance from
         * {@code BrowserPageContext} when the tab/window is closed.
         */
        TAB_CLOSE,

        /**
         * To remove the previous {@code BrowserPage} instance opened in the same tab
         * when new {@code BrowserPage} is requested by the tab.
         */
        INIT_REMOVE_PREVIOUS;
    }

    public abstract String webSocketUrl();

    /**
     * @param wsListener
     * @since 2.0.0
     * @author WFF
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
     * @since 2.1.0
     * @author WFF
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
     * @since 2.1.0
     * @author WFF
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

    public final WebSocketPushListener getWsListener() {
        return wsListener;
    }

    final void push(final NameValue... nameValues) {
        push(new ClientTasksWrapper(
                ByteBuffer.wrap(WffBinaryMessageUtil.VERSION_1.getWffBinaryMessageBytes(nameValues))));
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

            final ByteBuffer byteBuffer = ByteBuffer
                    .wrap(WffBinaryMessageUtil.VERSION_1.getWffBinaryMessageBytes(nameValues));
            tasks[index] = byteBuffer;
            index++;
        }

        final ClientTasksWrapper clientTasks = new ClientTasksWrapper(tasks);
        push(clientTasks);
        return clientTasks;
    }

    private void push(final ClientTasksWrapper clientTasks) {

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

    private void pushWffBMBytesQueue() {

        if (wsListener != null) {

            // hasQueuedThreads internally uses transient volatile Node
            // so it must be fine for production use but
            // TODO verify it in deep if it is good for production
            if (!pushWffBMBytesQueueLock.hasQueuedThreads() && !wffBMBytesQueue.isEmpty()) {

                Thread taskThread = null;
                waitingThreadRef.getAndSet(Thread.currentThread());
                try {
                    pushWffBMBytesQueueLock.acquire();
                } catch (final InterruptedException e) {
                    waitingThreadRef.compareAndSet(taskThread, null);
                    if (LOGGER.isLoggable(Level.SEVERE)) {
                        LOGGER.log(Level.SEVERE, "Thread InterruptedException", e);
                    }
                    return;
                }

                try {
                    taskThread = Thread.currentThread();

                    // wsPushInProgress must be implemented here and it is very
                    // important because multiple threads should not push
                    // simultaneously
                    // from same wffBMBytesQueue which will cause incorrect
                    // order of
                    // push

                    ClientTasksWrapper clientTask = wffBMBytesQueue.poll();
                    if (clientTask != null) {
                        AtomicReferenceArray<ByteBuffer> byteBuffers;

                        do {
                            pushQueueSize.decrement();
                            try {

                                byteBuffers = clientTask.tasks();
                                if (byteBuffers != null) {
                                    final int length = byteBuffers.length();
                                    for (int i = 0; i < length; i++) {
                                        final ByteBuffer byteBuffer = byteBuffers.get(i);
                                        if (byteBuffer != null) {
                                            wsListener.push(byteBuffer);
                                        }
                                        byteBuffers.set(i, null);
                                    }
                                    clientTask.nullifyTasks();
                                }

                            } catch (final PushFailedException e) {
                                if (pushQueueEnabled && wffBMBytesQueue.offerFirst(clientTask)) {
                                    pushQueueSize.increment();
                                }

                                break;
                            } catch (final IllegalStateException | NullPointerException e) {
                                if (wffBMBytesQueue.offerFirst(clientTask)) {
                                    pushQueueSize.increment();
                                }
                                break;
                            }

                            if (pushWffBMBytesQueueLock.hasQueuedThreads()
                                    && waitingThreadRef.get().getPriority() >= taskThread.getPriority()) {
                                break;
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
     * This method will be remove later. Use {@code webSocketMessaged}.
     *
     * @param message the bytes the received in onmessage
     * @since 2.0.0
     * @author WFF
     * @deprecated alternative method webSocketMessaged is available for the same
     *             job.
     *
     */
    @Deprecated
    public final void websocketMessaged(final byte[] message) {
        webSocketMessaged(message);
    }

    /**
     * @param message the bytes the received in onmessage
     * @since 2.1.0
     * @author WFF
     */
    public final void webSocketMessaged(final byte[] message) {
        // minimum number of an empty bm message length is 4
        // below that length is not a valid bm message so check
        // message.length < 4
        // later if there is such requirement
        if (message.length < 4) {
            // message.length == 0 when client sends an empty message just
            // for ping
            return;
        }

        // executeWffBMTask(message);

        taskFromClientQ.offer(message);

        if (!taskFromClientQ.isEmpty()) {
            final Executor executor = this.executor;
            if (executor != null) {
                executor.execute(this::executeTasksFromClientFromQ);
            } else {
                CompletableFuture.runAsync(this::executeTasksFromClientFromQ);
            }
        }

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
     * object in all of its life time.
     *
     * @since 3.0.1
     */
    protected void beforeRender() {
        // NOP override and use
    }

    /**
     * Override and use this method to render html content to the client browser
     * page. This method invokes only once per object in all of its life time.
     *
     * @return the object of {@link Html} class which needs to be displayed in the
     *         client browser page.
     * @author WFF
     */
    public abstract AbstractHtml render();

    /**
     * Invokes after {@link BrowserPage#render()} method. This is an empty method in
     * BrowserPage. Override and use. This method invokes only once per object in
     * all of its life time.
     *
     * @param rootTag the rootTag returned by {@link BrowserPage#render()} method.
     *
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
     *
     * @param rootTag the rootTag returned by {@link BrowserPage#render()} method.
     *
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
     *
     * @param rootTag the rootTag returned by {@link BrowserPage#render()} method.
     *
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

        final AbstractHtml methodTag = tagByWffId.get(wffTagId);
        if (methodTag != null) {

            final AbstractAttribute attributeByName = methodTag.getAttributeByName(eventAttrName);

            if (attributeByName != null) {

                if (attributeByName instanceof EventAttribute) {

                    final EventAttribute eventAttr = (EventAttribute) attributeByName;

                    final ServerAsyncMethod serverAsyncMethod = eventAttr.getServerAsyncMethod();

                    final ServerAsyncMethod.Event event = new ServerAsyncMethod.Event(methodTag, attributeByName,
                            eventAttr.getServerSideData());

                    final WffBMObject returnedObject;

                    try {
                        synchronized (this) {
                            // to read up to date value from
                            // main memory and to flush
                            // modification to main memory
                            // synchronized will do it as
                            // per
                            // java memory
                            // model
                            returnedObject = serverAsyncMethod.asyncMethod(wffBMObject, event);
                        }

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
                        nameValue.setName(JsUtil.toDynamicJs(jsPostFunctionBody).getBytes(StandardCharsets.UTF_8));

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

        final ServerMethod serverMethod = serverMethods.get(methodName);

        if (serverMethod != null) {

            final byte[][] values = methodNameAndArg.getValues();

            final WffBMObject wffBMObject = values.length > 0 ? new WffBMObject(values[0], true) : null;

            final WffBMObject returnedObject;
            try {

                synchronized (this) {
                    // to read up to date value from
                    // main memory and to flush
                    // modification to main memory
                    // synchronized will do it as
                    // per
                    // java memory
                    // model
                    returnedObject = serverMethod.getServerAsyncMethod().asyncMethod(wffBMObject,
                            new ServerAsyncMethod.Event(methodName, serverMethod.getServerSideData()));
                }

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
            LOGGER.warning(methodName + " doesn't exist, please add it as browserPage.addServerMethod(\"" + methodName
                    + "\", serverAsyncMethod)");
        }

    }

    /**
     * executes the task in the given wff binary message. <br>
     * For WFF authors :- Make sure that the passing {@code message} is not empty
     * while consuming this method, just as made conditional checking in
     * {@code BrowserPage#webSocketMessaged(byte[])} method.
     *
     * @since 2.0.0
     * @author WFF
     * @throws UnsupportedEncodingException throwing this exception will be removed
     *                                      in future version because its internal
     *                                      implementation will never make this
     *                                      exception due to the code changes since
     *                                      3.0.1.
     */
    private void executeWffBMTask(final byte[] message) throws UnsupportedEncodingException {

        final List<NameValue> nameValues = WffBinaryMessageUtil.VERSION_1.parse(message);

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

            }

        }

    }

    private void addAttrValueChangeListener(final AbstractHtml abstractHtml) {

        if (valueChangeListener == null) {
            valueChangeListener = new AttributeValueChangeListenerImpl(this, tagByWffId);
        }

        abstractHtml.getSharedObject().setValueChangeListener(valueChangeListener, ACCESS_OBJECT);
    }

    private void addDataWffIdAttribute(final AbstractHtml abstractHtml) {

        final Deque<Set<AbstractHtml>> childrenStack = new ArrayDeque<>();
        // passed 2 instead of 1 because the load factor is 0.75f
        final Set<AbstractHtml> initialSet = new LinkedHashSet<>(2);
        initialSet.add(abstractHtml);
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

                if (subChildren != null && subChildren.size() > 0) {
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
        // passed 2 instead of 1 because the load factor is 0.75f
        final Set<AbstractHtml> initialSet = new LinkedHashSet<>(2);
        initialSet.add(abstractHtml);
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

                    final String wffJs = WffJsFile.getAllOptimizedContent(wsUrlWithInstanceId, getInstanceId(),
                            removePrevFromBrowserContextOnTabInit, removeFromBrowserContextOnTabClose,
                            (wsHeartbeatInterval > 0 ? wsHeartbeatInterval : wsDefaultHeartbeatInterval),
                            (wsReconnectInterval > 0 ? wsReconnectInterval : wsDefaultReconnectInterval),
                            autoremoveWffScript);

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

                if (subChildren != null && subChildren.size() > 0) {
                    childrenStack.push(subChildren);
                }
            }

        }

        if (headAndbodyTagMissing) {
            wffScriptTagId = getNewDataWffId();

            final Script script = new Script(null, new Type(Type.TEXT_JAVASCRIPT));

            script.setDataWffId(wffScriptTagId);

            final String wffJs = WffJsFile.getAllOptimizedContent(wsUrlWithInstanceId, getInstanceId(),
                    removePrevFromBrowserContextOnTabInit, removeFromBrowserContextOnTabClose,
                    (wsHeartbeatInterval > 0 ? wsHeartbeatInterval : wsDefaultHeartbeatInterval),
                    (wsReconnectInterval > 0 ? wsReconnectInterval : wsDefaultReconnectInterval), autoremoveWffScript);

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
     * @author WFF
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
     * @since 2.1.4
     * @author WFF
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
     * @param charset the charset
     * @return {@code String} equalent to the html string of the tag including the
     *         child tags.
     * @author WFF
     */
    public String toHtmlString(final String charset) {
        initAbstractHtml();
        wsWarningDisabled = true;
        beforeToHtml(rootTag);
        wsWarningDisabled = false;
        final String htmlString = rootTag.toHtmlString(true, charset);
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
     * @param charset the charset to set for the returning value, eg:
     *                {@code StandardCharsets.UTF_8.name()}
     * @return {@code String} equalent to the html string of the tag including the
     *         child tags.
     *
     * @since 2.1.4
     * @author WFF
     */
    public String toHtmlString(final boolean rebuild, final String charset) {
        initAbstractHtml();
        wsWarningDisabled = true;
        beforeToHtml(rootTag);
        wsWarningDisabled = false;
        final String htmlString = rootTag.toHtmlString(rebuild, charset);
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
     * @since 2.1.4 void toOutputStream
     * @since 2.1.8 int toOutputStream
     * @throws IOException
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
     *
     * @throws IOException
     * @since 2.1.4 void toOutputStream
     * @since 2.1.8 int toOutputStream
     *
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
     *
     * @throws IOException
     * @since 3.0.2
     *
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
     *
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
     *
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
     *
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
     *
     * @param charset the charset
     *
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

            synchronized (this) {
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
     * @since 2.0.0
     * @author WFF
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
     * @param serverAsyncMethod
     * @since 2.1.0
     * @author WFF
     */
    public final void addServerMethod(final String methodName, final ServerAsyncMethod serverAsyncMethod) {
        serverMethods.put(methodName, new ServerMethod(serverAsyncMethod, null));
    }

    /**
     * @param methodName
     * @param serverAsyncMethod
     * @param serverSideData    this object will be available in the event of
     *                          serverAsyncMethod.asyncMethod
     * @since 3.0.2
     * @author WFF
     */
    public final void addServerMethod(final String methodName, final ServerAsyncMethod serverAsyncMethod,
            final Object serverSideData) {
        serverMethods.put(methodName, new ServerMethod(serverAsyncMethod, serverSideData));
    }

    /**
     * removes the method from
     *
     * @param methodName
     * @since 2.1.0
     * @author WFF
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
     * @since 2.1.0
     * @author WFF
     */
    public final void performBrowserPageAction(final ByteBuffer actionByteBuffer) {
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
     *
     * @return the holdPush true if the push is on hold
     * @since 2.1.3
     */
    public final boolean isHoldPush() {
        return holdPush.get() > 0;
    }

    /**
     * holds push if not already on hold until unholdPush is called Usage :-
     *
     * <pre>
     * try {
     *     browserPage.holdPush();
     *
     *     for (AbstractHtml tag : tags) {
     *         tag.removeAttributes("style");
     *     }
     *     // other tag manipulations
     * } finally {
     *     browserPage.unholdPush();
     * }
     * </pre>
     *
     * @since 2.1.3
     * @author WFF
     */
    public final void holdPush() {
        holdPush.incrementAndGet();
    }

    /**
     * unholds push if not already unheld. Usage :-
     *
     * <pre>
     * try {
     *     browserPage.holdPush();
     *
     *     for (AbstractHtml tag : tags) {
     *         tag.removeAttributes("style");
     *     }
     *     // other tag manipulations
     * } finally {
     *     browserPage.unholdPush();
     * }
     * </pre>
     *
     * @since 2.1.3
     * @author WFF
     */
    public final void unholdPush() {
        if (holdPush.get() > 0) {
            holdPush.decrementAndGet();
            if (copyCachedBMBytesToMainQ()) {
                pushWffBMBytesQueue();
            }
        }
    }

    private boolean copyCachedBMBytesToMainQ() {

        boolean copied = false;

        if (!unholdPushLock.hasQueuedThreads()) {
            try {
                unholdPushLock.acquire();
            } catch (final InterruptedException e) {
                // NOP
                if (LOGGER.isLoggable(Level.SEVERE)) {
                    LOGGER.log(Level.SEVERE, "Thread InterruptedException", e);
                }
                return false;
            }

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
                        values[index] = eachWffBM.array();
                        index++;
                    }

                    invokeMultipleTasks.setValues(values);

                    wffBMBytesQueue.add(new ClientTasksWrapper(ByteBuffer
                            .wrap(WffBinaryMessageUtil.VERSION_1.getWffBinaryMessageBytes(invokeMultipleTasks))));

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
     *
     * Use case :- Suppose there is a thread in the server which makes real time ui
     * changes. But if the end user lost connection and the webSocket is not closed
     * connection, in such case the developer can decide whether to make any more ui
     * updates from server when the pushQueueSize exceeds a particular limit.
     *
     * @return the size of internal push queue.
     * @since 2.1.4
     * @author WFF
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
     * @since 2.1.4
     * @author WFF
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
     * Invokes when this browser page instance is removed from browser page context.
     * Override and use this method to stop long running tasks / threads.
     *
     * @since 2.1.4
     * @author WFF
     */
    protected void removedFromContext() {
        // NOP
        // To override and use this method
    }

    /**
     * To check if the given tag exists in the UI. <br>
     * NB:- This method is valid only if {@code browserPage#toHtmlString} or
     * {@code browserPage#toOutputStream} is called at least once in the life time.
     *
     * @param tag the tag object to be checked.
     * @return true if the given tag contains in the BrowserPage i.e. UI. false if
     *         the given tag was removed or was not already added in the UI.
     * @throws NullValueException   throws this exception if the given tag is null.
     * @throws NotRenderedException if the {@code BrowserPage} object is not
     *                              rendered. i.e. if
     *                              {@code browserPage#toHtmlString} or
     *                              {@code browserPage#toOutputStream} was NOT
     *                              called at least once in the life time.
     * @since 2.1.7
     * @author WFF
     */
    public final boolean contains(final AbstractHtml tag) throws NullValueException, NotRenderedException {

        if (tag == null) {
            throw new NullValueException("tag object in browserPage.contains(AbstractHtml tag) method cannot be null");
        }

        if (tagByWffId == null) {
            throw new NotRenderedException(
                    "Could not check its existance. Make sure that you have called browserPage#toHtmlString method atleast once in the life time.");
        }

        final DataWffId dataWffId = tag.getDataWffId();
        if (dataWffId == null) {
            return false;
        }
        return tag.equals(tagByWffId.get(dataWffId.getValue()));
    }

    /**
     * Sets the heartbeat ping interval of webSocket client in milliseconds. Give -1
     * to disable it. By default it's set with -1. It affects only for the
     * corresponding {@code BrowserPage} instance from which it is called. <br>
     * NB:- This method has effect only if it is called before
     * {@code BrowserPage#render()} method return. This method can be called inside
     * {@code BrowserPage#render()} method to override the default global heartbeat
     * interval set by {@code BrowserPage#setWebSocketDefultHeartbeatInterval(int)}
     * method.
     *
     * @param milliseconds the heartbeat ping interval of webSocket client in
     *                     milliseconds. Give -1 to disable it.
     * @since 2.1.8
     * @author WFF
     */
    protected final void setWebSocketHeartbeatInterval(final int milliseconds) {
        wsHeartbeatInterval = milliseconds;
    }

    /**
     * @return the interval value set by
     *         {@code BrowserPage#setWebSocketHeartbeatInterval(int)} method.
     * @since 2.1.8
     * @author WFF
     */
    public final int getWebSocketHeartbeatInterval() {
        return wsHeartbeatInterval;
    }

    /**
     * Sets the default heartbeat ping interval of webSocket client in milliseconds.
     * Give -1 to disable it. It affects globally. By default it's set with -1 till
     * wffweb-2.1.8 and Since wffweb-2.1.9 it's 25000ms i.e. 25 seconds.<br>
     * NB:- This method has effect only if it is called before
     * {@code BrowserPage#render()} invocation.
     *
     *
     * @param milliseconds the heartbeat ping interval of webSocket client in
     *                     milliseconds. Give -1 to disable it
     * @since 2.1.8
     * @since 2.1.9 the default value is 25000ms i.e. 25 seconds.
     * @author WFF
     */
    public static final void setWebSocketDefultHeartbeatInterval(final int milliseconds) {
        wsDefaultHeartbeatInterval = milliseconds;
    }

    /**
     * @return the interval value set by {@code setWebSocketDefultHeartbeatInterval}
     *         method.
     * @since 2.1.8
     * @author WFF
     */
    public static final int getWebSocketDefultHeartbeatInterval() {
        return wsDefaultHeartbeatInterval;
    }

    /**
     * Sets the default reconnect interval of webSocket client in milliseconds. It
     * affects globally. By default it's set with 2000 ms.<br>
     * NB:- This method has effect only if it is called before
     * {@code BrowserPage#render()} invocation.
     *
     *
     * @param milliseconds the reconnect interval of webSocket client in
     *                     milliseconds. It must be greater than 0.
     * @since 2.1.8
     * @author WFF
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
     * @since 2.1.8
     * @author WFF
     */
    public static final int getWebSocketDefultReconnectInterval() {
        return wsDefaultReconnectInterval;
    }

    /**
     * Sets the reconnect interval of webSocket client in milliseconds. Give -1 to
     * disable it. By default it's set with -1. It affects only for the
     * corresponding {@code BrowserPage} instance from which it is called. <br>
     * NB:- This method has effect only if it is called before
     * {@code BrowserPage#render()} method return. This method can be called inside
     * {@code BrowserPage#render()} method to override the default global WebSocket
     * reconnect interval set by
     * {@code BrowserPage#setWebSocketDefultReconnectInterval(int)} method.
     *
     * @param milliseconds the reconnect interval of webSocket client in
     *                     milliseconds. Give -1 to disable it.
     * @since 2.1.8
     * @author WFF
     */
    protected final void setWebSocketReconnectInterval(final int milliseconds) {
        wsReconnectInterval = milliseconds;
    }

    /**
     * @return the interval value set by
     *         {@code BrowserPage#setWebSocketReconnectInterval(int)} method.
     * @since 2.1.8
     * @author WFF
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
     * @since 2.1.8
     * @author WFF
     */
    public final TagRepository getTagRepository() {

        if (tagRepository == null && rootTag != null) {
            synchronized (this) {
                if (tagRepository == null) {
                    tagRepository = new TagRepository(ACCESS_OBJECT, this, tagByWffId, rootTag);
                }
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
     * Gets the same instance of {@code PayloadProcessor} per caller thread for this
     * browser page. This PayloadProcessor can process incoming partial bytes from
     * WebSocket. To manually create new PayloadProcessor use <em>new
     * PayloadProcessor(browserPage)</em>.
     *
     * @return new instance of PayloadProcessor/thread
     * @since 3.0.2
     * @deprecated this method call may make deadlock somewhere in the application
     *             while using multiple threads. Use
     *             {@code BrowserPage#newPayloadProcessor()}.
     */
    @Deprecated
    public final PayloadProcessor getPayloadProcessor() {
        PayloadProcessor payloadProcessor = PALYLOAD_PROCESSOR_TL.get();
        if (payloadProcessor == null) {
            payloadProcessor = new PayloadProcessor(this, true);
            PALYLOAD_PROCESSOR_TL.set(payloadProcessor);
        }
        return payloadProcessor;
    }

    /**
     * Removes the current instance of {@code PayloadProcessor} of this caller
     * thread for this browser page and new instance will be reinitialized when
     * calling {@link #getPayloadProcessor()} by the same thread.
     *
     * @since 3.0.2
     * @deprecated this method call may make deadlock.
     */
    @Deprecated
    public final void removePayloadProcessor() {
        PALYLOAD_PROCESSOR_TL.remove();
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
        synchronized (this) {
            // to read up to date value from
            // main memory synchronized will do it as per
            // java memory model
            return executor;
        }
    }

    /**
     * Sets the executor to run {@link ServerAsyncMethod#asyncMethod}, <br>
     *
     * <br>
     * NB: You may need only one copy of executor object for all browserPage
     * instances in the project. Eg: <br>
     *
     * <pre>
     * <code>
     *
     * public static final Executor EXECUTOR = Executors.newCachedThreadPool();
     * browserPage.setExecutor(EXECUTOR);
     * </code>
     * </pre>
     *
     * When Java releases Virtual Thread we may be able to use as follows
     *
     * <pre>
     * <code>
     * public static final Executor EXECUTOR = Executors.newVirtualThreadExecutor();
     * browserPage.setExecutor(EXECUTOR);
     * </code>
     * </pre>
     *
     * @param executor
     * @since 3.0.15
     */
    protected final void setExecutor(final Executor executor) {
        synchronized (this) {
            // to flush modification to main memory
            // synchronized will do it as per
            // java memory model
            this.executor = executor;
        }
    }

}
