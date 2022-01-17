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
package com.webfirmframework.wffweb.server.page;

import java.io.Serializable;

import com.webfirmframework.wffweb.tag.html.attribute.event.ServerMethod;

/**
 * @author WFF
 * @since 3.0.2
 * @since 3.0.19 renamed ServerMethod to ServerMethodWrapper
 */
record ServerMethodWrapper(ServerMethod serverMethod, Object serverSideData) implements Serializable {

    private static final long serialVersionUID = 12_0L;

    /**
     * @return the serverMethod
     */
    @Override
    public ServerMethod serverMethod() {
        return serverMethod;
    }

    /**
     * @return the serverSideData
     */
    @Override
    public Object serverSideData() {
        return serverSideData;
    }

}
