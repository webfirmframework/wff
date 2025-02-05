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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.webfirmframework.wffweb.InvalidValueException;
import com.webfirmframework.wffweb.WffRuntimeException;
import com.webfirmframework.wffweb.util.WffBinaryMessageUtil;
import com.webfirmframework.wffweb.util.data.NameValue;

/**
 * The java object representation for JavaScript object. <br>
 * Sample code :- <br>
 *
 * <pre>
 * WffBMObject bmObject = new WffBMObject();
 * bmObject.put("serverKey", BMValueType.STRING, "value from server");
 * bmObject.put("string", BMValueType.STRING, "sample string");
 * bmObject.put("nul", BMValueType.NULL, null);
 * bmObject.put("number", BMValueType.NUMBER, 555);
 * bmObject.put("undef", BMValueType.UNDEFINED, null);
 * bmObject.put("reg", BMValueType.REG_EXP, "[w]");
 * bmObject.put("bool", BMValueType.BOOLEAN, true);
 * bmObject.put("testFun", BMValueType.FUNCTION, "function(arg) {alert(arg);}");
 * </pre>
 *
 * The {@code WffBMObject} can also hold array and binary data (as byte array).
 * Check out {@code WffBMArray} and {@code WffBMByteArray} respectively.
 *
 * @author WFF
 * @see WffBMArray
 * @see WffBMByteArray
 */
public class WffBMObject extends LinkedHashMap<String, ValueValueType> implements WffBMData {

    @Serial
    private static final long serialVersionUID = 1L;

    private boolean outer;

    private byte[] bMBytes;

    public WffBMObject() {
    }

    public WffBMObject(final boolean outer) {
        this.outer = outer;
    }

    public WffBMObject(final byte[] bMBytes) {
        try {
            initWffBMObject(bMBytes, true);
            this.bMBytes = bMBytes;
        } catch (final RuntimeException e) {
            throw new WffRuntimeException("Could not create wff bm object", e);
        }
    }

    public WffBMObject(final byte[] bMBytes, final boolean outer) {
        try {
            initWffBMObject(bMBytes, outer);
            this.bMBytes = bMBytes;
        } catch (final RuntimeException e) {
            throw new WffRuntimeException("Could not create wff bm object", e);
        }
    }

    @Override
    public ValueValueType put(final String key, final ValueValueType value) {
        // should be == here
        // as the cloned object returns true
        // with its equals method
        if (this == value.getValue()) {
            throw new InvalidValueException("The same instance cannot be passed as value in ValueValueType");
        }
        return super.put(key, value);
    }

    public void put(final String key, final BMValueType valueType, final Object value) {
        // should be == here
        // as the cloned object returns true
        // with its equals method
        if (this == value) {
            throw new InvalidValueException("The same instance cannot be passed as value");
        }
        super.put(key, new ValueValueType(key, valueType.getType(), value));
    }

    /**
     * replacement method for build() method.
     *
     * @return
     * @since 3.0.15
     */
    public byte[] buildBytes() {
        return buildBytes(outer);
    }

    /**
     * @param outer
     * @return bytes for this WffBMObject
     * @author WFF
     * @since 3.0.2
     */
    @Override
    public byte[] buildBytes(final boolean outer) {

        final Set<Entry<String, ValueValueType>> superEntrySet = super.entrySet();

        final int capacity = outer ? superEntrySet.size() + 1 : superEntrySet.size();

        final Deque<NameValue> nameValues = new ArrayDeque<>(capacity);

        if (outer) {
            final NameValue typeNameValue = new NameValue();
            typeNameValue.setName(BMType.OBJECT.getType());
            nameValues.add(typeNameValue);
        }

        for (final Entry<String, ValueValueType> entry : superEntrySet) {
            final String key = entry.getKey();
            final ValueValueType valueValueType = entry.getValue();
            final byte valueType = valueValueType.getValueTypeByte();

            final NameValue nameValue = new NameValue();
            nameValue.setName(key.getBytes(StandardCharsets.UTF_8));

            nameValues.add(nameValue);

            if (valueType == BMValueType.STRING.getType()) {

                final String value = (String) valueValueType.getValue();

                nameValue.setValues(new byte[] { valueType }, value.getBytes(StandardCharsets.UTF_8));

            } else if (valueType == BMValueType.NUMBER.getType()) {

                final Number value = (Number) valueValueType.getValue();
                final byte[] valueBytes = WffBinaryMessageUtil.getOptimizedBytesFromDouble(value.doubleValue());
                nameValue.setValues(new byte[] { valueType }, valueBytes);

            } else if (valueType == BMValueType.UNDEFINED.getType()) {

                final byte[] valueBytes = {};
                nameValue.setValues(new byte[] { valueType }, valueBytes);

            } else if (valueType == BMValueType.NULL.getType()) {
                final byte[] valueBytes = {};
                nameValue.setValues(new byte[] { valueType }, valueBytes);
            } else if (valueType == BMValueType.BOOLEAN.getType()) {

                final Boolean value = (Boolean) valueValueType.getValue();
                final byte[] valueBytes = { (byte) (value.booleanValue() ? 1 : 0) };
                nameValue.setValues(new byte[] { valueType }, valueBytes);
            } else if (valueType == BMValueType.BM_OBJECT.getType()) {

                final WffBMObject value = (WffBMObject) valueValueType.getValue();
                final byte[] valueBytes = value.buildBytes(false);
                nameValue.setValues(new byte[] { valueType }, valueBytes);

            } else if (valueType == BMValueType.BM_ARRAY.getType()) {

                final WffBMArray value = (WffBMArray) valueValueType.getValue();
                final byte[] valueBytes = value.buildBytes(false);
                nameValue.setValues(new byte[] { valueType }, valueBytes);

            } else if (valueType == BMValueType.BM_BYTE_ARRAY.getType()) {

                final WffBMByteArray value = (WffBMByteArray) valueValueType.getValue();
                final byte[] valueBytes = value.build(false);
                nameValue.setValues(new byte[] { valueType }, valueBytes);

            } else if (valueType == BMValueType.REG_EXP.getType()) {
                final String value = (String) valueValueType.getValue();
                nameValue.setValues(new byte[] { valueType }, value.getBytes(StandardCharsets.UTF_8));
            } else if (valueType == BMValueType.FUNCTION.getType()) {

                final String value = (String) valueValueType.getValue();

                nameValue.setValues(new byte[] { valueType }, value.getBytes(StandardCharsets.UTF_8));

            } else if (valueType == BMValueType.INTERNAL_BYTE.getType()) {
                throw new WffRuntimeException(
                        "BMValueType.BYTE is only for internal use, use WffBMByteArray for row bytes.");
            }

        }

        return WffBinaryMessageUtil.VERSION_1.getWffBinaryMessageBytes(nameValues);
    }

