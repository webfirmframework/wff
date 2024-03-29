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

import com.webfirmframework.wffweb.tag.html.AbstractHtml;

public interface ChildTagRemoveListener extends Serializable {

    public static class Event {

        private AbstractHtml removedChildTag;

        private AbstractHtml[] removedChildrenTags;

        private AbstractHtml parentTag;

        @SuppressWarnings("unused")
        private Event() {
            throw new AssertionError();
        }

        public Event(final AbstractHtml parentTag, final AbstractHtml removedChildTag) {
            this.parentTag = parentTag;
            this.removedChildTag = removedChildTag;
        }

        public Event(final AbstractHtml parentTag, final AbstractHtml[] removedChildrenTags) {
            this.parentTag = parentTag;
            this.removedChildrenTags = removedChildrenTags;
        }

        /**
         * @return the parentTag
         */
        public AbstractHtml getParentTag() {
            return parentTag;
        }

        /**
         * @param parentTag the parentTag to set
         */
        public void setParentTag(final AbstractHtml parentTag) {
            this.parentTag = parentTag;
        }

        /**
         * @return the removedChildTag
         */
        public AbstractHtml getRemovedChildTag() {
            return removedChildTag;
        }

        /**
         * @param removedChildTag the removedChildTag to set
         */
        public void setRemovedChildTag(final AbstractHtml removedChildTag) {
            this.removedChildTag = removedChildTag;
        }

        /**
         * @return the removedChildrenTags
         */
        public AbstractHtml[] getRemovedChildrenTags() {
            return removedChildrenTags;
        }

        /**
         * @param removedChildrenTags the removedChildrenTags to set
         */
        public void setRemovedChildrenTags(final AbstractHtml[] removedChildrenTags) {
            this.removedChildrenTags = removedChildrenTags;
        }

    }

    public void childRemoved(Event event);

    public void childrenRemoved(Event event);

    public void allChildrenRemoved(Event event);

}
