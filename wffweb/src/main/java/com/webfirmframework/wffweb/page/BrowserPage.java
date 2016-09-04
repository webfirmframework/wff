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
package com.webfirmframework.wffweb.page;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.UUID;
import java.util.logging.Logger;

import com.webfirmframework.wffweb.NullValueException;
import com.webfirmframework.wffweb.page.js.WffJsFile;
import com.webfirmframework.wffweb.tag.html.AbstractHtml;
import com.webfirmframework.wffweb.tag.html.TagNameConstants;
import com.webfirmframework.wffweb.tag.html.attribute.Type;
import com.webfirmframework.wffweb.tag.html.attribute.core.AbstractAttribute;
import com.webfirmframework.wffweb.tag.html.attribute.event.mouse.EventAttribute;
import com.webfirmframework.wffweb.tag.html.attribute.event.mouse.ServerAsyncMethod;
import com.webfirmframework.wffweb.tag.html.attribute.listener.AttributeValueChangeListener;
import com.webfirmframework.wffweb.tag.html.attributewff.CustomAttribute;
import com.webfirmframework.wffweb.tag.html.programming.Script;
import com.webfirmframework.wffweb.tag.htmlwff.NoTag;
import com.webfirmframework.wffweb.util.WffBinaryMessageUtil;
import com.webfirmframework.wffweb.util.data.NameValue;
import com.webfirmframework.wffweb.wffbm.data.WffBMObject;

/**
 * @author WFF
 * @since 1.2.0
 */
public abstract class BrowserPage implements Serializable {

    // if this class' is refactored then SecurityClassConstants should be
    // updated.

    private static final long serialVersionUID = 1L;

    public static final Logger LOGGER = Logger
            .getLogger(BrowserPage.class.getName());

    private String instanceId;

    private AttributeValueChangeListener valueChangeListener;

    private Map<String, AbstractHtml> tagByWffId;

    private AbstractHtml abstractHtml;

    private WebSocketPushListener wsListener;

    // for security purpose, the class name should not be modified
    private static final class Security implements Serializable {

        private static final long serialVersionUID = 1L;

        private Security() {
        }
    }

    private static final Security ACCESS_OBJECT;

    static {
        ACCESS_OBJECT = new Security();
    }

    public abstract String webSocketUrl();

    {
        instanceId = UUID.randomUUID().toString();
    }

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

