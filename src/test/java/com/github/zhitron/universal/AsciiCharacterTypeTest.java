package com.github.zhitron.universal;

import org.junit.Test;
import static org.junit.Assert.*;

public class AsciiCharacterTypeTest {

    @Test
    public void testControlCharacter() {
        // 测试控制字符范围 (0-31, 127)
        assertTrue(AsciiCharacterType.CONTROL.isMatch(0));
        assertTrue(AsciiCharacterType.CONTROL.isMatch(31));
        assertTrue(AsciiCharacterType.CONTROL.isMatch(127));
        assertFalse(AsciiCharacterType.CONTROL.isMatch(32));
        assertFalse(AsciiCharacterType.CONTROL.isMatch(126));
    }

    @Test
    public void testDeviceCharacter() {
        // 测试设备符号 (17-20)
        assertTrue(AsciiCharacterType.DEVICE.isMatch(17));
        assertTrue(AsciiCharacterType.DEVICE.isMatch(20));
        assertFalse(AsciiCharacterType.DEVICE.isMatch(16));
        assertFalse(AsciiCharacterType.DEVICE.isMatch(21));
    }

    @Test
    public void testInvisibleCharacter() {
        // 测试不可见字符 ('\b', '\f', '\r', '\n', '\t', ' ')
        assertTrue(AsciiCharacterType.INVISIBLE.isMatch('\b'));
        assertTrue(AsciiCharacterType.INVISIBLE.isMatch('\f'));
        assertTrue(AsciiCharacterType.INVISIBLE.isMatch('\r'));
        assertTrue(AsciiCharacterType.INVISIBLE.isMatch('\n'));
        assertTrue(AsciiCharacterType.INVISIBLE.isMatch('\t'));
        assertTrue(AsciiCharacterType.INVISIBLE.isMatch(' '));
        assertFalse(AsciiCharacterType.INVISIBLE.isMatch('a'));
    }

    @Test
    public void testLineBreaksCharacter() {
        // 测试换行字符 ('\r', '\n')
        assertTrue(AsciiCharacterType.LINE_BREAKS.isMatch('\r'));
        assertTrue(AsciiCharacterType.LINE_BREAKS.isMatch('\n'));
        assertFalse(AsciiCharacterType.LINE_BREAKS.isMatch('\t'));
    }

    @Test
    public void testBlankCharacter() {
        // 测试空白字符 ('\t', ' ')
        assertTrue(AsciiCharacterType.BLANK.isMatch('\t'));
        assertTrue(AsciiCharacterType.BLANK.isMatch(' '));
        assertFalse(AsciiCharacterType.BLANK.isMatch('\n'));
    }

    @Test
    public void testLetterCharacter() {
        // 测试字母 (A-Z, a-z)
        for (char c = 'A'; c <= 'Z'; c++) {
            assertTrue(AsciiCharacterType.LETTER.isMatch(c));
        }
        for (char c = 'a'; c <= 'z'; c++) {
            assertTrue(AsciiCharacterType.LETTER.isMatch(c));
        }
        assertFalse(AsciiCharacterType.LETTER.isMatch('0'));
        assertFalse(AsciiCharacterType.LETTER.isMatch('@'));
    }

    @Test
    public void testUpperLetterCharacter() {
        // 测试大写字母 (A-Z)
        for (char c = 'A'; c <= 'Z'; c++) {
            assertTrue(AsciiCharacterType.UPPER_LETTER.isMatch(c));
        }
        for (char c = 'a'; c <= 'z'; c++) {
            assertFalse(AsciiCharacterType.UPPER_LETTER.isMatch(c));
        }
    }

    @Test
    public void testLowerLetterCharacter() {
        // 测试小写字母 (a-z)
        for (char c = 'a'; c <= 'z'; c++) {
            assertTrue(AsciiCharacterType.LOWER_LETTER.isMatch(c));
        }
        for (char c = 'A'; c <= 'Z'; c++) {
            assertFalse(AsciiCharacterType.LOWER_LETTER.isMatch(c));
        }
    }

    @Test
    public void testNumberCharacter() {
        // 测试数字字符 (0-9, '+', '-', '.')
        for (char c = '0'; c <= '9'; c++) {
            assertTrue(AsciiCharacterType.NUMBER.isMatch(c));
        }
        assertTrue(AsciiCharacterType.NUMBER.isMatch('+'));
        assertTrue(AsciiCharacterType.NUMBER.isMatch('-'));
        assertTrue(AsciiCharacterType.NUMBER.isMatch('.'));
        assertFalse(AsciiCharacterType.NUMBER.isMatch('a'));
    }

    @Test
    public void testNumDecCharacter() {
        // 测试十进制数字 (0-9)
        for (char c = '0'; c <= '9'; c++) {
            assertTrue(AsciiCharacterType.NUM_DEC.isMatch(c));
        }
        assertFalse(AsciiCharacterType.NUM_DEC.isMatch('a'));
        assertFalse(AsciiCharacterType.NUM_DEC.isMatch('+'));
    }

