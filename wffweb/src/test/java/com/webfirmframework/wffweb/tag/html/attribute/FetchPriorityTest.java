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

public class FetchPriorityTest {

    @Test
    public void testFetchPriority() {
        FetchPriority fetchPriority = new FetchPriority();
        Div div = new Div(null, fetchPriority);

        Assert.assertEquals("<div fetchpriority></div>",  div.toHtmlString());

        fetchPriority.setValue(FetchPriority.HIGH);
        Assert.assertEquals("<div fetchpriority=\"high\"></div>",  div.toHtmlString());

        fetchPriority.setValue(FetchPriority.LOW);
        Assert.assertEquals("<div fetchpriority=\"low\"></div>",  div.toHtmlString());

        fetchPriority.setValue(FetchPriority.AUTO);
        Assert.assertEquals("<div fetchpriority=\"auto\"></div>",  div.toHtmlString());

        fetchPriority.setValue(null);
        Assert.assertEquals("<div fetchpriority></div>",  div.toHtmlString());

        fetchPriority.setValue("");
        Assert.assertEquals("<div fetchpriority=\"\"></div>",  div.toHtmlString());
    }

    @Test
    public void testFetchPriority1() {
        Div div = new Div(null, new FetchPriority(FetchPriority.HIGH));
        Assert.assertEquals("<div fetchpriority=\"high\"></div>",  div.toHtmlString());
    }

    @Test
    public void testFetchPriority2() {
        Div div = new Div(null, new FetchPriority(FetchPriority.LOW));
        Assert.assertEquals("<div fetchpriority=\"low\"></div>",  div.toHtmlString());
    }

    @Test
    public void testFetchPriority3() {
        Div div = new Div(null, new FetchPriority(FetchPriority.AUTO));
        Assert.assertEquals("<div fetchpriority=\"auto\"></div>",  div.toHtmlString());
    }

    @Test
    public void testFetchPriority4() {
        Div div = new Div(null, new FetchPriority(""));
        Assert.assertEquals("<div fetchpriority=\"\"></div>",  div.toHtmlString());
    }
}
