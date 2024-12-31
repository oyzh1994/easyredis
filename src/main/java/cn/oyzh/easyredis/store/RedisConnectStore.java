package cn.oyzh.easyredis.store;

import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.easyredis.domain.RedisConnect;
import cn.oyzh.easyredis.domain.RedisFilter;
import cn.oyzh.easyredis.domain.RedisSSHConfig;
import cn.oyzh.store.jdbc.JdbcStandardStore;

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
     * 过滤存储
     */
    private final RedisFilterStore filterStore = RedisFilterStore.INSTANCE;

    /**
     * 收藏存储
     */
    private final RedisCollectStore collectStore = RedisCollectStore.INSTANCE;

    /**
     * ssh配置存储
     */
    private final RedisSSHConfigStore sshConfigStore = RedisSSHConfigStore.INSTANCE;

    /**
     * 加载列表
     *
     * @return zk连接列表
     */
    public List<RedisConnect> load() {
        return super.selectList();
    }

    /**
     * 替换
     *
     * @param model 模型
     * @return 结果
     */
    public boolean replace(RedisConnect model) {
        boolean result = false;
        if (model != null) {
            if (super.exist(model.getId())) {
                result = this.update(model);
            } else {
                result = this.insert(model);
            }

            // ssh处理
            RedisSSHConfig sshConfig = model.getSshConfig();
            if (sshConfig != null) {
                sshConfig.setIid(model.getId());
                this.sshConfigStore.replace(sshConfig);
            } else {
                this.sshConfigStore.deleteByIid(model.getId());
            }

            // // 收藏处理
            // List<String> collects = model.getCollects();
            // if (CollectionUtil.isNotEmpty(collects)) {
            //     for (String collect : collects) {
            //         this.collectStore.replace(model.getId(), collect);
            //     }
            // }

            // 过滤处理
            List<RedisFilter> filters = model.getFilters();
            if (CollectionUtil.isNotEmpty(filters)) {
                this.filterStore.deleteByIid(model.getId());
                for (RedisFilter filter : filters) {
                    filter.setIid(model.getId());
                    this.filterStore.replace(filter);
                }
            }
        }
        return result;
    }

    @Override
    public boolean delete(RedisConnect model) {
        boolean result = super.delete(model);
        // 删除关联配置
        if (result) {
            this.filterStore.deleteByIid(model.getId());
            this.collectStore.deleteByIid(model.getId());
            this.sshConfigStore.deleteByIid(model.getId());
        }
        return result;
    }

    @Override
    protected Class<RedisConnect> modelClass() {
        return RedisConnect.class;
    }
}
