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
package com.webfirmframework.wffweb.tag.core;

import java.util.concurrent.locks.ReentrantLock;

import com.webfirmframework.wffweb.tag.html.attribute.core.AbstractAttribute;

/**
 * @author WFF
 *
 */
public abstract sealed class AbstractTagBase implements TagBase permits AbstractJsObject, AbstractAttribute {

    private static final long serialVersionUID = 1_0_0L;

    /**
     * replacement lock for synchronized block. i.e. synchronized (this) may be
     * replaced with this lock
     */
    private final ReentrantLock commonLock = new ReentrantLock();

    private volatile boolean rebuild = true;

    private volatile boolean modified;

    private volatile Object data;

    /**
     * @return the rebuild
     * @since 1.0.0
     * @author WFF
     */
    public boolean isRebuild() {
        return rebuild;
    }

    /**
     * @param rebuild the rebuild to set
     * @since 1.0.0
     * @author WFF
     */
    public void setRebuild(final boolean rebuild) {
        this.rebuild = rebuild;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        return super.equals(obj);
    }

    /**
     * @return the modified true if the current objects state is modified
     * @since 1.0.0
     * @author WFF
     */
    public boolean isModified() {
        return modified;
    }

    /**
     * set true whenever this object's state is modified.
     *
     * @param modified the modified to set
     * @since 1.0.0
     * @author WFF
     */
    public void setModified(final boolean modified) {
        this.modified = modified;
    }

    /**
     * Gets the given data set by {@code setData}. This is merely a getter-setter
     * methods of data property. The developer can set and get any data using these
     * methods.
     *
     * @return the data set by {@code setCode} method.
     * @since 2.1.4
     * @author WFF
     */
    public Object getData() {
        return data;
    }

    /**
     * Sets the given data. This is merely a getter-setter methods of data property.
     * The developer can set and get any data using these methods.
     *
     * @param data
     * @since 2.1.4
     * @author WFF
     */
    public void setData(final Object data) {
        this.data = data;
    }

    /**
     * replacement lock for synchronized block. i.e. synchronized (this) may be
     * replaced with this lock
     *
     * @return ReentrantLock
     * @since 12.0.0-beta.6
     */
    protected final ReentrantLock commonLock() {
        return commonLock;
    }
}
