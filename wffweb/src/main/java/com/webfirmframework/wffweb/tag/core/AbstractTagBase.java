/*
 * Copyright 2014-2015 Web Firm Framework
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

/**
 * @author WFF
 *
 */
public abstract class AbstractTagBase implements TagBase {

    /**
     *
     */
    private static final long serialVersionUID = 5458640407778791302L;
    private boolean rebuild = true;
    private boolean modified;

    /**
     * @return the rebuild
     * @since 1.0.0
     * @author WFF
     */
    public boolean isRebuild() {
        return rebuild;
    }

    /**
     * @param rebuild
     *            the rebuild to set
     * @since 1.0.0
     * @author WFF
     */
    public void setRebuild(final boolean rebuild) {
        this.rebuild = rebuild;
    }

    @Override
    public int hashCode() {
        // TODO Auto-generated method stub
        return super.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        // TODO Auto-generated method stub
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
     * @param modified
     *            the modified to set
     * @since 1.0.0
     * @author WFF
     */
    public void setModified(final boolean modified) {
        this.modified = modified;
    }
}
