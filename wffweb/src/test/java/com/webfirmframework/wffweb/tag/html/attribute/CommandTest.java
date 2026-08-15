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

public class CommandTest {

    @Test
    public void testCommand() {
        Command command = new Command();
        Div div = new Div(null, command);

        Assert.assertEquals("<div command></div>",  div.toHtmlString());

        command.setValue(Command.CLOSE);
        Assert.assertEquals("<div command=\"close\"></div>",  div.toHtmlString());

        command.setValue(Command.SHOW_MODAL);
        Assert.assertEquals("<div command=\"show-modal\"></div>",  div.toHtmlString());

        command.setValue(Command.REQUEST_CLOSE);
        Assert.assertEquals("<div command=\"request-close\"></div>",  div.toHtmlString());

        command.setValue(Command.SHOW_POPOVER);
        Assert.assertEquals("<div command=\"show-popover\"></div>",  div.toHtmlString());

        command.setValue(Command.HIDE_POPOVER);
        Assert.assertEquals("<div command=\"hide-popover\"></div>",  div.toHtmlString());

        command.setValue(Command.TOGGLE_POPOVER);
        Assert.assertEquals("<div command=\"toggle-popover\"></div>",  div.toHtmlString());

        command.setValue(null);
        Assert.assertEquals("<div command></div>",  div.toHtmlString());

        command.setValue("");
        Assert.assertEquals("<div command=\"\"></div>",  div.toHtmlString());
    }

    @Test
    public void testCommand1() {
        Div div = new Div(null, new Command(Command.CLOSE));
        Assert.assertEquals("<div command=\"close\"></div>",  div.toHtmlString());
    }

    @Test
    public void testCommand2() {
        Div div = new Div(null, new Command(Command.SHOW_MODAL));
        Assert.assertEquals("<div command=\"show-modal\"></div>",  div.toHtmlString());
    }

    @Test
    public void testCommand3() {
        Div div = new Div(null, new Command(Command.REQUEST_CLOSE));
        Assert.assertEquals("<div command=\"request-close\"></div>",  div.toHtmlString());
    }

    @Test
    public void testCommand4() {
        Div div = new Div(null, new Command(""));
        Assert.assertEquals("<div command=\"\"></div>",  div.toHtmlString());
    }
}
