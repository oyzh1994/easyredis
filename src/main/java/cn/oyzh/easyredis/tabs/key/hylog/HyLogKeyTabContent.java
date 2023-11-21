package cn.oyzh.easyredis.tabs.key.hylog;

import cn.oyzh.easyredis.controller.row.HyLogRowAddController;
import cn.oyzh.easyredis.tabs.key.KeyTabContent;
import cn.oyzh.easyredis.trees.hylog.RedisHyperLogLogKeyTreeItem;
import cn.oyzh.fx.plus.controls.text.FXLabel;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.stage.StageUtil;
import cn.oyzh.fx.plus.stage.StageWrapper;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * hyperLogLog键tab内容组件
 *
 * @author oyzh
 * @since 2023/06/21
 */
@Lazy
@Component
public class HyLogKeyTabContent extends KeyTabContent<RedisHyperLogLogKeyTreeItem> {

    /**
     * 数据大小
     */
    @FXML
    private FXLabel size;

    /**
     * 统计值
     */
    @FXML
    private FXLabel count;

    @Override
    public boolean init(RedisHyperLogLogKeyTreeItem treeItem) {
        if (super.init(treeItem)) {
            // 设置鼠标样式
            this.count.setCursor(Cursor.HAND);
            return true;
        }
        return false;
    }

    @Override
    public void reloadNode() {
        this.initNode();
    }

    @Override
    protected void initNode() {
        // 数据处理
        this.setRawData(this.treeItem.rawValue());
        // 大小
        Integer size = this.treeItem.size();
        if (size == null) {
            this.size.setText("大小: N/A");
        } else {
            this.size.setText("大小: " + size + " bytes");
        }
        // 统计值
        this.count.setText("统计值: " + this.treeItem.count());
    }

    /**
     * 刷新数据
     */
    @FXML
    private void reloadData() {
        // 刷新数据
        try {
            this.treeItem.refreshNodeValue();
            // 数据变更
            this.initNode();
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    /**
     * 添加行
     */
    @FXML
    private void addRow() {
        StageWrapper fxView = StageUtil.parseStage(HyLogRowAddController.class, this.treeItem.window());
        fxView.setProp("treeItem", this.treeItem);
        fxView.display();
    }
}
