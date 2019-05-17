package com.webfirmframework.wffweb.tag.html;

import java.util.logging.Logger;

import com.webfirmframework.wffweb.settings.WffConfiguration;
import com.webfirmframework.wffweb.tag.html.attribute.core.AbstractAttribute;
import com.webfirmframework.wffweb.tag.html.core.IndexedTagName;
import com.webfirmframework.wffweb.tag.html.identifier.GlobalAttributable;
import com.webfirmframework.wffweb.tag.html.identifier.H6Attributable;

/**
 * @author WFF
 * @since 1.1.3
 * @version 1.1.3
 *
 */
public class H6 extends AbstractHtml {

    private static final long serialVersionUID = 1_0_0L;

    public static final Logger LOGGER = Logger.getLogger(H6.class.getName());

    private static volatile int TAG_NAME_INDEX = -1;

    static {
        final Integer index = IndexedTagName.INSTANCE
                .getIndexByTagName(TagNameConstants.H6);
        TAG_NAME_INDEX = index != null ? index : -1;
    }

    {
        if (TAG_NAME_INDEX == -1) {
            final Integer index = IndexedTagName.INSTANCE
                    .getIndexByTagName(TagNameConstants.H6);
            TAG_NAME_INDEX = index != null ? index : -1;
        }
        super.setTagNameIndex(TAG_NAME_INDEX);
        init();
    }

    /**
     *
     * @param base
     *                       i.e. parent tag of this tag
     * @param attributes
     *                       An array of {@code AbstractAttribute}
     *
     * @since 1.1.3
     */
    public H6(final AbstractHtml base, final AbstractAttribute... attributes) {
        super(TagNameConstants.H6, base, attributes);
        if (WffConfiguration.isDirectionWarningOn()) {
            warnForUnsupportedAttributes(attributes);
        }
    }

    private static void warnForUnsupportedAttributes(
            final AbstractAttribute... attributes) {
        for (final AbstractAttribute abstractAttribute : attributes) {
            if (!(abstractAttribute != null
                    && (abstractAttribute instanceof H6Attributable
                            || abstractAttribute instanceof GlobalAttributable))) {
                LOGGER.warning(abstractAttribute
                        + " is not an instance of H6Attribute");
            }
        }
    }

    /**
     * invokes only once per object
     *
     * @since 1.1.3
     */
    protected void init() {
        // to override and use this method
    }

}
