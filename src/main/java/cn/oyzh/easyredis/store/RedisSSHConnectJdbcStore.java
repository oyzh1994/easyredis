package cn.oyzh.easyredis.store;

import cn.oyzh.easyredis.domain.RedisSSHConnect;
import cn.oyzh.store.jdbc.JdbcStandardStore;
import cn.oyzh.store.jdbc.QueryParam;

/**
 * @author oyzh
 * @since 2024/09/26
 */
public class RedisSSHConnectJdbcStore extends JdbcStandardStore<RedisSSHConnect> {

    /**
     * 当前实例
     */
    public static final RedisSSHConnectJdbcStore INSTANCE = new RedisSSHConnectJdbcStore();

    public RedisSSHConnect find(String iid) {
        QueryParam param = new QueryParam();
        param.setName("iid");
        param.setData(iid);
        return super.selectOne(param);
    }

    public boolean replace(RedisSSHConnect model) {
        String iid = model.getIid();
        if (super.exist(iid)) {
            return super.update(model);
        }
        return this.insert(model);
    }

    @Override
    protected RedisSSHConnect newModel() {
        return new RedisSSHConnect();
    }

    @Override
    protected Class<RedisSSHConnect> modelClass() {
        return RedisSSHConnect.class;
    }
}
