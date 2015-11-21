package com.webfirmframework.wffweb.tag.html.formatting;

import java.util.logging.Logger;

import com.webfirmframework.wffweb.settings.WffConfiguration;
import com.webfirmframework.wffweb.tag.html.AbstractHtml;
import com.webfirmframework.wffweb.tag.html.attribute.core.AbstractAttribute;
import com.webfirmframework.wffweb.tag.html.identifier.GlobalAttribute;
import com.webfirmframework.wffweb.tag.html.identifier.QAttribute;

/**
 * @author WFF
 * @since 1.0.0
 * @version 1.0.0
 *
 */
public class Q extends AbstractHtml {

    private static final long serialVersionUID = 4554063216056892606L;

    public static final Logger LOGGER = Logger.getLogger(Q.class.getName());

    {
        init();
    }

    /**
     * Represents the root of an HTML or XHTML document. All other elements must
     * be descendants of this element.
     *
     * @param base
     *            i.e. parent tag of this tag
     * @param attributes
     *            An array of {@code AbstractAttribute}
     *
     * @since 1.0.0
     */
    public Q(final AbstractHtml base, final AbstractAttribute... attributes) {
        super(Q.class.getSimpleName().toLowerCase(), base, attributes);
        if (WffConfiguration.isDirectionWarningOn()) {
            for (final AbstractAttribute abstractAttribute : attributes) {
                if (!(abstractAttribute != null && (abstractAttribute instanceof QAttribute || abstractAttribute instanceof GlobalAttribute))) {
                    LOGGER.warning(abstractAttribute
                            + " is not an instance of QAttribute");
                }
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
    }

}
