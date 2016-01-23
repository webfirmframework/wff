package com.webfirmframework.wffweb.tag.htmlwff;

import java.util.logging.Logger;

import com.webfirmframework.wffweb.settings.WffConfiguration;
import com.webfirmframework.wffweb.tag.html.AbstractHtml;
import com.webfirmframework.wffweb.tag.html.attribute.core.AbstractAttribute;
import com.webfirmframework.wffweb.tag.html.identifier.BaseAttributable;
import com.webfirmframework.wffweb.tag.html.identifier.GlobalAttributable;

/**
 * @author WFF
 * @since 1.0.0
 * @version 1.0.0
 *
 */
public class CustomTag extends AbstractHtml {

    private static final long serialVersionUID = 1_0_0L;

    public static final Logger LOGGER = Logger
            .getLogger(CustomTag.class.getName());

    private TagType tagType = TagType.OPENING_CLOSING;

    {
        init();
    }

    /**
     * Represents the root of an HTML or XHTML document. All other elements must
     * be descendants of this element.
     *
     * @param tagName
     *            the tag name
     * @param tagType
     *            the tag type for eg: {@code AbstractHtml.TagType.SELF_CLOSING}
     *
     * @param base
     *            i.e. parent tag of this tag
     * @param attributes
     *            An array of {@code AbstractAttribute}
     *
     * @since 1.0.0
     */
    public CustomTag(final String tagName, final TagType tagType,
            final AbstractHtml base, final AbstractAttribute... attributes) {
        super(tagType, tagName, base, attributes);
        this.tagType = tagType;
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
     * Represents the root of an HTML or XHTML document. All other elements must
     * be descendants of this element.
     *
     * @param tagName
     *            the tag name
     *
     * @param base
     *            i.e. parent tag of this tag
     * @param attributes
     *            An array of {@code AbstractAttribute}
     *
     * @since 1.0.0
     */
    public CustomTag(final String tagName, final AbstractHtml base,
            final AbstractAttribute... attributes) {
        super(TagType.OPENING_CLOSING, tagName, base, attributes);
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
    }

    /**
     * @return true if it is set as self closing tag otherwise false
     * @since 1.0.0
     * @author WFF
     */
    public boolean isSelfClosing() {
        return tagType == TagType.SELF_CLOSING;
    }

    /**
     * @return true if it is set as non closing tag otherwise false
     * @since 1.0.0
     * @author WFF
     */
    public boolean isNonClosing() {
        return tagType == TagType.NON_CLOSING;
    }
}
