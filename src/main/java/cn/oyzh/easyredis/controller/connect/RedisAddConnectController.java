package cn.oyzh.easyredis.controller.connect;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easyredis.domain.RedisConnect;
import cn.oyzh.easyredis.domain.RedisGroup;
import cn.oyzh.easyredis.domain.RedisJumpConfig;
import cn.oyzh.easyredis.dto.RedisFilterVO;
import cn.oyzh.easyredis.event.RedisEventUtil;
import cn.oyzh.easyredis.fx.RedisFilterTableView;
import cn.oyzh.easyredis.fx.RedisJumpTableView;
import cn.oyzh.easyredis.store.RedisConnectStore;
import cn.oyzh.easyredis.util.RedisConnectUtil;
import cn.oyzh.easyredis.util.RedisViewFactory;
import cn.oyzh.fx.gui.text.field.ClearableTextField;
import cn.oyzh.fx.gui.text.field.NumberTextField;
import cn.oyzh.fx.gui.text.field.PortTextField;
import cn.oyzh.fx.plus.FXConst;
import cn.oyzh.fx.plus.controller.StageController;
import cn.oyzh.fx.plus.controls.button.FXCheckBox;
import cn.oyzh.fx.plus.controls.tab.FXTabPane;
import cn.oyzh.fx.plus.controls.text.area.FXTextArea;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.tableview.TableViewUtil;
import cn.oyzh.fx.plus.window.FXStageStyle;
import cn.oyzh.fx.plus.window.StageAdapter;
import cn.oyzh.fx.plus.window.StageAttribute;
import cn.oyzh.i18n.I18nHelper;
import javafx.fxml.FXML;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;

import java.util.ArrayList;

/**
 * 添加redis信息业务
 *
 * @author oyzh
 * @since 2023/06/16
 */
@StageAttribute(
        stageStyle = FXStageStyle.UNIFIED,
        modality = Modality.APPLICATION_MODAL,
        value = FXConst.FXML_PATH + "connect/redisAddConnect.fxml"
)
public class RedisAddConnectController extends StageController {

    /**
     * 只读模式
     */
    @FXML
    private FXCheckBox readonly;

    /**
     * tab组件
     */
    @FXML
    private FXTabPane tabPane;

    /**
     * 名称
     */
    @FXML
    private ClearableTextField name;

    /**
     * 备注
     */
    @FXML
    private FXTextArea remark;

    /**
     * 用户名
     */
    @FXML
    private ClearableTextField user;

    /**
     * 认证密码
     */
    @FXML
    private ClearableTextField password;

    /**
     * 连接ip
     */
    @FXML
    private ClearableTextField hostIp;

    /**
     * 连接端口
     */
    @FXML
    private PortTextField hostPort;

    /**
     * 超时时间
     */
    @FXML
    private NumberTextField connectTimeOut;

    /**
     * 执行超时
     */
    @FXML
    private NumberTextField executeTimeOut;

    /**
     * 分组
     */
    private RedisGroup group;

    /**
     * 过滤列表
     */
    @FXML
    private RedisFilterTableView filterTable;

    /**
     * 过滤搜索
     */
    @FXML
    private ClearableTextField filterSearchKW;

    /**
     * 跳板机配置
     */
    @FXML
    private RedisJumpTableView jumpTableView;

    /**
     * redis连接储存对象
     */
    private final RedisConnectStore connectStore = RedisConnectStore.INSTANCE;

    /**
     * 获取连接地址
     *
     * @return 连接地址
     */
    private String getHost() {
        String hostText;
        String hostIp = this.hostIp.getTextTrim();
        this.tabPane.select(0);
        if (!this.hostPort.validate()) {
            this.tabPane.select(0);
            return null;
        }
        if (!this.hostIp.validate()) {
            this.tabPane.select(0);
            return null;
        }
        hostText = hostIp + ":" + this.hostPort.getValue();
        return hostText;
    }

    /**
     * 测试连接
     */
    @FXML
    private void testConnect() {
        // 检查连接地址
        String host = this.getHost();
        if (StringUtil.isBlank(host) || StringUtil.isBlank(host.split(":")[0])) {
            MessageBox.warn(I18nHelper.contentCanNotEmpty());
        } else {
            RedisConnect redisConnect = new RedisConnect();
            redisConnect.setHost(host);
            redisConnect.setExecuteTimeOut(3);
            redisConnect.setConnectTimeOut(3);
            redisConnect.setUser(this.user.getText());
            redisConnect.setPassword(this.password.getText());
            // 跳板机配置
            redisConnect.setJumpConfigs(this.jumpTableView.getItems());
            RedisConnectUtil.testConnect(this.stage, redisConnect);
        }
    }

