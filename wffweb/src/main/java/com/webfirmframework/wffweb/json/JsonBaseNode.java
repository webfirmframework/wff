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
 */
package com.webfirmframework.wffweb.json;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * @since 12.0.4
 */
public sealed interface JsonBaseNode extends JsonPart permits JsonMapNode, JsonListNode {

    /**
     * @return the JSON string.
     * @since 12.0.4
     */
    @Override
    String toJsonString();

    /**
     * @return the JSON string.
     * @since 12.0.9
     */
    String toBigJsonString();

    /**
     * @param outputStream the OutputStream to write the json.
     * @param charset      the charset
     * @param flushOnWrite true to flush after each write operation otherwise false.
     * @throws IOException if writing to OutputStream throws an exception.
     * @since 12.0.9
     */
    void toOutputStream(final OutputStream outputStream, final Charset charset, final boolean flushOnWrite)
            throws IOException;

    /**
     * @param outputStream the OutputStream to write the json.
     * @param charset      the charset
     * @throws IOException if writing to OutputStream throws an exception.
     * @since 12.0.9
     */
    default void toOutputStream(final OutputStream outputStream, final Charset charset) throws IOException {
        toOutputStream(outputStream, charset, false);
    }

}
