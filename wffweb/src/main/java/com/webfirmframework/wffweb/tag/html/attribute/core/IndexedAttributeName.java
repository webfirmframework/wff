/*
 * Copyright 2014-2020 Web Firm Framework
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author WFF
 * @since 3.0.3
 */
public enum IndexedAttributeName {

    // NB: indexing of tags must be done in a separate enum without including
    // any
    // class of attribute otherwise DataWffIdTest.testGetAttrNameIndex will fail
    INSTANCE;

    private final List<String> sortedAttrNames;

    private final Map<String, Integer> indexedAttrNames;

    private IndexedAttributeName() {
        final PreIndexedAttributeName[] values = PreIndexedAttributeName
                .values();

        final int initialCapacity = values.length;

        sortedAttrNames = new ArrayList<>(initialCapacity);
        indexedAttrNames = new ConcurrentHashMap<>(initialCapacity);

        for (final PreIndexedAttributeName each : values) {
            sortedAttrNames.add(each.attrName());
            indexedAttrNames.put(each.attrName(), each.index());
        }
    }

    List<String> sortedAttrNames() {
        return sortedAttrNames;
    }

    Map<String, Integer> indexedAttrNames() {
        return indexedAttrNames;
    }

    /**
     * @param attributeName
     * @return the index of attribute name
     * @since 3.0.3
     */
    public Integer getIndexByAttributeName(final String attributeName) {
        return indexedAttrNames.get(attributeName);
    }

}
