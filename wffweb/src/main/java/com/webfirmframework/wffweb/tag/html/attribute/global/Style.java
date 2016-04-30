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
 * @author WFF
 */
package com.webfirmframework.wffweb.tag.html.attribute.global;

import static com.webfirmframework.wffweb.css.CssConstants.IMPORTANT;
import static com.webfirmframework.wffweb.css.CssConstants.IMPORTANT_UPPERCASE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import com.webfirmframework.wffweb.InvalidValueException;
import com.webfirmframework.wffweb.NullValueException;
import com.webfirmframework.wffweb.css.BackgroundAttachment;
import com.webfirmframework.wffweb.css.BackgroundColor;
import com.webfirmframework.wffweb.css.BackgroundImage;
import com.webfirmframework.wffweb.css.BackgroundRepeat;
import com.webfirmframework.wffweb.css.Border;
import com.webfirmframework.wffweb.css.BorderBottom;
import com.webfirmframework.wffweb.css.BorderBottomColor;
import com.webfirmframework.wffweb.css.BorderBottomStyle;
import com.webfirmframework.wffweb.css.BorderBottomWidth;
import com.webfirmframework.wffweb.css.BorderCollapse;
import com.webfirmframework.wffweb.css.BorderColor;
import com.webfirmframework.wffweb.css.BorderLeft;
import com.webfirmframework.wffweb.css.BorderLeftColor;
import com.webfirmframework.wffweb.css.BorderLeftStyle;
import com.webfirmframework.wffweb.css.BorderLeftWidth;
import com.webfirmframework.wffweb.css.BorderRight;
import com.webfirmframework.wffweb.css.BorderRightColor;
import com.webfirmframework.wffweb.css.BorderRightStyle;
import com.webfirmframework.wffweb.css.BorderRightWidth;
import com.webfirmframework.wffweb.css.BorderSpacing;
import com.webfirmframework.wffweb.css.BorderStyle;
import com.webfirmframework.wffweb.css.BorderTop;
import com.webfirmframework.wffweb.css.BorderTopColor;
import com.webfirmframework.wffweb.css.BorderTopStyle;
import com.webfirmframework.wffweb.css.BorderTopWidth;
import com.webfirmframework.wffweb.css.BorderWidth;
import com.webfirmframework.wffweb.css.Bottom;
import com.webfirmframework.wffweb.css.CaptionSide;
import com.webfirmframework.wffweb.css.Clear;
import com.webfirmframework.wffweb.css.Color;
import com.webfirmframework.wffweb.css.CssConstants;
import com.webfirmframework.wffweb.css.CssNameConstants;
import com.webfirmframework.wffweb.css.Cursor;
import com.webfirmframework.wffweb.css.Direction;
import com.webfirmframework.wffweb.css.Display;
import com.webfirmframework.wffweb.css.EmptyCells;
import com.webfirmframework.wffweb.css.FloatCss;
import com.webfirmframework.wffweb.css.Font;
import com.webfirmframework.wffweb.css.FontFamily;
import com.webfirmframework.wffweb.css.FontSize;
import com.webfirmframework.wffweb.css.FontStyle;
import com.webfirmframework.wffweb.css.FontVariant;
import com.webfirmframework.wffweb.css.FontWeight;
import com.webfirmframework.wffweb.css.HeightCss;
import com.webfirmframework.wffweb.css.Left;
import com.webfirmframework.wffweb.css.LetterSpacing;
import com.webfirmframework.wffweb.css.LineHeight;
import com.webfirmframework.wffweb.css.ListStyleImage;
import com.webfirmframework.wffweb.css.ListStylePosition;
import com.webfirmframework.wffweb.css.ListStyleType;
import com.webfirmframework.wffweb.css.Margin;
import com.webfirmframework.wffweb.css.MarginBottom;
import com.webfirmframework.wffweb.css.MarginLeft;
import com.webfirmframework.wffweb.css.MarginRight;
import com.webfirmframework.wffweb.css.MarginTop;
import com.webfirmframework.wffweb.css.MaxHeight;
import com.webfirmframework.wffweb.css.MaxWidth;
import com.webfirmframework.wffweb.css.MinHeight;
import com.webfirmframework.wffweb.css.MinWidth;
import com.webfirmframework.wffweb.css.Outline;
import com.webfirmframework.wffweb.css.OutlineColor;
import com.webfirmframework.wffweb.css.OutlineOffset;
import com.webfirmframework.wffweb.css.OutlineStyle;
import com.webfirmframework.wffweb.css.OutlineWidth;
import com.webfirmframework.wffweb.css.Overflow;
import com.webfirmframework.wffweb.css.Padding;
import com.webfirmframework.wffweb.css.PaddingBottom;
import com.webfirmframework.wffweb.css.PaddingLeft;
import com.webfirmframework.wffweb.css.PaddingRight;
import com.webfirmframework.wffweb.css.PaddingTop;
import com.webfirmframework.wffweb.css.PageBreakAfter;
import com.webfirmframework.wffweb.css.PageBreakBefore;
import com.webfirmframework.wffweb.css.PageBreakInside;
import com.webfirmframework.wffweb.css.Position;
import com.webfirmframework.wffweb.css.Right;
import com.webfirmframework.wffweb.css.TableLayout;
import com.webfirmframework.wffweb.css.TextAlign;
import com.webfirmframework.wffweb.css.TextTransform;
import com.webfirmframework.wffweb.css.Top;
import com.webfirmframework.wffweb.css.UnicodeBidi;
import com.webfirmframework.wffweb.css.Visibility;
import com.webfirmframework.wffweb.css.WhiteSpace;
import com.webfirmframework.wffweb.css.WidthCss;
import com.webfirmframework.wffweb.css.WordSpacing;
import com.webfirmframework.wffweb.css.core.AbstractCssProperty;
import com.webfirmframework.wffweb.css.core.CssProperty;
import com.webfirmframework.wffweb.css.css3.AlignContent;
import com.webfirmframework.wffweb.css.css3.AlignItems;
import com.webfirmframework.wffweb.css.css3.AnimationDirection;
import com.webfirmframework.wffweb.css.css3.AnimationFillMode;
import com.webfirmframework.wffweb.css.css3.AnimationIterationCount;
import com.webfirmframework.wffweb.css.css3.AnimationPlayState;
import com.webfirmframework.wffweb.css.css3.BackfaceVisibility;
import com.webfirmframework.wffweb.css.css3.BackgroundClip;
import com.webfirmframework.wffweb.css.css3.BackgroundOrigin;
import com.webfirmframework.wffweb.css.css3.BackgroundSize;
import com.webfirmframework.wffweb.css.css3.BorderImageOutset;
import com.webfirmframework.wffweb.css.css3.BorderImageRepeat;
import com.webfirmframework.wffweb.css.css3.BorderImageSlice;
import com.webfirmframework.wffweb.css.css3.BorderImageSource;
import com.webfirmframework.wffweb.css.css3.BorderImageWidth;
import com.webfirmframework.wffweb.css.css3.BoxSizing;
import com.webfirmframework.wffweb.css.css3.ColumnCount;
import com.webfirmframework.wffweb.css.css3.ColumnFill;
import com.webfirmframework.wffweb.css.css3.ColumnGap;
import com.webfirmframework.wffweb.css.css3.ColumnRule;
import com.webfirmframework.wffweb.css.css3.ColumnRuleColor;
import com.webfirmframework.wffweb.css.css3.ColumnRuleStyle;
import com.webfirmframework.wffweb.css.css3.ColumnRuleWidth;
import com.webfirmframework.wffweb.css.css3.ColumnSpan;
import com.webfirmframework.wffweb.css.css3.ColumnWidth;
import com.webfirmframework.wffweb.css.css3.Columns;
import com.webfirmframework.wffweb.css.css3.Flex;
import com.webfirmframework.wffweb.css.css3.FlexBasis;
import com.webfirmframework.wffweb.css.css3.FlexDirection;
import com.webfirmframework.wffweb.css.css3.FlexGrow;
import com.webfirmframework.wffweb.css.css3.FlexShrink;
import com.webfirmframework.wffweb.css.css3.FlexWrap;
import com.webfirmframework.wffweb.css.css3.FontSizeAdjust;
import com.webfirmframework.wffweb.css.css3.FontStretch;
import com.webfirmframework.wffweb.css.css3.HangingPunctuation;
import com.webfirmframework.wffweb.css.css3.Icon;
import com.webfirmframework.wffweb.css.css3.JustifyContent;
import com.webfirmframework.wffweb.css.css3.MozBackgroundSize;
import com.webfirmframework.wffweb.css.css3.MozColumnCount;
import com.webfirmframework.wffweb.css.css3.MozColumnGap;
import com.webfirmframework.wffweb.css.css3.MozColumnRule;
import com.webfirmframework.wffweb.css.css3.MozColumnRuleColor;
import com.webfirmframework.wffweb.css.css3.MozColumnRuleStyle;
import com.webfirmframework.wffweb.css.css3.MozColumnRuleWidth;
import com.webfirmframework.wffweb.css.css3.MozColumnWidth;
import com.webfirmframework.wffweb.css.css3.MozFlex;
import com.webfirmframework.wffweb.css.css3.MozFlexBasis;
import com.webfirmframework.wffweb.css.css3.MozFlexGrow;
import com.webfirmframework.wffweb.css.css3.MozFlexShrink;
import com.webfirmframework.wffweb.css.css3.MozFlexWrap;
import com.webfirmframework.wffweb.css.css3.MozTextDecorationLine;
import com.webfirmframework.wffweb.css.css3.MozTextDecorationStyle;
import com.webfirmframework.wffweb.css.css3.MsFlex;
import com.webfirmframework.wffweb.css.css3.OBackgroundSize;
import com.webfirmframework.wffweb.css.css3.Opacity;
import com.webfirmframework.wffweb.css.css3.OverflowX;
import com.webfirmframework.wffweb.css.css3.OverflowY;
import com.webfirmframework.wffweb.css.css3.Perspective;
import com.webfirmframework.wffweb.css.css3.PerspectiveOrigin;
import com.webfirmframework.wffweb.css.css3.Resize;
import com.webfirmframework.wffweb.css.css3.TextAlignLast;
import com.webfirmframework.wffweb.css.css3.TextDecoration;
import com.webfirmframework.wffweb.css.css3.TextDecorationLine;
import com.webfirmframework.wffweb.css.css3.TextDecorationStyle;
import com.webfirmframework.wffweb.css.css3.TextJustify;
import com.webfirmframework.wffweb.css.css3.TransformStyle;
import com.webfirmframework.wffweb.css.css3.WebkitAnimationDirection;
import com.webfirmframework.wffweb.css.css3.WebkitAnimationFillMode;
import com.webfirmframework.wffweb.css.css3.WebkitAnimationPlayState;
import com.webfirmframework.wffweb.css.css3.WebkitBackfaceVisibility;
import com.webfirmframework.wffweb.css.css3.WebkitBackgroundSize;
import com.webfirmframework.wffweb.css.css3.WebkitColumnCount;
import com.webfirmframework.wffweb.css.css3.WebkitColumnGap;
import com.webfirmframework.wffweb.css.css3.WebkitColumnRule;
import com.webfirmframework.wffweb.css.css3.WebkitColumnRuleColor;
import com.webfirmframework.wffweb.css.css3.WebkitColumnRuleStyle;
import com.webfirmframework.wffweb.css.css3.WebkitColumnRuleWidth;
import com.webfirmframework.wffweb.css.css3.WebkitColumnSpan;
import com.webfirmframework.wffweb.css.css3.WebkitColumnWidth;
import com.webfirmframework.wffweb.css.css3.WebkitFlex;
import com.webfirmframework.wffweb.css.css3.WebkitFlexBasis;
import com.webfirmframework.wffweb.css.css3.WebkitFlexGrow;
import com.webfirmframework.wffweb.css.css3.WebkitFlexShrink;
import com.webfirmframework.wffweb.css.css3.WebkitFlexWrap;
import com.webfirmframework.wffweb.css.css3.WebkitTransformStyle;
import com.webfirmframework.wffweb.css.css3.WordBreak;
import com.webfirmframework.wffweb.css.css3.WordWrap;
import com.webfirmframework.wffweb.csswff.CustomCssProperty;
import com.webfirmframework.wffweb.informer.StateChangeInformer;
import com.webfirmframework.wffweb.tag.html.attribute.AttributeNameConstants;
import com.webfirmframework.wffweb.tag.html.attribute.core.AbstractAttribute;
import com.webfirmframework.wffweb.tag.html.identifier.GlobalAttributable;

