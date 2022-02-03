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

import com.webfirmframework.wffweb.internal.tag.html.listener.ChildTagRemoveListener;
import com.webfirmframework.wffweb.internal.tag.html.listener.ChildTagRemoveListener.Event;
import com.webfirmframework.wffweb.tag.html.model.AbstractHtml5SharedObject;

/**
 * @author WFF
 * @since 3.0.6
 *
 */
final class ChildTagRemoveListenerData {

    private final AbstractHtml5SharedObject sharedObject;

    private final ChildTagRemoveListener.Event event;

    private final ChildTagRemoveListener listener;

    ChildTagRemoveListenerData(final AbstractHtml5SharedObject sharedObject, final ChildTagRemoveListener listener,
            final Event event) {
        super();
        this.sharedObject = sharedObject;
        this.listener = listener;
        this.event = event;
    }

    AbstractHtml5SharedObject getSharedObject() {
        return sharedObject;
    }

    Event getEvent() {
        return event;
    }

    ChildTagRemoveListener getListener() {
        return listener;
    }

}
