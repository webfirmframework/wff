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

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.StampedLock;

/**
 * NB: For future development. Currently it is only good for ReadWriteLock.
 *
 *
 * @author WFF
 * @since 3.0.15
 */
final class ReentrantStampedLock extends StampedLock {

    private static final long serialVersionUID = 1L;

    private final transient AtomicReference<Thread> lockHolder = new AtomicReference<>();

    private class ReadLockView implements Lock {

        private final Lock lck;

        private final AtomicInteger lockCount = new AtomicInteger(0);

        private ReadLockView() {
            lck = ReentrantStampedLock.super.asReadLock();
        }

        @Override
        public void lock() {
            final Thread currentThread = Thread.currentThread();
            final boolean lockable = !currentThread.equals(lockHolder.get());
            if (lockable) {
                if (lockCount.getAndIncrement() == 0) {
                    lck.lock();
                    lockHolder.getAndSet(currentThread);
                }

            }

        }

        @Override
        public void lockInterruptibly() throws InterruptedException {
            final Thread currentThread = Thread.currentThread();
            final boolean lockable = !currentThread.equals(lockHolder.get());
            if (lockable) {

                if (lockCount.getAndIncrement() == 0) {
                    try {
                        lck.lockInterruptibly();
                        lockHolder.getAndSet(currentThread);
                    } catch (final InterruptedException e) {
                        lockCount.decrementAndGet();
                        throw e;
                    }
                }

            }
        }

        @Override
        public Condition newCondition() {
            final boolean lockable = !Thread.currentThread().equals(lockHolder.get());
            if (lockable) {
                return lck.newCondition();
            }
            return null;
        }

        @Override
        public boolean tryLock() {
            final Thread currentThread = Thread.currentThread();
            final boolean lockable = !currentThread.equals(lockHolder.get());
            if (lockable) {
                final boolean locked = lck.tryLock();
                if (locked) {
                    if (lockCount.getAndIncrement() == 0) {
                        lockHolder.getAndSet(currentThread);
                    }

                }
                return locked;
            }
            return false;
        }

        @Override
        public boolean tryLock(final long time, final TimeUnit unit) throws InterruptedException {
            final Thread currentThread = Thread.currentThread();
            final boolean lockable = !currentThread.equals(lockHolder.get());
            if (lockable) {
                final boolean locked = lck.tryLock(time, unit);
                if (locked) {
                    if (lockCount.getAndIncrement() == 0) {
                        lockHolder.getAndSet(currentThread);
                    }
                }
                return locked;
            }
            return false;
        }

        @Override
        public void unlock() {
            final boolean lockable = Thread.currentThread().equals(lockHolder.get());
            if (lockable) {
                final int count = lockCount.decrementAndGet();
                if (count == 0) {
                    lockHolder.getAndSet(null);
                    lck.unlock();
                } else if (count < 0) {
                    lockCount.incrementAndGet();
                }

            }
        }

    }

    private class WriteLockView implements Lock {

        private final Lock lck;

        private final AtomicInteger lockCount = new AtomicInteger(0);

        private WriteLockView() {
            lck = ReentrantStampedLock.super.asWriteLock();
        }

        @Override
        public void lock() {
            final Thread currentThread = Thread.currentThread();
            final boolean lockable = !currentThread.equals(lockHolder.get());
            if (lockable) {
                if (lockCount.getAndIncrement() == 0) {
                    lck.lock();
                    lockHolder.getAndSet(currentThread);
                }

            }

        }

        @Override
        public void lockInterruptibly() throws InterruptedException {
            final Thread currentThread = Thread.currentThread();
            final boolean lockable = !currentThread.equals(lockHolder.get());
            if (lockable) {

                if (lockCount.getAndIncrement() == 0) {
                    try {
                        lck.lockInterruptibly();
                        lockHolder.getAndSet(currentThread);
                    } catch (final InterruptedException e) {
                        lockCount.decrementAndGet();
                        throw e;
                    }
                }

            }
        }

        @Override
        public Condition newCondition() {
            final boolean lockable = !Thread.currentThread().equals(lockHolder.get());
            if (lockable) {
                return lck.newCondition();
            }
            return null;
        }

        @Override
        public boolean tryLock() {
            final Thread currentThread = Thread.currentThread();
            final boolean lockable = !currentThread.equals(lockHolder.get());
            if (lockable) {
                final boolean locked = lck.tryLock();
                if (locked) {
                    if (lockCount.getAndIncrement() == 0) {
                        lockHolder.getAndSet(currentThread);
                    }

                }
                return locked;
            }
            return false;
        }

        @Override
        public boolean tryLock(final long time, final TimeUnit unit) throws InterruptedException {
            final Thread currentThread = Thread.currentThread();
            final boolean lockable = !currentThread.equals(lockHolder.get());
            if (lockable) {
                final boolean locked = lck.tryLock(time, unit);
                if (locked) {
                    if (lockCount.getAndIncrement() == 0) {
                        lockHolder.getAndSet(currentThread);
                    }
                }
                return locked;
            }
            return false;
        }

        @Override
        public void unlock() {
            final boolean lockable = Thread.currentThread().equals(lockHolder.get());
            if (lockable) {
                final int count = lockCount.decrementAndGet();
                if (count == 0) {
                    lockHolder.getAndSet(null);
                    lck.unlock();
                } else if (count < 0) {
                    lockCount.incrementAndGet();
                }

            }
        }

    }

    private class ReadWriteLockView implements ReadWriteLock {

        @Override
        public Lock readLock() {
            return asReadLock();
        }

        @Override
        public Lock writeLock() {
            return asWriteLock();
        }

    }

    ReentrantStampedLock() {
    }

    @Override
    public Lock asReadLock() {
        return new ReadLockView();
    }

    @Override
    public Lock asWriteLock() {
        return new WriteLockView();
    }

    @Override
    public ReadWriteLock asReadWriteLock() {
        return new ReadWriteLockView();
    }

}
