package com.github.zhitron.universal;

import org.junit.Test;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.IntUnaryOperator;

import static org.junit.Assert.*;

/**
 * @author zhitron
 */
public class UniversalStringTest {

    // ==================== 索引和偏移量验证测试 ====================

    @Test
    public void test_isValidateByIndex() {
        // 测试有效索引范围
        assertTrue(UniversalString.isValidateByIndex(0, 5, 10));
        assertTrue(UniversalString.isValidateByIndex(2, 8, 10));

        // 测试无效索引范围
        assertFalse(UniversalString.isValidateByIndex(-1, 5, 10));
        assertFalse(UniversalString.isValidateByIndex(0, 11, 10));
        assertFalse(UniversalString.isValidateByIndex(5, 3, 10));
        assertFalse(UniversalString.isValidateByIndex(0, 0, 10));
        assertFalse(UniversalString.isValidateByIndex(10, 15, 10));

        // 测试边界情况
        assertFalse(UniversalString.isValidateByIndex(0, 5, 0)); // 长度为0
        assertFalse(UniversalString.isValidateByIndex(0, 5, -1)); // 长度为负数

        // 测试两个索引范围都有效
        assertTrue(UniversalString.isValidateByIndex(0, 5, 10, 0, 3, 5));
        assertTrue(UniversalString.isValidateByIndex(2, 6, 10, 1, 4, 5));

        // 测试输入索引范围无效
        assertFalse(UniversalString.isValidateByIndex(-1, 5, 10, 0, 3, 5));

        // 测试目标索引范围无效
        assertFalse(UniversalString.isValidateByIndex(0, 5, 10, -1, 3, 5));

        // 测试输入范围长度小于目标范围长度
        assertFalse(UniversalString.isValidateByIndex(0, 2, 10, 0, 5, 5));
    }

    @Test
    public void test_isValidateByOffset() {
        // 测试有效偏移量和计数
        assertTrue(UniversalString.isValidateByOffset(0, 5, 10));
        assertTrue(UniversalString.isValidateByOffset(2, 3, 10));

        // 测试无效偏移量和计数
        assertFalse(UniversalString.isValidateByOffset(-1, 5, 10));
        assertFalse(UniversalString.isValidateByOffset(0, -1, 10));
        assertFalse(UniversalString.isValidateByOffset(0, 0, 10));
        assertFalse(UniversalString.isValidateByOffset(0, 11, 10));
        assertFalse(UniversalString.isValidateByOffset(8, 3, 10));
        assertFalse(UniversalString.isValidateByOffset(0, 5, 0));
        assertFalse(UniversalString.isValidateByOffset(0, 5, -1));

        // 测试两个偏移量和计数都有效
        assertTrue(UniversalString.isValidateByOffset(0, 5, 10, 0, 3, 5));
        assertTrue(UniversalString.isValidateByOffset(2, 3, 10, 1, 2, 5));

        // 测试输入偏移量和计数无效
        assertFalse(UniversalString.isValidateByOffset(-1, 5, 10, 0, 3, 5));

        // 测试目标偏移量和计数无效
        assertFalse(UniversalString.isValidateByOffset(0, 5, 10, -1, 3, 5));

        // 测试输入范围的结束位置小于目标范围的结束位置
        assertFalse(UniversalString.isValidateByOffset(0, 2, 10, 0, 5, 5));
    }

    // ==================== 字符类型判断测试 ====================

    @Test
    public void test_isAscii() {
        // 测试有效的ASCII字符
        assertTrue("小写字母应该是ASCII字符", UniversalString.isAscii('a'));
        assertTrue("大写字母应该是ASCII字符", UniversalString.isAscii('Z'));
        assertTrue("数字字符应该是ASCII字符", UniversalString.isAscii('5'));
        assertTrue("控制字符应该是ASCII字符", UniversalString.isAscii('\n'));

        // 测试非ASCII字符
        assertFalse("128不是ASCII字符", UniversalString.isAscii(128));
        assertFalse("200不是ASCII字符", UniversalString.isAscii(200));
    }

    @Test
    public void test_isAsciiPrintable() {
        // 测试可见ASCII字符
        assertTrue("小写字母应该是可见ASCII字符", UniversalString.isAsciiPrintable('a'));
        assertTrue("大写字母应该是可见ASCII字符", UniversalString.isAsciiPrintable('Z'));
        assertTrue("数字字符应该是可见ASCII字符", UniversalString.isAsciiPrintable('5'));
        assertTrue("符号应该是可见ASCII字符", UniversalString.isAsciiPrintable('-'));

        // 测试不可见ASCII字符
        assertFalse("换行符不是可见ASCII字符", UniversalString.isAsciiPrintable('\n'));
        assertFalse("DEL字符(127)不是可见ASCII字符", UniversalString.isAsciiPrintable(127));

        // 测试非ASCII字符
        assertFalse("200不是可见ASCII字符", UniversalString.isAsciiPrintable(200));
    }

    @Test
    public void test_isAsciiControl() {
        // 测试可见字符（不应是控制符）
        assertFalse("小写字母不应是控制符", UniversalString.isAsciiControl('a'));
        assertFalse("大写字母不应是控制符", UniversalString.isAsciiControl('Z'));
        assertFalse("数字字符不应是控制符", UniversalString.isAsciiControl('5'));
        assertFalse("符号不应是控制符", UniversalString.isAsciiControl('-'));

        // 测试控制字符
        assertTrue("换行符应该是控制符", UniversalString.isAsciiControl('\n'));
        assertTrue("DEL字符(127)应该是控制符", UniversalString.isAsciiControl(127));

        // 测试边界情况
        assertFalse("126不是控制符", UniversalString.isAsciiControl(126));
    }

    @Test
    public void test_isHex() {
        // 测试数字字符(0-9)
        for (char c = '0'; c <= '9'; c++) {
            assertTrue("数字字符 " + c + " 应该是十六进制字符", UniversalString.isHex(c));
        }

        // 测试小写十六进制字母(a-f)
        for (char c = 'a'; c <= 'f'; c++) {
            assertTrue("小写字母 " + c + " 应该是十六进制字符", UniversalString.isHex(c));
        }

        // 测试大写十六进制字母(A-F)
        for (char c = 'A'; c <= 'F'; c++) {
            assertTrue("大写字母 " + c + " 应该是十六进制字符", UniversalString.isHex(c));
        }

        // 测试非十六进制字符
        assertFalse("字符 'g' 不应该是十六进制字符", UniversalString.isHex('g'));
        assertFalse("字符 'G' 不应该是十六进制字符", UniversalString.isHex('G'));
        assertFalse("空格字符不应该是十六进制字符", UniversalString.isHex(' '));
    }

    @Test
    public void test_isEmoji() {
        // 测试常见的emoji字符
        assertTrue("😀 (0x1F600) 应该是emoji字符", UniversalString.isEmoji(0x1F600)); // 😀
        assertTrue("🙏 (0x1F64F) 应该是emoji字符", UniversalString.isEmoji(0x1F64F)); // 🙏
        assertTrue("🌀 (0x1F300) 应该是emoji字符", UniversalString.isEmoji(0x1F300)); // 🌀
        assertTrue("🗿 (0x1F5FF) 应该是emoji字符", UniversalString.isEmoji(0x1F5FF)); // 🗿

        // 测试非emoji字符
        assertFalse("小写字母 'a' 不应该是emoji字符", UniversalString.isEmoji('a'));
        assertFalse("大写字母 'Z' 不应该是emoji字符", UniversalString.isEmoji('Z'));
        assertFalse("数字字符 '5' 不应该是emoji字符", UniversalString.isEmoji('5'));
        assertFalse("0x1F000 不在emoji范围内，不应该是emoji字符", UniversalString.isEmoji(0x1F000));
    }

    @Test
    public void test_isPathSeparator() {
        // 测试有效的路径分隔符
        assertTrue("正斜杠 '/' 应该是路径分隔符", UniversalString.isPathSeparator('/'));
        assertTrue("反斜杠 '\\' 应该是路径分隔符", UniversalString.isPathSeparator('\\'));

        // 测试非路径分隔符
        assertFalse("字母 'a' 不应该是路径分隔符", UniversalString.isPathSeparator('a'));
        assertFalse("冒号 ':' 不应该是路径分隔符", UniversalString.isPathSeparator(':'));
    }

    // ==================== 字符串空值和空白字符测试 ====================

    @Test
    public void test_isEmpty() {
        // 测试null值
        assertTrue("null值应该是空的", UniversalString.isEmpty(null));

        // 测试空字符串
        assertTrue("空字符串应该是空的", UniversalString.isEmpty(""));

        // 测试非空字符串
        assertFalse("非空字符串不应该是空的", UniversalString.isEmpty("a"));

        // 测试只包含空格的字符串
        assertFalse("只包含空格的字符串不应该是空的", UniversalString.isEmpty(" "));
    }

    @Test
    public void test_isNotEmpty() {
        // 测试null值
        assertFalse("null值不应该是非空的", UniversalString.isNotEmpty(null));

        // 测试空字符串
        assertFalse("空字符串不应该是非空的", UniversalString.isNotEmpty(""));

        // 测试非空字符串
        assertTrue("非空字符串应该是非空的", UniversalString.isNotEmpty("a"));

        // 测试只包含空格的字符串
        assertTrue("只包含空格的字符串应该是非空的", UniversalString.isNotEmpty(" "));
    }

