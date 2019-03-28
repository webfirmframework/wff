/*
 * Copyright 2014-2019 Web Firm Framework
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

import com.webfirmframework.wffweb.tag.html.AbstractHtml;

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
     * appends the given contents on the given parent tag either as text or HTML
     * based on the enum constant
     *
     * @param parent
     *                     the tag on which the operation to be done.
     * @param contents
     *                     contents to append to the given parent tag.
     * @since 3.0.2
     */
    public void appendChildren(final AbstractHtml parent,
            final String... contents) {
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
     * @param parent
     *                    the tag on which the operation to be done.
     * @param content
     *                    content to append to the given parent tag.
     * @since 3.0.2
     */
    public void appendChild(final AbstractHtml parent, final String content) {
        parent.appendChild(new NoTag(null, content, contentTypeHtml));
    }

    /**
     * prepends the given content on the given parent tag either as text or HTML
     * based on the enum constant
     *
     * @param parent
     *                    the tag on which the operation to be done.
     * @param content
     *                    content to prepend to the given parent tag.
     * @since 3.0.2
     */
    public void prependChild(final AbstractHtml parent, final String content) {
        parent.prependChildren(new NoTag(null, content, contentTypeHtml));
    }

    /**
     * prepends the given contents on the given parent tag either as text or
     * HTML based on the enum constant
     *
     * @param parent
     *                     the tag on which the operation to be done.
     * @param contents
     *                     contents to prepend to the given parent tag.
     * @since 3.0.2
     */
    public void prependChildren(final AbstractHtml parent,
            final String... contents) {
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
     * based on the enum constant. There must be a parent for the given parent
     * tag.
     *
     * @param parent
     *                    the tag on which the operation to be done.
     * @param content
     *                    content to insert before the given parent tag.
     * @since 3.0.2
     */
    public void insertBefore(final AbstractHtml parent, final String content) {
        parent.insertBefore(new NoTag(null, content, contentTypeHtml));
    }

    /**
     * Inserts the given contents before given parent tag either as text or HTML
     * based on the enum constant. There must be a parent for the given parent
     * tag.
     *
     * @param parent
     *                     the tag on which the operation to be done.
     * @param contents
     *                     contents to insert before the given parent tag.
     * @since 3.0.2
     */
    public void insertBefore(final AbstractHtml parent,
            final String... contents) {
        final NoTag[] noTags = new NoTag[contents.length];
        int index = 0;
        for (final String content : contents) {
            noTags[index] = new NoTag(null, content, contentTypeHtml);
            index++;
        }
        parent.insertBefore(noTags);
    }

    /**
     * Inserts the given content after given parent tag either as text or HTML
     * based on the enum constant. There must be a parent for the given parent
     * tag.
     *
     * @param parent
     *                    the tag on which the operation to be done.
     * @param content
     *                    content to insert after the given parent tag.
     * @since 3.0.2
     */
    public void insertAfter(final AbstractHtml parent, final String content) {
        parent.insertAfter(new NoTag(null, content, contentTypeHtml));
    }

    /**
     * Inserts the given contents after given parent tag either as text or HTML
     * based on the enum constant. There must be a parent for the given parent
     * tag.
     *
     * @param parent
     *                     the tag on which the operation to be done.
     * @param contents
     *                     contents to insert after the given parent tag.
     * @since 3.0.2
     */
    public void insertAfter(final AbstractHtml parent,
            final String... contents) {
        final NoTag[] noTags = new NoTag[contents.length];
        int index = 0;
        for (final String content : contents) {
            noTags[index] = new NoTag(null, content, contentTypeHtml);
            index++;
        }
        parent.insertAfter(noTags);
    }

    /**
     * Removes all children from the given parent tag and appends the given
     * content on the given parent tag either as text or HTML based on the enum
     * constant
     *
     * @param parent
     *                    the tag on which the operation to be done.
     * @param content
     *                    replacement content for the children of the given
     *                    parent.
     * @since 3.0.2
     */
    public void replaceChildren(final AbstractHtml parent,
            final String content) {
        parent.addInnerHtml(new NoTag(null, content, contentTypeHtml));
    }

    /**
     * Removes all children from the given parent tag and appends the given
     * contents on the given parent tag either as text or HTML based on the enum
     * constant
     *
     * @param parent
     *                     the tag on which the operation to be done.
     * @param contents
     *                     replacement contents for the children of the given
     *                     parent.
     * @since 3.0.2
     */
    public void replaceChildren(final AbstractHtml parent,
            final String... contents) {
        final NoTag[] noTags = new NoTag[contents.length];
        int index = 0;
        for (final String content : contents) {
            noTags[index] = new NoTag(null, content, contentTypeHtml);
            index++;
        }
        parent.addInnerHtmls(noTags);
    }

    /**
     * Removes all children from the given parent tag and appends the given
     * content on the given parent tag either as text or HTML based on the enum
     * constant
     *
     * @param parent
     *                    the tag on which the operation to be done.
     * @param content
     *                    replacement content for the children of the given
     *                    parent.
     * @since 3.0.2
     */
    public void addInnerContent(final AbstractHtml parent,
            final String content) {
        replaceChildren(parent, content);
    }

    /**
     * Removes all children from the given parent tag and appends the given
     * contents on the given parent tag either as text or HTML based on the enum
     * constant
     *
     * @param parent
     *                     the tag on which the operation to be done.
     * @param contents
     *                     replacement contents for the children of the given
     *                     parent.
     * @since 3.0.2
     */
    public void addInnerContent(final AbstractHtml parent,
            final String... contents) {
        replaceChildren(parent, contents);
    }

}