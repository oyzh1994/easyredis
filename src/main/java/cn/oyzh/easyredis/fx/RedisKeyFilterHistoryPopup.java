package cn.oyzh.easyredis.fx;

import cn.hutool.core.collection.CollUtil;
import cn.oyzh.easyredis.store.RedisKeyFilterHistoryStore;
import cn.oyzh.fx.plus.controls.popup.SearchHistoryPopup;

import java.util.List;

/**
 * redis键过滤历史弹窗
 *
 * @author oyzh
 * @since 2023/07/19
 */
public class RedisKeyFilterHistoryPopup extends SearchHistoryPopup {

    /**
     * 过滤历史储存
     */
    private final RedisKeyFilterHistoryStore historyStore = RedisKeyFilterHistoryStore.INSTANCE;

    @Override
    public List<String> getHistories() {
        List<String> list = this.historyStore.getPatterns();
        if (CollUtil.isNotEmpty(list)) {
            return list.reversed();
        }
        return list;
    }
}
