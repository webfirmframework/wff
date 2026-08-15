/*
 * Copyright since 2014 Web Firm Framework
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

import com.webfirmframework.wffweb.tag.html.stylesandsemantics.Div;
import org.junit.Assert;
import org.junit.Test;

public class PopoverTest {

    @Test
    public void testPopover() {
        Popover popover = new Popover();
        Div div = new Div(null, popover);

        Assert.assertEquals("<div popover></div>",  div.toHtmlString());

        popover.setValue(Popover.AUTO);
        Assert.assertEquals("<div popover=\"auto\"></div>",  div.toHtmlString());

        popover.setValue(Popover.HINT);
        Assert.assertEquals("<div popover=\"hint\"></div>",  div.toHtmlString());

        popover.setValue(Popover.MANUAL);
        Assert.assertEquals("<div popover=\"manual\"></div>",  div.toHtmlString());

        popover.setValue(null);
        Assert.assertEquals("<div popover></div>",  div.toHtmlString());

        popover.setValue("");
        Assert.assertEquals("<div popover=\"\"></div>",  div.toHtmlString());
    }

    @Test
    public void testPopover1() {
        Div div = new Div(null, new Popover(Popover.AUTO));
        Assert.assertEquals("<div popover=\"auto\"></div>",  div.toHtmlString());
    }

    @Test
    public void testPopover2() {
        Div div = new Div(null, new Popover(Popover.HINT));
        Assert.assertEquals("<div popover=\"hint\"></div>",  div.toHtmlString());
    }

    @Test
    public void testPopover3() {
        Div div = new Div(null, new Popover(Popover.MANUAL));
        Assert.assertEquals("<div popover=\"manual\"></div>",  div.toHtmlString());
    }

    @Test
    public void testPopover4() {
        Div div = new Div(null, new Popover(""));
        Assert.assertEquals("<div popover=\"\"></div>",  div.toHtmlString());
    }
}
