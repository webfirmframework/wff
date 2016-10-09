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
 */
package com.webfirmframework.wffweb.server.page;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author WFF
 * @since 2.0.0
 */
public enum BrowserPageContext {

    INSTANCE;

    public static final Logger LOGGER = Logger
            .getLogger(BrowserPageContext.class.getName());

    /**
     * key httpSessionId, value : (key: instanceId, value: BrowserPage)
     */
    private final Map<String, Map<String, BrowserPage>> httpSessionIdBrowserPages;

    /**
     * key:- unique id for AbstractBrowserPage (AKA wff instance id in terms of
     * wff) value:- AbstractBrowserPage
     */
    private final Map<String, BrowserPage> instanceIdBrowserPage;

    /**
     * key:- unique id for AbstractBrowserPage (AKA wff instance id in terms of
     * wff) value:- httpSessionId
     */
    private final Map<String, String> instanceIdHttpSessionId;

    private BrowserPageContext() {
        httpSessionIdBrowserPages = new ConcurrentHashMap<String, Map<String, BrowserPage>>();
        instanceIdBrowserPage = new ConcurrentHashMap<String, BrowserPage>();
        instanceIdHttpSessionId = new ConcurrentHashMap<String, String>();
    }

    /**
     * @param httpSessionId
     * @param browserPage
     * @return the instance id (unique) of the browser page.
     * @since 2.0.0
     * @author WFF
     */
    public String addBrowserPage(final String httpSessionId,
            final BrowserPage browserPage) {

        Map<String, BrowserPage> browserPages = httpSessionIdBrowserPages
                .get(httpSessionId);

        synchronized (this) {
            if (browserPages == null) {
                synchronized (this) {
                    browserPages = new ConcurrentHashMap<String, BrowserPage>();
                }
                httpSessionIdBrowserPages.put(httpSessionId, browserPages);
            }
        }

        browserPages.put(browserPage.getInstanceId(), browserPage);

        instanceIdBrowserPage.put(browserPage.getInstanceId(), browserPage);

        instanceIdHttpSessionId.put(browserPage.getInstanceId(), httpSessionId);

        return browserPage.getInstanceId();
    }

    /**
     * @param httpSessionId
     * @param instanceId
     * @return
     * @since 2.0.0
     * @author WFF
     */
    public BrowserPage getBrowserPage(final String httpSessionId,
            final String instanceId) {

        final Map<String, BrowserPage> browserPages = httpSessionIdBrowserPages
                .get(httpSessionId);
        if (browserPages != null) {
            return browserPages.get(instanceId);
        }
        return null;
    }

    /**
     * gets the browserPage object for the given instance id.
     * BrowserPage#getBrowserPge(httpSessionId, instanceId) method is better
     * than this method in terms of performance.
     *
     * @param instanceId
     * @return browser page object if it exists otherwise null.
     * @since 2.0.0
     * @author WFF
     */
    public BrowserPage getBrowserPage(final String instanceId) {
        return instanceIdBrowserPage.get(instanceId);
    }

    /**
     * Gets all browser pages associated with this session.
     *
     * @param httpSessionId
     * @since 2.0.2
     * @author WFF
     * @return an unmodifiable map of BrowserPages associated with this session
     *         where key as instanceId and value as BrowserPage.
     */
    public Map<String, BrowserPage> getBrowserPages(
            final String httpSessionId) {

        final Map<String, BrowserPage> browserPages = httpSessionIdBrowserPages
                .get(httpSessionId);

        if (browserPages == null) {
            return null;
        }
        return Collections.unmodifiableMap(browserPages);
    }

    /**
     * This should be called when the http session is closed
     *
     * @param httpSessionId
     *            the session id of http session
     * @since 2.0.0
     * @author WFF
     */
    public void destroyContext(final String httpSessionId) {
        final Map<String, BrowserPage> httpSessionIdBrowserPage = httpSessionIdBrowserPages
                .remove(httpSessionId);

        if (httpSessionIdBrowserPage != null) {

            for (final BrowserPage browserPage : httpSessionIdBrowserPage
                    .values()) {
                instanceIdHttpSessionId.remove(browserPage.getInstanceId());
                instanceIdBrowserPage.remove(browserPage.getInstanceId());
            }

            httpSessionIdBrowserPage.clear();
        }

    }

