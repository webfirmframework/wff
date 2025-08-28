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
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.webfirmframework.wffweb.InvalidTagException;
import com.webfirmframework.wffweb.internal.security.object.SecurityObject;
import com.webfirmframework.wffweb.internal.server.page.js.WffJsFile;
import com.webfirmframework.wffweb.internal.tag.html.listener.InsertTagsBeforeListener;
import com.webfirmframework.wffweb.tag.html.AbstractHtml;
import com.webfirmframework.wffweb.tag.html.TagUtil;
import com.webfirmframework.wffweb.tag.html.html5.attribute.global.DataWffId;
import com.webfirmframework.wffweb.tag.htmlwff.NoTag;
import com.webfirmframework.wffweb.util.data.NameValue;

/**
 * @author WFF
 * @since 3.0.7
 */
@Deprecated(forRemoval = true, since = "12.0.6")
public final class InsertTagsBeforeListenerImpl implements InsertTagsBeforeListener {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = Logger.getLogger(InsertTagsBeforeListenerImpl.class.getName());

    private final BrowserPage browserPage;

    private final SecurityObject accessObject;

    private final Map<String, AbstractHtml> tagByWffId;

    @SuppressWarnings("unused")
    private InsertTagsBeforeListenerImpl() {
        throw new AssertionError();
    }

    InsertTagsBeforeListenerImpl(final BrowserPage browserPage, final SecurityObject accessObject,
            final Map<String, AbstractHtml> tagByWffId) {
        this.browserPage = browserPage;
        this.accessObject = accessObject;
        this.tagByWffId = tagByWffId;

    }

    /**
     * adds to wffid map
     *
     * @param tag
     * @since 3.0.7
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
    public void insertedBefore(final AbstractHtml parentTag, final AbstractHtml beforeTag,
            @SuppressWarnings("exports") final Event... events) {

        // @formatter:off
        // removed all children tags task format :-
        // { "name": task_byte, "values" : [INSERTED_BEFORE_TAG_byte_from_Task_enum]}, {
        // "name": parent_data-wff-id, "values" : [ parent_tag_name, inserted_tag_html,
        // before_tag_name, before_tag_data-wff-id, 1_if_there_was_a_previous_parent ]}
        // { "name": 2, "values" : [[3]]}, { "name":"C55", "values" : ["div",
        // "<span></span>", 1]}
        // @formatter:on

        final NameValue task = Task.INSERTED_BEFORE_TAG.getTaskNameValue();

        final Deque<NameValue> nameValues = new ArrayDeque<>();
        nameValues.add(task);

        if (parentTag.getDataWffId() != null) {

            // start parent tag data
            final byte[][] parentTagNameAndWffId = DataWffIdUtil.getIndexedTagNameAndWffId(accessObject, parentTag);
            final byte[] parentTagName = parentTagNameAndWffId[0];
            final byte[] parentWffIdBytes = parentTagNameAndWffId[1];

            final NameValue parentTagNV = new NameValue();
            parentTagNV.setName(parentTagName);
            parentTagNV.setValues(parentWffIdBytes);
            nameValues.add(parentTagNV);
            // end parent tag data

            // start beforeTag data
            final byte[][] beforeTagNameAndWffId;

            if (TagUtil.isTagless(beforeTag)) {
                beforeTagNameAndWffId = DataWffIdUtil.getIndexedTagNameAndChildIndexForNoTag(accessObject,
                        (NoTag) beforeTag);
            } else {
                beforeTagNameAndWffId = DataWffIdUtil.getIndexedTagNameAndWffId(accessObject, beforeTag);
            }

            final NameValue beforeTagNV = new NameValue();
            beforeTagNV.setName(beforeTagNameAndWffId[0]);
            beforeTagNV.setValues(beforeTagNameAndWffId[1]);
            nameValues.add(beforeTagNV);
            // end beforeTag data

            // inserted tags data
            for (final Event event : events) {

                final AbstractHtml insertedTag = event.insertedTag();

                final AbstractHtml previousParentTag = event.previousParentTag();

                final NameValue nameValue = new NameValue();

                if (previousParentTag != null) {
                    nameValue.setName(new byte[] { 1 });
                } else {
                    nameValue.setName(new byte[0]);
                }

                try {
                    if (WffJsFile.COMPRESSED_WFF_DATA) {
                        nameValue.setValues(insertedTag.toCompressedWffBMBytesV3(StandardCharsets.UTF_8, accessObject));
                    } else {
                        nameValue.setValues(insertedTag.toWffBMBytes(StandardCharsets.UTF_8, accessObject));
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

                addInWffIdMap(insertedTag);
                nameValues.add(nameValue);
            }

        } else {
            LOGGER.severe("Could not find data-wff-id from owner tag");
        }

        browserPage.push(nameValues.toArray(new NameValue[nameValues.size()]));
    }

}