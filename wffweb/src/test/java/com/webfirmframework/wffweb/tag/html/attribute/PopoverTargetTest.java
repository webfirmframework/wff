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
package com.webfirmframework.wffweb.tag.html.attribute;

import com.webfirmframework.wffweb.tag.html.stylesandsemantics.Div;
import org.junit.Assert;
import org.junit.Test;

public class PopoverTargetTest {


    @Test
    public void testPopoverTarget() {
        PopoverTarget popover = new PopoverTarget();
        Div div = new Div(null, popover);

        Assert.assertEquals("<div popovertarget=\"\"></div>",  div.toHtmlString());

        popover.setValue("id-1");
        Assert.assertEquals("<div popovertarget=\"id-1\"></div>",  div.toHtmlString());

        popover.setValue(null);
        Assert.assertEquals("<div popovertarget></div>",  div.toHtmlString());

        popover.setValue("");
        Assert.assertEquals("<div popovertarget=\"\"></div>",  div.toHtmlString());
    }

    @Test
    public void testPopoverTarget1() {
        PopoverTarget popover = new PopoverTarget("id-1");
        Div div = new Div(null, popover);
        Assert.assertEquals("<div popovertarget=\"id-1\"></div>",  div.toHtmlString());
    }

    @Test
    public void testPopoverTarget4() {
        PopoverTarget popover = new PopoverTarget("");
        Div div = new Div(null, popover);
        Assert.assertEquals("<div popovertarget=\"\"></div>",  div.toHtmlString());
    }
    
}
