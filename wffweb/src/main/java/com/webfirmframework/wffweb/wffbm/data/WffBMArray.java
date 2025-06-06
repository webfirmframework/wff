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
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.webfirmframework.wffweb.InvalidValueException;
import com.webfirmframework.wffweb.WffRuntimeException;
import com.webfirmframework.wffweb.util.WffBinaryMessageUtil;
import com.webfirmframework.wffweb.util.data.NameValue;

/**
 * The java array object representation for JavaScript array. <br>
 * Sample code :- <br>
 *
 * <pre><code>
 * WffBMObject bmObject = new WffBMObject();
 *
 * WffBMArray stringArray = new WffBMArray(BMValueType.STRING);
 * stringArray.add("array value 1");
 * stringArray.add("array value 2");
 *
 * bmObject.put("stringArray", BMValueType.BM_ARRAY, stringArray);
 *
 * WffBMArray numberArray = new WffBMArray(BMValueType.NUMBER);
 * numberArray.add(555);
 * numberArray.add(5);
 * numberArray.add(55);
 *
 * bmObject.put("numberArray", BMValueType.BM_ARRAY, numberArray);
 *
 * // to store bytes in an array use WffBMByteArray
 * WffBMByteArray byteArray = new WffBMByteArray();
 * byteArray.write("こんにちは WFFWEB".getBytes(StandardCharsets.UTF_8));
 *
 * bmObject.put("byteArray", BMValueType.BM_BYTE_ARRAY, byteArray);
 *
 * WffBMArray booleanArray = new WffBMArray(BMValueType.BOOLEAN);
 * booleanArray.add(true);
 * booleanArray.add(false);
 * booleanArray.add(true);
 *
 * bmObject.put("booleanArray", BMValueType.BM_ARRAY, booleanArray);
 *
 * WffBMArray regexArray = new WffBMArray(BMValueType.REG_EXP);
 * regexArray.add("[w]");
 * regexArray.add("[f]");
 * regexArray.add("[f]");
 *
 * bmObject.put("regexArray", BMValueType.BM_ARRAY, regexArray);
 *
 * WffBMArray funcArray = new WffBMArray(BMValueType.FUNCTION);
 * funcArray.add("function(arg) {console.log(arg);}");
 * funcArray.add("function(arg1) {console.log(arg1);}");
 * funcArray.add("function(arg2) {console.log(arg2);}");
 *
 * bmObject.put("funcArray", BMValueType.BM_ARRAY, funcArray);
 *
 * WffBMArray nullArray = new WffBMArray(BMValueType.NULL);
 * nullArray.add(null);
 * nullArray.add(null);
 * nullArray.add(null);
 *
 * bmObject.put("nullArray", BMValueType.BM_ARRAY, nullArray);
 *
 * WffBMArray undefinedArray = new WffBMArray(BMValueType.UNDEFINED);
 * undefinedArray.add(null);
 * undefinedArray.add(null);
 * undefinedArray.add(null);
 *
 * bmObject.put("undefinedArray", BMValueType.BM_ARRAY, undefinedArray);
 *
 * WffBMArray arrayArray = new WffBMArray(BMValueType.BM_ARRAY);
 * arrayArray.add(funcArray);
 * arrayArray.add(funcArray);
 * arrayArray.add(funcArray);
 *
 * bmObject.put("arrayArray", BMValueType.BM_ARRAY, arrayArray);
 *
 * WffBMArray objectArray = new WffBMArray(BMValueType.BM_OBJECT);
 * objectArray.add(bmObject.clone());
 * objectArray.add(bmObject.clone());
 * objectArray.add(bmObject.clone());
 *
 * bmObject.put("objectArray", BMValueType.BM_ARRAY, objectArray);
 * </code></pre>
 *
 * @author WFF
 * @see WffBMByteArray
 * @see WffBMObject
 */
public class WffBMArray extends LinkedList<Object> implements WffBMData {

