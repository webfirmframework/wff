package com.webfirmframework.wffweb.wffbm.data;

import com.webfirmframework.wffweb.InvalidValueException;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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
    public void testGetValueAsBigDecimalDeserialzed() {
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

        BigDecimal valueForNumberString = wffBMObject.getValueAsBigDecimal(keyForNumberString);
        assertEquals(numberString, valueForNumberString.stripTrailingZeros().toPlainString());

        BigDecimal valueForNumberPrimitive1 = wffBMObject.getValueAsBigDecimal(keyForNumberPrimitive1);
        assertEquals(String.valueOf(numberPrimitive1), valueForNumberPrimitive1.stripTrailingZeros().toPlainString());

        BigDecimal valueForNumberPrimitive2 = wffBMObject.getValueAsBigDecimal(keyForNumberPrimitive2);
        assertEquals(String.valueOf(numberPrimitive2), valueForNumberPrimitive2.stripTrailingZeros().toPlainString());

        BigDecimal valueForNumberPrimitive3 = wffBMObject.getValueAsBigDecimal(keyForNumberPrimitive3);
        assertEquals(String.valueOf(numberPrimitive3), valueForNumberPrimitive3.stripTrailingZeros().toPlainString());

        //TODO review this later
        BigDecimal valueForNumberPrimitive4 = wffBMObject.getValueAsBigDecimal(keyForNumberPrimitive4).setScale(2, RoundingMode.DOWN);
        assertEquals(String.valueOf(numberPrimitive4), valueForNumberPrimitive4.stripTrailingZeros().toPlainString());
    }

    @Test(expected = InvalidValueException.class)
    public void testGetValueAsBigDecimalInvalidValueException() {
        final WffBMObject wffBMObject = new WffBMObject();
        final String keyForRegex = "keyForNumberString";
        final String regex = "*";
        wffBMObject.put(keyForRegex, BMValueType.REG_EXP, regex);
        //exception is expected
        wffBMObject.getValueAsBigDecimal(keyForRegex);
    }
}
