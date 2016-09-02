/*
 * Copyright 2014-2016 Web Firm Framework
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
package com.webfirmframework.wffweb.tag.html;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.webfirmframework.wffweb.MethodNotImplementedException;
import com.webfirmframework.wffweb.WffSecurityException;
import com.webfirmframework.wffweb.clone.CloneUtil;
import com.webfirmframework.wffweb.security.object.SecurityClassConstants;
import com.webfirmframework.wffweb.streamer.WffBinaryMessageOutputStreamer;
import com.webfirmframework.wffweb.tag.core.AbstractTagBase;
import com.webfirmframework.wffweb.tag.html.attribute.core.AbstractAttribute;
import com.webfirmframework.wffweb.tag.html.attribute.core.AttributeUtil;
import com.webfirmframework.wffweb.tag.html.attributewff.CustomAttribute;
import com.webfirmframework.wffweb.tag.html.core.TagRegistry;
import com.webfirmframework.wffweb.tag.html.listener.AttributeAddListener;
import com.webfirmframework.wffweb.tag.html.listener.AttributeRemoveListener;
import com.webfirmframework.wffweb.tag.html.listener.ChildTagAppendListener;
import com.webfirmframework.wffweb.tag.html.listener.ChildTagAppendListener.ChildMovedEvent;
import com.webfirmframework.wffweb.tag.html.listener.ChildTagRemoveListener;
import com.webfirmframework.wffweb.tag.html.listener.InnerHtmlAddListener;
import com.webfirmframework.wffweb.tag.html.model.AbstractHtml5SharedObject;
import com.webfirmframework.wffweb.util.WffBinaryMessageUtil;
import com.webfirmframework.wffweb.util.data.NameValue;

/**
 * @author WFF
 * @since 1.0.0
 * @version 1.2.0
 *
 */
public abstract class AbstractHtml extends AbstractTagBase {

    // if this class' is refactored then SecurityClassConstants should be
    // updated.

    private static final long serialVersionUID = 1_2_0L;

    protected static int tagNameIndex;

    private AbstractHtml parent;
    private Set<AbstractHtml> children;
    private String openingTag;

    // should be initialized with empty string
    private String closingTag = "";
    private StringBuilder htmlStartSB;
    private StringBuilder htmlMiddleSB;
    private StringBuilder htmlEndSB;

    private String tagName;

    private StringBuilder tagBuilder;
    private AbstractAttribute[] attributes;

    private Map<String, AbstractAttribute> attributesMap;

    private AbstractHtml5SharedObject sharedObject;

    private boolean htmlStartSBAsFirst;

    private OutputStream outputStream;

    // for future development
    private WffBinaryMessageOutputStreamer wffBinaryMessageOutputStreamer;

    private transient Charset charset = Charset.defaultCharset();

    // for security purpose, the class name should not be modified
    private static final class Security implements Serializable {

        private static final long serialVersionUID = 1L;

        private Security() {
        }
    }

    private static final Security ACCESS_OBJECT;

    static {
        ACCESS_OBJECT = new Security();
    }

    public static enum TagType {
        OPENING_CLOSING, SELF_CLOSING, NON_CLOSING;

        private TagType() {
        }
    }

    private TagType tagType = TagType.OPENING_CLOSING;

    {
        init();
    }

    @SuppressWarnings("unused")
    private AbstractHtml() {
        throw new AssertionError();
    }

    public AbstractHtml(final AbstractHtml base,
            final Collection<? extends AbstractHtml> children) {

        initInConstructor();

        buildOpeningTag(false);
        buildClosingTag();
        if (base != null) {
            base.addChild(this);
            // base.children.add(this);
            // should not uncomment the below codes as it is handled in the
            // above add method
            // parent = base;
            // sharedObject = base.sharedObject;
        } else {
            sharedObject = new AbstractHtml5SharedObject();
        }
        // this.children.addAll(children);
        for (final AbstractHtml child : children) {
            this.addChild(child);
        }
        // childAppended(parent, this);
    }

    /**
     * @param base
     * @param childContent
     *            any text, it can also be html text.
     */
    public AbstractHtml(final AbstractHtml base, final String childContent) {

        initInConstructor();

        htmlStartSBAsFirst = true;
        getHtmlMiddleSB().append(childContent);
        buildOpeningTag(false);
        buildClosingTag();
        if (base != null) {
            base.addChild(this);
            // base.children.add(this);
            // should not uncomment the below codes as it is handled in the
            // above add method
            // parent = base;
            // sharedObject = base.sharedObject;
        } else {
            sharedObject = new AbstractHtml5SharedObject();
        }
        setRebuild(true);

        // childAppended(parent, this);
    }

    /**
     * should be invoked to generate opening and closing tag base class
     * containing the functionalities to generate html string.
     *
     * @param tagName
     *            TODO
     * @param base
     *            TODO
     * @author WFF
     */
    public AbstractHtml(final String tagName, final AbstractHtml base,
            final AbstractAttribute[] attributes) {
        this.tagName = tagName;

        initAttributes(attributes);

        initInConstructor();

        markOwnerTag(attributes);
        buildOpeningTag(false);
        buildClosingTag();
        if (base != null) {
            base.addChild(this);
            // base.children.add(this);
            // should not uncomment the below codes as it is handled in the
            // above add method
            // parent = base;
            // sharedObject = base.sharedObject;
        } else {
            sharedObject = new AbstractHtml5SharedObject();
        }

        // childAppended(parent, this);
    }

    public boolean appendChild(final AbstractHtml child) {
        return addChild(child);
    }

    public void removeAllChildren() {

        final AbstractHtml[] removedAbstractHtmls = children
                .toArray(new AbstractHtml[children.size()]);

        children.clear();

        initNewSharedObjectInAllNestedTagsAndSetSuperParentNull(
                removedAbstractHtmls);

        final ChildTagRemoveListener listener = sharedObject
                .getChildTagRemoveListener(ACCESS_OBJECT);

        if (listener != null) {
            listener.allChildrenRemoved(new ChildTagRemoveListener.Event(
                    AbstractHtml.this, removedAbstractHtmls));
        }
    }

