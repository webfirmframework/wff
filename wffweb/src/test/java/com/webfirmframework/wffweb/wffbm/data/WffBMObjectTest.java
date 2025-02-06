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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.Test;

import com.webfirmframework.wffweb.InvalidValueException;
import com.webfirmframework.wffweb.util.NumberUtil;

public class WffBMObjectTest {

    @Test
    public void testGetValueAsBigDecimal() {
        final WffBMObject wffBMObject = new WffBMObject();

        final String keyForNumberString = "keyForNumberString";
        final String numberString = "14.01";
        wffBMObject.putString(keyForNumberString, numberString);
        BigDecimal valueAsBigDecimal = wffBMObject.getValueAsBigDecimal(keyForNumberString);
        assertEquals(numberString, valueAsBigDecimal.stripTrailingZeros().toPlainString());

        final String keyForNumberPrimitive1 = "keyForNumberPrimitive1";
        final double numberPrimitive1 = 14.01D;
        wffBMObject.put(keyForNumberPrimitive1, BMValueType.NUMBER, numberPrimitive1);
        valueAsBigDecimal = wffBMObject.getValueAsBigDecimal(keyForNumberPrimitive1);
        assertEquals(String.valueOf(numberPrimitive1), valueAsBigDecimal.stripTrailingZeros().toPlainString());

        final String keyForNumberPrimitive2 = "keyForNumberPrimitive2";
        final long numberPrimitive2 = 1401;
        wffBMObject.put(keyForNumberPrimitive2, BMValueType.NUMBER, numberPrimitive2);
        valueAsBigDecimal = wffBMObject.getValueAsBigDecimal(keyForNumberPrimitive2);
        assertEquals(String.valueOf(numberPrimitive2), valueAsBigDecimal.stripTrailingZeros().toPlainString());

        final String keyForNumberPrimitive3 = "keyForNumberPrimitive3";
        final int numberPrimitive3 = 1401;
        wffBMObject.put(keyForNumberPrimitive3, BMValueType.NUMBER, numberPrimitive3);
        valueAsBigDecimal = wffBMObject.getValueAsBigDecimal(keyForNumberPrimitive3);
        assertEquals(String.valueOf(numberPrimitive3), valueAsBigDecimal.stripTrailingZeros().toPlainString());

        final String keyForNumberPrimitive4 = "keyForNumberPrimitive4";
        final float numberPrimitive4 = 14.01F;
        wffBMObject.put(keyForNumberPrimitive4, BMValueType.NUMBER, numberPrimitive4);
        valueAsBigDecimal = wffBMObject.getValueAsBigDecimal(keyForNumberPrimitive4);
        assertEquals(String.valueOf(numberPrimitive4), valueAsBigDecimal.stripTrailingZeros().toPlainString());

        final String keyForNumberPrimitive5 = "keyForNumberPrimitive5";
        final short numberPrimitive5 = 14;
        wffBMObject.put(keyForNumberPrimitive5, BMValueType.NUMBER, numberPrimitive5);
        valueAsBigDecimal = wffBMObject.getValueAsBigDecimal(keyForNumberPrimitive5);
        assertEquals(String.valueOf(numberPrimitive5), valueAsBigDecimal.stripTrailingZeros().toPlainString());

        final String keyForNullNumber = "keyForNullNumber";
        wffBMObject.put(keyForNullNumber, BMValueType.NUMBER, null);
        valueAsBigDecimal = wffBMObject.getValueAsBigDecimal(keyForNullNumber);
        assertNull(valueAsBigDecimal);

        final String keyForNullString = "keyForNullString";
        wffBMObject.put(keyForNullString, BMValueType.STRING, null);
        valueAsBigDecimal = wffBMObject.getValueAsBigDecimal(keyForNullString);
        assertNull(valueAsBigDecimal);

        final String keyForNullType = "keyForNullType";
        wffBMObject.put(keyForNullType, BMValueType.NULL, null);
        valueAsBigDecimal = wffBMObject.getValueAsBigDecimal(keyForNullType);
        assertNull(valueAsBigDecimal);
    }

    @Test
    public void testGetValueAsBigDecimalDeserialized() {
        WffBMObject wffBMObject = new WffBMObject();
        final String keyForNumberString = "keyForNumberString";
        final String numberString = "14.01";
        wffBMObject.put(keyForNumberString, BMValueType.STRING, numberString);

        final String keyForNumberPrimitive1 = "keyForNumberPrimitive1";
        final double numberPrimitive1 = 14.01D;
        wffBMObject.put(keyForNumberPrimitive1, BMValueType.NUMBER, numberPrimitive1);

        final String keyForNumberPrimitive2 = "keyForNumberPrimitive2";
        final long numberPrimitive2 = 1401;
        wffBMObject.put(keyForNumberPrimitive2, BMValueType.NUMBER, numberPrimitive2);

        final String keyForNumberPrimitive3 = "keyForNumberPrimitive3";
        final int numberPrimitive3 = 1401;
        wffBMObject.put(keyForNumberPrimitive3, BMValueType.NUMBER, numberPrimitive3);

        final String keyForNumberPrimitive4 = "keyForNumberPrimitive4";
        final float numberPrimitive4 = 14.01F;
        wffBMObject.put(keyForNumberPrimitive4, BMValueType.NUMBER, numberPrimitive4);

        final String keyForNumberNonPrimitive1 = "keyForNumberNonPrimitive1";
        final BigDecimal numberNonPrimitive1 = new BigDecimal("14.01");
        wffBMObject.put(keyForNumberNonPrimitive1, BMValueType.NUMBER, numberNonPrimitive1);

        wffBMObject = new WffBMObject(wffBMObject.buildBytes(true), true);

        final BigDecimal valueForNumberString = wffBMObject.getValueAsBigDecimal(keyForNumberString);
        assertEquals(numberString, valueForNumberString.stripTrailingZeros().toPlainString());

        final BigDecimal valueForNumberPrimitive1 = wffBMObject.getValueAsBigDecimal(keyForNumberPrimitive1);
        assertEquals(String.valueOf(numberPrimitive1), valueForNumberPrimitive1.stripTrailingZeros().toPlainString());

        final BigDecimal valueForNumberPrimitive2 = wffBMObject.getValueAsBigDecimal(keyForNumberPrimitive2);
        assertEquals(String.valueOf(numberPrimitive2), valueForNumberPrimitive2.stripTrailingZeros().toPlainString());

        final BigDecimal valueForNumberPrimitive3 = wffBMObject.getValueAsBigDecimal(keyForNumberPrimitive3);
        assertEquals(String.valueOf(numberPrimitive3), valueForNumberPrimitive3.stripTrailingZeros().toPlainString());

        // TODO review this later
        final BigDecimal valueForNumberPrimitive4 = wffBMObject.getValueAsBigDecimal(keyForNumberPrimitive4);
        assertEquals(Float.toString(numberPrimitive4), valueForNumberPrimitive4.stripTrailingZeros().toPlainString());

        final BigDecimal valueForNumberNonPrimitive1 = wffBMObject.getValueAsBigDecimal(keyForNumberNonPrimitive1);
        assertEquals(numberNonPrimitive1.stripTrailingZeros().toPlainString(),
                valueForNumberNonPrimitive1.stripTrailingZeros().toPlainString());
    }

    @Test(expected = NumberFormatException.class)
    public void testGetValueAsBigDecimalNumberFormatException() {
        final WffBMObject wffBMObject = new WffBMObject();
        final String keyForRegex = "keyForNumberString";
        final String regex = "*";
        wffBMObject.put(keyForRegex, BMValueType.REG_EXP, regex);
        // exception is expected
        wffBMObject.getValueAsBigDecimal(keyForRegex);
    }