    @Test
    public void test_isBlank() {
        // 测试null值
        assertTrue("null值应该是空白的", UniversalString.isBlank(null));
        assertFalse("null值不应该是非空白的", UniversalString.isNotBlank(null));

        // 测试空字符串
        assertTrue("空字符串应该是空白的", UniversalString.isBlank(""));
        assertFalse("空字符串不应该是非空白的", UniversalString.isNotBlank(""));

        // 测试只包含空格的字符串
        assertTrue("只包含空格的字符串应该是空白的", UniversalString.isBlank(" "));
        assertFalse("只包含空格的字符串不应该是非空白的", UniversalString.isNotBlank(" "));

        // 测试包含制表符和换行符的字符串
        assertTrue("包含制表符和换行符的字符串应该是空白的", UniversalString.isBlank("\t\n"));
        assertFalse("包含制表符和换行符的字符串不应该是非空白的", UniversalString.isNotBlank("\t\n"));

        // 测试包含非空白字符的字符串
        assertFalse("包含非空白字符的字符串不应该是空白的", UniversalString.isBlank(" a "));
        assertTrue("包含非空白字符的字符串应该是非空白的", UniversalString.isNotBlank(" a "));
    }

    // ==================== 字符串包含测试 ====================

    @Test
    public void test_isContain() {
        // 测试包含情况
        assertTrue(UniversalString.isContain(false, "hello world", "world"));
        assertTrue(UniversalString.isContain(true, "Hello World", "world"));

        // 测试不包含情况
        assertFalse(UniversalString.isContain(false, "hello world", "java"));
        assertFalse(UniversalString.isContain(false, null, "world"));
        assertFalse(UniversalString.isContain(false, "hello", null));
        assertFalse(UniversalString.isContain(false, "hi", "hello")); // target longer than input
        assertFalse(UniversalString.isContain(false, "", "hello")); // empty input
    }

    @Test
    public void test_isContainAll() {
        // 测试包含所有情况
        assertTrue(UniversalString.isContainAll(false, "hello world java", "hello", "world"));
        assertTrue(UniversalString.isContainAll(true, "Hello World Java", "hello", "WORLD"));

        // 测试不包含所有情况
        assertFalse(UniversalString.isContainAll(false, "hello world", "hello", "java"));
        assertFalse(UniversalString.isContainAll(false, null, "hello"));
        assertFalse(UniversalString.isContainAll(false, "hello", (CharSequence[]) null));
        assertFalse(UniversalString.isContainAll(false, "", "hello")); // empty input
    }

    @Test
    public void test_isContainAny() {
        // 测试包含任意一个情况
        assertTrue(UniversalString.isContainAny(false, "hello world", "hello", "java"));
        assertTrue(UniversalString.isContainAny(true, "Hello World", "hello", "java"));

        // 测试不包含任意一个情况
        assertFalse(UniversalString.isContainAny(false, "hello world", "java", "python"));
        assertFalse(UniversalString.isContainAny(false, null, "hello"));
        assertFalse(UniversalString.isContainAny(false, "hello", (CharSequence[]) null));
        assertFalse(UniversalString.isContainAny(false, "", "hello")); // empty input
    }

    // ==================== 字符串数组测试 ====================

    @Test
    public void test_areAllEmpty() {
        // 测试所有都为空
        assertTrue(UniversalString.areAllEmpty(null, ""));
        assertTrue(UniversalString.areAllEmpty("", null));

        // 测试不都是空
        assertFalse(UniversalString.areAllEmpty("", "a"));
        assertFalse(UniversalString.areAllEmpty("a", ""));
        assertFalse(UniversalString.areAllEmpty()); // 空数组
    }

    @Test
    public void test_areAllNotEmpty() {
        // 测试所有都不为空
        assertTrue(UniversalString.areAllNotEmpty("a", "b"));
        assertTrue(UniversalString.areAllNotEmpty(" ", "a"));
        assertTrue(UniversalString.areAllNotEmpty("a")); // 单个元素

        // 测试不都是非空
        assertFalse(UniversalString.areAllNotEmpty("", "a"));
        assertFalse(UniversalString.areAllNotEmpty(null, "a"));
        assertFalse(UniversalString.areAllNotEmpty()); // 空数组
    }

    @Test
    public void test_areAllBlank() {
        // 测试所有都为空白
        assertTrue(UniversalString.areAllBlank(null, "", " ", "\t\n"));

        // 测试不都是空白
        assertFalse(UniversalString.areAllBlank("", "a"));
        assertFalse(UniversalString.areAllBlank("a", " "));
        assertFalse(UniversalString.areAllBlank()); // 空数组
    }

    @Test
    public void test_areAllNotBlank() {
        // 测试所有都不为空白
        assertTrue(UniversalString.areAllNotBlank("a", "b"));
        assertTrue(UniversalString.areAllNotBlank("a")); // 单个元素

        // 测试不都是非空白
        assertFalse(UniversalString.areAllNotBlank("a", " "));
        assertFalse(UniversalString.areAllNotBlank("", "a"));
        assertFalse(UniversalString.areAllNotBlank()); // 空数组
    }

    // ==================== 字符串相等性测试 ====================

    @Test
    public void test_isEquals() {
        String str = "test";

        // 测试相同引用
        assertTrue("相同引用应该相等(区分大小写)", UniversalString.isEquals(false, str, str));
        assertTrue("相同引用应该相等(忽略大小写)", UniversalString.isEquals(true, str, str));

        // 测试null值
        assertFalse("字符串与null不相等", UniversalString.isEquals(false, "test", null));
        assertFalse("null与字符串不相等", UniversalString.isEquals(false, null, "test"));
        assertTrue("null与null相等", UniversalString.isEquals(false, null, null));

        // 测试相同字符串
        assertTrue("相同字符串应该相等(区分大小写)", UniversalString.isEquals(false, "test", "test"));
        assertTrue("相同字符串应该相等(忽略大小写)", UniversalString.isEquals(true, "test", "test"));

        // 测试不同字符串
        assertFalse("不同字符串不应该相等(区分大小写)", UniversalString.isEquals(false, "test", "Test"));
        assertFalse("不同字符串不应该相等(忽略大小写)", UniversalString.isEquals(true, "test", "testing"));

        // 测试忽略大小写比较
        assertTrue("忽略大小写时'test'与'TEST'应该相等", UniversalString.isEquals(true, "test", "TEST"));
        assertTrue("忽略大小写时'Test'与'tEsT'应该相等", UniversalString.isEquals(true, "Test", "tEsT"));
        assertFalse("区分大小写时'test'与'TEST'不应该相等", UniversalString.isEquals(false, "test", "TEST"));
    }

    // ==================== 字符串包围测试 ====================

    @Test
    public void test_isSurround() {
        // 测试被包围情况
        assertTrue(UniversalString.isSurround(false, "\"hello\"", "\"", "\""));
        assertTrue(UniversalString.isSurround(true, "\"Hello\"", "\"", "\""));

        // 测试未被包围情况
        assertFalse(UniversalString.isSurround(false, "hello", "\"", "\""));
        assertFalse(UniversalString.isSurround(false, null, "\"", "\""));
        assertFalse(UniversalString.isSurround(false, "\"hello", "\"", "\""));
        assertFalse(UniversalString.isSurround(false, "hello\"", "\"", "\""));

        // 测试空字符串
        assertFalse(UniversalString.isSurround(false, "", "\"", "\""));
    }

    // ==================== 字符串前缀和后缀测试 ====================

    @Test
    public void test_isStartsWith() {
        String input = "Hello World";

        // 测试正常情况
        assertTrue("字符串应该以'Hello'开始(区分大小写)", UniversalString.isStartsWith(false, input, "Hello"));
        assertTrue("字符串应该以'HELLO'开始(忽略大小写)", UniversalString.isStartsWith(true, input, "HELLO"));
        assertFalse("字符串不应该以'World'开始", UniversalString.isStartsWith(false, input, "World"));

        // 测试null值
        assertFalse("null字符串不应该以任何前缀开始", UniversalString.isStartsWith(false, null, "test"));
        assertFalse("任何字符串都不应该以null开始", UniversalString.isStartsWith(false, "test", null));

        // 测试空字符串
        assertFalse("字符串不应该以空字符串开始(区分大小写)", UniversalString.isStartsWith(false, "test", ""));
        assertFalse("字符串不应该以空字符串开始(忽略大小写)", UniversalString.isStartsWith(true, "test", ""));
    }

    @Test
    public void test_isEndsWith() {
        String input = "Hello World";

        // 测试正常情况
        assertTrue("字符串应该以'World'结束(区分大小写)", UniversalString.isEndsWith(false, input, "World"));
        assertTrue("字符串应该以'WORLD'结束(忽略大小写)", UniversalString.isEndsWith(true, input, "WORLD"));
        assertFalse("字符串不应该以'Hello'结束", UniversalString.isEndsWith(false, input, "Hello"));

        // 测试null值
        assertFalse("null字符串不应该以任何后缀结束", UniversalString.isEndsWith(false, null, "test"));
        assertFalse("任何字符串都不应该以null结束", UniversalString.isEndsWith(false, "test", null));

        // 测试空字符串
        assertFalse("字符串不应该以空字符串结束(区分大小写)", UniversalString.isEndsWith(false, "test", ""));
        assertFalse("字符串不应该以空字符串结束(忽略大小写)", UniversalString.isEndsWith(true, "test", ""));
    }

    // ==================== 字符串区域匹配测试 ====================

