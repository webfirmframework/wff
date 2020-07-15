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
 */
package com.webfirmframework.wffweb.tag.html.attribute.event;

public class CustomEventAttribute extends AbstractEventAttribute {

    private static final long serialVersionUID = 1L;

    public CustomEventAttribute(final String attributeName,
            final ServerAsyncMethod serverAsyncMethod) {
        super(attributeName, null, serverAsyncMethod, null, null);
    }

    public CustomEventAttribute(final String attributeName,
            final String jsPreFunctionBody,
            final ServerAsyncMethod serverAsyncMethod,
            final String jsFilterFunctionBody,
            final String postJsFunctionBody) {
        super(attributeName, jsPreFunctionBody, serverAsyncMethod,
                jsFilterFunctionBody, postJsFunctionBody);
    }

    /**
     * @param attributeName
     * @param serverAsyncMethod
     * @param serverSideData
     * @since 3.0.2
     */
    public CustomEventAttribute(final String attributeName,
            final ServerAsyncMethod serverAsyncMethod,
            final Object serverSideData) {
        super(attributeName, null, serverAsyncMethod, null, null,
                serverSideData);
    }

    /**
     * @param attributeName
     * @param jsPreFunctionBody
     * @param serverAsyncMethod
     * @param jsFilterFunctionBody
     * @param postJsFunctionBody
     * @param serverSideData
     * @since 3.0.2
     */
    public CustomEventAttribute(final String attributeName,
            final String jsPreFunctionBody,
            final ServerAsyncMethod serverAsyncMethod,
            final String jsFilterFunctionBody, final String postJsFunctionBody,
            final Object serverSideData) {
        super(attributeName, jsPreFunctionBody, serverAsyncMethod,
                jsFilterFunctionBody, postJsFunctionBody, serverSideData);
    }

}
