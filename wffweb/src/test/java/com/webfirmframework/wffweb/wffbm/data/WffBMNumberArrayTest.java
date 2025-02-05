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

}
