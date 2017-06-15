package com.webfirmframework.wffweb.tag.html.formsandinputs;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import com.webfirmframework.wffweb.settings.WffConfiguration;
import com.webfirmframework.wffweb.tag.html.AbstractHtml;
import com.webfirmframework.wffweb.tag.html.NestedChild;
import com.webfirmframework.wffweb.tag.html.TagNameConstants;
import com.webfirmframework.wffweb.tag.html.attribute.AttributeNameConstants;
import com.webfirmframework.wffweb.tag.html.attribute.Type;
import com.webfirmframework.wffweb.tag.html.attribute.core.AbstractAttribute;
import com.webfirmframework.wffweb.tag.html.identifier.FormAttributable;
import com.webfirmframework.wffweb.tag.html.identifier.GlobalAttributable;

/**
 * @author WFF
 * @since 1.0.0
 * @version 1.0.0
 *
 */
public class Form extends AbstractHtml {

    private static final long serialVersionUID = 1_0_0L;

    public static final Logger LOGGER = Logger.getLogger(Form.class.getName());

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
    public Form(final AbstractHtml base,
            final AbstractAttribute... attributes) {
        super(TagNameConstants.FORM, base, attributes);
        if (WffConfiguration.isDirectionWarningOn()) {
            warnForUnsupportedAttributes(attributes);
        }
    }

    private static void warnForUnsupportedAttributes(
            final AbstractAttribute... attributes) {
        for (final AbstractAttribute abstractAttribute : attributes) {
            if (!(abstractAttribute != null
                    && (abstractAttribute instanceof FormAttributable
                            || abstractAttribute instanceof GlobalAttributable))) {
                LOGGER.warning(abstractAttribute
                        + " is not an instance of FormAttribute");
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
     * prepares and gets the js object for the given tag names under this form
     * tag. This js object may be used to return in onsubmit attribute.
     *
     * @param onlyForTagNames
     *
     *            TagNameConstants.INPUT, TagNameConstants.TEXTAREA and
     *            TagNameConstants.SELECT
     *
     * @return the js object string for the given tag names. The returned js
     *         string will be as {name1.name1.value} where name1 is the value of
     *         name attribute of the field.
     * @since 2.1.8
     * @author WFF
     */
    public String getNameBasedJsObject(final Set<String> onlyForTagNames) {

        // "{" should not be changed to '{'
        final StringBuilder jsObjectBuilder = new StringBuilder("{");
        final Set<String> appendedValues = new HashSet<String>();

        loopThroughAllNestedChildren(new NestedChild() {

            @Override
            public boolean eachChild(final AbstractHtml child) {

                final String tagName = child.getTagName();

                if (onlyForTagNames.contains(tagName)) {

                    final AbstractAttribute nameAttr = child
                            .getAttributeByName(AttributeNameConstants.NAME);
                    final AbstractAttribute typeAttr = child
                            .getAttributeByName(AttributeNameConstants.TYPE);

                    if (nameAttr != null) {
                        final String value = nameAttr.getAttributeValue();
                        if (!appendedValues.contains(value)) {

                            if (typeAttr != null && Type.CHECKBOX
                                    .equals(typeAttr.getAttributeValue())) {
                                jsObjectBuilder.append(value).append(':')
                                        .append(value).append(".checked,");
                                appendedValues.add(value);

                            } else {
                                jsObjectBuilder.append(value).append(':')
                                        .append(value).append(".value,");
                                appendedValues.add(value);

                            }

                        }
                    }

                }
                return true;
            }
        }, false, this);

        jsObjectBuilder.replace(jsObjectBuilder.length() - 1,
                jsObjectBuilder.length(), "}");
        return jsObjectBuilder.toString();
    }

    /**
     *
     * prepares and gets the js object for the input tag names
     * (TagNameConstants.INPUT, TagNameConstants.TEXTAREA and
     * TagNameConstants.SELECT) under this form tag. <br>
     * NB:- If there are any missing input tag types, please inform
     * webfirmframework to update this method. <br>
     * This js object may be used to return in onsubmit attribute.
     *
     * @return the js object string for field names of TagNameConstants.INPUT,
     *         TagNameConstants.TEXTAREA and TagNameConstants.SELECT. The
     *         returned js string will be as {name1.name1.value} where name1 is
     *         the value of name attribute of the field. If the input type is
     *         checkbox then checked property will be included instead of value
     *         property.
     * @since 2.1.8
     * @author WFF
     */
    public String getNameBasedJsObject() {

        final Set<String> tagNames = new HashSet<String>();
        tagNames.add(TagNameConstants.INPUT);
        tagNames.add(TagNameConstants.TEXTAREA);
        tagNames.add(TagNameConstants.SELECT);

        return getNameBasedJsObject(tagNames);
    }

}
