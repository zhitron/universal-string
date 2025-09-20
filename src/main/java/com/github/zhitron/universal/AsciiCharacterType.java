package com.github.zhitron.universal;
/**
 * ASCII字符类型枚举，用于识别和分类ASCII字符
 *
 * @author zhitron
 */
public enum AsciiCharacterType {
    /**
     * 控制字符 (0-31 127)
     */
    CONTROL,
    /**
     * 设备符号 (17-20)
     */
    DEVICE,
    /**
     * 不可见字符 (\b \f \r \n \t   )
     */
    INVISIBLE,
    /**
     * 换行字符 (\r \n)
     */
    LINE_BREAKS,
    /**
     * 空白字符 (\t  )
     */
    BLANK,
    /**
     * 字母 (A-Z a-z)
     */
    LETTER,
    /**
     * 大写字母 (A-Z)
     */
    UPPER_LETTER,
    /**
     * 小写字母 (a-z)
     */
    LOWER_LETTER,
    /**
     * 数字字符 (0-9 + - .)
     */
    NUMBER,
    /**
     * 十进制数字 (0-9)
     */
    NUM_DEC,
    /**
     * 十六进制数字 (0-9 a-f A-F)
     */
    NUM_HEX,
    /**
     * 八进制数字 (0-7)
     */
    NUM_OCT,
    /**
     * 标点符号
     */
    PUNCTUATION,
    /**
     * 四则运算符号 (+ - * /)
     */
    ARITHMETIC,
    /**
     * 比较操作符号 (&lt; &gt; =)
     */
    COMPARISON,
    /**
     * 括号符号 (&lt;&gt;)(&#91;&#93;){}
     */
    BRACKETS,
    /**
     * 左括号符号 (&lt; ( &#91; {)
     */
    LEFT_BRACKETS,
    /**
     * 右括号符号 (&gt; ) &#93; })
     */
    RIGHT_BRACKETS,
    /**
     * 路径分隔符 (/ \\)
     */
    PATH_SEPARATOR,
    /**
     * 空白字符 (\t \n \r  )
     */
    WHITESPACE,
    /**
     * 符号字符（非字母、非数字、非控制字符的可打印字符）
     */
    SYMBOL,
    /**
     * 可打印字符 (空格到波浪号)
     */
    PRINTABLE,
    /**
     * 字母数字字符 (A-Z a-z 0-9)
     */
    ALPHANUMERIC,
    /**
     * 引号字符 (" ' `)
     */
    QUOTE,
    /**
     * 特殊符号 (@ # $ % ^ &amp; *)
     */
    SPECIAL;
    private static final int[] ASCII = new int[128];

