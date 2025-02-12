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
 */
package com.webfirmframework.wffweb.server.page;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.Reference;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.junit.Assert;
import org.junit.Test;

import com.webfirmframework.wffweb.NotRenderedException;
import com.webfirmframework.wffweb.NullValueException;
import com.webfirmframework.wffweb.common.SampleBrowserPage;
import com.webfirmframework.wffweb.server.page.BrowserPage.Settings;
import com.webfirmframework.wffweb.tag.html.AbstractHtml;
import com.webfirmframework.wffweb.tag.html.Body;
import com.webfirmframework.wffweb.tag.html.Html;
import com.webfirmframework.wffweb.tag.html.metainfo.Head;
import com.webfirmframework.wffweb.tag.html.stylesandsemantics.Div;
import com.webfirmframework.wffweb.wffbm.data.WffBMObject;

@SuppressWarnings("serial")
public class BrowserPageTest {

    @Test
    public void testToHtmlString() {
        
        
        
        BrowserPage browserPage = new BrowserPage() {
            
            @Override
            public String webSocketUrl() {
                return "wss://webfirmframework.com/ws-con";
            }
            
            @Override
            public AbstractHtml render() {
                Html html = new Html(null) {
                    {
                        new Head(this);
                        new Body(this);
                    }
                };
                html.setPrependDocType(true);
                return html;
            }
        };
        
        String toHtmlString = browserPage.toHtmlString();
        
        Assert.assertEquals(toHtmlString, browserPage.toHtmlString());
        Assert.assertEquals(toHtmlString, browserPage.toHtmlString());
        Assert.assertEquals(toHtmlString, browserPage.toHtmlString());
        
//        fail("Not yet implemented");
    }
    
    @Test
    public void testContains() {
        
        final Div div = new Div(null);
        final Div div2 = new Div(null);
        
        BrowserPage browserPage = new BrowserPage() {
            
            @Override
            public String webSocketUrl() {
                return "wss://webfirmframework.com/ws-con";
            }
            
            @Override
            public AbstractHtml render() {
                Html html = new Html(null) {
                    {
                        new Head(this);
                        new Body(this).appendChild(div);
                    }
                };
                html.setPrependDocType(true);
                return html;
            }
        };
        
        @SuppressWarnings("unused")
        String toHtmlString = browserPage.toHtmlString();
        Assert.assertTrue(browserPage.contains(div));
        div.appendChild(div2);
        Assert.assertTrue(browserPage.contains(div2));
    }
    
    @Test(expected = NotRenderedException.class)
    public void testContainsWithNotRenderedException() {
        
        final Div div = new Div(null);
        
        BrowserPage browserPage = new BrowserPage() {
            
            @Override
            public String webSocketUrl() {
                return "wss://webfirmframework.com/ws-con";
            }
            
            @Override
            public AbstractHtml render() {
                Html html = new Html(null) {
                    {
                        new Head(this);
                        new Body(this).appendChild(div);
                    }
                };
                html.setPrependDocType(true);
                return html;
            }
        };
        Assert.assertFalse(browserPage.contains(div));
    }
    
    @Test(expected = NullValueException.class)
    public void testContainsWithNullValueException() {
        
        BrowserPage browserPage = new BrowserPage() {
            
            @Override
            public String webSocketUrl() {
                return "wss://webfirmframework.com/ws-con";
            }
            
            @Override
            public AbstractHtml render() {
                Html html = new Html(null) {
                    {
                        new Head(this);
                        new Body(this);
                    }
                };
                html.setPrependDocType(true);
                return html;
            }
        };
        @SuppressWarnings("unused")
        String toHtmlString = browserPage.toHtmlString();
        Assert.assertFalse(browserPage.contains(null));
    }
    
