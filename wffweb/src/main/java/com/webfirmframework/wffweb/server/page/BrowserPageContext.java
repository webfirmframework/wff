/*
 * Copyright 2014-2021 Web Firm Framework
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

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.webfirmframework.wffweb.NullValueException;
import com.webfirmframework.wffweb.concurrent.MinIntervalExecutor;
import com.webfirmframework.wffweb.util.FileUtil;

/**
 * @author WFF
 * @since 2.0.0
 */
public enum BrowserPageContext {

	INSTANCE;

	private static final Logger LOGGER = Logger.getLogger(BrowserPageContext.class.getName());

	/**
	 * key httpSessionId, value : (key: instanceId, value: BrowserPage)
	 */
	private final Map<String, Map<String, BrowserPage>> httpSessionIdBrowserPages;

	/**
	 * key:- unique id for BrowserPage (AKA wff instance id in terms of wff) value:-
	 * BrowserPage
	 */
	private final Map<String, BrowserPage> instanceIdBrowserPage;

	/**
	 * key:- unique id for BrowserPage (AKA wff instance id in terms of wff) value:-
	 * BrowserPage this is only for websocket open close handling
	 */
	private final Map<String, BrowserPage> instanceIdBPForWS;

	/**
	 * key:- unique id for AbstractBrowserPage (AKA wff instance id in terms of wff)
	 * value:- httpSessionId
	 */
	private final Map<String, String> instanceIdHttpSessionId;

	/**
	 * key:- httpSessionId value:- HeartbeatManager
	 */
	private transient final Map<String, HeartbeatManager> heartbeatManagers;

	private transient ScheduledExecutorService scheduledExecutorService;

	private transient ScheduledFuture<?> autoCleanScheduled;

	private transient volatile MinIntervalExecutor autoCleanTaskExecutor;

	private transient final ReferenceQueue<BrowserPage> browserPageRQ = new ReferenceQueue<>();

	private transient final Map<Reference<? extends BrowserPage>, Runnable> gcTaskForBrowserPageRef;

	BrowserPageContext() {
		httpSessionIdBrowserPages = new ConcurrentHashMap<>();
		instanceIdBrowserPage = new ConcurrentHashMap<>();
		instanceIdBPForWS = new ConcurrentHashMap<>();
		instanceIdHttpSessionId = new ConcurrentHashMap<>();
		heartbeatManagers = new ConcurrentHashMap<>();
		gcTaskForBrowserPageRef = new ConcurrentHashMap<>();
		initConfig();
	}

	private void initConfig() {
		Runtime.getRuntime().addShutdownHook(new Thread(this::triggeredJVMShutdown));
	}

	private void triggeredJVMShutdown() {
		// write all tasks to be executed on JVM shutdown
		executeGCTasksForBrowserPage();

		for (final BrowserPage browserPage : instanceIdBrowserPage.values()) {
			final String externalDrivePath = browserPage.getExternalDrivePath();
			if (externalDrivePath != null) {
				FileUtil.removeDirRecursively(externalDrivePath, browserPage.getInstanceId());
			}
		}
	}

	/**
	 * @param httpSessionId
	 * @param browserPage
	 * @return the instance id (unique) of the browser page.
	 * @author WFF
	 * @since 2.0.0
	 */
	public String addBrowserPage(final String httpSessionId, final BrowserPage browserPage) {

		// passed 4 instead of 1 because the load factor is 0.75f

		final Map<String, BrowserPage> browserPages = httpSessionIdBrowserPages.computeIfAbsent(httpSessionId,
		        key -> new ConcurrentHashMap<>(4));

		browserPages.computeIfAbsent(browserPage.getInstanceId(), k -> {
			instanceIdBrowserPage.put(browserPage.getInstanceId(), browserPage);
			instanceIdHttpSessionId.put(browserPage.getInstanceId(), httpSessionId);
			return browserPage;
		});

		final String externalDrivePath = browserPage.getExternalDrivePath();

		if (externalDrivePath != null) {
			gcTaskForBrowserPageRef.put(new PhantomReference<>(browserPage, browserPageRQ),
			        () -> FileUtil.removeDirRecursively(externalDrivePath, browserPage.getInstanceId()));
		}

		runAutoClean();

		return browserPage.getInstanceId();
	}

