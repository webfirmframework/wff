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
package com.webfirmframework.wffweb.tag.html.attribute.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.webfirmframework.wffweb.tag.html.attribute.AttributeNameConstants;
import com.webfirmframework.wffweb.tag.html.attribute.InternalAttrNameConstants;
import com.webfirmframework.wffweb.util.WffBinaryMessageUtil;

/**
 * @author WFF
 * @since 3.0.3
 */
public enum PreIndexedAttributeName {

    // NB: order should not be changed it is ordered by length then by ascending
    // order of name

    ID(AttributeNameConstants.ID),

    ALT(AttributeNameConstants.ALT),

    DIR(AttributeNameConstants.DIR),

    FOR(AttributeNameConstants.FOR),

    LOW(AttributeNameConstants.LOW),

    MAX(AttributeNameConstants.MAX),

    MIN(AttributeNameConstants.MIN),

    REL(AttributeNameConstants.REL),

    REV(AttributeNameConstants.REV),

    SRC(AttributeNameConstants.SRC),

    COLS(AttributeNameConstants.COLS),

    FACE(AttributeNameConstants.FACE),

    FORM(AttributeNameConstants.FORM),

    HIGH(AttributeNameConstants.HIGH),

    HREF(AttributeNameConstants.HREF),

    LANG(AttributeNameConstants.LANG),

    LIST(AttributeNameConstants.LIST),

    LOOP(AttributeNameConstants.LOOP),

    NAME(AttributeNameConstants.NAME),

    OPEN(AttributeNameConstants.OPEN),

    ROLE(AttributeNameConstants.ROLE),

    ROWS(AttributeNameConstants.ROWS),

    SIZE(AttributeNameConstants.SIZE),

    STEP(AttributeNameConstants.STEP),

    TYPE(AttributeNameConstants.TYPE),

    WRAP(AttributeNameConstants.WRAP),

    ALIGN(AttributeNameConstants.ALIGN),

    ASYNC(AttributeNameConstants.ASYNC),

    CLASS(AttributeNameConstants.CLASS),

    COLOR(AttributeNameConstants.COLOR),

    DEFER(AttributeNameConstants.DEFER),

    ISMAP(AttributeNameConstants.ISMAP),

    MEDIA(AttributeNameConstants.MEDIA),

    MUTED(AttributeNameConstants.MUTED),

    NONCE(AttributeNameConstants.NONCE),

    ONCUT(AttributeNameConstants.ONCUT),

    SCOPE(AttributeNameConstants.SCOPE),

    SHAPE(AttributeNameConstants.SHAPE),

    SIZES(AttributeNameConstants.SIZES),

    STYLE(AttributeNameConstants.STYLE),

    TITLE(AttributeNameConstants.TITLE),

    VALUE(AttributeNameConstants.VALUE),

    // not required to index
    // DATA_(AttributeNameConstants.DATA),

    WIDTH(AttributeNameConstants.WIDTH),

    ACCEPT(AttributeNameConstants.ACCEPT),

    ACTION(AttributeNameConstants.ACTION),

    BORDER(AttributeNameConstants.BORDER),

    COORDS(AttributeNameConstants.COORDS),

    HEIGHT(AttributeNameConstants.HEIGHT),

    HIDDEN(AttributeNameConstants.HIDDEN),

    METHOD(AttributeNameConstants.METHOD),

    NOHREF(AttributeNameConstants.NOHREF),

    ONBLUR(AttributeNameConstants.ONBLUR),

    ONCOPY(AttributeNameConstants.ONCOPY),

    ONDRAG(AttributeNameConstants.ONDRAG),

    ONDROP(AttributeNameConstants.ONDROP),

    ONLOAD(AttributeNameConstants.ONLOAD),

    ONPLAY(AttributeNameConstants.ONPLAY),

    ONSHOW(AttributeNameConstants.ONSHOW),

    POSTER(AttributeNameConstants.POSTER),

    SORTED(AttributeNameConstants.SORTED),

    SRCSET(AttributeNameConstants.SRCSET),

    TARGET(AttributeNameConstants.TARGET),

    USEMAP(AttributeNameConstants.USEMAP),

    CHARSET(AttributeNameConstants.CHARSET),

    CHECKED(AttributeNameConstants.CHECKED),

    COLSPAN(AttributeNameConstants.COLSPAN),

    CONTENT(AttributeNameConstants.CONTENT),

