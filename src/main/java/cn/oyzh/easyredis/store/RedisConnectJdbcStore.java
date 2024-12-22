package cn.oyzh.easyredis.store;

import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.easyredis.domain.RedisConnect;
import cn.oyzh.easyredis.domain.RedisSSHConfig;
import cn.oyzh.store.jdbc.DeleteParam;
import cn.oyzh.store.jdbc.JdbcStandardStore;
import cn.oyzh.store.jdbc.QueryParam;

import java.util.List;

/**
 * @author oyzh
 * @since 2024/09/26
 */
public class RedisConnectJdbcStore extends JdbcStandardStore<RedisConnect> {

    /**
     * 当前实例
     */
    public static final RedisConnectJdbcStore INSTANCE = new RedisConnectJdbcStore();

    public List<RedisConnect> load() {
        List<RedisConnect> list = super.selectList();
        // 处理ssh信息
        for (RedisConnect info : list) {
            info.setSshConnect(RedisSSHConfigStore.INSTANCE.find(info.getId()));
        }
        return list;
    }

    public boolean replace(RedisConnect info) {
        boolean result = false;
        if (info != null) {
            if (super.exist(info.getId())) {
                result = this.update(info);
            } else {
                result = this.insert(info);
            }

            // ssh信息处理
            RedisSSHConfig connect = info.getSshConnect();
            if (info.getSshConnect() != null) {
                RedisSSHConfigStore.INSTANCE.replace(connect);
            } else {
                DeleteParam param = new DeleteParam();
                param.addQueryParam(new QueryParam("iid", info.getId()));
                RedisSSHConfigStore.INSTANCE.delete(connect);
            }

            // 收藏处理
            List<String> collects = info.getCollects();
            if (CollectionUtil.isNotEmpty(collects)) {
                for (String collect : collects) {
                    RedisCollectJdbcStore.INSTANCE.replace(info.getId(), collect);
                }
            } else {
                RedisCollectJdbcStore.INSTANCE.delete(info.getId());
            }
        }
        return result;
    }

    @Override
    protected RedisConnect newModel() {
        return new RedisConnect();
    }

    @Override
    protected Class<RedisConnect> modelClass() {
        return RedisConnect.class;
    }
}