    static {
        /*
         * 初始化各字符类型的ASCII码表
         */

        // CONTROL - 控制字符 (0-31, 127)
        AsciiCharacterType.setSymbol(CONTROL, 0, 31, 127);

        // DEVICE - 设备符号 (17-20)
        AsciiCharacterType.setSymbol(DEVICE, 17, 20);

        // INVISIBLE - 不可见字符
        AsciiCharacterType.setSymbol(INVISIBLE, -1, -1, '\b', '\f', '\r', '\n', '\t', ' ');

        // LINE_BREAKS - 换行字符
        AsciiCharacterType.setSymbol(LINE_BREAKS, -1, -1, '\r', '\n');

        // BLANK - 空白字符
        AsciiCharacterType.setSymbol(BLANK, -1, -1, '\t', ' ');

        // LETTER - 字母
        AsciiCharacterType.setSymbol(LETTER, 'A', 'Z');
        AsciiCharacterType.setSymbol(LETTER, 'a', 'z');

        // UPPER_LETTER - 大写字母
        AsciiCharacterType.setSymbol(UPPER_LETTER, 'A', 'Z');

        // LOWER_LETTER - 小写字母
        AsciiCharacterType.setSymbol(LOWER_LETTER, 'a', 'z');

        // NUMBER - 数字（包括正负号和小数点）
        AsciiCharacterType.setSymbol(NUMBER, '0', '9', '+', '-', '.');

        // NUM_DEC - 10进制数字
        AsciiCharacterType.setSymbol(NUM_DEC, '0', '9');

        // NUM_HEX - 16进制数字
        AsciiCharacterType.setSymbol(NUM_HEX, '0', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'A', 'B', 'C', 'D', 'E', 'F');

        // NUM_OCT - 8进制数字
        AsciiCharacterType.setSymbol(NUM_OCT, '0', '7');

        // PUNCTUATION - 标点符号
        AsciiCharacterType.setSymbol(PUNCTUATION, 33, 47);  // !"#$%&'()*+,-./
        AsciiCharacterType.setSymbol(PUNCTUATION, 58, 64);  // :;<=>?@
        AsciiCharacterType.setSymbol(PUNCTUATION, 91, 96);  // [\]^_`
        AsciiCharacterType.setSymbol(PUNCTUATION, 123, 126); // {|}~

        // ARITHMETIC - 四则运算符号
        AsciiCharacterType.setSymbol(ARITHMETIC, -1, -1, '+', '-', '*', '/');

        // COMPARISON - 比较操作符号
        AsciiCharacterType.setSymbol(COMPARISON, -1, -1, '<', '>', '=');

        // BRACKETS - 括号符号
        AsciiCharacterType.setSymbol(BRACKETS, -1, -1, '<', '>', '(', ')', '[', ']', '{', '}');

        // LEFT_BRACKETS - 左括号符号
        AsciiCharacterType.setSymbol(LEFT_BRACKETS, -1, -1, '<', '(', '[', '{');

        // RIGHT_BRACKETS - 右括号符号
        AsciiCharacterType.setSymbol(RIGHT_BRACKETS, -1, -1, '>', ')', ']', '}');

        // PATH_SEPARATOR - 路径分隔符
        AsciiCharacterType.setSymbol(PATH_SEPARATOR, -1, -1, '/', '\\');

        // WHITESPACE - 空白字符
        AsciiCharacterType.setSymbol(WHITESPACE, -1, -1, '\t', '\n', '\r', ' ');

        // SYMBOL - 符号字符（非字母、非数字、非控制字符的可打印字符）
        AsciiCharacterType.setSymbol(SYMBOL, 33, 47);
        AsciiCharacterType.setSymbol(SYMBOL, 58, 64);
        AsciiCharacterType.setSymbol(SYMBOL, 91, 96);
        AsciiCharacterType.setSymbol(SYMBOL, 123, 126);

        // PRINTABLE - 可打印字符
        AsciiCharacterType.setSymbol(PRINTABLE, ' ', '~');

        // ALPHANUMERIC - 字母数字字符
        AsciiCharacterType.setSymbol(ALPHANUMERIC, 'A', 'Z');
        AsciiCharacterType.setSymbol(ALPHANUMERIC, 'a', 'z');
        AsciiCharacterType.setSymbol(ALPHANUMERIC, '0', '9');

        // QUOTE - 引号字符
        AsciiCharacterType.setSymbol(QUOTE, -1, -1, '"', '\'', '`');

        // SPECIAL - 特殊符号
        AsciiCharacterType.setSymbol(SPECIAL, -1, -1, '@', '#', '$', '%', '^', '&', '*');
    }

    /**
     * 设置指定ASCII范围内和特定字符的类型标记
     *
     * @param asciiCharacterType ASCII字符类型
     * @param startInclusive     起始字符（包含）
     * @param endInclusive       结束字符（包含）
     * @param chars              额外的特定字符
     */
    private static void setSymbol(AsciiCharacterType asciiCharacterType, int startInclusive, int endInclusive, int... chars) {
        int type = 1 << asciiCharacterType.ordinal();
        if (startInclusive <= endInclusive && startInclusive >= 0 && startInclusive < 128) {
            for (int i = startInclusive; i <= endInclusive; i++) {
                ASCII[i] |= type;
            }
        }
        for (int c : chars) {
            ASCII[c] |= type;
        }
    }

    /**
     * 判断字符码点是否全部属于指定的ASCII字符类型
     *
     * @param codepoint                字符码点
     * @param exceptAsciiCharacterType 期望的ASCII字符类型
     * @return 如果全部符合返回 {@code true}，否则 {@code false}
     */
    public static boolean isMatchAll(int codepoint, AsciiCharacterType... exceptAsciiCharacterType) {
        if (codepoint < 0 || codepoint >= ASCII.length) {
            return false;
        }
        int type = 0;
        for (AsciiCharacterType asciiCharacterType : exceptAsciiCharacterType) {
            type |= 1 << asciiCharacterType.ordinal();
        }
        return (ASCII[codepoint] & type) == type;
    }

    /**
     * 判断字符码点是否任意属于指定的ASCII字符类型
     *
     * @param codepoint                字符码点
     * @param exceptAsciiCharacterType 期望的ASCII字符类型
     * @return 如果任意符合返回 {@code true}，否则 {@code false}
     */
    public static boolean isMatchAny(int codepoint, AsciiCharacterType... exceptAsciiCharacterType) {
        if (codepoint < 0 || codepoint >= ASCII.length) {
            return false;
        }
        int type = 0;
        for (AsciiCharacterType asciiCharacterType : exceptAsciiCharacterType) {
            type |= 1 << asciiCharacterType.ordinal();
        }
        return (ASCII[codepoint] & type) != 0;
    }

    /**
     * 判断字符码点是否属于当前ASCII字符类型
     *
     * @param codepoint 字符码点
     * @return 如果符合返回 {@code true}，否则 {@code false}
     */
    public boolean isMatch(int codepoint) {
        if (codepoint < 0 || codepoint >= ASCII.length) {
            return false;
        }
        int type = 1 << ordinal();
        return (ASCII[codepoint] & type) == type;
    }
}
