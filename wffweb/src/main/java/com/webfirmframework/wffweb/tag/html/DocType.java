/*
 * Copyright 2014-2015 Web Firm Framework
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
package com.webfirmframework.wffweb.tag.html;

import com.webfirmframework.wffweb.tag.html.attribute.core.AbstractAttribute;

/**
 * @author WFF
 * @since 1.0.0
 * @version 1.0.0
 *
 */
public abstract class DocType extends AbstractHtml {

    private static final long serialVersionUID = -6563575503502210744L;

    private boolean prependDocType;

    private String docTypeTag = "<!DOCTYPE html>";

    /**
     * should be invoked to generate opening and closing tag
     *
     * @param tagName
     * @param attributes
     */
    public DocType(final String tagName, final AbstractHtml base,
            final AbstractAttribute[] attributes) {
        super(tagName, base, attributes);
    }

    @Override
    public String toHtmlString() {
        if (prependDocType) {
            return docTypeTag + "\n" + super.toHtmlString();
        }
        return super.toHtmlString();
    }

    @Override
    public String toHtmlString(final boolean rebuild) {
        if (prependDocType) {
            return docTypeTag + "\n" + super.toHtmlString();
        }
        return super.toHtmlString(rebuild);
    }

    @Override
    public String toString() {
        if (prependDocType) {
            return docTypeTag + "\n" + super.toHtmlString();
        }
        return super.toString();
    }

    /**
     * @return the prependDocType
     * @author WFF
     * @since 1.0.0
     */
    public boolean isPrependDocType() {
        return prependDocType;
    }

    /**
     * @param prependDocType
     *            the prependDocType to set
     * @author WFF
     * @since 1.0.0
     */
    public void setPrependDocType(final boolean prependDocType) {
        this.prependDocType = prependDocType;
    }

    /**
     * @return the docTypeTag
     * @author WFF
     * @since 1.0.0
     */
    public String getDocTypeTag() {
        return docTypeTag;
    }

    /**
     * the default doc type is <code> <!DOCTYPE html></code>
     *
     * @param docTypeTag
     *            the docTypeTag to set
     * @author WFF
     * @since 1.0.0
     */
    public void setDocTypeTag(final String docTypeTag) {
        this.docTypeTag = docTypeTag;
    }

}
