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
 * @author WFF
 */
package com.webfirmframework.wffweb.tag.html.attribute.event;

import com.webfirmframework.wffweb.tag.html.attribute.core.AbstractAttribute;

/**
 *
 * All event attributes will be extend by this class. It contains some common
 * features which all event attribute classes want.
 *
 * @since 1.2.0
 * @author WFF
 *
 */
public abstract class AbstractEventAttribute extends AbstractAttribute
        implements EventAttribute {

    private static final long serialVersionUID = 1_0_0L;

    private ServerAsyncMethod serverAsyncMethod;

    private String jsPostFunctionBody;

    {
        init();
    }

    protected AbstractEventAttribute() {
    }

    /**
     *
     * @param value
     *            the value for the attribute
     * @since 1.2.0
     * @author WFF
     */
    protected AbstractEventAttribute(final String attributeName,
            final String value) {
        super.setAttributeName(attributeName);
        super.setAttributeValue(value);
    }

    /**
     * @param attributeName
     * @param jsPreFunctionBody
     *            the body part javascript function (without function
     *            declaration). It must return true/false. This function will
     *            invoke at client side before {@code serverAsyncMethod}. If the
     *            jsPrefunction returns true then only {@code serverAsyncMethod}
     *            method will invoke (if it is implemented). It has an implicit
     *            object {@code source} which gives the reference of the current
     *            tag. <br>
     *            Eg:-
     *
     *            <pre>
     *            if (source.type == 'button') {
     *               return true;
     *            }
     *            return false;
     *            </pre>
     *
     * @param serverAsyncMethod
     *            This method will invoke at server side with an argument
     *            {@code wffBMObject}. The {@code wffBMObject} is the
     *            representational javascript object returned by
     *            {@code jsFilterFunctionBody}.
     *
     * @param jsFilterFunctionBody
     *            The body part of a javascript function (without function
     *            declaration). It can return a javascript object so that it
     *            will be available at server side in {@code serverAsyncMethod}
     *            as {@code wffBMObject} parameter. <br>
     *            Eg:-
     *
     *            <pre>
     *            var bName = source.name;
     *            return {buttonName: bName, author:'wff', dateOfYear: 2014};
     *            </pre>
     *
     * @param jsPostFunctionBody
     *            The body part of a javascript function (without function
     *            declaration). The {@code wffBMObject} returned by
     *            {@code serverAsyncMethod} will be available as an implicit
     *            object {@code jsObject} in the scope.
     * @since 1.2.0
     * @author WFF
     */
    protected AbstractEventAttribute(final String attributeName,
            final String preJsFunctionBody,
            final ServerAsyncMethod serverAsyncMethod,
            final String jsFilterFunctionBody,
            final String postJsFunctionBody) {

        setAttributeName(attributeName);
        setServerAsyncMethod(preJsFunctionBody, serverAsyncMethod,
                jsFilterFunctionBody, postJsFunctionBody);
    }

    /**
     * invokes only once per object
     *
     * @author WFF
     * @since 1.2.0
     */
    protected void init() {
        // to override and use this method
    }

    /**
     * sets the value for this attribute
     *
     * @param value
     *            the value for the attribute.
     * @since 1.2.0
     * @author WFF
     */
    public void setValue(final String value) {
        super.setAttributeValue(value);
    }

    /**
     * gets the value of this attribute
     *
     * @return the value of the attribute
     * @since 1.2.0
     * @author WFF
     */
    public String getValue() {
        return super.getAttributeValue();
    }

    public void setServerAsyncMethod(
            final ServerAsyncMethod serverAsyncMethod) {
        if (serverAsyncMethod != null) {
            this.serverAsyncMethod = serverAsyncMethod;
            super.setAttributeValue("wffServerMethods.invokeAsync(this,'"
                    + getAttributeName() + "')");
        }
    }

    private static String getPreparedJsFunctionBody(
            final String jsfunctionBody) {

        final String functionBody = jsfunctionBody.trim();
        final StringBuilder builder = new StringBuilder();

        builder.append("function(source){");
        builder.append(functionBody);
        if (functionBody.charAt(functionBody.length() - 1) != ';') {
            builder.append(';');
        }
        builder.append("}");

        return builder.toString();
    }

    /**
     * @param jsPreFunctionBody
     *            the body part javascript function (without function
     *            declaration). It must return true/false. This function will
     *            invoke at client side before {@code serverAsyncMethod}. If the
     *            jsPrefunction returns true then only {@code serverAsyncMethod}
     *            method will invoke (if it is implemented). It has an implicit
     *            object {@code source} which gives the reference of the current
     *            tag. <br>
     *            Eg:-
     *
     *            <pre>
     *            if (source.type == 'button') {
     *               return true;
     *            }
     *            return false;
     *            </pre>
     *
     * @param serverAsyncMethod
     *            This method will invoke at server side with an argument
     *            {@code wffBMObject}. The {@code wffBMObject} is the
     *            representational javascript object returned by
     *            {@code jsFilterFunctionBody}.
     *
     * @param jsFilterFunctionBody
     *            The body part of a javascript function (without function
     *            declaration). It can return a javascript object so that it
     *            will be available at server side in {@code serverAsyncMethod}
     *            as {@code wffBMObject} parameter. <br>
     *            Eg:-
     *
     *            <pre>
     *            var bName = source.name;
     *            //the js object can also contain UInt8Array value to transfer binary data to server.
     *            return {buttonName: bName, author:'wff', dateOfYear: 2014};
     *            </pre>
     *
     * @param jsPostFunctionBody
     *            The body part of a javascript function (without function
     *            declaration). The {@code wffBMObject} returned by
     *            {@code serverAsyncMethod} will be available as an implicit
     *            object {@code jsObject} in the scope.
     * @since 1.2.0
     * @author WFF
     */
    public void setServerAsyncMethod(final String jsPreFunctionBody,
            final ServerAsyncMethod serverAsyncMethod,
            final String jsFilterFunctionBody,
            final String jsPostFunctionBody) {

        if (serverAsyncMethod != null) {

            if (jsPreFunctionBody != null && jsPostFunctionBody != null
                    && jsFilterFunctionBody != null) {

                final StringBuilder builder = new StringBuilder();

                builder.append(
                        "wffServerMethods.invokeAsyncWithPreFilterFun(this,'"
                                + getAttributeName() + "',");

                builder.append(getPreparedJsFunctionBody(jsPreFunctionBody));
                builder.append(",");
                builder.append(getPreparedJsFunctionBody(jsFilterFunctionBody));
                builder.append(")");

                this.jsPostFunctionBody = jsPostFunctionBody;

                this.serverAsyncMethod = serverAsyncMethod;
                super.setAttributeValue(builder.toString());

            } else if (jsPreFunctionBody != null
                    && jsPostFunctionBody != null) {

                final StringBuilder builder = new StringBuilder();

                builder.append("wffServerMethods.invokeAsyncWithPreFun(this,'"
                        + getAttributeName() + "',");

                builder.append(getPreparedJsFunctionBody(jsPreFunctionBody));
                builder.append(")");

                this.jsPostFunctionBody = jsPostFunctionBody;

                this.serverAsyncMethod = serverAsyncMethod;
                super.setAttributeValue(builder.toString());

            } else if (jsPreFunctionBody != null
                    && jsFilterFunctionBody != null) {

                final StringBuilder builder = new StringBuilder();

                builder.append(
                        "wffServerMethods.invokeAsyncWithPreFilterFun(this,'"
                                + getAttributeName() + "',");

                builder.append(getPreparedJsFunctionBody(jsPreFunctionBody));
                builder.append(",");
                builder.append(getPreparedJsFunctionBody(jsFilterFunctionBody));

                builder.append(")");
                this.serverAsyncMethod = serverAsyncMethod;
                super.setAttributeValue(builder.toString());

            } else if (jsPostFunctionBody != null
                    && jsFilterFunctionBody != null) {

                final StringBuilder builder = new StringBuilder();

                builder.append(
                        "wffServerMethods.invokeAsyncWithFilterFun(this,'"
                                + getAttributeName() + "',");

                builder.append(getPreparedJsFunctionBody(jsFilterFunctionBody));
                builder.append(")");

                this.jsPostFunctionBody = jsPostFunctionBody;

                this.serverAsyncMethod = serverAsyncMethod;
                super.setAttributeValue(builder.toString());

            } else if (jsPreFunctionBody != null) {

                final StringBuilder builder = new StringBuilder();

                builder.append("wffServerMethods.invokeAsyncWithPreFun(this,'"
                        + getAttributeName() + "',");

                builder.append(getPreparedJsFunctionBody(jsPreFunctionBody));

                builder.append(")");
                this.serverAsyncMethod = serverAsyncMethod;
                super.setAttributeValue(builder.toString());

            } else if (jsFilterFunctionBody != null) {
                final StringBuilder builder = new StringBuilder();

                builder.append(
                        "wffServerMethods.invokeAsyncWithFilterFun(this,'"
                                + getAttributeName() + "',");

                builder.append(getPreparedJsFunctionBody(jsFilterFunctionBody));
                builder.append(")");

                this.jsPostFunctionBody = jsPostFunctionBody;

                this.serverAsyncMethod = serverAsyncMethod;
                super.setAttributeValue(builder.toString());
            } else if (jsPostFunctionBody != null) {

                final StringBuilder builder = new StringBuilder();

                builder.append("wffServerMethods.invokeAsync(this,'"
                        + getAttributeName() + "'");

                builder.append(")");

                this.jsPostFunctionBody = jsPostFunctionBody;

                this.serverAsyncMethod = serverAsyncMethod;
                super.setAttributeValue(builder.toString());
            } else {
                this.serverAsyncMethod = serverAsyncMethod;
                super.setAttributeValue("wffServerMethods.invokeAsync(this,'"
                        + getAttributeName() + "')");
            }

        }
    }

    @Override
    public ServerAsyncMethod getServerAsyncMethod() {
        return serverAsyncMethod;
    }

    @Override
    public String getJsPostFunctionBody() {
        return jsPostFunctionBody;
    }

    public void setJsPostFunctionBody(final String jsPostFunctionBody) {
        this.jsPostFunctionBody = jsPostFunctionBody;
    }
}