    public void addInnerHtml(final AbstractHtml innerHtml) {

        final AbstractHtml[] removedAbstractHtmls = children
                .toArray(new AbstractHtml[children.size()]);

        children.clear();

        initNewSharedObjectInAllNestedTagsAndSetSuperParentNull(
                removedAbstractHtmls);

        final InnerHtmlAddListener listener = sharedObject
                .getInnerHtmlAddListener(ACCESS_OBJECT);

        addChild(innerHtml, false);

        if (listener != null) {
            listener.innerHtmlAdded(new InnerHtmlAddListener.Event(
                    AbstractHtml.this, innerHtml));
        }
    }

    public boolean removeChildren(final Collection<AbstractHtml> children) {
        return this.children.removeAll(children);
    }

    public boolean removeChild(final AbstractHtml child) {

        final boolean removed = children.remove(child);

        if (removed) {

            // making child.parent = null inside the below method.
            initNewSharedObjectInAllNestedTagsAndSetSuperParentNull(child);

            final ChildTagRemoveListener listener = sharedObject
                    .getChildTagRemoveListener(ACCESS_OBJECT);

            if (listener != null) {
                listener.childRemoved(new ChildTagRemoveListener.Event(
                        AbstractHtml.this, child));
            }

        }

        return removed;
    }

    private boolean addChild(final AbstractHtml child) {
        return addChild(child, true);
    }

    private boolean addChild(final AbstractHtml child,
            final boolean invokeListener) {

        final boolean added = children.add(child);

        if (added) {

            // if alreadyHasParent = true then it means the child is moving from
            // one tag to another.
            final boolean alreadyHasParent = child.parent != null;
            final AbstractHtml previousParent = child.parent;

            if (alreadyHasParent) {
                child.parent.children.remove(child);
            }

            initParentAndSharedObject(child);

            if (invokeListener) {

                if (alreadyHasParent) {
                    final ChildTagAppendListener listener = sharedObject
                            .getChildTagAppendListener(ACCESS_OBJECT);

                    if (listener != null) {
                        listener.childMoved(
                                new ChildTagAppendListener.ChildMovedEvent(
                                        previousParent, AbstractHtml.this,
                                        child));
                    }

                } else {

                    final ChildTagAppendListener listener = sharedObject
                            .getChildTagAppendListener(ACCESS_OBJECT);
                    if (listener != null) {
                        final ChildTagAppendListener.Event event = new ChildTagAppendListener.Event(
                                AbstractHtml.this, child);
                        listener.childAppended(event);
                    }
                }
            }

        }
        return added;

    }

    private void initParentAndSharedObject(final AbstractHtml child) {
        final Stack<Set<AbstractHtml>> childrenStack = new Stack<Set<AbstractHtml>>();
        childrenStack.push(new HashSet<AbstractHtml>(Arrays.asList(child)));

        while (childrenStack.size() > 0) {

            final Set<AbstractHtml> children = childrenStack.pop();

            for (final AbstractHtml eachChild : children) {

                eachChild.sharedObject = sharedObject;

                // no need to add data-wff-id if the tag is not rendered by
                // BrowserPage (if it is rended by BrowserPage then
                // getLastDataWffId will not be -1)
                if (sharedObject.getLastDataWffId(ACCESS_OBJECT) != -1
                        && eachChild.getAttributeByName("data-wff-id") == null
                        && eachChild.getTagName() != null
                        && !eachChild.getTagName().isEmpty()) {
                    eachChild.addAttributes(false, new CustomAttribute(
                            "data-wff-id",
                            sharedObject.getNewDataWffId(ACCESS_OBJECT)));
                }

                final Set<AbstractHtml> subChildren = eachChild.children;

                if (subChildren != null && subChildren.size() > 0) {
                    childrenStack.push(subChildren);
                }

            }
        }

        child.parent = this;
    }

    public void appendChildren(final Collection<AbstractHtml> children) {

        final List<ChildMovedEvent> movedOrAppended = new LinkedList<ChildMovedEvent>();

        for (final AbstractHtml child : children) {
            final AbstractHtml previousParent = child.parent;

            addChild(child, false);

            final ChildMovedEvent event = new ChildMovedEvent(previousParent,
                    AbstractHtml.this, child);
            movedOrAppended.add(event);

        }

        final ChildTagAppendListener listener = sharedObject
                .getChildTagAppendListener(ACCESS_OBJECT);
        if (listener != null) {
            listener.childrendAppendedOrMoved(movedOrAppended);
        }
    }

    /**
     * initializes attributes in this.attributes and also in attributesMap. this
     * should be called only once per object.
     *
     * @param attributes
     * @since 1.2.0
     * @author WFF
     */
    private void initAttributes(final AbstractAttribute... attributes) {

        if (attributes == null || attributes.length == 0) {
            return;
        }

        attributesMap = new HashMap<String, AbstractAttribute>();

        for (final AbstractAttribute attribute : attributes) {
            attributesMap.put(attribute.getAttributeName(), attribute);
        }

        this.attributes = new AbstractAttribute[attributesMap.size()];
        attributesMap.values().toArray(this.attributes);
    }

    /**
     * adds the given attributes to this tag.
     *
     * @param attributes
     *            attributes to add
     * @since 1.2.0
     * @author WFF
     */
    public void addAttributes(final AbstractAttribute... attributes) {
        addAttributes(true, attributes);
    }

    /**
     * adds the given attributes to this tag.
     *
     * @param attributes
     *            attributes to add
     * @since 1.2.0
     * @author WFF
     */
    public void addAttributes(final Object accessObject,
            final boolean invokeListener,
            final AbstractAttribute... attributes) {
        if (accessObject == null || !(SecurityClassConstants.BROWSER_PAGE
                .equals(accessObject.getClass().getName()))) {
            throw new WffSecurityException(
                    "Not allowed to consume this method. This method is for internal use.");
        }
        addAttributes(invokeListener, attributes);
    }

