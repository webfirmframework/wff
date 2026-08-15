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
package com.webfirmframework.wffweb.tag.html.html5.attribute;

import com.webfirmframework.wffweb.tag.html.stylesandsemantics.Div;
import org.junit.Assert;
import org.junit.Test;

public class CrossOriginTest {

    @Test
    public void testCrossOrigin() {
        CrossOrigin crossOrigin = new CrossOrigin();
        Div div = new Div(null, crossOrigin);

        Assert.assertEquals("<div crossorigin></div>",  div.toHtmlString());

        crossOrigin.setValue(CrossOrigin.ANONYMOUS);
        Assert.assertEquals("<div crossorigin=\"anonymous\"></div>",  div.toHtmlString());

        crossOrigin.setValue(CrossOrigin.USE_CREDENTIALS);
        Assert.assertEquals("<div crossorigin=\"use-credentials\"></div>",  div.toHtmlString());

        crossOrigin.setValue(null);
        Assert.assertEquals("<div crossorigin></div>",  div.toHtmlString());

        crossOrigin.setValue("");
        Assert.assertEquals("<div crossorigin=\"\"></div>",  div.toHtmlString());
    }

    @Test
    public void testCrossOrigin1() {
        Div div = new Div(null, new CrossOrigin(CrossOrigin.ANONYMOUS));
        Assert.assertEquals("<div crossorigin=\"anonymous\"></div>",  div.toHtmlString());
    }

    @Test
    public void testCrossOrigin2() {
        Div div = new Div(null, new CrossOrigin(CrossOrigin.USE_CREDENTIALS));
        Assert.assertEquals("<div crossorigin=\"use-credentials\"></div>",  div.toHtmlString());
    }

    @Test
    public void testCrossOrigin4() {
        Div div = new Div(null, new CrossOrigin(""));
        Assert.assertEquals("<div crossorigin=\"\"></div>",  div.toHtmlString());
    }
}
