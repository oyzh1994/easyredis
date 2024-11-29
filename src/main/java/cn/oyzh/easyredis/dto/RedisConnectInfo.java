package cn.oyzh.easyredis.dto;

import lombok.Data;
import lombok.Setter;

/**
 * redis连接
 *
 * @author oyzh
 * @since 2023/8/10
 */
@Data
public class RedisConnectInfo {

    /**
     * 原始输入内容
     */
    @Setter
    private String input;

    /**
     * 地址
     */
    private String host = "127.0.0.1";

    /**
     * 端口
     */
    private int port = 6379;

    /**
     * 超时时间
     */
    private int timeout = 3000;

    /**
     * 用户
     */
    private String user;

    /**
     * 密码
     */
    private String password;

    /**
     * db索引
     */
    private int db = 0;

    /**
     * 只读模式
     */
    private boolean readonly;
}
