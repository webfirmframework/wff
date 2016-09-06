/*
 * Copyright 2014-2016 Web Firm Framework
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
package com.webfirmframework.wffweb.page;

import java.io.UnsupportedEncodingException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.logging.Logger;

import com.webfirmframework.wffweb.tag.html.AbstractHtml;
import com.webfirmframework.wffweb.tag.html.attribute.core.AbstractAttribute;
import com.webfirmframework.wffweb.tag.html.listener.ChildTagRemoveListener;
import com.webfirmframework.wffweb.util.WffBinaryMessageUtil;
import com.webfirmframework.wffweb.util.data.NameValue;

class ChildTagRemoveListenerImpl implements ChildTagRemoveListener {

    private static final long serialVersionUID = 1L;

    public static final Logger LOGGER = Logger
            .getLogger(ChildTagRemoveListenerImpl.class.getName());

    private BrowserPage browserPage;

    @SuppressWarnings("unused")
    private ChildTagRemoveListenerImpl() {
        throw new AssertionError();
    }

    ChildTagRemoveListenerImpl(final BrowserPage browserPage) {
        this.browserPage = browserPage;
    }

    @Override
    public void childRemoved(final Event event) {

        final AbstractHtml removedChildTag = event.getRemovedChildTag();

        removeChildren(new AbstractHtml[] { removedChildTag });

    }

    private void removeChildren(final AbstractHtml[] removedChildrenTags) {
        try {
            // should always be taken from browserPage as it could be changed
            final WebSocketPushListener wsListener = browserPage
                    .getWsListener();
            //@formatter:off
            // removed child task format :-
            // { "name": task_byte, "values" : [REMOVED_TAGS_byte_from_Task_enum]}, { "name": data-wff-id, "values" : [ parent_tag_name, html_string ]}
            // { "name": 2, "values" : [[3]]}, { "name":"C55", "values" : ["div"]}
            //@formatter:on

            final NameValue task = Task.REMOVED_TAGS.getTaskNameValue();

            final Deque<NameValue> nameValues = new ArrayDeque<NameValue>();
            nameValues.add(task);

            for (final AbstractHtml removedChildTag : removedChildrenTags) {

                final AbstractAttribute attribute = removedChildTag
                        .getAttributeByName("data-wff-id");

                if (attribute != null) {

                    final NameValue nameValue = new NameValue();

                    final byte[][] tagNameAndWffId = DataWffIdUtil
                            .getTagNameAndWffId(removedChildTag);

                    final byte[] parentWffIdBytes = tagNameAndWffId[1];

                    nameValue.setName(parentWffIdBytes);

                    final byte[] parentTagName = tagNameAndWffId[0];

                    nameValue.setValues(parentTagName);

                    nameValues.add(nameValue);

                } else {
                    LOGGER.severe("Could not find data-wff-id from owner tag");
                }

            }

            final byte[] wffBMBytes = WffBinaryMessageUtil.VERSION_1
                    .getWffBinaryMessageBytes(nameValues);

            wsListener.push(wffBMBytes);
        } catch (final UnsupportedEncodingException e) {
            e.printStackTrace();
            LOGGER.severe(e.toString());
        }
    }

    @Override
    public void childrenRemoved(final Event event) {
        removeChildren(event.getRemovedChildrenTags());
    }

    @Override
    public void allChildrenRemoved(final Event event) {

        // should always be taken from browserPage as it could be changed
        final WebSocketPushListener wsListener = browserPage.getWsListener();

        final AbstractHtml parentTag = event.getParentTag();

        //@formatter:off
        // removed all children tags task format :-
        // { "name": task_byte, "values" : [REMOVED_TAGS_byte_from_Task_enum]}, { "name": data-wff-id, "values" : [ parent_tag_name]}
        // { "name": 2, "values" : [[3]]}, { "name":"C55", "values" : ["div"]}
        //@formatter:on

        try {
            final NameValue task = Task.REMOVED_ALL_CHILDREN_TAGS
                    .getTaskNameValue();

            final AbstractAttribute attribute = parentTag
                    .getAttributeByName("data-wff-id");

            if (attribute != null) {

                final NameValue nameValue = new NameValue();

                final byte[][] tagNameAndWffId = DataWffIdUtil
                        .getTagNameAndWffId(parentTag);

                final byte[] parentWffIdBytes = tagNameAndWffId[1];

                nameValue.setName(parentWffIdBytes);

                final byte[] parentTagName = tagNameAndWffId[0];

                nameValue.setValues(parentTagName);

                final byte[] wffBMBytes = WffBinaryMessageUtil.VERSION_1
                        .getWffBinaryMessageBytes(task, nameValue);

                wsListener.push(wffBMBytes);
            } else {
                LOGGER.severe("Could not find data-wff-id from owner tag");
            }
        } catch (final UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

}