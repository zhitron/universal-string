package com.github.zhitron.universal;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.IntUnaryOperator;
import java.util.stream.LongStream;

/**
 * 字符串解析工具类，提供高效可靠的字符串转数字解析功能
 *
 * @author zhitron
 */
public class UniversalStringParsing {
    /**
     * Integer.MAX_VALUE的字符数组表示
     * 用于快速比较和验证整数字符串是否超出int类型范围
     */
    private static final char[] INTEGER_MAX_CHAR = String.valueOf(Integer.MAX_VALUE).toCharArray();
    /**
     * Long.MAX_VALUE的字符数组表示
     * 用于快速比较和验证整数字符串是否超出long类型范围
     */
    private static final char[] LONG_MAX_CHAR = String.valueOf(Long.MAX_VALUE).toCharArray();
    /**
     * 表示null值的字符数组，用于快速比较和匹配null字符串
     */
    private static final int[] NULL = {'n', 'u', 'l', 'l'};
    /**
     * 表示undefined值的字符数组，用于快速比较和匹配undefined字符串
     */
    private static final int[] UNDEFINED = {'u', 'n', 'd', 'e', 'f', 'i', 'n', 'e', 'd'};
    /**
     * 表示true值的字符数组，用于快速比较和匹配true字符串
     */
    private static final int[] TRUE = {'t', 'r', 'u', 'e'};
    /**
     * 表示false值的字符数组，用于快速比较和匹配false字符串
     */
    private static final int[] FALSE = {'f', 'a', 'l', 's', 'e'};

    /**
     * 私有构造函数，防止此类被实例化。
     * 抛出AssertionError以确保在编译和运行时都无法创建此类的实例。
     */
    private UniversalStringParsing() {
        throw new AssertionError("No instances.");
    }

    /**
     * 将十六进制字符转换为对应的数字
     *
     * @param codepoint 十六进制字符的Unicode码点
     * @return 对应的数字值，如果输入不是有效的十六进制字符则返回-1
     */
    public static int parseHexToNumber(int codepoint) {
        // 判断是否为0-9之间的数字字符
        if (codepoint >= '0' && codepoint <= '9') {
            codepoint = codepoint - '0';
        } else if (codepoint >= 'a' && codepoint <= 'f') {
            // 判断是否为a-f之间的字母字符
            codepoint = codepoint - 'a' + 10;
        } else if (codepoint >= 'A' && codepoint <= 'F') {
            // 判断是否为A-F之间的字母字符
            codepoint = codepoint - 'A' + 10;
        } else {
            // 如果不是有效的十六进制字符，返回-1
            codepoint = -1;
        }
        return codepoint;
    }

    /**
     * 解析Unicode转义序列并返回对应的代码点
     * <p>
     * 该方法用于处理形如 \\uXXXX 或 \\UXXXXXXXX 的Unicode转义序列，
     * 支持基本多语言平面（BMP）和辅助平面字符（Supplemental Planes）。
     * </p>
     *
     * @param prefetchOperator 用于预取下一个字符代码点的函数接口
     * @param consumeOperator  用于消费已解析字符数量的函数接口
     * @param isBMP            指示当前处理的是BMP字符（对应'\\u'）还是辅助平面字符（对应'\\U'）
     * @return 解析后的Unicode代码点，如果失败则返回-1
     */
    public static int parseUnicodeToCodepoint(IntUnaryOperator prefetchOperator, IntUnaryOperator consumeOperator, boolean isBMP) {
        // 参数校验
        if (prefetchOperator == null || consumeOperator == null) {
            return -1;
        }

        // 创建一个数组来存储解析后的十六进制数字（最多8位）
        int[] hexes = new int[8];
        int codepoint = 0, count = 0;

        // 确定需要解析的最小和最大字符数
        int minDigits = 4;  // BMP需要4位，非BMP至少4位
        int maxDigits = isBMP ? 4 : 8;  // BMP需要4位，非BMP最多8位

        // 解析十六进制字符
        for (int i = 0; i < maxDigits; i++) {
            int hexValue = UniversalStringParsing.parseHexToNumber(prefetchOperator.applyAsInt(i));
            if (hexValue == -1) {
                // 如果在最小位数之前就遇到非十六进制字符，则失败
                if (i < minDigits) {
                    return -1;
                }
                // 否则停止解析
                break;
            }
            hexes[count++] = hexValue;
        }

        // 检查是否解析到了足够的字符数
        if (count < minDigits) {
            return -1;
        }

        // 组合十六进制字符为代码点
        for (int i = 0; i < count; i++) {
            codepoint = (codepoint << 4) | hexes[i];
        }

        // 验证代码点有效性
        // 1. 代码点为0（空字符）被视为无效
        // 2. BMP字符落在0xD800~0xDFFF范围内是非法的（这是UTF-16代理对区域）
        // 3. 所有字符超过0x10FFFF为非法（Unicode最大有效码点为0x10FFFF）
        if (codepoint == 0 ||
                (codepoint >= 0xD800 && codepoint <= 0xDFFF) ||
                codepoint > 0x10FFFF) {
            return -1;
        }

        // 成功解析后消费字符
        consumeOperator.applyAsInt(count);

        return codepoint;
    }

