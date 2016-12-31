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
 * @author WFF
 */
package com.webfirmframework.wffweb.css;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.webfirmframework.wffweb.css.core.CssEnumUtil;
import com.webfirmframework.wffweb.util.TagStringUtil;

/**
 *
 * @author WFF
 * @since 1.0.0
 */
public enum CssColorName {

    ALICE_BLUE("aliceblue"),

    ANTIQUE_WHITE("antiquewhite"),

    AQUA("Aqua"),

    AQUAMARINE("aquamarine"),

    AZURE("azure"),

    BEIGE("beige"),

    BISQUE("bisque"),

    BLACK("black"),

    BLANCHED_ALMOND("blanchedalmond"),

    BLUE("blue"),

    BLUE_VIOLET("blueviolet"),

    BROWN("brown"),

    BURLY_WOOD("burlywood"),

    CADET_BLUE("cadetblue"),

    CHARTREUSE("chartreuse"),

    CHOCOLATE("chocolate"),

    CORAL("coral"),

    CORNFLOWER_BLUE("cornflowerblue"),

    CORNSILK("cornsilk"),

    CRIMSON("crimson"),

    CYAN("cyan"),

    DARK_BLUE("darkblue"),

    DARK_CYAN("darkcyan"),

    DARK_GOLDEN_ROD("darkgoldenrod"),

    DARK_GRAY("darkgray"),

    DARK_GREEN("darkgreen"),

    DARK_KHAKI("darkkhaki"),

    DARK_MAGENTA("darkmagenta"),

    DARK_OLIVE_GREEN("darkolivegreen"),

    DARK_ORANGE("darkorange"),

    DARK_ORCHID("darkorchid"),

    DARK_RED("darkred"),

    DARK_SALMON("darksalmon"),

    DARK_SEA_GREEN("darkseagreen"),

    DARK_SLATE_BLUE("darkslateblue"),

    DARK_SLATE_GRAY("darkslategray"),

    DARK_TURQUOISE("darkturquoise"),

    DARK_VIOLET("darkviolet"),

    DEEP_PINK("deeppink"),

    DEEP_SKY_BLUE("deepskyblue"),

    DIM_GRAY("dimgray"),

    DODGER_BLUE("dodgerblue"),

    FIRE_BRICK("firebrick"),

    FLORAL_WHITE("floralwhite"),

    FOREST_GREEN("forestgreen"),

    FUCHSIA("fuchsia"),

    GAINSBORO("gainsboro"),

    GHOST_WHITE("ghostwhite"),

    GOLD("gold"),

    GOLDEN_ROD("goldenrod"),

    GRAY("gray"),

    GREEN("green"),

    GREEN_YELLOW("greenyellow"),

    HONEY_DEW("honeydew"),

    HOT_PINK("hotpink"),

    INDIAN_RED("indianred"),

    INDIGO("indigo"),

    IVORY("ivory"),

    KHAKI("khaki"),

    LAVENDER("lavender"),

    LAVENDER_BLUSH("lavenderblush"),

    LAWN_GREEN("lawngreen"),

    LEMON_CHIFFON("lemonchiffon"),

    LIGHT_BLUE("lightblue"),

    LIGHT_CORAL("lightcoral"),

    LIGHT_CYAN("lightcyan"),

    LIGHT_GOLDEN_ROD_YELLOW("lightgoldenrodyellow"),

    LIGHT_GRAY("lightgray"),

    LIGHT_GREEN("lightgreen"),

    LIGHT_PINK("lightpink"),

    LIGHT_SALMON("lightsalmon"),

    LIGHT_SEA_GREEN("lightseagreen"),

    LIGHT_SKY_BLUE("lightskyblue"),

    LIGHT_SLATE_GRAY("lightslategray"),

    LIGHT_STEEL_BLUE("lightsteelblue"),

    LIGHT_YELLOW("lightyellow"),

    LIME("lime"),

    LIME_GREEN("limegreen"),

    LINEN("linen"),

    MAGENTA("magenta"),

    MAROON("maroon"),

    MEDIUM_AQUA_MARINE("mediumaquamarine"),

    MEDIUM_BLUE("mediumblue"),

    MEDIUM_ORCHID("mediumorchid"),

    MEDIUM_PURPLE("mediumpurple"),

    MEDIUM_SEA_GREEN("mediumseagreen"),

    MEDIUM_SLATE_BLUE("mediumslateblue"),

    MEDIUM_SPRING_GREEN("mediumspringgreen"),

    MEDIUM_TURQUOISE("mediumturquoise"),

    MEDIUM_VIOLET_RED("mediumvioletred"),

    MIDNIGHT_BLUE("midnightblue"),

    MINT_CREAM("mintcream"),

