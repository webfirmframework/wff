/*
 * Copyright 2014-2024 Web Firm Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * @author WFF
 */
package com.webfirmframework.wffweb.tag.htmlwff;

import com.webfirmframework.wffweb.InvalidTagException;
import com.webfirmframework.wffweb.tag.html.AbstractHtml;
import com.webfirmframework.wffweb.tag.html.TagUtil;

/**
 * @author WFF
 * @since 3.0.2
 */
public enum TagContent {

    /**
     * content type is plain text
     */
    TEXT(false),

    /**
     * content type is HTML
     */
    HTML(true);

    private final boolean contentTypeHtml;

    private TagContent(final boolean contentTypeHtml) {
        this.contentTypeHtml = contentTypeHtml;
    }

    /**
     * Adds inner content for the given parent object, children of parent will be
     * removed if already exists.
     *
     * @param parent  parent the tag on which the given content to be added as text
     *                content type
     * @param content content the content to be added
     * @return parent
     * @since 3.0.13
     */
    public static <R extends AbstractHtml> R text(final R parent, final String content) {
        TagContent.TEXT.addInnerContent(parent, content);
        return parent;
    }

    /**
     * Adds inner content for the given parent tag and its existing children will be
     * removed on if existing content of the parent tag is different from then given
     * one.
     *
     * @param parent  the tag on which the given content to be added as text content
     *                type
     * @param content the content to be added
     * @return parent
     * @since 12.0.0-beta.12
     */
    public static <R extends AbstractHtml> R textIfRequired(final R parent, final String content) {
        if (parent.getFirstChild() instanceof final NoTag noTag) {
            if (noTag.isChildContentTypeHtml() || !String.valueOf(content).equals(noTag.getChildContent())
                    || parent.getChildrenSize() > 1) {
                TagContent.TEXT.addInnerContent(parent, content);
            }
        } else {
            TagContent.TEXT.addInnerContent(parent, content);
        }
        return parent;
    }

    /**
     * Prepends content in the given parent object as the first child.
     *
     * @param parent
     * @return parent
     * @since 3.0.13
     */
    public static <R extends AbstractHtml> R prependText(final R parent, final String content) {
        TagContent.TEXT.prependChild(parent, content);
        return parent;
    }

    /**
     * Appends content in the given parent object as the last child.
     *
     * @param parent
     * @return parent
     * @since 3.0.13
     */
    public static <R extends AbstractHtml> R appendText(final R parent, final String content) {
        TagContent.TEXT.appendChild(parent, content);
        return parent;
    }

    /**
     * Adds inner content for the given parent object, children of parent will be
     * removed if already exists. It internally marks the given content type as
     * html.
     *
     * @param parent  the tag on which the given content to be added as html content
     *                type
     * @param content the content to be added
     * @return parent
     * @since 3.0.13
     */
    public static <R extends AbstractHtml> R html(final R parent, final String content) {
        TagContent.HTML.addInnerContent(parent, content);
        return parent;
    }

    /**
     * Adds inner content for the given parent tag, its children will be removed if
     * already exists. It internally marks the given content type as html. The
     * operation will be done only if the existing content of the given parent tag
     * is different from the given content.
     *
     * @param parent  the tag on which the given content to be added as html content
     *                type
     * @param content the content to be added
     * @return parent
     * @since 12.0.0-beta.12
     */
    public static <R extends AbstractHtml> R htmlIfRequired(final R parent, final String content) {

        if (parent.getFirstChild() instanceof final NoTag noTag) {
            if (!noTag.isChildContentTypeHtml() || !String.valueOf(content).equals(noTag.getChildContent())
                    || parent.getChildrenSize() > 1) {
                TagContent.HTML.addInnerContent(parent, content);
            }
        } else {
            TagContent.HTML.addInnerContent(parent, content);
        }

        return parent;
    }

    /**
     * Prepends content in the given parent object as the first child. It internally
     * marks the given content type as html.
     *
     * @param parent
     * @return parent
     * @since 3.0.13
     */
    public static <R extends AbstractHtml> R prependHtml(final R parent, final String content) {
        TagContent.HTML.prependChild(parent, content);
        return parent;
    }

    /**
     * Appends content in the given parent object as the last child. It internally
     * marks the given content type as html.
     *
     * @param parent
     * @return parent
     * @since 3.0.13
     */
    public static <R extends AbstractHtml> R appendHtml(final R parent, final String content) {
        TagContent.HTML.appendChild(parent, content);
        return parent;
    }

    private void checkCompatibility(final AbstractHtml tag) {
        if (TagUtil.isTagless(tag)) {
            throw new InvalidTagException("The given tag instance is not supported for this operation.");
        }
    }

    /**
     * appends the given contents on the given parent tag either as text or HTML
     * based on the enum constant
     *
     * @param parent   the tag on which the operation to be done.
     * @param contents contents to append to the given parent tag.
     *
     * @throws InvalidTagException if the given tag instance is not supported for
     *                             this operation.
     * @since 3.0.2
     */
    public void appendChildren(final AbstractHtml parent, final String... contents) {

        checkCompatibility(parent);

        final NoTag[] noTags = new NoTag[contents.length];
        int index = 0;
        for (final String content : contents) {
            noTags[index] = new NoTag(null, content, contentTypeHtml);
            index++;
        }
        parent.appendChildren(noTags);
    }

    /**
     * appends the given content on the given parent tag either as text or HTML
     * based on the enum constant
     *
     * @param parent  the tag on which the operation to be done.
     * @param content content to append to the given parent tag.
     * @return true if appended
     * @throws InvalidTagException if the given tag instance is not supported for
     *                             this operation.
     * @since 3.0.2
     */
    public boolean appendChild(final AbstractHtml parent, final String content) {
        checkCompatibility(parent);
        return parent.appendChild(new NoTag(null, content, contentTypeHtml));
    }