    /**
     * 解析字符串片段，判断其是否表示null值
     * <p>
     * 此方法通过字符预取器判断给定的字符串是否符合null或undefined的表示（不区分大小写）。
     * </p>
     *
     * @param codepointPrefetch 用于逐个获取字符的函数接口
     * @param consumeOperator   用于消费已匹配字符数量的函数接口
     * @return 如果字符串表示null或undefined（忽略大小写），则返回true；否则返回false
     */
    public static boolean parsePartialToNull(IntUnaryOperator codepointPrefetch, IntUnaryOperator consumeOperator) {
        if (UniversalString.isEquals(true, codepointPrefetch, NULL)) {
            consumeOperator.applyAsInt(NULL.length);
            return true;
        }
        if (UniversalString.isEquals(true, codepointPrefetch, UNDEFINED)) {
            consumeOperator.applyAsInt(UNDEFINED.length);
            return true;
        }
        return false;
    }

    /**
     * 将字符串片段解析为布尔值
     * <p>
     * 此方法通过字符预取器处理包含布尔值的字符串，返回相应的布尔值true或false。
     * </p>
     *
     * @param codepointPrefetch 用于逐个获取字符的函数接口
     * @param consumeOperator   用于消费已匹配字符数量的函数接口
     * @return 解析后的布尔值，如果无法解析则返回null
     */
    public static Boolean parsePartialToBoolean(IntUnaryOperator codepointPrefetch, IntUnaryOperator consumeOperator) {
        // 检查 "true" 或 "True" 等变体
        if (UniversalString.isEquals(true, codepointPrefetch, TRUE)) {
            consumeOperator.applyAsInt(TRUE.length);
            return true;
        }
        if (UniversalString.isEquals(true, codepointPrefetch, FALSE)) {
            consumeOperator.applyAsInt(FALSE.length);
            return false;
        }
        return null;
    }

    /**
     * 解析完整字符串，判断其是否表示null值
     * 此方法主要用于判断给定的字符串是否符合null的表示（不区分大小写）
     *
     * @param input 待解析的字符串
     * @return 如果字符串表示null（忽略大小写），则返回true；否则返回false
     */
    public static boolean parseFullToNull(CharSequence input) {
        // 判断输入字符串是否为空，如果是则返回false
        if (input == null || input.length() == 0) {
            return false;
        }

        // 将输入字符串转换为字符数组（去除空白字符）
        char[] chars = UniversalString.asCharacters(input, UniversalString::isWhitespace);

        // 检查字符串是否为"null"（不区分大小写）
        if (chars.length == 4) {
            for (int i = 0; i < NULL.length; i++) {
                if (Character.toLowerCase(chars[i]) != NULL[i]) {
                    return false;
                }
            }
            return true;
        }
        // 检查是否为 "undefined"
        else if (chars.length == 9) {
            for (int i = 0; i < UNDEFINED.length; i++) {
                if (Character.toLowerCase(chars[i]) != UNDEFINED[i]) {
                    return false;
                }
            }
            return true;
        }
        // 如果字符串不表示"null"或"undefined"，则返回false
        return false;
    }