    @Test
    public void testNumHexCharacter() {
        // 测试十六进制数字 (0-9, a-f, A-F)
        for (char c = '0'; c <= '9'; c++) {
            assertTrue(AsciiCharacterType.NUM_HEX.isMatch(c));
        }
        for (char c = 'a'; c <= 'f'; c++) {
            assertTrue(AsciiCharacterType.NUM_HEX.isMatch(c));
        }
        for (char c = 'A'; c <= 'F'; c++) {
            assertTrue(AsciiCharacterType.NUM_HEX.isMatch(c));
        }
        assertFalse(AsciiCharacterType.NUM_HEX.isMatch('g'));
        assertFalse(AsciiCharacterType.NUM_HEX.isMatch('G'));
    }

    @Test
    public void testNumOctCharacter() {
        // 测试八进制数字 (0-7)
        for (char c = '0'; c <= '7'; c++) {
            assertTrue(AsciiCharacterType.NUM_OCT.isMatch(c));
        }
        assertFalse(AsciiCharacterType.NUM_OCT.isMatch('8'));
        assertFalse(AsciiCharacterType.NUM_OCT.isMatch('9'));
    }

    @Test
    public void testPunctuationCharacter() {
        // 测试标点符号 (!"#$%&'()*+,-./:;<=>?@[\]^_`{|}~)
        String punctuations = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";
        for (int i = 0; i < punctuations.length(); i++) {
            assertTrue(AsciiCharacterType.PUNCTUATION.isMatch(punctuations.charAt(i)));
        }
        assertFalse(AsciiCharacterType.PUNCTUATION.isMatch('A'));
        assertFalse(AsciiCharacterType.PUNCTUATION.isMatch('0'));
    }

    @Test
    public void testArithmeticCharacter() {
        // 测试四则运算符号 (/+-*)
        assertTrue(AsciiCharacterType.ARITHMETIC.isMatch('+'));
        assertTrue(AsciiCharacterType.ARITHMETIC.isMatch('-'));
        assertTrue(AsciiCharacterType.ARITHMETIC.isMatch('*'));
        assertTrue(AsciiCharacterType.ARITHMETIC.isMatch('/'));
        assertFalse(AsciiCharacterType.ARITHMETIC.isMatch('a'));
    }

    @Test
    public void testComparisonCharacter() {
        // 测试比较操作符号 (< > =)
        assertTrue(AsciiCharacterType.COMPARISON.isMatch('<'));
        assertTrue(AsciiCharacterType.COMPARISON.isMatch('>'));
        assertTrue(AsciiCharacterType.COMPARISON.isMatch('='));
        assertFalse(AsciiCharacterType.COMPARISON.isMatch('!'));
    }

    @Test
    public void testBracketsCharacter() {
        // 测试括号符号 (<>()[]{})
        String brackets = "<>()[]{}";
        for (int i = 0; i < brackets.length(); i++) {
            assertTrue(AsciiCharacterType.BRACKETS.isMatch(brackets.charAt(i)));
        }
        assertFalse(AsciiCharacterType.BRACKETS.isMatch('a'));
    }

    @Test
    public void testLeftBracketsCharacter() {
        // 测试左括号符号 (<([{)
        String leftBrackets = "<([{";
        for (int i = 0; i < leftBrackets.length(); i++) {
            assertTrue(AsciiCharacterType.LEFT_BRACKETS.isMatch(leftBrackets.charAt(i)));
        }
        assertFalse(AsciiCharacterType.LEFT_BRACKETS.isMatch(')'));
    }

    @Test
    public void testRightBracketsCharacter() {
        // 测试右括号符号 (>)]})
        String rightBrackets = ">)]}";
        for (int i = 0; i < rightBrackets.length(); i++) {
            assertTrue(AsciiCharacterType.RIGHT_BRACKETS.isMatch(rightBrackets.charAt(i)));
        }
        assertFalse(AsciiCharacterType.RIGHT_BRACKETS.isMatch('('));
    }

    @Test
    public void testPathSeparatorCharacter() {
        // 测试路径分隔符 (/\)
        assertTrue(AsciiCharacterType.PATH_SEPARATOR.isMatch('/'));
        assertTrue(AsciiCharacterType.PATH_SEPARATOR.isMatch('\\'));
        assertFalse(AsciiCharacterType.PATH_SEPARATOR.isMatch('a'));
    }

    @Test
    public void testWhitespaceCharacter() {
        // 测试空白字符 ('\t', '\n', '\r', ' ')
        assertTrue(AsciiCharacterType.WHITESPACE.isMatch('\t'));
        assertTrue(AsciiCharacterType.WHITESPACE.isMatch('\n'));
        assertTrue(AsciiCharacterType.WHITESPACE.isMatch('\r'));
        assertTrue(AsciiCharacterType.WHITESPACE.isMatch(' '));
        assertFalse(AsciiCharacterType.WHITESPACE.isMatch('a'));
    }

