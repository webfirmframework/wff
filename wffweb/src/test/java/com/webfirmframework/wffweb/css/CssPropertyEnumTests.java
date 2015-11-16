/*
 * Copyright 2014-2015 Web Firm Framework
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
package com.webfirmframework.wffweb.css;

import org.junit.Assert;
import org.junit.Test;

import com.webfirmframework.wffweb.css.CssColorName;
import com.webfirmframework.wffweb.css.FontWeight;
import com.webfirmframework.wffweb.css.css3.AlignContent;

/**
 * @author WFF
 * @since 1.0.0
 * @version 1.0.0
 *
 */
public class CssPropertyEnumTests {

    @Test
    public void testAlignContent() {
       Assert.assertNotNull(AlignContent.getThis("stretch"));
       Assert.assertNotNull(AlignContent.getThis("center"));
       Assert.assertNotNull(AlignContent.getThis("flex-start"));
       Assert.assertNotNull(AlignContent.getThis("flex-end"));
       Assert.assertNotNull(AlignContent.getThis("space-between"));
       Assert.assertNotNull(AlignContent.getThis("space-around"));
       Assert.assertNotNull(AlignContent.getThis("initial"));
       Assert.assertNotNull(AlignContent.getThis("inherit"));
    }
    
    @Test
    public void testFontWeight() {
        Assert.assertNotNull(FontWeight.getThis("normal"));
        Assert.assertNotNull(FontWeight.getThis("bold"));
        Assert.assertNotNull(FontWeight.getThis("bolder"));
        Assert.assertNotNull(FontWeight.getThis("lighter"));
        Assert.assertNotNull(FontWeight.getThis("100"));
        Assert.assertNotNull(FontWeight.getThis("200"));
        Assert.assertNotNull(FontWeight.getThis("300"));
        Assert.assertNotNull(FontWeight.getThis("400"));
        Assert.assertNotNull(FontWeight.getThis("500"));
        Assert.assertNotNull(FontWeight.getThis("600"));
        Assert.assertNotNull(FontWeight.getThis("700"));
        Assert.assertNotNull(FontWeight.getThis("800"));
        Assert.assertNotNull(FontWeight.getThis("900"));
        Assert.assertNotNull(FontWeight.getThis("initial"));
        Assert.assertNotNull(FontWeight.getThis("inherit"));
    }
    
