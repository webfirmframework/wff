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
package com.webfirmframework.wffweb.tag.html;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Set;

import com.webfirmframework.wffweb.internal.InternalId;
import com.webfirmframework.wffweb.tag.html.SharedTagContent.ContentChangeListener;
import com.webfirmframework.wffweb.tag.html.SharedTagContent.DetachListener;

class ApplicableTagGCTask<T> extends WeakReference<AbstractHtml> implements Runnable {

    private volatile SharedTagContent<T> stc;

    private final InternalId applicableTagId;

    ApplicableTagGCTask(final AbstractHtml referent, final ReferenceQueue<? super AbstractHtml> q,
            final SharedTagContent<T> stc) {
        super(referent, q);
        this.stc = stc;
        this.applicableTagId = referent.internalId();
    }

    @Override
    public void run() {
        final SharedTagContent<T> sharedTagContent = this.stc;
        if (sharedTagContent != null) {
            final Map<InternalId, Set<DetachListener<T>>> detachListeners = sharedTagContent.detachListeners;
            if (detachListeners != null) {
                detachListeners.remove(applicableTagId);
            }
            final Map<InternalId, Set<ContentChangeListener<T>>> contentChangeListeners = sharedTagContent.contentChangeListeners;
            if (contentChangeListeners != null) {
                contentChangeListeners.remove(applicableTagId);
            }
            sharedTagContent.applicableTagGCTasksCache.remove(this);
            this.stc = null;
        }

    }
}
