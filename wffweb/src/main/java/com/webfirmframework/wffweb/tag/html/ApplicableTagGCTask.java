package com.webfirmframework.wffweb.tag.html;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Set;

import com.webfirmframework.wffweb.tag.html.SharedTagContent.ContentChangeListener;
import com.webfirmframework.wffweb.tag.html.SharedTagContent.DetachListener;

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
			final Map<Long, Set<DetachListener<T>>> detachListeners = sharedTagContent.detachListeners;
			if (detachListeners != null) {
				detachListeners.remove(applicableTagId);
			}
			final Map<Long, Set<ContentChangeListener<T>>> contentChangeListeners = sharedTagContent.contentChangeListeners;
			if (contentChangeListeners != null) {
				contentChangeListeners.remove(applicableTagId);
			}
			sharedTagContent.applicableTagGCTasksCache.remove(this);
			this.sharedTagContent = null;
		}

	}
}
