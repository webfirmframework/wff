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
package com.webfirmframework.wffweb.server.page;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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

    private final Semaphore inputBufferLimit;

    private final BrowserPage browserPage;

    private final boolean singleThreaded;

    private final transient Lock commonLock;

    public PayloadProcessor(final BrowserPage browserPage) {
        this.browserPage = browserPage;
        singleThreaded = false;
        commonLock = singleThreaded ? null : new ReentrantLock(true);
        inputBufferLimit = browserPage.settings.inputBufferLimit() > 0
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
        this.singleThreaded = singleThreaded;
        commonLock = singleThreaded ? null : new ReentrantLock(true);
        inputBufferLimit = browserPage.settings.inputBufferLimit() > 0
                ? new Semaphore(browserPage.settings.inputBufferLimit())
                : null;
    }

    /**
     * Merges and returns byte array from the given dataArray.
     *
     * @param dataArray the ByteByffers to merge
     * @return a single ByteBuffer after flip merged from all ByteBuffer objects
     *         from dataArray.
     *
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
            if (destStartIndex == totalCapacity) {
                break;
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

        if (last && wsMessageChunksTotalCapacity.get() == 0) {
            if (inputBufferLimit != null) {
                inputBufferLimit.acquireUninterruptibly(messagePart.capacity());
                try {
                    browserPage.webSocketMessaged(messagePart.array());
                } finally {
                    inputBufferLimit.release(messagePart.capacity());
                }
            } else {
                browserPage.webSocketMessaged(messagePart.array());
            }
        } else {
            if (singleThreaded) {
                if (inputBufferLimit != null) {
                    inputBufferLimit.acquireUninterruptibly(messagePart.capacity());
                    transferToBrowserPageWS(messagePart, last);
                } else {
                    transferToBrowserPageWS(messagePart, last);
                }

            } else {
                commonLock.lock();
                try {
                    inputBufferLimit.acquireUninterruptibly(messagePart.capacity());
                    transferToBrowserPageWS(messagePart, last);
                } finally {
                    commonLock.unlock();
                }
            }

        }
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
                if (inputBufferLimit != null) {
                    inputBufferLimit.release(message.length);
                }
                return;
            }
        }

        if (last) {
            wsMessageChunks.add(messagePart);
            final int totalCapacity = wsMessageChunksTotalCapacity.getAndSet(0) + messagePart.capacity();
            final byte[] message = pollAndConvertToByteArray(totalCapacity, wsMessageChunks);
            if (inputBufferLimit != null) {
                try {
                    browserPage.webSocketMessaged(message);
                } finally {
                    inputBufferLimit.release(message.length);
                }
            } else {
                browserPage.webSocketMessaged(message);
            }

        } else {
            wsMessageChunks.add(messagePart);
            wsMessageChunksTotalCapacity.addAndGet(messagePart.capacity());
        }
    }
}
