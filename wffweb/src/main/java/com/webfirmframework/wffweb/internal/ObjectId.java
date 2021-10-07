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

    private static final long serialVersionUID = 1L;

    private final long mostSigBits;

    private final long leastSigBits;

    public ObjectId(final long mostSigBits, final long leastSigBits) {
        this.mostSigBits = mostSigBits;
        this.leastSigBits = leastSigBits;
    }

    public String id() {
        return new UUID(mostSigBits, leastSigBits).toString();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ObjectId objectId = (ObjectId) o;
        return mostSigBits == objectId.mostSigBits && leastSigBits == objectId.leastSigBits;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mostSigBits, leastSigBits);
    }

    @Override
    public int compareTo(final ObjectId that) {
        return (mostSigBits < that.mostSigBits ? -1
                : (mostSigBits > that.mostSigBits ? 1
                        : (leastSigBits < that.leastSigBits ? -1 : (leastSigBits > that.leastSigBits ? 1 : 0))));
    }

}