	/**
	 * @param httpSessionId
	 * @param instanceId
	 * @return
	 * @author WFF
	 * @since 2.0.0
	 */
	public BrowserPage getBrowserPage(final String httpSessionId, final String instanceId) {

		final Map<String, BrowserPage> browserPages = httpSessionIdBrowserPages.get(httpSessionId);
		if (browserPages != null) {
			return browserPages.get(instanceId);
		}
		return null;
	}

	/**
	 * Gets the browserPage object for the given instance id. It also checks if the
	 * {@code browserPage} is valid.
	 * {@link BrowserPageContext#getBrowserPageIfValid(String, String)} method is
	 * better than this method in terms of performance. <br>
	 * Note: this operation is not atomic.
	 *
	 * @param httpSessionId
	 * @param instanceId
	 * @return the {@code browserPage} object if exists and its internal idle time
	 *         is not greater than or equal to the time set by
	 *         {@link BrowserPageContext#enableAutoClean} methods otherwise
	 *         {@code null}.
	 * @author WFF
	 * @since 3.0.16
	 */
	public BrowserPage getBrowserPageIfValid(final String httpSessionId, final String instanceId) {

		final Map<String, BrowserPage> browserPages = httpSessionIdBrowserPages.get(httpSessionId);
		if (browserPages != null) {
			final BrowserPage browserPage = browserPages.get(instanceId);
			final MinIntervalExecutor autoCleanTaskExecutor = this.autoCleanTaskExecutor;
			if (autoCleanTaskExecutor != null) {
				if ((System.currentTimeMillis() - browserPage.getLastClientAccessedTime()) >= autoCleanTaskExecutor
				        .minInterval()) {
					return null;
				}
			}
			return browserPage;
		}
		return null;
	}

	/**
	 * Gets the browserPage object for the given instance id.
	 * {@link BrowserPageContext#getBrowserPage(String, String)} method is better
	 * than this method in terms of performance.
	 *
	 * @param instanceId
	 * @return browser page object if it exists otherwise null.
	 * @author WFF
	 * @since 2.0.0
	 */
	public BrowserPage getBrowserPage(final String instanceId) {
		return instanceIdBrowserPage.get(instanceId);
	}

	/**
	 * gets the browserPage object for the given instance id. It also checks if the
	 * {@code browserPage} is valid.
	 * {@link BrowserPageContext#getBrowserPageIfValid(String, String)} method is
	 * better than this method in terms of performance.
	 *
	 * <br>
	 * Note: this operation is not atomic.
	 *
	 * @param instanceId
	 * @return the {@code browserPage} object if exists and its internal idle time
	 *         is not greater than or equal to the time set by
	 *         {@link BrowserPageContext#enableAutoClean} methods otherwise
	 *         {@code null}.
	 * @author WFF
	 * @since 3.0.16
	 */
	public BrowserPage getBrowserPageIfValid(final String instanceId) {
		final BrowserPage browserPage = instanceIdBrowserPage.get(instanceId);
		final MinIntervalExecutor autoCleanTaskExecutor = this.autoCleanTaskExecutor;
		if (autoCleanTaskExecutor != null) {
			if ((System.currentTimeMillis() - browserPage.getLastClientAccessedTime()) >= autoCleanTaskExecutor
			        .minInterval()) {
				return null;
			}
		}
		return browserPage;
	}

	/**
	 * Gets all browser pages associated with this session.
	 *
	 * @param httpSessionId
	 * @return an unmodifiable map of BrowserPages associated with this session
	 *         where key as instanceId and value as BrowserPage.
	 * @author WFF
	 * @since 2.0.2
	 */
	public Map<String, BrowserPage> getBrowserPages(final String httpSessionId) {

		final Map<String, BrowserPage> browserPages = httpSessionIdBrowserPages.get(httpSessionId);

		if (browserPages == null) {
			return null;
		}
		return Collections.unmodifiableMap(browserPages);
	}

	/**
	 * This should be called when the http session is closed
	 *
	 * @param httpSessionId the session id of http session
	 * @author WFF
	 * @since 2.0.0
	 */
	public void destroyContext(final String httpSessionId) {
		httpSessionClosed(httpSessionId);
	}

