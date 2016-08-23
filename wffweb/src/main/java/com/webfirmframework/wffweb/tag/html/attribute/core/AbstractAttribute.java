package com.webfirmframework.wffweb.tag.html.attribute.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import com.webfirmframework.wffweb.tag.core.AbstractTagBase;
import com.webfirmframework.wffweb.tag.html.AbstractHtml;
import com.webfirmframework.wffweb.util.StringBuilderUtil;
import com.webfirmframework.wffweb.util.WffBinaryMessageUtil;

public abstract class AbstractAttribute extends AbstractTagBase {

    private static final long serialVersionUID = 1_0_0L;

    public static final Logger LOGGER = Logger
            .getLogger(AbstractAttribute.class.getName());

    private String attributeName;
    private String attributeValue;
    private Map<String, String> attributeValueMap;
    private Set<String> attributeValueSet;

    private Set<AbstractHtml> ownerTags;

    private StringBuilder tagBuilder;

    private transient Charset charset = Charset.defaultCharset();

    {
        init();
    }

    private void init() {
        tagBuilder = new StringBuilder();
        ownerTags = new HashSet<AbstractHtml>();
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
            tagBuilder.delete(0, tagBuilder.length());
            // tagBuildzer.append(" ");
            tagBuilder.append(attributeName);
            if (attributeValue != null) {
                tagBuilder.append(new char[] { '=', '"' });
                tagBuilder.append(attributeValue);
                result = StringBuilderUtil.getTrimmedString(tagBuilder) + '"';
                tagBuilder.append('"');
            } else if (attributeValueMap != null
                    && attributeValueMap.size() > 0) {
                tagBuilder.append(new char[] { '=', '"' });
                final Set<Entry<String, String>> entrySet = getAttributeValueMap()
                        .entrySet();
                for (final Entry<String, String> entry : entrySet) {
                    tagBuilder.append(entry.getKey());
                    tagBuilder.append(':');
                    tagBuilder.append(entry.getValue());
                    tagBuilder.append(';');
                }

                result = StringBuilderUtil.getTrimmedString(tagBuilder) + '"';
                tagBuilder.append('"');
            } else if (attributeValueSet != null
                    && attributeValueSet.size() > 0) {
                tagBuilder.append(new char[] { '=', '"' });
                for (final String each : getAttributeValueSet()) {
                    tagBuilder.append(each);
                    tagBuilder.append(' ');
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
            // tagBuildzer.append(" ");

            final int attributeNameIndex = AttributeRegistry.getAttributeNames()
                    .indexOf(attributeName);

            if (attributeNameIndex == -1) {

                compressedByIndexBytes.write(new byte[] { (byte) 0 });

                compressedByIndexBytes
                        .write(attributeName.concat("=").getBytes(charset));

                LOGGER.warning(attributeName
                        + " is not indexed, please register it with AttributeRegistrar");
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
        if (this.attributeValueMap != null
                && !this.attributeValueMap.equals(attributeValueMap)) {
            setModified(true);
        } else if (this.attributeValueMap == null
                && attributeValueMap != null) {
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

        if (!attributeValueMap.containsKey(key)
                || !value.equals(attributeValueMap.get(key))) {
            attributeValueMap.put(key, value);
            setModified(true);
            return true;
        }
        return false;
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
        if (getAttributeValueMap().containsKey(key)) {
            setModified(true);
            result = true;
            getAttributeValueMap().remove(key);
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
        if (value == getAttributeValueMap().get(key) || (value != null
                && value.equals(getAttributeValueMap().get(key)))) {
            getAttributeValueMap().remove(key);
            setModified(true);
            return true;
        }
        return false;
    }

    protected void removeAllFromAttributeValueMap() {
        if (attributeValueMap != null && getAttributeValueMap().size() > 0) {
            getAttributeValueMap().clear();
            setModified(true);
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
        if (this.attributeValue != null
                && !this.attributeValue.equals(attributeValue)) {
            setModified(true);
        } else if (this.attributeValue == null && attributeValue != null) {
            setModified(true);
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
            ownerTags.iterator().next();
        }
        return null;
    }

    /**
     * This method is for internal purpose.
     *
     * @return the tags which are consuming this attribute.
     * @since 1.2.0
     * @author WFF
     */
    public Set<AbstractHtml> getOwnerTags() {
        return ownerTags;
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
     * @since 1.2.0
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
        if (this.attributeValueSet != null
                && !this.attributeValueSet.equals(attributeValueSet)) {
            setModified(true);
        } else if (this.attributeValueSet == null
                && attributeValueSet != null) {
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
     */
    protected void addToAttributeValueSet(final String value) {
        getAttributeValueSet().add(value);
        setModified(true);
    }

    /**
     * adds all to the attribute value set.
     *
     * @param value
     * @since 1.0.0
     * @author WFF
     */
    protected void addAllToAttributeValueSet(final Collection<String> values) {
        if (values != null && !getAttributeValueSet().containsAll(values)) {
            getAttributeValueSet().addAll(values);
            setModified(true);
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
        getAttributeValueSet().remove(value);
        setModified(true);
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
        getAttributeValueSet().removeAll(values);
        setModified(true);
    }

    /**
     * clears all values from the value set.
     *
     * @since 1.0.0
     * @author WFF
     */
    protected void removeAllFromAttributeValueSet() {
        getAttributeValueSet().clear();
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
}
