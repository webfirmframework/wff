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
package com.webfirmframework.wffweb.tag.html.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author WFF
 * @since 3.0.3
 */
public enum IndexedTagName {

    // NB: indexing of tags must be done in a separate enum without including
    // any class of tag otherwise initialization of tagNameIndex may not be
    // correct. It could also lead to initialization error.
    INSTANCE;

    private final List<String> sortedTagNames;

    private final Map<String, Integer> indexedTagNames;

    private IndexedTagName() {

        final PreIndexedTagName[] values = PreIndexedTagName.values();

        final int initialCapacity = values.length;

        sortedTagNames = new ArrayList<>(initialCapacity);
        indexedTagNames = new ConcurrentHashMap<>(initialCapacity);

        for (final PreIndexedTagName each : values) {
            sortedTagNames.add(each.tagName());
            indexedTagNames.put(each.tagName(), each.index());
        }
    }

    List<String> sortedTagNames() {
        return sortedTagNames;
    }

    Map<String, Integer> indexedTagNames() {
        return indexedTagNames;
    }

    /**
     * @param tagName
     * @return the index of tag name
     * @since 3.0.3
     */
    public Integer getIndexByTagName(final String tagName) {
        return indexedTagNames.get(tagName);
    }

}
