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

/**
 * Note: It is recommended to use event.data() method instead of first data
 * (WffBMObject) parameter from
 * {@link ServerAsyncMethod#asyncMethod(WffBMObject, Event)} method as the
 * signature of the method will be changed to a single parameter method in next
 * major version.
 *
 * @author WFF
 */
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

        private final WffBMObject data;

        /**
         * @param serverMethodName
         * @since 3.0.19 deprecated
         * @deprecated use {@link Event(WffBMObject, AbstractHtml, AbstractAttribute,
         *             String, Object)}
         */
        @Deprecated
        public Event(final String serverMethodName) {
            this.serverMethodName = serverMethodName;
            serverMethodNameFinal = serverMethodName;
            sourceAttribute = null;
            serverSideData = null;
            srcTag = null;
            data = null;
        }

        /**
         * @param sourceTag
         * @param sourceAttribute
         * @since 3.0.19 deprecated
         * @deprecated use {@link Event(WffBMObject, AbstractHtml, AbstractAttribute,
         *             String, Object)}
         */
        @Deprecated
        public Event(final AbstractHtml sourceTag, final AbstractAttribute sourceAttribute) {
            super();
            this.sourceTag = sourceTag;
            srcTag = sourceTag;
            this.sourceAttribute = sourceAttribute;
            serverSideData = null;
            serverMethodNameFinal = null;
            data = null;
        }

        /**
         * @param serverMethodName
         * @param serverSideData
         * @since 3.0.2
         * @since 3.0.19 deprecated
         * @deprecated use {@link Event(WffBMObject, AbstractHtml, AbstractAttribute,
         *             String, Object)}
         */
        @Deprecated
        public Event(final String serverMethodName, final Object serverSideData) {
            sourceAttribute = null;
            srcTag = null;
            this.serverMethodName = serverMethodName;
            serverMethodNameFinal = serverMethodName;
            this.serverSideData = serverSideData;
            data = null;
        }

        /**
         * @param sourceTag
         * @param sourceAttribute
         * @param serverSideData
         * @since 3.0.2
         * @since 3.0.19 deprecated
         * @deprecated use {@link Event(WffBMObject, AbstractHtml, AbstractAttribute,
         *             String, Object)}
         */
        @Deprecated
        public Event(final AbstractHtml sourceTag, final AbstractAttribute sourceAttribute,
                final Object serverSideData) {
            super();
            this.sourceTag = sourceTag;
            srcTag = sourceTag;
            this.sourceAttribute = sourceAttribute;
            this.serverSideData = serverSideData;
            serverMethodNameFinal = null;
            data = null;
        }

        /**
         * @param data
         * @param sourceTag
         * @param sourceAttribute
         * @param serverMethodName
         * @param serverSideData
         * @since 3.0.19
         */
        public Event(final WffBMObject data, final AbstractHtml sourceTag, final AbstractAttribute sourceAttribute,
                final String serverMethodName, final Object serverSideData) {
            super();
            this.sourceTag = sourceTag;
            srcTag = sourceTag;
            this.sourceAttribute = sourceAttribute;
            this.serverSideData = serverSideData;
            serverMethodNameFinal = null;
            this.data = data;
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

        /**
         * Use this method to get data instead of the first parameter from
         * {@link ServerAsyncMethod#asyncMethod(WffBMObject, Event)} method, both
         * contain the same object. But, it is recommended to use {@link Event#data()}
         * method to get the same data as the signature of
         * {@link ServerAsyncMethod#asyncMethod(WffBMObject, Event)} method will be
         * changed to a single parameter method in next major version.
         *
         * @return the data
         */
        public WffBMObject data() {
            return data;
        }

    }

    /**
     * @param data  the data received from the consumer i.e usually client browser.
     *              But, it is recommended to use {@link Event#data()} method to get
     *              the same data as the signature of
     *              {@link ServerAsyncMethod#asyncMethod(WffBMObject, Event)} method
     *              will be changed to a single parameter method in next major
     *              version.
     * @param event
     * @return the data to the consumer i.e usually client browser.
     */
    public abstract WffBMObject asyncMethod(final WffBMObject data, final Event event);

}
