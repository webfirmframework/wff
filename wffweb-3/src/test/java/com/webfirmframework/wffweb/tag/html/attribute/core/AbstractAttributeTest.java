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
package com.webfirmframework.wffweb.tag.html.attribute.core;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import org.junit.Test;

import com.webfirmframework.wffweb.server.page.BrowserPage;
import com.webfirmframework.wffweb.tag.html.AbstractHtml;
import com.webfirmframework.wffweb.tag.html.Body;
import com.webfirmframework.wffweb.tag.html.Html;
import com.webfirmframework.wffweb.tag.html.attribute.Name;
import com.webfirmframework.wffweb.tag.html.attribute.Src;
import com.webfirmframework.wffweb.tag.html.attribute.Value;
import com.webfirmframework.wffweb.tag.html.attribute.global.ClassAttribute;
import com.webfirmframework.wffweb.tag.html.attribute.global.Id;
import com.webfirmframework.wffweb.tag.html.attribute.global.Style;
import com.webfirmframework.wffweb.tag.html.formsandinputs.Input;
import com.webfirmframework.wffweb.tag.html.links.Link;
import com.webfirmframework.wffweb.tag.html.metainfo.Head;
import com.webfirmframework.wffweb.tag.html.stylesandsemantics.Div;
import com.webfirmframework.wffweb.tag.repository.TagRepository;

public class AbstractAttributeTest {

    @SuppressWarnings("serial")
    @Test
    public void testTestOwnerTag() {

        final Name name = new Name("somename");

        Html html = new Html(null, name) {
            {
                Div div1 = new Div(this, name);
                Div div2 = new Div(this, name);
                assertTrue(Arrays.asList(name.getOwnerTags()).contains(div1));
                assertTrue(Arrays.asList(name.getOwnerTags()).contains(div2));
            }
        };
        assertTrue(Arrays.asList(name.getOwnerTags()).contains(html));

    }

    @Test
    public void testGetWffPrintStructure() {

        ClassAttribute classAttribute = new ClassAttribute(
                "cl1 cl2 cl3 cl-col-4");

        System.out.println(classAttribute.getWffPrintStructure());
        assertEquals("class=cl1 cl2 cl3 cl-col-4",
                classAttribute.getWffPrintStructure());

        Style style = new Style("color:green;background:blue");
        assertEquals("style=color:green;background:blue;",
                style.getWffPrintStructure());

        Name name = new Name("somename");
        assertEquals("name=somename", name.getWffPrintStructure());
    }

    @Test
    public void testToWffString() {

        ClassAttribute classAttribute = new ClassAttribute(
                "cl1 cl2 cl3 cl-col-4");

        System.out.println(classAttribute.getWffPrintStructure());
        assertEquals("class=cl1 cl2 cl3 cl-col-4",
                classAttribute.toWffString());

        Style style = new Style("color:green;background:blue");
        assertEquals("style=color:green;background:blue;", style.toWffString());

        Name name = new Name("somename");
        assertEquals("name=somename", name.toWffString());
    }
    
    @Test
    public void testThreadSafety() throws InterruptedException, ExecutionException {
        
        AtomicInteger atomicInt = new AtomicInteger();
        
        Value value = new Value("initialVal");

        Supplier<Input> task1 = () -> {
            
            Input input = new Input(null);
            BrowserPage browserPage1 = new BrowserPage() {
                
                @Override
                public String webSocketUrl() {
                    return "ws://localhost/indexws";
                }
                
                @Override
                public AbstractHtml render() {
                   
                    Html rootTag = new Html(null).give(html -> {
                        new Head(html).give(head -> {
                            new Link(head, new Id("appbasicCssLink"), new Src("https://localhost/appbasic.css"));
                        });
                        new Body(html).give(body -> {
                           body.appendChild(input);                          
                        });
                    });
                    return rootTag;
                }
            };
            
            input.addAttributes(value);
            
            return input;
        };
        Supplier<Value> task2 = () -> {
            value.setAttributeValue("some" + atomicInt.incrementAndGet());
            return value;
        };
        
        List<CompletableFuture> results = new ArrayList<>();
        
        for (int i = 0; i < 100; i++) {           
            
            CompletableFuture<Input> result1 = CompletableFuture.supplyAsync(task1);
            CompletableFuture<Value> result2 = CompletableFuture.supplyAsync(task2);
            results.add(result1);
            results.add(result2);
        }
        
        for (CompletableFuture result : results) {
            result.get();            
        }
        
        
        
    }
    
