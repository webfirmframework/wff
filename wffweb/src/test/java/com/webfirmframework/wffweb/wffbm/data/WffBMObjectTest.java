package com.webfirmframework.wffweb.wffbm.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.junit.Test;

import com.webfirmframework.wffweb.InvalidValueException;

public class WffBMObjectTest {

    @Test
    public void testGetValueAsBigDecimal() {
        final WffBMObject wffBMObject = new WffBMObject();

        final String keyForNumberString = "keyForNumberString";
        final String numberString = "14.01";
        wffBMObject.put(keyForNumberString, BMValueType.STRING, numberString);
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

        wffBMObject = new WffBMObject(wffBMObject.buildBytes(true));

        final BigDecimal valueForNumberString = wffBMObject.getValueAsBigDecimal(keyForNumberString);
        assertEquals(numberString, valueForNumberString.stripTrailingZeros().toPlainString());

        final BigDecimal valueForNumberPrimitive1 = wffBMObject.getValueAsBigDecimal(keyForNumberPrimitive1);
        assertEquals(String.valueOf(numberPrimitive1), valueForNumberPrimitive1.stripTrailingZeros().toPlainString());

        final BigDecimal valueForNumberPrimitive2 = wffBMObject.getValueAsBigDecimal(keyForNumberPrimitive2);
        assertEquals(String.valueOf(numberPrimitive2), valueForNumberPrimitive2.stripTrailingZeros().toPlainString());

        final BigDecimal valueForNumberPrimitive3 = wffBMObject.getValueAsBigDecimal(keyForNumberPrimitive3);
        assertEquals(String.valueOf(numberPrimitive3), valueForNumberPrimitive3.stripTrailingZeros().toPlainString());

        // TODO review this later
        final BigDecimal valueForNumberPrimitive4 = wffBMObject.getValueAsBigDecimal(keyForNumberPrimitive4).setScale(2,
                RoundingMode.DOWN);
        assertEquals(String.valueOf(numberPrimitive4), valueForNumberPrimitive4.stripTrailingZeros().toPlainString());
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

        final String keyForNullNumber = "keyForNullNumber";
        wffBMObject.put(keyForNullNumber, BMValueType.NUMBER, null);
        valueAsLong = wffBMObject.getValueAsLong(keyForNullNumber);
        assertNull(valueAsLong);

        final String keyForNullType = "keyForNullType";
        wffBMObject.put(keyForNullType, BMValueType.NULL, null);
        valueAsLong = wffBMObject.getValueAsLong(keyForNullType);
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

//    ----------------------------

    @Test
    public void testGetValueAsBoolean() {
        final WffBMObject wffBMObject = new WffBMObject();

        final String keyForNumberString = "keyForNumberString";
        final String numberString = "true";
        wffBMObject.put(keyForNumberString, BMValueType.STRING, numberString);
        Boolean valueAsBoolean = wffBMObject.getValueAsBoolean(keyForNumberString);
        assertEquals(numberString, valueAsBoolean.toString());

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

}
