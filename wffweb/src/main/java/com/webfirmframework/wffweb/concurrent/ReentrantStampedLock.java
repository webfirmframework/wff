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

    private final AtomicInteger readLockCount = new AtomicInteger(0);

    private final AtomicInteger writeLockCount = new AtomicInteger(0);

    private final class ReadLockView implements Lock {

        private final Lock lck;

        private ReadLockView() {
            lck = ReentrantStampedLock.super.asReadLock();
        }

        @Override
        public void lock() {
            final Thread currentThread = Thread.currentThread();
            final boolean lockable = !currentThread.equals(lockHolder.get());
            if (lockable) {
                lck.lock();
                readLockCount.getAndIncrement();
                lockHolder.getAndSet(currentThread);
            } else {
                readLockCount.getAndIncrement();
            }

        }

        @Override
        public void lockInterruptibly() throws InterruptedException {
            final Thread currentThread = Thread.currentThread();
            final boolean lockable = !currentThread.equals(lockHolder.get());
            if (lockable) {
                lck.lockInterruptibly();
                readLockCount.getAndIncrement();
                lockHolder.getAndSet(currentThread);
            } else {
                readLockCount.getAndIncrement();
            }
        }

        @Override
        public Condition newCondition() {
            if (Thread.currentThread().equals(lockHolder.get())) {
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
                    readLockCount.getAndIncrement();
                    lockHolder.getAndSet(currentThread);
                }
                return locked;
            }

            readLockCount.getAndIncrement();
            return true;
        }

        @Override
        public boolean tryLock(final long time, final TimeUnit unit) throws InterruptedException {

            final Thread currentThread = Thread.currentThread();
            final boolean lockable = !currentThread.equals(lockHolder.get());
            if (lockable) {
                final boolean locked = lck.tryLock(time, unit);
                if (locked) {
                    readLockCount.getAndIncrement();
                    lockHolder.getAndSet(currentThread);
                }
                return locked;
            }

            readLockCount.getAndIncrement();
            return true;
        }

        @Override
        public void unlock() {
            final boolean unlockable = Thread.currentThread().equals(lockHolder.get());
            if (unlockable) {
                final int count = readLockCount.decrementAndGet();
                if (count == 0) {
                    lockHolder.getAndSet(null);
                    lck.unlock();
                }
            } else {
                // excess unlock should throw IllegalMonitorStateException
                lck.unlock();
            }
        }

    }

    private final class WriteLockView implements Lock {

        private final Lock lck;

        private WriteLockView() {
            lck = ReentrantStampedLock.super.asWriteLock();
        }

        @Override
        public void lock() {
            final Thread currentThread = Thread.currentThread();
            final boolean lockable = !currentThread.equals(lockHolder.get());
            if (lockable) {
                lck.lock();
                writeLockCount.getAndIncrement();
                lockHolder.getAndSet(currentThread);
            } else {
                if (readLockCount.get() > 0) {
                    lck.lock();
                    writeLockCount.getAndIncrement();
                    lockHolder.getAndSet(currentThread);
                } else {
                    writeLockCount.getAndIncrement();
                }
            }
        }

        @Override
        public void lockInterruptibly() throws InterruptedException {
            final Thread currentThread = Thread.currentThread();
            final boolean lockable = !currentThread.equals(lockHolder.get());
            if (lockable) {
                lck.lockInterruptibly();
                writeLockCount.getAndIncrement();
                lockHolder.getAndSet(currentThread);
            } else {
                writeLockCount.getAndIncrement();
            }
        }

        @Override
        public Condition newCondition() {
            if (Thread.currentThread().equals(lockHolder.get())) {
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
                    writeLockCount.getAndIncrement();
                    lockHolder.getAndSet(currentThread);
                }
                return locked;
            } else if (readLockCount.get() > 0) {
                final boolean locked = lck.tryLock();
                if (locked) {
                    writeLockCount.getAndIncrement();
                    lockHolder.getAndSet(currentThread);
                }
                return locked;
            }

            writeLockCount.getAndIncrement();
            return true;
        }

        @Override
        public boolean tryLock(final long time, final TimeUnit unit) throws InterruptedException {

            final Thread currentThread = Thread.currentThread();
            final boolean lockable = !currentThread.equals(lockHolder.get());
            if (lockable) {
                final boolean locked = lck.tryLock(time, unit);
                if (locked) {
                    writeLockCount.getAndIncrement();
                    lockHolder.getAndSet(currentThread);
                }
                return locked;
            } else if (readLockCount.get() > 0) {
                final boolean locked = lck.tryLock(time, unit);
                if (locked) {
                    writeLockCount.getAndIncrement();
                    lockHolder.getAndSet(currentThread);
                }
                return locked;
            }

            writeLockCount.getAndIncrement();
            return true;
        }

        @Override
        public void unlock() {
            final boolean unlockable = Thread.currentThread().equals(lockHolder.get());
            if (unlockable) {
                final int count = writeLockCount.decrementAndGet();
                if (count == 0) {
                    lockHolder.getAndSet(null);
                    lck.unlock();
                }
            } else {
                // excess unlock should throw IllegalMonitorStateException
                lck.unlock();
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
