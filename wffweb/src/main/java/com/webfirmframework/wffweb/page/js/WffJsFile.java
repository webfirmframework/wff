package com.webfirmframework.wffweb.page.js;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import com.webfirmframework.wffweb.page.Task;

/**
 * @author visruth
 *
 */
public enum WffJsFile {

    // should be kept in the same order
    WFF_GLOBAL("wffGlobal.js"),

    WFF_BM_UTIL("wffBMUtil.js"),

    WFF_TAG_UTIL("wffTagUtil.js"),

    WFF_BM_CRUID_UTIL("wffBMCRUIDUtil.js"),

    WFF_CLIENT_CRUD_UTIL("wffClientCRUDUtil.js"),

    WFF_WS("wffWS.js"),

    WFF_CLIENT_EVENTS("wffClientEvents.js"),

    WFF_SERVER_METHODS("wffServerMethods.js"),

    WFF_CLIENT_METHODS("wffClientMethods.js"),

    WFF_CLASSES("WffClasses.js")

    ;

    public static final Logger LOGGER = Logger
            .getLogger(WffJsFile.class.getName());

    // should be static final
    public static final boolean PRODUCTION_MODE = true;

    private static String allOptimizedContent;

    private String filename;

    private String optimizedFileContent;

    private static final Set<String> FUNCTION_NAMES = new HashSet<String>();

    private static volatile int functionId = 0;

    static {
        if (PRODUCTION_MODE) {
            FUNCTION_NAMES.add("getWffBinaryMessageBytes");
        }
    }

    private WffJsFile(final String filename) {
        this.filename = filename;
        init();
    }

    private void init() {
        try {

            final long fileLength = Files.size(
                    Paths.get(WffJsFile.class.getResource(filename).toURI()));

            final List<String> allLines = Files.readAllLines(
                    Paths.get(WffJsFile.class.getResource(filename).toURI()),
                    StandardCharsets.UTF_8);

            final StringBuilder builder = new StringBuilder((int) fileLength);
            for (final String eachLine : allLines) {
                // replacing double spaces with single space
                // by .replaceAll(" +", " ")
                // will not be file as there could e double space variable
                // initialization.
                String line = eachLine;
                final int indexOfCommentSlashes = line.indexOf("//");
                if (indexOfCommentSlashes != -1) {
                    if (indexOfCommentSlashes == 0) {
                        line = line.substring(0, indexOfCommentSlashes);
                    } else if (line
                            .indexOf("://") != (indexOfCommentSlashes - 1)) {
                        line = line.substring(0, indexOfCommentSlashes);
                    }
                }
                line = line.trim();

                builder.append(line);

                if (!line.isEmpty()) {
                    if (!PRODUCTION_MODE) {
                        builder.append('\n');
                    }
                }
            }

            if (PRODUCTION_MODE) {
                boolean containsComment = false;

                do {
                    containsComment = false;

                    final int indexOfSlashStarCommentStart = builder
                            .indexOf("/*");
                    if (indexOfSlashStarCommentStart != -1) {
                        final int indexOfStarCommentEnd = builder.indexOf("*/");

                        if (indexOfStarCommentEnd != -1
                                && indexOfSlashStarCommentStart < indexOfStarCommentEnd) {
                            builder.replace(indexOfSlashStarCommentStart,
                                    indexOfStarCommentEnd + 2, "");
                            containsComment = true;
                        }
                    }

                } while (containsComment);

                boolean containsLog = false;

                do {
                    containsLog = false;

                    final int indexOfConsole = builder.indexOf("console");
                    if (indexOfConsole != -1) {
                        final int indexOfSemiColon = builder.indexOf(");",
                                indexOfConsole);

                        if (indexOfSemiColon > indexOfConsole) {
                            builder.replace(indexOfConsole,
                                    indexOfSemiColon + 2, "");
                            containsLog = true;
                        }
                    }

                } while (containsLog);
            }

            optimizedFileContent = builder.toString().trim();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public static String getAllOptimizedContent(final String wsUrl) {

        if (allOptimizedContent != null) {
            return WFF_GLOBAL.optimizedFileContent.replace("${WS_URL}", wsUrl)
                    .replace("${TASK_VALUES}", Task.getJsObjectString())
                    + allOptimizedContent;
        }

        try {

            int totalContentLength = 0;
            for (final WffJsFile wffJsFile : WffJsFile.values()) {
                totalContentLength += wffJsFile.optimizedFileContent.length();
            }

            final StringBuilder builder = new StringBuilder(totalContentLength);

            final WffJsFile[] wffJsFiles = WffJsFile.values();
            for (int i = 1; i < wffJsFiles.length; i++) {
                builder.append('\n');
                builder.append(wffJsFiles[i].optimizedFileContent);
            }

            allOptimizedContent = builder.toString().trim();

            for (final String name : FUNCTION_NAMES) {
                allOptimizedContent = allOptimizedContent.replace(name,
                        "f" + (++functionId));
            }

            return WFF_GLOBAL.optimizedFileContent.replace("${WS_URL}", wsUrl)
                    .replace("${TASK_VALUES}", Task.getJsObjectString())
                    + allOptimizedContent;
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
