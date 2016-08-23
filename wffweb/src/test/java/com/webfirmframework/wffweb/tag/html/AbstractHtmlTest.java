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
package com.webfirmframework.wffweb.tag.html;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.webfirmframework.wffweb.tag.html.attribute.Name;
import com.webfirmframework.wffweb.tag.html.attribute.global.Id;
import com.webfirmframework.wffweb.tag.html.stylesandsemantics.Div;
import com.webfirmframework.wffweb.util.WffBinaryMessageUtil;
import com.webfirmframework.wffweb.util.data.NameValue;

public class AbstractHtmlTest {

    //for future development
//    @Test
    public void testToOutputStreamBooleanOutputStream() {
        
        Html html = new Html(null, new Id("id1")) {
            {
                for (int i = 0; i < 100; i++) {

                    new Div(this, new Id("id2 " + i));
                }
            }
        };
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            html.toOutputStream(true, baos);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        byte[] wffMessageBytes = baos.toByteArray();
        System.out.println("wffMessageBytes.length "+wffMessageBytes.length);
        System.out.println("html.toHtmlString().length() "+html.toHtmlString().length());
        List<NameValue> parse = WffBinaryMessageUtil.VERSION_1.parse(wffMessageBytes);
        
        
        
        System.out.println(html.toHtmlString());
        
//        fail("Not yet implemented");
    }
    
    @Test
    public void testAddRemoveAttributesAttributes() {
        
        final Name name = new Name("somename");
        
        Html html = new Html(null) {{
            Div div1 = new Div(this);
            div1.addAttributes(name);
            Div div2 = new Div(this);
            div2.addAttributes(name);
            
            Assert.assertTrue(name.getOwnerTags().contains(div1));
            Assert.assertTrue(name.getOwnerTags().contains(div2));
        }};
        
        html.addAttributes(name);
        Assert.assertTrue(name.getOwnerTags().contains(html));
        Assert.assertEquals("<html name=\"somename\"><div name=\"somename\"></div><div name=\"somename\"></div></html>", html.toHtmlString());
        
        html.removeAttributes(name);
        Assert.assertFalse(name.getOwnerTags().contains(html));
        Assert.assertEquals("<html><div name=\"somename\"></div><div name=\"somename\"></div></html>", html.toHtmlString());
        
        html.addAttributes(name);
        Assert.assertTrue(name.getOwnerTags().contains(html));
        Assert.assertEquals("<html name=\"somename\"><div name=\"somename\"></div><div name=\"somename\"></div></html>", html.toHtmlString());
        
        name.setValue("another");
        Assert.assertEquals("<html name=\"another\"><div name=\"another\"></div><div name=\"another\"></div></html>", html.toHtmlString());
        
        name.setValue("somename");
        html.removeAttributes("name");
        Assert.assertFalse(name.getOwnerTags().contains(html));
        Assert.assertEquals("<html><div name=\"somename\"></div><div name=\"somename\"></div></html>", html.toHtmlString());
        
        html.addAttributes(name);
        Assert.assertTrue(name.getOwnerTags().contains(html));
        Assert.assertEquals("<html name=\"somename\"><div name=\"somename\"></div><div name=\"somename\"></div></html>", html.toHtmlString());
        
        html.removeAttributes(new Name("somename"));
        Assert.assertTrue(name.getOwnerTags().contains(html));
        Assert.assertEquals("<html name=\"somename\"><div name=\"somename\"></div><div name=\"somename\"></div></html>", html.toHtmlString());
        
        Name name2 = new Name("another");
        html.addAttributes(name2);
        Assert.assertFalse(name.getOwnerTags().contains(html));
        Assert.assertTrue(name2.getOwnerTags().contains(html));
        Assert.assertEquals("<html name=\"another\"><div name=\"somename\"></div><div name=\"somename\"></div></html>", html.toHtmlString());

    }
    
    

}
