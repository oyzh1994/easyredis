package cn.oyzh.easyredis.controller.filter;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyredis.RedisConst;
import cn.oyzh.easyredis.domain.RedisFilter;
import cn.oyzh.easyredis.event.RedisEventUtil;
import cn.oyzh.easyredis.store.RedisFilterStore;
import cn.oyzh.fx.plus.controller.Controller;
import cn.oyzh.fx.plus.controls.textfield.ClearableTextField;
import cn.oyzh.fx.plus.controls.toggle.FXToggleSwitch;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.stage.StageAttribute;
import javafx.fxml.FXML;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;


/**
 * 过滤配置新增业务
 *
 * @author oyzh
 * @since 2023/06/30
 */
@StageAttribute(
        iconUrls = RedisConst.ICON_PATH,
        modality = Modality.WINDOW_MODAL,
        value = RedisConst.FXML_BASE_PATH + "filter/redisFilterAdd.fxml"
)
public class RedisFilterAddController extends Controller {

    /**
     * 关键字
     */
    @FXML
    private ClearableTextField kw;

    /**
     * 是否启用
     */
    @FXML
    private FXToggleSwitch enable;

    /**
     * 匹配方式
     */
    @FXML
    private FXToggleSwitch matchMode;

    /**
     * redis过滤配置储存
     */
    private final RedisFilterStore filterStore = RedisFilterStore.INSTANCE;

    /**
     * 添加过滤配置
     */
    @FXML
    private void addFilter() {
        // 获取输入内容
        String kw = this.kw.getText().trim();
        if (StrUtil.isBlank(kw)) {
            MessageBox.tipMsg(I18nResourceBundle.i18nString("base.contentNotEmpty"), this.kw);
            return;
        }
        if (this.filterStore.exist(kw)) {
            MessageBox.tipMsg(I18nResourceBundle.i18nString("base.contentAlreadyExists"), this.kw);
            return;
        }
        try {
            RedisFilter filter = new RedisFilter();
            filter.setKw(kw);
            filter.setEnable(this.enable.isSelected());
            filter.setPartMatch(this.matchMode.isSelected());
            if (this.filterStore.add(filter)) {
                RedisEventUtil.filterAdded();
                RedisEventUtil.treeChildFilter();
                MessageBox.okToast(I18nResourceBundle.i18nString("base.actionSuccess"));
                this.closeStage();
            } else {
                MessageBox.warn(I18nResourceBundle.i18nString("base.actionFail"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    @Override
    public void onStageShown(WindowEvent event) {
        this.stage.switchOnTab();
        this.stage.hideOnEscape();
    }

    @Override
    public String i18nId() {
        return "filter.add";
    }
}
