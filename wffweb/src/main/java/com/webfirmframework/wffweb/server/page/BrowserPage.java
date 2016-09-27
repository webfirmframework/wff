/*
 * Copyright 2014-2016 Web Firm Framework
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
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import com.webfirmframework.wffweb.NullValueException;
import com.webfirmframework.wffweb.PushFailedException;
import com.webfirmframework.wffweb.server.page.js.WffJsFile;
import com.webfirmframework.wffweb.tag.html.AbstractHtml;
import com.webfirmframework.wffweb.tag.html.TagNameConstants;
import com.webfirmframework.wffweb.tag.html.attribute.Type;
import com.webfirmframework.wffweb.tag.html.attribute.core.AbstractAttribute;
import com.webfirmframework.wffweb.tag.html.attribute.event.EventAttribute;
import com.webfirmframework.wffweb.tag.html.attribute.event.ServerAsyncMethod;
import com.webfirmframework.wffweb.tag.html.attribute.listener.AttributeValueChangeListener;
import com.webfirmframework.wffweb.tag.html.html5.attribute.global.DataWffId;
import com.webfirmframework.wffweb.tag.html.programming.Script;
import com.webfirmframework.wffweb.tag.htmlwff.NoTag;
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

    private AbstractHtml abstractHtml;

    private WebSocketPushListener wsListener;

    private DataWffId wffScriptTagId;

    private final Queue<NameValue[]> wffBMBytesQueue = new ArrayDeque<NameValue[]>();

    private static final Security ACCESS_OBJECT = new Security();

    // by default the push queue should be enabled
    private boolean pushQueueEnabled = true;

    // for security purpose, the class name should not be modified
    private static final class Security implements Serializable {

        private static final long serialVersionUID = 1L;

        private Security() {
        }
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
    }

    public WebSocketPushListener getWsListener() {
        return wsListener;
    }

    void push(final NameValue... wffBM) {

        if (wsListener != null) {

            wffBMBytesQueue.add(wffBM);

            while (wffBMBytesQueue.size() > 0) {

                final NameValue[] nameValues = wffBMBytesQueue.poll();

                if (nameValues == null) {
                    break;
                }

                final byte[] wffBinaryMessageBytes = WffBinaryMessageUtil.VERSION_1
                        .getWffBinaryMessageBytes(nameValues);

                try {
                    wsListener.push(wffBinaryMessageBytes);
                } catch (final PushFailedException e) {
                    if (pushQueueEnabled) {
                        wffBMBytesQueue.add(nameValues);
                    }
                    break;
                }

            }

        } else {
            LOGGER.warning(
                    "There is no websocket listener set, set it with BrowserPage#setWebSocketPushListener method.");
        }

    }

    DataWffId getNewDataWffId() {
        return abstractHtml.getSharedObject().getNewDataWffId(ACCESS_OBJECT);
    }

    /**
     * @param message
     *            the bytes the received in onmessage
     * @since 2.0.0
     * @author WFF
     */
    public void websocketMessaged(final byte[] message) {
        try {
            executeWffBMTask(message);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public abstract AbstractHtml render();

    /**
     * executes the task in the given wff binary message
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

                //@formatter:off
                // invoke method task format :-
                // { "name": task_byte, "values" : [invoke_method_byte_from_Task_enum]}, { "name": data-wff-id, "values" : [ event_attribute_name ]}
                // { "name": 2, "values" : [[0]]}, { "name":"C55", "values" : ["onclick"]}
                //@formatter:on

                final NameValue wffTagIdAndAttrName = nameValues.get(1);
                final byte[] intBytes = new byte[wffTagIdAndAttrName
                        .getName().length - 1];
                System.arraycopy(wffTagIdAndAttrName.getName(), 1, intBytes, 0,
                        intBytes.length);

                final String wffTagId = new String(
                        wffTagIdAndAttrName.getName(), 0, 1, "UTF-8")
                        + WffBinaryMessageUtil
                                .getIntFromOptimizedBytes(intBytes);

                final byte[][] values = wffTagIdAndAttrName.getValues();
                final String eventAttrName = new String(values[0], "UTF-8");

                WffBMObject wffBMObject = null;
                if (values.length > 1) {
                    final byte[] wffBMObjectBytes = values[1];
                    wffBMObject = new WffBMObject(wffBMObjectBytes, true);

                }

                final AbstractHtml methodTag = tagByWffId.get(wffTagId);
                if (methodTag != null) {

                    final AbstractAttribute dataWffIdAttr = methodTag
                            .getAttributeByName(eventAttrName);

                    if (dataWffIdAttr != null) {

                        if (dataWffIdAttr instanceof EventAttribute) {

                            final EventAttribute eventAttr = (EventAttribute) dataWffIdAttr;

                            final ServerAsyncMethod serverAsyncMethod = eventAttr
                                    .getServerAsyncMethod();

                            final ServerAsyncMethod.Event event = new ServerAsyncMethod.Event(
                                    methodTag);

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
                                nameValue.setName(
                                        jsPostFunctionBody.getBytes("UTF-8"));

                                if (returnedObject != null) {
                                    nameValue.setValues(new byte[][] {
                                            returnedObject.build(true) });
                                }

                                push(invokePostFunTask, nameValue);
                            }

                        } else {
                            LOGGER.severe(dataWffIdAttr
                                    + " is NOT instanceof EventAttribute");
                        }

                    } else {
                        LOGGER.severe("no event attribute found for "
                                + dataWffIdAttr);
                    }

                } else {
                    if (!PRODUCTION_MODE) {
                        LOGGER.severe("No tag found for wffTagId " + wffTagId);
                    }
                }

            }

        }

    }

    private void addAttrValueChangeListener(final AbstractHtml abstractHtml) {

        if (valueChangeListener == null) {
            valueChangeListener = new AttributeValueChangeListenerImpl(
                    BrowserPage.this, tagByWffId);
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

        if (wffScriptTagId != null && tagByWffId.containsKey(wffScriptTagId)) {
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

                    new NoTag(script, WffJsFile
                            .getAllOptimizedContent(wsUrlWithInstanceId));

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
                    WffJsFile.getAllOptimizedContent(wsUrlWithInstanceId));

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

    /**
     * @return {@code String} equalent to the html string of the tag including
     *         the child tags.
     *
     * @author WFF
     */
    public final String toHtmlString() {
        initAbstractHtml();
        return abstractHtml.toHtmlString(true);
    }

    /**
     * @param charset
     *            the charset
     * @return {@code String} equalent to the html string of the tag including
     *         the child tags.
     * @author WFF
     */
    public final String toHtmlString(final String charset) {
        initAbstractHtml();
        return abstractHtml.toHtmlString(true, charset);
    }

    /**
     * @param os
     *            the object of {@code OutputStream} to write to.
     *
     * @throws IOException
     */
    public final void toOutputStream(final OutputStream os) throws IOException {
        initAbstractHtml();
        abstractHtml.toOutputStream(os, true);
    }

    /**
     * @param os
     *            the object of {@code OutputStream} to write to.
     * @param rebuild
     *            true to rebuild & false to write previously built bytes.
     * @param charset
     *            the charset
     * @throws IOException
     */
    public final void toOutputStream(final OutputStream os,
            final String charset) throws IOException {
        initAbstractHtml();

        abstractHtml.toOutputStream(os, true, charset);
    }

    private void initAbstractHtml() {

        if (abstractHtml == null) {

            abstractHtml = render();

            if (abstractHtml == null) {
                throw new NullValueException(
                        "render must return an instance of AbstractHtml, eg:- new Html(null);");
            }

            tagByWffId = abstractHtml.getSharedObject()
                    .initTagByWffId(ACCESS_OBJECT);

            addDataWffIdAttribute(abstractHtml);
            // attribute value change listener
            // should be added only after adding data-wff-id attribute
            addAttrValueChangeListener(abstractHtml);
            addChildTagAppendListener(abstractHtml);
            addChildTagRemoveListener(abstractHtml);
            addAttributeAddListener(abstractHtml);
            addAttributeRemoveListener(abstractHtml);
            addInnerHtmlAddListener(abstractHtml);

        } else {
            wffBMBytesQueue.clear();
        }

        final String webSocketUrl = webSocketUrl();
        if (webSocketUrl == null) {
            throw new NullValueException(
                    "webSocketUrl must return valid websocket url");
        }

        final String wsUrlWithInstanceId = webSocketUrl.indexOf("?") == -1
                ? webSocketUrl + "?wffInstanceId=" + getInstanceId()
                : webSocketUrl + "&wffInstanceId=" + getInstanceId();

        embedWffScriptIfRequired(abstractHtml, wsUrlWithInstanceId);
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

}