	/**
	 * this method should be called when the websocket is opened
	 *
	 * @param wffInstanceId the wffInstanceId which can be retried from the request
	 *                      parameter in websocket connection
	 * @return the {@code BrowserPage} object associated with this instance id, if
	 *         the instanceId is associated with a closed http session it will
	 *         return null.
	 * @author WFF
	 * @since 2.0.0
	 */
	public BrowserPage webSocketOpened(final String wffInstanceId) {

		final String httpSessionId = instanceIdHttpSessionId.get(wffInstanceId);

		if (httpSessionId != null) {
			final Map<String, BrowserPage> browserPages = httpSessionIdBrowserPages.get(httpSessionId);
			if (browserPages != null) {
				final BrowserPage browserPage = browserPages.get(wffInstanceId);
				if (browserPage != null) {
					instanceIdBPForWS.put(browserPage.getInstanceId(), browserPage);
					browserPage.setLastClientAccessedTime(System.currentTimeMillis());
				}
				return browserPage;
			}
		}

		return null;
	}

	/**
	 * this method should be called when the websocket is opened
	 *
	 * @param wffInstanceId           the wffInstanceId which can be retried from
	 *                                the request parameter in websocket connection
	 * @param computeHeartbeatManager the function to compute
	 *                                {@code HeartbeatManager}.
	 * @return the {@code WebSocketOpenedRecord} object. It contains
	 *         {@code BrowserPage} object associated with this {@code wffInstanceId}
	 *         and {@code HeartbeatManager} associated with its http session id. If
	 *         the {@code wffInstanceId} is associated with a closed http session or
	 *         {@code wffInstanceId} is associated with the {@code BrowserPage} is
	 *         already removed from the context then this method will return
	 *         {@code null}. If this method returns {@code null}, the given
	 *         {@code computeHeartbeatManager} will be ignored.
	 * @since 3.0.16
	 */
	public WebSocketOpenedRecord webSocketOpened(final String wffInstanceId,
	        final Function<String, HeartbeatManager> computeHeartbeatManager) {

		// NB: not directly saving computeHeartbeatManager to avoid GC prevention as it
		// is an external object which could have dependency over its outer class.

		final String httpSessionId = instanceIdHttpSessionId.get(wffInstanceId);

		if (httpSessionId != null) {
			final Map<String, BrowserPage> browserPages = httpSessionIdBrowserPages.get(httpSessionId);
			if (browserPages != null) {
				final BrowserPage browserPage = browserPages.get(wffInstanceId);
				if (browserPage != null) {
					instanceIdBPForWS.put(browserPage.getInstanceId(), browserPage);
					final long lastAccessedTime = System.currentTimeMillis();
					browserPage.setLastClientAccessedTime(lastAccessedTime);
					final HeartbeatManager hbManager = heartbeatManagers.computeIfAbsent(httpSessionId,
					        k -> computeHeartbeatManager.apply(k));
					hbManager.setLastAccessedTime(lastAccessedTime);
					return new WebSocketOpenedRecord(browserPage, hbManager);
				}
			}
		}

		return null;
	}

	/**
	 * No need to call this method unless there is a special case like extending the
	 * functionality of {@code BrowserPageContext} instance.
	 *
	 * @param wffInstanceId the instance id of {@code BrowserPage}.
	 * @since 3.0.16
	 */
	public void removeBrowserPage(final String wffInstanceId) {

		final String httpSessionId = instanceIdHttpSessionId.get(wffInstanceId);

		final Map<String, BrowserPage> browserPages = httpSessionIdBrowserPages.get(httpSessionId);

		final AtomicReference<BrowserPage> bpRef = new AtomicReference<>();
		browserPages.computeIfPresent(wffInstanceId, (k, bp) -> {
			instanceIdHttpSessionId.remove(wffInstanceId);
			instanceIdBrowserPage.remove(wffInstanceId);
			instanceIdBPForWS.remove(wffInstanceId);
			bpRef.set(bp);
			return null;
		});
		final BrowserPage bp = bpRef.get();
		if (bp != null) {
			try {
				bp.removedFromContext();
			} catch (final Throwable e) {
				if (LOGGER.isLoggable(Level.WARNING)) {
					LOGGER.log(Level.WARNING,
					        "The overridden method BrowserPage#removedFromContext threw an exception.", e);
				}
			}
			bp.clearWSListeners();
		}

	}

