package cn.oyzh.easyredis.tabs.key.geo;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyredis.controller.row.RedisZSetCoordinateAddController;
import cn.oyzh.easyredis.event.RedisZSetCoordinateAddedEvent;
import cn.oyzh.easyredis.fx.RedisFormatComboBox;
import cn.oyzh.easyredis.redis.row.RedisZSetRow;
import cn.oyzh.easyredis.tabs.key.RedisRowKeyTabContent;
import cn.oyzh.easyredis.trees.zset.RedisZSetKeyTreeItem;
import cn.oyzh.common.thread.TaskManager;
import cn.oyzh.fx.plus.controls.textfield.DecimalTextField;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.controls.table.FlexTableColumn;
import cn.oyzh.i18n.I18nHelper;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.window.StageManager;
import cn.oyzh.fx.plus.window.StageAdapter;
import cn.oyzh.fx.plus.util.ClipboardUtil;
import cn.oyzh.fx.rich.richtextfx.data.RichDataTextAreaPane;
import cn.oyzh.fx.rich.richtextfx.data.RichDataType;
import com.google.common.eventbus.Subscribe;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * zset键地理坐标tab内容组件
 *
 * @author oyzh
 * @since 2023/06/30
 */
public class RedisGEOKeyTabContent extends RedisRowKeyTabContent<RedisZSetKeyTreeItem, RedisZSetRow> {

    /**
     * 数据撤销
     */
    @FXML
    private SVGGlyph dataUndo;

    /**
     * 数据重做
     */
    @FXML
    private SVGGlyph dataRedo;

    /**
     * redis数据保存按钮
     */
    @FXML
    private SVGGlyph saveNodeData;

    /**
     * 经度值
     */
    @FXML
    private DecimalTextField longitudeVal;

    /**
     * 纬度值
     */
    @FXML
    private DecimalTextField latitudeVal;

    /**
     * 编号列
     */
    @FXML
    private TableColumn<RedisZSetRow, Integer> index;

    /**
     * 经度列
     */
    @FXML
    private TableColumn<RedisZSetRow, Double> longitude;

    /**
     * 纬度列
     */
    @FXML
    private TableColumn<RedisZSetRow, Double> latitude;

    /**
     * 坐标列
     */
    @FXML
    private FlexTableColumn<RedisZSetRow, String> coordinate;

    /**
     * 数据组件
     */
    @FXML
    private RichDataTextAreaPane nodeData;

    /**
     * 格式
     */
    @FXML
    private RedisFormatComboBox format;