    DEFAULT(AttributeNameConstants.DEFAULT),

    DIRNAME(AttributeNameConstants.DIRNAME),

    ENCTYPE(AttributeNameConstants.ENCTYPE),

    HEADERS(AttributeNameConstants.HEADERS),

    ONABORT(AttributeNameConstants.ONABORT),

    ONCLICK(AttributeNameConstants.ONCLICK),

    ONENDED(AttributeNameConstants.ONENDED),

    ONERROR(AttributeNameConstants.ONERROR),

    ONFOCUS(AttributeNameConstants.ONFOCUS),

    ONINPUT(AttributeNameConstants.ONINPUT),

    ONKEYUP(AttributeNameConstants.ONKEYUP),

    ONPASTE(AttributeNameConstants.ONPASTE),

    ONPAUSE(AttributeNameConstants.ONPAUSE),

    ONRESET(AttributeNameConstants.ONRESET),

    ONWHEEL(AttributeNameConstants.ONWHEEL),

    OPTIMUM(AttributeNameConstants.OPTIMUM),

    PATTERN(AttributeNameConstants.PATTERN),

    PRELOAD(AttributeNameConstants.PRELOAD),

    ROWSPAN(AttributeNameConstants.ROWSPAN),

    SANDBOX(AttributeNameConstants.SANDBOX),

    AUTOPLAY(AttributeNameConstants.AUTOPLAY),

    CONTROLS(AttributeNameConstants.CONTROLS),

    DATETIME(AttributeNameConstants.DATETIME),

    DISABLED(AttributeNameConstants.DISABLED),

    DOWNLOAD(AttributeNameConstants.DOWNLOAD),

    DROPZONE(AttributeNameConstants.DROPZONE),

    HREFLANG(AttributeNameConstants.HREFLANG),

    MULTIPLE(AttributeNameConstants.MULTIPLE),

    ONCHANGE(AttributeNameConstants.ONCHANGE),

    ONONLINE(AttributeNameConstants.ONONLINE),

    ONRESIZE(AttributeNameConstants.ONRESIZE),

    ONSCROLL(AttributeNameConstants.ONSCROLL),

    ONSEARCH(AttributeNameConstants.ONSEARCH),

    ONSEEKED(AttributeNameConstants.ONSEEKED),

    ONSELECT(AttributeNameConstants.ONSELECT),

    ONSUBMIT(AttributeNameConstants.ONSUBMIT),

    ONTOGGLE(AttributeNameConstants.ONTOGGLE),

    ONUNLOAD(AttributeNameConstants.ONUNLOAD),

    READONLY(AttributeNameConstants.READONLY),

    REQUIRED(AttributeNameConstants.REQUIRED),

    REVERSED(AttributeNameConstants.REVERSED),

    SELECTED(AttributeNameConstants.SELECTED),

    TABINDEX(AttributeNameConstants.TABINDEX),

    ACCESSKEY(AttributeNameConstants.ACCESSKEY),

    AUTOFOCUS(AttributeNameConstants.AUTOFOCUS),

    DRAGGABLE(AttributeNameConstants.DRAGGABLE),

    MAXLENGTH(AttributeNameConstants.MAXLENGTH),

    MINLENGTH(AttributeNameConstants.MINLENGTH),

    ONCANPLAY(AttributeNameConstants.ONCANPLAY),

    ONDRAGEND(AttributeNameConstants.ONDRAGEND),

    ONEMPTIED(AttributeNameConstants.ONEMPTIED),

    ONFOCUSIN(AttributeNameConstants.ONFOCUSIN),

    ONINVALID(AttributeNameConstants.ONINVALID),

    ONKEYDOWN(AttributeNameConstants.ONKEYDOWN),

    ONMOUSEUP(AttributeNameConstants.ONMOUSEUP),

    ONOFFLINE(AttributeNameConstants.ONOFFLINE),

    ONPLAYING(AttributeNameConstants.ONPLAYING),

    ONSEEKING(AttributeNameConstants.ONSEEKING),

    ONSTALLED(AttributeNameConstants.ONSTALLED),

    ONSTORAGE(AttributeNameConstants.ONSTORAGE),

    ONSUSPEND(AttributeNameConstants.ONSUSPEND),

    ONWAITING(AttributeNameConstants.ONWAITING),

    TRANSLATE(AttributeNameConstants.TRANSLATE),

