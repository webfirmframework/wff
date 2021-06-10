package com.webfirmframework.wffweb.tag.html;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

class ApplicableTagGCTask<T> extends WeakReference<AbstractHtml> implements Runnable {

	private volatile SharedTagContent<T> sharedTagContent;

	private final long applicableTagId;

	ApplicableTagGCTask(final AbstractHtml referent, final ReferenceQueue<? super AbstractHtml> q,
	        final SharedTagContent<T> sharedTagContent) {
		super(referent, q);
		this.sharedTagContent = sharedTagContent;
		this.applicableTagId = referent.getId();
	}

	@Override
	public void run() {
		final SharedTagContent<T> sharedTagContent = this.sharedTagContent;
		if (sharedTagContent != null) {
			sharedTagContent.detachListeners.remove(applicableTagId);
			sharedTagContent.contentChangeListeners.remove(applicableTagId);
			sharedTagContent.applicableTagGCTasksCache.remove(this);
			this.sharedTagContent = null;
		}

	}
}
