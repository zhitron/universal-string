package com.github.zhitron.universal;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 表示一个通用的系统无关路径。
 *
 * <p>该类用于统一处理文件或资源路径，屏蔽操作系统差异。</p>
 *
 * <p><strong>线程安全性：</strong> 该类是不可变对象，因此是线程安全的。</p>
 *
 * @author zhitron
 */
public class StringPath {
    /**
     * 空字符串数组常量，用于表示空路径段。
     */
    private static final String[] EMPTY_STRING_ARRAY = new String[0];
    /**
     * 空路径常量，表示不以斜杠开头的空路径。
     */
    private static final StringPath EMPTY_STRING_PATH = new StringPath(false, EMPTY_STRING_ARRAY);
    /**
     * 根路径常量，表示以斜杠开头的空路径。
     */
    private static final StringPath ROOT_STRING_PATH = new StringPath(true, EMPTY_STRING_ARRAY);
    /**
     * 空根对象常量，用于表示没有根路径。
     */
    private static final Object EMPTY_ROOT = null;
    /**
     * 存储路径的各个段。每个段都是非空且不包含路径分隔符的字符串。
     */
    private final String[] segments;
    /**
     * 指示路径是否以斜杠开头（即绝对路径）。
     */
    private final boolean startsWithSlash;

    /**
     * 构造函数，创建一个新的SecurityPath实例。
     *
     * @param startsWithSlash 指示路径是否以斜杠开头
     * @param segments        路径段数组
     */
    protected StringPath(boolean startsWithSlash, String[] segments) {
        this.startsWithSlash = startsWithSlash;
        this.segments = segments;
    }

    /**
     * 构建路径字符串数组
     *
     * @param root      根路径对象，可以是 null、String 或 SecurityPath 类型
     * @param pathsList 可变长度的路径字符串数组
     * @return 返回构建好的路径字符串数组
     */
    private static StringPath build(Object root, String[]... pathsList) {
        // 如果 root 和 paths 都为空，则返回空数组
        if (root == null && (pathsList == null || pathsList.length == 0)) {
            return EMPTY_STRING_PATH;
        }
        List<String> list = new ArrayList<>();
        String str;
        Boolean startsWithSlash = null;
        // 处理 root 参数的不同类型
        if (root == null) {
            str = null; // root 为 null 时，str 设为 null
        } else if (root instanceof String) {
            str = (String) root; // root 为 String 类型时，直接赋值给 str
        } else if (root instanceof StringPath) {
            str = null;
            startsWithSlash = ((StringPath) root).startsWithSlash;
            Collections.addAll(list, ((StringPath) root).segments);
        } else if (root instanceof File) {
            str = root.toString(); // root 为 String 类型时，直接赋值给 str
        } else if (root instanceof Path) {
            str = root.toString(); // root 为 String 类型时，直接赋值给 str
        } else {
            throw new IllegalArgumentException("Unsupported root type: " + root.getClass());
        }
        char ch, chl;
        int rootLimit = -1, colon = 0, colonLimit = 2, codePoint;
        // 循环处理路径
        for (int ci = -1, pi = -1, si, ei, sc = 0, ec = 0, dc = 0; ci < pathsList.length; ) {
            if (str != null) {
                // 遍历字符串中的每个字符
                int strLen = str.length();
                for (si = 0, ei = 0; ei < strLen; ei++) {
                    ch = str.charAt(ei);
                    codePoint = ch;
                    // 处理 Unicode 高低位代理字符
                    if (Character.isHighSurrogate(ch) && ei + 1 < strLen) {
                        chl = str.charAt(ei + 1);
                        if (Character.isLowSurrogate(chl)) {
                            codePoint = Character.toCodePoint(ch, chl);
                            ei++;
                        }
                    }
                    switch (codePoint) {
                        case ':': // 冒号检查
                            if (list.size() <= colonLimit) {
                                colon++;
                                dc++;
                                break;
                            }
                        case '*': // 不允许的特殊字符
                        case '?':
                        case '"':
                        case '<':
                        case '>':
                        case '|':
                            throw new IllegalArgumentException("Invalid character in path: " + ch);
                        case '/': // 路径分隔符处理
                        case '\\':
                            if (dc != 0) {
                                StringPath.build(list, rootLimit, str, si + sc, ei - ec);
                            }
                            if (startsWithSlash == null) {
                                startsWithSlash = list.isEmpty();
                            }
                            sc = 0;
                            ec = 0;
                            dc = 0;
                            colon = 0;
                            si = ei + 1;
                            break;
                        default:
                            if (Character.isWhitespace(codePoint)) {
                                if (dc == 0) {
                                    sc++;
                                } else {
                                    ec++;
                                }
                            } else {
                                // 检查非法控制字符或冒号限制
                                if (Character.isISOControl(codePoint) || (list.size() > colonLimit && colon != 0)) {
                                    throw new IllegalArgumentException("Invalid control or colon-restricted character");
                                }
                                if (Character.isHighSurrogate(ch)) dc++;
                                dc += ec + 1;
                                ec = 0;
                            }
                    }
                }
                // 如果仍有未处理的字符，继续构建路径
                if (dc != 0) {
                    StringPath.build(list, rootLimit, str, si + sc, ei - ec);
                }
            }
            // 设置根路径限制
            if (rootLimit < 0) rootLimit = list.size();
            if (ci == -1 || pi >= pathsList[ci].length) {
                ci++;
                pi = 0;
            } else {
                pi++;
            }
            // 获取下一个路径段
            str = ci < pathsList.length && pi < pathsList[ci].length ? pathsList[ci][pi].toString() : null;
        }
        // 返回最终构建的路径数组
        return new StringPath(startsWithSlash != null && startsWithSlash, list.toArray(new String[0]));
    }

