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
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.webfirmframework.wffweb.InvalidTagException;
import com.webfirmframework.wffweb.internal.security.object.SecurityObject;
import com.webfirmframework.wffweb.internal.server.page.js.WffJsFile;
import com.webfirmframework.wffweb.internal.tag.html.listener.ChildTagAppendListener;
import com.webfirmframework.wffweb.tag.html.AbstractHtml;
import com.webfirmframework.wffweb.tag.html.TagUtil;
import com.webfirmframework.wffweb.tag.html.html5.attribute.global.DataWffId;
import com.webfirmframework.wffweb.util.data.NameValue;

public final class ChildTagAppendListenerImpl implements ChildTagAppendListener {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = Logger.getLogger(ChildTagRemoveListenerImpl.class.getName());

    private final SecurityObject accessObject;

    private final BrowserPage browserPage;

    private final Map<String, AbstractHtml> tagByWffId;

    @SuppressWarnings("unused")
    private ChildTagAppendListenerImpl() {
        throw new AssertionError();
    }

    ChildTagAppendListenerImpl(final BrowserPage browserPage, final SecurityObject accessObject,
            final Map<String, AbstractHtml> tagByWffId) {
        this.browserPage = browserPage;
        this.accessObject = accessObject;
        this.tagByWffId = tagByWffId;
    }

    @Override
    public void childAppended(@SuppressWarnings("exports") final Event event) {

        try {

            final AbstractHtml parentTag = event.parentTag();
            final AbstractHtml appendedChildTag = event.appendedChildTag();

            // add data-wff-id to all tags including nested tags
            final Deque<Set<AbstractHtml>> childrenStack = new ArrayDeque<>();
            final Set<AbstractHtml> initialSet = Set.of(appendedChildTag);
            childrenStack.push(initialSet);

            Set<AbstractHtml> children;
            while ((children = childrenStack.poll()) != null) {
                for (final AbstractHtml child : children) {

                    if (TagUtil.isTagged(child)) {
                        if (child.getDataWffId() == null) {
                            child.setDataWffId(browserPage.getNewDataWffId());
                        }
                        tagByWffId.put(child.getDataWffId().getValue(), child);
                    }

                    final Set<AbstractHtml> subChildren = child.getChildren(accessObject);
                    if (subChildren != null && !subChildren.isEmpty()) {
                        childrenStack.push(subChildren);
                    }

                }
            }

            final DataWffId dataWffId = parentTag.getDataWffId();

            if (dataWffId == null && LOGGER.isLoggable(Level.WARNING)) {
                LOGGER.warning("Could not find data-wff-id from direct parent tag");
            }

            // @formatter:off
            // appended child task format :-
            // { "name": task_byte, "values" : [invoke_method_byte_from_Task_enum]}, {
            // "name": data-wff-id, "values" : [ parent_tag_name, html_string ]}
            // { "name": 2, "values" : [[3]]}, { "name":"C55", "values" : ["body",
            // "<div><div></div></div>"]}
            // @formatter:on

            final NameValue task = Task.APPENDED_CHILD_TAG.getTaskNameValue();

            final NameValue nameValue = new NameValue();

            final byte[][] tagNameAndWffId = DataWffIdUtil.getIndexedTagNameAndWffId(accessObject, parentTag);

            final byte[] parentWffIdBytes = tagNameAndWffId[1];

            nameValue.setName(parentWffIdBytes);

            final byte[] parentTagName = tagNameAndWffId[0];

            if (WffJsFile.COMPRESSED_WFF_DATA) {
                nameValue.setValues(parentTagName,
                        appendedChildTag.toCompressedWffBMBytesV3(StandardCharsets.UTF_8, accessObject));
            } else {
                nameValue.setValues(parentTagName, appendedChildTag.toWffBMBytes(StandardCharsets.UTF_8, accessObject));
            }

            browserPage.push(task, nameValue);

        } catch (final InvalidTagException e) {
            if (LOGGER.isLoggable(Level.WARNING)) {
                LOGGER.log(Level.WARNING, "Do not append/add an empty NoTag as child tag, eg: new NoTag(null, \"\").\n"
                        .concat("To make a tag's children as empty then invoke removeAllChildren() method in it."), e);
            }
        }

    }

