package com.webfirmframework.wffweb.lang;

import static org.junit.Assert.*;

import org.junit.Test;

public class UnicodeStringTest {

    @Test
    public void testReplaceIntArrayString() {
//        fail("Not yet implemented");
    }

    @Test
    public void testReplaceStringString() {
//        fail("Not yet implemented");
        
    }
    
    @Test
    public void testSplitByAny() {
        assertArrayEquals(new UnicodeString[] {new UnicodeString("one"), new UnicodeString("two"), new UnicodeString("three"), new UnicodeString("")}, new UnicodeString("one;two;three;").splitByAny(new int[] {';'}));
        assertArrayEquals(new UnicodeString[] {new UnicodeString("one"), new UnicodeString("two"), new UnicodeString("three"), new UnicodeString("")}, new UnicodeString("one:two;three;").splitByAny(new int[] {';', ':'}));
    }
    
    @Test
    public void testIndexOf() {
        
        assertEquals(-1, UnicodeString.indexOf(null, null));
        assertEquals(-1, UnicodeString.indexOf(new int[2], null));
        assertEquals(-1, UnicodeString.indexOf(null, new int[2]));
        
        assertEquals(0, new UnicodeString("abcdefg").indexOf(new UnicodeString("ab")));
        
        assertEquals("abcdefg".indexOf("ab"), new UnicodeString("abcdefg").indexOf(new UnicodeString("ab")));
        
        assertEquals("abcdefg".indexOf("bc"), new UnicodeString("abcdefg").indexOf(new UnicodeString("bc")));
        
        assertEquals("abcdefg".indexOf("cd"), new UnicodeString("abcdefg").indexOf(new UnicodeString("cd")));
        
        assertEquals("abcdefg".indexOf("de"), new UnicodeString("abcdefg").indexOf(new UnicodeString("de")));
        
        assertEquals("abcdefg".indexOf("ef"), new UnicodeString("abcdefg").indexOf(new UnicodeString("ef")));
        
        assertEquals("abcdefg".indexOf("fg"), new UnicodeString("abcdefg").indexOf(new UnicodeString("fg")));
        
        assertEquals("abcdefg".indexOf("gh"), new UnicodeString("abcdefg").indexOf(new UnicodeString("gh")));
        
        
        assertEquals("abcdefg".indexOf("abcdefg"), new UnicodeString("abcdefg").indexOf(new UnicodeString("abcdefg")));
        
        assertEquals("abcdefg".indexOf("abcdefghi"), new UnicodeString("abcdefg").indexOf(new UnicodeString("abcdefghi")));
    }
    
    @Test
    public void testLastIndexOf() {
        
        assertEquals(-1, UnicodeString.lastIndexOf(null, null));
        assertEquals(-1, UnicodeString.lastIndexOf(new int[2], null));
        assertEquals(-1, UnicodeString.lastIndexOf(null, new int[2]));
        
        assertEquals(0, new UnicodeString("abcdefg").lastIndexOf(new UnicodeString("ab")));
        
        assertEquals("a".lastIndexOf("a"), new UnicodeString("a").lastIndexOf(new UnicodeString("a")));
        
        assertEquals("a".lastIndexOf("b"), new UnicodeString("a").lastIndexOf(new UnicodeString("b")));
        
        assertEquals("abcdefg".lastIndexOf("ab"), new UnicodeString("abcdefg").lastIndexOf(new UnicodeString("ab")));
        
        assertEquals("abcdefg".lastIndexOf("bc"), new UnicodeString("abcdefg").lastIndexOf(new UnicodeString("bc")));
        
        assertEquals("abcdefg".lastIndexOf("cd"), new UnicodeString("abcdefg").lastIndexOf(new UnicodeString("cd")));
        
        assertEquals("abcdefg".lastIndexOf("de"), new UnicodeString("abcdefg").lastIndexOf(new UnicodeString("de")));
        
        assertEquals("abcdefg".lastIndexOf("ef"), new UnicodeString("abcdefg").lastIndexOf(new UnicodeString("ef")));
        
        assertEquals("abcdefg".lastIndexOf("fg"), new UnicodeString("abcdefg").lastIndexOf(new UnicodeString("fg")));
        
        assertEquals("abcdefg".lastIndexOf("gh"), new UnicodeString("abcdefg").lastIndexOf(new UnicodeString("gh")));
        
        
        assertEquals("abcdefg".lastIndexOf("abcdefg"), new UnicodeString("abcdefg").lastIndexOf(new UnicodeString("abcdefg")));
        
        assertEquals("abcdefg".lastIndexOf("abcdefghi"), new UnicodeString("abcdefg").lastIndexOf(new UnicodeString("abcdefghi")));
        
        assertEquals("abcdabcdefghi".lastIndexOf("abcd"), new UnicodeString("abcdabcdefghi").lastIndexOf(new UnicodeString("abcd")));
    }
    