    /**
     * this method should be called when the websocket is opened
     *
     * @param wffInstanceId
     *            the wffInstanceId which can be retried from the request
     *            parameter in websocket connection
     * @since 2.0.0
     * @author WFF
     * @return the {@code BrowserPage} object associated with this instance id,
     *         if the instanceId is associated with a closed http session it
     *         will return null.
     */
    public BrowserPage webSocketOpened(final String wffInstanceId) {

        final String httpSessionId = instanceIdHttpSessionId.get(wffInstanceId);

        if (httpSessionId != null) {
            final Map<String, BrowserPage> browserPages = httpSessionIdBrowserPages
                    .get(httpSessionId);
            if (browserPages != null) {
                return browserPages.get(wffInstanceId);
            }
        }

        return null;
    }

    /**
     * this method should be called when the websocket is closed
     *
     * @param wffInstanceId
     *            the wffInstanceId which can be retried from the request
     *            parameter in websocket connection
     * @since 2.0.0
     * @author WFF
     * @deprecated
     */
    @Deprecated
    public void webSocketClosed(final String wffInstanceId) {
        // NOP for future development
    }

    /**
     * this method should be called when the websocket is closed.
     *
     * @param wffInstanceId
     *            the wffInstanceId which can be retried from the request
     *            parameter in websocket connection
     * @param sessionId
     *            the websocket session id, i.e. the unique id of the websocket
     *            session which is given in
     *            {@code BrowserPage#addWebSocketPushListener} method.
     * @since 2.1.0
     * @author WFF
     * @return browserPage instance associated with this wffInstanceId
     */
    public BrowserPage webSocketClosed(final String wffInstanceId,
            final String sessionId) {
        final BrowserPage browserPage = instanceIdBrowserPage
                .get(wffInstanceId);
        if (browserPage != null) {
            browserPage.removeWebSocketPushListener(sessionId);
        }
        return browserPage;
    }

    /**
     * should be called when the httpsession is closed. The closed http session
     * id should be passed as an argument.
     *
     * @param httpSessionId
     * @since 2.0.0
     * @author WFF
     */
    public void httpSessionClosed(final String httpSessionId) {

        if (httpSessionId != null) {
            final Map<String, BrowserPage> browserPages = httpSessionIdBrowserPages
                    .get(httpSessionId);
            if (browserPages != null) {

                for (final String instanceId : browserPages.keySet()) {
                    instanceIdHttpSessionId.remove(instanceId);
                    instanceIdBrowserPage.remove(instanceId);
                }

                httpSessionIdBrowserPages.remove(httpSessionId);

            }
        } else {
            if (LOGGER.isLoggable(Level.WARNING)) {
                LOGGER.warning(
                        "The associatd HttpSession is alread closed for this instance id");
            }
        }

    }

    /**
     * This method is will be removed in the next version. Use
     * {@code webSocketMessaged} method instead of this method.
     *
     * @param wffInstanceId
     *            the wffInstanceId which can be retried from the request
     *            parameter in websocket connection.
     * @since 2.0.0
     * @author WFF
     * @deprecated use webSocketMessaged which does the same job.
     */
    @Deprecated
    public BrowserPage websocketMessaged(final String wffInstanceId,
            final byte[] message) {
        return webSocketMessaged(wffInstanceId, message);
    }

    /**
     * this method should be called when the websocket is closed
     *
     * @param wffInstanceId
     *            the wffInstanceId which can be retried from the request
     *            parameter in websocket connection.
     * @since 2.1.0
     * @author WFF
     */
    public BrowserPage webSocketMessaged(final String wffInstanceId,
            final byte[] message) {

        final BrowserPage browserPage = instanceIdBrowserPage
                .get(wffInstanceId);

        if (browserPage != null) {
            browserPage.websocketMessaged(message);
        }

        return browserPage;

    }

}