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
package com.webfirmframework.wffweb.server.page.js;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.webfirmframework.wffweb.WffRuntimeException;
import com.webfirmframework.wffweb.server.page.Task;
import com.webfirmframework.wffweb.tag.html.attribute.core.AttributeRegistry;
import com.webfirmframework.wffweb.tag.html.core.TagRegistry;
import com.webfirmframework.wffweb.util.StringBuilderUtil;
import com.webfirmframework.wffweb.util.StringUtil;

/**
 * @author WFF
 */
public enum WffJsFile {

    // should be kept in the same order
    JS_WORK_AROUND("jsWorkAround.js"),

    WFF_GLOBAL("wffGlobal.js"),

    WFF_BM_UTIL("wffBMUtil.js"),

    WFF_TAG_UTIL("wffTagUtil.js"),

    WFF_BM_CRUID_UTIL("wffBMCRUIDUtil.js"),

    WFF_CLIENT_CRUD_UTIL("wffClientCRUDUtil.js"),

    WFF_SERVER_METHODS("wffServerMethods.js"),

    WFF_CLIENT_METHODS("wffClientMethods.js"),

    WFF_CLASSES("WffClasses.js"),

    WFF_ASYNC("wffAsync.js"),

    WFF_BM_CLIENT_EVENTS("wffBMClientEvents.js"),

    WFF_WS("wffWS.js"),

    WFF_CLIENT_EVENTS("wffClientEvents.js");

    private static final String AUTOREMOVE_PARENT_SCRIPT = "document.currentScript.parentNode.removeChild(document.currentScript);";

    private static final Logger LOGGER = Logger.getLogger(WffJsFile.class.getName());

    // should be static final
    public static final boolean PRODUCTION_MODE = true;

    public static final boolean COMPRESSED_WFF_DATA = true;

    private static String allOptimizedContent;

    private final String filename;

    private final String optimizedFileContent;

    private static volatile Map<String, Boolean> functionAndVarNames;

    private static String[][] minifiableParts = { { "else {", "else{" }, { "} else", "}else" }, { "if (", "if(" },
            { ") {", "){" } };

    private static final String HEART_BEAT_JS = "setInterval(function(){try{wffWS.send([]);}catch(e){wffWS.closeSocket();}},\"${HEARTBEAT_INTERVAL}\");";

    /**
     * INDEXED_TAGS_ARRAY
     */
    private static final String NDXD_TGS;
    private static final String NDXD_ATRBS;
    private static final String NDXD_BLN_ATRBS;
    private static final String NDXD_VNT_ATRBS;

    // java record class is perfect for such use case
    private static final class FunctionOrVarName {
        private final String name;
        private final boolean function;

        private FunctionOrVarName(final String name, final boolean function) {
            this.name = name;
            this.function = function;
        }
    }