    MISTY_ROSE("mistyrose"),

    MOCCASIN("moccasin"),

    NAVY("navy"),

    OLD_LACE("oldlace"),

    OLIVE("olive"),

    OLIVE_DRAB("olivedrab"),

    ORANGE("orange"),

    ORANGE_RED("orangered"),

    ORCHID("orchid"),

    PALE_GREEN("palegreen"),

    PALE_GOLDEN_ROD("palegoldenrod"),

    PALE_TURQUOISE("paleturquoise"),

    PALE_VIOLET_RED("palevioletred"),

    REBECCA_PURPLE("rebeccapurple"),

    NAVAJO_WHITE("navajowhite"),

    SPRING_GREEN("springgreen"),

    YELLOW_GREEN("yellowgreen"),

    PAPAYA_WHIP("papayawhip"),

    PEACH_PUFF("peachpuff"),

    PERU("peru"),

    PINK("pink"),

    PLUM("plum"),

    POWDER_BLUE("powderblue"),

    PURPLE("purple"),

    RED("red"),

    ROSY_BROWN("rosybrown"),

    ROYAL_BLUE("royalblue"),

    SADDLE_BROWN("saddlebrown"),

    SALMON("salmon"),

    SANDY_BROWN("sandybrown"),

    SEA_GREEN("seagreen"),

    SEA_SHELL("seashell"),

    SIENNA("sienna"),

    SILVER("silver"),

    SKY_BLUE("skyblue"),

    SLATE_BLUE("slateblue"),

    SLATE_GRAY("slategray"),

    SNOW("snow"),

    STEEL_BLUE("steelblue"),

    TAN("tan"),

    TEAL("teal"),

    THISTLE("thistle"),

    TOMATO("tomato"),

    TURQUOISE("turquoise"),

    VIOLET("violet"),

    WHEAT("wheat"),

    WHITE("white"),

    WHITE_SMOKE("whitesmoke"),

    YELLOW("yellow");

    private static final Collection<String> UPPER_CASE_SUPER_TO_STRINGS;

    private static final Map<String, CssColorName> ALL_OBJECTS;

    // the lowest length value
    private static final int LOWEST_LENGTH;
    // the highest length value
    private static final int HIGHEST_LENGTH;

    static {
        ALL_OBJECTS = new HashMap<String, CssColorName>();
        Collection<String> upperCaseSuperToStringsTemp = new ArrayList<String>();
        int min = values()[0].colorName.length();
        int max = 0;
        for (int i = 0; i < values().length; i++) {
            final int length = values()[i].colorName.length();
            if (max < length) {
                max = length;
            }
            if (min > length) {
                min = length;
            }
            final String upperCaseCssValue = TagStringUtil
                    .toUpperCase(values()[i].colorName);
            upperCaseSuperToStringsTemp.add(upperCaseCssValue);
            ALL_OBJECTS.put(upperCaseCssValue, values()[i]);
        }
        LOWEST_LENGTH = min;
        HIGHEST_LENGTH = max;
        if (values().length > 10) {
            upperCaseSuperToStringsTemp = new HashSet<String>(
                    upperCaseSuperToStringsTemp);
        }
        UPPER_CASE_SUPER_TO_STRINGS = upperCaseSuperToStringsTemp;
    }

    private final String colorName;

    private CssColorName(final String colorName) {
        this.colorName = colorName;

    }

    /**
     * @return the colorName
     * @since 1.0.0
     * @author WFF
     */
    public String getColorName() {
        return colorName;
    }

    /**
     * @return the name of this enum.
     * @since 1.0.0
     * @author WFF
     */
    public String getEnumName() {
        return super.toString();
    }

    @Override
    public String toString() {
        return colorName;
    }

    /**
     * checks whether the given given {@code colorName} is valid , i.e. whether
     * it can have a corresponding object from it.
     *
     * @param colorName
     * @return true if the given {@code cssColorName} has a corresponding
     *         object.
     * @since 1.0.0
     * @author WFF
     */
    public static boolean isValid(final String colorName) {
        return CssEnumUtil.contains(colorName, UPPER_CASE_SUPER_TO_STRINGS,
                LOWEST_LENGTH, HIGHEST_LENGTH);
    }

    /**
     * gets the corresponding object for the given {@code colorName} or null for
     * invalid colorName.
     *
     * @param colorName
     *            the inbuilt color name as per w3 standard.
     * @return the corresponding object for the given {@code colorName} or null
     *         for invalid colorName.
     * @since 1.0.0
     * @author WFF
     */
    public static CssColorName getThis(final String colorName) {
        final String enumString = TagStringUtil.toUpperCase(colorName);
        return ALL_OBJECTS.get(enumString);
    }

}
