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
 * @author WFF
 */
package com.webfirmframework.wffweb.css.file;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.webfirmframework.wffweb.css.core.CssProperty;

/**
 *
 * @author WFF
 * @since 3.0.7
 */
class CssPropertySet extends LinkedHashSet<CssProperty> {

    private static final long serialVersionUID = 1L;

    /**
     * must be reentrant otherwise makes a deadlock when tested so StampedLock is
     * not applicable
     */
    private final transient ReadWriteLock lock = new ReentrantReadWriteLock(true);

    private final Map<String, CssProperty> cssPropertiesAsMap = new LinkedHashMap<>();

    private final AbstractCssFileBlock cssFileBlock;

    private volatile String cssString = "";

    public CssPropertySet(final AbstractCssFileBlock cssFileBlock) {
        this.cssFileBlock = cssFileBlock;
    }

    @Override
    public int size() {
        final Lock readLock = lock.readLock();
        readLock.lock();
        try {
            return super.size();
        } finally {
            readLock.unlock();

        }
    }

    @Override
    public boolean isEmpty() {
        final Lock readLock = lock.readLock();
        readLock.lock();
        try {
            return super.isEmpty();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean contains(final Object o) {
        final Lock readLock = lock.readLock();
        readLock.lock();
        try {
            return super.contains(o);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Iterator<CssProperty> iterator() {
        final Lock readLock = lock.readLock();
        readLock.lock();
        try {
            return super.iterator();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Object[] toArray() {
        final Lock readLock = lock.readLock();
        readLock.lock();
        try {
            return super.toArray();
        } finally {
            readLock.unlock();
        }
    }

    @SuppressWarnings("hiding")
    @Override
    public <CssProperty> CssProperty[] toArray(final CssProperty[] a) {
        final Lock readLock = lock.readLock();
        readLock.lock();
        try {
            return super.toArray(a);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean add(final CssProperty cssProperty) {
        final Lock writeLock = lock.writeLock();
        writeLock.lock();
        try {
            final boolean added = super.add(cssProperty);
            if (added) {
                cssFileBlock.setModified(added);
                cssPropertiesAsMap.put(cssProperty.getCssName(), cssProperty);
            }
            return added;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean remove(final Object o) {
        final Lock writeLock = lock.writeLock();
        writeLock.lock();

        try {
            final boolean removed = super.remove(o);
            if (removed) {
                cssFileBlock.setModified(removed);
                if (o instanceof CssProperty) {
                    cssPropertiesAsMap.remove(((CssProperty) o).getCssName());
                }
            }
            return removed;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
        final Lock readLock = lock.readLock();
        readLock.lock();
        try {
            return super.containsAll(c);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean addAll(final Collection<? extends CssProperty> cssProperties) {
        final Lock writeLock = lock.writeLock();
        writeLock.lock();
        try {
            final boolean addedAll = super.addAll(cssProperties);
            if (addedAll) {
                cssFileBlock.setModified(addedAll);
                for (final CssProperty cssProperty : cssProperties) {
                    cssPropertiesAsMap.put(cssProperty.getCssName(), cssProperty);
                }
            }
            return addedAll;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        final Lock writeLock = lock.writeLock();
        writeLock.lock();
        try {
            return super.retainAll(c);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        final Lock writeLock = lock.writeLock();
        writeLock.lock();
        try {
            final boolean removedAll = super.removeAll(c);
            if (removedAll) {
                cssFileBlock.setModified(removedAll);
                for (final Object object : c) {
                    if (object instanceof CssProperty) {
                        cssPropertiesAsMap.remove(((CssProperty) object).getCssName());
                    }
                }
            }
            return removedAll;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void clear() {
        final Lock writeLock = lock.writeLock();
        writeLock.lock();
        try {
            cssFileBlock.setModified(true);
            super.clear();
            cssPropertiesAsMap.clear();
        } finally {
            writeLock.unlock();
        }
    }

    String toCssString() {

        if (cssFileBlock.isModified()) {
            final int length = cssString.length();
            final StringBuilder cssStringBuilder = new StringBuilder(length > 0 ? length : 16);
            cssStringBuilder.delete(0, cssStringBuilder.length());

            final Lock readLock = lock.readLock();
            readLock.lock();
            try {

                // 'for (final CssProperty cssProperty : this)' here makes
                // java.lang.IllegalMonitorStateException
                // because we are explicitly locking
                // for (final CssProperty cssProperty : this) {
                // }
                // must be super.
                final Iterator<CssProperty> iterator = super.iterator();
                while (iterator.hasNext()) {
                    final CssProperty cssProperty = iterator.next();
                    cssStringBuilder.append(cssProperty.getCssName()).append(':').append(cssProperty.getCssValue())
                            .append(';');
                }

            } finally {
                readLock.unlock();
            }

            cssString = cssStringBuilder.toString();
            cssFileBlock.setModified(false);
        }
        return cssString;
    }

    Map<String, CssProperty> getCssPropertiesAsMap() {
        return cssPropertiesAsMap;
    }

    ReadWriteLock getLock() {
        return lock;
    }

    void clearLockless() {
        super.clear();
    }

}
