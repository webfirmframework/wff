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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReferenceArray;

import org.junit.Assert;
import org.junit.Test;

import com.webfirmframework.wffweb.InvalidValueException;

public class ExternalDriveClientTasksWrapperDequeTest {

	private static final String dirName = "a3f9540a-026f-460a-a050-45303920d4f0";

	@Test
	public void testExternalDriveClientTasksWrapperDeque() {
		try {
			final ExternalDriveClientTasksWrapperDeque q = new ExternalDriveClientTasksWrapperDeque(
			        Files.createTempDirectory(this.getClass().getSimpleName()).toString(), dirName, "in");

			final List<String> expectedResult = new ArrayList<>(100);

			for (int i = 0; i < 100; i++) {
				final String first = "first string " + i;
				final String second = "second string " + i;
				final String third = "third string " + i;
				final String fourth = "fourth string " + i;
				final ClientTasksWrapper wrapper = new ClientTasksWrapper(
				        ByteBuffer.wrap(first.getBytes(StandardCharsets.UTF_8)),
				        ByteBuffer.wrap(second.getBytes(StandardCharsets.UTF_8)),
				        ByteBuffer.wrap(third.getBytes(StandardCharsets.UTF_8)),
				        ByteBuffer.wrap(fourth.getBytes(StandardCharsets.UTF_8)));

				q.offerLast(wrapper);
				expectedResult.add(first);
				expectedResult.add(second);
				expectedResult.add(third);
				expectedResult.add(fourth);
			}

			final List<String> actualResult = new ArrayList<>(100);

			ClientTasksWrapper polled = null;
			while ((polled = q.poll()) != null) {
				final AtomicReferenceArray<ByteBuffer> tasks = polled.tasks();
				for (int i = 0; i < tasks.length(); i++) {
					actualResult.add(new String(tasks.get(i).array(), StandardCharsets.UTF_8));
				}
			}

			Assert.assertArrayEquals(expectedResult.toArray(), actualResult.toArray());

			q.deleteBaseDirStructure();
		} catch (final IOException e) {
			e.printStackTrace();
			Assert.fail("testExternalDriveClientTasksWrapperDeque failed due to IOException");
		}
	}

	@Test
	public void testOfferFirst() {
		try {
			final ExternalDriveClientTasksWrapperDeque q = new ExternalDriveClientTasksWrapperDeque(
			        Files.createTempDirectory(this.getClass().getSimpleName()).toString(), dirName, "in");

			final List<String> expectedResult = new ArrayList<>(100);

			for (int i = 0; i < 100; i++) {
				final String first = " first string " + i;
				final String second = " second string " + i;
				final String third = " third string " + i;
				final String fourth = " fourth string " + i;
				final ClientTasksWrapper wrapper = new ClientTasksWrapper(
				        ByteBuffer.wrap(first.getBytes(StandardCharsets.UTF_8)),
				        ByteBuffer.wrap(second.getBytes(StandardCharsets.UTF_8)),
				        ByteBuffer.wrap(third.getBytes(StandardCharsets.UTF_8)),
				        ByteBuffer.wrap(fourth.getBytes(StandardCharsets.UTF_8)));

				q.offerLast(wrapper);
				expectedResult.add(first + second + third + fourth);
			}

			final List<String> actualResult = new ArrayList<>(100);

			final List<ClientTasksWrapper> polledTasks = new ArrayList<>(100);

			ClientTasksWrapper polled = null;
			while ((polled = q.poll()) != null) {
				polledTasks.add(polled);
				final AtomicReferenceArray<ByteBuffer> tasks = polled.tasks();

				String line = "";
				for (int i = 0; i < tasks.length(); i++) {
					line += new String(tasks.get(i).array(), StandardCharsets.UTF_8);
				}
				actualResult.add(line);
			}

			Assert.assertArrayEquals(expectedResult.toArray(), actualResult.toArray());

			Collections.reverse(expectedResult);
			actualResult.clear();

			for (final ClientTasksWrapper clientTasksWrapper : polledTasks) {
				q.addFirst(clientTasksWrapper);
			}

			while ((polled = q.poll()) != null) {
				final AtomicReferenceArray<ByteBuffer> tasks = polled.tasks();
				String line = "";
				for (int i = 0; i < tasks.length(); i++) {
					line += new String(tasks.get(i).array(), StandardCharsets.UTF_8);
				}
				actualResult.add(line);
			}

			Assert.assertArrayEquals(expectedResult.toArray(), actualResult.toArray());

			q.deleteBaseDirStructure();
		} catch (final IOException e) {
			e.printStackTrace();
			Assert.fail("testExternalDriveClientTasksWrapperDeque failed due to IOException");
		}
	}

