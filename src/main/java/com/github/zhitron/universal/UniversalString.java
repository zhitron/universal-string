package com.github.zhitron.universal;

import com.github.zhitron.algorithm.KMP;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.IntUnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

/**
 * 通用字符串工具类，提供各种字符串操作的静态方法
 *
 * @author zhitron
 */
public class UniversalString {
    /**
     * 表示未找到的索引值，通常用于字符串查找等操作的返回结果
     */
    public static final int NOT_FOUND = -1;
    /**
     * 空字符串常量，用于避免重复创建空字符串对象
     */
    public static final String EMPTY_STRING = "";
    /**
     * 空字符串数组常量，用于避免重复创建空字符串数组对象
     */
    public static final String[] EMPTY_STRING_ARRAY = new String[0];
    /**
     * 占位符匹配模式，用于匹配形如${...}的占位符，其中不包含{、}或$字符
     */
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{[^{}$]+}");
    /**
     * 空字符数组常量，用于避免重复创建空字符数组对象
     */
    public static char[] EMPTY_CHAR_ARRAY = new char[0];
    /**
     * 空整型数组常量，用于避免重复创建空整型数组对象
     */
    public static int[] EMPTY_INT_ARRAY = new int[0];

    private UniversalString() {
        throw new AssertionError();
    }

    /**
     * 验证索引范围是否有效
     *
     * @param startInclusive 起始索引（包含）
     * @param endExclusive   结束索引（不包含）
     * @param length         字符序列长度
     * @return 如果索引范围有效返回true，否则返回false
     */
    public static boolean isValidateByIndex(int startInclusive, int endExclusive, int length) {
        // 长度必须大于0
        if (length <= 0) {
            return false;
        }
        // 起始索引必须在有效范围内[0, length)
        if (startInclusive < 0 || startInclusive >= length) {
            return false;
        }
        // 结束索引必须大于0且起始索引不能超过长度
        if (endExclusive <= 0 || endExclusive > length) {
            return false;
        }
        // 起始索引必须小于结束索引
        return startInclusive < endExclusive;
    }

    /**
     * 验证输入和目标的索引范围是否都有效
     *
     * @param inputStartInclusive  输入起始索引（包含）
     * @param inputEndExclusive    输入结束索引（不包含）
     * @param inputLength          输入字符序列长度
     * @param targetStartInclusive 目标起始索引（包含）
     * @param targetEndExclusive   目标结束索引（不包含）
     * @param targetLength         目标字符序列长度
     * @return 如果两个索引范围都有效返回true，否则返回false
     */
    public static boolean isValidateByIndex(int inputStartInclusive, int inputEndExclusive, int inputLength,
                                            int targetStartInclusive, int targetEndExclusive, int targetLength) {
        // 验证输入索引范围是否有效
        if (!UniversalString.isValidateByIndex(inputStartInclusive, inputEndExclusive, inputLength)) {
            return false;
        }
        // 验证目标索引范围是否有效
        if (!UniversalString.isValidateByIndex(targetStartInclusive, targetEndExclusive, targetLength)) {
            return false;
        }
        // 输入范围长度必须大于等于目标范围长度
        return inputEndExclusive - inputStartInclusive >= targetEndExclusive - targetStartInclusive;
    }

    /**
     * 验证偏移量和计数是否有效
     *
     * @param offset 偏移量
     * @param count  计数
     * @param length 字符序列长度
     * @return 如果偏移量和计数有效返回true，否则返回false
     */
    public static boolean isValidateByOffset(int offset, int count, int length) {
        // 偏移量必须非负，计数和长度必须大于0
        if (offset < 0 || count <= 0 || length <= 0) {
            return false;
        }
        // 偏移量加上计数不能超过总长度
        return offset + count <= length;
    }

    /**
     * 验证输入和目标的偏移量和计数是否都有效
     *
     * @param inputOffset  输入偏移量
     * @param inputCount   输入计数
     * @param inputLength  输入字符序列长度
     * @param targetOffset 目标偏移量
     * @param targetCount  目标计数
     * @param targetLength 目标字符序列长度
     * @return 如果两个偏移量和计数都有效返回true，否则返回false
     */
    public static boolean isValidateByOffset(int inputOffset, int inputCount, int inputLength,
                                             int targetOffset, int targetCount, int targetLength) {
        // 验证输入偏移量和计数是否有效
        if (!UniversalString.isValidateByOffset(inputOffset, inputCount, inputLength)) {
            return false;
        }
        // 验证目标偏移量和计数是否有效
        if (!UniversalString.isValidateByOffset(targetOffset, targetCount, targetLength)) {
            return false;
        }
        // 输入范围的结束位置必须大于等于目标范围的结束位置
        return inputOffset + inputCount >= targetOffset + targetCount;
    }

    /**
     * 是否为ASCII字符，ASCII字符位于0~127之间
     *
     * <pre>
     *   CharUtil.isAscii('a')  = true
     *   CharUtil.isAscii('A')  = true
     *   CharUtil.isAscii('3')  = true
     *   CharUtil.isAscii('-')  = true
     *   CharUtil.isAscii('\n') = true
     *   CharUtil.isAscii('&copy;') = false
     * </pre>
     *
     * @param input 被检查的字符处
     * @return true表示为ASCII字符，ASCII字符位于0~127之间
     */
    public static boolean isAscii(int input) {
        return input < 128;
    }

    /**
     * 是否为可见ASCII字符，可见字符位于32~126之间
     *
     * <pre>
     *   CharUtil.isAsciiPrintable('a')  = true
     *   CharUtil.isAsciiPrintable('A')  = true
     *   CharUtil.isAsciiPrintable('3')  = true
     *   CharUtil.isAsciiPrintable('-')  = true
     *   CharUtil.isAsciiPrintable('\n') = false
     *   CharUtil.isAsciiPrintable('&copy;') = false
     * </pre>
     *
     * @param input 被检查的字符处
     * @return true表示为ASCII可见字符，可见字符位于32~126之间
     */
    public static boolean isAsciiPrintable(int input) {
        return input >= 32 && input < 127;
    }

    /**
     * 是否为ASCII控制符（不可见字符），控制符位于0~31和127
     *
     * <pre>
     *   CharUtil.isAsciiControl('a')  = false
     *   CharUtil.isAsciiControl('A')  = false
     *   CharUtil.isAsciiControl('3')  = false
     *   CharUtil.isAsciiControl('-')  = false
     *   CharUtil.isAsciiControl('\n') = true
     *   CharUtil.isAsciiControl('&copy;') = false
     * </pre>
     *
     * @param input 被检查的字符
     * @return true表示为控制符，控制符位于0~31和127
     */
    public static boolean isAsciiControl(int input) {
        return input < 32 || input == 127;
    }

    /**
     * 是否为16进制规范的字符，判断是否为如下字符
     *
     * <pre>
     * 1. 0~9
     * 2. a~f
     * 4. A~F
     * </pre>
     *
     * @param input 字符
     * @return 是否为16进制规范的字符
     */
    public static boolean isHex(int input) {
        return Character.isDigit(input) || (input >= 'a' && input <= 'f') || (input >= 'A' && input <= 'F');
    }

    /**
     * 判断是否为emoji表情符<br>
     *
     * @param input 字符
     * @return 是否为emoji
     */
    public static boolean isEmoji(int input) {
        // Emoji字符范围判断
        // 基本Emoji字符范围: 0x1F600-0x1F64F (Emoticons)
        // 其他常用Emoji范围: 0x1F300-0x1F5FF (Miscellaneous Symbols and Pictographs)
        //                   0x1F680-0x1F6FF (Transport and Map Symbols)
        //                   0x1F700-0x1F77F (Alchemical Symbols)
        //                   0x1F780-0x1F7FF (Geometric Shapes Extended)
        //                   0x1F800-0x1F8FF (Supplemental Arrows-C)
        //                   0x1F900-0x1F9FF (Supplemental Symbols and Pictographs)
        //                   0x1FA00-0x1FA6F (Chess Symbols)
        //                   0x1FA70-0x1FAFF (Symbols and Pictographs Extended-A)
        return (input >= 0x1F600 && input <= 0x1F64F) ||  // Emoticons
                (input >= 0x1F300 && input <= 0x1F5FF) ||  // Miscellaneous Symbols and Pictographs
                (input >= 0x1F680 && input <= 0x1F6FF) ||  // Transport and Map Symbols
                (input >= 0x1F700 && input <= 0x1F77F) ||  // Alchemical Symbols
                (input >= 0x1F780 && input <= 0x1F7FF) ||  // Geometric Shapes Extended
                (input >= 0x1F800 && input <= 0x1F8FF) ||  // Supplemental Arrows-C
                (input >= 0x1F900 && input <= 0x1F9FF) ||  // Supplemental Symbols and Pictographs
                (input >= 0x1FA00 && input <= 0x1FA6F) ||  // Chess Symbols
                (input >= 0x1FA70 && input <= 0x1FAFF);    // Symbols and Pictographs Extended-A
    }

    /**
     * 是否为Windows或者Linux（Unix）文件分隔符<br>
     * Windows平台下分隔符为\，Linux（Unix）为/
     *
     * @param input 字符
     * @return 是否为Windows或者Linux（Unix）文件分隔符
     */
    public static boolean isPathSeparator(int input) {
        return input == '/' || input == '\\';
    }

    /**
     * 检查字符是否为空白字符或引号字符
     *
     * @param input 字符
     * @return 如果是空白字符或引号字符则返回true，否则返回false
     */
    public static boolean isWhitespaceOrSingleQuote(int input) {
        return Character.isWhitespace(input) || input == '\'';
    }

    /**
     * 检查字符是否为空白字符或引号字符
     *
     * @param input 字符
     * @return 如果是空白字符或引号字符则返回true，否则返回false
     */
    public static boolean isWhitespaceOrDoubleQuotes(int input) {
        return Character.isWhitespace(input) || input == '"';
    }

    /**
     * 检查输入的字符序列是否为空（null 或长度为0）
     *
     * @param input 要检查的字符序列
     * @return 如果字符序列为空则返回true，否则返回false
     */
    public static boolean isEmpty(CharSequence input) {
        return input == null || input.length() == 0;
    }

    /**
     * 检查输入的字符序列是否不为空（非null且长度大于0）
     *
     * @param input 要检查的字符序列
     * @return 如果字符序列不为空则返回true，否则返回false
     */
    public static boolean isNotEmpty(CharSequence input) {
        return input != null && input.length() > 0;
    }

