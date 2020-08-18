/*
 * Copyright 2014-2020 Web Firm Framework
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
package com.webfirmframework.wffweb.concurrent;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.StampedLock;

/**
 * By default it is reentrant. NB: This is an experimental class it may not
 * exists in future release. It is not deeply tested. Event it is not making any
 * bug while running test cases it makes a deadlock bug in the production app so
 * it is not ready for use.
 *
 * @author WFF
 * @since 3.0.15
 */
final class WffReadWriteLock implements ReadWriteLock {

    private final Lock readLock;

    private final Lock writeLock;

    public WffReadWriteLock() {
        this(true);
    }

    public WffReadWriteLock(final boolean reentrant) {
        final ReadWriteLock readWriteLock = reentrant ? new ReentrantStampedLock().asReadWriteLock()
                : new StampedLock().asReadWriteLock();
        readLock = readWriteLock.readLock();
        writeLock = readWriteLock.writeLock();
    }

    @Override
    public Lock readLock() {
        return readLock;
    }

    @Override
    public Lock writeLock() {
        return writeLock;
    }

}
