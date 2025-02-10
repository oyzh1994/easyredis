package cn.oyzh.easyredis.store;

import cn.oyzh.common.dto.Paging;
import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easyredis.domain.RedisKeyFilterHistory;
import cn.oyzh.store.jdbc.DeleteParam;
import cn.oyzh.store.jdbc.JdbcStandardStore;
import cn.oyzh.store.jdbc.OrderByParam;
import cn.oyzh.store.jdbc.PageParam;
import cn.oyzh.store.jdbc.QueryParam;
import cn.oyzh.store.jdbc.QueryParams;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * redis过滤历史存储
 *
 * @author oyzh
 * @since 2023/07/19
 */
public class RedisKeyFilterHistoryStore extends JdbcStandardStore<RedisKeyFilterHistory> {

    /**
     * 最大历史数量
     */
    public static int His_Max_Size = 50;

    /**
     * 当前实例
     */
    public static final RedisKeyFilterHistoryStore INSTANCE = new RedisKeyFilterHistoryStore();

    public List<RedisKeyFilterHistory> load() {
        return super.selectList();
    }

    public boolean replace(RedisKeyFilterHistory model) {
        boolean result = false;
        if (model != null) {
            result = this.insert(model);
            QueryParams queryParams = new QueryParams();
            long count = super.selectCount(queryParams);
            // 删除超出限制的节点
            if (count > His_Max_Size) {
                DeleteParam deleteParam = new DeleteParam();
                deleteParam.addQueryParams(queryParams);
                deleteParam.setLimit(1L);
                super.delete(deleteParam);
            }
        }
        return result;
    }

    public boolean delete(String kw) {
        if (StringUtil.isNotBlank(kw)) {
            DeleteParam param = new DeleteParam();
            param.addQueryParam(new QueryParam("pattern", kw));
            return this.delete(param);
        }
        return false;
    }

    public Paging<RedisKeyFilterHistory> getPage(long pageNo, int limit, String kw) {
        PageParam pageParam = new PageParam(limit, pageNo * limit);
        List<RedisKeyFilterHistory> list = this.selectPage(kw, List.of("pattern"), pageParam);
        Paging<RedisKeyFilterHistory> paging;
        if (CollectionUtil.isNotEmpty(list)) {
            long count = this.selectCount(kw, List.of("pattern"));
            paging = new Paging<>(list, limit, count);
            paging.currentPage(pageNo);
        } else {
            paging = new Paging<>(limit);
        }
        return paging;
    }

    public boolean exist(String kw) {
        if (StringUtil.isNotBlank(kw)) {
            Map<String, Object> params = new HashMap<>();
            params.put("pattern", kw);
            return super.exist(params);
        }
        return false;
    }

    @Override
    protected Class<RedisKeyFilterHistory> modelClass() {
        return RedisKeyFilterHistory.class;
    }

    public List<String> getPatterns() {
        List<RedisKeyFilterHistory> histories = this.load();
        return histories.parallelStream().map(RedisKeyFilterHistory::getPattern).collect(Collectors.toList());
    }
}
