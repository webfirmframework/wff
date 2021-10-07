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
package com.webfirmframework.wffweb.tag.html.attribute.event;

import java.io.Serializable;

import com.webfirmframework.wffweb.tag.html.AbstractHtml;
import com.webfirmframework.wffweb.tag.html.attribute.core.AbstractAttribute;
import com.webfirmframework.wffweb.wffbm.data.WffBMObject;

@FunctionalInterface
public interface ServerAsyncMethod extends Serializable {

    /**
     * Contains event data for
     * {@link ServerAsyncMethod#asyncMethod(WffBMObject, Event)}.
     *
     * @param sourceTag
     * @param sourceAttribute
     * @param serverMethodName
     * @param serverSideData
     *
     */
    public static record Event(WffBMObject data, AbstractHtml sourceTag, AbstractAttribute sourceAttribute,
            String serverMethodName, Object serverSideData) {

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
     * @param data
     * @param event
     * @return
     */
    public abstract WffBMObject asyncMethod(final WffBMObject data, final Event event);

}
