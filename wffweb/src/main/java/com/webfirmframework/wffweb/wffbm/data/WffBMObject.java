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
import java.util.function.Predicate;

import com.webfirmframework.wffweb.InvalidValueException;
import com.webfirmframework.wffweb.WffRuntimeException;
import com.webfirmframework.wffweb.util.WffBinaryMessageUtil;
import com.webfirmframework.wffweb.util.data.NameValue;

/**
 * The java object representation for JavaScript object. <br>
 * Sample code :- <br>
 *
 * <pre><code>
 * WffBMObject bmObject = new WffBMObject();
 * bmObject.put("serverKey", BMValueType.STRING, "value from server");
 * bmObject.put("string", BMValueType.STRING, "sample string");
 * bmObject.put("nul", BMValueType.NULL, null);
 * bmObject.put("number", BMValueType.NUMBER, 555);
 * bmObject.put("undef", BMValueType.UNDEFINED, null);
 * bmObject.put("reg", BMValueType.REG_EXP, "[w]");
 * bmObject.put("bool", BMValueType.BOOLEAN, true);
 * bmObject.put("testFun", BMValueType.FUNCTION, "function(arg) {alert(arg);}");
 * </code></pre>
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
        // do not keep overloading method put(String, BMValueType, float) it may execute
        // this method (i.e. (... Object))
        // and for int value it will execute (..., float) method
        if (BMValueType.NUMBER.equals(valueType) && value instanceof final Float f) {
            super.put(key,
                    new ValueValueType(key, valueType.getType(), new BigDecimal(Float.toString(f)).doubleValue()));
        } else {
            super.put(key, new ValueValueType(key, valueType.getType(), value));
        }
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
     * The value will be internally saved as a double and its BMValueType will be
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

    /**
     * The value will be internally saved as boolean and its BMValueType will be
     * NUMBER.
     *
     * @param key   the key.
     * @param value the value for the key.
     * @since 12.0.3
     */
    public void put(final String key, final Boolean value) {
        put(key, BMValueType.BOOLEAN, value);
    }

    /**
     * The value will be internally saved as WffBMObject and its BMValueType will be
     * BM_OBJECT.
     *
     * @param key   the key.
     * @param value the value for the key.
     * @since 12.0.3
     */
    public void put(final String key, final WffBMObject value) {
        put(key, BMValueType.BM_OBJECT, value);
    }

    /**
     * The value will be internally saved as WffBMArray and its BMValueType will be
     * BM_ARRAY.
     *
     * @param key   the key.
     * @param value the value for the key.
     * @since 12.0.3
     */
    public void put(final String key, final WffBMArray value) {
        put(key, BMValueType.BM_ARRAY, value);
    }

    /**
     * The value will be internally saved as WffBMByteArray and its BMValueType will
     * be BM_BYTE_ARRAY.
     *
     * @param key   the key.
     * @param value the value for the key.
     * @since 12.0.3
     */
    public void put(final String key, final WffBMByteArray value) {
        put(key, BMValueType.BM_BYTE_ARRAY, value);
    }

    /**
     * The value will be internally saved as null and its BMValueType will be NULL.
     *
     * @param key the key.
     * @since 12.0.3
     */
    public void putNull(final String key) {
        put(key, BMValueType.NULL, null);
    }

    /**
     * The value will be internally saved as null and its BMValueType will be
     * UNDEFINED.
     *
     * @param key the key.
     * @since 12.0.3
     */
    public void putUndefined(final String key) {
        put(key, BMValueType.UNDEFINED, null);
    }

    /**
     * The value will be internally saved as regex string and its BMValueType will
     * be REG_EXP. Eg:
     *
     * <pre><code>
     *     WffBMObject obj = new WffBMObject();
     *     obj.putRegex("regexStr", "[w]");
     * </code></pre>
     *
     * @param key the key. @ param value the value for the key.
     * @since 12.0.3
     */
    public void putRegex(final String key, final String regex) {
        put(key, BMValueType.REG_EXP, regex);
    }

    /**
     * The value will be internally saved as string and its BMValueType will be
     * STRING.
     *
     * @param key the key. @ param value the value for the key.
     * @since 12.0.3
     */
    public void putString(final String key, final String string) {
        put(key, BMValueType.STRING, string);
    }

    /**
     * The value will be internally saved as function string and its BMValueType
     * will be FUNCTION. Eg:
     *
     * <pre><code>
     *     WffBMObject obj = new WffBMObject();
     *     obj.putFunction("funKey", "function(arg) {console.log(arg);}");
     * </code></pre>
     *
     * @param key the key. @ param value the value for the key.
     * @since 12.0.3
     */
    public void putFunction(final String key, final String function) {
        put(key, BMValueType.FUNCTION, function);
    }

    /**
     * @param key the key to get the value.
     * @return the Double value.
     * @throws NumberFormatException if the value is not convertible to Double.
     * @since 12.0.3
     */
    public Double getValueAsDouble(final String key) throws NumberFormatException {
        return getValueAsDouble(key, null, null);
    }

    /**
     * To get value as Double if the predicate test returns true. Eg:
     *
     * <pre><code>
     * WffBMObject wffBMObject = new WffBMObject();
     * wffBMObject.put("keyForDouble", 14.01D);
     * Double valueAsDouble = wffBMObject.getValueAsDouble("keyForDouble", valueValueType -> valueValueType.value() != null &amp;&amp; BMValueType.NUMBER.equals(valueValueType.valueType()), 1401.19D);
     * </code></pre> In the above code if the value is not null and valueType is
     * NUMBER then only it will convert the value to Double otherwise it will return
     * the default value passed in the third argument i.e. 1401.19D.
     *
     * @param key          the key to get the value.
     * @param predicate    to test the condition to return the converted value. If
     *                     the predicate test returns true this method will return
     *                     the converted value otherwise it will return default
     *                     value.
     * @param defaultValue the default value to return.
     * @return the Double value.
     * @throws NumberFormatException if the value is not convertible to Double.
     * @since 12.0.3
     */
    public Double getValueAsDouble(final String key, final Predicate<ValueAndValueType> predicate,
            final Double defaultValue) throws NumberFormatException {
        final ValueValueType valueValueType = super.get(key);
        if (valueValueType == null) {
            return defaultValue;
        }
        if (predicate == null
                || predicate.test(new ValueAndValueType(valueValueType.getValue(), valueValueType.getValueType()))) {
            return valueValueType.valueAsDouble();
        }
        return defaultValue;
    }

    /**
     * @param key the key to get the value.
     * @return the Float value.
     * @throws NumberFormatException if the value is not convertible to Float.
     * @since 12.0.3
     */
    public Float getValueAsFloat(final String key) throws NumberFormatException {
        return getValueAsFloat(key, null, null);
    }

    /**
     * To get value as Float if the predicate test returns true. Eg:
     *
     * <pre><code>
     * WffBMObject wffBMObject = new WffBMObject();
     * wffBMObject.put("keyForFloat", 14.01F);
     * Float valueAsFloat = wffBMObject.getValueAsFloat("keyForFloat", valueValueType -> valueValueType.value() != null &amp;&amp; BMValueType.NUMBER.equals(valueValueType.valueType()), 1401.19F);
     * </code></pre> In the above code if the value is not null and valueType is
     * NUMBER then only it will convert the value to Float otherwise it will return
     * the default value passed in the third argument i.e. 1401.19F.
     *
     * @param key          the key to get the value.
     * @param predicate    to test the condition to return the converted value. If
     *                     the predicate test returns true this method will return
     *                     the converted value otherwise it will return default
     *                     value.
     * @param defaultValue the default value to return.
     * @return the Float value.
     * @throws NumberFormatException if the value is not convertible to Float.
     * @since 12.0.3
     */
    public Float getValueAsFloat(final String key, final Predicate<ValueAndValueType> predicate,
            final Float defaultValue) throws NumberFormatException {
        final ValueValueType valueValueType = super.get(key);
        if (valueValueType == null) {
            return defaultValue;
        }
        if (predicate == null
                || predicate.test(new ValueAndValueType(valueValueType.getValue(), valueValueType.getValueType()))) {
            return valueValueType.valueAsFloat();
        }
        return defaultValue;
    }

    /**
     * @param key the key to get the value.
     * @return the BigDecimal value.
     * @throws NumberFormatException if the value is not convertible to BigDecimal.
     * @since 12.0.3
     */
    public BigDecimal getValueAsBigDecimal(final String key) throws NumberFormatException {
        return getValueAsBigDecimal(key, null, null);
    }

    /**
     * To get value as BigDecimal if the predicate test returns true. Eg:
     *
     * <pre><code>
     * WffBMObject wffBMObject = new WffBMObject();
     * wffBMObject.put("keyForBigDecimal", new BigDecimal("14.01"));
     * BigDecimal valueAsBigDecimal = wffBMObject.getValueAsBigDecimal("keyForBigDecimal", valueValueType -> valueValueType.value() != null &amp;&amp; BMValueType.NUMBER.equals(valueValueType.valueType()), new BigDecimal("1401.19"));
     * </code></pre> In the above code if the value is not null and valueType is
     * NUMBER then only it will convert the value to BigDecimal otherwise it will
     * return the default value passed in the third argument i.e. 1401.19.
     *
     * @param key          the key to get the value.
     * @param predicate    to test the condition to return the converted value. If
     *                     the predicate test returns true this method will return
     *                     the converted value otherwise it will return default
     *                     value.
     * @param defaultValue the default value to return.
     * @return the BigDecimal value.
     * @throws NumberFormatException if the value is not convertible to BigDecimal.
     * @since 12.0.3
     */
    public BigDecimal getValueAsBigDecimal(final String key, final Predicate<ValueAndValueType> predicate,
            final BigDecimal defaultValue) throws NumberFormatException {
        final ValueValueType valueValueType = super.get(key);
        if (valueValueType == null) {
            return defaultValue;
        }
        if (predicate == null
                || predicate.test(new ValueAndValueType(valueValueType.getValue(), valueValueType.getValueType()))) {
            return valueValueType.valueAsBigDecimal();
        }
        return defaultValue;
    }

    /**
     * @param key the key to get the value.
     * @return the BigInteger value.
     * @throws NumberFormatException if the value is not convertible to BigInteger.
     * @since 12.0.3
     */
    public BigInteger getValueAsBigInteger(final String key) throws NumberFormatException {
        return getValueAsBigInteger(key, null, null);
    }

    /**
     * To get value as BigInteger if the predicate test returns true. Eg:
     *
     * <pre><code>
     * WffBMObject wffBMObject = new WffBMObject();
     * wffBMObject.put("keyForBigInteger", new BigInteger("14"));
     * BigInteger valueAsBigInteger = wffBMObject.getValueAsBigInteger("keyForBigInteger", valueValueType -> valueValueType.value() != null &amp;&amp; BMValueType.NUMBER.equals(valueValueType.valueType()), new BigInteger("1401"));
     * </code></pre> In the above code if the value is not null and valueType is
     * NUMBER then only it will convert the value to BigInteger otherwise it will
     * return the default value passed in the third argument i.e. 1401.
     *
     * @param key          the key to get the value.
     * @param predicate    to test the condition to return the converted value. If
     *                     the predicate test returns true this method will return
     *                     the converted value otherwise it will return default
     *                     value.
     * @param defaultValue the default value to return.
     * @return the BigInteger value.
     * @throws NumberFormatException if the value is not convertible to BigInteger.
     * @since 12.0.3
     */
    public BigInteger getValueAsBigInteger(final String key, final Predicate<ValueAndValueType> predicate,
            final BigInteger defaultValue) throws NumberFormatException {
        final ValueValueType valueValueType = super.get(key);
        if (valueValueType == null) {
            return defaultValue;
        }
        if (predicate == null
                || predicate.test(new ValueAndValueType(valueValueType.getValue(), valueValueType.getValueType()))) {
            return valueValueType.valueAsBigInteger();
        }
        return defaultValue;
    }

    /**
     * @param key the key to get the value.
     * @return the Integer value.
     * @throws NumberFormatException if the value is not convertible to Integer.
     * @since 12.0.3
     */
    public Integer getValueAsInteger(final String key) throws NumberFormatException {
        return getValueAsInteger(key, null, null);
    }

    /**
     * To get value as Integer if the predicate test returns true. Eg:
     *
     * <pre><code>
     * WffBMObject wffBMObject = new WffBMObject();
     * wffBMObject.put("keyForInteger", 14);
     * Integer valueAsInteger = wffBMObject.getValueAsInteger("keyForInteger", valueValueType -> valueValueType.value() != null &amp;&amp; BMValueType.NUMBER.equals(valueValueType.valueType()), 1401);
     * </code></pre> In the above code if the value is not null and valueType is
     * NUMBER then only it will convert the value to Integer otherwise it will
     * return the default value passed in the third argument i.e. 1401.
     *
     * @param key          the key to get the value.
     * @param predicate    to test the condition to return the converted value. If
     *                     the predicate test returns true this method will return
     *                     the converted value otherwise it will return default
     *                     value.
     * @param defaultValue the default value to return.
     * @return the Integer value.
     * @throws NumberFormatException if the value is not convertible to Integer.
     * @since 12.0.3
     */
    public Integer getValueAsInteger(final String key, final Predicate<ValueAndValueType> predicate,
            final Integer defaultValue) throws NumberFormatException {
        final ValueValueType valueValueType = super.get(key);
        if (valueValueType == null) {
            return defaultValue;
        }
        if (predicate == null
                || predicate.test(new ValueAndValueType(valueValueType.getValue(), valueValueType.getValueType()))) {
            return valueValueType.valueAsInteger();
        }
        return defaultValue;
    }

    /**
     * @param key the key to get the value.
     * @return the Long value.
     * @throws NumberFormatException if the value is not convertible to Long.
     * @since 12.0.3
     */
    public Long getValueAsLong(final String key) {
        return getValueAsLong(key, null, null);
    }

    /**
     * To get value as Long if the predicate test returns true. Eg:
     *
     * <pre><code>
     * WffBMObject wffBMObject = new WffBMObject();
     * wffBMObject.put("keyForLong", 14L);
     * Long valueAsLong = wffBMObject.getValueAsLong("keyForLong", valueValueType -> valueValueType.value() != null &amp;&amp; BMValueType.NUMBER.equals(valueValueType.valueType()), 1401L);
     * </code></pre> In the above code if the value is not null and valueType is
     * NUMBER then only it will convert the value to Long otherwise it will return
     * the default value passed in the third argument i.e. 1401L.
     *
     * @param key          the key to get the value.
     * @param predicate    to test the condition to return the converted value. If
     *                     the predicate test returns true this method will return
     *                     the converted value otherwise it will return default
     *                     value.
     * @param defaultValue the default value to return.
     * @return the Long value.
     * @throws NumberFormatException if the value is not convertible to Long.
     * @since 12.0.3
     */
    public Long getValueAsLong(final String key, final Predicate<ValueAndValueType> predicate,
            final Long defaultValue) {
        final ValueValueType valueValueType = super.get(key);
        if (valueValueType == null) {
            return defaultValue;
        }
        if (predicate == null
                || predicate.test(new ValueAndValueType(valueValueType.getValue(), valueValueType.getValueType()))) {
            return valueValueType.valueAsLong();
        }
        return defaultValue;
    }

    /**
     * @param key the key to get the value.
     * @return the value as String.
     * @since 12.0.3
     */
    public String getValueAsString(final String key) {
        return getValueAsString(key, null, null);
    }

    /**
     * To get value as String if the predicate test returns true. Eg:
     *
     * <pre><code>
     * WffBMObject wffBMObject = new WffBMObject();
     * wffBMObject.putString("keyForString", "wffweb");
     * String valueAsString = wffBMObject.getValueAsString("keyForString", valueValueType -> valueValueType.value() != null &amp;&amp; BMValueType.STRING.equals(valueValueType.valueType()), "some default value");
     * </code></pre> In the above code if the value is not null and valueType is
     * STRING then only it will convert the value to String otherwise it will return
     * the default value passed in the third argument i.e. "some default value".
     *
     * @param key          the key to get the value.
     * @param predicate    to test the condition to return the converted value. If
     *                     the predicate test returns true this method will return
     *                     the converted value otherwise it will return default
     *                     value.
     * @param defaultValue the default value to return.
     * @return the value as String.
     * @since 12.0.3
     */
    public String getValueAsString(final String key, final Predicate<ValueAndValueType> predicate,
            final String defaultValue) {
        final ValueValueType valueValueType = super.get(key);
        if (valueValueType == null) {
            return defaultValue;
        }
        if (predicate == null
                || predicate.test(new ValueAndValueType(valueValueType.getValue(), valueValueType.getValueType()))) {
            return valueValueType.valueAsString();
        }
        return defaultValue;
    }

    /**
     * @param key the key to get the value.
     * @return the value as Boolean.
     * @since 12.0.3
     */
    public Boolean getValueAsBoolean(final String key) {
        return getValueAsBoolean(key, null, null);
    }

    /**
     * To get value as Boolean if the predicate test returns true. Eg:
     *
     * <pre><code>
     * WffBMObject wffBMObject = new WffBMObject();
     * wffBMObject.put("keyForBoolean", true);
     * Boolean valueAsBoolean = wffBMObject.getValueAsBoolean("keyForBoolean", valueValueType -> valueValueType.value() != null &amp;&amp; BMValueType.BOOLEAN.equals(valueValueType.valueType()), null);
     * </code></pre> In the above code if the value is not null and valueType is
     * BOOLEAN then only it will convert the value to Boolean otherwise it will
     * return the default value passed in the third argument, i.e null.
     *
     * @param key          the key to get the value.
     * @param predicate    to test the condition to return the converted value. If
     *                     the predicate test returns true this method will return
     *                     the converted value otherwise it will return default
     *                     value.
     * @param defaultValue the default value to return.
     * @return the value as Boolean.
     * @since 12.0.3
     */
    public Boolean getValueAsBoolean(final String key, final Predicate<ValueAndValueType> predicate,
            final Boolean defaultValue) {
        final ValueValueType valueValueType = super.get(key);
        if (valueValueType == null) {
            return defaultValue;
        }
        if (predicate == null
                || predicate.test(new ValueAndValueType(valueValueType.getValue(), valueValueType.getValueType()))) {
            return valueValueType.valueAsBoolean();
        }
        return defaultValue;
    }

    /**
     * @param other the other object for similarity checking.
     * @return true if the other object also contains the same data otherwise false.
     * @since 12.0.3
     */
    public boolean similar(final WffBMObject other) {
        return Arrays.equals(buildBytes(true), other.buildBytes(true));
    }

}
