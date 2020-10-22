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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.webfirmframework.wffweb.util.data.NameValue;

public enum WffBinaryMessageUtil {

    /**
     * A wff binary message is a collection of name-value pairs where both name and
     * value can be a binary data. It can have duplicate names.<br>
     *
     * The wff binary message is composed as follows :- <br>
     * The first byte represents the maximum number of bytes for name length bytes.
     * The second byte represents the maximum number of bytes for the values length
     * bytes. The remaining bytes represent the name-values pairs.
     *
     * <table border="1">
     * <caption>Name values pair</caption> <tbody>
     * <tr>
     * <td>name length bytes</td>
     * <td>name bytes</td>
     * <td>values length bytes</td>
     * <td>values bytes</td>
     * </tr>
     * </tbody>
     * </table>
     *
     *
     * Here, the name length bytes is the length of name bytes and values length
     * bytes is the length of values bytes.<br>
     *
     * The values in name-values pair is an array of value, the values is composed
     * as follows
     *
     * <table border="1">
     * <caption>Value as an array</caption> <tbody>
     * <tr>
     * <td>total values length bytes</td>
     * <td>value length bytes</td>
     * <td>value bytes</td>
     * <td>value length bytes</td>
     * <td>value bytes</td>
     * </tr>
     * </tbody>
     * </table>
     * Here, total values length bytes = sum of (value length bytes + value bytes +
     * value length bytes + value bytes) no. of bytes.
     *
     *
     *
     * @author WFF
     *
     */
    VERSION_1 {

        @Override
        public byte[] getWffBinaryMessageBytes(final Collection<NameValue> nameValues) {
            return getWffBinaryMessageBytes(nameValues.toArray(new NameValue[nameValues.size()]));
        }

        @Override
        public byte[] getWffBinaryMessageBytes(final NameValue... nameValues) {

            int maxNoOfNameBytes = 0;
            int maxNoOfValuesBytes = 0;

            for (final NameValue nameValue : nameValues) {

                final byte[] name = nameValue.getName();
                final byte[][] values = nameValue.getValues();

                if (name != null && name.length > maxNoOfNameBytes) {
                    maxNoOfNameBytes = name.length;
                }

                if (values != null) {
                    int maxValuesBytesLength = 0;
                    for (final byte[] value : values) {

                        if (value != null) {
                            maxValuesBytesLength += value.length;
                        }

                    }

                    final int maxBytesLengthFromTotalBytes = getLengthOfOptimizedBytesFromInt(maxValuesBytesLength);

                    final int maxBytesLengthForAllValues = values.length * maxBytesLengthFromTotalBytes;

                    final int totalNoOfBytesForAllValues = maxValuesBytesLength + maxBytesLengthForAllValues;

                    if (totalNoOfBytesForAllValues > maxNoOfValuesBytes) {
                        maxNoOfValuesBytes = totalNoOfBytesForAllValues;
                    }

                }

            }

            final byte maxNoNameLengthBytes = (byte) getLengthOfOptimizedBytesFromInt(maxNoOfNameBytes);

            final byte maxNoValueLengthBytes = (byte) getLengthOfOptimizedBytesFromInt(maxNoOfValuesBytes);

            int totalLengthOfBinaryMessage = 2;

            for (final NameValue nameValue : nameValues) {
                final byte[] name = nameValue.getName();

                totalLengthOfBinaryMessage += maxNoNameLengthBytes + name.length;

                final byte[][] values = nameValue.getValues();

                if (values.length == 0) {
                    totalLengthOfBinaryMessage += maxNoValueLengthBytes;
                } else {

                    // values will always be an array
                    final int totalNoOfValueLengthBytes = maxNoValueLengthBytes * values.length;
                    int totalNoOfValueBytes = 0;

                    for (final byte[] value : values) {
                        totalNoOfValueBytes += value.length;
                    }

                    totalLengthOfBinaryMessage += totalNoOfValueLengthBytes + totalNoOfValueBytes;

                    totalLengthOfBinaryMessage += maxNoValueLengthBytes;

                }

            }

            final byte[] wffBinaryMessageBytes = new byte[totalLengthOfBinaryMessage];

            wffBinaryMessageBytes[0] = maxNoNameLengthBytes;
            wffBinaryMessageBytes[1] = maxNoValueLengthBytes;

            int wffBinaryMessageBytesIndex = 2;

            for (final NameValue nameValue : nameValues) {

                final byte[] name = nameValue.getName();

                final byte[] nameLegthBytes = getLastBytesFromInt(name.length, maxNoNameLengthBytes);

                System.arraycopy(nameLegthBytes, 0, wffBinaryMessageBytes, wffBinaryMessageBytesIndex,
                        nameLegthBytes.length);

                wffBinaryMessageBytesIndex += nameLegthBytes.length;

                System.arraycopy(name, 0, wffBinaryMessageBytes, wffBinaryMessageBytesIndex, name.length);

                wffBinaryMessageBytesIndex += name.length;

                final byte[][] values = nameValue.getValues();

                if (values.length == 0) {
                    System.arraycopy(new byte[] { 0, 0, 0, 0 }, 0, wffBinaryMessageBytes, wffBinaryMessageBytesIndex,
                            maxNoValueLengthBytes);
                    wffBinaryMessageBytesIndex += maxNoValueLengthBytes;
                } else {

                    // it will always be an array of values
                    int valueLegth = 0;
                    for (final byte[] value : values) {
                        valueLegth += value.length;
                    }

                    valueLegth += (maxNoValueLengthBytes * values.length);

                    byte[] valueLegthBytes = getLastBytesFromInt(valueLegth, maxNoValueLengthBytes);

                    System.arraycopy(valueLegthBytes, 0, wffBinaryMessageBytes, wffBinaryMessageBytesIndex,
                            valueLegthBytes.length);

                    wffBinaryMessageBytesIndex += valueLegthBytes.length;

                    for (final byte[] value : values) {

                        valueLegthBytes = getLastBytesFromInt(value.length, maxNoValueLengthBytes);

                        System.arraycopy(valueLegthBytes, 0, wffBinaryMessageBytes, wffBinaryMessageBytesIndex,
                                valueLegthBytes.length);

                        wffBinaryMessageBytesIndex += valueLegthBytes.length;

                        System.arraycopy(value, 0, wffBinaryMessageBytes, wffBinaryMessageBytesIndex, value.length);
                        wffBinaryMessageBytesIndex += value.length;
                    }

                }

            }

            return wffBinaryMessageBytes;
        }

        @Override
        public List<NameValue> parse(final byte[] message) {

            // needs to optimize this code to find the total no. of name values.
            final List<NameValue> nameValues = new LinkedList<>();

            final byte[] nameLengthBytes = new byte[message[0]];
            final byte[] valueLengthBytes = new byte[message[1]];

            final int nameLengthBytesLength = nameLengthBytes.length;
            final int valueLengthBytesLength = valueLengthBytes.length;

            for (int messageIndex = 2; messageIndex < message.length; messageIndex++) {

                final NameValue nameValue = new NameValue();

                System.arraycopy(message, messageIndex, nameLengthBytes, 0, nameLengthBytesLength);
                messageIndex = messageIndex + nameLengthBytesLength;
                // nameToFetch = false;
                // valueToFetch = true;

                int fromByteArray = getIntFromOptimizedBytes(nameLengthBytes);
                final byte[] nameBytes = new byte[fromByteArray];

                System.arraycopy(message, messageIndex, nameBytes, 0, nameBytes.length);
                messageIndex = messageIndex + nameBytes.length;

                nameValue.setName(nameBytes);

                System.arraycopy(message, messageIndex, valueLengthBytes, 0, valueLengthBytesLength);
                messageIndex = messageIndex + valueLengthBytesLength;
                fromByteArray = getIntFromOptimizedBytes(valueLengthBytes);
                final byte[] valueBytes = new byte[fromByteArray];

                System.arraycopy(message, messageIndex, valueBytes, 0, valueBytes.length);
                messageIndex = messageIndex + valueBytes.length - 1;

                // process array values
                final byte[][] extractEachValueBytes = extractValuesFromArray(valueLengthBytes, valueLengthBytesLength,
                        valueBytes);
                nameValue.setValues(extractEachValueBytes);

                nameValues.add(nameValue);

            }

            return nameValues;
        }

        private byte[][] extractValuesFromArray(final byte[] valueLengthBytes, final int valueLengthBytesLength,
                final byte[] valueBytes) {
            final int[] valueBytesIndex = { 0 };
            final int[] eachValueBytesCount = { 0 };
            final int[] finalValuesIndex = { 0 };
            final byte[][][] finalValues = new byte[1][0][0];

            final byte[][] extractEachValueBytes = extractEachValueBytes(valueLengthBytes, valueLengthBytesLength,
                    valueBytes, valueBytesIndex, eachValueBytesCount, finalValues, finalValuesIndex);
            return extractEachValueBytes;
        }

    };

