package com.github.zhitron.universal;

import org.junit.Test;
import static org.junit.Assert.*;

public class AsciiWordTokenTest {

    @Test
    public void testOfLowerCamelCase() {
        AsciiWordToken token = AsciiWordToken.ofLowerCamelCase("helloWorld");
        assertEquals("helloWorld", token.toString());
        assertEquals("hello-world", token.toLowerKebabCase());
        assertEquals("HELLO_WORLD", token.toUpperUnderLine());
    }

    @Test
    public void testOfUpperCamelCase() {
        AsciiWordToken token = AsciiWordToken.ofUpperCamelCase("HelloWorld");
        assertEquals("HelloWorld", token.toString());
        assertEquals("helloWorld", token.toLowerCamelCase());
        assertEquals("HELLO-WORLD", token.toUpperKebabCase());
    }

    @Test
    public void testOfLowerUnderLine() {
        AsciiWordToken token = AsciiWordToken.ofLowerUnderLine("hello_world");
        assertEquals("helloWorld", token.toLowerCamelCase());
        assertEquals("hello_world", token.toLowerUnderLine());
        assertEquals("HelloWorld", token.toUpperCamelCase());
    }

    @Test
    public void testOfUpperUnderLine() {
        AsciiWordToken token = AsciiWordToken.ofUpperUnderLine("HELLO_WORLD");
        assertEquals("HELLO_WORLD", token.toString());
        assertEquals("helloWorld", token.toLowerCamelCase());
        assertEquals("hello-world", token.toLowerKebabCase());
    }

    @Test
    public void testOfLowerKebabCase() {
        AsciiWordToken token = AsciiWordToken.ofLowerKebabCase("hello-world");
        assertEquals("hello-world", token.toString());
        assertEquals("helloWorld", token.toLowerCamelCase());
        assertEquals("HELLO_WORLD", token.toUpperUnderLine());
    }

    @Test
    public void testOfUpperKebabCase() {
        AsciiWordToken token = AsciiWordToken.ofUpperKebabCase("HELLO-WORLD");
        assertEquals("HELLO-WORLD", token.toString());
        assertEquals("helloWorld", token.toLowerCamelCase());
        assertEquals("hello_world", token.toLowerUnderLine());
    }

    @Test
    public void testComplexNameConversion() {
        AsciiWordToken token = AsciiWordToken.of("XMLHttpRequest");
        assertEquals("xmlHttpRequest", token.toLowerCamelCase());
        assertEquals("XML_HTTP_REQUEST", token.toUpperUnderLine());
        assertEquals("xml-http-request", token.toLowerKebabCase());
    }

    @Test
    public void testSpecialCharacters() {
        AsciiWordToken token = AsciiWordToken.of("user_name-123");
        assertEquals("userName123", token.toLowerCamelCase());
        assertEquals("USER_NAME_123", token.toUpperUnderLine());
    }

    @Test
    public void testEmptyString() {
        AsciiWordToken token = AsciiWordToken.of("");
        assertEquals("", token.toString());
        assertEquals("", token.toLowerCamelCase());
    }

    @Test
    public void testNullString() {
        AsciiWordToken token = AsciiWordToken.of(null);
        assertEquals("", token.toString());
    }

    @Test
    public void testEqualsAndHashCode() {
        AsciiWordToken token1 = AsciiWordToken.of("HelloWorld");
        AsciiWordToken token2 = AsciiWordToken.of("hello-world");
        AsciiWordToken token3 = AsciiWordToken.of("HELLO_WORLD");

        assertEquals(token1, token2);
        assertEquals(token1, token3);
        assertEquals(token1.hashCode(), token2.hashCode());
        assertEquals(token1.hashCode(), token3.hashCode());
    }

    @Test
    public void testNotEquals() {
        AsciiWordToken token1 = AsciiWordToken.of("HelloWorld");
        AsciiWordToken token2 = AsciiWordToken.of("HelloWorld2");

        assertNotEquals(token1, token2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidCharacter() {
        AsciiWordToken.of("hello@world");
    }
}
