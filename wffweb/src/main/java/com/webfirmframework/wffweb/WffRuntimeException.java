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
 */
package com.webfirmframework.wffweb;

import java.io.Serial;

/**
 *
 * @author WFF
 * @since 1.0.0
 */
public class WffRuntimeException extends RuntimeException {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 1_0_0L;

    /**
     * Constructs a new runtime exception with {@code null} as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a call
     * to {@link #initCause}.
     *
     * @author WFF
     * @since 1.0.0
     */
    public WffRuntimeException() {
        super();
    }

    /**
     * Constructs a new runtime exception with the specified detail message, cause,
     * suppression enabled or disabled, and writable stack trace enabled or
     * disabled.
     *
     * @param message            the detail message.
     * @param cause              the cause. (A {@code null} value is permitted, and
     *                           indicates that the cause is nonexistent or
     *                           unknown.)
     * @param enableSuppression  whether or not suppression is enabled or disabled
     * @param writableStackTrace whether or not the stack trace should be writable
     * @author WFF
     * @since 1.0.0
     */
    public WffRuntimeException(final String message, final Throwable cause, final boolean enableSuppression,
            final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    /**
     * Constructs a new runtime exception with the specified detail message and
     * cause.
     * <p>
     * Note that the detail message associated with {@code cause} is <i>not</i>
     * automatically incorporated in this runtime exception's detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the
     *                {@link #getMessage()} method).
     * @param cause   the cause (which is saved for later retrieval by the
     *                {@link #getCause()} method). (A <em>null</em> value is
     *                permitted, and indicates that the cause is nonexistent or
     *                unknown.)
     * @author WFF
     * @since 1.0.0
     */
    public WffRuntimeException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new runtime exception with the specified detail message. The
     * cause is not initialized, and may subsequently be initialized by a call to
     * {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for later
     *                retrieval by the {@link #getMessage()} method.
     * @author WFF
     * @since 1.0.0
     */
    public WffRuntimeException(final String message) {
        super(message);
    }

    /**
     * Constructs a new runtime exception with the specified cause and a detail
     * message of <em>(cause==null ? null : cause.toString())</em> (which typically
     * contains the class and detail message of <em>cause</em>). This constructor is
     * useful for runtime exceptions that are little more than wrappers for other
     * throwables.
     *
     * @param cause the cause (which is saved for later retrieval by the
     *              {@link #getCause()} method). (A <em>null</em> value is
     *              permitted, and indicates that the cause is nonexistent or
     *              unknown.)
     * @author WFF
     * @since 1.0.0
     */
    public WffRuntimeException(final Throwable cause) {
        super(cause);
    }

}
