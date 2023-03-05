package com.webfirmframework.wffweb.common;

import com.webfirmframework.wffweb.server.page.BrowserPage;
import com.webfirmframework.wffweb.server.page.action.BrowserPageAction;
import com.webfirmframework.wffweb.tag.html.AbstractHtml;

/**
 * should be kept in a package other than com.webfirmframework.wffweb.server.page
 */
public class SampleBrowserPage extends BrowserPage {

    private static final long serialVersionUID = 1L;

    @Override
    public String webSocketUrl() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AbstractHtml render() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Settings useSettings() {
        Settings defaultSettings = super.defaultSettings();
        //to test if there is any compilation error for ServerSideAction in anonymous way
        OnPayloadLoss onPayloadLoss = new OnPayloadLoss("location.reload();", new ServerSideAction() {
            @Override
            public void perform() {
                SampleBrowserPage.this.performBrowserPageAction(BrowserPageAction.RELOAD_FROM_CACHE.getActionByteBuffer());
            }
        });

        //to test if there is any compilation error for ServerSideAction in anonymous way
        onPayloadLoss = new OnPayloadLoss("location.reload();", () -> SampleBrowserPage.this.performBrowserPageAction(BrowserPageAction.RELOAD_FROM_CACHE.getActionByteBuffer()));

        Settings settings = new BrowserPage.Settings(
                defaultSettings.inputBufferLimit(),
                defaultSettings.inputBufferTimeout(),
                defaultSettings.outputBufferLimit(),
                defaultSettings.outputBufferTimeout(),
                onPayloadLoss);
        return settings;
    }
    
    //the method is public just for testing, BrowserPage.Settings is not publicly accessible 
    @SuppressWarnings("exports")
    public BrowserPage.Settings getDefaultSettings() {     
        return defaultSettings();
    }
}
