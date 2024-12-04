package cn.oyzh.easyredis.tabs.keys;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyredis.controller.row.RedisStreamMessageAddController;
import cn.oyzh.easyredis.event.RedisStreamMessageAddedEvent;
import cn.oyzh.easyredis.redis.key.RedisStreamKey;
import cn.oyzh.easyredis.redis.row.RedisStreamRow;
import cn.oyzh.easyredis.trees.keys.RedisStreamKeyTreeItem;
import cn.oyzh.event.EventSubscribe;
import cn.oyzh.fx.plus.controls.textfield.ReadOnlyTextField;
import cn.oyzh.fx.plus.util.ClipboardUtil;
import cn.oyzh.fx.plus.window.StageAdapter;
import cn.oyzh.fx.plus.window.StageManager;
import cn.oyzh.fx.rich.richtextfx.data.RichDataTextAreaPane;
import cn.oyzh.fx.rich.richtextfx.data.RichDataType;
import cn.oyzh.i18n.I18nHelper;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * redis stream键tab
 *
 * @author oyzh
 * @since 2023/11/21
 */
public class RedisStreamKeyTab extends RedisKeyTab<RedisStreamKeyTreeItem> {

    public RedisStreamKeyTab(RedisStreamKeyTreeItem treeItem) {
        super(treeItem);
    }

    @Override
    protected String url() {
        return "/tabs/keys/redisStreamKeyTabContent.fxml";
    }

    @Override
    public RedisStreamKeyTabController controller() {
        return (RedisStreamKeyTabController) super.controller();
    }

    @Override
    public RedisStreamKey key() {
        return (RedisStreamKey) super.key();
    }

    /**
     * stream键tab内容组件
     *
     * @author oyzh
     * @since 2023/07/07
     */
    public static class RedisStreamKeyTabController extends RedisRowKeyTabController<RedisStreamKeyTreeItem, RedisStreamRow> {

        /**
         * 消息id
         */
        @FXML
        private ReadOnlyTextField streamID;

        /**
         * 编号列
         */
        @FXML
        private TableColumn<RedisStreamRow, Integer> index;

        /**
         * 分数列
         */
        @FXML
        private TableColumn<RedisStreamRow, String> id;

        /**
         * 值列
         */
        @FXML
        private TableColumn<RedisStreamRow, String> value;

        /**
         * 数据
         */
        @FXML
        private RichDataTextAreaPane nodeData;

        @Override
        public boolean init(RedisStreamKeyTreeItem treeItem) {
            this.pageData = null;
            return super.init(treeItem);
        }

        @Override
        protected void initNode() {
            // 初始化表单
            this.initTable();
            // 显示首页
            this.firstPage();
            // 绑定属性
            this.id.setCellValueFactory(new PropertyValueFactory<>("id"));
            this.index.setCellValueFactory(new PropertyValueFactory<>("index"));
            this.value.setCellValueFactory(new PropertyValueFactory<>("value"));
        }

        @Override
        protected List<RedisStreamRow> getRows() {
            List<RedisStreamRow> rows = this.treeItem.nodeValue();
            String filterKW = this.filter.getText();
            if (StrUtil.isNotEmpty(filterKW)) {
                rows = rows.parallelStream()
                        .filter(r -> StrUtil.containsIgnoreCase(r.getValue(), filterKW) ||
                                StrUtil.containsIgnoreCase(String.valueOf(r.getId()), filterKW))
                        .collect(Collectors.toList());
            }
            return rows;
        }

        @FXML
        @Override
        protected void addRow() {
            StageAdapter fxView = StageManager.parseStage(RedisStreamMessageAddController.class);
            fxView.setProp("treeItem", this.treeItem);
            fxView.display();
        }

        @Override
        protected void initRow(RedisStreamRow row) {
            super.initRow(row);
            if (row == null) {
                this.nodeData.clear();
                this.nodeData.disable();
                this.streamID.clear();
                this.streamID.disable();
            } else {
                this.streamID.setText(row.getId());
                this.streamID.enable();
                this.nodeData.enable();
            }
        }

        @FXML
        @Override
        protected void copyRow() {
            String builder = I18nHelper.keyName() + ": " + this.treeItem.key() + System.lineSeparator() +
                    I18nHelper.messageId() + ": " + this.treeItem.currentRow().getId() + System.lineSeparator() +
                    I18nHelper.content() + ": " + this.treeItem.currentRow().getValue();
            ClipboardUtil.setStringAndTip(builder, "消息");
        }

        /**
         * stream消息添加事件
         *
         * @param msg 消息
         */
        @EventSubscribe
        private void onStreamMessageAdded(RedisStreamMessageAddedEvent msg) {
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
}
