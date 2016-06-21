package com.webfirmframework.wffweb.tag.html;

import java.util.logging.Logger;

import com.webfirmframework.wffweb.settings.WffConfiguration;
import com.webfirmframework.wffweb.tag.html.attribute.core.AbstractAttribute;
import com.webfirmframework.wffweb.tag.html.identifier.BaseAttributable;
import com.webfirmframework.wffweb.tag.html.identifier.GlobalAttributable;

/**
 * @author WFF
 * @since 1.0.0
 * @version 1.0.0
 *
 */
public class Br extends AbstractHtml {

    private static final long serialVersionUID = 1_0_0L;

    public static final Logger LOGGER = Logger.getLogger(Br.class.getName());

    private static TagType tagType = TagType.SELF_CLOSING;

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
    public Br(final AbstractHtml base, final AbstractAttribute... attributes) {
        super(tagType, TagNameConstants.BR, base, attributes);
        if (WffConfiguration.isDirectionWarningOn()) {
            for (final AbstractAttribute abstractAttribute : attributes) {
                if (!(abstractAttribute != null
                        && (abstractAttribute instanceof BaseAttributable
                                || abstractAttribute instanceof GlobalAttributable))) {
                    LOGGER.warning(abstractAttribute
                            + " is not an instance of BaseAttribute");
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

    /**
     * @param selfClosing
     *            <code>true</code> to set as self closing tag and
     *            <code>false</code> for not to set as self closing tag. The
     *            default value is <code>true</code>.
     * @since 1.0.0
     * @author WFF
     */
    public static void setSelfClosing(final boolean selfClosing) {
        Br.tagType = selfClosing ? TagType.SELF_CLOSING : TagType.NON_CLOSING;
    }

    /**
     * @param nonClosing
     *            <code>true</code> to set as self closing tag and
     *            <code>false</code> for not to set as self closing tag. The
     *            default value is <code>true</code>.
     * @since 1.0.0
     * @author WFF
     */
    public static void setNonClosing(final boolean nonClosing) {
        Br.tagType = nonClosing ? TagType.NON_CLOSING : TagType.SELF_CLOSING;
    }

    /**
     * @return true if it is set as self closing tag otherwise false
     * @since 1.0.0
     * @author WFF
     */
    public static boolean isSelfClosing() {
        return TagType.SELF_CLOSING.equals(Br.tagType);
    }

    /**
     * @return true if it is set as non closing tag otherwise false
     * @since 1.0.0
     * @author WFF
     */
    public static boolean isNonClosing() {
        return TagType.NON_CLOSING.equals(Br.tagType);
    }
}
