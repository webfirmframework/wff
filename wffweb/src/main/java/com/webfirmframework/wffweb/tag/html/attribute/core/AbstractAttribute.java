package com.webfirmframework.wffweb.tag.html.attribute.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.webfirmframework.wffweb.tag.core.AbstractTagBase;
import com.webfirmframework.wffweb.tag.html.AbstractHtml;
import com.webfirmframework.wffweb.tag.html.attribute.listener.AttributeValueChangeListener;
import com.webfirmframework.wffweb.tag.html.model.AbstractHtml5SharedObject;
import com.webfirmframework.wffweb.util.StringBuilderUtil;
import com.webfirmframework.wffweb.util.WffBinaryMessageUtil;

public abstract class AbstractAttribute extends AbstractTagBase {

    private static final long serialVersionUID = 1_1_0L;

    public static final Logger LOGGER = Logger
            .getLogger(AbstractAttribute.class.getName());

    private static final Security ACCESS_OBJECT;

    private String attributeName;
    private String attributeValue;
    private Map<String, String> attributeValueMap;
    private Set<String> attributeValueSet;

    private transient Set<AbstractHtml> ownerTags;

    private StringBuilder tagBuilder;

    // private AttributeValueChangeListener valueChangeListener;

    private Set<AttributeValueChangeListener> valueChangeListeners;

    private transient Charset charset = Charset.defaultCharset();

    // for security purpose, the class name should not be modified
    private static final class Security implements Serializable {

        private static final long serialVersionUID = 1L;

        private Security() {
        }
    }

    static {
        ACCESS_OBJECT = new Security();
    }

    {
        init();
    }