    /**
     * 将完整字符串解析为布尔值
     * 此方法处理包含布尔值的字符串，返回相应的布尔值true或false
     *
     * @param input 待解析的字符串
     * @return 解析后的布尔值，如果无法解析则返回null
     */
    public static Boolean parseFullToBoolean(CharSequence input) {
        // 检查输入是否为空
        if (input == null || input.length() == 0) {
            return null;
        }

        // 将输入字符串转换为字符数组（去除空白字符）
        char[] chars = UniversalString.asCharacters(input, UniversalString::isWhitespace);

        // 检查是否为布尔值字符串"true"
        if (chars.length == 4) {
            for (int i = 0; i < TRUE.length; i++) {
                if (!UniversalString.isEquals(true, chars[i], TRUE[i])) {
                    return null;
                }
            }
            return true;
        }
        // 检查是否为布尔值字符串"false"
        else if (chars.length == 5) {
            for (int i = 0; i < FALSE.length; i++) {
                if (!UniversalString.isEquals(true, chars[i], FALSE[i])) {
                    return null;
                }
            }
            return false;
        }

        // 如果不是有效的布尔值字符串，返回null
        return null;
    }

    /**
     * 解析完整的10进制数字字符串
     *
     * @param input 待解析的字符串
     * @return 解析成功的数字或null
     */
    public static Number parseFullToDecimal(CharSequence input) {
        // 检查输入是否为空
        if (input == null || input.length() == 0) {
            return null;
        }

        // 将输入字符串转换为字符数组（去除空白字符）
        char[] chars = UniversalString.asCharacters(input, UniversalString::isWhitespace);
        if (chars.length == 0) return null;

        // 初始化起始索引和结束索引
        int si = 0, ei = chars.length - 1;

        // 判断数字是否为负数
        boolean isNegative = false;
        // 处理数字的正负号
        switch (chars[si]) {
            case '-':
                isNegative = true;
            case '+':
                si++;
                break;
        }

        // 初始化指数部分的索引e，小数点的索引d，幂次小数点的索引ed
        int e = -1, d = -1, ed = -1;
        // 遍历字符数组，解析数字
        for (int i = si; i <= ei; i++) {
            char c = chars[i];
            // 处理小数点和幂次小数点
            switch (c) {
                case '.':
                    if (e == -1) {
                        if (d != -1) return null;
                        d = i;
                    } else {
                        if (ed != -1) return null;
                        ed = i;
                    }
                    break;
                // 处理指数部分
                case 'e':
                case 'E':
                    if (e != -1) return null;
                    e = i;
                    break;
                // 处理指数部分的正负号
                case '-':
                case '+':
                    if (e + 1 != i) return null;
                    break;
                // 处理数字字符
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    break;
                // 遇到非数字字符，返回null
                default:
                    return null;
            }
        }

        // 检查指数部分是否有数字
        if (e != -1 && e == ei) return null;
        if (e != -1 && (chars[e + 1] == '+' || chars[e + 1] == '-') && e + 1 == ei) return null;

        // 提取解析后的数字字符串
        CharSequence number = input.subSequence(si - (isNegative ? 1 : 0), ei + 1);

        // 如果没有小数点和幂次小数点，尝试解析为整数类型
        if (e == -1 && d == -1 && ed == -1) {
            int len = ei - si + 1;
            boolean isInt = true;

            // 检查整数部分是否超过int类型的范围
            if (len == 10) {
                for (int i = 0; i < INTEGER_MAX_CHAR.length && i <= ei; i++) {
                    int cmp = chars[si + i] - INTEGER_MAX_CHAR[i] - (isNegative && i == INTEGER_MAX_CHAR.length - 1 ? 1 : 0);
                    if (cmp < 0) {
                        break;
                    } else if (cmp > 0) {
                        isInt = false;
                        break;
                    }
                }
            } else if (len > 10) {
                isInt = false;
            }

            // 如果整数部分在int类型范围内，解析为int类型
            if (isInt) {
                int result = chars[si] - '0';
                if (isNegative) result = -result;

                if (isNegative) {
                    for (int i = si + 1; i <= ei; i++) {
                        result *= 10;
                        result -= chars[i] - '0';
                    }
                } else {
                    for (int i = si + 1; i <= ei; i++) {
                        result *= 10;
                        result += chars[i] - '0';
                    }
                }
                return result;
            }

            boolean isLong = true;
            // 检查整数部分是否超过long类型的范围
            if (len == 19) {
                for (int i = 0; i < LONG_MAX_CHAR.length && i <= ei; i++) {
                    int cmp = chars[si + i] - LONG_MAX_CHAR[i] - (isNegative && i == LONG_MAX_CHAR.length - 1 ? 1 : 0);
                    if (cmp < 0) {
                        break;
                    } else if (cmp > 0) {
                        isLong = false;
                        break;
                    }
                }
            } else if (len > 19) {
                isLong = false;
            }

            // 如果整数部分在long类型范围内，解析为long类型
            if (isLong) {
                long result = chars[si] - '0';
                if (isNegative) result = -result;
                if (isNegative) {
                    for (int i = si + 1; i <= ei; i++) {
                        result *= 10L;
                        result -= chars[i] - '0';
                    }
                } else {
                    for (int i = si + 1; i <= ei; i++) {
                        result *= 10L;
                        result += chars[i] - '0';
                    }
                }
                return result;
            }

            // 如果整数部分超过long类型范围，解析为BigInteger类型
            return new BigInteger(number.toString());
        }

        // 如果包含小数点或幂次小数点，解析为BigDecimal类型
        return new BigDecimal(number.toString());
    }

