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
import java.util.Collection;

import com.webfirmframework.wffweb.server.page.ChildTagAppendListenerImpl;
import com.webfirmframework.wffweb.tag.html.AbstractHtml;

public sealed interface ChildTagAppendListener extends Serializable permits ChildTagAppendListenerImpl {

    public static record Event(AbstractHtml parentTag, AbstractHtml appendedChildTag,
            Collection<? extends AbstractHtml> appendedChildrenTags) {

    }

    public static record ChildMovedEvent(AbstractHtml previousParentTag, AbstractHtml currentParentTag,
            AbstractHtml movedChildTag) {

    }

    public void childAppended(Event event);

    public void childrenAppended(Event event);

    /**
     * child removed from another tag and appended to this tag
     *
     * @param event
     * @since 2.0.0
     * @author WFF
     */
    public void childMoved(ChildMovedEvent event);

    public void childrendAppendedOrMoved(Collection<ChildMovedEvent> events);

}
