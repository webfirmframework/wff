/*
 * Copyright 2014-2017 Web Firm Framework
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
 */
package com.webfirmframework.wffweb.tag.repository;

import java.util.HashSet;
import java.util.Set;

import com.webfirmframework.wffweb.NullValueException;
import com.webfirmframework.wffweb.tag.html.AbstractHtml;
import com.webfirmframework.wffweb.tag.html.AbstractHtmlRepository;
import com.webfirmframework.wffweb.tag.html.NestedChild;
import com.webfirmframework.wffweb.tag.html.attribute.AttributeNameConstants;
import com.webfirmframework.wffweb.tag.html.attribute.core.AbstractAttribute;

/**
 * @author WFF
 * @since 2.1.8
 */
public class TagRepository extends AbstractHtmlRepository {

    private final AbstractHtml[] rootTags;

    public TagRepository(final AbstractHtml... rootTags) {
        this.rootTags = rootTags;
    }

    /**
     * finds and returns the first tag matching with the given id.
     *
     * @param id
     *            the value of id attribute.
     * @param fromTags
     *            from the given tags and its nested children the finding to be
     *            done.
     * @return the first found tag with the given id
     * @since 2.1.8
     * @author WFF
     */
    public static AbstractHtml findTagById(final String id,
            final AbstractHtml... fromTags) {

        if (id == null) {
            throw new NullValueException("The id should not be null");
        }

        if (fromTags == null) {
            throw new NullValueException("The fromTags should not be null");
        }

        final AbstractHtml[] matchingTag = new AbstractHtml[1];

        loopThroughAllNestedChildren(new NestedChild() {

            @Override
            public boolean eachChild(final AbstractHtml child) {

                final AbstractAttribute idAttr = child
                        .getAttributeByName(AttributeNameConstants.ID);

                if (idAttr != null && id.equals(idAttr.getAttributeValue())) {
                    matchingTag[0] = child;
                    return false;
                }

                return true;
            }
        }, true, fromTags);

        return matchingTag[0];
    }

    /**
     * finds and returns the first tag matching with the given id.
     *
     * @param id
     *            the value of id attribute.
     * @return the first found tag with the given id
     * @since 2.1.8
     * @author WFF
     */
    public AbstractHtml findTagById(final String id) {
        return findTagById(id, rootTags);
    }

    /**
     * finds and returns the set of tags (including the nested tags) matching
     * with the given attribute name and value.
     *
     * @param attributeName
     *            the name of the attribute.
     * @param attributeValue
     *            the value of the attribute
     * @param fromTags
     *            from which the findings to be done.
     * @return the set of tags matching with the given attribute name and value.
     * @since 2.1.8
     * @author WFF
     */
    public static Set<AbstractHtml> findTagsByAttribute(
            final String attributeName, final String attributeValue,
            final AbstractHtml... fromTags) {

        if (attributeName == null) {
            throw new NullValueException(
                    "The attributeName should not be null");
        }
        if (attributeValue == null) {
            throw new NullValueException(
                    "The attributeValue should not be null");
        }

        if (fromTags == null) {
            throw new NullValueException("The fromTags should not be null");
        }

        final Set<AbstractHtml> matchingTags = new HashSet<AbstractHtml>();

        loopThroughAllNestedChildren(new NestedChild() {

            @Override
            public boolean eachChild(final AbstractHtml child) {

                final AbstractAttribute idAttr = child
                        .getAttributeByName(AttributeNameConstants.ID);

                if (idAttr != null
                        && attributeName.equals(idAttr.getAttributeName())
                        && attributeValue.equals(idAttr.getAttributeValue())) {
                    matchingTags.add(child);
                }

                return true;
            }
        }, true, fromTags);

        return matchingTags;
    }

    /**
     * finds and returns the first (including the nested tags) matching with the
     * given attribute name and value.
     *
     * @param attributeName
     *            the name of the attribute.
     * @param attributeValue
     *            the value of the attribute
     * @param fromTags
     *            from which the findings to be done.
     * @return the first matching tag with the given attribute name and value.
     * @since 2.1.8
     * @author WFF
     */
    public static AbstractHtml findOneTagByAttribute(final String attributeName,
            final String attributeValue, final AbstractHtml... fromTags) {

        if (attributeName == null) {
            throw new NullValueException(
                    "The attributeName should not be null");
        }
        if (attributeValue == null) {
            throw new NullValueException(
                    "The attributeValue should not be null");
        }

        if (fromTags == null) {
            throw new NullValueException("The fromTags should not be null");
        }

        final AbstractHtml[] matchingTag = new AbstractHtml[1];

        loopThroughAllNestedChildren(new NestedChild() {

            @Override
            public boolean eachChild(final AbstractHtml child) {

                final AbstractAttribute idAttr = child
                        .getAttributeByName(AttributeNameConstants.ID);

                if (idAttr != null
                        && attributeName.equals(idAttr.getAttributeName())
                        && attributeValue.equals(idAttr.getAttributeValue())) {
                    matchingTag[0] = child;
                    return false;
                }

                return true;
            }
        }, true, fromTags);

        return matchingTag[0];
    }