    /**
     * 检查输入的字符序列是否为空白（null、长度为0或仅包含空白字符）
     *
     * @param input 要检查的字符序列
     * @return 如果字符序列为空白则返回true，否则返回false
     */
    public static boolean isBlank(CharSequence input) {
        if (input == null || input.length() == 0) {
            return true;
        }
        for (int i = 0; i < input.length(); i++) {
            if (!Character.isWhitespace(input.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查输入的字符序列是否不为空白（非null、长度大于0且包含非空白字符）
     *
     * @param input 要检查的字符序列
     * @return 如果字符序列不为空白则返回true，否则返回false
     */
    public static boolean isNotBlank(CharSequence input) {
        if (input == null || input.length() == 0) {
            return false;
        }
        for (int i = 0; i < input.length(); i++) {
            if (!Character.isWhitespace(input.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查所有输入的字符序列是否都为空
     *
     * @param input 要检查的字符序列数组
     * @return 如果所有字符序列都为空则返回true，否则返回false
     */
    public static boolean areAllEmpty(CharSequence... input) {
        if (input == null || input.length == 0) {
            return false;
        }
        for (CharSequence ele : input) {
            if (!isEmpty(ele)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查所有输入的字符序列是否都不为空
     *
     * @param input 要检查的字符序列数组
     * @return 如果所有字符序列都不为空则返回true，否则返回false
     */
    public static boolean areAllNotEmpty(CharSequence... input) {
        if (input == null || input.length == 0) {
            return false;
        }
        for (CharSequence ele : input) {
            if (isEmpty(ele)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查所有输入的字符序列是否都为空白
     *
     * @param input 要检查的字符序列数组
     * @return 如果所有字符序列都为空白则返回true，否则返回false
     */
    public static boolean areAllBlank(CharSequence... input) {
        if (input == null || input.length == 0) {
            return false;
        }
        for (CharSequence ele : input) {
            if (!isBlank(ele)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查所有输入的字符序列是否都不为空白
     *
     * @param input 要检查的字符序列数组
     * @return 如果所有字符序列都不为空白则返回true，否则返回false
     */
    public static boolean areAllNotBlank(CharSequence... input) {
        if (input == null || input.length == 0) {
            return false;
        }
        for (CharSequence ele : input) {
            if (isBlank(ele)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查输入的字符序列是否包含指定的目标字符序列
     *
     * @param ignoreCase 是否忽略大小写
     * @param input      要检查的字符序列
     * @param target     要查找的目标字符序列
     * @return 如果包含目标字符序列则返回true，否则返回false
     */
    public static boolean isContain(boolean ignoreCase, CharSequence input, CharSequence target) {
        // 处理null情况
        if (input == null || target == null || target.length() == 0) {
            return false;
        }
        // 处理目标字符串长度超过输入字符串的情况
        if (target.length() > input.length()) {
            return false;
        }
        return UniversalString.indexOf(ignoreCase, input, 0, input.length(), target) >= 0;
    }

    /**
     * 检查输入的字符序列是否包含所有指定的目标字符序列
     *
     * @param ignoreCase 是否忽略大小写
     * @param input      要检查的字符序列
     * @param target     要查找的目标字符序列数组
     * @return 如果包含所有目标字符序列则返回true，否则返回false
     */
    public static boolean isContainAll(boolean ignoreCase, CharSequence input, CharSequence... target) {
        if (target == null || target.length == 0 || input == null || input.length() == 0) {
            return false;
        }
        // 检查每个目标字符序列是否都包含在input中
        for (CharSequence ele : target) {
            if (ele == null || ele.length() == 0 || UniversalString.indexOf(ignoreCase, input, 0, input.length(), ele) < 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查输入的字符序列是否包含任意一个指定的目标字符序列
     *
     * @param ignoreCase 是否忽略大小写
     * @param input      要检查的字符序列
     * @param target     要查找的目标字符序列数组
     * @return 如果包含任意一个目标字符序列则返回true，否则返回false
     */
    public static boolean isContainAny(boolean ignoreCase, CharSequence input, CharSequence... target) {
        if (target == null || target.length == 0 || input == null || input.length() == 0) {
            return false;
        }
        // 检查每个目标字符序列是否都包含在input中
        for (CharSequence ele : target) {
            if (ele == null || ele.length() == 0 || UniversalString.indexOf(ignoreCase, input, 0, input.length(), ele) >= 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 比较两个字符序列是否相等
     *
     * @param ignoreCase 是否忽略大小写
     * @param input      第一个字符序列
     * @param target     第二个字符序列
     * @return 如果两个字符序列相等则返回true，否则返回false
     */
    public static boolean isEquals(boolean ignoreCase, CharSequence input, CharSequence target) {
        // 如果两个引用指向同一个对象，则直接返回true
        if (input == target) {
            return true;
        }
        // 如果其中一个为null，另一个不为null，则返回false
        if (input == null || target == null) {
            return false;
        }
        // 检查长度是否相等
        if (input.length() != target.length()) {
            return false;
        }
        return UniversalString.regionMatches(ignoreCase, input, 0, target, 0, input.length());
    }

    /**
     * 比较两个字符是否相同
     *
     * @param ignoreCase 是否忽略大小写
     * @param input      字符1
     * @param target     字符2
     * @return 是否相同
     */
    public static boolean isEquals(boolean ignoreCase, int input, int target) {
        if (input == target) {
            return true;
        }
        return ignoreCase && Character.toLowerCase(input) == Character.toLowerCase(target);
    }

    /**
     * 给定字符串是否被字符包围
     *
     * @param ignoreCase 是否忽略大小写
     * @param input      字符串
     * @param prefix     前缀
     * @param suffix     后缀
     * @return 是否包围，空串不包围
     */
    public static boolean isSurround(boolean ignoreCase, CharSequence input, CharSequence prefix, CharSequence suffix) {
        return input != null && UniversalString.isSurround(ignoreCase, input, 0, input.length(), prefix, suffix);
    }

    /**
     * 给定字符串是否被字符包围
     *
     * @param ignoreCase          是否忽略大小写
     * @param input               字符串
     * @param inputStartInclusive 输入字符串的起始位置（包含）
     * @param inputEndExclusive   输入字符串的结束位置（不包含）
     * @param prefix              前缀
     * @param suffix              后缀
     * @return 是否包围，空串不包围
     */
    public static boolean isSurround(boolean ignoreCase, CharSequence input, int inputStartInclusive, int inputEndExclusive, CharSequence prefix, CharSequence suffix) {
        // 参数空值检查
        if (input == null || prefix == null || suffix == null) {
            return false;
        }
        // 检查输入字符串长度是否足够包含前缀和后缀
        if (input.length() < prefix.length() + suffix.length()) {
            return false;
        }
        // 判断字符串是否以指定前缀开始并以指定后缀结束
        return UniversalString.isStartsWith(ignoreCase, input, inputStartInclusive, inputEndExclusive, prefix)
                && UniversalString.isEndsWith(ignoreCase, input, inputStartInclusive, inputEndExclusive, suffix);
    }

    /**
     * 给定字符串是否被字符包围
     *
     * @param ignoreCase 是否忽略大小写
     * @param input      字符串
     * @param prefix     前缀
     * @param suffix     后缀
     * @return 是否包围，空串不包围
     */
    public static boolean isSurround(boolean ignoreCase, char[] input, char[] prefix, char[] suffix) {
        return input != null && UniversalString.isSurround(ignoreCase, input, 0, input.length, prefix, suffix);
    }

    /**
     * 给定字符串是否被字符包围
     *
     * @param ignoreCase          是否忽略大小写
     * @param input               字符串
     * @param inputStartInclusive 输入字符串的起始位置（包含）
     * @param inputEndExclusive   输入字符串的结束位置（不包含）
     * @param prefix              前缀
     * @param suffix              后缀
     * @return 是否包围，空串不包围
     */
    public static boolean isSurround(boolean ignoreCase, char[] input, int inputStartInclusive, int inputEndExclusive, char[] prefix, char[] suffix) {
        // 参数空值检查
        if (input == null || prefix == null || suffix == null) {
            return false;
        }
        // 检查输入字符串长度是否足够包含前缀和后缀
        if (input.length < prefix.length + suffix.length) {
            return false;
        }
        // 判断字符串是否以指定前缀开始并以指定后缀结束
        return UniversalString.isStartsWith(ignoreCase, input, inputStartInclusive, inputEndExclusive, prefix)
                && UniversalString.isEndsWith(ignoreCase, input, inputStartInclusive, inputEndExclusive, suffix);
    }

    /**
     * 给定字符串是否被字符包围
     *
     * @param ignoreCase 是否忽略大小写
     * @param input      字符串
     * @param prefix     前缀
     * @param suffix     后缀
     * @return 是否包围，空串不包围
     */
    public static boolean isSurround(boolean ignoreCase, int[] input, int[] prefix, int[] suffix) {
        return input != null && UniversalString.isSurround(ignoreCase, input, 0, input.length, prefix, suffix);
    }

    /**
     * 给定字符串是否被字符包围
     *
     * @param ignoreCase          是否忽略大小写
     * @param input               字符串
     * @param inputStartInclusive 输入字符串的起始位置（包含）
     * @param inputEndExclusive   输入字符串的结束位置（不包含）
     * @param prefix              前缀
     * @param suffix              后缀
     * @return 是否包围，空串不包围
     */
    public static boolean isSurround(boolean ignoreCase, int[] input, int inputStartInclusive, int inputEndExclusive, int[] prefix, int[] suffix) {
        // 参数空值检查
        if (input == null || prefix == null || suffix == null) {
            return false;
        }
        // 检查输入字符串长度是否足够包含前缀和后缀
        if (input.length < prefix.length + suffix.length) {
            return false;
        }
        // 判断字符串是否以指定前缀开始并以指定后缀结束
        return UniversalString.isStartsWith(ignoreCase, input, inputStartInclusive, inputEndExclusive, prefix)
                && UniversalString.isEndsWith(ignoreCase, input, inputStartInclusive, inputEndExclusive, suffix);
    }

    /**
     * 检查字符序列是否以指定的前缀开始
     *
     * @param ignoreCase 是否忽略大小写
     * @param input      要检查的字符序列
     * @param target     要匹配的前缀
     * @return 如果字符序列以指定前缀开始则返回true，否则返回false
     */
    public static boolean isStartsWith(boolean ignoreCase, CharSequence input, CharSequence target) {
        return input != null && UniversalString.isStartsWith(ignoreCase, input, 0, input.length(), target);
    }

    /**
     * 检查字符序列是否以指定的前缀开始
     *
     * @param ignoreCase          是否忽略大小写
     * @param input               要检查的字符序列
     * @param inputStartInclusive 起始索引（包含）
     * @param inputEndExclusive   结束索引（不包含）
     * @param target              要匹配的前缀
     * @return 如果字符序列以指定前缀开始则返回true，否则返回false
     */
    public static boolean isStartsWith(boolean ignoreCase, CharSequence input, int inputStartInclusive, int inputEndExclusive, CharSequence target) {
        // 空值检查
        if (input == null || target == null) {
            return false;
        }
        if (!UniversalString.isValidateByIndex(inputStartInclusive, inputEndExclusive, input.length(), 0, target.length(), target.length())) {
            return false;
        }
        // 使用regionMatches方法比较，从指定的起始位置开始比较target的全部长度
        return UniversalString.regionMatches(ignoreCase, input, inputStartInclusive, target, 0, target.length());
    }

    /**
     * 检查字符数组是否以指定的前缀开始
     *
     * @param ignoreCase 是否忽略大小写
     * @param input      要检查的字符数组
     * @param target     要匹配的前缀
     * @return 如果字符数组以指定前缀开始则返回true，否则返回false
     */
    public static boolean isStartsWith(boolean ignoreCase, char[] input, char[] target) {
        return input != null && UniversalString.isStartsWith(ignoreCase, input, 0, input.length, target);
    }

    /**
     * 检查字符数组是否以指定的前缀开始
     *
     * @param ignoreCase          是否忽略大小写
     * @param input               要检查的字符数组
     * @param inputStartInclusive 起始索引（包含）
     * @param inputEndExclusive   结束索引（不包含）
     * @param target              要匹配的前缀
     * @return 如果字符数组以指定前缀开始则返回true，否则返回false
     */
    public static boolean isStartsWith(boolean ignoreCase, char[] input, int inputStartInclusive, int inputEndExclusive, char[] target) {
        // 空值检查
        if (input == null || target == null) {
            return false;
        }
        if (!UniversalString.isValidateByIndex(inputStartInclusive, inputEndExclusive, input.length, 0, target.length, target.length)) {
            return false;
        }
        // 使用regionMatches方法比较，从指定的起始位置开始比较target的全部长度
        return UniversalString.regionMatches(ignoreCase, input, inputStartInclusive, target, 0, target.length);
    }

    /**
     * 检查整数数组是否以指定的前缀开始
     *
     * @param ignoreCase 是否忽略大小写
     * @param input      要检查的整数数组
     * @param target     要匹配的前缀
     * @return 如果整数数组以指定前缀开始则返回true，否则返回false
     */
    public static boolean isStartsWith(boolean ignoreCase, int[] input, int[] target) {
        return input != null && UniversalString.isStartsWith(ignoreCase, input, 0, input.length, target);
    }

    /**
     * 检查整数数组是否以指定的前缀开始
     *
     * @param ignoreCase          是否忽略大小写
     * @param input               要检查的整数数组
     * @param inputStartInclusive 起始索引（包含）
     * @param inputEndExclusive   结束索引（不包含）
     * @param target              要匹配的前缀
     * @return 如果整数数组以指定前缀开始则返回true，否则返回false
     */
    public static boolean isStartsWith(boolean ignoreCase, int[] input, int inputStartInclusive, int inputEndExclusive, int[] target) {
        // 空值检查
        if (input == null || target == null) {
            return false;
        }
        if (!UniversalString.isValidateByIndex(inputStartInclusive, inputEndExclusive, input.length, 0, target.length, target.length)) {
            return false;
        }
        // 使用regionMatches方法比较，从指定的起始位置开始比较target的全部长度
        return UniversalString.regionMatches(ignoreCase, input, inputStartInclusive, target, 0, target.length);
    }

    /**
     * 检查字符序列是否以指定的后缀结束
     *
     * @param ignoreCase 是否忽略大小写
     * @param input      要检查的字符序列
     * @param target     要匹配的后缀
     * @return 如果字符序列以指定后缀结束则返回true，否则返回false
     */
    public static boolean isEndsWith(boolean ignoreCase, CharSequence input, CharSequence target) {
        return input != null && UniversalString.isEndsWith(ignoreCase, input, 0, input.length(), target);
    }

    /**
     * 检查字符序列是否以指定的后缀结束
     *
     * @param ignoreCase          是否忽略大小写
     * @param input               要检查的字符序列
     * @param inputStartInclusive 起始索引（包含）
     * @param inputEndExclusive   结束索引（不包含）
     * @param target              要匹配的后缀
     * @return 如果字符序列以指定后缀结束则返回true，否则返回false
     */
    public static boolean isEndsWith(boolean ignoreCase, CharSequence input, int inputStartInclusive, int inputEndExclusive, CharSequence target) {
        // 空值检查
        if (input == null || target == null) {
            return false;
        }
        if (!UniversalString.isValidateByIndex(inputStartInclusive, inputEndExclusive, input.length(), 0, target.length(), target.length())) {
            return false;
        }
        // 使用regionMatches方法比较，从input的(inputLength - targetLength)位置开始与target的0位置比较target的全部长度
        return UniversalString.regionMatches(ignoreCase, input, inputEndExclusive - target.length(), target, 0, target.length());
    }

    /**
     * 检查字符数组是否以指定的后缀结束
     *
     * @param ignoreCase 是否忽略大小写
     * @param input      要检查的字符数组
     * @param target     要匹配的后缀
     * @return 如果字符数组以指定后缀结束则返回true，否则返回false
     */
    public static boolean isEndsWith(boolean ignoreCase, char[] input, char[] target) {
        return input != null && UniversalString.isEndsWith(ignoreCase, input, 0, input.length, target);
    }

    /**
     * 检查字符数组是否以指定的后缀结束
     *
     * @param ignoreCase          是否忽略大小写
     * @param input               要检查的字符数组
     * @param inputStartInclusive 起始索引（包含）
     * @param inputEndExclusive   结束索引（不包含）
     * @param target              要匹配的后缀
     * @return 如果字符数组以指定后缀结束则返回true，否则返回false
     */
    public static boolean isEndsWith(boolean ignoreCase, char[] input, int inputStartInclusive, int inputEndExclusive, char[] target) {
        // 空值检查
        if (input == null || target == null) {
            return false;
        }
        if (!UniversalString.isValidateByIndex(inputStartInclusive, inputEndExclusive, input.length, 0, target.length, target.length)) {
            return false;
        }
        // 使用regionMatches方法比较，从input的(inputLength - targetLength)位置开始与target的0位置比较target的全部长度
        return UniversalString.regionMatches(ignoreCase, input, inputEndExclusive - target.length, target, 0, target.length);
    }

    /**
     * 检查整数数组是否以指定的后缀结束
     *
     * @param ignoreCase 是否忽略大小写
     * @param input      要检查的整数数组
     * @param target     要匹配的后缀
     * @return 如果整数数组以指定后缀结束则返回true，否则返回false
     */
    public static boolean isEndsWith(boolean ignoreCase, int[] input, int[] target) {
        return input != null && UniversalString.isEndsWith(ignoreCase, input, 0, input.length, target);
    }

    /**
     * 检查整数数组是否以指定的后缀结束
     *
     * @param ignoreCase          是否忽略大小写
     * @param input               要检查的整数数组
     * @param inputStartInclusive 起始索引（包含）
     * @param inputEndExclusive   结束索引（不包含）
     * @param target              要匹配的后缀
     * @return 如果整数数组以指定后缀结束则返回true，否则返回false
     */
    public static boolean isEndsWith(boolean ignoreCase, int[] input, int inputStartInclusive, int inputEndExclusive, int[] target) {
        // 空值检查
        if (input == null || target == null) {
            return false;
        }
        if (!UniversalString.isValidateByIndex(inputStartInclusive, inputEndExclusive, input.length, 0, target.length, target.length)) {
            return false;
        }
        // 使用regionMatches方法比较，从input的(inputLength - targetLength)位置开始与target的0位置比较target的全部长度
        return UniversalString.regionMatches(ignoreCase, input, inputEndExclusive - target.length, target, 0, target.length);
    }

    /**
     * 比较两个字符序列的指定区域是否匹配
     *
     * @param ignoreCase   是否忽略大小写
     * @param input        第一个字符序列
     * @param inputOffset  第一个字符序列的起始位置
     * @param target       第二个字符序列
     * @param targetOffset 第二个字符序列的起始位置
     * @param count        要比较的字符长度
     * @return 如果指定区域匹配则返回true，否则返回false
     */
    public static boolean regionMatches(boolean ignoreCase, CharSequence input, int inputOffset, CharSequence target, int targetOffset, int count) {
        // 空值检查
        if (input == null || target == null) {
            return false;
        }
        int inputLength = input.length(), targetLength = target.length();
        if (!UniversalString.isValidateByOffset(inputOffset, count, inputLength, targetOffset, count, targetLength)) {
            return false;
        }
        // 如果忽略大小写且两个CharSequence都支持快速比较（如String），则使用内置方法
        if (input instanceof String && target instanceof String) {
            return ((String) input).regionMatches(ignoreCase, inputOffset, (String) target, targetOffset, count);
        }
        // 逐个比较字符
        for (int i = 0; i < count; i++) {
            int inputIndex = inputOffset + i;
            int targetIndex = targetOffset + i;
            // 获取code point
            int inputCodePoint = Character.codePointAt(input, inputIndex);
            int targetCodePoint = Character.codePointAt(target, targetIndex);
            // 处理代理对
            if (ignoreCase) {
                if (Character.toLowerCase(inputCodePoint) != Character.toLowerCase(targetCodePoint)) {
                    return false;
                }
            } else {
                if (inputCodePoint != targetCodePoint) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 比较两个字符数组的指定区域是否匹配
     *
     * @param ignoreCase   是否忽略大小写
     * @param input        第一个字符数组
     * @param inputOffset  第一个字符数组的起始位置
     * @param target       第二个字符数组
     * @param targetOffset 第二个字符数组的起始位置
     * @param count        要比较的字符长度
     * @return 如果指定区域匹配则返回true，否则返回false
     */
    public static boolean regionMatches(boolean ignoreCase, char[] input, int inputOffset, char[] target, int targetOffset, int count) {
        // 空值检查
        if (input == null || target == null) {
            return false;
        }
        int inputLength = input.length, targetLength = target.length;
        if (!UniversalString.isValidateByOffset(inputOffset, count, inputLength, targetOffset, count, targetLength)) {
            return false;
        }
        // 逐个比较字符
        int inputValue, targetValue;
        for (int i = 0; i < count; i++) {
            // 获取code point
            inputValue = input[inputOffset + i];
            targetValue = target[targetOffset + i];
            // 处理代理对
            if (ignoreCase) {
                if (Character.toLowerCase(inputValue) != Character.toLowerCase(targetValue)) {
                    return false;
                }
            } else {
                if (inputValue != targetValue) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 比较两个整数数组的指定区域是否匹配
     *
     * @param ignoreCase   是否忽略大小写
     * @param input        第一个整数数组
     * @param inputOffset  第一个整数数组的起始位置
     * @param target       第二个整数数组
     * @param targetOffset 第二个整数数组的起始位置
     * @param count        要比较的字符长度
     * @return 如果指定区域匹配则返回true，否则返回false
     */
    public static boolean regionMatches(boolean ignoreCase, int[] input, int inputOffset, int[] target, int targetOffset, int count) {
        // 空值检查
        if (input == null || target == null) {
            return false;
        }
        int inputLength = input.length, targetLength = target.length;
        if (!UniversalString.isValidateByOffset(inputOffset, count, inputLength, targetOffset, count, targetLength)) {
            return false;
        }
        // 逐个比较字符
        int inputValue, targetValue;
        for (int i = 0; i < count; i++) {
            // 获取code point
            inputValue = input[inputOffset + i];
            targetValue = target[targetOffset + i];
            // 处理代理对
            if (ignoreCase) {
                if (Character.toLowerCase(inputValue) != Character.toLowerCase(targetValue)) {
                    return false;
                }
            } else {
                if (inputValue != targetValue) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 计算需要修剪的边界索引，使用默认的空白字符作为修剪条件
     *
     * @param input          要处理的字符序列
     * @param isTrimLeading  是否修剪开头
     * @param isTrimTrailing 是否修剪结尾
     * @return 包含起始和结束位置的数组，格式为[起始位置startInclusive, 结束位置endExclusive]
     */
    public static int[] calculateTrimBounds(CharSequence input, boolean isTrimLeading, boolean isTrimTrailing) {
        // 调用重载方法，使用Character::isWhitespace作为默认修剪条件
        return UniversalString.calculateTrimBounds(input, isTrimLeading, isTrimTrailing, Character::isWhitespace);
    }

    /**
     * 计算需要修剪的边界索引，使用指定的修剪条件
     *
     * @param input          要处理的字符序列
     * @param isTrimLeading  是否修剪开头
     * @param isTrimTrailing 是否修剪结尾
     * @param trimCondition  修剪条件谓词，用于判断字符是否应该被修剪
     * @return 包含起始和结束位置的数组，格式为[起始位置startInclusive, 结束位置endExclusive]
     */
    public static int[] calculateTrimBounds(CharSequence input, boolean isTrimLeading, boolean isTrimTrailing, IntPredicate trimCondition) {
        // 初始化边界数组，起始位置为0，结束位置为输入序列的长度
        int[] bound = new int[]{0, input.length()};
        // 调用重载方法，传入边界数组和一个lambda表达式
        // lambda表达式将索引转换为对应的code point，并根据trimCondition判断是否需要修剪
        UniversalString.calculateTrimBounds(bound, isTrimLeading, isTrimTrailing, i -> {
            // 获取指定索引处的code point
            int value = Character.codePointAt(input, i);
            // 如果该code point满足修剪条件，则返回其占用的字符数（1或2）
            if (trimCondition.test(value)) {
                return Character.charCount(value);
            }
            // 否则返回0，表示不需要修剪
            return 0;
        });
        // 返回计算后的边界数组
        return bound;
    }

    /**
     * 计算需要修剪的边界索引，使用指定的跳过器
     *
     * @param bound          包含起始和结束位置的数组，格式为[起始位置startInclusive, 结束位置endExclusive]
     * @param isTrimLeading  是否修剪开头
     * @param isTrimTrailing 是否修剪结尾
     * @param trimSkipper    判断字符跳过器，如果符合要求则返回跳过字符数>0，否则返回0
     */
    public static void calculateTrimBounds(int[] bound, boolean isTrimLeading, boolean isTrimTrailing, IntUnaryOperator trimSkipper) {
        // 使用位运算标记需要处理的方向：0b01表示处理开头，0b10表示处理结尾
        for (int tag = (isTrimTrailing ? 0b10 : 0) | (isTrimLeading ? 0b01 : 0), i; tag != 0 && bound[0] < bound[1]; ) {
            // 如果需要处理开头且满足移除条件，则移动起始位置
            if ((tag & 0b01) == 0b01) {
                // 使用trimSkipper判断起始位置的字符是否需要跳过
                i = trimSkipper.applyAsInt(bound[0]);
                if (i > 0) {
                    // 如果需要跳过，则更新起始位置
                    bound[0] += i;
                } else {
                    // 否则清除处理开头的标记（将最低位设为0）
                    tag = tag & 0b10;
                }
            }
            // 如果需要处理结尾（tag的第二位为1）
            if ((tag & 0b10) == 0b10) {
                // 使用trimSkipper判断结束位置前一个字符是否需要跳过
                i = trimSkipper.applyAsInt(bound[1] - 1);
                if (i > 0) {
                    // 如果需要跳过，则更新结束位置
                    bound[1] -= i;
                } else {
                    // 否则清除处理结尾的标记（将第二位设为0）
                    tag = tag & 0b01;
                }
            }
        }
    }

    /**
     * 计算目标字符串在输入字符串中出现的次数
     *
     * @param ignoreCase 是否忽略大小写
     * @param input      输入字符串
     * @param target     目标字符串
     * @return 目标字符串在输入字符串中出现的次数
     */
    public static int countOccurrences(boolean ignoreCase, CharSequence input, CharSequence target) {
        if (input == null || target == null) {
            return 0;
        }
        int inputLength = input.length(), targetLength = target.length();
        if (inputLength == 0 || targetLength == 0 || targetLength > inputLength) {
            return 0;
        }
        char[] inputChars = UniversalString.toChars(input);
        char[] targetChars = UniversalString.toChars(target);
        KMP<Character> kmp = KMP.of(inputChars, targetChars).setCompare((ie, te) -> UniversalString.isEquals(ignoreCase, ie, te));
        int count = 0;
        int idx = 0;
        while ((idx = kmp.indexOf(idx)) != -1) {
            ++count;
            idx += targetChars.length;
        }
        return count;
    }

    /**
     * 在输入字符串中查找目标字符串第n次出现的位置
     *
     * @param ignoreCase          是否忽略大小写
     * @param input               输入字符串
     * @param inputStartInclusive 开始搜索位置（包含）
     * @param inputEndExclusive   结束搜索位置（不包含）
     * @param target              目标字符串
     * @param occurrence          要查找的出现次数(从1开始计数)
     * @return 目标字符串第n次出现的起始位置，如果未找到则返回-1
     */
    public static int indexOf(boolean ignoreCase, CharSequence input, int inputStartInclusive, int inputEndExclusive, CharSequence target, int occurrence) {
        if (input == null || target == null) {
            return NOT_FOUND;
        }
        int inputLength = input.length(), targetLength = target.length();
        if (!UniversalString.isValidateByIndex(inputStartInclusive, inputEndExclusive, inputLength, 0, targetLength, targetLength)) {
            return NOT_FOUND;
        }
        char[] inputChars = UniversalString.toChars(input);
        char[] targetChars = UniversalString.toChars(target);
        KMP<Character> kmp = KMP.of(inputChars, targetChars).setCompare((ie, te) -> UniversalString.isEquals(ignoreCase, ie, te));
        int start = inputStartInclusive, index = 0;
        // 循环查找目标字符串，直到找到第occurrence次出现或找不到为止
        for (int found = 0; found < occurrence && index >= 0; found++, start += targetLength) {
            index = kmp.indexOf(start, inputEndExclusive);
        }
        return index;
    }

    /**
     * 查找字符序列中目标字符串的首次出现位置
     *
     * @param ignoreCase          是否忽略大小写
     * @param input               要查找的字符序列
     * @param inputStartInclusive 起始查找位置（包含）
     * @param inputEndExclusive   结束查找位置（不包含）
     * @param target              要查找的目标字符串
     * @return 目标字符串的首次出现位置索引，未找到返回-1
     */
    public static int indexOf(boolean ignoreCase, CharSequence input, int inputStartInclusive, int inputEndExclusive, CharSequence target) {
        if (input == null || target == null) {
            return NOT_FOUND;
        }
        int inputLength = input.length(), targetLength = target.length();
        if (!UniversalString.isValidateByIndex(inputStartInclusive, inputEndExclusive, inputLength, 0, targetLength, targetLength)) {
            return NOT_FOUND;
        }
        char[] inputChars = UniversalString.toChars(input);
        char[] targetChars = UniversalString.toChars(target);
        return KMP.of(inputChars, targetChars).setCompare((ie, te) -> UniversalString.isEquals(ignoreCase, ie, te)).indexOf(inputStartInclusive, inputEndExclusive);
    }

    /**
     * 查找字符数组中目标字符数组的首次出现位置
     *
     * @param ignoreCase          是否忽略大小写
     * @param input               要查找的字符数组
     * @param inputStartInclusive 起始查找位置（包含）
     * @param inputEndExclusive   结束查找位置（不包含）
     * @param target              要查找的目标字符数组
     * @return 目标字符数组的首次出现位置索引，未找到返回-1
     */
    public static int indexOf(boolean ignoreCase, char[] input, int inputStartInclusive, int inputEndExclusive, char[] target) {
        if (input == null || target == null) {
            return NOT_FOUND;
        }
        int inputLength = input.length, targetLength = target.length;
        if (!UniversalString.isValidateByIndex(inputStartInclusive, inputEndExclusive, inputLength, 0, targetLength, targetLength)) {
            return NOT_FOUND;
        }
        return KMP.of(input, target).setCompare((ie, te) -> UniversalString.isEquals(ignoreCase, ie, te)).indexOf(inputStartInclusive, inputEndExclusive);
    }

    /**
     * 查找整数数组中目标整数数组的首次出现位置
     *
     * @param ignoreCase          是否忽略大小写
     * @param input               要查找的整数数组
     * @param inputStartInclusive 起始查找位置（包含）
     * @param inputEndExclusive   结束查找位置（不包含）
     * @param target              要查找的目标整数数组
     * @return 目标整数数组的首次出现位置索引，未找到返回-1
     */
    public static int indexOf(boolean ignoreCase, int[] input, int inputStartInclusive, int inputEndExclusive, int[] target) {
        if (input == null || target == null) {
            return NOT_FOUND;
        }
        int inputLength = input.length, targetLength = target.length;
        if (!UniversalString.isValidateByIndex(inputStartInclusive, inputEndExclusive, inputLength, 0, targetLength, targetLength)) {
            return NOT_FOUND;
        }
        return KMP.of(input, target).setCompare((ie, te) -> UniversalString.isEquals(ignoreCase, ie, te)).indexOf(inputStartInclusive, inputEndExclusive);
    }

    /**
     * 在输入字符串中查找目标字符串最后一次出现的位置
     *
     * @param ignoreCase          是否忽略大小写
     * @param input               输入字符串
     * @param inputStartInclusive 开始搜索位置（包含）
     * @param inputEndExclusive   结束搜索位置（不包含）
     * @param target              目标字符串
     * @param occurrence          查找第几次出现的位置
     * @return 目标字符串在输入字符串中第occurrence次出现的最后位置，如果未找到则返回-1
     */
    public static int lastIndexOf(boolean ignoreCase, CharSequence input, int inputStartInclusive, int inputEndExclusive, CharSequence target, int occurrence) {
        if (input == null || target == null || occurrence <= 0) {
            return NOT_FOUND;
        }
        int inputLength = input.length(), targetLength = target.length();
        if (!UniversalString.isValidateByIndex(inputStartInclusive, inputEndExclusive, inputLength, 0, targetLength, targetLength)) {
            return NOT_FOUND;
        }
        char[] inputChars = UniversalString.toChars(input);
        char[] targetChars = UniversalString.toChars(target);
        KMP<Character> kmp = KMP.of(inputChars, targetChars).setCompare((ie, te) -> UniversalString.isEquals(ignoreCase, ie, te));
        int index = inputEndExclusive;
        // 循环查找目标字符串，直到找到第occurrence次出现的位置
        for (int found = 0; found < occurrence && index >= 0; found++) {
            index = kmp.lastIndexOf(inputStartInclusive, index);
        }
        return index;
    }

    /**
     * 查找字符序列中目标字符串的最后出现位置
     *
     * @param ignoreCase          是否忽略大小写
     * @param input               要查找的字符序列
     * @param inputStartInclusive 开始搜索位置（包含）
     * @param inputEndExclusive   结束搜索位置（不包含）
     * @param target              要查找的目标字符串
     * @return 目标字符串的最后出现位置索引，未找到返回-1
     */
    public static int lastIndexOf(boolean ignoreCase, CharSequence input, int inputStartInclusive, int inputEndExclusive, CharSequence target) {
        if (input == null || target == null) {
            return NOT_FOUND;
        }
        int inputLength = input.length(), targetLength = target.length();
        if (!UniversalString.isValidateByIndex(inputStartInclusive, inputEndExclusive, inputLength, 0, targetLength, targetLength)) {
            return NOT_FOUND;
        }
        char[] inputChars = UniversalString.toChars(input);
        char[] targetChars = UniversalString.toChars(target);
        return KMP.of(inputChars, targetChars).setCompare((ie, te) -> UniversalString.isEquals(ignoreCase, ie, te)).lastIndexOf(inputStartInclusive, inputEndExclusive);
    }

    /**
     * 查找字符数组中目标字符数组的最后出现位置
     *
     * @param ignoreCase          是否忽略大小写
     * @param input               要查找的字符数组
     * @param inputStartInclusive 开始搜索位置（包含）
     * @param inputEndExclusive   结束搜索位置（不包含）
     * @param target              要查找的目标字符数组
     * @return 目标字符数组的最后出现位置索引，未找到返回-1
     */
    public static int lastIndexOf(boolean ignoreCase, char[] input, int inputStartInclusive, int inputEndExclusive, char[] target) {
        if (input == null || target == null) {
            return NOT_FOUND;
        }
        int inputLength = input.length, targetLength = target.length;
        if (!UniversalString.isValidateByIndex(inputStartInclusive, inputEndExclusive, inputLength, 0, targetLength, targetLength)) {
            return NOT_FOUND;
        }
        return KMP.of(input, target).setCompare((ie, te) -> UniversalString.isEquals(ignoreCase, ie, te)).lastIndexOf(inputStartInclusive, inputEndExclusive - targetLength);
    }

    /**
     * 查找整数数组中目标整数数组的最后出现位置
     *
     * @param ignoreCase          是否忽略大小写
     * @param input               要查找的整数数组
     * @param inputStartInclusive 开始搜索位置（包含）
     * @param inputEndExclusive   结束搜索位置（不包含）
     * @param target              要查找的目标整数数组
     * @return 目标整数数组的最后出现位置索引，未找到返回-1
     */
    public static int lastIndexOf(boolean ignoreCase, int[] input, int inputStartInclusive, int inputEndExclusive, int[] target) {
        if (input == null || target == null) {
            return NOT_FOUND;
        }
        int inputLength = input.length, targetLength = target.length;
        if (!UniversalString.isValidateByIndex(inputStartInclusive, inputEndExclusive, inputLength, 0, targetLength, targetLength)) {
            return NOT_FOUND;
        }
        return KMP.of(input, target).setCompare((ie, te) -> UniversalString.isEquals(ignoreCase, ie, te)).lastIndexOf(inputStartInclusive, inputEndExclusive - targetLength);
    }

    /**
     * 将给定字符串，变成 "xxx...xxx" 形式的字符串
     *
     * @param input          原始字符串
     * @param briefChar      省略符字符
     * @param briefLength    省略符的长度
     * @param leadingLength  前缀保留长度
     * @param trailingLength 后缀保留长度
     * @return 截取后的字符串
     */
    public static String brief(CharSequence input, char briefChar, int briefLength, int leadingLength, int trailingLength) {
        if (input == null) {
            return EMPTY_STRING;
        }
        // 参数合法性校验
        if (briefLength < 0 || leadingLength < 0 || trailingLength < 0) {
            throw new IllegalArgumentException("Length parameters must be non-negative.");
        }
        int inputLength = input.length();
        // 如果前后缀长度之和已经大于等于原字符串长度，则直接返回原字符串
        if (leadingLength + trailingLength >= inputLength) {
            return input.toString();
        }
        // 构造结果字符串
        StringBuilder sb = new StringBuilder(leadingLength + briefLength + trailingLength);
        if (leadingLength > 0) {
            sb.append(input, 0, leadingLength);
        }
        for (int i = 0; i < briefLength; i++) {
            sb.append(briefChar);
        }
        if (trailingLength > 0) {
            sb.append(input, inputLength - trailingLength, inputLength);
        }
        return sb.toString();
    }

    /**
     * 将字符序列的首字母大写
     *
     * @param input 要处理的字符序列
     * @return 首字母大写的字符串
     */
    public static String capitalize(CharSequence input, boolean capitalize) {
        if (input == null || input.length() == 0) {
            return EMPTY_STRING;
        }
        StringBuilder sb = new StringBuilder(input);
        char firstChar = sb.charAt(0);
        if (Character.isLetter(firstChar)) {
            if (capitalize) {
                sb.setCharAt(0, Character.toUpperCase(firstChar));
            } else {
                sb.setCharAt(0, Character.toLowerCase(firstChar));
            }
        }
        return sb.toString();
    }

    /**
     * 反转字符序列
     *
     * @param input 要反转的字符序列
     * @return 反转后的字符串
     */
    public static String reverse(CharSequence input) {
        if (input == null || input.length() == 0) {
            return EMPTY_STRING;
        }
        int length = input.length();
        if (length == 0) {
            return EMPTY_STRING;
        }
        // 使用StringBuilder构建结果
        StringBuilder sb = new StringBuilder(length);
        // 从后向前遍历，正确处理Unicode代理对
        for (int i = length - 1; i >= 0; ) {
            // 获取当前位置的code point
            int codePoint = Character.codePointAt(input, i);
            // 计算code point对应的字符数量（1或2个char）
            int charCount = Character.charCount(codePoint);
            // 将code point添加到结果中
            sb.appendCodePoint(codePoint);
            // 移动索引
            i -= charCount;
        }
        return sb.toString();
    }

    /**
     * 截取字符序列的子串
     *
     * @param input 要截取的字符序列
     * @param start 起始位置（包含）
     * @param end   结束位置（不包含）
     * @return 截取后的子串
     */
    public static String substring(CharSequence input, int start, int end) {
        if (input == null) {
            return EMPTY_STRING;
        }
        int length = input.length();
        if (length == 0) {
            return EMPTY_STRING;
        }
        if (start < 0) {
            start = 0;
        }
        if (end > length) {
            end = length;
        }
        if (start >= end) {
            return EMPTY_STRING;
        }
        return input.subSequence(start, end).toString();
    }

    /**
     * 清理字符序列中的指定内容
     *
     * @param ignoreCase 是否忽略大小写
     * @param input      要清理的字符序列
     * @param targets    要移除的目标字符串数组
     * @return 清理后的字符串
     */
    public static String clean(boolean ignoreCase, CharSequence input, CharSequence... targets) {
        // 空值检查
        if (input == null || input.length() == 0) {
            return EMPTY_STRING;
        }
        if (targets == null || targets.length == 0) {
            return input.toString();
        }
        // 将输入字符串转换为char数组，只需一次转换
        char[] inputChars = UniversalString.toChars(input);
        // 过滤掉空的目标字符串，并转换为char数组
        List<char[]> targetCharsList = Arrays.stream(targets)
                .filter(Objects::nonNull)
                .filter(e -> e.length() > 0)
                .sorted((a, b) -> Integer.compare(b.length(), a.length()))
                .map(UniversalString::toChars)
                .collect(Collectors.toCollection(LinkedList::new));
        if (targetCharsList.isEmpty()) {
            return input.toString();
        }
        // 创建KMP算法实例列表
        List<KMP<Character>> kmpList = new LinkedList<>();
        for (char[] targetChars : targetCharsList) {
            kmpList.add(KMP.of(inputChars, targetChars).setCompare((ie, te) -> isEquals(ignoreCase, ie, te)));
        }
        // 使用StringBuilder构建结果
        StringBuilder result = new StringBuilder(input.length());
        int inputLength = inputChars.length;
        int position = 0;
        while (position < inputLength) {
            int matchStart = -1;
            int matchLength = 0;
            // 检查当前位置是否匹配任何目标字符串
            for (KMP<Character> kmp : kmpList) {
                int foundIndex = kmp.indexOf(position);
                if (foundIndex == position) {
                    matchStart = foundIndex;
                    matchLength = kmp.getTargetLength();
                    break;
                }
            }
            if (matchStart != -1) {
                // 找到匹配项，跳过该匹配项
                position += matchLength;
            } else {
                // 没有找到匹配项，将当前字符添加到结果中
                result.append(inputChars[position]);
                position++;
            }
        }
        return result.toString();
    }

    /**
     * 根据条件清理字符序列
     *
     * @param input           要清理的字符序列
     * @param removeCondition 移除条件谓词
     * @return 清理后的字符串
     */
    public static String clean(CharSequence input, IntPredicate removeCondition) {
        // 空值检查
        if (input == null || input.length() == 0) {
            return EMPTY_STRING;
        }
        if (removeCondition == null) {
            return input.toString();
        }
        // 使用StringBuilder构建结果
        StringBuilder sb = new StringBuilder();
        int inputLength = input.length();
        for (int i = 0; i < inputLength; ) {
            // 获取当前位置的code point
            int codePoint = Character.codePointAt(input, i);
            // 检查是否满足移除条件
            if (!removeCondition.test(codePoint)) {
                // 如果不满足移除条件，则添加到结果中
                sb.appendCodePoint(codePoint);
            }
            // 移动到下一个字符位置
            i += Character.charCount(codePoint);
        }
        return sb.toString();
    }

    /**
     * 替换字符序列中的指定内容
     *
     * @param ignoreCase   是否忽略大小写
     * @param input        要处理的字符序列
     * @param target       要被替换的目标字符串
     * @param replacement  替换后的字符串
     * @param replaceCount 替换次数（-1表示全部替换）
     * @return 替换后的字符串
     */
    public static String replace(boolean ignoreCase, CharSequence input, CharSequence target, CharSequence replacement, int replaceCount) {
        if (input == null || input.length() == 0) {
            return EMPTY_STRING;
        }
        if (target == null || target.length() == 0 || replaceCount == 0) {
            return input.toString();
        }
        char[] inputChars = UniversalString.toChars(input);
        char[] targetChars = UniversalString.toChars(target);
        StringBuilder sb = new StringBuilder();
        KMP<Character> kmp = KMP.of(inputChars, targetChars).setCompare((ie, te) -> UniversalString.isEquals(ignoreCase, ie, te));
        int startIndex = 0;
        int foundIndex;
        int replacementCount = 0;
        while ((foundIndex = kmp.indexOf(startIndex)) != -1) {
            // Append the part before the found target
            for (int i = startIndex; i < foundIndex; i++) {
                sb.append(inputChars[i]);
            }
            // Append the replacement
            if (replacement != null) {
                sb.append(replacement);
            }
            // Move the start index past the found target
            startIndex = foundIndex + targetChars.length;
            // Increment the replacement count and check if we've reached the limit
            replacementCount++;
            if (replaceCount > 0 && replacementCount >= replaceCount) {
                break;
            }
        }
        // Append the remaining part of the input
        for (int i = startIndex; i < inputChars.length; i++) {
            sb.append(inputChars[i]);
        }
        return sb.toString();
    }

    /**
     * 替换占位符
     *
     * @param input  模板
     * @param params 参数
     * @return 返回处理后的值
     */
    public static String replacePlaceholder(String input, Map<?, ?> params, boolean ignoreNull) {
        return UniversalString.replacePlaceholder(input, text -> {
            if (params != null) {
                Object value = params.get(text);
                if (value != null) {
                    return value.toString();
                }
            }
            return null;
        }, ignoreNull);
    }

    /**
     * 替换占位符
     *
     * @param input    模板
     * @param function 参数
     * @return 返回处理后的值
     */
    public static String replacePlaceholder(String input, Function<String, String> function, boolean ignoreNull) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        if (function == null) {
            return input;
        }
        StringBuffer sb = new StringBuffer(input.length());
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(input);
        while (matcher.find()) {
            String ph = matcher.group();
            // 安全提取占位符内容（去除前后大括号）
            String placeholderContent = ph.length() > 3 ? ph.substring(2, ph.length() - 1) : "";
            // 使用paramSupplier获取实际替换值
            String replacement = function.apply(placeholderContent);
            // 对替换值进行转义处理
            if (replacement != null) {
                matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
            } else {
                if (ignoreNull) {
                    matcher.appendReplacement(sb, "");
                } else {
                    matcher.appendReplacement(sb, "null");
                }
            }
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 使用分隔符连接字符序列数组
     *
     * @param delimiter     分隔符
     * @param elementPrefix 每个元素的前缀
     * @param elementSuffix 每个元素的后缀
     * @param prefix        整体前缀
     * @param suffix        整体后缀
     * @param input         要连接的字符序列数组
     * @return 连接后的字符串
     */
    public static String join(CharSequence delimiter, CharSequence elementPrefix, CharSequence elementSuffix, CharSequence prefix, CharSequence suffix, CharSequence... input) {
        return UniversalString.join(delimiter, elementPrefix, elementSuffix, prefix, suffix, input == null ? Collections.emptyList() : Arrays.asList(input));
    }

    /**
     * 使用分隔符连接字符序列集合
     *
     * @param delimiter     分隔符
     * @param elementPrefix 每个元素的前缀
     * @param elementSuffix 每个元素的后缀
     * @param prefix        整体前缀
     * @param suffix        整体后缀
     * @param input         要连接的字符序列集合
     * @return 连接后的字符串
     */
    public static String join(CharSequence delimiter, CharSequence elementPrefix, CharSequence elementSuffix, CharSequence prefix, CharSequence suffix, Iterable<CharSequence> input) {
        if (input == null) {
            return EMPTY_STRING;
        }
        StringBuilder sb = new StringBuilder();
        // 添加整体前缀
        if (prefix != null) {
            sb.append(prefix);
        }
        boolean first = true;
        for (CharSequence element : input) {
            if (!first && delimiter != null) {
                sb.append(delimiter);
            }
            // 添加元素前缀
            if (elementPrefix != null) {
                sb.append(elementPrefix);
            }
            // 添加元素本身
            if (element != null) {
                sb.append(element);
            }
            // 添加元素后缀
            if (elementSuffix != null) {
                sb.append(elementSuffix);
            }
            first = false;
        }
        // 添加整体后缀
        if (suffix != null) {
            sb.append(suffix);
        }
        return sb.toString();
    }

    /**
     * 根据分隔符分割字符序列
     *
     * @param retainEmpty   是否保留空元素
     * @param delimiter     分隔符
     * @param elementPrefix 每个元素的前缀
     * @param elementSuffix 每个元素的后缀
     * @param prefix        整体前缀
     * @param suffix        整体后缀
     * @param input         要分割的字符序列
     * @return 分割后的字符串列表
     */
    public static List<String> split(boolean retainEmpty, CharSequence delimiter, CharSequence elementPrefix, CharSequence elementSuffix,
                                     CharSequence prefix, CharSequence suffix, IntPredicate trimCondition, CharSequence input) {
        // 空值检查
        if (input == null || input.length() == 0) {
            return retainEmpty ? Collections.singletonList("") : Collections.emptyList();
        }
        int[] inputCodePoints = input.codePoints().toArray();
        int[] prefixCodePoints = UniversalString.toCodePoints(prefix);
        int[] suffixCodePoints = UniversalString.toCodePoints(suffix);
        int[] indexes = {0, inputCodePoints.length};
        IntUnaryOperator trim = i -> trimCondition.test(inputCodePoints[i]) ? 1 : 0;
        if (trimCondition != null) {
            UniversalString.calculateTrimBounds(indexes, true, true, trim);
        }
        while (UniversalString.isStartsWith(false, inputCodePoints, indexes[0], indexes[1], prefixCodePoints)) {
            indexes[0] += prefixCodePoints.length;
        }
        while (UniversalString.isEndsWith(false, inputCodePoints, indexes[0], indexes[1], suffixCodePoints)) {
            indexes[1] -= suffixCodePoints.length;
        }
        if (indexes[0] >= indexes[1]) {
            return retainEmpty ? Collections.singletonList(EMPTY_STRING) : Collections.emptyList();
        }
        List<String> result = new ArrayList<>();
        int[] elementPrefixCodePoints = UniversalString.toCodePoints(elementPrefix);
        int[] elementSuffixCodePoints = UniversalString.toCodePoints(elementSuffix);
        if (delimiter == null || delimiter.length() == 0) {
            while (UniversalString.isStartsWith(false, inputCodePoints, indexes[0], indexes[1], elementPrefixCodePoints)) {
                indexes[0] += elementPrefixCodePoints.length;
            }
            while (UniversalString.isEndsWith(false, inputCodePoints, indexes[0], indexes[1], elementSuffixCodePoints)) {
                indexes[1] -= elementSuffixCodePoints.length;
            }
            if (indexes[0] >= indexes[1]) {
                return retainEmpty ? Collections.singletonList(EMPTY_STRING) : Collections.emptyList();
            }
            result.add(new String(inputCodePoints, indexes[0], indexes[1] - indexes[0]));
        } else {
            int[] delimiterCodePoints = delimiter.codePoints().toArray();
            int index;
            int[] elementIndexes = new int[2];
            while ((index = UniversalString.indexOf(false, inputCodePoints, indexes[0], indexes[1], delimiterCodePoints)) >= 0 && index < indexes[1]) {
                elementIndexes[0] = indexes[0];
                elementIndexes[1] = index;
                if (trimCondition != null) {
                    UniversalString.calculateTrimBounds(elementIndexes, true, true, trim);
                }
                // 处理元素前缀
                while (UniversalString.isStartsWith(false, inputCodePoints, elementIndexes[0], elementIndexes[1], elementPrefixCodePoints)) {
                    elementIndexes[0] += elementPrefixCodePoints.length;
                }
                // 处理元素后缀
                while (UniversalString.isEndsWith(false, inputCodePoints, elementIndexes[0], elementIndexes[1], elementSuffixCodePoints)) {
                    elementIndexes[1] -= elementSuffixCodePoints.length;
                }
                if (trimCondition != null) {
                    UniversalString.calculateTrimBounds(elementIndexes, true, true, trim);
                }
                // 添加元素到结果列表
                if (elementIndexes[0] < elementIndexes[1] || retainEmpty) {
                    result.add(new String(inputCodePoints, elementIndexes[0], elementIndexes[1] - elementIndexes[0]));
                }
                // 更新下一个搜索起始位置
                indexes[0] = index + delimiterCodePoints.length;
            }
            // 处理最后一个元素
            if (indexes[0] < indexes[1]) {
                elementIndexes[0] = indexes[0];
                elementIndexes[1] = indexes[1];
                if (trimCondition != null) {
                    UniversalString.calculateTrimBounds(elementIndexes, true, true, trim);
                }
                // 处理元素前缀
                if (UniversalString.isStartsWith(false, inputCodePoints, elementIndexes[0], elementIndexes[1], elementPrefixCodePoints)) {
                    elementIndexes[0] += elementPrefixCodePoints.length;
                }
                // 处理元素后缀
                if (UniversalString.isEndsWith(false, inputCodePoints, elementIndexes[0], elementIndexes[1], elementSuffixCodePoints)) {
                    elementIndexes[1] -= elementSuffixCodePoints.length;
                }
                if (trimCondition != null) {
                    UniversalString.calculateTrimBounds(elementIndexes, true, true, trim);
                }
                // 添加最后一个元素到结果列表
                if (elementIndexes[0] < elementIndexes[1] || retainEmpty) {
                    result.add(new String(inputCodePoints, elementIndexes[0], elementIndexes[1] - elementIndexes[0]));
                }
            } else if (indexes[0] == indexes[1] && retainEmpty) {
                // 如果startIndex等于end且需要保留空元素，则添加空字符串
                result.add(EMPTY_STRING);
            }
        }
        return result;
    }

    /**
     * 文字脱敏
     *
     * @param input             字符串数据
     * @param mark              掩码字符
     * @param markLeadingLength 前缀部分掩码长度
     * @param markCenterLength  中间部分掩码长度
     * @param markTailingLength 后缀部分掩码长度
     * @return 返回数据脱敏后的文字
     */
    public static String desensitize(CharSequence input, CharSequence mark, int markLeadingLength, int markCenterLength, int markTailingLength) {
        if (input == null) {
            return EMPTY_STRING;
        }
        int inputLength = input.length();
        if (inputLength == 0) {
            return EMPTY_STRING;
        }
        if (mark == null || mark.length() == 0) {
            return EMPTY_STRING;
        }
        // 确保掩码长度参数非负
        if (markLeadingLength < 0) {
            markLeadingLength = 0;
        }
        if (markCenterLength < 0) {
            markCenterLength = 0;
        }
        if (markTailingLength < 0) {
            markTailingLength = 0;
        }
        // 判断是否需要对整个字符串进行脱敏（即所有部分都被掩码覆盖）
        boolean isAllMark = markLeadingLength > inputLength || markCenterLength > inputLength || markTailingLength > inputLength
                || markLeadingLength + markTailingLength > inputLength
                || markCenterLength + markLeadingLength > inputLength
                || markCenterLength + markTailingLength > inputLength
                || markLeadingLength + markCenterLength + markTailingLength > inputLength;
        if (isAllMark) {
            // 如果需要全部脱敏，则返回与输入字符串等长的掩码字符串
            return UniversalString.repeat(null, null, null, null, null, mark, inputLength);
        }
        // 计算中间保留部分的长度
        int length = inputLength - markLeadingLength - markCenterLength - markTailingLength;
        int startLength = length / 2 + length % 2, endLength = length / 2;
        StringBuilder sb = new StringBuilder(inputLength);
        int count = 0;
        // 添加前缀掩码
        for (int i = 0; i < markLeadingLength; i++) {
            sb.append(mark);
        }
        count += markLeadingLength;
        // 添加前半部分保留内容
        for (int i = 0; i < startLength; i++) {
            int codePoint = Character.codePointAt(input, count + i);
            sb.appendCodePoint(codePoint);
            if (Character.charCount(codePoint) != 1) {
                i++;
            }
        }
        count += startLength;
        // 添加中间掩码
        for (int i = 0; i < markCenterLength; i++) {
            sb.append(mark);
        }
        count += markCenterLength;
        // 添加后半部分保留内容
        for (int i = 0; i < endLength; i++) {
            int codePoint = Character.codePointAt(input, count + i);
            sb.appendCodePoint(codePoint);
            if (Character.charCount(codePoint) != 1) {
                i++;
            }
        }
        count += endLength;
        // 添加后缀掩码
        for (int i = 0; i < markTailingLength; i++) {
            sb.append(mark);
        }
        count += markTailingLength;
        return sb.toString();
    }

    /**
     * 格式化字符串<br>
     * 此方法只是简单将指定占位符 按照顺序替换为参数<br>
     * 如果想输出占位符使用 \\转义即可，如果想输出占位符之前的 \ 使用双转义符 \\\\ 即可<br>
     * 例：<br>
     * 通常使用：format("this is {} for {}", "{}", "a", "b") =》 this is a for b<br>
     * 转义{}： format("this is \\{} for {}", "{}", "a", "b") =》 this is {} for a<br>
     * 转义\： format("this is \\\\{} for {}", "{}", "a", "b") =》 this is \a for b<br>
     *
     * @param template    字符串模板
     * @param placeHolder 占位符，例如{}
     * @param args        参数列表
     * @return 结果
     */
    public static String format(CharSequence template, CharSequence placeHolder, Object... args) {
        if (template == null || template.length() == 0) {
            return "";
        }
        if (placeHolder == null || placeHolder.length() == 0) {
            return template.toString();
        }
        int templateLength = template.length(), placeHolderLength = placeHolder.length();
        // 更合理的初始容量估算
        int estimatedCapacity = templateLength + (args.length * 10); // 假设每个参数平均长度为10
        StringBuilder sb = new StringBuilder(estimatedCapacity);
        int handledPosition = 0; // 已处理到的位置
        int argIndex = 0;
        while (handledPosition < templateLength) {
            int delimIndex = UniversalString.indexOf(false, template, handledPosition, templateLength, placeHolder);
            if (delimIndex == -1) {
                // 没有更多占位符了，追加剩余内容并返回
                sb.append(template, handledPosition, templateLength);
                break;
            }
            // 统计占位符前连续的反斜杠数量
            int slashCount = 0;
            for (int pos = delimIndex - 1; pos > 0 && template.charAt(pos) == '\\'; pos--) {
                slashCount++;
            }
            // 判断是否是转义
            if (slashCount % 2 == 1) {
                // 是转义，保留一个反斜杠，并跳过占位符
                sb.append(template, handledPosition, delimIndex - 1);
                sb.append(placeHolder);
            } else {
                // 不是转义，正常替换
                sb.append(template, handledPosition, delimIndex - (slashCount == 0 ? 0 : slashCount - 1));
                if (argIndex < args.length) {
                    sb.append(args[argIndex++]);
                } else {
                    // 参数不足，保留原始占位符
                    sb.append(placeHolder);
                }
            }
            handledPosition = delimIndex + placeHolderLength;
        }
        return sb.toString();
    }

    /**
     * 将给定的{@code String}表示形式解析为{@link Locale}，格式如下：
     *
     * <ul>
     *   <li>en
     *   <li>en_US
     *   <li>en US
     *   <li>en-US
     * </ul>
     *
     * @param input 语言环境名称
     * @return 相应的{@code Locale}实例，如果没有，则为{@code null}
     * @throws IllegalArgumentException 如果在无效的语言环境规范的情况下
     */
    public static Locale resolveLocale(CharSequence input) {
        if (input == null || input.length() == 0) {
            return null;
        }
        char[] inputChars = UniversalString.toChars(input);
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
        long[] parts = builder.build().toArray();
        if (parts.length == 0 || parts[0] == 0) {
            return null;
        }
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
    public static String resolveDecimal(CharSequence input, CharSequence source, CharSequence target) {
        if (input == null || input.length() == 0) {
            return null;
        }
        // 1. 去除前后空白字符，确定有效内容范围
        int[] bound = UniversalString.calculateTrimBounds(input, true, true);
        final int startInclusive = bound[0], endExclusive = bound[1];
        if (startInclusive >= endExclusive) {
            return null;
        }

        // 2. 初始化字符访问器，用于获取指定位置的Unicode码点
        IntUnaryOperator at = i -> {
            if (i < startInclusive || i >= endExclusive) {
                return -1;
            }
            return Character.codePointAt(input, i);
        };

        // 3. 检查并设置字符替换映射关系
        if (source != null) {
            if (target == null) {
                throw new IllegalArgumentException("The target must be not null");
            }
            int sourceLength = source.length();
            int targetLength = target.length();

            if (sourceLength > 0) {
                Map<Integer, Integer> map = new HashMap<>();
                for (int i = 0, iv, j = 0, jv; i < sourceLength && j < targetLength; i += Character.charCount(iv), j += Character.charCount(jv)) {
                    iv = Character.codePointAt(source, i);
                    jv = Character.codePointAt(target, j);
                    if (map.containsKey(iv)) {
                        throw new IllegalArgumentException("Duplicate mapping for character: " + iv);
                    }
                    map.put(iv, jv);
                }
                // 更新字符访问器，应用字符替换逻辑
                at = i -> {
                    if (i < startInclusive || i >= endExclusive) {
                        return -1;
                    }
                    int codePoint = Character.codePointAt(input, i);
                    return map.getOrDefault(codePoint, codePoint);
                };
            }
        }

        // 查找小数点位置（从后向前），用于判断是否存在合法的小数部分
        int dotIndex = endExclusive - 1;
        while (startInclusive < dotIndex && at.applyAsInt(dotIndex) != '.') {
            dotIndex--;
        }
        if (dotIndex <= startInclusive) {
            dotIndex = -1;
        }

        // 4. 构造结果数字字符数组，预留额外空间以应对格式转换
        int[] number = new int[endExclusive - startInclusive + 8];
        int count = 0;
        int currentIndex = startInclusive;
        int current = at.applyAsInt(currentIndex);
        // 处理可选的正负号
        if (current == '+' || current == '-') {
            number[count++] = current;
            current = at.applyAsInt(++currentIndex);
        }

        // 5. 标记是否出现过数字(必须至少有一个数字)
        boolean hasDigit = false;

        // 6. 处理整数部分(0个或多个数字)，跳过千位分隔符
        for (; current != -1; current = at.applyAsInt(currentIndex)) {
            if (current == ',' || (current == '.' && dotIndex != -1 && currentIndex < dotIndex)) {
                currentIndex++;
            } else if (Character.isDigit(current)) {
                hasDigit = true;
                number[count++] = current;
                currentIndex++;
            } else {
                break;
            }
        }
        // 7. 检查并处理小数点及小数部分
        if (current == '.') {
            current = at.applyAsInt(++currentIndex);
            // 如果整数部分没有数字，则补零
            if (!hasDigit) {
                number[count++] = '0';
            }
            number[count++] = '.';
            hasDigit = true;
            // 处理小数部分数字
            if (Character.isDigit(current)) {
                number[count++] = current;
                current = at.applyAsInt(++currentIndex);
                while (Character.isDigit(current)) {
                    number[count++] = current;
                    current = at.applyAsInt(++currentIndex);
                }
            } else {
                // 小数点后无数字则默认补零
                number[count++] = '0';
            }
        }

        // 8. 此时如果没有数字,说明无效(如".", "+.", "-"等)
        if (!hasDigit) {
            return null;
        }

        // 9. 检查并处理指数部分('e'或'E')
        if (current == 'e' || current == 'E') {
            current = at.applyAsInt(++currentIndex);
            int record = count;
            number[count++] = 'E';
            // 指数部分可选符号，默认为'+'
            if (current == '+' || current == '-') {
                number[count++] = current;
                current = at.applyAsInt(++currentIndex);
            } else {
                number[count++] = '+';
            }
            // 指数部分必须至少有一个数字
            int sign = count;
            while (Character.isDigit(current)) {
                number[count++] = current;
                current = at.applyAsInt(++currentIndex);
            }
            // 若指数部分无有效数字，则回退到记录点
            if (sign == count) {
                count = record;
            }
        }

        // 10. 必须整个字符串都被消耗完才算合法
        if (currentIndex == endExclusive) {
            return new String(number, 0, count);
        }
        return null;
    }

    /**
     * 解析特殊字符 {@code \t\n\r\f}
     *
     * @param input 字符
     * @return 返回特殊字符，如果不是则原样返回
     */
    public static int resolveEscape(int input, boolean unescape) {
        if (unescape) {
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
    public static String[] resolveArray(CharSequence input, char arrayElementSeparator, char escapeChar) {
        if (input == null || input.length() == 0) {
            return EMPTY_STRING_ARRAY;
        }
        int[] bound = UniversalString.calculateTrimBounds(input, true, true);
        int startInclusive = bound[0], endInclusive = bound[1] - 1;
        if (startInclusive > endInclusive) {
            return EMPTY_STRING_ARRAY;
        }
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
            return EMPTY_STRING_ARRAY;
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

    /**
     * 重复字符序列指定次数并用分隔符连接
     *
     * @param delimiter     分隔符
     * @param elementPrefix 每个元素的前缀
     * @param elementSuffix 每个元素的后缀
     * @param prefix        整体前缀
     * @param suffix        整体后缀
     * @param input         要重复的字符序列
     * @param repeat        重复次数
     * @return 重复并连接后的字符串
     */
    public static String repeat(CharSequence delimiter, CharSequence elementPrefix, CharSequence elementSuffix,
                                CharSequence prefix, CharSequence suffix, CharSequence input, int repeat) {
        if (repeat <= 0) {
            boolean emptyPrefix = UniversalString.isEmpty(prefix);
            boolean emptySuffix = UniversalString.isEmpty(suffix);
            if (emptyPrefix && emptySuffix) {
                return EMPTY_STRING;
            } else if (emptyPrefix) {
                return suffix.toString();
            } else if (emptySuffix) {
                return prefix.toString();
            } else {
                return prefix + "" + suffix;
            }
        }
        if (input == null || input.length() == 0) {
            return prefix + "" + suffix;
        }
        if (delimiter == null) {
            delimiter = "";
        }
        if (elementPrefix == null) {
            elementPrefix = "";
        }
        if (elementSuffix == null) {
            elementSuffix = "";
        }
        if (prefix == null) {
            prefix = "";
        }
        if (suffix == null) {
            suffix = "";
        }
        StringBuilder result = new StringBuilder();
        result.append(prefix);
        for (int i = 0; i < repeat; i++) {
            if (i > 0) {
                result.append(delimiter);
            }
            result.append(elementPrefix).append(input).append(elementSuffix);
        }
        result.append(suffix);
        return result.toString();
    }

    /**
     * 计算两个字符序列的相似度，基于编辑距离算法
     *
     * @param ignoreCase 是否忽略大小写
     * @param input      第一个字符序列
     * @param target     第二个字符序列
     * @param ignored    用于判断字符是否被忽略的谓词函数
     * @return 相似度值，范围在0.0-1.0之间，1.0表示完全相同，0.0表示完全不同
     */
    public static double similarity(boolean ignoreCase, CharSequence input, CharSequence target, IntPredicate ignored) {
        // 参数校验
        if (input == null || target == null) {
            return input == target ? 1.0 : 0.0;
        }
        // 边界条件处理：两个都是空串相似度为1
        int inputLength = input.length();
        int targetLength = target.length();
        if (inputLength == 0 && targetLength == 0) {
            return 1.0;
        }
        // 避免除零错误
        if (inputLength + targetLength == 0) {
            return 1.0;
        }
        int distance = UniversalString.similarDistance(ignoreCase, input, target, ignored);
        // 将编辑距离转换为0-1.0的相似度值
        return 1.0 - (double) distance / Math.max(inputLength, targetLength);
    }

    /**
     * 计算两个字符序列之间的相似编辑距离（Levenshtein Distance），支持忽略特定字符。
     * 使用动态规划算法实现，允许通过 IntPredicate 指定需要忽略的字符。
     *
     * @param ignoreCase 是否忽略大小写
     * @param input      输入字符序列
     * @param target     目标字符序列
     * @param ignored    用于判断是否忽略某个字符的谓词接口，若为 null 则不忽略任何字符
     * @return 两个字符序列之间的编辑距离
     */
    public static int similarDistance(boolean ignoreCase, CharSequence input, CharSequence target, IntPredicate ignored) {
        // 参数校验
        if (input == null || target == null) {
            return (input == null && target == null) ? 0 : (input == null ? target.length() : input.length());
        }
        int[] inputCodePoints = input.codePoints().toArray();
        int[] targetCodePoints = target.codePoints().toArray();
        int m = inputCodePoints.length;
        int n = targetCodePoints.length;
        // 创建dp表，dp[i][j]表示input前i个字符与target前j个字符的编辑距离
        int[][] dp = new int[m + 1][n + 1];
        // 初始化边界条件：空字符串到任意字符串的距离为其长度
        for (int i = 0; i <= m; i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= n; j++) {
            dp[0][j] = j;
        }
        // 填充dp表
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                // 如果当前字符被忽略，则直接继承前面的结果
                if (ignored != null && (ignored.test(inputCodePoints[i - 1]) || ignored.test(targetCodePoints[j - 1]))) {
                    dp[i][j] = Math.min(Math.min(dp[i - 1][j], dp[i][j - 1]), dp[i - 1][j - 1]);
                } else if (UniversalString.isEquals(ignoreCase, inputCodePoints[i - 1], targetCodePoints[j - 1])) {
                    dp[i][j] = dp[i - 1][j - 1]; // 字符相同，不需要操作
                } else {
                    // 字符不同，取增、删、改三种操作的最小值+1
                    dp[i][j] = Math.min(Math.min(dp[i - 1][j], dp[i][j - 1]), dp[i - 1][j - 1]) + 1;
                }
            }
        }
        return dp[m][n]; // 返回最终的编辑距离
    }

    /**
     * 去除字符序列两端的指定内容
     *
     * @param ignoreCase     是否忽略大小写
     * @param input          要处理的字符序列
     * @param isTrimLeading  是否去除开头
     * @param isTrimTrailing 是否去除结尾
     * @param trims          要去除的目标字符串数组
     * @return 处理后的字符串
     */
    public static String trim(boolean ignoreCase, CharSequence input, boolean isTrimLeading, boolean isTrimTrailing, CharSequence... trims) {
        // 空值检查：如果输入为空、长度为0，或既不处理前缀也不处理后缀，则直接返回空字符串
        if (input == null || input.length() == 0) {
            return EMPTY_STRING;
        }
        int inputLength = input.length(), startInclusive = 0, endExclusive = inputLength;
        // 如果没有提供要去除的内容，则使用默认空白字符进行trim操作
        if (trims == null || trims.length == 0) {
            return UniversalString.trim(input, isTrimLeading, isTrimTrailing, Character::isWhitespace);
        }
        // 过滤掉null和长度大于等于原字符串的无效trim项
        trims = Arrays.stream(trims).filter(Objects::nonNull).filter(ele -> ele.length() < inputLength).toArray(CharSequence[]::new);
        if (trims.length == 0) {
            return UniversalString.trim(input, isTrimLeading, isTrimTrailing, Character::isWhitespace);
        }
        // 去除开头部分匹配的内容
        if (isTrimLeading) {
            while (startInclusive < endExclusive) {
                boolean trim = false;
                // 检查当前起始位置是否与任意一个目标字符串匹配
                for (CharSequence target : trims) {
                    if (UniversalString.regionMatches(ignoreCase, input, startInclusive, target, 0, target.length())) {
                        trim = true;
                        startInclusive += target.length();
                        break;
                    }
                }
                if (!trim) {
                    break;
                }
            }
        }
        // 去除结尾部分匹配的内容
        if (isTrimTrailing) {
            while (startInclusive < endExclusive) {
                boolean trim = false;
                // 检查当前结束位置是否与任意一个目标字符串匹配
                for (CharSequence target : trims) {
                    if (UniversalString.regionMatches(ignoreCase, input, endExclusive - target.length(), target, 0, target.length())) {
                        trim = true;
                        endExclusive -= target.length();
                        break;
                    }
                }
                if (!trim) {
                    break;
                }
            }
        }
        // 如果整个字符串都被移除，则返回空字符串
        if (startInclusive >= endExclusive) {
            return EMPTY_STRING;
        }
        // 返回处理后的新字符串
        return input.subSequence(startInclusive, endExclusive).toString();
    }

    /**
     * 根据条件去除字符序列两端的内容
     *
     * @param input          要处理的字符序列
     * @param isTrimLeading  是否去除开头
     * @param isTrimTrailing 是否去除结尾
     * @param trimCondition  去除条件谓词
     * @return 处理后的字符串
     */
    public static String trim(CharSequence input, boolean isTrimLeading, boolean isTrimTrailing, IntPredicate trimCondition) {
        // 空值检查
        if (input == null || input.length() == 0) {
            return EMPTY_STRING;
        }
        int[] indexes = UniversalString.calculateTrimBounds(input, isTrimLeading, isTrimTrailing, trimCondition == null ? Character::isWhitespace : trimCondition);
        if (indexes[0] >= indexes[1]) {
            return EMPTY_STRING;
        }
        // 返回处理后的子串
        return input.subSequence(indexes[0], indexes[1]).toString();
    }

    /**
     * 根据条件去除字符数组两端的内容
     *
     * @param input          要处理的字符数组
     * @param isTrimLeading  是否去除开头
     * @param isTrimTrailing 是否去除结尾
     * @param trimCondition  去除条件谓词
     * @return 处理后的字符串
     */
    public static String trim(char[] input, boolean isTrimLeading, boolean isTrimTrailing, IntPredicate trimCondition) {
        // 空值检查
        if (input == null || input.length == 0) {
            return EMPTY_STRING;
        }
        if (trimCondition == null) {
            trimCondition = Character::isWhitespace;
        }
        IntPredicate trim = trimCondition;
        int[] indexes = {0, input.length};
        UniversalString.calculateTrimBounds(indexes, isTrimLeading, isTrimTrailing, i -> trim.test(input[i]) ? 1 : 0);
        if (indexes[0] >= indexes[1]) {
            return EMPTY_STRING;
        }
        // 返回处理后的子串
        return new String(input, indexes[0], indexes[1] - indexes[0]);
    }

    /**
     * 根据条件去除整数数组两端的内容
     *
     * @param input          要处理的整数数组（通常表示Unicode码点）
     * @param isTrimLeading  是否去除开头
     * @param isTrimTrailing 是否去除结尾
     * @param trimCondition  去除条件谓词
     * @return 处理后的字符串
     */
    public static String trim(int[] input, boolean isTrimLeading, boolean isTrimTrailing, IntPredicate trimCondition) {
        // 空值检查
        if (input == null || input.length == 0) {
            return EMPTY_STRING;
        }
        if (trimCondition == null) {
            trimCondition = Character::isWhitespace;
        }
        IntPredicate trim = trimCondition;
        int[] indexes = {0, input.length};
        UniversalString.calculateTrimBounds(indexes, isTrimLeading, isTrimTrailing, i -> trim.test(input[i]) ? 1 : 0);
        if (indexes[0] >= indexes[1]) {
            return EMPTY_STRING;
        }
        // 返回处理后的子串
        return new String(input, indexes[0], indexes[1] - indexes[0]);
    }

    /**
     * 将[1-20]数字转换为特殊字符：
     *
     * @param number 要转换的数字，范围必须是[1-20]
     * @param type   转换类型，1-带圈数字，2-带括号数字，3-带圆圈数字，其他-带点数字
     * @return 转换后的特殊字符
     * @throws IllegalArgumentException 当number不在[1-20]范围内时抛出
     */
    public static char toStyledNumberChar(int number, int type) {
        if (number <= 0 || number > 20) {
            throw new IllegalArgumentException("Number must be [1-20]");
        }
        number -= 1;
        int value;
        switch (type) {
            case 1:
                // 转换为带圈数字字符①-⑳
                value = '①' + number;
                break;
            case 2:
                // 转换为带括号数字字符⑴-⑳
                value = '⑴' + number;
                break;
            case 3:
                // 转换为带圆圈数字字符，1-10使用⓵-⓾，11-20使用⓫-㉟
                if (number <= 10) {
                    value = '⓵' + number;
                } else {
                    value = '⓫' + number - 10;
                }
                break;
            default:
                // 默认转换为带点数字字符⒈-⒛
                value = '⒈' + number;
                break;
        }
        return (char) value;
    }

    /**
     * 将字符序列转换为Unicode码点数组
     *
     * @param input 输入的字符序列，可以是String、StringBuilder等实现了CharSequence接口的对象
     * @return 返回Unicode码点数组，如果输入为null或空则返回空数组
     */
    public static int[] toCodePoints(CharSequence input) {
        // 如果输入为null或长度为0，返回空数组；否则将字符序列转换为码点流并收集为数组
        return input == null || input.length() == 0 ? EMPTY_INT_ARRAY : input.codePoints().toArray();
    }

    /**
     * 将CharSequence转换为字符数组
     *
     * @param input 输入的CharSequence对象，可以为null
     * @return 返回对应的字符数组，如果输入为null或空则返回空字符数组
     */
    public static char[] toChars(CharSequence input) {
        // 处理null或空输入的情况
        if (input == null || input.length() == 0) {
            return EMPTY_CHAR_ARRAY;
        }
        // 如果输入是String类型，直接使用toCharArray方法转换
        if (input instanceof String) {
            return ((String) input).toCharArray();
        } else {
            // 对于其他CharSequence实现，手动复制每个字符到数组中
            int sz = input.length();
            char[] array = new char[input.length()];
            for (int i = 0; i < sz; i++) {
                array[i] = input.charAt(i);
            }
            return array;
        }
    }

    /**
     * 将字符序列转换为小写形式
     *
     * @param input 要转换的字符序列
     * @return 转换后的小写字符串
     */
    public static String toLowerCase(CharSequence input) {
        if (input == null || input.length() == 0) {
            return EMPTY_STRING;
        }
        return input.toString().toLowerCase();
    }

    /**
     * 将字符序列数组中的每个元素转换为小写形式
     *
     * @param inputs 要转换的字符序列数组
     * @return 转换后的小写字符串数组
     */
    public static String[] toLowerCase(CharSequence... inputs) {
        return UniversalString.toStringArray(UniversalString::toLowerCase, inputs);
    }

    /**
     * 将字符序列转换为大写形式
     *
     * @param input 要转换的字符序列
     * @return 转换后的大写字符串
     */
    public static String toUpperCase(CharSequence input) {
        if (input == null || input.length() == 0) {
            return EMPTY_STRING;
        }
        return input.toString().toUpperCase();
    }

    /**
     * 将字符序列数组中的每个元素转换为大写形式
     *
     * @param inputs 要转换的字符序列数组
     * @return 转换后的大写字符串数组
     */
    public static String[] toUpperCase(CharSequence... inputs) {
        return UniversalString.toStringArray(UniversalString::toUpperCase, inputs);
    }

    /**
     * 根据方法对象生成方法标签字符串
     *
     * @param method 方法对象
     * @param var    参数变量名前缀，当parameterNames为null时使用
     * @return 方法标签字符串，格式为"返回类型 方法名(参数类型1 参数名1, 参数类型2 参数名2, ...)"
     */
    public static String toMethodTag(Method method, String var) {
        return UniversalString.toMethodTag(method.getName(), method.getReturnType(), method.getParameterTypes(), null, var);
    }

    /**
     * 生成方法标签字符串
     *
     * @param methodName     方法名
     * @param returnType     返回类型
     * @param parameterTypes 参数类型数组
     * @param parameterNames 参数名称数组，如果为null则使用var参数或默认的"var"+索引
     * @param var            参数变量名前缀，当parameterNames为null时使用
     * @return 方法标签字符串，格式为"返回类型 方法名(参数类型1 参数名1, 参数类型2 参数名2, ...)"
     */
    public static String toMethodTag(String methodName, Class<?> returnType, Class<?>[] parameterTypes, CharSequence[] parameterNames, String var) {
        StringBuilder container = new StringBuilder();
        if (returnType != null) {
            container.append(returnType.getSimpleName()).append(" ");
        } else {
            container.append("void ");
        }
        container.append(methodName).append("(");
        if (parameterTypes != null && parameterTypes.length > 0) {
            for (int i = 0; i < parameterTypes.length; i++) {
                container.append(parameterTypes[i].getSimpleName()).append(" ");
                if (parameterNames != null && i < parameterNames.length) {
                    container.append(parameterNames[i]);
                } else if (var != null && !var.isEmpty()) {
                    container.append(var).append(i);
                } else {
                    container.append("var").append(i);
                }
                container.append(", ");
            }
            container.setLength(container.length() - 2);
        }
        container.append(")");
        return container.toString();
    }

    /**
     * 将Locale对象转换为字符串表示形式
     *
     * @param input 要转换的Locale对象
     * @return 转换后的字符串，格式为"language-country"或仅"language"
     */
    public static String toString(Locale input) {
        return input.getLanguage() + (UniversalString.isNotEmpty(input.getCountry()) ? "-" + input.getCountry() : "");
    }

    /**
     * 使用指定的转换器函数将输入数组中的元素转换为字符串数组
     *
     * @param converter 转换器函数，用于将输入元素转换为字符串
     * @param inputs    输入元素数组
     * @param <T>       输入元素类型
     * @return 转换后的字符串数组
     */
    @SafeVarargs
    public static <T> String[] toStringArray(Function<T, String> converter, T... inputs) {
        if (inputs == null || inputs.length == 0) {
            return UniversalString.EMPTY_STRING_ARRAY;
        }
        String[] results = new String[inputs.length];
        int len = 0;
        for (T input : inputs) {
            results[len] = converter.apply(input);
            if (results[len] != null) {
                len++;
            }
        }
        if (len == 0) {
            return UniversalString.EMPTY_STRING_ARRAY;
        }
        if (len != inputs.length) {
            String[] temp = new String[len];
            System.arraycopy(results, 0, temp, 0, len);
            results = temp;
        }
        return results;
    }

    /**
     * 提取并清理字符序列中的字符串
     * <p>
     * 该方法根据指定的条件对输入的字符序列进行处理，包括去除首尾空白字符、提取子字符串等操作。
     * 支持StringBuilder和StringBuffer的就地修改，以及其他字符序列的字符串提取。
     *
     * @param input          输入的字符序列，可以是StringBuilder、StringBuffer或其他CharSequence实现
     * @param target         目标集合，用于存储处理后的结果字符串，如果为null则不添加
     * @param isTrimLeading  是否去除开头的空白字符
     * @param isTrimTrailing 是否去除结尾的空白字符
     * @param retainEmpty    是否保留空字符串，如果为false则空字符串不会添加到target集合中
     * @param trimCondition  判断字符是否应该被去除的条件谓词，如果为null则跳过trim操作
     * @return 返回处理后的字符串，如果输入为null则返回空字符串
     */
    public static String extractString(CharSequence input, Collection<String> target, boolean isTrimLeading, boolean isTrimTrailing, boolean retainEmpty, IntPredicate trimCondition) {
        String result = null;
        if (input != null) {
            // 如果提供了trim条件，则根据条件计算需要保留的边界范围
            if (trimCondition != null) {
                int[] bound = UniversalString.calculateTrimBounds(input, isTrimLeading, isTrimTrailing, trimCondition);
                // 对于StringBuilder和StringBuffer，直接修改其内容以提高性能
                if (input instanceof StringBuilder) {
                    ((StringBuilder) input).setLength(bound[1]);
                    if (bound[0] != 0) {
                        ((StringBuilder) input).delete(0, bound[0]);
                    }
                } else if (input instanceof StringBuffer) {
                    ((StringBuffer) input).setLength(bound[1]);
                    if (bound[0] != 0) {
                        ((StringBuffer) input).delete(0, bound[0]);
                    }
                } else {
                    // 对于其他类型的字符序列，提取子序列并转换为字符串
                    result = input.subSequence(bound[0], bound[1]).toString();
                }
            }
            // 如果还没有生成结果字符串，则根据输入类型进行相应处理
            if (result == null) {
                if (input instanceof StringBuilder) {
                    result = input.toString();
                    ((StringBuilder) input).setLength(0);
                } else if (input instanceof StringBuffer) {
                    result = input.toString();
                    ((StringBuffer) input).setLength(0);
                } else {
                    result = input.toString();
                }
            }
        } else {
            result = EMPTY_STRING;
        }
        // 如果目标集合不为null，并且满足保留条件，则将结果添加到目标集合中
        if (target != null && (retainEmpty || !result.isEmpty())) {
            target.add(result);
        }
        return result;
    }
}
