package com.webfirmframework.wffweb.tag.html.formsandinputs;

import java.util.logging.Logger;

import com.webfirmframework.wffweb.settings.WffConfiguration;
import com.webfirmframework.wffweb.tag.html.AbstractHtml;
import com.webfirmframework.wffweb.tag.html.TagNameConstants;
import com.webfirmframework.wffweb.tag.html.attribute.core.AbstractAttribute;
import com.webfirmframework.wffweb.tag.html.identifier.GlobalAttributable;
import com.webfirmframework.wffweb.tag.html.identifier.TextAreaAttributable;

/**
 * @author WFF
 * @since 1.0.0
 * @version 1.0.0
 *
 */
public class TextArea extends AbstractHtml {

    private static final long serialVersionUID = 1_0_0L;

    public static final Logger LOGGER = Logger
            .getLogger(TextArea.class.getName());

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
    public TextArea(final AbstractHtml base,
            final AbstractAttribute... attributes) {
        super(TagNameConstants.TEXTAREA, base, attributes);
        if (WffConfiguration.isDirectionWarningOn()) {
            for (final AbstractAttribute abstractAttribute : attributes) {
                if (!(abstractAttribute != null
                        && (abstractAttribute instanceof TextAreaAttributable
                                || abstractAttribute instanceof GlobalAttributable))) {
                    LOGGER.warning(abstractAttribute
                            + " is not an instance of TextAreaAttribute");
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
        // to override and use this method
    }

}
