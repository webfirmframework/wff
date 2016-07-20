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
import java.util.Set;

import com.webfirmframework.wffweb.css.Color;
import com.webfirmframework.wffweb.tag.html.attribute.Accept;
import com.webfirmframework.wffweb.tag.html.attribute.Align;
import com.webfirmframework.wffweb.tag.html.attribute.Alt;
import com.webfirmframework.wffweb.tag.html.attribute.AttributeNameConstants;
import com.webfirmframework.wffweb.tag.html.attribute.Charset;
import com.webfirmframework.wffweb.tag.html.attribute.Checked;
import com.webfirmframework.wffweb.tag.html.attribute.CoOrds;
import com.webfirmframework.wffweb.tag.html.attribute.Disabled;
import com.webfirmframework.wffweb.tag.html.attribute.Face;
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
import com.webfirmframework.wffweb.tag.html.attribute.Shape;
import com.webfirmframework.wffweb.tag.html.attribute.Size;
import com.webfirmframework.wffweb.tag.html.attribute.Target;
import com.webfirmframework.wffweb.tag.html.attribute.Type;
import com.webfirmframework.wffweb.tag.html.attribute.Value;
import com.webfirmframework.wffweb.tag.html.attribute.Width;
import com.webfirmframework.wffweb.tag.html.attribute.global.AccessKey;
import com.webfirmframework.wffweb.tag.html.attribute.global.ClassAttribute;
import com.webfirmframework.wffweb.tag.html.attribute.global.Dir;
import com.webfirmframework.wffweb.tag.html.attribute.global.Id;
import com.webfirmframework.wffweb.tag.html.attribute.global.Lang;
import com.webfirmframework.wffweb.tag.html.attribute.global.Style;
import com.webfirmframework.wffweb.tag.html.attribute.global.TabIndex;
import com.webfirmframework.wffweb.tag.html.attribute.global.Title;
import com.webfirmframework.wffweb.tag.html.formsandinputs.Form;
import com.webfirmframework.wffweb.tag.html.html5.Data;
import com.webfirmframework.wffweb.tag.html.html5.attribute.AutoComplete;
import com.webfirmframework.wffweb.tag.html.html5.attribute.AutoFocus;
import com.webfirmframework.wffweb.tag.html.html5.attribute.AutoPlay;
import com.webfirmframework.wffweb.tag.html.html5.attribute.Content;
import com.webfirmframework.wffweb.tag.html.html5.attribute.Controls;
import com.webfirmframework.wffweb.tag.html.html5.attribute.Download;
import com.webfirmframework.wffweb.tag.html.html5.attribute.FormAction;
import com.webfirmframework.wffweb.tag.html.html5.attribute.FormEncType;
import com.webfirmframework.wffweb.tag.html.html5.attribute.FormMethod;
import com.webfirmframework.wffweb.tag.html.html5.attribute.FormNoValidate;
import com.webfirmframework.wffweb.tag.html.html5.attribute.Loop;
import com.webfirmframework.wffweb.tag.html.html5.attribute.Max;
import com.webfirmframework.wffweb.tag.html.html5.attribute.Media;
import com.webfirmframework.wffweb.tag.html.html5.attribute.Min;
import com.webfirmframework.wffweb.tag.html.html5.attribute.Multiple;
import com.webfirmframework.wffweb.tag.html.html5.attribute.Muted;
import com.webfirmframework.wffweb.tag.html.html5.attribute.Pattern;
import com.webfirmframework.wffweb.tag.html.html5.attribute.Placeholder;
import com.webfirmframework.wffweb.tag.html.html5.attribute.Preload;
import com.webfirmframework.wffweb.tag.html.html5.attribute.Required;
import com.webfirmframework.wffweb.tag.html.html5.attribute.Step;
import com.webfirmframework.wffweb.tag.html.html5.attribute.global.ContentEditable;
import com.webfirmframework.wffweb.tag.html.html5.attribute.global.ContextMenu;
import com.webfirmframework.wffweb.tag.html.html5.attribute.global.Draggable;
import com.webfirmframework.wffweb.tag.html.html5.attribute.global.Dropzone;
import com.webfirmframework.wffweb.tag.html.html5.attribute.global.Hidden;
import com.webfirmframework.wffweb.tag.html.html5.attribute.global.SpellCheck;
import com.webfirmframework.wffweb.tag.html.html5.attribute.global.Translate;

