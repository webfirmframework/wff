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
package com.webfirmframework.wffweb.concurrent;

import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.webfirmframework.wffweb.InvalidValueException;
import com.webfirmframework.wffweb.NullValueException;
import com.webfirmframework.wffweb.settings.WffConfiguration;

/**
 *
 * It makes sure that the task is invoked only once in the specified interval of
 * time even if the {@link MinIntervalExecutor#runAsync()} methods is called by
 * multiple threads less than the interval of specified time.
 *
 * @since 3.0.16
 */
public final class MinIntervalExecutor {

    private static final Logger LOGGER = Logger.getLogger(MinIntervalExecutor.class.getName());

    // there will be only one thread waiting for the lock so fairness must be
    // false and fairness may decrease the lock time
    // only one thread is allowed to gain lock so permit should be 1
    private final transient Semaphore taskQLock = new Semaphore(1, false);

    // ConcurrentLinkedQueue give better performance than ConcurrentLinkedDeque
    // on benchmark
    private final Queue<Runnable> taskQ = new ConcurrentLinkedQueue<>();

    private final Executor executor;

    private final long minInterval;

    private volatile long lastExecTime;

    private final Runnable task;

    /**
     * @param executor    the executor object to use thread from it.
     * @param minInterval in milliseconds. It keeps the minimum interval between the
     *                    task execution.
     * @param task        the Runnable object to execute.
     */
    public MinIntervalExecutor(final Executor executor, final long minInterval, final Runnable task) {
        this.executor = executor != null ? executor : WffConfiguration.getVirtualThreadExecutor();
        this.minInterval = minInterval;
        this.task = task;
        if (task == null) {
            throw new NullValueException("task cannot be null");
        }
        if (minInterval < 0) {
            throw new InvalidValueException("minInterval cannot be less than 0");
        }
    }

    /**
     * @param minInterval in milliseconds. It keeps the minimum interval between the
     *                    task execution.
     * @param task        the Runnable object to execute.
     */
    public MinIntervalExecutor(final long minInterval, final Runnable task) {
        executor = WffConfiguration.getVirtualThreadExecutor();
        this.minInterval = minInterval;
        this.task = task;
        if (task == null) {
            throw new NullValueException("task cannot be null");
        }
        if (minInterval < 0) {
            throw new InvalidValueException("minInterval cannot be less than 0");
        }
    }

    /**
     * Runs the task given in the constructor in an asynchronous mode.
     *
     * @since 3.0.16
     */
    public void runAsync() {
        runAsync(task);
    }

    private void runAsync(final Runnable runnable) {
        final long currentTime = System.currentTimeMillis();
        if ((currentTime - lastExecTime) >= minInterval) {
            if (taskQ.isEmpty()) {
                taskQ.offer(runnable);
            }
            if (!taskQ.isEmpty()) {
                if (executor != null) {
                    executor.execute(() -> executeTasksFromQ(currentTime));
                } else {
                    CompletableFuture.runAsync(() -> executeTasksFromQ(currentTime));
                }
            }
        }
    }

    /**
     * @since 3.0.16
     */
    private void executeTasksFromQ(final long currentTime) {

        // hasQueuedThreads internally uses transient volatile Node
        // so it must be fine for production use but
        // TODO verify it in deep if it is good for production
        if (!taskQLock.hasQueuedThreads() && !taskQ.isEmpty()) {

            taskQLock.acquireUninterruptibly();

            try {

                // wsPushInProgress must be implemented here and it is very
                // important because multiple threads should not process
                // simultaneously
                // from same taskQ which will cause incorrect
                // order of
                // execution

                Runnable task = taskQ.poll();
                if (task != null) {

                    do {

                        try {

                            if ((currentTime - lastExecTime) >= minInterval) {
                                // should be before
                                lastExecTime = currentTime;
                                task.run();
                            }

                        } catch (final Exception e) {
                            if (LOGGER.isLoggable(Level.SEVERE)) {
                                LOGGER.log(Level.SEVERE, "Could not process this data received from client.", e);
                            }
                        }

                        if (taskQLock.hasQueuedThreads()) {
                            break;
                        }

                        task = taskQ.poll();

                    } while (task != null);

                }

            } finally {
                taskQLock.release();
            }
        }
    }

    /**
     * @return the minInterval
     * @since 3.0.16
     */
    public long minInterval() {
        return minInterval;
    }

}
