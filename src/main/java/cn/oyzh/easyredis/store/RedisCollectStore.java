package cn.oyzh.easyredis.store;

import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easyredis.domain.RedisCollect;
import cn.oyzh.store.jdbc.DeleteParam;
import cn.oyzh.store.jdbc.JdbcStandardStore;
import cn.oyzh.store.jdbc.QueryParam;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author oyzh
 * @since 2024/09/26
 */
public class RedisCollectStore extends JdbcStandardStore<RedisCollect> {

    /**
     * 当前实例
     */
    public static final RedisCollectStore INSTANCE = new RedisCollectStore();

    public List<String> list(String iid) {
        QueryParam param = new QueryParam();
        param.setName("iid");
        param.setData(iid);
        List<RedisCollect> collects = super.selectList(param);
        if (CollectionUtil.isNotEmpty(collects)) {
            return collects.parallelStream().map(RedisCollect::getKey).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public boolean replace(String iid, int dbIndex, String key) {
        return this.replace(new RedisCollect(iid, dbIndex, key));
    }

    public boolean replace(RedisCollect model) {
        if (model != null && !this.exist(model.getIid(), model.getDbIndex(), model.getKey())) {
            return this.insert(model);
        }
        return false;
    }

    public boolean delete(String iid) {
        if (StringUtil.isEmpty(iid)) {
            Map<String, Object> params = new HashMap<>();
            params.put("iid", iid);
            return this.delete(params);
        }
        return false;
    }

    public boolean delete(String iid, int dbIndex, String key) {
        if (StringUtil.isEmpty(iid) && StringUtil.isEmpty(key)) {
            Map<String, Object> params = new HashMap<>();
            params.put("iid", iid);
            params.put("key", key);
            params.put("dbIndex", dbIndex);
            return this.delete(params);
        }
        return false;
    }

    public boolean exist(String iid, int dbIndex, String key) {
        if (StringUtil.isNotBlank(iid) && StringUtil.isNotBlank(key)) {
            Map<String, Object> params = new HashMap<>();
            params.put("iid", iid);
            params.put("key", key);
            params.put("dbIndex", dbIndex);
            return super.exist(params);
        }
        return false;
    }

    @Override
    protected Class<RedisCollect> modelClass() {
        return RedisCollect.class;
    }

    public void deleteByIid(String iid) {
        DeleteParam param = new DeleteParam();
        param.addQueryParam(QueryParam.of("iid", iid));
        super.delete(param);
    }
}