    @Test
    public void testThreadSafetyScenario2() throws InterruptedException, ExecutionException {
        
        AtomicInteger atomicInt = new AtomicInteger();
        
        Value value = new Value("initialVal");
        
       
        
        BrowserPage browserPage1 = new BrowserPage() {
            
            @Override
            public String webSocketUrl() {
                return "ws://localhost/indexws";
            }
            
            @Override
            public AbstractHtml render() {
               
                Html rootTag = new Html(null).give(html -> {
                    new Head(html).give(head -> {
                        new Link(head, new Id("appbasicCssLink"), new Src("https://localhost/appbasic.css"));
                    });
                    new Body(html);
                });
                return rootTag;
            }
        };
        
        browserPage1.toBigHtmlString();
        
        Body body = browserPage1.getTagRepository().findBodyTag();

        Supplier<Input> task1 = () -> {
            
            Input input = new Input(null);
            body.appendChild(input); 
            
            input.addAttributes(value);
            
            return input;
        };
        Supplier<Value> task2 = () -> {
            value.setAttributeValue("some" + atomicInt.incrementAndGet());
            return value;
        };
        
        List<CompletableFuture> results = new ArrayList<>();
        
        for (int i = 0; i < 100; i++) {           
            
            CompletableFuture<Input> result1 = CompletableFuture.supplyAsync(task1);
            CompletableFuture<Value> result2 = CompletableFuture.supplyAsync(task2);
            results.add(result1);
            results.add(result2);
        }
        
        for (CompletableFuture result : results) {
            result.get();            
        }
        
        
        
    }
    
    @Test
    public void testThreadSafetyScenario3() throws InterruptedException, ExecutionException {
        
        
        ExecutorService threadPool = Executors.newCachedThreadPool();
        
        AtomicInteger atomicInt = new AtomicInteger();
        
        Value value = new Value("initialVal");
        
       
        
        BrowserPage browserPage1 = new BrowserPage() {
            
            @Override
            public String webSocketUrl() {
                return "ws://localhost/indexws";
            }
            
            @Override
            public AbstractHtml render() {
               
                Html rootTag = new Html(null).give(html -> {
                    new Head(html).give(head -> {
                        new Link(head, new Id("appbasicCssLink"), new Src("https://localhost/appbasic.css"));
                    });
                    new Body(html);
                });
                return rootTag;
            }
        };
        
        browserPage1.toBigHtmlString();
        
        Body body = browserPage1.getTagRepository().findBodyTag();

        Supplier<Input> task1 = () -> {
            
            Input input = new Input(null, value);
            
            
            return input;
        };
        Supplier<Value> task2 = () -> {
            value.setAttributeValue("some" + atomicInt.incrementAndGet());
            return value;
        };
        
        List<CompletableFuture> results = new ArrayList<>();
        
        for (int i = 0; i < 100; i++) {           
            
            CompletableFuture<Input> result1 = CompletableFuture.supplyAsync(task1, threadPool);
            CompletableFuture<Value> result2 = CompletableFuture.supplyAsync(task2, threadPool);
            results.add(result1);
            results.add(result2);
        }
        
        for (CompletableFuture result : results) {
            result.get();            
        }
        
        
        threadPool.shutdown();
    }
    
    @Test
    public void testThreadSafetyScenario4() throws InterruptedException, ExecutionException {
        
        ExecutorService threadPool = Executors.newCachedThreadPool();
        
        AtomicInteger atomicInt = new AtomicInteger();
        
        Value value = new Value("initialVal");
        
       
        
        BrowserPage browserPage1 = new BrowserPage() {
            
            @Override
            public String webSocketUrl() {
                return "ws://localhost/indexws";
            }
            
            @Override
            public AbstractHtml render() {
               
                Html rootTag = new Html(null).give(html -> {
                    new Head(html).give(head -> {
                        new Link(head, new Id("appbasicCssLink"), new Src("https://localhost/appbasic.css"));
                    });
                    new Body(html);
                });
                return rootTag;
            }
        };
        
        browserPage1.toBigHtmlString();
        
        Body body = browserPage1.getTagRepository().findBodyTag();

        Supplier<Input> task1 = () -> {
            
            Input input = new Input(null, value);
            body.appendChild(input); 
            
            return input;
        };
        Supplier<Value> task2 = () -> {
            value.setAttributeValue("some" + atomicInt.incrementAndGet());
            return value;
        };
        
        List<CompletableFuture> results = new ArrayList<>();
        
        for (int i = 0; i < 100; i++) {           
            
            CompletableFuture<Input> result1 = CompletableFuture.supplyAsync(task1, threadPool);
            CompletableFuture<Value> result2 = CompletableFuture.supplyAsync(task2, threadPool);
            results.add(result1);
            results.add(result2);
        }
        
        for (CompletableFuture result : results) {
            result.get();            
        }
        
        threadPool.shutdown();
        
    }

}
