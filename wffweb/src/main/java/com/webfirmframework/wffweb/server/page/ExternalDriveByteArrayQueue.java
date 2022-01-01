/*
 * Copyright 2014-2022 Web Firm Framework
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
package com.webfirmframework.wffweb.server.page;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.webfirmframework.wffweb.MethodNotImplementedException;
import com.webfirmframework.wffweb.util.FileUtil;

/**
 * Note: Designed only for {@code BrowserPage} requirements, all methods are not
 * implemented and some methods are partially implemented just to satisfy the
 * requirement.
 *
 * @author WFF
 * @since 3.0.18
 *
 */
class ExternalDriveByteArrayQueue implements Queue<byte[]> {

    private static final Logger LOGGER = Logger.getLogger(ExternalDriveByteArrayQueue.class.getName());

    private static final String fileNamePrefix = "";

    private static final String fileNameSuffix = ".wff.data";

    private final AtomicLong readId = new AtomicLong();

    private final AtomicLong writeId = new AtomicLong();

    private final Map<Long, Boolean> writeIdInProgressStates = new ConcurrentHashMap<>(1);

    private final Semaphore mapLock = new Semaphore(1, true);

    private final String basePath;

    private final String dirName;

    private final String subDirName;

    ExternalDriveByteArrayQueue(final String basePath, final String dirName, final String subDirName)
            throws IOException {
        this.basePath = basePath;
        this.dirName = dirName;
        this.subDirName = subDirName;
        createInitialDirStructure();
    }

    private boolean createInitialDirStructure() throws IOException {
        final Path dirPath = Paths.get(basePath, dirName, subDirName);
        if (Files.notExists(dirPath)) {
            Files.createDirectories(dirPath);
            return true;
        }
        return false;
    }

    void deleteBaseDirStructure() {
        FileUtil.removeDirRecursively(basePath, dirName);
    }

    @Override
    public byte[] poll() {
        long rId;
        while ((rId = readId.get()) < writeId.get()) {
            final long newReadId = rId + 1L;

            if (writeIdInProgressStates.get(newReadId) != null) {
                // writing is inprogress so return null
                return null;
            }

            if (readId.compareAndSet(rId, newReadId)) {
                final Path filePath = Paths.get(basePath, dirName, subDirName,
                        fileNamePrefix + newReadId + fileNameSuffix);
                if (Files.exists(filePath)) {
                    try {
                        final byte[] bytes = Files.readAllBytes(filePath);
                        Files.deleteIfExists(filePath);
                        return bytes;
                    } catch (final IOException e) {
                        // NOP
                        LOGGER.log(Level.SEVERE, e.getMessage(), e);
                    }
                }
            }
        }

        return null;
    }

    boolean deleteByReadId(final long id) {
        final Path filePath = Paths.get(basePath, dirName, subDirName, fileNamePrefix + id + fileNameSuffix);
        try {
            return Files.deleteIfExists(filePath);
        } catch (final IOException e) {
            // NOP
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return false;
    }

    @Override
    public boolean offer(final byte[] bytes) {

        final long newWId = generateWriteId();
        final Path filePath = Paths.get(basePath, dirName, subDirName, fileNamePrefix + newWId + fileNameSuffix);

        try {
            Files.write(filePath, bytes);
            writeIdInProgressStates.remove(newWId);
            return true;
        } catch (final IOException e) {
            // NOP
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return false;
    }

    private long generateWriteId() {

        mapLock.acquireUninterruptibly();

        try {
            long newWId;
            boolean idAvailable;
            do {

                idAvailable = false;

                final long lastWId = writeId.get();

                newWId = lastWId + 1L;

                if (writeIdInProgressStates.get(newWId) == null) {
                    writeIdInProgressStates.put(newWId, true);
                    idAvailable = writeId.compareAndSet(lastWId, newWId);
                    if (!idAvailable) {
                        writeIdInProgressStates.remove(newWId);
                    }
                }

            } while (!idAvailable);

            return newWId;
        } finally {
            mapLock.release();
        }

    }

    @Override
    public boolean add(final byte[] bytes) {
        return offer(bytes);
    }

    @Override
    public boolean isEmpty() {
        return size() == 0 && writeIdInProgressStates.isEmpty();
    }

    @Override
    public int size() {
        return (int) (writeId.get() - readId.get());
    }

    @Override
    public void clear() {
        long rId;
        while ((rId = readId.get()) < writeId.get()) {
            final long newReadId = rId + 1L;
            if (writeIdInProgressStates.get(newReadId) != null) {
                // writing is inprogress so break
                break;
            }
            if (readId.compareAndSet(rId, newReadId)) {
                deleteByReadId(newReadId);
            }
        }
    }

    @Override
    public boolean contains(final Object o) {
        throw new MethodNotImplementedException();
    }

    @Override
    public Iterator<byte[]> iterator() {
        throw new MethodNotImplementedException();
    }

    @Override
    public Object[] toArray() {
        throw new MethodNotImplementedException();
    }

    @Override
    public <T> T[] toArray(final T[] a) {
        throw new MethodNotImplementedException();
    }

    @Override
    public boolean remove(final Object o) {
        throw new MethodNotImplementedException();
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
        throw new MethodNotImplementedException();
    }

    @Override
    public boolean addAll(final Collection<? extends byte[]> c) {
        throw new MethodNotImplementedException();
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        throw new MethodNotImplementedException();
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        throw new MethodNotImplementedException();
    }

    @Override
    public byte[] remove() {
        throw new MethodNotImplementedException();
    }

    @Override
    public byte[] element() {
        throw new MethodNotImplementedException();
    }

    @Override
    public byte[] peek() {
        throw new MethodNotImplementedException();
    }

}