    @Test
    public void test_regionMatches() {
        // 测试区域匹配
        assertTrue(UniversalString.regionMatches(false, "hello world", 0, "hello", 0, 5));
        assertTrue(UniversalString.regionMatches(true, "Hello World", 0, "hello", 0, 5));

        // 测试区域不匹配
        assertFalse(UniversalString.regionMatches(false, "hello world", 0, "world", 0, 5));
        assertFalse(UniversalString.regionMatches(false, null, 0, "hello", 0, 5));
        assertFalse(UniversalString.regionMatches(false, "hello", 0, null, 0, 5));

        // 测试越界情况
        assertFalse(UniversalString.regionMatches(false, "hello", 10, "world", 0, 5));
        assertFalse(UniversalString.regionMatches(false, "hello", 0, "world", 10, 5));
    }

    // ==================== 字符串修剪边界计算测试 ====================

    @Test
    public void test_calculateBounds() {
        int[] bounds = {0, 10};
        IntUnaryOperator skip = i -> (i < 3 || i >= 7) ? 1 : 0; // 跳过前3个和后3个字符

        // 测试修剪开头和结尾
        UniversalString.calculateTrimBounds(bounds, true, true, skip);
        assertEquals(3, bounds[0]);
        assertEquals(7, bounds[1]);

        // 测试只修剪开头
        bounds = new int[]{0, 10};
        UniversalString.calculateTrimBounds(bounds, true, false, skip);
        assertEquals(3, bounds[0]);
        assertEquals(10, bounds[1]);

        // 测试只修剪结尾
        bounds = new int[]{0, 10};
        UniversalString.calculateTrimBounds(bounds, false, true, skip);
        assertEquals(0, bounds[0]);
        assertEquals(7, bounds[1]);
    }

    // ==================== 字符串出现次数统计测试 ====================

    @Test
    public void testCountOccurrences() {
        // 测试正常情况：找到匹配项
        assertEquals(2, UniversalString.countOccurrences(false, "hello world hello", "hello"));
        assertEquals(1, UniversalString.countOccurrences(false, "hello world", "world"));
        assertEquals(3, UniversalString.countOccurrences(false, "ababab", "ab"));

        // 测试忽略大小写情况
        assertEquals(2, UniversalString.countOccurrences(true, "Hello World HELLO", "hello"));
        assertEquals(1, UniversalString.countOccurrences(true, "Hello World", "WORLD"));

        // 测试无匹配情况
        assertEquals(0, UniversalString.countOccurrences(false, "hello world", "java"));
        assertEquals(0, UniversalString.countOccurrences(false, "hello", "hello world"));

        // 测试null输入
        assertEquals(0, UniversalString.countOccurrences(false, null, "hello"));
        assertEquals(0, UniversalString.countOccurrences(false, "hello", null));
        assertEquals(0, UniversalString.countOccurrences(false, null, null));

        // 测试空字符串
        assertEquals(0, UniversalString.countOccurrences(false, "", "hello"));
        assertEquals(0, UniversalString.countOccurrences(false, "hello", ""));
        assertEquals(0, UniversalString.countOccurrences(false, "", ""));

        // 测试目标字符串长度大于输入字符串
        assertEquals(0, UniversalString.countOccurrences(false, "hi", "hello"));

        // 测试重叠匹配情况
        assertEquals(2, UniversalString.countOccurrences(false, "aaaa", "aa"));
        assertEquals(1, UniversalString.countOccurrences(false, "banana", "ana")); // 重叠的"ana"

        // 测试完全匹配
        assertEquals(1, UniversalString.countOccurrences(false, "hello", "hello"));

        // 测试特殊字符
        assertEquals(3, UniversalString.countOccurrences(false, "a.b.c.d", "."));
        assertEquals(2, UniversalString.countOccurrences(false, "a*b*a", "*"));

        // 测试空白字符
        assertEquals(2, UniversalString.countOccurrences(false, "a a a", " "));

        // 测试连续相同字符
        assertEquals(5, UniversalString.countOccurrences(false, "aaaaa", "a"));
    }

    // ==================== 字符串简写测试 ====================

    @Test
    public void test_brief() {
        // 测试正常情况：输入"Hello World"，省略符为'*'，省略部分长度为3，前缀长度2，后缀长度3
        assertEquals("He***rld", UniversalString.brief("Hello World", '*', 3, 2, 3));

        // 测试输入为null的情况
        assertEquals("", UniversalString.brief(null, '*', 3, 2, 3));

        // 测试前缀和后缀长度之和大于等于输入长度的情况
        assertEquals("Hello", UniversalString.brief("Hello", '*', 2, 3, 2));

        // 测试省略部分长度为0的情况
        assertEquals("Hello", UniversalString.brief("Hello", '*', 0, 2, 3));

        // 测试前缀长度为0的情况
        assertEquals("***llo", UniversalString.brief("Hello", '*', 3, 0, 3));

        // 测试后缀长度为0的情况
        assertEquals("He***", UniversalString.brief("Hello", '*', 3, 2, 0));

        // 测试所有长度参数都为0的情况
        assertEquals("", UniversalString.brief("Hello", '*', 0, 0, 0));

        // 测试空字符串输入
        assertEquals("", UniversalString.brief("", '*', 3, 2, 3));

        // 测试省略符为Unicode字符的情况
        assertEquals("He♠♠♠rld", UniversalString.brief("Hello World", '♠', 3, 2, 3));

        // 测试负数参数异常情况
        try {
            // 测试省略部分长度为负数的情况，应抛出IllegalArgumentException异常
            UniversalString.brief("Hello World", '*', -1, 2, 3);
            fail("Expected IllegalArgumentException for negative briefLength");
        } catch (IllegalArgumentException e) {
            // 预期异常
        }

        try {
            // 测试前缀长度为负数的情况，应抛出IllegalArgumentException异常
            UniversalString.brief("Hello World", '*', 3, -1, 3);
            fail("Expected IllegalArgumentException for negative leadingLength");
        } catch (IllegalArgumentException e) {
            // 预期异常
        }

        try {
            // 测试后缀长度为负数的情况，应抛出IllegalArgumentException异常
            UniversalString.brief("Hello World", '*', 3, 2, -1);
            fail("Expected IllegalArgumentException for negative trailingLength");
        } catch (IllegalArgumentException e) {
            // 预期异常
        }
    }

    // ==================== 字符串首字母大小写转换测试 ====================

    @Test
    public void test_capitalize() {
        // 测试正常情况
        assertEquals("小写字符串首字母大写应该得到'Hello'", "Hello", UniversalString.capitalize("hello", true));
        assertEquals("已大写字符串首字母大写应该得到'Hello'", "Hello", UniversalString.capitalize("Hello", true));
        assertEquals("数字开头字符串应该保持'123abc'", "123abc", UniversalString.capitalize("123abc", true));

        // 测试单字符
        assertEquals("小写字母大写应该得到'A'", "A", UniversalString.capitalize("a", true));
        assertEquals("大写字母大写应该得到'A'", "A", UniversalString.capitalize("A", true));

        // 测试null和空字符串
        assertEquals("null字符串首字母大写应该得到空字符串", "", UniversalString.capitalize(null, true));
        assertEquals("空字符串首字母大写应该得到空字符串", "", UniversalString.capitalize("", true));

        // 测试uncapitalize
        assertEquals("大写字符串首字母小写应该得到'hello'", "hello", UniversalString.capitalize("Hello", false));
        assertEquals("null字符串首字母小写应该得到空字符串", "", UniversalString.capitalize(null, false));
        assertEquals("空字符串首字母小写应该得到空字符串", "", UniversalString.capitalize("", false));
    }

    // ==================== 字符串清理测试 ====================

    @Test
    public void test_clean() {
        // 测试基于字符的清理
        assertEquals("移除空格应该得到'helloworld'", "helloworld", UniversalString.clean(false, " hello world ", " "));
        assertEquals("移除空格和'o'应该得到'hellwrld'", "hellwrld", UniversalString.clean(false, " hello world ", " ", "o"));
        assertEquals("移除'l'应该得到' heo word '", " heo word ", UniversalString.clean(false, " hello world ", "l"));

        // 测试null值
        assertEquals("null字符串清理应该得到空字符串", "", UniversalString.clean(false, null, " "));
        assertEquals("目标字符为null时应该得到原字符串", "test", UniversalString.clean(false, "test", (CharSequence[]) null));

        // 测试基于条件的清理
        IntPredicate isWhitespace = Character::isWhitespace;
        assertEquals("移除空白字符应该得到'helloworld'", "helloworld", UniversalString.clean(" hello world ", isWhitespace));
        assertEquals("null字符串清理应该得到空字符串", "", UniversalString.clean(null, isWhitespace));
    }

    // ==================== 字符串索引查找测试 ====================

    @Test
    public void test_indexOf() {
        // 测试正常情况
        assertEquals(0, UniversalString.indexOf(false, "hello hello", 0, 11, "hello", 1));
        assertEquals(6, UniversalString.indexOf(false, "hello hello", 0, 11, "hello", 2));
        assertEquals(-1, UniversalString.indexOf(false, "hello hello", 0, 11, "hello", 3));

        // 测试null输入
        assertEquals(-1, UniversalString.indexOf(false, null, 0, 11, "hello", 1));
        assertEquals(-1, UniversalString.indexOf(false, "hello", 0, 5, null, 1));

        // 测试越界情况
        assertEquals(-1, UniversalString.indexOf(false, "hello", 10, 15, "hello", 1));
        assertEquals(-1, UniversalString.indexOf(false, "hello", -1, 5, "hello", 1));

        // 测试正常情况
        assertEquals(0, UniversalString.indexOf(false, "hello world", 0, 11, "hello"));
        assertEquals(6, UniversalString.indexOf(false, "hello world", 0, 11, "world"));
        assertEquals(-1, UniversalString.indexOf(false, "hello world", 0, 11, "java"));

        // 测试null输入
        assertEquals(-1, UniversalString.indexOf(false, null, 0, 11, "hello"));
        assertEquals(-1, UniversalString.indexOf(false, "hello", 0, 5, null));
    }

