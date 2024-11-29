package cn.oyzh.easyredis.store;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.StaticLog;
import cn.oyzh.common.log.JulLog;
import cn.oyzh.easyredis.RedisConst;
import cn.oyzh.easyredis.domain.RedisFilter;
import cn.oyzh.fx.common.dto.Paging;
import cn.oyzh.fx.common.store.ArrayFileStore;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * redis过滤存储
 *
 * @author oyzh
 * @since 2023/06/30
 */
public class RedisFilterStore extends ArrayFileStore<RedisFilter> {

    /**
     * 当前实例
     */
    public static final RedisFilterStore INSTANCE = new RedisFilterStore();

    {
        this.filePath(RedisConst.STORE_PATH + "redis_filter.json");
        JulLog.info("RedisFilterStore filePath:{} charset:{} init {}.", this.filePath(), this.charset(), super.init() ? "success" : "fail");
    }

    @Override
    public synchronized List<RedisFilter> load() {
        String text = FileUtil.readString(this.storeFile(), this.charset());
        if (StrUtil.isBlank(text)) {
            return new ArrayList<>();
        }
        List<RedisFilter> filters = JSONUtil.toList(text, RedisFilter.class);
        if (CollUtil.isNotEmpty(filters)) {
            filters = filters.parallelStream().filter(Objects::nonNull).sorted((o1, o2) -> o1.getKw().compareToIgnoreCase(o2.getKw())).collect(Collectors.toList());
        }
        return filters;
    }

    /**
     * 加载已启用的数据列表
     *
     * @return 已启用的数据列表
     */
    public synchronized List<RedisFilter> loadEnable() {
        List<RedisFilter> filters = this.load();
        if (CollUtil.isNotEmpty(filters)) {
            filters = filters.parallelStream().filter(RedisFilter::isEnable).toList();
        }
        return filters;
    }

    @Override
    public synchronized boolean add(@NonNull RedisFilter filter) {
        try {
            List<RedisFilter> filters = this.load();
            Optional<RedisFilter> optional = filters.parallelStream().filter(filter::compare).findFirst();
            if (optional.isEmpty()) {
                // 添加到集合
                filters.add(filter);
                // 更新数据
                return this.save(filters);
            }
            return true;
        } catch (Exception e) {
            JulLog.warn("add error,err:{}", e.getMessage());
        }
        return false;
    }

    @Override
    public synchronized boolean update(@NonNull RedisFilter filter) {
        try {
            List<RedisFilter> filters = this.load();
            Optional<RedisFilter> optional = filters.parallelStream().filter(filter::compare).findFirst();
            if (optional.isPresent()) {
                optional.get().copy(filter);
                // 更新数据
                return this.save(filters);
            }
        } catch (Exception e) {
            JulLog.warn("update error,err:{}", e.getMessage());
        }
        return false;
    }

    @Override
    public synchronized boolean delete(@NonNull RedisFilter filter) {
        try {
            List<RedisFilter> filters = this.load();
            if (CollUtil.isEmpty(filters)) {
                return false;
            }
            Optional<RedisFilter> optional = filters.parallelStream().filter(filter::compare).findFirst();
            if (optional.isPresent()) {
                // 移除redis信息
                filters.remove(optional.get());
                // 更新数据
                return this.save(filters);
            }
        } catch (Exception e) {
            JulLog.warn("delete error,err:{}", e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public Paging<RedisFilter> getPage(int limit, Map<String, Object> params) {
        // 加载数据
        List<RedisFilter> list = this.load();
        // 分页对象
        Paging<RedisFilter> paging = new Paging<>(list, limit);
        // 数据为空
        if (CollUtil.isNotEmpty(list)) {
            String searchKeyWord = params == null ? null : (String) params.get("searchKeyWord");
            // 过滤数据
            if (StrUtil.isNotBlank(searchKeyWord)) {
                final String kw = searchKeyWord.toLowerCase().trim();
                list = list.parallelStream().filter(z -> z.getKw().contains(kw)).collect(Collectors.toList());
            }
            // 添加到分页数据
            paging.dataList(list);
        }
        return paging;
    }

    public boolean exist(String kw) {
        List<RedisFilter> filters = this.load();
        if (CollUtil.isEmpty(filters)) {
            return false;
        }
        Optional<RedisFilter> optional = filters.parallelStream().filter(f -> f.compare(kw)).findFirst();
        return optional.isPresent();
    }
}
