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
package com.webfirmframework.wffweb.tag.html.attribute.event.form;

import com.webfirmframework.wffweb.tag.html.attribute.core.PreIndexedAttributeName;
import com.webfirmframework.wffweb.tag.html.attribute.event.AbstractEventAttribute;
import com.webfirmframework.wffweb.tag.html.attribute.event.ServerAsyncMethod;
import com.webfirmframework.wffweb.tag.html.identifier.AAttributable;

/**
 * <code>onsubmit</code> attribute for the element. This attribute is supported
 * by multiple tags.
 *
 * @author WFF
 * @since 2.0.0
 */
public class OnSubmit extends AbstractEventAttribute implements AAttributable {

    private static final long serialVersionUID = 1_0_0L;

    private static final PreIndexedAttributeName PRE_INDEXED_ATTR_NAME;

    static {
        PRE_INDEXED_ATTR_NAME = (PreIndexedAttributeName.ONSUBMIT);
    }

    {

        super.setPreIndexedAttribute(PRE_INDEXED_ATTR_NAME);
        init();
    }

    public OnSubmit() {
    }

    /**
     * @param serverAsyncMethod This method will invoke at server side with an
     *                          argument {@code wffBMObject}. The
     *                          {@code wffBMObject} is the representational
     *                          JavaScript object returned by
     *                          {@code jsFilterFunctionBody}.
     */
    public OnSubmit(final ServerAsyncMethod serverAsyncMethod) {
        setServerAsyncMethod(null, serverAsyncMethod, null, null);
    }

    /**
     * @param jsPreFunctionBody    the body part JavaScript function (without
     *                             function declaration). It must return true/false.
     *                             This function will invoke at client side before
     *                             {@code serverAsyncMethod}. If the jsPrefunction
     *                             returns true then only {@code serverAsyncMethod}
     *                             method will invoke (if it is implemented). It has
     *                             implicit objects like {@code event} and
     *                             {@code source} which gives the reference of the
     *                             current tag. <br>
     *                             Eg:-
     *
     *                             <pre>
     *                                                                    if (source.type == 'button') {
     *                                                                       return true;
     *                                                                    }
     *                                                                    return false;
     *                             </pre>
     *
     * @param serverAsyncMethod    This method will invoke at server side with an
     *                             argument {@code wffBMObject}. The
     *                             {@code wffBMObject} is the representational
     *                             JavaScript object returned by
     *                             {@code jsFilterFunctionBody}.
     * @param jsFilterFunctionBody The body part of a JavaScript function (without
     *                             function declaration). It can return a JavaScript
     *                             object so that it will be available at server
     *                             side in {@code serverAsyncMethod} as
     *                             {@code wffBMObject} parameter. There are implicit
     *                             objects {@code event} and {@code source} in the
     *                             scope.<br>
     *                             Eg:-
     *
     *                             <pre>
     *                                                                    var bName = source.name;
     *                                                                    return {buttonName: bName, author:'wff', dateOfYear: 2014};
     *                             </pre>
     *
     * @param jsPostFunctionBody   The body part of a JavaScript function (without
     *                             function declaration). The {@code wffBMObject}
     *                             returned by {@code serverAsyncMethod} will be
     *                             available as an implicit object {@code jsObject}
     *                             in the scope. There are common implicit objects
     *                             {@code event} and {@code source} in the scope.
     */
    public OnSubmit(final String jsPreFunctionBody, final ServerAsyncMethod serverAsyncMethod,
            final String jsFilterFunctionBody, final String jsPostFunctionBody) {
        setServerAsyncMethod(jsPreFunctionBody, serverAsyncMethod, jsFilterFunctionBody, jsPostFunctionBody);
    }

    public OnSubmit(final String value) {
        setAttributeValue(value);
    }

    /**
     * @param serverAsyncMethod This method will invoke at server side with an
     *                          argument {@code wffBMObject}. The
     *                          {@code wffBMObject} is the representational
     *                          JavaScript object returned by
     *                          {@code jsFilterFunctionBody}.
     * @param serverSideData    this data will be available in the Event object of
     *                          ServerAsyncMethod.asyncMethod method.
     * @since 3.0.2
     */
    public OnSubmit(final ServerAsyncMethod serverAsyncMethod, final Object serverSideData) {
        setServerAsyncMethod(null, serverAsyncMethod, null, null, serverSideData);
    }

