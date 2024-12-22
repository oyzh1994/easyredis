package cn.oyzh.easyredis.store;

import cn.oyzh.easyredis.domain.RedisSSHConfig;
import cn.oyzh.store.jdbc.JdbcStandardStore;
import cn.oyzh.store.jdbc.QueryParam;

/**
 * @author oyzh
 * @since 2024/09/26
 */
public class RedisSSHConfigStore extends JdbcStandardStore<RedisSSHConfig> {

    /**
     * 当前实例
     */
    public static final RedisSSHConfigStore INSTANCE = new RedisSSHConfigStore();

    public RedisSSHConfig find(String iid) {
        QueryParam param = new QueryParam();
        param.setName("iid");
        param.setData(iid);
        return super.selectOne(param);
    }

    public boolean replace(RedisSSHConfig model) {
        String iid = model.getIid();
        if (super.exist(iid)) {
            return super.update(model);
        }
        return this.insert(model);
    }

    @Override
    protected RedisSSHConfig newModel() {
        return new RedisSSHConfig();
    }

    @Override
    protected Class<RedisSSHConfig> modelClass() {
        return RedisSSHConfig.class;
    }
}