    @Test
    public void test_lastIndexOf() {
        // 测试正常情况：找到目标字符串最后一次出现的位置
        CharSequence input = "hello world hello universe";
        CharSequence target = "hello";
        int result = UniversalString.lastIndexOf(false, input, 0, input.length(), target, 2);
        assertEquals(0, result); // 第二次出现"hello"的位置应该是0

        // 测试忽略大小写的情况
        input = "Hello World HELLO Universe";
        result = UniversalString.lastIndexOf(true, input, 0, input.length(), target, 2);
        assertEquals(0, result); // 忽略大小写，第二次出现"hello"的位置应该是0

        // 测试输入为null的情况
        assertEquals(UniversalString.NOT_FOUND, UniversalString.lastIndexOf(false, null, 0, 10, "test", 1));
        assertEquals(UniversalString.NOT_FOUND, UniversalString.lastIndexOf(false, "test", 0, 4, null, 1));

        // 测试occurrence小于等于0的情况
        assertEquals(UniversalString.NOT_FOUND, UniversalString.lastIndexOf(false, "test", 0, 4, "t", 0));
        assertEquals(UniversalString.NOT_FOUND, UniversalString.lastIndexOf(false, "test", 0, 4, "t", -1));

        // 测试边界条件
        input = "abcabcabc";
        target = "abc";

        // 查找最后一次出现
        result = UniversalString.lastIndexOf(false, input, 0, input.length(), target, 3);
        assertEquals(0, result); // 第三次出现"abc"的位置应该是6

        // 查找超出范围的出现次数
        result = UniversalString.lastIndexOf(false, input, 0, input.length(), target, 4);
        assertEquals(UniversalString.NOT_FOUND, result); // 第四次出现应该返回NOT_FOUND

        // 测试空字符串情况
        assertEquals(UniversalString.NOT_FOUND, UniversalString.lastIndexOf(false, "", 0, 0, "test", 1));
        assertEquals(UniversalString.NOT_FOUND, UniversalString.lastIndexOf(false, "test", 0, 4, "", 1));

        // 测试指定索引范围的情况
        input = "hello world hello universe";
        target = "hello";

        // 在限定范围内查找
        result = UniversalString.lastIndexOf(false, input, 0, 10, target, 1);
        assertEquals(0, result); // 在前10个字符中，"hello"第一次也是最后一次出现的位置是0

        // 在限定范围内查找第二次出现（应该找不到）
        result = UniversalString.lastIndexOf(false, input, 0, 10, target, 2);
        assertEquals(UniversalString.NOT_FOUND, result); // 在前10个字符中，"hello"不会出现第二次
    }

    // ==================== 字符串脱敏测试 ====================

    @Test
    public void test_desensitize() {
        // 测试正常脱敏情况
        assertEquals("**llo", UniversalString.desensitize("hello", "*", 2, 0, 0));
        assertEquals("he**o", UniversalString.desensitize("hello", "*", 0, 2, 0));
        assertEquals("hel**", UniversalString.desensitize("hello", "*", 0, 0, 2));
        assertEquals("**l**", UniversalString.desensitize("hello", "*", 2, 0, 2));

        // 测试边界情况
        assertEquals("", UniversalString.desensitize(null, "*", 2, 1, 2)); // null输入
        assertEquals("", UniversalString.desensitize("", "*", 2, 1, 2));   // 空字符串
        assertEquals("*****", UniversalString.desensitize("hello", "*", 10, 2, 2)); // mark长度超过输入长度
        assertEquals("", UniversalString.desensitize("hello", null, 2, 0, 0)); // mark为null
    }

    // ==================== 字符串格式化测试 ====================

    @Test
    public void test_format() {
        // 测试正常格式化
        assertEquals("this is a for b", UniversalString.format("this is {} for {}", "{}", "a", "b"));

        // 测试转义情况
        assertEquals("this is {} for a", UniversalString.format("this is \\{} for {}", "{}", "a", "b"));
        assertEquals("this is \\a for b", UniversalString.format("this is \\\\{} for {}", "{}", "a", "b"));

        // 测试边界情况
        assertEquals("", UniversalString.format(null, "{}", "a"));         // null模板
        assertEquals("", UniversalString.format("", "{}", "a"));           // 空模板
        assertEquals("test", UniversalString.format("test", null, "a"));   // null占位符
        assertEquals("test", UniversalString.format("test", "", "a"));     // 空占位符
        assertEquals("this is a for {}", UniversalString.format("this is {} for {}", "{}", "a")); // 参数不足
    }

    // ==================== 字符串连接测试 ====================

    @Test
    public void test_join() {
        String[] array = {"a", "b", "c"};

        // 测试基本连接功能
        assertEquals("使用逗号连接应该得到'a,b,c'", "a,b,c", UniversalString.join(",", null, null, null, null, array));
        assertEquals("使用分号连接应该得到'a;b;c'", "a;b;c", UniversalString.join(";", null, null, null, null, array));

        // 测试前缀后缀
        assertEquals("添加前缀和后缀应该得到'[a,b,c]'", "[a,b,c]", UniversalString.join(",", null, null, "[", "]", array));

        // 测试元素前缀后缀
        assertEquals("为每个元素添加括号应该得到'(a),(b),(c)'", "(a),(b),(c)", UniversalString.join(",", "(", ")", null, null, array));

        // 测试null数组
        assertEquals("null数组连接应该得到空字符串", "", UniversalString.join(",", null, null, null, null, (CharSequence[]) null));
    }

    // ==================== 语言环境解析测试 ====================

    @Test
    public void test_resolveLocale() {
        // 测试 null 输入
        assertNull(UniversalString.resolveLocale(null));

        // 测试空字符串输入
        assertNull(UniversalString.resolveLocale(""));

        // 测试标准格式 en
        Locale locale = UniversalString.resolveLocale("en");
        assertNotNull(locale);
        assertEquals("en", locale.getLanguage());
        assertEquals("", locale.getCountry());
        assertEquals("", locale.getVariant());

        // 测试带国家的格式 en_US
        locale = UniversalString.resolveLocale("en_US");
        assertNotNull(locale);
        assertEquals("en", locale.getLanguage());
        assertEquals("US", locale.getCountry());
        assertEquals("", locale.getVariant());

        // 测试带空格的格式 en US
        locale = UniversalString.resolveLocale("en US");
        assertNotNull(locale);
        assertEquals("en", locale.getLanguage());
        assertEquals("US", locale.getCountry());
        assertEquals("", locale.getVariant());

        // 测试带连字符的格式 en-US
        locale = UniversalString.resolveLocale("en-US");
        assertNotNull(locale);
        assertEquals("en", locale.getLanguage());
        assertEquals("US", locale.getCountry());
        assertEquals("", locale.getVariant());

        // 测试带变体的格式 en_US_WIN
        locale = UniversalString.resolveLocale("en_US_WIN");
        assertNotNull(locale);
        assertEquals("en", locale.getLanguage());
        assertEquals("US", locale.getCountry());
        assertEquals("WIN", locale.getVariant());

        // 测试带#变体的格式 en__#LATN
        locale = UniversalString.resolveLocale("en__#LATN");
        assertNotNull(locale);
        assertEquals("en", locale.getLanguage());
        assertEquals("", locale.getCountry());
        assertEquals("#LATN", locale.getVariant());

        // 测试复杂变体格式
        locale = UniversalString.resolveLocale("de_DE_PREEURO");
        assertNotNull(locale);
        assertEquals("de", locale.getLanguage());
        assertEquals("DE", locale.getCountry());
        assertEquals("PREEURO", locale.getVariant());

        // 测试无效字符
        try {
            UniversalString.resolveLocale("en@US");
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // 预期异常
        }

        // 测试包含特殊字符的无效格式
        try {
            UniversalString.resolveLocale("en@US$");
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // 预期异常
        }
    }

    // ==================== 数字解析测试 ====================

    @Test
    public void test_resolveDecimal() {
        // 测试基本数字解析
        assertEquals("777123666.456", UniversalString.resolveDecimal("  777.123.666.456  ", null, null));
        assertEquals("123.456", UniversalString.resolveDecimal("  123.456  ", null, null));
        assertEquals("-0.123", UniversalString.resolveDecimal("  -0.123  ", null, null));
        assertEquals("+1.23E+10", UniversalString.resolveDecimal("  +1.23e10  ", null, null));
        assertEquals("0.123", UniversalString.resolveDecimal("  .123  ", null, null));
        assertEquals("123.0", UniversalString.resolveDecimal("  123.  ", null, null));
        assertEquals("1234.56", UniversalString.resolveDecimal("  1,234.56  ", null, null));

        // 测试无效输入
        assertNull(UniversalString.resolveDecimal("  abc  ", null, null));
        assertNull(UniversalString.resolveDecimal("", null, null));
        assertNull(UniversalString.resolveDecimal("   ", null, null));
        assertNull(UniversalString.resolveDecimal(null, null, null));
        assertEquals("1.23", UniversalString.resolveDecimal("  1.23e  ", null, null));
        assertEquals("1.23", UniversalString.resolveDecimal("  1.23e+  ", null, null));
        assertEquals("1.23", UniversalString.resolveDecimal("  1.23e-  ", null, null));

        // 测试字符替换功能
        assertEquals("1.23", UniversalString.resolveDecimal("  一.二三  ", "一二三", "123"));
        assertEquals("-1.23", UniversalString.resolveDecimal("  负一.二三  ", "负一二三", "-123"));

        // 测试边界情况
        assertEquals("0.0", UniversalString.resolveDecimal(".", null, null));
        assertEquals("+0.0", UniversalString.resolveDecimal("+.", null, null));
        assertEquals("-0.0", UniversalString.resolveDecimal("-.", null, null));
        assertEquals("0.0", UniversalString.resolveDecimal(".", null, null));
        assertNull(UniversalString.resolveDecimal("+", null, null));
        assertNull(UniversalString.resolveDecimal("-", null, null));

        // 测试指数部分
        assertEquals("1.23E+10", UniversalString.resolveDecimal("1.23e10", null, null));
        assertEquals("1.23E-10", UniversalString.resolveDecimal("1.23E-10", null, null));
        assertEquals("1.23E+0", UniversalString.resolveDecimal("1.23e0", null, null));
    }

