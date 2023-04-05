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
package com.webfirmframework.wffweb.internal.security.object;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

import com.webfirmframework.wffweb.internal.security.object.SecurityClassConstants;

public class SecurityClassConstantsTest {
    
    private static final Logger LOGGER = Logger.getLogger(SecurityClassConstantsTest.class.getName());

    @Test
    public void testHashCodeCollisionForSecurityClassConstants() {

        Set<Integer> hashCodes = new HashSet<>();
        hashCodes.add(SecurityClassConstants.ABSTRACT_ATTRIBUTE.hashCode());
        hashCodes.add(SecurityClassConstants.ABSTRACT_HTML.hashCode());
        hashCodes.add(SecurityClassConstants.ABSTRACT_JS_OBJECT.hashCode());
        hashCodes.add(SecurityClassConstants.BROWSER_PAGE.hashCode());
        hashCodes.add(SecurityClassConstants.SHARED_TAG_CONTENT.hashCode());

        if (hashCodes.size() != 5) {
            LOGGER.log(Level.WARNING, "hashCode collision found for SecurityClassConstants.");
        }
    }

}