    /**
     * adds the given attributes to this tag.
     *
     * @param invokeListener
     *            true to invoke listen
     * @param attributes
     *            attributes to add
     * @since 1.2.0
     * @author WFF
     */
    private void addAttributes(final boolean invokeListener,
            final AbstractAttribute... attributes) {

        if (attributesMap == null) {
            attributesMap = new HashMap<String, AbstractAttribute>();
        }

        if (this.attributes != null) {
            for (final AbstractAttribute attribute : this.attributes) {
                attributesMap.put(attribute.getAttributeName(), attribute);
            }
        }

        for (final AbstractAttribute attribute : attributes) {
            attribute.setOwnerTag(this);
            final AbstractAttribute previous = attributesMap
                    .put(attribute.getAttributeName(), attribute);
            if (previous != null && !attribute.equals(previous)) {
                previous.unsetOwnerTag(this);
            }
        }

        this.attributes = new AbstractAttribute[attributesMap.size()];
        attributesMap.values().toArray(this.attributes);
        setModified(true);

        // listener
        if (invokeListener) {
            final AttributeAddListener attributeAddListener = sharedObject
                    .getAttributeAddListener(ACCESS_OBJECT);
            if (attributeAddListener != null) {
                final AttributeAddListener.AddEvent event = new AttributeAddListener.AddEvent();
                event.setAddedToTag(AbstractHtml.this);
                event.setAddedAttributes(attributes);
                attributeAddListener.addedAttributes(event);
            }
        }

    }

    /**
     * @return the collection of attributes
     * @since 1.2.0
     * @author WFF
     */
    public Collection<AbstractAttribute> getAttributes() {
        if (attributesMap == null) {
            return null;
        }
        return Collections.unmodifiableCollection(attributesMap.values());
    }

    /**
     * gets the attribute by attribute name
     *
     * @return the attribute object for the given attribute name if exists
     *         otherwise returns null.
     * @since 1.2.0
     * @author WFF
     */
    public AbstractAttribute getAttributeByName(final String attributeName) {
        if (attributesMap == null) {
            return null;
        }
        return attributesMap.get(attributeName);
    }

    /**
     * removes the given attributes from this tag.
     *
     * @param attributes
     *            attributes to remove
     * @return true if any of the attributes are removed.
     * @since 1.2.0
     * @author WFF
     */
    public boolean removeAttributes(final AbstractAttribute... attributes) {
        return removeAttributes(true, attributes);
    }

    /**
     * removes the given attributes from this tag.
     *
     * @param invokeListener
     *            true to invoke listener
     * @param attributes
     *            attributes to remove
     * @return true if any of the attributes are removed.
     * @since 1.2.0
     * @author WFF
     */
    public boolean removeAttributes(final Object accessObject,
            final boolean invokeListener,
            final AbstractAttribute... attributes) {
        if (accessObject == null || !(SecurityClassConstants.BROWSER_PAGE
                .equals(accessObject.getClass().getName()))) {
            throw new WffSecurityException(
                    "Not allowed to consume this method. This method is for internal use.");

        }
        return removeAttributes(invokeListener, attributes);
    }

    /**
     * removes the given attributes from this tag.
     *
     * @param invokeListener
     *            true to invoke listener
     * @param attributes
     *            attributes to remove
     * @return true if any of the attributes are removed.
     * @since 1.2.0
     * @author WFF
     */
    private boolean removeAttributes(final boolean invokeListener,
            final AbstractAttribute... attributes) {

        if (attributesMap == null) {
            return false;
        }

        boolean removed = false;
        final List<String> removedAttributeNames = new LinkedList<String>();

        for (final AbstractAttribute attribute : attributes) {

            if (attribute.unsetOwnerTag(this)) {
                final String attributeName = attribute.getAttributeName();
                attributesMap.remove(attributeName);
                removed = true;
                removedAttributeNames.add(attributeName);
            }

        }

        if (removed) {
            this.attributes = new AbstractAttribute[attributesMap.size()];
            attributesMap.values().toArray(this.attributes);
            setModified(true);

            if (invokeListener) {
                final AttributeRemoveListener listener = sharedObject
                        .getAttributeRemoveListener(ACCESS_OBJECT);
                if (listener != null) {
                    final AttributeRemoveListener.RemovedEvent event = new AttributeRemoveListener.RemovedEvent(
                            AbstractHtml.this, removedAttributeNames.toArray(
                                    new String[removedAttributeNames.size()]));

                    listener.removedAttributes(event);
                }
            }
        }
        return removed;
    }

    /**
     * removes the given attributes from this tag.
     *
     * @param attributeNames
     *            to remove the attributes having in the given names.
     * @return true if any of the attributes are removed.
     * @since 1.2.0
     * @author WFF
     */
    public boolean removeAttributes(final String... attributeNames) {
        return removeAttributes(true, attributeNames);
    }

    /**
     * removes the given attributes from this tag.
     *
     * @param invokeListener
     *            true to invoke listener
     * @param attributeNames
     *            to remove the attributes having in the given names.
     * @return true if any of the attributes are removed.
     * @since 1.2.0
     * @author WFF
     */
    public boolean removeAttributes(final Object accessObject,
            final boolean invokeListener, final String... attributeNames) {
        if (accessObject == null || !(SecurityClassConstants.BROWSER_PAGE
                .equals(accessObject.getClass().getName()))) {
            throw new WffSecurityException(
                    "Not allowed to consume this method. This method is for internal use.");

        }
        return removeAttributes(invokeListener, attributeNames);
    }

    /**
     * removes the given attributes from this tag.
     *
     * @param invokeListener
     *            true to invoke listener
     * @param attributeNames
     *            to remove the attributes having in the given names.
     * @return true if any of the attributes are removed.
     * @since 1.2.0
     * @author WFF
     */
    private boolean removeAttributes(final boolean invokeListener,
            final String... attributeNames) {

        if (attributesMap == null) {
            return false;
        }

        boolean removed = false;

        for (final String attributeName : attributeNames) {

            final AbstractAttribute attribute = attributesMap
                    .get(attributeName);

            if (attribute != null) {
                attribute.unsetOwnerTag(this);
                attributesMap.remove(attributeName);
                removed = true;
            }

        }

        if (removed) {
            attributes = new AbstractAttribute[attributesMap.size()];
            attributesMap.values().toArray(attributes);
            setModified(true);

            if (invokeListener) {
                final AttributeRemoveListener listener = sharedObject
                        .getAttributeRemoveListener(ACCESS_OBJECT);
                if (listener != null) {
                    final AttributeRemoveListener.RemovedEvent event = new AttributeRemoveListener.RemovedEvent(
                            AbstractHtml.this, attributeNames);

                    listener.removedAttributes(event);
                }
            }
        }
        return removed;
    }