    /**
     * @param jsPreFunctionBody    the body part JavaScript function (without
     *                             function declaration). It must return true/false.
     *                             This function will invoke at client side before
     *                             {@code serverAsyncMethod}. If the jsPrefunction
     *                             returns true then only {@code serverAsyncMethod}
     *                             method will invoke (if it is implemented). It has
     *                             implicit objects like {@code event} and
     *                             {@code source} which gives the reference of the
     *                             current tag. <br>
     *                             Eg:-
     *
     *                             <pre>
     *                                                                    if (source.type == 'button') {
     *                                                                       return true;
     *                                                                    }
     *                                                                    return false;
     *                             </pre>
     *
     * @param serverAsyncMethod    This method will invoke at server side with an
     *                             argument {@code wffBMObject}. The
     *                             {@code wffBMObject} is the representational
     *                             JavaScript object returned by
     *                             {@code jsFilterFunctionBody}.
     * @param jsFilterFunctionBody The body part of a JavaScript function (without
     *                             function declaration). It can return a JavaScript
     *                             object so that it will be available at server
     *                             side in {@code serverAsyncMethod} as
     *                             {@code wffBMObject} parameter. There are implicit
     *                             objects {@code event} and {@code source} in the
     *                             scope.<br>
     *                             Eg:-
     *
     *                             <pre>
     *                                                                    var bName = source.name;
     *                                                                    return {buttonName: bName, author:'wff', dateOfYear: 2014};
     *                             </pre>
     *
     * @param jsPostFunctionBody   The body part of a JavaScript function (without
     *                             function declaration). The {@code wffBMObject}
     *                             returned by {@code serverAsyncMethod} will be
     *                             available as an implicit object {@code jsObject}
     *                             in the scope. There are common implicit objects
     *                             {@code event} and {@code source} in the scope.
     * @param serverSideData       this data will be available in the Event object
     *                             of ServerAsyncMethod.asyncMethod method.
     * @since 3.0.2
     */
    public OnSubmit(final String jsPreFunctionBody, final ServerAsyncMethod serverAsyncMethod,
            final String jsFilterFunctionBody, final String jsPostFunctionBody, final Object serverSideData) {
        setServerAsyncMethod(jsPreFunctionBody, serverAsyncMethod, jsFilterFunctionBody, jsPostFunctionBody,
                serverSideData);
    }

    /**
     * @param preventDefault    true to call event.preventDefault(); on event
     * @param serverAsyncMethod This method will invoke at server side with an
     *                          argument {@code wffBMObject}. The
     *                          {@code wffBMObject} is the representational
     *                          JavaScript object returned by
     *                          {@code jsFilterFunctionBody}.
     * @since 3.0.15
     */
    public OnSubmit(final boolean preventDefault, final ServerAsyncMethod serverAsyncMethod) {
        setServerAsyncMethod(preventDefault, null, serverAsyncMethod, null, null, null);
    }

    /**
     * @param preventDefault       true to call event.preventDefault(); on event
     * @param jsPreFunctionBody    the body part JavaScript function (without
     *                             function declaration). It must return true/false.
     *                             This function will invoke at client side before
     *                             {@code serverAsyncMethod}. If the jsPrefunction
     *                             returns true then only {@code serverAsyncMethod}
     *                             method will invoke (if it is implemented). It has
     *                             implicit objects like {@code event} and
     *                             {@code source} which gives the reference of the
     *                             current tag. <br>
     *                             Eg:-
     *
     *                             <pre>
     *                                                                    if (source.type == 'button') {
     *                                                                       return true;
     *                                                                    }
     *                                                                    return false;
     *                             </pre>
     *
     * @param serverAsyncMethod    This method will invoke at server side with an
     *                             argument {@code wffBMObject}. The
     *                             {@code wffBMObject} is the representational
     *                             JavaScript object returned by
     *                             {@code jsFilterFunctionBody}.
     * @param jsFilterFunctionBody The body part of a JavaScript function (without
     *                             function declaration). It can return a JavaScript
     *                             object so that it will be available at server
     *                             side in {@code serverAsyncMethod} as
     *                             {@code wffBMObject} parameter. There are implicit
     *                             objects {@code event} and {@code source} in the
     *                             scope.<br>
     *                             Eg:-
     *
     *                             <pre>
     *                                                                    var bName = source.name;
     *                                                                    return {buttonName: bName, author:'wff', dateOfYear: 2014};
     *                             </pre>
     *
     * @param jsPostFunctionBody   The body part of a JavaScript function (without
     *                             function declaration). The {@code wffBMObject}
     *                             returned by {@code serverAsyncMethod} will be
     *                             available as an implicit object {@code jsObject}
     *                             in the scope. There are common implicit objects
     *                             {@code event} and {@code source} in the scope.
     * @since 3.0.15
     */
    public OnSubmit(final boolean preventDefault, final String jsPreFunctionBody,
            final ServerAsyncMethod serverAsyncMethod, final String jsFilterFunctionBody,
            final String jsPostFunctionBody) {
        setServerAsyncMethod(preventDefault, jsPreFunctionBody, serverAsyncMethod, jsFilterFunctionBody,
                jsPostFunctionBody, null);
    }