    static {

        final StringBuilder tagsArraySB = new StringBuilder();
        final List<String> tagNames = TagRegistry.getTagNames();
        for (final String tagName : tagNames) {
            tagsArraySB.append(",\"");
            tagsArraySB.append(tagName);
            tagsArraySB.append('"');
        }
        // length must always be greater than 0 otherwise bug
        NDXD_TGS = "[" + tagsArraySB.substring(1) + "]";

        final StringBuilder attrbsArraySB = new StringBuilder();
        final List<String> attrNames = AttributeRegistry.getAttributeNames();
        for (final String attrName : attrNames) {
            attrbsArraySB.append(",\"");
            attrbsArraySB.append(attrName);
            attrbsArraySB.append('"');
        }
        // length must always be greater than 0 otherwise bug
        NDXD_ATRBS = "[" + attrbsArraySB.substring(1) + "]";

        final StringBuilder boolAttrbsArraySB = new StringBuilder();
        for (final String attrName : AttributeRegistry.getBooleanAttributeNames()) {
            boolAttrbsArraySB.append(",\"");
            boolAttrbsArraySB.append(attrName);
            boolAttrbsArraySB.append('"');
        }

        // length must always be greater than 0 otherwise bug
        NDXD_BLN_ATRBS = "[" + boolAttrbsArraySB.substring(1) + "]";

        final StringBuilder eventAttrbsArraySB = new StringBuilder();
        for (final String attrName : AttributeRegistry.getEventAttributeNames()) {
            eventAttrbsArraySB.append(",\"");
            eventAttrbsArraySB.append(attrName);
            eventAttrbsArraySB.append('"');
        }

        // length must always be greater than 0 otherwise bug
        NDXD_VNT_ATRBS = "[" + eventAttrbsArraySB.substring(1) + "]";

        if (PRODUCTION_MODE) {

            // to sort in descending order of the length of the names
            // final Comparator<String> descendingLength = (a, b) -> Integer
            // .compare(b.length(), a.length());

            // old impl
            // final Comparator<String> descendingLength = (o1, o2) -> {
            // if (o1.equals(o2)) {
            // return 0;
            // }
            // // to sort in descending order of the length of the names
            // if (o1.length() > o2.length()) {
            // return -1;
            // }
            // if (o1.length() < o2.length()) {
            // return 1;
            // }
            // return -1;
            // };

            final List<String> functionNameList = new ArrayList<>(44);
            final List<String> variableNameList = new ArrayList<>(105);

            // should be in descending order of the value length

            functionNameList.add("getAttrNameValueFromCompressedBytes");
            functionNameList.add("createTagFromCompressedWffBMBytes");
            functionNameList.add("getLengthOfOptimizedBytesFromInt");
            functionNameList.add("concatArrayValuesFromPosition");
            functionNameList.add("getTagNameFromCompressedBytes");
            functionNameList.add("invokeAsyncWithPreFilterFunPD");
            functionNameList.add("extractValuesFromValueBytes");
            functionNameList.add("getDoubleFromOptimizedBytes");
            functionNameList.add("parseWffBinaryMessageBytes");
            functionNameList.add("invokeAsyncWithFilterFunPD");
            functionNameList.add("getAttrUpdatedWffBMBytes");
            functionNameList.add("getWffBinaryMessageBytes");
            functionNameList.add("getIntFromOptimizedBytes");
            functionNameList.add("getOptimizedBytesFromInt");
            functionNameList.add("isWffWindowEventSupported");
            functionNameList.add("getTagByTagNameAndWffId");
            functionNameList.add("getTagDeletedWffBMBytes");
            functionNameList.add("createTagFromWffBMBytes");
            functionNameList.add("getTagCreatedWffBMBytes");
            functionNameList.add("wffRemovePrevBPInstance");
            functionNameList.add("getChildByNthIndexBytes");
            functionNameList.add("invokeAsyncWithPreFunPD");
            functionNameList.add("getWffIdFromWffIdBytes");
            functionNameList.add("getWffIdFromWffIdBytes");
            functionNameList.add("extractEachValueBytes");
            functionNameList.add("getAttrBytesForServer");
            functionNameList.add("getWffIdBytesFromTag");
            functionNameList.add("getWffIdBytesFromTag");
            functionNameList.add("appendHtmlAsChildren");
            functionNameList.add("wffRemoveBPInstance");
            functionNameList.add("getLastBytesFromInt");
            functionNameList.add("getAttributeUpdates");
            functionNameList.add("getStringFromBytes");
            functionNameList.add("splitAttrNameValue");
            functionNameList.add("getBytesFromDouble");
            functionNameList.add("getStringFromBytes");
            functionNameList.add("concatArrayValues");
            functionNameList.add("getTaskNameValue");
            functionNameList.add("getValueTypeByte");
            functionNameList.add("onWffWindowClose");
            functionNameList.add("getValueTypeByte");
            functionNameList.add("getIntFromBytes");
            functionNameList.add("getBytesFromInt");
            functionNameList.add("getTagByWffId");
            functionNameList.add("getTagByWffId");
            functionNameList.add("invokeAsyncPD");
            functionNameList.add("invokeTasks");
            functionNameList.add("recurChild");
            functionNameList.add("invokeTask");

            variableNameList.add("wffRemovePrevBPInstanceInvoked");
            variableNameList.add("maxBytesLengthFromTotalBytes");
            variableNameList.add("totalNoOfBytesForAllValues");
            variableNameList.add("maxBytesLengthForAllValues");
            variableNameList.add("totalNoOfBytesForAllValues");
            variableNameList.add("lengOfOptmzdBytsOfAttrNam");
            variableNameList.add("indexInWffBinaryMessage");
            variableNameList.add("lengOfOptmzdBytsOfTgNam");
            variableNameList.add("valueLengthBytesLength");
            variableNameList.add("nameLengthBytesLength");
            variableNameList.add("wffBinaryMessageBytes");
            variableNameList.add("maxNoValueLengthBytes");
            variableNameList.add("attrNameAndValueBytes");
            variableNameList.add("nameLengthBytesLength");
            variableNameList.add("extractEachValueBytes");
            variableNameList.add("nameLengthBytesLength");
            variableNameList.add("maxNoNameLengthBytes");
            variableNameList.add("nameValueCallbackFun");
            variableNameList.add("superParentNameValue");
            variableNameList.add("currentParentTagName");
            variableNameList.add("maxValuesBytesLength");
            variableNameList.add("maxNoNameLengthBytes");
            variableNameList.add("attrNamNdxOptmzdByts");
            // child index optimized int bytes
            variableNameList.add("chldNdxOptmzdIntByts");
            variableNameList.add("parentOfExistingTag");
            variableNameList.add("maxNoOfValuesBytes");
            variableNameList.add("wffInstanceIdBytes");
            variableNameList.add("attrNameValueBytes");
            variableNameList.add("currentParentWffId");
            variableNameList.add("tgNamNdxOptmzdByts");
            variableNameList.add("tgNamNdxOptmzdByts");
            variableNameList.add("callbackFunctions");
            variableNameList.add("attrNameValueArry");
            variableNameList.add("superParentValues");
            variableNameList.add("wffOnWindowClosed");
            variableNameList.add("indexOfSeparator");
            variableNameList.add("argumentBMObject");
            variableNameList.add("currentParentTag");
            variableNameList.add("maxNoOfNameBytes");
            variableNameList.add("valueLengthBytes");
            variableNameList.add("methodNameBytes");
            variableNameList.add("valueLegthBytes");
            variableNameList.add("bmObjOrArrBytes");
            variableNameList.add("nameLengthBytes");
            variableNameList.add("valuesToAppend");
            variableNameList.add("parentDocIndex");
            variableNameList.add("beforeTagWffId");
            variableNameList.add("nameLegthBytes");
            variableNameList.add("nodeValueBytes");
            variableNameList.add("nameLegthBytes");
            variableNameList.add("taskNameValues");
            variableNameList.add("taskNameValue");
            variableNameList.add("callbackFunId");
            variableNameList.add("fromByteArray");
            variableNameList.add("applicableTag");
            variableNameList.add("beforeTagName");
            variableNameList.add("sOrCUtf8Bytes");
            variableNameList.add("appendToArray");
            variableNameList.add("lastNoOfBytes");
            variableNameList.add("parentTagName");
            variableNameList.add("nodeNameBytes");
            variableNameList.add("attrNameValue");
            variableNameList.add("messageIndex");
            variableNameList.add("childTagName");
            variableNameList.add("tagNameBytes");
            variableNameList.add("reqBytsLngth");
            variableNameList.add("reqBytsLngth");
            variableNameList.add("parentIndex");
            variableNameList.add("doubleValue");
            variableNameList.add("tagToRemove");
            variableNameList.add("tagDocIndex");
            variableNameList.add("zerothIndex");
            variableNameList.add("valueLength");
            variableNameList.add("secondIndex");
            variableNameList.add("attrValByts");
            variableNameList.add("attrNamByts");
            variableNameList.add("attrNmOrNdx");
            variableNameList.add("thirdIndex");
            variableNameList.add("valueLegth");
            variableNameList.add("childWffId");
            variableNameList.add("intIdBytes");
            variableNameList.add("wffIdBytes");
            variableNameList.add("nameValues");
            variableNameList.add("valueBytes");
            variableNameList.add("valuesFrom");
            variableNameList.add("wffBMBytes");
            variableNameList.add("firstIndex");
            variableNameList.add("methodName");
            variableNameList.add("attrNamNdx");
            variableNameList.add("attrValLen");
            variableNameList.add("nameValue");
            variableNameList.add("nameBytes");
            variableNameList.add("beforeTag");
            variableNameList.add("attrValue");
            variableNameList.add("attrBytes");
            variableNameList.add("htmlNodes");
            variableNameList.add("wffTagId");
            variableNameList.add("attrName");
            variableNameList.add("childTag");
            variableNameList.add("nameByte");
            variableNameList.add("argBytes");
            variableNameList.add("tgNamNdx");
            variableNameList.add("allTags");
            variableNameList.add("allTags");
            variableNameList.add("wffIds");
            variableNameList.add("wffId");
            variableNameList.add("intId");
            variableNameList.add("sOrC");

            final List<FunctionOrVarName> functionAndVarNameList = new ArrayList<>(
                    functionNameList.size() + variableNameList.size());
            for (final String each : functionNameList) {
                functionAndVarNameList.add(new FunctionOrVarName(each, true));
            }
            for (final String each : variableNameList) {
                functionAndVarNameList.add(new FunctionOrVarName(each, false));
            }

            // to sort in descending order of the length of the names
            final Comparator<FunctionOrVarName> descendingLength = (a, b) -> Integer.compare(b.name.length(),
                    a.name.length());

            functionAndVarNameList.sort(descendingLength);

            // NB: passing descendingLength comparator as constructor argument
            // in TreeSet makes bug it also removes elements having same length
            functionAndVarNames = new LinkedHashMap<>(functionAndVarNameList.size());
            for (final FunctionOrVarName each : functionAndVarNameList) {
                functionAndVarNames.put(each.name, each.function);
            }

            // should not replace, because there is webSocket's onmessage
            // function
            // VARIABLE_NAMES.add("message");

            // these must be excluded
            // should not be included even by mistake
            functionAndVarNames.remove("wffSM");
            functionAndVarNames.remove("iawpff");
            functionAndVarNames.remove("iawpffpd");
            functionAndVarNames.remove("iawpf");
            functionAndVarNames.remove("iawpfpd");
            functionAndVarNames.remove("iawff");
            functionAndVarNames.remove("iawffpd");
            functionAndVarNames.remove("ia");
            functionAndVarNames.remove("iapd");
            functionAndVarNames.remove("a");
            functionAndVarNames.remove("b");
            functionAndVarNames.remove("c");
            functionAndVarNames.remove("d");
            functionAndVarNames.remove("e");
            functionAndVarNames.remove("f");
            functionAndVarNames.remove("g");
            functionAndVarNames.remove("h");
            functionAndVarNames.remove("action");
            functionAndVarNames.remove("perform");
        }
    }