    @Test
    public void testStartsWith() {
        assertTrue(new UnicodeString("one").startsWith(new UnicodeString("one")));
        assertTrue(new UnicodeString("one1").startsWith(new UnicodeString("one")));
        assertFalse(new UnicodeString("on").startsWith(new UnicodeString("one")));
        assertFalse(new UnicodeString("one").startsWith(new UnicodeString("ane")));
        
        assertTrue(new UnicodeString("ones").startsWith(new UnicodeString("one")));
        assertFalse(new UnicodeString("abcd").startsWith(new UnicodeString("one")));
        
        assertEquals("".startsWith(""), new UnicodeString("").startsWith(new UnicodeString("")));
    }
    
    @Test
    public void testEndsWith() {
        assertTrue(new UnicodeString("one").endsWith(new UnicodeString("one")));
        assertTrue(new UnicodeString("1one").endsWith(new UnicodeString("one")));
        assertFalse(new UnicodeString("on").endsWith(new UnicodeString("one")));
        assertFalse(new UnicodeString("one").endsWith(new UnicodeString("ane")));
        assertTrue(new UnicodeString("").endsWith(new UnicodeString("")));
        
        assertTrue(new UnicodeString("sone").endsWith(new UnicodeString("one")));
        assertFalse(new UnicodeString("abcd").endsWith(new UnicodeString("one")));
        
        assertEquals("".endsWith(""), new UnicodeString("").endsWith(new UnicodeString("")));
    }
    
