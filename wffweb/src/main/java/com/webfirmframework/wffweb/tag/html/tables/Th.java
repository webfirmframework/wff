package com.webfirmframework.wffweb.tag.html.tables;

import java.util.logging.Logger;

import com.webfirmframework.wffweb.settings.WffConfiguration;
import com.webfirmframework.wffweb.tag.html.AbstractHtml;
import com.webfirmframework.wffweb.tag.html.attribute.core.AbstractAttribute;
import com.webfirmframework.wffweb.tag.html.core.PreIndexedTagName;
import com.webfirmframework.wffweb.tag.html.identifier.GlobalAttributable;
import com.webfirmframework.wffweb.tag.html.identifier.ThAttributable;

/**
 * @author WFF
 * @since 1.0.0
 * @version 1.0.0
 *
 */
public class Th extends AbstractHtml {

    private static final long serialVersionUID = 1_0_0L;

    private static final Logger LOGGER = Logger.getLogger(Th.class.getName());

    private static final PreIndexedTagName PRE_INDEXED_TAG_NAME;

    static {

        PRE_INDEXED_TAG_NAME = (PreIndexedTagName.TH);

    }

    {

        init();
    }

    /**
     *
     * @param base
     *                       i.e. parent tag of this tag
     * @param attributes
     *                       An array of {@code AbstractAttribute}
     *
     * @since 1.0.0
     */
    public Th(final AbstractHtml base, final AbstractAttribute... attributes) {
        super(PRE_INDEXED_TAG_NAME, base, attributes);
        if (WffConfiguration.isDirectionWarningOn()) {
            warnForUnsupportedAttributes(attributes);
        }
    }

    private static void warnForUnsupportedAttributes(
            final AbstractAttribute... attributes) {
        for (final AbstractAttribute abstractAttribute : attributes) {
            if (!(abstractAttribute != null
                    && (abstractAttribute instanceof ThAttributable
                            || abstractAttribute instanceof GlobalAttributable))) {
                LOGGER.warning(abstractAttribute
                        + " is not an instance of ThAttribute");
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
