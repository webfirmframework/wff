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
package com.webfirmframework.wffweb.tag.html.attribute.core;

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

import com.webfirmframework.wffweb.css.Border;
import com.webfirmframework.wffweb.css.Color;
import com.webfirmframework.wffweb.tag.html.attribute.Accept;
import com.webfirmframework.wffweb.tag.html.attribute.AcceptCharset;
import com.webfirmframework.wffweb.tag.html.attribute.Action;
import com.webfirmframework.wffweb.tag.html.attribute.Align;
import com.webfirmframework.wffweb.tag.html.attribute.Alt;
import com.webfirmframework.wffweb.tag.html.attribute.Async;
import com.webfirmframework.wffweb.tag.html.attribute.AttributeNameConstants;
import com.webfirmframework.wffweb.tag.html.attribute.CellPadding;
import com.webfirmframework.wffweb.tag.html.attribute.CellSpacing;
import com.webfirmframework.wffweb.tag.html.attribute.Charset;
import com.webfirmframework.wffweb.tag.html.attribute.Checked;
import com.webfirmframework.wffweb.tag.html.attribute.CoOrds;
import com.webfirmframework.wffweb.tag.html.attribute.ColSpan;
import com.webfirmframework.wffweb.tag.html.attribute.Cols;
import com.webfirmframework.wffweb.tag.html.attribute.Defer;
import com.webfirmframework.wffweb.tag.html.attribute.DirName;
import com.webfirmframework.wffweb.tag.html.attribute.Disabled;
import com.webfirmframework.wffweb.tag.html.attribute.EncType;
import com.webfirmframework.wffweb.tag.html.attribute.Face;
import com.webfirmframework.wffweb.tag.html.attribute.For;
import com.webfirmframework.wffweb.tag.html.attribute.Headers;
import com.webfirmframework.wffweb.tag.html.attribute.Height;
import com.webfirmframework.wffweb.tag.html.attribute.Href;
import com.webfirmframework.wffweb.tag.html.attribute.HrefLang;
import com.webfirmframework.wffweb.tag.html.attribute.HttpEquiv;
import com.webfirmframework.wffweb.tag.html.attribute.MaxLength;
import com.webfirmframework.wffweb.tag.html.attribute.Method;
import com.webfirmframework.wffweb.tag.html.attribute.MinLength;
import com.webfirmframework.wffweb.tag.html.attribute.Name;
import com.webfirmframework.wffweb.tag.html.attribute.NoHref;
import com.webfirmframework.wffweb.tag.html.attribute.ReadOnly;
import com.webfirmframework.wffweb.tag.html.attribute.Rel;
import com.webfirmframework.wffweb.tag.html.attribute.Rev;
import com.webfirmframework.wffweb.tag.html.attribute.Role;
import com.webfirmframework.wffweb.tag.html.attribute.RowSpan;
import com.webfirmframework.wffweb.tag.html.attribute.Rows;
import com.webfirmframework.wffweb.tag.html.attribute.Scope;
import com.webfirmframework.wffweb.tag.html.attribute.Selected;
import com.webfirmframework.wffweb.tag.html.attribute.Shape;
import com.webfirmframework.wffweb.tag.html.attribute.Size;
import com.webfirmframework.wffweb.tag.html.attribute.Sorted;
import com.webfirmframework.wffweb.tag.html.attribute.Target;
import com.webfirmframework.wffweb.tag.html.attribute.Type;
import com.webfirmframework.wffweb.tag.html.attribute.Value;
import com.webfirmframework.wffweb.tag.html.attribute.Width;
import com.webfirmframework.wffweb.tag.html.attribute.event.animation.AnimationEnd;
import com.webfirmframework.wffweb.tag.html.attribute.event.animation.AnimationIteration;
import com.webfirmframework.wffweb.tag.html.attribute.event.animation.AnimationStart;
import com.webfirmframework.wffweb.tag.html.attribute.event.clipboard.OnCopy;
import com.webfirmframework.wffweb.tag.html.attribute.event.clipboard.OnCut;
import com.webfirmframework.wffweb.tag.html.attribute.event.clipboard.OnPaste;
import com.webfirmframework.wffweb.tag.html.attribute.event.drag.OnDrag;
import com.webfirmframework.wffweb.tag.html.attribute.event.drag.OnDragEnd;
import com.webfirmframework.wffweb.tag.html.attribute.event.drag.OnDragEnter;
import com.webfirmframework.wffweb.tag.html.attribute.event.drag.OnDragLeave;
import com.webfirmframework.wffweb.tag.html.attribute.event.drag.OnDragOver;
import com.webfirmframework.wffweb.tag.html.attribute.event.drag.OnDragStart;
import com.webfirmframework.wffweb.tag.html.attribute.event.drag.OnDrop;
import com.webfirmframework.wffweb.tag.html.attribute.event.form.OnBlur;
import com.webfirmframework.wffweb.tag.html.attribute.event.form.OnChange;
import com.webfirmframework.wffweb.tag.html.attribute.event.form.OnFocus;
import com.webfirmframework.wffweb.tag.html.attribute.event.form.OnFocusIn;
import com.webfirmframework.wffweb.tag.html.attribute.event.form.OnFocusOut;
import com.webfirmframework.wffweb.tag.html.attribute.event.form.OnInput;
import com.webfirmframework.wffweb.tag.html.attribute.event.form.OnInvalid;
import com.webfirmframework.wffweb.tag.html.attribute.event.form.OnReset;
import com.webfirmframework.wffweb.tag.html.attribute.event.form.OnSearch;
import com.webfirmframework.wffweb.tag.html.attribute.event.form.OnSelect;
import com.webfirmframework.wffweb.tag.html.attribute.event.form.OnSubmit;
import com.webfirmframework.wffweb.tag.html.attribute.event.frame.or.object.OnAbort;
import com.webfirmframework.wffweb.tag.html.attribute.event.frame.or.object.OnBeforeUnload;
import com.webfirmframework.wffweb.tag.html.attribute.event.frame.or.object.OnError;
import com.webfirmframework.wffweb.tag.html.attribute.event.frame.or.object.OnHashChange;
import com.webfirmframework.wffweb.tag.html.attribute.event.frame.or.object.OnLoad;
import com.webfirmframework.wffweb.tag.html.attribute.event.frame.or.object.OnPageHide;
import com.webfirmframework.wffweb.tag.html.attribute.event.frame.or.object.OnPageShow;
import com.webfirmframework.wffweb.tag.html.attribute.event.frame.or.object.OnResize;
import com.webfirmframework.wffweb.tag.html.attribute.event.frame.or.object.OnScroll;
import com.webfirmframework.wffweb.tag.html.attribute.event.frame.or.object.OnUnload;
import com.webfirmframework.wffweb.tag.html.attribute.event.keyboard.OnKeyDown;
import com.webfirmframework.wffweb.tag.html.attribute.event.keyboard.OnKeyPress;
import com.webfirmframework.wffweb.tag.html.attribute.event.keyboard.OnKeyUp;
import com.webfirmframework.wffweb.tag.html.attribute.event.media.OnCanPlay;
import com.webfirmframework.wffweb.tag.html.attribute.event.media.OnCanPlayThrough;
import com.webfirmframework.wffweb.tag.html.attribute.event.media.OnDurationChange;
import com.webfirmframework.wffweb.tag.html.attribute.event.media.OnEmptied;
import com.webfirmframework.wffweb.tag.html.attribute.event.media.OnEnded;
import com.webfirmframework.wffweb.tag.html.attribute.event.media.OnLoadStart;
import com.webfirmframework.wffweb.tag.html.attribute.event.media.OnLoadedData;
import com.webfirmframework.wffweb.tag.html.attribute.event.media.OnLoadedMetaData;
import com.webfirmframework.wffweb.tag.html.attribute.event.media.OnPause;
import com.webfirmframework.wffweb.tag.html.attribute.event.media.OnPlay;
import com.webfirmframework.wffweb.tag.html.attribute.event.media.OnPlaying;
import com.webfirmframework.wffweb.tag.html.attribute.event.media.OnProgress;
import com.webfirmframework.wffweb.tag.html.attribute.event.media.OnRateChange;
import com.webfirmframework.wffweb.tag.html.attribute.event.media.OnSeeked;
import com.webfirmframework.wffweb.tag.html.attribute.event.media.OnSeeking;
import com.webfirmframework.wffweb.tag.html.attribute.event.media.OnStalled;
import com.webfirmframework.wffweb.tag.html.attribute.event.media.OnSuspend;
import com.webfirmframework.wffweb.tag.html.attribute.event.media.OnTimeUpdate;
import com.webfirmframework.wffweb.tag.html.attribute.event.media.OnVolumeChange;
import com.webfirmframework.wffweb.tag.html.attribute.event.media.OnWaiting;
import com.webfirmframework.wffweb.tag.html.attribute.event.misc.OnOffline;
import com.webfirmframework.wffweb.tag.html.attribute.event.misc.OnOnline;
import com.webfirmframework.wffweb.tag.html.attribute.event.misc.OnPopState;
import com.webfirmframework.wffweb.tag.html.attribute.event.misc.OnShow;
import com.webfirmframework.wffweb.tag.html.attribute.event.misc.OnStorage;
import com.webfirmframework.wffweb.tag.html.attribute.event.misc.OnToggle;
import com.webfirmframework.wffweb.tag.html.attribute.event.misc.OnWheel;
import com.webfirmframework.wffweb.tag.html.attribute.event.mouse.OnClick;
import com.webfirmframework.wffweb.tag.html.attribute.event.mouse.OnContextMenu;
import com.webfirmframework.wffweb.tag.html.attribute.event.mouse.OnDblClick;
import com.webfirmframework.wffweb.tag.html.attribute.event.mouse.OnMouseDown;
import com.webfirmframework.wffweb.tag.html.attribute.event.mouse.OnMouseEnter;
import com.webfirmframework.wffweb.tag.html.attribute.event.mouse.OnMouseLeave;
import com.webfirmframework.wffweb.tag.html.attribute.event.mouse.OnMouseMove;
import com.webfirmframework.wffweb.tag.html.attribute.event.mouse.OnMouseOut;
import com.webfirmframework.wffweb.tag.html.attribute.event.mouse.OnMouseOver;
import com.webfirmframework.wffweb.tag.html.attribute.event.mouse.OnMouseUp;
import com.webfirmframework.wffweb.tag.html.attribute.event.print.OnAfterPrint;
import com.webfirmframework.wffweb.tag.html.attribute.event.print.OnBeforePrint;
import com.webfirmframework.wffweb.tag.html.attribute.event.touch.OnTouchCancel;
import com.webfirmframework.wffweb.tag.html.attribute.event.touch.OnTouchEnd;
import com.webfirmframework.wffweb.tag.html.attribute.event.touch.OnTouchMove;
import com.webfirmframework.wffweb.tag.html.attribute.event.touch.OnTouchStart;
import com.webfirmframework.wffweb.tag.html.attribute.event.transition.TransitionEnd;
import com.webfirmframework.wffweb.tag.html.attribute.global.AccessKey;
import com.webfirmframework.wffweb.tag.html.attribute.global.ClassAttribute;
import com.webfirmframework.wffweb.tag.html.attribute.global.Dir;
import com.webfirmframework.wffweb.tag.html.attribute.global.Id;
import com.webfirmframework.wffweb.tag.html.attribute.global.Lang;
import com.webfirmframework.wffweb.tag.html.attribute.global.Style;
import com.webfirmframework.wffweb.tag.html.attribute.global.TabIndex;
import com.webfirmframework.wffweb.tag.html.attribute.global.Title;
import com.webfirmframework.wffweb.tag.html.formsandinputs.Form;
import com.webfirmframework.wffweb.tag.html.html5.attribute.AutoComplete;
import com.webfirmframework.wffweb.tag.html.html5.attribute.AutoFocus;
import com.webfirmframework.wffweb.tag.html.html5.attribute.AutoPlay;
import com.webfirmframework.wffweb.tag.html.html5.attribute.Content;
import com.webfirmframework.wffweb.tag.html.html5.attribute.Controls;
import com.webfirmframework.wffweb.tag.html.html5.attribute.DateTime;
import com.webfirmframework.wffweb.tag.html.html5.attribute.Default;
import com.webfirmframework.wffweb.tag.html.html5.attribute.Download;
import com.webfirmframework.wffweb.tag.html.html5.attribute.FormAction;
import com.webfirmframework.wffweb.tag.html.html5.attribute.FormEncType;
import com.webfirmframework.wffweb.tag.html.html5.attribute.FormMethod;
import com.webfirmframework.wffweb.tag.html.html5.attribute.FormNoValidate;
import com.webfirmframework.wffweb.tag.html.html5.attribute.High;
import com.webfirmframework.wffweb.tag.html.html5.attribute.Loop;
import com.webfirmframework.wffweb.tag.html.html5.attribute.Low;
import com.webfirmframework.wffweb.tag.html.html5.attribute.Max;
import com.webfirmframework.wffweb.tag.html.html5.attribute.Media;
import com.webfirmframework.wffweb.tag.html.html5.attribute.Min;
import com.webfirmframework.wffweb.tag.html.html5.attribute.Multiple;
import com.webfirmframework.wffweb.tag.html.html5.attribute.Muted;
import com.webfirmframework.wffweb.tag.html.html5.attribute.Open;
import com.webfirmframework.wffweb.tag.html.html5.attribute.Optimum;
import com.webfirmframework.wffweb.tag.html.html5.attribute.Pattern;
import com.webfirmframework.wffweb.tag.html.html5.attribute.Placeholder;
import com.webfirmframework.wffweb.tag.html.html5.attribute.Poster;
import com.webfirmframework.wffweb.tag.html.html5.attribute.Preload;
import com.webfirmframework.wffweb.tag.html.html5.attribute.Required;
import com.webfirmframework.wffweb.tag.html.html5.attribute.Reversed;
import com.webfirmframework.wffweb.tag.html.html5.attribute.Sizes;
import com.webfirmframework.wffweb.tag.html.html5.attribute.SrcSet;
import com.webfirmframework.wffweb.tag.html.html5.attribute.Step;
import com.webfirmframework.wffweb.tag.html.html5.attribute.global.ContentEditable;
import com.webfirmframework.wffweb.tag.html.html5.attribute.global.ContextMenu;
import com.webfirmframework.wffweb.tag.html.html5.attribute.global.DataAttribute;
import com.webfirmframework.wffweb.tag.html.html5.attribute.global.Draggable;
import com.webfirmframework.wffweb.tag.html.html5.attribute.global.Dropzone;
import com.webfirmframework.wffweb.tag.html.html5.attribute.global.Hidden;
import com.webfirmframework.wffweb.tag.html.html5.attribute.global.SpellCheck;
import com.webfirmframework.wffweb.tag.html.html5.attribute.global.Translate;

