/*
 * Copyright since 2014 Web Firm Framework
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

import java.io.Serial;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.webfirmframework.wffweb.InvalidTagException;
import com.webfirmframework.wffweb.internal.security.object.SecurityObject;
import com.webfirmframework.wffweb.internal.server.page.js.WffJsFile;
import com.webfirmframework.wffweb.internal.tag.html.listener.ChildCreatedOrMovedEvent;
import com.webfirmframework.wffweb.internal.tag.html.listener.TagManipulationListener;
import com.webfirmframework.wffweb.tag.html.AbstractHtml;
import com.webfirmframework.wffweb.tag.html.TagUtil;
import com.webfirmframework.wffweb.tag.html.html5.attribute.global.DataWffId;
import com.webfirmframework.wffweb.tag.htmlwff.NoTag;
import com.webfirmframework.wffweb.util.data.NameValue;

/**
 * @since 12.0.6
 */
public final class TagManipulationListenerImpl implements TagManipulationListener {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = Logger.getLogger(TagManipulationListenerImpl.class.getName());

    private final BrowserPage browserPage;
    private final SecurityObject accessObject;
    private final Map<String, AbstractHtml> tagByWffId;

    public TagManipulationListenerImpl(final BrowserPage browserPage, final SecurityObject accessObject,
            final Map<String, AbstractHtml> tagByWffId) {
        this.browserPage = browserPage;
        this.accessObject = accessObject;
        this.tagByWffId = tagByWffId;
    }

    /**
     * adds to wffid map
     *
     * @param tag
     * @since 2.0.0
     * @author WFF
     */
    private void addInWffIdMap(final AbstractHtml tag) {

        final Deque<Set<AbstractHtml>> childrenStack = new ArrayDeque<>();
        final Set<AbstractHtml> initialSet = Set.of(tag);
        childrenStack.push(initialSet);

        Set<AbstractHtml> children;
        while ((children = childrenStack.poll()) != null) {
            for (final AbstractHtml child : children) {

                if (TagUtil.isTagged(child)) {
                    final DataWffId wffIdAttr = child.getDataWffId();

                    if (wffIdAttr != null) {
                        tagByWffId.put(wffIdAttr.getValue(), child);
                    }
                }

                final Set<AbstractHtml> subChildren = child.getChildren(accessObject);
                if (subChildren != null && !subChildren.isEmpty()) {
                    childrenStack.push(subChildren);
                }

            }
        }

    }

