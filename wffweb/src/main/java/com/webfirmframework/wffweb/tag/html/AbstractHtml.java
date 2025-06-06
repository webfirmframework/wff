/*
 * Copyright since 2014 Web Firm Framework
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
import java.io.Serial;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.webfirmframework.wffweb.InvalidTagException;
import com.webfirmframework.wffweb.InvalidUsageException;
import com.webfirmframework.wffweb.InvalidValueException;
import com.webfirmframework.wffweb.MethodNotImplementedException;
import com.webfirmframework.wffweb.NoParentException;
import com.webfirmframework.wffweb.WffRuntimeException;
import com.webfirmframework.wffweb.WffSecurityException;
import com.webfirmframework.wffweb.clone.CloneUtil;
import com.webfirmframework.wffweb.common.URIEvent;
import com.webfirmframework.wffweb.internal.InternalId;
import com.webfirmframework.wffweb.internal.ObjectId;
import com.webfirmframework.wffweb.internal.constants.CommonConstants;
import com.webfirmframework.wffweb.internal.constants.IndexedClassType;
import com.webfirmframework.wffweb.internal.security.object.AbstractHtmlSecurity;
import com.webfirmframework.wffweb.internal.security.object.SecurityObject;
import com.webfirmframework.wffweb.internal.tag.html.listener.AttributeAddListener;
import com.webfirmframework.wffweb.internal.tag.html.listener.AttributeRemoveListener;
import com.webfirmframework.wffweb.internal.tag.html.listener.ChildTagAppendListener;
import com.webfirmframework.wffweb.internal.tag.html.listener.ChildTagAppendListener.ChildMovedEvent;
import com.webfirmframework.wffweb.internal.tag.html.listener.ChildTagRemoveListener;
import com.webfirmframework.wffweb.internal.tag.html.listener.InnerHtmlAddListener;
import com.webfirmframework.wffweb.internal.tag.html.listener.InsertAfterListener;
import com.webfirmframework.wffweb.internal.tag.html.listener.InsertTagsBeforeListener;
import com.webfirmframework.wffweb.internal.tag.html.listener.PushQueue;
import com.webfirmframework.wffweb.internal.tag.html.listener.ReplaceListener;
import com.webfirmframework.wffweb.internal.tag.html.listener.URIChangeTagSupplier;
import com.webfirmframework.wffweb.server.page.BrowserPage;
import com.webfirmframework.wffweb.settings.WffConfiguration;
import com.webfirmframework.wffweb.streamer.WffBinaryMessageOutputStreamer;
import com.webfirmframework.wffweb.tag.core.AbstractJsObject;
import com.webfirmframework.wffweb.tag.html.attribute.core.AbstractAttribute;
import com.webfirmframework.wffweb.tag.html.attribute.core.AttributeRegistry;
import com.webfirmframework.wffweb.tag.html.attribute.core.AttributeUtil;
import com.webfirmframework.wffweb.tag.html.attributewff.CustomAttribute;
import com.webfirmframework.wffweb.tag.html.core.PreIndexedTagName;
import com.webfirmframework.wffweb.tag.html.core.TagRegistry;
import com.webfirmframework.wffweb.tag.html.html5.attribute.global.DataWffId;
import com.webfirmframework.wffweb.tag.html.listener.ParentGainedListener;
import com.webfirmframework.wffweb.tag.html.listener.ParentLostListener;
import com.webfirmframework.wffweb.tag.html.model.AbstractHtml5SharedObject;
import com.webfirmframework.wffweb.tag.htmlwff.CustomTag;
import com.webfirmframework.wffweb.tag.htmlwff.NoTag;
import com.webfirmframework.wffweb.tag.repository.TagRepository;
import com.webfirmframework.wffweb.util.NumberUtil;
import com.webfirmframework.wffweb.util.StringUtil;
import com.webfirmframework.wffweb.util.WffBinaryMessageUtil;
import com.webfirmframework.wffweb.util.data.NameValue;
import com.webfirmframework.wffweb.wffbm.data.WffBMArray;
import com.webfirmframework.wffweb.wffbm.data.WffBMData;
import com.webfirmframework.wffweb.wffbm.data.WffBMObject;

/**
 * @author WFF
 * @version 3.0.1
 * @since 1.0.0
 */
