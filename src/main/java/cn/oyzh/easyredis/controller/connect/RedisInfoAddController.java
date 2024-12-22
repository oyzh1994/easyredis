package cn.oyzh.easyredis.controller.connect;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easyredis.RedisConst;
import cn.oyzh.easyredis.domain.RedisConnect;
import cn.oyzh.easyredis.domain.RedisGroup;
import cn.oyzh.easyredis.domain.RedisSSHConfig;
import cn.oyzh.easyredis.event.RedisEventUtil;
import cn.oyzh.easyredis.store.RedisConnectJdbcStore;
import cn.oyzh.easyredis.util.RedisConnectUtil;
import cn.oyzh.fx.gui.text.field.ClearableTextField;
import cn.oyzh.fx.gui.text.field.PortTextField;
import cn.oyzh.fx.plus.controller.StageController;
import cn.oyzh.fx.plus.controls.box.FlexHBox;
import cn.oyzh.fx.plus.controls.button.FXCheckBox;
import cn.oyzh.fx.plus.controls.tab.FlexTabPane;
import cn.oyzh.fx.plus.controls.text.area.FlexTextArea;
import cn.oyzh.fx.gui.text.field.NumberTextField;
import cn.oyzh.fx.plus.controls.toggle.FXToggleSwitch;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.window.StageAttribute;
import cn.oyzh.i18n.I18nHelper;
import javafx.fxml.FXML;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;

/**
 * 添加redis信息业务
 *
 * @author oyzh
 * @since 2023/06/16
 */
@StageAttribute(
        modality = Modality.WINDOW_MODAL,
        iconUrl = RedisConst.ICON_PATH,
        value = RedisConst.FXML_BASE_PATH + "connect/redisConnectAdd.fxml"
)
public class RedisInfoAddController extends StageController {

    /**
     * 只读模式
     */
    @FXML
    private FXCheckBox readonly;

    /**
     * tab组件
     */
    @FXML
    private FlexTabPane tabPane;

    /**
     * 名称
     */
    @FXML
    private ClearableTextField name;

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
     * 备注
     */
    @FXML
    private FlexTextArea remark;

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

    // /**
    //  * 认证配置组件
    //  */
    // @FXML
    // private FlexHBox authInfoBox;
    //
    // /**
    //  * 哨兵配置组件
    //  */
    // @FXML
    // private FlexHBox sentinelBox;

    // /**
    //  * master用户名
    //  */
    // @FXML
    // private ClearableTextField masterUser;
    //
    // /**
    //  * master密码
    //  */
    // @FXML
    // private ClearableTextField masterPassword;
    //
    // /**
    //  * 重定向到master
    //  */
    // @FXML
    // private FXCheckBox redirectMaster;

    /**
     * 连接超时
     */
    @FXML
    private NumberTextField connectTimeOut;

    /**
     * 执行超时
     */
    @FXML
    private NumberTextField executeTimeOut;

    // /**
    //  * 认证方式
    //  */
    // @FXML
    // private FlexComboBox<String> authType;

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
     * ssh连接组件
     */
    @FXML
    private FlexHBox sshHostBox;

    /**
     * ssh认证组件
     */
    @FXML
    private FlexHBox sshAuthBox;

    // /**
    //  * ssh超时组件
    //  */
    // @FXML
    // private FlexHBox sshTimeoutBox;

    /**
     * 分组
     */
    private RedisGroup group;

