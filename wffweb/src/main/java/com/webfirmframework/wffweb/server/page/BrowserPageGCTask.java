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
 */
package com.webfirmframework.wffweb.server.page;

import java.lang.ref.Cleaner;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

import com.webfirmframework.wffweb.settings.WffConfiguration;
import com.webfirmframework.wffweb.util.FileUtil;

/**
 * @author WFF
 * @since 3.0.18
 *
 */
class BrowserPageGCTask extends WeakReference<BrowserPage> implements Runnable {

    private final String externalDrivePath;

    private final String subDirName;

    private final Cleaner.Cleanable cleanable;

    BrowserPageGCTask(final BrowserPage referent, final ReferenceQueue<? super BrowserPage> q) {
        super(referent, q);
        externalDrivePath = referent.getExternalDrivePath();
        subDirName = referent.getInstanceId();
        cleanable = WffConfiguration.secondaryCleaner().register(referent, this);
    }

    @Override
    public void run() {
        FileUtil.removeDirRecursively(externalDrivePath, subDirName);
    }

    /**
     * @since 12.0.10
     */
    void clean() {
        cleanable.clean();
    }

}