    private WffBinaryMessageUtil() {
    }

    private static byte[][] extractEachValueBytes(final byte[] valueLengthBytes, final int valueLengthBytesLength,
            final byte[] valueBytes, final int[] valueBytesIndex, final int[] eachValueBytesCount,
            final byte[][][] finalValues, final int[] finalValuesIndex) {

        for (; valueBytesIndex[0] < valueBytes.length; valueBytesIndex[0]++) {
            System.arraycopy(valueBytes, valueBytesIndex[0], valueLengthBytes, 0, valueLengthBytesLength);
            valueBytesIndex[0] += valueLengthBytesLength;
            final int eachValueBytesLength = getIntFromOptimizedBytes(valueLengthBytes);
            final byte[] eachValueBytes = new byte[eachValueBytesLength];

            System.arraycopy(valueBytes, valueBytesIndex[0], eachValueBytes, 0, eachValueBytes.length);
            eachValueBytesCount[0]++;

            valueBytesIndex[0] += eachValueBytesLength - 1;

            valueBytesIndex[0]++;
            extractEachValueBytes(valueLengthBytes, valueLengthBytesLength, valueBytes, valueBytesIndex,
                    eachValueBytesCount, finalValues, finalValuesIndex);
            if (finalValues[0].length == 0) {
                finalValuesIndex[0] = eachValueBytesCount[0] - 1;
                finalValues[0] = new byte[eachValueBytesCount[0]][1];
            }

            finalValues[0][finalValuesIndex[0]] = eachValueBytes;
            finalValuesIndex[0] = finalValuesIndex[0] - 1;
        }

        return finalValues[0];
    }

