/*
 * Copyright 2014-2021 Web Firm Framework
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
package com.webfirmframework.wffweb.internal;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * Note: only for internal use
 *
 * @author WFF
 * @since 3.0.19
 */
public final class ObjectId implements Comparable<ObjectId>, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    // thread-safety not required, duplicate value is also fine
    private static volatile long ORDER_COUNTER;

    private final long order;

    private final long mostSigBits;

    private final long leastSigBits;

    public ObjectId(final long mostSigBits, final long leastSigBits) {
        this.mostSigBits = mostSigBits;
        this.leastSigBits = leastSigBits;
        // thread-safety not required, duplicate value is also fine
        order = ++ORDER_COUNTER;
    }

    public String id() {
        return new UUID(mostSigBits, leastSigBits).toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ObjectId objectId = (ObjectId) o;
        return order == objectId.order && mostSigBits == objectId.mostSigBits && leastSigBits == objectId.leastSigBits;
    }

    @Override
    public int hashCode() {
        return Objects.hash(order, mostSigBits, leastSigBits);
    }

    @Override
    public int compareTo(final ObjectId that) {
        return (order < that.order ? -1
                : order > that.order ? 1
                        : (mostSigBits < that.mostSigBits ? -1
                                : (mostSigBits > that.mostSigBits ? 1
                                        : (leastSigBits < that.leastSigBits ? -1
                                                : (leastSigBits > that.leastSigBits ? 1 : 0)))));
    }

}