// @formatter:off
/**
 * @author WFF</br>
 * <pre>
 * {@link http://www.w3schools.com/cssref/css3_pr_align-content.asp}
 * </br>
 *CSS Properties :-</br>
 *align-content: stretch|center|flex-start|flex-end|space-between|space-around|initial|inherit;</br>
 *align-items: stretch|center|flex-start|flex-end|baseline|initial|inherit;</br>
 *align-self: auto|stretch|center|flex-start|flex-end|baseline|initial|inherit; </br>
 *animation: name duration timing-function delay iteration-count direction fill-mode play-state; </br>
 *animation-delay: time|initial|inherit;</br>
 *animation-direction: normal|reverse|alternate|alternate-reverse|initial|inherit; </br>
 *animation-duration: time|initial|inherit; </br>
 *animation-fill-mode: none|forwards|backwards|both|initial|inherit; </br>
 *animation-iteration-count: number|infinite|initial|inherit;</br>
 *animation-name: keyframename|none|initial|inherit; </br>
 *animation-play-state: paused|running|initial|inherit;</br>
 *animation-timing-function: linear|ease|ease-in|ease-out|cubic-bezier(n,n,n,n)|initial|inherit;</br>
 *backface-visibility: visible|hidden|initial|inherit; </br>
 *background: color position/size repeat origin clip attachment image|initial|inherit; </br>
 *background-attachment: scroll|fixed|local|initial|inherit;</br>
 *background-clip: border-box|padding-box|content-box|initial|inherit; </br>
 *background-color: color|transparent|initial|inherit; </br>
 *background-image: url|none|initial|inherit; </br>
 *background-origin: padding-box|border-box|content-box|initial|inherit; </br>
 *background-position: left top|left center|left bottom|right top|right center|right bottom|center top|center center|center bottom|x% y%|xpos ypos|initial|inherit; </br>
 *background-repeat: repeat|repeat-x|repeat-y|no-repeat|initial|inherit; </br>
 *background-size: auto|length|cover|contain|initial|inherit;</br>
 *border: border-width border-style border-color|initial|inherit; </br>
 *border-bottom: border-width border-style border-color|initial|inherit;</br>
 *border-bottom-color: color|transparent|initial|inherit;</br>
 *border-bottom-left-radius: length|% [length|%]|initial|inherit;</br>
 *border-bottom-right-radius: length|% [length|%]|initial|inherit;</br>
 *border-bottom-style:none|hidden|dotted|dashed|solid|double|groove|ridge|inset|outset|initial|inherit; </br>
 *border-bottom-width: medium|thin|thick|length|initial|inherit; </br>
 *border-collapse: separate|collapse|initial|inherit; </br>
 *border-color: color|transparent|initial|inherit; </br>
 *border-image: source slice width outset repeat|initial|inherit;</br>
 *border-image-outset: length|number|initial|inherit;</br>
 *border-image-repeat: stretch|repeat|round|initial|inherit; </br>
 *border-image-slice: number|%|fill|initial|inherit; </br>
 *border-image-source: none|image|initial|inherit; </br>
 *border-image-width: number|%|auto|initial|inherit;</br>
 *border-left: border-width border-style border-color|initial|inherit; </br>
 *border-left-color: color|transparent|initial|inherit;</br>
 *border-left-style:none|hidden|dotted|dashed|solid|double|groove|ridge|inset|outset|initial|inherit; </br>
 *border-left-width: medium|thin|thick|length|initial|inherit; </br>
 *border-radius  </br>
 *border-right </br>
 *border-right-color</br>
 *border-right-style </br>
 *border-right-width </br>
 *border-spacing </br>
 *border-style </br>
 *border-top </br>
 *border-top-color</br>
 *border-top-left-radius</br>
 *border-top-right-radius </br>
 *border-top-style </br>
 *border-top-width </br>
 *border-width </br>
 *bottom </br>
 *box-shadow</br>
 *box-sizing     </br>
 *caption-side </br>
 *clear </br>
 *clip </br>
 *color </br>
 *column-count</br>
 *column-fill </br>
 *column-gap </br>
 *column-rule </br>
 *column-rule-color</br>
 *column-rule-style </br>
 *column-rule-width </br>
 *column-span </br>
 *column-width </br>
 *columns </br>
 *content </br>
 *counter-increment</br>
 *counter-reset </br>
 *cursor </br>
 *direction </br>
 *display </br>
 *empty-cells </br>
 *flex </br>
 *flex-basis</br>
 *flex-direction </br>
 *flex-flow </br>
 *flex-grow </br>
 *flex-shrink </br>
 *flex-wrap </br>
 *float </br>
 *font </br>
 *@font-face : cannot be used directly</br>
 *font-family</br>
 *font-size</br>
 *font-size-adjust</br>
 *font-stretch</br>
 *font-style</br>
 *font-variant</br>
 *font-weight</br>
 *hanging-punctuation</br>
 *height</br>
 *icon</br>
 *justify-content</br>
 *@keyframes : cannot be used directly</br>
 *left</br>
 *letter-spacing</br>
 *line-height</br>
 *list-style</br>
 *list-style-image</br>
 *list-style-position</br>
 *list-style-type</br>
 *margin</br>
 *margin-bottom</br>
 *margin-left</br>
 *margin-right</br>
 *margin-top</br>
 *max-height</br>
 *max-width</br>
 *@media : cannot be used directly</br>
 *min-height</br>
 *min-width</br>
 *nav-down</br>
 *nav-index</br>
 *nav-left</br>
 *nav-right</br>
 *nav-up</br>
 *opacity</br>
 *order</br>
 *outline</br>
 *outline-color</br>
 *outline-offset</br>
 *outline-style</br>
 *outline-width</br>
 *overflow</br>
 *overflow-x</br>
 *overflow-y</br>
 *padding</br>
 *padding-bottom</br>
 *padding-left</br>
 *padding-right</br>
 *padding-top</br>
 *page-break-after</br>
 *page-break-before</br>
 *page-break-inside</br>
 *perspective</br>
 *perspective-origin</br>
 *position</br>
 *quotes</br>
 *resize</br>
 *right</br>
 *tab-size</br>
 *table-layout</br>
 *text-align</br>
 *text-align-last</br>
 *text-decoration</br>
 *text-decoration-color</br>
 *text-decoration-line</br>
 *text-decoration-style</br>
 *text-indent</br>
 *text-justify</br>
 *text-overflow</br>
 *text-shadow</br>
 *text-transform</br>
 *top</br>
 *transform</br>
 *transform-origin</br>
 *transform-style</br>
 *transition</br>
 *transition-delay</br>
 *transition-duration</br>
 *transition-property</br>
 *transition-timing-function</br>
 *unicode-bidi</br>
 *vertical-align</br>
 *visibility</br>
 *white-space</br>
 *width</br>
 *word-break</br>
 *word-spacing</br>
 *word-wrap</br>
 *z-index</br>
 *</pre>
 **/