	/**
	 * If any object is idle for a time which is greater than or equal the given
	 * {@code maxIdleTimeout} it will be removed from the
	 * {@code BrowserPageContext}. Usually may not need to call this method directly
	 * because once {@code BrowserPageContext#startAutoClean} is called it is
	 * internally calling this {@code clean} method.
	 *
	 * @param maxIdleTimeout the max idle time to remove the objects. It is usually
	 *                       equal to the {@code maxIdleTimeout} of websocket
	 *                       session. It should be greater than the minInterval
	 *                       given in the {@code HeartbeatManager}.
	 * @since 3.0.16
	 */
	public void clean(final long maxIdleTimeout) {

		final long currentTime = System.currentTimeMillis();

		for (final Entry<String, Map<String, BrowserPage>> entry : httpSessionIdBrowserPages.entrySet()) {

			final String httpSessionId = entry.getKey();

			final HeartbeatManager heartbeatManager = heartbeatManagers.get(httpSessionId);

			if (heartbeatManager == null || heartbeatManager.minInterval() < maxIdleTimeout) {

				final Map<String, BrowserPage> browserPages = entry.getValue();

				final List<String> expiredWffInstanceIds = new LinkedList<>();
				boolean hbmExpired = true;
				for (final Entry<String, BrowserPage> bpEntry : browserPages.entrySet()) {
					if ((currentTime - bpEntry.getValue().getLastClientAccessedTime()) < maxIdleTimeout) {
						if (hbmExpired) {
							hbmExpired = false;
						}
					} else {
						expiredWffInstanceIds.add(bpEntry.getKey());
					}
				}

				for (final String wffInstanceId : expiredWffInstanceIds) {
					final AtomicReference<BrowserPage> bpRef = new AtomicReference<>();
					browserPages.computeIfPresent(wffInstanceId, (k, bp) -> {
						if ((currentTime - bp.getLastClientAccessedTime()) >= maxIdleTimeout) {
							instanceIdHttpSessionId.remove(wffInstanceId);
							instanceIdBrowserPage.remove(wffInstanceId);
							instanceIdBPForWS.remove(wffInstanceId);
							bpRef.set(bp);
							return null;
						}
						return bp;
					});
					final BrowserPage bp = bpRef.get();
					if (bp != null) {
						try {
							bp.removedFromContext();
						} catch (final Throwable e) {
							if (LOGGER.isLoggable(Level.WARNING)) {
								LOGGER.log(Level.WARNING,
								        "The overridden method BrowserPage#removedFromContext threw an exception.", e);
							}
						}
						bp.clearWSListeners();
					}

				}

				if (hbmExpired) {
					// to atomically remove hbm if expired
					heartbeatManagers.computeIfPresent(httpSessionId, (k, hbm) -> {
						if ((currentTime - hbm.getLastAccessedTime()) >= maxIdleTimeout
						        && hbm.minInterval() < maxIdleTimeout) {
							return null;
						}
						return hbm;
					});
				}

			}

		}

		executeGCTasksForBrowserPage();
	}

	private void executeGCTasksForBrowserPage() {
		Reference<? extends BrowserPage> browserPageRef;
		while ((browserPageRef = browserPageRQ.poll()) != null) {
			gcTaskForBrowserPageRef.get(browserPageRef).run();
			gcTaskForBrowserPageRef.remove(browserPageRef);
		}
	}

//    /**
//     * Runs a periodic clean operation when idle time of objects like
//     * {@code BrowserPage}, {@code HeartbeatManager} etc.. are greater than or equal
//     * to the given {@code maxIdleTimeout}.
//     *
//     * @param maxIdleTimeout the max idle time to remove the objects. It is usually
//     *                       equal to the {@code maxIdleTimeout} of websocket
//     *                       session. It should be greater than the minInterval
//     *                       given in the {@code HeartbeatManager}.
//     * @param period         cleaning period in milliseconds.
//     * @since 3.0.16
//     */
//    public void startAutoClean(final long maxIdleTimeout, final long period) {
//        autoCleanStartOrCancel(maxIdleTimeout, period, false);
//    }

	/**
	 * Runs a periodic clean operation when idle time of objects like
	 * {@code BrowserPage}, {@code HeartbeatManager} etc.. are greater than or equal
	 * to the given {@code maxIdleTimeout}. If it is started it will do the clean
	 * operation whenever appropriate, there is no fixed interval of time for this
	 * execution.
	 *
	 * @param maxIdleTimeout the max idle time in milliseconds to remove the
	 *                       objects. It is usually equal to the
	 *                       {@code maxIdleTimeout} of websocket session. It should
	 *                       be greater than the minInterval given in the
	 *                       {@code HeartbeatManager}.
	 * @since 3.0.16
	 */
	public void enableAutoClean(final long maxIdleTimeout) {
		if (maxIdleTimeout <= 0) {
			throw new IllegalArgumentException("maxIdleTimeout must be greater than 0");
		}
		autoCleanTaskExecutor = new MinIntervalExecutor(maxIdleTimeout, () -> clean(maxIdleTimeout));
	}

