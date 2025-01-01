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
package com.webfirmframework.wffweb.tag.html.attribute.core;

import java.util.UUID;

import com.webfirmframework.wffweb.internal.ObjectId;

/**
 * Note: only for internal use. It should be specifically for AbstractAttribute
 * class id generation.
 *
 * @author WFF
 * @since 3.0.19
 *
 */
final class AttributeIdGenerator {

    static ObjectId nextId() {
        final UUID uuid = UUID.randomUUID();
        return new ObjectId(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits());
    }

}