    String getNewDataWffId() {
        return abstractHtml.getSharedObject().getNewDataWffId(ACCESS_OBJECT);
    }

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
     * @since 1.2.0
     * @author WFF
     * @throws UnsupportedEncodingException
     */
    private void executeWffBMTask(final byte[] message)
            throws UnsupportedEncodingException {

        final List<NameValue> nameValues = WffBinaryMessageUtil.VERSION_1
                .parse(message);

        final NameValue task = nameValues.get(0);
        final byte taskValue = task.getValues()[0][0];

        // T stands for Task
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

                            final ServerAsyncMethod.Event event = new ServerAsyncMethod.Event();

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

                                wsListener.push(WffBinaryMessageUtil.VERSION_1
                                        .getWffBinaryMessageBytes(
                                                invokePostFunTask, nameValue));
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
                    LOGGER.severe("No tag found for wffTagId " + wffTagId);
                }

            }

        }

    }

    private void addValueChangeListenerInAttribute(
            final Collection<AbstractAttribute> attribs) {

        for (final AbstractAttribute attr : attribs) {
            if (attr == null
                    || attr.getValueChangeListener(ACCESS_OBJECT) != null) {
                continue;
            }
            attr.setValueChangeListener(valueChangeListener, ACCESS_OBJECT);
        }
    }

    private void addDataWffIdAndAttrListener(final AbstractHtml abstractHtml) {

        if (tagByWffId == null) {
            tagByWffId = new HashMap<String, AbstractHtml>();
        }

        if (valueChangeListener == null) {
            valueChangeListener = new AttributeValueChangeListenerImpl(
                    BrowserPage.this);
        }

        final Stack<Set<AbstractHtml>> childrenStack = new Stack<Set<AbstractHtml>>();
        childrenStack.push(
                new LinkedHashSet<AbstractHtml>(Arrays.asList(abstractHtml)));

        while (childrenStack.size() > 0) {

            final Set<AbstractHtml> children = childrenStack.pop();

            for (final AbstractHtml child : children) {

                // adding value change listener for all attributes except
                // data-wff-id attribute
                final Collection<AbstractAttribute> attributes = child
                        .getAttributes();

                if (attributes != null) {
                    addValueChangeListenerInAttribute(attributes);
                }

                final String wffId = getNewDataWffId();
                child.addAttributes(ACCESS_OBJECT, false,
                        new CustomAttribute("data-wff-id", wffId));
                tagByWffId.put(wffId, child);

                final Set<AbstractHtml> subChildren = child
                        .getChildren(ACCESS_OBJECT);

                if (subChildren != null && subChildren.size() > 0) {
                    childrenStack.push(subChildren);
                }

            }

        }

    }

    private void embedScriptTag(final AbstractHtml abstractHtml,
            final String wsUrlWithInstanceId) {

        final Stack<Set<AbstractHtml>> childrenStack = new Stack<Set<AbstractHtml>>();
        childrenStack.push(
                new LinkedHashSet<AbstractHtml>(Arrays.asList(abstractHtml)));

        boolean bodyTagMissing = true;

        outerLoop: while (childrenStack.size() > 0) {

            final Set<AbstractHtml> children = childrenStack.pop();

            for (final AbstractHtml child : children) {

                if (TagNameConstants.BODY.equals(child.getTagName())) {

                    bodyTagMissing = false;

                    final Script script = new Script(child,
                            new Type("text/javascript"));

                    new NoTag(script, WffJsFile
                            .getAllOptimizedContent(wsUrlWithInstanceId));

                    // new Script(child, new Type("text/javascript"),
                    // new Src("js/wffGlobal.js"));
                    //
                    // new Script(child, new Type("text/javascript"),
                    // new Src("js/wffTagUtil.js"));
                    // new Script(child, new Type("text/javascript"),
                    // new Src("js/wffBMCRUIDUtil.js"));
                    // new Script(child, new Type("text/javascript"),
                    // new Src("js/wffClientCRUDUtil.js"));
                    // new Script(child, new Type("text/javascript"),
                    // new Src("js/wffWS.js"));
                    // new Script(child, new Type("text/javascript"),
                    // new Src("js/wffServerMethods.js"));

                    // <script type="text/javascript"
                    // src="js/wffTagUtil.js"></script>
                    // <script type="text/javascript"
                    // src="js/wffBMCRUIDUtil.js"></script>
                    // <script type="text/javascript"
                    // src="js/wffClientCRUDUtil.js"></script>
                    // <script type="text/javascript"
                    // src="js/wffWS.js"></script>

                    // new Script(child, new Type("text/javascript")) {
                    //
                    // private static final long serialVersionUID = 1L;
                    //
                    // {
                    // new NoTag(this, WffClientEventsContent
                    // .getContent(wsUrlWithInstanceId));
                    // }
                    // };

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
            final Script script = new Script(abstractHtml,
                    new Type("text/javascript"));
            new NoTag(script,
                    WffJsFile.getAllOptimizedContent(wsUrlWithInstanceId));
        }

    }

    private void addChildTagAppendListener(final AbstractHtml abstractHtml) {

        abstractHtml.getSharedObject().setChildTagAppendListener(
                new ChildTagAppendListenerImpl(this, ACCESS_OBJECT),
                ACCESS_OBJECT);
    }

    private void addChildTagRemoveListener(final AbstractHtml abstractHtml) {

        abstractHtml.getSharedObject().setChildTagRemoveListener(
                new ChildTagRemoveListenerImpl(this), ACCESS_OBJECT);
    }

    private void addAttributeAddListener(final AbstractHtml abstractHtml) {
        abstractHtml.getSharedObject().setAttributeAddListener(
                new AttributeAddListenerImpl(this), ACCESS_OBJECT);
    }

    private void addAttributeRemoveListener(final AbstractHtml abstractHtml) {
        abstractHtml.getSharedObject().setAttributeRemoveListener(
                new AttributeRemoveListenerImpl(this), ACCESS_OBJECT);
    }

    private void addInnerHtmlAddListener(final AbstractHtml abstractHtml) {
        abstractHtml.getSharedObject().setInnerHtmlAddListener(
                new InnerHtmlAddListenerImpl(this), ACCESS_OBJECT);
    }

    public final String toHtmlString() {
        initAbstractHtml();
        return abstractHtml.toHtmlString(true);
    }

    public final String toHtmlString(final String charset) {
        initAbstractHtml();
        return abstractHtml.toHtmlString(true, charset);
    }

    public final void toOutputStream(final OutputStream os) throws IOException {
        initAbstractHtml();
        abstractHtml.toOutputStream(os, true);
    }

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

            final String webSocketUrl = webSocketUrl();
            if (webSocketUrl == null) {
                throw new NullValueException(
                        "webSocketUrl must return valid websocket url");
            }

            final String wsUrlWithInstanceId = webSocketUrl.indexOf("?") == -1
                    ? webSocketUrl + "?wffInstanceId=" + getInstanceId()
                    : webSocketUrl + "&wffInstanceId=" + getInstanceId();

            embedScriptTag(abstractHtml, wsUrlWithInstanceId);
            addDataWffIdAndAttrListener(abstractHtml);
            addChildTagAppendListener(abstractHtml);
            addChildTagRemoveListener(abstractHtml);
            addAttributeAddListener(abstractHtml);
            addAttributeRemoveListener(abstractHtml);
            addInnerHtmlAddListener(abstractHtml);
        }
    }

    /**
     * @return a unique id for this instance
     * @since 1.2.0
     * @author WFF
     */
    public String getInstanceId() {
        return instanceId;
    }

}
