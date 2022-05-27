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
package com.webfirmframework.wffweb.tag.html;

import com.webfirmframework.wffweb.common.URIEvent;

public record TagEvent(AbstractHtml sourceTag, URIEvent uriEvent) {

    /**
     * @return the uri
     * @deprecated This method will be removed in the next release, use
     *             {@link #uriEvent()} and get the {@link URIEvent#uriAfter()}, i.e.
     *             the current uri.
     */
    @Deprecated(forRemoval = true, since = "12.0.0-beta.5")
    public String uri() {
        return uriEvent.uriAfter();
    }
}
