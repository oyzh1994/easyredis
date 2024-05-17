package cn.oyzh.easyredis.tabs.key.set;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyredis.controller.row.RedisSetMemberAddController;
import cn.oyzh.easyredis.event.RedisSetMemberAddedEvent;
import cn.oyzh.easyredis.fx.RedisFormatComboBox;
import cn.oyzh.easyredis.redis.row.RedisSetRow;
import cn.oyzh.easyredis.tabs.key.RedisRowKeyTabContent;
import cn.oyzh.easyredis.tabs.key.string.RedisStringKeyTabContent;
import cn.oyzh.easyredis.trees.set.RedisSetKeyTreeItem;
import cn.oyzh.fx.common.thread.TaskManager;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.i18n.I18nHelper;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.stage.StageUtil;
import cn.oyzh.fx.plus.stage.StageWrapper;
import cn.oyzh.fx.plus.util.ClipboardUtil;
import cn.oyzh.fx.rich.data.RichDataPane;
import cn.oyzh.fx.rich.data.RichDataType;
import com.google.common.eventbus.Subscribe;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * set键tab内容组件
 *
 * @author oyzh
 * @since 2023/06/21
 */
public class RedisSetKeyTabContent extends RedisRowKeyTabContent<RedisSetKeyTreeItem, RedisSetRow> {

    /**
     * redis数据保存按钮
     */
    @FXML
    private SVGGlyph saveNodeData;

    /**
     * 编号列
     */
    @FXML
    private TableColumn<RedisSetRow, Integer> index;

    /**
     * 值列
     */
    @FXML
    private TableColumn<RedisSetRow, String> value;

    /**
     * 格式
     */
    @FXML
    private RedisFormatComboBox format;

    /**
     * 数据组件
     */
    @FXML
    private RichDataPane nodeData;

    /**
     * redis数据监听器
     */
    private final ChangeListener<String> dataListener = (observable, oldValue, newValue) -> {
        if (this.treeItem.currentRow() == null || Objects.equals(newValue, this.treeItem.currentRow().getValue())) {
            this.treeItem.clearData();
        } else {
            this.treeItem.data(newValue);
        }
    };

    /**
     * 格式监听器
     */
    private final ChangeListener<String> formatListener = (t1, t2, t3) -> {
        if (this.format.isStringFormat()) {
            this.showData(RichDataType.STRING);
            this.nodeData.setEditable(true);
        } else if (this.format.isJsonFormat()) {
            this.showData(RichDataType.JSON);
            this.nodeData.setEditable(true);
        } else if (this.format.isBinaryFormat()) {
            this.showData(RichDataType.BINARY);
            this.nodeData.setEditable(false);
        } else if (this.format.isHexFormat()) {
            this.showData(RichDataType.HEX);
            this.nodeData.setEditable(false);
        } else if (this.format.isRawFormat()) {
            this.showData(RichDataType.RAW);
        }
    };

    @Override
    public boolean init(RedisSetKeyTreeItem treeItem) {
        this.pageData = null;
        if (super.init(treeItem)) {
            // 格式监听
            this.format.selectedItemChanged(this.formatListener);
            this.treeItem.dataProperty().addListener((observable, oldValue, newValue) -> this.saveNodeData.setDisable(newValue == null));
            // 键数据处理
            this.nodeData.addTextChangeListener(this.dataListener);
            this.nodeData.undoableProperty().addListener((observableValue, aBoolean, t1) -> this.dataUndo.setDisable(!t1));
            this.nodeData.redoableProperty().addListener((observableValue, aBoolean, t1) -> this.dataRedo.setDisable(!t1));
            return true;
        }
        return false;
    }

    @Override
    protected void initNode() {
        // 初始化表单
        this.initTable();
        // 显示首页
        this.firstPage();
        // 绑定属性
        this.index.setCellValueFactory(new PropertyValueFactory<>("index"));
        this.value.setCellValueFactory(new PropertyValueFactory<>("value"));
    }

    @Override
    protected List<RedisSetRow> getRows() {
        List<RedisSetRow> rows = this.treeItem.nodeValue();
        String filterKW = this.filter.getText();
        if (StrUtil.isNotEmpty(filterKW)) {
            rows = rows.parallelStream()
                    .filter(r -> StrUtil.containsIgnoreCase(r.getValue(), filterKW))
                    .collect(Collectors.toList());
        }
        return rows;
    }

    /**
     * 添加行
     */
    @FXML
    @Override
    protected void addRow() {
        StageWrapper fxView = StageUtil.parseStage(RedisSetMemberAddController.class, this.treeItem.window());
        fxView.setProp("treeItem", this.treeItem);
        fxView.display();
    }

    @FXML
    @Override
    protected void saveKeyData() {
        if (this.treeItem.checkExists()) {
            MessageBox.warn(I18nHelper.dataAlreadyExists());
            return;
        }
        if (this.treeItem.dataUnsaved()) {
            TaskManager.start(() -> this.treeItem.saveNodeValue());
        }
    }

    @FXML
    @Override
    protected void copyRow() {
        String builder = I18nHelper.keyName() + ": " + this.treeItem.key() + System.lineSeparator() +
                I18nHelper.member() + ": " + this.treeItem.currentRow().getValue();
        ClipboardUtil.setStringAndTip(builder, "成员信息");
    }

    /**
     * set成员添加事件
     *
     * @param msg 消息
     */
    @Subscribe
    private void onSetMemberAdded(RedisSetMemberAddedEvent msg) {
        if (this.treeItem == msg.data()) {
            this.firstPage();
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

    @Override
    protected void clearRaw() {
        this.nodeData.clear();
        this.nodeData.disable();
    }
}