    /**
     * prepends the given content on the given parent tag either as text or HTML
     * based on the enum constant
     *
     * @param parent  the tag on which the operation to be done.
     * @param content content to prepend to the given parent tag.
     * @throws InvalidTagException if the given tag instance is not supported for
     *                             this operation.
     * @since 3.0.2
     */
    public void prependChild(final AbstractHtml parent, final String content) {
        checkCompatibility(parent);
        parent.prependChildren(new NoTag(null, content, contentTypeHtml));
    }

    /**
     * prepends the given contents on the given parent tag either as text or HTML
     * based on the enum constant
     *
     * @param parent   the tag on which the operation to be done.
     * @param contents contents to prepend to the given parent tag.
     * @throws InvalidTagException if the given tag instance is not supported for
     *                             this operation.
     * @since 3.0.2
     */
    public void prependChildren(final AbstractHtml parent, final String... contents) {
        checkCompatibility(parent);
        final NoTag[] noTags = new NoTag[contents.length];
        int index = 0;
        for (final String content : contents) {
            noTags[index] = new NoTag(null, content, contentTypeHtml);
            index++;
        }
        parent.prependChildren(noTags);
    }

    /**
     * Inserts the given content before given parent tag either as text or HTML
     * based on the enum constant. There must be a parent for the given parent tag.
     *
     * @param parent  the tag on which the operation to be done.
     * @param content content to insert before the given parent tag.
     * @return true if inserted
     * @since 3.0.2
     */
    public boolean insertBefore(final AbstractHtml parent, final String content) {
        return parent.insertBefore(new NoTag(null, content, contentTypeHtml));
    }

    /**
     * Inserts the given contents before given parent tag either as text or HTML
     * based on the enum constant. There must be a parent for the given parent tag.
     *
     * @param parent   the tag on which the operation to be done.
     * @param contents contents to insert before the given parent tag.
     * @return true if inserted
     * @since 3.0.2
     */
    public boolean insertBefore(final AbstractHtml parent, final String... contents) {
        final NoTag[] noTags = new NoTag[contents.length];
        int index = 0;
        for (final String content : contents) {
            noTags[index] = new NoTag(null, content, contentTypeHtml);
            index++;
        }
        return parent.insertBefore(noTags);
    }

    /**
     * Inserts the given content after given parent tag either as text or HTML based
     * on the enum constant. There must be a parent for the given parent tag.
     *
     * @param parent  the tag on which the operation to be done.
     * @param content content to insert after the given parent tag.
     * @return true if inserted
     * @since 3.0.2
     */
    public boolean insertAfter(final AbstractHtml parent, final String content) {
        return parent.insertAfter(new NoTag(null, content, contentTypeHtml));
    }

    /**
     * Inserts the given contents after given parent tag either as text or HTML
     * based on the enum constant. There must be a parent for the given parent tag.
     *
     * @param parent   the tag on which the operation to be done.
     * @param contents contents to insert after the given parent tag.
     * @return true if inserted
     * @since 3.0.2
     */
    public boolean insertAfter(final AbstractHtml parent, final String... contents) {
        final NoTag[] noTags = new NoTag[contents.length];
        int index = 0;
        for (final String content : contents) {
            noTags[index] = new NoTag(null, content, contentTypeHtml);
            index++;
        }
        return parent.insertAfter(noTags);
    }

    /**
     * Removes all children from the given parent tag and appends the given content
     * on the given parent tag either as text or HTML based on the enum constant
     *
     * @param parent  the tag on which the operation to be done.
     * @param content replacement content for the children of the given parent.
     * @throws InvalidTagException if the given tag instance is not supported for
     *                             this operation.
     * @throws InvalidTagException if the given tag instance is not supported for
     *                             this operation.
     * @since 3.0.2
     */
    public void replaceChildren(final AbstractHtml parent, final String content) {
        checkCompatibility(parent);
        parent.addInnerHtmls(new NoTag(null, content, contentTypeHtml));
    }

    /**
     * Removes all children from the given parent tag and appends the given contents
     * on the given parent tag either as text or HTML based on the enum constant
     *
     * @param parent   the tag on which the operation to be done.
     * @param contents replacement contents for the children of the given parent.
     * @throws InvalidTagException if the given tag instance is not supported for
     *                             this operation.
     * @since 3.0.2
     */
    public void replaceChildren(final AbstractHtml parent, final String... contents) {
        checkCompatibility(parent);
        final NoTag[] noTags = new NoTag[contents.length];
        int index = 0;
        for (final String content : contents) {
            noTags[index] = new NoTag(null, content, contentTypeHtml);
            index++;
        }
        parent.addInnerHtmls(noTags);
    }

    /**
     * Removes all children from the given parent tag and appends the given content
     * on the given parent tag either as text or HTML based on the enum constant
     *
     * @param parent  the tag on which the operation to be done.
     * @param content replacement content for the children of the given parent.
     * @throws InvalidTagException if the given tag instance is not supported for
     *                             this operation.
     * @since 3.0.2
     */
    public void addInnerContent(final AbstractHtml parent, final String content) {
        replaceChildren(parent, content);
    }

    /**
     * Removes all children from the given parent tag and appends the given contents
     * on the given parent tag either as text or HTML based on the enum constant
     *
     * @param parent   the tag on which the operation to be done.
     * @param contents replacement contents for the children of the given parent.
     * @throws InvalidTagException if the given tag instance is not supported for
     *                             this operation.
     * @since 3.0.2
     */
    public void addInnerContents(final AbstractHtml parent, final String... contents) {
        replaceChildren(parent, contents);
    }

}
