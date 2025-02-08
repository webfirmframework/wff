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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.junit.Test;

import com.webfirmframework.wffweb.MethodNotImplementedException;
import com.webfirmframework.wffweb.util.NumberUtil;

public class WffBMNumberArrayTest {

    @Test(expected = MethodNotImplementedException.class)
    public void testGetValueAsRestrictedMethods() {
        final WffBMNumberArray<BigDecimal> bmNumberArray = new WffBMNumberArray<>(true);
        bmNumberArray.add(new BigDecimal("14.01"));
        bmNumberArray.getValueAsBoolean(0);
    }

    @Test
    public void testToSteam() {
        final WffBMNumberArray<BigDecimal> bmNumberArray = new WffBMNumberArray<>(true);
        final BigDecimal value1 = new BigDecimal("14.01");
        bmNumberArray.add(value1);
        bmNumberArray.add(new BigDecimal("14.011"));
        bmNumberArray.add(new BigDecimal("14.0119"));
        final long count = bmNumberArray.toStream().filter(each -> NumberUtil.isGreaterThan(each, value1)).count();
        assertEquals(2, count);
    }

    @Test
    public void testAddFloat() {
        final WffBMNumberArray<BigDecimal> bmNumberArray = new WffBMNumberArray<>(true);
        final float value1 = 14.01F;
        final float value2 = 14.011F;
        final float value3 = 14.0119F;
        assertTrue(bmNumberArray.addNumber(value1));
        bmNumberArray.add(value2);
        assertTrue(bmNumberArray.addNumber(value3));

        final Double value1AsDouble = bmNumberArray.getValueAsDouble(0);
        final Double value2AsDouble = bmNumberArray.getValueAsDouble(1);
        final Float value3AsFloat = bmNumberArray.getValueAsFloat(2);

        assertEquals(((Number) value1).toString(), value1AsDouble.toString());
        assertNotEquals(((Number) value2).toString(), value2AsDouble.toString());
        assertEquals(((Number) value3).toString(), value3AsFloat.toString());

        bmNumberArray.addNumberFirst(value2);
        Double firstValueAsDouble = bmNumberArray.getValueAsDouble(0);
        assertEquals(((Number) value2).toString(), firstValueAsDouble.toString());

        bmNumberArray.addNumberLast(value1);
        Double valueLastAsDouble = bmNumberArray.getValueAsDouble(bmNumberArray.size() - 1);
        assertEquals(((Number) value1).toString(), valueLastAsDouble.toString());

        bmNumberArray.addNumber(0, value1);
        firstValueAsDouble = bmNumberArray.getValueAsDouble(0);
        assertEquals(((Number) value1).toString(), firstValueAsDouble.toString());

        bmNumberArray.addNumber(bmNumberArray.size() - 1, value1);
        valueLastAsDouble = bmNumberArray.getValueAsDouble(bmNumberArray.size() - 1);
        assertEquals(((Number) value1).toString(), valueLastAsDouble.toString());
    }

}