    /**
     * 解析并格式化一个字符串为标准的十进制数字字符串。
     * 该方法会去除首尾空白字符，并根据传入的映射关系替换特定字符，
     * 然后尝试将输入解析为符合科学计数法格式的十进制数字字符串。
     *
     * @param input  输入字符串，可能包含数字、符号、小数点和指数部分
     * @param source 需要被替换的字符集合（如自定义数字字符）
     * @param target 用于替换的字符集合，与source一一对应
     * @return 格式化后的标准十进制数字字符串；如果输入无效则返回null
     * @throws IllegalArgumentException 当source和target长度不同或存在重复映射时抛出异常
     */
    public static String parseFullToDecimal(CharSequence input, CharSequence source, CharSequence target) {
        if (input == null || input.length() == 0) {
            return null;
        }
        // 1. 去除前后空白字符，确定有效内容范围
        int[] codepoints = UniversalString.asCodepoints(input, UniversalString::isWhitespaceOrQuotes, source, target);
        if (codepoints == null || codepoints.length == 0) {
            return null;
        }

        // 查找小数点位置（从后向前），用于判断是否存在合法的小数部分
        int dotIndex = codepoints.length - 1;
        while (0 <= dotIndex && codepoints[dotIndex] != '.') {
            dotIndex--;
        }

        // 4. 构造结果数字字符数组，预留额外空间以应对格式转换
        int[] number = new int[codepoints.length + 8];
        int c = 0, i = 0;
        // 处理可选的正负号
        if (codepoints[i] == '+' || codepoints[i] == '-') {
            number[c++] = codepoints[i++];
        }

        // 5. 标记是否出现过数字(必须至少有一个数字)
        boolean hasDigit = false;

        // 6. 处理整数部分(0个或多个数字)，跳过千位分隔符
        while (i < codepoints.length) {
            if (codepoints[i] == ',' || (codepoints[i] == '.' && dotIndex != -1 && i < dotIndex)) {
                // 跳过千位分隔符或小数点前的点号
                i++;
            } else if (Character.isDigit(codepoints[i])) {
                hasDigit = true;
                number[c++] = codepoints[i++];
            } else {
                break;
            }
        }
        // 7. 检查并处理小数点及小数部分
        if (i < codepoints.length && codepoints[i] == '.') {
            i++;
            // 如果整数部分没有数字，则补零
            if (!hasDigit) {
                number[c++] = '0';
            }
            number[c++] = '.';
            hasDigit = true;
            // 处理小数部分数字
            if (i < codepoints.length && Character.isDigit(codepoints[i])) {
                number[c++] = codepoints[i++];
                while (i < codepoints.length && Character.isDigit(codepoints[i])) {
                    number[c++] = codepoints[i++];
                }
            } else {
                // 小数点后无数字则默认补零
                number[c++] = '0';
            }
        }

        // 8. 此时如果没有数字,说明无效(如".", "+.", "-"等)
        if (!hasDigit) {
            return null;
        }

        // 9. 检查并处理指数部分('e'或'E')
        if (i < codepoints.length && (codepoints[i] == 'e' || codepoints[i] == 'E')) {
            i++;
            int record = c;
            number[c++] = 'E';
            // 指数部分可选符号，默认为'+'
            if (i < codepoints.length && (codepoints[i] == '+' || codepoints[i] == '-')) {
                number[c++] = codepoints[i++];
            } else {
                number[c++] = '+';
            }
            // 指数部分必须至少有一个数字
            int sign = c;
            while (i < codepoints.length && Character.isDigit(codepoints[i])) {
                number[c++] = codepoints[i++];
            }
            // 若指数部分无有效数字，则回退到记录点
            if (sign == c) {
                c = record;
            }
        }

        // 10. 必须整个字符串都被消耗完才算合法
        if (i == codepoints.length) {
            return new String(number, 0, c);
        }
        return null;
    }