    /**
     * 将给定的字符串片段解析并添加到路径段列表中，处理特殊符号如 "." 和 ".."
     *
     * @param list      存储路径段的列表
     * @param rootLimit 根路径段的数量，用于控制回退操作的边界
     * @param string    原始路径字符串
     * @param s         起始索引（包含）
     * @param e         结束索引（不包含）
     */
    private static void build(List<String> list, int rootLimit, String string, int s, int e) {
        switch (e - s) {
            case 0:
                // 空片段，不添加任何内容
                break;
            case 1:
                // 单个字符，检查是否为 "."
                if (string.charAt(s) != '.') {
                    list.add(string.substring(s, e));
                }
                break;
            case 2:
                // 两个字符，检查是否为 ".."
                if (string.charAt(s) == '.' && string.charAt(s + 1) == '.') {
                    // 回退操作：移除最后一个路径段（如果不在根路径范围内）
                    if (list.size() > rootLimit) {
                        list.remove(list.size() - 1);
                    }
                } else {
                    list.add(string.substring(s, e));
                }
                break;
            default:
                // 检查整个片段是否全为点（例如 "...", "...." 等）
                boolean all = true;
                for (int i = s; i < e; i++) {
                    if (string.charAt(i) != '.') {
                        all = false;
                        break;
                    }
                }
                if (all) {
                    // 全部是点的情况视为非法路径
                    throw new IllegalArgumentException("Invalid path segment: all characters are dots");
                }
                list.add(string.substring(s, e));
                break;
        }
    }

    /**
     * 创建一个新的SecurityPath实例，该实例基于根路径和一组子路径
     * 此方法主要用于组合根路径和额外的子路径，以构建一个新的SecurityPath对象
     * 它会根据根路径是否以斜杠开头来设置新路径的此属性，并将根路径和子路径结合起来
     *
     * @param root  根路径，用于构建新路径的基础
     * @param paths 一个或多个子路径，将被追加到根路径之后
     * @return 返回一个新的SecurityPath实例，包含根路径和子路径的组合
     */
    public static StringPath of(Object root, String... paths) {
        return StringPath.build(root, paths);
    }

    /**
     * 获取当前路径的父路径。
     *
     * <p>如果当前路径没有段（segments），则返回 null。</p>
     *
     * @return 返回当前路径的父路径，如果没有父路径，则返回 null
     */
    public StringPath getParent() {
        if (segments.length == 0) {
            return null;
        }
        return new StringPath(startsWithSlash, Arrays.copyOfRange(segments, 0, segments.length - 1));
    }

