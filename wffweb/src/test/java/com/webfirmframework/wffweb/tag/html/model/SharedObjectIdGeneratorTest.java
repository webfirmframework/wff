/*
 * Copyright since 2014 Web Firm Framework
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
package com.webfirmframework.wffweb.tag.html.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import com.webfirmframework.wffweb.internal.ObjectId;
import org.junit.Test;


public class SharedObjectIdGeneratorTest {

    @Test
    public void testNextId() throws InterruptedException, ExecutionException {
        
        Set<Object> ids = ConcurrentHashMap.newKeySet();
        ExecutorService threadPool = Executors.newCachedThreadPool();
        
        List<CompletableFuture<Void>> tasks = new ArrayList<>(1000);
        
        AtomicBoolean alreadyExists = new AtomicBoolean(false);
        
        for (int i = 0; i < 1000; i++) {
            CompletableFuture<Void> task = CompletableFuture.runAsync(() -> {
                final String id = SharedObjectIdGenerator.nextId().id();
                
                alreadyExists.set(alreadyExists.get() || ids.contains(id));
                
                ids.add(id);
            }, threadPool);
            
            tasks.add(task);
        }
        for (CompletableFuture<Void> task : tasks) {
            task.get();
        }
        if (alreadyExists.get()) {
            fail("SharedObjectIdGenerator.nextId generated a duplicate value");
        }
        
    }

    @Test
    public void testCompareTo() {
        final ObjectId objectId1 = SharedObjectIdGenerator.nextId();
        final ObjectId objectId2 = SharedObjectIdGenerator.nextId();
        assertEquals(1, objectId2.compareTo(objectId1));
        assertEquals(-1, objectId1.compareTo(objectId2));
        assertEquals(0, objectId1.compareTo(objectId1));
        assertEquals(0, objectId2.compareTo(objectId2));
    }

}
