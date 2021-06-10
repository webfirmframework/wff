package com.webfirmframework.wffweb.tag.html;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

import com.webfirmframework.wffweb.tag.htmlwff.NoTag;

class InsertedTagGCTask<T> extends WeakReference<AbstractHtml> implements Runnable {

	private volatile SharedTagContent<T> sharedTagContent;

	private final long insertedTagDataOrdinal;

	InsertedTagGCTask(final NoTag referent, final ReferenceQueue<? super AbstractHtml> q,
	        final SharedTagContent<T> sharedTagContent, final long insertedTagDataOrdinal) {
		super(referent, q);
		this.sharedTagContent = sharedTagContent;
		this.insertedTagDataOrdinal = insertedTagDataOrdinal;
	}

	@Override
	public void run() {
		final SharedTagContent<T> sharedTagContent = this.sharedTagContent;
		if (sharedTagContent != null) {
			sharedTagContent.contentFormatterByInsertedTagDataId.remove(insertedTagDataOrdinal);
			sharedTagContent.insertedTagGCTasksCache.remove(this);
			this.sharedTagContent = null;
		}
	}
}
