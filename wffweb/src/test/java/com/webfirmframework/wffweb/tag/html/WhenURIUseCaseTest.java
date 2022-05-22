package com.webfirmframework.wffweb.tag.html;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.Test;

import com.webfirmframework.wffweb.server.page.BrowserPage;
import com.webfirmframework.wffweb.tag.html.attribute.Name;
import com.webfirmframework.wffweb.tag.html.stylesandsemantics.Div;
import com.webfirmframework.wffweb.tag.htmlwff.NoTag;

public class WhenURIUseCaseTest {

    private Html html;
    
    private Set<AbstractHtml> expectedTagsForURIChange;
    
    private Div mainDiv;
    
    private String initialUri = "/someuri";
    
    private String uri1 = "/user";

    private String uri11 = "/user/dashboard";

    private String uri111 = "/user/dashboard/items";
    
    private String uri112 = "/user/dashboard/otheritems";

    private BrowserPage browserPage;
    

    @Before
    public void setup() {
        expectedTagsForURIChange = new HashSet<>();

        html = new Html(null);
        
        mainDiv = new Div(html);

        browserPage = new BrowserPage() {

            @Override
            public String webSocketUrl() {
                // TODO Auto-generated method stub
                return "wss://wffweb";
            }

            @Override
            public AbstractHtml render() {
                super.setURI(initialUri);

                return html;
            }

        };
        browserPage.toHtmlString();
        
    }
    
    
    @Test
    public void testUsage1() {
        

        StringBuilder controlFlow = new StringBuilder();

        AbstractHtml div1 = new Div(null).whenURI((uriEvent) -> uriEvent.uriAfter().startsWith(initialUri), () -> {

            controlFlow.append("div1.whenURI\n");

            return new AbstractHtml[] {new NoTag(null, "somecontent1")};
        }).currentAs();
        
        mainDiv.appendChild(div1);
        assertEquals("div1.whenURI\n", controlFlow.toString());
        assertEquals("<div data-wff-id=\"S3\">somecontent1</div>", div1.toBigHtmlString());
        
        browserPage.setURI(uri1);
        
        assertEquals("<div data-wff-id=\"S3\"></div>", div1.toBigHtmlString());
        
        assertEquals("div1.whenURI\n", controlFlow.toString());
        
       
    }
    
    @Test
    public void testUsage2() {
        

        StringBuilder controlFlow = new StringBuilder();

        AbstractHtml div1 = new Div(null).whenURI(uriEvent -> uriEvent.uriAfter().startsWith(initialUri), (event) -> {

            controlFlow.append("div1.whenURI\n");

            event.sourceTag().addInnerHtmls(new AbstractHtml[] {new NoTag(null, "somecontent1")});
        }).currentAs();
        
        mainDiv.appendChild(div1);
        assertEquals("div1.whenURI\n", controlFlow.toString());
        assertEquals("<div data-wff-id=\"S3\">somecontent1</div>", div1.toBigHtmlString());
        
        browserPage.setURI(uri1);
        
        assertEquals("div1.whenURI\n", controlFlow.toString());
        assertEquals("<div data-wff-id=\"S3\">somecontent1</div>", div1.toBigHtmlString());
        
       
    }
    
