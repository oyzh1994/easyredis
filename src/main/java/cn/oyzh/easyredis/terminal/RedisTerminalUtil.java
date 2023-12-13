package cn.oyzh.easyredis.terminal;

import cn.hutool.core.collection.CollUtil;
import lombok.experimental.UtilityClass;
import redis.clients.jedis.GeoCoordinate;
import redis.clients.jedis.resps.StreamEntry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * redis终端工具
 *
 * @author oyzh
 * @since 2023/7/26
 */
@UtilityClass
public class RedisTerminalUtil {

    /**
     * 格式化输出
     *
     * @param value 值
     * @return 结果
     */
    public static String formatOut(Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof Boolean b) {
            return "\"" + (b ? "1" : "0") + "\"";
        }
        return "\"" + value + "\"";
    }

    /**
     * 格式化输出
     *
     * @param values 值
     * @return 结果
     */
    public static String formatOut(Collection<?> values) {
        if (CollUtil.isEmpty(values)) {
            return "";
        }
        int index = 1;
        StringBuilder builder = new StringBuilder();
        for (Object value : values) {
            builder.append(index++).append(") ").append("\"").append(value).append("\"").append("\n");
        }
        return builder.toString();
    }

    /**
     * 格式化输出
     *
     * @param values 值
     * @return 结果
     */
    public static String formatOut(Map<?, ?> values) {
        if (CollUtil.isEmpty(values)) {
            return "";
        }
        List<Object> list = new ArrayList<>();
        for (Map.Entry<?, ?> entry : values.entrySet()) {
            list.add(entry.getKey());
            list.add(entry.getValue());
        }
        return formatOut(list);
    }

    /**
     * 格式化输出
     *
     * @param values 值
     * @return 结果
     */
    public static String formatOutStream(List<StreamEntry> values) {
        if (CollUtil.isEmpty(values)) {
            return "";
        }
        int index = 1;
        StringBuilder builder = new StringBuilder();
        for (Object value : values) {
            builder.append(index++).append(") ").append("\"").append(value).append("\"").append("\n");
        }
        return builder.toString();
    }

    /**
     * 格式化输出
     *
     * @param coordinates 坐标值
     * @return 结果
     */
    public static String formatOut(List<GeoCoordinate> coordinates) {
        if (CollUtil.isEmpty(coordinates)) {
            return "";
        }
        int index = 1;
        StringBuilder builder = new StringBuilder();
        for (GeoCoordinate value : coordinates) {
            builder.append(index++).append(") 1)")
                    .append("\"").append(value.getLongitude()).append("\"").append("\n")
                    .append(" ".repeat(index / 10)).append("   2)")
                    .append("\"").append(value.getLatitude()).append("\"").append("\n");
        }
        return builder.toString();
    }
}
