package cn.oyzh.easyredis.tabs.keys;

import cn.oyzh.easyredis.controller.row.RedisHyLogElementsAddController;
import cn.oyzh.easyredis.event.key.RedisHyLogElementsAddedEvent;
import cn.oyzh.easyredis.trees.keys.RedisStringKeyTreeItem;
import cn.oyzh.event.EventSubscribe;
import cn.oyzh.fx.plus.controls.text.FXText;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.window.StageAdapter;
import cn.oyzh.fx.plus.window.StageManager;
import cn.oyzh.fx.rich.richtextfx.data.RichDataTextAreaPane;
import cn.oyzh.fx.rich.richtextfx.data.RichDataType;
import cn.oyzh.i18n.I18nHelper;
import javafx.fxml.FXML;

/**
 * hyLog键tab内容组件
 *
 * @author oyzh
 * @since 2024/05/17
 */
public class RedisHylogKeyTabController extends RedisKeyTabController<RedisStringKeyTreeItem> {

    /**
     * 二进制数据
     */
    @FXML
    private FXText binary;

    /**
     * 统计值
     */
    @FXML
    private FXText count;

    /**
     * 数据
     */
    @FXML
    private RichDataTextAreaPane nodeData;

    @Override
    protected void initKey() {
        // 数据处理
        this.firstShowData();
        // 刷新二进制处理
        this.flushBinary();
        // 统计值
        this.count.setText(I18nHelper.count() + ": " + this.treeItem.count());
    }

    /**
     * 刷新二进制处理
     */
    private void flushBinary() {
        // 如果是raw格式，则选择binary
        if (this.treeItem.isRawEncoding()) {
            this.binary.display();
        } else {
            this.binary.disappear();
        }
    }

    /**
     * 重载数据
     */
    @FXML
    private void reloadData() {
        // 放弃保存
        if (this.treeItem.isDataUnsaved() && !MessageBox.confirm(I18nHelper.unsavedAndContinue())) {
            return;
        }
        // 刷新数据
        try {
            this.treeItem.refreshKeyValue();
            // 数据变更
            this.initKey();
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    /**
     * 添加统计值
     */
    @FXML
    private void addRow() {
        StageAdapter fxView = StageManager.parseStage(RedisHyLogElementsAddController.class, this.treeItem.window());
        fxView.setProp("treeItem", this.treeItem);
        fxView.display();
    }

    /**
     * hyLog元素添加事件
     *
     * @param msg 消息
     */
    @EventSubscribe
    private void onHyLogElementAdded(RedisHyLogElementsAddedEvent msg) {
        if (this.treeItem == msg.data()) {
            // 刷新数据
            this.treeItem.flushCount();
            this.initKey();
        }
    }

    @Override
    protected void firstShowData() {
        this.nodeData.showData(this.treeItem.rawValue());
        // 首次设置数据要清除历史
        this.nodeData.forgetHistory();
    }

    @Override
    protected void showData(RichDataType dataType) {
        this.nodeData.showData(dataType, this.treeItem.rawValue());
    }
}
