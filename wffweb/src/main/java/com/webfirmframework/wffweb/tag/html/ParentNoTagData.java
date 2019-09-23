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
 * NB: only for internal use
 *
 * @author WFF
 * @since 3.0.6
 *
 *
 */
final class ParentNoTagData<T> {

    private final AbstractHtml parent;

    private final NoTag noTag;

    private final NoTag previousNoTag;

    private final InsertedTagData<T> insertedTagData;

    private final SharedTagContent.Content<String> contentApplied;

    ParentNoTagData(final NoTag previousNoTag, final AbstractHtml parent,
            final InsertedTagData<T> insertedTagData) {
        super();
        this.previousNoTag = previousNoTag;
        this.parent = parent;
        this.noTag = null;
        this.insertedTagData = insertedTagData;
        this.contentApplied = null;
    }

    ParentNoTagData(final NoTag previousNoTag, final AbstractHtml parent,
            final NoTag noTag, final InsertedTagData<T> insertedTagData,
            final SharedTagContent.Content<String> contentApplied) {
        super();
        this.previousNoTag = previousNoTag;
        this.parent = parent;
        this.noTag = noTag;
        this.insertedTagData = insertedTagData;
        this.contentApplied = contentApplied;
    }

    AbstractHtml parent() {
        return parent;
    }

    NoTag previousNoTag() {
        return previousNoTag;
    }

    NoTag getNoTag() {
        return noTag;
    }

    InsertedTagData<T> insertedTagData() {
        return insertedTagData;
    }

    SharedTagContent.Content<String> contentApplied() {
        return contentApplied;
    }

}
