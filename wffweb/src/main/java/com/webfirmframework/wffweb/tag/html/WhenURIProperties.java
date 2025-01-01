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
 */
package com.webfirmframework.wffweb.tag.html;

/**
 * @since 12.0.0-beta.7
 */
public class WhenURIProperties {

    volatile boolean duplicateSuccessPrevented;

    volatile boolean duplicateFailPrevented;

    /**
     * @param prevent true to prevent invoking same success object's method if it is
     *                already invoked on the last uri change.
     * @since 12.0.0-beta.7
     */
    public void setPreventDuplicateSuccess(final boolean prevent) {
        duplicateSuccessPrevented = prevent;
    }

    /**
     * @param prevent true to prevent invoking same fail object's method if it is
     *                already invoked on the last uri change.
     * @since 12.0.0-beta.7
     */
    public void setPreventDuplicateFail(final boolean prevent) {
        duplicateFailPrevented = prevent;
    }

    /**
     * @return true or false
     * @since 12.0.0-beta.7
     */
    public boolean isPreventDuplicateSuccess() {
        return duplicateSuccessPrevented;
    }

    /**
     * @return true or false
     * @since 12.0.0-beta.7
     */
    public boolean isPreventDuplicateFail() {
        return duplicateFailPrevented;
    }

}
