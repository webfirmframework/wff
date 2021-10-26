package com.webfirmframework.wffweb.internal.security.object;

import java.io.Serializable;

import com.webfirmframework.wffweb.internal.constants.IndexedClassType;

/**
 * Note: only for internal use.
 *
 * @since 12.0.0
 */
public sealed interface SecurityObject extends
        Serializable permits AbstractAttributeSecurity,AbstractHtmlSecurity,AbstractJsObjectSecurity,BrowserPageSecurity,SharedTagContentSecurity {
    IndexedClassType forClassType();
}
