package cn.oyzh.easyredis.store;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.StaticLog;
import cn.oyzh.easyredis.RedisConst;
import cn.oyzh.easyredis.domain.RedisPageInfo;
import cn.oyzh.fx.common.store.ObjectFileStore;
import com.alibaba.fastjson.JSON;
import lombok.NonNull;


/**
 * 页面信息储存
 *
 * @author oyzh
 * @since 2023/06/17
 */
//@Slf4j
public class PageInfoStore extends ObjectFileStore<RedisPageInfo> {

    /**
     * 当前实例
     */
    public static final PageInfoStore INSTANCE = new PageInfoStore();

    /**
     * 当前设置
     */
    public static final RedisPageInfo PAGE_INFO = INSTANCE.load();

    {
        this.filePath(RedisConst.STORE_PATH + "page_info.json");
        StaticLog.info("PageInfoStore filePath:{} charset:{} init {}.", this.filePath(), this.charset(), super.init() ? "success" : "fail");
    }

    @Override
    public synchronized RedisPageInfo load() {
        RedisPageInfo pageInfo = null;
        String text = FileUtil.readString(this.storeFile(), this.charset());
        if (StrUtil.isNotBlank(text)) {
            try {
                pageInfo = JSON.parseObject(text, RedisPageInfo.class);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (pageInfo == null) {
            pageInfo = new RedisPageInfo();
        }
        return pageInfo;
    }

    @Override
    public boolean update(@NonNull RedisPageInfo data) {
        return this.saveData(data);
    }

}
