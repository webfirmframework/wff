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
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import com.webfirmframework.wffweb.tag.html.AbstractHtml;
import com.webfirmframework.wffweb.tag.html.attribute.core.AbstractAttribute;
import com.webfirmframework.wffweb.tag.html.attributewff.CustomAttribute;
import com.webfirmframework.wffweb.tag.html.listener.ChildTagAppendListener;
import com.webfirmframework.wffweb.util.WffBinaryMessageUtil;
import com.webfirmframework.wffweb.util.data.NameValue;

class ChildTagAppendListenerImpl implements ChildTagAppendListener {

    private static final long serialVersionUID = 1L;

    public static final Logger LOGGER = Logger
            .getLogger(ChildTagRemoveListenerImpl.class.getName());

    private Object accessObject;

    private BrowserPage browserPage;

    @SuppressWarnings("unused")
    private ChildTagAppendListenerImpl() {
        throw new AssertionError();
    }

    ChildTagAppendListenerImpl(final BrowserPage browserPage,
            final Object accessObject) {
        this.browserPage = browserPage;
        this.accessObject = accessObject;
    }

    @Override
    public void childAppended(final Event event) {

        try {

            // should always be taken from browserPage as it could be changed
            final WebSocketPushListener wsListener = browserPage
                    .getWsListener();

            final AbstractHtml parentTag = event.getParentTag();
            final AbstractHtml appendedChildTag = event.getAppendedChildTag();

            // add data-wff-id to all tags including nested tags
            final Deque<Set<AbstractHtml>> childrenStack = new ArrayDeque<Set<AbstractHtml>>();
            childrenStack.push(new LinkedHashSet<AbstractHtml>(
                    Arrays.asList(appendedChildTag)));

            while (childrenStack.size() > 0) {
                final Set<AbstractHtml> children = childrenStack.pop();
                for (final AbstractHtml child : children) {

                    final String wffId = browserPage.getNewDataWffId();
                    child.addAttributes(accessObject, false,
                            new CustomAttribute("data-wff-id", wffId));

                    final Set<AbstractHtml> subChildren = child
                            .getChildren(accessObject);
                    if (subChildren != null && subChildren.size() > 0) {
                        childrenStack.push(subChildren);
                    }

                }
            }

            final AbstractAttribute attribute = parentTag
                    .getAttributeByName("data-wff-id");

            if (attribute == null) {
                LOGGER.warning(
                        "Could not find data-wff-id from direct parent tag");
            }

            //@formatter:off
                // appended child task format :-
                // { "name": task_byte, "values" : [invoke_method_byte_from_Task_enum]}, { "name": data-wff-id, "values" : [ parent_tag_name, html_string ]}
                // { "name": 2, "values" : [[3]]}, { "name":"C55", "values" : ["body", "<div><div></div></div>"]}
                //@formatter:on

            final NameValue task = Task.APPENDED_CHILD_TAG.getTaskNameValue();

            final NameValue nameValue = new NameValue();

            final byte[][] tagNameAndWffId = DataWffIdUtil
                    .getTagNameAndWffId(parentTag);

            final byte[] parentWffIdBytes = tagNameAndWffId[1];

            nameValue.setName(parentWffIdBytes);

            final byte[] parentTagName = tagNameAndWffId[0];

            nameValue.setValues(parentTagName,
                    appendedChildTag.toHtmlString("UTF-8").getBytes("UTF-8"));

            final byte[] wffBMBytes = WffBinaryMessageUtil.VERSION_1
                    .getWffBinaryMessageBytes(task, nameValue);

            wsListener.push(wffBMBytes);

        } catch (final UnsupportedEncodingException e) {
            e.printStackTrace();
            LOGGER.severe(e.toString());
        }

    }

