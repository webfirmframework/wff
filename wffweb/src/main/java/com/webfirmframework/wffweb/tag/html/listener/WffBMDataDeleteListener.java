/*
 * Copyright 2014-2020 Web Firm Framework
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

public interface WffBMDataDeleteListener extends Serializable {

    public static final class DeleteEvent {

        private final String key;

        private final AbstractHtml tag;

        public DeleteEvent(final AbstractHtml tag, final String key) {
            super();
            this.tag = tag;
            this.key = key;
        }

        public String getKey() {
            return key;
        }

        public AbstractHtml getTag() {
            return tag;
        }

    }

    public void deletedWffData(DeleteEvent event);

}
