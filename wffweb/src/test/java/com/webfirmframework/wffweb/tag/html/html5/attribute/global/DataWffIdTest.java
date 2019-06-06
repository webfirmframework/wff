package com.webfirmframework.wffweb.tag.html.html5.attribute.global;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import com.webfirmframework.wffweb.tag.html.attribute.Value;
import com.webfirmframework.wffweb.tag.html.attribute.core.AttributeRegistry;

public class DataWffIdTest {

    @Test
    public void testGetAttrNameIndex() {
//        fail("Not yet implemented");        
        
//        {
//            Value value = new Value("S152");
//            assertEquals((int) AttributeRegistry.getIndexByAttributeName(value.getAttributeName()), value.getAttrNameIndex());
//            assertNotEquals(-1, value.getAttrNameIndex());
//        }
        
        {
            DataWffId dataWffId = new DataWffId("S152");            
            assertEquals((int) AttributeRegistry.getIndexByAttributeName(DataWffId.ATTRIBUTE_NAME), dataWffId.getAttrNameIndex());
            assertNotEquals(-1, dataWffId.getAttrNameIndex());
        }
    }

}