    FORMACTION(AttributeNameConstants.FORMACTION),

    FORMMETHOD(AttributeNameConstants.FORMMETHOD),

    FORMTARGET(AttributeNameConstants.FORMTARGET),

    HTTP_EQUIV(AttributeNameConstants.HTTP_EQUIV),

    ONDBLCLICK(AttributeNameConstants.ONDBLCLICK),

    ONDRAGOVER(AttributeNameConstants.ONDRAGOVER),

    ONFOCUSOUT(AttributeNameConstants.ONFOCUSOUT),

    ONKEYPRESS(AttributeNameConstants.ONKEYPRESS),

    ONMOUSEOUT(AttributeNameConstants.ONMOUSEOUT),

    ONPAGEHIDE(AttributeNameConstants.ONPAGEHIDE),

    ONPAGESHOW(AttributeNameConstants.ONPAGESHOW),

    ONPOPSTATE(AttributeNameConstants.ONPOPSTATE),

    ONPROGRESS(AttributeNameConstants.ONPROGRESS),

    ONTOUCHEND(AttributeNameConstants.ONTOUCHEND),

    SPELLCHECK(AttributeNameConstants.SPELLCHECK),

    CELLPADDING(AttributeNameConstants.CELLPADDING),

    CELLSPACING(AttributeNameConstants.CELLSPACING),

    CONTEXTMENU(AttributeNameConstants.CONTEXTMENU),

    DATA_WFF_ID(InternalAttrNameConstants.DATA_WFF_ID),

    FORMENCTYPE(AttributeNameConstants.FORMENCTYPE),

    ONDRAGENTER(AttributeNameConstants.ONDRAGENTER),

    ONDRAGLEAVE(AttributeNameConstants.ONDRAGLEAVE),

    ONDRAGSTART(AttributeNameConstants.ONDRAGSTART),

    ONLOADSTART(AttributeNameConstants.ONLOADSTART),

    ONMOUSEDOWN(AttributeNameConstants.ONMOUSEDOWN),

    ONMOUSEMOVE(AttributeNameConstants.ONMOUSEMOVE),

    ONMOUSEOVER(AttributeNameConstants.ONMOUSEOVER),

    ONTOUCHMOVE(AttributeNameConstants.ONTOUCHMOVE),

    PLACEHOLDER(AttributeNameConstants.PLACEHOLDER),

    ANIMATIONEND(AttributeNameConstants.ANIMATIONEND),

    AUTOCOMPLETE(AttributeNameConstants.AUTOCOMPLETE),

    ONAFTERPRINT(AttributeNameConstants.ONAFTERPRINT),

    ONHASHCHANGE(AttributeNameConstants.ONHASHCHANGE),

    ONLOADEDDATA(AttributeNameConstants.ONLOADEDDATA),

    ONMOUSEENTER(AttributeNameConstants.ONMOUSEENTER),

    ONMOUSELEAVE(AttributeNameConstants.ONMOUSELEAVE),

    ONRATECHANGE(AttributeNameConstants.ONRATECHANGE),

    ONTIMEUPDATE(AttributeNameConstants.ONTIMEUPDATE),

    ONTOUCHSTART(AttributeNameConstants.ONTOUCHSTART),

    ONBEFOREPRINT(AttributeNameConstants.ONBEFOREPRINT),

    ONCONTEXTMENU(AttributeNameConstants.ONCONTEXTMENU),

    ONTOUCHCANCEL(AttributeNameConstants.ONTOUCHCANCEL),

    TRANSITIONEND(AttributeNameConstants.TRANSITIONEND),

    ACCEPT_CHARSET(AttributeNameConstants.ACCEPT_CHARSET),

    ANIMATIONSTART(AttributeNameConstants.ANIMATIONSTART),

    FORMNOVALIDATE(AttributeNameConstants.FORMNOVALIDATE),

    ONBEFOREUNLOAD(AttributeNameConstants.ONBEFOREUNLOAD),

    ONVOLUMECHANGE(AttributeNameConstants.ONVOLUMECHANGE),

    CONTENTEDITABLE(AttributeNameConstants.CONTENTEDITABLE),

    ONCANPLAYTHROUGH(AttributeNameConstants.ONCANPLAYTHROUGH),

    ONDURATIONCHANGE(AttributeNameConstants.ONDURATIONCHANGE),

    ONLOADEDMETADATA(AttributeNameConstants.ONLOADEDMETADATA),

