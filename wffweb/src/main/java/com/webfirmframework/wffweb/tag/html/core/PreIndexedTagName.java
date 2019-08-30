/*
 * Copyright 2014-2019 Web Firm Framework
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
import com.webfirmframework.wffweb.security.object.SecurityClassConstants;
import com.webfirmframework.wffweb.tag.html.TagNameConstants;
import com.webfirmframework.wffweb.util.WffBinaryMessageUtil;

/**
 * @author WFF
 * @since 3.0.3
 */
public enum PreIndexedTagName {

    // NB: order should not be changed it is ordered by length

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

    HR(TagNameConstants.HR),

    RP(TagNameConstants.RP),

    RT(TagNameConstants.RT),

    BR(TagNameConstants.BR),

    TD(TagNameConstants.TD),

    TH(TagNameConstants.TH),

    LI(TagNameConstants.LI),

    TR(TagNameConstants.TR),

    DD(TagNameConstants.DD),

    DL(TagNameConstants.DL),

    DT(TagNameConstants.DT),

    UL(TagNameConstants.UL),

    EM(TagNameConstants.EM),

    H1(TagNameConstants.H1),

    H2(TagNameConstants.H2),

    H3(TagNameConstants.H3),

    H4(TagNameConstants.H4),

    H5(TagNameConstants.H5),

    H6(TagNameConstants.H6),

    OL(TagNameConstants.OL),

    WBR(TagNameConstants.WBR),

    DEL(TagNameConstants.DEL),

    NAV(TagNameConstants.NAV),

    VAR(TagNameConstants.VAR),

    DFN(TagNameConstants.DFN),

    SUB(TagNameConstants.SUB),

    SUP(TagNameConstants.SUP),

    MAP(TagNameConstants.MAP),

    SVG(TagNameConstants.SVG),

    BDI(TagNameConstants.BDI),

    BDO(TagNameConstants.BDO),

    COL(TagNameConstants.COL),

    DIV(TagNameConstants.DIV),

    PRE(TagNameConstants.PRE),

    IMG(TagNameConstants.IMG),

    KBD(TagNameConstants.KBD),

    INS(TagNameConstants.INS),

    QFN(TagNameConstants.QFN),

    PATH(TagNameConstants.PATH),

    TEXT(TagNameConstants.TEXT),

    ABBR(TagNameConstants.ABBR),

    META(TagNameConstants.META),

    MATH(TagNameConstants.MATH),

    SAMP(TagNameConstants.SAMP),

    MENU(TagNameConstants.MENU),

    CITE(TagNameConstants.CITE),

    SPAN(TagNameConstants.SPAN),

    DATA(TagNameConstants.DATA),

    MAIN(TagNameConstants.MAIN),

    BODY(TagNameConstants.BODY),

    HTML(TagNameConstants.HTML),

    AREA(TagNameConstants.AREA),

    CODE(TagNameConstants.CODE),

    LINE(TagNameConstants.LINE),

    LINK(TagNameConstants.LINK),

    HEAD(TagNameConstants.HEAD),

    RECT(TagNameConstants.RECT),

    RUBY(TagNameConstants.RUBY),

    FORM(TagNameConstants.FORM),

    TIME(TagNameConstants.TIME),

    MARK(TagNameConstants.MARK),

    BASE(TagNameConstants.BASE),

    EMBED(TagNameConstants.EMBED),

    INPUT(TagNameConstants.INPUT),

    STYLE(TagNameConstants.STYLE),

    AUDIO(TagNameConstants.AUDIO),

    TABLE(TagNameConstants.TABLE),

    TFOOT(TagNameConstants.TFOOT),

    SMALL(TagNameConstants.SMALL),

    TBODY(TagNameConstants.TBODY),

    METER(TagNameConstants.METER),

    ASIDE(TagNameConstants.ASIDE),

    THEAD(TagNameConstants.THEAD),

    VIDEO(TagNameConstants.VIDEO),

    /**
     * title
     */
    TITLE(TagNameConstants.TITLE_TAG),

    PARAM(TagNameConstants.PARAM),

    TRACK(TagNameConstants.TRACK),

    LABEL(TagNameConstants.LABEL),

    SELECT(TagNameConstants.SELECT),

    LEGEND(TagNameConstants.LEGEND),

    OUTPUT(TagNameConstants.OUTPUT),

    CANVAS(TagNameConstants.CANVAS),

    SCRIPT(TagNameConstants.SCRIPT),

    CIRCLE(TagNameConstants.CIRCLE),

    OBJECT(TagNameConstants.OBJECT),

    STRONG(TagNameConstants.STRONG),

    HGROUP(TagNameConstants.HGROUP),

    IFRAME(TagNameConstants.IFRAME),

    SOURCE(TagNameConstants.SOURCE),

    HEADER(TagNameConstants.HEADER),

    OPTION(TagNameConstants.OPTION),

    FOOTER(TagNameConstants.FOOTER),

    KEYGEN(TagNameConstants.KEYGEN),

    BUTTON(TagNameConstants.BUTTON),

    DIALOG(TagNameConstants.DIALOG),

    FIGURE(TagNameConstants.FIGURE),

    CAPTION(TagNameConstants.CAPTION),

    ADDRESS(TagNameConstants.ADDRESS),

    PICTURE(TagNameConstants.PICTURE),

    POLYGON(TagNameConstants.POLYGON),

    SECTION(TagNameConstants.SECTION),

    DETAILS(TagNameConstants.DETAILS),

    ELLIPSE(TagNameConstants.ELLIPSE),

    ARTICLE(TagNameConstants.ARTICLE),

    SUMMARY(TagNameConstants.SUMMARY),

    TEXTAREA(TagNameConstants.TEXTAREA),

    TEMPLATE(TagNameConstants.TEMPLATE),

    PROGRESS(TagNameConstants.PROGRESS),

    OPTGROUP(TagNameConstants.OPTGROUP),

    FIELDSET(TagNameConstants.FIELDSET),

    NOSCRIPT(TagNameConstants.NOSCRIPT),

    POLYLINE(TagNameConstants.POLYLINE),

    BASEFONT(TagNameConstants.BASEFONT),

    DATALIST(TagNameConstants.DATALIST),

    COLGROUP(TagNameConstants.COLGROUP),

    MENUITEM(TagNameConstants.MENUITEM),

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
            return new byte[] { indexBytes[0], indexBytes[1], indexBytes[2],
                    indexBytes[3] };
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
        if (accessObject == null || !((SecurityClassConstants.ABSTRACT_HTML
                .equals(accessObject.getClass().getName())))) {
            throw new WffSecurityException(
                    "Not allowed to consume this method. This method is for internal use.");
        }
        return indexBytes;
    }

}