    /**
     * 将给定的{@code String}表示形式解析为{@link Locale}
     * 支持多种格式如: en, en_US, en US, en-US
     *
     * @param input 语言环境名称
     * @return 相应的{@code Locale}实例，如果没有，则为{@code null}
     * @throws IllegalArgumentException 如果在无效的语言环境规范的情况下
     */
    public static Locale parseFullToLocale(CharSequence input) {
        // 检查输入是否为空
        if (input == null || input.length() == 0) {
            return null;
        }

        // 将输入字符串转换为字符数组
        char[] inputChars = UniversalString.asCharacters(input);

        // 验证字符有效性并直接在数组中替换分隔符
        for (int i = 0; i < inputChars.length; i++) {
            if (inputChars[i] == '_' || inputChars[i] == '#') {
                // 保留 _ 和 #，无需操作
                continue;
            }
            if (inputChars[i] == ' ' || inputChars[i] == '-') {
                inputChars[i] = '_';
            } else if (!Character.isLetterOrDigit(inputChars[i])) {
                throw new IllegalArgumentException("The locale must be ' ','_','-','#',letter or digit");
            }
        }

        // 手动分割int数组，使用_作为分隔符
        LongStream.Builder builder = LongStream.builder();
        for (int i = 0, start = 0; i <= inputChars.length; i++) {
            if (i == inputChars.length || inputChars[i] == '_') {
                builder.add(i > start ? ((start & 0xFFFFFFFFL) << 32) | (i - start) : 0);
                start = i + 1;
            }
        }

        // 获取分割后的部分
        long[] parts = builder.build().toArray();
        if (parts.length == 0 || parts[0] == 0) {
            return null;
        }

        // 提取语言、国家和地区部分
        String language = new String(inputChars, (int) (parts[0] >>> 32), (int) parts[0]);
        String country = "";
        String variant = "";

        if (parts.length > 1 && parts[1] != 0) {
            country = new String(inputChars, (int) (parts[1] >>> 32), (int) parts[1]);
        }

        for (int i = 2; i < parts.length; i++) {
            if (parts[i] != 0) {
                int offset = (int) (parts[i] >>> 32);
                variant = new String(inputChars, offset, inputChars.length - offset);
                break;
            }
        }

        // 特殊处理以 # 开头的 variant
        if (UniversalString.isEmpty(variant) && country.startsWith("#")) {
            variant = country;
            country = "";
        }

        return new Locale(language, country, variant);
    }

