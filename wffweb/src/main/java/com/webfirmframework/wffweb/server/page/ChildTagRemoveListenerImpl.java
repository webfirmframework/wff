/*
 * Copyright 2014-2021 Web Firm Framework
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
package com.webfirmframework.wffweb.server.page;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.webfirmframework.wffweb.tag.html.AbstractHtml;
import com.webfirmframework.wffweb.tag.html.html5.attribute.global.DataWffId;
import com.webfirmframework.wffweb.tag.html.listener.ChildTagRemoveListener;
import com.webfirmframework.wffweb.util.data.NameValue;

final class ChildTagRemoveListenerImpl implements ChildTagRemoveListener {

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = Logger.getLogger(ChildTagRemoveListenerImpl.class.getName());

    private final BrowserPage browserPage;

    private final Object accessObject;

    private final Map<String, AbstractHtml> tagByWffId;

    @SuppressWarnings("unused")
    private ChildTagRemoveListenerImpl() {
        throw new AssertionError();
    }

    ChildTagRemoveListenerImpl(final BrowserPage browserPage, final Object accessObject,
            final Map<String, AbstractHtml> tagByWffId) {
        this.browserPage = browserPage;
        this.accessObject = accessObject;
        this.tagByWffId = tagByWffId;
    }

    private void removeFromTagByWffIdMap(final AbstractHtml tag) {

        if (!tagByWffId.isEmpty()) {
            final Deque<Set<AbstractHtml>> childrenStack = new ArrayDeque<>();
            // passed 2 instead of 1 because the load factor is 0.75f
            final Set<AbstractHtml> initialSet = new HashSet<>(2);
            initialSet.add(tag);
            childrenStack.push(initialSet);

            Set<AbstractHtml> children;
            while ((children = childrenStack.poll()) != null) {
                for (final AbstractHtml child : children) {

                    final DataWffId dataWffId = child.getDataWffId();
                    if (dataWffId != null) {
                        tagByWffId.computeIfPresent(dataWffId.getValue(), (k, v) -> {
                            if (child.equals(v)) {
                                return null;
                            }
                            return v;
                        });
                    }

                    final Set<AbstractHtml> subChildren = child.getChildren(accessObject);
                    if (subChildren != null && subChildren.size() > 0) {
                        childrenStack.push(subChildren);
                    }

                }
            }
        }

    }

    @Override
    public void childRemoved(final Event event) {

        final AbstractHtml removedChildTag = event.getRemovedChildTag();

        removeChildren(new AbstractHtml[] { removedChildTag });

    }

    private void removeChildren(final AbstractHtml[] removedChildrenTags) {

        // @formatter:off
        // removed child task format :-
        // { "name": task_byte, "values" : [REMOVED_TAGS_byte_from_Task_enum]}, {
        // "name": data-wff-id, "values" : [ parent_tag_name, html_string ]}
        // { "name": 2, "values" : [[3]]}, { "name":"C55", "values" : ["div"]}
        // @formatter:on

        final NameValue task = Task.REMOVED_TAGS.getTaskNameValue();

        final Deque<NameValue> nameValues = new ArrayDeque<>();
        nameValues.add(task);

        for (final AbstractHtml removedChildTag : removedChildrenTags) {

            final DataWffId dataWffId = removedChildTag.getDataWffId();

            if (dataWffId != null) {

                final NameValue nameValue = new NameValue();

                final byte[][] tagNameAndWffId = DataWffIdUtil.getIndexedTagNameAndWffId(accessObject, removedChildTag);

                final byte[] parentWffIdBytes = tagNameAndWffId[1];

                nameValue.setName(parentWffIdBytes);

                final byte[] parentTagName = tagNameAndWffId[0];

                nameValue.setValues(parentTagName);

                nameValues.add(nameValue);

            } else {
                LOGGER.severe("Could not find data-wff-id from owner tag");
            }

        }

        browserPage.push(nameValues.toArray(new NameValue[nameValues.size()]));

        for (final AbstractHtml removedChildTag : removedChildrenTags) {
            removeFromTagByWffIdMap(removedChildTag);
        }

    }

    @Override
    public void childrenRemoved(final Event event) {
        removeChildren(event.getRemovedChildrenTags());
    }

    @Override
    public void allChildrenRemoved(final Event event) {

        final AbstractHtml parentTag = event.getParentTag();

        // @formatter:off
        // removed all children tags task format :-
        // { "name": task_byte, "values" : [REMOVED_TAGS_byte_from_Task_enum]}, {
        // "name": data-wff-id, "values" : [ parent_tag_name]}
        // { "name": 2, "values" : [[3]]}, { "name":"C55", "values" : ["div"]}
        // @formatter:on

        final NameValue task = Task.REMOVED_ALL_CHILDREN_TAGS.getTaskNameValue();

        final DataWffId dataWffId = parentTag.getDataWffId();

        if (dataWffId != null) {

            final NameValue nameValue = new NameValue();

            final byte[][] tagNameAndWffId = DataWffIdUtil.getIndexedTagNameAndWffId(accessObject, parentTag);

            final byte[] parentWffIdBytes = tagNameAndWffId[1];

            nameValue.setName(parentWffIdBytes);

            final byte[] parentTagName = tagNameAndWffId[0];

            nameValue.setValues(parentTagName);

            browserPage.push(task, nameValue);

            for (final AbstractHtml each : event.getRemovedChildrenTags()) {
                removeFromTagByWffIdMap(each);
            }
        } else {
            LOGGER.severe("Could not find data-wff-id from owner tag");
        }

    }

}