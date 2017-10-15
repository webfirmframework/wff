/*
 * Copyright 2014-2017 Web Firm Framework
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

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

public class WffBMObjectArrayTest {

    @Test
    public void testWffBMObject() {

        try {
            WffBMObject outerBMObject = new WffBMObject();
            WffBMArray bmArrayInOuterBMObject = new WffBMArray(
                    BMValueType.BM_OBJECT);
            WffBMObject bmObjectInArray = new WffBMObject();
            bmObjectInArray.put("k", BMValueType.STRING, "v");

            bmArrayInOuterBMObject.add(bmObjectInArray);
            outerBMObject.put("r", BMValueType.BM_ARRAY,
                    bmArrayInOuterBMObject);

            final WffBMObject parsed = new WffBMObject(
                    outerBMObject.build(true), true);

            WffBMArray parsedArray = (WffBMArray) parsed.getValue("r");
            final WffBMObject arrayObj = (WffBMObject) parsedArray.get(0);

            assertEquals("v", arrayObj.getValue("k"));
            
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception " + e.getMessage());
        }

    }

}
