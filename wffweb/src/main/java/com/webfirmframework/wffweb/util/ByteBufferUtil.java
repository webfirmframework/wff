/*
 * Copyright 2014-2020 Web Firm Framework
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
package com.webfirmframework.wffweb.util;

import java.nio.ByteBuffer;

/**
 *
 * @author WFF
 * @since 3.0.1
 */
public final class ByteBufferUtil {

    private ByteBufferUtil() {
        throw new AssertionError();
    }

    /**
     * Eg:
     *
     * <pre>
     * <code>
     * final String s = "1234567890abc";
     * final ByteBuffer inputData = ByteBuffer
     *         .wrap(s.getBytes(StandardCharsets.UTF_8));
     *
     * final StringBuilder builder = new StringBuilder();
     *
     * final int totalSlices = ByteBufferUtil.slice(inputData, 3,
     *         (part, last) -&gt; {
     *             builder.append(
     *                     new String(part.array(), StandardCharsets.UTF_8));
     *             return !last;
     *         });
     *
     * System.out.println(s.equals(builder.toString()));
     * System.out.println(5 == totalSlices);
     * </code>
     * </pre>
     *
     * prints
     *
     * <pre>
     * true
     * true
     * </pre>
     *
     * @param data         ByteBuffer to be sliced
     * @param maxSliceSize the maximum size of ByteBuffer of the sliced part.
     * @param slice        the Slice functional interface. The method Slice.each
     *                     must return true to continue slicing.
     * @return the total number of slices made. NB: it is just the count of
     *         invocation of Slice.each method. If the Slice.each returned false
     *         while the last (second param in Slice.each) is false then the
     *         returned slice count will be less than the probable number of slices
     *         in the input data.
     * @since 3.0.1
     */
    public static int slice(final ByteBuffer data, final int maxSliceSize, final Slice<ByteBuffer> slice) {

        ByteBuffer d = data;

        int remaining = 0;
        int sliceCount = 0;
        while ((remaining = d.remaining()) > 0) {
            sliceCount++;
            final byte[] bytes = new byte[maxSliceSize < remaining ? maxSliceSize : remaining];

            final boolean goOn = slice.each(ByteBuffer.wrap(bytes), d.get(bytes).remaining() == 0);

            if (!goOn) {
                break;
            }

            d = d.slice();
        }

        return sliceCount;
    }

    /**
     *
     * It slices only if required, i.e. if the capacity of input data is equal to
     * the remaining data in the buffer and the capacity is less than or equal to
     * the maxSliceSize then the Slice.each method will reuse the same input data as
     * the first argument. <br>
     * <br>
     * Eg:
     *
     * <pre>
     * <code>
     * final String s = "1234567890abc";
     * final ByteBuffer inputData = ByteBuffer
     *         .wrap(s.getBytes(StandardCharsets.UTF_8));
     *
     * final StringBuilder builder = new StringBuilder();
     *
     * final int totalSlices = ByteBufferUtil.sliceIfRequired(inputData, 3,
     *         (part, last) -&gt; {
     *             builder.append(
     *                     new String(part.array(), StandardCharsets.UTF_8));
     *             return !last;
     *         });
     *
     * System.out.println(s.equals(builder.toString()));
     * System.out.println(5 == totalSlices);
     * </code>
     * </pre>
     *
     * prints
     *
     * <pre>
     * true
     * true
     * </pre>
     *
     * @param data         ByteBuffer to be sliced
     * @param maxSliceSize the maximum size of ByteBuffer of the sliced part.
     * @param slice        the Slice functional interface. The method Slice.each
     *                     must return true to continue slicing.
     * @return the total number of slices made. NB: it is just the count of
     *         invocation of Slice.each method. If the Slice.each returned false
     *         while the last (second param in Slice.each) is false then the
     *         returned slice count will be less than the probable number of slices
     *         in the input data.
     * @since 3.0.1
     */
    public static int sliceIfRequired(final ByteBuffer data, final int maxSliceSize, final Slice<ByteBuffer> slice) {

        final int capacity = data.capacity();
        if (capacity == data.remaining() && capacity <= maxSliceSize) {
            slice.each(data, true);
            return 1;
        }

        ByteBuffer d = data;

        int remaining = 0;
        int sliceCount = 0;
        while ((remaining = d.remaining()) > 0) {
            sliceCount++;
            final byte[] bytes = new byte[maxSliceSize < remaining ? maxSliceSize : remaining];

            final boolean goOn = slice.each(ByteBuffer.wrap(bytes), d.get(bytes).remaining() == 0);

            if (!goOn) {
                break;
            }

            d = d.slice();
        }

        return sliceCount;
    }

    /**
     * Merges and returns ByteBuffer from the given dataArray. The ByteBuffer will
     * be returned after flip.
     *
     * @param dataArray the ByteByffers to merge
     * @return a single ByteBuffer after flip merged from all ByteBuffer objects
     *         from dataArray.
     *
     * @since 3.0.2
     */
    public static ByteBuffer merge(final ByteBuffer... dataArray) {
        int totalCapacity = 0;
        for (final ByteBuffer data : dataArray) {
            totalCapacity += data.capacity();
        }
        final ByteBuffer wholeData = ByteBuffer.allocate(totalCapacity);
        for (final ByteBuffer data : dataArray) {
            wholeData.put(data);
        }
        wholeData.flip();

        return wholeData;
    }

}
