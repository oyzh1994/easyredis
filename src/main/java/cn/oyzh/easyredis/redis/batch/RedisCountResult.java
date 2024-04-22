package cn.oyzh.easyredis.redis.batch;

import lombok.Data;
import redis.clients.jedis.params.ScanParams;

import java.util.Objects;

/**
 * redis 扫描结果
 *
 * @author oyzh
 * @since 2023/6/28
 */
@Data
public class RedisCountResult {

    /**
     * 光标
     */
    private String cursor;

    /**
     * 数据
     */
    private Integer count;

    public boolean isFinish() {
        return Objects.equals(this.cursor, ScanParams.SCAN_POINTER_START) || count == null || count == 0;
    }
}
