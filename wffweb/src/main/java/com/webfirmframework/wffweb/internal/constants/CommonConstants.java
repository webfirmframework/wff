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
package com.webfirmframework.wffweb.internal.constants;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author WFF
 * @since 1.0.0
 * @version 1.1.0
 *
 */
public final class CommonConstants {

    /**
     * It is the maximum value for color
     *
     * @since 3.0.2
     */
    public static final long FFFFFFFF_HEX_VALUE = Long.parseLong("FFFFFFFF", 16);

    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    /**
     *
     * @author WFF
     * @since 1.0.0
     */
    private CommonConstants() {
        throw new AssertionError();
    }

}
