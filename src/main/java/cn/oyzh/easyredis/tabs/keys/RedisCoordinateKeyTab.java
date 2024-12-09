package cn.oyzh.easyredis.tabs.keys;

import cn.oyzh.common.thread.TaskManager;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easyredis.controller.row.RedisZSetCoordinateAddController;
import cn.oyzh.easyredis.event.RedisZSetCoordinateAddedEvent;
import cn.oyzh.easyredis.fx.RedisFormatComboBox;
import cn.oyzh.easyredis.redis.key.RedisKeyRow;
import cn.oyzh.easyredis.redis.key.RedisZSetValue;
import cn.oyzh.easyredis.trees.keys.RedisZSetKeyTreeItem;
import cn.oyzh.event.EventSubscribe;
import cn.oyzh.fx.gui.text.field.DecimalTextField;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.util.ClipboardUtil;
import cn.oyzh.fx.plus.window.StageAdapter;
import cn.oyzh.fx.plus.window.StageManager;
import cn.oyzh.fx.rich.richtextfx.data.RichDataTextAreaPane;
import cn.oyzh.fx.rich.richtextfx.data.RichDataType;
import cn.oyzh.i18n.I18nHelper;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * redis zset键tab
 *
 * @author oyzh
 * @since 2023/11/21
 */
public class RedisCoordinateKeyTab extends RedisKeyTab<RedisZSetKeyTreeItem> {

    public RedisCoordinateKeyTab(RedisZSetKeyTreeItem treeItem) {
        super(treeItem);
    }

    @Override
    protected String url() {
        return "/tabs/keys/redisCoordinateKeyTab.fxml";
    }

    @Override
    public RedisCoordinateKeyTabController controller() {
        return (RedisCoordinateKeyTabController) super.controller();
    }

