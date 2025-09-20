package com.github.zhitron.universal;

import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.*;

/**
 * @author zhitron
 */
public class UniversalStringParsingTest {


    // ==================== 数字解析测试 ====================

    @Test
    public void test_parseFullToDecimal() {
        // 测试基本数字解析
        assertEquals("777123666.456", UniversalStringParsing.parseFullToDecimal("  777.123.666.456  ", null, null));
        assertEquals("123.456", UniversalStringParsing.parseFullToDecimal("  123.456  ", null, null));
        assertEquals("-0.123", UniversalStringParsing.parseFullToDecimal("  -0.123  ", null, null));
        assertEquals("+1.23E+10", UniversalStringParsing.parseFullToDecimal("  +1.23e10  ", null, null));
        assertEquals("0.123", UniversalStringParsing.parseFullToDecimal("  .123  ", null, null));
        assertEquals("123.0", UniversalStringParsing.parseFullToDecimal("  123.  ", null, null));
        assertEquals("1234.56", UniversalStringParsing.parseFullToDecimal("  1,234.56  ", null, null));

        // 测试无效输入
        assertNull(UniversalStringParsing.parseFullToDecimal("  abc  ", null, null));
        assertNull(UniversalStringParsing.parseFullToDecimal("", null, null));
        assertNull(UniversalStringParsing.parseFullToDecimal("   ", null, null));
        assertNull(UniversalStringParsing.parseFullToDecimal(null, null, null));
        assertEquals("1.23", UniversalStringParsing.parseFullToDecimal("  1.23e  ", null, null));
        assertEquals("1.23", UniversalStringParsing.parseFullToDecimal("  1.23e+  ", null, null));
        assertEquals("1.23", UniversalStringParsing.parseFullToDecimal("  1.23e-  ", null, null));

        // 测试字符替换功能
        assertEquals("1.23", UniversalStringParsing.parseFullToDecimal("  一.二三  ", "一二三", "123"));
        assertEquals("-1.23", UniversalStringParsing.parseFullToDecimal("  负一.二三  ", "负一二三", "-123"));

        // 测试边界情况
        assertEquals("0.0", UniversalStringParsing.parseFullToDecimal(".", null, null));
        assertEquals("+0.0", UniversalStringParsing.parseFullToDecimal("+.", null, null));
        assertEquals("-0.0", UniversalStringParsing.parseFullToDecimal("-.", null, null));
        assertEquals("0.0", UniversalStringParsing.parseFullToDecimal(".", null, null));
        assertNull(UniversalStringParsing.parseFullToDecimal("+", null, null));
        assertNull(UniversalStringParsing.parseFullToDecimal("-", null, null));

        // 测试指数部分
        assertEquals("1.23E+10", UniversalStringParsing.parseFullToDecimal("1.23e10", null, null));
        assertEquals("1.23E-10", UniversalStringParsing.parseFullToDecimal("1.23E-10", null, null));
        assertEquals("1.23E+0", UniversalStringParsing.parseFullToDecimal("1.23e0", null, null));
        assertEquals("1234.56", UniversalStringParsing.parseFullToDecimal("1,234.56", null, null));
    }

    // ==================== 数组解析测试 ====================