    // ==================== 数组解析测试 ====================

    @Test
    public void testResolveArray() {
        // 测试用例1: 基本数组表达式 [a,b,c]
        assertArrayEquals(new String[]{"a", "b", "c"}, UniversalString.resolveArray("[a,b,c]", ',', '\\'));

        assertArrayEquals(new String[]{"a a", "b b", "c c"}, UniversalString.resolveArray("[  a a  ,   b b,  c c  ]", ',', '\\'));

        assertArrayEquals(new String[]{"\"a a\"", "\"b b\"", "'c c'"}, UniversalString.resolveArray("[\"a a\",\"b b\",'c c']", ',', '\\'));

        assertArrayEquals(new String[]{"\"a 'a'\"", "\"b b\"", "'c c'"}, UniversalString.resolveArray("[\"a 'a'\",\"b b\",'c c']", ',', '\\'));

        assertArrayEquals(new String[]{"\"a \\\"a\\\"\"", "\"b b\"", "'c c'"}, UniversalString.resolveArray("[\"a \\\"a\\\"\",\"b b\",'c c']", ',', '\\'));

        // 测试用例6: 空数组表达式 []
        assertArrayEquals(new String[]{}, UniversalString.resolveArray("[]", ',', '\\'));

        // 测试用例7: 单元素数组表达式 [a]
        assertArrayEquals(new String[]{"a"}, UniversalString.resolveArray("[a]", ',', '\\'));

        // 测试用例8: 忽略引号处理 ignoreQuotes=true
        assertArrayEquals(new String[]{"\"a,b\"", "c"}, UniversalString.resolveArray("[\"a,b\",c]", ',', '\\'));

        // 测试用例9: 自定义分隔符
        assertArrayEquals(new String[]{"a", "b", "c"}, UniversalString.resolveArray("[a;b;c]", ';', '\\'));

        // 测试用例10: 自定义括号
        assertArrayEquals(new String[]{"a", "b", "c"}, UniversalString.resolveArray("(a,b,c)", ',', '\\'));

        // 测试用例11: 包含特殊转义字符
        assertArrayEquals(new String[]{"\"a\tb\"", "\"c\nd\""}, UniversalString.resolveArray("[\"a\\tb\",\"c\\nd\"]", ',', '\\'));

        // 测试用例12: 嵌套括号结构
        assertArrayEquals(new String[]{"a(b,c)", "d"}, UniversalString.resolveArray("[a(b,c),d]", ',', '\\'));

        // 测试用例13: null输入
        assertArrayEquals(new String[]{}, UniversalString.resolveArray(null, ',', '\\'));

        // 测试用例14: 空字符串输入
        assertArrayEquals(new String[]{}, UniversalString.resolveArray("", ',', '\\'));

        // 测试用例15: 只有空白字符
        assertArrayEquals(new String[]{}, UniversalString.resolveArray("   ", ',', '\\'));

        // 测试用例16: 包含空格的数组表达式 [   [a a]  ,   b  b,  c c  ]
        assertArrayEquals(new String[]{"[a a]", "b  b", "c c"}, UniversalString.resolveArray("[   [a a]  ,   b  b,  c c  ]", ',', '\\'));

        // 测试用例16: 包含空格的数组表达式 {a:1,b:{d:1,f:2},c:[1,2,3,4,5]}
        assertArrayEquals(new String[]{"a:1", "b:{d:1,f:2}", "c:[1,2,3,4,5]"}, UniversalString.resolveArray("{a:1,b:{d:1,f:2},c:[1,2,3,4,5]}", ',', '\\'));

        // 测试用例17: 复杂json格式
        // 测试嵌套对象和数组的解析
        assertArrayEquals(new String[]{"name:John", "age:30", "address:{city:New York,zip:10001}", "hobbies:[reading,music,travel]"},
                UniversalString.resolveArray("{name:John,age:30,address:{city:New York,zip:10001},hobbies:[reading,music,travel]}", ',', '\\'));

        // 测试包含转义字符的复杂JSON格式
        assertArrayEquals(new String[]{"message:\"Hello, \\\"World\\\"!\"", "path:\"C:\\Users\\test\""},
                UniversalString.resolveArray("{message:\"Hello, \\\"World\\\"!\",path:\"C:\\\\Users\\\\test\"}", ',', '\\'));

        // 测试空对象和空数组
        assertArrayEquals(new String[]{"emptyObj:{}", "emptyArr:[]", "value:null"},
                UniversalString.resolveArray("{emptyObj:{},emptyArr:[],value:null}", ',', '\\'));

        // 测试数组中包含对象
        assertArrayEquals(new String[]{"users:[{id:1,name:Alice},{id:2,name:Bob}]", "settings:{theme:dark,notifications:true}"},
                UniversalString.resolveArray("{users:[{id:1,name:Alice},{id:2,name:Bob}],settings:{theme:dark,notifications:true}}", ',', '\\'));
    }

    // ==================== 字符串反转测试 ====================

    @Test
    public void test_reverse() {
        // 测试正常情况
        assertEquals("字符串'Hello World'反转应该得到'dlroW olleH'", "dlroW olleH", UniversalString.reverse("Hello World"));
        assertEquals("单字符字符串反转应该得到'a'", "a", UniversalString.reverse("a"));

        // 测试回文
        assertEquals("回文字符串'aba'反转应该得到'aba'", "aba", UniversalString.reverse("aba"));

        // 测试空字符串和null
        assertEquals("空字符串反转应该得到空字符串", "", UniversalString.reverse(""));
        assertEquals("null字符串反转应该得到空字符串", "", UniversalString.reverse(null));
    }

    // ==================== 字符串替换测试 ====================

    @Test
    public void test_replace() {
        String str = "Hello World Hello";

        // 测试正常替换
        assertEquals("替换'Hello'为'Hi'应该得到'Hi World Hi'", "Hi World Hi", UniversalString.replace(false, str, "Hello", "Hi", -1));
        assertEquals("替换'Hi'为'Hello'应该得到原字符串", str, UniversalString.replace(false, str, "Hi", "Hello", -1));

        // 测试限制替换次数
        assertEquals("只替换一次'Hello'为'Hi'应该得到'Hi World Hello'", "Hi World Hello", UniversalString.replace(false, str, "Hello", "Hi", 1));

        // 测试null值
        assertEquals("null字符串替换应该得到空字符串", "", UniversalString.replace(false, null, "Hello", "Hi", -1));
        assertEquals("目标字符串为null时应该得到原字符串", str, UniversalString.replace(false, str, null, "Hi", -1));
    }

    // ==================== 占位符替换测试 ====================

    @Test
    public void test_replacePlaceholder() {
        // 测试基本的占位符替换功能
        String template = "Hello, ${name}!";
        Map<String, Object> params = new HashMap<>();
        params.put("name", "World");

        String result = UniversalString.replacePlaceholder(template, params, true);
        assertEquals("Hello, World!", result);

        // 测试多个占位符的替换
        template = "Hello, ${name}! Welcome to ${place}.";
        params = new HashMap<>();
        params.put("name", "Alice");
        params.put("place", "Wonderland");

        result = UniversalString.replacePlaceholder(template, params, true);
        assertEquals("Hello, Alice! Welcome to Wonderland.", result);

        // 测试缺少参数时的处理（应该替换为空字符串）
        template = "Hello, ${name}!";
        params = new HashMap<>();
        // 没有添加 name 参数

        result = UniversalString.replacePlaceholder(template, params, true);
        assertEquals("Hello, !", result);

        // 测试参数值为 null 的情况
        template = "Hello, ${name}!";
        params = new HashMap<>();
        params.put("name", null);

        result = UniversalString.replacePlaceholder(template, params, true);
        assertEquals("Hello, !", result);

        // 测试模板为 null 的情况
        params = new HashMap<>();
        params.put("name", "World");

        result = UniversalString.replacePlaceholder(null, params, true);
        assertNull(result);

        // 测试模板为空字符串的情况
        params = new HashMap<>();
        params.put("name", "World");

        result = UniversalString.replacePlaceholder("", params, true);
        assertEquals("", result);

        // 测试参数 map 为空的情况
        template = "Hello, ${name}!";

        result = UniversalString.replacePlaceholder(template, new HashMap<>(), true);
        assertEquals("Hello, !", result);

        // 测试参数 map 为 null 的情况
        template = "Hello, ${name}!";

        result = UniversalString.replacePlaceholder(template, (Map<String, ?>) null, true);
        assertEquals("Hello, !", result);

        // 测试特殊字符的处理
        template = "Price: ${price} USD";
        params = new HashMap<>();
        params.put("price", "$100");

        result = UniversalString.replacePlaceholder(template, params, true);
        assertEquals("Price: $100 USD", result);

        // 测试使用 Function 的重载方法
        template = "Hello, ${name}!";

        result = UniversalString.replacePlaceholder(template, s -> "World", true);
        assertEquals("Hello, World!", result);

        // 测试 Function 返回 null 的情况
        template = "Hello, ${name}!";

        result = UniversalString.replacePlaceholder(template, s -> null, true);
        assertEquals("Hello, !", result);

        // 测试 Function 为 null 的情况
        template = "Hello, ${name}!";

        result = UniversalString.replacePlaceholder(template, (Function<String, String>) null, true);
        assertEquals("Hello, ${name}!", result);

        // 测试复杂的占位符内容
        template = "User: ${user.name}, Age: ${user.age}";
        params = new HashMap<>();
        params.put("user.name", "John");
        params.put("user.age", 25);

        result = UniversalString.replacePlaceholder(template, params, true);
        assertEquals("User: John, Age: 25", result);
    }

