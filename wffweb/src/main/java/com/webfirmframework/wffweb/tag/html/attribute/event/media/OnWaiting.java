/*
 * Copyright 2014-2019 Web Firm Framework
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
package com.webfirmframework.wffweb.tag.html.attribute.event.media;

import com.webfirmframework.wffweb.tag.html.attribute.AttributeNameConstants;
import com.webfirmframework.wffweb.tag.html.attribute.core.IndexedAttributeName;
import com.webfirmframework.wffweb.tag.html.attribute.event.AbstractEventAttribute;
import com.webfirmframework.wffweb.tag.html.attribute.event.ServerAsyncMethod;

/**
 *
 * <code>onwaiting</code> attribute for the element. This attribute is supported
 * by multiple tags.
 *
 * @since 2.0.0
 * @author WFF
 *
 */
public class OnWaiting extends AbstractEventAttribute {

    private static final long serialVersionUID = 1_0_0L;

    private static volatile int ATTR_NAME_INDEX = -1;

    static {
        final Integer index = IndexedAttributeName.INSTANCE
                .getIndexByAttributeName(AttributeNameConstants.ONWAITING);
        ATTR_NAME_INDEX = index != null ? index : -1;
    }

    {
        if (ATTR_NAME_INDEX == -1) {
            final Integer index = IndexedAttributeName.INSTANCE
                    .getIndexByAttributeName(AttributeNameConstants.ONWAITING);
            ATTR_NAME_INDEX = index != null ? index : -1;
        }
        super.setAttributeNameIndex(ATTR_NAME_INDEX);
        super.setAttributeName(AttributeNameConstants.ONWAITING);
        init();
    }

    public OnWaiting() {
    }

    public OnWaiting(final ServerAsyncMethod serverAsyncMethod) {
        setServerAsyncMethod(null, serverAsyncMethod, null, null);
    }

    public OnWaiting(final String preJsFunctionBody,
            final ServerAsyncMethod serverAsyncMethod,
            final String jsFilterFunctionBody,
            final String postJsFunctionBody) {
        setServerAsyncMethod(preJsFunctionBody, serverAsyncMethod,
                jsFilterFunctionBody, postJsFunctionBody);
    }

    public OnWaiting(final String value) {
        setAttributeValue(value);
    }

    /**
     * @param serverAsyncMethod
     * @param serverSideData
     * @since 3.0.2
     */
    public OnWaiting(final ServerAsyncMethod serverAsyncMethod,
            final Object serverSideData) {
        setServerAsyncMethod(null, serverAsyncMethod, null, null,
                serverSideData);
    }

    /**
     * @param preJsFunctionBody
     * @param serverAsyncMethod
     * @param jsFilterFunctionBody
     * @param postJsFunctionBody
     * @param serverSideData
     * @since 3.0.2
     */
    public OnWaiting(final String preJsFunctionBody,
            final ServerAsyncMethod serverAsyncMethod,
            final String jsFilterFunctionBody, final String postJsFunctionBody,
            final Object serverSideData) {
        setServerAsyncMethod(preJsFunctionBody, serverAsyncMethod,
                jsFilterFunctionBody, postJsFunctionBody, serverSideData);
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