    @Test
    public void test_parseArray() {
        // 测试用例1: 基本数组表达式 [a,b,c]
        assertArrayEquals(new String[]{"a", "b", "c"}, UniversalStringParsing.parseArray("[a,b,c]", ',', '\\'));

        assertArrayEquals(new String[]{"a a", "b b", "c c"}, UniversalStringParsing.parseArray("[  a a  ,   b b,  c c  ]", ',', '\\'));

        assertArrayEquals(new String[]{"\"a a\"", "\"b b\"", "'c c'"}, UniversalStringParsing.parseArray("[\"a a\",\"b b\",'c c']", ',', '\\'));

        assertArrayEquals(new String[]{"\"a 'a'\"", "\"b b\"", "'c c'"}, UniversalStringParsing.parseArray("[\"a 'a'\",\"b b\",'c c']", ',', '\\'));

        assertArrayEquals(new String[]{"\"a \\\"a\\\"\"", "\"b b\"", "'c c'"}, UniversalStringParsing.parseArray("[\"a \\\"a\\\"\",\"b b\",'c c']", ',', '\\'));

        // 测试用例6: 空数组表达式 []
        assertArrayEquals(new String[]{}, UniversalStringParsing.parseArray("[]", ',', '\\'));

        // 测试用例7: 单元素数组表达式 [a]
        assertArrayEquals(new String[]{"a"}, UniversalStringParsing.parseArray("[a]", ',', '\\'));

        // 测试用例8: 忽略引号处理 ignoreQuotes=true
        assertArrayEquals(new String[]{"\"a,b\"", "c"}, UniversalStringParsing.parseArray("[\"a,b\",c]", ',', '\\'));

        // 测试用例9: 自定义分隔符
        assertArrayEquals(new String[]{"a", "b", "c"}, UniversalStringParsing.parseArray("[a;b;c]", ';', '\\'));

        // 测试用例10: 自定义括号
        assertArrayEquals(new String[]{"a", "b", "c"}, UniversalStringParsing.parseArray("(a,b,c)", ',', '\\'));

        // 测试用例11: 包含特殊转义字符
        assertArrayEquals(new String[]{"\"a\tb\"", "\"c\nd\""}, UniversalStringParsing.parseArray("[\"a\\tb\",\"c\\nd\"]", ',', '\\'));

        // 测试用例12: 嵌套括号结构
        assertArrayEquals(new String[]{"a(b,c)", "d"}, UniversalStringParsing.parseArray("[a(b,c),d]", ',', '\\'));

        // 测试用例13: null输入
        assertArrayEquals(new String[]{}, UniversalStringParsing.parseArray(null, ',', '\\'));

        // 测试用例14: 空字符串输入
        assertArrayEquals(new String[]{}, UniversalStringParsing.parseArray("", ',', '\\'));

        // 测试用例15: 只有空白字符
        assertArrayEquals(new String[]{}, UniversalStringParsing.parseArray("   ", ',', '\\'));

        // 测试用例16: 包含空格的数组表达式 [   [a a]  ,   b  b,  c c  ]
        assertArrayEquals(new String[]{"[a a]", "b  b", "c c"}, UniversalStringParsing.parseArray("[   [a a]  ,   b  b,  c c  ]", ',', '\\'));

        // 测试用例16: 包含空格的数组表达式 {a:1,b:{d:1,f:2},c:[1,2,3,4,5]}
        assertArrayEquals(new String[]{"a:1", "b:{d:1,f:2}", "c:[1,2,3,4,5]"}, UniversalStringParsing.parseArray("{a:1,b:{d:1,f:2},c:[1,2,3,4,5]}", ',', '\\'));

        // 测试用例17: 复杂json格式
        // 测试嵌套对象和数组的解析
        assertArrayEquals(new String[]{"name:John", "age:30", "address:{city:New York,zip:10001}", "hobbies:[reading,music,travel]"},
                UniversalStringParsing.parseArray("{name:John,age:30,address:{city:New York,zip:10001},hobbies:[reading,music,travel]}", ',', '\\'));

        // 测试包含转义字符的复杂JSON格式
        assertArrayEquals(new String[]{"message:\"Hello, \\\"World\\\"!\"", "path:\"C:\\Users\\test\""},
                UniversalStringParsing.parseArray("{message:\"Hello, \\\"World\\\"!\",path:\"C:\\\\Users\\\\test\"}", ',', '\\'));

        // 测试空对象和空数组
        assertArrayEquals(new String[]{"emptyObj:{}", "emptyArr:[]", "value:null"},
                UniversalStringParsing.parseArray("{emptyObj:{},emptyArr:[],value:null}", ',', '\\'));

        // 测试数组中包含对象
        assertArrayEquals(new String[]{"users:[{id:1,name:Alice},{id:2,name:Bob}]", "settings:{theme:dark,notifications:true}"},
                UniversalStringParsing.parseArray("{users:[{id:1,name:Alice},{id:2,name:Bob}],settings:{theme:dark,notifications:true}}", ',', '\\'));
        // 测试更多边界情况
        assertArrayEquals(new String[]{}, UniversalStringParsing.parseArray("  [  ]  ", ',', '\\'));
        assertArrayEquals(new String[]{"a", "b"}, UniversalStringParsing.parseArray("[a,,b]", ',', '\\'));

        // 测试不同类型的引号组合
        assertArrayEquals(new String[]{"'a\", \"b'"}, UniversalStringParsing.parseArray("['a\", \"b']", ',', '\\'));
    }