    /**
     * 获取指定索引处的路径段。
     *
     * <p>索引必须在有效范围内，否则将抛出 {@link IndexOutOfBoundsException}。</p>
     *
     * @param index 要获取的路径段的索引
     * @return 指定索引处的路径段
     * @throws IndexOutOfBoundsException 如果索引超出范围
     */
    public String getSegment(int index) {
        if (index < 0 || index >= segments.length) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + segments.length);
        }
        return segments[index];
    }

    /**
     * 获取路径中包含的段数。
     *
     * @return 返回路径中的段数
     */
    public int getSegmentCount() {
        return segments.length;
    }

    /**
     * 获取路径中的最后一个段，通常表示文件名。
     *
     * <p>如果路径没有段，则返回 null。</p>
     *
     * @return 返回路径中的最后一个段，如果没有段，则返回 null
     */
    public String getFileName() {
        if (segments.length == 0) {
            return null;
        }
        return segments[segments.length - 1];
    }

    /**
     * 获取文件的基本名称（不包括扩展名）。
     *
     * <p>如果路径没有段，则返回 null。</p>
     *
     * <p>如果文件名中没有扩展名，则返回完整的文件名。</p>
     *
     * @return 返回文件的基本名称，如果没有段，则返回 null
     */
    public String getFileBaseName() {
        if (segments.length == 0) {
            return null;
        }
        String fileName = segments[segments.length - 1];
        int dotIndex = segments[segments.length - 1].lastIndexOf('.');
        return dotIndex > 0 ? fileName.substring(0, dotIndex) : fileName;
    }

    /**
     * 获取文件的扩展名。
     *
     * <p>如果路径没有段，则返回 null。</p>
     *
     * <p>如果没有扩展名，则返回空字符串。</p>
     *
     * @return 返回文件的扩展名，如果没有段或没有扩展名，则返回空字符串
     */
    public String getFileExtension() {
        if (segments.length == 0) {
            return null;
        }
        String fileName = segments[segments.length - 1];
        int dotIndex = fileName.lastIndexOf('.');
        return dotIndex > 0 ? fileName.substring(dotIndex + 1) : "";
    }

    /**
     * 创建并返回从指定开始索引（包含）到结束索引（不包含）的子路径。
     *
     * <p>如果索引无效，将抛出 {@link IndexOutOfBoundsException}。</p>
     *
     * @param beginIndex 子路径的起始索引（包含）
     * @param endIndex   子路径的结束索引（不包含）
     * @return 返回新的 SecurityPath 实例，表示指定范围内的子路径
     * @throws IndexOutOfBoundsException 如果索引无效
     */
    public StringPath sub(int beginIndex, int endIndex) {
        if (beginIndex < 0 || endIndex > segments.length || beginIndex > endIndex) {
            throw new IndexOutOfBoundsException("Invalid indices: begin=" + beginIndex + ", end=" + endIndex);
        }
        String[] subSegments = new String[endIndex - beginIndex];
        System.arraycopy(segments, beginIndex, subSegments, 0, subSegments.length);
        if (beginIndex > 0) {
            return new StringPath(false, subSegments);
        } else {
            return new StringPath(startsWithSlash, subSegments);
        }
    }

    /**
     * 构造一个相对路径，使得通过该相对路径可以从当前路径到达指定的其他路径。
     *
     * <p>如果目标路径是当前路径的子路径，则返回相对路径；否则返回回溯路径。</p>
     *
     * @param other 目标路径
     * @return 返回构造的相对路径
     */
    public StringPath relativize(StringPath other) {
        if (other == null) {
            return this;
        }
        // 如果当前路径以 other 路径为前缀，则返回剩余部分
        String[] ta = this.segments, oa = other.segments;
        if (other.startsWith(this)) {
            return other.sub(ta.length, oa.length);
        }
        // 寻找共同的父路径
        int minLen = Math.min(oa.length, ta.length), i = 0;
        while (i < minLen && oa[i].equals(ta[i])) i++;
        // 构建相对路径：从当前路径到公共父路径，再向上回溯至 other 的路径
        String[] relativeSegments = new String[oa.length - i + (ta.length - i)];
        // 添加回溯部分（即 " .. " 段）
        for (int j = 0; j < ta.length - i; j++) {
            relativeSegments[j] = "..";
        }
        // 添加当前路径中非公共部分
        System.arraycopy(oa, i, relativeSegments, ta.length - i, oa.length - i);
        return new StringPath(false, relativeSegments);
    }

    /**
     * 将字符串形式的目标路径转换为 SecurityPath 并构造相对路径。
     *
     * @param securityPath 字符串形式的目标路径
     * @return 返回构造的相对路径
     */
    public StringPath relativize(String securityPath) {
        return relativize(StringPath.of(securityPath));
    }

    /**
     * 解析另一个路径，并返回解析后的路径。
     *
     * <p>如果另一个路径以斜杠开头，则直接返回该路径。</p>
     *
     * @param securityPath 另一个路径
     * @return 返回解析后的路径
     */
    public StringPath resolve(StringPath securityPath) {
        if (securityPath.startsWithSlash) {
            return securityPath;
        }
        return StringPath.of(this, securityPath.segments);
    }

    /**
     * 将字符串形式的路径解析为 SecurityPath 并返回解析后的路径。
     *
     * @param securityPath 字符串形式的路径
     * @return 返回解析后的路径
     */
    public StringPath resolve(String securityPath) {
        return resolve(StringPath.of(securityPath));
    }

    /**
     * 连接另一个路径，并返回连接后的路径。
     *
     * @param securityPath 另一个路径
     * @return 返回连接后的路径
     */
    public StringPath concat(StringPath securityPath) {
        return StringPath.build(this, securityPath.segments);
    }

    /**
     * 连接一个字符串形式的路径，并返回连接后的路径。
     *
     * @param securityPath 字符串形式的路径
     * @return 返回连接后的路径
     */
    public StringPath concat(String securityPath) {
        return StringPath.build(this, new String[]{securityPath});
    }

    /**
     * 检查当前路径是否以另一个SecurityPath实例的段开始
     * 这个方法用于确定当前路径是否在给定的路径之后，基于路径段的比较
     *
     * @param other SecurityPath实例，其段将与当前实例的段进行比较
     * @return 如果当前路径以另一个路径的段开始，则返回true；否则返回false
     */
    public boolean startsWith(StringPath other) {
        // 分别获取当前路径和比较路径的段数组
        String[] ta, oa;
        // 检查比较路径是否为空，或者其段数组长度是否为0，或者比较路径的段数是否超过当前路径的段数
        if (other == null || (oa = other.segments).length == 0 || oa.length > (ta = this.segments).length) {
            return false;
        }
        // 遍历比较路径的段数组，与当前路径的相应段进行比较
        for (int i = 0; i < oa.length; i++) {
            // 如果任何一段不匹配，则当前路径不以比较路径开始
            if (!ta[i].equals(oa[i])) {
                return false;
            }
        }
        // 所有段都匹配，确认当前路径以比较路径开始
        return true;
    }

    /**
     * 判断当前路径是否以给定的字符串开头
     * 此方法重载了startsWith方法，允许传入一个字符串参数
     * 主要作用是提高代码的可读性和易用性，通过将字符串转换为SecurityPath对象来进行比较
     *
     * @param other 要比较的字符串，代表一个路径
     * @return 如果当前路径以给定的字符串开头，则返回true；否则返回false
     */
    public boolean startsWith(String other) {
        return startsWith(StringPath.of(other));
    }

    /**
     * 判断当前路径是否以指定的路径结尾
     * 此方法用于检查当前路径的末尾部分是否与给定的路径相同，主要用于安全路径的比较
     *
     * @param other SecurityPath对象，表示要比较的路径
     * @return 如果当前路径以指定的路径结尾，则返回true；否则返回false
     */
    public boolean endsWith(StringPath other) {
        // 初始化路径段数组
        String[] ta, oa;
        // 检查传入的路径是否为null，或者其段数是否为0，或者其段数是否大于当前路径的段数，任何一种情况都不可能以当前路径结尾
        if (other == null || (oa = other.segments).length == 0 || oa.length > (ta = this.segments).length) {
            return false;
        }
        // 从后向前遍历路径段，比较每个对应位置的路径段是否相同
        for (int i = 0; i < oa.length; i++) {
            // 如果在任何位置上路径段不相同，则当前路径不以指定的路径结尾
            if (!ta[ta.length - 1 - i].equals(oa[oa.length - 1 - i])) {
                return false;
            }
        }
        // 所有路径段比较都相同，当前路径以指定的路径结尾
        return true;
    }

    /**
     * 判断当前对象表示的路径是否以指定的路径结尾
     * 此方法重载了endsWith方法，允许接受一个字符串参数
     * 主要用途是提供一个更便捷的接口，使得开发者可以传入路径字符串直接进行比较，
     * 而无需先将字符串转换为SecurityPath对象
     *
     * @param other 要比较的路径字符串，代表另一个路径
     * @return 如果当前路径以指定的路径结尾，则返回true；否则返回false
     */
    public boolean endsWith(String other) {
        return endsWith(StringPath.of(other));
    }

    /**
     * 忽略大小写比较两个SecurityPath对象是否相等
     * 此方法重写了Object类的equals方法，专门用于SecurityPath对象的比较
     * 它首先比较对象引用是否相同，然后比较类类型，最后比较对象的segments数组
     * 忽略大小写的比较是针对segments数组的每个元素进行的
     *
     * @param o 要比较的对象
     * @return 如果两个SecurityPath对象在忽略大小写的情况下相等，则返回true；否则返回false
     */
    public boolean equalsIgnoreCase(Object o) {
        // 比较对象引用是否相同
        if (this == o) return true;
        // 检查对象是否为空或是否是相同的类类型
        if (o == null || getClass() != o.getClass()) return false;
        // 将对象转换为SecurityPath类型
        StringPath other = (StringPath) o;
        // 比较是否以斜杠开头
        if (other.startsWithSlash != startsWithSlash) {
            return false;
        }
        // 比较segments数组的长度
        if (segments.length != other.segments.length) {
            return false;
        }
        // 逆序比较segments数组的每个元素，忽略大小写
        for (int i = segments.length - 1; i >= 0; i--) {
            if (!segments[i].equalsIgnoreCase(other.segments[i])) {
                return false;
            }
        }
        // 如果所有比较都通过，则返回true
        return true;
    }

    /**
     * 判断当前SecurityPath对象与指定的对象是否相等。
     *
     * <p>该方法重写了Object类的equals方法，用于比较两个SecurityPath对象的内容是否一致，
     * 包括路径是否以斜杠开头以及各路径段字符串的值。</p>
     *
     * @param o 要比较的对象
     * @return 如果两个SecurityPath对象内容相同，则返回true；否则返回false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // 同一对象引用直接返回true
        if (o == null || getClass() != o.getClass()) return false; // 空对象或类型不匹配返回false
        StringPath other = (StringPath) o;
        if (other.startsWithSlash != startsWithSlash) { // 比较是否以斜杠开头
            return false;
        }
        if (segments.length != other.segments.length) { // 段数不同则肯定不相等
            return false;
        }
        // 逆序逐个比较每个路径段字符串是否完全相同
        for (int i = segments.length - 1; i >= 0; i--) {
            if (!segments[i].equals(other.segments[i])) {
                return false;
            }
        }
        return true; // 所有检查通过，视为相等
    }

    /**
     * 计算并返回当前SecurityPath对象的哈希码值。
     *
     * <p>该方法重写了Object类的hashCode方法，基于路径段字符串的内容计算哈希值，
     * 确保内容相同的SecurityPath对象具有相同的哈希码。</p>
     *
     * @return 返回当前SecurityPath对象的哈希码值
     */
    @Override
    public int hashCode() {
        int r = 0;
        for (String e : segments) { // 遍历每个路径段
            int h = 0;
            for (int i = 0; i < e.length(); i++) { // 遍历每个字符
                char ch = e.charAt(i);
                // 统一转为小写字符参与哈希计算，确保忽略大小写不影响一致性
                if (Character.isUpperCase(ch)) {
                    h = 31 * h + Character.toLowerCase(ch);
                } else {
                    h = 31 * h + ch;
                }
            }
            r = 31 * r + h; // 将每个路径段的哈希合并到总哈希中
        }
        return r;
    }

    /**
     * 返回当前SecurityPath对象的字符串表示形式。
     *
     * <p>该方法将路径转换为标准格式的字符串，便于调试和日志记录。
     * 格式为：[斜杠开头]/段1/段2/.../段n</p>
     *
     * @return 当前SecurityPath对象的字符串表示
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (startsWithSlash) { // 如果路径以斜杠开头，则添加根斜杠
            sb.append('/');
        }
        for (String segment : segments) { // 依次追加每个路径段，并以斜杠分隔
            sb.append(segment).append('/');
        }
        if (sb.length() != 0) { // 如果非空，移除末尾多余的斜杠
            sb.setLength(sb.length() - 1);
        }
        return sb.toString(); // 返回构建好的路径字符串
    }

    /**
     * 将当前对象转换为Path对象
     * <p>
     * 此方法用于创建一个代表文件系统路径的Path对象它使用当前对象的字符串表示作为路径
     * Path对象通常用于文件系统操作，如读取或写入文件
     *
     * @return Path对象，表示文件系统中的一个路径
     */
    public Path toPath() {
        return Paths.get(toString());
    }

    /**
     * 将当前对象转换为File对象
     * <p>
     * 此方法用于创建并返回一个表示当前对象的File对象这在需要将对象的字符串表示形式
     * 转换为文件对象时特别有用，例如，当对象的toString方法返回一个有效的文件路径时
     *
     * @return File 表示当前对象的File对象
     */
    public File toFile() {
        return new File(toString());
    }
}