    /**
     * @param message the wff binary message bytes
     * @return the list of name value pairs
     * @since 1.0.0
     * @author WFF
     */
    public List<NameValue> parse(final byte[] message) {
        throw new AssertionError();
    }

    /**
     * @param nameValues
     * @return the wff binary message bytes for the given name value pairs
     * @since 1.0.0
     * @author WFF
     */
    public byte[] getWffBinaryMessageBytes(final Collection<NameValue> nameValues) {
        throw new AssertionError();
    }

    /**
     * @param nameValues
     * @return the wff binary message bytes for the given name value pairs
     * @since 2.0.0
     * @author WFF
     */
    public byte[] getWffBinaryMessageBytes(final NameValue... nameValues) {
        throw new AssertionError();
    }

    /**
     * @param bytes from which the integer value will be obtained
     * @return the integer value from the given bytes
     * @since 1.0.0
     * @author WFF
     */
    public static int getIntFromBytes(final byte[] bytes) {
        return bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
    }

    /**
     * @param bytes the optimized bytes from which the integer value will be
     *              obtained
     * @return the integer value from the given bytes
     * @since 1.1.3
     * @author WFF
     */
    public static int getIntFromOptimizedBytes(final byte[] bytes) {
        if (bytes.length == 4) {
            return bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
        } else if (bytes.length == 3) {
            return (bytes[0] & 0xFF) << 16 | (bytes[1] & 0xFF) << 8 | (bytes[2] & 0xFF);
        } else if (bytes.length == 2) {
            return (bytes[0] & 0xFF) << 8 | (bytes[1] & 0xFF);
        } else if (bytes.length == 1) {
            return (bytes[0] & 0xFF);
        }
        return bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
    }