    /**
     * zset键地理坐标tab内容组件
     *
     * @author oyzh
     * @since 2023/06/30
     */
    public static class RedisCoordinateKeyTabController extends RedisRowKeyTabController<RedisZSetKeyTreeItem, RedisZSetValue.RedisZSetRow> {

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
                this.nodeData.setEditable(true);
            }
        };

        /**
         * 数据监听器
         */
        private final ChangeListener<String> dataListener = (observable, oldValue, newValue) -> {
            if (!Objects.equals(this.treeItem.rawData(), newValue)) {
                this.saveNodeData.enable();
                if (this.treeItem.unsavedValue() == null) {
                    this.treeItem.data(this.treeItem.currentRow());
                }
                this.treeItem.unsavedValue().setValue(newValue);
            }
        };

        /**
         * 经度值监听器
         */
        private final ChangeListener<String> longitudeValListener = (observable, oldValue, newValue) -> {
            Number value = this.longitudeVal.getValue();
            RedisZSetValue.RedisZSetRow row = this.treeItem.rawValue();
            if (!Objects.equals(row.getLatitude(), value.doubleValue())) {
                this.saveNodeData.enable();
                if (this.treeItem.unsavedValue() == null) {
                    this.treeItem.data(this.treeItem.currentRow());
                }
                this.treeItem.unsavedValue().setLongitude(value.doubleValue());
            }
        };

        /**
         * 纬度值监听器
         */
        private final ChangeListener<String> latitudeValListener = (observable, oldValue, newValue) -> {
            Number value = this.longitudeVal.getValue();
            RedisZSetValue.RedisZSetRow row = this.treeItem.rawValue();
            if (!Objects.equals(row.getLatitude(), value.doubleValue())) {
                this.saveNodeData.enable();
                if (this.treeItem.unsavedValue() == null) {
                    this.treeItem.data(this.treeItem.currentRow());
                }
                this.treeItem.unsavedValue().setLatitude(value.doubleValue());
            }
        };

        @Override
        protected void initKey() {
            // 初始化表单
            this.initTable();
            // 显示首页
            this.firstPage();
        }

        @Override
        protected List<RedisZSetValue.RedisZSetRow> getRows() {
            List<RedisZSetValue.RedisZSetRow> rows = this.treeItem.rows();
            String filterKW = this.filter.getText();
            if (StringUtil.isNotEmpty(filterKW)) {
                rows = rows.parallelStream()
                        .filter(r -> StringUtil.containsIgnoreCase(r.getValue(), filterKW) || StringUtil.containsIgnoreCase(String.valueOf(r.getLatitude()), filterKW) || StringUtil.containsIgnoreCase(String.valueOf(r.getLongitude()), filterKW))
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
        protected void initRow(RedisZSetValue.RedisZSetRow row) {
            super.initRow(row);
            if (row == null) {
                this.nodeData.clear();
                this.nodeData.disable();
                this.latitudeVal.clear();
                this.longitudeVal.clear();
            } else {
                this.latitudeVal.setValue(row.getLatitude());
                this.longitudeVal.setValue(row.getLongitude());
                this.nodeData.enable();
                this.saveNodeData.disable();
                this.treeItem.clearData();
            }
        }

        @FXML
        @Override
        protected void saveKeyValue() {
            if (this.treeItem.checkRowExists()) {
                MessageBox.warn(I18nHelper.dataAlreadyExists());
                return;
            }
            if (this.treeItem.isDataUnsaved()) {
                TaskManager.start(() -> {
                    this.treeItem.saveKeyValue();
                    this.listTable.refresh();
                    this.saveNodeData.disable();
                });
            }
        }

        @FXML
        @Override
        protected void copyRow() {
            if (this.treeItem.isSelectRow()) {
                String builder = I18nHelper.keyName() + " : " + this.treeItem.key() + System.lineSeparator() +
                        I18nHelper.coordinates() + " : " + this.treeItem.currentRow().getValue() + System.lineSeparator() +
                        I18nHelper.longitude() + " : " + this.treeItem.currentRow().getLongitude() + System.lineSeparator() +
                        I18nHelper.latitude() + " : " + this.treeItem.currentRow().getLatitude();
                ClipboardUtil.setStringAndTip(builder);
            }
        }

        /**
         * 反转视图
         */
        @FXML
        private void reverseView() {
            this.treeItem.reverseView();
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
            RedisZSetValue.RedisZSetRow row = this.treeItem.data();
            if (row != null) {
                this.nodeData.showData(row.getValue());
                this.nodeData.forgetHistory();
                this.saveNodeData.disable();
            }
        }

        @Override
        protected void showData(RichDataType dataType) {
            RedisZSetValue.RedisZSetRow row = this.treeItem.data();
            if (row != null) {
                this.nodeData.showData(dataType, row.getValue());
            }
        }

        @FXML
        @Override
        protected void deleteRow() {
            if (this.treeItem.isSelectRow() && MessageBox.confirm(I18nHelper.deleteCoordinate() + "?")) {
                RedisKeyRow row = this.treeItem.currentRow();
                if (this.treeItem.deleteRow()) {
                    // 移除
                    if (this.listTable.getItemSize() > 1) {
                        this.listTable.removeItem(row);
                    } else {// 刷新
                        this.firstPage();
                    }
                }
            }
        }

        @Override
        protected void clearRow() {
            this.nodeData.clear();
            this.nodeData.disable();
        }

        @Override
        protected void bindListeners() {
            super.bindListeners();
            // 绑定属性
            this.latitudeVal.disableProperty().bind(this.nodeData.disabledProperty());
            this.latitudeVal.editableProperty().bind(this.nodeData.editableProperty());
            this.longitudeVal.disableProperty().bind(this.nodeData.disabledProperty());
            this.longitudeVal.editableProperty().bind(this.nodeData.editableProperty());
            // 格式监听
            this.format.selectedItemChanged(this.formatListener);
            // 坐标处理
            this.latitudeVal.addTextChangeListener(this.latitudeValListener);
            this.longitudeVal.addTextChangeListener(this.longitudeValListener);
            // 键数据处理
            this.nodeData.addTextChangeListener(this.dataListener);
            this.nodeData.undoableProperty().addListener((observableValue, aBoolean, t1) -> this.dataUndo.setDisable(!t1));
            this.nodeData.redoableProperty().addListener((observableValue, aBoolean, t1) -> this.dataRedo.setDisable(!t1));
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
    }
}
