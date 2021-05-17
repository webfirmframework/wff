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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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

			q.deleteDir();
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

			q.deleteDir();
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
			final ExternalDriveClientTasksWrapperDeque q = new ExternalDriveClientTasksWrapperDeque(
			        Files.createTempDirectory(this.getClass().getSimpleName()).toString(), dirName, "in");

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

			Assert.assertEquals(100, q.size());

			q.offerFirst(q.poll());
			q.offerFirst(q.poll());
			q.offerFirst(q.poll());
			q.offerFirst(q.poll());
			q.offerFirst(q.poll());

			Assert.assertEquals(100, q.size());

			Assert.assertFalse(q.isEmpty());

			q.clear();

			Assert.assertNull(q.poll());

			Assert.assertTrue(q.isEmpty());

			q.deleteDir();
		} catch (final IOException e) {
			e.printStackTrace();
			Assert.fail("testExternalDriveClientTasksWrapperDeque failed due to IOException");
		}
	}

}