    /**
     * should be invoked to generate opening and closing tag base class
     * containing the functionalities to generate html string.
     *
     * @param tagType
     *
     * @param tagName
     *            TODO
     * @param base
     *            TODO
     * @author WFF
     */
    protected AbstractHtml(final TagType tagType, final String tagName,
            final AbstractHtml base, final AbstractAttribute[] attributes) {
        this.tagType = tagType;
        this.tagName = tagName;
        initAttributes(attributes);

        initInConstructor();

        markOwnerTag(attributes);

        buildOpeningTag(false);

        if (tagType == TagType.OPENING_CLOSING) {
            buildClosingTag();
        }

        if (base != null) {
            base.addChild(this);
            // base.children.add(this);
            // not required it is handled in the above add method
            // parent = base;
            // sharedObject = base.sharedObject;
        } else {
            sharedObject = new AbstractHtml5SharedObject();
        }
    }

    /**
     * marks the owner tag in the attributes
     *
     * @param attributes
     * @since 1.0.0
     * @author WFF
     */
    private void markOwnerTag(final AbstractAttribute[] attributes) {
        if (attributes == null) {
            return;
        }
        for (final AbstractAttribute abstractAttribute : attributes) {
            abstractAttribute.setOwnerTag(this);
        }
    }

    private void init() {
        children = new LinkedHashSet<AbstractHtml>() {

            private static final long serialVersionUID = 1L;

            @Override
            public boolean remove(final Object child) {

                final boolean removed = super.remove(child);
                // this method is getting called when removeAll method
                // is called.
                //

                return removed;
            }

            @Override
            public boolean removeAll(final Collection<?> children) {

                final AbstractHtml[] removedAbstractHtmls = children
                        .toArray(new AbstractHtml[children.size()]);

                final boolean removedAll = super.removeAll(children);
                if (removedAll) {

                    initNewSharedObjectInAllNestedTagsAndSetSuperParentNull(
                            removedAbstractHtmls);

                    final ChildTagRemoveListener listener = sharedObject
                            .getChildTagRemoveListener(ACCESS_OBJECT);

                    if (listener != null) {
                        listener.childrenRemoved(
                                new ChildTagRemoveListener.Event(
                                        AbstractHtml.this,
                                        removedAbstractHtmls));
                    }

                }
                return removedAll;
            }

            @Override
            public boolean retainAll(final Collection<?> c) {
                throw new MethodNotImplementedException(
                        "This method is not implemented yet, may be implemented in future");
            }

            @Override
            public void clear() {
                super.clear();
            }

            // @Override
            // public boolean add(AbstractHtml child) {
            // boolean added = super.add(child);
            // if (added) {
            // if (child.parent != null) {
            //
            // final Stack<Set<AbstractHtml>> childrenStack = new
            // Stack<Set<AbstractHtml>>();
            // childrenStack.push(new HashSet<AbstractHtml>(
            // Arrays.asList(child)));
            //
            // while (childrenStack.size() > 0) {
            //
            // final Set<AbstractHtml> children = childrenStack
            // .pop();
            //
            // for (final AbstractHtml eachChild : children) {
            //
            // eachChild.sharedObject = AbstractHtml.this.sharedObject;
            //
            // final Set<AbstractHtml> subChildren = eachChild
            // .getChildren();
            //
            // if (subChildren != null
            // && subChildren.size() > 0) {
            // childrenStack.push(subChildren);
            // }
            //
            // }
            // }
            //
            // } else {
            // child.sharedObject = AbstractHtml.this.sharedObject;
            // }
            //
            // child.parent = AbstractHtml.this;
            // final ChildTagAppendListener listener = child.sharedObject
            // .getChildTagAppendListener(ACCESS_OBJECT);
            // if (listener != null) {
            // final ChildTagAppendListener.Event event = new
            // ChildTagAppendListener.Event(
            // AbstractHtml.this, child);
            // listener.childAppended(event);
            // }
            //
            // }
            // return added;
            // }

            @Override
            public boolean addAll(
                    final Collection<? extends AbstractHtml> children) {
                throw new MethodNotImplementedException(
                        "This method is not implemented");
                // No need to implement as it will call add method
                // boolean addedAll = super.addAll(children);
                // if (addedAll) {
                //
                // for (AbstractHtml child : children) {
                // child.parent = AbstractHtml.this;
                // child.sharedObject = AbstractHtml.this.sharedObject;
                // final ChildTagAppendListener listener = child.sharedObject
                // .getChildTagAppendListener(ACCESS_OBJECT);
                // if (listener != null) {
                // final ChildTagAppendListener.Event event = new
                // ChildTagAppendListener.Event(
                // AbstractHtml.this, children);
                // listener.childAppended(event);
                // }
                // }
                //
                //
                // }
                // return super.addAll(children);
            }

        };
        tagBuilder = new StringBuilder();
        setRebuild(true);
    }

    /**
     * to initialize objects in the constructor
     *
     * @since 1.0.0
     * @author WFF
     */
    private void initInConstructor() {
        htmlStartSB = new StringBuilder(tagName == null ? 0
                : tagName.length() + 2
                        + ((attributes == null ? 0 : attributes.length) * 16));

        htmlEndSB = new StringBuilder(
                tagName == null ? 16 : tagName.length() + 3);
    }

    public AbstractHtml getParent() {
        return parent;
    }

    /**
     * @param parent
     * @since 1.2.0
     * @author WFF
     * @deprecated This method is not allowed to use. It's not implemented.
     */
    @Deprecated
    public void setParent(final AbstractHtml parent) {
        throw new MethodNotImplementedException(
                "This method is not implemented");
        // this.parent = parent;
    }

