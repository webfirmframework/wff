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
     * {@link ServerAsyncMethod#asyncMethod(WffBMObject, Event)}. This class might
     * be replaced with java record class in future if the minimal java version of
     * this framework is upgraded to record supported version.
     *
     */
    public static class Event {

        private AbstractHtml sourceTag;

        private final AbstractHtml srcTag;

        private final String serverMethodNameFinal;

        private String serverMethodName;

        private final AbstractAttribute sourceAttribute;

        private final Object serverSideData;

        public Event(final String serverMethodName) {
            this.serverMethodName = serverMethodName;
            serverMethodNameFinal = serverMethodName;
            sourceAttribute = null;
            serverSideData = null;
            srcTag = null;
        }

        public Event(final AbstractHtml sourceTag, final AbstractAttribute sourceAttribute) {
            super();
            this.sourceTag = sourceTag;
            srcTag = sourceTag;
            this.sourceAttribute = sourceAttribute;
            serverSideData = null;
            serverMethodNameFinal = null;
        }

        /**
         * @param serverMethodName
         * @param serverSideData
         * @since 3.0.2
         */
        public Event(final String serverMethodName, final Object serverSideData) {
            sourceAttribute = null;
            srcTag = null;
            this.serverMethodName = serverMethodName;
            serverMethodNameFinal = serverMethodName;
            this.serverSideData = serverSideData;
        }

        /**
         * @param sourceTag
         * @param sourceAttribute
         * @param serverSideData
         * @since 3.0.2
         */
        public Event(final AbstractHtml sourceTag, final AbstractAttribute sourceAttribute,
                final Object serverSideData) {
            super();
            this.sourceTag = sourceTag;
            srcTag = sourceTag;
            this.sourceAttribute = sourceAttribute;
            this.serverSideData = serverSideData;
            serverMethodNameFinal = null;
        }

        /**
         * @return the sourceTag
         * @deprecated use {@link Event#sourceTag()} method instead as its value cannot
         *             be modified in this Event object. This method will not affect the
         *             value of {@link Event#sourceTag()} method. Using
         *             {@link Event#sourceTag()} is better for keeping java record class
         *             standard. This method will be removed only if the minimal java
         *             version of this framework is upgraded to a java record class
         *             supported version.
         */
        @Deprecated
        public AbstractHtml getSourceTag() {
            return sourceTag;
        }

        /**
         * <br>
         * Set the sourceTag so that the value of {@link Event#getSourceTag()} will
         * return this value. <br>
         * NB: Calling this method will not affect the value of
         * {@link Event#sourceTag()} method.
         *
         * @param sourceTag the sourceTag to set
         * @deprecated The use of this method is not encouraged. Use constructor
         *             initialization instead. This method will be removed only if the
         *             minimal java version of this framework is upgraded to a java
         *             record class supported version.
         */
        @Deprecated
        public void setSourceTag(final AbstractHtml sourceTag) {
            this.sourceTag = sourceTag;
        }

        /**
         * @return the serverMethodName
         *
         * @deprecated use {@link Event#serverMethodName()} method instead as its value
         *             cannot be modified in this Event object. Using
         *             {@link Event#serverMethodName()} is better for keeping java
         *             record class standard. This method will be removed only if the
         *             minimal java version of this framework is upgraded to a java
         *             record class supported version.
         */
        @Deprecated
        public String getServerMethodName() {
            return serverMethodName;
        }

        /**
         * <br>
         * Set the serverMethodName so that the value of
         * {@link Event#getServerMethodName()} will return this value. <br>
         * NB: Calling this method will not affect the value of
         * {@link Event#serverMethodName()} method.
         *
         * @param serverMethodName
         * @author WFF
         * @deprecated The use of this method is not encouraged. Use constructor
         *             initialization instead. This method will be removed only if the
         *             minimal java version of this framework is upgraded to a java
         *             record class supported version.
         */
        @Deprecated
        public void setServerMethodName(final String serverMethodName) {
            this.serverMethodName = serverMethodName;
        }

        /**
         * <br>
         * This method returns the source attribute from which the event is generated.
         *
         * @return the sourceAttribute
         *
         * @deprecated use {@link Event#sourceAttribute()} method instead. Using
         *             {@link Event#sourceAttribute()} is better for keeping java record
         *             class standard. This method will be removed only if the minimal
         *             java version of this framework is upgraded to a java record class
         *             supported version.
         * @since 2.1.2
         */
        @Deprecated
        public AbstractAttribute getSourceAttribute() {
            return sourceAttribute;
        }

        /**
         * @return the server side data passed in the event attribute argument.
         *
         * @deprecated use {@link Event#serverSideData()} method instead. Using
         *             {@link Event#serverSideData()} is better for keeping java record
         *             class standard. This method will be removed only if the minimal
         *             java version of this framework is upgraded to a java record class
         *             supported version.
         * @since 3.0.2
         */
        @Deprecated
        public Object getServerSideData() {
            return serverSideData;
        }

        /**
         * NB: The deprecated {@link Event#setSourceTag(AbstractHtml)} will not affect
         * the value of this method.
         *
         * @return the sourceTag
         * @since 3.0.15
         */
        public AbstractHtml sourceTag() {
            return srcTag;
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
            return serverMethodNameFinal;
        }

    }

    public abstract WffBMObject asyncMethod(WffBMObject data, Event event);

}