    /**
     * NB: not fully tested yet
     *
     * @param bytes the optimized bytes from which the long value will be obtained
     * @return the long value from the given bytes
     * @since 3.0.6
     * @author WFF
     */
    public static long getLongFromOptimizedBytes(final byte[] bytes) {

        // makes java.nio.BufferUnderflowException
        // return ByteBuffer.wrap(bytes).getLong();

        // incorrect value for optimized bytes
        // return new BigInteger(bytes).longValue();

        // TODO test and verify before use
        return longFromOptimizedBytes(bytes);
    }

    /**
     * NB: DO NOT use this method TODO There could be bug. Test and verify also
     * check performance
     *
     * @param bytes
     * @return
     */
    private static long longFromOptimizedBytes(final byte[] bytes) {
        if (bytes.length == 1) {
            final long i0 = bytes[0] & 0xFF;
            return i0;
        } else if (bytes.length == 2) {
            final long i0 = bytes[0] & 0xFF;
            final long i1 = bytes[1] & 0xFF;
            return i0 << 8 | i1;
        } else if (bytes.length == 3) {
            final long i0 = bytes[0] & 0xFF;
            final long i1 = bytes[1] & 0xFF;
            final long i2 = bytes[2] & 0xFF;
            return i0 << 16 | i1 << 8 | i2;
        } else if (bytes.length == 4) {
            final long i0 = bytes[0] & 0xFF;
            final long i1 = bytes[1] & 0xFF;
            final long i2 = bytes[2] & 0xFF;
            final long i3 = bytes[3] & 0xFF;
            return i0 << 24 | i1 << 16 | i2 << 8 | i3;
        } else if (bytes.length == 5) {
            final long i0 = bytes[0] & 0xFF;
            final long i1 = bytes[1] & 0xFF;
            final long i2 = bytes[2] & 0xFF;
            final long i3 = bytes[3] & 0xFF;
            final long i4 = bytes[4] & 0xFF;
            return i0 << 32 | i1 << 24 | i2 << 16 | i3 << 8 | i4;
        } else if (bytes.length == 6) {
            final long i0 = bytes[0] & 0xFF;
            final long i1 = bytes[1] & 0xFF;
            final long i2 = bytes[2] & 0xFF;
            final long i3 = bytes[3] & 0xFF;
            final long i4 = bytes[4] & 0xFF;
            final long i5 = bytes[5] & 0xFF;
            return i0 << 40 | i1 << 32 | i2 << 24 | i3 << 16 | i4 << 8 | i5;
        } else if (bytes.length == 7) {
            final long i0 = bytes[0] & 0xFF;
            final long i1 = bytes[1] & 0xFF;
            final long i2 = bytes[2] & 0xFF;
            final long i3 = bytes[3] & 0xFF;
            final long i4 = bytes[4] & 0xFF;
            final long i5 = bytes[5] & 0xFF;
            final long i6 = bytes[6] & 0xFF;
            return i0 << 48 | i1 << 40 | i2 << 32 | i3 << 24 | i4 << 16 | i5 << 8 | i6;
        } else if (bytes.length == 8) {
            final long i0 = bytes[0];
            final long i1 = bytes[1] & 0xFF;
            final long i2 = bytes[2] & 0xFF;
            final long i3 = bytes[3] & 0xFF;
            final long i4 = bytes[4] & 0xFF;
            final long i5 = bytes[5] & 0xFF;
            final long i6 = bytes[6] & 0xFF;
            final long i7 = bytes[7] & 0xFF;
            return i0 << 56 | i1 << 48 | i2 << 40 | i3 << 32 | i4 << 24 | i5 << 16 | i6 << 8 | i7;
        }

        final long i0 = bytes[0];
        final long i1 = bytes[1] & 0xFF;
        final long i2 = bytes[2] & 0xFF;
        final long i3 = bytes[3] & 0xFF;
        final long i4 = bytes[4] & 0xFF;
        final long i5 = bytes[5] & 0xFF;
        final long i6 = bytes[6] & 0xFF;
        final long i7 = bytes[7] & 0xFF;
        return i0 << 56 | i1 << 48 | i2 << 40 | i3 << 32 | i4 << 24 | i5 << 16 | i6 << 8 | i7;
    }