    @Test
    public void testPollAndConvertToByteArray() {
        {
            final ByteBuffer webfirmframework = ByteBuffer.wrap("webfirmframework".getBytes(StandardCharsets.UTF_8));
            final ByteBuffer wffweb = ByteBuffer.wrap("-wffweb".getBytes(StandardCharsets.UTF_8));
            final ByteBuffer useLatestVersion = ByteBuffer.wrap("-use latest version".getBytes(StandardCharsets.UTF_8));
            
            {
                Queue<ByteBuffer> queue = new ArrayDeque<>();
                queue.add(webfirmframework);
                int totalCapacity = 0;
                for (ByteBuffer byteBuffer : queue) {
                    totalCapacity += byteBuffer.array().length;
                }
                final byte[] merged = PayloadProcessor.pollAndConvertToByteArray(totalCapacity, queue);
                assertEquals("webfirmframework", new String(merged, StandardCharsets.UTF_8));
            }
            
            {
                Queue<ByteBuffer> queue = new ArrayDeque<>();
                queue.add(webfirmframework);
                queue.add(wffweb);
                queue.add(useLatestVersion);
                int totalCapacity = 0;
                for (ByteBuffer byteBuffer : queue) {
                    totalCapacity += byteBuffer.array().length;
                }
                final byte[] merged = PayloadProcessor.pollAndConvertToByteArray(totalCapacity, queue);
                assertEquals("webfirmframework-wffweb-use latest version", new String(merged, StandardCharsets.UTF_8));
            }
        }
    }

    @Test
    public void testPerformanceOfConcurrentLinkedQueueAndDeque()
            throws Exception {
        for (int i = 0; i < 1; i++) {
            printPerformanceOfConcurrentLinkedQueueAndDeque();
        }
    }
    
    public void printPerformanceOfConcurrentLinkedQueueAndDeque() throws Exception {
        Queue<ByteBuffer> concurrentLinkedQueue = new ConcurrentLinkedQueue<>();
        Queue<ByteBuffer> concurrentLinkedDeque = new ConcurrentLinkedDeque<>();
        long before = 0;
        long after = 0;
        long difference = 0;
        
        final ByteBuffer hello = ByteBuffer.wrap("Hello".getBytes(StandardCharsets.UTF_8)); 
        
        before = System.nanoTime();
        
        for (int i = 0; i < 1000; i++) {
            concurrentLinkedQueue.add(hello);
        }
        while(concurrentLinkedQueue.poll() != null) {
        }
        
        after = System.nanoTime();
        
        difference = after - before;
        
        System.out.println("concurrentLinkedQueue processing time: " + difference);
        
        before = System.nanoTime();
        
        for (int i = 0; i < 1000; i++) {
            concurrentLinkedDeque.add(hello);
        }
        while(concurrentLinkedDeque.poll() != null) {
        }
        
        after = System.nanoTime();
        
        difference = after - before;
        
        System.out.println("concurrentLinkedDeque processing time: " + difference);
        
        
        System.out.println("-----------");
        
    }
    
    public static Set<Reference<AbstractHtml>> getTagsForURIChangeForTest(BrowserPage browserPage) {
        return browserPage.getTagsForURIChangeForTest();
    }
    