    /**
     * @param preventDefault       true to call event.preventDefault(); on event
     *
     *
     * @param serverAsyncMethod    This method will invoke at server side with an
     *                             argument {@code wffBMObject}. The
     *                             {@code wffBMObject} is the representational
     *                             JavaScript object returned by
     *                             {@code jsFilterFunctionBody}.
     * @param jsFilterFunctionBody The body part of a JavaScript function (without
     *                             function declaration). It can return a JavaScript
     *                             object so that it will be available at server
     *                             side in {@code serverAsyncMethod} as
     *                             {@code wffBMObject} parameter. There are implicit
     *                             objects {@code event} and {@code source} in the
     *                             scope.<br>
     *                             Eg:-
     *
     *                             <pre>
     *                                                                    var bName = source.name;
     *                                                                    return {buttonName: bName, author:'wff', dateOfYear: 2014};
     *                             </pre>
     *
     * @param jsPostFunctionBody   The body part of a JavaScript function (without
     *                             function declaration). The {@code wffBMObject}
     *                             returned by {@code serverAsyncMethod} will be
     *                             available as an implicit object {@code jsObject}
     *                             in the scope. There are common implicit objects
     *                             {@code event} and {@code source} in the scope.
     * @since 3.0.15
     */
    public OnSubmit(final boolean preventDefault, final ServerAsyncMethod serverAsyncMethod,
            final String jsFilterFunctionBody, final String jsPostFunctionBody) {
        setServerAsyncMethod(preventDefault, null, serverAsyncMethod, jsFilterFunctionBody, jsPostFunctionBody, null);
    }

    /**
     * @param preventDefault    true to call event.preventDefault(); on event
     * @param serverAsyncMethod This method will invoke at server side with an
     *                          argument {@code wffBMObject}. The
     *                          {@code wffBMObject} is the representational
     *                          JavaScript object returned by
     *                          {@code jsFilterFunctionBody}.
     * @param serverSideData    this data will be available in the Event object of
     *                          ServerAsyncMethod.asyncMethod method.
     * @since 3.0.15
     */
    public OnSubmit(final boolean preventDefault, final ServerAsyncMethod serverAsyncMethod,
            final Object serverSideData) {
        setServerAsyncMethod(preventDefault, null, serverAsyncMethod, null, null, serverSideData);
    }

    /**
     * @param preventDefault       true to call event.preventDefault(); on event
     * @param jsPreFunctionBody    the body part JavaScript function (without
     *                             function declaration). It must return true/false.
     *                             This function will invoke at client side before
     *                             {@code serverAsyncMethod}. If the jsPrefunction
     *                             returns true then only {@code serverAsyncMethod}
     *                             method will invoke (if it is implemented). It has
     *                             implicit objects like {@code event} and
     *                             {@code source} which gives the reference of the
     *                             current tag. <br>
     *                             Eg:-
     *
     *                             <pre>
     *                                                                    if (source.type == 'button') {
     *                                                                       return true;
     *                                                                    }
     *                                                                    return false;
     *                             </pre>
     *
     * @param serverAsyncMethod    This method will invoke at server side with an
     *                             argument {@code wffBMObject}. The
     *                             {@code wffBMObject} is the representational
     *                             JavaScript object returned by
     *                             {@code jsFilterFunctionBody}.
     * @param jsFilterFunctionBody The body part of a JavaScript function (without
     *                             function declaration). It can return a JavaScript
     *                             object so that it will be available at server
     *                             side in {@code serverAsyncMethod} as
     *                             {@code wffBMObject} parameter. There are implicit
     *                             objects {@code event} and {@code source} in the
     *                             scope.<br>
     *                             Eg:-
     *
     *                             <pre>
     *                                                                    var bName = source.name;
     *                                                                    return {buttonName: bName, author:'wff', dateOfYear: 2014};
     *                             </pre>
     *
     * @param jsPostFunctionBody   The body part of a JavaScript function (without
     *                             function declaration). The {@code wffBMObject}
     *                             returned by {@code serverAsyncMethod} will be
     *                             available as an implicit object {@code jsObject}
     *                             in the scope. There are common implicit objects
     *                             {@code event} and {@code source} in the scope.
     * @param serverSideData       this data will be available in the Event object
     *                             of ServerAsyncMethod.asyncMethod method.
     * @since 3.0.15
     */
    public OnSubmit(final boolean preventDefault, final String jsPreFunctionBody,
            final ServerAsyncMethod serverAsyncMethod, final String jsFilterFunctionBody,
            final String jsPostFunctionBody, final Object serverSideData) {
        setServerAsyncMethod(preventDefault, jsPreFunctionBody, serverAsyncMethod, jsFilterFunctionBody,
                jsPostFunctionBody, serverSideData);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.webfirmframework.wffweb.tag.html.attribute.event.
     * AbstractEventAttribute#getPreventDefault()
     */
    @Override
    public boolean isPreventDefault() {
        return super.isPreventDefault();
    }

    /**
     * true to call event.preventDefault(); on event, it will prevent form
     * submission to action url if it is added on form. It will set only if there is
     * {@code ServerAsyncMethod}.
     *
     * @param preventDefault true to call event.preventDefault(); on event otherwise
     *                       false.
     * @since 3.0.15
     */
    @Override
    public void setPreventDefault(final boolean preventDefault) {
        super.setPreventDefault(preventDefault);
    }

    /**
     * invokes only once per object
     *
     * @author WFF
     * @since 2.0.0
     */
    @Override
    protected void init() {
        // to override and use this method
    }

}
