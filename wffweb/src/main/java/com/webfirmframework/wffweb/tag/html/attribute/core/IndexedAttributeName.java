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
package com.webfirmframework.wffweb.tag.html.attribute.core;

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

import com.webfirmframework.wffweb.tag.html.attribute.AttributeNameConstants;
import com.webfirmframework.wffweb.tag.html.attribute.InternalAttrNameConstants;

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
        final Field[] fields = AttributeNameConstants.class.getFields();

        int initialCapacity = fields.length;

        final Set<String> attributeNamesSet = new HashSet<>(initialCapacity);
        // DataWffId.ATTRIBUTE_NAME should not be referenced, it will cause
        // class initialization exception

        final Logger logger = Logger
                .getLogger(IndexedAttributeName.class.getName());

        for (final Field field : InternalAttrNameConstants.class.getFields()) {
            try {
                final String attrName = field.get(null).toString();
                attributeNamesSet.add(attrName);
            } catch (final Exception e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        }

        initialCapacity = attributeNamesSet.size();

        sortedAttrNames = new ArrayList<>(initialCapacity);
        indexedAttrNames = new ConcurrentHashMap<>(initialCapacity);

        for (final Field field : fields) {
            try {
                final String attrName = field.get(null).toString();
                attributeNamesSet.add(attrName);
            } catch (final Exception e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        }

        sortedAttrNames.addAll(attributeNamesSet);
        Collections.sort(sortedAttrNames, (o1, o2) -> {

            final Integer length1 = o1.length();
            final Integer length2 = o2.length();

            return length1.compareTo(length2);
        });

        int index = 0;
        for (final String attrName : sortedAttrNames) {
            indexedAttrNames.put(attrName, index);
            index++;
        }
    }

    List<String> getSortedAttrNames() {
        return sortedAttrNames;
    }

    Map<String, Integer> getIndexedAttrNames() {
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
