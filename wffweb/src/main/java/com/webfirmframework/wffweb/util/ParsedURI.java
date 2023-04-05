/*
 * Copyright 2014-2023 Web Firm Framework
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

import java.util.List;
import java.util.Map;

/**
 * @param pathname        the pathname of the uri i.e. uri without query string
 * @param pathParameters  map of path parameters
 * @param queryParameters map of query parameters
 * @param hash            the hash part of uri
 * @since 12.0.0-beta.9
 */
public record ParsedURI(String pathname, Map<String, String> pathParameters, Map<String, List<String>> queryParameters,
        String hash) {
}
