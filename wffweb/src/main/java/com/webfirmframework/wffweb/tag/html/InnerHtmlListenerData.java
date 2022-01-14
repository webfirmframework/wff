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
 */
package com.webfirmframework.wffweb.tag.html;

import com.webfirmframework.wffweb.internal.tag.html.listener.InnerHtmlAddListener;
import com.webfirmframework.wffweb.internal.tag.html.listener.InnerHtmlAddListener.Event;
import com.webfirmframework.wffweb.tag.html.model.AbstractHtml5SharedObject;

/**
 * NB: only for internal use
 *
 * @author WFF
 * @since 3.0.6
 *
 */
final class InnerHtmlListenerData {

    private final AbstractHtml5SharedObject sharedObject;

    private final InnerHtmlAddListener.Event[] events;

    private final InnerHtmlAddListener listener;

    InnerHtmlListenerData(final AbstractHtml5SharedObject sharedObject, final InnerHtmlAddListener listener,
            final Event[] events) {
        super();
        this.sharedObject = sharedObject;
        this.listener = listener;
        this.events = events;
    }

    AbstractHtml5SharedObject sharedObject() {
        return sharedObject;
    }

    InnerHtmlAddListener.Event[] events() {
        return events;
    }

    InnerHtmlAddListener listener() {
        return listener;
    }

}
