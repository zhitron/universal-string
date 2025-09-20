package com.github.zhitron.universal;

import java.util.ArrayList;
import java.util.stream.IntStream;

/**
 * AsciiWordToken 是一个用于处理和转换 ASCII 字符串的工具类。
 * 它支持多种命名规范的转换，如驼峰命名、下划线分隔、短横线分隔等。
 * 该类是不可变的，并且是线程安全的。
 *
 * @author zhitron
 */
public final class AsciiWordToken {
    /**
     * 空的AsciiWordToken实例
     */
    private static final AsciiWordToken EMPTY = new AsciiWordToken(new String[0], 0, null);
    /**
     * 存储分割后的单词部分
     */
    private final String[] parts;
    /**
     * 哈希码缓存，用于快速比较
     */
    private final int hash;
    /**
     * 格式类型，用于toString()方法确定输出格式
     */
    private final FormatType formatType;

    /**
     * 构造函数
     *
     * @param parts      单词部分数组
     * @param hash       哈希码
     * @param formatType 格式类型
     */
    private AsciiWordToken(String[] parts, int hash, FormatType formatType) {
        this.parts = parts;
        this.hash = hash;
        this.formatType = formatType;
    }

    /**
     * 创建小写驼峰命名规范的AsciiWordToken实例
     *
     * @param string 输入字符串
     * @return AsciiWordToken实例
     */
    public static AsciiWordToken ofLowerCamelCase(String string) {
        return AsciiWordToken.of(string, FormatType.LowerCamelCase);
    }

    /**
     * 创建大写驼峰命名规范的AsciiWordToken实例
     *
     * @param string 输入字符串
     * @return AsciiWordToken实例
     */
    public static AsciiWordToken ofUpperCamelCase(String string) {
        return AsciiWordToken.of(string, FormatType.UpperCamelCase);
    }

    /**
     * 创建小写下划线命名规范的AsciiWordToken实例
     *
     * @param string 输入字符串
     * @return AsciiWordToken实例
     */
    public static AsciiWordToken ofLowerUnderLine(String string) {
        return AsciiWordToken.of(string, FormatType.LowerUnderLine);
    }

    /**
     * 创建大写下划线命名规范的AsciiWordToken实例
     *
     * @param string 输入字符串
     * @return AsciiWordToken实例
     */
    public static AsciiWordToken ofUpperUnderLine(String string) {
        return AsciiWordToken.of(string, FormatType.UpperUnderLine);
    }

    /**
     * 创建小写短横线命名规范的AsciiWordToken实例
     *
     * @param string 输入字符串
     * @return AsciiWordToken实例
     */
    public static AsciiWordToken ofLowerKebabCase(String string) {
        return AsciiWordToken.of(string, FormatType.LowerKebabCase);
    }

    /**
     * 创建大写短横线命名规范的AsciiWordToken实例
     *
     * @param string 输入字符串
     * @return AsciiWordToken实例
     */
    public static AsciiWordToken ofUpperKebabCase(String string) {
        return AsciiWordToken.of(string, FormatType.UpperKebabCase);
    }

    /**
     * 创建默认格式的AsciiWordToken实例
     *
     * @param string 输入字符串
     * @return AsciiWordToken实例
     */
    public static AsciiWordToken of(String string) {
        return AsciiWordToken.of(string, null);
    }

    /**
     * 创建指定格式类型的AsciiWordToken实例
     *
     * @param string     输入字符串
     * @param formatType 格式类型
     * @return AsciiWordToken实例
     */
    private static AsciiWordToken of(String string, FormatType formatType) {
        if (string == null || string.isEmpty()) return AsciiWordToken.EMPTY;
        int[] codePoints = AsciiWordToken.asciiStream(string).toArray();
        int hash = AsciiWordToken.hashcode(codePoints);
        String[] parts = AsciiWordToken.parts(codePoints);
        return new AsciiWordToken(parts, hash, formatType);
    }

    /**
     * 过滤并验证ASCII字符流
     *
     * @param string 输入字符串
     * @return 有效的ASCII字符流
     */
    private static IntStream asciiStream(String string) {
        return string.codePoints().filter(v -> {
            if (v == '$' || v == '_' || v == '-' || (v >= '0' && v <= '9') || (v >= 'a' && v <= 'z') || (v >= 'A' && v <= 'Z')) {
                return true;
            } else {
                throw new IllegalArgumentException("Invalid character: " + v);
            }
        });
    }