	/**
	 * Runs a periodic clean operation when idle time of objects like
	 * {@code BrowserPage}, {@code HeartbeatManager} etc.. are greater than or equal
	 * to the given {@code maxIdleTimeout}. If it is started it will do the clean
	 * operation whenever appropriate, there is no fixed interval of time for this
	 * execution.
	 *
	 * @param maxIdleTimeout the max idle time in milliseconds to remove the
	 *                       objects. It is usually equal to the
	 *                       {@code maxIdleTimeout} of websocket session. It should
	 *                       be greater than the minInterval given in the
	 *                       {@code HeartbeatManager}.
	 * @param executor       the executor object from which the thread will be
	 *                       obtained to run the clean process.
	 * @since 3.0.16
	 * @since 3.0.18 bug fix
	 */
	public void enableAutoClean(final long maxIdleTimeout, final Executor executor) {
		if (maxIdleTimeout <= 0) {
			throw new IllegalArgumentException("maxIdleTimeout must be greater than 0");
		}
		autoCleanTaskExecutor = new MinIntervalExecutor(executor, maxIdleTimeout, () -> clean(maxIdleTimeout));
	}

	/**
	 * @since 3.0.16
	 */
	public void disableAutoClean() {
		autoCleanTaskExecutor = null;
	}

	/**
	 * @return true if the auto clean is enabled by {@code enableAutoClean} method.
	 * @since 3.0.16
	 */
	public boolean isAutoCleanEnabled() {
		return autoCleanTaskExecutor != null;
	}

	private void runAutoClean() {
		final MinIntervalExecutor autoCleanTaskExecutor = this.autoCleanTaskExecutor;
		if (autoCleanTaskExecutor != null) {
			autoCleanTaskExecutor.runAsync();
		}
	}

	@SuppressWarnings("unused")
	private synchronized void autoCleanStartOrCancel(final long maxIdleTimeout, final long period,
	        final boolean cancelAutoClean) {

		if (period < maxIdleTimeout) {
			throw new IllegalArgumentException("period cannot be less than maxIdleTimeout.");
		}

		// to avoid executing startAutoClean and cancelAutoClean methods simultaneously
		// both method implementations are put together inside a single method.

		if (cancelAutoClean) {
			if (autoCleanScheduled != null && !autoCleanScheduled.isCancelled()) {
				autoCleanScheduled.cancel(false);
				autoCleanScheduled = null;
				if (scheduledExecutorService != null) {
					scheduledExecutorService.shutdown();
					scheduledExecutorService = null;
				}
			}
		} else {
			if (scheduledExecutorService == null) {
				scheduledExecutorService = Executors.newScheduledThreadPool(1);
			}

			final ScheduledFuture<?> autoCleanScheduledLocal = autoCleanScheduled;

			if (autoCleanScheduledLocal == null || autoCleanScheduledLocal.isCancelled()) {
				autoCleanScheduled = scheduledExecutorService.scheduleAtFixedRate(() -> clean(maxIdleTimeout), 0,
				        maxIdleTimeout, TimeUnit.MILLISECONDS);
			}
		}

	}

	/**
	 * @param httpSessionId
	 * @return the {@code HeartbeatManager} associated with this
	 *         {@code httpSessionId} or {@code null} if not available. If the last
	 *         usage time of {@code HeartbeatManager} of this http session is
	 *         greater than or equal to {@code maxIdleTimeout} given in the
	 *         {@code BrowserPageContext#enableAutoClean(long)} then this method
	 *         will return {@code null}.
	 * @since 3.0.16
	 */
	public HeartbeatManager getHeartbeatManagerForHttpSession(final String httpSessionId) {

		return heartbeatManagers.computeIfPresent(httpSessionId, (k, hbm) -> {
			final MinIntervalExecutor autoCleanTaskExecutor = this.autoCleanTaskExecutor;
			final long currentTime = System.currentTimeMillis();
			if (autoCleanTaskExecutor != null) {
				final long maxIdleTimeout = autoCleanTaskExecutor.minInterval();
				if ((currentTime - hbm.getLastAccessedTime()) >= maxIdleTimeout && hbm.minInterval() < maxIdleTimeout) {
					return null;
				}
			}
			hbm.setLastAccessedTime(currentTime);
			return hbm;
		});
	}

