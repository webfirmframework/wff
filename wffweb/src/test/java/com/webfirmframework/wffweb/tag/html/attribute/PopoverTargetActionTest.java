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

public class PopoverTargetActionTest {

    @Test
    public void testPopover() {
        PopoverTargetAction popoverTargetAction = new PopoverTargetAction();
        Div div = new Div(null, popoverTargetAction);

        Assert.assertEquals("<div popovertargetaction></div>",  div.toHtmlString());

        popoverTargetAction.setValue(PopoverTargetAction.HIDE);
        Assert.assertEquals("<div popovertargetaction=\"hide\"></div>",  div.toHtmlString());

        popoverTargetAction.setValue(PopoverTargetAction.SHOW);
        Assert.assertEquals("<div popovertargetaction=\"show\"></div>",  div.toHtmlString());

        popoverTargetAction.setValue(PopoverTargetAction.TOGGLE);
        Assert.assertEquals("<div popovertargetaction=\"toggle\"></div>",  div.toHtmlString());

        popoverTargetAction.setValue(null);
        Assert.assertEquals("<div popovertargetaction></div>",  div.toHtmlString());

        popoverTargetAction.setValue("");
        Assert.assertEquals("<div popovertargetaction=\"\"></div>",  div.toHtmlString());
    }

    @Test
    public void testPopover1() {
        Div div = new Div(null, new PopoverTargetAction(PopoverTargetAction.HIDE));
        Assert.assertEquals("<div popovertargetaction=\"hide\"></div>",  div.toHtmlString());
    }

    @Test
    public void testPopover2() {
        Div div = new Div(null, new PopoverTargetAction(PopoverTargetAction.SHOW));
        Assert.assertEquals("<div popovertargetaction=\"show\"></div>",  div.toHtmlString());
    }

    @Test
    public void testPopover3() {
        Div div = new Div(null, new PopoverTargetAction(PopoverTargetAction.TOGGLE));
        Assert.assertEquals("<div popovertargetaction=\"toggle\"></div>",  div.toHtmlString());
    }

    @Test
    public void testPopover4() {
        Div div = new Div(null, new PopoverTargetAction(""));
        Assert.assertEquals("<div popovertargetaction=\"\"></div>",  div.toHtmlString());
    }
}
