package com.webfirmframework.wffweb.tag.html.formatting;

import java.util.logging.Logger;

import com.webfirmframework.wffweb.settings.WffConfiguration;
import com.webfirmframework.wffweb.tag.html.AbstractHtml;
import com.webfirmframework.wffweb.tag.html.attribute.core.AbstractAttribute;
import com.webfirmframework.wffweb.tag.html.identifier.GlobalAttribute;
import com.webfirmframework.wffweb.tag.html.identifier.SupAttribute;

/**
 * @author WFF
 * @since 1.0.0
 * @version 1.0.0
 *
 */
public class Sup extends AbstractHtml {

    private static final long serialVersionUID = -8092619824306328950L;

    public static final Logger LOGGER = Logger.getLogger(Sup.class.getName());

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
    public Sup(final AbstractHtml base, final AbstractAttribute... attributes) {
        super(Sup.class.getSimpleName().toLowerCase(), base, attributes);
        if (WffConfiguration.isDirectionWarningOn()) {
            for (final AbstractAttribute abstractAttribute : attributes) {
                if (!(abstractAttribute != null && (abstractAttribute instanceof SupAttribute || abstractAttribute instanceof GlobalAttribute))) {
                    LOGGER.warning(abstractAttribute
                            + " is not an instance of SupAttribute");
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
