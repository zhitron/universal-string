package com.github.zhitron.universal;


import org.junit.Test;

import static org.junit.Assert.*;

public class StringPathTest {

    //region 创建 SecurityPath 实例

    @Test
    public void testCreateSecurityPathWithRoot() {
        StringPath path = StringPath.of("user", "documents", "file.txt");
        assertEquals("user/documents/file.txt", path.toString());
    }

    @Test
    public void testCreateSecurityPathWithAbsolutePath() {
        StringPath path = StringPath.of("/user", "documents", "file.txt");
        assertEquals("/user/documents/file.txt", path.toString());
    }

    //endregion

    //region 获取父路径

    @Test
    public void testGetParent() {
        StringPath path = StringPath.of("user", "documents", "file.txt");
        System.out.println(path);
        StringPath parent = path.getParent();
        assertEquals("user/documents", parent.toString());
    }

    //endregion

    //region 获取特定索引处的路径段

    @Test
    public void testGetSegment() {
        StringPath path = StringPath.of("user", "documents", "file.txt");
        assertEquals("user", path.getSegment(0));
        assertEquals("file.txt", path.getSegment(2));
    }

    //endregion

    //region 获取路径段的数量

    @Test
    public void testGetSegmentCount() {
        StringPath path = StringPath.of("user", "documents", "file.txt");
        assertEquals(3, path.getSegmentCount());
    }

    //endregion

    //region 获取文件名、文件基础名和扩展名

    @Test
    public void testGetFileName() {
        StringPath path = StringPath.of("user", "documents", "file.txt");
        assertEquals("file.txt", path.getFileName());
    }

    @Test
    public void testGetFileBaseName() {
        StringPath path = StringPath.of("user", "documents", "file.txt");
        assertEquals("file", path.getFileBaseName());
    }

    @Test
    public void testGetFileExtension() {
        StringPath path = StringPath.of("user", "documents", "file.txt");
        assertEquals("txt", path.getFileExtension());
    }

    //endregion

    //region 判断路径是否以另一个路径开始或结束

    @Test
    public void testStartsWith_SecurityPath() {
        StringPath path = StringPath.of("user", "documents", "file.txt");
        StringPath otherPath = StringPath.of("user", "documents");
        assertTrue(path.startsWith(otherPath));
    }

    @Test
    public void testStartsWith_String() {
        StringPath path = StringPath.of("user", "documents", "file.txt");
        assertTrue(path.startsWith("user"));
    }

    @Test
    public void testEndsWith_SecurityPath() {
        StringPath path = StringPath.of("user", "documents", "file.txt");
        StringPath otherPath = StringPath.of("file.txt");
        assertTrue(path.endsWith(otherPath));
    }

    @Test
    public void testEndsWith_String() {
        StringPath path = StringPath.of("user", "documents", "file.txt");
        assertTrue(path.endsWith("file.txt"));
    }

    //endregion

    //region 构建相对路径

    @Test
    public void testRelativize() {
        StringPath basePath = StringPath.of("user", "documents");
        StringPath targetPath = StringPath.of("user", "documents", "file.txt");
        StringPath relativePath = basePath.relativize(targetPath);
        assertEquals("file.txt", relativePath.toString());
    }

    @Test
    public void testRelativize_WithDifferentPaths() {
        StringPath basePath = StringPath.of("a", "b", "c");
        StringPath targetPath = StringPath.of("a", "x", "y");

        StringPath relativePath = basePath.relativize(targetPath);
        assertEquals("../../x/y", relativePath.toString());
    }

    //endregion

    //region 解析路径

    @Test
    public void testResolve() {
        StringPath basePath = StringPath.of("user", "documents");
        StringPath resolvedPath = basePath.resolve("file.txt");
        assertEquals("user/documents/file.txt", resolvedPath.toString());
    }

    @Test
    public void testResolve_AbsolutePath() {
        StringPath basePath = StringPath.of("user", "documents");
        StringPath resolvedPath = basePath.resolve("/absolute/path/file.txt");
        assertEquals("/absolute/path/file.txt", resolvedPath.toString());
    }

    //endregion

    //region 合并路径

    @Test
    public void testConcat() {
        StringPath basePath = StringPath.of("/user", "documents");
        StringPath newPath = basePath.concat("file.txt");
        assertEquals("/user/documents/file.txt", newPath.toString());
    }

    //endregion

    //region 测试 equals 和 hashCode 方法

    @Test
    public void testEqualsAndHashCode() {
        StringPath path1 = StringPath.of("user", "documents", "file.txt");
        StringPath path2 = StringPath.of("user", "documents", "file.txt");
        StringPath path3 = StringPath.of("user", "documents", "another_file.txt");

        assertEquals(path1, path2);
        assertNotEquals(path1, path3);

        assertEquals(path1.hashCode(), path2.hashCode());
        assertNotEquals(path1.hashCode(), path3.hashCode());
    }

    @Test
    public void testEquals_IgnoreCase() {
        StringPath path1 = StringPath.of("User", "Documents");
        StringPath path2 = StringPath.of("user", "documents");

        assertTrue(path1.equalsIgnoreCase(path2));
    }

    //endregion

    //region 边界情况测试

    @Test
    public void testEmptyPath() {
        StringPath path = StringPath.of("");
        assertEquals("", path.toString());
    }

    @Test
    public void testSingleDotInPath() {
        StringPath path = StringPath.of("user", ".", "documents");
        assertEquals("user/documents", path.toString());
    }

    @Test
    public void testDoubleDotInPath() {
        StringPath path = StringPath.of("user", "..", "other", "file.txt");
        assertEquals("user/other/file.txt", path.toString());
    }

    @Test
    public void testMultipleSlashes() {
        StringPath path = StringPath.of("user//documents\\\\file.txt");
        assertEquals("user/documents/file.txt", path.toString());
    }

    //endregion

    //region 异常测试

    @Test
    public void testInvalidCharacters() {
        assertThrows(IllegalArgumentException.class, () -> StringPath.of("user<invalid>"));
        assertThrows(IllegalArgumentException.class, () -> StringPath.of("user|path"));
    }

    @Test
    public void testAllDotsInSegment() {
        assertThrows(IllegalArgumentException.class, () -> StringPath.of("...."));
    }

    //endregion
}
