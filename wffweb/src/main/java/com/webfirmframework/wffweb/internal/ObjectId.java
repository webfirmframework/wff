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

import java.util.UUID;

/**
 * Note: only for internal use
 * 
 * @author WFF
 * @since 3.0.19
 */
public final class ObjectId implements Comparable<ObjectId> {

    private final long mostSigBits;

    private final long leastSigBits;

    public ObjectId(final long mostSigBits, final long leastSigBits) {
        this.mostSigBits = mostSigBits;
        this.leastSigBits = leastSigBits;
    }

    public String id() {
        return new UUID(mostSigBits, leastSigBits).toString();
    }

    public int compareTo(ObjectId that) {
        return (this.mostSigBits < that.mostSigBits ? -1
                : (this.mostSigBits > that.mostSigBits ? 1
                        : (this.leastSigBits < that.leastSigBits ? -1
                                : (this.leastSigBits > that.leastSigBits ? 1 : 0))));
    }

}
