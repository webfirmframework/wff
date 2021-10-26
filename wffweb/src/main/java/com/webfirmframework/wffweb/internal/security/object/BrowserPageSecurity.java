package com.webfirmframework.wffweb.internal.security.object;

import java.io.Serial;

import com.webfirmframework.wffweb.internal.constants.IndexedClassType;

/**
 * Note: Only for internal use.
 *
 * @since 12.0.0
 */
public final class BrowserPageSecurity implements SecurityObject {

    @Serial
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unused")
    private BrowserPageSecurity() {
        throw new AssertionError("Not allowed to create more than one object. This class is only for internal use.");
    }

    public BrowserPageSecurity(final Object caller) {
        if (!SecurityClassConstants.BROWSER_PAGE.equals(caller.getClass().getName())) {
            throw new AssertionError(
                    "Not allowed to create more than one object. This class is only for internal use.");
        }
    }

    @Override
    public IndexedClassType forClassType() {
        return IndexedClassType.BROWSER_PAGE;
    }
}