    @Test
    public void testGetValueAsBigInteger() {
        final WffBMObject wffBMObject = new WffBMObject();

        final String keyForNumberString = "keyForNumberString";
        final String numberString = "14";
        wffBMObject.put(keyForNumberString, BMValueType.STRING, numberString);
        BigInteger valueAsBigInteger = wffBMObject.getValueAsBigInteger(keyForNumberString);
        assertEquals(numberString, valueAsBigInteger.toString());

        final String keyForNumberPrimitive2 = "keyForNumberPrimitive2";
        final long numberPrimitive2 = 1401;
        wffBMObject.put(keyForNumberPrimitive2, BMValueType.NUMBER, numberPrimitive2);
        valueAsBigInteger = wffBMObject.getValueAsBigInteger(keyForNumberPrimitive2);
        assertEquals(String.valueOf(numberPrimitive2), valueAsBigInteger.toString());

        final String keyForNumberPrimitive3 = "keyForNumberPrimitive3";
        final int numberPrimitive3 = 1401;
        wffBMObject.put(keyForNumberPrimitive3, BMValueType.NUMBER, numberPrimitive3);
        valueAsBigInteger = wffBMObject.getValueAsBigInteger(keyForNumberPrimitive3);
        assertEquals(String.valueOf(numberPrimitive3), valueAsBigInteger.toString());

        final String keyForNumberPrimitive4 = "keyForNumberPrimitive4";
        final short numberPrimitive4 = 14;
        wffBMObject.put(keyForNumberPrimitive4, BMValueType.NUMBER, numberPrimitive4);
        valueAsBigInteger = wffBMObject.getValueAsBigInteger(keyForNumberPrimitive4);
        assertEquals(String.valueOf(numberPrimitive4), valueAsBigInteger.toString());

        final String keyForNullNumber = "keyForNullNumber";
        wffBMObject.put(keyForNullNumber, BMValueType.NUMBER, null);
        valueAsBigInteger = wffBMObject.getValueAsBigInteger(keyForNullNumber);
        assertNull(valueAsBigInteger);

        final String keyForNullString = "keyForNullString";
        wffBMObject.put(keyForNullString, BMValueType.STRING, null);
        valueAsBigInteger = wffBMObject.getValueAsBigInteger(keyForNullString);
        assertNull(valueAsBigInteger);

        final String keyForNullType = "keyForNullType";
        wffBMObject.put(keyForNullType, BMValueType.NULL, null);
        valueAsBigInteger = wffBMObject.getValueAsBigInteger(keyForNullType);
        assertNull(valueAsBigInteger);
    }

    @Test
    public void testGetValueAsBigIntegerDeserialized() {
        WffBMObject wffBMObject = new WffBMObject();
        final String keyForNumberString = "keyForNumberString";
        final String numberString = "14";
        wffBMObject.put(keyForNumberString, BMValueType.STRING, numberString);

        final String keyForNumberPrimitive2 = "keyForNumberPrimitive2";
        final long numberPrimitive2 = 1401;
        wffBMObject.put(keyForNumberPrimitive2, BMValueType.NUMBER, numberPrimitive2);

        final String keyForNumberPrimitive3 = "keyForNumberPrimitive3";
        final int numberPrimitive3 = 1401;
        wffBMObject.put(keyForNumberPrimitive3, BMValueType.NUMBER, numberPrimitive3);

        wffBMObject = new WffBMObject(wffBMObject.buildBytes(true));

        final BigInteger valueForNumberString = wffBMObject.getValueAsBigInteger(keyForNumberString);
        assertEquals(numberString, valueForNumberString.toString());

        final BigInteger valueForNumberPrimitive2 = wffBMObject.getValueAsBigInteger(keyForNumberPrimitive2);
        assertEquals(String.valueOf(numberPrimitive2), valueForNumberPrimitive2.toString());

        final BigInteger valueForNumberPrimitive3 = wffBMObject.getValueAsBigInteger(keyForNumberPrimitive3);
        assertEquals(String.valueOf(numberPrimitive3), valueForNumberPrimitive3.toString());
    }

    @Test(expected = NumberFormatException.class)
    public void testGetValueAsBigIntegerNumberFormatException1() {
        final WffBMObject wffBMObject = new WffBMObject();
        final String keyForRegex = "keyForNumberString";
        final String regex = "*";
        wffBMObject.put(keyForRegex, BMValueType.REG_EXP, regex);
        // exception is expected
        wffBMObject.getValueAsBigInteger(keyForRegex);
    }

    @Test(expected = NumberFormatException.class)
    public void testGetValueAsBigIntegerNumberFormatException2() {
        final WffBMObject wffBMObject = new WffBMObject();
        final String keyForNumberDouble = "keyForNumberDouble";
        final double valueForNumberDouble = 14.01D;
        wffBMObject.put(keyForNumberDouble, BMValueType.NUMBER, valueForNumberDouble);
        // exception is expected
        wffBMObject.getValueAsBigInteger(keyForNumberDouble);
    }

    @Test
    public void testGetValueAsInteger() {
        final WffBMObject wffBMObject = new WffBMObject();

        Integer valueAsInteger;

        final String keyForNumberString = "keyForNumberString";
        final String numberString = "14";
        wffBMObject.put(keyForNumberString, BMValueType.STRING, numberString);
        valueAsInteger = wffBMObject.getValueAsInteger(keyForNumberString);
        assertEquals(numberString, valueAsInteger.toString());

        final String keyForNumberPrimitive2 = "keyForNumberPrimitive2";
        final long numberPrimitive2 = 1401;
        wffBMObject.put(keyForNumberPrimitive2, BMValueType.NUMBER, numberPrimitive2);
        valueAsInteger = wffBMObject.getValueAsInteger(keyForNumberPrimitive2);
        assertEquals(String.valueOf(numberPrimitive2), valueAsInteger.toString());

        final String keyForNumberPrimitive3 = "keyForNumberPrimitive3";
        final int numberPrimitive3 = 1401;
        wffBMObject.put(keyForNumberPrimitive3, BMValueType.NUMBER, numberPrimitive3);
        valueAsInteger = wffBMObject.getValueAsInteger(keyForNumberPrimitive3);
        assertEquals(String.valueOf(numberPrimitive3), valueAsInteger.toString());

        final String keyForNumberPrimitive4 = "keyForNumberPrimitive4";
        final short numberPrimitive4 = 14;
        wffBMObject.put(keyForNumberPrimitive4, BMValueType.NUMBER, numberPrimitive4);
        valueAsInteger = wffBMObject.getValueAsInteger(keyForNumberPrimitive4);
        assertEquals(numberPrimitive4, valueAsInteger.intValue());

        final String keyForNumberPrimitive5 = "keyForNumberPrimitive4";
        final long numberPrimitive5 = 14L;
        wffBMObject.put(keyForNumberPrimitive5, BMValueType.NUMBER, numberPrimitive5);
        valueAsInteger = wffBMObject.getValueAsInteger(keyForNumberPrimitive5);
        assertEquals(numberPrimitive5, valueAsInteger.longValue());

        final String keyForNullNumber = "keyForNullNumber";
        wffBMObject.put(keyForNullNumber, BMValueType.NUMBER, null);
        valueAsInteger = wffBMObject.getValueAsInteger(keyForNullNumber);
        assertNull(valueAsInteger);

        final String keyForNullType = "keyForNullType";
        wffBMObject.put(keyForNullType, BMValueType.NULL, null);
        valueAsInteger = wffBMObject.getValueAsInteger(keyForNullType);
        assertNull(valueAsInteger);
    }

    @Test
    public void testGetValueAsIntegerDeserialized() {
        WffBMObject wffBMObject = new WffBMObject();

        final String keyForNumberString = "keyForNumberString";
        final String numberString = "14";
        wffBMObject.put(keyForNumberString, BMValueType.STRING, numberString);

        final String keyForNumberPrimitive2 = "keyForNumberPrimitive2";
        final long numberPrimitive2 = 1401;
        wffBMObject.put(keyForNumberPrimitive2, BMValueType.NUMBER, numberPrimitive2);

        final String keyForNumberPrimitive3 = "keyForNumberPrimitive3";
        final int numberPrimitive3 = 1401;
        wffBMObject.put(keyForNumberPrimitive3, BMValueType.NUMBER, numberPrimitive3);

        wffBMObject = new WffBMObject(wffBMObject.buildBytes(true));

        final Integer valueForNumberString = wffBMObject.getValueAsInteger(keyForNumberString);
        assertEquals(numberString, valueForNumberString.toString());

        final Integer valueForNumberPrimitive2 = wffBMObject.getValueAsInteger(keyForNumberPrimitive2);
        assertEquals(String.valueOf(numberPrimitive2), valueForNumberPrimitive2.toString());

        final Integer valueForNumberPrimitive3 = wffBMObject.getValueAsInteger(keyForNumberPrimitive3);
        assertEquals(String.valueOf(numberPrimitive3), valueForNumberPrimitive3.toString());
    }

