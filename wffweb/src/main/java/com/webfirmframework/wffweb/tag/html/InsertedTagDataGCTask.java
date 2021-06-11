/*
 * Copyright 2014-2021 Web Firm Framework
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

import com.webfirmframework.wffweb.internal.InternalId;

class InsertedTagDataGCTask<T> extends WeakReference<InsertedTagData<T>> implements Runnable {

	private volatile SharedTagContent<T> stc;

	private final InternalId insertedTagDataId;

	InsertedTagDataGCTask(final InsertedTagData<T> referent, final ReferenceQueue<? super InsertedTagData<T>> q,
	        final SharedTagContent<T> stc, final InternalId insertedTagDataId) {
		super(referent, q);
		this.stc = stc;
		this.insertedTagDataId = insertedTagDataId;
	}

	@Override
	public void run() {
		final SharedTagContent<T> sharedTagContent = this.stc;
		if (sharedTagContent != null) {
			sharedTagContent.contentFormatterByInsertedTagDataId.remove(insertedTagDataId);
			sharedTagContent.insertedTagDataGCTasksCache.remove(this);
			this.stc = null;
		}
	}
}
