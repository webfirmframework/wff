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

    private static Set<String> functionNames;

    private static Set<String> variableNames;

    private static volatile int functionId = 0;

    private static volatile int variableId = 0;

    static {

        if (PRODUCTION_MODE) {

            functionNames = new LinkedHashSet<String>();
            variableNames = new LinkedHashSet<String>();

            // should be in descending order of the value length

            functionNames.add("getLengthOfOptimizedBytesFromInt");
            functionNames.add("concatArrayValuesFromPosition");
            functionNames.add("extractValuesFromValueBytes");
            functionNames.add("getDoubleFromOptimizedBytes");
            functionNames.add("parseWffBinaryMessageBytes");
            functionNames.add("getAttrUpdatedWffBMBytes");
            functionNames.add("getWffBinaryMessageBytes");
            functionNames.add("getWffBinaryMessageBytes");
            functionNames.add("getIntFromOptimizedBytes");
            functionNames.add("getOptimizedBytesFromInt");
            functionNames.add("getTagByTagNameAndWffId");
            functionNames.add("getTagDeletedWffBMBytes");
            functionNames.add("createTagFromWffBMBytes");
            functionNames.add("getTagCreatedWffBMBytes");
            functionNames.add("getTagByTagNameAndWffId");
            functionNames.add("createTagFromWffBMBytes");
            functionNames.add("getWffIdFromWffIdBytes");
            functionNames.add("getWffIdFromWffIdBytes");
            functionNames.add("getWffIdBytesFromTag");
            functionNames.add("getWffIdBytesFromTag");
            functionNames.add("getLastBytesFromInt");
            functionNames.add("getAttributeUpdates");
            functionNames.add("getStringFromBytes");
            functionNames.add("splitAttrNameValue");
            functionNames.add("getBytesFromDouble");
            functionNames.add("getStringFromBytes");
            functionNames.add("splitAttrNameValue");
            functionNames.add("concatArrayValues");
            functionNames.add("getTaskNameValue");
            functionNames.add("getValueTypeByte");
            functionNames.add("getIntFromBytes");
            functionNames.add("getBytesFromInt");
            functionNames.add("getTagByWffId");
            functionNames.add("getTagByWffId");
            functionNames.add("invokeTasks");
            functionNames.add("recurChild");
            functionNames.add("invokeTask");

            variableNames.add("maxBytesLengthFromTotalBytes");
            variableNames.add("maxBytesLengthForAllValues");
            variableNames.add("totalNoOfBytesForAllValues");
            variableNames.add("indexInWffBinaryMessage");
            variableNames.add("valueLengthBytesLength");
            variableNames.add("wffBinaryMessageBytes");
            variableNames.add("maxNoValueLengthBytes");
            variableNames.add("attrNameAndValueBytes");
            variableNames.add("nameLengthBytesLength");
            variableNames.add("extractEachValueBytes");
            variableNames.add("nameValueCallbackFun");
            variableNames.add("superParentNameValue");
            variableNames.add("currentParentTagName");
            variableNames.add("maxValuesBytesLength");
            variableNames.add("maxNoNameLengthBytes");
            variableNames.add("parentOfExistingTag");
            variableNames.add("attrNameValueBytes");
            variableNames.add("currentParentWffId");
            variableNames.add("maxNoOfValuesBytes");
            variableNames.add("callbackFunctions");
            variableNames.add("attrNameValueArry");
            variableNames.add("superParentValues");
            variableNames.add("indexOfSeparator");
            variableNames.add("argumentBMObject");
            variableNames.add("currentParentTag");
            variableNames.add("maxNoOfNameBytes");
            variableNames.add("valueLengthBytes");
            variableNames.add("methodNameBytes");
            variableNames.add("valueLegthBytes");
            variableNames.add("valuesToAppend");
            variableNames.add("parentDocIndex");
            variableNames.add("beforeTagWffId");
            variableNames.add("nameLegthBytes");
            variableNames.add("nodeValueBytes");
            variableNames.add("callbackFunId");
            variableNames.add("fromByteArray");
            variableNames.add("applicableTag");
            variableNames.add("beforeTagName");
            variableNames.add("sOrCUtf8Bytes");
            variableNames.add("appendToArray");
            variableNames.add("lastNoOfBytes");
            variableNames.add("parentTagName");
            variableNames.add("nodeNameBytes");
            variableNames.add("attrNameValue");
            variableNames.add("messageIndex");
            variableNames.add("childTagName");
            variableNames.add("parentIndex");
            variableNames.add("doubleValue");
            variableNames.add("tagToRemove");
            variableNames.add("tagDocIndex");
            variableNames.add("zerothIndex");
            variableNames.add("valueLength");
            variableNames.add("secondIndex");
            variableNames.add("thirdIndex");
            variableNames.add("valueLegth");
            variableNames.add("childWffId");
            variableNames.add("intIdBytes");
            variableNames.add("wffIdBytes");
            variableNames.add("nameValues");
            variableNames.add("valueBytes");
            variableNames.add("valuesFrom");
            variableNames.add("wffBMBytes");
            variableNames.add("firstIndex");
            variableNames.add("methodName");
            variableNames.add("nameValue");
            variableNames.add("nameBytes");
            variableNames.add("beforeTag");
            variableNames.add("attrValue");
            variableNames.add("attrValue");
            variableNames.add("attrBytes");
            variableNames.add("htmlNodes");
            variableNames.add("wffTagId");
            variableNames.add("attrName");
            variableNames.add("childTag");
            variableNames.add("nameByte");
            variableNames.add("argBytes");
            variableNames.add("allTags");
            variableNames.add("allTags");
            variableNames.add("wffIds");
            variableNames.add("wffId");
            variableNames.add("intId");
            variableNames.add("sOrC");

            // should not replace, because there is webSocket's onmessage
            // function
            // VARIABLE_NAMES.add("message");

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

            if (PRODUCTION_MODE && functionNames != null
                    && variableNames != null) {

                for (final String name : functionNames) {
                    allOptimizedContent = allOptimizedContent.replace(name,
                            "f" + (++functionId));
                }

                for (final String name : variableNames) {
                    allOptimizedContent = allOptimizedContent.replace(name,
                            "v" + (++variableId));
                }

                functionNames = null;
                variableNames = null;

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
