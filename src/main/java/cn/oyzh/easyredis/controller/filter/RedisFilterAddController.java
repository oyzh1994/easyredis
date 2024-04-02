package cn.oyzh.easyredis.controller.filter;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyredis.RedisConst;
import cn.oyzh.easyredis.RedisStyle;
import cn.oyzh.easyredis.domain.RedisFilter;
import cn.oyzh.easyredis.event.RedisEventUtil;
import cn.oyzh.easyredis.store.RedisFilterStore;
import cn.oyzh.fx.plus.controller.Controller;
import cn.oyzh.fx.plus.controls.FXToggleSwitch;
import cn.oyzh.fx.plus.controls.textfield.ClearableTextField;
import cn.oyzh.fx.plus.event.EventUtil;
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
//@Slf4j
@StageAttribute(
        title = "过滤配置新增",
        iconUrls = RedisConst.ICON_PATH,
        modality = Modality.WINDOW_MODAL,
        // cssUrls = RedisStyle.COMMON,
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
     * 模糊匹配
     */
    @FXML
    private FXToggleSwitch partMatch;

    /**
     * redis过滤配置储存
     */
    private final RedisFilterStore filterStore = RedisFilterStore.INSTANCE;

    /**
     * 添加过滤配置
     */
    @FXML
    private void addFilter() {
        // 获取键值
        String kw = this.kw.getText().trim();
        if (StrUtil.isBlank(kw)) {
            MessageBox.tipMsg("请输入过滤关键字！", this.kw);
            return;
        }
        if (this.filterStore.exist(kw)) {
            MessageBox.tipMsg("此关键字已存在！", this.kw);
            return;
        }
        try {
            RedisFilter filter = new RedisFilter();
            filter.setKw(kw);
            filter.setEnable(this.enable.isSelected());
            filter.setPartMatch(this.partMatch.isSelected());
            if (this.filterStore.add(filter)) {
                // EventUtil.fire(RedisEventTypes.REDIS_FILTER_ADDED);
                // EventUtil.fire(RedisEventTypes.REDIS_KEY_FILTER);
                RedisEventUtil.filterAdded();
                RedisEventUtil.keyFilter();
                MessageBox.okToast("新增Redis过滤配置成功!");
                this.closeStage();
            } else {
                MessageBox.warn("新增Redis过滤配置失败！");
            }
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    @Override
    public void onStageShown(WindowEvent event) {
        this.stage.switchOnTab();
        this.stage.hideOnEscape();
    }

    @Override
    public void onStageHidden(WindowEvent event) {
        super.onStageHidden(event);
    }
}