public class AttributeRegistry {

    private static List<String> attributeNames;

    private static final Set<String> attributeNamesSet;

    private static final Map<String, String> ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME;

    static {

        final Field[] fields = AttributeNameConstants.class.getFields();
        final int initialCapacity = fields.length;

        ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME = new HashMap<String, String>(
                initialCapacity);

        ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME.put(AttributeNameConstants.ACCEPT,
                Accept.class.getSimpleName());
        ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME.put(AttributeNameConstants.ACCESSKEY,
                AccessKey.class.getSimpleName());
        ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME.put(AttributeNameConstants.ALIGN,
                Align.class.getSimpleName());
        ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME.put(AttributeNameConstants.ALT,
                Alt.class.getSimpleName());
        ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME.put(
                AttributeNameConstants.AUTOCOMPLETE,
                AutoComplete.class.getSimpleName());
        ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME.put(AttributeNameConstants.AUTOFOCUS,
                AutoFocus.class.getSimpleName());
        ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME.put(AttributeNameConstants.AUTOPLAY,
                AutoPlay.class.getSimpleName());
        ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME.put(AttributeNameConstants.CHARSET,
                Charset.class.getSimpleName());
        ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME.put(AttributeNameConstants.CHECKED,
                Checked.class.getSimpleName());
        ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME.put(AttributeNameConstants.CLASS,
                ClassAttribute.class.getSimpleName());
        ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME.put(AttributeNameConstants.COLOR,
                Color.class.getSimpleName());
        ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME.put(AttributeNameConstants.CONTENT,
                Content.class.getSimpleName());
        ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME.put(
                AttributeNameConstants.CONTENTEDITABLE,
                ContentEditable.class.getSimpleName());
        ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME.put(
                AttributeNameConstants.CONTEXTMENU,
                ContextMenu.class.getSimpleName());
        ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME.put(AttributeNameConstants.CONTROLS,
                Controls.class.getSimpleName());
        ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME.put(AttributeNameConstants.COORDS,
                CoOrds.class.getSimpleName());
        ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME.put(AttributeNameConstants.DATA,
                Data.class.getSimpleName());
        ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME.put(AttributeNameConstants.DIR,
                Dir.class.getSimpleName());
        ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME.put(AttributeNameConstants.DISABLED,
                Disabled.class.getSimpleName());
        ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME.put(AttributeNameConstants.DOWNLOAD,
                Download.class.getSimpleName());
        ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME.put(AttributeNameConstants.DRAGGABLE,
                Draggable.class.getSimpleName());
        ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME.put(AttributeNameConstants.DROPZONE,
                Dropzone.class.getSimpleName());
        ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME.put(AttributeNameConstants.FACE,
                Face.class.getSimpleName());
        ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME.put(AttributeNameConstants.FORM,
                Form.class.getSimpleName());
        ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME.put(AttributeNameConstants.FORMACTION,
                FormAction.class.getSimpleName());
        ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME.put(
                AttributeNameConstants.FORMENCTYPE,
                FormEncType.class.getSimpleName());
        ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME.put(AttributeNameConstants.FORMMETHOD,
                FormMethod.class.getSimpleName());
        ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME.put(
                AttributeNameConstants.FORMNOVALIDATE,
                FormNoValidate.class.getSimpleName());
        ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME.put(AttributeNameConstants.FORMTARGET,
                Target.class.getSimpleName());
        ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME.put(AttributeNameConstants.HEIGHT,
                Height.class.getSimpleName());
        ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME.put(AttributeNameConstants.HIDDEN,
                Hidden.class.getSimpleName());
        ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME.put(AttributeNameConstants.HREF,
                Href.class.getSimpleName());
        ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME.put(AttributeNameConstants.HREFLANG,
                HrefLang.class.getSimpleName());
        ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME.put(AttributeNameConstants.HTTP_EQUIV,
                HttpEquiv.class.getSimpleName());
        ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME.put(AttributeNameConstants.ID,
                Id.class.getSimpleName());
        ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME.put(AttributeNameConstants.LANG,
                Lang.class.getSimpleName());
        ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME.put(AttributeNameConstants.LIST,
                List.class.getSimpleName());
        ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME.put(AttributeNameConstants.LOOP,
                Loop.class.getSimpleName());
        ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME.put(AttributeNameConstants.MAX,
                Max.class.getSimpleName());
        ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME.put(AttributeNameConstants.MAXLENGTH,
                MaxLength.class.getSimpleName());
        ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME.put(AttributeNameConstants.MEDIA,
                Media.class.getSimpleName());
        ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME.put(AttributeNameConstants.METHOD,
                Method.class.getSimpleName());
        ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME.put(AttributeNameConstants.MIN,
                Min.class.getSimpleName());
        ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME.put(AttributeNameConstants.MINLENGTH,
                MinLength.class.getSimpleName());
        ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME.put(AttributeNameConstants.MULTIPLE,
                Multiple.class.getSimpleName());
        ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME.put(AttributeNameConstants.MUTED,
                Muted.class.getSimpleName());
        ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME.put(AttributeNameConstants.NAME,
                Name.class.getSimpleName());
        ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME.put(AttributeNameConstants.NOHREF,
                NoHref.class.getSimpleName());
        ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME.put(AttributeNameConstants.PATTERN,
                Pattern.class.getSimpleName());
        ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME.put(
                AttributeNameConstants.PLACEHOLDER,
                Placeholder.class.getSimpleName());
        ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME.put(AttributeNameConstants.PRELOAD,
                Preload.class.getSimpleName());
        ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME.put(AttributeNameConstants.READONLY,
                ReadOnly.class.getSimpleName());
        ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME.put(AttributeNameConstants.REL,
                Rel.class.getSimpleName());
        ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME.put(AttributeNameConstants.REQUIRED,
                Required.class.getSimpleName());
        ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME.put(AttributeNameConstants.REV,
                Rev.class.getSimpleName());
        ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME.put(AttributeNameConstants.SHAPE,
                Shape.class.getSimpleName());
        ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME.put(AttributeNameConstants.SIZE,
                Size.class.getSimpleName());
        ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME.put(AttributeNameConstants.SPELLCHECK,
                SpellCheck.class.getSimpleName());
        ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME.put(AttributeNameConstants.SRC,
                com.webfirmframework.wffweb.tag.html.attribute.Src.class
                        .getSimpleName());
        ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME.put(AttributeNameConstants.STEP,
                Step.class.getSimpleName());
        ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME.put(AttributeNameConstants.STYLE,
                Style.class.getSimpleName());
        ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME.put(AttributeNameConstants.TABINDEX,
                TabIndex.class.getSimpleName());
        ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME.put(AttributeNameConstants.TARGET,
                Target.class.getSimpleName());
        ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME.put(AttributeNameConstants.TITLE,
                Title.class.getSimpleName());
        ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME.put(AttributeNameConstants.TRANSLATE,
                Translate.class.getSimpleName());
        ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME.put(AttributeNameConstants.TYPE,
                Type.class.getSimpleName());
        ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME.put(AttributeNameConstants.VALUE,
                Value.class.getSimpleName());
        ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME.put(AttributeNameConstants.WIDTH,
                Width.class.getSimpleName());

        attributeNames = new ArrayList<String>(initialCapacity);
        attributeNamesSet = new HashSet<String>(initialCapacity);

        attributeNamesSet.addAll(ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME.keySet());
        attributeNames.addAll(attributeNamesSet);

        for (final Field field : fields) {
            try {
                final String tagName = field.get(null).toString();
                attributeNamesSet.add(tagName);
            } catch (final Exception e) {
                e.printStackTrace();
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

        final HashSet<String> tagNamesWithoutDuplicates = new HashSet<String>(
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
     * @return
     * a map containing attribute name as key and value as tag class name without package name
     * @since 1.0.0
     * @author WFF
     */
    public static Map<String, String> getAttributeClassNameByAttributeName() {
        return ATTRIBUTE_CLASS_NAME_BY_ATTR_NAME;
    }

}
