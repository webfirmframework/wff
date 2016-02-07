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
import com.webfirmframework.wffweb.tag.html.html5.identifier.AudioAttributable;

/**
 * {@code <element autoplay> }
 *
 * A Boolean attribute; if specified (even if the value is "false"!), the audio
 * will automatically begin playback as soon as it can do so, without waiting
 * for the entire audio file to finish downloading.
 *
 * @author WFF
 *
 */
public class Autoplay extends AbstractAttribute implements AudioAttributable {

    private static final long serialVersionUID = 1_0_0L;

    private Boolean autoplay;

    {
        super.setAttributeName(AttributeNameConstants.AUTOPLAY);
        init();
    }

    public Autoplay() {
        setAttributeValue(null);
    }

    public Autoplay(final Boolean autoplay) {
        if (autoplay == null) {
            setAttributeValue(null);
        } else {
            setAttributeValue(String.valueOf(autoplay));
        }
        this.autoplay = autoplay;
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
     * @return the autoplay
     * @author WFF
     * @since 1.0.0
     */
    public boolean isAutoplay() {
        return autoplay == null || autoplay.booleanValue() ? true : false;
    }

    /**
     * @param autoplay
     *            the autoplay to set. {@code null} will remove the value.
     * @author WFF
     * @since 1.0.0
     */
    public void setAutoplay(final Boolean autoplay) {
        if (autoplay == null) {
            setAttributeValue(null);
        } else {
            setAttributeValue(String.valueOf(autoplay));
        }
        this.autoplay = autoplay;
    }

}
