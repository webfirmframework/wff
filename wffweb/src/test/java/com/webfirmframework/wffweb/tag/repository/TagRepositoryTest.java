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
package com.webfirmframework.wffweb.tag.repository;

import org.junit.Assert;
import org.junit.Test;

import com.webfirmframework.wffweb.tag.html.AbstractHtml;
import com.webfirmframework.wffweb.tag.html.attribute.Name;
import com.webfirmframework.wffweb.tag.html.attribute.global.Id;
import com.webfirmframework.wffweb.tag.html.formsandinputs.Form;
import com.webfirmframework.wffweb.tag.html.formsandinputs.Input;
import com.webfirmframework.wffweb.tag.html.formsandinputs.TextArea;
import com.webfirmframework.wffweb.tag.html.stylesandsemantics.Div;

@SuppressWarnings("serial")
public class TagRepositoryTest {

    @Test
    public void testFindTagById() {
        final Form form = new Form(null) {
            {
                new Input(this, new Name("name1"));
                new Div(this, new Name("excluded")){{new Input(this, new Name("name2"));}};
                new Input(this, new Name("name3"));
                new Div(this){{new Input(this, new Name("name4"));}};
                new Div(this){{new Input(this, new Id("id4"));}};
                new Div(this){{new Input(this, new Name("name4"));}};
                new TextArea(this, new Name("included"));
            }
        };
        
        final Input input44 = new Input(null, new Id("id44"));
        
        final Form form2 = new Form(null) {
            {
                new Input(this, new Name("name1"));
                new Div(this, new Name("excluded")){{new Input(this, new Name("name2"));}};
                new Input(this, new Name("name3"));
                new Div(this){{new Input(this, new Name("name4"));}};
                new Div(this){{appendChild(input44);}};
                new Div(this){{new Input(this, new Name("name4"));}};
                new TextArea(this, new Name("included"));
            }
        };
        
        AbstractHtml found = TagRepository.findTagById("id4", form);
        
        Assert.assertNotNull(found);
        Assert.assertEquals("id4", ((Id) found.getAttributeByName("id")).getValue());
        
        AbstractHtml found2 = TagRepository.findTagById("id44", form2);
        Assert.assertNotNull(found2);
        Assert.assertEquals(input44, found2);
        Assert.assertEquals("id44", ((Id) found2.getAttributeByName("id")).getValue());
        
        
    }

}
