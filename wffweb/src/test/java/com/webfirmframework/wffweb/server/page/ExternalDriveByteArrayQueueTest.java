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

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.Assert;
import org.junit.Test;

public class ExternalDriveByteArrayQueueTest {
	
	private static final String dirName = "a3f9540a-026f-460a-a050-45303920d4f0";

	@Test
	public void testExternalDriveByteArrayQueue() throws IOException, InterruptedException, ExecutionException {
		// String defaultBaseDir = System.getProperty("java.io.tmpdir");

		try {
			final ExternalDriveByteArrayQueue q = new ExternalDriveByteArrayQueue(
			        Files.createTempDirectory(this.getClass().getSimpleName()).toString(), dirName, "in");

			List<Integer> expectedResult = new ArrayList<>(100);

			List<CompletableFuture<Boolean>> results = new ArrayList<>();
			for (int i = 0; i < 100; i++) {
				final int count = i;
				expectedResult.add(count);
				CompletableFuture<Boolean> supplyAsync = CompletableFuture.supplyAsync(() -> {
					q.offer((String.valueOf(count)).getBytes(StandardCharsets.UTF_8));
					return true;
				});
				results.add(supplyAsync);
			}

			for (CompletableFuture<Boolean> completableFuture : results) {
				completableFuture.get();
			}

			List<Integer> actualResult = new ArrayList<>(100);

			byte[] polled = null;
			while ((polled = q.poll()) != null) {
				actualResult.add(Integer.parseInt(new String(polled, StandardCharsets.UTF_8)));
			}

			Collections.sort(actualResult);

			for (int i = 0; i < 100; i++) {
				q.offer((String.valueOf(i)).getBytes(StandardCharsets.UTF_8));
			}
			assertEquals(100, q.size());
			q.clear();
			assertEquals(0, q.size());
			
			q.deleteBaseDirStructure();

			Assert.assertArrayEquals(expectedResult.toArray(), actualResult.toArray());
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail("testExternalDriveByteArrayQueue failed due to IOException");
		}
	}
	

}