    @Test
    public void testCssColorName() {
//        CssColorName[] values = CssColorName.values();
//        for (CssColorName each : values) {
//            System.out.println(each.getColorName());
//        }
        
        Assert.assertNotNull(CssColorName.getThis("aliceblue"));
        Assert.assertNotNull(CssColorName.getThis("antiquewhite"));
        Assert.assertNotNull(CssColorName.getThis("Aqua"));
        Assert.assertNotNull(CssColorName.getThis("aquamarine"));
        Assert.assertNotNull(CssColorName.getThis("azure"));
        Assert.assertNotNull(CssColorName.getThis("beige"));
        Assert.assertNotNull(CssColorName.getThis("bisque"));
        Assert.assertNotNull(CssColorName.getThis("black"));
        Assert.assertNotNull(CssColorName.getThis("blanchedalmond"));
        Assert.assertNotNull(CssColorName.getThis("blue"));
        Assert.assertNotNull(CssColorName.getThis("blueviolet"));
        Assert.assertNotNull(CssColorName.getThis("brown"));
        Assert.assertNotNull(CssColorName.getThis("burlywood"));
        Assert.assertNotNull(CssColorName.getThis("cadetblue"));
        Assert.assertNotNull(CssColorName.getThis("chocolate"));
        Assert.assertNotNull(CssColorName.getThis("coral"));
        Assert.assertNotNull(CssColorName.getThis("cornflowerblue"));
        Assert.assertNotNull(CssColorName.getThis("cornsilk"));
        Assert.assertNotNull(CssColorName.getThis("crimson"));
        Assert.assertNotNull(CssColorName.getThis("cyan"));
        Assert.assertNotNull(CssColorName.getThis("darkblue"));
        Assert.assertNotNull(CssColorName.getThis("darkcyan"));
        Assert.assertNotNull(CssColorName.getThis("darkgoldenrod"));
        Assert.assertNotNull(CssColorName.getThis("darkgray"));
        Assert.assertNotNull(CssColorName.getThis("darkgreen"));
        Assert.assertNotNull(CssColorName.getThis("darkkhaki"));
        Assert.assertNotNull(CssColorName.getThis("darkmagenta"));
        Assert.assertNotNull(CssColorName.getThis("darkolivegreen"));
        Assert.assertNotNull(CssColorName.getThis("darkorange"));
        Assert.assertNotNull(CssColorName.getThis("darkorchid"));
        Assert.assertNotNull(CssColorName.getThis("darkred"));
        Assert.assertNotNull(CssColorName.getThis("darksalmon"));
        Assert.assertNotNull(CssColorName.getThis("darkseagreen"));
        Assert.assertNotNull(CssColorName.getThis("darkslateblue"));
        Assert.assertNotNull(CssColorName.getThis("darkslategray"));
        Assert.assertNotNull(CssColorName.getThis("darkturquoise"));
        Assert.assertNotNull(CssColorName.getThis("darkviolet"));
        Assert.assertNotNull(CssColorName.getThis("deeppink"));
        Assert.assertNotNull(CssColorName.getThis("deepskyblue"));
        Assert.assertNotNull(CssColorName.getThis("dimgray"));
        Assert.assertNotNull(CssColorName.getThis("dodgerblue"));
        Assert.assertNotNull(CssColorName.getThis("firebrick"));
        Assert.assertNotNull(CssColorName.getThis("floralwhite"));
        Assert.assertNotNull(CssColorName.getThis("forestgreen"));
        Assert.assertNotNull(CssColorName.getThis("fuchsia"));
        Assert.assertNotNull(CssColorName.getThis("gainsboro"));
        Assert.assertNotNull(CssColorName.getThis("ghostwhite"));
        Assert.assertNotNull(CssColorName.getThis("gold"));
        Assert.assertNotNull(CssColorName.getThis("goldenrod"));
        Assert.assertNotNull(CssColorName.getThis("gray"));
        Assert.assertNotNull(CssColorName.getThis("green"));
        Assert.assertNotNull(CssColorName.getThis("greenyellow"));
        Assert.assertNotNull(CssColorName.getThis("honeydew"));
        Assert.assertNotNull(CssColorName.getThis("hotpink"));
        Assert.assertNotNull(CssColorName.getThis("indianred"));
        Assert.assertNotNull(CssColorName.getThis("indigo"));
        Assert.assertNotNull(CssColorName.getThis("ivory"));
        Assert.assertNotNull(CssColorName.getThis("khaki"));
        Assert.assertNotNull(CssColorName.getThis("lavender"));
        Assert.assertNotNull(CssColorName.getThis("lavenderblush"));
        Assert.assertNotNull(CssColorName.getThis("lawngreen"));
        Assert.assertNotNull(CssColorName.getThis("lemonchiffon"));
        Assert.assertNotNull(CssColorName.getThis("lightblue"));
        Assert.assertNotNull(CssColorName.getThis("lightcoral"));
        Assert.assertNotNull(CssColorName.getThis("lightcyan"));
        Assert.assertNotNull(CssColorName.getThis("lightgoldenrodyellow"));
        Assert.assertNotNull(CssColorName.getThis("lightgray"));
        Assert.assertNotNull(CssColorName.getThis("lightgreen"));
        Assert.assertNotNull(CssColorName.getThis("lightpink"));
        Assert.assertNotNull(CssColorName.getThis("lightsalmon"));
        Assert.assertNotNull(CssColorName.getThis("lightseagreen"));
        Assert.assertNotNull(CssColorName.getThis("lightskyblue"));
        Assert.assertNotNull(CssColorName.getThis("lightslategray"));
        Assert.assertNotNull(CssColorName.getThis("lightsteelblue"));
        Assert.assertNotNull(CssColorName.getThis("lightyellow"));
        Assert.assertNotNull(CssColorName.getThis("lime"));
        Assert.assertNotNull(CssColorName.getThis("limegreen"));
        Assert.assertNotNull(CssColorName.getThis("linen"));
        Assert.assertNotNull(CssColorName.getThis("magenta"));
        Assert.assertNotNull(CssColorName.getThis("maroon"));
        Assert.assertNotNull(CssColorName.getThis("mediumaquamarine"));
        Assert.assertNotNull(CssColorName.getThis("mediumblue"));
        Assert.assertNotNull(CssColorName.getThis("mediumorchid"));
        Assert.assertNotNull(CssColorName.getThis("mediumpurple"));
        Assert.assertNotNull(CssColorName.getThis("mediumseagreen"));
        Assert.assertNotNull(CssColorName.getThis("mediumslateblue"));
        Assert.assertNotNull(CssColorName.getThis("mediumspringgreen"));
        Assert.assertNotNull(CssColorName.getThis("mediumturquoise"));
        Assert.assertNotNull(CssColorName.getThis("mediumvioletred"));
        Assert.assertNotNull(CssColorName.getThis("midnightblue"));
        Assert.assertNotNull(CssColorName.getThis("mintcream"));
        Assert.assertNotNull(CssColorName.getThis("mistyrose"));
        Assert.assertNotNull(CssColorName.getThis("moccasin"));
        Assert.assertNotNull(CssColorName.getThis("navy"));
        Assert.assertNotNull(CssColorName.getThis("oldlace"));
        Assert.assertNotNull(CssColorName.getThis("olive"));
        Assert.assertNotNull(CssColorName.getThis("olivedrab"));
        Assert.assertNotNull(CssColorName.getThis("orange"));
        Assert.assertNotNull(CssColorName.getThis("orangered"));
        Assert.assertNotNull(CssColorName.getThis("orchid"));
        Assert.assertNotNull(CssColorName.getThis("palegreen"));
        Assert.assertNotNull(CssColorName.getThis("palegoldenrod"));
        Assert.assertNotNull(CssColorName.getThis("paleturquoise"));
        Assert.assertNotNull(CssColorName.getThis("palevioletred"));
        Assert.assertNotNull(CssColorName.getThis("rebeccapurple"));
        Assert.assertNotNull(CssColorName.getThis("navajowhite"));
        Assert.assertNotNull(CssColorName.getThis("springgreen"));
        Assert.assertNotNull(CssColorName.getThis("yellowgreen"));
        Assert.assertNotNull(CssColorName.getThis("papayawhip"));
        Assert.assertNotNull(CssColorName.getThis("peachpuff"));
        Assert.assertNotNull(CssColorName.getThis("peru"));
        Assert.assertNotNull(CssColorName.getThis("pink"));
        Assert.assertNotNull(CssColorName.getThis("plum"));
        Assert.assertNotNull(CssColorName.getThis("powderblue"));
        Assert.assertNotNull(CssColorName.getThis("purple"));
        Assert.assertNotNull(CssColorName.getThis("red"));
        Assert.assertNotNull(CssColorName.getThis("rosybrown"));
        Assert.assertNotNull(CssColorName.getThis("royalblue"));
        Assert.assertNotNull(CssColorName.getThis("saddlebrown"));
        Assert.assertNotNull(CssColorName.getThis("salmon"));
        Assert.assertNotNull(CssColorName.getThis("sandybrown"));
        Assert.assertNotNull(CssColorName.getThis("seagreen"));
        Assert.assertNotNull(CssColorName.getThis("seashell"));
        Assert.assertNotNull(CssColorName.getThis("sienna"));
        Assert.assertNotNull(CssColorName.getThis("silver"));
        Assert.assertNotNull(CssColorName.getThis("skyblue"));
        Assert.assertNotNull(CssColorName.getThis("slateblue"));
        Assert.assertNotNull(CssColorName.getThis("slategray"));
        Assert.assertNotNull(CssColorName.getThis("snow"));
        Assert.assertNotNull(CssColorName.getThis("steelblue"));
        Assert.assertNotNull(CssColorName.getThis("tan"));
        Assert.assertNotNull(CssColorName.getThis("teal"));
        Assert.assertNotNull(CssColorName.getThis("thistle"));
        Assert.assertNotNull(CssColorName.getThis("tomato"));
        Assert.assertNotNull(CssColorName.getThis("turquoise"));
        Assert.assertNotNull(CssColorName.getThis("violet"));
        Assert.assertNotNull(CssColorName.getThis("wheat"));
        Assert.assertNotNull(CssColorName.getThis("white"));
        Assert.assertNotNull(CssColorName.getThis("whitesmoke"));
        Assert.assertNotNull(CssColorName.getThis("yellow"));
        
        
        
    }

}
