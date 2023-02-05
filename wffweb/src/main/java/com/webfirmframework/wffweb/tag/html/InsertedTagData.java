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
package com.webfirmframework.wffweb.tag.html;

import java.lang.ref.WeakReference;

import com.webfirmframework.wffweb.server.page.ClientTasksWrapper;
import com.webfirmframework.wffweb.tag.html.SharedTagContent.ContentFormatter;

/**
 * Note: this class has a natural ordering that is inconsistent with equals.
 * <br>
 * NB: only for internal use. No need to keep equals and compareTo contract by
 * overriding {@code equals} and {@code hashcode} methods, if we refer
 * {@code BigDecimal} we can see it is just based on the requirement. However,
 * this object should be unique for a particular {@code SharedTagContent} object
 * and should be used only in its scope.
 *
 * @author WFF
 * @since 3.0.6
 *
 */
final class InsertedTagData<T> implements Comparable<InsertedTagData<T>> {

    /**
     * it should be unique as it also acts as id
     */
    private final long ordinal;

    private final WeakReference<ContentFormatter<T>> formatterRef;

    /**
     * true if if SharedTagContent inserted by AbstractHtml.subscribeTo
     */
    private final boolean subscribed;

    private volatile WeakReference<ClientTasksWrapper> lastClientTaskRef;

    InsertedTagData(final long ordinal, final ContentFormatter<T> formatter, final boolean subscribed) {
        super();
        this.ordinal = ordinal;
        this.formatterRef = formatter != null ? new WeakReference<>(formatter) : null;
        this.subscribed = subscribed;
    }

    ContentFormatter<T> formatter() {
        if (formatterRef != null) {
            return formatterRef.get();
        }
        return null;
    }

    long id() {
        return ordinal;
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
     * @param lastClientTask the lastClientTask to set
     */
    void lastClientTask(final ClientTasksWrapper lastClientTask) {
        lastClientTaskRef = new WeakReference<>(lastClientTask);
    }

    @Override
    public int compareTo(final InsertedTagData<T> o) {
        return Long.compare(ordinal, o.ordinal);
    }

}