    @Test
    public void testSplitByCodePoint() {
        
        assertTrue(new UnicodeString("one").equals(new UnicodeString("one")));
        
        assertEquals(new UnicodeString((String) null), new UnicodeString((String) null));
        
        assertEquals(new UnicodeString(new int[0]), new UnicodeString(new int[0]));
        assertEquals(new UnicodeString("one"), new UnicodeString("one"));
        
        assertFalse(new UnicodeString("one1").equals(new UnicodeString("one")));
        
        assertArrayEquals(new UnicodeString[] {new UnicodeString("one"), new UnicodeString("two"), new UnicodeString("three")}, new UnicodeString("one/two/three").split("/".codePoints().toArray()[0]) );

        //FYI 
        //U+1F600
        String unicodeVal = "😀";
        assertEquals(2, unicodeVal.length());        
        assertEquals(2, unicodeVal.toCharArray().length);        
        assertEquals(1, unicodeVal.codePoints().toArray().length); 
        
        char c = unicodeVal.toCharArray()[0];
        
        assertEquals("a" + unicodeVal.toCharArray()[1], unicodeVal.replace(c, 'a'));
        
        assertTrue(unicodeVal.contains(c + ""));
        String valueContainingUnicode =  "one😀two😀three";
        
        
        //regex based String.split works well, it considers unicode        
        String[] stringRegExSplit = ("a" + unicodeVal).split("["+unicodeVal.toCharArray()[0]+"]");
        assertEquals(1, stringRegExSplit.length);
        assertEquals("a😀", stringRegExSplit[0]);        
        
        
        
        UnicodeString[] expected = {new UnicodeString("one"), new UnicodeString("two"), new UnicodeString("three")};
        
        int deli = Character.codePointAt(";", 0);
        
        assertEquals(new UnicodeString("one;two;three"), new UnicodeString("one;two;three").split( ':')[0]);
        
        assertEquals(new UnicodeString("without"), new UnicodeString("without").split(deli)[0]);
        
        assertEquals(new UnicodeString(""), new UnicodeString("").split(deli)[0]);
        
        assertEquals(new UnicodeString(""), new UnicodeString(";").split(deli)[0]);
        
        assertEquals(new UnicodeString("one"), new UnicodeString("one;two;three").split(deli)[0]);
        
        assertEquals(new UnicodeString("two"), new UnicodeString("one;two;three").split(deli)[1]);
        
        assertEquals(new UnicodeString("three"), new UnicodeString("one;two;three").split(deli)[2]);
        
        assertEquals(new UnicodeString(""), new UnicodeString(";two;three").split(deli)[0]);
        
        assertEquals(new UnicodeString(""), new UnicodeString("one;;three").split(deli)[1]);
        
        assertEquals(new UnicodeString(""), new UnicodeString("one;two;").split(deli)[2]);
        
        assertArrayEquals(new UnicodeString[] {new UnicodeString("one"), new UnicodeString("")}, new UnicodeString("one;").split(deli));
        
        assertArrayEquals(new UnicodeString[] {new UnicodeString(""), new UnicodeString("one")}, new UnicodeString(";one").split(deli));
        
        assertArrayEquals(new UnicodeString[] {new UnicodeString("one")}, new UnicodeString("one").split(deli));
        
        assertArrayEquals(new UnicodeString[] {new UnicodeString("")}, new UnicodeString("").split(deli));
        
        assertArrayEquals(new UnicodeString[] {new UnicodeString(""), new UnicodeString("two"), new UnicodeString("three"), new UnicodeString("")}, new UnicodeString(";two;three;").split(deli));
        
        assertArrayEquals(new UnicodeString[] {new UnicodeString(""), new UnicodeString(""), new UnicodeString("three"), new UnicodeString("")}, new UnicodeString(";;three;").split(deli));
        
        assertArrayEquals(new UnicodeString[] {new UnicodeString(""), new UnicodeString(""), new UnicodeString(""), new UnicodeString("")}, new UnicodeString(";;;").split(deli));
        
        assertArrayEquals(expected, new UnicodeString("one;two;three").split(deli));
        
        assertArrayEquals(new UnicodeString[] {new UnicodeString("one"), new UnicodeString("two"), new UnicodeString("three"), new UnicodeString("")}, new UnicodeString("one;two;three;").split(deli));
        
        assertArrayEquals(new UnicodeString[] {new UnicodeString(" one "), new UnicodeString(" two "), new UnicodeString(" three "), new UnicodeString(" ")}, new UnicodeString(" one ; two ; three ; ").split(deli));
        
        deli = ';';
        
        assertArrayEquals(expected, new UnicodeString("one;two;three").split(deli));
        
        assertArrayEquals(new UnicodeString[] {new UnicodeString("one"), new UnicodeString("two"), new UnicodeString("three"), new UnicodeString("")}, new UnicodeString("one;two;three;").split(deli));
        
        assertArrayEquals(new UnicodeString[] {new UnicodeString(" one "), new UnicodeString(" two "), new UnicodeString(" three "), new UnicodeString(" ")}, new UnicodeString(" one ; two ; three ; ").split(deli));
        
        deli = unicodeVal.codePointAt(0);
        
        assertEquals(new UnicodeString("without"), new UnicodeString("without").split(deli)[0]);
        
        assertEquals(new UnicodeString(""), new UnicodeString("").split(deli)[0]);
        
        assertEquals(new UnicodeString(""), new UnicodeString("😀").split(deli)[0]);
        
        assertEquals(new UnicodeString("one"), new UnicodeString(valueContainingUnicode).split(deli)[0]);
        
        assertEquals(new UnicodeString("two"), new UnicodeString(valueContainingUnicode).split(deli)[1]);
        
        assertEquals(new UnicodeString("three"), new UnicodeString(valueContainingUnicode).split(deli)[2]);
        
        assertEquals(new UnicodeString(""), new UnicodeString("😀two😀three").split(deli)[0]);
        
        assertEquals(new UnicodeString(""), new UnicodeString("one😀😀three").split(deli)[1]);
        
        assertEquals(new UnicodeString(""), new UnicodeString("one😀two😀").split(deli)[2]);
        
        assertArrayEquals(new UnicodeString[] {new UnicodeString("one"), new UnicodeString("")}, new UnicodeString("one😀").split(deli));
        
        assertArrayEquals(new UnicodeString[] {new UnicodeString(""), new UnicodeString("one")}, new UnicodeString("😀one").split(deli));
        
        assertArrayEquals(new UnicodeString[] {new UnicodeString("one")}, new UnicodeString("one").split(deli));
        
        assertArrayEquals(new UnicodeString[] {new UnicodeString("")}, new UnicodeString("").split(deli));
        
        assertArrayEquals(new UnicodeString[] {new UnicodeString(""), new UnicodeString("two"), new UnicodeString("three"), new UnicodeString("")}, new UnicodeString("😀two😀three😀").split(deli));
        
        assertArrayEquals(new UnicodeString[] {new UnicodeString(""), new UnicodeString(""), new UnicodeString("three"), new UnicodeString("")}, new UnicodeString("😀😀three😀").split(deli));
        
        assertArrayEquals(new UnicodeString[] {new UnicodeString(""), new UnicodeString(""), new UnicodeString(""), new UnicodeString("")}, new UnicodeString("😀😀😀").split(deli));
        
        assertArrayEquals(new UnicodeString[] {new UnicodeString("one"), new UnicodeString("two"), new UnicodeString("three"), new UnicodeString("")}, new UnicodeString("one😀two😀three😀").split(deli));
        
        assertArrayEquals(new UnicodeString[] {new UnicodeString(" one "), new UnicodeString(" two "), new UnicodeString(" three "), new UnicodeString(" ")}, new UnicodeString(" one 😀 two 😀 three 😀 ").split(deli));
        
        assertEquals(new UnicodeString("one"), new UnicodeString(valueContainingUnicode).split(deli)[0]);
        
        assertEquals(new UnicodeString("two"), new UnicodeString(valueContainingUnicode).split(deli)[1]);
        
        assertEquals(new UnicodeString("three"), new UnicodeString(valueContainingUnicode).split(deli)[2]);
        
        assertArrayEquals(expected, new UnicodeString(valueContainingUnicode).split(deli));       
        
        assertArrayEquals(new UnicodeString[] {new UnicodeString("one"), new UnicodeString("two"), new UnicodeString("three"), new UnicodeString("")}, new UnicodeString("one😀two😀three😀").split(deli));
        
        assertArrayEquals(new UnicodeString[] {new UnicodeString(" one "), new UnicodeString(" two "), new UnicodeString(" three "), new UnicodeString(" ")}, new UnicodeString(" one 😀 two 😀 three 😀 ").split(deli));
    }
    