public abstract non-sealed class AbstractHtml extends AbstractJsObject implements URIStateSwitch {

    // if this class' is refactored then SecurityClassConstants should be
    // updated.

    @Serial
    private static final long serialVersionUID = 3_0_18L;

    private static final SecurityObject ACCESS_OBJECT;

    // initial value must be -1 if not assigning any value if int
    // or null if byte[]
    private final byte[] tagNameIndexBytes;

    // its length will be always 1
    private static final byte[] INDEXED_AT_CHAR_BYTES;

    // its length will be always 1
    private static final byte[] INDEXED_HASH_CHAR_BYTES;

    /**
     * its length will be always 1. to represent int bytes to interpret as TEXT but
     * to represent noTagContentTypeHtml = true
     */
    private static final byte[] INDEXED_DOLLAR_CHAR_BYTES;

    /**
     * its length will be always 1. to represent int bytes to interpret as TEXT but
     * noTagContentTypeHtml = false
     */
    private static final byte[] INDEXED_PERCENT_CHAR_BYTES;

    private volatile AbstractHtml parent;

    private volatile boolean parentNullifiedOnce;

    /**
     * NB: iterator in this children is not synchronized so for-each loop may make
     * ConcurrentModificationException if the children object is not used in
     * synchronized block eg: synchronized (children) {} if possible replace this
     * with a new implementation of a concurrent linked hashset. Unfortunately, jdk
     * doesn't contain such class upto java 11. Solved: children is surrounded by
     * lock in its top outer method.
     */
    private final Set<AbstractHtml> children;

    private String openingTag;

    // should be initialized with empty string
    private final String closingTag;

    private final StringBuilder htmlStartSB;

    /**
     * NB: it should never be nullified after initialization
     */
    private volatile StringBuilder htmlMiddleSB;

//    private StringBuilder htmlEndSB;

    private final String tagName;

    private final StringBuilder tagBuilder = new StringBuilder();

    private volatile AbstractAttribute[] attributes;

    private volatile Map<String, AbstractAttribute> attributesMap;

    // NB never assign null, it should never be null
    private volatile AbstractHtml5SharedObject sharedObject;

    private final boolean htmlStartSBAsFirst;

    // for future development
    private WffBinaryMessageOutputStreamer wffBinaryMessageOutputStreamer;

    // only for toWffBMBytes method
    private int wffSlotIndex = -1;

    private volatile DataWffId dataWffId;

    // default must be TagType.OPENING_CLOSING
    private final TagType tagType;

    protected final boolean noTagContentTypeHtml;

    // just for caching formatter in NoTag object
    private Object cachedStcFormatter;

    @SuppressWarnings("rawtypes")
    transient volatile SharedTagContent sharedTagContent;

    private transient volatile Boolean sharedTagContentSubscribed;

    private final InternalId internalId = new InternalId();

    private volatile URIEvent lastURIEvent;

    private volatile int lastWhenURIIndex = -1;

    private volatile boolean lastWhenURISuccess = false;

    private volatile URIChangeContent lastURIChangeContent = null;

    private volatile Boolean lastURIPredicateTest = null;

    volatile long hierarchyOrder;

    private long hierarchyOrderCounter;

    private volatile ObjectId hierarchicalLoopId;

    private volatile List<URIChangeContent> uriChangeContents;

    private volatile ParentLostListenerVariables parentLostListenerVariables;

    private volatile ParentGainedListenerVariables parentGainedListenerVariables;

    public static enum TagType {
        OPENING_CLOSING, SELF_CLOSING, NON_CLOSING;

        private TagType() {
        }
    }

    /**
     * Note: Only for internal use.
     *
     */
    // for security purpose, the class name should not be modified
    private static final class Security {
        private Security() {
            if (ACCESS_OBJECT != null) {
                throw new AssertionError("Not allowed to call this constructor");
            }
        }
    }

    private enum WhenURIMethodType {

        SUCCESS_SUPPLIER_FAIL_CONSUMER,

        SUCCESS_CONSUMER_FAIL_SUPPLIER,

        SUCCESS_SUPPLIER_FAIL_SUPPLIER,

        SUCCESS_CONSUMER_FAIL_CONSUMER;
    }

    private static record URIChangeContent(Predicate<URIEvent> uriEventPredicate, Supplier<AbstractHtml[]> successTags,
            Supplier<AbstractHtml[]> failTags, Consumer<TagEvent> successConsumer, Consumer<TagEvent> failConsumer,
            WhenURIMethodType methodType, WhenURIProperties whenURIProperties) implements Serializable {
    }

    /**
     * @since 12.0.1
     */
    private record TagContractRecord(AbstractHtml tag, AbstractHtml5SharedObject sharedObject) {

        /**
         * @return objectId
         */
        private ObjectId objectId() {
            return sharedObject.objectId();
        }

        @SuppressWarnings("unused")
        private boolean isValid(final AbstractHtml5SharedObject latestSharedObject) {
            if (sharedObject.equals(tag.getSharedObjectLockless()) || (latestSharedObject == null)) {
                return true;
            }
            return tag.getSharedObjectLockless().objectId().compareTo(latestSharedObject.objectId()) >= 0;
        }

        @Override
        public String toString() {
            return sharedObject.objectId().id() + ":" + tag.internalId();
        }
    }

    static {
        ACCESS_OBJECT = new AbstractHtmlSecurity(new Security());

        // its length will be always 1
        INDEXED_AT_CHAR_BYTES = PreIndexedTagName.AT.internalIndexBytes(ACCESS_OBJECT);

        // its length will be always 1
        INDEXED_HASH_CHAR_BYTES = PreIndexedTagName.HASH.internalIndexBytes(ACCESS_OBJECT);

        // its length will be always 1
        INDEXED_DOLLAR_CHAR_BYTES = PreIndexedTagName.DOLLAR.internalIndexBytes(ACCESS_OBJECT);

        INDEXED_PERCENT_CHAR_BYTES = PreIndexedTagName.PERCENT.internalIndexBytes(ACCESS_OBJECT);

    }

    {
        // NB: iterator in this children is not synchronized
        // so for-each loop may make ConcurrentModificationException
        // if the children object is not used in synchronized block
        // eg: synchronized (children) {}
        // if possible replace this with a new implementation of
        // a concurrent linked hashset. Unfortunately, jdk doesn't contain
        // such class upto java 11
        // Solved: children is surrounded by lock in its top outer method.
        children = new LinkedHashSet<AbstractHtml>() {

            @Serial
            private static final long serialVersionUID = 2L;

            @Override
            public boolean remove(final Object child) {

                final boolean removed = super.remove(child);
                // this method is getting called when removeAll method
                // is called.
                //

                if (removed) {
                    sharedObject.setChildModified(removed, ACCESS_OBJECT);
                }

                return removed;
            }

            @Override
            public boolean add(final AbstractHtml e) {
                final boolean added = super.add(e);
                if (added) {
                    sharedObject.setChildModified(added, ACCESS_OBJECT);
                }
                return added;
            }

//            @Override
//            public boolean removeAll(final Collection<?> children) {
//
//                // NB: must be LinkedHashSet to keep order of removal
//                final Set<AbstractHtml> validChildren = new LinkedHashSet<>(calcCapacity(children.size()));
//
//                for (final Object each : children) {
//                    if (each instanceof AbstractHtml child) {
//                        if (AbstractHtml.this.equals(child.parent)) {
//                            validChildren.add(child);
//                        }
//                    }
//                }
//
//                final AbstractHtml[] removedAbstractHtmls = validChildren
//                        .toArray(new AbstractHtml[validChildren.size()]);
//                removeFromSharedTagContent(removedAbstractHtmls);
//
//                final boolean removedAll = super.removeAll(validChildren);
//
//                if (removedAll) {
//                    final List<Lock> newSOLocks = initNewSharedObjectInAllNestedTagsAndSetSuperParentNull(
//                            removedAbstractHtmls);
//
//                    try {
//                        final ChildTagRemoveListener listener = sharedObject.getChildTagRemoveListener(ACCESS_OBJECT);
//
//                        if (listener != null) {
//                            listener.childrenRemoved(
//                                    new ChildTagRemoveListener.Event(AbstractHtml.this, null, removedAbstractHtmls));
//                        }
//
//                        sharedObject.setChildModified(removedAll, ACCESS_OBJECT);
//                    } finally {
//                        for (final Lock newSOLock : newSOLocks) {
//                            newSOLock.unlock();
//                        }
//                    }
//
//                }
//
//                return removedAll;
//            }

            @Override
            public boolean retainAll(final Collection<?> c) {
                throw new MethodNotImplementedException(
                        "This method is not implemented yet, may be implemented in future");
            }

            @Override
            public void clear() {
                if (!super.isEmpty()) {
                    sharedObject.setChildModified(true, ACCESS_OBJECT);
                }
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
            // use poll instead of pop, pop will throw exp
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
            // final ChildTagAppendListener listener =
            // child.sharedObject
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
            public boolean addAll(final Collection<? extends AbstractHtml> children) {
                throw new MethodNotImplementedException("This method is not implemented");
                // No need to implement as it will call add method
                // boolean addedAll = super.addAll(children);
                // if (addedAll) {
                //
                // for (AbstractHtml child : children) {
                // child.parent = AbstractHtml.this;
                // child.sharedObject = AbstractHtml.this.sharedObject;
                // final ChildTagAppendListener listener =
                // child.sharedObject
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
        init();
    }

    @SuppressWarnings("unused")
    private AbstractHtml() {
        throw new AssertionError();
    }

    /**
     * @param base     the parent tag of this object
     * @param children the tags which will be added as a children tag of this
     *                 object.
     * @author WFF
     */
    public AbstractHtml(final AbstractHtml base, final Collection<? extends AbstractHtml> children) {
        this(base, children.toArray(new AbstractHtml[children.size()]));
    }

    /**
     * @param base     the parent tag of this object
     * @param children the tags which will be added as a children tag of this
     *                 object.
     * @author WFF
     * @since 3.0.1
     */
    public AbstractHtml(final AbstractHtml base, final AbstractHtml... children) {

        tagName = null;
        tagType = TagType.OPENING_CLOSING;
        tagNameIndexBytes = null;
        noTagContentTypeHtml = false;
        htmlStartSBAsFirst = false;

        final List<Lock> locks;

        if (base == null) {
            sharedObject = new AbstractHtml5SharedObject(this);
            locks = children != null ? TagUtil.lockAndGetWriteLocks(this, ACCESS_OBJECT, children)
                    : TagUtil.lockAndGetWriteLocks(this, ACCESS_OBJECT);
        } else {
            locks = children != null ? TagUtil.lockAndGetWriteLocks(base, ACCESS_OBJECT, children)
                    : TagUtil.lockAndGetWriteLocks(base, ACCESS_OBJECT);
        }

        final Deque<AbstractHtml> parentLostListenerTags = buildParentLostListenerTags(children);
        try {

            htmlStartSB = new StringBuilder(
                    tagName == null ? 0 : tagName.length() + 2 + ((attributes == null ? 0 : attributes.length) * 16));
            initInConstructor();

            buildOpeningTag(false);
            closingTag = buildClosingTag();
            if (base != null) {
                base.addChildLockless(this);
                // base.children.add(this);
                // should not uncomment the below codes as it is handled in the
                // above add method
                // parent = base;
                // sharedObject = base.sharedObject;
            }
            // sharedObject initialization must come first
            // else {
            // sharedObject = new AbstractHtml5SharedObject(this);
            // }
            // this.children.addAll(children);
            if (children != null) {
                appendChildrenLockless(children);
            }
            // childAppended(parent, this);
        } finally {
            for (final Lock lock : locks) {
                lock.unlock();
            }
        }

        pollAndInvokeParentLostListeners(parentLostListenerTags);
        // Note: GainedListener should be invoked after ParentLostListener
        pollAndInvokeParentGainedListeners(buildParentGainedListenerTags((AbstractHtml[]) null, children));
    }

    /**
     * @param base
     * @param childContent any text, it can also be html text.
     * @since 3.0.2
     */
    protected AbstractHtml(final AbstractHtml base, final String childContent, final boolean noTagContentTypeHtml) {

        tagName = null;
        tagType = TagType.OPENING_CLOSING;
        tagNameIndexBytes = null;
        this.noTagContentTypeHtml = noTagContentTypeHtml;

        final Lock lock;

        if (base == null) {
            sharedObject = new AbstractHtml5SharedObject(this);
            lock = lockAndGetWriteLock();
        } else {
            lock = base.lockAndGetWriteLock();
        }

        try {

            htmlStartSB = new StringBuilder(
                    tagName == null ? 0 : tagName.length() + 2 + ((attributes == null ? 0 : attributes.length) * 16));
            initInConstructor();

            htmlStartSBAsFirst = true;
            getHtmlMiddleSB().append(childContent);
            buildOpeningTag(false);
            closingTag = buildClosingTag();
            if (base != null) {
                base.addChildLockless(this);
                // base.children.add(this);
                // should not uncomment the below codes as it is handled in the
                // above add method
                // parent = base;
                // sharedObject = base.sharedObject;
            }
            // sharedObject initialization must come first
            // else {
            // sharedObject = new AbstractHtml5SharedObject(this);
            // }
            setRebuild(true);

            // childAppended(parent, this);

        } finally {
            lock.unlock();
        }
    }

    /**
     * @param base
     * @param childContent any text, it can also be html text.
     */
    public AbstractHtml(final AbstractHtml base, final String childContent) {
        this(base, childContent, false);
    }

    /**
     * should be invoked to generate opening and closing tag base class containing
     * the functionalities to generate html string.
     *
     * @param tagName TODO
     * @param base    TODO
     * @author WFF
     */
    public AbstractHtml(final String tagName, final AbstractHtml base, final AbstractAttribute[] attributes) {

        this.tagName = tagName;
        tagType = TagType.OPENING_CLOSING;
        tagNameIndexBytes = null;
        noTagContentTypeHtml = false;
        htmlStartSBAsFirst = false;

        final List<Lock> attrLocks = AttributeUtil.lockAndGetWriteLocks(ACCESS_OBJECT, attributes);

        final Lock lock;

        if (base == null) {
            sharedObject = new AbstractHtml5SharedObject(this);
            lock = lockAndGetWriteLock();
        } else {
            lock = base.lockAndGetWriteLock();
        }

        try {

            initAttributes(attributes);

            htmlStartSB = new StringBuilder(
                    tagName == null ? 0 : tagName.length() + 2 + ((attributes == null ? 0 : attributes.length) * 16));
            initInConstructor();

            markOwnerTag(attributes);
            buildOpeningTag(false);
            closingTag = buildClosingTag();
            if (base != null) {
                base.addChildLockless(this);
                // base.children.add(this);
                // should not uncomment the below codes as it is handled in the
                // above add method
                // parent = base;
                // sharedObject = base.sharedObject;
            }

            // else {
            // sharedObject = new AbstractHtml5SharedObject(this);
            // }

            // childAppended(parent, this);
        } finally {
            lock.unlock();

            if (attrLocks != null) {
                for (final Lock attrLock : attrLocks) {
                    attrLock.unlock();
                }
            }
        }
    }

    /**
     * should be invoked to generate opening and closing tag base class containing
     * the functionalities to generate html string.
     *
     * @param preIndexedTagName PreIndexedTagName constant
     * @param base              TODO
     * @author WFF
     * @since 3.0.3
     */
    protected AbstractHtml(final PreIndexedTagName preIndexedTagName, final AbstractHtml base,
            final AbstractAttribute[] attributes) {

        tagName = preIndexedTagName.tagName();
        tagType = TagType.OPENING_CLOSING;
        tagNameIndexBytes = preIndexedTagName.internalIndexBytes(ACCESS_OBJECT);
        noTagContentTypeHtml = false;
        htmlStartSBAsFirst = false;

        final List<Lock> attrLocks = AttributeUtil.lockAndGetWriteLocks(ACCESS_OBJECT, attributes);

        final Lock lock;

        if (base == null) {
            sharedObject = new AbstractHtml5SharedObject(this);
            lock = lockAndGetWriteLock();
        } else {
            lock = base.lockAndGetWriteLock();
        }

//        if (parentLock != null) {
//            parentLock.lock();
//        }
//        if (childLock != null) {
//            childLock.lock();
//        }

        try {

            initAttributes(attributes);

            htmlStartSB = new StringBuilder(
                    tagName == null ? 0 : tagName.length() + 2 + ((attributes == null ? 0 : attributes.length) * 16));
            initInConstructor();

            markOwnerTag(attributes);
            buildOpeningTag(false);
            closingTag = buildClosingTag();
            if (base != null) {
                base.addChildLockless(this);
                // base.children.add(this);
                // should not uncomment the below codes as it is handled in the
                // above add method
                // parent = base;
                // sharedObject = base.sharedObject;
            }

            // else {
            // sharedObject = new AbstractHtml5SharedObject(this);
            // }

            // childAppended(parent, this);

        } finally {
            lock.unlock();

            if (attrLocks != null) {
                for (final Lock attrLock : attrLocks) {
                    attrLock.unlock();
                }
            }

        }
    }

    private void init() {
        setRebuild(true);
    }

    /**
     * Calculates capacity for the default load factor
     *
     * @param size
     * @return the calculated capacity
     * @since 3.0.7
     */
    private static int calcCapacity(final int size) {
        // length/load factor + 1
        return (int) (size / 0.75F) + 1;
    }

    /**
     * Appends the given child tag to its children.
     *
     * @param child the tag to append to its children.
     * @return true if the given child tag is appended as child of this tag.
     * @author WFF
     */
    public boolean appendChild(final AbstractHtml child) {
        // TODO fix bug in addChild(child);
        // directly calling addChild(child); here will not work
        // it will block thread in a large no of threads

        // this method works fine
        // lock is called inside appendChildren
        appendChildren(child);
        return true;
    }

    /**
     * @param children
     * @since 3.0.18
     */
    private void removeFromSharedTagContent(final AbstractHtml... children) {
        if (children.length > 0) {
            final AbstractHtml firstChild = children[0];
            removeFromSharedTagContent(firstChild);
        }
    }

    /**
     * @param firstChild
     * @since 3.0.18
     */
    private void removeFromSharedTagContent(final AbstractHtml firstChild) {
        if (TagUtil.isTagless(firstChild) && !firstChild.parentNullifiedOnce) {
            final var sharedTagContent = firstChild.sharedTagContent;
            if (sharedTagContent != null && firstChild instanceof NoTag) {
                firstChild.sharedTagContent = null;
                firstChild.sharedTagContentSubscribed = null;
                firstChild.cachedStcFormatter = null;
                sharedTagContent.removeListeners(internalId);
            }
        }
    }

    /**
     * Removes all children from this tag.
     *
     * @author WFF
     */
    public void removeAllChildren() {

        boolean listenerInvoked = false;
        final Lock lock = lockAndGetWriteLock();

        // should be after locking
        final AbstractHtml5SharedObject sharedObject = this.sharedObject;
        List<Lock> newSOLocks = null;
        final AbstractHtml[] removedAbstractHtmls;
        try {

            removedAbstractHtmls = children.toArray(new AbstractHtml[0]);
            children.clear();

            removeFromSharedTagContent(removedAbstractHtmls);

            newSOLocks = initNewSharedObjectInAllNestedTagsAndSetSuperParentNull(removedAbstractHtmls);

            final ChildTagRemoveListener listener = sharedObject.getChildTagRemoveListener(ACCESS_OBJECT);
            if (listener != null) {
                listener.allChildrenRemoved(new ChildTagRemoveListener.Event(this, null, removedAbstractHtmls));
                listenerInvoked = true;
            }

        } finally {
            if (newSOLocks != null) {
                for (final Lock newSOLock : newSOLocks) {
                    newSOLock.unlock();
                }
            }
            lock.unlock();

        }
        if (listenerInvoked) {
            final PushQueue pushQueue = sharedObject.getPushQueue(ACCESS_OBJECT);
            if (pushQueue != null) {
                pushQueue.push();
            }
        }
        for (final AbstractHtml tag : removedAbstractHtmls) {
            tag.invokeParentLostListeners();
        }
    }

    /**
     * Removes all children from this tag.
     *
     * @param updateClient
     * @return
     * @since 3.0.6
     */
    ChildTagRemoveListenerData removeAllChildrenAndGetEventsLockless(final boolean updateClient) {

        final AbstractHtml[] removedAbstractHtmls = children.toArray(new AbstractHtml[0]);
        children.clear();

        final List<Lock> newSOLocks = initNewSharedObjectInAllNestedTagsAndSetSuperParentNull(removedAbstractHtmls);

        try {
            if (updateClient) {
                final AbstractHtml5SharedObject sharedObject = this.sharedObject;
                final ChildTagRemoveListener listener = sharedObject.getChildTagRemoveListener(ACCESS_OBJECT);
                if (listener != null) {

                    final ChildTagRemoveListenerData listenerData = new ChildTagRemoveListenerData(sharedObject,
                            listener, new ChildTagRemoveListener.Event(this, null, removedAbstractHtmls));
                    return listenerData;
                }
            }
        } finally {
            for (final Lock newSOLock : newSOLocks) {
                newSOLock.unlock();
            }
        }

        for (final AbstractHtml tag : removedAbstractHtmls) {
            tag.invokeParentLostListeners();
        }

        return null;
    }

    /**
     * removes all children and adds the given tag
     *
     * @param innerHtml the inner html tag to add
     * @author WFF
     */
    public void addInnerHtml(final AbstractHtml innerHtml) {
        addInnerHtmls(innerHtml);
    }

    /**
     * Removes all children and adds the given tags as children.
     *
     * @param innerHtmls the inner html tags to add
     * @author WFF
     * @since 2.1.3
     */
    public void addInnerHtmls(final AbstractHtml... innerHtmls) {
        addInnerHtmls(true, innerHtmls);
    }

    /**
     * Removes all children and adds the given tags as children.
     *
     * @param updateClient true to update client browser page if it is available.
     *                     The default value is true but it will be ignored if there
     *                     is no client browser page.
     * @param innerHtmls   the inner html tags to add
     * @author WFF
     * @since 2.1.15
     */
    protected void addInnerHtmls(final boolean updateClient, final AbstractHtml... innerHtmls) {
        addInnerHtmls(false, updateClient, innerHtmls);
    }

    /**
     * Removes all children and adds the given tags as children.
     *
     * @param updateClient true to update client browser page if it is available.
     *                     The default value is true but it will be ignored if there
     *                     is no client browser page.
     * @param innerHtmls   the inner html tags to add
     * @author WFF
     * @since 12.0.0-beta.1
     */
    protected void addInnerHtmls(final boolean ignoreApplyURIChange, final boolean updateClient,
            final AbstractHtml... innerHtmls) {

        boolean listenerInvoked = false;
//        final Lock lock = sharedObject.getLock(ACCESS_OBJECT).writeLock();
//        lock.lock();

        final List<Lock> locks = lockAndGetWriteLocks(innerHtmls);

        // sharedObject should be after locking
        final AbstractHtml5SharedObject sharedObject = this.sharedObject;

        List<Lock> newSOLocks = null;

        final Deque<AbstractHtml> parentGainedListenerTags;
        final Deque<AbstractHtml> parentLostListenerTags;

        try {
            validateChildren(innerHtmls);
            if (!ignoreApplyURIChange) {
                for (final AbstractHtml each : innerHtmls) {
                    each.applyURIChange(sharedObject);
                }
            }

            final Set<AbstractHtml> children = this.children;
            final AbstractHtml[] removedTags = children.toArray(new AbstractHtml[children.size()]);

            parentGainedListenerTags = buildParentGainedListenerTags(innerHtmls);
            parentLostListenerTags = buildParentLostListenerTags(innerHtmls, removedTags);

            children.clear();

            removeFromSharedTagContent(removedTags);

            newSOLocks = initNewSharedObjectInAllNestedTagsAndSetSuperParentNull(removedTags);

            final InnerHtmlAddListener listener = sharedObject.getInnerHtmlAddListener(ACCESS_OBJECT);

            if (listener != null && updateClient) {

                final InnerHtmlAddListener.Event[] events = new InnerHtmlAddListener.Event[innerHtmls.length];

                int index = 0;

                for (final AbstractHtml innerHtml : innerHtmls) {

                    final boolean alreadyHasParent = innerHtml.parent != null;
                    AbstractHtml previousParentTag = null;

                    if (alreadyHasParent) {
                        if (innerHtml.parent.sharedObject == sharedObject) {
                            previousParentTag = innerHtml.parent;
                        } else {
                            if (innerHtml.parent.sharedObject.getInnerHtmlAddListener(ACCESS_OBJECT) == null) {
                                removeFromTagByWffIdMap(innerHtml,
                                        innerHtml.parent.sharedObject.getTagByWffId(ACCESS_OBJECT));
                            } // else {TODO also write the code to push
                              // changes to the other BrowserPage}

                        }
                    } else {
                        removeDataWffIdFromHierarchyLockless(innerHtml);
                    }

                    addChild(innerHtml, false);

                    events[index] = new InnerHtmlAddListener.Event(this, innerHtml, previousParentTag);
                    index++;

                }

                listener.innerHtmlsAdded(this, events);
                listenerInvoked = true;
            } else {
                for (final AbstractHtml innerHtml : innerHtmls) {

                    if (innerHtml.parent != null) {
                        if (innerHtml.parent.sharedObject.getInnerHtmlAddListener(ACCESS_OBJECT) == null) {
                            removeFromTagByWffIdMap(innerHtml,
                                    innerHtml.parent.sharedObject.getTagByWffId(ACCESS_OBJECT));
                        } // else {TODO also write the code to push
                          // changes to the other BrowserPage}
                    } else {
                        removeDataWffIdFromHierarchy(sharedObject, innerHtml);
                    }

                    addChild(innerHtml, false);
                }
            }

        } finally {
            if (newSOLocks != null) {
                for (final Lock newSOLock : newSOLocks) {
                    newSOLock.unlock();
                }
            }

//            lock.unlock();
            for (final Lock lck : locks) {
                lck.unlock();
            }
        }

        if (listenerInvoked) {
            final PushQueue pushQueue = sharedObject.getPushQueue(ACCESS_OBJECT);
            if (pushQueue != null) {
                pushQueue.push();
            }
        }

        pollAndInvokeParentLostListeners(parentLostListenerTags);
        // Note: GainedListener should be invoked after ParentLostListener
        pollAndInvokeParentGainedListeners(parentGainedListenerTags);
    }

    /**
     * @return true if the parent is nullified at least once
     * @since 3.0.6
     */
    boolean isParentNullifiedOnce() {
        return parentNullifiedOnce;
    }

    /**
     * @param updateClient true to update client browser page if it is available.
     *                     The default value is true but it will be ignored if there
     *                     is no client browser page.
     * @param innerHtmls
     * @return
     * @since 3.0.6
     */
    InnerHtmlListenerData addInnerHtmlsAndGetEventsLockless(final boolean updateClient,
            final AbstractHtml... innerHtmls) {

        InnerHtmlListenerData innerHtmlListenerData = null;
        final AbstractHtml[] removedAbstractHtmls = children.toArray(new AbstractHtml[children.size()]);
        children.clear();

        final List<Lock> newSOLocks = initNewSharedObjectInAllNestedTagsAndSetSuperParentNull(removedAbstractHtmls);

        try {
            final InnerHtmlAddListener listener = sharedObject.getInnerHtmlAddListener(ACCESS_OBJECT);

            if (listener != null && updateClient) {

                final InnerHtmlAddListener.Event[] events = new InnerHtmlAddListener.Event[innerHtmls.length];

                int index = 0;

                for (final AbstractHtml innerHtml : innerHtmls) {

                    final boolean alreadyHasParent = innerHtml.parent != null;
                    AbstractHtml previousParentTag = null;

                    if (alreadyHasParent) {
                        if (innerHtml.parent.sharedObject == sharedObject) {
                            previousParentTag = innerHtml.parent;
                        } else {
                            if (innerHtml.parent.sharedObject.getInnerHtmlAddListener(ACCESS_OBJECT) == null) {
                                removeFromTagByWffIdMap(innerHtml,
                                        innerHtml.parent.sharedObject.getTagByWffId(ACCESS_OBJECT));
                            } // else {TODO also write the code to push
                              // changes to the other BrowserPage}

                        }
                    } else {
                        removeDataWffIdFromHierarchy(sharedObject, innerHtml);
                    }

                    addChild(innerHtml, false);

                    events[index] = new InnerHtmlAddListener.Event(this, innerHtml, previousParentTag);
                    index++;

                }

                innerHtmlListenerData = new InnerHtmlListenerData(sharedObject, listener, events);

            } else {
                for (final AbstractHtml innerHtml : innerHtmls) {
                    if (innerHtml.parent != null) {
                        if (innerHtml.parent.sharedObject.getInnerHtmlAddListener(ACCESS_OBJECT) == null) {
                            removeFromTagByWffIdMap(innerHtml,
                                    innerHtml.parent.sharedObject.getTagByWffId(ACCESS_OBJECT));
                        } // else {TODO also write the code to push
                          // changes to the other BrowserPage}
                    } else {
                        removeDataWffIdFromHierarchy(sharedObject, innerHtml);
                    }

                    addChild(innerHtml, false);
                }
            }
        } finally {
            for (final Lock newSOLock : newSOLocks) {
                newSOLock.unlock();
            }
        }

        for (final AbstractHtml tag : removedAbstractHtmls) {
            tag.invokeParentLostListeners();
        }

        return innerHtmlListenerData;

    }

    /**
     * @param sharedTagContent the shared content to be inserted as inner content.
     *                         Any changes of content in the sharedTagContent will
     *                         be reflected in this tag and all other consuming tags
     *                         of this sharedTagContent object.
     * @since 3.0.6
     */
    public <T> void addInnerHtml(final SharedTagContent<T> sharedTagContent) {
        addInnerHtml(true, sharedTagContent, null);
    }

    /**
     * @param sharedTagContent the shared content to be inserted as inner content.
     *                         Any changes of content in the sharedTagContent will
     *                         be reflected in this tag and all other consuming tags
     *                         of this sharedTagContent object.
     * @param formatter        content to be formatted using this formatter before
     *                         it is embedded in this tag.
     * @since 3.0.6
     */
    public <T> void addInnerHtml(final SharedTagContent<T> sharedTagContent,
            final SharedTagContent.ContentFormatter<T> formatter) {
        addInnerHtml(true, sharedTagContent, formatter);
    }

    /**
     * @param updateClient     true to update client browser page if it is
     *                         available. The default value is true but it will be
     *                         ignored if there is no client browser page. false
     *                         will skip updating client browser page only when this
     *                         method call.
     * @param sharedTagContent the shared content to be inserted as inner content.
     *                         Any changes of content in the sharedTagContent will
     *                         be reflected in this tag and all other consuming tags
     *                         of this sharedTagContent object.
     * @since 3.0.6
     */
    public <T> void addInnerHtml(final boolean updateClient, final SharedTagContent<T> sharedTagContent) {
        addInnerHtml(updateClient, sharedTagContent, null);
    }

    /**
     * @param updateClient     true to update client browser page if it is
     *                         available. The default value is true but it will be
     *                         ignored if there is no client browser page. false
     *                         will skip updating client browser page only when this
     *                         method call.
     * @param sharedTagContent the shared content to be inserted as inner content.
     *                         Any changes of content in the sharedTagContent will
     *                         be reflected in this tag and all other consuming tags
     *                         of this sharedTagContent object.
     * @param formatter        content to be formatted using this formatter before
     *                         it is embedded in this tag.
     * @since 3.0.6
     */
    public <T> void addInnerHtml(final boolean updateClient, final SharedTagContent<T> sharedTagContent,
            final SharedTagContent.ContentFormatter<T> formatter) {
        addInnerHtml(updateClient, sharedTagContent, formatter, false);
    }

    /**
     * Subscribes to the given SharedTagContent and listens to its content updates
     * but pushes updates of this tag to client browser page only if there is an
     * active WebSocket connection between server and client browser page and if
     * there is no active WebSocket connection that changes will not be cached for
     * later push. However, it will try to keep the change of this tag (server
     * object) up to date with client browser page. The difference of this method
     * with {@link AbstractHtml#addInnerHtml(SharedTagContent)} is that
     * addInnerHtml(SharedTagContent) will push all changes of this tag (server side
     * object) to client browser page and if push is failed it will be cached for
     * retry. {@link AbstractHtml#addInnerHtml(SharedTagContent)} will reliably
     * deliver each and every change to client browser page. There are cases where
     * it is discouraged,
     *
     * <br>
     * <br>
     * Eg:- printing current time in realtime. Imagine, while printing the time in
     * realtime the websocket communication between the client and server is lost
     * and reconnected after few seconds. In that case we don't have to push the out
     * dated data (time). In such cases
     * {@link AbstractHtml#subscribeTo(SharedTagContent)} is more appropriate.
     *
     * @param sharedTagContent the shared content to be inserted as inner content.
     *                         Any changes of content in the sharedTagContent will
     *                         be reflected in this tag and all other consuming tags
     *                         of this sharedTagContent object.
     * @since 3.0.6
     */
    public <T> void subscribeTo(final SharedTagContent<T> sharedTagContent) {
        addInnerHtml(true, sharedTagContent, null, true);
    }

    /**
     * Subscribes to the given SharedTagContent and listens to its content updates
     * but pushes updates of this tag to client browser page only if there is an
     * active WebSocket connection between server and client browser page and if
     * there is no active WebSocket connection that changes will not be cached for
     * later push. However, it will try to keep the change of this tag (server
     * object) up to date with client browser page. The difference of this method
     * with {@link AbstractHtml#addInnerHtml(SharedTagContent)} is that
     * addInnerHtml(SharedTagContent) will push all changes of this tag (server side
     * object) to client browser page and if push is failed it will be cached for
     * retry. {@link AbstractHtml#addInnerHtml(SharedTagContent)} will reliably
     * deliver each and every change to client browser page. There are cases where
     * it is discouraged,
     *
     * <br>
     * <br>
     * Eg:- printing current time in realtime. Imagine, while printing the time in
     * realtime the websocket communication between the client and server is lost
     * and reconnected after few seconds. In that case we don't have to push the out
     * dated data (time). In such cases
     * {@link AbstractHtml#subscribeTo(SharedTagContent)} is more appropriate.
     *
     * @param sharedTagContent the shared content to be inserted as inner content.
     *                         Any changes of content in the sharedTagContent will
     *                         be reflected in this tag and all other consuming tags
     *                         of this sharedTagContent object.
     * @param formatter        content to be formatted using this formatter before
     *                         it is embedded in this tag.
     * @since 3.0.6
     */
    public <T> void subscribeTo(final SharedTagContent<T> sharedTagContent,
            final SharedTagContent.ContentFormatter<T> formatter) {
        addInnerHtml(true, sharedTagContent, formatter, true);
    }

    /**
     * Subscribes to the given SharedTagContent and listens to its content updates
     * but pushes updates of this tag to client browser page only if there is an
     * active WebSocket connection between server and client browser page and if
     * there is no active WebSocket connection that changes will not be cached for
     * later push. However, it will try to keep the change of this tag (server
     * object) up to date with client browser page. The difference of this method
     * with {@link AbstractHtml#addInnerHtml(SharedTagContent)} is that
     * addInnerHtml(SharedTagContent) will push all changes of this tag (server side
     * object) to client browser page and if push is failed it will be cached for
     * retry. {@link AbstractHtml#addInnerHtml(SharedTagContent)} will reliably
     * deliver each and every change to client browser page. There are cases where
     * it is discouraged,
     *
     * <br>
     * <br>
     * Eg:- printing current time in realtime. Imagine, while printing the time in
     * realtime the websocket communication between the client and server is lost
     * and reconnected after few seconds. In that case we don't have to push the out
     * dated data (time). In such cases
     * {@link AbstractHtml#subscribeTo(SharedTagContent)} is more appropriate.
     *
     * @param updateClient     true to update client browser page if it is
     *                         available. The default value is true but it will be
     *                         ignored if there is no client browser page. false
     *                         will skip updating client browser page only when this
     *                         method call.
     * @param sharedTagContent the shared content to be inserted as inner content.
     *                         Any changes of content in the sharedTagContent will
     *                         be reflected in this tag and all other consuming tags
     *                         of this sharedTagContent object.
     * @param formatter        content to be formatted using this formatter before
     *                         it is embedded in this tag.
     * @since 3.0.6
     */
    public <T> void subscribeTo(final boolean updateClient, final SharedTagContent<T> sharedTagContent,
            final SharedTagContent.ContentFormatter<T> formatter) {
        addInnerHtml(updateClient, sharedTagContent, formatter, true);
    }

    /**
     * @param updateClient     true to update client browser page if it is
     *                         available. The default value is true but it will be
     *                         ignored if there is no client browser page. false
     *                         will skip updating client browser page only when this
     *                         method call.
     * @param sharedTagContent the shared content to be inserted as inner content.
     *                         Any changes of content in the sharedTagContent will
     *                         be reflected in this tag and all other consuming tags
     *                         of this sharedTagContent object.
     * @param formatter        content to be formatted using this formatter before
     *                         it is embedded in this tag.
     * @param subscribe        updateClient will be true only if there is an active
     *                         wsListener otherwise updateClient will be false.
     * @since 3.0.6
     */
    private <T> void addInnerHtml(final boolean updateClient, final SharedTagContent<T> sharedTagContent,
            final SharedTagContent.ContentFormatter<T> formatter, final boolean subscribe) {

        if (sharedTagContent != null) {
            final Lock lock = lockAndGetWriteLock();
            try {
                sharedTagContent.addInnerHtml(updateClient, this, formatter, subscribe);
            } finally {
                lock.unlock();
            }
        } else {
            removeSharedTagContent(false);
        }

    }

    /**
     * @param sharedTagContent
     * @since 3.0.6
     */
    <T> void setSharedTagContent(final SharedTagContent<T> sharedTagContent) {
        this.sharedTagContent = sharedTagContent;
    }

    /**
     * @param sharedTagContentSubscribed
     * @since 12.0.0-beta.12
     */
    void setSharedTagContentSubscribed(final Boolean sharedTagContentSubscribed) {
        this.sharedTagContentSubscribed = sharedTagContentSubscribed;
    }

    /**
     * @return the object of SharedTagContent which created the NoTag in the child
     *         or null if the child NoTag is not created by any SharedTagContent
     *         object.
     * @since 3.0.6
     */
    @SuppressWarnings("unchecked")
    public <T> SharedTagContent<T> getSharedTagContent() {
        final Lock lock = lockAndGetReadLock();
        try {
            if (children.size() == 1) {
                final Iterator<AbstractHtml> iterator = children.iterator();
                if (iterator.hasNext()) {
                    final AbstractHtml firstChild = iterator.next();
                    if (firstChild != null && !firstChild.parentNullifiedOnce && firstChild.sharedTagContent != null
                            && firstChild instanceof NoTag) {
                        // && firstChild.sharedTagContent.contains(firstChild) may lead to deadlock bug
                        // when using under whenURI thread
                        return firstChild.sharedTagContent;
                    }

                }

            }
        } finally {
            lock.unlock();
        }

        return null;
    }

    /**
     * @return true if this tag is subscribed to any SharedTagContent object
     *         otherwise false.
     * @since 3.0.6
     */
    public boolean isSubscribedToSharedTagContent() {

        final Lock lock = lockAndGetReadLock();
        try {
            if (children.size() == 1) {
                final Iterator<AbstractHtml> iterator = children.iterator();
                if (iterator.hasNext()) {
                    final AbstractHtml firstChild = iterator.next();
                    if (firstChild != null && !firstChild.parentNullifiedOnce
                            && firstChild.sharedTagContentSubscribed != null && firstChild instanceof NoTag) {
                        // firstChild.sharedTagContent.isSubscribed(firstChild) may lead to deadlock bug
                        // when using under whenURI thread fix it later
                        final Boolean subscribed = firstChild.sharedTagContentSubscribed;
                        return subscribed != null && subscribed;
                    }

                }

            }
        } finally {
            lock.unlock();
        }

        return false;
    }

    /**
     * @param removeContent true to remove the inner content of this tag otherwise
     *                      false. The inner content will be removed only if it
     *                      contains a ShareTagContent object.
     * @return true if any SharedTagContent object is removed from this tag
     *         otherwise false.
     * @since 3.0.6
     */
    public boolean removeSharedTagContent(final boolean removeContent) {

        boolean listenerInvoked = false;
        boolean removed = false;
        AbstractHtml[] removedAbstractHtmls = null;
        final Lock lock = lockAndGetWriteLock();
        try {
            if (children.size() == 1) {
                final Iterator<AbstractHtml> iterator = children.iterator();
                final AbstractHtml firstChild;
                if (iterator.hasNext() && (firstChild = iterator.next()) != null) {
                    final var sharedTagContent = firstChild.sharedTagContent;
                    if (!firstChild.parentNullifiedOnce && sharedTagContent != null && firstChild instanceof NoTag) {

                        firstChild.sharedTagContent = null;
                        firstChild.sharedTagContentSubscribed = null;
                        firstChild.cachedStcFormatter = null;
                        // nullifying should be before remove as remove is async
                        removed = sharedTagContent.remove(firstChild, this);

                        if (removed && removeContent) {

                            removedAbstractHtmls = children.toArray(new AbstractHtml[children.size()]);
                            children.clear();

                            final List<Lock> newSOLocks = initNewSharedObjectInAllNestedTagsAndSetSuperParentNull(
                                    removedAbstractHtmls);
                            try {
                                final ChildTagRemoveListener listener = sharedObject
                                        .getChildTagRemoveListener(ACCESS_OBJECT);
                                if (listener != null) {
                                    listener.allChildrenRemoved(
                                            new ChildTagRemoveListener.Event(this, null, removedAbstractHtmls));
                                    listenerInvoked = true;
                                }
                            } finally {
                                for (final Lock newSOLock : newSOLocks) {
                                    newSOLock.unlock();
                                }
                            }
                        }

                    }

                }

            }
        } finally {
            lock.unlock();
        }

        if (listenerInvoked) {
            final PushQueue pushQueue = sharedObject.getPushQueue(ACCESS_OBJECT);
            if (pushQueue != null) {
                pushQueue.push();
            }
        }

        if (removedAbstractHtmls != null) {
            for (final AbstractHtml tag : removedAbstractHtmls) {
                tag.invokeParentLostListeners();
            }
        }

        return removed;
    }

    private AbstractHtml[] removeAllChildren(final Collection<AbstractHtml> children,
            final Collection<AbstractHtml> childrenToBeRemoved) {
// NB: must be LinkedHashSet to keep order of removal
        final Set<AbstractHtml> validChildren = new LinkedHashSet<>(calcCapacity(childrenToBeRemoved.size()));

        for (final Object each : childrenToBeRemoved) {
            if (each instanceof final AbstractHtml child) {
                if (AbstractHtml.this.equals(child.parent)) {
                    validChildren.add(child);
                }
            }
        }

        final AbstractHtml[] removedAbstractHtmls = validChildren.toArray(new AbstractHtml[validChildren.size()]);
        removeFromSharedTagContent(removedAbstractHtmls);

        final boolean removedAll = children.removeAll(validChildren);

        if (removedAll) {
            final List<Lock> newSOLocks = initNewSharedObjectInAllNestedTagsAndSetSuperParentNull(removedAbstractHtmls);

            try {
                final ChildTagRemoveListener listener = sharedObject.getChildTagRemoveListener(ACCESS_OBJECT);

                if (listener != null) {
                    listener.childrenRemoved(
                            new ChildTagRemoveListener.Event(AbstractHtml.this, null, removedAbstractHtmls));
                }

                sharedObject.setChildModified(removedAll, ACCESS_OBJECT);
            } finally {
                for (final Lock newSOLock : newSOLocks) {
                    newSOLock.unlock();
                }
            }

        }

        return removedAbstractHtmls;
    }

    /**
     * Removes the given tags from its children tags.
     *
     * @param children the tags to remove from its children.
     * @return true given given children tags have been removed.
     * @author WFF
     */
    public boolean removeChildren(final Collection<AbstractHtml> children) {

        final List<Lock> locks = lockAndGetWriteLocks(children.toArray(new AbstractHtml[0]));

        // must be after locking
        final AbstractHtml5SharedObject sharedObject = this.sharedObject;

//        final Lock lock = sharedObject.getLock(ACCESS_OBJECT).writeLock();
//        lock.lock();
        final boolean removed;
        final AbstractHtml[] removedChildren;
        try {
            removedChildren = removeAllChildren(this.children, children);
            removed = removedChildren.length > 0;
        } finally {
//            lock.unlock();
            for (final Lock lck : locks) {
                lck.unlock();
            }
        }
        final PushQueue pushQueue = sharedObject.getPushQueue(ACCESS_OBJECT);
        if (pushQueue != null) {
            pushQueue.push();
        }
        for (final AbstractHtml tag : removedChildren) {
            tag.invokeParentLostListeners();
        }
        return removed;
    }

    /**
     * Removes the given tags from its children tags.
     *
     * @param children the tags to remove from its children.
     * @return true given given children tags have been removed.
     * @author WFF
     * @since 3.0.1
     */
    public boolean removeChildren(final AbstractHtml... children) {
        return removeChildren(List.of(children));
    }

    /**
     * Removes the given tag from its children only if the given tag is a child of
     * this tag.
     *
     * @param child the tag to remove from its children
     * @return true if removed
     * @author WFF
     */
    public boolean removeChild(final AbstractHtml child) {

        final List<Lock> locks = lockAndGetWriteLocks(child);

        // must be after locking
        final AbstractHtml5SharedObject sharedObject = this.sharedObject;

        boolean listenerInvoked = false;
//        final Lock lock = sharedObject.getLock(ACCESS_OBJECT).writeLock();
//        lock.lock();
        boolean removed = false;

        Lock newSOLock = null;
        try {

            removed = children.remove(child);

            if (removed) {
                removeFromSharedTagContent(child);
                // making child.parent = null inside the below method.
                newSOLock = initNewSharedObjectInAllNestedTagsAndSetSuperParentNull(child, true);

                final ChildTagRemoveListener listener = sharedObject.getChildTagRemoveListener(ACCESS_OBJECT);

                if (listener != null) {
                    listener.childRemoved(new ChildTagRemoveListener.Event(this, child, null));
                    listenerInvoked = true;
                }

            }

        } finally {
            if (newSOLock != null) {
                newSOLock.unlock();
            }
//            lock.unlock();
            for (final Lock lck : locks) {
                lck.unlock();
            }
        }
        if (listenerInvoked) {
            final PushQueue pushQueue = sharedObject.getPushQueue(ACCESS_OBJECT);
            if (pushQueue != null) {
                pushQueue.push();
            }
        }
        if (removed) {
            child.invokeParentLostListeners();
        }
        return removed;
    }

    @SuppressWarnings("unused")
    private boolean addChild(final AbstractHtml child) {
        // this method should contain lock even if it is a private method
        // because this method is called in constructors.
//        final Lock lock = sharedObject.getLock(ACCESS_OBJECT).writeLock();
//        lock.lock();

        final List<Lock> locks = lockAndGetWriteLocks(child);

        // sharedObject should be after locking
        final AbstractHtml5SharedObject sharedObject = this.sharedObject;
        boolean result = false;
        try {

            child.applyURIChange(sharedObject);

            if (child.parent != null) {
                if (child.parent.sharedObject.getChildTagAppendListener(ACCESS_OBJECT) == null) {
                    removeFromTagByWffIdMap(child, child.parent.sharedObject.getTagByWffId(ACCESS_OBJECT));
                } // else {TODO also write the code to push
                  // changes to the other BrowserPage}
            } else {
                removeDataWffIdFromHierarchyLockless(child);
            }

            result = addChild(child, true);
        } finally {
//            lock.unlock();
            for (final Lock lck : locks) {
                lck.unlock();
            }
        }
        final PushQueue pushQueue = sharedObject.getPushQueue(ACCESS_OBJECT);
        if (pushQueue != null) {
            pushQueue.push();
        }
        return result;
    }

    private boolean addChildLockless(final AbstractHtml child) {

        child.applyURIChange(sharedObject);

        boolean result = false;
        if (child.parent != null) {
            if (child.parent.sharedObject.getChildTagAppendListener(ACCESS_OBJECT) == null) {
                removeFromTagByWffIdMap(child, child.parent.sharedObject.getTagByWffId(ACCESS_OBJECT));
            } // else {TODO also write the code to push
              // changes to the other BrowserPage}
        } else {
            removeDataWffIdFromHierarchy(sharedObject, child);
        }

        result = addChild(child, true);
        final PushQueue pushQueue = sharedObject.getPushQueue(ACCESS_OBJECT);
        if (pushQueue != null) {
            pushQueue.push();
        }
        return result;
    }

    /**
     * NB: This method is for internal use
     *
     * @param accessObject
     * @param child
     * @param invokeListener
     * @return
     * @author WFF
     * @since 2.0.0
     */
    public final boolean addChild(@SuppressWarnings("exports") final SecurityObject accessObject,
            final AbstractHtml child, final boolean invokeListener) {
        if (accessObject == null || !(IndexedClassType.BROWSER_PAGE.equals(accessObject.forClassType()))) {
            throw new WffSecurityException("Not allowed to consume this method. This method is for internal use.");
        }

//        final Lock lock = sharedObject.getLock(ACCESS_OBJECT).writeLock();
//        lock.lock();

        final List<Lock> locks = lockAndGetWriteLocks(child);

        // sharedObject should be after locking
        final AbstractHtml5SharedObject sharedObject = this.sharedObject;

        boolean result = false;
        try {

            final boolean alreadyHasParent = child.parent != null;

            if (alreadyHasParent) {
                if (child.parent.sharedObject.getChildTagAppendListener(ACCESS_OBJECT) == null) {
                    removeFromTagByWffIdMap(child, child.parent.sharedObject.getTagByWffId(ACCESS_OBJECT));
                } // else {TODO also write the code to push
                  // changes to the other BrowserPage}
            } else {
                removeDataWffIdFromHierarchy(sharedObject, child);
            }

            result = addChild(child, invokeListener);
        } finally {
//            lock.unlock();
            for (final Lock lck : locks) {
                lck.unlock();
            }
        }
        if (invokeListener) {
            final PushQueue pushQueue = sharedObject.getPushQueue(ACCESS_OBJECT);
            if (pushQueue != null) {
                pushQueue.push();
            }
        }
        return result;
    }

    private boolean addChild(final AbstractHtml child, final boolean invokeListener) {

        boolean added = children.add(child);
        if (!added && !invokeListener) {
            children.remove(child);
            added = children.add(child);
        }
        if (added) {

            // if alreadyHasParent = true then it means the child is moving
            // from
            // one tag to another.
            final boolean alreadyHasParent = child.parent != null;
            final AbstractHtml previousParent = child.parent;

            final AbstractHtml5SharedObject sharedObject = this.sharedObject;

            // boolean foreignLocking method param
//            if (foreignLocking) {
//                Lock foreignLock = null;
//                AbstractHtml5SharedObject foreignSO = child.sharedObject;
//                if (foreignSO != null && !sharedObject.equals(foreignSO)) {
//                    foreignLock = foreignSO.getLock(ACCESS_OBJECT).writeLock();
//                    foreignSO = null;
//                    foreignLock.lock();
//                }
//                try {
//                    if (alreadyHasParent) {
//                        child.parent.children.remove(child);
//                    }
//                    initParentAndSharedObject(child);
//                } finally {
//                    if (foreignLock != null) {
//                        foreignLock.unlock();
//                    }
//                }
//
//            } else {
//                if (alreadyHasParent) {
//                    child.parent.children.remove(child);
//                }
//                initParentAndSharedObject(child);
//            }

            if (alreadyHasParent && !AbstractHtml.this.equals(child.parent)) {
                child.parent.children.remove(child);
            }
            initParentAndSharedObject(child);

            if (invokeListener) {

                if (alreadyHasParent) {
                    final ChildTagAppendListener listener = sharedObject.getChildTagAppendListener(ACCESS_OBJECT);

                    if (listener != null) {
                        listener.childMoved(new ChildTagAppendListener.ChildMovedEvent(previousParent, this, child));
                    }

                } else {

                    final ChildTagAppendListener listener = sharedObject.getChildTagAppendListener(ACCESS_OBJECT);
                    if (listener != null) {
                        final ChildTagAppendListener.Event event = new ChildTagAppendListener.Event(this, child, null);
                        listener.childAppended(event);
                    }
                }
            }

        }
        return added;

    }

    /**
     * @param tagByWffId
     * @param tag
     * @since 3.0.7
     */
    private static void removeFromTagByWffIdMap(final AbstractHtml tag, final Map<String, AbstractHtml> tagByWffId) {

        if (!tagByWffId.isEmpty()) {
            final Deque<Set<AbstractHtml>> childrenStack = new ArrayDeque<>();
            final Set<AbstractHtml> initialSet = Set.of(tag);
            childrenStack.push(initialSet);

            Set<AbstractHtml> children;
            while ((children = childrenStack.poll()) != null) {
                for (final AbstractHtml child : children) {

                    final DataWffId dataWffId = child.dataWffId;
                    if (dataWffId != null) {
                        tagByWffId.computeIfPresent(dataWffId.getValue(), (k, v) -> {
                            if (child.equals(v)) {
                                child.removeDataWffId();
                                return null;
                            }
                            return v;
                        });
                    }

                    final Set<AbstractHtml> subChildren = child.children;
                    if (subChildren != null && !subChildren.isEmpty()) {
                        childrenStack.push(subChildren);
                    }

                }
            }
        }
    }

    /**
     * locking if only if required. if the currentSharedObject and tag.sharedObject
     * is not matching then only locking will be applied.
     *
     * @param currentSharedObject
     * @param tag
     * @since 3.0.11
     */
    private static void removeDataWffIdFromHierarchy(final AbstractHtml5SharedObject currentSharedObject,
            final AbstractHtml tag) {
        if (currentSharedObject.equals(tag.sharedObject)) {
            removeDataWffIdFromHierarchyLockless(tag);
        } else {
            removeDataWffIdFromHierarchy(tag);
        }
    }

    /**
     * @param tag
     * @since 3.0.9
     */
    private static void removeDataWffIdFromHierarchy(final AbstractHtml tag) {

        Lock foreignLock = null;
        // foreignSO comes null when running BrowserPageTest.testToHtmlString
        // addChild from constructor will satisfy this condition
        AbstractHtml5SharedObject foreignSO = tag.sharedObject;
        if (foreignSO != null) {
            foreignLock = foreignSO.getLock(ACCESS_OBJECT).writeLock();
            foreignSO = null;
            foreignLock.lock();
        }

        try {
            removeDataWffIdFromHierarchyLockless(tag);
        } finally {
            if (foreignLock != null) {
                foreignLock.unlock();
            }
        }

    }

    /**
     * @param tag
     * @since 3.0.11
     */
    private static void removeDataWffIdFromHierarchyLockless(final AbstractHtml tag) {
        final Set<AbstractHtml> applicableTags = extractParentTagsForDataWffIdRemoval(tag);

        final Deque<Set<AbstractHtml>> childrenStack = new ArrayDeque<>();
        childrenStack.push(applicableTags);

        Set<AbstractHtml> children;
        while ((children = childrenStack.poll()) != null) {
            for (final AbstractHtml child : children) {

                child.removeDataWffId();

                final Set<AbstractHtml> subChildren = child.children;
                if (subChildren != null && !subChildren.isEmpty()) {
                    childrenStack.push(subChildren);
                }

            }
        }
    }

    /**
     * @param tag
     * @return only the applicable parent tags for DataWffId removal. NB: all of its
     *         children are also applicable for DataWffId removal.
     * @since 3.0.9
     */
    private static Set<AbstractHtml> extractParentTagsForDataWffIdRemoval(final AbstractHtml tag) {

        final Set<AbstractHtml> applicableTags = new HashSet<>(2);

        final Deque<Set<AbstractHtml>> childrenStack = new ArrayDeque<>();
        final Set<AbstractHtml> initialSet = Set.of(tag);
        childrenStack.push(initialSet);

        Set<AbstractHtml> children;
        while ((children = childrenStack.poll()) != null) {
            for (final AbstractHtml child : children) {

                if (child.parentNullifiedOnce) {
                    applicableTags.add(child);
                } else {
                    final Set<AbstractHtml> subChildren = child.children;
                    if (subChildren != null && !subChildren.isEmpty()) {
                        childrenStack.push(subChildren);
                    }
                }

            }
        }

        return applicableTags;
    }

    private void initParentAndSharedObject(final AbstractHtml child) {

        initSharedObject(child);

        child.parent = this;
    }

    private void initSharedObject(final AbstractHtml child) {
        initSharedObject(child, sharedObject);
    }

    /**
     * @param child
     * @param sharedObject
     * @since 3.0.9
     */
    private static void initSharedObject(final AbstractHtml child, final AbstractHtml5SharedObject sharedObject) {

        final Deque<Set<AbstractHtml>> childrenStack = new ArrayDeque<>();
        final Set<AbstractHtml> initialSet = Set.of(child);
        childrenStack.push(initialSet);

        Set<AbstractHtml> children;
        while ((children = childrenStack.poll()) != null) {

            for (final AbstractHtml eachChild : children) {

                eachChild.sharedObject = sharedObject;

                if (eachChild.uriChangeContents != null && !eachChild.sharedObject.isWhenURIUsed()) {
                    eachChild.sharedObject.whenURIUsed(ACCESS_OBJECT);
                }

                // NB: 0 for rootTag so first increment and assign
                eachChild.hierarchyOrder = ++sharedObject.getRootTag().hierarchyOrderCounter;

                // no need to add data-wff-id if the tag is not rendered by
                // BrowserPage (if it is rended by BrowserPage then
                // getLastDataWffId will not be -1)
                if (sharedObject.getLastDataWffId(ACCESS_OBJECT) != -1 && eachChild.dataWffId == null
                        && eachChild.tagName != null && !eachChild.tagName.isEmpty()) {
                    eachChild.initDataWffId(sharedObject);
                }

                final Set<AbstractHtml> subChildren = eachChild.children;

                if (subChildren != null && !subChildren.isEmpty()) {
                    childrenStack.push(subChildren);
                }

            }
        }

    }

    /**
     * @param sharedObject
     * @since 12.0.0-beta.1 should be called only after lock and while
     *        adding/append/prepend/whenURI etc.. this tag to another tag.
     */
    private void applyURIChange(final AbstractHtml5SharedObject sharedObject) {
        applyURIChange(sharedObject, null, false);
    }

    /**
     * @param sharedObject
     * @param tagByWffId   it is required only if calling from
     *                     browserPage.initAbstractHtml method otherwise null.
     * @param updateClient
     * @return
     * @since 12.0.0-beta.1 should be called only after lock and while
     *        adding/append/prepend/whenURI etc.. this tag to another tag.
     */
    void applyURIChange(final AbstractHtml5SharedObject sharedObject, final Map<String, AbstractHtml> tagByWffId,
            final boolean updateClient) {

        final URIChangeTagSupplier uriChangeTagSupplier = sharedObject.getURIChangeTagSupplier(ACCESS_OBJECT);

        final URIEvent currentURIEvent = uriChangeTagSupplier != null ? uriChangeTagSupplier.supply(null) : null;

        if (currentURIEvent != null || tagByWffId != null) {
            final ObjectId hierarchicalLoopId = LoopIdGenerator.nextId();
            final Deque<List<AbstractHtml>> childrenStack = new ArrayDeque<>();
            childrenStack.push(List.of(this));
            List<AbstractHtml> children;
            while ((children = childrenStack.poll()) != null) {

                for (final AbstractHtml eachChild : children) {
                    if (hierarchicalLoopId.equals(eachChild.hierarchicalLoopId)) {
                        continue;
                    }
                    eachChild.hierarchicalLoopId = hierarchicalLoopId;
                    final AbstractHtml[] innerHtmls = eachChild.changeInnerHtmlsForURIChange(currentURIEvent,
                            updateClient);
                    if (innerHtmls != null && innerHtmls.length > 0) {
                        childrenStack.push(List.of(innerHtmls));
                    } else if (eachChild.children != null && !eachChild.children.isEmpty()) {
                        childrenStack.push(List.copyOf(eachChild.children));
                    }
                    if (eachChild.uriChangeContents != null) {
                        // null checking is not required if uriChangeTagSupplier == null then both
                        // currentURIEvent and
                        // tagByWffId will be null
                        uriChangeTagSupplier.supply(eachChild);
                        if (tagByWffId != null && TagUtil.isTagged(eachChild)) {
                            if (eachChild.dataWffId == null) {
                                eachChild.setDataWffId(sharedObject.getNewDataWffId(ACCESS_OBJECT));
                            }
                            tagByWffId.put(eachChild.dataWffId.getValue(), eachChild);
                        }
                    }
                }
            }

        } else if (uriChangeContents != null && uriChangeTagSupplier != null) {
            uriChangeTagSupplier.supply(this);
        }
    }

    /**
     * adds the given children to the last position of the current children of this
     * object.
     *
     * @param children children to append in this object's existing children.
     * @author WFF
     */
    public void appendChildren(final Collection<AbstractHtml> children) {

        boolean listenerInvoked = false;

        final List<Lock> locks = lockAndGetWriteLocks(children.toArray(new AbstractHtml[0]));

        // sharedObject should be after locking
        final AbstractHtml5SharedObject sharedObject = this.sharedObject;
        final Deque<AbstractHtml> parentGainedListenerTags;
        final Deque<AbstractHtml> parentLostListenerTags;

//        final Lock lock = sharedObject.getLock(ACCESS_OBJECT).writeLock();
//        lock.lock();
        try {

            validateChildren(children);
            parentGainedListenerTags = buildParentGainedListenerTags(children);
            parentLostListenerTags = buildParentLostListenerTags(children);
            for (final AbstractHtml each : children) {
                each.applyURIChange(sharedObject);
            }

            final Collection<ChildMovedEvent> movedOrAppended = new ArrayDeque<>(children.size());

            for (final AbstractHtml child : children) {
                final AbstractHtml previousParent = child.parent;

                if (child.parent != null) {
                    if (child.parent.sharedObject.getChildTagAppendListener(ACCESS_OBJECT) == null) {
                        removeFromTagByWffIdMap(child, child.parent.sharedObject.getTagByWffId(ACCESS_OBJECT));
                    } // else {TODO also write the code to push
                      // changes to the other BrowserPage}
                } else {
                    removeDataWffIdFromHierarchyLockless(child);
                }

                addChild(child, false);

                final ChildMovedEvent event = new ChildMovedEvent(previousParent, this, child);
                movedOrAppended.add(event);

            }

            final ChildTagAppendListener listener = sharedObject.getChildTagAppendListener(ACCESS_OBJECT);
            if (listener != null) {
                listener.childrendAppendedOrMoved(movedOrAppended);
                listenerInvoked = true;
            }
        } finally {
//            lock.unlock();
            for (final Lock lck : locks) {
                lck.unlock();
            }
        }
        if (listenerInvoked) {
            final PushQueue pushQueue = sharedObject.getPushQueue(ACCESS_OBJECT);
            if (pushQueue != null) {
                pushQueue.push();
            }
        }

        pollAndInvokeParentLostListeners(parentLostListenerTags);
        pollAndInvokeParentGainedListeners(parentGainedListenerTags);
    }

    /**
     * adds the given children to the last position of the current children of this
     * object.
     *
     * @param children children to append in this object's existing children.
     * @author WFF
     * @since 2.1.6
     */
    public void appendChildren(final AbstractHtml... children) {

        // NB: any changes to this method should also be applied in
        // appendChildrenLockless(AbstractHtml... children)
        // this method in consumed in constructor

        boolean listenerInvoked = false;

        final List<Lock> locks = lockAndGetWriteLocks(children);

        // sharedObject should be after locking
        final AbstractHtml5SharedObject sharedObject = this.sharedObject;

        final Deque<AbstractHtml> parentGainedListenerTags;
        final Deque<AbstractHtml> parentLostListenerTags;

//        List<Lock> foreignLocks = TagUtil.lockAndGetWriteLocks(ACCESS_OBJECT, children);
//        List<Lock> attrLocks = TagUtil.lockAndGetNestedAttributeWriteLocks(ACCESS_OBJECT, children);

//        final Lock lock = sharedObject.getLock(ACCESS_OBJECT).writeLock();
//        lock.lock();
        try {
            validateChildren(children);
            parentLostListenerTags = buildParentLostListenerTags(children);
            parentGainedListenerTags = buildParentGainedListenerTags(children);
            for (final AbstractHtml each : children) {
                each.applyURIChange(sharedObject);
            }

            final Iterator<AbstractHtml> iterator = this.children.iterator();
            if (iterator.hasNext()) {
                final AbstractHtml firstExistingChild = iterator.next();
                removeFromSharedTagContent(firstExistingChild);
            }

            final Collection<ChildMovedEvent> movedOrAppended = new ArrayDeque<>(children.length);

            for (final AbstractHtml child : children) {
                final AbstractHtml previousParent = child.parent;

                if (previousParent != null) {
                    if (child.parent.sharedObject.getChildTagAppendListener(ACCESS_OBJECT) == null) {
                        removeFromTagByWffIdMap(child, child.parent.sharedObject.getTagByWffId(ACCESS_OBJECT));
                    } // else {TODO also write the code to push
                      // changes to the other BrowserPage}
                } else {
                    removeDataWffIdFromHierarchyLockless(child);
                }

                addChild(child, false);

                final ChildMovedEvent event = new ChildMovedEvent(previousParent, this, child);
                movedOrAppended.add(event);

            }

            final ChildTagAppendListener listener = sharedObject.getChildTagAppendListener(ACCESS_OBJECT);
            if (listener != null) {
                listener.childrendAppendedOrMoved(movedOrAppended);
                listenerInvoked = true;
            }

        } finally {
//            lock.unlock();
//            for (final Lock attrLock : attrLocks) {
//                attrLock.unlock();
//            }
//            for (final Lock foreignLock : foreignLocks) {
//                foreignLock.unlock();
//            }

            for (final Lock lck : locks) {
                lck.unlock();
            }
        }
        if (listenerInvoked) {
            final PushQueue pushQueue = sharedObject.getPushQueue(ACCESS_OBJECT);
            if (pushQueue != null) {
                pushQueue.push();
            }
        }
        pollAndInvokeParentLostListeners(parentLostListenerTags);
        // Note: GainedListener should be invoked after ParentLostListener
        pollAndInvokeParentGainedListeners(parentGainedListenerTags);
    }

    private void validateChildren(final AbstractHtml... children) {
        if (WffConfiguration.isDebugMode()) {
            for (final AbstractHtml child : children) {
                if (child == null) {
                    throw new InvalidValueException("The child tag object cannot be null.");
                }
            }
            validateChildren(Set.of(children));
        }
    }

    private void validateChildren(final Collection<AbstractHtml> children) {
        if (WffConfiguration.isDebugMode()) {
            for (final AbstractHtml child : children) {
                if (child == null) {
                    throw new InvalidValueException("The child tag object cannot be null.");
                }
            }
            validateChildren(new LinkedHashSet<>(children));
        }
    }

    private void validateChildren(final Set<AbstractHtml> initialSet) {
        final Deque<Set<AbstractHtml>> childrenStack = new ArrayDeque<>();
        childrenStack.push(initialSet);
        Set<AbstractHtml> children;
        while ((children = childrenStack.poll()) != null) {
            for (final AbstractHtml eachChild : children) {
                if (eachChild.equals(this)) {
                    throw new InvalidUsageException(
                            "Parent tag cannot be used as child or sub-child tag in any of its children hierarchy.");
                }
                final Set<AbstractHtml> subChildren = eachChild.children;
                if (subChildren != null && !subChildren.isEmpty()) {
                    childrenStack.push(subChildren);
                }
            }
        }
    }

    /**
     * prepends the given children to the first position of the current children of
     * this object. <br>
     * Eg:-
     *
     * <pre><code>
     * Div div = new Div(null, new Id("one")) {
     *     {
     *         new Div(this, new Id("child1"));
     *     }
     * };
     *
     * Span span = new Span(null);
     *
     * P p = new P(null);
     *
     * Br br = new Br(null);
     *
     * div.prependChildren(span, p, br);
     *
     * System.out.println(div.toHtmlString());
     *
     * </code></pre>
     * <p>
     * This prints
     *
     * <pre><code>
     * &lt;div id=&quot;one&quot;&gt;
     *     &lt;span&gt;&lt;/span&gt;
     *     &lt;p&gt;&lt;/p&gt;
     *     &lt;br/&gt;
     *     &lt;div id=&quot;child1&quot;&gt;&lt;/div&gt;
     * &lt;/div&gt;
     * </code></pre>
     *
     * @param children children to prepend in this object's existing children.
     * @author WFF
     * @since 3.0.1
     */
    public void prependChildren(final AbstractHtml... children) {

        // inserted, listener invoked
        boolean[] results = { false, false };

        final List<Lock> locks = lockAndGetWriteLocks(children);

        // sharedObject should be after locking
        final AbstractHtml5SharedObject sharedObject = this.sharedObject;

        final Deque<AbstractHtml> parentGainedListenerTags;
        final Deque<AbstractHtml> parentLostListenerTags;

//        final Lock lock = sharedObject.getLock(ACCESS_OBJECT).writeLock();
//        lock.lock();

        try {
            validateChildren(children);
            parentGainedListenerTags = buildParentGainedListenerTags(children);
            parentLostListenerTags = buildParentLostListenerTags(children);
            for (final AbstractHtml each : children) {
                each.applyURIChange(sharedObject);
            }

            final Set<AbstractHtml> thisChildren = this.children;
            final Iterator<AbstractHtml> iterator = thisChildren.iterator();
            if (iterator.hasNext()) {
                final AbstractHtml firstChild = iterator.next();

                removeFromSharedTagContent(firstChild);

                final AbstractHtml[] removedParentChildren = thisChildren
                        .toArray(new AbstractHtml[thisChildren.size()]);

                results = firstChild.insertBefore(removedParentChildren, children);

            } else {

                // NB: similar impl is done in appendChildren(AbstractHtml...
                // children) so any improvements here will be applicable in
                // there also
                final Collection<ChildMovedEvent> movedOrAppended = new ArrayDeque<>(children.length);

                for (final AbstractHtml child : children) {
                    final AbstractHtml previousParent = child.parent;

                    if (child.parent != null) {
                        if (child.parent.sharedObject.getChildTagAppendListener(ACCESS_OBJECT) == null) {
                            removeFromTagByWffIdMap(child, child.parent.sharedObject.getTagByWffId(ACCESS_OBJECT));
                        } // else {TODO also write the code to push
                          // changes to the other BrowserPage}
                    } else {
                        removeDataWffIdFromHierarchyLockless(child);
                    }

                    addChild(child, false);

                    final ChildMovedEvent event = new ChildMovedEvent(previousParent, this, child);
                    movedOrAppended.add(event);

                }

                final ChildTagAppendListener listener = sharedObject.getChildTagAppendListener(ACCESS_OBJECT);
                if (listener != null) {
                    listener.childrendAppendedOrMoved(movedOrAppended);
                    results[1] = true;
                }

            }
        } finally {
//            lock.unlock();
            for (final Lock lck : locks) {
                lck.unlock();
            }
        }

        if (results[1]) {
            final PushQueue pushQueue = sharedObject.getPushQueue(ACCESS_OBJECT);
            if (pushQueue != null) {
                pushQueue.push();
            }
        }

        pollAndInvokeParentLostListeners(parentLostListenerTags);
        // Note: GainedListener should be invoked after ParentLostListener
        pollAndInvokeParentGainedListeners(parentGainedListenerTags);
    }

    /**
     * NB: lockless implementation of
     * {@code appendChildren(AbstractHtml... children)} <br>
     * adds the given children to the last position of the current children of this
     * object.
     * <p>
     * NB: Previously this method was consumed by insertAfter method but since 3.0.7
     * it has it own implementation like insertBefore method. This method can also
     * be removed if not required.
     *
     * @param children children to append in this object's existing children.
     * @return in zeroth index: true if inserted otherwise false. in first index:
     *         true if listener invoked otherwise false.
     * @author WFF
     * @since 3.0.1
     */
    private boolean[] appendChildrenLockless(final AbstractHtml... children) {

        // any changes to this method should also be applied in
        // appendChildren(AbstractHtml... children)

        final AbstractHtml5SharedObject sharedObject = this.sharedObject;

        for (final AbstractHtml child : children) {
            child.applyURIChange(sharedObject);
        }

        // inserted, listener invoked
        final boolean[] results = { false, false };

        final Collection<ChildMovedEvent> movedOrAppended = new ArrayDeque<>(children.length);

        for (final AbstractHtml child : children) {
            final AbstractHtml previousParent = child.parent;

            if (child.parent != null) {
                if (child.parent.sharedObject.getChildTagAppendListener(ACCESS_OBJECT) == null) {
                    removeFromTagByWffIdMap(child, child.parent.sharedObject.getTagByWffId(ACCESS_OBJECT));
                } // else {TODO also write the code to push
                  // changes to the other BrowserPage}
            } else {
                removeDataWffIdFromHierarchy(sharedObject, child);
            }

            if (addChild(child, false)) {
                results[0] = true;
            }

            final ChildMovedEvent event = new ChildMovedEvent(previousParent, this, child);
            movedOrAppended.add(event);

        }

        final ChildTagAppendListener listener = sharedObject.getChildTagAppendListener(ACCESS_OBJECT);
        if (listener != null) {
            listener.childrendAppendedOrMoved(movedOrAppended);
            results[1] = true;
        }

        return results;
    }

    /**
     * initializes attributes in this.attributes and also in attributesMap. this
     * should be called only once per object.
     *
     * @param attributes
     * @author WFF
     * @since 2.0.0
     */
    private void initAttributes(final AbstractAttribute... attributes) {

        if (attributes == null || attributes.length == 0) {
            return;
        }

        // initial capacity must be greater than 1 instead of 1
        // because the load factor is 0.75f
        // possible initial attributes on a tag may be maximum 8
        // they may be id, name, class, value, style, onchange, placeholder
        final Map<String, AbstractAttribute> map = new ConcurrentHashMap<>(8);

        for (final AbstractAttribute attribute : attributes) {
            map.put(attribute.getAttributeName(), attribute);
        }

        attributesMap = map;

        this.attributes = map.values().toArray(new AbstractAttribute[map.size()]);
    }

    /**
     * adds the given attributes to this tag.
     *
     * @param attributes attributes to add
     * @author WFF
     * @since 2.0.0
     */
    public void addAttributes(final AbstractAttribute... attributes) {
        addAttributes(true, attributes);
    }

    /**
     * adds the given attributes to this tag.
     *
     * @param updateClient true to update client browser page if it is available.
     *                     The default value is true but it will be ignored if there
     *                     is no client browser page.
     * @param attributes   attributes to add
     * @author WFF
     * @since 2.0.0 initial implementation
     * @since 2.0.15 changed to public scope
     */
    public void addAttributes(final boolean updateClient, final AbstractAttribute... attributes) {
        final Set<AbstractAttribute> attrbs = new LinkedHashSet<>(attributes.length);
        for (final AbstractAttribute attr : attributes) {
            attrbs.add(attr);
        }

        addAttributes(updateClient, attrbs);
    }

    /**
     * adds the given attributes to this tag.
     *
     * @param updateClient true to update client browser page if it is available.
     *                     The default value is true but it will be ignored if there
     *                     is no client browser page.
     * @param attributes   attributes to add
     * @author WFF
     * @since 3.0.15 initial implementation
     */
    public void addAttributes(final boolean updateClient, final Collection<AbstractAttribute> attributes) {

        boolean listenerInvoked = false;

        final List<Lock> attrLocks = AttributeUtil.lockAndGetWriteLocks(ACCESS_OBJECT, attributes);

        final Lock lock = lockAndGetWriteLock();

        final AbstractHtml5SharedObject sharedObject = this.sharedObject;

        try {
            listenerInvoked = addAttributesLockless(updateClient, attributes.toArray(new AbstractAttribute[0]));
        } finally {
            lock.unlock();
            for (final Lock attrLock : attrLocks) {
                attrLock.unlock();
            }
        }

        if (listenerInvoked) {
            final PushQueue pushQueue = sharedObject.getPushQueue(ACCESS_OBJECT);
            if (pushQueue != null) {
                pushQueue.push();
            }
        }

    }

    /**
     * adds the given attributes to this tag.
     *
     * @param updateClient true to update client browser page if it is available.
     *                     The default value is true but it will be ignored if there
     *                     is no client browser page.
     * @param attributes   attributes to add
     * @return true if the listener invoked else false. DO NOT confuse it with
     *         whether attributes are added.
     * @author WFF
     * @since 3.0.1 initial implementation
     */
    private boolean addAttributesLockless(final boolean updateClient, final AbstractAttribute... attributes) {

        final AbstractHtml5SharedObject sharedObject = this.sharedObject;
        AbstractAttribute[] thisAttributes = this.attributes;

        boolean listenerInvoked = false;

        Map<String, AbstractAttribute> attributesMap = this.attributesMap;
        if (attributesMap == null) {
            commonLock().lock();
            try {
                attributesMap = this.attributesMap;
                if (attributesMap == null) {
                    attributesMap = new ConcurrentHashMap<>(attributes.length);
                    this.attributesMap = attributesMap;
                }
            } finally {
                commonLock().unlock();
            }
        }

        if (thisAttributes != null) {
            for (final AbstractAttribute attribute : thisAttributes) {
                attributesMap.put(attribute.getAttributeName(), attribute);
            }
        }

        for (final AbstractAttribute attribute : attributes) {
            AttributeUtil.assignOwnerTag(ACCESS_OBJECT, attribute, this);
            final AbstractAttribute previous = attributesMap.put(attribute.getAttributeName(), attribute);
            if (previous != null && !attribute.equals(previous)) {
                final Lock lock = AttributeUtil.lockAndGetWriteLock(ACCESS_OBJECT, previous);
                try {
                    AttributeUtil.unassignOwnerTag(ACCESS_OBJECT, previous, this);
                } finally {
                    lock.unlock();
                }

            }
        }

        thisAttributes = attributesMap.values().toArray(new AbstractAttribute[attributesMap.size()]);
        this.attributes = thisAttributes;
        setModified(true);

        sharedObject.setChildModified(true, ACCESS_OBJECT);

        // invokeListener
        if (updateClient) {
            final AttributeAddListener attributeAddListener = sharedObject.getAttributeAddListener(ACCESS_OBJECT);
            if (attributeAddListener != null) {
                final AttributeAddListener.AddEvent event = new AttributeAddListener.AddEvent(this, attributes);
                attributeAddListener.addedAttributes(event);
                listenerInvoked = true;
            }
        }

        return listenerInvoked;
    }

    /**
     * @return the collection of attributes
     * @author WFF
     * @since 2.0.0
     */
    public Collection<AbstractAttribute> getAttributes() {

        Map<String, AbstractAttribute> attributesMap = this.attributesMap;
        if (attributesMap == null) {
            return null;
        }

        final Lock lock = lockAndGetReadLock();

        try {
            attributesMap = this.attributesMap;
            if (attributesMap != null) {
                final Collection<AbstractAttribute> result = List.copyOf(attributesMap.values());
                return result;
            }
        } finally {
            lock.unlock();
        }
        return null;
    }

    /**
     * @return the collection of attributes
     * @author WFF
     * @since 3.0.15
     */
    final Collection<AbstractAttribute> getAttributesLockless() {

        final Map<String, AbstractAttribute> attributesMap = this.attributesMap;

        if (attributesMap != null) {
            final Collection<AbstractAttribute> result = List.copyOf(attributesMap.values());
            return result;
        }
        return null;
    }

    /**
     * @return true if it contains attributes
     * @since 12.0.2
     */
    final boolean containsAttributesLockless() {
        final Map<String, AbstractAttribute> attributesMap = this.attributesMap;
        if (attributesMap != null) {
            return !attributesMap.isEmpty();
        }
        return false;
    }

    /**
     * gets the attribute by attribute name
     *
     * @return the attribute object for the given attribute name if exists otherwise
     *         returns null.
     * @author WFF
     * @since 2.0.0
     */
    public AbstractAttribute getAttributeByName(final String attributeName) {
        Map<String, AbstractAttribute> attributesMap = this.attributesMap;
        if (attributesMap == null) {
            return null;
        }

        final Lock lock = lockAndGetReadLock();
        AbstractAttribute result = null;
        try {
            attributesMap = this.attributesMap;

            if (attributesMap != null) {
                result = attributesMap.get(attributeName);
            }
        } finally {
            lock.unlock();
        }
        return result;
    }

    /**
     * gets the attribute by attribute name. NB: only for internal use.
     *
     * @return the attribute object for the given attribute name if exists otherwise
     *         returns null.
     * @author WFF
     * @since 3.0.15
     */
    final AbstractAttribute getAttributeByNameLockless(final String attributeName) {

        final Map<String, AbstractAttribute> attributesMap = this.attributesMap;

        if (attributesMap != null) {
            return attributesMap.get(attributeName);
        }
        return null;
    }

    /**
     * removes the given attributes from this tag.
     *
     * @param attributes attributes to remove
     * @return true if any of the attributes are removed.
     * @author WFF
     * @since 2.0.0
     */
    public boolean removeAttributes(final AbstractAttribute... attributes) {
        return removeAttributes(true, attributes);
    }

    /**
     * removes the given attributes from this tag.
     *
     * @param invokeListener true to invoke listener
     * @param attributes     attributes to remove
     * @return true if any of the attributes are removed.
     * @author WFF
     * @since 2.0.0
     */
    public final boolean removeAttributes(@SuppressWarnings("exports") final SecurityObject accessObject,
            final boolean invokeListener, final AbstractAttribute... attributes) {

        if (accessObject == null || !(IndexedClassType.BROWSER_PAGE.equals(accessObject.forClassType()))) {
            throw new WffSecurityException("Not allowed to consume this method. This method is for internal use.");

        }
        // lock is not required here. it is used in
        // removeAttributes(invokeListener, attributes)
        return removeAttributes(invokeListener, attributes);
    }

    /**
     * removes the given attributes from this tag.
     *
     * @param updateClient true to update client browser page if it is available.
     *                     The default value is true but it will be ignored if there
     *                     is no client browser page.
     * @param attributes   attributes to remove
     * @return true if any of the attributes are removed.
     * @author WFF
     * @since 2.0.0 initial implementation
     * @since 2.0.15 changed to public scope
     */
    public boolean removeAttributes(final boolean updateClient, final AbstractAttribute... attributes) {

        boolean listenerInvoked = false;
        boolean removed = false;

        final List<Lock> attrLocks = AttributeUtil.lockAndGetWriteLocks(ACCESS_OBJECT, attributes);

        final Lock lock = lockAndGetWriteLock();
        final AbstractHtml5SharedObject sharedObject = this.sharedObject;
        try {

            final Map<String, AbstractAttribute> attributesMap = this.attributesMap;

            if (attributesMap == null) {
                return false;
            }

            final List<AbstractAttribute> removedAttributes = new ArrayList<>(attributes.length);

            for (final AbstractAttribute attribute : attributes) {

                if (AttributeUtil.unassignOwnerTag(ACCESS_OBJECT, attribute, this)
                        && attributesMap.remove(attribute.getAttributeName()) != null) {

                    removedAttributes.add(attribute);
                }

            }

            removed = !removedAttributes.isEmpty();

            if (removed) {
                this.attributes = attributesMap.values().toArray(new AbstractAttribute[attributesMap.size()]);
                setModified(true);
                sharedObject.setChildModified(true, ACCESS_OBJECT);

                // invokeListener
                if (updateClient) {
                    final AttributeRemoveListener listener = sharedObject.getAttributeRemoveListener(ACCESS_OBJECT);
                    if (listener != null) {
                        final AttributeRemoveListener.RemovedEvent event = new AttributeRemoveListener.RemovedEvent(
                                this, removedAttributes, null);

                        listener.removedAttributes(event);
                        listenerInvoked = true;

                    }
                }
            }
        } finally {
            lock.unlock();
            for (final Lock attrLock : attrLocks) {
                attrLock.unlock();
            }
        }

        if (listenerInvoked) {
            final PushQueue pushQueue = sharedObject.getPushQueue(ACCESS_OBJECT);
            if (pushQueue != null) {
                pushQueue.push();
            }
        }

        return removed;
    }

    /**
     * removes the dataWffId from this tag.
     *
     * @return true if dataWffId is removed.
     * @since 3.0.9
     */
    private boolean removeDataWffId() {

        boolean removed = false;

        DataWffId thisDataWffId = dataWffId;
        final Map<String, AbstractAttribute> thisAttributesMap = attributesMap;

        if (thisDataWffId == null || thisAttributesMap == null) {
            return false;
        }

        if (AttributeUtil.unassignOwnerTag(ACCESS_OBJECT, thisDataWffId, this)) {
            final String attributeName = thisDataWffId.getAttributeName();
            final AbstractAttribute prev = thisAttributesMap.remove(attributeName);
            removed = prev != null;
        }

        if (removed) {

            attributes = thisAttributesMap.values().toArray(new AbstractAttribute[thisAttributesMap.size()]);

            setModified(true);
            sharedObject.setChildModified(true, ACCESS_OBJECT);
        }

        thisDataWffId = null;
        dataWffId = null;

        return removed;
    }

    /**
     * removes the given attributes from this tag.
     *
     * @param attributeNames to remove the attributes having in the given names.
     * @return true if any of the attributes are removed.
     * @author WFF
     * @since 2.0.0
     */
    public boolean removeAttributes(final String... attributeNames) {
        return removeAttributes(true, attributeNames);
    }

    /**
     * removes the given attributes from this tag.
     *
     * @param invokeListener true to invoke listener
     * @param attributeNames to remove the attributes having in the given names.
     * @return true if any of the attributes are removed.
     * @author WFF
     * @since 2.0.0
     */
    public final boolean removeAttributes(@SuppressWarnings("exports") final SecurityObject accessObject,
            final boolean invokeListener, final String... attributeNames) {
        if (accessObject == null || !(IndexedClassType.BROWSER_PAGE.equals(accessObject.forClassType()))) {
            throw new WffSecurityException("Not allowed to consume this method. This method is for internal use.");

        }
        // lock is not required here. it is used in
        // removeAttributes(invokeListener, attributes)
        return removeAttributes(invokeListener, attributeNames);
    }

    /**
     * removes the given attributes from this tag.
     *
     * @param updateClient   true to update client browser page if it is available.
     *                       The default value is true but it will be ignored if
     *                       there is no client browser page.
     * @param attributeNames to remove the attributes having in the given names.
     * @return true if any of the attributes are removed.
     * @author WFF
     * @since 2.0.0 initial implementation
     * @since 2.0.15 changed to public scope
     */
    public boolean removeAttributes(final boolean updateClient, final String... attributeNames) {

        Lock lock = lockAndGetReadLock();

        final Set<AbstractAttribute> attributesToRemove = new HashSet<AbstractAttribute>(attributeNames.length);

        try {
            if (attributesMap == null) {
                return false;
            }
            for (final String attributeName : attributeNames) {

                final AbstractAttribute attribute = attributesMap.get(attributeName);

                if (attribute != null) {
                    attributesToRemove.add(attribute);
                }

//                if (attribute != null) {
//                    attribute.unsetOwnerTag(this);
//                    attributesMap.remove(attributeName);
//                    removed = true;
//                }

            }

        } finally {
            lock.unlock();
        }

        if (attributesToRemove.isEmpty()) {
            return false;
        }

        boolean removed = false;
        boolean listenerInvoked = false;

        final List<Lock> attrLocks = AttributeUtil.lockAndGetWriteLocks(ACCESS_OBJECT,
                attributesToRemove.toArray(new AbstractAttribute[attributesToRemove.size()]));

        lock = lockAndGetWriteLock();
        final AbstractHtml5SharedObject sharedObject = this.sharedObject;

        try {

            if (attributesMap == null) {
                return false;
            }

            final List<String> removedAttributeNames = new ArrayList<>(attributesToRemove.size());

            for (final AbstractAttribute attribute : attributesToRemove) {
                if (AttributeUtil.unassignOwnerTag(ACCESS_OBJECT, attribute, this)
                        && attributesMap.remove(attribute.getAttributeName()) != null) {
                    removedAttributeNames.add(attribute.getAttributeName());
                }
            }

            removed = !removedAttributeNames.isEmpty();

            if (removed) {
                attributes = attributesMap.values().toArray(new AbstractAttribute[attributesMap.size()]);
                setModified(true);
                sharedObject.setChildModified(true, ACCESS_OBJECT);

                // invokeListener
                if (updateClient) {
                    final AttributeRemoveListener listener = sharedObject.getAttributeRemoveListener(ACCESS_OBJECT);
                    if (listener != null) {
                        final AttributeRemoveListener.RemovedEvent event = new AttributeRemoveListener.RemovedEvent(
                                this, null, removedAttributeNames.toArray(new String[removedAttributeNames.size()]));

                        listener.removedAttributes(event);
                        listenerInvoked = true;
                    }
                }
            }
        } finally {
            if (attrLocks != null) {
                for (final Lock attrLock : attrLocks) {
                    attrLock.unlock();
                }
            }

            lock.unlock();
        }

        if (listenerInvoked) {
            final PushQueue pushQueue = sharedObject.getPushQueue(ACCESS_OBJECT);
            if (pushQueue != null) {
                pushQueue.push();
            }
        }

        return removed;
    }

    /**
     * should be invoked to generate opening and closing tag base class containing
     * the functionalities to generate html string.
     *
     * @param tagType
     * @param tagName TODO
     * @param base    TODO
     * @author WFF
     */
    protected AbstractHtml(final TagType tagType, final String tagName, final AbstractHtml base,
            final AbstractAttribute[] attributes) {
        this(tagType, tagName, null, base, attributes);
    }

    /**
     * should be invoked to generate opening and closing tag base class containing
     * the functionalities to generate html string.
     *
     * @param tagType
     * @param tagName           TODO
     * @param tagNameIndexBytes There is an index value for the each tag name in tag
     *                          registry then pass its indexBytes otherwise pass
     *                          null. Never pass an arbitrary value if the tag name
     *                          has no valid index value in TagRegistry.
     * @param base              TODO
     * @author WFF
     * @since 3.0.3
     */
    private AbstractHtml(final TagType tagType, final String tagName, final byte[] tagNameIndexBytes,
            final AbstractHtml base, final AbstractAttribute[] attributes) {

        this.tagType = tagType;
        this.tagName = tagName;
        this.tagNameIndexBytes = tagNameIndexBytes;
        noTagContentTypeHtml = false;
        htmlStartSBAsFirst = false;

        final List<Lock> attrLocks = AttributeUtil.lockAndGetWriteLocks(ACCESS_OBJECT, attributes);

        final Lock lock;

        if (base == null) {
            sharedObject = new AbstractHtml5SharedObject(this);
            lock = lockAndGetWriteLock();
        } else {
            lock = base.lockAndGetWriteLock();
        }

        try {

            initAttributes(attributes);

            htmlStartSB = new StringBuilder(
                    tagName == null ? 0 : tagName.length() + 2 + ((attributes == null ? 0 : attributes.length) * 16));
            initInConstructor();

            markOwnerTag(attributes);

            buildOpeningTag(false);

            closingTag = TagType.OPENING_CLOSING.equals(tagType) ? buildClosingTag() : "";

            if (base != null) {
                base.addChildLockless(this);
                // base.children.add(this);
                // not required it is handled in the above add method
                // parent = base;
                // sharedObject = base.sharedObject;
            }
            //
            // else {
            // sharedObject = new AbstractHtml5SharedObject(this);
            // }

        } finally {
            lock.unlock();
            if (attrLocks != null) {
                for (final Lock attrLock : attrLocks) {
                    attrLock.unlock();
                }
            }
        }
    }

    /**
     * should be invoked to generate opening and closing tag base class containing
     * the functionalities to generate html string.
     *
     * @param tagType
     * @param preIndexedTagName PreIndexedTagName constant
     * @param base              TODO
     * @author WFF
     * @since 3.0.3
     */
    protected AbstractHtml(final TagType tagType, final PreIndexedTagName preIndexedTagName, final AbstractHtml base,
            final AbstractAttribute[] attributes) {
        this(tagType, preIndexedTagName.tagName(), preIndexedTagName.internalIndexBytes(ACCESS_OBJECT), base,
                attributes);
    }

    /**
     * marks the owner tag in the attributes
     *
     * @param attributes
     * @author WFF
     * @since 1.0.0
     */
    private void markOwnerTag(final AbstractAttribute[] attributes) {
        if (attributes == null) {
            return;
        }
        for (final AbstractAttribute attribute : attributes) {
            AttributeUtil.assignOwnerTag(ACCESS_OBJECT, attribute, this);
        }
    }

    /**
     * to initialize objects in the constructor
     *
     * @author WFF
     * @since 1.0.0
     */
    private void initInConstructor() {
//        htmlStartSB = new StringBuilder(
//                tagName == null ? 0 : tagName.length() + 2 + ((attributes == null ? 0 : attributes.length) * 16));

//        htmlEndSB = new StringBuilder(tagName == null ? 16 : tagName.length() + 3);
    }

    public AbstractHtml getParent() {
        // no lock required here as there is no read and write
        return parent;
    }

    /**
     * @return the unmodifiable list of children
     * @author WFF
     * @since 2.0.0
     */
    public List<AbstractHtml> getChildren() {

        final Lock lock = lockAndGetReadLock();
        try {
            return List.copyOf(children);
        } finally {
            lock.unlock();
        }
    }

    /**
     * @return true if it contains children
     * @since 12.0.0
     */
    public boolean hasChildren() {
        final Lock lock = lockAndGetReadLock();
        try {
            return !children.isEmpty();
        } finally {
            lock.unlock();
        }
    }

    /**
     * gets inner html string
     *
     * @return inner html string.
     * @since 12.0.0
     */
    protected String toInnerHtmlString() {
        final Lock readLock = lockAndGetReadLock();
        try {
            final Set<AbstractHtml> children = this.children;
            if (children.isEmpty()) {
                return "";
            }

            final Iterator<AbstractHtml> iterator;
            if (children.size() == 1 && (iterator = children.iterator()).hasNext()
                    && iterator.next() instanceof final NoTag firstChild) {
                final StringBuilder htmlMiddleSB = ((AbstractHtml) firstChild).htmlMiddleSB;
                if (htmlMiddleSB != null) {
                    return htmlMiddleSB.toString();
                }
                return "";
            }
        } finally {
            readLock.unlock();
        }

        // should be WriteLock as toHtmlStringLockless requires WriteLock
        final Lock lock = lockAndGetWriteLock();
        try {
            final Set<AbstractHtml> children = this.children;
            if (!children.isEmpty()) {
                final StringBuilder builder = new StringBuilder();
                for (final AbstractHtml child : children) {
                    builder.append(child.toHtmlStringLockless());
                }
                return builder.toString();
            }
        } finally {
            lock.unlock();
        }
        return "";
    }

    /**
     * NB: this method is for internal use. The returned object should not be
     * modified.
     *
     * @return the internal children object.
     * @author WFF
     * @since 2.0.0
     */
    public final Set<AbstractHtml> getChildren(@SuppressWarnings("exports") final SecurityObject accessObject) {

        if (accessObject == null || !(IndexedClassType.BROWSER_PAGE.equals(accessObject.forClassType()))) {
            throw new WffSecurityException("Not allowed to consume this method. This method is for internal use.");
        }
        return children;
    }

    /**
     * Removes all current children and adds the given children under this tag.
     * Unlike setter methods, it will not reuse the given set object but it will
     * copy all children from the given set object. <br>
     *
     * @param children which will be set as the children tag after removing all
     *                 current children. Empty set or null will remove all current
     *                 children from this tag.
     * @author WFF
     * @since 2.1.12 proper implementation is available since 2.1.12
     */
    public void setChildren(final Set<AbstractHtml> children) {
        if (children == null || children.isEmpty()) {
            removeAllChildren();
        } else {
            addInnerHtmls(children.toArray(new AbstractHtml[children.size()]));
        }
    }

    /**
     * Gets the children of this tag as an array. An efficient way to get the
     * children as an array.
     *
     * @return the array of children of this tag.
     * @author WFF
     * @since 3.0.1
     */
    public AbstractHtml[] getChildrenAsArray() {

        final Lock lock = lockAndGetReadLock();
        try {
            return children.toArray(new AbstractHtml[children.size()]);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Gets the first child of this tag. The efficient way to get the first child.
     *
     * @return the first child of this tag or null if there is no child.
     * @author WFF
     * @since 3.0.1
     */
    public AbstractHtml getFirstChild() {
        // this block must be synchronized otherwise may get null or
        // ConcurrentModificationException
        // the test cases are written to check its thread safety and can be
        // reproduce by uncommenting this synchronized block, checkout
        // AbstractHtmlTest class for it.
        // synchronized (children) {
        //
        // }
        // it's been replaced with locking

        final Lock lock = lockAndGetReadLock();
        try {

            // this must be most efficient because the javadoc of findFirst says
            // "This is a short-circuiting terminal operation."
            // return (T) children.stream().findFirst().orElse(null);

            // but as per CodePerformanceTest.testPerformanceOfFindFirst
            // the below is faster

            final Iterator<AbstractHtml> iterator = children.iterator();
            if (iterator.hasNext()) {
                return iterator.next();
            }
            return null;
        } finally {
            lock.unlock();
        }

    }

    /**
     * Gets the last child of this tag. The efficient way to get the last child.
     *
     * @return the last child of this tag or null if there is no child.
     * @author WFF
     * @since 3.0.15
     */
    public AbstractHtml getLastChild() {
        // this block must be synchronized otherwise may get null or
        // ConcurrentModificationException
        // the test cases are written to check its thread safety and can be
        // reproduce by uncommenting this synchronized block, checkout
        // AbstractHtmlTest class for it.
        // synchronized (children) {
        //
        // }
        // it's been replaced with locking

        final Lock lock = lockAndGetReadLock();
        try {

            // this must be most efficient because the javadoc of findFirst says
            // "This is a short-circuiting terminal operation."
            // return (T) children.stream().findFirst().orElse(null);

            // but as per CodePerformanceTest.testPerformanceOfFindFirst
            // the below is faster
            // so for getting the last the following must be fast
            final Iterator<AbstractHtml> iterator = children.iterator();
            AbstractHtml lastChild = null;
            while (iterator.hasNext()) {
                lastChild = iterator.next();
            }
            return lastChild;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Gets the number of children in this tag. An efficient way to find the size of
     * children.
     *
     * @return the size of children.
     * @author WFF
     * @since 3.0.1
     */
    public int getChildrenSize() {
        final Lock lock = lockAndGetReadLock();
        try {

            return children.size();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Gets the number of children in this tag. An efficient way to find the size of
     * children.
     *
     * @return the size of children.
     * @author WFF
     * @since 3.0.6
     */
    int getChildrenSizeLockless() {
        return children.size();
    }

    /**
     * Gets the child at the specified position. An efficient way to get the child
     * at particular position. If you want to get the child at 0th(zeroth) index
     * then use {@code AbstractHtml#getFirstChild()} method instead of this method.
     *
     * @param index from this index the tag will be returned
     * @return the child at the specified index.
     * @author WFF
     * @since 3.0.1
     */
    public AbstractHtml getChildAt(final int index) {
        if (index < 0) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        final Lock lock = lockAndGetReadLock();
        try {

            final int size = children.size();
            if (size == 0 || index > (size - 1)) {
                throw new ArrayIndexOutOfBoundsException(index);
            }

            int count = 0;
            final Iterator<AbstractHtml> iterator = children.iterator();
            while (iterator.hasNext()) {
                final AbstractHtml each = iterator.next();
                if (count == index) {
                    return each;
                }
                count++;
            }

            return null;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Gets the nth index of the given tag in this tag's children.
     *
     * @param child
     * @return the index of this child in this tag's children. If the given tag is
     *         not a child in this tag's children then -1 will be returned.
     * @since 3.0.7
     */
    public int getIndexByChild(final AbstractHtml child) {
        // NB: this logic may be improved by declaring a childIndex variable in
        // tag so when the tag is added as a child in any tag childIndex needs
        // to be updated. Removing/adding another tag can also affect the
        // childIndex of that tag so that needs to be handled.

        final Lock lock = lockAndGetReadLock();
        try {
            final Iterator<AbstractHtml> iterator = children.iterator();
            int index = 0;
            while (iterator.hasNext()) {
                if (iterator.next().equals(child)) {
                    return index;
                }
                index++;
            }
        } finally {
            lock.unlock();
        }
        return -1;
    }

    /**
     * NB: only for internal use. Use getIndexByChild(AbstractHtml).<br>
     * <br>
     * <p>
     * Gets the nth index of the given tag in this tag's children.
     *
     * @param accessObject
     * @param child
     * @return the index of this child in this tag's children. If the given tag is
     *         not a child in this tag's children then -1 will be returned.
     * @since 3.0.7
     */
    public final int getIndexByChild(@SuppressWarnings("exports") final SecurityObject accessObject,
            final AbstractHtml child) {

        // Lockless method to get child index

        if (accessObject == null || !(IndexedClassType.BROWSER_PAGE.equals(accessObject.forClassType()))) {
            throw new WffSecurityException("Not allowed to consume this method. This method is for internal use.");
        }

        // NB: this logic may be improved by declaring a childIndex variable in
        // tag so when the tag is added as a child in any tag childIndex needs
        // to be updated. Removing/adding another tag can also affect the
        // childIndex of that tag so that needs to be handled.

        final Iterator<AbstractHtml> iterator = children.iterator();
        int index = 0;
        while (iterator.hasNext()) {
            if (iterator.next().equals(child)) {
                return index;
            }
            index++;
        }
        return -1;
    }

    /**
     * Checks whether a tag is contained in its direct children. An efficient way to
     * check if the given tag is a direct child of this tag.
     *
     * @param childTag
     * @return true if the given tag is a child of this tags.
     * @author WFF
     * @since 3.0.1
     */
    public boolean containsChild(final AbstractHtml childTag) {

        final Lock lock = lockAndGetReadLock();
        try {
            return children.contains(childTag);
        } finally {
            lock.unlock();
        }
    }

    /**
     * For internal purpose. Not recommended for external purpose.
     *
     * @return the opening tag of this object
     * @author WFF
     */
    public final String getOpeningTag() {
        if (isRebuild() || isModified()) {
            final Lock lock = lockAndGetWriteLock();
            try {
                buildOpeningTag(true);
            } finally {
                lock.unlock();
            }

        }
        return openingTag;
    }

    /**
     * For internal purpose.
     *
     * @return the closing tag of this object
     * @author WFF
     */
    public String getClosingTag() {
        return closingTag;
    }

    /**
     * @return {@code String} equalent to the html string of the tag including the
     *         child tags.
     * @author WFF
     * @since 1.0.0
     */
    protected String getPrintStructure() {
        if (isRebuild() || isModified()) {
            final Lock lock = lockAndGetWriteLock();
            try {
                final String printStructure = getPrintStructure(true);
                setRebuild(false);
                return printStructure;
            } finally {
                lock.unlock();
            }
        } else {
            final Lock lock = lockAndGetReadLock();
            try {
                final String html = tagBuilder.toString();
                return html;
            } finally {
                lock.unlock();
            }

        }

    }

    /**
     * @param rebuild
     * @return
     * @author WFF
     * @since 1.0.0
     */
    protected String getPrintStructure(final boolean rebuild) {

        if (rebuild || isRebuild() || isModified()) {

            final Lock lock = lockAndGetWriteLock();
            try {
                beforePrintStructure();
                if (tagBuilder.length() > 0) {
                    tagBuilder.delete(0, tagBuilder.length());
                }
                // passed 2 instead of 1 because the load factor is 0.75f
                final Set<AbstractHtml> localChildren = new LinkedHashSet<>(2);
                localChildren.add(this);
                recurChildren(tagBuilder, localChildren, true);
                setRebuild(false);
                tagBuilder.trimToSize();
                final String html = tagBuilder.toString();
                return html;
            } finally {
                lock.unlock();
            }
        }
        final Lock lock = lockAndGetReadLock();
        try {
            final String html = tagBuilder.toString();
            return html;
        } finally {
            lock.unlock();
        }
    }

    /**
     * @param charset
     * @param os
     * @param rebuild
     * @return the total number of bytes written
     * @throws IOException
     * @since 1.0.0
     */
    protected int writePrintStructureToOutputStream(final Charset charset, final OutputStream os, final boolean rebuild)
            throws IOException {
        return writePrintStructureToOutputStream(os, rebuild, charset, false);
    }

    /**
     * @param os
     * @param rebuild
     * @param charset
     * @param flushOnWrite true to flush on each write to OutputStream
     * @return the total number of bytes written
     * @throws IOException
     * @since 3.0.2
     */
    protected int writePrintStructureToOutputStream(final OutputStream os, final boolean rebuild, final Charset charset,
            final boolean flushOnWrite) throws IOException {

        final Lock lock = lockAndGetWriteLock();
        try {

            beforeWritePrintStructureToOutputStream();
            final int[] totalWritten = { 0 };
            // passed 2 instead of 1 because the load factor is 0.75f
            final Set<AbstractHtml> localChildren = new LinkedHashSet<>(2);
            localChildren.add(this);
            recurChildrenToOutputStream(totalWritten, charset, os, localChildren, rebuild, flushOnWrite);
            return totalWritten[0];
        } finally {
            lock.unlock();
        }
    }

    // for future development

    /**
     * @param rebuild
     * @throws IOException
     * @author WFF
     * @since 2.0.0
     */
    protected void writePrintStructureToWffBinaryMessageOutputStream(final boolean rebuild) throws IOException {

        final Lock lock = lockAndGetWriteLock();
        try {

            beforeWritePrintStructureToWffBinaryMessageOutputStream();
            // passed 2 instead of 1 because the load factor is 0.75f
            final Set<AbstractHtml> localChildren = new LinkedHashSet<>(2);
            localChildren.add(this);
            recurChildrenToWffBinaryMessageOutputStream(localChildren, true, CommonConstants.DEFAULT_CHARSET);
        } finally {
            lock.unlock();
        }
    }

    /**
     * to form html string from the children
     *
     * @param children
     * @param rebuild  TODO
     * @author WFF
     * @since 1.0.0
     */
    private static void recurChildren(final StringBuilder tagBuilder, final Set<AbstractHtml> children,
            final boolean rebuild) {
        if (children != null && !children.isEmpty()) {
            for (final AbstractHtml child : children) {
                child.setRebuild(rebuild);
                tagBuilder.append(child.getOpeningTag());

                // final Set<AbstractHtml> childrenOfChildren = child.children;
                // declaring a separate local variable childrenOfChildren will
                // consume stack space so directly passed it as argument
                recurChildren(tagBuilder, child.children, rebuild);

                tagBuilder.append(child.closingTag);
            }
        }
    }

    /**
     * Use this method to produce HTML from very heavy and complicated tag
     * hierarchy. For normal and simple HTML hierarchy use {@code toHtmlString}
     * method which is faster than this method. The advantage of
     * {@code toBigHtmlString} over {@code toHtmlString} is it will never throw
     * {@code StackOverflowError}. <br>
     * NB:- this method has not been undergone all testing process.
     *
     * @return the HTML string similar to toHtmlString method.
     * @author WFF
     * @since 2.1.12
     */
    public String toBigHtmlString() {

        final Lock lock = lockAndGetWriteLock();
        try {

            final String printStructure = getPrintStructureWithoutRecursive(getSharedObject().isChildModified());

            if (parent == null) {
                sharedObject.setChildModified(false, ACCESS_OBJECT);
            }

            return printStructure;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Use this method to produce HTML from very heavy and complicated tag
     * hierarchy. For normal and simple HTML hierarchy use {@code toHtmlString}
     * method which is faster than this method. The advantage of
     * {@code toBigHtmlString} over {@code toHtmlString} is it will never throw
     * {@code StackOverflowError}. <br>
     * NB:- this method has not been undergone all testing process.
     *
     * @param rebuild true to rebuild the tag hierarchy or false to return from
     *                cache if available.
     * @return the HTML string similar to toHtmlString method.
     * @author WFF
     * @since 2.1.12
     */
    public String toBigHtmlString(final boolean rebuild) {
        return getPrintStructureWithoutRecursive(rebuild);
    }

    private String getPrintStructureWithoutRecursive(final boolean rebuild) {

        if (rebuild || isRebuild() || isModified()) {
            final Lock lock = lockAndGetWriteLock();
            try {

                beforePrintStructure();
                if (tagBuilder.length() > 0) {
                    tagBuilder.delete(0, tagBuilder.length());
                }

                appendPrintStructureWithoutRecursive(tagBuilder, this, true);
                setRebuild(false);
                tagBuilder.trimToSize();
                final String html = tagBuilder.toString();
                return html;
            } finally {
                lock.unlock();
            }
        }

        final Lock lock = lockAndGetReadLock();
        try {
            final String html = tagBuilder.toString();
            return html;
        } finally {
            lock.unlock();
        }

    }

    /**
     * Use this method to produce HTML from very heavy and complicated tag
     * hierarchy. For normal and simple HTML hierarchy use {@code toOutputStream}
     * method which is faster than this method. The advantage of
     * {@code toBigOutputStream} over {@code toOutputStream} is it will never throw
     * {@code StackOverflowError} and the memory consumed at the time of writing
     * could be available for GC (depends on JVM GC rules). <br>
     * NB:- this method has not been undergone all testing process.
     *
     * @param os the object of {@code OutputStream} to write to.
     * @return the total number of bytes written
     * @throws IOException
     * @since 2.1.12
     */
    public int toBigOutputStream(final OutputStream os) throws IOException {
        return writePrintStructureToOSWithoutRecursive(CommonConstants.DEFAULT_CHARSET, os, true, false);
    }

    /**
     * Use this method to produce HTML from very heavy and complicated tag
     * hierarchy. For normal and simple HTML hierarchy use {@code toOutputStream}
     * method which is faster than this method. The advantage of
     * {@code toBigOutputStream} over {@code toOutputStream} is it will never throw
     * {@code StackOverflowError} and the memory consumed at the time of writing
     * could be available for GC (depends on JVM GC rules). <br>
     * NB:- this method has not been undergone all testing process.
     *
     * @param os      the object of {@code OutputStream} to write to.
     * @param charset the charset
     * @return
     * @throws IOException
     * @since 2.1.12
     */
    public int toBigOutputStream(final OutputStream os, final Charset charset) throws IOException {
        return writePrintStructureToOSWithoutRecursive(charset, os, true, false);
    }

    /**
     * Use this method to produce HTML from very heavy and complicated tag
     * hierarchy. For normal and simple HTML hierarchy use {@code toOutputStream}
     * method which is faster than this method. The advantage of
     * {@code toBigOutputStream} over {@code toOutputStream} is it will never throw
     * {@code StackOverflowError} and the memory consumed at the time of writing
     * could be available for GC (depends on JVM GC rules). <br>
     * NB:- this method has not been undergone all testing process.
     *
     * @param os      the object of {@code OutputStream} to write to.
     * @param charset the charset
     * @return the total number of bytes written
     * @throws IOException
     * @since 2.1.12
     */
    public int toBigOutputStream(final OutputStream os, final String charset) throws IOException {

        if (charset != null) {
            return writePrintStructureToOSWithoutRecursive(Charset.forName(charset), os, true, false);
        }
        return writePrintStructureToOSWithoutRecursive(CommonConstants.DEFAULT_CHARSET, os, true, false);
    }

    /**
     * Use this method to produce HTML from very heavy and complicated tag
     * hierarchy. For normal and simple HTML hierarchy use {@code toOutputStream}
     * method which is faster than this method. The advantage of
     * {@code toBigOutputStream} over {@code toOutputStream} is it will never throw
     * {@code StackOverflowError} and the memory consumed at the time of writing
     * could be available for GC (depends on JVM GC rules). <br>
     * NB:- this method has not been undergone all testing process.
     *
     * @param os      the object of {@code OutputStream} to write to.
     * @param rebuild true to rebuild &amp; false to write previously built bytes.
     * @return the total number of bytes written
     * @throws IOException
     * @since 2.1.12
     */
    public int toBigOutputStream(final OutputStream os, final boolean rebuild) throws IOException {
        return writePrintStructureToOSWithoutRecursive(CommonConstants.DEFAULT_CHARSET, os, rebuild, false);
    }

    /**
     * Use this method to produce HTML from very heavy and complicated tag
     * hierarchy. For normal and simple HTML hierarchy use {@code toOutputStream}
     * method which is faster than this method. The advantage of
     * {@code toBigOutputStream} over {@code toOutputStream} is it will never throw
     * {@code StackOverflowError} and the memory consumed at the time of writing
     * could be available for GC (depends on JVM GC rules). <br>
     * NB:- this method has not been undergone all testing process.
     *
     * @param os      the object of {@code OutputStream} to write to.
     * @param rebuild true to rebuild &amp; false to write previously built bytes.
     * @param charset the charset
     * @return the total number of bytes written
     * @throws IOException
     * @since 2.1.12
     */
    public int toBigOutputStream(final OutputStream os, final boolean rebuild, final Charset charset)
            throws IOException {
        if (charset == null) {
            return writePrintStructureToOSWithoutRecursive(CommonConstants.DEFAULT_CHARSET, os, rebuild, false);
        }
        return writePrintStructureToOSWithoutRecursive(charset, os, rebuild, false);
    }

    /**
     * Use this method to produce HTML from very heavy and complicated tag
     * hierarchy. For normal and simple HTML hierarchy use {@code toOutputStream}
     * method which is faster than this method. The advantage of
     * {@code toBigOutputStream} over {@code toOutputStream} is it will never throw
     * {@code StackOverflowError} and the memory consumed at the time of writing
     * could be available for GC (depends on JVM GC rules). <br>
     * NB:- this method has not been undergone all testing process.
     *
     * @param os           the object of {@code OutputStream} to write to.
     * @param rebuild      true to rebuild &amp; false to write previously built
     *                     bytes.
     * @param charset      the charset
     * @param flushOnWrite true to flush on each write to OutputStream
     * @return the total number of bytes written
     * @throws IOException
     * @since 3.0.15
     */
    public int toBigOutputStream(final OutputStream os, final boolean rebuild, final Charset charset,
            final boolean flushOnWrite) throws IOException {
        if (charset == null) {
            return writePrintStructureToOSWithoutRecursive(CommonConstants.DEFAULT_CHARSET, os, rebuild, flushOnWrite);
        }
        return writePrintStructureToOSWithoutRecursive(charset, os, rebuild, flushOnWrite);
    }

    /**
     * Use this method to produce HTML from very heavy and complicated tag
     * hierarchy. For normal and simple HTML hierarchy use {@code toOutputStream}
     * method which is faster than this method. The advantage of
     * {@code toBigOutputStream} over {@code toOutputStream} is it will never throw
     * {@code StackOverflowError} and the memory consumed at the time of writing
     * could be available for GC (depends on JVM GC rules). <br>
     * NB:- this method has not been undergone all testing process.
     *
     * @param os      the object of {@code OutputStream} to write to.
     * @param rebuild true to rebuild &amp; false to write previously built bytes.
     * @param charset the charset
     * @return the total number of bytes written
     * @throws IOException
     * @since 2.1.12
     */
    public int toBigOutputStream(final OutputStream os, final boolean rebuild, final String charset)
            throws IOException {

        if (charset == null) {
            return writePrintStructureToOSWithoutRecursive(CommonConstants.DEFAULT_CHARSET, os, rebuild, false);
        }
        return writePrintStructureToOSWithoutRecursive(Charset.forName(charset), os, rebuild, false);
    }

    private int writePrintStructureToOSWithoutRecursive(final Charset charset, final OutputStream os,
            final boolean rebuild, final boolean flushOnWrite) throws IOException {

        final Lock lock = lockAndGetWriteLock();
        try {

            beforeWritePrintStructureToOutputStream();
            final int[] totalWritten = { 0 };
            writePrintStructureToOSWithoutRecursive(totalWritten, charset, os, this, rebuild, flushOnWrite);
            return totalWritten[0];

        } finally {
            lock.unlock();
        }
    }

    private static void appendPrintStructureWithoutRecursive(final StringBuilder builder, final AbstractHtml topBase,
            final boolean rebuild) {

        AbstractHtml current = topBase;

        while (current != null) {

            final AbstractHtml child = current;
            current = null;

            final AbstractHtml bottomChild = appendOpeningTagAndReturnBottomTag(builder, child, rebuild);

            builder.append(bottomChild.closingTag);

            if (topBase.equals(bottomChild)) {
                break;
            }

            final List<AbstractHtml> childrenHoldingBottomChild = List.copyOf(bottomChild.parent.children);

            final int indexOfNextToBottomChild = childrenHoldingBottomChild.indexOf(bottomChild) + 1;

            if (indexOfNextToBottomChild < childrenHoldingBottomChild.size()) {
                final AbstractHtml nextToBottomChild = childrenHoldingBottomChild.get(indexOfNextToBottomChild);
                current = nextToBottomChild;
            } else {

                if (bottomChild.parent.parent == null) {
                    builder.append(bottomChild.parent.closingTag);
                    break;
                }

                final List<AbstractHtml> childrenHoldingParent = List.copyOf(bottomChild.parent.parent.children);

                final int indexOfNextToBottomParent = childrenHoldingParent.indexOf(bottomChild.parent) + 1;

                if (indexOfNextToBottomParent < childrenHoldingParent.size()) {
                    builder.append(bottomChild.parent.closingTag);

                    if (topBase.equals(bottomChild.parent)) {
                        break;
                    }

                    final AbstractHtml nextToParent = childrenHoldingParent.get(indexOfNextToBottomParent);
                    current = nextToParent;
                } else {
                    current = appendClosingTagUptoRootReturnFirstMiddleChild(builder, topBase, bottomChild);
                }

            }

        }

    }

    /**
     * @param totalWritten
     * @param charset
     * @param os
     * @param topBase
     * @param rebuild
     * @param flushOnWrite
     * @throws IOException
     * @author WFF
     * @since 2.1.12
     */
    private static void writePrintStructureToOSWithoutRecursive(final int[] totalWritten, final Charset charset,
            final OutputStream os, final AbstractHtml topBase, final boolean rebuild, final boolean flushOnWrite)
            throws IOException {

        AbstractHtml current = topBase;

        while (current != null) {

            final AbstractHtml child = current;
            current = null;

            final AbstractHtml bottomChild = writeOpeningTagAndReturnBottomTag(totalWritten, charset, os, child,
                    rebuild);

            {
                final byte[] closingTagBytes = bottomChild.closingTag.getBytes(charset);
                os.write(closingTagBytes);
                if (flushOnWrite) {
                    os.flush();
                }
                totalWritten[0] += closingTagBytes.length;

                if (topBase.equals(bottomChild)) {
                    break;
                }
            }

            final List<AbstractHtml> childrenHoldingBottomChild = List.copyOf(bottomChild.parent.children);

            final int indexOfNextToBottomChild = childrenHoldingBottomChild.indexOf(bottomChild) + 1;

            if (indexOfNextToBottomChild < childrenHoldingBottomChild.size()) {
                final AbstractHtml nextToBottomChild = childrenHoldingBottomChild.get(indexOfNextToBottomChild);
                current = nextToBottomChild;
            } else {

                {
                    if (bottomChild.parent.parent == null) {

                        final byte[] closingTagBytes = bottomChild.parent.closingTag.getBytes(charset);
                        os.write(closingTagBytes);
                        if (flushOnWrite) {
                            os.flush();
                        }

                        totalWritten[0] += closingTagBytes.length;
                        break;
                    }
                }

                final List<AbstractHtml> childrenHoldingParent = List.copyOf(bottomChild.parent.parent.children);

                final int indexOfNextToBottomParent = childrenHoldingParent.indexOf(bottomChild.parent) + 1;

                if (indexOfNextToBottomParent < childrenHoldingParent.size()) {

                    final byte[] closingTagBytes = bottomChild.parent.closingTag.getBytes(charset);
                    os.write(closingTagBytes);
                    if (flushOnWrite) {
                        os.flush();
                    }

                    totalWritten[0] += closingTagBytes.length;

                    if (topBase.equals(bottomChild.parent)) {
                        break;
                    }

                    final AbstractHtml nextToParent = childrenHoldingParent.get(indexOfNextToBottomParent);
                    current = nextToParent;
                } else {
                    current = writeClosingTagUptoRootReturnFirstMiddleChild(totalWritten, charset, os, topBase,
                            bottomChild, flushOnWrite);
                }

            }

        }

    }

    private static AbstractHtml appendOpeningTagAndReturnBottomTag(final StringBuilder builder, final AbstractHtml base,
            final boolean rebuild) {

        AbstractHtml bottomChild = base;

        // passed 2 instead of 1 because the load factor is 0.75f
        Set<AbstractHtml> current = new HashSet<>(2);
        current.add(base);

        while (current != null) {

            final Set<AbstractHtml> children = current;
            current = null;
            final Iterator<AbstractHtml> iterator = children.iterator();
            // only first child is required here
            if (iterator.hasNext()) {
                final AbstractHtml child = iterator.next();
                child.setRebuild(rebuild);
                builder.append(child.getOpeningTag());
                bottomChild = child;

                final Set<AbstractHtml> subChildren = child.children;
                if (subChildren != null && !subChildren.isEmpty()) {
                    current = subChildren;
                }

            }
        }

        return bottomChild;
    }

    private static AbstractHtml writeOpeningTagAndReturnBottomTag(final int[] totalWritten, final Charset charset,
            final OutputStream os, final AbstractHtml base, final boolean rebuild) throws IOException {

        AbstractHtml bottomChild = base;

        // passed 2 instead of 1 because the load factor is 0.75f
        Set<AbstractHtml> current = new HashSet<>(2);
        current.add(base);

        while (current != null) {

            final Set<AbstractHtml> children = current;
            current = null;

            final Iterator<AbstractHtml> iterator = children.iterator();
            // only first child is required here
            if (iterator.hasNext()) {
                final AbstractHtml child = iterator.next();

                child.setRebuild(rebuild);

                final byte[] openingTagBytes = child.getOpeningTag().getBytes(charset);
                os.write(openingTagBytes);
                totalWritten[0] += openingTagBytes.length;

                bottomChild = child;

                final Set<AbstractHtml> subChildren = child.children;
                if (subChildren != null && !subChildren.isEmpty()) {
                    current = subChildren;
                }
            }

        }

        return bottomChild;
    }

    private static AbstractHtml appendClosingTagUptoRootReturnFirstMiddleChild(final StringBuilder builder,
            final AbstractHtml topBase, final AbstractHtml bottomChild) {

        AbstractHtml current = bottomChild;

        while (current != null) {
            final AbstractHtml child = current;

            current = null;

            if (child.parent != null) {

                final List<AbstractHtml> childrenHoldingChild = List.copyOf(child.parent.children);
                final int nextIndexOfChild = childrenHoldingChild.indexOf(child) + 1;
                if (nextIndexOfChild < childrenHoldingChild.size()) {
                    return childrenHoldingChild.get(nextIndexOfChild);
                } else {
                    builder.append(child.parent.closingTag);
                    if (topBase.equals(child.parent)) {
                        break;
                    }
                    current = child.parent;

                }
            }
        }
        return null;
    }

    private static AbstractHtml writeClosingTagUptoRootReturnFirstMiddleChild(final int[] totalWritten,
            final Charset charset, final OutputStream os, final AbstractHtml topBase, final AbstractHtml bottomChild,
            final boolean flushOnWrite) throws IOException {

        AbstractHtml current = bottomChild;

        while (current != null) {
            final AbstractHtml child = current;

            current = null;

            if (child.parent != null) {
                final List<AbstractHtml> childrenHoldingChild = List.copyOf(child.parent.children);

                final int nextIndexOfChild = childrenHoldingChild.indexOf(child) + 1;
                if (nextIndexOfChild < childrenHoldingChild.size()) {
                    return childrenHoldingChild.get(nextIndexOfChild);
                } else {
                    final byte[] closingTagBytes = child.parent.closingTag.getBytes(charset);
                    os.write(closingTagBytes);
                    if (flushOnWrite) {
                        os.flush();
                    }

                    totalWritten[0] += closingTagBytes.length;

                    if (topBase.equals(child.parent)) {
                        break;
                    }
                    current = child.parent;

                }
            }
        }
        return null;
    }

    /**
     * to form html string from the children
     *
     * @param children
     * @param rebuild      TODO
     * @param flushOnWrite true to flush on each write to OutputStream
     * @throws IOException
     * @author WFF
     * @since 1.0.0
     */
    private static void recurChildrenToOutputStream(final int[] totalWritten, final Charset charset,
            final OutputStream os, final Set<AbstractHtml> children, final boolean rebuild, final boolean flushOnWrite)
            throws IOException {

        if (children != null && !children.isEmpty()) {
            for (final AbstractHtml child : children) {
                child.setRebuild(rebuild);

                byte[] openingTagBytes = child.getOpeningTag().getBytes(charset);
                os.write(openingTagBytes);
                if (flushOnWrite) {
                    os.flush();
                }
                totalWritten[0] += openingTagBytes.length;
                // explicitly dereferenced right after use
                // because it's a recursive method.
                openingTagBytes = null;

                // final Set<AbstractHtml> childrenOfChildren = child.children;
                // declaring a separate local variable childrenOfChildren will
                // consume stack space so directly passed it as argument
                recurChildrenToOutputStream(totalWritten, charset, os, child.children, rebuild, flushOnWrite);

                byte[] closingTagBytes = child.closingTag.getBytes(charset);
                os.write(closingTagBytes);
                if (flushOnWrite) {
                    os.flush();
                }
                totalWritten[0] += closingTagBytes.length;
                // explicitly dereferenced right after use
                // because it's a recursive method.
                closingTagBytes = null;
            }
        }
    }

    // for future development

    /**
     * to form html string from the children
     *
     * @param children
     * @param rebuild  TODO
     * @param charset  TODO
     * @throws IOException
     * @author WFF
     * @since 2.0.0
     */
    private void recurChildrenToWffBinaryMessageOutputStream(final Set<AbstractHtml> children, final boolean rebuild,
            final Charset charset) throws IOException {
        if (children != null && !children.isEmpty()) {
            for (final AbstractHtml child : children) {
                child.setRebuild(rebuild);
                // wffBinaryMessageOutputStreamer

                // outputStream.write(child.getOpeningTag().getBytes(charset));

                NameValue nameValue = new NameValue();

                final int tagNameIndex = TagRegistry.getTagNames().indexOf(child.tagName);

                // if the tag index is -1 i.e. it's not indexed then the tag
                // name prepended with 0 value byte should be set.
                // If the first byte == 0 and length is greater than 1 then it's
                // a tag name, if the first byte is greater than 0 then it is
                // index bytes

                byte[] closingTagNameConvertedBytes = null;
                if (tagNameIndex == -1) {

                    final byte[] tagNameBytes = child.tagName.getBytes(charset);

                    byte[] nameBytes = new byte[tagNameBytes.length + 1];

                    nameBytes[0] = 0;

                    System.arraycopy(tagNameBytes, 0, nameBytes, 1, tagNameBytes.length);
                    nameValue.setName(nameBytes);
                    closingTagNameConvertedBytes = nameBytes;
                    // explicitly dereferenced right after use
                    // because it's a recursive method.
                    nameBytes = null;
                } else {

                    // final byte[] indexBytes = WffBinaryMessageUtil
                    // .getOptimizedBytesFromInt(tagNameIndex);
                    // directly passed it as argument to
                    // avoid consuming stack space
                    nameValue.setName(WffBinaryMessageUtil.getOptimizedBytesFromInt(tagNameIndex));
                    closingTagNameConvertedBytes = WffBinaryMessageUtil.getOptimizedBytesFromInt((tagNameIndex * (-1)));
                }

                nameValue.setValues(child.getAttributeHtmlBytesCompressedByIndex(rebuild, charset));

                wffBinaryMessageOutputStreamer.write(nameValue);
                // explicitly dereferenced right after use
                // because it's a recursive method.
                nameValue = null;

                // final Set<AbstractHtml> childrenOfChildren = child.children;
                // declaring a separate local variable childrenOfChildren will
                // consume stack space so directly passed it as argument
                recurChildrenToWffBinaryMessageOutputStream(child.children, rebuild, charset);

                NameValue closingTagNameValue = new NameValue();
                closingTagNameValue.setName(closingTagNameConvertedBytes);
                closingTagNameValue.setValues(new byte[0][0]);
                wffBinaryMessageOutputStreamer.write(closingTagNameValue);

                // explicitly dereferenced right after use
                // because it's a recursive method.
                closingTagNameValue = null;
                closingTagNameConvertedBytes = null;

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
        final Lock lock = lockAndGetWriteLock();
        try {
            return toHtmlStringLockless();
        } finally {
            lock.unlock();
        }
    }

    /**
     * NB: use write lock instead of read lock when using this method
     *
     * @return the html string
     */
    private String toHtmlStringLockless() {
        final String printStructure = getPrintStructure(getSharedObject().isChildModified());

        if (parent == null) {
            sharedObject.setChildModified(false, ACCESS_OBJECT);
        }

        return printStructure;
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

    // TODO for future implementation

    /**
     * @param os the object of {@code OutputStream} to write to.
     * @throws IOException
     * @deprecated this method is for future implementation so it should not be
     *             consumed
     */
    @Deprecated
    void toOutputStream(final boolean asWffBinaryMessage, final OutputStream os) throws IOException {

        final Lock lock = lockAndGetWriteLock();
        try {

            if (asWffBinaryMessage) {
                try {
                    wffBinaryMessageOutputStreamer = new WffBinaryMessageOutputStreamer(os);
                    writePrintStructureToWffBinaryMessageOutputStream(true);
                } finally {
                    wffBinaryMessageOutputStreamer = null;
                }
            } else {
                toOutputStream(os);
            }
        } finally {
            lock.unlock();
        }

    }

    /**
     * @param os the object of {@code OutputStream} to write to.
     * @return the total number of bytes written
     * @throws IOException
     */
    public int toOutputStream(final OutputStream os) throws IOException {
        return writePrintStructureToOutputStream(CommonConstants.DEFAULT_CHARSET, os, true);
    }

    /**
     * @param os      the object of {@code OutputStream} to write to.
     * @param charset the charset
     * @return
     * @throws IOException
     */
    public int toOutputStream(final OutputStream os, final Charset charset) throws IOException {
        return writePrintStructureToOutputStream(charset, os, true);
    }

    /**
     * @param os           the object of {@code OutputStream} to write to.
     * @param charset      the charset
     * @param flushOnWrite true to flush on each write to OutputStream
     * @return
     * @throws IOException
     * @since 3.0.2
     */
    public int toOutputStream(final OutputStream os, final Charset charset, final boolean flushOnWrite)
            throws IOException {
        return writePrintStructureToOutputStream(os, true, charset, flushOnWrite);
    }

    /**
     * @param os      the object of {@code OutputStream} to write to.
     * @param charset the charset
     * @return the total number of bytes written
     * @throws IOException
     */
    public int toOutputStream(final OutputStream os, final String charset) throws IOException {

        if (charset != null) {
            return writePrintStructureToOutputStream(Charset.forName(charset), os, true);
        }
        return writePrintStructureToOutputStream(CommonConstants.DEFAULT_CHARSET, os, true);
    }

    /**
     * @param os      the object of {@code OutputStream} to write to.
     * @param rebuild true to rebuild &amp; false to write previously built bytes.
     * @return the total number of bytes written
     * @throws IOException
     */
    public int toOutputStream(final OutputStream os, final boolean rebuild) throws IOException {
        return writePrintStructureToOutputStream(CommonConstants.DEFAULT_CHARSET, os, rebuild);
    }

    /**
     * @param os           the object of {@code OutputStream} to write to.
     * @param rebuild      true to rebuild &amp; false to write previously built
     *                     bytes.
     * @param flushOnWrite true to flush on each write to OutputStream
     * @return the total number of bytes written
     * @throws IOException
     * @since 3.0.2
     */
    public int toOutputStream(final OutputStream os, final boolean rebuild, final boolean flushOnWrite)
            throws IOException {
        return writePrintStructureToOutputStream(os, rebuild, CommonConstants.DEFAULT_CHARSET, flushOnWrite);
    }

    /**
     * @param os      the object of {@code OutputStream} to write to.
     * @param rebuild true to rebuild &amp; false to write previously built bytes.
     * @param charset the charset
     * @return the total number of bytes written
     * @throws IOException
     */
    public int toOutputStream(final OutputStream os, final boolean rebuild, final Charset charset) throws IOException {
        if (charset == null) {
            return writePrintStructureToOutputStream(CommonConstants.DEFAULT_CHARSET, os, rebuild);
        }
        return writePrintStructureToOutputStream(charset, os, rebuild);
    }

    /**
     * @param os           the object of {@code OutputStream} to write to.
     * @param rebuild      true to rebuild &amp; false to write previously built
     *                     bytes.
     * @param charset      the charset
     * @param flushOnWrite true to flush on each write to OutputStream
     * @return the total number of bytes written
     * @throws IOException
     * @since 3.0.2
     */
    public int toOutputStream(final OutputStream os, final boolean rebuild, final Charset charset,
            final boolean flushOnWrite) throws IOException {
        if (charset == null) {
            return writePrintStructureToOutputStream(os, rebuild, CommonConstants.DEFAULT_CHARSET, flushOnWrite);
        }
        return writePrintStructureToOutputStream(os, rebuild, charset, flushOnWrite);
    }

    /**
     * @param os      the object of {@code OutputStream} to write to.
     * @param rebuild true to rebuild &amp; false to write previously built bytes.
     * @param charset the charset
     * @return the total number of bytes written
     * @throws IOException
     */
    public int toOutputStream(final OutputStream os, final boolean rebuild, final String charset) throws IOException {

        if (charset == null) {
            return writePrintStructureToOutputStream(CommonConstants.DEFAULT_CHARSET, os, rebuild);
        }
        return writePrintStructureToOutputStream(Charset.forName(charset), os, rebuild);
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
     * @return the tagName set by {@code AbstractHtml5#setTagName(String)} method.
     * @author WFF
     * @since 1.0.0
     */
    public String getTagName() {
        return tagName;
    }

    public byte[][] getAttributeHtmlBytesCompressedByIndex(final boolean rebuild, final Charset charset)
            throws IOException {
        return AttributeUtil.getAttributeHtmlBytesCompressedByIndex(rebuild, charset, attributes);
    }

    /**
     * @param rebuild TODO
     * @author WFF
     * @since 1.0.0
     */
    private void buildOpeningTag(final boolean rebuild) {

        // final String attributeHtmlString = AttributeUtil
        // .getAttributeHtmlString(rebuild, charset, attributes);

        if (htmlStartSB.length() > 0) {
            htmlStartSB.delete(0, htmlStartSB.length());
        }

        if (tagName != null) {
            // previously attributeHtmlString was used in append method
            // as argument.
            htmlStartSB.append('<').append(tagName).append(AttributeUtil.getAttributeHtmlString(rebuild, attributes));

            switch (tagType) {
            case OPENING_CLOSING -> htmlStartSB.append('>');
            case SELF_CLOSING -> htmlStartSB.append(new char[] { '/', '>' });
            default -> htmlStartSB.append('>');// here it will be tagType == TagType.NON_CLOSING as there are three
                                               // types in TagType class
            }

            htmlStartSB.trimToSize();
            openingTag = htmlStartSB.toString();
        } else {
            htmlStartSB.trimToSize();
            openingTag = "";
        }
    }

    /**
     * @return closing tag
     * @author WFF
     * @since 1.0.0
     */
    private String buildClosingTag() {
        final StringBuilder htmlEndSB = new StringBuilder(tagName == null ? 16 : tagName.length() + 3);
        if (tagName != null) {
            htmlEndSB.append(new char[] { '<', '/' }).append(tagName).append('>');
        } else {
            if (htmlStartSB != null) {
                htmlEndSB.append(getHtmlMiddleSB());
            }
        }
        return htmlEndSB.toString();
    }

    /**
     * NB: it will not always return the same object as could be modified by the
     * framework at any time.
     *
     * @return the sharedObject
     * @author WFF
     * @since 1.0.0
     */
    @Override
    public final AbstractHtml5SharedObject getSharedObject() {
        return sharedObject;
    }

    /**
     * Note: this method is only for internal use.
     *
     * @param accessObject the access object.
     * @return the sharedObject.
     * @since 12.0.1
     */
    public final AbstractHtml5SharedObject getSharedObject(
            @SuppressWarnings("exports") final SecurityObject accessObject) {
        // NB: this is the alternative lockless method for getSharedObject method so do
        // not use lock in this method.
        if (!IndexedClassType.ABSTRACT_ATTRIBUTE.equals(accessObject.forClassType())) {
            throw new WffSecurityException("Not allowed to consume this method. This method is for internal use.");
        }
        return sharedObject;
    }

    /**
     * only for internal purpose
     *
     * @return the sharedObject
     *
     * @since 3.0.15
     */
    final AbstractHtml5SharedObject getSharedObjectLockless() {
        return sharedObject;
    }

    /**
     * Note: Only for internal use
     *
     * @return the htmlMiddleSB
     * @author WFF
     * @since 1.0.0
     *
     */
    protected StringBuilder getHtmlMiddleSB() {
        if (htmlMiddleSB == null) {
            commonLock().lock();
            try {
                if (htmlMiddleSB == null) {
                    htmlMiddleSB = new StringBuilder();
                }
            } finally {
                commonLock().unlock();
            }
        }
        return htmlMiddleSB;
    }

    /**
     * @return the string value of htmlMiddleSB or empty
     * @since 12.0.0
     */
    protected String getHtmlMiddleString() {
        final StringBuilder htmlMiddleSB = this.htmlMiddleSB;
        if (htmlMiddleSB != null) {
            final Lock lock = lockAndGetReadLock();
            try {
                return htmlMiddleSB.toString();
            } finally {
                lock.unlock();
            }
        }
        return "";
    }

    /**
     * @param child the string to be removed from htmlMiddleSB
     * @since 12.0.0
     */
    protected void removeFromHtmlMiddleSB(final String child) {
        final StringBuilder htmlMiddleSB = this.htmlMiddleSB;
        if (htmlMiddleSB != null) {
            final Lock lock = lockAndGetWriteLock();
            try {
                final String sb = htmlMiddleSB.toString();
                final String replaced = StringUtil.replace(sb, child, "");
                final int lastIndex = htmlMiddleSB.length() - 1;
                htmlMiddleSB.delete(0, lastIndex);
                htmlMiddleSB.append(replaced);
            } finally {
                lock.unlock();
            }
        }
    }

    /**
     * @return the htmlStartSBAsFirst
     * @author WFF
     * @since 1.0.0
     */
    public boolean isHtmlStartSBAsFirst() {
        return htmlStartSBAsFirst;
    }

    protected AbstractHtml deepClone(final AbstractHtml objectToBeClonned) throws CloneNotSupportedException {
        return CloneUtil.deepClone(objectToBeClonned);
    }

    /**
     * invokes just before {@code getPrintStructure(final boolean} method and only
     * if the getPrintStructure(final boolean} rebuilds the structure.
     *
     * @author WFF
     * @since 1.0.0
     */
    protected void beforePrintStructure() {
        // NOP override and use
    }

    /**
     * invokes just before
     * {@code writePrintStructureToOutputStream(final OutputStream} method.
     *
     * @author WFF
     * @since 1.0.0
     */
    protected void beforeWritePrintStructureToOutputStream() {
        // NOP override and use
    }

    /**
     * invokes just before
     * {@code writePrintStructureToWffBinaryMessageOutputStream(final OutputStream}
     * method.
     *
     * @author WFF
     * @since 2.0.0
     */
    protected void beforeWritePrintStructureToWffBinaryMessageOutputStream() {
        // NOP override and use
    }

    /**
     * Creates and returns a deeply cloned copy of this object. The precise meaning
     * of "copy" may depend on the class of the object. The general intent is that,
     * for any object {@code x}, the expression: <blockquote>
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
     * </blockquote> will be {@code true}, but these are not absolute requirements.
     * While it is typically the case that: <blockquote>
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
     * By convention, the object returned by this method should be independent of
     * this object (which is being cloned). To achieve this independence, it may be
     * necessary to modify one or more fields of the object returned by
     * {@code super.clone} before returning it. Typically, this means copying any
     * mutable objects that comprise the internal "deep structure" of the object
     * being cloned and replacing the references to these objects with references to
     * the copies. If a class contains only primitive fields or references to
     * immutable objects, then it is usually the case that no fields in the object
     * returned by {@code super.clone} need to be modified.
     * <p>
     * The method {@code clone} for class {@code AbstractHtml} performs a specific
     * cloning operation. First, if the class of this object does not implement the
     * interfaces {@code Cloneable} and {@code Serializable}, then a
     * {@code CloneNotSupportedException} is thrown. Note that all arrays are
     * considered to implement the interface {@code Cloneable} and that the return
     * type of the {@code clone} method of an array type {@code T[]} is {@code T[]}
     * where T is any reference or primitive type. Otherwise, this method creates a
     * new instance of the class of this object and initializes all its fields with
     * exactly the contents of the corresponding fields of this object, as if by
     * assignment; the contents of the fields are not themselves cloned. Thus, this
     * method performs a "shallow copy" of this object, not a "deep copy" operation.
     *
     * @return a deep clone of this instance.
     * @throws CloneNotSupportedException if the object's class does not support the
     *                                    {@code Cloneable} and {@code Serializable}
     *                                    interfaces. Subclasses that override the
     *                                    {@code clone} method can also throw this
     *                                    exception to indicate that an instance
     *                                    cannot be cloned.
     * @author WFF
     * @see java.lang.Cloneable
     * @see java.io.Serializable
     */
    @Override
    public AbstractHtml clone() throws CloneNotSupportedException {
        return deepClone(this);
    }

    /**
     * Creates and returns a deeply cloned copy of this object. The precise meaning
     * of "copy" may depend on the class of the object. The general intent is that,
     * for any object {@code x}, the expression: <blockquote>
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
     * </blockquote> will be {@code true}, but these are not absolute requirements.
     * While it is typically the case that: <blockquote>
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
     * By convention, the object returned by this method should be independent of
     * this object (which is being cloned). To achieve this independence, it may be
     * necessary to modify one or more fields of the object returned by
     * {@code super.clone} before returning it. Typically, this means copying any
     * mutable objects that comprise the internal "deep structure" of the object
     * being cloned and replacing the references to these objects with references to
     * the copies. If a class contains only primitive fields or references to
     * immutable objects, then it is usually the case that no fields in the object
     * returned by {@code super.clone} need to be modified.
     * <p>
     * The method {@code clone} for class {@code AbstractHtml} performs a specific
     * cloning operation. First, if the class of this object does not implement the
     * interfaces {@code Cloneable} and {@code Serializable}, then a
     * {@code CloneNotSupportedException} is thrown. Note that all arrays are
     * considered to implement the interface {@code Cloneable} and that the return
     * type of the {@code clone} method of an array type {@code T[]} is {@code T[]}
     * where T is any reference or primitive type. Otherwise, this method creates a
     * new instance of the class of this object and initializes all its fields with
     * exactly the contents of the corresponding fields of this object, as if by
     * assignment; the contents of the fields are not themselves cloned. Thus, this
     * method performs a "shallow copy" of this object, not a "deep copy" operation.
     *
     * @param excludeAttributes pass the attributes names which need to be excluded
     *                          from all tags including their child tags.
     * @return a deep clone of this instance without the given attributes.
     * @throws CloneNotSupportedException if the object's class does not support the
     *                                    {@code Cloneable} and {@code Serializable}
     *                                    interfaces. Subclasses that override the
     *                                    {@code clone} method can also throw this
     *                                    exception to indicate that an instance
     *                                    cannot be cloned.
     * @author WFF
     * @see java.lang.Cloneable
     * @see java.io.Serializable
     * @since 2.0.0
     */
    public AbstractHtml clone(final String... excludeAttributes) throws CloneNotSupportedException {

        final Lock lock = lockAndGetReadLock();
        try {

            final AbstractHtml clonedObject = deepClone(this);

            final Deque<Set<AbstractHtml>> childrenStack = new ArrayDeque<>();
            final Set<AbstractHtml> initialSet = Set.of(clonedObject);
            childrenStack.push(initialSet);

            Set<AbstractHtml> children;
            while ((children = childrenStack.poll()) != null) {

                for (final AbstractHtml child : children) {
                    child.removeAttributes(excludeAttributes);

                    final Set<AbstractHtml> subChildren = child.children;
                    if (subChildren != null && !subChildren.isEmpty()) {
                        childrenStack.push(subChildren);
                    }
                }
            }

            return clonedObject;

        } finally {
            lock.unlock();
        }
    }

//    /**
//     * Just kept for future reference
//     *
//     * @param removedAbstractHtmls
//     * @return the locks after locking
//     */
//    private List<Lock> initNewSharedObjectInAllNestedTagsAndSetSuperParentNullOld(
//            final AbstractHtml[] removedAbstractHtmls) {
//        // TODO remove this unused method later
//        final List<Lock> locks = new ArrayList<>(removedAbstractHtmls.length);
//        for (final AbstractHtml abstractHtml : removedAbstractHtmls) {
//            final Lock lock = initNewSharedObjectInAllNestedTagsAndSetSuperParentNull(abstractHtml, true);
//            locks.add(lock);
//        }
//        return locks;
//    }

    /**
     * @param abstractHtmls the removed tags from the current object
     * @return locks after locking
     * @since 12.0.1 its old implementation is in
     *        initNewSharedObjectInAllNestedTagsAndSetSuperParentNullOld.
     */
    private List<Lock> initNewSharedObjectInAllNestedTagsAndSetSuperParentNull(final AbstractHtml[] abstractHtmls) {
        if (abstractHtmls == null || abstractHtmls.length == 0) {
            return List.of();
        }
        final List<TagContractRecord> tagContractRecords = new ArrayList<>(abstractHtmls.length);
        for (final AbstractHtml abstractHtml : abstractHtmls) {
            tagContractRecords.add(new TagContractRecord(abstractHtml, new AbstractHtml5SharedObject(abstractHtml)));
        }
        tagContractRecords.sort(Comparator.comparing(TagContractRecord::objectId));
        final List<Lock> locks = new ArrayList<>(abstractHtmls.length);
        for (final TagContractRecord tagContractRecord : tagContractRecords) {
            final WriteLock writeLock = tagContractRecord.sharedObject.getLock(ACCESS_OBJECT).writeLock();
            writeLock.lock();
            locks.add(writeLock);
            initNewSharedObjectInAllNestedTagsAndSetSuperParentNull(tagContractRecord.tag,
                    tagContractRecord.sharedObject);
        }
        Collections.reverse(locks);
        return locks;
    }

//    /**
//     * @param removedAbstractHtmls
//     * @return the locks after locking
//     */
//    private void initNewSharedObjectInAllNestedTagsAndSetSuperParentNullLockless(
//            final AbstractHtml[] removedAbstractHtmls) {
//        // TODO remove this unused method later
//        for (final AbstractHtml abstractHtml : removedAbstractHtmls) {
//            initNewSharedObjectInAllNestedTagsAndSetSuperParentNull(abstractHtml, false);
//        }
//    }

    /**
     * @param abstractHtml
     * @param lockSO       true to lock newly created sharedObject
     * @return the lock after locking
     */
    private Lock initNewSharedObjectInAllNestedTagsAndSetSuperParentNull(final AbstractHtml abstractHtml,
            final boolean lockSO) {

        final AbstractHtml5SharedObject newSharedObject = new AbstractHtml5SharedObject(abstractHtml);
        final WriteLock lock;
        if (lockSO) {
            lock = newSharedObject.getLock(ACCESS_OBJECT).writeLock();
            lock.lock();
        } else {
            lock = null;
        }

        initNewSharedObjectInAllNestedTagsAndSetSuperParentNull(abstractHtml, newSharedObject);

        return lock;
    }

    private void initNewSharedObjectInAllNestedTagsAndSetSuperParentNull(final AbstractHtml abstractHtml,
            final AbstractHtml5SharedObject newSharedObject) {

        if (TagUtil.isTagless(abstractHtml) && abstractHtml instanceof NoTag) {
            abstractHtml.sharedTagContent = null;
            abstractHtml.sharedTagContentSubscribed = null;
            abstractHtml.cachedStcFormatter = null;
        }

        // NB: the following code is never expected to make an exception otherwise on
        // exception the lock must be unlocked.

        abstractHtml.sharedObject = newSharedObject;

        if (abstractHtml.parent != null) {
            abstractHtml.parent = null;
            abstractHtml.parentNullifiedOnce = true;
        }

        final Map<String, AbstractHtml> tagByWffId = sharedObject.getTagByWffId(ACCESS_OBJECT);

        final Deque<Set<AbstractHtml>> removedTagsStack = new ArrayDeque<>();
        final Set<AbstractHtml> initialSet = Set.of(abstractHtml);
        removedTagsStack.push(initialSet);

        Set<AbstractHtml> stackChildren;
        while ((stackChildren = removedTagsStack.poll()) != null) {

            for (final AbstractHtml stackChild : stackChildren) {

                final DataWffId dataWffId = stackChild.dataWffId;
                if (dataWffId != null) {
                    tagByWffId.computeIfPresent(dataWffId.getValue(), (k, v) -> {
                        if (stackChild.equals(v)) {
                            return null;
                        }
                        return v;
                    });
                }

                stackChild.sharedObject = newSharedObject;
                stackChild.hierarchicalLoopId = null;

                if (stackChild.uriChangeContents != null && !stackChild.sharedObject.isWhenURIUsed()) {
                    stackChild.sharedObject.whenURIUsed(ACCESS_OBJECT);
                }

                final Set<AbstractHtml> subChildren = stackChild.children;

                if (subChildren != null && !subChildren.isEmpty()) {
                    removedTagsStack.push(subChildren);
                }
            }

        }
    }

    /**
     * @return the Wff Binary Message bytes of this tag. It uses default charset for
     *         encoding values.
     * @version 1.1
     * @author WFF
     * @since 2.0.0 initial implementation
     * @since 3.0.2 improved to handle NoTag with contentTypeHtml true
     */
    public byte[] toWffBMBytes() {
        return toWffBMBytes(CommonConstants.DEFAULT_CHARSET);
    }

    /**
     * @param charset Eg: UTF-8
     * @return the Wff Binary Message bytes of this tag
     * @throws InvalidTagException
     * @version 1.1
     * @author WFF
     * @since 2.0.0 initial implementation
     * @since 3.0.2 improved to handle NoTag with contentTypeHtml true
     */
    public byte[] toWffBMBytes(final String charset) {
        return toWffBMBytes(Charset.forName(charset));
    }

    /**
     * @param charset
     * @return the Wff Binary Message bytes of this tag
     * @throws InvalidTagException
     * @author WFF
     * @version 1.1
     * @since 3.0.1 initial implementation
     * @since 3.0.2 improved to handle NoTag with contentTypeHtml true
     */
    public byte[] toWffBMBytes(final Charset charset) {
        return toWffBMBytes(charset, null);
    }

    /**
     * Only for internal use.
     *
     * @param charset
     * @param accessObject
     * @return the Wff Binary Message bytes of this tag
     * @throws InvalidTagException
     * @author WFF
     * @version 1.1
     * @since 3.0.1 initial implementation
     * @since 3.0.2 improved to handle NoTag with contentTypeHtml true
     * @since 3.0.15 accessObject added
     */
    public final byte[] toWffBMBytes(final Charset charset,
            @SuppressWarnings("exports") final SecurityObject accessObject) {

        final byte[] encodedBytesForAtChar = "@".getBytes(charset);
        final byte[] encodedByesForHashChar = "#".getBytes(charset);

        final Lock lock;

        if (accessObject != null) {
            lock = null;
            if (!IndexedClassType.BROWSER_PAGE.equals(accessObject.forClassType())) {
                throw new WffSecurityException("Not allowed to consume this method. This method is for internal use.");
            }

        } else {
            // writeLock is better as we are writing to wffSlotIndex
            lock = sharedObject.getLock(ACCESS_OBJECT).writeLock();
            lock.lock();
        }

        try {

            final Deque<NameValue> nameValues = new ArrayDeque<>();

            // ArrayDeque give better performance than Stack, LinkedList
            final Deque<Set<AbstractHtml>> childrenStack = new ArrayDeque<>();
            final Set<AbstractHtml> initialSet = Set.of(this);
            childrenStack.push(initialSet);

            Set<AbstractHtml> children;
            while ((children = childrenStack.poll()) != null) {

                for (final AbstractHtml tag : children) {

                    final String nodeName = tag.tagName;

                    if (nodeName != null && !nodeName.isEmpty()) {

                        final NameValue nameValue = new NameValue();

                        final byte[] nodeNameBytes = nodeName.getBytes(charset);
                        final byte[][] wffAttributeBytes = AttributeUtil.getWffAttributeBytes(charset, tag.attributes);

                        final int parentWffSlotIndex = tag.parent == null ? -1 : tag.parent.wffSlotIndex;
                        nameValue.setName(WffBinaryMessageUtil.getBytesFromInt(parentWffSlotIndex));

                        final byte[][] values = new byte[wffAttributeBytes.length + 1][0];

                        values[0] = nodeNameBytes;

                        System.arraycopy(wffAttributeBytes, 0, values, 1, wffAttributeBytes.length);

                        nameValue.setValues(values);
                        tag.wffSlotIndex = nameValues.size();
                        nameValues.add(nameValue);

                    } else if (!tag.getClosingTag().isEmpty()) {

                        final int parentWffSlotIndex = tag.parent == null ? -1 : tag.parent.wffSlotIndex;

                        final NameValue nameValue = new NameValue();

                        // # short for #text
                        // @ short for html content
                        final byte[] nodeNameBytes = tag.noTagContentTypeHtml ? encodedBytesForAtChar
                                : encodedByesForHashChar;

                        nameValue.setName(WffBinaryMessageUtil.getBytesFromInt(parentWffSlotIndex));

                        final byte[][] values = new byte[2][0];

                        values[0] = nodeNameBytes;
                        values[1] = tag.getClosingTag().getBytes(charset);

                        nameValue.setValues(values);

                        tag.wffSlotIndex = nameValues.size();
                        nameValues.add(nameValue);
                    }

                    final Set<AbstractHtml> subChildren = tag.children;

                    if (subChildren != null && !subChildren.isEmpty()) {
                        childrenStack.push(subChildren);
                    }

                }

            }

            final NameValue nameValue = nameValues.getFirst();
            nameValue.setName(new byte[0]);

            return WffBinaryMessageUtil.VERSION_1.getWffBinaryMessageBytes(nameValues);
        } catch (final NoSuchElementException e) {
            throw new InvalidTagException(
                    "Not possible to build wff bm bytes on this tag.\nDon't use an empty new NoTag(null, \"\") or new Blank(null, \"\")",
                    e);
        } catch (final Exception e) {
            throw new WffRuntimeException(e.getMessage(), e);
        } finally {
            if (lock != null) {
                lock.unlock();
            }
        }
    }

    /**
     * Note: different wffweb lib version will generate different bytes so use the
     * same wffweb lib version for parsing.
     *
     * @param charset
     * @return the Wff Binary Message bytes of this tag containing indexed tag name
     *         and attribute name
     * @throws InvalidTagException
     * @author WFF
     * @version algorithm version 1.0
     * @since 3.0.3
     */
    public byte[] toCompressedWffBMBytes(final Charset charset) {

        final byte[] encodedBytesForAtChar = "@".getBytes(charset);
        final byte[] encodedBytesForHashChar = "#".getBytes(charset);

        final Lock lock = lockAndGetReadLock();
        try {

            final Deque<NameValue> nameValues = new ArrayDeque<>();

            // ArrayDeque give better performance than Stack, LinkedList
            final Deque<Set<AbstractHtml>> childrenStack = new ArrayDeque<>();
            final Set<AbstractHtml> initialSet = Set.of(this);
            childrenStack.push(initialSet);

            Set<AbstractHtml> children;
            while ((children = childrenStack.poll()) != null) {

                for (final AbstractHtml tag : children) {

                    final String nodeName = tag.tagName;

                    final AbstractHtml parentLocal = tag.parent;

                    if (nodeName != null && !nodeName.isEmpty()) {

                        final NameValue nameValue = new NameValue();

                        final byte[] nodeNameBytes;

                        // just be initialized as local
                        final byte[] tagNameIndexBytes = tag.tagNameIndexBytes;

                        if (tagNameIndexBytes == null) {
                            final byte[] rowNodeNameBytes = nodeName.getBytes(charset);
                            nodeNameBytes = new byte[rowNodeNameBytes.length + 1];
                            // if zero there is no optimized int bytes for index
                            // because there is no tagNameIndex. second byte
                            // onwards the bytes of tag name
                            nodeNameBytes[0] = 0;
                            System.arraycopy(rowNodeNameBytes, 0, nodeNameBytes, 1, rowNodeNameBytes.length);

                            // logging is not required here
                            // it is not an unusual case
                            // if (LOGGER.isLoggable(Level.WARNING)) {
                            // LOGGER.warning(nodeName
                            // + " is not indexed, please register it with
                            // TagRegistry");
                            // }

                        } else {

                            nodeNameBytes = new byte[tagNameIndexBytes.length + 1];
                            nodeNameBytes[0] = (byte) tagNameIndexBytes.length;
                            System.arraycopy(tagNameIndexBytes, 0, nodeNameBytes, 1, tagNameIndexBytes.length);
                        }

                        final byte[][] wffAttributeBytes = AttributeUtil.getAttributeHtmlBytesCompressedByIndex(false,
                                charset, tag.attributes);

                        final int parentWffSlotIndex = parentLocal == null ? -1 : parentLocal.wffSlotIndex;
                        nameValue.setName(WffBinaryMessageUtil.getBytesFromInt(parentWffSlotIndex));

                        final byte[][] values = new byte[wffAttributeBytes.length + 1][0];

                        values[0] = nodeNameBytes;

                        System.arraycopy(wffAttributeBytes, 0, values, 1, wffAttributeBytes.length);

                        nameValue.setValues(values);
                        tag.wffSlotIndex = nameValues.size();
                        nameValues.add(nameValue);

                    } else {

                        // tag.tagName == null means it is no tag
                        // !tag.getClosingTag().isEmpty() SharedTagContet is
                        // injecting NoTag with empty content so
                        // !tag.getClosingTag().isEmpty() is not valid here

                        final int parentWffSlotIndex = parentLocal == null ? -1 : parentLocal.wffSlotIndex;

                        final NameValue nameValue = new NameValue();

                        // # short for #text
                        // @ short for html content
                        final byte[] nodeNameBytes = tag.noTagContentTypeHtml ? encodedBytesForAtChar
                                : encodedBytesForHashChar;

                        nameValue.setName(WffBinaryMessageUtil.getBytesFromInt(parentWffSlotIndex));

                        final byte[][] values = new byte[2][0];

                        values[0] = nodeNameBytes;
                        values[1] = tag.getClosingTag().getBytes(charset);

                        nameValue.setValues(values);

                        tag.wffSlotIndex = nameValues.size();
                        nameValues.add(nameValue);
                    }

                    final Set<AbstractHtml> subChildren = tag.children;

                    if (subChildren != null && !subChildren.isEmpty()) {
                        childrenStack.push(subChildren);
                    }

                }

            }

            final NameValue nameValue = nameValues.getFirst();
            nameValue.setName(new byte[0]);

            return WffBinaryMessageUtil.VERSION_1.getWffBinaryMessageBytes(nameValues);
        } catch (final NoSuchElementException e) {
            throw new InvalidTagException(
                    "Not possible to build wff bm bytes on this tag.\nDon't use an empty new NoTag(null, \"\") or new Blank(null, \"\")",
                    e);
        } catch (final Exception e) {
            throw new WffRuntimeException(e.getMessage(), e);
        } finally {
            lock.unlock();
        }
    }

    /**
     * It uses algorithm version 2. Note: different wffweb lib version will generate
     * different bytes so use the same wffweb lib version for parsing.
     *
     * @param charset
     * @return the Wff Binary Message bytes of this tag containing indexed tag name
     *         and attribute name
     * @throws InvalidTagException
     * @author WFF
     * @since 3.0.6
     */
    public byte[] toCompressedWffBMBytesV2(final Charset charset) {
        return toCompressedWffBMBytesV2(charset, null);
    }

    /**
     * It uses algorithm version 3.
     *
     * To create Tag object from the returned bytes follow the example:
     *
     * <pre><code>
     * Div div = new Div(null, new Id("someId"), new Value("1401"));
     * AbstractHtml parsedTag = TagCompressedWffBMBytesParser.VERSION_3.parse(div.toCompressedWffBMBytesV3(StandardCharsets.UTF_8), true, StandardCharsets.UTF_8);
     * </code></pre> Note: different wffweb lib version will generate different
     * bytes so use the same wffweb lib version for parsing.
     *
     * @param charset
     * @return the Wff Binary Message bytes of this tag containing indexed tag name
     *         and attribute name
     * @throws InvalidTagException
     * @author WFF
     * @since 12.0.3
     */
    public byte[] toCompressedWffBMBytesV3(final Charset charset) {
        return toCompressedWffBMBytesV3(charset, null);
    }

    /**
     * Only for internal purpose. It uses algorithm version 2. Note: different
     * wffweb lib version will generate different bytes so use the same wffweb lib
     * version for parsing.
     *
     * @param charset
     * @param accessObject
     * @return the Wff Binary Message bytes of this tag containing indexed tag name
     *         and attribute name
     * @throws InvalidTagException
     * @author WFF
     * @since 3.0.6
     * @since 3.0.15 accessObject added
     */
    public final byte[] toCompressedWffBMBytesV2(final Charset charset,
            @SuppressWarnings("exports") final SecurityObject accessObject) {

        final Lock lock;

        if (accessObject != null) {
            lock = null;
            if (!IndexedClassType.BROWSER_PAGE.equals(accessObject.forClassType())) {
                throw new WffSecurityException("Not allowed to consume this method. This method is for internal use.");
            }
        } else {
            // writeLock is better as we are writing to wffSlotIndex
            lock = sharedObject.getLock(ACCESS_OBJECT).writeLock();
            lock.lock();
        }

        try {

            final Deque<NameValue> nameValues = new ArrayDeque<>();

            // ArrayDeque give better performance than Stack, LinkedList
            final Deque<Set<AbstractHtml>> childrenStack = new ArrayDeque<>();
            final Set<AbstractHtml> initialSet = Set.of(this);
            childrenStack.push(initialSet);

            Set<AbstractHtml> children;
            while ((children = childrenStack.poll()) != null) {

                for (final AbstractHtml tag : children) {

                    final String nodeName = tag.tagName;

                    final AbstractHtml parentLocal = tag.parent;

                    if (nodeName != null && !nodeName.isEmpty()) {

                        final NameValue nameValue = new NameValue();

                        final byte[] nodeNameBytes;

                        // just be initialized as local
                        final byte[] tagNameIndexBytes = tag.tagNameIndexBytes;

                        if (tagNameIndexBytes == null) {
                            final byte[] rowNodeNameBytes = nodeName.getBytes(charset);
                            nodeNameBytes = new byte[rowNodeNameBytes.length + 1];
                            // if zero there is no optimized int bytes for index
                            // because there is no tagNameIndex. second byte
                            // onwards the bytes of tag name
                            nodeNameBytes[0] = 0;
                            System.arraycopy(rowNodeNameBytes, 0, nodeNameBytes, 1, rowNodeNameBytes.length);

                            // logging is not required here
                            // it is not an unusual case
                            // if (LOGGER.isLoggable(Level.WARNING)) {
                            // LOGGER.warning(nodeName
                            // + " is not indexed, please register it with
                            // TagRegistry");
                            // }

                        } else {

                            if (tagNameIndexBytes.length == 1) {
                                nodeNameBytes = tagNameIndexBytes;
                            } else {
                                nodeNameBytes = new byte[tagNameIndexBytes.length + 1];
                                nodeNameBytes[0] = (byte) tagNameIndexBytes.length;
                                System.arraycopy(tagNameIndexBytes, 0, nodeNameBytes, 1, tagNameIndexBytes.length);
                            }

                        }

                        final byte[][] wffAttributeBytes = AttributeUtil.getAttributeHtmlBytesCompressedByIndex(false,
                                charset, tag.attributes);

                        final int parentWffSlotIndex = parentLocal == null ? -1 : parentLocal.wffSlotIndex;
                        nameValue.setName(WffBinaryMessageUtil.getBytesFromInt(parentWffSlotIndex));

                        final byte[][] values = new byte[wffAttributeBytes.length + 1][0];

                        values[0] = nodeNameBytes;

                        System.arraycopy(wffAttributeBytes, 0, values, 1, wffAttributeBytes.length);

                        nameValue.setValues(values);
                        tag.wffSlotIndex = nameValues.size();
                        nameValues.add(nameValue);

                    } else {

                        // tag.tagName == null means it is no tag
                        // !tag.getClosingTag().isEmpty() SharedTagContet is
                        // injecting NoTag with empty content so
                        // !tag.getClosingTag().isEmpty() is not valid here

                        final int parentWffSlotIndex = parentLocal == null ? -1 : parentLocal.wffSlotIndex;

                        final NameValue nameValue = new NameValue();

                        // # short for #text
                        // @ short for html content
                        // final byte[] nodeNameBytes = tag.noTagContentTypeHtml
                        // ? encodedBytesForAtChar
                        // : encodedByesForHashChar;

                        final byte[] nodeNameBytes = tag.noTagContentTypeHtml ? INDEXED_AT_CHAR_BYTES
                                : INDEXED_HASH_CHAR_BYTES;

                        nameValue.setName(WffBinaryMessageUtil.getBytesFromInt(parentWffSlotIndex));

                        final byte[][] values = new byte[2][0];

                        values[0] = nodeNameBytes;
                        values[1] = tag.getClosingTag().getBytes(charset);

                        nameValue.setValues(values);

                        tag.wffSlotIndex = nameValues.size();
                        nameValues.add(nameValue);
                    }

                    final Set<AbstractHtml> subChildren = tag.children;

                    if (subChildren != null && !subChildren.isEmpty()) {
                        childrenStack.push(subChildren);
                    }

                }

            }

            final NameValue nameValue = nameValues.getFirst();
            nameValue.setName(new byte[0]);

            return WffBinaryMessageUtil.VERSION_1.getWffBinaryMessageBytes(nameValues);
        } catch (final NoSuchElementException e) {
            throw new InvalidTagException(
                    "Not possible to build wff bm bytes on this tag.\nDon't use an empty new NoTag(null, \"\") or new Blank(null, \"\")",
                    e);
        } catch (final Exception e) {
            throw new WffRuntimeException(e.getMessage(), e);
        } finally {
            if (lock != null) {
                lock.unlock();
            }

        }
    }

    /**
     * Only for internal purpose. It uses algorithm version 3. Note: different
     * wffweb lib version will generate different bytes so use the same wffweb lib
     * version for parsing.
     *
     * @param charset
     * @param accessObject
     * @return the Wff Binary Message bytes of this tag containing indexed tag name
     *         and attribute name
     * @throws InvalidTagException
     * @author WFF
     * @since 12.0.3
     */
    public final byte[] toCompressedWffBMBytesV3(final Charset charset,
            @SuppressWarnings("exports") final SecurityObject accessObject) {

        final Lock lock;

        if (accessObject != null) {
            lock = null;
            if (!IndexedClassType.BROWSER_PAGE.equals(accessObject.forClassType())) {
                throw new WffSecurityException("Not allowed to consume this method. This method is for internal use.");
            }
        } else {
            // writeLock is better as we are writing to wffSlotIndex
            lock = sharedObject.getLock(ACCESS_OBJECT).writeLock();
            lock.lock();
        }

        try {

            final Deque<NameValue> nameValues = new ArrayDeque<>();

            // ArrayDeque give better performance than Stack, LinkedList
            final Deque<Set<AbstractHtml>> childrenStack = new ArrayDeque<>();
            final Set<AbstractHtml> initialSet = Set.of(this);
            childrenStack.push(initialSet);

            Set<AbstractHtml> children;
            while ((children = childrenStack.poll()) != null) {

                for (final AbstractHtml tag : children) {

                    final String nodeName = tag.tagName;

                    final AbstractHtml parentLocal = tag.parent;

                    if (nodeName != null && !nodeName.isEmpty()) {

                        final NameValue nameValue = new NameValue();

                        final byte[] nodeNameBytes;

                        // just be initialized as local
                        final byte[] tagNameIndexBytes = tag.tagNameIndexBytes;

                        if (tagNameIndexBytes == null) {
                            final byte[] rowNodeNameBytes = nodeName.getBytes(charset);
                            nodeNameBytes = new byte[rowNodeNameBytes.length + 1];
                            // if zero there is no optimized int bytes for index
                            // because there is no tagNameIndex. second byte
                            // onwards the bytes of tag name
                            nodeNameBytes[0] = 0;
                            System.arraycopy(rowNodeNameBytes, 0, nodeNameBytes, 1, rowNodeNameBytes.length);

                            // logging is not required here
                            // it is not an unusual case
                            // if (LOGGER.isLoggable(Level.WARNING)) {
                            // LOGGER.warning(nodeName
                            // + " is not indexed, please register it with
                            // TagRegistry");
                            // }

                        } else {

                            if (tagNameIndexBytes.length == 1) {
                                nodeNameBytes = tagNameIndexBytes;
                            } else {
                                nodeNameBytes = new byte[tagNameIndexBytes.length + 1];
                                nodeNameBytes[0] = (byte) tagNameIndexBytes.length;
                                System.arraycopy(tagNameIndexBytes, 0, nodeNameBytes, 1, tagNameIndexBytes.length);
                            }

                        }

                        final byte[][] wffAttributeBytes = AttributeUtil.getAttributeHtmlBytesCompressedByIndexV2(false,
                                charset, tag.attributes);

                        final int parentWffSlotIndex = parentLocal == null ? -1 : parentLocal.wffSlotIndex;
                        nameValue.setName(WffBinaryMessageUtil.getOptimizedBytesFromInt(parentWffSlotIndex));

                        final byte[][] values = new byte[wffAttributeBytes.length + 1][0];

                        values[0] = nodeNameBytes;

                        System.arraycopy(wffAttributeBytes, 0, values, 1, wffAttributeBytes.length);

                        nameValue.setValues(values);
                        tag.wffSlotIndex = nameValues.size();
                        nameValues.add(nameValue);

                    } else {

                        // tag.tagName == null means it is no tag
                        // !tag.getClosingTag().isEmpty() SharedTagContet is
                        // injecting NoTag with empty content so
                        // !tag.getClosingTag().isEmpty() is not valid here

                        final int parentWffSlotIndex = parentLocal == null ? -1 : parentLocal.wffSlotIndex;

                        final NameValue nameValue = new NameValue();

                        // # short for #text
                        // @ short for html content
                        // final byte[] nodeNameBytes = tag.noTagContentTypeHtml
                        // ? encodedBytesForAtChar
                        // : encodedByesForHashChar;
                        final String tagContent = tag.getClosingTag();
                        final boolean strictInt = NumberUtil.isStrictInt(tagContent);
                        final byte[] nodeNameBytes = strictInt
                                ? tag.noTagContentTypeHtml ? INDEXED_DOLLAR_CHAR_BYTES : INDEXED_PERCENT_CHAR_BYTES
                                : tag.noTagContentTypeHtml ? INDEXED_AT_CHAR_BYTES : INDEXED_HASH_CHAR_BYTES;

                        nameValue.setName(WffBinaryMessageUtil.getOptimizedBytesFromInt(parentWffSlotIndex));

                        final byte[][] values = new byte[2][0];

                        values[0] = nodeNameBytes;

                        values[1] = strictInt
                                ? WffBinaryMessageUtil.getOptimizedBytesFromInt(Integer.parseInt(tagContent))
                                : tagContent.getBytes(charset);

                        nameValue.setValues(values);

                        tag.wffSlotIndex = nameValues.size();
                        nameValues.add(nameValue);
                    }

                    final Set<AbstractHtml> subChildren = tag.children;

                    if (subChildren != null && !subChildren.isEmpty()) {
                        childrenStack.push(subChildren);
                    }

                }

            }

            final NameValue nameValue = nameValues.getFirst();
            nameValue.setName(new byte[0]);

            return WffBinaryMessageUtil.VERSION_1.getWffBinaryMessageBytes(nameValues);
        } catch (final NoSuchElementException e) {
            throw new InvalidTagException(
                    "Not possible to build wff bm bytes on this tag.\nDon't use an empty new NoTag(null, \"\") or new Blank(null, \"\")",
                    e);
        } catch (final Exception e) {
            throw new WffRuntimeException(e.getMessage(), e);
        } finally {
            if (lock != null) {
                lock.unlock();
            }

        }
    }

    /**
     * @param bmBytes Wff Binary Message bytes of tag i.e. returned by *
     *                {@link AbstractHtml#toWffBMBytes(String)}
     * @return the AbstractHtml instance from the given Wff BM bytes. It uses system
     *         default charset.
     * @author WFF
     * @since 2.0.0 initial implementation
     * @since 3.0.2 improved to handle NoTag with contentTypeHtml true
     */
    public static AbstractHtml getTagFromWffBMBytes(final byte[] bmBytes) {
        return getTagFromWffBMBytes(bmBytes, CommonConstants.DEFAULT_CHARSET);
    }

    /**
     * @param bmBytes Wff Binary Message bytes of tag i.e. returned by
     *                {@link AbstractHtml#toWffBMBytes(String)}
     * @param charset the charset used to generate bm bytes in
     *                {@link AbstractHtml#toWffBMBytes(String)}
     * @return the AbstractHtml instance from the given Wff BM bytes
     * @author WFF
     * @since 2.0.0 initial implementation
     * @since 3.0.2 improved to handle NoTag with contentTypeHtml true
     */
    public static AbstractHtml getTagFromWffBMBytes(final byte[] bmBytes, final String charset) {
        return getTagFromWffBMBytes(bmBytes, Charset.forName(charset));
    }

    /**
     * @param bmBytes Wff Binary Message bytes of tag i.e. returned by
     *                {@link AbstractHtml#toWffBMBytes(Charset)}
     * @param charset the charset used to generate bm bytes in
     *                {@link AbstractHtml#toWffBMBytes(Charset)}
     * @return the AbstractHtml instance from the given Wff BM bytes
     * @author WFF
     * @since 3.0.1 initial implementation
     * @since 3.0.2 improved to handle NoTag with contentTypeHtml true
     */
    public static AbstractHtml getTagFromWffBMBytes(final byte[] bmBytes, final Charset charset) {
        // Note: it cannot parse bytes from toCompressedWffBMBytes... methods.

        final List<NameValue> nameValuesAsList = WffBinaryMessageUtil.VERSION_1.parse(bmBytes);

        final NameValue[] nameValues = nameValuesAsList.toArray(new NameValue[nameValuesAsList.size()]);

        final NameValue superParentNameValue = nameValues[0];
        final byte[][] superParentValues = superParentNameValue.getValues();

        final AbstractHtml[] allTags = new AbstractHtml[nameValues.length];

        AbstractHtml parent = null;

        // # short for #text
        // @ short for html content
        boolean noTagContentTypeHtml = superParentValues[0][0] == '@';
        if (superParentValues[0][0] == '#' || noTagContentTypeHtml) {
            parent = new NoTag(null, new String(superParentValues[1], charset), noTagContentTypeHtml);
        } else {
            final String tagName = new String(superParentValues[0], charset);

            final AbstractAttribute[] attributes = new AbstractAttribute[superParentValues.length - 1];

            for (int i = 1; i < superParentValues.length; i++) {
                final String attrNameValue = new String(superParentValues[i], charset);
                final int indexOfEqualChar = attrNameValue.indexOf('=');
                final String attrName;
                final String attrValue;

                if (indexOfEqualChar == -1) {
                    attrName = attrNameValue;
                    attrValue = null;
                } else {
                    attrName = attrNameValue.substring(0, indexOfEqualChar);
                    attrValue = attrNameValue.substring(indexOfEqualChar + 1, attrNameValue.length());
                }
                // CustomAttribute should be replaced with relevant class
                // later
                attributes[i - 1] = new CustomAttribute(attrName, attrValue);
            }
            // CustomTag should be replaced with relevant class later
            parent = new CustomTag(tagName, null, attributes);
        }
        allTags[0] = parent;

        for (int i = 1; i < nameValues.length; i++) {

            final NameValue nameValue = nameValues[i];
            final int indexOfParent = WffBinaryMessageUtil.getIntFromOptimizedBytes(nameValue.getName());

            final byte[][] values = nameValue.getValues();
            // # short for #text
            // @ short for html content
            noTagContentTypeHtml = values[0][0] == '@';
            AbstractHtml child;
            if (values[0][0] == '#' || noTagContentTypeHtml) {
                child = new NoTag(allTags[indexOfParent], new String(values[1], charset), noTagContentTypeHtml);
            } else {
                final String tagName = new String(values[0], charset);

                final AbstractAttribute[] attributes = new AbstractAttribute[values.length - 1];

                for (int j = 1; j < values.length; j++) {
                    final String attrNameValue = new String(values[j], charset);
                    final int indexOfEqualChar = attrNameValue.indexOf('=');

                    final String attrName;
                    final String attrValue;

                    if (indexOfEqualChar == -1) {
                        attrName = attrNameValue;
                        attrValue = null;
                    } else {
                        attrName = attrNameValue.substring(0, indexOfEqualChar);
                        attrValue = attrNameValue.substring(indexOfEqualChar + 1, attrNameValue.length());
                    }

                    // CustomAttribute should be replaced with relevant
                    // class later
                    attributes[j - 1] = new CustomAttribute(attrName, attrValue);
                }
                // CustomTag should be replaced with relevant class later
                child = new CustomTag(tagName, allTags[indexOfParent], attributes);
            }
            allTags[i] = child;
        }

        return parent;
    }

    /**
     * @param bmBytes Wff Binary Message bytes of tag i.e. returned by
     *                {@link AbstractHtml#toWffBMBytes(Charset)}
     * @param charset the charset used to generate bm bytes in
     *                {@link AbstractHtml#toWffBMBytes(Charset)}
     * @return the AbstractHtml instance from the given Wff BM bytes
     * @author WFF
     * @since 3.0.2 Also includes the improvement to handle NoTag with
     *        contentTypeHtml true
     */
    public static AbstractHtml getExactTagFromWffBMBytes(final byte[] bmBytes, final Charset charset) {
        // Note: it cannot parse bytes from toCompressedWffBMBytes... methods.

        final List<NameValue> nameValuesAsList = WffBinaryMessageUtil.VERSION_1.parse(bmBytes);

        final NameValue[] nameValues = nameValuesAsList.toArray(new NameValue[nameValuesAsList.size()]);

        final NameValue superParentNameValue = nameValues[0];
        final byte[][] superParentValues = superParentNameValue.getValues();

        final AbstractHtml[] allTags = new AbstractHtml[nameValues.length];

        AbstractHtml parent = null;

        // # short for #text
        // @ short for html content
        boolean noTagContentTypeHtml = superParentValues[0][0] == '@';
        if (superParentValues[0][0] == '#' || noTagContentTypeHtml) {
            parent = new NoTag(null, new String(superParentValues[1], charset), noTagContentTypeHtml);
        } else {
            final String tagName = new String(superParentValues[0], charset);

            final AbstractAttribute[] attributes = new AbstractAttribute[superParentValues.length - 1];

            for (int i = 1; i < superParentValues.length; i++) {
                final String attrNameValue = new String(superParentValues[i], charset);
                final int indexOfEqualChar = attrNameValue.indexOf('=');

                final String attrName;
                final String attrValue;

                if (indexOfEqualChar == -1) {
                    attrName = attrNameValue;
                    attrValue = null;
                } else {
                    attrName = attrNameValue.substring(0, indexOfEqualChar);
                    attrValue = attrNameValue.substring(indexOfEqualChar + 1, attrNameValue.length());
                }

                final AbstractAttribute newAttributeInstance = AttributeRegistry
                        .getNewAttributeInstanceOrNullIfFailed(attrName, attrValue);

                if (newAttributeInstance != null) {
                    attributes[i - 1] = newAttributeInstance;
                } else {
                    attributes[i - 1] = new CustomAttribute(attrName, attrValue);
                }
            }

            final AbstractHtml newTagInstance = TagRegistry.getNewTagInstanceOrNullIfFailed(tagName, null, attributes);

            if (newTagInstance != null) {
                parent = newTagInstance;
            } else {
                parent = new CustomTag(tagName, null, attributes);
            }

        }
        allTags[0] = parent;

        for (int i = 1; i < nameValues.length; i++) {

            final NameValue nameValue = nameValues[i];
            final int indexOfParent = WffBinaryMessageUtil.getIntFromOptimizedBytes(nameValue.getName());

            final byte[][] values = nameValue.getValues();
            // # short for #text
            // @ short for html content
            noTagContentTypeHtml = values[0][0] == '@';
            AbstractHtml child;
            if (values[0][0] == '#' || noTagContentTypeHtml) {
                child = new NoTag(allTags[indexOfParent], new String(values[1], charset), noTagContentTypeHtml);
            } else {
                final String tagName = new String(values[0], charset);

                final AbstractAttribute[] attributes = new AbstractAttribute[values.length - 1];

                for (int j = 1; j < values.length; j++) {
                    final String attrNameValue = new String(values[j], charset);
                    final int indexOfEqualChar = attrNameValue.indexOf('=');

                    final String attrName;
                    final String attrValue;

                    if (indexOfEqualChar == -1) {
                        attrName = attrNameValue;
                        attrValue = null;
                    } else {
                        attrName = attrNameValue.substring(0, indexOfEqualChar);
                        attrValue = attrNameValue.substring(indexOfEqualChar + 1, attrNameValue.length());
                    }

                    final AbstractAttribute newAttributeInstance = AttributeRegistry
                            .getNewAttributeInstanceOrNullIfFailed(attrName, attrValue);

                    if (newAttributeInstance != null) {
                        attributes[j - 1] = newAttributeInstance;
                    } else {
                        attributes[j - 1] = new CustomAttribute(attrName, attrValue);
                    }

                }

                final AbstractHtml newTagInstance = TagRegistry.getNewTagInstanceOrNullIfFailed(tagName,
                        allTags[indexOfParent], attributes);

                if (newTagInstance != null) {
                    child = newTagInstance;
                } else {
                    child = new CustomTag(tagName, allTags[indexOfParent], attributes);
                }

            }
            allTags[i] = child;
        }

        return parent;
    }

    private void initDataWffId(final AbstractHtml5SharedObject sharedObject) {
        if (dataWffId == null) {
            commonLock().lock();
            try {
                if (dataWffId == null) {
                    final DataWffId newDataWffId = sharedObject.getNewDataWffId(ACCESS_OBJECT);
                    addAttributesLockless(false, newDataWffId);
                    dataWffId = newDataWffId;
                }
            } finally {
                commonLock().unlock();
            }
        } else {
            throw new WffRuntimeException("dataWffId already exists");
        }
    }

    /**
     * @return the dataWffId
     */
    public DataWffId getDataWffId() {
        return dataWffId;
    }

    /**
     * adds data-wff-id for the tag if doesn't already exist. NB:- this method is
     * ony for internal use.
     *
     * @param dataWffId the dataWffId to set
     */
    public void setDataWffId(final DataWffId dataWffId) {
        if (this.dataWffId == null) {
            commonLock().lock();
            try {
                if (this.dataWffId == null) {

                    final Lock lock = lockAndGetWriteLock();
                    try {
                        addAttributesLockless(false, dataWffId);
                        this.dataWffId = dataWffId;
                    } finally {
                        lock.unlock();
                    }

                }
            } finally {
                commonLock().unlock();
            }
        } else {
            throw new WffRuntimeException("dataWffId already exists");
        }
    }

    /**
     * Inserts the given tags before this tag. There must be a parent for this
     * method tag otherwise throws NoParentException.
     *
     * @param abstractHtmls to insert before this tag
     * @return true if inserted otherwise false.
     * @throws NoParentException if this tag has no parent
     * @author WFF
     * @since 2.1.1
     *
     */
    public boolean insertBefore(final AbstractHtml... abstractHtmls) throws NoParentException {
        return insertBefore(false, abstractHtmls);
    }

    /**
     * Inserts the given tags before this tag only if this tag has parent.
     *
     * @param abstractHtmls to insert before this tag
     * @return true if inserted otherwise false.
     * @author WFF
     * @since 3.0.15
     */
    public boolean insertBeforeIfPossible(final AbstractHtml... abstractHtmls) {
        return insertBefore(true, abstractHtmls);
    }

    /**
     * Inserts the given tags before this tag. There must be a parent for this
     * method tag.
     *
     * @param skipException skips NoParentException
     * @param abstractHtmls to insert before this tag
     * @return true if inserted otherwise false.
     * @author WFF
     * @since 3.0.15
     */
    private boolean insertBefore(final boolean skipException, final AbstractHtml... abstractHtmls) {

        AbstractHtml parent = this.parent;
        if (parent == null) {
            if (skipException) {
                return false;
            }
            throw new NoParentException("There must be a parent for this tag.");
        }

        // inserted, listener invoked
        boolean[] results = { false, false };

        final List<Lock> locks = lockAndGetWriteLocks(abstractHtmls);

        // sharedObject should be after locking
        final AbstractHtml5SharedObject sharedObject = this.sharedObject;

        final Deque<AbstractHtml> parentGainedListenerTags;
        final Deque<AbstractHtml> parentLostListenerTags;

//        final Lock lock = sharedObject.getLock(ACCESS_OBJECT).writeLock();
//        lock.lock();

        try {
            validateChildren(abstractHtmls);
            parent = this.parent;
            parentGainedListenerTags = buildParentGainedListenerTags(parent, abstractHtmls);
            parentLostListenerTags = buildParentLostListenerTags(parent, abstractHtmls);

            for (final AbstractHtml each : children) {
                each.applyURIChange(sharedObject);
            }

            if (parent == null) {
                if (skipException) {
                    return false;
                }
                throw new NoParentException("There must be a parent for this tag.");
            }

            final AbstractHtml[] removedParentChildren = parent.children
                    .toArray(new AbstractHtml[parent.children.size()]);

            results = insertBefore(removedParentChildren, abstractHtmls);

        } finally {
//            lock.unlock();
            for (final Lock lck : locks) {
                lck.unlock();
            }
        }

        if (results[1]) {
            final PushQueue pushQueue = sharedObject.getPushQueue(ACCESS_OBJECT);
            if (pushQueue != null) {
                pushQueue.push();
            }
        }

        pollAndInvokeParentLostListeners(parentLostListenerTags);
        // Note: GainedListener should be invoked after ParentLostListener
        pollAndInvokeParentGainedListeners(parentGainedListenerTags);

        return results[0];
    }

    /**
     * Replaces this tag with the given tags. There must be a parent for this method
     * tag otherwise throws NoParentException. Obviously, this tag will be removed
     * from its parent if this method is called.
     *
     * @param tags tags for the replacement of this tag
     * @return true if replaced otherwise false.
     * @throws NoParentException if this tag has no parent
     * @since 3.0.7
     */
    public boolean replaceWith(final AbstractHtml... tags) throws NoParentException {
        return replaceWith(false, tags);
    }

    /**
     * Replaces this tag with the given tags only if it has a parent. Obviously,
     * this tag will be removed from its parent if this method is called.
     *
     * @param tags tags for the replacement of this tag
     * @return true if replaced otherwise false.
     * @since 3.0.15
     */
    public boolean replaceWithIfPossible(final AbstractHtml... tags) {
        return replaceWith(true, tags);
    }

    /**
     * Replaces this tag with the given tags. There must be a parent for this method
     * tag. Obviously, this tag will be removed from its parent if this method is
     * called.
     *
     * @param skipException skips NoParentException
     * @param tags          tags for the replacement of this tag
     * @return true if replaced otherwise false.
     * @since 3.0.15
     */
    private boolean replaceWith(final boolean skipException, final AbstractHtml... tags) {

        AbstractHtml parent = this.parent;
        if (parent == null) {
            if (skipException) {
                return false;
            }
            throw new NoParentException("There must be a parent for this tag.");
        }

        // inserted, listener invoked
        boolean[] results = { false, false };

//        final Lock lock = thisSharedObject.getLock(ACCESS_OBJECT).writeLock();
//        lock.lock();

        // NB: this tag itself is getting replaced after replaceWith so
        // this.sharedObject will be different object after that and calling
        // this.sharedObject.getPushQueue is invalid. So keeping the right
        // object in thisSharedObject.

        final List<Lock> locks = lockAndGetWriteLocks(tags);

        // sharedObject should be after locking
        final AbstractHtml5SharedObject thisSharedObject = sharedObject;

        final Deque<AbstractHtml> parentGainedListenerTags;
        final Deque<AbstractHtml> parentLostListenerTags;

        try {
            validateChildren(tags);
            parent = this.parent;
            parentGainedListenerTags = buildParentGainedListenerTags(parent, tags);
            parentLostListenerTags = buildParentLostListenerTags(parent, tags, this);
            for (final AbstractHtml each : tags) {
                each.applyURIChange(thisSharedObject);
            }

            if (parent == null) {
                if (skipException) {
                    return false;
                }
                throw new NoParentException("There must be a parent for this tag.");
            }

            final AbstractHtml[] removedParentChildren = parent.children
                    .toArray(new AbstractHtml[parent.children.size()]);

            results = replaceWith(removedParentChildren, tags);
        } finally {
//            lock.unlock();
            for (final Lock lck : locks) {
                lck.unlock();
            }
        }

        if (results[1]) {
            final PushQueue pushQueue = thisSharedObject.getPushQueue(ACCESS_OBJECT);
            if (pushQueue != null) {
                pushQueue.push();
            }
        }

        pollAndInvokeParentLostListeners(parentLostListenerTags);
        // Note: GainedListener should be invoked after ParentLostListener
        pollAndInvokeParentGainedListeners(parentGainedListenerTags);

        return results[0];
    }

    /**
     * should be used inside a synchronized block. NB:- It's removing
     * removedParentChildren by parent.children.clear(); in this method.
     *
     * @param removedParentChildren just pass the parent children, no need to remove
     *                              it from parent. It's removing by
     *                              parent.children.clear();
     * @param abstractHtmls
     * @return in zeroth index: true if inserted otherwise false. in first index:
     *         true if listener invoked otherwise false.
     * @author WFF
     * @since 3.0.7
     */
    private boolean[] insertBefore(final AbstractHtml[] removedParentChildren, final AbstractHtml[] abstractHtmls) {

        // inserted, listener invoked
        final boolean[] results = { false, false };

        final int parentChildrenSize = parent.children.size();
        if (parentChildrenSize > 0) {

            final InsertTagsBeforeListener insertBeforeListener = sharedObject
                    .getInsertTagsBeforeListener(ACCESS_OBJECT);

            // this.parent will be nullified in
            // initNewSharedObjectInAllNestedTagsAndSetSuperParentNull so kept a
            // local copy
            final AbstractHtml thisParent = parent;
            final AbstractHtml5SharedObject thisSharedObject = sharedObject;

            thisParent.children.clear();

            final InsertTagsBeforeListener.Event[] events = new InsertTagsBeforeListener.Event[abstractHtmls.length];

            int count = 0;

            for (final AbstractHtml parentChild : removedParentChildren) {

                if (equals(parentChild)) {

                    for (final AbstractHtml tagToInsert : abstractHtmls) {

                        final boolean alreadyHasParent = tagToInsert.parent != null;

                        if (insertBeforeListener != null) {
                            AbstractHtml previousParent = null;

                            if (alreadyHasParent) {
                                if (tagToInsert.parent.sharedObject == thisSharedObject) {
                                    previousParent = tagToInsert.parent;
                                } else {

                                    if (tagToInsert.parent.sharedObject
                                            .getInsertTagsBeforeListener(ACCESS_OBJECT) == null) {
                                        removeFromTagByWffIdMap(tagToInsert,
                                                tagToInsert.parent.sharedObject.getTagByWffId(ACCESS_OBJECT));
                                    } // else {TODO also write the code to push
                                      // changes to the other BrowserPage}

                                }

                            }

                            // if parentChild == tagToInsert then
                            // tagToInsert.parent i.e. previousParent
                            // will be null (because we are calling
                            // initNewSharedObjectInAllNestedTagsAndSetSuperParentNull(parentChild))
                            // that is useful for not removing it
                            // from the browser UI.
                            final InsertTagsBeforeListener.Event event = new InsertTagsBeforeListener.Event(tagToInsert,
                                    previousParent);
                            events[count] = event;
                            count++;
                        } else if (alreadyHasParent) {
                            if (tagToInsert.parent.sharedObject.getInsertTagsBeforeListener(ACCESS_OBJECT) == null) {
                                removeFromTagByWffIdMap(tagToInsert,
                                        tagToInsert.parent.sharedObject.getTagByWffId(ACCESS_OBJECT));
                            } // else {TODO also write the code to push
                              // changes to the other BrowserPage}
                        }

                        // if alreadyHasParent = true then it means the
                        // child is
                        // moving from one tag to another.

                        if (alreadyHasParent) {
                            Lock foreignLock = null;
                            AbstractHtml5SharedObject foreignSO = tagToInsert.sharedObject;
                            if (foreignSO != null && !thisSharedObject.equals(foreignSO)) {
                                foreignLock = foreignSO.getLock(ACCESS_OBJECT).writeLock();
                                foreignSO = null;
                                foreignLock.lock();
                            }
                            try {
                                tagToInsert.parent.children.remove(tagToInsert);
                                initSharedObject(tagToInsert);
                                tagToInsert.parent = thisParent;
                            } finally {
                                if (foreignLock != null) {
                                    foreignLock.unlock();
                                }
                            }
                        } else {
                            Lock foreignLock = null;
                            AbstractHtml5SharedObject foreignSO = tagToInsert.sharedObject;
                            if (foreignSO != null && !thisSharedObject.equals(foreignSO)) {
                                foreignLock = foreignSO.getLock(ACCESS_OBJECT).writeLock();
                                foreignSO = null;
                                foreignLock.lock();
                            }
                            try {
                                removeDataWffIdFromHierarchyLockless(tagToInsert);
                                initSharedObject(tagToInsert);
                                tagToInsert.parent = thisParent;
                            } finally {
                                if (foreignLock != null) {
                                    foreignLock.unlock();
                                }
                            }

                        }

                        thisParent.children.add(tagToInsert);
                    }

                }

                thisParent.children.add(parentChild);
            }

            if (insertBeforeListener != null) {
                insertBeforeListener.insertedBefore(thisParent, this, events);
                results[1] = true;
            }

            results[0] = true;

        }
        return results;
    }

    /**
     * should be used inside a synchronized block. NB:- It's removing
     * removedParentChildren by parent.children.clear(); in this method.
     *
     * @param removedParentChildren just pass the parent children, no need to remove
     *                              it from parent. It's removing by
     *                              parent.children.clear();
     * @param abstractHtmls
     * @return in zeroth index: true if inserted otherwise false. in first index:
     *         true if listener invoked otherwise false.
     * @author WFF
     * @since 3.0.7
     */
    private boolean[] insertAfter(final AbstractHtml[] removedParentChildren, final AbstractHtml[] abstractHtmls) {

        // inserted, listener invoked
        final boolean[] results = { false, false };

        final int parentChildrenSize = parent.children.size();
        if (parentChildrenSize > 0) {

            final InsertAfterListener insertAfterListener = sharedObject.getInsertAfterListener(ACCESS_OBJECT);

            // this.parent will be nullified in
            // initNewSharedObjectInAllNestedTagsAndSetSuperParentNull so kept a
            // local copy
            final AbstractHtml thisParent = parent;
            final AbstractHtml5SharedObject thisSharedObject = sharedObject;

            thisParent.children.clear();

            final InsertAfterListener.Event[] events = new InsertAfterListener.Event[abstractHtmls.length];

            int count = 0;

            for (final AbstractHtml parentChild : removedParentChildren) {

                thisParent.children.add(parentChild);

                if (equals(parentChild)) {

                    for (final AbstractHtml tagToInsert : abstractHtmls) {

                        final boolean alreadyHasParent = tagToInsert.parent != null;

                        if (insertAfterListener != null) {
                            AbstractHtml previousParent = null;

                            if (alreadyHasParent) {
                                if (tagToInsert.parent.sharedObject == thisSharedObject) {
                                    previousParent = tagToInsert.parent;
                                } else {

                                    if (tagToInsert.parent.sharedObject.getInsertAfterListener(ACCESS_OBJECT) == null) {
                                        removeFromTagByWffIdMap(tagToInsert,
                                                tagToInsert.parent.sharedObject.getTagByWffId(ACCESS_OBJECT));
                                    } // else {TODO also write the code to push
                                      // changes to the other BrowserPage}

                                }

                            }

                            // if parentChild == tagToInsert then
                            // tagToInsert.parent i.e. previousParent
                            // will be null (because we are calling
                            // initNewSharedObjectInAllNestedTagsAndSetSuperParentNull(parentChild))
                            // that is useful for not removing it
                            // from the browser UI.
                            final InsertAfterListener.Event event = new InsertAfterListener.Event(tagToInsert,
                                    previousParent);
                            events[count] = event;
                            count++;
                        } else if (alreadyHasParent) {
                            if (tagToInsert.parent.sharedObject.getInsertAfterListener(ACCESS_OBJECT) == null) {
                                removeFromTagByWffIdMap(tagToInsert,
                                        tagToInsert.parent.sharedObject.getTagByWffId(ACCESS_OBJECT));
                            } // else {TODO also write the code to push
                              // changes to the other BrowserPage}
                        }

                        // if alreadyHasParent = true then it means the
                        // child is
                        // moving from one tag to another.

                        if (alreadyHasParent) {

                            Lock foreignLock = null;
                            AbstractHtml5SharedObject foreignSO = tagToInsert.sharedObject;
                            if (foreignSO != null && !thisSharedObject.equals(foreignSO)) {
                                foreignLock = foreignSO.getLock(ACCESS_OBJECT).writeLock();
                                foreignSO = null;
                                foreignLock.lock();
                            }
                            try {
                                tagToInsert.parent.children.remove(tagToInsert);
                                initSharedObject(tagToInsert);
                                tagToInsert.parent = thisParent;
                            } finally {
                                if (foreignLock != null) {
                                    foreignLock.unlock();
                                }
                            }

                        } else {

                            Lock foreignLock = null;
                            AbstractHtml5SharedObject foreignSO = tagToInsert.sharedObject;
                            if (foreignSO != null && !thisSharedObject.equals(foreignSO)) {
                                foreignLock = foreignSO.getLock(ACCESS_OBJECT).writeLock();
                                foreignSO = null;
                                foreignLock.lock();
                            }
                            try {
                                removeDataWffIdFromHierarchyLockless(tagToInsert);
                                initSharedObject(tagToInsert);
                                tagToInsert.parent = thisParent;
                            } finally {
                                if (foreignLock != null) {
                                    foreignLock.unlock();
                                }
                            }

                        }

                        thisParent.children.add(tagToInsert);
                    }

                }

            }

            if (insertAfterListener != null) {
                insertAfterListener.insertedAfter(thisParent, this, events);
                results[1] = true;
            }

            results[0] = true;

        }
        return results;
    }

    /**
     * should be used inside a synchronized block. NB:- It's removing
     * removedParentChildren by parent.children.clear(); in this method.
     *
     * @param removedParentChildren just pass the parent children, no need to remove
     *                              it from parent. It's removing by
     *                              parent.children.clear();
     * @param abstractHtmls
     * @return in zeroth index: true if inserted otherwise false. in first index:
     *         true if listener invoked otherwise false.
     * @author WFF
     * @since 3.0.7
     */
    private boolean[] replaceWith(final AbstractHtml[] removedParentChildren, final AbstractHtml[] abstractHtmls) {

        // inserted, listener invoked
        final boolean[] results = { false, false };

        final int parentChildrenSize = parent.children.size();
        if (parentChildrenSize > 0) {

            final ReplaceListener replaceListener = sharedObject.getReplaceListener(ACCESS_OBJECT);

            // this.parent will be nullified in
            // initNewSharedObjectInAllNestedTagsAndSetSuperParentNull so kept a
            // local copy
            final AbstractHtml thisParent = parent;
            final AbstractHtml5SharedObject thisSharedObject = sharedObject;

            thisParent.children.clear();

            final ReplaceListener.Event[] events = new ReplaceListener.Event[abstractHtmls.length];

            int count = 0;

            final List<Lock> newSOLocks = new ArrayList<>(1);

            try {

                for (final AbstractHtml parentChild : removedParentChildren) {

                    if (equals(parentChild)) {

                        // must be the first statement, the replacing tag and
                        // replacement tag could be same
                        final Lock newSOLock = initNewSharedObjectInAllNestedTagsAndSetSuperParentNull(parentChild,
                                true);
                        newSOLocks.add(newSOLock);

                        for (final AbstractHtml tagToInsert : abstractHtmls) {

                            final boolean alreadyHasParent = tagToInsert.parent != null;

                            if (replaceListener != null) {
                                AbstractHtml previousParent = null;

                                if (alreadyHasParent) {
                                    if (tagToInsert.parent.sharedObject == thisSharedObject) {
                                        previousParent = tagToInsert.parent;
                                    } else {

                                        if (tagToInsert.parent.sharedObject.getReplaceListener(ACCESS_OBJECT) == null) {
                                            removeFromTagByWffIdMap(tagToInsert,
                                                    tagToInsert.parent.sharedObject.getTagByWffId(ACCESS_OBJECT));
                                        } // else {TODO also write the code to push
                                          // changes to the other BrowserPage}

                                    }

                                }

                                // if parentChild == tagToInsert then
                                // tagToInsert.parent i.e. previousParent
                                // will be null (because we are calling
                                // initNewSharedObjectInAllNestedTagsAndSetSuperParentNull(parentChild))
                                // that is useful for not removing it
                                // from the browser UI.
                                final ReplaceListener.Event event = new ReplaceListener.Event(tagToInsert,
                                        previousParent);
                                events[count] = event;
                                count++;
                            } else if (alreadyHasParent) {
                                if (tagToInsert.parent.sharedObject.getReplaceListener(ACCESS_OBJECT) == null) {
                                    removeFromTagByWffIdMap(tagToInsert,
                                            tagToInsert.parent.sharedObject.getTagByWffId(ACCESS_OBJECT));
                                } // else {TODO also write the code to push
                                  // changes to the other BrowserPage}
                            }

                            // if alreadyHasParent = true then it means the
                            // child is
                            // moving from one tag to another.

                            if (alreadyHasParent) {
                                Lock foreignLock = null;
                                AbstractHtml5SharedObject foreignSO = tagToInsert.sharedObject;
                                if (foreignSO != null && !thisSharedObject.equals(foreignSO)) {
                                    foreignLock = foreignSO.getLock(ACCESS_OBJECT).writeLock();
                                    foreignSO = null;
                                    foreignLock.lock();
                                }
                                try {
                                    tagToInsert.parent.children.remove(tagToInsert);
                                    initSharedObject(tagToInsert, thisSharedObject);
                                    tagToInsert.parent = thisParent;
                                } finally {
                                    if (foreignLock != null) {
                                        foreignLock.unlock();
                                    }
                                }
                            } else {
                                Lock foreignLock = null;
                                AbstractHtml5SharedObject foreignSO = tagToInsert.sharedObject;
                                if (foreignSO != null && !thisSharedObject.equals(foreignSO)) {
                                    foreignLock = foreignSO.getLock(ACCESS_OBJECT).writeLock();
                                    foreignSO = null;
                                    foreignLock.lock();
                                }
                                try {
                                    removeDataWffIdFromHierarchyLockless(tagToInsert);
                                    initSharedObject(tagToInsert, thisSharedObject);
                                    tagToInsert.parent = thisParent;
                                } finally {
                                    if (foreignLock != null) {
                                        foreignLock.unlock();
                                    }
                                }
                            }

                            thisParent.children.add(tagToInsert);
                        }

                    } else {
                        thisParent.children.add(parentChild);
                    }

                }

                if (replaceListener != null) {
                    replaceListener.replacedWith(thisParent, this, events);
                    results[1] = true;
                }

                results[0] = true;
            } finally {
                for (final Lock newSOLock : newSOLocks) {
                    newSOLock.unlock();
                }
            }

        }
        return results;
    }

    /**
     * Inserts the given tags after this tag. There must be a parent for this method
     * tag otherwise throws NoParentException. <br>
     * Note : As promised this method is improved and works as performing and
     * reliable as insertBefore method.
     *
     * @param abstractHtmls to insert after this tag
     * @return true if inserted otherwise false.
     * @throws NoParentException if this tag has no parent
     * @author WFF
     * @since 2.1.6
     * @since 3.0.7 better implementation
     */
    public boolean insertAfter(final AbstractHtml... abstractHtmls) {
        return insertAfter(false, abstractHtmls);
    }

    /**
     * Inserts the given tags after this tag only if this tag has a parent.<br>
     *
     *
     * @param abstractHtmls to insert after this tag
     * @return true if inserted otherwise false.
     * @author WFF
     * @since 3.0.15
     */
    public boolean insertAfterIfPossible(final AbstractHtml... abstractHtmls) {
        return insertAfter(true, abstractHtmls);
    }

    /**
     * Inserts the given tags after this tag. There must be a parent for this method
     * tag. <br>
     * Note : As promised this method is improved and works as performing and
     * reliable as insertBefore method.
     *
     * @param skipException skips NoParentException
     * @param abstractHtmls to insert after this tag
     * @return true if inserted otherwise false.
     * @author WFF
     * @since 3.0.15 better implementation
     */
    private boolean insertAfter(final boolean skipException, final AbstractHtml... abstractHtmls) {

        AbstractHtml parent = this.parent;
        if (parent == null) {
            if (skipException) {
                return false;
            }
            throw new NoParentException("There must be a parent for this tag.");
        }

        // inserted, listener invoked
        boolean[] results = { false, false };

        final List<Lock> locks = lockAndGetWriteLocks(abstractHtmls);

        // sharedObject should be after locking
        final AbstractHtml5SharedObject sharedObject = this.sharedObject;

        final Deque<AbstractHtml> parentGainedListenerTags;
        final Deque<AbstractHtml> parentLostListenerTags;

//        final Lock lock = sharedObject.getLock(ACCESS_OBJECT).writeLock();
//        lock.lock();
//

        try {
            validateChildren(abstractHtmls);
            parent = this.parent;
            parentGainedListenerTags = buildParentGainedListenerTags(parent, abstractHtmls);
            parentLostListenerTags = buildParentLostListenerTags(parent, abstractHtmls);

            for (final AbstractHtml each : abstractHtmls) {
                each.applyURIChange(sharedObject);
            }

            if (parent == null) {
                if (skipException) {
                    return false;
                }
                throw new NoParentException("There must be a parent for this tag.");
            }

            final AbstractHtml[] removedParentChildren = parent.children
                    .toArray(new AbstractHtml[parent.children.size()]);

            results = insertAfter(removedParentChildren, abstractHtmls);
        } finally {
//            lock.unlock();
            for (final Lock lck : locks) {
                lck.unlock();
            }
        }

        if (results[1]) {
            final PushQueue pushQueue = sharedObject.getPushQueue(ACCESS_OBJECT);
            if (pushQueue != null) {
                pushQueue.push();
            }
        }

        pollAndInvokeParentLostListeners(parentLostListenerTags);
        // Note: GainedListener should be invoked after ParentLostListener
        pollAndInvokeParentGainedListeners(parentGainedListenerTags);

        return results[0];
    }

    /**
     * Loops through all nested children tags (excluding the given tag) of the given
     * tag. The looping is in a random order to gain maximum performance and minimum
     * memory footprint.
     *
     * @param nestedChild    the object of NestedChild from which the
     *                       eachChild(AbstractHtml) to be invoked.
     * @param includeParents true to include the given parent tags in the loop
     * @param parents        the tags from which to loop through.
     * @author WFF
     * @since 2.1.8
     */
    protected static void loopThroughAllNestedChildren(final NestedChild nestedChild, final boolean includeParents,
            final AbstractHtml... parents) {

        final Deque<Set<AbstractHtml>> childrenStack = new ArrayDeque<>();

        if (includeParents) {
            final Set<AbstractHtml> parentsSet = new HashSet<>(parents.length);
            Collections.addAll(parentsSet, parents);
            childrenStack.push(parentsSet);
        } else {
            for (final AbstractHtml parent : parents) {
                childrenStack.push(parent.children);
            }
        }

        Set<AbstractHtml> children;

        exit: while ((children = childrenStack.poll()) != null) {

            for (final AbstractHtml eachChild : children) {

                if (!nestedChild.eachChild(eachChild)) {
                    break exit;
                }

                final Set<AbstractHtml> subChildren = eachChild.children;

                if (subChildren != null && !subChildren.isEmpty()) {
                    childrenStack.push(subChildren);
                }
            }
        }
    }

    /**
     * @param key
     * @param wffBMData
     * @return
     * @author WFF
     * @since 2.1.8
     */
    protected WffBMData addWffData(final String key, final WffBMData wffBMData) {
        return AbstractJsObject.addWffData(this, key, wffBMData);
    }

    /**
     * @param key
     * @return
     * @author WFF
     * @since 2.1.8
     */
    protected WffBMData removeWffData(final String key) {
        return AbstractJsObject.removeWffData(this, key);
    }

    /**
     * @param key
     * @return
     * @author WFF
     * @since 3.0.1
     */
    protected WffBMData getWffData(final String key) {
        return AbstractJsObject.getWffData(this, key);
    }

    /**
     * Gets the unmodifiable map of wffObjects which are upserted by
     * {@link TagRepository#upsert(AbstractHtml, String, WffBMObject)} or
     * {@link TagRepository#upsert(AbstractHtml, String, WffBMArray)}. {@code null}
     * checking is required while consuming this map.
     *
     * @return the unmodifiable map of wffObjects. The value may either be an
     *         instance of {@link WffBMObject} or {@link WffBMArray}. This map may
     *         be null if there is no {@code TagRepository#upsert} operation has
     *         been done at least once in the whole life cycle. Otherwise it may
     *         also be empty.
     * @author WFF
     * @since 2.1.8
     */
    public Map<String, WffBMData> getWffObjects() {
        return Map.copyOf(wffBMDatas);
    }

    /**
     * Gets the root level tag of this tag.
     *
     * @return the root parent tag or the current tag if there is no parent for the
     *         given tag
     * @author WFF
     * @since 2.1.11
     */
    public AbstractHtml getRootTag() {
        return sharedObject.getRootTag();
    }

    /**
     * Gets the object which is accessible in all of this tag hierarchy. <br>
     * <br>
     * <br>
     * Eg:-
     *
     * <pre><code>
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
     *  Div div = TagRepository.findOneTagAssignableToTag(Div.class, html);
     *  Head head = TagRepository.findOneTagAssignableToTag(Head.class, html);
     *
     *  Object sharedData = "some object";
     *
     *  div.setSharedData(sharedData);
     *
     *  System.out.println(sharedData == head.getSharedData());
     *
     *  System.out.println(div.getSharedData() == head.getSharedData());
     *
     *  System.out.println(div.getSharedData().equals(head.getSharedData()));
     *
     *  //prints
     *
     *  true
     *  true
     *  true
     *
     * </code></pre>
     *
     * @return the sharedData object set by setSharedData method. This object is
     *         same across all of this tag hierarchy.
     * @author WFF
     * @since 2.1.11
     */
    public Object getSharedData() {
        return sharedObject.getSharedData();
    }

    /**
     * Sets the object which will be accessible by getSharedData method in all of
     * this tag hierarchy. {@code setData} sets an object for the specific tag but
     * {@code setSharedData} sets an object for all of the tag hierarchy. <br>
     * <br>
     * <br>
     * Eg:-
     *
     * <pre><code>
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
     *  Div div = TagRepository.findOneTagAssignableToTag(Div.class, html);
     *  Head head = TagRepository.findOneTagAssignableToTag(Head.class, html);
     *
     *  Object sharedData = "some object";
     *
     *  div.setSharedData(sharedData);
     *
     *  System.out.println(sharedData == head.getSharedData());
     *
     *  System.out.println(div.getSharedData() == head.getSharedData());
     *
     *  System.out.println(div.getSharedData().equals(head.getSharedData()));
     *
     *  //prints
     *
     *  true
     *  true
     *  true
     *
     * </code></pre>
     *
     * @param sharedData the object to access through all of this tag hierarchy.
     * @author WFF
     * @since 2.1.11
     */
    public void setSharedData(final Object sharedData) {
        sharedObject.setSharedData(sharedData);
    }

    /**
     * @param sharedData
     * @return true if set or false if it already had a value
     * @since 3.0.1
     */
    public boolean setSharedDataIfNull(final Object sharedData) {
        return sharedObject.setSharedDataIfNull(sharedData);
    }

    /**
     * Resets the hierarchy of this tag so that it can be used in another instance
     * of {@code BrowserPage}. If a tag is used under a {@code BrowserPage} instance
     * and the same instance of tag needs to be used in another instance of
     * {@code BrowserPage} then the tag needs to be reset before use otherwise there
     * could be some strange behaviour in the UI. To avoid compromising performance
     * such usage never throws any exception. <br>
     * <p>
     * NB:- Child tag cannot be reset, i.e. this tag should not be a child of
     * another tag.
     *
     * @throws InvalidTagException if the tag is already used by another tag, i.e.
     *                             if this tag has a parent tag.
     * @author WFF
     * @since 2.1.13
     */
    public final void resetHierarchy() throws InvalidTagException {

        if (parent != null) {
            throw new InvalidTagException("Child tag cannot be reset");
        }

        final Lock lock = lockAndGetWriteLock();
        try {

            loopThroughAllNestedChildren(child -> {
                child.removeAttributes(false, DataWffId.ATTRIBUTE_NAME);
                return true;
            }, true, this);

        } finally {
            lock.unlock();
        }
    }

    /**
     * NB: it might lead to StackOverflowException if the tag hierarchy is deep.
     *
     * @return stream of all nested children including this parent object.
     * @author WFF
     * @since 3.0.0
     */
    private Stream<AbstractHtml> buildNestedChildrenIncludingParent() {
        return Stream.concat(Stream.of(this),
                children.stream().flatMap(AbstractHtml::buildNestedChildrenIncludingParent));
    }

    /**
     * NB: it might lead to StackOverflowException if the tag hierarchy is deep.
     *
     * @param parent the parent object from which the nested children stream to be
     *               built.
     * @return stream of all nested children including the given parent object.
     * @author WFF
     * @since 2.1.15
     */
    protected static Stream<AbstractHtml> getAllNestedChildrenIncludingParent(final AbstractHtml parent) {
        return parent.buildNestedChildrenIncludingParent();
    }

    /**
     * @return the read lock object
     * @since 3.0.1
     */
    protected Lock getReadLock() {
        return sharedObject.getLock(ACCESS_OBJECT).readLock();
    }

    /**
     * @return the write lock object
     * @since 3.0.1
     */
    protected Lock getWriteLock() {
        return sharedObject.getLock(ACCESS_OBJECT).writeLock();
    }

    /**
     * @param sharedObject from which the lock to get
     * @return the read lock object
     * @since 3.0.15
     */
    static final Lock getReadLock(final AbstractHtml5SharedObject sharedObject) {
        return sharedObject.getLock(ACCESS_OBJECT).readLock();
    }

    /**
     * @param sharedObject from which the lock to get
     * @return the write lock object
     * @since 3.0.15
     */
    static final Lock getWriteLock(final AbstractHtml5SharedObject sharedObject) {
        return sharedObject.getLock(ACCESS_OBJECT).writeLock();
    }

    /**
     * NB: without this method this.sharedObject in the later execution of nested
     * methods may be different than the lock acquired sharedObject, we have faced
     * this issue that is why it is implemented.
     *
     * @param foreignTags
     * @return the locks
     */
    private List<Lock> lockAndGetWriteLocks(final AbstractHtml... foreignTags) {

//        AbstractHtml5SharedObject currentSO;
//        List<Lock> locks = null;
//        do {
//            if (locks != null) {
//                for (final Lock lck : locks) {
//                    lck.unlock();
//                }
//            }
//            currentSO = sharedObject;
//            locks = TagUtil.lockAndGetWriteLocks(this, ACCESS_OBJECT, foreignTags);
//        } while (!currentSO.equals(sharedObject));

        return TagUtil.lockAndGetWriteLocks(this, ACCESS_OBJECT, foreignTags);
    }

    /**
     * NB: without this method this.sharedObject in the later execution of nested
     * methods may be different than the lock acquired sharedObject, we have faced
     * this issue that is why it is implemented.
     *
     * @return the lock
     */
    Lock lockAndGetWriteLock() {

        AbstractHtml5SharedObject currentSO;
        Lock lock = null;
        do {
            if (lock != null) {
                lock.unlock();
                Thread.onSpinWait();
            }
            currentSO = sharedObject;
            lock = currentSO.getLock(ACCESS_OBJECT).writeLock();
            lock.lock();
        } while (!currentSO.equals(sharedObject));

        return lock;
    }

    /**
     * NB: without this method this.sharedObject in the later execution of nested
     * methods may be different than the lock acquired sharedObject, we have faced
     * this issue that is why it is implemented. This method is only for internal
     * use.
     *
     * @param latestSharedObject the latest sharedObject for comparison.
     * @return the lock if the sharedObject of this tag is latest compared to
     *         latestSharedObject otherwise null. i.e. If the latestSharedObject is
     *         newer than the sharedObject of this tag it will return null.
     * @since 12.0.1
     */
    WriteLock lockAndGetWriteLock(final AbstractHtml5SharedObject latestSharedObject) {

        AbstractHtml5SharedObject currentSO;
        WriteLock lock = null;
        do {
            if (lock != null) {
                lock.unlock();
                Thread.onSpinWait();
            }
            currentSO = sharedObject;
            if (latestSharedObject != null && latestSharedObject.objectId().compareTo(currentSO.objectId()) > 0) {
                return null;
            }
            lock = currentSO.getLock(ACCESS_OBJECT).writeLock();
            lock.lock();
        } while (!currentSO.equals(sharedObject));

        return lock;
    }

    /**
     * Note: This method is only for internal use.
     *
     * @param accessObject       the access object
     * @param latestSharedObject the sharedObject to compare
     * @return the lock if locked
     * @since 12.0.1
     */
    public WriteLock lockAndGetWriteLock(@SuppressWarnings("exports") final SecurityObject accessObject,
            final AbstractHtml5SharedObject latestSharedObject) {
        if (!IndexedClassType.ABSTRACT_ATTRIBUTE.equals(accessObject.forClassType())) {
            throw new WffSecurityException("Not allowed to consume this method. This method is for internal use.");
        }
        return lockAndGetWriteLock(latestSharedObject);
    }

    /**
     * NB: without this method this.sharedObject in the later execution of nested
     * methods may be different than the lock acquired sharedObject, we have faced
     * this issue that is why it is implemented.
     *
     * @return the lock
     */
    Lock lockAndGetReadLock() {

        AbstractHtml5SharedObject currentSO;
        Lock lock = null;
        do {
            if (lock != null) {
                lock.unlock();
                Thread.onSpinWait();
            }
            currentSO = sharedObject;
            lock = currentSO.getLock(ACCESS_OBJECT).readLock();
            lock.lock();
        } while (!currentSO.equals(sharedObject));

        return lock;
    }

    /**
     * NB: without this method this.sharedObject in the later execution of nested
     * methods may be different than the lock acquired sharedObject, we have faced
     * this issue that is why it is implemented.
     *
     * @param latestSharedObject the latest sharedObject for comparison.
     * @return the lock if the sharedObject of this tag is latest compared to
     *         latestSharedObject otherwise null. i.e. If the latestSharedObject is
     *         newer than the sharedObject of this tag it will return null.
     * @since 12.0.1
     */
    ReadLock lockAndGetReadLock(final AbstractHtml5SharedObject latestSharedObject) {

        AbstractHtml5SharedObject currentSO;
        ReadLock lock = null;
        do {
            if (lock != null) {
                lock.unlock();
                Thread.onSpinWait();
            }
            currentSO = sharedObject;
            if (latestSharedObject != null && latestSharedObject.objectId().compareTo(currentSO.objectId()) > 0) {
                return null;
            }
            lock = currentSO.getLock(ACCESS_OBJECT).readLock();
            lock.lock();
        } while (!currentSO.equals(sharedObject));

        return lock;
    }

    /**
     * Note: This method is only for internal use.
     *
     * @param accessObject       the access object
     * @param latestSharedObject the sharedObject to compare
     * @return the lock if locked
     * @since 12.0.1
     */
    public ReadLock lockAndGetReadLock(@SuppressWarnings("exports") final SecurityObject accessObject,
            final AbstractHtml5SharedObject latestSharedObject) {
        if (!IndexedClassType.ABSTRACT_ATTRIBUTE.equals(accessObject.forClassType())) {
            throw new WffSecurityException("Not allowed to consume this method. This method is for internal use.");
        }
        return lockAndGetReadLock(latestSharedObject);
    }

    /**
     * for internal purpose only
     *
     * @return
     * @since 3.0.6
     */
    byte[] getTagNameIndexBytes() {
        return tagNameIndexBytes;
    }

    /**
     * This method can avoid creating anonymous class coding. <br>
     * Eg: <br>
     *
     * <pre><code>
     * Div rootDiv = new Div(null, new Id("rootDivId")).&lt;Div&gt;give(parent -&gt; {
     *     new Div(parent, new Id("parentDivId")).give(nestedTag1 -&gt; {
     *         new Div(nestedTag1, new Id("child1"));
     *         new Div(nestedTag1, new Id("child2"));
     *         new Div(nestedTag1, new Id("child3"));
     *     });
     * });
     *
     * System.out.println(rootDiv.toHtmlString());
     * </code></pre>
     * <p>
     * produces
     *
     * <pre><code>
     * &lt;div id="rootDivId"&gt;
     *    &lt;div id="parentDivId"&gt;
     *         &lt;div id="child1"&gt;&lt;/div&gt;
     *         &lt;div id="child2"&gt;&lt;/div&gt;
     *         &lt;div id="child3"&gt;&lt;/div&gt;
     *     &lt;/div&gt;
     * &lt;/div&gt;
     * </code></pre>
     *
     * @param consumer the consumer object
     * @return the same object on which give method is called.
     * @since 3.0.7
     */
    @SuppressWarnings("unchecked")
    public <T extends AbstractHtml> T give(final Consumer<T> consumer) {
        consumer.accept((T) this);
        return (T) this;
    }

    /**
     * This method can avoid creating anonymous class coding. <br>
     * Eg: <br>
     *
     * <pre><code>
     * Div div = new Div(null, new Id("rootDivId")).give(TagContent::text, "Hello World");
     * System.out.println(div.toHtmlString());
     * </code></pre>
     * <p>
     * produces
     *
     * <pre><code>
     * &lt;div id="rootDivId"&gt;Hello World&lt;/div&gt;
     * </code></pre>
     * <p>
     * A mix of give methods will be
     *
     * <pre><code>
     * Div div = new Div(null).give(dv -&gt; {
     *     new Span(dv).give(TagContent::text, "Hello World");
     * });
     * </code></pre>
     * <p>
     * produces
     *
     * <pre><code>
     * &lt;div&gt;
     *     &lt;span&gt;Hello World&lt;/span&gt;
     * &lt;/div&gt;
     * </code></pre>
     *
     * @param consumer
     * @param input    the object to be passed as second argument of
     *                 {@code BiFunction#apply(Object, Object)} method.
     * @return the object which {@code BiFunction#apply(Object, Object)} returns
     *         which will be a subclass of {@code AbstractHtml}
     * @since 3.0.13
     */
    @SuppressWarnings("unchecked")
    public <R extends AbstractHtml, I> R give(final BiFunction<R, I, R> consumer, final I input) {
        return consumer.apply((R) this, input);
    }

    /**
     * @return the unique id for this object.
     * @since 3.0.18
     */
    @SuppressWarnings("exports")
    public final InternalId internalId() {
        return internalId;
    }

    /**
     * Only for internal use
     *
     * @param <T>
     * @param contentFormatter
     * @since 3.0.18
     * @since 12.0.0-beta.12 removed accessObject as the visibility of this method
     *        is limited to package level
     */
    final <T> void setCacheSTCFormatter(final SharedTagContent.ContentFormatter<T> contentFormatter) {
        cachedStcFormatter = contentFormatter;
    }

    /**
     * Note: only for testing purpose
     *
     * @return
     */
    final Object getCachedStcFormatter() {
        return cachedStcFormatter;
    }

    /**
     * Replaces the children of this tag with the tags supplied by
     * {@code successTagsSupplier} if the predicate test returns true otherwise
     * replaces the children with tags supplied by {@code failTagsSupplier} if no
     * further {@code whenURI} conditions exist and if the {@code failTagsSupplier}
     * is null the existing children of this tag will be removed. To remove the
     * supplier objects from this tag, call
     * {@link AbstractHtml#removeURIChangeActions()} method. To get the current uri
     * inside the supplier object call {@link BrowserPage#getURI()}. This action
     * will be performed after initial client ping. You can call {@code whenURI}
     * multiple times to set multiple actions,
     * {@link AbstractHtml#removeURIChangeAction(int)} may be used to remove each
     * action at the given index. If multiple actions are added by this method, only
     * the first {@code uriEventPredicate} test passed action will be performed on
     * uri change. The main intention of this method is to set children tags for
     * this tag when the given {@code uriEventPredicate} test passes on URI change.
     * <br>
     * Note: This method uses {@code null} for {@code failTagsSupplier}.
     *
     * @param uriEventPredicate   the predicate object to test, the argument of the
     *                            test method is the changed uri details, if the
     *                            test method returns true then the tags given by
     *                            {@code successTagsSupplier} will be added as inner
     *                            html to this tag. If test returns false, the tags
     *                            given by {@code failTagsSupplier} will be added as
     *                            * inner html to this tag and if the
     *                            {@code failTagsSupplier} is null the existing
     *                            children will be removed from this tag.
     * @param successTagsSupplier the supplier object for child tags if
     *                            {@code uriEventPredicate} test returns true. If
     *                            {@code successTagsSupplier.get()} method returns
     *                            null, no action will be done on the tag.
     *
     * @return URIStateSwitch
     * @since 12.0.0-beta.1
     */
    @Override
    public URIStateSwitch whenURI(final Predicate<URIEvent> uriEventPredicate,
            final Supplier<AbstractHtml[]> successTagsSupplier) {
        return whenURI(uriEventPredicate, successTagsSupplier, (Supplier<AbstractHtml[]>) null, -1);
    }

    /**
     * Replaces the children of this tag with the tags supplied by
     * {@code successTagsSupplier} if the predicate test returns true otherwise
     * replaces the children with tags supplied by {@code failTagsSupplier} if no
     * further {@code whenURI} conditions exist and if the {@code failTagsSupplier}
     * is null the existing children of this tag will be removed. To remove the
     * supplier objects from this tag, call
     * {@link AbstractHtml#removeURIChangeActions()} method. To get the current uri
     * inside the supplier object call {@link BrowserPage#getURI()}. This action
     * will be performed after initial client ping. You can call {@code whenURI}
     * multiple times to set multiple actions,
     * {@link AbstractHtml#removeURIChangeAction(int)} may be used to remove each
     * action at the given index. If multiple actions are added by this method, only
     * the first {@code uriEventPredicate} test passed action will be performed on
     * uri change. The main intention of this method is to set children tags for
     * this tag when the given {@code uriEventPredicate} test passes on URI change.
     * <br>
     * Note: This method uses {@code null} for {@code failTagsSupplier}.
     *
     * @param uriEventPredicate   the predicate object to test, the argument of the
     *                            test method is the changed uri details, if the
     *                            test method returns true then the tags given by
     *                            {@code successTagsSupplier} will be added as inner
     *                            html to this tag. If test returns false, the tags
     *                            given by {@code failTagsSupplier} will be added as
     *                            * inner html to this tag and if the
     *                            {@code failTagsSupplier} is null the existing
     *                            children will be removed from this tag.
     * @param successTagsSupplier the supplier object for child tags if
     *                            {@code uriEventPredicate} test returns true. If
     *                            {@code successTagsSupplier.get()} method returns
     *                            null, no action will be done on the tag.
     *
     * @param index               the index to replace the existing action with
     *                            this. A value less than zero will add this
     *                            condition to the last.
     *
     * @return URIStateSwitch
     * @since 12.0.0-beta.1
     */
    @Override
    public URIStateSwitch whenURI(final Predicate<URIEvent> uriEventPredicate,
            final Supplier<AbstractHtml[]> successTagsSupplier, final int index) {
        return whenURI(uriEventPredicate, successTagsSupplier, (Supplier<AbstractHtml[]>) null, index);
    }

    /**
     * Replaces the children of this tag with the tags supplied by
     * {@code successTagsSupplier} if the predicate test returns true otherwise
     * replaces the children with tags supplied by {@code failTagsSupplier} if no
     * further {@code whenURI} conditions exist and if the {@code failTagsSupplier}
     * is null the existing children of this tag will be removed. To remove the
     * supplier objects from this tag, call
     * {@link AbstractHtml#removeURIChangeActions()} method. To get the current uri
     * inside the supplier object call {@link BrowserPage#getURI()}. This action
     * will be performed after initial client ping. You can call {@code whenURI}
     * multiple times to set multiple actions,
     * {@link AbstractHtml#removeURIChangeAction(int)} may be used to remove each
     * action at the given index. If multiple actions are added by this method, only
     * the first {@code uriEventPredicate} test passed action will be performed on
     * uri change. The main intention of this method is to set children tags for
     * this tag when the given {@code uriEventPredicate} test passes on URI change.
     *
     * @param uriEventPredicate   the predicate object to test, the argument of the
     *                            test method is the changed uri details, if the
     *                            test method returns true then the tags given by
     *                            {@code successTagsSupplier} will be added as inner
     *                            html to this tag. If test returns false, the tags
     *                            given by {@code failTagsSupplier} will be added as
     *                            * inner html to this tag and if the
     *                            {@code failTagsSupplier} is null the existing
     *                            children will be removed from this tag.
     * @param successTagsSupplier the supplier object for child tags if
     *                            {@code uriEventPredicate} test returns true. If
     *                            {@code successTagsSupplier.get()} method returns
     *                            null, no action will be done on the tag.
     * @param failTagsSupplier    the supplier object for child tags if
     *                            {@code uriEventPredicate} test returns false. If
     *                            {@code failTagsSupplier.get()} * method returns
     *                            null, no action will be done on the tag.
     *
     * @return URIStateSwitch
     * @since 12.0.0-beta.1
     */
    @Override
    public URIStateSwitch whenURI(final Predicate<URIEvent> uriEventPredicate,
            final Supplier<AbstractHtml[]> successTagsSupplier, final Supplier<AbstractHtml[]> failTagsSupplier) {
        return whenURI(uriEventPredicate, successTagsSupplier, failTagsSupplier, -1);
    }

    /**
     * Replaces the children of this tag with the tags supplied by
     * {@code successTagsSupplier} if the predicate test returns true otherwise
     * replaces the children with tags supplied by {@code failTagsSupplier} if no
     * further {@code whenURI} conditions exist and if the {@code failTagsSupplier}
     * is null the existing children of this tag will be removed. To remove the
     * supplier objects from this tag, call
     * {@link AbstractHtml#removeURIChangeActions()} method. To get the current uri
     * inside the supplier object call {@link BrowserPage#getURI()}. This action
     * will be performed after initial client ping. You can call {@code whenURI}
     * multiple times to set multiple actions,
     * {@link AbstractHtml#removeURIChangeAction(int)} may be used to remove each
     * action at the given index. If multiple actions are added by this method, only
     * the first {@code uriEventPredicate} test passed action will be performed on
     * uri change. The main intention of this method is to set children tags for
     * this tag when the given {@code uriEventPredicate} test passes on URI change.
     *
     * @param uriEventPredicate   the predicate object to test, the argument of the
     *                            test method is the changed uri details, if the
     *                            test method returns true then the tags given by
     *                            {@code successTagsSupplier} will be added as inner
     *                            html to this tag. If test returns false, the tags
     *                            given by {@code failTagsSupplier} will be added as
     *                            * inner html to this tag and if the
     *                            {@code failTagsSupplier} is null the existing
     *                            children will be removed from this tag.
     * @param successTagsSupplier the supplier object for child tags if
     *                            {@code uriEventPredicate} test returns true. If
     *                            {@code successTagsSupplier.get()} method returns
     *                            null, no action will be done on the tag.
     * @param failTagsSupplier    the supplier object for child tags if
     *                            {@code uriEventPredicate} test returns false. If
     *                            {@code failTagsSupplier.get()} * method returns
     *                            null, no action will be done on the tag.
     * @param index               the index to replace the existing action with
     *                            this. A value less than zero will add this
     *                            condition to the last.
     * @return URIStateSwitch
     * @since 12.0.0-beta.1
     */
    @Override
    public URIStateSwitch whenURI(final Predicate<URIEvent> uriEventPredicate,
            final Supplier<AbstractHtml[]> successTagsSupplier, final Supplier<AbstractHtml[]> failTagsSupplier,
            final int index) {

        Objects.requireNonNull(uriEventPredicate, "uriEventPredicate cannot be null in whenURI method");
        Objects.requireNonNull(successTagsSupplier, "successTagsSupplier cannot be null in whenURI method");
        return whenURI(uriEventPredicate, successTagsSupplier, failTagsSupplier,
                WhenURIMethodType.SUCCESS_SUPPLIER_FAIL_SUPPLIER, null, null, index);
    }

    /**
     * Replaces the children of this tag with the tags supplied by
     * {@code successTagsSupplier} if the predicate test returns true otherwise
     * invokes {@code failConsumer} if no further {@code whenURI} conditions exist
     * and if the {@code failConsumer} is null the existing children of this tag
     * will be removed. To remove the supplier objects from this tag, call
     * {@link AbstractHtml#removeURIChangeActions()} method. To get the current uri
     * inside the supplier object call {@link BrowserPage#getURI()}. This action
     * will be performed after initial client ping. You can call {@code whenURI}
     * multiple times to set multiple actions,
     * {@link AbstractHtml#removeURIChangeAction(int)} may be used to remove each
     * action at the given index. If multiple actions are added by this method, only
     * the first {@code uriEventPredicate} test passed action will be performed on
     * uri change. The main intention of this method is to set children tags for
     * this tag when the given {@code uriEventPredicate} test passes on URI change.
     *
     * @param uriEventPredicate   the predicate object to test, the argument of the
     *                            test method is the changed uri details, if the
     *                            test method returns true then the tags given by
     *                            {@code successTagsSupplier} will be added as inner
     *                            html to this tag. If test returns false, the tags
     *                            given by {@code failTagsSupplier} will be added as
     *                            * inner html to this tag and if the
     *                            {@code failTagsSupplier} is null the existing
     *                            children will be removed from this tag.
     * @param successTagsSupplier the supplier object for child tags if
     *                            {@code uriEventPredicate} test returns true. If
     *                            {@code successTagsSupplier.get()} method returns
     *                            null, no action will be done on the tag.
     * @param failConsumer        the consumer to execute if
     *                            {@code uriEventPredicate.test()} returns false.
     *                            {@code null} can be passed if there is no
     *                            {@code failConsumer}.
     * @param index               the index to replace the existing action with
     *                            this. A value less than zero will add this
     *                            condition to the last.
     * @return URIStateSwitch
     * @since 12.0.0-beta.1
     */
    @Override
    public URIStateSwitch whenURI(final Predicate<URIEvent> uriEventPredicate,
            final Supplier<AbstractHtml[]> successTagsSupplier, final Consumer<TagEvent> failConsumer,
            final int index) {

        Objects.requireNonNull(uriEventPredicate, "uriEventPredicate cannot be null in whenURI method");
        Objects.requireNonNull(successTagsSupplier, "successTagsSupplier cannot be null in whenURI method");
        return whenURI(uriEventPredicate, successTagsSupplier, null, WhenURIMethodType.SUCCESS_SUPPLIER_FAIL_CONSUMER,
                null, failConsumer, index);
    }

    /**
     * Replaces the children of this tag with the tags supplied by
     * {@code successTagsSupplier} if the predicate test returns true otherwise
     * invokes {@code failConsumer} if no further {@code whenURI} conditions exist
     * and if the {@code failConsumer} is null the existing children of this tag
     * will be removed. To remove the supplier objects from this tag, call
     * {@link AbstractHtml#removeURIChangeActions()} method. To get the current uri
     * inside the supplier object call {@link BrowserPage#getURI()}. This action
     * will be performed after initial client ping. You can call {@code whenURI}
     * multiple times to set multiple actions,
     * {@link AbstractHtml#removeURIChangeAction(int)} may be used to remove each
     * action at the given index. If multiple actions are added by this method, only
     * the first {@code uriEventPredicate} test passed action will be performed on
     * uri change. The main intention of this method is to set children tags for
     * this tag when the given {@code uriEventPredicate} test passes on URI change.
     *
     * @param uriEventPredicate   the predicate object to test, the argument of the
     *                            test method is the changed uri details, if the
     *                            test method returns true then the tags given by
     *                            {@code successTagsSupplier} will be added as inner
     *                            html to this tag. If test returns false, the tags
     *                            given by {@code failTagsSupplier} will be added as
     *                            * inner html to this tag and if the
     *                            {@code failTagsSupplier} is null the existing
     *                            children will be removed from this tag.
     * @param successTagsSupplier the supplier object for child tags if
     *                            {@code uriEventPredicate} test returns true. If
     *                            {@code successTagsSupplier.get()} method returns
     *                            null, no action will be done on the tag.
     * @param failConsumer        the consumer to execute if
     *                            {@code uriEventPredicate.test()} returns false.
     *                            {@code null} can be passed if there is no
     *                            {@code failConsumer}.
     *
     * @return URIStateSwitch
     * @since 12.0.0-beta.1
     */
    @Override
    public URIStateSwitch whenURI(final Predicate<URIEvent> uriEventPredicate,
            final Supplier<AbstractHtml[]> successTagsSupplier, final Consumer<TagEvent> failConsumer) {

        Objects.requireNonNull(uriEventPredicate, "uriEventPredicate cannot be null in whenURI method");
        Objects.requireNonNull(successTagsSupplier, "successTagsSupplier cannot be null in whenURI method");
        return whenURI(uriEventPredicate, successTagsSupplier, null, WhenURIMethodType.SUCCESS_SUPPLIER_FAIL_CONSUMER,
                null, failConsumer, -1);
    }

    /**
     * Invokes {@code successConsumer} if the predicate test returns true otherwise
     * replaces the children of this tag with the tags supplied by
     * {@code failTagsSupplier} if no further {@code whenURI} conditions exist and
     * if the {@code successConsumer} is null the existing children of this tag will
     * be removed if predicate test returns true. To remove the whenURI actions from
     * this tag, call {@link AbstractHtml#removeURIChangeActions()} method. To get
     * the current uri inside the supplier object call {@link BrowserPage#getURI()}.
     * This action will be performed after initial client ping. You can call
     * {@code whenURI} multiple times to set multiple actions,
     * {@link AbstractHtml#removeURIChangeAction(int)} may be used to remove each
     * action at the given index. If multiple actions are added by this method, only
     * the first {@code uriEventPredicate} test passed action will be performed on
     * uri change. The main intention of this method is to set children tags for
     * this tag when the given {@code uriEventPredicate} test passes on URI change.
     *
     * @param uriEventPredicate the predicate object to test, the argument of the
     *                          test method is the changed uri details, if the test
     *                          method returns true then the tags given by
     *                          {@code successTagsSupplier} will be added as inner
     *                          html to this tag. If test returns false, the tags
     *                          given by {@code failTagsSupplier} will be added as *
     *                          inner html to this tag and if the
     *                          {@code failTagsSupplier} is null the existing
     *                          children will be removed from this tag.
     * @param successConsumer   the consumer object to invoke if
     *                          {@code uriEventPredicate} test returns true, no
     *                          changes will be done on the tag.
     * @param failTagsSupplier  the supplier object to supply child tags for the tag
     *                          if {@code uriEventPredicate.test()} returns false.
     *                          {@code null} can be passed if there is no
     *                          {@code failTagsSupplier} in such case the existing
     *                          children will be removed.
     *
     * @param index             the index to replace the existing action with this.
     *                          A value less than zero will add this condition to
     *                          the last.
     *
     * @return URIStateSwitch
     * @since 12.0.0-beta.1
     */
    @Override
    public URIStateSwitch whenURI(final Predicate<URIEvent> uriEventPredicate, final Consumer<TagEvent> successConsumer,
            final Supplier<AbstractHtml[]> failTagsSupplier, final int index) {

        Objects.requireNonNull(uriEventPredicate, "uriEventPredicate cannot be null in whenURI method");
        Objects.requireNonNull(successConsumer, "successConsumer cannot be null in whenURI method");
        return whenURI(uriEventPredicate, null, failTagsSupplier, WhenURIMethodType.SUCCESS_CONSUMER_FAIL_SUPPLIER,
                successConsumer, null, index);
    }

    /**
     * Invokes {@code successConsumer} if the predicate test returns true otherwise
     * replaces the children of this tag with the tags supplied by
     * {@code failTagsSupplier} if no further {@code whenURI} conditions exist and
     * if the {@code successConsumer} is null the existing children of this tag will
     * be removed if predicate test returns true. To remove the whenURI actions from
     * this tag, call {@link AbstractHtml#removeURIChangeActions()} method. To get
     * the current uri inside the supplier object call {@link BrowserPage#getURI()}.
     * This action will be performed after initial client ping. You can call
     * {@code whenURI} multiple times to set multiple actions,
     * {@link AbstractHtml#removeURIChangeAction(int)} may be used to remove each
     * action at the given index. If multiple actions are added by this method, only
     * the first {@code uriEventPredicate} test passed action will be performed on
     * uri change. The main intention of this method is to set children tags for
     * this tag when the given {@code uriEventPredicate} test passes on URI change.
     *
     * @param uriEventPredicate the predicate object to test, the argument of the
     *                          test method is the changed uri details, if the test
     *                          method returns true then the tags given by
     *                          {@code successTagsSupplier} will be added as inner
     *                          html to this tag. If test returns false, the tags
     *                          given by {@code failTagsSupplier} will be added as *
     *                          inner html to this tag and if the
     *                          {@code failTagsSupplier} is null the existing
     *                          children will be removed from this tag.
     * @param successConsumer   the consumer object to invoke if
     *                          {@code uriEventPredicate} test returns true, no
     *                          changes will be done on the tag.
     * @param failTagsSupplier  the supplier object to supply child tags for the tag
     *                          if {@code uriEventPredicate.test()} returns false.
     *                          {@code null} can be passed if there is no
     *                          {@code failTagsSupplier} in such case the existing
     *                          children will be removed.
     *
     *
     * @return URIStateSwitch
     * @since 12.0.0-beta.1
     */
    @Override
    public URIStateSwitch whenURI(final Predicate<URIEvent> uriEventPredicate, final Consumer<TagEvent> successConsumer,
            final Supplier<AbstractHtml[]> failTagsSupplier) {

        Objects.requireNonNull(uriEventPredicate, "uriEventPredicate cannot be null in whenURI method");
        Objects.requireNonNull(successConsumer, "successConsumer cannot be null in whenURI method");
        return whenURI(uriEventPredicate, null, failTagsSupplier, WhenURIMethodType.SUCCESS_CONSUMER_FAIL_SUPPLIER,
                successConsumer, null, -1);
    }

    /**
     *
     * @param uriEventPredicate
     * @param successConsumer   the consumer to execute if
     *                          {@code uriEventPredicate.test()} returns true
     * @param failConsumer      the consumer to execute if
     *                          {@code uriEventPredicate.test()} returns false.
     *                          {@code null} can be passed if there is no
     *                          {@code failConsumer}.
     * @param index             the position at which this action be the index to
     *                          replace the existing action with this. A value less
     *                          than zero will add this condition to the last.
     * @return URIStateSwitch
     * @since 12.0.0-beta.1
     */
    @Override
    public URIStateSwitch whenURI(final Predicate<URIEvent> uriEventPredicate, final Consumer<TagEvent> successConsumer,
            final Consumer<TagEvent> failConsumer, final int index) {

        Objects.requireNonNull(uriEventPredicate, "uriEventPredicate cannot be null in whenURI method");
        Objects.requireNonNull(successConsumer, "successConsumer cannot be null in whenURI method");
        return whenURI(uriEventPredicate, null, null, WhenURIMethodType.SUCCESS_CONSUMER_FAIL_CONSUMER, successConsumer,
                failConsumer, index);
    }

    /**
     *
     * @param uriEventPredicate
     * @param successConsumer   the consumer to execute if
     *                          {@code uriEventPredicate.test()} returns true
     * @param failConsumer      the consumer to execute if
     *                          {@code uriEventPredicate.test()} returns false.
     *                          {@code null} can be passed if there is no
     *                          {@code failConsumer}.
     * @return URIStateSwitch
     * @since 12.0.0-beta.1
     */
    @Override
    public URIStateSwitch whenURI(final Predicate<URIEvent> uriEventPredicate, final Consumer<TagEvent> successConsumer,
            final Consumer<TagEvent> failConsumer) {
        return whenURI(uriEventPredicate, successConsumer, failConsumer, -1);
    }

    @Override
    public URIStateSwitch whenURI(final Predicate<URIEvent> uriEventPredicate, final Consumer<TagEvent> successConsumer,
            final int index) {

        Objects.requireNonNull(uriEventPredicate, "uriEventPredicate cannot be null in whenURI method");
        Objects.requireNonNull(successConsumer, "successConsumer cannot be null in whenURI method");
        return whenURI(uriEventPredicate, null, null, WhenURIMethodType.SUCCESS_CONSUMER_FAIL_CONSUMER, successConsumer,
                null, index);
    }

    /**
     *
     * @param uriEventPredicate
     * @param successConsumer   the consumer to execute if
     *                          {@code uriEventPredicate.test()} returns true
     * @return URIStateSwitch
     * @since 12.0.0-beta.1
     */
    @Override
    public URIStateSwitch whenURI(final Predicate<URIEvent> uriEventPredicate,
            final Consumer<TagEvent> successConsumer) {
        return whenURI(uriEventPredicate, successConsumer, (Consumer<TagEvent>) null, -1);
    }

    /**
     * @param uriEventPredicate
     * @param successTagsSupplier
     * @param failTagsSupplier
     * @param methodType
     * @param successConsumer
     * @param failConsumer
     * @param index
     * @return AbstractHtml
     * @since 12.0.0-beta.1
     */
    private AbstractHtml whenURI(final Predicate<URIEvent> uriEventPredicate,
            final Supplier<AbstractHtml[]> successTagsSupplier, final Supplier<AbstractHtml[]> failTagsSupplier,
            final WhenURIMethodType methodType, final Consumer<TagEvent> successConsumer,
            final Consumer<TagEvent> failConsumer, final int index) {

        final Lock lock = lockAndGetWriteLock();
        try {
            // sharedObject should be after locking
            final AbstractHtml5SharedObject sharedObject = this.sharedObject;

            final URIChangeContent uriChangeContent = new URIChangeContent(uriEventPredicate, successTagsSupplier,
                    failTagsSupplier, successConsumer, failConsumer, methodType, new WhenURIProperties());

            if (uriChangeContents == null && index >= 0) {
                throw new InvalidValueException("There is no existing whenURI condition to replace");
            }
            uriChangeContents = uriChangeContents != null ? uriChangeContents : new LinkedList<>();
            if (index < 0) {
                uriChangeContents.add(uriChangeContent);
            } else {
                if (index >= uriChangeContents.size()) {
                    throw new InvalidValueException("There is no existing whenURI condition at this index");
                }
                uriChangeContents.set(index, uriChangeContent);
            }

            sharedObject.whenURIUsed(ACCESS_OBJECT);

            applyURIChange(sharedObject, null, true);

        } finally {
            lock.unlock();
        }

        return this;
    }

    /**
     * Removes all whenURI actions.
     *
     * @since 12.0.0-beta.1
     */
    @Override
    public void removeURIChangeActions() {
        final Lock lock = lockAndGetWriteLock();
        try {
            uriChangeContents = null;
            lastURIEvent = null;
            lastURIPredicateTest = null;
            lastWhenURIIndex = -1;
            lastURIChangeContent = null;
        } finally {
            lock.unlock();
        }
    }

    /**
     * @param index the action to remove at the given index
     * @since 12.0.0-beta.1
     */
    @Override
    public void removeURIChangeAction(final int index) {
        final Lock lock = lockAndGetWriteLock();
        try {
            if (uriChangeContents == null) {
                throw new InvalidValueException("There is no existing whenURI action.");
            }

            uriChangeContents.remove(index);
            if (uriChangeContents.isEmpty()) {
                uriChangeContents = null;
                lastURIEvent = null;
                lastURIPredicateTest = null;
                lastWhenURIIndex = -1;
                lastURIChangeContent = null;
            }

        } finally {
            lock.unlock();
        }
    }

    /**
     * NB: only for internal use
     *
     * @param uriEvent
     * @param expectedSO
     * @since 12.0.0-beta.1
     * @return true if sharedObjects are equals
     */
    final boolean changeInnerHtmlsForURIChange(final URIEvent uriEvent, final AbstractHtml5SharedObject expectedSO) {

        final Lock lock = lockAndGetWriteLock();
        try {
            if (expectedSO.equals(sharedObject)) {

                final URIChangeTagSupplier uriChangeTagSupplier = expectedSO.getURIChangeTagSupplier(ACCESS_OBJECT);
                final ObjectId hierarchicalLoopId = LoopIdGenerator.nextId();
                final Deque<List<AbstractHtml>> childrenStack = new ArrayDeque<>();
                childrenStack.push(List.of(this));
                List<AbstractHtml> children;
                while ((children = childrenStack.poll()) != null) {
                    for (final AbstractHtml eachChild : children) {
                        if (hierarchicalLoopId.equals(eachChild.hierarchicalLoopId)) {
                            continue;
                        }
                        eachChild.hierarchicalLoopId = hierarchicalLoopId;
                        final AbstractHtml[] innerHtmls = eachChild.changeInnerHtmlsForURIChange(uriEvent, true);
                        if (innerHtmls != null && innerHtmls.length > 0) {
                            childrenStack.push(List.of(innerHtmls));
                        } else if (eachChild.children != null && !eachChild.children.isEmpty()) {
                            childrenStack.push(List.copyOf(eachChild.children));
                        }
                        if (eachChild.uriChangeContents != null) {
                            uriChangeTagSupplier.supply(eachChild);
                        }
                    }
                }

                return true;
            }
        } finally {
            lock.unlock();
        }
        return false;
    }

    /**
     * NB: only for internal use
     *
     * @param uriEvent
     * @param updateClient to push changes to the UI
     * @return
     * @since 12.0.0-beta.1
     */
    private AbstractHtml[] changeInnerHtmlsForURIChange(final URIEvent uriEvent, final boolean updateClient) {

        AbstractHtml[] insertedHtmls = null;
        final String lastURI = lastURIEvent != null ? lastURIEvent.uriAfter() : null;
        if (uriChangeContents != null && uriEvent != null && uriEvent.uriAfter() != null
                && ((lastURIPredicateTest != null && !lastURIPredicateTest) || !uriEvent.uriAfter().equals(lastURI))) {

            URIChangeContent lastUriChangeContent = null;

            boolean executed = false;

            int index = 0;

            int uriChangeContentIndex = index;

            final int lastWhenURIIndexLocal = lastWhenURIIndex;

            for (final URIChangeContent each : uriChangeContents) {
                lastUriChangeContent = each;
                lastURIChangeContent = each;
                uriChangeContentIndex = index;
                lastWhenURIIndex = uriChangeContentIndex;
                index++;

                if (each.uriEventPredicate.test(uriEvent)) {
                    if (each.methodType.equals(WhenURIMethodType.SUCCESS_CONSUMER_FAIL_CONSUMER)
                            || each.methodType.equals(WhenURIMethodType.SUCCESS_CONSUMER_FAIL_SUPPLIER)) {
                        if (each.whenURIProperties == null || lastWhenURIIndexLocal == -1
                                || !each.whenURIProperties.duplicateSuccessPrevented
                                || lastWhenURIIndexLocal != uriChangeContentIndex || !lastWhenURISuccess) {
                            each.successConsumer.accept(new TagEvent(this, uriEvent));
                            lastWhenURISuccess = true;
                        }
                    } else {
                        if (each.whenURIProperties == null || lastWhenURIIndexLocal == -1
                                || !each.whenURIProperties.duplicateSuccessPrevented
                                || lastWhenURIIndexLocal != uriChangeContentIndex || !lastWhenURISuccess) {
                            final AbstractHtml[] innerHtmls = each.successTags.get();
                            if (innerHtmls != null) {
                                // just to throw exception if it contains null or duplicate element
                                Set.of(innerHtmls);
                                if (children == null || !Arrays.equals(children.toArray(), innerHtmls)) {
                                    addInnerHtmls(true, updateClient, innerHtmls);
                                }
                                insertedHtmls = innerHtmls;
                            }
                            lastWhenURISuccess = true;
                        }
                    }

                    executed = true;
                    break;
                }

            }

            if (lastUriChangeContent != null) {
                lastURIPredicateTest = executed;
                if (!executed) {
                    if (lastUriChangeContent.methodType.equals(WhenURIMethodType.SUCCESS_CONSUMER_FAIL_CONSUMER)
                            || lastUriChangeContent.methodType
                                    .equals(WhenURIMethodType.SUCCESS_SUPPLIER_FAIL_CONSUMER)) {
                        if (lastUriChangeContent.failConsumer != null) {
                            if (lastUriChangeContent.whenURIProperties == null || lastWhenURIIndexLocal == -1
                                    || !lastUriChangeContent.whenURIProperties.duplicateFailPrevented
                                    || lastWhenURIIndexLocal != uriChangeContentIndex || lastWhenURISuccess) {
                                lastUriChangeContent.failConsumer.accept(new TagEvent(this, uriEvent));
                                lastWhenURISuccess = false;
                            }
                            lastURIPredicateTest = true;
                        }
                    } else {
                        if (lastUriChangeContent.failTags != null) {
                            if (lastUriChangeContent.whenURIProperties == null || lastWhenURIIndexLocal == -1
                                    || !lastUriChangeContent.whenURIProperties.duplicateFailPrevented
                                    || lastWhenURIIndexLocal != uriChangeContentIndex || lastWhenURISuccess) {
                                final AbstractHtml[] innerHtmls = lastUriChangeContent.failTags.get();
                                if (innerHtmls != null) {
                                    // just to throw exception if it contains null or duplicate element
                                    Set.of(innerHtmls);
                                    if (children == null || !Arrays.equals(children.toArray(), innerHtmls)) {
                                        addInnerHtmls(true, updateClient, innerHtmls);
                                    }
                                    insertedHtmls = innerHtmls;
                                }
                                lastWhenURISuccess = false;
                            }
                            lastURIPredicateTest = true;

                        } else {
                            if (lastUriChangeContent.whenURIProperties == null || lastWhenURIIndexLocal == -1
                                    || !lastUriChangeContent.whenURIProperties.duplicateFailPrevented
                                    || lastWhenURIIndexLocal != uriChangeContentIndex || lastWhenURISuccess) {
                                if (updateClient) {
                                    removeAllChildren();
                                } else {
                                    removeAllChildrenAndGetEventsLockless(updateClient);
                                }
                                lastWhenURISuccess = false;
                            }
                        }
                    }
                }

                lastURIEvent = uriEvent;
                lastURIChangeContent = null;
            }

        }

        return insertedHtmls;
    }

    @Override
    public WhenURIProperties getCurrentWhenURIProperties() {
        final Lock lock = lockAndGetReadLock();
        try {
            // lastURIChangeContent is just to check if this method is called inside
            // predicate, success or fail object's methods.
            final URIChangeContent lastURIChangeContent = this.lastURIChangeContent;
            if (lastURIChangeContent != null) {
                return lastURIChangeContent.whenURIProperties;
            }
        } finally {
            lock.unlock();
        }
        return null;
    }

    @Override
    public WhenURIProperties getWhenURIProperties(final int index) {
        final Lock lock = lockAndGetReadLock();
        try {
            final List<URIChangeContent> uriChangeContents = this.uriChangeContents;
            if (uriChangeContents != null) {
                final URIChangeContent uriChangeContent = uriChangeContents.get(index);
                if (uriChangeContent != null) {
                    return uriChangeContent.whenURIProperties;
                }
            }
        } finally {
            lock.unlock();
        }
        return null;
    }

    // ParentLostListener starts

    /**
     * @param parentLostListener the ParentLostListener
     * @return the internal slot id of the added ParentLostListener. This id is
     *         unique only for this tag, i.e. other tag object can also return the
     *         same id.
     * @since 12.0.1
     */
    public final long addParentLostListener(final ParentLostListener parentLostListener) {
        if (parentLostListener == null) {
            throw new InvalidValueException("parentLostListener cannot be null");
        }
        final Lock lock = lockAndGetWriteLock();
        try {
            final ParentLostListenerVariables parentLostListenerVariablesLocal = parentLostListenerVariables;
            final ParentLostListenerVariables parentLostListenerVariables;
            if (parentLostListenerVariablesLocal == null) {
                parentLostListenerVariables = new ParentLostListenerVariables();
                this.parentLostListenerVariables = parentLostListenerVariables;
            } else {
                parentLostListenerVariables = parentLostListenerVariablesLocal;
            }

            final Map<Long, ParentLostListener> parentLostListenersLocal = parentLostListenerVariables.parentLostListeners;
            final Map<Long, ParentLostListener> parentLostListeners;
            if (parentLostListenersLocal == null) {
                parentLostListeners = new ConcurrentHashMap<>();
                parentLostListenerVariables.parentLostListeners = parentLostListeners;
            } else {
                parentLostListeners = parentLostListenersLocal;
            }

            final long parentLostListenerSlotId = parentLostListenerVariables.parentLostListenerSlotIdCounter + 1;
            parentLostListeners.put(parentLostListenerSlotId, parentLostListener);
            parentLostListenerVariables.parentLostListenerSlotIdCounter = parentLostListenerSlotId;
            return parentLostListenerSlotId;
        } finally {
            lock.unlock();
        }
    }

    /**
     * @param parentLostListenerSlotId the internal slot id returned by
     *                                 {@link #addParentLostListener(ParentLostListener)}
     *                                 method.
     * @return true if a listener slot is removed associated with this id otherwise
     *         false.
     * @since 12.0.1
     */
    public final boolean removeParentLostListener(final long parentLostListenerSlotId) {

        final Lock lock = lockAndGetWriteLock();
        try {

            final ParentLostListenerVariables parentLostListenerVariablesLocal = parentLostListenerVariables;
            if (parentLostListenerVariablesLocal == null) {
                return false;
            }

            final Map<Long, ParentLostListener> parentLostListenersLocal = parentLostListenerVariablesLocal.parentLostListeners;
            if (parentLostListenersLocal == null) {
                return false;
            }

            final ParentLostListener parentLostListener = parentLostListenersLocal.remove(parentLostListenerSlotId);
            if (parentLostListenersLocal.isEmpty()) {
                parentLostListenerVariablesLocal.parentLostListeners = null;
            }

            return parentLostListener != null;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Removes all <em>ParentLostListener</em>s from this tag.
     *
     * @since 12.0.1
     */
    public final void removeAllParentLostListeners() {
        final Lock lock = lockAndGetWriteLock();
        try {
            final ParentLostListenerVariables parentLostListenerVariablesLocal = parentLostListenerVariables;
            if (parentLostListenerVariablesLocal != null) {
                parentLostListenerVariablesLocal.parentLostListeners = null;
            }
        } finally {
            lock.unlock();
        }
    }

    private void invokeParentLostListeners() {
        final ParentLostListenerVariables parentLostListenerVariablesLocal = parentLostListenerVariables;
        if (parentLostListenerVariablesLocal != null) {
            final Map<Long, ParentLostListener> parentLostListenersLocal = parentLostListenerVariablesLocal.parentLostListeners;
            if (parentLostListenersLocal != null) {
                final List<Map.Entry<Long, ParentLostListener>> sortedEntries = parentLostListenersLocal.entrySet()
                        .stream().sorted(Map.Entry.comparingByKey()).toList();
                for (final Map.Entry<Long, ParentLostListener> entry : sortedEntries) {
                    entry.getValue().parentLost(new ParentLostListener.Event(this));
                }
            }
        }

    }

    private void pollAndInvokeParentLostListeners(final Deque<AbstractHtml> parentLostListenerTags) {
        if (parentLostListenerTags != null) {
            AbstractHtml tag;
            while ((tag = parentLostListenerTags.poll()) != null) {
                tag.invokeParentLostListeners();
            }
        }
    }

    private Deque<AbstractHtml> buildParentLostListenerTags(final AbstractHtml parent,
            final AbstractHtml[] tagsToBeChildren) {
        return buildParentLostListenerTags(parent, tagsToBeChildren, null, null);
    }

    private Deque<AbstractHtml> buildParentLostListenerTags(final AbstractHtml[] tagsToBeChildren) {
        return buildParentLostListenerTags(this, tagsToBeChildren, null, null);
    }

    private Deque<AbstractHtml> buildParentLostListenerTags(final AbstractHtml[] tagsToBeChildren,
            final AbstractHtml[] removedTags) {
        return buildParentLostListenerTags(this, tagsToBeChildren, removedTags, null);
    }

    private Deque<AbstractHtml> buildParentLostListenerTags(final AbstractHtml parent,
            final AbstractHtml[] tagsToBeChildren, final AbstractHtml removedTag) {
        return buildParentLostListenerTags(parent, tagsToBeChildren, null, removedTag);
    }

    private Deque<AbstractHtml> buildParentLostListenerTags(final Collection<AbstractHtml> tagsToBeChildren) {
        final int totalLength = tagsToBeChildren != null ? tagsToBeChildren.size() : 0;
        if (totalLength == 0) {
            return null;
        }
        final Deque<AbstractHtml> parentLostListenerTags = new ArrayDeque<>(totalLength);
        for (final AbstractHtml tag : tagsToBeChildren) {
            if (tag.parent != null && tag.parent != parent) {
                parentLostListenerTags.add(tag);
            }
        }
        return parentLostListenerTags.isEmpty() ? null : parentLostListenerTags;
    }

    private Deque<AbstractHtml> buildParentLostListenerTags(final AbstractHtml parent,
            final AbstractHtml[] tagsToBeChildren, final AbstractHtml[] removedTags, final AbstractHtml removedTag) {
        final int totalLength = (tagsToBeChildren != null ? tagsToBeChildren.length : 0)
                + (removedTags != null ? removedTags.length : 0) + (removedTag != null ? 1 : 0);
        if (totalLength == 0) {
            return null;
        }
        final Deque<AbstractHtml> parentLostListenerTags = new ArrayDeque<>(totalLength);
        if (tagsToBeChildren != null) {
            for (final AbstractHtml tag : tagsToBeChildren) {
                if (tag.parent != null && tag.parent != parent) {
                    parentLostListenerTags.add(tag);
                }
            }
        }
        if (removedTags != null) {
            for (final AbstractHtml tag : removedTags) {
                parentLostListenerTags.add(tag);
            }
        }
        if (removedTag != null) {
            parentLostListenerTags.add(removedTag);
        }
        return parentLostListenerTags.isEmpty() ? null : parentLostListenerTags;
    }

    // ParentLostListener ends

    // ParentGainedListener starts

    /**
     * @param parentGainedListener the ParentGainedListener
     * @return the internal slot id of the added ParentGainedListener. This id is
     *         unique only for this tag, i.e. other tag object can also return the
     *         same id.
     * @since 12.0.1
     */
    public final long addParentGainedListener(final ParentGainedListener parentGainedListener) {
        if (parentGainedListener == null) {
            throw new InvalidValueException("parentGainedListener cannot be null");
        }
        final Lock lock = lockAndGetWriteLock();
        try {

            final ParentGainedListenerVariables parentGainedListenerVariablesLocal = parentGainedListenerVariables;

            final ParentGainedListenerVariables parentGainedListenerVariables;
            if (parentGainedListenerVariablesLocal == null) {
                parentGainedListenerVariables = new ParentGainedListenerVariables();
                this.parentGainedListenerVariables = parentGainedListenerVariables;
            } else {
                parentGainedListenerVariables = parentGainedListenerVariablesLocal;
            }

            final Map<Long, ParentGainedListener> parentGainedListenersLocal = parentGainedListenerVariables.parentGainedListeners;
            final Map<Long, ParentGainedListener> parentGainedListeners;
            if (parentGainedListenersLocal == null) {
                parentGainedListeners = new ConcurrentHashMap<>();
                parentGainedListenerVariables.parentGainedListeners = parentGainedListeners;
            } else {
                parentGainedListeners = parentGainedListenersLocal;
            }

            final long parentGainedListenerSlotId = parentGainedListenerVariables.parentGainedListenerSlotIdCounter + 1;
            parentGainedListeners.put(parentGainedListenerSlotId, parentGainedListener);
            parentGainedListenerVariables.parentGainedListenerSlotIdCounter = parentGainedListenerSlotId;
            return parentGainedListenerSlotId;
        } finally {
            lock.unlock();
        }
    }

    /**
     * @param parentGainedListenerSlotId the internal slot id returned by
     *                                   {@link #addParentGainedListener(ParentGainedListener)}
     *                                   method.
     * @return true if a listener slot is removed associated with this id otherwise
     *         false.
     * @since 12.0.1
     */
    public final boolean removeParentGainedListener(final long parentGainedListenerSlotId) {

        final Lock lock = lockAndGetWriteLock();
        try {

            final ParentGainedListenerVariables parentGainedListenerVariablesLocal = parentGainedListenerVariables;

            if (parentGainedListenerVariablesLocal == null) {
                return false;
            }

            final Map<Long, ParentGainedListener> parentGainedListenersLocal = parentGainedListenerVariablesLocal.parentGainedListeners;

            if (parentGainedListenersLocal == null) {
                return false;
            }

            final ParentGainedListener parentGainedListener = parentGainedListenersLocal
                    .remove(parentGainedListenerSlotId);
            if (parentGainedListenersLocal.isEmpty()) {
                parentGainedListenerVariablesLocal.parentGainedListeners = null;
            }

            return parentGainedListener != null;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Removes all <em>ParentGainedListener</em>s from this tag.
     *
     * @since 12.0.1
     */
    public final void removeAllParentGainedListeners() {
        final Lock lock = lockAndGetWriteLock();
        try {
            final ParentGainedListenerVariables parentGainedListenerVariablesLocal = parentGainedListenerVariables;
            if (parentGainedListenerVariablesLocal != null) {
                parentGainedListenerVariablesLocal.parentGainedListeners = null;
            }
        } finally {
            lock.unlock();
        }
    }

    private void invokeParentGainedListeners() {
        final ParentGainedListenerVariables parentGainedListenerVariablesLocal = parentGainedListenerVariables;
        if (parentGainedListenerVariablesLocal != null) {
            final Map<Long, ParentGainedListener> parentGainedListenersLocal = parentGainedListenerVariablesLocal.parentGainedListeners;
            if (parentGainedListenersLocal != null) {
                final List<Map.Entry<Long, ParentGainedListener>> sortedEntries = parentGainedListenersLocal.entrySet()
                        .stream().sorted(Map.Entry.comparingByKey()).toList();
                for (final Map.Entry<Long, ParentGainedListener> entry : sortedEntries) {
                    entry.getValue().parentGained(new ParentGainedListener.Event(this));
                }
            }
        }
    }

    private void pollAndInvokeParentGainedListeners(final Deque<AbstractHtml> parentGainedListenerTags) {
        if (parentGainedListenerTags != null) {
            AbstractHtml tag;
            while ((tag = parentGainedListenerTags.poll()) != null) {
                tag.invokeParentGainedListeners();
            }
        }
    }

    private Deque<AbstractHtml> buildParentGainedListenerTags(final AbstractHtml parent,
            final AbstractHtml[] tagsToBeChildren) {
        return buildParentGainedListenerTags(parent, tagsToBeChildren, null, null);
    }

    private Deque<AbstractHtml> buildParentGainedListenerTags(final AbstractHtml[] tagsToBeChildren) {
        return buildParentGainedListenerTags(this, tagsToBeChildren, null, null);
    }

    private Deque<AbstractHtml> buildParentGainedListenerTags(final AbstractHtml[] tagsToBeChildren,
            final AbstractHtml[] addedTags) {
        return buildParentGainedListenerTags(this, tagsToBeChildren, addedTags, null);
    }

    private Deque<AbstractHtml> buildParentGainedListenerTags(final Collection<AbstractHtml> tagsToBeChildren) {
        final int totalLength = tagsToBeChildren != null ? tagsToBeChildren.size() : 0;
        if (totalLength == 0) {
            return null;
        }
        final int size = tagsToBeChildren.size();
        final Deque<AbstractHtml> parentGainedListenerTags = new ArrayDeque<>(size);
        for (final AbstractHtml tag : tagsToBeChildren) {
            if (tag.parent == null || tag.parent != this) {
                parentGainedListenerTags.add(tag);
            }
        }
        return parentGainedListenerTags.isEmpty() ? null : parentGainedListenerTags;
    }

    private Deque<AbstractHtml> buildParentGainedListenerTags(final AbstractHtml parent,
            final AbstractHtml[] tagsToBeChildren, final AbstractHtml[] addedTags, final AbstractHtml addedTag) {
        final int totalLength = (tagsToBeChildren != null ? tagsToBeChildren.length : 0)
                + (addedTags != null ? addedTags.length : 0) + (addedTag != null ? 1 : 0);
        if (totalLength == 0) {
            return null;
        }
        final Deque<AbstractHtml> parentGainedListenerTags = new ArrayDeque<>(totalLength);
        if (tagsToBeChildren != null) {
            for (final AbstractHtml tag : tagsToBeChildren) {
                if (tag.parent == null || tag.parent != this) {
                    parentGainedListenerTags.add(tag);
                }
            }
        }
        if (addedTags != null) {
            for (final AbstractHtml tag : addedTags) {
                parentGainedListenerTags.add(tag);
            }
        }
        if (addedTag != null) {
            parentGainedListenerTags.add(addedTag);
        }
        return parentGainedListenerTags.isEmpty() ? null : parentGainedListenerTags;
    }

    // ParentGainedListener ends

}
