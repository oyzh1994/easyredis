package cn.oyzh.easyredis.store;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.StaticLog;
import cn.oyzh.easyredis.RedisConst;
import cn.oyzh.easyredis.domain.RedisKeyFilterHistory;
import cn.oyzh.fx.common.dto.Paging;
import cn.oyzh.fx.common.store.ArrayFileStore;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * redis过滤历史存储
 *
 * @author oyzh
 * @since 2023/07/19
 */
//@Slf4j
public class RedisKeyFilterHistoryStore extends ArrayFileStore<RedisKeyFilterHistory> {

    /**
     * 最大历史数量
     */
    public static int His_Max_Size = 50;

    /**
     * 当前实例
     */
    public static final RedisKeyFilterHistoryStore INSTANCE = new RedisKeyFilterHistoryStore();

    {
        this.filePath(RedisConst.STORE_PATH + "redis_key_filter_history.json");
        JulLog.info("RedisKeyFilterHistoryStore filePath:{} charset:{} init {}.", this.filePath(), this.charset(), super.init() ? "success" : "fail");
    }

    @Override
    public synchronized List<RedisKeyFilterHistory> load() {
        String text = FileUtil.readString(this.storeFile(), this.charset());
        if (StrUtil.isBlank(text)) {
            return new ArrayList<>();
        }
        return JSONUtil.toList(text, RedisKeyFilterHistory.class);
    }

    /**
     * 获取过滤模式列表
     *
     * @return 过滤模式列表
     */
    public synchronized List<String> getPatterns() {
        return this.load().parallelStream().map(RedisKeyFilterHistory::getPattern).collect(Collectors.toList());
    }

    @Override
    public synchronized boolean add(@NonNull RedisKeyFilterHistory history) {
        try {
            // 历史列表
            List<RedisKeyFilterHistory> histories = this.load();
            // 过滤出当前类型
            List<RedisKeyFilterHistory> hisList = histories.parallelStream().collect(Collectors.toList());
            // 最新的数据是当前数据，则无需添加
            if (history.compare(CollUtil.getLast(hisList))) {
                return true;
            }
            // 移除当前添加内容
            histories.removeIf(h -> h.compare(history));
            // 添加到集合
            histories.add(history);
            // 对超出限制的数据，进行删除
            int limit = hisList.size() - His_Max_Size + 1;
            if (limit > 0) {
                List<RedisKeyFilterHistory> delList = hisList.parallelStream().limit(limit).toList();
                histories.removeAll(delList);
            }
            // 保存数据
            return this.save(histories);
        } catch (Exception e) {
            JulLog.warn("add error,err:{}", e.getMessage());
        }
        return false;
    }

    /**
     * 添加过滤历史
     *
     * @param kw 关键词
     * @return 结果
     */
    public boolean addHistory(@NonNull String kw) {
        return this.add(new RedisKeyFilterHistory(kw));
    }

    @Override
    public synchronized boolean update(@NonNull RedisKeyFilterHistory history) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean delete(@NonNull RedisKeyFilterHistory history) {
        try {
            List<RedisKeyFilterHistory> histories = this.load();
            if (histories.removeIf(h -> h.compare(history))) {
                return this.save(histories);
            }
        } catch (Exception e) {
            JulLog.warn("delete error,err:{}", e.getMessage());
        }
        return false;
    }

    @Override
    public Paging<RedisKeyFilterHistory> getPage(int limit, Map<String, Object> params) {
        throw new UnsupportedOperationException();
    }
}
