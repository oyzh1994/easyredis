package cn.oyzh.easyredis.store;

import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easyredis.domain.RedisCollect;
import cn.oyzh.store.jdbc.JdbcStore;
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
public class RedisCollectJdbcStore extends JdbcStore<RedisCollect> {

    /**
     * 当前实例
     */
    public static final RedisCollectJdbcStore INSTANCE = new RedisCollectJdbcStore();

    public List<String> list(String iid) {
        QueryParam param = new QueryParam();
        param.setName("iid");
        param.setData(iid);
        List<RedisCollect> collects = super.selectList(param);
        if (CollectionUtil.isNotEmpty(collects)) {
            return collects.parallelStream().map(RedisCollect::getPath).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public boolean replace(String iid, String path) {
        return this.replace(new RedisCollect(iid, path));
    }

    public boolean replace(RedisCollect model) {
        if (model != null && !this.exist(model.getIid(), model.getPath())) {
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

    public boolean delete(String iid, String path) {
        if (StringUtil.isEmpty(iid) && StringUtil.isEmpty(path)) {
            Map<String, Object> params = new HashMap<>();
            params.put("iid", iid);
            params.put("path", path);
            return this.delete(params);
        }
        return false;
    }

    public boolean exist(String iid, String path) {
        if (StringUtil.isNotBlank(iid) && StringUtil.isNotBlank(path)) {
            Map<String, Object> params = new HashMap<>();
            params.put("iid", iid);
            params.put("path", path);
            return super.exist(params);
        }
        return false;
    }

    @Override
    protected RedisCollect newModel() {
        return new RedisCollect();
    }

    @Override
    protected Class<RedisCollect> modelClass() {
        return RedisCollect.class;
    }
}
