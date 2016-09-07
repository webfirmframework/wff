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
package com.webfirmframework.wffweb.tag.html.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.webfirmframework.wffweb.WffSecurityException;
import com.webfirmframework.wffweb.security.object.SecurityClassConstants;
import com.webfirmframework.wffweb.tag.core.AbstractTagBase;
import com.webfirmframework.wffweb.tag.html.AbstractHtml;
import com.webfirmframework.wffweb.tag.html.listener.AttributeAddListener;
import com.webfirmframework.wffweb.tag.html.listener.AttributeRemoveListener;
import com.webfirmframework.wffweb.tag.html.listener.ChildTagAppendListener;
import com.webfirmframework.wffweb.tag.html.listener.ChildTagRemoveListener;
import com.webfirmframework.wffweb.tag.html.listener.InnerHtmlAddListener;

/**
 *
 * @author WFF
 * @since 1.0.0
 */
public class AbstractHtml5SharedObject implements Serializable {

    private static final long serialVersionUID = 1_0_0L;

    private boolean childModified;

    private Set<AbstractTagBase> rebuiltTags;

    private ChildTagAppendListener childTagAppendListener;

    private ChildTagRemoveListener childTagRemoveListener;

    private AttributeAddListener attributeAddListener;

    private AttributeRemoveListener attributeRemoveListener;

    private InnerHtmlAddListener innerHtmlAddListener;

    private Map<String, AbstractHtml> tagByWffId;

    private volatile int dataWffId = -1;

    /**
     * @return unique int id for data-wff-id attribute
     * @since 1.2.0
     * @author WFF
     */
    public String getNewDataWffId(final Object accessObject) {

        if (accessObject == null || !((SecurityClassConstants.ABSTRACT_HTML
                .equals(accessObject.getClass().getName()))
                || (SecurityClassConstants.BROWSER_PAGE
                        .equals(accessObject.getClass().getName())))) {
            throw new WffSecurityException(
                    "Not allowed to consume this method. This method is for internal use.");
        }

        final String id = "S" + (++dataWffId);
        return id;
    }

    public int getLastDataWffId(final Object accessObject) {

        if (accessObject == null || !((SecurityClassConstants.ABSTRACT_HTML
                .equals(accessObject.getClass().getName())))) {
            throw new WffSecurityException(
                    "Not allowed to consume this method. This method is for internal use.");
        }
        return dataWffId;
    }

    /**
     * @return the childModified true if any of the children has been modified
     * @since 1.0.0
     * @author WFF
     */
    public boolean isChildModified() {
        return childModified;
    }

    /**
     * set true if any of the children has been modified
     *
     * @param childModified
     *            the childModified to set
     * @since 1.0.0
     * @author WFF
     */
    public void setChildModified(final boolean childModified) {
        this.childModified = childModified;
    }

    /**
     * gets the set containing the objects which are rebuilt after modified by
     * its {@code AbstractTagBase} method.
     *
     * @return the rebuiltTags
     * @since 1.0.0
     * @author WFF
     */
    public Set<AbstractTagBase> getRebuiltTags() {
        if (rebuiltTags == null) {
            rebuiltTags = new HashSet<AbstractTagBase>();
        }
        return rebuiltTags;
    }

    /**
     * NB:- This method is for only for internal use
     *
     * @return the childTagAppendListener
     */
    public ChildTagAppendListener getChildTagAppendListener(
            final Object accessObject) {
        if (accessObject == null || !(SecurityClassConstants.ABSTRACT_HTML
                .equals(accessObject.getClass().getName()))) {
            throw new WffSecurityException(
                    "Not allowed to consume this method. This method is for internal use.");
        }
        return childTagAppendListener;
    }

    /**
     * NB:- This method is for only for internal use
     *
     * @param childTagAppendListener
     *            the childTagAppendListener to set
     */
    public void setChildTagAppendListener(
            final ChildTagAppendListener childTagAppendListener,
            final Object accessObject) {
        if (accessObject == null || !(SecurityClassConstants.BROWSER_PAGE
                .equals(accessObject.getClass().getName()))) {
            throw new WffSecurityException(
                    "Not allowed to consume this method. This method is for internal use.");
        }
        this.childTagAppendListener = childTagAppendListener;
    }

    /**
     * NB:- This method is for only for internal use
     *
     * @return the childTagRemoveListener
     */
    public ChildTagRemoveListener getChildTagRemoveListener(
            final Object accessObject) {
        if (accessObject == null || !(SecurityClassConstants.ABSTRACT_HTML
                .equals(accessObject.getClass().getName()))) {
            throw new WffSecurityException(
                    "Not allowed to consume this method. This method is for internal use.");
        }
        return childTagRemoveListener;
    }

