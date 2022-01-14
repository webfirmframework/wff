/*
 * Copyright 2014-2021 Web Firm Framework
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
import com.webfirmframework.wffweb.tag.html.attribute.core.PreIndexedAttributeName;
import com.webfirmframework.wffweb.util.StringUtil;

/**
 * All event attributes will be extend by this class. It contains some common
 * features which all event attribute classes want.
 *
 * @author WFF
 * @since 2.0.0
 */
public abstract class AbstractEventAttribute extends AbstractAttribute implements EventAttribute {

    private static final long serialVersionUID = 1_0_0L;

    private static final Logger LOGGER = Logger.getLogger(AbstractEventAttribute.class.getName());

    private volatile boolean preventDefault;

    private volatile ServerMethod serverMethod;

    private volatile String jsFilterFunctionBody;

    private volatile String jsPreFunctionBody;

    private volatile String jsPostFunctionBody;

    private volatile Object serverSideData;

    /**
     * not required to be atomic because null also works
     */
    private PreIndexedAttributeName preIndexedAttrName;

    // short name for wffServerMethods is wffSM

    // for better readability it's not capitalized
    // short name for invokeAsync is ia
    private static final String invokeAsync = "wffSM.a";

    // short name for invokeAsync with PD as true is iapd
    private static final String invokeAsyncPDTrue = "wffSM.b";

    // for better readability it's not capitalized
    // short name for invokeAsyncWithFilterFun is iawff
    private static final String invokeAsyncWithFilterFun = "wffSM.c";

    // short name for invokeAsyncWithFilterFun with PD as true is iawffpd
    private static final String invokeAsyncWithFilterFunPDTrue = "wffSM.d";

    // for better readability it's not capitalized
    // short name for invokeAsyncWithPreFun is iawpf
    private static final String invokeAsyncWithPreFun = "wffSM.e";

    // short name for invokeAsyncWithPreFun with PD as true is iawpfpd
    private static final String invokeAsyncWithPreFunPDTrue = "wffSM.f";

    // for better readability it's not capitalized
    // short name for invokeAsyncWithPreFilterFun is iawpff
    private static final String invokeAsyncWithPreFilterFun = "wffSM.g";

    // short name for invokeAsyncWithPreFilterFun with PD as true is iawpffpd
    private static final String invokeAsyncWithPreFilterFunPDTrue = "wffSM.h";

    {
        init();
    }

    protected AbstractEventAttribute() {
    }

    /**
     * @param value the value for the attribute
     * @author WFF
     * @since 2.0.0
     */
    protected AbstractEventAttribute(final String attributeName, final String value) {
        // NB: always trim attributeName to avoid starting with 0 value byte
        // because the first byte value is zero if sending its index bytes from
        // client.
        super.setAttributeName(attributeName != null ? attributeName.trim() : attributeName);
        super.setAttributeValue(value);
    }