	/**
	 * @param wffInstanceId the wffInstanceId of {@code BrowserPage}.
	 * @return the {@code HeartbeatManager} associated with this
	 *         {@code httpSessionId} or {@code null} if not available. If the last
	 *         usage time of {@code HeartbeatManager} of this http session is
	 *         greater than or equal to {@code maxIdleTimeout} given in the
	 *         {@code BrowserPageContext#enableAutoClean(long)} then this method
	 *         will return {@code null}.
	 * @since 3.0.16
	 */
	public HeartbeatManager getHeartbeatManagerForBrowserPage(final String wffInstanceId) {
		final String httpSessionId = instanceIdHttpSessionId.get(wffInstanceId);
		if (httpSessionId != null) {
			return getHeartbeatManagerForHttpSession(httpSessionId);
		}
		return null;
	}

	/**
	 * this method should be called when the websocket is closed
	 *
	 * @param wffInstanceId the wffInstanceId which can be retried from the request
	 *                      parameter in websocket connection
	 * @author WFF
	 * @since 2.0.0
	 * @deprecated this method is for future development
	 */
	@Deprecated
	public void webSocketClosed(final String wffInstanceId) {
		// NOP for future development
	}

	/**
	 * this method should be called when the websocket is closed.
	 *
	 * @param wffInstanceId the wffInstanceId which can be retried from the request
	 *                      parameter in websocket connection
	 * @param sessionId     the websocket session id, i.e. the unique id of the
	 *                      websocket session which is given in
	 *                      {@code BrowserPage#addWebSocketPushListener} method.
	 * @return browserPage instance associated with this wffInstanceId
	 * @author WFF
	 * @since 2.1.0
	 */
	public BrowserPage webSocketClosed(final String wffInstanceId, final String sessionId) {
		final BrowserPage bp = instanceIdBPForWS.get(wffInstanceId);
		if (bp != null) {
			bp.removeWebSocketPushListener(sessionId);
			if (bp.getWsListener() == null) {
				instanceIdBPForWS.remove(wffInstanceId);
			}
		}

		return bp;
	}

	/**
	 * should be called when the httpsession is closed. The closed http session id
	 * should be passed as an argument.
	 *
	 * @param httpSessionId
	 * @author WFF
	 * @since 2.0.0
	 */
	public void httpSessionClosed(final String httpSessionId) {

		if (httpSessionId != null) {
			final Map<String, BrowserPage> browserPages = httpSessionIdBrowserPages.remove(httpSessionId);
			if (browserPages != null) {

				for (final String instanceId : browserPages.keySet()) {
					instanceIdHttpSessionId.remove(instanceId);
					final BrowserPage removedBrowserPage = instanceIdBrowserPage.remove(instanceId);
					instanceIdBPForWS.remove(instanceId);
					if (removedBrowserPage != null) {
						try {
							removedBrowserPage.removedFromContext();
						} catch (final Throwable e) {
							if (LOGGER.isLoggable(Level.WARNING)) {
								LOGGER.log(Level.WARNING,
								        "The overridden method BrowserPage#removedFromContext threw an exception.", e);
							}
						}
						removedBrowserPage.clearWSListeners();
					}
				}
				browserPages.clear();
			}

		} else {
			if (LOGGER.isLoggable(Level.WARNING)) {
				LOGGER.warning("The associatd HttpSession is alread closed for the id");
			}
		}
		runAutoClean();
	}

	/**
	 * This method is will be removed in the next version. Use
	 * {@code webSocketMessaged} method instead of this method.
	 *
	 * @param wffInstanceId the wffInstanceId which can be retried from the request
	 *                      parameter in websocket connection.
	 * @param message       the message received from websocket
	 * @author WFF
	 * @since 2.0.0
	 * @deprecated use webSocketMessaged which does the same job.
	 */
	@Deprecated
	public BrowserPage websocketMessaged(final String wffInstanceId, final byte[] message) {
		return webSocketMessaged(wffInstanceId, message);
	}

