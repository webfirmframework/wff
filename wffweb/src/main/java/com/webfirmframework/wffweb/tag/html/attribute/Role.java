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
package com.webfirmframework.wffweb.tag.html.attribute;

import com.webfirmframework.wffweb.tag.html.attribute.core.AbstractAttribute;
import com.webfirmframework.wffweb.tag.html.identifier.GlobalAttributable;

/**
 * The role value can be any {@code Role.*}, for eg {@code Role.BUTTON}. Refer
 * http://w3c.github.io/html/dom.html#global-aria--attributes for more info
 *
 * @author WFF
 * @since 2.0.1
 */
public class Role extends AbstractAttribute implements GlobalAttributable {

    private static final long serialVersionUID = 1_0_0L;

    public static final String ANY = "any";

    public static final String ALERT = "alert";

    public static final String ALERTDIALOG = "alertdialog";

    public static final String APPLICATION = "application";

    public static final String ARTICLE = "article";

    public static final String BANNER = "banner";

    public static final String BUTTON = "button";

    public static final String CHECKBOX = "checkbox";

    public static final String COLUMNHEADER = "columnheader";

    public static final String COMBOBOX = "combobox";

    public static final String COMPLEMENTARY = "complementary";

    public static final String CONTENTINFO = "contentinfo";

    public static final String DEFINITION = "definition";

    public static final String DIALOG = "dialog";

    public static final String DIRECTORY = "directory";

    public static final String DOCUMENT = "document";

    public static final String FORM = "form";

    public static final String GRID = "grid";

    public static final String GRIDCELL = "gridcell";

    public static final String GROUP = "group";

    public static final String HEADING = "heading";

    public static final String IMG = "img";

    public static final String LINK = "link";

    public static final String LIST = "list";

    public static final String LISTBOX = "listbox";

    public static final String LISTITEM = "listitem";

    public static final String LOG = "log";

    public static final String MAIN = "main";

    public static final String MARQUEE = "marquee";

    public static final String MATH = "math";

    public static final String MENU = "menu";

    public static final String MENUBAR = "menubar";

    public static final String MENUITEM = "menuitem";

    public static final String MENUITEMCHECKBOX = "menuitemcheckbox";

    public static final String MENUITEMRADIO = "menuitemradio";

    public static final String NAVIGATION = "navigation";

    public static final String NOTE = "note";

    public static final String OPTION = "option";

    public static final String PRESENTATION = "presentation";

    public static final String PROGRESSBAR = "progressbar";

    public static final String RADIO = "radio";

    public static final String RADIOGROUP = "radiogroup";

    public static final String REGION = "region";

    public static final String ROW = "row";

    public static final String ROWGROUP = "rowgroup";

    public static final String ROWHEADER = "rowheader";

    public static final String SCROLLBAR = "scrollbar";

    public static final String SEARCH = "search";

    public static final String SEPARATOR = "separator";

    public static final String SLIDER = "slider";

    public static final String SPINBUTTON = "spinbutton";

    public static final String STATUS = "status";

    public static final String TAB = "tab";

    public static final String TABLIST = "tablist";

    public static final String TABPANEL = "tabpanel";

    public static final String TEXTBOX = "textbox";

    public static final String TIMER = "timer";

    public static final String TOOLBAR = "toolbar";

    public static final String TOOLTIP = "tooltip";

    public static final String TREE = "tree";

    public static final String TREEGRID = "treegrid";

    public static final String TREEITEM = "treeitem";

    {
        super.setAttributeName(AttributeNameConstants.ROLE);
        init();
    }

    /**
     *
     * @param value
     *            the value for the attribute
     * @since 2.0.1
     * @author WFF
     */
    public Role(final String value) {
        setAttributeValue(value);
    }

    /**
     * sets the value for this attribute
     *
     * @param value
     *            the value for the attribute.
     * @since 2.0.1
     * @author WFF
     */
    public void setValue(final String value) {
        super.setAttributeValue(value);
    }

    /**
     * gets the value of this attribute
     *
     * @return the value of the attribute
     * @since 2.0.1
     * @author WFF
     */
    public String getValue() {
        return super.getAttributeValue();
    }

    /**
     * invokes only once per object
     *
     * @author WFF
     * @since 2.0.1
     */
    protected void init() {
        // to override and use this method
    }

}
