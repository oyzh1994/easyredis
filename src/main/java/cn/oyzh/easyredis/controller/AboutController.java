package cn.oyzh.easyredis.controller;


import cn.oyzh.common.dto.Project;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easyredis.RedisConst;
import cn.oyzh.fx.plus.FXConst;
import cn.oyzh.fx.plus.controller.SubStageController;
import cn.oyzh.fx.plus.controls.text.FlexText;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import cn.oyzh.fx.plus.window.StageAttribute;
import cn.oyzh.i18n.I18nHelper;
import javafx.fxml.FXML;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;


/**
 * 关于业务
 *
 * @author oyzh
 * @since 2023/06/22
 */
@StageAttribute(
        resizable = false,
        iconUrl = RedisConst.ICON_PATH,
        modality = Modality.APPLICATION_MODAL,
        value = FXConst.FXML_PATH + "about.fxml"
)
public class AboutController extends SubStageController {

    @FXML
    private FlexText name;

    @FXML
    private FlexText type;

    @FXML
    private FlexText version;

    @FXML
    private FlexText updateDate;

    @FXML
    private FlexText copyright;

    /**
     * 项目信息
     */
    private final Project project = Project.load();

    @Override
    public void onStageShown(WindowEvent event) {
        this.name.setText(this.project.getName());
        this.version.setText("v" + this.project.getVersion());
        this.updateDate.setText(this.project.getUpdateDate());
        this.copyright.setText(this.project.getCopyright());
        this.type.setText(StringUtil.equals(this.project.getType(), "build") ? I18nHelper.buildType1() : I18nHelper.buildType2());
        // 设置标题
        this.stage.appendTitle(" " + this.project.getName());
        this.stage.hideOnEscape();
    }

    @Override
    public String getViewTitle() {
        return I18nResourceBundle.i18nString("base.title.about");
    }
}
