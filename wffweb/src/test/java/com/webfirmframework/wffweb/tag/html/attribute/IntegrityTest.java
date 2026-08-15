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
package com.webfirmframework.wffweb.tag.html.attribute;

import com.webfirmframework.wffweb.tag.html.html5.attribute.AutoComplete;
import com.webfirmframework.wffweb.util.StringUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class IntegrityTest {

    @Test
    public void testIntegrityString() {
        {
            Integrity integrity = new Integrity("sha256-NmUxNTFiMDUzZGIwZjcwZDIyYTc5NTA4ZmQyNT");
            assertEquals("integrity=\"sha256-NmUxNTFiMDUzZGIwZjcwZDIyYTc5NTA4ZmQyNT\"", integrity.toHtmlString());
            
        }
        {
            Integrity integrity = new Integrity("sha256-NmUxNTFiMDUzZGIwZjcwZDIyYTc5NTA4ZmQyNT sha384-Tk2Yjg3YmYzMWNkZTdhMTFkM2FlNDg4ZjE3MzEzNTk3ZDlh");
            assertEquals("integrity=\"sha256-NmUxNTFiMDUzZGIwZjcwZDIyYTc5NTA4ZmQyNT sha384-Tk2Yjg3YmYzMWNkZTdhMTFkM2FlNDg4ZjE3MzEzNTk3ZDlh\"", integrity.toHtmlString());
            
        }
        
    }

    @Test
    public void testIntegrityStringArray() {
        {
            Integrity integrity = new Integrity("sha256-NmUxNTFiMDUzZGIwZjcwZDIyYTc5NTA4ZmQyNT");
            assertEquals("integrity=\"sha256-NmUxNTFiMDUzZGIwZjcwZDIyYTc5NTA4ZmQyNT\"", integrity.toHtmlString());
            
        }
        {
            Integrity integrity = new Integrity("sha256-NmUxNTFiMDUzZGIwZjcwZDIyYTc5NTA4ZmQyNT", "sha384-Tk2Yjg3YmYzMWNkZTdhMTFkM2FlNDg4ZjE3MzEzNTk3ZDlh");
            assertEquals("integrity=\"sha256-NmUxNTFiMDUzZGIwZjcwZDIyYTc5NTA4ZmQyNT sha384-Tk2Yjg3YmYzMWNkZTdhMTFkM2FlNDg4ZjE3MzEzNTk3ZDlh\"", integrity.toHtmlString());
            
        }
        {
            Integrity integrity = new Integrity("sha256-NmUxNTFiMDUzZGIwZjcwZDIyYTc5NTA4ZmQyNT sha384-Tk2Yjg3YmYzMWNkZTdhMTFkM2FlNDg4ZjE3MzEzNTk3ZDlh sha512-OGUwYThkZDc2YzFlZGI5MDEzZmZhMGFkMGQ0OTQ3MzZkNGYZTEzODk2");
            assertEquals("integrity=\"sha256-NmUxNTFiMDUzZGIwZjcwZDIyYTc5NTA4ZmQyNT sha384-Tk2Yjg3YmYzMWNkZTdhMTFkM2FlNDg4ZjE3MzEzNTk3ZDlh sha512-OGUwYThkZDc2YzFlZGI5MDEzZmZhMGFkMGQ0OTQ3MzZkNGYZTEzODk2\"", integrity.toHtmlString());
            
        }
    }
    
    @Test
    public void testContains() {
        {
            Integrity integrity = new Integrity(AutoComplete.NAME);
            integrity.setValue(false, AutoComplete.NAME + " "
                    + AutoComplete.EMAIL + " " + AutoComplete.USERNAME);
            Assert.assertTrue(integrity.contains(AutoComplete.NAME));
            Assert.assertTrue(integrity.contains(AutoComplete.USERNAME));
            Assert.assertFalse(integrity.contains(AutoComplete.ADDRESS_LINE2));
        }
        {
            Integrity integrity = new Integrity("");
            Assert.assertFalse(integrity.contains(AutoComplete.ADDRESS_LINE2));
        }
    }
    
    @Test
    public void testContainsAll() {
        {
            Integrity integrity = new Integrity("");
            Assert.assertFalse(integrity.containsAll(Arrays.asList(AutoComplete.ADDRESS_LINE2)));
        }
        Integrity integrity = new Integrity(AutoComplete.NAME);
        integrity.setValue(false, AutoComplete.NAME + " "
                + AutoComplete.EMAIL + " " + AutoComplete.USERNAME);
        Assert.assertTrue(integrity.containsAll(Arrays.asList(AutoComplete.NAME, AutoComplete.USERNAME, AutoComplete.EMAIL)));
        Assert.assertTrue(integrity.containsAll(Arrays.asList(AutoComplete.USERNAME)));
        Assert.assertFalse(integrity.containsAll(Arrays.asList(AutoComplete.ADDRESS_LINE2)));
        Assert.assertFalse(integrity.containsAll(Arrays.asList(AutoComplete.NAME, AutoComplete.ADDRESS_LINE2)));
        
    }
    
    @Test
    public void testRemoveValues() {
        Integrity attribute = new Integrity("hash-1 hash-2 hash-3");
        assertEquals("integrity=\"hash-1 hash-2 hash-3\"", attribute.toHtmlString());
        attribute.removeValues(Arrays.asList("hash-1", "hash-3"));
        assertEquals("integrity=\"hash-2\"", attribute.toHtmlString());
    }
    
    @Test
    public void testRemoveAllValues() {
        
        Integrity attribute = new Integrity("one two");
        
        attribute.removeAllValues();
        Assert.assertEquals("", attribute.getAttributeValue());
        
        attribute.addValues(Arrays.asList(StringUtil.splitBySpace("one two three four five six")));
        
        Assert.assertEquals("one two three four five six", attribute.getAttributeValue());
        
        attribute.removeAllValues();
        Assert.assertEquals("", attribute.getAttributeValue());
    }

}
