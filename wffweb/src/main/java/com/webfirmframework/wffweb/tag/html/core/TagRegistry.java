/*
 * Copyright since 2014 Web Firm Framework
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
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.webfirmframework.wffweb.InvalidValueException;
import com.webfirmframework.wffweb.tag.html.AbstractHtml;
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
import com.webfirmframework.wffweb.tag.html.attribute.core.AbstractAttribute;
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

    private static final Logger LOGGER = Logger.getLogger(TagRegistry.class.getName());

    private static final Set<String> TAG_NAMES_SET;

    private static final Map<String, String> TAG_CLASS_NAME_BY_TAG_NAME;

    private static final Map<String, Class<?>> TAG_CLASS_BY_TAG_NAME;

    private static final List<Class<?>> INDEXED_TAG_CLASSES = new ArrayList<>();

    private static volatile Map<String, Class<?>> tagClassByTagNameTmp = new ConcurrentHashMap<>();

    static {

        final Field[] fields = TagNameConstants.class.getFields();
        final int initialCapacity = fields.length;

        final Map<String, Class<?>> tagClassByTagName = new ConcurrentHashMap<>(initialCapacity);

        tagClassByTagName.put(TagNameConstants.A, A.class);
        tagClassByTagName.put(TagNameConstants.ABBR, Abbr.class);
        tagClassByTagName.put(TagNameConstants.ADDRESS, Address.class);
        tagClassByTagName.put(TagNameConstants.AREA, Area.class);
        tagClassByTagName.put(TagNameConstants.ARTICLE, Article.class);
        tagClassByTagName.put(TagNameConstants.ASIDE, Aside.class);
        tagClassByTagName.put(TagNameConstants.AUDIO, Audio.class);
        tagClassByTagName.put(TagNameConstants.B, B.class);
        tagClassByTagName.put(TagNameConstants.BASE, Base.class);
        tagClassByTagName.put(TagNameConstants.BASEFONT, BaseFont.class);
        tagClassByTagName.put(TagNameConstants.BDI, Bdi.class);
        tagClassByTagName.put(TagNameConstants.BDO, Bdo.class);
        tagClassByTagName.put(TagNameConstants.BLOCKQUOTE, BlockQuote.class);
        tagClassByTagName.put(TagNameConstants.BODY, Body.class);
        tagClassByTagName.put(TagNameConstants.BR, Br.class);
        tagClassByTagName.put(TagNameConstants.BUTTON, Button.class);
        tagClassByTagName.put(TagNameConstants.CANVAS, Canvas.class);
        tagClassByTagName.put(TagNameConstants.CAPTION, Caption.class);
        tagClassByTagName.put(TagNameConstants.CITE, Cite.class);
        tagClassByTagName.put(TagNameConstants.CODE, Code.class);
        tagClassByTagName.put(TagNameConstants.COL, Col.class);
        tagClassByTagName.put(TagNameConstants.COLGROUP, ColGroup.class);
        tagClassByTagName.put(TagNameConstants.DATA, Data.class);
        tagClassByTagName.put(TagNameConstants.DATALIST, DataList.class);
        tagClassByTagName.put(TagNameConstants.DD, Dd.class);
        tagClassByTagName.put(TagNameConstants.DEL, Del.class);
        tagClassByTagName.put(TagNameConstants.DETAILS, Details.class);
        tagClassByTagName.put(TagNameConstants.DFN, Dfn.class);
        tagClassByTagName.put(TagNameConstants.DIALOG, Dialog.class);
        tagClassByTagName.put(TagNameConstants.DIV, Div.class);
        tagClassByTagName.put(TagNameConstants.DL, Dl.class);
        tagClassByTagName.put(TagNameConstants.DT, Dt.class);
        tagClassByTagName.put(TagNameConstants.EM, Em.class);
        tagClassByTagName.put(TagNameConstants.EMBED, Embed.class);
        tagClassByTagName.put(TagNameConstants.FIELDSET, FieldSet.class);
        tagClassByTagName.put(TagNameConstants.FIGCAPTION, FigCaption.class);
        tagClassByTagName.put(TagNameConstants.FIGURE, Figure.class);
        tagClassByTagName.put(TagNameConstants.FOOTER, Footer.class);
        tagClassByTagName.put(TagNameConstants.FORM, Form.class);
        tagClassByTagName.put(TagNameConstants.H1, H1.class);
        tagClassByTagName.put(TagNameConstants.H2, H2.class);
        tagClassByTagName.put(TagNameConstants.H3, H3.class);
        tagClassByTagName.put(TagNameConstants.H4, H4.class);
        tagClassByTagName.put(TagNameConstants.H5, H5.class);

        tagClassByTagName.put(TagNameConstants.HEAD, Head.class);
        tagClassByTagName.put(TagNameConstants.HEADER, Header.class);
        tagClassByTagName.put(TagNameConstants.HGROUP, HGroup.class);
        tagClassByTagName.put(TagNameConstants.HR, Hr.class);
        tagClassByTagName.put(TagNameConstants.HTML, Html.class);
        tagClassByTagName.put(TagNameConstants.I, I.class);
        tagClassByTagName.put(TagNameConstants.IFRAME, IFrame.class);
        tagClassByTagName.put(TagNameConstants.IMG, Img.class);
        tagClassByTagName.put(TagNameConstants.INPUT, Input.class);
        tagClassByTagName.put(TagNameConstants.INS, Ins.class);
        tagClassByTagName.put(TagNameConstants.KBD, Kbd.class);
        tagClassByTagName.put(TagNameConstants.KEYGEN, KeyGen.class);
        tagClassByTagName.put(TagNameConstants.LABEL, Label.class);
        tagClassByTagName.put(TagNameConstants.LEGEND, Legend.class);
        tagClassByTagName.put(TagNameConstants.LI, Li.class);
        tagClassByTagName.put(TagNameConstants.LINK, Link.class);
        tagClassByTagName.put(TagNameConstants.MAIN, Main.class);
        tagClassByTagName.put(TagNameConstants.MAP, MapTag.class);
        tagClassByTagName.put(TagNameConstants.MARK, Mark.class);
        tagClassByTagName.put(TagNameConstants.MATH, MathTag.class);
        tagClassByTagName.put(TagNameConstants.MENU, Menu.class);
        tagClassByTagName.put(TagNameConstants.MENUITEM, MenuItem.class);
        tagClassByTagName.put(TagNameConstants.META, Meta.class);
        tagClassByTagName.put(TagNameConstants.METER, Meter.class);
        tagClassByTagName.put(TagNameConstants.NAV, Nav.class);
        tagClassByTagName.put(TagNameConstants.NOSCRIPT, NoScript.class);
        tagClassByTagName.put(TagNameConstants.OBJECT, ObjectTag.class);
        tagClassByTagName.put(TagNameConstants.OL, Ol.class);
        tagClassByTagName.put(TagNameConstants.OPTGROUP, OptGroup.class);
        tagClassByTagName.put(TagNameConstants.OPTION, Option.class);
        tagClassByTagName.put(TagNameConstants.OUTPUT, Output.class);
        tagClassByTagName.put(TagNameConstants.P, P.class);
        tagClassByTagName.put(TagNameConstants.PARAM, Param.class);
        tagClassByTagName.put(TagNameConstants.PRE, Pre.class);
        tagClassByTagName.put(TagNameConstants.PROGRESS, Progress.class);
        tagClassByTagName.put(TagNameConstants.Q, Q.class);
        tagClassByTagName.put(TagNameConstants.QFN, Qfn.class);
        tagClassByTagName.put(TagNameConstants.RP, Rp.class);
        tagClassByTagName.put(TagNameConstants.RT, Rt.class);
        tagClassByTagName.put(TagNameConstants.RUBY, Ruby.class);
        tagClassByTagName.put(TagNameConstants.S, S.class);
        tagClassByTagName.put(TagNameConstants.SAMP, Samp.class);
        tagClassByTagName.put(TagNameConstants.SCRIPT, Script.class);
        tagClassByTagName.put(TagNameConstants.SECTION, Section.class);
        tagClassByTagName.put(TagNameConstants.SELECT, Select.class);
        tagClassByTagName.put(TagNameConstants.SMALL, Small.class);
        tagClassByTagName.put(TagNameConstants.SOURCE, Source.class);
        tagClassByTagName.put(TagNameConstants.SPAN, Span.class);
        tagClassByTagName.put(TagNameConstants.STRONG, Strong.class);
        tagClassByTagName.put(TagNameConstants.STYLE, StyleTag.class);
        tagClassByTagName.put(TagNameConstants.SUB, Sub.class);
        tagClassByTagName.put(TagNameConstants.SUMMARY, Summary.class);
        tagClassByTagName.put(TagNameConstants.SUP, Sup.class);
        tagClassByTagName.put(TagNameConstants.SVG, Svg.class);
        tagClassByTagName.put(TagNameConstants.TABLE, Table.class);
        tagClassByTagName.put(TagNameConstants.TBODY, TBody.class);
        tagClassByTagName.put(TagNameConstants.TD, Td.class);
        tagClassByTagName.put(TagNameConstants.TEMPLATE, Template.class);
        tagClassByTagName.put(TagNameConstants.TEXTAREA, TextArea.class);
        tagClassByTagName.put(TagNameConstants.TFOOT, TFoot.class);
        tagClassByTagName.put(TagNameConstants.TH, Th.class);
        tagClassByTagName.put(TagNameConstants.THEAD, THead.class);
        tagClassByTagName.put(TagNameConstants.TIME, Time.class);
        tagClassByTagName.put(TagNameConstants.TITLE_TAG, TitleTag.class);
        tagClassByTagName.put(TagNameConstants.TR, Tr.class);
        tagClassByTagName.put(TagNameConstants.TRACK, Track.class);
        tagClassByTagName.put(TagNameConstants.U, U.class);
        tagClassByTagName.put(TagNameConstants.UL, Ul.class);
        tagClassByTagName.put(TagNameConstants.VAR, Var.class);
        tagClassByTagName.put(TagNameConstants.VIDEO, Video.class);
        tagClassByTagName.put(TagNameConstants.WBR, Wbr.class);
        tagClassByTagName.put(TagNameConstants.H6, H6.class);
        tagClassByTagName.put(TagNameConstants.PICTURE, Picture.class);
        tagClassByTagName.put(TagNameConstants.RECT, Rect.class);
        tagClassByTagName.put(TagNameConstants.CIRCLE, Circle.class);
        tagClassByTagName.put(TagNameConstants.ELLIPSE, Ellipse.class);
        tagClassByTagName.put(TagNameConstants.LINE, Line.class);
        tagClassByTagName.put(TagNameConstants.POLYGON, Polygon.class);
        tagClassByTagName.put(TagNameConstants.POLYLINE, Polyline.class);
        tagClassByTagName.put(TagNameConstants.PATH, Path.class);
        tagClassByTagName.put(TagNameConstants.TEXT, Text.class);

        TAG_CLASS_BY_TAG_NAME = Map.copyOf(tagClassByTagName);

        tagClassByTagNameTmp.putAll(tagClassByTagName);

        final Map<String, String> tagClassNameByTagName = new ConcurrentHashMap<>();

        for (final Entry<String, Class<?>> entry : tagClassByTagName.entrySet()) {
            tagClassNameByTagName.put(entry.getKey(), entry.getValue().getSimpleName());
        }

        TAG_CLASS_NAME_BY_TAG_NAME = Map.copyOf(tagClassNameByTagName);

        TAG_NAMES_SET = ConcurrentHashMap.newKeySet(initialCapacity);

        TAG_NAMES_SET.addAll(TAG_CLASS_NAME_BY_TAG_NAME.keySet());

        for (final Field field : fields) {
            try {
                final String tagName = field.get(null).toString();
                TAG_NAMES_SET.add(tagName);
            } catch (final Exception e) {
                if (LOGGER.isLoggable(Level.SEVERE)) {
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        }

        int index = 0;
        for (final String tagName : IndexedTagName.INSTANCE.sortedTagNames()) {
            final Class<?> attrClass = TAG_CLASS_BY_TAG_NAME.get(tagName);
            INDEXED_TAG_CLASSES.add(index, attrClass);

            index++;
        }
    }

    /**
     * @param preIndexedTagName
     * @return the lambda to create tag object.
     * @since 12.0.6
     */
    private static BiFunction<AbstractHtml, AbstractAttribute[], AbstractHtml> getTagCreatorLamda(
            final PreIndexedTagName preIndexedTagName) {
        // without this null checking it will throw
        // java.lang.NullPointerException: Cannot invoke
        // "com.webfirmframework.wffweb.tag.html.core.PreIndexedTagName.ordinal()"
        // because "preIndexedTagName" is null
        if (preIndexedTagName == null) {
            return null;
        }
        return switch (preIndexedTagName) {
        case HASH, DOLLAR, PERCENT, AT -> null;
        case A -> A::new;
        case B -> B::new;
        case I -> I::new;
        case P -> P::new;
        case Q -> Q::new;
        case S -> S::new;
        case U -> U::new;
        case BR -> Br::new;
        case DD -> Dd::new;
        case DL -> Dl::new;
        case DT -> Dt::new;
        case EM -> Em::new;
        case H1 -> H1::new;
        case H2 -> H2::new;
        case H3 -> H3::new;
        case H4 -> H4::new;
        case H5 -> H5::new;
        case H6 -> H6::new;
        case HR -> Hr::new;
        case LI -> Li::new;
        case OL -> Ol::new;
        case RP -> Rp::new;
        case RT -> Rt::new;
        case TD -> Td::new;
        case TH -> Th::new;
        case TR -> Tr::new;
        case UL -> Ul::new;
        case BDI -> Bdi::new;
        case BDO -> Bdo::new;
        case COL -> Col::new;
        case DEL -> Del::new;
        case DFN -> Dfn::new;
        case DIV -> Div::new;
        case IMG -> Img::new;
        case INS -> Ins::new;
        case KBD -> Kbd::new;
        case MAP -> MapTag::new;
        case NAV -> Nav::new;
        case PRE -> Pre::new;
        case QFN -> Qfn::new;
        case SUB -> Sub::new;
        case SUP -> Sup::new;
        case SVG -> Svg::new;
        case VAR -> Var::new;
        case WBR -> Wbr::new;
        case ABBR -> Abbr::new;
        case AREA -> Area::new;
        case BASE -> Base::new;
        case BODY -> Body::new;
        case CITE -> Cite::new;
        case CODE -> Code::new;
        case DATA -> Data::new;
        case FORM -> Form::new;
        case HEAD -> Head::new;
        case HTML -> Html::new;
        case LINE -> Line::new;
        case LINK -> Link::new;
        case MAIN -> Main::new;
        case MARK -> Mark::new;
        case MATH -> MathTag::new;
        case MENU -> Menu::new;
        case META -> Meta::new;
        case PATH -> Path::new;
        case RECT -> Rect::new;
        case RUBY -> Ruby::new;
        case SAMP -> Samp::new;
        case SPAN -> Span::new;
        case TEXT -> Text::new;
        case TIME -> Time::new;
        case ASIDE -> Aside::new;
        case AUDIO -> Audio::new;
        case EMBED -> Embed::new;
        case INPUT -> Input::new;
        case LABEL -> Label::new;
        case METER -> Meter::new;
        case PARAM -> Param::new;
        case SMALL -> Small::new;
        case STYLE -> StyleTag::new;
        case TABLE -> Table::new;
        case TBODY -> TBody::new;
        case TFOOT -> TFoot::new;
        case THEAD -> THead::new;
        case TITLE -> TitleTag::new;
        case TRACK -> Track::new;
        case VIDEO -> Video::new;
        case BUTTON -> Button::new;
        case CANVAS -> Canvas::new;
        case CIRCLE -> Circle::new;
        case DIALOG -> Dialog::new;
        case FIGURE -> Figure::new;
        case FOOTER -> Footer::new;
        case HEADER -> Header::new;
        case HGROUP -> HGroup::new;
        case IFRAME -> IFrame::new;
        case KEYGEN -> KeyGen::new;
        case LEGEND -> Legend::new;
        case OBJECT -> ObjectTag::new;
        case OPTION -> Option::new;
        case OUTPUT -> Output::new;
        case SCRIPT -> Script::new;
        case SELECT -> Select::new;
        case SOURCE -> Source::new;
        case STRONG -> Strong::new;
        case ADDRESS -> Address::new;
        case ARTICLE -> Article::new;
        case CAPTION -> Caption::new;
        case DETAILS -> Details::new;
        case ELLIPSE -> Ellipse::new;
        case PICTURE -> Picture::new;
        case POLYGON -> Polygon::new;
        case SECTION -> Section::new;
        case SUMMARY -> Summary::new;
        case BASEFONT -> BaseFont::new;
        case COLGROUP -> ColGroup::new;
        case DATALIST -> DataList::new;
        case FIELDSET -> FieldSet::new;
        case MENUITEM -> MenuItem::new;
        case NOSCRIPT -> NoScript::new;
        case OPTGROUP -> OptGroup::new;
        case POLYLINE -> Polyline::new;
        case PROGRESS -> Progress::new;
        case TEMPLATE -> Template::new;
        case TEXTAREA -> TextArea::new;
        case BLOCKQUOTE -> BlockQuote::new;
        case FIGCAPTION -> FigCaption::new;
        };
    }

    /**
     * @return the list of tag names sorted in the ascending order of its length
     * @since 1.1.3
     * @since 12.0.0-beta.7 immutable list
     * @author WFF
     */
    public static List<String> getTagNames() {
        return List.copyOf(IndexedTagName.INSTANCE.sortedTagNames());
    }

    /**
     * @param tagName
     * @return the index of tag name
     * @since 3.0.3
     */
    public static Integer getIndexByTagName(final String tagName) {
        return IndexedTagName.INSTANCE.getIndexByTagName(tagName);
    }

    /**
     *
     * @param tagNamesToRegister the tag names to register , eg:-
     *                           register("new-tag1", "new-tag2")
     * @since 1.1.3
     * @author WFF
     */
    public static void register(final String... tagNamesToRegister) {

        final Set<String> tagNamesWithoutDuplicates = new HashSet<>(tagNamesToRegister.length);
        Collections.addAll(tagNamesWithoutDuplicates, tagNamesToRegister);

        TAG_NAMES_SET.addAll(tagNamesWithoutDuplicates);

        IndexedTagName.INSTANCE.sortedTagNames().clear();
        IndexedTagName.INSTANCE.sortedTagNames().addAll(TAG_NAMES_SET);

        // sorting in ascending order of length is the first priority
        // then needs to sort by ascending order of name otherwise
        // in some machines the order will be different for names having same
        // length
        IndexedTagName.INSTANCE.sortedTagNames()
                .sort(Comparator.comparingInt(String::length).thenComparing(String::compareTo));

    }

    /**
     * @return a map containing tag name as key and value as tag class name without
     *         package name
     * @since 1.1.3
     * @author WFF
     */
    public static Map<String, String> getTagClassNameByTagName() {
        return TAG_CLASS_NAME_BY_TAG_NAME;
    }

    /**
     * @return
     * @since 3.0.2
     */
    public static Map<String, Class<?>> getTagClassByTagName() {
        return Map.copyOf(TAG_CLASS_BY_TAG_NAME);
    }

    /**
     * Loads all tag classes.
     *
     * @since 2.1.13
     * @author WFF
     */
    public static void loadAllTagClasses() {

        final Map<String, Class<?>> tagClassByTagNameTmpLocal = tagClassByTagNameTmp;
        if (tagClassByTagNameTmpLocal != null) {
            final Map<String, Class<?>> unloadedClasses = new HashMap<>();

            for (final Entry<String, Class<?>> entry : tagClassByTagNameTmpLocal.entrySet()) {
                try {

                    Class.forName(entry.getValue().getName());

                } catch (final ClassNotFoundException e) {
                    unloadedClasses.put(entry.getKey(), entry.getValue());
                    if (LOGGER.isLoggable(Level.WARNING)) {
                        LOGGER.warning("Could not load tag class " + entry.getValue().getName());
                    }

                }
            }
            tagClassByTagNameTmpLocal.clear();
            if (!unloadedClasses.isEmpty()) {
                tagClassByTagNameTmpLocal.putAll(unloadedClasses);
            } else {
                tagClassByTagNameTmp = null;
            }
        }

    }

    /**
     * @param tagName name of tag in lower case
     * @return the new instance of given tag name without any parent tag and
     *         attributes
     * @since 3.0.2
     * @throws InvalidValueException
     */
    public static AbstractHtml getNewTagInstance(final String tagName) {
        return getNewTagInstance(tagName, null, new AbstractAttribute[0]);
    }

    /**
     * @param tagName    name of tag in lower case
     * @param parent
     * @param attributes
     * @return
     * @since 3.0.2
     * @throws InvalidValueException
     */
    public static AbstractHtml getNewTagInstance(final String tagName, final AbstractHtml parent,
            final AbstractAttribute... attributes) {

        final Class<?> tagClass = TAG_CLASS_BY_TAG_NAME.get(tagName);

        if (tagClass == null) {
            return null;
        }

        try {

            final AbstractHtml tag = (AbstractHtml) tagClass
                    .getConstructor(AbstractHtml.class, AbstractAttribute[].class).newInstance(parent, attributes);

            return tag;
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            throw new InvalidValueException("Unable to create instance for " + tagClass, e);
        }
    }

    /**
     * @param tagName    name of tag in lower case
     * @param parent
     * @param attributes
     * @return new instance or null if failed
     * @since 3.0.2
     */
    public static AbstractHtml getNewTagInstanceOrNullIfFailed(final String tagName, final AbstractHtml parent,
            final AbstractAttribute... attributes) {

        final Class<?> tagClass = TAG_CLASS_BY_TAG_NAME.get(tagName);

        if (tagClass == null) {
            return null;
        }

        try {

            final AbstractHtml tag = (AbstractHtml) tagClass
                    .getConstructor(AbstractHtml.class, AbstractAttribute[].class).newInstance(parent, attributes);

            return tag;
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            // NOP
        }
        return null;
    }

    /**
     * @param preIndexedTagName the preIndexedTagName
     * @param parent
     * @param attributes
     * @return new instance or null if failed
     * @since 12.0.6
     */
    public static AbstractHtml getNewTagInstanceOrNullIfFailed(final PreIndexedTagName preIndexedTagName,
            final AbstractHtml parent, final AbstractAttribute... attributes) {
        final BiFunction<AbstractHtml, AbstractAttribute[], AbstractHtml> tagCreatorLamda = getTagCreatorLamda(
                preIndexedTagName);
        if (tagCreatorLamda != null) {
            return tagCreatorLamda.apply(parent, attributes);
        }
        return null;
    }

    /**
     * @param tagNameIndex index of tag name
     * @param parent
     * @param attributes
     * @return new instance or null if failed
     * @since 3.0.3
     */
    public static AbstractHtml getNewTagInstanceOrNullIfFailed(final int tagNameIndex, final AbstractHtml parent,
            final AbstractAttribute... attributes) {

        final Class<?> tagClass = INDEXED_TAG_CLASSES.get(tagNameIndex);

        if (tagClass == null) {
            return null;
        }

        try {

            final AbstractHtml tag = (AbstractHtml) tagClass
                    .getConstructor(AbstractHtml.class, AbstractAttribute[].class).newInstance(parent, attributes);

            return tag;
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            // NOP
        }
        return null;
    }

    // only for testing purpose
    static void test() throws InstantiationException, IllegalAccessException, NoSuchFieldException, SecurityException,
            IllegalArgumentException, InvocationTargetException, NoSuchMethodException, InvalidValueException {
        for (final Entry<String, Class<?>> each : TAG_CLASS_BY_TAG_NAME.entrySet()) {
            final String expectedTagName = each.getKey();
            final Class<?> tagClass = each.getValue();
            final AbstractHtml newInstance = (AbstractHtml) tagClass
                    .getConstructor(AbstractHtml.class, AbstractAttribute[].class)
                    .newInstance(null, new AbstractAttribute[0]);
            final String actualTagName = newInstance.getTagName();

            if (!expectedTagName.equals(actualTagName)) {
                throw new InvalidValueException(
                        "expectedTagName: " + expectedTagName + " actualTagName: " + actualTagName);
            }
        }
    }

}
