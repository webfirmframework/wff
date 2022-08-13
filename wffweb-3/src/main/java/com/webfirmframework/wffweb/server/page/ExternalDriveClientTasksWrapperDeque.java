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
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentLinkedDeque;

import com.webfirmframework.wffweb.InvalidValueException;
import com.webfirmframework.wffweb.MethodNotImplementedException;

/**
 * Note: Designed only for {@code BrowserPage} requirements, all methods are not
 * implemented and some methods are partially implemented just to satisfy the
 * requirement.
 *
 * @author WFF
 * @since 3.0.18
 *
 */
class ExternalDriveClientTasksWrapperDeque extends ExternalDriveClientTasksWrapperQueue
        implements Deque<ClientTasksWrapper> {

    private final Deque<Long> firstUnreadIds = new ConcurrentLinkedDeque<>();

    ExternalDriveClientTasksWrapperDeque(final String basePath, final String dirName, final String subDirName)
            throws IOException {
        super(basePath, dirName, subDirName);
    }

    @Override
    public ClientTasksWrapper poll() {

        final Long unreadId = firstUnreadIds.poll();
        if (unreadId != null) {
            return super.pollByReadId(unreadId);
        }

        return super.poll();
    }

    @Override
    public ClientTasksWrapper pollFirst() {
        return poll();
    }

    @Override
    public boolean offerLast(final ClientTasksWrapper tasksWrapper) {
        return super.offer(tasksWrapper);
    }

    @Override
    public boolean offerFirst(final ClientTasksWrapper tasksWrapper) {
        final Long queueEntryId = tasksWrapper.queueEntryId();
        if (queueEntryId == null) {
            throw new InvalidValueException("ClientTasksWrapper doesn't contain queueEntryId property");
        }
        super.writingInProgress(queueEntryId);
        firstUnreadIds.offerFirst(queueEntryId);
        return super.offerAt(tasksWrapper, queueEntryId);
    }

    @Override
    public void addFirst(final ClientTasksWrapper tasksWrapper) {
        offerFirst(tasksWrapper);
    }

    @Override
    public void addLast(final ClientTasksWrapper tasksWrapper) {
        super.offer(tasksWrapper);
    }

    @Override
    public boolean isEmpty() {
        return super.isEmpty() && firstUnreadIds.isEmpty();
    }

    @Override
    public int size() {
        return super.size() + firstUnreadIds.size();
    }

    @Override
    public ClientTasksWrapper pop() {
        final ClientTasksWrapper e = poll();
        if (e == null) {
            throw new NoSuchElementException();
        }
        return e;
    }

    @Override
    public void clear() {

        Long unreadId;
        while ((unreadId = firstUnreadIds.poll()) != null) {
            deleteByReadId(unreadId);
        }

        super.clear();
    }

    @Override
    public ClientTasksWrapper removeFirst() {
        throw new MethodNotImplementedException();
    }

    @Override
    public ClientTasksWrapper removeLast() {
        throw new MethodNotImplementedException();
    }

    @Override
    public ClientTasksWrapper pollLast() {
        throw new MethodNotImplementedException();
    }

    @Override
    public ClientTasksWrapper getFirst() {
        throw new MethodNotImplementedException();
    }

    @Override
    public ClientTasksWrapper getLast() {
        throw new MethodNotImplementedException();
    }

    @Override
    public ClientTasksWrapper peekFirst() {
        throw new MethodNotImplementedException();
    }

    @Override
    public ClientTasksWrapper peekLast() {
        throw new MethodNotImplementedException();
    }

    @Override
    public boolean removeFirstOccurrence(final Object o) {
        throw new MethodNotImplementedException();
    }

    @Override
    public boolean removeLastOccurrence(final Object o) {
        throw new MethodNotImplementedException();
    }

    @Override
    public void push(final ClientTasksWrapper e) {
        throw new MethodNotImplementedException();
    }

    @Override
    public Iterator<ClientTasksWrapper> descendingIterator() {
        throw new MethodNotImplementedException();
    }

}
