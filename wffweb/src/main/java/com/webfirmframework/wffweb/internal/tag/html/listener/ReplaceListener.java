/*
 * Copyright 2014-2023 Web Firm Framework
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

import com.webfirmframework.wffweb.server.page.ReplaceListenerImpl;
import com.webfirmframework.wffweb.tag.html.AbstractHtml;

/**
 * @author WFF
 * @since 3.0.7
 *
 */
public sealed interface ReplaceListener extends Serializable permits ReplaceListenerImpl {

    public static record Event(AbstractHtml insertedTag, AbstractHtml previousParentTag) {

    }

    /**
     * Tags insertedTags inserted before tag beforeTag having parent parentTag.
     *
     * @param parentTag
     * @param replacingTag
     * @param events
     * @since 3.0.7
     * @author WFF
     */
    public void replacedWith(final AbstractHtml parentTag, final AbstractHtml replacingTag, final Event... events);

}