    // ==================== 字符串重复测试 ====================

    @Test
    public void test_repeat() {
        // 测试基本重复
        assertEquals("重复3次'a'使用逗号连接应该得到'a,a,a'", "a,a,a", UniversalString.repeat(",", null, null, null, null, "a", 3));
        assertEquals("重复3次'a'无分隔符应该得到'aaa'", "aaa", UniversalString.repeat(null, null, null, null, null, "a", 3));

        // 测试前缀后缀
        assertEquals("重复3次'a'使用连字符连接并添加前缀后缀应该得到'[a-a-a]'", "[a-a-a]", UniversalString.repeat("-", null, null, "[", "]", "a", 3));

        // 测试元素前缀后缀
        assertEquals("重复3次'a'使用逗号连接并为每个元素添加括号应该得到'(a),(a),(a)'", "(a),(a),(a)", UniversalString.repeat(",", "(", ")", null, null, "a", 3));

        // 测试0次和负数次重复
        assertEquals("重复0次应该得到空字符串", "", UniversalString.repeat(",", null, null, null, null, "a", 0));
        assertEquals("重复负数次应该得到空字符串", "", UniversalString.repeat(",", null, null, null, null, "a", -1));

        // 测试特殊情况：重复次数为1
        assertEquals("重复1次应该得到'a'", "a", UniversalString.repeat(",", null, null, null, null, "a", 1));

        // 测试null输入字符串
        assertEquals("重复null字符串应该得到前缀后缀", "[]", UniversalString.repeat(",", null, null, "[", "]", null, 3));
    }

    // ==================== 字符串相似度测试 ====================

    @Test
    public void test_similarity() {
        // 测试相同字符串
        assertEquals(1.0, UniversalString.similarity(false, "hello", "hello", null), 0.001);

        // 测试完全不同的字符串
        assertEquals(0.0, UniversalString.similarity(false, "abc", "xyz", null), 0.001);

        // 测试null输入
        assertEquals(1.0, UniversalString.similarity(false, null, null, null), 0.001);
        assertEquals(0.0, UniversalString.similarity(false, "hello", null, null), 0.001);
        assertEquals(0.0, UniversalString.similarity(false, null, "hello", null), 0.001);

        // 测试空字符串
        assertEquals(1.0, UniversalString.similarity(false, "", "", null), 0.001);
        assertEquals(0.0, UniversalString.similarity(false, "hello", "", null), 0.001);
        assertEquals(0.0, UniversalString.similarity(false, "", "hello", null), 0.001);

        // 测试忽略大小写
        assertEquals(1.0, UniversalString.similarity(true, "Hello", "hello", null), 0.001);
    }

    @Test
    public void test_similarDistance() {
        // 测试相同字符串
        assertEquals(0, UniversalString.similarDistance(false, "hello", "hello", null));

        // 测试null输入
        assertEquals(0, UniversalString.similarDistance(false, null, null, null));
        assertEquals(5, UniversalString.similarDistance(false, "hello", null, null));
        assertEquals(5, UniversalString.similarDistance(false, null, "hello", null));

        // 测试空字符串
        assertEquals(0, UniversalString.similarDistance(false, "", "", null));
        assertEquals(5, UniversalString.similarDistance(false, "hello", "", null));
        assertEquals(5, UniversalString.similarDistance(false, "", "hello", null));
    }

    // ==================== 字符串截取测试 ====================

    @Test
    public void test_substring() throws NoSuchMethodException {
        String str = "Hello World";

        // 测试正常情况
        assertEquals("截取索引1到5应该得到'ello'", "ello", UniversalString.substring(str, 1, 5));
        assertEquals("截取索引6到11应该得到'World'", "World", UniversalString.substring(str, 6, 11));

        // 测试边界情况
        assertEquals("截取索引0到0应该得到空字符串", "", UniversalString.substring(str, 0, 0));
        assertEquals("截取整个字符串应该得到原字符串", str, UniversalString.substring(str, 0, str.length()));
        assertEquals("截取末尾应该得到空字符串", "", UniversalString.substring(str, str.length(), str.length()));

        // 测试越界情况
        assertEquals("结束索引越界应该截取到字符串末尾", "Hello World", UniversalString.substring(str, 0, 20));
        assertEquals("开始和结束索引都越界应该截取相应部分", "World", UniversalString.substring(str, 6, 20));
        assertEquals("开始索引越界应该得到空字符串", "", UniversalString.substring(str, 20, 30));

        // 测试null值
        assertEquals("null字符串截取应该得到空字符串", "", UniversalString.substring(null, 0, 5));

        // 测试正常情况
        Method method = String.class.getMethod("substring", int.class, int.class);
        String result = UniversalString.toMethodTag(method, "param");
        assertEquals("String substring(int param0, int param1)", result);

        // 测试void返回类型
        method = StringBuilder.class.getMethod("append", String.class);
        result = UniversalString.toMethodTag(method, "param");
        assertEquals("StringBuilder append(String param0)", result);

        // 测试无参数方法
        method = String.class.getMethod("length");
        result = UniversalString.toMethodTag(method, "param");
        assertEquals("int length()", result);

        // 测试正常情况
        result = UniversalString.toMethodTag("substring", String.class, new Class[]{int.class, int.class}, null, "param");
        assertEquals("String substring(int param0, int param1)", result);

        // 测试void返回类型
        result = UniversalString.toMethodTag("append", StringBuilder.class, new Class[]{String.class}, null, "param");
        assertEquals("StringBuilder append(String param0)", result);

        // 测试无参数方法
        result = UniversalString.toMethodTag("length", int.class, new Class[]{}, null, "param");
        assertEquals("int length()", result);

        // 测试null参数名称数组
        result = UniversalString.toMethodTag("substring", String.class, new Class[]{int.class, int.class}, null, null);
        assertEquals("String substring(int var0, int var1)", result);
    }

    // ==================== 字符串分割测试 ====================

