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
package com.webfirmframework.wffweb.wffbm.data;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

public interface WffData extends Serializable {

    /**
     * @return the BMType
     * @since 2.1.8
     * @author WFF
     */
    public abstract BMType getBMType();

    /**
     * @param outer
     * @return
     * @since 2.1.8
     * @author WFF
     */
    public abstract byte[] build(boolean outer)
            throws UnsupportedEncodingException;

}
