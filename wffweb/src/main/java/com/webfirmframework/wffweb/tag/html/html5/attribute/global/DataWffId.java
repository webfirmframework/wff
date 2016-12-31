/*
 * Copyright 2014-2017 Web Firm Framework
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
package com.webfirmframework.wffweb.tag.html.html5.attribute.global;

import com.webfirmframework.wffweb.WffSecurityException;

public class DataWffId extends DataAttribute {

    private static final long serialVersionUID = 1_0_0L;

    /**
     * @param value
     *            the value for the attribute
     * @since 2.0.0
     * @author WFF
     */
    public DataWffId(final String value) {
        super("wff-id", value);
    }

    @Override
    public void setValue(final String value) {
        throw new WffSecurityException("Not allowed to change its value");
    }

}