    @Test
    public void testSymbolCharacter() {
        // 测试符号字符（非字母、非数字、非控制字符的可打印字符）
        // 符号字符范围: 33-47, 58-64, 91-96, 123-126
        for (int i = 33; i <= 47; i++) {
            assertTrue(AsciiCharacterType.SYMBOL.isMatch(i));
        }
        for (int i = 58; i <= 64; i++) {
            assertTrue(AsciiCharacterType.SYMBOL.isMatch(i));
        }
        for (int i = 91; i <= 96; i++) {
            assertTrue(AsciiCharacterType.SYMBOL.isMatch(i));
        }
        for (int i = 123; i <= 126; i++) {
            assertTrue(AsciiCharacterType.SYMBOL.isMatch(i));
        }
        assertFalse(AsciiCharacterType.SYMBOL.isMatch('A'));
        assertFalse(AsciiCharacterType.SYMBOL.isMatch('0'));
        assertFalse(AsciiCharacterType.SYMBOL.isMatch(' '));
    }

    @Test
    public void testPrintableCharacter() {
        // 测试可打印字符 (空格到波浪号)
        assertTrue(AsciiCharacterType.PRINTABLE.isMatch(' '));
        assertTrue(AsciiCharacterType.PRINTABLE.isMatch('~'));
        for (char c = '!'; c <= '|'; c++) {
            assertTrue(AsciiCharacterType.PRINTABLE.isMatch(c));
        }
        assertFalse(AsciiCharacterType.PRINTABLE.isMatch(0));
        assertFalse(AsciiCharacterType.PRINTABLE.isMatch(31));
        assertFalse(AsciiCharacterType.PRINTABLE.isMatch(127));
    }

    @Test
    public void testAlphanumericCharacter() {
        // 测试字母数字字符 (A-Z, a-z, 0-9)
        for (char c = 'A'; c <= 'Z'; c++) {
            assertTrue(AsciiCharacterType.ALPHANUMERIC.isMatch(c));
        }
        for (char c = 'a'; c <= 'z'; c++) {
            assertTrue(AsciiCharacterType.ALPHANUMERIC.isMatch(c));
        }
        for (char c = '0'; c <= '9'; c++) {
            assertTrue(AsciiCharacterType.ALPHANUMERIC.isMatch(c));
        }
        assertFalse(AsciiCharacterType.ALPHANUMERIC.isMatch('@'));
        assertFalse(AsciiCharacterType.ALPHANUMERIC.isMatch(' '));
    }

    @Test
    public void testQuoteCharacter() {
        // 测试引号字符 ("'`)
        assertTrue(AsciiCharacterType.QUOTE.isMatch('"'));
        assertTrue(AsciiCharacterType.QUOTE.isMatch('\''));
        assertTrue(AsciiCharacterType.QUOTE.isMatch('`'));
        assertFalse(AsciiCharacterType.QUOTE.isMatch('a'));
    }

    @Test
    public void testSpecialCharacter() {
        // 测试特殊符号 (@#$%^&*)
        String specials = "@#$%^&*";
        for (int i = 0; i < specials.length(); i++) {
            assertTrue(AsciiCharacterType.SPECIAL.isMatch(specials.charAt(i)));
        }
        assertFalse(AsciiCharacterType.SPECIAL.isMatch('a'));
    }

    @Test
    public void testIsMatchAll() {
        // 测试 isMatchAll 方法
        assertTrue(AsciiCharacterType.isMatchAll('A', AsciiCharacterType.LETTER, AsciiCharacterType.UPPER_LETTER, AsciiCharacterType.ALPHANUMERIC, AsciiCharacterType.PRINTABLE));
        assertFalse(AsciiCharacterType.isMatchAll('A', AsciiCharacterType.LETTER, AsciiCharacterType.LOWER_LETTER));
        assertFalse(AsciiCharacterType.isMatchAll(200, AsciiCharacterType.LETTER)); // 非ASCII字符
    }

    @Test
    public void testIsMatchAny() {
        // 测试 isMatchAny 方法
        assertTrue(AsciiCharacterType.isMatchAny('A', AsciiCharacterType.LETTER, AsciiCharacterType.NUMBER));
        assertTrue(AsciiCharacterType.isMatchAny('+', AsciiCharacterType.LETTER, AsciiCharacterType.NUMBER));
        assertFalse(AsciiCharacterType.isMatchAny('A', AsciiCharacterType.NUMBER, AsciiCharacterType.CONTROL));
        assertFalse(AsciiCharacterType.isMatchAny(200, AsciiCharacterType.LETTER)); // 非ASCII字符
    }

    @Test
    public void testNonAsciiCharacter() {
        // 测试非ASCII字符
        assertFalse(AsciiCharacterType.LETTER.isMatch(128));
        assertFalse(AsciiCharacterType.LETTER.isMatch(-1));
        assertFalse(AsciiCharacterType.isMatchAll(128, AsciiCharacterType.LETTER));
        assertFalse(AsciiCharacterType.isMatchAny(128, AsciiCharacterType.LETTER));
    }
}
