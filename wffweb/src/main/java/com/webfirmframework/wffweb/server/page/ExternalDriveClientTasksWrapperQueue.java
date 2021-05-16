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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.webfirmframework.wffweb.MethodNotImplementedException;
import com.webfirmframework.wffweb.util.WffBinaryMessageUtil;
import com.webfirmframework.wffweb.util.data.NameValue;

/**
 * @author WFF
 * @since 3.0.18
 *
 */
class ExternalDriveClientTasksWrapperQueue implements Queue<ClientTasksWrapper> {

	private static final Logger LOGGER = Logger.getLogger(ExternalDriveClientTasksWrapperQueue.class.getName());

	private static final String fileNamePrefix = "";

	private static final String fileNameSuffix = ".wff.data";

	private final AtomicLong readId = new AtomicLong();

	private final AtomicLong writeId = new AtomicLong();

	private final String basePath;

	private final String dirName;

	private final String subDirName;

	ExternalDriveClientTasksWrapperQueue(final String path, final String dirName, final String subDirName)
	        throws IOException {
		basePath = path;
		this.dirName = dirName;
		this.subDirName = subDirName;
		init();
	}

	private void init() throws IOException {
		final Path dirPath = Paths.get(basePath, dirName, subDirName);

		if (Files.notExists(dirPath)) {
			Files.createDirectories(dirPath);
		}

	}

	void deleteDir() {
		final Path dirPath = Paths.get(basePath, dirName);
		if (Files.exists(dirPath)) {
			try {
				final Deque<Path> q = new ArrayDeque<>(Files.list(dirPath).collect(Collectors.toList()));
				Path each = null;
				while ((each = q.poll()) != null) {
					if (Files.isDirectory(each)) {
						final List<Path> paths = Files.list(each).collect(Collectors.toList());
						if (paths.size() > 0) {
							for (final Path path : paths) {
								q.addFirst(path);
							}
							q.addLast(each);
						} else {
							Files.deleteIfExists(each);
						}

					} else if (Files.isRegularFile(each)) {
						Files.deleteIfExists(each);
					}
				}
				Files.deleteIfExists(dirPath);
			} catch (final IOException e) {
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
				// NOP
			}
		}
	}

	/**
	 * NB: this method should be used by only one thread at a time.
	 */
	@Override
	public ClientTasksWrapper poll() {
		if (readId.get() < writeId.get()) {
			final long id = readId.incrementAndGet();
			final Path filePath = Paths.get(basePath, dirName, subDirName, fileNamePrefix + id + fileNameSuffix);
			if (Files.exists(filePath)) {

				try {

					final List<NameValue> nameValues = WffBinaryMessageUtil.VERSION_1
					        .parse(Files.readAllBytes(filePath));
					final NameValue nameValue = nameValues.get(0);

					final byte[][] byteArrayTasks = nameValue.getValues();

					final ByteBuffer[] byteBufferTasks = new ByteBuffer[byteArrayTasks.length];
					for (int i = 0; i < byteArrayTasks.length; i++) {
						byteBufferTasks[i] = ByteBuffer.wrap(byteArrayTasks[i]);
					}

					Files.deleteIfExists(filePath);

					return new ClientTasksWrapper(id, byteBufferTasks);
				} catch (final IOException e) {
					// NOP
					LOGGER.log(Level.SEVERE, e.getMessage(), e);
				}
			}
		}

		return null;
	}

	@Override
	public boolean offer(final ClientTasksWrapper tasksWrapper) {
		return offerAt(tasksWrapper, writeId.incrementAndGet());
	}

	@Override
	public boolean add(final ClientTasksWrapper tasksWrapper) {
		return offerAt(tasksWrapper, writeId.incrementAndGet());
	}

	private boolean offerAt(final ClientTasksWrapper tasksWrapper, final long id) {
		final AtomicReferenceArray<ByteBuffer> tasks = tasksWrapper.tasks();

		final int length = tasks.length();

		final byte[][] byteArrayTasks = new byte[length][0];

		for (int i = 0; i < length; i++) {
			byteArrayTasks[i] = tasks.get(i).array();
		}

		final Path filePath = Paths.get(basePath, dirName, subDirName, fileNamePrefix + id + fileNameSuffix);

		final NameValue nameValue = new NameValue();
		nameValue.setName(new byte[0]);
		nameValue.setValues(byteArrayTasks);

		try {
			Files.write(filePath, WffBinaryMessageUtil.VERSION_1.getWffBinaryMessageBytes(nameValue));
			return true;
		} catch (final IOException e) {
			// NOP
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}

		return false;
	}

	@Override
	public boolean isEmpty() {
		return readId.get() == writeId.get();
	}

	@Override
	public int size() {
		return (int) (writeId.get() - readId.get());
	}

	@Override
	public void clear() {
		while ((poll()) != null) {
			// NOP
		}
	}

	@Override
	public boolean addAll(final Collection<? extends ClientTasksWrapper> taskWrappers) {
		throw new MethodNotImplementedException();
	}

	@Override
	public boolean contains(final Object o) {
		throw new MethodNotImplementedException();
	}

	@Override
	public Iterator<ClientTasksWrapper> iterator() {
		throw new MethodNotImplementedException();
	}

	@Override
	public Object[] toArray() {
		throw new MethodNotImplementedException();
	}

	@Override
	public <T> T[] toArray(final T[] a) {
		throw new MethodNotImplementedException();
	}

	@Override
	public boolean remove(final Object o) {
		throw new MethodNotImplementedException();
	}

	@Override
	public boolean containsAll(final Collection<?> c) {
		throw new MethodNotImplementedException();
	}

	@Override
	public boolean removeAll(final Collection<?> c) {
		throw new MethodNotImplementedException();
	}

	@Override
	public boolean retainAll(final Collection<?> c) {
		throw new MethodNotImplementedException();
	}

	@Override
	public ClientTasksWrapper remove() {
		throw new MethodNotImplementedException();
	}

	@Override
	public ClientTasksWrapper element() {
		throw new MethodNotImplementedException();
	}

	@Override
	public ClientTasksWrapper peek() {
		throw new MethodNotImplementedException();
	}

}
