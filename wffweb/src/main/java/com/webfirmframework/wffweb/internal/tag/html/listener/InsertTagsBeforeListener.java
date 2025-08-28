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
package com.webfirmframework.wffweb.internal.tag.html.listener;

import java.io.Serializable;

import com.webfirmframework.wffweb.server.page.InsertTagsBeforeListenerImpl;
import com.webfirmframework.wffweb.tag.html.AbstractHtml;

/**
 * @author WFF
 * @since 3.0.7
 *
 */
@Deprecated(forRemoval = true, since = "12.0.6")
public sealed interface InsertTagsBeforeListener extends Serializable permits InsertTagsBeforeListenerImpl {

    public static record Event(AbstractHtml insertedTag, AbstractHtml previousParentTag) {

    }

    /**
     * inserts tags beforeTag having parent parentTag.
     *
     * @param parentTag
     * @param beforeTag
     * @param events
     * @since 3.0.7
     * @author WFF
     */
    @Deprecated(forRemoval = true, since = "12.0.6")
    public void insertedBefore(final AbstractHtml parentTag, final AbstractHtml beforeTag, final Event... events);

}