    @Test
    public void test_split() {
        String str = "a,b,c";

        // 测试基本分割
        List<String> result = UniversalString.split(true, ",", null, null, null, null, Character::isWhitespace, str);
        assertEquals("分割结果应该包含3个元素", 3, result.size());
        assertEquals("第一个元素应该是'a'", "a", result.get(0));
        assertEquals("第二个元素应该是'b'", "b", result.get(1));
        assertEquals("第三个元素应该是'c'", "c", result.get(2));

        // 测试保留空元素
        List<String> result2 = UniversalString.split(true, ",", null, null, null, null, Character::isWhitespace, "a,,b");
        assertEquals("保留空元素时应该包含3个元素", 3, result2.size());

        List<String> result3 = UniversalString.split(false, ",", null, null, null, null, Character::isWhitespace, "a,,b");
        assertEquals("不保留空元素时应该包含2个元素", 2, result3.size());

        // 测试null值
        assertEquals("null字符串分割(保留空元素)应该包含1个元素", 1, UniversalString.split(true, ",", null, null, null, null, Character::isWhitespace, null).size());
        assertEquals("null字符串分割(不保留空元素)应该包含0个元素", 0, UniversalString.split(false, ",", null, null, null, null, Character::isWhitespace, null).size());

        // 测试基本的字符串分割功能
        result = UniversalString.split(false, ",", null, null, null, null, Character::isWhitespace, "a,b,c");
        assertEquals(3, result.size());
        assertEquals("a", result.get(0));
        assertEquals("b", result.get(1));
        assertEquals("c", result.get(2));

        // 测试空输入字符串的情况
        List<String> resultEmpty = UniversalString.split(false, ",", null, null, null, null, Character::isWhitespace, "");
        assertTrue(resultEmpty.isEmpty());

        List<String> resultWithKeepEmpty = UniversalString.split(true, ",", null, null, null, null, Character::isWhitespace, "");
        assertEquals(1, resultWithKeepEmpty.size());
        assertEquals("", resultWithKeepEmpty.get(0));

        // 测试null输入字符串的情况
        List<String> resultNull = UniversalString.split(false, ",", null, null, null, null, Character::isWhitespace, null);
        assertTrue(resultNull.isEmpty());

        List<String> resultWithKeepEmptyNull = UniversalString.split(true, ",", null, null, null, null, Character::isWhitespace, null);
        assertFalse(resultWithKeepEmptyNull.isEmpty());

        // 测试保留空元素的情况
        List<String> resultKeepEmpty = UniversalString.split(true, ",", null, null, null, null, Character::isWhitespace, "a,,c");
        assertEquals(3, resultKeepEmpty.size());
        assertEquals("a", resultKeepEmpty.get(0));
        assertEquals("", resultKeepEmpty.get(1));
        assertEquals("c", resultKeepEmpty.get(2));

        List<String> resultKeepEmpty2 = UniversalString.split(true, ",", null, null, null, null, Character::isWhitespace, ",b,");
        assertEquals(3, resultKeepEmpty2.size());
        assertEquals("", resultKeepEmpty2.get(0));
        assertEquals("b", resultKeepEmpty2.get(1));
        assertEquals("", resultKeepEmpty2.get(2));

        // 测试不保留空元素的情况
        List<String> resultNotKeepEmpty = UniversalString.split(false, ",", null, null, null, null, Character::isWhitespace, "a,,c");
        assertEquals(2, resultNotKeepEmpty.size());
        assertEquals("a", resultNotKeepEmpty.get(0));
        assertEquals("c", resultNotKeepEmpty.get(1));

        List<String> resultNotKeepEmpty2 = UniversalString.split(false, ",", null, null, null, null, Character::isWhitespace, ",b,");
        assertEquals(1, resultNotKeepEmpty2.size());
        assertEquals("b", resultNotKeepEmpty2.get(0));

        // 测试带有前缀和后缀的情况
        List<String> resultWithPrefixSuffix = UniversalString.split(false, ",", null, null, "[", "]", Character::isWhitespace, "[a,b,c]");
        assertEquals(3, resultWithPrefixSuffix.size());
        assertEquals("a", resultWithPrefixSuffix.get(0));
        assertEquals("b", resultWithPrefixSuffix.get(1));
        assertEquals("c", resultWithPrefixSuffix.get(2));

        List<String> resultWithPrefixSuffix2 = UniversalString.split(true, ",", null, null, "[", "]", Character::isWhitespace, "[a,,,,c]");
        assertEquals(5, resultWithPrefixSuffix2.size());
        assertEquals("a", resultWithPrefixSuffix2.get(0));
        assertEquals("", resultWithPrefixSuffix2.get(1));
        assertEquals("", resultWithPrefixSuffix2.get(2));
        assertEquals("", resultWithPrefixSuffix2.get(3));
        assertEquals("c", resultWithPrefixSuffix2.get(4));

        // 测试带有元素前缀和后缀的情况
        List<String> resultWithElementPrefixSuffix = UniversalString.split(false, ",", "\"", "\"", null, null, Character::isWhitespace, "\"a\",\"b\",\"c\"");
        assertEquals(3, resultWithElementPrefixSuffix.size());
        assertEquals("a", resultWithElementPrefixSuffix.get(0));
        assertEquals("b", resultWithElementPrefixSuffix.get(1));
        assertEquals("c", resultWithElementPrefixSuffix.get(2));

        List<String> resultWithElementPrefixSuffix2 = UniversalString.split(true, ",", "\"", "\"", null, null, Character::isWhitespace, "\"a\",\"\",\"c\"");
        assertEquals(3, resultWithElementPrefixSuffix2.size());
        assertEquals("a", resultWithElementPrefixSuffix2.get(0));
        assertEquals("", resultWithElementPrefixSuffix2.get(1));
        assertEquals("c", resultWithElementPrefixSuffix2.get(2));

        // 测试复杂情况：同时包含整体前缀后缀和元素前缀后缀
        List<String> resultComplex = UniversalString.split(false, ",", "\"", "\"", "[", "]", Character::isWhitespace, "[\"a\",\"b\",\"c\"]");
        assertEquals(3, resultComplex.size());
        assertEquals("a", resultComplex.get(0));
        assertEquals("b", resultComplex.get(1));
        assertEquals("c", resultComplex.get(2));

        List<String> resultComplex2 = UniversalString.split(true, ",", "\"", "\"", "[", "]", Character::isWhitespace, "[\"a\",\"\",\"c\"]");
        assertEquals(3, resultComplex2.size());
        assertEquals("a", resultComplex2.get(0));
        assertEquals("", resultComplex2.get(1));
        assertEquals("c", resultComplex2.get(2));

        // 测试没有分隔符的情况
        List<String> resultNoDelimiter = UniversalString.split(false, null, "\"", "\"", "[", "]", Character::isWhitespace, "[\"abc\"]");
        assertEquals(1, resultNoDelimiter.size());
        assertEquals("abc", resultNoDelimiter.get(0));

        List<String> resultNoDelimiter2 = UniversalString.split(true, "", "\"", "\"", "[", "]", Character::isWhitespace, "[\"abc\"]");
        assertEquals(1, resultNoDelimiter2.size());
        assertEquals("abc", resultNoDelimiter2.get(0));

        // 测试只有前缀没有后缀的情况
        List<String> resultOnlyPrefix = UniversalString.split(false, ",", null, null, "[", null, Character::isWhitespace, "[a,b,c");
        assertEquals(3, resultOnlyPrefix.size());
        assertEquals("a", resultOnlyPrefix.get(0));
        assertEquals("b", resultOnlyPrefix.get(1));
        assertEquals("c", resultOnlyPrefix.get(2));

        // 测试只有后缀没有前缀的情况
        List<String> resultOnlySuffix = UniversalString.split(false, ",", null, null, null, "]", Character::isWhitespace, "a,b,c]");
        assertEquals(3, resultOnlySuffix.size());
        assertEquals("a", resultOnlySuffix.get(0));
        assertEquals("b", resultOnlySuffix.get(1));
        assertEquals("c", resultOnlySuffix.get(2));

        // 测试前缀或后缀不存在的情况
        List<String> resultNoMatch = UniversalString.split(false, ",", null, null, "[", "]", Character::isWhitespace, "a,b,c");
        assertEquals(3, resultNoMatch.size());
        assertEquals("a", resultNoMatch.get(0));
        assertEquals("b", resultNoMatch.get(1));
        assertEquals("c", resultNoMatch.get(2));

        // 测试特殊字符作为分隔符
        List<String> resultSpecialDelimiter = UniversalString.split(false, "|", null, null, null, null, Character::isWhitespace, "a|b|c");
        assertEquals(3, resultSpecialDelimiter.size());
        assertEquals("a", resultSpecialDelimiter.get(0));
        assertEquals("b", resultSpecialDelimiter.get(1));
        assertEquals("c", resultSpecialDelimiter.get(2));

        List<String> resultSpecialDelimiter2 = UniversalString.split(false, ";;", null, null, null, null, Character::isWhitespace, "a;;b;;c");
        assertEquals(3, resultSpecialDelimiter2.size());
        assertEquals("a", resultSpecialDelimiter2.get(0));
        assertEquals("b", resultSpecialDelimiter2.get(1));
        assertEquals("c", resultSpecialDelimiter2.get(2));

        // 测试空分隔符的情况
        List<String> resultEmptyDelimiter = UniversalString.split(false, "", null, null, null, null, Character::isWhitespace, "abc");
        assertEquals(1, resultEmptyDelimiter.size());
        assertEquals("abc", resultEmptyDelimiter.get(0));

        // 测试Unicode字符
        List<String> resultUnicode = UniversalString.split(false, "，", null, null, null, null, Character::isWhitespace, "苹果，香蕉，橙子");
        assertEquals(3, resultUnicode.size());
        assertEquals("苹果", resultUnicode.get(0));
        assertEquals("香蕉", resultUnicode.get(1));
        assertEquals("橙子", resultUnicode.get(2));
    }

    // ==================== 字符串修剪测试 ====================

