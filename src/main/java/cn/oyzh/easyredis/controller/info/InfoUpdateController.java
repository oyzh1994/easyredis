package cn.oyzh.easyredis.controller.info;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyredis.RedisConst;
import cn.oyzh.easyredis.RedisStyle;
import cn.oyzh.easyredis.domain.RedisInfo;
import cn.oyzh.easyredis.event.RedisEventTypes;
import cn.oyzh.easyredis.store.RedisInfoStore;
import cn.oyzh.easyredis.util.RedisConnectUtil;
import cn.oyzh.fx.plus.controller.Controller;
import cn.oyzh.fx.plus.controls.FlexHBox;
import cn.oyzh.fx.plus.controls.area.FlexTextArea;
import cn.oyzh.fx.plus.controls.button.FlexCheckBox;
import cn.oyzh.fx.plus.controls.tab.FlexTabPane;
import cn.oyzh.fx.plus.controls.textfield.ClearableTextField;
import cn.oyzh.fx.plus.controls.textfield.NumberTextField;
import cn.oyzh.fx.plus.controls.textfield.PortTextField;
import cn.oyzh.fx.plus.event.EventUtil;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.stage.StageAttribute;
import javafx.fxml.FXML;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * redis信息修改业务
 *
 * @author oyzh
 * @since 2022/06/16
 */
@Slf4j
@StageAttribute(
        title = "Redis连接修改",
        modality = Modality.WINDOW_MODAL,
        iconUrls = RedisConst.ICON_PATH,
        cssUrls = RedisStyle.COMMON,
        value = RedisConst.FXML_BASE_PATH + "info/redisInfoUpdate.fxml"
)
public class InfoUpdateController extends Controller {

    /**
     * tab组件
     */
    @FXML
    private FlexTabPane tabPane;

    /**
     * redis信息
     */
    private RedisInfo redisInfo;

    // /**
    //  * 字符集
    //  */
    // @FXML
    // private CharsetComboBox charset;

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
     * 连接ip
     */
    @FXML
    private ClearableTextField hostIp;

    /**
     * 显示哨兵配置
     */
    @FXML
    private FlexCheckBox showSentinel;

    /**
     * 哨兵配置组件
     */
    @FXML
    private FlexHBox sentinelBox;

    /**
     * master用户名
     */
    @FXML
    private ClearableTextField masterUser;

    /**
     * master密码
     */
    @FXML
    private ClearableTextField masterPassword;

    /**
     * 重定向到master
     */
    @FXML
    private FlexCheckBox redirectMaster;

    /**
     * 连接端口
     */
    @FXML
    private PortTextField hostPort;

    /**
     * 备注
     */
    @FXML
    private FlexTextArea remark;

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

    /**
     * redis连接储存对象
     */
    private final RedisInfoStore infoStore = RedisInfoStore.INSTANCE;

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
        if (StrUtil.isNotBlank(host)) {
            RedisConnectUtil.testConnect(this.stage, host, this.password.getText(), 3);
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
        if (StrUtil.isBlank(this.name.getTextTrim())) {
            this.name.setText(host.replace(":", "_"));
        }
        String name = this.name.getTextTrim();
        this.redisInfo.setName(name);
        // 检查名称
        if (this.infoStore.exist(this.redisInfo)) {
            this.tabPane.select(0);
            MessageBox.warn("此名称已存在！");
            return;
        }
        // String charset = this.charset.getValue();
        Number connectTimeOut = this.connectTimeOut.getValue();
        Number executeTimeOut = this.executeTimeOut.getValue();

        this.redisInfo.setHost(host.trim());
        this.redisInfo.setUser(this.user.getText());
        this.redisInfo.setRemark(this.remark.getTextTrim());
        this.redisInfo.setPassword(this.password.getText());
        // this.redisInfo.setCharset("跟随系统".equals(charset) ? null : charset.toLowerCase());
        this.redisInfo.setConnectTimeOut(connectTimeOut == null ? 5 : connectTimeOut.intValue());
        this.redisInfo.setExecuteTimeOut(executeTimeOut == null ? 5 : executeTimeOut.intValue());
        if (this.showSentinel.isSelected()) {
            this.redisInfo.setMasterUser(this.masterUser.getText());
            this.redisInfo.setMasterPassword(this.masterPassword.getText());
            this.redisInfo.setRedirectMaster(this.redirectMaster.isSelected());
        } else {
            this.redisInfo.setMasterUser(null);
            this.redisInfo.setMasterPassword(null);
            this.redisInfo.setRedirectMaster(false);
        }
        // 保存数据
        if (this.infoStore.update(this.redisInfo)) {
            EventUtil.fire(RedisEventTypes.REDIS_INFO_UPDATED, this.redisInfo);
            MessageBox.okToast("修改Redis信息成功!");
            this.closeStage();
        } else {
            MessageBox.warn("修改失败！");
        }
    }

    @Override
    public void onStageShown(@NonNull WindowEvent event) {
        super.onStageShown(event);
        this.redisInfo = this.getStageProp("redisInfo");
        this.name.setText(this.redisInfo.getName());
        this.user.setText(this.redisInfo.getUser());
        this.remark.setText(this.redisInfo.getRemark());
        this.hostIp.setText(this.redisInfo.hostIp());
        // this.charset.select(this.redisInfo.getCharset());
        this.hostPort.setValue(this.redisInfo.hostPort());
        this.password.setText(this.redisInfo.getPassword());
        this.masterUser.setText(this.redisInfo.getMasterUser());
        this.masterPassword.setText(this.redisInfo.getMasterPassword());
        this.connectTimeOut.setValue(this.redisInfo.getConnectTimeOut());
        this.executeTimeOut.setValue(this.redisInfo.getExecuteTimeOut());
        this.redirectMaster.setSelected(this.redisInfo.isRedirectMaster());
        this.sentinelBox.managedBindVisible();
        if (this.redisInfo.isRedirectMaster()) {
            this.sentinelBox.display();
            this.showSentinel.setSelected(true);
        }
        this.stage.switchOnTab();
        this.stage.hideOnEscape();
    }

    @Override
    protected void bindListeners() {
        this.redirectMaster.selectedChanged((observable, oldValue, newValue) -> {
            if (newValue) {
                this.masterUser.enable();
                this.masterPassword.enable();
            } else {
                this.masterUser.disable();
                this.masterPassword.disable();
            }
        });
        this.showSentinel.selectedChanged((observable, oldValue, newValue) -> {
            if (newValue) {
                this.sentinelBox.display();
            } else {
                this.sentinelBox.disappear();
            }
        });
    }

    @Override
    public void onStageHidden(WindowEvent event) {
        super.onStageHidden(event);
    }
}
