package com.webfirmframework.wffweb.tag.html;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.webfirmframework.wffweb.server.page.BrowserPage;
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

        AbstractHtml div1 = new Div(null).whenURI((uri) -> uri.startsWith(initialUri), () -> {

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

        AbstractHtml div1 = new Div(null).whenURI(uri -> uri.startsWith(initialUri), (event) -> {

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
}