    /**
     * @param attributeName        the name of the attribute
     * @param jsPreFunctionBody    It is the body part of JavaScript function
     *                             (without function declaration). It must return
     *                             true/false. This function will invoke at client
     *                             side before {@code serverMethod}. If the
     *                             jsPrefunction returns true then only
     *                             {@code serverMethod} method will invoke (if
     *                             it is implemented). It has implicit objects in
     *                             its scope. They are {@code event}, {@code source}
     *                             which gives the reference of the current tag and
     *                             {@code action}. The {@code action} implicit
     *                             object has a function named {@code perform()}
     *                             which can be used to invoke
     *                             {@code jsFilterFunctionBody} and
     *                             {@code serverMethod} (it works just like
     *                             returning true in the {@code jsPreFunctionBody}).
     *                             If the {@code action.perform()} is called inside
     *                             {@code jsPreFunctionBody} then returning true in
     *                             it has no effect. The {@code action} implicit
     *                             object is only available since 3.0.15. <br>
     *                             Eg:-
     *
     *                             <pre>
     *                              if (source.type == 'button') {
     *                                  return true;
     *                              }
     *                              return false;
     *                             </pre>
     *
     *                             <br>
     *                             NB: calling {@code action.perform()} inside
     *                             {@code jsFilterFunctionBody} and
     *                             {@code jsPostFunctionBody} of the same event
     *                             attribute will be harmful as it will make an
     *                             infinite recursive call.
     *
     * @param serverMethod    This method will invoke at server side with an
     *                             argument {@code wffBMObject}. The
     *                             {@code wffBMObject} is the representational
     *                             javascript object returned by
     *                             {@code jsFilterFunctionBody}.
     * @param jsFilterFunctionBody The body part of a javascript function (without
     *                             function declaration). It can return a javascript
     *                             object so that it will be available at server
     *                             side in {@code serverMethod} as
     *                             {@code wffBMObject} parameter. There are implicit
     *                             objects {@code event} and {@code source} in the
     *                             scope.<br>
     *                             Eg:-
     *
     *                             <pre>
     *                                                                                                var bName = source.name;
     *                                                                                                return {buttonName: bName, author:'wff', dateOfYear: 2014};
     *                             </pre>
     *
     * @param jsPostFunctionBody   The body part of a javascript function (without
     *                             function declaration). The {@code wffBMObject}
     *                             returned by {@code serverMethod} will be
     *                             available as an implicit object {@code jsObject}
     *                             in the scope. There are common implicit objects
     *                             {@code event} and {@code source} in the scope.
     * @author WFF
     * @since 2.0.0
     */
    protected AbstractEventAttribute(final String attributeName, final String jsPreFunctionBody,
            final ServerMethod serverMethod, final String jsFilterFunctionBody,
            final String jsPostFunctionBody) {
        // NB: always trim attributeName to avoid starting with 0 value byte
        // because the first byte value is zero if sending its index bytes from
        // client.
        super.setAttributeName(attributeName != null ? attributeName.trim() : attributeName);
        setServerMethod(jsPreFunctionBody, serverMethod, jsFilterFunctionBody, jsPostFunctionBody);
    }

    /**
     * @param attributeName        the name of the attribute
     * @param jsPreFunctionBody    It is the body part of JavaScript function
     *                             (without function declaration). It must return
     *                             true/false. This function will invoke at client
     *                             side before {@code serverMethod}. If the
     *                             jsPrefunction returns true then only
     *                             {@code serverMethod} method will invoke (if
     *                             it is implemented). It has implicit objects in
     *                             its scope. They are {@code event}, {@code source}
     *                             which gives the reference of the current tag and
     *                             {@code action}. The {@code action} implicit
     *                             object has a function named {@code perform()}
     *                             which can be used to invoke
     *                             {@code jsFilterFunctionBody} and
     *                             {@code serverMethod} (it works just like
     *                             returning true in the {@code jsPreFunctionBody}).
     *                             If the {@code action.perform()} is called inside
     *                             {@code jsPreFunctionBody} then returning true in
     *                             it has no effect. The {@code action} implicit
     *                             object is only available since 3.0.15. <br>
     *                             Eg:-
     *
     *                             <pre>
     *                              if (source.type == 'button') {
     *                                  return true;
     *                              }
     *                              return false;
     *                             </pre>
     *
     *                             <br>
     *                             NB: calling {@code action.perform()} inside
     *                             {@code jsFilterFunctionBody} and
     *                             {@code jsPostFunctionBody} of the same event
     *                             attribute will be harmful as it will make an
     *                             infinite recursive call.
     *
     * @param serverMethod    This method will invoke at server side with an
     *                             argument {@code wffBMObject}. The
     *                             {@code wffBMObject} is the representational
     *                             javascript object returned by
     *                             {@code jsFilterFunctionBody}.
     * @param jsFilterFunctionBody The body part of a javascript function (without
     *                             function declaration). It can return a javascript
     *                             object so that it will be available at server
     *                             side in {@code serverMethod} as
     *                             {@code wffBMObject} parameter. There are implicit
     *                             objects {@code event} and {@code source} in the
     *                             scope.<br>
     *                             Eg:-
     *
     *                             <pre>
     *                                                                                                var bName = source.name;
     *                                                                                                return {buttonName: bName, author:'wff', dateOfYear: 2014};
     *                             </pre>
     *
     * @param jsPostFunctionBody   The body part of a javascript function (without
     *                             function declaration). The {@code wffBMObject}
     *                             returned by {@code serverMethod} will be
     *                             available as an implicit object {@code jsObject}
     *                             in the scope. There are common implicit objects
     *                             {@code event} and {@code source} in the scope.
     * @param serverSideData       this data will be available in the Event object
     *                             of ServerMethod.invoke method.
     * @author WFF
     * @since 3.0.2
     */
    protected AbstractEventAttribute(final String attributeName, final String jsPreFunctionBody,
            final ServerMethod serverMethod, final String jsFilterFunctionBody,
            final String jsPostFunctionBody, final Object serverSideData) {
        // NB: always trim attributeName to avoid starting with 0 value byte
        // because the first byte value is zero if sending its index bytes from
        // client.
        super.setAttributeName(attributeName != null ? attributeName.trim() : attributeName);
        setServerMethod(jsPreFunctionBody, serverMethod, jsFilterFunctionBody, jsPostFunctionBody,
                serverSideData);
    }