	@Test(expected = InvalidValueException.class)
	public void testOfferFirstException() {
		ExternalDriveClientTasksWrapperDeque q = null;
		try {
			q = new ExternalDriveClientTasksWrapperDeque(
			        Files.createTempDirectory(this.getClass().getSimpleName()).toString(), dirName, "in");
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		final String first = "first string ";
		final String second = "second string ";
		final ClientTasksWrapper wrapper = new ClientTasksWrapper(
		        ByteBuffer.wrap(first.getBytes(StandardCharsets.UTF_8)),
		        ByteBuffer.wrap(second.getBytes(StandardCharsets.UTF_8)));
		q.offerFirst(wrapper);
	}

	@Test
	public void testClear() {
		try {
			Path tempDirectory = Files.createTempDirectory(this.getClass().getSimpleName());
			final ExternalDriveClientTasksWrapperDeque q = new ExternalDriveClientTasksWrapperDeque(
			        tempDirectory.toString(), dirName, "in");

			final Path pathOfDataFilesDir = Path.of(tempDirectory.toString(), dirName, "in");

			for (int i = 0; i < 100; i++) {
				final String first = "first string " + i;
				final String second = "second string " + i;
				final String third = "third string " + i;
				final String fourth = "fourth string " + i;
				final ClientTasksWrapper wrapper = new ClientTasksWrapper(
				        ByteBuffer.wrap(first.getBytes(StandardCharsets.UTF_8)),
				        ByteBuffer.wrap(second.getBytes(StandardCharsets.UTF_8)),
				        ByteBuffer.wrap(third.getBytes(StandardCharsets.UTF_8)),
				        ByteBuffer.wrap(fourth.getBytes(StandardCharsets.UTF_8)));

				q.offerLast(wrapper);
			}


			long totalFilesInDir = Files.list(pathOfDataFilesDir).count();

			Assert.assertEquals(100, totalFilesInDir);

			Assert.assertEquals(100, q.size());

			q.offerFirst(q.poll());
			q.offerFirst(q.poll());
			q.offerFirst(q.poll());
			q.offerFirst(q.poll());
			q.offerFirst(q.poll());

			Assert.assertEquals(100, q.size());

			Assert.assertFalse(q.isEmpty());

			q.clear();

			totalFilesInDir = Files.list(pathOfDataFilesDir).count();

			Assert.assertEquals(0, totalFilesInDir);
			Assert.assertEquals(q.size(), totalFilesInDir);

			Assert.assertNull(q.poll());

			Assert.assertTrue(q.isEmpty());

			q.deleteBaseDirStructure();
		} catch (final IOException e) {
			e.printStackTrace();
			Assert.fail("testExternalDriveClientTasksWrapperDeque failed due to IOException");
		}
	}
	
    @Test
    public void testPoll() {
        try {
            final ExternalDriveClientTasksWrapperDeque q = new ExternalDriveClientTasksWrapperDeque(
                    Files.createTempDirectory(this.getClass().getSimpleName()).toString(), dirName, "in");

            for (int i = 0; i < 5; i++) {
                final String first = "first string " + i;
                final String second = "second string " + i;
                final String third = "third string " + i;
                final String fourth = "fourth string " + i;
                final ClientTasksWrapper wrapper = new ClientTasksWrapper(
                        ByteBuffer.wrap(first.getBytes(StandardCharsets.UTF_8)),
                        ByteBuffer.wrap(second.getBytes(StandardCharsets.UTF_8)),
                        ByteBuffer.wrap(third.getBytes(StandardCharsets.UTF_8)),
                        ByteBuffer.wrap(fourth.getBytes(StandardCharsets.UTF_8)));

                q.offerLast(wrapper);
            }

            Assert.assertEquals(5, q.size());

            q.offerFirst(q.poll());
            q.offerFirst(q.poll());

            Assert.assertEquals(5, q.size());

            Assert.assertFalse(q.isEmpty());

            ClientTasksWrapper wrapper;
            int i = 0;
            while ((wrapper = q.poll()) != null) {
                String firstString = new String(wrapper.tasks().get(0).array());
                String secondString = new String(wrapper.tasks().get(1).array());
                String thirdString = new String(wrapper.tasks().get(2).array());
                String fourthString = new String(wrapper.tasks().get(3).array());
                Assert.assertEquals("first string " + i, firstString);
                Assert.assertEquals("second string " + i, secondString);
                Assert.assertEquals("third string " + i, thirdString);
                Assert.assertEquals("fourth string " + i, fourthString);
                i++;
            }

            Assert.assertNull(q.pollFirst());

            q.deleteBaseDirStructure();
        } catch (final IOException e) {
            e.printStackTrace();
            Assert.fail("testExternalDriveClientTasksWrapperDeque failed due to IOException");
        }
    }

}