    @Test
    public void testStrip() {
        assertEquals("", new UnicodeString("").strip().newString());
        assertEquals("one", new UnicodeString("one").strip().newString());
        assertEquals("one", new UnicodeString(" one ").strip().newString());
        assertEquals("o n e", new UnicodeString(" o n e ").strip().newString());
        assertEquals("one", new UnicodeString("    one      ").strip().newString());
        assertEquals("one", new UnicodeString("\none\n").strip().newString());
        assertEquals("one", new UnicodeString("\r\none\r\n").strip().newString());
        assertEquals("one", new UnicodeString("\rone\r").strip().newString());
        assertEquals("one", new UnicodeString("\t\tone\t\t\t").strip().newString());
        assertEquals("_", new UnicodeString("\t\t_\t\t\t").strip().newString());
        assertEquals("", new UnicodeString(" \t\r\r\n  ").strip().newString());
    }
    
    @Test
    public void testLength() {
        String s = "\u0048\u0065\u006C\u006C\u006F World";
        assertEquals(11, s.length());
        
        assertEquals(11, new UnicodeString(s).length());
        
        assertEquals(5, "😀bcd".length());
        
        assertEquals(4, new UnicodeString("😀bcd").length());
    }
    
    @Test
    public void testSubstring1() {
        assertEquals("bc", new UnicodeString("abcd").substring(1, 3).newString());
        assertEquals("abcd", new UnicodeString("abcd").substring(0, 4).newString());
        
        assertEquals("bc", new UnicodeString("😀bcd").substring(1, 3).newString());
        assertEquals("😀bcd", new UnicodeString("😀bcd").substring(0, 4).newString());
        
        assertEquals("bc", new UnicodeString("😀bc😀").substring(1, 3).newString());
        assertEquals("😀bc😀", new UnicodeString("😀bc😀").substring(0, 4).newString());
        
    }
    
