package cn.oyzh.easyredis.store;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.StaticLog;
import cn.oyzh.easyredis.RedisConst;
import cn.oyzh.easyredis.domain.RedisSetting;
import cn.oyzh.fx.common.store.ObjectFileStore;
import lombok.NonNull;


/**
 * redis设置储存
 *
 * @author oyzh
 * @since 2026/06/26
 */
//@Slf4j
public class RedisSettingStore extends ObjectFileStore<RedisSetting> {

    /**
     * 当前实例
     */
    public static final RedisSettingStore INSTANCE = new RedisSettingStore();

    /**
     * 当前设置
     */
    public static final RedisSetting SETTING = INSTANCE.load();

    {
        this.filePath(RedisConst.STORE_PATH + "redis_setting.json");
        StaticLog.info("RedisSettingStore filePath:{} charset:{} init {}.", this.filePath(), this.charset(), super.init() ? "success" : "fail");
    }

    @Override
    public synchronized RedisSetting load() {
        RedisSetting setting = null;
        String text = FileUtil.readString(this.storeFile(), this.charset());
        if (StrUtil.isNotBlank(text)) {
            try {
                setting = JSONUtil.toBean(text, RedisSetting.class);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (setting == null) {
            setting = new RedisSetting();
        }
        return setting;
    }

    @Override
    public boolean update(@NonNull RedisSetting setting) {
        return this.saveData(setting);
    }

}