    /**
     * NB: not fully tested yet
     *
     * @param bytes the optimized bytes from which the long value will be obtained
     * @return the long value from the given bytes
     * @since 3.0.6
     * @author WFF
     */
    public static long getLongFrom56bitOptimizedBytes(final byte[] bytes) {

        // makes java.nio.BufferUnderflowException
        // return ByteBuffer.wrap(bytes).getLong();

        // incorrect value for optimized bytes
        // return new BigInteger(bytes).longValue();

        // TODO test and verify before use
        return longFrom56bitOptimizedBytes(bytes);
    }

    /**
     * NB: DO NOT use this method TODO There could be bug. Test and verify also
     * check performance
     *
     * @param bytes
     * @return
     */
    private static long longFrom56bitOptimizedBytes(final byte[] bytes) {
        if (bytes.length == 1) {
            final long i0 = bytes[0] & 0xFF;
            return i0;
        } else if (bytes.length == 2) {
            final long i0 = bytes[0] & 0xFF;
            final long i1 = bytes[1] & 0xFF;
            return i0 << 8 | i1;
        } else if (bytes.length == 3) {
            final long i0 = bytes[0] & 0xFF;
            final long i1 = bytes[1] & 0xFF;
            final long i2 = bytes[2] & 0xFF;
            return i0 << 16 | i1 << 8 | i2;
        } else if (bytes.length == 4) {
            final long i0 = bytes[0] & 0xFF;
            final long i1 = bytes[1] & 0xFF;
            final long i2 = bytes[2] & 0xFF;
            final long i3 = bytes[3] & 0xFF;
            return i0 << 24 | i1 << 16 | i2 << 8 | i3;
        } else if (bytes.length == 5) {
            final long i0 = bytes[0] & 0xFF;
            final long i1 = bytes[1] & 0xFF;
            final long i2 = bytes[2] & 0xFF;
            final long i3 = bytes[3] & 0xFF;
            final long i4 = bytes[4] & 0xFF;
            return i0 << 32 | i1 << 24 | i2 << 16 | i3 << 8 | i4;
        } else if (bytes.length == 6) {
            final long i0 = bytes[0] & 0xFF;
            final long i1 = bytes[1] & 0xFF;
            final long i2 = bytes[2] & 0xFF;
            final long i3 = bytes[3] & 0xFF;
            final long i4 = bytes[4] & 0xFF;
            final long i5 = bytes[5] & 0xFF;
            return i0 << 40 | i1 << 32 | i2 << 24 | i3 << 16 | i4 << 8 | i5;
        } else if (bytes.length == 7) {
            final long i0 = bytes[0];
            final long i1 = bytes[1] & 0xFF;
            final long i2 = bytes[2] & 0xFF;
            final long i3 = bytes[3] & 0xFF;
            final long i4 = bytes[4] & 0xFF;
            final long i5 = bytes[5] & 0xFF;
            final long i6 = bytes[6] & 0xFF;
            return i0 << 48 | i1 << 40 | i2 << 32 | i3 << 24 | i4 << 16 | i5 << 8 | i6;
        }

        final long i0 = bytes[0];
        final long i1 = bytes[1] & 0xFF;
        final long i2 = bytes[2] & 0xFF;
        final long i3 = bytes[3] & 0xFF;
        final long i4 = bytes[4] & 0xFF;
        final long i5 = bytes[5] & 0xFF;
        final long i6 = bytes[6] & 0xFF;
        return i0 << 48 | i1 << 40 | i2 << 32 | i3 << 24 | i4 << 16 | i5 << 8 | i6;
    }