    /**
     * 解析转义字符或反转义字符
     *
     * <p>该方法支持两种模式：
     * <ul>
     *   <li>当 {@code unescape} 为 {@code true} 时，将常见的转义字符（如换行符、制表符等）
     *       转换为其对应的转义序列表示（如 'n' 表示 '\n'）</li>
     *   <li>当 {@code unescape} 为 {@code false} 时，将转义序列字符转换为实际的控制字符</li>
     * </ul>
     *
     * <p>支持的转义字符包括：
     * <ul>
     *   <li>'\t' (制表符) &lt;--&gt; 't'</li>
     *   <li>'\n' (换行符) &lt;--&gt; 'n'</li>
     *   <li>'\r' (回车符) &lt;--&gt; 'r'</li>
     *   <li>'\f' (换页符) &lt;--&gt; 'f'</li>
     *   <li>'\b' (退格符) &lt;--&gt; 'b'</li>
     * </ul>
     *
     * @param input    需要处理的字符
     * @param unescape 操作模式：{@code true} 表示进行反转义处理，
     *                 {@code false} 表示进行转义处理
     * @return 处理后的字符。如果输入字符不是支持的转义字符，则原样返回
     */
    public static int parseEscape(int input, boolean unescape) {
        if (unescape) {
            // 反转义：将实际字符转换为转义序列字符
            if (input == '\t') {
                return 't';
            } else if (input == '\n') {
                return 'n';
            } else if (input == '\r') {
                return 'r';
            } else if (input == '\f') {
                return 'f';
            } else if (input == '\b') {
                return 'b';
            } else {
                return input;
            }
        } else {
            // 转义：将转义序列字符转换为实际字符
            if (input == 't') {
                return '\t';
            } else if (input == 'n') {
                return '\n';
            } else if (input == 'r') {
                return '\r';
            } else if (input == 'f') {
                return '\f';
            } else if (input == 'b') {
                return '\b';
            } else {
                return input;
            }
        }
    }

