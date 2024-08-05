/*
 * Copyright 2014-2024 Web Firm Framework
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
package com.webfirmframework.wffweb.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author WFF
 * @since 3.0.18
 *
 */
public final class FileUtil {

    private static final Logger LOGGER = Logger.getLogger(FileUtil.class.getName());

    private FileUtil() {
        throw new AssertionError();
    }

    /**
     * Deletes the basePath directory even if it is not empty.
     *
     * @param basePath
     * @param more     sub-directories
     * @return true if the basePath directory is deleted
     * @since 3.0.18
     * @since 12.0.0-beta.7 memory optimization improvements
     */
    public static boolean removeDirRecursively(final String basePath, final String... more) {
        final Path dirPath = Paths.get(basePath, more);

        boolean deleted = false;
        if (Files.exists(dirPath) && Files.isDirectory(dirPath)) {
            // even if it is false it should proceed
            try {
                removeFilesRecursivelyByWalk(dirPath);
                try (Stream<Path> pathsUnderDirPath = Files.list(dirPath)) {
                    final Deque<Path> q = pathsUnderDirPath.collect(Collectors.toCollection(ArrayDeque::new));
                    Path each;
                    while ((each = q.poll()) != null) {
                        if (Files.isDirectory(each)) {
                            try (Stream<Path> pathsOfEach = Files.list(each)) {
                                final List<Path> paths = pathsOfEach.toList();
                                if (!paths.isEmpty()) {
                                    for (final Path path : paths) {
                                        q.addFirst(path);
                                    }
                                    q.addLast(each);
                                } else {
                                    Files.deleteIfExists(each);
                                }
                            }
                        } else {
                            Files.deleteIfExists(each);
                        }
                    }
                    deleted = Files.deleteIfExists(dirPath);
                }
            } catch (final IOException e) {
                // NOP
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }

        return deleted;
    }

    private static void deleteIfExists(final Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (final IOException e) {
            // NOP
        }
    }

    /**
     * Deletes the basePath directory even if it is not empty.
     *
     * @param basePath
     * @param more     sub-directories
     * @return true if the basePath directory is deleted
     * @since 12.0.0-beta.7
     */
    public static boolean removeDirRecursivelyByWalk(final String basePath, final String... more) {
        final Path dirPath = Paths.get(basePath, more);
        if (Files.exists(dirPath) && Files.isDirectory(dirPath)) {
            try {
                removeFilesRecursivelyByWalk(dirPath);
                try (Stream<Path> walk = Files.walk(dirPath)) {
                    walk.sorted(Comparator.reverseOrder()).forEach(FileUtil::deleteIfExists);
                }
            } catch (final IOException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }

        return !Files.exists(dirPath);
    }

    private static void removeFilesRecursivelyByWalk(final Path dirPath) throws IOException {
        try (Stream<Path> walk = Files.walk(dirPath)) {
            walk.filter(Files::isRegularFile).forEach(FileUtil::deleteIfExists);
        }
    }

}