    /**
     * @param value
     * @return the bytes for the given double value in the IEEE 754 Standard
     * @since 2.0.0
     * @author WFF
     */
    public static byte[] getOptimizedBytesFromDouble(final double value) {
        return getOptimizedBytesFromLong(Double.doubleToLongBits(value));
    }

    /**
     * @param value
     * @return the bytes for the given long value
     * @since 2.0.0
     * @author WFF
     */
    public static byte[] getOptimizedBytesFromLong(final long value) {

        final byte zerothIndex = (byte) (value >> 56L);
        final byte firstIndex = (byte) (value >> 48L);
        final byte secondIndex = (byte) (value >> 40L);
        final byte thirdIndex = (byte) (value >> 32L);
        final byte fourthIndex = (byte) (value >> 24L);
        final byte fifthIndex = (byte) (value >> 16L);
        final byte sixthIndex = (byte) (value >> 8L);
        final byte seventhIndex = (byte) value;

        if (zerothIndex == 0) {

            if (firstIndex == 0) {

                if (secondIndex == 0) {

                    if (thirdIndex == 0) {

                        if (fourthIndex == 0) {

                            if (fifthIndex == 0) {

                                if (sixthIndex == 0) {

                                    return new byte[] { seventhIndex };
                                }
                                return new byte[] { sixthIndex, seventhIndex };
                            }
                            return new byte[] { fifthIndex, sixthIndex, seventhIndex };
                        }

                        return new byte[] { fourthIndex, fifthIndex, sixthIndex, seventhIndex };
                    }

                    return new byte[] { thirdIndex, fourthIndex, fifthIndex, sixthIndex, seventhIndex };
                }

                return new byte[] { secondIndex, thirdIndex, fourthIndex, fifthIndex, sixthIndex, seventhIndex };
            }

            return new byte[] { firstIndex, secondIndex, thirdIndex, fourthIndex, fifthIndex, sixthIndex,
                    seventhIndex };
        }

        return new byte[] { zerothIndex, firstIndex, secondIndex, thirdIndex, fourthIndex, fifthIndex, sixthIndex,
                seventhIndex };
    }

    /**
     * handles long upto (long) Math.pow(2, 55) - 1
     *
     * @param value
     * @return the bytes for the given long value
     * @since 3.0.6
     * @author WFF
     */
    public static byte[] getOptimizedBytesFromInt56bit(final long value) {

        final byte zerothIndex = (byte) (value >> 48L);
        final byte firstIndex = (byte) (value >> 40L);
        final byte secondIndex = (byte) (value >> 32L);
        final byte thirdIndex = (byte) (value >> 24L);
        final byte fourthIndex = (byte) (value >> 16L);
        final byte fifthIndex = (byte) (value >> 8L);
        final byte sixthIndex = (byte) value;

        if (zerothIndex == 0) {

            if (firstIndex == 0) {

                if (secondIndex == 0) {

                    if (thirdIndex == 0) {

                        if (fourthIndex == 0) {

                            if (fifthIndex == 0) {

                                return new byte[] { sixthIndex };
                            }
                            return new byte[] { fifthIndex, sixthIndex };
                        }
                        return new byte[] { fourthIndex, fifthIndex, sixthIndex };
                    }

                    return new byte[] { thirdIndex, fourthIndex, fifthIndex, sixthIndex };
                }

                return new byte[] { secondIndex, thirdIndex, fourthIndex, fifthIndex, sixthIndex };
            }

            return new byte[] { firstIndex, secondIndex, thirdIndex, fourthIndex, fifthIndex, sixthIndex };
        }

        return new byte[] { zerothIndex, firstIndex, secondIndex, thirdIndex, fourthIndex, fifthIndex, sixthIndex };
    }

