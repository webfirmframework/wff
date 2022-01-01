/*
 * Copyright 2014-2022 Web Firm Framework
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
package com.webfirmframework.wffweb.tag.html.attribute.event.animation;

import com.webfirmframework.wffweb.tag.html.attribute.core.PreIndexedAttributeName;
import com.webfirmframework.wffweb.tag.html.attribute.event.AbstractEventAttribute;
import com.webfirmframework.wffweb.tag.html.attribute.event.ServerAsyncMethod;

/**
 *
 * <code>animationend</code> attribute for the element. This attribute is
 * supported by multiple tags.
 *
 * @since 2.0.0
 * @author WFF
 *
 */
public class AnimationEnd extends AbstractEventAttribute {

    private static final long serialVersionUID = 1_0_0L;

    private static final PreIndexedAttributeName PRE_INDEXED_ATTR_NAME;

    static {
        PRE_INDEXED_ATTR_NAME = (PreIndexedAttributeName.ANIMATIONEND);

    }

    {

        super.setPreIndexedAttribute(PRE_INDEXED_ATTR_NAME);
        init();
    }

    public AnimationEnd() {
    }

    public AnimationEnd(final ServerAsyncMethod serverAsyncMethod) {
        setServerAsyncMethod(null, serverAsyncMethod, null, null);
    }

    public AnimationEnd(final String jsPreFunctionBody, final ServerAsyncMethod serverAsyncMethod,
            final String jsFilterFunctionBody, final String jsPostFunctionBody) {
        setServerAsyncMethod(jsPreFunctionBody, serverAsyncMethod, jsFilterFunctionBody, jsPostFunctionBody);
    }

    /**
     * @param serverAsyncMethod
     * @param serverSideData
     * @since 3.0.2
     */
    public AnimationEnd(final ServerAsyncMethod serverAsyncMethod, final Object serverSideData) {
        setServerAsyncMethod(null, serverAsyncMethod, null, null, serverSideData);
    }

    /**
     * @param jsPreFunctionBody
     * @param serverAsyncMethod
     * @param jsFilterFunctionBody
     * @param jsPostFunctionBody
     * @param serverSideData
     * @since 3.0.2
     */
    public AnimationEnd(final String jsPreFunctionBody, final ServerAsyncMethod serverAsyncMethod,
            final String jsFilterFunctionBody, final String jsPostFunctionBody, final Object serverSideData) {
        setServerAsyncMethod(jsPreFunctionBody, serverAsyncMethod, jsFilterFunctionBody, jsPostFunctionBody,
                serverSideData);
    }

    public AnimationEnd(final String value) {
        setAttributeValue(value);
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
