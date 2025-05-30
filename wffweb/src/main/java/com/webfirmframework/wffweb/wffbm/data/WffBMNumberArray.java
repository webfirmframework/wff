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
package com.webfirmframework.wffweb.wffbm.data;

import java.io.Serial;
import java.util.Collection;
import java.util.stream.Stream;

import com.webfirmframework.wffweb.MethodNotImplementedException;
import com.webfirmframework.wffweb.WffRuntimeException;

public class WffBMNumberArray<T extends Number> extends WffBMArray {

    @Serial
    private static final long serialVersionUID = 1L;

    public WffBMNumberArray() {
        super(BMValueType.NUMBER);
    }

    public WffBMNumberArray(final boolean outer) {
        super(BMValueType.NUMBER, outer);
    }

    public WffBMNumberArray(final byte[] bmArrayBytes, final boolean outer) {
        super(bmArrayBytes, outer);
    }

    public WffBMNumberArray(final byte[] bmArrayBytes) {
        super(bmArrayBytes);
    }

    public void add(final int index, final T element) {
        super.add(index, element);
    }

    @Override
    public void add(final int index, final Object element) {
        if (!(element instanceof Number)) {
            throw new WffRuntimeException(
                    "This array supports only Number type values, eg Integer, Double, Byte, Short, Float, Long etc..");
        }
        super.add(index, element);
    }

    public boolean add(final T e) {
        return super.add(e);
    }

    @Override
    public boolean add(final Object e) {
        if (!(e instanceof Number)) {
            throw new WffRuntimeException(
                    "This array supports only Number type values, eg Integer, Double, Byte, Short, Float, Long etc..");
        }
        return super.add(e);
    }

    @Override
    public boolean addAll(final Collection<? extends Object> c) {
        for (final Object object : c) {
            if (!(object instanceof Number)) {
                throw new WffRuntimeException(
                        "This array supports only Number type values, eg Integer, Double, Byte, Short, Float, Long etc..");
            }
        }

        return super.addAll(c);
    }

    @Override
    public boolean addAll(final int index, final Collection<? extends Object> c) {
        for (final Object object : c) {
            if (!(object instanceof Number)) {
                throw new WffRuntimeException(
                        "This array supports only Number type values, eg Integer, Double, Byte, Short, Float, Long etc..");
            }
        }
        return super.addAll(index, c);
    }

    public void addFirst(final T e) {
        super.addFirst(e);
    }

    @Override
    public void addFirst(final Object e) {
        if (!(e instanceof Number)) {
            throw new WffRuntimeException(
                    "This array supports only Number type values, eg Integer, Double, Byte, Short, Float, Long etc..");
        }
        super.addFirst(e);
    }

    public void addLast(final T e) {
        super.addLast(e);
    }

    @Override
    public void addLast(final Object e) {
        if (!(e instanceof Number)) {
            throw new WffRuntimeException(
                    "This array supports only Number type values, eg Integer, Double, Byte, Short, Float, Long etc..");
        }
        super.addLast(e);
    }

    /**
     * @param index the index to get the value as Boolean.
     * @return nothing, it always throws exception as converting a number to boolean
     *         doesn't make sense.
     * @throws MethodNotImplementedException converting a number to a boolean
     *                                       doesn't make sense.
     * @since 12.0.3
     */
    @Override
    public Boolean getValueAsBoolean(final int index) throws MethodNotImplementedException {
        throw new MethodNotImplementedException("Cannot convert number to a boolean value.");
    }

    /**
     * @return the Stream object.
     * @since 12.0.3
     */
    @SuppressWarnings("unchecked")
    public Stream<T> toStream() {
        return stream().map(each -> (T) each);
    }

}
