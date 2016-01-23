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
package com.webfirmframework.wffweb.tag.html.html5.attribute.global;

import com.webfirmframework.wffweb.tag.html.attribute.AttributeNameConstants;
import com.webfirmframework.wffweb.tag.html.attribute.core.AbstractAttribute;
import com.webfirmframework.wffweb.tag.html.identifier.GlobalAttributable;

/**
 * {@code <element translate="yes|no">  }
 *
 * <pre>
 * The translate attribute specifies whether the content of an element should be translated or not.
 * </pre>
 *
 * @author WFF
 *
 */
public class Translate extends AbstractAttribute implements GlobalAttributable {

    private static final long serialVersionUID = 1_0_0L;

    private boolean translation;

    {
        super.setAttributeName(AttributeNameConstants.TRANSLATE);
        init();
    }

    /**
     * <code>false</code> will be set as the value.
     *
     * @author WFF
     * @since 1.0.0
     */
    public Translate() {
        setAttributeValue(translation ? "yes" : "no");
    }

    /**
     * @param translation
     *            the translation to set. The argument {@code true } or
     *            {@code false } will set {@code yes} or {@code no}
     *            respectively.
     * @author WFF
     * @since 1.0.0
     */
    public Translate(final boolean translation) {
        setAttributeValue(translation ? "yes" : "no");
        this.translation = translation;
    }

    /**
     * invokes only once per object
     *
     * @author WFF
     * @since 1.0.0
     */
    protected void init() {
    }

    /**
     * @return the translation
     * @author WFF
     * @since 1.0.0
     */
    public boolean isTranslation() {
        return translation;
    }

    /**
     * @param translation
     *            the translation to set. The argument {@code true } or
     *            {@code false } will set {@code yes} or {@code no}
     *            respectively.
     * @author WFF
     * @since 1.0.0
     */
    public void setTranslation(final boolean translation) {
        setAttributeValue(translation ? "yes" : "no");
        this.translation = translation;
    }

}