    /**
     * @param bmObjectBytes
     * @param outer
     */
    private void initWffBMObject(final byte[] bmObjectBytes, final boolean outer) {

        final WffBMObject wffBMObject = this;

        final List<NameValue> bmObject = WffBinaryMessageUtil.VERSION_1.parse(bmObjectBytes);

        final Iterator<NameValue> iterator = bmObject.iterator();
        if (iterator.hasNext()) {

            if (outer) {
                final NameValue typeNameValue = iterator.next();
                if (typeNameValue.getName()[0] == BMType.OBJECT.getType()) {
                    wffBMObject.outer = true;
                } else {
                    throw new WffRuntimeException("Not a valid Wff BM Object bytes");
                }
            }

            while (iterator.hasNext()) {
                final NameValue nameValue = iterator.next();
                final String name = new String(nameValue.getName(), StandardCharsets.UTF_8);
                final byte[][] values = nameValue.getValues();
                final byte valueType = values[0][0];
                final byte[] value = values[1];

                if (valueType == BMValueType.STRING.getType()) {
                    final ValueValueType valueValueType = new ValueValueType(name, valueType,
                            new String(value, StandardCharsets.UTF_8));
                    wffBMObject.put(name, valueValueType);
                } else if (valueType == BMValueType.NUMBER.getType()) {

                    final double doubleValue = ByteBuffer.wrap(value).getDouble(0);

                    final ValueValueType valueValueType = new ValueValueType(name, valueType, doubleValue);
                    wffBMObject.put(name, valueValueType);
                } else if (valueType == BMValueType.UNDEFINED.getType()) {
                    final ValueValueType valueValueType = new ValueValueType(name, valueType, null);
                    wffBMObject.put(name, valueValueType);
                } else if (valueType == BMValueType.NULL.getType()) {
                    final ValueValueType valueValueType = new ValueValueType(name, valueType, null);
                    wffBMObject.put(name, valueValueType);
                } else if (valueType == BMValueType.BOOLEAN.getType()) {
                    final ValueValueType valueValueType = new ValueValueType(name, valueType, value[0] == 1);
                    wffBMObject.put(name, valueValueType);
                } else if (valueType == BMValueType.BM_OBJECT.getType()) {

                    final ValueValueType valueValueType = new ValueValueType(name, valueType,
                            new WffBMObject(value, false));
                    wffBMObject.put(name, valueValueType);

                } else if (valueType == BMValueType.BM_ARRAY.getType()) {
                    final ValueValueType valueValueType = new ValueValueType(name, valueType,
                            new WffBMArray(value, false));
                    wffBMObject.put(name, valueValueType);
                } else if (valueType == BMValueType.BM_BYTE_ARRAY.getType()) {
                    final WffBMByteArray byteArray = new WffBMByteArray(value, false);
                    final ValueValueType valueValueType = new ValueValueType(name, valueType, byteArray);
                    wffBMObject.put(name, valueValueType);
                } else if (valueType == BMValueType.REG_EXP.getType()) {
                    final ValueValueType valueValueType = new ValueValueType(name, valueType,
                            new String(value, StandardCharsets.UTF_8));
                    wffBMObject.put(name, valueValueType);
                } else if (valueType == BMValueType.FUNCTION.getType()) {
                    final ValueValueType valueValueType = new ValueValueType(name, valueType,
                            new String(value, StandardCharsets.UTF_8));
                    wffBMObject.put(name, valueValueType);
                } else if (valueType == BMValueType.INTERNAL_BYTE.getType()) {
                    throw new WffRuntimeException(
                            "BMValueType.BYTE is only for internal use, use WffBMByteArray for row bytes.");
                }

            }
        }

    }