// @formatter:on
public class Style extends AbstractAttribute
        implements GlobalAttributable, StateChangeInformer<CssProperty> {

    /**
     *
     */
    private static final long serialVersionUID = 1_0_0L;

    public static final Logger LOGGER = Logger.getLogger(Style.class.getName());

    /**
     * key as style name, and value as class which extends or value as enum
     * class which implements {@code CssProperty} interface.
     * {@code AbstractCssProperty} class.
     */
    private static final Map<String, Class<?>> CSSPROPERTY_CLASSES = new HashMap<String, Class<?>>();

    /**
     * style name as key and value as object of {@code AbstractCssProperty}
     */
    protected Map<String, AbstractCssProperty<?>> abstractCssPropertyClassObjects;

    private boolean fromAddCssProperty;

    private boolean fromRemoveCssProperty;

    private boolean clearOnlyInCssProperties;

    static {
        CSSPROPERTY_CLASSES.put(CssNameConstants.ALIGN_CONTENT,
                AlignContent.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.ALIGN_ITEMS, AlignItems.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.BACKFACE_VISIBILITY,
                BackfaceVisibility.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.WEBKIT_BACKFACE_VISIBILITY,
                WebkitBackfaceVisibility.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.BORDER_BOTTOM_STYLE,
                BorderBottomStyle.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.BORDER_COLLAPSE,
                BorderCollapse.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.BORDER_LEFT_STYLE,
                BorderLeftStyle.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.BORDER_RIGHT_STYLE,
                BorderRightStyle.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.BORDER_TOP_STYLE,
                BorderTopStyle.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.BORDER_STYLE,
                BorderStyle.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.COLUMN_RULE_STYLE,
                ColumnRuleStyle.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.WEBKIT_COLUMN_RULE_STYLE,
                WebkitColumnRuleStyle.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.MOZ_COLUMN_RULE_STYLE,
                MozColumnRuleStyle.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.FONT_STYLE, FontStyle.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.OUTLINE_STYLE,
                OutlineStyle.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.TEXT_DECORATION_STYLE,
                TextDecorationStyle.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.MOZ_TEXT_DECORATION_STYLE,
                MozTextDecorationStyle.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.TRANSFORM_STYLE,
                TransformStyle.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.WEBKIT_TRANSFORM_STYLE,
                WebkitTransformStyle.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.OVERFLOW, Overflow.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.OVERFLOW_X, OverflowX.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.OVERFLOW_Y, OverflowY.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.PAGE_BREAK_AFTER,
                PageBreakAfter.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.PAGE_BREAK_BEFORE,
                PageBreakBefore.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.PAGE_BREAK_INSIDE,
                PageBreakInside.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.LIST_STYLE_TYPE,
                ListStyleType.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.LIST_STYLE_POSITION,
                ListStylePosition.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.POSITION, Position.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.RESIZE, Resize.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.TABLE_LAYOUT,
                TableLayout.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.TEXT_ALIGN, TextAlign.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.TEXT_ALIGN_LAST,
                TextAlignLast.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.TEXT_DECORATION,
                TextDecoration.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.TEXT_DECORATION_LINE,
                TextDecorationLine.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.MOZ_TEXT_DECORATION_LINE,
                MozTextDecorationLine.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.TEXT_JUSTIFY,
                TextJustify.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.TEXT_TRANSFORM,
                TextTransform.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.UNICODE_BIDI,
                UnicodeBidi.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.DIRECTION, Direction.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.VISIBILITY, Visibility.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.WHITE_SPACE, WhiteSpace.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.WORD_BREAK, WordBreak.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.WORD_WRAP, WordWrap.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.ANIMATION_DIRECTION,
                AnimationDirection.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.WEBKIT_ANIMATION_DIRECTION,
                WebkitAnimationDirection.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.ANIMATION_FILL_MODE,
                AnimationFillMode.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.WEBKIT_ANIMATION_FILL_MODE,
                WebkitAnimationFillMode.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.ANIMATION_PLAY_STATE,
                AnimationPlayState.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.WEBKIT_ANIMATION_PLAY_STATE,
                WebkitAnimationPlayState.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.BACKGROUND_ATTACHMENT,
                BackgroundAttachment.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.BACKGROUND_CLIP,
                BackgroundClip.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.BACKGROUND_ORIGIN,
                BackgroundOrigin.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.BACKGROUND_REPEAT,
                BackgroundRepeat.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.BOX_SIZING, BoxSizing.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.CAPTION_SIDE,
                CaptionSide.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.CLEAR, Clear.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.COLUMN_FILL, ColumnFill.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.DISPLAY, Display.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.EMPTY_CELLS, EmptyCells.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.FLEX_DIRECTION,
                FlexDirection.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.FLOAT, FloatCss.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.FONT_STRETCH,
                FontStretch.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.FONT_VARIANT,
                FontVariant.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.HANGING_PUNCTUATION,
                HangingPunctuation.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.JUSTIFY_CONTENT,
                JustifyContent.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.FLEX_WRAP, FlexWrap.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.MOZ_FLEX_WRAP,
                MozFlexWrap.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.WEBKIT_FLEX_WRAP,
                WebkitFlexWrap.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.COLUMN_SPAN, ColumnSpan.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.WEBKIT_COLUMN_SPAN,
                WebkitColumnSpan.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.FONT_WEIGHT, FontWeight.class);

        CSSPROPERTY_CLASSES.put(CssNameConstants.CURSOR, Cursor.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.WIDTH, WidthCss.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.HEIGHT, HeightCss.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.MIN_WIDTH, MinWidth.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.MAX_WIDTH, MaxWidth.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.MIN_HEIGHT, MinHeight.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.MAX_HEIGHT, MaxHeight.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.LIST_STYLE_TYPE,
                ListStyleType.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.LIST_STYLE_POSITION,
                ListStylePosition.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.LIST_STYLE_IMAGE,
                ListStyleImage.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.WORD_SPACING,
                WordSpacing.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.BORDER_BOTTOM_WIDTH,
                BorderBottomWidth.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.BORDER_LEFT_WIDTH,
                BorderLeftWidth.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.BORDER_RIGHT_WIDTH,
                BorderRightWidth.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.BORDER_TOP_WIDTH,
                BorderTopWidth.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.COLUMN_RULE_WIDTH,
                ColumnRuleWidth.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.WEBKIT_COLUMN_RULE_WIDTH,
                WebkitColumnRuleWidth.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.MOZ_COLUMN_RULE_WIDTH,
                MozColumnRuleWidth.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.COLUMN_WIDTH,
                ColumnWidth.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.WEBKIT_COLUMN_WIDTH,
                WebkitColumnWidth.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.MOZ_COLUMN_WIDTH,
                MozColumnWidth.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.COLUMN_RULE_COLOR,
                ColumnRuleColor.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.WEBKIT_COLUMN_RULE_COLOR,
                WebkitColumnRuleColor.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.MOZ_COLUMN_RULE_COLOR,
                MozColumnRuleColor.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.COLUMN_RULE, ColumnRule.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.WEBKIT_COLUMN_RULE,
                WebkitColumnRule.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.MOZ_COLUMN_RULE,
                MozColumnRule.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.BACKGROUND_COLOR,
                BackgroundColor.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.BORDER_BOTTOM_COLOR,
                BorderBottomColor.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.BORDER_TOP_COLOR,
                BorderTopColor.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.BORDER_LEFT_COLOR,
                BorderLeftColor.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.BORDER_RIGHT_COLOR,
                BorderRightColor.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.BORDER_COLOR,
                BorderColor.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.COLOR, Color.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.OUTLINE_COLOR,
                OutlineColor.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.OUTLINE_WIDTH,
                OutlineWidth.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.OUTLINE_OFFSET,
                OutlineOffset.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.BORDER_WIDTH,
                BorderWidth.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.BORDER, Border.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.PADDING_TOP, PaddingTop.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.PADDING_RIGHT,
                PaddingRight.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.PADDING_BOTTOM,
                PaddingBottom.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.PADDING_LEFT,
                PaddingLeft.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.PADDING, Padding.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.BORDER_TOP, BorderTop.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.BORDER_RIGHT,
                BorderRight.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.BORDER_BOTTOM,
                BorderBottom.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.BORDER_LEFT, BorderLeft.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.MARGIN_TOP, MarginTop.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.MARGIN_RIGHT,
                MarginRight.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.MARGIN_BOTTOM,
                MarginBottom.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.MARGIN_LEFT, MarginLeft.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.MARGIN, Margin.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.OUTLINE, Outline.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.TOP, Top.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.RIGHT, Right.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.BOTTOM, Bottom.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.LEFT, Left.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.COLUMN_GAP, ColumnGap.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.MOZ_COLUMN_GAP,
                MozColumnGap.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.WEBKIT_COLUMN_GAP,
                WebkitColumnGap.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.LETTER_SPACING,
                LetterSpacing.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.LINE_HEIGHT, LineHeight.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.BORDER_SPACING,
                BorderSpacing.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.BACKGROUND_SIZE,
                BackgroundSize.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.WEBKIT_BACKGROUND_SIZE,
                WebkitBackgroundSize.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.MOZ_BACKGROUND_SIZE,
                MozBackgroundSize.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.O_BACKGROUND_SIZE,
                OBackgroundSize.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.OPACITY, Opacity.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.PERSPECTIVE,
                Perspective.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.PERSPECTIVE_ORIGIN,
                PerspectiveOrigin.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.BACKGROUND_IMAGE,
                BackgroundImage.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.ICON, Icon.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.FLEX_BASIS, FlexBasis.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.WEBKIT_FLEX_BASIS,
                WebkitFlexBasis.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.MOZ_FLEX_BASIS,
                MozFlexBasis.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.ANIMATION_ITERATION_COUNT,
                AnimationIterationCount.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.FLEX_GROW, FlexGrow.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.MOZ_FLEX_GROW,
                MozFlexGrow.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.WEBKIT_FLEX_GROW,
                WebkitFlexGrow.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.FLEX_SHRINK, FlexShrink.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.MOZ_FLEX_SHRINK,
                MozFlexShrink.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.WEBKIT_FLEX_SHRINK,
                WebkitFlexShrink.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.FONT_SIZE_ADJUST,
                FontSizeAdjust.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.COLUMN_COUNT,
                ColumnCount.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.MOZ_COLUMN_COUNT,
                MozColumnCount.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.WEBKIT_COLUMN_COUNT,
                WebkitColumnCount.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.FLEX, Flex.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.WEBKIT_FLEX, WebkitFlex.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.MOZ_FLEX, MozFlex.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.MS_FLEX, MsFlex.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.FONT_FAMILY, FontFamily.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.FONT_SIZE, FontSize.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.FONT, Font.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.COLUMNS, Columns.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.BORDER_IMAGE_REPEAT,
                BorderImageRepeat.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.BORDER_IMAGE_WIDTH,
                BorderImageWidth.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.BORDER_IMAGE_OUTSET,
                BorderImageOutset.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.BORDER_IMAGE_SLICE,
                BorderImageSlice.class);
        CSSPROPERTY_CLASSES.put(CssNameConstants.BORDER_IMAGE_SOURCE,
                BorderImageSource.class);
    }

    {
        super.setAttributeName(AttributeNameConstants.STYLE);
        abstractCssPropertyClassObjects = new HashMap<String, AbstractCssProperty<?>>() {

            private static final long serialVersionUID = 1_0_0L;

            @Override
            public AbstractCssProperty<?> put(final String key,
                    final AbstractCssProperty<?> value) {
                // must not be null
                value.setAlreadyInUse(true);
                value.setStateChangeInformer(Style.this);

                final AbstractCssProperty<?> previous = super.put(key, value);
                if (previous != null && previous != value) {
                    previous.setAlreadyInUse(false);
                }
                return previous;
            }

            @Override
            public AbstractCssProperty<?> remove(final Object key) {
                final AbstractCssProperty<?> previous = super.remove(key);
                if (previous != null) {
                    previous.setAlreadyInUse(false);
                }
                return previous;
            }

            @Override
            public void putAll(
                    final Map<? extends String, ? extends AbstractCssProperty<?>> m) {
                super.putAll(m);
                throw new IllegalArgumentException(
                        "add needful code to make the replaced abstractCssProperty objects to set setAlreadyInUse(false) and added objects to set setAlreadyInUse(false)");
            }
        };
        init();
    }

    public Style() {
    }

    /**
     * @param styles
     *            styles separated by semicolon.</br>
     *            eg :- {@code color:blue;text-align:center }
     */
    public Style(final String styles) {
        extractStylesAndAddToAttributeValueMap(styles);
    }

    /**
     * @param styles
     *            {@code Map} containing styles,</br>
     *            eg : </br>
     *            {@code
     * Map<String, String> styles = new HashMap<String, String>();
     * } </br>
     *            {@code
     * styles.put("color", "green");
     * }
     *
     */
    public Style(final Map<String, String> styles) {
        addAllToAttributeValueMap(styles);
    }

    /**
     * @param cssProperties
     *            eg :-
     *            {@code addCssProperties(AlignContent.FLEX_END, AlignItems.FLEX_END)}
     * @since 1.0.0
     * @author WFF
     */
    public Style(final CssProperty... cssProperties) {
        addCssProperties(cssProperties);
    }

    /**
     * @param important
     *            true to mark the given style as important.
     * @param cssProperties
     *            eg :-
     *            {@code addStyles(true, AlignContent.FLEX_END, AlignItems.FLEX_END)}
     * @since 1.0.0
     * @author WFF
     */
    public Style(final boolean important, final CssProperty... cssProperties) {
        addCssProperties(important, cssProperties);
    }

    /**
     * @param cssName
     *            custom css name eg:- {@code "custom-width"}
     * @param cssClass
     *            corresponding custom css class/enum for {@code "custom-width"}
     *            , may be {@code CustomWidth.class}
     * @since 1.0.0
     * @author WFF
     */
    public static void addSupportForNewCustomCssClass(final String cssName,
            final Class<?> cssClass) {
        if (cssName != null && cssClass != null) {
            CSSPROPERTY_CLASSES.put(cssName, cssClass);
        }
    }

    /**
     * removes the support for the class from {@code Style class} or its
     * extended class.
     *
     * @param cssClass
     * @since 1.0.0
     * @author WFF
     */
    public static void removeSupportOfCssClass(final Class<?> cssClass) {
        if (cssClass != null) {
            for (final Entry<String, Class<?>> entry : CSSPROPERTY_CLASSES
                    .entrySet()) {
                if (entry.getValue() == cssClass) {
                    CSSPROPERTY_CLASSES.remove(entry.getKey());
                    break;
                }
            }
        }
    }

    /**
     * removes the support for the classes from {@code Style class} or its
     * extended class.
     *
     * @param cssClass
     * @since 1.0.0
     * @author WFF
     */
    public static void removeSupportOfCssClasses(final Class<?>... cssClasses) {
        for (final Class<?> cssClass : cssClasses) {
            if (cssClass != null) {
                removeSupportOfCssClass(cssClass);
            }
        }
    }

    /**
     * @param cssProperties
     *            styles separated by semicolon.</br>
     *            eg :- {@code color:blue;text-align:center }
     *
     * @since 1.0.0
     * @author WFF
     */
    public void addCssProperties(final String cssProperties) {
        extractStylesAndAddToAttributeValueMap(cssProperties);
    }

    /**
     * @param cssProperties
     *            {@code Map} containing styles,</br>
     *            eg : </br>
     *            {@code
     * Map<String, String> cssProperties = new HashMap<String, String>();
     * } </br>
     *            {@code
     * cssProperties.put("color", "green");
     * }
     * @return true if the values are added otherwise false.
     */
    public boolean addCssProperties(final Map<String, String> cssProperties) {
        final boolean addAllToAttributeValueMap = addAllToAttributeValueMap(
                cssProperties);
        if (addAllToAttributeValueMap) {
            final Set<String> cssNames = cssProperties.keySet();
            for (final String cssName : cssNames) {
                getCssProperty(cssName);// to save the corresponding object to
                                        // abstractCssPropertyClassObjects
            }
        }
        return addAllToAttributeValueMap;
    }

    /**
     * eg: {@code addCssProperty("color", "green")}
     *
     * @param cssName
     *            eg: color
     * @param cssValue
     *            eg: green (for color styleName)
     * @since 1.0.0
     * @author WFF
     * @return true if the cssName and cssValue has been added or modified. If
     *         it contains same style name and value then it will return false.
     */
    public boolean addCssProperty(final String cssName, final String cssValue) {
        if (cssName == null) {
            throw new NullValueException("styleName cannot be null");
        }
        if (cssName.trim().endsWith(":")) {
            throw new InvalidValueException(
                    "cssName can not end with : (colon)");
        }
        if (cssValue == null) {
            throw new NullValueException("cssValue cannot be null");
        }
        if (cssValue.trim().startsWith(":") || cssValue.trim().endsWith(";")) {
            throw new InvalidValueException(
                    "value can not start with : (colon) or end with ; (semicolon)");
        }

        final boolean addToAttributeValueMap = addToAttributeValueMap(cssName,
                cssValue);
        if (addToAttributeValueMap) {
            getCssProperty(cssName);// to save the corresponding object to
                                    // abstractCssPropertyClassObjects
        }
        return addToAttributeValueMap;
    }

    /**
     * @param important
     *            true to mark the given style as important.
     * @param cssProperties
     *            eg :-
     *            {@code addCssProperties(true, AlignContent.FLEX_END, AlignItems.FLEX_END)}
     * @since 1.0.0
     * @author WFF
     */
    public void addCssProperties(final boolean important,
            final CssProperty... cssProperties) {
        if (important) {
            for (final CssProperty styleValue : cssProperties) {
                final CssProperty cssProperty = styleValue;
                final boolean addToAttributeValueMap = addToAttributeValueMap(
                        cssProperty.getCssName(),
                        styleValue.getCssValue() + " " + IMPORTANT);
                if (addToAttributeValueMap) {
                    if (styleValue instanceof AbstractCssProperty) {
                        abstractCssPropertyClassObjects.put(
                                cssProperty.getCssName(),
                                (AbstractCssProperty<?>) cssProperty);
                    }
                }
            }
        } else {
            for (final CssProperty styleValue : cssProperties) {

                final CssProperty cssProperty = styleValue;

                final boolean addToAttributeValueMap = addToAttributeValueMap(
                        cssProperty.getCssName(), styleValue.getCssValue());

                if (addToAttributeValueMap
                        && styleValue instanceof AbstractCssProperty) {
                    abstractCssPropertyClassObjects.put(
                            cssProperty.getCssName(),
                            (AbstractCssProperty<?>) cssProperty);
                }
            }
        }
    }

    /**
     * @param cssProperties
     *            eg :-
     *            {@code addStyles(AlignContent.FLEX_END, AlignItems.FLEX_END)}
     * @since 1.0.0
     * @author WFF
     * @return the added {@code CssProperty} objects as a
     *         {@code List<CssProperty>}.
     */
    public List<CssProperty> addCssProperties(
            final CssProperty... cssProperties) {
        final List<CssProperty> addedCssPropertys = new ArrayList<>();
        for (final CssProperty cssProperty : cssProperties) {
            addedCssPropertys.add(addCssProperty(cssProperty));
        }
        return addedCssPropertys;
    }

    /**
     * adds each cssProperty from the given collection. <br/>
     * NB:- After using this method, adding or removing any value in the
     * collection will not have any effect on {@code Style}, so this method may
     * be removed later.
     *
     * @param cssProperties
     *            eg :-
     *            {@code addStyles(Arrays.asList(AlignContent.FLEX_END, AlignItems.FLEX_END))}
     * @since 1.0.0
     * @author WFF
     * @return the added {@code CssProperty} objects as a
     *         {@code List<CssProperty>}.
     */
    @Deprecated
    public List<CssProperty> addCssProperties(
            final Collection<? extends CssProperty> cssProperties) {
        if (cssProperties == null) {
            throw new NullValueException("");
        }
        final List<CssProperty> addedCssPropertys = new ArrayList<>();
        for (final CssProperty cssProperty : cssProperties) {
            addedCssPropertys.add(addCssProperty(cssProperty));
        }
        return addedCssPropertys;
    }

    /**
     * @param cssProperty
     *            eg :- {@code addStyle(AlignContent.FLEX_END)}
     * @since 1.0.0
     * @author WFF
     * @return the added {@code CssProperty} objects as a {@code CssProperty}.
     */
    public CssProperty addCssProperty(CssProperty cssProperty) {
        if (cssProperty instanceof AbstractCssProperty<?>) {
            final AbstractCssProperty<?> abstractCssProperty = (AbstractCssProperty<?>) cssProperty;
            if (abstractCssProperty.isAlreadyInUse()
                    && abstractCssPropertyClassObjects
                            .get(cssProperty.getCssName()) != cssProperty) {
                try {
                    cssProperty = abstractCssProperty.clone();
                    // hashcode should be replaced with getUuid after its
                    // implementation.
                    LOGGER.warning("cloned cssProperty " + cssProperty
                            + "(hashcode: " + cssProperty.hashCode()
                            + ") as it is already used in another tag");
                } catch (final CloneNotSupportedException e) {
                    e.printStackTrace();
                }
            }
        }
        final boolean addToAttributeValueMap = addToAttributeValueMap(
                cssProperty.getCssName(), cssProperty.getCssValue());
        if (addToAttributeValueMap || abstractCssPropertyClassObjects
                .get(cssProperty.getCssName()) != cssProperty) {
            if (cssProperty instanceof AbstractCssProperty) {

                abstractCssPropertyClassObjects.put(cssProperty.getCssName(),
                        (AbstractCssProperty<?>) cssProperty);
                // internally does the following
                // ((AbstractCssProperty<?>) cssProperty).setAlreadyInUse(true);
                // ((AbstractCssProperty<?>) cssProperty)
                // .setStateChangeInformer(this);
            }
            setFromAddCssProperty(true);
            cssProperties.add(cssProperty);
            setFromAddCssProperty(false);
            return cssProperty;
        }
        return null;
    }

    /**
     * removes the style.
     *
     * @param cssName
     *            eg: color
     * @since 1.0.0
     * @author WFF
     * @return true if the given cssName (as well as value contained
     *         corresponding to it) has been removed, if it contains no cssName
     *         then false.
     */
    public boolean removeCssProperty(final String cssName) {
        if (getAttributeValueMap().get(cssName) != null) {
            setFromRemoveCssProperty(true);
            cssProperties.remove(getCssProperty(cssName));
            setFromRemoveCssProperty(false);
        }
        final boolean removeFromAttributeValueMap = removeFromAttributeValueMap(
                cssName);
        if (removeFromAttributeValueMap) {
            abstractCssPropertyClassObjects.remove(cssName);
        }
        return removeFromAttributeValueMap;
    }

    /**
     * removes only if it contains the given style name and value.
     *
     * @param cssName
     *            eg: color
     * @param value
     *            eg: green (for color styleName)
     * @since 1.0.0
     * @return true if the given syleName (as well as value contained
     *         corresponding to it) has been removed, or false if it doesn't
     *         contain the given styleName and value.
     * @author WFF
     */
    public boolean removeCssProperty(final String cssName,
            final String cssValue) {

        final String value = getAttributeValueMap().get(cssName);
        // the value may contain !important that's why startsWith method is used
        // here.
        if (value != null && value.startsWith(cssValue.toLowerCase()
                .replace(CssConstants.IMPORTANT, "").trim())) {
            return removeCssProperty(cssName);
        }

        return false;
    }

    /**
     * removes the style.
     *
     * @param cssProperty
     * @since 1.0.0
     * @return true if the given syleName (as well as value contained
     *         corresponding to it) has been removed, or false if it doesn't
     *         contain the given styleName and value.
     * @author WFF
     */
    public boolean removeCssProperty(final CssProperty cssProperty) {
        final AbstractCssProperty<?> abstractCssProperty = abstractCssPropertyClassObjects
                .get(cssProperty.getCssName());
        if (abstractCssProperty != null && abstractCssProperty != cssProperty) {
            LOGGER.warning(
                    "The added CssProperty object is different. Use the same object which was used to add the style.");
            return false;
        }
        setFromRemoveCssProperty(true);
        cssProperties.remove(cssProperty);
        setFromRemoveCssProperty(false);
        return removeCssProperty(cssProperty.getCssName(),
                cssProperty.getCssValue());
    }

    /**
     * @param cssProperties
     * @return the {@code List<Boolean>} of status whether the corresponding
     *         object has been removed.
     * @since 1.0.0
     * @author WFF
     */
    public List<Boolean> removeCssProperties(
            final CssProperty... cssProperties) {
        final List<Boolean> removedStatus = new ArrayList<Boolean>();
        for (final CssProperty cssProperty : cssProperties) {
            removedStatus.add(removeCssProperty(cssProperty));
        }
        return removedStatus;
    }

    /**
     * NB:- After using this method, adding or removing any value in the
     * collection will not have any effect on {@code Style}, so this method may
     * be removed later.
     *
     * @param cssProperties
     * @return the {@code List<Boolean>} of status whether the corresponding
     *         object has been removed.
     * @since 1.0.0
     * @author WFF
     */
    @Deprecated
    public List<Boolean> removeCssProperties(
            final Collection<CssProperty> cssProperties) {
        final List<Boolean> removedStatus = new ArrayList<Boolean>();
        for (final CssProperty cssProperty : cssProperties) {
            removedStatus.add(removeCssProperty(cssProperty));
        }
        return removedStatus;
    }

    /**
     * marks as important. i.e. it will add {@code !important} from the
     * corresponding styleName value
     *
     * @param styleName
     * @return true if it's marked as important or false if it already marked as
     *         important
     * @since 1.0.0
     * @author WFF
     */
    public boolean markAsImportant(final String styleName) {
        if (styleName == null) {
            return false;
        }
        final String trimmedStyleName = styleName.trim();
        final String value = getAttributeValueMap().get(trimmedStyleName);
        if (value != null && !value.isEmpty()) {
            return addToAttributeValueMap(trimmedStyleName,
                    value + " " + IMPORTANT);
        }
        return false;
    }

    /**
     * marks as unimportant. i.e. it will remove {@code !important} from the
     * corresponding styleName value
     *
     * @param styleName
     * @return true if it's marked as unimportant or false if it already marked
     *         as unimportant
     * @since 1.0.0
     * @author WFF
     */
    public boolean markAsUnimportant(final String styleName) {
        if (styleName == null) {
            return false;
        }
        final String trimmedStyleName = styleName.trim();
        final String value = getAttributeValueMap().get(trimmedStyleName);
        if (value != null && !value.isEmpty()) {
            return addToAttributeValueMap(trimmedStyleName,
                    value.replace(IMPORTANT, "").trim());
        }
        return false;
    }

    private void extractStylesAndAddToAttributeValueMap(final String styles) {
        if (styles == null || styles.trim().isEmpty()) {
            return;
        }
        final String[] stylesArray = styles.split(";");
        for (final String each : stylesArray) {
            if (!each.trim().isEmpty()) {
                final String[] styleNameValue = each.split(":");
                if (styleNameValue.length == 2
                        && !styleNameValue[0].trim().isEmpty()
                        && !styleNameValue[1].trim().isEmpty()) {
                    addToAttributeValueMap(styleNameValue[0],
                            styleNameValue[1]);
                    getCssProperty(each);// to save the corresponding object to
                                         // abstractCssPropertyClassObjects
                } else {
                    LOGGER.warning("\"" + styles
                            + "\" contains invalid value or no value for any style name in it.");
                }
            }
        }
    }

    // TODO many functionalities have to be added refer style attribute and add

    /**
     * invokes only once per object
     *
     * @author WFF
     * @since 1.0.0
     */
    protected void init() {
    }

    /**
     * @param cssProperty
     * @return true if the given style value has been set as important otherwise
     *         false. eg : for content-align : flex-end,
     *         {@code isImportant(AlignContent.FLEX_END)} will return true but
     *         {@code isImportant(AlignContent.CENTER)} will return false. Use
     *         {@code isImportant(alignContent.getName())} or
     *         {@code isImportant(StyleContants.ALIGN_CONTENT)} to avoid
     *         checking of the value.
     * @since 1.0.0
     * @author WFF
     */
    public boolean isImportant(final CssProperty cssProperty) {
        String value = getAttributeValueMap().get(cssProperty.getCssName());
        if (value != null) {
            value = value.toUpperCase();
            return value.contains(cssProperty.getCssValue().toUpperCase())
                    && value.contains(IMPORTANT_UPPERCASE);
        }

        return false;
    }

    /**
     * @param styleName
     * @return true if the given style name is marked as important. eg:- for
     *         content-align : flex-end,
     *         {@code isImportant(alignContent.getName())} and
     *         {@code isImportant(StyleContants.ALIGN_CONTENT)} will return
     *         true.
     * @since 1.0.0
     * @author WFF
     */
    public boolean isImportant(final String styleName) {

        String value = getAttributeValueMap().get(styleName);
        if (value != null) {
            value = value.toUpperCase();
            return value.contains(IMPORTANT_UPPERCASE);
        }

        return false;
    }

    /**
     * @param cssName
     * @return the Enum object which implements {@code CssProperty} interface.
     *         This object be may type casted in to the corresponding enum,
     *         eg:-</br>
     *         {@code CssProperty cssProperty = style.getCssProperty(CssConstants.ALIGN_CONTENT);}
     *         </br>
     *         {@code if (cssProperty != null &&
     *         CssConstants.ALIGN_CONTENT.equals(cssProperty.getName())) } </br>
     *         {@code      AlignContent alignContent = (AlignContent) cssProperty;}
     *         </br>
     *         {@code  } </br>
     * @since 1.0.0
     * @author WFF
     */
    @SuppressWarnings("unchecked")
    public CssProperty getCssProperty(final String cssName) {
        // given priority for optimization rather than coding standard.

        @SuppressWarnings("rawtypes")
        final Class classClass = CSSPROPERTY_CLASSES.get(cssName);

        String value = null;
        // given priority for optimization rather than coding standard
        if (classClass != null && classClass.isEnum()
                && (value = getAttributeValueMap().get(cssName)) != null) {

            value = value.toUpperCase().replace(IMPORTANT_UPPERCASE, "")
                    .replace("-", "_").trim();

            final CssProperty cssProperty = (CssProperty) Enum
                    .valueOf(classClass, value);
            setFromAddCssProperty(true);
            cssProperties.add(cssProperty);
            setFromAddCssProperty(false);
            return cssProperty;
            // given priority for optimization rather than coding standard.
        } else if (classClass != null
                && (value = getAttributeValueMap().get(cssName)) != null) {

            value = value.toLowerCase().replace(IMPORTANT, "");

            try {
                AbstractCssProperty<?> abstractCssProperty = null;
                // given priority for optimization rather than coding standard.
                if ((abstractCssProperty = abstractCssPropertyClassObjects
                        .get(cssName)) != null) {
                    abstractCssProperty = abstractCssPropertyClassObjects
                            .get(cssName);
                } else {
                    abstractCssProperty = (AbstractCssProperty<?>) classClass
                            .newInstance();
                    abstractCssProperty.setStateChangeInformer(this);
                    abstractCssPropertyClassObjects.put(cssName,
                            abstractCssProperty);
                }
                abstractCssProperty.setCssValue(value);
                setFromAddCssProperty(true);
                cssProperties.add(abstractCssProperty);
                setFromAddCssProperty(false);
                return abstractCssProperty;
            } catch (InstantiationException | IllegalAccessException e) {
                LOGGER.severe(String.valueOf(e));
            }
        } else if ((value = getAttributeValueMap().get(cssName)) != null) {
            value = value.toLowerCase().replace(IMPORTANT, "");
            final CustomCssProperty customCssProperty = new CustomCssProperty(
                    cssName, value);
            customCssProperty.setAlreadyInUse(true);
            customCssProperty.setStateChangeInformer(this);
            abstractCssPropertyClassObjects.put(cssName, customCssProperty);
            setFromAddCssProperty(true);
            cssProperties.add(customCssProperty);
            setFromAddCssProperty(false);
            return customCssProperty;
        }

        return null;
    }

    // TODO review this code later
    private final Set<CssProperty> cssProperties = new HashSet<CssProperty>() {

        private static final long serialVersionUID = 1_0_0L;

        @Override
        public boolean add(final CssProperty cssProperty) {
            synchronized (this) {
                if (!isFromAddCssProperty()) {
                    addCssProperty(cssProperty);
                }
                return super.add(cssProperty);
            }
        }

        @Override
        public boolean addAll(
                final Collection<? extends CssProperty> cssProperties) {
            synchronized (this) {
                if (!isFromAddCssProperty()) {
                    for (final CssProperty cssProperty : cssProperties) {
                        addCssProperty(cssProperty);
                    }
                }
                return super.addAll(cssProperties);
            }
        }

        @Override
        public void clear() {
            synchronized (this) {
                if (!isClearOnlyInCssProperties()) {
                    for (final CssProperty cssProperty : this) {
                        removeCssProperty(cssProperty);
                    }
                }
                super.clear();
            }
        }

        @Override
        public boolean remove(final Object cssProperty) {
            synchronized (this) {
                if (!isFromRemoveCssProperty()) {
                    try {
                        removeCssProperty((CssProperty) cssProperty);
                    } catch (final Exception e) {
                        throw new IllegalArgumentException(
                                "the object to remove should be an instance of CssProperty");
                    }
                }
                return super.remove(cssProperty);
            }
        }

        @Override
        public boolean removeAll(final Collection<?> cssProperties) {
            synchronized (this) {
                if (!isFromRemoveCssProperty()) {
                    try {
                        for (final Object cssProperty : cssProperties) {
                            removeCssProperty((CssProperty) cssProperty);
                        }
                    } catch (final RuntimeException e) {
                        throw new IllegalArgumentException(
                                "the given collection should be Collection<? extends CssProperty>");
                    }
                }
                return super.removeAll(cssProperties);
            }
        }
    };

    /**
     * gets the {@code Collection<CssProperty>}.
     *
     * @return {@code Collection<CssProperty>}
     * @since 1.0.0
     * @author WFF
     */
    public Collection<CssProperty> getCssProperties() {
        return cssProperties;
    }

    @Override
    public void stateChanged(final CssProperty stateChangedObject) {
        addToAttributeValueMap(stateChangedObject.getCssName(),
                stateChangedObject.getCssValue());
    }

    @Override
    protected void beforePrintStructure() {
        // TODO update the collection values if it has any value change.
    }

    /**
     * for internal use.
     *
     * @return the fromRemoveCssProperty
     * @author WFF
     * @since 1.0.0
     */
    boolean isFromRemoveCssProperty() {
        return fromRemoveCssProperty;
    }

    /**
     * for internal use.
     *
     * @param fromRemoveCssProperty
     *            the fromRemoveCssProperty to set
     * @author WFF
     * @since 1.0.0
     */
    void setFromRemoveCssProperty(final boolean fromRemoveCssProperty) {
        this.fromRemoveCssProperty = fromRemoveCssProperty;
    }

    /**
     * for internal use.
     *
     * @return the fromAddCssProperty
     * @author WFF
     * @since 1.0.0
     */
    boolean isFromAddCssProperty() {
        return fromAddCssProperty;
    }

    /**
     * for internal use.
     *
     * @param fromAddCssProperty
     *            the fromAddCssProperty to set
     * @author WFF
     * @since 1.0.0
     */
    void setFromAddCssProperty(final boolean fromAddCssProperty) {
        this.fromAddCssProperty = fromAddCssProperty;
    }

    /**
     * for internal use.
     *
     * @return the clearOnlyInCssProperties
     * @author WFF
     * @since 1.0.0
     */
    boolean isClearOnlyInCssProperties() {
        return clearOnlyInCssProperties;
    }

    /**
     * for internal use.
     *
     * @param clearOnlyInCssProperties
     *            the clearOnlyInCssProperties to set
     * @author WFF
     * @since 1.0.0
     */
    void setClearOnlyInCssProperties(final boolean clearOnlyInCssProperties) {
        this.clearOnlyInCssProperties = clearOnlyInCssProperties;
    }

    /**
     * @return the csspropertyClasses
     * @author WFF
     * @since 1.0.0
     */
    public static Map<String, Class<?>> getSupportedCsspropertyClasses() {
        return CSSPROPERTY_CLASSES;
    }
}