    @Test(expected = NumberFormatException.class)
    public void testGetValueAsIntegerNumberFormatException1() {
        final WffBMObject wffBMObject = new WffBMObject();
        final String keyForRegex = "keyForNumberString";
        final String regex = "*";
        wffBMObject.put(keyForRegex, BMValueType.REG_EXP, regex);
        // exception is expected
        wffBMObject.getValueAsInteger(keyForRegex);
    }

    @Test(expected = NumberFormatException.class)
    public void testGetValueAsIntegerNumberFormatException2() {
        final WffBMObject wffBMObject = new WffBMObject();
        final String keyForNumberDouble = "keyForNumberDouble";
        final double valueForNumberDouble = 14.01D;
        wffBMObject.put(keyForNumberDouble, BMValueType.NUMBER, valueForNumberDouble);
        // exception is expected
        wffBMObject.getValueAsInteger(keyForNumberDouble);
    }

    @Test(expected = NumberFormatException.class)
    public void testGetValueAsIntegerNumberFormatException3() {
        final WffBMObject wffBMObject = new WffBMObject();
        final String keyForNumberDouble = "keyForNumberDouble";
        final long valueForNumberDouble = Long.MAX_VALUE;
        wffBMObject.put(keyForNumberDouble, BMValueType.NUMBER, valueForNumberDouble);
        // exception is expected
        wffBMObject.getValueAsInteger(keyForNumberDouble);
    }

    @Test
    public void testGetValueAsLong() {
        final WffBMObject wffBMObject = new WffBMObject();

        Long valueAsLong;

        final String keyForNumberString = "keyForNumberString";
        final String numberString = "14";
        wffBMObject.put(keyForNumberString, BMValueType.STRING, numberString);
        valueAsLong = wffBMObject.getValueAsLong(keyForNumberString);
        assertEquals(numberString, valueAsLong.toString());

        final String keyForNumberPrimitive2 = "keyForNumberPrimitive2";
        final long numberPrimitive2 = 1401;
        wffBMObject.put(keyForNumberPrimitive2, BMValueType.NUMBER, numberPrimitive2);
        valueAsLong = wffBMObject.getValueAsLong(keyForNumberPrimitive2);
        assertEquals(String.valueOf(numberPrimitive2), valueAsLong.toString());

        final String keyForNumberPrimitive3 = "keyForNumberPrimitive3";
        final int numberPrimitive3 = 1401;
        wffBMObject.put(keyForNumberPrimitive3, BMValueType.NUMBER, numberPrimitive3);
        valueAsLong = wffBMObject.getValueAsLong(keyForNumberPrimitive3);
        assertEquals(String.valueOf(numberPrimitive3), valueAsLong.toString());

        final String keyForNumberPrimitive4 = "keyForNumberPrimitive4";
        final short numberPrimitive4 = 14;
        wffBMObject.put(keyForNumberPrimitive4, BMValueType.NUMBER, numberPrimitive4);
        valueAsLong = wffBMObject.getValueAsLong(keyForNumberPrimitive4);
        assertEquals(numberPrimitive4, valueAsLong.longValue());

        final String keyForNullNumber = "keyForNullNumber";
        wffBMObject.put(keyForNullNumber, BMValueType.NUMBER, null);
        valueAsLong = wffBMObject.getValueAsLong(keyForNullNumber);
        assertNull(valueAsLong);

        final String keyForNullType = "keyForNullType";
        wffBMObject.put(keyForNullType, BMValueType.NULL, null);
        valueAsLong = wffBMObject.getValueAsLong(keyForNullType);
        assertNull(valueAsLong);

        final String keyForNullString = "keyForNullString";
        wffBMObject.put(keyForNullString, BMValueType.STRING, null);
        valueAsLong = wffBMObject.getValueAsLong(keyForNullString);
        assertNull(valueAsLong);
    }

    @Test
    public void testGetValueAsLongDeserialized() {
        WffBMObject wffBMObject = new WffBMObject();

        final String keyForNumberString = "keyForNumberString";
        final String numberString = "14";
        wffBMObject.put(keyForNumberString, BMValueType.STRING, numberString);

        final String keyForNumberPrimitive2 = "keyForNumberPrimitive2";
        final long numberPrimitive2 = 1401;
        wffBMObject.put(keyForNumberPrimitive2, BMValueType.NUMBER, numberPrimitive2);

        final String keyForNumberPrimitive3 = "keyForNumberPrimitive3";
        final int numberPrimitive3 = 1401;
        wffBMObject.put(keyForNumberPrimitive3, BMValueType.NUMBER, numberPrimitive3);

        wffBMObject = new WffBMObject(wffBMObject.buildBytes(true));

        final Long valueForNumberString = wffBMObject.getValueAsLong(keyForNumberString);
        assertEquals(numberString, valueForNumberString.toString());

        final Long valueForNumberPrimitive2 = wffBMObject.getValueAsLong(keyForNumberPrimitive2);
        assertEquals(String.valueOf(numberPrimitive2), valueForNumberPrimitive2.toString());

        final Long valueForNumberPrimitive3 = wffBMObject.getValueAsLong(keyForNumberPrimitive3);
        assertEquals(String.valueOf(numberPrimitive3), valueForNumberPrimitive3.toString());
    }

    @Test(expected = NumberFormatException.class)
    public void testGetValueAsLongNumberFormatException1() {
        final WffBMObject wffBMObject = new WffBMObject();
        final String keyForRegex = "keyForNumberString";
        final String regex = "*";
        wffBMObject.put(keyForRegex, BMValueType.REG_EXP, regex);
        // exception is expected
        wffBMObject.getValueAsLong(keyForRegex);
    }

    @Test(expected = NumberFormatException.class)
    public void testGetValueAsLongNumberFormatException2() {
        final WffBMObject wffBMObject = new WffBMObject();
        final String keyForNumberDouble = "keyForNumberDouble";
        final double valueForNumberDouble = 14.01D;
        wffBMObject.put(keyForNumberDouble, BMValueType.NUMBER, valueForNumberDouble);
        // exception is expected
        wffBMObject.getValueAsLong(keyForNumberDouble);
    }