    /**
     * @return the bmBytes
     * @since 3.0.16
     */
    public byte[] getBMBytes() {
        return Arrays.copyOf(bMBytes, bMBytes.length);
    }

    public boolean isOuter() {
        return outer;
    }

    public void setOuter(final boolean outer) {
        this.outer = outer;
    }

    /**
     * @param key the key name
     * @author WFF
     * @since 2.0.0
     */
    public Object getValue(final String key) {
        final ValueValueType valueValueType = super.get(key);
        if (valueValueType == null) {
            return null;
        }

        return valueValueType.getValue();
    }

    /**
     * @param key
     * @return the value type of this key
     * @author WFF
     * @since 2.0.0
     */
    public BMValueType getValueType(final String key) {
        final ValueValueType valueValueType = super.get(key);
        if (valueValueType == null) {
            return null;
        }
        return valueValueType.getValueType();
    }

    @Override
    public BMType getBMType() {
        return BMType.OBJECT;
    }

    /**
     * @param key the key to get the value.
     * @return the Double value.
     * @throws NumberFormatException if the value is not convertible to Double.
     * @since 12.0.3
     */
    public Double getValueAsDouble(final String key) throws NumberFormatException {
        final ValueValueType valueValueType = super.get(key);
        if (valueValueType == null) {
            return null;
        }
        return valueValueType.valueAsDouble();
    }

    /**
     * @param key the key to get the value.
     * @return the Float value.
     * @throws NumberFormatException if the value is not convertible to Float.
     * @since 12.0.3
     */
    public Float getValueAsFloat(final String key) throws NumberFormatException {
        final ValueValueType valueValueType = super.get(key);
        if (valueValueType == null) {
            return null;
        }
        return valueValueType.valueAsFloat();
    }

    /**
     * @param key the key to get the value.
     * @return the BigDecimal value.
     * @throws NumberFormatException if the value is not convertible to BigDecimal.
     * @since 12.0.3
     */
    public BigDecimal getValueAsBigDecimal(final String key) throws NumberFormatException {
        final ValueValueType valueValueType = super.get(key);
        if (valueValueType == null) {
            return null;
        }
        return valueValueType.valueAsBigDecimal();
    }

    /**
     * @param key the key to get the value.
     * @return the BigInteger value.
     * @throws NumberFormatException if the value is not convertible to BigInteger.
     * @since 12.0.3
     */
    public BigInteger getValueAsBigInteger(final String key) throws NumberFormatException {
        final ValueValueType valueValueType = super.get(key);
        if (valueValueType == null) {
            return null;
        }
        return valueValueType.valueAsBigInteger();
    }

    /**
     * @param key the key to get the value.
     * @return the Integer value.
     * @throws NumberFormatException if the value is not convertible to Integer.
     * @since 12.0.3
     */
    public Integer getValueAsInteger(final String key) throws NumberFormatException {
        final ValueValueType valueValueType = super.get(key);
        if (valueValueType == null) {
            return null;
        }
        return valueValueType.valueAsInteger();
    }

    /**
     * @param key the key to get the value.
     * @return the Integer value.
     * @throws NumberFormatException if the value is not convertible to Long.
     * @since 12.0.3
     */
    public Long getValueAsLong(final String key) {
        final ValueValueType valueValueType = super.get(key);
        if (valueValueType == null) {
            return null;
        }
        return valueValueType.valueAsLong();
    }

    /**
     * @param key the key to get the value.
     * @return the value as a String.
     * @since 12.0.3
     */
    public String getValueAsString(final String key) {
        final ValueValueType valueValueType = super.get(key);
        if (valueValueType == null) {
            return null;
        }
        return valueValueType.valueAsString();
    }

    /**
     * @param key the key to get the value.
     * @return the value as a Boolean.
     * @since 12.0.3
     */
    public Boolean getValueAsBoolean(final String key) {
        final ValueValueType valueValueType = super.get(key);
        if (valueValueType == null) {
            return null;
        }
        return valueValueType.valueAsBoolean();
    }

    /**
     * The value will be internally saved as a double and the BMValueType will be
     * NUMBER. If you want to save a big value larger than double, save it as a
     * string and get it by getValueAsBigInteger/getValueAsBigDecimal method.
     *
     * @param key   the key.
     * @param value the value for the key.
     * @since 12.0.3
     */
    public void put(final String key, final Number value) {
        put(key, BMValueType.NUMBER, value);
    }

}
