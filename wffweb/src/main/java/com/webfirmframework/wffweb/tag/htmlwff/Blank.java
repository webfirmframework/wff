/*
 * Copyright since 2014 Web Firm Framework
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
package com.webfirmframework.wffweb.tag.htmlwff;

import java.io.Serial;
import java.util.Collection;

import com.webfirmframework.wffweb.tag.html.AbstractHtml;

/**
 *
 *
 * @author WFF
 * @since 1.0.0
 * @deprecated use {@link NoTag} instead of this class.
 */
@Deprecated(forRemoval = true, since = "12.0.0-beta.1")
class Blank extends NoTag {

    // TODO This class needs to be tested properly

    @Serial
    private static final long serialVersionUID = 1_0_0L;

    /**
     *
     * @param base     i.e. parent tag of this tag
     * @param children An array of {@code AbstractHtml}
     *
     * @since 1.0.0
     */
    Blank(final AbstractHtml base, final AbstractHtml... children) {
        super(base, children);
    }

    /**
     *
     * @param base     i.e. parent tag of this tag
     * @param children An array of {@code AbstractHtml}
     *
     * @since 1.0.0
     */
    public Blank(final AbstractHtml base, final Collection<? extends AbstractHtml> children) {
        super(base, children);
    }

    /**
     *
     * @param base         i.e. parent tag of this tag
     * @param childContent
     *
     * @since 1.0.0
     */
    public Blank(final AbstractHtml base, final String childContent) {
        super(base, childContent);
    }

}
