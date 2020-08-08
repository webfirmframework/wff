/*
 * Copyright 2014-2020 Web Firm Framework
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
package com.webfirmframework.wffweb.css.file;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

import com.webfirmframework.wffweb.clone.CloneUtil;
import com.webfirmframework.wffweb.css.core.CssProperty;

/**
 *
 * @author WFF
 * @since 1.0.0
 */
public abstract class AbstractCssFileBlock implements CssFileBlock {

    private static final long serialVersionUID = 1_0_0L;

    private final CssPropertySet cssProperties;

    private final Map<String, CssProperty> cssPropertiesAsMap;

    private final Set<CssFile> cssFiles;

    private String selectors;

    private volatile boolean modified;

    private volatile boolean loadedOnce;

    private boolean excludeCssBlock;

    private final ReadWriteLock lock;

    @SuppressWarnings("unused")
    private AbstractCssFileBlock() {
    }

    public AbstractCssFileBlock(final String selectors) {
        this.selectors = selectors;
    }

    {
        cssProperties = new CssPropertySet(this);
        cssPropertiesAsMap = cssProperties.getCssPropertiesAsMap();
        lock = cssProperties.getLock();

        cssFiles = new LinkedHashSet<>();
    }

    protected abstract void load(Set<CssProperty> cssProperties);

    void addCssFile(final CssFile cssFile) {
        cssFiles.add(cssFile);
    }

    void removeCssFile(final CssFile cssFile) {
        cssFiles.remove(cssFile);
    }

    @Override
    public String toString() {
        return super.toString();
    }

    /**
     * @return
     * @since 1.0.0
     * @author WFF
     */
    public String toCssString() {
        if (!loadedOnce) {
            final Lock writeLock = lock.writeLock();
            writeLock.lock();

            try {
                if (!loadedOnce) {
                    cssProperties.clearLockless();
                    load(cssProperties);
                    loadedOnce = true;
                    setModified(true);
                    return selectors + "{" + cssProperties.toCssString() + "}";
                }

            } finally {
                writeLock.unlock();
            }
        }
        return selectors + "{" + cssProperties.toCssString() + "}";
    }

    /**
     * @param rebuild
     * @return the css string.
     * @since 1.0.0
     * @author WFF
     */
    public String toCssString(final boolean rebuild) {
        if (rebuild || !loadedOnce) {
            final Lock writeLock = lock.writeLock();
            writeLock.lock();
            try {
                if (rebuild || !loadedOnce) {
                    cssProperties.clearLockless();
                    load(cssProperties);
                    loadedOnce = true;
                    setModified(true);
                    return selectors + "{" + cssProperties.toCssString() + "}";
                }
            } finally {
                writeLock.unlock();
            }
        }
        return selectors + "{" + cssProperties.toCssString() + "}";
    }

    /**
     * @param rebuild
     * @return the css string without selector.
     * @since 3.0.7
     * @author WFF
     */
    public String toCssStringNoSelector(final boolean rebuild) {
        if (rebuild || !loadedOnce) {
            final Lock writeLock = lock.writeLock();
            writeLock.lock();
            try {
                if (rebuild || !loadedOnce) {
                    cssProperties.clearLockless();
                    load(cssProperties);
                    loadedOnce = true;
                    setModified(true);
                    return cssProperties.toCssString();
                }
            } finally {
                writeLock.unlock();
            }
        }
        return cssProperties.toCssString();
    }

    @Override
    public AbstractCssFileBlock clone() throws CloneNotSupportedException {
        return CloneUtil.deepClone(this);
    }

    /**
     * @return the cssProperties
     * @since 1.0.0
     * @author WFF
     */
    public Set<CssProperty> getCssProperties() {
        if (!loadedOnce) {
            final Lock writeLock = lock.writeLock();
            writeLock.lock();
            try {
                if (!loadedOnce) {
                    cssProperties.clearLockless();
                    load(cssProperties);
                    loadedOnce = true;
                    setModified(true);
                }
            } finally {
                writeLock.unlock();
            }
        }
        return cssProperties;
    }

    /**
     * @param modified the modified to set
     * @since 1.0.0
     * @author WFF
     */
    void setModified(final boolean modified) {
        if (modified) {
            for (final CssFile cssFile : cssFiles) {
                cssFile.setModified(true);
            }
        }
        this.modified = modified;
    }

    /**
     * @return true if modified otherwise false
     * @since 3.0.7
     */
    boolean isModified() {
        return modified;
    }

    /**
     * @return the selectors
     * @since 1.0.0
     * @author WFF
     */
    public String getSelectors() {
        return selectors;
    }

    /**
     * rebuild true to rebuild, the load method will be invoked again.
     *
     * @return the cssProperties as map with key as the cssName and value as
     *         {@code CssProperty}.
     * @since 1.0.0
     * @author WFF
     */
    Map<String, CssProperty> getCssPropertiesAsMap(final boolean rebuild) {
        // NB: if this method is made public or the returned map is modified,
        // locking mechanism should also be implemented in cssPropertiesAsMap.
        // i.e. may need to implement custom map.

        if (rebuild || !loadedOnce) {
            final Lock writeLock = lock.writeLock();
            writeLock.lock();
            try {
                if (rebuild || !loadedOnce) {
                    cssProperties.clearLockless();
                    load(cssProperties);
                    loadedOnce = true;
                    setModified(true);
                }
            } finally {
                writeLock.unlock();

            }
        }
        return cssPropertiesAsMap;
    }

    /**
     * @return the excludeCssBlock true if the css block has been excluded, i.e. it
     *         will not be contained in the generated css.
     */
    public boolean isExcludeCssBlock() {
        return excludeCssBlock;
    }

    /**
     * @param excludeCssBlock the excludeCssBlock to set. If it is set to true, then
     *                        this css block will not be contained in the generated
     *                        css.
     */
    protected void setExcludeCssBlock(final boolean excludeCssBlock) {
        this.excludeCssBlock = excludeCssBlock;
    }
}
