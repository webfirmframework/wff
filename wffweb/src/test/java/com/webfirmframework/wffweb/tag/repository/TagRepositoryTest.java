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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.webfirmframework.wffweb.tag.html.AbstractHtml;
import com.webfirmframework.wffweb.tag.html.Body;
import com.webfirmframework.wffweb.tag.html.Html;
import com.webfirmframework.wffweb.tag.html.TagNameConstants;
import com.webfirmframework.wffweb.tag.html.attribute.Name;
import com.webfirmframework.wffweb.tag.html.attribute.core.AbstractAttribute;
import com.webfirmframework.wffweb.tag.html.attribute.global.Id;
import com.webfirmframework.wffweb.tag.html.formsandinputs.Form;
import com.webfirmframework.wffweb.tag.html.formsandinputs.Input;
import com.webfirmframework.wffweb.tag.html.formsandinputs.TextArea;
import com.webfirmframework.wffweb.tag.html.metainfo.Head;
import com.webfirmframework.wffweb.tag.html.stylesandsemantics.Div;
import com.webfirmframework.wffweb.tag.htmlwff.NoTag;

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
    
    @Test
    public void testFindTagsByTagName() {
        final Set<Div> divs = new HashSet<Div>();
        
        final Form form = new Form(null) {
            {
                new Input(this, new Name("name1"));
                Div d1 = new Div(this, new Name("excluded")){{
                    new Input(this, new Name("name2"));
                    }};
                    divs.add(d1);
                new Input(this, new Name("name3"));
                Div d2 = new Div(this){{new Input(this, new Name("name4"));}};
                divs.add(d2);
                Div d3 = new Div(this){{new Input(this, new Id("id4"));}};
                divs.add(d3);
                Div d4 = new Div(this){{new Input(this, new Name("name4"));}};
                divs.add(d4);
                new TextArea(this, new Name("included"));
            }
        };
        
        
        Assert.assertEquals(form, TagRepository.findOneTagByTagName(TagNameConstants.FORM, form));
        Assert.assertEquals("div", TagRepository.findOneTagByTagName(TagNameConstants.DIV, form).getTagName());
        
        final Collection<AbstractHtml> tagsByTagName = TagRepository.findTagsByTagName(TagNameConstants.DIV, form);
        Assert.assertTrue(tagsByTagName.size() == divs.size());
        
        for (Div div : divs) {
            Assert.assertTrue(tagsByTagName.contains(div));
        }
        
        
        final AbstractHtml[] tags = new AbstractHtml[1];
        
        Html html = new Html(null) {{
            new Head(this);
            new Body(this) {{
                tags[0] = this;
                    new Div(this) {{
                            new NoTag(this, "\nsamplediv ");
                            new Div(this);
                    }};
            }};
        }};
        
        Assert.assertEquals(tags[0], TagRepository.findOneTagByTagName(TagNameConstants.BODY, html));
        Assert.assertEquals("div", TagRepository.findOneTagByTagName(TagNameConstants.DIV, html).getTagName());
        
    }
    
    @Test
    public void testFindAttributesByTagName() {
        
        
        final Set<AbstractAttribute> attributes = new HashSet<AbstractAttribute>();
        
        final Id idTwo = new Id("two");
        final Name name = new Name("name");
        
        attributes.add(idTwo);
        attributes.add(name);
        
        
        Html html = new Html(null) {{
            new Head(this);
            new Body(this, new Id("one")) {{
                new Div(this, idTwo, name) {{
                    new NoTag(this, "\nsamplediv ");
                    new Div(this);
                }};
            }};
        }};
        
        final Collection<AbstractAttribute> attributesByTagName = TagRepository.findAttributesByTagName(TagNameConstants.DIV, html);
        Assert.assertTrue(attributesByTagName.size() == attributes.size());
        
        for (AbstractAttribute attr : attributes) {
            Assert.assertTrue(attributesByTagName.contains(attr));
        }
        
    }

}
