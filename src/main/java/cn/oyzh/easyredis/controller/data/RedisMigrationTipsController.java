package cn.oyzh.easyredis.controller.data;

import cn.oyzh.easyredis.RedisConst;
import cn.oyzh.easyredis.store.RedisStoreUtil;
import cn.oyzh.easyredis.util.RedisI18nHelper;
import cn.oyzh.fx.plus.FXConst;
import cn.oyzh.fx.plus.controller.StageController;
import cn.oyzh.fx.plus.controls.button.FXCheckBox;
import cn.oyzh.fx.plus.window.FXStageStyle;
import cn.oyzh.fx.plus.window.StageAttribute;
import cn.oyzh.fx.plus.window.StageManager;
import javafx.fxml.FXML;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;


/**
 * redis迁移业务
 *
 * @author oyzh
 * @since 2024/11/25
 */
@StageAttribute(
        stageStyle = FXStageStyle.UNIFIED,
        modality = Modality.APPLICATION_MODAL,
        value = FXConst.FXML_PATH + "data/redisMigrationTips.fxml"
)
public class RedisMigrationTipsController extends StageController {

    @FXML
    private FXCheckBox ignoreMigration;

    @Override
    public void onWindowShown(WindowEvent event) {
        super.onWindowShown(event);
        this.stage.hideOnEscape();
    }

    @Override
    public String getViewTitle() {
        return RedisI18nHelper.migrationTip6();
    }

    @Override
    public void onWindowHidden(WindowEvent event) {
        super.onWindowHidden(event);
        if(this.ignoreMigration.isSelected()){
            RedisStoreUtil.ignoreMigration();
        }
    }

    @FXML
    private void close( ) {
        super.closeWindow();
    }

    @FXML
    private void migration( ) {
        this.close();
        StageManager.showStage(RedisDataMigrationController.class);
    }
}