    /**
     * NB:- This method is for only for internal use
     *
     * @param childTagRemoveListener
     *            the childTagRemoveListener to set
     */
    public void setChildTagRemoveListener(
            final ChildTagRemoveListener childTagRemoveListener,
            final Object accessObject) {
        if (accessObject == null || !(SecurityClassConstants.BROWSER_PAGE
                .equals(accessObject.getClass().getName()))) {
            throw new WffSecurityException(
                    "Not allowed to consume this method. This method is for internal use.");
        }
        this.childTagRemoveListener = childTagRemoveListener;
    }

    /**
     * @return the attributeAddListener
     */
    public AttributeAddListener getAttributeAddListener(
            final Object accessObject) {
        if (accessObject == null || !(SecurityClassConstants.ABSTRACT_HTML
                .equals(accessObject.getClass().getName()))) {
            throw new WffSecurityException(
                    "Not allowed to consume this method. This method is for internal use.");
        }
        return attributeAddListener;
    }

    /**
     * @param attributeAddListener
     *            the attributeAddListener to set
     */
    public void setAttributeAddListener(
            final AttributeAddListener attributeAddListener,
            final Object accessObject) {
        if (accessObject == null || !(SecurityClassConstants.BROWSER_PAGE
                .equals(accessObject.getClass().getName()))) {
            throw new WffSecurityException(
                    "Not allowed to consume this method. This method is for internal use.");
        }
        this.attributeAddListener = attributeAddListener;
    }

    /**
     * @return the attributeRemoveListener
     */
    public AttributeRemoveListener getAttributeRemoveListener(
            final Object accessObject) {
        if (accessObject == null || !(SecurityClassConstants.ABSTRACT_HTML
                .equals(accessObject.getClass().getName()))) {
            throw new WffSecurityException(
                    "Not allowed to consume this method. This method is for internal use.");
        }
        return attributeRemoveListener;
    }

    /**
     * @param attributeRemoveListener
     *            the attributeRemoveListener to set
     */
    public void setAttributeRemoveListener(
            final AttributeRemoveListener attributeRemoveListener,
            final Object accessObject) {
        if (accessObject == null || !(SecurityClassConstants.BROWSER_PAGE
                .equals(accessObject.getClass().getName()))) {
            throw new WffSecurityException(
                    "Not allowed to consume this method. This method is for internal use.");
        }
        this.attributeRemoveListener = attributeRemoveListener;
    }

    /**
     * @return the innerHtmlAddListener
     */
    public InnerHtmlAddListener getInnerHtmlAddListener(
            final Object accessObject) {
        if (accessObject == null || !(SecurityClassConstants.ABSTRACT_HTML
                .equals(accessObject.getClass().getName()))) {
            throw new WffSecurityException(
                    "Not allowed to consume this method. This method is for internal use.");
        }
        return innerHtmlAddListener;
    }

    /**
     * @param innerHtmlAddListener
     *            the innerHtmlAddListener to set
     */
    public void setInnerHtmlAddListener(
            final InnerHtmlAddListener innerHtmlAddListener,
            final Object accessObject) {

        if (accessObject == null || !(SecurityClassConstants.BROWSER_PAGE
                .equals(accessObject.getClass().getName()))) {
            throw new WffSecurityException(
                    "Not allowed to consume this method. This method is for internal use.");
        }
        this.innerHtmlAddListener = innerHtmlAddListener;
    }

    /**
     * @param accessObject
     * @return the map containing wffid and tag
     * @since 1.2.0
     * @author WFF
     */
    public Map<String, AbstractHtml> getTagByWffId(final Object accessObject) {
        if (accessObject == null || !(SecurityClassConstants.BROWSER_PAGE
                .equals(accessObject.getClass().getName()))) {
            throw new WffSecurityException(
                    "Not allowed to consume this method. This method is for internal use.");
        }
        if (tagByWffId == null) {
            tagByWffId = new ConcurrentHashMap<String, AbstractHtml>();
        }
        return tagByWffId;
    }

    /**
     * @param accessObject
     * @since 1.2.0
     * @author WFF
     * @return the map containing wffid and tag
     */
    public Map<String, AbstractHtml> initTagByWffId(final Object accessObject) {
        if (accessObject == null || !(SecurityClassConstants.BROWSER_PAGE
                .equals(accessObject.getClass().getName()))) {
            throw new WffSecurityException(
                    "Not allowed to consume this method. This method is for internal use.");
        }
        if (tagByWffId == null) {
            tagByWffId = new ConcurrentHashMap<String, AbstractHtml>();
        }
        return tagByWffId;
    }

}