    /**
     * 计算哈希码，忽略大小写
     *
     * @param codePoints 字符数组
     * @return 哈希码
     */
    private static int hashcode(int[] codePoints) {
        int hash = 1;
        for (int v : codePoints) {
            if (v == '$' || (v >= '0' && v <= '9') || (v >= 'a' && v <= 'z') || (v >= 'A' && v <= 'Z')) {
                hash = 31 * hash + v + ((v >= 'A' && v <= 'Z') ? 32 : 0);
            }
        }
        return hash;
    }

    /**
     * 将字符数组分割成单词部分
     *
     * @param codePoints 字符数组
     * @return 单词部分数组
     */
    private static String[] parts(int[] codePoints) {
        // 预估容量以减少扩容
        ArrayList<String> list = new ArrayList<>(Math.max(6, codePoints.length / 4));
        StringBuilder sb = new StringBuilder(16);
        for (int i = 0; i < codePoints.length; i++) {
            int codePoint = codePoints[i];
            // 分隔符处理
            if (codePoint == '-' || codePoint == '_' || codePoint == '&') {
                if (sb.length() != 0) {
                    list.add(sb.toString());
                    sb.setLength(0);
                }
                continue;
            }
            // 小写字母和数字直接添加
            if ((codePoint >= 'a' && codePoint <= 'z') || (codePoint >= '0' && codePoint <= '9')) {
                sb.appendCodePoint(codePoint);
                continue;
            }
            // 大写字母和$处理
            if ((codePoint >= 'A' && codePoint <= 'Z') || codePoint == '$') {
                if (sb.length() != 0) {
                    list.add(sb.toString());
                    sb.setLength(0);
                }
                // 添加当前字符
                sb.appendCodePoint(codePoint);
                int j = i + 1;
                // 查找连续的大写字母或数字
                while (j < codePoints.length &&
                        ((codePoints[j] >= 'A' && codePoints[j] <= 'Z') ||
                                (codePoints[j] >= '0' && codePoints[j] <= '9'))) {
                    j++;
                }
                // 如果有连续的大写字母或数字
                if (j > i + 1) {
                    // 检查下一个字符是否为小写字母
                    if (j < codePoints.length && codePoints[j] >= 'a' && codePoints[j] <= 'z') {
                        // 将除最后一个外的所有字符添加到当前部分
                        for (int k = i + 1; k < j - 1; k++) {
                            sb.appendCodePoint(codePoints[k]);
                        }
                        i = j - 2; // 调整索引
                    } else {
                        // 全部添加到当前部分
                        for (int k = i + 1; k < j; k++) {
                            sb.appendCodePoint(codePoints[k]);
                        }
                        i = j - 1; // 调整索引
                    }
                }
                continue;
            }
            throw new IllegalArgumentException("Invalid character: " + codePoint);
        }
        // 添加最后一个部分
        if (sb.length() != 0) {
            list.add(sb.toString());
        }
        return list.toArray(new String[0]);
    }

    /**
     * 转换为大写短横线命名
     *
     * @return 大写短横线命名字符串
     */
    public String toUpperKebabCase() {
        return this.toKebabCase(true);
    }

    /**
     * 转换为大写下划线命名
     *
     * @return 大写下划线命名字符串
     */
    public String toUpperUnderLine() {
        return this.toUnderLine(true);
    }

    /**
     * 转换为大写驼峰命名
     *
     * @return 大写驼峰命名字符串
     */
    public String toUpperCamelCase() {
        return this.toCamelCase(true);
    }

    /**
     * 转换为小写短横线命名
     *
     * @return 小写短横线命名字符串
     */
    public String toLowerKebabCase() {
        return this.toKebabCase(false);
    }

    /**
     * 转换为小写下划线命名
     *
     * @return 小写下划线命名字符串
     */
    public String toLowerUnderLine() {
        return this.toUnderLine(false);
    }

    /**
     * 转换为小写驼峰命名
     *
     * @return 小写驼峰命名字符串
     */
    public String toLowerCamelCase() {
        return this.toCamelCase(false);
    }

    /**
     * 转换为短横线命名
     *
     * @param upper 是否大写
     * @return 短横线命名字符串
     */
    private String toKebabCase(boolean upper) {
        return marge("-", upper);
    }

    /**
     * 转换为下划线命名
     *
     * @param upper 是否大写
     * @return 下划线命名字符串
     */
    private String toUnderLine(boolean upper) {
        return marge("_", upper);
    }