    private void init() {
        tagBuilder = new StringBuilder();
        ownerTags = Collections
                .newSetFromMap(new WeakHashMap<AbstractHtml, Boolean>());
        setRebuild(true);
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
            printStructure = getPrintStructure(isRebuild());
            setRebuild(false);
        } else {
            printStructure = tagBuilder.toString();
        }
        return printStructure;
    }

    protected String getPrintStructure(final boolean rebuild) {
        String result = "";
        if (rebuild || isRebuild() || isModified()) {
            beforePrintStructure();

            if (tagBuilder.length() > 0) {
                tagBuilder.delete(0, tagBuilder.length());
            }
            // tagBuildzer.append(' ');
            tagBuilder.append(attributeName);
            if (attributeValue != null) {
                tagBuilder.append(new char[] { '=', '"' })
                        .append(attributeValue);
                result = StringBuilderUtil.getTrimmedString(tagBuilder) + '"';
                tagBuilder.append('"');
            } else if (attributeValueMap != null
                    && attributeValueMap.size() > 0) {
                tagBuilder.append(new char[] { '=', '"' });
                final Set<Entry<String, String>> entrySet = getAttributeValueMap()
                        .entrySet();
                for (final Entry<String, String> entry : entrySet) {
                    tagBuilder.append(entry.getKey()).append(':')
                            .append(entry.getValue()).append(';');
                }

                result = StringBuilderUtil.getTrimmedString(tagBuilder) + '"';
                tagBuilder.append('"');
            } else if (attributeValueSet != null
                    && attributeValueSet.size() > 0) {
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
             * should be analyzed for optimization (as the deleteCharAt method
             * might compromise performance).
             *
             * if (Character.isWhitespace(tagBuilder.charAt(lastIndex))) {
             * tagBuilder.deleteCharAt(lastIndex); }
             */
            // result = StringBuilderUtil.getTrimmedString(tagBuilder) + "\"";
            // tagBuilder.append("\"");
            setRebuild(false);
        }
        tagBuilder.trimToSize();
        return result;
    }

    /**
     * gets the attribute name and value in the format of name=value. <br>
     * Eg: style=color:green;background:blue <br>
     * This reduces 2 bytes taken for ".
     *
     * @return the attribute name and value in the format of name=value. Eg:
     *         style=color:green;background:blue
     * @since 2.0.0
     * @author WFF
     */
    protected String getWffPrintStructure() {
        String result = "";
        beforeWffPrintStructure();
        final StringBuilder attrBuilder = new StringBuilder();

        attrBuilder.append(attributeName);
        if (attributeValue != null) {
            attrBuilder.append(new char[] { '=' }).append(attributeValue);
            result = StringBuilderUtil.getTrimmedString(attrBuilder);
        } else if (attributeValueMap != null && attributeValueMap.size() > 0) {
            attrBuilder.append(new char[] { '=' });
            final Set<Entry<String, String>> entrySet = getAttributeValueMap()
                    .entrySet();
            for (final Entry<String, String> entry : entrySet) {
                attrBuilder.append(entry.getKey()).append(':')
                        .append(entry.getValue()).append(';');
            }

            result = StringBuilderUtil.getTrimmedString(attrBuilder);
        } else if (attributeValueSet != null && attributeValueSet.size() > 0) {
            attrBuilder.append(new char[] { '=' });
            for (final String each : getAttributeValueSet()) {
                attrBuilder.append(each).append(' ');
            }
            result = StringBuilderUtil.getTrimmedString(attrBuilder);
        } else {
            result = attrBuilder.toString();
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
     * @since 2.0.0
     * @author WFF
     */
    public String toWffString() {
        return getWffPrintStructure();
    }

    protected void beforeWffPrintStructure() {
        // NOT override and use if required
    }

    /**
     * gives compressed by index bytes for the attribute and value. The first
     * byte represents the attribute name index bytes length, the next bytes
     * represent the attribute name index bytes and the remaining bytes
     * represent attribute value without <i>=</i> and <i>"</i>.
     *
     * @param rebuild
     * @return the compressed by index bytes.
     * @throws IOException
     * @since 1.1.3
     * @author WFF
     */
    protected byte[] getBinaryStructureCompressedByIndex(final boolean rebuild)
            throws IOException {

        final String charset = this.charset.name();
        // TODO review code

        final ByteArrayOutputStream compressedByIndexBytes = new ByteArrayOutputStream();

        byte[] compressedBytes = new byte[0];

        // String result = "";
        if (rebuild || isRebuild() || isModified()) {

            beforePrintStructureCompressedByIndex();
            // tagBuildzer.append(' ');

            final int attributeNameIndex = AttributeRegistry.getAttributeNames()
                    .indexOf(attributeName);

            if (attributeNameIndex == -1) {

                compressedByIndexBytes.write(new byte[] { (byte) 0 });

                compressedByIndexBytes
                        .write(attributeName.concat("=").getBytes(charset));

                if (LOGGER.isLoggable(Level.WARNING)) {
                    LOGGER.warning(attributeName
                            + " is not indexed, please register it with AttributeRegistrar");
                }
            } else {

                final byte[] optimizedBytesFromInt = WffBinaryMessageUtil
                        .getOptimizedBytesFromInt(attributeNameIndex);
                compressedByIndexBytes.write(
                        new byte[] { (byte) optimizedBytesFromInt.length });
                compressedByIndexBytes.write(optimizedBytesFromInt);
            }

            if (attributeValue != null) {

                compressedByIndexBytes.write(attributeValue.getBytes(charset));
                compressedBytes = compressedByIndexBytes.toByteArray();
            } else if (attributeValueMap != null
                    && attributeValueMap.size() > 0) {
                final Set<Entry<String, String>> entrySet = getAttributeValueMap()
                        .entrySet();
                for (final Entry<String, String> entry : entrySet) {

                    compressedByIndexBytes
                            .write(entry.getKey().getBytes(charset));
                    compressedByIndexBytes.write(new byte[] { ':' });
                    compressedByIndexBytes
                            .write(entry.getValue().getBytes(charset));
                    compressedByIndexBytes.write(new byte[] { ';' });
                }
                compressedBytes = compressedByIndexBytes.toByteArray();
            } else if (attributeValueSet != null
                    && attributeValueSet.size() > 0) {
                for (final String each : getAttributeValueSet()) {

                    compressedByIndexBytes.write(each.getBytes(charset));
                    compressedByIndexBytes.write(new byte[] { ' ' });
                }
                compressedBytes = compressedByIndexBytes.toByteArray();
            } else {
                compressedBytes = compressedByIndexBytes.toByteArray();
            }

            setRebuild(false);
        }
        return compressedBytes;
    }

    /**
     * @return the attributeName set by
     *         {@code AbstractAttribute#setAttributeName(String)}
     * @since 1.0.0
     * @author WFF
     */
    public String getAttributeName() {
        return attributeName;
    }

    /**
     * Set attribute name, eg: width, height, name, type etc..
     *
     * @param attributeName
     *            the attributeName to set
     * @since 1.0.0
     * @author WFF
     */
    protected void setAttributeName(final String attributeName) {
        this.attributeName = attributeName;
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

    public byte[] toCompressedBytesByIndex(final boolean rebuild)
            throws IOException {
        return getBinaryStructureCompressedByIndex(rebuild);
    }

    public byte[] toCompressedBytesByIndex(final boolean rebuild,
            final Charset charset) throws IOException {
        final Charset previousCharset = this.charset;
        try {
            this.charset = charset;
            return toCompressedBytesByIndex(rebuild);
        } finally {
            this.charset = previousCharset;
        }
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
     * @since 1.0.0
     * @author WFF
     */
    protected Map<String, String> getAttributeValueMap() {
        if (attributeValueMap == null) {
            setAttributeValueMap(new LinkedHashMap<String, String>());
        }
        return attributeValueMap;
    }

    /**
     * @param attributeValueMap
     *            the attributeValueMap to set
     * @since 1.0.0
     * @author WFF
     */
    protected void setAttributeValueMap(
            final Map<String, String> attributeValueMap) {

        if (!Objects.equals(attributeValueMap, this.attributeValueMap)) {
            setModified(true);
        }

        this.attributeValueMap = attributeValueMap;
    }

    /**
     * adds the given key value.
     *
     * @param key
     * @param value
     * @return true if it is modified
     * @since 1.0.0
     * @author WFF
     */
    protected boolean addToAttributeValueMap(final String key,
            final String value) {

        final Map<String, String> attributeValueMap = getAttributeValueMap();

        final String previousValue = attributeValueMap.put(key, value);

        if (!Objects.equals(previousValue, value)) {

            setModified(true);

            invokeValueChangeListeners();

            return true;

        }

        // if (!attributeValueMap.containsKey(key)
        // || !value.equals(attributeValueMap.get(key))) {
        //
        //
        // setModified(true);
        // return true;
        // }
        return false;
    }

    private void invokeValueChangeListeners() {
        final Set<AbstractHtml5SharedObject> sharedObjects = new HashSet<AbstractHtml5SharedObject>(
                ownerTags.size());

        for (final AbstractHtml ownerTag : ownerTags) {
            sharedObjects.add(ownerTag.getSharedObject());
        }

        for (final AbstractHtml5SharedObject sharedObject : sharedObjects) {
            final AttributeValueChangeListener valueChangeListener = sharedObject
                    .getValueChangeListener(ACCESS_OBJECT);
            if (valueChangeListener != null) {

                // ownerTags should not be modified in the consuming
                // part, here
                // skipped it making unmodifiable to gain
                // performance
                final AttributeValueChangeListener.Event event = new AttributeValueChangeListener.Event(
                        this, ownerTags);
                valueChangeListener.valueChanged(event);
            }
        }

        if (valueChangeListeners != null) {
            for (final AttributeValueChangeListener listener : valueChangeListeners) {
                final AttributeValueChangeListener.Event event = new AttributeValueChangeListener.Event(
                        AbstractAttribute.this,
                        Collections.unmodifiableSet(ownerTags));
                listener.valueChanged(event);
            }
        }
    }

    /**
     * adds all to the attribute value map.
     *
     * @param map
     * @since 1.0.0
     * @author WFF
     * @return true if it is modified
     */
    protected boolean addAllToAttributeValueMap(final Map<String, String> map) {
        if (map != null && map.size() > 0) {
            getAttributeValueMap().putAll(map);

            setModified(true);

            invokeValueChangeListeners();

            return true;
        }
        return false;
    }

    /**
     * removes the key value for the input key.
     *
     * @param key
     * @since 1.0.0
     * @author WFF
     * @return true if the given key (as well as value contained corresponding
     *         to it) has been removed.
     */
    protected boolean removeFromAttributeValueMap(final String key) {
        boolean result = false;
        final String previous = getAttributeValueMap().remove(key);

        if (previous != null) {
            setModified(true);
            result = true;
            invokeValueChangeListeners();
        }

        return result;
    }

    /**
     * removes only if the key and value matches in the map for any particular
     * entry.
     *
     * @param key
     * @param value
     * @since 1.0.0
     * @author WFF
     * @return true if it is modified
     */
    protected boolean removeFromAttributeValueMap(final String key,
            final String value) {
        final String previousValue = getAttributeValueMap().remove(key);

        if (!Objects.equals(previousValue, value)) {

            setModified(true);

            invokeValueChangeListeners();

            return true;

        }
        return false;
    }

    protected void removeAllFromAttributeValueMap() {
        if (attributeValueMap != null && getAttributeValueMap().size() > 0) {
            getAttributeValueMap().clear();
            setModified(true);

            invokeValueChangeListeners();

        }
    }

    /**
     * @return the attributeValue
     * @since 1.0.0
     * @author WFF
     */
    public String getAttributeValue() {
        return attributeValue;
    }

    /**
     * @param attributeValue
     *            the attributeValue to set
     * @since 1.0.0
     * @author WFF
     */
    protected void setAttributeValue(final String attributeValue) {
        if (!Objects.equals(this.attributeValue, attributeValue)) {
            setModified(true);

            invokeValueChangeListeners();

        }
        this.attributeValue = attributeValue;
    }

    /**
     * @returns one of the ownerTags
     * @since 1.0.0
     * @author WFF
     * @deprecated this method may be removed later as there could be multiple
     *             owner tags.
     */
    @Deprecated
    public AbstractHtml getOwnerTag() {
        if (ownerTags.iterator().hasNext()) {
            return ownerTags.iterator().next();
        }
        return null;
    }

    /**
     *
     * @return the tags which are consuming this attribute as an array
     * @since 2.0.0
     * @author WFF
     */
    public AbstractHtml[] getOwnerTags() {
        // returning the set is not good because
        // if the AbstractHtml needs to be
        // modified while iterating the set will cause
        // ConcurrentModificationException
        return ownerTags.toArray(new AbstractHtml[ownerTags.size()]);
    }

    /**
     * NB:- this method is used for internal purpose, so it should not be
     * consumed.
     *
     * @param ownerTag
     *            the ownerTag to set
     * @since 1.0.0
     * @author WFF
     */
    public void setOwnerTag(final AbstractHtml ownerTag) {
        ownerTags.add(ownerTag);
    }

    /**
     * NB:- this method is used for internal purpose, so it should not be
     * consumed.
     *
     * @param ownerTag
     *            the ownerTag to unset
     * @return true if the given ownerTag is an owner of the attribute.
     * @since 2.0.0
     * @author WFF
     */
    public boolean unsetOwnerTag(final AbstractHtml ownerTag) {
        return ownerTags.remove(ownerTag);
    }

    @Override
    public void setModified(final boolean modified) {
        super.setModified(modified);

        for (final AbstractHtml ownerTag : ownerTags) {
            if (ownerTag.getSharedObject() != null) {
                ownerTag.getSharedObject().setChildModified(modified);
            }
        }
    }

    /**
     * NB:- this is only for getting values. Use addToAttributeValueSet method
     * for adding
     *
     * @return the attributeValueSet
     * @since 1.0.0
     * @author WFF
     */
    protected Set<String> getAttributeValueSet() {
        if (attributeValueSet == null) {
            attributeValueSet = new LinkedHashSet<String>();
        }
        return attributeValueSet;
    }

    /**
     * @param attributeValueSet
     *            the attributeValueSet to set
     * @since 1.0.0
     * @author WFF
     */
    protected void setAttributeValueSet(final Set<String> attributeValueSet) {
        if (!Objects.equals(this.attributeValueSet, attributeValueSet)) {
            setModified(true);
        }
        this.attributeValueSet = attributeValueSet;
    }

    /**
     * adds to the attribute value set.
     *
     * @param value
     * @since 1.0.0
     * @author WFF
     * @return
     */
    protected boolean addToAttributeValueSet(final String value) {
        final boolean added = getAttributeValueSet().add(value);

        if (added) {
            setModified(true);

            invokeValueChangeListeners();

        }
        return added;
    }

    /**
     * adds all to the attribute value set.
     *
     * @param value
     * @since 1.0.0
     * @author WFF
     */
    protected void addAllToAttributeValueSet(final Collection<String> values) {
        if (values != null) {

            final boolean added = getAttributeValueSet().addAll(values);

            if (added) {
                setModified(true);

                invokeValueChangeListeners();

            }

        }
    }

    /**
     * removes the value from the the attribute set.
     *
     * @param value
     * @since 1.0.0
     * @author WFF
     */
    protected void removeFromAttributeValueSet(final String value) {
        final boolean removed = getAttributeValueSet().remove(value);

        if (removed) {

            setModified(true);

            invokeValueChangeListeners();

        }
    }

    /**
     * removes the value from the the attribute set.
     *
     * @param values
     * @since 1.0.0
     * @author WFF
     */
    protected void removeAllFromAttributeValueSet(
            final Collection<String> values) {
        final boolean removedAll = getAttributeValueSet().removeAll(values);

        if (removedAll) {
            setModified(true);

            invokeValueChangeListeners();

        }

    }

    /**
     * clears all values from the value set.
     *
     * @since 1.0.0
     * @author WFF
     */
    protected void removeAllFromAttributeValueSet() {
        getAttributeValueSet().clear();
        setModified(true);

        invokeValueChangeListeners();

    }

    /**
     * invokes just before {@code getPrintStructure(final boolean} method and
     * only if the getPrintStructure(final boolean} rebuilds the structure.
     *
     * @since 1.0.0
     * @author WFF
     */
    protected void beforePrintStructure() {
        // TODO override and use
    }

    /**
     * invokes just before
     * {@code getPrintStructureCompressedByIndex(final boolean} method and only
     * if the getPrintStructureCompressedByIndex(final boolean} rebuilds the
     * structure.
     *
     * @since 1.0.0
     * @author WFF
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
     * @param charset
     *            the charset to set
     */
    public void setCharset(final Charset charset) {
        this.charset = charset;
    }

    /**
     * adds value change lister which will be invoked when the value changed
     *
     * @param valueChangeListener
     * @since 2.0.0
     * @author WFF
     */
    public void addValueChangeListener(
            final AttributeValueChangeListener valueChangeListener) {
        if (valueChangeListeners == null) {
            valueChangeListeners = new TreeSet<AttributeValueChangeListener>();
        }

        valueChangeListeners.add(valueChangeListener);
    }

    /**
     * removes the corresponding value change listener
     *
     * @param valueChangeListener
     * @since 2.0.0
     * @author WFF
     */
    public void removeValueChangeListener(
            final AttributeValueChangeListener valueChangeListener) {
        if (valueChangeListeners != null) {
            valueChangeListeners.remove(valueChangeListener);
        }
    }

    /**
     * @return the set of value change listeners
     * @since 2.0.0
     * @author WFF
     */
    public Set<AttributeValueChangeListener> getValueChangeListeners() {
        return Collections.unmodifiableSet(valueChangeListeners);
    }

}