    @Test
    public void testUsage3() {
        
        AtomicInteger counter = new AtomicInteger();
        

        StringBuilder controlFlow = new StringBuilder();
        
        AbstractHtml div3 = new Div(null, new Name("div3-" + counter.incrementAndGet()));
        div3.whenURI(uriEvent -> uriEvent.uriAfter().startsWith(uri111), () -> {

            controlFlow.append("div3.whenURI1+");

            return new AbstractHtml[] {new NoTag(null, "somecontent1 uri111")};
        }).currentAs();
        
        div3.whenURI(uriEvent -> uriEvent.uriAfter().startsWith(uri112), () -> {

            controlFlow.append("div3.whenURI2+");

            return new AbstractHtml[] {new NoTag(null, "somecontent2 uri112")};
        }).currentAs();
        
        
        AbstractHtml div2 = new Div(null, new Name("div2-" + counter.incrementAndGet())).whenURI(uriEvent -> uriEvent.uriAfter().startsWith(uri11), () -> {

            controlFlow.append("div2.whenURI+");

            return new AbstractHtml[] {div3};
        }).currentAs();
        
        
        AbstractHtml div1 = new Div(null, new Name("div1-" + counter.incrementAndGet())).whenURI(uriEvent -> uriEvent.uriAfter().startsWith(uri1), () -> {

            controlFlow.append("div1.whenURI+");

            return new AbstractHtml[] {div2};
        }).currentAs();
        
        
        mainDiv.appendChild(div1);
        String expectedCFForInitialURI = "";
        assertEquals(expectedCFForInitialURI, controlFlow.toString());
        String expectedForInitialURI = "<div data-wff-id=\"S3\" name=\"div1-3\"></div>";
        assertEquals(expectedForInitialURI, div1.toBigHtmlString());
        
        controlFlow.delete(0, controlFlow.length());
        browserPage.setURI(uri1);
        
        String expectedCFForURI1 = "div1.whenURI+";
        assertEquals(expectedCFForURI1, controlFlow.toString());
        String expectedForURI1 = "<div data-wff-id=\"S3\" name=\"div1-3\"><div data-wff-id=\"S4\" name=\"div2-2\"></div></div>";
        assertEquals(expectedForURI1, div1.toBigHtmlString());
        
        
        controlFlow.delete(0, controlFlow.length());
        browserPage.setURI(uri11);
        
        String expectedCFForURI11 = "div1.whenURI+div2.whenURI+";
        assertEquals(expectedCFForURI11, controlFlow.toString());
        String expectedForURI11 = "<div data-wff-id=\"S3\" name=\"div1-3\"><div data-wff-id=\"S4\" name=\"div2-2\"><div data-wff-id=\"S5\" name=\"div3-1\"></div></div></div>";
        assertEquals(expectedForURI11, div1.toBigHtmlString());
        
        controlFlow.delete(0, controlFlow.length());
        browserPage.setURI(uri111);
        
        String expectedCFForURI111 = "div1.whenURI+div2.whenURI+div3.whenURI1+";
        assertEquals(expectedCFForURI111, controlFlow.toString());
        String expectedForURI111 = "<div data-wff-id=\"S3\" name=\"div1-3\"><div data-wff-id=\"S4\" name=\"div2-2\"><div data-wff-id=\"S5\" name=\"div3-1\">somecontent1 uri111</div></div></div>";
        assertEquals(expectedForURI111, div1.toBigHtmlString());
        
        
        
        controlFlow.delete(0, controlFlow.length());
        browserPage.setURI(uri112);
        
        String expectedCFForURI112 = "div1.whenURI+div2.whenURI+div3.whenURI2+";
        assertEquals(expectedCFForURI112, controlFlow.toString());
        String expectedForURI112 = "<div data-wff-id=\"S3\" name=\"div1-3\"><div data-wff-id=\"S4\" name=\"div2-2\"><div data-wff-id=\"S5\" name=\"div3-1\">somecontent2 uri112</div></div></div>";
        assertEquals(expectedForURI112, div1.toBigHtmlString());
        
        
        record ResultCombination(String expectedHtml, String expectedControlFlow, String uri) {
        }
        
        List<ResultCombination> resultCombinations = new ArrayList<>();
        resultCombinations.add(new ResultCombination(expectedForURI111, expectedCFForURI111, uri111));
        resultCombinations.add(new ResultCombination(expectedForURI112, expectedCFForURI112, uri112));
        
        String prevURI = "";
        for(int i = 0; i < 250; i++) {
            for (var each: resultCombinations) {
                if (!prevURI.equals(each.uri)) {
                    controlFlow.delete(0, controlFlow.length());    
                }
                browserPage.setURI(each.uri);
                prevURI = each.uri;
                
                assertEquals(each.expectedControlFlow, controlFlow.toString());
                assertEquals(each.expectedHtml, div1.toBigHtmlString());
            }
            Collections.shuffle(resultCombinations);
        }
        
        
        
        resultCombinations.add(new ResultCombination(expectedForInitialURI, expectedCFForInitialURI, initialUri));
        resultCombinations.add(new ResultCombination(expectedForURI1, expectedCFForURI1, uri1));
        resultCombinations.add(new ResultCombination(expectedForURI11, expectedCFForURI11, uri11));
        
        
        for(int i = 0; i < 250; i++) {
            for (var each: resultCombinations) {
                if (!prevURI.equals(each.uri)) {
                    controlFlow.delete(0, controlFlow.length());    
                }
                
                browserPage.setURI(each.uri);
                prevURI = each.uri;
                assertEquals(each.expectedControlFlow, controlFlow.toString());
            } 
        }
       
    }
    
    @Test
    public void testUsage4() {

        StringBuilder controlFlow = new StringBuilder();
        
        AbstractHtml someWrapper = new Div(null).give(dv -> {
            AbstractHtml div1 = new Div(dv).whenURI(uriEvent -> uriEvent.uriAfter().startsWith("unmatched"), event -> {

                controlFlow.append("success.whenURI\n");

                event.sourceTag().addInnerHtmls(new AbstractHtml[] { new NoTag(null, "somecontent1") });
            }, event -> {

                controlFlow.append("fail.whenURI\n");

                event.sourceTag().addInnerHtmls(new AbstractHtml[] { new NoTag(null, "somecontent2") });
            }).currentAs();
            
        });

        

        mainDiv.appendChild(someWrapper);
        assertEquals("fail.whenURI\n", controlFlow.toString());
//        assertEquals("<div data-wff-id=\"S3\">somecontent2</div>", div1.toBigHtmlString());

        browserPage.setURI(uri1);

        assertEquals("fail.whenURI\nfail.whenURI\n", controlFlow.toString());
//        assertEquals("<div data-wff-id=\"S3\">somecontent2</div>", div1.toBigHtmlString());
        
        browserPage.setURI("changeduri");
        assertEquals("fail.whenURI\nfail.whenURI\nfail.whenURI\n", controlFlow.toString());

    }
    
    @Test
    public void testGetURI() {
        
        BrowserPage browserPage = new BrowserPage() {

            @Override
            public String webSocketUrl() {
                // TODO Auto-generated method stub
                return "wss://wffweb";
            }

            @Override
            public AbstractHtml render() {
                return new Html(null);
            }

        };
        assertNull(browserPage.getURI());
        browserPage.toHtmlString();
        assertNull(browserPage.getURI());
        
    }
}