    @Test
    public void testGetValueAsString() {
        final WffBMObject wffBMObject = new WffBMObject();

        final String keyForNumberString = "keyForNumberString";
        final String numberString = "14.01";
        wffBMObject.put(keyForNumberString, BMValueType.STRING, numberString);
        String valueAsString = wffBMObject.getValueAsString(keyForNumberString);
        assertEquals(numberString, valueAsString);

        final String keyForNumberPrimitive1 = "keyForNumberPrimitive1";
        final double numberPrimitive1 = 14.01D;
        wffBMObject.put(keyForNumberPrimitive1, BMValueType.NUMBER, numberPrimitive1);
        valueAsString = wffBMObject.getValueAsString(keyForNumberPrimitive1);
        assertEquals(String.valueOf(numberPrimitive1), valueAsString);

        final String keyForNumberPrimitive2 = "keyForNumberPrimitive2";
        final long numberPrimitive2 = 1401;
        wffBMObject.put(keyForNumberPrimitive2, BMValueType.NUMBER, numberPrimitive2);
        valueAsString = wffBMObject.getValueAsString(keyForNumberPrimitive2);
        assertEquals(String.valueOf(numberPrimitive2), valueAsString);

        final String keyForNumberPrimitive3 = "keyForNumberPrimitive3";
        final int numberPrimitive3 = 1401;
        wffBMObject.put(keyForNumberPrimitive3, BMValueType.NUMBER, numberPrimitive3);
        valueAsString = wffBMObject.getValueAsString(keyForNumberPrimitive3);
        assertEquals(String.valueOf(numberPrimitive3), valueAsString);

        final String keyForNumberPrimitive4 = "keyForNumberPrimitive4";
        final float numberPrimitive4 = 14.01F;
        wffBMObject.put(keyForNumberPrimitive4, BMValueType.NUMBER, numberPrimitive4);
        valueAsString = wffBMObject.getValueAsString(keyForNumberPrimitive4);
        assertEquals(String.valueOf(numberPrimitive4), valueAsString);

        final String keyForNumberNonPrimitive1 = "keyForNumberNonPrimitive1";
        final BigDecimal numberNonPrimitive1 = new BigDecimal("14.01");
        wffBMObject.put(keyForNumberNonPrimitive1, BMValueType.NUMBER, numberNonPrimitive1);
        valueAsString = wffBMObject.getValueAsString(keyForNumberNonPrimitive1);
        assertEquals(numberNonPrimitive1.stripTrailingZeros().toPlainString(), valueAsString);

        final String keyForRegex = "keyForNumberString";
        final String regex = "*";
        wffBMObject.put(keyForRegex, BMValueType.REG_EXP, regex);
        valueAsString = wffBMObject.getValueAsString(keyForRegex);
        assertEquals(regex, valueAsString);

        final String keyForNullFunctionString = "keyForFunctionString";
        final String functionString = null;
        wffBMObject.put(keyForNullFunctionString, BMValueType.REG_EXP, functionString);
        valueAsString = wffBMObject.getValueAsString(keyForNullFunctionString);
        assertEquals(functionString, valueAsString);

        final String keyForNullNumber = "keyForNullNumber";
        wffBMObject.put(keyForNullNumber, BMValueType.NUMBER, null);
        valueAsString = wffBMObject.getValueAsString(keyForNullNumber);
        assertNull(valueAsString);

        final String keyForNullString = "keyForNullString";
        wffBMObject.put(keyForNullString, BMValueType.STRING, null);
        valueAsString = wffBMObject.getValueAsString(keyForNullString);
        assertNull(valueAsString);

        final String keyForNullType = "keyForNullType";
        wffBMObject.put(keyForNullType, BMValueType.NULL, null);
        valueAsString = wffBMObject.getValueAsString(keyForNullType);
        assertNull(valueAsString);
    }

    @Test
    public void testGetValueAsDouble() {
        final WffBMObject wffBMObject = new WffBMObject();

        final String keyForNumberString = "keyForNumberString";
        final String numberString = "14.01";
        wffBMObject.put(keyForNumberString, BMValueType.STRING, numberString);
        Double valueAsDouble = wffBMObject.getValueAsDouble(keyForNumberString);
        assertEquals(numberString, valueAsDouble.toString());

        final String keyForNumberPrimitive1 = "keyForNumberPrimitive1";
        final double numberPrimitive1 = 14.01D;
        wffBMObject.put(keyForNumberPrimitive1, BMValueType.NUMBER, numberPrimitive1);
        valueAsDouble = wffBMObject.getValueAsDouble(keyForNumberPrimitive1);
        assertEquals(String.valueOf(numberPrimitive1), valueAsDouble.toString());

        final String keyForNumberPrimitive2 = "keyForNumberPrimitive2";
        final long numberPrimitive2 = 1401;
        wffBMObject.put(keyForNumberPrimitive2, BMValueType.NUMBER, numberPrimitive2);
        valueAsDouble = wffBMObject.getValueAsDouble(keyForNumberPrimitive2);
        assertEquals(String.valueOf(numberPrimitive2).concat(".0"), valueAsDouble.toString());

        final String keyForNumberPrimitive3 = "keyForNumberPrimitive3";
        final int numberPrimitive3 = 1401;
        wffBMObject.put(keyForNumberPrimitive3, BMValueType.NUMBER, numberPrimitive3);
        valueAsDouble = wffBMObject.getValueAsDouble(keyForNumberPrimitive3);
        assertEquals(String.valueOf(numberPrimitive3).concat(".0"), valueAsDouble.toString());

        final String keyForNumberPrimitive4 = "keyForNumberPrimitive4";
        final float numberPrimitive4 = 14.01F;
        wffBMObject.put(keyForNumberPrimitive4, BMValueType.NUMBER, numberPrimitive4);
        valueAsDouble = wffBMObject.getValueAsDouble(keyForNumberPrimitive4);
        assertEquals(String.valueOf(numberPrimitive4), String.valueOf(valueAsDouble.floatValue()));

        final String keyForNullNumber = "keyForNullNumber";
        wffBMObject.put(keyForNullNumber, BMValueType.NUMBER, null);
        valueAsDouble = wffBMObject.getValueAsDouble(keyForNullNumber);
        assertNull(valueAsDouble);

        final String keyForNullString = "keyForNullString";
        wffBMObject.put(keyForNullString, BMValueType.STRING, null);
        valueAsDouble = wffBMObject.getValueAsDouble(keyForNullString);
        assertNull(valueAsDouble);

        final String keyForNullType = "keyForNullType";
        wffBMObject.put(keyForNullType, BMValueType.NULL, null);
        valueAsDouble = wffBMObject.getValueAsDouble(keyForNullType);
        assertNull(valueAsDouble);
    }

    @Test
    public void testGetValueAsDoubleDeserialized() {
        WffBMObject wffBMObject = new WffBMObject();
        final String keyForNumberString = "keyForNumberString";
        final String numberString = "14.01";
        wffBMObject.put(keyForNumberString, BMValueType.STRING, numberString);

        final String keyForNumberPrimitive1 = "keyForNumberPrimitive1";
        final double numberPrimitive1 = 14.01D;
        wffBMObject.put(keyForNumberPrimitive1, BMValueType.NUMBER, numberPrimitive1);

        final String keyForNumberPrimitive2 = "keyForNumberPrimitive2";
        final long numberPrimitive2 = 1401;
        wffBMObject.put(keyForNumberPrimitive2, BMValueType.NUMBER, numberPrimitive2);

        final String keyForNumberPrimitive3 = "keyForNumberPrimitive3";
        final int numberPrimitive3 = 1401;
        wffBMObject.put(keyForNumberPrimitive3, BMValueType.NUMBER, numberPrimitive3);

        final String keyForNumberPrimitive4 = "keyForNumberPrimitive4";
        final float numberPrimitive4 = 14.01F;
        wffBMObject.put(keyForNumberPrimitive4, BMValueType.NUMBER, numberPrimitive4);

        wffBMObject = new WffBMObject(wffBMObject.buildBytes(true));

        final Double valueForNumberString = wffBMObject.getValueAsDouble(keyForNumberString);
        assertEquals(numberString, valueForNumberString.toString());

        final Double valueForNumberPrimitive1 = wffBMObject.getValueAsDouble(keyForNumberPrimitive1);
        assertEquals(String.valueOf(numberPrimitive1), valueForNumberPrimitive1.toString());

        final Double valueForNumberPrimitive2 = wffBMObject.getValueAsDouble(keyForNumberPrimitive2);
        assertEquals(String.valueOf(numberPrimitive2).concat(".0"), valueForNumberPrimitive2.toString());

        final Double valueForNumberPrimitive3 = wffBMObject.getValueAsDouble(keyForNumberPrimitive3);
        assertEquals(String.valueOf(numberPrimitive3).concat(".0"), valueForNumberPrimitive3.toString());

        final Double valueForNumberPrimitive4 = wffBMObject.getValueAsDouble(keyForNumberPrimitive4);
        assertEquals(String.valueOf(numberPrimitive4), String.valueOf(valueForNumberPrimitive4.floatValue()));
    }

