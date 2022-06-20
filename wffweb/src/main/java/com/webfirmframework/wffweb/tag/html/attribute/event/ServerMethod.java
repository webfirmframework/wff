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
package com.webfirmframework.wffweb.tag.html.attribute.event;

import java.io.Serializable;

import com.webfirmframework.wffweb.tag.html.AbstractHtml;
import com.webfirmframework.wffweb.tag.html.attribute.core.AbstractAttribute;
import com.webfirmframework.wffweb.wffbm.data.WffBMObject;

@FunctionalInterface
public interface ServerMethod extends Serializable {

    /**
     * Contains event data for {@link ServerMethod#invoke(Event)}.
     *
     * @param data
     * @param sourceTag
     * @param sourceAttribute
     * @param serverMethodName
     * @param serverSideData
     * @param uri
     * @param recordData
     */
    public static record Event(WffBMObject data, AbstractHtml sourceTag, AbstractAttribute sourceAttribute,
            String serverMethodName, Object serverSideData, String uri, Record recordData) {

        /**
         * @param data
         * @param sourceTag
         * @param sourceAttribute
         * @param serverMethodName
         * @param serverSideData
         * @param uri
         */
        public Event(final WffBMObject data, final AbstractHtml sourceTag, final AbstractAttribute sourceAttribute,
                final String serverMethodName, final Object serverSideData, final String uri) {
            this(data, sourceTag, sourceAttribute, serverMethodName, serverSideData, uri, null);
        }

        /**
         * Note: only for internal use.
         *
         * @param serverMethodName
         * @param serverSideData
         * @param uri
         * @param recordData
         * @since 12.0.0-beta.6
         */
        public Event(final String serverMethodName, final Object serverSideData, final String uri,
                final Record recordData) {
            this(null, null, null, serverMethodName, serverSideData, uri, recordData);
        }

        /**
         * @return the sourceTag
         * @since 3.0.15
         */
        public AbstractHtml sourceTag() {
            return sourceTag;
        }

        /**
         * the source attribute from which the event is generated.
         *
         * @return the sourceAttribute
         * @since 3.0.15
         */
        public AbstractAttribute sourceAttribute() {
            return sourceAttribute;
        }

        /**
         * @return the server side data passed in the event attribute argument.
         * @since 3.0.15
         */
        public Object serverSideData() {
            return serverSideData;
        }

        /**
         * @return the serverMethodName
         * @since 3.0.15
         */
        public String serverMethodName() {
            return serverMethodName;
        }

        /**
         * @return data from the consumer i.e. usually from the browser client.
         */
        @Override
        public WffBMObject data() {
            return data;
        }
    }

    /**
     * Runs in the same order of the event occurred.
     *
     * @param event
     *
     * @return
     */
    public abstract WffBMObject invoke(final Event event);

}
