/*
 * Copyright 2014-2015 Web Firm Framework
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
/**
 *
 */
package com.webfirmframework.wffweb.view;

import com.webfirmframework.wffweb.io.OutputBuffer;

/**
 * @author WFF
 * @since 1.0.0
 * @version 1.0.0
 *
 */
public abstract class AbstractHtmlView implements HtmlView {

    private static final long serialVersionUID = 1836494599206744660L;

    private static final ThreadLocal<OutputBuffer> outputBufferTL = new ThreadLocal<OutputBuffer>() {
        /*
         * (non-Javadoc)
         *
         * @see java.lang.ThreadLocal#initialValue()
         */
        @Override
        protected OutputBuffer initialValue() {
            return new OutputBuffer();
        }
    };

    /*
     * (non-Javadoc)
     *
     * @see com.webfirmframework.wffweb.tag.Base#toValueString()
     */
    @Override
    public String toHtmlString() {
        final OutputBuffer outputBuffer = outputBufferTL.get();
        develop(outputBuffer);
        return outputBuffer.toString();
    }

    @Override
    public String toHtmlString(final boolean rebuild) {
        final OutputBuffer outputBuffer = outputBufferTL.get();
        outputBuffer.setRebuild(rebuild);
        develop(outputBuffer);
        return outputBuffer.toString();
    }

    @Override
    public String toString() {
        final OutputBuffer outputBuffer = outputBufferTL.get();
        develop(outputBuffer);
        return outputBuffer.toString();
    }
}
