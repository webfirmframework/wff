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
 */
package com.webfirmframework.wffweb.tag.html.attribute.event;

public class CustomEventAttribute extends AbstractEventAttribute {

    private static final long serialVersionUID = 1L;

    public CustomEventAttribute(final String attributeName, final ServerAsyncMethod serverAsyncMethod) {
        super(attributeName, null, serverAsyncMethod, null, null);
    }

    public CustomEventAttribute(final String attributeName, final String jsPreFunctionBody,
            final ServerAsyncMethod serverAsyncMethod, final String jsFilterFunctionBody,
            final String jsPostFunctionBody) {
        super(attributeName, jsPreFunctionBody, serverAsyncMethod, jsFilterFunctionBody, jsPostFunctionBody);
    }

    /**
     * @param attributeName
     * @param serverAsyncMethod
     * @param serverSideData
     * @since 3.0.2
     */
    public CustomEventAttribute(final String attributeName, final ServerAsyncMethod serverAsyncMethod,
            final Object serverSideData) {
        super(attributeName, null, serverAsyncMethod, null, null, serverSideData);
    }

    /**
     * @param attributeName
     * @param jsPreFunctionBody
     * @param serverAsyncMethod
     * @param jsFilterFunctionBody
     * @param jsPostFunctionBody
     * @param serverSideData
     * @since 3.0.2
     */
    public CustomEventAttribute(final String attributeName, final String jsPreFunctionBody,
            final ServerAsyncMethod serverAsyncMethod, final String jsFilterFunctionBody,
            final String jsPostFunctionBody, final Object serverSideData) {
        super(attributeName, jsPreFunctionBody, serverAsyncMethod, jsFilterFunctionBody, jsPostFunctionBody,
                serverSideData);
    }

    /**
     * @param attributeName
     * @param preventDefault       true to call event.preventDefault(); on event
     *                             otherwise false. In almost all cases this may not
     *                             be required.
     * @param jsPreFunctionBody
     * @param serverAsyncMethod
     * @param jsFilterFunctionBody
     * @param jsPostFunctionBody
     * @param serverSideData
     * @since 3.0.15
     */
    public CustomEventAttribute(final String attributeName, final boolean preventDefault,
            final String jsPreFunctionBody, final ServerAsyncMethod serverAsyncMethod,
            final String jsFilterFunctionBody, final String jsPostFunctionBody, final Object serverSideData) {
        super(attributeName, preventDefault, jsPreFunctionBody, serverAsyncMethod, jsFilterFunctionBody,
                jsPostFunctionBody, serverSideData);
    }

    /**
     * @param attributeName
     * @param preventDefault       true to call event.preventDefault(); on event
     *                             otherwise false. In almost all cases this may not
     *                             be required.
     * @param jsPreFunctionBody
     * @param serverAsyncMethod
     * @param jsFilterFunctionBody
     * @param jsPostFunctionBody
     * @since 3.0.15
     */
    public CustomEventAttribute(final String attributeName, final boolean preventDefault,
            final String jsPreFunctionBody, final ServerAsyncMethod serverAsyncMethod,
            final String jsFilterFunctionBody, final String jsPostFunctionBody) {
        super(attributeName, preventDefault, jsPreFunctionBody, serverAsyncMethod, jsFilterFunctionBody,
                jsPostFunctionBody, null);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.webfirmframework.wffweb.tag.html.attribute.event.
     * AbstractEventAttribute#isPreventDefault()
     */
    @Override
    public boolean isPreventDefault() {
        return super.isPreventDefault();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.webfirmframework.wffweb.tag.html.attribute.event.
     * AbstractEventAttribute#setPreventDefault(boolean)
     */
    @Override
    public void setPreventDefault(final boolean preventDefault) {
        super.setPreventDefault(preventDefault);
    }

}