    @Test
    public void testInvokeServerMethod() {
        BrowserPage browserPage = new BrowserPage() {

            @Override
            public String webSocketUrl() {
                return "wss://webfirmframework.com/ws-con";
            }

            @Override
            public AbstractHtml render() {
                Html html = new Html(null) {
                    {
                        new Head(this);
                        new Body(this);
                    }
                };
                html.setPrependDocType(true);
                return html;
            }
        };

        record RecordData(InputStream inputStream, OutputStream outputStream) {

        }

        final WffBMObject result = new WffBMObject();
        final InputStream inputStream = new ByteArrayInputStream(new byte[0]);
        final OutputStream outputStream = new ByteArrayOutputStream();
        final String uri = "/user";
        final String customServerMethodName1 = "customServerMethod1";
        final Object serverSideData = new String("someserversidedata");

        final RecordData recordData = new RecordData(inputStream, outputStream);

        WffBMObject returnedResult = null;

        browserPage.addServerMethod(customServerMethodName1, event -> {

            assertNotNull(event);

            RecordData recordDataObj = (RecordData) event.recordData();
            assertEquals(inputStream, recordDataObj.inputStream());
            assertEquals(outputStream, recordDataObj.outputStream());
            assertEquals(uri, event.uri());
            assertEquals(customServerMethodName1, event.serverMethodName());
            assertEquals(serverSideData, event.serverSideData());

            assertNull(event.data());
            assertNull(event.sourceTag());
            assertNull(event.sourceAttribute());

            return result;
        }, serverSideData);

        returnedResult = browserPage.invokeServerMethod(customServerMethodName1, recordData, uri);
        assertEquals(result, returnedResult);

        browserPage.addServerMethod(customServerMethodName1, event -> {

            assertNotNull(event);
            RecordData recordDataObj = (RecordData) event.recordData();
            assertEquals(inputStream, recordDataObj.inputStream());
            assertEquals(outputStream, recordDataObj.outputStream());
            assertEquals(uri, event.uri());
            assertEquals(customServerMethodName1, event.serverMethodName());
            assertEquals(customServerMethodName1, event.serverMethodName());

            assertNull(event.data());
            assertNull(event.serverSideData());
            assertNull(event.sourceTag());
            assertNull(event.sourceAttribute());

            return result;
        });

        returnedResult = browserPage.invokeServerMethod(customServerMethodName1, recordData, uri);
        assertEquals(result, returnedResult);

        final String customServerMethodName2 = "customServerMethod2";

        browserPage.addServerMethod(customServerMethodName2, event -> {

            assertNotNull(event);
            RecordData recordDataObj = (RecordData) event.recordData();
            assertEquals(inputStream, recordDataObj.inputStream());
            assertEquals(outputStream, recordDataObj.outputStream());
            assertEquals(uri, event.uri());
            assertEquals(customServerMethodName2, event.serverMethodName());
            assertNull(event.serverSideData());

            assertNull(event.data());
            assertNull(event.sourceTag());
            assertNull(event.sourceAttribute());

            return result;
        });

        returnedResult = browserPage.invokeServerMethod(customServerMethodName1, recordData, uri);
        assertEquals(result, returnedResult);

        browserPage.addServerMethod(customServerMethodName1, event -> {

            assertNotNull(event);
            RecordData recordDataObj = (RecordData) event.recordData();
            assertEquals(inputStream, recordDataObj.inputStream());
            assertEquals(outputStream, recordDataObj.outputStream());
            assertEquals(uri, event.uri());
            assertEquals(customServerMethodName1, event.serverMethodName());
            assertEquals(serverSideData, event.serverSideData());

            assertNull(event.data());
            assertNull(event.sourceTag());
            assertNull(event.sourceAttribute());

            return result;
        }, serverSideData);

        returnedResult = browserPage.invokeServerMethod(customServerMethodName1, recordData, uri);
        assertEquals(result, returnedResult);

    }

    @Test
    public void testSettings() {
        SampleBrowserPage sampleBrowserPage = new SampleBrowserPage();
        Settings defaultSettings = sampleBrowserPage.getDefaultSettings();
        assertEquals(defaultSettings.inputBufferLimit(), sampleBrowserPage.getSettings().inputBufferLimit());
        assertEquals(defaultSettings.inputBufferTimeout(), sampleBrowserPage.getSettings().inputBufferTimeout());
        assertEquals(defaultSettings.outputBufferLimit(), sampleBrowserPage.getSettings().outputBufferLimit());
        assertEquals(defaultSettings.outputBufferTimeout(), sampleBrowserPage.getSettings().outputBufferTimeout());
        
        assertNotEquals(defaultSettings.onPayloadLoss(), sampleBrowserPage.getSettings().onPayloadLoss());
        assertNotEquals(defaultSettings.onPayloadLoss().serverSideAction(), sampleBrowserPage.getSettings().onPayloadLoss().serverSideAction());
        
        assertEquals(defaultSettings.onPayloadLoss().javaScript(), sampleBrowserPage.getSettings().onPayloadLoss().javaScript());
    }

    @Test
    public void testHoldPushUnholdPushAndIsHoldPush() {
        final BrowserPage browserPage = new BrowserPage() {
            @Override
            public String webSocketUrl() {
                return "wss://webfirmframework.com/ws-con";
            }

            @Override
            public AbstractHtml render() {
                Html rootTag = new Html(null).give(html -> {
                    new Head(html);
                    new Body(html);
                });
                rootTag.setPrependDocType(true);
                return rootTag;
            }
        };
        try {
            browserPage.toOutputStream(new ByteArrayOutputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            fail("failed due to IOException");
        }

        assertFalse(browserPage.isHoldPush());
        browserPage.holdPush();
        assertTrue(browserPage.isHoldPush());
        try {
            browserPage.getTagRepository().findBodyTag().appendChild(new Div(null));
        } finally {
            browserPage.unholdPush();
        }
        assertFalse(browserPage.isHoldPush());

        //unholding without holding
        browserPage.unholdPush();

        assertFalse(browserPage.isHoldPush());
        browserPage.holdPush();
        assertTrue(browserPage.isHoldPush());
        try {
            browserPage.getTagRepository().findBodyTag().appendChild(new Div(null));
        } finally {
            browserPage.unholdPush();
        }
        assertFalse(browserPage.isHoldPush());

    }

}
