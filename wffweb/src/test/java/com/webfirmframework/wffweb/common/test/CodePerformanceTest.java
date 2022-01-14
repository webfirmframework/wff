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
package com.webfirmframework.wffweb.common.test;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

public class CodePerformanceTest {

    private static final String[] ARRAY = new String[10000];
    static {
        for (int i = 0; i < ARRAY.length; i++) {
            ARRAY[i] = "value" + i;
        }
    }

    @Test
    public void testPerformanceOfArrayToListJava7Syntax() {
        @SuppressWarnings("unused")
        final List<String> asList = Arrays.asList(ARRAY);
    }

    @Test
    public void testPerformanceOfArrayToListJava8Syntax() {
        @SuppressWarnings("unused")
        final List<String> asList = Stream.of(ARRAY)
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unused")
    @Test
    public void testPerformanceDifferenceOfArrayToListJava7And8Syntax() {

        long before = System.nanoTime();
        final List<String> asList1 = Arrays.asList(ARRAY);
        long after = System.nanoTime();

        long first = after - before;

        before = System.nanoTime();
        final List<String> asList2 = Stream.of(ARRAY)
                .collect(Collectors.toList());
        after = System.nanoTime();

        long second = after - before;

        System.out.println("time taken for asList: " + first + " ns");
        System.out
                .println("time taken for Collectors.toList: " + second + " ns");

        if (first == second) {
            System.out.println(
                    "performance of java 7 Arrays.asList == java8 stream's Collectors.toList, gain: 0 ns");
        } else if (first < second) {
            System.out.println(
                    "performance of java 7 Arrays.asList > java8 stream's Collectors.toList, gain: "
                            + (second - first) + " ns");
        } else if (second < first) {
            System.out.println(
                    "performance of java 7 Arrays.asList < java8 stream's Collectors.toList, gain: "
                            + (first - second) + " ns");
        }

    }

    @SuppressWarnings("unused")
    @Test
    public void testPerformanceOfFindFirst() {

        Set<String> children = Collections
                .synchronizedSet(new LinkedHashSet<String>());
        for (String each : ARRAY) {
            children.add(each);
        }

        long before = System.nanoTime();
        final String firstBySteam = children.stream().findFirst().orElse(null);
        long after = System.nanoTime();

        long first = after - before;

        before = System.nanoTime();
        String firstByLoop = null;

        if (children.size() > 0) {
            for (String string : children) {
                firstByLoop = string;
                break;
            }
        }
        after = System.nanoTime();

        long second = after - before;

        before = System.nanoTime();
        final Iterator<String> iterator = children.iterator();
        iterator.hasNext();

        String firstByIterator = iterator.hasNext() ? iterator.next() : null;
        after = System.nanoTime();

        long third = after - before;

        System.out.println("time taken for firstBySteam: " + first + " ns");
        System.out.println("time taken for firstByLoop: " + second + " ns");
        System.out.println("time taken for firstByIterator: " + third + " ns");

    }
    
    @SuppressWarnings("unused")
    @Test
    public void testPerformanceOfLinkedListAndArrayDeque() throws Exception {
        final long difference1;
        final long difference2;
        {
            long before = System.nanoTime();
            final Collection<String> list = new LinkedList<>();
            for (int i = 0; i < 100000; i++) {
                list.add("item" + i);
            }
            for (String each : list) {
                
            }
            list.toArray(new String[list.size()]);
            long after = System.nanoTime();
            difference1 = after - before;
            System.out.println("performance of LinkedList: " + difference1);
        }
        {
            long before = System.nanoTime();
            final Collection<String> list = new ArrayDeque<>();
            for (int i = 0; i < 100000; i++) {
                list.add("item" + i);
            }
            for (String each : list) {
                
            }
            list.toArray(new String[list.size()]);
            long after = System.nanoTime();
            difference2 = after - before;
            System.out.println("performance of ArrayDeque: " + (after - before));
        }
        System.out.println("Additional ns taken by LinkedList than ArrayDeque: " + (difference1 - difference2));
    }
    
    @SuppressWarnings("unused")
    @Test
    public void testPerformanceOfSmallByteArrayCopy() throws Exception {
        {
            final byte[] indexBytes = {1, 2, 3, 4};
            
            byte[] copied = new byte[indexBytes.length];
            
            long before = System.nanoTime();
            for (int i = 0; i < 10000; i++) {
                System.arraycopy(indexBytes, 0, copied, 0, indexBytes.length);    
            }       
            
            long after = System.nanoTime();
            
            System.out.println("time taken for System.arraycopy " + (after - before) + "ns");
            
            before = System.nanoTime();
            for (int i = 0; i < 10000; i++) {
                copied[0] = indexBytes[0];    
            }  
            after = System.nanoTime();
            
            System.out.println("time taken for init array copy  " + (after - before) + "ns");
        }
        System.out.println("-------------");
        {
            final byte[] indexBytes = {1, 2, 3, 4};
            
            byte[] copied = new byte[indexBytes.length];
            long before ;
            long after ;
            
            before = System.nanoTime();
            for (int i = 0; i < 10000; i++) {
                copied[0] = indexBytes[0];    
                copied[1] = indexBytes[1];    
                copied[2] = indexBytes[2];    
                copied[3] = indexBytes[3];    
            }  
            after = System.nanoTime();
            
            System.out.println("time taken for init array copy  " + (after - before) + "ns");
            
            before = System.nanoTime();
            for (int i = 0; i < 10000; i++) {
                System.arraycopy(indexBytes, 0, copied, 0, indexBytes.length);    
            }       
            
            after = System.nanoTime();
            
            System.out.println("time taken for System.arraycopy " + (after - before) + "ns");
            
            
        }
        System.out.println("-------------");
        {
            final byte[] indexBytes = {1, 2, 3, 4};
            
           
            long before ;
            long after ;
            
            before = System.nanoTime();
            for (int i = 0; i < 10000; i++) {
                byte[] copied = {indexBytes[0], indexBytes[1], indexBytes[2], indexBytes[3]};    
            }  
            after = System.nanoTime();
            
            System.out.println("time taken for declare init array copy " + (after - before) + "ns");
            
            before = System.nanoTime();
            
            for (int i = 0; i < 10000; i++) {
                byte[] copied = new byte[indexBytes.length];
                System.arraycopy(indexBytes, 0, copied, 0, indexBytes.length);    
            }       
            
            after = System.nanoTime();
            
            System.out.println("time taken for System.arraycopy        " + (after - before) + "ns");
            
            
        }
    }

}