    /**
     * @return the unmodifiable list of children
     * @since 1.2.0
     * @author WFF
     */
    public List<AbstractHtml> getChildren() {
        return Collections
                .unmodifiableList(new ArrayList<AbstractHtml>(children));
    }

    /**
     * NB: this method is for internal use. The returned object should not be
     * modified.
     *
     * @return the internal children object.
     * @since 1.2.0
     * @author WFF
     */
    public Set<AbstractHtml> getChildren(final Object accessObject) {

        if (accessObject == null || !(SecurityClassConstants.BROWSER_PAGE
                .equals(accessObject.getClass().getName()))) {
            throw new WffSecurityException(
                    "Not allowed to consume this method. This method is for internal use.");
        }

        return children;
    }

    public void setChildren(final Set<AbstractHtml> children) {
        this.children = children;
    }

    public String getOpeningTag() {
        if (isRebuild() || isModified()) {
            buildOpeningTag(true);
        }
        return openingTag;
    }

    public String getClosingTag() {
        return closingTag;
    }

    /**
     * @return {@code String} equalent to the html string of the tag including
     *         the child tags.
     * @since 1.0.0
     * @author WFF
     */
    protected String getPrintStructure() {
        String printStructure = null;
        if (isRebuild() || isModified()) {
            printStructure = getPrintStructure(true);
            setRebuild(false);
        } else {
            printStructure = tagBuilder.toString();
        }
        return printStructure;
    }

    /**
     * @param rebuild
     * @return
     * @since 1.0.0
     * @author WFF
     */
    protected String getPrintStructure(final boolean rebuild) {
        if (rebuild || isRebuild() || isModified()) {
            beforePrintStructure();
            tagBuilder.delete(0, tagBuilder.length());
            recurChildren(new LinkedHashSet<AbstractHtml>(Arrays.asList(this)),
                    true);
            setRebuild(false);
        }
        tagBuilder.trimToSize();
        return tagBuilder.toString();
    }

    /**
     * @param rebuild
     * @since 1.0.0
     * @author WFF
     * @throws IOException
     */
    protected void writePrintStructureToOutputStream(final boolean rebuild)
            throws IOException {
        beforeWritePrintStructureToOutputStream();
        recurChildrenToOutputStream(
                new LinkedHashSet<AbstractHtml>(Arrays.asList(this)), true);
    }

    // for future development
    /**
     * @param rebuild
     * @since 1.2.0
     * @author WFF
     * @throws IOException
     */
    protected void writePrintStructureToWffBinaryMessageOutputStream(
            final boolean rebuild) throws IOException {
        beforeWritePrintStructureToWffBinaryMessageOutputStream();
        recurChildrenToWffBinaryMessageOutputStream(
                new LinkedHashSet<AbstractHtml>(Arrays.asList(this)), true);
    }

    /**
     * to form html string from the children
     *
     * @param children
     * @param rebuild
     *            TODO
     * @since 1.0.0
     * @author WFF
     */
    private void recurChildren(final Set<AbstractHtml> children,
            final boolean rebuild) {
        if (children != null && children.size() > 0) {
            for (final AbstractHtml child : children) {
                child.setRebuild(rebuild);
                tagBuilder.append(child.getOpeningTag());

                final Set<AbstractHtml> childrenOfChildren = child.children;

                recurChildren(childrenOfChildren, rebuild);

                tagBuilder.append(child.closingTag);
            }
        }
    }

    /**
     * to form html string from the children
     *
     * @param children
     * @param rebuild
     *            TODO
     * @since 1.0.0
     * @author WFF
     * @throws IOException
     */
    private void recurChildrenToOutputStream(final Set<AbstractHtml> children,
            final boolean rebuild) throws IOException {

        if (children != null && children.size() > 0) {
            for (final AbstractHtml child : children) {
                child.setRebuild(rebuild);
                outputStream.write(child.getOpeningTag().getBytes(charset));

                final Set<AbstractHtml> childrenOfChildren = child.children;

                recurChildrenToOutputStream(childrenOfChildren, rebuild);
                outputStream.write(child.closingTag.getBytes(charset));
            }
        }
    }

