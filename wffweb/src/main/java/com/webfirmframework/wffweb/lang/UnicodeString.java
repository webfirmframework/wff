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
package com.webfirmframework.wffweb.lang;

import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.IntStream.Builder;

/**
 * An immutable class for unicode based string manipulation.
 *
 * @author WFF
 * @since 3.0.15
 *
 */
public final class UnicodeString {

    private final int[] codePoints;

    public UnicodeString(final String s) {
        this(s != null ? s.codePoints().toArray() : null);
    }

    public UnicodeString(final int[] codePoints) {
        super();
        this.codePoints = codePoints;
    }

    public int[] codePoints() {
        if (codePoints != null) {
            return Arrays.copyOf(codePoints, codePoints.length);
        }
        return null;
    }

    public String newString() {
        if (codePoints != null) {
            return new String(codePoints, 0, codePoints.length);
        }
        return null;
    }

    /**
     * @param c
     * @return
     */
    public int indexOf(final int c) {
        int index = 0;
        for (final int i : codePoints) {
            if (i == c) {
                return index;
            }
            index++;
        }
        return -1;
    }

    /**
     * @param c
     * @return
     */
    public int lastIndexOf(final int c) {

        for (int i = codePoints.length - 1; i >= 0; i--) {
            if (codePoints[i] == c) {
                return i;
            }
        }

        return -1;
    }

    /**
     *
     * @param forString the string for which the replacement to be done.
     * @param with      the matching to be replaced with this value.
     * @return the processed string
     * @since 3.0.15
     */
    public UnicodeString replace(final String forString, final String with) {
        return replace(codePoints, forString.codePoints().toArray(), with.codePoints().toArray());
    }

    /**
     *
     * @param forString the string for which the replacement to be done.
     * @param with      the matching to be replaced with this value.
     * @return the processed string
     * @since 3.0.15
     */
    public UnicodeString replace(final UnicodeString forString, final UnicodeString with) {
        return replace(codePoints, forString.codePoints, with.codePoints);
    }

    /**
     * @param codePoints         codePoints of string for processing.
     * @param codePointsSequence the array of code points for matching in its
     *                           sequential order.
     * @param withCodePoints     the matching to be replaced with this value.
     * @return the processed UnicodeString
     * @since 3.0.15
     */
    static UnicodeString replace(final int[] codePoints, final int[] codePointsSequence, final int[] withCodePoints) {

        if (codePoints == null || codePointsSequence == null || withCodePoints == null) {
            return new UnicodeString(codePoints);
        }

        if (codePoints.length == codePointsSequence.length && Arrays.equals(codePoints, codePointsSequence)) {
            return new UnicodeString(withCodePoints);
        }

        final int maxPossibleMatches = codePoints.length - (codePointsSequence.length - 1);

        if (maxPossibleMatches < 1) {
            return new UnicodeString(codePoints);
        }

        final Builder resultBuilder = IntStream.builder();

        if (maxPossibleMatches == codePoints.length && codePointsSequence.length == 1) {
            final int cpToMatch = codePointsSequence[0];
            if (withCodePoints.length > 0) {
                for (final int c : codePoints) {
                    if (c == cpToMatch) {
                        for (final int w : withCodePoints) {
                            resultBuilder.add(w);
                        }

                    } else {
                        resultBuilder.add(c);
                    }
                }
            } else {
                for (final int c : codePoints) {
                    if (c != cpToMatch) {
                        resultBuilder.add(c);
                    }
                }
            }
        } else {
            for (int i = 0; i < maxPossibleMatches; i++) {

                final int[] part = new int[codePointsSequence.length];

                System.arraycopy(codePoints, i, part, 0, part.length);

                if (Arrays.equals(part, codePointsSequence)) {
                    if (withCodePoints.length > 0) {
                        for (final int w : withCodePoints) {
                            resultBuilder.add(w);
                        }
                    }
                    i = i + (codePointsSequence.length - 1);
                } else {
                    resultBuilder.add(codePoints[i]);
                }

                if (i >= (maxPossibleMatches - 1)) {
                    final int nextIndex = i + 1;
                    final int total = codePoints.length - nextIndex;
                    for (int w = 0; w < total; w++) {
                        resultBuilder.add(codePoints[nextIndex + w]);
                    }
                }
            }

        }

        return new UnicodeString(resultBuilder.build().toArray());
    }

}
