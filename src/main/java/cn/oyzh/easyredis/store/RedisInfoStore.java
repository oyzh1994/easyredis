package cn.oyzh.easyredis.store;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyredis.RedisConst;
import cn.oyzh.easyredis.domain.RedisInfo;
import cn.oyzh.fx.common.dto.Paging;
import cn.oyzh.fx.common.store.ArrayFileStore;
import com.alibaba.fastjson.JSON;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * redis信息存储
 *
 * @author oyzh
 * @since 2023/6/23
 */
@Slf4j
public class RedisInfoStore extends ArrayFileStore<RedisInfo> {

    /**
     * 当前实例
     */
    public static final RedisInfoStore INSTANCE = new RedisInfoStore();

    /**
     * 已加载的redis键
     */
    private final List<RedisInfo> redisInfos;

    {
        this.filePath(RedisConst.STORE_PATH + "redis_info.json");
        log.info("RedisInfoStore filePath:{} charset:{} init {}.", this.filePath(), this.charset(), super.init() ? "success" : "fail");
        this.redisInfos = this.load();
        for (RedisInfo RedisInfo : this.redisInfos) {
            if (StrUtil.isBlank(RedisInfo.getId())) {
                RedisInfo.setId(UUID.fastUUID().toString(true));
                this.update(RedisInfo);
            }
        }
    }

    @Override
    public synchronized List<RedisInfo> load() {
        if (this.redisInfos == null) {
            String text = FileUtil.readString(this.storeFile(), this.charset());
            if (StrUtil.isBlank(text)) {
                return new ArrayList<>();
            }
            List<RedisInfo> redisInfos = JSON.parseArray(text, RedisInfo.class);
            if (CollUtil.isNotEmpty(redisInfos)) {
                redisInfos = redisInfos.parallelStream().sorted().collect(Collectors.toList());
            }
            return redisInfos;
        }
        return this.redisInfos;
    }

    @Override
    public synchronized boolean add(@NonNull RedisInfo redisInfo) {
        try {
            if (!this.redisInfos.contains(redisInfo)) {
                redisInfo.setId(UUID.fastUUID().toString(true));
                // 添加到集合
                this.redisInfos.add(redisInfo);
                // 更新数据
                return this.save(this.redisInfos);
            }
        } catch (Exception e) {
            log.warn("add error,err:{}", e.getMessage());
        }
        return false;
    }

    @Override
    public synchronized boolean update(@NonNull RedisInfo redisInfo) {
        try {
            // 更新数据
            if (this.redisInfos.contains(redisInfo)) {
                return this.save(this.redisInfos);
            }
        } catch (Exception e) {
            log.warn("update error,err:{}", e.getMessage());
        }
        return false;
    }

    @Override
    public synchronized boolean delete(@NonNull RedisInfo redisInfo) {
        try {
            // 删除数据
            if (this.redisInfos.remove(redisInfo)) {
                return this.save(this.redisInfos);
            }
        } catch (Exception e) {
            log.warn("delete error,err:{}", e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public Paging<RedisInfo> getPage(int limit, Map<String, Object> params) {
        // 加载数据
        List<RedisInfo> redisInfos = this.load();
        // 分页对象
        Paging<RedisInfo> paging = new Paging<>(redisInfos, limit);
        // 数据为空
        if (CollUtil.isNotEmpty(redisInfos)) {
            String searchKeyWord = params == null ? null : (String) params.get("searchKeyWord");
            // 过滤数据
            if (StrUtil.isNotBlank(searchKeyWord)) {
                final String kw = searchKeyWord.toLowerCase().trim();
                redisInfos = redisInfos.parallelStream().filter(z ->
                        z.getHost() != null && z.getHost().contains(kw)
                                || z.getName() != null && z.getName().toLowerCase().contains(kw)
                                || z.getRemark() != null && z.getRemark().toLowerCase().contains(kw)
                ).collect(Collectors.toList());
            }
            // 对数据排序
            redisInfos = redisInfos.parallelStream().sorted().collect(Collectors.toList());
            // 添加到分页数据
            paging.dataList(redisInfos);
        }
        return paging;
    }

    /**
     * 是否存在此redis信息
     *
     * @param RedisInfo redis信息
     * @return 结果
     */
    public boolean exist(RedisInfo RedisInfo) {
        if (RedisInfo == null) {
            return false;
        }
        for (RedisInfo info : this.redisInfos) {
            if (info.compareTo(RedisInfo) == 0 && info != RedisInfo) {
                return true;
            }
        }
        return false;
    }
}
