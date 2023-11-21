package cn.oyzh.easyredis.trees;

import cn.oyzh.fx.plus.controls.tree.FXTreeCell;
import cn.oyzh.fx.plus.drag.DragNodeItem;
import cn.oyzh.fx.plus.drag.DragUtil;
import cn.oyzh.fx.plus.drag.DrapNodeHandler;
import cn.oyzh.fx.plus.trees.RichTreeCell;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import lombok.extern.slf4j.Slf4j;

/**
 * redis树节点工厂
 *
 * @author oyzh
 * @since 2023/06/31
 */
@Slf4j
public class RedisTreeCell extends RichTreeCell<RedisTreeItemValue> {

    // public static final String DRAG_CONTENT = "redis_drag";
    //
    // /**
    //  * 拖动增强
    //  */
    // private DrapNodeHandler drapNodeHandler;
    //
    // @Override
    // public Node initGraphic() {
    //     TreeItem<?> item = this.getTreeItem();
    //     if (item instanceof RedisKeyTreeItem<?> treeItem && !treeItem.visible()) {
    //         return null;
    //     }
    //     // 初始化拖动
    //     if (item instanceof DragNodeItem dragItem && dragItem.allowDragDrop() && this.drapNodeHandler == null) {
    //         this.drapNodeHandler = new DrapNodeHandler();
    //         DragUtil.initDragNode(this.drapNodeHandler, this, DRAG_CONTENT);
    //     }
    //     // 基础节点
    //     if (item instanceof BaseTreeItem treeItem) {
    //         treeItem.flushGraphic();
    //         if (this.getCursor() != Cursor.HAND) {
    //             this.setCursor(Cursor.HAND);
    //         }
    //         return treeItem.itemValue().create();
    //     }
    //     return null;
    // }

    // /**
    //  * 初始化拖动事件
    //  */
    // private void initDragEvent() {
    //     this.drapNodeHandler = new DrapNodeHandler();
    //     DragUtil.initDrag(this.drapNodeHandler, this);
    // }
}