    @Test(expected = NumberFormatException.class)
    public void testGetValueAsDoubleNumberFormatException() {
        final WffBMObject wffBMObject = new WffBMObject();
        final String keyForRegex = "keyForNumberString";
        final String regex = "*";
        wffBMObject.put(keyForRegex, BMValueType.REG_EXP, regex);
        // exception is expected
        wffBMObject.getValueAsDouble(keyForRegex);
    }

    @Test
    public void testGetValueAsBoolean() {
        final WffBMObject wffBMObject = new WffBMObject();

        final String keyForBooleanString = "keyForBooleanString";
        String booleanString = "true";
        wffBMObject.put(keyForBooleanString, BMValueType.STRING, booleanString);
        Boolean valueAsBoolean = wffBMObject.getValueAsBoolean(keyForBooleanString);
        assertEquals(booleanString, valueAsBoolean.toString());

        booleanString = "false";
        wffBMObject.put(keyForBooleanString, BMValueType.STRING, booleanString);
        valueAsBoolean = wffBMObject.getValueAsBoolean(keyForBooleanString);
        assertEquals(booleanString, valueAsBoolean.toString());

        final String keyForBoolean = "keyForBoolean";
        wffBMObject.put(keyForBoolean, true);
        valueAsBoolean = wffBMObject.getValueAsBoolean(keyForBoolean);
        assertTrue(valueAsBoolean);

        wffBMObject.put(keyForBoolean, BMValueType.BOOLEAN, false);
        valueAsBoolean = wffBMObject.getValueAsBoolean(keyForBoolean);
        assertFalse(valueAsBoolean);

        wffBMObject.put(keyForBoolean, BMValueType.BOOLEAN, null);
        valueAsBoolean = wffBMObject.getValueAsBoolean(keyForBoolean);
        assertNull(valueAsBoolean);

        final String keyForNullString = "keyForNullString";
        wffBMObject.put(keyForNullString, BMValueType.STRING, null);
        valueAsBoolean = wffBMObject.getValueAsBoolean(keyForNullString);
        assertNull(valueAsBoolean);

        final String keyForNullType = "keyForNullType";
        wffBMObject.put(keyForNullType, BMValueType.NULL, null);
        valueAsBoolean = wffBMObject.getValueAsBoolean(keyForNullType);
        assertNull(valueAsBoolean);
    }

    @Test(expected = InvalidValueException.class)
    public void testGetValueAsBooleanInvalidValueException1() {
        final WffBMObject wffBMObject = new WffBMObject();
        final String keyForNumberPrimitive1 = "keyForNumberPrimitive1";
        final double numberPrimitive1 = 1D;
        wffBMObject.put(keyForNumberPrimitive1, BMValueType.NUMBER, numberPrimitive1);
        wffBMObject.getValueAsBoolean(keyForNumberPrimitive1);
    }

    @Test(expected = InvalidValueException.class)
    public void testGetValueAsBooleanInvalidValueException2() {
        final WffBMObject wffBMObject = new WffBMObject();
        final String keyForNumberPrimitive2 = "keyForNumberPrimitive2";
        final long numberPrimitive2 = 1401;
        wffBMObject.put(keyForNumberPrimitive2, BMValueType.NUMBER, numberPrimitive2);
        wffBMObject.getValueAsBoolean(keyForNumberPrimitive2);
    }

    @Test(expected = InvalidValueException.class)
    public void testGetValueAsBooleanInvalidValueException3() {
        final WffBMObject wffBMObject = new WffBMObject();
        final String keyForNumberPrimitive3 = "keyForNumberPrimitive3";
        final int numberPrimitive3 = 1401;
        wffBMObject.put(keyForNumberPrimitive3, BMValueType.NUMBER, numberPrimitive3);
        wffBMObject.getValueAsBoolean(keyForNumberPrimitive3);
    }

    @Test(expected = InvalidValueException.class)
    public void testGetValueAsBooleanInvalidValueException4() {
        final WffBMObject wffBMObject = new WffBMObject();
        final String keyForNumberPrimitive4 = "keyForNumberPrimitive4";
        final float numberPrimitive4 = 14.01F;
        wffBMObject.put(keyForNumberPrimitive4, BMValueType.NUMBER, numberPrimitive4);
        wffBMObject.getValueAsBoolean(keyForNumberPrimitive4);
    }

    @Test(expected = InvalidValueException.class)
    public void testGetValueAsBooleanInvalidValueException5() {
        final WffBMObject wffBMObject = new WffBMObject();
        final String keyForNullNumber = "keyForNullNumber";
        wffBMObject.put(keyForNullNumber, BMValueType.NUMBER, null);
        wffBMObject.getValueAsBoolean(keyForNullNumber);
    }

    @Test
    public void testGetValueAsBooleanDeserialized() {
        WffBMObject wffBMObject = new WffBMObject();
        final String keyForNumberString = "keyForNumberString";
        final String numberString = "true";
        wffBMObject.put(keyForNumberString, BMValueType.STRING, numberString);

        wffBMObject = new WffBMObject(wffBMObject.buildBytes(true));

        final Boolean valueForNumberString = wffBMObject.getValueAsBoolean(keyForNumberString);
        assertEquals(numberString, valueForNumberString.toString());
    }

    @Test(expected = InvalidValueException.class)
    public void testGetValueAsBooleanInvalidValueException() {
        final WffBMObject wffBMObject = new WffBMObject();
        final String keyForRegex = "keyForNumberString";
        final String regex = "*";
        wffBMObject.put(keyForRegex, BMValueType.REG_EXP, regex);
        // exception is expected
        wffBMObject.getValueAsBoolean(keyForRegex);
    }

    @Test
    public void testGetValueAsFloat() {
        final WffBMObject wffBMObject = new WffBMObject();

        final String keyForNumberString = "keyForNumberString";
        final String numberString = "14.01";
        wffBMObject.put(keyForNumberString, BMValueType.STRING, numberString);
        Float valueAsFloat = wffBMObject.getValueAsFloat(keyForNumberString);
        assertEquals(numberString, valueAsFloat.toString());

        final String keyForNumberPrimitive1 = "keyForNumberPrimitive1";
        final double numberPrimitive1 = 14.01D;
        wffBMObject.put(keyForNumberPrimitive1, BMValueType.NUMBER, numberPrimitive1);
        valueAsFloat = wffBMObject.getValueAsFloat(keyForNumberPrimitive1);
        assertEquals(String.valueOf(numberPrimitive1), valueAsFloat.toString());

        final String keyForNumberPrimitive2 = "keyForNumberPrimitive2";
        final long numberPrimitive2 = 1401;
        wffBMObject.put(keyForNumberPrimitive2, BMValueType.NUMBER, numberPrimitive2);
        valueAsFloat = wffBMObject.getValueAsFloat(keyForNumberPrimitive2);
        assertEquals(String.valueOf(numberPrimitive2).concat(".0"), valueAsFloat.toString());

        final String keyForNumberPrimitive3 = "keyForNumberPrimitive3";
        final int numberPrimitive3 = 1401;
        wffBMObject.put(keyForNumberPrimitive3, BMValueType.NUMBER, numberPrimitive3);
        valueAsFloat = wffBMObject.getValueAsFloat(keyForNumberPrimitive3);
        assertEquals(String.valueOf(numberPrimitive3).concat(".0"), valueAsFloat.toString());

        final String keyForNumberPrimitive4 = "keyForNumberPrimitive4";
        final float numberPrimitive4 = 14.01F;
        wffBMObject.put(keyForNumberPrimitive4, BMValueType.NUMBER, numberPrimitive4);
        valueAsFloat = wffBMObject.getValueAsFloat(keyForNumberPrimitive4);
        assertEquals(String.valueOf(numberPrimitive4), String.valueOf(valueAsFloat.floatValue()));

        final String keyForNullNumber = "keyForNullNumber";
        wffBMObject.put(keyForNullNumber, BMValueType.NUMBER, null);
        valueAsFloat = wffBMObject.getValueAsFloat(keyForNullNumber);
        assertNull(valueAsFloat);

        final String keyForNullString = "keyForNullString";
        wffBMObject.put(keyForNullString, BMValueType.STRING, null);
        valueAsFloat = wffBMObject.getValueAsFloat(keyForNullString);
        assertNull(valueAsFloat);

        final String keyForNullType = "keyForNullType";
        wffBMObject.put(keyForNullType, BMValueType.NULL, null);
        valueAsFloat = wffBMObject.getValueAsFloat(keyForNullType);
        assertNull(valueAsFloat);
    }

