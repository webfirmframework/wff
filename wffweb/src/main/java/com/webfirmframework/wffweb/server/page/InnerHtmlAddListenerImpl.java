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
package com.webfirmframework.wffweb.server.page;

import java.io.UnsupportedEncodingException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.webfirmframework.wffweb.tag.html.AbstractHtml;
import com.webfirmframework.wffweb.tag.html.html5.attribute.global.DataWffId;
import com.webfirmframework.wffweb.tag.html.listener.InnerHtmlAddListener;
import com.webfirmframework.wffweb.util.WffBinaryMessageUtil;
import com.webfirmframework.wffweb.util.data.NameValue;

class InnerHtmlAddListenerImpl implements InnerHtmlAddListener {

    private static final long serialVersionUID = 1L;

    public static final Logger LOGGER = Logger
            .getLogger(InnerHtmlAddListenerImpl.class.getName());

    private BrowserPage browserPage;

    private Object accessObject;

    private Map<String, AbstractHtml> tagByWffId;

    @SuppressWarnings("unused")
    private InnerHtmlAddListenerImpl() {
        throw new AssertionError();
    }

    InnerHtmlAddListenerImpl(final BrowserPage browserPage,
            final Object accessObject,
            final Map<String, AbstractHtml> tagByWffId) {
        this.browserPage = browserPage;
        this.accessObject = accessObject;
        this.tagByWffId = tagByWffId;

    }

    /**
     * adds to wffid map
     *
     * @param tag
     * @since 1.2.0
     * @author WFF
     */
    private void addInWffIdMap(final AbstractHtml tag) {

        final Deque<Set<AbstractHtml>> childrenStack = new ArrayDeque<Set<AbstractHtml>>();
        childrenStack.push(new HashSet<AbstractHtml>(Arrays.asList(tag)));

        while (childrenStack.size() > 0) {
            final Set<AbstractHtml> children = childrenStack.pop();
            for (final AbstractHtml child : children) {

                final DataWffId wffIdAttr = child.getDataWffId();

                if (wffIdAttr != null) {
                    tagByWffId.put(wffIdAttr.getValue(), child);
                }

                final Set<AbstractHtml> subChildren = child
                        .getChildren(accessObject);
                if (subChildren != null && subChildren.size() > 0) {
                    childrenStack.push(subChildren);
                }

            }
        }

    }

    @Override
    public void innerHtmlAdded(final Event event) {

        // should always be taken from browserPage as it could be changed
        final WebSocketPushListener wsListener = browserPage.getWsListener();

        final AbstractHtml parentTag = event.getParentTag();
        final AbstractHtml innerHtmlTag = event.getInnerHtmlTag();

        //@formatter:off
        // removed all children tags task format :-
        // { "name": task_byte, "values" : [ADDED_INNER_HTML_byte_from_Task_enum]}, { "name": data-wff-id, "values" : [ parent_tag_name, html_string ]}
        // { "name": 2, "values" : [[3]]}, { "name":"C55", "values" : ["div", "<span></span>]}
        //@formatter:on

        try {
            final NameValue task = Task.ADDED_INNER_HTML.getTaskNameValue();

            final Deque<NameValue> nameValues = new ArrayDeque<NameValue>();
            nameValues.add(task);

            final DataWffId dataWffId = parentTag.getDataWffId();

            if (dataWffId != null) {

                final NameValue nameValue = new NameValue();

                final byte[][] tagNameAndWffId = DataWffIdUtil
                        .getTagNameAndWffId(parentTag);

                final byte[] parentWffIdBytes = tagNameAndWffId[1];

                nameValue.setName(parentWffIdBytes);

                final byte[] parentTagName = tagNameAndWffId[0];

                nameValue.setValues(parentTagName,
                        innerHtmlTag.toWffBMBytes("UTF-8"));

                final byte[] wffBMBytes = WffBinaryMessageUtil.VERSION_1
                        .getWffBinaryMessageBytes(task, nameValue);

                wsListener.push(wffBMBytes);

                addInWffIdMap(innerHtmlTag);
            } else {
                LOGGER.severe("Could not find data-wff-id from owner tag");
            }
        } catch (final UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

}