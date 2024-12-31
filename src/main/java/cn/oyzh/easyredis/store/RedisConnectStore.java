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
public class RedisConnectStore extends JdbcStandardStore<RedisConnect> {

    /**
     * 当前实例
     */
    public static final RedisConnectStore INSTANCE = new RedisConnectStore();

    /**
     * ssh配置存储
     */
    private  final RedisSSHConfigStore sshConfigStore = RedisSSHConfigStore.INSTANCE;

    public List<RedisConnect> load() {
        return super.selectList();
    }

    public boolean replace(RedisConnect model) {
        boolean result = false;
        if (model != null) {
            if (super.exist(model.getId())) {
                result = this.update(model);
            } else {
                result = this.insert(model);
            }
            // ssh配置处理
            RedisSSHConfig sshConfig = model.getSshConfig();
            if (sshConfig != null) {
                this.sshConfigStore.replace(sshConfig);
            } else {
                this.sshConfigStore.deleteByIid(model.getId());
            }
            // 收藏处理
            List<String> collects = model.getCollects();
            if (CollectionUtil.isNotEmpty(collects)) {
                for (String collect : collects) {
                    RedisCollectStore.INSTANCE.replace(model.getId(), collect);
                }
            } else {
                RedisCollectStore.INSTANCE.deleteByIid(model.getId());
            }
        }
        return result;
    }

    @Override
    protected Class<RedisConnect> modelClass() {
        return RedisConnect.class;
    }
}
