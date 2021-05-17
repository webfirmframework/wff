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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.webfirmframework.wffweb.MethodNotImplementedException;

/**
 * @author WFF
 * @since 3.0.18
 *
 */
class ExternalDriveByteArrayQueue implements Queue<byte[]> {

	private static final Logger LOGGER = Logger.getLogger(ExternalDriveByteArrayQueue.class.getName());

	private static final String fileNamePrefix = "";

	private static final String fileNameSuffix = ".wff.data";

	private final AtomicLong readId = new AtomicLong();

	private final AtomicLong writeId = new AtomicLong();

	private final String basePath;

	private final String dirName;

	private final String subDirName;

	ExternalDriveByteArrayQueue(final String path, final String dirName, final String subDirName) throws IOException {
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
				final Deque<Path> q = Files.list(dirPath).collect(Collectors.toCollection(ArrayDeque::new));
				Path each;
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

	@Override
	public byte[] poll() {
		long rId;
		while ((rId = readId.get()) < writeId.get()) {
			final long newReadId = rId + 1L;
			if (readId.compareAndSet(rId, newReadId)) {
				final Path filePath = Paths.get(basePath, dirName, subDirName,
				        fileNamePrefix + newReadId + fileNameSuffix);
				if (Files.exists(filePath)) {
					try {
						final byte[] bytes = Files.readAllBytes(filePath);
						Files.deleteIfExists(filePath);
						return bytes;
					} catch (final IOException e) {
						// NOP
						LOGGER.log(Level.SEVERE, e.getMessage(), e);
					}
				}
			}
		}

		return null;
	}

	@Override
	public boolean offer(final byte[] bytes) {
		try {
			final Path filePath = Paths.get(basePath, dirName, subDirName,
			        fileNamePrefix + writeId.incrementAndGet() + fileNameSuffix);
			Files.write(filePath, bytes);
			return true;
		} catch (final IOException e) {
			// NOP
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		return false;
	}

	@Override
	public boolean add(final byte[] bytes) {
		return offer(bytes);
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
//NOP
		}
	}

	@Override
	public boolean contains(final Object o) {
		throw new MethodNotImplementedException();
	}

	@Override
	public Iterator<byte[]> iterator() {
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
	public boolean addAll(final Collection<? extends byte[]> c) {
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
	public byte[] remove() {
		throw new MethodNotImplementedException();
	}

	@Override
	public byte[] element() {
		throw new MethodNotImplementedException();
	}

	@Override
	public byte[] peek() {
		throw new MethodNotImplementedException();
	}

}
