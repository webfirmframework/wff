/*
 * Copyright 2014-2021 Web Firm Framework
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

import static org.junit.Assert.*;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.junit.Assert;
import org.junit.Test;

import com.webfirmframework.wffweb.NotRenderedException;
import com.webfirmframework.wffweb.NullValueException;
import com.webfirmframework.wffweb.tag.html.AbstractHtml;
import com.webfirmframework.wffweb.tag.html.Body;
import com.webfirmframework.wffweb.tag.html.Html;
import com.webfirmframework.wffweb.tag.html.metainfo.Head;
import com.webfirmframework.wffweb.tag.html.stylesandsemantics.Div;

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
        

}
