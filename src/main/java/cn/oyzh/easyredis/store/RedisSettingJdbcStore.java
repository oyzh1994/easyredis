package cn.oyzh.easyredis.store;

import cn.oyzh.easyredis.domain.RedisSetting;
import cn.oyzh.store.jdbc.JdbcStandardStore;


/**
 * @author oyzh
 * @since 2024/09/23
 */
public class RedisSettingJdbcStore extends JdbcStandardStore<RedisSetting> {

    /**
     * 当前实例
     */
    public static final RedisSettingJdbcStore INSTANCE = new RedisSettingJdbcStore();

    /**
     * 当前设置
     */
    public static final RedisSetting SETTING = INSTANCE.load();

    /**
     * 数据id
     */
    private static final String DATA_UID = "DEFAULT";

    public RedisSetting load() {
        RedisSetting setting = super.selectOne(DATA_UID);
        if (setting == null) {
            setting = new RedisSetting();
        }
        return setting;
    }

    public boolean replace(RedisSetting model) {
        if (model != null) {
            if (super.exist(DATA_UID)) {
                return this.update(model);
            }
            return this.insert(model);
        }
        return false;
    }

    @Override
    protected RedisSetting newModel() {
        return new RedisSetting();
    }

    @Override
    protected Class<RedisSetting> modelClass() {
        return RedisSetting.class;
    }
}
