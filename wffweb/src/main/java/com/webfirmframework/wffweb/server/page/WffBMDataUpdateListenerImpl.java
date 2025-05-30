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
import java.util.logging.Level;
import java.util.logging.Logger;

import com.webfirmframework.wffweb.internal.security.object.SecurityObject;
import com.webfirmframework.wffweb.internal.tag.html.listener.WffBMDataUpdateListener;
import com.webfirmframework.wffweb.tag.html.AbstractHtml;
import com.webfirmframework.wffweb.tag.html.TagUtil;
import com.webfirmframework.wffweb.util.data.NameValue;
import com.webfirmframework.wffweb.wffbm.data.BMType;
import com.webfirmframework.wffweb.wffbm.data.WffBMData;

/**
 * @author WFF
 * @since 2.1.8
 */
public final class WffBMDataUpdateListenerImpl implements WffBMDataUpdateListener {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = Logger.getLogger(WffBMDataUpdateListenerImpl.class.getName());

    private final BrowserPage browserPage;

    private final SecurityObject accessObject;

    @SuppressWarnings("unused")
    private WffBMDataUpdateListenerImpl() {
        throw new AssertionError();
    }

    WffBMDataUpdateListenerImpl(final BrowserPage browserPage, final SecurityObject accessObject) {
        this.browserPage = browserPage;
        this.accessObject = accessObject;
    }

    @Override
    public void updatedWffData(@SuppressWarnings("exports") final UpdateEvent event) {

        try {

            // @formatter:off
            // SET_BM_OBJ_ON_TAG/SET_BM_ARR_ON_TAG task format :-
            // { "name": task_byte, "values" :
            // [SET_BM_OBJ_ON_TAG/SET_BM_ARR_ON_TAG_byte_from_Task_enum]},
            // { "name": [tag name bytes], "values" : [[wff id bytes], [key bytes], [bm
            // bytes] }
            // @formatter:on

            final AbstractHtml tag = event.tag();
            final WffBMData wffBMData = event.wffData();
            final NameValue task;

            if (BMType.OBJECT.equals(wffBMData.getBMType())) {
                task = Task.SET_BM_OBJ_ON_TAG.getTaskNameValue();
            } else if (BMType.ARRAY.equals(wffBMData.getBMType())) {
                task = Task.SET_BM_ARR_ON_TAG.getTaskNameValue();
            } else {
                return;
            }

            final NameValue nameValue = new NameValue();

            final NameValue[] nameValues = { task, nameValue };

            final byte[] wffTagNameBytes = TagUtil.getTagNameBytesCompressedByIndex(accessObject, tag,
                    StandardCharsets.UTF_8);
            nameValue.setName(wffTagNameBytes);

            final byte[] dataWffIdBytes = DataWffIdUtil.getDataWffIdBytes(tag.getDataWffId().getValue());

            nameValue.setValues(new byte[][] { dataWffIdBytes, event.key().getBytes(StandardCharsets.UTF_8),
                    wffBMData.buildBytes(true) });

            browserPage.push(nameValues);

        } catch (final Exception e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }

    }

}