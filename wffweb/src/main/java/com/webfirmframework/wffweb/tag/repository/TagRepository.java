/*
 * Copyright 2014-2022 Web Firm Framework
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
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.webfirmframework.wffweb.InvalidTagException;
import com.webfirmframework.wffweb.NullValueException;
import com.webfirmframework.wffweb.WffSecurityException;
import com.webfirmframework.wffweb.internal.constants.IndexedClassType;
import com.webfirmframework.wffweb.internal.security.object.SecurityObject;
import com.webfirmframework.wffweb.server.page.BrowserPage;
import com.webfirmframework.wffweb.server.page.action.BrowserPageAction;
import com.webfirmframework.wffweb.tag.html.AbstractHtml;
import com.webfirmframework.wffweb.tag.html.AbstractHtmlRepository;
import com.webfirmframework.wffweb.tag.html.Body;
import com.webfirmframework.wffweb.tag.html.TitleTag;
import com.webfirmframework.wffweb.tag.html.attribute.AttributeNameConstants;
import com.webfirmframework.wffweb.tag.html.attribute.core.AbstractAttribute;
import com.webfirmframework.wffweb.tag.html.metainfo.Head;
import com.webfirmframework.wffweb.tag.htmlwff.NoTag;
import com.webfirmframework.wffweb.wffbm.data.WffBMArray;
import com.webfirmframework.wffweb.wffbm.data.WffBMData;
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
public class TagRepository extends AbstractHtmlRepository implements Serializable {

    private static final long serialVersionUID = 1L;

    private final BrowserPage browserPage;

    private final AbstractHtml[] rootTags;

    private final Map<String, AbstractHtml> tagByWffId;

    private volatile Reference<TitleTag> titleTagRef;

    private volatile Reference<Body> bodyTagRef;

    private volatile Reference<Head> headTagRef;

    /**
     * This constructor is only for internal use. To get an object of
     * {@code TagRepository} use {@code BrowserPage#getTagRepository()} method.
     *
     * @param browserPage the instance of {@code BrowserPage}
     * @param tagByWffId  map containing wff id and its corresponding
     *                    {@code AbstractHtml}.
     * @param rootTags    the rootTags in the browserPage instance.
     * @since 2.1.16
     * @author WFF
     */
    public TagRepository(@SuppressWarnings("exports") final SecurityObject accessObject, final BrowserPage browserPage,
            final Map<String, AbstractHtml> tagByWffId, final AbstractHtml... rootTags) {

        if (accessObject == null || !((IndexedClassType.ABSTRACT_HTML.equals(accessObject.forClassType()))
                || (IndexedClassType.BROWSER_PAGE.equals(accessObject.forClassType())))) {
            throw new WffSecurityException("Not allowed to consume this constructor. This method is for internal use.");
        }

        this.tagByWffId = tagByWffId;
        this.browserPage = browserPage;
        this.rootTags = rootTags;
    }

    /**
     * Finds and returns the first tag matching with the given id.
     *
     * @param id       the value of id attribute.
     * @param fromTags from the given tags and its nested children the finding to be
     *                 done.
     * @return the first found tag with the given id
     * @throws NullValueException if the {@code id} or {@code fromTags} is null
     * @since 2.1.8
     * @author WFF
     */
    public static AbstractHtml findTagById(final String id, final AbstractHtml... fromTags) throws NullValueException {
        return findTagById(false, id, fromTags);
    }

    /**
     * Finds and returns the first tag matching with the given id.
     *
     * @param parallel true to internally use parallel stream. If true it will split
     *                 the finding task to different batches and will execute the
     *                 batches in different threads in parallel consuming all CPUs.
     *                 It will perform faster in finding from extremely large number
     *                 of tags but at the same time it will less efficient in
     *                 finding from small number of tags.
     * @param id       the value of id attribute.
     * @param fromTags from the given tags and its nested children the finding to be
     *                 done.
     * @return the first found tag with the given id
     * @throws NullValueException if the {@code id} or {@code fromTags} is null
     * @since 3.0.0
     * @author WFF
     */
    public static AbstractHtml findTagById(final boolean parallel, final String id, final AbstractHtml... fromTags)
            throws NullValueException {

        if (id == null) {
            throw new NullValueException("The id should not be null");
        }

        if (fromTags == null) {
            throw new NullValueException("The fromTags should not be null");
        }

        final Collection<Lock> locks = lockAndGetReadLocks(fromTags);

        try {
            final Stream<AbstractHtml> stream = getAllNestedChildrenIncludingParent(parallel, fromTags);

            final Optional<AbstractHtml> any = stream.filter(child -> {

                final AbstractAttribute idAttr = getAttributeByNameLockless(child, AttributeNameConstants.ID);

                return idAttr != null && id.equals(idAttr.getAttributeValue());

            }).findAny();

            return any.orElse(null);
        } finally {
            for (final Lock lock : locks) {
                lock.unlock();
            }
        }
    }

    /**
     * Finds and returns the first tag matching with the given id.
     *
     * @param id the value of id attribute.
     * @return the first found tag with the given id
     *
     * @throws NullValueException if the {@code id} is null
     * @since 2.1.8
     * @author WFF
     */
    public AbstractHtml findTagById(final String id) throws NullValueException {
        return findTagById(false, id);
    }

    /**
     * Finds and returns the first tag matching with the given id.
     *
     * @param parallel true to internally use parallel stream. If true it will split
     *                 the finding task to different batches and will execute the
     *                 batches in different threads in parallel consuming all CPUs.
     *                 It will perform faster in finding from extremely large number
     *                 of tags but at the same time it will less efficient in
     *                 finding from small number of tags.
     * @param id       the value of id attribute.
     * @return the first found tag with the given id
     *
     * @throws NullValueException if the {@code id} is null
     * @since 3.0.0
     * @author WFF
     */
    public AbstractHtml findTagById(final boolean parallel, final String id) throws NullValueException {
        return findOneTagByAttribute(parallel, AttributeNameConstants.ID, id);
    }

    /**
     * Finds and returns the collection of tags (including the nested tags) matching
     * with the given attribute name and value.
     *
     * @param attributeName  the name of the attribute.
     * @param attributeValue the value of the attribute
     * @param fromTags       from which the findings to be done.
     * @return the collection of tags matching with the given attribute name and
     *         value.
     * @throws NullValueException if the {@code attributeName},
     *                            {@code attributeValue} or {@code fromTags} is null
     * @since 2.1.8
     * @author WFF
     */
    public static Collection<AbstractHtml> findTagsByAttribute(final String attributeName, final String attributeValue,
            final AbstractHtml... fromTags) throws NullValueException {

        return findTagsByAttribute(false, attributeName, attributeValue, fromTags);
    }

    /**
     * Finds and returns the collection of tags (including the nested tags) matching
     * with the given attribute name and value.
     *
     * @param parallel       true to internally use parallel stream. If true it will
     *                       split the finding task to different batches and will
     *                       execute the batches in different threads in parallel
     *                       consuming all CPUs. It will perform faster in finding
     *                       from extremely large number of tags but at the same
     *                       time it will less efficient in finding from small
     *                       number of tags.
     * @param attributeName  the name of the attribute.
     * @param attributeValue the value of the attribute
     * @param fromTags       from which the findings to be done.
     * @return the collection of tags matching with the given attribute name and
     *         value.
     * @throws NullValueException if the {@code attributeName},
     *                            {@code attributeValue} or {@code fromTags} is null
     * @since 3.0.0
     * @author WFF
     */
    public static Collection<AbstractHtml> findTagsByAttribute(final boolean parallel, final String attributeName,
            final String attributeValue, final AbstractHtml... fromTags) throws NullValueException {

        if (attributeName == null) {
            throw new NullValueException("The attributeName should not be null");
        }
        if (attributeValue == null) {
            throw new NullValueException("The attributeValue should not be null");
        }

        if (fromTags == null) {
            throw new NullValueException("The fromTags should not be null");
        }

        final Collection<Lock> locks = lockAndGetReadLocks(fromTags);

        try {
            return getAllNestedChildrenIncludingParent(parallel, fromTags).filter(child -> {

                final AbstractAttribute attribute = getAttributeByNameLockless(child, attributeName);

                return attribute != null && attributeValue.equals(attribute.getAttributeValue());
            }).collect(Collectors.toSet());
        } finally {
            for (final Lock lock : locks) {
                lock.unlock();
            }
        }
    }

    /**
     * Finds and returns the collection of tags (including the nested tags) matching
     * with the give tag name.
     *
     * @param tagName  the name of the tag.
     * @param fromTags from which the findings to be done.
     * @return the collection of tags matching with the given tag name .
     * @throws NullValueException if the {@code tagName} or {@code fromTags} is null
     * @since 2.1.11
     * @author WFF
     */
    public static Collection<AbstractHtml> findTagsByTagName(final String tagName, final AbstractHtml... fromTags)
            throws NullValueException {

        return findTagsByTagName(false, tagName, fromTags);
    }

    /**
     * Finds and returns the collection of tags (including the nested tags) matching
     * with the give tag name.
     *
     * @param parallel true to internally use parallel stream. If true it will split
     *                 the finding task to different batches and will execute the
     *                 batches in different threads in parallel consuming all CPUs.
     *                 It will perform faster in finding from extremely large number
     *                 of tags but at the same time it will less efficient in
     *                 finding from small number of tags.
     * @param tagName  the name of the tag.
     * @param fromTags from which the findings to be done.
     * @return the collection of tags matching with the given tag name .
     * @throws NullValueException if the {@code tagName} or {@code fromTags} is null
     * @since 3.0.0
     * @author WFF
     */
    public static Collection<AbstractHtml> findTagsByTagName(final boolean parallel, final String tagName,
            final AbstractHtml... fromTags) throws NullValueException {

        if (tagName == null) {
            throw new NullValueException("The tagName should not be null");
        }

        if (fromTags == null) {
            throw new NullValueException("The fromTags should not be null");
        }

        final Collection<Lock> locks = lockAndGetReadLocks(fromTags);

        try {
            return getAllNestedChildrenIncludingParent(parallel, fromTags)
                    .filter(child -> tagName.equals(child.getTagName())).collect(Collectors.toSet());
        } finally {
            for (final Lock lock : locks) {
                lock.unlock();
            }
        }
    }

    /**
     * Finds and returns the collection of attributes (including from nested tags)
     * of the tags matching with the give tag name.
     *
     * @param tagName  the name of the tag.
     * @param fromTags from which the findings to be done.
     * @return the collection of attributes of the tags matching with the given tag
     *         name.
     * @throws NullValueException if the {@code tagName} or {@code fromTags} is null
     * @since 2.1.11
     * @author WFF
     */
    public static Collection<AbstractAttribute> findAttributesByTagName(final String tagName,
            final AbstractHtml... fromTags) throws NullValueException {
        return findAttributesByTagName(false, tagName, fromTags);
    }

    /**
     * Finds and returns the collection of attributes (including from nested tags)
     * of the tags by the given filter.
     *
     *
     *
     * @param filter   the filter lambda expression containing return true to
     *                 include and false to exclude.
     * @param fromTags from which the findings to be done.
     * @return the collection of attributes of the tags by the given filter.
     * @throws NullValueException if the {@code filter} or {@code fromTags} is null
     * @since 3.0.0
     * @author WFF
     */
    public static Collection<AbstractAttribute> findAttributesByAttributeFilter(
            final Predicate<? super AbstractAttribute> filter, final AbstractHtml... fromTags)
            throws NullValueException {

        return findAttributesByAttributeFilter(false, filter, fromTags);
    }

    /**
     * Finds and returns the collection of attributes (including from nested tags)
     * of the tags by the given filter.
     *
     * @param parallel true to internally use parallel stream. If true it will split
     *                 the finding task to different batches and will execute the
     *                 batches in different threads in parallel consuming all CPUs.
     *                 It will perform faster in finding from extremely large number
     *                 of tags but at the same time it will less efficient in
     *                 finding from small number of tags.
     *
     * @param filter   the filter lambda expression containing return true to
     *                 include and false to exclude.
     * @param fromTags from which the findings to be done.
     * @return the collection of attributes of the tags by the given filter.
     * @throws NullValueException if the {@code filter} or {@code fromTags} is null
     * @since 3.0.0
     * @author WFF
     */
    public static Collection<AbstractAttribute> findAttributesByAttributeFilter(final boolean parallel,
            final Predicate<? super AbstractAttribute> filter, final AbstractHtml... fromTags)
            throws NullValueException {

        if (filter == null) {
            throw new NullValueException("The tagName should not be null");
        }

        if (fromTags == null) {
            throw new NullValueException("The fromTags should not be null");
        }

        final Collection<Lock> locks = lockAndGetReadLocks(fromTags);

        try {
            return getAllNestedChildrenIncludingParent(parallel, fromTags)
                    .filter(child -> getAttributesLockless(child) != null)
                    .map(AbstractHtmlRepository::getAttributesLockless).flatMap(Collection::stream).filter(filter)
                    .collect(Collectors.toSet());
        } finally {
            for (final Lock lock : locks) {
                lock.unlock();
            }
        }
    }

    /**
     * Finds and returns the collection of attributes (including from nested tags)
     * of the tags matching with the give tag name.
     *
     * @param parallel true to internally use parallel stream. If true it will split
     *                 the finding task to different batches and will execute the
     *                 batches in different threads in parallel consuming all CPUs.
     *                 It will perform faster in finding from extremely large number
     *                 of tags but at the same time it will less efficient in
     *                 finding from small number of tags.
     *
     * @param tagName  the name of the tag.
     * @param fromTags from which the findings to be done.
     * @return the collection of attributes of the tags matching with the given tag
     *         name.
     * @throws NullValueException if the {@code tagName} or {@code fromTags} is null
     * @since 3.0.0
     * @author WFF
     */
    public static Collection<AbstractAttribute> findAttributesByTagName(final boolean parallel, final String tagName,
            final AbstractHtml... fromTags) throws NullValueException {

        if (tagName == null) {
            throw new NullValueException("The tagName should not be null");
        }

        if (fromTags == null) {
            throw new NullValueException("The fromTags should not be null");
        }

        final Collection<Lock> locks = lockAndGetReadLocks(fromTags);

        try {
            return getAllNestedChildrenIncludingParent(parallel, fromTags)
                    .filter(child -> tagName.equals(child.getTagName()) && getAttributesLockless(child) != null)
                    .map(AbstractHtmlRepository::getAttributesLockless).flatMap(Collection::stream)
                    .collect(Collectors.toSet());
        } finally {
            for (final Lock lock : locks) {
                lock.unlock();
            }
        }

    }

    /**
     * Finds and returns the collection of attributes (including from nested tags)
     * of the tags by the given filter
     *
     *
     *
     * @param filter the filter lambda expression containing return true to include
     *               and false to exclude.
     *
     * @return the collection of attributes of the tags by the given tag filter
     * @throws NullValueException if the {@code filter} or {@code fromTags} is null
     * @since 3.0.0
     * @author WFF
     */
    public static Collection<AbstractAttribute> findAttributesByTagFilter(final Predicate<? super AbstractHtml> filter,
            final AbstractHtml... fromTags) throws NullValueException {
        return findAttributesByTagFilter(false, filter, fromTags);
    }

    /**
     * Finds and returns the collection of attributes (including from nested tags)
     * of the tags by the given filter
     *
     * @param parallel true to internally use parallel stream. If true it will split
     *                 the finding task to different batches and will execute the
     *                 batches in different threads in parallel consuming all CPUs.
     *                 It will perform faster in finding from extremely large number
     *                 of tags but at the same time it will less efficient in
     *                 finding from small number of tags.
     *
     * @param filter   the filter lambda expression containing return true to
     *                 include and false to exclude.
     *
     * @return the collection of attributes of the tags by the given tag filter
     * @throws NullValueException if the {@code filter} or {@code fromTags} is null
     * @since 3.0.0
     * @author WFF
     */
    public static Collection<AbstractAttribute> findAttributesByTagFilter(final boolean parallel,
            final Predicate<? super AbstractHtml> filter, final AbstractHtml... fromTags) throws NullValueException {

        if (filter == null) {
            throw new NullValueException("The filter should not be null");
        }

        if (fromTags == null) {
            throw new NullValueException("The fromTags should not be null");
        }

        final Collection<Lock> locks = lockAndGetReadLocks(fromTags);

        try {
            return getAllNestedChildrenIncludingParent(parallel, fromTags)
                    .filter(child -> getAttributesLockless(child) != null).filter(filter)
                    .map(AbstractHtmlRepository::getAttributesLockless).flatMap(Collection::stream)
                    .collect(Collectors.toSet());
        } finally {
            for (final Lock lock : locks) {
                lock.unlock();
            }
        }
    }

    /**
     * Finds and returns the first (including the nested tags) matching with the
     * given attribute name and value.
     *
     * @param attributeName  the name of the attribute.
     * @param attributeValue the value of the attribute
     * @param fromTags       from which the findings to be done.
     * @return the first matching tag with the given attribute name and value.
     * @throws NullValueException if the {@code attributeName},
     *                            {@code attributeValue} or {@code fromTags} is null
     * @since 2.1.8
     * @author WFF
     */
    public static AbstractHtml findOneTagByAttribute(final String attributeName, final String attributeValue,
            final AbstractHtml... fromTags) throws NullValueException {
        return findOneTagByAttribute(false, attributeName, attributeValue, fromTags);
    }

    /**
     * Finds and returns the first (including the nested tags) matching with the
     * given attribute name and value.
     *
     * @param attributeName  the name of the attribute.
     * @param attributeValue the value of the attribute
     * @param fromTags       from which the findings to be done.
     * @return the first matching tag with the given attribute name and value.
     * @throws NullValueException if the {@code attributeName},
     *                            {@code attributeValue} or {@code fromTags} is null
     * @since 3.0.0
     * @author WFF
     */
    public static AbstractHtml findOneTagByAttribute(final boolean parallel, final String attributeName,
            final String attributeValue, final AbstractHtml... fromTags) throws NullValueException {

        if (attributeName == null) {
            throw new NullValueException("The attributeName should not be null");
        }
        if (attributeValue == null) {
            throw new NullValueException("The attributeValue should not be null");
        }

        if (fromTags == null) {
            throw new NullValueException("The fromTags should not be null");
        }

        final Collection<Lock> locks = lockAndGetReadLocks(fromTags);

        try {
            final Stream<AbstractHtml> stream = getAllNestedChildrenIncludingParent(parallel, fromTags);

            final Optional<AbstractHtml> any = stream.filter(child -> {

                final AbstractAttribute attr = getAttributeByNameLockless(child, attributeName);

                return attr != null && attributeValue.equals(attr.getAttributeValue());

            }).findAny();

            return any.orElse(null);
        } finally {
            for (final Lock lock : locks) {
                lock.unlock();
            }
        }
    }

    /**
     * Finds and returns the first (including the nested tags) matching with the
     * given tag name.
     *
     * @param tagName  the name of the tag.
     * @param fromTags from which the findings to be done.
     * @return the first matching tag with the given tag name.
     * @throws NullValueException if the {@code tagName} or {@code fromTags} is null
     * @since 2.1.11
     * @author WFF
     */
    public static AbstractHtml findOneTagByTagName(final String tagName, final AbstractHtml... fromTags)
            throws NullValueException {
        return findOneTagByTagName(false, tagName, fromTags);
    }

    /**
     * Finds and returns the first (including the nested tags) matching with the
     * given tag name.
     *
     * @param parallel true to internally use parallel stream. If true it will split
     *                 the finding task to different batches and will execute the
     *                 batches in different threads in parallel consuming all CPUs.
     *                 It will perform faster in finding from extremely large number
     *                 of tags but at the same time it will less efficient in
     *                 finding from small number of tags.
     * @param tagName  the name of the tag.
     * @param fromTags from which the findings to be done.
     * @return the first matching tag with the given tag name.
     * @throws NullValueException if the {@code tagName} or {@code fromTags} is null
     * @since 3.0.0
     * @author WFF
     */
    public static AbstractHtml findOneTagByTagName(final boolean parallel, final String tagName,
            final AbstractHtml... fromTags) throws NullValueException {

        if (tagName == null) {
            throw new NullValueException("The tagName should not be null");
        }

        if (fromTags == null) {
            throw new NullValueException("The fromTags should not be null");
        }

        final Collection<Lock> locks = lockAndGetReadLocks(fromTags);

        try {
            final Optional<AbstractHtml> any = getAllNestedChildrenIncludingParent(parallel, fromTags)
                    .filter(tag -> tagName.equals(tag.getTagName())).findAny();

            return any.orElse(null);

        } finally {
            for (final Lock lock : locks) {
                lock.unlock();
            }
        }
    }

    /**
     * Finds and returns the first matching (including from nested tags) tag (which
     * is assignable to the given tag class). <br>
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
     *  &lt;title&gt;some title&lt;/title&gt;
     *
     * </code>
     * </pre>
     *
     * @param tagClass the class of the tag.
     * @param fromTags from which the findings to be done.
     * @return the first matching tag which is assignable to the given tag class.
     * @throws NullValueException  if the {@code tagClass} or {@code fromTags} is
     *                             null
     * @throws InvalidTagException if the given tag class is NoTag.class
     * @since 2.1.11
     * @author WFF
     */
    public static <T extends AbstractHtml> T findOneTagAssignableToTag(final Class<T> tagClass,
            final AbstractHtml... fromTags) throws NullValueException, InvalidTagException {
        return findOneTagAssignableToTag(false, tagClass, fromTags);
    }

    /**
     * Finds and returns the first matching (including from nested tags) tag (which
     * is assignable to the given tag class). <br>
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
     *  &lt;title&gt;some title&lt;/title&gt;
     *
     * </code>
     * </pre>
     *
     * @param parallel true to internally use parallel stream. If true it will split
     *                 the finding task to different batches and will execute the
     *                 batches in different threads in parallel consuming all CPUs.
     *                 It will perform faster in finding from extremely large number
     *                 of tags but at the same time it will less efficient in
     *                 finding from small number of tags.
     *
     * @param tagClass the class of the tag.
     * @param fromTags from which the findings to be done.
     * @return the first matching tag which is assignable to the given tag class.
     * @throws NullValueException  if the {@code tagClass} or {@code fromTags} is
     *                             null
     * @throws InvalidTagException if the given tag class is NoTag.class
     * @since 3.0.0
     * @author WFF
     */
    @SuppressWarnings("unchecked")
    public static <T extends AbstractHtml> T findOneTagAssignableToTag(final boolean parallel, final Class<T> tagClass,
            final AbstractHtml... fromTags) throws NullValueException, InvalidTagException {

        if (tagClass == null) {
            throw new NullValueException("The tagClass should not be null");
        }

        if (NoTag.class.isAssignableFrom(tagClass)) {
            throw new InvalidTagException(
                    "classes like NoTag.class cannot be used to find tags as it's not a logical tag in behaviour.");
        }

        if (fromTags == null) {
            throw new NullValueException("The fromTags should not be null");
        }

        final Collection<Lock> locks = lockAndGetReadLocks(fromTags);

        try {
            final Optional<AbstractHtml> any = getAllNestedChildrenIncludingParent(parallel, fromTags)
                    .filter(child -> tagClass.isAssignableFrom(child.getClass())
                            && !NoTag.class.isAssignableFrom(child.getClass()))
                    .findAny();

            return (T) any.orElse(null);

        } finally {
            for (final Lock lock : locks) {
                lock.unlock();
            }
        }
    }

    /**
     * Finds and returns the all matching (including from nested tags) tags (which
     * is assignable to the given tag class). <br>
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
     *  Collection&lt;Head&gt; heads = TagRepository.findTagsAssignableToTag(Head.class, html);
     *  Collection&lt;Div&gt; divs = TagRepository.findTagsAssignableToTag(Div.class, html);
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
     * @param tagClass the class of the tag.
     * @param fromTags from which the findings to be done.
     * @return the all matching tags which is assignable to the given tag class.
     * @throws NullValueException  if the {@code tagClass} or {@code fromTags} is
     *                             null
     * @throws InvalidTagException if the given tag class is NoTag.class
     * @since 2.1.11
     * @author WFF
     */
    public static <T extends AbstractHtml> Collection<T> findTagsAssignableToTag(final Class<T> tagClass,
            final AbstractHtml... fromTags) throws NullValueException, InvalidTagException {
        return findTagsAssignableToTag(false, tagClass, fromTags);
    }

    /**
     * Finds and returns the all matching (including from nested tags) tags (which
     * is assignable to the given tag class). <br>
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
     *  Collection&lt;Head&gt; heads = TagRepository.findTagsAssignableToTag(Head.class, html);
     *  Collection&lt;Div&gt; divs = TagRepository.findTagsAssignableToTag(Div.class, html);
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
     * @param parallel true to internally use parallel stream. If true it will split
     *                 the finding task to different batches and will execute the
     *                 batches in different threads in parallel consuming all CPUs.
     *                 It will perform faster in finding from extremely large number
     *                 of tags but at the same time it will less efficient in
     *                 finding from small number of tags.
     *
     * @param tagClass the class of the tag.
     * @param fromTags from which the findings to be done.
     * @return the all matching tags which is assignable to the given tag class.
     * @throws NullValueException  if the {@code tagClass} or {@code fromTags} is
     *                             null
     * @throws InvalidTagException if the given tag class is NoTag.class
     * @since 3.0.0
     * @author WFF
     */
    @SuppressWarnings("unchecked")
    public static <T extends AbstractHtml> Collection<T> findTagsAssignableToTag(final boolean parallel,
            final Class<T> tagClass, final AbstractHtml... fromTags) throws NullValueException, InvalidTagException {

        if (tagClass == null) {
            throw new NullValueException("The tagClass should not be null");
        }

        if (NoTag.class.isAssignableFrom(tagClass)) {
            throw new InvalidTagException(
                    "classes like NoTag.class cannot be used to find tags as it's not a logical tag in behaviour.");
        }

        if (fromTags == null) {
            throw new NullValueException("The fromTags should not be null");
        }

        final Collection<Lock> locks = lockAndGetReadLocks(fromTags);

        try {
            return (Collection<T>) getAllNestedChildrenIncludingParent(parallel, fromTags).filter(
                    tag -> tagClass.isAssignableFrom(tag.getClass()) && !NoTag.class.isAssignableFrom(tag.getClass()))
                    .collect(Collectors.toSet());
        } finally {
            for (final Lock lock : locks) {
                lock.unlock();
            }
        }
    }

    /**
     * Finds and returns the first (including the nested tags) matching with the
     * given attribute name.
     *
     * @param attributeName the name of the attribute.
     * @param fromTags      from which the findings to be done.
     * @return the first matching tag with the given attribute name and value.
     * @throws NullValueException if the {@code attributeName} or {@code fromTags}
     *                            is null
     * @since 2.1.8
     * @author WFF
     */
    public static AbstractHtml findOneTagByAttributeName(final String attributeName, final AbstractHtml... fromTags)
            throws NullValueException {
        return findOneTagByAttributeName(false, attributeName, fromTags);
    }

    /**
     * Finds and returns the first (including the nested tags) matching with the
     * given attribute name.
     *
     * @param parallel      true to internally use parallel stream. If true it will
     *                      split the finding task to different batches and will
     *                      execute the batches in different threads in parallel
     *                      consuming all CPUs. It will perform faster in finding
     *                      from extremely large number of tags but at the same time
     *                      it will less efficient in finding from small number of
     *                      tags.
     * @param attributeName the name of the attribute.
     * @param fromTags      from which the findings to be done.
     * @return the first matching tag with the given attribute name and value.
     * @throws NullValueException if the {@code attributeName} or {@code fromTags}
     *                            is null
     * @since 3.0.0
     * @author WFF
     */
    public static AbstractHtml findOneTagByAttributeName(final boolean parallel, final String attributeName,
            final AbstractHtml... fromTags) throws NullValueException {

        if (attributeName == null) {
            throw new NullValueException("The attributeName should not be null");
        }

        if (fromTags == null) {
            throw new NullValueException("The fromTags should not be null");
        }

        final Collection<Lock> locks = lockAndGetReadLocks(fromTags);

        try {
            final Optional<AbstractHtml> any = getAllNestedChildrenIncludingParent(parallel, fromTags)
                    .filter(child -> getAttributeByNameLockless(child, attributeName) != null).findAny();

            return any.orElse(null);
        } finally {
            for (final Lock lock : locks) {
                lock.unlock();
            }
        }
    }

    /**
     * Finds and returns the collection of tags (including the nested tags) matching
     * with the given attribute name.
     *
     * @param attributeName the name of the attribute.
     * @param fromTags      from which the findings to be done.
     * @return the collection of tags matching with the given attribute.
     * @throws NullValueException if the {@code attributeName} or {@code fromTags}
     *                            is null
     * @since 2.1.8
     * @author WFF
     */
    public static Collection<AbstractHtml> findTagsByAttributeName(final String attributeName,
            final AbstractHtml... fromTags) throws NullValueException {
        return findTagsByAttributeName(false, attributeName, fromTags);
    }

    /**
     * Finds and returns the collection of tags (including the nested tags) matching
     * with the given attribute name.
     *
     * @param parallel      true to internally use parallel stream. If true it will
     *                      split the finding task to different batches and will
     *                      execute the batches in different threads in parallel
     *                      consuming all CPUs. It will perform faster in finding
     *                      from extremely large number of tags but at the same time
     *                      it will less efficient in finding from small number of
     *                      tags.
     *
     * @param attributeName the name of the attribute.
     * @param fromTags      from which the findings to be done.
     * @return the collection of tags matching with the given attribute.
     * @throws NullValueException if the {@code attributeName} or {@code fromTags}
     *                            is null
     * @since 3.0.0
     * @author WFF
     */
    public static Collection<AbstractHtml> findTagsByAttributeName(final boolean parallel, final String attributeName,
            final AbstractHtml... fromTags) throws NullValueException {

        if (attributeName == null) {
            throw new NullValueException("The attributeName should not be null");
        }

        if (fromTags == null) {
            throw new NullValueException("The fromTags should not be null");
        }

        final Collection<Lock> locks = lockAndGetReadLocks(fromTags);

        try {
            return getAllNestedChildrenIncludingParent(parallel, fromTags)
                    .filter(child -> getAttributeByNameLockless(child, attributeName) != null)
                    .collect(Collectors.toSet());
        } finally {
            for (final Lock lock : locks) {
                lock.unlock();
            }
        }
    }

    /**
     * Finds and returns the collection of tags (including the nested tags) matching
     * with the given attribute name.
     *
     * @param attributeName the name of the attribute.
     * @return the collection of tags matching with the given attribute.
     * @throws NullValueException if the {@code attributeName} is null
     * @since 2.1.8
     * @author WFF
     */
    public Collection<AbstractHtml> findTagsByAttributeName(final String attributeName) throws NullValueException {
        return findTagsByAttributeName(false, attributeName);
    }

    /**
     * Finds and returns the collection of tags (including the nested tags) matching
     * with the given attribute name.
     *
     * @param parallel      true to internally use parallel stream. If true it will
     *                      split the finding task to different batches and will
     *                      execute the batches in different threads in parallel
     *                      consuming all CPUs. It will perform faster in finding
     *                      from extremely large number of tags but at the same time
     *                      it will less efficient in finding from small number of
     *                      tags.
     * @param attributeName the name of the attribute.
     * @return the collection of tags matching with the given attribute.
     * @throws NullValueException if the {@code attributeName} is null
     * @since 3.0.0
     * @author WFF
     */
    public Collection<AbstractHtml> findTagsByAttributeName(final boolean parallel, final String attributeName)
            throws NullValueException {

        if (attributeName == null) {
            throw new NullValueException("The attributeName should not be null");
        }

        final Collection<Lock> locks = lockAndGetReadLocks(rootTags);

        try {
            return buildAllTagsStream(parallel).filter(tag -> getAttributeByNameLockless(tag, attributeName) != null)
                    .collect(Collectors.toSet());
        } finally {
            for (final Lock lock : locks) {
                lock.unlock();
            }
        }
    }

    /**
     * Finds and returns the collection of tags (including the nested tags) matching
     * with the given attribute name and value.
     *
     * @param attributeName  the name of the attribute.
     * @param attributeValue the value of the attribute
     * @return the collection of tags matching with the given attribute name and
     *         value.
     * @throws NullValueException if the {@code attributeName} or
     *                            {@code attributeValue} is null
     * @since 2.1.8
     * @author WFF
     */
    public Collection<AbstractHtml> findTagsByAttribute(final String attributeName, final String attributeValue)
            throws NullValueException {
        return findTagsByAttribute(false, attributeName, attributeValue);
    }

    /**
     * Finds and returns the collection of tags (including the nested tags) matching
     * with the given attribute name and value.
     *
     * @param parallel       true to internally use parallel stream. If true it will
     *                       split the finding task to different batches and will
     *                       execute the batches in different threads in parallel
     *                       consuming all CPUs. It will perform faster in finding
     *                       from extremely large number of tags but at the same
     *                       time it will less efficient in finding from small
     *                       number of tags.
     * @param attributeName  the name of the attribute.
     * @param attributeValue the value of the attribute
     * @return the collection of tags matching with the given attribute name and
     *         value.
     * @throws NullValueException if the {@code attributeName} or
     *                            {@code attributeValue} is null
     * @since 3.0.0
     * @author WFF
     */
    public Collection<AbstractHtml> findTagsByAttribute(final boolean parallel, final String attributeName,
            final String attributeValue) throws NullValueException {

        if (attributeName == null) {
            throw new NullValueException("The attributeName should not be null");
        }

        if (attributeValue == null) {
            throw new NullValueException("The attributeValue should not be null");
        }

        final Collection<Lock> locks = lockAndGetReadLocks(rootTags);

        try {
            return buildAllTagsStream(parallel).filter(tag -> {

                final AbstractAttribute attribute = getAttributeByNameLockless(tag, attributeName);

                return attribute != null && attributeValue.equals(attribute.getAttributeValue());
            }).collect(Collectors.toSet());
        } finally {
            for (final Lock lock : locks) {
                lock.unlock();
            }
        }
    }

    /**
     * Finds and returns the collection of tags (including the nested tags) matching
     * with the give tag name and value.
     *
     * @param tagName the name of the tag.
     * @return the collection of tags matching with the given tag name and value.
     * @throws NullValueException if the {@code tagName} is null
     * @since 2.1.11
     * @author WFF
     */
    public Collection<AbstractHtml> findTagsByTagName(final String tagName) throws NullValueException {
        return findTagsByTagName(false, tagName);
    }

    /**
     * Finds and returns the collection of tags (including the nested tags) matching
     * with the give tag name and value.
     *
     * @param parallel true to internally use parallel stream. If true it will split
     *                 the finding task to different batches and will execute the
     *                 batches in different threads in parallel consuming all CPUs.
     *                 It will perform faster in finding from extremely large number
     *                 of tags but at the same time it will less efficient in
     *                 finding from small number of tags.
     * @param tagName  the name of the tag.
     * @return the collection of tags matching with the given tag name and value.
     * @throws NullValueException if the {@code tagName} is null
     * @since 3.0.0
     * @author WFF
     */
    public Collection<AbstractHtml> findTagsByTagName(final boolean parallel, final String tagName)
            throws NullValueException {

        if (tagName == null) {
            throw new NullValueException("The tagName should not be null");
        }

        final Collection<Lock> locks = lockAndGetReadLocks(rootTags);

        try {
            return buildAllTagsStream(parallel).filter(tag -> tagName.equals(tag.getTagName()))
                    .collect(Collectors.toSet());
        } finally {
            for (final Lock lock : locks) {
                lock.unlock();
            }
        }
    }

    /**
     * Finds and returns the collection of tags (including the nested tags but
     * excluding {@code NoTag} ) filtered by the given filter.
     *
     *
     * @param filter the filter lambda expression containing return true to include
     *               and false to exclude.
     * @return the collection of tags by the given filter.
     * @throws NullValueException if the {@code Predicate filter} is null
     * @since 3.0.0
     * @author WFF
     */
    public Collection<AbstractHtml> findTagsByTagFilter(final Predicate<? super AbstractHtml> filter)
            throws NullValueException {
        return findTagsByTagFilter(false, filter);
    }

    /**
     * Finds and returns the collection of tags (including the nested tags but
     * excluding {@code NoTag} ) filtered by the given filter.
     *
     * @param parallel true to internally use parallel stream. If true it will split
     *                 the finding task to different batches and will execute the
     *                 batches in different threads in parallel consuming all CPUs.
     *                 It will perform faster in finding from extremely large number
     *                 of tags but at the same time it will less efficient in
     *                 finding from small number of tags.
     * @param filter   the filter lambda expression containing return true to
     *                 include and false to exclude.
     * @return the collection of tags by the given filter.
     * @throws NullValueException if the {@code Predicate filter} is null
     * @since 3.0.0
     * @author WFF
     */
    public Collection<AbstractHtml> findTagsByTagFilter(final boolean parallel,
            final Predicate<? super AbstractHtml> filter) throws NullValueException {

        if (filter == null) {
            throw new NullValueException("The filter should not be null");
        }

        final Collection<Lock> locks = lockAndGetReadLocks(rootTags);

        try {
            return buildAllTagsStream(parallel).filter(filter).collect(Collectors.toSet());
        } finally {
            for (final Lock lock : locks) {
                lock.unlock();
            }
        }
    }

    /**
     * Finds and returns the collection of tags (including the nested tags and
     * {@code NoTag}) filtered by the given filter.
     *
     * @param parallel true to internally use parallel stream. If true it will split
     *                 the finding task to different batches and will execute the
     *                 batches in different threads in parallel consuming all CPUs.
     *                 It will perform faster in finding from extremely large number
     *                 of tags but at the same time it will less efficient in
     *                 finding from small number of tags.
     * @param filter   the filter lambda expression containing return true to
     *                 include and false to exclude.
     * @return the collection of tags by the given filter.
     * @throws NullValueException if the {@code Predicate filter} or
     *                            {@code fromTags} is null
     * @since 3.0.0
     * @author WFF
     */
    public static Collection<AbstractHtml> findTagsByTagFilter(final boolean parallel,
            final Predicate<? super AbstractHtml> filter, final AbstractHtml... fromTags) throws NullValueException {

        if (filter == null) {
            throw new NullValueException("The filter should not be null");
        }
        if (fromTags == null) {
            throw new NullValueException("The fromTags should not be null");
        }

        final Collection<Lock> locks = lockAndGetReadLocks(fromTags);

        try {

            return getAllNestedChildrenIncludingParent(parallel, fromTags).filter(filter).collect(Collectors.toSet());
        } finally {
            for (final Lock lock : locks) {
                lock.unlock();
            }
        }
    }

    /**
     * Finds and returns the collection of tags (including the nested tags and
     * {@code NoTag}) filtered by the given filter.
     *
     *
     * @param filter the filter lambda expression containing return true to include
     *               and false to exclude.
     * @return the collection of tags matching with the given tag name and value.
     * @throws NullValueException if the {@code tagName} or {@code fromTags} is null
     * @since 3.0.0
     * @author WFF
     */
    public static Collection<AbstractHtml> findTagsByTagFilter(final Predicate<? super AbstractHtml> filter,
            final AbstractHtml... fromTags) throws NullValueException {

        if (filter == null) {
            throw new NullValueException("The filter should not be null");
        }

        return findTagsByTagFilter(false, filter, fromTags);
    }

    /**
     * Finds and returns the collection of attributes (including from nested tags)
     * of the tags matching with the give tag name.
     *
     * @param tagName the name of the tag.
     * @return the collection of attributes of the tags matching with the given tag
     *         name.
     * @throws NullValueException if the {@code tagName} or {@code fromTags} is null
     * @since 2.1.11
     * @author WFF
     */
    public Collection<AbstractAttribute> findAttributesByTagName(final String tagName) throws NullValueException {
        return findAttributesByTagName(false, tagName);
    }

    /**
     * Finds and returns the collection of attributes (including from nested tags)
     * of the tags matching with the give tag name.
     *
     * @param parallel true to internally use parallel stream. If true it will split
     *                 the finding task to different batches and will execute the
     *                 batches in different threads in parallel consuming all CPUs.
     *                 It will perform faster in finding from extremely large number
     *                 of tags but at the same time it will less efficient in
     *                 finding from small number of tags.
     * @param tagName  the name of the tag.
     * @return the collection of attributes of the tags matching with the given tag
     *         name.
     * @throws NullValueException if the {@code tagName} is null
     * @since 3.0.0
     * @author WFF
     */
    public Collection<AbstractAttribute> findAttributesByTagName(final boolean parallel, final String tagName)
            throws NullValueException {

        if (tagName == null) {
            throw new NullValueException("The tagName should not be null");
        }

        final Collection<Lock> locks = lockAndGetReadLocks(rootTags);

        try {
            final Stream<AbstractAttribute> attributesStream = buildAllTagsStream(parallel)
                    .filter(tag -> tagName.equals(tag.getTagName()) && getAttributesLockless(tag) != null)
                    .map(AbstractHtmlRepository::getAttributesLockless).flatMap(Collection::stream);

            return attributesStream.collect(Collectors.toSet());
        } finally {
            for (final Lock lock : locks) {
                lock.unlock();
            }
        }
    }

    /**
     * Finds and returns the collection of attributes (including from nested tags)
     * of the tags filtered by the given filter
     *
     *
     * @param filter the filter lambda expression containing return true to include
     *               and false to exclude.
     * @return the collection of attributes by the given filter.
     * @throws NullValueException if the {@code tagName} is null
     * @since 3.0.0
     * @author WFF
     */
    public Collection<AbstractAttribute> findAttributesByAttributeFilter(
            final Predicate<? super AbstractAttribute> filter) throws NullValueException {

        return findAttributesByAttributeFilter(false, filter);
    }

    /**
     * Finds and returns the collection of attributes (including from nested tags)
     * of the tags filtered by the given filter
     *
     * @param parallel true to internally use parallel stream. If true it will split
     *                 the finding task to different batches and will execute the
     *                 batches in different threads in parallel consuming all CPUs.
     *                 It will perform faster in finding from extremely large number
     *                 of tags but at the same time it will less efficient in
     *                 finding from small number of tags.
     * @param filter   the filter lambda expression containing return true to
     *                 include and false to exclude.
     * @return the collection of attributes by the given filter.
     * @throws NullValueException if the {@code tagName} is null
     * @since 3.0.0
     * @author WFF
     */
    public Collection<AbstractAttribute> findAttributesByAttributeFilter(final boolean parallel,
            final Predicate<? super AbstractAttribute> filter) throws NullValueException {

        if (filter == null) {
            throw new NullValueException("The filter should not be null");
        }

        final Collection<Lock> locks = lockAndGetReadLocks(rootTags);

        try {
            final Stream<AbstractAttribute> attributesStream = buildAllTagsStream(parallel)
                    .filter(tag -> getAttributesLockless(tag) != null)
                    .map(AbstractHtmlRepository::getAttributesLockless).flatMap(Collection::stream);

            return attributesStream.filter(filter).collect(Collectors.toSet());
        } finally {
            for (final Lock lock : locks) {
                lock.unlock();
            }

        }
    }

    /**
     * Finds and returns the first (including the nested tags) matching with the
     * given attribute name and value.
     *
     * @param attributeName  the name of the attribute.
     * @param attributeValue the value of the attribute
     * @return the first matching tag with the given attribute name and value.
     * @throws NullValueException if the {@code attributeName} or
     *                            {@code attributeValue} is null
     * @since 2.1.8
     * @author WFF
     */
    public AbstractHtml findOneTagByAttribute(final String attributeName, final String attributeValue)
            throws NullValueException {
        return findOneTagByAttribute(false, attributeName, attributeValue);
    }

    /**
     * Finds and returns the first (including the nested tags) matching with the
     * given attribute name and value.
     *
     * @param parallel       true to internally use parallel stream. If true it will
     *                       split the finding task to different batches and will
     *                       execute the batches in different threads in parallel
     *                       consuming all CPUs. It will perform faster in finding
     *                       from extremely large number of tags but at the same
     *                       time it will less efficient in finding from small
     *                       number of tags.
     * @param attributeName  the name of the attribute.
     * @param attributeValue the value of the attribute
     * @return the first matching tag with the given attribute name and value.
     * @throws NullValueException if the {@code attributeName} or
     *                            {@code attributeValue} is null
     * @since 3.0.0
     * @author WFF
     */
    public AbstractHtml findOneTagByAttribute(final boolean parallel, final String attributeName,
            final String attributeValue) throws NullValueException {

        final Collection<Lock> locks = lockAndGetReadLocks(rootTags);

        try {
            final Stream<AbstractHtml> stream = buildAllTagsStream(parallel);

            final Optional<AbstractHtml> any = stream.filter(tag -> {
                final AbstractAttribute attribute = getAttributeByNameLockless(tag, attributeName);
                return attribute != null && attributeValue.equals(attribute.getAttributeValue());
            }).findAny();

            return any.orElse(null);

        } finally {
            for (final Lock lock : locks) {
                lock.unlock();
            }
        }
    }

    /**
     * Finds and returns the first (including the nested tags) matching with the
     * given tag name.
     *
     * @param tagName the name of the tag.
     * @return the first matching tag with the given tag name.
     * @throws NullValueException if the {@code tagName} is null
     * @since 2.1.11
     * @author WFF
     */
    public AbstractHtml findOneTagByTagName(final String tagName) throws NullValueException {
        return findOneTagByTagName(false, tagName);
    }

    /**
     * Finds and returns the first (including the nested tags) matching with the
     * given tag name.
     *
     * @param parallel true to internally use parallel stream. If true it will split
     *                 the finding task to different batches and will execute the
     *                 batches in different threads in parallel consuming all CPUs.
     *                 It will perform faster in finding from extremely large number
     *                 of tags but at the same time it will less efficient in
     *                 finding from small number of tags.
     * @param tagName  the name of the tag.
     * @return the first matching tag with the given tag name.
     * @throws NullValueException if the {@code tagName} is null
     * @since 3.0.0
     * @author WFF
     */
    public AbstractHtml findOneTagByTagName(final boolean parallel, final String tagName) throws NullValueException {

        if (tagName == null) {
            throw new NullValueException("The tagName should not be null");
        }

        final Collection<Lock> locks = lockAndGetReadLocks(rootTags);

        try {
            final Optional<AbstractHtml> any = buildAllTagsStream(parallel)
                    .filter(tag -> tagName.equals(tag.getTagName())).findAny();

            return any.orElse(null);
        } finally {
            for (final Lock lock : locks) {
                lock.unlock();
            }
        }
    }

    /**
     * Finds and returns the first matching (including from nested tags) tag (which
     * is assignable to the given tag class). <br>
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
     *  &lt;title&gt;some title&lt;/title&gt;
     *
     * </code>
     * </pre>
     *
     * @param tagClass the class of the tag.
     * @return the first matching tag which is assignable to the given tag class.
     * @throws NullValueException if the {@code tagClass} is null
     * @since 2.1.11
     * @author WFF
     */
    public <T extends AbstractHtml> T findOneTagAssignableToTag(final Class<T> tagClass) throws NullValueException {
        return findOneTagAssignableToTag(false, tagClass);
    }

    /**
     * Finds and returns the first matching (including from nested tags) tag (which
     * is assignable to the given tag class). <br>
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
     *  &lt;title&gt;some title&lt;/title&gt;
     *
     * </code>
     * </pre>
     *
     * @param parallel true to internally use parallel stream. If true it will split
     *                 the finding task to different batches and will execute the
     *                 batches in different threads in parallel consuming all CPUs.
     *                 It will perform faster in finding from extremely large number
     *                 of tags but at the same time it will less efficient in
     *                 finding from small number of tags.
     * @param tagClass the class of the tag.
     * @return the first matching tag which is assignable to the given tag class.
     * @throws NullValueException if the {@code tagClass} is null
     * @since 3.0.0
     * @author WFF
     */
    @SuppressWarnings("unchecked")
    public <T extends AbstractHtml> T findOneTagAssignableToTag(final boolean parallel, final Class<T> tagClass)
            throws NullValueException {

        final Collection<Lock> locks = lockAndGetReadLocks(rootTags);

        try {
            final Stream<AbstractHtml> stream = buildAllTagsStream(parallel);

            final Optional<AbstractHtml> any = stream.filter(
                    tag -> tagClass.isAssignableFrom(tag.getClass()) && !NoTag.class.isAssignableFrom(tag.getClass()))
                    .findAny();

            return (T) any.orElse(null);
        } finally {
            for (final Lock lock : locks) {
                lock.unlock();
            }
        }

    }

    /**
     * Finds and returns the all matching (including from nested tags) tags (which
     * is assignable to the given tag class). <br>
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
     *  Collection&lt;Head&gt; heads = TagRepository.findTagsAssignableToTag(Head.class, html);
     *  Collection&lt;Div&gt; divs = TagRepository.findTagsAssignableToTag(Div.class, html);
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
     * @param tagClass the class of the tag.
     * @return the all matching tags which is assignable to the given tag class.
     * @throws NullValueException  if the {@code tagClass} is null
     * @throws InvalidTagException if the given tag class is NoTag.class
     * @since 2.1.11
     * @author WFF
     */
    public <T extends AbstractHtml> Collection<T> findTagsAssignableToTag(final Class<T> tagClass)
            throws NullValueException, InvalidTagException {
        return findTagsAssignableToTag(false, tagClass);
    }

    /**
     * Finds and returns the all matching (including from nested tags) tags (which
     * is assignable to the given tag class). <br>
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
     *  Collection&lt;Head&gt; heads = TagRepository.findTagsAssignableToTag(Head.class, html);
     *  Collection&lt;Div&gt; divs = TagRepository.findTagsAssignableToTag(Div.class, html);
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
     * @param parallel true to internally use parallel stream. If true it will split
     *                 the finding task to different batches and will execute the
     *                 batches in different threads in parallel consuming all CPUs.
     *                 It will perform faster in finding from extremely large number
     *                 of tags but at the same time it will less efficient in
     *                 finding from small number of tags.
     * @param tagClass the class of the tag.
     * @return the all matching tags which is assignable to the given tag class.
     * @throws NullValueException  if the {@code tagClass} is null
     * @throws InvalidTagException if the given tag class is NoTag.class
     * @since 3.0.0
     * @author WFF
     */
    @SuppressWarnings("unchecked")
    public <T extends AbstractHtml> Collection<T> findTagsAssignableToTag(final boolean parallel,
            final Class<T> tagClass) throws NullValueException, InvalidTagException {

        if (tagClass == null) {
            throw new NullValueException("The tagClass should not be null");
        }

        if (NoTag.class.isAssignableFrom(tagClass)) {
            throw new InvalidTagException(
                    "classes like NoTag.class cannot be used to find tags as it's not a logical tag in behaviour.");
        }

        final Collection<Lock> locks = lockAndGetReadLocks(rootTags);

        try {
            final Set<AbstractHtml> set = buildAllTagsStream(parallel).filter(
                    tag -> tagClass.isAssignableFrom(tag.getClass()) && !NoTag.class.isAssignableFrom(tag.getClass()))
                    .collect(Collectors.toSet());

            return (Collection<T>) set;
        } finally {
            for (final Lock lock : locks) {
                lock.unlock();
            }
        }
    }

    /**
     * Finds and returns the first (including the nested tags) matching with the
     * given attribute name.
     *
     * @param attributeName the name of the attribute.
     * @return the first matching tag with the given attribute name and value.
     * @throws NullValueException if the {@code attributeName} is null
     * @since 2.1.8
     * @author WFF
     */
    public AbstractHtml findOneTagByAttributeName(final String attributeName) throws NullValueException {
        return findOneTagByAttributeName(false, attributeName);
    }

    /**
     * Finds and returns the first (including the nested tags) matching with the
     * given attribute name.
     *
     * @param parallel      true to internally use parallel stream. If true it will
     *                      split the finding task to different batches and will
     *                      execute the batches in different threads in parallel
     *                      consuming all CPUs. It will perform faster in finding
     *                      from extremely large number of tags but at the same time
     *                      it will less efficient in finding from small number of
     *                      tags.
     * @param attributeName the name of the attribute.
     * @return the first matching tag with the given attribute name and value.
     * @throws NullValueException if the {@code attributeName} is null
     * @since 3.0.0
     * @author WFF
     */
    public AbstractHtml findOneTagByAttributeName(final boolean parallel, final String attributeName)
            throws NullValueException {

        if (attributeName == null) {
            throw new NullValueException("The attributeName should not be null");
        }
        final Collection<Lock> locks = lockAndGetReadLocks(rootTags);

        try {
            final Stream<AbstractHtml> stream = buildAllTagsStream(parallel);

            final Optional<AbstractHtml> any = stream
                    .filter(tag -> getAttributeByNameLockless(tag, attributeName) != null).findAny();

            return any.orElse(null);
        } finally {
            for (final Lock lock : locks) {
                lock.unlock();
            }
        }
    }

    /**
     * Finds tags by attribute instance.
     *
     * @param attribute
     * @return all tags which are consuming the given attribute instance. It returns
     *         the only tags consuming the given attribute object which are
     *         available in browserPage.
     * @throws NullValueException if the {@code attribute} is null
     * @since 2.1.8
     * @author WFF
     */
    public Collection<AbstractHtml> findTagsByAttribute(final AbstractAttribute attribute) throws NullValueException {

        if (attribute == null) {
            throw new NullValueException("attribute cannot be null");
        }

        final Collection<AbstractHtml> tags = new HashSet<>();

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
     * @return the first matching tag consuming the given attribute instance. There
     *         must be a consuming tag which is available in the browserPage
     *         instance otherwise returns null.
     * @throws NullValueException if the {@code attribute } is null
     * @since 2.1.8
     * @author WFF
     */
    public AbstractHtml findOneTagByAttribute(final AbstractAttribute attribute) {

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
     * @param tag      the tag object on which the given bmObject to be set.
     * @param key      key to set in wffObjects property of the tag.
     * @param bmObject
     * @throws InvalidTagException if the given instance is of NoTag / Blank
     * @throws NullValueException  if tag, key or bmObject is null
     * @since 2.1.8
     * @author WFF
     */
    public void upsert(final AbstractHtml tag, final String key, final WffBMObject bmObject)
            throws InvalidTagException, NullValueException {
        if (tag == null) {
            throw new NullValueException("tag cannot be null");
        }
        if (tag instanceof NoTag) {
            throw new InvalidTagException("NoTag and Blank tag are not allowed to use");
        }
        if (key == null) {
            throw new NullValueException("key cannot be null");
        }
        if (bmObject == null) {
            throw new NullValueException("bmObject cannot be null");
        }

        if (!browserPage.contains(tag)) {
            throw new InvalidTagException("The given tag is not available in the corresponding browserPage instance.");
        }

        AbstractHtmlRepository.addWffData(tag, key, bmObject);
    }

    /**
     * Inserts or replaces (if already exists) the key bmArray pair in the
     * wffObjects property of tag. The conventional JavaScript array of
     * {@code WffBMArray} will be set to the {@code wffObjects} property of the
     * given tag at client side.
     *
     * @param tag     the tag object on which the given bmArray to be set.
     * @param key     key to set in wffObjects property of the tag.
     * @param bmArray
     * @throws InvalidTagException if the given instance is of NoTag / Blank
     * @throws NullValueException  if tag, key or bmArray is null
     * @since 2.1.8
     * @author WFF
     */
    public void upsert(final AbstractHtml tag, final String key, final WffBMArray bmArray)
            throws InvalidTagException, NullValueException {

        if (tag == null) {
            throw new NullValueException("tag cannot be null");
        }
        if (tag instanceof NoTag) {
            throw new InvalidTagException("NoTag and Blank tag are not allowed to use");
        }
        if (key == null) {
            throw new NullValueException("key cannot be null");
        }
        if (bmArray == null) {
            throw new NullValueException("bmArray cannot be null");
        }
        if (!browserPage.contains(tag)) {
            throw new InvalidTagException("The given tag is not available in the corresponding browserPage instance.");
        }
        AbstractHtmlRepository.addWffData(tag, key, bmArray);
    }

    /**
     * Deletes the key-WffBMObject/WffBMArray pair from the {@code wffObjects}
     * property of tag.
     *
     * @param tag
     * @param key
     * @throws InvalidTagException if the given instance is of NoTag / Blank
     * @throws NullValueException  if tag or key is null
     * @since 2.1.8
     * @author WFF
     */
    public void delete(final AbstractHtml tag, final String key) throws InvalidTagException, NullValueException {

        if (tag == null) {
            throw new NullValueException("tag cannot be null");
        }
        if (tag instanceof NoTag) {
            throw new InvalidTagException("NoTag and Blank tag are not allowed to use");
        }
        if (key == null) {
            throw new NullValueException("key cannot be null");
        }
        if (!browserPage.contains(tag)) {
            throw new InvalidTagException("The given tag is not available in the corresponding browserPage instance.");
        }
        AbstractHtmlRepository.removeWffData(tag, key);
    }

    /**
     * gets the WffBMObject/WffBMArray from the {@code wffObjects} property of tag
     * in the form of WffBMData.
     *
     * @param tag
     * @param key
     * @return WffBMData which may be type casted to either WffBMObject or
     *         WffBMArray.
     * @throws InvalidTagException if the given instance is of NoTag / Blank
     * @throws NullValueException  if tag or key is null
     * @since 3.0.1
     * @author WFF
     */
    public WffBMData getWffBMData(final AbstractHtml tag, final String key)
            throws InvalidTagException, NullValueException {

        if (tag == null) {
            throw new NullValueException("tag cannot be null");
        }
        if (tag instanceof NoTag) {
            throw new InvalidTagException("NoTag and Blank tag are not allowed to use");
        }
        if (key == null) {
            throw new NullValueException("key cannot be null");
        }
        if (!browserPage.contains(tag)) {
            throw new InvalidTagException("The given tag is not available in the corresponding browserPage instance.");
        }
        return AbstractHtmlRepository.getWffData(tag, key);
    }

    /**
     * Finds all tags excluding {@code NoTag}. To get {@code NoTag} included
     * collection use static method
     * {@code TagRepository.findAllTags(AbstractHtml... fromTags)}.
     *
     * This method may perform better than the static method.
     *
     * @return the collection of all tags
     * @since 2.1.8
     * @author WFF
     */
    public Collection<AbstractHtml> findAllTags() {
        return findAllTags(false);
    }

    /**
     * Finds all tags excluding {@code NoTag}. To get {@code NoTag} included
     * collection use static method
     * {@code TagRepository.findAllTags(boolean parallel, AbstractHtml... fromTags)}.
     *
     * This method may perform better than the static method.
     *
     * @param parallel true to internally use parallel stream. If true it will split
     *                 the finding task to different batches and will execute the
     *                 batches in different threads in parallel consuming all CPUs.
     *                 It will perform faster in finding from extremely large number
     *                 of tags but at the same time it will less efficient in
     *                 finding from small number of tags.
     * @return the collection of all tags
     * @since 3.0.0
     * @author WFF
     */
    public Collection<AbstractHtml> findAllTags(final boolean parallel) {
        final Collection<Lock> locks = lockAndGetReadLocks(rootTags);

        try {
            return buildAllTagsStream(parallel).collect(Collectors.toSet());
        } finally {
            for (final Lock lock : locks) {
                lock.unlock();
            }
        }
    }

    /**
     * Finds all tags excluding {@code NoTag}.
     *
     * @param parallel true to internally use parallel stream. If true it will split
     *                 the finding task to different batches and will execute the
     *                 batches in different threads in parallel consuming all CPUs.
     *                 It will perform faster in finding from extremely large number
     *                 of tags but at the same time it will less efficient in
     *                 finding from small number of tags.
     * @return the stream of all tags
     * @since 3.0.0
     * @author WFF
     */
    public Stream<AbstractHtml> buildAllTagsStream(final boolean parallel) {
        return parallel ? tagByWffId.values().parallelStream() : tagByWffId.values().stream();
    }

    /**
     * Finds all tags including the nested tags (and including {@code NoTag}) from
     * the given tags.
     *
     * @param fromTags to find all tags from these tags
     * @return all tags including the nested tags from the given tags.
     * @throws NullValueException if {@code fromTags} is null
     * @since 2.1.9
     * @author WFF
     */
    public static Collection<AbstractHtml> findAllTags(final AbstractHtml... fromTags) throws NullValueException {
        return findAllTags(false, fromTags);
    }

    /**
     * Finds all tags including the nested tags (and including {@code NoTag}) from
     * the given tags.
     *
     * @param parallel true to internally use parallel stream. If true it will split
     *                 the finding task to different batches and will execute the
     *                 batches in different threads in parallel consuming all CPUs.
     *                 It will perform faster in finding from extremely large number
     *                 of tags but at the same time it will less efficient in
     *                 finding from small number of tags.
     *
     * @param fromTags to find all tags from these tags
     * @return all tags including the nested tags from the given tags.
     * @throws NullValueException if {@code fromTags} is null
     * @since 3.0.0
     * @author WFF
     */
    public static Collection<AbstractHtml> findAllTags(final boolean parallel, final AbstractHtml... fromTags)
            throws NullValueException {

        if (fromTags == null) {
            throw new NullValueException("fromTags cannot be null");
        }

        final Collection<Lock> locks = lockAndGetReadLocks(fromTags);

        try {
            return getAllNestedChildrenIncludingParent(parallel, fromTags).collect(Collectors.toSet());

        } finally {
            for (final Lock lock : locks) {
                lock.unlock();
            }
        }
    }

    /**
     * Finds all attributes from all tags.
     *
     * @return the collection of all attributes
     * @since 2.1.8
     * @author WFF
     */
    public Collection<AbstractAttribute> findAllAttributes() {
        return findAllAttributes(false);
    }

    /**
     * Finds all attributes from all tags.
     *
     * @param parallel true to internally use parallel stream. If true it will split
     *                 the finding task to different batches and will execute the
     *                 batches in different threads in parallel consuming all CPUs.
     *                 It will perform faster in finding from extremely large number
     *                 of tags but at the same time it will less efficient in
     *                 finding from small number of tags.
     * @return the collection of all attributes
     * @since 3.0.0
     * @author WFF
     */
    public Collection<AbstractAttribute> findAllAttributes(final boolean parallel) {
        final Collection<Lock> locks = lockAndGetReadLocks(rootTags);

        try {
            return buildAllAttributesStream(parallel).collect(Collectors.toSet());
        } finally {
            for (final Lock lock : locks) {
                lock.unlock();
            }
        }
    }

    /**
     * Finds all attributes as stream from all tags.
     *
     * @param parallel true to internally use parallel stream. If true it will split
     *                 the finding task to different batches and will execute the
     *                 batches in different threads in parallel consuming all CPUs.
     *                 It will perform faster in finding from extremely large number
     *                 of tags but at the same time it will less efficient in
     *                 finding from small number of tags.
     * @return the stream of all attributes
     * @since 3.0.0
     * @author WFF
     */
    public Stream<AbstractAttribute> buildAllAttributesStream(final boolean parallel) {
        return buildAllTagsStream(parallel).filter(tag -> tag.getAttributes() != null).map(AbstractHtml::getAttributes)
                .flatMap(Collection::stream);
    }

    /**
     * Finds all attributes from the given tags
     *
     * @param fromTags the tags to find the attributes from.
     * @return the all attributes from the given tags including the nested tags.
     * @throws NullValueException if {@code fromTags} is null
     * @since 2.1.9
     * @author WFF
     */
    public static Collection<AbstractAttribute> findAllAttributes(final AbstractHtml... fromTags)
            throws NullValueException {
        return findAllAttributes(false, fromTags);
    }

    /**
     * Finds all attributes from the given tags
     *
     * @param parallel true to internally use parallel stream. If true it will split
     *                 the finding task to different batches and will execute the
     *                 batches in different threads in parallel consuming all CPUs.
     *                 It will perform faster in finding from extremely large number
     *                 of tags but at the same time it will less efficient in
     *                 finding from small number of tags.
     * @param fromTags the tags to find the attributes from.
     * @return the all attributes from the given tags including the nested tags.
     * @throws NullValueException if {@code fromTags} is null
     * @since 3.0.0
     * @author WFF
     */
    public static Collection<AbstractAttribute> findAllAttributes(final boolean parallel,
            final AbstractHtml... fromTags) throws NullValueException {

        if (fromTags == null) {
            throw new NullValueException("fromTags cannot be null");
        }

        final Collection<Lock> locks = lockAndGetReadLocks(fromTags);

        try {
            return getAllNestedChildrenIncludingParent(parallel, fromTags)
                    .filter(tag -> getAttributesLockless(tag) != null)
                    .map(AbstractHtmlRepository::getAttributesLockless).flatMap(Collection::stream)
                    .collect(Collectors.toSet());
        } finally {
            for (final Lock lock : locks) {
                lock.unlock();
            }
        }
    }

    /**
     * Checks the existence of a tag instance.
     *
     * @param tag
     * @return true if the given tag instance exists anywhere in the browser page.
     * @throws NullValueException  if the tag is null
     * @throws InvalidTagException if the given tag is {@code NoTag}.
     * @since 2.1.8
     * @author WFF
     */
    public boolean exists(final AbstractHtml tag) throws NullValueException, InvalidTagException {
        if (tag == null) {
            throw new NullValueException("tag cannot be null");
        }
        if (NoTag.class.isAssignableFrom(tag.getClass())) {
            throw new InvalidTagException(
                    "classes like NoTag.class cannot be used to find tags as it's not a logical tag in behaviour.");
        }
        return browserPage.contains(tag);
    }

    /**
     * Checks the existence of an attribute instance.
     *
     * @param attribute
     * @return true if the given attribute instance exists anywhere in the browser
     *         page.
     * @throws NullValueException if the {@code attribute} is null
     * @since 2.1.8
     * @author WFF
     */
    public boolean exists(final AbstractAttribute attribute) throws NullValueException {

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

    /**
     * Executes the JavaScript at the client browser page. This method is equalent
     * to calling <br>
     *
     * <pre>
     * <code>
     * try {
     *      browserPage.performBrowserPageAction(
     *              BrowserPageAction.getActionByteBufferForExecuteJS(js));
     *      return true;
     *  } catch (final UnsupportedEncodingException e) {
     *      e.printStackTrace();
     *  }
     * </code>
     * </pre>
     *
     * Eg:-
     *
     * <pre>
     * <code>
     * tagRepository.executeJs("alert('This is an alert');");
     * </code>
     * </pre>
     *
     * This shows an alert in the browser: <b><i>This is an alert</i></b>.
     *
     * @param js the JavaScript to be executed at the client browser page.
     * @return true if the given js string is in a supported encoding otherwise
     *         false. Returning true DOESN'T mean the given js string is valid ,
     *         successfully sent to the client browser to execute or executed
     *         successfully.
     * @since 2.1.11
     * @author WFF
     */
    public boolean executeJs(final String js) {
        browserPage.performBrowserPageAction(BrowserPageAction.getActionByteBufferForExecuteJS(js));
        return true;
    }

    /**
     * Performs the given {@code BrowserPageAction}. This method is equalent to
     * calling
     * <code>browserPage.performBrowserPageAction(pageAction.getActionByteBuffer());</code>.
     * <br>
     * <br>
     * Eg, the below code reloads the client browser page.:-
     *
     * <pre>
     * <code>
     * tagRepository.execute(BrowserPageAction.RELOAD);
     * </code>
     * </pre>
     *
     * @param pageAction to perform the given {@code BrowserPageAction}
     * @since 2.1.11
     * @author WFF
     */
    public void execute(final BrowserPageAction pageAction) {
        browserPage.performBrowserPageAction(pageAction.getActionByteBuffer());
    }

    /**
     * Finds the {@code body} tag.
     *
     * @return the {@code body} tag. If there are multiple {@code body} tags
     *         available any one of them will be returned. If no {@code body} tag
     *         found then null will be returned.
     * @since 3.0.0
     * @author WFF
     */
    public Body findBodyTag() {
        return findBodyTag(false);
    }

    /**
     * Finds the {@code body} tag.
     *
     * @param parallel true to internally use parallel stream. If true it will split
     *                 the finding task to different batches and will execute the
     *                 batches in different threads in parallel consuming all CPUs.
     *                 It will perform faster in finding from extremely large number
     *                 of tags but at the same time it will less efficient in
     *                 finding from small number of tags.
     *
     * @return the {@code body} tag. If there are multiple {@code body} tags
     *         available any one of them will be returned. If no {@code body} tag
     *         found then null will be returned.
     * @since 3.0.0
     * @author WFF
     */
    public Body findBodyTag(final boolean parallel) {

        final Reference<Body> bodyTagRef = this.bodyTagRef;
        if (bodyTagRef != null) {
            final Body bodyTag = bodyTagRef.get();
            if (bodyTag != null) {
                for (final AbstractHtml rootTag : rootTags) {
                    if (bodyTag.getRootTag().equals(rootTag)) {
                        return bodyTag;
                    }
                }
            }
        }
        final Body bodyTag = findOneTagAssignableToTag(parallel, Body.class);
        if (bodyTag != null) {
            this.bodyTagRef = new WeakReference<Body>(bodyTag);
        } else {
            this.bodyTagRef = null;
        }
        return bodyTag;

    }

    /**
     * Finds the {@code head} tag.
     *
     * @return the {@code head} tag. If there are multiple {@code head} tags
     *         available any one of them will be returned. If no {@code head} tag
     *         found then null will be returned.
     * @since 3.0.0
     * @author WFF
     */
    public Head findHeadTag() {
        return findHeadTag(false);
    }

    /**
     * Finds the {@code head} tag.
     *
     * @param parallel true to internally use parallel stream. If true it will split
     *                 the finding task to different batches and will execute the
     *                 batches in different threads in parallel consuming all CPUs.
     *                 It will perform faster in finding from extremely large number
     *                 of tags but at the same time it will less efficient in
     *                 finding from small number of tags.
     *
     * @return the {@code head} tag. If there are multiple {@code head} tags
     *         available any one of them will be returned. If no {@code head} tag
     *         found then null will be returned.
     * @since 3.0.0
     * @author WFF
     */
    public Head findHeadTag(final boolean parallel) {
        final Reference<Head> headTagRef = this.headTagRef;
        if (headTagRef != null) {
            final Head headTag = headTagRef.get();
            if (headTag != null) {
                for (final AbstractHtml rootTag : rootTags) {
                    if (headTag.getRootTag().equals(rootTag)) {
                        return headTag;
                    }
                }
            }
        }
        final Head headTag = findOneTagAssignableToTag(parallel, Head.class);
        if (headTag != null) {
            this.headTagRef = new WeakReference<Head>(headTag);
        } else {
            this.headTagRef = null;
        }
        return headTag;
    }

    /**
     * Finds the {@code title} tag.
     *
     * @return the {@code title} tag. If there are multiple {@code title} tags
     *         available any one of them will be returned. If no {@code title} tag
     *         found then null will be returned.
     * @since 3.0.0
     * @author WFF
     */
    public TitleTag findTitleTag() {
        return findTitleTag(false);
    }

    /**
     * Finds the {@code title} tag.
     *
     * @param parallel true to internally use parallel stream. If true it will split
     *                 the finding task to different batches and will execute the
     *                 batches in different threads in parallel consuming all CPUs.
     *                 It will perform faster in finding from extremely large number
     *                 of tags but at the same time it will less efficient in
     *                 finding from small number of tags.
     *
     * @return the {@code title} tag. If there are multiple {@code title} tags
     *         available any one of them will be returned. If no {@code title} tag
     *         found then null will be returned.
     * @since 3.0.0
     * @author WFF
     */
    public TitleTag findTitleTag(final boolean parallel) {
        final Reference<TitleTag> titleTagRef = this.titleTagRef;
        if (titleTagRef != null) {
            final TitleTag titleTag = titleTagRef.get();
            if (titleTag != null) {
                for (final AbstractHtml rootTag : rootTags) {
                    if (titleTag.getRootTag().equals(rootTag)) {
                        return titleTag;
                    }
                }
            }
        }
        final TitleTag titleTag = findOneTagAssignableToTag(parallel, TitleTag.class);
        if (titleTag != null) {
            this.titleTagRef = new WeakReference<TitleTag>(titleTag);
        } else {
            this.titleTagRef = null;
        }
        return titleTag;
    }

    /**
     * Builds all tags stream from the given tags and its nested children.
     *
     * @param parallel true to internally use parallel stream. If true it will split
     *                 the finding task to different batches and will execute the
     *                 batches in different threads in parallel consuming all CPUs.
     *                 It will perform faster in finding from extremely large number
     *                 of tags but at the same time it will less efficient in
     *                 finding from small number of tags.
     * @param fromTags the tags to to build stream of nested children from.
     * @return {@code Stream<AbstractHtml> }
     * @since 3.0.0
     * @author WFF
     */
    public static Stream<AbstractHtml> buildAllTagsStream(final boolean parallel, final AbstractHtml... fromTags) {
        return getAllNestedChildrenIncludingParent(parallel, fromTags);
    }

    /**
     * Builds all tags stream from the given tags and its nested children.
     *
     * @param fromTags the tags to build stream of nested children from.
     * @return {@code Stream<AbstractHtml> }
     * @since 3.0.0
     * @author WFF
     */
    public static Stream<AbstractHtml> buildAllTagsStream(final AbstractHtml... fromTags) {
        return buildAllTagsStream(false, fromTags);
    }

    /**
     * Builds all attributes stream from the given tags.
     *
     * @param fromTags the tags to build stream of nested children's attributes
     *                 from.
     * @return {@code Stream<AbstractAttribute>}
     * @since 3.0.0
     * @author WFF
     */
    public static Stream<AbstractAttribute> buildAllAttributesStream(final AbstractHtml... fromTags) {
        return buildAllAttributesStream(false, fromTags);
    }

    /**
     * Builds all attributes stream from the given tags.
     *
     * @param parallel true to internally use parallel stream. If true it will split
     *                 the finding task to different batches and will execute the
     *                 batches in different threads in parallel consuming all CPUs.
     *                 It will perform faster in finding from extremely large number
     *                 of tags but at the same time it will less efficient in
     *                 finding from small number of tags.
     * @param fromTags the tags to build stream of nested children's attributes
     *                 from.
     * @return {@code Stream<AbstractAttribute>}
     * @since 3.0.0
     * @author WFF
     */
    public static Stream<AbstractAttribute> buildAllAttributesStream(final boolean parallel,
            final AbstractHtml... fromTags) {

        final Collection<Lock> locks = lockAndGetReadLocks(fromTags);

        try {
            return getAllNestedChildrenIncludingParent(parallel, fromTags)
                    .filter(tag -> getAttributesLockless(tag) != null)
                    .map(AbstractHtmlRepository::getAttributesLockless).flatMap(Collection::stream);
        } finally {
            for (final Lock lock : locks) {
                lock.unlock();
            }
        }

    }

    /**
     * Builds all attributes stream from all tags.
     *
     * @return {@code Stream<AbstractAttribute>}
     * @since 3.0.0
     * @author WFF
     */
    public Stream<AbstractAttribute> buildAllAttributesStream() {
        return buildAllAttributesStream(false);
    }

    /**
     * Finds and returns the first matching parent tag (including nested parent)
     * (which is assignable to the given tag class).
     *
     * @param tagClass the class of the tag.
     * @param fromTag  from which the findings to be done.
     * @return the first matching tag which is assignable to the given tag class or
     *         null if not found.
     * @throws NullValueException  if the {@code tagClass} or {@code fromTags} is
     *                             null
     * @throws InvalidTagException if the given tag class is NoTag.class
     * @since 3.0.15
     */
    @SuppressWarnings("unchecked")
    public static <T extends AbstractHtml> T findFirstParentTagAssignableToTag(final Class<T> tagClass,
            final AbstractHtml fromTag) throws NullValueException, InvalidTagException {

        if (tagClass == null) {
            throw new NullValueException("The tagClass should not be null");
        }

        if (NoTag.class.isAssignableFrom(tagClass)) {
            throw new InvalidTagException(
                    "classes like NoTag.class cannot be used to find tags as it's not a logical tag in behaviour.");
        }

        if (fromTag == null) {
            throw new NullValueException("The fromTag should not be null");
        }

        final Collection<Lock> locks = lockAndGetReadLocks(fromTag);

        try {
            final Deque<AbstractHtml> stack = new ArrayDeque<>();
            stack.add(fromTag);

            AbstractHtml each;

            while ((each = stack.poll()) != null) {
                final AbstractHtml parent = each.getParent();
                if (parent != null) {
                    if (tagClass.isAssignableFrom(parent.getClass())) {
                        return (T) parent;
                    }
                    stack.push(parent);
                }
            }

            return null;
        } finally {
            for (final Lock lock : locks) {
                lock.unlock();
            }
        }
    }

}
