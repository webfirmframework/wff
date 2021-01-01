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
package com.webfirmframework.wffweb.tag.html.listener;

import java.io.Serializable;

import com.webfirmframework.wffweb.tag.html.AbstractHtml;

/**
 * @author WFF
 * @since 3.0.7
 *
 */
public interface ReplaceListener extends Serializable {

    public static class Event {

        private AbstractHtml insertedTag;

        private AbstractHtml previousParentTag;

        @SuppressWarnings("unused")
        private Event() {
            throw new AssertionError();
        }

        public Event(final AbstractHtml insertedTag, final AbstractHtml previousParentTag) {
            this.insertedTag = insertedTag;
            this.previousParentTag = previousParentTag;
        }

        public AbstractHtml getInsertedTag() {
            return insertedTag;
        }

        public void setInsertedTag(final AbstractHtml insertedTag) {
            this.insertedTag = insertedTag;
        }

        /**
         * @return the previousParentTag
         */
        public AbstractHtml getPreviousParentTag() {
            return previousParentTag;
        }

        /**
         * @param previousParentTag the previousParentTag to set
         */
        public void setPreviousParentTag(final AbstractHtml previousParentTag) {
            this.previousParentTag = previousParentTag;
        }

    }

    /**
     * Tags insertedTags inserted before tag beforeTag having parent parentTag.
     *
     * @param parentTag
     * @param replacingTag
     * @param events
     * @since 3.0.7
     * @author WFF
     */
    public void replacedWith(final AbstractHtml parentTag, final AbstractHtml replacingTag, final Event... events);

}
