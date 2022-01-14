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
package com.webfirmframework.wffweb.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author WFF
 * @since 3.0.18
 *
 */
public class FileUtil {

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
     */
    public static final boolean removeDirRecursively(final String basePath, final String... more) {
        final Path dirPath = Paths.get(basePath, more);
        boolean deleted = false;
        try {
            if (Files.exists(dirPath)) {
                final Deque<Path> q = Files.list(dirPath).collect(Collectors.toCollection(ArrayDeque::new));
                Path each;
                while ((each = q.poll()) != null) {
                    if (Files.isDirectory(each)) {
                        final List<Path> paths = Files.list(each).collect(Collectors.toList());
                        if (paths.size() > 0) {
                            for (final Path path : paths) {
                                q.addFirst(path);
                            }
                            q.addLast(each);
                        } else {
                            Files.deleteIfExists(each);
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
        return deleted;
    }

}
