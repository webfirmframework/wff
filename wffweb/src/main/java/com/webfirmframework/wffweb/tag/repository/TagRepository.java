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

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;

import com.webfirmframework.wffweb.InvalidTagException;
import com.webfirmframework.wffweb.NullValueException;
import com.webfirmframework.wffweb.WffSecurityException;
import com.webfirmframework.wffweb.security.object.SecurityClassConstants;
import com.webfirmframework.wffweb.server.page.BrowserPage;
import com.webfirmframework.wffweb.tag.html.AbstractHtml;
import com.webfirmframework.wffweb.tag.html.AbstractHtmlRepository;
import com.webfirmframework.wffweb.tag.html.NestedChild;
import com.webfirmframework.wffweb.tag.html.attribute.AttributeNameConstants;
import com.webfirmframework.wffweb.tag.html.attribute.core.AbstractAttribute;
import com.webfirmframework.wffweb.tag.htmlwff.NoTag;
import com.webfirmframework.wffweb.wffbm.data.WffBMArray;
import com.webfirmframework.wffweb.wffbm.data.WffBMObject;

/**
 * {@code TagRepository} class for tag operations like finding tags/attributes
 * with certain criteria, upserting/deleting wffObjects from tag etc... The
 * object of {@code TagRepository} class may be got by
 * {@link BrowserPage#getTagRepository()} method.
 *
 * @author WFF
 * @since 2.1.8
 */
