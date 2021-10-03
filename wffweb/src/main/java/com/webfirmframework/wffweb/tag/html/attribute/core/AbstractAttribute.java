/*
 * Copyright 2014-2021 Web Firm Framework
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
package com.webfirmframework.wffweb.tag.html.attribute.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
import java.util.concurrent.locks.StampedLock;

import com.webfirmframework.wffweb.tag.core.AbstractTagBase;
import com.webfirmframework.wffweb.tag.html.AbstractHtml;
import com.webfirmframework.wffweb.tag.html.attribute.listener.AttributeValueChangeListener;
import com.webfirmframework.wffweb.tag.html.listener.PushQueue;
import com.webfirmframework.wffweb.tag.html.model.AbstractHtml5SharedObject;
import com.webfirmframework.wffweb.util.StringBuilderUtil;

public abstract class AbstractAttribute extends AbstractTagBase {

    private static final long serialVersionUID = 1_1_1L;

    private static final Security ACCESS_OBJECT;

    private String attributeName;

    // initial value must be -1 if integer type
    // null if byte[]
    private byte[] attrNameIndexBytes = null;

    private volatile String attributeValue;

    private volatile Map<String, String> attributeValueMap;

    private volatile Set<String> attributeValueSet;

    private transient final Set<AbstractHtml> ownerTags;

    private transient final StampedLock ownerTagsLock = new StampedLock();

    private final StringBuilder tagBuilder;

    // private AttributeValueChangeListener valueChangeListener;

    private volatile Set<AttributeValueChangeListener> valueChangeListeners;

    private transient Charset charset = Charset.defaultCharset();

    private final boolean nullableAttrValueMapValue;

    private volatile byte[] compressedBytes;


    /**
     * NB: do not generate equals and hashcode base on this as the deserialized
     * object can lead to bug.
     */
    private final String objectId;

    // for security purpose, the class name should not be modified
    private static final class Security implements Serializable {

        private static final long serialVersionUID = 1L;

        private Security() {
        }
    }

    /**
     *
     * @since 3.0.15
     *
     */
    private static final class OwnerTagsRecord {

        private final Set<AbstractHtml> ownerTags;

        private final Collection<AbstractHtml5SharedObject> sharedObjects;

        private OwnerTagsRecord(final Set<AbstractHtml> ownerTags,
                final Collection<AbstractHtml5SharedObject> sharedObjects) {
            this.ownerTags = ownerTags;
            this.sharedObjects = sharedObjects;
        }
    }

    /**
     *
     * @since 3.0.15
     *
     */
    private static final class TagContractRecord {

        private final AbstractHtml tag;

        private final AbstractHtml5SharedObject sharedObject;

        private TagContractRecord(final AbstractHtml tag, final AbstractHtml5SharedObject sharedObject) {
            super();
            this.tag = tag;
            this.sharedObject = sharedObject;
        }

        /**
         * @since 3.0.15 returns long value type
         * @since 3.0.19 returns string value type
         * @return objectId
         */
        private String objectId() {
            return sharedObject.objectId();
        }

        private boolean isValid() {
            return sharedObject.equals(tag.getSharedObject());
        }
    }

    static {
        ACCESS_OBJECT = new Security();
    }

    {
        tagBuilder = new StringBuilder();
        // almost all attributes may have only one owner tag
        // in a practical scenario so passed 2 here because the load factor is
        // 0.75f
        ownerTags = Collections.newSetFromMap(new WeakHashMap<AbstractHtml, Boolean>(2));

        setRebuild(true);
    }

    public AbstractAttribute() {
        nullableAttrValueMapValue = false;
        objectId = AttributeIdGenerator.nextId();
    }

    /**
     * this constructor is only for the attribute whose value is an attribute value
     * map, eg: style
     *
     * @param nullableAttrValueMapValue only
     */
    protected AbstractAttribute(final boolean nullableAttrValueMapValue) {
        this.nullableAttrValueMapValue = nullableAttrValueMapValue;
        objectId = AttributeIdGenerator.nextId();
    }

    /**
     * @return {@code String} equalent to the html string of the tag including the
     *         child tags.
     * @author WFF
     * @since 1.0.0
     */
    protected String getPrintStructure() {
        String printStructure = null;
        if (isRebuild() || isModified()) {
            printStructure = getPrintStructure(isRebuild());
            setRebuild(false);
        } else {
            printStructure = tagBuilder.toString();
        }
        return printStructure;
    }

    protected String getPrintStructure(final boolean rebuild) {
        final String attributeValue = this.attributeValue;
        String result = "";
        if (rebuild || isRebuild() || isModified()) {
            beforePrintStructure();

            if (tagBuilder.length() > 0) {
                tagBuilder.delete(0, tagBuilder.length());
            }
            // tagBuildzer.append(' ');
            tagBuilder.append(attributeName);
            if (attributeValue != null) {
                tagBuilder.append(new char[] { '=', '"' }).append(attributeValue);
                result = StringBuilderUtil.getTrimmedString(tagBuilder) + '"';
                tagBuilder.append('"');
            } else if (attributeValueMap != null && attributeValueMap.size() > 0) {
                tagBuilder.append(new char[] { '=', '"' });
                final Set<Entry<String, String>> entrySet = getAttributeValueMap().entrySet();
                for (final Entry<String, String> entry : entrySet) {
                    tagBuilder.append(entry.getKey()).append(':').append(entry.getValue()).append(';');
                }

                result = StringBuilderUtil.getTrimmedString(tagBuilder) + '"';
                tagBuilder.append('"');
            } else if (attributeValueSet != null && attributeValueSet.size() > 0) {
                tagBuilder.append(new char[] { '=', '"' });
                for (final String each : getAttributeValueSet()) {
                    tagBuilder.append(each).append(' ');
                }
                result = StringBuilderUtil.getTrimmedString(tagBuilder) + '"';
                tagBuilder.append('"');
            } else {
                result = tagBuilder.toString();
            }
            /*
             * int lastIndex = tagBuilder.length() - 1;
             *
             * should be analyzed for optimization (as the deleteCharAt method might
             * compromise performance).
             *
             * if (Character.isWhitespace(tagBuilder.charAt(lastIndex))) {
             * tagBuilder.deleteCharAt(lastIndex); }
             */
            // result = StringBuilderUtil.getTrimmedString(tagBuilder) + "\"";
            // tagBuilder.append("\"");
            setRebuild(false);
            tagBuilder.trimToSize();
        }

        return result;
    }

    /**
     * gets the attribute name and value in the format of name=value. <br>
     * Eg: style=color:green;background:blue <br>
     * This reduces 2 bytes taken for ".
     *
     * @return the attribute name and value in the format of name=value. Eg:
     *         style=color:green;background:blue
     * @author WFF
     * @since 2.0.0
     */
    protected String getWffPrintStructure() {
        final String attributeValue = this.attributeValue;
        String result = "";
        beforeWffPrintStructure();
        final StringBuilder attrBuilder = new StringBuilder();

        attrBuilder.append(attributeName);
        if (attributeValue != null) {
            attrBuilder.append(new char[] { '=' }).append(attributeValue);
            result = StringBuilderUtil.getTrimmedString(attrBuilder);
        } else {
            final Map<String, String> attributeValueMap = this.attributeValueMap;
            if (attributeValueMap != null && attributeValueMap.size() > 0) {
                attrBuilder.append(new char[] { '=' });
                final Set<Entry<String, String>> entrySet = getAttributeValueMap().entrySet();
                for (final Entry<String, String> entry : entrySet) {
                    attrBuilder.append(entry.getKey()).append(':').append(entry.getValue()).append(';');
                }

                result = StringBuilderUtil.getTrimmedString(attrBuilder);
            } else {
                final Set<String> attributeValueSet = this.attributeValueSet;
                if (attributeValueSet != null && attributeValueSet.size() > 0) {
                    attrBuilder.append(new char[] { '=' });
                    for (final String each : getAttributeValueSet()) {
                        attrBuilder.append(each).append(' ');
                    }
                    result = StringBuilderUtil.getTrimmedString(attrBuilder);
                } else {
                    result = attrBuilder.toString();
                }
            }
        }

        return result;
    }

    /**
     * gets the attribute name and value in the format of name=value. <br>
     * Eg: style=color:green;background:blue <br>
     * This reduces 2 bytes taken for ".
     *
     * @return the attribute name and value in the format of name=value. Eg:
     *         style=color:green;background:blue;
     * @author WFF
     * @since 2.0.0
     */
    public String toWffString() {
        return getWffPrintStructure();
    }

    protected void beforeWffPrintStructure() {
        // NOT override and use if required
    }

    /**
     * gives compressed by index bytes for the attribute and value. The first byte
     * represents the attribute name index bytes length, the next bytes represent
     * the attribute name index bytes and the remaining bytes represent attribute
     * value without <i>=</i> and <i>"</i>.
     *
     * @param rebuild
     * @return the compressed by index bytes.
     * @throws IOException
     * @author WFF
     * @since 1.1.3
     */
    protected byte[] getBinaryStructureCompressedByIndex(final boolean rebuild) throws IOException {
        return getBinaryStructureCompressedByIndex(rebuild, charset);
    }

    /**
     * gives compressed by index bytes for the attribute and value. The first byte
     * represents the attribute name index bytes length, the next bytes represent
     * the attribute name index bytes and the remaining bytes represent attribute
     * value without <i>=</i> and <i>"</i>.
     *
     * @param rebuild
     * @param charset
     * @return the compressed by index bytes.
     * @throws IOException
     * @author WFF
     * @since 3.0.3
     */
    protected byte[] getBinaryStructureCompressedByIndex(final boolean rebuild, final Charset charset)
            throws IOException {

        final String attributeValue = this.attributeValue;
        final String attributeName = this.attributeName;
        byte[] compressedBytes = this.compressedBytes;

        // String result = "";
        if (rebuild || compressedBytes == null) {

            compressedBytes = new byte[0];
            final ByteArrayOutputStream compressedBytesBuilder = new ByteArrayOutputStream();

            beforePrintStructureCompressedByIndex();
            // tagBuildzer.append(' ');

            // final int attributeNameIndex =
            // AttributeRegistry.getAttributeNames()
            // .indexOf(attributeName);

            // should always use local variable attrNameIndex
            // this.attrNameIndex is eventually consistent
            final byte[] attrNameIndexBytes = this.attrNameIndexBytes;

            if (attrNameIndexBytes == null) {

                compressedBytesBuilder.write(new byte[] { (byte) 0 });

                compressedBytesBuilder.write(attributeName.getBytes(charset));

                // logging is not required here
                // as it is not an unusual case
                // if (LOGGER.isLoggable(Level.WARNING)) {
                // LOGGER.warning(attributeName
                // + " is not indexed, please register it with
                // AttributeRegistry");
                // }
            } else {

                compressedBytesBuilder.write(new byte[] { (byte) attrNameIndexBytes.length });
                compressedBytesBuilder.write(attrNameIndexBytes);
            }

            if (attributeValue != null) {
                if (attrNameIndexBytes == null) {
                    compressedBytesBuilder.write("=".getBytes(charset));
                }

                compressedBytesBuilder.write(attributeValue.getBytes(charset));
                compressedBytes = compressedBytesBuilder.toByteArray();
            } else if (attributeValueMap != null && attributeValueMap.size() > 0) {

                if (attrNameIndexBytes == null) {
                    compressedBytesBuilder.write("=".getBytes(charset));
                }

                final Set<Entry<String, String>> entrySet = getAttributeValueMap().entrySet();
                for (final Entry<String, String> entry : entrySet) {

                    compressedBytesBuilder.write(entry.getKey().getBytes(charset));
                    compressedBytesBuilder.write(new byte[] { ':' });
                    compressedBytesBuilder.write(entry.getValue().getBytes(charset));
                    compressedBytesBuilder.write(new byte[] { ';' });
                }
                compressedBytes = compressedBytesBuilder.toByteArray();
            } else if (attributeValueSet != null && attributeValueSet.size() > 0) {

                if (attrNameIndexBytes == null) {
                    compressedBytesBuilder.write("=".getBytes(charset));
                }

                for (final String each : getAttributeValueSet()) {

                    compressedBytesBuilder.write(each.getBytes(charset));
                    compressedBytesBuilder.write(new byte[] { ' ' });
                }
                compressedBytes = compressedBytesBuilder.toByteArray();
            } else {
                compressedBytes = compressedBytesBuilder.toByteArray();
            }

        } else {
            return compressedBytes;
        }
        this.compressedBytes = compressedBytes;
        return compressedBytes;
    }

    /**
     * @return the attributeName set by
     *         {@code AbstractAttribute#setAttributeName(String)}
     * @author WFF
     * @since 1.0.0
     */
    public String getAttributeName() {
        return attributeName;
    }

    /**
     * Set attribute name, eg: width, height, name, type etc..
     *
     * @param attributeName the attributeName to set
     * @author WFF
     * @since 1.0.0
     */
    protected void setAttributeName(final String attributeName) {
        this.attributeName = attributeName;
    }

    /**
     * NB: only for internal use. Signature of this method may be modified in future
     * version. Sets PreIndexedAttributeName for name and index
     *
     * @param preIndexedAttrName PreIndexedAttributeName object
     * @author WFF
     * @since 3.0.3
     */
    protected void setPreIndexedAttribute(final PreIndexedAttributeName preIndexedAttrName) {
        attributeName = preIndexedAttrName.attrName();
        attrNameIndexBytes = preIndexedAttrName.internalIndexBytes();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.webfirmframework.wffweb.tag.Base#toHtmlString()
     *
     * @since 1.0.0
     *
     * @author WFF
     */
    @Override
    public String toHtmlString() {
        return getPrintStructure(true);
    }

    public byte[] toCompressedBytesByIndex(final boolean rebuild) throws IOException {
        return getBinaryStructureCompressedByIndex(rebuild);
    }

    public byte[] toCompressedBytesByIndex(final boolean rebuild, final Charset charset) throws IOException {
        return getBinaryStructureCompressedByIndex(rebuild, charset);
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
            return toHtmlString();
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
            return toHtmlString();
        } finally {
            this.charset = previousCharset;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.webfirmframework.wffweb.tag.Base#toHtmlString(boolean)
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
            return toHtmlString(rebuild);
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
            return toHtmlString(rebuild);
        } finally {
            this.charset = previousCharset;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getPrintStructure();
    }

    /**
     * @return the attributeValueMap
     * @author WFF
     * @since 1.0.0
     */
    protected Map<String, String> getAttributeValueMap() {
        if (attributeValueMap == null) {
            synchronized (this) {
                if (attributeValueMap == null) {
                    setAttributeValueMap(new LinkedHashMap<String, String>());
                }
            }
        }
        return attributeValueMap;
    }

    /**
     * @param attributeValueMap the attributeValueMap to set
     * @author WFF
     * @since 1.0.0
     */
    protected void setAttributeValueMap(final Map<String, String> attributeValueMap) {

        if (!Objects.equals(attributeValueMap, this.attributeValueMap)) {
            this.attributeValueMap = attributeValueMap;
            setModifiedLockless(true);
        }

    }

    /**
     * adds the given key value.
     *
     * @param key
     * @param value
     * @return true if it is modified
     * @author WFF
     * @since 1.0.0
     */
    protected boolean addToAttributeValueMap(final String key, final String value) {

        boolean listenerInvoked = false;

        final Collection<Lock> writeLocks = lockAndGetWriteLocksWithAttrLock();

        // should be after lockAndGetWriteLocks
        final OwnerTagsRecord ownerTagsRecord = getOwnerTagsRecord();

        try {
            final Map<String, String> attributeValueMap = getAttributeValueMap();

            final String previousValue = attributeValueMap.put(key, value);

            if (!Objects.equals(previousValue, value)) {
                setModifiedLockless(true);
                invokeValueChangeListeners(ownerTagsRecord);
                listenerInvoked = true;
            }

        } finally {
            for (final Lock lock : writeLocks) {
                lock.unlock();
            }

        }

        pushQueues(ownerTagsRecord.sharedObjects, listenerInvoked);

        return listenerInvoked;
    }

    /**
     * this method should be called after changing of attribute value not before
     * changing value
     */
    private void invokeValueChangeListeners(final OwnerTagsRecord ownerTagsRecord) {

        final Collection<AbstractHtml5SharedObject> sharedObjects = ownerTagsRecord.sharedObjects;

        final Set<AbstractHtml> ownerTags = ownerTagsRecord.ownerTags;

        for (final AbstractHtml5SharedObject sharedObject : sharedObjects) {
            final AttributeValueChangeListener valueChangeListener = sharedObject.getValueChangeListener(ACCESS_OBJECT);
            if (valueChangeListener != null) {

                // ownerTags should not be modified in the consuming
                // part, here
                // skipped it making unmodifiable to gain
                // performance
                final AttributeValueChangeListener.Event event = new AttributeValueChangeListener.Event(this,
                        ownerTags);
                valueChangeListener.valueChanged(event);
            }
        }

        if (valueChangeListeners != null) {
            for (final AttributeValueChangeListener listener : valueChangeListeners) {
                final AttributeValueChangeListener.Event event = new AttributeValueChangeListener.Event(this,
                        ownerTags);
                listener.valueChanged(event);
            }
        }

    }

    @SuppressWarnings("unused")
    private Collection<AbstractHtml5SharedObject> getSharedObjects() {

        final long stamp = ownerTagsLock.readLock();

        try {
            // internally ownerTags.size() (WeakHashMap) contains synchronization so
            // better avoid calling it
            // normally there will be one sharedObject so the capacity may be
            // considered as 1
            final Collection<AbstractHtml5SharedObject> sharedObjects = new HashSet<>(1);

            for (final AbstractHtml ownerTag : ownerTags) {
                sharedObjects.add(ownerTag.getSharedObject());
            }
            return sharedObjects;
        } finally {
            ownerTagsLock.unlockRead(stamp);
        }
    }

    /**
     * @return OwnerTagsRecord object
     * @since 3.0.15
     */
    private OwnerTagsRecord getOwnerTagsRecord() {
        // internally ownerTags.size() (WeakHashMap) contains synchronization so
        // better avoid calling it
        // normally there will be one sharedObject so the capacity may be
        // considered as 1
        final Collection<AbstractHtml5SharedObject> sharedObjects = new LinkedHashSet<>(1);

        final Set<AbstractHtml> ownerTags = Collections.unmodifiableSet(this.ownerTags);
        for (final AbstractHtml ownerTag : ownerTags) {
            sharedObjects.add(ownerTag.getSharedObject());
        }
        return new OwnerTagsRecord(ownerTags, sharedObjects);
    }

    /**
     * adds all to the attribute value map.
     *
     * @param map
     * @return true if it is modified
     * @author WFF
     * @since 1.0.0
     */
    protected boolean addAllToAttributeValueMap(final Map<String, String> map) {

        if (map != null && map.size() > 0) {

            boolean listenerInvoked = false;

            final Collection<Lock> writeLocks = lockAndGetWriteLocksWithAttrLock();

            // should be after lockAndGetWriteLocks
            final OwnerTagsRecord ownerTagsRecord = getOwnerTagsRecord();
            try {

                getAttributeValueMap().putAll(map);

                setModifiedLockless(true);

                invokeValueChangeListeners(ownerTagsRecord);
                listenerInvoked = true;

            } finally {
                for (final Lock lock : writeLocks) {
                    lock.unlock();
                }

            }
            pushQueues(ownerTagsRecord.sharedObjects, listenerInvoked);
            return listenerInvoked;

        }
        return false;
    }

    /**
     * removes the key value for the input key.
     *
     * @param key
     * @return true if the given key (as well as value contained corresponding to
     *         it) has been removed.
     * @author WFF
     * @since 1.0.0
     */
    protected boolean removeFromAttributeValueMap(final String key) {
        return removeFromAttributeValueMapByKeys(key);
    }

    /**
     * removes the key value for the input key.
     *
     * @param keys
     * @return true if any of the given keys (as well as value contained
     *         corresponding to it) has been removed.
     * @author WFF
     * @since 3.0.1
     */
    protected boolean removeFromAttributeValueMapByKeys(final String... keys) {

        boolean listenerInvoked = false;

        final Collection<Lock> writeLocks = lockAndGetWriteLocksWithAttrLock();

        // should be after lockAndGetWriteLocks
        final OwnerTagsRecord ownerTagsRecord = getOwnerTagsRecord();

        boolean result = false;
        try {

            final Map<String, String> valueMap = getAttributeValueMap();

            if (nullableAttrValueMapValue) {
                for (final String key : keys) {
                    result = valueMap.containsKey(key);
                    if (result) {
                        break;
                    }
                }
            }

            for (final String key : keys) {
                final String previous = valueMap.remove(key);
                if (previous != null) {
                    result = true;
                }
            }

            if (result) {
                setModifiedLockless(true);
                invokeValueChangeListeners(ownerTagsRecord);
                listenerInvoked = true;
            }

        } finally {
            for (final Lock lock : writeLocks) {
                lock.unlock();
            }

        }
        pushQueues(ownerTagsRecord.sharedObjects, listenerInvoked);
        return result;
    }

    /**
     * pushes PushQueue from all shared object of parent tags.
     *
     * @param sharedObjects
     * @param listenerInvoked
     */
    private void pushQueues(final Collection<AbstractHtml5SharedObject> sharedObjects, final boolean listenerInvoked) {
        if (listenerInvoked) {
            for (final AbstractHtml5SharedObject sharedObject : sharedObjects) {
                final PushQueue pushQueue = sharedObject.getPushQueue(ACCESS_OBJECT);
                if (pushQueue != null) {
                    pushQueue.push();
                }
            }
        }
    }

    /**
     * removes only if the key and value matches in the map for any particular
     * entry.
     *
     * @param key
     * @param value
     * @return true if it is modified
     * @author WFF
     * @since 1.0.0
     */
    protected boolean removeFromAttributeValueMap(final String key, final String value) {

        boolean listenerInvoked = false;

        final Collection<Lock> writeLocks = lockAndGetWriteLocksWithAttrLock();

        // should be after lockAndGetWriteLocks
        final OwnerTagsRecord ownerTagsRecord = getOwnerTagsRecord();
        try {

            final boolean removed = getAttributeValueMap().remove(key, value);

            if (removed) {
                setModifiedLockless(true);
                invokeValueChangeListeners(ownerTagsRecord);
                listenerInvoked = true;
            }

        } finally {
            for (final Lock lock : writeLocks) {
                lock.unlock();
            }

        }
        pushQueues(ownerTagsRecord.sharedObjects, listenerInvoked);
        return listenerInvoked;
    }

    protected void removeAllFromAttributeValueMap() {
        if (attributeValueMap != null && getAttributeValueMap().size() > 0) {

            boolean listenerInvoked = false;

            final Collection<Lock> writeLocks = lockAndGetWriteLocksWithAttrLock();

            // should be after lockAndGetWriteLocks
            final OwnerTagsRecord ownerTagsRecord = getOwnerTagsRecord();
            try {
                getAttributeValueMap().clear();
                setModifiedLockless(true);

                invokeValueChangeListeners(ownerTagsRecord);
                listenerInvoked = true;
            } finally {
                for (final Lock lock : writeLocks) {
                    lock.unlock();
                }
            }
            pushQueues(ownerTagsRecord.sharedObjects, listenerInvoked);
        }
    }

    /**
     * @return the attributeValue
     * @author WFF
     * @since 1.0.0
     */
    public String getAttributeValue() {
        // NB:- Do not call this method for internal operations
        // use attributeValue variable instead of it
        // because it may be overridden by tags, for eg: ClassAttribute
        return attributeValue;
    }

    /**
     * @param attributeValue the attributeValue to set
     * @author WFF
     * @since 1.0.0
     */
    protected void setAttributeValue(final String attributeValue) {
        if (!Objects.equals(this.attributeValue, attributeValue)) {
            boolean listenerInvoked = false;

            final Collection<Lock> writeLocks = lockAndGetWriteLocksWithAttrLock();

            // should be after lockAndGetWriteLocks
            final OwnerTagsRecord ownerTagsRecord = getOwnerTagsRecord();

            try {

                // this.attributeValue = attributeValue must be
                // before invokeValueChangeListeners
                this.attributeValue = attributeValue;
                setModifiedLockless(true);
                invokeValueChangeListeners(ownerTagsRecord);
                listenerInvoked = true;
            } finally {
                for (final Lock lock : writeLocks) {
                    lock.unlock();
                }

            }

            pushQueues(ownerTagsRecord.sharedObjects, listenerInvoked);
        }
    }

    /**
     * @param updateClient   true to update client browser page if it is available.
     *                       The default value is true but it will be ignored if
     *                       there is no client browser page.
     * @param attributeValue the attributeValue to set
     * @author WFF
     * @since 2.1.15
     */
    protected void setAttributeValue(final boolean updateClient, final String attributeValue) {

        if (!Objects.equals(this.attributeValue, attributeValue)) {
            boolean listenerInvoked = false;

            final Collection<Lock> writeLocks = lockAndGetWriteLocksWithAttrLock();

            // should be after lockAndGetWriteLocks
            final OwnerTagsRecord ownerTagsRecord = getOwnerTagsRecord();

            try {

                // this.attributeValue = attributeValue must be
                // before invokeValueChangeListeners
                this.attributeValue = attributeValue;
                setModifiedLockless(true);
                if (updateClient) {
                    invokeValueChangeListeners(ownerTagsRecord);
                    listenerInvoked = true;
                }
            } finally {
                for (final Lock lock : writeLocks) {
                    lock.unlock();
                }

            }
            pushQueues(ownerTagsRecord.sharedObjects, listenerInvoked);
        }
    }

    /**
     * @return one of the ownerTags
     * @author WFF
     * @since 1.0.0
     * @deprecated this method may be removed later as there could be multiple owner
     *             tags.
     */
    @Deprecated
    public AbstractHtml getOwnerTag() {

        final long stamp = ownerTagsLock.readLock();
        try {
            final AbstractHtml tag;
            if (ownerTags.iterator().hasNext()) {
                tag = ownerTags.iterator().next();
            } else {
                tag = null;
            }
            return tag;
        } finally {
            ownerTagsLock.unlockRead(stamp);
        }
    }

    /**
     * Please know that the AbstractAttribute class doesn't prevent its consumer
     * tags to be garbage collected. So, this is a weak method. i.e. if the consumer
     * tags are garbage collected they will not be included in the array.
     *
     * @return the tags which are consuming this attribute as an array. If there is
     *         no owner tag then it will return an empty array instead of null.
     * @author WFF
     * @since 2.0.0
     */
    public AbstractHtml[] getOwnerTags() {

        final long stamp = ownerTagsLock.readLock();

        // returning the set is not good because
        // if the AbstractHtml needs to be
        // modified while iterating the set will cause
        // ConcurrentModificationException
        final AbstractHtml[] result;
        try {
            result = ownerTags.toArray(new AbstractHtml[0]);
        } finally {
            ownerTagsLock.unlockRead(stamp);
        }

        return result;
    }

    /**
     * NB:- this method is used for internal purpose, so it should not be consumed.
     *
     * @param ownerTag the ownerTag to set
     * @author WFF
     * @since 1.0.0
     */
    public void setOwnerTag(final AbstractHtml ownerTag) {

        final long stamp = ownerTagsLock.writeLock();
        try {
            ownerTags.add(ownerTag);
        } finally {
            ownerTagsLock.unlockWrite(stamp);
        }
    }

    /**
     * NB:- this method is used for internal purpose, so it should not be consumed.
     *
     * @param ownerTag the ownerTag to set
     * @author WFF
     * @since 3.0.15
     */
    final void setOwnerTagLockless(final AbstractHtml ownerTag) {
        ownerTags.add(ownerTag);
    }

    /**
     * @return the readLock
     * @since 3.0.15
     */
    final Lock getObjectReadLock() {
        return ownerTagsLock.asReadLock();
    }

    /**
     * @return the writeLock
     * @since 3.0.15
     */
    final Lock getObjectWriteLock() {
        return ownerTagsLock.asWriteLock();
    }

    /**
     * NB:- this method is used for internal purpose, so it should not be consumed.
     *
     * @param ownerTag the ownerTag to unset
     * @return true if the given ownerTag is an owner of the attribute.
     * @author WFF
     * @since 2.0.0
     */
    public boolean unsetOwnerTag(final AbstractHtml ownerTag) {
        final long stamp = ownerTagsLock.writeLock();
        final boolean result;
        try {
            result = ownerTags.remove(ownerTag);
        } finally {
            ownerTagsLock.unlockWrite(stamp);
        }
        return result;
    }

    /**
     * NB:- this method is used for internal purpose, so it should not be consumed.
     *
     * @param ownerTag the ownerTag to unset
     * @return true if the given ownerTag is an owner of the attribute.
     * @author WFF
     * @since 3.0.15
     */
    final boolean unsetOwnerTagLockless(final AbstractHtml ownerTag) {
        return ownerTags.remove(ownerTag);
    }

    @Override
    public void setModified(final boolean modified) {
        final long stamp = ownerTagsLock.writeLock();
        try {
            setModifiedLockless(modified);
        } finally {
            ownerTagsLock.unlockWrite(stamp);
        }
    }

    /**
     * @param modified
     * @since 3.0.15
     */
    protected final void setModifiedLockless(final boolean modified) {
        super.setModified(modified);
        if (modified) {
            compressedBytes = null;
            for (final AbstractHtml ownerTag : ownerTags) {
                ownerTag.setModified(modified);
                ownerTag.getSharedObject().setChildModified(modified);
            }
        }
    }

    /**
     * NB:- this is only for getting values. Use addToAttributeValueSet method for
     * adding
     *
     * @return the attributeValueSet
     * @author WFF
     * @since 1.0.0
     */
    protected Set<String> getAttributeValueSet() {
        if (attributeValueSet == null) {
            synchronized (this) {
                if (attributeValueSet == null) {
                    // because the load factor is 0.75f
                    attributeValueSet = new LinkedHashSet<>(2);
                }
            }
        }
        return attributeValueSet;
    }

    /**
     * @param attributeValueSet the attributeValueSet to set
     * @author WFF
     * @since 1.0.0
     */
    protected void setAttributeValueSet(final Set<String> attributeValueSet) {
        if (!Objects.equals(this.attributeValueSet, attributeValueSet)) {
            final long stamp = ownerTagsLock.writeLock();
            try {
                this.attributeValueSet = attributeValueSet;
                setModifiedLockless(true);
            } finally {
                ownerTagsLock.unlockWrite(stamp);
            }
        }
    }

    /**
     * adds to the attribute value set.
     *
     * @param value
     * @return
     * @author WFF
     * @since 1.0.0
     */
    protected boolean addToAttributeValueSet(final String value) {
        boolean listenerInvoked = false;
        final Collection<Lock> writeLocks = lockAndGetWriteLocksWithAttrLock();

        // should be after lockAndGetWriteLocks
        final OwnerTagsRecord ownerTagsRecord = getOwnerTagsRecord();
        try {

            final boolean added = getAttributeValueSet().add(value);

            if (added) {
                setModifiedLockless(true);

                invokeValueChangeListeners(ownerTagsRecord);
                listenerInvoked = true;

            }

        } finally {
            for (final Lock lock : writeLocks) {
                lock.unlock();
            }

        }
        pushQueues(ownerTagsRecord.sharedObjects, listenerInvoked);
        return listenerInvoked;
    }

    /**
     * adds all to the attribute value set.
     *
     * @param values
     * @author WFF
     * @since 1.0.0
     */
    protected void addAllToAttributeValueSet(final Collection<String> values) {
        if (values != null) {
            boolean listenerInvoked = false;

            final Collection<Lock> writeLocks = lockAndGetWriteLocksWithAttrLock();

            // should be after lockAndGetWriteLocks
            final OwnerTagsRecord ownerTagsRecord = getOwnerTagsRecord();
            try {

                final boolean added = getAttributeValueSet().addAll(values);

                if (added) {
                    setModifiedLockless(true);

                    invokeValueChangeListeners(ownerTagsRecord);
                    listenerInvoked = true;
                }

            } finally {
                for (final Lock lock : writeLocks) {
                    lock.unlock();
                }

            }
            pushQueues(ownerTagsRecord.sharedObjects, listenerInvoked);
        }
    }

    /**
     * removes all and then adds all to the attribute value set.
     *
     * @param values
     * @author WFF
     * @since 3.0.1
     */
    protected void replaceAllInAttributeValueSet(final Collection<String> values) {
        replaceAllInAttributeValueSet(true, values);
    }

    /**
     * removes all and then adds all to the attribute value set.
     *
     * @param updateClient true to update client browser page
     * @param values
     * @author WFF
     * @since 3.0.1
     */
    protected void replaceAllInAttributeValueSet(final boolean updateClient, final Collection<String> values) {
        if (values != null) {

            boolean listenerInvoked = false;

            final Collection<Lock> writeLocks = lockAndGetWriteLocksWithAttrLock();

            // should be after lockAndGetWriteLocks
            final OwnerTagsRecord ownerTagsRecord = getOwnerTagsRecord();
            try {

                final Set<String> attrValueSet = getAttributeValueSet();
                attrValueSet.clear();
                final boolean added = attrValueSet.addAll(values);

                if (added) {
                    setModifiedLockless(true);

                    if (updateClient) {
                        invokeValueChangeListeners(ownerTagsRecord);
                        listenerInvoked = true;
                    }
                }

            } finally {
                for (final Lock lock : writeLocks) {
                    lock.unlock();
                }

            }
            pushQueues(ownerTagsRecord.sharedObjects, listenerInvoked);
        }
    }

    /**
     * adds all to the attribute value set.
     *
     * @param updateClient true to update client browser page if it is available.
     *                     The default value is true but it will be ignored if there
     *                     is no client browser page.
     * @param values
     * @author WFF
     * @since 2.1.15
     */
    protected void addAllToAttributeValueSet(final boolean updateClient, final Collection<String> values) {
        if (values != null) {

            boolean listenerInvoked = false;
            final Collection<Lock> writeLocks = lockAndGetWriteLocksWithAttrLock();

            // should be after lockAndGetWriteLocks
            final OwnerTagsRecord ownerTagsRecord = getOwnerTagsRecord();
            try {

                final boolean added = getAttributeValueSet().addAll(values);

                if (added) {
                    setModifiedLockless(true);

                    if (updateClient) {
                        invokeValueChangeListeners(ownerTagsRecord);
                        listenerInvoked = true;
                    }
                }
            } finally {
                for (final Lock lock : writeLocks) {
                    lock.unlock();
                }

            }
            pushQueues(ownerTagsRecord.sharedObjects, listenerInvoked);
        }
    }

    /**
     * removes the value from the the attribute set.
     *
     * @param value
     * @author WFF
     * @since 1.0.0
     */
    protected void removeFromAttributeValueSet(final String value) {
        boolean listenerInvoked = false;

        final Collection<Lock> writeLocks = lockAndGetWriteLocksWithAttrLock();

        // should be after lockAndGetWriteLocks
        final OwnerTagsRecord ownerTagsRecord = getOwnerTagsRecord();
        try {

            final boolean removed = getAttributeValueSet().remove(value);

            if (removed) {

                setModifiedLockless(true);

                invokeValueChangeListeners(ownerTagsRecord);
                listenerInvoked = true;
            }
        } finally {
            for (final Lock lock : writeLocks) {
                lock.unlock();
            }

        }
        pushQueues(ownerTagsRecord.sharedObjects, listenerInvoked);
    }

    /**
     * removes the value from the the attribute set.
     *
     * @param values
     * @author WFF
     * @since 1.0.0
     */
    protected void removeAllFromAttributeValueSet(final Collection<String> values) {

        boolean listenerInvoked = false;

        final Collection<Lock> writeLocks = lockAndGetWriteLocksWithAttrLock();

        // should be after lockAndGetWriteLocks
        final OwnerTagsRecord ownerTagsRecord = getOwnerTagsRecord();
        try {

            final boolean removedAll = getAttributeValueSet().removeAll(values);

            if (removedAll) {
                setModifiedLockless(true);

                invokeValueChangeListeners(ownerTagsRecord);
                listenerInvoked = true;
            }

        } finally {
            for (final Lock lock : writeLocks) {
                lock.unlock();
            }

        }
        pushQueues(ownerTagsRecord.sharedObjects, listenerInvoked);
    }

    /**
     * clears all values from the value set.
     *
     * @author WFF
     * @since 1.0.0
     */
    protected void removeAllFromAttributeValueSet() {

        boolean listenerInvoked = false;

        final Collection<Lock> writeLocks = lockAndGetWriteLocksWithAttrLock();

        // should be after lockAndGetWriteLocks
        final OwnerTagsRecord ownerTagsRecord = getOwnerTagsRecord();
        try {
            getAttributeValueSet().clear();
            setModifiedLockless(true);

            invokeValueChangeListeners(ownerTagsRecord);
            listenerInvoked = true;
        } finally {
            for (final Lock lock : writeLocks) {
                lock.unlock();
            }

        }
        pushQueues(ownerTagsRecord.sharedObjects, listenerInvoked);
    }

    /**
     * invokes just before {@code getPrintStructure(final boolean} method and only
     * if the getPrintStructure(final boolean} rebuilds the structure.
     *
     * @author WFF
     * @since 1.0.0
     */
    protected void beforePrintStructure() {
        // TODO override and use
    }

    /**
     * invokes just before {@code getPrintStructureCompressedByIndex(final boolean}
     * method and only if the getPrintStructureCompressedByIndex(final boolean}
     * rebuilds the structure.
     *
     * @author WFF
     * @since 1.0.0
     */
    protected void beforePrintStructureCompressedByIndex() {
        // TODO override and use
    }

    /**
     * @return the charset
     */
    public Charset getCharset() {
        return charset;
    }

    /**
     * @param charset the charset to set
     */
    public void setCharset(final Charset charset) {
        this.charset = charset;
    }

    /**
     * adds value change lister which will be invoked when the value changed
     *
     * @param valueChangeListener
     * @author WFF
     * @since 2.0.0
     */
    public void addValueChangeListener(final AttributeValueChangeListener valueChangeListener) {
        Set<AttributeValueChangeListener> valueChangeListeners = this.valueChangeListeners;
        if (valueChangeListeners == null) {
            synchronized (this) {
                valueChangeListeners = this.valueChangeListeners;
                if (valueChangeListeners == null) {
                    valueChangeListeners = new LinkedHashSet<>();
                    this.valueChangeListeners = valueChangeListeners;
                }
            }
        }

        valueChangeListeners.add(valueChangeListener);
    }

    /**
     * removes the corresponding value change listener
     *
     * @param valueChangeListener
     * @author WFF
     * @since 2.0.0
     */
    public void removeValueChangeListener(final AttributeValueChangeListener valueChangeListener) {
        final Set<AttributeValueChangeListener> valueChangeListeners = this.valueChangeListeners;
        if (valueChangeListeners != null) {
            valueChangeListeners.remove(valueChangeListener);
        }
    }

    /**
     * @return the set of value change listeners
     * @author WFF
     * @since 2.0.0
     */
    public Set<AttributeValueChangeListener> getValueChangeListeners() {
        return Collections.unmodifiableSet(valueChangeListeners);
    }

    /**
     * NB: this may not return the same locks as the ownerTags of this attribute may
     * be changed at any time. Use it for only unlock. This method also locks this
     * attribute as well. NB: this attribute lock is not reentrant.
     *
     * @return the set of write locks after locking
     * @since 3.0.15
     */
    protected final Collection<Lock> lockAndGetWriteLocksWithAttrLock() {

        final Lock ownerTagsWriteLock = ownerTagsLock.asWriteLock();
        // lock must be called before using ownerTags
        ownerTagsWriteLock.lock();

        // ownerTag state before lock
        List<TagContractRecord> tagContractRecords;
        boolean ownerTagModified = false;
        List<Lock> writeLocks = null;

        int capacity = ownerTags.size();

        do {
            if (ownerTagModified) {
                for (final Lock lock : writeLocks) {
                    lock.unlock();
                }
            }

            tagContractRecords = new ArrayList<>(capacity);

            // internally this.ownerTags.size() (WeakHashMap) contains synchronization so
            // better avoid calling it
            // normally there will be one sharedObject so the capacity may be
            // considered as 1

            for (final AbstractHtml ownerTag : ownerTags) {
                tagContractRecords.add(new TagContractRecord(ownerTag, ownerTag.getSharedObject()));
            }

            tagContractRecords.sort(Comparator.comparing(TagContractRecord::objectId));
            capacity = tagContractRecords.size();

            writeLocks = new ArrayList<>(tagContractRecords.size() + 1);
            ownerTagModified = false;
            for (final TagContractRecord tagContractRecord : tagContractRecords) {
                if (!tagContractRecord.isValid()) {
                    ownerTagModified = true;
                    break;
                }

                final WriteLock lock = tagContractRecord.sharedObject.getLock(ACCESS_OBJECT).writeLock();
                lock.lock();
                writeLocks.add(lock);

                if (!tagContractRecord.isValid()) {
                    ownerTagModified = true;
                    break;
                }
            }

            // NB: must reverse it before returning because its unlocking must be in the
            // reverse order
            if (writeLocks.size() > 1) {
                Collections.reverse(writeLocks);
            }

        } while (ownerTagModified);

        writeLocks.add(ownerTagsWriteLock);

        return writeLocks;
    }

    /**
     * NB: this may not return the same locks as the ownerTags of this attribute may
     * be changed at any time. Use it for only unlock.
     *
     * @return the set of write locks after locking
     */
    protected Collection<WriteLock> lockAndGetWriteLocks() {

        final long stamp = ownerTagsLock.readLock();

        try {

            // ownerTag state before lock
            List<TagContractRecord> tagContractRecords;
            boolean ownerTagModified = false;
            List<WriteLock> writeLocks = null;

            int capacity = ownerTags.size();

            do {
                if (ownerTagModified) {
                    for (final Lock lock : writeLocks) {
                        lock.unlock();
                    }
                }

                tagContractRecords = new ArrayList<>(capacity);

                // internally this.ownerTags.size() (WeakHashMap) contains synchronization so
                // better avoid calling it
                // normally there will be one sharedObject so the capacity may be
                // considered as 1
                for (final AbstractHtml ownerTag : ownerTags) {
                    tagContractRecords.add(new TagContractRecord(ownerTag, ownerTag.getSharedObject()));
                }

                tagContractRecords.sort(Comparator.comparing(TagContractRecord::objectId));
                capacity = tagContractRecords.size();

                writeLocks = new ArrayList<>(tagContractRecords.size());
                ownerTagModified = false;
                for (final TagContractRecord tagContractRecord : tagContractRecords) {
                    if (!tagContractRecord.isValid()) {
                        ownerTagModified = true;
                        break;
                    }

                    final WriteLock lock = tagContractRecord.sharedObject.getLock(ACCESS_OBJECT).writeLock();
                    lock.lock();
                    writeLocks.add(lock);

                    if (!tagContractRecord.isValid()) {
                        ownerTagModified = true;
                        break;
                    }
                }

                // NB: must reverse it before returning because its unlocking must be in the
                // reverse order
                if (writeLocks.size() > 1) {
                    Collections.reverse(writeLocks);
                }

            } while (ownerTagModified);

            return writeLocks;
        } finally {
            ownerTagsLock.unlockRead(stamp);
        }
    }

    /**
     * NB: this may not return the same locks as the ownerTags of this attribute may
     * be changed at any time. Use it for only unlock. This method also locks this
     * attribute as well. NB: this attribute lock is not reentrant.
     *
     * @return the set of read locks after locking
     * @since 3.0.15
     */
    protected final Collection<Lock> lockAndGetReadLocksWithAttrLock() {

        final Lock ownerTagsReadLock = ownerTagsLock.asReadLock();
        // lock must be called before using ownerTags
        ownerTagsReadLock.lock();

        // ownerTag state before lock
        List<TagContractRecord> tagContractRecords;
        boolean ownerTagModified = false;
        List<Lock> readLocks = null;
        int capacity = ownerTags.size();
        do {
            if (ownerTagModified) {
                for (final Lock lock : readLocks) {
                    lock.unlock();
                }
            }

            tagContractRecords = new ArrayList<>(capacity);

            // internally this.ownerTags.size() (WeakHashMap) contains synchronization so
            // better avoid calling it
            // normally there will be one sharedObject so the capacity may be
            // considered as 1

            for (final AbstractHtml ownerTag : ownerTags) {
                tagContractRecords.add(new TagContractRecord(ownerTag, ownerTag.getSharedObject()));
            }

            tagContractRecords.sort(Comparator.comparing(TagContractRecord::objectId));
            capacity = tagContractRecords.size();

            readLocks = new ArrayList<>(tagContractRecords.size() + 1);
            ownerTagModified = false;
            for (final TagContractRecord tagContractRecord : tagContractRecords) {
                if (!tagContractRecord.isValid()) {
                    ownerTagModified = true;
                    break;
                }

                final ReadLock lock = tagContractRecord.sharedObject.getLock(ACCESS_OBJECT).readLock();
                lock.lock();
                readLocks.add(lock);

                if (!tagContractRecord.isValid()) {
                    ownerTagModified = true;
                    break;
                }
            }

            // NB: must reverse it before returning because its unlocking must be in the
            // reverse order
            if (readLocks.size() > 1) {
                Collections.reverse(readLocks);
            }

        } while (ownerTagModified);

        readLocks.add(ownerTagsReadLock);

        return readLocks;
    }

    /**
     * NB: this may not return the same locks as the ownerTags of this attribute may
     * be changed at any time. Use it for only unlock.
     *
     * @return the set of read locks after locking
     */
    protected Collection<ReadLock> lockAndGetReadLocks() {

        final long stamp = ownerTagsLock.readLock();

        try {

            // ownerTag state before lock
            List<TagContractRecord> tagContractRecords;
            boolean ownerTagModified = false;
            List<ReadLock> readLocks = null;
            int capacity = ownerTags.size();

            do {
                if (ownerTagModified) {
                    for (final Lock lock : readLocks) {
                        lock.unlock();
                    }
                }

                tagContractRecords = new ArrayList<>(capacity);

                // internally this.ownerTags.size() (WeakHashMap) contains synchronization so
                // better avoid calling it
                // normally there will be one sharedObject so the capacity may be
                // considered as 1

                for (final AbstractHtml ownerTag : ownerTags) {
                    tagContractRecords.add(new TagContractRecord(ownerTag, ownerTag.getSharedObject()));
                }

                tagContractRecords.sort(Comparator.comparing(TagContractRecord::objectId));
                capacity = tagContractRecords.size();

                readLocks = new ArrayList<>(tagContractRecords.size());
                ownerTagModified = false;
                for (final TagContractRecord tagContractRecord : tagContractRecords) {
                    if (!tagContractRecord.isValid()) {
                        ownerTagModified = true;
                        break;
                    }

                    final ReadLock lock = tagContractRecord.sharedObject.getLock(ACCESS_OBJECT).readLock();
                    lock.lock();
                    readLocks.add(lock);

                    if (!tagContractRecord.isValid()) {
                        ownerTagModified = true;
                        break;
                    }
                }

                // NB: must reverse it before returning because its unlocking must be in the
                // reverse order
                if (readLocks.size() > 1) {
                    Collections.reverse(readLocks);
                }

            } while (ownerTagModified);

            return readLocks;
        } finally {
            ownerTagsLock.unlockRead(stamp);
        }

    }

    /**
     * NB: this may not return the same locks as the ownerTags of this attribute may
     * be changed at any time. So call only once and reuse it for both lock and
     * unlock call.
     *
     * NB: should be unlocked in the reverse order.
     *
     * @return the set of write locks
     */
    protected Collection<WriteLock> getWriteLocks() {
        final long stamp = ownerTagsLock.readLock();

        try {

            // internally this.ownerTags.size() (WeakHashMap) contains synchronization so
            // better avoid calling it
            // normally there will be one sharedObject so the capacity may be
            // considered as 1
            final Set<AbstractHtml5SharedObject> sharedObjectsSet = new HashSet<>(1);

            for (final AbstractHtml ownerTag : ownerTags) {
                sharedObjectsSet.add(ownerTag.getSharedObject());
            }

            final List<AbstractHtml5SharedObject> sharedObjects = new ArrayList<>(sharedObjectsSet);

            sharedObjects.sort(Comparator.comparing(AbstractHtml5SharedObject::objectId));

            final List<WriteLock> locks = new ArrayList<>(sharedObjects.size());

            for (final AbstractHtml5SharedObject sharedObject : sharedObjects) {
                locks.add(sharedObject.getLock(ACCESS_OBJECT).writeLock());
            }
            // should not be sorted because it should be in the order of objectId otherwise
            // it may lead to deadlock
//            locks.sort((o1, o2) -> Integer.compare(o2.getHoldCount(), o1.getHoldCount()));

            return locks;
        } finally {
            ownerTagsLock.unlockRead(stamp);
        }
    }

    /**
     * NB: this may not return the same locks as there could be the its ownerTags
     * change. So call only once and reuse it for both lock and unlock call. NB:
     * should be unlocked in the reverse order.
     *
     * @return the set of read locks
     */
    protected Collection<ReadLock> getReadLocks() {
        final long stamp = ownerTagsLock.readLock();

        try {

            // internally this.ownerTags.size() (WeakHashMap) contains synchronization
            // better avoid calling it
            // normally there will be one sharedObject so the capacity may be
            // considered as 2 because the load factor is 0.75f

            final Set<AbstractHtml5SharedObject> sharedObjectsSet = new HashSet<>(1);

            for (final AbstractHtml ownerTag : ownerTags) {
                sharedObjectsSet.add(ownerTag.getSharedObject());
            }

            final List<AbstractHtml5SharedObject> sharedObjects = new ArrayList<>(sharedObjectsSet);

            sharedObjects.sort(Comparator.comparing(AbstractHtml5SharedObject::objectId));

            final Collection<ReadLock> readLocks = new HashSet<>(sharedObjects.size());

            for (final AbstractHtml5SharedObject sharedObject : sharedObjects) {
                readLocks.add(sharedObject.getLock(ACCESS_OBJECT).readLock());
            }

            return readLocks;
        } finally {
            ownerTagsLock.unlockRead(stamp);
        }
    }

    /**
     * for testing purpose only
     *
     * @return
     * @since 3.0.3
     */
    final byte[] getAttrNameIndexBytes() {
        return attrNameIndexBytes;
    }

    /**
     * @return the objectId
     * @since 3.0.15
     */
    final String objectId() {
        return objectId;
    }

}
