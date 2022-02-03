/*
 * Copyright 2014-2022 Web Firm Framework
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

/**
 * A Serializable id mainly for frameworks's internal use.
 *
 * @since 3.0.18
 */
public final class InternalId implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public InternalId() {
    }
}
