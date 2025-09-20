package com.github.zhitron.universal;

import java.util.function.IntBinaryOperator;
import java.util.function.IntConsumer;
import java.util.function.IntUnaryOperator;
import java.util.stream.IntStream;

/**
 * 索引范围类，用于表示一个左闭右开的整数区间[startInclusive, endExclusive)
 *
 * @author zhitron
 */
public class IndexBounds {

    /**
     * 起始索引（包含）
     */
    private final int startInclusive;

    /**
     * 结束索引（不包含）
     */
    private final int endExclusive;

    /**
     * 构造一个索引范围对象
     *
     * @param startInclusive 起始索引（包含）
     * @param endExclusive   结束索引（不包含）
     */
    public IndexBounds(int startInclusive, int endExclusive) {
        this.startInclusive = startInclusive;
        this.endExclusive = endExclusive;
    }

    /**
     * 获取起始索引（包含）
     *
     * @return 起始索引值
     */
    public int getStartInclusive() {
        return startInclusive;
    }

    /**
     * 获取结束索引（不包含）
     *
     * @return 结束索引值
     */
    public int getEndExclusive() {
        return endExclusive;
    }

    /**
     * 获取结束索引（不包含）
     *
     * @return 结束索引值
     */
    public int getEndInclusive() {
        return endExclusive - 1;
    }

    /**
     * 计算范围的长度
     *
     * @return 范围的长度，如果范围无效则返回0
     */
    public int length() {
        // 计算范围长度，确保不为负数
        return Math.max(endExclusive - startInclusive, 0);
    }


    /**
     * 判断范围是否为空
     *
     * @return 当起始位置大于等于结束位置时返回true，表示范围为空；否则返回false
     */
    public boolean isEmpty() {
        return startInclusive >= endExclusive;
    }


    /**
     * 创建一个从起始值（包含）到结束值（不包含）的整数流
     *
     * @return 返回一个IntStream对象，包含从startInclusive到endExclusive-1的所有整数
     */
    public IntStream each() {
        return IntStream.range(startInclusive, endExclusive);
    }

    /**
     * 对指定范围内的每个整数执行给定的操作
     *
     * @param consumer 要对每个整数执行的操作，不能为null
     */
    public void each(IntConsumer consumer) {
        // 遍历从startInclusive到endExclusive-1的每个整数
        for (int i = startInclusive; i < endExclusive; i++) {
            consumer.accept(i);
        }
    }

    /**
     * 对指定范围内的每个整数执行给定的操作
     *
     * @param skipper 要对每个整数应用的跳步操作，不能为null
     */
    public void each(IntUnaryOperator skipper) {
        // 遍历从startInclusive到endExclusive-1的每个整数
        for (int i = startInclusive; i < endExclusive; ) {
            i += skipper.applyAsInt(i);
        }
    }

    /**
     * 对指定范围内的每个整数执行给定的操作
     *
     * @param initialIndex 初始索引值，用于传递给skipper操作
     * @param skipper      跳步操作函数，接受当前整数和索引值，返回需要跳过的步长
     */
    public void each(int initialIndex, IntBinaryOperator skipper) {
        // 遍历从startInclusive到endExclusive-1的每个整数
        for (int current = startInclusive, index = initialIndex; current < endExclusive; ) {
            int skip = skipper.applyAsInt(current, index);
            index += skip;
            current += skip;
        }
    }

    /**
     * 返回索引范围的字符串表示形式
     * 格式为：[startInclusive , endExclusive]
     *
     * @return 索引范围的字符串表示
     */
    @Override
    public String toString() {
        return '[' + startInclusive + " , " + endExclusive + ']';
    }

}
