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
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.webfirmframework.wffweb.clone.CloneUtil;
import com.webfirmframework.wffweb.tag.core.AbstractTagBase;
import com.webfirmframework.wffweb.tag.html.attribute.core.AbstractAttribute;
import com.webfirmframework.wffweb.tag.html.attribute.core.AttributeUtil;
import com.webfirmframework.wffweb.tag.html.model.AbstractHtml5SharedObject;

/**
 * @author WFF
 * @since 1.0.0
 * @version 1.0.0
 *
 */
public abstract class AbstractHtml extends AbstractTagBase {

    private static final long serialVersionUID = 1_0_0L;

    private AbstractHtml parent;
    private List<AbstractHtml> children;
    private String openingTag;

    // should be initialized with empty string
    private String closingTag = "";
    private StringBuilder htmlStartSB;
    private StringBuilder htmlMiddleSB;
    private StringBuilder htmlEndSB;

    private String tagName;

    private StringBuilder tagBuilder;
    private AbstractAttribute[] attributes;

    private AbstractHtml5SharedObject sharedObject;

    private boolean htmlStartSBAsFirst;

    private OutputStream outputStream;

    private transient Charset charset = Charset.defaultCharset();

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
            final Collection<AbstractHtml> children) {

        initInConstructor();

        children.addAll(children);
        buildOpeningTag(false);
        buildClosingTag();
        if (base != null) {
            setParent(base);
            base.children.add(this);
            setSharedObject(base.getSharedObject());
        } else {
            setSharedObject(new AbstractHtml5SharedObject());
        }

    }

    /**
     * @param base
     * @param childContent
     *            any text, it can also be html text.
     */
    public AbstractHtml(final AbstractHtml base, final String childContent) {

        initInConstructor();

        setHtmlStartSBAsFirst(true);
        getHtmlMiddleSB().append(childContent);
        buildOpeningTag(false);
        buildClosingTag();
        if (base != null) {
            setParent(base);
            base.children.add(this);
            setSharedObject(base.getSharedObject());
        } else {
            setSharedObject(new AbstractHtml5SharedObject());
        }
        setRebuild(true);
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
        this.attributes = attributes;

        initInConstructor();

        markOwnerTag(attributes);
        buildOpeningTag(false);
        buildClosingTag();
        if (base != null) {
            setParent(base);
            base.children.add(this);
            setSharedObject(base.getSharedObject());
        } else {
            setSharedObject(new AbstractHtml5SharedObject());
        }
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
        this.attributes = attributes;

        initInConstructor();

        markOwnerTag(attributes);

        buildOpeningTag(false);

        if (tagType == TagType.OPENING_CLOSING) {
            buildClosingTag();
        }

        if (base != null) {
            setParent(base);
            base.children.add(this);
            setSharedObject(base.getSharedObject());
        } else {
            setSharedObject(new AbstractHtml5SharedObject());
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
        children = new LinkedList<AbstractHtml>();
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

    public void setParent(final AbstractHtml parent) {
        this.parent = parent;
    }

    public List<AbstractHtml> getChildren() {
        return children;
    }

    public void setChildren(final List<AbstractHtml> children) {
        this.children = children;
    }

    public String getOpeningTag() {
        if (isRebuild() || isModified()) {
            buildOpeningTag(true);
        }
        return openingTag;
    }

    public void setOpeningTag(final String openingTag) {
        this.openingTag = openingTag;
    }

    public String getClosingTag() {
        return closingTag;
    }

    public void setClosingTag(final String closingTag) {
        this.closingTag = closingTag;
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
            recurChildren(Arrays.asList(this), true);
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
        recurChildrenToOutputStream(Arrays.asList(this), true);
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
    private void recurChildren(final List<AbstractHtml> children,
            final boolean rebuild) {
        if (children != null && children.size() > 0) {
            for (final AbstractHtml child : children) {
                child.setRebuild(rebuild);
                tagBuilder.append(child.getOpeningTag());
                // tagBuilder.append("\n");// TODO should be removed later
                if (isHtmlStartSBAsFirst() && htmlMiddleSB != null) {
                    tagBuilder.append(getHtmlMiddleSB());
                }

                final List<AbstractHtml> childrenOfChildren = child.children;

                if (!isHtmlStartSBAsFirst() && htmlMiddleSB != null) {
                    tagBuilder.append(getHtmlMiddleSB());
                }
                recurChildren(childrenOfChildren, rebuild);

                tagBuilder.append(child.getClosingTag());
                // tagBuilder.append("\n");// TODO should be removed later
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
    private void recurChildrenToOutputStream(final List<AbstractHtml> children,
            final boolean rebuild) throws IOException {
        if (children != null && children.size() > 0) {
            for (final AbstractHtml child : children) {
                child.setRebuild(rebuild);
                outputStream.write(child.getOpeningTag().getBytes(charset));
                if (isHtmlStartSBAsFirst() && htmlMiddleSB != null) {
                    outputStream.write(
                            getHtmlMiddleSB().toString().getBytes(charset));
                }

                final List<AbstractHtml> childrenOfChildren = child.children;

                if (!isHtmlStartSBAsFirst() && htmlMiddleSB != null) {
                    outputStream.write(
                            getHtmlMiddleSB().toString().getBytes(charset));
                }
                recurChildrenToOutputStream(childrenOfChildren, rebuild);
                outputStream.write(child.getClosingTag().getBytes(charset));
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
        return getPrintStructure(true);
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
     * @see com.webfirmframework.wffweb.tag.TagBase#toString()
     *
     * @since 1.0.0
     *
     * @author WFF
     */
    @Override
    public String toString() {
        final String printStructure = getPrintStructure(
                getSharedObject().isChildModified()
                        && !getSharedObject().getRebuiltTags().contains(this));
        if (getParent() == null) {
            getSharedObject().setChildModified(false);
        } else {
            getSharedObject().getRebuiltTags().add(this);
        }
        return printStructure;
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
        if (getTagName() != null) {
            htmlStartSB.append('<');
            htmlStartSB.append(getTagName());
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
        }
        htmlStartSB.trimToSize();
        setOpeningTag(htmlStartSB.toString());
    }

    /**
     *
     * @since 1.0.0
     * @author WFF
     */
    private void buildClosingTag() {
        htmlEndSB.delete(0, htmlEndSB.length());
        if (getTagName() != null) {
            htmlEndSB.append(new char[] { '<', '/' });
            htmlEndSB.append(getTagName());
            htmlEndSB.append('>');
        } else {
            if (htmlStartSB != null) {
                htmlEndSB.append(getHtmlMiddleSB());
            }
        }
        htmlEndSB.trimToSize();
        setClosingTag(htmlEndSB.toString());
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
     * @param sharedObject
     *            the sharedObject to set
     * @since 1.0.0
     * @author WFF
     */
    public void setSharedObject(final AbstractHtml5SharedObject sharedObject) {
        this.sharedObject = sharedObject;
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

    /**
     * @param htmlStartSBAsFirst
     *            the htmlStartSBAsFirst to set
     * @since 1.0.0
     * @author WFF
     */
    public void setHtmlStartSBAsFirst(final boolean htmlStartSBAsFirst) {
        this.htmlStartSBAsFirst = htmlStartSBAsFirst;
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
        // TODO override and use
    }

    /**
     * invokes just before
     * {@code writePrintStructureToOutputStream(final OutputStream} method.
     *
     * @since 1.0.0
     * @author WFF
     */
    protected void beforeWritePrintStructureToOutputStream() {
        // TODO override and use
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