    private WffJsFile(final String filename) {
        this.filename = filename;
        optimizedFileContent = buildOptimizedFileContent();
    }

    private String buildOptimizedFileContent() {

        try (final InputStream in = WffJsFile.class.getResourceAsStream(filename);
                InputStreamReader isr = new InputStreamReader(in, StandardCharsets.UTF_8);
                final BufferedReader reader = new BufferedReader(isr);) {

            // this might make java.nio.file.FileSystemNotFoundException in
            // production server.
            // final List<String> allLines = Files.readAllLines(
            // Paths.get(WffJsFile.class.getResource(filename).toURI()),
            // StandardCharsets.UTF_8);

            // this will might java.nio.file.FileSystemNotFoundException in
            // production server.
            // final long fileLength = Files.size(
            // Paths.get(WffJsFile.class.getResource(filename).toURI()));

            final long fileLength = in.available() > 0 ? in.available() : 16;

            final StringBuilder builder = new StringBuilder((int) fileLength);

            String eachLine;

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
                    } else if (line.indexOf("://") != (indexOfCommentSlashes - 1)) {
                        line = line.substring(0, indexOfCommentSlashes);
                    }
                }
                line = StringUtil.strip(line);

                builder.append(line);

                if (!line.isEmpty()) {
                    if (line.charAt(line.length() - 1) != ';') {
                        builder.append('\n');
                    } else if (!PRODUCTION_MODE) {
                        builder.append('\n');
                    }
                }
            }

            if (PRODUCTION_MODE) {
                boolean containsComment;

                do {
                    containsComment = false;

                    final int indexOfSlashStarCommentStart = builder.indexOf("/*");
                    if (indexOfSlashStarCommentStart != -1) {
                        final int indexOfStarCommentEnd = builder.indexOf("*/");

                        if (indexOfStarCommentEnd != -1 && indexOfSlashStarCommentStart < indexOfStarCommentEnd) {
                            builder.replace(indexOfSlashStarCommentStart, indexOfStarCommentEnd + 2, "");
                            containsComment = true;
                        }
                    }

                } while (containsComment);

                boolean containsLog;

                do {
                    containsLog = false;

                    final int indexOfConsole = builder.indexOf("console");
                    if (indexOfConsole != -1) {
                        final int indexOfSemiColon = builder.indexOf(");", indexOfConsole);

                        if (indexOfSemiColon > indexOfConsole) {
                            builder.replace(indexOfConsole, indexOfSemiColon + 2, "");
                            containsLog = true;
                        }
                    }

                } while (containsLog);
            }

            return StringBuilderUtil.getTrimmedString(builder);
        } catch (final RuntimeException | IOException e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
            throw new WffRuntimeException("Unable to build optimizedFileContent");
        }
    }

    /**
     * NB :- This method is only for internal use.
     *
     * @param wsUrl                   the complete websocket url
     * @param instanceId              the instanceId of browserPage
     * @param removePrevBPOnInitTab   true or false
     * @param removePrevBPOnClosetTab true or false
     * @param heartbeatInterval       in milliseconds
     * @param wsReconnectInterval     in milliseconds
     * @param autoremoveParentScript  true or false
     * @return the js string for the client
     * @author WFF
     */
    public static String getAllOptimizedContent(final String wsUrl, final String instanceId,
            final boolean removePrevBPOnInitTab, final boolean removePrevBPOnClosetTab, final int heartbeatInterval,
            final int wsReconnectInterval, final boolean autoremoveParentScript) {

        if (allOptimizedContent != null) {

            if (heartbeatInterval > 0) {
                if (autoremoveParentScript) {
                    return buildJsContentWithHeartbeat(wsUrl, instanceId, removePrevBPOnInitTab,
                            removePrevBPOnClosetTab, heartbeatInterval, wsReconnectInterval)
                                    .append(AUTOREMOVE_PARENT_SCRIPT).toString();
                }
                return buildJsContentWithHeartbeat(wsUrl, instanceId, removePrevBPOnInitTab, removePrevBPOnClosetTab,
                        heartbeatInterval, wsReconnectInterval).toString();
            }

            if (autoremoveParentScript) {
                return buildJsContentWithoutHeartbeat(wsUrl, instanceId, removePrevBPOnInitTab, removePrevBPOnClosetTab,
                        wsReconnectInterval).append(AUTOREMOVE_PARENT_SCRIPT).toString();
            }
            return buildJsContentWithoutHeartbeat(wsUrl, instanceId, removePrevBPOnInitTab, removePrevBPOnClosetTab,
                    wsReconnectInterval).toString();
        }

        try {

            int totalContentLength = 0;
            for (final WffJsFile wffJsFile : WffJsFile.values()) {
                totalContentLength += wffJsFile.optimizedFileContent.length();
            }

            final StringBuilder builder = new StringBuilder(totalContentLength);

            final WffJsFile[] wffJsFiles = WffJsFile.values();
            for (int i = 2; i < wffJsFiles.length; i++) {
                builder.append('\n').append(wffJsFiles[i].optimizedFileContent);
            }

            allOptimizedContent = StringBuilderUtil.getTrimmedString(builder);

            if (PRODUCTION_MODE && functionAndVarNames != null) {

                synchronized (WffJsFile.class) {

                    if (functionAndVarNames != null) {

                        int functionId = 0;
                        int variableId = 0;

                        for (final Entry<String, Boolean> entry : functionAndVarNames.entrySet()) {

                            final String minName = entry.getValue() ? "f" + (++functionId) : "v" + (++variableId);
                            allOptimizedContent = allOptimizedContent.replace(entry.getKey(), minName);
                        }

                        for (final String[] each : minifiableParts) {
                            allOptimizedContent = allOptimizedContent.replace(each[0], each[1]);
                        }

                        // there is bug while enabling this, also enable in Task
                        // for (final Task task : Task.getSortedTasks()) {
                        // allOptimizedContent = allOptimizedContent
                        // .replace(task.name(), task.getShortName());
                        // }

                        functionAndVarNames = null;
                        minifiableParts = null;

                    }

                }

            }

            if (heartbeatInterval > 0) {
                if (autoremoveParentScript) {
                    return buildJsContentWithHeartbeat(wsUrl, instanceId, removePrevBPOnInitTab,
                            removePrevBPOnClosetTab, heartbeatInterval, wsReconnectInterval)
                                    .append(AUTOREMOVE_PARENT_SCRIPT).toString();
                }
                return buildJsContentWithHeartbeat(wsUrl, instanceId, removePrevBPOnInitTab, removePrevBPOnClosetTab,
                        heartbeatInterval, wsReconnectInterval).toString();
            }

            if (autoremoveParentScript) {
                return buildJsContentWithoutHeartbeat(wsUrl, instanceId, removePrevBPOnInitTab, removePrevBPOnClosetTab,
                        wsReconnectInterval).append(AUTOREMOVE_PARENT_SCRIPT).toString();
            }
            return buildJsContentWithoutHeartbeat(wsUrl, instanceId, removePrevBPOnInitTab, removePrevBPOnClosetTab,
                    wsReconnectInterval).toString();
        } catch (final Exception e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }
        return "";
    }

    private static StringBuilder buildJsContentWithHeartbeat(final String wsUrl, final String instanceId,
            final boolean removePrevBPOnInitTab, final boolean removePrevBPOnClosetTab, final int heartbeatInterval,
            final int wsReconnectInterval) {
        return buildJsContentWithoutHeartbeat(wsUrl, instanceId, removePrevBPOnInitTab, removePrevBPOnClosetTab,
                wsReconnectInterval).append(
                        HEART_BEAT_JS.replace("\"${HEARTBEAT_INTERVAL}\"", Integer.toString(heartbeatInterval)));
    }

    private static StringBuilder buildJsContentWithoutHeartbeat(final String wsUrl, final String instanceId,
            final boolean removePrevBPOnInitTab, final boolean removePrevBPOnClosetTab, final int wsReconnectInterval) {

        final StringBuilder globalContentBuider = new StringBuilder(WFF_GLOBAL.optimizedFileContent);

        StringBuilderUtil.replaceFirst(globalContentBuider, "\"${CPRSD_DATA}\"", String.valueOf(COMPRESSED_WFF_DATA));

        StringBuilderUtil.replaceFirst(globalContentBuider, "\"${NDXD_TGS}\"", NDXD_TGS);

        StringBuilderUtil.replaceFirst(globalContentBuider, "\"${NDXD_ATRBS}\"", NDXD_ATRBS);

        StringBuilderUtil.replaceFirst(globalContentBuider, "\"${NDXD_VNT_ATRBS}\"", NDXD_VNT_ATRBS);

        StringBuilderUtil.replaceFirst(globalContentBuider, "\"${NDXD_BLN_ATRBS}\"", NDXD_BLN_ATRBS);

        StringBuilderUtil.replaceFirst(globalContentBuider, "${WS_URL}", wsUrl);

        StringBuilderUtil.replaceFirst(globalContentBuider, "${INSTANCE_ID}", instanceId);

        StringBuilderUtil.replaceFirst(globalContentBuider, "\"${REMOVE_PREV_BP_ON_TABCLOSE}\"",
                String.valueOf(removePrevBPOnClosetTab));

        StringBuilderUtil.replaceFirst(globalContentBuider, "\"${REMOVE_PREV_BP_ON_INITTAB}\"",
                String.valueOf(removePrevBPOnInitTab));

        StringBuilderUtil.replaceFirst(globalContentBuider, "\"${TASK_VALUES}\"", Task.getJsObjectString());

        StringBuilderUtil.replaceFirst(globalContentBuider, "\"${WS_RECON}\"", String.valueOf(wsReconnectInterval));

        final String globalContent = globalContentBuider.toString();

        return new StringBuilder("var wffLog = console.log;").append(JS_WORK_AROUND.optimizedFileContent)
                .append(globalContent).append(allOptimizedContent);
    }

}