    @Test
    public void test_trim() {
        // 1. 基本功能测试
        assertEquals("hello", UniversalString.trim(false, "  hello  ", true, true));
        assertEquals("  hello", UniversalString.trim(false, "  hello  ", false, true));
        assertEquals("hello  ", UniversalString.trim(false, "  hello  ", true, false));
        assertEquals("  hello  ", UniversalString.trim(false, "  hello  ", false, false));

        // 2. 指定字符修剪测试
        assertEquals("hello", UniversalString.trim(false, "xxhelloxx", true, true, "x"));
        assertEquals("helloxx", UniversalString.trim(false, "xxhelloxx", true, false, "x"));
        assertEquals("xxhello", UniversalString.trim(false, "xxhelloxx", false, true, "x"));
        assertEquals("xxhelloxx", UniversalString.trim(false, "xxhelloxx", false, false, "x"));

        // 3. 多字符序列修剪测试
        assertEquals("hello", UniversalString.trim(false, "xyhelloxy", true, true, "x", "y"));
        assertEquals("helloxy", UniversalString.trim(false, "xyhelloxy", true, false, "x", "y"));
        assertEquals("xyhello", UniversalString.trim(false, "xyhelloxy", false, true, "x", "y"));

        // 4. 长字符串前缀/后缀修剪测试
        assertEquals("content", UniversalString.trim(false, "prefix_content_suffix", true, true, "prefix_", "_suffix"));
        assertEquals("content_suffix", UniversalString.trim(false, "prefix_content_suffix", true, false, "prefix_"));
        assertEquals("prefix_content", UniversalString.trim(false, "prefix_content_suffix", false, true, "_suffix"));

        // 5. 连续重复前缀/后缀处理
        assertEquals("middle", UniversalString.trim(false, "prepremiddlepostsuff", true, true, "pre", "post", "suff"));
        assertEquals("middlepostsuff", UniversalString.trim(false, "prepremiddlepostsuff", true, false, "pre"));
        assertEquals("prepremiddlepost", UniversalString.trim(false, "prepremiddlepostsuff", false, true, "suff"));

        // 6. 忽略大小写测试
        assertEquals("content", UniversalString.trim(true, "PREFIX_content_SUFFIX", true, true, "prefix_", "_suffix"));
        assertEquals("content", UniversalString.trim(true, "AbAbcontentCdCd", true, true, "ab", "cd"));
        assertEquals("hello", UniversalString.trim(true, "XxhelloXX", true, true, "x"));

        // 7. 重叠匹配处理
        assertEquals("bc", UniversalString.trim(false, "aaabcaaa", true, true, "aaa"));
        assertEquals("", UniversalString.trim(false, "aaaaaa", true, true, "aa"));

        // 8. 边界情况测试
        assertEquals("", UniversalString.trim(false, "", true, true, "x"));
        assertEquals("", UniversalString.trim(false, null, true, true, "x"));
        assertEquals("", UniversalString.trim(false, null, true, true));
        assertEquals("", UniversalString.trim(false, "", true, true));
        assertEquals("hello", UniversalString.trim(false, "hello", false, false));

        // 9. 特殊输入处理
        CharSequence[] trimsWithNull = {null, "x", "y"};
        assertEquals("hello", UniversalString.trim(false, "xyhelloxy", true, true, trimsWithNull));

        // 空trims数组回退到默认空白字符处理
        assertEquals("hello", UniversalString.trim(false, "  hello  ", true, true));
        assertEquals("hello", UniversalString.trim(false, "  hello  ", true, true));
        assertEquals("hello", UniversalString.trim(false, "  hello  ", true, true, (CharSequence[]) null));

        // 超长或无效trims处理
        assertEquals("hello", UniversalString.trim(false, "hello", true, true, (CharSequence) null));
        assertEquals("hello", UniversalString.trim(false, "hello", true, true, "verylongstring"));

        // 10. 字符数组修剪测试
        char[] inputCharArray = "  hello  ".toCharArray();
        IntPredicate isWhitespace = Character::isWhitespace;
        assertEquals("去除两端空白字符应该得到'hello'", "hello", UniversalString.trim(inputCharArray, true, true, isWhitespace));
        assertEquals("只去除开头空白字符应该得到'hello  '", "hello  ", UniversalString.trim(inputCharArray, true, false, isWhitespace));
        assertEquals("只去除结尾空白字符应该得到'  hello'", "  hello", UniversalString.trim(inputCharArray, false, true, isWhitespace));

        // 字符数组边界情况
        assertEquals("null字符数组trim应该得到空字符串", "", UniversalString.trim((char[]) null, true, true, isWhitespace));
        assertEquals("空字符数组trim应该得到空字符串", "", UniversalString.trim(new char[0], true, true, isWhitespace));
        assertEquals("null条件只去除结尾空白字符", "test", UniversalString.trim("  test  ".toCharArray(), true, true, null));

        // 特定字符处理
        assertEquals("去除两端'a'字符应该得到'hello'", "hello", UniversalString.trim("aaahelloaaa".toCharArray(), true, true, c -> c == 'a'));
        assertEquals("去除两端数字字符应该得到'hello'", "hello", UniversalString.trim("123hello456".toCharArray(), true, true, Character::isDigit));

        // 单字符处理
        assertEquals("全部为空白字符应该得到空字符串", "", UniversalString.trim("   ".toCharArray(), true, true, isWhitespace));
        assertEquals("单个空白字符应该得到空字符串", "", UniversalString.trim(" ".toCharArray(), true, true, isWhitespace));
        assertEquals("单个非空白字符应该得到'a'", "a", UniversalString.trim("a".toCharArray(), true, true, isWhitespace));
        assertEquals("无空白字符的字符串应该得到'hello'", "hello", UniversalString.trim("hello".toCharArray(), true, true, isWhitespace));

        // 11. 整数数组(Unicode码点)修剪测试
        int[] inputIntArray = "  hello  ".codePoints().toArray();
        assertEquals("去除两端空白字符应该得到'hello'", "hello", UniversalString.trim(inputIntArray, true, true, isWhitespace));
        assertEquals("只去除开头空白字符应该得到'hello  '", "hello  ", UniversalString.trim(inputIntArray, true, false, isWhitespace));
        assertEquals("只去除结尾空白字符应该得到'  hello'", "  hello", UniversalString.trim(inputIntArray, false, true, isWhitespace));

        // 整数数组边界情况
        assertEquals("null整数数组trim应该得到空字符串", "", UniversalString.trim((int[]) null, true, true, isWhitespace));
        assertEquals("空整数数组trim应该得到空字符串", "", UniversalString.trim(new int[0], true, true, isWhitespace));
        assertEquals("null条件只去除结尾空白字符", "test", UniversalString.trim("  test  ".codePoints().toArray(), true, true, null));

        // 特殊字符处理
        assertEquals("去除两端'a'字符应该得到'hello'", "hello", UniversalString.trim("aaahelloaaa".codePoints().toArray(), true, true, c -> c == 'a'));
        assertEquals("去除两端数字字符应该得到'hello'", "hello", UniversalString.trim("123hello456".codePoints().toArray(), true, true, Character::isDigit));
        assertEquals("去除两端🔥emoji应该得到'hello'", "hello", UniversalString.trim("🔥🔥hello🔥🔥".codePoints().toArray(), true, true, c -> c == 0x1F525));
        assertEquals("去除两端'好'字符应该得到'hello'", "hello", UniversalString.trim("好好hello好好".codePoints().toArray(), true, true, c -> c == '好'));
    }

    // ==================== 字符串转换测试 ====================

    @Test
    public void test_toCodePoint() {
        // 测试正常字符串
        int[] result = UniversalString.toCodePoints("hello");
        assertArrayEquals("hello".codePoints().toArray(), result);

        // 测试null输入
        assertArrayEquals(new int[0], UniversalString.toCodePoints(null));

        // 测试空字符串
        assertArrayEquals(new int[0], UniversalString.toCodePoints(""));

        // 测试包含emoji的字符串
        result = UniversalString.toCodePoints("hello😀world");
        assertArrayEquals("hello😀world".codePoints().toArray(), result);
    }

    @Test
    public void test_toChars() {
        // 测试String输入
        char[] result = UniversalString.toChars("hello");
        assertArrayEquals("hello".toCharArray(), result);

        // 测试StringBuilder输入
        StringBuilder sb = new StringBuilder("world");
        result = UniversalString.toChars(sb);
        assertArrayEquals("world".toCharArray(), result);

        // 测试null输入
        assertArrayEquals(new char[0], UniversalString.toChars(null));

        // 测试空字符串
        assertArrayEquals(new char[0], UniversalString.toChars(""));
    }

    @Test
    public void test_toStyledNumberChar() {
        // 测试各种类型的数字字符转换
        assertEquals("数字1类型1应该得到①", '①', UniversalString.toStyledNumberChar(1, 1));
        assertEquals("数字1类型2应该得到⑴", '⑴', UniversalString.toStyledNumberChar(1, 2));
        assertEquals("数字1类型3应该得到⓵", '⓵', UniversalString.toStyledNumberChar(1, 3));
        assertEquals("数字1类型4应该得到⒈", '⒈', UniversalString.toStyledNumberChar(1, 4));

        assertEquals("数字20类型1应该得到⑳", '⑳', UniversalString.toStyledNumberChar(20, 1));
        assertEquals("数字20类型2应该得到⒇", '⒇', UniversalString.toStyledNumberChar(20, 2));
        assertEquals("数字20类型3应该得到⓴", '⓴', UniversalString.toStyledNumberChar(20, 3));
        assertEquals("数字20类型4应该得到⒛", '⒛', UniversalString.toStyledNumberChar(20, 4));

        // 测试异常情况
        try {
            assertEquals("数字0应该抛出异常", ' ', UniversalString.toStyledNumberChar(0, 1));
            fail("期望抛出IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // 预期的异常
        }

        try {
            assertEquals("数字21应该抛出异常", ' ', UniversalString.toStyledNumberChar(21, 1));
            fail("期望抛出IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // 预期的异常
        }
    }

    @Test
    public void test_toLowerCase() {
        // 测试正常情况
        assertEquals("大写转小写应该得到'hello'", "hello", UniversalString.toLowerCase("Hello"));

        String nullString = null;
        // 测试null值
        assertEquals("null转小写应该得到空字符串", "", UniversalString.toLowerCase(nullString));

        // 测试空字符串
        assertEquals("空字符串转小写应该得到空字符串", "", UniversalString.toLowerCase(""));
    }

    @Test
    public void test_toUpperCase() {
        assertEquals("小写转大写应该得到'HELLO'", "HELLO", UniversalString.toUpperCase("Hello"));
        String nullString = null;
        assertEquals("null转大写应该得到空字符串", "", UniversalString.toUpperCase(nullString));
        assertEquals("空字符串转大写应该得到空字符串", "", UniversalString.toUpperCase(""));
    }

    @Test
    public void test_toMethodTag() {
        // 测试正常情况
        String result = UniversalString.toMethodTag("substring", String.class, new Class[]{int.class, int.class}, null, "param");
        assertEquals("String substring(int param0, int param1)", result);

        // 测试void返回类型
        result = UniversalString.toMethodTag("append", StringBuilder.class, new Class[]{String.class}, null, "param");
        assertEquals("StringBuilder append(String param0)", result);

        // 测试无参数方法
        result = UniversalString.toMethodTag("length", int.class, new Class[]{}, null, "param");
        assertEquals("int length()", result);

        // 测试null参数名称数组
        result = UniversalString.toMethodTag("substring", String.class, new Class[]{int.class, int.class}, null, null);
        assertEquals("String substring(int var0, int var1)", result);
    }
}
