package cn.oyzh.easyredis.controller.key;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyredis.RedisConst;
import cn.oyzh.easyredis.fx.RedisKeyFilterHistoryPopup;
import cn.oyzh.easyredis.store.RedisKeyFilterHistoryStore;
import cn.oyzh.easyredis.trees.db.RedisDBTreeItem;
import cn.oyzh.fx.plus.controller.Controller;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.controls.textfield.ClearableTextField;
import cn.oyzh.fx.plus.event.EventUtil;
import cn.oyzh.fx.plus.search.SearchTextField;
import cn.oyzh.fx.plus.stage.StageAttribute;
import com.google.common.eventbus.Subscribe;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;

import java.util.List;


/**
 * redis键过滤业务
 *
 * @author oyzh
 * @since 2023/07/19
 */
@StageAttribute(
        title = "Redis键过滤",
        iconUrls = RedisConst.ICON_PATH,
        modality = Modality.WINDOW_MODAL,
        // cssUrls = RedisStyle.COMMON,
        resizeable = false,
        value = RedisConst.FXML_BASE_PATH + "key/redisKeyFilter.fxml"
)
public class RedisKeyFilterController extends Controller {

    /**
     * 过滤模式
     */
    @FXML
    private SearchTextField keyFilter;

    /**
     * 树键
     */
    private RedisDBTreeItem treeItem;

    /**
     * 过滤历史储存
     */
    private final RedisKeyFilterHistoryStore historyStore = RedisKeyFilterHistoryStore.INSTANCE;

    @Override
    public void onStageShown(WindowEvent event) {
        EventUtil.register(this);
        this.stage.hideOnEscape();
        super.onStageShown(event);
        this.treeItem = this.getStageProp("treeItem");
        String pattern = this.getStageProp("pattern");
        if (!StrUtil.isBlank(pattern)) {
            this.keyFilter.setText(pattern);
        }
        this.keyFilter.requestFocus();
        this.keyFilter.setHistoryPopup(new RedisKeyFilterHistoryPopup());
    }

    /**
     * 键过滤
     */
    @FXML
    private void keyFilter() {
        String pattern = this.keyFilter.getText();
        if (StrUtil.isNotBlank(pattern) && !"*".equals(pattern)) {
            this.historyStore.addHistory(pattern);
        }
        this.treeItem.doKeyFilter(pattern);
        this.closeStage();
    }
}
