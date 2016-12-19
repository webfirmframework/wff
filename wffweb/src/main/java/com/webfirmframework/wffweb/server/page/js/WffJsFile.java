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
package com.webfirmframework.wffweb.server.page.js;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.webfirmframework.wffweb.server.page.Task;

/**
 * @author WFF
 *
 */
public enum WffJsFile {

    // should be kept in the same order
    JS_WORK_AROUND("jsWorkAround.js"),

    WFF_GLOBAL("wffGlobal.js"),

    WFF_BM_UTIL("wffBMUtil.js"),

    WFF_TAG_UTIL("wffTagUtil.js"),

    WFF_BM_CRUID_UTIL("wffBMCRUIDUtil.js"),

    WFF_CLIENT_CRUD_UTIL("wffClientCRUDUtil.js"),

    WFF_WS("wffWS.js"),

    WFF_CLIENT_EVENTS("wffClientEvents.js"),

    WFF_SERVER_METHODS("wffServerMethods.js"),

    WFF_CLIENT_METHODS("wffClientMethods.js"),

    WFF_CLASSES("WffClasses.js"),

    WFF_ASYNC("wffAsync.js");

    public static final Logger LOGGER = Logger
            .getLogger(WffJsFile.class.getName());

    // should be static final
    public static final boolean PRODUCTION_MODE = true;

    private static String allOptimizedContent;

    private String filename;

    private String optimizedFileContent;

    private static Set<String> FUNCTION_NAMES;

    private static Set<String> VARIABLE_NAMES;

    private static volatile int functionId = 0;

    private static volatile int variableId = 0;

    static {

        if (PRODUCTION_MODE) {

            FUNCTION_NAMES = new LinkedHashSet<String>();
            VARIABLE_NAMES = new LinkedHashSet<String>();

            // should be in descending order of the value length

            FUNCTION_NAMES.add("getLengthOfOptimizedBytesFromInt");
            FUNCTION_NAMES.add("concatArrayValuesFromPosition");
            FUNCTION_NAMES.add("extractValuesFromValueBytes");
            FUNCTION_NAMES.add("getDoubleFromOptimizedBytes");
            FUNCTION_NAMES.add("parseWffBinaryMessageBytes");
            FUNCTION_NAMES.add("getAttrUpdatedWffBMBytes");
            FUNCTION_NAMES.add("getWffBinaryMessageBytes");
            FUNCTION_NAMES.add("getWffBinaryMessageBytes");
            FUNCTION_NAMES.add("getIntFromOptimizedBytes");
            FUNCTION_NAMES.add("getOptimizedBytesFromInt");
            FUNCTION_NAMES.add("getTagByTagNameAndWffId");
            FUNCTION_NAMES.add("getTagDeletedWffBMBytes");
            FUNCTION_NAMES.add("createTagFromWffBMBytes");
            FUNCTION_NAMES.add("getTagCreatedWffBMBytes");
            FUNCTION_NAMES.add("getTagByTagNameAndWffId");
            FUNCTION_NAMES.add("createTagFromWffBMBytes");
            FUNCTION_NAMES.add("getWffIdFromWffIdBytes");
            FUNCTION_NAMES.add("getWffIdFromWffIdBytes");
            FUNCTION_NAMES.add("getWffIdBytesFromTag");
            FUNCTION_NAMES.add("getWffIdBytesFromTag");
            FUNCTION_NAMES.add("getLastBytesFromInt");
            FUNCTION_NAMES.add("getAttributeUpdates");
            FUNCTION_NAMES.add("getStringFromBytes");
            FUNCTION_NAMES.add("splitAttrNameValue");
            FUNCTION_NAMES.add("getBytesFromDouble");
            FUNCTION_NAMES.add("getStringFromBytes");
            FUNCTION_NAMES.add("splitAttrNameValue");
            FUNCTION_NAMES.add("concatArrayValues");
            FUNCTION_NAMES.add("getTaskNameValue");
            FUNCTION_NAMES.add("getValueTypeByte");
            FUNCTION_NAMES.add("getIntFromBytes");
            FUNCTION_NAMES.add("getBytesFromInt");
            FUNCTION_NAMES.add("getTagByWffId");
            FUNCTION_NAMES.add("getTagByWffId");
            FUNCTION_NAMES.add("invokeTasks");
            FUNCTION_NAMES.add("recurChild");
            FUNCTION_NAMES.add("invokeTask");

            VARIABLE_NAMES.add("maxBytesLengthFromTotalBytes");
            VARIABLE_NAMES.add("maxBytesLengthForAllValues");
            VARIABLE_NAMES.add("indexInWffBinaryMessage");
            VARIABLE_NAMES.add("valueLengthBytesLength");
            VARIABLE_NAMES.add("wffBinaryMessageBytes");
            VARIABLE_NAMES.add("maxNoValueLengthBytes");
            VARIABLE_NAMES.add("attrNameAndValueBytes");
            VARIABLE_NAMES.add("nameLengthBytesLength");
            VARIABLE_NAMES.add("extractEachValueBytes");
            VARIABLE_NAMES.add("superParentNameValue");
            VARIABLE_NAMES.add("currentParentTagName");
            VARIABLE_NAMES.add("maxNoNameLengthBytes");
            VARIABLE_NAMES.add("attrNameValueBytes");
            VARIABLE_NAMES.add("currentParentWffId");
            VARIABLE_NAMES.add("maxNoOfValuesBytes");
            VARIABLE_NAMES.add("attrNameValueArry");
            VARIABLE_NAMES.add("superParentValues");
            VARIABLE_NAMES.add("indexOfSeparator");
            VARIABLE_NAMES.add("argumentBMObject");
            VARIABLE_NAMES.add("currentParentTag");
            VARIABLE_NAMES.add("maxNoOfNameBytes");
            VARIABLE_NAMES.add("valueLengthBytes");
            VARIABLE_NAMES.add("valueLegthBytes");
            VARIABLE_NAMES.add("valuesToAppend");
            VARIABLE_NAMES.add("beforeTagWffId");
            VARIABLE_NAMES.add("nodeValueBytes");
            VARIABLE_NAMES.add("fromByteArray");
            VARIABLE_NAMES.add("applicableTag");
            VARIABLE_NAMES.add("beforeTagName");
            VARIABLE_NAMES.add("sOrCUtf8Bytes");
            VARIABLE_NAMES.add("appendToArray");
            VARIABLE_NAMES.add("parentTagName");
            VARIABLE_NAMES.add("nodeNameBytes");
            VARIABLE_NAMES.add("attrNameValue");
            VARIABLE_NAMES.add("childTagName");
            VARIABLE_NAMES.add("parentIndex");
            VARIABLE_NAMES.add("tagToRemove");
            VARIABLE_NAMES.add("zerothIndex");
            VARIABLE_NAMES.add("secondIndex");
            VARIABLE_NAMES.add("thirdIndex");
            VARIABLE_NAMES.add("valueLegth");
            VARIABLE_NAMES.add("childWffId");
            VARIABLE_NAMES.add("intIdBytes");
            VARIABLE_NAMES.add("wffIdBytes");
            VARIABLE_NAMES.add("nameValues");
            VARIABLE_NAMES.add("valueBytes");
            VARIABLE_NAMES.add("valuesFrom");
            VARIABLE_NAMES.add("firstIndex");
            VARIABLE_NAMES.add("nameValue");
            VARIABLE_NAMES.add("nameBytes");
            VARIABLE_NAMES.add("beforeTag");
            VARIABLE_NAMES.add("attrValue");
            VARIABLE_NAMES.add("attrValue");
            VARIABLE_NAMES.add("htmlNodes");
            VARIABLE_NAMES.add("wffTagId");
            VARIABLE_NAMES.add("attrName");
            VARIABLE_NAMES.add("childTag");
            VARIABLE_NAMES.add("allTags");
            VARIABLE_NAMES.add("allTags");
            VARIABLE_NAMES.add("wffIds");
            VARIABLE_NAMES.add("wffId");
            VARIABLE_NAMES.add("intId");
            VARIABLE_NAMES.add("sOrC");

        }
    }

