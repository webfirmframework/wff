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

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ConcurrentModificationException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

import com.webfirmframework.wffweb.InvalidValueException;
import com.webfirmframework.wffweb.concurrent.MinIntervalExecutor;

/**
 * PayloadProcessor for BrowserPage WebSocket's incoming bytes
 *
 * @author WFF
 * @since 3.0.2
 */
public class PayloadProcessor implements Serializable {

    private static final long serialVersionUID = 1L;

    // ByteBuffer will be useful if we are planning for any memory optimization
    // for the saved bytes
    private final transient Queue<ByteBuffer> wsMessageChunks = new ConcurrentLinkedQueue<>();

    private final AtomicInteger wsMessageChunksTotalCapacity = new AtomicInteger(0);

    private final Semaphore inputBufferLimitLock;

    private final BrowserPage browserPage;

    private volatile boolean outOfMemoryError;

    public PayloadProcessor(final BrowserPage browserPage) {
        this.browserPage = browserPage;
        inputBufferLimitLock = browserPage.settings.inputBufferLimit() > 0
                ? new Semaphore(browserPage.settings.inputBufferLimit())
                : null;
    }

    /**
     * @param browserPage
     * @param singleThreaded to be used under single thread.
     * @since 3.0.3
     */
    public PayloadProcessor(final BrowserPage browserPage, final boolean singleThreaded) {
        this.browserPage = browserPage;
        inputBufferLimitLock = browserPage.settings.inputBufferLimit() > 0
                ? new Semaphore(browserPage.settings.inputBufferLimit())
                : null;
    }

    /**
     * Merges and returns byte array from the given dataArray.
     *
     * @param dataArray the ByteByffers to merge
     * @return a single ByteBuffer after flip merged from all ByteBuffer objects
     *         from dataArray.
     * @since 3.0.2
     */
    // for testing purpose the method visibility is changed to package level
    // (default)
    static byte[] pollAndConvertToByteArray(final int totalCapacity, final Queue<ByteBuffer> dataArray) {

        final byte[] wholeData = new byte[totalCapacity];

        int destStartIndex = 0;

        ByteBuffer data;
        while ((data = dataArray.poll()) != null) {
            final byte[] array = data.array();
            System.arraycopy(array, 0, wholeData, destStartIndex, array.length);
            destStartIndex += array.length;
            if (destStartIndex > totalCapacity) {
                throw new ConcurrentModificationException(
                        "PayloadProcessor.webSocketMessaged method is NOT allowed to call more than one thread at a time.");
            }
        }

        return wholeData;
    }

    /**
     * This method will be useful when the WebSocket server receives messages as
     * chucks. A WebSocket server may have a max size of byte array that can be sent
     * or receive as a single object. eg: websocket
     * session.getMaxBinaryMessageBufferSize may limit it. In such case this method
     * can be used to get the complete data as chucks.
     *
     * @param messagePart message part
     * @param last        true if it is the last part of the message
     * @since 3.0.2
     */
    public void webSocketMessaged(final ByteBuffer messagePart, final boolean last) {
        if (outOfMemoryError) {
            throw new OutOfMemoryError(
                    """
                            The payloads received from client cannot be processed due to OutOfMemoryError, the previous payload size was more than the allowed size of %s.
                             Increase the value of browserPage.settings.inputBufferLimit to fix this error.
                             Override useSettings method in the subclass of BrowserPage to set a new values browserPage.settings."""
                            .formatted(browserPage.settings.inputBufferLimit()));
        }

        // As per javadoc, inputBufferLimitLock.availablePermits() is typically used for
        // debugging and testing purposes so avoided using it
        if (last && wsMessageChunksTotalCapacity.get() == 0) {
            if (inputBufferLimitLock != null) {
                if (messagePart.capacity() > browserPage.settings.inputBufferLimit()) {
                    outOfMemoryError = true;
                    throw getOutOfMemoryError(messagePart.capacity());
                }
                if (inputBufferLimitLock.tryAcquire(messagePart.capacity())) {
                    try {
                        browserPage.webSocketMessaged(messagePart.array());
                    } finally {
                        inputBufferLimitLock.release(messagePart.capacity());
                    }
                } else {
                    outOfMemoryError = true;
                    throw getOutOfMemoryError(messagePart.capacity());
                }
            } else {
                browserPage.webSocketMessaged(messagePart.array());
            }
        } else {
            if (inputBufferLimitLock != null) {
                if (inputBufferLimitLock.tryAcquire(messagePart.capacity())) {
                    transferToBrowserPageWS(messagePart, last);
                } else {
                    outOfMemoryError = true;
                    throw getOutOfMemoryError(messagePart.capacity());
                }
            } else {
                transferToBrowserPageWS(messagePart, last);
            }
        }
    }

