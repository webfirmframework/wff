/*
 * Copyright 2014-2023 Web Firm Framework
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
package com.webfirmframework.wffweb.concurrent;

import static org.junit.Assert.*;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.junit.Assert;
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
    
    @Test
    public void testReentrantStampedLockComparingWithReentrantReadWriteLock1() {
        ReentrantStampedLock stampedLock = new ReentrantStampedLock();
        ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();
        
        Lock wffWriteLock = stampedLock.asWriteLock();
        wffWriteLock.lock();
        
        WriteLock writeLock = reentrantReadWriteLock.writeLock();
        writeLock.lock();
        
        assertEquals(writeLock.tryLock(), wffWriteLock.tryLock());
        
        Lock wffReadLock = stampedLock.asReadLock();
        wffReadLock.lock();
        
        ReadLock readLock = reentrantReadWriteLock.readLock();
        readLock.lock();
        
        assertEquals(readLock.tryLock(), wffReadLock.tryLock());
        
    }
    
    @Test
    public void testReentrantStampedLockComparingWithReentrantReadWriteLock2() {
        ReentrantStampedLock stampedLock = new ReentrantStampedLock();
        ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();
        
        Lock wffWriteLock = stampedLock.asWriteLock();
        wffWriteLock.lock();
        
        WriteLock writeLock = reentrantReadWriteLock.writeLock();
        writeLock.lock();
        
        boolean tryLock = writeLock.tryLock();
        assertTrue(tryLock);
        assertEquals(tryLock, wffWriteLock.tryLock());
        
        Lock wffReadLock = stampedLock.asReadLock();
        wffReadLock.lock();
        
        ReadLock readLock = reentrantReadWriteLock.readLock();
        readLock.lock();
        
        boolean tryLock1 = readLock.tryLock();
        assertTrue(tryLock1);
        assertEquals(tryLock1, wffReadLock.tryLock());
        
        readLock.unlock();
        wffReadLock.unlock();
        
        assertEquals(readLock.tryLock(), wffReadLock.tryLock());
        
        readLock.unlock();
        wffReadLock.unlock();
        
        writeLock.unlock();
        wffWriteLock.unlock();
        boolean tryLock2 = writeLock.tryLock();
        assertTrue(tryLock2);
        
        //TODO verify later. 
        //It will not satisfy  because no. of locks > no.unlocks
//        assertEquals(tryLock2, wffWriteLock.tryLock());
        writeLock.unlock();
        wffWriteLock.unlock();
        
        //will satisfy because no. of locks = no.unlocks
        assertEquals(writeLock.tryLock(), wffWriteLock.tryLock());
    }
    
    @Test
    public void lockNotAvailableCase1() {
        ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();
        ReentrantStampedLock stampedLock = new ReentrantStampedLock();

        ReadLock readLock = reentrantReadWriteLock.readLock();
        readLock.lock();

        WriteLock writeLock = reentrantReadWriteLock.writeLock();

        Lock wffReadLock = stampedLock.asReadLock();
        wffReadLock.lock();

        Lock wffWriteLock = stampedLock.asWriteLock();

        boolean tryLock = writeLock.tryLock();

        assertFalse(tryLock);
        boolean wffTryLock = wffWriteLock.tryLock();
        assertFalse(wffTryLock);

        assertEquals(tryLock, wffTryLock);
    }
    
    @Test(expected = IllegalMonitorStateException.class)
    public void exceptionSampleWithSameThreadReentrantReadWriteLock1() {
        ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();

        Lock writeLock = reentrantReadWriteLock.writeLock();
        writeLock.lock();
        writeLock.lock();
        writeLock.unlock();
        writeLock.unlock();
        
        //throws IllegalMonitorStateException 
        writeLock.unlock();        
    }
    
    @Test(expected = IllegalMonitorStateException.class)
    public void exceptionSampleWithSameThreadReentrantReadWriteLock2() {
        ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();

        Lock readLock = reentrantReadWriteLock.readLock();
        readLock.lock();
        readLock.lock();
        readLock.unlock();
        readLock.unlock();
        
        //throws IllegalMonitorStateException 
        readLock.unlock();        
    }
    
    @Test(expected = IllegalMonitorStateException.class)
    public void exceptionSampleWithSameThreadReentrantReadWriteLock3() {
        ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();

        Lock writeLock = reentrantReadWriteLock.writeLock();
        
        //throws IllegalMonitorStateException 
        writeLock.unlock();        
    }
    
    @Test(expected = IllegalMonitorStateException.class)
    public void exceptionSampleWithSameThreadReentrantReadWriteLock4() {
        ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();

        Lock readLock = reentrantReadWriteLock.readLock();
        
        //throws IllegalMonitorStateException 
        readLock.unlock();        
    }
    
    
    
    @Test(expected = IllegalMonitorStateException.class)
    public void exceptionSampleWithSameThreadReentrantStampedLock1() {
        ReentrantStampedLock stampedLock = new ReentrantStampedLock();

        Lock writeLock = stampedLock.asWriteLock();
        writeLock.lock();
        writeLock.lock();
        writeLock.unlock();
        writeLock.unlock();
        
        //throws IllegalMonitorStateException 
        writeLock.unlock();        
    }
    
    @Test(expected = IllegalMonitorStateException.class)
    public void exceptionSampleWithSameThreadReentrantStampedLock2() {
        ReentrantStampedLock stampedLock = new ReentrantStampedLock();

        Lock readLock = stampedLock.asReadLock();
        readLock.lock();
        readLock.lock();
        readLock.unlock();
        readLock.unlock();
        
        //throws IllegalMonitorStateException 
        readLock.unlock();              
    }
    
    @Test(expected = IllegalMonitorStateException.class)
    public void exceptionSampleWithSameThreadReentrantStampedLock3() {
        ReentrantStampedLock stampedLock = new ReentrantStampedLock();

        Lock writeLock = stampedLock.asWriteLock();
        writeLock.lock();
        writeLock.lock();
        writeLock.unlock();
        writeLock.unlock();
        
        //throws IllegalMonitorStateException 
        writeLock.unlock();        
    }
    
    @Test(expected = IllegalMonitorStateException.class)
    public void exceptionSampleWithSameThreadReentrantStampedLock4() {
        ReentrantStampedLock stampedLock = new ReentrantStampedLock();

        Lock readLock = stampedLock.asReadLock();
        readLock.lock();
        readLock.lock();
        readLock.unlock();
        readLock.unlock();
        
        //throws IllegalMonitorStateException 
        readLock.unlock();              
    }
    

    //this is a deadlock sample
    public void deadlockSampleWithSameThreadReentrantReadWriteLock() {
        ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();
        
        ReadLock readLock = reentrantReadWriteLock.readLock();
        readLock.lock();
        
        WriteLock writeLock = reentrantReadWriteLock.writeLock();
        writeLock.lock();        
    }
    
    //this is a deadlock sample
    public void deadlockSampleWithSameThreadReentrantStampedLock() {
        ReentrantStampedLock stampedLock = new ReentrantStampedLock();
        
        Lock readLock = stampedLock.asReadLock();
        readLock.lock();
        Lock writeLock = stampedLock.asWriteLock();
        writeLock.lock();        
    }

}
