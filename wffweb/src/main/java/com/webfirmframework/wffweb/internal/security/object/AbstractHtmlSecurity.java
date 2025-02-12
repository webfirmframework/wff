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
package com.webfirmframework.wffweb.internal.security.object;

import java.io.Serial;

import com.webfirmframework.wffweb.internal.constants.IndexedClassType;

/**
 * Note: Only for internal use.
 *
 * @since 12.0.0
 */
public final class AbstractHtmlSecurity implements SecurityObject {

    @Serial
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unused")
    private AbstractHtmlSecurity() {
        throw new AssertionError("Not allowed to create more than one object. This class is only for internal use.");
    }

    public AbstractHtmlSecurity(final Object caller) {
        if (!SecurityClassConstants.ABSTRACT_HTML.equals(caller.getClass().getName())) {
            throw new AssertionError(
                    "Not allowed to create more than one object. This class is only for internal use.");
        }
    }

    @Override
    public IndexedClassType forClassType() {
        return IndexedClassType.ABSTRACT_HTML;
    }
}
