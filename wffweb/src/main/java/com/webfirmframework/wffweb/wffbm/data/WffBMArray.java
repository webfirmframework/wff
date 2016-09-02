/*
 * Copyright 2014-2016 Web Firm Framework
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

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.webfirmframework.wffweb.WffRuntimeException;
import com.webfirmframework.wffweb.util.WffBinaryMessageUtil;
import com.webfirmframework.wffweb.util.data.NameValue;

public class WffBMArray extends LinkedList<Object> implements WffData {

    private static final long serialVersionUID = 1L;

    private boolean outer;

    private BMValueType valueType;

    public WffBMArray(final BMValueType valueType) {
        this.valueType = valueType;
    }

    public WffBMArray(final BMValueType valueType, final boolean outer) {
        this.valueType = valueType;
        this.outer = outer;
    }

    public WffBMArray(final byte[] bmArrayBytes) {
        try {
            initWffBMObject(bmArrayBytes, true);
        } catch (final UnsupportedEncodingException e) {
            throw new WffRuntimeException("Invalid Wff BM Array bytes", e);
        }
    }

    public WffBMArray(final byte[] bmArrayBytes, final boolean outer) {
        try {
            initWffBMObject(bmArrayBytes, outer);
        } catch (final UnsupportedEncodingException e) {
            throw new WffRuntimeException("Invalid Wff BM Array bytes", e);
        }
    }

    private void initWffBMObject(final byte[] bmArrayBytes, final boolean outer)
            throws UnsupportedEncodingException {

        final List<NameValue> bmObject = WffBinaryMessageUtil.VERSION_1
                .parse(bmArrayBytes);

        final Iterator<NameValue> iterator = bmObject.iterator();
        if (iterator.hasNext()) {

            if (outer) {
                final NameValue typeNameValue = iterator.next();
                if (typeNameValue.getName()[0] == BMType.OBJECT.getType()) {
                    this.outer = true;
                } else {
                    throw new WffRuntimeException(
                            "Not a valid Wff BM Array bytes");
                }
            }

            if (iterator.hasNext()) {
                final NameValue nameValue = iterator.next();
                final byte valueType = nameValue.getName()[0];
                final byte[][] values = nameValue.getValues();

                if (valueType == BMValueType.STRING.getType()) {

                    for (final byte[] value : values) {
                        this.add(new String(value, "UTF-8"));
                    }
                } else if (valueType == BMValueType.NUMBER.getType()) {

                    for (final byte[] value : values) {
                        final double doubleValue = Double
                                .longBitsToDouble(WffBinaryMessageUtil
                                        .getLongFromOptimizedBytes(value));

                        this.add(doubleValue);
                    }

                } else if (valueType == BMValueType.UNDEFINED.getType()) {

                    for (@SuppressWarnings("unused")
                    final byte[] value : values) {
                        this.add(null);
                    }

                } else if (valueType == BMValueType.NULL.getType()) {

                    for (@SuppressWarnings("unused")
                    final byte[] value : values) {
                        this.add(null);
                    }

                } else if (valueType == BMValueType.BOOLEAN.getType()) {
                    for (final byte[] value : values) {
                        this.add(value[0] == 1);
                    }
                } else if (valueType == BMValueType.OBJECT.getType()) {

                    for (final byte[] value : values) {
                        this.add(new WffBMObject(value));
                    }

                } else if (valueType == BMValueType.ARRAY.getType()) {

                    for (final byte[] value : values) {

                        this.add(new WffBMArray(value, false));
                    }

                } else if (valueType == BMValueType.REG_EXP.getType()) {
                    for (final byte[] value : values) {
                        this.add(new String(value, "UTF-8"));
                    }
                } else if (valueType == BMValueType.FUNCTION.getType()) {

                    for (final byte[] value : values) {
                        this.add(new String(value, "UTF-8"));
                    }
                }

            }
        }

    }

    public boolean isOuter() {
        return outer;
    }

    public void setOuter(final boolean outer) {
        this.outer = outer;
    }

    public byte[] build() {
        try {
            return build(outer);
        } catch (final UnsupportedEncodingException e) {
            throw new WffRuntimeException("Could not build wff bm array bytes",
                    e);
        }
    }

    public byte[] build(final boolean outer)
            throws UnsupportedEncodingException {

        final List<NameValue> nameValues = new LinkedList<NameValue>();

        if (outer) {
            final NameValue typeNameValue = new NameValue();
            typeNameValue.setName(new byte[] { BMType.OBJECT.getType() });
            nameValues.add(typeNameValue);
        }

        final NameValue nameValue = new NameValue();
        final byte valueType = this.valueType.getType();
        nameValue.setName(valueType);

        nameValues.add(nameValue);

        final byte[][] values = new byte[size()][0];
        nameValue.setValues(values);

        if (valueType == BMValueType.STRING.getType()) {

            int count = 0;
            for (final Object eachValue : this) {
                final String value = (String) eachValue;
                values[count] = value.getBytes("UTF-8");
                count++;
            }
        } else if (valueType == BMValueType.NUMBER.getType()) {

            int count = 0;
            for (final Object eachValue : this) {
                final Number value = (Number) eachValue;
                values[count] = WffBinaryMessageUtil
                        .getOptimizedBytesFromDouble(value.doubleValue());
                count++;
            }

        } else if (valueType == BMValueType.UNDEFINED.getType()) {

            for (int i = 0; i < size(); i++) {
                values[i] = new byte[] {};
            }

        } else if (valueType == BMValueType.NULL.getType()) {

            for (int i = 0; i < size(); i++) {
                values[i] = new byte[] {};
            }
        } else if (valueType == BMValueType.BOOLEAN.getType()) {
            int count = 0;
            for (final Object eachValue : this) {
                final Boolean value = (Boolean) eachValue;
                final byte[] valueBytes = {
                        (byte) (value.booleanValue() ? 1 : 0) };
                values[count] = valueBytes;
                count++;
            }
        } else if (valueType == BMValueType.OBJECT.getType()) {
            int count = 0;
            for (final Object eachValue : this) {
                final WffBMObject value = (WffBMObject) eachValue;
                values[count] = value.build(false);
                count++;
            }

        } else if (valueType == BMValueType.ARRAY.getType()) {

            int count = 0;
            for (final Object eachValue : this) {
                final WffBMArray value = (WffBMArray) eachValue;
                values[count] = value.build(false);
                count++;
            }

        } else if (valueType == BMValueType.REG_EXP.getType()) {
            int count = 0;
            for (final Object eachValue : this) {
                final String value = (String) eachValue;
                values[count] = value.getBytes("UTF-8");
                count++;
            }
        } else if (valueType == BMValueType.FUNCTION.getType()) {

            int count = 0;
            for (final Object eachValue : this) {
                final String value = (String) eachValue;
                values[count] = value.getBytes("UTF-8");
                count++;
            }
        }

        return WffBinaryMessageUtil.VERSION_1
                .getWffBinaryMessageBytes(nameValues);
    }
}
