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

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.Test;

import com.webfirmframework.wffweb.InvalidValueException;

public class WffBMArrayTest {

//    @Test
//    public void testAllValueTypes() {
//        WffBMArray wffBMArray = new WffBMArray(BMValueType.BOOLEAN);
//        wffBMArray.add(true);
//
//        WffBMObject wffBMObject = new WffBMObject();
//        wffBMObject.put("test", BMValueType.BM_ARRAY, wffBMArray);
//        wffBMObject.buildBytes(true);
//    }

    @Test
    public void testGetAsString() {
        final WffBMArray wffBMArray = new WffBMArray(BMValueType.STRING);
        wffBMArray.add("one");
        wffBMArray.add("two");
        wffBMArray.add("three");
        String valueAsString = wffBMArray.getValueAsString(0);
        assertEquals("one", valueAsString);

        valueAsString = wffBMArray.getValueAsString(1);
        assertEquals("two", valueAsString);

        valueAsString = wffBMArray.getValueAsString(2);
        assertEquals("three", valueAsString);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetAsStringIndexOutOfBoundsException() {
        final WffBMArray wffBMArray = new WffBMArray(BMValueType.STRING);
        wffBMArray.getValueAsString(0);
    }

    @Test
    public void testGetAsBigDecimal() {
        final WffBMArray wffBMArray = new WffBMArray(BMValueType.NUMBER);
        wffBMArray.add(1);
        wffBMArray.add(2);
        wffBMArray.add(3);
        BigDecimal value = wffBMArray.getValueAsBigDecimal(0);
        assertEquals(BigDecimal.valueOf(1), value);

        value = wffBMArray.getValueAsBigDecimal(1);
        assertEquals(BigDecimal.valueOf(2), value);

        value = wffBMArray.getValueAsBigDecimal(2);
        assertEquals(BigDecimal.valueOf(3), value);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetAsBigDecimalIndexOutOfBoundsException() {
        final WffBMArray wffBMArray = new WffBMArray(BMValueType.STRING);
        wffBMArray.getValueAsBigDecimal(0);
    }

    @Test
    public void testGetAsDouble() {
        final WffBMArray wffBMArray = new WffBMArray(BMValueType.NUMBER);
        wffBMArray.add(1D);
        wffBMArray.add(2);
        wffBMArray.add(3);
        wffBMArray.add(14.01);
        Double value = wffBMArray.getValueAsDouble(0);
        assertEquals(1D, value, 0);

        value = wffBMArray.getValueAsDouble(1);
        assertEquals(2D, value, 0);

        value = wffBMArray.getValueAsDouble(2);
        assertEquals(3D, value, 0);

        value = wffBMArray.getValueAsDouble(3);
        assertEquals(14.01, value, 0);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetAsDoubleIndexOutOfBoundsException() {
        final WffBMArray wffBMArray = new WffBMArray(BMValueType.STRING);
        wffBMArray.getValueAsDouble(0);
    }

    @Test
    public void testGetAsBigInteger() {
        final WffBMArray wffBMArray = new WffBMArray(BMValueType.NUMBER);
        wffBMArray.add(1);
        wffBMArray.add(2);
        wffBMArray.add(3);
        BigInteger value = wffBMArray.getValueAsBigInteger(0);
        assertEquals(BigInteger.valueOf(1), value);

        value = wffBMArray.getValueAsBigInteger(1);
        assertEquals(BigInteger.valueOf(2), value);

        value = wffBMArray.getValueAsBigInteger(2);
        assertEquals(BigInteger.valueOf(3), value);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetAsBigIntegerIndexOutOfBoundsException() {
        final WffBMArray wffBMArray = new WffBMArray(BMValueType.NUMBER);
        wffBMArray.getValueAsBigInteger(0);
    }

    @Test
    public void testGetAsInteger() {
        final WffBMArray wffBMArray = new WffBMArray(BMValueType.NUMBER);
        wffBMArray.add(1);
        wffBMArray.add(2);
        wffBMArray.add(3);
        Integer value = wffBMArray.getValueAsInteger(0);
        assertEquals(1, value.intValue());

        value = wffBMArray.getValueAsInteger(1);
        assertEquals(2, value.intValue());

        value = wffBMArray.getValueAsInteger(2);
        assertEquals(3, value.intValue());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetAsIntegerIndexOutOfBoundsException() {
        final WffBMArray wffBMArray = new WffBMArray(BMValueType.NUMBER);
        wffBMArray.getValueAsInteger(0);
    }

    @Test
    public void testGetAsLong() {
        final WffBMArray wffBMArray = new WffBMArray(BMValueType.NUMBER);
        wffBMArray.add(1L);
        wffBMArray.add(2L);
        wffBMArray.add(3);
        Long value = wffBMArray.getValueAsLong(0);
        assertEquals(1L, value.intValue());

        value = wffBMArray.getValueAsLong(1);
        assertEquals(2, value.intValue());

        value = wffBMArray.getValueAsLong(2);
        assertEquals(3L, value.intValue());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetAsLongIndexOutOfBoundsException() {
        final WffBMArray wffBMArray = new WffBMArray(BMValueType.STRING);
        wffBMArray.getValueAsLong(0);
    }

    @Test
    public void testGetAsBoolean() {
        final WffBMArray wffBMArray = new WffBMArray(BMValueType.BOOLEAN);
        wffBMArray.add(true);
        wffBMArray.add(false);
        wffBMArray.add("true");
        wffBMArray.add("false");
        Boolean value = wffBMArray.getValueAsBoolean(0);
        assertEquals(true, value.booleanValue());

        value = wffBMArray.getValueAsBoolean(1);
        assertEquals(false, value.booleanValue());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetAsBooleanIndexOutOfBoundsException() {
        final WffBMArray wffBMArray = new WffBMArray(BMValueType.STRING);
        wffBMArray.getValueAsBoolean(0);
    }

    @Test(expected = InvalidValueException.class)
    public void testGetAsBooleanInvalidValueException() {
        final WffBMArray wffBMArray = new WffBMArray(BMValueType.NUMBER);
        wffBMArray.add(1);
        wffBMArray.getValueAsBoolean(0);
    }

    @Test(expected = InvalidValueException.class)
    public void testGetAsBooleanInvalidValueException2() {
        final WffBMArray wffBMArray = new WffBMArray(BMValueType.BOOLEAN);
        wffBMArray.add("true");
        wffBMArray.getValueAsBoolean(0);
    }

}