    ANIMATIONITERATION(AttributeNameConstants.ANIMATIONITERATION);

    private static final PreIndexedAttributeName[] allValues;

    private static final PreIndexedAttributeName[] allEventAttributes;

    private final String attrName;

    private final int index;

    private final byte[] indexBytes;

    private static final Map<String, PreIndexedAttributeName> OBJ_BY_ATTR_NAME;

    private final boolean eventAttr;

    private int eventAttrIndex = -1;

    static {
        allValues = PreIndexedAttributeName.values();
        final float lf = 0.75F;
        final int capacity = (int) (allValues.length / lf) + 1;
        OBJ_BY_ATTR_NAME = new ConcurrentHashMap<>(capacity, lf, 1);
        int eventAttrIndexCount = 0;
        final List<PreIndexedAttributeName> eventAttrs = new ArrayList<>();
        for (final PreIndexedAttributeName each : allValues) {
            OBJ_BY_ATTR_NAME.put(each.attrName, each);
            if (each.eventAttr) {
                eventAttrs.add(each);
                each.eventAttrIndex = eventAttrIndexCount;
                eventAttrIndexCount++;
            }
        }

        allEventAttributes = eventAttrs.toArray(new PreIndexedAttributeName[eventAttrs.size()]);
    }

    /**
     * @param attrName
     * @since 3.0.3
     */
    PreIndexedAttributeName(final String attrName) {
        this.attrName = attrName;
        index = ordinal();
        indexBytes = WffBinaryMessageUtil.getOptimizedBytesFromInt(index);

        // should not use AttributeRegistry inside this class as it causes a
        // cyclic dependency
        // always returns null
        // final Map<String, Class<?>> map = AttributeRegistry
        // .getAttributeClassByAttrName();
        eventAttr = attrName.startsWith("on");
    }

    /**
     * @return the attribute name
     * @since 3.0.3
     */
    public String attrName() {
        return attrName;
    }

    /**
     * @return the index
     * @since 3.0.3
     */
    public int index() {
        return index;
    }

    /**
     * @return optimized bytes of index
     * @since 3.0.3
     */
    public byte[] indexBytes() {
        if (indexBytes.length == 1) {
            return new byte[] { indexBytes[0] };
        } else if (indexBytes.length == 2) {
            return new byte[] { indexBytes[0], indexBytes[1] };
        } else if (indexBytes.length == 3) {
            return new byte[] { indexBytes[0], indexBytes[1], indexBytes[2] };
        } else if (indexBytes.length == 4) {
            return new byte[] { indexBytes[0], indexBytes[1], indexBytes[2], indexBytes[3] };
        }
        return Arrays.copyOf(indexBytes, indexBytes.length);
    }

    /**
     * Only for internal purpose
     *
     * @return optimized bytes of index
     * @since 3.0.6
     */
    byte[] internalIndexBytes() {
        return indexBytes;
    }

    /**
     * @param attrName
     * @return the enum object
     * @since 3.0.6
     */
    static PreIndexedAttributeName forAttrName(final String attrName) {
        return OBJ_BY_ATTR_NAME.get(attrName);
    }

    /**
     * for internal use
     *
     * @param index the index got by {@link PreIndexedAttributeName#index()}
     * @return the PreIndexedAttributeName object at the given index
     * @since 3.0.15
     */
    static PreIndexedAttributeName forAttrIndex(final int index) {
        return allValues[index];
    }

    /**
     * for internal use
     *
     * @param index pass the index got by
     *              {@linkplain PreIndexedAttributeName#eventAttrIndex}
     * @return the PreIndexedAttributeName object at the given index
     * @since 3.0.15
     */
    static PreIndexedAttributeName forEventAttrIndex(final int index) {
        return allEventAttributes[index];
    }

    /**
     * for internal use. never change it to public as the array values could be
     * modified.
     *
     * @return
     * @since 3.0.15
     */
    static PreIndexedAttributeName[] allEventAttributes() {
        return allEventAttributes;
    }

    /**
     * @return true if the attribute is an instance of EventAttribute.
     * @since 3.0.15
     */
    public boolean eventAttr() {
        return eventAttr;
    }

    /**
     * @return the index of event attribute if it is not an event attribute then -1
     *         will be returned.
     * @since 3.0.15
     */
    public int eventAttrIndex() {
        return eventAttrIndex;
    }

}
