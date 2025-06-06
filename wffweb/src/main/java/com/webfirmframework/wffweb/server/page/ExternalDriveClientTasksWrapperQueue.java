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
package com.webfirmframework.wffweb.server.page;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.webfirmframework.wffweb.MethodNotImplementedException;
import com.webfirmframework.wffweb.util.FileUtil;
import com.webfirmframework.wffweb.util.WffBinaryMessageUtil;
import com.webfirmframework.wffweb.util.data.NameValue;

/**
 * Note: Designed only for {@code BrowserPage} requirements, all methods are not
 * implemented and some methods are partially implemented just to satisfy the
 * requirement.
 *
 * @author WFF
 * @since 3.0.18
 *
 */
class ExternalDriveClientTasksWrapperQueue implements Queue<ClientTasksWrapper> {

    private static final Logger LOGGER = Logger.getLogger(ExternalDriveClientTasksWrapperQueue.class.getName());

    private static final String fileNamePrefix = "";

    private static final String fileNameSuffix = ".wff.data";

    private final AtomicLong readId = new AtomicLong();

    private final AtomicLong writeId = new AtomicLong();

    private final Map<Long, Boolean> writeIdInProgressStates = new ConcurrentHashMap<>(1);

    private final Semaphore mapLock = new Semaphore(1, true);

    private final String basePath;

    private final String dirName;

    private final String subDirName;

    ExternalDriveClientTasksWrapperQueue(final String basePath, final String dirName, final String subDirName)
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
    public ClientTasksWrapper poll() {
        long rId;
        while ((rId = readId.get()) < writeId.get()) {
            final long newReadId = rId + 1L;

            if (writeIdInProgressStates.get(newReadId) != null) {
                // writing is inprogress so return null
                return null;
            }

            if (readId.compareAndSet(rId, newReadId)) {
                return pollByReadId(newReadId);
            }
        }

        return null;
    }

    ClientTasksWrapper pollLast() {
        // NB: it is not possible to atomically poll from last so don't use this method
        // it will make bug
        long rId;
        long wId = writeId.get();
        while ((rId = readId.get()) < wId) {
            final long newWriteId = wId - 1L;

            final long toCompare = wId;
            final long rIdFinal = rId;
            final long currentValue = writeId.accumulateAndGet(newWriteId, (existing, newValue) -> {
                // null means no writing in progress
                if (existing == toCompare && writeIdInProgressStates.get(newWriteId) == null
                        && readId.get() == rIdFinal) {
                    return newValue;
                }

                return existing;
            });

            if (currentValue == newWriteId) {
                return pollByReadId(wId);
            } else {
                wId = newWriteId;
            }

        }

        return null;
    }

    ClientTasksWrapper pollByReadId(final long id) {
        if (writeIdInProgressStates.get(id) != null) {
            // writing is inprogress so return null
            return null;
        }
        final Path filePath = Paths.get(basePath, dirName, subDirName, fileNamePrefix + id + fileNameSuffix);
        if (Files.exists(filePath)) {

            try {

                final List<NameValue> nameValues = WffBinaryMessageUtil.VERSION_1.parse(Files.readAllBytes(filePath));
                final NameValue nameValue = nameValues.get(0);

                final byte[][] byteArrayTasks = nameValue.getValues();

                final ByteBuffer[] byteBufferTasks = new ByteBuffer[byteArrayTasks.length];
                for (int i = 0; i < byteArrayTasks.length; i++) {
                    byteBufferTasks[i] = ByteBuffer.wrap(byteArrayTasks[i]);
                }

                Files.deleteIfExists(filePath);

                return new ClientTasksWrapper(id, byteBufferTasks);
            } catch (final IOException e) {
                // NOP
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
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
    public boolean offer(final ClientTasksWrapper tasksWrapper) {
        final long newWId = generateWriteId();
        return offerAt(tasksWrapper, newWId);
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

    void writingInProgress(final long id) {
        writeIdInProgressStates.put(id, true);
    }

    @Override
    public boolean add(final ClientTasksWrapper tasksWrapper) {
        final long newWId = generateWriteId();
        return offerAt(tasksWrapper, newWId);
    }

    boolean offerAt(final ClientTasksWrapper tasksWrapper, final long id) {

        final AtomicReferenceArray<ByteBuffer> tasks = tasksWrapper.tasks();

        final int length = tasks.length();

        final byte[][] byteArrayTasks = new byte[length][0];

        for (int i = 0; i < length; i++) {
            byteArrayTasks[i] = tasks.get(i).array();
        }

        final Path filePath = Paths.get(basePath, dirName, subDirName, fileNamePrefix + id + fileNameSuffix);

        final NameValue nameValue = new NameValue();
        nameValue.setName(new byte[0]);
        nameValue.setValues(byteArrayTasks);

        try {
            Files.write(filePath, WffBinaryMessageUtil.VERSION_1.getWffBinaryMessageBytes(nameValue));
            writeIdInProgressStates.remove(id);
            return true;
        } catch (final IOException e) {
            // NOP
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        return false;
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
        clearInverse();
    }

    @SuppressWarnings("unused")
    private void clearFromFirstToLast() {
        // old clear() method implementation
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

    void clearInverse() {
        long rId;
        long wId;
        while ((rId = readId.get()) < (wId = writeId.get())) {
            if (writeIdInProgressStates.get(rId) == null && readId.compareAndSet(rId, wId)) {
                for (long i = rId; i < wId; i++) {
                    deleteByReadId(i + 1L);
                }
            }
        }
    }

    @Override
    public boolean addAll(final Collection<? extends ClientTasksWrapper> taskWrappers) {
        throw new MethodNotImplementedException();
    }

    @Override
    public boolean contains(final Object o) {
        throw new MethodNotImplementedException();
    }

    @Override
    public Iterator<ClientTasksWrapper> iterator() {
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
    public boolean removeAll(final Collection<?> c) {
        throw new MethodNotImplementedException();
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        throw new MethodNotImplementedException();
    }

    @Override
    public ClientTasksWrapper remove() {
        throw new MethodNotImplementedException();
    }

    @Override
    public ClientTasksWrapper element() {
        throw new MethodNotImplementedException();
    }

    @Override
    public ClientTasksWrapper peek() {
        throw new MethodNotImplementedException();
    }

}