    @Test
    public void testGetValueAsFloatDeserialized() {
        WffBMObject wffBMObject = new WffBMObject();
        final String keyForNumberString = "keyForNumberString";
        final String numberString = "14.01";
        wffBMObject.put(keyForNumberString, BMValueType.STRING, numberString);

        final String keyForNumberPrimitive1 = "keyForNumberPrimitive1";
        final double numberPrimitive1 = 14.01D;
        wffBMObject.put(keyForNumberPrimitive1, BMValueType.NUMBER, numberPrimitive1);

        final String keyForNumberPrimitive2 = "keyForNumberPrimitive2";
        final long numberPrimitive2 = 1401;
        wffBMObject.put(keyForNumberPrimitive2, BMValueType.NUMBER, numberPrimitive2);

        final String keyForNumberPrimitive3 = "keyForNumberPrimitive3";
        final int numberPrimitive3 = 1401;
        wffBMObject.put(keyForNumberPrimitive3, BMValueType.NUMBER, numberPrimitive3);

        final String keyForNumberPrimitive4 = "keyForNumberPrimitive4";
        final float numberPrimitive4 = 14.01F;
        wffBMObject.put(keyForNumberPrimitive4, BMValueType.NUMBER, numberPrimitive4);

        final String keyForNumberPrimitive5 = "keyForNumberPrimitive5";
        final float numberPrimitive5 = 14.01F;
        wffBMObject.put(keyForNumberPrimitive5, numberPrimitive5);

        wffBMObject = new WffBMObject(wffBMObject.buildBytes(true));

        final Float valueForNumberString = wffBMObject.getValueAsFloat(keyForNumberString);
        assertEquals(numberString, valueForNumberString.toString());

        final Float valueForNumberPrimitive1 = wffBMObject.getValueAsFloat(keyForNumberPrimitive1);
        assertEquals(String.valueOf(numberPrimitive1), valueForNumberPrimitive1.toString());

        final Float valueForNumberPrimitive2 = wffBMObject.getValueAsFloat(keyForNumberPrimitive2);
        assertEquals(String.valueOf(numberPrimitive2).concat(".0"), valueForNumberPrimitive2.toString());

        final Float valueForNumberPrimitive3 = wffBMObject.getValueAsFloat(keyForNumberPrimitive3);
        assertEquals(String.valueOf(numberPrimitive3).concat(".0"), valueForNumberPrimitive3.toString());

        final Float valueForNumberPrimitive4 = wffBMObject.getValueAsFloat(keyForNumberPrimitive4);
        assertEquals(String.valueOf(numberPrimitive4), String.valueOf(valueForNumberPrimitive4.floatValue()));

        final Float valueForNumberPrimitive5 = wffBMObject.getValueAsFloat(keyForNumberPrimitive5);
        assertEquals(String.valueOf(numberPrimitive5), String.valueOf(valueForNumberPrimitive5.floatValue()));
    }

    @Test(expected = NumberFormatException.class)
    public void testGetValueAsFloatNumberFormatException() {
        final WffBMObject wffBMObject = new WffBMObject();
        final String keyForRegex = "keyForNumberString";
        final String regex = "*";
        wffBMObject.put(keyForRegex, BMValueType.REG_EXP, regex);
        // exception is expected
        wffBMObject.getValueAsFloat(keyForRegex);
    }

    @Test
    public void testPutBigDecimal() {
        WffBMObject wffBMObject = new WffBMObject(true);
        final String keyForBigDecimal = "keyForBigDecimal";
        final BigDecimal expected = new BigDecimal("14.01");
        wffBMObject.put(keyForBigDecimal, expected);
        BigDecimal actual = wffBMObject.getValueAsBigDecimal(keyForBigDecimal);
        assertTrue(NumberUtil.isEqual(expected, actual));

        wffBMObject = new WffBMObject(wffBMObject.buildBytes(true), true);
        actual = wffBMObject.getValueAsBigDecimal(keyForBigDecimal);
        assertTrue(NumberUtil.isEqual(expected, actual));
    }

    @Test
    public void testPutBigInteger() {
        WffBMObject wffBMObject = new WffBMObject(true);
        final String keyForBigInteger = "keyForBigInteger";
        final BigInteger expected = new BigInteger("14");
        wffBMObject.put(keyForBigInteger, expected);
        BigInteger actual = wffBMObject.getValueAsBigInteger(keyForBigInteger);
        assertTrue(NumberUtil.isEqual(expected, actual));

        wffBMObject = new WffBMObject(wffBMObject.buildBytes(true), true);
        actual = wffBMObject.getValueAsBigInteger(keyForBigInteger);
        assertTrue(NumberUtil.isEqual(expected, actual));
    }

    @Test
    public void testPutBigIntegerBigDecimal() {
        WffBMObject wffBMObject = new WffBMObject(true);
        final String keyForBigInteger = "keyForBigInteger";
        final String expectedValueString = "14";
        final BigInteger valueForBigInteger = new BigInteger(expectedValueString);
        wffBMObject.put(keyForBigInteger, valueForBigInteger);

        final String keyForBigDecimal = "keyForBigDecimal";
        final BigDecimal valueForBigDecimal = new BigDecimal(expectedValueString);
        wffBMObject.put(keyForBigDecimal, valueForBigDecimal);

        assertTrue(NumberUtil.isEqual(valueForBigInteger, wffBMObject.getValueAsBigInteger(keyForBigInteger)));
        assertTrue(NumberUtil.isEqual(valueForBigDecimal, wffBMObject.getValueAsBigDecimal(keyForBigDecimal)));

        assertTrue(NumberUtil.isEqual(new BigDecimal(valueForBigInteger),
                wffBMObject.getValueAsBigDecimal(keyForBigInteger)));
        assertTrue(NumberUtil.isEqual(valueForBigDecimal.toBigInteger(),
                wffBMObject.getValueAsBigInteger(keyForBigDecimal)));

        assertEquals(Integer.parseInt(expectedValueString), wffBMObject.getValueAsInteger(keyForBigInteger).intValue());
        assertEquals(Integer.parseInt(expectedValueString), wffBMObject.getValueAsInteger(keyForBigDecimal).intValue());

        assertEquals(Long.parseLong(expectedValueString), wffBMObject.getValueAsLong(keyForBigInteger).longValue());
        assertEquals(Long.parseLong(expectedValueString), wffBMObject.getValueAsLong(keyForBigDecimal).longValue());

        assertEquals(Double.parseDouble(expectedValueString), wffBMObject.getValueAsDouble(keyForBigInteger), 0);
        assertEquals(Double.parseDouble(expectedValueString), wffBMObject.getValueAsDouble(keyForBigDecimal), 0);

        assertEquals(Float.parseFloat(expectedValueString), wffBMObject.getValueAsFloat(keyForBigInteger), 0);
        assertEquals(Float.parseFloat(expectedValueString), wffBMObject.getValueAsFloat(keyForBigDecimal), 0);

        wffBMObject = new WffBMObject(wffBMObject.buildBytes(true), true);

        assertTrue(NumberUtil.isEqual(valueForBigInteger, wffBMObject.getValueAsBigInteger(keyForBigInteger)));
        assertTrue(NumberUtil.isEqual(valueForBigDecimal, wffBMObject.getValueAsBigDecimal(keyForBigDecimal)));

        assertTrue(NumberUtil.isEqual(new BigDecimal(valueForBigInteger),
                wffBMObject.getValueAsBigDecimal(keyForBigInteger)));
        assertTrue(NumberUtil.isEqual(valueForBigDecimal.toBigInteger(),
                wffBMObject.getValueAsBigInteger(keyForBigDecimal)));

        assertEquals(Integer.parseInt(expectedValueString), wffBMObject.getValueAsInteger(keyForBigInteger).intValue());
        assertEquals(Integer.parseInt(expectedValueString), wffBMObject.getValueAsInteger(keyForBigDecimal).intValue());

        assertEquals(Long.parseLong(expectedValueString), wffBMObject.getValueAsLong(keyForBigInteger).longValue());
        assertEquals(Long.parseLong(expectedValueString), wffBMObject.getValueAsLong(keyForBigDecimal).longValue());

        assertEquals(Double.parseDouble(expectedValueString), wffBMObject.getValueAsDouble(keyForBigInteger), 0);
        assertEquals(Double.parseDouble(expectedValueString), wffBMObject.getValueAsDouble(keyForBigDecimal), 0);

        assertEquals(Float.parseFloat(expectedValueString), wffBMObject.getValueAsFloat(keyForBigInteger), 0);
        assertEquals(Float.parseFloat(expectedValueString), wffBMObject.getValueAsFloat(keyForBigDecimal), 0);
    }

