package com.webfirmframework.wffweb.server.page;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

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
    private final Queue<ByteBuffer> wsMessageChunks = new ConcurrentLinkedQueue<>();

    private final AtomicInteger wsMessageChunksTotalCapacity = new AtomicInteger(
            0);

    private final BrowserPage browserPage;

    public PayloadProcessor(final BrowserPage browserPage) {
        this.browserPage = browserPage;
    }

    /**
     * Merges and returns byte array from the given dataArray.
     *
     * @param dataArray
     *                      the ByteByffers to merge
     * @return a single ByteBuffer after flip merged from all ByteBuffer objects
     *         from dataArray.
     *
     * @since 3.0.2
     */
    // for testing purpose the method visibility is changed to package level
    // (default)
    static byte[] pollAndConvertToByteArray(final int totalCapacity,
            final Queue<ByteBuffer> dataArray) {

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
     * chucks. A WebSocket server may have a max size of byte array that can be
     * sent or receive as a single object. eg: websocket
     * session.getMaxBinaryMessageBufferSize may limit it. In such case this
     * method can be used to get the complete data as chucks.
     *
     * @param messagePart
     *                        message part
     * @param last
     *                        true if it is the last part of the message
     * @since 3.0.2
     */
    public void webSocketMessaged(final ByteBuffer messagePart,
            final boolean last) {

        wsMessageChunks.add(messagePart);

        if (last) {
            final int totalCapacity = wsMessageChunksTotalCapacity.getAndSet(0)
                    + messagePart.capacity();

            browserPage.webSocketMessaged(
                    pollAndConvertToByteArray(totalCapacity, wsMessageChunks));
        } else {
            wsMessageChunksTotalCapacity.addAndGet(messagePart.capacity());
        }

    }
}