    // ==================== 语言环境解析测试 ====================

    @Test
    public void test_parseFullToLocale() {
        // 测试 null 输入
        assertNull(UniversalStringParsing.parseFullToLocale(null));

        // 测试空字符串输入
        assertNull(UniversalStringParsing.parseFullToLocale(""));

        // 测试标准格式 en
        Locale locale = UniversalStringParsing.parseFullToLocale("en");
        assertNotNull(locale);
        assertEquals("en", locale.getLanguage());
        assertEquals("", locale.getCountry());
        assertEquals("", locale.getVariant());

        // 测试带国家的格式 en_US
        locale = UniversalStringParsing.parseFullToLocale("en_US");
        assertNotNull(locale);
        assertEquals("en", locale.getLanguage());
        assertEquals("US", locale.getCountry());
        assertEquals("", locale.getVariant());

        // 测试带空格的格式 en US
        locale = UniversalStringParsing.parseFullToLocale("en US");
        assertNotNull(locale);
        assertEquals("en", locale.getLanguage());
        assertEquals("US", locale.getCountry());
        assertEquals("", locale.getVariant());

        // 测试带连字符的格式 en-US
        locale = UniversalStringParsing.parseFullToLocale("en-US");
        assertNotNull(locale);
        assertEquals("en", locale.getLanguage());
        assertEquals("US", locale.getCountry());
        assertEquals("", locale.getVariant());

        // 测试带变体的格式 en_US_WIN
        locale = UniversalStringParsing.parseFullToLocale("en_US_WIN");
        assertNotNull(locale);
        assertEquals("en", locale.getLanguage());
        assertEquals("US", locale.getCountry());
        assertEquals("WIN", locale.getVariant());

        // 测试带#变体的格式 en__#LATN
        locale = UniversalStringParsing.parseFullToLocale("en__#LATN");
        assertNotNull(locale);
        assertEquals("en", locale.getLanguage());
        assertEquals("", locale.getCountry());
        assertEquals("#LATN", locale.getVariant());

        // 测试复杂变体格式
        locale = UniversalStringParsing.parseFullToLocale("de_DE_PREEURO");
        assertNotNull(locale);
        assertEquals("de", locale.getLanguage());
        assertEquals("DE", locale.getCountry());
        assertEquals("PREEURO", locale.getVariant());

        // 测试更多语言环境边界情况
        locale = UniversalStringParsing.parseFullToLocale("zh_CN");
        assertNotNull(locale);
        assertEquals("zh", locale.getLanguage());
        assertEquals("CN", locale.getCountry());

        locale = UniversalStringParsing.parseFullToLocale("fr_FR");
        assertNotNull(locale);
        assertEquals("fr", locale.getLanguage());
        assertEquals("FR", locale.getCountry());

        // 测试无效字符
        try {
            UniversalStringParsing.parseFullToLocale("en@US");
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // 预期异常
        }

        // 测试包含特殊字符的无效格式
        try {
            UniversalStringParsing.parseFullToLocale("en@US$");
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // 预期异常
        }
    }

}