    /**
     * redis连接储存对象
     */
    private final RedisConnectJdbcStore infoStore = RedisConnectJdbcStore.INSTANCE;

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
    private RedisSSHConfig getSSHInfo() {
        RedisSSHConfig sshConnectInfo = new RedisSSHConfig();
        sshConnectInfo.setHost(this.sshHost.getText());
        sshConnectInfo.setUser(this.sshUser.getText());
        sshConnectInfo.setPassword(this.sshPassword.getText());
        sshConnectInfo.setPort(this.sshPort.getIntValue());
        sshConnectInfo.setTimeout(this.sshTimeout.getIntValue());
        return sshConnectInfo;
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
            RedisConnect redisInfo = new RedisConnect();
            redisInfo.setHost(host);
            redisInfo.setExecuteTimeOut(3);
            redisInfo.setConnectTimeOut(3);
            redisInfo.setUser(this.user.getText());
            redisInfo.setPassword(this.password.getText());
            redisInfo.setSshForward(this.sshForward.isSelected());
            if (redisInfo.isSSHForward()) {
                redisInfo.setSshConnect(this.getSSHInfo());
            }
            RedisConnectUtil.testConnect(this.stage, redisInfo);
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
            String name = this.name.getTextTrim();
            RedisConnect redisInfo = new RedisConnect();
            redisInfo.setName(name);
            Number connectTimeOut = this.connectTimeOut.getValue();
            Number executeTimeOut = this.executeTimeOut.getValue();

            redisInfo.setHost(host);
            redisInfo.setUser(this.user.getText());
            redisInfo.setSshConnect(this.getSSHInfo());
            redisInfo.setRemark(this.remark.getTextTrim());
            redisInfo.setPassword(this.password.getText());
            redisInfo.setReadonly(this.readonly.isSelected());
            redisInfo.setSshForward(this.sshForward.isSelected());
            redisInfo.setGroupId(this.group == null ? null : this.group.getGid());
            redisInfo.setConnectTimeOut(connectTimeOut == null ? 5 : connectTimeOut.intValue());
            redisInfo.setExecuteTimeOut(executeTimeOut == null ? 5 : executeTimeOut.intValue());
            // // 哨兵配置
            // if (this.redirectMaster.isSelected()) {
            //     redisInfo.setMasterUser(this.masterUser.getText());
            //     redisInfo.setMasterPassword(this.masterPassword.getText());
            //     redisInfo.setRedirectMaster(this.redirectMaster.isSelected());
            // } else {
            //     redisInfo.setMasterUser(null);
            //     redisInfo.setMasterPassword(null);
            //     redisInfo.setRedirectMaster(false);
            // }
            // // 无需认证
            // if (this.authType.getSelectedIndex() == 0) {
            //     redisInfo.setUser(null);
            //     redisInfo.setPassword(null);
            //     redisInfo.setMasterUser(null);
            //     redisInfo.setMasterPassword(null);
            //     redisInfo.setRedirectMaster(false);
            // } else if (this.authType.getSelectedIndex() == 1) {// 密码认证
            //     redisInfo.setUser(null);
            //     redisInfo.setMasterUser(null);
            // }
            // 保存数据
            boolean result = this.infoStore.replace(redisInfo);
            if (result) {
                RedisEventUtil.infoAdded(redisInfo);
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
        // // 重定向到master
        // this.redirectMaster.selectedChanged((observable, oldValue, newValue) -> {
        //     if (newValue) {
        //         if (this.authType.getSelectedIndex() == 2) {
        //             this.masterUser.enable();
        //         }
        //         this.masterPassword.enable();
        //     } else {
        //         if (this.authType.getSelectedIndex() == 2) {
        //             this.masterUser.disable();
        //         }
        //         this.masterPassword.disable();
        //     }
        // });
        // // 开启哨兵配置
        // this.redirectMaster.selectedChanged((observable, oldValue, newValue) -> {
        //     if (!this.authInfoBox.isDisable()) {
        //         if (newValue) {
        //             this.sentinelBox.enable();
        //         } else {
        //             this.sentinelBox.disable();
        //         }
        //     }
        // });
        // // 认证方式配置
        // this.authType.selectedIndexChanged((observable, oldValue, newValue) -> {
        //     if (newValue.intValue() == 0) {
        //         this.authInfoBox.disable();
        //         this.sentinelBox.disable();
        //     } else if (newValue.intValue() == 1) {
        //         this.user.disable();
        //         this.masterUser.disable();
        //         this.authInfoBox.enable();
        //         if (this.redirectMaster.isSelected()) {
        //             this.sentinelBox.enable();
        //         }
        //     } else {
        //         this.user.enable();
        //         this.authInfoBox.enable();
        //         if (this.redirectMaster.isSelected()) {
        //             this.masterUser.enable();
        //         }
        //     }
        // });
        // ssh配置
        this.sshForward.selectedChanged((observable, oldValue, newValue) -> {
            if (newValue) {
                this.sshAuthBox.enable();
                this.sshHostBox.enable();
                this.sshTimeout.enable();
            } else {
                this.sshAuthBox.disable();
                this.sshHostBox.disable();
                this.sshTimeout.disable();
            }
        });
    }

    @Override
    public void onStageShown(WindowEvent event) {
        super.onStageShown(event);
        this.group = this.getWindowProp("group");
        this.stage.switchOnTab();
        this.stage.hideOnEscape();
    }

    @Override
    public String getViewTitle() {
        return I18nResourceBundle.i18nString("base.title.info.add");
    }
}
