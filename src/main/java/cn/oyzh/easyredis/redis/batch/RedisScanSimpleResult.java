package cn.oyzh.easyredis.redis.batch;

import cn.hutool.core.collection.CollUtil;
import cn.oyzh.easyredis.redis.key.RedisKey;
import lombok.Data;
import redis.clients.jedis.params.ScanParams;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * redis 扫描结果
 *
 * @author oyzh
 * @since 2023/6/28
 */
@Data
public class RedisScanSimpleResult {

    /**
     * 光标
     */
    private String cursor;

    /**
     * 数据
     */
    private List<String> keys;

    public boolean isFinish() {
        return Objects.equals(this.cursor, ScanParams.SCAN_POINTER_START) || CollUtil.isEmpty(this.keys);
    }

    public int keySize() {
        return this.keys == null ? 0 : this.keys.size();
    }
}
