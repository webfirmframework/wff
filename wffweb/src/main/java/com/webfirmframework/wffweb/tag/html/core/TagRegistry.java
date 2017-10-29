/*
 * Copyright 2014-2017 Web Firm Framework
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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.webfirmframework.wffweb.tag.html.BaseFont;
import com.webfirmframework.wffweb.tag.html.Body;
import com.webfirmframework.wffweb.tag.html.Br;
import com.webfirmframework.wffweb.tag.html.H1;
import com.webfirmframework.wffweb.tag.html.H2;
import com.webfirmframework.wffweb.tag.html.H3;
import com.webfirmframework.wffweb.tag.html.H4;
import com.webfirmframework.wffweb.tag.html.H5;
import com.webfirmframework.wffweb.tag.html.H6;
import com.webfirmframework.wffweb.tag.html.Hr;
import com.webfirmframework.wffweb.tag.html.Html;
import com.webfirmframework.wffweb.tag.html.P;
import com.webfirmframework.wffweb.tag.html.Qfn;
import com.webfirmframework.wffweb.tag.html.TagNameConstants;
import com.webfirmframework.wffweb.tag.html.TitleTag;
import com.webfirmframework.wffweb.tag.html.formatting.Abbr;
import com.webfirmframework.wffweb.tag.html.formatting.Address;
import com.webfirmframework.wffweb.tag.html.formatting.B;
import com.webfirmframework.wffweb.tag.html.formatting.Bdo;
import com.webfirmframework.wffweb.tag.html.formatting.BlockQuote;
import com.webfirmframework.wffweb.tag.html.formatting.Cite;
import com.webfirmframework.wffweb.tag.html.formatting.Code;
import com.webfirmframework.wffweb.tag.html.formatting.Del;
import com.webfirmframework.wffweb.tag.html.formatting.Dfn;
import com.webfirmframework.wffweb.tag.html.formatting.Em;
import com.webfirmframework.wffweb.tag.html.formatting.I;
import com.webfirmframework.wffweb.tag.html.formatting.Ins;
import com.webfirmframework.wffweb.tag.html.formatting.Kbd;
import com.webfirmframework.wffweb.tag.html.formatting.Pre;
import com.webfirmframework.wffweb.tag.html.formatting.Q;
import com.webfirmframework.wffweb.tag.html.formatting.S;
import com.webfirmframework.wffweb.tag.html.formatting.Samp;
import com.webfirmframework.wffweb.tag.html.formatting.Small;
import com.webfirmframework.wffweb.tag.html.formatting.Strong;
import com.webfirmframework.wffweb.tag.html.formatting.Sub;
import com.webfirmframework.wffweb.tag.html.formatting.Sup;
import com.webfirmframework.wffweb.tag.html.formatting.U;
import com.webfirmframework.wffweb.tag.html.formatting.Var;
import com.webfirmframework.wffweb.tag.html.formsandinputs.Button;
import com.webfirmframework.wffweb.tag.html.formsandinputs.FieldSet;
import com.webfirmframework.wffweb.tag.html.formsandinputs.Form;
import com.webfirmframework.wffweb.tag.html.formsandinputs.Input;
import com.webfirmframework.wffweb.tag.html.formsandinputs.Label;
import com.webfirmframework.wffweb.tag.html.formsandinputs.Legend;
import com.webfirmframework.wffweb.tag.html.formsandinputs.OptGroup;
import com.webfirmframework.wffweb.tag.html.formsandinputs.Option;
import com.webfirmframework.wffweb.tag.html.formsandinputs.Select;
import com.webfirmframework.wffweb.tag.html.formsandinputs.TextArea;
import com.webfirmframework.wffweb.tag.html.frames.IFrame;
import com.webfirmframework.wffweb.tag.html.html5.Circle;
import com.webfirmframework.wffweb.tag.html.html5.Data;
import com.webfirmframework.wffweb.tag.html.html5.Ellipse;
import com.webfirmframework.wffweb.tag.html.html5.HGroup;
import com.webfirmframework.wffweb.tag.html.html5.Line;
import com.webfirmframework.wffweb.tag.html.html5.MathTag;
import com.webfirmframework.wffweb.tag.html.html5.Path;
import com.webfirmframework.wffweb.tag.html.html5.Polygon;
import com.webfirmframework.wffweb.tag.html.html5.Polyline;
import com.webfirmframework.wffweb.tag.html.html5.Rect;
import com.webfirmframework.wffweb.tag.html.html5.Source;
import com.webfirmframework.wffweb.tag.html.html5.Svg;
import com.webfirmframework.wffweb.tag.html.html5.Template;
import com.webfirmframework.wffweb.tag.html.html5.Text;
import com.webfirmframework.wffweb.tag.html.html5.Track;
import com.webfirmframework.wffweb.tag.html.html5.Video;
import com.webfirmframework.wffweb.tag.html.html5.audiovideo.Audio;
import com.webfirmframework.wffweb.tag.html.html5.formatting.Bdi;
import com.webfirmframework.wffweb.tag.html.html5.formatting.Mark;
import com.webfirmframework.wffweb.tag.html.html5.formatting.Meter;
import com.webfirmframework.wffweb.tag.html.html5.formatting.Progress;
import com.webfirmframework.wffweb.tag.html.html5.formatting.Rp;
import com.webfirmframework.wffweb.tag.html.html5.formatting.Rt;
import com.webfirmframework.wffweb.tag.html.html5.formatting.Ruby;
import com.webfirmframework.wffweb.tag.html.html5.formatting.Time;
import com.webfirmframework.wffweb.tag.html.html5.formatting.Wbr;
import com.webfirmframework.wffweb.tag.html.html5.formsandinputs.DataList;
import com.webfirmframework.wffweb.tag.html.html5.formsandinputs.KeyGen;
import com.webfirmframework.wffweb.tag.html.html5.formsandinputs.Output;
import com.webfirmframework.wffweb.tag.html.html5.images.Canvas;
import com.webfirmframework.wffweb.tag.html.html5.images.FigCaption;
import com.webfirmframework.wffweb.tag.html.html5.images.Figure;
import com.webfirmframework.wffweb.tag.html.html5.images.Picture;
import com.webfirmframework.wffweb.tag.html.html5.links.Nav;
import com.webfirmframework.wffweb.tag.html.html5.lists.Menu;
import com.webfirmframework.wffweb.tag.html.html5.lists.MenuItem;
import com.webfirmframework.wffweb.tag.html.html5.programming.Embed;
import com.webfirmframework.wffweb.tag.html.html5.stylesandsemantics.Article;
import com.webfirmframework.wffweb.tag.html.html5.stylesandsemantics.Aside;
import com.webfirmframework.wffweb.tag.html.html5.stylesandsemantics.Details;
import com.webfirmframework.wffweb.tag.html.html5.stylesandsemantics.Dialog;
import com.webfirmframework.wffweb.tag.html.html5.stylesandsemantics.Footer;
import com.webfirmframework.wffweb.tag.html.html5.stylesandsemantics.Header;
import com.webfirmframework.wffweb.tag.html.html5.stylesandsemantics.Main;
import com.webfirmframework.wffweb.tag.html.html5.stylesandsemantics.Section;
import com.webfirmframework.wffweb.tag.html.html5.stylesandsemantics.Summary;
import com.webfirmframework.wffweb.tag.html.images.Area;
import com.webfirmframework.wffweb.tag.html.images.Img;
import com.webfirmframework.wffweb.tag.html.images.MapTag;
import com.webfirmframework.wffweb.tag.html.links.A;
import com.webfirmframework.wffweb.tag.html.links.Link;
import com.webfirmframework.wffweb.tag.html.lists.Dd;
import com.webfirmframework.wffweb.tag.html.lists.Dl;
import com.webfirmframework.wffweb.tag.html.lists.Dt;
import com.webfirmframework.wffweb.tag.html.lists.Li;
import com.webfirmframework.wffweb.tag.html.lists.Ol;
import com.webfirmframework.wffweb.tag.html.lists.Ul;
import com.webfirmframework.wffweb.tag.html.metainfo.Base;
import com.webfirmframework.wffweb.tag.html.metainfo.Head;
import com.webfirmframework.wffweb.tag.html.metainfo.Meta;
import com.webfirmframework.wffweb.tag.html.programming.NoScript;
import com.webfirmframework.wffweb.tag.html.programming.ObjectTag;
import com.webfirmframework.wffweb.tag.html.programming.Param;
import com.webfirmframework.wffweb.tag.html.programming.Script;
import com.webfirmframework.wffweb.tag.html.stylesandsemantics.Div;
import com.webfirmframework.wffweb.tag.html.stylesandsemantics.Span;
import com.webfirmframework.wffweb.tag.html.stylesandsemantics.StyleTag;
import com.webfirmframework.wffweb.tag.html.tables.Caption;
import com.webfirmframework.wffweb.tag.html.tables.Col;
import com.webfirmframework.wffweb.tag.html.tables.ColGroup;
import com.webfirmframework.wffweb.tag.html.tables.TBody;
import com.webfirmframework.wffweb.tag.html.tables.TFoot;
import com.webfirmframework.wffweb.tag.html.tables.THead;
import com.webfirmframework.wffweb.tag.html.tables.Table;
import com.webfirmframework.wffweb.tag.html.tables.Td;
import com.webfirmframework.wffweb.tag.html.tables.Th;
import com.webfirmframework.wffweb.tag.html.tables.Tr;

public class TagRegistry {

    public static final Logger LOGGER = Logger
            .getLogger(TagRegistry.class.getName());

    private static List<String> tagNames;

    private static final Set<String> tagNamesSet;

    // TAG_CLASS_NAME_BY_TAG_NAME

    private static final Map<String, String> TAG_CLASS_NAME_BY_TAG_NAME;

    private static final Map<String, Class<?>> TAG_CLASS_BY_TAG_NAME;

    static {

        final Field[] fields = TagNameConstants.class.getFields();
        final int initialCapacity = fields.length;

        TAG_CLASS_BY_TAG_NAME = new HashMap<String, Class<?>>(initialCapacity);
        TAG_CLASS_NAME_BY_TAG_NAME = new HashMap<String, String>(
                initialCapacity);

        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.A, A.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.ABBR, Abbr.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.ADDRESS, Address.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.AREA, Area.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.ARTICLE, Article.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.ASIDE, Aside.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.AUDIO, Audio.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.B, B.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.BASE, Base.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.BASEFONT, BaseFont.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.BDI, Bdi.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.BDO, Bdo.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.BLOCKQUOTE,
                BlockQuote.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.BODY, Body.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.BR, Br.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.BUTTON, Button.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.CANVAS, Canvas.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.CAPTION, Caption.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.CITE, Cite.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.CODE, Code.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.COL, Col.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.COLGROUP, ColGroup.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.DATA, Data.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.DATALIST, DataList.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.DD, Dd.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.DEL, Del.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.DETAILS, Details.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.DFN, Dfn.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.DIALOG, Dialog.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.DIV, Div.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.DL, Dl.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.DT, Dt.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.EM, Em.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.EMBED, Embed.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.FIELDSET, FieldSet.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.FIGCAPTION,
                FigCaption.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.FIGURE, Figure.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.FOOTER, Footer.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.FORM, Form.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.H1, H1.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.H2, H2.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.H3, H3.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.H4, H4.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.H5, H5.class);

        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.HEAD, Head.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.HEADER, Header.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.HGROUP, HGroup.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.HR, Hr.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.HTML, Html.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.I, I.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.IFRAME, IFrame.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.IMG, Img.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.INPUT, Input.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.INS, Ins.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.KBD, Kbd.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.KEYGEN, KeyGen.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.LABEL, Label.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.LEGEND, Legend.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.LI, Li.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.LINK, Link.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.MAIN, Main.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.MAP, MapTag.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.MARK, Mark.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.MATH, MathTag.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.MENU, Menu.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.MENUITEM, MenuItem.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.META, Meta.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.METER, Meter.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.NAV, Nav.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.NOSCRIPT, NoScript.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.OBJECT, ObjectTag.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.OL, Ol.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.OPTGROUP, OptGroup.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.OPTION, Option.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.OUTPUT, Output.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.P, P.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.PARAM, Param.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.PRE, Pre.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.PROGRESS, Progress.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.Q, Q.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.QFN, Qfn.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.RP, Rp.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.RT, Rt.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.RUBY, Ruby.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.S, S.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.SAMP, Samp.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.SCRIPT, Script.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.SECTION, Section.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.SELECT, Select.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.SMALL, Small.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.SOURCE, Source.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.SPAN, Span.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.STRONG, Strong.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.STYLE, StyleTag.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.SUB, Sub.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.SUMMARY, Summary.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.SUP, Sup.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.SVG, Svg.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.TABLE, Table.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.TBODY, TBody.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.TD, Td.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.TEMPLATE, Template.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.TEXTAREA, TextArea.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.TFOOT, TFoot.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.TH, Th.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.THEAD, THead.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.TIME, Time.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.TITLE_TAG, TitleTag.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.TR, Tr.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.TRACK, Track.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.U, U.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.UL, Ul.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.VAR, Var.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.VIDEO, Video.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.WBR, Wbr.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.H6, H6.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.PICTURE, Picture.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.RECT, Rect.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.CIRCLE, Circle.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.ELLIPSE, Ellipse.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.LINE, Line.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.POLYGON, Polygon.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.POLYLINE, Polyline.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.PATH, Path.class);
        TAG_CLASS_BY_TAG_NAME.put(TagNameConstants.TEXT, Text.class);

        for (final Entry<String, Class<?>> entry : TAG_CLASS_BY_TAG_NAME
                .entrySet()) {
            TAG_CLASS_NAME_BY_TAG_NAME.put(entry.getKey(),
                    entry.getValue().getSimpleName());
        }

        tagNames = new ArrayList<String>(initialCapacity);
        tagNamesSet = new HashSet<String>(initialCapacity);

        tagNamesSet.addAll(TAG_CLASS_NAME_BY_TAG_NAME.keySet());
        tagNames.addAll(tagNamesSet);

        for (final Field field : fields) {
            try {
                final String tagName = field.get(null).toString();
                tagNamesSet.add(tagName);
            } catch (final Exception e) {
                if (LOGGER.isLoggable(Level.SEVERE)) {
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        }

        tagNames.addAll(tagNamesSet);

        Collections.sort(tagNames, new Comparator<String>() {

            @Override
            public int compare(final String o1, final String o2) {

                final Integer length1 = o1.length();
                final Integer length2 = o2.length();

                return length1.compareTo(length2);
            }
        });
    }

    /**
     * @return the list of tag names sorted in the ascending order of its length
     * @since 1.1.3
     * @author WFF
     */
    public static List<String> getTagNames() {
        return tagNames;
    }

    /**
     *
     * @param tagNamesToRegister
     *            the tag names to register , eg:- register("new-tag1",
     *            "new-tag2")
     * @since 1.1.3
     * @author WFF
     */
    public static void register(final String... tagNamesToRegister) {

        final Set<String> tagNamesWithoutDuplicates = new HashSet<String>(
                Arrays.asList(tagNamesToRegister));

        tagNamesSet.addAll(tagNamesWithoutDuplicates);

        tagNames.clear();
        tagNames.addAll(tagNamesSet);

        Collections.sort(tagNames, new Comparator<String>() {

            @Override
            public int compare(final String o1, final String o2) {

                final Integer length1 = o1.length();
                final Integer length2 = o2.length();

                return length1.compareTo(length2);
            }
        });

    }

    /**
     * @return a map containing tag name as key and value as tag class name
     *         without package name
     * @since 1.1.3
     * @author WFF
     */
    public static Map<String, String> getTagClassNameByTagName() {
        return TAG_CLASS_NAME_BY_TAG_NAME;
    }

    /**
     * Loads all tag classes.
     *
     * @since 2.1.13
     * @author WFF
     */
    public static void loadAllTagClasses() {
        for (final Class<?> cls : TAG_CLASS_BY_TAG_NAME.values()) {
            try {
                Class.forName(cls.getName());
            } catch (final ClassNotFoundException e) {
                if (LOGGER.isLoggable(Level.WARNING)) {
                    LOGGER.warning("Could not load tag class " + cls.getName());
                }

            }
        }
    }

}