    @Test(expected = NumberFormatException.class)
    public void testValueAsFloatNumberFormatException1() {

        final WffBMObject wffBMObject = new WffBMObject(true);
        final String keyForBigInteger = "keyForBigInteger";
        final String expectedValueString = BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE).toString();
        final BigInteger valueForBigInteger = new BigInteger(expectedValueString);
        wffBMObject.put(keyForBigInteger, valueForBigInteger);

        wffBMObject.getValueAsFloat(keyForBigInteger);
    }

    @Test(expected = NumberFormatException.class)
    public void testValueAsFloatNumberFormatException2() {

        final WffBMObject wffBMObject = new WffBMObject(true);
        final String expectedValueString = BigDecimal.valueOf(16777217).stripTrailingZeros().toPlainString();

        final String keyForBigDecimal = "keyForBigDecimal";
        final BigDecimal valueForBigDecimal = new BigDecimal(expectedValueString);
        wffBMObject.put(keyForBigDecimal, valueForBigDecimal);

        wffBMObject.getValueAsFloat(keyForBigDecimal);
    }

    @Test(expected = ArithmeticException.class)
    public void testValueAsIntegerArithmeticException1() {

        final WffBMObject wffBMObject = new WffBMObject(true);
        final String keyForBigInteger = "keyForBigInteger";
        final String expectedValueString = BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE).toString();
        final BigInteger valueForBigInteger = new BigInteger(expectedValueString);
        wffBMObject.put(keyForBigInteger, valueForBigInteger);

        wffBMObject.getValueAsInteger(keyForBigInteger);
    }

    @Test(expected = ArithmeticException.class)
    public void testValueAsIntegerArithmeticException2() {

        final WffBMObject wffBMObject = new WffBMObject(true);
        final String expectedValueString = BigDecimal.valueOf(Long.MAX_VALUE).stripTrailingZeros().toPlainString();

        final String keyForBigDecimal = "keyForBigDecimal";
        final BigDecimal valueForBigDecimal = new BigDecimal(expectedValueString);
        wffBMObject.put(keyForBigDecimal, valueForBigDecimal);

        wffBMObject.getValueAsInteger(keyForBigDecimal);
    }

    @Test(expected = NumberFormatException.class)
    public void testValueAsIntegerNumberFormatException1() {

        final WffBMObject wffBMObject = new WffBMObject(true);
        final String expectedValueString = new BigDecimal("14.01").stripTrailingZeros().toPlainString();

        final String keyForBigDecimal = "keyForBigDecimal";
        final BigDecimal valueForBigDecimal = new BigDecimal(expectedValueString);
        wffBMObject.put(keyForBigDecimal, valueForBigDecimal);

        wffBMObject.getValueAsInteger(keyForBigDecimal);
    }

    @Test(expected = NumberFormatException.class)
    public void testValueAsLongNumberFormatException1() {

        final WffBMObject wffBMObject = new WffBMObject(true);
        final String expectedValueString = new BigDecimal("14.01").stripTrailingZeros().toPlainString();

        final String keyForBigDecimal = "keyForBigDecimal";
        final BigDecimal valueForBigDecimal = new BigDecimal(expectedValueString);
        wffBMObject.put(keyForBigDecimal, valueForBigDecimal);

        wffBMObject.getValueAsLong(keyForBigDecimal);
    }

    @Test
    public void testPredicateWithAllGetValueAsMethods() {
        final WffBMObject wffBMObject = new WffBMObject(true);

        final String keyForBigDecimal = "keyForBigDecimal";
        final BigDecimal valueForBigDecimal = new BigDecimal("14.01");
        wffBMObject.put(keyForBigDecimal, valueForBigDecimal);
        BigDecimal valueAsBigDecimal = wffBMObject.getValueAsBigDecimal(keyForBigDecimal,
                valueValueType -> valueValueType.value() != null
                        && BMValueType.NUMBER.equals(valueValueType.valueType()),
                null);
        assertNotNull(valueAsBigDecimal);
        valueAsBigDecimal = wffBMObject.getValueAsBigDecimal(keyForBigDecimal,
                valueValueType -> BMValueType.STRING.equals(valueValueType.valueType()), null);
        assertNull(valueAsBigDecimal);
        valueAsBigDecimal = wffBMObject.getValueAsBigDecimal(keyForBigDecimal,
                valueValueType -> BMValueType.STRING.equals(valueValueType.valueType()), BigDecimal.ONE);
        assertTrue(NumberUtil.isEqual(valueAsBigDecimal, BigDecimal.ONE));

        final String keyForBigInteger = "keyForBigInteger";
        final BigInteger valueForBigInteger = new BigInteger("1401");
        wffBMObject.put(keyForBigInteger, valueForBigInteger);
        BigInteger valueAsBigInteger = wffBMObject.getValueAsBigInteger(keyForBigInteger,
                valueValueType -> valueValueType.value() != null
                        && BMValueType.NUMBER.equals(valueValueType.valueType()),
                null);
        assertNotNull(valueAsBigInteger);
        valueAsBigInteger = wffBMObject.getValueAsBigInteger(keyForBigInteger,
                valueValueType -> BMValueType.STRING.equals(valueValueType.valueType()), null);
        assertNull(valueAsBigInteger);
        valueAsBigInteger = wffBMObject.getValueAsBigInteger(keyForBigInteger,
                valueValueType -> BMValueType.STRING.equals(valueValueType.valueType()), BigInteger.valueOf(14));
        assertTrue(NumberUtil.isEqual(valueAsBigInteger, BigInteger.valueOf(14)));

        final String keyForDouble = "keyForDouble";
        final double valueForDouble = 14.01D;
        wffBMObject.put(keyForDouble, valueForDouble);
        Double valueAsDouble = wffBMObject.getValueAsDouble(keyForDouble,
                valueValueType -> valueValueType.value() != null
                        && BMValueType.NUMBER.equals(valueValueType.valueType()),
                null);
        assertNotNull(valueAsDouble);
        valueAsDouble = wffBMObject.getValueAsDouble(keyForDouble,
                valueValueType -> BMValueType.STRING.equals(valueValueType.valueType()), null);
        assertNull(valueAsDouble);
        valueAsDouble = wffBMObject.getValueAsDouble(keyForDouble,
                valueValueType -> BMValueType.STRING.equals(valueValueType.valueType()), 1401D);
        assertEquals(1401D, valueAsDouble, 0);

        final String keyForFloat = "keyForDouble";
        final float valueForFloat = 14.01F;
        wffBMObject.put(keyForFloat, valueForFloat);
        Float valueAsFloat = wffBMObject.getValueAsFloat(keyForFloat, valueValueType -> valueValueType.value() != null
                && BMValueType.NUMBER.equals(valueValueType.valueType()), null);
        assertNotNull(valueAsFloat);
        valueAsFloat = wffBMObject.getValueAsFloat(keyForFloat,
                valueValueType -> BMValueType.STRING.equals(valueValueType.valueType()), null);
        assertNull(valueAsFloat);
        valueAsFloat = wffBMObject.getValueAsFloat(keyForFloat,
                valueValueType -> BMValueType.STRING.equals(valueValueType.valueType()), 1401F);
        assertEquals(1401F, valueAsFloat, 0);

        final String keyForInteger = "keyForInteger";
        final int valueForInteger = 1401;
        wffBMObject.put(keyForInteger, valueForInteger);
        Integer valueAsInteger = wffBMObject.getValueAsInteger(keyForInteger,
                valueValueType -> valueValueType.value() != null
                        && BMValueType.NUMBER.equals(valueValueType.valueType()),
                null);
        assertNotNull(valueAsInteger);
        valueAsInteger = wffBMObject.getValueAsInteger(keyForInteger,
                valueValueType -> BMValueType.STRING.equals(valueValueType.valueType()), null);
        assertNull(valueAsInteger);
        valueAsInteger = wffBMObject.getValueAsInteger(keyForInteger,
                valueValueType -> BMValueType.STRING.equals(valueValueType.valueType()), 14);
        assertEquals(14, valueAsInteger.intValue());

        final String keyForLong = "keyForLong";
        final long valueForLong = 1401;
        wffBMObject.put(keyForLong, valueForLong);
        Long valueAsLong = wffBMObject.getValueAsLong(keyForLong, valueValueType -> valueValueType.value() != null
                && BMValueType.NUMBER.equals(valueValueType.valueType()), null);
        assertNotNull(valueAsLong);
        valueAsLong = wffBMObject.getValueAsLong(keyForLong,
                valueValueType -> BMValueType.STRING.equals(valueValueType.valueType()), null);
        assertNull(valueAsLong);
        valueAsLong = wffBMObject.getValueAsLong(keyForLong,
                valueValueType -> BMValueType.STRING.equals(valueValueType.valueType()), 14L);
        assertEquals(14L, valueAsLong.longValue());

        final String keyForBoolean = "keyForBoolean";
        boolean valueForBoolean = false;
        wffBMObject.put(keyForBoolean, BMValueType.BOOLEAN, valueForBoolean);
        Boolean valueAsBoolean = wffBMObject.getValueAsBoolean(keyForBoolean,
                valueValueType -> valueValueType.value() != null
                        && BMValueType.BOOLEAN.equals(valueValueType.valueType()),
                null);
        assertNotNull(valueAsBoolean);
        assertFalse(valueAsBoolean);
        valueAsBoolean = wffBMObject.getValueAsBoolean(keyForBoolean,
                valueValueType -> BMValueType.STRING.equals(valueValueType.valueType()), null);
        assertNull(valueAsBoolean);
        valueAsBoolean = wffBMObject.getValueAsBoolean(keyForBoolean,
                valueValueType -> BMValueType.STRING.equals(valueValueType.valueType()), true);
        assertTrue(valueAsBoolean);

        valueForBoolean = true;
        wffBMObject.put(keyForBoolean, BMValueType.BOOLEAN, valueForBoolean);
        valueAsBoolean = wffBMObject.getValueAsBoolean(keyForBoolean, valueValueType -> valueValueType.value() != null
                && BMValueType.BOOLEAN.equals(valueValueType.valueType()), null);
        assertNotNull(valueAsBoolean);
        assertTrue(valueAsBoolean);
        valueAsBoolean = wffBMObject.getValueAsBoolean(keyForBoolean,
                valueValueType -> BMValueType.STRING.equals(valueValueType.valueType()), null);
        assertNull(valueAsBoolean);
        valueAsBoolean = wffBMObject.getValueAsBoolean(keyForBoolean,
                valueValueType -> BMValueType.STRING.equals(valueValueType.valueType()), false);
        assertFalse(valueAsBoolean);
    }

    @Test
    public void testPutMethods() throws IOException {
        final WffBMObject wffBMObject = new WffBMObject(true);

        final WffBMObject bmObj = new WffBMObject();
        bmObj.putString("str", "someVal");

        final WffBMArray wffBMAry = new WffBMArray(BMValueType.STRING);
        wffBMAry.add("one");

        final WffBMByteArray wffBMByteAry = new WffBMByteArray();
        wffBMByteAry.write(new byte[] { 1, 2, 3 });

        wffBMObject.put("bmObj", bmObj);
        wffBMObject.put("wffBMAry", wffBMAry);
        wffBMObject.put("wffBMByteAry", wffBMByteAry);
        wffBMObject.putNull("nullValue");
        wffBMObject.putUndefined("undefinedValue");
        wffBMObject.putRegex("regexStr", "[w]");
        wffBMObject.putString("keyForStr", "someVal");
        wffBMObject.putFunction("keyForFun", "function(arg) {console.log(arg);}");

        assertTrue(wffBMObject.getValue("bmObj") instanceof WffBMObject);
        assertEquals(BMValueType.BM_OBJECT, wffBMObject.getValueType("bmObj"));

        assertTrue(wffBMObject.getValue("wffBMAry") instanceof WffBMArray);
        assertEquals(BMValueType.BM_ARRAY, wffBMObject.getValueType("wffBMAry"));

        assertTrue(wffBMObject.getValue("wffBMByteAry") instanceof WffBMByteArray);
        assertEquals(BMValueType.BM_BYTE_ARRAY, wffBMObject.getValueType("wffBMByteAry"));

        assertTrue(wffBMObject.getValue("regexStr") instanceof String);
        assertEquals(BMValueType.REG_EXP, wffBMObject.getValueType("regexStr"));
        assertEquals("[w]", wffBMObject.getValue("regexStr"));

        assertTrue(wffBMObject.getValue("keyForStr") instanceof String);
        assertEquals(BMValueType.STRING, wffBMObject.getValueType("keyForStr"));
        assertEquals("someVal", wffBMObject.getValue("keyForStr"));

        assertTrue(wffBMObject.getValue("keyForFun") instanceof String);
        assertEquals(BMValueType.FUNCTION, wffBMObject.getValueType("keyForFun"));
        assertEquals("function(arg) {console.log(arg);}", wffBMObject.getValue("keyForFun"));

        assertNull(wffBMObject.getValue("nullValue"));
        assertEquals(BMValueType.NULL, wffBMObject.getValueType("nullValue"));

        assertNull(wffBMObject.getValue("undefinedValue"));
        assertEquals(BMValueType.UNDEFINED, wffBMObject.getValueType("undefinedValue"));
    }

    @Test
    public void testSimilar() throws IOException {

        final WffBMObject wffBMObject = new WffBMObject();

        final WffBMObject bmObj = new WffBMObject();
        bmObj.putString("str", "someVal");

        final WffBMArray wffBMAry = new WffBMArray(BMValueType.STRING);
        wffBMAry.add("one");

        final WffBMByteArray wffBMByteAry = new WffBMByteArray();
        wffBMByteAry.write(new byte[] { 1, 2, 3 });

        wffBMObject.put("bmObj", bmObj);
        wffBMObject.put("wffBMAry", wffBMAry);
        wffBMObject.put("wffBMByteAry", wffBMByteAry);
        wffBMObject.putNull("nullValue");
        wffBMObject.putUndefined("undefinedValue");
        wffBMObject.putRegex("regexStr", "[w]");
        wffBMObject.putString("keyForStr", "someVal");
        wffBMObject.putFunction("keyForFun", "function(arg) {console.log(arg);}");

        assertFalse(wffBMObject.isOuter());

        final WffBMObject wffBMObjectCopy = new WffBMObject(wffBMObject.buildBytes(), wffBMObject.isOuter());
        assertFalse(wffBMObjectCopy.isOuter());
        assertTrue(wffBMObject.similar(wffBMObjectCopy));
        wffBMObjectCopy.setOuter(true);
        assertTrue(wffBMObject.similar(wffBMObjectCopy));

        wffBMObjectCopy.putString("keyForStr", "valueChanged");
        assertFalse(wffBMObject.similar(wffBMObjectCopy));
    }

}