    private void tagManipulated(final AbstractHtml targetTag, final Collection<ChildCreatedOrMovedEvent> events,
            final Task taskForNameValue) {

        // @formatter:off
        // moved children tags from some parents to another task format (in this method
        // moving only one child) :-
        // { "name": task_byte, "values" : [MOVED_CHILDREN_TAGS_byte_from_Task_enum or
        // MOVED_CHILDREN_TAGS_PREPENDED_byte_from_Task_enum]}, {
        // "name": new_parent_data-wff-id, "values" : [ new_parent_tag_name,
        // child_data-wff-id, child_tag_name ]}
        // { "name": 2, "values" : [[3]]}, { "name":"C55", "values" : ["div", "S255",
        // "span"]}
        // @formatter:on

        try {

            final NameValue task = taskForNameValue.getTaskNameValue();

            final Deque<NameValue> nameValues = new ArrayDeque<>(events.size() + 2);
            nameValues.add(task);

            boolean invalidTargetTag = false;
            byte[] targetTagNameIndexed = null;
            NameValue targetNameValue = null;

            if (targetTag != null) {
                // values.length is checked at client side to identify if the targetTag is
                // NoTag.
                if (TagUtil.isTagless(targetTag)) {
                    final AbstractHtml taggedParent = targetTag.getParent();
                    if (taggedParent == null) {
                        throw new InvalidTagException(
                                "Tag manipulation on the target NoTag is not allowed as it has no parent tag.");
                    }
                    if (TagUtil.isTagless(taggedParent)) {
                        throw new InvalidTagException(
                                "Tag manipulation on NoTag is not allowed if its parent is also a NoTag.");
                    }

                    final byte[][] targetTagNameAndChildIndex = DataWffIdUtil
                            .getIndexedTagNameAndChildIndexForNoTag(accessObject, (NoTag) targetTag);
                    final byte[] targetTagChildIndexBytes = targetTagNameAndChildIndex[1];

                    final byte[][] taggedParentNameAndWffId = DataWffIdUtil.getIndexedTagNameAndWffId(accessObject,
                            taggedParent);
                    if (taggedParentNameAndWffId != null) {
                        final NameValue nameValue = new NameValue();
                        nameValue.setName(taggedParentNameAndWffId[0]);
                        nameValue.setValues(taggedParentNameAndWffId[1], targetTagChildIndexBytes);
                        nameValues.add(nameValue);
                    } else {
                        invalidTargetTag = true;
                    }
                } else {
                    final byte[][] targetTagNameAndWffId = DataWffIdUtil.getIndexedTagNameAndWffId(accessObject,
                            targetTag);
                    if (targetTagNameAndWffId != null) {
                        targetNameValue = new NameValue();
                        targetTagNameIndexed = targetTagNameAndWffId[0];
                        targetNameValue.setName(targetTagNameIndexed);
                        targetNameValue.setValues(targetTagNameAndWffId[1]);
                        nameValues.add(targetNameValue);
                    } else {
                        invalidTargetTag = true;
                    }
                }
            } else {
                throw new InvalidTagException("Invalid target tag. Target tag is null.");
            }

            if (invalidTargetTag) {
                LOGGER.severe("Could not figure out data-wff-id for the target tag");
                throw new InvalidTagException("Invalid target tag. Could not figure out data-wff-id.");
            }

            for (final ChildCreatedOrMovedEvent event : events) {

                final AbstractHtml childTag = event.childTag();

                final NameValue nameValue = new NameValue();

                final boolean noTag = TagUtil.isTagless(childTag);

                final byte[][] childTagNameAndWffId = noTag ? DataWffIdUtil.getTagNameAndWffIdForNoTag()
                        : DataWffIdUtil.getIndexedTagNameAndWffId(accessObject, childTag);

                if (childTagNameAndWffId == null) {
                    throw new InvalidTagException("Could not find data-wff-id");
                }

                final byte[] childTagName = childTagNameAndWffId[0];

                final byte[] childTagWffIdBytes = childTagNameAndWffId[1];

                if (noTag || !event.movable()) {
                    try {
                        if (WffJsFile.COMPRESSED_WFF_DATA) {
                            nameValue.setName(childTagName);
                            if (noTag) {
                                nameValue.setValues(
                                        childTag.toCompressedWffBMBytesV3(StandardCharsets.UTF_8, accessObject));
                            } else {
                                nameValue.setValues(childTagWffIdBytes,
                                        childTag.toCompressedWffBMBytesV3(StandardCharsets.UTF_8, accessObject));
                            }
                        } else {
                            nameValue.setName(childTagName);
                            if (noTag) {
                                nameValue.setValues(childTag.toWffBMBytes(StandardCharsets.UTF_8, accessObject));
                            } else {
                                nameValue.setValues(childTagWffIdBytes,
                                        childTag.toWffBMBytes(StandardCharsets.UTF_8, accessObject));
                            }
                        }
                    } catch (final InvalidTagException e) {
                        if (LOGGER.isLoggable(Level.WARNING)) {
                            LOGGER.log(Level.WARNING,
                                    "Do not append/add an empty NoTag as child tag, eg: new NoTag(null, \"\").\n"
                                            .concat("To make a tag's children as empty then invoke removeAllChildren() method in it."),
                                    e);
                        }
                        continue;
                    }
                } else {
                    nameValue.setName(childTagName);
                    nameValue.setValues(childTagWffIdBytes);
                }

                nameValues.add(nameValue);

                addInWffIdMap(childTag);
            }

            if (DataWffIdUtil.isTagNameTextArea(targetTagNameIndexed, StandardCharsets.UTF_8)) {
                final Queue<Collection<NameValue>> multiTasks = new ArrayDeque<>(2);
                multiTasks.add(nameValues);
                // @formatter:off
                // removed all children tags task format :-
                // { "name": task_byte, "values" :
                // [COPY_INNER_TEXT_TO_VALUE_byte_from_Task_enum]}, { "name": parent_tag_name,
                // "values" : [ data-wff-id ] }
                // @formatter:on
                final NameValue copyInnerTexToValueTask = Task.COPY_INNER_TEXT_TO_VALUE.getTaskNameValue();

                // browserPage.push(copyInnerTexToValueTask, parentTagNameValue);

                final Queue<NameValue> task2NameValues = new ArrayDeque<>(2);
                task2NameValues.add(copyInnerTexToValueTask);
                task2NameValues.add(targetNameValue);
                multiTasks.add(task2NameValues);
                browserPage.pushAndGetWrapper(multiTasks);
            } else {
                browserPage.push(nameValues.toArray(new NameValue[0]));
            }

        } catch (final NoSuchElementException e) {
            if (LOGGER.isLoggable(Level.WARNING)) {
                LOGGER.log(Level.WARNING, "Do not append/add an empty NoTag as child tag, eg: new NoTag(null, \"\").\n"
                        .concat("To make a tag's children as empty then invoke removeAllChildren() method in it."), e);
            }
        } catch (final UnsupportedEncodingException e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    @Override
    public void childrenPrependedOrMoved(@SuppressWarnings("exports") final AbstractHtml targetTag,
            @SuppressWarnings("exports") final Collection<ChildCreatedOrMovedEvent> events) {
        tagManipulated(targetTag, events, Task.MOVABLE_PREPENDED_CHILDREN_TO_TAG);
    }

    @Override
    public void childrenAppendedOrMoved(@SuppressWarnings("exports") final AbstractHtml targetTag,
            @SuppressWarnings("exports") final Collection<ChildCreatedOrMovedEvent> events) {
        tagManipulated(targetTag, events, Task.MOVABLE_APPENDED_CHILDREN_TO_TAG);
    }

    @Override
    public void childrenInsertedOrMovedBeforeTag(final AbstractHtml targetTag,
            final Collection<ChildCreatedOrMovedEvent> events) {
        tagManipulated(targetTag, events, Task.MOVABLE_INSERTED_BEFORE_TAG);
    }

    @Override
    public void childrenInsertedOrMovedAfterTag(final AbstractHtml targetTag,
            final Collection<ChildCreatedOrMovedEvent> events) {
        tagManipulated(targetTag, events, Task.MOVABLE_INSERTED_AFTER_TAG);
    }

    @Override
    public void tagReplacedOrMovedWithChildren(final AbstractHtml targetTag,
            final Collection<ChildCreatedOrMovedEvent> events) {
        tagManipulated(targetTag, events, Task.MOVABLE_REPLACED_TAG);
    }

    @Override
    public void tagChildrenReplacedOrMovedWithChildren(final AbstractHtml targetTag,
            final Collection<ChildCreatedOrMovedEvent> events) {
        tagManipulated(targetTag, events, Task.MOVABLE_REPLACED_ALL_CHILDREN_OF_TAG);
    }

}