    /**
     * finds and returns the first (including the nested tags) matching with the
     * given attribute name.
     *
     * @param attributeName
     *            the name of the attribute.
     * @param fromTags
     *            from which the findings to be done.
     * @return the first matching tag with the given attribute name and value.
     * @since 2.1.8
     * @author WFF
     */
    public static AbstractHtml findOneTagByAttributeName(
            final String attributeName, final AbstractHtml... fromTags) {

        if (attributeName == null) {
            throw new NullValueException(
                    "The attributeName should not be null");
        }

        if (fromTags == null) {
            throw new NullValueException("The fromTags should not be null");
        }

        final AbstractHtml[] matchingTag = new AbstractHtml[1];

        loopThroughAllNestedChildren(new NestedChild() {

            @Override
            public boolean eachChild(final AbstractHtml child) {

                final AbstractAttribute idAttr = child
                        .getAttributeByName(AttributeNameConstants.ID);

                if (idAttr != null
                        && attributeName.equals(idAttr.getAttributeName())) {
                    matchingTag[0] = child;
                    return false;
                }

                return true;
            }
        }, true, fromTags);

        return matchingTag[0];
    }

    /**
     * finds and returns the set of tags (including the nested tags) matching
     * with the given attribute name.
     *
     * @param attributeName
     *            the name of the attribute.
     * @param fromTags
     *            from which the findings to be done.
     * @return the set of tags matching with the given attribute.
     * @since 2.1.8
     * @author WFF
     */
    public static Set<AbstractHtml> findTagsByAttributeName(
            final String attributeName, final AbstractHtml... fromTags) {

        if (attributeName == null) {
            throw new NullValueException(
                    "The attributeName should not be null");
        }

        if (fromTags == null) {
            throw new NullValueException("The fromTags should not be null");
        }

        final Set<AbstractHtml> matchingTags = new HashSet<AbstractHtml>();

        loopThroughAllNestedChildren(new NestedChild() {

            @Override
            public boolean eachChild(final AbstractHtml child) {

                final AbstractAttribute idAttr = child
                        .getAttributeByName(AttributeNameConstants.ID);

                if (idAttr != null
                        && attributeName.equals(idAttr.getAttributeName())) {
                    matchingTags.add(child);
                }

                return true;
            }
        }, true, fromTags);

        return matchingTags;
    }

    /**
     * finds and returns the set of tags (including the nested tags) matching
     * with the given attribute name.
     *
     * @param attributeName
     *            the name of the attribute.
     * @return the set of tags matching with the given attribute.
     * @since 2.1.8
     * @author WFF
     */
    public Set<AbstractHtml> findTagsByAttributeName(
            final String attributeName) {
        return findTagsByAttributeName(attributeName, rootTags);
    }

    /**
     * finds and returns the set of tags (including the nested tags) matching
     * with the given attribute name and value.
     *
     * @param attributeName
     *            the name of the attribute.
     * @param attributeValue
     *            the value of the attribute
     * @return the set of tags matching with the given attribute name and value.
     * @since 2.1.8
     * @author WFF
     */
    public Set<AbstractHtml> findTagsByAttribute(final String attributeName,
            final String attributeValue) {
        return findTagsByAttribute(attributeName, attributeValue, rootTags);
    }

    /**
     * finds and returns the first (including the nested tags) matching with the
     * given attribute name and value.
     *
     * @param attributeName
     *            the name of the attribute.
     * @param attributeValue
     *            the value of the attribute
     * @return the first matching tag with the given attribute name and value.
     * @since 2.1.8
     * @author WFF
     */
    public AbstractHtml findOneTagByAttribute(final String attributeName,
            final String attributeValue) {
        return findOneTagByAttribute(attributeName, attributeValue, rootTags);
    }

    /**
     * finds and returns the first (including the nested tags) matching with the
     * given attribute name.
     *
     * @param attributeName
     *            the name of the attribute.
     * @return the first matching tag with the given attribute name and value.
     * @since 2.1.8
     * @author WFF
     */
    public AbstractHtml findOneTagByAttributeName(final String attributeName) {
        return findOneTagByAttributeName(attributeName, rootTags);
    }

}
