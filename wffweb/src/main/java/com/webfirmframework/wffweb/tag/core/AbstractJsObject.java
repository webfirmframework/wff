/*
 * Copyright 2014-2017 Web Firm Framework
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
 */
package com.webfirmframework.wffweb.tag.core;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.webfirmframework.wffweb.tag.html.AbstractHtml;
import com.webfirmframework.wffweb.tag.html.listener.WffBMDataDeleteListener;
import com.webfirmframework.wffweb.tag.html.listener.WffBMDataUpdateListener;
import com.webfirmframework.wffweb.tag.html.model.AbstractHtml5SharedObject;
import com.webfirmframework.wffweb.wffbm.data.WffBMData;

/**
 * @author WFF
 * @since 2.1.8
 */
public abstract class AbstractJsObject extends AbstractTagBase {

    private static final long serialVersionUID = 1L;

    private static final Security ACCESS_OBJECT;

    /**
     * should not be directly consumed as it may not have been initialized,
     * instead use getWffDatas method. But, its direct usage is valid only for
     * {@code AbstractHtml#getWffObjects()}
     */
    protected volatile Map<String, WffBMData> wffBMDatas;

    // for security purpose, the class name should not be modified
    private static final class Security implements Serializable {

        private static final long serialVersionUID = 1L;

        private Security() {
        }
    }

    static {
        ACCESS_OBJECT = new Security();
    }

    public abstract AbstractHtml5SharedObject getSharedObject();

    /**
     * @return
     * @since 2.1.8
     * @author WFF
     */
    private Map<String, WffBMData> getWffDatas() {
        if (wffBMDatas == null) {
            initWffDatas();
        }
        return wffBMDatas;
    }

    /**
     *
     * @since 2.1.8
     * @author WFF
     */
    private synchronized void initWffDatas() {
        if (wffBMDatas == null) {
            wffBMDatas = new HashMap<String, WffBMData>();
        }
    }

    /**
     * @param abstractHtml
     * @param key
     * @param wffBMData
     * @return
     * @since 2.1.8
     * @author WFF
     */
    protected static WffBMData addWffData(final AbstractHtml abstractHtml,
            final String key, final WffBMData wffBMData) {

        final AbstractJsObject abstractJsObject = abstractHtml;

        final WffBMData previous = abstractJsObject.getWffDatas().put(key,
                wffBMData);
        final WffBMDataUpdateListener listener = abstractJsObject
                .getSharedObject().getWffBMDataUpdateListener(ACCESS_OBJECT);
        if (listener != null) {
            listener.updatedWffData(new WffBMDataUpdateListener.UpdateEvent(
                    abstractHtml, key, wffBMData));
        }
        return previous;
    }

    /**
     * @param abstractHtml
     * @param key
     * @return
     * @since 2.1.8
     * @author WFF
     */
    protected static WffBMData removeWffData(final AbstractHtml abstractHtml,
            final String key) {

        final AbstractJsObject abstractJsObject = abstractHtml;

        final WffBMData previous = abstractJsObject.getWffDatas().remove(key);

        final WffBMDataDeleteListener listener = abstractJsObject
                .getSharedObject().getWffBMDataDeleteListener(ACCESS_OBJECT);
        if (listener != null) {
            listener.deletedWffData(
                    new WffBMDataDeleteListener.DeleteEvent(abstractHtml, key));
        }

        return previous;
    }

}
