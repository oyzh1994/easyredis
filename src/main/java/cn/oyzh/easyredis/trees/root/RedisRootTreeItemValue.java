package cn.oyzh.easyredis.trees.root;

import cn.oyzh.easyredis.RedisConst;
import cn.oyzh.easyredis.trees.RedisTreeItemValue;
import cn.oyzh.fx.plus.controls.FlexImageView;
import cn.oyzh.fx.plus.util.IconUtil;


/**
 * redis 根节点值
 *
 * @author oyzh
 * @since 2023/11/21
 */
public class RedisRootTreeItemValue extends RedisTreeItemValue {

    public RedisRootTreeItemValue() {
        this.flushGraphic();
        this.flushText();
    }

    @Override
    public String name() {
        return "Redis连接列表";
    }

    @Override
    public void flushGraphic() {
        if (this.graphic() == null) {
            this.graphic(new FlexImageView(IconUtil.getIcon(RedisConst.ICON_PATH), 13));
        }
    }
}
