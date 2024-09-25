/*
 * Copyright 2014-2024 Web Firm Framework
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

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.locks.StampedLock;

/**
 * @since 12.0.0-beta.4
 */
final class TokenWrapper {

    final StampedLock lock = new StampedLock();

    private volatile String value;

    private volatile long updatedTimeMillis;

    private volatile int updatedId;

    final String key;

    TokenWrapper(final String key) {
        this.key = key;
    }

    /**
     * To set token pass token or token bytes. To remove token pass token and
     * orTokenBytes as null.
     *
     * @param value
     * @param orTokenBytes
     * @param writeTime
     * @param updatedId
     * @return
     */
    boolean setTokenAndWriteTime(final String value, final byte[] orTokenBytes, final long writeTime,
            final int updatedId) {
        return setTokenAndWriteTime(value, orTokenBytes, writeTime, updatedId, null, null);
    }

    boolean setTokenAndWriteTime(final String value, final byte[] orTokenBytes, final long writeTime,
            final int updatedId, final String key, final Map<String, TokenWrapper> tokenWrapperByKey) {
        if (writeTime >= updatedTimeMillis) {
            if (writeTime > updatedTimeMillis || (writeTime == updatedTimeMillis && updatedId > this.updatedId)) {
                if (orTokenBytes != null) {
                    this.value = new String(orTokenBytes, StandardCharsets.UTF_8);
                } else {
                    this.value = value;
                }
                if (value == null && orTokenBytes == null && tokenWrapperByKey != null && key != null) {
                    tokenWrapperByKey.remove(key);
                }
                updatedTimeMillis = writeTime;
                this.updatedId = updatedId;
                return true;
            }

        }
        return false;
    }

    public String getValue() {
        return value;
    }

    public long getUpdatedTimeMillis() {
        return updatedTimeMillis;
    }
}