    /**
     * 数据监听器
     */
    private final ChangeListener<String> dataListener = (observable, oldValue, newValue) -> {
        if (this.treeItem.currentRow() == null || Objects.equals(newValue, this.treeItem.currentRow().getValue())) {
            this.treeItem.data(null);
        } else {
            this.treeItem.data(newValue);
        }
        this.saveNodeData.setDisable(!this.treeItem.dataUnsaved());
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

    /**
     * 经度值监听器
     */
    private final ChangeListener<String> longitudeValListener = (observable, oldValue, newValue) -> {
        Number value = this.longitudeVal.getValue();
        if (this.treeItem.currentRow() == null || Objects.equals(value.doubleValue(), this.treeItem.currentRow().getLongitude())) {
            this.treeItem.longitude(null);
        } else {
            this.treeItem.longitude(value.doubleValue());
        }
        this.saveNodeData.setDisable(!this.treeItem.dataUnsaved());
    };

    /**
     * 纬度值监听器
     */
    private final ChangeListener<String> latitudeValListener = (observable, oldValue, newValue) -> {
        Number value = this.latitudeVal.getValue();
        if (this.treeItem.currentRow() == null || Objects.equals(value.doubleValue(), this.treeItem.currentRow().getLatitude())) {
            this.treeItem.latitude(null);
        } else {
            this.treeItem.latitude(value.doubleValue());
        }
        this.saveNodeData.setDisable(!this.treeItem.dataUnsaved());
    };

    @Override
    public boolean init(RedisZSetKeyTreeItem treeItem) {
        if (super.init(treeItem)) {
            this.pageData = null;
            // 格式监听
            this.format.selectedItemChanged(this.formatListener);
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
        this.coordinate.setCellValueFactory(new PropertyValueFactory<>("value"));
        this.latitude.setCellValueFactory(new PropertyValueFactory<>("latitude"));
        this.longitude.setCellValueFactory(new PropertyValueFactory<>("longitude"));
        this.latitudeVal.addTextChangeListener(this.latitudeValListener);
        this.longitudeVal.addTextChangeListener(this.longitudeValListener);
    }

    @Override
    protected List<RedisZSetRow> getRows() {
        List<RedisZSetRow> rows = this.treeItem.nodeValue();
        String filterKW = this.filter.getText();
        if (StrUtil.isNotEmpty(filterKW)) {
            rows = rows.parallelStream()
                    .filter(r -> StrUtil.containsIgnoreCase(r.getValue(), filterKW) || StrUtil.containsIgnoreCase(String.valueOf(r.getLatitude()), filterKW) || StrUtil.containsIgnoreCase(String.valueOf(r.getLongitude()), filterKW))
                    .collect(Collectors.toList());
        }
        return rows;
    }

    @FXML
    @Override
    protected void addRow() {
        StageAdapter fxView = StageManager.parseStage(RedisZSetCoordinateAddController.class);
        fxView.setProp("treeItem", this.treeItem);
        fxView.display();
    }

    @Override
    protected void initRow(RedisZSetRow row) {
        super.initRow(row);
        if (row == null) {
            this.nodeData.clear();
            this.nodeData.disable();
            this.latitudeVal.clear();
            this.latitudeVal.disable();
            this.longitudeVal.clear();
            this.longitudeVal.disable();
        } else {
            this.latitudeVal.setValue(row.getLatitude());
            this.latitudeVal.enable();
            this.longitudeVal.setValue(row.getLongitude());
            this.longitudeVal.enable();
            this.nodeData.enable();
            this.saveNodeData.disable();
            this.treeItem.clearData();
        }
    }

    @FXML
    @Override
    protected void saveKeyData() {
        if (this.treeItem.checkExists()) {
            MessageBox.warn(I18nHelper.dataAlreadyExists());
            return;
        }
        if (this.treeItem.dataUnsaved()) {
            TaskManager.start(() -> {
                if (this.treeItem.saveNodeValue()) {
                    this.saveNodeData.disable();
                }
            });
        }
    }

    @FXML
    @Override
    protected void copyRow() {
        String builder = I18nHelper.keyName() + ": " + this.treeItem.key() + System.lineSeparator() +
                I18nHelper.coordinates() + ": " + this.treeItem.currentRow().getValue() + System.lineSeparator() +
                I18nHelper.longitude() + ": " + this.treeItem.currentRow().getLongitude() + System.lineSeparator() +
                I18nHelper.latitude() + ": " + this.treeItem.currentRow().getLatitude();
        ClipboardUtil.setStringAndTip(builder);
    }

    /**
     * 反转视图
     */
    @FXML
    private void reverseView() {
        this.treeItem.reverseView();
    }

    /**
     * zset坐标添加事件
     *
     * @param event 事件
     */
    @EventSubscribe
    private void zSetCoordinateAdded(RedisZSetCoordinateAddedEvent event) {
        if (this.treeItem == event.data()) {
            this.firstPage();
        }
    }

    /**
     * 数据撤销
     */
    @FXML
    private void dataUndo() {
        this.nodeData.undo();
        this.nodeData.requestFocus();
    }

    /**
     * 数据重做
     */
    @FXML
    private void dataRedo() {
        this.nodeData.redo();
        this.nodeData.requestFocus();
    }

    /**
     * 粘贴数据
     */
    @FXML
    private void pasteData() {
        this.nodeData.paste();
        this.nodeData.requestFocus();
    }

    /**
     * 清除数据
     */
    @FXML
    private void clearData() {
        this.nodeData.clear();
        this.nodeData.requestFocus();
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
