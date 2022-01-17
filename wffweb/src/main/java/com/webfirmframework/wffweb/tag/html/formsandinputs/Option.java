/*
 * Copyright 2014-2022 Web Firm Framework
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
package com.webfirmframework.wffweb.tag.html.formsandinputs;

import java.util.logging.Logger;

import com.webfirmframework.wffweb.settings.WffConfiguration;
import com.webfirmframework.wffweb.tag.html.AbstractHtml;
import com.webfirmframework.wffweb.tag.html.attribute.core.AbstractAttribute;
import com.webfirmframework.wffweb.tag.html.core.PreIndexedTagName;
import com.webfirmframework.wffweb.tag.html.identifier.GlobalAttributable;
import com.webfirmframework.wffweb.tag.html.identifier.OptionAttributable;

/**
 * @author WFF
 * @since 1.0.0
 * @version 1.0.0
 *
 */
public class Option extends AbstractHtml {

    private static final long serialVersionUID = 1_0_0L;

    private static final Logger LOGGER = Logger.getLogger(Option.class.getName());

    private static final PreIndexedTagName PRE_INDEXED_TAG_NAME;

    static {

        PRE_INDEXED_TAG_NAME = (PreIndexedTagName.OPTION);

    }

    {

        init();
    }

    /**
     *
     * @param base       i.e. parent tag of this tag
     * @param attributes An array of {@code AbstractAttribute}
     *
     * @since 1.0.0
     */
    public Option(final AbstractHtml base, final AbstractAttribute... attributes) {
        super(PRE_INDEXED_TAG_NAME, base, attributes);
        if (WffConfiguration.isDirectionWarningOn()) {
            warnForUnsupportedAttributes(attributes);
        }
    }

    private static void warnForUnsupportedAttributes(final AbstractAttribute... attributes) {
        for (final AbstractAttribute abstractAttribute : attributes) {
            if (!(abstractAttribute != null && (abstractAttribute instanceof OptionAttributable
                    || abstractAttribute instanceof GlobalAttributable))) {
                LOGGER.warning(abstractAttribute + " is not an instance of OptionAttribute");
            }
        }
    }

    /**
     * invokes only once per object
     *
     * @author WFF
     * @since 1.0.0
     */
    protected void init() {
        // to override and use this method
    }

}