    private WffJsFile(final String filename) {
        this.filename = filename;
        init();
    }

    private void init() {
        try {

            // this might make java.nio.file.FileSystemNotFoundException in
            // production server.
            // final List<String> allLines = Files.readAllLines(
            // Paths.get(WffJsFile.class.getResource(filename).toURI()),
            // StandardCharsets.UTF_8);

            final InputStream in = WffJsFile.class
                    .getResourceAsStream(filename);
            final BufferedReader reader = new BufferedReader(
                    new InputStreamReader(in, "UTF-8"));

            // this will might java.nio.file.FileSystemNotFoundException in
            // production server.
            // final long fileLength = Files.size(
            // Paths.get(WffJsFile.class.getResource(filename).toURI()));

            final long fileLength = in.available() > 0 ? in.available() : 16;

            final StringBuilder builder = new StringBuilder((int) fileLength);

            String eachLine = null;

            while ((eachLine = reader.readLine()) != null) {
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
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    public static String getAllOptimizedContent(final String wsUrl) {

        if (allOptimizedContent != null) {
            return JS_WORK_AROUND.optimizedFileContent
                    + WFF_GLOBAL.optimizedFileContent
                            .replace("${WS_URL}", wsUrl)
                            .replace("\"${TASK_VALUES}\"",
                                    Task.getJsObjectString())
                    + allOptimizedContent;
        }

        try {

            int totalContentLength = 0;
            for (final WffJsFile wffJsFile : WffJsFile.values()) {
                totalContentLength += wffJsFile.optimizedFileContent.length();
            }

            final StringBuilder builder = new StringBuilder(totalContentLength);

            final WffJsFile[] wffJsFiles = WffJsFile.values();
            for (int i = 2; i < wffJsFiles.length; i++) {
                builder.append('\n');
                builder.append(wffJsFiles[i].optimizedFileContent);
            }

            allOptimizedContent = builder.toString().trim();

            if (PRODUCTION_MODE) {
                for (final String name : FUNCTION_NAMES) {
                    allOptimizedContent = allOptimizedContent.replace(name,
                            "f" + (++functionId));
                }

                for (final String name : VARIABLE_NAMES) {
                    allOptimizedContent = allOptimizedContent.replace(name,
                            "v" + (++variableId));
                }

            }

            return JS_WORK_AROUND.optimizedFileContent
                    + WFF_GLOBAL.optimizedFileContent
                            .replace("${WS_URL}", wsUrl)
                            .replace("\"${TASK_VALUES}\"",
                                    Task.getJsObjectString())
                    + allOptimizedContent;
        } catch (final Exception e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }
        return "";
    }
}