public class TagRepository extends AbstractHtmlRepository
        implements Serializable {

    private static final long serialVersionUID = 1L;

    private final BrowserPage browserPage;

    private final AbstractHtml[] rootTags;

    /**
     * This constructor is only for internal use. To get an object of
     * {@code TagRepository} use {@code BrowserPage#getTagRepository()} method.
     *
     * @param browserPage
     *            the instance of {@code BrowserPage}
     * @param rootTags
     *            the rootTags in the browserPage instance.
     * @since 2.1.8
     * @author WFF
     */
    public TagRepository(final Object accessObject,
            final BrowserPage browserPage, final AbstractHtml... rootTags) {

        if (accessObject == null || !((SecurityClassConstants.ABSTRACT_HTML
                .equals(accessObject.getClass().getName()))
                || (SecurityClassConstants.BROWSER_PAGE
                        .equals(accessObject.getClass().getName())))) {
            throw new WffSecurityException(
                    "Not allowed to consume this constructor. This method is for internal use.");
        }

        this.browserPage = browserPage;
        this.rootTags = rootTags;
    }

    /**
     * Finds and returns the first tag matching with the given id.
     *
     * @param id
     *            the value of id attribute.
     * @param fromTags
     *            from the given tags and its nested children the finding to be
     *            done.
     * @return the first found tag with the given id
     * @throws NullValueException
     *             if the {@code id} or {@code fromTags} is null
     * @since 2.1.8
     * @author WFF
     */
    public static AbstractHtml findTagById(final String id,
            final AbstractHtml... fromTags) throws NullValueException {

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
     * Finds and returns the first tag matching with the given id.
     *
     * @param id
     *            the value of id attribute.
     * @return the first found tag with the given id
     *
     * @throws NullValueException
     *             if the {@code id} is null
     * @since 2.1.8
     * @author WFF
     */
    public AbstractHtml findTagById(final String id) throws NullValueException {
        return findTagById(id, rootTags);
    }

    /**
     * Finds and returns the collection of tags (including the nested tags)
     * matching with the given attribute name and value.
     *
     * @param attributeName
     *            the name of the attribute.
     * @param attributeValue
     *            the value of the attribute
     * @param fromTags
     *            from which the findings to be done.
     * @return the collection of tags matching with the given attribute name and
     *         value.
     * @throws NullValueException
     *             if the {@code attributeName}, {@code attributeValue} or
     *             {@code fromTags} is null
     * @since 2.1.8
     * @author WFF
     */
    public static Collection<AbstractHtml> findTagsByAttribute(
            final String attributeName, final String attributeValue,
            final AbstractHtml... fromTags) throws NullValueException {

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

        final Collection<AbstractHtml> matchingTags = new HashSet<AbstractHtml>();

        loopThroughAllNestedChildren(new NestedChild() {

            @Override
            public boolean eachChild(final AbstractHtml child) {

                final AbstractAttribute attribute = child
                        .getAttributeByName(attributeName);

                if (attribute != null && attributeValue
                        .equals(attribute.getAttributeValue())) {
                    matchingTags.add(child);
                }

                return true;
            }
        }, true, fromTags);

        return matchingTags;
    }

    /**
     * Finds and returns the collection of tags (including the nested tags)
     * matching with the give tag name.
     *
     * @param tagName
     *            the name of the tag.
     * @param fromTags
     *            from which the findings to be done.
     * @return the collection of tags matching with the given tag name .
     * @throws NullValueException
     *             if the {@code tagName} or {@code fromTags} is null
     * @since 2.1.11
     * @author WFF
     */
    public static Collection<AbstractHtml> findTagsByTagName(
            final String tagName, final AbstractHtml... fromTags)
            throws NullValueException {

        if (tagName == null) {
            throw new NullValueException("The tagName should not be null");
        }

        if (fromTags == null) {
            throw new NullValueException("The fromTags should not be null");
        }

        final Collection<AbstractHtml> matchingTags = new HashSet<AbstractHtml>();

        loopThroughAllNestedChildren(new NestedChild() {

            @Override
            public boolean eachChild(final AbstractHtml child) {

                if (tagName.equals(child.getTagName())) {
                    matchingTags.add(child);
                }

                return true;
            }
        }, true, fromTags);

        return matchingTags;
    }

    /**
     * Finds and returns the collection of attributes (including from nested
     * tags) of the tags matching with the give tag name.
     *
     * @param tagName
     *            the name of the tag.
     * @param fromTags
     *            from which the findings to be done.
     * @return the collection of attributes of the tags matching with the given
     *         tag name.
     * @throws NullValueException
     *             if the {@code tagName} or {@code fromTags} is null
     * @since 2.1.11
     * @author WFF
     */
    public static Collection<AbstractAttribute> findAttributesByTagName(
            final String tagName, final AbstractHtml... fromTags)
            throws NullValueException {

        if (tagName == null) {
            throw new NullValueException("The tagName should not be null");
        }

        if (fromTags == null) {
            throw new NullValueException("The fromTags should not be null");
        }

        final Collection<AbstractAttribute> matchingAttributes = new HashSet<AbstractAttribute>();

        loopThroughAllNestedChildren(new NestedChild() {

            @Override
            public boolean eachChild(final AbstractHtml child) {

                if (tagName.equals(child.getTagName())) {
                    final Collection<AbstractAttribute> attributes = child
                            .getAttributes();
                    if (attributes != null) {
                        matchingAttributes.addAll(attributes);
                    }
                }

                return true;
            }
        }, true, fromTags);

        return matchingAttributes;
    }

    /**
     * Finds and returns the first (including the nested tags) matching with the
     * given attribute name and value.
     *
     * @param attributeName
     *            the name of the attribute.
     * @param attributeValue
     *            the value of the attribute
     * @param fromTags
     *            from which the findings to be done.
     * @return the first matching tag with the given attribute name and value.
     * @throws NullValueException
     *             if the {@code attributeName}, {@code attributeValue} or
     *             {@code fromTags} is null
     * @since 2.1.8
     * @author WFF
     */
    public static AbstractHtml findOneTagByAttribute(final String attributeName,
            final String attributeValue, final AbstractHtml... fromTags)
            throws NullValueException {

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

                final AbstractAttribute attribute = child
                        .getAttributeByName(attributeName);

                if (attribute != null && attributeValue
                        .equals(attribute.getAttributeValue())) {
                    matchingTag[0] = child;
                    return false;
                }

                return true;
            }
        }, true, fromTags);

        return matchingTag[0];
    }

    /**
     * Finds and returns the first (including the nested tags) matching with the
     * given tag name.
     *
     * @param tagName
     *            the name of the tag.
     * @param fromTags
     *            from which the findings to be done.
     * @return the first matching tag with the given tag name.
     * @throws NullValueException
     *             if the {@code tagName} or {@code fromTags} is null
     * @since 2.1.11
     * @author WFF
     */
    public static AbstractHtml findOneTagByTagName(final String tagName,
            final AbstractHtml... fromTags) throws NullValueException {

        if (tagName == null) {
            throw new NullValueException("The tagName should not be null");
        }

        if (fromTags == null) {
            throw new NullValueException("The fromTags should not be null");
        }

        final AbstractHtml[] matchingTag = new AbstractHtml[1];

        loopThroughAllNestedChildren(new NestedChild() {

            @Override
            public boolean eachChild(final AbstractHtml child) {

                if (tagName.equals(child.getTagName())) {
                    matchingTag[0] = child;
                    return false;
                }

                return true;
            }
        }, true, fromTags);

        return matchingTag[0];
    }

    /**
     * Finds and returns the first matching (including from nested tags) tag
     * (which is assignable to the given tag class). <br>
     * <br>
     *
     * <pre>
     * <code>
     * Html html = new Html(null) {{
     *      new Head(this) {{
     *          new TitleTag(this){{
     *              new NoTag(this, "some title");
     *          }};
     *      }};
     *      new Body(this, new Id("one")) {{
     *          new Div(this);
     *      }};
     *  }};
     *
     *  TitleTag titleTag = TagRepository.findOneTagAssignableToTagClass(TitleTag.class, html);
     *
     *  System.out.println(titleTag.getTagName());
     *  System.out.println(titleTag.toHtmlString());
     *
     *  //prints
     *
     *  title
     *  <title>some title</title>
     *
     * </code>
     * </pre>
     *
     * @param tagClass
     *            the class of the tag.
     * @param fromTags
     *            from which the findings to be done.
     * @return the first matching tag which is assignable to the given tag
     *         class.
     * @throws NullValueException
     *             if the {@code tagClass} or {@code fromTags} is null
     * @throws InvalidTagException
     *             if the given tag class is NoTag.class
     * @since 2.1.11
     * @author WFF
     */
    @SuppressWarnings("unchecked")
    public static <T> T findOneTagAssignableToTag(final Class<T> tagClass,
            final AbstractHtml... fromTags)
            throws NullValueException, InvalidTagException {

        if (tagClass == null) {
            throw new NullValueException("The tagClass should not be null");
        }

        if (tagClass == NoTag.class) {
            throw new InvalidTagException(
                    "NoTag.class cannot be used to find tags as it's not a logical tag in behaviour.");
        }

        if (fromTags == null) {
            throw new NullValueException("The fromTags should not be null");
        }

        final AbstractHtml[] matchingTag = new AbstractHtml[1];

        loopThroughAllNestedChildren(new NestedChild() {

            @Override
            public boolean eachChild(final AbstractHtml child) {

                if (tagClass.isAssignableFrom(child.getClass())
                        && !NoTag.class.isAssignableFrom(child.getClass())) {
                    matchingTag[0] = child;
                    return false;
                }

                return true;
            }
        }, true, fromTags);

        return (T) matchingTag[0];
    }

    /**
     * Finds and returns the all matching (including from nested tags) tags
     * (which is assignable to the given tag class). <br>
     * <br>
     *
     * <pre>
     * <code>
     * Html html = new Html(null) {{
     *      new Head(this) {{
     *          new TitleTag(this){{
     *              new NoTag(this, "some title");
     *          }};
     *      }};
     *      new Body(this, new Id("one")) {{
     *          new Div(this);
     *          new Div(this) {{
     *              new Div(this) {{
     *                  new Div(this);
     *              }};
     *          }};
     *          new Div(this);
     *      }};
     *  }};
     *
     *  Collection<Head> heads = TagRepository.findTagsAssignableToTag(Head.class, html);
     *  Collection<Div> divs = TagRepository.findTagsAssignableToTag(Div.class, html);
     *
     *  System.out.println(heads.size());
     *  System.out.println(divs.size());
     *
     *  //prints
     *
     *  1
     *  5
     *
     * </code>
     * </pre>
     *
     * @param tagClass
     *            the class of the tag.
     * @param fromTags
     *            from which the findings to be done.
     * @return the all matching tags which is assignable to the given tag class.
     * @throws NullValueException
     *             if the {@code tagClass} or {@code fromTags} is null
     * @throws InvalidTagException
     *             if the given tag class is NoTag.class
     * @since 2.1.11
     * @author WFF
     */
    @SuppressWarnings("unchecked")
    public static <T> Collection<T> findTagsAssignableToTag(
            final Class<T> tagClass, final AbstractHtml... fromTags)
            throws NullValueException, InvalidTagException {

        if (tagClass == null) {
            throw new NullValueException("The tagClass should not be null");
        }

        if (tagClass == NoTag.class) {
            throw new InvalidTagException(
                    "NoTag.class cannot be used to find tags as it's not a logical tag in behaviour.");
        }

        if (fromTags == null) {
            throw new NullValueException("The fromTags should not be null");
        }

        final Collection<AbstractHtml> matchingTags = new HashSet<AbstractHtml>();

        loopThroughAllNestedChildren(new NestedChild() {

            @Override
            public boolean eachChild(final AbstractHtml child) {

                if (tagClass.isAssignableFrom(child.getClass())
                        && !NoTag.class.isAssignableFrom(child.getClass())) {
                    matchingTags.add(child);
                }

                return true;
            }
        }, true, fromTags);

        return (Collection<T>) matchingTags;
    }

    /**
     * Finds and returns the first (including the nested tags) matching with the
     * given attribute name.
     *
     * @param attributeName
     *            the name of the attribute.
     * @param fromTags
     *            from which the findings to be done.
     * @return the first matching tag with the given attribute name and value.
     * @throws NullValueException
     *             if the {@code attributeName} or {@code fromTags} is null
     * @since 2.1.8
     * @author WFF
     */
    public static AbstractHtml findOneTagByAttributeName(
            final String attributeName, final AbstractHtml... fromTags)
            throws NullValueException {

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

                final AbstractAttribute attribute = child
                        .getAttributeByName(attributeName);

                if (attribute != null) {
                    matchingTag[0] = child;
                    return false;
                }

                return true;
            }
        }, true, fromTags);

        return matchingTag[0];
    }

    /**
     * Finds and returns the collection of tags (including the nested tags)
     * matching with the given attribute name.
     *
     * @param attributeName
     *            the name of the attribute.
     * @param fromTags
     *            from which the findings to be done.
     * @return the collection of tags matching with the given attribute.
     * @throws NullValueException
     *             if the {@code attributeName} or {@code fromTags} is null
     * @since 2.1.8
     * @author WFF
     */
    public static Collection<AbstractHtml> findTagsByAttributeName(
            final String attributeName, final AbstractHtml... fromTags)
            throws NullValueException {

        if (attributeName == null) {
            throw new NullValueException(
                    "The attributeName should not be null");
        }

        if (fromTags == null) {
            throw new NullValueException("The fromTags should not be null");
        }

        final Collection<AbstractHtml> matchingTags = new HashSet<AbstractHtml>();

        loopThroughAllNestedChildren(new NestedChild() {

            @Override
            public boolean eachChild(final AbstractHtml child) {

                final AbstractAttribute attribute = child
                        .getAttributeByName(attributeName);

                if (attribute != null) {
                    matchingTags.add(child);
                }

                return true;
            }
        }, true, fromTags);

        return matchingTags;
    }

    /**
     * Finds and returns the collection of tags (including the nested tags)
     * matching with the given attribute name.
     *
     * @param attributeName
     *            the name of the attribute.
     * @return the collection of tags matching with the given attribute.
     * @throws NullValueException
     *             if the {@code attributeName} is null
     * @since 2.1.8
     * @author WFF
     */
    public Collection<AbstractHtml> findTagsByAttributeName(
            final String attributeName) throws NullValueException {
        return findTagsByAttributeName(attributeName, rootTags);
    }

    /**
     * Finds and returns the collection of tags (including the nested tags)
     * matching with the given attribute name and value.
     *
     * @param attributeName
     *            the name of the attribute.
     * @param attributeValue
     *            the value of the attribute
     * @return the collection of tags matching with the given attribute name and
     *         value.
     * @throws NullValueException
     *             if the {@code attributeName} or {@code attributeValue} is
     *             null
     * @since 2.1.8
     * @author WFF
     */
    public Collection<AbstractHtml> findTagsByAttribute(
            final String attributeName, final String attributeValue)
            throws NullValueException {
        return findTagsByAttribute(attributeName, attributeValue, rootTags);
    }

    /**
     * Finds and returns the collection of tags (including the nested tags)
     * matching with the give tag name and value.
     *
     * @param tagName
     *            the name of the tag.
     * @return the collection of tags matching with the given tag name and
     *         value.
     * @throws NullValueException
     *             if the {@code tagName} or {@code fromTags} is null
     * @since 2.1.11
     * @author WFF
     */
    public Collection<AbstractHtml> findTagsByTagName(final String tagName)
            throws NullValueException {
        return findTagsByTagName(tagName, rootTags);
    }

    /**
     * Finds and returns the collection of attributes (including from nested
     * tags) of the tags matching with the give tag name.
     *
     * @param tagName
     *            the name of the tag.
     * @return the collection of attributes of the tags matching with the given
     *         tag name.
     * @throws NullValueException
     *             if the {@code tagName} or {@code fromTags} is null
     * @since 2.1.11
     * @author WFF
     */
    public Collection<AbstractAttribute> findAttributesByTagName(
            final String tagName) throws NullValueException {
        return findAttributesByTagName(tagName, rootTags);
    }

    /**
     * Finds and returns the first (including the nested tags) matching with the
     * given attribute name and value.
     *
     * @param attributeName
     *            the name of the attribute.
     * @param attributeValue
     *            the value of the attribute
     * @return the first matching tag with the given attribute name and value.
     * @throws NullValueException
     *             if the {@code attributeName} or {@code attributeValue} is
     *             null
     * @since 2.1.8
     * @author WFF
     */
    public AbstractHtml findOneTagByAttribute(final String attributeName,
            final String attributeValue) throws NullValueException {
        return findOneTagByAttribute(attributeName, attributeValue, rootTags);
    }

    /**
     * Finds and returns the first (including the nested tags) matching with the
     * given tag name.
     *
     * @param tagName
     *            the name of the tag.
     * @return the first matching tag with the given tag name.
     * @throws NullValueException
     *             if the {@code tagName is null
     * @since 2.1.11
     * @author WFF
     */
    public AbstractHtml findOneTagByTagName(final String tagName)
            throws NullValueException {
        return findOneTagByTagName(tagName, rootTags);
    }

    /**
     * Finds and returns the first matching (including from nested tags) tag
     * (which is assignable to the given tag class). <br>
     * <br>
     *
     * <pre>
     * <code>
     * Html html = new Html(null) {{
     *      new Head(this) {{
     *          new TitleTag(this){{
     *              new NoTag(this, "some title");
     *          }};
     *      }};
     *      new Body(this, new Id("one")) {{
     *          new Div(this);
     *      }};
     *  }};
     *
     *  TitleTag titleTag = TagRepository.findOneTagAssignableToTagClass(TitleTag.class, html);
     *
     *  System.out.println(titleTag.getTagName());
     *  System.out.println(titleTag.toHtmlString());
     *
     *  //prints
     *
     *  title
     *  <title>some title</title>
     *
     * </code>
     * </pre>
     *
     * @param tagClass
     *            the class of the tag.
     * @return the first matching tag which is assignable to the given tag
     *         class.
     * @throws NullValueException
     *             if the {@code tagClass} or {@code fromTags} is null
     * @since 2.1.11
     * @author WFF
     */
    public <T> T findOneTagAssignableToTag(final Class<T> tagClass)
            throws NullValueException {
        return findOneTagAssignableToTag(tagClass, rootTags);
    }

    /**
     * Finds and returns the all matching (including from nested tags) tags
     * (which is assignable to the given tag class). <br>
     * <br>
     *
     * <pre>
     * <code>
     * Html html = new Html(null) {{
     *      new Head(this) {{
     *          new TitleTag(this){{
     *              new NoTag(this, "some title");
     *          }};
     *      }};
     *      new Body(this, new Id("one")) {{
     *          new Div(this);
     *          new Div(this) {{
     *              new Div(this) {{
     *                  new Div(this);
     *              }};
     *          }};
     *          new Div(this);
     *      }};
     *  }};
     *
     *  Collection<Head> heads = TagRepository.findTagsAssignableToTag(Head.class, html);
     *  Collection<Div> divs = TagRepository.findTagsAssignableToTag(Div.class, html);
     *
     *  System.out.println(heads.size());
     *  System.out.println(divs.size());
     *
     *  //prints
     *
     *  1
     *  5
     *
     * </code>
     * </pre>
     *
     * @param tagClass
     *            the class of the tag.
     * @return the all matching tags which is assignable to the given tag class.
     * @throws NullValueException
     *             if the {@code tagClass} or {@code fromTags} is null
     * @throws InvalidTagException
     *             if the given tag class is NoTag.class
     * @since 2.1.11
     * @author WFF
     */
    public <T> Collection<T> findTagsAssignableToTag(final Class<T> tagClass)
            throws NullValueException, InvalidTagException {
        return findTagsAssignableToTag(tagClass, rootTags);
    }

    /**
     * Finds and returns the first (including the nested tags) matching with the
     * given attribute name.
     *
     * @param attributeName
     *            the name of the attribute.
     * @return the first matching tag with the given attribute name and value.
     * @throws NullValueException
     *             if the {@code attributeName} is null
     * @since 2.1.8
     * @author WFF
     */
    public AbstractHtml findOneTagByAttributeName(final String attributeName)
            throws NullValueException {
        return findOneTagByAttributeName(attributeName, rootTags);
    }

    /**
     * Finds tags by attribute instance.
     *
     * @param attribute
     * @return all tags which are consuming the given attribute instance. It
     *         returns the only tags consuming the given attribute object which
     *         are available in browserPage.
     * @throws NullValueException
     *             if the {@code attribute} is null
     * @since 2.1.8
     * @author WFF
     */
    public Collection<AbstractHtml> findTagsByAttribute(
            final AbstractAttribute attribute) throws NullValueException {

        if (attribute == null) {
            throw new NullValueException("attribute cannot be null");
        }

        final Collection<AbstractHtml> tags = new HashSet<AbstractHtml>();

        for (final AbstractHtml ownerTag : attribute.getOwnerTags()) {
            if (browserPage.contains(ownerTag)) {
                tags.add(ownerTag);
            }
        }

        return tags;
    }

    /**
     * Finds one tag by attribute instance.
     *
     * @param attribute
     * @return the first matching tag consuming the given attribute instance.
     *         There must be a consuming tag which is available in the
     *         browserPage instance otherwise returns null.
     * @throws NullValueException
     *             if the {@code attribute } is null
     * @since 2.1.8
     * @author WFF
     */
    public AbstractHtml findOneTagByAttribute(
            final AbstractAttribute attribute) {

        if (attribute == null) {
            throw new NullValueException("attribute cannot be null");
        }

        for (final AbstractHtml ownerTag : attribute.getOwnerTags()) {
            if (browserPage.contains(ownerTag)) {
                return ownerTag;
            }
        }

        return null;
    }

    /**
     * Inserts or replaces (if already exists) the key bmObject pair in the
     * wffObjects property of tag. The conventional JavaScript object of
     * {@code WffBMObject} will be set to the {@code wffObjects} property of the
     * given tag at client side.
     *
     * @param tag
     *            the tag object on which the given bmObject to be set.
     * @param key
     *            key to set in wffObjects property of the tag.
     * @param bmObject
     * @throws InvalidTagException
     *             if the given instance is of NoTag / Blank
     * @throws NullValueException
     *             if tag, key or bmObject is null
     * @since 2.1.8
     * @author WFF
     */
    public void upsert(final AbstractHtml tag, final String key,
            final WffBMObject bmObject)
            throws InvalidTagException, NullValueException {
        if (tag == null) {
            throw new NullValueException("tag cannot be null");
        }
        if (tag instanceof NoTag) {
            throw new InvalidTagException(
                    "NoTag and Blank tag are not allowed to use");
        }
        if (key == null) {
            throw new NullValueException("key cannot be null");
        }
        if (bmObject == null) {
            throw new NullValueException("bmObject cannot be null");
        }

        if (!browserPage.contains(tag)) {
            throw new InvalidTagException(
                    "The given tag is not available in the corresponding browserPage instance.");
        }

        AbstractHtmlRepository.addWffData(tag, key, bmObject);
    }

    /**
     * Inserts or replaces (if already exists) the key bmArray pair in the
     * wffObjects property of tag. The conventional JavaScript array of
     * {@code WffBMArray} will be set to the {@code wffObjects} property of the
     * given tag at client side.
     *
     * @param tag
     *            the tag object on which the given bmArray to be set.
     * @param key
     *            key to set in wffObjects property of the tag.
     * @param bmArray
     * @throws InvalidTagException
     *             if the given instance is of NoTag / Blank
     * @throws NullValueException
     *             if tag, key or bmArray is null
     * @since 2.1.8
     * @author WFF
     */
    public void upsert(final AbstractHtml tag, final String key,
            final WffBMArray bmArray)
            throws InvalidTagException, NullValueException {

        if (tag == null) {
            throw new NullValueException("tag cannot be null");
        }
        if (tag instanceof NoTag) {
            throw new InvalidTagException(
                    "NoTag and Blank tag are not allowed to use");
        }
        if (key == null) {
            throw new NullValueException("key cannot be null");
        }
        if (bmArray == null) {
            throw new NullValueException("bmArray cannot be null");
        }
        if (!browserPage.contains(tag)) {
            throw new InvalidTagException(
                    "The given tag is not available in the corresponding browserPage instance.");
        }
        AbstractHtmlRepository.addWffData(tag, key, bmArray);
    }

    /**
     * Deletes the key-WffBMObject/WffBMArray pair from the {@code wffObjects}
     * property of tag.
     *
     * @param tag
     * @param key
     * @throws InvalidTagException
     *             if the given instance is of NoTag / Blank
     * @throws NullValueException
     *             if tag or key is null
     * @since 2.1.8
     * @author WFF
     */
    public void delete(final AbstractHtml tag, final String key)
            throws InvalidTagException, NullValueException {

        if (tag == null) {
            throw new NullValueException("tag cannot be null");
        }
        if (tag instanceof NoTag) {
            throw new InvalidTagException(
                    "NoTag and Blank tag are not allowed to use");
        }
        if (key == null) {
            throw new NullValueException("key cannot be null");
        }
        if (!browserPage.contains(tag)) {
            throw new InvalidTagException(
                    "The given tag is not available in the corresponding browserPage instance.");
        }
        AbstractHtmlRepository.removeWffData(tag, key);
    }

    /**
     * Finds all tags.
     *
     * @return the collection of all tags
     * @since 2.1.8
     * @author WFF
     */
    public Collection<AbstractHtml> findAllTags() {
        return findAllTags(rootTags);
    }

    /**
     * Finds all tags including the nested tags from the given tags.
     *
     * @param fromTags
     *            to find all tags from these tags
     * @return all tags including the nested tags from the given tags.
     * @throws NullValueException
     *             if {@code fromTags} is null
     * @since 2.1.9
     * @author WFF
     */
    public static Collection<AbstractHtml> findAllTags(
            final AbstractHtml... fromTags) throws NullValueException {

        if (fromTags == null) {
            throw new NullValueException("fromTags cannot be null");
        }

        final Collection<AbstractHtml> allTags = new HashSet<AbstractHtml>();

        loopThroughAllNestedChildren(new NestedChild() {

            @Override
            public boolean eachChild(final AbstractHtml child) {
                allTags.add(child);
                return true;
            }
        }, true, fromTags);

        return allTags;
    }

    /**
     * Finds all attributes.
     *
     * @return the collection of all attributes
     * @since 2.1.8
     * @author WFF
     */
    public Collection<AbstractAttribute> findAllAttributes() {
        return findAllAttributes(rootTags);
    }

    /**
     * Finds all attributes from the given tags
     *
     * @param fromTags
     *            the tags to find the attributes from.
     * @return the all attributes from the given tags including the nested tags.
     * @throws NullValueException
     *             if {@code fromTags} is null
     * @since 2.1.9
     * @author WFF
     */
    public static Collection<AbstractAttribute> findAllAttributes(
            final AbstractHtml... fromTags) throws NullValueException {

        if (fromTags == null) {
            throw new NullValueException("fromTags cannot be null");
        }

        final Collection<AbstractAttribute> allAttributes = new HashSet<AbstractAttribute>();

        loopThroughAllNestedChildren(new NestedChild() {

            @Override
            public boolean eachChild(final AbstractHtml child) {

                final Collection<AbstractAttribute> attributes = child
                        .getAttributes();
                if (attributes != null) {
                    allAttributes.addAll(attributes);
                }
                return true;
            }
        }, true, fromTags);

        return allAttributes;
    }

    /**
     * Checks the existence of a tag instance.
     *
     * @param tag
     * @return true if the given tag instance exists anywhere in the browser
     *         page.
     * @throws NullValueException
     *             if the tag is null
     * @since 2.1.8
     * @author WFF
     */
    public boolean exists(final AbstractHtml tag) throws NullValueException {
        if (tag == null) {
            throw new NullValueException("tag cannot be null");
        }
        return browserPage.contains(tag);
    }

    /**
     * Checks the existence of an attribute instance.
     *
     * @param attribute
     * @return true if the given attribute instance exists anywhere in the
     *         browser page.
     * @throws NullValueException
     *             if the {@code attribute} is null
     * @since 2.1.8
     * @author WFF
     */
    public boolean exists(final AbstractAttribute attribute)
            throws NullValueException {

        if (attribute == null) {
            throw new NullValueException("attribute cannot be null");
        }

        for (final AbstractHtml ownerTag : attribute.getOwnerTags()) {
            if (browserPage.contains(ownerTag)) {
                return true;
            }
        }

        return false;
    }

}