    @Override
    public void childrenAppended(final Event event) {

        try {
            // should always be taken from browserPage as it could be changed
            final WebSocketPushListener wsListener = browserPage
                    .getWsListener();

            final AbstractHtml parentTag = event.getParentTag();
            final Collection<? extends AbstractHtml> appendedChildTags = event
                    .getAppendedChildrenTags();

            // add data-wff-id to all tags including nested tags
            final Deque<Set<AbstractHtml>> childrenStack = new ArrayDeque<Set<AbstractHtml>>();
            childrenStack
                    .push(new LinkedHashSet<AbstractHtml>(appendedChildTags));

            while (childrenStack.size() > 0) {
                final Set<AbstractHtml> children = childrenStack.pop();
                for (final AbstractHtml child : children) {

                    final String wffId = browserPage.getNewDataWffId();
                    child.addAttributes(accessObject, false,
                            new CustomAttribute("data-wff-id", wffId));

                    final Set<AbstractHtml> subChildren = child
                            .getChildren(accessObject);
                    if (subChildren != null && subChildren.size() > 0) {
                        childrenStack.push(subChildren);
                    }

                }
            }

            final AbstractAttribute attribute = parentTag
                    .getAttributeByName("data-wff-id");

            if (attribute == null) {
                LOGGER.warning(
                        "Could not find data-wff-id from direct parent tag");
            }

            //@formatter:off
                // appended child task format :-
                // { "name": task_byte, "values" : [invoke_method_byte_from_Task_enum]}, { "name": data-wff-id, "values" : [ parent_tag_name, html_string ]}
                // { "name": 2, "values" : [[3]]}, { "name":"C55", "values" : ["body", "<div><div></div></div>"]}
                //@formatter:on

            final NameValue task = Task.APPENDED_CHILDREN_TAGS
                    .getTaskNameValue();

            final Deque<NameValue> nameValues = new ArrayDeque<NameValue>();
            nameValues.add(task);

            for (final AbstractHtml appendedChildTag : appendedChildTags) {

                final NameValue nameValue = new NameValue();

                final byte[][] tagNameAndWffId = DataWffIdUtil
                        .getTagNameAndWffId(parentTag);

                final byte[] parentWffIdBytes = tagNameAndWffId[1];

                nameValue.setName(parentWffIdBytes);

                final byte[] parentTagName = tagNameAndWffId[0];

                nameValue.setValues(parentTagName, appendedChildTag
                        .toHtmlString("UTF-8").getBytes("UTF-8"));

                nameValues.add(nameValue);
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
    public void childMoved(final ChildMovedEvent event) {
        // should always be taken from browserPage as it could be changed
        final WebSocketPushListener wsListener = browserPage.getWsListener();

        //@formatter:off
        // moved children tags from some parents to another task format (in this method moving only one child) :-
        // { "name": task_byte, "values" : [REMOVED_TAGS_byte_from_Task_enum]}, { "name": new_parent_data-wff-id, "values" : [ new_parent_tag_name, child_data-wff-id, child_tag_name ]}
        // { "name": 2, "values" : [[3]]}, { "name":"C55", "values" : ["div", "S255", "span"]}
        //@formatter:on

        try {

            final AbstractHtml currentParentTag = event.getCurrentParentTag();
            final AbstractHtml movedChildTag = event.getMovedChildTag();

            final NameValue task = Task.MOVED_CHILDREN_TAGS.getTaskNameValue();

            final AbstractAttribute currentParentAttr = currentParentTag
                    .getAttributeByName("data-wff-id");

            if (currentParentAttr != null) {

                final NameValue nameValue = new NameValue();

                final byte[][] currentParentTagNameAndWffId = DataWffIdUtil
                        .getTagNameAndWffId(currentParentTag);

                final byte[] parentWffIdBytes = currentParentTagNameAndWffId[1];

                nameValue.setName(parentWffIdBytes);

                final byte[] currentTagName = currentParentTagNameAndWffId[0];

                final byte[][] movedChildTagNameAndWffId = DataWffIdUtil
                        .getTagNameAndWffId(movedChildTag);

                final byte[] movedChildWffIdBytes = movedChildTagNameAndWffId[1];

                final byte[] movedChildTagName = movedChildTagNameAndWffId[0];

                nameValue.setValues(currentTagName, movedChildWffIdBytes,
                        movedChildTagName);

                final byte[] wffBMBytes = WffBinaryMessageUtil.VERSION_1
                        .getWffBinaryMessageBytes(task, nameValue);

                wsListener.push(wffBMBytes);
            } else {
                LOGGER.severe(
                        "Could not find data-wff-id from previousParentTag");
            }
        } catch (final UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void childrendAppendedOrMoved(final List<ChildMovedEvent> events) {

        // should always be taken from browserPage as it could be changed
        final WebSocketPushListener wsListener = browserPage.getWsListener();

        //@formatter:off
        // moved children tags from some parents to another task format (in this method moving only one child) :-
        // { "name": task_byte, "values" : [REMOVED_TAGS_byte_from_Task_enum]}, { "name": new_parent_data-wff-id, "values" : [ new_parent_tag_name, child_data-wff-id, child_tag_name ]}
        // { "name": 2, "values" : [[3]]}, { "name":"C55", "values" : ["div", "S255", "span"]}
        //@formatter:on

        try {

            final NameValue task = Task.MOVED_CHILDREN_TAGS.getTaskNameValue();

            final Deque<NameValue> nameValues = new ArrayDeque<NameValue>();
            nameValues.add(task);

            for (final ChildMovedEvent event : events) {

                // if previousParentTag == null it means it's appending a new
                // child tag
                // this checking is done at client side
                final AbstractHtml previousParentTag = event
                        .getPreviousParentTag();
                final AbstractHtml currentParentTag = event
                        .getCurrentParentTag();
                final AbstractHtml movedChildTag = event.getMovedChildTag();

                final AbstractAttribute currentParentAttr = currentParentTag
                        .getAttributeByName("data-wff-id");

                if (currentParentAttr != null) {

                    final NameValue nameValue = new NameValue();

                    final byte[][] currentParentTagNameAndWffId = DataWffIdUtil
                            .getTagNameAndWffId(currentParentTag);

                    final byte[] parentWffIdBytes = currentParentTagNameAndWffId[1];

                    nameValue.setName(parentWffIdBytes);

                    final byte[] currentTagName = currentParentTagNameAndWffId[0];

                    final byte[][] movedChildTagNameAndWffId = DataWffIdUtil
                            .getTagNameAndWffId(movedChildTag);

                    final byte[] movedChildWffIdBytes = movedChildTagNameAndWffId[1];

                    final byte[] movedChildTagName = movedChildTagNameAndWffId[0];

                    if (previousParentTag == null) {
                        // if the previousParentTag is null it means it's a new
                        // tag
                        nameValue.setValues(currentTagName,
                                movedChildWffIdBytes, movedChildTagName,
                                movedChildTag.toHtmlString("UTF-8")
                                        .getBytes("UTF-8"));
                    } else {
                        nameValue.setValues(currentTagName,
                                movedChildWffIdBytes, movedChildTagName);
                    }

                    nameValues.add(nameValue);

                } else {
                    LOGGER.severe(
                            "Could not find data-wff-id from previousParentTag");
                }

            }

            final byte[] wffBMBytes = WffBinaryMessageUtil.VERSION_1
                    .getWffBinaryMessageBytes(nameValues);

            wsListener.push(wffBMBytes);

        } catch (final UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }
}
