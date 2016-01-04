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
import java.util.Set;

import com.webfirmframework.wffweb.tag.core.AbstractTagBase;

/**
 *
 * @author WFF
 * @since 1.0.0
 */
public class AbstractHtml5SharedObject implements Serializable {

    private static final long serialVersionUID = -505355481390467389L;

    private boolean childModified;
    private Set<AbstractTagBase> rebuiltTags;

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
}
