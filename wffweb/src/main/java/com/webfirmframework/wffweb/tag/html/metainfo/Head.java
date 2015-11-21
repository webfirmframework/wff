package com.webfirmframework.wffweb.tag.html.metainfo;

import java.util.logging.Logger;

import com.webfirmframework.wffweb.tag.html.AbstractHtml;
import com.webfirmframework.wffweb.tag.html.attribute.core.AbstractAttribute;
import com.webfirmframework.wffweb.tag.html.identifier.GlobalAttribute;
import com.webfirmframework.wffweb.tag.html.identifier.HeadAttribute;

/**
 * @author WFF
 * @since 1.0.0
 * @version 1.0.0
 *
 */
public class Head extends AbstractHtml {

    private static final long serialVersionUID = -1172229883151826949L;

    public static final Logger LOGGER = Logger.getLogger(Head.class.getName());

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
    public Head(final AbstractHtml base, final AbstractAttribute... attributes) {
        super(Head.class.getSimpleName().toLowerCase(), base, attributes);
        for (final AbstractAttribute abstractAttribute : attributes) {
            if (!(abstractAttribute != null && (abstractAttribute instanceof HeadAttribute || abstractAttribute instanceof GlobalAttribute))) {
                LOGGER.warning(abstractAttribute
                        + " is not an instance of HeadAttribute");
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