    /**
     * 解析数组表达式，支持多种格式和转义字符
     *
     * <p>该方法能够解析形如 {@code [a,b,c]} 或 {@code ["a a","b b",c c]} 的数组表达式。
     * 支持特殊转义字符 {@code \t\n\r\f}，引号内的内容除特殊字符外无需转义。
     *
     * <p><strong>特性说明：</strong>
     * <ul>
     *   <li>支持单引号(')和双引号(")包围的字符串元素</li>
     *   <li>引号内的内容可包含空格和特殊字符，无需额外转义</li>
     *   <li>支持自定义分隔符、转义符和括号字符</li>
     *   <li>自动忽略元素前后的空白字符</li>
     *   <li>支持嵌套括号结构</li>
     * </ul>
     *
     * <p><strong>示例：</strong>
     * <table border="1">
     * <caption>数组表达式解析示例</caption>
     * <thead>
     *   <tr>
     *     <th>输入表达式</th>
     *     <th>输出结果</th>
     *   </tr>
     * </thead>
     * <tbody>
     *   <tr>
     *     <td>{@code [a,b,c]}</td>
     *     <td>{@code ["a", "b", "c"]}</td>
     *   </tr>
     *   <tr>
     *     <td>{@code [a a,b b,c c]}</td>
     *     <td>{@code ["a a", "b b", "c c"]}</td>
     *   </tr>
     *   <tr>
     *     <td>{@code ["a a","b b",c c]}</td>
     *     <td>{@code ["a a", "b b", "c c"]}</td>
     *   </tr>
     *   <tr>
     *     <td>{@code ["a 'a'","b b",'c c']}</td>
     *     <td>{@code ["a 'a'", "b b", "c c"]}</td>
     *   </tr>
     *   <tr>
     *     <td>{@code ["a \"a\"","b b",'c c']}</td>
     *     <td>{@code ["a \"a\"", "b b", "c c"]}</td>
     *   </tr>
     * </tbody>
     * </table>
     *
     * @param input                 要解析的数组表达式字符串，可以为null
     * @param arrayElementSeparator 数组元素之间的分隔符，默认为逗号(',')
     * @param escapeChar            转义字符，默认为反斜杠('\')
     * @return 解析后的字符串数组，如果输入为null或空则返回空数组
     * @throws IllegalArgumentException 当数组表达式格式错误时抛出，例如：
     *                                  <ul>
     *                                    <li>未闭合的引号</li>
     *                                    <li>未正确匹配的括号</li>
     *                                    <li>引号后缺少分隔符</li>
     *                                  </ul>
     */
    public static String[] parseArray(CharSequence input, char arrayElementSeparator, char escapeChar) {
        if (input == null || input.length() == 0) {
            return UniversalString.EMPTY_STRING_ARRAY;
        }
        IndexBounds bounds = UniversalString.calculateTrimBounds(input, true, true);
        if (bounds.isEmpty()) {
            return UniversalString.EMPTY_STRING_ARRAY;
        }
        int startInclusive = bounds.getStartInclusive(), endInclusive = bounds.getEndInclusive();
        // 处理括号
        int firstChar = Character.codePointAt(input, startInclusive);
        int lastChar = Character.codePointAt(input, endInclusive);
        if ((firstChar == '[' && lastChar == ']') || (firstChar == '{' && lastChar == '}') || (firstChar == '(' && lastChar == ')')) {
            startInclusive += Character.charCount(firstChar);
            endInclusive -= Character.charCount(lastChar);
        } else if (firstChar == '[' || firstChar == '{' || firstChar == '(' || lastChar == ']' || lastChar == '}' || lastChar == ')') {
            throw new IllegalArgumentException("Invalid array expression: " + input);
        }
        // 如果处理完括号后没有内容，返回空数组
        if (startInclusive > endInclusive) {
            return UniversalString.EMPTY_STRING_ARRAY;
        }
        // 使用预估容量初始化，避免频繁扩容
        List<String> list = new ArrayList<>((endInclusive - startInclusive) / 10 + 2);
        StringBuilder sb = new StringBuilder();
        int bracketsCount = 0;
        boolean escape = false, inDoubleQuotes = false, inSingleQuotes = false, hasNonWhitespace = false;
        for (int i = startInclusive, c; i <= endInclusive; i += Character.charCount(c)) {
            c = Character.codePointAt(input, i);
            // 跳过引号外的空白符，但只在元素开始前或结束后跳过
            if (!hasNonWhitespace && Character.isWhitespace(c)) {
                continue;
            }
            // 处理转义字符
            if (c == escapeChar) {
                if (escape) {
                    sb.appendCodePoint(escapeChar);
                    hasNonWhitespace = true;
                }
                escape = !escape;
                continue;
            }
            // 如果是转义状态，直接添加转义后的字符
            if (escape) {
                switch (c) {
                    case 't':
                        sb.append('\t');
                        break;
                    case 'n':
                        sb.append('\n');
                        break;
                    case 'r':
                        sb.append('\r');
                        break;
                    case 'f':
                        sb.append('\f');
                        break;
                    case 'b':
                        sb.append('\b');
                        break;
                    default:
                        if (c == arrayElementSeparator) {
                            sb.append(arrayElementSeparator);
                        } else {
                            sb.append('\\').appendCodePoint(c);
                        }
                        break;
                }
                escape = false;
                hasNonWhitespace = true;
                continue;
            }
            // 如果没有忽略引号则处理引号
            if (c == '"') {
                if (!inSingleQuotes) {
                    inDoubleQuotes = !inDoubleQuotes;
                }
            } else if (c == '\'') {
                if (!inDoubleQuotes) {
                    inSingleQuotes = !inSingleQuotes;
                }
            } else if (c == '{' || c == '[' || c == '(') {
                bracketsCount++;
            } else if (c == '}' || c == ']' || c == ')') {
                bracketsCount--;
            }
            // 检查是否有未匹配的右括号
            if (bracketsCount < 0) {
                throw new IllegalArgumentException("Mismatched brackets in array expression: " + input);
            }
            // 不在引号内，且不在第一层括号外，并且是分割符则获取该值
            if (!inSingleQuotes && !inDoubleQuotes && bracketsCount == 0 && c == arrayElementSeparator) {
                UniversalString.extractString(sb, list, true, true, false, Character::isWhitespace);
                hasNonWhitespace = false; // Reset for next element
            } else {
                sb.appendCodePoint(c);
                // 如果添加的不是空白字符，则标记为有内容
                if (!hasNonWhitespace && !Character.isWhitespace(c)) {
                    hasNonWhitespace = true;
                }
            }
        }
        // 检查括号是否正确闭合
        if (bracketsCount != 0) {
            throw new IllegalArgumentException("Unterminated brackets mark");
        }
        // 添加最后一个元素，并去除首尾空白
        if (sb.length() != 0) {
            UniversalString.extractString(sb, list, true, true, false, Character::isWhitespace);
        }
        return list.toArray(new String[0]);
    }
}
