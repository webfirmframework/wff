/*
 * Copyright 2014-2023 Web Firm Framework
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
package com.webfirmframework.wffweb.util;

import static org.junit.Assert.*;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.webfirmframework.wffweb.PushFailedException;

public class ByteBufferUtilTest {
    
    @Test(expected = PushFailedException.class)
    public void testName() throws Exception {
        
        final String s = "1234567890abc";
        final ByteBuffer inputData = ByteBuffer
                .wrap(s.getBytes(StandardCharsets.UTF_8));

        ByteBufferUtil.slice(inputData, 1, (part, last) -> {
            
            if (last) {
                throw new PushFailedException();
            }
            return !last;
        });
        
    }

    @Test
    public void testSlice() {

        {
            final String s = "1234567890abc";
            final ByteBuffer inputData = ByteBuffer
                    .wrap(s.getBytes(StandardCharsets.UTF_8));

            StringBuilder builder = new StringBuilder();

            final int totalSlices = ByteBufferUtil.slice(inputData, 1, (part, last) -> {
                builder.append(
                        new String(part.array(), StandardCharsets.UTF_8));
                return !last;
            });

            assertEquals(s, builder.toString());
            assertEquals(13, totalSlices);
        }

        {
            final String s = "1234567890abc";
            final ByteBuffer inputData = ByteBuffer
                    .wrap(s.getBytes(StandardCharsets.UTF_8));

            StringBuilder builder = new StringBuilder();

            final int totalSlices = ByteBufferUtil.slice(inputData, 2, (part, last) -> {
                builder.append(
                        new String(part.array(), StandardCharsets.UTF_8));
                return !last;
            });

            assertEquals(s, builder.toString());
            assertEquals(7, totalSlices);
        }

        {
            final String s = "1234567890abc";
            final ByteBuffer inputData = ByteBuffer
                    .wrap(s.getBytes(StandardCharsets.UTF_8));

            StringBuilder builder = new StringBuilder();

            final int totalSlices = ByteBufferUtil.slice(inputData, 3, (part, last) -> {
                builder.append(
                        new String(part.array(), StandardCharsets.UTF_8));
                return !last;
            });

            assertEquals(s, builder.toString());
            assertEquals(5, totalSlices);
        }

        {
            final String s = "1234567890abc";
            final ByteBuffer inputData = ByteBuffer
                    .wrap(s.getBytes(StandardCharsets.UTF_8));

            StringBuilder builder = new StringBuilder();

            final int totalSlices = ByteBufferUtil.slice(inputData, 4, (part, last) -> {
                builder.append(
                        new String(part.array(), StandardCharsets.UTF_8));
                return !last;
            });

            assertEquals(s, builder.toString());
            assertEquals(4, totalSlices);
        }

        {
            final String s = "1234567890abc";
            final ByteBuffer inputData = ByteBuffer
                    .wrap(s.getBytes(StandardCharsets.UTF_8));

            StringBuilder builder = new StringBuilder();

            final int totalSlices = ByteBufferUtil.slice(inputData, 5, (part, last) -> {
                builder.append(
                        new String(part.array(), StandardCharsets.UTF_8));
                return !last;
            });

            assertEquals(s, builder.toString());
            assertEquals(3, totalSlices);
        }
        {
            final String s = "1234567890abc";
            final ByteBuffer inputData = ByteBuffer
                    .wrap(s.getBytes(StandardCharsets.UTF_8));

            StringBuilder builder = new StringBuilder();

            final int totalSlices = ByteBufferUtil.slice(inputData, 6, (part, last) -> {
                builder.append(
                        new String(part.array(), StandardCharsets.UTF_8));
                return !last;
            });

            assertEquals(s, builder.toString());
            assertEquals(3, totalSlices);
        }
        {
            final String s = "1234567890abc";
            final ByteBuffer inputData = ByteBuffer
                    .wrap(s.getBytes(StandardCharsets.UTF_8));

            StringBuilder builder = new StringBuilder();

            final int totalSlices = ByteBufferUtil.slice(inputData, 7, (part, last) -> {
                builder.append(
                        new String(part.array(), StandardCharsets.UTF_8));
                return !last;
            });

            assertEquals(s, builder.toString());
            assertEquals(2, totalSlices);
        }

        {
            final String s = "1234567890abc";
            final ByteBuffer inputData = ByteBuffer
                    .wrap(s.getBytes(StandardCharsets.UTF_8));

            StringBuilder builder = new StringBuilder();

            final int totalSlices = ByteBufferUtil.slice(inputData, 20, (part, last) -> {
                builder.append(
                        new String(part.array(), StandardCharsets.UTF_8));
                return !last;
            });

            assertEquals(s, builder.toString());
            assertEquals(1, totalSlices);
        }

        // fail("Not yet implemented");
    }
    
    @Test
    public void testSliceIfRequired() throws Exception {
        {
            final String s = "1234567890abc";
            final ByteBuffer inputData = ByteBuffer
                    .wrap(s.getBytes(StandardCharsets.UTF_8));
            
            assertEquals(13, inputData.capacity());


            final int totalSlices = ByteBufferUtil.sliceIfRequired(inputData, s.length(), (part, last) -> {
                
                //assertEquals cannot be used here 
                //it has to compare the reference
                assertTrue(inputData == part);
                
                assertEquals(inputData, part);
                assertTrue(last);
                
                return !last;
            });
            
            assertEquals(1, totalSlices);
        }
        
        {
            final String s = "1234567890abc";
            final ByteBuffer inputData = ByteBuffer
                    .wrap(s.getBytes(StandardCharsets.UTF_8));
            
            assertEquals(13, inputData.capacity());


            final int totalSlices = ByteBufferUtil.sliceIfRequired(inputData, s.length() + 1, (part, last) -> {
                
                //assertEquals cannot be used here 
                //it has to compare the reference
                assertTrue(inputData == part);
                
                assertEquals(inputData, part);
                assertTrue(last);
                
                return !last;
            });
            
            assertEquals(1, totalSlices);
        }
        
        {
            final String s = "1234567890abc";
            final ByteBuffer inputData = ByteBuffer
                    .wrap(s.getBytes(StandardCharsets.UTF_8));
            
            assertEquals(13, inputData.capacity());


            final int totalSlices = ByteBufferUtil.sliceIfRequired(inputData, s.length() + 10, (part, last) -> {
                
                //assertEquals cannot be used here 
                //it has to compare the reference
                assertTrue(inputData == part);
                
                assertEquals(inputData, part);
                assertTrue(last);
                
                return !last;
            });
            
            assertEquals(1, totalSlices);
        }
        
        {
            final String s = "1234567890abc";
            final ByteBuffer inputData = ByteBuffer
                    .wrap(s.getBytes(StandardCharsets.UTF_8));
            
            assertEquals(13, inputData.capacity());


            final int totalSlices = ByteBufferUtil.sliceIfRequired(inputData, s.length() - 1, (part, last) -> {
                
                //assertNotEquals cannot be used here 
                //it has to compare the reference
                assertFalse(inputData == part);
                
                return !last;
            });
            
            assertEquals(2, totalSlices);
        }
    }
    
    @Test
    public void testMerge() throws Exception {
        {
            final ByteBuffer webfirmframework = ByteBuffer.wrap("webfirmframework".getBytes(StandardCharsets.UTF_8));
            final ByteBuffer merged = ByteBufferUtil.merge(webfirmframework);
            
            assertEquals("webfirmframework", new String(merged.array(), StandardCharsets.UTF_8));
        }
        {
            final ByteBuffer webfirmframework = ByteBuffer.wrap("webfirmframework".getBytes(StandardCharsets.UTF_8));
            final ByteBuffer wffweb = ByteBuffer.wrap("-wffweb".getBytes(StandardCharsets.UTF_8));
            final ByteBuffer useLatestVersion = ByteBuffer.wrap("-use latest version".getBytes(StandardCharsets.UTF_8));
            final ByteBuffer merged = ByteBufferUtil.merge(webfirmframework, wffweb, useLatestVersion);
            
            assertEquals("webfirmframework-wffweb-use latest version", new String(merged.array(), StandardCharsets.UTF_8));
        }

    }

}
