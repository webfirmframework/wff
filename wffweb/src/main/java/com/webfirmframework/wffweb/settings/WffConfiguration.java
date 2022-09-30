/*
 * Copyright 2014-2022 Web Firm Framework
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
package com.webfirmframework.wffweb.settings;

import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author WFF
 * @version 1.0.0
 * @since 1.0.0
 */
public class WffConfiguration {

    private static boolean debugMode;
    private static boolean directionWarningOn;

    private static final Executor VIRTUAL_THREAD_EXECUTOR;

    static {
        ExecutorService tempExecutor = null;
        try {
            // TODO ManagementFactory requires java.management module so remove this line
            // later and the module entry from module-info.java as well
            final List<String> inputArguments = ManagementFactory.getRuntimeMXBean().getInputArguments();
            if (Integer.parseInt(System.getProperty("java.vm.specification.version", "17")) >= 19
                    && (inputArguments.contains("--enable-preview")
                            || inputArguments.contains("--enable-virtual-thread"))
                    && Executors.class.getMethod("newVirtualThreadPerTaskExecutor")
                            .invoke(null) instanceof final ExecutorService executor) {
                tempExecutor = executor;
            }
        } catch (final Exception e) {
            // NOP
        }
        VIRTUAL_THREAD_EXECUTOR = tempExecutor;
    }

    /**
     * @return the debugMode
     * @author WFF
     * @since 1.0.0
     */
    public static boolean isDebugMode() {
        return debugMode;
    }

    /**
     * @param debugMode the debugMode to set
     * @author WFF
     * @since 1.0.0
     */
    public static void setDebugMode(final boolean debugMode) {
        WffConfiguration.debugMode = debugMode;
    }

    /**
     * @return the directionWarningOn
     * @author WFF
     * @since 1.0.0
     */
    public static boolean isDirectionWarningOn() {
        return directionWarningOn;
    }

    /**
     * gives warning message on inappropriate usage of code if it is set to true.
     * NB:- its implementation is not finished yet so it may give unwanted warning.
     *
     * @param directionWarningOn the directionWarningOn to set
     * @author WFF
     * @since 1.0.0
     */
    public static void setDirectionWarningOn(final boolean directionWarningOn) {
        WffConfiguration.directionWarningOn = directionWarningOn;
    }

    /**
     * @return the virtual thread per task executor if available otherwise null.
     * @since 12.0.0-beta.7
     */
    public static Executor getVirtualThreadExecutor() {
        return VIRTUAL_THREAD_EXECUTOR;
    }
}