    @Override
    public void childrenAppended(@SuppressWarnings("exports") final Event event) {

        final AbstractHtml parentTag = event.parentTag();
        final Collection<? extends AbstractHtml> appendedChildTags = event.appendedChildrenTags();

        // add data-wff-id to all tags including nested tags
        final Deque<Set<AbstractHtml>> childrenStack = new ArrayDeque<>();
        childrenStack.push(new LinkedHashSet<>(appendedChildTags));

        Set<AbstractHtml> children;
        while ((children = childrenStack.poll()) != null) {
            for (final AbstractHtml child : children) {

                if (TagUtil.isTagged(child)) {
                    if (child.getDataWffId() == null) {
                        child.setDataWffId(browserPage.getNewDataWffId());
                    }
                    tagByWffId.put(child.getDataWffId().getValue(), child);
                }

                final Set<AbstractHtml> subChildren = child.getChildren(accessObject);
                if (subChildren != null && !subChildren.isEmpty()) {
                    childrenStack.push(subChildren);
                }

            }
        }

        final DataWffId dataWffId = parentTag.getDataWffId();

        if (dataWffId == null && LOGGER.isLoggable(Level.WARNING)) {
            LOGGER.warning("Could not find data-wff-id from direct parent tag");
        }

        // @formatter:off
        // appended child task format :-
        // { "name": task_byte, "values" : [invoke_method_byte_from_Task_enum]}, {
        // "name": data-wff-id, "values" : [ parent_tag_name, html_string ]}
        // { "name": 2, "values" : [[3]]}, { "name":"C55", "values" : ["body",
        // "<div><div></div></div>"]}
        // @formatter:on

        final NameValue task = Task.APPENDED_CHILDREN_TAGS.getTaskNameValue();

        final Deque<NameValue> nameValues = new ArrayDeque<>(appendedChildTags.size() + 1);
        nameValues.add(task);

        for (final AbstractHtml appendedChildTag : appendedChildTags) {

            final NameValue nameValue = new NameValue();

            final byte[][] tagNameAndWffId = DataWffIdUtil.getIndexedTagNameAndWffId(accessObject, parentTag);

            final byte[] parentWffIdBytes = tagNameAndWffId[1];

            nameValue.setName(parentWffIdBytes);

            final byte[] parentTagName = tagNameAndWffId[0];

            try {
                if (WffJsFile.COMPRESSED_WFF_DATA) {
                    nameValue.setValues(parentTagName,
                            appendedChildTag.toCompressedWffBMBytesV3(StandardCharsets.UTF_8, accessObject));
                } else {
                    nameValue.setValues(parentTagName,
                            appendedChildTag.toWffBMBytes(StandardCharsets.UTF_8, accessObject));
                }

            } catch (final InvalidTagException e) {
                if (LOGGER.isLoggable(Level.WARNING)) {
                    LOGGER.log(Level.WARNING,
                            "Do not append/add an empty NoTag as child tag, eg: new NoTag(null, \"\").\n".concat(
                                    "To make a tag's children as empty then invoke removeAllChildren() method in it."),
                            e);
                }
                continue;
            }

            nameValues.add(nameValue);
        }

        browserPage.push(nameValues.toArray(new NameValue[nameValues.size()]));

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

    @Override
    public void childMoved(@SuppressWarnings("exports") final ChildMovedEvent event) {

        // @formatter:off
        // moved children tags from some parents to another task format (in this method
        // moving only one child) :-
        // { "name": task_byte, "values" : [MOVED_CHILDREN_TAGS_byte_from_Task_enum]}, {
        // "name": new_parent_data-wff-id, "values" : [ new_parent_tag_name,
        // child_data-wff-id, child_tag_name ]}
        // { "name": 2, "values" : [[3]]}, { "name":"C55", "values" : ["div", "S255",
        // "span"]}
        // @formatter:on

        final AbstractHtml currentParentTag = event.currentParentTag();
        final AbstractHtml movedChildTag = event.movedChildTag();

        final NameValue task = Task.MOVED_CHILDREN_TAGS.getTaskNameValue();

        final DataWffId currentParentDataWffIdAttr = currentParentTag.getDataWffId();

        if (currentParentDataWffIdAttr != null) {

            final NameValue nameValue = new NameValue();

            final byte[][] currentParentTagNameAndWffId = DataWffIdUtil.getIndexedTagNameAndWffId(accessObject,
                    currentParentTag);

            final byte[] parentWffIdBytes = currentParentTagNameAndWffId[1];

            nameValue.setName(parentWffIdBytes);

            final byte[] currentTagName = currentParentTagNameAndWffId[0];

            final byte[][] movedChildTagNameAndWffId = DataWffIdUtil.getIndexedTagNameAndWffId(accessObject,
                    movedChildTag);

            final byte[] movedChildWffIdBytes = movedChildTagNameAndWffId[1];

            final byte[] movedChildTagName = movedChildTagNameAndWffId[0];

            nameValue.setValues(currentTagName, movedChildWffIdBytes, movedChildTagName);

            browserPage.push(task, nameValue);

            addInWffIdMap(movedChildTag);

        } else {
            LOGGER.severe("Could not find data-wff-id from previousParentTag");
        }

    }

    @Override
    public void childrendAppendedOrMoved(@SuppressWarnings("exports") final Collection<ChildMovedEvent> events) {

        // @formatter:off
        // moved children tags from some parents to another task format (in this method
        // moving only one child) :-
        // { "name": task_byte, "values" : [MOVED_CHILDREN_TAGS_byte_from_Task_enum]}, {
        // "name": new_parent_data-wff-id, "values" : [ new_parent_tag_name,
        // child_data-wff-id, child_tag_name ]}
        // { "name": 2, "values" : [[3]]}, { "name":"C55", "values" : ["div", "S255",
        // "span"]}
        // @formatter:on

        try {

            final NameValue task = Task.MOVED_CHILDREN_TAGS.getTaskNameValue();

            final Deque<NameValue> nameValues = new ArrayDeque<>();
            nameValues.add(task);

            for (final ChildMovedEvent event : events) {

                // if previousParentTag == null it means it's appending a new
                // child tag
                // this checking is done at client side
                final AbstractHtml previousParentTag = event.previousParentTag();
                final AbstractHtml currentParentTag = event.currentParentTag();
                final AbstractHtml movedChildTag = event.movedChildTag();

                final DataWffId currentParentDataWffIdAttr = currentParentTag.getDataWffId();

                if (currentParentDataWffIdAttr != null) {

                    final NameValue nameValue = new NameValue();

                    final byte[][] currentParentTagNameAndWffId = DataWffIdUtil.getIndexedTagNameAndWffId(accessObject,
                            currentParentTag);

                    final byte[] parentWffIdBytes = currentParentTagNameAndWffId[1];

                    nameValue.setName(parentWffIdBytes);

                    final byte[] currentTagName = currentParentTagNameAndWffId[0];

                    final boolean noTag = movedChildTag.getTagName() == null;

                    final byte[][] movedChildTagNameAndWffId = noTag ? DataWffIdUtil.getTagNameAndWffIdForNoTag()
                            : DataWffIdUtil.getIndexedTagNameAndWffId(accessObject, movedChildTag);

                    final byte[] movedChildWffIdBytes = movedChildTagNameAndWffId[1];

                    final byte[] movedChildTagName = movedChildTagNameAndWffId[0];

                    if (previousParentTag == null) {
                        try {
                            // if the previousParentTag is null it means it's a
                            // new
                            // tag
                            if (WffJsFile.COMPRESSED_WFF_DATA) {
                                nameValue.setValues(currentTagName, movedChildWffIdBytes, movedChildTagName,
                                        movedChildTag.toCompressedWffBMBytesV3(StandardCharsets.UTF_8, accessObject));
                            } else {
                                nameValue.setValues(currentTagName, movedChildWffIdBytes, movedChildTagName,
                                        movedChildTag.toWffBMBytes(StandardCharsets.UTF_8, accessObject));
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
                        nameValue.setValues(currentTagName, movedChildWffIdBytes, movedChildTagName);
                    }

                    nameValues.add(nameValue);

                    addInWffIdMap(movedChildTag);

                } else {
                    LOGGER.severe("Could not find data-wff-id from previousParentTag");
                }

            }

            browserPage.push(nameValues.toArray(new NameValue[nameValues.size()]));

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
}
