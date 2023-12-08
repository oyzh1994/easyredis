package cn.oyzh.easyredis.controller.info;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyredis.RedisConst;
import cn.oyzh.easyredis.RedisStyle;
import cn.oyzh.easyredis.domain.RedisGroup;
import cn.oyzh.easyredis.domain.RedisInfo;
import cn.oyzh.easyredis.event.RedisEventUtil;
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

/**
 * 添加redis信息业务
 *
 * @author oyzh
 * @since 2023/06/16
 */
//@Slf4j
@StageAttribute(
        title = "Redis连接新增",
        modality = Modality.WINDOW_MODAL,
        iconUrls = RedisConst.ICON_PATH,
        cssUrls = RedisStyle.COMMON,
        value = RedisConst.FXML_BASE_PATH + "info/redisInfoAdd.fxml"
)
public class RedisInfoAddController extends Controller {

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
     * 分组
     */
    private RedisGroup group;

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
     * 添加redis信息
     */
    @FXML
    private void add() {
        String host = this.getHost();
        if (host == null) {
            return;
        }
        // 名称未填，则直接以host为名称
        if (StrUtil.isBlank(this.name.getTextTrim())) {
            this.name.setText(host.replace(":", "_"));
        }
        try {
            String name = this.name.getTextTrim();
            RedisInfo redisInfo = new RedisInfo();
            redisInfo.setName(name);
            // 检查名称是否存在
            if (this.infoStore.exist(redisInfo)) {
                MessageBox.warn("此名称已存在！");
                return;
            }

            Number connectTimeOut = this.connectTimeOut.getValue();
            Number executeTimeOut = this.executeTimeOut.getValue();

            redisInfo.setHost(host);
            redisInfo.setUser(this.user.getText());
            redisInfo.setRemark(this.remark.getTextTrim());
            redisInfo.setPassword(this.password.getText());
            redisInfo.setGroupId(this.group == null ? null : this.group.getGid());
            redisInfo.setConnectTimeOut(connectTimeOut == null ? 5 : connectTimeOut.intValue());
            redisInfo.setExecuteTimeOut(executeTimeOut == null ? 5 : executeTimeOut.intValue());
            if (this.showSentinel.isSelected()) {
                redisInfo.setMasterUser(this.masterUser.getText());
                redisInfo.setMasterPassword(this.masterPassword.getText());
                redisInfo.setRedirectMaster(this.redirectMaster.isSelected());
            } else {
                redisInfo.setMasterUser(null);
                redisInfo.setMasterPassword(null);
                redisInfo.setRedirectMaster(false);
            }
            // 保存数据
            boolean result = this.infoStore.add(redisInfo);
            if (result) {
                RedisEventUtil.infoAdded(redisInfo);
                MessageBox.okToast("新增redis信息成功!");
                this.closeStage();
            } else {
                MessageBox.warn("新增redis信息失败！");
            }
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    @Override
    public void onStageShown(WindowEvent event) {
        super.onStageShown(event);
        this.stage.switchOnTab();
        this.group = this.getStageProp("group");
        this.sentinelBox.managedBindVisible();
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
