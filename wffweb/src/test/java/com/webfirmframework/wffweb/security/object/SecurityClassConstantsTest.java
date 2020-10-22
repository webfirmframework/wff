package com.webfirmframework.wffweb.security.object;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

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