    @Serial
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
            valueType = initWffBMObject(bmArrayBytes, true);
        } catch (final RuntimeException e) {
            throw new WffRuntimeException("Invalid Wff BM Array bytes", e);
        }
    }

    public WffBMArray(final byte[] bmArrayBytes, final boolean outer) {
        try {
            valueType = initWffBMObject(bmArrayBytes, outer);
        } catch (final RuntimeException e) {
            throw new WffRuntimeException("Invalid Wff BM Array bytes", e);
        }
    }

    /**
     * @param bmArrayBytes
     * @param outer
     * @return
     *
     */
    private BMValueType initWffBMObject(final byte[] bmArrayBytes, final boolean outer) {

        if (bmArrayBytes.length == 0 && !outer) {
            // if the inner WffBMArray is an empty array then the bmArrayBytes
            // will be an empty array. Then we can say its type is null in the
            // perceptive of Java it is BMValueType is null
            // because there is no type for an empty array value in JavaSript
            // for JavaScript null type there is BMValueType.NULL
            return null;
        }

        final List<NameValue> bmObject = WffBinaryMessageUtil.VERSION_1.parse(bmArrayBytes);

        final Iterator<NameValue> iterator = bmObject.iterator();
        if (iterator.hasNext()) {

            if (outer) {
                final NameValue typeNameValue = iterator.next();
                if (typeNameValue.getName()[0] == BMType.ARRAY.getType()) {
                    this.outer = true;
                } else {
                    throw new WffRuntimeException("Not a valid Wff BM Array bytes");
                }
            }

            if (iterator.hasNext()) {
                final NameValue nameValue = iterator.next();
                final byte valueTypeByte = nameValue.getName()[0];
                final BMValueType valueType = BMValueType.getInstanceByType(valueTypeByte);
                final byte[][] values = nameValue.getValues();

                return switch (valueType) {
                case STRING, REG_EXP, FUNCTION -> {
                    for (final byte[] value : values) {
                        this.add(new String(value, StandardCharsets.UTF_8));
                    }
                    yield valueType;
                }
                case NUMBER -> {
                    for (final byte[] value : values) {
                        final double doubleValue = ByteBuffer.wrap(value).getDouble(0);
                        this.add(doubleValue);
                    }
                    yield valueType;
                }
                case UNDEFINED, NULL -> {
                    for (@SuppressWarnings("unused")
                    final byte[] value : values) {
                        this.add(null);
                    }
                    yield valueType;
                }
                case BOOLEAN -> {
                    for (final byte[] value : values) {
                        this.add(value[0] == 1);
                    }
                    yield valueType;
                }
                case BM_OBJECT -> {
                    for (final byte[] value : values) {
                        this.add(new WffBMObject(value, false));
                    }
                    yield valueType;
                }
                case BM_ARRAY -> {
                    for (final byte[] value : values) {
                        this.add(new WffBMArray(value, false));
                    }
                    yield valueType;
                }
                case BM_BYTE_ARRAY -> {
                    for (final byte[] value : values) {
                        this.add(new WffBMByteArray(value, false));
                    }
                    yield valueType;
                }
                case INTERNAL_BYTE -> throw new WffRuntimeException(
                        "BMValueType.BYTE is only for internal use, use WffBMByteArray for row bytes.");
                };
            }
        }

        return null;
    }

    public boolean isOuter() {
        return outer;
    }

    public void setOuter(final boolean outer) {
        this.outer = outer;
    }

    public byte[] build() {
        try {
            return buildBytes(outer);
        } catch (final Exception e) {
            throw new WffRuntimeException("Could not build wff bm array bytes", e);
        }
    }

    /**
     * @param outer
     * @return bytes for this WffBMArray object
     */
    @Override
    public byte[] buildBytes(final boolean outer) {
        final BMValueType valueType = this.valueType;
        final Deque<NameValue> nameValues = new ArrayDeque<>(outer ? 2 : 1);

        if (outer) {
            final NameValue typeNameValue = new NameValue();
            typeNameValue.setName(new byte[] { BMType.ARRAY.getType() });
            nameValues.add(typeNameValue);
        }

        final NameValue nameValue = new NameValue();
        final byte valueTypeByte = valueType.getType();
        nameValue.setName(valueTypeByte);

        nameValues.add(nameValue);

        final byte[][] values = new byte[size()][0];
        nameValue.setValues(values);

        return switch (valueType) {
        case STRING, REG_EXP, FUNCTION -> {
            int count = 0;
            for (final Object eachValue : this) {
                final String value = (String) eachValue;
                values[count] = value.getBytes(StandardCharsets.UTF_8);
                count++;
            }
            yield WffBinaryMessageUtil.VERSION_1.getWffBinaryMessageBytes(nameValues);
        }
        case NUMBER -> {
            int count = 0;
            for (final Object eachValue : this) {
                final Number value = (Number) eachValue;
                values[count] = WffBinaryMessageUtil.getOptimizedBytesFromDouble(value.doubleValue());
                count++;
            }
            yield WffBinaryMessageUtil.VERSION_1.getWffBinaryMessageBytes(nameValues);
        }
        case UNDEFINED, NULL -> {
            for (int i = 0; i < size(); i++) {
                values[i] = new byte[] {};
            }
            yield WffBinaryMessageUtil.VERSION_1.getWffBinaryMessageBytes(nameValues);
        }
        case BOOLEAN -> {
            int count = 0;
            for (final Object eachValue : this) {
                final Boolean value = (Boolean) eachValue;
                final byte[] valueBytes = { (byte) (value.booleanValue() ? 1 : 0) };
                values[count] = valueBytes;
                count++;
            }
            yield WffBinaryMessageUtil.VERSION_1.getWffBinaryMessageBytes(nameValues);
        }
        case BM_OBJECT -> {
            int count = 0;
            for (final Object eachValue : this) {
                final WffBMObject value = (WffBMObject) eachValue;
                values[count] = value.buildBytes(false);
                count++;
            }
            yield WffBinaryMessageUtil.VERSION_1.getWffBinaryMessageBytes(nameValues);
        }
        case BM_ARRAY -> {
            int count = 0;
            for (final Object eachValue : this) {
                final WffBMArray value = (WffBMArray) eachValue;
                values[count] = value.buildBytes(false);
                count++;
            }
            yield WffBinaryMessageUtil.VERSION_1.getWffBinaryMessageBytes(nameValues);
        }
        case BM_BYTE_ARRAY -> {
            int count = 0;
            for (final Object eachValue : this) {
                @SuppressWarnings("resource")
                final WffBMByteArray value = (WffBMByteArray) eachValue;
                values[count] = value.build(false);
                count++;
            }
            yield WffBinaryMessageUtil.VERSION_1.getWffBinaryMessageBytes(nameValues);
        }
        case INTERNAL_BYTE -> throw new WffRuntimeException(
                "BMValueType.BYTE is only for internal use, use WffBMByteArray for row bytes.");
        };
    }

    /**
     * gets value type of this array only if it contains any value otherwise returns
     * <code>null</code>.
     *
     * @return the value type of this array
     * @since 2.1.0
     * @author WFF
     */
    public BMValueType getValueType() {
        return valueType;
    }

    @Override
    public BMType getBMType() {
        return BMType.ARRAY;
    }

    /**
     * @param value the number to add to the array.
     * @return true or false.
     * @since 12.0.3
     */
    public boolean addNumber(final Number value) {
        if (BMValueType.NUMBER.equals(valueType)) {
            if (value instanceof final Float f) {
                return super.add(floatToDouble(f));
            } else {
                return super.add(value);
            }
        } else {
            throw new InvalidValueException("Number value is allowed to add only in BMValueType.NUMBER type array.");
        }
    }

    /**
     * @param index the index
     * @param value the number to add to the array.
     * @since 12.0.3
     */
    public void addNumber(final int index, final Number value) {
        if (BMValueType.NUMBER.equals(valueType)) {
            if (value instanceof final Float f) {
                super.add(index, floatToDouble(f));
            } else {
                super.add(index, value);
            }
        } else {
            throw new InvalidValueException("Number value is allowed to add only in BMValueType.NUMBER type array.");
        }
    }

    /**
     * Inserts the specified number at the beginning of this array.
     *
     * @param value the number to insert.
     * @since 12.0.3
     */
    public void addNumberFirst(final Number value) {
        if (BMValueType.NUMBER.equals(valueType)) {
            if (value instanceof final Float f) {
                super.addFirst(floatToDouble(f));
            } else {
                super.addFirst(value);
            }
        } else {
            throw new InvalidValueException("Number value is allowed to add only in BMValueType.NUMBER type array.");
        }
    }

    /**
     * Appends the specified number to the end of this array.
     *
     * @param value the number to insert.
     * @since 12.0.3
     */
    public void addNumberLast(final Number value) {
        if (BMValueType.NUMBER.equals(valueType)) {
            if (value instanceof final Float f) {
                super.addLast(floatToDouble(f));
            } else {
                super.addLast(value);
            }
        } else {
            throw new InvalidValueException("Number value is allowed to add only in BMValueType.NUMBER type array.");
        }
    }

    /**
     * @param value converts the float value to double without loosing precision.
     * @return the double value.
     * @since 12.0.3
     */
    protected static double floatToDouble(final Float value) {
        return new BigDecimal(Float.toString(value)).doubleValue();
    }

    /**
     * @param index the index to get the value as string.
     * @return the value as String.
     * @since 12.0.3
     */
    public String getValueAsString(final int index) {
        final Object value = get(index);
        if (value != null) {
            return new ValueValueType("", valueType, value).valueAsString();
        }
        return null;
    }

    /**
     * @param index the index to get the value as BigDecimal.
     * @return the value as BigDecimal.
     * @since 12.0.3
     */
    public BigDecimal getValueAsBigDecimal(final int index) {
        final Object value = get(index);
        if (value != null) {
            return new ValueValueType("", valueType, value).valueAsBigDecimal();
        }
        return null;
    }

    /**
     * @param index the index to get the value as BigInteger.
     * @return the value as BigInteger.
     * @since 12.0.3
     */
    public BigInteger getValueAsBigInteger(final int index) {
        final Object value = get(index);
        if (value != null) {
            return new ValueValueType("", valueType, value).valueAsBigInteger();
        }
        return null;
    }

    /**
     * @param index the index to get the value as Integer.
     * @return the value as Integer.
     * @since 12.0.3
     */
    public Integer getValueAsInteger(final int index) {
        final Object value = get(index);
        if (value != null) {
            return new ValueValueType("", valueType, value).valueAsInteger();
        }
        return null;
    }

    /**
     * @param index the index to get the value as Long.
     * @return the value as Long.
     * @since 12.0.3
     */
    public Long getValueAsLong(final int index) {
        final Object value = get(index);
        if (value != null) {
            return new ValueValueType("", valueType, value).valueAsLong();
        }
        return null;
    }

    /**
     * @param index the index to get the value as Double.
     * @return the value as Double.
     * @since 12.0.3
     */
    public Double getValueAsDouble(final int index) {
        final Object value = get(index);
        if (value != null) {
            return new ValueValueType("", valueType, value).valueAsDouble();
        }
        return null;
    }

    /**
     * @param index the index to get the value as Float.
     * @return the value as Float.
     * @since 12.0.3
     */
    public Float getValueAsFloat(final int index) {
        final Object value = get(index);
        if (value != null) {
            return new ValueValueType("", valueType, value).valueAsFloat();
        }
        return null;
    }

    /**
     * @param index the index to get the value as Boolean.
     * @return the value as Boolean.
     * @since 12.0.3
     */
    public Boolean getValueAsBoolean(final int index) {
        final Object value = get(index);
        if (value != null) {
            final Boolean valueAsBoolean = new ValueValueType("", valueType, value).valueAsBoolean();
            if (valueAsBoolean == null) {
                throw new InvalidValueException(
                        "Unable to parse boolean from value %s having valueType %s".formatted(value, valueType.name()));
            }
            return valueAsBoolean;
        }
        return null;
    }

    /**
     * @param index the index to get the value as WffBMObject.
     * @return the value as Boolean.
     * @since 12.0.4
     */
    public WffBMObject getValueAsWffBMObject(final int index) {
        final Object value = get(index);
        if (value != null) {
            return (WffBMObject) value;
        }
        return null;
    }

    /**
     * @param index the index to get the value as WffBMArray.
     * @return the value as Boolean.
     * @since 12.0.4
     */
    public WffBMArray getValueAsWffBMArray(final int index) {
        final Object value = get(index);
        if (value != null) {
            return (WffBMArray) value;
        }
        return null;
    }

    /**
     * @param index the index to get the value as WffBMNumberArray.
     * @return the value as WffBMNumberArray.
     * @since 12.0.4
     */
    public WffBMNumberArray<?> getValueAsWffBMNumberArray(final int index) {
        final Object value = get(index);
        if (value != null) {
            return (WffBMNumberArray<?>) value;
        }
        return null;
    }

    /**
     * @param index the index to get the value as WffBMByteArray.
     * @return the value as WffBMByteArray.
     * @since 12.0.4
     */
    public WffBMByteArray getValueAsWffBMByteArray(final int index) {
        final Object value = get(index);
        if (value != null) {
            return (WffBMByteArray) value;
        }
        return null;
    }

    /**
     * @param other the other object for similarity checking.
     * @return true if the other object also contains the same data otherwise false.
     * @since 12.0.3
     */
    public boolean similar(final WffBMArray other) {
        return Arrays.equals(buildBytes(true), other.buildBytes(true));
    }
}
