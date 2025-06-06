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
package com.webfirmframework.wffweb.tag.html.attribute.event.form;

import java.io.Serial;

import com.webfirmframework.wffweb.tag.html.attribute.core.PreIndexedAttributeName;
import com.webfirmframework.wffweb.tag.html.attribute.event.AbstractEventAttribute;
import com.webfirmframework.wffweb.tag.html.attribute.event.ServerMethod;
import com.webfirmframework.wffweb.tag.html.identifier.GlobalAttributable;
import com.webfirmframework.wffweb.tag.html.identifier.InputAttributable;
import com.webfirmframework.wffweb.tag.html.identifier.TextAreaAttributable;

/**
 *
 * <code>oninput</code> attribute for the elements like <code>input</code>,
 * <code>textarea</code> and elements which are editable. <code>OnInput</code>
 * may be used to catch the even when the content of the element is changed,
 * <br>
 * for eg: if a content of <code>input</code> or <code>textarea</code> is
 * changed. If a <code>div</code> or any other elements contain
 * <code>contenteditable</code> attribute then its content will be editable and
 * <code>oninput</code> attribute can be used to catch its content change event
 * (<code>onchange</code> attribute may not catch event). It's a kind of global
 * attribute as <code>contenteditable</code> is a global attribute.
 *
 * @since 2.0.0
 * @author WFF
 *
 */
public class OnInput extends AbstractEventAttribute
        implements InputAttributable, TextAreaAttributable, GlobalAttributable {

    @Serial
    private static final long serialVersionUID = 1_0_0L;

    private static final PreIndexedAttributeName PRE_INDEXED_ATTR_NAME = PreIndexedAttributeName.ONINPUT;

    {
        super.setPreIndexedAttribute(PRE_INDEXED_ATTR_NAME);
        init();
    }

    public OnInput() {
    }

    public OnInput(final ServerMethod serverMethod) {
        setServerMethod(null, serverMethod, null, null);
    }

    public OnInput(final String jsPreFunctionBody, final ServerMethod serverMethod, final String jsFilterFunctionBody,
            final String jsPostFunctionBody) {
        setServerMethod(jsPreFunctionBody, serverMethod, jsFilterFunctionBody, jsPostFunctionBody);
    }

    public OnInput(final String value) {
        setAttributeValue(value);
    }

    /**
     * @param serverMethod
     * @param serverSideData
     * @since 3.0.2
     */
    public OnInput(final ServerMethod serverMethod, final Object serverSideData) {
        setServerMethod(null, serverMethod, null, null, serverSideData);
    }

    /**
     * @param jsPreFunctionBody
     * @param serverMethod
     * @param jsFilterFunctionBody
     * @param jsPostFunctionBody
     * @param serverSideData
     * @since 3.0.2
     */
    public OnInput(final String jsPreFunctionBody, final ServerMethod serverMethod, final String jsFilterFunctionBody,
            final String jsPostFunctionBody, final Object serverSideData) {
        setServerMethod(jsPreFunctionBody, serverMethod, jsFilterFunctionBody, jsPostFunctionBody, serverSideData);
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