    /**
     * @param attributeName        the name of the attribute
     *
     * @param preventDefault       true to call event.preventDefault(); on event
     * @param jsPreFunctionBody    It is the body part of JavaScript function
     *                             (without function declaration). It must return
     *                             true/false. This function will invoke at client
     *                             side before {@code serverMethod}. If the
     *                             jsPrefunction returns true then only
     *                             {@code serverMethod} method will invoke (if
     *                             it is implemented). It has implicit objects in
     *                             its scope. They are {@code event}, {@code source}
     *                             which gives the reference of the current tag and
     *                             {@code action}. The {@code action} implicit
     *                             object has a function named {@code perform()}
     *                             which can be used to invoke
     *                             {@code jsFilterFunctionBody} and
     *                             {@code serverMethod} (it works just like
     *                             returning true in the {@code jsPreFunctionBody}).
     *                             If the {@code action.perform()} is called inside
     *                             {@code jsPreFunctionBody} then returning true in
     *                             it has no effect. The {@code action} implicit
     *                             object is only available since 3.0.15. <br>
     *                             Eg:-
     *
     *                             <pre>
     *                              if (source.type == 'button') {
     *                                  return true;
     *                              }
     *                              return false;
     *                             </pre>
     *
     *                             <br>
     *                             NB: calling {@code action.perform()} inside
     *                             {@code jsFilterFunctionBody} and
     *                             {@code jsPostFunctionBody} of the same event
     *                             attribute will be harmful as it will make an
     *                             infinite recursive call.
     *
     * @param serverMethod    This method will invoke at server side with an
     *                             argument {@code wffBMObject}. The
     *                             {@code wffBMObject} is the representational
     *                             javascript object returned by
     *                             {@code jsFilterFunctionBody}.
     * @param jsFilterFunctionBody The body part of a javascript function (without
     *                             function declaration). It can return a javascript
     *                             object so that it will be available at server
     *                             side in {@code serverMethod} as
     *                             {@code wffBMObject} parameter. There are implicit
     *                             objects {@code event} and {@code source} in the
     *                             scope.<br>
     *                             Eg:-
     *
     *                             <pre>
     *                                                                                                var bName = source.name;
     *                                                                                                return {buttonName: bName, author:'wff', dateOfYear: 2014};
     *                             </pre>
     *
     * @param jsPostFunctionBody   The body part of a javascript function (without
     *                             function declaration). The {@code wffBMObject}
     *                             returned by {@code serverMethod} will be
     *                             available as an implicit object {@code jsObject}
     *                             in the scope. There are common implicit objects
     *                             {@code event} and {@code source} in the scope.
     * @param serverSideData       this data will be available in the Event object
     *                             of ServerMethod.invoke method.
     * @author WFF
     * @since 3.0.15
     */
    protected AbstractEventAttribute(final String attributeName, final boolean preventDefault,
            final String jsPreFunctionBody, final ServerMethod serverMethod,
            final String jsFilterFunctionBody, final String jsPostFunctionBody, final Object serverSideData) {
        // NB: always trim attributeName to avoid starting with 0 value byte
        // because the first byte value is zero if sending its index bytes from
        // client.
        super.setAttributeName(attributeName != null ? attributeName.trim() : attributeName);
        setServerMethod(preventDefault, jsPreFunctionBody, serverMethod, jsFilterFunctionBody,
                jsPostFunctionBody, serverSideData);
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

    /*
     * (non-Javadoc)
     *
     * @see com.webfirmframework.wffweb.tag.html.attribute.core.AbstractAttribute#
     * setPreIndexedAttribute(com.webfirmframework.wffweb.tag.html.attribute.
     * core.PreIndexedAttributeName)
     */
    @Override
    protected void setPreIndexedAttribute(final PreIndexedAttributeName preIndexedAttrName) {
        super.setPreIndexedAttribute(preIndexedAttrName);
        this.preIndexedAttrName = preIndexedAttrName;
    }

    @Override
    protected void setAttributeName(final String attributeName) {
        // NB: always trim attributeName to avoid starting with 0 value byte
        // because the first byte value is zero if sending its index bytes from
        // client.
        super.setAttributeName(attributeName != null ? attributeName.trim() : attributeName);
    }

    /**
     * sets the value for this attribute
     *
     * @param value the value for the attribute.
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
     * @param serverMethod the {@code ServerMethod} object to set.
     * @author WFF
     */
    public void setServerMethod(final ServerMethod serverMethod) {
        if (serverMethod != null) {
            setServerMethod(jsPreFunctionBody, serverMethod, jsFilterFunctionBody, jsPostFunctionBody);
        }
    }

    private static String getPreparedJsPreFunctionBody(final String jsFunctionBody) {

        final String functionBody = StringUtil.strip(jsFunctionBody);
        final StringBuilder builder = new StringBuilder(26);

        builder.append("function(event,source,action){").append(functionBody);
        if (functionBody.charAt(functionBody.length() - 1) != ';') {
            builder.append(';');
        }

        return builder.append('}').toString();
    }

    private static String getPreparedJsFilterFunctionBody(final String jsFunctionBody) {

        final String functionBody = StringUtil.strip(jsFunctionBody);
        final StringBuilder builder = new StringBuilder(26);

        builder.append("function(event,source){").append(functionBody);
        if (functionBody.charAt(functionBody.length() - 1) != ';') {
            builder.append(';');
        }

        return builder.append('}').toString();
    }

    /**
     * @param jsPreFunctionBody    It is the body part of JavaScript function
     *                             (without function declaration). It must return
     *                             true/false. This function will invoke at client
     *                             side before {@code serverMethod}. If the
     *                             jsPrefunction returns true then only
     *                             {@code serverMethod} method will invoke (if
     *                             it is implemented). It has implicit objects in
     *                             its scope. They are {@code event}, {@code source}
     *                             which gives the reference of the current tag and
     *                             {@code action}. The {@code action} implicit
     *                             object has a function named {@code perform()}
     *                             which can be used to invoke
     *                             {@code jsFilterFunctionBody} and
     *                             {@code serverMethod} (it works just like
     *                             returning true in the {@code jsPreFunctionBody}).
     *                             If the {@code action.perform()} is called inside
     *                             {@code jsPreFunctionBody} then returning true in
     *                             it has no effect. The {@code action} implicit
     *                             object is only available since 3.0.15. <br>
     *                             Eg:-
     *
     *                             <pre>
     *                              if (source.type == 'button') {
     *                                  return true;
     *                              }
     *                              return false;
     *                             </pre>
     *
     *                             <br>
     *                             NB: calling {@code action.perform()} inside
     *                             {@code jsFilterFunctionBody} and
     *                             {@code jsPostFunctionBody} of the same event
     *                             attribute will be harmful as it will make an
     *                             infinite recursive call.
     *
     * @param serverMethod    This method will invoke at server side with an
     *                             argument {@code wffBMObject}. The
     *                             {@code wffBMObject} is the representational
     *                             javascript object returned by
     *                             {@code jsFilterFunctionBody}.
     * @param jsFilterFunctionBody The body part of a javascript function (without
     *                             function declaration). It can return a javascript
     *                             object so that it will be available at server
     *                             side in {@code serverMethod} as
     *                             {@code wffBMObject} parameter. There are implicit
     *                             objects {@code event} and {@code source} in the
     *                             scope.<br>
     *                             Eg:-
     *
     *                             <pre>
     *                                                                                                var bName = source.name;
     *                                                                                                return {buttonName: bName, author:'wff', dateOfYear: 2014};
     *                             </pre>
     *
     * @param jsPostFunctionBody   The body part of a javascript function (without
     *                             function declaration). The {@code wffBMObject}
     *                             returned by {@code serverMethod} will be
     *                             available as an implicit object {@code jsObject}
     *                             in the scope. There are common implicit objects
     *                             {@code event} and {@code source} in the scope.
     * @author WFF
     * @since 2.0.0
     */
    public void setServerMethod(final String jsPreFunctionBody, final ServerMethod serverMethod,
            final String jsFilterFunctionBody, final String jsPostFunctionBody) {

        setServerMethod(jsPreFunctionBody, serverMethod, jsFilterFunctionBody, jsPostFunctionBody,
                serverSideData);
    }

    /**
     * @param jsPreFunctionBody    It is the body part of JavaScript function
     *                             (without function declaration). It must return
     *                             true/false. This function will invoke at client
     *                             side before {@code serverMethod}. If the
     *                             jsPrefunction returns true then only
     *                             {@code serverMethod} method will invoke (if
     *                             it is implemented). It has implicit objects in
     *                             its scope. They are {@code event}, {@code source}
     *                             which gives the reference of the current tag and
     *                             {@code action}. The {@code action} implicit
     *                             object has a function named {@code perform()}
     *                             which can be used to invoke
     *                             {@code jsFilterFunctionBody} and
     *                             {@code serverMethod} (it works just like
     *                             returning true in the {@code jsPreFunctionBody}).
     *                             If the {@code action.perform()} is called inside
     *                             {@code jsPreFunctionBody} then returning true in
     *                             it has no effect. The {@code action} implicit
     *                             object is only available since 3.0.15. <br>
     *                             Eg:-
     *
     *                             <pre>
     *                              if (source.type == 'button') {
     *                                  return true;
     *                              }
     *                              return false;
     *                             </pre>
     *
     *                             <br>
     *                             NB: calling {@code action.perform()} inside
     *                             {@code jsFilterFunctionBody} and
     *                             {@code jsPostFunctionBody} of the same event
     *                             attribute will be harmful as it will make an
     *                             infinite recursive call.
     *
     * @param serverMethod    This method will invoke at server side with an
     *                             argument {@code wffBMObject}. The
     *                             {@code wffBMObject} is the representational
     *                             javascript object returned by
     *                             {@code jsFilterFunctionBody}.
     * @param jsFilterFunctionBody The body part of a javascript function (without
     *                             function declaration). It can return a javascript
     *                             object so that it will be available at server
     *                             side in {@code serverMethod} as
     *                             {@code wffBMObject} parameter. There are implicit
     *                             objects {@code event} and {@code source} in the
     *                             scope.<br>
     *                             Eg:-
     *
     *                             <pre>
     *                                                                                                var bName = source.name;
     *                                                                                                return {buttonName: bName, author:'wff', dateOfYear: 2014};
     *                             </pre>
     *
     * @param jsPostFunctionBody   The body part of a javascript function (without
     *                             function declaration). The {@code wffBMObject}
     *                             returned by {@code serverMethod} will be
     *                             available as an implicit object {@code jsObject}
     *                             in the scope. There are common implicit objects
     *                             {@code event} and {@code source} in the scope.
     * @param serverSideData       this data will be available in the Event object
     *                             of ServerMethod.invoke method.
     * @author WFF
     * @since 3.0.2
     */
    public void setServerMethod(final String jsPreFunctionBody, final ServerMethod serverMethod,
            final String jsFilterFunctionBody, final String jsPostFunctionBody, final Object serverSideData) {
        setServerMethod(preventDefault, jsPreFunctionBody, serverMethod, jsFilterFunctionBody,
                jsPostFunctionBody, serverSideData);
    }

    /**
     * @param preventDefault       true to call event.preventDefault(); on event
     * @param jsPreFunctionBody    It is the body part of JavaScript function
     *                             (without function declaration). It must return
     *                             true/false. This function will invoke at client
     *                             side before {@code serverMethod}. If the
     *                             jsPrefunction returns true then only
     *                             {@code serverMethod} method will invoke (if
     *                             it is implemented). It has implicit objects in
     *                             its scope. They are {@code event}, {@code source}
     *                             which gives the reference of the current tag and
     *                             {@code action}. The {@code action} implicit
     *                             object has a function named {@code perform()}
     *                             which can be used to invoke
     *                             {@code jsFilterFunctionBody} and
     *                             {@code serverMethod} (it works just like
     *                             returning true in the {@code jsPreFunctionBody}).
     *                             If the {@code action.perform()} is called inside
     *                             {@code jsPreFunctionBody} then returning true in
     *                             it has no effect. The {@code action} implicit
     *                             object is only available since 3.0.15. <br>
     *                             Eg:-
     *
     *                             <pre>
     *                              if (source.type == 'button') {
     *                                  return true;
     *                              }
     *                              return false;
     *                             </pre>
     *
     *                             <br>
     *                             NB: calling {@code action.perform()} inside
     *                             {@code jsFilterFunctionBody} and
     *                             {@code jsPostFunctionBody} of the same event
     *                             attribute will be harmful as it will make an
     *                             infinite recursive call.
     *
     * @param serverMethod    This method will invoke at server side with an
     *                             argument {@code wffBMObject}. The
     *                             {@code wffBMObject} is the representational
     *                             javascript object returned by
     *                             {@code jsFilterFunctionBody}.
     * @param jsFilterFunctionBody The body part of a javascript function (without
     *                             function declaration). It can return a javascript
     *                             object so that it will be available at server
     *                             side in {@code serverMethod} as
     *                             {@code wffBMObject} parameter. There are implicit
     *                             objects {@code event} and {@code source} in the
     *                             scope.<br>
     *                             Eg:-
     *
     *                             <pre>
     *                                                                                                var bName = source.name;
     *                                                                                                return {buttonName: bName, author:'wff', dateOfYear: 2014};
     *                             </pre>
     *
     * @param jsPostFunctionBody   The body part of a JavaScript function (without
     *                             function declaration). The {@code wffBMObject}
     *                             returned by {@code serverMethod} will be
     *                             available as an implicit object {@code jsObject}
     *                             in the scope. There are common implicit objects
     *                             {@code event} and {@code source} in the scope.
     * @param serverSideData       this data will be available in the Event object
     *                             of ServerMethod.invoke method.
     * @author WFF
     * @since 3.0.15
     */
    protected void setServerMethod(final boolean preventDefault, final String jsPreFunctionBody,
            final ServerMethod serverMethod, final String jsFilterFunctionBody,
            final String jsPostFunctionBody, final Object serverSideData) {

        this.serverSideData = serverSideData;

        if (serverMethod != null) {

            final PreIndexedAttributeName preIndexedAttrName = this.preIndexedAttrName;
            final String attrIndexOrName = preIndexedAttrName != null && preIndexedAttrName.eventAttr()
                    ? String.valueOf(preIndexedAttrName.eventAttrIndex())
                    : '\'' + getAttributeName() + '\'';

            if (jsPreFunctionBody != null && jsPostFunctionBody != null && jsFilterFunctionBody != null) {

                super.setAttributeValue(new StringBuilder(
                        preventDefault ? invokeAsyncWithPreFilterFunPDTrue : invokeAsyncWithPreFilterFun)
                                .append("(event,this,").append(attrIndexOrName).append(',')
                                .append(getPreparedJsPreFunctionBody(jsPreFunctionBody)).append(',')
                                .append(getPreparedJsFilterFunctionBody(jsFilterFunctionBody)).append(')').toString());

            } else if (jsPreFunctionBody != null && jsPostFunctionBody != null) {

                super.setAttributeValue(
                        new StringBuilder().append(preventDefault ? invokeAsyncWithPreFunPDTrue : invokeAsyncWithPreFun)
                                .append("(event,this,").append(attrIndexOrName).append(',')
                                .append(getPreparedJsPreFunctionBody(jsPreFunctionBody)).append(')').toString());

            } else if (jsPreFunctionBody != null && jsFilterFunctionBody != null) {

                super.setAttributeValue(new StringBuilder(
                        preventDefault ? invokeAsyncWithPreFilterFunPDTrue : invokeAsyncWithPreFilterFun)
                                .append("(event,this,").append(attrIndexOrName).append(',')
                                .append(getPreparedJsPreFunctionBody(jsPreFunctionBody)).append(',')
                                .append(getPreparedJsFilterFunctionBody(jsFilterFunctionBody)).append(')').toString());

            } else if (jsPostFunctionBody != null && jsFilterFunctionBody != null) {

                super.setAttributeValue(
                        new StringBuilder(preventDefault ? invokeAsyncWithFilterFunPDTrue : invokeAsyncWithFilterFun)
                                .append("(event,this,").append(attrIndexOrName).append(',')
                                .append(getPreparedJsFilterFunctionBody(jsFilterFunctionBody)).append(')').toString());

            } else if (jsPreFunctionBody != null) {

                super.setAttributeValue(
                        new StringBuilder(preventDefault ? invokeAsyncWithPreFunPDTrue : invokeAsyncWithPreFun)
                                .append("(event,this,").append(attrIndexOrName).append(',')
                                .append(getPreparedJsPreFunctionBody(jsPreFunctionBody)).append(')').toString());

            } else if (jsFilterFunctionBody != null) {

                super.setAttributeValue(
                        new StringBuilder(preventDefault ? invokeAsyncWithFilterFunPDTrue : invokeAsyncWithFilterFun)
                                .append("(event,this,").append(attrIndexOrName).append(',')
                                .append(getPreparedJsFilterFunctionBody(jsFilterFunctionBody)).append(')').toString());
            } else if (jsPostFunctionBody != null) {
                super.setAttributeValue(new StringBuilder(preventDefault ? invokeAsyncPDTrue : invokeAsync)
                        .append("(event,this,").append(attrIndexOrName).append(')').toString());
            } else {

                super.setAttributeValue(new StringBuilder(preventDefault ? invokeAsyncPDTrue : invokeAsync)
                        .append("(event,this,").append(attrIndexOrName).append(')').toString());
            }
            this.preventDefault = preventDefault;
            this.jsPreFunctionBody = jsPreFunctionBody;
            this.jsFilterFunctionBody = jsFilterFunctionBody;
            this.jsPostFunctionBody = jsPostFunctionBody;
            this.serverMethod = serverMethod;
        } else {
            LOGGER.warning(
                    "serverMethod is null so jsPreFunctionBody, jsFilterFunctionBody and jsPostFunctionBody are not also set.They are valid only if serverMethod is NOT null.");
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
    public ServerMethod getServerMethod() {
        return serverMethod;
    }

    @Override
    public String getJsPostFunctionBody() {
        return jsPostFunctionBody;
    }

    /**
     * @return the js filter function body
     * @author WFF
     * @since 2.1.9
     */
    public String getJsFilterFunctionBody() {
        return jsFilterFunctionBody;
    }

    /**
     * @return the js prefunction body
     * @author WFF
     * @since 2.1.9
     */
    public String getJsPreFunctionBody() {
        return jsPreFunctionBody;
    }

    /**
     * true to call event.preventDefault(); on event. It will set only if there is
     * {@code ServerMethod}. This is applicable for some special attributes
     * like OnSubmit.
     *
     * @param preventDefault true to call event.preventDefault(); on event otherwise
     *                       false.
     * @since 3.0.15
     */
    protected void setPreventDefault(final boolean preventDefault) {
        setServerMethod(preventDefault, jsPreFunctionBody, serverMethod, jsFilterFunctionBody,
                jsPostFunctionBody, serverSideData);
    }

    /**
     * Sets the post function body JavaScript.
     *
     * @param jsPostFunctionBody the post function body JavaScript.
     * @author WFF
     */
    public void setJsPostFunctionBody(final String jsPostFunctionBody) {
        setServerMethod(jsPreFunctionBody, serverMethod, jsFilterFunctionBody, jsPostFunctionBody);
    }

    /**
     * Sets the pre function body JavaScript. It is the body part of JavaScript
     * function (without function declaration). It must return true/false. This
     * function will invoke at client side before {@code serverMethod}. If the
     * jsPrefunction returns true then only {@code serverMethod} method will
     * invoke (if it is implemented). It has implicit objects in its scope. They are
     * {@code event}, {@code source} which gives the reference of the current tag
     * and {@code action}. The {@code action} implicit object has a function named
     * {@code perform()} which can be used to invoke {@code jsFilterFunctionBody}
     * and {@code serverMethod} (it works just like returning true in the
     * {@code jsPreFunctionBody}). If the {@code action.perform()} is called inside
     * {@code jsPreFunctionBody} then returning true in it has no effect. The
     * {@code action} implicit object is only available since 3.0.15.
     *
     * <br>
     * NB: calling {@code action.perform()} inside {@code jsFilterFunctionBody} and
     * {@code jsPostFunctionBody} of the same event attribute will be harmful as it
     * will make an infinite recursive call.
     *
     * @param jsPreFunctionBody the JavaScript to execute.
     * @author WFF
     * @since 2.1.9
     */
    public void setJsPreFunctionBody(final String jsPreFunctionBody) {
        setServerMethod(jsPreFunctionBody, serverMethod, jsFilterFunctionBody, jsPostFunctionBody);
    }

    /**
     * Sets the filter function body JavaScript.
     *
     * @param jsFilterFunctionBody the js filter function body
     * @author WFF
     * @since 2.1.9
     */
    public void setJsFilterFunctionBody(final String jsFilterFunctionBody) {
        setServerMethod(jsPreFunctionBody, serverMethod, jsFilterFunctionBody, jsPostFunctionBody);
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
     * @param serverSideData the serverSideData to set
     * @since 3.0.2
     */
    public void setServerSideData(final Object serverSideData) {
        this.serverSideData = serverSideData;
    }

}
