package com.webfirmframework.wffweb.wffbm.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.junit.Test;

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
    }

    @Test
    public void testGetValueAsIntegerDeserialized() {
        WffBMObject wffBMObject = new WffBMObject();

        final String keyForNumberPrimitive2 = "keyForNumberPrimitive2";
        final long numberPrimitive2 = 1401;
        wffBMObject.put(keyForNumberPrimitive2, BMValueType.NUMBER, numberPrimitive2);

        final String keyForNumberPrimitive3 = "keyForNumberPrimitive3";
        final int numberPrimitive3 = 1401;
        wffBMObject.put(keyForNumberPrimitive3, BMValueType.NUMBER, numberPrimitive3);

        wffBMObject = new WffBMObject(wffBMObject.buildBytes(true));

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
    }

    @Test
    public void testGetValueAsLongDeserialized() {
        WffBMObject wffBMObject = new WffBMObject();

        final String keyForNumberPrimitive2 = "keyForNumberPrimitive2";
        final long numberPrimitive2 = 1401;
        wffBMObject.put(keyForNumberPrimitive2, BMValueType.NUMBER, numberPrimitive2);

        final String keyForNumberPrimitive3 = "keyForNumberPrimitive3";
        final int numberPrimitive3 = 1401;
        wffBMObject.put(keyForNumberPrimitive3, BMValueType.NUMBER, numberPrimitive3);

        wffBMObject = new WffBMObject(wffBMObject.buildBytes(true));

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
}
