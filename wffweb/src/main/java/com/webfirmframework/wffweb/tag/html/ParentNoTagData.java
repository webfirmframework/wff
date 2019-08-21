/*
 * Copyright 2014-2019 Web Firm Framework
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

import com.webfirmframework.wffweb.tag.htmlwff.NoTag;

/**
 * @author WFF
 * @since 3.0.6
 *
 */
class ParentNoTagData {

    private final AbstractHtml parent;

    private final NoTag noTag;

    private final NoTag previousNoTag;

    ParentNoTagData(final NoTag previousNoTag, final AbstractHtml parent,
            final NoTag noTag) {
        super();
        this.previousNoTag = previousNoTag;
        this.parent = parent;
        this.noTag = noTag;
    }

    AbstractHtml getParent() {
        return parent;
    }

    public NoTag getPreviousNoTag() {
        return previousNoTag;
    }

    NoTag getNoTag() {
        return noTag;
    }

}