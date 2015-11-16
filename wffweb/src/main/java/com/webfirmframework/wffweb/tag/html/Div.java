package com.webfirmframework.wffweb.tag.html;

import java.util.logging.Logger;

import com.webfirmframework.wffweb.tag.html.attribute.core.AbstractAttribute;
import com.webfirmframework.wffweb.tag.html.identifier.DivAttribute;
import com.webfirmframework.wffweb.tag.html.identifier.GlobalAttribute;

/**
 * @author WFF
 * @since 1.0.0
 * @version 1.0.0
 *
 */
public class Div extends AbstractHtml {

    private static final long serialVersionUID = 5866252512012827294L;

    public static final Logger LOGGER = Logger.getLogger(Div.class.getName());
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
    public Div(final AbstractHtml base, final AbstractAttribute... attributes) {
        super(Div.class.getSimpleName().toLowerCase(), base, attributes);
        for (final AbstractAttribute abstractAttribute : attributes) {
            if (!(abstractAttribute != null && (abstractAttribute instanceof DivAttribute || abstractAttribute instanceof GlobalAttribute))) {
                LOGGER.warning(abstractAttribute
                        + " is not an instance of DivAttribute");
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

    @Override
    public Div clone() throws CloneNotSupportedException {
        return (Div) super.clone();
    }
}
