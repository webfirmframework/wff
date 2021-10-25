package com.webfirmframework.wffweb.internal.security.object;

import java.io.Serializable;

import com.webfirmframework.wffweb.internal.constants.IndexedClassType;
import com.webfirmframework.wffweb.server.page.BrowserPage;
import com.webfirmframework.wffweb.tag.core.AbstractJsObject;
import com.webfirmframework.wffweb.tag.html.AbstractHtml;
import com.webfirmframework.wffweb.tag.html.SharedTagContent;
import com.webfirmframework.wffweb.tag.html.attribute.core.AbstractAttribute;

public sealed interface SecurityObject extends
        Serializable permits BrowserPage.Security,AbstractJsObject.Security,AbstractHtml.Security,SharedTagContent.Security,AbstractAttribute.Security {
    IndexedClassType forClassType();
}
