package cn.oyzh.easyredis.controller.connect;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easyredis.domain.RedisConnect;
import cn.oyzh.easyredis.domain.RedisFilter;
import cn.oyzh.easyredis.domain.RedisSSHConfig;
import cn.oyzh.easyredis.dto.RedisFilterVO;
import cn.oyzh.easyredis.event.RedisEventUtil;
import cn.oyzh.easyredis.fx.RedisFilterTableView;
import cn.oyzh.easyredis.store.RedisConnectStore;
import cn.oyzh.easyredis.store.RedisFilterStore;
import cn.oyzh.easyredis.store.RedisSSHConfigStore;
import cn.oyzh.easyredis.util.RedisConnectUtil;
import cn.oyzh.fx.gui.text.field.ClearableTextField;
import cn.oyzh.fx.gui.text.field.NumberTextField;
import cn.oyzh.fx.gui.text.field.PortTextField;
import cn.oyzh.fx.plus.FXConst;
import cn.oyzh.fx.plus.controller.StageController;
import cn.oyzh.fx.plus.controls.button.FXCheckBox;
import cn.oyzh.fx.plus.controls.tab.FXTab;
import cn.oyzh.fx.plus.controls.tab.FXTabPane;
import cn.oyzh.fx.plus.controls.text.area.FXTextArea;
import cn.oyzh.fx.plus.controls.toggle.FXToggleSwitch;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.node.NodeGroupUtil;
import cn.oyzh.fx.plus.window.FXStageStyle;
import cn.oyzh.fx.plus.window.StageAttribute;
import cn.oyzh.i18n.I18nHelper;
import javafx.fxml.FXML;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;

import java.util.List;

/**
 * redis信息修改业务
 *
 * @author oyzh
 * @since 2023/06/16
 */
@StageAttribute(
        stageStyle = FXStageStyle.UNIFIED,
        modality = Modality.APPLICATION_MODAL,
        value = FXConst.FXML_PATH + "connect/redisUpdateConnect.fxml"
)
public class RedisUpdateConnectController extends StageController {

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
     * redis信息
     */
    private RedisConnect redisConnect;

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
     * ssh面板
     */
    @FXML
    private FXTab sshTab;

    /**
     * 开启ssh
     */
    @FXML
    private FXToggleSwitch sshForward;

    /**
     * ssh主机地址
     */
    @FXML
    private ClearableTextField sshHost;

    /**
     * ssh主机端口
     */
    @FXML
    private PortTextField sshPort;

    /**
     * ssh主机端口
     */
    @FXML
    private NumberTextField sshTimeout;

    /**
     * ssh主机用户
     */
    @FXML
    private ClearableTextField sshUser;

    /**
     * ssh主机密码
     */
    @FXML
    private ClearableTextField sshPassword;

    /**
     * redis连接储存对象
     */
    private final RedisConnectStore connectStore = RedisConnectStore.INSTANCE;

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
     * redis过滤配置储存
     */
    private final RedisFilterStore filterStore = RedisFilterStore.INSTANCE;

