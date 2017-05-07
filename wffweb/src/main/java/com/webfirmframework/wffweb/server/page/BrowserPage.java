/*
 * Copyright 2014-2017 Web Firm Framework
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
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.webfirmframework.wffweb.InvalidValueException;
import com.webfirmframework.wffweb.NotRenderedException;
import com.webfirmframework.wffweb.NullValueException;
import com.webfirmframework.wffweb.PushFailedException;
import com.webfirmframework.wffweb.server.page.js.WffJsFile;
import com.webfirmframework.wffweb.tag.html.AbstractHtml;
import com.webfirmframework.wffweb.tag.html.Html;
import com.webfirmframework.wffweb.tag.html.TagNameConstants;
import com.webfirmframework.wffweb.tag.html.attribute.Type;
import com.webfirmframework.wffweb.tag.html.attribute.core.AbstractAttribute;
import com.webfirmframework.wffweb.tag.html.attribute.event.EventAttribute;
import com.webfirmframework.wffweb.tag.html.attribute.event.ServerAsyncMethod;
import com.webfirmframework.wffweb.tag.html.attribute.listener.AttributeValueChangeListener;
import com.webfirmframework.wffweb.tag.html.html5.attribute.global.DataWffId;
import com.webfirmframework.wffweb.tag.html.programming.Script;
import com.webfirmframework.wffweb.tag.htmlwff.NoTag;
import com.webfirmframework.wffweb.tag.repository.TagRepository;
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

    private static final long serialVersionUID = 1_0_0L;

    public static final Logger LOGGER = Logger
            .getLogger(BrowserPage.class.getName());

    private static final boolean PRODUCTION_MODE = true;

    private final String instanceId = UUID.randomUUID().toString();

    private AttributeValueChangeListener valueChangeListener;

    private Map<String, AbstractHtml> tagByWffId;

    private volatile AbstractHtml rootTag;

    private final Map<String, WebSocketPushListener> sessionIdWsListeners = new HashMap<String, WebSocketPushListener>();

    private final Deque<WebSocketPushListener> wsListeners = new ArrayDeque<WebSocketPushListener>();

    private WebSocketPushListener wsListener;

    private DataWffId wffScriptTagId;

    private final Queue<ByteBuffer> wffBMBytesQueue = new ConcurrentLinkedDeque<ByteBuffer>();

    private final Queue<ByteBuffer> wffBMBytesHoldPushQueue = new ConcurrentLinkedDeque<ByteBuffer>();

    private static final Security ACCESS_OBJECT = new Security();

    // by default the push queue should be enabled
    private boolean pushQueueEnabled = true;

    // by default the pushQueueOnNewWebSocketListener should be enabled
    private boolean pushQueueOnNewWebSocketListener = true;

    private volatile boolean holdPush;

    private final Map<String, ServerAsyncMethod> serverMethods = new HashMap<String, ServerAsyncMethod>();

    private boolean removeFromBrowserContextOnTabClose = true;

    private boolean removePrevFromBrowserContextOnTabInit = true;

    private int wsHeartbeatInterval = -1;

    private int wsReconnectInterval = -1;

    private static int wsDefaultHeartbeatInterval = -1;

    private static int wsDefaultReconnectInterval = 2000;

    private final AtomicInteger pushQueueSize = new AtomicInteger(0);

    private volatile TagRepository tagRepository;

    // for security purpose, the class name should not be modified
    private static final class Security implements Serializable {

        private static final long serialVersionUID = 1L;

        private Security() {
        }
    }

    /**
     * To specify (by removeFromContext method) when to remove
     * {@code BrowserPage} instance from {@code BrowserPageContext}.
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
         * To remove the previous {@code BrowserPage} instance opened in the
         * same tab when new {@code BrowserPage} is requested by the tab.
         */
        INIT_REMOVE_PREVIOUS;
    }

    public abstract String webSocketUrl();

    /**
     * @param wsListener
     * @since 2.0.0
     * @author WFF
     */
    public void setWebSocketPushListener(
            final WebSocketPushListener wsListener) {
        this.wsListener = wsListener;
        if (pushQueueOnNewWebSocketListener) {
            pushWffBMBytesQueue();
        }
    }

    /**
     * adds the WebSocket listener for the given WebSocket session
     *
     * @param sessionId
     *            the unique id of WebSocket session
     * @param wsListener
     * @since 2.1.0
     * @author WFF
     */
    public void addWebSocketPushListener(final String sessionId,
            final WebSocketPushListener wsListener) {

        sessionIdWsListeners.put(sessionId, wsListener);
        wsListeners.push(wsListener);

        this.wsListener = wsListener;

        if (pushQueueOnNewWebSocketListener) {
            pushWffBMBytesQueue();
        }

    }

    /**
     * removes the WebSocket listener added for this WebSocket session
     *
     * @param sessionId
     *            the unique id of WebSocket session
     * @since 2.1.0
     * @author WFF
     */
    public void removeWebSocketPushListener(final String sessionId) {

        final WebSocketPushListener removed = sessionIdWsListeners
                .remove(sessionId);
        wsListeners.remove(removed);

        wsListener = wsListeners.peek();
    }

    public WebSocketPushListener getWsListener() {
        return wsListener;
    }

    void push(final NameValue... nameValues) {
        push(ByteBuffer.wrap(WffBinaryMessageUtil.VERSION_1
                .getWffBinaryMessageBytes(nameValues)));
    }

    private void push(final ByteBuffer wffBM) {

        if (holdPush) {
            wffBMBytesHoldPushQueue.add(wffBM);
        } else {
            wffBMBytesQueue.add(wffBM);
            pushQueueSize.incrementAndGet();
            pushWffBMBytesQueue();
        }
    }

    private void pushWffBMBytesQueue() {
        if (wsListener != null) {

            ByteBuffer byteBuffer = wffBMBytesQueue.poll();

            if (byteBuffer != null) {

                do {
                    pushQueueSize.decrementAndGet();

                    try {
                        wsListener.push(byteBuffer);
                    } catch (final PushFailedException e) {
                        if (pushQueueEnabled) {
                            wffBMBytesQueue.add(byteBuffer);
                            pushQueueSize.incrementAndGet();
                        }
                        break;
                    }

                    byteBuffer = wffBMBytesQueue.poll();
                } while (byteBuffer != null);
            }

        } else {
            if (LOGGER.isLoggable(Level.WARNING)) {
                LOGGER.warning(
                        "There is no WebSocket listener set, set it with BrowserPage#setWebSocketPushListener method.");
            }
        }
    }

    DataWffId getNewDataWffId() {
        return rootTag.getSharedObject().getNewDataWffId(ACCESS_OBJECT);
    }

    /**
     * This method will be remove later. Use {@code webSocketMessaged}.
     *
     * @param message
     *            the bytes the received in onmessage
     * @since 2.0.0
     * @author WFF
     * @deprecated alternative method webSocketMessaged is available for the
     *             same job.
     *
     */
    @Deprecated
    public void websocketMessaged(final byte[] message) {
        webSocketMessaged(message);
    }

    /**
     * @param message
     *            the bytes the received in onmessage
     * @since 2.1.0
     * @author WFF
     */
    public void webSocketMessaged(final byte[] message) {
        try {

            // TODO minimum number of an empty bm message length is 4
            // below that length is not a valid bm message so check
            // message.length < 4
            // later if there is such requirement
            if (message.length == 0) {
                // should not proceed if the message.length is zero because
                // to avoid exception if the client sends an empty message just
                // for ping
                return;
            }

            executeWffBMTask(message);
        } catch (final Exception e) {
            if (!PRODUCTION_MODE) {
                e.printStackTrace();
            }
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE,
                        "Could not process this data received from client.", e);
            }
        }
    }

    /**
     * Override and use this method to render html content to the client browser
     * page.
     *
     * @return the object of {@link Html} class which needs to be displayed in
     *         the client browser page.
     * @author WFF
     */
    public abstract AbstractHtml render();

    private void invokeAsychMethod(final List<NameValue> nameValues)
            throws UnsupportedEncodingException {
        //@formatter:off
        // invoke method task format :-
        // { "name": task_byte, "values" : [invoke_method_byte_from_Task_enum]}, { "name": data-wff-id, "values" : [ event_attribute_name ]}
        // { "name": 2, "values" : [[0]]}, { "name":"C55", "values" : ["onclick"]}
        //@formatter:on

        final NameValue wffTagIdAndAttrName = nameValues.get(1);
        final byte[] intBytes = new byte[wffTagIdAndAttrName.getName().length
                - 1];
        System.arraycopy(wffTagIdAndAttrName.getName(), 1, intBytes, 0,
                intBytes.length);

        final String wffTagId = new String(wffTagIdAndAttrName.getName(), 0, 1,
                "UTF-8")
                + WffBinaryMessageUtil.getIntFromOptimizedBytes(intBytes);

        final byte[][] values = wffTagIdAndAttrName.getValues();
        final String eventAttrName = new String(values[0], "UTF-8");

        WffBMObject wffBMObject = null;
        if (values.length > 1) {
            final byte[] wffBMObjectBytes = values[1];
            wffBMObject = new WffBMObject(wffBMObjectBytes, true);

        }

        final AbstractHtml methodTag = tagByWffId.get(wffTagId);
        if (methodTag != null) {

            final AbstractAttribute attributeByName = methodTag
                    .getAttributeByName(eventAttrName);

            if (attributeByName != null) {

                if (attributeByName instanceof EventAttribute) {

                    final EventAttribute eventAttr = (EventAttribute) attributeByName;

                    final ServerAsyncMethod serverAsyncMethod = eventAttr
                            .getServerAsyncMethod();

                    final ServerAsyncMethod.Event event = new ServerAsyncMethod.Event(
                            methodTag, attributeByName);

                    final WffBMObject returnedObject = serverAsyncMethod
                            .asyncMethod(wffBMObject, event);

                    final String jsPostFunctionBody = eventAttr
                            .getJsPostFunctionBody();

                    if (jsPostFunctionBody != null) {

                        final NameValue invokePostFunTask = Task.INVOKE_POST_FUNCTION
                                .getTaskNameValue();
                        final NameValue nameValue = new NameValue();
                        // name as function body string and value at
                        // zeroth index as
                        // wffBMObject bytes
                        nameValue.setName(jsPostFunctionBody.getBytes("UTF-8"));

                        if (returnedObject != null) {
                            nameValue.setValues(new byte[][] {
                                    returnedObject.build(true) });
                        }

                        push(invokePostFunTask, nameValue);
                    }

                } else {
                    LOGGER.severe(attributeByName
                            + " is NOT instanceof EventAttribute");
                }

            } else {
                LOGGER.severe(
                        "no event attribute found for " + attributeByName);
            }

        } else {
            if (!PRODUCTION_MODE) {
                LOGGER.severe("No tag found for wffTagId " + wffTagId);
            }
        }
    }

    private void removeBrowserPageFromContext(final List<NameValue> nameValues)
            throws UnsupportedEncodingException {
        //@formatter:off
        // invoke custom server method task format :-
        // { "name": task_byte, "values" : [remove_browser_page_byte_from_Task_enum]},
        // { "name": wff-instance-id-bytes, "values" : []}
        //@formatter:on

        final NameValue instanceIdNameValue = nameValues.get(1);

        final String instanceIdToRemove = new String(
                instanceIdNameValue.getName(), "UTF-8");

        BrowserPageContext.INSTANCE.removeBrowserPage(getInstanceId(),
                instanceIdToRemove);
    }

    private void invokeCustomServerMethod(final List<NameValue> nameValues)
            throws UnsupportedEncodingException {
        //@formatter:off
        // invoke custom server method task format :-
        // { "name": task_byte, "values" : [invoke_custom_server_method_byte_from_Task_enum]},
        // { "name": server method name bytes, "values" : [ wffBMObject bytes ]}
        // { "name": callback function id bytes, "values" : [ ]}
        //@formatter:on

        final NameValue methodNameAndArg = nameValues.get(1);
        final String methodName = new String(methodNameAndArg.getName(),
                "UTF-8");

        final ServerAsyncMethod serverAsyncMethod = serverMethods
                .get(methodName);

        if (serverAsyncMethod != null) {

            final byte[][] values = methodNameAndArg.getValues();

            WffBMObject wffBMObject = null;

            if (values.length > 0) {
                wffBMObject = new WffBMObject(values[0], true);
            }

            final ServerAsyncMethod.Event event = new ServerAsyncMethod.Event(
                    methodName);

            final WffBMObject returnedObject = serverAsyncMethod
                    .asyncMethod(wffBMObject, event);

            String callbackFunId = null;

            if (nameValues.size() > 2) {
                final NameValue callbackFunNameValue = nameValues.get(2);
                callbackFunId = new String(callbackFunNameValue.getName(),
                        "UTF-8");
            }

            if (callbackFunId != null) {
                final NameValue invokeCallbackFuncTask = Task.INVOKE_CALLBACK_FUNCTION
                        .getTaskNameValue();

                final NameValue nameValue = new NameValue();
                nameValue.setName(callbackFunId.getBytes("UTF-8"));

                if (returnedObject != null) {
                    nameValue.setValues(
                            new byte[][] { returnedObject.build(true) });
                }

                push(invokeCallbackFuncTask, nameValue);

            }

        } else {
            LOGGER.warning(methodName
                    + " doesn't exist, please add it as browserPage.addServerMethod(\""
                    + methodName + "\", serverAsyncMethod)");
        }

    }

    /**
     * executes the task in the given wff binary message. <br>
     * For WFF authors :- Make sure that the passing {@code message} is not
     * empty while consuming this method, just as made conditional checking in
     * {@code BrowserPage#webSocketMessaged(byte[])} method.
     *
     * @since 2.0.0
     * @author WFF
     * @throws UnsupportedEncodingException
     */
    private void executeWffBMTask(final byte[] message)
            throws UnsupportedEncodingException {

        final List<NameValue> nameValues = WffBinaryMessageUtil.VERSION_1
                .parse(message);

        final NameValue task = nameValues.get(0);
        final byte taskValue = task.getValues()[0][0];

        if (Task.TASK.getValueByte() == task.getName()[0]) {

            // IM stands for Invoke Method
            if (taskValue == Task.INVOKE_ASYNC_METHOD.getValueByte()) {

                invokeAsychMethod(nameValues);

            } else if (taskValue == Task.INVOKE_CUSTOM_SERVER_METHOD
                    .getValueByte()) {

                invokeCustomServerMethod(nameValues);

            } else if (taskValue == Task.REMOVE_BROWSER_PAGE.getValueByte()) {

                removeBrowserPageFromContext(nameValues);

            }

        }

    }

    private void addAttrValueChangeListener(final AbstractHtml abstractHtml) {

        if (valueChangeListener == null) {
            valueChangeListener = new AttributeValueChangeListenerImpl(this,
                    tagByWffId);
        }

        abstractHtml.getSharedObject()
                .setValueChangeListener(valueChangeListener, ACCESS_OBJECT);
    }

    private void addDataWffIdAttribute(final AbstractHtml abstractHtml) {

        final Deque<Set<AbstractHtml>> childrenStack = new ArrayDeque<Set<AbstractHtml>>();
        childrenStack.push(
                new LinkedHashSet<AbstractHtml>(Arrays.asList(abstractHtml)));

        while (childrenStack.size() > 0) {

            final Set<AbstractHtml> children = childrenStack.pop();

            for (final AbstractHtml child : children) {

                if (child.getDataWffId() == null) {
                    child.setDataWffId(getNewDataWffId());
                }

                tagByWffId.put(child.getDataWffId().getValue(), child);

                final Set<AbstractHtml> subChildren = child
                        .getChildren(ACCESS_OBJECT);

                if (subChildren != null && subChildren.size() > 0) {
                    childrenStack.push(subChildren);
                }

            }

        }

    }

    private void embedWffScriptIfRequired(final AbstractHtml abstractHtml,
            final String wsUrlWithInstanceId) {

        if (wffScriptTagId != null
                && tagByWffId.containsKey(wffScriptTagId.getValue())) {
            // no need to add script tag if it exists in the ui
            return;
        }

        final Deque<Set<AbstractHtml>> childrenStack = new ArrayDeque<Set<AbstractHtml>>();
        childrenStack.push(
                new LinkedHashSet<AbstractHtml>(Arrays.asList(abstractHtml)));

        boolean bodyTagMissing = true;

        outerLoop: while (childrenStack.size() > 0) {

            final Set<AbstractHtml> children = childrenStack.pop();

            for (final AbstractHtml child : children) {

                if (TagNameConstants.BODY.equals(child.getTagName())) {

                    bodyTagMissing = false;

                    wffScriptTagId = getNewDataWffId();

                    final Script script = new Script(null,
                            new Type("text/javascript"));

                    script.setDataWffId(wffScriptTagId);

                    new NoTag(script, WffJsFile.getAllOptimizedContent(
                            wsUrlWithInstanceId, getInstanceId(),
                            removePrevFromBrowserContextOnTabInit,
                            removeFromBrowserContextOnTabClose,
                            (wsHeartbeatInterval > 0 ? wsHeartbeatInterval
                                    : wsDefaultHeartbeatInterval),
                            (wsReconnectInterval > 0 ? wsReconnectInterval
                                    : wsDefaultReconnectInterval)));

                    // to avoid invoking listener
                    child.addChild(ACCESS_OBJECT, script, false);

                    // ConcurrentHashMap cannot contain null as value
                    tagByWffId.put(wffScriptTagId.getValue(), script);

                    break outerLoop;
                }

                final Set<AbstractHtml> subChildren = child
                        .getChildren(ACCESS_OBJECT);

                if (subChildren != null && subChildren.size() > 0) {
                    childrenStack.push(subChildren);
                }
            }

        }

        if (bodyTagMissing) {
            wffScriptTagId = getNewDataWffId();

            final Script script = new Script(null, new Type("text/javascript"));

            script.setDataWffId(wffScriptTagId);

            new NoTag(script,
                    WffJsFile.getAllOptimizedContent(wsUrlWithInstanceId,
                            getInstanceId(),
                            removePrevFromBrowserContextOnTabInit,
                            removeFromBrowserContextOnTabClose,
                            (wsHeartbeatInterval > 0 ? wsHeartbeatInterval
                                    : wsDefaultHeartbeatInterval),
                            (wsReconnectInterval > 0 ? wsReconnectInterval
                                    : wsDefaultReconnectInterval)));

            // to avoid invoking listener
            abstractHtml.addChild(ACCESS_OBJECT, script, false);

            // ConcurrentHashMap cannot contain null as value
            tagByWffId.put(wffScriptTagId.getValue(), script);

        }

    }

    private void addChildTagAppendListener(final AbstractHtml abstractHtml) {

        abstractHtml.getSharedObject().setChildTagAppendListener(
                new ChildTagAppendListenerImpl(this, ACCESS_OBJECT, tagByWffId),
                ACCESS_OBJECT);
    }

    private void addChildTagRemoveListener(final AbstractHtml abstractHtml) {

        abstractHtml.getSharedObject().setChildTagRemoveListener(
                new ChildTagRemoveListenerImpl(this, ACCESS_OBJECT, tagByWffId),
                ACCESS_OBJECT);
    }

    private void addAttributeAddListener(final AbstractHtml abstractHtml) {
        abstractHtml.getSharedObject().setAttributeAddListener(
                new AttributeAddListenerImpl(this), ACCESS_OBJECT);
    }

    private void addAttributeRemoveListener(final AbstractHtml abstractHtml) {
        abstractHtml.getSharedObject().setAttributeRemoveListener(
                new AttributeRemoveListenerImpl(this, tagByWffId),
                ACCESS_OBJECT);
    }

    private void addInnerHtmlAddListener(final AbstractHtml abstractHtml) {
        abstractHtml.getSharedObject().setInnerHtmlAddListener(
                new InnerHtmlAddListenerImpl(this, ACCESS_OBJECT, tagByWffId),
                ACCESS_OBJECT);
    }

    private void addInsertBeforeListener(final AbstractHtml abstractHtml) {
        abstractHtml.getSharedObject().setInsertBeforeListener(
                new InsertBeforeListenerImpl(this, ACCESS_OBJECT, tagByWffId),
                ACCESS_OBJECT);
    }

    /**
     * @return {@code String} equalent to the html string of the tag including
     *         the child tags.
     *
     * @author WFF
     */
    public String toHtmlString() {
        initAbstractHtml();
        return rootTag.toHtmlString(true);
    }

    /**
     * rebuilds the html string of the tag including the child tags/values if
     * parameter is true, otherwise returns the html string prebuilt and kept in
     * the cache.
     *
     * @param rebuild
     *            true to rebuild & false to return previously built string.
     * @return {@code String} equalent to the html string of the tag including
     *         the child tags.
     * @since 2.1.4
     * @author WFF
     */
    public String toHtmlString(final boolean rebuild) {
        initAbstractHtml();
        return rootTag.toHtmlString(rebuild);
    }

    /**
     * @param charset
     *            the charset
     * @return {@code String} equalent to the html string of the tag including
     *         the child tags.
     * @author WFF
     */
    public String toHtmlString(final String charset) {
        initAbstractHtml();
        return rootTag.toHtmlString(true, charset);
    }

    /**
     * rebuilds the html string of the tag including the child tags/values if
     * parameter is true, otherwise returns the html string prebuilt and kept in
     * the cache.
     *
     * @param rebuild
     *            true to rebuild & false to return previously built string.
     * @param the
     *            charset to set for the returning value, eg:
     *            {@code StandardCharsets.UTF_8.name()}
     * @return {@code String} equalent to the html string of the tag including
     *         the child tags.
     *
     * @since 2.1.4
     * @author WFF
     */
    public String toHtmlString(final boolean rebuild, final String charset) {
        initAbstractHtml();
        return rootTag.toHtmlString(rebuild, charset);
    }

    /**
     * @param os
     *            the object of {@code OutputStream} to write to.
     * @return the total number of bytes written
     * @since 2.1.4 void toOutputStream
     * @since 2.1.8 int toOutputStream
     * @throws IOException
     */
    public int toOutputStream(final OutputStream os) throws IOException {
        initAbstractHtml();
        return rootTag.toOutputStream(os, true);
    }

    /**
     * @param os
     *            the object of {@code OutputStream} to write to.
     * @param rebuild
     *            true to rebuild & false to write previously built bytes.
     * @return the total number of bytes written
     *
     * @throws IOException
     * @since 2.1.4 void toOutputStream
     * @since 2.1.8 int toOutputStream
     *
     */
    public int toOutputStream(final OutputStream os, final boolean rebuild)
            throws IOException {
        initAbstractHtml();
        return rootTag.toOutputStream(os, rebuild);
    }

    /**
     * @param os
     *            the object of {@code OutputStream} to write to.
     * @param rebuild
     *            true to rebuild & false to write previously built bytes.
     * @param charset
     *            the charset
     * @return the total number of bytes written
     * @throws IOException
     * @since 2.1.4 void toOutputStream
     * @since 2.1.8 int toOutputStream
     */
    public int toOutputStream(final OutputStream os, final String charset)
            throws IOException {
        initAbstractHtml();

        return rootTag.toOutputStream(os, true, charset);
    }

    /**
     * @param os
     *            the object of {@code OutputStream} to write to.
     * @param rebuild
     *            true to rebuild & false to write previously built bytes.
     * @param charset
     *            the charset
     * @return the total number of bytes written
     * @throws IOException
     * @since 2.1.4 void toOutputStream
     * @since 2.1.8 int toOutputStream
     *
     */
    public int toOutputStream(final OutputStream os, final boolean rebuild,
            final String charset) throws IOException {
        initAbstractHtml();

        return rootTag.toOutputStream(os, rebuild, charset);
    }

    private void initAbstractHtml() {

        if (rootTag == null) {

            synchronized (this) {
                if (rootTag == null) {
                    rootTag = render();
                    if (rootTag == null) {
                        throw new NullValueException(
                                "render must return an instance of AbstractHtml, eg:- new Html(null);");
                    }

                    tagByWffId = rootTag.getSharedObject()
                            .initTagByWffId(ACCESS_OBJECT);

                    addDataWffIdAttribute(rootTag);
                    // attribute value change listener
                    // should be added only after adding data-wff-id attribute
                    addAttrValueChangeListener(rootTag);
                    addChildTagAppendListener(rootTag);
                    addChildTagRemoveListener(rootTag);
                    addAttributeAddListener(rootTag);
                    addAttributeRemoveListener(rootTag);
                    addInnerHtmlAddListener(rootTag);
                    addInsertBeforeListener(rootTag);
                } else {
                    synchronized (wffBMBytesQueue) {
                        wffBMBytesQueue.clear();
                        pushQueueSize.set(0);
                    }
                }
            }

        } else {
            synchronized (wffBMBytesQueue) {
                wffBMBytesQueue.clear();
                pushQueueSize.set(0);
            }
        }

        final String webSocketUrl = webSocketUrl();
        if (webSocketUrl == null) {
            throw new NullValueException(
                    "webSocketUrl must return valid WebSocket url");
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
    public String getInstanceId() {
        return instanceId;
    }

    /**
     * By default, it is set as true.
     *
     * @return the pushQueueEnabled
     * @since 2.0.2
     */
    public boolean isPushQueueEnabled() {
        return pushQueueEnabled;
    }

    /**
     * If the server could not push any server updates it will be put in the
     * queue and when it tries to push in the next time it will first push
     * updates from this queue. By default, it is set as true.
     *
     * @param enabledPushQueue
     *            the enabledPushQueue to set
     * @since 2.0.2
     */
    public void setPushQueueEnabled(final boolean enabledPushQueue) {
        pushQueueEnabled = enabledPushQueue;
    }

    /**
     * @param methodName
     * @param serverAsyncMethod
     * @since 2.1.0
     * @author WFF
     */
    public void addServerMethod(final String methodName,
            final ServerAsyncMethod serverAsyncMethod) {
        serverMethods.put(methodName, serverAsyncMethod);
    }

    /**
     * removes the method from
     *
     * @param methodName
     * @since 2.1.0
     * @author WFF
     */
    public void removeServerMethod(final String methodName) {
        serverMethods.remove(methodName);
    }

    /**
     * performs action provided by {@code BrowserPageAction}.
     *
     * @param actionByteBuffer
     *            The ByteBuffer object taken from {@code BrowserPageAction}
     *            .Eg:- {@code BrowserPageAction.RELOAD.getActionByteBuffer();}
     * @since 2.1.0
     * @author WFF
     */
    public void performBrowserPageAction(final ByteBuffer actionByteBuffer) {
        push(actionByteBuffer);
    }

    /**
     * @return the pushQueueOnNewWebSocketListener true if it's enabled
     *         otherwise false. By default it's set as true.
     * @since 2.1.1
     */
    public boolean isPushQueueOnNewWebSocketListener() {
        return pushQueueOnNewWebSocketListener;
    }

    /**
     * By default it's set as true. If it's enabled then the wffbmBytesQueue
     * will be pushed when new webSocket listener is added/set.
     *
     * @param pushQueueOnNewWebSocketListener
     *            the pushQueueOnNewWebSocketListener to set. Pass true to
     *            enable this option and false to disable this option.
     * @since 2.1.1
     */
    public void setPushQueueOnNewWebSocketListener(
            final boolean pushQueueOnNewWebSocketListener) {
        this.pushQueueOnNewWebSocketListener = pushQueueOnNewWebSocketListener;
    }

    /**
     *
     * @return the holdPush true if the push is on hold
     * @since 2.1.3
     */
    public boolean isHoldPush() {
        return holdPush;
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
    public void holdPush() {
        holdPush = true;
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
    public void unholdPush() {

        if (holdPush) {

            synchronized (wffBMBytesQueue) {

                holdPush = false;

                ByteBuffer wffBM = wffBMBytesHoldPushQueue.poll();

                if (wffBM != null) {

                    final NameValue invokeMultipleTasks = Task
                            .getTaskOfTasksNameValue();

                    final Deque<ByteBuffer> wffBMs = new ArrayDeque<ByteBuffer>(
                            wffBMBytesHoldPushQueue.size());

                    do {

                        wffBMs.add(wffBM);

                        wffBM = wffBMBytesHoldPushQueue.poll();
                    } while (wffBM != null);

                    final byte[][] values = new byte[wffBMs.size()][0];

                    int index = 0;
                    for (final ByteBuffer eachWffBM : wffBMs) {
                        values[index] = eachWffBM.array();
                        index++;
                    }

                    invokeMultipleTasks.setValues(values);

                    wffBMBytesQueue
                            .add(ByteBuffer.wrap(WffBinaryMessageUtil.VERSION_1
                                    .getWffBinaryMessageBytes(
                                            invokeMultipleTasks)));
                    pushQueueSize.incrementAndGet();

                    pushWffBMBytesQueue();
                }

            }
        }

    }

    /**
     * Gets the size of internal push queue. This size might not be accurate in
     * multi-threading environment.
     *
     * Use case :- Suppose there is a thread in the server which makes real time
     * ui changes. But if the end user lost connection and the webSocket is not
     * closed connection, in such case the developer can decide whether to make
     * any more ui updates from server when the pushQueueSize exceeds a
     * particular limit.
     *
     * @return the size of internal push queue.
     * @since 2.1.4
     * @author WFF
     */
    public int getPushQueueSize() {
        // wffBMBytesQueue.size() is not reliable as
        // it's ConcurrentLinkedDeque
        return pushQueueSize.get();
    }

    /**
     * By default On.TAB_CLOSE and On.INIT_REMOVE_PREVIOUS are enabled.
     *
     * @param enable
     * @param ons
     *            the instance of On to represent on which browser event the
     *            browser page needs to be removed.
     * @since 2.1.4
     * @author WFF
     */
    public void removeFromContext(final boolean enable, final On... ons) {
        for (final On on : ons) {
            if (On.TAB_CLOSE.equals(on)) {
                removeFromBrowserContextOnTabClose = enable;
            } else if (On.INIT_REMOVE_PREVIOUS.equals(on)) {
                removePrevFromBrowserContextOnTabInit = enable;
            }
        }
    }

    /**
     * Invokes when this browser page instance is removed from browser page
     * context. Override and use this method to stop long running tasks /
     * threads.
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
     * {@code browserPage#toOutputStream} is called at least once in the life
     * time.
     *
     * @param tag
     *            the tag object to be checked.
     * @return true if the given tag contains in the BrowserPage i.e. UI. false
     *         if the given tag was removed or was not already added in the UI.
     * @throws NullValueException
     *             throws this exception if the given tag is null.
     * @throws NotRenderedException
     *             if the {@code BrowserPage} object is not rendered. i.e. if
     *             {@code browserPage#toHtmlString} or
     *             {@code browserPage#toOutputStream} was NOT called at least
     *             once in the life time.
     * @since 2.1.7
     * @author WFF
     */
    public boolean contains(final AbstractHtml tag)
            throws NullValueException, NotRenderedException {

        if (tag == null) {
            throw new NullValueException(
                    "tag object in browserPage.contains(AbstractHtml tag) method cannot be null");
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
     * Sets the heartbeat ping interval of webSocket client in milliseconds.
     * Give -1 to disable it. By default it's set with -1. It affects only for
     * the corresponding {@code BrowserPage} instance from which it is called.
     * <br>
     * NB:- This method has effect only if it is called before
     * {@code BrowserPage#render()} method return. This method can be called
     * inside {@code BrowserPage#render()} method to override the default global
     * heartbeat interval set by
     * {@code BrowserPage#setWebSocketDefultHeartbeatInterval(int)} method.
     *
     * @param milliseconds
     *            the heartbeat ping interval of webSocket client in
     *            milliseconds. Give -1 to disable it.
     * @since 2.1.8
     * @author WFF
     */
    protected void setWebSocketHeartbeatInterval(final int milliseconds) {
        wsHeartbeatInterval = milliseconds;
    }

    /**
     * @return the interval value set by
     *         {@code BrowserPage#setWebSocketHeartbeatInterval(int)} method.
     * @since 2.1.8
     * @author WFF
     */
    public int getWebSocketHeartbeatInterval() {
        return wsHeartbeatInterval;
    }

    /**
     * Sets the default heartbeat ping interval of webSocket client in
     * milliseconds. Give -1 to disable it. It affects globally. By default it's
     * set with -1.<br>
     * NB:- This method has effect only if it is called before
     * {@code BrowserPage#render()} invocation.
     *
     *
     * @param milliseconds
     *            the heartbeat ping interval of webSocket client in
     *            milliseconds. Give -1 to disable it
     * @since 2.1.8
     * @author WFF
     */
    public static void setWebSocketDefultHeartbeatInterval(
            final int milliseconds) {
        wsDefaultHeartbeatInterval = milliseconds;
    }

    /**
     * @return the interval value set by
     *         {@code setWebSocketDefultHeartbeatInterval} method.
     * @since 2.1.8
     * @author WFF
     */
    public static int getWebSocketDefultHeartbeatInterval() {
        return wsDefaultHeartbeatInterval;
    }

    /**
     * Sets the default reconnect interval of webSocket client in milliseconds.
     * It affects globally. By default it's set with 2000 ms.<br>
     * NB:- This method has effect only if it is called before
     * {@code BrowserPage#render()} invocation.
     *
     *
     * @param milliseconds
     *            the reconnect interval of webSocket client in milliseconds. It
     *            must be greater than 0.
     * @since 2.1.8
     * @author WFF
     */
    public static void setWebSocketDefultReconnectInterval(
            final int milliseconds) {
        if (milliseconds < 1) {
            throw new InvalidValueException("The value must be greater than 0");
        }
        wsDefaultReconnectInterval = milliseconds;
    }

    /**
     * @return the interval value set by
     *         {@code setWebSocketDefultReconnectInterval} method.
     * @since 2.1.8
     * @author WFF
     */
    public static int getWebSocketDefultReconnectInterval() {
        return wsDefaultReconnectInterval;
    }

    /**
     * Sets the reconnect interval of webSocket client in milliseconds. Give -1
     * to disable it. By default it's set with -1. It affects only for the
     * corresponding {@code BrowserPage} instance from which it is called. <br>
     * NB:- This method has effect only if it is called before
     * {@code BrowserPage#render()} method return. This method can be called
     * inside {@code BrowserPage#render()} method to override the default global
     * WebSocket reconnect interval set by
     * {@code BrowserPage#setWebSocketDefultReconnectInterval(int)} method.
     *
     * @param milliseconds
     *            the reconnect interval of webSocket client in milliseconds.
     *            Give -1 to disable it.
     * @since 2.1.8
     * @author WFF
     */
    protected void setWebSocketReconnectInterval(final int milliseconds) {
        wsReconnectInterval = milliseconds;
    }

    /**
     * @return the interval value set by
     *         {@code BrowserPage#setWebSocketReconnectInterval(int)} method.
     * @since 2.1.8
     * @author WFF
     */
    public int getWebSocketReconnectInterval() {
        return wsReconnectInterval;
    }

    /**
     * Gets the TagRepository to do different tag operations.
     *
     * @return the TagRepository object to do different tag operations.
     * @since 2.1.8
     * @author WFF
     */
    public TagRepository getTagRepository() {

        if (tagRepository == null) {
            synchronized (this) {
                if (tagRepository == null) {
                    tagRepository = new TagRepository(rootTag);
                }
            }
        }

        return tagRepository;
    }

}
