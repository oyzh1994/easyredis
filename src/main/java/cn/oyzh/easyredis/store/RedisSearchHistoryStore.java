package cn.oyzh.easyredis.store;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.StaticLog;
import cn.oyzh.easyredis.RedisConst;
import cn.oyzh.easyredis.domain.RedisSearchHistory;
import cn.oyzh.fx.common.dto.Paging;
import cn.oyzh.fx.common.store.ArrayFileStore;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * redis搜索历史存储
 *
 * @author oyzh
 * @since 2023/06/16
 */
//@Slf4j
public class RedisSearchHistoryStore extends ArrayFileStore<RedisSearchHistory> {

    /**
     * 最大历史数量
     */
    public static int His_Max_Size = 50;

    /**
     * 当前实例
     */
    public static final RedisSearchHistoryStore INSTANCE = new RedisSearchHistoryStore();

    {
        this.filePath(RedisConst.STORE_PATH + "redis_search_history.json");
        StaticLog.info("RedisSearchHistoryStore filePath:{} charset:{} init {}.", this.filePath(), this.charset(), super.init() ? "success" : "fail");
    }

    @Override
    public synchronized List<RedisSearchHistory> load() {
        String text = FileUtil.readString(this.storeFile(), this.charset());
        if (StrUtil.isBlank(text)) {
            return new ArrayList<>();
        }
        return JSONUtil.toList(text, RedisSearchHistory.class);
    }

    /**
     * 获取搜索词
     *
     * @return 搜索词列表
     */
    public synchronized List<String> getSearchKw() {
        return this.load().parallelStream().filter(h -> Objects.equals(h.getType(), 1)).map(RedisSearchHistory::getKw).collect(Collectors.toList());
    }

    /**
     * 获取替换词
     *
     * @return 替换词列表
     */
    public synchronized List<String> getReplaceKw() {
        return this.load().parallelStream().filter(h -> Objects.equals(h.getType(), 2)).map(RedisSearchHistory::getKw).collect(Collectors.toList());
    }

    @Override
    public synchronized boolean add(@NonNull RedisSearchHistory history) {
        try {
            // 历史列表
            List<RedisSearchHistory> histories = this.load();
            // 过滤出当前类型
            List<RedisSearchHistory> hisList = histories.parallelStream().filter(h -> Objects.equals(h.getType(), history.getType())).collect(Collectors.toList());
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
                List<RedisSearchHistory> delList = hisList.parallelStream().limit(limit).toList();
                histories.removeAll(delList);
            }
            // 保存数据
            return this.save(histories);
        } catch (Exception e) {
            StaticLog.warn("add error,err:{}", e.getMessage());
        }
        return false;
    }

    /**
     * 添加搜索历史
     *
     * @param kw 关键词
     * @return 结果
     */
    public boolean addSearchHistory(@NonNull String kw) {
        return this.add(new RedisSearchHistory(kw, 1));
    }

    /**
     * 添加替换历史
     *
     * @param kw 关键词
     * @return 结果
     */
    public boolean addReplaceHistory(@NonNull String kw) {
        return this.add(new RedisSearchHistory(kw, 2));
    }

    @Override
    public synchronized boolean update(@NonNull RedisSearchHistory history) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean delete(@NonNull RedisSearchHistory history) {
        try {
            List<RedisSearchHistory> histories = this.load();
            if (histories.removeIf(h -> h.compare(history))) {
                return this.save(histories);
            }
        } catch (Exception e) {
            StaticLog.warn("delete error,err:{}", e.getMessage());
        }
        return false;
    }

    @Override
    public Paging<RedisSearchHistory> getPage(int limit, Map<String, Object> params) {
        throw new UnsupportedOperationException();
    }
}