    /**
     * 添加redis信息
     */
    @FXML
    private void add() {
        String host = this.getHost();
        if (host == null) {
            return;
        }
        // 名称未填，则直接以host为名称
        if (StringUtil.isBlank(this.name.getTextTrim())) {
            this.name.setText(host.replace(":", "_"));
        }
        try {
            RedisConnect redisConnect = new RedisConnect();
            String name = this.name.getTextTrim();
            redisConnect.setName(name);
            Number connectTimeOut = this.connectTimeOut.getValue();
            Number executeTimeOut = this.executeTimeOut.getValue();

            redisConnect.setHost(host);
            redisConnect.setUser(this.user.getText());
            redisConnect.setRemark(this.remark.getTextTrim());
            redisConnect.setPassword(this.password.getText());
            redisConnect.setReadonly(this.readonly.isSelected());
            redisConnect.setGroupId(this.group == null ? null : this.group.getGid());
            redisConnect.setConnectTimeOut(connectTimeOut == null ? 5 : connectTimeOut.intValue());
            redisConnect.setExecuteTimeOut(executeTimeOut == null ? 5 : executeTimeOut.intValue());
            // 过滤列表
            redisConnect.setFilters(this.filterTable.getFilters());
            // 跳板机配置
            redisConnect.setJumpConfigs(this.jumpTableView.getItems());
            // 保存数据
            if (this.connectStore.replace(redisConnect)) {
                RedisEventUtil.connectAdded(redisConnect);
                MessageBox.okToast(I18nHelper.operationSuccess());
                this.closeWindow();
            } else {
                MessageBox.warn(I18nHelper.operationFail());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    @Override
    protected void bindListeners() {
        // 过滤监听
        this.filterSearchKW.addTextChangeListener((observableValue, s, t1) -> this.initFilterDataList());
    }

    @Override
    public void onWindowShown(WindowEvent event) {
        super.onWindowShown(event);
        this.group = this.getProp("group");
        this.initFilterDataList();
        this.stage.switchOnTab();
        this.stage.hideOnEscape();
    }

    @Override
    public String getViewTitle() {
        return I18nHelper.connectAddTitle();
    }

    /**
     * 初始化过滤
     */
    private void initFilterDataList() {
        if (!this.filterTable.hasData()) {
            this.filterTable.setFilters(new ArrayList<>());
        } else {
            this.filterTable.setKw(this.filterSearchKW.getText());
        }
    }

    /**
     * 添加过滤
     */
    @FXML
    private void addFilter() {
        RedisFilterVO filter = new RedisFilterVO();
        filter.setEnable(true);
        filter.setPartMatch(true);
        this.filterTable.addFilter(filter);
        this.filterTable.selectLast();
    }

    /**
     * 删除过滤
     */
    @FXML
    private void deleteFilter() {
        RedisFilterVO filter = this.filterTable.getSelectedItem();
        if (filter == null) {
            return;
        }
        if (MessageBox.confirm(I18nHelper.deleteData())) {
            this.filterTable.removeItem(filter);
        }
    }

    /**
     * 添加跳板
     */
    @FXML
    private void addJump() {
        StageAdapter adapter = RedisViewFactory.addJump();
        if (adapter == null) {
            return;
        }
        RedisJumpConfig jumpConfig = adapter.getProp("jumpConfig");
        if (jumpConfig != null) {
            this.jumpTableView.addItem(jumpConfig);
            this.jumpTableView.updateOrder();
        }
    }

    /**
     * 编辑跳板
     */
    @FXML
    private void updateJump() {
        RedisJumpConfig config = this.jumpTableView.getSelectedItem();
        if (config == null) {
            return;
        }
        StageAdapter adapter = RedisViewFactory.updateJump(config);
        if (adapter == null) {
            return;
        }
        RedisJumpConfig jumpConfig = adapter.getProp("jumpConfig");
        if (jumpConfig != null) {
            this.jumpTableView.refresh();
            this.jumpTableView.updateOrder();
        }
    }

    /**
     * 删除跳板
     */
    @FXML
    private void deleteJump() {
        this.jumpTableView.removeSelectedItem();
        this.jumpTableView.updateOrder();
    }

    /**
     * 上移跳板
     */
    @FXML
    private void moveJumpUp() {
        TableViewUtil.moveUp(this.jumpTableView);
        this.jumpTableView.refresh();
        this.jumpTableView.updateOrder();
    }

    /**
     * 下移跳板
     */
    @FXML
    private void moveJumpDown() {
        TableViewUtil.moveDown(this.jumpTableView);
        this.jumpTableView.refresh();
        this.jumpTableView.updateOrder();
    }
}
