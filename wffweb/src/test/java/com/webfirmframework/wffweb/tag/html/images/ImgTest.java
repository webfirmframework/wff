/*
 * Copyright 2014-2023 Web Firm Framework
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
package com.webfirmframework.wffweb.tag.html.images;

import static org.junit.Assert.assertEquals;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.webfirmframework.wffweb.tag.html.AbstractHtml.TagType;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ImgTest {


    @Test
    public void atestImg1() {

        Img img = new Img(null);
        assertEquals("<img>", img.toHtmlString());
        
        Img.setTagType(TagType.OPENING_CLOSING);
        img = new Img(null);
        assertEquals("<img></img>", img.toHtmlString());
        
        Img.setTagType(TagType.SELF_CLOSING);
        img = new Img(null);
        assertEquals("<img/>", img.toHtmlString());
        
        Img.setTagType(TagType.NON_CLOSING);
        img = new Img(null);
        assertEquals("<img>", img.toHtmlString());
    }

    @Test
    public void testImg2() {

        Img.setTagType(TagType.SELF_CLOSING);
        Img img = new Img(null);
        assertEquals("<img/>", img.toHtmlString());

    }

}
