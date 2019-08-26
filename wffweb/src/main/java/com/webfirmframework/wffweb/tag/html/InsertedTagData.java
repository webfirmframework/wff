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
 */
package com.webfirmframework.wffweb.tag.html;

import java.lang.ref.WeakReference;

import com.webfirmframework.wffweb.server.page.ClientTasksWrapper;
import com.webfirmframework.wffweb.tag.html.SharedTagContent.ContentFormatter;

/**
 * NB: only for internal use
 *
 * @author WFF
 * @since 3.0.6
 *
 */
class InsertedTagData<T> {

    private final ContentFormatter<T> formatter;

    /**
     * true if if SharedTagContent inserted by AbstractHtml.subscribeTo
     */
    private final boolean subscribed;

    private volatile WeakReference<ClientTasksWrapper> lastClientTaskRef;

    InsertedTagData(final ContentFormatter<T> formatter,
            final boolean subscribed) {
        super();
        this.formatter = formatter;
        this.subscribed = subscribed;
    }

    ContentFormatter<T> formatter() {
        return formatter;
    }

    boolean subscribed() {
        return subscribed;
    }

    /**
     * @return the lastClientTask
     */
    ClientTasksWrapper lastClientTask() {
        if (lastClientTaskRef != null) {
            return lastClientTaskRef.get();
        }
        return null;
    }

    /**
     * @param lastClientTask
     *                           the lastClientTask to set
     */
    void lastClientTask(final ClientTasksWrapper lastClientTask) {
        lastClientTaskRef = new WeakReference<>(lastClientTask);
    }

}