    private OutOfMemoryError getOutOfMemoryError(final int messagePartSize) {
        return new OutOfMemoryError(
                """
                        The client payload size is more than the allowed size of %s specified by browserPage.settings.inputBufferLimit so further payloads received from client will not be processed.
                         Increase the value of browserPage.settings.inputBufferLimit, it should be greater than or equal to a value of %s to fix this error.
                         Override useSettings method in the subclass of BrowserPage to set a new values browserPage.settings."""
                        .formatted(browserPage.settings.inputBufferLimit(), messagePartSize));
    }

    /**
     * @param messagePart
     * @param last
     * @since 3.0.3
     */
    private void transferToBrowserPageWS(final ByteBuffer messagePart, final boolean last) {
        // if lossless communication is enabled
        if (wsMessageChunks.isEmpty()) {
            final byte[] message = messagePart.array();
            if (!browserPage.checkLosslessCommunication(message)) {
                if (inputBufferLimitLock != null) {
                    inputBufferLimitLock.release(message.length);
                }
                return;
            }
        }

        if (last) {
            wsMessageChunks.add(messagePart);
            final int totalCapacity = wsMessageChunksTotalCapacity.getAndSet(0) + messagePart.capacity();
            final byte[] message = pollAndConvertToByteArray(totalCapacity, wsMessageChunks);
            if (inputBufferLimitLock != null) {
                try {
                    browserPage.webSocketMessagedWithoutLosslessCheck(message);
                } finally {
                    inputBufferLimitLock.release(message.length);
                }
            } else {
                browserPage.webSocketMessagedWithoutLosslessCheck(message);
            }
        } else {
            wsMessageChunks.add(messagePart);
            wsMessageChunksTotalCapacity.addAndGet(messagePart.capacity());
        }
    }

    /**
     * Note: this method is relevant only if the heartbeat is enabled by
     * {@link BrowserPage#setWebSocketHeartbeatInterval(int)}.
     *
     * @param maxIdleTimeout The expected value is in milliseconds. It should be
     *                       greater than zero otherwise it will throw an
     *                       {@code InvalidValueException}.
     * @return true if the BrowserPage associated with this PayloadProcessor is not
     *         expired.
     * @since 12.0.1
     */
    public final boolean isValid(final long maxIdleTimeout) {
        if (maxIdleTimeout <= 0) {
            throw new InvalidValueException("maxIdleTimeout should be greater than 0.");
        }
        if (browserPage.wsHeartbeatInterval > 0) {
            return (System.currentTimeMillis() - browserPage.lastClientAccessedTime) < maxIdleTimeout;
        }
        return true;
    }

    /**
     * Note: this method is relevant only if the heartbeat is enabled by
     * {@link BrowserPage#setWebSocketHeartbeatInterval(int)} and the auto clean is
     * enabled in BrowserPageContext by any
     * {@link BrowserPageContext#enableAutoClean} methods.
     *
     * @return true if the BrowserPage associated with this PayloadProcessor is not
     *         expired.
     * @since 12.0.1
     */
    public final boolean isValid() {
        if (browserPage.wsHeartbeatInterval > 0) {
            final MinIntervalExecutor autoCleanTaskExecutor = BrowserPageContext.INSTANCE.autoCleanTaskExecutor;
            if (autoCleanTaskExecutor != null) {
                return (System.currentTimeMillis() - browserPage.lastClientAccessedTime) < autoCleanTaskExecutor
                        .minInterval();
            }
        }
        return true;
    }
}