    /**
     * ssh配置储存
     */
    private final RedisSSHConfigStore sshConfigStore = RedisSSHConfigStore.INSTANCE;

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
     * 获取ssh信息
     *
     * @return ssh连接信息
     */
    private RedisSSHConfig getSSHConfig() {
        RedisSSHConfig sshConfig = new RedisSSHConfig();
        sshConfig.setHost(this.sshHost.getText());
        sshConfig.setUser(this.sshUser.getText());
        sshConfig.setPort(this.sshPort.getIntValue());
        sshConfig.setPassword(this.sshPassword.getText());
        sshConfig.setTimeout(this.sshTimeout.getIntValue());
        return sshConfig;
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
            redisConnect.setId(this.redisConnect.getId());
            redisConnect.setUser(this.user.getText());
            redisConnect.setPassword(this.password.getText());
            redisConnect.setSshForward(this.sshForward.isSelected());
            if (redisConnect.isSSHForward()) {
                redisConnect.setSshConfig(this.getSSHConfig());
            }
            RedisConnectUtil.testConnect(this.stage, redisConnect);
        }
    }

    /**
     * 修改redis信息
     */
    @FXML
    private void update() {
        String host = this.getHost();
        if (host == null) {
            return;
        }
        // 名称未填，则直接以host为名称
        if (StringUtil.isBlank(this.name.getTextTrim())) {
            this.name.setText(host.replace(":", "_"));
        }
        try {
            String name = this.name.getTextTrim();
            this.redisConnect.setName(name);
            Number connectTimeOut = this.connectTimeOut.getValue();
            Number executeTimeOut = this.executeTimeOut.getValue();

            this.redisConnect.setHost(host.trim());
            this.redisConnect.setUser(this.user.getText());
            // ssh配置
            this.redisConnect.setSshConfig(this.getSSHConfig());
            this.redisConnect.setSshForward(this.sshForward.isSelected());
            this.redisConnect.setRemark(this.remark.getTextTrim());
            this.redisConnect.setPassword(this.password.getText());
            this.redisConnect.setReadonly(this.readonly.isSelected());
            this.redisConnect.setConnectTimeOut(connectTimeOut == null ? 5 : connectTimeOut.intValue());
            this.redisConnect.setExecuteTimeOut(executeTimeOut == null ? 5 : executeTimeOut.intValue());
            // 过滤列表
            this.redisConnect.setFilters(this.filterTable.getFilters());
            // 保存数据
            if (this.connectStore.replace(this.redisConnect)) {
                RedisEventUtil.connectUpdated(this.redisConnect);
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
        // 连接ip处理
        this.hostIp.addTextChangeListener((observableValue, s, t1) -> {
            // 内容包含“:”，则直接切割字符为ip端口
            if (t1 != null && t1.contains(":")) {
                try {
                    this.hostIp.setText(t1.split(":")[0]);
                    this.hostPort.setValue(Integer.parseInt(t1.split(":")[1]));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        // ssh配置
        this.sshForward.selectedChanged((observable, oldValue, newValue) -> {
            if (newValue) {
                NodeGroupUtil.enable(this.sshTab, "ssh");
            } else {
                NodeGroupUtil.disable(this.sshTab, "ssh");
            }
        });
        // 过滤监听
        this.filterSearchKW.addTextChangeListener((observableValue, s, t1) -> this.initFilterDataList());
    }

    @Override
    public void onWindowShown( WindowEvent event) {
        super.onWindowShown(event);
        this.redisConnect = this.getWindowProp("redisConnect");
        this.name.setText(this.redisConnect.getName());
        this.user.setText(this.redisConnect.getUser());
        this.hostIp.setText(this.redisConnect.hostIp());
        this.remark.setText(this.redisConnect.getRemark());
        this.hostPort.setValue(this.redisConnect.hostPort());
        this.password.setText(this.redisConnect.getPassword());
        this.readonly.setSelected(this.redisConnect.isReadonly());
        this.connectTimeOut.setValue(this.redisConnect.getConnectTimeOut());
        this.executeTimeOut.setValue(this.redisConnect.getExecuteTimeOut());
        // ssh配置
        this.sshForward.setSelected(this.redisConnect.isSSHForward());
        RedisSSHConfig sshConfig = this.sshConfigStore.getByIid(this.redisConnect.getId());
        if (sshConfig != null) {
            this.sshHost.setText(sshConfig.getHost());
            this.sshUser.setText(sshConfig.getUser());
            this.sshPort.setValue(sshConfig.getPort());
            this.sshTimeout.setValue(sshConfig.getTimeout());
            this.sshPassword.setText(sshConfig.getPassword());
        }
        // 初始化数据
        this.initFilterDataList();
        this.stage.switchOnTab();
        this.stage.hideOnEscape();
    }

    @Override
    public String getViewTitle() {
        return I18nHelper.connectUpdateTitle();
    }

    /**
     * 初始化过滤
     */
    private void initFilterDataList() {
        if (!this.filterTable.hasData()) {
            List<RedisFilter> list = this.filterStore.loadByIid(this.redisConnect.getId());
            this.filterTable.setFilters(list);
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
}

