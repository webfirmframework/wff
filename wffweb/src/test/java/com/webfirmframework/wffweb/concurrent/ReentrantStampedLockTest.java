package com.webfirmframework.wffweb.concurrent;

import static org.junit.Assert.*;

import java.util.concurrent.locks.ReadWriteLock;

import org.junit.Test;

public class ReentrantStampedLockTest {

    @Test
    public void testReentrantStampedLock() {
//        fail("Not yet implemented");
        // TODO incomplete test case

        ReentrantStampedLock stampedLock = new ReentrantStampedLock();

        ReadWriteLock readWriteLock = stampedLock.asReadWriteLock();
        readWriteLock.writeLock().lock();
        readWriteLock.writeLock().lock();
        readWriteLock.writeLock().unlock();
        readWriteLock.writeLock().unlock();
    }

}
