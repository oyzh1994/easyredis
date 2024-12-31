package cn.oyzh.easyredis.dto;

import lombok.Data;

/**
 * 客户端项目
 *
 * @author oyzh
 * @since 2023/8/1
 */
@Data
public class RedisClientItem {

    /**
     * 编号
     */
    private int index;

    /**
     * 地址
     */
    private String addr;

    /**
     * 标记
     */
    private String flags;

    /**
     * 当前db
     */
    private String db;

    /**
     * 存活时间
     */
    private String age;

    /**
     * 空闲时间
     */
    private String idle;

    public static RedisClientItem from(String l) {
        String[] arr = l.split(" ");
        RedisClientItem item = new RedisClientItem();
        for (String s : arr) {
            if (s.toLowerCase().startsWith("age")) {
                item.age = s.split("=")[1];
            } else if (s.toLowerCase().startsWith("addr")) {
                item.addr = s.split("=")[1];
            } else if (s.toLowerCase().startsWith("db")) {
                item.db = s.split("=")[1];
            } else if (s.toLowerCase().startsWith("flags")) {
                item.flags = s.split("=")[1];
            } else if (s.toLowerCase().startsWith("idle")) {
                item.idle = s.split("=")[1];
            }
        }
        return item;
    }
}
