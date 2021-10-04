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
package com.webfirmframework.wffweb.tag.html.core;

import java.util.Arrays;

import com.webfirmframework.wffweb.WffSecurityException;
import com.webfirmframework.wffweb.internal.security.object.SecurityClassConstants;
import com.webfirmframework.wffweb.tag.html.TagNameConstants;
import com.webfirmframework.wffweb.util.WffBinaryMessageUtil;

/**
 * @author WFF
 * @since 3.0.3
 */
public enum PreIndexedTagName {

    // NB: order should not be changed it is ordered by length in ascending and
    // then by name in ascending

    /**
     * not a tag name. For internal purpose only
     */
    HASH("#"),

    /**
     * not a tag name. For internal purpose only
     */
    AT("@"),

    A(TagNameConstants.A),

    B(TagNameConstants.B),

    I(TagNameConstants.I),

    P(TagNameConstants.P),

    Q(TagNameConstants.Q),

    S(TagNameConstants.S),

    U(TagNameConstants.U),

    BR(TagNameConstants.BR),

    DD(TagNameConstants.DD),

    DL(TagNameConstants.DL),

    DT(TagNameConstants.DT),

    EM(TagNameConstants.EM),

    H1(TagNameConstants.H1),

    H2(TagNameConstants.H2),

    H3(TagNameConstants.H3),

    H4(TagNameConstants.H4),

    H5(TagNameConstants.H5),

    H6(TagNameConstants.H6),

    HR(TagNameConstants.HR),

    LI(TagNameConstants.LI),

    OL(TagNameConstants.OL),

    RP(TagNameConstants.RP),

    RT(TagNameConstants.RT),

    TD(TagNameConstants.TD),

    TH(TagNameConstants.TH),

    TR(TagNameConstants.TR),

    UL(TagNameConstants.UL),

    BDI(TagNameConstants.BDI),

    BDO(TagNameConstants.BDO),

    COL(TagNameConstants.COL),

    DEL(TagNameConstants.DEL),

    DFN(TagNameConstants.DFN),

    DIV(TagNameConstants.DIV),

    IMG(TagNameConstants.IMG),

    INS(TagNameConstants.INS),

    KBD(TagNameConstants.KBD),

    MAP(TagNameConstants.MAP),

    NAV(TagNameConstants.NAV),

    PRE(TagNameConstants.PRE),

    QFN(TagNameConstants.QFN),

    SUB(TagNameConstants.SUB),

    SUP(TagNameConstants.SUP),

    SVG(TagNameConstants.SVG),

    VAR(TagNameConstants.VAR),

    WBR(TagNameConstants.WBR),

    ABBR(TagNameConstants.ABBR),

    AREA(TagNameConstants.AREA),

    BASE(TagNameConstants.BASE),

    BODY(TagNameConstants.BODY),

    CITE(TagNameConstants.CITE),

    CODE(TagNameConstants.CODE),

    DATA(TagNameConstants.DATA),

    FORM(TagNameConstants.FORM),

    HEAD(TagNameConstants.HEAD),

    HTML(TagNameConstants.HTML),

    LINE(TagNameConstants.LINE),

    LINK(TagNameConstants.LINK),

    MAIN(TagNameConstants.MAIN),

    MARK(TagNameConstants.MARK),

    MATH(TagNameConstants.MATH),

    MENU(TagNameConstants.MENU),

    META(TagNameConstants.META),

    PATH(TagNameConstants.PATH),

    RECT(TagNameConstants.RECT),

    RUBY(TagNameConstants.RUBY),

    SAMP(TagNameConstants.SAMP),

    SPAN(TagNameConstants.SPAN),

    TEXT(TagNameConstants.TEXT),

    TIME(TagNameConstants.TIME),

    ASIDE(TagNameConstants.ASIDE),

    AUDIO(TagNameConstants.AUDIO),

    EMBED(TagNameConstants.EMBED),

    INPUT(TagNameConstants.INPUT),

    LABEL(TagNameConstants.LABEL),

    METER(TagNameConstants.METER),

    PARAM(TagNameConstants.PARAM),

    SMALL(TagNameConstants.SMALL),

    STYLE(TagNameConstants.STYLE),

    TABLE(TagNameConstants.TABLE),

    TBODY(TagNameConstants.TBODY),

    TFOOT(TagNameConstants.TFOOT),

    THEAD(TagNameConstants.THEAD),

    /**
     * title
     */
    TITLE(TagNameConstants.TITLE_TAG),

    TRACK(TagNameConstants.TRACK),

    VIDEO(TagNameConstants.VIDEO),

    BUTTON(TagNameConstants.BUTTON),

    CANVAS(TagNameConstants.CANVAS),

    CIRCLE(TagNameConstants.CIRCLE),

    DIALOG(TagNameConstants.DIALOG),

    FIGURE(TagNameConstants.FIGURE),

    FOOTER(TagNameConstants.FOOTER),

    HEADER(TagNameConstants.HEADER),

    HGROUP(TagNameConstants.HGROUP),

    IFRAME(TagNameConstants.IFRAME),

    KEYGEN(TagNameConstants.KEYGEN),

    LEGEND(TagNameConstants.LEGEND),

    OBJECT(TagNameConstants.OBJECT),

    OPTION(TagNameConstants.OPTION),

    OUTPUT(TagNameConstants.OUTPUT),

    SCRIPT(TagNameConstants.SCRIPT),

    SELECT(TagNameConstants.SELECT),

    SOURCE(TagNameConstants.SOURCE),

    STRONG(TagNameConstants.STRONG),

    ADDRESS(TagNameConstants.ADDRESS),

    ARTICLE(TagNameConstants.ARTICLE),

    CAPTION(TagNameConstants.CAPTION),

    DETAILS(TagNameConstants.DETAILS),

    ELLIPSE(TagNameConstants.ELLIPSE),

    PICTURE(TagNameConstants.PICTURE),

    POLYGON(TagNameConstants.POLYGON),

    SECTION(TagNameConstants.SECTION),

    SUMMARY(TagNameConstants.SUMMARY),

    BASEFONT(TagNameConstants.BASEFONT),

    COLGROUP(TagNameConstants.COLGROUP),

    DATALIST(TagNameConstants.DATALIST),

    FIELDSET(TagNameConstants.FIELDSET),

    MENUITEM(TagNameConstants.MENUITEM),

    NOSCRIPT(TagNameConstants.NOSCRIPT),

    OPTGROUP(TagNameConstants.OPTGROUP),

    POLYLINE(TagNameConstants.POLYLINE),

    PROGRESS(TagNameConstants.PROGRESS),

    TEMPLATE(TagNameConstants.TEMPLATE),

    TEXTAREA(TagNameConstants.TEXTAREA),

    BLOCKQUOTE(TagNameConstants.BLOCKQUOTE),

    FIGCAPTION(TagNameConstants.FIGCAPTION);

    private final String tagName;

    private final int index;

    private final byte[] indexBytes;

    /**
     * @param tagName
     * @since 3.0.3
     */
    private PreIndexedTagName(final String tagName) {
        this.tagName = tagName;
        index = ordinal();
        indexBytes = WffBinaryMessageUtil.getOptimizedBytesFromInt(index);
    }

    /**
     * @return the tag name
     * @since 3.0.3
     */
    public String tagName() {
        return tagName;
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
    public byte[] internalIndexBytes(final Object accessObject) {
        if (accessObject == null
                || !((SecurityClassConstants.ABSTRACT_HTML.equals(accessObject.getClass().getName())))) {
            throw new WffSecurityException("Not allowed to consume this method. This method is for internal use.");
        }
        return indexBytes;
    }

}