    /**
     * @param value the integer value to be converted to bytes.
     * @return the bytes for the corresponding integer given.
     * @since 1.0.0
     * @author WFF
     */
    public static byte[] getBytesFromInt(final int value) {
        return new byte[] { (byte) (value >> 24), (byte) (value >> 16), (byte) (value >> 8), (byte) value };
    }

    /**
     * @param value the integer value to be converted to optimized bytes. Optimized
     *              bytes means the minimum bytes required to represent the given
     *              integer value.
     * @return the bytes for the corresponding integer given.
     * @since 1.1.3
     * @author WFF
     */
    public static byte[] getOptimizedBytesFromInt(final int value) {

        final byte zerothIndex = (byte) (value >> 24);
        final byte firstIndex = (byte) (value >> 16);
        final byte secondIndex = (byte) (value >> 8);
        final byte thirdIndex = (byte) value;

        if (zerothIndex == 0) {

            if (firstIndex == 0) {

                if (secondIndex == 0) {

                    return new byte[] { thirdIndex };
                }

                return new byte[] { secondIndex, thirdIndex };
            }

            return new byte[] { firstIndex, secondIndex, thirdIndex };
        }

        return new byte[] { zerothIndex, firstIndex, secondIndex, thirdIndex };
    }

    /**
     * @param value the integer value to be converted to optimized bytes. Optimized
     *              bytes means the minimum bytes required to represent the given
     *              integer value.
     * @return the array length of the bytes for the corresponding integer given.
     * @since 1.1.5
     * @author WFF
     */
    public static int getLengthOfOptimizedBytesFromInt(final int value) {

        // simple checking just like the below commented is heavy, which is
        // proved in performance testing
        // if (value < 256) {
        // return 1;
        // } else if (value < 65536) {
        // return 2;
        // } else if (value < 16777216) {
        // return 3;
        // }

        final byte zerothIndex = (byte) (value >> 24);
        final byte firstIndex = (byte) (value >> 16);
        final byte secondIndex = (byte) (value >> 8);

        if (zerothIndex == 0) {

            if (firstIndex == 0) {

                if (secondIndex == 0) {

                    return 1;
                }

                return 2;
            }

            return 3;
        }

        return 4;
    }

    /**
     * @param value         the integer value to be converted to optimized bytes.
     *                      Optimized bytes means the minimum bytes required to
     *                      represent the given integer value.
     * @param lastNoOfBytes the last no of bytes to be returned. Expected inputs are
     *                      1, 2, 3 or 4.
     * @return the bytes for the corresponding integer given.
     * @since 1.1.5
     * @author WFF
     */
    public static byte[] getLastBytesFromInt(final int value, final int lastNoOfBytes) {

        final byte zerothIndex = (byte) (value >> 24);
        final byte firstIndex = (byte) (value >> 16);
        final byte secondIndex = (byte) (value >> 8);
        final byte thirdIndex = (byte) value;

        if (lastNoOfBytes == 1) {
            return new byte[] { thirdIndex };
        } else if (lastNoOfBytes == 2) {
            return new byte[] { secondIndex, thirdIndex };
        } else if (lastNoOfBytes == 3) {
            return new byte[] { firstIndex, secondIndex, thirdIndex };
        }

        return new byte[] { zerothIndex, firstIndex, secondIndex, thirdIndex };
    }

}
