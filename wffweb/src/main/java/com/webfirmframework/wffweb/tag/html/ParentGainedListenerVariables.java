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
 * @author WFF
 */
package com.webfirmframework.wffweb.tag.html;

import com.webfirmframework.wffweb.tag.html.listener.ParentGainedListener;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class ParentGainedListenerVariables implements Serializable {

    @Serial
    private static final long serialVersionUID = 12_0_1L;

    volatile Map<Long, ParentGainedListener> parentGainedListeners;

    volatile long parentGainedListenerSlotIdCounter;

    ParentGainedListenerVariables() {
        parentGainedListeners = new ConcurrentHashMap<>();
    }
}