    // for future development
    /**
     * to form html string from the children
     *
     * @param children
     * @param rebuild
     *            TODO
     * @since 1.2.0
     * @author WFF
     * @throws IOException
     */
    private void recurChildrenToWffBinaryMessageOutputStream(
            final Set<AbstractHtml> children, final boolean rebuild)
                    throws IOException {
        if (children != null && children.size() > 0) {
            for (final AbstractHtml child : children) {
                child.setRebuild(rebuild);
                // wffBinaryMessageOutputStreamer

                // outputStream.write(child.getOpeningTag().getBytes(charset));

                final NameValue nameValue = new NameValue();

                final int tagNameIndex = TagRegistry.getTagNames()
                        .indexOf(child.getTagName());

                // if the tag index is -1 i.e. it's not indexed then the tag
                // name prepended with 0 value byte should be set.
                // If the first byte == 0 and length is greater than 1 then it's
                // a tag name, if the first byte is greater than 0 then it is
                // index bytes

                byte[] closingTagNameConvertedBytes = null;
                if (tagNameIndex == -1) {

                    final byte[] tagNameBytes = child.getTagName()
                            .getBytes(charset);

                    final byte[] nameBytes = new byte[tagNameBytes.length + 1];

                    nameBytes[0] = 0;

                    System.arraycopy(tagNameBytes, 0, nameBytes, 1,
                            tagNameBytes.length);
                    nameValue.setName(nameBytes);
                    closingTagNameConvertedBytes = nameBytes;
                } else {
                    final byte[] indexBytes = WffBinaryMessageUtil
                            .getOptimizedBytesFromInt(tagNameIndex);
                    nameValue.setName(indexBytes);
                    closingTagNameConvertedBytes = WffBinaryMessageUtil
                            .getOptimizedBytesFromInt((tagNameIndex * (-1)));
                }

                nameValue.setValues(
                        child.getAttributeHtmlBytesCompressedByIndex(rebuild,
                                charset));

                wffBinaryMessageOutputStreamer.write(nameValue);

                final Set<AbstractHtml> childrenOfChildren = child.children;

                recurChildrenToWffBinaryMessageOutputStream(childrenOfChildren,
                        rebuild);

                final NameValue closingTagNameValue = new NameValue();
                closingTagNameValue.setName(closingTagNameConvertedBytes);
                closingTagNameValue.setValues(new byte[0][0]);
                wffBinaryMessageOutputStreamer.write(closingTagNameValue);

                // outputStream.write(child.closingTag.getBytes(charset));
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.webfirmframework.wffweb.tag.TagBase#toHtmlString()
     *
     * @since 1.0.0
     *
     * @author WFF
     */
    @Override
    public String toHtmlString() {
        final String printStructure = getPrintStructure(
                getSharedObject().isChildModified()
                        && !getSharedObject().getRebuiltTags().contains(this));

        if (parent == null) {
            getSharedObject().setChildModified(false);
        } else {
            getSharedObject().getRebuiltTags().add(this);
        }
        return printStructure;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.webfirmframework.wffweb.tag.core.TagBase#toHtmlString(java.nio.
     * charset.Charset)
     */
    @Override
    public String toHtmlString(final Charset charset) {
        final Charset previousCharset = this.charset;
        try {
            this.charset = charset;
            // assigning it to new variable is very important here as this
            // line of code should invoke before finally block
            final String htmlString = toHtmlString();
            return htmlString;
        } finally {
            this.charset = previousCharset;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.webfirmframework.wffweb.tag.core.TagBase#toHtmlString(java.lang.
     * String)
     */
    @Override
    public String toHtmlString(final String charset) {
        final Charset previousCharset = this.charset;
        try {
            this.charset = Charset.forName(charset);
            // assigning it to new variable is very important here as this
            // line of code should invoke before finally block
            final String htmlString = toHtmlString();
            return htmlString;
        } finally {
            this.charset = previousCharset;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.webfirmframework.wffweb.tag.TagBase#toHtmlString(boolean)
     *
     * @since 1.0.0
     *
     * @author WFF
     */
    @Override
    public String toHtmlString(final boolean rebuild) {
        return getPrintStructure(rebuild);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.webfirmframework.wffweb.tag.core.TagBase#toHtmlString(boolean,
     * java.nio.charset.Charset)
     */
    @Override
    public String toHtmlString(final boolean rebuild, final Charset charset) {
        final Charset previousCharset = this.charset;
        try {
            this.charset = charset;
            // assigning it to new variable is very important here as this
            // line of code should invoke before finally block
            final String htmlString = toHtmlString(rebuild);
            return htmlString;
        } finally {
            this.charset = previousCharset;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.webfirmframework.wffweb.tag.core.TagBase#toHtmlString(boolean,
     * java.lang.String)
     */
    @Override
    public String toHtmlString(final boolean rebuild, final String charset) {
        final Charset previousCharset = this.charset;
        try {
            this.charset = Charset.forName(charset);
            // assigning it to new variable is very important here as this
            // line of code should invoke before finally block
            final String htmlString = toHtmlString(rebuild);
            return htmlString;
        } finally {
            this.charset = previousCharset;
        }
    }

    // TODO for future implementation
    /**
     * @param os
     *            the object of {@code OutputStream} to write to.
     * @throws IOException
     * @Deprecated this method is for future implementation so it should not be
     *             consumed
     */
    @Deprecated
    void toOutputStream(final boolean asWffBinaryMessage, final OutputStream os)
            throws IOException {
        if (asWffBinaryMessage) {
            try {
                wffBinaryMessageOutputStreamer = new WffBinaryMessageOutputStreamer(
                        os);
                writePrintStructureToWffBinaryMessageOutputStream(true);
            } finally {
                wffBinaryMessageOutputStreamer = null;
            }
        } else {
            toOutputStream(os);
        }
    }

    /**
     * @param os
     *            the object of {@code OutputStream} to write to.
     * @throws IOException
     */
    public void toOutputStream(final OutputStream os) throws IOException {
        try {
            outputStream = os;
            writePrintStructureToOutputStream(true);
        } finally {
            outputStream = null;
        }
    }

    /**
     * @param os
     *            the object of {@code OutputStream} to write to.
     * @param charset
     *            the charset
     * @throws IOException
     */
    public void toOutputStream(final OutputStream os, final Charset charset)
            throws IOException {
        final Charset previousCharset = this.charset;
        try {
            this.charset = charset;
            outputStream = os;
            writePrintStructureToOutputStream(true);
        } finally {
            outputStream = null;
            this.charset = previousCharset;
        }
    }

    /**
     * @param os
     *            the object of {@code OutputStream} to write to.
     * @param charset
     *            the charset
     * @throws IOException
     */
    public void toOutputStream(final OutputStream os, final String charset)
            throws IOException {
        final Charset previousCharset = this.charset;
        try {
            this.charset = Charset.forName(charset);
            outputStream = os;
            writePrintStructureToOutputStream(true);
        } finally {
            outputStream = null;
            this.charset = previousCharset;
        }
    }

    /**
     * @param os
     *            the object of {@code OutputStream} to write to.
     * @param rebuild
     *            true to rebuild & false to write previously built bytes.
     *
     * @throws IOException
     */
    public void toOutputStream(final OutputStream os, final boolean rebuild)
            throws IOException {
        try {
            outputStream = os;
            writePrintStructureToOutputStream(rebuild);
        } finally {
            outputStream = null;
        }
    }

    /**
     * @param os
     *            the object of {@code OutputStream} to write to.
     * @param rebuild
     *            true to rebuild & false to write previously built bytes.
     * @param charset
     *            the charset
     * @throws IOException
     */
    public void toOutputStream(final OutputStream os, final boolean rebuild,
            final Charset charset) throws IOException {
        final Charset previousCharset = this.charset;
        try {
            this.charset = charset;
            outputStream = os;
            writePrintStructureToOutputStream(rebuild);
        } finally {
            outputStream = null;
            this.charset = previousCharset;
        }
    }

    /**
     * @param os
     *            the object of {@code OutputStream} to write to.
     * @param rebuild
     *            true to rebuild & false to write previously built bytes.
     * @param charset
     *            the charset
     * @throws IOException
     */
    public void toOutputStream(final OutputStream os, final boolean rebuild,
            final String charset) throws IOException {
        final Charset previousCharset = this.charset;
        try {
            this.charset = Charset.forName(charset);
            outputStream = os;
            writePrintStructureToOutputStream(rebuild);
        } finally {
            outputStream = null;
            this.charset = previousCharset;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    // it is not a best practice to print html string by this method because if
    // it is used in ThreadLocal class it may cause memory leak.
    @Override
    public String toString() {
        return super.toString();
    }

    /**
     * Eg tag names :- html, body, div table, input, button etc...
     *
     * @return the tagName set by {@code AbstractHtml5#setTagName(String)}
     *         method.
     * @since 1.0.0
     * @author WFF
     */
    public String getTagName() {
        return tagName;
    }

    public byte[][] getAttributeHtmlBytesCompressedByIndex(
            final boolean rebuild, final Charset charset) throws IOException {
        return AttributeUtil.getAttributeHtmlBytesCompressedByIndex(rebuild,
                charset, attributes);
    }

    /**
     *
     * @param rebuild
     *            TODO
     * @since 1.0.0
     * @author WFF
     */
    private void buildOpeningTag(final boolean rebuild) {
        final String attributeHtmlString = AttributeUtil
                .getAttributeHtmlString(rebuild, charset, attributes);
        htmlStartSB.delete(0, htmlStartSB.length());
        if (tagName != null) {
            htmlStartSB.append('<');
            htmlStartSB.append(tagName);
            htmlStartSB.append(attributeHtmlString);
            if (tagType == TagType.OPENING_CLOSING) {
                htmlStartSB.append('>');
            } else if (tagType == TagType.SELF_CLOSING) {
                htmlStartSB.append(new char[] { '/', '>' });
            } else {
                // here it will be tagType == TagType.NON_CLOSING as there are
                // three types in TagType class
                htmlStartSB.append('>');
            }
            htmlStartSB.trimToSize();
            openingTag = htmlStartSB.toString();
        } else {
            htmlStartSB.trimToSize();
            openingTag = "";
        }
    }

    /**
     *
     * @since 1.0.0
     * @author WFF
     */
    private void buildClosingTag() {
        htmlEndSB.delete(0, htmlEndSB.length());
        if (tagName != null) {
            htmlEndSB.append(new char[] { '<', '/' });
            htmlEndSB.append(tagName);
            htmlEndSB.append('>');
        } else {
            if (htmlStartSB != null) {
                htmlEndSB.append(getHtmlMiddleSB());
            }
        }
        htmlEndSB.trimToSize();
        closingTag = htmlEndSB.toString();
    }

    /**
     * @return the sharedObject
     * @since 1.0.0
     * @author WFF
     */
    public AbstractHtml5SharedObject getSharedObject() {
        return sharedObject;
    }

    /**
     * @return the htmlMiddleSB
     * @since 1.0.0
     * @author WFF
     */
    protected StringBuilder getHtmlMiddleSB() {
        if (htmlMiddleSB == null) {
            htmlMiddleSB = new StringBuilder();
        }
        return htmlMiddleSB;
    }

    /**
     * @return the htmlStartSBAsFirst
     * @since 1.0.0
     * @author WFF
     */
    public boolean isHtmlStartSBAsFirst() {
        return htmlStartSBAsFirst;
    }

    protected AbstractHtml deepClone(final AbstractHtml objectToBeClonned)
            throws CloneNotSupportedException {
        return CloneUtil.<AbstractHtml> deepClone(objectToBeClonned);
    }

    /**
     * invokes just before {@code getPrintStructure(final boolean} method and
     * only if the getPrintStructure(final boolean} rebuilds the structure.
     *
     * @since 1.0.0
     * @author WFF
     */
    protected void beforePrintStructure() {
        // NOP override and use
    }

    /**
     * invokes just before
     * {@code writePrintStructureToOutputStream(final OutputStream} method.
     *
     * @since 1.0.0
     * @author WFF
     */
    protected void beforeWritePrintStructureToOutputStream() {
        // NOP override and use
    }

    /**
     * invokes just before
     * {@code writePrintStructureToWffBinaryMessageOutputStream(final OutputStream}
     * method.
     *
     * @since 1.2.0
     * @author WFF
     */
    protected void beforeWritePrintStructureToWffBinaryMessageOutputStream() {
        // NOP override and use
    }

    /**
     * Creates and returns a deeply cloned copy of this object. The precise
     * meaning of "copy" may depend on the class of the object. The general
     * intent is that, for any object {@code x}, the expression: <blockquote>
     *
     * <pre>
     * x.clone() != x
     * </pre>
     *
     * </blockquote> will be true, and that the expression: <blockquote>
     *
     * <pre>
     * x.clone().getClass() == x.getClass()
     * </pre>
     *
     * </blockquote> will be {@code true}, but these are not absolute
     * requirements. While it is typically the case that: <blockquote>
     *
     * <pre>
     * x.clone().equals(x)
     * </pre>
     *
     * </blockquote> will be {@code true}, this is not an absolute requirement.
     * <p>
     * By convention, the returned object should be obtained by calling
     * {@code super.clone}. If a class and all of its superclasses (except
     * {@code Object}) obey this convention, it will be the case that
     * {@code x.clone().getClass() == x.getClass()}.
     * <p>
     * By convention, the object returned by this method should be independent
     * of this object (which is being cloned). To achieve this independence, it
     * may be necessary to modify one or more fields of the object returned by
     * {@code super.clone} before returning it. Typically, this means copying
     * any mutable objects that comprise the internal "deep structure" of the
     * object being cloned and replacing the references to these objects with
     * references to the copies. If a class contains only primitive fields or
     * references to immutable objects, then it is usually the case that no
     * fields in the object returned by {@code super.clone} need to be modified.
     * <p>
     * The method {@code clone} for class {@code AbstractHtml} performs a
     * specific cloning operation. First, if the class of this object does not
     * implement the interfaces {@code Cloneable} and {@code Serializable}, then
     * a {@code CloneNotSupportedException} is thrown. Note that all arrays are
     * considered to implement the interface {@code Cloneable} and that the
     * return type of the {@code clone} method of an array type {@code T[]} is
     * {@code T[]} where T is any reference or primitive type. Otherwise, this
     * method creates a new instance of the class of this object and initializes
     * all its fields with exactly the contents of the corresponding fields of
     * this object, as if by assignment; the contents of the fields are not
     * themselves cloned. Thus, this method performs a "shallow copy" of this
     * object, not a "deep copy" operation.
     *
     * @return a deep clone of this instance.
     * @exception CloneNotSupportedException
     *                if the object's class does not support the
     *                {@code Cloneable} and {@code Serializable} interfaces.
     *                Subclasses that override the {@code clone} method can also
     *                throw this exception to indicate that an instance cannot
     *                be cloned.
     * @see java.lang.Cloneable
     * @see java.io.Serializable
     * @author WFF
     */
    @Override
    public AbstractHtml clone() throws CloneNotSupportedException {
        return deepClone(this);
    }

    /**
     * Creates and returns a deeply cloned copy of this object. The precise
     * meaning of "copy" may depend on the class of the object. The general
     * intent is that, for any object {@code x}, the expression: <blockquote>
     *
     * <pre>
     * x.clone() != x
     * </pre>
     *
     * </blockquote> will be true, and that the expression: <blockquote>
     *
     * <pre>
     * x.clone().getClass() == x.getClass()
     * </pre>
     *
     * </blockquote> will be {@code true}, but these are not absolute
     * requirements. While it is typically the case that: <blockquote>
     *
     * <pre>
     * x.clone().equals(x)
     * </pre>
     *
     * </blockquote> will be {@code true}, this is not an absolute requirement.
     * <p>
     * By convention, the returned object should be obtained by calling
     * {@code super.clone}. If a class and all of its superclasses (except
     * {@code Object}) obey this convention, it will be the case that
     * {@code x.clone().getClass() == x.getClass()}.
     * <p>
     * By convention, the object returned by this method should be independent
     * of this object (which is being cloned). To achieve this independence, it
     * may be necessary to modify one or more fields of the object returned by
     * {@code super.clone} before returning it. Typically, this means copying
     * any mutable objects that comprise the internal "deep structure" of the
     * object being cloned and replacing the references to these objects with
     * references to the copies. If a class contains only primitive fields or
     * references to immutable objects, then it is usually the case that no
     * fields in the object returned by {@code super.clone} need to be modified.
     * <p>
     * The method {@code clone} for class {@code AbstractHtml} performs a
     * specific cloning operation. First, if the class of this object does not
     * implement the interfaces {@code Cloneable} and {@code Serializable}, then
     * a {@code CloneNotSupportedException} is thrown. Note that all arrays are
     * considered to implement the interface {@code Cloneable} and that the
     * return type of the {@code clone} method of an array type {@code T[]} is
     * {@code T[]} where T is any reference or primitive type. Otherwise, this
     * method creates a new instance of the class of this object and initializes
     * all its fields with exactly the contents of the corresponding fields of
     * this object, as if by assignment; the contents of the fields are not
     * themselves cloned. Thus, this method performs a "shallow copy" of this
     * object, not a "deep copy" operation.
     *
     * @param excludeAttributes
     *            pass the attributes names which need to be excluded from all
     *            tags including their child tags.
     *
     * @return a deep clone of this instance without the given attributes.
     * @exception CloneNotSupportedException
     *                if the object's class does not support the
     *                {@code Cloneable} and {@code Serializable} interfaces.
     *                Subclasses that override the {@code clone} method can also
     *                throw this exception to indicate that an instance cannot
     *                be cloned.
     * @see java.lang.Cloneable
     * @see java.io.Serializable
     * @author WFF
     * @since 1.2.0
     */
    public AbstractHtml clone(final String... excludeAttributes)
            throws CloneNotSupportedException {

        final AbstractHtml clonedObject = deepClone(this);

        final Stack<Set<AbstractHtml>> childrenStack = new Stack<Set<AbstractHtml>>();
        childrenStack.push(
                new LinkedHashSet<AbstractHtml>(Arrays.asList(clonedObject)));

        while (childrenStack.size() > 0) {

            final Set<AbstractHtml> children = childrenStack.pop();

            for (final AbstractHtml child : children) {
                child.removeAttributes(excludeAttributes);

                final Set<AbstractHtml> subChildren = child.children;
                if (subChildren != null && subChildren.size() > 0) {
                    childrenStack.push(subChildren);
                }
            }

        }

        return clonedObject;
    }

    /**
     * @return the charset
     */
    public Charset getCharset() {
        return charset;
    }

    /**
     * @param charset
     *            the charset to set
     */
    public void setCharset(final Charset charset) {
        this.charset = charset;
    }

    private void initNewSharedObjectInAllNestedTagsAndSetSuperParentNull(
            final AbstractHtml[] removedAbstractHtmls) {
        for (final AbstractHtml abstractHtml : removedAbstractHtmls) {
            initNewSharedObjectInAllNestedTagsAndSetSuperParentNull(
                    abstractHtml);

        }
    }

    private void initNewSharedObjectInAllNestedTagsAndSetSuperParentNull(
            final AbstractHtml abstractHtml) {

        abstractHtml.parent = null;
        abstractHtml.sharedObject = new AbstractHtml5SharedObject();

        final Stack<Set<AbstractHtml>> removedTagsStack = new Stack<Set<AbstractHtml>>();
        removedTagsStack
                .push(new HashSet<AbstractHtml>(Arrays.asList(abstractHtml)));

        while (removedTagsStack.size() > 0) {

            final Set<AbstractHtml> stackChildren = removedTagsStack.pop();

            for (final AbstractHtml stackChild : stackChildren) {
                stackChild.sharedObject = abstractHtml.sharedObject;

                final Set<AbstractHtml> subChildren = stackChild.children;

                if (subChildren != null && subChildren.size() > 0) {
                    removedTagsStack.push(subChildren);
                }
            }

        }
    }
}
