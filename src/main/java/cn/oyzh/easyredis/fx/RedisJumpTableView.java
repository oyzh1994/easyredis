package cn.oyzh.easyredis.fx;

import cn.oyzh.easyredis.domain.RedisJumpConfig;
import cn.oyzh.easyredis.store.RedisJumpConfigStore;
import cn.oyzh.fx.plus.controls.table.FXTableView;
import cn.oyzh.fx.plus.tableview.TableViewUtil;

import java.util.List;

/**
 * @author oyzh
 * @since 2025-04-15
 */
public class RedisJumpTableView extends FXTableView<RedisJumpConfig> {

    {
        TableViewUtil.copyCellDataOnDoubleClicked(this);
    }

    /**
     * 跳板配置存储器
     */
    private final RedisJumpConfigStore configStore = RedisJumpConfigStore.INSTANCE;

    public void init(String iid) {
        List<RedisJumpConfig> configs = this.configStore.listByIid(iid);
        this.setItem(configs);
    }

    /**
     * 更新排序
     */
    public void updateOrder() {
        for (int i = 0; i < this.getItemSize(); i++) {
            RedisJumpConfig config = (RedisJumpConfig) this.getItem(i);
            config.setOrder(i);
        }
    }

}
