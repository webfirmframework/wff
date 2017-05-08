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
import com.webfirmframework.wffweb.tag.html.listener.WffDataDeleteListener;
import com.webfirmframework.wffweb.tag.html.listener.WffDataUpdateListener;
import com.webfirmframework.wffweb.tag.html.model.AbstractHtml5SharedObject;
import com.webfirmframework.wffweb.wffbm.data.WffData;

public abstract class AbstractJsObject extends AbstractTagBase {

    private static final long serialVersionUID = 1L;

    private static final Security ACCESS_OBJECT;

    /**
     * should not be directly consumed, instead use getWffDatas method
     */
    private volatile Map<String, WffData> wffDatas;

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

    private Map<String, WffData> getWffDatas() {
        if (wffDatas == null) {
            initWffDatas();
        }
        return wffDatas;
    }

    private synchronized void initWffDatas() {
        if (wffDatas == null) {
            wffDatas = new HashMap<String, WffData>();
        }
    }

    protected static WffData addWffData(final AbstractHtml abstractHtml,
            final String key, final WffData wffData) {

        final AbstractJsObject abstractJsObject = abstractHtml;

        final WffData previous = abstractJsObject.getWffDatas().put(key,
                wffData);
        final WffDataUpdateListener listener = abstractJsObject
                .getSharedObject().getWffDataUpdateListener(ACCESS_OBJECT);
        if (listener != null) {
            listener.updatedWffData(new WffDataUpdateListener.UpdateEvent(
                    abstractHtml, key, wffData));
        }
        return previous;
    }

    protected static WffData removeWffData(final AbstractHtml abstractHtml,
            final String key) {

        final AbstractJsObject abstractJsObject = abstractHtml;

        final WffData previous = abstractJsObject.getWffDatas().remove(key);

        final WffDataDeleteListener listener = abstractJsObject
                .getSharedObject().getWffDataDeleteListener(ACCESS_OBJECT);
        if (listener != null) {
            listener.deletedWffData(
                    new WffDataDeleteListener.DeleteEvent(abstractHtml, key));
        }

        return previous;
    }

}
