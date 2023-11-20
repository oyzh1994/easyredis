package cn.oyzh.easyredis.redis;

import cn.hutool.core.collection.CollUtil;
import lombok.Data;
import redis.clients.jedis.params.ScanParams;

import java.util.List;
import java.util.Objects;

/**
 * redis 扫描结果
 *
 * @author oyzh
 * @since 2023/6/28
 */
@Data
public class RedisScanResult {

    /**
     * 光标
     */
    private String cursor;

    /**
     * 数据
     */
    private List<RedisKey> keys;

    public boolean isFinish() {
        return Objects.equals(this.cursor, ScanParams.SCAN_POINTER_START) || CollUtil.isEmpty(this.keys);
    }
}
