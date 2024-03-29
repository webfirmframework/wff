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
package com.webfirmframework.wffweb.tag.html.listener;

import java.io.Serializable;
import java.util.List;

import com.webfirmframework.wffweb.tag.html.AbstractHtml;
import com.webfirmframework.wffweb.tag.html.attribute.core.AbstractAttribute;

public interface AttributeRemoveListener extends Serializable {

    public static class RemovedEvent {

        private AbstractHtml removedFromTag;

        private String[] removedAttributeNames;

        private List<AbstractAttribute> removedAttributes;

        public RemovedEvent() {
        }

        public RemovedEvent(final AbstractHtml removedFromTag, final String... removedAttributeNames) {
            super();
            this.removedFromTag = removedFromTag;
            this.removedAttributeNames = removedAttributeNames;
        }

        /**
         * @param removedFromTag
         * @param removedAttributes
         * @since 3.0.6
         */
        public RemovedEvent(final AbstractHtml removedFromTag, final List<AbstractAttribute> removedAttributes) {
            super();
            this.removedFromTag = removedFromTag;
            this.removedAttributes = removedAttributes;
        }

        public AbstractHtml getRemovedFromTag() {
            return removedFromTag;
        }

        public void setRemovedFromTag(final AbstractHtml removedFromTag) {
            this.removedFromTag = removedFromTag;
        }

        public String[] getRemovedAttributeNames() {
            return removedAttributeNames;
        }

        public void setRemovedAttributeNames(final String[] removedAttributeNames) {
            this.removedAttributeNames = removedAttributeNames;
        }

        public List<AbstractAttribute> getRemovedAttributes() {
            return removedAttributes;
        }

    }

    public void removedAttributes(RemovedEvent event);
}
