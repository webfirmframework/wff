/*
 * Copyright 2014-2024 Web Firm Framework
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
package com.webfirmframework.wffweb.internal.tag.html.listener;

import java.io.Serializable;

import com.webfirmframework.wffweb.server.page.AttributeAddListenerImpl;
import com.webfirmframework.wffweb.tag.html.AbstractHtml;
import com.webfirmframework.wffweb.tag.html.attribute.core.AbstractAttribute;

public sealed interface AttributeAddListener extends Serializable permits AttributeAddListenerImpl {

    public static final record AddEvent(AbstractHtml addedToTag, AbstractAttribute... addedAttributes) {

    }

    public void addedAttributes(AddEvent event);

}
