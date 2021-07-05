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
import java.util.List;
import java.util.concurrent.atomic.AtomicReferenceArray;

import org.junit.Assert;
import org.junit.Test;

public class ExternalDriveClientTasksWrapperQueueTest {

	private static final String dirName = "a3f9540a-026f-460a-a050-45303920d4f0";

	@Test
	public void testExternalDriveClientTasksWrapperQueue() {
		try {
			ExternalDriveClientTasksWrapperQueue q = new ExternalDriveClientTasksWrapperQueue(
			        Files.createTempDirectory(this.getClass().getSimpleName()).toString(), dirName, "in");

			List<String> expectedResult = new ArrayList<>(100);

			for (int i = 0; i < 100; i++) {
				String first = "first string " + i;
				String second = "second string " + i;
				String third = "third string " + i;
				String fourth = "fourth string " + i;
				ClientTasksWrapper wrapper = new ClientTasksWrapper(
				        ByteBuffer.wrap(first.getBytes(StandardCharsets.UTF_8)),
				        ByteBuffer.wrap(second.getBytes(StandardCharsets.UTF_8)),
				        ByteBuffer.wrap(third.getBytes(StandardCharsets.UTF_8)),
				        ByteBuffer.wrap(fourth.getBytes(StandardCharsets.UTF_8)));

				q.offer(wrapper);
				expectedResult.add(first);
				expectedResult.add(second);
				expectedResult.add(third);
				expectedResult.add(fourth);
			}

			List<String> actualResult = new ArrayList<>(100);

			ClientTasksWrapper polled = null;
			while ((polled = q.poll()) != null) {
				AtomicReferenceArray<ByteBuffer> tasks = polled.tasks();
				for (int i = 0; i < tasks.length(); i++) {
					actualResult.add(new String(tasks.get(i).array(), StandardCharsets.UTF_8));
				}
			}

			Assert.assertArrayEquals(expectedResult.toArray(), actualResult.toArray());

			q.deleteBaseDirStructure();
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail("ExternalDriveClientTasksWrapperQueue failed due to IOException");
		}
	}

}
