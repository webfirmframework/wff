/*
 * Copyright 2014-2022 Web Firm Framework
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

import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.webfirmframework.wffweb.InvalidTagException;
import com.webfirmframework.wffweb.server.page.js.WffJsFile;
import com.webfirmframework.wffweb.tag.html.AbstractHtml;
import com.webfirmframework.wffweb.tag.html.TagUtil;
import com.webfirmframework.wffweb.tag.html.html5.attribute.global.DataWffId;
import com.webfirmframework.wffweb.tag.html.listener.InnerHtmlAddListener;
import com.webfirmframework.wffweb.util.data.NameValue;

final class InnerHtmlAddListenerImpl implements InnerHtmlAddListener {

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = Logger.getLogger(InnerHtmlAddListenerImpl.class.getName());

    private final BrowserPage browserPage;

    private final Object accessObject;

    private final Map<String, AbstractHtml> tagByWffId;

    @SuppressWarnings("unused")
    private InnerHtmlAddListenerImpl() {
        throw new AssertionError();
    }

    InnerHtmlAddListenerImpl(final BrowserPage browserPage, final Object accessObject,
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
        // passed 2 instead of 1 because the load factor is 0.75f
        final Set<AbstractHtml> initialSet = new HashSet<>(2);
        initialSet.add(tag);
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
                if (subChildren != null && subChildren.size() > 0) {
                    childrenStack.push(subChildren);
                }

            }
        }

    }

    @Override
    public ClientTasksWrapper innerHtmlsAdded(final AbstractHtml parentTag, final Event... events) {

        // @formatter:off
        // removed all children tags task format :-
        // { "name": task_byte, "values" : [ADDED_INNER_HTML_byte_from_Task_enum]}, {
        // "name": parent_tag_name, "values" : [ data-wff-id ] }, { "name": html_string,
        // "values" : [ 1_if_there_was_a_previous_parent ]}
        // { "name": 2, "values" : [[3]]}, { "name":"C55", "values" : ["div",
        // "<span></span>", 1]}
        // @formatter:on

        final Collection<NameValue> nameValues = new ArrayDeque<>(events.length + 2);

        final NameValue task = Task.ADDED_INNER_HTML.getTaskNameValue();
        nameValues.add(task);

        final DataWffId dataWffId = parentTag.getDataWffId();

        if (dataWffId == null) {

            LOGGER.severe("Could not find data-wff-id from owner tag");
            return null;
        }

        final byte[][] tagNameAndWffId = DataWffIdUtil.getIndexedTagNameAndWffId(accessObject, parentTag);

        final byte[] parentTagNameIndexed = tagNameAndWffId[0];

        final byte[] parentWffIdBytes = tagNameAndWffId[1];

        final NameValue parentTagNameValue = new NameValue(parentTagNameIndexed, new byte[][] { parentWffIdBytes });

        nameValues.add(parentTagNameValue);

        for (final Event event : events) {

            final AbstractHtml innerHtmlTag = event.getInnerHtmlTag();
            final AbstractHtml previousParentTag = event.getPreviousParentTag();

            final NameValue nameValue = new NameValue();

            try {
                if (WffJsFile.COMPRESSED_WFF_DATA) {
                    nameValue.setName(innerHtmlTag.toCompressedWffBMBytesV2(StandardCharsets.UTF_8, accessObject));
                } else {
                    nameValue.setName(innerHtmlTag.toWffBMBytes(StandardCharsets.UTF_8, accessObject));
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

            if (previousParentTag != null) {
                nameValue.setValues(new byte[] { 1 });
            } else {
                nameValue.setValues(new byte[0][0]);
            }

            addInWffIdMap(innerHtmlTag);

            nameValues.add(nameValue);

        }

        // browserPage.push(nameValues.toArray(new
        // NameValue[nameValues.size()]));

        final Queue<Collection<NameValue>> multiTasks = new ArrayDeque<>(2);
        multiTasks.add(nameValues);

        if (DataWffIdUtil.isTagNameTextArea(parentTagNameIndexed, StandardCharsets.UTF_8)) {
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
            task2NameValues.add(parentTagNameValue);

            multiTasks.add(task2NameValues);
        }

        return browserPage.pushAndGetWrapper(multiTasks);
    }

    @Override
    public void innerHtmlAdded(final Event event) {
        innerHtmlsAdded(event.getParentTag(), event);
    }

}