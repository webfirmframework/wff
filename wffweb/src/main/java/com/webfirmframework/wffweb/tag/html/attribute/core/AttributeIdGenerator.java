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
package com.webfirmframework.wffweb.tag.html.attribute.core;

import com.webfirmframework.wffweb.internal.IndexedClassType;

/**
 * Note: only for internal use. It should be specifically for AbstractAttribute
 * class id generation.
 * 
 * @author WFF
 * @since 3.0.19
 *
 */
final class AttributeIdGenerator {

    private static final int CLASS_TYPE_INDEX = IndexedClassType.ABSTRACT_ATTRIBUTE.ordinal();

    private static volatile long count;

    static String nextId() {
        return (++count) + "_" + Thread.currentThread().getId() + "_" + CLASS_TYPE_INDEX;
    }

}
