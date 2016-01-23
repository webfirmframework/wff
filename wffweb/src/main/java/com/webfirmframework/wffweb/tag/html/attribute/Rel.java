/*
 * Copyright 2014-2016 Web Firm Framework
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
 * @author WFF
 */
package com.webfirmframework.wffweb.tag.html.attribute;

import com.webfirmframework.wffweb.tag.html.attribute.core.AbstractAttribute;
import com.webfirmframework.wffweb.tag.html.identifier.AAttributable;
import com.webfirmframework.wffweb.tag.html.identifier.AreaAttributable;

/**
 * For anchors containing the href attribute, this attribute specifies the
 * relationship of the target object to the link object. The value is a
 * space-separated list of link types values. The values and their semantics
 * will be registered by some authority that might have meaning to the document
 * author. The default relationship, if no other is given, is void. Use this
 * attribute only if the href attribute is present. <code>rel</code> attribute
 * for the element.
 *
 * @author WFF
 *
 */
public class Rel extends AbstractAttribute implements AAttributable, AreaAttributable {

    private static final long serialVersionUID = 1_0_0L;

    /**
     * Links to an alternate version of the document (i.e. print page,
     * translated or mirror)
     */
    public static final String ALTERNATE = "alternative";

    /**
     * Links to the author of the document
     */
    public static final String AUTHOR = "author";

    /**
     * Permanent URL used for bookmarking
     */
    public static final String BOOKMARK = "bookmark";

    /**
     * Links to a help document
     */
    public static final String HELP = "help";

    /**
     * Links to copyright information for the document
     */
    public static final String LICENSE = "license";

    /**
     * The next document in a selection
     */
    public static final String NEXT = "next";

    /**
     * Links to an unendorsed document, like a paid link. ("nofollow" is used by
     * Google, to specify that the Google search spider should not follow that
     * link)
     */
    public static final String NOFOLLOW = "nofollow";

    /**
     * Specifies that the browser should not send a HTTP referer header if the
     * user follows the hyperlink
     */
    public static final String NOREFERRER = "noreferrer";

    /**
     * Specifies that the target document should be cached
     */
    public static final String PREFETCH = "prefetch";

    /**
     * The previous document in a selection
     */
    public static final String PREV = "prev";

    /**
     * Links to a search tool for the document
     */
    public static final String SEARCH = "search";

    /**
     * A tag (keyword) for the current document
     */
    public static final String TAG = "tag";

    {
        super.setAttributeName(AttributeNameConstants.REL);
        init();
    }

    /**
     *
     * @param value
     *            the value for the attribute
     * @since 1.0.0
     * @author WFF
     */
    public Rel(final String value) {
        setAttributeValue(value);
    }

    /**
     * sets the value for this attribute
     *
     * @param value
     *            the value for the attribute.
     * @since 1.0.0
     * @author WFF
     */
    protected void setValue(final String value) {
        super.setAttributeValue(value);
    }

    /**
     * gets the value of this attribute
     *
     * @return the value of the attribute
     * @since 1.0.0
     * @author WFF
     */
    public String getValue() {
        return super.getAttributeValue();
    }

    /**
     * invokes only once per object
     *
     * @author WFF
     * @since 1.0.0
     */
    protected void init() {
    }

}
