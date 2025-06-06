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
 * @author WFF
 */
package com.webfirmframework.wffweb;

import java.io.Serial;

/**
 *
 * @author WFF
 * @since 2.1.8
 */
public class InvalidTagException extends WffRuntimeException {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 1_0_0L;

    public InvalidTagException() {
        super();
    }

    public InvalidTagException(final String message, final Throwable cause, final boolean enableSuppression,
            final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public InvalidTagException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public InvalidTagException(final String message) {
        super(message);
    }

    public InvalidTagException(final Throwable cause) {
        super(cause);
    }

}
