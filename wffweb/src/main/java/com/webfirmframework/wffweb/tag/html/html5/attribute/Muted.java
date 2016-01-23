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
package com.webfirmframework.wffweb.tag.html.html5.attribute;

import com.webfirmframework.wffweb.tag.html.attribute.AttributeNameConstants;
import com.webfirmframework.wffweb.tag.html.attribute.core.AbstractAttribute;
import com.webfirmframework.wffweb.tag.html.html5.identifier.AudioAttribute;

/**
 * {@code <element muted> }
 *
 * A Boolean attribute which indicates whether the audio will be initially
 * silenced.
 * 
 * @author WFF
 *
 */
public class Muted extends AbstractAttribute implements AudioAttribute {

    private static final long serialVersionUID = 1_0_0L;

    private Boolean muted;

    {
        super.setAttributeName(AttributeNameConstants.MUTED);
        init();
    }

    public Muted() {
        setAttributeValue(null);
    }

    public Muted(final Boolean muted) {
        if (muted == null) {
            setAttributeValue(null);
        } else {
            setAttributeValue(String.valueOf(muted));
        }
        this.muted = muted;
    }

    /**
     * invokes only once per object
     *
     * @author WFF
     * @since 1.0.0
     */
    protected void init() {
    }

    /**
     * @return the muted
     * @author WFF
     * @since 1.0.0
     */
    public boolean isMuted() {
        return muted == null || muted.booleanValue() ? true : false;
    }

    /**
     * @param muted
     *            the muted to set. {@code null} will remove the value.
     * @author WFF
     * @since 1.0.0
     */
    public void setLoop(final Boolean muted) {
        if (muted == null) {
            setAttributeValue(null);
        } else {
            setAttributeValue(String.valueOf(muted));
        }
        this.muted = muted;
    }

}
