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
package com.webfirmframework.wffweb.tag.html.attribute.event;

import java.util.logging.Logger;

import com.webfirmframework.wffweb.tag.html.attribute.core.AbstractAttribute;
import com.webfirmframework.wffweb.util.StringUtil;

/**
 * All event attributes will be extend by this class. It contains some common
 * features which all event attribute classes want.
 *
 * @author WFF
 * @since 2.0.0
 */
public abstract class AbstractEventAttribute extends AbstractAttribute
        implements EventAttribute {

    private static final long serialVersionUID = 1_0_0L;

    private static final Logger LOGGER = Logger
            .getLogger(AbstractEventAttribute.class.getName());

    private volatile boolean preventDefault;

    private volatile ServerAsyncMethod serverAsyncMethod;

    private volatile String jsFilterFunctionBody;

    private volatile String jsPreFunctionBody;

    private volatile String jsPostFunctionBody;

    private volatile Object serverSideData;

    // short name for wffServerMethods is wffSM

    // for better readability it's not capitalized
    // short name for invokeAsyncWithPreFilterFun is iawpff
    private static final String invokeAsyncWithPreFilterFun = "wffSM.iawpff";

    // short name for invokeAsyncWithPreFilterFunPrvntDflt is iawpffpd
    private static final String invokeAsyncWithPreFilterFunPrvntDflt = "wffSM.iawpffpd";

    // for better readability it's not capitalized
    // short name for invokeAsyncWithPreFun is iawpf
    private static final String invokeAsyncWithPreFun = "wffSM.iawpf";

    // short name for invokeAsyncWithPreFunPrvntDflt is iawpfpd
    private static final String invokeAsyncWithPreFunPrvntDflt = "wffSM.iawpfpd";

    // for better readability it's not capitalized
    // short name for invokeAsyncWithFilterFun is iawff
    private static final String invokeAsyncWithFilterFun = "wffSM.iawff";

    // short name for invokeAsyncWithFilterFunPrvntDflt is iawffpd
    private static final String invokeAsyncWithFilterFunPrvntDflt = "wffSM.iawffpd";

    // for better readability it's not capitalized
    // short name for invokeAsync is ia
    private static final String invokeAsync = "wffSM.ia";

    // short name for invokeAsyncPrvntDflt is iapd
    private static final String invokeAsyncPrvntDflt = "wffSM.iapd";

    {
        init();
    }

    protected AbstractEventAttribute() {
    }

    /**
     * @param value
     *                  the value for the attribute
     * @author WFF
     * @since 2.0.0
     */
    protected AbstractEventAttribute(final String attributeName,
            final String value) {
        super.setAttributeName(attributeName);
        super.setAttributeValue(value);
    }

    /**
     * @param attributeName
     * @param jsPreFunctionBody
     *                                 the body part javascript function
     *                                 (without function declaration). It must
     *                                 return true/false. This function will
     *                                 invoke at client side before
     *                                 {@code serverAsyncMethod}. If the
     *                                 jsPrefunction returns true then only
     *                                 {@code serverAsyncMethod} method will
     *                                 invoke (if it is implemented). It has
     *                                 implicit objects like {@code event} and
     *                                 {@code source} which gives the reference
     *                                 of the current tag. <br>
     *                                 Eg:-
     *
     *                                 <pre>
     *                                                                    if (source.type == 'button') {
     *                                                                       return true;
     *                                                                    }
     *                                                                    return false;
     *                                 </pre>
     *
     * @param serverAsyncMethod
     *                                 This method will invoke at server side
     *                                 with an argument {@code wffBMObject}. The
     *                                 {@code wffBMObject} is the
     *                                 representational javascript object
     *                                 returned by {@code jsFilterFunctionBody}.
     * @param jsFilterFunctionBody
     *                                 The body part of a javascript function
     *                                 (without function declaration). It can
     *                                 return a javascript object so that it
     *                                 will be available at server side in
     *                                 {@code serverAsyncMethod} as
     *                                 {@code wffBMObject} parameter. There are
     *                                 implicit objects {@code event} and
     *                                 {@code source} in the scope.<br>
     *                                 Eg:-
     *
     *                                 <pre>
     *                                                                    var bName = source.name;
     *                                                                    return {buttonName: bName, author:'wff', dateOfYear: 2014};
     *                                 </pre>
     *
     * @param jsPostFunctionBody
     *                                 The body part of a javascript function
     *                                 (without function declaration). The
     *                                 {@code wffBMObject} returned by
     *                                 {@code serverAsyncMethod} will be
     *                                 available as an implicit object
     *                                 {@code jsObject} in the scope. There are
     *                                 common implicit objects {@code event} and
     *                                 {@code source} in the scope.
     * @author WFF
     * @since 2.0.0
     */
    protected AbstractEventAttribute(final String attributeName,
            final String jsPreFunctionBody,
            final ServerAsyncMethod serverAsyncMethod,
            final String jsFilterFunctionBody,
            final String jsPostFunctionBody) {

        setAttributeName(attributeName);
        setServerAsyncMethod(jsPreFunctionBody, serverAsyncMethod,
                jsFilterFunctionBody, jsPostFunctionBody);
    }

    /**
     * @param attributeName
     * @param jsPreFunctionBody
     *                                 the body part javascript function
     *                                 (without function declaration). It must
     *                                 return true/false. This function will
     *                                 invoke at client side before
     *                                 {@code serverAsyncMethod}. If the
     *                                 jsPrefunction returns true then only
     *                                 {@code serverAsyncMethod} method will
     *                                 invoke (if it is implemented). It has
     *                                 implicit objects like {@code event} and
     *                                 {@code source} which gives the reference
     *                                 of the current tag. <br>
     *                                 Eg:-
     *
     *                                 <pre>
     *                                                                    if (source.type == 'button') {
     *                                                                       return true;
     *                                                                    }
     *                                                                    return false;
     *                                 </pre>
     *
     * @param serverAsyncMethod
     *                                 This method will invoke at server side
     *                                 with an argument {@code wffBMObject}. The
     *                                 {@code wffBMObject} is the
     *                                 representational javascript object
     *                                 returned by {@code jsFilterFunctionBody}.
     * @param jsFilterFunctionBody
     *                                 The body part of a javascript function
     *                                 (without function declaration). It can
     *                                 return a javascript object so that it
     *                                 will be available at server side in
     *                                 {@code serverAsyncMethod} as
     *                                 {@code wffBMObject} parameter. There are
     *                                 implicit objects {@code event} and
     *                                 {@code source} in the scope.<br>
     *                                 Eg:-
     *
     *                                 <pre>
     *                                                                    var bName = source.name;
     *                                                                    return {buttonName: bName, author:'wff', dateOfYear: 2014};
     *                                 </pre>
     *
     * @param jsPostFunctionBody
     *                                 The body part of a javascript function
     *                                 (without function declaration). The
     *                                 {@code wffBMObject} returned by
     *                                 {@code serverAsyncMethod} will be
     *                                 available as an implicit object
     *                                 {@code jsObject} in the scope. There are
     *                                 common implicit objects {@code event} and
     *                                 {@code source} in the scope.
     * @param serverSideData
     *                                 this data will be available in the Event
     *                                 object of ServerAsyncMethod.asyncMethod
     *                                 method.
     * @author WFF
     * @since 3.0.2
     */
    protected AbstractEventAttribute(final String attributeName,
            final String jsPreFunctionBody,
            final ServerAsyncMethod serverAsyncMethod,
            final String jsFilterFunctionBody, final String jsPostFunctionBody,
            final Object serverSideData) {
        setAttributeName(attributeName);
        setServerAsyncMethod(jsPreFunctionBody, serverAsyncMethod,
                jsFilterFunctionBody, jsPostFunctionBody, serverSideData);
    }

    /**
     * invokes only once per object
     *
     * @author WFF
     * @since 2.0.0
     */
    protected void init() {
        // to override and use this method
    }

    /**
     * sets the value for this attribute
     *
     * @param value
     *                  the value for the attribute.
     * @author WFF
     * @since 2.0.0
     */
    public void setValue(final String value) {
        super.setAttributeValue(value);
    }

    /**
     * gets the value of this attribute
     *
     * @return the value of the attribute
     * @author WFF
     * @since 2.0.0
     */
    public String getValue() {
        return super.getAttributeValue();
    }

    /**
     * @param serverAsyncMethod
     *                              the {@code ServerAsyncMethod} object to set.
     * @author WFF
     */
    public void setServerAsyncMethod(
            final ServerAsyncMethod serverAsyncMethod) {
        if (serverAsyncMethod != null) {

            setServerAsyncMethod(jsPreFunctionBody, serverAsyncMethod,
                    jsFilterFunctionBody, jsPostFunctionBody);
        }
    }

    private static String getPreparedJsFunctionBody(
            final String jsFunctionBody) {

        final String functionBody = StringUtil.strip(jsFunctionBody);
        final StringBuilder builder = new StringBuilder(26);

        builder.append("function(event, source){").append(functionBody);
        if (functionBody.charAt(functionBody.length() - 1) != ';') {
            builder.append(';');
        }

        return builder.append('}').toString();
    }

    /**
     * @param jsPreFunctionBody
     *                                 the body part javascript function
     *                                 (without function declaration). It must
     *                                 return true/false. This function will
     *                                 invoke at client side before
     *                                 {@code serverAsyncMethod}. If the
     *                                 jsPrefunction returns true then only
     *                                 {@code serverAsyncMethod} method will
     *                                 invoke (if it is implemented). It has
     *                                 implicit objects like {@code event} and
     *                                 {@code source} which gives the reference
     *                                 of the current tag. <br>
     *                                 Eg:-
     *
     *                                 <pre>
     *                                                                    if (source.type == 'button') {
     *                                                                       return true;
     *                                                                    }
     *                                                                    return false;
     *                                 </pre>
     *
     * @param serverAsyncMethod
     *                                 This method will invoke at server side
     *                                 with an argument {@code wffBMObject}. The
     *                                 {@code wffBMObject} is the
     *                                 representational javascript object
     *                                 returned by {@code jsFilterFunctionBody}.
     * @param jsFilterFunctionBody
     *                                 The body part of a javascript function
     *                                 (without function declaration). It can
     *                                 return a javascript object so that it
     *                                 will be available at server side in
     *                                 {@code serverAsyncMethod} as
     *                                 {@code wffBMObject} parameter. There are
     *                                 implicit objects {@code event} and
     *                                 {@code source} in the scope.<br>
     *                                 Eg:-
     *
     *                                 <pre>
     *                                                                    var bName = source.name;
     *                                                                    return {buttonName: bName, author:'wff', dateOfYear: 2014};
     *                                 </pre>
     *
     * @param jsPostFunctionBody
     *                                 The body part of a javascript function
     *                                 (without function declaration). The
     *                                 {@code wffBMObject} returned by
     *                                 {@code serverAsyncMethod} will be
     *                                 available as an implicit object
     *                                 {@code jsObject} in the scope. There are
     *                                 common implicit objects {@code event} and
     *                                 {@code source} in the scope.
     * @author WFF
     * @since 2.0.0
     */
    public void setServerAsyncMethod(final String jsPreFunctionBody,
            final ServerAsyncMethod serverAsyncMethod,
            final String jsFilterFunctionBody,
            final String jsPostFunctionBody) {

        setServerAsyncMethod(jsPreFunctionBody, serverAsyncMethod,
                jsFilterFunctionBody, jsPostFunctionBody, serverSideData);
    }

    /**
     * @param jsPreFunctionBody
     *                                 the body part javascript function
     *                                 (without function declaration). It must
     *                                 return true/false. This function will
     *                                 invoke at client side before
     *                                 {@code serverAsyncMethod}. If the
     *                                 jsPrefunction returns true then only
     *                                 {@code serverAsyncMethod} method will
     *                                 invoke (if it is implemented). It has
     *                                 implicit objects like {@code event} and
     *                                 {@code source} which gives the reference
     *                                 of the current tag. <br>
     *                                 Eg:-
     *
     *                                 <pre>
     *                                                                    if (source.type == 'button') {
     *                                                                       return true;
     *                                                                    }
     *                                                                    return false;
     *                                 </pre>
     *
     * @param serverAsyncMethod
     *                                 This method will invoke at server side
     *                                 with an argument {@code wffBMObject}. The
     *                                 {@code wffBMObject} is the
     *                                 representational javascript object
     *                                 returned by {@code jsFilterFunctionBody}.
     * @param jsFilterFunctionBody
     *                                 The body part of a javascript function
     *                                 (without function declaration). It can
     *                                 return a javascript object so that it
     *                                 will be available at server side in
     *                                 {@code serverAsyncMethod} as
     *                                 {@code wffBMObject} parameter. There are
     *                                 implicit objects {@code event} and
     *                                 {@code source} in the scope.<br>
     *                                 Eg:-
     *
     *                                 <pre>
     *                                                                    var bName = source.name;
     *                                                                    return {buttonName: bName, author:'wff', dateOfYear: 2014};
     *                                 </pre>
     *
     * @param jsPostFunctionBody
     *                                 The body part of a javascript function
     *                                 (without function declaration). The
     *                                 {@code wffBMObject} returned by
     *                                 {@code serverAsyncMethod} will be
     *                                 available as an implicit object
     *                                 {@code jsObject} in the scope. There are
     *                                 common implicit objects {@code event} and
     *                                 {@code source} in the scope.
     * @param serverSideData
     *                                 this data will be available in the Event
     *                                 object of ServerAsyncMethod.asyncMethod
     *                                 method.
     * @author WFF
     * @since 3.0.2
     */
    public void setServerAsyncMethod(final String jsPreFunctionBody,
            final ServerAsyncMethod serverAsyncMethod,
            final String jsFilterFunctionBody, final String jsPostFunctionBody,
            final Object serverSideData) {
        setServerAsyncMethod(preventDefault, jsPreFunctionBody,
                serverAsyncMethod, jsFilterFunctionBody, jsPostFunctionBody,
                serverSideData);
    }

    /**
     * @param preventDefault
     *                                 true to call event.preventDefault(); on
     *                                 event
     * @param jsPreFunctionBody
     *                                 the body part javascript function
     *                                 (without function declaration). It must
     *                                 return true/false. This function will
     *                                 invoke at client side before
     *                                 {@code serverAsyncMethod}. If the
     *                                 jsPrefunction returns true then only
     *                                 {@code serverAsyncMethod} method will
     *                                 invoke (if it is implemented). It has
     *                                 implicit objects like {@code event} and
     *                                 {@code source} which gives the reference
     *                                 of the current tag. <br>
     *                                 Eg:-
     *
     *                                 <pre>
     *                                                                    if (source.type == 'button') {
     *                                                                       return true;
     *                                                                    }
     *                                                                    return false;
     *                                 </pre>
     *
     * @param serverAsyncMethod
     *                                 This method will invoke at server side
     *                                 with an argument {@code wffBMObject}. The
     *                                 {@code wffBMObject} is the
     *                                 representational javascript object
     *                                 returned by {@code jsFilterFunctionBody}.
     * @param jsFilterFunctionBody
     *                                 The body part of a javascript function
     *                                 (without function declaration). It can
     *                                 return a javascript object so that it
     *                                 will be available at server side in
     *                                 {@code serverAsyncMethod} as
     *                                 {@code wffBMObject} parameter. There are
     *                                 implicit objects {@code event} and
     *                                 {@code source} in the scope.<br>
     *                                 Eg:-
     *
     *                                 <pre>
     *                                                                    var bName = source.name;
     *                                                                    return {buttonName: bName, author:'wff', dateOfYear: 2014};
     *                                 </pre>
     *
     * @param jsPostFunctionBody
     *                                 The body part of a javascript function
     *                                 (without function declaration). The
     *                                 {@code wffBMObject} returned by
     *                                 {@code serverAsyncMethod} will be
     *                                 available as an implicit object
     *                                 {@code jsObject} in the scope. There are
     *                                 common implicit objects {@code event} and
     *                                 {@code source} in the scope.
     * @param serverSideData
     *                                 this data will be available in the Event
     *                                 object of ServerAsyncMethod.asyncMethod
     *                                 method.
     * @author WFF
     * @since 3.0.15
     */
    protected void setServerAsyncMethod(final boolean preventDefault,
            final String jsPreFunctionBody,
            final ServerAsyncMethod serverAsyncMethod,
            final String jsFilterFunctionBody, final String jsPostFunctionBody,
            final Object serverSideData) {

        this.serverSideData = serverSideData;

        if (serverAsyncMethod != null) {

            if (jsPreFunctionBody != null && jsPostFunctionBody != null
                    && jsFilterFunctionBody != null) {

                super.setAttributeValue(new StringBuilder(
                        preventDefault ? invokeAsyncWithPreFilterFunPrvntDflt
                                : invokeAsyncWithPreFilterFun)
                                        .append("(event,this,'")
                                        .append(getAttributeName()).append("',")
                                        .append(getPreparedJsFunctionBody(
                                                jsPreFunctionBody))
                                        .append(',')
                                        .append(getPreparedJsFunctionBody(
                                                jsFilterFunctionBody))
                                        .append(')').toString());

            } else if (jsPreFunctionBody != null
                    && jsPostFunctionBody != null) {

                super.setAttributeValue(new StringBuilder()
                        .append(preventDefault ? invokeAsyncWithPreFunPrvntDflt
                                : invokeAsyncWithPreFun)
                        .append("(event,this,'").append(getAttributeName())
                        .append("',")
                        .append(getPreparedJsFunctionBody(jsPreFunctionBody))
                        .append(')').toString());

            } else if (jsPreFunctionBody != null
                    && jsFilterFunctionBody != null) {

                super.setAttributeValue(new StringBuilder(
                        preventDefault ? invokeAsyncWithPreFilterFunPrvntDflt
                                : invokeAsyncWithPreFilterFun)
                                        .append("(event,this,'")
                                        .append(getAttributeName()).append("',")
                                        .append(getPreparedJsFunctionBody(
                                                jsPreFunctionBody))
                                        .append(',')
                                        .append(getPreparedJsFunctionBody(
                                                jsFilterFunctionBody))
                                        .append(')').toString());

            } else if (jsPostFunctionBody != null
                    && jsFilterFunctionBody != null) {

                super.setAttributeValue(new StringBuilder(
                        preventDefault ? invokeAsyncWithFilterFunPrvntDflt
                                : invokeAsyncWithFilterFun)
                                        .append("(event,this,'")
                                        .append(getAttributeName()).append("',")
                                        .append(getPreparedJsFunctionBody(
                                                jsFilterFunctionBody))
                                        .append(')').toString());

            } else if (jsPreFunctionBody != null) {

                super.setAttributeValue(new StringBuilder(
                        preventDefault ? invokeAsyncWithPreFunPrvntDflt
                                : invokeAsyncWithPreFun).append("(event,this,'")
                                        .append(getAttributeName()).append("',")
                                        .append(getPreparedJsFunctionBody(
                                                jsPreFunctionBody))
                                        .append(')').toString());

            } else if (jsFilterFunctionBody != null) {

                super.setAttributeValue(new StringBuilder(
                        preventDefault ? invokeAsyncWithFilterFunPrvntDflt
                                : invokeAsyncWithFilterFun)
                                        .append("(event,this,'")
                                        .append(getAttributeName()).append("',")
                                        .append(getPreparedJsFunctionBody(
                                                jsFilterFunctionBody))
                                        .append(')').toString());
            } else if (jsPostFunctionBody != null) {
                super.setAttributeValue(new StringBuilder(
                        preventDefault ? invokeAsyncPrvntDflt : invokeAsync)
                                .append("(event,this,'")
                                .append(getAttributeName()).append("')")
                                .toString());
            } else {

                super.setAttributeValue(new StringBuilder(
                        preventDefault ? invokeAsyncPrvntDflt : invokeAsync)
                                .append("(event,this,'")
                                .append(getAttributeName()).append("')")
                                .toString());
            }
            this.preventDefault = preventDefault;
            this.jsPreFunctionBody = jsPreFunctionBody;
            this.jsFilterFunctionBody = jsFilterFunctionBody;
            this.jsPostFunctionBody = jsPostFunctionBody;
            this.serverAsyncMethod = serverAsyncMethod;
        } else {
            LOGGER.warning(
                    "serverAsyncMethod is null so jsPreFunctionBody, jsFilterFunctionBody and jsPostFunctionBody are not also set.They are valid only if serverAsyncMethod is NOT null.");
        }
    }

    /**
     * This is applicable for some special attributes like OnSubmit.
     *
     * @return true or false. true means to call event.preventDefault() on event
     *         otherwise false.
     * @since 3.0.15
     */
    protected boolean isPreventDefault() {
        return preventDefault;
    }

    @Override
    public ServerAsyncMethod getServerAsyncMethod() {
        return serverAsyncMethod;
    }

    @Override
    public String getJsPostFunctionBody() {
        return jsPostFunctionBody;
    }

    /**
     * @return
     * @author WFF
     * @since 2.1.9
     */
    public String getJsFilterFunctionBody() {
        return jsFilterFunctionBody;
    }

    /**
     * @return
     * @author WFF
     * @since 2.1.9
     */
    public String getJsPreFunctionBody() {
        return jsPreFunctionBody;
    }

    /**
     * true to call event.preventDefault(); on event. It will set only if there
     * is {@code ServerAsyncMethod}. This is applicable for some special
     * attributes like OnSubmit.
     *
     * @param preventDefault
     *                           true to call event.preventDefault(); on event
     *                           otherwise false.
     * @since 3.0.15
     */
    protected void setPreventDefault(final boolean preventDefault) {
        setServerAsyncMethod(preventDefault, jsPreFunctionBody,
                serverAsyncMethod, jsFilterFunctionBody, jsPostFunctionBody,
                serverSideData);
    }

    /**
     * Sets the post function body JavaScript.
     *
     * @param jsPostFunctionBody
     *                               the post function body JavaScript.
     * @author WFF
     */
    public void setJsPostFunctionBody(final String jsPostFunctionBody) {
        setServerAsyncMethod(jsPreFunctionBody, serverAsyncMethod,
                jsFilterFunctionBody, jsPostFunctionBody);
    }

    /**
     * Sets the pre function body JavaScript.
     *
     * @param jsPreFunctionBody
     * @author WFF
     * @since 2.1.9
     */
    public void setJsPreFunctionBody(final String jsPreFunctionBody) {
        setServerAsyncMethod(jsPreFunctionBody, serverAsyncMethod,
                jsFilterFunctionBody, jsPostFunctionBody);
    }

    /**
     * Sets the filter function body JavaScript.
     *
     * @param jsFilterFunctionBody
     * @author WFF
     * @since 2.1.9
     */
    public void setJsFilterFunctionBody(final String jsFilterFunctionBody) {
        setServerAsyncMethod(jsPreFunctionBody, serverAsyncMethod,
                jsFilterFunctionBody, jsPostFunctionBody);
    }

    /**
     * @return the serverSideData
     * @since 3.0.2
     */
    @Override
    public Object getServerSideData() {
        return serverSideData;
    }

    /**
     * @param serverSideData
     *                           the serverSideData to set
     * @since 3.0.2
     */
    public void setServerSideData(final Object serverSideData) {
        this.serverSideData = serverSideData;
    }

}
