package com.webfirmframework.wffweb.settings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class WffConfigurationTest {

    @Test
    public void testParseFirstDigits() {
        String actual = WffConfiguration.parseFirstDigits("1234567890");
        assertEquals("1234567890", actual);

        actual = WffConfiguration.parseFirstDigits("1234567890.5555");
        assertEquals("1234567890", actual);

        actual = WffConfiguration.parseFirstDigits("1234567890 5555");
        assertEquals("1234567890", actual);

        actual = WffConfiguration.parseFirstDigits("1234567890_5555");
        assertEquals("1234567890", actual);

        actual = WffConfiguration.parseFirstDigits("1234567890-5555");
        assertEquals("1234567890", actual);

        actual = WffConfiguration.parseFirstDigits("1234567890A5555");
        assertEquals("1234567890", actual);

        actual = WffConfiguration.parseFirstDigits(".1234567890A5555");
        assertEquals("", actual);
    }
    
    @Test
    public void testWffConfiguration() {
        final int javaSpecVersion = Integer
                .parseInt(WffConfiguration.parseFirstDigits(System.getProperty("java.vm.specification.version", "17")));
        
        if (javaSpecVersion < 19) {
            assertNull(WffConfiguration.getVirtualThreadExecutor());
        } else if (javaSpecVersion >= 21) {
            assertNotNull(WffConfiguration.getVirtualThreadExecutor());
        }
    }
    
    

}