    /**
     * 转换为驼峰命名
     *
     * @param upper 是否大写
     * @return 驼峰命名字符串
     */
    private String toCamelCase(boolean upper) {
        return marge(null, upper);
    }

    /**
     * 合并单词部分为指定格式的字符串
     *
     * @param delimiter 分隔符，null表示驼峰命名
     * @param upper     是否大写（仅对分隔符格式有效）
     * @return 合并后的字符串
     */
    private String marge(String delimiter, boolean upper) {
        if (parts.length == 0) {
            return "";
        }
        String string = parts[0];
        boolean isCamelCase = delimiter == null || delimiter.isEmpty();
        StringBuilder sb = new StringBuilder(parts.length * (string.length() + 3));
        if (isCamelCase) {
            if (Character.isUpperCase(string.charAt(0)) && Character.isLowerCase(string.charAt(string.length() - 1))) {
                if (upper) {
                    sb.append(string);
                } else {
                    sb.append(string.toLowerCase());
                }
            } else {
                string = parts[0];
                boolean set = false;
                for (int i = 0; i < string.length(); i++) {
                    char c = string.charAt(i);
                    if (!set && Character.isLetter(c)) {
                        sb.append(upper ? Character.toUpperCase(c) : Character.toLowerCase(c));
                        set = true;
                    } else {
                        sb.append(Character.toLowerCase(c));
                    }
                }
            }
        } else {
            sb.append(upper ? string.toUpperCase() : string.toLowerCase());
        }
        for (int x = 1; x < parts.length; x++) {
            if (delimiter != null) sb.append(delimiter);
            string = parts[x];
            if (isCamelCase) {
                boolean set = false;
                for (int i = 0; i < string.length(); i++) {
                    char c = string.charAt(i);
                    if (!set && Character.isLetter(c)) {
                        sb.append(Character.toUpperCase(c));
                        set = true;
                    } else {
                        sb.append(Character.toLowerCase(c));
                    }
                }
            } else {
                sb.append(upper ? string.toUpperCase() : string.toLowerCase());
            }
        }
        return sb.toString();
    }

    /**
     * 禁止克隆
     */
    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    /**
     * 比较两个AsciiWordToken是否相等
     *
     * @param other 另一个对象
     * @return 是否相等
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null) return false;
        String[] otherParts = null;
        int otherHash = -1;
        if (other instanceof String) {
            int[] codePoints = AsciiWordToken.asciiStream((String) other).toArray();
            otherHash = AsciiWordToken.hashcode(codePoints);
            otherParts = AsciiWordToken.parts(codePoints);
        } else if (other instanceof AsciiWordToken) {
            otherHash = ((AsciiWordToken) other).hash;
            otherParts = ((AsciiWordToken) other).parts;
        }
        if (otherParts != null && hash == otherHash && parts.length == otherParts.length) {
            for (int i = 0; i < parts.length; i++) {
                if (!parts[i].equalsIgnoreCase(otherParts[i])) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * 获取哈希码
     *
     * @return 哈希码
     */
    @Override
    public int hashCode() {
        return hash;
    }

    /**
     * 转换为字符串表示
     *
     * @return 字符串表示
     */
    @Override
    public String toString() {
        if (formatType != null) {
            switch (formatType) {
                case UpperCamelCase:
                    return toUpperCamelCase();
                case LowerCamelCase:
                    return toLowerCamelCase();
                case UpperUnderLine:
                    return toUpperUnderLine();
                case LowerUnderLine:
                    return toLowerUnderLine();
                case UpperKebabCase:
                    return toUpperKebabCase();
                case LowerKebabCase:
                    return toLowerKebabCase();
            }
        }
        return marge(null, false);
    }

    /**
     * 支持的格式类型枚举
     */
    private enum FormatType {
        /**
         * 大写驼峰命名：FirstName
         */
        UpperCamelCase,
        /**
         * 小写驼峰命名：firstName
         */
        LowerCamelCase,
        /**
         * 大写下划线命名：FIRST_NAME
         */
        UpperUnderLine,
        /**
         * 小写下划线命名：first_name
         */
        LowerUnderLine,
        /**
         * 大写短横线命名：FIRST-NAME
         */
        UpperKebabCase,
        /**
         * 小写短横线命名：first-name
         */
        LowerKebabCase,
    }
}