public class AttributeRegistry {

    private static final Logger LOGGER = Logger
            .getLogger(AttributeRegistry.class.getName());

    private static List<String> attributeNames;

    private static final Set<String> attributeNamesSet;

    private static final Map<String, String> ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME;

    private static final Map<String, Class<?>> ATTRIBUTE_CLASS_BY_ATTR_NAME;

    static {

        final Field[] fields = AttributeNameConstants.class.getFields();
        final int initialCapacity = fields.length;

        ATTRIBUTE_CLASS_BY_ATTR_NAME = new HashMap<String, Class<?>>(
                initialCapacity);
        ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME = new HashMap<String, String>(
                initialCapacity);

        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ACCEPT,
                Accept.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ACCESSKEY,
                AccessKey.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ALIGN,
                Align.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ALT, Alt.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.AUTOCOMPLETE,
                AutoComplete.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.AUTOFOCUS,
                AutoFocus.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.AUTOPLAY,
                AutoPlay.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.CHARSET,
                Charset.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.CHECKED,
                Checked.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.CLASS,
                ClassAttribute.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.COLOR,
                Color.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.CONTENT,
                Content.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.CONTENTEDITABLE,
                ContentEditable.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.CONTEXTMENU,
                ContextMenu.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.CONTROLS,
                Controls.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.COORDS,
                CoOrds.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.DATA,
                DataAttribute.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.DIR, Dir.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.DISABLED,
                Disabled.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.DOWNLOAD,
                Download.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.DRAGGABLE,
                Draggable.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.DROPZONE,
                Dropzone.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.FACE,
                Face.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.FORM,
                Form.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.FORMACTION,
                FormAction.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.FORMENCTYPE,
                FormEncType.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.FORMMETHOD,
                FormMethod.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.FORMNOVALIDATE,
                FormNoValidate.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.FORMTARGET,
                Target.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.HEIGHT,
                Height.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.HIDDEN,
                Hidden.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.HREF,
                Href.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.HREFLANG,
                HrefLang.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.HTTP_EQUIV,
                HttpEquiv.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ID, Id.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.LANG,
                Lang.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.LIST,
                List.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.LOOP,
                Loop.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.MAX, Max.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.MAXLENGTH,
                MaxLength.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.MEDIA,
                Media.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.METHOD,
                Method.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.MIN, Min.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.MINLENGTH,
                MinLength.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.MULTIPLE,
                Multiple.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.MUTED,
                Muted.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.NAME,
                Name.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.NOHREF,
                NoHref.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.PATTERN,
                Pattern.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.PLACEHOLDER,
                Placeholder.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.PRELOAD,
                Preload.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.READONLY,
                ReadOnly.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.REL, Rel.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.REQUIRED,
                Required.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.REV, Rev.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.SHAPE,
                Shape.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.SIZE,
                Size.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.SPELLCHECK,
                SpellCheck.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.SRC,
                com.webfirmframework.wffweb.tag.html.attribute.Src.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.STEP,
                Step.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.STYLE,
                Style.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.TABINDEX,
                TabIndex.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.TARGET,
                Target.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.TITLE,
                Title.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.TRANSLATE,
                Translate.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.TYPE,
                Type.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.VALUE,
                Value.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.WIDTH,
                Width.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.COLSPAN,
                ColSpan.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ROWSPAN,
                RowSpan.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.HEADERS,
                Headers.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.SCOPE,
                Scope.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.SORTED,
                Sorted.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ENCTYPE,
                EncType.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ACTION,
                Action.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.BORDER,
                Border.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.CELLPADDING,
                CellPadding.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.CELLSPACING,
                CellSpacing.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONCLICK,
                OnClick.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONCONTEXTMENU,
                OnContextMenu.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONDBLCLICK,
                OnDblClick.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONMOUSEDOWN,
                OnMouseDown.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONMOUSEENTER,
                OnMouseEnter.class);

        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONMOUSELEAVE,
                OnMouseLeave.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONMOUSEMOVE,
                OnMouseMove.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONMOUSEOUT,
                OnMouseOut.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONMOUSEUP,
                OnMouseUp.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONKEYDOWN,
                OnKeyDown.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONKEYPRESS,
                OnKeyPress.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONKEYUP,
                OnKeyUp.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONABORT,
                OnAbort.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONBEFOREUNLOAD,
                OnBeforeUnload.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONERROR,
                OnError.class);

        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONHASHCHANGE,
                OnHashChange.class);

        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONLOAD,
                OnLoad.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONPAGESHOW,
                OnPageShow.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONPAGEHIDE,
                OnPageHide.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONSCROLL,
                OnScroll.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONUNLOAD,
                OnUnload.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONBLUR,
                OnBlur.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONCHANGE,
                OnChange.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONFOCUS,
                OnFocus.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONFOCUSIN,
                OnFocusIn.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONFOCUSOUT,
                OnFocusOut.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONINPUT,
                OnInput.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONINVALID,
                OnInvalid.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONRESET,
                OnReset.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONSEARCH,
                OnSearch.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONSELECT,
                OnSelect.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONSUBMIT,
                OnSubmit.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONDRAG,
                OnDrag.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONDRAGEND,
                OnDragEnd.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONDRAGENTER,
                OnDragEnter.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONDRAGLEAVE,
                OnDragLeave.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONDRAGOVER,
                OnDragOver.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONDRAGSTART,
                OnDragStart.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONDROP,
                OnDrop.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONCOPY,
                OnCopy.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONCUT,
                OnCut.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONPASTE,
                OnPaste.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONAFTERPRINT,
                OnAfterPrint.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONBEFOREPRINT,
                OnBeforePrint.class);

        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONCANPLAY,
                OnCanPlay.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(
                AttributeNameConstants.ONCANPLAYTHROUGH,
                OnCanPlayThrough.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(
                AttributeNameConstants.ONDURATIONCHANGE,
                OnDurationChange.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONEMPTIED,
                OnEmptied.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONENDED,
                OnEnded.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONLOADEDDATA,
                OnLoadedData.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(
                AttributeNameConstants.ONLOADEDMETADATA,
                OnLoadedMetaData.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONLOADSTART,
                OnLoadStart.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONPAUSE,
                OnPause.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONPLAY,
                OnPlay.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONPLAYING,
                OnPlaying.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONPROGRESS,
                OnProgress.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONRATECHANGE,
                OnRateChange.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONSEEKED,
                OnSeeked.class);

        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONSEEKING,
                OnSeeking.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONSTALLED,
                OnStalled.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONSUSPEND,
                OnSuspend.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONTIMEUPDATE,
                OnTimeUpdate.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONVOLUMECHANGE,
                OnVolumeChange.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONWAITING,
                OnWaiting.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ANIMATIONEND,
                AnimationEnd.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(
                AttributeNameConstants.ANIMATIONITERATION,
                AnimationIteration.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ANIMATIONSTART,
                AnimationStart.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.TRANSITIONEND,
                TransitionEnd.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONOFFLINE,
                OnOffline.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONONLINE,
                OnOnline.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONPOPSTATE,
                OnPopState.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONSHOW,
                OnShow.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONSTORAGE,
                OnStorage.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONTOGGLE,
                OnToggle.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONWHEEL,
                OnWheel.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONTOUCHCANCEL,
                OnTouchCancel.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONTOUCHEND,
                OnTouchEnd.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONTOUCHMOVE,
                OnTouchMove.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONTOUCHSTART,
                OnTouchStart.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ROLE,
                Role.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.SRCSET,
                SrcSet.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.SIZES,
                Sizes.class);

        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.COLS,
                Cols.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ROWS,
                Rows.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.FOR, For.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.SELECTED,
                Selected.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ACCEPT_CHARSET,
                AcceptCharset.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ASYNC,
                Async.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.DATETIME,
                DateTime.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONMOUSEOVER,
                OnMouseOver.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.ONRESIZE,
                OnResize.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.OPEN,
                Open.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.OPTIMUM,
                Optimum.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.DEFER,
                Defer.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.DEFAULT,
                Default.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.DIRNAME,
                DirName.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.HIGH,
                High.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.LOW, Low.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.REVERSED,
                Reversed.class);
        ATTRIBUTE_CLASS_BY_ATTR_NAME.put(AttributeNameConstants.POSTER,
                Poster.class);

        for (final Entry<String, Class<?>> entry : ATTRIBUTE_CLASS_BY_ATTR_NAME
                .entrySet()) {
            ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME.put(entry.getKey(),
                    entry.getValue().getSimpleName());
        }

        attributeNames = new ArrayList<>(initialCapacity);
        attributeNamesSet = new HashSet<>(initialCapacity);

        attributeNamesSet.addAll(ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME.keySet());
        attributeNames.addAll(attributeNamesSet);

        for (final Field field : fields) {
            try {
                final String tagName = field.get(null).toString();
                attributeNamesSet.add(tagName);
            } catch (final Exception e) {
                if (LOGGER.isLoggable(Level.SEVERE)) {
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        }

        attributeNames.addAll(attributeNamesSet);
        Collections.sort(attributeNames, new Comparator<String>() {

            @Override
            public int compare(final String o1, final String o2) {

                final Integer length1 = o1.length();
                final Integer length2 = o2.length();

                return length1.compareTo(length2);
            }
        });
    }

    /**
     * @param attrNames
     *            the attribute names to register, eg:-
     *            AttributeRegistry.register("attri-name1", "attri-name2")
     * @since 1.1.3
     * @author WFF
     */
    public static void register(final String... attrNames) {

        final HashSet<String> tagNamesWithoutDuplicates = new HashSet<>(
                Arrays.asList(attrNames));

        attributeNamesSet.addAll(tagNamesWithoutDuplicates);

        attributeNames.clear();
        attributeNames.addAll(attributeNamesSet);

        Collections.sort(attributeNames, new Comparator<String>() {

            @Override
            public int compare(final String o1, final String o2) {

                final Integer length1 = o1.length();
                final Integer length2 = o2.length();

                return length1.compareTo(length2);
            }
        });
    }

    /**
     * @return the list of attribute names sorted in the ascending order of its
     *         length
     * @since 1.1.3
     * @author WFF
     */
    public static List<String> getAttributeNames() {
        return attributeNames;
    }

    /**
     * @return a map containing attribute name as key and value as tag class
     *         name without package name
     * @since 1.0.0
     * @author WFF
     */
    public static Map<String, String> getAttributeClassNameByAttributeName() {
        return ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME;
    }

    /**
     * Loads all attribute classes.
     *
     * @since 2.1.13
     * @author WFF
     */
    public static void loadAllAttributeClasses() {
        for (final Class<?> cls : ATTRIBUTE_CLASS_BY_ATTR_NAME.values()) {
            try {
                Class.forName(cls.getName());
            } catch (final ClassNotFoundException e) {
                if (LOGGER.isLoggable(Level.WARNING)) {
                    LOGGER.warning(
                            "Could not load attribute class " + cls.getName());
                }

            }
        }
    }

}
