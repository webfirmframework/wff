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

import com.webfirmframework.wffweb.tag.html.SharedTagContent.Content;
import com.webfirmframework.wffweb.tag.html.SharedTagContent.ContentFormatter;

/**
 * NB: only for internal use
 *
 * @author WFF
 * @since 3.0.6
 *
 */
final class ModifiedParentData<T> {

    private final AbstractHtml parent;

    private final SharedTagContent.Content<String> contentApplied;

    private final SharedTagContent.ContentFormatter<T> formatter;

    public ModifiedParentData(final AbstractHtml parent, final Content<String> contentApplied,
            final ContentFormatter<T> formatter) {
        super();
        this.parent = parent;
        this.contentApplied = contentApplied;
        this.formatter = formatter;
    }

    AbstractHtml parent() {
        return parent;
    }

    SharedTagContent.Content<String> contentApplied() {
        return contentApplied;
    }

    SharedTagContent.ContentFormatter<T> formatter() {
        return formatter;
    }

}
