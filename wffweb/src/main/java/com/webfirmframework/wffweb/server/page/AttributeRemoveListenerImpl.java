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

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.webfirmframework.wffweb.internal.security.object.SecurityObject;
import com.webfirmframework.wffweb.internal.tag.html.listener.AttributeRemoveListener;
import com.webfirmframework.wffweb.tag.html.AbstractHtml;
import com.webfirmframework.wffweb.tag.html.attribute.core.AbstractAttribute;
import com.webfirmframework.wffweb.tag.html.attribute.core.AttributeUtil;
import com.webfirmframework.wffweb.util.data.NameValue;

public final class AttributeRemoveListenerImpl implements AttributeRemoveListener {

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = Logger.getLogger(AttributeRemoveListenerImpl.class.getName());

    private final BrowserPage browserPage;

    private final Map<String, AbstractHtml> tagByWffId;

    private final SecurityObject accessObject;

    @SuppressWarnings("unused")
    private AttributeRemoveListenerImpl() {
        throw new AssertionError();
    }

    AttributeRemoveListenerImpl(final BrowserPage browserPage, final SecurityObject accessObject,
            final Map<String, AbstractHtml> tagByWffId) {
        this.browserPage = browserPage;
        this.accessObject = accessObject;
        this.tagByWffId = tagByWffId;
    }

    @Override
    public void removedAttributes(@SuppressWarnings("exports") final RemovedEvent event) {

        try {

            final AbstractHtml removedFromTag = event.removedFromTag();

            if (removedFromTag.getDataWffId() == null
                    || !tagByWffId.containsKey(removedFromTag.getDataWffId().getValue())) {
                return;
            }

            // @formatter:off
            // removed attribute task format :-
            // { "name": task_byte, "values" : [REMOVED_ATTRIBUTES_byte_from_Task_enum]}, {
            // "name": MANY_TO_ONE_byte, "values" : [ tagName, its_data-wff-id,
            // attribute_name1, attribute_name2 ]}
            // { "name": 2, "values" : [[1]]}, { "name":[2], "values" : ["div", "C55",
            // "style", "name"]}
            // @formatter:on

            final NameValue task = Task.REMOVED_ATTRIBUTES.getTaskNameValue();

            final NameValue nameValue = new NameValue();
            // many attributes to one tag
            nameValue.setName(Task.MANY_TO_ONE.getValueByte());

            final byte[][] tagNameAndWffId = DataWffIdUtil.getIndexedTagNameAndWffId(accessObject, removedFromTag);

            final List<AbstractAttribute> removedAttributes = event.removedAttributes();

            if (removedAttributes != null) {

                final int totalValues = removedAttributes.size() + 2;

                final byte[][] values = new byte[totalValues][0];

                values[0] = tagNameAndWffId[0];
                values[1] = tagNameAndWffId[1];

                for (int i = 2; i < totalValues; i++) {
                    // should be just name
                    // final byte[] attrNameBytes = removedAttributes[i - 2]
                    // .getBytes(StandardCharsets.UTF_8);
                    // values[i] = attrNameBytes;

                    values[i] = AttributeUtil.getAttrNameBytesCompressedByIndex(accessObject,
                            removedAttributes.get(i - 2), StandardCharsets.UTF_8);
                }

                nameValue.setValues(values);

            } else {
                final String[] removedAttributeNames = event.removedAttributeNames();

                final int totalValues = removedAttributeNames.length + 2;

                final byte[][] values = new byte[totalValues][0];

                values[0] = tagNameAndWffId[0];
                values[1] = tagNameAndWffId[1];

                for (int i = 2; i < totalValues; i++) {
                    // should be just name
                    // final byte[] attrNameBytes = removedAttributes[i - 2]
                    // .getBytes(StandardCharsets.UTF_8);
                    // values[i] = attrNameBytes;

                    values[i] = AttributeUtil.getAttrNameBytesCompressedByIndex(accessObject,
                            removedAttributeNames[i - 2], StandardCharsets.UTF_8);
                }

                nameValue.setValues(values);
            }

            browserPage.push(task, nameValue);

        } catch (final Exception e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }

    }

    // @Override
    // public void removedAttributes(RemovedEvent event) {
    //
    // // TODO incorrect implementation,
    //
    // // should always be taken from browserPage as it could be changed
    // WebSocketPushListener wsListener = browserPage.getWsListener();
    //
    // try {
    // if (wsListener != null) {
    //
//                //@formatter:off
//                // removed attribute task format :-
//                // { "name": task_byte, "values" : [REMOVED_ATTRIBUTE_byte_from_Task_enum]}, { "name": attribute_name, "values" : [ data-wff-id, data-wff-id ]}
//                // { "name": 2, "values" : [[1]]}, { "name":"onclick", "values" : ["C55", "S555"]}
//                //@formatter:on
    //
    // final NameValue task = Task.REMOVED_ATTRIBUTES
    // .getTaskNameValue();
    // final NameValue nameValue = new NameValue();
    //
    // // should be name
    // String attrName = event.sourceAttribute().getAttributeName();
    //
    // nameValue.setName(attrName.getBytes("UTF-8"));
    //
    // final AbstractHtml[] ownerTags = event.getRemovedFromTags();
    //
    // final byte[][] dataWffIds = new byte[ownerTags.length][0];
    //
    // int count = 0;
    //
    // for (final AbstractHtml owner : ownerTags) {
    //
    // final AbstractAttribute attribute = owner
    // .getAttributeByName("data-wff-id");
    //
    // if (attribute != null) {
    //
    // final byte[] dataWffIdBytes = DataWffIdUtil
    // .getDataWffIdBytes(
    // attribute.getAttributeValue());
    //
    // dataWffIds[count] = dataWffIdBytes;
    // count++;
    //
    // } else {
    // LOGGER.severe(
    // "Could not find data-wff-id from owner tag");
    // }
    // }
    //
    // byte[][] values = dataWffIds;
    //
    // if (ownerTags.length > count) {
    // values = new byte[count][0];
    // System.arraycopy(dataWffIds, 0, values, 0, values.length);
    // }
    //
    // nameValue.setValues(values);
    //
    // final byte[] wffBinaryMessageBytes = WffBinaryMessageUtil.VERSION_1
    // .getWffBinaryMessageBytes(task, nameValue);
    //
    // wsListener.push(wffBinaryMessageBytes);
    //
    // } else {
    // LOGGER.severe(
    // "setWebSocketPushListener must be set to sent server changes to client");
    // }
    // } catch (final UnsupportedEncodingException e) {
    // if (LOGGER.isLoggable(Level.SEVERE)) { LOGGER.log(Level.SEVERE,
    // e.getMessage(), e); }
    // }
    // }
}