    @Test
    public void testSubstring2() {
        assertEquals("abcd", new UnicodeString("abcd").substring(0).newString());
        assertEquals("bcd", new UnicodeString("abcd").substring(1).newString());
        assertEquals("cd", new UnicodeString("abcd").substring(2).newString());
        assertEquals("d", new UnicodeString("abcd").substring(3).newString());
        
        assertEquals("😀bc😀", new UnicodeString("😀bc😀").substring(0).newString());
        assertEquals("bc😀", new UnicodeString("😀bc😀").substring(1).newString());
        assertEquals("c😀", new UnicodeString("😀bc😀").substring(2).newString());
        assertEquals("😀", new UnicodeString("😀bc😀").substring(3).newString());
    }

    @Test
    public void testReplaceIntArrayIntArrayIntArray() {
        assertEquals("12cdefg", UnicodeString.replace("abcdefg".codePoints().toArray(), "ab".codePoints().toArray(), "12".codePoints().toArray()).newString());
        assertEquals("abcdefg", UnicodeString.replace("abcdefg".codePoints().toArray(), "ab".codePoints().toArray(), "ab".codePoints().toArray()).newString());
        assertEquals("abcdefgcdefg", UnicodeString.replace("abcdefg".codePoints().toArray(), "ab".codePoints().toArray(), "abcdefg".codePoints().toArray()).newString());
        assertEquals("abcdefg", UnicodeString.replace("abcdefg".codePoints().toArray(), "abcdefg".codePoints().toArray(), "abcdefg".codePoints().toArray()).newString());
        assertEquals("abcdefg", UnicodeString.replace("abcdefg".codePoints().toArray(), "abcdefgh".codePoints().toArray(), "a".codePoints().toArray()).newString());
        assertEquals("abcdefg", UnicodeString.replace("abcdefg".codePoints().toArray(), "abcdefghi".codePoints().toArray(), "abcdefghi".codePoints().toArray()).newString());
        assertEquals("a", UnicodeString.replace("abcdefg".codePoints().toArray(), "abcdefg".codePoints().toArray(), "a".codePoints().toArray()).newString());
        assertEquals("a", UnicodeString.replace("a".codePoints().toArray(), "abcdefg".codePoints().toArray(), "b".codePoints().toArray()).newString());
        assertEquals("c", UnicodeString.replace("a".codePoints().toArray(), "a".codePoints().toArray(), "c".codePoints().toArray()).newString());
        assertEquals("abcdefg", UnicodeString.replace("a".codePoints().toArray(), "a".codePoints().toArray(), "abcdefg".codePoints().toArray()).newString());

        assertEquals("abcdefg", UnicodeString.replace("😀".codePoints().toArray(), "😀".codePoints().toArray(), "abcdefg".codePoints().toArray()).newString());

        assertEquals("ab1cd", UnicodeString.replace("ab😀cd".codePoints().toArray(), "😀".codePoints().toArray(), "1".codePoints().toArray()).newString());

        assertEquals("😀😀😀😀😀😀😀", UnicodeString.replace("aaaaaaa".codePoints().toArray(), "a".codePoints().toArray(), "😀".codePoints().toArray()).newString());

        assertEquals("bcdefg", UnicodeString.replace("abcdefg".codePoints().toArray(), "a".codePoints().toArray(), "".codePoints().toArray()).newString());
        assertEquals("ab", UnicodeString.replace("abc".codePoints().toArray(), "c".codePoints().toArray(), "".codePoints().toArray()).newString());
        assertEquals("", UnicodeString.replace("a".codePoints().toArray(), "a".codePoints().toArray(), "".codePoints().toArray()).newString());
        assertEquals("", UnicodeString.replace("ab".codePoints().toArray(), "ab".codePoints().toArray(), "".codePoints().toArray()).newString());
        assertEquals("", UnicodeString.replace("abc".codePoints().toArray(), "abc".codePoints().toArray(), "".codePoints().toArray()).newString());
        assertEquals("bc", UnicodeString.replace("abc".codePoints().toArray(), "a".codePoints().toArray(), "".codePoints().toArray()).newString());
        assertEquals("c", UnicodeString.replace("bc".codePoints().toArray(), "b".codePoints().toArray(), "".codePoints().toArray()).newString());

        assertNull(UnicodeString.replace(null, "b".codePoints().toArray(), "abcd".codePoints().toArray()).newString());
        assertEquals("abc", UnicodeString.replace("abc".codePoints().toArray(), (int[]) null, "".codePoints().toArray()).newString());
        assertEquals("abc", UnicodeString.replace("abc".codePoints().toArray(), "b".codePoints().toArray(), null).newString());
        assertEquals("green ", UnicodeString.replace("green !important".codePoints().toArray(), "!important".codePoints().toArray(), "".codePoints().toArray()).newString());
        assertEquals(" green", UnicodeString.replace("!important green".codePoints().toArray(), "!important".codePoints().toArray(), "".codePoints().toArray()).newString());
        
        assertEquals("aaaaaaa", UnicodeString.replace("😀aaaaaaa😀".codePoints().toArray(), "😀".codePoints().toArray(), "".codePoints().toArray()).newString());
        assertEquals("aaaaaaa", UnicodeString.replace("baaaaaaab".codePoints().toArray(), "b".codePoints().toArray(), "".codePoints().toArray()).newString());
        assertEquals("aaaaaaa", UnicodeString.replace("baaaaaaa".codePoints().toArray(), "b".codePoints().toArray(), "".codePoints().toArray()).newString());
        assertEquals("aaaaaaa", UnicodeString.replace("aaaaaaab".codePoints().toArray(), "b".codePoints().toArray(), "".codePoints().toArray()).newString());
        assertEquals("aaaaaaa", UnicodeString.replace("aaaaaaa".codePoints().toArray(), "b".codePoints().toArray(), "".codePoints().toArray()).newString());
        assertEquals("", UnicodeString.replace("".codePoints().toArray(), "a".codePoints().toArray(), "b".codePoints().toArray()).newString());
        assertEquals("b", UnicodeString.replace("".codePoints().toArray(), "".codePoints().toArray(), "b".codePoints().toArray()).newString());
        assertEquals("", UnicodeString.replace("".codePoints().toArray(), "".codePoints().toArray(), "".codePoints().toArray()).newString());
    }

}
