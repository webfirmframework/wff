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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.webfirmframework.wffweb.tag.html.TagNameConstants;

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

        final Field[] fields = TagNameConstants.class.getFields();

        int initialCapacity = fields.length;

        final Set<String> tagNamesSet = new HashSet<>(initialCapacity);

        initialCapacity = tagNamesSet.size();

        sortedTagNames = new ArrayList<>(initialCapacity);
        indexedTagNames = new ConcurrentHashMap<>(initialCapacity);

        for (final Field field : fields) {
            try {
                final String tagName = field.get(null).toString();
                tagNamesSet.add(tagName);
            } catch (final Exception e) {
                Logger.getLogger(IndexedTagName.class.getName())
                        .log(Level.SEVERE, e.getMessage(), e);
            }
        }

        sortedTagNames.addAll(tagNamesSet);
        Collections.sort(sortedTagNames, (o1, o2) -> {

            final Integer length1 = o1.length();
            final Integer length2 = o2.length();

            return length1.compareTo(length2);
        });

        int index = 0;
        for (final String tagName : sortedTagNames) {
            indexedTagNames.put(tagName, index);
            index++;
        }
    }

    List<String> getSortedTagNames() {
        return sortedTagNames;
    }

    Map<String, Integer> getIndexedTagNames() {
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
