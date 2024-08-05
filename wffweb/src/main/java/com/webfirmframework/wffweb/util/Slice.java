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
package com.webfirmframework.wffweb.util;

@FunctionalInterface
public interface Slice<T> {

    /**
     * Possible question: why it returns boolean to continue slicing? Sometimes we
     * may need to stop slicing once a certain data is received, in such case it
     * will be a great optimization.
     *
     * @param part each part in the when slicing.
     * @param last true if the part is the last part.
     * @return false to break slicing or true to go on.
     * @since 3.0.1
     */
    public abstract boolean each(T part, boolean last);

}