	/**
	 * this method should be called when the websocket is messaged.
	 *
	 * @param wffInstanceId the wffInstanceId which can be retried from the request
	 *                      parameter in websocket connection.
	 * @param message       the message received from websocket
	 * @author WFF
	 * @since 2.1.0
	 */
	public BrowserPage webSocketMessaged(final String wffInstanceId, final byte[] message) {

		final BrowserPage browserPage = instanceIdBrowserPage.get(wffInstanceId);

		if (browserPage != null) {
			browserPage.webSocketMessaged(message);
		}

		return browserPage;

	}

	/**
	 * removes browser page by the given instance id. This method is for internal
	 * usage.
	 *
	 * @param callerInstanceId instance id of the caller browserPage instance.
	 * @param instanceId       the instance id of the browser page which is indented
	 *                         to be removed.
	 * @since 2.1.4
	 */
	void removeBrowserPage(final String callerInstanceId, final String instanceId) {

		final String callerHttpSessionId = instanceIdHttpSessionId.get(callerInstanceId);

		final String httpSessionId = instanceIdHttpSessionId.get(instanceId);

		// this is a security checking
		// the caller session id must be
		// same as the session id of the instanceId
		// otherwise it's considered as a hacking.
		if (httpSessionId != null && httpSessionId.equals(callerHttpSessionId)) {

			final Map<String, BrowserPage> browserPages = httpSessionIdBrowserPages.get(httpSessionId);
			if (browserPages != null) {

				final AtomicReference<BrowserPage> bpRef = new AtomicReference<>();

				browserPages.computeIfPresent(instanceId, (k, bp) -> {
					instanceIdBrowserPage.remove(instanceId);
					instanceIdHttpSessionId.remove(instanceId);
					instanceIdBPForWS.remove(instanceId);
					bpRef.set(bp);
					return null;
				});
				final BrowserPage bp = bpRef.get();
				if (bp != null) {
					try {
						bp.removedFromContext();
					} catch (final Throwable e) {
						if (LOGGER.isLoggable(Level.WARNING)) {
							LOGGER.log(Level.WARNING,
							        "The overridden method BrowserPage#removedFromContext threw an exception.", e);
						}
					}
					bp.clearWSListeners();
				}
			}
		} else {
			if (LOGGER.isLoggable(Level.WARNING)) {
				LOGGER.warning("The callerInstanceId " + callerInstanceId + " tried to remove instanceId " + instanceId
				        + " BrowserPageContext");
			}
		}

	}

	/**
	 * Checks the existence of {@code browserPage} in this context.
	 *
	 * @param browserPage
	 * @return true if the given browserPage exists in the BrowserPageContext.
	 * @throws NullValueException if the given browserPage instance is null
	 * @author WFF
	 * @since 2.1.13
	 */
	public boolean exists(final BrowserPage browserPage) throws NullValueException {
		if (browserPage == null) {
			throw new NullValueException("browserPage instance cannot be null");
		}
		return browserPage.equals(instanceIdBrowserPage.get(browserPage.getInstanceId()));
	}

	/**
	 * Checks the existence of valid {@code browserPage} in this context.
	 *
	 * <br>
	 * Note: this operation is not atomic. The validity is time dependent, even if
	 * the method returns true {@code browserPage} could be invalid in the next
	 * moment. However, if it returns false it is trust worthy.
	 *
	 * @param browserPage
	 * @return true if the given browserPage exists in the BrowserPageContext and
	 *         has not expired. The {@code browserPage} instance will be considered
	 *         as invalid if the internal idle time of it is greater than or equal
	 *         to the time set by {@link BrowserPageContext#enableAutoClean}
	 *         methods.
	 * @throws NullValueException if the given browserPage instance is null
	 * @author WFF
	 * @since 3.0.16
	 */
	public boolean existsAndValid(final BrowserPage browserPage) throws NullValueException {
		if (browserPage == null) {
			throw new NullValueException("browserPage instance cannot be null");
		}

		final MinIntervalExecutor autoCleanTaskExecutor = this.autoCleanTaskExecutor;
		if (autoCleanTaskExecutor != null) {
			if ((System.currentTimeMillis() - browserPage.getLastClientAccessedTime()) >= autoCleanTaskExecutor
			        .minInterval()) {
				return false;
			}
		}

		return browserPage.equals(instanceIdBrowserPage.get(browserPage.getInstanceId()));
	}

}